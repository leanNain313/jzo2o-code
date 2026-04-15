package com.jzo2o.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 服务技能表
 * </p>
 *
 * @author itcast
 * @since 2023-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("serve_skill")
public class ServeSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 服务人员/机构id
     */
    @ApiModelProperty(value = "服务人员/机构id")
    private Long serveProviderId;

    /**
     * 类型，2：服务人员，3：服务机构
     */
    @ApiModelProperty(value = "类型，2：服务人员，3：服务机构")
    private Integer serveProviderType;

    /**
     * 服务类型id
     */
    @ApiModelProperty(value = "服务类型id")
    private Long serveTypeId;

    /**
     * 服务类型名称
     */
    @ApiModelProperty(value = "服务类型名称")
    private String serveTypeName;

    /**
     * 服务项id
     */
    @ApiModelProperty(value = "服务项id")
    private Long serveItemId;

    /**
     * 服务项名称
     */
    @ApiModelProperty(value = "服务项名称")
    private String serveItemName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否已删除，0：未删除，1：已删除
     */
    @ApiModelProperty(value = "是否已删除，0：未删除，1：已删除")
    private Integer isDelete;

    /**
     * 服务人员姓名
     */
    @ApiModelProperty(value = "服务人员姓名")
    private String staffName;

    /**
     * 0 - 审核中， 1 - 审核不通过， 2 - 审核通过
     */
    @ApiModelProperty(value = "0 - 审核中， 1 - 审核不通过， 2 - 审核通过")
    private Integer auditStatus;

    /**
     * 审核描述
     */
    @ApiModelProperty(value = "审核描述")
    private String auditReason;
}
