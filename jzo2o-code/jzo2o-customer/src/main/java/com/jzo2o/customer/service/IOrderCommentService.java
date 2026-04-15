package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.OrderComment;
import com.jzo2o.customer.model.dto.request.OrderCommentCreateReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentPageReqDTO;
import com.jzo2o.customer.model.dto.response.OrderCommentPageResDTO;

/**
 * 订单评论服务
 */
public interface IOrderCommentService extends IService<OrderComment> {

    /**
     * 根据订单id评论服务
     *
     * @param reqDTO 请求参数
     */
    void commentByOrderId(OrderCommentCreateReqDTO reqDTO);

    /**
     * 根据服务项id分页获取评论
     *
     * @param reqDTO 请求参数
     * @return 分页结果
     */
    PageResult<OrderCommentPageResDTO> pageByServeItemId(OrderCommentPageReqDTO reqDTO);

    /**
     * 根据评论id删除评论（评论id即order_id）
     *
     * @param reqDTO 请求参数
     */
    void deleteByCommentId(OrderCommentDeleteReqDTO reqDTO);
}
