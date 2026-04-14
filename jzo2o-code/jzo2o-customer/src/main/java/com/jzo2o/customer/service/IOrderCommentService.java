package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.OrderComment;
import com.jzo2o.customer.model.dto.request.OrderCommentCreateReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentPageReqDTO;
import com.jzo2o.customer.model.dto.response.OrderCommentPageResDTO;

/**
 * Order comment service.
 */
public interface IOrderCommentService extends IService<OrderComment> {

    /**
     * Comment service by order id.
     */
    void commentByOrderId(OrderCommentCreateReqDTO reqDTO);

    /**
     * Query comments by serve item id with pagination.
     */
    PageResult<OrderCommentPageResDTO> pageByServeItemId(OrderCommentPageReqDTO reqDTO);

    /**
     * Delete comment by comment id (comment id equals order id).
     */
    void deleteByCommentId(OrderCommentDeleteReqDTO reqDTO);
}
