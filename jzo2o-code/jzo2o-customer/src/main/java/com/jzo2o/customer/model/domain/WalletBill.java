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
 * 钱包流水：收入、退款、提现扣款等均落一条，便于用户端展示与运营对账。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wallet_bill")
public class WalletBill implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联 {@link Wallet#getId()} */
    private Long walletId;

    /** 类型，见 {@link com.jzo2o.customer.enums.WalletBillTypeEnum} */
    private Integer type;

    /** 关联订单 ID，非订单类流水可为空 */
    private Long serviceOrderId;

    /** 变动金额（正数） */
    private BigDecimal amount;

    /** 交易发生时间 */
    private LocalDateTime transactionTime;

    /** 说明文案 */
    private String description;
}
