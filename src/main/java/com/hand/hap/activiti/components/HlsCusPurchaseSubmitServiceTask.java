package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.mapper.HlsCusFinanceAttachmentMapper;
import com.hand.hls.inv.mapper.HlsCusFinancePurchaseMapper;
import com.hand.hls.inv.service.HlsCusIFinanceAttachmentService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseDetailIdBakService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:申购审批结束
 * @Author: wty
 * @Date: Created in 14:19 2018/4/26
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPurchaseSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusIFinancePurchaseService hlsCusIFinancePurchaseService;

    @Autowired
    private HlsCusFinancePurchaseMapper hlsCusFinancePurchaseMapper;

    @Autowired
    private HlsCusIFinanceAttachmentService attachmentService;

    @Autowired
    private HlsCusFinanceAttachmentMapper attachmentMapper;

    @Autowired
    private HlsCusIFinancePurchaseDetailIdBakService detailIdBakService;

    @Autowired
    private SysEventService sysEventService;

    public HlsCusPurchaseSubmitServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long documentId = (Long) delegateExecution.getVariable("documentId");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String hlsCusPurchasePrams = (String) delegateExecution.getVariable("hlsCusPurchase");
        HlsCusFinancePurchase purchase = JSON.parseObject(hlsCusPurchasePrams, HlsCusFinancePurchase.class);
        HlsCusFinancePurchase resultPurchase = new HlsCusFinancePurchase();
        Long purchaseId =  purchase.getFinancePurchaseId();
        resultPurchase.setFinancePurchaseId(purchaseId);
       // resultPurchase.setFinancePurchaseId(documentId);
        resultPurchase = hlsCusIFinancePurchaseService.selectByPrimaryKey(requestCtx, resultPurchase);
        String flagInfo = "";

        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            flagInfo = "审批通过";
            resultPurchase.setPurchaseStatus(flag);
            hlsCusIFinancePurchaseService.updateByPrimaryKeySelective(requestCtx, resultPurchase);


        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            flag = "APPROVED_RETURN";
            flagInfo = "审批退回";
            flag = "APPROVED_RETURN";
            resultPurchase.setPurchaseStatus(flag);
            hlsCusIFinancePurchaseService.updateByPrimaryKeySelective(requestCtx, resultPurchase);
        }

        Map<String, Object> evenParams = new HashMap<>();
        if ("NEW".equals(purchase.getNewOrAdd())) {
            evenParams.put("message", purchase.getFinancialProductName() + "新建申购" + flagInfo);
        } else if ("ADD".equals(purchase.getNewOrAdd())) {
            evenParams.put("message", purchase.getFinancialProductName() + "新建追加" + flagInfo);
        }
        evenParams.put("noticeTitle", "新建申购审批");
        evenParams.put("url", "");
        evenParams.put("level", 1L);
        evenParams.put("noticeType", "NOTICE");
        sysEventService.eventSave(requestCtx, purchase.getFinancePurchaseId(), purchase.getDocumentCategory(), purchase.getDocumentType()
                , "INV", "INV_FINANCE", "P2D", evenParams);
    }


}
