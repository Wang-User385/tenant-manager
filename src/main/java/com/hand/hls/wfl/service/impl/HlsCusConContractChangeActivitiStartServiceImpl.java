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
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huangzaipeng on 2018/04/19.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractChangeActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CON_CONTRACT_CHANGE_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusPrjProjectService prjProjectService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;

    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private BpCreditRateMapper bpCreditRateMapper;

    @Autowired
    private PrjProjectApprovalMapper prjProjectApprovalMapper;
    @Autowired
    private IProjectApprovalService projectApprovalService;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusPrjProject) list.get(0), iRequest,params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusPrjProject hlsCusPrjProject, IRequest iRequest,Map param) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CON_CONTRACT_CHANGE_WFL","CON_CONTRACT_CHANGE_WFL");
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

        hlsCusPrjProject = prjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);

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
        restVariable6.setValue(hlsCusPrjProjectOld.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusPrjProjectOld.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusPrjProjectOld.getChanceId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("projectNumber");
        restVariable9.setValue(hlsCusPrjProjectOld.getProjectNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("projectName");
        restVariable10.setValue(hlsCusPrjProjectOld.getProjectName());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);


        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("projectId");
        restVariable13.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable13);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("projectDocumentCategory");
        restVariable17.setValue("PRJ_PROJECT");
        variables.add(restVariable17);


        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("companyId");
        restVariable15.setValue(hlsCusPrjProject.getCompanyId());
        variables.add(restVariable15);


        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("hlsCusPrjProjectOld");
        JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectOld));
        restVariable19.setValue(jsonObject1.toString());
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("hlsCusPrjProjectNew");
        JSONObject jsonObject2 = JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectNew));
        restVariable20.setValue(jsonObject2.toString());
        variables.add(restVariable20);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("projectIdOld");
        restVariable21.setValue(hlsCusPrjProject.getRefProjectId());
        variables.add(restVariable21);

        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("projectIdNew");
        restVariable22.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable22);

        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("pName");
        restVariable23.setValue(name);
        variables.add(restVariable23);


        RestVariable restVariable25 = new RestVariable();
        restVariable25.setName("documentName");
        restVariable25.setValue(hlsCusPrjProject.getContractName());
        variables.add(restVariable25);

        RestVariable restVariable35 = new RestVariable();
        restVariable35.setName("documentNumber");
        restVariable35.setValue(hlsCusPrjProject.getProjectNumber());
        variables.add(restVariable35);

        RestVariable restVariable26 = new RestVariable();
        restVariable26.setName("unitId");
        restVariable26.setValue(hlsCusEmployee.getUnitId());
        variables.add(restVariable26);

        HlsCusEmployee managerAssign = new HlsCusEmployee();
        managerAssign.setEmployeeId(hlsCusPrjProject.getEmployeeId());
        managerAssign.setCompanyId(hlsCusPrjProject.getCompanyId());
        RestVariable restVariable27 = new RestVariable();
        restVariable27.setName("employeeManagerAssignsId");
        restVariable27.setValue(hlsCusEmployeeMapper.selectEmployeeAssignIdByEmployeeId(managerAssign).get(0).getEmployeeAssignId());
        variables.add(restVariable27);

        RestVariable restVariable28 = new RestVariable();
        restVariable28.setName("assistUnitId");
        restVariable28.setValue(hlsCusPrjProjectNew.getAssistUnitId());
        variables.add(restVariable28);


        RestVariable restVariable31 = new RestVariable();
        restVariable31.setName("withdrawFlag");
        restVariable31.setValue("N");
        variables.add(restVariable31);


        RestVariable restVariable32 = new RestVariable();
        restVariable32.setName("quotaFlag");
        String quotaFlag = "N";
        String businessType = hlsCusPrjProjectNew.getBusinessType();
        if(businessType.equals("GENERAL_CREDIT_APPROVAL") || businessType.equals("VENDOR_CREDIT_APPROVAL") || businessType.equals("UNION_CREDIT_APPROVAL")){
            //额度审批
            quotaFlag = "Y";
        }
        restVariable32.setValue(quotaFlag);
        variables.add(restVariable32);

        RestVariable restVariable34 = new RestVariable();
        restVariable34.setName("industryCategory");
        String industryCategory = hlsCusPrjProject.getIndustryCategory();
        restVariable34.setValue(industryCategory);
        variables.add(restVariable34);

        RestVariable restVariable37 = new RestVariable();
        restVariable37.setName("versionFlag");
        restVariable37.setValue("N");
        variables.add(restVariable37);

        RestVariable restVariable38 = new RestVariable();
        restVariable38.setName("refProcessInstanceId");
        restVariable38.setValue(hlsCusPrjProjectOld.getProcessInstanceId());
        variables.add(restVariable38);

        //项目经理B（协办）
        RestVariable restVariable39 = new RestVariable();
        restVariable39.setName("projectAssistant");
        restVariable39.setValue(hlsCusPrjProject.getAssistProjectManager());
        variables.add(restVariable39);

        RestVariable restVariable40 = new RestVariable();
        restVariable40.setName("financeAmount");
        restVariable40.setValue(hlsCusPrjProject.getFinanceAmount());
        variables.add(restVariable40);

        List<HlsCusPrjQuotation> hlsCusPrjQuotations = prjQuotationMapper.selectQuotationByProjectId(hlsCusPrjProject);
        HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusPrjQuotations.get(0);
        //融资金额
        RestVariable restVariable41 = new RestVariable();
        restVariable41.setName("leaseItemAmount");
        restVariable41.setValue(hlsCusPrjQuotation.getLeaseItemAmount());
        variables.add(restVariable41);

        RestVariable restVariable42 = new RestVariable();
        restVariable42.setName("changeType");
        restVariable42.setValue(param.get("changeType"));
        variables.add(restVariable42);

        RestVariable restVariable43 = new RestVariable();
        restVariable43.setName("meetFlag");
        restVariable43.setValue(param.get("meetFlag"));
        variables.add(restVariable43);

        RestVariable restVariable44 = new RestVariable();
        restVariable44.setName("changeReqId");
        restVariable44.setValue(param.get("changeReqId"));
        variables.add(restVariable44);

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

//        HlsCusPrjProject  hlsCusPrjProject=new HlsCusPrjProject();
//        hlsCusPrjProject.setProjectId(projectId);
//        hlsCusPrjProject.setProjectStatus("APPROVED");
//        hlsCusPrjProject=prjProjectService.updateByPrimaryKeySelective(iRequest,hlsCusPrjProject);

        //修改变更信息表中的申请状态，原状态新建
        HlsCusChangeReqInfo changeReqInfo = new HlsCusChangeReqInfo();
        changeReqInfo.setDocumentId(projectId);
        changeReqInfo.setDocumentCategory("PRJ_PROJECT");
        changeReqInfo.setStatus("APPROVING");
        List<HlsCusChangeReqInfo> changeReqInfos = hlsCusChangeReqInfoService.select(iRequest, changeReqInfo, 1, 99999);
        HlsCusChangeReqInfo info = new HlsCusChangeReqInfo();
        if (changeReqInfos != null && changeReqInfos.size() == 1) {
            info.setChangeReqId(changeReqInfos.get(0).getChangeReqId());
            info.setStatus("NEW");
            hlsCusChangeReqInfoService.updateByPrimaryKeySelective(iRequest, info);
        }
    }
}
