package com.jzo2o.foundations.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzo2o.api.foundations.dto.response.ServeAggregationResDTO;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author itcast
 * @since 2023-07-03
 */
public interface ServeMapper extends BaseMapper<Serve> {

    /**
     * 根据区域id查询服务信息
     *
     * @param regionId 区域id
     * @return 服务信息
     */
    List<ServeResDTO> queryListByRegionId(Long regionId);

    List<ServeCategoryResDTO> firstPageServeListById(Long regionId);

    List<ServeAggregationSimpleResDTO> findServeListByRegionId(Long regionId);

    List<ServeAggregationTypeSimpleResDTO> findServeTypeListByRegionId(Long regionId);

    ServeAggregationResDTO findServeDetailById(Long id);

    @Update("update serve set buy_num =buy_num + 1 where id = #{id}")
    void placeOrder(Long id);

}
