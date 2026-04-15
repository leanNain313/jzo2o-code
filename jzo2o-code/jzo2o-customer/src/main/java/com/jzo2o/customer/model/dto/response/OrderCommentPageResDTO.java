package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论分页查询返回体
 */
@Data
@ApiModel("评论分页查询返回体")
public class OrderCommentPageResDTO {

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论人ID")
    private Long userId;

    @ApiModelProperty("评论人名称")
    private String userName;

    @ApiModelProperty("评论人头像URL")
    private String userAvatar;

    @ApiModelProperty("服务评分")
    private Integer serviceScore;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("评价图片列表（JSON数组）")
    private List<String> imageList;

    @ApiModelProperty("订单ID（主键）")
    private Long orderId;
}
