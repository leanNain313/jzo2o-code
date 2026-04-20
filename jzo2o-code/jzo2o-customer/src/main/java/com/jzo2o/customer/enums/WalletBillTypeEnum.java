package com.jzo2o.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 钱包账单类型，与表 wallet_bill.type 取值一致。
 */
@Getter
@AllArgsConstructor
public enum WalletBillTypeEnum {
    /** 入账类（含订单收入、提现失败退回等记为收入的场景） */
    INCOME(0, "收入"),
    REFUND(1, "退款"),
    /** 提现申请时产生的扣款流水 */
    WITHDRAW(2, "提现");

    private final Integer status;
    private final String desc;
}
