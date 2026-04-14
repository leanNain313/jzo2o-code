package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Create comment request.
 */
@Data
@ApiModel("Create order comment request")
public class OrderCommentCreateReqDTO {

    @NotNull(message = "orderId cannot be null")
    @ApiModelProperty(value = "Order id", required = true)
    private Long orderId;

    @NotNull(message = "serviceScore cannot be null")
    @Min(value = 1, message = "serviceScore must be >= 1")
    @Max(value = 5, message = "serviceScore must be <= 5")
    @ApiModelProperty(value = "Service score, range 1~5", required = true)
    private Integer serviceScore;

    @ApiModelProperty(value = "Comment image list")
    private List<String> imageList;

    @Size(max = 2000, message = "content length cannot exceed 2000")
    @ApiModelProperty(value = "Comment content")
    private String content;

    @NotNull(message = "serveItemId cannot be null")
    @ApiModelProperty(value = "Serve item id", required = true)
    private Long serveItemId;
}