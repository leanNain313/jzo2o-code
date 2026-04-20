package com.jzo2o.customer.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.WalletBillPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.WalletPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawApplyPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawAuditReqDTO;
import com.jzo2o.customer.model.dto.response.WalletBillResDTO;
import com.jzo2o.customer.model.dto.response.WalletResDTO;
import com.jzo2o.customer.model.dto.response.WithdrawApplyResDTO;
import com.jzo2o.customer.service.IWalletBillService;
import com.jzo2o.customer.service.IWalletService;
import com.jzo2o.customer.service.IWithdrawApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 运营端钱包：全量钱包与账单查询、提现申请列表及审核。
 */
@RestController("operationWalletController")
@RequestMapping("/operation/wallet")
@Api(tags = "运营端 - 钱包相关接口")
public class WalletController {
    @Resource
    private IWalletService walletService;
    @Resource
    private IWalletBillService walletBillService;
    @Resource
    private IWithdrawApplyService withdrawApplyService;

    @GetMapping("/page")
    @ApiOperation("分页查询钱包列表")
    public PageResult<WalletResDTO> pageQueryWallet(WalletPageQueryReqDTO reqDTO) {
        return walletService.pageQuery(reqDTO);
    }

    @GetMapping("/bill/page")
    @ApiOperation("分页查询钱包账单")
    public PageResult<WalletBillResDTO> pageQueryBill(WalletBillPageQueryReqDTO reqDTO) {
        return walletBillService.pageQuery(reqDTO);
    }

    @GetMapping("/withdraw/page")
    @ApiOperation("分页查询提现申请")
    public PageResult<WithdrawApplyResDTO> pageQueryWithdraw(WithdrawApplyPageQueryReqDTO reqDTO) {
        return withdrawApplyService.pageQuery(reqDTO);
    }

    @PostMapping("/withdraw/audit")
    @ApiOperation("审核提现申请")
    public void auditWithdraw(@RequestBody WithdrawAuditReqDTO reqDTO) {
        withdrawApplyService.audit(reqDTO);
    }
}
