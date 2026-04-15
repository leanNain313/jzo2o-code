package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 新增会话请求体
 */
@Data
@ApiModel("新增会话请求体")
public class ChatSessionCreateReqDTO {

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private Long userId;

    @NotNull(message = "服务人员id不能为空")
    @ApiModelProperty(value = "服务人员id", required = true)
    private Long staffId;
}
