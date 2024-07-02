package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.dto.ActHiTaskinst;
import com.hand.hap.activiti.service.ActHiTaskinstService;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.service.ICshDepositDeductReqHdService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CshDepositDeductReqActivitiStartServiceImpl implements IActivitiCommonService {
    //获取实现类
    private static final String WORK_FLOW_TYPE = "CSH_DEPOSIT_DEDUCT_REQ";

    //对应工作流页面配置的唯一标志
    private static final String WORK_FLOW_KEY = "CSH_DEPOSIT_DEDUCT_REQ";
    //对应工作流页面配置的分类
    private static final String DEMO = "CSH";

    private static final String BUSINESS_KEY = "BUSINESS_KEY";

    @Autowired
    private IActivitiService iActivitiService;
    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    private ICshDepositDeductReqHdService iCshDepositDeductReqHdService;

    @Autowired
    private ActHiTaskinstService actHiTaskinstService;

    @Autowired
    private HistoryService historyService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        //设置主键
        map.put(WORK_FLOW_NAME, WORK_FLOW_KEY);
        map.put(DEMO_NAME, DEMO);
        map.put(BUSINESS_KEY, ((CshDepositDeductReqHd) list.get(0)).getReqHdId());

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, map);

        ProcessInstanceResponse processInstanceResponse = iActivitiService.startProcess(iRequest, processInstanceCreateRequest);
        //保证金抵扣申请流程自动审批完成后没有插入act_hi_taskinst导致流程详情审批表单无法展示，现手动插入
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        HistoricProcessInstance historicProcessInstance = historicProcessInstanceQuery.processInstanceId(processInstanceResponse.getId()).singleResult();
        ActHiTaskinst actHiTaskinst = new ActHiTaskinst();
        actHiTaskinst.setFormKey("modules/CSH/CSH_TRX/CSH308/csh_deposit_deduction_wfl.lview");
        actHiTaskinst.setProcInstId(processInstanceResponse.getId());
        actHiTaskinst.setProcDefId(processInstanceResponse.getProcessDefinitionId());
        actHiTaskinst.setStartTime(historicProcessInstance.getStartTime());
        actHiTaskinst.setEndTime(historicProcessInstance.getEndTime());
        actHiTaskinstService.insertSelective(iRequest, actHiTaskinst);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        iRequest.setAttribute("authorityRuleFlag", "N");
        String businessKey = (String) params.get("businessKey");
        Long reqHdId = Long.parseLong(businessKey);

        CshDepositDeductReqHd cshDepositDeductReqHd = new CshDepositDeductReqHd();
        cshDepositDeductReqHd.setReqHdId(reqHdId);

        cshDepositDeductReqHd = iCshDepositDeductReqHdService.selectByPrimaryKey(iRequest, cshDepositDeductReqHd);

        iCshDepositDeductReqHdService.beforeDeduct(iRequest, cshDepositDeductReqHd, HlsConstantUtil.WorkFlowStatus.CANCEL);

        iCshDepositDeductReqHdService.releaseAmount(iRequest, reqHdId);
    }
}
