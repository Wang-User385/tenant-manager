package com.hand.hls.fin.service.impl;


import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.Code;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusWithdrawRepayment;
import com.hand.hls.abs.mapper.HlsCusWithdrawRepaymentMapper;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqLnService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fin.dto.*;
import com.hand.hls.fin.exception.HlsCusAmountOverException;
import com.hand.hls.fin.mapper.*;
import com.hand.hls.fin.service.*;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.mapper.FinancialAttributeMapper;
import com.hand.hls.gld.mapper.HlsCusLonContractRepaymentMergeMapper;
import com.hand.hls.gld.mapper.PeriodMapper;
import com.hand.hls.gld.mapper.SetOfBooksMapper;
import com.hand.hls.gld.service.GldLonContractFinCostService;
import com.hand.hls.gld.service.IHlsCusLonContractRepaymentMergeService;
import com.hand.hls.gld.utils.IrrUtil;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.DateUtil.HlsCusEndOfMonth;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.HlsCusXirr;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractWithdrawServiceImpl extends BaseServiceImpl<HlsCusLonContractWithdraw> implements HlsCusLonContractWithdrawService {
    public static final String WEEK = "WEEK";
    public static final String MONTH = "MONTH";
    public static final String QUARTER = "QUARTER";
    //初始变量
    public static final Double INIT_ZERO = 0D;
    public static final Long YEAR_MONTH = 12L;
    @Autowired
    private final static Logger logger = LoggerFactory.getLogger(HlsCusLonContractWithdrawService.class);

    @Autowired
    private HlsCusLonContractWithdrawMapper lonContractWithdrawMapper;

    @Autowired
    private HlsCusLonContractRepaymentMapper hlsCusLonContractRepaymentMapper;

    @Autowired
    private HlsCusLonContractWithdrawPlanService hlsCusLonContractWithdrawPlanService;

    @Autowired
    private HlsCusLonContractRepaymentPlanService hlsCusLonContractRepaymentPlanService;

    @Autowired
    private HlsCusLonContractRepaymentService hlsCusLonContractRepaymentService;

    @Autowired
    private HlsCusLonContractPurposeService hlsCusLonContractPurposeService;

    @Autowired
    private HlsCusCtLonContractOtherPurposeService hlsCusCtLonContractOtherPurposeService;

    @Autowired
    private HlsCusLonContractQuotationService hlsCusLonContractQuotationService;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private GldLonContractFinCostService gldLonContractFinCostService;

    @Autowired
    private HlsCusLonContractService hlsCusLonContractService;

    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private HlsCusCtLonBankAccountService hlsCusCtLonBankAccountService;

    @Autowired
    private HlsCusConFloatingRateReqLnService conFloatingRateReqLnService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusCtLonContractBankAccountService hlsCusCtLonContractBankAccountService;

    @Autowired
    private HlsCreditLineService hlsCreditLineService;
//    @Autowired
//    private HlsCusICreditContractService hlsCusICreditContractService;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private HlsCusCreditContractLineMapper hlsCusCreditContractLineMapper;

    @Autowired
    private HlsCusLonContractQuotationMapper hlsCusLonContractQuotationMapper;

    @Autowired
    private HlsSysFileService hlsSysFileService;

    @Autowired
    private HlsCusCtLonContractBankAccountService ctLonContractBankAccountService;

    @Autowired
    private ICodeService iCodeService;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    private IFndAttachmentMultiService iFndAttachmentMultiService;

    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private IFndAttachmentService iFndAttachmentService;
    @Autowired
    private HlsCusLonContractAttachmentMapper hlsCusLonContractAttachmentMapper;
    @Autowired
    private HlsCusCtLonContractBankAccountMapper ctLonContractBankAccountMapper;
    //    @Autowired
//    private IHlsCusWithdrawRepaymentService iHlsCusWithdrawRepaymentService;
    @Autowired
    private HlsCusWithdrawRepaymentMapper hlsCusWithdrawRepaymentMapper;
    @Autowired
    private PeriodMapper periodMapper;
    private FinancialAttributeMapper financialAttributeMapper;
    @Autowired
    private SetOfBooksMapper setOfBooksMapper;
    private Calendar calendar = Calendar.getInstance();//获取一个日历的实例

    @Autowired
    HlsCusILonContractAttachmentService hlsCusILonContractAttachmentService;
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;
    @Autowired
    private HlsCusLonContractRepaymentMergeMapper lonContractRepaymentMergeMapper;

    @Autowired
    private IHlsCusLonContractRepaymentMergeService lonContractRepaymentMergeService;

    private static final String BALANCE = "BALANCE";

    @Override
    public List<Map<String, Object>> queryReport(IRequest request, Map<String, Object> map) {
        PageHelper.startPage((int) map.get("page"), (int) map.get("pageSize"));
        return lonContractWithdrawMapper.queryReport(map);
    }

    /**
     * sss
     * 根据条件,获取对应的时间戳
     *
     * @param timeCondition
     * @return
     */
    private static Date getDateByCondition(String timeCondition) {
        if (!StringUtils.isEmpty(timeCondition)) {
            LocalDateTime now = LocalDateTime.now();
            ZoneId zoneId = ZoneId.systemDefault();
            String condition = timeCondition.toUpperCase();
            Date date = null;
            switch (condition) {
                case WEEK:
                    date = Date.from(now.plusDays(-7).atZone(zoneId).toInstant());
                    break;
                case MONTH:
                    date = Date.from(now.plusMonths(-1).atZone(zoneId).toInstant());
                    break;
                case QUARTER:
                    date = Date.from(now.plusMonths(-3).atZone(zoneId).toInstant());
                    break;
                default:
                    break;
            }
            return date;
        }
        return null;
    }

    @Override
    public List<HlsCusLonContractWithdraw> queryContractWithdraw(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize) {
        lonContractWithdraw.setCompanyId(request.getCompanyId());
        String timeCondition = lonContractWithdraw.getTimeCondition();
        Date dateByCondition = getDateByCondition(timeCondition);
        lonContractWithdraw.setConditionDate(dateByCondition);
        PageHelper.startPage(page, pageSize);
        return lonContractWithdrawMapper.selectContractWithdraw(lonContractWithdraw);
    }

    @Override
    public HlsCusLonContractWithdraw lonContractWithdrawFormData(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, HttpSession session) {
        Long companyId = request.getCompanyId();
        lonContractWithdraw.setCompanyId(companyId);
        List<HlsCusLonContractWithdraw> hlsCusLonContractWithdrawList = lonContractWithdrawMapper.lonContractWithdrawFormData(lonContractWithdraw);
        HlsCusLonContractWithdraw result = new HlsCusLonContractWithdraw();
        if (hlsCusLonContractWithdrawList.size() > 0) {
            result = hlsCusLonContractWithdrawList.get(0);
        }
        return result;
    }

    @Override
    public HlsCusLonContractWithdraw lonContractWithdrawCreate(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, HttpSession session) throws HlsCusException {
        Long contractId = lonContractWithdraw.getContractId();
        Long companyId = request.getCompanyId();

        HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
        hlsCusLonContract.setContractId(contractId);
        hlsCusLonContract = hlsCusLonContractService.selectByPrimaryKey(request, hlsCusLonContract);

//        if (hlsCusLonContract.getCreditContractId() != null) {
//            HlsCusCreditContract hlsCusCreditContract = new HlsCusCreditContract();
//            hlsCusCreditContract.setCreditContractId(hlsCusLonContract.getCreditContractId());
//            hlsCusCreditContract = hlsCusICreditContractService.selectByPrimaryKey(request, hlsCusCreditContract);
//            if ("PENDING".equalsIgnoreCase(hlsCusCreditContract.getCreditContractStatus())) {
//                throw new HlsCusException("融资合同关联的授信合同在变更中，故不能创建提款！");
//            }
//        }

        //暂时注释，为了流程走下去
       /* if (hlsCusLonContract.getCreditLineId() != null) {
            //分项额度可用额度
            Double creditAmt = lonContractWithdrawMapper.selectCreditDueAmount(hlsCusLonContract.getCreditLineId());
            if (creditAmt == null) {
                creditAmt = 0D;
            }
            Double amt = hlsCusCreditContractLineMapper.selectForecastCreditAmt(null, hlsCusLonContract.getCreditLineId());
            DecimalFormat df = new DecimalFormat("###,##0.00");
            if (new BigDecimal(creditAmt).compareTo(new BigDecimal(0)) != 1) {
                throw new HlsCusException("实际提款金额不可超出授信实际可用金额！其中预提款金额：" + df.format(amt) + "(元)");
            }
        }*/


        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();

        /**
         * 此方式删除了提款不会因此受到影响
         */
        String maxNum = lonContractWithdrawMapper.selectWithdrawNumberMax(contractId);
        String withdrawPlanNumber = hlsCusLonContract.getContractNumber() + "-" + maxNum;

        hlsCusLonContractWithdraw.set__status("add");
        hlsCusLonContractWithdraw.setContractId(contractId);
        hlsCusLonContractWithdraw.setLoanBpId(hlsCusLonContract.getCreditBpId());
        hlsCusLonContractWithdraw.setCompanyId(companyId);
        hlsCusLonContractWithdraw.setDocumentType("STD");
        hlsCusLonContractWithdraw.setDocumentCategory("LON_CONTRACT_WITHDRAW");
        hlsCusLonContractWithdraw.setBusinessType("LON_CONTRACT_WITHDRAW");
        hlsCusLonContractWithdraw.setWithdrawNumber(withdrawPlanNumber);
        hlsCusLonContractWithdraw.setWithdrawStatus("NEW");
        hlsCusLonContractWithdraw.setCfType(60L);
        hlsCusLonContractWithdraw.setCfItem(60L);
        hlsCusLonContractWithdraw.setCfDirection("INFLOW");
        hlsCusLonContractWithdraw.setCfStatus("RELEASE");
        hlsCusLonContractWithdraw.setWriteOffFlag("NOT");
        hlsCusLonContractWithdraw.setDataClass("NORMAL");
        hlsCusLonContractWithdraw.setWithdrawCurrencyCode(hlsCusLonContract.getCurrency());
        hlsCusLonContractWithdraw = hlsCusLonContractWithdrawService.insertSelective(request, hlsCusLonContractWithdraw);
        hlsCusLonContractWithdraw.setRefD01(hlsCusLonContract.getFinancingTermTo());
        hlsCusLonContractWithdraw.getRefD01();
        return hlsCusLonContractWithdraw;
    }

    @Override
    public HlsCusLonContractWithdraw save(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw) throws HlsCusAmountOverException, HlsCusException {
        lonContractWithdraw.setChangeTime(lonContractWithdraw.getChangeTime());
        lonContractWithdraw.setChangeReason(lonContractWithdraw.getChangeReason());
        hlsCusLonContractWithdrawService.updateByPrimaryKeySelective(request, lonContractWithdraw);
        //变更到期是否自动核销状态
        hlsCusLonContractWithdrawService.updateAutoWriteByWithdrawId(lonContractWithdraw);

        //保存合同信息-提款账户ID
        HlsCusLonContract lonContract = new HlsCusLonContract();
        lonContract.setContractId(lonContractWithdraw.getContractId());
        lonContract = hlsCusLonContractService.selectByPrimaryKey(request, lonContract);

        //根据规则生成报表头
        if ("NEW".equals(lonContractWithdraw.getWithdrawStatus())) {
            StringBuilder stringBuilder = new StringBuilder("");
            if (StringUtils.isEmpty(lonContractWithdraw.getExtraNam())) {
                stringBuilder.append(lonContractWithdraw.getLoanBpName());
            } else {
                stringBuilder.append(lonContractWithdraw.getExtraNam());
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            BigDecimal decimal = new BigDecimal(lonContractWithdraw.getDueAmount().toString()).divide(new BigDecimal(10000));
        }

        if (lonContract.getCreditLineId() != null) {

            //分项额度可用额度
            Double creditAmt = lonContractWithdrawMapper.selectCreditDueAmount(lonContract.getCreditLineId());
            if (creditAmt == null) {
                creditAmt = 0D;
            }
            Double amt = hlsCusCreditContractLineMapper.selectForecastCreditAmt(null, lonContract.getCreditLineId());
            DecimalFormat df = new DecimalFormat("###,##0.00");
            if (new BigDecimal(creditAmt.toString()).compareTo(new BigDecimal(0)) == -1) {
                throw new HlsCusAmountOverException("实际提款金额超出授信实际可用金额！其中预提款金额(包括当前):" + df.format(amt) + "(元)");
            }
        }

        //提款金额不能大于合同金额
        if(lonContractWithdraw.getDueAmount().compareTo(lonContract.getFinanceAmount()) > 0 ){
            throw new HlsCusAmountOverException("提款金额不能大于合同金额!");
        }
        //提款日不能在 合同执行期 前
        if(lonContractWithdraw.getDueDate().compareTo(lonContract.getFinancingTermFrom()) < 0 ){
            throw new HlsCusAmountOverException("提款日不能早于合同执行期!");
        }

        HlsCusLonContractQuotation lonContractQuotation = new HlsCusLonContractQuotation();
        lonContractQuotation.setWithdrawId(lonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractQuotation> quotations = hlsCusLonContractQuotationMapper.lonContractQuotationById(lonContractQuotation);

        if (quotations.size() > 0) {
            lonContractWithdraw.setQuotationId(quotations.get(0).getQuotationId());
        } else {
            lonContractWithdraw.setQuotationId(null);
        }

        lonContractQuotation.setQuotationId(lonContractWithdraw.getQuotationId());
        lonContractQuotation.setIntRate(lonContractWithdraw.getIntRate());
        lonContractQuotation.setPlanInterestPaymentDate(lonContractWithdraw.getPlanInterestPaymentDate());
        lonContractQuotation.setPlanPrincipalPaymentDate(lonContractWithdraw.getPlanPrincipalPaymentDate());

        lonContractQuotation.setContractId(lonContractWithdraw.getContractId());
        lonContractQuotation.setBaseRate(lonContractWithdraw.getBaseRate());
        lonContractQuotation.setBaseRateType(lonContractWithdraw.getBaseRateType());
        lonContractQuotation.setCalcInterestYearDays(lonContractWithdraw.getCalcInterestYearDays());
        lonContractQuotation.setComperhensiveFinancingCost(lonContractWithdraw.getComperhensiveFinancingCost());
        lonContractQuotation.setXirr(lonContractWithdraw.getXirr());
        lonContractQuotation.setDescription(lonContractWithdraw.getDescription());
        lonContractQuotation.setExchangeRate(lonContractWithdraw.getExchangeRate());
        lonContractQuotation.setFirstRate(lonContractWithdraw.getFirstRate());
        lonContractQuotation.setFloatingRangeMethod(lonContractWithdraw.getFloatingRangeMethod());
        lonContractQuotation.setFloatingWay(lonContractWithdraw.getFloatingWay());
        lonContractQuotation.setFloatingWayRange(lonContractWithdraw.getFloatingWayRange());
        lonContractQuotation.setIntRateType(lonContractWithdraw.getIntRateType());
        lonContractQuotation.setInterestCalcDate(lonContractWithdraw.getInterestCalcDate());
        lonContractQuotation.setInterestCalcMethod(lonContractWithdraw.getInterestCalcMethod());
        lonContractQuotation.setInterestCycle(lonContractWithdraw.getInterestCycle());
        lonContractQuotation.setInterestPaymentDate(lonContractWithdraw.getInterestPaymentDate());
        lonContractQuotation.setLoanRate(lonContractWithdraw.getLoanRate());
        lonContractQuotation.setLoanTerm(lonContractWithdraw.getLoanTerm());
        lonContractQuotation.setLoanTimes(lonContractWithdraw.getLoanTimes());
        lonContractQuotation.setPrincipalCycle(lonContractWithdraw.getPrincipalCycle());
        lonContractQuotation.setPrincipalPaymentDate(lonContractWithdraw.getPrincipalPaymentDate());
        lonContractQuotation.setInterestMonth(lonContractWithdraw.getInterestMonth());
        lonContractQuotation.setServeAmount(lonContractWithdraw.getServeAmount());
        lonContractQuotation.setServeCurrencyCode(lonContractWithdraw.getServeCurrencyCode());
        lonContractQuotation.setServeFlag(lonContractWithdraw.getServeFlag());
        lonContractQuotation.setServeMethod(lonContractWithdraw.getServeMethod());
        lonContractQuotation.setServeRate(lonContractWithdraw.getServeRate());
        lonContractQuotation.setTaxRate(lonContractWithdraw.getTaxRate());
        lonContractQuotation.setCreatedBy(request.getUserId());
        lonContractQuotation.setLastUpdatedBy(request.getUserId());
        lonContractQuotation.setBaseRateChangeDate(lonContractWithdraw.getBaseRateChangeDate());
        lonContractQuotation.setChangeTerm(lonContractWithdraw.getChangeTerm());
        lonContractQuotation.setRateChangeDate(lonContractWithdraw.getRateChangeDate());
        //设置测算类型，折算币种，折算期初本金金额(元)，折算应付本金总金额（元），折算利息总金额（元） update:2019-3-1
        lonContractQuotation.setMeasureType(lonContractWithdraw.getMeasureType());
        lonContractQuotation.setConvertCurrency(lonContractWithdraw.getConvertCurrency());
        lonContractQuotation.setConvertBeginPrincipalAmount(lonContractWithdraw.getConvertBeginPrincipalAmount());

        lonContractQuotation.setFloatingRangeMethodRemark(lonContractWithdraw.getFloatingRangeMethodRemark());
        lonContractQuotation.setPriceList(lonContractWithdraw.getPriceList());
        lonContractQuotation.setStartActiveDate(lonContractWithdraw.getDueDate());
        lonContractQuotation.setEndActiveDate(lonContractWithdraw.getWithdrawEndDate());
        lonContractQuotation.setCurrency(lonContractWithdraw.getCurrency());
        lonContractQuotation.setLoanAmount(lonContractWithdraw.getDueAmount());
        //更新完毕
        if (lonContractWithdraw.getQuotationId() != null) {
            hlsCusLonContractQuotationService.updateByPrimaryKey(request, lonContractQuotation);
        } else {
            hlsCusLonContractQuotationService.insertSelective(request, lonContractQuotation);
        }

        //获取授信机构
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(lonContract.getCreditBpId());
        hlsCusBpMaster = hlsCusBpMasterService.selectByPrimaryKey(request, hlsCusBpMaster);

        HlsCusCtLonBankAccount hlsCusCtLonBankAccount = new HlsCusCtLonBankAccount();
        hlsCusCtLonBankAccount.setBpId(hlsCusBpMaster.getBpId());
        List<HlsCusCtLonBankAccount> ctLonBankAccountList = hlsCusCtLonBankAccountService.select(request, hlsCusCtLonBankAccount, 1, 999999);

        //保存提款项下还款计划
        if (lonContractWithdraw.getHlsCusLonContractRepaymentList() != null) {
            for (HlsCusLonContractRepayment hlsCusLonContractRepayment : lonContractWithdraw.getHlsCusLonContractRepaymentList()) {
                if (hlsCusLonContractRepayment.getRepaymentId() != null) {
                    hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(request, hlsCusLonContractRepayment);
                } else {
                    hlsCusLonContractRepayment.set__status("add");
                    hlsCusLonContractRepayment.setCfType(70L);
                    hlsCusLonContractRepayment.setCfDirection("OUTFLOW");
                    hlsCusLonContractRepayment.setCfStatus("RELEASE");
                    hlsCusLonContractRepayment.setWriteOffFlag("NOT");
                    hlsCusLonContractRepayment.setPlannedCalcDate(hlsCusLonContractRepayment.getPlannedDueDate());
                    hlsCusLonContractRepayment.setReceivedBpName(hlsCusBpMaster.getBpName());
                    hlsCusLonContractRepayment.setDueAmount(0D);
                    hlsCusLonContractRepayment.setCnyDueAmount(0D);
//                    if (ctLonBankAccountList.size() > 0) {
//                        hlsCusLonContractRepayment.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
//                        hlsCusLonContractRepayment.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
//                    }
                    hlsCusLonContractRepaymentService.insertSelective(request, hlsCusLonContractRepayment);
                }
            }
            //暂时注释
//            updateAllPrincipalOutStd(request, lonContractWithdraw, "N", null);
        }

        //保存提款项下付款计划
        if (lonContractWithdraw.getHlsCusLonContractPaymentList() != null) {

            for (HlsCusLonContractRepayment hlsCusLonContractPayment : lonContractWithdraw.getHlsCusLonContractPaymentList()) {
                if (hlsCusLonContractPayment.getRepaymentId() != null) {
                    hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(request, hlsCusLonContractPayment);
                } else {
                    hlsCusLonContractPayment.set__status("add");
                    hlsCusLonContractPayment.setCfType(70L);
                    hlsCusLonContractPayment.setCfDirection("OUTFLOW");
                    hlsCusLonContractPayment.setCfStatus("RELEASE");
                    hlsCusLonContractPayment.setWriteOffFlag("NOT");
                    hlsCusLonContractPayment.setPlannedCalcDate(hlsCusLonContractPayment.getPlannedDueDate());
                    hlsCusLonContractPayment.setReceivedBpName(hlsCusBpMaster.getBpName());
                    hlsCusLonContractPayment.setDueAmount(0D);
                    hlsCusLonContractPayment.setCnyDueAmount(0D);
                    if (ctLonBankAccountList.size() > 0) {
                        hlsCusLonContractPayment.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
                        hlsCusLonContractPayment.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
                    }
                    hlsCusLonContractRepaymentService.insertSelective(request, hlsCusLonContractPayment);
                }
            }
        }

        if ("Y".equals(lonContractWithdraw.getServeFlag())) {
            //提款计划总金额
            Double amountSum = hlsCusLonContractRepaymentMapper.selectPlanAmountSum(lonContractWithdraw.getWithdrawId(), "N");
            if (amountSum > lonContractWithdraw.getServeAmount()) {
                throw new HlsCusAmountOverException("融资费用付款计划总金额超出限制");
            }
        }

        //保存账户信息
        if (lonContractWithdraw.getLonContractBankAccounts() != null) {
            for (HlsCusCtLonContractBankAccount lonContractBankAccount : lonContractWithdraw.getLonContractBankAccounts()) {
                lonContractBankAccount.setContractId(lonContractWithdraw.getContractId());
                lonContractBankAccount.setWithdrawId(lonContractWithdraw.getWithdrawId());
                if (lonContractBankAccount.getConBankAccountId() != null) {
                    ctLonContractBankAccountService.updateByPrimaryKeySelective(request, lonContractBankAccount);
                } else {
                    lonContractBankAccount.set__status("add");
                    HlsCusCtLonContractBankAccount hlsCusCtLonContractBankAccount = ctLonContractBankAccountService.insertSelective(request, lonContractBankAccount);
                    ctLonContractBankAccountService.updateSelective(request,hlsCusCtLonContractBankAccount);
                }
            }
        }

        if (lonContractWithdraw.getHlsCusPrjProjectAttachments() != null) {
            for (HlsCusPrjProjectAttachment attachment : lonContractWithdraw.getHlsCusPrjProjectAttachments()) {
                if (attachment.getProjectAttachmentId() != null) {
                    if (attachment.getDescription() == null) {
                        attachment.setDescription("");
                    }
                    if (attachment.getAttachmentCode() == null) {
                        attachment.setAttachmentCode("");
                    }
                    hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(request, attachment);

                    HlsCusSysFile sysFile = new HlsCusSysFile();
                    sysFile.setFileId(Long.parseLong(attachment.getFileId()));
                    sysFile.setFileName(attachment.getFileName());
                    hlsSysFileService.updateByPrimaryKeySelective(request, sysFile);
                } else {
                    hlsCusPrjProjectAttachmentService.insertSelective(request, attachment);
                }
            }

        }
        int attachCodeCount = hlsCusPrjProjectAttachmentService.selectAttachmentCodeNullCount(lonContractWithdraw.getWithdrawId(), "LON_CONTRACT_WITHDRAW");
        if (attachCodeCount > 0) {
            throw new HlsCusAmountOverException("附件信息中附件编码不可以为空");
        }

        if (lonContractWithdraw.getLonContractAttachments() != null) {
            for (HlsCusLonContractAttachment attachment : lonContractWithdraw.getLonContractAttachments()) {
                if (attachment.getContractAttachmentId() != null) {
                    hlsCusILonContractAttachmentService.updateByPrimaryKeySelective(request, attachment);

                    HlsCusSysFile sysFile = new HlsCusSysFile();
                    sysFile.setFileId(attachment.getFileId());
                    sysFile.setFileName(attachment.getFileName());
                    hlsSysFileService.updateByPrimaryKeySelective(request, sysFile);
                } else {
                    hlsCusILonContractAttachmentService.insertSelective(request, attachment);
                }
            }

        }


        //外币汇算
        HlsCusLonContractRepayment hlsCusLonContractRepayment1 = new HlsCusLonContractRepayment();
        hlsCusLonContractRepayment1.setCfItem(301L);
        hlsCusLonContractRepayment1.setWithdrawId(lonContractWithdraw.getWithdrawId());
        Double pricipalAmountSum = hlsCusLonContractRepaymentService.cfItemAmountSum(request, hlsCusLonContractRepayment1);
        hlsCusLonContractRepayment1.setCfItem(302L);
        Double interestAmountSum = hlsCusLonContractRepaymentService.cfItemAmountSum(request, hlsCusLonContractRepayment1);
        if (!"CNY".equalsIgnoreCase(lonContract.getCurrency())) {
            lonContractQuotation.setConvertPayPrincipalAmount(pricipalAmountSum);
            lonContractQuotation.setConvertPayInterestAmount(interestAmountSum);
        } else {
            lonContractQuotation.setConvertPayPrincipalAmount(null);
            lonContractQuotation.setConvertPayInterestAmount(null);
            lonContractQuotation.setConvertCurrency(null);
            lonContractQuotation.setConvertBeginPrincipalAmount(null);
            lonContractQuotation.setExchangeRate(null);
        }
        hlsCusLonContractQuotationService.updateByPrimaryKey(request, lonContractQuotation);
        return lonContractWithdraw;
    }

    /*
     * 杂项费用回写提款记录
     */
    public void updateOtherFeesSum(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, List<HlsCusLonContractRepayment> hlsCusLonContractRepaymentList) {
        Double sumChargeFee = 0D;
        Double sumConsultingFee = 0D;
        Double sumDeposit = 0D;
        Double sumConsignmentSalesFee = 0D;
        Double sumCollocationFee = 0D;
        Double sumManagementFee = 0D;
        Double sumOtherFee = 0D;
        for (HlsCusLonContractRepayment lonContractRepayment : hlsCusLonContractRepaymentList
        ) {
            if (lonContractRepayment.getCfItem().equals(303L)) {
                sumChargeFee = sumChargeFee + lonContractRepayment.getPlannedDueAmount();
            } else if (lonContractRepayment.getCfItem().equals(304L)) {
                sumConsultingFee = sumConsultingFee + lonContractRepayment.getPlannedDueAmount();
            } else if (lonContractRepayment.getCfItem().equals(305L)) {
                sumDeposit = sumDeposit + lonContractRepayment.getPlannedDueAmount();
            } else if (lonContractRepayment.getCfItem().equals(306L)) {
                sumConsignmentSalesFee = sumConsignmentSalesFee + lonContractRepayment.getPlannedDueAmount();
            } else if (lonContractRepayment.getCfItem().equals(307L)) {
                sumCollocationFee = sumCollocationFee + lonContractRepayment.getPlannedDueAmount();
            } else if (lonContractRepayment.getCfItem().equals(308L)) {
                sumManagementFee = sumManagementFee + lonContractRepayment.getPlannedDueAmount();
            } else if (lonContractRepayment.getCfItem().equals(309L)) {
                sumOtherFee = sumOtherFee + lonContractRepayment.getPlannedDueAmount();
            }
        }
        lonContractWithdraw.set__status(DTOStatus.UPDATE);
        lonContractWithdraw.setChargeFee(sumChargeFee);
        lonContractWithdraw.setConsultingFee(sumConsultingFee);
        lonContractWithdraw.setDeposit(sumDeposit);
        lonContractWithdraw.setConsignmentSalesFee(sumConsignmentSalesFee);
        lonContractWithdraw.setCollocationFee(sumCollocationFee);
        lonContractWithdraw.setManagementFee(sumManagementFee);
        lonContractWithdraw.setOtherFee(sumOtherFee);

        self().updateByPrimaryKeySelective(request, lonContractWithdraw);
    }

    /**
     * 融资提款-本息还款计划
     *
     * @param iRequest
     * @param hlsCusLonContractWithdraw
     * @throws HlsCusAmountOverException
     */
    @Override
    public void lonContractCalcRepayment(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw) throws Exception {
        //先保存数据
        self().save(iRequest, hlsCusLonContractWithdraw);

        hlsCusLonContractWithdraw = self().selectByPrimaryKey(iRequest, hlsCusLonContractWithdraw);

        //获取合同ID
        Long contractId = hlsCusLonContractWithdraw.getContractId();

        //获取报价信息
        HlsCusLonContractQuotation hlsCusLonContractQuotationTemp = new HlsCusLonContractQuotation();
        hlsCusLonContractQuotationTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());

        List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationMapper.lonContractQuotationById(hlsCusLonContractQuotationTemp);

        HlsCusLonContractQuotation hlsCusLonContractQuotation = hlsCusLonContractQuotations.get(0);
        Long quotationId = hlsCusLonContractQuotation.getQuotationId();

        if ("SYSTEM_ESTIMATION".equals(hlsCusLonContractQuotation.getMeasureType())) {
            //系统测算生成本金计划，系统测算不需要生成本金计划
            calcPrincipalCf(iRequest, hlsCusLonContractQuotation, hlsCusLonContractWithdraw);
        } else {
            //获取该笔提款计划项下所有还息计划
            HlsCusLonContractRepayment hlsCusLonContractInterestTemp = new HlsCusLonContractRepayment();
            hlsCusLonContractInterestTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            hlsCusLonContractInterestTemp.setCfItem(302L);
            List<HlsCusLonContractRepayment> hlsCusLonContractInterests = hlsCusLonContractRepaymentMapper.select(hlsCusLonContractInterestTemp);
            if (hlsCusLonContractInterests.size() > 0) {
                throw new HlsCusException("请先删除还款利息再进行计算！");
            }

        }

        //获取该笔提款计划项下所有还款计划
        HlsCusLonContractRepayment hlsCusLonContractRepaymentTemp = new HlsCusLonContractRepayment();
        hlsCusLonContractRepaymentTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        hlsCusLonContractRepaymentTemp.setCfItem(301L);
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        int principalCount = 0;
        Long principalCfItem = 301L;
        Date lastRepaymentPlanDate = hlsCusLonContractWithdraw.getWithdrawEndDate();
        //本金
        Double principalSum = 0D;

        //判断该笔提款计划项下是否存在还本计划
        for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {
            if (principalCfItem.equals(hlsCusLonContractRepayment.getCfItem())) {
                principalCount = principalCount + 1;
                lastRepaymentPlanDate = hlsCusLonContractRepayment.getPlannedDueDate();
                principalSum = principalSum + hlsCusLonContractRepayment.getPlannedDueAmount();
            }
        }

        //如果为剩余本金计算法，则按照还本计划计算，使用计息方式为剩余本金计息
        //无还本计划则在末期插入

        //取得所有还本日期
        Date[] repaymentPrincipalDate = new Date[principalCount];
        int j = 0;
        for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {
            if (principalCfItem.equals(hlsCusLonContractRepayment.getCfItem())) {
                repaymentPrincipalDate[j] = hlsCusLonContractRepayment.getPlannedDueDate();
                j = j + 1;
            }
        }

        //计算还本付息计划
        if ("SYSTEM_ESTIMATION".equals(hlsCusLonContractQuotation.getMeasureType())) {
            outStdPrincipalCalcRepayment(iRequest, hlsCusLonContractWithdraw, hlsCusLonContractQuotation, lastRepaymentPlanDate, principalCount, repaymentPrincipalDate);
        } else {
            handEstimation(iRequest, hlsCusLonContractWithdraw, hlsCusLonContractQuotation, hlsCusLonContractRepayments, repaymentPrincipalDate);
        }
        //刷新PLANNED_DUE_AMOUNT位数,保留2位
        HlsCusLonContractRepayment refreshRepaymentTemp = new HlsCusLonContractRepayment();
        refreshRepaymentTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractRepayment> refreshRepaymentList = hlsCusLonContractRepaymentMapper.select(refreshRepaymentTemp);
        for (HlsCusLonContractRepayment dt : refreshRepaymentList) {
            if (dt.getPlannedDueAmount() != null && dt.getPlannedDueAmount() != 0) {
                BigDecimal amountNew = new BigDecimal(dt.getPlannedDueAmount());
                dt.setPlannedDueAmount(amountNew.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            hlsCusLonContractRepaymentMapper.updateByPrimaryKeySelective(dt);
        }

        //查询还款本金和还款利息的金额之和
        HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
        hlsCusLonContractRepayment.setCfItem(301L);
        hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        Double pricipalAmountSum = hlsCusLonContractRepaymentService.cfItemAmountSum(iRequest, hlsCusLonContractRepayment);
        hlsCusLonContractRepayment.setCfItem(302L);
        Double interestAmountSum = hlsCusLonContractRepaymentService.cfItemAmountSum(iRequest, hlsCusLonContractRepayment);

        //如果当前的融资币种是【外币】将两个数据更新到表 lon_contract_quotation中
        HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
        hlsCusLonContract.setContractId(contractId);
        hlsCusLonContract = hlsCusLonContractService.selectByPrimaryKey(iRequest, hlsCusLonContract);
        if (!"CNY".equalsIgnoreCase(hlsCusLonContract.getCurrency())) {
            HlsCusLonContractQuotation contractQuotation = new HlsCusLonContractQuotation();
            contractQuotation.setQuotationId(quotationId);
            contractQuotation = hlsCusLonContractQuotationService.selectByPrimaryKey(iRequest, contractQuotation);
            contractQuotation.setConvertPayPrincipalAmount(pricipalAmountSum);
            contractQuotation.setConvertPayInterestAmount(interestAmountSum);
            contractQuotation = hlsCusLonContractQuotationService.updateByPrimaryKey(iRequest, contractQuotation);
        }

    }

    /**
     * 融资提款-变更本息还款计划
     *
     * @param iRequest
     * @param hlsCusLonContractWithdraw
     * @throws
     */
    @Override
    public void lonContractChangeCalcRepayment(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw) throws Exception {


        //先保存数据
/*
        self().save(iRequest, hlsCusLonContractWithdraw);
*/

/*
        hlsCusLonContractWithdraw = self().selectByPrimaryKey(iRequest, hlsCusLonContractWithdraw);
*/

        //获取合同ID
/*
        Long contractId = hlsCusLonContractWithdraw.getContractId();
*/

        //获取报价信息
        HlsCusLonContractQuotation hlsCusLonContractQuotationTemp = new HlsCusLonContractQuotation();
        hlsCusLonContractQuotationTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationMapper.lonContractQuotationById(hlsCusLonContractQuotationTemp);

        HlsCusLonContractQuotation hlsCusLonContractQuotation = hlsCusLonContractQuotations.get(0);

        calcInterestCf(iRequest, hlsCusLonContractQuotation, hlsCusLonContractWithdraw);


    }

    void calcInterestCf(IRequest iRequest, HlsCusLonContractQuotation hlsCusLonContractQuotation, HlsCusLonContractWithdraw hlsCusLonContractWithdraw) throws Exception {
        if (("EQUAL_PRINCIPAL".equals(hlsCusLonContractQuotation.getInterestCalcMethod()) || "ONCE_CLEAR".equals(hlsCusLonContractQuotation.getInterestCalcMethod())) && ("SYSTEM_ESTIMATION".equals(hlsCusLonContractQuotation.getMeasureType())) || ("AI").equals(hlsCusLonContractWithdraw.getChangeType())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date nextPlannedDueDate;
            String flag = "N";
            //开始调息期数
            Long times = hlsCusLonContractQuotation.getChangeTerm();
            //开始调息日期
            Date rateChangeDate = hlsCusLonContractQuotation.getRateChangeDate();
            Double day = Double.valueOf(hlsCusLonContractQuotation.getCalcInterestYearDays());
            //还款日期
            Date plannedDueDate;
            Double planDueAmount = 0D;
            Double outstandingPrincipal = 0D;
            Long oldWithdrawId = new Double(hlsCusLonContractWithdraw.getOldWithdrawId()).longValue();
            HlsCusLonContractQuotation contractQuotation = new HlsCusLonContractQuotation();
            contractQuotation.setWithdrawId(Long.valueOf(oldWithdrawId));
            List<HlsCusLonContractQuotation> contractQuotations = hlsCusLonContractQuotationMapper.lonContractQuotationById(contractQuotation);
            HlsCusLonContractRepayment hlsCusLonContractRepaymentTemp = new HlsCusLonContractRepayment();
            hlsCusLonContractRepaymentTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            if (times.equals(Long.valueOf(1))) {
                hlsCusLonContractRepaymentTemp.setCfItem(302L);
                hlsCusLonContractRepaymentTemp.setTimes(times);
                plannedDueDate = hlsCusLonContractWithdraw.getDueDate();
                outstandingPrincipal = hlsCusLonContractWithdraw.getDueAmount();
            } else {
                hlsCusLonContractRepaymentTemp.setCfItem(302L);
                hlsCusLonContractRepaymentTemp.setTimes(times);
                List<HlsCusLonContractRepayment> hlsCusLonContractRepayment1 = hlsCusLonContractRepaymentMapper.queryPlannedDueDateStart(hlsCusLonContractRepaymentTemp);
                plannedDueDate = hlsCusLonContractRepayment1.get(0).getPlannedDueDateStart();
                outstandingPrincipal = hlsCusLonContractRepaymentMapper.queryinterestAccrualBalance(hlsCusLonContractQuotation.getWithdrawId(), "302L", times);


            }


            List<HlsCusLonContractRepayment> hlsCusLonContractRepayments = hlsCusLonContractRepaymentMapper.selectRepaymentDetail(hlsCusLonContractRepaymentTemp);
            for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {

                nextPlannedDueDate = hlsCusLonContractRepayment.getPlannedDueDateEnd();

                if (rateChangeDate.getTime() > plannedDueDate.getTime() && rateChangeDate.getTime() < nextPlannedDueDate.getTime() && hlsCusLonContractRepayment.getTimes().equals(times)) {

                    Long beforeDay = (rateChangeDate.getTime() - plannedDueDate.getTime()) / (24 * 60 * 60 * 1000);
                    Long afterDay = (nextPlannedDueDate.getTime() - rateChangeDate.getTime()) / (24 * 60 * 60 * 1000);
                    /*System.out.println("-----------------3:"+beforeDay);
                    System.out.println("-----------------3:"+rateChangeDate.getTime());
                    System.out.println("-----------------3:"+plannedDueDate);
                    System.out.println("-----------------3:"+nextPlannedDueDate);*/

                    planDueAmount = (beforeDay * contractQuotations.get(0).getIntRate() * outstandingPrincipal + afterDay * hlsCusLonContractQuotation.getIntRate() * outstandingPrincipal) / day;
                    hlsCusLonContractRepayment.setPlannedDueAmount(planDueAmount);
                    hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, hlsCusLonContractRepayment);
                } else {
                    Long afterDay = (nextPlannedDueDate.getTime() - plannedDueDate.getTime()) / (24 * 60 * 60 * 1000);
                    planDueAmount = (afterDay * hlsCusLonContractQuotation.getIntRate() * outstandingPrincipal) / day;
                    hlsCusLonContractRepayment.setPlannedDueAmount(planDueAmount);
                    hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, hlsCusLonContractRepayment);

                }
                plannedDueDate = hlsCusLonContractRepayment.getPlannedDueDateEnd();
                outstandingPrincipal = hlsCusLonContractRepayment.getInterestAccrualBalance();

            }


        }


    }

    /**
     * 生成本金还款计划
     */
    void calcPrincipalCf(IRequest iRequest, HlsCusLonContractQuotation hlsCusLonContractQuotation, HlsCusLonContractWithdraw hlsCusLonContractWithdraw) throws Exception {
        //先删除本金然后重新生成
        HlsCusLonContractRepayment deleteRepaymentTemp = new HlsCusLonContractRepayment();
        deleteRepaymentTemp.setWithdrawId(hlsCusLonContractQuotation.getWithdrawId());
        deleteRepaymentTemp.setCfItem(301L);
        deleteRepaymentTemp.setIsWriteOffFlag("N");
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(deleteRepaymentTemp);

        //等额本金需要生成还款计划，一次性还本后面代码会生成一条还款计划
        if ("EQUAL_PRINCIPAL".equals(hlsCusLonContractQuotation.getInterestCalcMethod())) {
            //获取第1期的计算日期
            Date firstInterestCalcDate = null;
            String flag = "N";
            Long times = 1L;
            Double repaymentAmount;
            //实际提款日
            Date repaymentDate = hlsCusLonContractWithdraw.getDueDate();

            Date repaymentCalcDate = hlsCusLonContractWithdraw.getDueDate();
            Date nextRepaymentCalcDate;
            Date nextRepaymentDate;
            HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
            Double outstandingPrincipal = hlsCusLonContractWithdraw.getDueAmount();
            for (int i = 1; i <= hlsCusLonContractQuotation.getLoanTimes(); i++) {

                if (i == 1) {
                    nextRepaymentCalcDate = hlsCusLonContractWithdraw.getPlanPrincipalPaymentDate();
                    nextRepaymentDate = hlsCusLonContractWithdraw.getPlanPrincipalPaymentDate();
                } else {
                    nextRepaymentCalcDate = getNextRepaymentDateTmp(hlsCusLonContractWithdraw.getPlanPrincipalPaymentDate(), hlsCusLonContractQuotation.getInterestCycle(), flag, hlsCusLonContractQuotation.getInterestMonth(), times - 1);
                    nextRepaymentDate = getNextRepaymentDateTmp(hlsCusLonContractWithdraw.getDueDate(), hlsCusLonContractQuotation.getInterestCycle(), flag, hlsCusLonContractQuotation.getInterestMonth(), times - 1);

                }
                //如果firstInterestCalcDate第一期的计算日期已经存在，那么就按照第一期的计算日期为基准日期进行月末矫正
                if (!ObjectUtils.isEmpty(firstInterestCalcDate)) {
                    nextRepaymentCalcDate = HlsCusEndOfMonth.calcEndOfMonth(firstInterestCalcDate, nextRepaymentCalcDate);
                }
                //计息开始日
                Calendar cStart = Calendar.getInstance();
                //计息结束日
                Calendar cEnd = Calendar.getInstance();
                cStart.setTime(repaymentCalcDate);
                cEnd.setTime(nextRepaymentCalcDate);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                if (cStart.getTime() == cEnd.getTime() && times == 1) {
                    i = i - 1;
                    repaymentDate = nextRepaymentDate;
                    repaymentCalcDate = nextRepaymentCalcDate;
                    continue;
                }


                //最后一期
                if (Objects.equals(times, hlsCusLonContractQuotation.getLoanTimes())) {
                    nextRepaymentCalcDate = hlsCusLonContractWithdraw.getWithdrawEndDate();
                    nextRepaymentDate = nextRepaymentCalcDate;
                    cEnd.setTime(nextRepaymentCalcDate);

                }


                hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                hlsCusLonContractRepayment.setTimes(times);
                hlsCusLonContractRepayment.setCfType(70L);
                hlsCusLonContractRepayment.setCfItem(301L);
                hlsCusLonContractRepayment.setCfDirection("OUTFLOW");
                hlsCusLonContractRepayment.setCfStatus("RELEASE");
                //还款计算日
                hlsCusLonContractRepayment.setPlannedCalcDate(nextRepaymentCalcDate);
                hlsCusLonContractRepayment.setPlannedDueDate(nextRepaymentCalcDate);

                //如果是最后一期
                if (Objects.equals(times, hlsCusLonContractQuotation.getLoanTimes())) {
                    repaymentAmount = outstandingPrincipal;

                } else {
                    repaymentAmount = CalculateUtil.div(hlsCusLonContractWithdraw.getDueAmount(), hlsCusLonContractQuotation.getLoanTimes().doubleValue());
                    ;
                    outstandingPrincipal = CalculateUtil.sub(outstandingPrincipal, repaymentAmount);
                }
                hlsCusLonContractRepayment.setContractId(hlsCusLonContractWithdraw.getContractId());
                hlsCusLonContractRepayment.setPlannedDueAmount(repaymentAmount);
                hlsCusLonContractRepayment.setWriteOffFlag("NOT");
                hlsCusLonContractRepayment.setExchangeRate(1D);
                hlsCusLonContractRepayment.setIsSystemFlag("N");
                HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
                lonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                lonContractRepayment.setCfItem(301L);
                lonContractRepayment.setTimes(times);

                List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentService.select(iRequest, lonContractRepayment, 1, 999999);
                if (lonContractRepaymentList.size() == 0 && (hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= hlsCusLonContractWithdraw.getWithdrawEndDate().getTime()) && (hlsCusLonContractRepayment.getPlannedCalcDate().getTime() <= hlsCusLonContractWithdraw.getWithdrawEndDate().getTime())) {

                    hlsCusLonContractRepaymentService.insertSelective(iRequest, hlsCusLonContractRepayment);
                }
                times = times + 1;
                repaymentDate = nextRepaymentDate;
                repaymentCalcDate = nextRepaymentCalcDate;
            }
        }
    }

    /**
     * @Description:剩余本金计算还本付息计划，同时判断合同是否为一次性还本的还款方式
     * @Author: zhangyu
     * @Date: Created on 2018/5/14
     */
    public void outStdPrincipalCalcRepayment(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw, HlsCusLonContractQuotation hlsCusLonContractQuotation, Date lastRepaymentPlanDate, int principalCount, Date[] repaymentPrincipalDate) throws Exception {
        //获取第1期的计算日期
        Date firstInterestCalcDate = null;

        Long times = 1L;
        Long calcInterestDays;//计息天数
        Double repaymentAmount;
        //提款金额
        Double outStd = hlsCusLonContractWithdraw.getDueAmount();
        //实际提款日

        Date repaymentCalcDate = hlsCusLonContractWithdraw.getDueDate();
        Date nextRepaymentCalcDate;
        Date nextRepaymentDate;
        HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
        //获取融资合同
        HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
        hlsCusLonContract.setContractId(hlsCusLonContractWithdraw.getContractId());
        hlsCusLonContract = hlsCusLonContractService.selectByPrimaryKey(iRequest, hlsCusLonContract);

        //获取授信机构
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(hlsCusLonContract.getCreditBpId());
        hlsCusBpMaster = hlsCusBpMasterService.selectByPrimaryKey(iRequest, hlsCusBpMaster);

        HlsCusCtLonBankAccount hlsCusCtLonBankAccount = new HlsCusCtLonBankAccount();
        hlsCusCtLonBankAccount.setBpId(hlsCusBpMaster.getBpId());
        List<HlsCusCtLonBankAccount> ctLonBankAccountList = hlsCusCtLonBankAccountService.select(iRequest, hlsCusCtLonBankAccount, 1, 999999);
        //获取报价信息
        HlsCusLonContractQuotation hlsCusLonContractQuotationTemp = new HlsCusLonContractQuotation();
        hlsCusLonContractQuotationTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationService.select(iRequest, hlsCusLonContractQuotationTemp, 1, 9999);

        //1.删除所有未确认还息计划
        HlsCusLonContractRepayment deleteRepaymentTemp = new HlsCusLonContractRepayment();
        deleteRepaymentTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        deleteRepaymentTemp.setCfItem(302L);
        deleteRepaymentTemp.setIsWriteOffFlag("N");
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(deleteRepaymentTemp);

        //2.如果存在还本计划，则更新每一期还本计划的剩余本金
        if (principalCount > 0) {
            updateAllPrincipalOutStd(iRequest, hlsCusLonContractWithdraw, "N", null);
        }
        /**
         * add by tengfei,添加利随本清
         */
        HlsCusLonContractQuotation contractQuotation = new HlsCusLonContractQuotation();
        contractQuotation = hlsCusLonContractQuotations.get(0);
        if (!ObjectUtils.isEmpty(contractQuotation)) {
            //3.在规定还款期数内计算还款计划，在提款期数范围内循环插入
            String flag = "N";

            for (int i = 1; i <= hlsCusLonContractQuotations.get(0).getLoanTimes(); i++) {
                calcInterestDays = 0L;
//                if (i == hlsCusLonContractQuotations.get(0).getLoanTimes()) {
//                    flag = "Y";
//                }
                if (i == 1) {
                    flag = "Y";
                    nextRepaymentCalcDate = hlsCusLonContractWithdraw.getPlanInterestPaymentDate();
                    nextRepaymentDate = hlsCusLonContractWithdraw.getPlanInterestPaymentDate();
                } else {
                    nextRepaymentCalcDate = getNextRepaymentDateTmp(hlsCusLonContractWithdraw.getPlanInterestPaymentDate(), hlsCusLonContractQuotation.getInterestCycle(), flag, hlsCusLonContractQuotation.getInterestMonth(), times - 1);
                    nextRepaymentDate = getNextRepaymentDateTmp(hlsCusLonContractWithdraw.getPlanInterestPaymentDate(), hlsCusLonContractQuotation.getInterestCycle(), flag, hlsCusLonContractQuotation.getInterestMonth(), times - 1);
                }
                //如果firstInterestCalcDate第一期的计算日期已经存在，那么就按照第一期的计算日期为基准日期进行月末矫正
                if (!ObjectUtils.isEmpty(firstInterestCalcDate)) {
                    nextRepaymentCalcDate = HlsCusEndOfMonth.calcEndOfMonth(firstInterestCalcDate, nextRepaymentCalcDate);
                }
                //计息开始日
                Calendar cStart = Calendar.getInstance();
                //计息结束日
                Calendar cEnd = Calendar.getInstance();
                cStart.setTime(repaymentCalcDate);
                cEnd.setTime(nextRepaymentCalcDate);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                if (cStart.getTime() == cEnd.getTime() && times == 1) {

                    repaymentCalcDate = nextRepaymentCalcDate;
                    continue;
                }

                //最后一期
                if (Objects.equals(times, hlsCusLonContractQuotation.getLoanTimes())) {
                    nextRepaymentCalcDate = hlsCusLonContractWithdraw.getWithdrawEndDate();
                    nextRepaymentDate = nextRepaymentCalcDate;
                    cEnd.setTime(nextRepaymentCalcDate);

                }


                //计息天数
                calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
                calcInterestDays = calcInterestDays + daysAjust(hlsCusLonContractWithdraw.getDueDate(), hlsCusLonContractWithdraw.getWithdrawEndDate(),
                        repaymentPrincipalDate, repaymentCalcDate, nextRepaymentCalcDate, hlsCusLonContractWithdraw.getWithdrawId());
                //考虑到一个临界值，如果第一期的结息日期和起息日期一致，那么上述计算得到的值为0，那么给当前值+1，然后在总体计算完成之后，
                // 不需要去给第一期+1天
                if (times == 1 && calcInterestDays == 0) {
                    calcInterestDays = calcInterestDays + 1;
                }

                //还款利息，当期剩余本金
                /*if (hlsCusLonContractWithdraw.getRateChangeDate() != null && hlsCusLonContractWithdraw.getRateChangeAfter() != null
                        && hlsCusLonContractWithdraw.getCalcInterestYearDaysAfter() != null) {
                    //分段计算
                    if (repaymentCalcDate.getTime() <= hlsCusLonContractWithdraw.getRateChangeDate().getTime() && hlsCusLonContractWithdraw.getRateChangeDate().getTime() < nextRepaymentCalcDate.getTime()) {
                        Calendar cChangeStart = Calendar.getInstance();//计息开始日
                        Calendar cChangeEnd = Calendar.getInstance();//计息结束日
                        cChangeStart.setTime(repaymentCalcDate);
                        cChangeEnd.setTime(hlsCusLonContractWithdraw.getRateChangeDate());
                        if (1 == (cChangeEnd.getTimeInMillis() - cChangeStart.getTimeInMillis()) / (1000 * 3600 * 24)) {
                            //变更日正好是该期的第一天，算整期
                            repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate,
                                    hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(),
                                    hlsCusLonContractWithdraw.getRateChangeAfter() * 100,
                                    hlsCusLonContractWithdraw.getCalcInterestYearDaysAfter(), repaymentPrincipalDate);
                        } else {
                            //变更日期不是第一天，相减算头不算为，故变更日期往前调整一天在计算天数
                            cChangeEnd.add(Calendar.DAY_OF_YEAR, -1);
                            repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, cChangeEnd.getTime(), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), hlsCusLonContractWithdraw.getIntRate(), hlsCusLonContractWithdraw.getCalcInterestYearDays(), repaymentPrincipalDate);
                            repaymentAmount = repaymentAmount + getRepaymentAmount(iRequest, cChangeEnd.getTime(), nextRepaymentCalcDate, hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), hlsCusLonContractWithdraw.getRateChangeAfter() * 100, hlsCusLonContractWithdraw.getCalcInterestYearDaysAfter(), repaymentPrincipalDate);
                        }
                    } else if (repaymentCalcDate.getTime() >= hlsCusLonContractWithdraw.getRateChangeDate().getTime()) {
                        repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate, hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), hlsCusLonContractWithdraw.getRateChangeAfter() * 100, hlsCusLonContractWithdraw.getCalcInterestYearDaysAfter(), repaymentPrincipalDate);
                    } else {
                        repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate, hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), hlsCusLonContractWithdraw.getIntRate(), hlsCusLonContractWithdraw.getCalcInterestYearDays(), repaymentPrincipalDate);
                    }
                } else {
                    repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate, hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), hlsCusLonContractWithdraw.getIntRate(), hlsCusLonContractWithdraw.getCalcInterestYearDays(), repaymentPrincipalDate);
                }*/
                repaymentAmount = outStd * calcInterestDays * hlsCusLonContractWithdraw.getIntRate() / Integer.valueOf(hlsCusLonContractWithdraw.getCalcInterestYearDays()).intValue();

                outStd = getOutStd(nextRepaymentCalcDate, hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount());

                if (times == hlsCusLonContractQuotations.get(0).getLoanTimes()) {
                    outStd = 0D;
                }

                hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                hlsCusLonContractRepayment.setTimes(times);
                hlsCusLonContractRepayment.setCfType(70L);
                hlsCusLonContractRepayment.setCfItem(302L);
                hlsCusLonContractRepayment.setCfDirection("OUTFLOW");
                hlsCusLonContractRepayment.setCfStatus("RELEASE");
                //还款计算日
                hlsCusLonContractRepayment.setPlannedCalcDate(nextRepaymentCalcDate);
                hlsCusLonContractRepayment.setPlannedDueDate(nextRepaymentDate);

                hlsCusLonContractRepayment.setInterestPeriodDays(Double.valueOf(calcInterestDays));
                hlsCusLonContractRepayment.setContractId(hlsCusLonContractWithdraw.getContractId());
                hlsCusLonContractRepayment.setPlannedDueAmount(repaymentAmount);
                hlsCusLonContractRepayment.setInterestAccrualBalance(outStd);
                hlsCusLonContractRepayment.setWriteOffFlag("NOT");
                hlsCusLonContractRepayment.setReceivedBpName(hlsCusBpMaster.getBpName());
                hlsCusLonContractRepayment.setExchangeRate(1D);
                hlsCusLonContractRepayment.setIsSystemFlag("Y");
                if (ctLonBankAccountList.size() > 0) {
                    hlsCusLonContractRepayment.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
                    hlsCusLonContractRepayment.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
                }

                HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
                lonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                lonContractRepayment.setCfItem(302L);
                lonContractRepayment.setTimes(times);

                List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentService.select(iRequest, lonContractRepayment, 1, 999999);
                if (lonContractRepaymentList.size() == 0 && (hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= hlsCusLonContractWithdraw.getWithdrawEndDate().getTime()) && (hlsCusLonContractRepayment.getPlannedCalcDate().getTime() <= hlsCusLonContractWithdraw.getWithdrawEndDate().getTime())) {

                    hlsCusLonContractRepaymentService.insertSelective(iRequest, hlsCusLonContractRepayment);
                }
                List<HlsCusLonContractRepayment> repaymentListtest = hlsCusLonContractRepaymentMapper.selectRepaymentPlanIntByTimesOrderByRepaymentDate(hlsCusLonContractRepayment);

                times = times + 1;

                repaymentCalcDate = nextRepaymentCalcDate;
//}

            }
        }
        /* }*/

        //4.如果没有还本计划，则在末期插入还本计划
        if (principalCount == 0 || "ONCE_REPAYMENT".equalsIgnoreCase(hlsCusLonContractQuotation.getInterestCalcMethod())) {
            hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            //hlsCusLonContractRepayment.setTimes(times - 1);
            hlsCusLonContractRepayment.setTimes(hlsCusLonContractQuotation.getLoanTimes());
            hlsCusLonContractRepayment.setCfType(70L);
            hlsCusLonContractRepayment.setCfItem(301L);
            hlsCusLonContractRepayment.setCfDirection("OUTFLOW");
            hlsCusLonContractRepayment.setCfStatus("RELEASE");
            /*hlsCusLonContractRepayment.setPlannedDueDate(repaymentDate);
            hlsCusLonContractRepayment.setPlannedCalcDate(repaymentCalcDate);*/
            //updateBy:tengfei date:2019-1-21
            hlsCusLonContractRepayment.setPlannedDueDate(hlsCusLonContractWithdraw.getWithdrawEndDate());
            hlsCusLonContractRepayment.setPlannedCalcDate(hlsCusLonContractWithdraw.getWithdrawEndDate());
            hlsCusLonContractRepayment.setInterestPeriodDays((double) 0L);
            hlsCusLonContractRepayment.setContractId(hlsCusLonContractWithdraw.getContractId());
            hlsCusLonContractRepayment.setPlannedDueAmount(hlsCusLonContractWithdraw.getDueAmount());
            hlsCusLonContractRepayment.setInterestAccrualBalance(0D);
            hlsCusLonContractRepayment.setWriteOffFlag("NOT");
            hlsCusLonContractRepayment.setReceivedBpName(hlsCusBpMaster.getBpName());
            hlsCusLonContractRepayment.setExchangeRate(1D);
            if (ctLonBankAccountList.size() > 0) {
                hlsCusLonContractRepayment.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
                hlsCusLonContractRepayment.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
            }
            hlsCusLonContractRepaymentService.insertSelective(iRequest, hlsCusLonContractRepayment);

        }

        //还本计划处理剩余本金,同时刷新还款计划期数
//        updateAllPrincipalOutStd(iRequest, hlsCusLonContractWithdraw, "Y", repaymentPrincipalDate);

        //计算XIRR
        calXirr(iRequest, hlsCusLonContractWithdraw, hlsCusLonContractQuotation);


        //合并重复还款-利息的金额为一期
        HlsCusLonContractRepayment hlsCusLonContractRepayment3 = new HlsCusLonContractRepayment();
        hlsCusLonContractRepayment3.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        hlsCusLonContractRepayment3.setCfItem(302L);
        List<HlsCusLonContractRepayment> repaymentList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment3);

        for (int i = 0; i < repaymentList.size() - 1; i++) {
            for (int k = i + 1; k < repaymentList.size(); k++) {
                if (repaymentList.get(i).getPlannedDueDate().getTime() == repaymentList.get(k).getPlannedDueDate().getTime()) {
                    if (repaymentList.get(i).getPlannedCalcDate().getTime() > repaymentList.get(k).getPlannedCalcDate().getTime()) {
                        repaymentList.get(i).setInterestPeriodDays(repaymentList.get(k).getInterestPeriodDays());
                        //  repaymentList.get(i).setPlannedDueAmount(repaymentList.get(i).getPlannedDueAmount() + repaymentList.get(k).getPlannedDueAmount());
                        // repaymentList.get(i).setCnyDueAmount(repaymentList.get(k).getPlannedDueAmount() + repaymentList.get(i).getPlannedDueAmount());
                        hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, repaymentList.get(i));
                        hlsCusLonContractRepaymentService.deleteByPrimaryKey(repaymentList.get(k));
                    } else {
                        repaymentList.get(k).setInterestPeriodDays(repaymentList.get(k).getInterestPeriodDays());
//                        if (repaymentList.get(i).getPlannedDueDate().getTime() != hlsCusLonContractWithdraw.getWithdrawEndDate().getTime()) {
//                            repaymentList.get(k).setPlannedDueAmount(repaymentList.get(k).getPlannedDueAmount() + repaymentList.get(i).getPlannedDueAmount());
//                            repaymentList.get(k).setCnyDueAmount(repaymentList.get(k).getPlannedDueAmount() + repaymentList.get(i).getPlannedDueAmount());
//                        }

                        hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, repaymentList.get(k));
                        hlsCusLonContractRepaymentService.deleteByPrimaryKey(repaymentList.get(i));
                    }
                }

            }
            if (repaymentList.get(i).getPlannedDueAmount() <= 0L) {
                hlsCusLonContractRepaymentService.deleteByPrimaryKey(repaymentList.get(i));
            }
        }

        HlsCusLonContractRepayment t1 = new HlsCusLonContractRepayment();
        t1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        t1.setCfItem(302L);
        List<HlsCusLonContractRepayment> dd = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(t1);

//处理数据
        HlsCusLonContractRepayment hlsCusLonContractRepaymentDeal1 = new HlsCusLonContractRepayment();
        hlsCusLonContractRepaymentDeal1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        hlsCusLonContractRepaymentDeal1.setCfItem(302L);
        hlsCusLonContractRepaymentDeal1.setIsSystemFlag("Y");
        List<HlsCusLonContractRepayment> repaymentListDeal1 = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentDeal1);
//        Calendar dealStartDate = Calendar.getInstance();
//        Calendar dealEndDate = Calendar.getInstance();
        for (int i = 0; i < repaymentListDeal1.size(); i++) {
            HlsCusLonContractRepayment hlsCusLonContractRepaymentDeal2 = new HlsCusLonContractRepayment();
            hlsCusLonContractRepaymentDeal2.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            hlsCusLonContractRepaymentDeal2.setCfItem(302L);
            hlsCusLonContractRepaymentDeal2.setIsSystemFlag("N");

            Calendar dealStartDate = Calendar.getInstance();
            Calendar dealEndDate = Calendar.getInstance();
            if (i == 0) {
                dealStartDate.setTime(hlsCusLonContractWithdraw.getDueDate());
                dealEndDate.setTime(repaymentListDeal1.get(i).getPlannedDueDate());
            } else if (i == repaymentListDeal1.size() - 1) {
                dealStartDate.setTime(repaymentListDeal1.get(i - 1).getPlannedDueDate());
                dealStartDate.add(Calendar.DAY_OF_MONTH, 1);
                dealEndDate.setTime(repaymentListDeal1.get(i).getPlannedDueDate());
                dealEndDate.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                dealStartDate.setTime(repaymentListDeal1.get(i - 1).getPlannedDueDate());
                dealStartDate.add(Calendar.DAY_OF_MONTH, 1);
                dealEndDate.setTime(repaymentListDeal1.get(i).getPlannedDueDate());
            }
            hlsCusLonContractRepaymentDeal2.setStartDay(dealStartDate.getTime());
            hlsCusLonContractRepaymentDeal2.setEndDay(dealEndDate.getTime());
            Double intereSum = hlsCusLonContractRepaymentMapper.intereSum(hlsCusLonContractRepaymentDeal2);
            repaymentListDeal1.get(i).setPlannedDueAmount(CalculateUtil.sub(repaymentListDeal1.get(i).getPlannedDueAmount(), intereSum));
            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, repaymentListDeal1.get(i));

        }

        //将期数排序
//        sortContractRepayment(iRequest, hlsCusLonContractWithdraw);

       /* //保存数据
        self().save(iRequest, hlsCusLonContractWithdraw);*/
    }

    //手工测算
    private void handEstimation(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw, HlsCusLonContractQuotation contractQuotation, List<HlsCusLonContractRepayment> hlsCusLonContractRepayments, Date[] repaymentPrincipalDate) throws Exception {
        //获取第1期的计算日期
        Date firstInterestCalcDate = null;

        Long times = 1L;
        Long calcInterestDays;//计息天数
        Double repaymentAmount;
        //提款金额
        Double outStd = hlsCusLonContractWithdraw.getDueAmount();
        //实际提款日

        Date repaymentCalcDate = hlsCusLonContractWithdraw.getDueDate();
        Date nextRepaymentCalcDate;
        Date nextRepaymentDate;

        if (!ObjectUtils.isEmpty(contractQuotation)) {
            //3.在规定还款期数内计算还款计划，在提款期数范围内循环插入
            String flag = "N";

            for (int i = 0; i < hlsCusLonContractRepayments.size(); i++) {
                calcInterestDays = 0L;
                nextRepaymentCalcDate = hlsCusLonContractRepayments.get(i).getPlannedDueDate();
                nextRepaymentDate = hlsCusLonContractRepayments.get(i).getPlannedDueDate();

                //如果firstInterestCalcDate第一期的计算日期已经存在，那么就按照第一期的计算日期为基准日期进行月末矫正
                if (!ObjectUtils.isEmpty(firstInterestCalcDate)) {
                    nextRepaymentCalcDate = HlsCusEndOfMonth.calcEndOfMonth(firstInterestCalcDate, nextRepaymentCalcDate);
                }
                //计息开始日
                Calendar cStart = Calendar.getInstance();
                //计息结束日
                Calendar cEnd = Calendar.getInstance();
                cStart.setTime(repaymentCalcDate);
                cEnd.setTime(nextRepaymentCalcDate);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                if (cStart.getTime() == cEnd.getTime() && times == 1) {

                    repaymentCalcDate = nextRepaymentCalcDate;
                    continue;
                }

                //计息天数
                calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
                calcInterestDays = calcInterestDays + daysAjust(hlsCusLonContractWithdraw.getDueDate(), hlsCusLonContractWithdraw.getWithdrawEndDate(),
                        repaymentPrincipalDate, repaymentCalcDate, nextRepaymentCalcDate, hlsCusLonContractWithdraw.getWithdrawId());
                //考虑到一个临界值，如果第一期的结息日期和起息日期一致，那么上述计算得到的值为0，那么给当前值+1，然后在总体计算完成之后，
                // 不需要去给第一期+1天
                if (times == 1 && calcInterestDays == 0) {
                    calcInterestDays = calcInterestDays + 1;
                }

                String calcInterestYearDays = hlsCusLonContractWithdraw.getCalcInterestYearDays();
                if (i == 0) {
                    repaymentAmount = hlsCusLonContractWithdraw.getDueAmount() * calcInterestDays * hlsCusLonContractWithdraw.getIntRate() / Integer.valueOf(calcInterestYearDays).intValue();
                } else {
                    repaymentAmount = hlsCusLonContractRepayments.get(i - 1).getInterestAccrualBalance() * calcInterestDays * hlsCusLonContractWithdraw.getIntRate() / Integer.valueOf(calcInterestYearDays).intValue();
                }

                HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
                hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                hlsCusLonContractRepayment.setTimes(hlsCusLonContractRepayments.get(i).getTimes());
                hlsCusLonContractRepayment.setCfType(70L);
                hlsCusLonContractRepayment.setCfItem(302L);
                hlsCusLonContractRepayment.setCfDirection("OUTFLOW");
                hlsCusLonContractRepayment.setCfStatus("RELEASE");
                //还款计算日
                hlsCusLonContractRepayment.setPlannedCalcDate(nextRepaymentCalcDate);
                hlsCusLonContractRepayment.setPlannedDueDate(nextRepaymentDate);

                hlsCusLonContractRepayment.setInterestPeriodDays(Double.valueOf(calcInterestDays));
                hlsCusLonContractRepayment.setContractId(hlsCusLonContractWithdraw.getContractId());
                hlsCusLonContractRepayment.setPlannedDueAmount(repaymentAmount);
                hlsCusLonContractRepayment.setInterestAccrualBalance(hlsCusLonContractRepayments.get(i).getInterestAccrualBalance());
                hlsCusLonContractRepayment.setWriteOffFlag("NOT");
                hlsCusLonContractRepayment.setReceivedBpName(hlsCusLonContractRepayments.get(i).getReceivedBpName());
                hlsCusLonContractRepayment.setExchangeRate(1D);
                hlsCusLonContractRepayment.setIsSystemFlag("Y");
                hlsCusLonContractRepaymentService.insertSelective(iRequest, hlsCusLonContractRepayment);

                repaymentCalcDate = nextRepaymentCalcDate;
            }
        }
    }

    //2018/9/27:第一期：算头算尾+1   还本日还息：不算头不算尾-1   还本下一期：算头算尾+1   合同结束日：不算头不算尾-1
    private Long daysAjust(Date withdrawPlanDate, Date endActiveDate, Date[] repaymentPrincipalDate,
                           Date repaymentCalcDateFrom, Date repaymentCalcDateTo, Long withdrawId) {

        Long adjustDays = 0L;
        //上一个日期是否为系统
        boolean isSysFlagPre;
        //下一个日期是否为系统
        boolean isSysFlagNext;
        //上一期的日期是否存在系统计算的利息，同时上一期的日期存在对应本金
        boolean existPrincipalInterestPre = false;
        //下一期的日期是否存在系统计算的利息，同时下一期的日期存在对应本金
        boolean existPrincipalInterestNext = false;

        HlsCusLonContractRepayment repayment = new HlsCusLonContractRepayment();
        repayment.setWithdrawId(withdrawId);
        repayment.setCfItem(301L);
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments =
                hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(repayment);
        //获取所有的利息
        repayment.setCfItem(302L);
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments1 =
                hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(repayment);


        //获取系统计算的利息
        repayment.setIsSystemFlag("Y");
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments2 =
                hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(repayment);

        if (repaymentCalcDateFrom.getTime() == withdrawPlanDate.getTime()) {
            isSysFlagPre = false;
            for (HlsCusLonContractRepayment t : hlsCusLonContractRepayments1) {
                if (t.getPlannedCalcDate().getTime() == repaymentCalcDateFrom.getTime()) {
                    isSysFlagPre = true;
                    break;
                }
            }

        } else {
            isSysFlagPre = true;
            for (HlsCusLonContractRepayment contractRepayment : hlsCusLonContractRepayments) {
                if (contractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateFrom.getTime()) {
                    isSysFlagPre = false;
                    //如果当前手工插入本金的日期和当前系统计算的利息重合 则为true
                    for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments2) {
                        if (hlsCusLonContractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateFrom.getTime()) {
                            existPrincipalInterestPre = true;
                            break;
                        }
                    }

                }
            }
            if (existPrincipalInterestPre) {
                isSysFlagPre = true;
            }

        }

        if (repaymentCalcDateTo.getTime() == endActiveDate.getTime()) {
            isSysFlagNext = false;
        } else {
            isSysFlagNext = true;
            for (HlsCusLonContractRepayment contractRepayment : hlsCusLonContractRepayments) {
                if (contractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateTo.getTime()) {
                    isSysFlagNext = false;
                    //如果当前手工插入本金的日期和当前系统计算的利息重合 则为true
                    for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments2) {
                        if (hlsCusLonContractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateTo.getTime()) {
                            existPrincipalInterestNext = true;
                            break;
                        }
                    }
                }
            }

            if (existPrincipalInterestNext) {
                isSysFlagNext = true;
            }

        }

//        if(isSysFlagPre && isSysFlagNext){
//            adjustDays=0L;
//        }
//        if(!isSysFlagPre && isSysFlagNext){
//            adjustDays = 1L;
//        }
//        if(isSysFlagPre && !isSysFlagNext){
//            adjustDays=-1L;
//        }
//        if(!isSysFlagPre && !isSysFlagNext){
//            adjustDays=0L;
//        }

        adjustDays = 0L;
//      /*  Long adjustDays = 0L;
//        if (repaymentCalcDateFrom.getTime() == withdrawPlanDate.getTime()) {
//            adjustDays = adjustDays + 1;
//        }
//        for (int i = 0; i < repaymentPrincipalDate.length; i++) {
//            if (repaymentCalcDateTo.getTime() == repaymentPrincipalDate[i].getTime()) {
//                adjustDays = adjustDays - 1;
//            }
//        }
//        for (int i = 0; i < repaymentPrincipalDate.length; i++) {
//            if (repaymentCalcDateFrom.getTime() == repaymentPrincipalDate[i].getTime()) {
//                adjustDays = adjustDays + 1;
//            }
//        }
//        if (repaymentCalcDateTo.getTime() == endActiveDate.getTime()) {
//            adjustDays = adjustDays - 1;
//            //若合同结束日又是还本日则不需要两次减一
//            for (int i = 0; i < repaymentPrincipalDate.length; i++) {
//                if (endActiveDate.getTime() == repaymentPrincipalDate[i].getTime()) {
//                    adjustDays = adjustDays + 1;
//                }
//            }
//        }
//*/
//       /* if(repaymentCalcDateFrom.getTime() == withdrawPlanDate.getTime() && repaymentCalcDateTo.getTime()!=withdrawPlanDate.getTime()){
//            adjustDays=adjustDays-1;
//        }*/
//        /*起息日结息日还本日同一天*/
//        /*if (repaymentCalcDateFrom.getTime() == repaymentCalcDateTo.getTime()){
//            adjustDays = adjustDays + 1;
//        }*/
        return adjustDays;
    }

    /**
     * @param currentMonth
     * @param InterestMonth
     * @param count
     * @return
     */
    private Boolean calcMonth(int currentMonth, int InterestMonth, int count) {
        int month = InterestMonth + count;
        month = month == 12 ? 12 : month % 12;
        if (currentMonth == month) {
            return true;
        } else {
            return false;
        }

    }

    //还本计划处理剩余本金,同时刷新还款计划期数
    public void updateAllPrincipalOutStd(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw, String flag, Date[] repaymentPrincipalDate) {
        Double outStd;
        HlsCusLonContractQuotation hlsCusLonContractQuotationTemp = new HlsCusLonContractQuotation();

        hlsCusLonContractQuotationTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationService.select(iRequest, hlsCusLonContractQuotationTemp, 1, 9999);
        //按照还款日顺序获取该笔提款项下所有还本计划
        HlsCusLonContractRepayment hlsCusLonContractRepaymentTemp = new HlsCusLonContractRepayment();
        hlsCusLonContractRepaymentTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        hlsCusLonContractRepaymentTemp.setCfItem(301L);
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {
            //- hlsCusLonContractRepayment.getPlannedDueAmount()
            outStd = getOutStd(hlsCusLonContractRepayment.getPlannedDueDate(), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount());
            hlsCusLonContractRepayment.setInterestAccrualBalance(outStd);
            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, hlsCusLonContractRepayment);
        }
        //302L:融资-还款利息
        HlsCusLonContractRepayment hlsCusLonContractRepaymentTemp1 = new HlsCusLonContractRepayment();
        hlsCusLonContractRepaymentTemp1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        hlsCusLonContractRepaymentTemp1.setCfItem(302L);
        hlsCusLonContractRepaymentTemp1.setInterestFlag("Y");
        List<HlsCusLonContractRepayment> lonContractRepaymentList1 = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp1);

        //301L:融资-还款本金
        hlsCusLonContractRepaymentTemp.setCfItem(301L);
        hlsCusLonContractRepaymentTemp.setInterestFlag("Y");
        List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        Long times = 0L;
        Long times1 = 1L;
        Date lastRepaymentDate = hlsCusLonContractWithdraw.getDueDate();
        //对还款-本金进行排序
        for (HlsCusLonContractRepayment lonContractRepayment : lonContractRepaymentList) {
            if (lonContractRepayment.getPlannedDueDate().getTime() > lastRepaymentDate.getTime()) {
                times = times + 1;
            }
            lonContractRepayment.set__status("update");
            lonContractRepayment.setTimes(times);
            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
        //按照日期升序排序
        if (CollectionUtils.isNotEmpty(lonContractRepaymentList1)) {
            Collections.sort(lonContractRepaymentList1, new Comparator<HlsCusLonContractRepayment>() {
                @Override
                public int compare(HlsCusLonContractRepayment o1, HlsCusLonContractRepayment o2) {
                    if (o1.getPlannedDueDate().after(o2.getPlannedDueDate())) {
                        return 1;
                    }
                    if (o1.getPlannedDueDate() == o2.getPlannedDueDate()) {
                        return 0;
                    }
                    return -1;
                }
            });

        }
        for (HlsCusLonContractRepayment lonContractRepayment : lonContractRepaymentList1) {
            if (lonContractRepayment.getPlannedDueDate().getTime() > lastRepaymentDate.getTime()) {
                times1 = times1 + 1;
            }
            lonContractRepayment.set__status("update");
            lonContractRepayment.setTimes(times1);
            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
        //当期还本还息
        if (("REPAYMENT_INTERES_CURRENT_PERIOD".equalsIgnoreCase(hlsCusLonContractQuotations.get(0).getInterestCalcMethod())
                || HlsCusConstant.LON_INTEREST_CALC_METHOD.ONCE_CLEAR.equalsIgnoreCase(hlsCusLonContractQuotations.get(0).getInterestCalcMethod()))
                && flag.equals("Y")) {
            //获取融资合同
            HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
            hlsCusLonContract.setContractId(hlsCusLonContractWithdraw.getContractId());
            hlsCusLonContract = hlsCusLonContractService.selectByPrimaryKey(iRequest, hlsCusLonContract);

            //获取授信机构
            HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
            hlsCusBpMaster.setBpId(hlsCusLonContract.getCreditBpId());
            hlsCusBpMaster = hlsCusBpMasterService.selectByPrimaryKey(iRequest, hlsCusBpMaster);

            HlsCusCtLonBankAccount hlsCusCtLonBankAccount = new HlsCusCtLonBankAccount();
            hlsCusCtLonBankAccount.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusCtLonBankAccount> ctLonBankAccountList = hlsCusCtLonBankAccountService.select(iRequest, hlsCusCtLonBankAccount, 1, 999999);

            Long totalDays = Long.valueOf(compareDays(hlsCusLonContractWithdraw.getDueDate(), hlsCusLonContractWithdraw.getWithdrawEndDate()));

            HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
            hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            hlsCusLonContractRepayment.setInterestFlag("Y");
            //查询利息
            List<HlsCusLonContractRepayment> tempIntList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanIntByTimesOrderByRepaymentDate(hlsCusLonContractRepayment);

            String sysflag;
            for (int i = 1; i < tempIntList.size() + 1; i++) {
                HlsCusLonContractRepayment interestRepayment = tempIntList.get(i - 1);
                Long betweenDay = 0L;
                Date StartDate;
                if (i == 1) {
                    hlsCusLonContractRepayment.setStartDay(hlsCusLonContractWithdraw.getDueDate());
                    hlsCusLonContractRepayment.setEndDay(tempIntList.get(i - 1).getPlannedCalcDate());

                } else {
                    hlsCusLonContractRepayment.setStartDay(tempIntList.get(i - 2).getPlannedCalcDate());
                    hlsCusLonContractRepayment.setEndDay(tempIntList.get(i - 1).getPlannedCalcDate());
                }

                //查询两个日期之间的本金
                List<HlsCusLonContractRepayment> lonContractRepaymentLists = hlsCusLonContractRepaymentMapper.selectRepaymentPlanByLastDateOrderByRepaymentDate(hlsCusLonContractRepayment);
                if (CollectionUtils.isNotEmpty(lonContractRepaymentLists)) {


                    //用来获取startDay和endDay中间的最后一期的还款-本金
                    HlsCusLonContractRepayment lastPrincipal = lonContractRepaymentLists.get(lonContractRepaymentLists.size() - 1);
                    for (int j = 0; j < lonContractRepaymentLists.size(); j++) {
                        double amount = 0L;
                        HlsCusLonContractRepayment var1 = new HlsCusLonContractRepayment();
                        if (i == 1) {
                            StartDate = hlsCusLonContractWithdraw.getDueDate();
                        } else {
//                        HlsCusLonContractRepayment temp = new HlsCusLonContractRepayment();
//                        temp.setStartDay(tempIntList.get(i - 1).getPlannedDueDate());
//                        temp.setEndDay(tempIntList.get(i).getPlannedDueDate());
//                        List<HlsCusLonContractRepayment> tempList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanByDateOrderByRepaymentDate(temp);
//                        StartDate = tempList.get(tempList.size() - 1).getPlannedDueDate();
                            StartDate = tempIntList.get(i - 2).getPlannedCalcDate();
                        }
                        betweenDay = Long.valueOf(compareDays(StartDate, lonContractRepaymentLists.get(j).getPlannedCalcDate()));
                        betweenDay = betweenDay + daysAjust(hlsCusLonContractWithdraw.getDueDate(), hlsCusLonContractWithdraw.getWithdrawEndDate(),
                                repaymentPrincipalDate, StartDate, lonContractRepaymentLists.get(j).getPlannedCalcDate(), hlsCusLonContractWithdraw.getWithdrawId());
                        amount = getOutStd(lonContractRepaymentLists.get(j).getPlannedCalcDate(), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount());
                        var1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                        var1.setTimes(lonContractRepaymentLists.get(j).getTimes());
                        var1.setCfType(70L);
                        var1.setCfItem(302L);
                        var1.setCfDirection("OUTFLOW");
                        var1.setCfStatus("RELEASE");
                        var1.setPlannedDueDate(lonContractRepaymentLists.get(j).getPlannedDueDate());
                        var1.setPlannedCalcDate(lonContractRepaymentLists.get(j).getPlannedCalcDate());
                        var1.setInterestPeriodDays(Double.valueOf(betweenDay));
                        var1.setContractId(hlsCusLonContractWithdraw.getContractId());
                        var1.setPlannedDueAmount(transfor(lonContractRepaymentLists.get(j).getPlannedDueAmount() * betweenDay / new Long(String.valueOf(hlsCusLonContractQuotations.get(0).getCalcInterestYearDays())) * hlsCusLonContractWithdraw.getIntRate()));
                        var1.setInterestAccrualBalance(transfor(amount));
                        var1.setWriteOffFlag("NOT");
                        var1.setExchangeRate(1D);
                        var1.setReceivedBpName(hlsCusBpMaster.getBpName());
                        if (ctLonBankAccountList.size() > 0) {
                            var1.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
                            var1.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
                        }
                        if ((transfor(lonContractRepaymentLists.get(j).getPlannedDueAmount() * betweenDay / new Long(String.valueOf(hlsCusLonContractQuotations.get(0).getCalcInterestYearDays())) * hlsCusLonContractWithdraw.getIntRate())) > 0) {
                            var1 = hlsCusLonContractRepaymentService.insertSelective(iRequest, var1);

                        }
                    }

//                    if("N".equalsIgnoreCase(lastPrincipal.getIsSystemFlag()) &&
////                            lastPrincipal.getPlannedCalcDate().getTime()==hlsCusLonContractWithdraw.getWithdrawEndDate().getTime()){
////
////                    }else{
////                        //betweenDay = betweenDay + daysAjust(hlsCusLonContractWithdraw.getDueDate(), hlsCusLonContractWithdraw.getWithdrawEndDate(), repaymentPrincipalDate, StartDate, lonContractRepaymentLists.get(j).getPlannedCalcDate());
////                        Double amount = transfor(lastPrincipal.getInterestAccrualBalance() * interestRepayment.getInterestPeriodDays()
////                                * hlsCusLonContractWithdraw.getIntRate() / new Long(String.valueOf(hlsCusLonContractQuotations.get(0).getCalcInterestYearDays())));
////                        if(amount>0){
////                            interestRepayment.setPlannedDueAmount(amount);
////                            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, interestRepayment);
////                        }
////                    }





                    /* HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
                lonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                lonContractRepayment.setCfItem(302L);
                if (i == 1) {
                    lonContractRepayment.setStartDay(hlsCusLonContractWithdraw.getDueDate());
                    lonContractRepayment.setEndDay(tempIntList.get(i - 1).getPlannedDueDate());
                } else {
                    lonContractRepayment.setStartDay(tempIntList.get(i-2).getPlannedDueDate());
                    lonContractRepayment.setEndDay(tempIntList.get(i-1).getPlannedDueDate());
                }
                hlsCusLonContractRepaymentMapper.updateLonContractRepayment(lonContractRepayment);*/
                }
            }
        }

    }

    public void sortContractRepayment(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw) {
        //302L:融资-还款利息
        HlsCusLonContractRepayment hlsCusLonContractRepaymentTemp1 = new HlsCusLonContractRepayment();
        hlsCusLonContractRepaymentTemp1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        hlsCusLonContractRepaymentTemp1.setCfItem(302L);
        hlsCusLonContractRepaymentTemp1.setInterestFlag("Y");
        List<HlsCusLonContractRepayment> lonContractRepaymentList1 = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp1);

        //301L:融资-还款本金
        hlsCusLonContractRepaymentTemp1.setCfItem(301L);
        hlsCusLonContractRepaymentTemp1.setInterestFlag("Y");
        List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp1);

        Long times = 0L;
        Long times1 = 0L;
        Date lastRepaymentDate = hlsCusLonContractWithdraw.getDueDate();
        for (HlsCusLonContractRepayment lonContractRepayment : lonContractRepaymentList) {
            if (lonContractRepayment.getPlannedDueDate().getTime() > lastRepaymentDate.getTime()) {
                times = times + 1;
            }
            lonContractRepayment.set__status("update");
            lonContractRepayment.setTimes(times);
            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
        //按照日期升序排序
        if (CollectionUtils.isNotEmpty(lonContractRepaymentList1)) {
            Collections.sort(lonContractRepaymentList1, new Comparator<HlsCusLonContractRepayment>() {
                @Override
                public int compare(HlsCusLonContractRepayment o1, HlsCusLonContractRepayment o2) {
                    if (o1.getPlannedDueDate().after(o2.getPlannedDueDate())) {
                        return 1;
                    }
                    if (o1.getPlannedDueDate() == o2.getPlannedDueDate()) {
                        return 0;
                    }
                    return -1;
                }
            });

        }
        for (HlsCusLonContractRepayment lonContractRepayment : lonContractRepaymentList1) {
            /*if (lonContractRepayment.getPlannedDueDate().getTime() > lastRepaymentDate.getTime()) {*/
            times1 = times1 + 1;
            /* }*/
            lonContractRepayment.set__status("update");
            lonContractRepayment.setTimes(times1);
            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
    }


    /**
     * 剩余本金计算利息
     *
     * @param iRequest
     * @param repaymentCalcDateFrom
     * @param repaymentCalcDateTo
     * @param withdrawId
     * @param withdrawAmount
     * @param intRate
     * @param calcInterestYearDays
     * @return
     */
    //计算当期还款利息
    @Override
    public Double getRepaymentAmount(IRequest iRequest, Date repaymentCalcDateFrom, Date repaymentCalcDateTo,
                                     Long withdrawId, Double withdrawAmount, Double intRate, String calcInterestYearDays,
                                     Date[] repaymentPrincipalDate) {
        Double resultAmount = 0D;
        Double sumRepaymentPrincipal = 0D;
        Date calcDateStart = repaymentCalcDateFrom;
        Long calcInterestDays = 0L;
        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Long adjDays = 0L;
        HlsCusLonContractWithdraw contractWithdraw = new HlsCusLonContractWithdraw();
        contractWithdraw.setWithdrawId(withdrawId);
        contractWithdraw = self().selectByPrimaryKey(iRequest, contractWithdraw);

        HlsCusLonContractRepayment hlsCusLonContractRepaymentTemp = new HlsCusLonContractRepayment();
        hlsCusLonContractRepaymentTemp.setWithdrawId(withdrawId);
        hlsCusLonContractRepaymentTemp.setCfItem(301L);
        //hlsCusLonContractRepaymentTemp.setStartDay(repaymentCalcDateFrom);
        //hlsCusLonContractRepaymentTemp.setEndDay(repaymentCalcDateTo);
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);
//        for (int k = 0; k < repaymentPrincipalDate.length; k++) {
//            if (repaymentPrincipalDate[k].getTime() == repaymentCalcDateTo.getTime()) {
//                adjDays = 1L;
//            }
//        }


        /*for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {*/
        for (int i = 0; i < hlsCusLonContractRepayments.size(); i++) {
            HlsCusLonContractRepayment hlsCusLonContractRepayment = hlsCusLonContractRepayments.get(i);
            calcInterestDays = 0L;
            if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() < repaymentCalcDateFrom.getTime()) {
                sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
            }
            if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() >= repaymentCalcDateFrom.getTime()
                    && hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= repaymentCalcDateTo.getTime()) {
                cStart.setTime(calcDateStart);
                cEnd.setTime(hlsCusLonContractRepayment.getPlannedDueDate());
                calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24) + adjDays;
                if (repaymentPrincipalDate != null) {
                    calcInterestDays = calcInterestDays + daysAjust(contractWithdraw.getDueDate(), contractWithdraw.getWithdrawEndDate(),
                            repaymentPrincipalDate, cStart.getTime(), cEnd.getTime(), withdrawId);
                }

                resultAmount = resultAmount + ((withdrawAmount - sumRepaymentPrincipal) * calcInterestDays * intRate / Integer.valueOf(calcInterestYearDays).intValue());
                sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
                calcDateStart = hlsCusLonContractRepayment.getPlannedDueDate();
            }
            if (calcDateStart.getTime() >= repaymentCalcDateFrom.getTime()
                    && calcDateStart.getTime() < repaymentCalcDateTo.getTime()
                    && hlsCusLonContractRepayment.getPlannedDueDate().getTime() > repaymentCalcDateTo.getTime()) {
                cStart.setTime(calcDateStart);
                cEnd.setTime(repaymentCalcDateTo);
                calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
                if (repaymentPrincipalDate != null) {
                    calcInterestDays = calcInterestDays + daysAjust(contractWithdraw.getDueDate(), contractWithdraw.getWithdrawEndDate(),
                            repaymentPrincipalDate, cStart.getTime(), cEnd.getTime(), withdrawId);
                }
                resultAmount = resultAmount + ((withdrawAmount - sumRepaymentPrincipal) * calcInterestDays * intRate / Integer.valueOf(calcInterestYearDays).intValue());

                calcDateStart = hlsCusLonContractRepayment.getPlannedDueDate();
            }
            adjDays = 0L;
        }
        for (int k = 0; k < repaymentPrincipalDate.length; k++) {
            if (repaymentPrincipalDate[k].getTime() == repaymentCalcDateTo.getTime()) {
                adjDays = 1L;
            }
        }
        if (resultAmount == 0) {
            cStart.setTime(repaymentCalcDateFrom);
            cEnd.setTime(repaymentCalcDateTo);
            calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24) + adjDays;
            calcInterestDays = calcInterestDays + daysAjust(contractWithdraw.getDueDate(), contractWithdraw.getWithdrawEndDate(),
                    repaymentPrincipalDate, repaymentCalcDateFrom, repaymentCalcDateTo, withdrawId);
            resultAmount = (withdrawAmount - sumRepaymentPrincipal) * calcInterestDays * intRate / Integer.valueOf(calcInterestYearDays).intValue();
            adjDays = 0L;
        }

        return (double) (resultAmount);
    }

    //计算当期剩余本金
    private Double getOutStd(Date repaymentCalcDateTo, Long withdrawId, Double withdrawAmount) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        Double resultAmount;
        Double sumRepaymentPrincipal = 0D;

        HlsCusLonContractRepayment hlsCusLonContractRepaymentTemp = new HlsCusLonContractRepayment();
        hlsCusLonContractRepaymentTemp.setWithdrawId(withdrawId);
        hlsCusLonContractRepaymentTemp.setCfItem(301L);
        List<HlsCusLonContractRepayment> hlsCusLonContractRepayments = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
        hlsCusLonContractWithdraw.setWithdrawId(withdrawId);
        hlsCusLonContractWithdraw = self().selectByPrimaryKey(iRequest, hlsCusLonContractWithdraw);
        Date lastDate = hlsCusLonContractWithdraw.getWithdrawEndDate();
        for (HlsCusLonContractRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {

            //如果不是最后一期
            if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() < lastDate.getTime()) {
                if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= repaymentCalcDateTo.getTime()) {
                    sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
                }
            } else {
                if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= repaymentCalcDateTo.getTime()) {
                    sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
                }

            }
        }
        resultAmount = withdrawAmount - sumRepaymentPrincipal;
        return resultAmount;
    }

    //计算下一期还款日或者还款计算日
    private Date getNextRepaymentDate(Date dateFrom, String interestCycle) {
        Date resultDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFrom);//设置起时间

        if ("YEAR".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.YEAR, 1);
        } else if ("HALF_A_YEAR".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, 6);
        } else if ("QUARTER".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, 3);
        } else if ("MONTH".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, 1);
        }

        resultDate = cal.getTime();
        return resultDate;
    }


    /**
     * 计算XIRR
     *
     * @param iRequest
     * @param hlsCusLonContractWithdraw
     * @param hlsCusLonContractQuotation
     */
    public void calXirr(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw, HlsCusLonContractQuotation hlsCusLonContractQuotation) {

        HlsCusLonContractRepayment contractRepayment = new HlsCusLonContractRepayment();
        contractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractRepayment> lonContractReps = hlsCusLonContractRepaymentMapper.selectLonContractRep(contractRepayment);
        double[] payments = new double[lonContractReps.size() + 1];
        Date[] dates = new Date[lonContractReps.size() + 1];
        for (int i = 0; i < lonContractReps.size(); i++) {
            if ("OUTFLOW".equalsIgnoreCase(lonContractReps.get(i).getCfDirection())) {
                payments[i] = -lonContractReps.get(i).getPlannedDueAmount();
                dates[i] = lonContractReps.get(i).getPlannedDueDate();
            } else {
                payments[i] = lonContractReps.get(i).getPlannedDueAmount();
                dates[i] = lonContractReps.get(i).getPlannedDueDate();
            }
        }
        //防止精度问题
        payments[lonContractReps.size()] = hlsCusLonContractWithdraw.getDueAmount();
        dates[lonContractReps.size()] = hlsCusLonContractWithdraw.getDueDate();
        Double xirr = HlsCusXirr.Newtons_method(0.1, payments, dates);
        if (!xirr.isInfinite() && !xirr.isNaN()) {
            //(new BigDecimal(xirr.toString()).setScale(6, BigDecimal.ROUND_HALF_UP)).doubleValue()
            hlsCusLonContractQuotation.setXirr((double) Math.round(xirr * 1000000) / 1000000);
            hlsCusLonContractQuotationService.updateByPrimaryKeySelective(iRequest, hlsCusLonContractQuotation);
        } else {
            hlsCusLonContractQuotation.setXirr(null);
            hlsCusLonContractQuotationMapper.updateXirrNull(hlsCusLonContractQuotation.getQuotationId());
        }


    }


    //提款提交工作流
    @Override
    public List<HlsCusLonContractWithdraw> submitLonContractWithdrawToWfl(IRequest iRequest, HlsCusLonContractWithdraw lonContractWithdraw, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
        HlsCusCtLonContractBankAccount bankAccount = new HlsCusCtLonContractBankAccount();
        bankAccount.setBankAccountType("WITHDRAWAL_ACCOUNT");
        bankAccount.setContractId(lonContractWithdraw.getContractId());
        int accountTypeCount = ctLonContractBankAccountService.selectBankAccountTypeCount(bankAccount);
//        if (accountTypeCount == 0) {
//            throw new HlsCusAmountOverException("账户信息中需要维护一个账户类型为提款户的账户信息!");
//        }

        //先保存数据
        self().save(iRequest, lonContractWithdraw);

        //校验计提方式所有手续费的要一致，所有担保费的要一致;
        Long count303 = hlsCusLonContractRepaymentMapper.queryAmortizationMethodCount(lonContractWithdraw.getWithdrawId(), 303L);
        Long count304 = hlsCusLonContractRepaymentMapper.queryAmortizationMethodCount(lonContractWithdraw.getWithdrawId(), 304L);
        if (count303 > 1) {
            throw new IllegalArgumentException("所有手续费的计提方式要一致,请核对!");
        }

        if (count304 > 1) {
            throw new IllegalArgumentException("所有担保费的计提方式要一致,请核对!");
        }
        //还款周期为空时，费用信息计提方式不可选实际利率法！
        HlsCusLonContractQuotation lonContractQuotation = new HlsCusLonContractQuotation();
        lonContractQuotation.setWithdrawId(lonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractQuotation> quotations = hlsCusLonContractQuotationMapper.lonContractQuotationById(lonContractQuotation);

        //手续费分摊方式
        String chargeAmortizationMethod = lonContractRepaymentMergeMapper.queryAmortizationMethod(lonContractWithdraw.getWithdrawId(), 303L);
        //担保费分摊方式
        String guaAmortizationMethod = lonContractRepaymentMergeMapper.queryAmortizationMethod(lonContractWithdraw.getWithdrawId(), 304L);
        if (ObjectUtils.isEmpty(quotations.get(0).getInterestCycle()) && (BALANCE.equals(chargeAmortizationMethod) || BALANCE.equals(guaAmortizationMethod))) {
            throw new IllegalArgumentException("还款周期为空时，费用信息计提方式不可选实际利率法,请核对!");
        }
        //还款计划总金额
        Double planAmountSum = hlsCusLonContractRepaymentMapper.selectPlanAmountSum(lonContractWithdraw.getWithdrawId(), "Y");
        if (planAmountSum.compareTo(lonContractWithdraw.getDueAmount()) != 0) {
            throw new HlsCusAmountOverException("还本付息计划中各期本金金额加和需等于提款金额,请核对!");
        }
        if ("NORMAL".equalsIgnoreCase(lonContractWithdraw.getDataClass()) && lonContractWithdraw.getCreditContractId() != null) {
            //已提交金额+本次申请提款金额不可超过授信额度
            Double surplusCredit = OracleUtils.nvl(lonContractWithdrawMapper.querySurplusCredit(lonContractWithdraw.getWithdrawId()), 0.0);
            if (lonContractWithdraw.getDueAmount() > surplusCredit) {
                String str = new BigDecimal(surplusCredit.toString()).toPlainString();
                throw new HlsCusAmountOverException("可用授信额度为" + str + "本次提款金额大于可用额度,请核对!");
            }
        }


        Double oldContractId = lonContractWithdraw.getOldContractId();
        Double oldWithdrawId = lonContractWithdraw.getOldWithdrawId();

        lonContractWithdraw = self().selectByPrimaryKey(iRequest, lonContractWithdraw);

            //修改单据状态
        lonContractWithdraw.setWithdrawStatus("APPROVED");
        lonContractWithdraw.setChangeStatus("APPROVED");
        lonContractWithdraw.setApprovalDate(new Date());

        if ("NORMAL".equalsIgnoreCase(lonContractWithdraw.getDataClass())) {
            lonContractRepaymentMergeService.calcLonConWithdrawFinCostNew(iRequest, lonContractWithdraw,null);
        } else {
            hlsCusLonContractWithdrawService.lonConWithdrawChangeReqConfirm(iRequest, lonContractWithdraw);
            lonContractWithdraw.setApprovalDate(new Date());
            HlsCusLonContractWithdraw lonContractWithdrawNormal = new HlsCusLonContractWithdraw();
            lonContractWithdrawNormal.setWithdrawId(lonContractWithdraw.getChangeReqId());
            lonContractWithdrawNormal = hlsCusLonContractWithdrawService.selectByPrimaryKey(iRequest, lonContractWithdrawNormal);
            lonContractRepaymentMergeService.calcLonConWithdrawFinCostNew(iRequest, lonContractWithdrawNormal,null);
            lonContractWithdraw.setChangeStatus("APPROVED");
        }
        self().updateByPrimaryKeySelective(iRequest, lonContractWithdraw);

        List<HlsCusLonContractWithdraw> list = new ArrayList<>();
        list.add(lonContractWithdraw);
        return list;
    }

    /*---------------融资提款综合查询----------------*/
    @Override
    public List<HlsCusLonContractWithdraw> withdrawComprehensiveQuery(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);
        Long companyId = request.getCompanyId();

        String[] withdrawStatusArray = null;
        String[] financingChannelArray = null;
        if (lonContractWithdraw.getWithdrawStatus() != null) {
            withdrawStatusArray = lonContractWithdraw.getWithdrawStatus().split("、");
        }
        if (lonContractWithdraw.getFinancingChannel() != null) {
            financingChannelArray = lonContractWithdraw.getFinancingChannel().split("、");
        }
        Map<String, Object> params = new HashMap<String, Object>(8);
        params.put("companyId", companyId);
        params.put("withdrawStatusArray", withdrawStatusArray);
        params.put("financingChannelArray", financingChannelArray);
        params.put("amountFrom", lonContractWithdraw.getAmountFrom());
        params.put("amountTo", lonContractWithdraw.getAmountTo());
        params.put("withdrawDateFrom", lonContractWithdraw.getWithdrawDateFrom());
        params.put("withdrawDateTo", lonContractWithdraw.getWithdrawDateTo());
        params.put("withdrawEndDateFrom", lonContractWithdraw.getWithdrawEndDateFrom());
        params.put("withdrawEndDateTo", lonContractWithdraw.getWithdrawEndDateTo());
        params.put("contractNumber", lonContractWithdraw.getContractNumber());
        params.put("contractName", lonContractWithdraw.getContractName());
        params.put("creditBpName", lonContractWithdraw.getCreditBpName());
        params.put("withdrawNumber", lonContractWithdraw.getWithdrawNumber());

        return lonContractWithdrawMapper.withdrawComprehensiveQuery(params);
    }

    /**
     * 融资提款计提入口
     * 利息按照天数权重分摊至每个月份，还款当月倒减
     */
    @Override
    public void calcLonConWithdrawFinCost(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw) {
        int page = 1;
        int pageSize = 10;
        Long contractId = lonContractWithdraw.getContractId();
        Long withdrawId = lonContractWithdraw.getWithdrawId();
        Long companyId = lonContractWithdraw.getCompanyId();
        Date calcStartDate = lonContractWithdraw.getDueDate();
        Date calcEndDate = lonContractWithdraw.getWithdrawEndDate();
        Date lastRepaymentDate = lonContractWithdraw.getDueDate();

        //获取提款项下还息计划
        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
        lonContractRepayment.setWithdrawId(withdrawId);
        lonContractRepayment.setCfItem(302L);
        List<HlsCusLonContractRepayment> lonContractRepaymentInterestList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(lonContractRepayment);

        //获取提款项下所有还本计划
        lonContractRepayment.setCfItem(301L);
        List<HlsCusLonContractRepayment> lonContractRepaymentPrincipalList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(lonContractRepayment);

        //获取最后一笔还本日
        if (lonContractRepaymentPrincipalList.size() > 0) {
            calcEndDate = lonContractRepaymentPrincipalList.get(lonContractRepaymentPrincipalList.size() - 1).getPlannedDueDate();
        }

        //删除未确认计提数据
        HlsCusGldLonContractFinCost gldLonContractFinCostTmp = new HlsCusGldLonContractFinCost();
        gldLonContractFinCostTmp.setContractId(contractId);
        gldLonContractFinCostTmp.setWithdrawId(withdrawId);
        gldLonContractFinCostTmp.setPostFlag("N");
        List<HlsCusGldLonContractFinCost> gldLonContractFinCostList = gldLonContractFinCostService.select(request, gldLonContractFinCostTmp, 1, 999999);
        gldLonContractFinCostService.batchDelete(gldLonContractFinCostList);

        //循环所有还息计划   update by tengfei on 2019-2-20  循环所有的还本计划
        if (lonContractRepaymentPrincipalList.size() > 0 && calcStartDate != null && calcEndDate != null) {
            int month;
            Calendar cStart = Calendar.getInstance();
            Calendar cEnd = Calendar.getInstance();

            //初始化待插入计提数据
            HlsCusGldLonContractFinCost gldLonContractFinCost = new HlsCusGldLonContractFinCost();
            for (int k = 0; k < lonContractRepaymentPrincipalList.size(); k++) {
                HlsCusLonContractRepayment hlsCusLonContractRepayment = lonContractRepaymentPrincipalList.get(k);
                Double sumFinCost;
                Double finCost;
                Long calcDays;

                cStart.setTime(calcStartDate);
                cEnd.setTime(hlsCusLonContractRepayment.getPlannedDueDate());
                month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH);

                Calendar calcStart = Calendar.getInstance();
                Calendar calcEnd = Calendar.getInstance();
                calcStart.setTime(calcStartDate);

                //判断lon_contract_withdraw表中的FUND_INVEST
                String src = lonContractWithdraw.getFundInvest();
                Code code = new Code();
                code.setCode("LON.FUNDING");
                List<Code> codeList = iCodeService.selectCodes(request, code, 1, 10);
                CodeValue codeValue = new CodeValue();
                codeValue.setCodeId(codeList.get(0).getCodeId());
                codeValue.setValue(src);
                List<CodeValue> codeValueList = iCodeService.selectCodeValues(request, codeValue, page, pageSize);
                String tag = "";
                for (CodeValue codeValue1 : codeValueList) {
                    if (src.equalsIgnoreCase(codeValue1.getValue())) {
                        tag = codeValue1.getTag();
                    }
                }


                //循环相差月份
                for (int i = 0; i <= month; i++) {
                    //还款当月前半段提款日至还款日，倒减计算
                    if (i == 0 && month == 0) {
                        //获取该还款期内计算开始日之前所有计提总和
                        /*sumFinCost = getSumFinCost(request, hlsCusLonContractRepayment.getRepaymentId(), withdrawId);
                        finCost = hlsCusLonContractRepayment.getPlannedDueAmount() - sumFinCost;*/

                        calcEnd.setTime(hlsCusLonContractRepayment.getPlannedDueDate());
                        calcEnd.add(calcEnd.DATE, -1);
                        calcDays = getCalcDays(calcStartDate, calcEnd.getTime());
                        //利息=剩余本金*利率*天数/年度计息天数
                        finCost = (hlsCusLonContractRepayment.getInterestAccrualBalance() + hlsCusLonContractRepayment.getPlannedDueAmount())
                                * lonContractWithdraw.getIntRate()
                                * calcDays / Integer.parseInt(lonContractWithdraw.getCalcInterestYearDays());
                        finCost = (double) Math.round(finCost * 100) / 100;
                    }
                    //还款当月后半段至月底，按天数权重计算
                    else if (i == 0 && month != 0) {
                        calcEnd.setTime(getMonthEndDate(calcStartDate));
                        calcDays = getCalcDays(calcStartDate, calcEnd.getTime());
                        //finCost = (double) (hlsCusLonContractRepayment.getPlannedDueAmount() * calcDays / hlsCusLonContractRepayment.getInterestPeriodDays());
                        //利息=剩余本金*利率*天数/年度计息天数
                        finCost = (hlsCusLonContractRepayment.getInterestAccrualBalance() + hlsCusLonContractRepayment.getPlannedDueAmount())
                                * lonContractWithdraw.getIntRate()
                                * calcDays / Integer.parseInt(lonContractWithdraw.getCalcInterestYearDays());
                        finCost = (double) Math.round(finCost * 100) / 100;
                    }
                    //还款当月前半段月初至还款日，倒减计算
                    else if (i == month && month > 0) {
                        calcEnd.setTime(hlsCusLonContractRepayment.getPlannedDueDate());
                        calcEnd.add(calcEnd.DATE, -1);
                        calcDays = getCalcDays(calcStartDate, calcEnd.getTime());
                        if (lonContractRepaymentPrincipalList.size() - 1 == k) {
                            sumFinCost = getSumFinCost(request, hlsCusLonContractRepayment.getRepaymentId(), withdrawId);
                            sumFinCost = (double) Math.round(sumFinCost * 100) / 100;
                            List<HlsCusLonContractWithdraw> withdrawList = lonContractWithdrawMapper.lonContractWithdrawFormData(lonContractWithdraw);
                            finCost = withdrawList.get(0).getInterestSum() - sumFinCost;
                            finCost = (double) Math.round(finCost * 100) / 100;
                        } else {
                            finCost = (hlsCusLonContractRepayment.getInterestAccrualBalance() + hlsCusLonContractRepayment.getPlannedDueAmount())
                                    * lonContractWithdraw.getIntRate()
                                    * calcDays / Integer.parseInt(lonContractWithdraw.getCalcInterestYearDays());
                            finCost = (double) Math.round(finCost * 100) / 100;
                        }

                    }
                    //整月计算，按天数权重计算
                    else {
                        calcEnd.setTime(getMonthEndDate(calcStartDate));
                        calcDays = getCalcDays(calcStartDate, calcEnd.getTime());
                        //finCost = (double) (hlsCusLonContractRepayment.getPlannedDueAmount() * calcDays / hlsCusLonContractRepayment.getInterestPeriodDays());
                        finCost = (hlsCusLonContractRepayment.getInterestAccrualBalance() + hlsCusLonContractRepayment.getPlannedDueAmount())
                                * lonContractWithdraw.getIntRate()
                                * calcDays / Integer.parseInt(lonContractWithdraw.getCalcInterestYearDays());
                        finCost = (double) Math.round(finCost * 100) / 100;
                    }
                    gldLonContractFinCost.setContractId(contractId);
                    gldLonContractFinCost.setWithdrawId(withdrawId);
                    gldLonContractFinCost.setCompanyId(companyId);
                    gldLonContractFinCost.setRepaymentId(hlsCusLonContractRepayment.getRepaymentId());
                    gldLonContractFinCost.setCfItem(302L);
                    gldLonContractFinCost.setStartDate(calcStartDate);
                    gldLonContractFinCost.setEndDate(calcEnd.getTime());
                    String periodName;
                    if (calcEnd.get(Calendar.MONTH) + 1 < 10) {
                        periodName = calcEnd.get(Calendar.YEAR) + "-0" + (calcEnd.get(Calendar.MONTH) + 1);

                    } else {
                        periodName = calcEnd.get(Calendar.YEAR) + "-" + (calcEnd.get(Calendar.MONTH) + 1);
                    }
                    gldLonContractFinCost.setPeriodName(periodName);
                    gldLonContractFinCost.setDays(calcDays);


                    Double cost = (double) Math.round(finCost / (1 + Double.parseDouble(tag)) * 100) / 100;

                    gldLonContractFinCost.setFinanceIncomeInclud(finCost);
                    gldLonContractFinCost.setFinanceIncomeVat(finCost - cost);
                    gldLonContractFinCost.setFinanceCost(cost);
                    gldLonContractFinCost.setPostFlag("N");
                    gldLonContractFinCost.setFinanceCostId(null);

                    HlsCusGldLonContractFinCost gldLonContractFinCostExists = new HlsCusGldLonContractFinCost();
                    gldLonContractFinCostExists.setContractId(contractId);
                    gldLonContractFinCostExists.setWithdrawId(withdrawId);
                    gldLonContractFinCostExists.setCompanyId(companyId);
                    gldLonContractFinCostExists.setRepaymentId(hlsCusLonContractRepayment.getRepaymentId());
                    gldLonContractFinCostExists.setStartDate(calcStartDate);
                    gldLonContractFinCostExists.setEndDate(calcEnd.getTime());
                    List<HlsCusGldLonContractFinCost> gldLonContractFinCostExistsList = gldLonContractFinCostService.select(request, gldLonContractFinCostExists, 1, 999999);

                    if (gldLonContractFinCostExistsList.size() == 0) {
                        gldLonContractFinCostService.insertSelective(request, gldLonContractFinCost);
                    }

                    calcStart.setTime(calcStartDate);
                    calcEnd.add(Calendar.DAY_OF_MONTH, 1);
                    calcStartDate = calcEnd.getTime();
                }

                //cEnd.add(Calendar.DAY_OF_MONTH, 1);
                calcStartDate = cEnd.getTime();
                lastRepaymentDate = hlsCusLonContractRepayment.getPlannedDueDate();
            }
        }
//        calculateWithdrawRepayment( request,  withdrawId);
//        calculateWithdrawRepaymentIncomes( request,  withdrawId);
        calculateWithdrawRepaymentIncomes(request, withdrawId);
    }

    //获取日期之间计提总和
    private Double getSumFinCost(IRequest request, Long repaymentId, Long withdrawId) {
        Double result = 0D;
        HlsCusGldLonContractFinCost gldLonContractFinCost = new HlsCusGldLonContractFinCost();
        gldLonContractFinCost.setWithdrawId(withdrawId);
        /* gldLonContractFinCost.setRepaymentId(repaymentId);*/
        List<HlsCusGldLonContractFinCost> gldLonContractFinCostList = gldLonContractFinCostService.select(request, gldLonContractFinCost, 1, 999999);

        if (gldLonContractFinCostList.size() > 0) {
            for (HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost : gldLonContractFinCostList) {
                result = result + hlsCusGldLonContractFinCost.getFinanceIncomeInclud();

            }
        }
        return result;
    }

    /**
     * 获取两个日期之间相差天数
     *
     * @param calcStartDate
     * @param calcEndDate
     * @author zhangyu
     */
    @Override
    public Long getCalcDays(Date calcStartDate, Date calcEndDate) {
        Long result;
        Calendar calcStart = Calendar.getInstance();
        Calendar calcEnd = Calendar.getInstance();
        calcStart.setTime(calcStartDate);
        calcEnd.setTime(calcEndDate);

        result = (calcEnd.getTimeInMillis() - calcStart.getTimeInMillis()) / (1000 * 3600 * 24) + 1;

        return result;
    }

    /**
     * 获取日期所在月份的月末
     *
     * @param calcDate
     * @author zhangyu
     */
    @Override
    public Date getMonthEndDate(Date calcDate) {
        Date result = calcDate;
        int year;
        int month;
        int day;
        boolean leap;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cDate = Calendar.getInstance();
        cDate.setTime(calcDate);

        year = cDate.get(Calendar.YEAR);
        month = cDate.get(Calendar.MONTH) + 1;

        //1,3,5,7,8,10,12为31天，其余除了2月份以外为30天
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            day = 31;
        } else {
            day = 30;
        }
        //2月份闰年为29天，非闰年为28天
        if (month == 2) {
            leap = leapYear(year);
            if (leap) {
                day = 29;
            } else {
                day = 28;
            }
        }

        try {
            result = df.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 判断输入年份是否为闰年
     *
     * @param year
     * @return 是：true  否：false
     * @author zhangyu
     */
    public boolean leapYear(int year) {
        boolean leap;
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    leap = true;
                } else {
                    leap = false;
                }
            } else {
                leap = true;
            }
        } else {
            leap = false;
        }
        return leap;
    }

    /*
     * 提款计划变更创建入口
     */
    @Override
    public HlsCusLonContractWithdraw lonConWithdrawChangeReqCreate(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw) throws HlsCusException {
        lonContractWithdraw.setCreatedBy(lonContractWithdraw.getCreatedBy());
        lonContractWithdraw.setChangeTime(lonContractWithdraw.getChangeTime());
        lonContractWithdraw.setChangeType(lonContractWithdraw.getChangeType());
        if (lonContractWithdraw.getChangeReason() != "" || lonContractWithdraw.getChangeReason() != "undefined") {
            lonContractWithdraw.setChangeReason(lonContractWithdraw.getChangeReason());
        }

        hlsCusLonContractWithdrawService.updateByPrimaryKeySelective(request, lonContractWithdraw);

        // self().updateByPrimaryKeySelective(request, lonContractWithdraw);
        lonContractWithdraw = lonContractWithdrawMapper.selectByPrimaryKey(lonContractWithdraw);
        HlsCusLonContractWithdraw lonContractWithdrawChangeReq = new HlsCusLonContractWithdraw();
        Long changeReqId;//withdrawId

        if (lonContractWithdraw.getChangeReqId() != null) {
            changeReqId = lonContractWithdraw.getChangeReqId();
        } else {
            changeReqId = conFloatingRateReqLnService.copyWithdrawRepayment(request, lonContractWithdraw, "CHANGE_REQ", "", lonContractWithdraw.getWithdrawId());

            HlsCusLonContractPurpose lonContractPurpose = new HlsCusLonContractPurpose();
            lonContractPurpose.setWithdrawId(lonContractWithdraw.getWithdrawId());
            List<HlsCusLonContractPurpose> lonContractPurposeList = hlsCusLonContractPurposeService.select(request, lonContractPurpose, 1, 999999);
            if (lonContractPurposeList.size() > 0) {
                for (HlsCusLonContractPurpose lonContractPurposeNormal : lonContractPurposeList
                ) {
                    HlsCusLonContractPurpose lonContractPurposeChangeReq = new HlsCusLonContractPurpose();
                    Map<String, String> mapPurposeChangeReq = hlsBeanRefUtilService.getFieldValueMap(lonContractPurposeNormal);
                    hlsBeanRefUtilService.setFieldValue(lonContractPurposeChangeReq, mapPurposeChangeReq);
                    lonContractPurposeChangeReq.set__status(DTOStatus.ADD);
                    lonContractPurposeChangeReq.setWithdrawId(changeReqId);
                    lonContractPurposeChangeReq.setPurposeId(null);
                    hlsCusLonContractPurposeService.insertSelective(request, lonContractPurposeChangeReq);
                }
            }

            HlsCusCtLonContractOtherPurpose lonContractOtherPurpose = new HlsCusCtLonContractOtherPurpose();
            lonContractOtherPurpose.setWithdrawId(lonContractWithdraw.getWithdrawId());
            List<HlsCusCtLonContractOtherPurpose> lonContractOtherPurposeList = hlsCusCtLonContractOtherPurposeService.select(request, lonContractOtherPurpose, 1, 999999);
            if (lonContractOtherPurposeList.size() > 0) {
                for (HlsCusCtLonContractOtherPurpose lonContractOtherPurposeNormal : lonContractOtherPurposeList
                ) {
                    HlsCusCtLonContractOtherPurpose lonContractOtherPurposeChangeReq = new HlsCusCtLonContractOtherPurpose();
                    Map<String, String> mapOtherPurposeChangeReq = hlsBeanRefUtilService.getFieldValueMap(lonContractOtherPurposeNormal);
                    hlsBeanRefUtilService.setFieldValue(lonContractOtherPurposeChangeReq, mapOtherPurposeChangeReq);
                    lonContractOtherPurposeChangeReq.set__status(DTOStatus.ADD);
                    lonContractOtherPurposeChangeReq.setWithdrawId(changeReqId);
                    lonContractOtherPurposeChangeReq.setOtherPurposeId(null);
                    hlsCusCtLonContractOtherPurposeService.insertSelective(request, lonContractOtherPurposeChangeReq);
                }
            }
        }
        lonContractWithdrawChangeReq.setWithdrawId(changeReqId);
        lonContractWithdrawChangeReq = self().selectByPrimaryKey(request, lonContractWithdrawChangeReq);
        HlsCusLonContractWithdraw withdrawChangeCount = new HlsCusLonContractWithdraw();
        withdrawChangeCount.setWithdrawNumber(lonContractWithdrawChangeReq.getWithdrawNumber());
        withdrawChangeCount.setDataClass("CHANGE_REQ");
        List<HlsCusLonContractWithdraw> withdrawChangeCountList = self().select(request, withdrawChangeCount, 1, 10000);
        lonContractWithdrawChangeReq.setChangeVersionId(withdrawChangeCountList.size() + 0L);
        lonContractWithdrawChangeReq.setChangeStatus("NEW");
        lonContractWithdrawChangeReq = self().updateByPrimaryKeySelective(request, lonContractWithdrawChangeReq);

        lonContractWithdraw.set__status(DTOStatus.UPDATE);
        lonContractWithdraw.setChangeReqId(changeReqId);
        lonContractWithdraw.setWithdrawStatus("PENDING");
        self().updateByPrimaryKeySelective(request, lonContractWithdraw);

        return lonContractWithdrawChangeReq;
    }


    /*
     * 提款计划变更确认
     */
    @Override
    public void lonConWithdrawChangeReqConfirm(IRequest request, HlsCusLonContractWithdraw lonContractWithdrawChangeReq) throws HlsCusException {
        Long changeReqId = lonContractWithdrawChangeReq.getWithdrawId();
        HlsCusLonContractWithdraw lonContractWithdrawNormal = new HlsCusLonContractWithdraw();
        lonContractWithdrawNormal.setChangeReqId(lonContractWithdrawChangeReq.getWithdrawId());
        List<HlsCusLonContractWithdraw> lonContractWithdrawList = self().select(request, lonContractWithdrawNormal, 1, 999999);
        lonContractWithdrawNormal = lonContractWithdrawList.get(0);
        lonContractWithdrawNormal.setChangeVersionId(lonContractWithdrawChangeReq.getChangeVersionId());
        //复制NORMAL做HISTORY版本
        conFloatingRateReqLnService.copyWithdrawRepayment(request, lonContractWithdrawNormal, "HISTORY", "CHANGE_REQ" + changeReqId, lonContractWithdrawNormal.getWithdrawId());

        //CHANGE_REQ覆盖NORMAL
        HlsCusLonContractRepayment lonContractRepaymentDelete = new HlsCusLonContractRepayment();
        lonContractRepaymentDelete.setWithdrawId(lonContractWithdrawNormal.getWithdrawId());
        lonContractRepaymentDelete.setWriteOffFlag("NOT");
        //删除未确认还款本金
        lonContractRepaymentDelete.setCfItem(301L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认还款利息
        lonContractRepaymentDelete.setCfItem(302L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认手续费
        lonContractRepaymentDelete.setCfItem(303L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认咨询费
        lonContractRepaymentDelete.setCfItem(304L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认保证金
        lonContractRepaymentDelete.setCfItem(305L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认承销费
        lonContractRepaymentDelete.setCfItem(306L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认托管费
        lonContractRepaymentDelete.setCfItem(307L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认管理费
        lonContractRepaymentDelete.setCfItem(308L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除未确认其他费用
        lonContractRepaymentDelete.setCfItem(309L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //删除服务费
        lonContractRepaymentDelete.setCfItem(310L);
        lonContractWithdrawMapper.deleteLonConAllRepayWithCf(lonContractRepaymentDelete);
        //按计划还款日获取所有还款计划
        HlsCusLonContractRepayment lonContractRepaymentChangeReqTmp = new HlsCusLonContractRepayment();
        lonContractRepaymentChangeReqTmp.setWithdrawId(lonContractWithdrawChangeReq.getWithdrawId());
        lonContractRepaymentChangeReqTmp.setConfirmFlag("N");
        lonContractRepaymentChangeReqTmp.setWriteOffFlag("NOT");

        List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(lonContractRepaymentChangeReqTmp);
        for (HlsCusLonContractRepayment lonContractRepaymentChangeReq : lonContractRepaymentList
        ) {
            HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
            Map<String, String> mapRepaymentChangeReq = hlsBeanRefUtilService.getFieldValueMap(lonContractRepaymentChangeReq);
            hlsBeanRefUtilService.setFieldValue(lonContractRepayment, mapRepaymentChangeReq);
            lonContractRepayment.set__status(DTOStatus.ADD);
            lonContractRepayment.setWithdrawId(lonContractWithdrawNormal.getWithdrawId());
            lonContractRepayment.setRepaymentId(null);
            hlsCusLonContractRepaymentService.insertSelective(request, lonContractRepayment);
        }

        //还原NORMAL状态
        lonContractWithdrawNormal.set__status(DTOStatus.UPDATE);
        lonContractWithdrawNormal.setChangeReqId(null);
        lonContractWithdrawNormal.setWithdrawStatus("APPROVED");
        lonContractWithdrawNormal.setApprovalDate(lonContractWithdrawChangeReq.getApprovalDate());
        lonContractWithdrawNormal.setWithdrawEndDate(lonContractWithdrawChangeReq.getWithdrawEndDate());
        lonContractWithdrawNormal.setTimes(lonContractWithdrawChangeReq.getTimes());
        lonContractWithdrawNormal.setChargeFeeMethod(lonContractWithdrawChangeReq.getChargeFeeMethod());
        lonContractWithdrawNormal.setChargeFeeRate(lonContractWithdrawChangeReq.getChargeFeeRate());
        lonContractWithdrawNormal.setChargeFee(lonContractWithdrawChangeReq.getChargeFee());
        lonContractWithdrawNormal.setConsultingFee(lonContractWithdrawChangeReq.getConsultingFee());
        lonContractWithdrawNormal.setConsultingFeeRate(lonContractWithdrawChangeReq.getConsultingFeeRate());
        lonContractWithdrawNormal.setConsultingFeeMethod(lonContractWithdrawChangeReq.getConsultingFeeMethod());
        lonContractWithdrawNormal.setDeposit(lonContractWithdrawChangeReq.getDeposit());
        lonContractWithdrawNormal.setDepositRate(lonContractWithdrawChangeReq.getDepositRate());
        lonContractWithdrawNormal.setDepositMethod(lonContractWithdrawChangeReq.getDepositMethod());
        lonContractWithdrawNormal.setCollocationFee(lonContractWithdrawChangeReq.getCollocationFee());
        lonContractWithdrawNormal.setCollocationFeeRate(lonContractWithdrawChangeReq.getCollocationFeeRate());
        lonContractWithdrawNormal.setCollocationFeeMethod(lonContractWithdrawChangeReq.getCollocationFeeMethod());
        lonContractWithdrawNormal.setConsignmentSalesFee(lonContractWithdrawChangeReq.getConsignmentSalesFee());
        lonContractWithdrawNormal.setConsignmentSalesFeeRate(lonContractWithdrawChangeReq.getConsignmentSalesFeeRate());
        lonContractWithdrawNormal.setConsignmentSalesFeeMethod(lonContractWithdrawChangeReq.getConsignmentSalesFeeMethod());
        lonContractWithdrawNormal.setManagementFee(lonContractWithdrawChangeReq.getManagementFee());
        lonContractWithdrawNormal.setManagementFeeRate(lonContractWithdrawChangeReq.getManagementFeeRate());
        lonContractWithdrawNormal.setManagementFeeMethod(lonContractWithdrawChangeReq.getManagementFeeMethod());
        lonContractWithdrawNormal.setOtherFee(lonContractWithdrawChangeReq.getOtherFee());
        lonContractWithdrawNormal.setOtherFeeMethod(lonContractWithdrawChangeReq.getOtherFeeMethod());
        lonContractWithdrawNormal.setOtherFeeRate(lonContractWithdrawChangeReq.getOtherFeeRate());

        self().updateByPrimaryKey(request, lonContractWithdrawNormal);


        HlsCusLonContractQuotation quotationChangeReq = new HlsCusLonContractQuotation();
        quotationChangeReq.setWithdrawId(changeReqId);
        List<HlsCusLonContractQuotation> cusLonContractQuotationsChange = hlsCusLonContractQuotationMapper.lonContractQuotationById(quotationChangeReq);

        quotationChangeReq = cusLonContractQuotationsChange.get(0);

        HlsCusLonContractQuotation quotationNormal = new HlsCusLonContractQuotation();
        quotationNormal.setWithdrawId(lonContractWithdrawNormal.getWithdrawId());
        List<HlsCusLonContractQuotation> cusLonContractQuotationsNormal = hlsCusLonContractQuotationMapper.lonContractQuotationById(quotationNormal);

        quotationNormal = cusLonContractQuotationsNormal.get(0);

        Long quotationId = quotationNormal.getQuotationId();
        Map<String, String> mapQuotation = hlsBeanRefUtilService.getFieldValueMap(quotationChangeReq);
        hlsBeanRefUtilService.setFieldValue(quotationNormal, mapQuotation);

        quotationNormal.setQuotationId(quotationId);
        quotationNormal.setWithdrawId(lonContractWithdrawNormal.getWithdrawId());
        quotationNormal.setContractId(lonContractWithdrawNormal.getContractId());
        hlsCusLonContractQuotationService.updateByPrimaryKey(request, quotationNormal);

        //删除提款下的账户信息
        HlsCusCtLonContractBankAccount lonContractBankAccountDelete = new HlsCusCtLonContractBankAccount();
        lonContractBankAccountDelete.setWithdrawId(lonContractWithdrawNormal.getWithdrawId());
        ctLonContractBankAccountMapper.deleteBankAccountByWithdrawId(lonContractBankAccountDelete);

        //复制提款下的账户信息
        HlsCusCtLonContractBankAccount lonContractBankAccount = new HlsCusCtLonContractBankAccount();
        lonContractBankAccount.setWithdrawId(changeReqId);
        List<HlsCusCtLonContractBankAccount> bankAccountList = hlsCusCtLonContractBankAccountService.select(request, lonContractBankAccount, 1, 999999);
        if (bankAccountList.size() > 0) {
            for (HlsCusCtLonContractBankAccount lonContractBankAccountSource : bankAccountList) {
                HlsCusCtLonContractBankAccount lonContractBankAccountCopy = new HlsCusCtLonContractBankAccount();
                Map<String, String> mapRepaymentNormal = hlsBeanRefUtilService.getFieldValueMap(lonContractBankAccountSource);
                hlsBeanRefUtilService.setFieldValue(lonContractBankAccountCopy, mapRepaymentNormal);
                lonContractBankAccountCopy.set__status("add");
                lonContractBankAccountCopy.setWithdrawId(lonContractWithdrawNormal.getWithdrawId());
                hlsCusCtLonContractBankAccountService.insertSelective(request, lonContractBankAccountCopy);
            }
        }

        //删除提款下的附件信息
        HlsCusLonContractAttachment hlsCusLonContractAttachmentDelete = new HlsCusLonContractAttachment();
        hlsCusLonContractAttachmentDelete.setSourceId(lonContractWithdrawNormal.getWithdrawId());
        hlsCusLonContractAttachmentMapper.deleteAttachmentBySourceIdAndType(hlsCusLonContractAttachmentDelete);

        //复制提款下的附件信息
        HlsCusLonContractAttachment hlsCusLonContractAttachment = new HlsCusLonContractAttachment();
        hlsCusLonContractAttachment.setSourceId(changeReqId);
        List<HlsCusLonContractAttachment> hlsCusLonContractAttachmentList = hlsCusLonContractAttachmentMapper.select(hlsCusLonContractAttachment);
        if (hlsCusLonContractAttachmentList.size() > 0) {
            for (HlsCusLonContractAttachment dt : hlsCusLonContractAttachmentList) {
                HlsCusLonContractAttachment lonContractAttachmentCopy = new HlsCusLonContractAttachment();
                Map<String, String> mapAttachmentNormal = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(lonContractAttachmentCopy, mapAttachmentNormal);
                lonContractAttachmentCopy.set__status("add");
                lonContractAttachmentCopy.setSourceId(lonContractWithdrawNormal.getWithdrawId());
                hlsCusLonContractAttachmentMapper.insertSelective(lonContractAttachmentCopy);
                //复制系统附件表
                copyFndAtmFile(request, String.valueOf(dt.getContractAttachmentId()), String.valueOf(lonContractAttachmentCopy.getContractAttachmentId()), "LON_CONTRACT_ATTACHMENT");
            }
        }
    }

    //复制系统附件表
    void copyFndAtmFile(IRequest iRequest, String oldPkValue, String newPkValue, String tableName) throws HlsCusException {

        if (org.apache.commons.lang3.StringUtils.isEmpty(oldPkValue) || org.apache.commons.lang3.StringUtils.isEmpty(tableName) || org.apache.commons.lang3.StringUtils.isEmpty(newPkValue)) {
            throw new HlsCusException("数据异常，请联系管理员!");
        }

        //复制fnd_atm_attachment_multi
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTableName(tableName);
        fndAttachmentMulti.setTablePkValue(oldPkValue);

        List<FndAttachmentMulti> multiList = fndAttachmentMultiMapper.select(fndAttachmentMulti);
        if (CollectionUtils.isNotEmpty(multiList)) {
            for (FndAttachmentMulti multi : multiList) {

                FndAttachmentMulti attachmentMulti = new FndAttachmentMulti();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(multi);

                hlsBeanRefUtilService.setFieldValue(attachmentMulti, map);
                attachmentMulti.setTablePkValue(newPkValue);
                iFndAttachmentMultiService.insertSelective(iRequest, attachmentMulti);

                //复制fnd_atm_attachment
                FndAttachment fndAttachment = new FndAttachment();
                fndAttachment.setAttachmentId(multi.getAttachmentId());
                fndAttachment = fndAttachmentMapper.selectByPrimaryKey(fndAttachment);

/*
                fndAttachment.setAttachmentId(attachmentMulti.getAttachmentId());
*/
                fndAttachment.setAttachmentId(null);
                iFndAttachmentService.insert(iRequest, fndAttachment);

                attachmentMulti.setAttachmentId(fndAttachment.getAttachmentId());
                iFndAttachmentMultiService.updateByPrimaryKeySelective(iRequest, attachmentMulti);
            }
        }

    }

    /*
     * 提款计划变更取消
     */
    @Override
    public void lonContractWithdrawChangeReqCancel(IRequest request, HlsCusLonContractWithdraw lonContractWithdrawChangeReq) {
        lonContractWithdrawChangeReq = self().selectByPrimaryKey(request, lonContractWithdrawChangeReq);
        lonContractWithdrawChangeReq.setWithdrawStatus("CANCEL");
        lonContractWithdrawChangeReq.setChangeStatus("CANCEL");
        self().updateByPrimaryKey(request, lonContractWithdrawChangeReq);
        HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
        lonContractWithdraw.setWithdrawNumber(lonContractWithdrawChangeReq.getWithdrawNumber());
        lonContractWithdraw.setDataClass("NORMAL");
        List<HlsCusLonContractWithdraw> lonContractWithdrawList = self().select(request, lonContractWithdraw, 1, 999999);

        lonContractWithdraw = lonContractWithdrawList.get(0);

        lonContractWithdraw.set__status(DTOStatus.UPDATE);
        lonContractWithdraw.setWithdrawStatus("APPROVED");
        lonContractWithdraw.setChangeReqId(null);
        self().updateByPrimaryKey(request, lonContractWithdraw);
    }

    @Override
    public List<HlsCusLonContractWithdraw> queryDebtStructureChart(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("companyId", lonContractWithdraw.getCompanyId());
        params.put("minAmt", lonContractWithdraw.getMinAmt());
        params.put("maxAmt", lonContractWithdraw.getMaxAmt());
        params.put("minLeaseTerm", lonContractWithdraw.getMinLeaseTerm());
        params.put("maxLeaseTerm", lonContractWithdraw.getMaxLeaseTerm());
        List<HlsCusLonContractWithdraw> list = lonContractWithdrawMapper.queryDebtStructureChart(params);
        Double totalAmount = 0D;
        for (HlsCusLonContractWithdraw dt : list) {
            totalAmount = totalAmount + dt.getDueAmount();
        }
        for (HlsCusLonContractWithdraw dt : list) {
            dt.setPartPercent(dt.getDueAmount() / totalAmount);
        }
        HlsCusLonContractWithdraw lonWithdraw = new HlsCusLonContractWithdraw();
        lonWithdraw.setWithdrawId(-1L);
        lonWithdraw.setDueAmount(totalAmount);
        if (CollectionUtils.isNotEmpty(list)) {
            list.add(lonWithdraw);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> queryFoundUseOfProject(IRequest request, Map<String, Object> map) {
        PageHelper.startPage((int) map.get("page"), (int) map.get("pageSize"));
        return lonContractWithdrawMapper.queryFoundUseOfProject(map);
    }

    @Override
    public List<Map<String, Object>> queryFoundUseOfWithdraw(IRequest request, Map<String, Object> map) {
        PageHelper.startPage((int) map.get("page"), (int) map.get("pageSize"));
        return lonContractWithdrawMapper.queryFoundUseOfWithdraw(map);
    }

    @Override
    public List<Map<String, Object>> queryDebtReport(IRequest request, Map<String, Object> map) {
        PageHelper.startPage((int) map.get("page"), (int) map.get("pageSize"));
        return lonContractWithdrawMapper.queryDebtReport(map);
    }

    @Override
    public List<HlsCusLonContractWithdraw> queryLonWithdrawInfo(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return lonContractWithdrawMapper.queryLonWithdrawInfo(lonContractWithdraw);
    }

    @Override
    public List<HlsCusLonContractWithdraw> queryLonWithdrawDetailInfo(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, Integer page, Integer pageSize) {
        if (page != null && pageSize != null) {
            PageHelper.startPage(page, pageSize);
        }
        return lonContractWithdrawMapper.queryLonWithdrawDetailInfo(lonContractWithdraw);
    }

    /**
     * 比较日期相差的天数
     *
     * @param start
     * @param from
     * @return
     */
    private static int compareDays(Date start, Date from) {
        if (null == start || null == from) {
            return -1;
        }
        long intervalMilli = Math.abs(start.getTime() - from.getTime());//取绝对值
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }

    public Double transfor(Double amount) {
        BigDecimal bg = new BigDecimal(amount);
        double num = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }


    @Autowired
    private HlsCusLonContractQuotationMapper lonContractQuotationMapper;
    @Autowired
    private HlsCusLonContractPurposeMapper lonContractPurposeMapper;

    @Autowired
    private HlsCusPrjProjectAttachmentMapper attachmentMapper;

    @Override
    public void batchDeleteWithdraw(IRequest request, List<HlsCusLonContractWithdraw> lonContractWithdraws) {
        for (HlsCusLonContractWithdraw lonContractWithdraw : lonContractWithdraws) {
            //删除货款用途
            HlsCusLonContractPurpose purpose = new HlsCusLonContractPurpose();
            purpose.setWithdrawId(lonContractWithdraw.getWithdrawId());
            lonContractPurposeMapper.delete(purpose);

            //删除还款付款计划
            HlsCusLonContractRepayment repayment = new HlsCusLonContractRepayment();
            repayment.setWithdrawId(lonContractWithdraw.getWithdrawId());
            hlsCusLonContractRepaymentMapper.delete(repayment);

            //删除附件
            HlsCusPrjProjectAttachment attachment = new HlsCusPrjProjectAttachment();
            attachment.setProjectId(lonContractWithdraw.getWithdrawId());
            attachment.setProjectAttachmentCategory("LON_CONTRACT_WITHDRAW");
            attachmentMapper.delete(attachment);

            //删除报价信息
            HlsCusLonContractQuotation quotation = new HlsCusLonContractQuotation();
            quotation.setWithdrawId(lonContractWithdraw.getWithdrawId());
            lonContractQuotationMapper.delete(quotation);

            //删除提款头
            lonContractWithdrawMapper.deleteByPrimaryKey(lonContractWithdraw);
        }
    }


    /**
     * 融资提款-还款计划行计算逻辑
     *
     * @param iRequest
     * @param hlsCusLonContractRepaymentList
     * @return
     */
//    @Override
//    public List<HlsCusLonContractRepayment> calcLoanRepaymentCashflow(IRequest iRequest, List<HlsCusLonContractRepayment>
//            hlsCusLonContractRepaymentList) throws HlsCusException {
//        //处理NPE问题
//        if (CollectionUtils.isEmpty(hlsCusLonContractRepaymentList)) {
//            throw new HlsCusException("融资提款还款计划不能为空");
//        }
//        //获取融资提款
//        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
//        hlsCusLonContractWithdraw.setWithdrawId(hlsCusLonContractRepaymentList.get(0).getWithdrawId());
//        hlsCusLonContractWithdraw = self().selectByPrimaryKey(iRequest, hlsCusLonContractWithdraw);
//        //执行保存逻辑
//        for (HlsCusLonContractRepayment lonContractRepayment : hlsCusLonContractRepaymentList) {
//            if (lonContractRepayment.getPlannedDueDate().before(hlsCusLonContractWithdraw.getDueDate())) {
//                throw new HlsCusException("还款日期【" + HlsCusConstant.sdf.format(lonContractRepayment.getPlannedDueDate()) + "】不能小于提款起始日期【" +
//                        HlsCusConstant.sdf.format(hlsCusLonContractWithdraw.getDueDate()) + "】");
//            }
//            if (lonContractRepayment.getPlannedDueDate().after(hlsCusLonContractWithdraw.getWithdrawEndDate())) {
//                throw new HlsCusException("还款日期【" + HlsCusConstant.sdf.format(lonContractRepayment.getPlannedDueDate())
//                        + "】不能大于计划到期日期【" + HlsCusConstant.sdf.format(hlsCusLonContractWithdraw.getWithdrawEndDate()) + "】");
//            }
//            hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
//        }
//
//        //校验本金的金额和利息的金额
//        //还款计划总金额
//        Double planAmountSum = hlsCusLonContractRepaymentMapper.selectPlanAmountSum(hlsCusLonContractWithdraw.getWithdrawId(), "Y");
//
//        if (planAmountSum > hlsCusLonContractWithdraw.getDueAmount()) {
//            throw new HlsCusException("还款本金之和【" + planAmountSum + "】不等于交易信息的实际提款金额【" + hlsCusLonContractWithdraw.getDueAmount() + "】，请调整");
//        }
//
//        //获取提款报价
//        HlsCusLonContractQuotation hlsCusLonContractQuotationTemp = new HlsCusLonContractQuotation();
//        hlsCusLonContractQuotationTemp.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
//        List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationService.select(iRequest, hlsCusLonContractQuotationTemp, 1, 9999);
//
//
//        updateAllPrincipalOutStd(iRequest, hlsCusLonContractWithdraw, "Y", null);
//        //重新对期数进行排序
//
//        //处理杂项费用，目前处理为每次计算都会重算，先删除之前存在的现金流，重新插入0期现金流
//        calcWithdrawOtherFees(iRequest, hlsCusLonContractWithdraw);
//
//        //计算XIRR
//        calXirr(iRequest, hlsCusLonContractWithdraw, hlsCusLonContractQuotations.get(0));
//        //将期数排序
//        sortContractRepayment(iRequest, hlsCusLonContractWithdraw);
//
//        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
//        lonContractRepayment.setWithdrawId(hlsCusLonContractRepaymentList.get(0).getWithdrawId());
//        return hlsCusLonContractRepaymentService.selectList(iRequest, lonContractRepayment, null, null);
//    }

    /**
     * 计算提款现金流
     *
     * @param withdrawId
     */
//    private List<Map> calculate(Long withdrawId) {
//        //step1:用实率现金流算出一个实际利率
//        //step2:然后用这个实际利率重新算每一期的实率利息，再用租金减实率利息=实率本金
//        // step3:实率利息=剩余本金*（实际年利率/频率）
//        final List<Map> total = new ArrayList<>();
//        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
//        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
//        hlsCusLonContractWithdraw.setWithdrawId(withdrawId);
//        hlsCusLonContractWithdraw = lonContractWithdrawMapper.selectOne(hlsCusLonContractWithdraw);
//        lonContractRepayment.setCfItem(310L);
//        lonContractRepayment.setContractId(hlsCusLonContractWithdraw.getContractId());
//        lonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
//        lonContractRepayment.setSortname("times");
//        lonContractRepayment.setSortorder("asc");
//        List<HlsCusLonContractRepayment> repaymentList = hlsCusLonContractRepaymentMapper.select(lonContractRepayment);
//        if (repaymentList == null || repaymentList.size() == 0) {
//            throw new IllegalArgumentException("未找到对应的现金流");
//        }
//        Double vatRate = hlsCusLonContractWithdraw.getIntRate();
//        Double vatRateParam = CalculateUtil.add(vatRate != null ? vatRate : 0D, 1D);
//        //按实率分摊
//        //Long payTimes = contract.getAnnualPayTimes();
//        Double finaceAmount1 = hlsCusLonContractRepaymentMapper.chargeAmountSum(lonContractRepayment);
//        Double finaceAmount = hlsCusLonContractWithdraw.getDueAmount();
//        final List<Double> irrArray = new ArrayList<>();
//        irrArray.add(CalculateUtil.sub(0D, finaceAmount));
//        HlsCusLonContractRepayment lonContractRepayment1 = new HlsCusLonContractRepayment();
//        lonContractRepayment1.setContractId(hlsCusLonContractWithdraw.getContractId());
//        lonContractRepayment1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
//        lonContractRepayment1.setSortname("times");
//        lonContractRepayment1.setSortorder("asc");
//        List<HlsCusLonContractRepayment> repaymentList1 = hlsCusLonContractRepaymentMapper.select(lonContractRepayment1);
////        repaymentList1.forEach(item -> {
////                irrArray.add(item.getPlannedDueAmount());
////            });
////            Double irr = IrrUtil.irr(irrArray);//irr利率
//        double[] payments = new double[repaymentList1.size() + 1];
//        Date[] dates = new Date[repaymentList1.size() + 1];
//        for (int i = 0; i < repaymentList1.size(); i++) {
//            if ("OUTFLOW".equalsIgnoreCase(repaymentList1.get(i).getCfDirection())) {
//                payments[i] = -repaymentList1.get(i).getPlannedDueAmount();
//                dates[i] = repaymentList1.get(i).getPlannedDueDate();
//            } else {
//                payments[i] = repaymentList1.get(i).getPlannedDueAmount();
//                dates[i] = repaymentList1.get(i).getPlannedDueDate();
//            }
//        }
//        //防止精度问题
//        payments[repaymentList1.size()] = hlsCusLonContractWithdraw.getDueAmount();
//        dates[repaymentList1.size()] = hlsCusLonContractWithdraw.getDueDate();
//        Double xirr = HlsCusXirr.Newtons_method(0.1, payments, dates);
//        Double financeAmountCopy = new Double(finaceAmount1.doubleValue());
//        for (int i = 0; i < repaymentList.size(); i++) {
//            HlsCusLonContractRepayment item = repaymentList.get(i);
//            Map map = new HashMap<>();
//            Double erInterest = null;
//            Double erPrincipal = null;
//            Double interestBalance = null;
//            Double vatIncome = null;
//            Long times = item.getTimes();
//            Long repaymentId = item.getRepaymentId();
//            if (i + 1 == repaymentList.size()) {
//                Double interest = CalculateUtil.sub(item.getPlannedDueAmount(), financeAmountCopy);
//                if (repaymentList.size() == 1)
//                    erPrincipal = finaceAmount;
//                else
//                    erPrincipal = (Double) total.get(i - 1).get("interestBalance");
//                interestBalance = 0D;
//                erInterest = CalculateUtil.sub(interest, CalculateUtil.mul(CalculateUtil.div(interest, vatRateParam), vatRate));
//                vatIncome = CalculateUtil.sub(interest, erInterest);
//                financeAmountCopy = interestBalance;
//
//            } else {
//                Double interest = CalculateUtil.mul(xirr, financeAmountCopy);
//                erInterest = CalculateUtil.sub(interest, CalculateUtil.mul(CalculateUtil.div(interest, vatRateParam), vatRate));
//                erPrincipal = CalculateUtil.sub(item.getPlannedDueAmount(), interest);
//                interestBalance = CalculateUtil.sub(financeAmountCopy, erPrincipal);
//                vatIncome = CalculateUtil.sub(interest, erInterest);
//                financeAmountCopy = interestBalance;
//            }
//            map.put("erInterest", erInterest);
//            map.put("erPrincipal", erPrincipal);
//            map.put("interestBalance", interestBalance);
//            map.put("vatIncome", new BigDecimal(vatIncome));
//            map.put("times", times);
//            map.put("cfItem", repaymentList.get(i).getCfItem());
//            map.put("repaymentId", repaymentId);
//            map.put("dueDate", item.getPlannedDueDate());
//            map.put("contractId", hlsCusLonContractWithdraw.getContractId());
//            map.put("withdrawId", withdrawId);
//            total.add(map);
//        }
//        return total;
//    }
    //按实率分摊手续费
    public void calculateWithdrawRepaymentIncomes(IRequest request, Long withdrawId) {
        //融资手续费没有重新分摊
        HlsCusWithdrawRepayment hlsCusWithdrawRepaymentTemp = new HlsCusWithdrawRepayment();
        hlsCusWithdrawRepaymentTemp.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LON_SERVICE_CF_ITEM);
        hlsCusWithdrawRepaymentTemp.setWithdrawId(withdrawId);
        List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentTempList = hlsCusWithdrawRepaymentMapper.select(hlsCusWithdrawRepaymentTemp);
        if (hlsCusWithdrawRepaymentTempList.size() == 0) {
            Calendar cStart = (Calendar) calendar.clone();
            Calendar cEnd = (Calendar) calendar.clone();
            Double cashflowAmount = 0D;
            //期初摊余成本
            Double amortizedCost = 0D;
            //利息及融资费用
            Double interestFinancingFee = 0D;
            //合同利息
            Double contractInterest = 0D;
            //分摊费用
            Double financeIncome = INIT_ZERO;

            //找到提款
            HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
            hlsCusLonContractWithdraw.setWithdrawId(withdrawId);
            hlsCusLonContractWithdraw = lonContractWithdrawMapper.selectOne(hlsCusLonContractWithdraw);
            int incomeTimes = getMonthSpace(hlsCusLonContractWithdraw.getDueDate(), hlsCusLonContractWithdraw.getWithdrawEndDate());//分摊次数
            //找到手续费现金流
            HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
            lonContractRepayment.setContractId(hlsCusLonContractWithdraw.getContractId());
            lonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            lonContractRepayment.setSortname("times");
            lonContractRepayment.setSortorder("asc");
            //手续费总额
            Double finaceAmount1 = hlsCusLonContractRepaymentMapper.chargeAmountSum(lonContractRepayment);
            if (finaceAmount1.compareTo(INIT_ZERO) == 0) {
                return;
            }
            //计算剩余本金
            //Double remainingPrincipal=
            //新建数组，保存剩余本金
            List<Double> remainingPrincipalArray = new ArrayList<>();
            //新建数组，保存计算IRR的数据
            List<Double> irrArray = new ArrayList<>();
            for (int i = 0; i < incomeTimes; i++) {
                Date from;//日期从
                Date to;//日期到
                cStart.clear();
                cEnd.clear();
                from = hlsCusLonContractWithdraw.getDueDate();
                cStart.setTime(from);
                cStart.add(Calendar.MONTH, i);
                if (i > 0) {
                    cStart.setTime(from);
                    cStart.add(Calendar.MONTH, i);
                    cStart.add(Calendar.DAY_OF_MONTH, -cStart.get(Calendar.DAY_OF_MONTH) + 1);
                }
//
//                cEnd.setTime(cStart.getTime());
//                cEnd.add(Calendar.MONTH, 1);
//                cEnd.add(Calendar.DAY_OF_MONTH, -1);
                cEnd.setTime(cStart.getTime());
                cEnd.add(Calendar.DAY_OF_MONTH, cStart.getActualMaximum(Calendar.DAY_OF_MONTH) - cStart.get(Calendar.DAY_OF_MONTH));
                if (i + 1 == incomeTimes) {
                    //  cStart.clear();
                    cEnd.clear();
                    //  from = hlsCusLonContractWithdraw.getWithdrawEndDate();
                    //   cStart.setTime(from);
                    cEnd.setTime(hlsCusLonContractWithdraw.getWithdrawEndDate());
                    //end.add(Calendar.DAY_OF_MONTH,-1);
                }
                if (cEnd.getTime().getTime() >= hlsCusLonContractWithdraw.getWithdrawEndDate().getTime() && i + 1 != incomeTimes) {
                    cEnd.clear();
                    cEnd.setTime(hlsCusLonContractWithdraw.getWithdrawEndDate());
                    cEnd.add(Calendar.DAY_OF_MONTH, -1);
                }
                Double remainingPrincipal = CalculateUtil.sub(hlsCusLonContractWithdraw.getDueAmount(), hlsCusLonContractRepaymentMapper.selectRemainingPrincipalAmountSum(hlsCusLonContractWithdraw.getWithdrawId(), cStart.getTime()));
                //Double remainingPrincipal=hlsCusLonContractRepaymentMapper.selectRemainingPrincipalAmountSum(hlsCusLonContractWithdraw.getWithdrawId(), start.getTime());
                Double chargePriAmount = hlsCusLonContractRepaymentMapper.selectAmountSum(hlsCusLonContractWithdraw.getWithdrawId(), cStart.getTime(), cEnd.getTime());
                if (i == 0) {
                    contractInterest = INIT_ZERO;

                    cashflowAmount = CalculateUtil.sub(CalculateUtil.sub(hlsCusLonContractWithdraw.getDueAmount(), chargePriAmount), contractInterest);
                } else {
                    contractInterest = transforIncome(CalculateUtil.div(CalculateUtil.mul(remainingPrincipal, hlsCusLonContractWithdraw.getIntRate()), 12D));

                    cashflowAmount = CalculateUtil.sub(CalculateUtil.sub(INIT_ZERO, chargePriAmount), contractInterest);

                }
                irrArray.add(cashflowAmount);
                //插入分摊临时表
                HlsCusWithdrawRepayment hlsCusWithdrawRepayment = new HlsCusWithdrawRepayment();
                hlsCusWithdrawRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                hlsCusWithdrawRepayment.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LON_SERVICE_CF_ITEM);
                hlsCusWithdrawRepayment.setFinIncomeDate(cStart.getTime());
                hlsCusWithdrawRepayment.setCashflowAmount(cashflowAmount);
                hlsCusWithdrawRepayment.setRemainingPrincipal(remainingPrincipal);
                hlsCusWithdrawRepayment.setContractInterest(contractInterest);
                hlsCusWithdrawRepayment.setContractId(hlsCusLonContractWithdraw.getContractId());
                hlsCusWithdrawRepaymentMapper.insert(hlsCusWithdrawRepayment);
            }
            //计算irr
            Double irr = IrrUtil.irr(irrArray) * YEAR_MONTH;//irr利率
            if ("NaN".equals(irr.toString())) {
                irr = 0D;
            }
//计算期初摊余成本&利息及融资费用
            HlsCusWithdrawRepayment hlsCusWithdrawRepayment1 = new HlsCusWithdrawRepayment();
            hlsCusWithdrawRepayment1.setContractId(hlsCusLonContractWithdraw.getContractId());
            hlsCusWithdrawRepayment1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            hlsCusWithdrawRepayment1.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LON_SERVICE_CF_ITEM);
            hlsCusWithdrawRepayment1.setSortname("finIncomeDate");
            hlsCusWithdrawRepayment1.setSortorder("asc");
            List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentList = hlsCusWithdrawRepaymentMapper.select(hlsCusWithdrawRepayment1);
            for (int i = 0; i < hlsCusWithdrawRepaymentList.size(); i++) {
                Calendar tempDate = (Calendar) calendar.clone();

                HlsCusWithdrawRepayment withdrawRepayment = hlsCusWithdrawRepaymentList.get(i);
                tempDate.clear();
                tempDate.setTime(withdrawRepayment.getFinIncomeDate());
                if (i < hlsCusWithdrawRepaymentList.size() - 1) {
                    tempDate.add(Calendar.MONTH, 1);
                    tempDate.add(Calendar.DAY_OF_MONTH, -1);
                }
                if (tempDate.getTime().getTime() >= hlsCusLonContractWithdraw.getWithdrawEndDate().getTime() && i + 1 != hlsCusWithdrawRepaymentList.size()) {
                    tempDate.clear();
                    tempDate.setTime(hlsCusLonContractWithdraw.getWithdrawEndDate());
                    tempDate.add(Calendar.DAY_OF_MONTH, -1);
                }
                Double chargePriAmount = hlsCusLonContractRepaymentMapper.selectAmountSum(hlsCusLonContractWithdraw.getWithdrawId(), withdrawRepayment.getFinIncomeDate(), tempDate.getTime());
                if (i == 0) {
                    amortizedCost = CalculateUtil.sub(hlsCusLonContractWithdraw.getDueAmount(), chargePriAmount);
                    interestFinancingFee = INIT_ZERO;
                } else {
                    interestFinancingFee = transforIncome(CalculateUtil.div(CalculateUtil.mul(hlsCusWithdrawRepaymentList.get(i - 1).getAmortizedCost(), irr), 12D));
                    amortizedCost = CalculateUtil.sub(CalculateUtil.sub(CalculateUtil.add(hlsCusWithdrawRepaymentList.get(i - 1).getAmortizedCost(), interestFinancingFee), withdrawRepayment.getContractInterest()), chargePriAmount);
                }
                if (i + 1 == hlsCusWithdrawRepaymentList.size()) {
                    withdrawRepayment.setFinanceIncome(CalculateUtil.sub(finaceAmount1, financeIncome));
                } else {
                    withdrawRepayment.setFinanceIncome(transforIncome(CalculateUtil.sub(interestFinancingFee, withdrawRepayment.getContractInterest())));
                }
                financeIncome = CalculateUtil.add(CalculateUtil.sub(interestFinancingFee, withdrawRepayment.getContractInterest()), financeIncome);
                //   withdrawRepayment.setFinanceIncome(irr);
                withdrawRepayment.setAmortizedCost(amortizedCost);
                withdrawRepayment.setInterestFinancingFee(interestFinancingFee);

                withdrawRepayment.setIrr(irr);
                hlsCusWithdrawRepaymentMapper.updateByPrimaryKey(withdrawRepayment);
            }
            //Double temp =CalculateUtil.div(3D,0D);
            //更新到分摊表
            HlsCusWithdrawRepayment hlsCusWithdrawRepaymentFinCost = new HlsCusWithdrawRepayment();
            hlsCusWithdrawRepaymentFinCost.setContractId(hlsCusLonContractWithdraw.getContractId());
            hlsCusWithdrawRepaymentFinCost.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            hlsCusWithdrawRepaymentFinCost.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LON_SERVICE_CF_ITEM);

            List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentFinCostList = hlsCusWithdrawRepaymentMapper.select(hlsCusWithdrawRepaymentFinCost);
            Calendar finStart = (Calendar) calendar.clone();
            Calendar finEnd = (Calendar) calendar.clone();
            String periodName;
            for (int i = 1; i < hlsCusWithdrawRepaymentFinCostList.size(); i++) {
                Long Days = 0L;
                finEnd.clear();
                finEnd.setTime(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost = new HlsCusGldLonContractFinCost();
                hlsCusGldLonContractFinCost.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
                hlsCusGldLonContractFinCost.setContractId(hlsCusLonContractWithdraw.getContractId());
                hlsCusGldLonContractFinCost.setFinanceCost(hlsCusWithdrawRepaymentFinCostList.get(i).getFinanceIncome());
                hlsCusGldLonContractFinCost.setPostFlag("N");
                hlsCusGldLonContractFinCost.setCfItem(HlsCusConstant.CASHFLOW_ITEM.LON_SERVICE_CF_ITEM);
                hlsCusGldLonContractFinCost.setSourceType("LON_CONTRACT");
                hlsCusGldLonContractFinCost.setCompanyId(hlsCusLonContractWithdraw.getCompanyId());
                if (finEnd.get(Calendar.MONTH) + 1 < 10) {
                    periodName = finEnd.get(Calendar.YEAR) + "-0" + (finEnd.get(Calendar.MONTH) + 1);
                } else {
                    periodName = finEnd.get(Calendar.YEAR) + "-" + (finEnd.get(Calendar.MONTH) + 1);
                }
                hlsCusGldLonContractFinCost.setPeriodName(periodName);
                if (i == 1) {
                    hlsCusGldLonContractFinCost.setStartDate(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    finEnd.setTime(hlsCusWithdrawRepaymentFinCostList.get(i + 1).getFinIncomeDate());
                    finEnd.add(Calendar.DAY_OF_MONTH, -1);
                    hlsCusGldLonContractFinCost.setEndDate(finEnd.getTime());
                    Days = getCalcDays(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate(), finEnd.getTime());
                    hlsCusGldLonContractFinCost.setDays(Days);
                } else {
                    finStart.clear();
                    finStart.setTime(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    //   finStart.add(Calendar.DAY_OF_MONTH, 1);
                    hlsCusGldLonContractFinCost.setStartDate(finStart.getTime());
                    if (i + 1 == hlsCusWithdrawRepaymentFinCostList.size()) {
                        finEnd.setTime(hlsCusLonContractWithdraw.getWithdrawEndDate());
                        finEnd.add(Calendar.DAY_OF_MONTH, -1);
                    } else {
                        finEnd.setTime(hlsCusWithdrawRepaymentFinCostList.get(i + 1).getFinIncomeDate());
                        finEnd.add(Calendar.DAY_OF_MONTH, -1);

                    }
                    hlsCusGldLonContractFinCost.setEndDate(finEnd.getTime());
                    Days = getCalcDays(finStart.getTime(), finEnd.getTime());
                    hlsCusGldLonContractFinCost.setDays(Days);
                }
                gldLonContractFinCostService.insertSelective(request, hlsCusGldLonContractFinCost);
            }
            HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost1 = new HlsCusGldLonContractFinCost();
            hlsCusGldLonContractFinCost1.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            hlsCusGldLonContractFinCost1.setContractId(hlsCusLonContractWithdraw.getContractId());
            hlsCusGldLonContractFinCost1.setCfItem(310L);
            List<HlsCusGldLonContractFinCost> t = gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCost1, 1, 999999999);
        }
    }


//计算分摊次数

    /**
     * @param date1 <Date>
     * @param date2 <Date>
     * @return int
     */
    public int getMonthSpace(Date date1, Date date2) {

        int result = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(date1);
        c2.setTime(date2);

        int yearInterval = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        if (c2.get(Calendar.MONTH) < c1.get(Calendar.MONTH))
            yearInterval--;
        int monthInterval = (c2.get(Calendar.MONTH) + 12) - c1.get(Calendar.MONTH);
//        if (c2.get(Calendar.DAY_OF_MONTH) > c1.get(Calendar.DAY_OF_MONTH)) monthInterval++;
        monthInterval %= 12;
        return yearInterval * 12 + monthInterval + 1;


    }

    public Double transforIncome(Double amount) {
        BigDecimal bg = new BigDecimal(amount);
        double num = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }

    //计算下一期还款日或者还款计算日
    private Date getNextRepaymentDateTmp(Date dateFrom, String interestCycle, String flag, String interestMonth, Long times) {
        Date resultDate;
        Date temp;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFrom);//设置起时间
        int RepaymentMonth = cal.get(Calendar.MONTH) + 1;

        if ("YEAR".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.YEAR, new Long(1 * times).intValue());

//            int interestMonthTemp = Integer.valueOf(interestMonth).intValue();
//            if (RepaymentMonth > interestMonthTemp) {
//                cal.add(Calendar.YEAR, 1);
//            } else {
//                cal.add(Calendar.YEAR, 0);
//            }
//            if (flag.equals("N")) {
//                cal.add(Calendar.YEAR, 1);
//            }


        } else if ("HALF_A_YEAR".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, new Long(6 * times).intValue());
        } else if ("QUARTER".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, new Long(3 * times).intValue());
        } else if ("MONTH".equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, new Long(1 * times).intValue());
        }
        resultDate = cal.getTime();
        return resultDate;
    }

    public List<HlsCusLonContractWithdraw> withdrawChangeHistoryQuery(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize) {
        return lonContractWithdrawMapper.withdrawChangeHistoryQuery(lonContractWithdraw);
    }

    @Override
    public List<Double> queryForFinanceQuotation(HlsCusLonContractWithdraw hlsCusLonContractWithdraw) {
        List<Double> base_rate = new ArrayList<>();
        if (hlsCusLonContractWithdraw.getDueDate() != null && hlsCusLonContractWithdraw.getBaseRateType() != null) {
            base_rate.add(lonContractWithdrawMapper.queryForFinanceQuotation(hlsCusLonContractWithdraw.getBaseRateType(), hlsCusLonContractWithdraw.getDueDate()));
        }
        return base_rate;
    }

    @Override
    public List<Long> queryForFinanceQuotationTime(HlsCusLonContractWithdraw hlsCusLonContractWithdraw) {
        List<Long> change_term = new ArrayList<>();
        if (hlsCusLonContractWithdraw.getRateChangeDate() != null) {
            change_term.add(lonContractWithdrawMapper.queryForFinanceQuotationTime(hlsCusLonContractWithdraw.getRateChangeDate(), hlsCusLonContractWithdraw.getWithdrawId()));
        }
        return change_term;
    }

    @Override
    public void updateAutoWriteByWithdrawId(HlsCusLonContractWithdraw lonContractWithdraw) {
        lonContractWithdrawMapper.updateAutoWriteByWithdrawId(lonContractWithdraw);
    }

    @Override
    public List<HlsCusLonContractWithdraw> selectStampDutyFinancing(HlsCusLonContractWithdraw dto) {
        List<HlsCusLonContractWithdraw> cusLonContractWithdraws = lonContractWithdrawMapper.selectStampDutyFinancing(dto);
        List<HlsCusLonContractWithdraw> cusLonContractWithdrawList = new ArrayList<>();
        for (int i = 0; i < cusLonContractWithdraws.size(); i++) {
            HlsCusLonContractWithdraw hlsCusLonContractWithdraw = cusLonContractWithdraws.get(i);
            String leaseTime = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss").format(hlsCusLonContractWithdraw.getDueDate());
            hlsCusLonContractWithdraw.setPeriodName(leaseTime.substring(0, leaseTime.lastIndexOf("-")));
            cusLonContractWithdrawList.add(hlsCusLonContractWithdraw);
        }
        return cusLonContractWithdrawList;
    }

    @Override
    public void lonContractRepaymentImport(IRequest iRequest, Long hdId, Long withdrawId, Long contractId) throws ExcelException, Exception, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 0L);
        List<HlsCusLonContractRepayment> lonContractRepaymentCheckList = new ArrayList<>();

        HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
        lonContractWithdraw.setWithdrawId(withdrawId);
        lonContractWithdraw = self().selectByPrimaryKey(iRequest, lonContractWithdraw);

        //先删除未核销的现金流
        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
        lonContractRepayment.setWithdrawId(withdrawId);
        lonContractRepayment.setWriteOffFlag("NOT");
        List<HlsCusLonContractRepayment> deleteLonContractRepaymentList = hlsCusLonContractRepaymentMapper.select(lonContractRepayment);
        hlsCusLonContractRepaymentService.batchDelete(deleteLonContractRepaymentList);

        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusLonContractRepayment record = new HlsCusLonContractRepayment();

            Long times = 0L;
            Long cfItem = 0L;
            Date plannedCalcDate = new Date();
            Date plannedDueDate = new Date();
            Double interestPeriodDays = 0.0;
            Double plannedDueAmount = 0.0;
            Double interestAccrualBalance = 0.0;
            Double exchangeRate = 1.0;
            Double cnyPlannedDueAmount = 0.0;
            Double intRate = null;
            String currency = "CNY";

            if (lonContractWithdraw != null) {
                HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
                hlsCusLonContract.setContractId(lonContractWithdraw.getContractId());
                hlsCusLonContract = hlsCusLonContractService.selectByPrimaryKey(iRequest, hlsCusLonContract);
                currency = hlsCusLonContract.getCurrency();
            }

            //期数
            if (fndInterfaceLine.getAttributes_1() != null) {
                times = Long.parseLong(fndInterfaceLine.getAttributes_1());
            } else {
                throw new HlsCusException("期数不能为空！");
            }
            //现金流项目
            if (fndInterfaceLine.getAttributes_2() != null) {
                if (fndInterfaceLine.getAttributes_2().equals("融资-还款本金")) {
                    cfItem = 301L;
                } else if (fndInterfaceLine.getAttributes_2().equals("融资-还款利息")) {
                    cfItem = 302L;
                } else {
                    throw new HlsCusException("现金流项目非“融资-还款本金”或“融资-还款利息”！");
                }
                //cfItem = Long.parseLong(fndInterfaceLine.getAttributes_2());
            } else {
                throw new HlsCusException("现金流项目不能为空！");
            }
            //还款日期
            if (fndInterfaceLine.getAttributes_3() != null) {
                plannedCalcDate = df.parse(fndInterfaceLine.getAttributes_3());
                plannedDueDate = df.parse(fndInterfaceLine.getAttributes_3());
            } else {
                throw new HlsCusException("还款日期不能为空！");
            }
            /*//还款日期
            if (fndInterfaceLine.getAttributes_5() != null) {
                plannedDueDate = df.parse(fndInterfaceLine.getAttributes_5());
            }*/
            //原币还款金额(元)
            if (fndInterfaceLine.getAttributes_4() != null) {
                plannedDueAmount = Double.parseDouble(fndInterfaceLine.getAttributes_4());
                cnyPlannedDueAmount = CalculateUtil.mul(plannedDueAmount, exchangeRate);
            } else {
                throw new HlsCusException("本币还款金额(元)不能为空！");
            }

            //本币剩余本金(元)
            if (fndInterfaceLine.getAttributes_5() != null) {
                interestAccrualBalance = Double.parseDouble(fndInterfaceLine.getAttributes_5());
            } else {
                throw new HlsCusException("本币剩余本金(元)不能为空！");
            }
            if (fndInterfaceLine.getAttributes_6() != null) {
                intRate = Double.parseDouble(fndInterfaceLine.getAttributes_6());
            }

            /*//校验资产编号不能重复
            if (leaseItemCode.equalsIgnoreCase(leaseItemCodeCompare)) {
                throw new HlsCusException("excel第" + fndInterfaceLine.getLineNumber() + "行:资产编号重复！");
            } else {
                leaseItemCodeCompare = leaseItemCode;
            }*/

            record.setTimes(times);
            record.setCfItem(cfItem);
            record.setPlannedCalcDate(plannedCalcDate);
            record.setPlannedDueDate(plannedDueDate);
            record.setInterestPeriodDays(interestPeriodDays);
            record.setPlannedDueAmount(plannedDueAmount);
            record.setInterestAccrualBalance(interestAccrualBalance);
            record.setExchangeRate(exchangeRate);
            record.setCnyDueAmount(cnyPlannedDueAmount);
            record.setDueAmount(plannedDueAmount);
            record.setDueDate(plannedDueDate);
            record.setCfType(70L);
            record.setCfDirection("OUTFLOW");
            record.setCfStatus("RELEASE");
            record.setWriteOffFlag("NOT");
            record.setWithdrawId(withdrawId);
            record.setContractId(contractId);
            HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
            hlsCusLonContract.setContractId(contractId);
            hlsCusLonContract = hlsCusLonContractService.selectByPrimaryKey(iRequest, hlsCusLonContract);
            /*if (hlsCusLonContract != null) {
                record.setCurrency(hlsCusLonContract.getCurrency());
            }*/
            record.setCurrency(currency);
            if (withdrawId == null || contractId == null) {
                throw new HlsCusException("未正确获取withdrawId或contractId！");
            }


            //通过 期数 现金流 项目 判断 如果存在 更新 ， 如果 不存在 插入 ，如果 已经核销 不操作
//            HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
//            lonContractRepayment.setWithdrawId(withdrawId);
//            lonContractRepayment.setTimes(times);
//            lonContractRepayment.setCfItem(cfItem);
//            lonContractRepayment.setWriteOffFlag("NOT");
//            List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentMapper.select(lonContractRepayment);

            if (lonContractWithdraw != null) {
                if (lonContractWithdraw.getDueDate() == null || lonContractWithdraw.getWithdrawEndDate() == null) {
                    throw new HlsCusException("提款起始日和计划到期日不能为空！");
                }
                if (lonContractWithdraw.getDueDate().getTime() > record.getPlannedCalcDate().getTime() || lonContractWithdraw.getWithdrawEndDate().getTime() < record.getPlannedCalcDate().getTime()) {
                    throw new HlsCusException("还款日期必须介于提款起始日和提款结束日之间！");
                }
            } else {
                throw new HlsCusException("未获取到提款！");
            }
            hlsCusLonContractRepaymentService.insertSelective(iRequest, record);
//            if (lonContractRepaymentList.size() == 0) {
//                hlsCusLonContractRepaymentService.insertSelective(iRequest, record);
//            } else if (lonContractRepaymentList.size() == 1) {
//                record.setRepaymentId(lonContractRepaymentList.get(0).getRepaymentId());
//                hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(iRequest, record);
//            }
            lonContractRepaymentCheckList.add(record);
        }
        for (int i = 0; i < lonContractRepaymentCheckList.size() - 1; i++) {
            for (int j = i + 1; j < lonContractRepaymentCheckList.size(); j++) {
                if (lonContractRepaymentCheckList.get(i).getCfItem().equals(lonContractRepaymentCheckList.get(j).getCfItem())) {
                    if (lonContractRepaymentCheckList.get(i).getTimes().equals(lonContractRepaymentCheckList.get(j).getTimes())) {
                        throw new HlsCusException("同一种现金流类型的记录，期数不能相同！");
                    }/*else if(lonContractRepaymentCheckList.get(i).getTimes() < lonContractRepaymentCheckList.get(j).getTimes() && lonContractRepaymentCheckList.get(i).getPlannedCalcDate().getTime() > lonContractRepaymentCheckList.get(j).getPlannedCalcDate().getTime()){
                        throw new HlsCusException("同一种现金流类型的记录，期数和还款日期不对应！");
                    }else if(lonContractRepaymentCheckList.get(i).getTimes() > lonContractRepaymentCheckList.get(j).getTimes() && lonContractRepaymentCheckList.get(i).getPlannedCalcDate().getTime() < lonContractRepaymentCheckList.get(j).getPlannedCalcDate().getTime()){
                        throw new HlsCusException("同一种现金流类型的记录，期数和还款日期不对应！");
                    }*/
                }
            }
        }
    }

    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId, Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;

    }

}