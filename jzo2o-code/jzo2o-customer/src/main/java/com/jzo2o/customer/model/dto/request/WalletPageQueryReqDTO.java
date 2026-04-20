package com.jzo2o.customer.model.dto.request;

import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** 运营端：按用户 ID、姓名模糊筛选钱包列表。 */
@Data
@ApiModel("钱包分页查询请求")
public class WalletPageQueryReqDTO extends PageQueryDTO {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户姓名")
    private String userName;
}
