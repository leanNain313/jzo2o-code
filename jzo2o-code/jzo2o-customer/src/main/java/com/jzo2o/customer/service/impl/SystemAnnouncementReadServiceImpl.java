package com.jzo2o.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.customer.mapper.SystemAnnouncementReadMapper;
import com.jzo2o.customer.model.domain.SystemAnnouncementRead;
import com.jzo2o.customer.service.ISystemAnnouncementReadService;
import org.springframework.stereotype.Service;

@Service
public class SystemAnnouncementReadServiceImpl extends ServiceImpl<SystemAnnouncementReadMapper, SystemAnnouncementRead>
        implements ISystemAnnouncementReadService {
}
