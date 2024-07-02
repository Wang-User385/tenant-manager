package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.eft.mapper.HlsCusFundTransferListMapper;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
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
 * description
 *
 * @author yuanyuan 2019/07/25 8:10 PM
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusEftFinanceTransferActivitiStartServiceImpl implements IActivitiCommonService {



    private static final String workFlowType = HlsCusConstant.TRANSFER_WFL.FINANCE_TRANSFER_WFL;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusFundTransferService fundTransferService;

    @Autowired
    private HlsCusFundTransferListMapper fundTransferListMapper;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        HlsCusFundTransfer fundTransfer = (HlsCusFundTransfer) list.get(0);
        params.put(WORK_FLOW_NAME, workFlowType);
        params.put(DEMO_NAME, "EFT_FUND");
        params.put(BUSINESS_KEY, fundTransfer.getTransferId());
        params.put("documentCategory", "EFT_FUND");
        params.put("documentName", fundTransfer.getApplyNumber());

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusFundTransfer fundTransfer, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef( HlsCusConstant.TRANSFER_WFL.FINANCE_TRANSFER_WFL,HlsCusConstant.TRANSFER_WFL.FUND_TRANSFER);
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(fundTransfer.getTransferId().toString());


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
        restVariable5.setName("hlsCusFundTransfer");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(fundTransfer));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("transferId");
        restVariable6.setValue(fundTransfer.getTransferId());
        variables.add(restVariable6);

        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("workflowType");
        restVariable7.setValue(workFlowType);
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("companyId");
        restVariable8.setValue(fundTransfer.getCompanyId());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("unitId");
        restVariable10.setValue(fundTransfer.getUnitId());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("businessType");
        restVariable11.setValue(fundTransfer.getBusinessType());
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("hasFinacceChange");
        int changeCount = fundTransferListMapper.selectHasChangeCount(fundTransfer.getTransferId());
        if(changeCount>0){
            restVariable12.setValue("true");
        }else{
            restVariable12.setValue("false");
        }
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("hasDateChange");
        String hasDateChange = "false";
        String isFund = "false";
        HlsCusFundTransferList hlsCusFundTransferList = new HlsCusFundTransferList();
        hlsCusFundTransferList.setFinTransferId(fundTransfer.getTransferId());
        List<HlsCusFundTransferList> fundList = fundTransferListMapper.select(hlsCusFundTransferList);
        for (HlsCusFundTransferList dt : fundList) {
            HlsCusFundTransfer hlsCusFundTransfer=new HlsCusFundTransfer();
            hlsCusFundTransfer.setTransferId(dt.getTransferId());
            hlsCusFundTransfer=fundTransferService.selectByPrimaryKey(iRequest,hlsCusFundTransfer);
            if("FUND".equalsIgnoreCase(hlsCusFundTransfer.getBusinessType())&&"PAY".equalsIgnoreCase(dt.getTransferType())){
                isFund="true";
                if(dt.getActualPayDate()!=null&&dt.getPlannedDueDate()!=null){
                    if (dt.getActualPayDate().before(dt.getPlannedDueDate()) || dt.getActualPayDate().after(dt.getPlannedDueDate())) {
                        hasDateChange = "true";
                    }
                }
            }
        }
        restVariable13.setValue(hasDateChange);
        variables.add(restVariable13);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("documentName");
        restVariable20.setValue(fundTransfer.getApplyNumber());
        variables.add(restVariable20);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("isFund");
        restVariable21.setValue(isFund);
        variables.add(restVariable21);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }



    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsCusFundTransfer fundTransfer=new HlsCusFundTransfer();
        fundTransfer.setTransferId(Long.parseLong(businessKey));
        fundTransfer.setApplyStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
        fundTransferService.updateByPrimaryKeySelective(iRequest, fundTransfer);
    }
}
