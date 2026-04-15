package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

@Data
@ApiModel("审核请求封装类")
public class AuditRequest {

    /**
     * 审核id
     */
    @ApiModelProperty(value = "审核id", required = true)
    private Long id;

    /**
     * 0 - 审核中， 1 - 审核不通过， 2 - 审核通过
     */
    @ApiModelProperty(value = "0 - 审核中， 1 - 审核不通过， 2 - 审核通过", required = true)
    private Integer auditStatus;

    /**
     * 审核原因
     */
    @ApiModelProperty(value = "审核描述", required = true)
    private String auditReason;

    /**
     * 服务人员id
     */
    @NonNull
    @ApiModelProperty(value = "服务人员id", required = true)
    private Long serveProviderId;

}
