package com.jzo2o.orders.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.orders.base.model.domain.AfterSales;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesApplyReqDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesAuditReqDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesPageQueryReqDTO;
import com.jzo2o.orders.manager.model.dto.response.AfterSalesResDTO;

public interface IAfterSalesService extends IService<AfterSales> {

    AfterSalesResDTO submit(AfterSalesApplyReqDTO reqDTO, CurrentUserInfo currentUser);

    PageResult<AfterSalesResDTO> consumerPage(AfterSalesPageQueryReqDTO queryReqDTO, Long userId);

    PageResult<AfterSalesResDTO> operationPage(AfterSalesPageQueryReqDTO queryReqDTO);

    AfterSalesResDTO consumerDetail(Long id, Long userId);

    AfterSalesResDTO operationDetail(Long id);

    void audit(Long id, AfterSalesAuditReqDTO reqDTO, CurrentUserInfo currentUser);

    void remark(Long id, String content, CurrentUserInfo currentUser);

    AfterSalesResDTO latestByOrdersId(Long ordersId);
}
