package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.dto.HlsCusAbsProductCashDetail;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import com.hand.hls.abs.dto.HlsCusAbsProductReceipt;
import com.hand.hls.abs.service.HlsCusAbsProductCashDetailService;
import com.hand.hls.abs.service.HlsCusAbsProductCollectionService;
import com.hand.hls.abs.service.HlsCusAbsProductReceiptService;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import com.hand.hls.cap.service.HlsCusCapFinancingPlanLnService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ferry
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductSubmitEndServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusAbsProductService hlsCusAbsProductService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusAbsProductCollectionService productCollectionService;
    @Autowired
    private HlsCusAbsProductCashDetailService productCashDetailService;
    @Autowired
    private JeTrxCommonService commonService;
    @Autowired
    private HlsCusAbsProductReceiptService productReceiptService;

    @Autowired
    private HlsCusCapFinancingPlanLnService capFinancingPlanLnService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest request = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long productId = Long.valueOf(delegateExecution.getProcessInstanceBusinessKey());

        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(productId);
        hlsCusAbsProduct = hlsCusAbsProductService.selectByPrimaryKey(request, hlsCusAbsProduct);
        if("NORMAL".equals(hlsCusAbsProduct.getDataClass())) {
            if (StringUtils.equals("APPROVED", result)) {
                hlsCusAbsProduct.setProductStatus("APPROVED");
            } else {
                hlsCusAbsProduct.setProductStatus(result);
            }
            hlsCusAbsProductService.updateByPrimaryKeySelective(request, hlsCusAbsProduct);

            //复制一份临时兑付计划
            /*if (StringUtils.equals("APPROVED", result)) {
                HlsCusAbsProductCollection productCollection = new HlsCusAbsProductCollection();
                productCollection.setProductId(productId);
                productCollection.setDataClass("NORMAL");
                List<HlsCusAbsProductCollection> productCollections = productCollectionService.select(request, productCollection, 1, 9999);
                for (HlsCusAbsProductCollection collection : productCollections) {
                    HlsCusAbsProductCashDetail cashDetail = new HlsCusAbsProductCashDetail();
                    cashDetail.setCollectionId(collection.getCollectionId());
                    List<HlsCusAbsProductCashDetail> productCashDetails = productCashDetailService.select(request, cashDetail, 1, 999);

                    collection.setCollectionId(null);
                    collection.setDataClass("TEMP");
                    collection.setCreationDate(null);
                    collection.setLastUpdateDate(null);
                    collection.setCreatedBy(request.getUserId());
                    collection.setLastUpdatedBy(request.getUserId());
                    productCollectionService.insertSelective(request, collection);

                    for (HlsCusAbsProductCashDetail cashDetail1 : productCashDetails) {
                        cashDetail1.setCashDetailId(null);
                        cashDetail1.setCollectionId(collection.getCollectionId());
                        cashDetail1.setCreationDate(null);
                        cashDetail1.setLastUpdateDate(null);
                        cashDetail1.setCreatedBy(request.getUserId());
                        cashDetail1.setLastUpdatedBy(request.getUserId());
                        cashDetail1.set__status(DTOStatus.ADD);
                    }
                    productCashDetailService.batchUpdate(request, productCashDetails);
                }

                AbstractJeTrxService receiptService = commonService.map.get("ABS_RECEIPT");
                Map params = new HashMap<>();
                //插入凭证事物流水表
                HlsCusAbsProductReceipt productReceipt=new HlsCusAbsProductReceipt();
                productReceipt.setProductId(hlsCusAbsProduct.getProductId());
                List<HlsCusAbsProductReceipt> absProductReceipts = productReceiptService.select(request, productReceipt, 1, 9999);
                for(HlsCusAbsProductReceipt receipt:absProductReceipts) {
                    params.put("jeTrxId", receipt.getReceiptId());
                    params.put("companyId", hlsCusAbsProduct.getCompanyId());
                    params.put("contractId", receipt.getReceiptId());
                    params.put("sourceDoc", "ABS_PRODUCT_RECEIPT");
                    receiptService.process(request, params);
                }


                //分摊
                try {
                    hlsCusAbsProductService.absProductIncome(request, hlsCusAbsProduct);
                } catch (HlsCusException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }*/

//            String messageName = "ABS产品创建提交审批";
//            String noticeTitle = "ABS产品创建提交审批";
//            Map<String, Object> evenParams = new HashMap<>();
//            evenParams.put("message", messageName + (StringUtils.equals("APPROVED", result) ? "审批通过" : "审批退回"));
//            evenParams.put("noticeTitle", noticeTitle);
//            evenParams.put("url", "/abs/ABS110A/abs_product_detail_read.view?productId=" + hlsCusAbsProduct.getProductId());
//            evenParams.put("level", 1L);
//            evenParams.put("noticeType", "NOTICE");
//            sysEventService.eventSave(request, hlsCusAbsProduct.getProductId(), "ABS_PRODUCT", "ABS_PRODUCT",
//                    "ABS", "ABS_PRODUCT", "P2D", evenParams);

        }else{
            //变更
            if (StringUtils.equals("APPROVED", result)) {
                 hlsCusAbsProductService.confirmProductChange(request, hlsCusAbsProduct);

                HlsCusAbsProduct normalProduct = new HlsCusAbsProduct();
                normalProduct.setProductId(hlsCusAbsProduct.getRefProductId());
                normalProduct = hlsCusAbsProductService.selectByPrimaryKey(request, normalProduct);
                hlsCusAbsProductService.confirmCollectionTemp(request,normalProduct);
            }else{
                HlsCusChangeReqInfo reqInfo=new HlsCusChangeReqInfo();
                reqInfo.setStatus(result);
                reqInfo.setChangeReqId(hlsCusAbsProduct.getChangeReqId());
                hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request,reqInfo);
            }
        }

       /* if(hlsCusAbsProduct.getFinancePlanLineId()!=null){
            capFinancingPlanLnService.updateFinancePlanStatus(null,hlsCusAbsProduct.getFinancePlanLineId());
        }*/
    }
}
