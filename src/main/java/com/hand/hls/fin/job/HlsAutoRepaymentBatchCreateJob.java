package com.hand.hls.fin.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatch;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatchLn;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchLnService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HlsAutoRepaymentBatchCreateJob extends AbstractJob {

    @Autowired
    private HlsCusLonContractRepaymentService hlsCusLonContractRepaymentService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private IHlsCusLonConRepaymentBatchService service;
    @Autowired
    private IHlsCusLonConRepaymentBatchLnService hlsCusLonConRepaymentBatchLnService;

    private static String DOCUMENT_CATEGORY = "LON_CONTRACT_REPAYMENT";
    private static String DOCUMENT_TYPE = "LON_CON_REPAYMENT_BATCH";
    private static String BUSINESS_TYPE = "LON_CON_REPAYMENT_BATCH";

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest requestCtx = RequestHelper.newEmptyRequest();
        requestCtx.setEmployeeCode("ADMIN");
        requestCtx.setUserName("admin");
        requestCtx.setCompanyId(248L);
        requestCtx.setUserId(10001L);
        requestCtx.setLocale("zh_CN");

        Long allocationId=144L;
        requestCtx.setAttribute("allocationId",allocationId);

        requestCtx.setAttribute("employeeCode","ADMIN");
        RequestHelper.setCurrentRequest(requestCtx);
        HlsCusLonContractRepayment lcr = new HlsCusLonContractRepayment();
        lcr.setAutoWrite("Y");
        //设置日期,只查询当天的还本付息
        java.util.Date date1 = new java.util.Date();
        lcr.setPlannedCalcDate(date1);
        List<HlsCusLonContractRepayment> list = hlsCusLonContractRepaymentService.selectLonContractRepAndFin(lcr);
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments = new ArrayList<>();
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getPlannedCalcDate().getTime() == list.get(j).getPlannedCalcDate().getTime() && list.get(i).getCreditBpId().equals(list.get(j).getCreditBpId())
                        && list.get(i).getLonCompanyId().equals(list.get(j).getLonCompanyId()) && list.get(i).getCurrency().equals(list.get(j).getCurrency())
                        //同一期还本付息计划出现了多个批次创建,增加条件排除之前已经创建批次的还本付息计划
                        && !ints.contains(j)
                ){
                    ints.add(j);
                    hlsCusLonContractRepayments.add(list.get(j));
                }
                if (j == (list.size()-1) && !ints.contains(i)){
                    hlsCusLonContractRepayments.add(list.get(i));
                }
            }
            if(hlsCusLonContractRepayments.size() == 0 && !ints.contains(i)){
                hlsCusLonContractRepayments.add(list.get(i));
            }
            if(hlsCusLonContractRepayments.size()>0){
                HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch = new HlsCusLonConRepaymentBatch();
                Map<String, String> params = new HashMap<>();
                String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
                hlsCusLonConRepaymentBatch.setBatchNumber(ruleCode);
                hlsCusLonConRepaymentBatch.setBatchStatus("APPROVED");
                hlsCusLonConRepaymentBatch.setUnitId(104L);
                hlsCusLonConRepaymentBatch.setRepaymentDate(new java.util.Date(hlsCusLonContractRepayments.get(0).getPlannedCalcDate().getTime()));
                hlsCusLonConRepaymentBatch.setCreditBpId(hlsCusLonContractRepayments.get(0).getCreditBpId());
                long time = hlsCusLonContractRepayments.get(0).getPlannedCalcDate().getTime();
                java.sql.Date sdate = new java.sql.Date(time);
                String s = sdate.toString();
                String date = s.substring(0, s.lastIndexOf("-"));
                System.out.println(date);
                hlsCusLonConRepaymentBatch.setDescription(date.replace("-","年")+"月银行还款-系统自动核销创建批次");
                hlsCusLonConRepaymentBatch = service.insertSelective(requestCtx,hlsCusLonConRepaymentBatch);
                if(hlsCusLonConRepaymentBatch.getBatchId() != null){
                    for(HlsCusLonContractRepayment dt:hlsCusLonContractRepayments){
                        HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn = new HlsCusLonConRepaymentBatchLn();
                        hlsCusLonConRepaymentBatchLn.setBatchId(hlsCusLonConRepaymentBatch.getBatchId());
                        hlsCusLonConRepaymentBatchLn.setRepaymentId(dt.getRepaymentId());
                        hlsCusLonConRepaymentBatchLn.setContractId(dt.getContractId());
                        hlsCusLonConRepaymentBatchLn.setWithdrawId(dt.getWithdrawId());
                        hlsCusLonConRepaymentBatchLn.setPlannedDueAmount(dt.getPlannedDueAmount());
                        hlsCusLonConRepaymentBatchLn = hlsCusLonConRepaymentBatchLnService.insertSelective(requestCtx,hlsCusLonConRepaymentBatchLn);
                    }
                }
                hlsCusLonContractRepayments.clear();
            }
        }
    }
}
