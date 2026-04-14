package com.jzo2o.customer.model.dto.request;

import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Page query request for comments by serve item id.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("Page query order comments by serve item")
public class OrderCommentPageReqDTO extends PageQueryDTO {

    @NotNull(message = "serveItemId cannot be null")
    @ApiModelProperty(value = "服务项id", required = true)
    private Long serveItemId;
}