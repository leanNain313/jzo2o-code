package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("提现申请响应")
public class WithdrawApplyResDTO {

    @ApiModelProperty("申请id")
    private Long id;

    @ApiModelProperty("钱包id")
    private Long walletId;

    @ApiModelProperty("申请金额")
    private BigDecimal amount;

    @ApiModelProperty("银行账户")
    private String bankAccount;

    @ApiModelProperty("姓名")
    private String applicantName;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("申请状态：0-申请中，1-打款中，2-提现成功，3-提现失败")
    private Integer status;

    @ApiModelProperty("审核人id")
    private Long reviewerId;

    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty("失败原因")
    private String failReason;

    @ApiModelProperty("发起时间")
    private LocalDateTime applyTime;
}
