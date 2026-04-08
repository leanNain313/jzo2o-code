package com.jzo2o.market.service;

import com.jzo2o.api.market.dto.request.CouponUseBackReqDTO;
import com.jzo2o.api.market.dto.request.CouponUseReqDTO;
import com.jzo2o.api.market.dto.response.AvailableCouponsResDTO;
import com.jzo2o.api.market.dto.response.CouponUseResDTO;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.market.model.domain.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.market.model.dto.request.CouponOperationPageQueryReqDTO;
import com.jzo2o.market.model.dto.request.SeizeCouponReqDTO;
import com.jzo2o.market.model.dto.response.CouponInfoResDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author itcast
 * @since 2023-09-16
 */
public interface ICouponService extends IService<Coupon> {


    PageResult<CouponInfoResDTO> findByPage(CouponOperationPageQueryReqDTO dto);
    /**
     * 已领取优惠券自动过期
     */
    void processExpireCoupon();

    void seizeCoupon(SeizeCouponReqDTO seizeCouponReqDTO);

    /**
     * 我的优惠券列表
     *
     * @param lastId 最后一个优惠券id
     * @param userId 用户id
     * @param status 状态
     * @return 优惠券列表
     */
    List<CouponInfoResDTO> queryForList(Long lastId, Long userId, Integer status);
}
