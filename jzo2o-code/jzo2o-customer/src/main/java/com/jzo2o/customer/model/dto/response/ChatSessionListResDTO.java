package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话列表返回体
 */
@Data
@ApiModel("会话列表返回体")
public class ChatSessionListResDTO {

    @ApiModelProperty("会话id")
    private Long sessionId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户头像")
    private String userImage;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("服务人员id")
    private Long staffId;

    @ApiModelProperty("服务人员头像")
    private String staffImage;

    @ApiModelProperty("服务人员姓名")
    private String staffName;

    @ApiModelProperty("最后一条消息")
    private String lastMessage;

    @ApiModelProperty("最后消息时间")
    private LocalDateTime lastTime;
}
