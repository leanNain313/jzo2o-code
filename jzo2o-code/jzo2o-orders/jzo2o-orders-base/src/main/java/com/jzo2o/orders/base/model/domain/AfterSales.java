package com.jzo2o.orders.base.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("orders_after_sales")
public class AfterSales implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long ordersId;

    private Long userId;

    private Long serveProviderId;

    private Integer serveProviderType;

    private Integer afterSalesType;

    private Integer status;

    private String reason;

    private String description;

    private String images;

    private Integer refundRequired;

    private BigDecimal refundAmount;

    private Long refundNo;

    private Integer refundStatus;

    private Long auditUserId;

    private String auditUserName;

    private String auditRemark;

    private LocalDateTime auditTime;

    private LocalDateTime finishTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
