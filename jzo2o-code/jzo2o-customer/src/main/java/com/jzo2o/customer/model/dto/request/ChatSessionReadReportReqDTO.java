package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 上报会话已读到某条消息的时间（当前登录用户为客服则更新 staffReadLastTime，为用户则更新 userReadLastTime）
 */
@Data
@ApiModel("会话已读上报请求体")
public class ChatSessionReadReportReqDTO {

    @NotNull(message = "会话id不能为空")
    @ApiModelProperty(value = "会话id", required = true)
    private Long sessionId;

    @NotNull(message = "已读到最后一条消息的创建时间不能为空")
    @ApiModelProperty(value = "已读到的最后一条消息的创建时间（支持 ISO-8601、yyyy-MM-dd HH:mm:ss，可含毫秒）", required = true)
    private LocalDateTime lastReadMessageCreatedAt;
}
