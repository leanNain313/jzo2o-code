package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发送消息返回体
 */
@Data
@ApiModel("发送消息返回体")
public class ChatMessageSendResDTO {

    @ApiModelProperty("消息id")
    private Long messageId;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("发送时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("消息类型（1-文本，2-图片）")
    private Integer msgType;

    @ApiModelProperty("角色（1-用户，2-服务人员）")
    private Integer role;
}
