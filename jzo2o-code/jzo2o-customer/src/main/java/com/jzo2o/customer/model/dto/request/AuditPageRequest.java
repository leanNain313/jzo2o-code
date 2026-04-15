package com.jzo2o.customer.model.dto.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditPageRequest extends PageQueryDTO {

    /**
     * 服务类型id
     */
    @ApiModelProperty("服务类型id")
    private Long serveTypeId;

    /**
     * 服务项id
     */
    @ApiModelProperty("服务项id")
    private Long serveItemId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否已删除，0：未删除，1：已删除
     */
    private Integer isDelete;

    /**
     * 服务人员姓名
     */
    @ApiModelProperty("服务人员姓名")
    private String staffName;

    /**
     * 0 - 审核中， 1 - 审核不通过， 2 - 审核通过
     */
    @ApiModelProperty("0 - 审核中， 1 - 审核不通过， 2 - 审核通过")
    private Integer auditStatus;

}
