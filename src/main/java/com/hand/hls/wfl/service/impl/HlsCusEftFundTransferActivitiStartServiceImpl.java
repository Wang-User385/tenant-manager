package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
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
 * @author yuanyuan 2019/07/25 7:58 PM
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusEftFundTransferActivitiStartServiceImpl implements IActivitiCommonService {


    private static final String workFlowType =  "MONEY_TRANSFERS_WFL";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusFundTransferService fundTransferService;
    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsCusCshPaymentReqHd cusCshPaymentReqHd=new HlsCusCshPaymentReqHd();
        cusCshPaymentReqHd.setPaymentReqId(Long.parseLong(businessKey));
        cusCshPaymentReqHd.setTransferStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, cusCshPaymentReqHd);
    }
}
