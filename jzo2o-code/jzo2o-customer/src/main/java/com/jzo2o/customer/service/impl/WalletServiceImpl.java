package com.jzo2o.customer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.mapper.WalletMapper;
import com.jzo2o.customer.model.domain.Wallet;
import com.jzo2o.customer.model.domain.WalletBill;
import com.jzo2o.customer.model.dto.request.WalletPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.WalletResDTO;
import com.jzo2o.customer.enums.WalletBillTypeEnum;
import com.jzo2o.customer.service.IWalletBillService;
import com.jzo2o.customer.service.IWalletService;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 钱包业务实现：余额变更通过 SQL 表达式原子更新；订单入账通过「收入+订单号」账单去重实现幂等。
 */
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements IWalletService {
    @javax.annotation.Resource
    private IWalletBillService walletBillService;
    @Override
    @Transactional
    public Wallet getOrInitWallet(Long userId, String userName) {
        Wallet wallet = getByUserId(userId);
        if (ObjectUtil.isNotNull(wallet)) {
            return wallet;
        }
        Wallet initWallet = new Wallet();
        initWallet.setUserId(userId);
        initWallet.setUserName(userName);
        initWallet.setBalance(BigDecimal.ZERO);
        save(initWallet);
        return getByUserId(userId);
    }

    @Override
    public Wallet getByUserId(Long userId) {
        LambdaQueryWrapper<Wallet> queryWrapper = Wrappers.<Wallet>lambdaQuery()
                .eq(Wallet::getUserId, userId)
                .last("limit 1");
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void deductBalance(Long walletId, BigDecimal amount) {
        // 条件更新：仅当当前余额 >= 扣减额时执行，否则行数为 0，表示余额不足或并发竞争
        LambdaUpdateWrapper<Wallet> updateWrapper = Wrappers.<Wallet>lambdaUpdate()
                .eq(Wallet::getId, walletId)
                .ge(Wallet::getBalance, amount)
                .setSql("balance = balance - " + amount);
        boolean updated = update(updateWrapper);
        if (!updated) {
            throw new BadRequestException("钱包余额不足");
        }
    }

    @Override
    @Transactional
    public void addBalance(Long walletId, BigDecimal amount) {
        LambdaUpdateWrapper<Wallet> updateWrapper = Wrappers.<Wallet>lambdaUpdate()
                .eq(Wallet::getId, walletId)
                .setSql("balance = balance + " + amount);
        update(updateWrapper);
    }

    @Override
    public PageResult<WalletResDTO> pageQuery(WalletPageQueryReqDTO reqDTO) {
        Page<Wallet> page = PageUtils.parsePageQuery(reqDTO, Wallet.class);
        LambdaQueryWrapper<Wallet> queryWrapper = Wrappers.<Wallet>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(reqDTO.getUserId()), Wallet::getUserId, reqDTO.getUserId())
                .like(ObjectUtil.isNotEmpty(reqDTO.getUserName()), Wallet::getUserName, reqDTO.getUserName());
        Page<Wallet> result = baseMapper.selectPage(page, queryWrapper);
        return PageUtils.toPage(result, WalletResDTO.class);
    }

    @Override
    @Transactional
    public void incomeByOrder(Long userId, String userName, Long serviceOrderId, BigDecimal amount, String description) {
        Wallet wallet = getOrInitWallet(userId, userName);
        // 幂等：同一钱包下「收入」类型且同一服务订单号已存在账单则视为已入账，直接返回
        long exists = walletBillService.lambdaQuery()
                .eq(WalletBill::getWalletId, wallet.getId())
                .eq(WalletBill::getType, WalletBillTypeEnum.INCOME.getStatus())
                .eq(WalletBill::getServiceOrderId, serviceOrderId)
                .count();
        if (exists > 0) {
            return;
        }
        addBalance(wallet.getId(), amount);
        walletBillService.addBill(wallet.getId(), WalletBillTypeEnum.INCOME.getStatus(), amount, serviceOrderId, description);
    }
}
