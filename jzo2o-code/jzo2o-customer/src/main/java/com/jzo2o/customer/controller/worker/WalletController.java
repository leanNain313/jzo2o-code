package com.jzo2o.customer.controller.worker;

import cn.hutool.core.bean.BeanUtil;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.Wallet;
import com.jzo2o.customer.model.dto.request.WalletBillPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawApplyPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawApplyReqDTO;
import com.jzo2o.customer.model.dto.response.WalletBillResDTO;
import com.jzo2o.customer.model.dto.response.WalletResDTO;
import com.jzo2o.customer.model.dto.response.WithdrawApplyResDTO;
import com.jzo2o.customer.service.IWalletBillService;
import com.jzo2o.customer.service.IWalletService;
import com.jzo2o.customer.service.IWithdrawApplyService;
import com.jzo2o.mvc.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 服务人员端钱包：查询当前钱包、提现申请、账单与提现记录分页（自动限定为当前登录用户的钱包）。
 */
@RestController("workerWalletController")
@RequestMapping("/worker/wallet")
@Api(tags = "服务端 - 钱包相关接口")
public class WalletController {
    @Resource
    private IWalletService walletService;
    @Resource
    private IWalletBillService walletBillService;
    @Resource
    private IWithdrawApplyService withdrawApplyService;

    @GetMapping("/current")
    @ApiOperation("查询当前用户钱包")
    public WalletResDTO currentWallet() {
        Wallet wallet = walletService.getOrInitWallet(UserContext.currentUserId(), UserContext.currentUser().getName());
        return BeanUtil.toBean(wallet, WalletResDTO.class);
    }

    @PostMapping("/withdraw/apply")
    @ApiOperation("发起提现申请")
    public void applyWithdraw(@RequestBody WithdrawApplyReqDTO reqDTO) {
        withdrawApplyService.apply(reqDTO, UserContext.currentUserId(), UserContext.currentUser().getName());
    }

    @GetMapping("/bill/page")
    @ApiOperation("分页查询我的钱包账单")
    public PageResult<WalletBillResDTO> pageQueryBill(WalletBillPageQueryReqDTO reqDTO) {
        Wallet wallet = walletService.getOrInitWallet(UserContext.currentUserId(), UserContext.currentUser().getName());
        reqDTO.setWalletId(wallet.getId());
        return walletBillService.pageQuery(reqDTO);
    }

    @GetMapping("/withdraw/page")
    @ApiOperation("分页查询我的提现申请")
    public PageResult<WithdrawApplyResDTO> pageQueryWithdraw(WithdrawApplyPageQueryReqDTO reqDTO) {
        Wallet wallet = walletService.getOrInitWallet(UserContext.currentUserId(), UserContext.currentUser().getName());
        reqDTO.setWalletId(wallet.getId());
        return withdrawApplyService.pageQuery(reqDTO);
    }
}
