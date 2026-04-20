package com.jzo2o.customer.service.impl;

import cn.hutool.core.util.NumberUtil;
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
import com.jzo2o.customer.model.dto.OrderCommentStaffScoreAggDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentPageReqDTO;
import com.jzo2o.customer.model.dto.response.CommentCount;
import com.jzo2o.customer.model.dto.response.EvaluationAndOrdersResDTO;
import com.jzo2o.customer.model.dto.response.OrderCommentPageResDTO;
import com.jzo2o.customer.service.ICommonUserService;
import com.jzo2o.customer.service.IOrderCommentService;
import com.jzo2o.customer.service.IServeProviderService;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private IServeProviderService serveProviderService;

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

        if (ObjectUtil.notEqual(order.getOrdersStatus(), ORDER_STATUS_WAITING_EVALUATION)
         && ObjectUtil.notEqual(order.getOrdersStatus(), 500)) {
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

    /**
     * 根据订单评论汇总各服务人员评分，并写入 serve_provider（综合评分、好评率）。
     * 好评率规则：2 分及以下为差评，3 分为中评，4 分及以上为好评；好评率 = 好评数 / 评论总数。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void statisticsScore() {
        List<OrderCommentStaffScoreAggDTO> statisticsList = baseMapper.selectStaffScoreStatistics();
        if (CollUtils.isEmpty(statisticsList)) {
            return;
        }
        for (OrderCommentStaffScoreAggDTO row : statisticsList) {
            if (ObjectUtil.isNull(row.getStaffId()) || row.getCommentCount() == null || row.getCommentCount() <= 0) {
                continue;
            }
            Long goodCount = ObjectUtil.defaultIfNull(row.getGoodCount(), 0L);
            String goodLevelRate = NumberUtil.decimalFormat("#.##%",
                    NumberUtil.div(goodCount, row.getCommentCount(), 4));
            Double score = ObjectUtil.isNull(row.getAvgScore())
                    ? 5.0
                    : NumberUtil.round(row.getAvgScore(), 1).doubleValue();
            serveProviderService.updateScoreById(row.getStaffId(), score, goodLevelRate);
        }
    }

    /**
     * 分页查询我的评论（返回结构与服务员端分页一致：{@link EvaluationAndOrdersResDTO} + 订单信息）
     */
    @Override
    public PageResult<EvaluationAndOrdersResDTO> commentPageByUserId(Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new BadRequestException("请求参数不能为空");
        }
        Page<OrderComment> page = lambdaQuery()
                .eq(OrderComment::getUserId, UserContext.currentUserId())
                .orderByDesc(OrderComment::getCreatedAt)
                .page(new Page<>(pageNo, pageSize));
        return toEvaluationPageResult(page);
    }

    /**
     * 服务员端分页：当前用户 id 与订单评论 staff_id 一致（与历史上对外评价系统的 targetId 语义一致）。
     */
    @Override
    public PageResult<EvaluationAndOrdersResDTO> pageForCurrentWorkerByScoreLevel(Integer scoreLevel, Integer pageNo,
                                                                                   Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new BadRequestException("请求参数不能为空");
        }
        Long staffId = UserContext.currentUserId();
        LambdaQueryWrapper<OrderComment> queryWrapper = Wrappers.<OrderComment>lambdaQuery()
                .eq(OrderComment::getStaffId, staffId)
                .orderByDesc(OrderComment::getCreatedAt);
        applyWorkerScoreLevelFilter(queryWrapper, scoreLevel);

        Page<OrderComment> resultPage = baseMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        return toEvaluationPageResult(resultPage);
    }

//    @Override
//    public CommentCount commentCount() {
//        return CommentCount.builder()
//                .commentCount(lambdaQuery()
//                        .eq())
//                .build();
//    }

    private PageResult<EvaluationAndOrdersResDTO> toEvaluationPageResult(Page<OrderComment> resultPage) {
        List<OrderComment> records = resultPage.getRecords();
        if (CollUtils.isEmpty(records)) {
            PageResult<EvaluationAndOrdersResDTO> empty = PageResult.getInstance();
            empty.setList(Collections.emptyList());
            empty.setTotal(resultPage.getTotal());
            empty.setPages(resultPage.getPages());
            return empty;
        }
        List<Long> orderIds = records.stream().map(OrderComment::getOrderId).distinct().collect(Collectors.toList());
        List<OrderResDTO> orderList = ordersApi.queryByIds(orderIds);
        Map<Long, OrderResDTO> ordersMap = orderList.stream().collect(Collectors.toMap(OrderResDTO::getId, o -> o, (a, b) -> a));
        List<EvaluationAndOrdersResDTO> dtoList = records.stream()
                .map(c -> toEvaluationAndOrdersRes(c, ordersMap.get(c.getOrderId())))
                .collect(Collectors.toList());
        PageResult<EvaluationAndOrdersResDTO> pageResult = PageResult.getInstance();
        pageResult.setList(dtoList);
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setPages(resultPage.getPages());
        return pageResult;
    }

    /**
     * scoreLevel：1 差评≤2，2 中评=3，3 好评≥4；null 不按等级筛选。
     */
    private static void applyWorkerScoreLevelFilter(LambdaQueryWrapper<OrderComment> queryWrapper, Integer scoreLevel) {
        if (scoreLevel == null) {
            return;
        }
        if (ObjectUtil.equal(scoreLevel, 3)) {
            queryWrapper.ge(OrderComment::getServiceScore, 4);
        } else if (ObjectUtil.equal(scoreLevel, 2)) {
            queryWrapper.eq(OrderComment::getServiceScore, 3);
        } else if (ObjectUtil.equal(scoreLevel, 1)) {
            queryWrapper.le(OrderComment::getServiceScore, 2);
        }
    }

    private static EvaluationAndOrdersResDTO toEvaluationAndOrdersRes(OrderComment c, OrderResDTO order) {
        EvaluationAndOrdersResDTO dto = new EvaluationAndOrdersResDTO();
        dto.setId(String.valueOf(c.getOrderId()));
        dto.setRelationId(String.valueOf(c.getOrderId()));
        if (c.getStaffId() != null) {
            dto.setTargetId(String.valueOf(c.getStaffId()));
        }
        if (c.getServiceScore() != null) {
            dto.setTotalScore(c.getServiceScore().doubleValue());
            dto.setScoreLevel(resolveScoreLevelTag(c.getServiceScore()));
        }
        dto.setContent(c.getContent());
        if (CollUtils.isNotEmpty(c.getImageList())) {
            dto.setPictureArray(c.getImageList().toArray(new String[0]));
        }
        EvaluationAndOrdersResDTO.Person person = new EvaluationAndOrdersResDTO.Person();
        person.setNickName(c.getUserName());
        person.setAvatar(c.getUserAvatar());
        person.setIsAnonymous(0);
        dto.setEvaluatorInfo(person);
        if (c.getCreatedAt() != null) {
            dto.setCreateTime(Date.from(c.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        }
        if (order != null) {
            dto.setServeAddress(order.getServeAddress());
            dto.setServeItemImg(order.getServeItemImg());
            dto.setServeStartTime(order.getServeStartTime());
            if (order.getServeStartTime() != null) {
                dto.setUpdateTime(Date.from(order.getServeStartTime().atZone(ZoneId.systemDefault()).toInstant()));
            }
        }
        return dto;
    }

    /** 业务标签：3 好评，2 中评，1 差评（与前端 tab、历史接口一致） */
    private static Integer resolveScoreLevelTag(Integer serviceScore) {
        if (serviceScore == null) {
            return null;
        }
        if (serviceScore >= 4) {
            return 3;
        }
        if (serviceScore == 3) {
            return 2;
        }
        return 1;
    }
}
