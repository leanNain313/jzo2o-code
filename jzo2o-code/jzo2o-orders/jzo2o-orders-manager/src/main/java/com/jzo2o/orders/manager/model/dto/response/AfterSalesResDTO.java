package com.jzo2o.orders.manager.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("售后单响应")
public class AfterSalesResDTO {

    @ApiModelProperty("售后单id")
    private Long id;

    @ApiModelProperty("订单id")
    private Long ordersId;

    @ApiModelProperty("申请用户id")
    private Long userId;

    @ApiModelProperty("用户姓名")
    private String contactsName;

    @ApiModelProperty("用户手机号")
    private String contactsPhone;

    @ApiModelProperty("服务项名称")
    private String serveItemName;

    @ApiModelProperty("服务提供方id")
    private Long serveProviderId;

    @ApiModelProperty("服务提供方类型")
    private Integer serveProviderType;

    @ApiModelProperty("售后类型")
    private Integer afterSalesType;

    @ApiModelProperty("售后状态")
    private Integer status;

    @ApiModelProperty("申请原因")
    private String reason;

    @ApiModelProperty("问题描述")
    private String description;

    @ApiModelProperty("图片地址JSON数组")
    private String images;

    @ApiModelProperty("是否需要退款")
    private Integer refundRequired;

    @ApiModelProperty("退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("支付服务退款单号")
    private Long refundNo;

    @ApiModelProperty("退款状态")
    private Integer refundStatus;

    @ApiModelProperty("审核人id")
    private Long auditUserId;

    @ApiModelProperty("审核人姓名")
    private String auditUserName;

    @ApiModelProperty("审核备注")
    private String auditRemark;

    @ApiModelProperty("审核时间")
    private LocalDateTime auditTime;

    @ApiModelProperty("完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("处理记录")
    private List<AfterSalesRecordResDTO> records;
}
