package com.jzo2o.api.customer.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 钱包入账请求：由订单服务在支付成功后组装，经 Feign 调用 customer 内部接口。
 */
@Data
public class WalletIncomeReqDTO {
    /** 服务人员用户 ID，与钱包表 user_id 对应 */
    private Long userId;
    /** 用于懒创建钱包时的展示名 */
    private String userName;
    /** 服务订单/业务订单主键，用于账单幂等与对账 */
    private Long serviceOrderId;
    /** 入账金额，一般为订单实付金额 */
    private BigDecimal amount;
    /** 账单摘要说明 */
    private String description;
}
