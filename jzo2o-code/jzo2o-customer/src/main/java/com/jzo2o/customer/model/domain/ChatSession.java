package com.jzo2o.customer.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息会话列表实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_session")
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 服务人员ID
     */
    @TableField("staff_id")
    private Long staffId;

    /**
     * 用户头像
     */
    @TableField("user_image")
    private String userImage;

    /**
     * 服务人员头像
     */
    @TableField("staff_image")
    private String staffImage;

    /**
     * 服务人员姓名
     */
    @TableField("staff_name")
    private String staffName;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 最后一条消息
     */
    @TableField("last_message")
    private String lastMessage;

    /**
     * 最后消息时间
     */
    @TableField("last_time")
    private LocalDateTime lastTime;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
