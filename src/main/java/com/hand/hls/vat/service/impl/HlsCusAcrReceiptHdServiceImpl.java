package com.hand.hls.vat.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.vat.dto.HlsCusAcrReceiptHd;
import com.hand.hls.vat.service.HlsCusAcrReceiptHdService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAcrReceiptHdServiceImpl extends BaseServiceImpl<HlsCusAcrReceiptHd> implements HlsCusAcrReceiptHdService {
//    /**
//     * 单据类型
//     */
//    private static final String DOCUMENT_TYPE = "RECEIPT";
//    /**
//     * 单据类别
//     */
//    private static final String DOCUMENT_CATEGORY = "AR_RECEIPT";
//    /**
//     * 单据类别
//     */
//    private static final String BUSINESS_TYPE = "RECEIPT";
//
//    @Autowired
//    private FndCodingRuleValuesService fndCodingRuleValuesService;
//    @Autowired
//    private AcrReceiptLnMapper acrReceiptLnMapper;
//    @Autowired
//    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
//    @Autowired
//    private HlsCusFctQuotationCashflowService hlsCusFctQuotationCashflowService;
//    @Autowired
//    private HlsCusAcrInvoiceHdMapper hlsCusAcrInvoiceHdMapper;
//
//
//    @Override
//    public List<Map<String, Object>> queryAcrReceipt(IRequest request, AcrReceiptHd acrReceiptHd) {
//        List<Map<String, Object>> list = new ArrayList<>();
//        list.add(hlsCusAcrInvoiceHdMapper.queryAcrReceipt(acrReceiptHd.getReceiptHdId()));
//        return list;
//    }
//
//    @Override
//    public List<Map<String, Object>> queryAcrReceiptLn(IRequest request, AcrReceiptHd acrReceiptHd) {
//        return hlsCusAcrInvoiceHdMapper.queryAcrReceiptLn(acrReceiptHd.getReceiptHdId());
//    }
//
//    @Override
//    public void createReceipt(IRequest request, List<HlsCusAcrInvoiceHd> list) {
//        if(CollectionUtils.isEmpty(list)) throw new IllegalArgumentException("未找到待创建的收据参数!");
//        HlsCusAcrInvoiceHd invoiceHd = list.get(0);
//        // 收据头表
//        AcrReceiptHd acrReceiptHd = new AcrReceiptHd();
//        acrReceiptHd.setCompanyId(invoiceHd.getCompanyId());
//        acrReceiptHd.setBpId(invoiceHd.getBpId());
//        acrReceiptHd.setCurrency("CNY");
//        acrReceiptHd.setDocumentType(DOCUMENT_TYPE);
//        acrReceiptHd.setDocumentCategory(DOCUMENT_CATEGORY);
//        acrReceiptHd.setBusinessType(BUSINESS_TYPE);
//        Map<String, String> params = new HashMap<>();
//        acrReceiptHd.setDocumentNumber(fndCodingRuleValuesService.getCodeRuleValue(request, acrReceiptHd.getDocumentCategory(), acrReceiptHd.getDocumentType(), acrReceiptHd.getBusinessType(), params));
//        acrReceiptHd.setReceiptDate(new Date());
//        acrReceiptHd.setReceiptStatus("NEW");
//        acrReceiptHd.set__status(DTOStatus.ADD);
//        acrReceiptHd = self().insertSelective(request, acrReceiptHd);
//        Double totalAmount = 0D;
//        for (HlsCusAcrInvoiceHd hd : list){
//            // 收据行表
//            AcrReceiptLn ln = new AcrReceiptLn();
//            ln.setReceiptHdId(acrReceiptHd.getReceiptHdId());
//            ln.setSourceType(hd.getDocumentCategory());
//            ln.setSourceId(hd.getCashflowId());
//            ln.setAmount(hd.getDueAmount());
//            totalAmount += hd.getDueAmount();
//            acrReceiptLnMapper.insertSelective(ln);
//
//            // 回写到现金流表
//            if(StringUtils.equals("CON_CONTRACT",hd.getDocumentCategory())){
//                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
//                hlsCusConContractCashflow.setCashflowId(hd.getCashflowId());
//                hlsCusConContractCashflow = hlsCusConContractCashflowService.selectByPrimaryKey(request, hlsCusConContractCashflow);
//                if(hlsCusConContractCashflow.getBillingAmount()==null){
//                    hlsCusConContractCashflow.setBillingAmount(0D);
//                }
//                if(hlsCusConContractCashflow.getBillingAmount()==null){
//                    hlsCusConContractCashflow.setBillingAmount(0D);
//                }
//                if(hlsCusConContractCashflow.getCfItem() == 1L){
//                    hlsCusConContractCashflow.setBillingPrincipal(hlsCusConContractCashflow.getBillingPrincipal()+hd.getDueAmount());
//                    hlsCusConContractCashflow.setBillingAmount(hlsCusConContractCashflow.getBillingAmount()+hd.getDueAmount());
//                    hlsCusConContractCashflow = setBillingStatus(hlsCusConContractCashflow);
//                }else {
//                    hlsCusConContractCashflow.setBillingAmount(hlsCusConContractCashflow.getBillingAmount()+hd.getDueAmount());
//                    hlsCusConContractCashflow = setBillingStatus(hlsCusConContractCashflow);
//                }
//                hlsCusConContractCashflow.set__status(DTOStatus.UPDATE);
//                hlsCusConContractCashflowService.updateByPrimaryKeySelective(request, hlsCusConContractCashflow);
//            }else if(StringUtils.equals("FCT_CONTRACT",hd.getDocumentCategory())){
//                HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow = new HlsCusFctQuotationCashflow();
//                hlsCusFctQuotationCashflow.setQuotationCashflowId(hd.getCashflowId());
//                hlsCusFctQuotationCashflow = hlsCusFctQuotationCashflowService.selectByPrimaryKey(request, hlsCusFctQuotationCashflow);
//                if(hlsCusFctQuotationCashflow.getBillingAmount()==null){
//                    hlsCusFctQuotationCashflow.setBillingAmount(0D);
//                }
//                hlsCusFctQuotationCashflow.setBillingAmount(hlsCusFctQuotationCashflow.getBillingAmount()+hd.getDueAmount());
//                hlsCusFctQuotationCashflow = setBillingStatus(hlsCusFctQuotationCashflow);
//                hlsCusFctQuotationCashflow.set__status(DTOStatus.UPDATE);
//                hlsCusFctQuotationCashflowService.updateByPrimaryKeySelective(request, hlsCusFctQuotationCashflow);
//            }else {
//                throw new IllegalArgumentException("未知的业务类型!");
//            }
//        }
//        acrReceiptHd.setTotalAmount(totalAmount);
//        acrReceiptHd.set__status(DTOStatus.UPDATE);
//        self().updateByPrimaryKeySelective(request, acrReceiptHd);
//    }
//
//    private HlsCusFctQuotationCashflow setBillingStatus(HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow){
//        if(Double.compare(hlsCusFctQuotationCashflow.getBillingAmount(),0D) == 0){
//            hlsCusFctQuotationCashflow.setBillingStatus("NOT");
//        }else if(Double.compare(hlsCusFctQuotationCashflow.getDueAmount(),hlsCusFctQuotationCashflow.getBillingAmount()) > 0){
//            hlsCusFctQuotationCashflow.setBillingStatus("PARTIAL");
//        }else if(Double.compare(hlsCusFctQuotationCashflow.getDueAmount(),hlsCusFctQuotationCashflow.getBillingAmount()) == 0){
//            hlsCusFctQuotationCashflow.setBillingStatus("FULL");
//        }else {
//            throw new IllegalArgumentException("开票金额大于应收金额!");
//        }
//        return hlsCusFctQuotationCashflow;
//    }
//
//    private HlsCusConContractCashflow setBillingStatus(HlsCusConContractCashflow hlsCusConContractCashflow){
//        if(Double.compare(hlsCusConContractCashflow.getBillingAmount(),0D) == 0){
//            hlsCusConContractCashflow.setBillingStatus("NOT");
//        }else if(Double.compare(hlsCusConContractCashflow.getDueAmount(),hlsCusConContractCashflow.getBillingAmount()) > 0){
//            hlsCusConContractCashflow.setBillingStatus("PARTIAL");
//        }else if(Double.compare(hlsCusConContractCashflow.getDueAmount(),hlsCusConContractCashflow.getBillingAmount()) == 0){
//            hlsCusConContractCashflow.setBillingStatus("FULL");
//        }else {
//            throw new IllegalArgumentException("开票金额大于应收金额!");
//        }
//        return hlsCusConContractCashflow;
//    }
//
//    /**
//     * @param date 要改变的时间
//     * @param month 增减的月数
//     * @return 改变后的日期
//     */
//    private Date addDate(Date date, Long month){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.MONTH,  month.intValue());
//        return calendar.getTime();
//    }
}
