package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel("评价数量统计类")
public class CommentCount {

    @ApiModelProperty("已评价数量")
    private Integer commentCount;

    @ApiModelProperty("未评价数量")
    private Integer noCommentCount;
}
