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
 * Order comment service implementation.
 */
@Service
public class OrderCommentServiceImpl extends ServiceImpl<OrderCommentMapper, OrderComment> implements IOrderCommentService {
    /**
     * Order status: waiting for evaluation.
     */
    private static final int ORDER_STATUS_WAITING_EVALUATION = 400;

    /**
     * Evaluation status: already evaluated.
     */
    private static final int EVALUATION_STATUS_EVALUATED = 1;

    @Resource
    private OrdersApi ordersApi;

    @Resource
    private OrdersServeApi ordersServeApi;

    @Resource
    private ICommonUserService commonUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commentByOrderId(OrderCommentCreateReqDTO reqDTO) {
        OrderComment existComment = this.getById(reqDTO.getOrderId());
        if (ObjectUtil.isNotNull(existComment)) {
            throw new BadRequestException("This order has already been commented");
        }

        OrderResDTO order = ordersApi.queryById(reqDTO.getOrderId());
        if (ObjectUtil.isNull(order)) {
            throw new BadRequestException("Order does not exist");
        }

        if (ObjectUtil.notEqual(order.getOrdersStatus(), ORDER_STATUS_WAITING_EVALUATION)) {
            throw new ForbiddenOperationException("Only orders waiting for evaluation can be commented");
        }

        if (ObjectUtil.equal(order.getEvaluationStatus(), EVALUATION_STATUS_EVALUATED)) {
            throw new BadRequestException("This order has already been evaluated");
        }

        if (ObjectUtil.notEqual(order.getServeItemId(), reqDTO.getServeItemId())) {
            throw new BadRequestException("serveItemId does not match this order");
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

        // Keep order evaluation status in sync.
        ordersApi.evaluate(reqDTO.getOrderId());
    }

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

    @Override
    public void deleteByCommentId(OrderCommentDeleteReqDTO reqDTO) {
        OrderComment orderComment = this.getById(reqDTO.getCommentId());
        if (ObjectUtil.isNull(orderComment)) {
            return;
        }

        if (ObjectUtil.notEqual(orderComment.getUserId(), UserContext.currentUserId())) {
            throw new ForbiddenOperationException("You can only delete your own comments");
        }

        this.removeById(reqDTO.getCommentId());
    }
}
