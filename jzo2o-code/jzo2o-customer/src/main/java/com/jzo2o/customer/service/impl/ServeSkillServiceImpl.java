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
import com.jzo2o.customer.service.IServeProviderSettingsService;
import com.jzo2o.customer.service.IServeProviderSyncService;
import com.jzo2o.customer.service.IServeSkillService;
import com.jzo2o.mvc.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 鏈嶅姟鎶€鑳芥湇鍔″疄鐜扮被
 */
@Service
public class ServeSkillServiceImpl extends ServiceImpl<ServeSkillMapper, ServeSkill> implements IServeSkillService {

    @Resource
    private ServeTypeApi serveTypeApi;

    @Resource
    private ServeItemApi serveItemApi;

    @Resource
    private IServeProviderSettingsService serveProviderSettingsService;

    @Resource
    private IServeProviderSyncService serveProviderSyncService;

    @Resource
    private IServeSkillService serveSkillService;

    /**
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpsert(List<ServeSkillAddReqDTO> serveSkillAddReqDTOList) {
        CurrentUserInfo currentUserInfo = UserContext.currentUser();

        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId());
        baseMapper.delete(queryWrapper);

        List<ServeSkill> serveSkillList = BeanUtil.copyToList(serveSkillAddReqDTOList, ServeSkill.class);
        serveSkillList.forEach(s -> {
            s.setServeProviderId(currentUserInfo.getId());
            s.setServeProviderType(currentUserInfo.getUserType());
            s.setStaffName(currentUserInfo.getName());
            s.setAuditStatus(AuditStatus.AUDITING.getStatus());
        });
        super.saveBatch(serveSkillList);

        serveProviderSettingsService.setHaveSkill(UserContext.currentUserId());

        List<Long> serveItemIds = serveSkillAddReqDTOList.stream()
                .map(ServeSkillAddReqDTO::getServeItemId)
                .collect(Collectors.toList());

        ServeProviderSync serveProviderSync = ServeProviderSync.builder()
                .id(UserContext.currentUserId())
                .serveItemIds(serveItemIds)
                .build();
        serveProviderSyncService.updateById(serveProviderSync);
    }

    /**
     * 鏌ヨ鏈嶅姟鎶€鑳界洰褰曪細濉厖閫変腑鐘舵€併€佸鏍哥姸鎬佺爜鍜屾枃妗?     */
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

        List<ServeSkillCategoryResDTO> list = BeanUtils.copyToList(
                serveTypeCategory,
                ServeSkillCategoryResDTO.class,
                (origin, target) -> target.setServeSkillItemResDTOList(
                        BeanUtils.copyToList(origin.getServeItemList(), ServeSkillItemResDTO.class)
                )
        );

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
                    item.setAuditStatus(null);
                    item.setAuditStatus(null);
                    return;
                }
                item.setIsSelected(true);
                item.setAuditStatus(serveSkill.getAuditStatus());
            });
        });
        return list;
    }

    @Override
    public List<Long> queryServeSkillListByServeProvider(Long providerId, Integer providerType, String cityCode) {
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, providerType)
                .eq(ServeSkill::getServeProviderId, providerId);
        List<ServeSkill> serveSkillList = baseMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(serveSkillList)) {
            return Collections.emptyList();
        }
        return serveSkillList.stream().map(ServeSkill::getServeItemId).collect(Collectors.toList());
    }

    @Override
    public List<ServeTypeSimpleResDTO> queryCurrentUserServeSkillTypeList() {
        CurrentUserInfo currentUserInfo = UserContext.currentUser();
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId());
        List<ServeSkill> serveSkillList = baseMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(serveSkillList)) {
            return Collections.emptyList();
        }

        List<Long> skillServeTypeId = serveSkillList.stream()
                .map(ServeSkill::getServeTypeId)
                .distinct()
                .collect(Collectors.toList());

        return serveTypeApi.listByIds(skillServeTypeId);
    }

    @Override
    public List<ServeItemSimpleResDTO> queryCurrentUserServeSkillItemList() {
        CurrentUserInfo currentUserInfo = UserContext.currentUser();
        LambdaQueryWrapper<ServeSkill> queryWrapper = Wrappers.<ServeSkill>lambdaQuery()
                .eq(ServeSkill::getServeProviderType, currentUserInfo.getUserType())
                .eq(ServeSkill::getServeProviderId, currentUserInfo.getId())
                .select(ServeSkill::getServeItemId);
        List<ServeSkill> serveSkillList = baseMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(serveSkillList)) {
            return Collections.emptyList();
        }
        List<Long> serveItemIds = serveSkillList.stream().map(ServeSkill::getServeItemId).collect(Collectors.toList());
        return serveItemApi.listByIds(serveItemIds);
    }

    /**
     * 发送审核请求
     */
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

    /**
     * 审核分页列表     */
    @Override
    @Transactional
    public void messageAudit(AuditRequest request) {
        if (request == null) {
            throw new ForbiddenOperationException("请求参数为空");
        }
        if (request.getId() == null) {
            throw new ForbiddenOperationException("审核记录id不能为空");
        }
        if (request.getServeProviderId() == null) {
            throw new ForbiddenOperationException("服务人员id不能为空");
        }
        if (!AuditStatus.contains(request.getAuditStatus())) {
            throw new ForbiddenOperationException("审核状态不合法");
        }
        if (StrUtil.isEmpty(request.getAuditReason())) {
            throw new ForbiddenOperationException("审核描述不能为空");
        }

        UpdateWrapper<ServeSkill> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", request.getId());
        updateWrapper.set("audit_reason", request.getAuditReason());
        updateWrapper.set("audit_status", request.getAuditStatus());
        update(updateWrapper);

        serveSkillService.skillSetting(request.getId(), request.getServeProviderId());
    }

    /**
     * 同步技能到es同步表
     * */
    public void skillSetting(Long id, Long staffId) {
        List<ServeSkill> list = lambdaQuery()
                .eq(ServeSkill::getServeProviderId, staffId)
                .eq(ServeSkill::getAuditStatus, AuditStatus.AUDIT_PASS.getStatus())
                .list();

        if (CollUtils.isNotEmpty(list)) {
            serveProviderSettingsService.setHaveSkill(staffId);
            List<Long> collect = list.stream().map(ServeSkill::getServeItemId).collect(Collectors.toList());
            serveProviderSyncService.lambdaUpdate()
                    .eq(ServeProviderSync::getId, staffId)
                    .set(ServeProviderSync::getServeItemIds, collect.toString())
                    .update();
        } else {
            serveProviderSyncService.lambdaUpdate()
                    .eq(ServeProviderSync::getId, staffId)
                    .set(ServeProviderSync::getServeItemIds, null)
                    .update();
        }
    }

    @Override
    public PageResult<ServeSkill> skillPage(AuditPageRequest request) {

        Page<ServeSkill> page = new Page<>(request.getPageNo(), request.getPageSize());

        Page<ServeSkill> resultPage = lambdaQuery()
                .eq(request.getServeTypeId() != null, ServeSkill::getServeTypeId, request.getServeTypeId())
                .eq(request.getServeItemId() != null, ServeSkill::getServeItemId, request.getServeItemId())
                .eq(request.getAuditStatus() != null, ServeSkill::getAuditStatus, request.getAuditStatus())
                .like(request.getStaffName() != null && !request.getStaffName().isEmpty(),
                        ServeSkill::getStaffName, request.getStaffName())
                .eq(ServeSkill::getIsDelete, 0)
                .page(page);

        PageResult<ServeSkill> pageResult = new PageResult<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setList(resultPage.getRecords());
        return pageResult;
    }
}
