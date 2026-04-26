package com.jzo2o.foundations.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author itcast
 */
@Data
@ApiModel("运营人员新增模型")
public class OperatorAddDTO {
    @NotBlank(message = "账号不能为空")
    @ApiModelProperty("账号")
    private String username;

    @NotBlank(message = "运营人员姓名不能为空")
    @ApiModelProperty("运营人员姓名")
    private String name;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty("密码")
    private String password;
}
