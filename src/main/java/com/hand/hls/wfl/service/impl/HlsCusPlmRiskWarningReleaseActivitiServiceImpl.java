package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:风险预警解除工作流
 * @Author: wty
 * @Date: Created in 10:34 2018/5/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmRiskWarningReleaseActivitiServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "PLM_RW_RELEASE_WORK_FLOW";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusIRiskWarningService hlsCusIRiskWarningService;
    @Autowired
    private HlsCusFiveClassificationMapper fiveClassificationMapper;
    @Autowired
    private HlsCusIFiveClassificationService fiveClassificationService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(iRequest, (HlsCusRiskWarning) list.get(0));
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        riskWarning.setFiveClassification(null);
        riskWarning.setRiskWarningInfo(null);
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("RW_RISK_WARNING_RELEASE_WORK_FLOW", "PLMANAGEMENT");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(riskWarning.getRiskWarningId().toString());

        //工作流参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();

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
        restVariable4.setName("riskWarning");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(riskWarning));
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
        restVariable8.setValue(riskWarning.getRiskWarningId().toString());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("plmType");
        restVariable9.setValue("RW");
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("companyId");
        restVariable10.setValue(iRequest.getCompanyId().toString());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("documentName");
        restVariable11.setValue(riskWarning.getRiskWarningNumber());
        variables.add(restVariable11);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
        String processInstanceId = (String) params.get("processInstanceId");
        long riskWarningId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        HlsCusRiskWarning returnRiskWarning = new HlsCusRiskWarning();
        returnRiskWarning.setRiskWarningId(riskWarningId);
        returnRiskWarning = hlsCusIRiskWarningService.selectByPrimaryKey(iRequest, returnRiskWarning);
        returnRiskWarning.setStatus("APPROVED");
        returnRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest, returnRiskWarning);

        //更新对应的五级分类
        HlsCusFiveClassification fiveClassification = new HlsCusFiveClassification();
        fiveClassification.setBelongsToId(returnRiskWarning.getRiskWarningId());
        fiveClassification.setCompanyId(iRequest.getCompanyId());
        fiveClassification.setFiveClassificationType("RW");
        List<HlsCusFiveClassification> fcList = fiveClassificationMapper.selectFiveClassficationByBelongsToId(fiveClassification);
        if (CollectionUtils.isNotEmpty(fcList)) {
            HlsCusFiveClassification cusFiveClassification = new HlsCusFiveClassification();
            cusFiveClassification.setFiveClassificationId(fcList.get(0).getFiveClassificationId());
            cusFiveClassification.setStatus("APPROVED");
            fiveClassificationService.updateByPrimaryKeySelective(iRequest, cusFiveClassification);
        }
    }
}
