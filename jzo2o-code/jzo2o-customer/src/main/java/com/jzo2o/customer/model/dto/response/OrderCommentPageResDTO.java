package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment page response item.
 */
@Data
@ApiModel("Order comment page response")
public class OrderCommentPageResDTO {

    @ApiModelProperty("Comment content")
    private String content;

    @ApiModelProperty("Comment user id")
    private Long userId;

    @ApiModelProperty("Comment user name")
    private String userName;

    @ApiModelProperty("Comment user avatar")
    private String userAvatar;

    @ApiModelProperty("Service score")
    private Integer serviceScore;

    @ApiModelProperty("Create time")
    private LocalDateTime createdAt;

    @ApiModelProperty("Comment image list")
    private List<String> imageList;

    @ApiModelProperty("Order id (primary key)")
    private Long orderId;
}