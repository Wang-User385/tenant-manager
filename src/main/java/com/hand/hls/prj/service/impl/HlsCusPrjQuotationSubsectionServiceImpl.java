package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowHMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationSubsectionMapper;
import com.hand.hls.prj.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 10:19
 * @Description:
 * @Purpose:
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationSubsectionServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationSubsection> implements HlsCusPrjQuotationSubsectionService {
    public static final Long DUE_AMOUNT_CF_ITEM = 1L;

    @Autowired
    private HlsCusPrjQuotationSubsectionMapper hlsCusPrjQuotationSubsectionMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowHService hlsCusPrjQuotationCashflowHService;
    @Autowired
    private HlsCusPrjQuotationCashflowHMapper hlsCusPrjQuotationCashflowHMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusCshWriteOffMapper hlsCusCshWriteOffMapper;
    //计算报价
    /*
     * EQUAL_INTEREST  等额租金
     * EQUAL_PRINCIPAL 等额本金
     * DESIGNATED_RENT 指定租金
     * DESIGNATED_PRINCIPAL  指定本金
     * */
    @Override
    public void calcPrjQuotation(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, String changeFlag, Double changeFinanceAmount, Double changeLeaseTerm) throws IllegalArgumentException {
        Double financeAmount = 0D;
        Double leaseTerm = 0D;
        Date leaseStartDate = new Date();
        Date inceptionOfLease = new Date();
        Long contractId = hlsCusPrjQuotation.getContractId() == null ? 0L : hlsCusPrjQuotation.getContractId();
        hlsCusPrjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, hlsCusPrjQuotation);
        HlsCusPrjQuotationSubsection hlsCusPrjQuotationSubsection = new HlsCusPrjQuotationSubsection();
        hlsCusPrjQuotationSubsection.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        /*list从按照期数从小到大排序，按照顺序来执行报价*/
        List<HlsCusPrjQuotationSubsection> list = hlsCusPrjQuotationSubsectionMapper.selectPrjQuotationSubsectionDetail(hlsCusPrjQuotationSubsection);

        String businessType = "";   //租赁类型
        if ("PRJ_PROJECT".equals(hlsCusPrjQuotation.getSourceDocumentCategory())) {
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(hlsCusPrjQuotation.getSourceDocumentId());
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            businessType = hlsCusPrjProject.getBusinessType();
        } else {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(hlsCusPrjQuotation.getSourceDocumentId());
            hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest, hlsCusConContract);
            businessType = hlsCusConContract.getBusinessType();
        }
        if ("Y".equals(changeFlag)) {
            //变更算法，剩余本金作为融资额计算剩余未核销期数作为总期数
            financeAmount = changeFinanceAmount - hlsCusPrjQuotationService.doubleDataTran(hlsCusPrjQuotation.getDownPayment());  //融资额
            inceptionOfLease = hlsCusPrjQuotation.getChangeDate();     //起租日
            leaseStartDate = hlsCusPrjQuotation.getFirstRentalPayChangeDate();
            leaseTerm = changeLeaseTerm;       //租赁期限（月为单位）
        } else if ("N".equals(changeFlag)) {
            financeAmount = hlsCusPrjQuotation.getLeaseItemAmount() - hlsCusPrjQuotationService.doubleDataTran(hlsCusPrjQuotation.getDownPayment());  //融资额
            inceptionOfLease = hlsCusPrjQuotation.getLeaseStartDate();     //起租日
            leaseStartDate = hlsCusPrjQuotation.getFirstRentalPaymentDate();
            leaseTerm = hlsCusPrjQuotation.getLeaseTerm();       //租赁期限（月为单位）
        }



        Double vatRate = hlsCusPrjQuotation.getVatRate();           //税率

        /*
        按日计息：计算年利率=年利率/360*365
        按期计息：计算年利率=年利率/360*360
         */
        Double intRate = 0D;           //年利率
        if (("DAILY_INTEREST").equals(hlsCusPrjQuotation.getPaymentMethod())) {
            intRate = (double) Math.round(hlsCusPrjQuotation.getIntRate() / 360 * 365 * Math.pow(10, 10)) / Math.pow(10, 10);
        } else if (("TIMES_INTEREST").equals(hlsCusPrjQuotation.getPaymentMethod())) {
            intRate = (double) Math.round(hlsCusPrjQuotation.getIntRate() / 360 * 360 * Math.pow(10, 10)) / Math.pow(10, 10);
        } else {
            throw new IllegalArgumentException("未取到计息方式，请检查数据！");
        }
        String rentingMethod = hlsCusPrjQuotation.getRentingMethod(); //先付后付,先付为PERIOD_FINAL，后付为PERIOD_BENGINING
        Long payType = 0L;
        if ("PERIOD_BENGINING".equals(rentingMethod)) {
            payType = 1L;
        } else {
            payType = 0L;
        }

        /*如果list存在，就开始计算报价*/
        if (list.size() > 0) {
            HlsCusPrjQuotationSubsection prjQuotationSubsection = new HlsCusPrjQuotationSubsection();
            HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
            hlsCusPrjQuotationCashflowH.setQuotationId(hlsCusPrjQuotation.getQuotationId());

            /*计算报价参数*/
            Long subsectionId = 0L;
            String calcWay = "";
            Long annualPayTimes = 0L;
            Double amount = 0D;

            /*前一次报价完毕得到的参数*/
            Long calcLeaseTerm = 0L;
            Double calcLeaseTermAll = 0D;
            Double outstandingPrincipal = 0D;
            Date date = new Date();
            /*迭代*/
            for (int i = 0; i < list.size(); i++) {
                prjQuotationSubsection = list.get(i);
                subsectionId = prjQuotationSubsection.getSubsectionId();
                calcWay = prjQuotationSubsection.getCalcWay();
                annualPayTimes = prjQuotationSubsection.getAnnualPayTimes();
                amount = prjQuotationSubsection.getAmount();
                /*第一次报价*/
                if (i == 0) {
                    if ("EQUAL_INTEREST".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcEqualInterest(iRequest, hlsCusPrjQuotation.getQuotationId(), financeAmount, leaseStartDate, leaseTerm, annualPayTimes, vatRate, intRate, payType, subsectionId, calcWay, businessType, 0L, inceptionOfLease);
                    }
                    if ("EQUAL_PRINCIPAL".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcEqualPrincipal(iRequest, hlsCusPrjQuotation.getQuotationId(), financeAmount, leaseStartDate, leaseTerm, annualPayTimes, vatRate, intRate, payType, subsectionId, calcWay, businessType, 0L, inceptionOfLease);
                    }
                    if ("DESIGNATED_RENT".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcDesignatedRent(iRequest, hlsCusPrjQuotation.getQuotationId(), financeAmount, leaseStartDate, leaseTerm, annualPayTimes, vatRate, intRate, amount, payType, subsectionId, calcWay, businessType, 0L, inceptionOfLease);
                    }
                    if ("DESIGNATED_PRINCIPAL".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcDesignatedPrincipal(iRequest, hlsCusPrjQuotation.getQuotationId(), financeAmount, leaseStartDate, leaseTerm, annualPayTimes, vatRate, intRate, amount, payType, subsectionId, calcWay, businessType, 0L, inceptionOfLease);
                    }
                } else {
                    /*后面的报价默认用后付，因为先付只是第一期利息为0*/
                    payType = 0L;
                    if ("EQUAL_INTEREST".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcEqualInterest(iRequest, hlsCusPrjQuotation.getQuotationId(), outstandingPrincipal, date, calcLeaseTermAll, annualPayTimes, vatRate, intRate, payType, subsectionId, calcWay, businessType, 1L, inceptionOfLease);
                    }
                    if ("EQUAL_PRINCIPAL".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcEqualPrincipal(iRequest, hlsCusPrjQuotation.getQuotationId(), outstandingPrincipal, date, calcLeaseTermAll, annualPayTimes, vatRate, intRate, payType, subsectionId, calcWay, businessType, 1L, inceptionOfLease);
                    }
                    if ("DESIGNATED_RENT".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcDesignatedRent(iRequest, hlsCusPrjQuotation.getQuotationId(), outstandingPrincipal, date, calcLeaseTermAll, annualPayTimes, vatRate, intRate, amount, payType, subsectionId, calcWay, businessType, 1L, inceptionOfLease);
                    }
                    if ("DESIGNATED_PRINCIPAL".equals(calcWay)) {
                        hlsCusPrjQuotationCashflowHService.calcDesignatedPrincipal(iRequest, hlsCusPrjQuotation.getQuotationId(), outstandingPrincipal, date, calcLeaseTermAll, annualPayTimes, vatRate, intRate, amount, payType, subsectionId, calcWay, businessType, 1L, inceptionOfLease);
                    }
                }

                /*执行完毕之后取这一次分段报价对应租赁期限(按月统计,所有分段报价汇总)，剩余本金，最后一期due_date作为下一次分段报价计算的基准*/
                calcLeaseTerm = (prjQuotationSubsection.getEndTime() - prjQuotationSubsection.getStartTime() + 1) * annualPayTimes + calcLeaseTerm;
                calcLeaseTermAll = leaseTerm - calcLeaseTerm;

                Long times = prjQuotationSubsection.getEndTime() - prjQuotationSubsection.getStartTime() + 1;
                hlsCusPrjQuotationCashflowH.setTimes(times);
                hlsCusPrjQuotationCashflowH.setCalcWay(calcWay);
                hlsCusPrjQuotationCashflowH.setSubsectionId(subsectionId);
                hlsCusPrjQuotationCashflowH = hlsCusPrjQuotationCashflowHMapper.selectPrjQuotationSubDetail(hlsCusPrjQuotationCashflowH);

                outstandingPrincipal = hlsCusPrjQuotationCashflowH.getOutstandingPrincipal();
                date = hlsCusPrjQuotationCashflowH.getDueDate();
            }

            /*执行分段报价完毕之后，进行报价汇总，报价汇总到主表prj_quotation_cashflow*/
            /*先清除报价表数据*/
            hlsCusPrjQuotationCashflowMapper.deleteByQuotationId(hlsCusPrjQuotation);
            /*0期 投放现金流*/
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            hlsCusPrjQuotationCashflow.setCfItem(0L);
            hlsCusPrjQuotationCashflow.setCfType(0L);
            hlsCusPrjQuotationCashflow.setCfDirection("OUTFLOW");
            hlsCusPrjQuotationCashflow.setCfStatus("RELEASE");
            hlsCusPrjQuotationCashflow.setTimes(0L);
            hlsCusPrjQuotationCashflow.setDueDate(hlsCusPrjQuotation.getLeaseStartDate());
            hlsCusPrjQuotationCashflow.setCalcDate(hlsCusPrjQuotation.getLeaseStartDate());
            hlsCusPrjQuotationCashflow.setFinIncomeDate(hlsCusPrjQuotation.getLeaseStartDate());
            hlsCusPrjQuotationCashflow.setPlanDueDate(hlsCusPrjQuotation.getLeaseStartDate());
            hlsCusPrjQuotationCashflow.setDueAmount(hlsCusPrjQuotation.getLeaseItemAmount());
            hlsCusPrjQuotationCashflow.setNetDueAmount(0D);
            hlsCusPrjQuotationCashflow.setVatDueAmount(0D);
            hlsCusPrjQuotationCashflow.setPrincipal(0D);
            hlsCusPrjQuotationCashflow.setNetPrincipal(0D);
            hlsCusPrjQuotationCashflow.setVatPrincipal(0D);
            hlsCusPrjQuotationCashflow.setInterest(0D);
            hlsCusPrjQuotationCashflow.setNetInterest(0D);
            hlsCusPrjQuotationCashflow.setVatInterest(0D);
            hlsCusPrjQuotationCashflow.setOutstandingRental(0D);
            hlsCusPrjQuotationCashflow.setOutstandingPrincipal(financeAmount);
            hlsCusPrjQuotationCashflow.setOutstandingInterest(0D);
            hlsCusPrjQuotationCashflow.setInterestAccrualBalance(0D);
            hlsCusPrjQuotationCashflow.setAccumulatedUnpaidInterest(0D);
            hlsCusPrjQuotationCashflow.setWriteOffAmount(0D);
            hlsCusPrjQuotationCashflow.setWriteOffDays(0D);
            hlsCusPrjQuotationCashflow.setChangeFineAmount(0D);
            hlsCusPrjQuotationCashflow.setBillingVatRate(0D);
            hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow);
            //最大核销期数
            HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
            hlsCusCshWriteOff.setContractId(contractId);
            hlsCusCshWriteOff.setCfItem(DUE_AMOUNT_CF_ITEM);
            List<HlsCusCshWriteOff> hlsCusCshWriteOffList = hlsCusCshWriteOffMapper.select(hlsCusCshWriteOff);
            /*通过分段报价里面的期数从到，来截取对应的prj_quotation_cashflow_h表对应的现金流条数，汇总成
            总报价现金流到prj_quotation_cashflow表*/
            hlsCusPrjQuotationSubsection.setEndTime(Long.parseLong(hlsCusCshWriteOffList.size() + ""));
            /*list从按照期数从小到大排序，按照顺序来执行报价*/
            List<HlsCusPrjQuotationSubsection> listTemp = hlsCusPrjQuotationSubsectionMapper.selectPrjQuotationSubsectionDetail(hlsCusPrjQuotationSubsection);

            Long startTime = 0L;
            Long endTime = 0L;
            Long times = 0L;
            for (int i = 0; i < listTemp.size(); i++) {
                prjQuotationSubsection = list.get(i);
                subsectionId = prjQuotationSubsection.getSubsectionId();
                if (i == 0) {
                    startTime = Long.parseLong(hlsCusCshWriteOffList.size() + "") + 1;
                } else {
                    startTime = prjQuotationSubsection.getStartTime();
                }
                endTime = prjQuotationSubsection.getEndTime();
                times = endTime - startTime + 1; //对应这一次报价的总期数
                calcWay = prjQuotationSubsection.getCalcWay();
                hlsCusPrjQuotationCashflowH.setTimes(times);
                hlsCusPrjQuotationCashflowH.setCalcWay(calcWay);
                hlsCusPrjQuotationCashflowH.setSubsectionId(subsectionId);
                /*按照times从小到大排列查出*/
                List<HlsCusPrjQuotationCashflowH> quotationCashflowHList = hlsCusPrjQuotationCashflowHMapper.selectPrjQuotationSubListDetail(hlsCusPrjQuotationCashflowH);
                if (quotationCashflowHList.size() > 0) {
                    /*第一次报价，所以quotationCashflowHList返回的现金流times符合顺序，不需要处理*/
                    if (i == 0) {
                        for (HlsCusPrjQuotationCashflowH var : quotationCashflowHList) {
                            hlsCusPrjQuotationCashflow.setCfItem(var.getCfItem());
                            hlsCusPrjQuotationCashflow.setCfType(var.getCfType());
                            hlsCusPrjQuotationCashflow.setCfDirection(var.getCfDirection());
                            hlsCusPrjQuotationCashflow.setCfStatus(var.getCfStatus());
                            hlsCusPrjQuotationCashflow.setTimes(var.getTimes());
                            hlsCusPrjQuotationCashflow.setDueDate(var.getDueDate());
                            hlsCusPrjQuotationCashflow.setCalcDate(var.getCalcDate());
                            hlsCusPrjQuotationCashflow.setFinIncomeDate(var.getFinIncomeDate());
                            hlsCusPrjQuotationCashflow.setPlanDueDate(var.getPlanDueDate());
                            hlsCusPrjQuotationCashflow.setDueAmount(var.getDueAmount());
                            hlsCusPrjQuotationCashflow.setNetDueAmount(var.getNetDueAmount());
                            hlsCusPrjQuotationCashflow.setVatDueAmount(var.getVatDueAmount());
                            hlsCusPrjQuotationCashflow.setPrincipal(var.getPrincipal());
                            hlsCusPrjQuotationCashflow.setNetPrincipal(var.getNetPrincipal());
                            hlsCusPrjQuotationCashflow.setVatPrincipal(var.getVatPrincipal());
                            hlsCusPrjQuotationCashflow.setInterest(var.getInterest());
                            hlsCusPrjQuotationCashflow.setNetInterest(var.getNetInterest());
                            hlsCusPrjQuotationCashflow.setVatInterest(var.getVatInterest());
                            hlsCusPrjQuotationCashflow.setOutstandingRental(var.getOutstandingRental());
                            hlsCusPrjQuotationCashflow.setOutstandingPrincipal(var.getOutstandingPrincipal());
                            hlsCusPrjQuotationCashflow.setOutstandingInterest(var.getOutstandingInterest());
                            hlsCusPrjQuotationCashflow.setInterestAccrualBalance(var.getInterestAccrualBalance());
                            hlsCusPrjQuotationCashflow.setAccumulatedUnpaidInterest(var.getAccumulatedUnpaidInterest());
                            hlsCusPrjQuotationCashflow.setWriteOffAmount(var.getWriteOffAmount());
                            hlsCusPrjQuotationCashflow.setWriteOffDays(var.getWriteOffDays());
                            hlsCusPrjQuotationCashflow.setChangeFineAmount(var.getChangeFineAmount());
                            hlsCusPrjQuotationCashflow.setBillingVatRate(var.getBillingVatRate());
                            hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow);
                        }
                    } else {
                        /*处理接着第一次报价后的现金流times*/
                        /*quotationCashflowHList的大小肯定等于endTime-startTime+1*/
                        for (int j = 0; j < quotationCashflowHList.size(); j++) {
                            hlsCusPrjQuotationCashflow.setCfItem(quotationCashflowHList.get(j).getCfItem());
                            hlsCusPrjQuotationCashflow.setCfType(quotationCashflowHList.get(j).getCfType());
                            hlsCusPrjQuotationCashflow.setCfDirection(quotationCashflowHList.get(j).getCfDirection());
                            hlsCusPrjQuotationCashflow.setCfStatus(quotationCashflowHList.get(j).getCfStatus());
                            for (int k = startTime.intValue(); k <= endTime; k++) {
                                if (k - startTime == j) {
                                    hlsCusPrjQuotationCashflow.setTimes(Long.valueOf(k));
                                }
                            }
                            hlsCusPrjQuotationCashflow.setDueDate(quotationCashflowHList.get(j).getDueDate());
                            hlsCusPrjQuotationCashflow.setCalcDate(quotationCashflowHList.get(j).getCalcDate());
                            hlsCusPrjQuotationCashflow.setFinIncomeDate(quotationCashflowHList.get(j).getFinIncomeDate());
                            hlsCusPrjQuotationCashflow.setPlanDueDate(quotationCashflowHList.get(j).getPlanDueDate());
                            hlsCusPrjQuotationCashflow.setDueAmount(quotationCashflowHList.get(j).getDueAmount());
                            hlsCusPrjQuotationCashflow.setNetDueAmount(quotationCashflowHList.get(j).getNetDueAmount());
                            hlsCusPrjQuotationCashflow.setVatDueAmount(quotationCashflowHList.get(j).getVatDueAmount());
                            hlsCusPrjQuotationCashflow.setPrincipal(quotationCashflowHList.get(j).getPrincipal());
                            hlsCusPrjQuotationCashflow.setNetPrincipal(quotationCashflowHList.get(j).getNetPrincipal());
                            hlsCusPrjQuotationCashflow.setVatPrincipal(quotationCashflowHList.get(j).getVatPrincipal());
                            hlsCusPrjQuotationCashflow.setInterest(quotationCashflowHList.get(j).getInterest());
                            hlsCusPrjQuotationCashflow.setNetInterest(quotationCashflowHList.get(j).getNetInterest());
                            hlsCusPrjQuotationCashflow.setVatInterest(quotationCashflowHList.get(j).getVatInterest());
                            hlsCusPrjQuotationCashflow.setOutstandingRental(quotationCashflowHList.get(j).getOutstandingRental());
                            hlsCusPrjQuotationCashflow.setOutstandingPrincipal(quotationCashflowHList.get(j).getOutstandingPrincipal());
                            hlsCusPrjQuotationCashflow.setOutstandingInterest(quotationCashflowHList.get(j).getOutstandingInterest());
                            hlsCusPrjQuotationCashflow.setInterestAccrualBalance(quotationCashflowHList.get(j).getInterestAccrualBalance());
                            hlsCusPrjQuotationCashflow.setAccumulatedUnpaidInterest(quotationCashflowHList.get(j).getAccumulatedUnpaidInterest());
                            hlsCusPrjQuotationCashflow.setWriteOffAmount(quotationCashflowHList.get(j).getWriteOffAmount());
                            hlsCusPrjQuotationCashflow.setWriteOffDays(quotationCashflowHList.get(j).getWriteOffDays());
                            hlsCusPrjQuotationCashflow.setChangeFineAmount(quotationCashflowHList.get(j).getChangeFineAmount());
                            hlsCusPrjQuotationCashflow.setBillingVatRate(quotationCashflowHList.get(j).getBillingVatRate());
                            hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("分段报价测算计算异常！");
                }
            }

            /*更新现金流信息，可以指定首期支付日 和第二期以后支付日 yyyy mm dd 中的d*/
            Date firstRentalPaymentDate = hlsCusPrjQuotation.getFirstRentalPaymentDate(); //首期支付日

            /*找到第一期的租金现金流*/
            HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            prjQuotationCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            prjQuotationCashflow.setTimes(1L);
            prjQuotationCashflow = hlsCusPrjQuotationCashflowMapper.selectPrjQuotationCashflowFirstTimeRental(prjQuotationCashflow);
            /*第一期现金流支付日*/

            // Date startDate = prjQuotationCashflow.getDueDate();
            Date startDate;
            Date endDate;
            String dateFlag = "N";
            if (prjQuotationCashflow.getPlanDueDate().compareTo(firstRentalPaymentDate) == 0 || prjQuotationCashflow.getPlanDueDate().compareTo(firstRentalPaymentDate) == 1) {
                startDate = firstRentalPaymentDate;
                endDate = prjQuotationCashflow.getPlanDueDate();
                dateFlag = "Y";
            } else {
                startDate = prjQuotationCashflow.getPlanDueDate();
                endDate = firstRentalPaymentDate;
            }
            Long days = 0L;
            String startDateString = getStartDate(startDate);
            String dayString = startDateString.substring(startDateString.indexOf("-", startDateString.indexOf("-") + 1) + 1);
            String endDateString = getEndDate(dayString, endDate);

            if (endDateString.equals(startDateString)) {
                days = 0L;
            } else {
                if (dateFlag.equals("Y")) {
                    days = -getDays(startDateString, endDateString);
                } else {
                    days = getDays(startDateString, endDateString);
                }

            }


            Double interest = transfor(financeAmount * intRate / 360 * days);
            if ("PERIOD_BENGINING".equals(rentingMethod)) {
                interest = 0D;
            } else {
                interest = interest + prjQuotationCashflow.getInterest();
            }
            prjQuotationCashflow.setInterest(interest);
            prjQuotationCashflow.setNetInterest(transfor(interest / (1 + vatRate)));
            prjQuotationCashflow.setVatInterest(prjQuotationCashflow.getInterest() - prjQuotationCashflow.getNetInterest());
            prjQuotationCashflow.setDueAmount(prjQuotationCashflow.getPrincipal() + prjQuotationCashflow.getInterest());
            prjQuotationCashflow.setNetDueAmount(prjQuotationCashflow.getNetPrincipal() + prjQuotationCashflow.getNetInterest());
            prjQuotationCashflow.setVatDueAmount(prjQuotationCashflow.getDueAmount() - prjQuotationCashflow.getNetDueAmount());
            prjQuotationCashflow.setDueDate(firstRentalPaymentDate);
            prjQuotationCashflow.setCalcDate(firstRentalPaymentDate);
            prjQuotationCashflow.setFinIncomeDate(firstRentalPaymentDate);
            hlsCusPrjQuotationCashflowService.updateByPrimaryKeySelective(iRequest, prjQuotationCashflow);

            /*找到第二期以后的租金现金流，包括第二期*/
            HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();
            cashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            List<HlsCusPrjQuotationCashflow> cashflowList = hlsCusPrjQuotationCashflowMapper.selectPrjQuotationCashflowAfterFirstTimeRental(cashflow);
            if (cashflowList.size() > 0) {
                /*更新后续现金流的支付日*/
                /*第一期租金的年月*/
                Long firstY = Long.valueOf(String.format("%tY", startDate));
                Long firstM = Long.valueOf(String.format("%tm", startDate));

                Long firstPayY = Long.valueOf(String.format("%tY", firstRentalPaymentDate));
                Long firstPayM = Long.valueOf(String.format("%tm", firstRentalPaymentDate));

                Long month = (firstY - firstPayY) * 12 + (firstM - firstPayM);

                String y = "";
                String m = "";
                String dateString = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dueDate = new Date();
                Long time = 2L;
                Calendar calendar = Calendar.getInstance();
                Date dueDate1 = new Date();
                try {
                    for (HlsCusPrjQuotationCashflow eachCashflow : cashflowList) {
                        Long day = hlsCusPrjQuotation.getPaymentDay();
                        calendar.setTime(eachCashflow.getDueDate());
                        calendar.add(Calendar.MONTH, -month.intValue());
                        y = String.format("%tY", calendar.getTime());
                        m = String.format("%tm", calendar.getTime());

                        int dayTemp = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        if (day > (int) dayTemp) {
                            day = (long) dayTemp;
                        }
                        dateString = y + "-" + m + "-" + day;
                        dueDate = sdf.parse(dateString);
                        dueDate1 = dueDate;
                        /*如果是第二期，需要更新一下对应的租金和利息*/
                        if (time == eachCashflow.getTimes()) {
                            Long d = 0L;
                            String startDateS = getStartDate(dueDate1);
                            String dayS = startDateS.substring(startDateS.indexOf("-", startDateS.indexOf("-") + 1) + 1);
                            String endDateS = getEndDate(dayS, eachCashflow.getPlanDueDate());
                            d = -getDays(startDateS, endDateS);
                            Double interestAmount = transfor(((eachCashflow.getPrincipal() + eachCashflow.getOutstandingPrincipal()) * intRate / 360 * d) -
                                    ((eachCashflow.getPrincipal() + eachCashflow.getOutstandingPrincipal()) * intRate / 360 * days));
                            interestAmount = interestAmount + eachCashflow.getInterest();
                            eachCashflow.setInterest(interestAmount);
                            eachCashflow.setNetInterest(transfor(interestAmount / (1 + vatRate)));
                            eachCashflow.setVatInterest(eachCashflow.getInterest() - eachCashflow.getNetInterest());
                            eachCashflow.setDueAmount(eachCashflow.getPrincipal() + eachCashflow.getInterest());
                            eachCashflow.setNetDueAmount(eachCashflow.getNetPrincipal() + eachCashflow.getNetInterest());
                            eachCashflow.setVatDueAmount(eachCashflow.getDueAmount() - eachCashflow.getNetDueAmount());
                        }
                        eachCashflow.setDueDate(dueDate1);
                        eachCashflow.setCalcDate(dueDate1);
                        eachCashflow.setFinIncomeDate(dueDate1);
                        hlsCusPrjQuotationCashflowService.updateByPrimaryKeySelective(iRequest, eachCashflow);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }


            HlsCusPrjQuotationCashflow cusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            cusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            HlsCusPrjQuotationCashflow maxPrjQuotationCashflow = hlsCusPrjQuotationCashflowMapper.selectPrjQuotationCashflowMaxTimeAndDate(cusPrjQuotationCashflow);
            /*手续费*/
            String leaseChargingFrequency = hlsCusPrjQuotation.getLeaseChargingFrequency();
            Double leaseCharge = hlsCusPrjQuotation.getLeaseCharge() == null ? 0D : hlsCusPrjQuotation.getLeaseCharge();
            Long leaseTime = 0L;  //leaseTerm
            if (leaseCharge.compareTo(0D) == 1) {
                cusPrjQuotationCashflow.setCfItem(3L);
                cusPrjQuotationCashflow.setCfType(3L);
                cusPrjQuotationCashflow.setCfDirection("INFLOW");
                cusPrjQuotationCashflow.setCfStatus("RELEASE");
                cusPrjQuotationCashflow.setTimes(0L);
                cusPrjQuotationCashflow.setDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setCalcDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setFinIncomeDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setPlanDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setDueAmount(leaseCharge);
                cusPrjQuotationCashflow.setNetDueAmount(transfor(leaseCharge / (1 + vatRate)));
                cusPrjQuotationCashflow.setVatDueAmount(cusPrjQuotationCashflow.getDueAmount() - cusPrjQuotationCashflow.getNetDueAmount());
                cusPrjQuotationCashflow.setPrincipal(0D);
                cusPrjQuotationCashflow.setNetPrincipal(0D);
                cusPrjQuotationCashflow.setVatPrincipal(0D);
                cusPrjQuotationCashflow.setInterest(0D);
                cusPrjQuotationCashflow.setNetInterest(0D);
                cusPrjQuotationCashflow.setVatInterest(0D);
                cusPrjQuotationCashflow.setOutstandingRental(0D);
                cusPrjQuotationCashflow.setOutstandingPrincipal(0D);
                cusPrjQuotationCashflow.setOutstandingInterest(0D);
                cusPrjQuotationCashflow.setInterestAccrualBalance(0D);
                cusPrjQuotationCashflow.setAccumulatedUnpaidInterest(0D);
                cusPrjQuotationCashflow.setWriteOffAmount(0D);
                cusPrjQuotationCashflow.setWriteOffDays(0D);
                cusPrjQuotationCashflow.setChangeFineAmount(0D);
                cusPrjQuotationCashflow.setBillingVatRate(0D);
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, cusPrjQuotationCashflow);
            } else if ("MONTH".equals(leaseChargingFrequency)) {
                leaseTime = leaseTerm.longValue();

            } else if ("QUARTER".equals(leaseChargingFrequency)) {
                leaseTerm = leaseTerm / 12 * 4;
                leaseTime = leaseTerm.longValue();  //转化为long的时候，自动向下取整
            } else if ("HALF_A_YEAR".equals(leaseChargingFrequency)) {
                leaseTerm = leaseTerm / 12 * 2;
                leaseTime = leaseTerm.longValue();  //转化为long的时候，自动向下取整
            } else if ("YEAR".equals(leaseChargingFrequency)) {
                leaseTerm = leaseTerm / 12 * 1;
                leaseTime = leaseTerm.longValue();  //转化为long的时候，自动向下取整
            }
            /*咨询服务费*/
            String advServicingFrequency = hlsCusPrjQuotation.getAdvServicingFrequency();
            Double advServiceFee = hlsCusPrjQuotation.getAdvServiceFee() == null ? 0D : hlsCusPrjQuotation.getAdvServiceFee();
            if (advServiceFee.compareTo(0D) == 1) {
                cusPrjQuotationCashflow.setCfItem(4L);
                cusPrjQuotationCashflow.setCfType(4L);
                cusPrjQuotationCashflow.setCfDirection("INFLOW");
                cusPrjQuotationCashflow.setCfStatus("RELEASE");
                cusPrjQuotationCashflow.setTimes(0L);
                cusPrjQuotationCashflow.setDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setCalcDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setFinIncomeDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setPlanDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setDueAmount(advServiceFee);
                cusPrjQuotationCashflow.setNetDueAmount(transfor(advServiceFee / (1 + vatRate)));
                cusPrjQuotationCashflow.setVatDueAmount(cusPrjQuotationCashflow.getDueAmount() - cusPrjQuotationCashflow.getNetDueAmount());
                cusPrjQuotationCashflow.setPrincipal(0D);
                cusPrjQuotationCashflow.setNetPrincipal(0D);
                cusPrjQuotationCashflow.setVatPrincipal(0D);
                cusPrjQuotationCashflow.setInterest(0D);
                cusPrjQuotationCashflow.setNetInterest(0D);
                cusPrjQuotationCashflow.setVatInterest(0D);
                cusPrjQuotationCashflow.setOutstandingRental(0D);
                cusPrjQuotationCashflow.setOutstandingPrincipal(0D);
                cusPrjQuotationCashflow.setOutstandingInterest(0D);
                cusPrjQuotationCashflow.setInterestAccrualBalance(0D);
                cusPrjQuotationCashflow.setAccumulatedUnpaidInterest(0D);
                cusPrjQuotationCashflow.setWriteOffAmount(0D);
                cusPrjQuotationCashflow.setWriteOffDays(0D);
                cusPrjQuotationCashflow.setChangeFineAmount(0D);
                cusPrjQuotationCashflow.setBillingVatRate(0D);
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, cusPrjQuotationCashflow);
            }
            /*保证金*/

            Double deposit = hlsCusPrjQuotation.getDeposit() == null ? 0D : hlsCusPrjQuotation.getDeposit();
            if (deposit.compareTo(0D) == 1) {
                /*保证金*/
                cusPrjQuotationCashflow.setCfItem(51L);
                cusPrjQuotationCashflow.setCfType(5L);
                cusPrjQuotationCashflow.setCfDirection("INFLOW");
                cusPrjQuotationCashflow.setCfStatus("RELEASE");
                cusPrjQuotationCashflow.setTimes(0L);
                cusPrjQuotationCashflow.setDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setCalcDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setFinIncomeDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setPlanDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setDueAmount(deposit);
                cusPrjQuotationCashflow.setNetDueAmount(transfor(deposit / (1 + vatRate)));
                cusPrjQuotationCashflow.setVatDueAmount(cusPrjQuotationCashflow.getDueAmount() - cusPrjQuotationCashflow.getNetDueAmount());
                cusPrjQuotationCashflow.setPrincipal(0D);
                cusPrjQuotationCashflow.setNetPrincipal(0D);
                cusPrjQuotationCashflow.setVatPrincipal(0D);
                cusPrjQuotationCashflow.setInterest(0D);
                cusPrjQuotationCashflow.setNetInterest(0D);
                cusPrjQuotationCashflow.setVatInterest(0D);
                cusPrjQuotationCashflow.setOutstandingRental(0D);
                cusPrjQuotationCashflow.setOutstandingPrincipal(0D);
                cusPrjQuotationCashflow.setOutstandingInterest(0D);
                cusPrjQuotationCashflow.setInterestAccrualBalance(0D);
                cusPrjQuotationCashflow.setAccumulatedUnpaidInterest(0D);
                cusPrjQuotationCashflow.setWriteOffAmount(0D);
                cusPrjQuotationCashflow.setWriteOffDays(0D);
                cusPrjQuotationCashflow.setChangeFineAmount(0D);
                cusPrjQuotationCashflow.setBillingVatRate(0D);
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, cusPrjQuotationCashflow);
                //保证金退还
                cusPrjQuotationCashflow.setCfItem(52L);
                cusPrjQuotationCashflow.setCfType(52L);
                cusPrjQuotationCashflow.setCfDirection("OUTFLOW");
                cusPrjQuotationCashflow.setCfStatus("RELEASE");
                cusPrjQuotationCashflow.setTimes(maxPrjQuotationCashflow.getTimes());
                cusPrjQuotationCashflow.setDueDate(maxPrjQuotationCashflow.getDueDate());
                cusPrjQuotationCashflow.setCalcDate(maxPrjQuotationCashflow.getDueDate());
                cusPrjQuotationCashflow.setFinIncomeDate(maxPrjQuotationCashflow.getDueDate());
                cusPrjQuotationCashflow.setPlanDueDate(maxPrjQuotationCashflow.getDueDate());
                cusPrjQuotationCashflow.setDueAmount(deposit);
                cusPrjQuotationCashflow.setNetDueAmount(transfor(deposit / (1 + vatRate)));
                cusPrjQuotationCashflow.setVatDueAmount(cusPrjQuotationCashflow.getDueAmount() - cusPrjQuotationCashflow.getNetDueAmount());
                cusPrjQuotationCashflow.setPrincipal(0D);
                cusPrjQuotationCashflow.setNetPrincipal(0D);
                cusPrjQuotationCashflow.setVatPrincipal(0D);
                cusPrjQuotationCashflow.setInterest(0D);
                cusPrjQuotationCashflow.setNetInterest(0D);
                cusPrjQuotationCashflow.setVatInterest(0D);
                cusPrjQuotationCashflow.setOutstandingRental(0D);
                cusPrjQuotationCashflow.setOutstandingPrincipal(0D);
                cusPrjQuotationCashflow.setOutstandingInterest(0D);
                cusPrjQuotationCashflow.setInterestAccrualBalance(0D);
                cusPrjQuotationCashflow.setAccumulatedUnpaidInterest(0D);
                cusPrjQuotationCashflow.setWriteOffAmount(0D);
                cusPrjQuotationCashflow.setWriteOffDays(0D);
                cusPrjQuotationCashflow.setChangeFineAmount(0D);
                cusPrjQuotationCashflow.setBillingVatRate(0D);
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, cusPrjQuotationCashflow);

            }
            //留购价
            Double residualValue = hlsCusPrjQuotation.getResidualValue() == null ? 0D : hlsCusPrjQuotation.getResidualValue();
            if (residualValue.compareTo(0D) == 1) {
                cusPrjQuotationCashflow.setCfItem(8L);
                cusPrjQuotationCashflow.setCfType(8L);
                cusPrjQuotationCashflow.setCfDirection("INFLOW");
                cusPrjQuotationCashflow.setCfStatus("RELEASE");
                cusPrjQuotationCashflow.setTimes(maxPrjQuotationCashflow.getTimes());
                cusPrjQuotationCashflow.setDueDate(maxPrjQuotationCashflow.getDueDate());
                cusPrjQuotationCashflow.setCalcDate(maxPrjQuotationCashflow.getDueDate());
                cusPrjQuotationCashflow.setFinIncomeDate(maxPrjQuotationCashflow.getDueDate());
                cusPrjQuotationCashflow.setPlanDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setDueAmount(residualValue);
                cusPrjQuotationCashflow.setNetDueAmount(transfor(residualValue / (1 + vatRate)));
                cusPrjQuotationCashflow.setVatDueAmount(cusPrjQuotationCashflow.getDueAmount() - cusPrjQuotationCashflow.getNetDueAmount());
                cusPrjQuotationCashflow.setPrincipal(0D);
                cusPrjQuotationCashflow.setNetPrincipal(0D);
                cusPrjQuotationCashflow.setVatPrincipal(0D);
                cusPrjQuotationCashflow.setInterest(0D);
                cusPrjQuotationCashflow.setNetInterest(0D);
                cusPrjQuotationCashflow.setVatInterest(0D);
                cusPrjQuotationCashflow.setOutstandingRental(0D);
                cusPrjQuotationCashflow.setOutstandingPrincipal(0D);
                cusPrjQuotationCashflow.setOutstandingInterest(0D);
                cusPrjQuotationCashflow.setInterestAccrualBalance(0D);
                cusPrjQuotationCashflow.setAccumulatedUnpaidInterest(0D);
                cusPrjQuotationCashflow.setWriteOffAmount(0D);
                cusPrjQuotationCashflow.setWriteOffDays(0D);
                cusPrjQuotationCashflow.setChangeFineAmount(0D);
                cusPrjQuotationCashflow.setBillingVatRate(0D);
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, cusPrjQuotationCashflow);
            }
            //首付款
            Double downPayment = hlsCusPrjQuotation.getDownPayment() == null ? 0D : hlsCusPrjQuotation.getDownPayment();
            if (downPayment.compareTo(0D) == 1) {
                cusPrjQuotationCashflow.setCfItem(2L);
                cusPrjQuotationCashflow.setCfType(2L);
                cusPrjQuotationCashflow.setCfDirection("INFLOW");
                cusPrjQuotationCashflow.setCfStatus("RELEASE");
                cusPrjQuotationCashflow.setTimes(0L);
                cusPrjQuotationCashflow.setDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setCalcDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setFinIncomeDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setPlanDueDate(hlsCusPrjQuotation.getLeaseStartDate());
                cusPrjQuotationCashflow.setDueAmount(downPayment);
                cusPrjQuotationCashflow.setNetDueAmount(transfor(downPayment / (1 + vatRate)));
                cusPrjQuotationCashflow.setVatDueAmount(cusPrjQuotationCashflow.getDueAmount() - cusPrjQuotationCashflow.getNetDueAmount());
                cusPrjQuotationCashflow.setPrincipal(0D);
                cusPrjQuotationCashflow.setNetPrincipal(0D);
                cusPrjQuotationCashflow.setVatPrincipal(0D);
                cusPrjQuotationCashflow.setInterest(0D);
                cusPrjQuotationCashflow.setNetInterest(0D);
                cusPrjQuotationCashflow.setVatInterest(0D);
                cusPrjQuotationCashflow.setOutstandingRental(0D);
                cusPrjQuotationCashflow.setOutstandingPrincipal(0D);
                cusPrjQuotationCashflow.setOutstandingInterest(0D);
                cusPrjQuotationCashflow.setInterestAccrualBalance(0D);
                cusPrjQuotationCashflow.setAccumulatedUnpaidInterest(0D);
                cusPrjQuotationCashflow.setWriteOffAmount(0D);
                cusPrjQuotationCashflow.setWriteOffDays(0D);
                cusPrjQuotationCashflow.setChangeFineAmount(0D);
                cusPrjQuotationCashflow.setBillingVatRate(0D);
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, cusPrjQuotationCashflow);
            }
//跳工作日
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflowSkip = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflowSkip.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowService.select(iRequest, hlsCusPrjQuotationCashflowSkip, 1, 9999999);
            Long principalNegativeCount = 0L;
            for (HlsCusPrjQuotationCashflow prjQuotationCashflowTemp : hlsCusPrjQuotationCashflowList) {
                if (prjQuotationCashflowTemp.getPrincipal().compareTo(0D) == -1) {
                    principalNegativeCount = principalNegativeCount + 1;
                }
            }
            if (principalNegativeCount.compareTo(0L) == 1) {
                throw new IllegalArgumentException("指定本金过小，本金为负，请重新计算！");
            }
            hlsCusPrjQuotationCashflowService.cashSkipWorkday(iRequest, hlsCusPrjQuotationCashflowList);
            //变更插入con_contract_cashflow
            if ("Y".equals(changeFlag)) {
                //删除这个contractId项下现金流(其他应收付不删除)
                hlsCusConContractCashflowMapper.deleteCalcByContractId(hlsCusPrjQuotation.getContractId());
                for (HlsCusPrjQuotationCashflow dt : hlsCusPrjQuotationCashflowList) {
                    HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                    Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dt);
                    hlsBeanRefUtilService.setFieldValue(hlsCusConContractCashflow, mapCsh);
                    hlsCusConContractCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                    hlsCusConContractCashflow.setContractId(contractId);
                    hlsCusConContractCashflow.setWriteOffFlag("NOT");
                    hlsCusConContractCashflowService.insertSelective(iRequest, hlsCusConContractCashflow);
                }
            }
            //update 现金流 当本金为负时 本金处理为0 租金=利息

//            try {calcPrjQuotation
//                dealPrincipalCashflow(iRequest, hlsCusPrjQuotationCashflowList);
//
//            } catch (HlsCusException e) {
//                throw new IllegalArgumentException("处理现金流失败！");
//            }
        } else {
            throw new IllegalArgumentException("未取到分段测算报价！");
        }

    }

    //保留两位小数
    public Double transfor(Double amount) {
        BigDecimal bg = new BigDecimal(amount);
        double num = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }

    /*
     * @param date需要判断的日期
     * 如果起始日期是一个月的最后一天，则等于同月的 30 号
     * 因为有2月份的情况，可能出现2-30的情况，所以这边只能用字符形式返回
     */
    @Override

    public String getStartDate(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String y = "";
        String m = "";
        String dateString = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            y = String.format("%tY", date);
            m = String.format("%tm", date);
            dateString = y + "-" + m + "-" + "30";
        } else {
            dateString = sdf.format(date);
        }
        return dateString;
    }

    /*
     * @param startDate起始日期  endDate结束日期
     * 如果终止日期是一个月的最后一天，并且起始日期早于30号，则终止日期等于下一个月的1号，否则，终止日期等于本月的 30 号。
     * 因为有2月份的情况，可能出现2-30的情况，所以这边只能用字符形式返回
     */
    @Override
    public String getEndDate(String day, Date endDate) {
        String y = "";
        String m = "";
        String dateString = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            //2月不处理
            if (!String.format("%tm", endDate).equals("02")) {
                if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    if (Long.valueOf(day) < 30) {
                        calendar.add(Calendar.MONTH, 1);
                        Date newEndDate = calendar.getTime();
                        y = String.format("%tY", newEndDate);
                        m = String.format("%tm", newEndDate);
                        dateString = y + "-" + m + "-" + "01";
                    } else {
                        y = String.format("%tY", endDate);
                        m = String.format("%tm", endDate);
                        dateString = y + "-" + m + "-" + "30";
                    }
                } else {
                    dateString = sdf.format(endDate);
                }
            } else {
                dateString = sdf.format(endDate);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return dateString;
    }

    /*
     * @param startDate起始日期字符串  endDate结束日期字符串
     * days360算法
     * 1. 同月的两个日期：天数差=结束日一开始日
     * 2. 相邻月：天数差=30-开始日+结束日
     * 3. 跨N月不跨年：天数差=（结束月-开始月-1）*30+30-开始日+结束日
     * 4. 跨N年：设开始日为S ，结束日为E ，开始年的12月30号为SE，结束年的1月1号为ES ，相隔年数差为N ，天数差=SE-S+(N-1)*360+（ES-E+1）
     */
    @Override
    public Long getDays(String startDate, String endDate) {
        Long days = 0L;
        String startYear = startDate.substring(0, startDate.indexOf("-"));
        String startMonth = startDate.substring(startDate.indexOf("-") + 1, startDate.indexOf("-", startDate.indexOf("-") + 1));
        String startDay = startDate.substring(startDate.indexOf("-", startDate.indexOf("-") + 1) + 1);

        String endYear = endDate.substring(0, endDate.indexOf("-"));
        String endMonth = endDate.substring(endDate.indexOf("-") + 1, endDate.indexOf("-", endDate.indexOf("-") + 1));
        String endDay = endDate.substring(endDate.indexOf("-", endDate.indexOf("-") + 1) + 1);

        /*如果结束日期<起始日期,就用起始日期减去结束日期，取负数*/
//        if ((Long.valueOf(startYear) - Long.valueOf(endYear) > 0) || ((Long.valueOf(startYear) - Long.valueOf(endYear) == 0) && (Long.valueOf(startMonth) - Long.valueOf(endMonth) > 0)) || ((Long.valueOf(startYear) - Long.valueOf(endYear) == 0) && (Long.valueOf(startMonth) - Long.valueOf(endMonth) == 0) && (Long.valueOf(startDay) - Long.valueOf(endDay) > 0))) {
//            days = -getDays(endDate, startDate);
//        } else {
        if (startYear.equals(endYear)) {
            /*同月的两个日期：天数差=结束日一开始日*/
            if (startMonth.equals(endMonth)) {
                days = Long.valueOf(endDay) - Long.valueOf(startDay);
            }
            /*相邻月：天数差=30-开始日+结束日*/
            if (Long.valueOf(endMonth) - Long.valueOf(startMonth) == 1L) {
                days = 30L - Long.valueOf(startDay) + Long.valueOf(endDay);
            }
            /*跨N月不跨年：天数差=（结束月-开始月-1）*30+30-开始日+结束日*/
            if (Long.valueOf(endMonth) - Long.valueOf(startMonth) > 1L) {
                days = (Long.valueOf(endMonth) - Long.valueOf(startMonth) - 1) * 30 + 30L - Long.valueOf(startDay) + Long.valueOf(endDay);
            }
        } else {
            /*跨N年：设开始日为S ，结束日为E ，开始年的12月30号为SE，结束年的1月1号为ES ，相隔年数差为N 天数差=SE-S+(N-1)*360+（ES-E+1）*/
            String SE = startYear + "-12-30";
            String ES = endYear + "-01-01";
            days = getDays(startDate, SE) + (Long.valueOf(endYear) - Long.valueOf(startYear) - 1) * 360 + getDays(ES, endDate) + 1;
        }
        //  }
        return days;
    }

    public void dealPrincipalCashflow(IRequest iRequest, List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowLists) throws HlsCusException {
        if (hlsCusPrjQuotationCashflowLists.size() > 0) {
            for (HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow : hlsCusPrjQuotationCashflowLists) {
                if (hlsCusPrjQuotationCashflow.getPrincipal().compareTo(0D) == -1) {
                    hlsCusPrjQuotationCashflow.setPrincipal(0D);
                    hlsCusPrjQuotationCashflow.setInterest(hlsCusPrjQuotationCashflow.getDueAmount());
                    hlsCusPrjQuotationCashflow.set__status(DTOStatus.UPDATE);
                    hlsCusPrjQuotationCashflowService.updateByPrimaryKeySelective(iRequest, hlsCusPrjQuotationCashflow);
                }
            }
            HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
            prjQuotation.setQuotationId(hlsCusPrjQuotationCashflowLists.get(0).getQuotationId());
            prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, prjQuotation);
            hlsCusPrjQuotationCashflowService.taxCashflowDemolition(iRequest, prjQuotation);

        }
    }
}
