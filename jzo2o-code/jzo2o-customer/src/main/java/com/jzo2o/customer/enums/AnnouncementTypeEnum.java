package com.jzo2o.customer.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AnnouncementTypeEnum {

    ALL(0, "全体公告"),
    CONSUMER(1, "客户公告"),
    WORKER(2, "服务人员公告");

    private final Integer type;
    private final String desc;

    AnnouncementTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean valid(Integer type) {
        return Arrays.stream(values()).anyMatch(item -> item.type.equals(type));
    }

    public static List<Integer> consumerVisibleTypes() {
        return Arrays.asList(ALL.type, CONSUMER.type);
    }

    public static List<Integer> workerVisibleTypes() {
        return Arrays.asList(ALL.type, WORKER.type);
    }
}
