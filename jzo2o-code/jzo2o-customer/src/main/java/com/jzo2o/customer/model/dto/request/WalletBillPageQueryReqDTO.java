package com.jzo2o.customer.model.dto.request;

import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户端由控制器自动写入 walletId；运营端可传任意钱包 ID 查流水。
 */
@Data
@ApiModel("钱包账单分页查询请求")
public class WalletBillPageQueryReqDTO extends PageQueryDTO {

    @ApiModelProperty("钱包id")
    private Long walletId;

    @ApiModelProperty("账单类型：0-收入，1-退款，2-提现")
    private Integer type;
}
