package com.jzo2o.customer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.enums.WalletBillTypeEnum;
import com.jzo2o.customer.enums.WithdrawStatusEnum;
import com.jzo2o.customer.mapper.WithdrawApplyMapper;
import com.jzo2o.customer.model.domain.Wallet;
import com.jzo2o.customer.model.domain.WithdrawApply;
import com.jzo2o.customer.model.dto.request.WithdrawApplyPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawApplyReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawAuditReqDTO;
import com.jzo2o.customer.model.dto.response.WithdrawApplyResDTO;
import com.jzo2o.customer.service.IWalletBillService;
import com.jzo2o.customer.service.IWalletService;
import com.jzo2o.customer.service.IWithdrawApplyService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现申请与审核：申请时先扣余额再落单；审核仅允许
 * 申请中→打款中 / 申请中→失败、打款中→成功 / 打款中→失败；失败路径统一退款并记收入流水。
 */
@Service
public class WithdrawApplyServiceImpl extends ServiceImpl<WithdrawApplyMapper, WithdrawApply> implements IWithdrawApplyService {
    @Resource
    private IWalletService walletService;
    @Resource
    private IWalletBillService walletBillService;

    @Override
    @Transactional
    public void apply(WithdrawApplyReqDTO reqDTO, Long userId, String userName) {
        if (ObjectUtil.isEmpty(reqDTO.getAmount()) || reqDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("提现金额必须大于0");
        }
        Wallet wallet = walletService.getOrInitWallet(userId, userName);
        BigDecimal balance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
        if (reqDTO.getAmount().compareTo(balance) > 0) {
            throw new BadRequestException("提现金额不能超过当前余额");
        }
        walletService.deductBalance(wallet.getId(), reqDTO.getAmount());

        WithdrawApply withdrawApply = new WithdrawApply();
        withdrawApply.setWalletId(wallet.getId());
        withdrawApply.setApplicantName(reqDTO.getApplicantName());
        withdrawApply.setBankAccount(reqDTO.getBankAccount());
        withdrawApply.setBankName(reqDTO.getBankName());
        withdrawApply.setAmount(reqDTO.getAmount());
        withdrawApply.setStatus(WithdrawStatusEnum.APPLYING.getStatus());
        withdrawApply.setApplyTime(LocalDateTime.now());
        save(withdrawApply);

        walletBillService.addBill(wallet.getId(), WalletBillTypeEnum.WITHDRAW.getStatus(), reqDTO.getAmount(), null, "提现申请扣款");
    }

    @Override
    @Transactional
    public void audit(WithdrawAuditReqDTO reqDTO) {
        WithdrawApply withdrawApply = getById(reqDTO.getId());
        if (ObjectUtil.isEmpty(withdrawApply)) {
            throw new BadRequestException("提现申请不存在");
        }
        Integer targetStatus = reqDTO.getStatus();
        Integer currentStatus = withdrawApply.getStatus();
        CurrentUserInfo currentUserInfo = UserContext.currentUser();

        // 运营通过：申请中 → 打款中（资金已在申请时扣除，此处只更新状态与审核人）
        if (ObjectUtil.equal(currentStatus, WithdrawStatusEnum.APPLYING.getStatus())
                && ObjectUtil.equal(targetStatus, WithdrawStatusEnum.PAYING.getStatus())) {
            withdrawApply.setStatus(WithdrawStatusEnum.PAYING.getStatus());
            withdrawApply.setReviewerId(currentUserInfo.getId());
            withdrawApply.setReviewTime(LocalDateTime.now());
            updateById(withdrawApply);
            return;
        }

        // 申请阶段驳回：退回余额 + 收入流水说明
        if (ObjectUtil.equal(currentStatus, WithdrawStatusEnum.APPLYING.getStatus())
                && ObjectUtil.equal(targetStatus, WithdrawStatusEnum.FAIL.getStatus())) {
            rejectAndRefund(withdrawApply, reqDTO.getFailReason(), "提现失败退回");
            return;
        }

        // 打款完成
        if (ObjectUtil.equal(currentStatus, WithdrawStatusEnum.PAYING.getStatus())
                && ObjectUtil.equal(targetStatus, WithdrawStatusEnum.SUCCESS.getStatus())) {
            withdrawApply.setStatus(WithdrawStatusEnum.SUCCESS.getStatus());
            withdrawApply.setReviewTime(LocalDateTime.now());
            updateById(withdrawApply);
            return;
        }

        // 打款失败：退回余额 + 收入流水说明
        if (ObjectUtil.equal(currentStatus, WithdrawStatusEnum.PAYING.getStatus())
                && ObjectUtil.equal(targetStatus, WithdrawStatusEnum.FAIL.getStatus())) {
            rejectAndRefund(withdrawApply, reqDTO.getFailReason(), "打款失败退回");
            return;
        }

        throw new BadRequestException("不支持的状态流转");
    }

    @Override
    public PageResult<WithdrawApplyResDTO> pageQuery(WithdrawApplyPageQueryReqDTO reqDTO) {
        Page<WithdrawApply> page = PageUtils.parsePageQuery(reqDTO, WithdrawApply.class);
        LambdaQueryWrapper<WithdrawApply> queryWrapper = Wrappers.<WithdrawApply>lambdaQuery()
                .eq(reqDTO.getWalletId() != null, WithdrawApply::getWalletId, reqDTO.getWalletId())
                .eq(reqDTO.getStatus() != null, WithdrawApply::getStatus, reqDTO.getStatus());
        Page<WithdrawApply> result = baseMapper.selectPage(page, queryWrapper);
        return PageUtils.toPage(result, WithdrawApplyResDTO.class);
    }

    /**
     * 将申请置为失败、记录原因，并把申请金额退回钱包，同时记一条收入类型账单便于对账。
     */
    private void rejectAndRefund(WithdrawApply withdrawApply, String failReason, String billDescription) {
        if (ObjectUtil.isEmpty(failReason)) {
            throw new BadRequestException("失败原因不能为空");
        }
        withdrawApply.setStatus(WithdrawStatusEnum.FAIL.getStatus());
        withdrawApply.setFailReason(failReason);
        withdrawApply.setReviewTime(LocalDateTime.now());
        updateById(withdrawApply);

        walletService.addBalance(withdrawApply.getWalletId(), withdrawApply.getAmount());
        walletBillService.addBill(withdrawApply.getWalletId(), WalletBillTypeEnum.INCOME.getStatus(), withdrawApply.getAmount(), null, billDescription);
    }
}
