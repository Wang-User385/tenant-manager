package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
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
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:关税付款申请
 * @author: congweijing
 * @date: 2021/5/18 09:51
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusTariffCshPaymentReqActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "TARIFF_PAYMENT_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;



    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusCshPaymentReqHd)list.get(0),iRequest);

        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("TARIFF_PAYMENT_WFL","TARIFF_PAYMENT_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusCshPaymentReqHd.getPaymentReqId().toString());
        //查询合同详情
        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusCshPaymentReqHd.getSourceContractId());
        if(hlsCusConContract == null){
            throw new IllegalArgumentException("合同信息获取失败，请联系IT人员！");
        }
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

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(hlsCusCshPaymentReqHd.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusCshPaymentReqHd.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusCshPaymentReqHd.getPaymentReqId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("contractNumber");
        restVariable10.setValue(hlsCusConContract.getContractNumber());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(hlsCusConContract.getContractName());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentNumber");
        restVariable13.setValue(hlsCusCshPaymentReqHd.getPaymentReqNumber());
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("contractId");
        restVariable14.setValue(hlsCusConContract.getContractId());
        variables.add(restVariable14);

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusCshPaymentReqHd.getPaymentReqId());
        variables.add(restVariable0);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(id);
        hlsCusCshPaymentReqHd.setPaymentReqStatus(CANCEL_STATUS);
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest,hlsCusCshPaymentReqHd);
    }

}
