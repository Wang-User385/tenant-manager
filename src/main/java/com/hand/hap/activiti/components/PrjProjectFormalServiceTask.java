package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PrjProjectFormalServiceTask implements JavaDelegate, IActivitiBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    IYLMessageNoticeService messageNoticeService;

    @Autowired
    IYLMessageNoticeService iylMessageNoticeService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsCusBpMasterBankAccountMapper bankAccountmapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution execution) {
        Long projectId = Long.parseLong(execution.getProcessInstanceBusinessKey());
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        IRequest requestCtx = (IRequest) execution.getVariable("iRequest");

        String result = (String) execution.getVariable("approveResult");
        if("APPROVED".equals(result)){
            //合同创建逻辑放在正审通过后
            prjCreateCon(projectId);
            hlsCusPrjProject.setProjectStatus("APPROVED");
            hlsCusPrjProject.setApprovedDate(new Date());
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }else if("REJECTED".equals(result)){
            hlsCusPrjProject.setProjectStatus("REJECTED");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }
        iylMessageNoticeService.orderAuditResult(projectId,"PRE_RISK",requestCtx);



    }

    public void prjCreateCon(Long projectId) {
        //step1 创建合同
        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(projectId);
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(prjProject);
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(prjProject.getTenantId());
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
        newContract.setVatRate(prjQuotation.getIntRate());
        newContract.setIntRate(prjQuotation.getIntRate());
        newContract.setLeaseItemAmount(newContract.getFinanceAmount());
        //获取产品线罚息率
        newContract.setPenaltyRate(prjProject.getPenaltyRate());
        newContract.setRiskAssistantFirst(null);//合同阶段风控初审需要重置
        newContract.setRiskHost(null);//合同阶段风控复核需要重置
        hlsCusConContractMapper.insertSelective(newContract);
    }


}
