package com.jzo2o.orders.manager.controller.consumer;

import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesApplyReqDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesPageQueryReqDTO;
import com.jzo2o.orders.manager.model.dto.response.AfterSalesResDTO;
import com.jzo2o.orders.manager.service.IAfterSalesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Api(tags = "用户端 - 售后相关接口")
@RequestMapping("/consumer/after-sales")
public class ConsumerAfterSalesController {

    @Resource
    private IAfterSalesService afterSalesService;

    @PostMapping
    @ApiOperation("提交售后申请")
    public AfterSalesResDTO submit(@RequestBody @Validated AfterSalesApplyReqDTO reqDTO) {
        CurrentUserInfo currentUser = UserContext.currentUser();
        return afterSalesService.submit(reqDTO, currentUser);
    }

    @GetMapping("/page")
    @ApiOperation("售后分页查询")
    public PageResult<AfterSalesResDTO> page(AfterSalesPageQueryReqDTO queryReqDTO) {
        return afterSalesService.consumerPage(queryReqDTO, UserContext.currentUser().getId());
    }

    @GetMapping("/{id}")
    @ApiOperation("售后详情")
    public AfterSalesResDTO detail(@PathVariable("id") Long id) {
        return afterSalesService.consumerDetail(id, UserContext.currentUser().getId());
    }
}
