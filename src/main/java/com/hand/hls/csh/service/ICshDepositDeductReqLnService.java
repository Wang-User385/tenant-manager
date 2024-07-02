package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.dto.CshDepositDeductReqLn;

import java.util.List;

public interface ICshDepositDeductReqLnService extends IBaseService<CshDepositDeductReqLn>, ProxySelf<ICshDepositDeductReqLnService> {
    /**
     * 查询保证金抵扣申请行列表
     *
     * @param request
     * @param cshDepositDeductReqLn
     * @param page
     * @param pagesize
     * @return 保证金抵扣申请行列表
     */
    List<CshDepositDeductReqLn> selectCshDepositDeductReqLnList(IRequest request, CshDepositDeductReqLn cshDepositDeductReqLn, int page, int pagesize);

    /**
     * 获取该合同未在本次抵扣中现金流最大期数
     *
     * @param cshDepositDeductReqHd
     * @return
     */
    Long selectUnDeductCashflowMaxTimes(CshDepositDeductReqHd cshDepositDeductReqHd);

    /**
     * 查询主键下一个值
     */
    Long queryNextPkValue();


    /**
     * 通过保证金抵扣插入保证金报表
     * @param reqLnId 抵扣行ID
     * @param writeOffSum 当前已核销金额汇总
     */
    void insertDepositWriteOffHistoryByDeduct(Long reqLnId,Double writeOffSum);


}