package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话双方已读游标（用于前端同步）
 */
@Data
@ApiModel("会话已读状态")
public class ChatSessionReadStateResDTO {

    @ApiModelProperty("会话id")
    private Long sessionId;

    @ApiModelProperty("用户侧已读到最后一条消息的创建时间")
    private LocalDateTime userReadLastTime;

    @ApiModelProperty("服务人员侧已读到最后一条消息的创建时间")
    private LocalDateTime staffReadLastTime;

    @ApiModelProperty("未读消息条数（客服：用户发来未读；用户：客服发来未读）")
    private Long unreadCount;
}
