package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 删除会话请求体
 */
@Data
@ApiModel("删除会话请求体")
public class ChatSessionDeleteReqDTO {

    @NotNull(message = "会话id不能为空")
    @ApiModelProperty(value = "会话id", required = true)
    private Long sessionId;
}
