package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 根据订单id删除评论请求体
 */
@Data
@ApiModel("根据订单id删除评论请求体")
public class OrderCommentDeleteReqDTO {

    @NotNull(message = "评论id不能为空")
    @ApiModelProperty(value = "评论id（即order_id主键）", required = true)
    private Long commentId;
}
