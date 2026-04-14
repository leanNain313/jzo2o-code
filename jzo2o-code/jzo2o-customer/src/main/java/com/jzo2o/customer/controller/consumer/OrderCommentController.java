package com.jzo2o.customer.controller.consumer;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.OrderCommentCreateReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentDeleteReqDTO;
import com.jzo2o.customer.model.dto.request.OrderCommentPageReqDTO;
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
 * Order comment related APIs.
 */
@RestController("consumerOrderCommentController")
@RequestMapping("/consumer/order-comment")
@Api(tags = "Consumer - Order comment APIs")
@Validated
public class OrderCommentController {

    @Resource
    private IOrderCommentService orderCommentService;

    @PostMapping
    @ApiOperation("发布评论")
    public void commentByOrderId(@Validated @RequestBody OrderCommentCreateReqDTO reqDTO) {
        orderCommentService.commentByOrderId(reqDTO);
    }

    @GetMapping("/pageByServeItemId")
    @ApiOperation("根据服务项id获取评论分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "Start page number", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "Page size", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "serveItemId", value = "Serve item id", required = true, dataTypeClass = Long.class)
    })
    public PageResult<OrderCommentPageResDTO> pageByServeItemId(@Validated OrderCommentPageReqDTO reqDTO) {
        return orderCommentService.pageByServeItemId(reqDTO);
    }

    @DeleteMapping
    @ApiOperation("根据id删除评论")
    public void deleteByCommentId(@Validated @RequestBody OrderCommentDeleteReqDTO reqDTO) {
        orderCommentService.deleteByCommentId(reqDTO);
    }
}
