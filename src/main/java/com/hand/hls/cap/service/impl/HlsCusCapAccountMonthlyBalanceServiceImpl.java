package com.hand.hls.cap.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cap.dto.HlsCusCapAccountMonthlyBalance;
import com.hand.hls.cap.dto.HlsCusCapPkg;
import com.hand.hls.cap.mapper.HlsCusCapAccountMonthlyBalanceMapper;
import com.hand.hls.cap.service.HlsCusCapAccountMonthlyBalanceService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
import com.hand.hls.inv.service.HlsCusIFinanceRedeemService;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCapAccountMonthlyBalanceServiceImpl extends BaseServiceImpl<HlsCusCapAccountMonthlyBalance> implements HlsCusCapAccountMonthlyBalanceService {

    @Autowired
    HlsCusCapAccountMonthlyBalanceMapper balanceMapper;
//    @Autowired
//    private HlsCusBpMasterFactorMapper bpMasterFactorMapper;
    @Autowired
HlsCusIFinancePurchaseService hlsCusIFinancePurchaseService;

    @Autowired
    HlsCusIFinanceRedeemService hlsCusIFinanceRedeemService;
//    @Autowired
//    private HlsCusCapitalOtherPlanMapper capitalOtherPlanMapper;

    @Autowired
    private ICodeService codeService;

    @Autowired
    private FndCompanyMapper fndCompanyMapper;

    Calendar cl = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final String DAY = "DAY";
    private static final String MONTH = "MONTH";
    private static final String QUARTER = "QUARTER";
    private static final String HALFYEAR = "HALFYEAR";
    private static final String YEAR = "YEAR";
    /**
     * 初始化金额
     */
    private static final Double ZERO = 0D;
    /**
     * 公司ID
     */
    private static final Long CT = 261L;
    private static final Long CX = 264L;

//    private String getLastDate(String date) throws ParseException {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(dateFormat.parse(date));
//        calendar.add(Calendar.MONTH, 1);
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        String end = dateFormat.format(calendar.getTime());
//        return end;
//    }
//
//    private String getFirtDate(String date) throws ParseException {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(dateFormat.parse(date));
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        String start = dateFormat.format(calendar.getTime());
//        return start;
//    }
//
//    private long getDays(String end, String start) throws ParseException {
//        return (dateFormat.parse(end).getTime() - dateFormat.parse(start).getTime()) / 1000 / 60 / 60 / 24;
//    }
//
//    private int getMonthsBetween(Date end, Date start) {
//        int months = DateUtils.getMonthValue(end) - DateUtils.getMonthValue(start);
//        int yesrs = DateUtils.getYear(end) - DateUtils.getYear(start);
//        months = yesrs * 12 + months + 1;
//        return months;
//    }
//
//    //根据bankAccountId和期间查询其它金额
//    private Double queryOtherAmount(Long bankAccountId, String period) {
//        HlsCusCapAccountMonthlyBalance dto = new HlsCusCapAccountMonthlyBalance();
//        dto.setBankAccountId(bankAccountId);
//        dto.setPeriod(period);
//        Double otherAmount = 0D;
//        dto = balanceMapper.queryByPeriodAndAccount(dto);
//        if (dto != null) {
//            otherAmount = dto.getOtherAmount();
//        }
//        return otherAmount;
//    }
//
//    @SuppressWarnings("ALL")
//    //根据起始日期和截止日期，查出每个月的其它，再均摊到每一天
//    private Double queryOtherAmountBetween(String start, String end, HlsCusCapAccountMonthlyBalance balance, Boolean headCount) throws ParseException {
//        //查询其它
//        Double other = 0D;
//        Date startDate = dateFormat.parse(start);
//        Date endDate = dateFormat.parse(end);
//        //获取该日期月份最后一天
//        String firstMonthEnd = this.getLastDate(start);
//        String lastMonthEnd = this.getLastDate(end);
//        //获取该日期月份的第一天
//        String firstMonthStart = this.getFirtDate(start);
//        String lastMonthStart = this.getFirtDate(end);
//        //计算两个日期相差月份
//        long days = 0;
//        long total = 0;
//        int months = this.getMonthsBetween(endDate, startDate);
//        if (months == 1) {
//            days = this.getDays(end, start);
//            if (headCount) days++;
//            total = this.getDays(lastMonthEnd, firstMonthStart) + 1;
//            other = this.queryOtherAmount(balance.getBankAccountId(), start.substring(0, 7)) * days / total;
//        } else {
//            for (int i = 0; i < months; i++) {
//                if (i == 0) {
//                    days = this.getDays(firstMonthEnd, start);
//                    if (headCount) days++;
//                    total = this.getDays(firstMonthEnd, firstMonthStart) + 1;
//                    other = other + this.queryOtherAmount(balance.getBankAccountId(), start.substring(0, 7)) * days / total;
//                } else if (i == months - 1) {
//                    days = this.getDays(end, lastMonthStart);
//                    if (headCount) days++;
//                    total = this.getDays(lastMonthEnd, lastMonthStart) + 1;
//                    other = other + this.queryOtherAmount(balance.getBankAccountId(), end.substring(0, 7)) * days / total;
//                } else {
//                    long k = (long) i;
//                    Date cur = DateUtils.plusMonths(startDate, k);
//                    String current = dateFormat.format(cur);
//                    other = other + this.queryOtherAmount(balance.getBankAccountId(), current.substring(0, 7));
//                }
//            }
//        }
//        return other;
//    }
//
//
//    //根据其实日期计算账户余额
//    private Double queryAccountBalanceByStartPeriod(String startPeriod, HlsCusCapAccountMonthlyBalance balance) throws ParseException {
//        Double accountBalance = 0D;
//        //获取起始日期的一号
//        String start = this.getFirtDate(startPeriod);
//        //获取起始日期最后一天
//        String monthEnd = this.getLastDate(startPeriod);
//
//        HlsCusCapAccountMonthlyBalance hlsCusCapAccountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
//        hlsCusCapAccountMonthlyBalance.setBankAccountId(balance.getBankAccountId());
//        hlsCusCapAccountMonthlyBalance.setPeriod(startPeriod.substring(0, 7));
//        List<HlsCusCapAccountMonthlyBalance> balanceList = balanceMapper.queryBeforePeriodAndAccount(hlsCusCapAccountMonthlyBalance);
//        //账户余额取起始日期期间的期初金额，如果没有当月月结数据，则取最近一条数据的期初
//        String openingPeriod = this.getFirtDate(startPeriod);
//        if (!balanceList.isEmpty()) {
//            openingPeriod = balanceList.get(0).getPeriod() + "-01";
//            accountBalance = balanceList.get(0).getOpeningBalance();
//        }
//        // 再根据选择的日期，计算openingPeriod的一号至该日期的所有收付
//        HlsCusCapAccountMonthlyBalance capAccountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
//        capAccountMonthlyBalance.setStart(openingPeriod);
//        capAccountMonthlyBalance.setEnd(startPeriod);
//        capAccountMonthlyBalance.setBankAccountId(balance.getBankAccountId());
//        List<HlsCusCapAccountMonthlyBalance> hlsCusCapAccountMonthlyBalances = balanceMapper.accountFlowQuery(capAccountMonthlyBalance);
//        for (HlsCusCapAccountMonthlyBalance b : hlsCusCapAccountMonthlyBalances) {
//            if (StringUtils.equalsIgnoreCase(b.getTransactionType(), "RECEIPT") || StringUtils.equalsIgnoreCase(b.getTransactionType(), "LON_WITHDRAW")) {
//                accountBalance = accountBalance + b.getTransactionAmount();
//            } else {
//                accountBalance = accountBalance - b.getTransactionAmount();
//            }
//        }
//        // 其他均摊，算出余额
//        accountBalance = accountBalance + this.queryOtherAmountBetween(openingPeriod, startPeriod, capAccountMonthlyBalance, false);//算头不算尾
//        return accountBalance;
//    }
//
//    @SuppressWarnings("ALL")
//    //查出受限制金额
//    private Double restrictedAmountQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//        Double restrictedAmount = ZERO;
//        HlsCusFinancePurchase financePurchase = new HlsCusFinancePurchase();
//        financePurchase.setPurchaseStatus("APPROVED");
//        financePurchase.setBankAccountId(dto.getBankAccountId());
//        financePurchase.setFinancialType("CLOSE");//只统计封闭式的
//        List<HlsCusFinancePurchase> financePurchases = hlsCusIFinancePurchaseService.select(request, financePurchase, 1, 99999);
//        //只有当前日期在起息日(算)到结息日(不算)之前才记录
//        List<HlsCusFinancePurchase> list = financePurchases.stream().filter(cf -> cf.getExpectedDueDate().after(new Date()) && !cf.getValueDate().after(new Date())).collect(Collectors.toList());
//        //如果期间是当月,则用当天判断
//        String period = dto.getPeriod();
//        String today = sdf.format(new Date());
//        if (StringUtils.equalsIgnoreCase(period, today)) {
//            for (HlsCusFinancePurchase purchase : list) {
//                //当前日期在起息日和结息日之间,则为受限金额
//                if (!purchase.getValueDate().after(new Date()) && !purchase.getExpectedDueDate().before(new Date())) {
//                    restrictedAmount = restrictedAmount + purchase.getInvestmentAmount();
//                }
//            }
//        } else {
//            // 不是当月，那么只要期间月份在起息日到结息日之间，就算作受限制金额
//            for (HlsCusFinancePurchase purchase : list) {
//                String dateFrom = sdf.format(purchase.getValueDate());
//                String dateTo = sdf.format(purchase.getExpectedDueDate());
//                if (period.compareTo(dateFrom) >= 0 && period.compareTo(dateTo) <= 0) {
//                    restrictedAmount = restrictedAmount + purchase.getInvestmentAmount();
//                }
//            }
//        }
//        return restrictedAmount;
//    }

    //查出收付记录
    private HlsCusCapAccountMonthlyBalance flowQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
        List<HlsCusCapAccountMonthlyBalance> balances = self().accountFlowQuery(request, dto, 1, 99999);
        Double paidAmount = ZERO;
        Double receivedAmount = ZERO;
        for (HlsCusCapAccountMonthlyBalance balance : balances) {
            if ("RECEIPT".equalsIgnoreCase(balance.getTransactionType()) || "LON_WITHDRAW".equalsIgnoreCase(balance.getTransactionType())) {
                receivedAmount = receivedAmount + balance.getTransactionAmount();
            } else {
                paidAmount = paidAmount + balance.getTransactionAmount();
            }
        }
        dto.setReceivedAmount(receivedAmount);
        dto.setPaidAmount(paidAmount);
        return dto;
    }

//    //查询最近一个已保存的余额
//    @SuppressWarnings("ALL")
//    private HlsCusCapAccountMonthlyBalance queryLatestSavedBalance(HlsCusCapAccountMonthlyBalance dto, IRequest request) {
//        HlsCusCapAccountMonthlyBalance capAccountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
//        capAccountMonthlyBalance.setBankAccountId(dto.getBankAccountId());
//        capAccountMonthlyBalance.setPeriod(dto.getPeriod());
//        List<HlsCusCapAccountMonthlyBalance> cusCapAccountMonthlyBalances = self().queryBeforePeriodAndAccount(request, capAccountMonthlyBalance);
//        if (cusCapAccountMonthlyBalances.isEmpty()) {
//            dto.setOpeningBalance(0.00);
//        } else {
//            dto.setOpeningBalance(cusCapAccountMonthlyBalances.get(0).getAccountBalance());
//        }
//        return dto;
//    }
//
//    @SuppressWarnings("ALL")
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> capAccountQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) throws ParseException {
//        PageHelper.startPage(page, pageSize);
//        List<HlsCusCapAccountMonthlyBalance> balanceList = balanceMapper.capAccountQuery(dto);
//        if (dto.getStartPeriod() != null && dto.getEndPeriod() != null) {
//            for (HlsCusCapAccountMonthlyBalance balance : balanceList) {
//                balance.setOther(this.queryOtherAmountBetween(dto.getStartPeriod(), dto.getEndPeriod(), balance, true));
//                balance.setAccountBalance(this.queryAccountBalanceByStartPeriod(dto.getStartPeriod(), balance));
//                //计算保理/租赁 + 融资的现金流
//                HlsCusCapAccountMonthlyBalance capAccountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
//                capAccountMonthlyBalance.setBankAccountId(balance.getBankAccountId());
//                capAccountMonthlyBalance.setPeriod(dto.getEndPeriod());
//                capAccountMonthlyBalance.setStartPeriod(dto.getStartPeriod());
//                List<HlsCusCapAccountMonthlyBalance> cBalances = self().queryCtCxCashDetail(request, capAccountMonthlyBalance, 1, 0);
//                Double amount = 0D;
//                for (HlsCusCapAccountMonthlyBalance c : cBalances) {
//                    if (request.getCompanyId() == 261L) {
//                        amount = amount + c.getDueAmount() + c.getAdviceAmount() + c.getServiceAmount() + c.getDepositAmount() - c.getDepositOut() - c.getAmount() + c.getResidualAmount();
//                    } else if (request.getCompanyId() == 264L) {
//                        amount = amount + c.getDueAmount() + c.getUseAmount() + c.getDepositAmount() + c.getServiceAmount() + c.getCapitalAmount() + c.getGraceAmount() + c.getOtherAmount()
//                                - c.getDepositAmountOut() - c.getAmount();
//                    }
//                }
//                List<HlsCusCapAccountMonthlyBalance> lBalances = self().queryLonCashDetail(request, capAccountMonthlyBalance, 1, 0);
//                for (HlsCusCapAccountMonthlyBalance l : lBalances) {
//                    amount = amount + l.getWithdrawAmount() - l.getRepaymentAmount() - l.getInterestAmount() - l.getConsignmentSalesFee() - l.getCollocationFee() - l.getManagementFee()
//                            - l.getChargeFee() - l.getConsultingFee() - l.getDeposit() - l.getOtherAmount();
//                }
//                balance.setPredictedAmount(balance.getAccountBalance() + balance.getOther() + amount);
//            }
//        }
//        //计算受限制金额
//        for (HlsCusCapAccountMonthlyBalance b : balanceList) {
//            HlsCusCapAccountMonthlyBalance balance = new HlsCusCapAccountMonthlyBalance();
//            balance.setPeriod(dto.getPeriod());
//            balance.setBankAccountId(b.getBankAccountId());
//            b.setRestrictedAmount(this.restrictedAmountQuery(request, balance));
//        }
//        return balanceList;
//    }

    @Override
    public List<HlsCusCapAccountMonthlyBalance> accountFlowQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusCapAccountMonthlyBalance> balances = balanceMapper.accountFlowQuery(dto);
        return balances;
    }

    @Override
    public HlsCusCapAccountMonthlyBalance queryByPeriodAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
        return balanceMapper.queryByPeriodAndAccount(dto);
    }

    @Override
    public HlsCusCapAccountMonthlyBalance queryBeforeMinBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
        return balanceMapper.queryBeforeMinBalanceDateAndAccount(dto);
    }

    @Override
    public HlsCusCapAccountMonthlyBalance queryByBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
        return balanceMapper.queryByBalanceDateAndAccount(dto);
    }

//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryBeforePeriodAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        return balanceMapper.queryBeforePeriodAndAccount(dto);
//    }
//
    @Override
    public List<HlsCusCapAccountMonthlyBalance> queryBeforeBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
        return balanceMapper.queryBeforeBalanceDateAndAccount(dto);
    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryAfterPeriodAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        return balanceMapper.queryAfterPeriodAndAccount(dto);
//    }

    @Override
    public List<HlsCusCapAccountMonthlyBalance> queryAfterBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
        return balanceMapper.queryAfterBalanceDateAndAccount(dto);
    }

//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> accountReceivedAndPaid(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        return balanceMapper.accountReceivedAndPaid(dto);
//    }
//
//    @Override
//    public HlsCusCapAccountMonthlyBalance queryBalanceInfo(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        //查出受限制金额
//        Double restrictedAmount = this.restrictedAmountQuery(request, dto);
//        //查出收付记录
//        HlsCusCapAccountMonthlyBalance flowBalance = this.flowQuery(request, dto);
//        Double paidAmount = flowBalance.getPaidAmount();
//        Double receivedAmount = flowBalance.getReceivedAmount();
//
//        HlsCusCapAccountMonthlyBalance hlsCusCapAccountMonthlyBalance = self().queryByPeriodAndAccount(request, dto);
//        if (hlsCusCapAccountMonthlyBalance == null) {
//            HlsCusCapAccountMonthlyBalance balance = new HlsCusCapAccountMonthlyBalance();
//            String period = dto.getPeriod();
//            try {
//                period = self().monthCal(period, -1);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            balance.setPeriod(period);
//            balance = self().queryByPeriodAndAccount(request, balance);
//            if (balance != null && "FULL".equalsIgnoreCase(balance.getBalanceStatus())) {
//                dto.setOpeningBalance(balance.getAccountBalance());
//            } else if (balance == null || !"FULL".equalsIgnoreCase(balance.getBalanceStatus())) {
//                //当上一期未月结,查询最近一个已保存的余额
//                dto = this.queryLatestSavedBalance(dto, request);
//            } else {
//                dto.setOpeningBalance(0.00);
//            }
//            dto.setBalanceStatus("NOT");
//            dto.setPaidAmount(paidAmount);
//            dto.setReceivedAmount(receivedAmount);
//            dto.setRestrictedAmount(restrictedAmount);
//            dto.setAccountBalance(dto.getOpeningBalance() - paidAmount + receivedAmount);
//        } else if ("NOT".equalsIgnoreCase(hlsCusCapAccountMonthlyBalance.getBalanceStatus())) {
//            dto = hlsCusCapAccountMonthlyBalance;
//            dto.setReceivedAmount(receivedAmount);
//            dto.setPaidAmount(paidAmount);
//            dto.setRestrictedAmount(restrictedAmount);
//            dto.setAccountBalance(dto.getOpeningBalance() - paidAmount + receivedAmount + dto.getOtherAmount());
//        } else {
//            dto = hlsCusCapAccountMonthlyBalance;
//            dto.setRestrictedAmount(restrictedAmount);
//        }
//        return dto;
//    }

    @Override
    public HlsCusCapAccountMonthlyBalance monthConfirm(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
        dto.setBalanceStatus("FULL");
        self().updateByPrimaryKeySelective(request, dto);
        /*插入下个月数据*/
        try {
            dto.setPeriod(monthCal(dto.getPeriod(), 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //先查询该period数据是否存在
        HlsCusCapAccountMonthlyBalance newBalance = self().queryByPeriodAndAccount(request, dto);
        dto.setOpeningBalance(dto.getAccountBalance());
        dto.setAccountBalance(ZERO);
        //查出收付记录
        HlsCusCapAccountMonthlyBalance flowBalance = this.flowQuery(request, dto);
        dto.setPaidAmount(flowBalance.getPaidAmount());
        dto.setReceivedAmount(flowBalance.getReceivedAmount());
        dto.setOtherAmount(ZERO);
        dto.setBalanceStatus("NOT");
        dto.setBalanceId(null);
        dto.setAccountBalance(dto.getOpeningBalance() + dto.getReceivedAmount() - dto.getPaidAmount());
        dto.setCreatedBy(request.getUserId());
        dto.setLastUpdatedBy(request.getUserId());
        dto.setLastUpdateLogin(request.getUserId());
        if (newBalance != null) {
            dto.setBalanceId(newBalance.getBalanceId());
            dto = self().updateByPrimaryKeySelective(request, dto);
        } else {
            dto = self().insertSelective(request, dto);
        }
        return dto;
    }

    @Override
    public String monthCal(String period, int amount) throws ParseException {
        Date periodDate = sdf.parse(period);
        cl.setTime(periodDate);
        cl.add(Calendar.MONTH, amount);
        period = sdf.format(cl.getTime());
        return period;
    }

    @Override
    public HlsCusCapPkg dayBalanceSave(IRequest request, HlsCusCapPkg pkg) {
        /*如果上一个结算期间未结算则不能保存*/
        /*modify: 如果之前从未进行过保存(账户是新建的)就可以保存*/
        List<HlsCusCapAccountMonthlyBalance> AllList = pkg.getHlsCusCapAccountMonthlyBalanceList();
        if (CollectionUtils.isNotEmpty(AllList)) {
            for (HlsCusCapAccountMonthlyBalance dto : AllList) {
                HlsCusCapAccountMonthlyBalance monthlyBalance = new HlsCusCapAccountMonthlyBalance();
                Date balanceDate = dto.getBalanceDate();
                Long bankAccountId = dto.getBankAccountId();
                HlsCusCapAccountMonthlyBalance accountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
                accountMonthlyBalance.setBankAccountId(bankAccountId);
                accountMonthlyBalance.setBalanceDate(balanceDate);
                List<HlsCusCapAccountMonthlyBalance> accountMonthlyBalanceList = this.queryBeforeBalanceDateAndAccount(request, accountMonthlyBalance);
                Boolean newFlag = false;
                if (accountMonthlyBalanceList.isEmpty())
                    newFlag = true;
                /*账户非新建时，才进行结算日期之前的情况判断*/
                if (!newFlag) {
                    monthlyBalance.setBalanceDate(balanceDate);
                    monthlyBalance.setBankAccountId(bankAccountId);
                    monthlyBalance = self().queryBeforeMinBalanceDateAndAccount(request, monthlyBalance);
                    if ((monthlyBalance != null && !"FULL".equalsIgnoreCase(monthlyBalance.getBalanceStatus()))) {
                        throw new IllegalArgumentException("请先对账户:" + dto.getBankAccountNum() + "的" + dateFormat.format(monthlyBalance.getBalanceDate()) + "进行结算");
                    }
                }

                //判断其间是否存在
                HlsCusCapAccountMonthlyBalance hlsCusCapAccountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
                hlsCusCapAccountMonthlyBalance.setBankAccountId(dto.getBankAccountId());
                hlsCusCapAccountMonthlyBalance.setBalanceDate(dto.getBalanceDate());
                hlsCusCapAccountMonthlyBalance = self().queryByBalanceDateAndAccount(request, hlsCusCapAccountMonthlyBalance);
                if (hlsCusCapAccountMonthlyBalance != null) {
//                    throw new IllegalArgumentException("该计划区间已存在!请调整");
                    self().updateByPrimaryKeySelective(request, dto);
                } else {
                    self().insertSelective(request, dto);
                }

                //查询是否有后续期间的数据,如果有则逐一更新
                HlsCusCapAccountMonthlyBalance newBalance = new HlsCusCapAccountMonthlyBalance();
                newBalance.setBalanceDate(pkg.getHlsCusCapAccountMonthlyBalance().getBalanceDate());
                newBalance.setBankAccountId(bankAccountId);
                List<HlsCusCapAccountMonthlyBalance> newBalanceList = this.queryAfterBalanceDateAndAccount(request, newBalance);
                for (int i = 0; i < newBalanceList.size(); i++) {
                    if (i == 0) {
                        newBalanceList.get(i).setOpeningBalance(AllList.get(0).getAccountBalance());
                    } else {
                        newBalanceList.get(i).setOpeningBalance(newBalanceList.get(i - 1).getAccountBalance());
                    }
                    newBalanceList.get(i).setAccountBalance(newBalanceList.get(i).getOpeningBalance() + newBalanceList.get(i).getReceivedAmount() - newBalanceList.get(i).getPaidAmount() + newBalanceList.get(i).getOtherAmount());
                    self().updateByPrimaryKeySelective(request, newBalanceList.get(i));
                }
            }
        }

        return pkg;
    }
//
//    @Override
//    public HlsCusCapPkg balanceSave(IRequest request, HlsCusCapPkg pkg) {
//        /*如果上一个会计期间未月结则不能保存*/
//        /*modify: 如果之前从未进行过保存(账户是新建的)就可以保存*/
//        HlsCusCapAccountMonthlyBalance monthlyBalance = new HlsCusCapAccountMonthlyBalance();
//        String period = pkg.getHlsCusCapAccountMonthlyBalance().getPeriod();
//        Long bankAccountId = pkg.getHlsCusCapAccountMonthlyBalanceList().get(0).getBankAccountId();
//        HlsCusCapAccountMonthlyBalance accountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
//        accountMonthlyBalance.setBankAccountId(bankAccountId);
//        accountMonthlyBalance.setPeriod(period);
//        List<HlsCusCapAccountMonthlyBalance> accountMonthlyBalanceList = this.queryBeforePeriodAndAccount(request, accountMonthlyBalance);
//        Boolean newFlag = false;
//        if (accountMonthlyBalanceList.isEmpty())
//            newFlag = true;
//        try {
//            period = self().monthCal(period, -1);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        /*账户非新建时，才进行上月情况判断*/
//        if (!newFlag) {
//            monthlyBalance.setPeriod(period);
//            monthlyBalance.setBankAccountId(pkg.getHlsCusCapAccountMonthlyBalance().getBankAccountId());
//            monthlyBalance = self().queryByPeriodAndAccount(request, monthlyBalance);
//            if ((monthlyBalance == null || !"FULL".equalsIgnoreCase(monthlyBalance.getBalanceStatus()))) {
//                throw new IllegalArgumentException("请先对" + period + "进行月结");
//            }
//        }
//
//        List<HlsCusCapAccountMonthlyBalance> list = pkg.getHlsCusCapAccountMonthlyBalanceList();
//        for (HlsCusCapAccountMonthlyBalance balance : list) {
//            //判断其间是否存在
//            HlsCusCapAccountMonthlyBalance hlsCusCapAccountMonthlyBalance = self().queryByPeriodAndAccount(request, pkg.getHlsCusCapAccountMonthlyBalance());
//            if (balance.getBalanceId() == null || balance.getBalanceId() == 0) {
//                if (hlsCusCapAccountMonthlyBalance != null) {
//                    throw new IllegalArgumentException("该计划区间已存在!请调整");
//                }
//                balance.setCompanyId(request.getCompanyId());
//                self().insertSelective(request, balance);
//            } else {
//                if (hlsCusCapAccountMonthlyBalance != null && hlsCusCapAccountMonthlyBalance.getBalanceId().compareTo(balance.getBalanceId()) != 0) {
//                    throw new IllegalArgumentException("该计划区间已存在!请调整");
//                }
//                self().updateByPrimaryKeySelective(request, balance);
//            }
//        }
//        pkg.setHlsCusCapAccountMonthlyBalanceList(list);
//        //查询是否有后续期间的数据,如果有则逐一更新
//        HlsCusCapAccountMonthlyBalance newBalance = new HlsCusCapAccountMonthlyBalance();
//        newBalance.setPeriod(pkg.getHlsCusCapAccountMonthlyBalance().getPeriod());
//        newBalance.setBankAccountId(bankAccountId);
//        List<HlsCusCapAccountMonthlyBalance> newBalanceList = this.queryAfterPeriodAndAccount(request, newBalance);
//        for (int i = 0; i < newBalanceList.size(); i++) {
//            if (i == 0) {
//                newBalanceList.get(i).setOpeningBalance(list.get(0).getAccountBalance());
//            } else {
//                newBalanceList.get(i).setOpeningBalance(newBalanceList.get(i - 1).getAccountBalance());
//            }
//            newBalanceList.get(i).setAccountBalance(newBalanceList.get(i).getOpeningBalance() + newBalanceList.get(i).getReceivedAmount() - newBalanceList.get(i).getPaidAmount() + newBalanceList.get(i).getOtherAmount());
//            self().updateByPrimaryKeySelective(request, newBalanceList.get(i));
//        }
//        return pkg;
//    }
//
//    private Double nullReturnZero(Double amount) {
//        if (amount == null)
//            return ZERO;
//        return amount;
//    }
//
//    @Override
//    public List<Map> accountChartQuery(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto) {
//        Double restrictedAmount = ZERO;
//
//        if (dto.getBalanceDate() == null) {
//            dto.setBalanceDate(new Date());
//        }
//        List<HlsCusCapAccountMonthlyBalance> balances = balanceMapper.accountBalanceGroupByBank(dto);
//        Double accountBalance = ZERO;
//        for (HlsCusCapAccountMonthlyBalance balance : balances) {
//            balance.setPeriod(dto.getPeriod());
//            accountBalance = accountBalance + nullReturnZero(balance.getAccountBalance());
////            restrictedAmount = restrictedAmount + nullReturnZero(balance.getRestrictedAmount());
////            restrictedAmount = restrictedAmount + this.restrictedAmountQuery(iRequest, balance);
//        }
//        Map map = new HashMap();
//        map.put("availableAmount", accountBalance);
//        map.put("restrictedAmount", restrictedAmount);
//        map.put("investmentAmount", ZERO);
//        List<Map> list = new ArrayList<>();
//        list.add(map);
//        return list;
//    }
//
//    @SuppressWarnings("ALL")
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryPredictAccountStatus(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) throws ParseException {
//        PageHelper.startPage(page, pageSize);
//        dto.setEndPeriod(dateFormat.format(new Date()));
//        HlsCusCapAccountMonthlyBalance capAccountMonthlyBalance = new HlsCusCapAccountMonthlyBalance();
//        BeanUtils.copyProperties(dto, capAccountMonthlyBalance);
//        if (StringUtils.equalsIgnoreCase("PRE", capAccountMonthlyBalance.getPredictOrActual())) {
//            if (dto.getStartPeriod().compareTo(dateFormat.format(new Date())) < 0) {
//                capAccountMonthlyBalance.setStartPeriod(dateFormat.format(new Date()));
//            }
//        } else if (StringUtils.equalsIgnoreCase("ACT", capAccountMonthlyBalance.getPredictOrActual())) {
//            if (dto.getPeriod().compareTo(dateFormat.format(new Date())) > 0) {
//                capAccountMonthlyBalance.setPeriod(dateFormat.format(new Date()));
//            }
//        }
//        List<HlsCusCapAccountMonthlyBalance> balances = new ArrayList<>();
//        if ("Y".equalsIgnoreCase(dto.getGroupEnable())) {
//            balances = balanceMapper.queryPredictAccountStatusGroupByBank(capAccountMonthlyBalance);
//        } else {
//            balances = balanceMapper.queryPredictAccountStatus(capAccountMonthlyBalance);
//        }
//
//        for (HlsCusCapAccountMonthlyBalance balance : balances) {
//            balance.setRepaymentAmount(nullReturnZero(balance.getLonRepaymentAmount()) + nullReturnZero(balance.getRepaymentAmount()));
//            balance.setWithdrawAmount(nullReturnZero(balance.getWithdrawPlanAmount()) + nullReturnZero(balance.getWithdrawAmount()));
//            balance.setLonOtherAmount(nullReturnZero(balance.getLonOtherAmount()) + nullReturnZero(balance.getAnotherAmount()));
//            balance.setAccountBalance(this.queryAccountBalanceByStartPeriod(dto.getStartPeriod(), balance));
//        }
//        for (HlsCusCapAccountMonthlyBalance balance : balances) {
//            balance.setOther(this.queryOtherAmountBetween(dto.getStartPeriod(), dto.getPeriod(), balance, true));//算头算尾
//            //拆其它，今天之前为实际，今天(包括)之后为预计
//            String today = dateFormat.format(new Date());
//            if (dto.getPeriod().compareTo(today) <= 0) {
//                balance.setOtherAct(balance.getOther());
//            } else if (dto.getStartPeriod().compareTo(today) > 0) {
//                balance.setOtherPre(balance.getOther());
//            } else {
//                balance.setOtherAct(this.queryOtherAmountBetween(dto.getStartPeriod(), today, balance, true));//算头算尾
//                balance.setOtherPre(this.queryOtherAmountBetween(today, dto.getPeriod(), balance, false));//不算头算尾
//            }
//        }
//        return balances;
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryPredictUnAccountStatus(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        //TODO
//        List<HlsCusCapAccountMonthlyBalance> returnList = new ArrayList<>();
//        HlsCusCapAccountMonthlyBalance balance = balanceMapper.queryCxPredictUnAccountStatus(dto);
//
//        HlsCusCapAccountMonthlyBalance cusCapAccountMonthlyBalance = balanceMapper.queryCtPredictUnAccountStatus(dto);
//        balance.setAmount(nullReturnZero(cusCapAccountMonthlyBalance.getAmount()));
//        balance.setDueAmount(nullReturnZero(cusCapAccountMonthlyBalance.getDueAmount()));
//        balance.setOtherAmount(nullReturnZero(cusCapAccountMonthlyBalance.getOtherAmount()));
//        balance.setDepositOut(nullReturnZero(cusCapAccountMonthlyBalance.getDepositOut()));
//        balance.setWithdrawAmount(nullReturnZero(balanceMapper.queryWithdrawPredictUnAccountStatus(dto).getWithdrawAmount()));
//        balance.setRepaymentAmount(nullReturnZero(balanceMapper.queryRepaymentPredictUnAccountStatus(dto).getRepaymentAmount()));
//        balance.setLonOtherAmount(nullReturnZero(balanceMapper.queryRepaymentPredictUnAccountStatus(dto).getLonOtherAmount()));
//        returnList.add(balance);
//        return returnList;
//    }
//
//
//    /**
//     * 融资现金流(预测)查询
//     */
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryLonCashDetail(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {
//        PageHelper.startPage(page, pageSize);
//        List<HlsCusCapAccountMonthlyBalance> list = balanceMapper.queryLonCashDetail(dto);
//        list.sort(Comparator.comparing(HlsCusCapAccountMonthlyBalance::getPeriod));
//        return list;
//    }
//
//    /**
//     * 财通/财信现金流(预测)查询
//     */
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryCtCxCashDetail(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {
//        PageHelper.startPage(page, pageSize);
//        if (request.getCompanyId().equals(CT))
//            return balanceMapper.queryCtCashDetail(dto);
//        return balanceMapper.queryCxCashDetail(dto);
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> pressureQueryCx(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        String businessType[] = new String[]{"FACTORING", "REVERSE_FACTORING", "ALL"};
//        return this.pressureQuery(request, dto, "CX", businessType);
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> pressureQueryCt(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        String businessType[] = new String[]{"LEASE", "LEASEBACK", "ALL"};
//        return this.pressureQuery(request, dto, "CT", businessType);
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> pressureQueryTax(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        String businessType[] = new String[]{"LEASE", "LEASEBACK", "FACTORING", "REVERSE_FACTORING", "ALL"};
//        return this.pressureQuery(request, dto, "TAX", businessType);
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> pressureQueryLon(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        String businessType[] = new String[]{"CAPITAL_LOAN", "SHAREHOLDER_LOAN", "OTHERS", "ALL"};
//        return this.pressureQuery(request, dto, "LON", businessType);
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryPressureFlow(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        if ("ONE".equalsIgnoreCase(dto.getType()))
//            return balanceMapper.queryPressureInflowOne(dto);
//        else if ("SIX".equalsIgnoreCase(dto.getType()))
//            return balanceMapper.queryPressureInflowSix(dto);
//        else
//            return balanceMapper.queryPressureInflowTwelve(dto);
//    }
//
//    @Override
//    @SuppressWarnings("ALL")
//    //收款 && 融资提款
//    public void insertInFlowFromOther(IRequest request, Long bankAccountId, Double amount, Date date) {
//        HlsCusCapAccountMonthlyBalance balance = new HlsCusCapAccountMonthlyBalance();
//        balance.setBankAccountId(bankAccountId);
//        balance.setPeriod(sdf.format(date));
//        balance = self().queryByPeriodAndAccount(request, balance);
//        if (balance != null) {
//            balance.setReceivedAmount(balance.getReceivedAmount() + amount);
//            balance.setAccountBalance(balance.getAccountBalance() + amount);
//            self().updateByPrimaryKeySelective(request, balance);
//        } else {
//            HlsCusCapAccountMonthlyBalance monthlyBalance = new HlsCusCapAccountMonthlyBalance();
//            monthlyBalance.setBankAccountId(bankAccountId);
//            monthlyBalance.setPeriod(sdf.format(date));
//            monthlyBalance.setBalanceStatus("NOT");
//            monthlyBalance.setCompanyId(request.getCompanyId());
//            monthlyBalance.setReceivedAmount(amount);
//            monthlyBalance.setCreatedBy(request.getUserId());
//            monthlyBalance.setLastUpdatedBy(request.getUserId());
//            monthlyBalance.setLastUpdateLogin(request.getUserId());
//            this.queryLatestSavedBalance(monthlyBalance, request);
//            monthlyBalance.setAccountBalance(amount + monthlyBalance.getOpeningBalance());
//            self().insertSelective(request, monthlyBalance);
//        }
//    }
//
//    @Override
//    @SuppressWarnings("ALL")
//    //付款 + 融资还款
//    public void insertOutFlowFromOther(IRequest request, Long bankAccountId, Double amount, Date date) {
//        HlsCusCapAccountMonthlyBalance balance = new HlsCusCapAccountMonthlyBalance();
//        balance.setBankAccountId(bankAccountId);
//        balance.setPeriod(sdf.format(date));
//        balance = this.queryByPeriodAndAccount(request, balance);
//        if (balance != null) {
//            balance.setPaidAmount(balance.getPaidAmount() + amount);
//            balance.setAccountBalance(balance.getAccountBalance() - amount);
//            self().updateByPrimaryKey(request, balance);
//        } else {
//            HlsCusCapAccountMonthlyBalance monthlyBalance = new HlsCusCapAccountMonthlyBalance();
//            monthlyBalance.setBankAccountId(bankAccountId);
//            monthlyBalance.setPeriod(sdf.format(date));
//            monthlyBalance.setBalanceStatus("NOT");
//            monthlyBalance.setCompanyId(request.getCompanyId());
//            monthlyBalance.setPaidAmount(amount);
//            monthlyBalance.setCreatedBy(request.getUserId());
//            monthlyBalance.setLastUpdatedBy(request.getUserId());
//            monthlyBalance.setLastUpdateLogin(request.getUserId());
//            this.queryLatestSavedBalance(monthlyBalance, request);
//            monthlyBalance.setAccountBalance(monthlyBalance.getOpeningBalance() - amount);
//            self().insertSelective(request, monthlyBalance);
//        }
//    }
//
//    public static Integer getDifMonth(Date startDate, Date endDate) {
//        Calendar start = Calendar.getInstance();
//        Calendar end = Calendar.getInstance();
//        start.setTime(startDate);
//        end.setTime(endDate);
//        int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
//        int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
//        return (month + result);
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> queryAmountByFrequency(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        if (dto.getFrequency().equalsIgnoreCase(DAY)) {
//            Long count = (dto.getEndDate().getTime() - dto.getStartDate().getTime()) / (60 * 60 * 24 * 1000);
//            if (count < 0) {
//                return null;
//            }
//            int[] dayCount = new int[Math.toIntExact(count)];
//            for (int i = 0; i < dayCount.length; i++) {
//                dayCount[i] = i + 2;
//            }
//            dto.setCount(dayCount);
//            return this.balanceMapper.queryAmountByDay(dto);
//        } else if (dto.getFrequency().equalsIgnoreCase(MONTH)) {
//            int count = getDifMonth(dto.getStartDate(), dto.getEndDate());
//            if (count < 0) {
//                return null;
//            }
//            int[] monthCount = new int[count];
//            for (int i = 0; i < monthCount.length; i++) {
//                monthCount[i] = i + 2;
//            }
//            dto.setCount(monthCount);
//            dto.setFrequencyN(1);
//            return this.balanceMapper.queryAmountByMonth(dto);
//        } else {
//            int n = 0;
//            if (dto.getFrequency().equalsIgnoreCase(QUARTER)) {
//                n = 3;
//            } else if (dto.getFrequency().equalsIgnoreCase(HALFYEAR)) {
//                n = 6;
//            } else if (dto.getFrequency().equalsIgnoreCase(YEAR)) {
//                n = 12;
//            }
//            dto.setFrequencyN(n);
//            if (n != 0) {
//                int count = (int) Math.floor(Double.valueOf(getDifMonth(dto.getStartDate(), dto.getEndDate())) / Double.valueOf(n));
//                if (count < 0) {
//                    return null;
//                }
//                int[] frequencyCount = new int[count];
//                for (int i = 0; i < frequencyCount.length; i++) {
//                    frequencyCount[i] = (i + 2) * n;
//                }
//                dto.setCount(frequencyCount);
//                return this.balanceMapper.queryAmountByMonth(dto);
//            } else {
//                return null;
//            }
//        }
//    }
//
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> pressureQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto, String type, String[] businessType) {
//        List<HlsCusCapAccountMonthlyBalance> list = new ArrayList<>();
//        HlsCusCapAccountMonthlyBalance nullBalance = new HlsCusCapAccountMonthlyBalance();
//        nullBalance.setAllMonth(ZERO);
//        for (int j = 0; j < businessType.length; j++) {
//            HlsCusCapAccountMonthlyBalance balance = new HlsCusCapAccountMonthlyBalance();
//            if ("ALL".equalsIgnoreCase(businessType[j]))
//                dto.setBusinessType(null);
//            else
//                dto.setBusinessType(businessType[j]);
//            if ("CT".equalsIgnoreCase(type))
//                balance = balanceMapper.queryCtPrinciple(dto);
//            else if ("CX".equalsIgnoreCase(type))
//                balance = balanceMapper.queryCxPrinciple(dto);
//            else if ("TAX".equalsIgnoreCase(type))
//                balance = balanceMapper.queryTaxPrinciple(dto);
//            else if ("LON".equalsIgnoreCase(type))
//                balance = balanceMapper.queryLonPrinciple(dto);
//            if (balance == null)
//                balance = nullBalance;
//            list.add(balance);
//        }
//        return list;
//    }
//
//    private Boolean checkQueryDate(HlsCusCapAccountMonthlyBalance dto){
//        if(dto.getStartDate()==null ||dto.getEndDate()==null || dto.getFrequency()==null){
//            return false;
//        }
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
//        dto.setStartPeriod(simpleDateFormat.format(dto.getStartDate()));
//        dto.setEndPeriod(simpleDateFormat.format(dto.getEndDate()));
//        return true;
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> selectfeatureAmountData(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {
//
//        if(!checkQueryDate(dto)){
//            return new ArrayList<>();
//        }
//
//        PageHelper.startPage(page, pageSize);
//
//        return balanceMapper.selectfeatureAmountData(dto);
//    }
//
//
//    @Override
//    public void exportfeatureAmountData(HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//
//        if(!checkQueryDate(dto)){
//            return ;
//        }
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList(" 频率 ","直接租赁","售后回租","经营性租赁","正向保理","反向保理");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "dateCount","amountForLease","amountForLeaseback","amountForOperatinglease","amountForFactoring","amountForReverseFactoring");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<HlsCusCapAccountMonthlyBalance> balances =balanceMapper.selectfeatureAmountData(dto);
//        for (HlsCusCapAccountMonthlyBalance data:balances){
//            XSSFRow row = sheet.createRow(dataRowNum++);
//            try {
//                ExportExcelUtil.setData(xwork,sheet, row, data,colGetMethods);
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "租赁保理未来收支分析");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    @Override
//    public List<Map<String, Object>> selectFinanceFeatureData(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {
//        if(!checkQueryDate(dto)){
//            return new ArrayList<>();
//        }
//        PageHelper.startPage(page, pageSize);
//
//        return balanceMapper.selectFinanceFeatureData(dto);
//    }
//
//    @Override
//    public void exportfeatureFinanceData(HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        if(!checkQueryDate(dto)){
//            return ;
//        }
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList(" 频率 ","银行融资本金","银行融资利息","银行融资服务费","非银行金融机构融资本金","非银行金融机构融资费用"
//        ,"非银行金融机构服务费","股东借款本金","股东借款利息","股东借款服务费","ABS/ABN租金回收","ABS/ABN费用","其他发债本金","其他发债利息","其他发债费用");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "dateCount","amountForBankPri","amountForBankInt","amountForBankFee","amountForOtherPri","amountForOtherInt",
//                "amountForOtherfee","amountForHolderPri","amountForHolderInt","amountForHolderfee","amountForCollection","amountForAbsFee","amountForAbsPri","amountForAbsInt","amountForProFee");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<Map<String, Object>> financeFeatureData = balanceMapper.selectFinanceFeatureData(dto);
//        setData(xwork,sheet,dataRowNum,financeFeatureData,colGetMethods);
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "融资端未来应付分析");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> selectFeatureBpFatorData(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {
//
//        if(!checkQueryDate(dto)){
//            return new ArrayList<>();
//        }
//        PageHelper.startPage(page, pageSize);
//
//        return balanceMapper.selectFeatureBpFatorData(dto);
//    }
//
//
//    @Override
//    public void exportfeatureFactorData(HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        if(!checkQueryDate(dto)){
//            return ;
//        }
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList(" 频率 ","直接租赁","售后回租","经营性租赁","正向保理","反向保理");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "dateCount","amountForLease","amountForLeaseback","amountForOperatinglease","amountForFactoring","amountForReverseFactoring");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<HlsCusCapAccountMonthlyBalance> balances =balanceMapper.selectFeatureBpFatorData(dto);
//        for (HlsCusCapAccountMonthlyBalance data:balances){
//            XSSFRow row = sheet.createRow(dataRowNum++);
//            try {
//                ExportExcelUtil.setData(xwork,sheet, row, data,colGetMethods);
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            String fileName;
//            if ("Y".equals(dto.getFactorFlag())) {
//                fileName = "租赁保理未来收支分析-客户系数不为0";
//            } else {
//                fileName = "租赁保理未来收支分析-客户系数为0";
//            }
//            ExportExcelUtil.IOWrite(xwork, null,request, response, fileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> selectFinanceIncomeAndPayData(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        if(dto.getPeriod()==null){
//            return new ArrayList<>();
//        }
//         dto.setPeriod("'"+dto.getPeriod()+"'");
//        return balanceMapper.selectFinanceIncomeAndPayData(dto);
//    }
//
//    @Override
//    public List<HlsCusCapAccountMonthlyBalance> selectFinancePressureData(IRequest request, HlsCusCapAccountMonthlyBalance dto) {
//        if(dto.getPeriod()==null){
//            return new ArrayList<>();
//        }
//        dto.setPeriod("'"+dto.getPeriod()+"'");
//        return balanceMapper.selectFinancePressureData(dto);
//    }
//
//
//    @Override
//    public void exportFinancePressureData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        if(!checkQueryDate(dto)){
//            return ;
//        }
//
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//
//        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
//        DecimalFormat df =new DecimalFormat("###,##0.00");
//        List<HlsCusBpMasterFactor> hlsCusBpMasterFactors = bpMasterFactorMapper.selectBpMasterFactorData(new HlsCusBpMasterFactor());
//        Date date= hlsCusBpMasterFactors.stream().map(HlsCusBpMasterFactor::getBpDate).max((o1, o2) -> {
//            if (o1.before(o2)){
//                return -1;
//            }
//            else if (o1.equals(o2))
//            {
//                return 0;
//            }
//            return 1;
//        }).get();
//        int rowTotal=0;
//        int colTotal=0;
//        CellStyle cellBorderStyle = ExportExcelUtil.creatBaseCellStyle(xwork);
//        //参数数据
//        XSSFRow param1Row = sheet.createRow(rowTotal++);
//        param1Row.createCell(colTotal).setCellValue("制单日期");
//        param1Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param1Row.createCell(colTotal+1).setCellValue(dateFormat.format(new Date()));
//        param1Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param2Row = sheet.createRow(rowTotal++);
//        param2Row.createCell(colTotal).setCellValue("测试日期从");
//        param2Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param2Row.createCell(colTotal+1).setCellValue(dto.getStartPeriod());
//        param2Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param3Row = sheet.createRow(rowTotal++);
//        param3Row.createCell(colTotal).setCellValue("测试日期至");
//        param3Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param3Row.createCell(colTotal+1).setCellValue(dto.getEndPeriod());
//        param3Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param4Row = sheet.createRow(rowTotal++);
//        param4Row.createCell(colTotal).setCellValue("测试公司");
//        param4Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param4Row.createCell(colTotal+1).setCellValue(dto.getCompanyName());
//        param4Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param5Row = sheet.createRow(rowTotal++);
//        param5Row.createCell(colTotal).setCellValue("制单日期账户余额");
//        param5Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param5Row.createCell(colTotal+1).setCellValue(df.format(dto.getAccountBalance()));
//        param5Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param6Row = sheet.createRow(rowTotal++);
//        param6Row.createCell(colTotal).setCellValue("客户系数版本");
//        param6Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param6Row.createCell(colTotal+1).setCellValue(dateFormat.format(date));
//        param6Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param7Row = sheet.createRow(rowTotal++);
//        param7Row.createCell(colTotal).setCellValue("单位");
//        param7Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param7Row.createCell(colTotal+1).setCellValue("元");
//        param7Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//
//
//
//         //空两行
//        rowTotal++;
//        rowTotal++;
//
//
//        //压力系数
//        List<CodeValue> codeValues = codeService.selectCodeValuesByCodeName(iRequest, "CAP.PRESSURE_COEFFICIENT");
//        codeValues.sort(Comparator.comparing((a) -> Double.parseDouble(a.getValue())));
//
//
//        List<String> frequencys = balanceMapper.selectDateFrequency(dto);
//
//        CellStyle headStyle = ExportExcelUtil.getTitleStyle(xwork, 14);
//        CellStyle nextHeadStyle = ExportExcelUtil.getTitleStyle(xwork, 12);
//
//
//        String cellValue0="客户名称/融资渠道";
//        XSSFRow titleRow = sheet.createRow(rowTotal);
//        XSSFCell bpNameCell = titleRow.createCell(colTotal);
//        bpNameCell.setCellValue(cellValue0);
//        sheet.addMergedRegion(new CellRangeAddress(rowTotal, rowTotal+1, colTotal, colTotal));
//        sheet.setColumnWidth(colTotal, 60*256);
//        bpNameCell.setCellStyle(headStyle);
//        colTotal++;
//
//        XSSFCell bpFactorCell = titleRow.createCell(colTotal);
//        bpFactorCell.setCellValue("客户系数");
//        sheet.addMergedRegion(new CellRangeAddress(rowTotal, rowTotal+1, colTotal, colTotal));
//        sheet.setColumnWidth(colTotal, 25*256);
//        bpFactorCell.setCellStyle(headStyle);
//        colTotal++;
//
//        XSSFRow frequencyRow = sheet.createRow(rowTotal+1);
//
//
//        //租金
//        titleRow.createCell(colTotal).setCellValue("租金");
//        titleRow.getCell(colTotal).setCellStyle(headStyle);
//        sheet.addMergedRegion(new CellRangeAddress(rowTotal, rowTotal, colTotal, colTotal+frequencys.size()-1));
//        for(int i=0;i<frequencys.size();i++){
//            frequencyRow.createCell(colTotal).setCellValue(frequencys.get(i));
//            // 列宽自动
//            sheet.setColumnWidth(colTotal, (int) ((frequencys.get(i).getBytes().length * 1.5) * 256));
//            frequencyRow.getCell(colTotal).setCellStyle(nextHeadStyle);
//            colTotal++;
//        }
//
//        //各系数
//        for(CodeValue value:codeValues){
//            String precentValue=new BigDecimal(value.getValue()).multiply(new BigDecimal(100)).toPlainString();
//            titleRow.createCell(colTotal).setCellValue(value.getDescription()+precentValue+"%");
//            sheet.addMergedRegion(new CellRangeAddress(rowTotal, rowTotal, colTotal, colTotal+frequencys.size()-1));
//            titleRow.getCell(colTotal).setCellStyle(headStyle);
//            for(int i=0;i<frequencys.size();i++){
//                frequencyRow.createCell(colTotal+i).setCellValue(frequencys.get(i));
//                // 列宽自动
//                sheet.setColumnWidth(colTotal+i, (int) ((frequencys.get(i).getBytes().length * 1.5) * 256));
//                frequencyRow.getCell(colTotal+i).setCellStyle(nextHeadStyle);
//            }
//            colTotal=colTotal+frequencys.size();
//        }
//        rowTotal++;
//        rowTotal++;
//
//        //收入数据
//        XSSFCellStyle cellStyle = xwork.createCellStyle();
//        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
//        List<HlsCusCapAccountMonthlyBalance> incomeData = balanceMapper.selectPressureIncomeData(dto);
//
//        //收入合计
//        Map<Integer,BigDecimal> incomeSumMap=new HashMap<>();
//        //可能存在期间无收入的情况 导致无合计数据 所以先放0
//        for(int j = 0; j < frequencys.size()*(codeValues.size()+1); j++){
//            incomeSumMap.put(j+2,BigDecimal.ZERO);
//        }
//
//
//        for(int i = 0; i < incomeData.size(); i++) {
//            colTotal=0;
//            XSSFRow row = sheet.createRow(rowTotal);
//            Map<String,BigDecimal>  incomeFrequencyMap = frequencyToMap(incomeData.get(i).getCapFrequencies());
//            row.createCell(colTotal).setCellValue(incomeData.get(i).getBpName());
//            colTotal++;
//
//            String bpFactorStr=incomeData.get(i).getBpFactor().multiply(new BigDecimal(100)).toPlainString()+"%";
//            row.createCell(colTotal).setCellValue(bpFactorStr);
//            colTotal++;
//            //租金
//            for(int j = 0; j < frequencys.size(); j++){
//                BigDecimal amount;
//               if(incomeFrequencyMap.get(frequencys.get(j))!=null){
//                  amount=incomeFrequencyMap.get(frequencys.get(j));
//                   row.createCell(colTotal).setCellValue(df.format(incomeFrequencyMap.get(frequencys.get(j))));
//               }else{
//                   amount=BigDecimal.ZERO;
//                   row.createCell(colTotal).setCellValue("0.00");
//               }
//                row.getCell(colTotal).setCellStyle(cellStyle);
//                incomeSumMap.put(colTotal,incomeSumMap.get(colTotal).add(amount));
//                colTotal++;
//            }
//
//               //各系数 收入中排除其他应收项
//                for (CodeValue value : codeValues) {
//                    for (int j = 0; j < frequencys.size(); j++) {
//                        BigDecimal amount;
//                        if(!"其他应收".equals(incomeData.get(i).getBpName())) {
//                            if (incomeFrequencyMap.get(frequencys.get(j)) != null) {
//                                amount = incomeFrequencyMap.get(frequencys.get(j)).multiply(new BigDecimal(value.getValue())).multiply(incomeData.get(i).getBpFactor());
//                                row.createCell(colTotal).setCellValue(df.format(amount));
//                            } else {
//                                amount = BigDecimal.ZERO;
//                                row.createCell(colTotal).setCellValue("0.00");
//                            }
//                            row.getCell(colTotal).setCellStyle(cellStyle);
//                        }else{
//                            if (incomeFrequencyMap.get(frequencys.get(j)) != null) {
//                                amount =incomeFrequencyMap.get(frequencys.get(j));
//                            }else{
//                                amount = BigDecimal.ZERO;
//                            }
//                        }
//                        incomeSumMap.put(colTotal, incomeSumMap.get(colTotal).add(amount));
//                        colTotal++;
//                    }
//                }
//
//            rowTotal++;
//        }
//
//        colTotal=0;
//        XSSFRow incomeSumRow = sheet.createRow(rowTotal);
//        incomeSumRow.createCell(colTotal).setCellValue("收入合计");
//        for(Integer key:incomeSumMap.keySet()){
//            incomeSumRow.createCell(key).setCellValue(df.format(incomeSumMap.get(key)));
//            incomeSumRow.getCell(key).setCellStyle(cellStyle);
//        }
//        rowTotal++;
//        //空出一行
//        rowTotal++;
//
//        //支出数据
//        List<HlsCusCapAccountMonthlyBalance> payData = balanceMapper.selectPressurePayData(dto);
//
//        //支出合计
//        Map<Integer,BigDecimal> paySumMap=new HashMap<>();
//        //可能存在期间无支出的情况 导致无合计数据 所以先放0
//        for(int j = 0; j < frequencys.size()*(codeValues.size()+1); j++){
//            paySumMap.put(j+2,BigDecimal.ZERO);
//        }
//
//        for(int i = 0; i < payData.size(); i++) {
//            colTotal=0;
//            XSSFRow row = sheet.createRow(rowTotal);
//            Map<String,BigDecimal>  payFrequencyMap = frequencyToMap(payData.get(i).getCapFrequencies());
//            row.createCell(colTotal).setCellValue(payData.get(i).getBpName());
//            colTotal++;
//            //系数行跳过
//            colTotal++;
//
//            //租金
//            for(int j = 0; j < frequencys.size(); j++){
//                BigDecimal amount;
//                if(payFrequencyMap.get(frequencys.get(j))!=null){
//                    amount=payFrequencyMap.get(frequencys.get(j));
//                    row.createCell(colTotal).setCellValue(df.format(payFrequencyMap.get(frequencys.get(j))));
//                }else{
//                    amount=BigDecimal.ZERO;
//                    row.createCell(colTotal).setCellValue("0.00");
//                }
//                row.getCell(colTotal).setCellStyle(cellStyle);
//                paySumMap.put(colTotal,paySumMap.get(colTotal).add(amount));
//                colTotal++;
//            }
//
//            for (CodeValue value : codeValues) {
//                for (int j = 0; j < frequencys.size(); j++) {
//                    BigDecimal amount=payFrequencyMap.get(frequencys.get(j))==null?BigDecimal.ZERO:payFrequencyMap.get(frequencys.get(j));
//                    paySumMap.put(colTotal,paySumMap.get(colTotal).add(amount));
//                    colTotal++;
//                }
//            }
//
//            rowTotal++;
//        }
//
//        colTotal=0;
//        XSSFRow paySumRow = sheet.createRow(rowTotal);
//        paySumRow.createCell(colTotal).setCellValue("支出合计");
//        for(int j = 0; j < frequencys.size(); j++){
//            paySumRow.createCell(j+2).setCellValue(df.format(paySumMap.get(j+2)));
//            paySumRow.getCell(j+2).setCellStyle(cellStyle);
//        }
//        rowTotal++;
//        //空出一行
//        rowTotal++;
//
//        XSSFRow gap1SumRow = sheet.createRow(rowTotal++);
//        gap1SumRow.createCell(colTotal).setCellValue("单月资金缺口");
//        //空出一行
//        rowTotal++;
//        XSSFRow totalSumRow = sheet.createRow(rowTotal++);
//        totalSumRow.createCell(colTotal).setCellValue("累计资金缺口");
//        for(Integer key:paySumMap.keySet()){
//            BigDecimal gapAmount;
//            if(key==2) {
//                gapAmount=incomeSumMap.get(key).subtract(paySumMap.get(key)).add(new BigDecimal(dto.getAccountBalance().toString()));
//            }else{
//                gapAmount=incomeSumMap.get(key).subtract(paySumMap.get(key)).add(incomeSumMap.get(key-1));
//            }
//            gap1SumRow.createCell(key).setCellValue(df.format(incomeSumMap.get(key).subtract(paySumMap.get(key))));
//            gap1SumRow.getCell(key).setCellStyle(cellStyle);
//            totalSumRow.createCell(key).setCellValue(df.format(gapAmount));
//            totalSumRow.getCell(key).setCellStyle(cellStyle);
//            incomeSumMap.put(key,gapAmount);
//        }
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "压力测试报表");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private Map<String,BigDecimal>  frequencyToMap(List<HlsCusCapFrequency> capFrequencies){
//        Map<String,BigDecimal> map=new HashMap<>();
//        for(HlsCusCapFrequency frequency:capFrequencies){
//            map.put(frequency.getDateDesc(),frequency.getDueAmount());
//        }
//        return map;
//    }


    @Override
    public List<Map<String, Object>> selectBankAccountPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);

        return balanceMapper.selectBankAccountPredictedAmount(dto);
    }

    @Override
    public List<Map<String, Object>> selectNotConfirmAccountPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto) {

        return balanceMapper.selectNotConfirmAccountPredictedAmount(dto);
    }
//
//
//    @Override
//    public void exportBankPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList(" 银行名称 ","公司","币种","期初账户余额","期中项目本息回款","期中融资提款",
//                "期中项目其他费用收支","期中内部资金账户划转","期中项目放款","期中融资还款","期中融资其他费用支出","期末预计账户余额");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "bankName","companyName","currencyName","accountBalance","conPriIntAmount","lonWithdrawAmount"
//        ,"conOtherAmount","fundTransferAmount","conRepaymentAmount","lonRepaymentAmount","lonFeeAmount","predictedAmount");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<Map<String, Object>> accountPredictedAmount = balanceMapper.selectBankAccountPredictedAmount(dto);
//        setData(xwork,sheet,dataRowNum,accountPredictedAmount,colGetMethods);
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "资金账户收支预测");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private  void setData(XSSFWorkbook workbook , XSSFSheet sheet, int dataRowNum,  List<Map<String, Object>> datas ,List<String> colGetMethods){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df =new DecimalFormat("###,##0.00");
        for (Map<String, Object> data:datas){
            XSSFRow row = sheet.createRow(dataRowNum++);
            for (int i = 0; i < colGetMethods.size(); i++) {
                XSSFCell cell = row.createCell(i);
                Object value = data.get(colGetMethods.get(i));
                if(value instanceof Date){
                    value = simpleDateFormat.format(value);
                }
                if(value instanceof BigDecimal){
                    XSSFCellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                    value=df.format(value);
                    cell.setCellStyle(cellStyle);
                }
                if (Objects.nonNull(value)) {
                    ExportExcelUtil.initColWidth(sheet, i, value.toString());
                    cell.setCellValue(value.toString());
                }
            }
        }
    }

    @Override
    public List<Map<String, Object>> selectAccountPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);

        return balanceMapper.selectAccountPredictedAmount(dto);
    }


//    @Override
//    public void exportAccountPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList(" 开户行 ","账户名称","账户号","期初账户余额","期中项目本息回款","期中融资提款",
//                "期中项目其他费用收支","期中内部资金账户划转","期中项目放款","期中融资还款","期中融资其他费用支出","期末预计账户余额");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "bankBranchName","bankAccountName","bankAccountNum","accountBalance","conPriIntAmount","lonWithdrawAmount"
//                ,"conOtherAmount","fundTransferAmount","conRepaymentAmount","lonRepaymentAmount","lonFeeAmount","predictedAmount");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<Map<String, Object>> accountPredictedAmount = balanceMapper.selectAccountPredictedAmount(dto);
//        setData(xwork,sheet,dataRowNum,accountPredictedAmount,colGetMethods);
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "资金账户收支预测");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public List<Map<String, Object>> selectFctConContractPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);

        return balanceMapper.selectFctConContractPredictedAmount(dto);
    }
//
//    @Override
//    public void exportFctConPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList(" 合同查询编号 ","业务合同编号","合同名称"," 日期 ","现金流类型","现金流方向", " 金额 ");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "contractNumber","approvalNumber","contractName","dueDateStr","cfItemDesc","cfDirectionDesc","dueAmount");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<Map<String, Object>> accountPredictedAmount = balanceMapper.selectFctConContractPredictedAmount(dto);
//        setData(xwork,sheet,dataRowNum,accountPredictedAmount,colGetMethods);
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "租赁保理收支预测明细");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public List<Map<String, Object>> selectLonFinancePredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);
        if(HlsCusConstant.FLAG.Y.equals(dto.getAccountFlag())){
            return balanceMapper.selectLonFinancePredictedAmount(dto);
        }else{
            return balanceMapper.selectNotConfirmLonPredictedAmount(dto);
        }
    }

//    @Override
//    public void exportLonPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList("提款编号/产品编号","主合同编号","合同名称/产品简称"," 日期 ","现金流类型","现金流方向", " 金额 ");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "withdrawNumber","majorContractNumber","contractName","dueDateStr","cfItemDesc","cfDirectionDesc","dueAmount");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<Map<String, Object>> accountPredictedAmount=null;
//        if(HlsCusConstant.FLAG.Y.equals(dto.getAccountFlag())) {
//            accountPredictedAmount = balanceMapper.selectLonFinancePredictedAmount(dto);
//        }else{
//            accountPredictedAmount = balanceMapper.selectNotConfirmLonPredictedAmount(dto);
//        }
//        setData(xwork,sheet,dataRowNum,accountPredictedAmount,colGetMethods);
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "融资收支预测明细");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public List<Map<String, Object>> selectTransferPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) {
//
//        PageHelper.startPage(page, pageSize);
//
//        return balanceMapper.selectTransferPredictedAmount(dto);
//    }
//
//    @Override
//    public void exportTransferPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList("申请编号","申请日期","申请人","申请部门","公司","申请支付日期", "现金流方向","申请支付金额","申请支付用途");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "applyNumber","applyDate","userName","dueDateStr","unitName","companyName","dueDate","cfDirectionDesc","dueAmount","applyPurposeDesc");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<Map<String, Object>> accountPredictedAmount = balanceMapper.selectTransferPredictedAmount(dto);
//        setData(xwork,sheet,dataRowNum,accountPredictedAmount,colGetMethods);
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "内部资金账户划转");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public List<Map<String, Object>> selectAccountBalanceChart(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto) {
//        HlsCusCapAccountMonthlyBalance balanceChart = balanceMapper.selectAccountBalanceChart(dto);
//        BigDecimal amountSum=new BigDecimal(balanceChart.getRestrictedAmount().toString()).add(
//                new BigDecimal(balanceChart.getAvailableAmount().toString())).add(
//                new BigDecimal(balanceChart.getInvLimitedAmount().toString())).add(
//                new BigDecimal(balanceChart.getInvUnlimitedAmount().toString()));
//        List<Map<String, Object>> chartData=new ArrayList<>();
//        Map<String, Object> restrictedMap=new HashMap<>();
//        restrictedMap.put("balanceType","RESTRICT");
//        restrictedMap.put("balanceAmount",balanceChart.getRestrictedAmount());
//        restrictedMap.put("balancePrecent",new BigDecimal(balanceChart.getRestrictedAmount().toString()).multiply(new BigDecimal(100))
//                                          .divide(amountSum,2,BigDecimal.ROUND_HALF_UP).toPlainString()+"%");
//        chartData.add(restrictedMap);
//
//        Map<String, Object> availableMap=new HashMap<>();
//        availableMap.put("balanceType","AVAILABLE");
//        availableMap.put("balanceAmount",balanceChart.getAvailableAmount());
//        availableMap.put("balancePrecent",new BigDecimal(balanceChart.getAvailableAmount().toString()).multiply(new BigDecimal(100))
//                .divide(amountSum,2,BigDecimal.ROUND_HALF_UP).toPlainString()+"%");
//        chartData.add(availableMap);
//
//        Map<String, Object> invLimitedMap=new HashMap<>();
//        invLimitedMap.put("balanceType","LIMITED");
//        invLimitedMap.put("balanceAmount",balanceChart.getInvLimitedAmount());
//        invLimitedMap.put("balancePrecent",new BigDecimal(balanceChart.getInvLimitedAmount().toString()).multiply(new BigDecimal(100))
//                .divide(amountSum,2,BigDecimal.ROUND_HALF_UP).toPlainString()+"%");
//        chartData.add(invLimitedMap);
//
//        Map<String, Object> invUnlimitMap=new HashMap<>();
//        invUnlimitMap.put("balanceType","UNLIMITED");
//        invUnlimitMap.put("balanceAmount",balanceChart.getInvUnlimitedAmount());
//        invUnlimitMap.put("balancePrecent",new BigDecimal(balanceChart.getInvUnlimitedAmount().toString()).multiply(new BigDecimal(100))
//                .divide(amountSum,2,BigDecimal.ROUND_HALF_UP).toPlainString()+"%");
//        chartData.add(invUnlimitMap);
//
//        return chartData;
//    }
//
//    @Override
//    public void exportAccountBalanceData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto) {
//
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//
//        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
//        DecimalFormat df =new DecimalFormat("###,##0.00");
//
//        int rowTotal=0;
//        int colTotal=0;
//        CellStyle cellBorderStyle = ExportExcelUtil.creatBaseCellStyle(xwork);
//        FndCompany fndCompany = fndCompanyMapper.selectByPrimaryKey(dto.getCompanyId());
//        //参数数据
//        XSSFRow param1Row = sheet.createRow(rowTotal++);
//        param1Row.createCell(colTotal).setCellValue("公司名称");
//        param1Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param1Row.createCell(colTotal+1).setCellValue(fndCompany.getCompany_full_name());
//        param1Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param2Row = sheet.createRow(rowTotal++);
//        param2Row.createCell(colTotal).setCellValue("结算日期");
//        param2Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param2Row.createCell(colTotal+1).setCellValue(dateFormat.format(dto.getBalanceDate()));
//        param2Row.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//        XSSFRow param3Row = sheet.createRow(rowTotal++);
//        param3Row.createCell(colTotal).setCellValue("资金总额");
//        param3Row.getCell(colTotal).setCellStyle(cellBorderStyle);
//        param3Row.createCell(colTotal+1).setCellStyle(cellBorderStyle);
//        //空两行
//        rowTotal++;
//        rowTotal++;
//
//        //表头
//        final  List<String> colNameList = Lists.newArrayList("银行名称","支行名称","账户名称","账户号","账户性质","币种",
//                "账户余额","可用资金余额","未受限投资产品余额","受限投资产品余额","账户受限余额");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "bankName","bankBranchName","bankAccountName","bankAccountNum","bankAccountTypeDesc","currencyName",
//                "accountBalance","availableAmount","invUnlimitedAmount","invLimitedAmount","restrictedAmount");
//
//        XSSFRow titleRow = sheet.createRow(rowTotal);
//        XSSFCell cell = titleRow.createCell(0);
//        cell.setCellValue("资金分布汇总表");
//        titleRow.setHeight((short) (35 * 20));
//        sheet.addMergedRegion(new CellRangeAddress(rowTotal, rowTotal, 0, colNameList.size() - 1));
//        CellStyle titleStyle = ExportExcelUtil.getTitleStyle(xwork, 14);
//        cell.setCellStyle(titleStyle);
//        rowTotal++;
//        // 显示列名的行
//        XSSFRow headRow = sheet.createRow(rowTotal++);
//        headRow.setHeight((short) (25 * 20));
//        CellStyle headStyle = ExportExcelUtil.getTitleStyle(xwork, 12);
//        for (int i = 0; i < colNameList.size(); i++) {
//            String colName = colNameList.get(i);
//            XSSFCell fcell = headRow.createCell(i);
//            fcell.setCellStyle(headStyle);
//            fcell.setCellValue(colName);
//            // 列宽自动
//            sheet.setColumnWidth(i, (int) ((colName.getBytes().length * 1.5) * 256));
//        }
//
//        List<HlsCusCapAccountMonthlyBalance> balances =balanceMapper.accountBalanceInfoQuery(dto);
//        BigDecimal accountSum=BigDecimal.ZERO;
//        for (HlsCusCapAccountMonthlyBalance data:balances){
//            XSSFRow row = sheet.createRow(rowTotal++);
//            accountSum=accountSum.add(new BigDecimal(data.getAccountBalance().toString()));
//            try {
//                ExportExcelUtil.setData(xwork,sheet, row, data,colGetMethods);
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        //资金总额
//        param3Row.getCell(1).setCellValue(df.format(accountSum));
//
//        //空一行
//        rowTotal++;
//
//        XSSFRow userRow = sheet.createRow(rowTotal++);
//        userRow.createCell(colTotal).setCellValue("导出人");
//        userRow.getCell(colTotal).setCellStyle(cellBorderStyle);
//        userRow.createCell(colTotal+1).setCellValue(iRequest.getAttribute("description").toString());
//        userRow.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//
//        XSSFRow dateRow = sheet.createRow(rowTotal++);
//        dateRow.createCell(colTotal).setCellValue("导出时间");
//        dateRow.getCell(colTotal).setCellStyle(cellBorderStyle);
//        dateRow.createCell(colTotal+1).setCellValue(dateFormat.format(new Date()));
//        dateRow.getCell(colTotal+1).setCellStyle(cellBorderStyle);
//
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "资金分布汇总表");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void exportOtherPlan(HttpServletRequest request, HttpServletResponse response, HlsCusCapitalOtherPlan dto) {
//        XSSFWorkbook xwork = new XSSFWorkbook();
//        XSSFSheet sheet = xwork.createSheet("sheet1");
//        final  List<String> colNameList = Lists.newArrayList("计划日期","计划支付金额","计划收款金额","备注");
//        final List<String> colGetMethods = Lists.newArrayList(
//                "planDate","planPayAmount","planReceiptAmount","description");
//        int dataRowNum =ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
//        List<HlsCusCapitalOtherPlan> list = capitalOtherPlanMapper.selectCapitalOtherPlanData(dto);
//        List<Map<String,String>> datas= new ArrayList<>();
//        for (HlsCusCapitalOtherPlan hlsCusCapitalOtherPlan : list) {
//            XSSFRow row = sheet.createRow(dataRowNum++);
//            ExportExcelUtil.setData(xwork,sheet,row,BeanRefUtil.getFieldValueMap(hlsCusCapitalOtherPlan),colGetMethods);
//        }
//        try {
//            ExportExcelUtil.IOWrite(xwork, null,request, response, "未来其他收支明细");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
