package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("发起提现申请请求")
public class WithdrawApplyReqDTO {

    @ApiModelProperty("申请金额")
    private BigDecimal amount;

    @ApiModelProperty("银行账户")
    private String bankAccount;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("姓名")
    private String applicantName;
}
