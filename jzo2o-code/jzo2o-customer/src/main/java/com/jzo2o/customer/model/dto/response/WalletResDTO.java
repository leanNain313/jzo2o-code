package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("钱包响应")
public class WalletResDTO {

    @ApiModelProperty("钱包id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("钱包余额")
    private BigDecimal balance;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
