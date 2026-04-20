package com.jzo2o.customer.model.dto.request;

import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** 用户端按 walletId 查本人申请；运营端可按状态筛选全站申请。 */
@Data
@ApiModel("提现申请分页查询请求")
public class WithdrawApplyPageQueryReqDTO extends PageQueryDTO {

    @ApiModelProperty("钱包id")
    private Long walletId;

    @ApiModelProperty("申请状态：0-申请中，1-打款中，2-提现成功，3-提现失败")
    private Integer status;
}
