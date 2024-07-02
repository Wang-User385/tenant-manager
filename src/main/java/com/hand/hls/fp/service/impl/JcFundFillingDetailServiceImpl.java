package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.dto.*;
import com.hand.hls.fp.mapper.*;
import com.hand.hls.fp.service.FundFillingReqLnService;
import com.hand.hls.fp.service.JcFundFillingLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.service.JcFundFillingDetailService;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bstek.ureport.expression.model.condition.Join.and;
import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static java.lang.Enum.valueOf;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundFillingDetailServiceImpl extends BaseServiceImpl<JcFundFillingDetail> implements JcFundFillingDetailService {
    @Autowired
    private JcFundFillingDetailMapper mapper;
    @Autowired
    private FundFillingReqDetailMapper reqDetailMapper;
    @Autowired
    private JcFundFillingLnMapper lnMapper;
    @Autowired
    private FundFillingReqLnMapper reqLnMapper;
    @Autowired
    private JcFundingPlanMapper planMapper;
    @Autowired
    private JcFundFillingDetailService service;
    @Autowired
    private JcFundFillingLnService serviceLn;
    @Autowired
    private FundFillingReqLnService reqLnService;
    @Autowired
    private JcFundingPlanOneMapper oneMapper;
    private static String BUSINESS_DEPART = "BUSINESS_DEPART";
    @Autowired
    private JcFundingPlanSecondMapper secondMapper;
    @Autowired
    private JcFundingPlanThreeMapper threeMapper;
    @Autowired
    /**
     * 定义年份数组
     */
    private static final String[] YEAR_LIST = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13"};
    private static final String[] FIRST = new String[]{"01", "02", "03"};
    private static final String[] SECOND = new String[]{"04", "05", "06"};
    private static final String[] THIRD = new String[]{"07", "08", "09"};
    private static final String[] FOURTH = new String[]{"10", "11", "12"};
    private static final String ONE_ITEM = "JY_INFLOW";
    private static final String JY_OUTFLOW = "JY_OUTFLOW";
    private static final String JY_INFLOW = "JY_INFLOW";
    private static final String PRINCIPAL = "PRINCIPAL";
    private static final String INTEREST = "INTEREST";
    private static final String DEPOSIT = "DEPOSIT";
    private static final String LEGAL_FEE = "LEGAL_FEE";
    private static final String LOAN_DELIVERY = "LOAN_DELIVERY";
    private static final String OTHER = "OTHER";
    private static final Double INIT_AMOUNT = 0D;
    /*下面四个类型通过本金 利息 手续费 保证金进行区分汇总*/
    private static final String IN_THE = "IN_THE";
    private static final String SX_KYH = "SX_KYH";
    private static final String SX_BLH = "SX_BLH";
    private static final String TJ_KYH = "TJ_KYH";
    private static final String TJ_BLH = "TJ_BLH";
    private static final String[] WEEK_LIST = new String[]{"01", "02", "03", "04"};
    private static final String[] DAY_LIST = new String[13];
    private static final String INITIAL_BALANCE = "INITIAL_BALANCE";
    private static final String SX_KYHA = "SX_KYHA";
    private static final String SX_BLHA = "SX_BLHA";
    private static final String TJ_KYHA = "TJ_KYHA";
    private static final String TJ_BLHA = "TJ_BLHA";
    private static final String ZB_AMOUNT = "ZB_AMOUNT";
    private static final String WD_AMOUNT_NEW = "WD_AMOUNT_NEW";
    private static final String WD_AMOUNT_CHANGE = "WD_AMOUNT_CHANGE";
    private static final String YD_AMOUNT_NEW = "YD_AMOUNT_NEW";
    private static final String ZQ_AMOUNT = "ZQ_AMOUNT";
    private static final String PJ_AMOUNT = "PJ_AMOUNT";
    private static final String OTHER_AMOUNT = "OTHER_AMOUNT";
    private static final String ZB_AMOUNT1 = "ZB_AMOUNT1";
    private static final String WD_AMOUNT_NEW1 = "WD_AMOUNT_NEW1";
    private static final String WD_AMOUNT_CHANGE1 = "WD_AMOUNT_CHANGE1";
    private static final String YD_AMOUNT_NEW1 = "YD_AMOUNT_NEW1";
    private static final String ZQ_AMOUNT1 = "ZQ_AMOUNT1";
    private static final String PJ_AMOUNT1 = "PJ_AMOUNT1";
    private static final String OTHER_AMOUNT1 = "OTHER_AMOUNT1";
    private static final String CZ_INFLOWA = "CZ_INFLOWA";
    private static final String JY_INFLOWA = "JY_INFLOWA";
    private static final String HK_DETAIL = "HK_DETAIL";
    private static final String HK_ACCOUNT = "HK_ACCOUNT";
    private static final String BUSINESS_DEPT_ZD = "BUSINESS_DEPT_ZD";
    private static final String BUSINESS_DEPT_OD = "BUSINESS_DEPT_OD";
    private static final String BUSINESS_DEPT_TD = "BUSINESS_DEPT_TD";
    private static final String BUSINESS_DEPT_THD = "BUSINESS_DEPT_THD";
    private static final String BUSINESS_DEPT_ZD1 = "BUSINESS_DEPT_ZD1";
    private static final String BUSINESS_DEPT_OD1 = "BUSINESS_DEPT_OD1";
    private static final String BUSINESS_DEPT_TD1 = "BUSINESS_DEPT_TD1";
    private static final String BUSINESS_DEPT_THD1 = "BUSINESS_DEPT_THD1";
    private static final String SX_KYHC = "SX_KYHC";
    private static final String SX_BLHC = "SX_BLHC";
    private static final String TJ_KYHC = "TJ_KYHC";
    private static final String TJ_BLHC = "TJ_BLHC";
    private static final String CAPITAL_INFLOW = "CAPITAL_INFLOW";
    private static final String CAPITAL_OUTFLOW = "CAPITAL_OUTFLOW";
    private static final String END_BALANCE = "END_BALANCE";
    private static final String CZ_OUTFLOWA = "CZ_OUTFLOWA";
    private static final String TZ_OUTFLOWA = "TZ_OUTFLOWA";
    private static final String WD_AMOUNT_A = "WD_AMOUNT_A";
    private static final String YD_AMOUNT_A = "YD_AMOUNT_A";
    private static final String ZQ_AMOUNT_A = "ZQ_AMOUNT_A";
    private static final String PJ_AMOUNT_A = "PJ_AMOUNT_A";
    private static final String PRINCIPAL_WD = "PRINCIPAL_WD";
    private static final String INTEREST_WD = "INTEREST_WD";
    private static final String LEGAL_FEE_WD = "LEGAL_FEE_WD";
    private static final String DEPOSIT_WD = "DEPOSIT_WD";
    private static final String PRINCIPAL_WD_TJ = "PRINCIPAL_WD_TJ";
    private static final String INTEREST_WD_TJ = "INTEREST_WD_TJ";
    private static final String LEGAL_FEE_WD_TJ = "LEGAL_FEE_WD_TJ";
    private static final String DEPOSIT_WD_TJ = "DEPOSIT_WD_TJ";
    private static final String PRINCIPAL_YD = "PRINCIPAL_YD";
    private static final String INTEREST_YD = "INTEREST_YD";
    private static final String LEGAL_FEE_YD = "LEGAL_FEE_YD";
    private static final String DEPOSIT_YD = "DEPOSIT_YD";
    private static final String PRINCIPAL_YD1 = "PRINCIPAL_YD1";
    private static final String INTEREST_YD1 = "INTEREST_YD1";
    private static final String LEGAL_FEE_YD1 = "LEGAL_FEE_YD1";
    private static final String DEPOSIT_YD1 = "DEPOSIT_YD1";
    private static final String PRINCIPAL_YD2 = "PRINCIPAL_YD2";
    private static final String INTEREST_YD2 = "INTEREST_YD2";
    private static final String LEGAL_FEE_YD2 = "LEGAL_FEE_YD2";
    private static final String DEPOSIT_YD2 = "DEPOSIT_YD2";
    private static final String PRINCIPAL_YD3 = "PRINCIPAL_YD3";
    private static final String INTEREST_YD3 = "INTEREST_YD3";
    private static final String LEGAL_FEE_YD3 = "LEGAL_FEE_YD3";
    private static final String DEPOSIT_YD3 = "DEPOSIT_YD3";
    private static final String PRINCIPAL_ZQ = "PRINCIPAL_ZQ";
    private static final String INTEREST_ZQ = "INTEREST_ZQ";
    private static final String FEE_ZQ = "FEE_ZQ";
    private static final String LEGAL_FEE_PJ = "LEGAL_FEE_PJ";
    private static final String DEPOSIT_PJ = "DEPOSIT_PJ";
    private static final String PLUS_PJ = "PLUS_PJ";
    private static final String LEGAL_FEE_PJ_TJ = "LEGAL_FEE_PJ_TJ";
    private static final String DEPOSIT_PJ_TJ = "DEPOSIT_PJ_TJ";
    private static final String PLUS_PJ_TJ = "PLUS_PJ_TJ";
    private static final String SALARY_BONUS = "SALARY_BONUS";
    private static final String TAX_FEE = "TAX_FEE";
    private static final String PURCHASE_OTHER = "PURCHASE_OTHER";
    private static final String TJ_BLHB = "TJ_BLHB";
    private static final String SX_BLHB = "SX_BLHB";
    private static final String TJ_KYHB = "TJ_KYHB";
    private static final String JY_OUTFLOWA = "JY_OUTFLOWA";
    private static final String SX_KYHB = "SX_KYHB";
    private static final Long companyIdSx = 248L;
    private static final Long companyIdTj = 313L;
    private static final String REPORT_END_BALANCE = "JC-20200311-003";
    private static final String REPORT_END_BALANCE_DETAIL = "JC-20200311-004";
//    private static final Long SummaryInit = 0L;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private JcFundPlanScheduleMapper scheduleMapper;
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

    /*
     * 行表金额更新jc_fund_filling_ln
     */
    @Override
    public List<JcFundFillingDetail> updateFundLnAmount(IRequest request, JcFundFillingDetail detail) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        Long fillingId = detail.getFillingId();
        String fillYear = detail.getFillYear();
        String fillMon = detail.getFillMon();
        String fillWeek = detail.getFillWeek();
        Long unitId = detail.getHostUnitId();
        String fillType = detail.getFillType();
        String fillQuarter = detail.getFillQuarter();
        JcFundFillingLn ln = new JcFundFillingLn();
        ln.setFillingId(fillingId);
        //根据fillType类型不同 赋予其不同的数组 年 季度
        String[] queryList = new String[]{};
        if ("YEAR".equalsIgnoreCase(fillType)) {
            queryList = YEAR_LIST;
        } else if ("QUARTER".equalsIgnoreCase(fillType)) {
            if ("FIRST".equalsIgnoreCase(fillQuarter)) {
                queryList = FIRST;
            } else if ("SECOND".equalsIgnoreCase(fillQuarter)) {
                queryList = SECOND;
            } else if ("THIRD".equalsIgnoreCase(fillQuarter)) {
                queryList = THIRD;
            } else if ("FOURTH".equalsIgnoreCase(fillQuarter)) {
                queryList = FOURTH;
            }
        } else if ("MON".equalsIgnoreCase(fillType)) {
            queryList = WEEK_LIST;
        } else if ("WEEK".equalsIgnoreCase(fillType)) {
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
            }
            queryList = DAY_LIST;
        }
        List<JcFundFillingLn> lnList = lnMapper.queryAllForUpdateAmount(ln);
        if (lnList.size() > 0) {
            for (JcFundFillingLn lnSec : lnList) {
                //年计划 遍历 1到12月  12个字段
                for (int i = 0; i < queryList.length; i++) {
                    if (queryList[i] != null) {
                        String fundType = "";
                        if ("MON".equalsIgnoreCase(fillType)) {
                            fundType = fillYear + "-" + fillMon + "-" + queryList[i];
                        } else if ("WEEK".equalsIgnoreCase(fillType)) {
                            fundType = queryList[i];
                        } else {
                            fundType = fillYear + "-" + queryList[i];
                        }

                        JcFundFillingDetail detailSec = new JcFundFillingDetail();
                        detailSec.setItemCode(lnSec.getFillItemCode());
                        detailSec.setFundType(fundType);
                        detailSec.setFillWeek(fillWeek);
                        detailSec.setFillMon(fillMon);
                        detailSec.setFillYear(fillYear);
                        detailSec.setHostUnitId(unitId);
                        detailSec.setFillingId(fillingId);
                        detailSec.setFillingLnId(lnSec.getFillingLnId());
                        if (PRINCIPAL.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST.equalsIgnoreCase(lnSec.getFillItemCode())
                                || DEPOSIT.equalsIgnoreCase(lnSec.getFillItemCode()) || LEGAL_FEE.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailTotalSec(detailSec);
                        } else if (SX_KYH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailSxKyh(detailSec);
                        } else if (SX_BLH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailSxBlh(detailSec);
                        } else if (TJ_KYH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailTjKyh(detailSec);
                        } else if (TJ_BLH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailTjBlh(detailSec);
                        } else if (JY_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailTotalInflow(detailSec);
                        } else if (JY_OUTFLOW.equalsIgnoreCase(lnSec.getFillItemCode()) || LOAN_DELIVERY.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailTotalOutflow(detailSec);
                        } else if (HK_DETAIL.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailTotalOutflow(detailSec);
                        } else if (HK_ACCOUNT.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = mapper.queryDetailTotalOutflow(detailSec);
                        }
                        if (detailSec != null) {
                            //明确更新每个月份字段
                            if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField1(detailSec.getDueAmount());
                            } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField2(detailSec.getDueAmount());
                            } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField3(detailSec.getDueAmount());
                            } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField4(detailSec.getDueAmount());
                            } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField5(detailSec.getDueAmount());
                            } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField6(detailSec.getDueAmount());
                            } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField7(detailSec.getDueAmount());
                            } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField8(detailSec.getDueAmount());
                            } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField9(detailSec.getDueAmount());
                            } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField10(detailSec.getDueAmount());
                            } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField11(detailSec.getDueAmount());
                            } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField12(detailSec.getDueAmount());
                            } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField13(detailSec.getDueAmount());
                            }
                        } else {//解决金额明细单行删除 对应行金额无法删除问题
                            if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField1(0D);
                            } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField2(0D);
                            } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField3(0D);
                            } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField4(0D);
                            } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField5(0D);
                            } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField6(0D);
                            } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField7(0D);
                            } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField8(0D);
                            } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField9(0D);
                            } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField10(0D);
                            } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField11(0D);
                            } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField12(0D);
                            } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField13(0D);
                            }
                        }
                        detailList.add(detailSec);
                    }
                }
                //更新明细金额到头
                serviceLn.updateByPrimaryKeySelective(request, lnSec);
            }
        }
        return detailList;
    }

    /*
     * 行表金额更新jc_fund_filling_req_ln  周计划调整
     */
    @Override
    public List<FundFillingReqDetail> updateReqFundLnAmount(IRequest request, FundFillingReqDetail detail) {
        List<FundFillingReqDetail> detailList = new ArrayList<>();
        Long fillingId = detail.getFillingId();
        String fillYear = detail.getFillYear();
        String fillMon = detail.getFillMon();
        String fillWeek = detail.getFillWeek();
        Long unitId = detail.getHostUnitId();
        String fillType = detail.getFillType();
        String fillQuarter = detail.getFillQuarter();
        FundFillingReqLn ln = new FundFillingReqLn();
        ln.setFillingId(fillingId);
        //根据fillType类型不同 赋予其不同的数组 年 季度
        String[] queryList = new String[]{};
        if ("WEEK".equalsIgnoreCase(fillType)) {
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
            queryList = DAY_LIST;
        }
        List<FundFillingReqLn> lnList = reqLnMapper.queryAllForUpdateAmount(ln);
        if (lnList.size() > 0) {
            for (FundFillingReqLn lnSec : lnList) {
                //年计划 遍历 1到12月  12个字段
                for (int i = 0; i < queryList.length; i++) {
                    if (queryList[i] != null) {
                        String fundType = queryList[i];

                        FundFillingReqDetail detailSec = new FundFillingReqDetail();
                        detailSec.setItemCode(lnSec.getFillItemCode());
                        detailSec.setFundType(fundType);
                        detailSec.setFillWeek(fillWeek);
                        detailSec.setFillMon(fillMon);
                        detailSec.setFillYear(fillYear);
                        detailSec.setHostUnitId(unitId);
                        detailSec.setFillingId(fillingId);
                        detailSec.setFillingLnId(lnSec.getFillingLnId());
                        if (PRINCIPAL.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST.equalsIgnoreCase(lnSec.getFillItemCode())
                                || DEPOSIT.equalsIgnoreCase(lnSec.getFillItemCode()) || LEGAL_FEE.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailTotalSec(detailSec);
                        } else if (SX_KYH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailSxKyh(detailSec);
                        } else if (SX_BLH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailSxBlh(detailSec);
                        } else if (TJ_KYH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailTjKyh(detailSec);
                        } else if (TJ_BLH.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailTjBlh(detailSec);
                        } else if (JY_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailTotalInflow(detailSec);
                        } else if (JY_OUTFLOW.equalsIgnoreCase(lnSec.getFillItemCode()) || LOAN_DELIVERY.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailTotalOutflow(detailSec);
                        } else if (HK_DETAIL.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailTotalOutflow(detailSec);
                        } else if (HK_ACCOUNT.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec = reqDetailMapper.queryDetailTotalOutflow(detailSec);
                        }
                        if (detailSec != null) {
                            //明确更新每个月份字段
                            if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField1(detailSec.getDueAmount());
                            } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField2(detailSec.getDueAmount());
                            } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField3(detailSec.getDueAmount());
                            } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField4(detailSec.getDueAmount());
                            } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField5(detailSec.getDueAmount());
                            } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField6(detailSec.getDueAmount());
                            } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField7(detailSec.getDueAmount());
                            } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField8(detailSec.getDueAmount());
                            } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField9(detailSec.getDueAmount());
                            } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField10(detailSec.getDueAmount());
                            } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField11(detailSec.getDueAmount());
                            } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField12(detailSec.getDueAmount());
                            } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField13(detailSec.getDueAmount());
                            }
                        } else {
                            if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField1(0D);
                            } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField2(0D);
                            } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField3(0D);
                            } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField4(0D);
                            } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField5(0D);
                            } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField6(0D);
                            } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField7(0D);
                            } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField8(0D);
                            } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField9(0D);
                            } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField10(0D);
                            } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField11(0D);
                            } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField12(0D);
                            } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                lnSec.setField13(0D);
                            }
                        }
                        detailList.add(detailSec);
                    }
                }
                //更新明细金额到头
                reqLnService.updateByPrimaryKeySelective(request, lnSec);
            }
        }
        return detailList;
    }

    @Override
    public List<JcFundFillingDetail> selectDetailAll(IRequest request, JcFundFillingDetail jcFundFillingDetail, int page, int pageSize) {
//        PageHelper.startPage(page,pageSize);
        return mapper.queryDetail(jcFundFillingDetail);
    }


    /**
     * 汇总 行表金额更新 jc_fund_filling_ln
     */
    @Override
    public List<JcFundFillingDetail> updateSummaryAmount(IRequest request, JcFundFillingDetail detail) {
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        Long fillingId = detail.getFillingId();
        String fillYear = detail.getFillYear();
        String fillMon = detail.getFillMon();
        String fillWeek = detail.getFillWeek();
        Long unitId = detail.getHostUnitId();
        String fillType = detail.getFillType();
        String fillQuarter = detail.getFillQuarter();
        String fillUpdate = detail.getFillUpdate();
        JcFundFillingLn ln = new JcFundFillingLn();
        ln.setFillingId(fillingId);
        //金额明细保存 更新金额名的新加行记录的host_unit_id
        if (detail.getFillingLnId() != null) {
            ln.setFillingLnId(detail.getFillingLnId());
            lnMapper.updateFillingDetailUnitId(ln);
        }
        //根据fillType类型不同 赋予其不同的数组 年 季度
        String[] queryList = new String[]{};
        if ("YEAR".equalsIgnoreCase(fillType)) {
            queryList = YEAR_LIST;
        } else if ("QUARTER".equalsIgnoreCase(fillType)) {
            if ("FIRST".equalsIgnoreCase(fillQuarter)) {
                queryList = FIRST;
            } else if ("SECOND".equalsIgnoreCase(fillQuarter)) {
                queryList = SECOND;
            } else if ("THIRD".equalsIgnoreCase(fillQuarter)) {
                queryList = THIRD;
            } else if ("FOURTH".equalsIgnoreCase(fillQuarter)) {
                queryList = FOURTH;
            }
        } else if ("MON".equalsIgnoreCase(fillType)) {
            queryList = WEEK_LIST;
        } else if ("WEEK".equalsIgnoreCase(fillType)) {
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
            }
            queryList = DAY_LIST;
        }
        List<JcFundFillingLn> lnList = lnMapper.queryAllForUpdateAmount(ln);
        if (lnList.size() > 0) {
            for (JcFundFillingLn lnSec : lnList) {
                //年计划 遍历 1到12月  12个字段
                for (int i = 0; i < queryList.length; i++) {
                    if (queryList[i] != null) {
                        String fundType = "";
                        if ("MON".equalsIgnoreCase(fillType)) {
                            fundType = fillYear + "-" + fillMon + "-" + queryList[i];
                        } else if ("WEEK".equalsIgnoreCase(fillType)) {
                            fundType = queryList[i];
                        } else {
                            fundType = fillYear + "-" + queryList[i];
                        }

                        JcFundFillingDetail detailSec = new JcFundFillingDetail();
                        JcFundFillingDetail detailThree = new JcFundFillingDetail();
                        detailSec.setItemCode(lnSec.getFillItemCode());
                        detailSec.setFundType(fundType);
                        detailSec.setFillWeek(fillWeek);
                        detailSec.setFillMon(fillMon);
                        detailSec.setFillYear(fillYear);
                        detailSec.setHostUnitId(unitId);
                        detailSec.setFillingId(fillingId);
                        detailSec.setFillType(fillType);
                        detailSec.setUnitCode(lnSec.getFillItemCode());
                        detailSec.setFillingLnId(lnSec.getFillingLnId());
                        detailSec.setFillUpdate(fillUpdate);

                        detailThree.setItemCode(lnSec.getFillItemCode());
                        detailThree.setFundType(fundType);
                        detailThree.setFillWeek(fillWeek);
                        detailThree.setFillMon(fillMon);
                        detailThree.setFillYear(fillYear);
                        detailThree.setHostUnitId(unitId);
                        detailThree.setFillingId(fillingId);
                        detailThree.setFillType(fillType);
                        detailThree.setFillUpdate(fillUpdate);
                        detailThree.setUnitCode(lnSec.getFillItemCode());
                        detailThree.setFillingLnId(lnSec.getFillingLnId());
                        //目录求和需要明确是那列求和便于sql取值
                        detailSec.setFiledSeq(YEAR_LIST[i]);
                        detailThree.setFiledSeq(YEAR_LIST[i]);
//                        if (INITIAL_BALANCE.equalsIgnoreCase(lnSec.getFillItemCode())) {
//                            //一、期初资金余额
//                            detailSec = mapper.queryDetailTotalBlance(detailSec);
//                        }
                        /*else if (SX_KYHA.equalsIgnoreCase(lnSec.getFillItemCode()) || SX_BLHA.equalsIgnoreCase(lnSec.getFillItemCode())
                                || TJ_KYHA.equalsIgnoreCase(lnSec.getFillItemCode()) || TJ_BLHA.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //陕西公司可用资金余额 陕西公司保理户/监管户资金余额 天津公司可用资金余额 天津公司保理户/监管户资金余额
                            detailSec = mapper.queryDetailTotalHA(detailSec);
                        } */
//                        else
                        if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //二、资金流入
                            detailSec = mapper.queryDetailTotalJYInflow(detailSec);
                            detailThree = mapper.queryDetailTotalCZ(detailThree);
                        } else if (CZ_INFLOWA.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1. 筹资活动资金流入总计
                            detailSec = mapper.queryDetailTotalCZ(detailThree);
                        }
                        /*else if (ZB_AMOUNT.equalsIgnoreCase(lnSec.getFillItemCode()) || WD_AMOUNT_NEW.equalsIgnoreCase(lnSec.getFillItemCode())
                                || WD_AMOUNT_CHANGE.equalsIgnoreCase(lnSec.getFillItemCode()) || YD_AMOUNT_NEW.equalsIgnoreCase(lnSec.getFillItemCode())
                                || ZQ_AMOUNT.equalsIgnoreCase(lnSec.getFillItemCode()) || PJ_AMOUNT.equalsIgnoreCase(lnSec.getFillItemCode())
                                || OTHER_AMOUNT.equalsIgnoreCase(lnSec.getFillItemCode()) || ZB_AMOUNT1.equalsIgnoreCase(lnSec.getFillItemCode())
                                || WD_AMOUNT_NEW1.equalsIgnoreCase(lnSec.getFillItemCode()) || WD_AMOUNT_CHANGE1.equalsIgnoreCase(lnSec.getFillItemCode())
                                || YD_AMOUNT_NEW1.equalsIgnoreCase(lnSec.getFillItemCode()) || ZQ_AMOUNT1.equalsIgnoreCase(lnSec.getFillItemCode())
                                || PJ_AMOUNT1.equalsIgnoreCase(lnSec.getFillItemCode()) || OTHER_AMOUNT1.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1. 筹资活动资金流入 明细
                            detailSec = mapper.queryDetailTotalHA(detailSec);
                        }*/
                        else if (JY_INFLOWA.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //2. 经营活动资金流入
                            detailSec = mapper.queryDetailTotalJYInflow(detailSec);
                        } else if (HK_DETAIL.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //2.1 回款明细  2.1目录和 等于 2 等于 2.2
                            detailSec = mapper.queryDetailTotalJYInflow(detailSec);
                        } else if (HK_ACCOUNT.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //2.2 回款资金账户
                            detailSec = mapper.queryDetailTotalJYInflow(detailSec);
                        } else if (BUSINESS_DEPT_ZD.equalsIgnoreCase(lnSec.getFillItemCode()) || BUSINESS_DEPT_OD.equalsIgnoreCase(lnSec.getFillItemCode())
                                || BUSINESS_DEPT_TD.equalsIgnoreCase(lnSec.getFillItemCode()) || BUSINESS_DEPT_THD.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //2.1.1战略业务部 2.1.2业务一部 2.1.3 业务二部 2.1.4 业务三部
                            detailSec = mapper.queryDetailTotalSecSummary(detailSec);
                        } else if (PRINCIPAL.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST.equalsIgnoreCase(lnSec.getFillItemCode())
                                || DEPOSIT.equalsIgnoreCase(lnSec.getFillItemCode()) || LEGAL_FEE.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec.setUnitCode(lnSec.getUnitCode());
                            detailSec = mapper.queryDetailTotalSecSummaryDetail(detailSec);
                        } else if (SX_KYHC.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //陕西公司可用户
                            detailSec = mapper.queryDetailSxKyhc(detailSec);
                        } else if (SX_BLHC.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //陕西公司保理户/监管户
                            detailSec = mapper.queryDetailSxBlhc(detailSec);
                        } else if (TJ_KYHC.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //天津公司可用户
                            detailSec = mapper.queryDetailTjKyhc(detailSec);
                        } else if (TJ_BLHC.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //天津公司保理户/监管户
                            detailSec = mapper.queryDetailTjBlhc(detailSec);
                        } else if (CAPITAL_OUTFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //三、资金流出
                            detailSec = mapper.queryDetailTotalCapitalOutflow(detailSec);
                        } else if (CZ_OUTFLOWA.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1. 筹资活动资金流出 ```
                            detailSec = mapper.queryDetailTotalCZOutflow(detailSec);
                        } else if (TZ_OUTFLOWA.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //2. 投资活动资金流出 --
                            detailSec = mapper.queryDetailTotalTZOutflow(detailSec);
                        } else if (JY_OUTFLOWA.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //3. 经营活动资金流出 --
                            detailSec = mapper.queryDetailTotalJYOutflow(detailSec);
                        } else if (BUSINESS_DEPT_ZD1.equalsIgnoreCase(lnSec.getFillItemCode()) || BUSINESS_DEPT_OD1.equalsIgnoreCase(lnSec.getFillItemCode())
                                || BUSINESS_DEPT_TD1.equalsIgnoreCase(lnSec.getFillItemCode()) || BUSINESS_DEPT_THD1.equalsIgnoreCase(lnSec.getFillItemCode()) || LOAN_DELIVERY.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //2.1.1战略业务部 2.1.2业务一部 2.1.3 业务二部 2.1.4 业务三部   这里算投放款  备注：部门取值 五级目录去四级目录code做部门code 四级目录取三级目录做部门code
                            if (LOAN_DELIVERY.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                detailSec.setUnitCode(lnSec.getUnitCode());
                            } else {
                                detailSec.setUnitCode(lnSec.getFillItemCode());
                            }
                            detailSec = mapper.queryDetailTotalLoadDelivery(detailSec);
                        } else if (WD_AMOUNT_A.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1.1 委贷
                            if ("YEAR".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalWdAmountAY(detailSec);
                            } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalWdAmountAQ(detailSec);
                            } else if ("MON".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalWdAmountAM(detailSec);
                            } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalWdAmountAW(detailSec);
                            }
                        } else if (PRINCIPAL_WD.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_WD.equalsIgnoreCase(lnSec.getFillItemCode()) || LEGAL_FEE_WD.equalsIgnoreCase(lnSec.getFillItemCode())
                                || DEPOSIT_WD.equalsIgnoreCase(lnSec.getFillItemCode()) || PRINCIPAL_WD_TJ.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_WD_TJ.equalsIgnoreCase(lnSec.getFillItemCode())
                                || LEGAL_FEE_WD_TJ.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_WD_TJ.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            // 委贷明细
                            if (PRINCIPAL_WD.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_WD.equalsIgnoreCase(lnSec.getFillItemCode())
                                    || LEGAL_FEE_WD.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_WD.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                detailSec.setCompanyId(companyIdSx);
                            } else {
                                detailSec.setCompanyId(companyIdTj);
                            }
                            if ("Y".equalsIgnoreCase(fillUpdate)) {
                                //金额更新则直接取值 不在以初始化形式去范围取值
                                if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField1());
                                } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField2());
                                } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField3());
                                } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField4());
                                } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField5());
                                } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField6());
                                } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField7());
                                } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField8());
                                } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField9());
                                } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField10());
                                } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField11());
                                } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField12());
                                } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField13());
                                }
                            } else {
                                if ("YEAR".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalWdAmountAYDetail(detailSec);
                                } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalWdAmountAQDetail(detailSec);
                                } else if ("MON".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalWdAmountAMDetail(detailSec);
                                } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalWdAmountAWDetail(detailSec);
                                }
                            }

                        } else if (YD_AMOUNT_A.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1.2 银行贷款
                            if ("YEAR".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalYdAmountAY(detailSec);
                            } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalYdAmountAQ(detailSec);
                            } else if ("MON".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalYdAmountAM(detailSec);
                            } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalYdAmountAW(detailSec);
                            }
                        } else if (PRINCIPAL_YD.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_YD.equalsIgnoreCase(lnSec.getFillItemCode()) || LEGAL_FEE_YD.equalsIgnoreCase(lnSec.getFillItemCode())
                                || DEPOSIT_YD.equalsIgnoreCase(lnSec.getFillItemCode()) || PRINCIPAL_YD2.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_YD2.equalsIgnoreCase(lnSec.getFillItemCode())
                                || LEGAL_FEE_YD2.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_YD2.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec.setBankTypeNew("USED_ACCOUNT");
                            if (PRINCIPAL_YD.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_YD.equalsIgnoreCase(lnSec.getFillItemCode())
                                    || LEGAL_FEE_YD.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_YD.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                detailSec.setCompanyId(companyIdSx);
                            } else {
                                detailSec.setCompanyId(companyIdTj);
                            }
                            //银行贷款明细 可用
                            if ("Y".equalsIgnoreCase(fillUpdate)) {
                                //金额更新则直接取值 不在以初始化形式去范围取值
                                if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField1());
                                } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField2());
                                } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField3());
                                } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField4());
                                } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField5());
                                } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField6());
                                } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField7());
                                } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField8());
                                } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField9());
                                } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField10());
                                } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField11());
                                } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField12());
                                } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField13());
                                }
                            } else {
                                if ("YEAR".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAYDetail(detailSec);
                                } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAQDetail(detailSec);
                                } else if ("MON".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAMDetail(detailSec);
                                } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAWDetail(detailSec);
                                }
                            }
                        } else if (PRINCIPAL_YD1.equalsIgnoreCase(lnSec.getFillItemCode()) || PRINCIPAL_YD3.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_YD1.equalsIgnoreCase(lnSec.getFillItemCode())
                                || INTEREST_YD3.equalsIgnoreCase(lnSec.getFillItemCode()) || LEGAL_FEE_YD1.equalsIgnoreCase(lnSec.getFillItemCode()) || LEGAL_FEE_YD3.equalsIgnoreCase(lnSec.getFillItemCode())
                                || DEPOSIT_YD1.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_YD3.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            detailSec.setBankTypeNew("FACTORING_ACCOUNT");
                            if (PRINCIPAL_YD1.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_YD1.equalsIgnoreCase(lnSec.getFillItemCode())
                                    || LEGAL_FEE_YD1.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_YD1.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                detailSec.setCompanyId(companyIdSx);
                            } else {
                                detailSec.setCompanyId(companyIdTj);
                            }
                            //银行贷款明细 监管
                            if ("Y".equalsIgnoreCase(fillUpdate)) {
                                //金额更新则直接取值 不在以初始化形式去范围取值
                                if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField1());
                                } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField2());
                                } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField3());
                                } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField4());
                                } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField5());
                                } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField6());
                                } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField7());
                                } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField8());
                                } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField9());
                                } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField10());
                                } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField11());
                                } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField12());
                                } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField13());
                                }
                            } else {
                                if ("YEAR".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAYDetail(detailSec);
                                } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAQDetail(detailSec);
                                } else if ("MON".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAMDetail(detailSec);
                                } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalYdAmountAWDetail(detailSec);
                                }
                            }
                        } else if (ZQ_AMOUNT_A.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1.3 债券
                            if ("YEAR".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalZqAmountAY(detailSec);
                            } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalZqAmountAQ(detailSec);
                            } else if ("MON".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalZqAmountAM(detailSec);
                            } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalZqAmountAW(detailSec);
                            }
                        } else if (PRINCIPAL_ZQ.equalsIgnoreCase(lnSec.getFillItemCode()) || INTEREST_ZQ.equalsIgnoreCase(lnSec.getFillItemCode()) || FEE_ZQ.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1.3 债券 明细
                            if ("Y".equalsIgnoreCase(fillUpdate)) {
                                //金额更新则直接取值 不在以初始化形式去范围取值
                                if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField1());
                                } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField2());
                                } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField3());
                                } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField4());
                                } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField5());
                                } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField6());
                                } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField7());
                                } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField8());
                                } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField9());
                                } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField10());
                                } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField11());
                                } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField12());
                                } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField13());
                                }
                            } else {
                                if ("YEAR".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalZqAmountAYDetail(detailSec);
                                } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalZqAmountAQDetail(detailSec);
                                } else if ("MON".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalZqAmountAMDetail(detailSec);
                                } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalZqAmountAWDetail(detailSec);
                                }
                            }
                        } else if (PJ_AMOUNT_A.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //1.4 票据
                            if ("YEAR".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalPjAmountAY(detailSec);
                            } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalPjAmountAQ(detailSec);
                            } else if ("MON".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalPjAmountAM(detailSec);
                            } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                detailSec = mapper.queryDetailTotalPjAmountAW(detailSec);
                            }
                        } else if (LEGAL_FEE_PJ.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_PJ.equalsIgnoreCase(lnSec.getFillItemCode()) || PLUS_PJ.equalsIgnoreCase(lnSec.getFillItemCode())
                                || LEGAL_FEE_PJ_TJ.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_PJ_TJ.equalsIgnoreCase(lnSec.getFillItemCode()) || PLUS_PJ_TJ.equalsIgnoreCase(lnSec.getFillItemCode())) {
                            //票据明细
                            if (LEGAL_FEE_PJ.equalsIgnoreCase(lnSec.getFillItemCode()) || DEPOSIT_PJ.equalsIgnoreCase(lnSec.getFillItemCode()) || PLUS_PJ.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                detailSec.setCompanyId(companyIdSx);
                            } else {
                                detailSec.setCompanyId(companyIdTj);
                            }
                            if ("Y".equalsIgnoreCase(fillUpdate)) {
                                //金额更新则直接取值 不在以初始化形式去范围取值
                                if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField1());
                                } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField2());
                                } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField3());
                                } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField4());
                                } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField5());
                                } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField6());
                                } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField7());
                                } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField8());
                                } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField9());
                                } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField10());
                                } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField11());
                                } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField12());
                                } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                    detailSec.setDueAmount(lnSec.getField13());
                                }
                            } else {
                                if ("YEAR".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalPjAmountAYDetail(detailSec);
                                } else if ("QUARTER".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalPjAmountAQDetail(detailSec);
                                } else if ("MON".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalPjAmountAMDetail(detailSec);
                                } else if ("WEEK".equalsIgnoreCase(fillType)) {
                                    detailSec = mapper.queryDetailTotalPjAmountAWDetail(detailSec);
                                }
                            }
                        }
                        if (detailSec != null) {
                            //明确更新每个月份字段
                            if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField1(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField1(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField1(detailSec.getDueAmount());
                                }
                            } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField2(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField2(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField2(detailSec.getDueAmount());
                                }
                            } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField3(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField3(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField3(detailSec.getDueAmount());
                                }
                            } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField4(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField4(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField4(detailSec.getDueAmount());
                                }
                            } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField5(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField5(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField5(detailSec.getDueAmount());
                                }
                            } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField6(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField6(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField6(detailSec.getDueAmount());
                                }
                            } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField7(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField7(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField7(detailSec.getDueAmount());
                                }
                            } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField8(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField8(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField8(detailSec.getDueAmount());
                                }
                            } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField9(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField9(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField9(detailSec.getDueAmount());
                                }
                            } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField10(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField10(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField10(detailSec.getDueAmount());
                                }
                            } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField11(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField11(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField11(detailSec.getDueAmount());
                                }
                            } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField12(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField12(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField12(detailSec.getDueAmount());
                                }
                            } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField13(detailSec.getDueAmount() + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField13(detailSec.getDueAmount());
                                    }
                                } else {
                                    lnSec.setField13(detailSec.getDueAmount());
                                }
                            }
                        } else {
                            if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField1(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField1(0D);
                                    }
                                } else {
                                    lnSec.setField1(0D);
                                }
                            } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField2(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField2(0D);
                                    }
                                } else {
                                    lnSec.setField2(0D);
                                }
                            } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField3(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField3(0D);
                                    }
                                } else {
                                    lnSec.setField3(0D);
                                }
                            } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField4(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField4(0D);
                                    }
                                } else {
                                    lnSec.setField4(0D);
                                }
                            } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField5(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField5(0D);
                                    }
                                } else {
                                    lnSec.setField5(0D);
                                }
                            } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField6(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField6(0D);
                                    }
                                } else {
                                    lnSec.setField6(0D);
                                }
                            } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField7(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField7(0D);
                                    }
                                } else {
                                    lnSec.setField7(0D);
                                }
                            } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField8(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField8(0D);
                                    }
                                } else {
                                    lnSec.setField8(0D);
                                }
                            } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField9(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField9(0D);
                                    }
                                } else {
                                    lnSec.setField9(0D);
                                }
                            } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField10(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField10(0D);
                                    }
                                } else {
                                    lnSec.setField10(0D);
                                }
                            } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField11(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField11(0D);
                                    }
                                } else {
                                    lnSec.setField11(0D);
                                }
                            } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField12(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField12(0D);
                                    }
                                } else {
                                    lnSec.setField12(0D);
                                }
                            } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                                if (CAPITAL_INFLOW.equalsIgnoreCase(lnSec.getFillItemCode())) {
                                    if (detailThree != null) {
                                        lnSec.setField13(0D + detailThree.getDueAmount());
                                    } else {
                                        lnSec.setField13(0D);
                                    }
                                } else {
                                    lnSec.setField13(0D);
                                }
                            }
                        }
                        detailList.add(detailSec);
                    }
                }
                //更新明细金额到头
                serviceLn.updateByPrimaryKeySelective(request, lnSec);
            }
        }
        //期初期末余额处理
        updateStartEndAmount(request, fillingId, queryList, fillType, fillYear, fillMon);

        return detailList;
    }

    //期初期末余额处理
    public void updateStartEndAmount(IRequest request, Long fillingId, String[] queryList, String fillType, String fillYear, String fillMon) {
        JcFundFillingLn endln = new JcFundFillingLn();


        //初始值
        Double endAmount2 = 0D;
        Double endAmount3 = 0D;
        Double endAmount4 = 0D;
        Double endAmount5 = 0D;

        List<JcFundFillingLn> LnListBatchUpdate = new ArrayList<>(105);
        for (int i = 0; i < queryList.length; i++) {
            //期末
            Double endAmountTotal = 0D;
            //期初
            Double startAmountTotal = 0D;
            if (queryList[i] != null) {
                String ItemCodeList = SX_KYHA + "'" + "," + "'" + SX_BLHA + "'" + "," + "'" + TJ_KYHA + "'" + "," + "'"
                        + TJ_BLHA + "'" + "," + "'" + SX_KYHB + "'" + "," + "'" + SX_BLHB + "'" + "," + "'" + TJ_KYHB + "'" + "," + "'"
                        + TJ_BLHB + "'" + "," + "'" + INITIAL_BALANCE + "'" + "," + "'" + END_BALANCE;
//                endln.setFillItemCode(ItemCodeList);
                String fundType = "";
                if ("MON".equalsIgnoreCase(fillType)) {
                    fundType = fillYear + "-" + fillMon + "-" + queryList[i];
                } else if ("WEEK".equalsIgnoreCase(fillType)) {
                    fundType = queryList[i];
                } else {
                    fundType = fillYear + "-" + queryList[i];
                }
                //查询目录明细指定目录（期初期末  一个期初接着一个期末的顺序 查询的sql按照序号排列 序号顺序在code定义表中设定好 期初期末顺序）
                endln.setFillingId(fillingId);
                List<JcFundFillingLn> fillingLnList = lnMapper.queryByItemCode(endln);
                //目录循环 是一个期初，下一个是其对应期末（这种遍历顺序是便于下面用期初算期末）
                Double endAmount1 = 0D;
                if (fillingLnList.size() > 0) {
                    for (JcFundFillingLn fillingLn : fillingLnList) {
                        JcFundFillingLn lnUpdate = new JcFundFillingLn();
                        lnUpdate.setFillingLnId(fillingLn.getFillingLnId());
                        lnUpdate.setFillingId(fillingId);
                        //查询结果按照一定顺序排列了 下面遍历不需要再去看计算的先后顺序
                        if (SX_KYHA.equalsIgnoreCase(fillingLn.getFillItemCode()) || SX_BLHA.equalsIgnoreCase(fillingLn.getFillItemCode()) ||
                                TJ_KYHA.equalsIgnoreCase(fillingLn.getFillItemCode()) || TJ_BLHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                            if (i == 0) {
                                //获取期初 filed1（客户录入或者为0）初始金额
                                if (SX_KYHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = nvl(fillingLn.getField1(), 0D);
                                } else if (SX_BLHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = nvl(fillingLn.getField1(), 0D);
                                } else if (TJ_KYHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = nvl(fillingLn.getField1(), 0D);
                                } else if (TJ_BLHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = nvl(fillingLn.getField1(), 0D);
                                }
                            } else {
                                //那上次的期末做本次的期初
                                if (SX_KYHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = endAmount2;
                                } else if (SX_BLHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = endAmount3;
                                } else if (TJ_KYHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = endAmount4;
                                } else if (TJ_BLHA.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                    endAmount1 = endAmount5;
                                }
                            }
                            //期初加总
                            startAmountTotal = startAmountTotal + endAmount1;
                        }

                        endln.setFiledNum(YEAR_LIST[i]);

                        endln.setEndAmount1(endAmount1);
                        endln.setFundType(fundType);
                        endln.setFillingId(fillingId);
                        //通过期初算期末
                        if (SX_KYHB.equalsIgnoreCase(fillingLn.getFillItemCode()) || SX_BLHB.equalsIgnoreCase(fillingLn.getFillItemCode()) ||
                                TJ_KYHB.equalsIgnoreCase(fillingLn.getFillItemCode()) || TJ_BLHB.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                            if (SX_KYHB.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                endln = lnMapper.queryEndBalanceAmountSxK(endln);
                                endAmount1 = nvl(endln.getEndAmount1(), 0D);
                                endAmount2 = endAmount1;
                            } else if (SX_BLHB.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                endln = lnMapper.queryEndBalanceAmountSxB(endln);
                                endAmount1 = nvl(endln.getEndAmount1(), 0D);
                                endAmount3 = endAmount1;
                            } else if (TJ_KYHB.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                endln = lnMapper.queryEndBalanceAmountTjK(endln);
                                endAmount1 = nvl(endln.getEndAmount1(), 0D);
                                endAmount4 = endAmount1;
                            } else if (TJ_BLHB.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                endln = lnMapper.queryEndBalanceAmountTjB(endln);
                                endAmount1 = nvl(endln.getEndAmount1(), 0D);
                                endAmount5 = endAmount1;
                            }
                            //期末加总
                            endAmountTotal = endAmountTotal + endAmount1;
                        }


                        if ("01".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField1(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField1(endAmountTotal);
                            } else {
                                lnUpdate.setField1(endAmount1);
                            }

                        } else if ("02".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField2(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField2(endAmountTotal);
                            } else {
                                lnUpdate.setField2(endAmount1);
                            }
                        } else if ("03".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField3(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField3(endAmountTotal);
                            } else {
                                lnUpdate.setField3(endAmount1);
                            }
                        } else if ("04".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField4(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField4(endAmountTotal);
                            } else {
                                lnUpdate.setField4(endAmount1);
                            }
                        } else if ("05".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField5(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField5(endAmountTotal);
                            } else {
                                lnUpdate.setField5(endAmount1);
                            }
                        } else if ("06".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField6(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField6(endAmountTotal);
                            } else {
                                lnUpdate.setField6(endAmount1);
                            }
                        } else if ("07".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField7(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField7(endAmountTotal);
                            } else {
                                lnUpdate.setField7(endAmount1);
                            }
                        } else if ("08".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField8(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField8(endAmountTotal);
                            } else {
                                lnUpdate.setField8(endAmount1);
                            }
                        } else if ("09".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField9(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField9(endAmountTotal);
                            } else {
                                lnUpdate.setField9(endAmount1);
                            }
                        } else if ("10".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField10(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField10(endAmountTotal);
                            } else {
                                lnUpdate.setField10(endAmount1);
                            }
                        } else if ("11".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField11(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField11(endAmountTotal);
                            } else {
                                lnUpdate.setField11(endAmount1);
                            }
                        } else if ("12".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField12(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField12(endAmountTotal);
                            } else {
                                lnUpdate.setField12(endAmount1);
                            }
                        } else if ("13".equalsIgnoreCase(YEAR_LIST[i])) {
                            if (INITIAL_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField13(startAmountTotal);
                            } else if (END_BALANCE.equalsIgnoreCase(fillingLn.getFillItemCode())) {
                                lnUpdate.setField13(endAmountTotal);
                            } else {
                                lnUpdate.setField13(endAmount1);
                            }
                        }
                        serviceLn.updateByPrimaryKeySelective(request, lnUpdate);
                    }
                }

            }
        }
//        serviceLn.batchUpdate(request, LnListBatchUpdate);
    }
}