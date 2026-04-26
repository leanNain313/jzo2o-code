package com.jzo2o.orders.manager.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesAuditReqDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesPageQueryReqDTO;
import com.jzo2o.orders.manager.model.dto.request.AfterSalesRemarkReqDTO;
import com.jzo2o.orders.manager.model.dto.response.AfterSalesResDTO;
import com.jzo2o.orders.manager.service.IAfterSalesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Api(tags = "运营端 - 售后相关接口")
@RequestMapping("/operation/after-sales")
public class OperationAfterSalesController {

    @Resource
    private IAfterSalesService afterSalesService;

    @GetMapping("/page")
    @ApiOperation("售后工单分页查询")
    public PageResult<AfterSalesResDTO> page(AfterSalesPageQueryReqDTO queryReqDTO) {
        return afterSalesService.operationPage(queryReqDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("售后工单详情")
    public AfterSalesResDTO detail(@PathVariable("id") Long id) {
        return afterSalesService.operationDetail(id);
    }

    @PutMapping("/{id}/audit")
    @ApiOperation("售后审核")
    public void audit(@PathVariable("id") Long id, @RequestBody @Validated AfterSalesAuditReqDTO reqDTO) {
        afterSalesService.audit(id, reqDTO, UserContext.currentUser());
    }

    @PutMapping("/{id}/remark")
    @ApiOperation("追加处理备注")
    public void remark(@PathVariable("id") Long id, @RequestBody @Validated AfterSalesRemarkReqDTO reqDTO) {
        afterSalesService.remark(id, reqDTO.getContent(), UserContext.currentUser());
    }
}
