package com.jzo2o.orders.manager.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.orders.base.enums.OrderPayStatusEnum;
import com.jzo2o.orders.base.enums.OrderStatusEnum;
import com.jzo2o.orders.base.mapper.OrdersMapper;
import com.jzo2o.orders.base.model.domain.Orders;
import com.jzo2o.orders.manager.model.dto.OrderCancelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OrderCancelStrategyManager {

    @Autowired
    private OrdersMapper ordersMapper;

    //key格式：userType+":"+orderStatusEnum，例：1：NO_PAY
    private Map<String, OrderCancelStrategy> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化
        strategyMap = SpringUtil.getBeansOfType(OrderCancelStrategy.class);
    }

    public void cancel(OrderCancelDTO orderCancelDTO) {
        //1. 根据订单id查询订单信息,如果订单不存在, 直接返回错误
        Orders orders = ordersMapper.selectById(orderCancelDTO.getId());
        if (ObjectUtil.isNull(orders)) {
            throw new ForbiddenOperationException("订单不存在");
        }
        BeanUtil.copyProperties(orders, orderCancelDTO);

        String key = orderCancelDTO.getCurrentUserType() + ":" + OrderStatusEnum.codeOf(orders.getOrdersStatus()).toString();
        OrderCancelStrategy strategy =  strategyMap.get(key);
        if (ObjectUtil.isEmpty(strategy)) {
            throw new ForbiddenOperationException("不被许可的操作");
        }

        //3. 执行策略对象的方法
        strategy.cancel(orderCancelDTO);
    }

}
