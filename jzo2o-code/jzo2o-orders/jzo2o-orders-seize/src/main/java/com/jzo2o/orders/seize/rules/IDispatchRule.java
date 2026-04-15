package com.jzo2o.orders.seize.rules;


import com.jzo2o.orders.seize.model.dto.ServeProviderDTO;

import java.util.List;

/**
 * 派单规则
 */
public interface IDispatchRule {

    /**
     * 根据派单规则过滤服务人员
     * @param serveProviderDTOS
     * @return
     */
    List<ServeProviderDTO> filter(List<ServeProviderDTO> serveProviderDTOS);

    /**
     * 获取下一级规则
     *
     * @return
     */
    IDispatchRule next();

}
