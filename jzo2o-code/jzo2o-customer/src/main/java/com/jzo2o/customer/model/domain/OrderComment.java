package com.jzo2o.customer.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order comment entity.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "order_comment", autoResultMap = true)
public class OrderComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Order id, also primary key.
     */
    @TableId(value = "order_id", type = IdType.INPUT)
    private Long orderId;

    /**
     * Serve item id.
     */
    @TableField("serve_item_id")
    private Long serveItemId;

    /**
     * Comment content.
     */
    private String content;

    /**
     * Comment user id.
     */
    @TableField("user_id")
    private Long userId;

    /**
     * Comment user name.
     */
    @TableField("user_name")
    private String userName;

    /**
     * Comment user avatar.
     */
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * Comment image list.
     */
    @TableField(value = "image_list", typeHandler = JacksonTypeHandler.class)
    private List<String> imageList;

    /**
     * Staff id.
     */
    @TableField("staff_id")
    private Long staffId;

    /**
     * Service score, range 1~5.
     */
    @TableField("service_score")
    private Integer serviceScore;

    /**
     * Create time.
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * Update time.
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}