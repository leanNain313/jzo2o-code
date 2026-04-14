package com.jzo2o.foundations.controller.consumer;

import cn.hutool.core.util.StrUtil;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.model.Result;
import com.jzo2o.foundations.model.dto.response.*;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

    @GetMapping("/page")
    public PageResult<ServePageResponse> firstPage(String cityCode, Integer pageNo, Integer pageSize) throws IOException {
        if (StrUtil.isEmpty(cityCode)) {
            throw new ForbiddenOperationException("城市编码不能为空");
        }
        if (pageNo == null || pageSize == null) {
            throw new ForbiddenOperationException("分页参数为空");
        }
        return iServeService.firstPage(cityCode, pageNo, pageSize);
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
