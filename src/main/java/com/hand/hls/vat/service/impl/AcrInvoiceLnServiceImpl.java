
package com.hand.hls.vat.service.impl;


import com.github.pagehelper.PageHelper;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.fnd.dto.FndSysCodes;
import com.hand.hls.fnd.mapper.FndSysCodesMapper;
import com.hand.hls.vat.dto.*;
import com.hand.hls.vat.exception.AcrInvoiceException;
import com.hand.hls.vat.mapper.HlsCusAcpInvoiceLnMapper;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceLnMapper;
import com.hand.hls.vat.mapper.HlsInvoiceProfileDtlMapper;
import com.hand.hls.vat.mapper.HlsInvoiceProfileMapper;
import com.hand.hls.vat.service.IAcrInvoiceLnService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class AcrInvoiceLnServiceImpl extends BaseServiceImpl<HlsCusAcrInvoiceLn> implements IAcrInvoiceLnService {
    private static final String CONTRACT_BT_LEASEBACK = "LEASEBACK";
    private static final String CONTRACT_BT_LEASE = "LEASE";
    private static final String INVOICE_KIND_RECEIPT = "RECEIPT";
    private static final long CF_ITEM_RENTAL = 1L;
    private static final long CF_ITEM_PRINCIPAL = 100L;
    private static final long CF_ITEM_INTEREST = 101L;
    private static final String BILLING_TYPE_PRINCIPAL = "PRINCIPAL";
    private static final String BILLING_TYPE_INTEREST = "INTEREST";
    private static final String CF_ITEM_DESC_PRINCIPAL = "本金";
    private static final String CF_ITEM_DESC_INTEREST = "利息";
    private static final String GROUP_RULE_ITEM = "ITEM";
    @Autowired
    private HlsCusAcrInvoiceLnMapper acrInvoiceLnMapper;
    @Autowired
    private HlsCusAcpInvoiceLnMapper hlsCusAcpInvoiceLnMapper;

    @Autowired
    private HlsInvoiceProfileMapper invoiceProfileMapper;
    @Autowired
    private HlsInvoiceProfileDtlMapper invoiceProfileDtlMapper;
    @Autowired
    private FndSysCodesMapper fndSysCodesMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;

    public AcrInvoiceLnServiceImpl() {
    }

    public List<AcrInvoiceLn> selectForCreate(List<Long> cashflowIds, String groupRule,String billingType) throws AcrInvoiceException {
        List<AcrInvoiceLn> list = this.acrInvoiceLnMapper.selectForCreateAcrInvoice(cashflowIds);
        List<AcrInvoiceLn> splitList = new ArrayList();
        Iterator var5 = list.iterator();

        while(var5.hasNext()) {
            AcrInvoiceLn item = (AcrInvoiceLn)var5.next();
            if (item.getBillingMethod() == null) {
                throw new AcrInvoiceException(AcrInvoiceException.WITH_OUT_BILLING_METHOD);
            }

            HlsInvoiceProfile billingRule = (HlsInvoiceProfile)this.invoiceProfileMapper.selectByPrimaryKey(item.getBillingMethod());
            HlsInvoiceProfileDtl condition = new HlsInvoiceProfileDtl();
            condition.setInvoiceProfile(billingRule.getInvoiceProfile());
            condition.setEnabledFlag("Y");
            if (item.getCfItem() != 1L) {
                condition.setCfItem(item.getCfItem());
            }

            List<HlsInvoiceProfileDtl> billingMethods = this.invoiceProfileDtlMapper.query(condition);
            if (item.getCfItem() != 1L && (billingMethods.size() == 0 || "RECEIPT".equals(((HlsInvoiceProfileDtl)billingMethods.get(0)).getInvoiceKind()))) {
                throw new AcrInvoiceException(AcrInvoiceException.CF_ITEM_NOT_MATCH_INVOICE_PROFILE_DTL);
            }

            if (item.getCfItem() == 1L) {
                HlsCusConContract contract = new HlsCusConContract();
                contract.setContractNumber(item.getContractNumber());
                contract = (HlsCusConContract)this.conContractMapper.selectOne(contract);
                String businessType = contract.getBusinessType();
                List rentalDtls;
                if ("LEASEBACK".equals(businessType)) {
                    rentalDtls = (List)billingMethods.stream().filter((o) -> {
                        return o.getCfItem() == 101L;
                    }).collect(Collectors.toList());
                    HlsInvoiceProfileDtl invoiceProfileInterest = (HlsInvoiceProfileDtl)rentalDtls.get(0);
                    String taxRateCode = invoiceProfileInterest.getTaxTypeCode();
                    FndSysCodes example = new FndSysCodes();
                    example.setCompanyId(/*RequestHelper.getCurrentRequest().getCompanyId()*/248L);
                    example.setTaxTypeCode(taxRateCode);
                    FndSysCodes taxRate = (FndSysCodes)this.fndSysCodesMapper.selectByPrimaryKey(example);
                    if (rentalDtls.size() < 1) {
                        throw new AcrInvoiceException(AcrInvoiceException.BILLING_METHOD_DTL_DEFINE_ERROR);
                    }

                    item.setBillingType("INTEREST");
                    item.setCfItem(101L);
                    item.setCfItemN("利息");
                    item.setDueAmount(item.getInterest());
                    item.setBillingAmount(item.getBillingInterest());
                    item.setTaxableServiceName(invoiceProfileInterest.getTaxableServiceName());
                    item.setTaxTypeRate(taxRate.getTaxTypeRate());
                    item.setTaxTypeCode(taxRate.getTaxTypeCode());
                    item.setTaxTypeCodeN(taxRate.getDescription());
                }

                if ("LEASE".equals(businessType)) {
                    rentalDtls = (List)billingMethods.stream().filter((o) -> {
                        return o.getCfItem() == 1L;
                    }).collect(Collectors.toList());
                    List<HlsInvoiceProfileDtl> PIDtls = (List)billingMethods.stream().filter((o) -> {
                        return o.getCfItem() == 100L || o.getCfItem() == 101L;
                    }).collect(Collectors.toList());
                    if (rentalDtls.size() == 0 && (PIDtls.size() == 0 || PIDtls.size() != 2)) {
                        throw new AcrInvoiceException(AcrInvoiceException.BILLING_METHOD_DTL_DEFINE_ERROR);
                    }

                    if (PIDtls.size() == 2) {
                        long count = PIDtls.stream().filter((o) -> {
                            return "Y".equals(o.getMergeRentalFlag());
                        }).count();
                        if (count == 1L) {
                            throw new AcrInvoiceException(AcrInvoiceException.BILLING_METHOD_DTL_DEFINE_ERROR);
                        }

                        HlsInvoiceProfileDtl principalInvoiceProfile = (HlsInvoiceProfileDtl)billingMethods.stream().filter((o) -> {
                            return o.getCfItem() == 100L;
                        }).findFirst().get();
                        HlsInvoiceProfileDtl interestInvoiceProfile = (HlsInvoiceProfileDtl)billingMethods.stream().filter((o) -> {
                            return o.getCfItem() == 101L;
                        }).findFirst().get();
                        FndSysCodes interestCondition = new FndSysCodes();
                        interestCondition.setTaxTypeCode(interestInvoiceProfile.getTaxTypeCode());
                        interestCondition.setCompanyId(/*RequestHelper.getCurrentRequest().getCompanyId()*/248L);
                        FndSysCodes interestTaxRate = (FndSysCodes)this.fndSysCodesMapper.selectByPrimaryKey(interestCondition);
                        FndSysCodes principalCondition = new FndSysCodes();
                        principalCondition.setTaxTypeCode(principalInvoiceProfile.getTaxTypeCode());
                        principalCondition.setCompanyId(/*RequestHelper.getCurrentRequest().getCompanyId()*/248L);
                        FndSysCodes principalTaxRate = (FndSysCodes)this.fndSysCodesMapper.selectByPrimaryKey(principalCondition);
                        if (count == 0L) {
                            if("INTEREST".equals(billingType)){
                                item.setDueAmount(item.getInterest());
                                item.setBillingAmount(item.getBillingInterest());
                                item.setBillingType("INTEREST");
                                item.setCfItem(101L);
                                item.setCfItemN("利息");
                                item.setTaxableServiceName(interestInvoiceProfile.getTaxableServiceName());
//                                item.setTaxTypeRate(principalTaxRate.getTaxTypeRate());
//                                item.setTaxTypeCode(principalTaxRate.getTaxTypeCode());
//                                item.setTaxTypeCodeN(principalTaxRate.getDescription());
//                                item.setTaxTypeRate(principalCondition.getTaxTypeRate());
//                                item.setTaxTypeCode(principalCondition.getTaxTypeCode());
//                                item.setTaxTypeCodeN(principalCondition.getDescription());
                            }else if("PRINCIPAL".equals(billingType)){
                                item.setDueAmount(item.getPrincipal());
                                item.setBillingAmount(item.getBillingPrincipal());
                                item.setBillingType("PRINCIPAL");
                                item.setCfItem(100L);
                                item.setCfItemN("本金");
                                item.setTaxableServiceName(principalInvoiceProfile.getTaxableServiceName());
//                                item.setTaxTypeRate(principalTaxRate.getTaxTypeRate());
//                                item.setTaxTypeCode(principalTaxRate.getTaxTypeCode());
//                                item.setTaxTypeCodeN(principalTaxRate.getDescription());
//                                item.setTaxTypeRate(principalCondition.getTaxTypeRate());
//                                item.setTaxTypeCode(principalCondition.getTaxTypeCode());
//                                item.setTaxTypeCodeN(principalCondition.getDescription());
                            }else{
                                try {
                                    AcrInvoiceLn p = (AcrInvoiceLn)item.clone();
                                    p.setDueAmount(p.getInterest());
                                    p.setBillingAmount(p.getBillingInterest());
                                    p.setBillingType("INTEREST");
                                    p.setCfItem(101L);
                                    p.setCfItemN("利息");
                                    p.setTaxableServiceName(interestInvoiceProfile.getTaxableServiceName());
//                                    p.setTaxTypeRate(interestTaxRate.getTaxTypeRate());
//                                    p.setTaxTypeCode(interestTaxRate.getTaxTypeCode());
//                                    p.setTaxTypeCodeN(interestTaxRate.getDescription());
//                                    p.setTaxTypeRate(principalCondition.getTaxTypeRate());
//                                    p.setTaxTypeCode(principalCondition.getTaxTypeCode());
//                                    p.setTaxTypeCodeN(principalCondition.getDescription());
                                    //splitList.add(p);
                                } catch (CloneNotSupportedException var23) {
                                    var23.printStackTrace();
                                }

                                //直租开租金票 不开分本利
                                //item.setDueAmount(item.getPrincipal());
                                //item.setBillingAmount(item.getBillingPrincipal());
                                //item.setBillingType("PRINCIPAL");
                                //item.setCfItem(100L);
                                //item.setCfItemN("本金");
                                //item.setTaxableServiceName(principalInvoiceProfile.getTaxableServiceName());
//                                item.setTaxTypeRate(principalTaxRate.getTaxTypeRate());
//                                item.setTaxTypeCode(principalTaxRate.getTaxTypeCode());
//                                item.setTaxTypeCodeN(principalTaxRate.getDescription());
//                                item.setTaxTypeRate(principalCondition.getTaxTypeRate());
//                                item.setTaxTypeCode(principalCondition.getTaxTypeCode());
//                                item.setTaxTypeCodeN(principalCondition.getDescription());
                            }
                        }

                        if (count == 2L) {
                            item.setTaxableServiceName(principalInvoiceProfile.getTaxableServiceName());
//                            item.setTaxTypeRate(principalTaxRate.getTaxTypeRate());
//                            item.setTaxTypeCode(principalTaxRate.getTaxTypeCode());
//                            item.setTaxTypeCodeN(principalTaxRate.getDescription());
//                            item.setTaxTypeRate(principalCondition.getTaxTypeRate());
//                            item.setTaxTypeCode(principalCondition.getTaxTypeCode());
//                            item.setTaxTypeCodeN(principalCondition.getDescription());
                        }
                    }
                }
            }
        }

        if (splitList.size() > 0) {
            list.addAll(splitList);
        }

        List<AcrInvoiceLn> result = this.group(list, groupRule);;
        return result;
    }

    private List<AcrInvoiceLn> group(List<AcrInvoiceLn> list, String groupRule) {
        List<AcrInvoiceLn> combinedList = new ArrayList();
        if ("ITEM".equals(groupRule)) {
            (list.stream().collect(Collectors.groupingBy((item) -> {
                return item.getCfItemN() + ";" + item.getTaxTypeRate();
            }))).forEach((k, v) -> {
                AcrInvoiceLn combinedOne = (AcrInvoiceLn)v.get(0);
                combinedOne.setCashflowIds(combinedOne.getCashflowId() + "");

                for(int j = 1; j < v.size(); ++j) {
                    combinedOne.setDueAmount(CalculateUtil.add(combinedOne.getDueAmount(), ((AcrInvoiceLn)v.get(j)).getDueAmount()));
                    combinedOne.setBillingAmount(CalculateUtil.add(combinedOne.getBillingAmount(), ((AcrInvoiceLn)v.get(j)).getBillingAmount()));
                    combinedOne.setCashflowIds(combinedOne.getCashflowIds() + "," + ((AcrInvoiceLn)v.get(j)).getCashflowId());
                }

                combinedList.add(combinedOne);
            });
            return combinedList;
        } else {
            return list;
        }
    }

    public List<AcrInvoiceLn> queryAcrInvoiceLnDetailByHdId(Long invocieHdId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.acrInvoiceLnMapper.queryAcrInvoiceLnDetailByHdId(invocieHdId);
    }

    @Override
    public List<HlsCusAcrInvoiceLn> selectImportTempList(Long headerId) {
        //        tempList.forEach((item) -> {
//            item.setErrorMsg(this.validImportRecord(item));
//        });
        return this.acrInvoiceLnMapper.selectImportTempList(headerId);
    }

    @Override
    public List<Double> queryTotalAmount(HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn){
        List<Double>  total_amount= new ArrayList<>();
        if(hlsCusAcpInvoiceLn.getContractId() !=null){
            total_amount.add(hlsCusAcpInvoiceLnMapper.queryTotalAmount(hlsCusAcpInvoiceLn.getContractId(),hlsCusAcpInvoiceLn.getInvoiceLnId()));
        }
        return total_amount;
    }

    @Override
    public List<Double> queryContractAmount(HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn){
        List<Double>  contract_amount= new ArrayList<>();
        if(hlsCusAcpInvoiceLn.getContractId() !=null){
            contract_amount.add(hlsCusAcpInvoiceLnMapper.queryContractAmount(hlsCusAcpInvoiceLn.getContractId()));
        }
        return contract_amount;
    }
}
