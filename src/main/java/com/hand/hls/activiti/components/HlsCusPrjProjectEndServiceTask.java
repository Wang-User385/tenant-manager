package com.hand.hls.activiti.components;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.service.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectEndServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private IHlsCusPrjQuotationHistoryService hlsCusPrjQuotationHistoryService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private IHlsCusPrjQuotationCashflowHistoryService hlsCusPrjCashflowHistoryService;
    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;
    @Autowired
    private IHlsCusPrjQuotationDetailsHistoryService hlsCusPrjQuotationDetailsHistoryService;


    public HlsCusPrjProjectEndServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        Calendar cal = Calendar.getInstance();
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProject");
        String versionFlag = (String) delegateExecution.getVariable("versionFlag");
        HlsCusPrjProject hlsCusPrjProject = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProject = new HlsCusPrjProject();
        resultHlsCusPrjProject.setProjectId(hlsCusPrjProject.getProjectId());
        resultHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProject);
        databaseLockProvider.lock(resultHlsCusPrjProject);

        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            resultHlsCusPrjProject.setProjectStatus(flag);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            flag = "REJECTED";
            resultHlsCusPrjProject.setProjectStatus(flag);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        } else if ("SUSPEND".equalsIgnoreCase(result)) {
            flag = "SUSPEND";
            resultHlsCusPrjProject.setProjectStatus(flag);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        }

        if("APPROVED".equalsIgnoreCase(result) || "REJECTED".equalsIgnoreCase(result) || "SUSPEND".equalsIgnoreCase(result) || "REVIEW".equalsIgnoreCase(result)){
            if(versionFlag.equals("N")){
                List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
                HlsCusPrjProject prjProject = new HlsCusPrjProject();
                prjProject.setProjectId(resultHlsCusPrjProject.getProjectId());
                hlsCusPrjQuotationList = hlsCusPrjQuotationService.queryPrjQuotationByProjectId(prjProject);


                Long version_count = hlsCusPrjQuotationHistoryService.selectVersionCount(prjProject);
                version_count++;

                for(HlsCusPrjQuotation dt:hlsCusPrjQuotationList){
                    JSONObject jsonDTO = (JSONObject) JSONObject.toJSON(dt);
                    HlsCusPrjQuotationHistory hlsCusPrjQuotationHistory = jsonDTO.toJavaObject(HlsCusPrjQuotationHistory.class);
                    hlsCusPrjQuotationHistory.setQuotationId(null);
                    hlsCusPrjQuotationHistory.setVersionId(version_count);
                    hlsCusPrjQuotationHistory.setSourceQuotationId(dt.getQuotationId());
                    hlsCusPrjQuotationHistory.setChangeCategory("PRJ_BEFORE_NOTICE");
                    hlsCusPrjQuotationHistoryService.insertSelective(requestCtx,hlsCusPrjQuotationHistory);
                    //复制现金流
                    List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowService.queryQuotationCashFlowById(dt);
                    for(HlsCusPrjQuotationCashflow dt2:hlsCusPrjQuotationCashflowList){
                        JSONObject jsonCashflowDTO = (JSONObject) JSONObject.toJSON(dt2);
                        HlsCusPrjQuotationCashflowHistory hlsCusPrjQuotationCashflowHistory = jsonCashflowDTO.toJavaObject(HlsCusPrjQuotationCashflowHistory.class);
                        hlsCusPrjQuotationCashflowHistory.setQuotationCashflowId(null);
                        hlsCusPrjQuotationCashflowHistory.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                        if(dt2.getCfItem() != 90 && dt2.getCfItem() != 91){
                            hlsCusPrjCashflowHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationCashflowHistory);
                        }else{
                            //对承兑汇票现金流单独处理
                            if(dt2.getCfItem() == 91){
                                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow2 = new HlsCusPrjQuotationCashflow();
                                hlsCusPrjQuotationCashflow2.setQuotationCashflowId(dt2.getSourceCashflowId());
                                hlsCusPrjQuotationCashflow2 = hlsCusPrjQuotationCashflowService.selectByPrimaryKey(requestCtx,hlsCusPrjQuotationCashflow2);
                                JSONObject jsonCashflowDTO2 = (JSONObject) JSONObject.toJSON(hlsCusPrjQuotationCashflow2);
                                HlsCusPrjQuotationCashflowHistory hlsCusPrjQuotationCashflowHistory2 = jsonCashflowDTO2.toJavaObject(HlsCusPrjQuotationCashflowHistory.class);
                                hlsCusPrjQuotationCashflowHistory2.setQuotationCashflowId(null);
                                hlsCusPrjQuotationCashflowHistory2.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                                hlsCusPrjQuotationCashflowHistory2 = hlsCusPrjCashflowHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationCashflowHistory2);
                                hlsCusPrjQuotationCashflowHistory.setSourceCashflowId(hlsCusPrjQuotationCashflowHistory2.getQuotationCashflowId());
                                hlsCusPrjCashflowHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationCashflowHistory);
                            }
                        }
                    }

                    //复制details
                    HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
                    hlsCusPrjQuotationDetails.setQuotationId(dt.getQuotationId());
                    List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
                    for(HlsCusPrjQuotationDetails dt3:hlsCusPrjQuotationDetailsList){
                        JSONObject jsonDetailDTO = (JSONObject) JSONObject.toJSON(dt3);
                        HlsCusPrjQuotationDetailsHistory hlsCusPrjQuotationDetailsHistory = jsonDetailDTO.toJavaObject(HlsCusPrjQuotationDetailsHistory.class);
                        hlsCusPrjQuotationDetailsHistory.setQuotationDeatilId(null);
                        hlsCusPrjQuotationDetailsHistory.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                        hlsCusPrjQuotationDetailsHistoryService.insertSelective(requestCtx,hlsCusPrjQuotationDetailsHistory);
                    }
                }

                delegateExecution.setVariable("versionFlag", "Y");
            }

        }






    }


}
