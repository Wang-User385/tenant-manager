package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusDepositDeduction;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import hls.core.utils.exception.HlsCusException;

import java.util.List;


public interface HlsCusDepositDeductionService extends IBaseService<HlsCusDepositDeduction>, ProxySelf<HlsCusDepositDeductionService> {


    /**
     * 抵扣查询
     *
     * @param iRequest
     * @param hlsCusDepositDeduction
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusDepositDeduction> selectDepositDedctionData(IRequest iRequest, HlsCusDepositDeduction hlsCusDepositDeduction, int page, int pageSize);


    /**
     * 保存+ 抵扣
     *
     * @param iRequest
     * @param hlsCusDepositDeductions
     * @return
     */
    List<HlsCusDepositDeduction> saveDepositDedctionData(IRequest iRequest, List<HlsCusDepositDeduction> hlsCusDepositDeductions) throws IllegalArgumentException, HlsCusException;


    void depositDeductionAdd(IRequest iRequest, HlsCusDepositDeduction depositDeduction) throws BeyondAmountLimitException;


    /**
     * 已保存未抵扣的金额
     *
     * @param hlsCusDepositDeduction
     * @return
     */
    Double selectNewDectionAmountSum(HlsCusDepositDeduction hlsCusDepositDeduction);


    void approvalDepositDeduction(IRequest iRequest, List<HlsCusDepositDeduction> hlsCusDepositDeductions) throws IllegalArgumentException, HlsCusException;
}