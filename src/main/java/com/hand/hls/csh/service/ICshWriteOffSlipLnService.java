package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshWriteOffSlipLn;

import java.util.List;

/**
 * @author ferry
 * @date 2019-08-08
 * @description 付款单业务接口
 */

public interface ICshWriteOffSlipLnService extends IBaseService<CshWriteOffSlipLn>, ProxySelf<ICshWriteOffSlipLnService>{
    /**
     * 查询收款单行数据
     */
    List<CshWriteOffSlipLn> query(IRequest iRequest, CshWriteOffSlipLn cshWriteOffSlipLn, int page, int pageSize);

    /**
     * 退款的冻结明细
     */
    List<CshWriteOffSlipLn> queryRefundBlockDetail(IRequest iRequest, Long transactionId);

    /**
     * 删除认领单行记录
     */
    int deleteBySlipId(Long slipId);
}
