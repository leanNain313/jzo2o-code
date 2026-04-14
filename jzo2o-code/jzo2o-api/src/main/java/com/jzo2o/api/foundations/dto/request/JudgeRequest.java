package com.jzo2o.api.foundations.dto.request;

import lombok.Data;

@Data
public class JudgeRequest {

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 服务id
     */
    private Long serveId;
}
