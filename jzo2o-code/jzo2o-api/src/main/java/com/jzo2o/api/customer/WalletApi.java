package com.jzo2o.api.customer;

import com.jzo2o.api.customer.dto.request.WalletIncomeReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 调用 customer 服务内部钱包接口，典型场景为订单支付成功后将实付金额记入服务人员钱包。
 */
@FeignClient(contextId = "jzo2o-customer", value = "jzo2o-customer", path = "/customer/inner/wallet", qualifiers = "customerWalletApi")
public interface WalletApi {

    /**
     * 按订单维度入账（服务端幂等），入参需包含服务人员用户 ID、订单 ID 与金额等。
     */
    @PostMapping("/income")
    void income(@RequestBody WalletIncomeReqDTO reqDTO);
}
