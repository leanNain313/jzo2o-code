package com.jzo2o.market.service;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.market.model.domain.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.market.model.dto.request.ActivityQueryForPageReqDTO;
import com.jzo2o.market.model.dto.request.ActivitySaveReqDTO;
import com.jzo2o.market.model.dto.response.ActivityInfoResDTO;
import com.jzo2o.market.model.dto.response.SeizeCouponInfoResDTO;

import javax.validation.constraints.Null;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author itcast
 * @since 2023-09-16
 */
public interface IActivityService extends IService<Activity> {


    void saveOrUpdateActivity(ActivitySaveReqDTO dto);

    PageResult<ActivityInfoResDTO> findByPage(ActivityQueryForPageReqDTO dto);

    ActivityInfoResDTO findById(Long id);

    void revoke(Long id);

    /**
     * 修改活动状态
     */
    void updateStatus();

    void preHeat();

    List<SeizeCouponInfoResDTO> queryForListFromCache(Integer tabType);

    ActivityInfoResDTO getActivityInfoByIdFromCache(@Null(message = "请求失败") Long id);
}
