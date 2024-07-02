package com.hand.hls.vat.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.vat.dto.*;
import com.hand.hls.vat.exception.AcrInvoiceException;
import com.hand.hls.vat.mapper.AcrInvoiceLnDtMapper;
import com.hand.hls.vat.service.AcrInvoiceBillService;
import com.hand.hls.vat.service.impl.AcrInvoiceHdServiceImpl;
import com.hand.hls.vat.service.impl.AcrInvoiceLnServiceImpl;
import com.hand.hls.vat.service.impl.AcrInvoiceRelationshipServiceImpl;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;


@Component
public class HlsBillJob extends AbstractJobWithIRequest {


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;

    @Autowired
    private AcrInvoiceBillService acrInvoiceBillService;

    @Autowired
    private AcrInvoiceHdServiceImpl acrInvoiceHdService;

    @Autowired
    private AcrInvoiceLnServiceImpl acrInvoiceLnService;

    @Autowired
    private AcrInvoiceLnDtMapper acrInvoiceLnDtMapper;

    @Autowired
    private AcrInvoiceRelationshipServiceImpl acrInvoiceRelationshipService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext context,IRequest requestCtx) throws Exception {

        try {
            List<AcrInvoiceBill> acrInvoiceBills = acrInvoiceBillService.queryAcrInvoiceBill();
            List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHds = new ArrayList<HlsCusAcrInvoiceHd>();

            HlsCusAcrInvoiceHd hd = new HlsCusAcrInvoiceHd();
            HlsCusAcrInvoiceLn ln = new HlsCusAcrInvoiceLn();
            AcrInvoiceLnDt dt = new AcrInvoiceLnDt();
            HlsCusAcrInvoiceRelationship rl = new HlsCusAcrInvoiceRelationship();
            if (!acrInvoiceBills.isEmpty()) {
                for (AcrInvoiceBill acrInvoiceBill : acrInvoiceBills) {
                    hd.setCompanyId(acrInvoiceBill.getCompanyId());
                    hd.setDocumentCategory("AR_INVOICE");
                    hd.setDocumentType("ACR");
                    hd.setBusinessType("ACR");
                    Map<String, String> params = new HashMap<>();
                    hd.setDocumentNumber(fndCodingRuleValuesService.getCodeRuleValue(requestCtx, hd.getDocumentCategory(), hd.getDocumentType(), hd.getBusinessType(), params));
                    hd.setInvoiceKind(acrInvoiceBill.getInvoiceKind());
                    hd.setBpId(acrInvoiceBill.getBpId());
                    hd.setBpName(acrInvoiceBill.getBpName());
                    hd.setBpTaxRegistryNum(acrInvoiceBill.getBpTaxRegistryNum());
                    hd.setBpAddressPhoneNum(acrInvoiceBill.getBpAddressPhoneNum());
                    hd.setBpBankAccount(acrInvoiceBill.getBpBankAccount());
                    hd.setTotalAmount(acrInvoiceBill.getTotalAmount());
                    Double netAmount = CalculateUtil.div(acrInvoiceBill.getTotalAmount(), CalculateUtil.add(1D, Double.valueOf(acrInvoiceBill.getTaxTypeRate())));
                    Double taxAmount = CalculateUtil.sub(acrInvoiceBill.getTotalAmount(), netAmount);
                    hd.setNetAmount(netAmount);
                    hd.setTaxAmount(taxAmount);
                    hd.setCurrency("CNY");
                    hd.setInvoiceDate(new Date());
                    hd.setInvoiceStatus("NEW");
                    hd.set__status(DTOStatus.ADD);

                    hd.setProductName(acrInvoiceBill.getProductName());
                    hd.setPrice(acrInvoiceBill.getBillingAmount());
                    hd.setTaxTypeRate(Double.valueOf(acrInvoiceBill.getTaxTypeRate()));

//                    hd.setBillingWay(acrInvoiceBill.getBillingWay());
//                    hd.setCurrency(acrInvoiceBill.getCurrency());
//                    hd.setBillingMethod(acrInvoiceBill.getBillingMethod());
                    hd.setBillingAmount(acrInvoiceBill.getBillingAmount());
                    hd.setCashflowId(acrInvoiceBill.getCashflowId());
                    hd.setSourceDocumentId(acrInvoiceBill.getCashflowId());

                    hlsCusAcrInvoiceHds.add(hd);
                }

                hlsCusAcrInvoiceHds = split(hlsCusAcrInvoiceHds);
                for (HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd : hlsCusAcrInvoiceHds) {
                    hd = acrInvoiceHdService.insertSelective(requestCtx, hlsCusAcrInvoiceHd);

                    ln.setInvoiceHdId(hd.getInvoiceHdId());
                    ln.setProductName(hlsCusAcrInvoiceHd.getProductName());
                    ln.setPrice(hlsCusAcrInvoiceHd.getBillingAmount());
                    ln.setNetPrice(hlsCusAcrInvoiceHd.getNetAmount());
                    ln.setTaxTypeRate(hlsCusAcrInvoiceHd.getTaxTypeRate());
                    ln.setTaxIncludedFlag("Y");
                    ln.setTaxAmount(hd.getTaxAmount());
                    ln.setNetAmount(hd.getNetAmount());
                    ln.setTotalAmount(hd.getTotalAmount());
                    ln.set__status(DTOStatus.ADD);
                    ln = acrInvoiceLnService.insertSelective(requestCtx, ln);

                    dt.setInvoiceLnId(ln.getInvoiceLnId());
                    dt.setCashflowId(hlsCusAcrInvoiceHd.getCashflowId());
                    dt.setBillingAmount(hlsCusAcrInvoiceHd.getBillingAmount());
                    dt.setBillingType(hlsCusAcrInvoiceHd.getBillingType());
                    acrInvoiceLnDtMapper.insertSelective(dt);

                    rl.setInvoiceLnId(ln.getInvoiceLnId());
                    rl.setSourceDocumentType(hlsCusAcrInvoiceHd.getDocumentType());
                    rl.setSourceDocumentId(hlsCusAcrInvoiceHd.getCashflowId());
                    rl.setBillingAmount(hlsCusAcrInvoiceHd.getBillingAmount());
                    rl.setTaxTypeRate(hlsCusAcrInvoiceHd.getTaxTypeRate());
                    rl.setTaxIncludedFlag(ln.getTaxIncludedFlag());
                    rl.setTaxAmount(ln.getTaxAmount());
                    rl.setNetAmount(ln.getNetAmount());
                    rl.setInvoiceKind(hlsCusAcrInvoiceHd.getInvoiceKind());
                    rl.setBpId(hlsCusAcrInvoiceHd.getBpId());
                    rl.set__status(DTOStatus.ADD);
                    acrInvoiceRelationshipService.insertSelective(requestCtx, rl);
                    logger.info("===================================================================================");
                    logger.info("生成待开票清单成功:" + hlsCusAcrInvoiceHd.getDocumentNumber());
                    logger.info("===================================================================================");
                }
            }

            setExecutionSummary("success");
        } catch (Exception e) {
            exception = e;
            logger.info("===================================================================================");
            logger.info("生成待开票清单失败:" + e);
            logger.info("===================================================================================");
            this.setExecutionSummary(this.exception.getClass().getName() + ":" + this.exception.getMessage());
        }

    }


    private List<HlsCusAcrInvoiceHd> split(List<HlsCusAcrInvoiceHd> invoiceHdList) throws AcrInvoiceException {

        //承载拆分后的发票头表list
        List<HlsCusAcrInvoiceHd> spiltInvoiceHds = new ArrayList<>();

        for (HlsCusAcrInvoiceHd acrInvoiceHd : invoiceHdList) {
            //发票限额
            Double limit = 1000000D;
            if (acrInvoiceHd.getTotalAmount()<limit) {
                spiltInvoiceHds.add(acrInvoiceHd);
            } else {

                //拆分后de发票张数
                int count = ((Double) Math.ceil(acrInvoiceHd.getTotalAmount() / limit)).intValue();
                //是否有余数，有余数则为最后一张发票的金额
                Double left = BigDecimal.valueOf(acrInvoiceHd.getTotalAmount()).remainder(BigDecimal.valueOf(limit)).doubleValue();

                List<HlsCusAcrInvoiceHd> nowHds = new ArrayList<>();

                for (int m = 1; m <= count; m++) {

                    HlsCusAcrInvoiceHd invoiceHd = new HlsCusAcrInvoiceHd();
                    BeanRefUtils.beanToBean(acrInvoiceHd, invoiceHd, hlsBeanRefUtilService);
                    invoiceHd.setTotalAmount(limit);

                    //本次额度
                    Double credit = limit;
                    if (m == count && new BigDecimal(left).compareTo(new BigDecimal(0.0D)) > 0) {
                        credit = left;
                        invoiceHd.setTotalAmount(left);
                    }

                    nowHds.add(invoiceHd);
                }
                spiltInvoiceHds.addAll(nowHds);
            }
        }
        return spiltInvoiceHds;
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        //任务发生异常时候进行的动作
        //false 挂起JOB等待处理
        //true 继续执行
        return false;
    }

}
