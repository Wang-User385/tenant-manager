package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.inv.dto.HlsCusFinanceAttachment;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.mapper.HlsCusFinanceAttachmentMapper;
import com.hand.hls.inv.service.HlsCusIFinanceAttachmentService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
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
 * @Description:投资理财申购追加工作流启动
 * @Author: wty
 * @Date: Created in 10:34 2018/4/26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusInvPurchaseAddActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "INV_PURCHASE_ADD_WFL";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusIFinancePurchaseService service;
    @Autowired
    private HlsCusFinanceAttachmentMapper attachmentMapper;
    @Autowired
    private HlsCusIFinanceAttachmentService attachmentService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(iRequest, (HlsCusFinancePurchase) list.get(0));
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(IRequest iRequest, HlsCusFinancePurchase purchase) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("INV_PURCHASE_ADD_WORK_FLOW", "INVESTMENT");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(purchase.getFinancePurchaseId().toString());

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
        restVariable4.setName("hlsCusPurchase");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(purchase));
        restVariable4.setValue(jsonObject.toString());
        variables.add(restVariable4);

        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("startUserDescription");
        restVariable5.setValue(sysUser.getDescription());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(purchase.getDocumentCategory());
        variables.add(restVariable6);

        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(purchase.getDocumentType());
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("purchaseNumber");
        restVariable8.setValue(purchase.getPurchaseNumber());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("companyId");
        restVariable10.setValue(purchase.getCompanyId().toString());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workflowType");
        restVariable11.setValue(WORK_FLOW_TYPE);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(purchase.getPurchaseNumber());
        variables.add(restVariable12);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String status = "NEW";
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusFinancePurchase financePurchase = new HlsCusFinancePurchase();
        financePurchase.setFinancePurchaseId(id);
        financePurchase.setPurchaseStatus("NEW");
        service.updateByPrimaryKeySelective(iRequest, financePurchase);
        HlsCusFinanceAttachment attachment = new HlsCusFinanceAttachment();
        attachment.setStatus(status);
        attachment.setPurchaseId(id);
        attachment.setMakeUp("NORMAL");
        List<HlsCusFinanceAttachment> attachments = attachmentMapper.queryAll(attachment);
        if (CollectionUtils.isNotEmpty(attachments)) {
            for (int i = 0; i < attachments.size(); i++) {
                attachments.get(i).setStatus(status);
                attachments.get(i).set__status("update");
            }
            attachmentService.batchUpdate(iRequest, attachments);
        }

    }
}
