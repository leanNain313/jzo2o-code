package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送消息请求体
 */
@Data
@ApiModel("发送消息请求体")
public class ChatMessageSendReqDTO {

    @NotNull(message = "会话id不能为空")
    @ApiModelProperty(value = "会话id", required = true)
    private Long sessionId;

    @NotNull(message = "发送角色不能为空")
    @Min(value = 1, message = "发送角色只能为1或2")
    @Max(value = 2, message = "发送角色只能为1或2")
    @ApiModelProperty(value = "发送角色（1-用户，2-服务人员）", required = true)
    private Integer role;

    @NotBlank(message = "消息内容不能为空")
    @ApiModelProperty(value = "消息内容", required = true)
    private String content;

    @NotNull(message = "消息类型不能为空")
    @Min(value = 1, message = "消息类型只能为1或2")
    @Max(value = 2, message = "消息类型只能为1或2")
    @ApiModelProperty(value = "消息类型（1-文本，2-图片）", required = true)
    private Integer msgType;
}
