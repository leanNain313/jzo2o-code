package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 消息滚动查询请求体
 */
@Data
@ApiModel("消息滚动查询请求体")
public class ChatMessageScrollQueryReqDTO {

    @NotNull(message = "会话id不能为空")
    @ApiModelProperty(value = "会话id", required = true)
    private Long sessionId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后一条消息时间，不传则查询最新")
    private LocalDateTime lastTime;
}
