package com.jzo2o.foundations.mapper;

import com.jzo2o.foundations.model.domain.ServeSync;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 服务同步表 Mapper 接口
 * </p>
 *
 * @author itcast
 * @since 2023-07-10
 */
public interface ServeSyncMapper extends BaseMapper<ServeSync> {

    @Update("update serve_sync set buy_num = buy_num + 1 where id = #{id}")
    void placeOrder(Long id);

}
