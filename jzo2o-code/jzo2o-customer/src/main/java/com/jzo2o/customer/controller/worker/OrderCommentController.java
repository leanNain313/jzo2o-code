package com.jzo2o.customer.controller.worker;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.response.EvaluationAndOrdersResDTO;
import com.jzo2o.customer.service.IOrderCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 服务员端 — 订单评论（替代原 {@link EvaluationController} 的外部评价系统分页）
 */
@RestController("workerOrderCommentController")
@RequestMapping("/worker/order-comment")
@Api(tags = "服务端 - 订单评论")
public class OrderCommentController {

    @Resource
    private IOrderCommentService orderCommentService;

    @GetMapping("/pageByCurrentStaff")
    @ApiOperation("当前服务人员：按评价等级分页查询订单评论（含关联订单信息）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scoreLevel", value = "评价等级：1差评(≤2分)，2中评(3分)，3好评(≥4分)，不传为全部", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageNo", value = "页码，默认1", defaultValue = "1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "每页条数，默认10", defaultValue = "10", dataTypeClass = Integer.class)
    })
    public PageResult<EvaluationAndOrdersResDTO> pageByCurrentStaff(
            @RequestParam(value = "scoreLevel", required = false) Integer scoreLevel,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return orderCommentService.pageForCurrentWorkerByScoreLevel(scoreLevel, pageNo, pageSize);
    }
}
