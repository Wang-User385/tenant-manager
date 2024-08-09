package com.hand.hls.gld.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import com.hand.hap.account.exception.UserException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.activiti.service.HlsCusActMeetingRiskListService;
import com.hand.hls.app.dto.HlsCashflowAyncDto;
import com.hand.hls.app.service.HlsCashflowAyncService;
import com.hand.hls.ast.dto.VirtualConContractLov;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.*;
import com.hand.hls.cont.service.*;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.credit.service.TongDunService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.csh.mapper.ProjectCreditConditionMapper;
import com.hand.hls.csh.service.HlsCusPaymentDeductService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.csh.service.IHlsCusCshPaymentReqLnService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.fnd.dto.FndSysCodes;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.fnd.service.FndSysCodesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeTrxDtl;
import com.hand.hls.gld.mapper.GldCusConContractMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.HlsCusContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.gld.utils.CalculateUtil;
import com.hand.hls.gld.utils.IrrUtil;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.mapper.HlsCusFundingPlanMapper;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import com.hand.hls.mort.dto.HlsMortgage;
import com.hand.hls.partner.service.IAlipayService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.*;
import com.hand.hls.vat.dto.HlsInvoiceProfileDtl;
import com.hand.hls.vat.service.HlsInvoiceProfileDtlService;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

/**
 * @author wujun
 * @version 1.0
 * @date 2020/2/7 19:30
 * @description copy from gd
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractServiceImpl extends BaseServiceImpl<HlsCusConContract> implements HlsCusConContractService {

    public static final String NEW = "NEW";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVED_RETURN = "APPROVED_RETURN";
    @Autowired
    private GldCusConContractMapper gldCusConContractMapper;
    @Autowired
    private FinanceBaseUtils financeBaseUtils;

    @Override
    public HlsCusConContract queryConContractByKey(IRequest iRequest, HlsCusConContract hlsCusConContract) {
        return gldCusConContractMapper.queryConContractByKey(hlsCusConContract);
    }


    /**
     * 还租频率
     * "MONTH" 月付
     * "QUARTER" 季付
     * "HALF_A_YEAR" 半年付
     * "YEAR" 年付
     * "DOUBLE_MONTH"双月付
     */
    private static final String MONTH = "MONTH";
    private static final String QUARTER = "QUARTER";
    private static final String HALF_A_YEAR = "HALF_A_YEAR";
    private static final String YEAR = "YEAR";
    private static final String DOUBLE_MONTH = "DOUBLE_MONTH";
    /**
     * 合同状态
     * "CHANGE_REQ" 变更
     * "HISTORY" 历史
     * "PENDING" 暂挂
     * "CANCEL" 取消
     */
    private static final String CHANGEREQ = "CHANGE_REQ";

    private static final String HISTORY = "HISTORY";

    private static final String PENDING = "PENDING";

    private static final String CANCEL = "CANCEL";
    private static final Double OPERATING_LEASE_RATE = 1.13D;
    /**
     * 合同变更类型
     * "GUARANTOR" 追加担保
     * "CONTRACTTEXT" 合同文本变更
     * "RENTALPLAN" 租金计划变更
     * "BACKMONEYACCOUNT" 回款账户变更
     * "PREREPAYMENT" 提前还款
     */
    private static final String GUARANTOR = "GUARANTOR";

    private static final String CONTRACTTEXT = "CONTRACTTEXT";

    private static final String RENTALPLAN = "RENTALPLAN";

    private static final String BACKMONEYACCOUNT = "BACKMONEYACCOUNT";

    private static final String PREREPAYMENT = "PREREPAYMENT";
    /**
     * 现金流项目
     * <p>
     * 3L 手续费现金流项目
     * 4L 咨询服务费现金流项目
     * 1L 租金现金流项目
     * 0L 设备款现金流项目
     * 10L 租前息现金流项目
     * 11 提前结清现金流
     */
    private static final Long CF_ITEM_LEASE_CHARGE = 3L;
    private static final Long CF_ITEM_ADVSERVICEFEE = 4L;
    private static final Long CF_ITEM_DUE_AMOUNT = 1L;
    private static final Long CF_ITEM_ET = 11L;
    private static final Long CF_ITEM_LEASE_ITEM = 0L;
    private static final Long CF_ITEM_RENT = 10L;
    private static final Long CF_TYPE_RENT = 10L;
    private static final String SOURCE_DOCUMENT_CATEGORY = "CON_CONTRACT";
    //常规投放
    private static final String ROUTINE = "ROUTINE";

    @Autowired
    private HlsCusContractAttachmentMapper hlsCusContractAttachmentMapper;

    @Autowired
    private TongDunService tongDunService;

    @Autowired
    private HlsWsRequestsMapper hlsWsRequestsMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusChangeReqInfoMapper hlsCusChangeReqInfoMapper;
    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusConContractBpService hlsCusConContractBpService;
    @Autowired
    private HlsCusPrjProjectLeaseItemService hlsCusPrjProjectLeaseItemService;
    @Autowired
    private HlsCusConContractLeaseItemService hlsCusConContractLeaseItemService;
    @Autowired
    private HlsCusPrjProjectMortgageService hlsCusPrjProjectMortgageService;
    @Autowired
    private HlsCusConContractMortgageService hlsCusConContractMortgageService;
    @Autowired
    private HlsCusPrjProjectPledgeService hlsCusPrjProjectPledgeService;
    @Autowired
    private HlsCusConContractPledgeService hlsCusConContractPledgeService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusConQuotationService hlsCusConQuotationService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;

    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusEmployeeMapper employeeMapper;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    //    @Autowired
//    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;
    @Autowired
    private HlsCusContractFinanceIncomeService contractFinanceIncomeService;
    @Autowired
    private HlsCusFctQuotationCashflowService hlsCusFctQuotationCashflowService;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private IHlsCusCshPaymentReqLnService hlsCusCshPaymentReqLnService;
    @Autowired
    private HlsCusConContractPaymentPtService hlsCusConContractPaymentPtService;
    @Autowired
    private HlsCusContractAttachmentService hlsCusContractAttachmentService;
    @Autowired
    private HlsCusContractTerminationService hlsCusContractTerminationService;
    @Autowired
    private HlsCreditLineService hlsCreditLineService;
    @Autowired
    private HlsInvoiceProfileDtlService hlsInvoiceProfileDtlService;
    @Autowired
    private FndSysCodesService fndSysCodesService;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusPaymentDeductService hlsCusPaymentDeductService;
    @Autowired
    private HlsCusActMeetingRiskListService hlsCusActMeetingRiskListService;
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private HlsCusPrjProjectInsureService hlsCusPrjProjectInsureService;
    @Autowired
    private HlsCusConContractBeforeRentHService hlsCusConContractBeforeRentHService;
    @Autowired
    private HlsCusConContractBeforeRentHMapper hlsCusConContractBeforeRentHMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    private ProjectCreditConditionMapper projectCreditConditionMapper;

    @Autowired
    private HlsCusPrjQuotationDetailsMapper hlsCusPrjQuotationDetailsMapper;

    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;
    @Autowired
    private IHlsCusConContractRentPaymentConfirmService conContractRentPaymentConfirmService;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusConContractRentPaymentConfirmMapper conContractRentPaymentConfirmMapper;
    @Autowired
    private IGldContractCashflowService gldContractCashflowService;
    @Autowired
    private IConContractArchiveService contractArchiveService;

    @Autowired
    HlsCashflowAyncService hlsCashflowAyncService;

    @Autowired
    private ConChangeEtInfoService conChangeEtInfoSerivce;
    @Autowired
    ConChangeRepaymentInfoService conChangeRepaymentInfoService;

    @Autowired
    ConChangeEtInfoMapper conChangeEtInfoMapper;
    @Autowired
    ConChangeRepaymentInfoMapper conChangeRepaymentInfoMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private IAlipayService iAlipayService;

    @Override
    public List<Map<String, Object>> queryPaymentChangeInfoLov(IRequest request, HlsCusConContract hlsCusConContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusConContractMapper.queryPaymentChangeInfoLov(hlsCusConContract);
    }

    @Override
    public boolean submitConContractPreRepaymentChange(IRequest request, HlsCusConContract hlsCusConContract) {
        Date earlyTerminationDate = hlsCusConContract.getEarlyTerminationDate();
        List<HlsCusConContract> hlsCusConContractList = new ArrayList<>();
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(hlsCusConContract.getContractId());
        conContract = self().selectByPrimaryKey(request, hlsCusConContract);
        conContract.setEarlyTerminationDate(earlyTerminationDate);
        hlsCusConContractList.add(conContract);
        databaseLockProvider.lock(hlsCusConContract);
        // 获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        // 开始流程
        Map<String, Object> params = new HashMap<>();
        // params.put("workFlowType", "PRJ_CONTRACT_PREREPAYMENT");
        params.put("workFlowType", "PRJ_EARLY_SETTLEMENT");
        activitiStartService.start(request, hlsCusConContractList, params);
        // 更新审批信息表
        updateChangeReqInfo(request, conContract);
//        Map<String, Object> paramsEvent = new HashMap<>();
//        String userName = "";
//        if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
//            userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
//        }
//        String msg = userName + "创建了" + conContract.getContractName() + "提前还款审批" + conContract.getContractNumber();
//        paramsEvent.put("message", msg);
//        paramsEvent.put("noticeTitle", "提前还款审批");
//        paramsEvent.put("noticeType", "NOTICE");
//        paramsEvent.put("url", "");
//        paramsEvent.put("level", 1L);
//        sysEventService.eventSave(request, conContract.getContractId(), conContract.getDocumentCategory(), conContract.getDocumentType(), "BAC", "PRJ_CONTRACT_PREREPAYMENT", "P2D", paramsEvent);
        return true;
    }

    @Override
    public boolean submitConContractRentplanChange(IRequest request, HlsCusConContract hlsCusConContract) {
        List<HlsCusConContract> hlsCusConContractList = new ArrayList<>();
        HlsCusConContract conContract = self().selectByPrimaryKey(request, hlsCusConContract);
        hlsCusConContractList.add(conContract);
        databaseLockProvider.lock(hlsCusConContract);
        // 获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        // 开始流程
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "PRJ_CONTRACT_RENTALPLAN");
        activitiStartService.start(request, hlsCusConContractList, params);

        // 更新审批信息表
        updateChangeReqInfo(request, hlsCusConContract);
        Map<String, Object> paramsEvent = new HashMap<>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + conContract.getContractName() + "租赁租金计划变更审批" + conContract.getContractNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "租赁租金计划变更审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(request, conContract.getContractId(), conContract.getDocumentCategory(), conContract.getDocumentType(), "BAC", "PRJ_CONTRACT_RENTALPLAN", "P2D", paramsEvent);
        return true;
    }

    @Override
    public boolean submitConContractChange(IRequest request, HlsCusConContract hlsCusConContract) {
        List<HlsCusConContract> hlsCusConContractList = new ArrayList<>();
        HlsCusConContract conContract = self().selectByPrimaryKey(request, hlsCusConContract);
        hlsCusConContractList.add(conContract);
        databaseLockProvider.lock(hlsCusConContract);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程CON_CONTRACT_CHANGE
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "CON_ACCOUNT_CHANGE_WORK_FLOW");
        activitiStartService.start(request, hlsCusConContractList, params);

        // 更新审批信息表
        updateChangeReqInfo(request, hlsCusConContract);
        Map<String, Object> paramsEvent = new HashMap<>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + conContract.getContractName() + "租赁回款账户变更审批" + conContract.getContractNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "租赁回款账户变更审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(request, conContract.getContractId(), conContract.getDocumentCategory(), conContract.getDocumentType(), "BAC", "CON_CONTRACT_CHANGE", "P2D", paramsEvent);
        return true;
    }

    private void updateChangeReqInfo(IRequest request, HlsCusConContract hlsCusConContract) {
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusConContract = self().selectByPrimaryKey(request, hlsCusConContract);
        hlsCusChangeReqInfo.setChangeReqId(hlsCusConContract.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus("APPROVING");
        hlsCusChangeReqInfo.set__status(DTOStatus.UPDATE);
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, hlsCusChangeReqInfo);
    }

    @Override
    public boolean saveConContractChange(IRequest request, HlsCusConContract hlsCusConContract) {
        HlsCusConContract conContract = self().selectByPrimaryKey(request, hlsCusConContract);
        if (conContract == null) {
            return false;
        }
        HlsCusChangeReqInfo hlsCusChangeReqInfo = getChangeReqInfo(request, conContract);
        if (hlsCusChangeReqInfo == null) {
            return false;
        }
        conContract.setConPaymentAccountId(hlsCusConContract.getConPaymentAccountId());
        conContract.set__status(DTOStatus.UPDATE);
        self().updateByPrimaryKeySelective(request, conContract);
        return true;
    }

    /**
     * 取消回款账户，提前还款，租金计划变更
     *
     * @param hlsCusConContract 必须有contractId
     */
    @Override
    public boolean backConContractChange(IRequest request, HlsCusConContract hlsCusConContract) {
        HlsCusConContract changeContract = new HlsCusConContract();
        changeContract.setContractId(hlsCusConContract.getContractId());
        changeContract = self().selectByPrimaryKey(request, hlsCusConContract);
        HlsCusConContract normalContract = new HlsCusConContract();
        normalContract.setContractId(changeContract.getRefContractId());
        normalContract = self().selectByPrimaryKey(request, normalContract);
        HlsCusPrjProject normalProject = new HlsCusPrjProject();
        normalProject.setProjectId(normalContract.getProjectId());
        normalProject = hlsCusPrjProjectService.selectByPrimaryKey(request, normalProject);

        //原单据的信息
        normalContract.setContractStatus(changeContract.getContractStatus());
        normalContract.set__status(DTOStatus.UPDATE);
        self().updateByPrimaryKeySelective(request, normalContract);
        //原项目信息
        normalProject.setContractStatus(changeContract.getContractStatus());
        normalProject.set__status(DTOStatus.UPDATE);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, normalProject);
        //更新审批表数据
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(changeContract.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus(CANCEL);
        hlsCusChangeReqInfo.set__status(DTOStatus.UPDATE);
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, hlsCusChangeReqInfo);
        return true;
    }

    /**
     * 获取审批信息
     */
    @Override
    public HlsCusChangeReqInfo getChangeReqInfo(IRequest request, HlsCusConContract hlsCusConContract) {
        hlsCusConContract = self().selectByPrimaryKey(request, hlsCusConContract);
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(hlsCusConContract.getChangeReqId());
        return hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
    }

    //合同管理首页环状图
    @Override
    public List<Map> conHomePageGetAllStatusContractCount(IRequest iRequest, HlsCusConContract conContract) {
        return hlsCusConContractMapper.conHomePageGetAllStatusContractCount(conContract);
    }

    //合同管理首页grid
    @Override
    public List<HlsCusConContract> conHomePageContractInfoGrid(IRequest iRequest, HlsCusConContract conContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusConContractMapper.conHomePageContractInfoGrid(conContract);
    }

    @Override
    public List<HlsCusConContract> conContractSave(IRequest iRequest, List<HlsCusPrjQuotation> hlsCusPrjQuotationList) {
        List<HlsCusConContract> conContractList = new ArrayList<>();
        if (hlsCusPrjQuotationList.size() != 0) {
            //获取虚拟合同数据
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(hlsCusPrjQuotationList.get(0).getSourceDocumentId());
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjProject);

            //开始复制虚拟合同主表的信息到合同表上作为基本信息
            for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
                if ((dt.getSelectedFlag() != null) && dt.getSelectedFlag().equalsIgnoreCase("Y") && "NEW".equalsIgnoreCase(dt.getPaymentStatus())) {
                    HlsCusConContract oldCon = new HlsCusConContract();
                    oldCon.setQuotationId(dt.getQuotationId());
                    List<HlsCusConContract> oldList = self().select(iRequest, oldCon, 1, 100000);
                    if (oldList.size() == 0) {
                        HlsCusConContract hlsCusConContract = new HlsCusConContract();
                        hlsBeanRefUtilService.setFieldValue(hlsCusConContract, map);
                        hlsCusConContract.setContractAmount(dt.getTotalRental());
                        hlsCusConContract.setDataClass("NORMAL");
                        hlsCusConContract.setContractStatus("SIGN");
                        hlsCusConContract.setQuotationId(dt.getQuotationId());
                        hlsCusConContract.setProjectId(hlsCusPrjProject.getProjectId());
                        hlsCusConContract.setContractNumber(dt.getPaymentNumber());
                        hlsCusConContract.setDocumentCategory("CON_CONTRACT");
                        StringBuffer sub = new StringBuffer();
                        sub.append(hlsCusPrjProject.getBusinessType());
                        sub.append("_CON");
                        hlsCusConContract.setDocumentType(sub.toString());
                        hlsCusConContract.setAbsShowFlag("C");
                        hlsCusConContract.setPenaltyProfile("STD");
                        hlsCusConContract.setWriteOffAbleFlag("N");//设定未完成租金支付确认不能核销
                        hlsCusConContract = self().insertSelective(iRequest, hlsCusConContract);
                        hlsCusConContract = projectInfoCopy(iRequest, hlsCusConContract);
                        conContractList.add(hlsCusConContract);
                    }
                }
            }
        }

        return conContractList;
    }

    @Override
    public HlsCusPrjProject conContractSubmit(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws HlsCusException {
        //保存虚拟合同信息
        HlsCusPrjProject prjProject = hlsCusPrjProjectInfo.getHlsCusPrjProject();
        List<Map> prjProjects = hlsCusPrjProjectMapper.queryPrjDetail(prjProject);
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(Long.valueOf(prjProjects.get(0).get("project_id").toString()));
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.select(hlsCusPrjQuotation);
        if (hlsCusPrjQuotations.size() != 1) {
            throw new HlsCusException("请进行报价计算！");
        }
        if (!NEW.equals(prjProjects.get(0).get("contract_status")) &&
                !REJECTED.equals(prjProjects.get(0).get("contract_status")) &&
                !APPROVED_RETURN.equals(prjProjects.get(0).get("contract_status"))) {
            throw new HlsCusException("当前合同无法提交工作流，请联系管理员！");

        }

        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, prjProject);
        hlsCusPrjProjectList.add(prjProject);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (Objects.isNull(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "CON_CONTRACT_CREATE_WFL");
        if (prjProjects.get(0).get("hostUnitId") != null) {
            params.put("unitId", prjProjects.get(0).get("hostUnitId"));
        } else {
            throw new HlsCusException("主办项目经理部门为空");
        }
        params.put("companyId", iRequest.getCompanyId());

        params.put("assistUnitId", prjProjects.get(0).get("assistUnitId"));

        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);
        prjProject.setCreateContractStatus("APPROVING");
        prjProject.setContractStatus("APPROVING");
        prjProject = hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);

        return prjProject;

    }

    @Override
    public String paymentTableMakeStatus(IRequest iRequest, Long projectId) {
        //绿色 todo 灰色 undo 蓝色 read
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        //合同已撤销
        if ("CANCEL".equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) || "PENDING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else if ("SIGNING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "undo";
        }
        return "todo";
    }

    @Override
    public String paymentReqStatus(IRequest iRequest, Long projectId) {
        //绿色 todo 灰色 undo 蓝色 read
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        //合同已撤销
        if (("CANCEL").equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) || "PENDING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else if (("SIGNING").equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "undo";
        }
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(projectId);
        hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 10000);
        if (hlsCusPrjQuotationList.size() == 0) {
            //没有报价信息
            return "undo";
        } else if (hlsCusPrjQuotationList.size() != 0) {
            //租金支付确认过程中
            for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
                if (("NEW").equalsIgnoreCase(dt.getPaymentStatus())) {
                    return "todo";
                }
            }
        }
        return "read";
    }

    @Override
    public String paymentTableConfirmStatus(IRequest iRequest, Long projectId) {
        //绿色 todo 灰色 undo 蓝色 read
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        //合同已撤销
        if (("CANCEL").equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) || "PENDING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else if (("SIGNING").equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "undo";
        }
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(projectId);
        hlsCusPrjQuotation.setDataClass("PRJ_PROJECT_PAYMENT_TABLE");
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotationList = hlsCusPrjQuotationService.queryPaymentTableInfoConfirm(iRequest, hlsCusPrjQuotation, 1, 10000);
        Boolean hasApproved = false;
        Boolean hasApproving = false;
        if (hlsCusPrjQuotationList.size() == 0) {
            //没有报价信息
            return "undo";
        } else if (hlsCusPrjQuotationList.size() != 0) {
            //租金支付确认过程中
            for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
                if ("未确认".equalsIgnoreCase(dt.getConfirmFlag())) {
                    hasApproved = true;
                }
                if ("确认中".equalsIgnoreCase(dt.getConfirmFlag())) {
                    hasApproving = true;
                }
            }
        }
        if (hasApproving) {
            return "read";
        } else if (hasApproved) {
            return "todo";
        }
        return "read";
    }

    @Override
    public String conContractChangeStatus(IRequest iRequest, Long projectId) {
        //绿色 todo 灰色 undo 蓝色 read
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        //合同已撤销
        if (("CANCEL").equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) || "PENDING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else if (("SIGNING").equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "undo";
        }
        return "todo";
    }

    @Override
    public String conContractEtStatus(IRequest iRequest, Long projectId) {
        //绿色 todo 灰色 undo 蓝色 read
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        //合同已撤销
        if (("CANCEL").equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) || "PENDING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else if (("SIGNING").equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "undo";
        }
        return "todo";
    }

    @Override
    public String conContractCancelStatus(IRequest iRequest, Long projectId) {
        //绿色 todo 灰色 undo 蓝色 read
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        //合同已撤销
        if (("CANCEL").equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) || "PENDING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) || "INCEPT".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else if ("SIGNING".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "undo";
        }
        return "todo";
    }

    @Override
    public List<HlsCusConContract> queryPaymentInfoList(IRequest iRequest, HlsCusConContract conContract, int page, int pageSize) {
        return hlsCusConContractMapper.queryPaymentInfoList(conContract);
    }


    public HlsCusConContract projectInfoCopy(IRequest iRequest, HlsCusConContract conContract) {
        //项目
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(conContract.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);

        //客户信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = new ArrayList<>();
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(conContract.getProjectId());
        hlsCusPrjProjectBpList = hlsCusPrjProjectBpService.select(iRequest, hlsCusPrjProjectBp, 1, 100);
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            HlsCusConContractBp hlsCusConContractBp = new HlsCusConContractBp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusConContractBp, map);
            hlsCusConContractBp.setContractId(conContract.getContractId());
            hlsCusConContractBpService.insertSelective(iRequest, hlsCusConContractBp);
        }

        //租赁物清单
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItems = new ArrayList<>();
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(conContract.getProjectId());
        hlsCusPrjProjectLeaseItems = hlsCusPrjProjectLeaseItemService.select(iRequest, hlsCusPrjProjectLeaseItem, 1, 10);
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItems) {
            HlsCusConContractLeaseItem hlsCusConContractLeaseItem = new HlsCusConContractLeaseItem();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusConContractLeaseItem, map);
            hlsCusConContractLeaseItem.setContractId(conContract.getContractId());
            hlsCusConContractLeaseItemService.insertSelective(iRequest, hlsCusConContractLeaseItem);
        }

        //抵押物清单
        List<HlsCusPrjProjectMortgage> hlsCusPrjProjectMortgages = new ArrayList<>();
        HlsCusPrjProjectMortgage hlsCusPrjProjectMortgage = new HlsCusPrjProjectMortgage();
        hlsCusPrjProjectMortgage.setProjectId(conContract.getProjectId());
        hlsCusPrjProjectMortgages = hlsCusPrjProjectMortgageService.select(iRequest, hlsCusPrjProjectMortgage, 1, 100);
        for (HlsCusPrjProjectMortgage dt : hlsCusPrjProjectMortgages) {
            HlsCusConContractMortgage hlsCusConContractMortgage = new HlsCusConContractMortgage();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusConContractMortgage, map);
            hlsCusConContractMortgage.setContractId(conContract.getContractId());
            hlsCusConContractMortgageService.insertSelective(iRequest, hlsCusConContractMortgage);
        }

        //质押物清单
        List<HlsCusPrjProjectPledge> hlsCusPrjProjectPledges = new ArrayList<>();
        HlsCusPrjProjectPledge hlsCusPrjProjectPledge = new HlsCusPrjProjectPledge();
        hlsCusPrjProjectPledge.setProjectId(conContract.getProjectId());
        hlsCusPrjProjectPledges = hlsCusPrjProjectPledgeService.select(iRequest, hlsCusPrjProjectPledge, 1, 100);
        for (HlsCusPrjProjectPledge dt : hlsCusPrjProjectPledges) {
            HlsCusConContractPledge hlsCusConContractPledge = new HlsCusConContractPledge();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusConContractPledge, map);
            hlsCusConContractPledge.setContractId(conContract.getContractId());
            hlsCusConContractPledgeService.insertSelective(iRequest, hlsCusConContractPledge);
        }

        //报价信息
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(conContract.getQuotationId());
        hlsCusPrjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, hlsCusPrjQuotation);
        hlsCusPrjQuotation.setGeneratedStatus("GENERATED");
        hlsCusPrjQuotation = hlsCusPrjQuotationService.updateByPrimaryKey(iRequest, hlsCusPrjQuotation);
        hlsCusPrjQuotation.setSelectedFlag("N");
        hlsCusPrjQuotation = hlsCusPrjQuotationService.updateByPrimaryKey(iRequest, hlsCusPrjQuotation);
        HlsCusConQuotation hlsCusConQuotation = new HlsCusConQuotation();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotation);
        hlsBeanRefUtilService.setFieldValue(hlsCusConQuotation, map);
        hlsCusConQuotation.setSourceDocumentId(conContract.getContractId());
        hlsCusConQuotation.setSourceDocumentCategory("CON_CONTRACT");
        hlsCusConQuotation = hlsCusConQuotationService.insertSelective(iRequest, hlsCusConQuotation);
        Double downPayment = hlsCusConQuotation.getDownPayment() == null ? 0D : hlsCusConQuotation.getDownPayment();
        //回写合同
        conContract.setLeaseTimes(hlsCusConQuotation.getLeaseTimes());//租赁期数
        conContract.setLeaseTerm(hlsCusConQuotation.getLeaseTerm());//租赁期限
        conContract.setCurrency("CNY");//币种
        conContract.setInterestYearDays(hlsCusPrjQuotation.getInterestYearDays());//年计息天数
        conContract.setBillingProfile(hlsCusPrjQuotation.getBillingProfile());//开票规则
        conContract.setEstimateRentingDate(hlsCusConQuotation.getEstimateRentingDate());
        conContract.setPriceList(hlsCusPrjQuotation.getPriceList());
        conContract.setVatRate(hlsCusPrjQuotation.getVatRate());//稅率
        conContract.setInceptionOfLease(hlsCusConQuotation.getEstimateRentingDate());
        conContract.setRentingFrequency(hlsCusConQuotation.getRentingFrequency());
        conContract.setTotalRental(hlsCusConQuotation.getTotalRental());//租赁总价款
        conContract.setNetDownPayment(hlsCusConQuotation.getNetDownPayment());//不含税首付金额
        conContract.setVatDownPayment(hlsCusConQuotation.getVatDownPayment());//首付款税额
        conContract.setFinanceAmount(hlsCusConQuotation.getLeaseItemAmount() - downPayment);//融资额
        conContract.setNetFinanceAmount(hlsCusConQuotation.getNetFinanceAmount());//不含税融资额
        conContract.setVatFinanceAmount(hlsCusConQuotation.getVatFinanceAmount());//融资额增值税
        conContract.setNetTotalRental(hlsCusConQuotation.getNetTotalRental());//不含税租金
        conContract.setVatTotalRental(hlsCusConQuotation.getVatTotalRental());//租金增值税额
        conContract.setTotalInterest(hlsCusConQuotation.getTotalInterest());//利息总额
        conContract.setNetTotalInterest(hlsCusConQuotation.getNetTotalInterest());//不含税利息
        conContract.setVatTotalInterest(hlsCusConQuotation.getVatTotalInterest());//利息增值税额
        conContract.setLeaseChargeRatio(hlsCusConQuotation.getLeaseChargeRatio());//手续费比例
        conContract.setLeaseCharge(hlsCusConQuotation.getLeaseCharge());//手续费
        conContract.setNetLeaseCharge(hlsCusConQuotation.getNetLeaseCharge());//不含税手续费
        conContract.setVatLeaseCharge(hlsCusConQuotation.getVatLeaseCharge());//手续费税额
        conContract.setLeaseMgtFee(hlsCusConQuotation.getLeaseMgtFee());//管理费
        conContract.setLeaseMgtFeeRatio(hlsCusConQuotation.getLeaseMgtFeeRatio());//管理费比例
        conContract.setLeaseMgtFeeRule(hlsCusConQuotation.getLeaseMgtFeeRule());//管理费计算方式
        conContract.setDeposit(hlsCusConQuotation.getDeposit());//保证金
        conContract.setDepositRatio(hlsCusConQuotation.getDepositRatio());//保证金比例
        conContract.setDepositDeduction(hlsCusPrjQuotation.getDepositReturnMethod());//保证金抵扣方式
        conContract.setResidualValue(hlsCusConQuotation.getResidualValue());//留购价款
        conContract.setResidualRatio(hlsCusConQuotation.getResidualRatio());//留购价款比例
        conContract.setNetResidualValue(hlsCusConQuotation.getNetResidualValue());//不含税留购金
        conContract.setBalloon(hlsCusConQuotation.getBalloon());//末期租金
        conContract.setBalloonRatio(hlsCusConQuotation.getBalloonRatio());//末期租金比例
        conContract.setNetBalloon(hlsCusConQuotation.getNetBalloon());//不含税末期租金
        conContract.setVatBalloon(hlsCusConQuotation.getVatBalloon());//末期租金税额
        conContract.setBaseRateType(hlsCusConQuotation.getBaseRateType());// 基准利率类别
        conContract.setBaseRate(hlsCusConQuotation.getBaseRate());//基准利率
        conContract.setIntRateType(hlsCusConQuotation.getIntRateType());//租赁利率类型
        conContract.setIntRate(hlsCusConQuotation.getIntRate());//租赁利率
        conContract.setIrr(hlsCusConQuotation.getIrr());//内部收益率
        conContract.setIrrAfterTax(hlsCusConQuotation.getIrrAfterTax());//税后内部收益率
        //conContract.setPenaltyProfile(hlsCusConQuotation.getPenaltyProfile());//迟延履行金规则
        conContract.setFloatingWay(hlsCusConQuotation.getFloatingWay());//浮动方式
        conContract.setFloatingWayRate(hlsCusConQuotation.getFloatingWayRate());
        conContract.setAnnualPayTimes(getRentingFrequency(conContract.getRentingFrequency()));//还租频率
        conContract.setVatInput(hlsCusPrjQuotation.getVatInput());//进项税额
        conContract.setVatInputTaxTypeRate(hlsCusPrjQuotation.getVatInputTaxTypeRate());//进项税率
        conContract.setLeaseItemAmount(hlsCusPrjQuotation.getLeaseItemAmount());//租赁物总价款
        conContract.setBaseRateType("PBOC");
        conContract.setReceivedStatus("NOT");
        conContract.setFloatingRangeMethod(hlsCusPrjProject.getFloatingRangeMethod());//调息范围规则
        conContract.setDataClass("NORMAL");
        conContract.setInceptionOfLease(null);

        conContract = self().updateByPrimaryKey(iRequest, conContract);

        //插入对应现金流表
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(conContract.getQuotationId());
        hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowService.select(iRequest, hlsCusPrjQuotationCashflow, 1, 1000);
        for (HlsCusPrjQuotationCashflow dto : hlsCusPrjQuotationCashflows) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(hlsCusConContractCashflow, mapCsh);
            hlsCusConContractCashflow.setQuotationId(hlsCusConQuotation.getQuotationId());
            hlsCusConContractCashflow.setContractId(conContract.getContractId());
            hlsCusConContractCashflow.setWriteOffFlag("NOT");
            hlsCusConContractCashflowService.insertSelective(iRequest, hlsCusConContractCashflow);
        }
        return conContract;
    }

    //租赁支付表未实现融资收益分摊
    //add by zhangyu on 2018/6/5
    @Override
    public void calcConFinIncome(IRequest iRequest, HlsCusConContract conContract) {
        Long contractId = conContract.getContractId();
        Long companyId = conContract.getCompanyId();
        Date calcStartDate = conContract.getInceptionOfLease();
        Date lastRepaymentDate = conContract.getInceptionOfLease();

        //  Double totalNetInterest = 0D;
        Long incomeTimes = getOperatingLeaseRentingFrequency(conContract.getRentingFrequency());
        if ("INCEPT".equalsIgnoreCase(conContract.getContractStatus())) {
            calcStartDate = conContract.getInceptionOfLease();
            lastRepaymentDate = conContract.getInceptionOfLease();
        }

        //删除未确认收益
        HlsCusContractFinanceIncome contractFinanceIncomeTmp = new HlsCusContractFinanceIncome();
        contractFinanceIncomeTmp.setContractId(contractId);
        contractFinanceIncomeTmp.setPostFlag("N");
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = contractFinanceIncomeService.select(iRequest, contractFinanceIncomeTmp, 1, 999999);
        contractFinanceIncomeService.batchDelete(contractFinanceIncomeList);

        //获取支付表项下所有租金还款计划
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(contractId);
        hlsCusConContractCashflow.setCfItem(CF_ITEM_DUE_AMOUNT);
        List<HlsCusConContractCashflow> conContractCashflowList = hlsCusConContractCashflowMapper.queryCashflowByCfItemOrderByDueDate(hlsCusConContractCashflow);
        if (conContractCashflowList.size() > 0 && calcStartDate != null) {
            int month;
            Calendar cStart = Calendar.getInstance();
            Calendar cEnd = Calendar.getInstance();
            //初始化待插入分摊数据
            HlsCusContractFinanceIncome contractFinanceIncome = new HlsCusContractFinanceIncome();
            if (!conContract.getBusinessType().equals("OPERATING_LEASE")) {
                //循环租金还款计划，利息分摊
                for (HlsCusConContractCashflow conContractCashflow : conContractCashflowList
                ) {
                    Double sumFinIncome;
                    Double sumFinIncomeInclud;
                    Double finIncome;
                    Double finIncomeInclud;
                    Long calcDays;

                    cStart.setTime(calcStartDate);
                    cEnd.setTime(conContractCashflow.getCalcDate());
                    cEnd.add(Calendar.DAY_OF_MONTH, -1);
                    month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH);

                    Calendar calcStart = Calendar.getInstance();
                    Calendar calcEnd = Calendar.getInstance();
                    calcStart.setTime(calcStartDate);

                    //循环相差月份
                    for (int i = 0; i <= month; i++) {
                        Calendar calcCal = Calendar.getInstance();
                        //还款当月前半段起租日至还款日，倒减计算
                        if (i == 0 && month == 0) {
                            calcEnd.setTime(conContractCashflow.getCalcDate());
                            calcEnd.add(Calendar.DAY_OF_MONTH, -1);
                            calcDays = getCalcDays2(calcStart.getTime(), calcEnd.getTime());
                            sumFinIncome = getSumFinIncome(iRequest, conContractCashflow.getCashflowId(), contractId);
                            sumFinIncomeInclud = getSumFinIncomeInclud(iRequest, conContractCashflow.getCashflowId(), contractId);
                            finIncome = conContractCashflow.getNetInterest() - sumFinIncome;
                            finIncomeInclud = conContractCashflow.getInterest() - sumFinIncomeInclud;
                        }
                        //还款当月后半段至月底，按天数权重计算
                        else if (i == 0 && month != 0) {
                            calcEnd.setTime(getMonthEndDate(calcStartDate));
                            calcDays = getCalcDays2(calcStart.getTime(), calcEnd.getTime());
                            calcCal.setTime(conContractCashflow.getCalcDate());
                            calcCal.add(Calendar.DAY_OF_MONTH, -1);
                            finIncome = (double) Math.round(conContractCashflow.getNetInterest() * calcDays / getCalcDays2(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                            finIncomeInclud = (double) Math.round(conContractCashflow.getInterest() * calcDays / getCalcDays2(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                        }
                        //还款当月前半段月初至还款日，倒减计算
                        else if (i == month && month > 0) {
                            calcEnd.setTime(conContractCashflow.getCalcDate());
                            calcEnd.add(Calendar.DAY_OF_MONTH, -1);
                            calcDays = getCalcDays2(calcStart.getTime(), calcEnd.getTime());
                            sumFinIncome = getSumFinIncome(iRequest, conContractCashflow.getCashflowId(), contractId);
                            sumFinIncomeInclud = getSumFinIncomeInclud(iRequest, conContractCashflow.getCashflowId(), contractId);
                            finIncome = conContractCashflow.getNetInterest() - sumFinIncome;
                            finIncomeInclud = conContractCashflow.getInterest() - sumFinIncomeInclud;
                        }
                        //整月计算，按天数权重计算
                        else {
                            calcEnd.setTime(getMonthEndDate(calcStartDate));
                            calcDays = getCalcDays2(calcStart.getTime(), calcEnd.getTime());
                            calcCal.setTime(conContractCashflow.getCalcDate());
                            calcCal.add(Calendar.DAY_OF_MONTH, -1);
                            finIncome = (double) Math.round(conContractCashflow.getNetInterest() * calcDays / getCalcDays2(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                            finIncomeInclud = (double) Math.round(conContractCashflow.getInterest() * calcDays / getCalcDays2(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                        }
                        contractFinanceIncome.setCompanyId(companyId);
                        contractFinanceIncome.setContractId(contractId);
                        contractFinanceIncome.setGldCashflowId(conContractCashflow.getCashflowId());
                        String periodName;
                        if (calcEnd.get(Calendar.MONTH) + 1 < 10) {
                            periodName = calcEnd.get(Calendar.YEAR) + "-0" + (calcEnd.get(Calendar.MONTH) + 1);

                        } else {
                            periodName = calcEnd.get(Calendar.YEAR) + "-" + (calcEnd.get(Calendar.MONTH) + 1);
                        }
                        contractFinanceIncome.setPeriodName(periodName);
                        contractFinanceIncome.setStartDate(calcStart.getTime());
                        contractFinanceIncome.setEndDate(calcEnd.getTime());
                        contractFinanceIncome.setDays(calcDays);
                        contractFinanceIncome.setFinanceIncome(finIncome);
                        contractFinanceIncome.setFinanceIncomeInclud(finIncomeInclud);
                        contractFinanceIncome.setFinanceIncomeVat(finIncomeInclud - finIncome);
                        contractFinanceIncome.setCfItem(conContractCashflow.getCfItem());
                        contractFinanceIncome.setPostFlag("N");
                        contractFinanceIncome.setFinanceIncomeId(null);
                        contractFinanceIncome.setSourceType("CON_CONTRACT");
                        contractFinanceIncome.setSourceId(contractId);
                        HlsCusContractFinanceIncome contractFinanceIncomeExists = new HlsCusContractFinanceIncome();
                        contractFinanceIncomeExists.setContractId(contractId);
                        contractFinanceIncomeExists.setCompanyId(companyId);
                        //contractFinanceIncomeExists.setGldCashflowId(conContractCashflow.getCashflowId());
                        contractFinanceIncomeExists.setStartDate(calcStart.getTime());
                        contractFinanceIncomeExists.setEndDate(calcEnd.getTime());
                        List<HlsCusContractFinanceIncome> contractFinanceIncomeExistsList = contractFinanceIncomeService.select(iRequest, contractFinanceIncomeExists, 1, 999999);
                        if (contractFinanceIncomeExistsList.size() == 0) {
                            contractFinanceIncomeService.insertSelective(iRequest, contractFinanceIncome);
                        }
                        calcEnd.add(Calendar.DAY_OF_MONTH, 1);
                        calcStartDate = calcEnd.getTime();
                        calcStart.setTime(calcStartDate);
                        //  totalNetInterest = totalNetInterest + finIncome;
                    }
                    cEnd.add(Calendar.DAY_OF_MONTH, 1);
                    calcStartDate = cEnd.getTime();
                    lastRepaymentDate = conContractCashflow.getCalcDate();

                }
            }


            hlsCusConContractCashflow.setCfItem(CF_ITEM_LEASE_CHARGE);
            List<HlsCusConContractCashflow> hlsCusConContractCashflowlist1 = hlsCusConContractCashflowMapper.queryCashflowLeaseChargeinfo(hlsCusConContractCashflow);
            if (hlsCusConContractCashflowlist1.get(0) != null) {
                //手续费分摊
                calcConLeaseChargeFinIncome(iRequest, conContract);
            }

            //融资服务费
            Double rate = getRate(iRequest, CF_ITEM_ADVSERVICEFEE, conContract.getBusinessType());
            //咨询服务费
            hlsCusConContractCashflow.setCfItem(CF_ITEM_ADVSERVICEFEE);
            List<HlsCusConContractCashflow> hlsCusConContractCashflowlist = hlsCusConContractCashflowMapper.queryCashflowLeaseChargeinfo(hlsCusConContractCashflow);
            if (hlsCusConContractCashflowlist.get(0) != null) {
                Calendar cal = Calendar.getInstance();
                int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                List<HlsCusContractFinanceIncome> advServiceFeeIncomeList = contractFinanceIncomeService.select(iRequest, contractFinanceIncomeTmp, 1, 1);
                HlsCusContractFinanceIncome hlsCusContractFinanceService = new HlsCusContractFinanceIncome();
                if (advServiceFeeIncomeList.size() > 0) {
                    Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(advServiceFeeIncomeList.get(0));
                    hlsBeanRefUtilService.setFieldValue(hlsCusContractFinanceService, mapCsh);
                }
                hlsCusContractFinanceService.setCfItem(CF_ITEM_ADVSERVICEFEE);
                Double netadvServiceFee = (double) Math.round(hlsCusConContractCashflowlist.get(0).getDueAmount() / (rate + 1) * 100) / 100;//不含税金额
                Double advServiceFeeVat = hlsCusConContractCashflowlist.get(0).getDueAmount() - netadvServiceFee;//税额
                hlsCusContractFinanceService.setFinanceIncomeVat(advServiceFeeVat);
                hlsCusContractFinanceService.setFinanceIncome(netadvServiceFee);
                hlsCusContractFinanceService.setFinanceIncomeInclud(hlsCusConContractCashflowlist.get(0).getDueAmount());
                hlsCusContractFinanceService.setGldCashflowId(hlsCusConContractCashflowlist.get(0).getCashflowId());
                hlsCusContractFinanceService.setStartDate(hlsCusConContractCashflowlist.get(0).getDueDate());
                hlsCusContractFinanceService.setEndDate(hlsCusConContractCashflowlist.get(0).getDueDate());
                hlsCusContractFinanceService.setDays(1L);
                hlsCusContractFinanceService.setCreationDate(new Date());
                HlsCusContractFinanceIncome hlsCusContractFinance1 = new HlsCusContractFinanceIncome();
                hlsCusContractFinance1.setStartDate(hlsCusContractFinanceService.getStartDate());
                hlsCusContractFinance1.setEndDate(hlsCusContractFinanceService.getEndDate());
                hlsCusContractFinance1.setCfItem(CF_ITEM_ADVSERVICEFEE);
                hlsCusContractFinance1.setContractId(conContract.getContractId());
                hlsCusContractFinance1.setPeriodName(hlsCusContractFinanceService.getPeriodName());
                List<HlsCusContractFinanceIncome> contractFinanceIncomeExistsList = contractFinanceIncomeService.select(iRequest, hlsCusContractFinance1, 1, 999999);
                if (contractFinanceIncomeExistsList.size() == 0) {
                    contractFinanceIncomeService.insertSelective(iRequest, hlsCusContractFinanceService);
                }
            }
        }

    }

    //获取日期之间计提总和
    private Double getSumFinIncome(IRequest request, Long cashflowId, Long contractId) {
        Double result = 0D;

        HlsCusContractFinanceIncome contractFinanceIncomeTmp = new HlsCusContractFinanceIncome();
        contractFinanceIncomeTmp.setContractId(contractId);
        contractFinanceIncomeTmp.setGldCashflowId(cashflowId);
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = contractFinanceIncomeService.select(request, contractFinanceIncomeTmp, 1, 999999);

        if (contractFinanceIncomeList.size() > 0) {
            for (HlsCusContractFinanceIncome contractFinanceIncome : contractFinanceIncomeList
            ) {
                result = result + contractFinanceIncome.getFinanceIncome();

            }
        }

        return result;
    }

    //获取日期之间计提总和
    private Double getSumFinIncomeInclud(IRequest request, Long cashflowId, Long contractId) {
        Double result = 0D;

        HlsCusContractFinanceIncome contractFinanceIncomeTmp = new HlsCusContractFinanceIncome();
        contractFinanceIncomeTmp.setContractId(contractId);
        contractFinanceIncomeTmp.setGldCashflowId(cashflowId);
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = contractFinanceIncomeService.select(request, contractFinanceIncomeTmp, 1, 999999);

        if (contractFinanceIncomeList.size() > 0) {
            for (HlsCusContractFinanceIncome contractFinanceIncome : contractFinanceIncomeList
            ) {
                result = result + contractFinanceIncome.getFinanceIncomeInclud();

            }
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> selectContractForABS(IRequest request, HlsCusConContract hlsCusConContract, int page, int pagesize) {
        Map<String, Object> map = new HashMap<>(5);
        if (StringUtils.isNotEmpty(hlsCusConContract.getParams())) {
            map.put("contractIds", hlsCusConContract.getParams().split(","));
        }
        map.put("contractName", hlsCusConContract.getContractName());
        map.put("contractNumber", hlsCusConContract.getContractNumber());
        map.put("dateStr", hlsCusConContract.getDateStr());

        LocalDate baseDate = LocalDate.parse(hlsCusConContract.getDateStr());  // 封包日
        LocalDate dueDateBegin = LocalDate.parse(hlsCusConContract.getDateStr2());  // 发行日
        PageHelper.startPage(page, pagesize);
        List<Map<String, Object>> list = hlsCusConContractMapper.selectContractForABS(map);
        return this.calcContractAmount(list, baseDate, dueDateBegin, request);
    }


    @SuppressWarnings("all")
    @Override
    public List<Map<String, Object>> calcContractAmount(List<Map<String, Object>> maps, LocalDate baseDate, LocalDate dueDateBegin, IRequest request) {
        for (Map<String, Object> contract : maps) {
            if (StringUtils.equalsIgnoreCase("CON_CONTRACT", (String) contract.get("documentType"))) {
                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                hlsCusConContractCashflow.setContractId((Long) contract.get("contractId"));
                List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowService.select(request, hlsCusConContractCashflow, 1, 99999);
                cashflowList = cashflowList.stream().filter(cashflow -> cashflow.getCfItem().equals(CF_ITEM_DUE_AMOUNT))
                        .sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).collect(Collectors.toList());
                for (HlsCusConContractCashflow cashflow : cashflowList) {
                    if (baseDate.isEqual(DateUtils.format(cashflow.getDueDate()))) {
                        // 如果封包日正好等于某一期的应收日期,那么上面查询数据不需要修正
                        break;
                    }
                    // 封包日小于某一期应收日期时, 因为集合是根据应收日期顺序排序后的,所以第一次进入判断的就是距离封包日最近的未收的应收日期
                    if (baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        int index = cashflowList.indexOf(cashflow);
                        if (index > 0) {
                            // 计算出上一期与这一期的天数差
                            long days = DateUtils.between(cashflowList.get(index - 1).getDueDate(), cashflow.getDueDate());
                            // 计算出封包日与这一期的天数差
                            long days2 = DateUtils.between(DateUtils.format(baseDate), cashflow.getDueDate());

                            BigDecimal uncollectedInterest = new BigDecimal(cashflow.getInterest());
                            uncollectedInterest = uncollectedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_EVEN).add((BigDecimal) contract.get("uncollectedInterest"));
                            contract.put("uncollectedInterest", uncollectedInterest);
                            break;
                        } else {
                            // 封包日在第一期之前那么结束循环
                            break;
                        }
                    }
                }
                // 此集合存储封包日到发行日之间的现金流信息
                List<HlsCusConContractCashflow> cashflows = new ArrayList<>(10);
                for (HlsCusConContractCashflow cashflow : cashflowList) {
                    if (dueDateBegin.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        break;
                    }
                    if (baseDate.isEqual(DateUtils.format(cashflow.getDueDate())) || baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        cashflows.add(cashflow);
                    }
                }
                if (CollectionUtils.isEmpty(cashflows)) {
                    contract.put("receivedInterest", 0); // 已收利息
                    contract.put("netReceivedInterest", 0); // 不含税已收利息
                    contract.put("receivedPrincipal", 0); // 已收本金
                } else {
                    long days1, days2;
                    if (cashflows.get(0).getTimes() == 1L) {
                        days1 = 1L;
                        days2 = 1L;
                    } else {
                        days1 = DateUtils.between(cashflowList.get(cashflowList.indexOf(cashflows.get(0)) - 1).getDueDate(), cashflows.get(0).getDueDate());
                        days2 = DateUtils.between(baseDate, DateUtils.format(cashflows.get(0).getDueDate()));
                    }
                    BigDecimal receivedInterest = new BigDecimal(cashflows.get(0).getReceivedInterest() == null ? 0 : cashflows.get(0).getReceivedInterest());
                    receivedInterest = receivedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days1), 2, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal receivedPrincipal = new BigDecimal(cashflows.get(0).getReceivedPrincipal());
                    if (cashflows.size() > 1) {
                        for (int i = 1; i < cashflows.size(); i++) {
                            receivedInterest = receivedInterest.add(new BigDecimal(cashflows.get(i).getReceivedInterest()));
                            receivedPrincipal = receivedPrincipal.add(new BigDecimal(cashflows.get(i).getReceivedPrincipal()));
                        }
                    }
                    contract.put("receivedInterest", receivedInterest); // 已收利息
                    contract.put("netReceivedInterest", receivedInterest.divide(new BigDecimal(1.06), 2, BigDecimal.ROUND_HALF_EVEN)); // 不含税已收利息
                    contract.put("receivedPrincipal", receivedPrincipal); // 已收本金
                }

            }
            if (StringUtils.equalsIgnoreCase("FCT_CONTRACT", (String) contract.get("documentType"))) {
                HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow = new HlsCusFctQuotationCashflow();
                hlsCusFctQuotationCashflow.setContractId((Long) contract.get("contractId"));
                List<HlsCusFctQuotationCashflow> cashflowList = hlsCusFctQuotationCashflowService.select(request, hlsCusFctQuotationCashflow, 1, 99999);
                cashflowList = cashflowList.stream().filter(cashflow -> (cashflow.getCfItem() == 54L || cashflow.getCfItem() == 55L))
                        .sorted(Comparator.comparing(HlsCusFctQuotationCashflow::getDueDate)).collect(Collectors.toList());
                for (HlsCusFctQuotationCashflow cashflow : cashflowList) {
                    cashflow.setInterest(cashflow.getInterest() == null ? 0D : cashflow.getInterest());
                    cashflow.setReceivedInterest(cashflow.getReceivedInterest() == null ? 0D : cashflow.getReceivedInterest());
                    cashflow.setPrincipal(cashflow.getPrincipal() == null ? 0D : cashflow.getPrincipal());
                    cashflow.setReceivedPrincipal(cashflow.getReceivedPrincipal() == null ? 0D : cashflow.getReceivedPrincipal());
                    if (baseDate.isEqual(DateUtils.format(cashflow.getDueDate()))) {
                        // 如果封包日正好等于某一期的应收日期,那么上面查询数据不需要修正
                        break;
                    }
                    // 封包日小于某一期应收日期时, 因为集合是根据应收日期顺序排序后的,所以第一次进入判断的就是距离封包日最近的未收的应收日期
                    if (baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        int index = cashflowList.indexOf(cashflow);
                        if (index > 0) {
                            // 计算出上一期与这一期的天数差
                            HlsCusFctQuotationCashflow quotationCashflow = new HlsCusFctQuotationCashflow();
                            quotationCashflow.setContractId(cashflow.getContractId());
                            quotationCashflow.setTimes(cashflow.getTimes() - 1);
                            List<HlsCusFctQuotationCashflow> cl = hlsCusFctQuotationCashflowService.select(request, quotationCashflow, 1, 99999);
                            long days = DateUtils.between(cl.get(0).getDueDate(), cashflow.getDueDate());
                            // 计算出封包日与这一期的天数差
                            long days2 = DateUtils.between(DateUtils.format(baseDate), cashflow.getDueDate());

                            BigDecimal uncollectedInterest = new BigDecimal(cashflow.getInterest());
                            uncollectedInterest = uncollectedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days)).add((BigDecimal) contract.get("uncollectedInterest"));
                            contract.put("uncollectedInterest", uncollectedInterest);
                            break;
                        }
                    }
                }

                // 此集合存储封包日到发行日之间的现金流信息
                List<HlsCusFctQuotationCashflow> cashflows = new ArrayList<>(10);
                for (HlsCusFctQuotationCashflow cashflow : cashflowList) {
                    if (dueDateBegin.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        break;
                    }
                    if (baseDate.isEqual(DateUtils.format(cashflow.getDueDate())) || baseDate.isBefore(DateUtils.format(cashflow.getDueDate()))) {
                        cashflows.add(cashflow);
                    }
                }
                if (CollectionUtils.isEmpty(cashflows)) {
                    contract.put("receivedInterest", 0); // 已收利息
                    contract.put("netReceivedInterest", 0); // 不含税已收利息
                    contract.put("receivedPrincipal", 0); // 已收本金
                } else {
                    // 计算出上一期与这一期的天数差
                    HlsCusFctQuotationCashflow quotationCashflow = new HlsCusFctQuotationCashflow();
                    quotationCashflow.setContractId(cashflows.get(0).getContractId());
                    quotationCashflow.setTimes(cashflows.get(0).getTimes() - 1);
                    List<HlsCusFctQuotationCashflow> cl = hlsCusFctQuotationCashflowService.select(request, quotationCashflow, 1, 99999);
                    long days1 = DateUtils.between(cl.get(0).getDueDate(), cashflows.get(0).getDueDate());
                    // 计算出封包日与这一期的天数差
                    long days2 = days1;
                    if (cashflows.get(0).getTimes() != 1L) {
                        days2 = DateUtils.between(baseDate, DateUtils.format(cashflows.get(0).getDueDate()));
                    }
                    BigDecimal receivedInterest = new BigDecimal(cashflows.get(0).getReceivedInterest() == null ? 0 : cashflows.get(0).getReceivedInterest());
                    receivedInterest = receivedInterest.multiply(new BigDecimal(days2)).divide(new BigDecimal(days1), 2, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal receivedPrincipal = new BigDecimal(cashflows.get(0).getReceivedPrincipal());
                    if (cashflows.size() > 1) {
                        for (int i = 1; i < cashflows.size(); i++) {
                            receivedInterest = receivedInterest.add(new BigDecimal(cashflows.get(i).getReceivedInterest()));
                            receivedPrincipal = receivedPrincipal.add(new BigDecimal(cashflows.get(i).getReceivedPrincipal()));
                        }
                    }
                    contract.put("receivedInterest", receivedInterest); // 已收利息
                    contract.put("netReceivedInterest", receivedInterest.divide(new BigDecimal(1.06), 2, BigDecimal.ROUND_HALF_EVEN)); // 不含税已收利息
                    contract.put("receivedPrincipal", receivedPrincipal); // 已收本金
                }
            }
        }
        return maps;
    }

    @Override
    public List<HlsCusConContract> selectConCshPaymentReqForSummary(IRequest iRequest, HlsCusConContract dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusConContractMapper.selectConCshPaymentReqForSummary(dto);
    }

    @Override
    public List<HlsCusConContract> conContractCshReqDetail(HlsCusConContract dto) {
        return hlsCusConContractMapper.conContractCshReqDetail(dto);
    }


    @Override
    public void conContractValidate(IRequest request, HlsCusContractPkg hlsCusContractPkg) throws HlsCusException {
        Double totalPaymentAmount = 0D;
        Double totalOtherPaymentAmount = 0D;
        HlsCusConContract dto = hlsCusContractPkg.getHlsCusConContract();
        Long contractId = dto.getContractId();
        //支付结算信息头
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = hlsCusContractPkg.getHlsCusCshPaymentReqHd();
        //判断放款金额
        HlsCusCshPaymentReqHd cshPaymentReqTemp = new HlsCusCshPaymentReqHd();
        cshPaymentReqTemp.setSourceDocId(contractId);
        cshPaymentReqTemp.setSourceDocType("CON_CONTRACT");
        List<HlsCusCshPaymentReqHd> cshPaymentReqTempList = cshPaymentReqHdService.select(request, cshPaymentReqTemp, 1, 9999999);
        HlsCusConContractCashflow hlsCusConContractCashflowAmount = new HlsCusConContractCashflow();
        for (HlsCusCshPaymentReqHd paymentReqHd : cshPaymentReqTempList) {
            if (paymentReqHd.getSourceDocLineId() != null) {
                hlsCusConContractCashflowAmount.setCashflowId(paymentReqHd.getSourceDocLineId());
                hlsCusConContractCashflowAmount = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflowAmount);
            }
            if (paymentReqHd.getPaymentReqId().compareTo(hlsCusCshPaymentReqHd.getPaymentReqId()) != 0 && hlsCusConContractCashflowAmount.getCfItem().compareTo(0L) == 0) {
                totalPaymentAmount = totalPaymentAmount + paymentReqHd.getAmount();
            }
            if (paymentReqHd.getPaymentReqId().compareTo(hlsCusCshPaymentReqHd.getPaymentReqId()) != 0 && hlsCusConContractCashflowAmount.getCfItem().compareTo(1000L) == 0) {
                totalOtherPaymentAmount = totalOtherPaymentAmount + paymentReqHd.getAmount();
            }
        }
        if (hlsCusCshPaymentReqHd.getSourceDocLineId() != null) {
            hlsCusConContractCashflowAmount.setCashflowId(hlsCusCshPaymentReqHd.getSourceDocLineId());
            hlsCusConContractCashflowAmount = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflowAmount);
        }

        if (hlsCusConContractCashflowAmount.getCfItem().compareTo(0L) == 0) {
            totalPaymentAmount = hlsCusCshPaymentReqHd.getAmount() + totalPaymentAmount;
        } else {
            totalOtherPaymentAmount = hlsCusCshPaymentReqHd.getAmount() + totalOtherPaymentAmount;
        }

        if (totalPaymentAmount.compareTo(dto.getLeaseItemAmount()) > 0) {
            throw new HlsCusException("放款金额已大于合同可放款金额！");
        } else if (totalOtherPaymentAmount.compareTo(hlsCusConContractCashflowAmount.getDueAmount()) > 0) {
            throw new HlsCusException("放款金额已大于款项 【" + hlsCusConContractCashflowAmount.getCashflowName() + "】 可放款金额！");
        }
    }

    @Override
    public HlsCusConContract conContractSave(IRequest request, HlsCusContractPkg hlsCusContractPkg) {
        HlsCusConContract dto = hlsCusContractPkg.getHlsCusConContract();
        Long contractId = dto.getContractId();
        Long projectId = dto.getProjectId();
        //支付结算信息头
        //更新期限
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        HlsCusHlsCreditLine resultHlsCusHlsCreditline = new HlsCusHlsCreditLine();
        resultHlsCusHlsCreditline.setCreditLineId(hlsCusPrjProject.getCreditLineId());
        resultHlsCusHlsCreditline = hlsCreditLineService.selectByPrimaryKey(request, resultHlsCusHlsCreditline);
        resultHlsCusHlsCreditline.setTerm(Long.valueOf(dto.getTerm()));
        hlsCreditLineService.updateByPrimaryKey(request, resultHlsCusHlsCreditline);
        //回款账户
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(dto.getContractId());
        hlsCusConContract.setConPaymentAccountId(dto.getConPaymentAccountId());
        hlsCusConContract.setReturnAccount(dto.getConPaymentAccountId());
        hlsCusConContract.setPlanLnId(dto.getPlanLnId());
        self().updateByPrimaryKeySelective(request, hlsCusConContract);
        return dto;
    }

    @Override
    public void conLoanSubmit(IRequest iRequest, Long paymentReqId) throws ResMessageException {
        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<>();
        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(paymentReqId);
        cshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(iRequest, cshPaymentReqHd);
        hlsCusCshPaymentReqHds.add(cshPaymentReqHd);

        if (!cshPaymentReqHd.getPaymentReqStatus().equalsIgnoreCase("APPROVING") && !cshPaymentReqHd.getPaymentReqStatus().equalsIgnoreCase("APPROVED")) {
            ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
      /*  if(hlsCusCshPaymentReqHds.size() != 1){
            throw new ResMessageException("单据未找到");
        }*/
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setPaymentReqId(paymentReqId);
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns = hlsCusCshPaymentReqLnMapper.select(hlsCusCshPaymentReqLn);
            for (int i = 0; i < hlsCusCshPaymentReqLns.size(); i++) {
                if (hlsCusCshPaymentReqLns.get(i).getBpBankAccountName() == null
                        && hlsCusCshPaymentReqLns.get(i).getBpBankAccountNum() == null
                        && hlsCusCshPaymentReqLns.get(i).getBpBankBranchName() == null) {
                    throw new ResMessageException("收款方户名、收款账号、收款银行信息不能为空！");
                }
            }

            //add by 20211112 涂广新  部门年度已放款金额+本次申请金额 若超出部门年度预算 切备注栏为空则强校验
            HlsCusCshPaymentReqHd reqHd = hlsCusCshPaymentReqHdMapper.queryCshPaymentCheckHdById(cshPaymentReqHd.getPaymentReqId());
            if (reqHd != null) {
                if (reqHd.getFillAmount() != 0 && cshPaymentReqHd.getDescription() == null) {
                    if (reqHd.getFillAmount() < reqHd.getDueAmount()) {
                        throw new ResMessageException("本次申请金额超出年度预算，请项目经理在放款申请时填写备注说明！");
                    }
                }
            }

            databaseLockProvider.lock(cshPaymentReqHd);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            if (Objects.isNull(employee)) {
                throw new ResMessageException("获取提交人失败");
            }
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
            //开始流程
            Map<String, Object> params = new HashMap<>();
            params.put("workFlowType", "CON_PAYMENT_WFL");
            params.put("loanTotalAmount", cshPaymentReqHd.getLoanTotalAmount());

            activitiStartService.start(iRequest, hlsCusCshPaymentReqHds, params);

            //保留原始的userId
            Long originalUserId = iRequest.getUserId();
            String msg = "工作流开始通知-放款申请审批流程-" + cshPaymentReqHd.getBpName();
            String projectId = "";
            if (cshPaymentReqHd.getSourceContractId() != null) {
                HlsCusConContract contract = new HlsCusConContract();
                contract.setContractId(cshPaymentReqHd.getSourceContractId());
                contract = self().selectByPrimaryKey(iRequest, contract);
                projectId = contract.getProjectId().toString();
                msg = "工作流开始通知-放款申请审批流程-" + contract.getContractName() + "-" + cshPaymentReqHd.getBpName() + "-" + contract.getContractNumber();
            }
            String url = "/CSH/CSH_SET/CSH002/csh002_csh_payment_req_hd_detail.lview?layout_code=CSH002F2QNEW&payment_req_id=" + cshPaymentReqHd.getPaymentReqId() + "&project_id=" + projectId + "&maintain_type=READONLY&function_usage=QUERY&function_code=CSH002F2QNEW";
            //放款申请提交发送通知给黄蕾、李宁、任江舟
            String[] sendUsers = {"huanglei", "li.ning", "renjiangzhou"};
            for (String userName : sendUsers) {
                Map<String, Object> paramsEvent = new HashMap<>();
                SysUser user = new SysUser();
                user = sysUserMapper.queryUserByUserName(userName);
                if (user != null) {
                    iRequest.setUserId(user.getUserId());
                    paramsEvent.put("message", msg);
                    paramsEvent.put("noticeTitle", "放款申请审批流程");
                    paramsEvent.put("noticeType", "NOTICE");
                    paramsEvent.put("url", url);
                    paramsEvent.put("level", 1L);
                    sysEventService.eventSave(iRequest, cshPaymentReqHd.getPaymentReqId(), cshPaymentReqHd.getDocumentCategory(), cshPaymentReqHd.getDocumentType(), "BAC", "CON_PAYMENT_WFL", "P2D", paramsEvent);
                }
            }
            iRequest.setUserId(originalUserId);

            cshPaymentReqHd.setPaymentReqStatus("APPROVING");
            cshPaymentReqHd.setPaymentReqDate(new Date());
            //综合成本率 当支付表上存在时去第一个支付表的值付给自己
            if (cshPaymentReqHd.getComCostRate() == null) {
                HlsCusCshPaymentReqHd cusCshPaymentReqHd = hlsCusCshPaymentReqHdMapper.queryConComCostRate(cshPaymentReqHd.getPaymentReqId());
                cshPaymentReqHd.setComCostRate(cusCshPaymentReqHd.getComCostRate());
            }

            cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, cshPaymentReqHd);


            List<HlsCusCshPaymentReqHd> cusCshPaymentReqHdList = hlsCusCshPaymentReqHdMapper.queryContractIdByFundPlan(cshPaymentReqHd);

            if (cusCshPaymentReqHdList.size() == 1) {
                if (cusCshPaymentReqHdList.get(0).getContractId() != null) {
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(cusCshPaymentReqHdList.get(0).getContractId());
                    HlsCusConContract hlsCusConContractNew = this.selectByPrimaryKey(iRequest, hlsCusConContract);
                    if (!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                        hlsCusConContractNew.setContractStatus("PAYMENTING");
                    }
                    this.updateByPrimaryKeySelective(iRequest, hlsCusConContractNew);
                }
            }
        } else {
            throw new ResMessageException("该付款申请已经提交审批！");
        }


    }

    //刷新现金流的due_date
    @Override
    public void contractUpdateCashflowDueDate(IRequest iRequest, HlsCusConContract hlsCusConContract) throws IllegalArgumentException {
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(hlsCusConContract.getContractId());
        conContract = self().selectByPrimaryKey(iRequest, hlsCusConContract);
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(hlsCusConContract.getContractId());
//获取报价
        HlsCusPrjQuotation conQuotation = new HlsCusPrjQuotation();
        conQuotation.setSourceDocumentId(conContract.getContractId());
        conQuotation.setSourceDocumentCategory(SOURCE_DOCUMENT_CATEGORY);
        List<HlsCusPrjQuotation> conQuotationList = hlsCusPrjQuotationService.select(iRequest, conQuotation, 1, 999999);
        if (conQuotationList.size() != 1L) {
            throw new IllegalArgumentException("请检查报价数据");
        }
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowMapper.selectConContractCashflowDetail(cashflow);
        // 基准开始日期
        Date baseStartDate = hlsCusConContract.getFirstRentalPaymentDate();
        Date oldLeaseStartDate = conQuotationList.get(0).getFirstRentalPaymentDate();
        int diffMoth = baseStartDate.getYear() * 12 + baseStartDate.getMonth() - (oldLeaseStartDate.getYear() * 12 + oldLeaseStartDate.getMonth());
        //   int diffDay = baseStartDate.getDate() - oldLeaseStartDate.getDate();
        //    if (baseStartDate.compareTo(oldLeaseStartDate) != 0) {
        Calendar cc = Calendar.getInstance();
        for (int i = 0; i < cashflowList.size(); i++) {
            HlsCusConContractCashflow cashflowItem = cashflowList.get(i);
            Long times = cashflowItem.getTimes();
            cc.clear();
            if (cashflowList.get(i).getTimes().compareTo(0L) == 0) {
                cc.setTime(hlsCusConContract.getLeaseStartDate());
            } else if (cashflowList.get(i).getTimes().compareTo(1L) == 0 && cashflowList.get(i).getCfItem().compareTo(1L) == 0) {
                cc.setTime(hlsCusConContract.getFirstRentalPaymentDate());
            } else if (cashflowList.get(i).getTimes().compareTo(-1000L) == 0) {
                //-1000为其他应收付的期次
                cc.setTime(hlsCusConContract.getLeaseStartDate());
            } else {
                cc.setTime(cashflowList.get(i).getCalcDate());
                cc.set(Calendar.DAY_OF_MONTH, 1);
                cc.add(Calendar.MONTH, diffMoth);
                int day = cc.getActualMaximum(Calendar.DATE);
                if (hlsCusConContract.getPaymentDay().intValue() < day) {
                    day = hlsCusConContract.getPaymentDay().intValue();
                }
                cc.set(Calendar.DAY_OF_MONTH, day);
            }
            HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
            contractCashflow.setContractId(cashflowItem.getContractId());
            contractCashflow.setTimes(times);
            contractCashflow.setDueDate(cc.getTime());
            contractCashflow.setCalcDate(cc.getTime());
            contractCashflow.setCashflowId(cashflowItem.getCashflowId());
            hlsCusConContractCashflowMapper.updateDueDate(contractCashflow);

        }
        //  }
        conContract.setInceptionOfLease(hlsCusConContract.getLeaseStartDate());
        conContract.setLeaseStartDate(hlsCusConContract.getLeaseStartDate());
        self().updateByPrimaryKeySelective(iRequest, conContract);
        if (conQuotationList.size() == 1) {
            conQuotation = conQuotationList.get(0);
            conQuotation.setLeaseStartDate(hlsCusConContract.getLeaseStartDate());
            conQuotation.setFirstRentalPaymentDate(hlsCusConContract.getFirstRentalPaymentDate());
            conQuotation.setPaymentDay(hlsCusConContract.getPaymentDay());
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, conQuotation);
        }
        try {
            calcRent(iRequest, hlsCusConContract);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // calcRent(iRequest, hlsCusConContract);
        //跳工作日
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setContractId(hlsCusConContract.getContractId());
        List<HlsCusConContractCashflow> cashflowLists = hlsCusConContractCashflowMapper.select(contractCashflow);

        for (HlsCusConContractCashflow dto : cashflowLists) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dto.getDueDate());
            if (dto.getTimes() != 0L) {
                //如果支付日期为周六，那么让当前日期-1天
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    dto.setDueDate(calendar.getTime());
                }
                //如果是周日，那么让当前日期-2天
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, -2);
                    dto.setDueDate(calendar.getTime());
                }
                //更新到数据库中
                hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(dto);
            }
        }
    }

    @Override
    public void conInceptSubmit(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException {
        List<HlsCusConContract> hlsCusConContractList = new ArrayList<>();
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(hlsCusConContract.getContractId());
        Date leaseDateAdjust = null;
        if (hlsCusConContract.getLeaseDateAdjust() != null) {
            leaseDateAdjust = hlsCusConContract.getLeaseDateAdjust();
        }


        hlsCusConContract.setContractId(hlsCusConContract.getContractId());
        hlsCusConContract = self().selectByPrimaryKey(iRequest, hlsCusConContract);
        hlsCusConContract.setLeaseDateAdjust(leaseDateAdjust);
        hlsCusConContractList.add(hlsCusConContract);
        if ("APPROVING".equalsIgnoreCase(hlsCusConContract.getInceptWflStatus())) {
            throw new HlsCusException("当前合同已在起租日调整流程中！");
        }

        databaseLockProvider.lock(hlsCusConContract);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (Objects.isNull(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CON_CONTRACT_INCEPT_WFL");
        activitiStartService.start(iRequest, hlsCusConContractList, params);

        hlsCusConContract.setInceptWflStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, hlsCusConContract);
    }

    @Override
    public void conInceptSave(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException {

        HlsCusConContract cusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContract);
        hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(hlsCusConContract.getContractId());

        HlsCusPrjQuotation cusPrjQuotation = hlsCusPrjQuotationMapper.queryQuotationByConId(hlsCusPrjQuotation);
//        cusPrjQuotation.setLeaseStartDate(hlsCusConContract.getLeaseStartDate());
        cusPrjQuotation.setLeaseDateAdjust(hlsCusConContract.getLeaseDateAdjust());
        hlsCusPrjQuotationMapper.updateByPrimaryKey(cusPrjQuotation);

    }

    @Override
    public HlsCusConContract selectByProjectId(Long projectId) {
        return hlsCusConContractMapper.selectByProjectId(projectId);
    }

    @Override
    public Integer updateByProjectId(HlsCusConContract hlsCusConContract) {
        return hlsCusConContractMapper.updateByProjectId(hlsCusConContract);
    }


    /**
     * 租赁合同结束提交事件
     *
     * @param requestCtx
     * @return
     */
    @Override
    public HlsCusConContract conContractEndSubmitWfl(IRequest requestCtx, HlsCusContractTermination hlsCusContractTermination) throws HlsCusException {

        Long contractId = hlsCusContractTermination.getContractId();
        HlsCusConContract t = new HlsCusConContract();
        t.setContractId(contractId);
        HlsCusConContract hlsCusConContract = self().selectByPrimaryKey(requestCtx, t);

        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(hlsCusConContract.getContractId());
        //检查是否满足提交条件
        List<HlsCusConContractCashflow> list = hlsCusConContractCashflowMapper.select(hlsCusConContractCashflow);
        for (HlsCusConContractCashflow cashflow : list) {
            if (!"FULL".equals(cashflow.getWriteOffFlag().toUpperCase())) {
                throw new HlsCusException("请完全核销现金流后提交合同结束申请！");
            }
        }
        //如果满足条件，启动工作流
        HlsEmployee employee = employeeMapper.getEmployeeCode(requestCtx.getUserId());
        String employeeCode = employee.getEmployeeCode();
        requestCtx.setEmployeeCode(employeeCode);

        List<HlsCusConContract> hlsCusFctContractListParam = new ArrayList<>();
        hlsCusFctContractListParam.add(hlsCusConContract);
        databaseLockProvider.lock(hlsCusConContract);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        //此次启动的工作流的唯一标识
        params.put("workFlowType", "CON_CONTRACT_END_WFL");


        params.put("contractTerminationId", hlsCusContractTermination.getContractTerminationId());
        Map<String, Object> evenParams = new HashMap<>();
        activitiStartService.start(requestCtx, hlsCusFctContractListParam, params);


        hlsCusContractTermination.setStatus("APPROVING");
        hlsCusContractTerminationService.updateByPrimaryKeySelective(requestCtx, hlsCusContractTermination);
        return hlsCusConContract;
    }

    //手续费分摊
    public void calcConLeaseChargeFinIncome(IRequest iRequest, HlsCusConContract conContract) {
        Long contractId = conContract.getContractId();
        Double rate = getRate(iRequest, CF_ITEM_LEASE_CHARGE, conContract.getBusinessType());
        Double finIncomeSum = 0D;
        Double finIncomeIncludSum = 0D;
        Double finIncomeVatSum = 0D;
        Double totalNetInterest = 0D;
        // totalNetInterest = totalNetInterest + finIncome;
        //手续费总额
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(contractId);
        hlsCusConContractCashflow.setCfItem(CF_ITEM_LEASE_CHARGE);
        Double leaseCharge = hlsCusConContractCashflowMapper.queryCashflowTotalAmount(hlsCusConContractCashflow);//含税金额

        Double netLeaseCharge = (double) Math.round(leaseCharge / (1 + rate) * 100) / 100;//不含税金额
        Double leaseChargeVat = CalculateUtil.sub(leaseCharge, netLeaseCharge);//税额
        //未确认分摊收益
        HlsCusContractFinanceIncome contractFinanceIncomeTmp = new HlsCusContractFinanceIncome();
        contractFinanceIncomeTmp.setContractId(contractId);
        contractFinanceIncomeTmp.setPostFlag("N");
        contractFinanceIncomeTmp.setCfItem(CF_ITEM_DUE_AMOUNT);
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = contractFinanceIncomeService.select(iRequest, contractFinanceIncomeTmp, 1, 999999);
        for (HlsCusContractFinanceIncome dto : contractFinanceIncomeList) {
            totalNetInterest = totalNetInterest + dto.getFinanceIncome();
        }
        for (int i = 0; i < contractFinanceIncomeList.size(); i++) {
            Double finIncome;
            Double finIncomeInclud;
            Double finIncomeVat;
            HlsCusContractFinanceIncome hlsCusContractFinance = new HlsCusContractFinanceIncome();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(contractFinanceIncomeList.get(i));
            hlsBeanRefUtilService.setFieldValue(hlsCusContractFinance, mapCsh);
            hlsCusContractFinance.setCfItem(CF_ITEM_LEASE_CHARGE);
            //最后一期倒减
            if (i == contractFinanceIncomeList.size() - 1) {
                hlsCusContractFinance.setFinanceIncomeInclud(leaseCharge - finIncomeIncludSum);
                hlsCusContractFinance.setFinanceIncome(netLeaseCharge - finIncomeSum);
                hlsCusContractFinance.setFinanceIncomeVat(leaseChargeVat - finIncomeVatSum);
            } else {
                finIncome = (double) Math.round(contractFinanceIncomeList.get(i).getFinanceIncome() / totalNetInterest * netLeaseCharge * 100) / 100;
                finIncomeSum = CalculateUtil.add(finIncomeSum, finIncome);
                finIncomeInclud = (double) Math.round(finIncome * (1 + rate) * 100) / 100;
                finIncomeIncludSum = CalculateUtil.add(finIncomeIncludSum, finIncomeInclud);
                finIncomeVat = CalculateUtil.sub(finIncomeInclud, finIncome);
                finIncomeVatSum = CalculateUtil.add(finIncomeVatSum, finIncomeVat);
                hlsCusContractFinance.setFinanceIncomeInclud(finIncomeInclud);
                hlsCusContractFinance.setFinanceIncome(finIncome);
                hlsCusContractFinance.setFinanceIncomeVat(finIncomeVat);
            }
            HlsCusContractFinanceIncome hlsCusContractFinance1 = new HlsCusContractFinanceIncome();
            hlsCusContractFinance1.setStartDate(contractFinanceIncomeList.get(i).getStartDate());
            hlsCusContractFinance1.setEndDate(contractFinanceIncomeList.get(i).getEndDate());
            hlsCusContractFinance1.setCfItem(3L);
            hlsCusContractFinance1.setPeriodName(contractFinanceIncomeList.get(0).getPeriodName());
            List<HlsCusContractFinanceIncome> contractFinanceIncomeExistsList = contractFinanceIncomeService.select(iRequest, hlsCusContractFinance1, 1, 999999);
            if (contractFinanceIncomeExistsList.size() == 0) {
                contractFinanceIncomeService.insertSelective(iRequest, hlsCusContractFinance);
            }

        }
    }

    private Double getRate(IRequest request, long cfitem, String invoiceProfile) {
        HlsInvoiceProfileDtl hlsInvoiceProfileDtl = new HlsInvoiceProfileDtl();
        hlsInvoiceProfileDtl.setCfItem(cfitem);
        hlsInvoiceProfileDtl.setInvoiceProfile("BACK_LEASE");
        List<HlsInvoiceProfileDtl> hlsInvoiceProfileDtlList = hlsInvoiceProfileDtlService.select(request, hlsInvoiceProfileDtl, 1, 99999);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(hlsInvoiceProfileDtlList)) {
            throw new IllegalArgumentException("找不到利息对应的税率!");
        }
        // 获取税率
        FndSysCodes fndSysCodes = new FndSysCodes();
        fndSysCodes.setTaxTypeCode(hlsInvoiceProfileDtlList.get(0).getTaxTypeCode());
        fndSysCodes.setCompanyId(request.getCompanyId());
        List<FndSysCodes> fndSysCodesList = fndSysCodesService.select(request, fndSysCodes, 1, 99999);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(fndSysCodesList)) {
            throw new IllegalArgumentException("找不到利息对应的税率!");
        }
        fndSysCodes = fndSysCodesList.get(0);
        return fndSysCodes.getTaxTypeRate();
    }

    @Override
    public HlsCusConContract conContractChangeSave(IRequest request, HlsCusContractPkg hlsCusContractPkg) throws HlsCusException {
        HlsCusConContract dto = hlsCusContractPkg.getHlsCusConContract();
        Long contractId = dto.getContractId();
        Long projectId = dto.getProjectId();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(dto.getContractId());
        conContract = self().selectByPrimaryKey(request, conContract);
        HlsCusConContractCashflow normalConContractCashflow = new HlsCusConContractCashflow();
        normalConContractCashflow.setContractId(contractId);
        normalConContractCashflow.setCfItem(CF_ITEM_DUE_AMOUNT);
        normalConContractCashflow.setSortorder("asc");
        normalConContractCashflow.setSortname("times");
        Double residualInterest = 0D;
        Double residualPrincipal = 0D;
        if (dto.getPrepaymentMethod().equals("CALCULATOR_TO_SETTLED_DATE")) {
            normalConContractCashflow.setWriteOffFlag("FULL");
            List<HlsCusConContractCashflow> normalConContractCashflowList = hlsCusConContractCashflowService.select(request, normalConContractCashflow, 1, 99999999);
            if (normalConContractCashflowList.size() == 0) {
                normalConContractCashflow.setCfItem(CF_ITEM_LEASE_ITEM);
                normalConContractCashflow.setTimes(0L);
                normalConContractCashflow.setWriteOffFlag(null);
                List<HlsCusConContractCashflow> normalConContractCashflowList1 = hlsCusConContractCashflowService.select(request, normalConContractCashflow, 1, 99999999);
                Long calcDays = getCalcDays(normalConContractCashflowList1.get(0).getDueDate(), dto.getEarlyTerminationDate());
                residualInterest = (double) Math.round(normalConContractCashflowList1.get(0).getOutstandingPrincipal() * conContract.getIntRate() * calcDays / 360 * 100) / 100;

            } else {
                for (int i = 0; i < normalConContractCashflowList.size(); i++) {
                    if (i + 1 == normalConContractCashflowList.size()) {
                        Long calcDays = getCalcDays(normalConContractCashflowList.get(i).getDueDate(), dto.getEarlyTerminationDate());
                        residualInterest = (double) Math.round(normalConContractCashflowList.get(i - 1).getOutstandingPrincipal() * conContract.getIntRate() * calcDays / 360 * 100) / 100;
                    }
                }
            }

        } else {
            normalConContractCashflow.setWriteOffFlag("NOT");
            List<HlsCusConContractCashflow> normalConContractCashflowList = hlsCusConContractCashflowService.select(request, normalConContractCashflow, 1, 99999999);

            for (int i = 0; i < normalConContractCashflowList.size(); i++) {
                residualInterest = residualInterest + normalConContractCashflowList.get(i).getInterest();
            }
        }
        normalConContractCashflow.setWriteOffFlag("NOT");
        List<HlsCusConContractCashflow> normalConContractCashflowPrincipalList = hlsCusConContractCashflowService.select(request, normalConContractCashflow, 1, 99999999);
        for (HlsCusConContractCashflow contractCashflow : normalConContractCashflowPrincipalList) {
            residualPrincipal = CalculateUtil.add(residualPrincipal, contractCashflow.getPrincipal());

        }
        dto.setResidualPrincipal(residualPrincipal);
        dto.setResidualInterest(residualInterest);
        self().updateByPrimaryKeySelective(request, dto);
        //附件
        List<HlsCusContractAttachment> hlsCusContractAttachmentList = hlsCusContractPkg.getHlsCusContractAttachmentList();
        if (hlsCusContractAttachmentList != null) {
            for (HlsCusContractAttachment dt : hlsCusContractAttachmentList) {
                if (dt.getContractAttachmentId() == null || dt.getContractAttachmentId() == 0) {
                    dt.set__status("add");
                    dt.setContractId(contractId);
                    dt.setContractAttachmentCategory(dt.getContractAttachmentCategory());
                    dt.setSourceType(dt.getSourceType());
                    hlsCusContractAttachmentService.insertSelective(request, dt);
                } else {
                    dt.set__status("update");
                    dt.setContractId(contractId);
                    hlsCusContractAttachmentService.updateByPrimaryKeySelective(request, dt);
                }
            }
        }

        return dto;
    }

    /**
     * 获取两个日期之间相差天数
     *
     * @param calcStartDate
     * @param calcEndDate
     * @author Yenick
     */

    private Long getCalcDays(Date calcStartDate, Date calcEndDate) {
        Long result;
        Calendar calcStart = Calendar.getInstance();
        Calendar calcEnd = Calendar.getInstance();
        calcStart.setTime(calcStartDate);
        calcEnd.setTime(calcEndDate);

        result = (calcEnd.getTimeInMillis() - calcStart.getTimeInMillis()) / (1000 * 3600 * 24);

        return result;
    }

    /**
     * 获取两个日期之间相差天数
     *
     * @param calcStartDate
     * @param calcEndDate
     * @author Yenick
     */

    private Long getCalcDays2(Date calcStartDate, Date calcEndDate) {
        Long result;
        Calendar calcStart = Calendar.getInstance();
        Calendar calcEnd = Calendar.getInstance();
        calcStart.setTime(calcStartDate);
        calcEnd.setTime(calcEndDate);

        result = (calcEnd.getTimeInMillis() - calcStart.getTimeInMillis()) / (1000 * 3600 * 24) + 1;

        return result;
    }

    @Override
    public void contractGenerate(IRequest request, HlsCusPrjProject dto) {
        Long projectId = dto.getProjectId();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjProject);
        hlsBeanRefUtilService.setFieldValue(hlsCusConContract, map);
        hlsCusConContract.setProjectId(dto.getProjectId());
        hlsCusConContract.set__status("add");
        hlsCusConContract.setDataClass("NORMAL");
        hlsCusConContract.setContractStatus("NEW");
        hlsCusConContract.setDocumentCategory("CON_CONTRACT");
        StringBuffer sub = new StringBuffer();
        sub.append(hlsCusPrjProject.getBusinessType());
        sub.append("_CON");
        hlsCusConContract.setDocumentType(sub.toString());
        hlsCusConContract.setAbsShowFlag("C");
        hlsCusConContract.setPenaltyProfile("STD");
        hlsCusConContract.setWriteOffAbleFlag("N");//设定未
        hlsCusConContract.setBillingProfile("CON");
        hlsCusConContract = self().insertSelective(request, hlsCusConContract);
        hlsCusActMeetingRiskListService.insertConPaymentPt(request, hlsCusConContract);
        Long contractId = hlsCusConContract.getContractId();
//复制项目报价到合同层
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(dto.getProjectId());
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotation.setDataClass("VIRTUAL_CON");
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(request, hlsCusPrjQuotation, 1, 99999);
        hlsCusPrjQuotation = hlsCusPrjQuotationList.get(0);
        hlsCusPrjQuotation.setGeneratedStatus("GENERATED");
        hlsCusPrjQuotation = hlsCusPrjQuotationService.updateByPrimaryKey(request, hlsCusPrjQuotation);
        hlsCusPrjQuotation.setSelectedFlag("N");
        hlsCusPrjQuotation = hlsCusPrjQuotationService.updateByPrimaryKey(request, hlsCusPrjQuotation);
        HlsCusPrjQuotation hlsCusConQuotation = new HlsCusPrjQuotation();
        Map<String, String> contractmMap = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotation);
        hlsBeanRefUtilService.setFieldValue(hlsCusConQuotation, contractmMap);
        hlsCusConQuotation.setSourceDocumentId(contractId);
        hlsCusConQuotation.setSourceDocumentCategory("CON_CONTRACT");
        hlsCusConQuotation = hlsCusPrjQuotationService.insertSelective(request, hlsCusConQuotation);

        //回写合同
        hlsCusConContract.setQuotationId(hlsCusConQuotation.getQuotationId());
        hlsCusConContract.setLeaseTimes(hlsCusConQuotation.getLeaseTimes());//租赁期数
        hlsCusConContract.setLeaseTerm(hlsCusConQuotation.getLeaseTerm());//租赁期限
        hlsCusConContract.setCurrency("CNY");//币种
        hlsCusConContract.setInterestYearDays(hlsCusPrjQuotation.getInterestYearDays());//年计息天数
        hlsCusConContract.setBillingProfile(hlsCusPrjQuotation.getBillingProfile());//开票规则
        hlsCusConContract.setEstimateRentingDate(hlsCusConQuotation.getEstimateRentingDate());
        hlsCusConContract.setPriceList(hlsCusPrjQuotation.getPriceList());
        hlsCusConContract.setVatRate(hlsCusPrjQuotation.getVatRate());//稅率
        // hlsCusConContract.setInceptionOfLease(hlsCusConQuotation.getEstimateRentingDate());
        hlsCusConContract.setRentingFrequency(hlsCusConQuotation.getRentingFrequency().toString());
        hlsCusConContract.setTotalRental(hlsCusConQuotation.getTotalRental());//租赁总价款
        hlsCusConContract.setNetDownPayment(hlsCusConQuotation.getNetDownPayment());//不含税首付金额
        hlsCusConContract.setVatDownPayment(hlsCusConQuotation.getVatDownPayment());//首付款税额
        hlsCusConContract.setFinanceAmount(hlsCusConQuotation.getFinanceAmount());//融资额
        hlsCusConContract.setNetFinanceAmount(hlsCusConQuotation.getNetFinanceAmount());//不含税融资额
        hlsCusConContract.setVatFinanceAmount(hlsCusConQuotation.getVatFinanceAmount());//融资额增值税
        hlsCusConContract.setNetTotalRental(hlsCusConQuotation.getNetTotalRental());//不含税租金
        hlsCusConContract.setVatTotalRental(hlsCusConQuotation.getVatTotalRental());//租金增值税额
        hlsCusConContract.setTotalInterest(hlsCusConQuotation.getTotalInterest());//利息总额
        hlsCusConContract.setNetTotalInterest(hlsCusConQuotation.getNetTotalInterest());//不含税利息
        hlsCusConContract.setVatTotalInterest(hlsCusConQuotation.getVatTotalInterest());//利息增值税额
        hlsCusConContract.setLeaseChargeRatio(hlsCusConQuotation.getLeaseChargeRatio());//手续费比例
        hlsCusConContract.setLeaseCharge(hlsCusConQuotation.getLeaseCharge());//手续费
        hlsCusConContract.setNetLeaseCharge(hlsCusConQuotation.getNetLeaseCharge());//不含税手续费
        hlsCusConContract.setVatLeaseCharge(hlsCusConQuotation.getVatLeaseCharge());//手续费税额
        hlsCusConContract.setLeaseMgtFee(hlsCusConQuotation.getLeaseMgtFee());//管理费
        hlsCusConContract.setLeaseMgtFeeRatio(hlsCusConQuotation.getLeaseMgtFeeRatio());//管理费比例
        hlsCusConContract.setLeaseMgtFeeRule(hlsCusConQuotation.getLeaseMgtFeeRule());//管理费计算方式
        hlsCusConContract.setDeposit(hlsCusConQuotation.getDeposit());//保证金
        hlsCusConContract.setDepositRatio(hlsCusConQuotation.getDepositRatio());//保证金比例
        hlsCusConContract.setDepositDeduction(hlsCusPrjQuotation.getDepositReturnMethod());//保证金抵扣方式
        hlsCusConContract.setResidualValue(hlsCusConQuotation.getResidualValue());//留购价款
        hlsCusConContract.setResidualRatio(hlsCusConQuotation.getResidualRatio());//留购价款比例
        hlsCusConContract.setNetResidualValue(hlsCusConQuotation.getNetResidualValue());//不含税留购金
        hlsCusConContract.setBalloon(hlsCusConQuotation.getBalloon());//末期租金
        hlsCusConContract.setBalloonRatio(hlsCusConQuotation.getBalloonRatio());//末期租金比例
        hlsCusConContract.setNetBalloon(hlsCusConQuotation.getNetBalloon());//不含税末期租金
        hlsCusConContract.setVatBalloon(hlsCusConQuotation.getVatBalloon());//末期租金税额
        hlsCusConContract.setBaseRateType(hlsCusConQuotation.getBaseRateType());// 基准利率类别
        hlsCusConContract.setBaseRate(hlsCusConQuotation.getBaseRate());//基准利率
        hlsCusConContract.setIntRateType(hlsCusConQuotation.getIntRateType());//租赁利率类型
        hlsCusConContract.setIntRate(hlsCusConQuotation.getIntRate());//租赁利率
        hlsCusConContract.setIrr(hlsCusConQuotation.getIrr());//内部收益率
        hlsCusConContract.setIrrAfterTax(hlsCusConQuotation.getIrrAfterTax());//税后内部收益率
        //conContract.setPenaltyProfile(hlsCusConQuotation.getPenaltyProfile());//迟延履行金规则
        hlsCusConContract.setFloatingWay(hlsCusConQuotation.getFloatingWay());//浮动方式
        hlsCusConContract.setFloatingWayRate(hlsCusConQuotation.getFloatingWayRate());
        hlsCusConContract.setAnnualPayTimes(getRentingFrequency(hlsCusConContract.getRentingFrequency()));//还租频率
        hlsCusConContract.setVatInput(hlsCusPrjQuotation.getVatInput());//进项税额
        hlsCusConContract.setVatInputTaxTypeRate(hlsCusPrjQuotation.getVatInputTaxTypeRate());//进项税率
        hlsCusConContract.setLeaseItemAmount(hlsCusPrjQuotation.getLeaseItemAmount());//租赁物总价款
        //hlsCusConContract.setBaseRateType("PBOC");
        hlsCusConContract.setReceivedStatus("NOT");
        hlsCusConContract.setWriteOffAbleFlag("Y");
        hlsCusConContract.setFloatingRangeMethod(hlsCusPrjProject.getFloatingRangeMethod());//调息范围规则
        hlsCusConContract.setDataClass("NORMAL");
        hlsCusConContract.setInceptionOfLease(null);

        hlsCusConContract = self().updateByPrimaryKey(request, hlsCusConContract);
        /*复制现金流表*/
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowService.select(request, hlsCusPrjQuotationCashflow, 1, 9999999);
        for (HlsCusPrjQuotationCashflow dt : hlsCusPrjQuotationCashflowList) {
            HlsCusPrjQuotationCashflow hlsCusConQuotationCashflow = new HlsCusPrjQuotationCashflow();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusConQuotationCashflow, mapCsh);
            hlsCusConQuotationCashflow.setQuotationId(hlsCusConQuotation.getQuotationId());
            hlsCusConQuotationCashflow.setQuotationCashflowId(null);
            hlsCusPrjQuotationCashflowService.insertSelective(request, hlsCusConQuotationCashflow);
        }
        //Long cashflowId = 0L;
        for (HlsCusPrjQuotationCashflow dt : hlsCusPrjQuotationCashflowList) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusConContractCashflow, mapCsh);
            hlsCusConContractCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            hlsCusConContractCashflow.setContractId(hlsCusConContract.getContractId());
            hlsCusConContractCashflow.setWriteOffFlag("NOT");
            hlsCusConContractCashflowService.insertSelective(request, hlsCusConContractCashflow);
        }
    }

    /**
     * 直租回租付款频率
     */
    @Override
    public Long getRentingFrequency(String rentingFrequency) {
        Long rf = 1L;
        if (org.apache.commons.lang3.StringUtils.equals(MONTH, rentingFrequency)) {
            rf = 12L;
        } else if (org.apache.commons.lang3.StringUtils.equals(QUARTER, rentingFrequency)) {
            rf = 4L;
        } else if (org.apache.commons.lang3.StringUtils.equals(HALF_A_YEAR, rentingFrequency)) {
            rf = 2L;
        } else if (org.apache.commons.lang3.StringUtils.equals(YEAR, rentingFrequency)) {
            rf = 1L;
        } else if (org.apache.commons.lang3.StringUtils.equals(DOUBLE_MONTH, rentingFrequency)) {
            rf = 6L;
        }
        return rf;
    }

    /**
     * 经租付款频率
     */
    public Long getOperatingLeaseRentingFrequency(String rentingFrequency) {
        Long rf = 1L;
        if (org.apache.commons.lang3.StringUtils.equals(MONTH, rentingFrequency)) {
            rf = 1L;
        } else if (org.apache.commons.lang3.StringUtils.equals(QUARTER, rentingFrequency)) {
            rf = 3L;
        } else if (org.apache.commons.lang3.StringUtils.equals(HALF_A_YEAR, rentingFrequency)) {
            rf = 6L;
        } else if (org.apache.commons.lang3.StringUtils.equals(YEAR, rentingFrequency)) {
            rf = 12L;
        } else if (org.apache.commons.lang3.StringUtils.equals(DOUBLE_MONTH, rentingFrequency)) {
            rf = 2L;
        }
        return rf;
    }

    public String getCodeValue(IRequest requestContext) {
        Map<String, String> params = new HashMap<String, String>();
        String value = codingRuleValuesService.getCodeRuleValue(requestContext, "CSH_PAYMENT_REQ", "PAYMENT_REQ", "PAYMENT_REQ", params);
        return value;
    }


    /**
     * 获取日期所在月份的月末
     *
     * @param calcDate
     * @author zhangyu
     */
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


    @Override
    public HlsCusConContract conInceptSave(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws HlsCusException {
        //项目信息
        HlsCusPrjProject prjProject = hlsCusPrjProjectInfo.getHlsCusPrjProject();
        //报价信息
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();

        hlsCusPrjQuotation.setQuotationId(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getQuotationId());
        hlsCusPrjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, hlsCusPrjQuotation);
        hlsCusPrjQuotation.setLeaseStartDate(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getLeaseStartDate());
        hlsCusPrjQuotation.setAccountLeaseDate(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getAccountLeaseDate());
        hlsCusPrjQuotation.setObjectVersionNumber(hlsCusPrjQuotation.getObjectVersionNumber());
        hlsCusPrjQuotationService.updateByPrimaryKey(iRequest, hlsCusPrjQuotation);
        //合同信息
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(prjProject.getContractId());
        hlsCusConContract = self().selectByPrimaryKey(iRequest, hlsCusConContract);
        hlsCusConContract.setInceptionOfLease(hlsCusPrjQuotation.getLeaseStartDate());
        hlsCusConContract.setLeaseStartDate(hlsCusPrjQuotation.getLeaseStartDate());
        self().updateByPrimaryKeySelective(iRequest, hlsCusConContract);
        HlsCusPrjQuotation conQuotation = new HlsCusPrjQuotation();
        conQuotation.setSourceDocumentId(hlsCusConContract.getContractId());
        conQuotation.setSourceDocumentCategory(SOURCE_DOCUMENT_CATEGORY);
        List<HlsCusPrjQuotation> conQuotationList = hlsCusPrjQuotationService.select(iRequest, conQuotation, 1, 999999);
        if (conQuotationList.size() != 1L) {
            throw new IllegalArgumentException("请检查报价数据");
        }
        conQuotation = conQuotationList.get(0);
        conQuotation.setLeaseStartDate(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getLeaseStartDate());
        conQuotation.setFirstRentalPaymentDate(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getFirstRentalPaymentDate());
        conQuotation.setPaymentDay(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getPaymentDay());
        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, conQuotation);

        List<HlsCusPrjProjectInsure> hlsCusPrjProjectInsureList = new ArrayList<>();
        hlsCusPrjProjectInsureList = hlsCusPrjProjectInfo.getHlsCusPrjProjectInsureList();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(hlsCusPrjProjectInsureList)) {
            for (HlsCusPrjProjectInsure dt : hlsCusPrjProjectInsureList) {
                if (dt.getInsureId() == null || dt.getInsureId() == 0) {
                    dt.setProjectId(prjProject.getProjectId());
                    dt.set__status(DTOStatus.ADD);
                    hlsCusPrjProjectInsureService.insertSelective(iRequest, dt);
                } else {
                    dt.set__status(DTOStatus.UPDATE);
                    dt.setProjectId(prjProject.getProjectId());
                    hlsCusPrjProjectInsureService.updateByPrimaryKey(iRequest, dt);
                }
            }
        }
        //租前息
        List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHList = new ArrayList<>();
        hlsCusConContractBeforeRentHList = hlsCusPrjProjectInfo.getHlsCusConContractBeforeRentHList();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(hlsCusConContractBeforeRentHList)) {
            for (HlsCusConContractBeforeRentH dt : hlsCusConContractBeforeRentHList) {
                if (dt.getRentId() == null || dt.getRentId() == 0) {
                    dt.setProjectId(prjProject.getProjectId());
                    dt.setCompanyId(iRequest.getCompanyId());
                    dt.set__status(DTOStatus.ADD);
                    hlsCusConContractBeforeRentHService.insertSelective(iRequest, dt);
                } else {
                    dt.set__status(DTOStatus.UPDATE);
                    dt.setProjectId(prjProject.getProjectId());
                    hlsCusConContractBeforeRentHService.updateByPrimaryKey(iRequest, dt);
                }
            }
        }
        return hlsCusConContract;
    }

    @Override
    public HlsCusPrjProject conContractChangeSubmit(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws HlsCusException {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject = hlsCusPrjProjectService.prjProjectSave(iRequest, hlsCusPrjProjectInfo);
//        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
//        hlsCusPrjProjectList.add(prjProject);
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(prjProject);
        databaseLockProvider.lock(prjProject);
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (Objects.isNull(employee)) {
            throw new HlsCusException("获取提交人失败");
        }

        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        if (hlsCusChangeReqInfo.getBusinessType().equals("BEFORELAUNCH")) {
            params.put("workFlowType", "PRJ_CON_ALTER");
        } else {
            params.put("workFlowType", "PRJ_CONTEXT_ALTER");
        }

        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);
        //修改变更状态

        hlsCusChangeReqInfo.setStatus("APPROVING");
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(iRequest, hlsCusChangeReqInfo);
        return prjProject;
    }

    //计算租前息
    public void calcRent(IRequest iRequest, HlsCusConContract hlsCusConContract) throws IllegalArgumentException {
        hlsCusConContract = self().selectByPrimaryKey(iRequest, hlsCusConContract);
        //删除未核销的租前息现金流
        hlsCusConContractCashflowMapper.deleteNotWriteOffRent(hlsCusConContract.getContractId());
        //删除起租日后的租前息数据
        HlsCusConContractBeforeRentH hlsCusConContractBeforeRentH = new HlsCusConContractBeforeRentH();
        hlsCusConContractBeforeRentH.setProjectId(hlsCusConContract.getProjectId());
        hlsCusConContractBeforeRentH.setInceptOfLease(hlsCusConContract.getLeaseStartDate());
        hlsCusConContractBeforeRentHMapper.deleteRent(hlsCusConContractBeforeRentH);
        hlsCusConContractBeforeRentH.setSortname("receivable_date");
        hlsCusConContractBeforeRentH.setSortorder("asc");
        List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHList = hlsCusConContractBeforeRentHService.select(iRequest, hlsCusConContractBeforeRentH, 1, 9999999);
        //合同向下的付款行明细
        HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
        hlsCusCshPaymentReqLn.setSourceDocId(hlsCusConContract.getContractId());
        hlsCusCshPaymentReqLn.setSortname("payment_completed_date");
        hlsCusCshPaymentReqLn.setSortorder("asc");
        List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = hlsCusCshPaymentReqLnMapper.select(hlsCusCshPaymentReqLn);
        if (hlsCusCshPaymentReqLnList.size() > 0) {
            Double preDueAmount = 0D;
            Long day = 0L;
            Long times = 0L;
            Date minPaymentDate = hlsCusCshPaymentReqLnList.get(0).getPaymentCompletedDate();
            for (int i = 0; i < hlsCusConContractBeforeRentHList.size(); i++) {
                Double dueAmount = 0D;
                HlsCusCshPaymentReqLn hlsCusCshPaymentReqLnTemp = new HlsCusCshPaymentReqLn();
                hlsCusCshPaymentReqLnTemp.setSourceDocId(hlsCusConContract.getContractId());
                if (i == 0) {
                    hlsCusCshPaymentReqLnTemp.setRentStartDate(minPaymentDate);
                    hlsCusCshPaymentReqLnTemp.setRentEndDate(hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                    List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnTempList = hlsCusCshPaymentReqLnMapper.selectCshPaymentReqLnWithStartEndDate(hlsCusCshPaymentReqLnTemp);
                    if (hlsCusCshPaymentReqLnTempList.size() > 0) {
                        for (int k = 0; k < hlsCusCshPaymentReqLnTempList.size(); k++) {
                            day = getCalcDays(hlsCusCshPaymentReqLnTempList.get(k).getPaymentCompletedDate(), hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                            dueAmount = hlsCusConContract.getIntRate() * hlsCusCshPaymentReqLnTempList.get(k).getAmountPaid() * day / 360 + dueAmount;
                        }
                    }
                } else {
                    hlsCusCshPaymentReqLnTemp.setRentStartDate(hlsCusConContractBeforeRentHList.get(i - 1).getReceivableDate());
                    hlsCusCshPaymentReqLnTemp.setRentEndDate(hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                    hlsCusCshPaymentReqLnTemp.setPaymentCompletedDate(hlsCusConContractBeforeRentHList.get(i - 1).getReceivableDate());
                    List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnTempList = hlsCusCshPaymentReqLnMapper.selectCshPaymentReqLnWithStartEndDate(hlsCusCshPaymentReqLnTemp);
                    preDueAmount = hlsCusCshPaymentReqLnMapper.selectCshPaymentReqLnAmountPaid(hlsCusCshPaymentReqLnTemp);
                    if (preDueAmount == null || preDueAmount.compareTo(0D) == 0) {
                        continue;
                    }
                    if (hlsCusCshPaymentReqLnTempList.size() > 0) {
                        for (int k = 0; k < hlsCusCshPaymentReqLnTempList.size(); k++) {
                            day = getCalcDays(hlsCusCshPaymentReqLnTempList.get(k).getPaymentCompletedDate(), hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                            dueAmount = hlsCusConContract.getIntRate() * hlsCusCshPaymentReqLnTempList.get(k).getAmountPaid() * day / 360 + dueAmount;
                        }
                    }

                    if (preDueAmount > 0) {
                        day = getCalcDays(hlsCusConContractBeforeRentHList.get(i - 1).getReceivableDate(), hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                        dueAmount = dueAmount + (double) Math.round(preDueAmount * day * hlsCusConContract.getIntRate() / 360 * 100) / 100;
                    }
                }
                //插入现金流表con_contract_cashflow
                if (dueAmount.compareTo(0D) == 1) {
                    HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                    conContractCashflow.setContractId(hlsCusConContract.getContractId());
                    conContractCashflow.setCashflowId(null);
                    conContractCashflow.setQuotationId(hlsCusConContract.getQuotationId());
                    conContractCashflow.setTimes(times - (i + 1));
                    conContractCashflow.setCfItem(CF_ITEM_RENT);
                    conContractCashflow.setCfType(CF_TYPE_RENT);
                    conContractCashflow.setCfDirection("INFLOW");
                    conContractCashflow.setCfStatus("RELEASE");
                    conContractCashflow.setDueAmount(dueAmount);
                    conContractCashflow.setInterest(dueAmount);
                    conContractCashflow.setDueDate(hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                    conContractCashflow.setCalcDate(hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                    conContractCashflow.setFinIncomeDate(hlsCusConContractBeforeRentHList.get(i).getReceivableDate());
                    conContractCashflow.setNetDueAmount((double) Math.round(dueAmount / (1 + hlsCusConContract.getVatRate()) * 100) / 100);
                    conContractCashflow.setVatDueAmount(dueAmount - (double) Math.round(dueAmount / (1 + hlsCusConContract.getVatRate()) * 100) / 100);
                    conContractCashflow.setNetInterest((double) Math.round(dueAmount / (1 + hlsCusConContract.getVatRate()) * 100) / 100);
                    conContractCashflow.setVatInterest(dueAmount - (double) Math.round(dueAmount / (1 + hlsCusConContract.getVatRate()) * 100) / 100);

                    conContractCashflow.setWriteOffFlag("NOT");
                    conContractCashflow.setBillingStatus("NOT");
                    conContractCashflow.set__status(DTOStatus.ADD);
                    hlsCusConContractCashflowService.insertSelective(iRequest, conContractCashflow);
                }
            }
        }
    }


    /*
    1.删除结清日后的所有分摊结果
    2.结清日上前一期剩余本金算出 结清当期营收利息
    3.当期分摊额(含税)=已分摊利息-(结清日前利息+结清=当期利息)
     */
    @Override
    public void calcConFinIncomeSpecialGd(IRequest iRequest, HlsCusConContract conContract) {
        HlsCusContractFinanceIncome contractFinanceIncomeTmp = new HlsCusContractFinanceIncome();
        contractFinanceIncomeTmp.setContractId(conContract.getContractId());
        contractFinanceIncomeTmp.setPostFlag("N");
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = contractFinanceIncomeService.select(iRequest, contractFinanceIncomeTmp, 1, 999999);
        List<HlsCusContractFinanceIncome> contractFinanceIncomeDeleteList = new ArrayList<>();
        for (HlsCusContractFinanceIncome deleteDto : contractFinanceIncomeList) {
            if (deleteDto.getEndDate().compareTo(conContract.getEarlyTerminationDate()) == 1) {
                contractFinanceIncomeDeleteList.add(deleteDto);
            }
        }
        contractFinanceIncomeService.batchDelete(contractFinanceIncomeDeleteList);

        contractFinanceIncomeTmp.setSortorder("desc");
        contractFinanceIncomeTmp.setSortname("end_date");
        contractFinanceIncomeTmp.setCfItem(1L);
        List<HlsCusContractFinanceIncome> contractFinanceIncomeResultList = contractFinanceIncomeService.select(iRequest, contractFinanceIncomeTmp, 1, 999999);
        if (contractFinanceIncomeResultList.size() > 0) {

            Double totalInterest = 0D;
            Double financeIncomeIncludTemp = 0D;
            Long calcDays;
            Calendar cStart = Calendar.getInstance();
            Calendar cEnd = Calendar.getInstance();
            cStart.setTime(contractFinanceIncomeResultList.get(0).getEndDate());
            cStart.add(Calendar.DAY_OF_MONTH, 1);
            cEnd.setTime(conContract.getEarlyTerminationDate());
            cEnd.add(Calendar.DAY_OF_MONTH, -1);
            calcDays = getCalcDays2(cStart.getTime(), cEnd.getTime());
            //获取支付表项下所有租金还款计划
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId(conContract.getContractId());
            hlsCusConContractCashflow.setCfItem(CF_ITEM_DUE_AMOUNT);
            List<HlsCusConContractCashflow> conContractCashflowList = hlsCusConContractCashflowMapper.queryCashflowByCfItemOrderByDueDate(hlsCusConContractCashflow);
            if (conContractCashflowList.size() > 0) {
                Double outstandingPrincipal = conContractCashflowList.get(conContractCashflowList.size() - 1).getOutstandingPrincipal();
                Double dailyInterestRate = (double) Math.round(conContract.getIntRate() / 360 * Math.pow(10, 10)) / Math.pow(10, 10);
                Long calcDayFinanceInterest = getCalcDays2(conContractCashflowList.get(conContractCashflowList.size() - 1).getCalcDate(), cEnd.getTime());
                Double financeIncomeInterest = (double) Math.round(outstandingPrincipal * calcDayFinanceInterest * dailyInterestRate * 365 / 360 * Math.pow(10, 10)) / Math.pow(10, 10);

                for (HlsCusConContractCashflow contractCashflow : conContractCashflowList) {
                    totalInterest = totalInterest + contractCashflow.getInterest();
                }
                totalInterest = totalInterest + financeIncomeInterest;
            }

            for (HlsCusContractFinanceIncome hlsCusContractFinanceIncome : contractFinanceIncomeResultList) {
                financeIncomeIncludTemp = financeIncomeIncludTemp + hlsCusContractFinanceIncome.getFinanceIncomeInclud();
            }
            Double financeIncomeInclud = totalInterest - financeIncomeIncludTemp;
            Double financeIncome = (double) Math.round(financeIncomeInclud / (1 + conContract.getVatRate()) * 100) / 100;

            HlsCusContractFinanceIncome contractFinanceIncome = new HlsCusContractFinanceIncome();
            String periodName;
            if (cEnd.get(Calendar.MONTH) + 1 < 10) {
                periodName = cEnd.get(Calendar.YEAR) + "-0" + (cEnd.get(Calendar.MONTH) + 1);

            } else {
                periodName = cEnd.get(Calendar.YEAR) + "-" + (cEnd.get(Calendar.MONTH) + 1);
            }

            contractFinanceIncome.setPeriodName(periodName);
            contractFinanceIncome.setStartDate(cStart.getTime());
            contractFinanceIncome.setEndDate(cEnd.getTime());
            contractFinanceIncome.setDays(calcDays);
            contractFinanceIncome.setFinanceIncome(financeIncome);
            contractFinanceIncome.setFinanceIncomeInclud(financeIncomeInclud);
            contractFinanceIncome.setFinanceIncomeVat(financeIncomeInclud - financeIncome);
            contractFinanceIncome.setCfItem(1L);
            contractFinanceIncome.setPostFlag("N");
            contractFinanceIncome.setCompanyId(conContract.getCompanyId());
            contractFinanceIncome.setContractId(conContract.getContractId());
            contractFinanceIncome.setGldCashflowId(null);
            contractFinanceIncome.setSourceType("CON_CONTRACT");
            contractFinanceIncome.setFinanceIncomeId(null);
            HlsCusContractFinanceIncome contractFinanceIncomeExists = new HlsCusContractFinanceIncome();
            contractFinanceIncomeExists.setContractId(conContract.getCompanyId());
            contractFinanceIncomeExists.setCompanyId(conContract.getContractId());
            //   contractFinanceIncomeExists.setGldCashflowId(conContractCashflow.getCashflowId());
            contractFinanceIncomeExists.setStartDate(cStart.getTime());
            contractFinanceIncomeExists.setEndDate(cEnd.getTime());
            List<HlsCusContractFinanceIncome> contractFinanceIncomeExistsList = contractFinanceIncomeService.select(iRequest, contractFinanceIncomeExists, 1, 999999);
            if (contractFinanceIncomeExistsList.size() == 0) {
                contractFinanceIncomeService.insertSelective(iRequest, contractFinanceIncome);
            }

        }

    }

    @Override
    public List<HlsCusConContract> queryInsureContractList(IRequest iRequest, HlsCusConContract dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusConContractMapper.queryInsureContractList(dto);
    }

    @Autowired
    private HlsCusFundingPlanMapper hlsCusFundingPlanMapper;

    @Override
    public List<HlsCusConContract> createContractBatchByContract(IRequest iRequest, List<HlsCusConContract> list) throws Exception {

        if (list != null && list.size() > 0) {
            if (list.get(0).getProjectId() == null) {
                throw new HlsCusException("关键参数获取失败!");
            }

            //校验合同签署放款次数
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(list.get(0).getProjectId());
            hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
          /*  String contractSignTimes = hlsCusPrjProject.getContractSigningTimes();
            if(StringUtil.isEmpty(contractSignTimes)){
                throw new HlsCusException("请先维护合同签署放款次数!");
            }*/


            if (chekcContractLeaseItem(iRequest, list)) {
                for (HlsCusConContract contract : list) {
                    if (contract.getContractId() == null) {
                        createContractByProject(iRequest, contract);
                    } else {
                        hlsCusConContractMapper.updateByPrimaryKeySelective(contract);
                        //更新报价融资额
                        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
                        quotation.setSourceDocumentCategory("CON_CONTRACT");
                        quotation.setSourceDocumentId(contract.getContractId());
                        quotation = hlsCusPrjQuotationMapper.selectOne(quotation);
                        quotation.setFinanceAmount(contract.getFinanceAmount());
                        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, quotation);
                    }
                }
            } else {
                throw new HlsCusException("拆分金额总和超过当前合同的租赁物总价款！");
            }
        }
        return list;
    }

    @Override
    public String exitCheckBp(IRequest iRequest, List<HlsCusConContract> list) throws Exception {
        String flag = "true";

        if (list != null && list.size() > 0) {
            if (list.get(0).getProjectId() == null) {
                throw new HlsCusException("关键参数获取失败!");
            }

            for (HlsCusConContract contract : list) {
                if (contract.getContractId() != null) {
                    //查询放款计划的收款对象是否为空
                    Map<String, Object> resultMap = new HashMap();
                    resultMap.put("contractId", contract.getContractId());
                    List<Map> maps = hlsCusConContractCashflowMapper.selectContractCashflowInfo(resultMap);
                    if (maps.size() > 0) {
                        for (Map m : maps) {
                            if (m.get("bp_id") == null) {
                                flag = "false";
                            }
                        }
                    }
                }
            }

        }

        return flag;

    }

    @Override
    public void deleteContract(IRequest iRequest, List<HlsCusConContract> list) {
        for (HlsCusConContract contract : list) {
            if (contract.getContractId() != null) {
                //删除合同现金流
                hlsCusConContractCashflowMapper.deleteCalcByContractId(contract.getContractId());
                //删除合同信息
                hlsCusConContractMapper.deleteByPrimaryKey(contract);
            }
        }
    }

    public String rentPaymentNumber(HlsCusPrjProject prjProject) {
        String renyNumberSeq = "";
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(prjProject.getProjectId());

        List<HlsCusConContract> hlsCusConContractList = hlsCusConContractMapper.queryContractByProjectId(hlsCusConContract);
        //已经存在支付表时，获取最大的支付表的末位数字，不存在时，拼上“-1”
        if (hlsCusConContractList.size() > 0) {
            Long numberSeq = 0L;
            for (HlsCusConContract dt : hlsCusConContractList) {
                String[] tempContractNumbers = dt.getContractNumber().split("-");
                Long tempNumberSeq = Long.parseLong(tempContractNumbers[tempContractNumbers.length - 1]);
                if (numberSeq < tempNumberSeq) {
                    numberSeq = tempNumberSeq;
                }
            }
            numberSeq++;
            renyNumberSeq = prjProject.getContractNumber() + "-" + String.valueOf(numberSeq);
        } else {
            renyNumberSeq = prjProject.getContractNumber() + "-1";
        }

        return renyNumberSeq;

    }

    void createContractByProject(IRequest iRequest, HlsCusConContract contract) throws Exception {

        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(contract.getProjectId());
        project = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, project);

        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(project.getProjectId());

        List<HlsCusConContract> list = hlsCusConContractMapper.select(hlsCusConContract);

        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(project);
        hlsBeanRefUtilService.setFieldValue(hlsCusConContract, map);

        String rentNumberSeq = rentPaymentNumber(project);


//        hlsCusConContract.setContractNumber(project.getContractNumber()+"-"+ rentNumberSeq);
//        hlsCusConContract.setContractNumber(project.getContractNumber()+"-"+(list.size()));
        hlsCusConContract.setContractNumber(rentNumberSeq);
        hlsCusConContract.setFinanceAmount(contract.getFinanceAmount());
        hlsCusConContract.setInceptionOfLease(contract.getInceptionOfLease());

        //计算罚息相关字段 兴业动态界面 配置了这个字段 @QZK
        hlsCusConContract.setPenaltyRate(project.getFalsifyInterestRate());
        hlsCusConContract.setPenaltyCalcMethod(project.getFalsifyCalculationMethod());

        hlsCusConContract.set__status("insert");
        hlsCusConContract.setDataClass("NORMAL");
        hlsCusConContract.setContractStatus("NEW");
        hlsCusConContract.setDocumentCategory("CON_CONTRACT");
        StringBuffer sub = new StringBuffer();
        sub.append(project.getBusinessType());
        sub.append("_CON");
        hlsCusConContract.setDocumentType(sub.toString());
        hlsCusConContract.setAbsShowFlag("C");
        hlsCusConContract.setPenaltyProfile("STD");
        hlsCusConContract.setWriteOffAbleFlag("N");//设定未
        hlsCusConContract.setBillingProfile("CON");
        hlsCusConContract = self().insertSelective(iRequest, hlsCusConContract);

        //prj表里租金支付表序号加1
        HlsCusPrjProject cusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(project);
        cusPrjProject.setRentNumberSeq(rentNumberSeq);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, cusPrjProject);

        Long contractId = hlsCusConContract.getContractId();
        //复制项目报价到合同层
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(project.getProjectId());
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 99999);
        hlsCusPrjQuotation = hlsCusPrjQuotationList.get(0);

        HlsCusPrjQuotation hlsCusConQuotation = new HlsCusPrjQuotation();
        Map<String, String> contractmMap = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotation);
        hlsBeanRefUtilService.setFieldValue(hlsCusConQuotation, contractmMap);
        hlsCusConQuotation.setSourceDocumentId(contractId);
        hlsCusConQuotation.setFinanceAmount(contract.getFinanceAmount());
        hlsCusConQuotation.setSourceDocumentCategory("CON_CONTRACT");
        hlsCusConQuotation.setDataClass("VIRTUAL_CON");
        hlsCusConQuotation = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusConQuotation);

        //更新合同头上字段
        hlsCusConContract.setIrr(hlsCusConQuotation.getIrr());
        hlsCusConContract.setIrrAfterTax(hlsCusConQuotation.getIrrAfterTax());
        hlsCusConContract.setXirr(hlsCusConQuotation.getXirr());
        hlsCusConContract.setXirrNet(hlsCusConQuotation.getXirrNet());
        self().updateByPrimaryKeySelective(iRequest, hlsCusConContract);


        //复制prj_quotation_detail
        HlsCusPrjQuotationDetails quotationDetails = new HlsCusPrjQuotationDetails();
        quotationDetails.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        quotationDetails = hlsCusPrjQuotationDetailsMapper.selectOne(quotationDetails);

        if (quotationDetails == null) {
            throw new HlsCusException("报价信息异常，请检查报价信息!");
        }
        quotationDetails.setQuotationId(hlsCusConQuotation.getQuotationId());
        hlsCusPrjQuotationDetailsService.insert(iRequest, quotationDetails);

        //查询保证金跟费用项
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowMapper.selectQuotationFeeCashflow(hlsCusPrjQuotationCashflow);
        for (HlsCusPrjQuotationCashflow dto : hlsCusPrjQuotationCashflows) {
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
            dto = hlsCusPrjQuotationCashflowService.selectByPrimaryKey(iRequest, dto);
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
            hlsCusPrjQuotationCashflow1.setQuotationId(hlsCusConQuotation.getQuotationId());

            //判断是自定义还是采用放款金额，自定义直接复制，放款金额则根据租赁物总价款重新计算金额
            if (hlsCusPrjQuotationCashflow1.getCalcBase() != null) {
                if ("RELEASE".equals(hlsCusPrjQuotationCashflow1.getCalcBase())) {

                    hlsCusPrjQuotationCashflow1.setCalcBaseAmount(hlsCusConQuotation.getFinanceAmount());
                    hlsCusPrjQuotationCashflow1.setDueAmount(com.hand.hls.bp.components.CalculateUtil.mul(hlsCusConQuotation.getLeaseItemAmount(), hlsCusPrjQuotationCashflow1.getCalcRatio()));
                    //计算税额
                    if (hlsCusPrjQuotationCashflow1.getTaxTypeRate() != null) {
                        Double netDueAmount = com.hand.hls.bp.components.CalculateUtil.div(hlsCusPrjQuotationCashflow1.getDueAmount(), (CalculateUtil.add(1D, hlsCusPrjQuotationCashflow1.getTaxTypeRate())), 2);
                        Double vatDueAmount = com.hand.hls.bp.components.CalculateUtil.sub(hlsCusPrjQuotationCashflow1.getDueAmount(), netDueAmount);
                        hlsCusPrjQuotationCashflow1.setNetDueAmount(netDueAmount);
                        hlsCusPrjQuotationCashflow1.setVatDueAmount(vatDueAmount);
                    } else {
                        hlsCusPrjQuotationCashflow1.setNetDueAmount(hlsCusPrjQuotationCashflow1.getDueAmount());
                        hlsCusPrjQuotationCashflow1.setVatDueAmount(0D);
                    }
                }
            }
            hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow1);
        }

        hlsCusConContractCashflowMapper.deleteContractCashflowByQuotationId(hlsCusConQuotation);
        hlsCusPrjQuotationCashflowService.saveCashflowFromQuotationCashflow(iRequest, hlsCusConQuotation.getSourceDocumentId(), hlsCusConQuotation.getQuotationId(), hlsCusConQuotation.getInterestAmortizationMethod());

    }

    void autoCalcQuotation(IRequest iRequest, HlsCusConContract hlsCusConContract) throws Exception {
        //自动报价
        HlsCusPrjQuotation hlsCusConQuotation = new HlsCusPrjQuotation();
        hlsCusConQuotation.setSourceDocumentCategory("CON_CONTRACT");
        hlsCusConQuotation.setSourceDocumentId(hlsCusConContract.getContractId());
        hlsCusConQuotation = hlsCusPrjQuotationMapper.selectOne(hlsCusConQuotation);
        hlsCusConQuotation.setLeaseItemAmount(hlsCusConContract.getLeaseItemAmount());
        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, hlsCusConQuotation);
        hlsCusPrjQuotationService.quotationReCalc(iRequest, hlsCusConQuotation.getQuotationId(), true);

        hlsCusConQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusConQuotation);

        //回写合同
        hlsCusConContract.setQuotationId(hlsCusConQuotation.getQuotationId());
        hlsCusConContract.setLeaseTimes(hlsCusConQuotation.getLeaseTimes());//租赁期数
        hlsCusConContract.setLeaseTerm(hlsCusConQuotation.getLeaseTerm());//租赁期限
        hlsCusConContract.setCurrency("CNY");//币种
        hlsCusConContract.setInterestYearDays(hlsCusConQuotation.getInterestYearDays());//年计息天数
        hlsCusConContract.setBillingProfile(hlsCusConQuotation.getBillingProfile());//开票规则
        hlsCusConContract.setEstimateRentingDate(hlsCusConQuotation.getEstimateRentingDate());
        hlsCusConContract.setPriceList(hlsCusConQuotation.getPriceList());
        hlsCusConContract.setVatRate(hlsCusConQuotation.getVatRate());//稅率
        // hlsCusConContract.setInceptionOfLease(hlsCusConQuotation.getEstimateRentingDate());
        hlsCusConContract.setRentingFrequency(hlsCusConQuotation.getRentingFrequency().toString());
        hlsCusConContract.setTotalRental(hlsCusConQuotation.getTotalRental());//租赁总价款
        hlsCusConContract.setNetDownPayment(hlsCusConQuotation.getNetDownPayment());//不含税首付金额
        hlsCusConContract.setVatDownPayment(hlsCusConQuotation.getVatDownPayment());//首付款税额
        hlsCusConContract.setFinanceAmount(hlsCusConQuotation.getFinanceAmount());//融资额
        hlsCusConContract.setNetFinanceAmount(hlsCusConQuotation.getNetFinanceAmount());//不含税融资额
        hlsCusConContract.setVatFinanceAmount(hlsCusConQuotation.getVatFinanceAmount());//融资额增值税
        hlsCusConContract.setNetTotalRental(hlsCusConQuotation.getNetTotalRental());//不含税租金
        hlsCusConContract.setVatTotalRental(hlsCusConQuotation.getVatTotalRental());//租金增值税额
        hlsCusConContract.setTotalInterest(hlsCusConQuotation.getTotalInterest());//利息总额
        hlsCusConContract.setNetTotalInterest(hlsCusConQuotation.getNetTotalInterest());//不含税利息
        hlsCusConContract.setVatTotalInterest(hlsCusConQuotation.getVatTotalInterest());//利息增值税额
        hlsCusConContract.setLeaseChargeRatio(hlsCusConQuotation.getLeaseChargeRatio());//手续费比例
        hlsCusConContract.setLeaseCharge(hlsCusConQuotation.getLeaseCharge());//手续费
        hlsCusConContract.setNetLeaseCharge(hlsCusConQuotation.getNetLeaseCharge());//不含税手续费
        hlsCusConContract.setVatLeaseCharge(hlsCusConQuotation.getVatLeaseCharge());//手续费税额
        hlsCusConContract.setLeaseMgtFee(hlsCusConQuotation.getLeaseMgtFee());//管理费
        hlsCusConContract.setLeaseMgtFeeRatio(hlsCusConQuotation.getLeaseMgtFeeRatio());//管理费比例
        hlsCusConContract.setLeaseMgtFeeRule(hlsCusConQuotation.getLeaseMgtFeeRule());//管理费计算方式
        hlsCusConContract.setDeposit(hlsCusConQuotation.getDeposit());//保证金
        hlsCusConContract.setDepositRatio(hlsCusConQuotation.getDepositRatio());//保证金比例
        hlsCusConContract.setDepositDeduction(hlsCusConQuotation.getDepositReturnMethod());//保证金抵扣方式
        hlsCusConContract.setResidualValue(hlsCusConQuotation.getResidualValue());//留购价款
        hlsCusConContract.setResidualRatio(hlsCusConQuotation.getResidualRatio());//留购价款比例
        hlsCusConContract.setNetResidualValue(hlsCusConQuotation.getNetResidualValue());//不含税留购金
        hlsCusConContract.setBalloon(hlsCusConQuotation.getBalloon());//末期租金
        hlsCusConContract.setBalloonRatio(hlsCusConQuotation.getBalloonRatio());//末期租金比例
        hlsCusConContract.setNetBalloon(hlsCusConQuotation.getNetBalloon());//不含税末期租金
        hlsCusConContract.setVatBalloon(hlsCusConQuotation.getVatBalloon());//末期租金税额
        hlsCusConContract.setBaseRateType(hlsCusConQuotation.getBaseRateType());// 基准利率类别
        hlsCusConContract.setBaseRate(hlsCusConQuotation.getBaseRate());//基准利率
        hlsCusConContract.setIntRateType(hlsCusConQuotation.getIntRateType());//租赁利率类型
        hlsCusConContract.setIntRate(hlsCusConQuotation.getIntRate());//租赁利率
        hlsCusConContract.setIrr(hlsCusConQuotation.getIrr());//内部收益率
        hlsCusConContract.setIrrAfterTax(hlsCusConQuotation.getIrrAfterTax());//税后内部收益率
        hlsCusConContract.setXirr(hlsCusConQuotation.getXirr());//内部收益率
        hlsCusConContract.setXirrNet(hlsCusConQuotation.getXirrNet());//内部收益率
        //conContract.setPenaltyProfile(hlsCusConQuotation.getPenaltyProfile());//迟延履行金规则
        hlsCusConContract.setFloatingWay(hlsCusConQuotation.getFloatingWay());//浮动方式
        hlsCusConContract.setFloatingWayRate(hlsCusConQuotation.getFloatingWayRate());
        hlsCusConContract.setAnnualPayTimes(getRentingFrequency(hlsCusConContract.getRentingFrequency()));//还租频率
        hlsCusConContract.setVatInput(hlsCusConQuotation.getVatInput());//进项税额
        hlsCusConContract.setVatInputTaxTypeRate(hlsCusConQuotation.getVatInputTaxTypeRate());//进项税率
        hlsCusConContract.setLeaseItemAmount(hlsCusConQuotation.getLeaseItemAmount());//租赁物总价款
        hlsCusConContract.setBaseRateType("PBOC");
        hlsCusConContract.setReceivedStatus("NOT");
        hlsCusConContract.setWriteOffAbleFlag("Y");
        hlsCusConContract.setDataClass("NORMAL");
        hlsCusConContract.setInceptionOfLease(null);

        hlsCusConContract = self().updateByPrimaryKeySelective(iRequest, hlsCusConContract);
    }

    Boolean chekcContractLeaseItem(IRequest iRequest, List<HlsCusConContract> list) throws HlsCusException {

        //校验总金额不能超过项目总金额

        Double sumAmount = 0D;
        HlsCusConContract contract = new HlsCusConContract();
        contract.setProjectId(list.get(0).getProjectId());

        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(list.get(0).getProjectId());
        project = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, project);
        Double financeAmount = project.getFinanceAmount();

        //航空事业部的不校验金额
        if (project.getHostUnitId() == 114L) {
            return true;
        }

        if (financeAmount == null) {
            throw new HlsCusException("融资额获取失败!");
        }

        List<HlsCusConContract> existList = hlsCusConContractMapper.select(contract);

        for (HlsCusConContract conContract : list) {
            if ("insert".equals(conContract.get__status())) {
                existList.add(conContract);
            }
        }

        /*if(existList.size() > contractSignTimes){
            throw new HlsCusException("合同拆分次数不能超过合同签署放款次数!");
        }*/

        for (int i = 0; i < existList.size(); i++) {
            sumAmount = MathUtil.add(sumAmount, existList.get(i).getFinanceAmount());
        }

        if (sumAmount.compareTo(financeAmount) != 1) {
            return true;
        }

        return false;
    }


    @Override
    public void conPaymentSubmit(IRequest iRequest, Long paymentReqId) throws ResMessageException {
        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<>();
        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(paymentReqId);
        hlsCusCshPaymentReqHds = hlsCusCshPaymentReqHdMapper.queryForPaymentSubmit(cshPaymentReqHd);
        if (!hlsCusCshPaymentReqHds.get(0).getPaymentApprovedStatus().equalsIgnoreCase("APPROVING") && !hlsCusCshPaymentReqHds.get(0).getPaymentApprovedStatus().equalsIgnoreCase("APPROVED")) {
            databaseLockProvider.lock(cshPaymentReqHd);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            if (Objects.isNull(employee)) {
                throw new ResMessageException("获取提交人失败");
            }
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
            //开始流程
            Map<String, Object> params = new HashMap<>();
            params.put("workFlowType", "CSH_PAYMENT_WFL");
            params.put("loanTotalAmount", hlsCusCshPaymentReqHds.get(0).getLoanTotalAmount());
            params.put(IActivitiCommonService.WORK_FLOW_NAME, "CSH_PAYMENT_WFL");
            params.put(IActivitiCommonService.DEMO_NAME, "CSH_PAYMENT_WFL");
            params.put(IActivitiCommonService.BUSINESS_KEY, hlsCusCshPaymentReqHds.get(0).getPaymentReqId());
            params.put("BUSINESS_KEY", paymentReqId);
            params.put("companySpvN", hlsCusCshPaymentReqHds.get(0).getCompanySpvN());


            params.put("companySpv", hlsCusCshPaymentReqHds.get(0).getCompanySpv());
            params.put("companyId", hlsCusCshPaymentReqHds.get(0).getCompanyId());
            params.put("documentCategory", "CON_CONTRACT");
            params.put("documentName", hlsCusCshPaymentReqHds.get(0).getContractName());
            params.put("documentNumber", hlsCusCshPaymentReqHds.get(0).getContractNumber());
            activitiStartService.start(iRequest, hlsCusCshPaymentReqHds, params);

            cshPaymentReqHd.setPaymentApprovedStatus("APPROVING");
            // cshPaymentReqHd.setPaymentReqDate(new Date());
            cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, cshPaymentReqHd);
        } else {
            throw new ResMessageException("该笔单据已经提交审批");
        }
/*
        ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
*/
      /*  if(hlsCusCshPaymentReqHds.size() != 1){
            throw new ResMessageException("单据未找到");
        }*/
     /*   HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
        hlsCusCshPaymentReqLn.setPaymentReqId(paymentReqId);
        List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns = hlsCusCshPaymentReqLnMapper.select(hlsCusCshPaymentReqLn);
        for (int i = 0; i < hlsCusCshPaymentReqLns.size(); i++) {
            if(hlsCusCshPaymentReqLns.get(i).getBpBankAccountName() == null
                    && hlsCusCshPaymentReqLns.get(i).getBpBankAccountNum() == null
                    && hlsCusCshPaymentReqLns.get(i).getBpBankBranchName() == null){
                throw new ResMessageException("收款方户名、收款账号、收款银行信息不能为空！");
            }
        }*/
/*
        projectCreditCondition.setProjectId(paymentReqId);
*/

        // projectCreditCondition.setProjectId(hlsCusCshPaymentReqHds.get(0).getProjectId());
     /*   List<ProjectCreditCondition> projectCreditConditions = projectCreditConditionMapper.queryByPrj(projectCreditCondition);
        for (int i = 0; i < projectCreditConditions.size(); i++) {
            if(!"Y".equals(projectCreditConditions.get(i).getImplementationLease()) && !"Y".equals(projectCreditConditions.get(i).getIfPracticable())){
                throw new ResMessageException("租后落实和已落实不能同时为空!");

            }
        }*/


/*
        List<HlsCusCshPaymentReqHd> cusCshPaymentReqHdList =   hlsCusCshPaymentReqHdMapper.queryContractIdByFundPlan(cshPaymentReqHd);

        if(cusCshPaymentReqHdList.size() == 1){
            if(cusCshPaymentReqHdList.get(0).getContractId() != null ){
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(cusCshPaymentReqHdList.get(0).getContractId() );
                HlsCusConContract hlsCusConContractNew = this.selectByPrimaryKey(iRequest , hlsCusConContract);
                if(!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                    hlsCusConContractNew.setContractStatus("PAYMENTING");
                }
                this.updateByPrimaryKeySelective(iRequest , hlsCusConContractNew );
            }
        }*/


    }


    @Override
    public List<VirtualConContractLov> queryVirtualContractLov(IRequest iRequest, VirtualConContractLov virtualConContractLov, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);

        return hlsCusConContractMapper.queryVirtualContractLov(virtualConContractLov);
    }

    @Override
    public List<HlsCusPrjQuotation> createContractPlanByContract(IRequest iRequest, List<HlsCusPrjQuotation> list) throws Exception {
        if (list != null && list.size() > 0) {
            for (HlsCusPrjQuotation contractQuotation : list) {
                if (contractQuotation.getQuotationId() == null) {
                    String paymentNumber = prjQuotationMapper.queryPrjQuotationPaymentNumber(contractQuotation.getSourceDocumentId());
                    HlsCusPrjQuotation quotationReturn = createContractPlanQuotationByProject(iRequest, contractQuotation);
                    quotationReturn.setPaymentNumber(paymentNumber);
                    hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, quotationReturn);

                    contractQuotation.setQuotationId(quotationReturn.getQuotationId());
                }
                //更新报价几个可以填的字段
                HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
                quotation.setQuotationId(contractQuotation.getQuotationId());
                quotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotation);
                quotation.setLoanType(contractQuotation.getLoanType());
                quotation.setPriceList(contractQuotation.getPriceList());
                quotation.setLeaseItemAmountOrg(contractQuotation.getLeaseItemAmountOrg());
                quotation.setLeaseItemAmount(contractQuotation.getLeaseItemAmount());
                quotation.setCurrency(contractQuotation.getCurrency());
                quotation.setExchangeRate(contractQuotation.getExchangeRate());
                quotation.setBaseRateCollect(contractQuotation.getBaseRateCollect());
                quotation.setPaymentMethodNote(contractQuotation.getPaymentMethodNote());
                //常规类型
                quotation.setLoanType("ROUTINE");
                hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, quotation);
            }
        }

        return list;
    }

    /**
     * 创建投放计划报价
     *
     * @param iRequest
     * @param contractQuotaion
     * @return
     * @throws Exception
     */
    HlsCusPrjQuotation createContractPlanQuotationByProject(IRequest iRequest, HlsCusPrjQuotation contractQuotaion) throws Exception {

        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setSourceDocumentId(contractQuotaion.getSourceDocumentId());
        prjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        prjQuotation.setDataClass("VIRTUAL_CON");
        List<HlsCusPrjQuotation> prjQuotations = hlsCusPrjQuotationMapper.select(prjQuotation);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(prjQuotations)) {
            throw new ResMessageException("未找到合同主报价");
        }
        HlsCusPrjQuotation hlsCusPrjQuotationReturn = copyQuotationRelated(iRequest, prjQuotations.get(0).getQuotationId(), contractQuotaion.getSourceDocumentId(), "PRJ_PROJECT", "CONTRACT_PLAN");
        return hlsCusPrjQuotationReturn;
    }

    /**
     * 从老报价复制到新报价
     *
     * @param iRequest
     * @param quotationId               老报价的quotationID
     * @param newSourceDocumentId       新报价的SourceDocumentId
     * @param newSourceDocumentCategory 新报价的SourceDocumentCategory
     * @param newDataClass              新报价的DataClass
     * @return
     */
    @Override
    public HlsCusPrjQuotation copyQuotationRelated(IRequest iRequest, Long quotationId, Long newSourceDocumentId, String newSourceDocumentCategory, String newDataClass) {
        HlsCusPrjQuotation hlsCusPrjQuotationReturn = new HlsCusPrjQuotation();//用于返回

        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(prjQuotation);
        prjQuotation.setSourceDocumentCategory(newSourceDocumentCategory);
        prjQuotation.setSourceDocumentId(newSourceDocumentId);
        prjQuotation.setDataClass(newDataClass);
        //复制报价并获得新的ID
        Long newQuotationId = hlsCusPrjQuotationService.insertSelective(iRequest, prjQuotation).getQuotationId();

        hlsCusPrjQuotationReturn.setQuotationId(newQuotationId);

        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.select(prjQuotationCashflow);

        //复制报价现金流
        for (HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow : prjQuotationCashflowList) {
            hlsCusPrjQuotationCashflow.setQuotationId(newQuotationId);
            hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow);
        }

        HlsCusPrjQuotationDetails prjQuotationDetails = new HlsCusPrjQuotationDetails();
        prjQuotationDetails.setQuotationId(quotationId);
        List<HlsCusPrjQuotationDetails> prjQuotationDetailsList = hlsCusPrjQuotationDetailsMapper.select(prjQuotationDetails);

        //复制PrjQuotationDetails
        for (HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails : prjQuotationDetailsList) {
            hlsCusPrjQuotationDetails.setQuotationId(newQuotationId);
            hlsCusPrjQuotationDetailsService.insertSelective(iRequest, hlsCusPrjQuotationDetails);
        }

        //合同上的quotation_id同步更新
        if ("CON_CONTRACT".equals(newSourceDocumentCategory)) {
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractId(newSourceDocumentId);
            contract.setQuotationId(newQuotationId);
            self().updateByPrimaryKeySelective(iRequest, contract);
        }

        return hlsCusPrjQuotationReturn;
    }

    @Override
    public void deleteContractPlan(IRequest iRequest, List<HlsCusPrjQuotation> list) {
        for (HlsCusPrjQuotation quotation : list) {
            if (quotation.getQuotationId() != null) {
                //删除报价现金流
                HlsCusPrjQuotationCashflow cashPara = new HlsCusPrjQuotationCashflow();
                cashPara.setQuotationId(quotation.getQuotationId());
                List<HlsCusPrjQuotationCashflow> deleteCashflows = hlsCusPrjQuotationCashflowService.select(iRequest, cashPara, 1, 99999);
                hlsCusPrjQuotationCashflowService.batchDelete(deleteCashflows);

                //删除PrjQuotationDetails
                HlsCusPrjQuotationDetails detailPara = new HlsCusPrjQuotationDetails();
                detailPara.setQuotationId(quotation.getQuotationId());
                List<HlsCusPrjQuotationDetails> deleteDetails = hlsCusPrjQuotationDetailsService.select(iRequest, detailPara, 1, 99999);
                hlsCusPrjQuotationDetailsService.batchDelete(deleteDetails);
                //删除主表
                hlsCusPrjQuotationService.deleteByPrimaryKey(quotation);

            }
        }
    }

    @Override
    public void confirmContractPlan(IRequest iRequest, List<HlsCusPrjQuotation> list) {
        for (HlsCusPrjQuotation quotation : list) {
            if (quotation.getQuotationId() != null && quotation.getSourceDocumentId() != null) {
                //查询虚拟合同详情
                HlsCusPrjProject project = new HlsCusPrjProject();
                project.setProjectId(quotation.getProjectId());
                project = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, project);
                //更改状态
                quotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, quotation);
                quotation.setPaymentStatus("CONFIRM");
                hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, quotation);

                //已确认的常规投放的投放计划金额不能超过合同金额，关税投放不校验
                Double enableLeaseItemAmount = hlsCusPrjProjectMapper.queryLeaseItemAmount(project.getProjectId());
                if (ROUTINE.equals(quotation.getLoanType()) && enableLeaseItemAmount < 0) {
                    throw new IllegalArgumentException("已确认的常规投放的投放计划金额不能超过合同金额");
                }

                //创建合同
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(project);
                hlsBeanRefUtilService.setFieldValue(hlsCusConContract, map);
                hlsCusConContract.setProjectId(project.getProjectId());
                hlsCusConContract.setContractNumber(quotation.getPaymentNumber());
                hlsCusConContract.setPenaltyRate(project.getFalsifyInterestRate());
                hlsCusConContract.setLeaseTimes(quotation.getLeaseTimes());
                hlsCusConContract.setLeaseTerm(quotation.getLeaseTerm());
                hlsCusConContract.setPenaltyCalcMethod(project.getFalsifyCalculationMethod());
                hlsCusConContract.setQuotationId(quotation.getQuotationId());
                hlsCusConContract.setPriceList(quotation.getPriceList());
                hlsCusConContract.setVatRate(quotation.getVatRate());
                hlsCusConContract.setIntRate(quotation.getIntRate());
                hlsCusConContract.setIntRateType(quotation.getIntRateType());
                hlsCusConContract.setFloatingWayRate(quotation.getFloatingWayRate());
                hlsCusConContract.setRentingFrequency(quotation.getRentingFrequency());
                hlsCusConContract.setBaseRate(quotation.getBaseRate());
                hlsCusConContract.setBaseRateType(quotation.getBaseRateType());
                hlsCusConContract.setIrr(quotation.getIrr());
                hlsCusConContract.setXirr(quotation.getXirr());
                hlsCusConContract.setTotalRental(quotation.getTotalRental());
                hlsCusConContract.setTotalInterest(quotation.getTotalInterest());
                hlsCusConContract.setFinanceAmount(quotation.getFinanceAmount());
                hlsCusConContract.setLeaseStartDate(quotation.getLeaseStartDate());
                hlsCusConContract.setLeaseEndDate(quotation.getLeaseStartDate());
                hlsCusConContract.setPmt(quotation.getPmt());
                hlsCusConContract.setIrrAfterTax(quotation.getIrrAfterTax());
                hlsCusConContract.setLeaseItemAmount(quotation.getLeaseItemAmount());
                hlsCusConContract.setResidualValue(quotation.getResidualValue());
                hlsCusConContract.setPayType(quotation.getPayType());
                hlsCusConContract.setDownPayment(quotation.getDownPayment());
                hlsCusConContract.setDownPaymentRatio(quotation.getDownPaymentRatio());
                hlsCusConContract.setLeaseCharge(quotation.getLeaseCharge());
                hlsCusConContract.setLeaseChargeRatio(quotation.getLeaseChargeRatio());
                hlsCusConContract.setDeposit(quotation.getDeposit());
                hlsCusConContract.setDepositRatio(quotation.getDepositRatio());
                hlsCusConContract.setDepositDeduction(quotation.getDepositDeduction());

                hlsCusConContract.set__status("insert");
                hlsCusConContract.setDataClass("NORMAL");
                hlsCusConContract.setContractStatus("SIGN");
                hlsCusConContract.setDocumentCategory("CON_CONTRACT");
                StringBuffer sub = new StringBuffer();
                sub.append(project.getBusinessType());
                sub.append("_CON");
                hlsCusConContract.setDocumentType(sub.toString());
                hlsCusConContract.setAbsShowFlag("C");
                hlsCusConContract.setPenaltyProfile("STD");
                hlsCusConContract.setWriteOffAbleFlag("N");//设定未
                hlsCusConContract.setBillingProfile("CON");
                hlsCusConContract.setLegalContractNumber(project.getContractNum());
                hlsCusConContract = self().insertSelective(iRequest, hlsCusConContract);
                //保存到合同现金流
                hlsCusPrjQuotationCashflowService.saveCashflowFromQuotationCashflow(iRequest, hlsCusConContract.getContractId(), quotation.getQuotationId(), quotation.getInterestAmortizationMethod());
                //生成合同档案记录
                contractArchiveService.saveContractArchive(iRequest, hlsCusConContract);
            }
        }
    }

    @Override
    public List<HlsCusConContractRentPaymentConfirm> createContractConfirm(IRequest iRequest, HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm) throws HlsCusException {
        List<HlsCusConContractRentPaymentConfirm> returnList = new ArrayList<>();
        if (conContractRentPaymentConfirm.getQuotationId() != null) {
            //校验同一个合同只能创建一次支付表确认,如果是关联解付ID可以每个解付ID创建一次
            List<HlsCusConContractRentPaymentConfirm> list = conContractRentPaymentConfirmMapper.queryConContractRentPaymentConfirm(conContractRentPaymentConfirm);
            if (list.size() > 0) {
                throw new HlsCusException("该支付表已经确认过，请创建解付");
            }
            conContractRentPaymentConfirm.setStatus("NEW");
            conContractRentPaymentConfirmService.insert(iRequest, conContractRentPaymentConfirm);
            HlsCusPrjQuotation hlsCusPrjQuotationReturn = copyQuotationRelated(iRequest, conContractRentPaymentConfirm.getQuotationId(), conContractRentPaymentConfirm.getPaymentConfirmId(), "CONTRACT_CONFIRM", "CONTRACT_CONFIRM");

            conContractRentPaymentConfirm = conContractRentPaymentConfirmService.selectByPrimaryKey(iRequest, conContractRentPaymentConfirm);
            conContractRentPaymentConfirm.setQuotationId(hlsCusPrjQuotationReturn.getQuotationId());
            conContractRentPaymentConfirmService.updateByPrimaryKey(iRequest, conContractRentPaymentConfirm);
            returnList.add(conContractRentPaymentConfirm);
        }
        return returnList;
    }

    @Override
    public List<HlsCusConContractRentPaymentConfirm> submitContractConfirmWfl(IRequest iRequest, HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm) throws HlsCusException {
        List<HlsCusConContractRentPaymentConfirm> returnList = new ArrayList<>();
        if (conContractRentPaymentConfirm.getPaymentConfirmId() != null) {
            conContractRentPaymentConfirm = conContractRentPaymentConfirmService.selectByPrimaryKey(iRequest, conContractRentPaymentConfirm);
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(conContractRentPaymentConfirm.getProjectId());
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            returnList.add(conContractRentPaymentConfirm);
            //校验现金流
            checkCashflow(iRequest, conContractRentPaymentConfirm.getPaymentConfirmId());
            //校验 如果支付表编号最后两位不为01 则报价报价留购价需要为0  substr(str,-1)
            /*List<HlsCusConContractRentPaymentConfirm> confirmList = conContractRentPaymentConfirmMapper.queryCheckConfirm(conContractRentPaymentConfirm);
            if (confirmList.size() > 0) {
                if (!"01".equalsIgnoreCase(confirmList.get(0).getContractNumber())) {
                    throw new HlsCusException("同一个合同项下仅第一笔投放的支付表存在留购金，其他需为0，请修改！");
                }
            }*/
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            if (ObjectUtils.isEmpty(employee)) {
                throw new HlsCusException("获取提交人失败");
            }
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);

            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", "CON_CONTRACT_CASHCONFIRM");
            params.put("documentCategory", "CON_CONTRACT_CASHCONFIRM");
            params.put("projectAssistant", Optional.ofNullable(hlsCusPrjProject).map(HlsCusPrjProject::getAssistProjectManager).orElse(null));
            activitiStartService.start(iRequest, returnList, params);

            //修改单据状态
            conContractRentPaymentConfirm.setStatus("APPROVING");
            conContractRentPaymentConfirmService.updateByPrimaryKeySelective(iRequest, conContractRentPaymentConfirm);
        }
        return returnList;
    }

    /*
     * 支付表确认审批通过
     */
    @Override
    public List<HlsCusConContractRentPaymentConfirm> submitContractConfirmApproved(IRequest iRequest, HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm) throws Exception {
        List<HlsCusConContractRentPaymentConfirm> returnList = new ArrayList<>();
        returnList.add(conContractRentPaymentConfirm);
        if (conContractRentPaymentConfirm.getPaymentConfirmId() != null) {
            conContractRentPaymentConfirm = conContractRentPaymentConfirmService.selectByPrimaryKey(iRequest, conContractRentPaymentConfirm);

            //先删除未核销的现金流
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId(conContractRentPaymentConfirm.getContractId());
            hlsCusConContractCashflow.setWriteOffFlag("NOT");
            List<HlsCusConContractCashflow> deleteCashflows = hlsCusConContractCashflowMapper.select(hlsCusConContractCashflow);
            deleteCashflows.forEach(item -> {
                //（关税现金流不能删除）
                if (item.getCfItem() != 131 && item.getCfItem() != 132) {
                    hlsCusConContractCashflowService.deleteByPrimaryKey(item);
                }
            });

            //查询新现金流
            HlsCusPrjQuotationCashflow cashPara = new HlsCusPrjQuotationCashflow();
            cashPara.setQuotationId(conContractRentPaymentConfirm.getQuotationId());
            List<HlsCusPrjQuotationCashflow> newCashflows = hlsCusPrjQuotationCashflowService.select(iRequest, cashPara, 1, 99999);
            for (int i = 0; i < newCashflows.size(); i++) {
                //先根据cfItem和times查询原现金流是否核销,已经核销的不会被覆盖（除已经解付的设备款外）
                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                conContractCashflow.setContractId(conContractRentPaymentConfirm.getContractId());
                conContractCashflow.setTimes(newCashflows.get(i).getTimes().longValue());
                conContractCashflow.setCfItem(newCashflows.get(i).getCfItem());
                List<HlsCusConContractCashflow> oldlist = hlsCusConContractCashflowService.select(iRequest, conContractCashflow, 1, 99999);
                if (oldlist.size() == 0) {
                    BeanRefUtils.beanToBean(newCashflows.get(i), conContractCashflow, hlsBeanRefUtilService);
                    conContractCashflow.setTimes(newCashflows.get(i).getTimes().longValue());
                    conContractCashflow.setContractId(conContractRentPaymentConfirm.getContractId());
                    conContractCashflow.setCfStatus("RELEASE");
                    conContractCashflow.setWriteOffFlag("NOT");
                    conContractCashflow.setBillingStatus("NOT");
                    conContractCashflow.setOverdueStatus("N");
                    conContractCashflow.setPenaltyProcessStatus("N");
                    conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
                    conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
                    conContractCashflow.setGeneratedSourceDocId(conContractRentPaymentConfirm.getQuotationId());
                    conContractCashflow.setGeneratedSourceDocLineId(newCashflows.get(i).getQuotationCashflowId());
                    hlsCusConContractCashflowService.insertSelective(iRequest, conContractCashflow);
                } else {
                    if (oldlist.size() > 1) {
                        throw new HlsCusException("相同类型存在期次相同两条的现金流，请核对！");
                    } else {
                        //解付ID需要将已经核销的现金流修改，同时刷新核销记录中的金额
                        if (conContractRentPaymentConfirm.getPaymentDischargeId() != null && newCashflows.get(i).getCfItem() == 0) {
                            BeanRefUtils.beanToBean(newCashflows.get(i), conContractCashflow, hlsBeanRefUtilService);
                            conContractCashflow.setTimes(newCashflows.get(i).getTimes().longValue());
                            conContractCashflow.setContractId(conContractRentPaymentConfirm.getContractId());
                            conContractCashflow.setCfStatus(oldlist.get(0).getCfStatus());
                            conContractCashflow.setWriteOffFlag(oldlist.get(0).getWriteOffFlag());
                            conContractCashflow.setWriteOffDueAmount(conContractCashflow.getDueAmount());
                            conContractCashflow.setReceivedAmount(conContractCashflow.getDueAmount());
                            conContractCashflow.setBillingStatus(oldlist.get(0).getBillingStatus());
                            conContractCashflow.setOverdueStatus(oldlist.get(0).getOverdueStatus());
                            conContractCashflow.setPenaltyProcessStatus(oldlist.get(0).getPenaltyProcessStatus());
                            conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
                            conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
                            conContractCashflow.setGeneratedSourceDocId(conContractRentPaymentConfirm.getQuotationId());
                            conContractCashflow.setGeneratedSourceDocLineId(newCashflows.get(i).getQuotationCashflowId());
                            conContractCashflow.setCashflowId(oldlist.get(0).getCashflowId());
                            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);
                        } else {
                            conContractCashflow.setContractId(conContractRentPaymentConfirm.getContractId());
                            conContractCashflow.setDueDate(newCashflows.get(i).getDueDate());
                            conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
                            conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
                            conContractCashflow.setGeneratedSourceDocId(conContractRentPaymentConfirm.getQuotationId());
                            conContractCashflow.setGeneratedSourceDocLineId(newCashflows.get(i).getQuotationCashflowId());
                            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);
                        }
                    }
                }

            }
            //更新con_contract表quotation_id
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(conContractRentPaymentConfirm.getContractId());
            hlsCusConContract.setQuotationId(conContractRentPaymentConfirm.getQuotationId());
            //获取起租类型,设置起租状态
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContract).getProjectId());
            hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
            if (hlsCusPrjProject != null && "Y".equals(hlsCusPrjProject.getLoanInitialLease())) {
                hlsCusConContract.setContractStatus("INCEPT");
                if ("OUTSIDE".equals(hlsCusPrjProject.getIsLowRisk())) {
                    hlsCusConContract.setRiskReserveRatio(0.015D);
                }
                if ("WITHIN".equals(hlsCusPrjProject.getIsLowRisk())) {
                    hlsCusConContract.setRiskReserveRatio(0.01D);
                }
                //查询最新的合同数据
                HlsCusConContract ct = new HlsCusConContract();
                ct.setContractId(hlsCusConContract.getContractId());
                ct = self().selectByPrimaryKey(iRequest, ct);

                if ("LEASE".equalsIgnoreCase(ct.getBusinessType())) {
                    hlsCusConContract.setStampDuty("0.00005");
                    hlsCusConContract.setPostFlag("N");
                    hlsCusConContract.setPurStampDuty("0.0003");
                    hlsCusConContract.setPurPostFlag("N");
                }
                if ("LEASEBACK".equalsIgnoreCase(ct.getBusinessType())) {
                    hlsCusConContract.setStampDuty("0.00005");
                    hlsCusConContract.setPostFlag("N");
                }

                gldContractCashflowService.clacFinanceIncome(iRequest, ct.getContractId(), ct.getVatRate(), ct.getIrr());
                //gldContractCashflowService.clacFinanceIncome(iRequest, conContractRentPaymentConfirm.getContractId(), hlsCusPrjProject.getVatRate(), null);

                //起租凭证
                Map contractInceptMap = new HashMap<>();
                contractInceptMap.put("jeTrxId", ct.getContractId());
                contractInceptMap.put("companyId", ct.getCompanyId());
                contractInceptMap.put("contractId", ct.getContractId());
                contractInceptMap.put("sourceDoc", "CON_CONTRACT");
                AbstractJeTrxService contractInceptJeTrxService = JeTrxCommonService.map.get("CONTRACT_INCEPT");
                contractInceptJeTrxService.process(RequestHelper.getCurrentRequest(true), contractInceptMap);
            } else {
                hlsCusConContract.setContractStatus("PAYMENTED");
            }

            //更新合同起租日
            HlsCusPrjQuotation prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(conContractRentPaymentConfirm.getQuotationId());
            hlsCusConContract.setLeaseStartDate(prjQuotation.getLeaseStartDate());
            hlsCusConContract.setXirr(prjQuotation.getXirr());
            hlsCusConContract.setIrr(prjQuotation.getIrr());
            hlsCusConContract.setLeaseTimes(prjQuotation.getLeaseTimes());
            self().updateByPrimaryKeySelective(iRequest, hlsCusConContract);

            //查询税率 todo 报价xirr落表取后放开
            /*HlsCusPrjQuotation prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(conContractRentPaymentConfirm.getQuotationId());
            if (ObjectUtils.isEmpty(prjQuotation.getVatRate())) {
                throw new HlsCusException("税率获取失败");
            }*/
            //查询xirr todo 报价xirr落表取后放开
            /*if (ObjectUtils.isEmpty(prjQuotation.getXirr())) {
                throw new HlsCusException("xirr获取失败");
            }*/
            //执行分摊逻辑
            //gldContractCashflowService.clacFinanceIncome(iRequest, conContractRentPaymentConfirm.getContractId(), prjQuotation.getVatRate(), prjQuotation.getXirr());

            //大单支付表确认后同步至财务中台
            financeBaseUtils.paymentFlowItfc(iRequest, hlsCusConContract.getContractId());
        }
        return returnList;
    }

    @Override
    public void checkCashflow(IRequest iRequest, Long paymentConfirmId) throws HlsCusException {
        HlsCusConContractRentPaymentConfirm confirm = conContractRentPaymentConfirmMapper.selectByPrimaryKey(paymentConfirmId);
        //查询原合同现金流
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(confirm.getContractId());
        List<HlsCusConContractCashflow> cashflows = hlsCusConContractCashflowMapper.select(hlsCusConContractCashflow);
        for (HlsCusConContractCashflow item : cashflows) {
            //如果已经核销则查询现金流金额是否改变
            if (!"NOT".equals(item.getWriteOffFlag())) {
                HlsCusPrjQuotationCashflow cashPara = new HlsCusPrjQuotationCashflow();
                cashPara.setQuotationId(confirm.getQuotationId());
                cashPara.setCfItem(item.getCfItem());
                cashPara.setTimes(item.getTimes());
                List<HlsCusPrjQuotationCashflow> newCashflows = hlsCusPrjQuotationCashflowService.select(iRequest, cashPara, 1, 99999);
                if (newCashflows.size() > 1) {
                    throw new HlsCusException("相同类型存在期次相同两条的现金流，请核对！");
                } else if (newCashflows.size() == 1) {
                    //已核销现金流不可修改现金流金额（信用证解付情况 可以修改设备款金额）
                    if (item.getCfItem() != 0 || confirm.getPaymentDischargeId() == null) {
                        if (newCashflows.get(0).getDueAmount() - item.getDueAmount() != 0) {
                            throw new HlsCusException("cfItem为" + item.getCfItem() + "的第" + item.getTimes() + "期现金流已经核销不允许更改金额");
                        }
                    }
                }

            }
        }

        //当前合同最后一期支付日要小于第零期投放日+租赁月数
        HlsCusPrjQuotation quotationConfirm = new HlsCusPrjQuotation();
        quotationConfirm.setQuotationId(confirm.getQuotationId());
        quotationConfirm = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, quotationConfirm);

        HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
        quotationCashflow.setQuotationId(confirm.getQuotationId());
        List<HlsCusPrjQuotationCashflow> quotationCashflowList = hlsCusPrjQuotationCashflowMapper.select(quotationCashflow);
        if (quotationCashflowList != null || quotationCashflowList.size() != 0) {
            quotationCashflow = quotationCashflowList.stream().filter(item -> 1 == item.getCfItem())
                    .sorted(Comparator.comparing(HlsCusPrjQuotationCashflow::getTimes).reversed()).collect(Collectors.toList()).get(0);
            int leaseTermMonth = new Double(quotationConfirm.getLeaseTerm() * 12).intValue();
            Date leaseStartDate = quotationConfirm.getLeaseStartDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(leaseStartDate);
            calendar.add(Calendar.MONTH, leaseTermMonth);
            Date endDate = calendar.getTime();
            if (quotationCashflow.getDueDate().compareTo(endDate) >= 0) {
                throw new HlsCusException("当前合同报价最后一期支付日需小于合同投放日+租赁月数，请修改最后一期支付日后进行合同签约");
            }
        }
    }

    @Override
    public void validatePurchaseContractStatus(IRequest iRequest, Long paymentReqId) throws HlsCusException {
        HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
        hlsCusCshPaymentReqLn.setPaymentReqId(paymentReqId);
        List<HlsCusCshPaymentReqLn> cusCshPaymentReqLnList = hlsCusCshPaymentReqLnMapper.select(hlsCusCshPaymentReqLn);
        for (HlsCusCshPaymentReqLn cshPaymentReqLn : cusCshPaymentReqLnList) {
            if (cshPaymentReqLn.getPurchaseContractId() != null) {
                Integer approvingPurchaseContractCount = hlsCusCshPaymentReqLnMapper.selectApprovingPurchaseContractCount(cshPaymentReqLn.getPurchaseContractId());
                if (approvingPurchaseContractCount != 0) {
                    throw new HlsCusException("采购合同还在审批中，不能提交审批");
                }
            }
        }
    }

    @Override
    public List<HlsCusConContract> selectStampDutyLease(HlsCusConContract dto) throws HlsCusException {
        List<HlsCusConContract> hlsCusConContracts = hlsCusConContractMapper.selectStampDutyLease(dto);
        List<HlsCusConContract> hlsCusConContractsList = new ArrayList<>();
        for (int i = 0; i < hlsCusConContracts.size(); i++) {
            HlsCusConContract hlsCusConContract = hlsCusConContracts.get(i);
            String leaseTime = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss").format(hlsCusConContract.getInceptionOfLease());
            hlsCusConContract.setPeriodName(leaseTime.substring(0, leaseTime.lastIndexOf("-")));
            hlsCusConContractsList.add(hlsCusConContract);
        }
        return hlsCusConContractsList;
    }

    @Override
    public List<HlsCusConContract> selectRiskFundAccrual(HlsCusConContract dto) {
        List<HlsCusConContract> hlsCusConContracts = hlsCusConContractMapper.selectRiskFundAccrual(dto);
        List<HlsCusConContract> hlsCusConContractsList = new ArrayList<>();
        for (int i = 0; i < hlsCusConContracts.size(); i++) {
            HlsCusConContract hlsCusConContract = hlsCusConContracts.get(i);
            hlsCusConContract.setPeriodName(dto.getPeriodName());
            hlsCusConContractsList.add(hlsCusConContract);
        }
        return hlsCusConContractsList;
    }

    @Override
    public void conInceptconfirm(IRequest iRequest, Map paraMap) throws HlsCusException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Long contractId = Long.valueOf(paraMap.get("contract_id").toString());
        Long quotationId = Long.valueOf(paraMap.get("quotation_id").toString());
        Long projectId = Long.valueOf(paraMap.get("project_id").toString());
        Date newLeaseStartDate = new Date();
        try {
            newLeaseStartDate = sdf.parse(paraMap.get("lease_start_date").toString());
        } catch (Exception e) {
            return;
        }

        //获取报价原起租日
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);

        //获取现金流
        //项目现金流（虚拟合同）
        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setCfItem(0L);
        prjQuotationCashflow.setTimes(0L);
        prjQuotationCashflow.setCfType(0L);
        prjQuotationCashflow.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjCashFlows = hlsCusPrjQuotationCashflowMapper.select(prjQuotationCashflow);

        //合同现金流（合同）
        HlsCusConContractCashflow conQuotationCashflow = new HlsCusConContractCashflow();
        conQuotationCashflow.setCfItem(0L);
        conQuotationCashflow.setTimes(0L);
        conQuotationCashflow.setCfType(0L);
        conQuotationCashflow.setContractId(contractId);
        List<HlsCusConContractCashflow> conCashFlows = hlsCusConContractCashflowMapper.select(conQuotationCashflow);

        //更新现金流的日期, 只更新第0期日期
        for (HlsCusPrjQuotationCashflow prjCashFlow : prjCashFlows) {
            prjCashFlow.setDueDate(newLeaseStartDate);
            hlsCusPrjQuotationCashflowMapper.updateByPrimaryKeySelective(prjCashFlow);
        }
        for (HlsCusConContractCashflow conCashFlow : conCashFlows) {
            conCashFlow.setDueDate(newLeaseStartDate);
            hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(conCashFlow);
        }

        //更新报价起租日
        hlsCusPrjQuotation.setLeaseStartDate(newLeaseStartDate);
        prjQuotationMapper.updateByPrimaryKeySelective(hlsCusPrjQuotation);

        //更新起租状态
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract = selectByPrimaryKey(iRequest, contract);
        contract.setContractStatus("INCEPT");
        if (paraMap.get("is_low_risk").toString().equals("OUTSIDE")) {
            contract.setRiskReserveRatio(0.015D);
        }
        if (paraMap.get("is_low_risk").toString().equals("WITHIN")) {
            contract.setRiskReserveRatio(0.01D);
        }
        if (contract.getBusinessType().equalsIgnoreCase("LEASE")) {
            contract.setStampDuty("0.00005");
            contract.setPostFlag("N");
            contract.setPurStampDuty("0.0003");
            contract.setPurPostFlag("N");
        }
        if (contract.getBusinessType().equalsIgnoreCase("LEASEBACK")) {
            contract.setStampDuty("0.00005");
            contract.setPostFlag("N");
        }
        hlsCusConContractMapper.updateByPrimaryKeySelective(contract);

        //还款同步接口 插入 表数据
        HlsCashflowAyncDto hlsCashflowAyncDto = new HlsCashflowAyncDto();
        hlsCashflowAyncDto.setContractId(contractId);
        hlsCashflowAyncDto.setInceptionDate(newLeaseStartDate);
        hlsCashflowAyncDto.setType(conQuotationCashflow.getCfItem());
        hlsCashflowAyncDto.setPostFlag("N");
        hlsCashflowAyncService.insertSelective(iRequest, hlsCashflowAyncDto);

        //查询最新的合同数据
        HlsCusConContract ct = new HlsCusConContract();
        ct.setContractId(contract.getContractId());
        ct = self().selectByPrimaryKey(iRequest, ct);
        gldContractCashflowService.clacFinanceIncome(iRequest, ct.getContractId(), ct.getVatRate(), ct.getIrr());

        //起租凭证
        Map contractInceptMap = new HashMap<>();
        contractInceptMap.put("jeTrxId", ct.getContractId());
        contractInceptMap.put("companyId", ct.getCompanyId());
        contractInceptMap.put("contractId", ct.getContractId());
        contractInceptMap.put("sourceDoc", "CON_CONTRACT");
        AbstractJeTrxService contractInceptJeTrxService = JeTrxCommonService.map.get("CONTRACT_INCEPT");
        contractInceptJeTrxService.process(RequestHelper.getCurrentRequest(true), contractInceptMap);

    }

    @Override
    @SneakyThrows
    public void updateCshDate(IRequest iRequest, Map paraMap) throws HlsCusException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long quotationId = Long.valueOf(paraMap.get("quotation_id").toString());
        Date date = sdf.parse(paraMap.get("lease_start_date").toString());
        Long projectId = Long.valueOf(paraMap.get("project_id").toString());

        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(projectId);
        hlsCusConContract.setDataClass("NORMAL");
        hlsCusConContract = hlsCusConContractMapper.select(hlsCusConContract).get(0);

        //获取合同第0期现金流
        HlsCusConContractCashflow conQuotationCashflow = new HlsCusConContractCashflow();
        conQuotationCashflow.setCfItem(0L);
        conQuotationCashflow.setTimes(0L);
        conQuotationCashflow.setCfType(0L);
        conQuotationCashflow.setContractId(hlsCusConContract.getContractId());
        List<HlsCusConContractCashflow> conCashFlows = hlsCusConContractCashflowMapper.select(conQuotationCashflow);


        //获取项目第0期现金流
        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setCfItem(0L);
        prjQuotationCashflow.setTimes(0L);
        prjQuotationCashflow.setCfType(0L);
        prjQuotationCashflow.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjCashFlows = hlsCusPrjQuotationCashflowMapper.select(prjQuotationCashflow);

        //获取报价
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);

        //更新dueDate为投放日
        try {
            if (conCashFlows.size() > 0) {
                HlsCusConContractCashflow hlsCusConContractCashflow = conCashFlows.get(0);
                hlsCusConContractCashflow.setDueDate(date);
                hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(hlsCusConContractCashflow);
            }
            if (prjCashFlows.size() > 0) {
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = prjCashFlows.get(0);
                hlsCusPrjQuotationCashflow.setDueDate(date);
                hlsCusPrjQuotationCashflowMapper.updateByPrimaryKeySelective(hlsCusPrjQuotationCashflow);
            }
            //更新报价起租日
            hlsCusPrjQuotation.setLeaseStartDate(date);
            prjQuotationMapper.updateByPrimaryKeySelective(hlsCusPrjQuotation);
        } catch (Exception e) {
            throw new ResMessageException(e.getMessage());
        }

    }

    @Override
    @SneakyThrows
    public void updateQuotationInfo(IRequest iRequest, Map paraMap) throws HlsCusException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long quotationId = Long.valueOf(paraMap.get("quotation_id").toString());
        Long projectId = Long.valueOf(paraMap.get("project_id").toString());
        List<HlsCusPrjQuotationCashflow> quotationCashflowList = JSON.parseArray(JSON.toJSONString(paraMap.get("cashflows")), HlsCusPrjQuotationCashflow.class);

        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(projectId);
        hlsCusConContract.setDataClass("NORMAL");
        hlsCusConContract = hlsCusConContractMapper.select(hlsCusConContract).get(0);

        //获取合同现金流
        HlsCusConContractCashflow conQuotationCashflow = new HlsCusConContractCashflow();
        conQuotationCashflow.setContractId(hlsCusConContract.getContractId());
        List<HlsCusConContractCashflow> conCashFlows = hlsCusConContractCashflowMapper.select(conQuotationCashflow);

        //获取项目现金流
        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjCashFlows = hlsCusPrjQuotationCashflowMapper.select(prjQuotationCashflow);

        //获取报价
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
        //付款频率 1:月付 3:季付 6:半年付 12:年付
        String rentingFrequency = hlsCusPrjQuotation.getRentingFrequency();

        //更新dueDate为投放日
        try {
            if (conCashFlows.size() > 0) {
                for (HlsCusConContractCashflow cashflow : conCashFlows) {
                    if (0L != cashflow.getCfItem()) {
                        cashflow.setNetDueAmount(cashflow.getNetPrincipal() + cashflow.getNetInterest());
                        cashflow.setVatDueAmount(cashflow.getVatPrincipal() + cashflow.getVatInterest());
                    }
                    hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(cashflow);
                }
            }
            HlsCusPrjQuotationCashflow maxCashflow = prjCashFlows.stream().max(Comparator.comparing(HlsCusPrjQuotationCashflow::getTimes)).get();
            if (prjCashFlows.size() > 0) {
                for (HlsCusPrjQuotationCashflow quotationCashflow : prjCashFlows) {
                    if (quotationCashflow.getCfItem() == null) {
                        Date dueDate = quotationCashflow.getDueDate() == null ? quotationCashflow.getCalcDate() : quotationCashflow.getDueDate();
                        quotationCashflow.setDueDate(dueDate);
                        quotationCashflow.setCalcDate(dueDate);
                        quotationCashflow.setFinIncomeDate(dueDate);
                        quotationCashflow.setCfItem(1L);
                        quotationCashflow.setCfType(1L);
                        quotationCashflow.setCfDirection("INFLOW");
                        quotationCashflow.setCfStatus("RELEASE");
                        quotationCashflow.setNetDueAmount(quotationCashflow.getNetPrincipal() + quotationCashflow.getNetInterest());
                        quotationCashflow.setVatDueAmount(quotationCashflow.getVatPrincipal() + quotationCashflow.getVatInterest());
                    } else {
                        if (1L == quotationCashflow.getCfItem()) {
                            quotationCashflow.setNetDueAmount(quotationCashflow.getNetPrincipal() + quotationCashflow.getNetInterest());
                            quotationCashflow.setVatDueAmount(quotationCashflow.getVatPrincipal() + quotationCashflow.getVatInterest());
                        }
                        //留购金日期与期次要与最后一期租金同步
                        if (8L == quotationCashflow.getCfItem()) {
                            Date dueDate = maxCashflow.getDueDate() == null ? maxCashflow.getCalcDate() : maxCashflow.getDueDate();
                            quotationCashflow.setTimes(maxCashflow.getTimes());
                            quotationCashflow.setDueDate(dueDate);
                            quotationCashflow.setCalcDate(dueDate);
                            quotationCashflow.setFinIncomeDate(dueDate);
                        }
                    }
                    hlsCusPrjQuotationCashflowMapper.updateByPrimaryKeySelective(quotationCashflow);
                }
            }

            // 计算IRR、XIRR
            List<HlsCusPrjQuotationCashflow> flows = new ArrayList<>();
            HlsCusPrjQuotationCashflow f = new HlsCusPrjQuotationCashflow();
            f.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            f.setCfItem(1L);
            flows = hlsCusPrjQuotationCashflowService.select(iRequest, f, 1, 999);
            //计算出现金流后，再加上第0期现金流
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
            Double deposit = hlsCusPrjQuotation.getDeposit() == null ? 0 : hlsCusPrjQuotation.getDeposit();
            //首付款
            Double down_payment = hlsCusPrjQuotation.getDownPayment() == null ? 0 : hlsCusPrjQuotation.getDownPayment();
            //手续费
            Double lease_charge = hlsCusPrjQuotation.getLeaseCharge() == null ? 0 : hlsCusPrjQuotation.getLeaseCharge();
            //留购金
            Double residual_value = hlsCusPrjQuotation.getResidualValue() == null ? 0 : hlsCusPrjQuotation.getResidualValue();
            //第0期加上保证金、手续费
            hlsCusPrjQuotationCashflow1.setDueAmount(0 - hlsCusPrjQuotation.getFinanceAmount() + deposit + lease_charge);
            hlsCusPrjQuotationCashflow1.setDueDate(hlsCusPrjQuotation.getLeaseStartDate());
            hlsCusPrjQuotationCashflow1.setTimes(0L);
            flows.add(0, hlsCusPrjQuotationCashflow1);

            //最后一期扣除保证金 加上留购金
            hlsCusPrjQuotationCashflow1 = flows.get(flows.size() - 1);
            hlsCusPrjQuotationCashflow1.setDueAmount(hlsCusPrjQuotationCashflow1.getDueAmount() - deposit + residual_value);

            Double irr = getIrr(flows);
            irr = irr * (12L / Long.valueOf(rentingFrequency));
            hlsCusPrjQuotation.setIrr(irr);

            Double xirr = getXirr(flows);
            hlsCusPrjQuotation.setXirr(xirr);
            hlsCusPrjQuotation.setLeaseTimes(maxCashflow.getTimes());
            prjQuotationMapper.updateByPrimaryKeySelective(hlsCusPrjQuotation);
        } catch (Exception e) {
            throw new ResMessageException(e.getMessage());
        }
    }

    @Override
    public void conTerminateConfirm(IRequest iRequest, Map paraMap) throws HlsCusException {

        Long contractId = Long.valueOf(paraMap.get("contract_id").toString());
        Long quotationId = Long.valueOf(paraMap.get("quotation_id").toString());
        Long projectId = Long.valueOf(paraMap.get("project_id").toString());

        //获取报价原起租日
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);


        //获取现金流
        //项目现金流（虚拟合同）
        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setCfItem(1L);
        prjQuotationCashflow.setQuotationId(quotationId);

        //合同现金流（合同）
        HlsCusConContractCashflow conQuotationCashflow = new HlsCusConContractCashflow();
        conQuotationCashflow.setCfItem(1L);
        conQuotationCashflow.setQuotationId(quotationId);
        List<HlsCusConContractCashflow> conCashFlows = hlsCusConContractCashflowMapper.select(conQuotationCashflow);

        for (HlsCusConContractCashflow conCashFlow : conCashFlows) {
            //判断现金流是否都核销了
            if (!"FULL".equals(conCashFlow.getWriteOffFlag())) {
                throw new RuntimeException("存在未核销的租金现金流");
            }
        }

        //更新合同状态
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract.setContractStatus("TERMINATE");
        hlsCusConContractMapper.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void conContractChangeSubmitWfl(IRequest iRequest, HlsCusPrjProject prjProject) {
        //直接发起变更
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(prjProject);
        /*修改审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        //hlsCusChangeReqInfo.setDocumentCategory("CON_CONTRACT");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus("APPROVING");
        hlsCusChangeReqInfo.setWflNodeStatus(null);
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.updateByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        HlsCusPrjProject prjProjectOld = new HlsCusPrjProject();
        prjProjectOld.setProjectId(hlsCusChangeReqInfo.getDocumentId());
        prjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, prjProjectOld);
        prjProjectOld.setProjectStatus("PENDING");
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProjectOld);
        prjProject.setRefProjectId(prjProjectOld.getProjectId());
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //还款计划变更与提前结清校验是否已经计算
        String calcFlag = "Y";
        int irrCompare = 0;
        if ("REPAYMENT_SCHEDULE".equals(hlsCusChangeReqInfo.getChangeType())) {
            ConChangeRepaymentInfo conChangeRepaymentInfo = new ConChangeRepaymentInfo();
            conChangeRepaymentInfo.setProjectId(prjProject.getProjectId());
            conChangeRepaymentInfo = conChangeRepaymentInfoMapper.select(conChangeRepaymentInfo).get(0);
            calcFlag = conChangeRepaymentInfo.getCalcFlag();
        } else if ("ET".equals(hlsCusChangeReqInfo.getChangeType())) {
            ConChangeEtInfo conChangeEtInfo = new ConChangeEtInfo();
            conChangeEtInfo.setProjectId(prjProject.getProjectId());
            conChangeEtInfo = conChangeEtInfoMapper.select(conChangeEtInfo).get(0);
            calcFlag = conChangeEtInfo.getCalcFlag();
        }
        if ("BUSINESS_CHANGE_BEFORE".equals(hlsCusChangeReqInfo.getChangeType()) || "REPAYMENT_SCHEDULE".equals(hlsCusChangeReqInfo.getChangeType())) {
            //获取变更前后报价,比较irr
            Long oldProjectId = prjProjectOld.getProjectId();
            Long newProjectId = prjProject.getProjectId();
            HlsCusPrjQuotation oldQuotation = new HlsCusPrjQuotation();
            HlsCusPrjQuotation newQuotation = new HlsCusPrjQuotation();
            oldQuotation.setSourceDocumentCategory("PRJ_PROJECT");
            oldQuotation.setDataClass("VIRTUAL_CON");
            oldQuotation.setSourceDocumentId(oldProjectId);
            newQuotation.setSourceDocumentCategory("PRJ_PROJECT");
            newQuotation.setDataClass("VIRTUAL_CON");
            newQuotation.setSourceDocumentId(newProjectId);
            oldQuotation = hlsCusPrjQuotationMapper.select(oldQuotation).get(0);
            newQuotation = hlsCusPrjQuotationMapper.select(newQuotation).get(0);
            BigDecimal oldIrr = BigDecimal.valueOf(oldQuotation.getIrr());
            BigDecimal newIrr = BigDecimal.valueOf(newQuotation.getIrr());
            irrCompare = newIrr.compareTo(oldIrr);
        }
        if (!"Y".equals(calcFlag)) {
            throw new RuntimeException("请先计算后再提交！");
        }
        if (irrCompare < 0) {
            throw new RuntimeException("当前irr小于原irr,不可提交！");
        }

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CON_CONTRACT_CHANGE_WFL");
        params.put("wflKey", "CON_CONTRACT_CHANGE_WFL");

        params.put("changeType", hlsCusChangeReqInfo.getChangeType());
        //是否上会
        params.put("meetFlag", hlsCusChangeReqInfo.getMeetFlag());
        params.put("changeReqId", hlsCusChangeReqInfo.getChangeReqId());
        params.put("projectId", prjProject.getProjectId());

        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

        prjProject.setProjectStatus("APPROVING");
        HlsCusPrjProject getPrj = new HlsCusPrjProject();
        getPrj.setProjectId(prjProject.getProjectId());
        prjProject.setObjectVersionNumber(hlsCusPrjProjectService.selectByPrimaryKey(iRequest, getPrj).getObjectVersionNumber());
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, getPrj);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjProjectOld.getProjectName() + "项目的合同变更" + prjProjectOld.getProjectNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "合同变更");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, prjProject.getProjectId(), prjProject.getDocumentCategory(), prjProject.getDocumentType(), "BAC", "CON_CONTRACT_WFL", "P2D", paramsEvent);
    }

    /**
     * 还款计划变更计算
     *
     * @param req
     * @param hlsCusConContract
     * @return
     */
    @SneakyThrows
    @Override
    public boolean contractChangeRepaymentCalculate(IRequest req, HlsCusConContract hlsCusConContract) {

        //每次计算在最原始的现金流上计算,将当前变更的数据恢复
        //当前变更中的虚拟合同
        HlsCusPrjProject cProject = new HlsCusPrjProject();
        cProject.setProjectId(hlsCusConContract.getProjectId());
        cProject = hlsCusPrjProjectService.selectByPrimaryKey(req, cProject);
        //原虚拟合同
        HlsCusPrjProject oProject = new HlsCusPrjProject();
        oProject.setProjectId(cProject.getRefProjectId());
        oProject = hlsCusPrjProjectService.selectByPrimaryKey(req, oProject);
        //变更单据中报价
        HlsCusPrjQuotation cQuotation = new HlsCusPrjQuotation();
        cQuotation.setSourceDocumentId(cProject.getProjectId());
        cQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        cQuotation.setDataClass("VIRTUAL_CON");
        cQuotation = hlsCusPrjQuotationService.select(req, cQuotation, 1, 999).get(0);
        //原单据报价
        HlsCusPrjQuotation oQuotation = new HlsCusPrjQuotation();
        oQuotation.setSourceDocumentId(oProject.getProjectId());
        oQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        oQuotation.setDataClass("VIRTUAL_CON");
        oQuotation = hlsCusPrjQuotationService.select(req, oQuotation, 1, 999).get(0);
        //先删除变更单据的现金流
        HlsCusPrjQuotationCashflow flow = new HlsCusPrjQuotationCashflow();
        flow.setQuotationId(cQuotation.getQuotationId());
        hlsCusPrjQuotationCashflowService.batchDelete(hlsCusPrjQuotationCashflowService.select(req, flow, 1, 999));
        //现金流从原单据复制到变更中的单据
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(oQuotation.getQuotationId());
        hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowService.select(req, hlsCusPrjQuotationCashflow, 1, 999);
        for (HlsCusPrjQuotationCashflow dto : hlsCusPrjQuotationCashflows) {
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
            hlsCusPrjQuotationCashflow1.setQuotationId(cQuotation.getQuotationId());
            hlsCusPrjQuotationCashflowService.insertSelective(req, hlsCusPrjQuotationCashflow1);
        }
        //获取合同
        HlsCusConContract contract = new HlsCusConContract();
        contract.setProjectId(oProject.getProjectId());
        contract = self().select(req, contract, 1, 999).get(0);
        //获取合同租金现金流
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(contract.getContractId());
        hlsCusConContractCashflow.setCfItem(1L);
        List<HlsCusConContractCashflow> conCashFlowList = hlsCusConContractCashflowService.select(req, hlsCusConContractCashflow, 1, 999);

        //获取已核销的期次
        Long writeOffTimes = 0L;
        for (HlsCusConContractCashflow conFlow : conCashFlowList) {
            if ("FULL".equals(conFlow.getWriteOffFlag()) && conFlow.getTimes() > writeOffTimes) {
                writeOffTimes = conFlow.getTimes();
            }
        }

        //计算
        //获取还款计划变更信息
        ConChangeRepaymentInfo conChangeRepaymentInFo = new ConChangeRepaymentInfo();
        conChangeRepaymentInFo.setProjectId(cProject.getProjectId());
        conChangeRepaymentInFo = conChangeRepaymentInfoService.select(req, conChangeRepaymentInFo, 1, 999).get(0);

        //获取最近后一期的现金流
        HlsCusPrjQuotationCashflow afterFlow = hlsCusPrjQuotationCashflowMapper.queryAfterChangeDateCashflow(cQuotation.getQuotationId(), conChangeRepaymentInFo.getChangeStartDate());

        //获取剩余本金
        Double remainPrincipal = afterFlow.getOutstandingPrincipal() + afterFlow.getPrincipal();

        //获取剩余期次
        long remainTimes = conChangeRepaymentInFo.getAfterTotalTimes() - afterFlow.getTimes() + 1;

        //变更开始期次
        long startTimes = afterFlow.getTimes();

        //获取年利率
        Double year_rate = oQuotation.getIntRate();

        //付款频率 1:月付 3:季付 6:半年付 12:年付
        String rentingFrequency = oQuotation.getRentingFrequency();

        //手续费
        Double ccrFee = conChangeRepaymentInFo.getCcrFee();

        //校验提前结清之前一期是否核销
        long changeBeforeTime = startTimes - 1;
        if (writeOffTimes != changeBeforeTime) {
            throw new HlsCusException("第" + changeBeforeTime + "期现金流未核销");
        }

        //删除变更开始及之后期次的现金流
        hlsCusPrjQuotationCashflowMapper.deleteChengeTimeAfterCashflow(cQuotation.getQuotationId(), startTimes);

        //判断报价方案 以剩余本金与剩余期次进行还本付息测算
        if ("GECALCULATOR_PMT_YH".equals(oQuotation.getPriceList())) {
            //等额租金
            //pmt=(pv*rt*((rt + 1)^Tn) / ((rt + 1)^Tn - 1)
            //ipmt=(pv*rt*((rt + 1)^(Tn + 1) - (rt + 1)^n)) / ((rt + 1)* ((rt + 1)^Tn - 1))

            Long pmt_frequency = 12 / Long.valueOf(rentingFrequency);

            //每一期还款
            double per_times_amount = 0D;
            if ("YEAR/TIMES".equals(cQuotation.getPaymentMethod())) {
                //年利率/每年还款次数
                per_times_amount = (Math.round(((remainPrincipal * (year_rate / pmt_frequency) * Math.pow(1 + year_rate / pmt_frequency, remainTimes)) / (Math.pow(1 + year_rate / pmt_frequency, remainTimes) - 1)) * 100)) / 100.0;
            } else {
                //年利率/360*每期实际天数
                double a = Math.pow((1 + year_rate / 360 * 365 / 12), new Double(rentingFrequency)) - 1;
                per_times_amount = (Math.round(((remainPrincipal * (a) * Math.pow(1 + a, remainTimes)) / (Math.pow(1 + a, remainTimes) - 1)) * 100)) / 100.0;
            }


            Double vatRate = cQuotation.getVatRate();//税率

            Date preFlowDueDate = null; //上一期应收日期
            double preOutstandingPrincipal = remainPrincipal; //上一期剩余本金

            double principalSum = 0D;
            //测算现金流
            for (int i = 0; i < remainTimes; i++) {
                //本期利息
                double interest = 0D;
                if ("YEAR/TIMES".equals(cQuotation.getPaymentMethod())) {
                    //年利率/每年还款次数
                    interest = Math.round(((remainPrincipal * (year_rate / pmt_frequency) * (Math.pow(1 + year_rate / pmt_frequency, remainTimes + 1) - Math.pow(1 + year_rate / pmt_frequency, i + 1))) / ((1 + year_rate / pmt_frequency) * (Math.pow(1 + year_rate / pmt_frequency, remainTimes) - 1))) * 100) / 100.0;
                } else {
                    //年利率/360*每期实际天数
                    double a = Math.pow((1 + year_rate / 360 * 365 / 12), new Double(rentingFrequency)) - 1;
                    interest = Math.round(((remainPrincipal * (a) * (Math.pow(1 + a, remainTimes + 1) - Math.pow(1 + a, i + 1))) / ((1 + a) * (Math.pow(1 + a, remainTimes) - 1))) * 100) / 100.0;
                }

                //本期本金
                double payment_principal = per_times_amount - interest;  //每一期本金
                //最后一期单独处理,本金用剩余本金减去之前总本金
                if (i == remainTimes - 1) {
                    payment_principal = remainPrincipal - principalSum;
                    per_times_amount = payment_principal + interest;
                }
                principalSum += payment_principal;
                HlsCusPrjQuotationCashflow newFlow = new HlsCusPrjQuotationCashflow();

                //期次从变更开始期次计算
                newFlow.setTimes(i + startTimes);
                newFlow.setPrincipal(payment_principal);
                newFlow.setInterest(interest);
                newFlow.setDueAmount(per_times_amount);

                double netPrincipal = payment_principal / (1 + vatRate);
                double netInterest = interest / (1 + vatRate);
                double netDueAmount = netPrincipal + netInterest;
                newFlow.setNetPrincipal(netPrincipal);//不含税本金
                newFlow.setNetInterest(netInterest);//不含税利息
                newFlow.setNetDueAmount(netDueAmount);//不含税租金
                newFlow.setVatPrincipal(payment_principal - netPrincipal);
                newFlow.setVatInterest(interest - netInterest);
                newFlow.setVatDueAmount(per_times_amount - netDueAmount);

                newFlow.setOutstandingPrincipal(0D);

                newFlow.setCfItem(1L);
                newFlow.setCfType(1L);
                newFlow.setCfDirection("INFLOW");
                newFlow.setCfStatus("RELEASE");

                newFlow.setQuotationId(cQuotation.getQuotationId());

                //还款日 若是开始生成第一期,则直接去取开始变更期次现金流的日期, 否则取上一期生成的现金流日期去计算
                Date dueDate;
                double outstandingPrincipal = 0D;
                if (i == 0) {
                    dueDate = afterFlow.getDueDate();
                } else {
                    dueDate = CalculateDate(rentingFrequency, preFlowDueDate);
                    outstandingPrincipal = preOutstandingPrincipal - payment_principal;
                }
                //暂存本次生成的现金流日期,用于计算下一期的现金流日期
                preFlowDueDate = dueDate;
                preOutstandingPrincipal = outstandingPrincipal;
                newFlow.setDueDate(dueDate);
                newFlow.setFinIncomeDate(dueDate);
                newFlow.setOutstandingPrincipal(outstandingPrincipal);

                hlsCusPrjQuotationCashflowService.insertSelective(req, newFlow);

                //变更首期处理手续费
                if (i == 0 && ccrFee != null && ccrFee > 0) {
                    HlsCusPrjQuotationCashflow sxFlow = new HlsCusPrjQuotationCashflow();
                    sxFlow.setCfItem(3L);
                    sxFlow.setCfType(3L);
                    sxFlow.setQuotationId(cQuotation.getQuotationId());
                    //lgFlow = hlsCusPrjQuotationCashflowMapper.select(lgFlow).get(0);
                    sxFlow.setDueDate(newFlow.getDueDate());
                    sxFlow.setTimes(newFlow.getTimes());
                    sxFlow.setCfDirection("INFLOW");
                    sxFlow.setCfStatus("RELEASE");
                    sxFlow.setDueAmount(ccrFee);
                    sxFlow.setNetDueAmount(ccrFee / (1 + vatRate));
                    sxFlow.setVatDueAmount(ccrFee - newFlow.getNetDueAmount());
                    hlsCusPrjQuotationCashflowService.insertSelective(req, sxFlow);
                }

                //最后一期更新留购价现金流日期和期次
                if (i == remainTimes - 1L) {
                    HlsCusPrjQuotationCashflow lgFlow = new HlsCusPrjQuotationCashflow();
                    lgFlow.setCfItem(8L);
                    lgFlow.setCfType(8L);
                    lgFlow.setQuotationId(cQuotation.getQuotationId());
                    lgFlow = hlsCusPrjQuotationCashflowMapper.select(lgFlow).get(0);
                    lgFlow.setDueDate(newFlow.getDueDate());
                    lgFlow.setTimes(newFlow.getTimes());
                    hlsCusPrjQuotationCashflowService.updateByPrimaryKeySelective(req, lgFlow);
                }
            }
        } else if ("GECALCULATOR_LP_YH".equals(oQuotation.getPriceList())) {
            //等额本金
            Double vatRate = cQuotation.getVatRate();//税率
            Date preFlowDueDate = null; //上一期应收日期
            double preOutstandingPrincipal = remainPrincipal; //上一期剩余本金

            //每期本金
            //double per_times_principal = remainPrincipal/remainTimes;
            BigDecimal ptp = BigDecimal.valueOf(remainPrincipal).divide(BigDecimal.valueOf(remainTimes)).setScale(2, BigDecimal.ROUND_HALF_UP);
            double per_times_principal = ptp.doubleValue();

            double principalSum = 0D;
            for (int i = 0; i < remainTimes; i++) {
                //最后一期单独处理,本金倒减
                if (i == remainTimes - 1) {
                    per_times_principal = remainPrincipal - principalSum;
                }

                principalSum += per_times_principal;

                //本期利息 上一期剩余本金*年利率*租金收取频率/12
                double interest = 0D;
                if ("YEAR/TIMES".equals(cQuotation.getPaymentMethod())) {
                    //年利率/每年还款次数
                    interest = preOutstandingPrincipal * year_rate * Long.valueOf(rentingFrequency) / 12;
                } else {
                    //年利率/360*每期实际天数
                    double r = Math.pow((1 + year_rate / 360 * 365 / 12), new Double(rentingFrequency)) - 1;
                    interest = preOutstandingPrincipal * r;
                }


                HlsCusPrjQuotationCashflow newFlow = new HlsCusPrjQuotationCashflow();

                newFlow.setPrincipal(per_times_principal);

                //期次从变更开始期次计算
                newFlow.setTimes(i + startTimes);
                newFlow.setPrincipal(per_times_principal);
                newFlow.setInterest(interest);
                newFlow.setDueAmount(interest + per_times_principal);

                double netPrincipal = per_times_principal / (1 + vatRate);
                double netInterest = interest / (1 + vatRate);
                double netDueAmount = netPrincipal + netInterest;
                newFlow.setNetPrincipal(netPrincipal);//不含税本金
                newFlow.setNetInterest(netInterest);//不含税利息
                newFlow.setNetDueAmount(netDueAmount);//不含税租金
                newFlow.setVatPrincipal(per_times_principal - netPrincipal);
                newFlow.setVatInterest(interest - netInterest);
                newFlow.setVatDueAmount(interest + per_times_principal - netDueAmount);

                newFlow.setCfItem(1L);
                newFlow.setCfType(1L);
                newFlow.setCfDirection("INFLOW");
                newFlow.setCfStatus("RELEASE");

                newFlow.setQuotationId(cQuotation.getQuotationId());


                //还款日 若是开始生成第一期,则直接去取开始变更期次现金流的日期, 否则取上一期生成的现金流日期去计算
                Date dueDate;
                //double outstandingPrincipal = afterFlow.getDueAmount();
                if (i == 0) {
                    dueDate = afterFlow.getDueDate();
                } else {
                    dueDate = CalculateDate(rentingFrequency, preFlowDueDate);
                }
                double outstandingPrincipal = preOutstandingPrincipal - per_times_principal;
                //暂存本次生成的现金流日期,用于计算下一期的现金流日期
                preFlowDueDate = dueDate;
                preOutstandingPrincipal = outstandingPrincipal;
                newFlow.setDueDate(dueDate);
                newFlow.setFinIncomeDate(dueDate);
                newFlow.setOutstandingPrincipal(outstandingPrincipal);

                hlsCusPrjQuotationCashflowService.insertSelective(req, newFlow);

                //变更首期处理手续费
                if (i == 0 && ccrFee != null && ccrFee > 0) {
                    HlsCusPrjQuotationCashflow sxFlow = new HlsCusPrjQuotationCashflow();
                    sxFlow.setCfItem(3L);
                    sxFlow.setCfType(3L);
                    sxFlow.setQuotationId(cQuotation.getQuotationId());
                    //lgFlow = hlsCusPrjQuotationCashflowMapper.select(lgFlow).get(0);
                    sxFlow.setDueDate(newFlow.getDueDate());
                    sxFlow.setTimes(newFlow.getTimes());
                    sxFlow.setCfDirection("INFLOW");
                    sxFlow.setCfStatus("RELEASE");
                    sxFlow.setDueAmount(ccrFee);
                    sxFlow.setNetDueAmount(ccrFee / (1 + vatRate));
                    sxFlow.setVatDueAmount(ccrFee - newFlow.getNetDueAmount());
                    hlsCusPrjQuotationCashflowService.insertSelective(req, sxFlow);
                }

                //最后一期更新留购价现金流日期和期次
                if (i == remainTimes - 1L) {
                    HlsCusPrjQuotationCashflow lgFlow = new HlsCusPrjQuotationCashflow();
                    lgFlow.setCfItem(8L);
                    lgFlow.setCfType(8L);
                    lgFlow.setQuotationId(cQuotation.getQuotationId());
                    lgFlow = hlsCusPrjQuotationCashflowMapper.select(lgFlow).get(0);
                    lgFlow.setDueDate(newFlow.getDueDate());
                    lgFlow.setTimes(newFlow.getTimes());
                    hlsCusPrjQuotationCashflowMapper.updateByPrimaryKeySelective(lgFlow);
                }
            }

        }

        //更新xirr
        List<HlsCusPrjQuotationCashflow> flows = new ArrayList<>();
        HlsCusPrjQuotationCashflow f = new HlsCusPrjQuotationCashflow();
        f.setQuotationId(cQuotation.getQuotationId());
        f.setCfItem(1L);
        flows = hlsCusPrjQuotationCashflowService.select(req, f, 1, 999);

        //计算出现金流后，再加上第0期现金流
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
        Double deposit = cQuotation.getDeposit() == null ? 0 : cQuotation.getDeposit();
        //第0期加上保证金
        hlsCusPrjQuotationCashflow1.setDueAmount(0 - cQuotation.getFinanceAmount() + deposit);
        hlsCusPrjQuotationCashflow1.setDueDate(cQuotation.getLeaseStartDate());
        hlsCusPrjQuotationCashflow1.setTimes(0L);
        /*hlsCusPrjQuotationCashflow1.setQuotationId(cQuotation.getQuotationId());
        hlsCusPrjQuotationCashflow1.setTimes(0L);
        hlsCusPrjQuotationCashflow1.setCfItem(0L);
        hlsCusPrjQuotationCashflow1.setCfType(0L);
        hlsCusPrjQuotationCashflow1.setCfDirection("OUTFLOW");
        hlsCusPrjQuotationCashflow1 = hlsCusPrjQuotationCashflowMapper.select(hlsCusPrjQuotationCashflow1).get(0);
        hlsCusPrjQuotationCashflow1.setDueAmount(-hlsCusPrjQuotationCashflow1.getDueAmount());*/
        flows.add(0, hlsCusPrjQuotationCashflow1);

        //最后一期扣除保证金
        hlsCusPrjQuotationCashflow1 = flows.get(flows.size() - 1);
        hlsCusPrjQuotationCashflow1.setDueAmount(hlsCusPrjQuotationCashflow1.getDueAmount() - deposit);

        //手续费处理
        List<HlsCusPrjQuotationCashflow> feeFlows = new ArrayList<>();
        HlsCusPrjQuotationCashflow f2 = new HlsCusPrjQuotationCashflow();
        f2.setQuotationId(cQuotation.getQuotationId());
        f2.setCfItem(3L);
        feeFlows = hlsCusPrjQuotationCashflowService.select(req, f2, 1, 999);
        //手续费同期现金流金额合并
        for (HlsCusPrjQuotationCashflow feeFlow : feeFlows) {
            List<HlsCusPrjQuotationCashflow> rentFlows = flows.stream().filter(rentf -> rentf.getTimes() == feeFlow.getTimes()).collect(Collectors.toList());
            if (rentFlows.size() == 0) {
                continue;
            }
            HlsCusPrjQuotationCashflow rentFlow = rentFlows.get(0);
            rentFlow.setDueAmount(rentFlow.getDueAmount() + feeFlow.getDueAmount());
        }

        // 计算IRR
        Double irr = getIrr(flows);
        irr = irr * (12L / Long.valueOf(rentingFrequency));
        cQuotation.setIrr(irr);

        // 计算XIRR
        Double xirr = getXirr(flows);
        cQuotation.setXirr(xirr);
        hlsCusPrjQuotationService.updateByPrimaryKeySelective(req, cQuotation);

        //更新还款计划变更信息表 变更起始期数 变更后总期数 变更后剩余期数 缩期/展期
        conChangeRepaymentInFo.setChangeStartTimes(startTimes);
        conChangeRepaymentInFo.setAfterTotalTimes(startTimes - 1 + remainTimes);
        conChangeRepaymentInFo.setAfterRemainTimes(remainTimes);
        conChangeRepaymentInFo.setNewXirr(xirr);
        if (conChangeRepaymentInFo.getAfterTotalTimes() > conChangeRepaymentInFo.getBeforeTotalTimes()) {
            conChangeRepaymentInFo.setRepaymentChangeType("extension");
        } else {
            conChangeRepaymentInFo.setRepaymentChangeType("contraction");
        }

        //跟新还款计划比对字段
        Map resMap = hlsCusPrjQuotationCashflowMapper.selectQuotationCompareInfo(cQuotation.getQuotationId()).get(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        conChangeRepaymentInFo.setAfterIrr(Double.valueOf(resMap.get("irr").toString()));
        conChangeRepaymentInFo.setAfterLeaseEndDate(sdf.parse(resMap.get("lease_end_date").toString()));
        conChangeRepaymentInFo.setAfterTotalAmount(Double.valueOf(resMap.get("total_amount").toString()));
        conChangeRepaymentInFo.setAfterTotalPrincipal(Double.valueOf(resMap.get("total_principal").toString()));
        conChangeRepaymentInFo.setAfterTotalInterest(Double.valueOf(resMap.get("total_interest").toString()));
        conChangeRepaymentInFo.setAfterTotalFee(Double.valueOf(resMap.get("total_fee").toString()));
        conChangeRepaymentInFo.setAfterTotalDeposit(Double.valueOf(resMap.get("total_deposit").toString()));
        conChangeRepaymentInFo.setCalcFlag("Y");
        conChangeRepaymentInfoService.updateByPrimaryKey(req, conChangeRepaymentInFo);

        return true;
    }

    /**
     * 提前结清计算
     *
     * @param req
     * @param hlsCusConContract
     * @return
     */
    @SneakyThrows
    @Override
    public boolean contractChangeEtCalculate(IRequest req, HlsCusConContract hlsCusConContract) {
        /*
            尚未偿还租赁本金总额：取值提前结清日之前最接近的那期现金流的剩余本金，之前的现金流须完成还款且核销；
            至提前结清日的未偿利息：上期剩余本金*年利率/360*上一期还款日到提前结清日天数
            提前结清手续费：默认为租赁本金余额*年利率*30/360
            实际应结清金额根据公式自动得出：实际应结清金额=尚未偿还租赁本金余额+至提前结清日的未偿利息+留购价款+提前结清手续费+其他应付款-减免金额）。
        */

        //校验

        //每次计算在最原始的现金流上计算,将当前变更的数据恢复
        //当前变更中的虚拟合同
        HlsCusPrjProject cProject = new HlsCusPrjProject();
        cProject.setProjectId(hlsCusConContract.getProjectId());
        cProject = hlsCusPrjProjectService.selectByPrimaryKey(req, cProject);
        //原虚拟合同
        HlsCusPrjProject oProject = new HlsCusPrjProject();
        oProject.setProjectId(cProject.getRefProjectId());
        oProject = hlsCusPrjProjectService.selectByPrimaryKey(req, oProject);
        //变更单据中报价
        HlsCusPrjQuotation cQuotation = new HlsCusPrjQuotation();
        cQuotation.setSourceDocumentId(cProject.getProjectId());
        cQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        cQuotation.setDataClass("VIRTUAL_CON");
        cQuotation = hlsCusPrjQuotationService.select(req, cQuotation, 1, 999).get(0);
        //原单据报价
        HlsCusPrjQuotation oQuotation = new HlsCusPrjQuotation();
        oQuotation.setSourceDocumentId(oProject.getProjectId());
        oQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        oQuotation.setDataClass("VIRTUAL_CON");
        oQuotation = hlsCusPrjQuotationService.select(req, oQuotation, 1, 999).get(0);
        //先删除变更单据的现金流
        HlsCusPrjQuotationCashflow flow = new HlsCusPrjQuotationCashflow();
        flow.setQuotationId(cQuotation.getQuotationId());
        hlsCusPrjQuotationCashflowService.batchDelete(hlsCusPrjQuotationCashflowService.select(req, flow, 1, 999));
        //现金流从原单据复制到变更中的单据
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setProjectId(cProject.getRefProjectId());
        conContract = hlsCusConContractMapper.select(conContract).get(0);

        HlsCusConContractCashflow hlsCusConContractCashflowQuery = new HlsCusConContractCashflow();
        hlsCusConContractCashflowQuery.setContractId(conContract.getContractId());
        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = hlsCusConContractCashflowService.select(req, hlsCusConContractCashflowQuery, 1, 100000);
        Double residualValue = 0D;
        for (HlsCusConContractCashflow cashflow : hlsCusConContractCashflowList) {
            if (cashflow.getCfItem() == 8) {
                residualValue = cashflow.getDueAmount();
            }
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(cashflow);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
            hlsCusPrjQuotationCashflow1.setQuotationId(cQuotation.getQuotationId());
            hlsCusPrjQuotationCashflowService.insertSelective(req, hlsCusPrjQuotationCashflow1);
        }

//        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
//        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
//        hlsCusPrjQuotationCashflow.setQuotationId(oQuotation.getQuotationId());
//        hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowService.select(req, hlsCusPrjQuotationCashflow, 1, 999);
//        for (HlsCusPrjQuotationCashflow dto : hlsCusPrjQuotationCashflows) {
//            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
//            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
//            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
//            hlsCusPrjQuotationCashflow1.setQuotationId(cQuotation.getQuotationId());
//            hlsCusPrjQuotationCashflowService.insertSelective(req, hlsCusPrjQuotationCashflow1);
//        }
        //获取合同
        HlsCusConContract contract = new HlsCusConContract();
        contract.setProjectId(oProject.getProjectId());
        contract = self().select(req, contract, 1, 999).get(0);
        //获取合同租金现金流
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(contract.getContractId());
        hlsCusConContractCashflow.setCfItem(1L);
        List<HlsCusConContractCashflow> conCashFlowList = hlsCusConContractCashflowService.select(req, hlsCusConContractCashflow, 1, 999);

        //获取已核销的期次
        long writeOffTimes = 0L;
        for (HlsCusConContractCashflow conFlow : conCashFlowList) {
            if ("FULL".equals(conFlow.getWriteOffFlag()) && conFlow.getTimes() > writeOffTimes) {
                writeOffTimes = conFlow.getTimes();
            }
        }

        //计算
        //获取提前结清信息
        ConChangeEtInfo conChangeEtInFo = new ConChangeEtInfo();
        conChangeEtInFo.setProjectId(cProject.getProjectId());
        conChangeEtInFo = conChangeEtInfoSerivce.select(req, conChangeEtInFo, 1, 999).get(0);

        //获取年利率
        BigDecimal intRate = BigDecimal.valueOf(contract.getIntRate());
        //日利率
        BigDecimal dayRate = intRate.divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);

        //获取最近前一期的现金流
        HlsCusPrjQuotationCashflow beforeFlow = hlsCusPrjQuotationCashflowMapper.queryBeforeEtDateCashflow(cQuotation.getQuotationId(), conChangeEtInFo.getEtDate());

        //提前结清期次(最后一期核销)
        long etTimes = 1L;
        if (beforeFlow != null) {
            etTimes = beforeFlow.getTimes();
        }

        //校验提前结清之前一期是否核销
        long etBeforeTime = etTimes;
        if (writeOffTimes != etBeforeTime) {
            throw new HlsCusException("请选择第" + writeOffTimes + "期之后的时间");
        }

        //尚未偿还租赁本金余额
        BigDecimal unreceivedPrincipal = new BigDecimal("0");
        //至提前结清日的未偿利息 上期剩余本金*年利率/360*上一期还款日到提前结清日天数
        BigDecimal etInterest = new BigDecimal("0");
        long calcEtInterestDays = 1;
        if (beforeFlow != null) {
            calcEtInterestDays = calcEtInterestDays + daysBetween(beforeFlow.getDueDate(), conChangeEtInFo.getEtDate());
            unreceivedPrincipal = BigDecimal.valueOf(beforeFlow.getOutstandingPrincipal());
            etInterest = unreceivedPrincipal.multiply(dayRate).multiply(new BigDecimal(calcEtInterestDays));
        } else {
            calcEtInterestDays = calcEtInterestDays + daysBetween(conChangeEtInFo.getLeaseStartDate(), conChangeEtInFo.getEtDate());
            unreceivedPrincipal = BigDecimal.valueOf(contract.getFinanceAmount());
            etInterest = unreceivedPrincipal.multiply(dayRate).multiply(new BigDecimal(calcEtInterestDays));
        }

        //留购价款
        BigDecimal nominalCost = new BigDecimal("0");
        nominalCost = BigDecimal.valueOf(residualValue);
//        if(oQuotation.getResidualValue() != null){
//            nominalCost = BigDecimal.valueOf(oQuotation.getResidualValue());
//        }

        //提前结清手续费 如果不为空就直接使用,空则取 租赁本金余额*年利率*30/360
        BigDecimal etFee = new BigDecimal("0");
        if (conChangeEtInFo.getEtFee() != null) {
            etFee = BigDecimal.valueOf(conChangeEtInFo.getEtFee());
        } else {
            etFee = unreceivedPrincipal.multiply(intRate).multiply(new BigDecimal("30")).divide(new BigDecimal("360"), 6, RoundingMode.HALF_UP);
        }

        //减免金额
        BigDecimal reduceAmount = new BigDecimal("0");
        if (conChangeEtInFo.getReduceAmount() != null) {
            reduceAmount = BigDecimal.valueOf(conChangeEtInFo.getReduceAmount());
        }

        //违约金 判断结清日是否是起租日的12个月后, 是则默认值为0,否则默认值取 剩余本金*30%
        Double penaltyRatio = oProject.getPenaltyRatio();
        BigDecimal liquidatedDamages = new BigDecimal("0");
        if (conChangeEtInFo.getLiquidatedDamages() != null) {
            liquidatedDamages = BigDecimal.valueOf(conChangeEtInFo.getLiquidatedDamages());
        } else if (!timeSpan(conChangeEtInFo.getEtDate(), conChangeEtInFo.getLeaseStartDate())) {
            liquidatedDamages = unreceivedPrincipal.multiply(BigDecimal.valueOf(penaltyRatio));//违约金率修改为虚拟合同维护的
        }

        //实际应结清金额 实际应结清金额=尚未偿还租赁本金余额+至提前结清日的未偿利息+提前结清手续费+违约金 -减免金额
        BigDecimal totalAmount = new BigDecimal("0");
        totalAmount = unreceivedPrincipal.add(etInterest).add(etFee).add(liquidatedDamages).subtract(reduceAmount);

        //生成提前结清现金流
        HlsCusPrjQuotationCashflow etFlow = new HlsCusPrjQuotationCashflow();
        etFlow.setCfItem(11L);
        etFlow.setCfType(11L);
        etFlow.setQuotationId(cQuotation.getQuotationId());
        etFlow.setDueDate(conChangeEtInFo.getEtDate());
        etFlow.setDueAmount(totalAmount.doubleValue());
        etFlow.setCfDirection("INFLOW");
        etFlow.setCfStatus("RELEASE");
        etFlow.setFinIncomeDate(conChangeEtInFo.getEtDate());
        etFlow.setTimes(etTimes + 1);

        Double vatRate = cQuotation.getVatRate();//税率
        double netPrincipal = etFlow.getDueAmount() / (1 + vatRate);
        etFlow.setNetPrincipal(netPrincipal);//不含税本金
        etFlow.setNetDueAmount(netPrincipal);//不含税租金
        etFlow.setVatPrincipal(etFlow.getDueAmount() - netPrincipal);
        etFlow.setVatDueAmount(etFlow.getDueAmount() - netPrincipal);
        etFlow.setNetInterest(0D);
        etFlow.setVatInterest(0D);
        hlsCusPrjQuotationCashflowService.insertSelective(req, etFlow);

        //删除结清日期之后的现金流
        List<HlsCusPrjQuotationCashflow> cFlows = hlsCusPrjQuotationCashflowService.select(req, flow, 1, 999);
        cFlows = cFlows.stream().filter(f -> f.getTimes() >= etFlow.getTimes() && f.getCfItem() == 1L).collect(Collectors.toList());
        if (cFlows.size() > 0) {
            hlsCusPrjQuotationCashflowService.batchDelete(cFlows);
        }

        //更新留购价现金流日期和期次
        HlsCusPrjQuotationCashflow lgFlow = new HlsCusPrjQuotationCashflow();
        lgFlow.setCfItem(8L);
        lgFlow.setCfType(8L);
        lgFlow.setQuotationId(cQuotation.getQuotationId());
        lgFlow = hlsCusPrjQuotationCashflowMapper.select(lgFlow).get(0);
        lgFlow.setDueDate(etFlow.getDueDate());
        lgFlow.setTimes(etFlow.getTimes());
        hlsCusPrjQuotationCashflowMapper.updateByPrimaryKeySelective(lgFlow);

        //计算xirr
        cFlows = hlsCusPrjQuotationCashflowService.select(req, flow, 1, 999);
        // 循环修改正负号
        for (HlsCusPrjQuotationCashflow item : cFlows) {
            item.setDueAmount(HlsCusConstant.OUTFLOW.equalsIgnoreCase(item.getCfDirection()) ? -1 * item.getDueAmount() : item.getDueAmount());
        }
        //期次排序
        List<HlsCusPrjQuotationCashflow> cashflowSortList = cFlows.stream().sorted(Comparator.comparing(HlsCusPrjQuotationCashflow::getTimes)).collect(Collectors.toList());
        //期数相同合并
        int times = Math.toIntExact(cashflowSortList.get(cashflowSortList.size() - 1).getTimes());
        List<HlsCusPrjQuotationCashflow> endQuotationCashflowList = new ArrayList<>();
        for (int i = 0; i <= times; i++) {
            int finalI = i;
            List<HlsCusPrjQuotationCashflow> collect = cashflowSortList.stream().filter(a -> finalI == a.getTimes().intValue()).collect(Collectors.toList());
            Double dueAmount = 0D;
            Double netDueAmount = 0D;
            Date dueDate = new Date();
            for (HlsCusPrjQuotationCashflow item :
                    collect) {
                dueDate = item.getDueDate();
                dueAmount = HlsCusMathUtil.add(dueAmount, item.getDueAmount());
                netDueAmount = HlsCusMathUtil.add(netDueAmount, item.getNetDueAmount());
            }
            if (i == 0) {
                dueDate = cQuotation.getLeaseStartDate();
            }
            if (collect.size() > 0) {
                collect.get(0).setDueAmount(dueAmount);
                collect.get(0).setNetDueAmount(netDueAmount);
                collect.get(0).setDueDate(dueDate);
                collect.get(0).setTimes(Long.valueOf(i));
                endQuotationCashflowList.add(collect.get(0));
            }
        }
        double[] dueAmountList = new double[endQuotationCashflowList.size()];
        Date[] dueAmountDateList = new Date[endQuotationCashflowList.size()];
        for (int i = 0; i < endQuotationCashflowList.size(); i++) {
            dueAmountDateList[i] = endQuotationCashflowList.get(i).getDueDate();
            dueAmountList[i] = endQuotationCashflowList.get(i).getDueAmount();
            System.out.println("计算参数：");
            System.out.println(i);
            System.out.println(endQuotationCashflowList.get(i).getDueDate());
            System.out.println(endQuotationCashflowList.get(i).getDueAmount());
        }
        Double xirr = HlsCusXirr.Newtons_method(0.1, dueAmountList, dueAmountDateList);
        System.out.println("计算结果：");
        System.out.println(xirr);

        //更新提前结清信息表
        conChangeEtInFo.setTotalAmount(totalAmount.doubleValue());
        conChangeEtInFo.setUnreceivedPrincipal(unreceivedPrincipal.doubleValue());
        conChangeEtInFo.setEtInterest(etInterest.doubleValue());
        conChangeEtInFo.setNominalCost(nominalCost.doubleValue());
        conChangeEtInFo.setEtFee(etFee.doubleValue());
        conChangeEtInFo.setLiquidatedDamages(liquidatedDamages.doubleValue());
        conChangeEtInFo.setReduceAmount(reduceAmount.doubleValue());
        conChangeEtInFo.setNewXirr(xirr);
        conChangeEtInFo.setCalcFlag("Y");
        conChangeEtInFo.setCalcEtInterestDays(calcEtInterestDays);
        conChangeEtInfoSerivce.updateByPrimaryKeySelective(req, conChangeEtInFo);

        return true;
    }

    @Override
    public void conContractChangeCancel(IRequest iRequest, HlsCusPrjProject prjProject) {
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus("CANCEL");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.updateByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        HlsCusPrjProject prjProjectOld = new HlsCusPrjProject();
        prjProjectOld.setProjectId(hlsCusChangeReqInfo.getDocumentId());
        prjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, prjProjectOld);
        prjProjectOld.setProjectStatus("APPROVED");
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProjectOld);
    }

    @SneakyThrows
    @Override
    public boolean compareinfoGenerate(IRequest req, HlsCusConContract hlsCusConContract) {

        ConChangeRepaymentInfo conChangeRepaymentInFo = new ConChangeRepaymentInfo();
        conChangeRepaymentInFo.setProjectId(hlsCusConContract.getProjectId());
        conChangeRepaymentInFo = conChangeRepaymentInfoMapper.select(conChangeRepaymentInFo).get(0);


        // 变更前总期数 还款变更方案
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(hlsCusConContract.getQuotationId());
        hlsCusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);

        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        hlsCusPrjQuotationCashflow.setCfItem(1L);
        hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowMapper.select(hlsCusPrjQuotationCashflow);

        //查询现金流表获取 总期数
        //conChangeRepaymentInFo.setBeforeTotalTimes(Long.valueOf(hlsCusPrjQuotationCashflows.size()));

        //跟新还款计划比对字段
        Map resMap = hlsCusPrjQuotationCashflowMapper.selectQuotationCompareInfo(hlsCusPrjQuotation.getQuotationId()).get(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        conChangeRepaymentInFo.setAfterIrr(Double.valueOf(resMap.get("irr").toString()));
        conChangeRepaymentInFo.setAfterLeaseEndDate(sdf.parse(resMap.get("lease_end_date").toString()));
        conChangeRepaymentInFo.setAfterTotalAmount(Double.valueOf(resMap.get("total_amount").toString()));
        conChangeRepaymentInFo.setAfterTotalPrincipal(Double.valueOf(resMap.get("total_principal").toString()));
        conChangeRepaymentInFo.setAfterTotalInterest(Double.valueOf(resMap.get("total_interest").toString()));
        conChangeRepaymentInFo.setAfterTotalFee(Double.valueOf(resMap.get("total_fee").toString()));
        conChangeRepaymentInFo.setAfterTotalDeposit(Double.valueOf(resMap.get("total_deposit").toString()));
        conChangeRepaymentInFo.setNewXirr(hlsCusPrjQuotation.getXirr());

        conChangeRepaymentInFo.setCalcFlag("Y");

        //变更后总期数 AFTER_TOTAL_TIMES
        conChangeRepaymentInFo.setAfterTotalTimes(Long.valueOf(hlsCusPrjQuotationCashflows.size()));
        //变更起始期数 CHANGE_START_TIMES     获取最近后一期的现金流
        HlsCusPrjQuotationCashflow afterFlow = hlsCusPrjQuotationCashflowMapper.queryAfterChangeDateCashflow(hlsCusPrjQuotation.getQuotationId(), conChangeRepaymentInFo.getChangeStartDate());
        if (afterFlow != null) {
            conChangeRepaymentInFo.setChangeStartTimes(afterFlow.getTimes());
            //变更后剩余期数 AFTER_REMAIN_TIMES 变更后总期数-变更起始期数+1
            conChangeRepaymentInFo.setAfterRemainTimes(conChangeRepaymentInFo.getAfterTotalTimes() - conChangeRepaymentInFo.getChangeStartTimes() + 1);
        }
        //还款变更方案 REPAYMENT_CHANGE_TYPE(extension/contraction)
        if (conChangeRepaymentInFo.getAfterTotalTimes() > conChangeRepaymentInFo.getBeforeTotalTimes()) {
            conChangeRepaymentInFo.setRepaymentChangeType("extension");
        } else {
            conChangeRepaymentInFo.setRepaymentChangeType("contraction");
        }
        //变更手续费 CCR_FEE
        conChangeRepaymentInFo.setCcrFee(Double.valueOf(resMap.get("ccr_fee").toString()));

        conChangeRepaymentInfoService.updateByPrimaryKeySelective(req, conChangeRepaymentInFo);


        return true;
    }

    /**
     * 日期之间天数计算
     */
    private int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 判断时间间隔是否超过12个月
     */
    private boolean timeSpan(Date date1, Date date2) {
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long time = time1 - time2;
        if (time > 31536000000L) {
            return true;
        }
        return false;
    }

    //根据还款频率计算还款计划的还款时间
    private Date CalculateDate(String type, Date date) {
        Calendar calendar = Calendar.getInstance();
        if (type.equals("1")) {
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
        } else if (type.equals("3")) {
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 3);
        } else if (type.equals("6")) {
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 6);
        } else if (type.equals("12")) {
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, 1);
        }
        return calendar.getTime();
    }

    /**
     * 计算IRR
     */
    private Double getIrr(List<HlsCusPrjQuotationCashflow> cashFlowList) {

        List<Double> irrArray = new ArrayList();
        for (int i = 0; i < cashFlowList.size(); i++) {
            irrArray.add(cashFlowList.get(i).getDueAmount());
        }
        Double irr = IrrUtil.irr(irrArray);


        BigDecimal irrRes = new BigDecimal(irr);
        return irrRes.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    /**
     * 计算XIRR
     */
    private Double getXirr(List<HlsCusPrjQuotationCashflow> cashFlowList) {
        double[] payments = new double[cashFlowList.size()];
        Date[] dates = new Date[cashFlowList.size()];
        for (int i = 0; i < cashFlowList.size(); i++) {
            payments[i] = cashFlowList.get(i).getDueAmount();
            dates[i] = cashFlowList.get(i).getDueDate();
        }
        return HlsCusXirr.Newtons_method(0.1, payments, dates);
    }

    /**
     * 根据合同编号查找合同id
     */
    @Override
    public List<HlsCusConContract> queryContractIdByContractNumber(String contractNumber) {
        return hlsCusConContractMapper.queryContractIdByContractNumber(contractNumber);
    }

    @Override
    public List<HlsCusConContract> queryContractInceptInfoMain(IRequest iRequest, HlsCusPrjProject dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusConContract> list = hlsCusConContractMapper.queryContractInceptInfoMain(dto);
        return list;
    }

    //流程编码
    private final static String WORK_FLOW = "CAR_MORTGAGE";
    //流程分类
    private final static String DEMO_NAME = "CAR_MORTGAGE";
    private final static String DOCUMENT_NAME = "车辆业务抵押申请";

    private final static String DOCUMENT_CATEGORY = "CON_CONTRACT";

    private final static String DOCUMENT_TYPE = " CONLB";


    @Override
    public List<HlsCusConContract> submitWfl(HlsCusConContract dto, IRequest requestCtx,HttpServletRequest request) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("workFlowType", WORK_FLOW);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, WORK_FLOW);
        params.put(IActivitiCommonService.DEMO_NAME, DEMO_NAME);
        params.put(IActivitiCommonService.BUSINESS_KEY, dto.getContractId());
        params.put("contractId", dto.getContractId());
        dto = hlsCusConContractMapper.selectByPrimaryKey(dto);
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(dto.getTenantId());
        hlsCusBpMaster =  hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        //单据类别
        params.put("documentCategory",DOCUMENT_CATEGORY);
        //单据类型
        params.put("documentType", DOCUMENT_TYPE);
        //单据名称
        params.put("documentName", dto.getContractNumber()+hlsCusBpMaster.getBpName()+DOCUMENT_NAME);
        //单据编号
        params.put("documentNumber", dto.getContractNumber());
        //查询
        List<HlsCusConContract> res = new ArrayList<>();
        res.add(dto);
        activitiStartService.start(requestCtx, res, params);
//         设置状态为提交审批
//        dto.setMortgageStatus("APPROVING");
//        updateByPrimaryKeySelective(requestCtx, dto);
        return res;
    }


    @Override
    public void updateZdwRegisterStatus(Long contractId, HttpServletRequest request) {
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(contractId);
        hlsCusConContract =  hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContract);
        hlsCusConContract.setZdwRegisterStatus("REVOKED");
        hlsCusConContractMapper.updateByPrimaryKey(hlsCusConContract);
    }

    @Override
    public void updateContractStatus(Long contractId, HttpServletRequest request) throws hls.core.utils.exception.HlsCusException {
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(contractId);
        hlsCusConContract =  hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContract);
        hlsCusConContract.setContractStatus("TERMINATE");
        hlsCusConContractMapper.updateByPrimaryKey(hlsCusConContract);

        //如果订单是蚂蚁链已签约，则需调用取消签约
        hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(contractId);
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusConContract.getProjectId());
        if("ACTIVATED".equals(hlsCusPrjProject.getAlipayStatus())){
            iAlipayService.signCancel(hlsCusPrjProject.getProjectId());
        }
    }

    @Override
    public void confirm(Long contractId, HttpServletRequest request) {
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(contractId);
        hlsCusConContract =  hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContract);
        hlsCusConContract.setPledgeFlag("Y");
        hlsCusConContractMapper.updateByPrimaryKey(hlsCusConContract);
    }

    private void commonLog(ResponseData responseData, String code, String returnStatus, String parameter, HlsWsRequests hlsWsRequests) {
        responseData.setCode(code);
        responseData.setMessage(parameter);
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
    }

    private void commonLogHead(HlsWsRequests hlsWsRequests, String functionName, Object param, HttpServletRequest request) {
        //获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
        //功能名称
        hlsWsRequests.setFunctionName(functionName);
        //状态变更日期
        hlsWsRequests.setStatusDate(new Date());
        // user_id
        String userId = request.getParameter("user_id");
        if (userId != null) {
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
        //请求状态
        hlsWsRequests.setStatusCode("200");
        //参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(param);
        hlsWsRequests.setRequestJson(s);
    }

}
