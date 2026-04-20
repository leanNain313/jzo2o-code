package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.Wallet;
import com.jzo2o.customer.model.dto.request.WalletPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.WalletResDTO;

import java.math.BigDecimal;

/**
 * 钱包服务：维护用户钱包余额，支持运营端分页查询、订单支付后的入账（幂等）。
 */
public interface IWalletService extends IService<Wallet> {

    /**
     * 按用户查询钱包；若不存在则创建一条余额为 0 的钱包记录（懒初始化）。
     *
     * @param userId   用户 ID（服务人员端与钱包 user_id 一致）
     * @param userName 用户姓名，用于新建钱包时落库
     */
    Wallet getOrInitWallet(Long userId, String userName);

    /**
     * 根据用户 ID 查询钱包（最多一条，因表上有 uk_user_id）。
     */
    Wallet getByUserId(Long userId);

    /**
     * 扣减钱包余额。使用「当前余额大于等于扣减金额」条件更新，避免并发下超扣。
     *
     * @param walletId 钱包主键
     * @param amount   扣减金额，须大于 0
     */
    void deductBalance(Long walletId, BigDecimal amount);

    /**
     * 增加钱包余额（退款、入账、提现失败退回等场景）。
     */
    void addBalance(Long walletId, BigDecimal amount);

    /**
     * 运营端：分页查询钱包列表。
     */
    PageResult<WalletResDTO> pageQuery(WalletPageQueryReqDTO reqDTO);

    /**
     * 订单支付成功后的收入入账：加余额并写一条「收入」类型账单。
     * 同一订单（serviceOrderId）重复调用时应在实现内幂等，避免重复加款。
     */
    void incomeByOrder(Long userId, String userName, Long serviceOrderId, BigDecimal amount, String description);
}
