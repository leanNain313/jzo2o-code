package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.OrderComment;
import com.jzo2o.customer.model.dto.request.OrderCommentCreateReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentOperationPageReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentPageReqDTO;
import com.jzo2o.customer.model.dto.response.CommentCount;
import com.jzo2o.customer.model.dto.response.EvaluationAndOrdersResDTO;
import com.jzo2o.customer.model.dto.response.OrderCommentOperationPageResDTO;
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
     * 运营端分页查询订单评价
     *
     * @param reqDTO 查询条件
     * @return 分页结果
     */
    PageResult<OrderCommentOperationPageResDTO> operationPage(OrderCommentOperationPageReqDTO reqDTO);

    /**
     * 根据评论id删除评论（评论id即order_id）
     *
     * @param reqDTO 请求参数
     */
    void deleteByCommentId(OrderCommentDeleteReqDTO reqDTO);

    void statisticsScore();

    /**
     * 用户端：分页查询当前登录用户发出的订单评论（结构与服务员端分页一致，含关联订单信息）。
     */
    PageResult<EvaluationAndOrdersResDTO> commentPageByUserId(Integer pageNo, Integer pageSize);

    /**
     * 服务员端：按当前登录服务人员（staffId）分页查询收到的订单评论，并可按评价等级筛选。
     * 等级规则：好评≥4分，中评=3分，差评≤2分（与 scoreLevel：3/2/1 对应）。
     *
     * @param scoreLevel 1 差评，2 中评，3 好评，null 为全部
     */
    PageResult<EvaluationAndOrdersResDTO> pageForCurrentWorkerByScoreLevel(Integer scoreLevel, Integer pageNo, Integer pageSize);

//    CommentCount commentCount();
}
