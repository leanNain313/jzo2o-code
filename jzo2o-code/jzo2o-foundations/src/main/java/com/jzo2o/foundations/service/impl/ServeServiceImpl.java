package com.jzo2o.foundations.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.foundations.dto.response.ServeAggregationResDTO;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.mapper.*;
import com.jzo2o.foundations.model.domain.*;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.*;
import com.jzo2o.foundations.service.IServeService;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务同步表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-07-10
 */
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {

    @Autowired
    private ServeMapper serveMapper;

    @Autowired
    private ServeItemMapper serveItemMapper;

    @Autowired
    private ServeTypeMapper serveTypeMapper;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private ServeSyncMapper serveSyncMapper;

    @Autowired
    private RestHighLevelClient client;
    /**
     * 根据区域id获取服务列表
     * @param request 请求参数
     * @return 返回服务列表
     */
    @Override
    public PageResult<ServeResDTO> servePageByRegion(ServePageQueryReqDTO request) {
        return PageHelperUtils.selectPage(request, () -> serveMapper.queryListByRegionId(request.getRegionId()));
    }

    /**
     * 批量添加服务
     */
    @Transactional
    @Override
    public void addServeByBatch(List<ServeUpsertReqDTO> request) {
        for (ServeUpsertReqDTO serveUpsertReqDTO : request) {
            //1. 服务项目必须是启用状态的才能添加到区域
            ServeItem serveItem = serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());
            if (ObjectUtil.isEmpty(serveItem) || serveItem.getActiveStatus() != 2) {
                throw new ForbiddenOperationException("添加失败,服务项目状态有误");
            }
            //2. 一个服务项目对于一个区域，只能添加一次
            Integer count = lambdaQuery().eq(Serve::getServeItemId, serveUpsertReqDTO.getServeItemId())
                    .eq(Serve::getRegionId, serveUpsertReqDTO.getRegionId())
                    .count();
            if (count > 0) {
                throw new ForbiddenOperationException("添加失败，当前项目已经存在");
            }

            // 属性拷贝
            Serve serve = BeanUtil.copyProperties(serveUpsertReqDTO, Serve.class);
            Region region = regionMapper.selectById(serve.getRegionId());
            if (ObjectUtil.isNotEmpty(region)) {
                serve.setCityCode(region.getCityCode());
            }
            // 插入数据
            save(serve);

        }


    }

    /**
     * 根据服务id修改服务
     * @param id 服务id
     * @param price 服务价格
     */
    @Override
    public void updateServeById(Long id, BigDecimal price) {
        valid(id);
        UpdateWrapper<Serve> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("price", price);
        update(updateWrapper);
    }

    /**
     * 根据服务id移除服务
     * @param id 服务id
     */
    @Override
    public void removeServeById(Long id) {
        // 删除区域服务就是将当前城市与服务项目对应的记录删除，要求当状态为草稿状态方可删除
        Serve serve = getById(id);
        if (ObjectUtil.isEmpty(serve) || serve.getSaleStatus() != 0) {
            throw new ForbiddenOperationException("删除失败");
        }
        removeById(id);
    }

    /**
     * 上架服务根据服务id
     * @param id 服务id
     */
    @Override
    public void onSale(Long id) {
        // 1 区域服务当前非上架状态
        Serve serve = getById(id);
        if (ObjectUtil.isEmpty(serve) || serve.getSaleStatus() == 2) {
            throw new ForbiddenOperationException("上架失败，服务已上架~");
        }
        // 2 服务项目是启用状态
        ServeItem serveItem = serveItemMapper.selectById(serve.getServeItemId());
        if (ObjectUtil.isEmpty(serveItem) || serveItem.getActiveStatus() != 2) {
            throw new ForbiddenOperationException("上架失败，该服务没有启用");
        }
        UpdateWrapper<Serve> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("sale_status", 2);
        update(updateWrapper);

        // 同步es
        addServeSync(id);
    }

    /**
     * 根据服务id下架服务
     * @param id 服务id
     */
    @Override
    public void downSaleById(Long id) {
        // 1.区域服务当前为上架状态
        Serve serve = getById(id);
        if (ObjectUtil.isEmpty(serve) || serve.getSaleStatus() != 2) {
            throw new ForbiddenOperationException("下架失败， 当前服务还没有上架");
        }
        UpdateWrapper<Serve> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("sale_status", 1);
        updateWrapper.eq("id", id);
        update(updateWrapper);

        // 同步es
        addServeSync(id);
    }

    /**
     * 根据取消热门
     * @param id 服务id
     */
    @Override
    public void offHot(Long id) {
        valid(id);
        UpdateWrapper<Serve> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_hot", 0);
        update(updateWrapper);
    }

    @Override
    public void onHot(Long id) {
        valid(id);
        UpdateWrapper<Serve> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_hot", 1);
        update(updateWrapper);
    }

    /**
     * 根据区域id查询区域下的服务项目
     * @param regionId 区域id
     * @return 数据集
     */
    @Caching(
            cacheable = {
                    // 返回数据为为空，也缓存一个空集合，防止缓存击穿
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId",
                            unless = "#result.size() > 0",cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    // 正常返回，对数据进行缓存
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId",
                    unless = "#result.size() <= 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    @Override
    public List<ServeCategoryResDTO> firstPageServeList(Long regionId) {
        //1 对区域进行校验
        Region region = regionMapper.selectById(regionId);
        if (ObjectUtil.isNull(region) || region.getActiveStatus() != 2) {
            return Collections.emptyList();
        }
        List<ServeCategoryResDTO> serveCategoryResDTOS = serveMapper.firstPageServeListById(regionId);
        if (CollUtil.isEmpty(serveCategoryResDTOS)) {
            return Collections.emptyList();
        }

        //3. 截取
        serveCategoryResDTOS = CollUtil.sub(serveCategoryResDTOS, 0, Math.min(serveCategoryResDTOS.size(), 2));//服务类型截取
        serveCategoryResDTOS.forEach(e ->
                //服务项目截取
                e.setServeResDTOList(CollUtil.sub(e.getServeResDTOList(), 0, Math.min(e.getServeResDTOList().size(), 4)))
        );
        return serveCategoryResDTOS;
    }

    @Caching(
            cacheable = {
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE, key = "#regionId",
                            unless = "#result.size() > 0",cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE, key = "#regionId",
                            unless = "#result.size() <= 0",cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    @Override
    public List<ServeAggregationSimpleResDTO> hotServeList(Long regionId) {
        //1 对区域进行校验
        Region region = regionMapper.selectById(regionId);
        if (ObjectUtil.isNull(region) || region.getActiveStatus() != 2) {
            return Collections.emptyList();
        }

        //2 查询指定区域下上架且热门的服务项目信息
        return serveMapper.findServeListByRegionId(regionId);
    }

    @Override
    public ServeAggregationSimpleResDTO findById(Long id) {
        //1. 根据服务id去serve表中查询服务信息(内含服务项目id)
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("服务不存在");
        }

        //2. 根据服务项目id去serve_item表中查询服务项目信息
        ServeItem serveItem = serveItemMapper.selectById(serve.getServeItemId());
        if (ObjectUtil.isNull(serveItem)){
            throw new ForbiddenOperationException("服务项目不存在");
        }

        //3. 将两部分内容组装成返回结果
        ServeAggregationSimpleResDTO dto = BeanUtil.copyProperties(serve, ServeAggregationSimpleResDTO.class);
        dto.setServeItemName(serveItem.getName());
        dto.setServeItemImg(serveItem.getImg());
        dto.setDetailImg(serveItem.getDetailImg());
        dto.setUnit(serveItem.getUnit());

        return dto;
    }

    @Override
    public List<ServeAggregationTypeSimpleResDTO> serveTypeList(Long regionId) {
        //1 对区域进行校验
        Region region = regionMapper.selectById(regionId);
        if (ObjectUtil.isNull(region) || region.getActiveStatus() != 2) {
            return Collections.emptyList();
        }

        //2 查询当前区域下上架服务对应的分类
        return serveMapper.findServeTypeListByRegionId(regionId);
    }

    @Override
    public List<ServeSimpleResDTO> search(String city, String keyword, Long serveTypeId) {
        // 构建请求参数
        SearchRequest request = new SearchRequest("serve_aggregation");
        request.source().sort("serve_type_sort_num", SortOrder.ASC);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("city_code", city));
        if (StrUtil.isNotEmpty(keyword)) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(keyword, "serve_item_name", "serve_type_name"));
        }
        if (ObjectUtil.isNotEmpty(serveTypeId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery("serve_type_id", serveTypeId));
        }
        // 发送请求
        SearchResponse searchResponse;
        try {
            searchResponse = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 解析数据
        if (searchResponse.getHits().getTotalHits().value == 0) {
            return List.of();
        }
        return Arrays.stream(searchResponse.getHits().getHits())
                .map(e -> JSONUtil.toBean(e.getSourceAsString(), ServeSimpleResDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServeAggregationResDTO findServeDetailById(Long id) {
        return baseMapper.findServeDetailById(id);
    }

    /**
     * 根据id校验服务是否存在
     */
    private void valid(Long id) {
        Serve serve = getById(id);
        if (ObjectUtil.isEmpty(serve)) {
            throw new ForbiddenOperationException("操作失败，该服务不存在");
        }
    }

    /**
     * 新增服务同步数据
     *
     * @param serveId 服务id
     */
    private void addServeSync(Long serveId) {
        //服务信息
        Serve serve = baseMapper.selectById(serveId);
        //区域信息
        Region region = regionMapper.selectById(serve.getRegionId());
        //服务项信息
        ServeItem serveItem = serveItemMapper.selectById(serve.getServeItemId());
        //服务类型
        ServeType serveType = serveTypeMapper.selectById(serveItem.getServeTypeId());

        ServeSync serveSync = new ServeSync();
        serveSync.setServeTypeId(serveType.getId());
        serveSync.setServeTypeName(serveType.getName());
        serveSync.setServeTypeIcon(serveType.getServeTypeIcon());
        serveSync.setServeTypeImg(serveType.getImg());
        serveSync.setServeTypeSortNum(serveType.getSortNum());

        serveSync.setServeItemId(serveItem.getId());
        serveSync.setServeItemIcon(serveItem.getServeItemIcon());
        serveSync.setServeItemName(serveItem.getName());
        serveSync.setServeItemImg(serveItem.getImg());
        serveSync.setServeItemSortNum(serveItem.getSortNum());
        serveSync.setUnit(serveItem.getUnit());
        serveSync.setDetailImg(serveItem.getDetailImg());
        serveSync.setPrice(serve.getPrice());

        serveSync.setCityCode(region.getCityCode());
        serveSync.setId(serve.getId());
        serveSync.setIsHot(serve.getIsHot());
        serveSyncMapper.insert(serveSync);
    }
}
