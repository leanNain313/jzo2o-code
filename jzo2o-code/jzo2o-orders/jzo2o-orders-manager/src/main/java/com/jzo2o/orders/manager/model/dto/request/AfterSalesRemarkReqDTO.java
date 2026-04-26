package com.jzo2o.orders.manager.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("售后备注请求")
public class AfterSalesRemarkReqDTO {

    @NotBlank
    @ApiModelProperty("备注内容")
    private String content;
}
