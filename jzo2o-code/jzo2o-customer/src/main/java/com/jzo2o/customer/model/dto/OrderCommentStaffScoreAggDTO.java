package com.jzo2o.customer.model.dto;

import lombok.Data;

/**
 * 订单评论按服务人员聚合的评分统计（用于同步 serve_provider）
 */
@Data
public class OrderCommentStaffScoreAggDTO {

    /**
     * 服务人员 id（serve_provider.id）
     */
    private Long staffId;

    /**
     * 平均服务评分
     */
    private Double avgScore;

    /**
     * 评论总数
     */
    private Long commentCount;

    /**
     * 好评数量：评分 4 分及以上（2 分及以下差评，3 分中评，4 分及以上好评）
     */
    private Long goodCount;
}
