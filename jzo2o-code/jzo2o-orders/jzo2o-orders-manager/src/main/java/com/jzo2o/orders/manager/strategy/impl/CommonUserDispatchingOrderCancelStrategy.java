package com.jzo2o.orders.manager.strategy.impl;

import cn.hutool.core.bean.BeanUtil;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.orders.base.enums.OrderRefundStatusEnum;
import com.jzo2o.orders.base.enums.OrderStatusEnum;
import com.jzo2o.orders.base.mapper.OrdersCanceledMapper;
import com.jzo2o.orders.base.mapper.OrdersRefundMapper;
import com.jzo2o.orders.base.model.domain.OrdersCanceled;
import com.jzo2o.orders.base.model.domain.OrdersRefund;
import com.jzo2o.orders.base.model.dto.OrderUpdateStatusDTO;
import com.jzo2o.orders.base.service.IOrdersCommonService;
import com.jzo2o.orders.manager.model.dto.OrderCancelDTO;
import com.jzo2o.orders.manager.service.IOrdersCanceledService;
import com.jzo2o.orders.manager.strategy.OrderCancelStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component("1:DISPATCHING")
public class CommonUserDispatchingOrderCancelStrategy implements OrderCancelStrategy {

    @Autowired
    private IOrdersCommonService iOrdersCommonService;

    @Autowired
    private OrdersCanceledMapper ordersCanceledMapper;

    @Autowired
    private OrdersRefundMapper ordersRefundMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(OrderCancelDTO orderCancelDTO) {
        // 1) 更新订单状态为已关闭
        // update orders set orders_status = 700 , refund_status = 1 where id = 订单id and orders_status = 100
        OrderUpdateStatusDTO orderUpdateStatusDTO = OrderUpdateStatusDTO.builder()
                .id(orderCancelDTO.getId())//订单id
                .originStatus(OrderStatusEnum.DISPATCHING.getStatus())//原始状态
                .targetStatus(OrderStatusEnum.CLOSED.getStatus())//目标状态
                .refundStatus(OrderRefundStatusEnum.APPLY_REFUND.getStatus()) //退款状态
                .build();
        Integer i = iOrdersCommonService.updateStatus(orderUpdateStatusDTO);
        if (i <= 0) {
            throw new ForbiddenOperationException("订单取消失败");
        }

        // 2) 保存取消订单记录
        OrdersCanceled ordersCanceled = new OrdersCanceled();
        ordersCanceled.setId(orderCancelDTO.getId());//订单id
        ordersCanceled.setCancellerId(orderCancelDTO.getCurrentUserId());//取消人
        ordersCanceled.setCancelerName(orderCancelDTO.getCurrentUserName());//取消人名称
        ordersCanceled.setCancellerType(orderCancelDTO.getCurrentUserType());//取消人类型，1：普通用户，4：运营人员
        ordersCanceled.setCancelReason(orderCancelDTO.getCancelReason());//取消原因
        ordersCanceled.setCancelTime(LocalDateTime.now());//取消时间
        ordersCanceledMapper.insert(ordersCanceled);

        //3) 保存待退款的记录
        OrdersRefund ordersRefund = BeanUtil.copyProperties(orderCancelDTO, OrdersRefund.class);
        ordersRefundMapper.insert(ordersRefund);
    }
}
