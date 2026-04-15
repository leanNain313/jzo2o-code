package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.foundations.ServeItemApi;
import com.jzo2o.api.foundations.ServeTypeApi;
import com.jzo2o.api.foundations.dto.response.ServeItemSimpleResDTO;
import com.jzo2o.api.foundations.dto.response.ServeTypeCategoryResDTO;
import com.jzo2o.api.foundations.dto.response.ServeTypeSimpleResDTO;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.customer.enums.AuditStatus;
import com.jzo2o.customer.mapper.ServeSkillMapper;
import com.jzo2o.customer.model.domain.ServeProviderSync;
import com.jzo2o.customer.model.domain.ServeSkill;
import com.jzo2o.customer.model.dto.request.AuditPageRequest;
import com.jzo2o.customer.model.dto.request.AuditRequest;
import com.jzo2o.customer.model.dto.request.ServeSkillAddReqDTO;
import com.jzo2o.customer.model.dto.response.ServeSkillCategoryResDTO;
import com.jzo2o.customer.model.dto.response.ServeSkillItemResDTO;
import com.jzo2o.customer.service.IServeProviderService;
import com.jzo2o.customer.service.IServeProviderSettingsService;
import com.jzo2o.customer.service.IServeProviderSyncService;
import com.jzo2o.customer.service.IServeSkillService;
import com.jzo2o.mvc.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 鏈嶅姟鎶€鑳借〃 鏈嶅姟瀹炵幇绫?
 * </p>
 *
 * @author itcast
 * @since 2023-07-18
 */
@Service
public class ServeSkillServiceImpl extends ServiceImpl<ServeSkillMapper, ServeSkill> implements IServeSkillService {
    @Resource
    private ServeTypeApi serveTypeApi;
    @Resource
    private ServeItemApi serveItemApi;
    @Resource
    private IServeProviderService serveProviderService;
    @Resource
    private IServeProviderSettingsService serveProviderSettingsService;

    @Resource
    private IServeProviderSyncService serveProviderSyncService;

    @Resource
    private IServeSkillService serveSkillService;

    /**
     * 鎵归噺鏂板鎴栦慨鏀?
     *
     * @param serveSkillAddReqDTOList 鎵归噺鏂板鎴栦慨鏀规暟鎹?
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpsert(List<ServeSkillAddReqDTO> serveSkillAddReqDTOList) {
        CurrentUserInfo currentUserInfo = UserContext.currentUser();

        //1.鍒犻櫎涓婁竴娆¤鐢ㄦ埛璁剧疆鐨勬湇鍔℃妧鑳?
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId());
        baseMapper.delete(queryWrapper);

        //2.娣诲姞鏂扮殑鏈嶅姟鎶€鑳?
        List<ServeSkill> serveSkillList = BeanUtil.copyToList(serveSkillAddReqDTOList, ServeSkill.class);
        serveSkillList.forEach(s -> {
            s.setServeProviderId(currentUserInfo.getId());
            s.setServeProviderType(currentUserInfo.getUserType());
            s.setStaffName(currentUserInfo.getName());
            s.setAuditStatus(AuditStatus.AUDITING.getStatus());
        });
        super.saveBatch(serveSkillList);

        // 3.璁剧疆鎶€鑳?
        serveProviderSettingsService.setHaveSkill(UserContext.currentUserId());
//        // 4.鏍￠獙骞惰缃垵娆¤缃畬鎴?
//        serveProviderService.settingStatus(UserContext.currentUserId());
        // 5.鏍煎紡鍖栨湇鍔℃妧鑳斤紝鍑嗗鎻掑叆鍚屾琛?
        List<Long> serveItemIds = serveSkillAddReqDTOList.stream()
                .map(ServeSkillAddReqDTO::getServeItemId)
                .collect(Collectors.toList());
        //鍐欏叆鏈嶅姟鎻愪緵鑰呭悓姝ヨ〃锛屽皢鏉ョ敱鍚屾浠诲姟鍚屾鍒癊S
        ServeProviderSync serveProviderSync = ServeProviderSync.builder()
                .id(UserContext.currentUserId())
                .serveItemIds(serveItemIds)
                .build();
        serveProviderSyncService.updateById(serveProviderSync);
    }

    /**
     * 鏌ヨ鏈嶅姟鎶€鑳界洰褰?
     *
     * @return 鏈嶅姟鎶€鑳界洰褰?
     */
        @Override
    public List<ServeSkillCategoryResDTO> category() {
        CurrentUserInfo currentUserInfo = UserContext.currentUser();
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId())
                .eq(ServeSkill::getIsDelete, 0)
                .select(ServeSkill::getServeTypeId, ServeSkill::getServeItemId, ServeSkill::getAuditStatus);
        List<ServeSkill> serveSkillList = baseMapper.selectList(queryWrapper);

        List<ServeTypeCategoryResDTO> serveTypeCategory = serveItemApi.queryActiveServeItemCategory();
        if (ObjectUtil.isEmpty(serveTypeCategory)) {
            return Collections.emptyList();
        }

        List<ServeSkillCategoryResDTO> list = BeanUtils.copyToList(serveTypeCategory, ServeSkillCategoryResDTO.class, (origin, target) -> {
            target.setServeSkillItemResDTOList(BeanUtils.copyToList(origin.getServeItemList(), ServeSkillItemResDTO.class));
        });

        Map<Long, Long> skillTypeCount = serveSkillList.stream()
                .collect(Collectors.groupingBy(ServeSkill::getServeTypeId, Collectors.counting()));
        Map<Long, ServeSkill> serveSkillMap = serveSkillList.stream()
                .collect(Collectors.toMap(ServeSkill::getServeItemId, skill -> skill, (oldValue, newValue) -> newValue));

        list.forEach(type -> {
            Long count = skillTypeCount.get(type.getServeTypeId());
            type.setCount(count == null ? 0 : count.intValue());
            type.getServeSkillItemResDTOList().forEach(item -> {
                ServeSkill serveSkill = serveSkillMap.get(item.getServeItemId());
                if (serveSkill == null) {
                    item.setIsSelected(false);
                    item.setAuditStatusCode(null);
                    item.setAuditStatus(null);
                    return;
                }
                item.setIsSelected(true);
                item.setAuditStatusCode(serveSkill.getAuditStatus());
                AuditStatus auditStatus = AuditStatus.of(serveSkill.getAuditStatus());
                item.setAuditStatus(auditStatus == null ? null : auditStatus.getDescription());
            });
        });
        return list;
    }

    /**
     * 鏌ヨ鏈嶅姟鑰呯殑鏈嶅姟鎶€鑳?
     *
     * @param providerId   鏈嶅姟鑰卛d
     * @param providerType 鏈嶅姟鑰呯被鍨?
     * @param cityCode     鍩庡競缂栫爜
     * @return 鏈嶅姟鎶€鑳藉垪琛?
     */
    @Override
    public List<Long> queryServeSkillListByServeProvider(Long providerId, Integer providerType, String cityCode) {
        //1.鑾峰彇鏈嶅姟鑰呯殑鎵€鏈夋湇鍔℃妧鑳?
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, providerType)
                .eq(ServeSkill::getServeProviderId, providerId);
        List<ServeSkill> serveSkillList = baseMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(serveSkillList)) {
            return Collections.emptyList();
        }

        //2.浠庢妧鑳戒腑鎻愬彇鏈嶅姟椤瑰垪琛?
        List<Long> skillServeItemIds = serveSkillList.stream().map(ServeSkill::getServeItemId).collect(Collectors.toList());

        return skillServeItemIds;
    }

    /**
     * 鑾峰彇鏈嶅姟鑰呯殑鎶€鑳藉垎绫?
     *
     * @return 鎶€鑳藉垎绫诲垪琛?
     */
    @Override
    public List<ServeTypeSimpleResDTO> queryCurrentUserServeSkillTypeList() {
        //1.鏌ヨ褰撳墠鐢ㄦ埛鐨勬湇鍔℃妧鑳?
        CurrentUserInfo currentUserInfo = UserContext.currentUser();
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId());
        List<ServeSkill> serveSkillList = baseMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(serveSkillList)) {
            return Collections.emptyList();
        }

        //2.鎻愬彇鍑烘湇鍔＄被鍨媔d锛屽苟鍘婚噸
        List<Long> skillServeTypeId = serveSkillList.stream().map(ServeSkill::getServeTypeId).distinct().collect(Collectors.toList());

        //3.鏍规嵁id鍒楄〃鏌ヨ鏈嶅姟绫诲瀷
        return serveTypeApi.listByIds(skillServeTypeId);
    }

    /**
     * 鑾峰彇鏈嶅姟鑰呯殑鎵€鏈夋妧鑳?
     *
     * @return 鎶€鑳藉垪琛?
     */
    @Override
    public List<ServeItemSimpleResDTO> queryCurrentUserServeSkillItemList() {
        //1.鏌ヨ褰撳墠鐢ㄦ埛鐨勬湇鍔℃妧鑳?
        CurrentUserInfo currentUserInfo = UserContext.currentUser();
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId())
                .select(ServeSkill::getServeItemId);
        List<ServeSkill> serveSkillList = baseMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(serveSkillList)) {
            return Collections.emptyList();
        }

        //2.鎻愬彇鏈嶅姟椤筰d鍒楄〃鏌ヨ鍚嶇О淇℃伅
        List<Long> serveItemIds = serveSkillList.stream().map(ServeSkill::getServeItemId).collect(Collectors.toList());
        return serveItemApi.listByIds(serveItemIds);
    }

        @Override
    public void sendAudit(ServeSkillAddReqDTO request) {
        CurrentUserInfo currentUserInfo = UserContext.currentUser();

        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId())
                .eq(ServeSkill::getServeItemId, request.getServeItemId())
                .eq(ServeSkill::getIsDelete, 0)
                .orderByDesc(ServeSkill::getUpdateTime, ServeSkill::getCreateTime, ServeSkill::getId)
                .last("limit 1");

        ServeSkill existSkill = baseMapper.selectOne(queryWrapper);
        if (existSkill != null) {
            existSkill.setServeTypeId(request.getServeTypeId());
            existSkill.setServeTypeName(request.getServeTypeName());
            existSkill.setServeItemId(request.getServeItemId());
            existSkill.setServeItemName(request.getServeItemName());
            existSkill.setStaffName(currentUserInfo.getName());
            existSkill.setAuditStatus(AuditStatus.AUDITING.getStatus());
            existSkill.setAuditReason(null);
            updateById(existSkill);
            return;
        }

        ServeSkill serveSkill = BeanUtil.copyProperties(request, ServeSkill.class);
        serveSkill.setServeProviderId(currentUserInfo.getId());
        serveSkill.setServeProviderType(currentUserInfo.getUserType());
        serveSkill.setStaffName(currentUserInfo.getName());
        serveSkill.setAuditStatus(AuditStatus.AUDITING.getStatus());
        serveSkill.setAuditReason(null);
        save(serveSkill);
    }

    @Override
    @Transactional
    public void messageAudit(AuditRequest request) {
        if (request == null) {
            throw new ForbiddenOperationException("璇锋眰鍙傛暟涓虹┖");
        }
        if (AuditStatus.contains(request.getAuditStatus())) {
            throw new ForbiddenOperationException("娌℃湁璇ョ姸鎬?);
        }
        if (StrUtil.isEmpty(request.getAuditReason())) {
            throw new ForbiddenOperationException("瀹℃牳鎻忚堪涓虹┖");
        }
        UpdateWrapper<ServeSkill> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", request);
        updateWrapper.set("audit_reason", request.getAuditReason());
        updateWrapper.set("audit_status", request.getAuditStatus());
        update(updateWrapper);

        // 鍚屾鍒版湇鍔′汉鍛榚s鍚屾琛ㄩ噷闈?
        serveSkillService.skillSetting(request.getId(), request.getServeProviderId());
    }

    /**
     * 鍚屾鍒版妧鑳借〃
     * @param id 瀹℃牳id
     * @param staffId 鏈嶅姟浜哄憳id
     */
    public void skillSetting(Long id, Long staffId) {
        //鑾峰彇瀹℃牳閫氳繃鐨勬妧鑳?
        List<ServeSkill> list = lambdaQuery().eq(ServeSkill::getServeProviderId, staffId)
                .eq(ServeSkill::getAuditStatus, AuditStatus.AUDIT_PASS.getStatus())
                .list();
        if (CollUtils.isNotEmpty(list)) {
            serveProviderSettingsService.setHaveSkill(staffId);

            // 鍑嗗鏈嶅姟椤筰d鍒楄〃鍙傛暟
            List<Long> collect = list.stream().map(ServeSkill::getServeItemId)
                    .collect(Collectors.toList());
            //鍐欏叆鏈嶅姟鎻愪緵鑰呭悓姝ヨ〃锛屽皢鏉ョ敱鍚屾浠诲姟鍚屾鍒癊S
            ServeProviderSync serveProviderSync = ServeProviderSync.builder()
                    .id(staffId)
                    .serveItemIds(collect)
                    .build();
            serveProviderSyncService.lambdaUpdate()
                    .eq(ServeProviderSync::getId, staffId)
                    .set(ServeProviderSync::getServeItemIds, collect).update();
        } else {
            serveProviderSyncService.lambdaUpdate()
                    .eq(ServeProviderSync::getId, staffId)
                    .set(ServeProviderSync::getServeItemIds, null).update();
        }
    }

    @Override
    public PageResult<ServeSkill> skillPage(AuditPageRequest request) {

        // 1. 鍒嗛〉瀵硅薄
        Page<ServeSkill> page = new Page<>(request.getPageNo(), request.getPageSize());

        // 2. 鏌ヨ
        Page<ServeSkill> resultPage = lambdaQuery()
                .eq(request.getServeTypeId() != null, ServeSkill::getServeTypeId, request.getServeTypeId())
                .eq(request.getServeItemId() != null, ServeSkill::getServeItemId, request.getServeItemId())
                .eq(request.getAuditStatus() != null, ServeSkill::getAuditStatus, request.getAuditStatus())
                .like(request.getStaffName() != null && !request.getStaffName().isEmpty(),
                        ServeSkill::getStaffName, request.getStaffName())
                .eq(ServeSkill::getIsDelete, 0)
                .page(page);

        // 3. 灏佽杩斿洖
        PageResult<ServeSkill> pageResult = new PageResult<>();
        page.setTotal(page.getTotal());
        pageResult.setList(pageResult.getList());
        return pageResult;
    }
}

