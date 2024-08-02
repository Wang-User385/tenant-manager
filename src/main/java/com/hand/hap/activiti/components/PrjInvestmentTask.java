package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PrjInvestmentTask implements JavaDelegate, IActivitiBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    private HlsCusConContractCashflowMapper hlsconContractCashflowMapper;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution execution) {
        Long projectId = Long.parseLong(execution.getProcessInstanceBusinessKey());
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);

        String result = (String) execution.getVariable("approveResult");
        if("APPROVED".equals(result)){
            prjCreateCon(projectId);
            hlsCusPrjProject.setInvestmentStatus("APPROVED");
            hlsCusPrjProject.setSignDate(new Date());
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }else if("REJECTED".equals(result)){
            hlsCusPrjProject.setInvestmentStatus("REJECTED");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }

    }

    public void prjCreateCon(Long projectId){
        //step1 创建合同
        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(projectId);
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(prjProject);

        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        prjQuotation.setSourceDocumentId(prjProject.getProjectId());
        prjQuotation = hlsCusPrjQuotationMapper.select(prjQuotation).get(0);
        HlsCusConContract newContract = new HlsCusConContract();
        hlsBeanRefUtilService.setFieldValue(newContract, map);
        newContract.setContractNumber(prjProject.getProjectNumber());
        newContract.setContractStatus("NEW");
        newContract.setDocumentType("CONLB");
        newContract.setDocumentCategory("CON_CONTRACT");
        newContract.setBusinessType("LEASEBACK");
        newContract.setQuotationId(prjQuotation.getQuotationId());
        newContract.setCreationDate(new Date());
        hlsCusConContractMapper.insertSelective(newContract);
        //step2 创建合同现金流
        HlsCusPrjQuotationCashflow prjQuoCashflow = new HlsCusPrjQuotationCashflow();
        prjQuoCashflow.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> prjQuoCashflowList = hlsCusPrjQuotationCashflowMapper.select(prjQuoCashflow);
        for(HlsCusPrjQuotationCashflow cashflow : prjQuoCashflowList){
            HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
            map = hlsBeanRefUtilService.getFieldValueMap(cashflow);
            hlsBeanRefUtilService.setFieldValue(newCashflow, map);
            newCashflow.setContractId(newContract.getContractId());
            newCashflow.setCreationDate(new Date());
            hlsconContractCashflowMapper.insertSelective(newCashflow);
        }
        //step3 创建付款申请数据
        HlsCusConContractCashflow equiCashflow = new HlsCusConContractCashflow();
        equiCashflow.setContractId(newContract.getContractId());
        equiCashflow.setCfItem(0L);
        equiCashflow.setCfType(0L);
        equiCashflow = hlsconContractCashflowMapper.select(equiCashflow).get(0);

        HlsCusCshPaymentReqHd reqHd = new HlsCusCshPaymentReqHd();
        reqHd.setCompanyId(newContract.getCompanyId());
        reqHd.setDocumentType("PAYMENT_REQ");
        reqHd.setDocumentCategory("CSH_PAYMENT_REQ");
        reqHd.setBusinessType("PAYMENT_REQ");
        String paymentReqNumber = codingRuleValuesService.getCodeRuleValue(RequestHelper.getCurrentRequest(), "CSH_PAYMENT_REQ", "PAYMENT_REQ", "PAYMENT_REQ", new HashMap<String, String>());
        reqHd.setPaymentReqNumber(paymentReqNumber);
        reqHd.setPaymentReqDate(new Date());
        reqHd.setPaymentReqStatus("APPROVED");
        reqHd.setAmount(equiCashflow.getDueAmount());
        reqHd.setCurrency("CNY");
        reqHd.setSourceContractId(newContract.getContractId());
        reqHd.setSourceDocType("CON_CONTRACT_CASHFLOW");
        reqHd.setSourceDocId(equiCashflow.getCashflowId());
        reqHd.setCreationDate(new Date());
        hlsCusCshPaymentReqHdMapper.insertSelective(reqHd);

        HlsCusCshPaymentReqLn reqLn = new HlsCusCshPaymentReqLn();
        reqLn.setPaymentReqId(reqHd.getPaymentReqId());
        reqLn.setSourceDocCategory("CON_CONTRACT");
        reqLn.setSourceDocId(equiCashflow.getContractId());
        reqLn.setSourceDocLineId(equiCashflow.getCashflowId());
        reqLn.setAmount(equiCashflow.getDueAmount());
        reqLn.setCreationDate(new Date());
        hlsCusCshPaymentReqLnMapper.insertSelective(reqLn);
    }

}
