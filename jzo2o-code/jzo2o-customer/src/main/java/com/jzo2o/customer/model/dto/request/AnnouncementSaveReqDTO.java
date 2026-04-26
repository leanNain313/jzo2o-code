package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel("公告保存请求")
public class AnnouncementSaveReqDTO {

    @ApiModelProperty(value = "公告标题", required = true)
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题不能超过100个字符")
    private String title;

    @ApiModelProperty(value = "公告内容", required = true)
    @NotBlank(message = "公告内容不能为空")
    private String content;

    @ApiModelProperty(value = "公告类型：0全体，1客户，2服务人员", required = true)
    @NotNull(message = "公告类型不能为空")
    private Integer type;

    @ApiModelProperty("状态：0草稿，1已发布，2已下线")
    private Integer status;
}
