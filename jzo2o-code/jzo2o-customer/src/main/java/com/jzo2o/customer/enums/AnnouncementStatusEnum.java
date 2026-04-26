package com.jzo2o.customer.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AnnouncementStatusEnum {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下线");

    private final Integer status;
    private final String desc;

    AnnouncementStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static boolean valid(Integer status) {
        return Arrays.stream(values()).anyMatch(item -> item.status.equals(status));
    }
}
