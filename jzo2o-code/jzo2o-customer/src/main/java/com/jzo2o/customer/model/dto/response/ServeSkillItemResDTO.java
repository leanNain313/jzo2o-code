package com.jzo2o.customer.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("返回参数")
public class ServeSkillItemResDTO {

    @ApiModelProperty("服务项id")
    private Long serveItemId;

    @ApiModelProperty("服务项名称")
    private String serveItemName;

    @ApiModelProperty("是否选中")
    private Boolean isSelected;

    @ApiModelProperty("审核状态：null-未申请， 0- 审核中， 1- 不通过， 2-通过")
    private Integer auditStatus;

    @ApiModelProperty("服务图片")
    private String img;

    @ApiModelProperty("服务描述")
    private String description;

}