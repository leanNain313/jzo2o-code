package com.jzo2o.orders.seize.strategys;


import com.jzo2o.orders.seize.model.dto.ServeProviderDTO;

import java.util.List;

public interface IDispatchStrategy {

    /**
     * 从服务人员/机构列表中获取高优先级别的一个，如果出现多个相同优先级随机获取一个
     *
     * @param serveProviderDTOS 服务人员/机构列表
     * @return
     */
    ServeProviderDTO getPrecedenceServeProvider(List<ServeProviderDTO> serveProviderDTOS);
}
