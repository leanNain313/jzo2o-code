package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.constants.UserType;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.enums.AnnouncementStatusEnum;
import com.jzo2o.customer.enums.AnnouncementTypeEnum;
import com.jzo2o.customer.mapper.SystemAnnouncementMapper;
import com.jzo2o.customer.model.domain.SystemAnnouncement;
import com.jzo2o.customer.model.domain.SystemAnnouncementRead;
import com.jzo2o.customer.model.dto.request.AnnouncementPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AnnouncementSaveReqDTO;
import com.jzo2o.customer.model.dto.response.AnnouncementResDTO;
import com.jzo2o.customer.service.ISystemAnnouncementReadService;
import com.jzo2o.customer.service.ISystemAnnouncementService;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SystemAnnouncementServiceImpl extends ServiceImpl<SystemAnnouncementMapper, SystemAnnouncement>
        implements ISystemAnnouncementService {

    private static final int NOT_DELETED = 0;

    @Resource
    private ISystemAnnouncementReadService announcementReadService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAnnouncement(AnnouncementSaveReqDTO reqDTO, CurrentUserInfo currentUser) {
        validateSaveReq(reqDTO);
        Integer status = ObjectUtil.defaultIfNull(reqDTO.getStatus(), AnnouncementStatusEnum.DRAFT.getStatus());
        SystemAnnouncement announcement = BeanUtil.toBean(reqDTO, SystemAnnouncement.class);
        announcement.setStatus(status);
        announcement.setOperatorId(currentUser.getId());
        announcement.setOperatorName(currentUser.getName());
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setIsDeleted(NOT_DELETED);
        if (AnnouncementStatusEnum.PUBLISHED.getStatus().equals(status)) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        if (AnnouncementStatusEnum.OFFLINE.getStatus().equals(status)) {
            announcement.setOfflineTime(LocalDateTime.now());
        }
        save(announcement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(Long id, AnnouncementSaveReqDTO reqDTO, CurrentUserInfo currentUser) {
        validateSaveReq(reqDTO);
        SystemAnnouncement old = getNotDeleted(id);
        Integer status = ObjectUtil.defaultIfNull(reqDTO.getStatus(), old.getStatus());
        SystemAnnouncement update = BeanUtil.toBean(reqDTO, SystemAnnouncement.class);
        update.setId(id);
        update.setStatus(status);
        update.setOperatorId(currentUser.getId());
        update.setOperatorName(currentUser.getName());
        update.setUpdateTime(LocalDateTime.now());
        if (AnnouncementStatusEnum.PUBLISHED.getStatus().equals(status)
                && ObjectUtil.isNull(old.getPublishTime())) {
            update.setPublishTime(LocalDateTime.now());
        }
        if (AnnouncementStatusEnum.OFFLINE.getStatus().equals(status)
                && ObjectUtil.notEqual(old.getStatus(), AnnouncementStatusEnum.OFFLINE.getStatus())) {
            update.setOfflineTime(LocalDateTime.now());
        }
        updateById(update);
    }

    @Override
    public PageResult<AnnouncementResDTO> operationPage(AnnouncementPageQueryReqDTO queryReqDTO) {
        Page<SystemAnnouncement> page = PageUtils.parsePageQuery(queryReqDTO, SystemAnnouncement.class);
        Page<SystemAnnouncement> result = page(page, buildOperationWrapper(queryReqDTO));
        return toPageResult(result, Collections.emptySet());
    }

    @Override
    public AnnouncementResDTO operationDetail(Long id) {
        return BeanUtil.toBean(getNotDeleted(id), AnnouncementResDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id, CurrentUserInfo currentUser) {
        SystemAnnouncement announcement = getNotDeleted(id);
        SystemAnnouncement update = new SystemAnnouncement()
                .setId(id)
                .setStatus(AnnouncementStatusEnum.PUBLISHED.getStatus())
                .setPublishTime(ObjectUtil.defaultIfNull(announcement.getPublishTime(), LocalDateTime.now()))
                .setOfflineTime(null)
                .setOperatorId(currentUser.getId())
                .setOperatorName(currentUser.getName())
                .setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long id, CurrentUserInfo currentUser) {
        getNotDeleted(id);
        SystemAnnouncement update = new SystemAnnouncement()
                .setId(id)
                .setStatus(AnnouncementStatusEnum.OFFLINE.getStatus())
                .setOfflineTime(LocalDateTime.now())
                .setOperatorId(currentUser.getId())
                .setOperatorName(currentUser.getName())
                .setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public PageResult<AnnouncementResDTO> receiverPage(AnnouncementPageQueryReqDTO queryReqDTO, Long userId, Integer userType) {
        Page<SystemAnnouncement> page = PageUtils.parsePageQuery(queryReqDTO, SystemAnnouncement.class);
        Page<SystemAnnouncement> result = page(page, buildReceiverWrapper(queryReqDTO, userType));
        Set<Long> readIds = readAnnouncementIds(userId, userType);
        return toPageResult(result, readIds);
    }

    @Override
    public AnnouncementResDTO receiverDetail(Long id, Long userId, Integer userType) {
        SystemAnnouncement announcement = getNotDeleted(id);
        if (!AnnouncementStatusEnum.PUBLISHED.getStatus().equals(announcement.getStatus())
                || !visibleTypes(userType).contains(announcement.getType())) {
            throw new ForbiddenOperationException("无权查看该公告");
        }
        AnnouncementResDTO dto = BeanUtil.toBean(announcement, AnnouncementResDTO.class);
        dto.setRead(readAnnouncementIds(userId, userType).contains(id));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id, Long userId, Integer userType) {
        receiverDetail(id, userId, userType);
        SystemAnnouncementRead read = new SystemAnnouncementRead()
                .setAnnouncementId(id)
                .setUserId(userId)
                .setUserType(userType)
                .setReadTime(LocalDateTime.now());
        try {
            announcementReadService.save(read);
        } catch (DuplicateKeyException ignored) {
            // Idempotent read reporting.
        }
    }

    @Override
    public Long unreadCount(Long userId, Integer userType) {
        Set<Long> readIds = readAnnouncementIds(userId, userType);
        LambdaQueryWrapper<SystemAnnouncement> wrapper = Wrappers.<SystemAnnouncement>lambdaQuery()
                .eq(SystemAnnouncement::getIsDeleted, NOT_DELETED)
                .eq(SystemAnnouncement::getStatus, AnnouncementStatusEnum.PUBLISHED.getStatus())
                .in(SystemAnnouncement::getType, visibleTypes(userType));
        if (!readIds.isEmpty()) {
            wrapper.notIn(SystemAnnouncement::getId, readIds);
        }
        return count(wrapper);
    }

    private void validateSaveReq(AnnouncementSaveReqDTO reqDTO) {
        if (!AnnouncementTypeEnum.valid(reqDTO.getType())) {
            throw new BadRequestException("公告类型不正确");
        }
        Integer status = ObjectUtil.defaultIfNull(reqDTO.getStatus(), AnnouncementStatusEnum.DRAFT.getStatus());
        if (!AnnouncementStatusEnum.valid(status)) {
            throw new BadRequestException("公告状态不正确");
        }
    }

    private SystemAnnouncement getNotDeleted(Long id) {
        SystemAnnouncement announcement = getById(id);
        if (announcement == null || ObjectUtil.notEqual(announcement.getIsDeleted(), NOT_DELETED)) {
            throw new BadRequestException("公告不存在");
        }
        return announcement;
    }

    private LambdaQueryWrapper<SystemAnnouncement> buildOperationWrapper(AnnouncementPageQueryReqDTO queryReqDTO) {
        return Wrappers.<SystemAnnouncement>lambdaQuery()
                .eq(SystemAnnouncement::getIsDeleted, NOT_DELETED)
                .like(StrUtil.isNotBlank(queryReqDTO.getTitle()), SystemAnnouncement::getTitle, queryReqDTO.getTitle())
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getType()), SystemAnnouncement::getType, queryReqDTO.getType())
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getStatus()), SystemAnnouncement::getStatus, queryReqDTO.getStatus())
                .between(ObjectUtil.isAllNotEmpty(queryReqDTO.getMinPublishTime(), queryReqDTO.getMaxPublishTime()),
                        SystemAnnouncement::getPublishTime, queryReqDTO.getMinPublishTime(), queryReqDTO.getMaxPublishTime())
                .orderByDesc(SystemAnnouncement::getCreateTime);
    }

    private LambdaQueryWrapper<SystemAnnouncement> buildReceiverWrapper(AnnouncementPageQueryReqDTO queryReqDTO, Integer userType) {
        return Wrappers.<SystemAnnouncement>lambdaQuery()
                .eq(SystemAnnouncement::getIsDeleted, NOT_DELETED)
                .eq(SystemAnnouncement::getStatus, AnnouncementStatusEnum.PUBLISHED.getStatus())
                .in(SystemAnnouncement::getType, visibleTypes(userType))
                .like(StrUtil.isNotBlank(queryReqDTO.getTitle()), SystemAnnouncement::getTitle, queryReqDTO.getTitle())
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getType()), SystemAnnouncement::getType, queryReqDTO.getType())
                .between(ObjectUtil.isAllNotEmpty(queryReqDTO.getMinPublishTime(), queryReqDTO.getMaxPublishTime()),
                        SystemAnnouncement::getPublishTime, queryReqDTO.getMinPublishTime(), queryReqDTO.getMaxPublishTime())
                .orderByDesc(SystemAnnouncement::getPublishTime)
                .orderByDesc(SystemAnnouncement::getCreateTime);
    }

    private List<Integer> visibleTypes(Integer userType) {
        if (ObjectUtil.equal(userType, UserType.C_USER)) {
            return AnnouncementTypeEnum.consumerVisibleTypes();
        }
        if (ObjectUtil.equal(userType, UserType.WORKER)) {
            return AnnouncementTypeEnum.workerVisibleTypes();
        }
        throw new ForbiddenOperationException("当前用户无权查看公告");
    }

    private Set<Long> readAnnouncementIds(Long userId, Integer userType) {
        return announcementReadService.lambdaQuery()
                .eq(SystemAnnouncementRead::getUserId, userId)
                .eq(SystemAnnouncementRead::getUserType, userType)
                .list()
                .stream()
                .map(SystemAnnouncementRead::getAnnouncementId)
                .collect(Collectors.toSet());
    }

    private PageResult<AnnouncementResDTO> toPageResult(Page<SystemAnnouncement> page, Set<Long> readIds) {
        List<AnnouncementResDTO> list = page.getRecords().stream().map(item -> {
            AnnouncementResDTO dto = BeanUtil.toBean(item, AnnouncementResDTO.class);
            if (!readIds.isEmpty()) {
                dto.setRead(readIds.contains(item.getId()));
            }
            return dto;
        }).collect(Collectors.toList());
        PageResult<AnnouncementResDTO> pageResult = PageResult.getInstance();
        pageResult.setTotal(page.getTotal());
        pageResult.setPages(page.getPages());
        pageResult.setList(list);
        return pageResult;
    }
}
