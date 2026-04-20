package com.jzo2o.customer.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包主表：每个业务用户（此处为服务人员）一条记录，余额以 BigDecimal 存储。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wallet")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户 ID，与业务侧服务人员主键一致 */
    private Long userId;

    /** 用户姓名快照 */
    private String userName;

    /** 当前可用余额 */
    private BigDecimal balance;

    /** 最后更新时间（若表无触发器需业务侧维护） */
    private LocalDateTime updateTime;
}
