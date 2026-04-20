package com.jzo2o.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.mapper.WalletBillMapper;
import com.jzo2o.customer.model.domain.WalletBill;
import com.jzo2o.customer.model.dto.request.WalletBillPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.WalletBillResDTO;
import com.jzo2o.customer.service.IWalletBillService;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包账单持久化与分页查询。
 */
@Service
public class WalletBillServiceImpl extends ServiceImpl<WalletBillMapper, WalletBill> implements IWalletBillService {
    @Override
    public void addBill(Long walletId, Integer type, BigDecimal amount, Long serviceOrderId, String description) {
        WalletBill walletBill = new WalletBill();
        walletBill.setWalletId(walletId);
        walletBill.setType(type);
        walletBill.setAmount(amount);
        walletBill.setServiceOrderId(serviceOrderId);
        walletBill.setDescription(description);
        walletBill.setTransactionTime(LocalDateTime.now());
        save(walletBill);
    }

    @Override
    public PageResult<WalletBillResDTO> pageQuery(WalletBillPageQueryReqDTO reqDTO) {
        Page<WalletBill> page = PageUtils.parsePageQuery(reqDTO, WalletBill.class);
        LambdaQueryWrapper<WalletBill> queryWrapper = Wrappers.<WalletBill>lambdaQuery()
                .eq(reqDTO.getWalletId() != null, WalletBill::getWalletId, reqDTO.getWalletId())
                .eq(reqDTO.getType() != null, WalletBill::getType, reqDTO.getType());
        Page<WalletBill> result = baseMapper.selectPage(page, queryWrapper);
        return PageUtils.toPage(result, WalletBillResDTO.class);
    }
}
