package com.jzo2o.api.foundations;

import com.jzo2o.api.foundations.dto.request.JudgeRequest;
import com.jzo2o.api.foundations.dto.response.ServeAggregationResDTO;
import com.jzo2o.api.foundations.dto.response.ServeSimpleResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "jzo2o-foundations", value = "jzo2o-foundations", path = "/foundations/inner/serve")
public interface ServeApi {

    @GetMapping("/{id}")
    ServeAggregationResDTO findById(@PathVariable("id") Long id);

    @PostMapping("/region")
    Boolean judgeServe(@RequestBody JudgeRequest request);

    @PostMapping("/place/{id}")
    void placeOrder(@PathVariable Long id);

    @GetMapping("/serve")
    ServeAggregationResDTO getServeByCityCodeAndItemId(
            @RequestParam Long serveItemId,
            @RequestParam String cityCode
    );

    /**
     * 按城市编码和关键词搜索服务列表（基于 ES 全文检索）
     *
     * @param cityCode 城市编码（必填）
     * @param keyword  搜索关键词（可选）
     * @return 匹配的服务简略信息列表
     */
    @GetMapping("/search")
    List<ServeSimpleResDTO> search(
            @RequestParam("cityCode") String cityCode,
            @RequestParam(value = "keyword", required = false) String keyword
    );
}
