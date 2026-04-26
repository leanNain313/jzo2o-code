package com.jzo2o.orders.base.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AfterSalesStatusEnum {

    WAIT_AUDIT(0, "待审核"),
    PROCESSING(1, "处理中"),
    APPROVED(2, "审核通过"),
    REJECTED(3, "审核驳回"),
    REFUNDING(4, "退款中"),
    FINISHED(5, "已完成"),
    CLOSED(6, "已关闭");

    private final Integer status;
    private final String desc;

    AfterSalesStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static List<Integer> unfinishedStatuses() {
        return Arrays.asList(
                WAIT_AUDIT.status,
                PROCESSING.status,
                APPROVED.status,
                REFUNDING.status
        );
    }
}
