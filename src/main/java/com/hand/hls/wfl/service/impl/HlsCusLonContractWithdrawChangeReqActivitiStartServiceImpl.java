package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.service.HlsCusLonContractService;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
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
 * Created by zhangyu on 2018/7/23.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractWithdrawChangeReqActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CT_LON_CON_WITHDRAW_CHANGE_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private HlsCusLonContractWithdrawService lonContractWithdrawService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusLonContractService hlsCusLonContractService;
    @Autowired
    private HlsCusLonContractMapper hlsCusLonContractMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;
    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusLonContractWithdraw)list.get(0),iRequest);

        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusLonContractWithdraw lonContractWithdraw, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs;
        reProcdefs = reProcdefService.queryReProcdef("CT_LON_CON_WITHDRAW_CHANGE_WFL", "LON_CONTRACT_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(lonContractWithdraw.getWithdrawId().toString());
        //设置参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();
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
        restVariable5.setName("lonContractWithdraw");
        JSONObject jsonObject= JSON.parseObject(JSON.toJSONString(lonContractWithdraw));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(lonContractWithdraw.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(lonContractWithdraw.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(lonContractWithdraw.getWithdrawId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("withdrawNumber");
        restVariable10.setValue(lonContractWithdraw.getWithdrawNumber());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
        hlsCusLonContract.setContractId(lonContractWithdraw.getContractId());
        hlsCusLonContract = hlsCusLonContractMapper.selectByPrimaryKey(hlsCusLonContract);

        String documentName = "";
        if(hlsCusLonContract != null){
            HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
            hlsCusBpMaster.setBpId(hlsCusLonContract.getCreditBpId());
            hlsCusBpMaster = hlsCusBpMasterService.selectByPrimaryKey(iRequest,hlsCusBpMaster);
            if(hlsCusBpMaster != null){
                documentName = hlsCusBpMaster.getBpName();
            }
        }
        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(documentName);
        variables.add(restVariable12);
        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentNumber");
        restVariable13.setValue(lonContractWithdraw.getWithdrawNumber());
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("changeReqId");
        restVariable14.setValue(lonContractWithdraw.getChangeReqId());
        variables.add(restVariable14);

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setEmployeeCode(iRequest.getEmployeeCode());
        hlsCusEmployee.setCompanyId(iRequest.getCompanyId());
        hlsCusEmployee = hlsCusEmployeeMapper.selectUnitIdByEmployeeCodeAndCompanyId(hlsCusEmployee).get(0);
        RestVariable restVariable15= new RestVariable();
        restVariable15.setName("unitId");
        restVariable15.setValue(Long.valueOf(hlsCusEmployee.getUnitId()));
        variables.add(restVariable15);
        RestVariable restVariable16= new RestVariable();
        restVariable16.setName("companyId");
        restVariable16.setValue(iRequest.getCompanyId());
        variables.add(restVariable16);

        RestVariable restVariable17= new RestVariable();
        restVariable17.setName("BUSINESS_KEY");
        restVariable17.setValue(lonContractWithdraw.getWithdrawId());
        variables.add(restVariable17);

        RestVariable restVariable18= new RestVariable();
        restVariable18.setName("oldWithdrawId");
        restVariable18.setValue(lonContractWithdraw.getOldWithdrawId());
        variables.add(restVariable18);

        RestVariable restVariable19= new RestVariable();
        restVariable19.setName("contractId");
        restVariable19.setValue(lonContractWithdraw.getContractId());
        variables.add(restVariable19);

        RestVariable restVariable20= new RestVariable();
        restVariable20.setName("oldContractId");
        restVariable20.setValue(lonContractWithdraw.getOldContractId());
        variables.add(restVariable20);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);//change_req的withdrawId
        long prcId = Long.parseLong(processInstanceId);
        HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
        lonContractWithdraw.setWithdrawId(id);
        lonContractWithdraw.setWithdrawStatus("NEW");
        lonContractWithdraw = lonContractWithdrawService.updateByPrimaryKeySelective(iRequest,lonContractWithdraw);
    }

}
