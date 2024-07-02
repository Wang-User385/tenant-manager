package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.inv.dto.HlsCusFinanceAttachment;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseDetailIdBak;
import com.hand.hls.inv.mapper.HlsCusFinanceAttachmentMapper;
import com.hand.hls.inv.mapper.HlsCusFinancePurchaseDetailIdBakMapper;
import com.hand.hls.inv.mapper.HlsCusFinancePurchaseMapper;
import com.hand.hls.inv.service.HlsCusIFinanceAttachmentService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseDetailIdBakService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:申购追加作废审批结束
 * @Author: wty
 * @Date: Created in 14:19 2018/4/26
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPurchaseInvalidSubmitServiceTask implements JavaDelegate, IActivitiBean {

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
    private HlsCusFinancePurchaseDetailIdBakMapper detailIdBakMapper;

    @Autowired
    private SysEventService sysEventService;

    public HlsCusPurchaseInvalidSubmitServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String hlsCusPurchasePrams = (String) delegateExecution.getVariable("hlsCusPurchase");
        HlsCusFinancePurchase purchase = JSON.parseObject(hlsCusPurchasePrams, HlsCusFinancePurchase.class);
        HlsCusFinancePurchase resultPurchase = new HlsCusFinancePurchase();
        resultPurchase.setFinancePurchaseId(purchase.getFinancePurchaseId());
        HlsCusFinancePurchaseDetailIdBak detailIdBak = new HlsCusFinancePurchaseDetailIdBak();

        Map<String, Object> evenParams = new HashMap<>();
        String flagInfo = "";

        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            flagInfo = "审批通过";
            resultPurchase.setPurchaseStatus("INVALID");
            //如果是新建申购。则同时更新他的附件
            if ("NEW".equals(purchase.getNewOrAdd())) {
                updateAttachment(flag,purchase.getFinancePurchaseId(),requestCtx);
                detailIdBak.setInvalidBak("INVALID");
                detailIdBak.setDetailId(purchase.getFinancePurchaseId());
                detailIdBakMapper.updateDetailId(detailIdBak);
            }
            if ("ADD".equals(purchase.getNewOrAdd())) {
                //追加时判断是否完全赎回，如果完全赎回则更新他的detailDateId
                List<HlsCusFinancePurchase> list = hlsCusIFinancePurchaseService.checkIsRedeemed(requestCtx, resultPurchase);
                if (CollectionUtils.isNotEmpty(list)) {
                    Double investmentAmount = list.get(0).getInvestmentAmount();//投资总金额
                    Double purchaseRedeemedAmount = list.get(0).getPurchaseRedeemedAmount();//赎回总金额
                    if (investmentAmount != null && purchaseRedeemedAmount != null) {
                        if ((investmentAmount - purchaseRedeemedAmount) == 0D) {
                            HlsCusFinancePurchase updatePurchase = new HlsCusFinancePurchase();

                            //更新id取值历史表把对应的id更新为INVALID
                            detailIdBak.setFinancePurchaseId(purchase.getParentPurchaseId());
                            detailIdBak.setDetailId(purchase.getFinancePurchaseId());
                            detailIdBak.setInvalidBak("INVALID");
                            detailIdBakMapper.updateDetailId(detailIdBak);

                            //取是NORMAL状态下的最新的一条
                            HlsCusFinancePurchaseDetailIdBak normalDetailIdBak = new HlsCusFinancePurchaseDetailIdBak();
                            normalDetailIdBak.setInvalidBak("NORMAL");
                            normalDetailIdBak.setFinancePurchaseId(purchase.getParentPurchaseId());
                            List<HlsCusFinancePurchaseDetailIdBak> bakIdList = detailIdBakMapper.queryAll(normalDetailIdBak);
                            if (CollectionUtils.isNotEmpty(bakIdList)) {
                                updatePurchase.setParentPurchaseId(purchase.getParentPurchaseId());
                                updatePurchase.setDetailDateId(bakIdList.get(0).getDetailId());
                                hlsCusFinancePurchaseMapper.updateDetailDateId(updatePurchase);
                            }
                        }
                    }
                }
            }
            hlsCusIFinancePurchaseService.updateByPrimaryKeySelective(requestCtx, resultPurchase);
        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            flag = "APPROVED_RETURN";
            flagInfo = "审批退回";
            if ("NEW".equals(purchase.getNewOrAdd())) {
                updateAttachment(flag,purchase.getFinancePurchaseId(),requestCtx);
            }
            resultPurchase.setPurchaseStatus("APPROVED");
            hlsCusIFinancePurchaseService.updateByPrimaryKeySelective(requestCtx, resultPurchase);
        }
        if ("NEW".equals(purchase.getNewOrAdd())) {
            evenParams.put("message", purchase.getFinancialProductName() + "申购作废" + flagInfo);
        } else if ("ADD".equals(purchase.getNewOrAdd())) {
            evenParams.put("message", purchase.getFinancialProductName() + "追加作废" + flagInfo);
        }
        evenParams.put("noticeTitle", "作废审批");
        evenParams.put("url", "");
        evenParams.put("level", 1L);
        evenParams.put("noticeType", "NOTICE");
        sysEventService.eventSave(requestCtx, purchase.getFinancePurchaseId(), purchase.getDocumentCategory(), purchase.getDocumentType()
                , "INV", "INV_FINANCE", "P2D", evenParams);
    }


    /**
     * @Description:更新附件状态
     * @Author: Wty
     * @Date: Created om 21:08 2018/5/1
     * @param: [flag, id, requestCtx]   附件状态，申购id，irequest
     * @return: java.util.List<hls.core.inv.dto.HlsCusFinanceAttachment>
     */
    private void updateAttachment(String flag, Long id, IRequest requestCtx) {
        HlsCusFinanceAttachment attachment = new HlsCusFinanceAttachment();
        attachment.setStatus("APPROVING");
        attachment.setPurchaseId(id);
        List<HlsCusFinanceAttachment> attachments = attachmentMapper.queryAll(attachment);
        if (CollectionUtils.isNotEmpty(attachments)) {
            for (int i = 0; i < attachments.size(); i++) {
                if ("APPROVED".equals(flag)) {
                    attachments.get(i).setStatus("INVALID");
                } else if ("APPROVED_RETURN".equals(flag)) {
                    attachments.get(i).setStatus("APPROVED");
                }
                attachments.get(i).set__status("update");
            }
        }
        attachmentService.batchUpdate(requestCtx, attachments);
    }
}
