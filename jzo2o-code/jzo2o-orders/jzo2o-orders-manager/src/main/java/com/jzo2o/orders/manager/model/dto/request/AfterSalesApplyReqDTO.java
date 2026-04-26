package com.jzo2o.orders.manager.model.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("售后申请请求")
public class AfterSalesApplyReqDTO {

    @NotNull
    @ApiModelProperty("订单id")
    private Long ordersId;

    @NotNull
    @ApiModelProperty("售后类型：1退款，2投诉，3服务质量，4其他")
    private Integer afterSalesType;

    @NotBlank
    @ApiModelProperty("申请原因")
    private String reason;

    @ApiModelProperty("问题描述")
    private String description;

    @ApiModelProperty("图片地址列表")
    private List<String> images;

    @ApiModelProperty("是否申请退款：0否，1是")
    private Integer refundRequired;

    @ApiModelProperty("申请退款金额")
    private BigDecimal refundAmount;
}
