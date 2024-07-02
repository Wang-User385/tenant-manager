package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.mapper.HlsCusPostloanInspectionMapper;
import com.hand.hls.plm.pli.mapper.PlmPliContractMapper;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:贷后检查工作流
 * @Author: wty
 * @Date: Created in 10:34 2018/5/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmPostloanInspectionActivitiServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "PLM_PLI_WORK_FLOW";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusIPostloanInspectionService service;

    @Autowired
    private HlsCusPostloanInspectionMapper hlsCusPostloanInspectionMapper;

    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Autowired
    private PlmPliContractMapper plmPliContractMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    private static final String nameSpace = "PLMANAGEMENT";

    private static final String REGULAR_ON = "PLM_PLI_REGULAR_ON_WFL";
    private static final String REGULAR_OFF = "PLM_PLI_REGULAR_OFF_WFL";
    private static final String IRREGULAR_ON = "PLM_PLI_IRREGULAR_ON_WFL";
    private static final String IRREGULAR_OFF = "PLM_PLI_IRREGULAR_OFF_WFL";
    private static final String PLANE_ON = "PLM_PLI_PLANE_ON_WFL";
    private static final String PLANE_OFF = "PLM_PLI_PLANE_OFF_WFL";

    private static final String REGULAR = "REGULAR";
    private static final String IRREGULAR = "IRREGULAR";

    private static final String ON_SITE_INSPECT = "ON_SITE_INSPECT";
    private static final String OFF_SITE_INSPECT = "OFF_SITE_INSPECT";
    private static final String ALL = "ALL";
    private static final String Y = "Y";

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(iRequest, (HlsCusPostloanInspection) list.get(0));
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(IRequest iRequest, HlsCusPostloanInspection postloanInspection) {
        //选择开启的流程
        postloanInspection = hlsCusPostloanInspectionMapper.selectByPrimaryKey(postloanInspection);
        String code = null;
        if (REGULAR.equalsIgnoreCase(postloanInspection.getInspectionMethod())) {
            if(Y.equalsIgnoreCase(postloanInspection.getIsPlaneCheck())){
                code = PLANE_OFF;
            }else{
                if (ON_SITE_INSPECT.equalsIgnoreCase(postloanInspection.getInspectionType()) || ALL.equalsIgnoreCase(postloanInspection.getInspectionType())) {
                    code = REGULAR_ON;
                } else if (OFF_SITE_INSPECT.equalsIgnoreCase(postloanInspection.getInspectionType())) {
                    code = REGULAR_OFF;
                }
            }

        } else if (IRREGULAR.equalsIgnoreCase(postloanInspection.getInspectionMethod())) {
            if (Y.equalsIgnoreCase(postloanInspection.getIsPlaneCheck())) {
                code = PLANE_ON;
            } else {
                if (ON_SITE_INSPECT.equalsIgnoreCase(postloanInspection.getInspectionType())) {
                    code = IRREGULAR_ON;
                } else if (OFF_SITE_INSPECT.equalsIgnoreCase(postloanInspection.getInspectionType())) {
                    code = IRREGULAR_OFF;
                }
            }
        }

        if (StringUtils.isBlank(code)) {
            try {
                throw new Exception("无法选择启动的流程CODE");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        //开启流程 PLI_POSTLOAN_INSPECTION_WORK_FLOW
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(code, code);
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(postloanInspection.getPostloanInspectionId().toString());

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(iRequest.getCompanyId());
        hlsCusEmployee.setEmployeeCode(iRequest.getEmployeeCode());
        List<HlsCusEmployee> list=hlsCusEmployeeMapper.selectUnitIdByEmployeeCodeAndCompanyId(hlsCusEmployee);
        if (!list.isEmpty())
        {
            hlsCusEmployee = list.get(0);
        }
        postloanInspection.setFiveClassification(null);
        postloanInspection.setPostloanInspectionConclusionHList(null);
        //工作流参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(postloanInspection.getPostloanInspectionId().toString());
        variables.add(restVariable0);

        RestVariable restVariable1 = new RestVariable();
        restVariable1.setName("processDefinitionId");
        restVariable1.setValue(id);
        variables.add(restVariable1);

        RestVariable restVariable2 = new RestVariable();
        restVariable2.setName("iRequest");
        restVariable2.setValue(iRequest);
        variables.add(restVariable2);

        RestVariable restVariable3 = new RestVariable();
        restVariable3.setName("startUserName");
        restVariable3.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable3);

        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("postloanInspection");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(postloanInspection));
        restVariable4.setValue(jsonObject.toString());
        variables.add(restVariable4);

        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("startUserDescription");
        restVariable5.setValue(sysUser.getDescription());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("pName");
        restVariable6.setValue(name);
        variables.add(restVariable6);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("postLoanId");
        restVariable8.setValue(postloanInspection.getPostloanInspectionId().toString());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("plmType");
        restVariable9.setValue("PLI");
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("companyId");
        restVariable10.setValue(iRequest.getCompanyId().toString());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(WORK_FLOW_TYPE);
        variables.add(restVariable11);

        //将documentName设置成商业伙伴名称
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(postloanInspection.getBpId());
        hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        StringBuilder sb = new StringBuilder();
        sb.append(hlsCusBpMaster.getBpName());
        sb.append(postloanInspection.getInspectionYear());
        sb.append("年");
        sb.append(postloanInspection.getInspectionMonth());
        sb.append("月贷后检查");
        restVariable12.setValue(sb.toString());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentCategory");
        restVariable13.setValue(postloanInspection.getDocumentCategory());
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("documentType");
        restVariable14.setValue(postloanInspection.getDocumentType());
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("businessType");
        restVariable15.setValue(postloanInspection.getBusinessType());
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("unitId");
        restVariable16.setValue(hlsCusEmployee.getUnitId());
        variables.add(restVariable16);

        //查询项目经理
        List<Long> employeeAssignId =plmPliContractMapper.selectContractEmployAssignId(postloanInspection.getPostloanInspectionId());
        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("employeeAssignIdList");
        restVariable17.setValue(JSON.toJSONString(employeeAssignId));
        variables.add(restVariable17);

        List<Long> unitIdList = plmPliContractMapper.selectContractUnitId(postloanInspection.getPostloanInspectionId());

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("unitIdList");
        restVariable18.setValue(unitIdList);
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("isPlaneCheck");
        restVariable19.setValue(postloanInspection.getIsPlaneCheck());
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("inspectionMethod");
        restVariable20.setValue(postloanInspection.getInspectionMethod());
        variables.add(restVariable20);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("inspectionType");
        restVariable21.setValue(postloanInspection.getInspectionType());
        variables.add(restVariable21);

        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("finStatementHdId");
        restVariable22.setValue(postloanInspection.getFinStatementHdId());
        variables.add(restVariable22);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        HlsCusPostloanInspection dto = new HlsCusPostloanInspection();
        dto.setPostloanInspectionId(id);
        dto.setStatus("NEW");
        service.updateByPrimaryKeySelective(iRequest, dto);
    }
}
