package com.jzo2o.orders.manager.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("售后处理记录")
public class AfterSalesRecordResDTO {

    @ApiModelProperty("记录id")
    private Long id;

    @ApiModelProperty("售后单id")
    private Long afterSalesId;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作人类型")
    private Integer operatorType;

    @ApiModelProperty("变更前状态")
    private Integer fromStatus;

    @ApiModelProperty("变更后状态")
    private Integer toStatus;

    @ApiModelProperty("操作动作")
    private String action;

    @ApiModelProperty("处理内容")
    private String content;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
