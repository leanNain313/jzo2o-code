package com.jzo2o.api.foundations;

import com.jzo2o.api.foundations.dto.request.JudgeRequest;
import com.jzo2o.api.foundations.dto.response.ServeAggregationResDTO;
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

}
