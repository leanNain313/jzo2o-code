package com.jzo2o.foundations.controller.consumer;

import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/customer/serve")
@RestController("customerServeController")
@Api(tags = "用户端 - 服务项相关接口")
public class ServeController {

    @Autowired
    private IServeService iServeService;

    @GetMapping("/firstPageServeList")
    public List<ServeCategoryResDTO> firstPageServeList(Long regionId) {
        return iServeService.firstPageServeList(regionId);
    }

    @GetMapping("/hotServeList")
    @ApiOperation("精选推荐")
    public List<ServeAggregationSimpleResDTO> hotServeList(Long regionId) {
        return iServeService.hotServeList(regionId);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询服务详情")
    public ServeAggregationSimpleResDTO findById(@PathVariable("id") Long id) {
        return iServeService.findById(id);
    }
    @GetMapping("/serveTypeList")
    @ApiOperation("查询当前区域下上架服务对应的分类")
    public List<ServeAggregationTypeSimpleResDTO> serveTypeList(Long regionId) {
        return iServeService.serveTypeList(regionId);
    }

    @GetMapping("/search")
    public List<ServeSimpleResDTO> search(String cityCode, String keyword, Long serveTypeId) {
        return iServeService.search(cityCode, keyword, serveTypeId);
    }
}
