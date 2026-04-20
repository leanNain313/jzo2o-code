package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("钱包账单响应")
public class WalletBillResDTO {

    @ApiModelProperty("账单id")
    private Long id;

    @ApiModelProperty("钱包id")
    private Long walletId;

    @ApiModelProperty("明细类型：0-收入，1-退款，2-提现")
    private Integer type;

    @ApiModelProperty("服务单id")
    private Long serviceOrderId;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("交易时间")
    private LocalDateTime transactionTime;

    @ApiModelProperty("描述")
    private String description;
}
