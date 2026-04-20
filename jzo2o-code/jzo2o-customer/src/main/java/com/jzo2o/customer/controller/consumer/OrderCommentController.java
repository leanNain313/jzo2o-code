package com.jzo2o.customer.controller.consumer;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.OrderCommentCreateReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentPageReqDTO;
import com.jzo2o.customer.model.dto.response.CommentCount;
import com.jzo2o.customer.model.dto.response.EvaluationAndOrdersResDTO;
import com.jzo2o.customer.model.dto.response.OrderCommentPageResDTO;
import com.jzo2o.customer.service.IOrderCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单评论相关接口
 */
@RestController("consumerOrderCommentController")
@RequestMapping("/consumer/order-comment")
@Api(tags = "用户端 - 订单评论相关接口")
@Validated
public class OrderCommentController {

    @Resource
    private IOrderCommentService orderCommentService;

    @PostMapping
    @ApiOperation("根据订单id评论服务")
    public void commentByOrderId(@Validated @RequestBody OrderCommentCreateReqDTO reqDTO) {
        orderCommentService.commentByOrderId(reqDTO);
    }

    @GetMapping("/pageByServeItemId")
    @ApiOperation("根据服务项id分页获取评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "起始页", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "serveItemId", value = "服务项id", required = true, dataTypeClass = Long.class)
    })
    public PageResult<OrderCommentPageResDTO> pageByServeItemId(@Validated OrderCommentPageReqDTO reqDTO) {
        return orderCommentService.pageByServeItemId(reqDTO);
    }

    @ApiOperation("分页获取「我的评论」（结构与服务员端订单评论分页一致，含订单关联信息）")
    @GetMapping("/commentPageByUserId")
    public PageResult<EvaluationAndOrdersResDTO> commentPageByUserId(Integer pageNo, Integer pageSize) {
        return orderCommentService.commentPageByUserId(pageNo, pageSize);
    }

//    public CommentCount commentCount() {
//        return orderCommentService.commentCount();
//    }

    @DeleteMapping
    @ApiOperation("根据订单id删除评论")
    public void deleteByCommentId(@Validated @RequestBody OrderCommentDeleteReqDTO reqDTO) {
        orderCommentService.deleteByCommentId(reqDTO);
    }
}
