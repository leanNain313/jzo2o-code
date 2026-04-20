package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 运营审核入参。允许的目标状态由服务层校验：
 * 申请中→打款中/失败，打款中→成功/失败；目标为失败时 failReason 必填。
 */
@Data
@ApiModel("提现审核请求")
public class WithdrawAuditReqDTO {

    @ApiModelProperty("申请id")
    private Long id;

    @ApiModelProperty("目标状态：1-打款中（通过），2-成功，3-失败（驳回或打款失败）；不可直接写 0")
    private Integer status;

    @ApiModelProperty("失败原因，目标状态为 3（提现失败）时必填")
    private String failReason;
}
