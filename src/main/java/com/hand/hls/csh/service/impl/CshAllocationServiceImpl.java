package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.dto.CshAllocation;
import com.hand.hls.csh.dto.CshAllocationCredit;
import com.hand.hls.csh.dto.CshAllocationReceipt;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.mapper.CshAllocationCreditMapper;
import com.hand.hls.csh.mapper.CshAllocationMapper;
import com.hand.hls.csh.mapper.CshAllocationReceiptMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.service.ICshAllocationCreditService;
import com.hand.hls.csh.service.ICshAllocationReceiptService;
import com.hand.hls.csh.service.ICshAllocationService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshAllocationServiceImpl extends BaseServiceImpl<CshAllocation> implements ICshAllocationService {

    @Autowired
    private CshAllocationMapper cshAllocationMapper;

    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private ICshAllocationCreditService cshAllocationCreditService;
    @Autowired
    private ICshAllocationReceiptService cshAllocationReceiptService;
    @Autowired
    private CshAllocationReceiptMapper cshAllocationReceiptMapper;
    @Autowired
    private CshAllocationCreditMapper cshAllocationCreditMapper;

    @Autowired
    private ICshAllocationService cshAllocationService;

    @Override
    public List<CshAllocation> allocationQuery(IRequest iRequest, CshAllocation cshAllocation, int page, int pageSize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(page, pageSize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }

        return cshAllocationMapper.allocationQuery(cshAllocation);
    }


    //收款流水未反冲、未核销、未退款才能参与自动分配
    public void transactionCheck(List<HlsCusCshTransaction> cshTransactionList) throws ResMessageException {
        for (HlsCusCshTransaction cshTransaction : cshTransactionList) {
            if (!"N".equalsIgnoreCase(cshTransaction.getReversedFlag())) {
                throw new ResMessageException("收款流水" + cshTransaction.getTransactionNum() + "已反冲，不能进行后续操作!");
            }
            if (!"NOT".equalsIgnoreCase(cshTransaction.getWriteOffFlag())) {
                throw new ResMessageException("收款流水" + cshTransaction.getTransactionNum() + "已核销，不能进行后续操作!");
            }
            if (!"N".equalsIgnoreCase(cshTransaction.getRefundFlag())) {
                throw new ResMessageException("收款流水" + cshTransaction.getTransactionNum() + "已退款，不能进行后续操作!");
            }
        }
    }


    public CshAllocation createAllocation(IRequest iRequest, List<HlsCusCshTransaction> cshTransactionList, List<HlsCusConContractCashflow> conContractCashflowList, String strTwo) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //插入 分配相关表
        //删除后 重新插入
        CshAllocationReceipt allocationReceipt = new CshAllocationReceipt();
        allocationReceipt.setTransactionId(cshTransactionList.get(0).getTransactionId());

        List<CshAllocationReceipt> CshAllocationReceipts = cshAllocationReceiptMapper.receiptQueryAll(allocationReceipt);
        for (CshAllocationReceipt CshAllocationReceipt : CshAllocationReceipts) {
            CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
            cshAllocationReceipt.setAllocationId(CshAllocationReceipt.getAllocationId());
            cshAllocationReceipt.setTransactionId(CshAllocationReceipt.getTransactionId());
            cshAllocationReceiptService.batchDelete(cshAllocationReceiptMapper.select(cshAllocationReceipt));
            CshAllocationCredit allocationCredit = new CshAllocationCredit();
            allocationCredit.setAllocationId(CshAllocationReceipt.getAllocationId());
            cshAllocationCreditService.batchDelete(cshAllocationCreditMapper.select(allocationCredit));

            CshAllocation cshAllocation1 = new CshAllocation();
            cshAllocation1.setAllocationId(CshAllocationReceipt.getAllocationId());
            cshAllocationService.batchDelete(cshAllocationMapper.select(cshAllocation1));
        }

        CshAllocation cshAllocation = new CshAllocation();
        cshAllocation.setAllocationNumber(codingRuleValuesService.getCodeRuleValue(iRequest, "CSH_TRX",
                "ALLOCATION", "ALLOCATION", null));
        cshAllocation.setAllocationDate(df.parse(df.format(new Date())));
        cshAllocation.setAllocationSource("AUTO");
        cshAllocation.setAllocationStatus("N");
        self().insertSelective(iRequest, cshAllocation);


        for (HlsCusCshTransaction transaction : cshTransactionList) {
            CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
            cshAllocationReceipt.setAllocationId(cshAllocation.getAllocationId());
            cshAllocationReceipt.setTransactionId(transaction.getTransactionId());
            cshAllocationReceiptService.insertSelective(iRequest, cshAllocationReceipt);
        }
        //匹配问题 收款未核销金额不等于现金流应收金额则重新查询现金流
        if (cshTransactionList.get(0).getUnWriteOffAmount().compareTo(conContractCashflowList.get(0).getDueAmount()) == 0) {
            for (HlsCusConContractCashflow cashflow : conContractCashflowList) {
                CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
                cshAllocationCredit.setAllocationId(cshAllocation.getAllocationId());
                cshAllocationCredit.setCashflowId(cashflow.getCashflowId());
                cshAllocationCredit.setDueAmount(cashflow.getDueAmount() - cashflow.getReceivedAmount());
                cshAllocationCredit.setPrincipal(cashflow.getPrincipal());
                cshAllocationCredit.setInterest(cashflow.getInterest());
                cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
            }
        } else if ("Two".equalsIgnoreCase(strTwo)) {
            HlsCusConContractCashflow cashflowSignleTenant = new HlsCusConContractCashflow();
            cashflowSignleTenant.setIsSingleTenant("Y");
            cashflowSignleTenant.setTenantId(cshTransactionList.get(0).getBpId());
            cashflowSignleTenant.setDueDate(cshTransactionList.get(0).getTransactionDate());
            List<HlsCusConContractCashflow> cashflowSingleList1 = hlsCusConContractCashflowMapper.queryAllocationCashflow4(cashflowSignleTenant);
            if (cashflowSingleList1.size() > 0) {
                for (HlsCusConContractCashflow cashflow : cashflowSingleList1) {
                    CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
                    cshAllocationCredit.setAllocationId(cshAllocation.getAllocationId());
                    cshAllocationCredit.setCashflowId(cashflow.getCashflowId());
                    cshAllocationCredit.setDueAmount(cashflow.getDueAmount() - cashflow.getReceivedAmount());
                    cshAllocationCredit.setPrincipal(cashflow.getPrincipal());
                    cshAllocationCredit.setInterest(cashflow.getInterest());
                    cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
                }
            }
        } else {
            for (HlsCusConContractCashflow cashflow : conContractCashflowList) {
                CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
                cshAllocationCredit.setAllocationId(cshAllocation.getAllocationId());
                cshAllocationCredit.setCashflowId(cashflow.getCashflowId());
                cshAllocationCredit.setDueAmount(cashflow.getDueAmount() - cashflow.getReceivedAmount());
                cshAllocationCredit.setPrincipal(cashflow.getPrincipal());
                cshAllocationCredit.setInterest(cashflow.getInterest());
                cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
            }
        }

        return cshAllocation;
    }

    @Override
    public List<CshAllocation> autoAllocation(IRequest iRequest, String transactionIdStr) throws ResMessageException, ParseException,HlsCusException {

        List<Long> transactionIdS = new ArrayList<>();

        String[] str = transactionIdStr.split(",");
        for (int i = 0; i < str.length; i++) {
            if (!"undefined".equals(str[i])) {
                transactionIdS.add(Long.parseLong(str[i]));
            }
        }
        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        cshTransaction.setTransactionIdS(transactionIdS);
        //查找出 所有前台勾选的现金事务
        List<HlsCusCshTransaction> cshTransactionList = hlsCusCshTransactionMapper.detailQuery(cshTransaction);

        //校验 收款流水未反冲、未核销、未退款才能参与自动分配
        transactionCheck(cshTransactionList);
        if (cshTransaction.getBpId() == null){
            throw  new HlsCusException("没有收款对象无法自动匹配债权");
        }
        //按照收款对象分组
        Set<Long> bpIdSet = cshTransactionList.stream().collect(Collectors.groupingBy(HlsCusCshTransaction::getBpId)).keySet();

        Long[] bpIds = bpIdSet.toArray(new Long[0]);

        List<CshAllocation> cshAllocationList = new ArrayList<>();

        for (int i = 0; i < bpIds.length; i++) {
            List<HlsCusCshTransaction> transactionList = new ArrayList<>();
            CshAllocation cshAllocation = new CshAllocation();

            Long bpId = bpIds[i];

            transactionList = cshTransactionList.stream().filter(item -> bpId.compareTo(item.getBpId()) == 0).collect(Collectors.toList());
            //当前收款对象对应的收款流水总额
            Double unWriteOffAmount = HlsCusMathUtil.round(transactionList.stream().collect(Collectors.summingDouble(HlsCusCshTransaction::getUnWriteOffAmount)), 2);

            Double canWriteOffAmount = 0.0;
            Double canTotalalWriteOffAmount = 0.0;


            //优先查找没有共同承租人的合同 现金流
            HlsCusConContractCashflow cashflowSignleTenant = new HlsCusConContractCashflow();
            cashflowSignleTenant.setIsSingleTenant("Y");
            cashflowSignleTenant.setTenantId(bpId);
            List<HlsCusConContractCashflow> bpIdList = hlsCusConContractCashflowMapper.queryAllocationBpId(cashflowSignleTenant);
            if (bpIdList.size() > 0 && null != bpIdList.get(0)) {
                cashflowSignleTenant.setDueDate(cshTransactionList.get(0).getTransactionDate());
                List<HlsCusConContractCashflow> cashflowSingleList1 = hlsCusConContractCashflowMapper.queryAllocationCashflow(cashflowSignleTenant);

                /*bug fix(2023-02-02): cashflowSingleList1只查询返回getTotalAmount的值，将cashflowSingleList1传入createAllocation()会导致空指针*/
                List<HlsCusConContractCashflow> cashflowSingleList4 = hlsCusConContractCashflowMapper.queryAllocationCashflow4(cashflowSignleTenant);

                if (cashflowSingleList1.size() > 0 && null != cashflowSingleList1.get(0)) {
                    //查找收款日期与现金流due_date是同一天 并且可以核销金额等于totalAmount
                    canWriteOffAmount = cashflowSingleList1.get(0).getTotalAmount();
                    if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                        cshAllocation = createAllocation(iRequest, transactionList, cashflowSingleList4, "Two");
                        cshAllocationList.add(cshAllocation);
                    } else {
                        List<HlsCusConContractCashflow> cashflowSingleList2 = hlsCusConContractCashflowMapper.queryAllocationCashflow1(cashflowSignleTenant);

                        List<HlsCusConContractCashflow> cashflowSingleList3 = hlsCusConContractCashflowMapper.queryAllocationCashflow3(cashflowSignleTenant);
                        List<HlsCusConContractCashflow> cashflowSingleListL = new ArrayList<>();
                        if (null != cashflowSingleList3.get(0)) {
                            canTotalalWriteOffAmount = cashflowSingleList3.get(0).getTotalAmount();

                        }
                        //查找收款日期与现金流due_date是同一个月 并且可以核销金额等于totalAmount
                        if (cashflowSingleList2.size() > 0 && null != cashflowSingleList3.get(0)) {
                            for (HlsCusConContractCashflow cashflowSingleList : cashflowSingleList2) {
                                canWriteOffAmount = cashflowSingleList.getDueAmount() - cashflowSingleList.getReceivedAmount();

                                if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                                    cashflowSingleListL.add(cashflowSingleList);
                                    BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                    double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                    unWriteOffAmount = f1;

                                } else {
                                    if (unWriteOffAmount.compareTo(canTotalalWriteOffAmount) == 0) {
                                        cashflowSingleListL.add(cashflowSingleList);
                                        BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        unWriteOffAmount = f1;

                                        BigDecimal c = new BigDecimal(canTotalalWriteOffAmount - canWriteOffAmount);
                                        double f2 = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        canTotalalWriteOffAmount = f2;
                                    }

                                }


                            }
                            if (cashflowSingleListL.size() > 0) {
                                cshAllocation = createAllocation(iRequest, transactionList, cashflowSingleListL, null);
                                cshAllocationList.add(cshAllocation);
                            }


                        }/*else{
                        canWriteOffAmount = cashflowSingleList3.get(0).getTotalAmount();
                        if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                            cshAllocation = createAllocation(iRequest, transactionList, cashflowSingleList3);
                            cshAllocationList.add(cshAllocation);
                        }
                    }*/
                    }
                } else {
                    cashflowSignleTenant.setDueDate(cshTransactionList.get(0).getTransactionDate());

                    List<HlsCusConContractCashflow> cashflowSingleList2 = hlsCusConContractCashflowMapper.queryAllocationCashflow1(cashflowSignleTenant);

                    List<HlsCusConContractCashflow> cashflowSingleList3 = hlsCusConContractCashflowMapper.queryAllocationCashflow3(cashflowSignleTenant);
                    List<HlsCusConContractCashflow> cashflowSingleListL = new ArrayList<>();
/*
                    canTotalalWriteOffAmount = cashflowSingleList3.get(0).getTotalAmount();
*/
                    if (null != cashflowSingleList3.get(0)) {
                        canTotalalWriteOffAmount = cashflowSingleList3.get(0).getTotalAmount();

                    }
                    if (cashflowSingleList2.size() > 0 && null != cashflowSingleList3.get(0)) {
                        for (HlsCusConContractCashflow cashflowSingleList : cashflowSingleList2) {
                            canWriteOffAmount = cashflowSingleList.getDueAmount() - cashflowSingleList.getReceivedAmount();

                            if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                                cashflowSingleListL.add(cashflowSingleList);
                                BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                unWriteOffAmount = f1;

                            } else {

/*
                                canWriteOffAmount = cashflowSingleList3.get(0).getTotalAmount();
*/
                                if (unWriteOffAmount.compareTo(canTotalalWriteOffAmount) == 0) {
                                    cashflowSingleListL.add(cashflowSingleList);
                                    BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                    double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    unWriteOffAmount = f1;
                                    BigDecimal c = new BigDecimal(canTotalalWriteOffAmount - canWriteOffAmount);
                                    double f2 = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    canTotalalWriteOffAmount = f2;

                                }

                            }


                        }
                        if (cashflowSingleListL.size() > 0) {
                            cshAllocation = createAllocation(iRequest, transactionList, cashflowSingleListL, null);
                            cshAllocationList.add(cshAllocation);
                        }


                    }/*else{
                        canWriteOffAmount = cashflowSingleList3.get(0).getTotalAmount();
                        if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                            cshAllocation = createAllocation(iRequest, transactionList, cashflowSingleList3);
                            cshAllocationList.add(cshAllocation);
                        }
                    }*/
                }

            } else {
                HlsCusConContractCashflow cashflowUnionTenant = new HlsCusConContractCashflow();
                cashflowUnionTenant.setIsUnionTenant("Y");
                cashflowUnionTenant.setTenantId(bpId);
                List<HlsCusConContractCashflow> bpIdList1 = hlsCusConContractCashflowMapper.queryAllocationBpId(cashflowUnionTenant);
                if (bpIdList1.size() > 0 && null != bpIdList1.get(0)) {
                    cashflowUnionTenant.setDueDate(cshTransactionList.get(0).getTransactionDate());
                    cashflowSignleTenant.setDueDate(cshTransactionList.get(0).getTransactionDate());
                    List<HlsCusConContractCashflow> cashflowUnionList = hlsCusConContractCashflowMapper.queryAllocationCashflow(cashflowUnionTenant);

                    /*bug fix(2023-02-02): cashflowUnionList只查询返回getTotalAmount的值，将cashflowUnionList传入createAllocation()会导致空指针*/
                    List<HlsCusConContractCashflow> cashflowUnionList4 = hlsCusConContractCashflowMapper.queryAllocationCashflow4(cashflowSignleTenant);
                    if (cashflowUnionList.size() > 0 && null != cashflowUnionList.get(0) && cashflowUnionList4.size() > 0 && null != cashflowUnionList4.get(0)) {
                        canWriteOffAmount = cashflowUnionList.get(0).getTotalAmount();
                        if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                            cshAllocation = createAllocation(iRequest, transactionList, cashflowUnionList4, null);
                            cshAllocationList.add(cshAllocation);
                        } else {
                            List<HlsCusConContractCashflow> cashflowUnionList1 = hlsCusConContractCashflowMapper.queryAllocationCashflow1(cashflowUnionTenant);
                            List<HlsCusConContractCashflow> cashflowUnionList3 = hlsCusConContractCashflowMapper.queryAllocationCashflow3(cashflowUnionTenant);
                            List<HlsCusConContractCashflow> cashflowSingleListL = new ArrayList<>();
                            if (null != cashflowUnionList3.get(0)) {
                                canTotalalWriteOffAmount = cashflowUnionList3.get(0).getTotalAmount();

                            }
                            if (cashflowUnionList1.size() > 0 && null != cashflowUnionList3.get(0)) {
                                for (HlsCusConContractCashflow cashflowSingleListt : cashflowUnionList1) {
                                    canWriteOffAmount = cashflowSingleListt.getDueAmount() - cashflowSingleListt.getReceivedAmount();

                                    if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                                        cashflowSingleListL.add(cashflowSingleListt);
                                        BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        unWriteOffAmount = f1;
                                    } else {

                                        if (unWriteOffAmount.compareTo(canTotalalWriteOffAmount) == 0) {
                                            cashflowSingleListL.add(cashflowSingleListt);
                                            BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                            double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                            BigDecimal c = new BigDecimal(canTotalalWriteOffAmount - canWriteOffAmount);
                                            double f2 = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                            unWriteOffAmount = f1;
                                            canTotalalWriteOffAmount = f2;

                                        }
                                    }
                                }
                                if (cashflowSingleListL.size() > 0) {
                                    cshAllocation = createAllocation(iRequest, transactionList, cashflowSingleListL, null);
                                    cshAllocationList.add(cshAllocation);
                                }


                            }
                        }
                    } else {
                        cashflowUnionTenant.setDueDate(cshTransactionList.get(0).getTransactionDate());

                        List<HlsCusConContractCashflow> cashflowUnionList1 = hlsCusConContractCashflowMapper.queryAllocationCashflow1(cashflowUnionTenant);
                        List<HlsCusConContractCashflow> cashflowUnionList3 = hlsCusConContractCashflowMapper.queryAllocationCashflow3(cashflowUnionTenant);
                        List<HlsCusConContractCashflow> cashflowSingleListL = new ArrayList<>();
                        if (null != cashflowUnionList3.get(0)) {
                            canTotalalWriteOffAmount = cashflowUnionList3.get(0).getTotalAmount();

                        }
                        if (cashflowUnionList1.size() > 0 && null != cashflowUnionList3.get(0)) {
                            for (HlsCusConContractCashflow cashflowSingleListt : cashflowUnionList1) {
                                canWriteOffAmount = cashflowSingleListt.getDueAmount() - cashflowSingleListt.getReceivedAmount();
                                if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                                    cashflowSingleListL.add(cashflowSingleListt);
                                    BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                    double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    unWriteOffAmount = f1;
                                } else {
                                    if (unWriteOffAmount.compareTo(canTotalalWriteOffAmount) == 0) {
                                        cashflowSingleListL.add(cashflowSingleListt);
                                        BigDecimal b = new BigDecimal(unWriteOffAmount - canWriteOffAmount);
                                        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        BigDecimal c = new BigDecimal(canTotalalWriteOffAmount - canWriteOffAmount);
                                        double f2 = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        unWriteOffAmount = f1;
                                        canTotalalWriteOffAmount = f2;
                                    }
                                }
                            }
                            if (cashflowSingleListL.size() > 0) {
                                cshAllocation = createAllocation(iRequest, transactionList, cashflowSingleListL, null);
                                cshAllocationList.add(cshAllocation);
                            }


                        }/*else{
                            canWriteOffAmount = cashflowUnionList3.get(0).getTotalAmount();
                            if (unWriteOffAmount.compareTo(canWriteOffAmount) == 0) {
                                cshAllocation = createAllocation(iRequest, transactionList, cashflowUnionList3);
                                cshAllocationList.add(cshAllocation);
                            }
                        }*/


                    }
                }
            }
        }
        String allocationIdStr = "";
        for (int i = 0; i < cshAllocationList.size(); i++) {
            if (i == 0) {
                allocationIdStr = cshAllocationList.get(0).getAllocationId().toString();
            } else {
                allocationIdStr = allocationIdStr + ',' + cshAllocationList.get(i).getAllocationId().toString();
            }
        }
        String finalAllocationIdStr = allocationIdStr;

        cshAllocationList.stream().forEach(item -> item.setAllocationIdStr(finalAllocationIdStr));

        return cshAllocationList;
    }
}