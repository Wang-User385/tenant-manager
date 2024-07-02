package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.mapper.BpCreditRateMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.mapper.PrjProjectApprovalMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IProjectApprovalService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by huqingtao on 2019/06/28.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCreditTenantAmountActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CREDIT_TENANT_AMOUNT_WFL";
    private static final String workspace = "CREDIT_TENANT_AMOUNT_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusPrjProjectService prjProjectService;
    @Autowired
    private PrjProjectApprovalMapper prjProjectApprovalMapper;
    @Autowired
    private IProjectApprovalService projectApprovalService;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;

    private static final String PRJ_AMOUNT_ALLOCATE = "PRJ_AMOUNT_ALLOCATE";

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusPrjProject) list.get(0), iRequest);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusPrjProject hlsCusPrjProject, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(workFlowType,workspace);
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusPrjProject.getProjectId().toString());

        Long projectIdOld = hlsCusPrjProject.getRefProjectId();
        Long projectIdNew = hlsCusPrjProject.getProjectId();
        HlsCusPrjProject hlsCusPrjProjectOld = new HlsCusPrjProject();
        hlsCusPrjProjectOld.setProjectId(projectIdOld);
        hlsCusPrjProjectOld = prjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProjectOld);
        HlsCusPrjProject hlsCusPrjProjectNew = new HlsCusPrjProject();
        hlsCusPrjProjectNew.setProjectId(projectIdNew);
        hlsCusPrjProjectNew = prjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProjectNew);

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setEmployeeCode(iRequest.getEmployeeCode());
        hlsCusEmployee.setCompanyId(hlsCusPrjProject.getCompanyId());
        hlsCusEmployee = hlsCusEmployeeMapper.selectUnitIdByEmployeeCodeAndCompanyId(hlsCusEmployee).get(0);

        //项目提交成功 初始化项目批复书表
        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setProjectId(hlsCusPrjProject.getProjectId().toString());
        List<PrjProjectApproval>  approvals = prjProjectApprovalMapper.queryAll(prjProjectApproval);
        if(approvals.size() == 0){
            Map<String, String> params1 = new HashMap<String, String>();
            prjProjectApproval.setProjectId(hlsCusPrjProject.getProjectId().toString());
            prjProjectApproval = projectApprovalService.insertSelective(iRequest,prjProjectApproval);
        }else{
            prjProjectApproval = approvals.get(0);
        }

        //初始化附件 法律审查意见/风险评估报告
        HlsCusPrjProjectAttachment record = new HlsCusPrjProjectAttachment();
        record.setProjectId(hlsCusPrjProject.getProjectId());
        record.setProjectAttachmentCategory("PRJ_PROJECT_JD");
        record.setDocumentName("法律审查意见");
        List<HlsCusPrjProjectAttachment> records = hlsCusPrjProjectAttachmentMapper.select(record);
        if(records.size() == 0){
            HlsCusPrjProjectAttachment item1 = new HlsCusPrjProjectAttachment();
            item1.setProjectId(hlsCusPrjProject.getProjectId());
            item1.setProjectAttachmentCategory("PRJ_PROJECT_JD");
            item1.setDocumentName("法律审查意见");
            item1.setUploadDate(new Date());
            item1.setUploadPerson(Long.toString(iRequest.getUserId()));
            hlsCusPrjProjectAttachmentService.insertSelective(iRequest, item1);
        }
        record.setDocumentName("风险评估报告");
        records = hlsCusPrjProjectAttachmentMapper.select(record);
        if(records.size() == 0){
            HlsCusPrjProjectAttachment item2 = new HlsCusPrjProjectAttachment();
            item2.setProjectId(hlsCusPrjProject.getProjectId());
            item2.setProjectAttachmentCategory("PRJ_PROJECT_JD");
            item2.setDocumentName("风险评估报告");
            item2.setUploadDate(new Date());
            item2.setUploadPerson(Long.toString(iRequest.getUserId()));
            hlsCusPrjProjectAttachmentService.insertSelective(iRequest, item2);
        }

        //设置参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusPrjProject.getProjectId().toString());
        variables.add(restVariable0);

        RestVariable restVariable1 = new RestVariable();
        restVariable1.setName("processDefinitionId");
        restVariable1.setValue(id);
        variables.add(restVariable1);
        RestVariable restVariable2 = new RestVariable();
        restVariable2.setName("startUserDescription");
        restVariable2.setValue(sysUser.getDescription());
        variables.add(restVariable2);
        RestVariable restVariable3 = new RestVariable();
        restVariable3.setName("iRequest");
        restVariable3.setValue(iRequest);
        variables.add(restVariable3);
        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("startUserName");
        restVariable4.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable4);
        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("hlsCusPrjProject");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusPrjProject));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(hlsCusPrjProject.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusPrjProject.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusPrjProject.getChanceId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("projectNumber");
        restVariable9.setValue(hlsCusPrjProject.getProjectNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("projectName");
        restVariable10.setValue(hlsCusPrjProject.getProjectName());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("companyId");
        restVariable12.setValue(hlsCusPrjProject.getCompanyId());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("hlsCusPrjProjectOld");
        JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectOld));
        restVariable13.setValue(jsonObject1.toString());
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("hlsCusPrjProjectNew");
        JSONObject jsonObject2 = JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectNew));
        restVariable14.setValue(jsonObject2.toString());
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("projectIdOld");
        restVariable15.setValue(hlsCusPrjProject.getRefProjectId());
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("projectIdNew");
        restVariable16.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable16);

        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("projectId");
        restVariable23.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable23);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("projectDocumentCategory");
        restVariable17.setValue("PRJ_PROJECT");
        variables.add(restVariable17);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("pName");
        restVariable18.setValue(name);
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("attType");
        restVariable19.setValue("PRJ_CREDIT_WFL");
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("documentName");
        restVariable20.setValue(hlsCusPrjProject.getProjectName());
        variables.add(restVariable20);

        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("documentNumber");
        restVariable22.setValue(hlsCusPrjProject.getProjectNumber());
        variables.add(restVariable22);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("unitId");
        restVariable21.setValue(hlsCusEmployee.getUnitId());
        variables.add(restVariable21);

        //财务报表头id,用来查询
        RestVariable restVariable25 = new RestVariable();
        restVariable25.setName("finStatementHdId");
        restVariable25.setValue(hlsCusPrjProject.getFinStatementHdId());
        variables.add(restVariable25);

        RestVariable restVariable27 = new RestVariable();
        restVariable27.setName("assistUnitId");
        restVariable27.setValue(hlsCusPrjProjectMapper.queryAssistUnitId(hlsCusPrjProject.getAssistProjectManager()));
        variables.add(restVariable27);

        RestVariable restVariable29 = new RestVariable();
        restVariable29.setName("versionFlag");
        restVariable29.setValue("N");
        variables.add(restVariable29);

        RestVariable restVariable35 = new RestVariable();
        restVariable35.setName("accountFlag");
        restVariable35.setValue("N");
        variables.add(restVariable35);
        //项目经理B（协办）
        RestVariable restVariable36 = new RestVariable();
        restVariable36.setName("projectAssistant");
        restVariable36.setValue(hlsCusPrjProject.getAssistProjectManager());
        variables.add(restVariable36);

        //工作流自定义参数：用于控制按钮
        RestVariable restVariable37 = new RestVariable();
        restVariable37.setName("butFlag");
        restVariable37.setValue("N");
        variables.add(restVariable37);

        //项目批复id
        RestVariable restVariable38 = new RestVariable();
        restVariable38.setName("approvalId");
        restVariable38.setValue(prjProjectApproval.getApprovalId());
        variables.add(restVariable38);

        RestVariable restVariable39 = new RestVariable();
        restVariable39.setName("financeAmount");
        restVariable39.setValue(hlsCusPrjProject.getFinanceAmount());
        variables.add(restVariable39);

        RestVariable restVariable40 = new RestVariable();
        restVariable40.setName("isLowrisk");
        restVariable40.setValue(hlsCusPrjProject.getIsLowRisk());
        variables.add(restVariable40);


        if("Y".equals(hlsCusPrjProject.getCreditFlag())){
            RestVariable restVariable42 = new RestVariable();
            restVariable42.setName("leaseItemAmount");
            restVariable42.setValue(hlsCusPrjProject.getLeaseItemAmount() == null ? 0 : hlsCusPrjProject.getLeaseItemAmount());
            variables.add(restVariable42);
        }

        //法务与风险指派岗跳过标记
        RestVariable restVariable43 = new RestVariable();
        restVariable43.setName("riskAppointSkip");
        restVariable43.setValue(hlsCusPrjProject.getRiskAllocationId() == null ? "N" : "Y");
        variables.add(restVariable43);
        RestVariable restVariable44 = new RestVariable();
        restVariable44.setName("legalAppointSkip");
        restVariable44.setValue(hlsCusPrjProject.getLegalAllocationId() == null ? "N" : "Y");
        variables.add(restVariable44);

        //是否授信项目
        RestVariable restVariable45 = new RestVariable();
        restVariable45.setName("creditFlag");
        restVariable45.setValue(hlsCusPrjProject.getCreditFlag());
        variables.add(restVariable45);

        RestVariable restVariable46 = new RestVariable();
        restVariable46.setName("dataType");
        restVariable46.setValue(hlsCusPrjProject.getDataType());
        variables.add(restVariable46);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
        String processInstanceId = (String) params.get("processInstanceId");
        long projectId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        HlsCusPrjProject  projectChange=new HlsCusPrjProject();
        projectChange.setProjectId(projectId);
        projectChange=prjProjectService.selectByPrimaryKey(iRequest,projectChange);

        //修改变更信息表中的申请状态
        HlsCusChangeReqInfo changeReqInfo = new HlsCusChangeReqInfo();
        changeReqInfo.setChangeReqId(projectChange.getChangeReqId());
        changeReqInfo.setDocumentCategory("PRJ_PROJECT");
        changeReqInfo.setChangeType(PRJ_AMOUNT_ALLOCATE);
        List<HlsCusChangeReqInfo> changeReqInfos = hlsCusChangeReqInfoService.select(iRequest, changeReqInfo, 1, 99999);
        HlsCusChangeReqInfo info = new HlsCusChangeReqInfo();
        if (changeReqInfos != null && changeReqInfos.size() == 1) {
            info.setStatus("CANCEL");
            hlsCusChangeReqInfoService.updateByPrimaryKeySelective(iRequest, info);
        }

        HlsCusPrjProject prjProjectOld = new HlsCusPrjProject();
        prjProjectOld.setProjectId(projectChange.getRefProjectId());
        prjProjectOld.setProjectStatus("APPROVED");
        prjProjectOld.setMeetingStatus("APPROVED");
        prjProjectService.updateByPrimaryKeySelective(iRequest, prjProjectOld);
    }

}
