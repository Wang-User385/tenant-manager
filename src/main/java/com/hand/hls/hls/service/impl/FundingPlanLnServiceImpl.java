package com.hand.hls.hls.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.impl.HlsCusConContractCashflowServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.service.CshPaymentReqDtService;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.hls.mapper.HlsCusFundingPlanMapper;
import com.hand.hls.hls.service.IFundingPlanService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.ResMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.FundingPlanLn;
import com.hand.hls.hls.service.IFundingPlanLnService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingPlanLnServiceImpl extends BaseServiceImpl<HlsCusFundingPlanLn> implements IFundingPlanLnService{

    public static final String NOT = "NOT";
    public static final String INFLOW = "INFLOW";
    public static final String RELEASE = "RELEASE";
    public static final String REPORTING = "REPORTING";
    public static final String REPORTED = "REPORTED";
    public static final String INDIRECT = "INDIRECT";
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    private HlsCusCshPaymentReqDtMapper hlsCusCshPaymentReqDtMapper;
    @Autowired
    private CshPaymentReqDtService cshPaymentReqDtService;
    @Autowired
    private HlsCusFundingPlanLnMapper hlsCusFundingPlanLnMapper;
    @Autowired
    private HlsCusFundingPlanMapper hlsCusFundingPlanMapper;
    @Autowired
    private IFundingPlanService iFundingPlanService;

    @Override
    public void insertDt(IRequest iRequest,List<HlsCusFundingPlanLn> hlsCusFundingPlanLns){
        for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
            HlsCusConContractCashflow hlsCusConContractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusFundingPlanLns.get(i).getPlanId());
            if(hlsCusConContractCashflow.getCfItem() == 0) {
                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                conContractCashflow.setContractId(hlsCusConContractCashflow.getContractId());
                conContractCashflow.setDueDate(hlsCusConContractCashflow.getDueDate());
                conContractCashflow.setWriteOffFlag(NOT);
                conContractCashflow.setCfDirection(INFLOW);
                conContractCashflow.setCfStatus(RELEASE);
                List<HlsCusConContractCashflow> hlsCusConContractCashflows = hlsCusConContractCashflowMapper.select(conContractCashflow);
                for (int j = 0; j < hlsCusConContractCashflows.size(); j++) {
                    if(!REPORTING.equals(hlsCusConContractCashflows.get(j).getFundingPlanStatus())
                            &&!REPORTED.equals(hlsCusConContractCashflows.get(j).getFundingPlanStatus())){
                        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = hlsCusPrjQuotationCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflows.get(j).getGeneratedSourceDocLineId());
                        if(INDIRECT.equals(hlsCusPrjQuotationCashflow.getReceiptType())){
                            HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt = new HlsCusCshPaymentReqDt();
                            hlsCusCshPaymentReqDt.setFundingPlanLnId(hlsCusFundingPlanLns.get(i).getFundingPlanLnId());
                            hlsCusCshPaymentReqDt.setSourceDocId(hlsCusConContractCashflows.get(j).getContractId());
                            hlsCusCshPaymentReqDt.setSourceDocLineId(hlsCusConContractCashflows.get(j).getCashflowId());
                            hlsCusCshPaymentReqDt.setDeductAmount(hlsCusConContractCashflows.get(j).getDueAmount());
                            cshPaymentReqDtService.insert(iRequest, hlsCusCshPaymentReqDt);

                            HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                            contractCashflow.setCashflowId(hlsCusConContractCashflows.get(j).getCashflowId());
                            contractCashflow.setFundingPlanStatus(REPORTING);
                            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,contractCashflow);
                        }
                    }
                }
            }
        }
    }

    @Override
    public  List<HlsCusFundingPlanLn> removePlanLn(IRequest iRequest, List<HlsCusFundingPlanLn> hlsCusFundingPlanLns)throws ResMessageException {
        HlsCusFundingPlan hlsCusFundingPlan = new HlsCusFundingPlan();
        Double loanTotalAmount = 0D;
        Double loanNetAmount = 0D;
        Double deductAmount = 0D;
        if(hlsCusFundingPlanLns.size()>0){
            hlsCusFundingPlan = hlsCusFundingPlanMapper.selectByPrimaryKey(hlsCusFundingPlanLns.get(0).getFundingPlanId());
        }
        for (int j = 0; j < hlsCusFundingPlanLns.size(); j++) {
            HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt = new HlsCusCshPaymentReqDt();
            hlsCusCshPaymentReqDt.setFundingPlanLnId(hlsCusFundingPlanLns.get(j).getFundingPlanLnId());
            List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDts = hlsCusCshPaymentReqDtMapper.select(hlsCusCshPaymentReqDt);
            for (int k = 0; k < hlsCusCshPaymentReqDts.size(); k++) {
                HlsCusConContractCashflow hlsCusConContractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusCshPaymentReqDts.get(k).getSourceDocLineId());
                hlsCusConContractCashflow.setFundingPlanStatus("");
                hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,hlsCusConContractCashflow);

                deductAmount = HlsCusMathUtil.add(deductAmount,hlsCusConContractCashflow.getDueAmount());

            }
            cshPaymentReqDtService.batchDelete(hlsCusCshPaymentReqDts);

            HlsCusFundingPlanLn hlsCusFundingPlanLn = hlsCusFundingPlanLnMapper.selectByPrimaryKey(hlsCusFundingPlanLns.get(j).getFundingPlanLnId());
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setCashflowId(hlsCusFundingPlanLn.getPlanId());
            conContractCashflow.setFundingPlanStatus("");
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,conContractCashflow);



            HlsCusConContractCashflow contractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusFundingPlanLn.getPlanId());
            loanTotalAmount = HlsCusMathUtil.add(loanTotalAmount,contractCashflow.getDueAmount());
            if(contractCashflow.getCfItem()!=90){
                loanNetAmount = HlsCusMathUtil.add(loanNetAmount,contractCashflow.getDueAmount());
                loanNetAmount = HlsCusMathUtil.sub(loanNetAmount,deductAmount);
            }

        }
        this.batchDelete(hlsCusFundingPlanLns);
        hlsCusFundingPlan.setLoanTotalAmount(HlsCusMathUtil.sub(hlsCusFundingPlan.getLoanTotalAmount(),loanTotalAmount));
        hlsCusFundingPlan.setLoanNetAmount(HlsCusMathUtil.sub(hlsCusFundingPlan.getLoanNetAmount(),loanNetAmount));
        iFundingPlanService.updateByPrimaryKeySelective(iRequest,hlsCusFundingPlan);
        return hlsCusFundingPlanLns;
    }
}