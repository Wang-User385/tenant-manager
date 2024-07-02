package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Yenick
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConPaymentActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CSH_PAYMENT_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;
    @Autowired
    private HlsCusConContractService service;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        // 回写工作流实例ID
        Long paymentReqId = (Long) params.get(IActivitiCommonService.BUSINESS_KEY);
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(paymentReqId);

        hlsCusCshPaymentReqHd.setPaymentProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest,hlsCusCshPaymentReqHd);

    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long paymentReqHdId = Long.parseLong(businessKey);
        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(paymentReqHdId);
        cshPaymentReqHd.setPaymentApprovedStatus("NEW");
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, cshPaymentReqHd);
    }
}
