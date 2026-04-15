package com.jzo2o.customer.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改用户请求封装类")
public class UpdateUserRequest {

    /**
     *
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long id;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickname;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

}
