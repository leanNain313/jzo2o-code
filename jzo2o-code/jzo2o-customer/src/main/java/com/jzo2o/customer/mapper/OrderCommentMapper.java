package com.jzo2o.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzo2o.customer.model.domain.OrderComment;
import com.jzo2o.customer.model.dto.OrderCommentStaffScoreAggDTO;

import java.util.List;

/**
 * 订单评论Mapper
 */
public interface OrderCommentMapper extends BaseMapper<OrderComment> {

    /**
     * 按服务人员聚合评论评分：平均评分、评论总数、好评数（评分大于等于 4）
     */
    List<OrderCommentStaffScoreAggDTO> selectStaffScoreStatistics();
}
