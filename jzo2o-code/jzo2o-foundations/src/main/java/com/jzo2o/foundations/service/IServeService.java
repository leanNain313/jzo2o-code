package com.jzo2o.foundations.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.api.foundations.dto.response.ServeAggregationResDTO;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.*;
import io.swagger.models.auth.In;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 服务表 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-07-03
 */
public interface IServeService extends IService<Serve> {

    PageResult<ServeResDTO> servePageByRegion(ServePageQueryReqDTO request);

    void addServeByBatch(List<ServeUpsertReqDTO> request);

    void updateServeById(Long id, BigDecimal price, BigDecimal serveRate);

    void removeServeById(Long id);

    void onSale(Long id);

    void downSaleById(Long id);

    void offHot(Long id);

    void onHot(Long id);

    List<ServeCategoryResDTO> firstPageServeList(Long regionId);

    List<ServeAggregationSimpleResDTO> hotServeList(Long regionId);

    ServeAggregationSimpleResDTO findById(Long id);

    List<ServeAggregationTypeSimpleResDTO> serveTypeList(Long regionId);

    List<ServeSimpleResDTO> search(String city, String keyword, Long serveTypeId);

    ServeAggregationResDTO findServeDetailById(Long id);

    void addServeSync(Long serveId);

    void placeOrder(Long id);

    PageResult<ServePageResponse> firstPage(String cityCode, Integer page, Integer pageSize) throws IOException;
}
