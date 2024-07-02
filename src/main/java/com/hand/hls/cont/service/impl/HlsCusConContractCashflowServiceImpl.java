package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.activiti.service.HlsCusActMeetingRiskListService;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsCusContractAttachment;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.HlsCusContractAttachmentService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusPaymentDeduct;
import com.hand.hls.csh.service.HlsCusPaymentDeductService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HlsCusDocumentList;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.fnd.service.HlsCusDocumentListService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractCashflowServiceImpl extends BaseServiceImpl<HlsCusConContractCashflow> implements HlsCusConContractCashflowService {
    /**
     * 还租频率
     * "MONTH" 月付
     * "QUARTER" 季付
     * "HALF_A_YEAR" 半年付
     * "YEAR" 年付
     * "DOUBLE_MONTH" 双月付
     */
    private static final String MONTH = "MONTH";
    private static final String QUARTER = "QUARTER";
    private static final String HALF_A_YEAR = "HALF_A_YEAR";
    private static final String YEAR = "YEAR";
    private static final String DOUBLE_MONTH = "DOUBLE_MONTH";


    /**
     * cf_item
     * 1 租金
     * 8 留购金
     * 9 延迟履行金
     * 11 提前结清合同终止金
     * 51 保证金
     */
    private static final Long CF_ITEM_RENT = 1L;
    private static final Long CF_ITEM_LEAVE = 8L;
    private static final Long CF_ITEM_INTEREST = 9L;
    private static final Long CF_ITEM_CONTERMINATIONMONEY = 11L;
    private static final Long CF_ITEM_DEPOSIT = 51L;
    private static final Long CF_ITEM_DOWNPAYMENT = 2L;
    /**
     * 现金流方向
     * "INFLOW" 流入
     * "OUTFLOW" 流出
     */
    private static final String INFLOW = "INFLOW";
    private static final String OUTFLOW = "OUTFLOW";

    /**
     * 现金流状态
     * "BLOCK" 冻结
     * "RELEASE" 下达
     * "CANCEL" 取消
     */
    private static final String BLOCK = "BLOCK";
    private static final String RELEASE = "RELEASE";
    private static final String CANCEL = "CANCEL";

    /**
     * 租赁方式
     * "LEASE" 直租
     * "LEASEBACK" 回租
     */
    private static final String LEASE = "LEASE";
    private static final String LEASEBACK = "LEASEBACK";

    /**
     * 报价方式
     * "LEVEL_PMT_TAX_INC_CT" 等额本息
     * "LEVEL_RATE_TAX_INC_CT" 等额本金
     */
    private static final String LEVEL_PMT_TAX_INC_CT = "LEVEL_PMT_TAX_INC_CT";
    private static final String LEVEL_RATE_TAX_INC_CT = "LEVEL_RATE_TAX_INC_CT";

    /**
     * 核销标志
     * "NOT" 未核销
     * "PARTIAL" 部分核销
     * "FULL" 全部核销
     */
    private static final String NOT = "NOT";
    private static final String PARTIAL = "PARTIAL";
    private static final String FULL = "FULL";

    /**
     * 租赁提前还款方式
     * "ONE_TIME" 	结清日一次性收取剩余未收租金
     * "TERMINATE_CALC" 结清日按照实际用款天数计算利息
     * "TERMINATE_DAY" 结清日按照实际用款天数计算利息加收合同终止金
     */
    private static final String ONE_TIME = "ONE_TIME";
    private static final String TERMINATE_CALC = "TERMINATE_CALC";
    private static final String TERMINATE_DAY = "TERMINATE_DAY";


    /**
     * 租金 1
     * 本金 100
     * 利息 101
     */
    private static final Long RENT = 1L;
    private static final Long INTEREST = 101L;
    private static final Long PRIN = 101L;

    private static final int SCALE = 2;

    @Autowired
    private HlsCusFctQuotationCashflowService hlsCusFctQuotationCashflowService;

    @Autowired
    private HlsCusContractAttachmentService hlsCusContractAttachmentService;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;

    @Autowired
    private HlsCusDocumentListService documentListService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusActMeetingRiskListService hlsCusActMeetingRiskListService;

    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;


    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    @Override
    public String confirmFullWriteOff(IRequest requestContext, Long contractId, String writeOffType) {
        //PAYMENT_DEBT  租赁
        if ("PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setContractId(contractId);
            conContractCashflow.setTimes(0L);
            conContractCashflow.setCfDirection("INFLOW");
            List<HlsCusConContractCashflow> oldList = self().select(requestContext, conContractCashflow, 1, 0);
            for (HlsCusConContractCashflow dt : oldList) {
                if (!"FULL".equalsIgnoreCase(dt.getWriteOffFlag())) {
                    return "false";
                }
            }
        }
        //CT_FCT_PAYMENT_DEBT 保理
        else if ("CT_FCT_PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
            HlsCusFctQuotationCashflow fctQuotationCashflow = new HlsCusFctQuotationCashflow();
            fctQuotationCashflow.setContractId(contractId);
            fctQuotationCashflow.setTimes(0L);
            fctQuotationCashflow.setCfDirection("INFLOW");
            List<HlsCusFctQuotationCashflow> oldList = hlsCusFctQuotationCashflowService.select(requestContext, fctQuotationCashflow, 1, 0);
            for (HlsCusFctQuotationCashflow dt : oldList) {
                if (!"FULL".equalsIgnoreCase(dt.getWriteOffFlag())) {
                    return "false";
                }
            }
        }
        return "true";
    }



    @Override
    public List<HlsCusCshPaymentReqHd> contractGenerate(IRequest request, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
//        HlsCusConContract hlsCusConContract = new HlsCusConContract();
//        hlsCusConContract.setProjectId(hlsCusPrjProject.getProjectId());
//        hlsCusConContract.setDataClass("NORMAL");
//        List<HlsCusConContract> hlsCusConContractList = hlsCusConContractService.select(request, hlsCusConContract, 1, 99999999);
//        hlsCusConContract = hlsCusConContractList.get(0);
        //hlsCusActMeetingRiskListService.insertConPaymentPt(request, hlsCusConContract);
        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setCompanyId(hlsCusPrjProject.getCompanyId());
        cshPaymentReqHd.setAmount(0D);
        cshPaymentReqHd.setCurrency(hlsCusPrjProject.getCurrency());
        //cshPaymentReqHd = cshPaymentReqHdService.insertSelective(request, cshPaymentReqHd);
        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<>();
        hlsCusCshPaymentReqHds.add(cshPaymentReqHd);
        return hlsCusCshPaymentReqHds;
    }

    public String getCodeValue(IRequest requestContext) {
        Map<String, String> params = new HashMap<String, String>();
        return codingRuleValuesService.getCodeRuleValue(requestContext, "CSH_PAYMENT_REQ", "PAYMENT_REQ", "PAYMENT_REQ", params);
    }

    private void createCshDeduct(Long contractId, Long paymentReqId, IRequest iRequest) {
        //坐扣表初始化
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(contractId);
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowMapper.select(cashflow);
        for (HlsCusConContractCashflow cf : cashflowList) {
            if (CF_ITEM_DEPOSIT.equals(cf.getCfItem()) || CF_ITEM_DOWNPAYMENT.equals(cf.getCfItem())) {
                HlsCusPaymentDeduct hlsCusPaymentDeduct = new HlsCusPaymentDeduct();
                hlsCusPaymentDeduct.setPaymentReqId(paymentReqId);
                hlsCusPaymentDeduct.setCashflowId(cf.getCashflowId());
                hlsCusPaymentDeduct.setDeductAmount(0D);
                hlsCusPaymentDeduct.setDeductPrincipal(0D);
                hlsCusPaymentDeduct.setDeductInterest(0D);
                hlsCusPaymentDeductService.insertSelective(iRequest, hlsCusPaymentDeduct);
            }

        }
    }

    @Override
    public List<HlsCusConContractCashflow> queryFactoringInvoice(IRequest request, HlsCusConContractCashflow cashflow, int page, int pageSize) {
        if (cashflow.getCfItems() == null) {
            cashflow.setCfItems(new Long[]{1L, 9L});
        }
        if (StringUtils.isBlank(cashflow.getContractNameOrNumber())) {
            cashflow.setContractNameOrNumber(null);
        }
        return hlsCusConContractCashflowMapper.queryFactoringInvoice(cashflow);
    }

    @Override
    public List<HlsCusConContractCashflow> selectConLoanRequest(IRequest requestContext, HlsCusConContractCashflow dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusConContractCashflow> hlsCusConContractCashflows = hlsCusConContractCashflowMapper.selectConLoanRequest(dto);
        return hlsCusConContractCashflows;
    }

    @Autowired
    private HlsCusPaymentDeductService hlsCusPaymentDeductService;


    @Override
    public List<HlsCusConContractCashflow> queryContractCashflowLov(IRequest requestCtx, HlsCusConContractCashflow hlsCusConContractCashflow, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsCusConContractCashflowMapper.queryContractCashflowLov(hlsCusConContractCashflow);
    }

    @Override
    public List<HlsCusConContractCashflow> queryCshFineInfoList(IRequest iRequest, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusConContractCashflow> hlsCusConContractCashflowList  = hlsCusConContractCashflowMapper.queryCshFineInfoList(hlsCusConContractCashflow);
        for (HlsCusConContractCashflow dt : hlsCusConContractCashflowList) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setContractId(dt.getContractId());
            conContractCashflow.setCfItem(9L);
            List<HlsCusConContractCashflow> list = self().select(iRequest, conContractCashflow, 1, 1000);
            for (HlsCusConContractCashflow xt : list) {
                if (("APPROVING").equalsIgnoreCase(xt.getFineStatus())) {
                    dt.setFineStatus("APPROVING");
                    dt.setFineStatusDesc("审批中");
                    break;
                }
            }
        }
        return hlsCusConContractCashflowList;
    }


    @Override
    public void createCshFineAttachment(IRequest iRequest, Long contractId) {
        HlsCusContractAttachment conContractAttachment = new HlsCusContractAttachment();
        conContractAttachment.setContractAttachmentCategory(HlsCusConstant.CON_ATTACHMENT.FINE);
        conContractAttachment.setSourceType(HlsCusConstant.CON_ATTACHMENT.FINE);
        conContractAttachment.setContractId(contractId);
        conContractAttachment.setSourceId(contractId);
        List<HlsCusContractAttachment> contractAttachments = hlsCusContractAttachmentService.select(iRequest, conContractAttachment, 1, 1);
        if (contractAttachments.size() == 0) {
            conContractAttachment.setDocumentName("罚息减免申请书");
            hlsCusContractAttachmentService.insertSelective(iRequest, conContractAttachment);
        }
    }


    @Override
    public List<HlsCusConContractCashflow> fineCshSubmit(IRequest requestContext, List<HlsCusConContractCashflow> hlsCusConContractCashflowList) {
        //保存罚息现金流的信息
        for (HlsCusConContractCashflow dt : hlsCusConContractCashflowList) {
            if (dt.getCashflowId() != null && dt.getCashflowId() != 0) {
                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                hlsCusConContractCashflow.setCashflowId(dt.getCashflowId());
                hlsCusConContractCashflow = self().selectByPrimaryKey(requestContext, hlsCusConContractCashflow);
                hlsCusConContractCashflow.setFineReduceAmount(dt.getFineReduceAmount());
                hlsCusConContractCashflow.setFineReductionType(dt.getFineReductionType());
                hlsCusConContractCashflow.setFineReduceReq(dt.getFineReduceReq());
                hlsCusConContractCashflow.setDescription(dt.getDescription());
                //申请罚息减免的现金流fineReduceReq=Y,状态修改为申请中，原状态为新建
                if (dt.getFineReduceReq() != null && dt.getFineReduceReq().equalsIgnoreCase("Y")) {
                    hlsCusConContractCashflow.setFineStatus("APPROVING");
                }
                dt = self().updateByPrimaryKey(requestContext, hlsCusConContractCashflow);
            }
        }
        //更新报价表中租金支付表的罚息状态

        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(hlsCusConContractCashflowList.get(0).getContractId());
        hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(requestContext, hlsCusConContract);

        //修改实际合同的状态为暂挂，防止被合同调息和变更选中
        hlsCusConContract.setContractStatus("PENDING");
        hlsCusConContract = hlsCusConContractService.updateByPrimaryKeySelective(requestContext, hlsCusConContract);

        List<HlsCusConContract> hlsCusConContractList = new ArrayList<>();
        hlsCusConContractList.add(hlsCusConContract);
        databaseLockProvider.lock(hlsCusConContract);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(requestContext.getUserId());
        String employeeCode = employee.getEmployeeCode();
        requestContext.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CON_CONTRACT_CSH_WFL");
        activitiStartService.start(requestContext, hlsCusConContractList, params);

        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(requestContext.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(requestContext.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + hlsCusConContract.getContractNumber() + "合同的罚息减免审核";
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "租赁罚息减免审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(requestContext, hlsCusConContract.getContractId(), "CON_CONTRACT", "CON_CONTRACT", "BAC", "CON_CONTRACT_CSH_FINE_WFL", "P2D", paramsEvent);

        return hlsCusConContractCashflowList;
    }

    @Override
    public List<HlsCusConContractCashflow> queryContractRepCashflowByContractId(HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pagesize) {
        return hlsCusConContractCashflowMapper.queryContractRepCashflowByContractId(hlsCusConContractCashflow);

    }

    @Override
    public List<HlsCusConContractCashflow> conQueryCashFlow(HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pagesize) {
        return hlsCusConContractCashflowMapper.conQueryCashFlow(hlsCusConContractCashflow);
    }

    private final static String SHEET_NAME_CASH = "还款计划";
    private final static String FILE_NAME_CASH = "还款计划";
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private static final List<String> colCashFlowNameList = Lists.newArrayList(
            "期数", "现金流项目", "现金流方向", "计算日期", "支付日期", "金额(元)", "本金(元)", "利息(元)", "剩余本金(元)");
    private static final List<String> colCashGetMethods = Lists.newArrayList(
            "times", "cfItemDesc", "cfDirection", "calcDateExport", "dueDateExport", "dueAmount", "principal", "interest", "outstandingPrincipal", "writeOffFlagDesc");


    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private SysEventService sysEventService;

    @Override
    public List<HlsCusConContractCashflow> queryForRealIncomeReport(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize){
        PageHelper.startPage(page, pageSize);
        return hlsCusConContractCashflowMapper.queryForRealIncomeReport(hlsCusConContractCashflow);
    }

    /**
     * 二期功能：进件投放审查通过后，复制现金流
     *
     * @param request
     * @param contractId
     * @param quotationId
     * @param newQuotationId
     * @return
     */
    @Override
    public List<HlsCusConContractCashflow> saveCashflowFromPrjCashflow(IRequest request, Long contractId, Long quotationId, Long newQuotationId) {
        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = conContractMapper.selectByPrimaryKey(c);
        List<HlsCusConContractCashflow> conContractCashflowList = new ArrayList<HlsCusConContractCashflow>();
        HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflowParameter.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
        if (prjQuotationCashflowList.size() > 0) {
            c.setFirstPayDate(( prjQuotationCashflowList.get(0)).getDueDate());
            c.setLeaseEndDate(( prjQuotationCashflowList.get(0)).getDueDate());
        }

        for (int i = 0; i < prjQuotationCashflowList.size(); ++i) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            if (( prjQuotationCashflowList.get(i)).getDueDate() != null) {
                if ( prjQuotationCashflowList.get(i).getCfItem() == 1L &&  prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getFirstPayDate().getTime() < 0L) {
                    c.setFirstPayDate(( prjQuotationCashflowList.get(i)).getDueDate());
                }

                if (( prjQuotationCashflowList.get(i)).getCfItem() == 1L && ( prjQuotationCashflowList.get(i)).getDueDate().getTime() - c.getLeaseEndDate().getTime() > 0L) {
                    c.setLeaseEndDate(( prjQuotationCashflowList.get(i)).getDueDate());
                }
            }

            BeanRefUtils.beanToBean(prjQuotationCashflowList.get(i), conContractCashflow, hlsBeanRefUtilService);
            conContractCashflow.setTimes(( prjQuotationCashflowList.get(i)).getTimes().longValue());
            conContractCashflow.setContractId(contractId);
            conContractCashflow.setWriteOffFlag("NOT");
            conContractCashflow.setBillingStatus("NOT");
            conContractCashflow.setOverdueStatus("N");
            conContractCashflow.setPenaltyProcessStatus("N");
            conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
            conContractCashflow.setGeneratedSource("PRJ_QUOTATION");

            conContractCashflow.setDueAmount(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getDueAmount(),0.0),SCALE));
            conContractCashflow.setVatDueAmount(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getVatDueAmount(),0.0),SCALE));
            conContractCashflow.setNetDueAmount(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getNetDueAmount(),0.0),SCALE));
            conContractCashflow.setPrincipal(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getPrincipal(),0.0),SCALE));
            conContractCashflow.setNetPrincipal(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getNetPrincipal(),0.0),SCALE));
            conContractCashflow.setVatPrincipal(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getVatPrincipal(),0.0),SCALE));
            conContractCashflow.setInterest(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getInterest(),0.0),SCALE));
            conContractCashflow.setNetInterest(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getNetInterest(),0.0),SCALE));
            conContractCashflow.setVatInterest(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getVatInterest(),0.0),SCALE));
            conContractCashflow.setOutstandingPrincipal(MathUtil.round(nvl(prjQuotationCashflowList.get(i).getOutstandingPrincipal(),0.0),SCALE));


            conContractCashflow.setGeneratedSourceDocId(newQuotationId);
            conContractCashflow.setGeneratedSourceDocLineId(( prjQuotationCashflowList.get(i)).getQuotationCashflowId());
            self().insertSelective(request, conContractCashflow);
            conContractCashflowList.add(conContractCashflow);
        }

        conContractMapper.updateByPrimaryKeySelective(c);
        return conContractCashflowList;
    }

    /**
     * 二期功能：付款申请页面查询
     *
     * @param request                   请求
     * @param hlsCusConContractCashflow 入参
     * @param page                      分页参数
     * @param pageSize                  分页参数
     * @return 列表
     */
    @Override
    public List<HlsCusConContractCashflow> queryCshPaymentCreateInfo(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusConContractCashflow> hlsCusConContractCashflows = this.hlsCusConContractCashflowMapper.queryCshPaymentCreateInfo2(hlsCusConContractCashflow);
        hlsCusConContractCashflows.sort(Comparator.comparing(HlsCusConContractCashflow::getDueDate).reversed());
        return hlsCusConContractCashflows;
    }

    /**
     * 二期功能：查询现金流
     *
     * @param contractId
     * @return
     */
    @Override
    public List<HlsCusConContractCashflow> queryCashflowList(Long contractId) {
        return hlsCusConContractCashflowMapper.queryCashflowList(contractId);
    }

    @Override
    public void contTariffCashflowImport(IRequest iRequest,Long hdId) throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 0L);
        List<HlsCusConContractCashflow> conContractCashflowList = new ArrayList<>();

        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();

            Long contractId;
            Long times = 0L;
            Long cfItem = 0L;
            Date dueDate = new Date();
            Double dueAmount = 0.0;
            String currency = "CNY";
            String expenseItem = "";

            //支付表编号
            if (fndInterfaceLine.getAttributes_1() != null) {
                HlsCusConContract contractpara = new HlsCusConContract();
                contractpara.setContractNumber(fndInterfaceLine.getAttributes_1());
                List<HlsCusConContract> contracts = hlsCusConContractService.select(iRequest,contractpara,1,99999);
                if(contracts.size()>1){
                    throw new HlsCusException("此编号："+fndInterfaceLine.getAttributes_1()+"此编号存在多条记录！");
                }else if(contracts.size() == 0){
                    throw new HlsCusException("此编号："+fndInterfaceLine.getAttributes_1()+"不存在，请核对！");
                }else{
                    contractId = contracts.get(0).getContractId();
                }
            } else {
                throw new HlsCusException("支付表编号不能为空！");
            }

            //期数
            if (fndInterfaceLine.getAttributes_2() != null) {
                times = Long.parseLong(fndInterfaceLine.getAttributes_2());
            } else {
                throw new HlsCusException("期数不能为空！");
            }

            //应收/付日期
            if (fndInterfaceLine.getAttributes_3() != null) {
                dueDate = df.parse(fndInterfaceLine.getAttributes_3());
            } else {
                throw new HlsCusException("应收/付日期不能为空！");
            }
            //关税金额
            if (fndInterfaceLine.getAttributes_4() != null) {
                dueAmount = Double.parseDouble(fndInterfaceLine.getAttributes_4());
            } else {
                throw new HlsCusException("关税金额(元)不能为空！");
            }

            //所属费用项
            if (fndInterfaceLine.getAttributes_5() != null) {
                expenseItem = fndInterfaceLine.getAttributes_5();
            } else {
                throw new HlsCusException("所属费用项不能为空！");
            }
            cashflow.setContractId(contractId);
            cashflow.setTimes(times);
            cashflow.setDueAmount(dueAmount);
            cashflow.setDueDate(dueDate);
            cashflow.setExpenseItem(expenseItem);
            cashflow.setCfType(13L);
            cashflow.setCfStatus("RELEASE");
            cashflow.setWriteOffFlag("NOT");
            cashflow.setCurrency(currency);
            cashflow.setGeneratedSource("TARIFF_IMPORT");

            //插入两条现金流
            cashflow.setCfItem(131L);
            cashflow.setCfDirection("INFLOW");
            checkCashflow(cashflow);
            this.insertSelective(iRequest, cashflow);
            cashflow.setCashflowId(null);
            cashflow.setCfItem(132L);
            cashflow.setCfDirection("OUTFLOW");
            checkCashflow(cashflow);
            this.insertSelective(iRequest, cashflow);
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

    public void checkCashflow (HlsCusConContractCashflow newcashflow) throws HlsCusException{
        HlsCusConContractCashflow oldcashflow = new HlsCusConContractCashflow();
        //查询原合同现金流
        oldcashflow.setContractId(newcashflow.getContractId());
        oldcashflow.setCfItem(newcashflow.getCfItem());
        oldcashflow.setTimes(newcashflow.getTimes());

        List<HlsCusConContractCashflow> cashflows = hlsCusConContractCashflowMapper.select(oldcashflow);
        if(cashflows.size()>0){
            throw new HlsCusException("不允许存在期次相同两条的现金流，请核对！");
        }
    }

    @Override
    public List<Map<String, Object>> queryEtAmount(IRequest iRequest, Map map) {
        map.put("dueDate", new Date());
        List<HlsCusConContractCashflow> list = this.hlsCusConContractCashflowMapper.queryEtAmount(map);
        List<Map<String, Object>> ets = new ArrayList();
        Map<String, Object> et = new HashMap();
        list.forEach((item) -> {
            String var2 = item.getCfItem().toString();
            byte var3 = -1;
            switch (var2) {
                case "1":
                    et.put("ccrOverdueRental", item.getAmount());
                    break;
                case "5":
                    et.put("guaranteeAmount", item.getAmount());
                    break;
                case "8":
                    et.put("retention", item.getAmount());
                    break;
                case "9":
                    et.put("ccrPenalty", item.getAmount());
                    break;
                case "100":
                    et.put("ccrOverduePrin", item.getAmount());
            }
        });
        List<HlsCusConContractCashflow> cashflows = hlsCusConContractCashflowMapper.queryEtRestCapital(map);
        if (CollectionUtils.isNotEmpty(cashflows)) {
            et.put("ccrOutstandingPrinTaxIncld", (cashflows.get(0)).getAmount());
        }

        et.put("quotationId", map.get("quotationId"));
        ets.add(et);
        return ets;
    }
}
