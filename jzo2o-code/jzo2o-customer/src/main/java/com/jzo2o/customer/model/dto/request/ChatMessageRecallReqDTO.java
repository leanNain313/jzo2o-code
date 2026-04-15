package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 撤回消息请求体
 */
@Data
@ApiModel("撤回消息请求体")
public class ChatMessageRecallReqDTO {

    @NotNull(message = "消息id不能为空")
    @ApiModelProperty(value = "消息id", required = true)
    private Long messageId;
}
