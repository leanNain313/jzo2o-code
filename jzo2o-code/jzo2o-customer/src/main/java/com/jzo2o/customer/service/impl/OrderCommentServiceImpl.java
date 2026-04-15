package com.jzo2o.customer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.orders.OrdersApi;
import com.jzo2o.api.orders.OrdersServeApi;
import com.jzo2o.api.orders.dto.response.OrderResDTO;
import com.jzo2o.api.orders.dto.response.ServeProviderIdResDTO;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.mapper.OrderCommentMapper;
import com.jzo2o.customer.model.domain.CommonUser;
import com.jzo2o.customer.model.domain.OrderComment;
import com.jzo2o.customer.model.dto.request.OrderCommentCreateReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentPageReqDTO;
import com.jzo2o.customer.model.dto.response.OrderCommentPageResDTO;
import com.jzo2o.customer.service.ICommonUserService;
import com.jzo2o.customer.service.IOrderCommentService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 订单评论服务实现类
 */
@Service
public class OrderCommentServiceImpl extends ServiceImpl<OrderCommentMapper, OrderComment> implements IOrderCommentService {
    /**
     * 订单状态：待评价
     */
    private static final int ORDER_STATUS_WAITING_EVALUATION = 400;

    /**
     * 评价状态：已评价
     */
    private static final int EVALUATION_STATUS_EVALUATED = 1;

    @Resource
    private OrdersApi ordersApi;

    @Resource
    private OrdersServeApi ordersServeApi;

    @Resource
    private ICommonUserService commonUserService;

    /**
     * 根据订单id评论服务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commentByOrderId(OrderCommentCreateReqDTO reqDTO) {
        OrderComment existComment = this.getById(reqDTO.getOrderId());
        if (ObjectUtil.isNotNull(existComment)) {
            throw new BadRequestException("该订单已评论，请勿重复提交");
        }

        OrderResDTO order = ordersApi.queryById(reqDTO.getOrderId());
        if (ObjectUtil.isNull(order)) {
            throw new BadRequestException("订单不存在");
        }

        if (ObjectUtil.notEqual(order.getOrdersStatus(), ORDER_STATUS_WAITING_EVALUATION)) {
            throw new ForbiddenOperationException("非待评价状态不可评价");
        }

        if (ObjectUtil.equal(order.getEvaluationStatus(), EVALUATION_STATUS_EVALUATED)) {
            throw new BadRequestException("该订单已评价");
        }

        if (ObjectUtil.notEqual(order.getServeItemId(), reqDTO.getServeItemId())) {
            throw new BadRequestException("服务项id与订单不匹配");
        }

        Long currentUserId = UserContext.currentUserId();
        CommonUser commonUser = commonUserService.getById(currentUserId);

        Long staffId = order.getServerId();
        if (ObjectUtil.isNull(staffId)) {
            ServeProviderIdResDTO serveProviderIdResDTO = ordersServeApi.queryServeProviderIdByOrderId(reqDTO.getOrderId());
            if (ObjectUtil.isNotNull(serveProviderIdResDTO)) {
                staffId = serveProviderIdResDTO.getServeProviderId();
            }
        }

        OrderComment orderComment = new OrderComment();
        orderComment.setOrderId(reqDTO.getOrderId());
        orderComment.setServeItemId(reqDTO.getServeItemId());
        orderComment.setServiceScore(reqDTO.getServiceScore());
        orderComment.setImageList(reqDTO.getImageList());
        orderComment.setContent(reqDTO.getContent());
        orderComment.setUserId(currentUserId);
        orderComment.setUserName(ObjectUtil.isNull(commonUser) ? null : commonUser.getNickname());
        orderComment.setUserAvatar(ObjectUtil.isNull(commonUser) ? null : commonUser.getAvatar());
        orderComment.setStaffId(staffId);

        this.save(orderComment);

        // 订单评价状态同步为已评价
        ordersApi.evaluate(reqDTO.getOrderId());
    }

    /**
     * 根据服务项id分页获取评论
     */
    @Override
    public PageResult<OrderCommentPageResDTO> pageByServeItemId(OrderCommentPageReqDTO reqDTO) {
        Page<OrderComment> page = PageUtils.parsePageQuery(reqDTO, OrderComment.class);

        LambdaQueryWrapper<OrderComment> queryWrapper = Wrappers.<OrderComment>lambdaQuery()
                .eq(OrderComment::getServeItemId, reqDTO.getServeItemId())
                .orderByDesc(OrderComment::getCreatedAt);

        Page<OrderComment> resultPage = baseMapper.selectPage(page, queryWrapper);
        return PageUtils.toPage(resultPage, OrderCommentPageResDTO.class, (entity, dto) -> {
            dto.setImageList(entity.getImageList());
            dto.setOrderId(entity.getOrderId());
        });
    }

    /**
     * 根据评论id删除评论
     */
    @Override
    public void deleteByCommentId(OrderCommentDeleteReqDTO reqDTO) {
        OrderComment orderComment = this.getById(reqDTO.getCommentId());
        if (ObjectUtil.isNull(orderComment)) {
            return;
        }

        if (ObjectUtil.notEqual(orderComment.getUserId(), UserContext.currentUserId())) {
            throw new ForbiddenOperationException("只能删除自己发布的评论");
        }

        this.removeById(reqDTO.getCommentId());
    }
}
