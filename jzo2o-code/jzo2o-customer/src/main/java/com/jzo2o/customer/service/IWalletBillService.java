package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.WalletBill;
import com.jzo2o.customer.model.dto.request.WalletBillPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.WalletBillResDTO;

import java.math.BigDecimal;

/**
 * 钱包账单明细：记录每笔收入、退款、提现等流水，供用户端与运营端分页查询。
 */
public interface IWalletBillService extends IService<WalletBill> {

    /**
     * 新增一条账单记录（不修改余额，余额变更由 {@link IWalletService} 完成）。
     *
     * @param walletId        钱包 ID
     * @param type            明细类型，见 {@link com.jzo2o.customer.enums.WalletBillTypeEnum}
     * @param amount          金额（正数）
     * @param serviceOrderId  关联服务单/订单 ID，无则传 null
     * @param description     展示用说明文案
     */
    void addBill(Long walletId, Integer type, BigDecimal amount, Long serviceOrderId, String description);

    /**
     * 分页查询账单，可按钱包 ID、类型筛选。
     */
    PageResult<WalletBillResDTO> pageQuery(WalletBillPageQueryReqDTO reqDTO);
}
