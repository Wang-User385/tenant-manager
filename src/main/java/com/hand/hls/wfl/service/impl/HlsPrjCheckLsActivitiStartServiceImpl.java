package com.hand.hls.wfl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xuju on 2018/04/19.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsPrjCheckLsActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "RENT_CHECK_LS_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private IPrjCheckService prjCheckService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    HlsCusPrjProjectMapper projectMapper;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        PrjCheck prjCheck = (PrjCheck) list.get(0);
        Long assistProjectManager = projectMapper.queryCreditAssistProjectManager(prjCheck.getManufacturerId());
        HlsCusBpMaster hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(prjCheck.getManufacturerId());
        String documentName = hlsCusBpMaster.getBpName() + "-零售业务租后检查流程";
        params.put(WORK_FLOW_NAME, "RENT_CHECK_LS_WFL");
        params.put(DEMO_NAME, "RENT_CHECK_LS_WFL");
        params.put(BUSINESS_KEY, prjCheck.getCheckId());
        params.put("documentCategory", "RENT_CHECK_WFL");
        params.put("documentName", documentName);
        params.put("documentNumber", prjCheck.getCheckNumber());
        params.put("projectAssistant", assistProjectManager);
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long projectId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        PrjCheck prjCheck=new PrjCheck();
        prjCheck.setApproveSuggest("NEW");
        prjCheck.setCheckId(projectId);
        prjCheckService.updateByPrimaryKeySelective(iRequest,prjCheck);
    }
}
