package com.jzo2o.customer.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单评论实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "order_comment", autoResultMap = true)
public class OrderComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID（主键）
     */
    @ApiModelProperty("订单ID（主键）")
    @TableId(value = "order_id", type = IdType.INPUT)
    private Long orderId;

    /**
     * 服务项ID
     */
    @ApiModelProperty("服务项ID")
    @TableField("serve_item_id")
    private Long serveItemId;

    /**
     * 服务id
     */
    @ApiModelProperty("服务id")
    private Long serveId;

    /**
     * 评论内容
     */
    @ApiModelProperty("评论内容")
    private String content;

    /**
     * 评论人ID
     */
    @ApiModelProperty("评论人ID")
    @TableField("user_id")
    private Long userId;

    /**
     * 评论人名称
     */
    @ApiModelProperty("评论人名称")
    @TableField("user_name")
    private String userName;

    /**
     * 评论人头像
     */
    @ApiModelProperty("评论人头像")
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * 评价图片列表
     */
    @ApiModelProperty("评价图片列表")
    @TableField(value = "image_list", typeHandler = JacksonTypeHandler.class)
    private List<String> imageList;

    /**
     * 服务人员ID
     */
    @ApiModelProperty("服务人员ID")
    @TableField("staff_id")
    private Long staffId;

    /**
     * 服务评分（1~5）
     */
    @ApiModelProperty("服务评分（1~5）")
    @TableField("service_score")
    private Integer serviceScore;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
