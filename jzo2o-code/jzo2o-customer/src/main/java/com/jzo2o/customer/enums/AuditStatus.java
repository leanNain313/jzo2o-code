package com.jzo2o.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AuditStatus {

    AUDITING(0,"审核中"),
    NO_PASS(1, "审核不通过"),
    AUDIT_PASS(2, "审核通过");

    private Integer status;

    private String description;


    /**
     * 判断是否包含该status
     */
    public static boolean contains(Integer status) {
        if (status == null) {
            return false;
        }
        for (AuditStatus auditStatus : AuditStatus.values()) {
            if (auditStatus.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    public static AuditStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (AuditStatus auditStatus : AuditStatus.values()) {
            if (auditStatus.getStatus().equals(status)) {
                return auditStatus;
            }
        }
        return null;
    }
}
