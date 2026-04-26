package com.jzo2o.orders.manager.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("售后审核请求")
public class AfterSalesAuditReqDTO {

    @NotNull
    @ApiModelProperty("是否通过")
    private Boolean approved;

    @ApiModelProperty("是否需要退款：0否，1是")
    private Integer refundRequired;

    @ApiModelProperty("审核退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("审核备注")
    private String auditRemark;
}
