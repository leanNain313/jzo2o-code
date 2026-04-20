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
 * 提现申请单：提交时钱包已扣款；审核通过进入打款中直至成功；任意失败节点退回余额并记收入流水。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("withdraw_apply")
public class WithdrawApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联钱包 */
    private Long walletId;

    /** 收款人姓名 */
    private String applicantName;

    /** 银行账号 */
    private String bankAccount;

    /** 开户行名称 */
    private String bankName;

    /** 申请时间 */
    private LocalDateTime applyTime;

    /** 状态，见 {@link com.jzo2o.customer.enums.WithdrawStatusEnum} */
    private Integer status;

    /** 审核/打款失败原因 */
    private String failReason;

    /** 审核人用户 ID（通过至打款中时记录） */
    private Long reviewerId;

    /** 最近审核或状态变更时间 */
    private LocalDateTime reviewTime;

    /** 提现金额 */
    private BigDecimal amount;
}
