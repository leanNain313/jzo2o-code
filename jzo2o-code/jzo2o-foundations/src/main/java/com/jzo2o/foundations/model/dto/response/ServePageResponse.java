package com.jzo2o.foundations.model.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServePageResponse {


    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 服务id
     */
    @ApiModelProperty("服务项id")
    private Long serveItemId;

    /**
     * 服务项名称
     */
    @ApiModelProperty("服务项名称")
    private String serveItemName;

    /**
     * 服务项图片
     */
    @ApiModelProperty("服务项图片")
    private String serveItemImg;

    /**
     * 服务单位
     */
    @ApiModelProperty("服务单位")
    private Integer unit;

    /**
     * 价格
     */
    @ApiModelProperty("价格")
    private BigDecimal price;

    /**
     * 服务详图
     */
    @ApiModelProperty("服务详图")
    private String detailImg;

    /**
     * 城市编码
     */
    @ApiModelProperty("城市编码")
    private String cityCode;


    /**
     * 服务描述
     */
    @ApiModelProperty("服务描述")
    private String description;

    /**
     * 下单次数
     */
    @ApiModelProperty("下单次数")
    private Long buyNum;
}
