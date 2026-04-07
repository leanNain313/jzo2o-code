package com.jzo2o.orders.manager.handler;

import cn.hutool.core.collection.CollUtil;
import com.jzo2o.api.trade.RefundRecordApi;
import com.jzo2o.common.constants.UserType;
import com.jzo2o.orders.base.enums.OrderPayStatusEnum;
import com.jzo2o.orders.base.enums.OrderRefundStatusEnum;
import com.jzo2o.orders.base.enums.OrderStatusEnum;
import com.jzo2o.orders.base.model.domain.Orders;
import com.jzo2o.orders.manager.model.dto.OrderCancelDTO;
import com.jzo2o.orders.manager.service.IOrdersManagerService;
import com.jzo2o.orders.manager.strategy.OrderCancelStrategyManager;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderHandler {

    @Autowired
    private OrderCancelStrategyManager orderCancelStrategyManager;

    @Autowired
    private IOrdersManagerService ordersManagerService;

    @Resource
    private RefundRecordApi refundRecordApi;



    /**
     * 取消超时订单
     */
    @XxlJob("cancelOverTimePayOrder")
    public void cancelOverTimePayOrder() {
        //1. 查询超时未支付的订单
        //select * from orders where orders_status = 0 and pay_status = 2 and create_time < 当前时间 - 15分钟
        List<Orders> list = ordersManagerService.lambdaQuery()
                .eq(Orders::getOrdersStatus, OrderStatusEnum.NO_PAY.getStatus())//orders_status = 0
                .eq(Orders::getPayStatus, OrderPayStatusEnum.NO_PAY.getStatus())//pay_status = 2
                .lt(Orders::getCreateTime, LocalDateTime.now().minusMinutes(15))//create_time < 当前时间 - 15分钟
                .last("limit 100")//限制每次最多查100条
                .list();
        if (CollUtil.isEmpty(list)){
            return;
        }

        //2. 遍历集合, 获取到每一笔订单
        for (Orders orders : list) {
            //然后去取消
            OrderCancelDTO orderCancelDTO = new OrderCancelDTO();
            orderCancelDTO.setId(orders.getId());//订单id
            orderCancelDTO.setCurrentUserId(0L);//当前用户id
            orderCancelDTO.setCurrentUserName("系统定时任务");//当前用户名称
            orderCancelDTO.setCurrentUserType(UserType.SYSTEM);//当前用户类型
            orderCancelDTO.setCancelReason("超时未支付");//取消原因
            orderCancelStrategyManager.cancel(orderCancelDTO);
        }
    }

    public void handleRefundOrders() {
        List<Orders> ordersList = ordersManagerService.lambdaQuery()
                .eq(Orders::getRefundStatus, OrderRefundStatusEnum.APPLY_REFUND.getStatus())
                .list();
        // 判断是否有订单需要退单
        if (CollUtil.isEmpty(ordersList)) {
            return;
        }
//        for (Orders orders : ordersList) {
//
//        }
//        refundRecordApi.refundTrading()
    }
}
