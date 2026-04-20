package com.jzo2o.orders.manager.model.dto.response;

import com.jzo2o.api.orders.dto.response.OrderSimpleResDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("返回参数")
@Builder
public class CommentPageDTO {

    @ApiModelProperty("订单列表")
    private List<OrderSimpleResDTO> orderSimpleResDTOList;

    @ApiModelProperty("以评价数量")
    private Integer commentCount;

    @ApiModelProperty("未评价数量")
    private Integer noCommentCount;

}
