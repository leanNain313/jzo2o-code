package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Delete comment request.
 */
@Data
@ApiModel("Delete order comment request")
public class OrderCommentDeleteReqDTO {

    @NotNull(message = "commentId cannot be null")
    @ApiModelProperty(value = "Comment id (same as order_id primary key)", required = true)
    private Long commentId;
}
