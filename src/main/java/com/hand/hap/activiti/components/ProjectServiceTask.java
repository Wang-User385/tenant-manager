package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.mapper.SysConfigMapper;

import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;

import com.hand.hls.prj.mapper.HlsBpMasterMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.PrjProjectBpMapper;

import com.hand.hls.prj.service.BpSignService;
import com.hand.hls.prj.service.HlsBpMasterService;
import com.hand.hls.prj.service.IPrjProjectService;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 工作流结束监听器
 */
@Component
public class ProjectServiceTask implements JavaDelegate, IActivitiBean {

    Logger logger = LoggerFactory.getLogger(ProjectServiceTask.class);

    private static final String PROJECT = "project";

    private static final String IREQUEST = "iRequest";
    private static final String APPROVE_RESULT = "approveResult";
    private static final String START_USER_ID = "startUserId";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    private static final String NEW = "NEW";

    private static final String N = "N";


    @Autowired
    private IPrjProjectService prjProjectService;
//    @Autowired
//    private ISendMessageService sendMessageService;
    @Autowired
    PrjProjectBpMapper prjProjectBpMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
//    @Autowired
//    private ProjectExamineMapper projectExamineMapper;
//    @Autowired
//    private IProjectExamineService iProjectExamineService;
    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;
//    @Autowired
//    private OrgSignServiceImpl orgSignService;
    @Autowired
    private HlsBpMasterService hlsBpMasterService;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
//    @Autowired
//    private ISendMessageHtwyService sendMessageHtwyService;
    @Autowired
    private SysConfigMapper configMapper;
//    @Autowired
//    private IParamConfigValueService paramConfigValueService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    private static final String SIGN_ONLINE = "SIGN_ONLINE";
    private static final String UNCONFIRMED = "UNCONFIRMED";
    private static final String DOCUMENT_CATEGORY ="PRJ_PROJECT_LS";
    private static final String DOCUMENT_TYPE = "PRJ_PROJECT_LS";
    private static final String BUSINESS_TYPE = "PRJ_PROJECT_LS";

    public ProjectServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String prj = (String) delegateExecution.getVariable(PROJECT);
        HlsCusPrjProject prjProject = JSON.parseObject(prj, HlsCusPrjProject.class);

        IRequest requestCtx = (IRequest) delegateExecution.getVariable(IREQUEST);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        String result = (String) delegateExecution.getVariable(APPROVE_RESULT);
        String userId = String.valueOf(delegateExecution.getVariable(START_USER_ID));

        String eventCode = null;

        HlsCusPrjProject project = prjProjectService.selectByPrimaryKey(requestCtx, prjProject);
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(project.getTenantId());
        hlsCusBpMaster = hlsCusBpMasterService.selectByPrimaryKey(requestCtx,hlsCusBpMaster);

        HlsCusBpMaster hlsBpMasterManufacturer = (HlsCusBpMaster) hlsCusBpMasterMapper.selectByPrimaryKey(project.getManufacturerId());
        if(StringUtils.equals(hlsBpMasterManufacturer.getBpCode(),"C000004453")){
            project.setPartnersContractStatus("03");
        }
        // lock prj_project
//        databaseLockProvider.lock(project);
       /* if ("APPROVED".equalsIgnoreCase(result)) {
            service.saveConContractFromPrjProject(requestCtx, prjProject);
        }*/
           requestCtx.setUserId(Long.valueOf(userId));

           String signType = project.getSignType();
           //审批结束修改合同状态
           if (APPROVED.equalsIgnoreCase(result)) {
               project.setProjectStatus(APPROVED);
               project.setDocxFlag("Y");
           } else if (REJECTED.equalsIgnoreCase(result)) {
               project.setProjectStatus(REJECTED);
               project.setDocxFlag(N);
           } else {
               project.setProjectStatus(APPROVED);
               project.setDocxFlag("Y");
           }

           String returnMsg = new String();

           if (StringUtils.equals(project.getProjectStatus(), APPROVED)) {
               //记录首次通过的时间
//            ProjectExamine projectExamine  = new ProjectExamine();
//            projectExamine.setProjectId(project.getProjectId());
//            projectExamine.setTimeType("FIRST_APPROVED_TIME");
//            if(projectExamineMapper.select(projectExamine).size()==0){
//                projectExamine.setTime(new Date());
//                iProjectExamineService.insert(requestCtx,projectExamine);
//            }
               project.setSignStatus(NEW);
               project.setProjectDate(new Date());

               Map<String, String> param = new HashMap<>();
               String parameter1 = hlsBpMasterManufacturer.getBpEngName() == null? "" : hlsBpMasterManufacturer.getBpEngName().substring(0,4).toUpperCase();
               param.put("PARAMETER_01", parameter1);
               String parameter2 = project.getDocumentType().length() == 4 ? "Z" : "H";
               param.put("PARAMETER_02", parameter2);
               String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY,  DOCUMENT_TYPE , BUSINESS_TYPE , param);
               project.setContractNum(ruleCode);
               prjProjectService.updateByPrimaryKeySelective(requestCtx, project);


//            //签约类型是在线签约的，进件审批通过的时候会发送短信给承租人去绑定微信并进行在线签约
//            if (StringUtils.equals(hlsBpMaster.getBpClass(),"NP")) {
//                if(StringUtils.isNotEmpty(signType) && StringUtils.equals(signType,SIGN_ONLINE)){
//                    try {
//                        //签署状态更新为承租人签署中
//                        orgSignService.yxAnxinsignUpdateProjectOrgSignState(project.getProjectId(),"TENANT",null);
//                        logger.info("进件编号：{}，进件审批结束，发送在线签约短信提醒!",project.getProjectNumber());
//                        String  config= paramConfigValueService.getConfigCodeValue("MESSAGE_FLAG");
//                        if(config.equals("Y")){
//                            sendMessageHtwyService.approvedSendMessage(requestCtx, prjProject,project.getBpIdTenant());
//                        }else{
//                            sendMessageService.approvedSendMessage(requestCtx, project, project.getBpIdTenant());
//                        }
//                        logger.info("进件编号：{}，进件审批结束，自然人承租人生成签署定义版本记录!",project.getProjectNumber());
//                        returnMsg = orgSignService.yxPrjSignVersionInfoCreate(requestCtx,project.getProjectId());
//                        logger.info(returnMsg);
////                        //担保人-自然人
////                        Example example = new Example(HlsCusPrjProjectBp.class);
////                        example.createCriteria().andEqualTo("bpCategory", "GUARANTOR").andEqualTo("bpClass", "NP").andEqualTo("projectId", project.getProjectId());
////                        List<HlsCusPrjProjectBp> prjProjectBps = hlsCusPrjProjectBpMapper.selectByExample(example);
////                        for (HlsCusPrjProjectBp prjProjectBp : prjProjectBps) {
////                            if(config.equals("Y")){
////                                sendMessageHtwyService.approvedSendMessage(requestCtx, prjProject,prjProjectBp.getBpId());
////                            }else{
////                                sendMessageService.approvedSendMessage(requestCtx, project, prjProjectBp.getBpId());
////                            }
////                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }else{
//                    returnMsg = orgSignService.yxPrjSignVersionInfoCreate(requestCtx,project.getProjectId());
//                    orgSignService.yxAnxinsignUpdateProjectOrgSignState(project.getProjectId(),"TENANT",null);
//                    orgSignService.yxAnxinsignUpdateProjectOrgSignState(project.getProjectId(),null,"TENANT");
//                }
//            }

               project = prjProjectService.selectByPrimaryKey(requestCtx, prjProject);
//            try {
//                //担保人-自然人在线签约
//                Example example = new Example(HlsCusPrjProjectBp.class);
//                example.createCriteria().andEqualTo("bpCategory", "GUARANTOR").andEqualTo("bpClass", "NP").andEqualTo("refV04", "SIGN_ONLINE").andEqualTo("projectId", project.getProjectId());
//                List<HlsCusPrjProjectBp> prjProjectBps = hlsCusPrjProjectBpMapper.selectByExample(example);
//                String  config= paramConfigValueService.getConfigCodeValue("MESSAGE_FLAG");
//                for (HlsCusPrjProjectBp prjProjectBp : prjProjectBps) {
//                    if(config.equals("Y")){
//                        sendMessageHtwyService.approvedSendMessage(requestCtx, prjProject,prjProjectBp.getBpId());
//                    }else{
//                        sendMessageService.approvedSendMessage(requestCtx, project, prjProjectBp.getBpId());
//                    }
//                }
//                //担保人-法人在线签约
//                Example exampleOrg = new Example(HlsCusPrjProjectBp.class);
//                exampleOrg.createCriteria().andEqualTo("bpCategory", "GUARANTOR").andEqualTo("bpClass", "ORG").andEqualTo("refV04", "SIGN_ONLINE").andEqualTo("projectId", project.getProjectId());
//                List<HlsCusPrjProjectBp> prjProjectBpOrgs = hlsCusPrjProjectBpMapper.selectByExample(exampleOrg);
//                for (HlsCusPrjProjectBp prjProjectBp : prjProjectBpOrgs) {
//                    prjProjectService.createOrgGuarantorSignRequest(requestCtx,project,prjProjectBp);
//                }
//                if (prjProjectBps.size() > 0 || prjProjectBpOrgs.size() >0) {
//                    //设置担保人签约状态
//                    project.setGuaranteeSignedFlag("NOT");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
               //如果商业模式为厂商并且签约模式为线上，给进件确认状态初始化
               if (StringUtils.equals(signType, SIGN_ONLINE) && StringUtils.equals(project.getLeaseChannel(), "10") && StringUtils.equals(hlsCusBpMaster.getBpClass(), "NP")) {
                   project.setConfirmStatus(UNCONFIRMED);
//                project.setSignedFlag(BaseConstants.NO);
//                //设置担保人签约状态
//                project.setGuaranteeSignedFlag("NOT");
               }
           }


           prjProjectService.updateByPrimaryKeySelective(requestCtx, project);


           if (StringUtils.equals(project.getProjectStatus(), APPROVED) && StringUtils.equals(hlsCusBpMaster.getBpClass(), "ORG")) {
               prjProjectService.createOrgTenantSignRequest(requestCtx, project);
           }
    }
}
