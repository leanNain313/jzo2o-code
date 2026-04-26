package com.jzo2o.orders.manager.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.constants.UserType;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.mysql.utils.PageUtils;
import com.jzo2o.orders.base.enums.AfterSalesStatusEnum;
import com.jzo2o.orders.base.enums.OrderStatusEnum;
import com.jzo2o.orders.base.enums.OrderRefundStatusEnum;
import com.jzo2o.orders.base.mapper.AfterSalesMapper;
import com.jzo2o.orders.base.model.domain.AfterSales;
import com.jzo2o.orders.base.model.domain.AfterSalesRecord;
import com.jzo2o.orders.base.model.domain.Orders;
import com.jzo2o.orders.base.model.domain.OrdersServe;
import com.jzo2o.orders.manager.model.dto.OrderCancelDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesApplyReqDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesAuditReqDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesPageQueryReqDTO;
import com.jzo2o.orders.manager.model.dto.response.AfterSalesRecordResDTO;
import com.jzo2o.orders.manager.model.dto.response.AfterSalesResDTO;
import com.jzo2o.orders.manager.service.IAfterSalesRecordService;
import com.jzo2o.orders.manager.service.IAfterSalesService;
import com.jzo2o.orders.manager.service.IOrdersManagerService;
import com.jzo2o.orders.manager.service.IOrdersServeManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AfterSalesServiceImpl extends ServiceImpl<AfterSalesMapper, AfterSales> implements IAfterSalesService {

    private static final int YES = 1;

    @Resource
    private IAfterSalesRecordService afterSalesRecordService;

    @Resource
    private IOrdersManagerService ordersManagerService;

    @Resource
    private IOrdersServeManagerService ordersServeManagerService;

    @Override
    @Transactional
    public AfterSalesResDTO submit(AfterSalesApplyReqDTO reqDTO, CurrentUserInfo currentUser) {
        if (!Objects.equals(UserType.C_USER, currentUser.getUserType())) {
            throw new ForbiddenOperationException("Only consumer can submit after-sales");
        }
        Orders orders = ordersManagerService.queryById(reqDTO.getOrdersId());
        if (orders == null || !Objects.equals(orders.getUserId(), currentUser.getId())) {
            throw new ForbiddenOperationException("Order does not belong to current user");
        }
        if (!Objects.equals(OrderStatusEnum.FINISHED.getStatus(), orders.getOrdersStatus())) {
            throw new ForbiddenOperationException("Only finished orders can submit after-sales");
        }
        long unfinishedCount = lambdaQuery()
                .eq(AfterSales::getOrdersId, reqDTO.getOrdersId())
                .eq(AfterSales::getIsDeleted, 0)
                .in(AfterSales::getStatus, AfterSalesStatusEnum.unfinishedStatuses())
                .count();
        if (unfinishedCount > 0) {
            throw new BadRequestException("After-sales already exists");
        }

        OrdersServe ordersServe = ordersServeManagerService.queryById(reqDTO.getOrdersId());
        AfterSales afterSales = BeanUtil.toBean(reqDTO, AfterSales.class);
        afterSales.setUserId(currentUser.getId());
        afterSales.setStatus(AfterSalesStatusEnum.WAIT_AUDIT.getStatus());
        afterSales.setRefundRequired(ObjectUtil.defaultIfNull(reqDTO.getRefundRequired(), 0));
        afterSales.setImages(CollUtils.isEmpty(reqDTO.getImages()) ? null : JSONUtil.toJsonStr(reqDTO.getImages()));
        afterSales.setIsDeleted(0);
        afterSales.setCreateTime(LocalDateTime.now());
        afterSales.setUpdateTime(LocalDateTime.now());
        if (ordersServe != null) {
            afterSales.setServeProviderId(ordersServe.getServeProviderId());
            afterSales.setServeProviderType(ordersServe.getServeProviderType());
        }
        save(afterSales);
        addRecord(afterSales.getId(), currentUser, null, afterSales.getStatus(), "SUBMIT", afterSales.getReason());
        return operationDetail(afterSales.getId());
    }

    @Override
    public PageResult<AfterSalesResDTO> consumerPage(AfterSalesPageQueryReqDTO queryReqDTO, Long userId) {
        queryReqDTO.setUserId(userId);
        return queryPage(queryReqDTO);
    }

    @Override
    public PageResult<AfterSalesResDTO> operationPage(AfterSalesPageQueryReqDTO queryReqDTO) {
        return queryPage(queryReqDTO);
    }

    @Override
    public AfterSalesResDTO consumerDetail(Long id, Long userId) {
        AfterSales afterSales = getById(id);
        if (afterSales == null || !Objects.equals(afterSales.getUserId(), userId)) {
            throw new ForbiddenOperationException("After-sales does not belong to current user");
        }
        return toDetail(afterSales);
    }

    @Override
    public AfterSalesResDTO operationDetail(Long id) {
        AfterSales afterSales = getById(id);
        if (afterSales == null) {
            throw new BadRequestException("After-sales not found");
        }
        return toDetail(afterSales);
    }

    @Override
    @Transactional
    public void audit(Long id, AfterSalesAuditReqDTO reqDTO, CurrentUserInfo currentUser) {
        AfterSales afterSales = getById(id);
        if (afterSales == null) {
            throw new BadRequestException("After-sales not found");
        }
        if (!Objects.equals(afterSales.getStatus(), AfterSalesStatusEnum.WAIT_AUDIT.getStatus())) {
            throw new ForbiddenOperationException("After-sales cannot be audited repeatedly");
        }

        Integer fromStatus = afterSales.getStatus();
        Integer toStatus;
        if (!Boolean.TRUE.equals(reqDTO.getApproved())) {
            toStatus = AfterSalesStatusEnum.REJECTED.getStatus();
            updateAuditInfo(afterSales, reqDTO, currentUser, toStatus);
            addRecord(id, currentUser, fromStatus, toStatus, "AUDIT", reqDTO.getAuditRemark());
            return;
        }

        boolean needRefund = Objects.equals(reqDTO.getRefundRequired(), YES)
                || Objects.equals(afterSales.getRefundRequired(), YES);
        if (needRefund) {
            Orders orders = ordersManagerService.queryById(afterSales.getOrdersId());
            OrderCancelDTO orderCancelDTO = BeanUtil.toBean(orders, OrderCancelDTO.class);
            orderCancelDTO.setCurrentUserId(currentUser.getId());
            orderCancelDTO.setCurrentUserName(currentUser.getName());
            orderCancelDTO.setCurrentUserType(UserType.OPERATION);
            orderCancelDTO.setCancelReason(StrUtil.blankToDefault(reqDTO.getAuditRemark(), afterSales.getReason()));
            ordersManagerService.cancel(orderCancelDTO);

            Orders updatedOrders = ordersManagerService.queryById(afterSales.getOrdersId());
            afterSales.setRefundRequired(YES);
            afterSales.setRefundAmount(ObjectUtil.defaultIfNull(reqDTO.getRefundAmount(), updatedOrders.getRealPayAmount()));
            afterSales.setRefundNo(updatedOrders.getRefundNo());
            afterSales.setRefundStatus(updatedOrders.getRefundStatus());
            toStatus = Objects.equals(updatedOrders.getRefundStatus(), OrderRefundStatusEnum.REFUND_SUCCESS.getStatus())
                    ? AfterSalesStatusEnum.FINISHED.getStatus()
                    : AfterSalesStatusEnum.REFUNDING.getStatus();
            afterSales.setFinishTime(Objects.equals(toStatus, AfterSalesStatusEnum.FINISHED.getStatus()) ? LocalDateTime.now() : null);
        } else {
            afterSales.setRefundRequired(0);
            afterSales.setRefundAmount(reqDTO.getRefundAmount());
            toStatus = AfterSalesStatusEnum.FINISHED.getStatus();
            afterSales.setFinishTime(LocalDateTime.now());
        }

        updateAuditInfo(afterSales, reqDTO, currentUser, toStatus);
        addRecord(id, currentUser, fromStatus, toStatus, "AUDIT", reqDTO.getAuditRemark());
    }

    @Override
    @Transactional
    public void remark(Long id, String content, CurrentUserInfo currentUser) {
        AfterSales afterSales = getById(id);
        if (afterSales == null) {
            throw new BadRequestException("After-sales not found");
        }
        addRecord(id, currentUser, afterSales.getStatus(), afterSales.getStatus(), "REMARK", content);
    }

    @Override
    public AfterSalesResDTO latestByOrdersId(Long ordersId) {
        AfterSales afterSales = lambdaQuery()
                .eq(AfterSales::getOrdersId, ordersId)
                .eq(AfterSales::getIsDeleted, 0)
                .orderByDesc(AfterSales::getCreateTime)
                .last("limit 1")
                .one();
        return afterSales == null ? null : toDetail(afterSales);
    }

    private PageResult<AfterSalesResDTO> queryPage(AfterSalesPageQueryReqDTO queryReqDTO) {
        Page<AfterSales> page = PageUtils.parsePageQuery(queryReqDTO, AfterSales.class);
        LambdaQueryWrapper<AfterSales> wrapper = Wrappers.<AfterSales>lambdaQuery()
                .eq(AfterSales::getIsDeleted, 0)
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getId()), AfterSales::getId, queryReqDTO.getId())
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getOrdersId()), AfterSales::getOrdersId, queryReqDTO.getOrdersId())
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getUserId()), AfterSales::getUserId, queryReqDTO.getUserId())
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getAfterSalesType()), AfterSales::getAfterSalesType, queryReqDTO.getAfterSalesType())
                .eq(ObjectUtil.isNotEmpty(queryReqDTO.getStatus()), AfterSales::getStatus, queryReqDTO.getStatus())
                .between(ObjectUtil.isAllNotEmpty(queryReqDTO.getMinCreateTime(), queryReqDTO.getMaxCreateTime()),
                        AfterSales::getCreateTime, queryReqDTO.getMinCreateTime(), queryReqDTO.getMaxCreateTime())
                .orderByDesc(AfterSales::getCreateTime);
        Page<AfterSales> result = page(page, wrapper);
        List<AfterSalesResDTO> list = enrich(result.getRecords());
        return PageResult.<AfterSalesResDTO>builder()
                .total(result.getTotal())
                .pages(result.getPages())
                .list(list)
                .build();
    }

    private AfterSalesResDTO toDetail(AfterSales afterSales) {
        AfterSalesResDTO dto = CollUtils.getFirst(enrich(Collections.singletonList(afterSales)));
        List<AfterSalesRecord> records = afterSalesRecordService.lambdaQuery()
                .eq(AfterSalesRecord::getAfterSalesId, afterSales.getId())
                .orderByAsc(AfterSalesRecord::getCreateTime)
                .list();
        dto.setRecords(BeanUtil.copyToList(records, AfterSalesRecordResDTO.class));
        return dto;
    }

    private List<AfterSalesResDTO> enrich(List<AfterSales> afterSalesList) {
        if (CollUtils.isEmpty(afterSalesList)) {
            return Collections.emptyList();
        }
        List<Long> orderIds = afterSalesList.stream().map(AfterSales::getOrdersId).collect(Collectors.toList());
        Map<Long, Orders> ordersMap = ordersManagerService.batchQuery(orderIds)
                .stream()
                .collect(Collectors.toMap(Orders::getId, orders -> orders, (left, right) -> left));
        return afterSalesList.stream().map(afterSales -> {
            AfterSalesResDTO dto = BeanUtil.toBean(afterSales, AfterSalesResDTO.class);
            Orders orders = ordersMap.get(afterSales.getOrdersId());
            if (orders != null) {
                dto.setContactsName(orders.getContactsName());
                dto.setContactsPhone(orders.getContactsPhone());
                dto.setServeItemName(orders.getServeItemName());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private void updateAuditInfo(AfterSales afterSales, AfterSalesAuditReqDTO reqDTO,
                                 CurrentUserInfo currentUser, Integer status) {
        afterSales.setStatus(status);
        afterSales.setAuditUserId(currentUser.getId());
        afterSales.setAuditUserName(currentUser.getName());
        afterSales.setAuditRemark(reqDTO.getAuditRemark());
        afterSales.setAuditTime(LocalDateTime.now());
        afterSales.setUpdateTime(LocalDateTime.now());
        updateById(afterSales);
    }

    private void addRecord(Long afterSalesId, CurrentUserInfo currentUser, Integer fromStatus,
                           Integer toStatus, String action, String content) {
        AfterSalesRecord record = new AfterSalesRecord()
                .setAfterSalesId(afterSalesId)
                .setOperatorId(currentUser == null ? null : currentUser.getId())
                .setOperatorName(currentUser == null ? null : currentUser.getName())
                .setOperatorType(currentUser == null ? UserType.SYSTEM : currentUser.getUserType())
                .setFromStatus(fromStatus)
                .setToStatus(toStatus)
                .setAction(action)
                .setContent(content)
                .setCreateTime(LocalDateTime.now());
        afterSalesRecordService.save(record);
    }
}
