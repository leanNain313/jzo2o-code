package com.jzo2o.foundations.controller.inner;

import cn.hutool.core.bean.BeanUtil;
import com.jzo2o.api.foundations.dto.request.JudgeRequest;
import com.jzo2o.api.foundations.dto.response.ServeAggregationResDTO;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//内部接口 - 服务相关接口
@RestController
@RequestMapping(value = "/inner/serve")
@ApiOperation("内部接口 - 服务相关接口")
public class InnerServeController {

    @Resource
    private IServeService serveService;

    //根据ID查询服务详情
    @GetMapping("/{id}")
    public ServeAggregationResDTO findById(@PathVariable("id")Long id){
        return serveService.findServeDetailById(id);
    }

    @GetMapping("/serve")
    public ServeAggregationResDTO getServeByCityCodeAndItemId(Long serveItemId, String cityCode) {
        return BeanUtil.toBean(serveService.lambdaQuery().eq(Serve::getCityCode, cityCode)
                .eq(Serve::getServeItemId, serveItemId)
                .one(), ServeAggregationResDTO.class);
    }

    /**
     * 复用客户端首页同款区域上线服务查询，供智能体通过 Feign 获取真实可见服务。
     */
    @GetMapping("/firstPageServeList")
    public List<ServeCategoryResDTO> firstPageServeList(@RequestParam("regionId") Long regionId) {
        return serveService.firstPageServeList(regionId);
    }

    @PostMapping("/region")
    public Boolean judgeServe(@RequestBody JudgeRequest request) {
        Integer count = serveService.lambdaQuery()
                .eq(Serve::getCityCode, request.getCityCode())
                .eq(Serve::getId, request.getServeId())
                .count();
        return count < 1 ? Boolean.FALSE : Boolean.TRUE;
    }

    @PostMapping("/place/{id}")
    public void placeOrder(@PathVariable Long id) {
        serveService.placeOrder(id);
    }

    /**
     * 按城市编码和关键词搜索服务
     * <p>
     * 复用 IServeService.search() 已有的 ES 全文检索能力，
     * 避免在智能体侧重复实现搜索逻辑（DRY）。
     * serveTypeId 传 null 表示不限服务类型。
     * </p>
     */
    @GetMapping("/search")
    public List<ServeSimpleResDTO> search(
            @RequestParam("cityCode") String cityCode,
            @RequestParam(value = "keyword", required = false) String keyword) {
        // 委托已有的 ES 搜索实现，serveTypeId 不限
        return serveService.search(cityCode, keyword, null);
    }
}
