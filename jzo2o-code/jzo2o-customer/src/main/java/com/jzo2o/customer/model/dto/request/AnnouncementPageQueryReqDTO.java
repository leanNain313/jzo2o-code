package com.jzo2o.customer.model.dto.request;

import com.jzo2o.common.model.dto.PageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("公告分页查询请求")
public class AnnouncementPageQueryReqDTO extends PageQueryDTO {

    @ApiModelProperty("公告标题")
    private String title;

    @ApiModelProperty("公告类型：0全体，1客户，2服务人员")
    private Integer type;

    @ApiModelProperty("状态：0草稿，1已发布，2已下线")
    private Integer status;

    @ApiModelProperty("最早发布时间")
    private LocalDateTime minPublishTime;

    @ApiModelProperty("最晚发布时间")
    private LocalDateTime maxPublishTime;
}
