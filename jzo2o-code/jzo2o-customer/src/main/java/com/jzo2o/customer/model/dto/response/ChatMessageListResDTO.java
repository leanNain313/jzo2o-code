package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息列表返回体
 */
@Data
@ApiModel("消息列表返回体")
public class ChatMessageListResDTO {

    @ApiModelProperty("消息id")
    private Long messageId;

    @ApiModelProperty("会话id")
    private Long sessionId;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("发送时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("消息类型（1-文本，2-图片）")
    private Integer msgType;

    @ApiModelProperty("角色（1-用户，2-服务人员）")
    private Integer role;

    @ApiModelProperty("创建人id")
    private Long creatorId;

    @ApiModelProperty("对方是否已读（仅「自己发出的消息」有值；客服看自己消息时表示用户是否已读）")
    private Boolean peerRead;
}
