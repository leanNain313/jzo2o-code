package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.IRegionService;
import com.jzo2o.foundations.service.IServeService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SpringCacheSyncHandler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IRegionService regionService;

    @Autowired
    private IServeService iServeService;

    //更新缓存---开通区域
    @XxlJob("activeRegionCacheSync")
    public void activeRegionCacheSync() {
        log.info("=============开始更新开通区域列表缓存============");
        //1. 使用redisTemplate删除当前缓存中开通区域列表
        redisTemplate.delete("JZ_CACHE::ACTIVE_REGIONS");
        
        //2. 重新将开通区域列表添加到缓存
        regionService.queryActiveRegionList();
    }

    @XxlJob("activeServeCacheSync")
    public void activeServeCacheSync() {
        log.info("=============开始更新开通区域下服务列表缓存============");
//        redisTemplate.delete(RedisConstants.CacheName.)
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionList();
        for (RegionSimpleResDTO regionSimpleResDTO : regionSimpleResDTOS) {
            redisTemplate.delete(RedisConstants.CacheName.SERVE_ICON + regionSimpleResDTO.getId());
            iServeService.firstPageServeList(regionSimpleResDTO.getId());
        }
    }

    @XxlJob("activeHotDataCacheSync")
    public void activeHotDataCacheSync() {
        log.info("=============开始更新热点服务列表缓存============");
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionList();
        for (RegionSimpleResDTO regionSimpleResDTO : regionSimpleResDTOS) {
            redisTemplate.delete(RedisConstants.CacheName.HOT_SERVE + regionSimpleResDTO.getId());
            iServeService.hotServeList(regionSimpleResDTO.getId());
        }
    }
}