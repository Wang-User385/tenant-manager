package com.hand.hls.fp.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fp.dto.*;
import com.hand.hls.fp.mapper.*;
import com.hand.hls.fp.service.*;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.mapper.FndOrgUnitMapper;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundFillingServiceImpl extends BaseServiceImpl<JcFundFilling> implements JcFundFillingService {

    @Autowired
    private JcFundFillingDetailMapper mapper;
    @Autowired
    private JcFundFillingLnMapper lnMapper;
    @Autowired
    private JcFundingPlanMapper planMapper;
    @Autowired
    private JcFundFillingDetailService detailService;
    @Autowired
    private JcFundFillingLnService serviceLn;
    @Autowired
    private JcFundingPlanOneMapper oneMapper;
    private static String BUSINESS_DEPART = "BUSINESS_DEPART";
    private static String MONEY_DEPART = "MONEY_DEPART";
    @Autowired
    private JcFundingPlanSecondMapper secondMapper;
    @Autowired
    private JcFundingPlanThreeMapper threeMapper;
    @Autowired
    private JcFundFillingMapper fundFillingMapper;
    @Autowired
    private JcFundingPlanFourMapper fourMapper;
    @Autowired
    private JcFundingPlanFiveMapper fiveMapper;
    @Autowired
    private JcFundFillingService service;
    @Autowired
    private FndOrgUnitMapper unitMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    /**
     * 定义年份数组
     */
    private static final String[] YEAR_LIST = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private static final String[] FIRST = new String[]{"01", "02", "03"};
    private static final String[] SECOND = new String[]{"04", "05", "06"};
    private static final String[] THIRD = new String[]{"07", "08", "09"};
    private static final String[] FOURTH = new String[]{"10", "11", "12"};
    private static final String[] WEEK_LIST = new String[]{"01", "02", "03", "04"};
    private static final String ONE_ITEM = "JY_INFLOW";
    private static final String JY_OUTFLOW = "JY_OUTFLOW";
    private static final String PRINCIPAL = "PRINCIPAL";
    private static final String INTEREST = "INTEREST";
    private static final String DEPOSIT = "DEPOSIT";
    private static final String LEGAL_FEE = "LEGAL_FEE";
    private static final String LOAN_DELIVERY = "LOAN_DELIVERY";
    private static final String PRINCIPAL_YD = "PRINCIPAL_YD";
    private static final String INTEREST_YD = "INTEREST_YD";
    private static final String DEPOSIT_YD = "DEPOSIT_YD";
    private static final String LEGAL_FEE_YD = "LEGAL_FEE_YD";
    private static final String OTHER = "OTHER";
    private static final Double INIT_AMOUNT = 0D;
    /*下面四个类型通过本金 利息 手续费 保证金进行区分汇总*/
    private static final String IN_THE = "IN_THE";
    private static final String SX_KYH = "SX_KYH";
    private static final String SX_BLH = "SX_BLH";
    private static final String TJ_KYH = "TJ_KYH";
    private static final String TJ_BLH = "TJ_BLH";
    private static final String BUSINESS_DEPT_OD = "BUSINESS_DEPT_OD";
    private static final String BUSINESS_DEPT_TD = "BUSINESS_DEPT_TD";
    private static final String BUSINESS_DEPT_THD = "BUSINESS_DEPT_THD";
    private static final String BUSINESS_DEPT_ZD = "BUSINESS_DEPT_ZD";
    private static final String FINANCIAL_MARKETS_DEPT = "FINANCIAL_MARKETS_DEPT";
    private static final String PRINCIPAL_WD = "PRINCIPAL_WD";
    private static final String INTEREST_WD = "INTEREST_WD";
    private static final String LEGAL_FEE_WD = "LEGAL_FEE_WD";
    private static final String DEPOSIT_WD = "DEPOSIT_WD";
    private static final String PRINCIPAL_WD_TJ = "PRINCIPAL_WD_TJ";
    private static final String INTEREST_WD_TJ = "INTEREST_WD_TJ";
    private static final String LEGAL_FEE_WD_TJ = "LEGAL_FEE_WD_TJ";
    private static final String DEPOSIT_WD_TJ = "DEPOSIT_WD_TJ";
    private static final String PRINCIPAL_ZQ = "PRINCIPAL_ZQ";
    private static final String INTEREST_ZQ = "INTEREST_ZQ";
    private static final String FEE_ZQ = "FEE_ZQ";
    private static final String LEGAL_FEE_PJ = "LEGAL_FEE_PJ";
    private static final String DEPOSIT_PJ = "DEPOSIT_PJ";
    private static final String PLUS_PJ = "PLUS_PJ";
    private static final String LEGAL_FEE_PJ_TJ = "LEGAL_FEE_PJ_TJ";
    private static final String DEPOSIT_PJ_TJ = "DEPOSIT_PJ_TJ";
    private static final String PLUS_PJ_TJ = "PLUS_PJ_TJ";
    private static final String REPORT_CODE = "JC-20200311-002";

    @Autowired
    private JcFundPlanScheduleMapper scheduleMapper;
    private static final String[] DAY_LIST = new String[13];
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private FundingExecuteService executeService;
    @Autowired
    private FundingExecuteLnService executeLnService;
    @Autowired
    private FundingExecuteLnMapper executeLnMapper;
    @Autowired
    private FundFillingReqLnService reqLnService;
    @Autowired
    private FundFillingReqDetailService reqDetailService;
    @Autowired
    private FundingExecuteMapper executeMapper;
    private static final Long EXECUTE_INT = 2L;
    private static final String INPUT = "INPUT";// 收入
    private static final String OUTPUT = "OUTPUT";// 支出
    private static final String INTEGRATED_MGT_DEPT = "INTEGRATED_MGT_DEPT";
    private static final String ACCOUNTING_DEPT = "ACCOUNTING_DEPT";
    /*
     * SX_KYH :陕西公司可用户 --> 取时间区间内的应收本金+应收利息+应收手续费+应收保证金合计，且关联虚拟合同的收款账户的公司名称为陕西君成且账户类型（新）为可用户
     * SX_BLH :陕西公司保理户/监管户 --> 取时间区间内的应收本金+应收利息+应收手续费+应收保证金合计，且关联虚拟合同的收款账户的公司名称为陕西君成且账户类型（新）为保理户/监管户
     * TJ_KYH :天津公司可用户 --> 取时间区间内的应收本金+应收利息+应收手续费+应收保证金合计，且关联虚拟合同的收款账户的公司名称为天津君成且账户类型（新）为可用户
     * TJ_BLH :天津公司保理户/监管户 -->取时间区间内的应收本金+应收利息+应收手续费+应收保证金合计，且关联虚拟合同的收款账户的公司名称为天津君成且账户类型（新）为保理户/监管户
     */
    /**
     * 定义项目类型
     */
    private static final String[] ITEM_LIST = new String[]{
            "一、经营活动资金流入合计", "本金", "利息", "手续费", "保证金", "其中：", "陕西公司可用户", "陕西公司保理户/监管户", "天津公司可用户", "天津公司保理户/监管户", "二、投资活动资金流出合计", "投放款", "其他"
    };

    @Override
    public List<JcFundFilling> queryAllByUnit(IRequest request, JcFundFilling filling, int page, int pageSize) {
        return fundFillingMapper.queryAllByUnit(filling);
    }

    ;

    @Override
    public List<JcFundFilling> querySummaryAllByUnit(IRequest request, JcFundFilling filling, int page, int pageSize) {
        return fundFillingMapper.queryAllNew(filling);
    }

    ;

    /*资金计划年初始化 20210722*/
    @Override
    public List<JcFundFillingDetail> initYear(IRequest request, JcFundFilling filling) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        //删除原 行以及明细表记录
        if (fillingId != null) {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        } else {
            //初始化先做头存入
            filling.setFillUserId(request.getUserId());
            filling.setDocumentType("FILL");
            filling.setPlanStatus("NEW");
            service.insert(request, filling);
            fillingId = filling.getFillingId();
        }
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        //遍历查询结果并存表
        Double dueAmount = null;
        /*遍历项目*/
        JcFundingPlan plan = new JcFundingPlan();
        plan.setUnitCode(BUSINESS_DEPART);
        //业务部
        List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
        if (planList.size() > 0) {
            JcFundingPlanOne one = new JcFundingPlanOne();
            one.setFundingId(planList.get(0).getFundingId());
            one.setEnabledFlag("Y");
            List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
            //遍历以及目录
            if (oneList.size() > 0) {
                //一级目录插入资金填报明细行
                for (JcFundingPlanOne planOne : oneList) {
                    JcFundFillingLn lnOne = new JcFundFillingLn();
                    lnOne.setFillingId(fillingId);
                    lnOne.setFillItem(planOne.getItemName());
                    lnOne.setFillItemCode(planOne.getItemCode());
                    serviceLn.insert(request, lnOne);
                    //二级目录
                    JcFundingPlanSecond second = new JcFundingPlanSecond();
                    second.setFundingOneId(planOne.getFundingOneId());
                    second.setEnabledFlag("Y");
                    List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                    if (secondList.size() > 0) {
                        for (JcFundingPlanSecond planSecond : secondList) {
                            JcFundFillingLn lnSecond = new JcFundFillingLn();
                            lnSecond.setFillingId(fillingId);
                            lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                            lnSecond.setFillItem(planSecond.getSecondItemName());
                            serviceLn.insert(request, lnSecond);
                            //其他项目类型(二级） 需要进入三级明细
                            if (IN_THE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                JcFundingPlanThree three = new JcFundingPlanThree();
                                three.setFundingSecondId(planSecond.getFundingSecondId());
                                three.setEnabledFlag("Y");
                                List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                                if (threeList.size() > 0) {
                                    //遍历三级目录
                                    for (JcFundingPlanThree planThree : threeList) {
                                        JcFundFillingLn lnThree = new JcFundFillingLn();
                                        lnThree.setFillingId(fillingId);
                                        lnThree.setFillItemCode(planThree.getThreeItemCode());
                                        lnThree.setFillItem(planThree.getThreeItemName());
                                        serviceLn.insert(request, lnThree);
                                    }
                                }
                            } else {
                                //遍历一年中的月份 并根据项目获取金额
                                for (int i = 0; i < YEAR_LIST.length; i++) {
                                    //设置现金流获取年月
                                    String fundType = "";
                                    fundType = fillYear + "-" + YEAR_LIST[i];
                                    JcFundFillingDetail detailNew = new JcFundFillingDetail();
                                    detailNew.setItem(planSecond.getSecondItemName());
                                    detailNew.setItemCode(planSecond.getSecondItemCode());
                                    detailNew.setFillYear(fillYear);
                                    detailNew.setFundType(fundType);
                                    detailNew.setFillItem(planSecond.getSecondItemName());
                                    detailNew.setHostUnitId(hostUnitId);
                                    List<JcFundFillingDetail> detailLists = new ArrayList<>();
                                    //二级目录 本金 利息 手续费 保证金 需要更新金额
                                    if (PRINCIPAL.equalsIgnoreCase(planSecond.getSecondItemCode()) || INTEREST.equalsIgnoreCase(planSecond.getSecondItemCode())
                                            || DEPOSIT.equalsIgnoreCase(planSecond.getSecondItemCode()) || LEGAL_FEE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                        detailLists = mapper.queryDetail(detailNew);
                                    }
                                    if (detailLists.size() > 0) {
                                        for (JcFundFillingDetail detailSecond : detailLists) {
                                            JcFundFillingDetail detailIns = new JcFundFillingDetail();
                                            detailIns.setFillYear(fillYear);
                                            detailIns.setItem(planSecond.getSecondItemName());
                                            detailIns.setItemCode(planSecond.getSecondItemCode());
                                            detailIns.setFundType(fundType);
                                            detailIns.setFillingId(fillingId);
                                            detailIns.setProjectName(detailSecond.getProjectName());
                                            detailIns.setProjectNumber(detailSecond.getProjectNumber());
                                            detailIns.setFillingLnId(lnSecond.getFillingLnId());
                                            detailIns.setDueAmount(detailSecond.getDueAmount());
                                            detailIns.setDueDate(detailSecond.getDueDate());
                                            detailIns.setProjectId(detailSecond.getProjectId());
                                            detailIns.setContractId(detailSecond.getContractId());
                                            detailIns.setHostUnitId(hostUnitId);
                                            detailIns.setBankId(detailSecond.getBankAccountId());
                                            detailService.insert(request, detailIns);
                                            detailList.add(detailIns);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateFundLnAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }


    /*资金计划季度初始化  20210722*/
    @Override
    public List<JcFundFillingDetail> initQuarter(IRequest request, JcFundFilling filling) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        String[] quarterList = new String[]{};
        if ("FIRST".equalsIgnoreCase(fillQuarter)) {
            quarterList = FIRST;
        } else if ("SECOND".equalsIgnoreCase(fillQuarter)) {
            quarterList = SECOND;
        } else if ("THIRD".equalsIgnoreCase(fillQuarter)) {
            quarterList = THIRD;
        } else if ("FOURTH".equalsIgnoreCase(fillQuarter)) {
            quarterList = FOURTH;
        }
        //删除原 行以及明细表记录
        if (fillingId != null) {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        } else {
            //初始化先做头存入
            filling.setFillUserId(request.getUserId());
            filling.setDocumentType("FILL");
            filling.setPlanStatus("NEW");
            service.insert(request, filling);
            fillingId = filling.getFillingId();
        }
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        //遍历查询结果并存表
        Double dueAmount = null;
        /*遍历项目*/
        JcFundingPlan plan = new JcFundingPlan();
        plan.setUnitCode(BUSINESS_DEPART);
        //业务部
        List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
        if (planList.size() > 0) {
            JcFundingPlanOne one = new JcFundingPlanOne();
            one.setFundingId(planList.get(0).getFundingId());
            one.setEnabledFlag("Y");
            List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
            //遍历以及目录
            if (oneList.size() > 0) {
                //一级目录插入资金填报明细行
                for (JcFundingPlanOne planOne : oneList) {
                    JcFundFillingLn lnOne = new JcFundFillingLn();
                    lnOne.setFillingId(fillingId);
                    lnOne.setFillItem(planOne.getItemName());
                    lnOne.setFillItemCode(planOne.getItemCode());
                    serviceLn.insert(request, lnOne);
                    //二级目录
                    JcFundingPlanSecond second = new JcFundingPlanSecond();
                    second.setFundingOneId(planOne.getFundingOneId());
                    second.setEnabledFlag("Y");
                    List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                    if (secondList.size() > 0) {
                        for (JcFundingPlanSecond planSecond : secondList) {
                            JcFundFillingLn lnSecond = new JcFundFillingLn();
                            lnSecond.setFillingId(fillingId);
                            lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                            lnSecond.setFillItem(planSecond.getSecondItemName());
                            serviceLn.insert(request, lnSecond);
                            //其他项目类型(二级） 需要进入三级明细
                            if (IN_THE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                JcFundingPlanThree three = new JcFundingPlanThree();
                                three.setFundingSecondId(planSecond.getFundingSecondId());
                                three.setEnabledFlag("Y");
                                List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                                if (threeList.size() > 0) {
                                    //遍历三级目录
                                    for (JcFundingPlanThree planThree : threeList) {
                                        JcFundFillingLn lnThree = new JcFundFillingLn();
                                        lnThree.setFillingId(fillingId);
                                        lnThree.setFillItemCode(planThree.getThreeItemCode());
                                        lnThree.setFillItem(planThree.getThreeItemName());
                                        serviceLn.insert(request, lnThree);
                                    }
                                }
                            } else {
                                //遍历一年中的月份 并根据项目获取金额
                                for (int i = 0; i < quarterList.length; i++) {
                                    //设置现金流获取年月
                                    String fundType = "";
                                    fundType = fillYear + "-" + quarterList[i];
                                    JcFundFillingDetail detailNew = new JcFundFillingDetail();
                                    detailNew.setItem(planSecond.getSecondItemName());
                                    detailNew.setItemCode(planSecond.getSecondItemCode());
                                    detailNew.setFillYear(fillYear);
                                    detailNew.setFundType(fundType);
                                    detailNew.setFillItem(planSecond.getSecondItemName());
                                    detailNew.setHostUnitId(hostUnitId);
                                    List<JcFundFillingDetail> detailLists = new ArrayList<>();
                                    //二级目录 本金 利息 手续费 保证金 需要更新金额
                                    if (PRINCIPAL.equalsIgnoreCase(planSecond.getSecondItemCode()) || INTEREST.equalsIgnoreCase(planSecond.getSecondItemCode())
                                            || DEPOSIT.equalsIgnoreCase(planSecond.getSecondItemCode()) || LEGAL_FEE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                        detailLists = mapper.queryDetail(detailNew);
                                    }
                                    if (detailLists.size() > 0) {
                                        for (JcFundFillingDetail detailSecond : detailLists) {
                                            JcFundFillingDetail detailIns = new JcFundFillingDetail();
                                            detailIns.setFillYear(fillYear);
                                            detailIns.setItem(planSecond.getSecondItemName());
                                            detailIns.setItemCode(planSecond.getSecondItemCode());
                                            detailIns.setFundType(fundType);
                                            detailIns.setFillingId(fillingId);
                                            detailIns.setProjectName(detailSecond.getProjectName());
                                            detailIns.setProjectNumber(detailSecond.getProjectNumber());
                                            detailIns.setFillingLnId(lnSecond.getFillingLnId());
                                            detailIns.setDueAmount(detailSecond.getDueAmount());
                                            detailIns.setDueDate(detailSecond.getDueDate());
                                            detailIns.setProjectId(detailSecond.getProjectId());
                                            detailIns.setContractId(detailSecond.getContractId());
                                            detailIns.setBankId(detailSecond.getBankAccountId());
                                            detailIns.setHostUnitId(hostUnitId);
                                            detailService.insert(request, detailIns);
                                            detailList.add(detailIns);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateFundLnAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }

    /*资金计划月初始化  20210722*/
    @Override
    public List<JcFundFillingDetail> initMon(IRequest request, JcFundFilling filling) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        String[] quarterList = new String[]{};
        //删除原 行以及明细表记录
        if (fillingId != null) {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        } else {
            //初始化先做头存入
            filling.setFillUserId(request.getUserId());
            filling.setPlanStatus("NEW");
            filling.setDocumentType("FILL");
            service.insert(request, filling);
            fillingId = filling.getFillingId();
        }
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        //遍历查询结果并存表
        Double dueAmount = null;
        /*遍历项目*/
        JcFundingPlan plan = new JcFundingPlan();
        plan.setUnitCode(BUSINESS_DEPART);
        //业务部
        List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
        if (planList.size() > 0) {
            JcFundingPlanOne one = new JcFundingPlanOne();
            one.setFundingId(planList.get(0).getFundingId());
            one.setEnabledFlag("Y");
            List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
            //遍历以及目录
            if (oneList.size() > 0) {
                //一级目录插入资金填报明细行
                for (JcFundingPlanOne planOne : oneList) {
                    JcFundFillingLn lnOne = new JcFundFillingLn();
                    lnOne.setFillingId(fillingId);
                    lnOne.setFillItem(planOne.getItemName());
                    lnOne.setFillItemCode(planOne.getItemCode());
                    serviceLn.insert(request, lnOne);
                    //二级目录
                    JcFundingPlanSecond second = new JcFundingPlanSecond();
                    second.setFundingOneId(planOne.getFundingOneId());
                    second.setEnabledFlag("Y");
                    List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                    if (secondList.size() > 0) {
                        for (JcFundingPlanSecond planSecond : secondList) {
                            JcFundFillingLn lnSecond = new JcFundFillingLn();
                            lnSecond.setFillingId(fillingId);
                            lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                            lnSecond.setFillItem(planSecond.getSecondItemName());
                            serviceLn.insert(request, lnSecond);
                            //其他项目类型(二级） 需要进入三级明细
                            if (IN_THE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                JcFundingPlanThree three = new JcFundingPlanThree();
                                three.setFundingSecondId(planSecond.getFundingSecondId());
                                three.setEnabledFlag("Y");
                                List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                                if (threeList.size() > 0) {
                                    //遍历三级目录
                                    for (JcFundingPlanThree planThree : threeList) {
                                        JcFundFillingLn lnThree = new JcFundFillingLn();
                                        lnThree.setFillingId(fillingId);
                                        lnThree.setFillItemCode(planThree.getThreeItemCode());
                                        lnThree.setFillItem(planThree.getThreeItemName());
                                        serviceLn.insert(request, lnThree);
                                    }
                                }
                            } else {
                                //遍历一年中的月份 并根据项目获取金额
                                for (int i = 0; i < WEEK_LIST.length; i++) {
                                    //设置现金流获取年月
                                    String fundType = "";
                                    //年月周确定一类数据
                                    fundType = fillYear + "-" + fillMon + "-" + WEEK_LIST[i];
                                    JcFundFillingDetail detailNew = new JcFundFillingDetail();
                                    detailNew.setItem(planSecond.getSecondItemName());
                                    detailNew.setItemCode(planSecond.getSecondItemCode());
                                    detailNew.setFillYear(fillYear);
                                    detailNew.setFundType(fundType);
                                    detailNew.setFillItem(planSecond.getSecondItemName());
                                    detailNew.setHostUnitId(hostUnitId);
                                    detailNew.setFillMon(fillMon);
                                    fillWeek = Long.valueOf(i + 1).toString();
                                    detailNew.setFillWeek(fillWeek);
                                    List<JcFundFillingDetail> detailLists = new ArrayList<>();
                                    //二级目录 本金 利息 手续费 保证金 需要更新金额
                                    if (PRINCIPAL.equalsIgnoreCase(planSecond.getSecondItemCode()) || INTEREST.equalsIgnoreCase(planSecond.getSecondItemCode())
                                            || DEPOSIT.equalsIgnoreCase(planSecond.getSecondItemCode()) || LEGAL_FEE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                        detailLists = mapper.queryDetailMon(detailNew);
                                    }
                                    if (detailLists.size() > 0) {
                                        for (JcFundFillingDetail detailSecond : detailLists) {
                                            JcFundFillingDetail detailIns = new JcFundFillingDetail();
                                            detailIns.setFillYear(fillYear);
                                            detailIns.setItem(planSecond.getSecondItemName());
                                            detailIns.setItemCode(planSecond.getSecondItemCode());
                                            detailIns.setFundType(fundType);
                                            detailIns.setFillingId(fillingId);
                                            detailIns.setProjectName(detailSecond.getProjectName());
                                            detailIns.setProjectNumber(detailSecond.getProjectNumber());
                                            detailIns.setFillingLnId(lnSecond.getFillingLnId());
                                            detailIns.setDueAmount(detailSecond.getDueAmount());
                                            detailIns.setDueDate(detailSecond.getDueDate());
                                            detailIns.setProjectId(detailSecond.getProjectId());
                                            detailIns.setContractId(detailSecond.getContractId());
                                            detailIns.setHostUnitId(hostUnitId);
                                            detailIns.setBankId(detailSecond.getBankAccountId());
                                            detailService.insert(request, detailIns);
                                            detailList.add(detailIns);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateFundLnAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }

    /*资金计划周初始化  20210722*/
    @Override
    public List<JcFundFillingDetail> initWeek(IRequest request, JcFundFilling filling) {

        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        //删除原 行以及明细表记录
        if (fillingId != null) {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        } else {
            //初始化先做头存入
            filling.setFillUserId(request.getUserId());
            filling.setDocumentType("FILL");
            filling.setPlanStatus("NEW");
            service.insert(request, filling);
            fillingId = filling.getFillingId();
        }
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        JcFundPlanSchedule schedule = new JcFundPlanSchedule();
        schedule.setFillWeek(Long.valueOf(fillWeek));
        schedule.setFillMon(Long.valueOf(fillMon));
        schedule.setFillYear(Long.valueOf(fillYear));
        //周计划需要查询出对应日期
        List<JcFundPlanSchedule> scheduleList = scheduleMapper.queryAll(schedule);
        if (scheduleList.size() > 0) {
            List list = new ArrayList();
            DAY_LIST[0] = df.format(scheduleList.get(0).getFirstDay());
            if (scheduleList.get(0).getSecondDay() != null) {
                DAY_LIST[1] = df.format(scheduleList.get(0).getSecondDay());
            }
            if (scheduleList.get(0).getThirdDay() != null) {
                DAY_LIST[2] = df.format(scheduleList.get(0).getThirdDay());
            }
            if (scheduleList.get(0).getFourthDay() != null) {
                DAY_LIST[3] = df.format(scheduleList.get(0).getFourthDay());
            }
            if (scheduleList.get(0).getFifthDay() != null) {
                DAY_LIST[4] = df.format(scheduleList.get(0).getFifthDay());
            }
            if (scheduleList.get(0).getSixthDay() != null) {
                DAY_LIST[5] = df.format(scheduleList.get(0).getSixthDay());
            }
            if (scheduleList.get(0).getSeventhDay() != null) {
                DAY_LIST[6] = df.format(scheduleList.get(0).getSeventhDay());
            }
            if (scheduleList.get(0).getEighthDay() != null) {
                DAY_LIST[7] = df.format(scheduleList.get(0).getEighthDay());
            }
            if (scheduleList.get(0).getNinthDay() != null) {
                DAY_LIST[8] = df.format(scheduleList.get(0).getNinthDay());
            }
            if (scheduleList.get(0).getTenthDay() != null) {
                DAY_LIST[9] = df.format(scheduleList.get(0).getTenthDay());
            }
            if (scheduleList.get(0).getEleventhDay() != null) {
                DAY_LIST[10] = df.format(scheduleList.get(0).getEleventhDay());
            }
            if (scheduleList.get(0).getTwelfthDay() != null) {
                DAY_LIST[11] = df.format(scheduleList.get(0).getTwelfthDay());
            }
            if (scheduleList.get(0).getThirteenthDay() != null) {
                DAY_LIST[12] = df.format(scheduleList.get(0).getThirteenthDay());
            }
            //遍历查询结果并存表
            Double dueAmount = null;
            /*遍历项目*/
            JcFundingPlan plan = new JcFundingPlan();
            plan.setUnitCode(BUSINESS_DEPART);
            //业务部
            List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
            if (planList.size() > 0) {
                JcFundingPlanOne one = new JcFundingPlanOne();
                one.setFundingId(planList.get(0).getFundingId());
                one.setEnabledFlag("Y");
                List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
                //遍历以及目录
                if (oneList.size() > 0) {
                    //一级目录插入资金填报明细行
                    for (JcFundingPlanOne planOne : oneList) {
                        JcFundFillingLn lnOne = new JcFundFillingLn();
                        lnOne.setFillingId(fillingId);
                        lnOne.setFillItem(planOne.getItemName());
                        lnOne.setFillItemCode(planOne.getItemCode());
                        serviceLn.insert(request, lnOne);
                        //二级目录
                        JcFundingPlanSecond second = new JcFundingPlanSecond();
                        second.setFundingOneId(planOne.getFundingOneId());
                        second.setEnabledFlag("Y");
                        List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                        if (secondList.size() > 0) {
                            for (JcFundingPlanSecond planSecond : secondList) {
                                JcFundFillingLn lnSecond = new JcFundFillingLn();
                                lnSecond.setFillingId(fillingId);
                                lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                                lnSecond.setFillItem(planSecond.getSecondItemName());
                                serviceLn.insert(request, lnSecond);
                                //其他项目类型(二级） 需要进入三级明细
                                if (IN_THE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                    JcFundingPlanThree three = new JcFundingPlanThree();
                                    three.setFundingSecondId(planSecond.getFundingSecondId());
                                    three.setEnabledFlag("Y");
                                    List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                                    if (threeList.size() > 0) {
                                        //遍历三级目录
                                        for (JcFundingPlanThree planThree : threeList) {
                                            JcFundFillingLn lnThree = new JcFundFillingLn();
                                            lnThree.setFillingId(fillingId);
                                            lnThree.setFillItemCode(planThree.getThreeItemCode());
                                            lnThree.setFillItem(planThree.getThreeItemName());
                                            serviceLn.insert(request, lnThree);
                                        }
                                    }
                                } else {
                                    //遍历一年中的月份 并根据项目获取金额
                                    for (int i = 0; i < DAY_LIST.length; i++) {
                                        if (DAY_LIST[i] != null) {
                                            //设置现金流获取年月
                                            String fundType = "";
                                            //年月周确定一类数据
                                            fundType = DAY_LIST[i];
                                            JcFundFillingDetail detailNew = new JcFundFillingDetail();
                                            detailNew.setItem(planSecond.getSecondItemName());
                                            detailNew.setItemCode(planSecond.getSecondItemCode());
                                            detailNew.setFillYear(fillYear);
                                            detailNew.setFundType(fundType);
                                            detailNew.setFillItem(planSecond.getSecondItemName());
                                            detailNew.setHostUnitId(hostUnitId);
                                            detailNew.setFillMon(fillMon);
                                            fillWeek = Long.valueOf(i + 1).toString();
                                            detailNew.setFillWeek(fillWeek);
                                            List<JcFundFillingDetail> detailLists = new ArrayList<>();
                                            //二级目录 本金 利息 手续费 保证金 需要更新金额
                                            if (PRINCIPAL.equalsIgnoreCase(planSecond.getSecondItemCode()) || INTEREST.equalsIgnoreCase(planSecond.getSecondItemCode())
                                                    || DEPOSIT.equalsIgnoreCase(planSecond.getSecondItemCode()) || LEGAL_FEE.equalsIgnoreCase(planSecond.getSecondItemCode())) {
                                                detailLists = mapper.queryDetailWeek(detailNew);
                                            }
                                            if (detailLists.size() > 0) {
                                                for (JcFundFillingDetail detailSecond : detailLists) {
                                                    JcFundFillingDetail detailIns = new JcFundFillingDetail();
                                                    detailIns.setFillYear(fillYear);
                                                    detailIns.setItem(planSecond.getSecondItemName());
                                                    detailIns.setItemCode(planSecond.getSecondItemCode());
                                                    detailIns.setFundType(fundType);
                                                    detailIns.setFillingId(fillingId);
                                                    detailIns.setProjectName(detailSecond.getProjectName());
                                                    detailIns.setProjectNumber(detailSecond.getProjectNumber());
                                                    detailIns.setFillingLnId(lnSecond.getFillingLnId());
                                                    detailIns.setDueAmount(detailSecond.getDueAmount());
                                                    detailIns.setDueDate(detailSecond.getDueDate());
                                                    detailIns.setProjectId(detailSecond.getProjectId());
                                                    detailIns.setContractId(detailSecond.getContractId());
                                                    detailIns.setBankId(detailSecond.getBankAccountId());
                                                    detailIns.setHostUnitId(hostUnitId);
                                                    detailService.insert(request, detailIns);
                                                    detailList.add(detailIns);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateFundLnAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }

    /*提交工作流*/
    @Override
    public JcFundFilling fillingSubmitWfl(IRequest iRequest, JcFundFilling filling) throws HlsCusException {
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling.setFillingId(filling.getFillingId());
        fundFilling = fundFillingMapper.queryAll(fundFilling).get(0);
        if ("APPROVING".equalsIgnoreCase(fundFilling.getPlanStatus()) || "APPROVED".equalsIgnoreCase(fundFilling.getPlanStatus())) {
            throw new HlsCusException("该单据不可提交,请检查单据状态！");
        }
        String fundType = "";
        if ("YEAR".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear();
        }
        if ("QUARTER".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear() + "-" + fundFilling.getFillQuarterN();
        }
        if ("MON".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear() + "-" + fundFilling.getFillQuarterN() + "-" + fundFilling.getFillMonN();
        }
        if ("WEEK".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear() + "-" + fundFilling.getFillQuarterN() + "-" + fundFilling.getFillMonN() + "-" + fundFilling.getFillWeekN();
        }

        List<JcFundFilling> fillingList = new ArrayList<>();
        fillingList.add(fundFilling);
        databaseLockProvider.lock(fundFilling);
        HlsEmployee employee = hlsCusEmployeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "FUNDING_PLAN_WFL_NEW");
        params.put("jcFundFilling", JSON.toJSONString(fundFilling));
        params.put("fillingId", fundFilling.getFillingId());
        params.put("fillType", fundFilling.getFillType());
        params.put("fillYear", fundFilling.getFillYear());
        params.put("fillQuarter", fundFilling.getFillQuarter());
        params.put("fillMon", fundFilling.getFillMon());
        params.put("fillWeek", fundFilling.getFillWeek());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "FUNDING_PLAN_WFL_NEW");
        params.put(IActivitiCommonService.DEMO_NAME, "FUNDING_PLAN_WFL_NEW");
        params.put(IActivitiCommonService.BUSINESS_KEY, fundFilling.getFillingId());
        params.put("documentNumber", fundFilling.getFillTypeN() + "-" + fundType);
        params.put("documentName", fundFilling.getFillTypeN() + "填报审批申请");
        params.put("companyId", iRequest.getCompanyId());
        params.put("unitId", iRequest.getAttribute("unitId"));
        params.put("documentCategory", "FUNDING_PLAN_WFL_NEW");
        params.put("documentType", "FUNDING_PLAN_WFL_NEW");
        activitiStartService.start(iRequest, fillingList, params);

        fundFilling.setPlanStatus("APPROVING");
        fundFilling = self().updateByPrimaryKeySelective(iRequest, fundFilling);
        return fundFilling;
    }

    /*汇总提交工作流*/
    @Override
    public JcFundFilling fillingSummarySubmitWfl(IRequest iRequest, JcFundFilling filling) throws HlsCusException {
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling.setFillingId(filling.getFillingId());
        fundFilling = fundFillingMapper.queryAll(fundFilling).get(0);
        if ("APPROVING".equalsIgnoreCase(fundFilling.getSummaryStatus()) || "APPROVED".equalsIgnoreCase(fundFilling.getSummaryStatus())) {
            throw new HlsCusException("该单据不可提交,请检查单据状态！");
        }
        String fundType = "";
        if ("YEAR".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear();
        }
        if ("QUARTER".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear() + "-" + fundFilling.getFillQuarterN();
        }
        if ("MON".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear() + "-" + fundFilling.getFillQuarterN() + "-" + fundFilling.getFillMonN();
        }
        if ("WEEK".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear() + "-" + fundFilling.getFillQuarterN() + "-" + fundFilling.getFillMonN() + "-" + fundFilling.getFillWeekN();
        }

        List<JcFundFilling> fillingList = new ArrayList<>();
        fillingList.add(fundFilling);
        databaseLockProvider.lock(fundFilling);
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "FUNDING_PLAN_WFL_NEW_ZJ");
        params.put("jcFundFilling", JSON.toJSONString(fundFilling));
        params.put("fillingId", fundFilling.getFillingId());
        params.put("fillType", fundFilling.getFillType());
        params.put("fillYear", fundFilling.getFillYear());
        params.put("fillQuarter", fundFilling.getFillQuarter());
        params.put("fillMon", fundFilling.getFillMon());
        params.put("fillWeek", fundFilling.getFillWeek());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "FUNDING_PLAN_WFL_NEW_ZJ");
        params.put(IActivitiCommonService.DEMO_NAME, "FUNDING_PLAN_WFL_NEW");
        params.put(IActivitiCommonService.BUSINESS_KEY, fundFilling.getFillingId());
        params.put("documentNumber", fundFilling.getFillTypeN() + "-" + fundType);
        params.put("documentName", fundFilling.getFillTypeN() + "汇总填报审批申请");
        params.put("companyId", iRequest.getCompanyId());
        params.put("unitId", iRequest.getAttribute("unitId"));
        params.put("documentCategory", "FUNDING_PLAN_WFL_NEW_ZJ");
        params.put("documentType", "FUNDING_PLAN_WFL_NEW_ZJ");
        activitiStartService.start(iRequest, fillingList, params);

        fundFilling.setSummaryStatus("APPROVING");
        fundFilling = self().updateByPrimaryKeySelective(iRequest, fundFilling);
        return fundFilling;
    }

    //汇总年 jc_fund_filling_detail 插入
    public void insSummaryYear(IRequest request, JcFundFilling filling, Long ListType, JcFundingPlanThree planThree, JcFundingPlanFour planFour, JcFundingPlanFive planFive, JcFundFillingLn ln) {
        //遍历一年中的月份 并根据项目获取金额
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        for (int i = 0; i < YEAR_LIST.length; i++) {
            //设置现金流获取年月
            String fundType = "";
            fundType = filling.getFillYear() + "-" + YEAR_LIST[i];
            JcFundFillingDetail detailNew = new JcFundFillingDetail();
            detailNew.setFillYear(filling.getFillYear());
            detailNew.setFundType(fundType);
            detailNew.setFillType(fillType);
            detailNew.setFillQuarter(fillQuarter);
            detailNew.setFillMon(fillMon);
            detailNew.setFillWeek(fillWeek);
            detailNew.setRefFillingId(filling.getRefFillingId());

            List<JcFundFillingDetail> detailLists = new ArrayList<>();
            if (ListType == 5) {
                detailNew.setItem(planFive.getFiveItemName());
                detailNew.setItemCode(planFive.getFiveItemCode());
                detailNew.setFillItem(planFive.getFiveItemName());
                //部门设定和四级目录一致
                detailNew.setUnitCode(planFour.getFourItemCode());
                //五级目录 本金 利息 手续费 保证金 需从业务部已填报的数据去取
                if (PRINCIPAL.equalsIgnoreCase(planFive.getFiveItemCode()) || INTEREST.equalsIgnoreCase(planFive.getFiveItemCode())
                        || DEPOSIT.equalsIgnoreCase(planFive.getFiveItemCode()) || LEGAL_FEE.equalsIgnoreCase(planFive.getFiveItemCode())) {
                    detailLists = mapper.querySummaryDetail(detailNew);
                }
            } else if (ListType == 4) {
                detailNew.setItem(planFour.getFourItemName());
                detailNew.setItemCode(planFour.getFourItemCode());
                detailNew.setFillItem(planFour.getFourItemName());
                //部门设定和三级目录一致
                detailNew.setUnitCode(planThree.getThreeItemCode());
                if (LOAN_DELIVERY.equalsIgnoreCase(planFour.getFourItemCode())) {
                    detailLists = mapper.querySummaryDeliveryDetail(detailNew);

                }
            }


            if (detailLists.size() > 0) {
                for (JcFundFillingDetail detailSecond : detailLists) {
                    JcFundFillingDetail detailIns = new JcFundFillingDetail();
                    detailIns.setFillYear(fillYear);
                    if (ListType == 5) {
                        detailIns.setItem(planFive.getFiveItemName());
                        detailIns.setItemCode(planFive.getFiveItemCode());
                    } else {
                        detailIns.setItem(planFour.getFourItemName());
                        detailIns.setItemCode(planFour.getFourItemCode());
                    }

                    detailIns.setFundType(fundType);
                    detailIns.setFillingId(fillingId);
                    detailIns.setProjectName(detailSecond.getProjectName());
                    detailIns.setProjectNumber(detailSecond.getProjectNumber());
                    detailIns.setFillingLnId(ln.getFillingLnId());
                    detailIns.setDueAmount(detailSecond.getDueAmount());
                    detailIns.setDueDate(detailSecond.getDueDate());
                    detailIns.setProjectId(detailSecond.getProjectId());
                    detailIns.setContractId(detailSecond.getContractId());
                    detailIns.setHostUnitId(detailSecond.getUnitId());
                    detailIns.setBankId(detailSecond.getBankId());
                    detailIns.setPaymentMethod(detailSecond.getPaymentMethod());
                    detailService.insert(request, detailIns);
                }
            }
        }
    }

    //汇总季度 jc_fund_filling_detail 插入
    public void insSummaryQuarter(IRequest request, JcFundFilling filling, Long ListType, JcFundingPlanThree planThree, JcFundingPlanFour planFour, JcFundingPlanFive planFive, JcFundFillingLn ln) {
        //遍历一年中的月份 并根据项目获取金额
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        String[] quarterList = new String[]{};
        if ("FIRST".equalsIgnoreCase(fillQuarter)) {
            quarterList = FIRST;
        } else if ("SECOND".equalsIgnoreCase(fillQuarter)) {
            quarterList = SECOND;
        } else if ("THIRD".equalsIgnoreCase(fillQuarter)) {
            quarterList = THIRD;
        } else if ("FOURTH".equalsIgnoreCase(fillQuarter)) {
            quarterList = FOURTH;
        }
        for (int i = 0; i < quarterList.length; i++) {
            //设置现金流获取年月
            String fundType = "";
            fundType = filling.getFillYear() + "-" + quarterList[i];
            JcFundFillingDetail detailNew = new JcFundFillingDetail();
            detailNew.setFillYear(filling.getFillYear());
            detailNew.setFundType(fundType);
            detailNew.setFillType(fillType);
            detailNew.setFillQuarter(fillQuarter);
            detailNew.setFillMon(fillMon);
            detailNew.setFillWeek(fillWeek);
            detailNew.setRefFillingId(filling.getRefFillingId());
            List<JcFundFillingDetail> detailLists = new ArrayList<>();
            if (ListType == 5) {
                detailNew.setItem(planFive.getFiveItemName());
                detailNew.setItemCode(planFive.getFiveItemCode());
                detailNew.setFillItem(planFive.getFiveItemName());
                //部门设定和四级目录一致
                detailNew.setUnitCode(planFour.getFourItemCode());
                //五级目录 本金 利息 手续费 保证金 需从业务部已填报的数据去取
                if (PRINCIPAL.equalsIgnoreCase(planFive.getFiveItemCode()) || INTEREST.equalsIgnoreCase(planFive.getFiveItemCode())
                        || DEPOSIT.equalsIgnoreCase(planFive.getFiveItemCode()) || LEGAL_FEE.equalsIgnoreCase(planFive.getFiveItemCode())) {
                    detailLists = mapper.querySummaryDetailQuarter(detailNew);
                }
            } else if (ListType == 4) {
                detailNew.setItem(planFour.getFourItemName());
                detailNew.setItemCode(planFour.getFourItemCode());
                detailNew.setFillItem(planFour.getFourItemName());
                //部门设定和三级目录一致
                detailNew.setUnitCode(planThree.getThreeItemCode());
                if (LOAN_DELIVERY.equalsIgnoreCase(planFour.getFourItemCode())) {
                    detailLists = mapper.querySummaryDeliveryDetail(detailNew);

                }
            }

            if (detailLists.size() > 0) {
                for (JcFundFillingDetail detailSecond : detailLists) {
                    JcFundFillingDetail detailIns = new JcFundFillingDetail();
                    detailIns.setFillYear(fillYear);
                    if (ListType == 5) {
                        detailIns.setItem(planFive.getFiveItemName());
                        detailIns.setItemCode(planFive.getFiveItemCode());
                    } else {
                        detailIns.setItem(planFour.getFourItemName());
                        detailIns.setItemCode(planFour.getFourItemCode());
                    }

                    detailIns.setFundType(fundType);
                    detailIns.setFillingId(fillingId);
                    detailIns.setProjectName(detailSecond.getProjectName());
                    detailIns.setProjectNumber(detailSecond.getProjectNumber());
                    detailIns.setFillingLnId(ln.getFillingLnId());
                    detailIns.setDueAmount(detailSecond.getDueAmount());
                    detailIns.setDueDate(detailSecond.getDueDate());
                    detailIns.setProjectId(detailSecond.getProjectId());
                    detailIns.setContractId(detailSecond.getContractId());
                    detailIns.setHostUnitId(detailSecond.getUnitId());
                    detailIns.setBankId(detailSecond.getBankId());
                    detailIns.setPaymentMethod(detailSecond.getPaymentMethod());
                    detailService.insertSelective(request, detailIns);
                }
            }
        }
    }

    //汇总月 jc_fund_filling_detail 插入
    public void insSummaryMon(IRequest request, JcFundFilling filling, Long ListType, JcFundingPlanThree planThree, JcFundingPlanFour planFour, JcFundingPlanFive planFive, JcFundFillingLn ln) {
        //遍历一年中的月份 并根据项目获取金额
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        for (int i = 0; i < WEEK_LIST.length; i++) {
            //设置现金流获取年月
            String fundType = "";
            fundType = fillYear + "-" + fillMon + "-" + WEEK_LIST[i];
            JcFundFillingDetail detailNew = new JcFundFillingDetail();
            detailNew.setFillYear(filling.getFillYear());
            detailNew.setFundType(fundType);
            detailNew.setFillType(fillType);
            detailNew.setFillQuarter(fillQuarter);
            detailNew.setFillMon(fillMon);
            detailNew.setFillWeek(fillWeek);
            detailNew.setRefFillingId(filling.getRefFillingId());
            List<JcFundFillingDetail> detailLists = new ArrayList<>();
            if (ListType == 5) {
                detailNew.setItem(planFive.getFiveItemName());
                detailNew.setItemCode(planFive.getFiveItemCode());
                detailNew.setFillItem(planFive.getFiveItemName());
                //部门设定和四级目录一致
                detailNew.setUnitCode(planFour.getFourItemCode());
                //五级目录 本金 利息 手续费 保证金 需从业务部已填报的数据去取
                if (PRINCIPAL.equalsIgnoreCase(planFive.getFiveItemCode()) || INTEREST.equalsIgnoreCase(planFive.getFiveItemCode())
                        || DEPOSIT.equalsIgnoreCase(planFive.getFiveItemCode()) || LEGAL_FEE.equalsIgnoreCase(planFive.getFiveItemCode())) {
                    detailLists = mapper.querySummaryDetailMon(detailNew);
                }
            } else if (ListType == 4) {
                detailNew.setItem(planFour.getFourItemName());
                detailNew.setItemCode(planFour.getFourItemCode());
                detailNew.setFillItem(planFour.getFourItemName());
                //部门设定和三级目录一致
                detailNew.setUnitCode(planThree.getThreeItemCode());
                if (LOAN_DELIVERY.equalsIgnoreCase(planFour.getFourItemCode())) {
                    detailLists = mapper.querySummaryDeliveryDetail(detailNew);

                }
            }


            if (detailLists.size() > 0) {
                for (JcFundFillingDetail detailSecond : detailLists) {
                    JcFundFillingDetail detailIns = new JcFundFillingDetail();
                    detailIns.setFillYear(fillYear);
                    if (ListType == 5) {
                        detailIns.setItem(planFive.getFiveItemName());
                        detailIns.setItemCode(planFive.getFiveItemCode());
                    } else {
                        detailIns.setItem(planFour.getFourItemName());
                        detailIns.setItemCode(planFour.getFourItemCode());
                    }

                    detailIns.setFundType(fundType);
                    detailIns.setFillingId(fillingId);
                    detailIns.setProjectName(detailSecond.getProjectName());
                    detailIns.setProjectNumber(detailSecond.getProjectNumber());
                    detailIns.setFillingLnId(ln.getFillingLnId());
                    detailIns.setDueAmount(detailSecond.getDueAmount());
                    detailIns.setDueDate(detailSecond.getDueDate());
                    detailIns.setProjectId(detailSecond.getProjectId());
                    detailIns.setContractId(detailSecond.getContractId());
                    detailIns.setHostUnitId(detailSecond.getUnitId());
                    detailIns.setBankId(detailSecond.getBankId());
                    detailIns.setPaymentMethod(detailSecond.getPaymentMethod());
                    detailService.insertSelective(request, detailIns);
                }
            }
        }
    }

    //汇总周 jc_fund_filling_detail 插入
    public void insSummaryWeek(IRequest request, JcFundFilling filling, Long ListType, JcFundingPlanThree planThree, JcFundingPlanFour planFour, JcFundingPlanFive planFive, JcFundFillingLn ln) {
        //遍历一年中的月份 并根据项目获取金额
        Long fillingId = filling.getFillingId();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        JcFundPlanSchedule schedule = new JcFundPlanSchedule();
        schedule.setFillWeek(Long.valueOf(fillWeek));
        schedule.setFillMon(Long.valueOf(fillMon));
        schedule.setFillYear(Long.valueOf(fillYear));
        //周计划需要查询出对应日期
        List<JcFundPlanSchedule> scheduleList = scheduleMapper.queryAll(schedule);
        if (scheduleList.size() > 0) {
            List list = new ArrayList();
            DAY_LIST[0] = df.format(scheduleList.get(0).getFirstDay());
            DAY_LIST[1] = df.format(scheduleList.get(0).getSecondDay());
            if (scheduleList.get(0).getThirdDay() != null) {
                DAY_LIST[2] = df.format(scheduleList.get(0).getThirdDay());
            }
            if (scheduleList.get(0).getFourthDay() != null) {
                DAY_LIST[3] = df.format(scheduleList.get(0).getFourthDay());
            }
            if (scheduleList.get(0).getFifthDay() != null) {
                DAY_LIST[4] = df.format(scheduleList.get(0).getFifthDay());
            }
            if (scheduleList.get(0).getSixthDay() != null) {
                DAY_LIST[5] = df.format(scheduleList.get(0).getSixthDay());
            }
            if (scheduleList.get(0).getSeventhDay() != null) {
                DAY_LIST[6] = df.format(scheduleList.get(0).getSeventhDay());
            }
            if (scheduleList.get(0).getEighthDay() != null) {
                DAY_LIST[7] = df.format(scheduleList.get(0).getEighthDay());
            }
            if (scheduleList.get(0).getNinthDay() != null) {
                DAY_LIST[8] = df.format(scheduleList.get(0).getNinthDay());
            }
            if (scheduleList.get(0).getTenthDay() != null) {
                DAY_LIST[9] = df.format(scheduleList.get(0).getTenthDay());
            }
            if (scheduleList.get(0).getEleventhDay() != null) {
                DAY_LIST[10] = df.format(scheduleList.get(0).getEleventhDay());
            }
            if (scheduleList.get(0).getTwelfthDay() != null) {
                DAY_LIST[11] = df.format(scheduleList.get(0).getTwelfthDay());
            }
            if (scheduleList.get(0).getThirteenthDay() != null) {
                DAY_LIST[12] = df.format(scheduleList.get(0).getThirteenthDay());
            }
        }
        for (int i = 0; i < DAY_LIST.length; i++) {
            if (DAY_LIST[i] != null) {
                //设置现金流获取年月
                String fundType = "";
                fundType = DAY_LIST[i];
                JcFundFillingDetail detailNew = new JcFundFillingDetail();
                detailNew.setFillYear(filling.getFillYear());
                detailNew.setFundType(fundType);
                detailNew.setFillType(fillType);
                detailNew.setFillQuarter(fillQuarter);
                detailNew.setFillMon(fillMon);
                detailNew.setFillWeek(fillWeek);
                detailNew.setRefFillingId(filling.getRefFillingId());
                List<JcFundFillingDetail> detailLists = new ArrayList<>();
                if (ListType == 5) {
                    detailNew.setItem(planFive.getFiveItemName());
                    detailNew.setItemCode(planFive.getFiveItemCode());
                    detailNew.setFillItem(planFive.getFiveItemName());
                    //部门设定和四级目录一致
                    detailNew.setUnitCode(planFour.getFourItemCode());
                    //五级目录 本金 利息 手续费 保证金 需从业务部已填报的数据去取
                    if (PRINCIPAL.equalsIgnoreCase(planFive.getFiveItemCode()) || INTEREST.equalsIgnoreCase(planFive.getFiveItemCode())
                            || DEPOSIT.equalsIgnoreCase(planFive.getFiveItemCode()) || LEGAL_FEE.equalsIgnoreCase(planFive.getFiveItemCode())) {
                        detailLists = mapper.querySummaryDetailWeek(detailNew);
                    }
                } else if (ListType == 4) {
                    detailNew.setItem(planFour.getFourItemName());
                    detailNew.setItemCode(planFour.getFourItemCode());
                    detailNew.setFillItem(planFour.getFourItemName());
                    //部门设定和三倍级目录一致
                    detailNew.setUnitCode(planThree.getThreeItemCode());
                    if (LOAN_DELIVERY.equalsIgnoreCase(planFour.getFourItemCode())) {
                        detailLists = mapper.querySummaryDeliveryDetail(detailNew);

                    }
                }

                if (detailLists.size() > 0) {
                    for (JcFundFillingDetail detailSecond : detailLists) {
                        JcFundFillingDetail detailIns = new JcFundFillingDetail();
                        detailIns.setFillYear(fillYear);
                        if (ListType == 5) {
                            detailIns.setItem(planFive.getFiveItemName());
                            detailIns.setItemCode(planFive.getFiveItemCode());
                        } else {
                            detailIns.setItem(planFour.getFourItemName());
                            detailIns.setItemCode(planFour.getFourItemCode());
                        }

                        detailIns.setFundType(fundType);
                        detailIns.setFillingId(fillingId);
                        detailIns.setProjectName(detailSecond.getProjectName());
                        detailIns.setProjectNumber(detailSecond.getProjectNumber());
                        detailIns.setFillingLnId(ln.getFillingLnId());
                        detailIns.setDueAmount(detailSecond.getDueAmount());
                        detailIns.setDueDate(detailSecond.getDueDate());
                        detailIns.setProjectId(detailSecond.getProjectId());
                        detailIns.setContractId(detailSecond.getContractId());
                        detailIns.setHostUnitId(detailSecond.getUnitId());
                        detailIns.setBankId(detailSecond.getBankId());
                        detailIns.setPaymentMethod(detailSecond.getPaymentMethod());
                        detailService.insertSelective(request, detailIns);
                    }
                }
            }
        }
    }

    /*四个业务部同一类型全部填报完成并提交审批通过后生成汇总报表*/
    /*汇总初始化年*/
    @Override
    public List<JcFundFillingDetail> initSummaryYear(IRequest request, JcFundFilling filling) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        String unitId = request.getAttributeMap().get("unitId").toString();
        //判断工作流进入 则需要四个部门都审批通过才创建汇总 功能页面进入则删除原数据 重新生成
        if ("Y".equalsIgnoreCase(filling.getWflFlag())) {
            //初始化先做头存入 注：要求四个业务部同维度单据都审批通过 才生成数据
            List<JcFundFilling> fillingList = fundFillingMapper.queryCheckApproved(filling);
            if (fillingList.size() == 4) {
                filling.setFillUserId(10478L);
                filling.setRefFillingId(filling.getFillingId());
                filling.setSummaryStatus("NEW");
                filling.setUnitId(107L);
                filling.setFillingId(null);
                filling.setDocumentType("SUMMARY");
                service.insert(request, filling);
            } else {
                return detailList;
            }

        } else {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        }
        Long fillingId = filling.getFillingId();
        Long refFillingId = filling.getRefFillingId();
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        //遍历查询结果并存表
        /*遍历项目*/
        JcFundingPlan plan = new JcFundingPlan();
        plan.setUnitCode(MONEY_DEPART);
        plan.setReportCode(REPORT_CODE);
        //资金部
        List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
        if (planList.size() > 0) {
            JcFundingPlanOne one = new JcFundingPlanOne();
            one.setFundingId(planList.get(0).getFundingId());
            one.setEnabledFlag("Y");
            List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
            //遍历以及目录
            if (oneList.size() > 0) {
                //一级目录插入资金填报明细行
                for (JcFundingPlanOne planOne : oneList) {
                    JcFundFillingLn lnOne = new JcFundFillingLn();
                    lnOne.setFillingId(fillingId);
                    lnOne.setFillItem(planOne.getItemName());
                    lnOne.setFillItemCode(planOne.getItemCode());
                    serviceLn.insert(request, lnOne);
                    //二级目录
                    JcFundingPlanSecond second = new JcFundingPlanSecond();
                    second.setFundingOneId(planOne.getFundingOneId());
                    second.setEnabledFlag("Y");
                    List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                    if (secondList.size() > 0) {
                        for (JcFundingPlanSecond planSecond : secondList) {
                            JcFundFillingLn lnSecond = new JcFundFillingLn();
                            lnSecond.setFillingId(fillingId);
                            lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                            lnSecond.setFillItem(planSecond.getSecondItemName());
                            serviceLn.insert(request, lnSecond);
                            //三级目录
                            JcFundingPlanThree three = new JcFundingPlanThree();
                            three.setFundingSecondId(planSecond.getFundingSecondId());
                            three.setEnabledFlag("Y");
                            List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                            if (threeList.size() > 0) {
                                for (JcFundingPlanThree planThree : threeList) {
                                    JcFundFillingLn lnThree = new JcFundFillingLn();
                                    lnThree.setFillingId(fillingId);
                                    lnThree.setFillItemCode(planThree.getThreeItemCode());
                                    lnThree.setFillItem(planThree.getThreeItemName());
                                    serviceLn.insert(request, lnThree);
                                    //三级级目录插入明细
                                    // insSummaryYear(request,filling,3L,planThree,null,null,lnThree);
                                    //四级目录
                                    JcFundingPlanFour four = new JcFundingPlanFour();
                                    four.setFundingThreeId(planThree.getFundingThreeId());
                                    four.setEnabledFlag("Y");
                                    List<JcFundingPlanFour> fourList = fourMapper.queryAll(four);
                                    if (fourList.size() > 0) {
                                        for (JcFundingPlanFour planFour : fourList) {
                                            JcFundFillingLn lnFour = new JcFundFillingLn();
                                            lnFour.setFillingId(fillingId);
                                            lnFour.setFillItemCode(planFour.getFourItemCode());
                                            lnFour.setFillItem(planFour.getFourItemName());
                                            lnFour.setUnitCode(planThree.getThreeItemCode());
                                            serviceLn.insert(request, lnFour);
                                            //四级级级目录插入明细
                                            insSummaryYear(request, filling, 4L, planThree, planFour, null, lnFour);

                                            if (BUSINESS_DEPT_OD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_TD.equalsIgnoreCase(planFour.getFourItemCode())
                                                    || BUSINESS_DEPT_THD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_ZD.equalsIgnoreCase(planFour.getFourItemCode())) {
                                                //五级目录
                                                JcFundingPlanFive five = new JcFundingPlanFive();
                                                five.setFundingFourId(planFour.getFundingFourId());
                                                five.setEnabledFlag("Y");
                                                List<JcFundingPlanFive> fiveList = fiveMapper.queryAll(five);
                                                if (fiveList.size() > 0) {
                                                    for (JcFundingPlanFive planFive : fiveList) {
                                                        JcFundFillingLn lnFive = new JcFundFillingLn();
                                                        lnFive.setFillingId(fillingId);
                                                        lnFive.setFillItemCode(planFive.getFiveItemCode());
                                                        lnFive.setFillItem(planFive.getFiveItemName());
                                                        lnFive.setUnitCode(planFour.getFourItemCode());
                                                        serviceLn.insert(request, lnFive);
                                                        //五级目录插入明细
                                                        insSummaryYear(request, filling, 5L, planThree, planFour, planFive, lnFive);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateSummaryAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }

    ;

    /*汇总初始化季度*/
    @Override
    public List<JcFundFillingDetail> initSummaryQuarter(IRequest request, JcFundFilling filling) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        String unitId = request.getAttributeMap().get("unitId").toString();
        //判断工作流进入 则需要四个部门都审批通过才创建汇总 功能页面进入则删除原数据 重新生成
        if ("Y".equalsIgnoreCase(filling.getWflFlag())) {
            //初始化先做头存入 注：要求四个业务部同维度单据都审批通过 才生成数据
            List<JcFundFilling> fillingList = fundFillingMapper.queryCheckApproved(filling);
            if (fillingList.size() == 4) {
                filling.setFillUserId(10478L);
                filling.setRefFillingId(filling.getFillingId());
                filling.setSummaryStatus("NEW");
                filling.setUnitId(107L);
                filling.setDocumentType("SUMMARY");
                filling.setFillingId(null);
                service.insert(request, filling);
            } else {
                return detailList;
            }

        } else {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        }
        Long fillingId = filling.getFillingId();
        Long refFillingId = filling.getRefFillingId();
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        //遍历查询结果并存表
        /*遍历项目*/
        JcFundingPlan plan = new JcFundingPlan();
        plan.setUnitCode(MONEY_DEPART);
        //资金部
        List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
        if (planList.size() > 0) {
            JcFundingPlanOne one = new JcFundingPlanOne();
            one.setFundingId(planList.get(0).getFundingId());
            one.setEnabledFlag("Y");
            List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
            //遍历以及目录
            if (oneList.size() > 0) {
                //一级目录插入资金填报明细行
                for (JcFundingPlanOne planOne : oneList) {
                    JcFundFillingLn lnOne = new JcFundFillingLn();
                    lnOne.setFillingId(fillingId);
                    lnOne.setFillItem(planOne.getItemName());
                    lnOne.setFillItemCode(planOne.getItemCode());
                    serviceLn.insert(request, lnOne);
                    //二级目录
                    JcFundingPlanSecond second = new JcFundingPlanSecond();
                    second.setFundingOneId(planOne.getFundingOneId());
                    second.setEnabledFlag("Y");
                    List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                    if (secondList.size() > 0) {
                        for (JcFundingPlanSecond planSecond : secondList) {
                            JcFundFillingLn lnSecond = new JcFundFillingLn();
                            lnSecond.setFillingId(fillingId);
                            lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                            lnSecond.setFillItem(planSecond.getSecondItemName());
                            serviceLn.insert(request, lnSecond);
                            //三级目录
                            JcFundingPlanThree three = new JcFundingPlanThree();
                            three.setFundingSecondId(planSecond.getFundingSecondId());
                            three.setEnabledFlag("Y");
                            List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                            if (threeList.size() > 0) {
                                for (JcFundingPlanThree planThree : threeList) {
                                    JcFundFillingLn lnThree = new JcFundFillingLn();
                                    lnThree.setFillingId(fillingId);
                                    lnThree.setFillItemCode(planThree.getThreeItemCode());
                                    lnThree.setFillItem(planThree.getThreeItemName());
                                    serviceLn.insert(request, lnThree);
                                    //四级目录
                                    JcFundingPlanFour four = new JcFundingPlanFour();
                                    four.setFundingThreeId(planThree.getFundingThreeId());
                                    four.setEnabledFlag("Y");
                                    List<JcFundingPlanFour> fourList = fourMapper.queryAll(four);
                                    if (fourList.size() > 0) {
                                        for (JcFundingPlanFour planFour : fourList) {
                                            JcFundFillingLn lnFour = new JcFundFillingLn();
                                            lnFour.setFillingId(fillingId);
                                            lnFour.setFillItemCode(planFour.getFourItemCode());
                                            lnFour.setFillItem(planFour.getFourItemName());
                                            lnFour.setUnitCode(planThree.getThreeItemCode());
                                            serviceLn.insert(request, lnFour);
                                            //四级目录插入明细
                                            insSummaryQuarter(request, filling, 4L, planThree, planFour, null, lnFour);
                                            if (BUSINESS_DEPT_OD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_TD.equalsIgnoreCase(planFour.getFourItemCode())
                                                    || BUSINESS_DEPT_THD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_ZD.equalsIgnoreCase(planFour.getFourItemCode())) {
                                                //五级目录
                                                JcFundingPlanFive five = new JcFundingPlanFive();
                                                five.setFundingFourId(planFour.getFundingFourId());
                                                five.setEnabledFlag("Y");
                                                List<JcFundingPlanFive> fiveList = fiveMapper.queryAll(five);
                                                if (fiveList.size() > 0) {
                                                    for (JcFundingPlanFive planFive : fiveList) {
                                                        JcFundFillingLn lnFive = new JcFundFillingLn();
                                                        lnFive.setFillingId(fillingId);
                                                        lnFive.setFillItemCode(planFive.getFiveItemCode());
                                                        lnFive.setFillItem(planFive.getFiveItemName());
                                                        lnFive.setUnitCode(planFour.getFourItemCode());
                                                        serviceLn.insert(request, lnFive);
                                                        //五级目录插入明细
                                                        insSummaryQuarter(request, filling, 5L, planThree, planFour, planFive, lnFive);

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateSummaryAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }

    ;

    /*汇总初始化月*/
    @Override
    public List<JcFundFillingDetail> initSummaryMon(IRequest request, JcFundFilling filling) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        String unitId = request.getAttributeMap().get("unitId").toString();
        //判断工作流进入 则需要四个部门都审批通过才创建汇总 功能页面进入则删除原数据 重新生成
        if ("Y".equalsIgnoreCase(filling.getWflFlag())) {
            //初始化先做头存入 注：要求四个业务部同维度单据都审批通过 才生成数据
            List<JcFundFilling> fillingList = fundFillingMapper.queryCheckApproved(filling);
            if (fillingList.size() == 4) {
                filling.setFillUserId(10478L);
                filling.setRefFillingId(filling.getFillingId());
                filling.setSummaryStatus("NEW");
                filling.setUnitId(107L);
                filling.setDocumentType("SUMMARY");
                filling.setFillingId(null);
                service.insert(request, filling);
            } else {
                return detailList;
            }

        } else {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        }
        Long fillingId = filling.getFillingId();
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        //遍历查询结果并存表
        /*遍历项目*/
        JcFundingPlan plan = new JcFundingPlan();
        plan.setUnitCode(MONEY_DEPART);
        //资金部
        List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
        if (planList.size() > 0) {
            JcFundingPlanOne one = new JcFundingPlanOne();
            one.setFundingId(planList.get(0).getFundingId());
            one.setEnabledFlag("Y");
            List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
            //遍历以及目录
            if (oneList.size() > 0) {
                //一级目录插入资金填报明细行
                for (JcFundingPlanOne planOne : oneList) {
                    JcFundFillingLn lnOne = new JcFundFillingLn();
                    lnOne.setFillingId(fillingId);
                    lnOne.setFillItem(planOne.getItemName());
                    lnOne.setFillItemCode(planOne.getItemCode());
                    serviceLn.insert(request, lnOne);
                    //二级目录
                    JcFundingPlanSecond second = new JcFundingPlanSecond();
                    second.setFundingOneId(planOne.getFundingOneId());
                    second.setEnabledFlag("Y");
                    List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                    if (secondList.size() > 0) {
                        for (JcFundingPlanSecond planSecond : secondList) {
                            JcFundFillingLn lnSecond = new JcFundFillingLn();
                            lnSecond.setFillingId(fillingId);
                            lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                            lnSecond.setFillItem(planSecond.getSecondItemName());
                            serviceLn.insert(request, lnSecond);
                            //三级目录
                            JcFundingPlanThree three = new JcFundingPlanThree();
                            three.setFundingSecondId(planSecond.getFundingSecondId());
                            three.setEnabledFlag("Y");
                            List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                            if (threeList.size() > 0) {
                                for (JcFundingPlanThree planThree : threeList) {
                                    JcFundFillingLn lnThree = new JcFundFillingLn();
                                    lnThree.setFillingId(fillingId);
                                    lnThree.setFillItemCode(planThree.getThreeItemCode());
                                    lnThree.setFillItem(planThree.getThreeItemName());
                                    serviceLn.insert(request, lnThree);
                                    //四级目录
                                    JcFundingPlanFour four = new JcFundingPlanFour();
                                    four.setFundingThreeId(planThree.getFundingThreeId());
                                    four.setEnabledFlag("Y");
                                    List<JcFundingPlanFour> fourList = fourMapper.queryAll(four);
                                    if (fourList.size() > 0) {
                                        for (JcFundingPlanFour planFour : fourList) {
                                            JcFundFillingLn lnFour = new JcFundFillingLn();
                                            lnFour.setFillingId(fillingId);
                                            lnFour.setFillItemCode(planFour.getFourItemCode());
                                            lnFour.setFillItem(planFour.getFourItemName());
                                            lnFour.setUnitCode(planThree.getThreeItemCode());
                                            serviceLn.insert(request, lnFour);
                                            //四级级级目录插入明细
                                            insSummaryMon(request, filling, 4L, planThree, planFour, null, lnFour);
                                            if (BUSINESS_DEPT_OD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_TD.equalsIgnoreCase(planFour.getFourItemCode())
                                                    || BUSINESS_DEPT_THD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_ZD.equalsIgnoreCase(planFour.getFourItemCode())) {
                                                //五级目录
                                                JcFundingPlanFive five = new JcFundingPlanFive();
                                                five.setFundingFourId(planFour.getFundingFourId());
                                                five.setEnabledFlag("Y");
                                                List<JcFundingPlanFive> fiveList = fiveMapper.queryAll(five);
                                                if (fiveList.size() > 0) {
                                                    for (JcFundingPlanFive planFive : fiveList) {
                                                        JcFundFillingLn lnFive = new JcFundFillingLn();
                                                        lnFive.setFillingId(fillingId);
                                                        lnFive.setFillItemCode(planFive.getFiveItemCode());
                                                        lnFive.setFillItem(planFive.getFiveItemName());
                                                        lnFive.setUnitCode(planFour.getFourItemCode());
                                                        serviceLn.insert(request, lnFive);
                                                        //五级级级目录插入明细
                                                        insSummaryMon(request, filling, 5L, planThree, planFour, planFive, lnFive);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateSummaryAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }

    ;

    /*汇总初始化周*/
    @Override
    public List<JcFundFillingDetail> initSummaryWeek(IRequest request, JcFundFilling filling) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        JcFundFillingDetail detail = new JcFundFillingDetail();
        String fillYear = filling.getFillYear();
        String fillType = filling.getFillType();
        Long hostUnitId = filling.getUnitId();
        String fillWeek = filling.getFillWeek();
        String fillMon = filling.getFillMon();
        String fillQuarter = filling.getFillQuarter();
        String unitId = request.getAttributeMap().get("unitId").toString();
        //判断工作流进入 则需要四个部门都审批通过才创建汇总 功能页面进入则删除原数据 重新生成
        if ("Y".equalsIgnoreCase(filling.getWflFlag())) {
            //初始化先做头存入 注：要求四个业务部同维度单据都审批通过 才生成数据
            List<JcFundFilling> fillingList = fundFillingMapper.queryCheckApproved(filling);
            if (fillingList.size() == 4) {
                filling.setFillUserId(10478L);
                filling.setRefFillingId(filling.getFillingId());
                filling.setSummaryStatus("NEW");
                filling.setUnitId(107L);
                filling.setFillingId(null);
                filling.setDocumentType("SUMMARY");
                service.insert(request, filling);
            } else {
                return detailList;
            }

        } else {
            fundFillingMapper.deleteFundLn(filling);
            fundFillingMapper.deleteFundDetail(filling);
        }
        Long fillingId = filling.getFillingId();
        Long refFillingId = filling.getRefFillingId();
        detail.setHostUnitId(hostUnitId);
        detail.setFillingId(fillingId);
        detail.setFillYear(fillYear);
        detail.setFillQuarter(fillQuarter);
        detail.setFillType(fillType);
        detail.setFillWeek(fillWeek);
        detail.setFillMon(fillMon);
        //遍历查询结果并存表
        /*遍历项目*/
        JcFundingPlan plan = new JcFundingPlan();
        plan.setUnitCode(MONEY_DEPART);
        //资金部
        List<JcFundingPlan> planList = planMapper.queryUintCodeList(plan);
        if (planList.size() > 0) {
            JcFundingPlanOne one = new JcFundingPlanOne();
            one.setFundingId(planList.get(0).getFundingId());
            one.setEnabledFlag("Y");
            List<JcFundingPlanOne> oneList = oneMapper.queryList(one);
            //遍历以及目录
            if (oneList.size() > 0) {
                //一级目录插入资金填报明细行
                for (JcFundingPlanOne planOne : oneList) {
                    JcFundFillingLn lnOne = new JcFundFillingLn();
                    lnOne.setFillingId(fillingId);
                    lnOne.setFillItem(planOne.getItemName());
                    lnOne.setFillItemCode(planOne.getItemCode());
                    serviceLn.insert(request, lnOne);
                    //二级目录
                    JcFundingPlanSecond second = new JcFundingPlanSecond();
                    second.setFundingOneId(planOne.getFundingOneId());
                    second.setEnabledFlag("Y");
                    List<JcFundingPlanSecond> secondList = secondMapper.queryAll(second);
                    if (secondList.size() > 0) {
                        for (JcFundingPlanSecond planSecond : secondList) {
                            JcFundFillingLn lnSecond = new JcFundFillingLn();
                            lnSecond.setFillingId(fillingId);
                            lnSecond.setFillItemCode(planSecond.getSecondItemCode());
                            lnSecond.setFillItem(planSecond.getSecondItemName());
                            serviceLn.insert(request, lnSecond);
                            //三级目录
                            JcFundingPlanThree three = new JcFundingPlanThree();
                            three.setFundingSecondId(planSecond.getFundingSecondId());
                            three.setEnabledFlag("Y");
                            List<JcFundingPlanThree> threeList = threeMapper.queryAll(three);
                            if (threeList.size() > 0) {
                                for (JcFundingPlanThree planThree : threeList) {
                                    JcFundFillingLn lnThree = new JcFundFillingLn();
                                    lnThree.setFillingId(fillingId);
                                    lnThree.setFillItemCode(planThree.getThreeItemCode());
                                    lnThree.setFillItem(planThree.getThreeItemName());
                                    serviceLn.insert(request, lnThree);
                                    //四级目录
                                    JcFundingPlanFour four = new JcFundingPlanFour();
                                    four.setFundingThreeId(planThree.getFundingThreeId());
                                    four.setEnabledFlag("Y");
                                    List<JcFundingPlanFour> fourList = fourMapper.queryAll(four);
                                    if (fourList.size() > 0) {
                                        for (JcFundingPlanFour planFour : fourList) {
                                            JcFundFillingLn lnFour = new JcFundFillingLn();
                                            lnFour.setFillingId(fillingId);
                                            lnFour.setFillItemCode(planFour.getFourItemCode());
                                            lnFour.setFillItem(planFour.getFourItemName());
                                            lnFour.setUnitCode(planThree.getThreeItemCode());
                                            serviceLn.insert(request, lnFour);
                                            //四级级级目录插入明细
                                            insSummaryWeek(request, filling, 4L, planThree, planFour, null, lnFour);
                                            if (BUSINESS_DEPT_OD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_TD.equalsIgnoreCase(planFour.getFourItemCode())
                                                    || BUSINESS_DEPT_THD.equalsIgnoreCase(planFour.getFourItemCode()) || BUSINESS_DEPT_ZD.equalsIgnoreCase(planFour.getFourItemCode())) {
                                                //五级目录
                                                JcFundingPlanFive five = new JcFundingPlanFive();
                                                five.setFundingFourId(planFour.getFundingFourId());
                                                five.setEnabledFlag("Y");
                                                List<JcFundingPlanFive> fiveList = fiveMapper.queryAll(five);
                                                if (fiveList.size() > 0) {
                                                    for (JcFundingPlanFive planFive : fiveList) {
                                                        JcFundFillingLn lnFive = new JcFundFillingLn();
                                                        lnFive.setFillingId(fillingId);
                                                        lnFive.setFillItemCode(planFive.getFiveItemCode());
                                                        lnFive.setFillItem(planFive.getFiveItemName());
                                                        lnFive.setUnitCode(planFour.getFourItemCode());
                                                        serviceLn.insert(request, lnFive);
                                                        //五级目录插入明细
                                                        insSummaryWeek(request, filling, 5L, planThree, planFour, planFive, lnFive);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //行表更新jc_fund_filling_ln
        detailService.updateSummaryAmount(request, detail);
        if (detailList.size() <= 0) {
            JcFundFillingDetail detailReturn = new JcFundFillingDetail();
            detailReturn.setFillingId(fillingId);
            detailList.add(detailReturn);
        }
        return detailList;
    }

    @Override
    public void fundIncome(IRequest request, FundingExecute execute, String summaryFlag) {
        FundingExecuteLn executeLnQuery = new FundingExecuteLn();
        executeLnQuery.setFillType(execute.getFillType());
        executeLnQuery.setFillingId(execute.getFillingId());
        for (int i = 0; i < EXECUTE_INT; i++) {
            FundingExecuteLn executeLn = new FundingExecuteLn();
            executeLn.setExcuteId(execute.getExcuteId());
            if (i == 0) {
                executeLn.setItemCode(INPUT);
                if ("N".equalsIgnoreCase(summaryFlag)) {
                    executeLnQuery = executeLnMapper.queryAmount(executeLnQuery);
                } else {
                    executeLnQuery = executeLnMapper.queryAmountSummary(executeLnQuery);
                }

                if (executeLnQuery != null) {
                    executeLn.setExecuteAmount(executeLnQuery.getExecuteAmount());
                    executeLn.setPlanAmount(executeLnQuery.getPlanAmount());
                }
            } else {
                executeLnQuery.setFillType(execute.getFillType());
                executeLnQuery.setFillingId(execute.getFillingId());
                executeLn.setItemCode(OUTPUT);
                if ("N".equalsIgnoreCase(summaryFlag)) {
                    executeLnQuery = executeLnMapper.queryAmountOut(executeLnQuery);
                } else {
                    executeLnQuery = executeLnMapper.queryAmountSummaryOut(executeLnQuery);
                }

                if (executeLnQuery != null) {
                    executeLn.setExecuteAmount(executeLnQuery.getExecuteAmount());
                    executeLn.setPlanAmount(executeLnQuery.getPlanAmount());
                }
            }
            executeLnService.insertSelective(request, executeLn);
        }
    }

    @Override
    public void fundExpenditure(IRequest request, FundingExecute execute, String summaryFlag) {
        FundingExecuteLn executeLnQuery = new FundingExecuteLn();
        executeLnQuery.setFillingId(execute.getFillingId());
        executeLnQuery.setFillType(execute.getFillType());
        for (int i = 0; i < EXECUTE_INT; i++) {
            FundingExecuteLn executeLn = new FundingExecuteLn();
            executeLn.setExcuteId(execute.getExcuteId());
            if (i == 0) {
                executeLn.setItemCode(INPUT);
                executeLn.setExecuteAmount(0D);
                executeLn.setPlanAmount(0D);

            } else {
                executeLnQuery.setFillingId(execute.getFillingId());
                executeLnQuery.setFillType(execute.getFillType());
                executeLn.setItemCode(OUTPUT);
                executeLnQuery = executeLnMapper.queryAmountSummarysOut(executeLnQuery);

                if (executeLnQuery != null) {
                    executeLn.setExecuteAmount(0D);
                    executeLn.setPlanAmount(executeLnQuery.getPlanAmount());
                }
            }
            executeLnService.insertSelective(request, executeLn);
        }
    }

    /*资金计划执行 数据生成*/
    @Override
    public void initExecutInit(IRequest request, JcFundFilling filling, String summaryFlag) {
        FundingExecute execute1 = new FundingExecute();
        FundingExecute execute2 = new FundingExecute();
        //汇总分需要生成两份数据
        if ("N".equalsIgnoreCase(summaryFlag)) {
            BeanUtils.copyProperties(filling, execute1);
            execute1.setConfirmStatus("NEW");
            execute1.setDimensionType("APARTMENT");
            executeService.insertSelective(request, execute1);
            execute1.setFillType(filling.getFillType());
            //行  收入 支出
            fundIncome(request, execute1, summaryFlag);
        } else {
            BeanUtils.copyProperties(filling, execute1);
            BeanUtils.copyProperties(filling, execute2);
            execute1.setConfirmStatus("NEW");
            execute1.setDimensionType("CONCLUSION");
            FndOrgUnit unit1 = new FndOrgUnit();
            unit1.setUnitCode(ACCOUNTING_DEPT);
            List<FndOrgUnit> orgUnitList1 = unitMapper.selectUnit(unit1);
            //标注原资金部改为财务会计部 by20211108
            execute1.setUnitId(orgUnitList1.get(0).getUnitId());
            FndOrgUnit unit = new FndOrgUnit();
            unit.setUnitCode(INTEGRATED_MGT_DEPT);
            List<FndOrgUnit> orgUnitList = unitMapper.selectUnit(unit);
            execute2.setConfirmStatus("NEW");
            execute2.setDimensionType("APARTMENT");
            execute2.setUnitId(orgUnitList.get(0).getUnitId());
            executeService.insertSelective(request, execute1);
            executeService.insertSelective(request, execute2);
            execute1.setFillType(filling.getFillType());
            execute2.setFillType(filling.getFillType());
            //行  收入 支出
            fundIncome(request, execute1, summaryFlag);
            fundExpenditure(request, execute2, summaryFlag);
        }
    }


    public void fundIncomeInit(IRequest request, FundingExecute execute, String summaryFlag) {
        FundingExecuteLn executeLnQuery = new FundingExecuteLn();
        executeLnQuery.setExcuteId(execute.getExcuteId());
        executeLnQuery.setFillingId(execute.getFillingId());
        executeLnQuery.setFillType(execute.getFillType());
        List<FundingExecuteLn> executeLnList = executeLnMapper.queryAll(executeLnQuery);
        for (FundingExecuteLn ln : executeLnList) {
            if (INPUT.equalsIgnoreCase(ln.getItemCode())) {
                if ("N".equalsIgnoreCase(summaryFlag)) {
                    executeLnQuery = executeLnMapper.queryAmount(executeLnQuery);
                } else {
                    executeLnQuery = executeLnMapper.queryAmountSummary(executeLnQuery);
                }

                if (executeLnQuery != null) {
                    ln.setExecuteAmount(executeLnQuery.getExecuteAmount());
                    ln.setPlanAmount(executeLnQuery.getPlanAmount());
                }
            } else {
                executeLnQuery.setExcuteId(execute.getExcuteId());
                executeLnQuery.setFillType(execute.getFillType());
                executeLnQuery.setFillingId(execute.getFillingId());
                if ("N".equalsIgnoreCase(summaryFlag)) {
                    executeLnQuery = executeLnMapper.queryAmountOut(executeLnQuery);
                } else {
                    executeLnQuery = executeLnMapper.queryAmountSummaryOut(executeLnQuery);
                }

                if (executeLnQuery != null) {
                    ln.setExecuteAmount(executeLnQuery.getExecuteAmount());
                    ln.setPlanAmount(executeLnQuery.getPlanAmount());
                }
            }
            executeLnService.updateByPrimaryKeySelective(request, ln);
        }
    }


    public void fundExpenditureInit(IRequest request, FundingExecute execute) {
        FundingExecuteLn executeLnQuery = new FundingExecuteLn();
        executeLnQuery.setExcuteId(execute.getExcuteId());
        executeLnQuery.setFillType(execute.getFillType());
        executeLnQuery.setFillingId(execute.getFillingId());
        List<FundingExecuteLn> executeLnList = executeLnMapper.queryAll(executeLnQuery);
        for (FundingExecuteLn ln : executeLnList) {
            if (INPUT.equalsIgnoreCase(ln.getItemCode())) {
                ln.setExecuteAmount(0D);
                ln.setPlanAmount(0D);

            } else {
                executeLnQuery.setExcuteId(execute.getExcuteId());
                executeLnQuery.setFillType(execute.getFillType());
                executeLnQuery.setFillingId(execute.getFillingId());
                executeLnQuery = executeLnMapper.queryAmountSummarysOut(executeLnQuery);
                if (executeLnQuery != null) {
                    ln.setExecuteAmount(0D);
                    ln.setPlanAmount(executeLnQuery.getPlanAmount());
                }
            }
            executeLnService.updateByPrimaryKeySelective(request, ln);
        }
    }

    /*资金计划执行情况*/
    @Override
    public void initExecut(IRequest request, FundingExecute execute, String summaryFlag) {
        List<FundingExecute> executeList = executeMapper.queryByfillingId(execute);
        if (executeList.size() > 0) {
            if (INTEGRATED_MGT_DEPT.equalsIgnoreCase(executeList.get(0).getUnitCode())) {
                fundExpenditureInit(request, executeList.get(0));
            } else {
                fundIncomeInit(request, executeList.get(0), summaryFlag);
            }
        }

    }

    @Override
    public List<JcFundFilling> queryAllByUnitReq(IRequest request, JcFundFilling filling, int page, int pageSize) {
        return fundFillingMapper.queryAllByUnitReq(filling);
    }

    ;

    //计划调整变更创建
    @Override
    public JcFundFilling createReq(IRequest iRequest, JcFundFilling fillingReq) throws Exception {
        //throw new IllegalArgumentException("请先维护lpr利率，再做调息！");
        JcFundFilling filling = new JcFundFilling();
        filling.setPlanStatus("APPROVED");
        filling.setFillType("WEEK");
        filling.setFillYear(fillingReq.getFillYear());
        filling.setFillMon(fillingReq.getFillMon());
        filling.setFillWeek(fillingReq.getFillWeek());
        filling.setUnitId(Long.valueOf(iRequest.getAttributeMap().get("unitId").toString()));
        List<JcFundFilling> fillingList = fundFillingMapper.queryReq(filling);
        if (fillingList.size() > 0) {
            if (fillingList.size() != 1) {
                throw new IllegalArgumentException("每年每月有且还能有一个相同周计划，请检查该周计划是否存在相同周填报！");
            } else {
                for (JcFundFilling fundFilling : fillingList) {
                    fundFilling.setReqStatus("NEW");
                    service.updateByPrimaryKeySelective(iRequest,fundFilling);
                    JcFundFillingLn ln = new JcFundFillingLn();
                    FundFillingReqLn reqLn = new FundFillingReqLn();
                    ln.setFillingId(fundFilling.getFillingId());
                    List<JcFundFillingLn> lnList = lnMapper.queryAllReq(ln);
                    if (lnList.size() > 0) {
                        for (JcFundFillingLn fundFillingLn : lnList) {
                            BeanUtils.copyProperties(fundFillingLn, reqLn);
                            reqLnService.insertSelective(iRequest, reqLn);

                            JcFundFillingDetail detail = new JcFundFillingDetail();
                            FundFillingReqDetail reqDetail = new FundFillingReqDetail();
                            detail.setFillingLnId(fundFillingLn.getFillingLnId());
                            List<JcFundFillingDetail> detailList = mapper.queryAllByLnId(detail);
                            if (detailList.size() > 0) {
                                for (JcFundFillingDetail fillingDetail : detailList) {
                                    BeanUtils.copyProperties(fillingDetail, reqDetail);
                                    reqDetail.setFillingLnId(reqLn.getFillingLnId());
                                    reqDetailService.insertSelective(iRequest, reqDetail);
                                }
                            }

                        }
                    }
                }
            }

        } else {
            throw new IllegalArgumentException("该周计划不存在，请在资金计划填报功能创建！");
        }
        return fillingList.get(0);
    }


    /*提交工作流*/
    @Override
    public JcFundFilling fillingReqSubmitWfl(IRequest iRequest, JcFundFilling filling) throws HlsCusException {
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling.setFillingId(filling.getFillingId());
        fundFilling = fundFillingMapper.queryAll(fundFilling).get(0);
        if ("APPROVING".equalsIgnoreCase(fundFilling.getReqStatus()) || "APPROVED".equalsIgnoreCase(fundFilling.getReqStatus())) {
            throw new HlsCusException("该单据不可提交,请检查单据状态！");
        }
        String fundType = "";
        if ("WEEK".equalsIgnoreCase(fundFilling.getFillType())) {
            fundType = fundFilling.getFillYear() + "-" + fundFilling.getFillQuarterN() + "-" + fundFilling.getFillMonN() + "-" + fundFilling.getFillWeekN();
        }

        List<JcFundFilling> fillingList = new ArrayList<>();
        fillingList.add(fundFilling);
        databaseLockProvider.lock(fundFilling);
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "FUNDING_PLAN_WFL_REQ");
        params.put("jcFundFilling", JSON.toJSONString(fundFilling));
        params.put("fillingId", fundFilling.getFillingId());
        params.put("fillType", fundFilling.getFillType());
        params.put("fillYear", fundFilling.getFillYear());
        params.put("fillQuarter", fundFilling.getFillQuarter());
        params.put("fillMon", fundFilling.getFillMon());
        params.put("fillWeek", fundFilling.getFillWeek());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "FUNDING_PLAN_WFL_REQ");
        params.put(IActivitiCommonService.DEMO_NAME, "FUNDING_PLAN_WFL_NEW");
        params.put(IActivitiCommonService.BUSINESS_KEY, fundFilling.getFillingId());
        params.put("documentNumber", fundFilling.getFillTypeN() + "-" + fundType);
        params.put("documentName", fundFilling.getFillTypeN() + "调整审批申请");
        params.put("companyId", iRequest.getCompanyId());
        params.put("unitId", iRequest.getAttribute("unitId"));
        params.put("documentCategory", "FUNDING_PLAN_WFL_REQ");
        params.put("documentType", "FUNDING_PLAN_WFL_REQ");
        activitiStartService.start(iRequest, fillingList, params);

        fundFilling.setReqStatus("APPROVING");
        fundFilling = self().updateByPrimaryKeySelective(iRequest, fundFilling);
        return fundFilling;
    }
}