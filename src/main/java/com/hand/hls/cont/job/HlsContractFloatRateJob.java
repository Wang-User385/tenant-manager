package com.hand.hls.cont.job;

import com.hand.hap.activiti.components.HlsCusConFloatingRateReqSubmitServiceTask;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.impl.ConFloatingRateReqLnServiceImpl;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Qian Yuanfeng
 * @date 2020/7/6 - 18:06
 */
public class HlsContractFloatRateJob extends AbstractJob {

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private ConFloatingRateReqLnServiceImpl conFloatingRateReqLnService;

    @Autowired
    private HlsCusConFloatingRateReqSubmitServiceTask hlsCusConFloatingRateReqSubmitServiceTask;

    @Autowired
    private HlsCusConFloatingRateReqMapper hlsCusConFloatingRateReqMapper;

    @Autowired
    private JeTrxCommonService commonService;

    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    private static final String LPR_ADJUST_PERIOD_THIS = "THIS_PERIOD";
    private static final String LPR_ADJUST_PERIOD_NEXT = "NEXT_PERIOD";
    private static final String STATUS_CALCULATED = "CALCULATED";


    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;


    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        List<HlsCusConContract> conContractList = new ArrayList<>();

        IRequest requestCtx = RequestHelper.getCurrentRequest(true);
        //查询出 所有 调息生效日 = new date() 的已付款合同  , 且没有逾期
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationMapper.queryQuotationCalcReqJob(hlsCusPrjQuotation);
        HlsCusConFloatingRateReq hlsCusConFloatingRateReq = new HlsCusConFloatingRateReq();

        if (hlsCusPrjQuotationList.size() > 0) {
            //创建批次 ，插入调息头行表
            List<HlsCusConFloatingRateReqLn> cusConFloatingRateReqLnList = conFloatingRateReqLnService.createFloatingRateQuotation(requestCtx, hlsCusPrjQuotationList);
            List<Long> quotationIds = new ArrayList();

            if (cusConFloatingRateReqLnList.size() > 0) {
                //计算逻辑
                for (HlsCusConFloatingRateReqLn floatingRateReqLn : cusConFloatingRateReqLnList) {
                    if (floatingRateReqLn.getQuotationId() != null) {
                        quotationIds.add(floatingRateReqLn.getQuotationId());
                    }
                }

                Long fltReqId = cusConFloatingRateReqLnList.get(0).getFltReqId();
                conFloatingRateReqLnService.calRateChange(requestCtx, fltReqId, quotationIds);

                // 更新 lpr_link_date 和 next_adjustment_date
                for (HlsCusConFloatingRateReqLn reqLn : cusConFloatingRateReqLnList) {
                    HlsCusConFloatingRateReqLn floatingRateReqLn = new HlsCusConFloatingRateReqLn();
                    floatingRateReqLn = conFloatingRateReqLnService.selectByPrimaryKey(requestCtx, reqLn);
                    if (STATUS_CALCULATED.equalsIgnoreCase(floatingRateReqLn.getStatus())) {

                        if (floatingRateReqLn.getContractId() != null) {

                            HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(floatingRateReqLn.getContractId());

                            HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
                            cusPrjQuotation.setQuotationId(floatingRateReqLn.getQuotationId());
                            HlsCusPrjQuotation prjQuotationOld = hlsCusPrjQuotationMapper.selectByPrimaryKey(cusPrjQuotation);

                            HlsCusPrjQuotation cusPrjQuotationNew = new HlsCusPrjQuotation();
                            cusPrjQuotationNew.setQuotationId(floatingRateReqLn.getQuotationIdNew());
                            HlsCusPrjQuotation prjQuotationNew = hlsCusPrjQuotationMapper.selectByPrimaryKey(cusPrjQuotationNew);


                            //旧的lpr_link_date + 调息期限 = 新的 lpr_link_date (即lpr挂钩日期)
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(prjQuotationOld.getLprLinkDate());
                            calendar.add(Calendar.MONTH, Integer.parseInt(prjQuotationOld.getLprAdjustmentTerm()));
                            Date LprLinkeDateNew = calendar.getTime();
                            hlsCusConContract.setLprLinkDate(LprLinkeDateNew);

                            if (floatingRateReqLn.getNewBaseRate() != null) {
                                hlsCusConContract.setBaseRate(floatingRateReqLn.getNewBaseRate());
                            }
                            if (floatingRateReqLn.getNewIntRate() != null) {
                                hlsCusConContract.setIntRate(floatingRateReqLn.getNewIntRate());
                            }


                            if (LPR_ADJUST_PERIOD_THIS.equalsIgnoreCase(prjQuotationOld.getLprAdjustmentPeriod())) {
                                //下次调息日期 - 本期情况下，lpr挂钩日期加上调息期限  - 放款日/起租日
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(LprLinkeDateNew);
                                cal.add(Calendar.MONTH, Integer.parseInt(prjQuotationOld.getLprAdjustmentTerm()));
                                Date nextAdjustmentDate = cal.getTime();

                                hlsCusConContract.setNextAdjustmentDate(nextAdjustmentDate);

                                prjQuotationOld.setLprLinkDate(LprLinkeDateNew);
                                prjQuotationOld.setNextAdjustmentDate(nextAdjustmentDate);

                                hlsCusPrjQuotationService.updateByPrimaryKeySelective(requestCtx, prjQuotationOld);

                            } else if (LPR_ADJUST_PERIOD_NEXT.equalsIgnoreCase(prjQuotationOld.getLprAdjustmentPeriod())) {
                                //下次调息日期 - 次期情况下 ,算出日期在的期次，该期次的计算日即为下次调息日期
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(LprLinkeDateNew);
                                cal.add(Calendar.MONTH, Integer.parseInt(prjQuotationOld.getLprAdjustmentTerm()));
                                Date nextAdjustmentDate = cal.getTime();

                                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                                hlsCusPrjQuotationCashflow.setQuotationId(prjQuotationNew.getQuotationId());
                                hlsCusPrjQuotationCashflow.setSourceDocumentId(prjQuotationNew.getSourceDocumentId());
                                hlsCusPrjQuotationCashflow.setLprCalcDate(nextAdjustmentDate);

                                List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.queryPrjCashflowByDate(hlsCusPrjQuotationCashflow);
                                if (prjQuotationCashflowList.size() > 0) {
                                    nextAdjustmentDate = prjQuotationCashflowList.get(0).getCalcDate();
                                }
                                hlsCusConContract.setNextAdjustmentDate(nextAdjustmentDate);

                                prjQuotationOld.setLprLinkDate(LprLinkeDateNew);
                                prjQuotationOld.setNextAdjustmentDate(nextAdjustmentDate);
                                hlsCusPrjQuotationService.updateByPrimaryKeySelective(requestCtx, prjQuotationOld);

                            }

                            hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);


                            conContractList.add(hlsCusConContract);


                        }
                    }
                }

            }

            //更新原现金流
            HlsCusConFloatingRateReq floatingRateReq = new HlsCusConFloatingRateReq();
            floatingRateReq.setFltReqId(cusConFloatingRateReqLnList.get(0).getFltReqId());
            hlsCusConFloatingRateReq = hlsCusConFloatingRateReqMapper.selectByPrimaryKey(floatingRateReq);
            hlsCusConFloatingRateReqSubmitServiceTask.updateCashflowByQuotation(requestCtx, hlsCusConFloatingRateReq);

            //出调息凭证
            for (HlsCusConFloatingRateReqLn floatingRateReqLn : cusConFloatingRateReqLnList) {
                AbstractJeTrxService floatingRateReqLnService = commonService.map.get("FLOATING_INTEREST");
                Map params = new HashMap<>();
                params.put("jeTrxId", floatingRateReqLn.getFltReqLnId());
                params.put("companyId", requestCtx.getCompanyId());
                params.put("contractId", floatingRateReqLn.getFltReqLnId());
                params.put("sourceDoc", "CON_CONTRACT");
                floatingRateReqLnService.process(requestCtx, params);
            }

        }


        //摊销
        iContractFinanceIncomeService.financeIncomeSharing(requestCtx, conContractList);

    }
}
