package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.service.HlsCalcConfigService;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndSysCodes;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndSysCodesService;
import com.hand.hls.fnd.service.HlsCusImpDataService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.service.HlsCusPrjQuotationSubsectionService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.*;
import com.hand.hls.vat.dto.HlsInvoiceProfileDtl;
import com.hand.hls.vat.service.HlsInvoiceProfileDtlService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationCashflowServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationCashflow> implements HlsCusPrjQuotationCashflowService {
    private static final Long DEPOSIT_CF_ITEM = 51L;
    private static final Long DEPOSIT_RETURN_CF_ITEM = 52L;
    private static final String DEPOSIT_RETURN_CF_DIRECTION = "OUTFLOW";

    private static final Long RISK_CF_ITEM = 53L;
    private static final Long RISK_RETURN_CF_ITEM = 54L;

    /*
    保证金退还方式
    PERIOD_FINAL_RETURN：不抵扣
    PERIOD_FINAL_DEDUCTIBLE：抵扣
     */
    private static final String PERIOD_FINAL_RETURN = "PERIOD_FINAL_RETURN";
    private static final String PERIOD_FINAL_DEDUCTIBLE = "PERIOD_FINAL_DEDUCTIBLE";
    public static final double tol = 0.000000001;

    private static final String BEFORE_RENT_STAGE_TYPE = "租前期";

    private static final Long BEFORE_RENT_CF = 10L;

    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsInvoiceProfileDtlService hlsInvoiceProfileDtlService;
    @Autowired
    private FndSysCodesService fndSysCodesService;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusPrjQuotationSubsectionService hlsCusPrjQuotationSubsectionService;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private HlsCusImpDataService impDataService;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCalcConfigService hlsCalcConfigService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private IConContractCashflowService conContractCashflowService;

    //从报价器复制到项目现金流表
    @Override
    public List<HlsCusPrjQuotationCashflow> saveCalc2PrjQuotationCashflow(IRequest requestContext, HlsCusPrjQuotation prjQuotation) throws Exception {
        String modles = "prj";
        if (prjQuotation.getSourceDocumentCategory() != null && prjQuotation.getSourceDocumentCategory().equals("CON_CONTRACT")) {
            modles = "cont";
        }
//        if (prjQuotation.getSourceDocumentCategory() != null && prjQuotation.getSourceDocumentCategory().equals("CON_FLOATING_RATE_REQ")) {
//            modles = "cont";
//        }

        //读取行配置的现金流
        List<Object> ObjectList = hlsCusCalcExcelImportUtilService.getExcelToCalcLnTable(requestContext, prjQuotation.getSheets(), prjQuotation.getPriceList(), modles, Integer.parseInt(prjQuotation.getLeaseTimes().toString()), prjQuotation.getSourceDocumentCategory());


        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = new ArrayList<>();

        for (int i = 0; i < ObjectList.size(); i++) {
            String s = JSON.toJSONString(ObjectList.get(i));
            HlsCusPrjQuotationCashflow prjQuotationCashflow = JSON.parseObject(s, HlsCusPrjQuotationCashflow.class);
            prjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());
            prjQuotationCashflow.setDueDate(nvl(prjQuotationCashflow.getDueDate(),prjQuotationCashflow.getCalcDate()));
            prjQuotationCashflow.setCalcDate(prjQuotationCashflow.getCalcDate());
            prjQuotationCashflow.setFinIncomeDate(prjQuotationCashflow.getCalcDate());

            //设置租前期现金的cftype，cfitem
            if(BEFORE_RENT_STAGE_TYPE.equals(prjQuotationCashflow.getStageType())){
                prjQuotationCashflow.setCfType(BEFORE_RENT_CF);
                prjQuotationCashflow.setCfItem(BEFORE_RENT_CF);
            }

            //新增
            prjQuotationCashflow.set__status(DTOStatus.ADD);

            //头配置现金流特殊处理（设备款）
            if(prjQuotationCashflow.getCfStatus() != null && "HEAD_CASH_FLOW".equals(prjQuotationCashflow.getCfStatus())){
                prjQuotationCashflow.setDueDate(prjQuotation.getFirstReleaseDate());
                prjQuotationCashflow.setTimes(0L);
                prjQuotationCashflow.setFinIncomeDate(prjQuotation.getFirstReleaseDate());
                prjQuotationCashflow.setOutstandingPrincipal(prjQuotation.getLeaseItemAmount());
                // 2023-07-27 首付款outstandingPrincipal从financeAmount取
                if(prjQuotationCashflow.getCfItem()==2L){
                    prjQuotationCashflow.setOutstandingPrincipal(prjQuotation.getFinanceAmount());
                }
                prjQuotationCashflow.setCalcDate(prjQuotation.getFirstReleaseDate());

                //直租计算税额 一般融资租赁-直租
                if("LEASE".equals(prjQuotation.getBusinessType()) || "一般融资租赁-直租".equals(prjQuotation.getBusinessTypeN())) {
                    if (prjQuotation.getVatRate() == null) {
                        throw new HlsCusException("税率不能为空!");
                    }
                    if(prjQuotationCashflow.getCfItem() == 2){
                        prjQuotationCashflow.setNetDueAmount(prjQuotation.getNetDownPayment());
                        prjQuotationCashflow.setVatDueAmount(prjQuotation.getVatDownPayment());
                    }else{
                        Double netDueAmount = CalculateUtil.div(prjQuotation.getLeaseItemAmount(), (CalculateUtil.add(1D, prjQuotation.getVatRate())), 2);
                        Double vatDueAmount = CalculateUtil.sub(prjQuotation.getLeaseItemAmount(), netDueAmount);
                        prjQuotationCashflow.setNetDueAmount(netDueAmount);
                        prjQuotationCashflow.setVatDueAmount(vatDueAmount);
                    }
                }else{
                    if(prjQuotationCashflow.getCfItem() == 2){
                        prjQuotationCashflow.setNetDueAmount(prjQuotation.getNetDownPayment());
                        prjQuotationCashflow.setVatDueAmount(prjQuotation.getVatDownPayment());
                    }else{
                        prjQuotationCashflow.setVatDueAmount(prjQuotationCashflow.getDueAmount() - nvl(prjQuotationCashflow.getNetDueAmount(),0D));
                        prjQuotationCashflow.setNetDueAmount( nvl(prjQuotationCashflow.getNetDueAmount(),prjQuotationCashflow.getDueAmount()));
                    }

                }
            }
            //下达
            prjQuotationCashflow.setCfStatus("RELEASE");
            prjQuotationCashflowList.add(prjQuotationCashflow);
        }



        //删除这个quotation项下现金流(只删除在价目表配置中存在的现金流)
        prjQuotationCashflowMapper.deleteRentByQuotationId(prjQuotation);
        for (HlsCusPrjQuotationCashflow dt : prjQuotationCashflowList) {
            dt.set__status(DTOStatus.ADD);
            dt.setQuotationId(prjQuotation.getQuotationId());
        }
        self().batchUpdate(requestContext, prjQuotationCashflowList);


        //单次放款设备款单独处理
        autoCreateLeaseItemAmount(requestContext,prjQuotation);

        if(prjQuotation.getHolidayAdjust() != null && "Y".equals(prjQuotation.getHolidayAdjust())) {
            cashSkipWorkday(requestContext, prjQuotationCashflowList);
        }

        //插入留购金
        insertResidualValue(requestContext,prjQuotation);

        //根据修改后的日期刷新保证金跟费用的due_date
        HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();
        cashflow.setQuotationId(prjQuotation.getQuotationId());
        cashflow.setDueDate(prjQuotation.getFirstReleaseDate());
        prjQuotationCashflowMapper.updatePrjQuotationCashflowDuedate(cashflow);

        //刷新返还现金流的日期和期数
        cashflow.setDueDate(prjQuotation.getLeaseEndDate());
        cashflow.setTimes(prjQuotation.getLeaseTimes());
        prjQuotationCashflowMapper.updatePrjQuotationCashflowReturnDuedate(cashflow);

        //更新费用方案基准金额
        updateCalcBase(requestContext,prjQuotation);

        return prjQuotationCashflowList;
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> calculateLeaseChargeCashflow(IRequest iRequest, HlsCusPrjQuotation prjQuotation) throws Exception {
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = new ArrayList<>();
        prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(prjQuotation.getQuotationId());
        if(prjQuotation.getLeaseChargeRatio() == null || prjQuotation.getLeaseChargeRatio() <= 0){
            return prjQuotationCashflowList;
        }
        Double leaseAmount = MathUtil.mul(prjQuotation.getLeaseChargeRatio(),prjQuotation.getLeaseItemAmount());
        //年付
        if("12".equals(prjQuotation.getChargeCollectType())){
            //先计算一共分多少次支付
            Long lease_times = prjQuotation.getLeaseTimes();
            Long charge_times = 1L;
            Long annual_repayment_times = 0L;
            if(prjQuotation.getRentingFrequency()!=null){
                Long renting_frequency = Long.valueOf(prjQuotation.getRentingFrequency());
                annual_repayment_times = 12/renting_frequency;
                charge_times = lease_times/(12/renting_frequency) + 1;
            }
            Double dueAmount =  MathUtil.div(leaseAmount,charge_times,2);
            for(int i=0;i<charge_times;i++){
                Long times = 0+i*annual_repayment_times;
                HlsCusPrjQuotationCashflow result = prjQuotationCashflowMapper.queryDueDateByTimes(prjQuotation.getQuotationId(),times);
                HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
                quotationCashflow.setQuotationId(prjQuotation.getQuotationId());
                quotationCashflow.setCfType(3L);
                quotationCashflow.setCfItem(3L);
                quotationCashflow.setCfDirection("INFLOW");
                if(i == charge_times-1){
                    //最后一期倒减
                    quotationCashflow.setDueAmount(leaseAmount - dueAmount*(charge_times-1));
                }else{
                    quotationCashflow.setDueAmount(dueAmount);
                }
                quotationCashflow.setDueDate(result.getDueDate());
                quotationCashflow.setTimes(times);
                quotationCashflow.setFinIncomeDate(result.getFinIncomeDate());
                quotationCashflow.setCalcDate(result.getCalcDate());
                quotationCashflow.setCfStatus("RELEASE");
                //更新税额
                updateLeaseItemAmountTax(quotationCashflow,prjQuotation,quotationCashflow.getDueAmount(),quotationCashflow.getDueAmount());
                quotationCashflow.setOutstandingPrincipal(result.getOutstandingPrincipal());
                self().insert(iRequest,quotationCashflow);
            }
        }else{
            //一次性付清
            HlsCusPrjQuotationCashflow result = prjQuotationCashflowMapper.queryDueDateByTimes(prjQuotation.getQuotationId(),0L);
            HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
            quotationCashflow.setQuotationId(prjQuotation.getQuotationId());
            quotationCashflow.setCfType(3L);
            quotationCashflow.setCfItem(3L);
            quotationCashflow.setCfDirection("INFLOW");
            quotationCashflow.setDueAmount(leaseAmount);
            quotationCashflow.setDueDate(result.getDueDate());
            quotationCashflow.setTimes(0L);
            quotationCashflow.setFinIncomeDate(result.getFinIncomeDate());
            quotationCashflow.setCalcDate(result.getCalcDate());
            quotationCashflow.setCfStatus("RELEASE");
            //更新税额
            updateLeaseItemAmountTax(quotationCashflow,prjQuotation,quotationCashflow.getDueAmount(),quotationCashflow.getDueAmount());
            quotationCashflow.setOutstandingPrincipal(result.getOutstandingPrincipal());
            self().insert(iRequest,quotationCashflow);
        }
        return prjQuotationCashflowList;
    }

    //更新费用方案基准金额
    void updateCalcBase(IRequest requestContext, HlsCusPrjQuotation quotation) {
        quotation = hlsCusPrjQuotationService.selectByPrimaryKey(requestContext,quotation);

        HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();
        cashflow.setQuotationId(quotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> list = prjQuotationCashflowMapper.queryPrjCalcBaseCashflow(cashflow);
        for (HlsCusPrjQuotationCashflow item : list) {
            if ("RELEASE".equals(item.getCalcBase())) {
                item.setCalcBaseAmount(quotation.getFinanceAmount());
                item.setDueAmount(quotation.getFinanceAmount() * item.getCalcRatio());
                self().updateByPrimaryKeySelective(requestContext, item);
            }
        }
    }


    public static LocalDate DateToLocaleDate(Date date) {

        Instant instant = date.toInstant();

        ZoneId zoneId = ZoneId.systemDefault();

        return instant.atZone(zoneId).toLocalDate();

    }

    public static Date LocalDateToDate(LocalDate localDate) {

        ZoneId zoneId = ZoneId.systemDefault();

        ChronoZonedDateTime<LocalDate> zonedDateTime = localDate.atStartOfDay(zoneId);

        return Date.from(zonedDateTime.toInstant());

    }

    //根据价目表配置生成设备款
    void autoCreateLeaseItemAmount(IRequest iRequest,HlsCusPrjQuotation quotation) throws HlsCusException {

        if(quotation.getQuotationId() == null){
            throw new HlsCusException("参数获取失败,请联系管理员!");
        }

        quotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest,quotation);

        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(quotation.getPriceList());
        hlsCalcConfig = hlsCalcConfigService.selectByPrimaryKey(iRequest,hlsCalcConfig);

        String paymentType = hlsCalcConfig.getPaymentType();

        if(!StringUtils.isEmpty(paymentType)){

            //单次放款才插入或者更新设备款
            if("SINGE_PAYMENT".equals(paymentType)){

                //查询设备款是否存在
                HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
                quotationCashflow.setQuotationId(quotation.getQuotationId());
                quotationCashflow.setCfType(0L);
                quotationCashflow.setCfItem(0L);
                quotationCashflow.setCfDirection("OUTFLOW");

                List<HlsCusPrjQuotationCashflow> cashflowList = prjQuotationCashflowMapper.select(quotationCashflow);


                if(CollectionUtils.isEmpty(cashflowList) || cashflowList.size() == 0){

                    //插入一条设备款
                    quotationCashflow.setDueDate(quotation.getFirstReleaseDate());
                    quotationCashflow.setTimes(0L);
                    quotationCashflow.setFinIncomeDate(quotation.getFirstReleaseDate());
                    quotationCashflow.setCalcDate(quotation.getFirstReleaseDate());
                    quotationCashflow.setCfStatus("RELEASE");

                    //更新税额
                    updateLeaseItemAmountTax(quotationCashflow,quotation,quotation.getLeaseItemAmount(),quotation.getLeaseItemAmount());
                    quotationCashflow.setOutstandingPrincipal(quotation.getLeaseItemAmount());
                    self().insert(iRequest,quotationCashflow);
                }else{

                    //更新设备款
                    quotationCashflow = cashflowList.get(0);

                    //如果存在承兑汇票的话，同步更新承兑汇票的日期
                    List<HlsCusPrjQuotationCashflow> feeCashflowList = prjQuotationCashflowMapper.selectPaynoteLeaseChageCashflow(quotationCashflow);
                    if(CollectionUtils.isNotEmpty(feeCashflowList)){
                        for(HlsCusPrjQuotationCashflow fee:feeCashflowList){
                            if(fee.getDueDate().equals(quotationCashflow.getDueDate())){
                                fee.setDueDate(quotation.getFirstReleaseDate());
                                self().updateByPrimaryKeySelective(iRequest,fee);
                            }

                            //更新承兑时间
                            HlsCusPrjQuotationCashflow quotationCashflowPy = new HlsCusPrjQuotationCashflow();
                            quotationCashflowPy.setQuotationCashflowId(fee.getSourceCashflowId());
                            quotationCashflowPy = prjQuotationCashflowMapper.selectByPrimaryKey(quotationCashflowPy);
                            if(quotationCashflowPy != null){
                               if(quotationCashflowPy.getPaynoteTerm() != null){
                                    Date date = fee.getDueDate();//取时间
                                    LocalDate localDateFrom = DateToLocaleDate(date);
                                    date = LocalDateToDate(localDateFrom.plusDays(quotationCashflowPy.getPaynoteTerm()));
                                    quotationCashflowPy.setDueDate(date);
                                    self().updateByPrimaryKeySelective(iRequest,quotationCashflowPy);
                                }
                            }


                            //刷新承兑现金流金额
                            HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();
                            cashflow.setQuotationCashflowId(fee.getSourceCashflowId());
                            cashflow = self().selectByPrimaryKey(iRequest,cashflow);
                            if(cashflow != null){
                                if(cashflow.getPaynoteRate() != null){
                                    cashflow.setDueAmount(CalculateUtil.mul(quotation.getLeaseItemAmount(),cashflow.getPaynoteRate()));
                                    cashflow.setNetDueAmount(cashflow.getDueAmount());
                                    self().updateByPrimaryKeySelective(iRequest,cashflow);
                                }
                            }
                        }
                    }

                    quotationCashflow.setDueDate(quotation.getFirstReleaseDate());
//                    quotationCashflow.setDueDate(quotation.getLeaseStartDate());
                    quotationCashflow.setTimes(0L);

                    //更新税额
                    updateLeaseItemAmountTax(quotationCashflow,quotation,quotation.getLeaseItemAmount(),quotation.getLeaseItemAmount());
                    quotationCashflow.setOutstandingPrincipal(quotation.getLeaseItemAmount());
                    self().updateByPrimaryKeySelective(iRequest,quotationCashflow);

                }

            }
        }
        updateLeaseItemByPaynote(iRequest,quotation);
    }

    void updateLeaseItemAmountTax(HlsCusPrjQuotationCashflow cashflow,HlsCusPrjQuotation quotation,Double dueAmount,Double baseAmount) throws HlsCusException {
        cashflow.setDueAmount(dueAmount);
        cashflow.setBaseAmount(baseAmount);
        if(checkQuotationIsLease(quotation)) {
            if (quotation.getVatRate() == null) {
                throw new HlsCusException("税率不能为空!");
            }
            Double netDueAmount = CalculateUtil.div(dueAmount, (CalculateUtil.add(1D, quotation.getVatRate())), 2);
            Double vatDueAmount = CalculateUtil.sub(dueAmount, netDueAmount);
            cashflow.setNetDueAmount(netDueAmount);
            cashflow.setVatDueAmount(vatDueAmount);

            Double netBaseAmount = CalculateUtil.div(baseAmount, (CalculateUtil.add(1D, quotation.getVatRate())), 2);
            Double vatBaseAmount = CalculateUtil.sub(baseAmount, netBaseAmount);
            cashflow.setNetBaseAmount(netBaseAmount);
            cashflow.setVatBaseAmount(vatBaseAmount);
        }else {
            cashflow.setVatDueAmount(0D);
            cashflow.setNetDueAmount(dueAmount);

            cashflow.setNetBaseAmount(baseAmount);
            cashflow.setVatBaseAmount(0D);
        }

    }

    void updateLeaseItemByPaynote(IRequest iRequest,HlsCusPrjQuotation quotation) throws HlsCusException {

        //查询承兑汇票的现金流及对应放款日期的设备款现金流，先查询承兑手续费
        HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();
        cashflow.setQuotationId(quotation.getQuotationId());

        List<HlsCusPrjQuotationCashflow> feeCashflowList = prjQuotationCashflowMapper.selectPaynoteLeaseChageCashflow(cashflow);

        for(HlsCusPrjQuotationCashflow fee: feeCashflowList){

            //查询对应的承兑现金流
            cashflow.setQuotationCashflowId(fee.getSourceCashflowId());
            cashflow = prjQuotationCashflowMapper.selectByPrimaryKey(cashflow);

            if(cashflow == null){
                throw new HlsCusException("承兑现金流数据异常，请检查数据!");
            }

            //查询对应的设备款
            HlsCusPrjQuotationCashflow leaseCashflow = new HlsCusPrjQuotationCashflow();
            leaseCashflow.setQuotationId(quotation.getQuotationId());
            leaseCashflow.setDueDate(fee.getDueDate());
            List<HlsCusPrjQuotationCashflow> leaseCashflowList = prjQuotationCashflowMapper.selectLeaseItemPaynoteCashflow(leaseCashflow);
            if(CollectionUtils.isEmpty(leaseCashflowList) || leaseCashflowList.size() != 1){
                throw new HlsCusException("根据承兑现金流的放款日期找不到对应的放款现金流,请检查数据！");
            }
            leaseCashflow = leaseCashflowList.get(0);

            Double dueAmount = CalculateUtil.sub(leaseCashflow.getBaseAmount(),cashflow.getDueAmount());
            leaseCashflow.setDueAmount(dueAmount);
            if(checkQuotationIsLease(quotation)) {
                if (quotation.getVatRate() == null) {
                    throw new HlsCusException("税率不能为空!");
                }
                Double netDueAmount = CalculateUtil.div(dueAmount, (CalculateUtil.add(1D, quotation.getVatRate())), 2);
                Double vatDueAmount = CalculateUtil.sub(dueAmount, netDueAmount);
                leaseCashflow.setNetDueAmount(netDueAmount);
                leaseCashflow.setVatDueAmount(vatDueAmount);
            }else {
                leaseCashflow.setDueAmount(dueAmount);
                leaseCashflow.setNetDueAmount(dueAmount);
            }
            self().updateByPrimaryKeySelective(iRequest,leaseCashflow);

        }



    }

    Boolean checkQuotationIsLease(HlsCusPrjQuotation quotation){

        if("N".equals(quotation.getVatablePrinFlag())) {
            return false;
        }

        if("M_LEASE_13".equals(quotation.getTaxStructure()) || "R_LEASE_9".equals(quotation.getTaxStructure())){
            return true;
        }
        return false;
    }

    void insertResidualValue(IRequest iRequest,HlsCusPrjQuotation prjQuotation) throws HlsCusException {

        HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();

        prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest,prjQuotation);

        //先删除留购金
        prjQuotationCashflowMapper.deleteResidualCashflow(prjQuotation);

        //获取税率
        Double vatRate = prjQuotation.getVatRate();

        //获取留购金
        Double residualValue = prjQuotation.getResidualValue();
        cashflow.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflow = prjQuotationCashflowMapper.selectMaxDueDateByQuotationId(cashflow);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflow1 = prjQuotationCashflowMapper.select(cashflow);
        if(prjQuotationCashflow == null){
            throw new HlsCusException("租金生成失败，请检查数据!");
        }

        //计算税额
        if (vatRate != null && residualValue != null) {
            Double netDueAmount = CalculateUtil.div(residualValue, (CalculateUtil.add(1D, vatRate)), 2);
            Double vatDueAmount = CalculateUtil.sub(residualValue, netDueAmount);
            cashflow.setTimes(prjQuotation.getLeaseTimes());
            cashflow.setDueAmount(residualValue);
            if(prjQuotationCashflow != null) {
                if(prjQuotationCashflow.get(0)!=null) {
                    if (prjQuotationCashflow.get(0).getDueDate() != null) {
                        cashflow.setDueDate(prjQuotationCashflow.get(0).getDueDate());
                        cashflow.setFinIncomeDate(prjQuotationCashflow.get(0).getDueDate());
                    } else {
                        cashflow.setDueDate(prjQuotationCashflow.get(0).getFinIncomeDate());
                        cashflow.setFinIncomeDate(prjQuotationCashflow.get(0).getFinIncomeDate());
                    }
                }
            }
            cashflow.setCfItem(8L);
            cashflow.setCfType(8L);
            cashflow.setCfStatus("RELEASE");
            cashflow.setCfDirection("INFLOW");
            cashflow.setNetDueAmount(netDueAmount);
            cashflow.setVatDueAmount(vatDueAmount);
            cashflow.setTaxTypeRate(vatRate);
            self().insert(iRequest,cashflow);
        }

    }

    public Double getBillingVatRate(Long cfItem, HlsCusPrjQuotation prjQuotation, IRequest iRequest) {
        Double billingVatRate = 0D;
        HlsInvoiceProfileDtl dtl = new HlsInvoiceProfileDtl();
        dtl.setInvoiceProfile(prjQuotation.getBillingProfile());//开票税率
        dtl.setCfItem(cfItem);
        List<HlsInvoiceProfileDtl> list = new ArrayList<>();
        list = hlsInvoiceProfileDtlService.select(iRequest, dtl, 1, 1000);
        if (list.size() != 0) {
            String taxTypeCode = list.get(0).getTaxTypeCode();
            FndSysCodes fndSysCodes = new FndSysCodes();
            fndSysCodes.setTaxTypeCode(taxTypeCode);
            List<FndSysCodes> fndSysCodesList = fndSysCodesService.select(iRequest, fndSysCodes, 1, 1000);
            if (fndSysCodesList.size() != 0) {
                billingVatRate = fndSysCodesList.get(0).getTaxTypeRate();
            }

        }
        return billingVatRate;
    }


    @Override
    public List<HlsCusPrjQuotationCashflow> queryPrjQuotationCashflowByProjectId(HlsCusPrjQuotationCashflow prjQuotationCashflow, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusPrjQuotationCashflow> oldCashflow = prjQuotationCashflowMapper.queryPrjQuotationCashflowByProjectId(prjQuotationCashflow);
        HlsCusPrjProject project = new HlsCusPrjProject();

        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setQuotationId(prjQuotationCashflow.getQuotationId());
        quotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotation);
        if(quotation == null){
            return new ArrayList<>();
        }
        project.setProjectId(quotation.getSourceDocumentId());
        double sumPri = 0d;
        Double projectPrincipal = CalculateUtil.sub(quotation.getLeaseItemAmount(),quotation.getDownPayment());
        for(int i=0;i<oldCashflow.size();i++){
            if(oldCashflow.get(i).getCfItem()!=0){
                sumPri = CalculateUtil.add(sumPri,oldCashflow.get(i).getPrincipal());
            }
            oldCashflow.get(i).setOutstandingPrincipal(CalculateUtil.sub(projectPrincipal,sumPri));
        }
        return oldCashflow;
    }


    @Override
    public List<HlsCusPrjQuotationCashflow> queryCshFineInfo(HlsCusPrjQuotationCashflow prjQuotationCashflow) {
        return prjQuotationCashflowMapper.queryCshFineInfo(prjQuotationCashflow);
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> queryCshFineInfo2(HlsCusPrjQuotationCashflow prjQuotationCashflow) {
        return prjQuotationCashflowMapper.queryCshFineInfo2(prjQuotationCashflow);
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> queryFineCshFlowInfo(HlsCusPrjQuotationCashflow prjQuotationCashflow) {
        return prjQuotationCashflowMapper.queryFineCshFlowInfo(prjQuotationCashflow);
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> fineCshSubmit(IRequest requestContext, List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList) {
        //保存罚息现金流的信息
        for (HlsCusPrjQuotationCashflow dt : hlsCusPrjQuotationCashflowList) {
            if (dt.getQuotationCashflowId() != null && dt.getQuotationCashflowId() != 0) {
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                hlsCusPrjQuotationCashflow.setQuotationCashflowId(dt.getQuotationCashflowId());
                hlsCusPrjQuotationCashflow = self().selectByPrimaryKey(requestContext, hlsCusPrjQuotationCashflow);
                hlsCusPrjQuotationCashflow.setChangeFineAmount(dt.getChangeFineAmount());
                hlsCusPrjQuotationCashflow.setReductionType(dt.getReductionType());
                hlsCusPrjQuotationCashflow.setDescription(dt.getDescription());
                dt = self().updateByPrimaryKey(requestContext, hlsCusPrjQuotationCashflow);
            }
        }
        //更新报价表中租金支付表的罚息状态
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(hlsCusPrjQuotationCashflowList.get(0).getQuotationId());
        hlsCusPrjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(requestContext, hlsCusPrjQuotation);
        hlsCusPrjQuotation.setFineStatus("APPROVING");
        hlsCusPrjQuotation = hlsCusPrjQuotationService.updateByPrimaryKey(requestContext, hlsCusPrjQuotation);

        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        hlsCusPrjQuotationList.add(hlsCusPrjQuotation);
        databaseLockProvider.lock(hlsCusPrjQuotation);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(requestContext.getUserId());
        String employeeCode = employee.getEmployeeCode();
        requestContext.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CON_CONTRACT_CSH_WFL");
        activitiStartService.start(requestContext, hlsCusPrjQuotationList, params);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(requestContext.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(requestContext.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + hlsCusPrjQuotation.getPaymentNumber() + "合同的罚息减免审核";
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "租赁罚息减免审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(requestContext, hlsCusPrjQuotation.getProjectId(), "CON_CONTRACT", "CON_CONTRACT", "CON", "CON_CONTRACT_CSH_FINE_WFL", "P2D", paramsEvent);
        return hlsCusPrjQuotationCashflowList;
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> queryPrjQuotationOutCashflowByProjectId(HlsCusPrjQuotationCashflow prjQuotationCashflow, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prjQuotationCashflowMapper.queryPrjQuotationOutCashflowByProjectId(prjQuotationCashflow);
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> prjQueryCashFlow(HlsCusPrjQuotationCashflow prjQuotationCashflow, int page, int pagesize) {

        List<HlsCusPrjQuotationCashflow> resList = prjQuotationCashflowMapper.prjQueryCashFlow(prjQuotationCashflow);
        /*
        处理数据 保证金
        不抵扣 最后一期直接减去保证金金额
        抵扣  最后一期倒剪
         */
        if (resList.size() > 0) {
            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            hlsCusPrjQuotation.setQuotationId(resList.get(0).getQuotationId());
            hlsCusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
            Double residualValue = hlsCusPrjQuotation.getResidualValue() == null ? 0D : hlsCusPrjQuotation.getResidualValue();
            Double deposit = hlsCusPrjQuotation.getDeposit() == null ? 0D : hlsCusPrjQuotation.getDeposit();
            if ((PERIOD_FINAL_RETURN).equals(hlsCusPrjQuotation.getDepositReturnMethod())) {
                for (int i = 0; i < resList.size(); i++) {
                    if (i + 1 == resList.size()) {
                        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = resList.get(resList.size() - 1);
                        hlsCusPrjQuotationCashflow.setTheCashFlow(hlsCusPrjQuotationCashflow.getTheReceipt() - hlsCusPrjQuotation.getDeposit() + residualValue);
                    }
                }
            } else if (PERIOD_FINAL_DEDUCTIBLE.equals(hlsCusPrjQuotation.getDepositReturnMethod())) {
                Double lastTimeSumAmount = resList.get(resList.size() - 1).getTheReceipt();
                if (lastTimeSumAmount < deposit) {
                    lastTimeSumAmount = deposit - resList.get(resList.size() - 1).getTheReceipt();
                    resList.get(resList.size() - 1).setTheCashFlow(residualValue);

                    //进行保证金抵扣
                    for (int i = resList.size() - 2; i >= 0; i--) {
                        lastTimeSumAmount = lastTimeSumAmount - resList.get(i).getTheCashFlow();

                        if (lastTimeSumAmount < 0D) {
                            resList.get(i).setTheCashFlow(resList.get(i).getTheCashFlow() - lastTimeSumAmount - resList.get(i).getTheCashFlow());
                            break;
                        } else {
                            resList.get(i).setTheCashFlow(0D);

                        }
                    }
                } else {
                    resList.get(resList.size() - 1).setTheCashFlow(lastTimeSumAmount - deposit + residualValue);
                }
            }
        }
        return resList;
    }

    //拆税
    @Override
    public void taxCashflowDemolition(IRequest requestContext, HlsCusPrjQuotation prjQuotation) {
        Double vatRate = CalculateUtil.add(1D, prjQuotation.getVatRate());
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(prjQuotation.getSourceDocumentId());
        hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
        HlsCusPrjQuotationCashflow dto = new HlsCusPrjQuotationCashflow();
        dto.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowLists = self().select(requestContext, dto, 1, 99999999);
        if (CollectionUtils.isNotEmpty(prjQuotationCashflowLists)) {
            for (HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow : prjQuotationCashflowLists) {
                Double vatDueAmount = 0D;
                Double netDueAmount = 0D;
                Double vatPrincipal = 0D;
                Double netPrincipal = 0D;
                Double vatInterest = 0D;
                Double netInterest = 0D;
                netDueAmount = CalculateUtil.div(hlsCusPrjQuotationCashflow.getDueAmount(), vatRate);
                vatDueAmount = hlsCusPrjQuotationCashflow.getDueAmount() - netDueAmount;
                if (hlsCusPrjQuotationCashflow.getCfItem().equals(1L)) {
                    if (!hlsCusPrjProject.getBusinessType().equals("LEASEBACK")) {
                        netPrincipal = CalculateUtil.div(hlsCusPrjQuotationCashflow.getPrincipal(), vatRate);
                        vatPrincipal = hlsCusPrjQuotationCashflow.getPrincipal() - netPrincipal;
                    }
                    netInterest = CalculateUtil.div(hlsCusPrjQuotationCashflow.getInterest(), vatRate);
                    vatInterest = hlsCusPrjQuotationCashflow.getInterest() - netInterest;
                }
                hlsCusPrjQuotationCashflow.setVatDueAmount(vatDueAmount);
                hlsCusPrjQuotationCashflow.setNetDueAmount(netDueAmount);
                hlsCusPrjQuotationCashflow.setVatPrincipal(vatPrincipal);
                hlsCusPrjQuotationCashflow.setNetPrincipal(netPrincipal);
                hlsCusPrjQuotationCashflow.setVatInterest(vatInterest);
                hlsCusPrjQuotationCashflow.setNetInterest(netInterest);
                self().updateByPrimaryKeySelective(requestContext, hlsCusPrjQuotationCashflow);

            }
        }
        //  self().batchUpdate(requestContext, prjQuotationCashflowLists);
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> queryQuotationCashFlowById(HlsCusPrjQuotation hlsCusPrjQuotation) {
        return prjQuotationCashflowMapper.queryQuotationCashFlowById(hlsCusPrjQuotation);
    }

    @Override
    public int deleteQuotationCashFlowById(HlsCusPrjQuotation hlsCusPrjQuotation) {
        return prjQuotationCashflowMapper.deleteQuotationCashFlowById(hlsCusPrjQuotation);
    }

    public void levelingCashflowInterest(IRequest requestContext, List<HlsCusPrjQuotationCashflow> prjQuotationCashflowLists, HlsCusPrjQuotation prjQuotation) {

        List<HlsCusPrjQuotationCashflow> cashflowList = new ArrayList<>();
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotationCashflowLists.get(0).getQuotationId());
        hlsCusPrjQuotationCashflow.setCfItem(1L);//租金
        hlsCusPrjQuotationCashflow.setSortname("times");
        hlsCusPrjQuotationCashflow.setSortorder("desc");
        cashflowList = self().select(requestContext, hlsCusPrjQuotationCashflow, 1, 99999999);
        if (cashflowList.size() > 0) {
            for (int i = 0; i < cashflowList.size(); i++) {
                if (i == 0) {
                    Double interest = cashflowList.get(i).getInterest() + prjQuotation.getAssetsSurplusValue() - cashflowList.get(i).getOutstandingPrincipal();
                    Double principal = cashflowList.get(i).getPrincipal() - prjQuotation.getAssetsSurplusValue() + cashflowList.get(i).getOutstandingPrincipal();
                    cashflowList.get(i).setInterest(interest);
                    cashflowList.get(i).setPrincipal(principal);
                    cashflowList.get(i).setOutstandingPrincipal(prjQuotation.getAssetsSurplusValue());
                    self().updateByPrimaryKeySelective(requestContext, cashflowList.get(i));
                }
            }
        }
    }

    public void getExcelDays(IRequest requestContext, HlsCusPrjQuotation prjQuotation) {
        //计算的现金流 升序
        HlsCusPrjQuotationCashflow HlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        HlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowLists = prjQuotationCashflowMapper.prjQueryCashFlow(HlsCusPrjQuotationCashflow);
        //新建数组 保存days360计算天数
        List<Long> ExcelDaysArray = new ArrayList<>();
        List<Double> ExcelValueArray = new ArrayList<>();
        Long[] days = new Long[prjQuotationCashflowLists.size()];
        double[] excelValue = new double[prjQuotationCashflowLists.size()];
        Long Days = 0L;
        //初始化日历
        Long daysTemp = 0L;
        Calendar calendarEnd = Calendar.getInstance();
        Calendar calendarTemp = Calendar.getInstance();
        Calendar calendarStart = Calendar.getInstance();
        Date startDate = getStartDate(prjQuotationCashflowLists.get(0).getDueDate());
        calendarStart.setTime(startDate);

        Date endDate;
        int resultMonth = 0;
        for (int i = 0; i < prjQuotationCashflowLists.size(); i++) {
            if (i == 0) {
                Days = 0L;
            } else {
                calendarEnd.setTime(prjQuotationCashflowLists.get(i).getDueDate());
                endDate = getEndDate(startDate, calendarEnd.getTime());
                calendarTemp.setTime(endDate);
                resultMonth = calendarTemp.get(Calendar.MONTH) - calendarStart.get(Calendar.MONTH);

                daysTemp = getCalcDays(prjQuotationCashflowLists.get(0).getDueDate(), endDate, prjQuotationCashflowLists.get(i).getDueDate());
                Days = resultMonth * 30 - daysTemp;
            }
            ExcelDaysArray.add(Days);
            days[i] = Days;
            excelValue[i] = prjQuotationCashflowLists.get(i).getTheCashFlow();
            ExcelValueArray.add(prjQuotationCashflowLists.get(i).getTheCashFlow());
        }
        //  Double xirr = Newtons_method(0.1D, excelValue, days);
        // Double irr = titularIrr(ExcelValueArray, ExcelDaysArray, 0.1D);
    }

    public Date getStartDate(Date startDate) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startDate);
        calendarStart.add(Calendar.DAY_OF_MONTH, -calendarStart.get(Calendar.DAY_OF_MONTH));
        calendarStart.add(Calendar.DAY_OF_MONTH, 30);

        return calendarStart.getTime();
    }

    public Date getEndDate(Date startDate, Date endDate) {

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endDate);
        calendarEnd.add(Calendar.DAY_OF_MONTH, -calendarEnd.get(Calendar.DAY_OF_MONTH));
        calendarEnd.add(Calendar.DAY_OF_MONTH, 30);
        return calendarEnd.getTime();

    }

    /**
     * 获取两个日期之间相差天数
     *
     * @param calcStartDate
     * @param calcEndDate
     */

    public Long getCalcDays(Date calcStartDate, Date calcEndDate, Date dueDate) {
        Long result;
        Calendar calcStart = Calendar.getInstance();
        Calendar calcEnd = Calendar.getInstance();
        Calendar calcDueDate = Calendar.getInstance();
        calcStart.setTime(calcStartDate);
        calcEnd.setTime(calcEndDate);
        calcDueDate.setTime(dueDate);

        if (calcStart.get(Calendar.DAY_OF_MONTH) == calcDueDate.get(Calendar.DAY_OF_MONTH) || calcDueDate.get(Calendar.DAY_OF_MONTH) > 30L) {
            result = 0L;
        } else {
            if (calcEndDate.getTime() > dueDate.getTime()) {
                result = (calcEnd.getTimeInMillis() - calcDueDate.getTimeInMillis()) / (1000 * 3600 * 24);
            } else {
                result = (calcDueDate.getTimeInMillis() - calcEnd.getTimeInMillis()) / (1000 * 3600 * 24);

            }
        }

        return result;
    }

    /*
    计算名义IRR1
     */
    public static double titularIrr(List<Double> values, List<Long> days, double guess) {
        int maxIterationCount = 20;
        double absoluteAccuracy = 1.0E-7D;
        double x0 = guess;

        for (int i = 0; i < maxIterationCount; ++i) {
            double fValue = 0.0D;
            double fDerivative = 0.0D;

            for (int k = 0; k < days.size(); ++k) {
                Long day = days.get(k);
                fValue += (Double) values.get(k) / Math.pow(1.0D + x0, (double) day);
                fDerivative += (double) (-k) * (Double) values.get(k) / Math.pow(1.0D + x0, (double) (days.get(k + 1)));
            }

            double x1 = x0 - fValue / fDerivative;
            if (Math.abs(x1 - x0) <= absoluteAccuracy) {
                return x1;
            }

            x0 = x1;
        }

        return 0.0D / 0.0;
    }

    //保证金退还现金流
    public void insertdeposit(IRequest requestContext, HlsCusPrjQuotation prjQuotation) {
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());

        hlsCusPrjQuotationCashflow.setCfItem(DEPOSIT_CF_ITEM);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowLists = prjQuotationCashflowMapper.select(hlsCusPrjQuotationCashflow);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflow = prjQuotationCashflowMapper.selectMaxDueDateByQuotationId(hlsCusPrjQuotationCashflow);

        for (HlsCusPrjQuotationCashflow quotationCashflow : prjQuotationCashflowLists) {
            HlsCusPrjQuotationCashflow dto = new HlsCusPrjQuotationCashflow();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(quotationCashflow);
            hlsBeanRefUtilService.setFieldValue(dto, map);
            dto.setCfItem(DEPOSIT_RETURN_CF_ITEM);
            dto.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_DEPOSIT_RETURN_CF_TYPE);
            dto.setCfDirection(DEPOSIT_RETURN_CF_DIRECTION);
            dto.setTimes(prjQuotation.getLeaseTimes());
            dto.setDueDate(prjQuotationCashflow.get(0).getDueDate());
            dto.setCalcDate(prjQuotationCashflow.get(0).getDueDate());
            dto.setOutstandingPrincipal(0D);
            self().insertSelective(requestContext, dto);
        }

    }

    @Override
    public void saveFeeCashflow(IRequest iRequest, List<HlsCusPrjQuotationCashflow> cusPrjQuotationCashflowList) throws hls.core.utils.exception.HlsCusException, HlsCusException {

        if(cusPrjQuotationCashflowList != null && cusPrjQuotationCashflowList.size() > 0) {

            HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
            quotation.setQuotationId(cusPrjQuotationCashflowList.get(0).getQuotationId());
            quotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest,quotation);

            for (HlsCusPrjQuotationCashflow cashflow : cusPrjQuotationCashflowList) {
                if (cashflow.getQuotationId() == null) {
                    throw new hls.core.utils.exception.HlsCusException("数据异常,请联系管理员!");
                }
                if (cashflow.getQuotationCashflowId() == null && "insert".equals(cashflow.get__status())) {
                    cashflow.setCfDirection("INFLOW");
                    if(cashflow.getTimes() == null) {
                        cashflow.setTimes(0L);
                    }
                    cashflow.setCfStatus("RELEASE");
                    cashflow.setCalcDate(cashflow.getDueDate());
                    cashflow.setFinIncomeDate(cashflow.getDueDate());

                    //计算税额
                    if (cashflow.getTaxTypeRate() != null) {
                        Double netDueAmount = CalculateUtil.div(cashflow.getDueAmount(), (CalculateUtil.add(1D, cashflow.getTaxTypeRate())), 2);
                        Double vatDueAmount = CalculateUtil.sub(cashflow.getDueAmount(), netDueAmount);
                        cashflow.setNetDueAmount(netDueAmount);
                        cashflow.setVatDueAmount(vatDueAmount);
                    }else{
                        cashflow.setNetDueAmount(cashflow.getDueAmount());
                        cashflow.setVatDueAmount(0D);
                    }

                    //设备款拆税

                    if(cashflow.getCfItem().equals(0L) && cashflow.getCfType().equals(0L)){
                        cashflow.setCfDirection("OUTFLOW");
                        updateLeaseItemAmountTax(cashflow,quotation,cashflow.getDueAmount(),cashflow.getBaseAmount());
                    }

                    self().insert(iRequest, cashflow);


                } else if("update".equals(cashflow.get__status())){
                    //计算税额
                    if (cashflow.getTaxTypeRate() != null) {
                        Double netDueAmount = CalculateUtil.div(cashflow.getDueAmount(), (CalculateUtil.add(1D, cashflow.getTaxTypeRate())), 2);
                        Double vatDueAmount = CalculateUtil.sub(cashflow.getDueAmount(), netDueAmount);
                        cashflow.setNetDueAmount(netDueAmount);
                        cashflow.setVatDueAmount(vatDueAmount);
                    }else{
                        cashflow.setNetDueAmount(cashflow.getDueAmount());
                        cashflow.setVatDueAmount(0D);
                    }

                    //设备款拆税
                    if(cashflow.getCfItem().equals(0L) && cashflow.getCfType().equals(0L)){
                        updateLeaseItemAmountTax(cashflow,quotation,cashflow.getDueAmount(),cashflow.getBaseAmount());
                    }

                    self().updateByPrimaryKeySelective(iRequest,cashflow);
                }else if("delete".equals(cashflow.get__status())) {
                    if(cashflow.getCfItem().equals(91L)){

                        cashflow = self().selectByPrimaryKey(iRequest,cashflow);
                        HlsCusPrjQuotationCashflow paynote = new HlsCusPrjQuotationCashflow();
                        paynote.setQuotationCashflowId(cashflow.getSourceCashflowId());

                        self().deleteByPrimaryKey(cashflow);
                        self().deleteByPrimaryKey(paynote);
                    }else {
                        self().deleteByPrimaryKey(cashflow);
                    }
                }
            }

            //刷新保证金退还
            reflashdeposit(iRequest,quotation);

            //刷新风险金
            reflashRisk(iRequest,quotation);

            //刷新deposit, lease_charge, LEASE_MGT_FEE
            List<HlsCusPrjQuotation> quotationList = hlsCusPrjQuotationMapper.selectQuotationFeeSum(quotation);
            if(quotationList != null && quotationList.size() == 1){
                quotation.setDeposit(quotationList.get(0).getDeposit());
                quotation.setLeaseCharge(quotationList.get(0).getLeaseCharge());
                quotation.setLeaseMgtFee(quotationList.get(0).getLeaseMgtFee());
                hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest,quotation);
            }

            //当时合同报价的时候，同步更新合同现金流表
            if("CON_CONTRACT".equals(quotation.getSourceDocumentCategory())) {
                hlsCusConContractCashflowMapper.deleteContractCashflowByQuotationId(quotation);
                saveCashflowFromQuotationCashflow(iRequest, quotation.getSourceDocumentId(), quotation.getQuotationId(), quotation.getInterestAmortizationMethod());
            }

            hlsCusPrjQuotationService.updateXirr(iRequest, quotation);
        }


    }

    @Override
    public void savePaynote(IRequest iRequest, List<HlsCusPrjQuotationCashflow> cashflowList) throws HlsCusException {

        if(cashflowList != null && cashflowList.size() > 0){

            HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
            quotation.setQuotationId(cashflowList.get(0).getQuotationId());
            quotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest,quotation);
            if(quotation == null){
                throw new HlsCusException("参数获取失败,请联系管理员!");
            }

            for(HlsCusPrjQuotationCashflow cashflow : cashflowList){
                if(cashflow.getQuotationCashflowId() != null){

                    //quotationCashflowId为手续费的现金流id
                    HlsCusPrjQuotationCashflow leaseChage = new HlsCusPrjQuotationCashflow();
                    leaseChage.setQuotationCashflowId(cashflow.getQuotationCashflowId());
                    leaseChage = self().selectByPrimaryKey(iRequest,leaseChage);
                    leaseChage.setDueDate(cashflow.getDueDate());
                    leaseChage.setFinIncomeDate(cashflow.getDueDate());
                    leaseChage.setCalcDate(cashflow.getDueDate());
                    leaseChage.setDueAmount(cashflow.getPaynoteChargeFee());
                    leaseChage.setPaynoteChargeRate(cashflow.getPaynoteChargeRate());

                    //拆税
                    /*Double netDueAmount = CalculateUtil.div(leaseChage.getDueAmount(), (CalculateUtil.add(1D, 0.06)), 2);
                    Double vatDueAmount = CalculateUtil.sub(leaseChage.getDueAmount(), netDueAmount);*/
                    leaseChage.setNetDueAmount(leaseChage.getDueAmount());
                    leaseChage.setVatDueAmount(0D);
                    self().updateByPrimaryKeySelective(iRequest, leaseChage);

                    //根据来源id查询开票现金流
                    HlsCusPrjQuotationCashflow paynote = new HlsCusPrjQuotationCashflow();
                    paynote.setQuotationCashflowId(leaseChage.getSourceCashflowId());
                    paynote.setPaynoteTerm(cashflow.getPaynoteTerm());
                    paynote.setDueDate(cashflow.getAcceptedDate());
                    paynote.setFinIncomeDate(cashflow.getAcceptedDate());
                    paynote.setCalcDate(cashflow.getAcceptedDate());
                    paynote.setDueAmount(cashflow.getPaynoteAmount());
                    paynote.setPaynoteRate(cashflow.getPaynoteRate());
                    //拆税
                    if(checkQuotationIsLease(quotation)) {
                        if (quotation.getVatRate() == null) {
                            throw new HlsCusException("税率不能为空!");
                        }
                        Double payNetDueAmount = CalculateUtil.div(paynote.getDueAmount(), (CalculateUtil.add(1D, quotation.getVatRate())), 2);
                        Double PayVatDueAmount = CalculateUtil.sub(paynote.getDueAmount(), payNetDueAmount);
                        paynote.setNetDueAmount(payNetDueAmount);
                        paynote.setVatDueAmount(PayVatDueAmount);
                    }else {
                        paynote.setNetDueAmount(cashflow.getPaynoteAmount());
                    }
                    self().updateByPrimaryKeySelective(iRequest,paynote);

                }else{

                    //开票金额生成一条现金流
                    HlsCusPrjQuotationCashflow paynote = new HlsCusPrjQuotationCashflow();
                    paynote.setTimes(0L);
                    paynote.setCfType(90L);
                    paynote.setCfItem(90L);
                    paynote.setCfDirection("OUTFLOW");
                    paynote.setCfStatus("RELEASE");
                    paynote.setPaynoteRate(cashflow.getPaynoteRate());
                    paynote.setQuotationId(cashflow.getQuotationId());
                    paynote.setPaynoteTerm(cashflow.getPaynoteTerm());
                    paynote.setDueDate(cashflow.getAcceptedDate());
                    paynote.setFinIncomeDate(cashflow.getAcceptedDate());
                    paynote.setCalcDate(cashflow.getAcceptedDate());
                    paynote.setDueAmount(cashflow.getPaynoteAmount());

                    //拆税
                    if(checkQuotationIsLease(quotation)) {
                        if (quotation.getVatRate() == null) {
                            throw new HlsCusException("税率不能为空!");
                        }
                        Double netDueAmount = CalculateUtil.div(paynote.getDueAmount(), (CalculateUtil.add(1D, quotation.getVatRate())), 2);
                        Double vatDueAmount = CalculateUtil.sub(paynote.getDueAmount(), netDueAmount);
                        paynote.setNetDueAmount(netDueAmount);
                        paynote.setVatDueAmount(vatDueAmount);
                    }else {
                        paynote.setNetDueAmount(cashflow.getPaynoteAmount());
                    }
                    self().insert(iRequest, paynote);

                    //手续费生成一条现金流
                    HlsCusPrjQuotationCashflow leaseChage = new HlsCusPrjQuotationCashflow();
                    leaseChage.setTimes(0L);
                    leaseChage.setCfType(91L);
                    leaseChage.setCfItem(91L);
                    leaseChage.setDueDate(cashflow.getDueDate());
                    leaseChage.setFinIncomeDate(cashflow.getDueDate());
                    leaseChage.setCalcDate(cashflow.getDueDate());
                    leaseChage.setDueAmount(cashflow.getPaynoteChargeFee());
                    leaseChage.setPaynoteChargeRate(cashflow.getPaynoteChargeRate());
                    leaseChage.setQuotationId(cashflow.getQuotationId());
                    leaseChage.setCfDirection("OUTFLOW");
                    leaseChage.setCfStatus("RELEASE");

                    //设置来源现金流
                    leaseChage.setSourceCashflowId(paynote.getQuotationCashflowId());

                    //拆税
                    /*Double netDueAmount = CalculateUtil.div(leaseChage.getDueAmount(), (CalculateUtil.add(1D, 0.06)), 2);
                    Double vatDueAmount = CalculateUtil.sub(leaseChage.getDueAmount(), netDueAmount);*/
                    leaseChage.setNetDueAmount(leaseChage.getDueAmount());
                    leaseChage.setVatDueAmount(0D);
                    self().insert(iRequest, leaseChage);
                }
            }
            autoCreateLeaseItemAmount(iRequest,quotation);

            hlsCusPrjQuotationService.updateXirr(iRequest, quotation);
        }

    }

    public void saveCashflowFromQuotationCashflow(IRequest iRequest, Long contractId, Long quotationId,String interestAmortizationMethod) {

        //关闭查询时附带权限
        iRequest.setAttribute("wflRuleControlFlag", "Y");

        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = hlsCusConContractMapper.selectByPrimaryKey(c);
        List<HlsCusConContractCashflow> conContractCashflowList = new ArrayList<>();
        HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflowParameter.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
        if (prjQuotationCashflowList.size() > 0) {
            c.setFirstPayDate(prjQuotationCashflowList.get(0).getDueDate());
            c.setLeaseEndDate(prjQuotationCashflowList.get(0).getDueDate());
        }
        for (int i = 0; i < prjQuotationCashflowList.size(); i++) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            if (prjQuotationCashflowList.get(i).getDueDate() != null) {
                //新加判断 报价现金流的dueDate可能为空
                if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getFirstPayDate().getTime()) < 0) {
                    c.setFirstPayDate(prjQuotationCashflowList.get(i).getDueDate());
                }
                if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getLeaseEndDate().getTime()) > 0) {
                    c.setLeaseEndDate(prjQuotationCashflowList.get(i).getDueDate());
                }
            }

            BeanRefUtils.beanToBean(prjQuotationCashflowList.get(i), conContractCashflow, hlsBeanRefUtilService);

            // prjQuotationCashflow times double -> conContractCashflow times long
            conContractCashflow.setTimes(prjQuotationCashflowList.get(i).getTimes().longValue());
            conContractCashflow.setContractId(contractId);
            conContractCashflow.setCfStatus("RELEASE");
            conContractCashflow.setWriteOffFlag("NOT");
            conContractCashflow.setBillingStatus("NOT");
            conContractCashflow.setOverdueStatus("N");
            conContractCashflow.setPenaltyProcessStatus("N");
            conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
            conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
            conContractCashflow.setGeneratedSourceDocId(quotationId);
            conContractCashflow.setGeneratedSourceDocLineId(prjQuotationCashflowList.get(i).getQuotationCashflowId());

            if(conContractCashflow.getCfItem() == 1L){
                conContractCashflow.setAmortizationMethod(interestAmortizationMethod);
            }

            conContractCashflowService.insertSelective(iRequest, conContractCashflow);
            conContractCashflowList.add(conContractCashflow);
        }
        hlsCusConContractMapper.updateByPrimaryKey(c);
    }

    //刷新刷新风险金退还现金流
    public void reflashRisk(IRequest requestContext, HlsCusPrjQuotation prjQuotation) {
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());

        hlsCusPrjQuotationCashflow.setCfItem(RISK_CF_ITEM);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowLists = prjQuotationCashflowMapper.select(hlsCusPrjQuotationCashflow);

        //先删除风险金退回现金流
        prjQuotationCashflowMapper.deleteRiskReturnCashflow(prjQuotation);

        for (HlsCusPrjQuotationCashflow quotationCashflow : prjQuotationCashflowLists) {
            HlsCusPrjQuotationCashflow dto = new HlsCusPrjQuotationCashflow();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(quotationCashflow);
            hlsBeanRefUtilService.setFieldValue(dto, map);
            dto.setCfItem(RISK_RETURN_CF_ITEM);
            dto.setCfType(5L);
            dto.setCfDirection(DEPOSIT_RETURN_CF_DIRECTION);
            dto.setTimes(prjQuotation.getLeaseTimes());
            if(quotationCashflow.getDepositReturnDate() != null) {
                dto.setDueDate(quotationCashflow.getDepositReturnDate());
                dto.setCalcDate(quotationCashflow.getDepositReturnDate());
            }else{
                dto.setDueDate(prjQuotation.getLeaseEndDate());
                dto.setCalcDate(prjQuotation.getLeaseEndDate());
            }
            dto.setOutstandingPrincipal(0D);
            self().insertSelective(requestContext, dto);
        }

    }

    //刷新保证金退还现金流
    public void reflashdeposit(IRequest requestContext, HlsCusPrjQuotation prjQuotation) {
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());

        hlsCusPrjQuotationCashflow.setCfItem(DEPOSIT_CF_ITEM);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowLists = prjQuotationCashflowMapper.select(hlsCusPrjQuotationCashflow);
        //HlsCusPrjQuotationCashflow prjQuotationCashflow = prjQuotationCashflowMapper.selectMaxDueDateByQuotationId(hlsCusPrjQuotationCashflow);

        //先删除保证金退回现金流
        prjQuotationCashflowMapper.deleteDepositReturnCashflow(prjQuotation);

        for (HlsCusPrjQuotationCashflow quotationCashflow : prjQuotationCashflowLists) {
            HlsCusPrjQuotationCashflow dto = new HlsCusPrjQuotationCashflow();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(quotationCashflow);
            hlsBeanRefUtilService.setFieldValue(dto, map);
            dto.setCfItem(DEPOSIT_RETURN_CF_ITEM);
            dto.setCfType(5L);
            dto.setCfDirection(DEPOSIT_RETURN_CF_DIRECTION);
            dto.setTimes(prjQuotation.getLeaseTimes());
            if(quotationCashflow.getDepositReturnDate() != null) {
                dto.setDueDate(quotationCashflow.getDepositReturnDate());
                dto.setCalcDate(quotationCashflow.getDepositReturnDate());
            }else{
                dto.setDueDate(prjQuotation.getLeaseEndDate());
                dto.setCalcDate(prjQuotation.getLeaseEndDate());
            }
            dto.setOutstandingPrincipal(0D);
            self().insertSelective(requestContext, dto);
        }

    }

        /*
      计算名义IRR2
      */

    public double dateDiff(Date d1, Date d2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //   long day = 24 * 60 * 60 * 1000;
//        String startDate = sdf.format(d1);
//        String endDate = sdf.format(d2);
        String dateFlag = "N";
        Date startDate;
        Date endDate;
        if (d1.compareTo(d2) == 0 || d1.compareTo(d2) == 1) {
            startDate = d2;
            endDate = d1;
            dateFlag = "Y";
        } else {
            startDate = d1;
            endDate = d2;
        }
        String startDateString = hlsCusPrjQuotationSubsectionService.getStartDate(startDate);
        String dayString = startDateString.substring(startDateString.indexOf("-", startDateString.indexOf("-") + 1) + 1);
        String endDateString = hlsCusPrjQuotationSubsectionService.getEndDate(dayString, endDate);
        //   days = getDays(startDateString, endDateString);
        Long days = 0L;
        if (endDateString.equals(startDateString)) {
            days = 0L;
        } else {
            if (dateFlag.equals("Y")) {
                days = -hlsCusPrjQuotationSubsectionService.getDays(startDateString, endDateString);
            } else {
                days = hlsCusPrjQuotationSubsectionService.getDays(startDateString, endDateString);
            }

        }
        if (d1.compareTo(d2) == 0) {
            days = 0L;
        }
        return -days;
    }

    public double f_xirr(double p, Date dt, Date dt0, double x) {
        return p * Math.pow((1.0 + x), (dateDiff(dt0, dt) / 360.0));
    }

    public double df_xirr(double p, Date dt, Date dt0, double x) {
        return (1.0 / 360.0) * dateDiff(dt0, dt) * p * Math.pow((x + 1.0), ((dateDiff(dt0, dt) / 360.0) - 1.0));
    }

    public double total_f_xirr(double[] payments, Date[] days, double x) {
        double resf = 0.0;

        for (int i = 0; i < payments.length; i++) {
            resf = resf + f_xirr(payments[i], days[i], days[0], x);
        }

        return resf;
    }

    public double total_df_xirr(double[] payments, Date[] days, double x) {
        double resf = 0.0;

        for (int i = 0; i < payments.length; i++) {
            resf = resf + df_xirr(payments[i], days[i], days[0], x);
        }

        return resf;
    }

    @Override
    public double Newtons_method(double guess, double[] payments, Date[] days) {
        double x0 = guess;
        double x1 = 0.0;
        double err = 1e+100;

        while (err > tol) {
            x1 = x0 - total_f_xirr(payments, days, x0) / total_df_xirr(payments, days, x0);
            err = Math.abs(x1 - x0);
            x0 = x1;
        }

        return x0;
    }

    //跳过工作日历
    @Override
    public void cashSkipWorkday(IRequest requestContext, List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList) {
        //跳过工作日
        for (HlsCusPrjQuotationCashflow cashflow : hlsCusPrjQuotationCashflowList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cashflow.getDueDate());
            if (cashflow.getTimes() != 0L) {
                //如果支付日期为周六，那么让当前日期-1天
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    cashflow.setDueDate(calendar.getTime());
                }
                //如果是周日，那么让当前日期-2天
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, -2);
                    cashflow.setDueDate(calendar.getTime());
                }
                //更新到数据库中
                self().updateByPrimaryKeySelective(requestContext, cashflow);
            }
        }
    }

    /**
     * 数据导入方法
     * 将临时表中的数据提取出来，每条数据封装成一个Map对象
     * Map<属性名，属性值>
     * 每个Map应该可以转换成一个DTO对象
     * 该方法重写需要实现以下功能
     * 1.Map转换为DTO
     * 2.数据合法性校验
     * 3.将合法的数据插入到数据库中
     *
     * @param iRequest
     * @param dataMap  临时表数据
     * @param descMap  获取字段描述用Map,<key=Dto属性名,value=模板中定义的显示用名>
     * @param lang     当导入涉及到多语言时（在模板定义时勾选了多语言选项）这里会传入一个在执行导入时勾选的多语言对应的Code
     * @return 执行成功的条数
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int importData(IRequest iRequest, List<Map<String, String>> dataMap, Map<String, String> descMap, String lang) throws IllegalArgumentException {
        int errorCount = 0;
        //传递时间格式
        List<CodeValue> dateFormat = codeService.selectCodeValuesByCodeName(iRequest, "FND_IMP_DATE_FORMAT");
        List<String> formats = new ArrayList<>();
        for (CodeValue c : dateFormat) {
            formats.add(c.getValue());
        }
        try {
            List<Object> objects = HlsCusImportDataUtil.dataToDto(HlsCusPrjQuotationCashflow.class, dataMap, formats);
            Long quotationId = Long.parseLong(dataMap.get(0).get("tempKey"));

            //根据项目Id查询报价方案Id

            //获取头上的保证金
            HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
            prjQuotation.setQuotationId(quotationId);
            prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, prjQuotation);
            if (ObjectUtils.isEmpty(prjQuotation)) {
                throw new IllegalArgumentException("未找到报价数据，请联系管理员");
            }


            Double depositAmount = prjQuotation.getDeposit() == null ? 0D : prjQuotation.getDeposit();
            Double serviceAmount = prjQuotation.getAdvServiceFee() == null ? 0D : prjQuotation.getAdvServiceFee();
            Double leaseChargeAmount = prjQuotation.getLeaseCharge() == null ? 0D : prjQuotation.getLeaseCharge();
            Double residualValueAmount = prjQuotation.getResidualValue() == null ? 0D : prjQuotation.getResidualValue();
            Double downPaymentAmount = prjQuotation.getDownPayment() == null ? 0D : prjQuotation.getDownPayment();


            /**
             * 导入之前，应该先校验该报价方案中是否存在还款计划，如果存在那么将其先删除
             */
            HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();
            cashflow.setQuotationId(quotationId);
            //设置核销标识，只删除核销标识为NOT的现金流
            cashflow.setWriteOffFlag("NOT");
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowTemp = self().select(iRequest, cashflow, 1, 999999);
            self().batchDelete(hlsCusPrjQuotationCashflowTemp);

           /* List<HlsCusFctQuotationCashflow> hlsCusFctQuotationCashflowList=self().fctQuotationCashflowselect(iRequest,cashflow,null,null);
            if(CollectionUtils.isNotEmpty(hlsCusFctQuotationCashflowList)){
            List<HlsCusFctQuotationCashflow> hlsCusFctQuotationCashflowList = self().fctQuotationCashflowselect(iRequest, cashflow, null, null);
            if (CollectionUtils.isNotEmpty(hlsCusFctQuotationCashflowList)) {
                self().batchDelete(hlsCusFctQuotationCashflowList);
            }
            */
            //保证金
            Double lineDepositInAmount = 0D;
            //咨询服务费
            Double lineServiceTotalAmount = 0D;
            //手续费
            Double lineLeaseChargeTotalAmount = 0D;
            //留购价
            Double lineResidualValueTotalAmount = 0D;
            //首付款
            Double lineDownPaymentTotalAmount = 0D;
            //遍历
            for (int i = 0; i < objects.size(); i++) {
                //定义错误消息
                StringBuilder sb = new StringBuilder();

                HlsCusPrjQuotationCashflow quotationCashflow = (HlsCusPrjQuotationCashflow) objects.get(i);

                String cfDirectionDesc = quotationCashflow.getCfDirectionDesc();
                //判断是哪一种现金流类型
                switch (quotationCashflow.getCfItemDesc()) {
                    //保证金
                    case HlsCusConstant.CASHFLOW_ITEM.LEASE_DEPOSIT_DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_DEPOSIT_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_DEPOSIT_CF_TYPE);
                        break;
                    //保证金退还
                    case HlsCusConstant.CASHFLOW_ITEM.DEPOSIT_RETURN_DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_DEPOSIT_RETURN_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_DEPOSIT_RETURN_CF_TYPE);
                        break;
                    //租金
                    case HlsCusConstant.CASHFLOW_ITEM.LEASE_DUE_AMOUNT_DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_DUE_AMOUNT_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_DUE_AMOUNT_CF_TYPE);
                        break;
                    //设备款
                    case HlsCusConstant.CASHFLOW_ITEM.LEASE_ITEM_DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_ITEM_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_ITEM_CF_TYPE);
                        break;
                    //手续费
                    case HlsCusConstant.CASHFLOW_ITEM.LEASE_LEASE_CHARGE_DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_LEASE_CHARGE_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_LEASE_CHARGE_CF_TYPE);
                        break;
                    //首付款
                    case HlsCusConstant.CASHFLOW_ITEM.LEASE_DOWN_PAYMENT_DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_DOWN_PAYMENT_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_DOWN_PAYMENT_CF_TYPE);
                        break;
                    //咨询服务费
                    case HlsCusConstant.CASHFLOW_ITEM.LEASE_ADVICE_DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_ADVICE_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_ADVICE_CF_TYPE);
                        break;
                    //留购价
                    case HlsCusConstant.CASHFLOW_ITEM.LEASE_RESIDUAL_VALUE__DESC:
                        quotationCashflow.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LEASE_RESIDUAL_VALUE_CF_ITEM);
                        quotationCashflow.setCfType(HlsCusConstant.CASHFLOW_ITEM.LEASE_RESIDUAL_VALUE_CF_TYPE);
                        break;
                    default:
                        errorCount++;
                        impDataService.updateErrMessage(dataMap, i, sb.append("错误的现金流类型").toString());
                }
                //校验导入的还款计划中保证金额是否和报价中的保证金额相等，校验咨询服务费金额是否相等
                //保证金
                if (HlsCusConstant.CASHFLOW_ITEM.LEASE_DEPOSIT_CF_ITEM.equals(quotationCashflow.getCfItem())) {
                    lineDepositInAmount += quotationCashflow.getDueAmount() == null ? 0D : quotationCashflow.getDueAmount();
                }
                //咨询服务费
                if (HlsCusConstant.CASHFLOW_ITEM.LEASE_ADVICE_CF_ITEM.equals(quotationCashflow.getCfItem())) {
                    lineServiceTotalAmount += quotationCashflow.getDueAmount() == null ? 0D : quotationCashflow.getDueAmount();
                }
                //手续费
                if (HlsCusConstant.CASHFLOW_ITEM.LEASE_LEASE_CHARGE_CF_ITEM.equals(quotationCashflow.getCfItem())) {
                    lineLeaseChargeTotalAmount += quotationCashflow.getDueAmount() == null ? 0D : quotationCashflow.getDueAmount();
                }
                //留购价
                if (HlsCusConstant.CASHFLOW_ITEM.LEASE_RESIDUAL_VALUE_CF_ITEM.equals(quotationCashflow.getCfItem())) {
                    lineResidualValueTotalAmount += quotationCashflow.getDueAmount() == null ? 0D : quotationCashflow.getDueAmount();
                }
                //首付款
                if (HlsCusConstant.CASHFLOW_ITEM.LEASE_DOWN_PAYMENT_CF_ITEM.equals(quotationCashflow.getCfItem())) {
                    lineDownPaymentTotalAmount += quotationCashflow.getDueAmount() == null ? 0D : quotationCashflow.getDueAmount();
                }

                //判断现金流方向是否是 流入或者流出  不是的话，就报错
                if (HlsCusConstant.INFLOW_DESC.equalsIgnoreCase(cfDirectionDesc)) {
                    quotationCashflow.setCfDirection(HlsCusConstant.INFLOW);
                } else if (HlsCusConstant.OUTFLOW_DESC.equalsIgnoreCase(cfDirectionDesc)) {
                    quotationCashflow.setCfDirection(HlsCusConstant.OUTFLOW);
                } else {
                    errorCount++;
                    impDataService.updateErrMessage(dataMap, i, sb.append("现金流方向错误").toString());
                }
                //对数据进行保存
                quotationCashflow.setQuotationId(quotationId);
                quotationCashflow.setProjectId(prjQuotation.getSourceDocumentId());
                quotationCashflow.setWriteOffFlag("NOT");
                quotationCashflow.setWriteOffAmount(0D);
                quotationCashflow.setCfStatus("RELEASE");
                //拆税getRate
//                quotationCashflow.setNetDueAmount(CalculateUtil.div(quotationCashflow.getDueAmount(), CalculateUtil.add(1D, rate)));
//                quotationCashflow.setVatDueAmount(CalculateUtil.sub(quotationCashflow.getDueAmount(), CalculateUtil.div(quotationCashflow.getDueAmount(), CalculateUtil.add(1D, rate))));
//
                Double dueAmount = CalculateUtil.add(quotationCashflow.getInterest(), quotationCashflow.getPrincipal());
                if (dueAmount.compareTo(quotationCashflow.getDueAmount()) != 0 && quotationCashflow.getCfItem().compareTo(HlsCusConstant.CASHFLOW_ITEM.LEASE_DUE_AMOUNT_CF_ITEM) == 0) {
                    throw new HlsCusException("导入的利息+本金与租金不相等，请检查");
                }
                quotationCashflow = self().insertSelective(iRequest, quotationCashflow);
            }
            //在插入数据库之前先校验一下保证金和咨询服务费

            if (!lineDepositInAmount.equals(depositAmount)) {
                //在这里抛出一个错误，然后在catch那里去捕获
                throw new HlsCusException("导入的保证金金额之和与报价头上的保证金额不相等，请检查");
            }
            if (!lineLeaseChargeTotalAmount.equals(leaseChargeAmount)) {
                //在这里抛出一个错误，然后在catch那里去捕获
                throw new HlsCusException("导入的手续费金额之和与报价头上的手续费金额不相等，请检查");
            }
            if (!lineResidualValueTotalAmount.equals(residualValueAmount)) {
                //在这里抛出一个错误，然后在catch那里去捕获
                throw new HlsCusException("导入的留购价金额之和与报价头上的留购价金额不相等，请检查");
            }
            if (!lineServiceTotalAmount.equals(serviceAmount)) {
                throw new HlsCusException("导入的咨询服务费之和【" + lineServiceTotalAmount + "】与报价头上的咨询服务费金额【" + serviceAmount + "】不相等，请检查");
            }
            if (!lineDownPaymentTotalAmount.equals(downPaymentAmount)) {
                //在这里抛出一个错误，然后在catch那里去捕获
                throw new HlsCusException("导入的首付款金额之和与报价头上的首付款金额不相等，请检查");
            }
            //拆税
            taxCashflowDemolition(iRequest, prjQuotation);
            //跳工作日
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflow.setQuotationId(quotationId);
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = self().select(iRequest, hlsCusPrjQuotationCashflow, 1, 9999999);
            cashSkipWorkday(iRequest, hlsCusPrjQuotationCashflowList);
            //计算irr xirr
            hlsCusPrjQuotationService.updateXirr(iRequest, prjQuotation);

            if (errorCount > 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return dataMap.size() - errorCount;
            }

        } catch (Exception e) {
            e.printStackTrace();
            for (Map<String, String> m : dataMap) {
                impDataService.updateErrMessage(m, e.getClass() + ":" + e.getMessage());
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
        return dataMap.size() - errorCount;
    }

    @Override
    public void saveLeaseChargeCashflow(IRequest iRequest, List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowLists) throws HlsCusException {
        if (hlsCusPrjQuotationCashflowLists.size() > 0) {
            Long quotationId = 0L;
            for (HlsCusPrjQuotationCashflow tempDto : hlsCusPrjQuotationCashflowLists) {
                if (tempDto.getQuotationId() != null) {
                    quotationId = tempDto.getQuotationId();
                }
            }
            if (quotationId > 0) {
                for (HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow : hlsCusPrjQuotationCashflowLists) {
                    if (hlsCusPrjQuotationCashflow.getQuotationCashflowId() == null || hlsCusPrjQuotationCashflow.getQuotationCashflowId() == 0) {
                        hlsCusPrjQuotationCashflow.set__status(DTOStatus.ADD);
                        hlsCusPrjQuotationCashflow.setQuotationId(quotationId);
                        self().insertSelective(iRequest, hlsCusPrjQuotationCashflow);
                    } else {
                        hlsCusPrjQuotationCashflow.set__status(DTOStatus.UPDATE);
                        self().updateByPrimaryKeySelective(iRequest, hlsCusPrjQuotationCashflow);
                    }

                }
                HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
                prjQuotation.setQuotationId(quotationId);
                prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, prjQuotation);
                //拆税
                taxCashflowDemolition(iRequest, prjQuotation);
            }

        }
    }

    private final static String SHEET_NAME = "sheet1";
    private final static String FILE_NAME = "还款计划";
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private static final List<String> colNameList = Lists.newArrayList(
            "期数", "现金流项目", "现金流方向", "计算日期", "支付日期", "金额(元)", "本金(元)", "利息(元)", "剩余本金(元)");
    private static final List<String> colGetMethods = Lists.newArrayList(
            "times", "cfItemDesc", "cfDirection", "calcDateExport", "dueDateExport", "dueAmount", "principal", "interest", "outstandingPrincipal");

    @Override
    public void exportPrjQuotationCashflow(HttpServletRequest request, HttpServletResponse response, HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow) throws IOException, InvocationTargetException, IllegalAccessException {
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet(SHEET_NAME);
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflows = prjQuotationCashflowMapper.prjQuotationCashflowExport(hlsCusPrjQuotationCashflow);
        int num = 1;
        for (HlsCusPrjQuotationCashflow cashflow : prjQuotationCashflows) {
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            setData(xwork, sheet, row, cashflow);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, FILE_NAME);
    }

    private final static String CF_FILE_NAME = "现金流";
    private static final List<String> cfNameList = Lists.newArrayList(
            "支付日期", "计算日期", "收款(元)", "本金(元)", "利息(元)", "咨询服务费(元)", "保证金(元)", "本金投放(元)", "现金流(元)");
    private static final List<String> cfGetMethods = Lists.newArrayList(
            "calcDate", "dueDate", "theReceipt", "thePrincipal", "theInterest", "theConsultingServiceFee", "theMargin", "theDeposit", "theCashFlow");

    @Override
    public void exportPrjCashFlow(HttpServletRequest request, HttpServletResponse response, HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow) throws IOException, InvocationTargetException, IllegalAccessException {
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet(SHEET_NAME);
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, cfNameList);
        List<HlsCusPrjQuotationCashflow> fctQuotationCashflows = prjQuotationCashflowMapper.prjQueryCashFlow(hlsCusPrjQuotationCashflow);
        for (HlsCusPrjQuotationCashflow cashflow : fctQuotationCashflows) {
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            setCFData(xwork, sheet, row, cashflow);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, CF_FILE_NAME);
    }

    /**
     * 把dto中的数据设置到Excel行中
     *
     * @param row
     */
    private void setData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow) throws InvocationTargetException, IllegalAccessException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(hlsCusPrjQuotationCashflow, colGetMethods.get(i));
            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }
            if (value instanceof Double) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value = df.format(value);
                cell.setCellStyle(cellStyle);
            }

            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                cell.setCellValue(value.toString());
            }

        }
    }

    private void setCFData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow) throws InvocationTargetException, IllegalAccessException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (int i = 0; i < cfGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(hlsCusPrjQuotationCashflow, cfGetMethods.get(i));
            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }
            if (value instanceof Double) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value = df.format(value);
                cell.setCellStyle(cellStyle);
            }

            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                if ("0.00".equals(value.toString())) {
                    cell.setCellValue("");
                } else {
                    cell.setCellValue(value.toString());
                }
            }

        }
    }

    @Override
    public void calcNewCashflow(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws IllegalArgumentException, HlsCusException {
        HlsCusPrjQuotation prjQuotation = hlsCusPrjProjectInfo.getHlsCusPrjQuotation();
        prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, prjQuotation);
//        saveLeaseChargeCashflow(iRequest, hlsCusPrjProjectInfo.getHlsCusPrjQuotationChargeCashflowList());
        long quotationId= hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getQuotationId();
        HlsCusPrjQuotationCashflow hcc = new  HlsCusPrjQuotationCashflow();
        hcc.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> cashflowList = this.select(iRequest,hcc,1,10000);
        recalcNewCashflow(iRequest, cashflowList,prjQuotation);
    }

    private void recalcNewCashflow(IRequest iRequest, List<HlsCusPrjQuotationCashflow> cashflows, HlsCusPrjQuotation prjQuotation){
            double intRate = prjQuotation.getIntRate();
            Date leaseStartDate = prjQuotation.getLeaseStartDate();
            long calcDays   = 0l;
            double currentInterest =0d;
            double balancePri =0d;
            cashflows= cashflows.stream().filter(a->a.getCfItem()==1).collect(Collectors.toList());
            cashflows=cashflows.stream().sorted(Comparator.comparing(HlsCusPrjQuotationCashflow::getDueDate)).collect(Collectors.toList());
        for(int i=0;i<cashflows.size();i++){
            HlsCusPrjQuotationCashflow cashflow=cashflows.get(i);
                if(i==0){
                    calcDays= DateUtils.between(leaseStartDate,cashflow.getDueDate());
                        balancePri=prjQuotation.getBalancePri()+prjQuotation.getTotlePri();
                }else{
                    balancePri=balancePri-cashflow.getPrincipal();
                }
                currentInterest=doubleFormat2(balancePri*intRate*calcDays/360);
                cashflow.setInterest(currentInterest);
                cashflow.setTimes((long) i+1);
                cashflow.setDueAmount(currentInterest+cashflow.getPrincipal());
                cashflow.setOutstandingPrincipal(balancePri);
                this.updateByPrimaryKey(iRequest,cashflow);
            }
    }


    public double  doubleFormat2(double value) {

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

        @Override
    public void calcIrrAndXirr(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws IllegalArgumentException, HlsCusException {
        HlsCusPrjQuotation prjQuotation = hlsCusPrjProjectInfo.getHlsCusPrjQuotation();
        if (prjQuotation.getQuotationId() == null || prjQuotation.getQuotationId() == 0L) {
            throw new IllegalArgumentException("未取到报价信息");
        }
        prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, prjQuotation);
        taxCashflowDemolition(iRequest, prjQuotation);

        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowLists = self().select(iRequest, hlsCusPrjQuotationCashflow, 1, 999999);
        if (hlsCusPrjQuotationCashflowLists.size() > 0) {
            hlsCusPrjQuotationService.updateXirr(iRequest, prjQuotation);
        } else {
            throw new HlsCusException("未取到现金流信息");
        }
    }
}
