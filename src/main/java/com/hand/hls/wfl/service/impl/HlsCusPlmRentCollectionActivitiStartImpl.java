package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fct.dto.HlsCusFctContract;
import com.hand.hls.fin.service.HlsCusFctContractService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * description
 *
 * @author yuanyuan 2019/03/25 8:26 PM
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmRentCollectionActivitiStartImpl implements IActivitiCommonService {

    private static final String workFlowType = "PLM_RENT_COLLECTION_WFL";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusFctContractService hlsCusFctContractService;



    private static final Long ZERO = 0L;
    private static final String Y = "Y";
    private static final String N = "N";



    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
       getProcessInstanceCreateRequest((HlsCusRentCollectionRule)list.get(0),iRequest);

      //  ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusRentCollectionRule)list.get(0),iRequest);
      //  activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private void getProcessInstanceCreateRequest(HlsCusRentCollectionRule rentCollectionRule, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("RENT_COLLECTION_WFL","RENT_COLLECTION_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(rentCollectionRule.getCollectionRuleId().toString());
        //设置参数
        Map map = new HashMap();
        map.put("businessKey",rentCollectionRule.getCollectionRuleId().toString());
        map.put("employeeAssignsId",rentCollectionRule.getEmployeeAssignId());
        map.put("assistantAssignsId",rentCollectionRule.getProjectAssistantAssignsId());
        map.put("companyId",rentCollectionRule.getCompanyId());
        map.put("processDefinitionId",id);
        map.put("iRequest",iRequest);
        map.put("startUserName",iRequest.getEmployeeCode());
        map.put("startUserName", iRequest.getEmployeeCode());
        map.put("rentCollectionRule", JSON.toJSONString(rentCollectionRule));
        map.put("documentCategory",rentCollectionRule.getContractDocCategory());
        map.put("documentType",rentCollectionRule.getContractDocCategory());
        map.put("documentId",rentCollectionRule.getCollectionRuleId());
        map.put("contractId",rentCollectionRule.getContractId());
        map.put("documentName",rentCollectionRule.getContractName());
        map.put("contractNumber",rentCollectionRule.getContractNumber());
        map.put("pName",name);
        map.put("workflowType",workFlowType);
        List<Long> employeeAssignIdList = new ArrayList<>();
        employeeAssignIdList.add(rentCollectionRule.getEmployeeAssignId());
        map.put("employeeAssignIdList", employeeAssignIdList);
        if ("CON_CONTRACT".equalsIgnoreCase(rentCollectionRule.getContractDocCategory())) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(rentCollectionRule.getContractId());
            hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest, hlsCusConContract);
            map.put("unitId", hlsCusConContract.getUnitId());
        } else if ("FCT_CONTRACT".equalsIgnoreCase(rentCollectionRule.getContractDocCategory())) {
            HlsCusFctContract hlsCusFctContract = new HlsCusFctContract();
            hlsCusFctContract.setContractId(rentCollectionRule.getContractId());
            hlsCusFctContract = hlsCusFctContractService.selectByPrimaryKey(iRequest, hlsCusFctContract);
            map.put("unitId", hlsCusFctContract.getUnitId());
        }

        Long allocationId = 144L;
//        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
//        identityService.setAuthenticatedUserId(iRequest.getEmployeeCode());
        identityService.setAuthenticatedUserId(String.valueOf(allocationId));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("RENT_COLLECTION_WFL",rentCollectionRule.getCollectionRuleId().toString(),map);
        //insertInstanceToOAFromStart(Long.valueOf(processInstance.getId()),iRequest);


        //工作流参数
//        List<RestVariable> variables = new ArrayList<RestVariable>();
//        List<RestVariable> transientVariables = new ArrayList<RestVariable>();
//
//        RestVariable restVariable1 = new RestVariable();
//        restVariable1.setName("processDefinitionId");
//        restVariable1.setValue(id);
//        variables.add(restVariable1);
//
//        RestVariable restVariable2 = new RestVariable();
//        restVariable2.setName("iRequest");
//        restVariable2.setValue(iRequest);
//        variables.add(restVariable2);
//
//        RestVariable restVariable3 = new RestVariable();
//        restVariable3.setName("startUserName");
//        restVariable3.setValue(iRequest.getEmployeeCode());
//        variables.add(restVariable3);
//
//        RestVariable restVariable4 = new RestVariable();
//        restVariable4.setName("rentCollectionRule");
//        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(rentCollectionRule));
//        restVariable4.setValue(jsonObject.toString());
//        variables.add(restVariable4);
//
//        RestVariable restVariable5 = new RestVariable();
//        restVariable5.setName("startUserDescription");
//        restVariable5.setValue(sysUser.getDescription());
//        variables.add(restVariable5);
//
//        RestVariable restVariable6 = new RestVariable();
//        restVariable6.setName("pName");
//        restVariable6.setValue(name);
//        variables.add(restVariable6);
//
//
//        RestVariable restVariable7 = new RestVariable();
//        restVariable7.setName("documentCategory");
//        restVariable7.setValue(rentCollectionRule.getContractDocCategory());
//        variables.add(restVariable7);
//
//        RestVariable restVariable8 = new RestVariable();
//        restVariable8.setName("documentType");
//        restVariable8.setValue(rentCollectionRule.getContractDocCategory());
//        variables.add(restVariable8);
//
//        RestVariable restVariable9 = new RestVariable();
//        restVariable9.setName("documentId");
//        restVariable9.setValue(rentCollectionRule.getCollectionRuleId());
//        variables.add(restVariable9);
//
//        RestVariable restVariable10 = new RestVariable();
//        restVariable10.setName("companyId");
//        restVariable10.setValue(iRequest.getCompanyId().toString());
//        variables.add(restVariable10);
//
//        RestVariable restVariable11 = new RestVariable();
//        restVariable11.setName("workflowType");
//        restVariable11.setValue(workFlowType);
//        variables.add(restVariable11);
//
//        RestVariable restVariable12 = new RestVariable();
//        restVariable12.setName("documentName");
//        restVariable12.setValue(rentCollectionRule.getContractName());
//        variables.add(restVariable12);
//
//        RestVariable restVariable13 = new RestVariable();
//        restVariable13.setName("employeeCode");
//        restVariable13.setValue(iRequest.getEmployeeCode());
//        variables.add(restVariable13);
//
//
//        RestVariable restVariable14 = new RestVariable();
//        restVariable14.setName("contractNumber");
//        restVariable14.setValue("rentCollectionRule.getContractNumber()");
//        variables.add(restVariable14);
//
//
//        Long unitId=0L;
//        if ("CON_CONTRACT".equalsIgnoreCase(rentCollectionRule.getContractDocCategory())) {
//            HlsCusConContract hlsCusConContract = new HlsCusConContract();
//            hlsCusConContract.setContractId(rentCollectionRule.getContractId());
//            hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest, hlsCusConContract);
//            //map.put("unitId", hlsCusConContract.getUnitId());
//            unitId=hlsCusConContract.getUnitId();
//        } else if ("FCT_CONTRACT".equalsIgnoreCase(rentCollectionRule.getContractDocCategory())) {
//            HlsCusFctContract hlsCusFctContract = new HlsCusFctContract();
//            hlsCusFctContract.setContractId(rentCollectionRule.getContractId());
//            hlsCusFctContract = hlsCusFctContractService.selectByPrimaryKey(iRequest, hlsCusFctContract);
//            //map.put("unitId", hlsCusFctContract.getUnitId());
//            unitId=hlsCusFctContract.getUnitId();
//        }
//        RestVariable restVariable15 = new RestVariable();
//        restVariable15.setName("unitId");
//        restVariable15.setValue(unitId);
//        variables.add(restVariable15);
//
//        RestVariable restVariable16 = new RestVariable();
//        restVariable16.setName("businessKey");
//        restVariable16.setValue(rentCollectionRule.getCollectionRuleId().toString());
//        variables.add(restVariable16);
//
//        RestVariable restVariable17 = new RestVariable();
//        restVariable17.setName("employeeAssignsId");
//        restVariable17.setValue(rentCollectionRule.getEmployeeAssignId());
//        variables.add(restVariable17);
//
//
//        RestVariable restVariable18 = new RestVariable();
//        restVariable18.setName("assistantAssignsId");
//        restVariable18.setValue(rentCollectionRule.getProjectAssistantAssignsId());
//        variables.add(restVariable18);
//
//
//        createRequest.setVariables(variables);
//        createRequest.setTransientVariables(transientVariables);
//
//        return createRequest;
    }



    @Override
    public void cancel(IRequest iRequest, Map params) {

    }

//    private void insertInstanceToOAFromStart(Long instanceId, IRequest iRequest) {
//        OADealsInterface oaDealsInterface = new OADealsInterface();
//        oaDealsInterface.setInstanceId(instanceId);
//        List<OADealsInterface> oaDealsInterfaceList = new ArrayList<>();
//        oaDealsInterfaceList = oaDealsInterfaceMapper.queryToDoList(oaDealsInterface);
//        if (CollectionUtils.isNotEmpty(oaDealsInterfaceList)) {
//            //流程开始 将recordId未存在于接口表中的记录推送至OA接口表中
//            for (OADealsInterface dto : oaDealsInterfaceList) {
//                if (dto.getExistFlag().equalsIgnoreCase(N)) {
//                    UUID uuid = UUID.randomUUID();
//                    oaDealsInterface.setToken(String.valueOf(uuid).replace("-", ""));
//                    oaDealsInterface.setUserId(dto.getApproverUserId());
//                    oaDealsInterface.setCompanyId(iRequest.getCompanyId());
//                    oaDealsInterface.setRecordId(dto.getRecordId());
////                    oaDealsInterface.setRoleId(iRequest.getRoleId());
//                    oaDealsInterface.setDoneFlag(N);
//                    oaDealsInterface.setPostFlag(N);
//                    oaDealsInterface.setPostErrorTimes(ZERO);
//                    oaDealsInterface.setDoneErrorTimes(ZERO);
//                    oaDealsInterfaceService.insert(iRequest, oaDealsInterface);
//                }
//            }
//        }
//    }
}
