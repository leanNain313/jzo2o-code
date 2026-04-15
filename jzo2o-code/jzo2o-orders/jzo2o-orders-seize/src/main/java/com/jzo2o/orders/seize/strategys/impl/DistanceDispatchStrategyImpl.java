package com.jzo2o.orders.seize.strategys.impl;

import com.jzo2o.orders.seize.annotations.DispatchStrategy;
import com.jzo2o.orders.seize.enums.DispatchStrategyEnum;
import com.jzo2o.orders.seize.rules.IDispatchRule;
import com.jzo2o.orders.seize.rules.impl.AcceptNumDispatchRule;
import com.jzo2o.orders.seize.rules.impl.DistanceDispatchRule;
import org.springframework.stereotype.Component;

/**
 * 距离优先
 * 先根据距离计算得分，如果最高分大于1时按最少接单数计算得分，依次类推，算规则执行顺序如下：
 * 按距离计算->按最少接单数计算
 */
@Component("distanceDispatchStrategy")
@DispatchStrategy(DispatchStrategyEnum.DISTANCE)
public class DistanceDispatchStrategyImpl extends AbstractDispatchStrategyImpl {
    @Override
    protected IDispatchRule getRules() {
        // 最少接单规则
        IDispatchRule acceptNumDispatchRule = new AcceptNumDispatchRule(null);
        // 距离优先规则
        IDispatchRule distanceDispatchRule = new DistanceDispatchRule(acceptNumDispatchRule);
        return distanceDispatchRule;
//        // 最少接单规则
//        DefaultIDispatchRule leastAcceptOrderRule = new DefaultIDispatchRule(null, ComparatorUtils.nullToFirstComparing(ServeProviderDTO::getAcceptanceNum));
//        // 距离优先规则
//        DefaultIDispatchRule distanceDispatchRule = new DefaultIDispatchRule(leastAcceptOrderRule, ComparatorUtils.nullToLastComparing(ServeProviderDTO::getAcceptanceDistance));
//        return distanceDispatchRule;
    }

}
