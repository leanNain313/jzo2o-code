package com.jzo2o.customer.model.dto.request;

import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 根据服务项分页查询评论请求体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("根据服务项分页查询评论请求体")
public class OrderCommentPageReqDTO extends PageQueryDTO {

    @NotNull(message = "服务项id不能为空")
    @ApiModelProperty(value = "服务项id", required = true)
    private Long serveItemId;
}
