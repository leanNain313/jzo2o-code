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
 * 根据订单发表评论请求体
 */
@Data
@ApiModel("根据订单发表评论请求体")
public class OrderCommentCreateReqDTO {

    @NotNull(message = "订单id不能为空")
    @ApiModelProperty(value = "订单id", required = true)
    private Long orderId;

    @NotNull(message = "评论分数不能为空")
    @Min(value = 1, message = "评论分数不能小于1")
    @Max(value = 5, message = "评论分数不能大于5")
    @ApiModelProperty(value = "评论分数（1~5）", required = true)
    private Integer serviceScore;

    @ApiModelProperty(value = "评论图片列表")
    private List<String> imageList;

    @Size(max = 2000, message = "评论内容长度不能超过2000")
    @ApiModelProperty(value = "评论内容")
    private String content;

    @NotNull(message = "服务项id不能为空")
    @ApiModelProperty(value = "服务项id", required = true)
    private Long serveItemId;

    @NotNull(message = "服务id不能为空")
    @ApiModelProperty(value = "服务id",required = true)
    private Long serveId;
}
