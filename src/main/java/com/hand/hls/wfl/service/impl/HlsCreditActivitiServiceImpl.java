package com.hand.hls.wfl.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fnd.dto.HlsEmployeeAssigns;
import com.hand.hls.fnd.mapper.HlsEmployeeAssignsMapper;
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
 * @Date: Created in 15:46 2018/10/12
 * @Description: 授信立项开始工作流程
 * @Description: copy from GDXF by yy.chen
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditActivitiServiceImpl  implements IActivitiCommonService{
    private static final String workFlowType = "CREDIT_CHANCE_CREATE_WFL";
    private static final String namespace = "CREDIT_CHANCE_CREATE_WFL";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsEmployeeAssignsMapper hlsEmployeeAssignsMapper;

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;
    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstance = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstance);
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(((HlsCusHlsCreditLineChance) list.get(0)).getChanceId());
        chance.setCreditLineStatus("APPROVING");
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(iRequest,chance);
    }

    @Override
    /**
     * 退回事件
     */
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(Long.parseLong(businessKey));
        chance.setCreditLineStatus("CANCEL");
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(iRequest,chance);
    }
}
