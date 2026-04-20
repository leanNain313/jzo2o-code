package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 会话滚动查询请求体
 */
@Data
@ApiModel("会话滚动查询请求体")
public class ChatSessionScrollQueryReqDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后一条消息时间，不传则查询最新")
    private LocalDateTime lastTime;

    @ApiModelProperty(value = "用户id（C 端传；与 staffId 二选一，需与当前登录人一致）")
    private Long userId;

    @ApiModelProperty(value = "服务人员id（服务人员端传；与 userId 二选一，需与当前登录人一致）")
    private Long staffId;
}
