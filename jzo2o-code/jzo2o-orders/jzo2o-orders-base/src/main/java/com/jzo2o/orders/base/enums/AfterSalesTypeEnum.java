package com.jzo2o.orders.base.enums;

import lombok.Getter;

@Getter
public enum AfterSalesTypeEnum {

    REFUND(1, "退款"),
    COMPLAINT(2, "投诉"),
    SERVICE_QUALITY(3, "服务质量"),
    OTHER(4, "其他");

    private final Integer type;
    private final String desc;

    AfterSalesTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
