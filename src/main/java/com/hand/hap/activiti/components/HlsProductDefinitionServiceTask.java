package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.service.IHlsProductDefinitionService;
import com.hand.hls.utils.HlsConstantUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HlsProductDefinitionServiceTask implements JavaDelegate, IActivitiBean {
    private static final String I_REQUEST = "iRequest";
    private static final String APPROVE_RESULT = "approveResult";
    private static final String START_USER_ID = "startUserId";

    private static final String HLS_PRODUCT_DEFINITION = "hlsProductDefinition";
    private static final String DEFINITION_ID = "definitionId";
    private static final String DEFINITION_CODE = "definitionCode";
    private static final String WORKFLOW_TYPE = "workFlowType";
    private static final String DOCUMENT_CATEGORY = "documentCategory";
    private static final String DOCUMENT_TYPE = "documentType";
    private static final String BUSINESS_TYPE = "businessType";
    private static final String DOCUMENT_ID = "documentId";
    private static final String DOCUMENT_NUMBER = "documentNumber";

    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    @Autowired
    private IHlsProductDefinitionService iHlsProductDefinitionService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable(I_REQUEST);
        String result = (String) delegateExecution.getVariable(APPROVE_RESULT);
//        String userId = String.valueOf(delegateExecution.getVariable(START_USER_ID));
//        requestCtx.setUserId(Long.valueOf(userId));
        String hlsProductDefinitionStr = (String) delegateExecution.getVariable(HLS_PRODUCT_DEFINITION);
        HlsProductDefinition hlsProductDefinition = JSON.parseObject(hlsProductDefinitionStr, HlsProductDefinition.class);
        hlsProductDefinition = iHlsProductDefinitionService.selectByPrimaryKey(requestCtx, hlsProductDefinition);
        // lock
//        databaseLockProvider.lock(hlsProductDefinition);
        //审批结束修改
        if (APPROVED.equalsIgnoreCase(result)) {
            hlsProductDefinition.setProductStatus(HlsConstantUtil.WorkFlowStatus.APPROVED);
            //审批通过产品启用
            hlsProductDefinition.setEnabledFlag("Y");
        } else if (REJECTED.equalsIgnoreCase(result)) {
            hlsProductDefinition.setProductStatus(HlsConstantUtil.WorkFlowStatus.REJECTED);
        }
        iHlsProductDefinitionService.updateByPrimaryKeySelective(requestCtx, hlsProductDefinition);
    }
}
