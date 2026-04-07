package com.jzo2o.foundations.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.model.Result;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/operation/serve")
@RestController("operationServeController")
@Api(tags = "运营端 - 服务项相关接口")
public class ServeController {

    @Autowired
    private IServeService iServeService;

    @GetMapping("/page")
    @ApiOperation("服务分页查询")
    public PageResult<ServeResDTO> servePageByRegion(ServePageQueryReqDTO request) {
        return iServeService.servePageByRegion(request);
    }

    @PostMapping("/batch")
    @ApiOperation("批量增加服务")
    public Result<Void> addServeByBatch(@RequestBody List<ServeUpsertReqDTO> request) {
        iServeService.addServeByBatch(request);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改服务")
    public Result<Void> updateServeById(@PathVariable Long id, BigDecimal price) {
        iServeService.updateServeById(id, price);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除服务")
    public Result<Void> removeServeById(@PathVariable Long id) {
        iServeService.removeServeById(id);
        return Result.ok();
    }

    @PutMapping("/onSale/{id}")
    @ApiOperation("上架服务")
    public Result<Void> onSaleById(@PathVariable Long id) {
        iServeService.onSale(id);
        return Result.ok();
    }

    @ApiOperation("下架服务")
    @PutMapping("/offSale/{id}")
    public Result<Void> downSaleById(@PathVariable Long id) {
        iServeService.downSaleById(id);
        return Result.ok();
    }

    @ApiOperation("取消热门")
    @PutMapping("/offHot/{id}")
    public Result<Void> offHot(@PathVariable Long id) {
        iServeService.offHot(id);
        return Result.ok();
    }

    @ApiOperation(("设置热门"))
    @PutMapping("/onHot/{id}")
    public Result<Void> onHot(@PathVariable Long id) {
        iServeService.onHot(id);
        return Result.ok();
    }

}
