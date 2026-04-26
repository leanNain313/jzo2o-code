package com.jzo2o.orders.base.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("orders_after_sales_record")
public class AfterSalesRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long afterSalesId;

    private Long operatorId;

    private String operatorName;

    private Integer operatorType;

    private Integer fromStatus;

    private Integer toStatus;

    private String action;

    private String content;

    private LocalDateTime createTime;
}
