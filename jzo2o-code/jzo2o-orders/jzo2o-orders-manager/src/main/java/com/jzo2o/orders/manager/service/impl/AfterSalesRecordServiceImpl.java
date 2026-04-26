package com.jzo2o.orders.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.orders.base.mapper.AfterSalesRecordMapper;
import com.jzo2o.orders.base.model.domain.AfterSalesRecord;
import com.jzo2o.orders.manager.service.IAfterSalesRecordService;
import org.springframework.stereotype.Service;

@Service
public class AfterSalesRecordServiceImpl extends ServiceImpl<AfterSalesRecordMapper, AfterSalesRecord> implements IAfterSalesRecordService {
}
