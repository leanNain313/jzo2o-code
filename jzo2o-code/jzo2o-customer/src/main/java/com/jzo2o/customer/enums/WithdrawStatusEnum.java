package com.jzo2o.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提现申请状态机节点，与表 withdraw_apply.status 取值一致。
 */
@Getter
@AllArgsConstructor
public enum WithdrawStatusEnum {
    /** 已扣款，待运营审核 */
    APPLYING(0, "申请中"),
    /** 审核通过，线下/三方打款处理中 */
    PAYING(1, "打款中"),
    SUCCESS(2, "提现成功"),
    /** 含审核驳回、打款失败等，需配合 fail_reason */
    FAIL(3, "提现失败");

    private final Integer status;
    private final String desc;
}
