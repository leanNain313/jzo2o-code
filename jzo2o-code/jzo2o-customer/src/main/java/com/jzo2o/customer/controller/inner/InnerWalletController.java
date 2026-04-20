package com.jzo2o.customer.controller.inner;

import com.jzo2o.api.customer.WalletApi;
import com.jzo2o.api.customer.dto.request.WalletIncomeReqDTO;
import com.jzo2o.customer.service.IWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 内部 Feign 入口：供订单等模块在支付成功后调用，将收入记入服务人员钱包（幂等由服务层保证）。
 */
@RestController
@RequestMapping("/inner/wallet")
@Api(tags = "内部接口 - 钱包相关接口")
public class InnerWalletController implements WalletApi {
    @Resource
    private IWalletService walletService;

    @Override
    @PostMapping("/income")
    @ApiOperation("钱包收入入账")
    public void income(@RequestBody WalletIncomeReqDTO reqDTO) {
        walletService.incomeByOrder(reqDTO.getUserId(), reqDTO.getUserName(), reqDTO.getServiceOrderId(), reqDTO.getAmount(), reqDTO.getDescription());
    }
}
