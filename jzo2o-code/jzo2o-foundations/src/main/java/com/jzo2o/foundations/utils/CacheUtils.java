package com.jzo2o.foundations.utils;

import com.jzo2o.foundations.constants.RedisConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CacheUtils {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 删除某个区域下开通服务的缓存
     * @param id 区域id
     */
    public void refreshRegionRelateCaches(Long id) {
        redisTemplate.delete(RedisConstants.CacheName.SERVE_ICON + "::" + id);
    }
}
