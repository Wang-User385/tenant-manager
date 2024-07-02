package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractRiskFund;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

/**
 * @Auther: hxh
 * @Date: 2020年6月1日
 * @Description:
 */
public interface IGldMonthSettleService extends IBaseService<HlsCusContractFinanceIncome>, ProxySelf<IGldMonthSettleService> {
    int SCALE = 2;

    List<HlsCusContractFinanceIncome> preFinIncomeRecognition(IRequest requestCtx, List<HlsCusContractFinanceIncome> contractFinanceIncomeList,String periodName) throws ResMessageException;

    List<HlsCusContractFinanceIncome> finIncomeRecognition(IRequest requestCtx, List<HlsCusContractFinanceIncome> contractFinanceIncomeList,String periodName) throws ResMessageException;

    List<HlsCusContractFinanceIncome> reversePreFinIncome(IRequest requestCtx, List<HlsCusContractFinanceIncome> contractFinanceIncomeList,String periodName) throws ResMessageException;

    List<HlsCusConContract> stampDutyAccrual(IRequest requestCtx, List<HlsCusConContract> hlsCusConContractList) throws ResMessageException;

    List<HlsCusGldLonContractFinCost> lonFinCostRecognition(IRequest requestCtx, List<HlsCusGldLonContractFinCost> gldLonContractFinCostsList, String periodName, String contractType) throws ResMessageException;

    List<HlsCusConContract> stampDutyAccrualLease(List<HlsCusConContract> cusConContractList);

    List<HlsCusLonContractWithdraw> stampDutyAccrualFinancing(List<HlsCusLonContractWithdraw> cusLonContractWithdraws);

    List<HlsCusConContractRiskFund> riskFundAccrual(IRequest requestCtx, List<HlsCusConContractRiskFund> conContractRiskFundList, String periodName);
}