package com.jzo2o.orders.manager.model.dto.request;

import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel("售后分页查询请求")
public class AfterSalesPageQueryReqDTO extends PageQueryDTO {

    @ApiModelProperty("售后单id")
    private Long id;

    @ApiModelProperty("订单id")
    private Long ordersId;

    @ApiModelProperty("申请用户id")
    private Long userId;

    @ApiModelProperty("用户手机号")
    private String contactsPhone;

    @ApiModelProperty("售后类型")
    private Integer afterSalesType;

    @ApiModelProperty("处理状态")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最小创建时间")
    private LocalDateTime minCreateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最大创建时间")
    private LocalDateTime maxCreateTime;
}
