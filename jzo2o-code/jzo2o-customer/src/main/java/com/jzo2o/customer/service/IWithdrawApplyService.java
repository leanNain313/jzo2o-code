package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.WithdrawApply;
import com.jzo2o.customer.model.dto.request.WithdrawApplyPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawApplyReqDTO;
import com.jzo2o.customer.model.dto.request.WithdrawAuditReqDTO;
import com.jzo2o.customer.model.dto.response.WithdrawApplyResDTO;

/**
 * 提现申请：服务人员发起申请（扣款+流水），运营审核与打款结果流转（失败时退款+收入流水）。
 */
public interface IWithdrawApplyService extends IService<WithdrawApply> {

    /**
     * 服务人员发起提现：校验金额、扣减钱包余额、写入申请单（申请中）、写入提现类型账单。
     */
    void apply(WithdrawApplyReqDTO reqDTO, Long userId, String userName);

    /**
     * 运营审核/打款结果：仅允许约定状态迁移；拒绝或打款失败需填写失败原因并退回余额与补收入账单。
     */
    void audit(WithdrawAuditReqDTO reqDTO);

    /**
     * 分页查询提现申请（用户端按 walletId 过滤，运营端可全量或按状态筛选）。
     */
    PageResult<WithdrawApplyResDTO> pageQuery(WithdrawApplyPageQueryReqDTO reqDTO);
}
