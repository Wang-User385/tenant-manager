package com.hand.hls.prj.service.impl;

import cfca.paperless.base.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsBpMasterRoleService;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.ContentNumberLineMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.cont.service.*;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.mapper.ProjectCreditConditionMapper;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.csh.service.IHlsCusCshPaymentReqLnService;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.*;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.*;
import com.hand.hls.fnd.components.Datasource2Json;
import com.hand.hls.fnd.dto.*;
import com.hand.hls.fnd.mapper.FndCountryMapper;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.fnd.service.IInterfaceErrorMsgService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeTrxDtl;
import com.hand.hls.gld.mapper.ContractFinanceIncomeMapper;
import com.hand.hls.gld.mapper.HlsCusJeHeadMapper;
import com.hand.hls.gld.mapper.JeTrxDtlMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayService;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.mapper.HlsCusFundingPlanMapper;
import com.hand.hls.hls.mapper.HlsDurationHdMapper;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.*;
import com.hand.hls.prj.utils.IDUtils;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.ruleengine.dto.RuleEngineType;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import com.hand.hls.ruleengine.service.IRuleEngineTypeService;
import com.hand.hls.sys.dto.SysDocumentList;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.sys.mapper.SysDocumentListMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.*;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import leaf.service.validation.ParameterNullException;
import lombok.SneakyThrows;
import net.logstash.logback.encoder.org.apache.commons.lang.Validate;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hand.hls.utils.HlsConstantUtil.BaseController.Y;

import static com.hand.hls.utils.HlsCusMathUtil.add;
import static com.hand.hls.utils.HlsCusMathUtil.sub;
import static com.hand.hls.utils.HlsCusMathUtil.mul;
import static com.hand.hls.utils.HlsCusMathUtil.div;
import static com.hand.hls.utils.HlsCusMathUtil.round;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectServiceImpl extends BaseServiceImpl<HlsCusPrjProject> implements HlsCusPrjProjectService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


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

    private static final String TERMINATE = "TERMINATE";
    //税率
    private static final Double OPERATING_LEASE_RATE = 0.13D;
    private static final Double LEASE_RATE = 0.06D;

    private static final String CREDIT_PROJECT_REVIEW_WFL = "CREDIT_PROJECT_REVIEW_WFL";
    private static final String PROJECT_REVIEW_WFL = "PROJECT_REVIEW_WFL";
    private static final String QUOTA_OCCUPATION_WFL = "QUOTA_OCCUPATION_WFL";

    /**
     * 合同变更类型
     * "GUARANTOR" 追加担保
     * "CONTRACTTEXT" 合同文本变更
     * "RENTALPLAN" 租金计划变更
     * "BACKMONEYACCOUNT" 回款账户变更
     * "PREREPAYMENT" 提前还款
     * "BEFORELAUNCH" 投放前合同变更
     * "AFTERAUNCH" 投放后合同变更
     */
    private static final String GUARANTOR = "GUARANTOR";

    private static final String CONTRACTTEXT = "CONTRACTTEXT";

    private static final String RENTALPLAN = "RENTALPLAN";

    private static final String BACKMONEYACCOUNT = "BACKMONEYACCOUNT";

    private static final String PREREPAYMENT = "PREREPAYMENT";
    private static final String BEFORELAUNCH = "BEFORELAUNCH";
    private static final String AFTERLAUNCH = "AFTERLAUNCH";
    private static final String PROJECT_CREDIT = "PROJECT_CREDIT";
    private static final String PROJECT_CREDIT_CHANGE = "PROJECT_CREDIT_CHANGE";

    private static final String PRJ_AMOUNT_ALLOCATE = "PRJ_AMOUNT_ALLOCATE";


    private static final Long ONE = 1L;
    /*
    罚息率默认为0.8%
    调息方式默认为次期
    租赁物性质默认为动产
     */
    private static final Double DEFAULT_PENALTY_RATE = 0.0008D;
    private static final String FLOATING_RANGE_METHOD = "NEXT_YDAY";
    private static final String LEASE_ITEM_TYPE = "MOVABLE";
    private static final Double DEFAULT_ASSETS_SURPLUS_VALUE = 0D;


    private static final String COMPANY_NAME = "CIBFL";
    private static final String COMMON_STR = "-";
    private static final String CONTENT_NUMBER_REQ = "CONTENT_NUMBER_REQ";

    private static final String LON_CONTENT = "LON_CONTENT";
    private static final String ZZ = "ZZ";

    //放款金额
    private static final String RELEASE = "RELEASE";

    //自定义
    private static final String CUSTOMER = "CUSTOMER";

    private static final String PRJ_REVIEW_REPORT = "PRJ_REVIEW_REPORT";

    private static final String PARAM_NOT_FOUND = "参数未找到";
    private static final Long READ_LINE = 0L;
    private static final String PROJECT_TENANT_SHEET = "承租人&进件基本信息";
    private static final String PROJECT_TENANT = "PROJECT_TENANT";
    //承租人
    public static final String TENANT = "TENANT";
    //项目单据类别
    public static final String PROJECT_DOCUMENT_CATEGORY = "PRJ_PROJECT";
    private static final String LEASE_INSURANCE_SHEET = "租赁物&保险信息";
    private static final String LEASE_INSURANCE = "LEASE_INSURANCE";
    //工程机械
    private static final String ENGINEERING_MACHINERY = "ENGINEERING_MACHINERY";
    public static final String QUOTATION_CALC_EXCEPTION = "报价计算失败,请联系管理员!";
    //商用车
    private static final String COMMERCIAL_VEHICLE = "COMMERCIAL_VEHICLE";
    private static final String GUARANTOR_SHEET = "担保人信息";
    private static final String PRJ_PROJECT = "PRJ_PROJECT";
    public static final String NP_ID_CARD_EXCEPTION = "身份证号校验失败!";
    //承租人类别不能为空
    public static final String TENANT_TYPE_NOT_NULL = "承租人类别不能为空";
    private static final String PRJ_PROJECT_ATTACHMENT = "prj_project_attachment";
    public static final String DEALER_TEL_EXCEPTION = "担保人联系方式不得为空";
    //证件类型（身份证）
    private static final String ID_CARD = "ID_CARD";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //商业伙伴单据类别
    public static final String HLS_BP_DOCUMENT_CATEGORY = "HLS_BP_MASTER";
    //法人
    public static final String ORG = "ORG";
    //自然人
    public static final String NP = "NP";
    //项目状态
    public static final String PROJECT_STATUS_NEW = "NEW";
    private static final String INSURANCE_PURCHASE_STATUS = "INSURANCE_PURCHASE_STATUS";
    public static final String ID_CARD_EXCEPTION = "配偶身份证号校验失败!";
    /*
    其他应收现金流期次-1000
     */
    private static final Long otherCashTimes = -1000L;

    /**
     * 工作流相关的常量
     */
    //换行符
    private static final String BR = "<br>";
    /**
     * 用于代码获取工作流提交的实现类
     */
    private static final String PROJECT_SIGN_WORK_FLOW = "PROJECT_SIGN_WORK_FLOW";
    private static final String PROJECT = "project";
    private static final String PROJECT_NAME = "projectName";
    private static final String DOCUMENT_CATEGORY = "documentCategory";
    private static final String DOCUMENT_TYPE = "documentType";
    private static final String DOCUMENT_ID = "documentId";
    private static final String WORKFLOW_TYPE = "workFlowType";
    private static final String DOCUMENT_NAME = "documentName";
    private static final String DOCUMENT_NUMBER = "documentNumber";
    private static final String LEASE_CHANNEL = "leaseChannel";

    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsBpMasterInceptRuleMapper hlsBpMasterInceptRuleMapper;
    @Autowired
    private BpMasterReplyMapper bpMasterReplyMapper;
    @Autowired
    private HlsCodeValueMapper codeValueMapper;
    @Autowired
    private IHlsBpMasterInceptRuleService iHlsBpMasterInceptRuleService;
    @Autowired
    HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private IInterfaceErrorMsgService interfaceErrorMsgService;
    @Autowired
    HlsCusBpMasterMapper hlsBpMasterMapper;
    @Autowired
    private FndCountryMapper fndCountryMapper;
    @Autowired
    private HlsBpMasterAddressMapper hlsBpMasterAddressMapper;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper prjProjectAttachmentMapper;
    @Autowired
    private HlsCusBpMasterService hlsBpMasterService;
    @Autowired
    private IBpMasterChangeReqService bpMasterChangeReqService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusChangeReqInfoMapper hlsCusChangeReqInfoMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private ZxBpOrgbaseMapper zxBpOrgbaseMapper;
    @Autowired
    private HlsBpMasterMainMembersMapper hlsBpMasterMainMembersMapper;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemService hlsCusPrjProjectLeaseItemService;
    @Autowired
    private HlsCusPrjProjectMortgageService hlsCusPrjProjectMortgageService;
    @Autowired
    private HlsCusPrjProjectMortgageMapper hlsCusPrjProjectMortgageMapper;
    @Autowired
    private HlsCusPrjProjectPledgeService hlsCusPrjProjectPledgeService;
    @Autowired
    private HlsCusPrjProjectPledgeMapper hlsCusPrjProjectPledgeMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;

    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;
    @Autowired
    private HlsCusPrjBpFinancingSituationService hlsCusPrjBpFinancingSituationService;
    @Autowired
    private HlsCusPrjBpLiabilitiesService hlsCusPrjBpLiabilitiesService;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper attachmentMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IProjectMeetingApproverService projectMeetingApproverService;

    @Autowired
    private FndCompanyMapper fndCompanyMapper;

    @Autowired
    private IProjectCreditConditionService projectCreditConditionService;

    @Autowired
    private HlsCusPrjSurviveRequireService hlsCusPrjSurviveRequireService;

    @Autowired
    private HlsCusPrjSurviveRequireMapper hlsCusPrjSurviveRequireMapper;

    @Autowired
    private HlsCusPrjProjectInsureService hlsCusPrjProjectInsureService;
    @Autowired
    private HlsCusConContractBeforeRentHService hlsCusConContractBeforeRentHService;
    @Autowired
    private IQuotationSubsectionService quotationSubsectionService;
    @Autowired
    private ContentNumberLineMapper contentNumberLineMapper;
    @Autowired
    private HlsCusFctProjectAttachmentService hlsCusFctProjectAttachmentService;

    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private IFndAttachmentService iFndAttachmentService;

    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    private IFndAttachmentMultiService iFndAttachmentMultiService;

    @Autowired
    private HlsCusProjectCreditNoticeMapper cusProjectCreditNoticeMapper;

    @Autowired
    private IHlsCusProjectCreditNoticeService iHlsCusProjectCreditNoticeService;

    @Autowired
    private IContentNumberHeadService contentNumberHeadService;

    @Autowired
    private ProjectCreditConditionMapper projectCreditConditionMapper;

    @Autowired
    private ProjectMeetingApproverMapper projectMeetingApproverMapper;

    @Autowired
    private HlsCusFundingPlanMapper hlsCusFundingPlanMapper;

    @Autowired
    private HlsBpMasterRoleMapper hlsBpMasterRoleMapper;

    @Autowired
    private HlsBpMasterRoleService hlsBpMasterRoleService;


    public static final String BP_CATEGORY_VENDER = "VENDER";

    @Autowired
    private IGldFinanceIncomeDayService gldFinanceIncomeDayService;

    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;

    @Autowired
    private HlsCusHlsCreditLineChanceAttachService hlsCreditLineChanceService;

    @Autowired
    private IProjectApprovalService projectApprovalService;
    @Autowired
    private PrjProjectApprovalMapper prjProjectApprovalMapper;
    @Autowired
    private ProjectApprovalConditionMapper projectApprovalConditionMapper;
    @Autowired
    private ProjectApprovalConditionService projectApprovalConditionService;

    @Autowired
    private HlsDurationHdMapper hlsDurationHdMapper;
    @Autowired
    private SysDocumentListMapper sysDocumentListMapper;

    @Autowired
    private HlsDocFileTempletMapper hlsDocFileTempletMapper;
    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;

    @Autowired
    HlsCreditPlanService hlsCreditPlanService;

    @Autowired
    HlsCreditPlanMapper hlsCreditPlanMapper;

    @Autowired
    HlsCusHlsCreditLineChanceBpService hlsCusHlsCreditLineChanceBpService;

    @Autowired
    HlsCusPrjProjectChanceMpMapper hlsCusPrjProjectChanceMpMapper;

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private IPrjCreditReplyParaService replyParaService;
    @Autowired
    private IReplyProductService replyProductService;
    @Autowired
    private IReplyProductParaService replyProductParaService;
    @Autowired
    private PrjCreditReplyParaMapper replyParaMapper;
    @Autowired
    private ReplyProductMapper replyProductMapper;
    @Autowired
    private ReplyProductParaMapper replyProductParaMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<HlsCusPrjProject> prjManager(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        return hlsCusPrjProjectMapper.prjManager(hlsCusPrjProject);
    }

    @Override
    public List<HlsCusPrjProject> prjManagers(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        return hlsCusPrjProjectMapper.prjManagers(hlsCusPrjProject);
    }

    /**
     * 提交合同文本变更和追加担保工作流
     */
    @Override
    public void submitPrjContractChange(IRequest request, HlsCusPrjProject hlsCusPrjProject) {
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        HlsCusPrjProject prjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        hlsCusPrjProjectList.add(prjProject);
        //databaseLockProvider.lock(hlsCusPrjProject);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "PRJ_CON_LEASE_CHANGE");
        activitiStartService.start(request, hlsCusPrjProjectList, params);

        //更新审批信息表
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus("APPROVING");
        hlsCusChangeReqInfo.set__status(DTOStatus.UPDATE);
        hlsCusChangeReqInfoMapper.updateByPrimaryKeySelective(hlsCusChangeReqInfo);
        Map<String, Object> paramsEvent = new HashMap<>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
        }
        String changeName = "";
        if (StringUtils.equals(CONTRACTTEXT, hlsCusChangeReqInfo.getBusinessType())) {
            changeName = "租赁合同文本变更审批";
        }
        if (StringUtils.equals(GUARANTOR, hlsCusChangeReqInfo.getBusinessType())) {
            changeName = "租赁追加担保审批";
        }
        String msg = userName + "创建了" + hlsCusPrjProject.getContractName() + changeName + hlsCusPrjProject.getContractNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", changeName);
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(request, hlsCusPrjProject.getProjectId(), hlsCusPrjProject.getDocumentCategory(), hlsCusPrjProject.getDocumentType(), "BAC", "PRJ_CONTRACT_CHANGE", "P2D", paramsEvent);
    }

    /**
     * 取消合同文本或者追加担保
     */
    @Override
    public boolean backPrjContractChange(IRequest request, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject changeProject = new HlsCusPrjProject();
        changeProject.setProjectId(hlsCusPrjProject.getProjectId());
        changeProject = self().selectByPrimaryKey(request, changeProject);

        HlsCusPrjProject normalProject = new HlsCusPrjProject();
        normalProject.setProjectId(changeProject.getRefProjectId());
        normalProject = self().selectByPrimaryKey(request, normalProject);

        normalProject.setContractStatus(changeProject.getContractStatus());
        normalProject.set__status(DTOStatus.UPDATE);
        self().updateByPrimaryKeySelective(request, normalProject);

        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(changeProject.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus(CANCEL);
        hlsCusChangeReqInfo.set__status(DTOStatus.UPDATE);
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, hlsCusChangeReqInfo);

        HlsCusConContract normalContract = new HlsCusConContract();
        normalContract.setContractId(hlsCusChangeReqInfo.getDocumentId());
        normalContract = hlsCusConContractService.selectByPrimaryKey(request, normalContract);
        normalContract.setContractStatus(changeProject.getContractStatus());
        normalContract.set__status(DTOStatus.UPDATE);
        hlsCusConContractService.updateByPrimaryKeySelective(request, normalContract);
        return true;
    }

    /**
     * 租赁合同变更创建
     */
    /*@Override
    public List<HlsCusPrjProject> savePrjContractChange(IRequest request, HlsCusPrjProject hlsCusPrjProject) {
        // 待变更的合同id
        Long contractIdOld = hlsCusPrjProject.getContractId();
        // 变更类型
        String businessType = hlsCusPrjProject.getBusinessType();
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        // if (StringUtils.equals(GUARANTOR, hlsCusPrjProject.getBusinessType()) || StringUtils.equals(CONTRACTTEXT, hlsCusPrjProject.getBusinessType())) {
        // 待变更的项目
        Long projectIdOld = hlsCusPrjProject.getProjectId();
        //  hlsCusChangeReqInfo = insertInfoChangeReqInfo(request, hlsCusPrjProject);

        //  if (StringUtils.equals(GUARANTOR, businessType)) {

        //  }
        //  } else {
        HlsCusConContract normalContract = new HlsCusConContract();
        normalContract.setContractId(contractIdOld);
        normalContract = hlsCusConContractService.selectByPrimaryKey(request, normalContract);
        if (StringUtils.equals(PREREPAYMENT, hlsCusPrjProject.getBusinessType())) {
            normalContract.setPrepaymentMethod("CALCULATOR_TO_SETTLED_DATE");
            //计算剩余利息
            HlsCusConContractCashflow normalConContractCashflow = new HlsCusConContractCashflow();
            normalConContractCashflow.setContractId(contractIdOld);
            normalConContractCashflow.setCfItem(1L);
            normalConContractCashflow.setWriteOffFlag("FULL");
            normalConContractCashflow.setSortorder("asc");
            normalConContractCashflow.setSortname("times");
            Double residualInterest = 0D;
            List<HlsCusConContractCashflow> normalConContractCashflowList = hlsCusConContractCashflowService.select(request, normalConContractCashflow, 1, 99999999);
            for (int i = 0; i < normalConContractCashflowList.size(); i++) {
                if (i + 1 == normalConContractCashflowList.size()) {
                    Long calcDays = hlsCusLonContractWithdrawService.getCalcDays(normalConContractCashflowList.get(i).getDueDate(), hlsCusPrjProject.getChangeReqDate());
                    residualInterest = (double) Math.round(normalConContractCashflowList.get(i - 1).getOutstandingPrincipal() * normalContract.getBaseRate() * calcDays / 360 * 100) / 100;

                }
            }
            //计算剩余本金
            normalConContractCashflow.setWriteOffFlag("NOT");
            List<HlsCusConContractCashflow> normalConContractCashflowPrincipalList = hlsCusConContractCashflowService.select(request, normalConContractCashflow, 1, 99999999);
            Double residualPrincipal = 0D;
            for (HlsCusConContractCashflow contractCashflow : normalConContractCashflowPrincipalList) {
                residualPrincipal = CalculateUtil.add(residualPrincipal, contractCashflow.getPrincipal());

            }
            normalContract.setResidualPrincipal(residualPrincipal);
            normalContract.setResidualInterest(residualInterest);
            hlsCusConContractService.updateByPrimaryKeySelective(request, normalContract);
        }
        // 插入审批信息
        hlsCusPrjProject.setDocumentCategory(normalContract.getDocumentCategory());
        hlsCusPrjProject.setDocumentType(normalContract.getDocumentType());
        hlsCusPrjProject.setContractId(normalContract.getContractId());
        hlsCusChangeReqInfo = insertInfoChangeReqInfo(request, hlsCusPrjProject);
        hlsCusPrjProject = clonePrjProject(request, hlsCusPrjProject, hlsCusChangeReqInfo.getChangeReqId());
        clonePrjProjectLnList(request, projectIdOld, hlsCusPrjProject.getProjectId());
        // 复制数据
        HlsCusConContract changeContract = new HlsCusConContract();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(normalContract);
        hlsBeanRefUtilService.setFieldValue(changeContract, map);
        //复制新数据
        changeContract.setContractId(null);
        changeContract.setRefContractId(normalContract.getContractId());
        changeContract.setProjectId(hlsCusPrjProject.getProjectId());
        changeContract.setDataClass(CHANGEREQ);
        changeContract.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        changeContract.set__status(DTOStatus.ADD);
        changeContract = hlsCusConContractService.insertSelective(request, changeContract);

        // 修改原数据的状态
        normalContract.setContractStatus(PENDING);
        normalContract.set__status(DTOStatus.UPDATE);
        hlsCusConContractService.updateByPrimaryKeySelective(request, normalContract);
        if (StringUtils.equals(PREREPAYMENT, hlsCusChangeReqInfo.getBusinessType()) || StringUtils.equals(BEFORELAUNCH, hlsCusChangeReqInfo.getBusinessType()) || StringUtils.equals(AFTERLAUNCH, hlsCusChangeReqInfo.getBusinessType())) {
            //复制quotation/quotationcashflow/rent(租前息)/分段测算信息
            cloneQuotationAndQuotationCashflow(request, contractIdOld, changeContract.getContractId(), hlsCusChangeReqInfo.getChangeReqDate());
            //变更前租前息
            HlsCusConContractBeforeRentH hlsCusConContractBeforeRentHOld = new HlsCusConContractBeforeRentH();
            hlsCusConContractBeforeRentHOld.setProjectId(projectIdOld);
            List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHOldList = hlsCusConContractBeforeRentHService.select(request, hlsCusConContractBeforeRentHOld, 1, 99999);
            //复制租前息数据
            for (HlsCusConContractBeforeRentH dto : hlsCusConContractBeforeRentHOldList) {
                dto.setProjectId(hlsCusPrjProject.getProjectId());
                dto.set__status(DTOStatus.ADD);
                hlsCusConContractBeforeRentHService.insertSelective(request, dto);
            }
            hlsCusConContractCashflowService.cloneCashflow(normalContract, changeContract);
        }
        //如果是合同文本变更，需要复制合同文本附件
        if (StringUtils.equals(CONTRACTTEXT, hlsCusChangeReqInfo.getBusinessType())) {
            HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
            hlsCusFctProjectAttachment.setProjectId(projectIdOld);
            hlsCusFctProjectAttachment.setSourceType("PRJ_PROJECT_CONTENT");
            List<HlsCusFctProjectAttachment> hlsCusFctProjectAttachmentList = hlsCusFctProjectAttachmentService.select(request, hlsCusFctProjectAttachment, 1, 99999999);
            for (HlsCusFctProjectAttachment dto : hlsCusFctProjectAttachmentList) {
                HlsCusFctProjectAttachment hlsCusFctProjectAttachmentNew = new HlsCusFctProjectAttachment();
                dto.setProjectId(hlsCusPrjProject.getProjectId());
                dto.setProjectAttachmentId(null);
                dto.set__status(DTOStatus.ADD);
                hlsCusFctProjectAttachmentNew = hlsCusFctProjectAttachmentService.insertSelective(request, dto);
                //sys_attament数据
                HlsCusSysAttachment sysAttachment = new HlsCusSysAttachment();
                sysAttachment.setSourceKey(hlsCusFctProjectAttachmentNew.getProjectAttachmentId().toString());
                sysAttachment.setSourceType("PRJ_PROJECT_CONTENT");
                List<HlsCusSysAttachment> sysAttachmentList = hlsSysAttachmentService.select(request, sysAttachment, 1, 99999999);
                if (sysAttachmentList.size() > 0) {
                    //合同文本附件，一一对应
                    HlsCusSysAttachment hlsCusSysAttachment = new HlsCusSysAttachment();
                    Map<String, String> sysAttamentmap = hlsBeanRefUtilService.getFieldValueMap(sysAttachmentList.get(0));
                    hlsBeanRefUtilService.setFieldValue(hlsCusSysAttachment, sysAttamentmap);
                    hlsCusSysAttachment.setAttachmentId(null);
                    hlsCusSysAttachment.setSourceType("PRJ_PROJECT_CONTENT");
                    *//*先判断有无当前类型附件，没有才insert*//*
                    hlsCusSysAttachment.setSourceKey(String.valueOf(hlsCusFctProjectAttachmentNew.getProjectAttachmentId()));
                    List<HlsCusSysAttachment> atts = hlsSysAttachmentService.select(request, hlsCusSysAttachment, 1, 99999);
                    if (!CollectionUtils.isNotEmpty(atts)) {
                        hlsCusSysAttachment = hlsSysAttachmentService.insertSelective(request, hlsCusSysAttachment);
                    }
                    // hlsSysFileService
                    HlsCusSysFile hlsCusSysFile = new HlsCusSysFile();
                    hlsCusSysFile.setAttachmentId(sysAttachmentList.get(0).getAttachmentId());
                    List<HlsCusSysFile> hlsCusSysFileList = hlsSysFileService.select(request, hlsCusSysFile, 1, 9999999);
                    if (hlsCusSysFileList.size() > 0) {
                        HlsCusSysFile sysFile = new HlsCusSysFile();
                        Map<String, String> sysFilemap = hlsBeanRefUtilService.getFieldValueMap(hlsCusSysFileList.get(0));
                        hlsBeanRefUtilService.setFieldValue(sysFile, sysFilemap);
                        sysFile.setAttachmentId(hlsCusSysAttachment.getAttachmentId());
                        sysFile.setFileId(null);
                        hlsSysFileService.insertSelective(request, sysFile);
                    }
                }

            }
        }
        HlsCusPrjProject normalPrjProject = new HlsCusPrjProject();
        normalPrjProject.setProjectId(projectIdOld);
        normalPrjProject.setContractStatus(PENDING);
        normalPrjProject.setContractId(changeContract.getContractId());
        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, normalPrjProject);
        hlsCusPrjProject.setContractId(changeContract.getContractId());
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(hlsCusPrjProject);
        return hlsCusPrjProjectList;
    }*/

    //备份各行表数据
    @Override
    public void clonePrjProjectLnList(IRequest request, Long projectIdOld, Long projectIdNew) {
        //复制客户信息
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(projectIdOld);
        hlsCusPrjProjectBp.setBpCategroy("CON_CONTRACT");
        List<HlsCusPrjProjectBp> bpList = hlsCusPrjProjectBpMapper.select(hlsCusPrjProjectBp);
        if (CollectionUtils.isNotEmpty(bpList)) {
            for (HlsCusPrjProjectBp bp : bpList) {
                bp.setPrjBpId(null);
                bp.setProjectId(projectIdNew);
                bp.set__status(DTOStatus.ADD);
                hlsCusPrjProjectBpService.insertSelective(request, bp);
            }
        }
        //复制抵押物信息
        HlsCusPrjProjectMortgage hlsCusPrjProjectMortgage = new HlsCusPrjProjectMortgage();
        hlsCusPrjProjectMortgage.setProjectId(projectIdOld);
        List<HlsCusPrjProjectMortgage> mortgageList = hlsCusPrjProjectMortgageMapper.select(hlsCusPrjProjectMortgage);
        if (CollectionUtils.isNotEmpty(mortgageList)) {
            for (HlsCusPrjProjectMortgage mortgage : mortgageList) {
                mortgage.setPrjMortgageId(null);
                mortgage.setProjectId(projectIdNew);
                mortgage.set__status(DTOStatus.ADD);
                hlsCusPrjProjectMortgageService.insertSelective(request, mortgage);
            }
        }
        //复制质押物信息
        HlsCusPrjProjectPledge hlsCusPrjProjectPledge = new HlsCusPrjProjectPledge();
        hlsCusPrjProjectPledge.setProjectId(projectIdOld);
        List<HlsCusPrjProjectPledge> pledgeList = hlsCusPrjProjectPledgeMapper.select(hlsCusPrjProjectPledge);
        if (CollectionUtils.isNotEmpty(pledgeList)) {
            for (HlsCusPrjProjectPledge pledge : pledgeList) {
                pledge.setProjectPledgeId(null);
                pledge.setProjectId(projectIdNew);
                pledge.set__status(DTOStatus.ADD);
                hlsCusPrjProjectPledgeService.insertSelective(request, pledge);
            }
        }
        //复制租赁物信息
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(projectIdOld);
        List<HlsCusPrjProjectLeaseItem> leaseItemList = hlsCusPrjProjectLeaseItemService.select(request, hlsCusPrjProjectLeaseItem, 1, 99999999);
        if (CollectionUtils.isNotEmpty(leaseItemList)) {
            for (HlsCusPrjProjectLeaseItem leaseItem : leaseItemList) {
                leaseItem.setProjectLeaseItemId(null);
                leaseItem.setProjectId(projectIdNew);
                leaseItem.set__status(DTOStatus.ADD);
                hlsCusPrjProjectLeaseItemService.insertSelective(request, leaseItem);
            }
        }
        //复制保险信息
        HlsCusPrjProjectInsure hlsCusPrjProjectInsure = new HlsCusPrjProjectInsure();
        hlsCusPrjProjectInsure.setProjectId(projectIdOld);
        List<HlsCusPrjProjectInsure> projectInsureList = hlsCusPrjProjectInsureService.select(request, hlsCusPrjProjectInsure, 1, 99999999);
        if (CollectionUtils.isNotEmpty(projectInsureList)) {
            for (HlsCusPrjProjectInsure projectInsure : projectInsureList) {
                projectInsure.setInsureId(null);
                projectInsure.setProjectId(projectIdNew);
                projectInsure.set__status(DTOStatus.ADD);
                hlsCusPrjProjectInsureService.insertSelective(request, projectInsure);
            }
        }
    }

    //向变更表插入数据
    private HlsCusChangeReqInfo insertInfoChangeReqInfo(IRequest request, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setDocumentVersionId(1L);
        hlsCusChangeReqInfo.setBusinessType(hlsCusPrjProject.getBusinessType());
        hlsCusChangeReqInfo.setStatus("NEW");
        hlsCusChangeReqInfo.setChangeReqDate(hlsCusPrjProject.getChangeReqDate());
        hlsCusChangeReqInfo.setChangeReqUserId(request.getUserId());
        hlsCusChangeReqInfo.setChangeDescription(hlsCusPrjProject.getChangeDescription());
        if (hlsCusPrjProject.getContractId() == null) {
            hlsCusChangeReqInfo.setDocumentId(hlsCusPrjProject.getProjectId());
        } else {
            hlsCusChangeReqInfo.setDocumentId(hlsCusPrjProject.getContractId());
        }
        hlsCusChangeReqInfo.setDocumentCategory(hlsCusPrjProject.getDocumentCategory());
        hlsCusChangeReqInfo.setDocumentType(hlsCusPrjProject.getDocumentType());
        return hlsCusChangeReqInfoService.insertSelective(request, hlsCusChangeReqInfo);
    }

    //备份单据
    private HlsCusPrjProject clonePrjProject(IRequest request, HlsCusPrjProject hlsCusPrjProject, Long changeReqId) {
        HlsCusPrjProject normalProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        HlsCusPrjProject changeProject = new HlsCusPrjProject();
        // 复制项目
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(normalProject);
        hlsBeanRefUtilService.setFieldValue(changeProject, map);
        // 更新项目
//        normalProject.setContractStatus(PENDING);
//        normalProject.set__status(DTOStatus.UPDATE);
//        normalProject = hlsCusPrjProjectService.updateByPrimaryKeySelective(request, normalProject);
        // 复制单据
        changeProject.setProjectId(null);
        changeProject.setRefProjectId(normalProject.getProjectId());
        changeProject.setDataType(CHANGEREQ);
        changeProject.setChangeReqId(changeReqId);
        changeProject.set__status(DTOStatus.ADD);

        return hlsCusPrjProjectService.insertSelective(request, changeProject);
    }


    @Override
    public List<HlsCusPrjProject> queryPrijectAndBpNameByProjectId(IRequest request, HlsCusPrjProject hlsCusPrjProject) {
        return hlsCusPrjProjectMapper.queryPrijectAndBpNameByProjectId(hlsCusPrjProject);
    }

    @Override
    public List<HlsCusPrjProjectBp> queryPrijectBpCount(IRequest request, HlsCusPrjProjectBp hlsCusPrjProjectBp) {
        return hlsCusPrjProjectMapper.queryPrijectBpCount(hlsCusPrjProjectBp);
    }

    @Override
    public List<HlsCusPrjProject> queryPrjContractChange(int page, int pageSize) {
        return hlsCusPrjProjectMapper.queryPrjContractChange();
    }

    @Autowired
    ICodeService iCodeService;
    @Autowired
    private HlsCusPrjProjectAttachmentService attachmentService;

    @Autowired
    private IHlsCusPrjProjectSupplementService hlsCusPrjProjectSupplementService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsCusPrjProjectSupplementMapper hlsCusPrjProjectSupplementMapper;
    @Autowired
    private IHlsCusPrjProjectChanceMpService hlsCusPrjProjectChanceMpService;

    @Override
    public HlsCusPrjProject prjProjectSave(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) {

        //项目评审
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject = hlsCusPrjProjectInfo.getHlsCusPrjProject();
        String category = "PRJ_PROJECT";
        if ("VIRTUAL_CON".equalsIgnoreCase(prjProject.getDataClass())) {
            category = "CON_CONTRACT";
        } else {
            category = "PRJ_PROJECT";
        }
        if (prjProject.getProjectId() != 0) {
            //获取正确的ObjectVersionNumber
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(prjProject.getProjectId());

            hlsCusPrjProject = self().selectByPrimaryKey(iRequest, hlsCusPrjProject);
            prjProject.setObjectVersionNumber(hlsCusPrjProject.getObjectVersionNumber());
            prjProject = self().updateByPrimaryKeySelective(iRequest, prjProject);

        } else {
            prjProject.set__status(DTOStatus.UPDATE);
            prjProject.setDocumentCategory("PRJ_PROJECT");
            prjProject.setProjectStatus("NEW");
            //尽调审查当前进度 1.0尚未受理
//            prjProject.setReviewStatus("100");
            prjProject.setDataClass("NORMAL");
            prjProject.setDataType("NORMAL");//设置为正常的项目尽调单据
            prjProject.setCompanyId(iRequest.getCompanyId());
            prjProject.setDocumentType("PRJ_PROJECT");
            prjProject.setCreatedBy(iRequest.getUserId());
            String prjName = prjProject.getProjectName();
            Map<String, String> params = new HashMap<String, String>();
            prjProject.setProjectNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, prjProject.getDocumentCategory(), prjProject.getDocumentType(), prjProject.getBusinessType(), params));
            //prjProject.setProjectName(prjProject.getProjectNumber() + prjName);
            prjProject.setProjectName(prjName);
            prjProject = self().insertSelective(iRequest, prjProject);
            prjProject.setProjectIdNew(prjProject.getProjectId());

            List<CodeValue> codeValueList = iCodeService.selectCodeValuesByCodeName(iRequest, HlsCusConstant.SYS_CODE.PRJ_LIST_INFORMATION);
            for (CodeValue codeValue : codeValueList) {
                HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
                hlsCusPrjProjectAttachment.setProjectAttachmentCategory("PRJ_PROJECT_REVIEW");
                hlsCusPrjProjectAttachment.setProjectId(prjProject.getProjectId());
                hlsCusPrjProjectAttachment.setDocumentName(codeValue.getMeaning());
                attachmentService.insertSelective(iRequest, hlsCusPrjProjectAttachment);
            }
        }
        //项目评审客户信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = new ArrayList<>();
        hlsCusPrjProjectBpList = hlsCusPrjProjectInfo.getHlsCusPrjProjectBpList();

        List<Long> prjBpIds = new ArrayList();
        int i = 0;
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            HlsCusPrjProjectBp dtTemp = hlsCusPrjProjectBpService.selectByBpId(dt);
            if (dtTemp != null) {
                dt.setPrjBpId(dtTemp.getPrjBpId());
            } else {
                dt.setPrjBpId(null);
            }
            if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                //dt.setPrjBpId(null);
                dt.set__status(DTOStatus.ADD);
                if ("VIRTUAL_CON".equalsIgnoreCase(prjProject.getDataClass())) {
                    dt.setBpCategroy(category);
                } else {
                    dt.setBpCategroy(category);
                }
                dt = hlsCusPrjProjectBpService.insertSelective(iRequest, dt);
                prjBpIds.add(dt.getPrjBpId());


            } else {
                dt.set__status(DTOStatus.UPDATE);
                dt.setProjectId(prjProject.getProjectId());
                dt.setBpCategroy(category);
                dt = hlsCusPrjProjectBpService.updateByPrimaryKeySelective(iRequest, dt);
                prjBpIds.add(dt.getPrjBpId());

            }
        }

        if (!prjBpIds.isEmpty()) {
            //删除已有的承租人信息
            if (prjProject.getProjectId() != null) {
                hlsCusPrjProjectBpService.deleteByPrjBpIds(prjBpIds, prjProject.getProjectId());
            }


        }


        //项目评审租赁物信息
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = new ArrayList<>();
        hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectInfo.getHlsCusPrjProjectLeaseItemList();
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItemList) {
            if (dt.getProjectLeaseItemId() == null || dt.getProjectLeaseItemId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                dt.set__status(DTOStatus.ADD);
                dt = hlsCusPrjProjectLeaseItemService.insertSelective(iRequest, dt);
            } else {
                dt.set__status(DTOStatus.UPDATE);
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectLeaseItemService.updateByPrimaryKeySelective(iRequest, dt);
            }
        }
        /*//项目评审保险信息
        List<HlsCusPrjProjectInsure> hlsCusPrjProjectInsureList = new ArrayList<>();
        hlsCusPrjProjectInsureList = hlsCusPrjProjectInfo.getHlsCusPrjProjectInsureList();
        if (CollectionUtils.isNotEmpty(hlsCusPrjProjectInsureList)) {
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
        }*/
        //租前息
        List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHList = new ArrayList<>();
        hlsCusConContractBeforeRentHList = hlsCusPrjProjectInfo.getHlsCusConContractBeforeRentHList();
        if (CollectionUtils.isNotEmpty(hlsCusConContractBeforeRentHList)) {
            for (HlsCusConContractBeforeRentH dt : hlsCusConContractBeforeRentHList) {
                if (dt.getRentId() == null || dt.getRentId() == 0) {
                    dt.setProjectId(prjProject.getProjectId());
                    dt.setCompanyId(iRequest.getCompanyId());
                    dt.set__status(DTOStatus.ADD);
                    hlsCusConContractBeforeRentHService.insertSelective(iRequest, dt);
                } else {
                    dt.set__status(DTOStatus.UPDATE);
                    dt.setProjectId(prjProject.getProjectId());
                    hlsCusConContractBeforeRentHService.updateByPrimaryKeySelective(iRequest, dt);
                }
            }
        }
        /*//项目评审保证信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectGuaranteeList = new ArrayList<>();
        hlsCusPrjProjectGuaranteeList = hlsCusPrjProjectInfo.getHlsCusPrjProjectGuaranteeList();
        if ("VIRTUAL_CON".equals(prjProject.getDataClass())) {
            hlsCusPrjProjectGuaranteeList = hlsCusPrjProjectBpService.createSerialNumber(iRequest, prjProject.getProjectId(), hlsCusPrjProjectGuaranteeList, "WARRANTOR");

        }
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectGuaranteeList) {
            if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                dt.set__status(DTOStatus.ADD);
                dt.setBpCategroy(category);
                dt.setBpType("WARRANTOR");
                dt.setBpRoleType("WARRANTOR");
                dt = hlsCusPrjProjectBpService.insertSelective(iRequest, dt);
            } else {
                dt.set__status(DTOStatus.UPDATE);
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectBpService.updateByPrimaryKey(iRequest, dt);
            }
        }*/
        //项目评审抵押物信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectMortgageList = new ArrayList<>();
        hlsCusPrjProjectMortgageList = hlsCusPrjProjectInfo.getHlsCusPrjProjectMortgageList();
        if ("VIRTUAL_CON".equals(prjProject.getDataClass())) {
            hlsCusPrjProjectMortgageList = hlsCusPrjProjectBpService.createSerialNumber(iRequest, prjProject.getProjectId(), hlsCusPrjProjectMortgageList, "MORTGAGOR");
        }
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectMortgageList) {
            if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                dt.setBpCategroy(category);
                dt.setBpType("MORTGAGOR");
                dt.setProjectId(prjProject.getProjectId());
                dt.setBpRoleType("MORTGAGOR");
                dt.set__status(DTOStatus.ADD);
                dt = hlsCusPrjProjectBpService.insertSelective(iRequest, dt);
            } else {
                dt.set__status(DTOStatus.UPDATE);
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectBpService.updateByPrimaryKeySelective(iRequest, dt);
            }
        }
        //项目评审质押物信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectPledgesList = new ArrayList<>();
        hlsCusPrjProjectPledgesList = hlsCusPrjProjectInfo.getHlsCusPrjProjectPledgeList();
        if ("VIRTUAL_CON".equals(prjProject.getDataClass())) {
            hlsCusPrjProjectPledgesList = hlsCusPrjProjectBpService.createSerialNumber(iRequest, prjProject.getProjectId(), hlsCusPrjProjectPledgesList, "PLEDGOR");
        }
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectPledgesList) {
            if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                dt.setBpCategroy(category);
                dt.setBpType("PLEDGOR");
                dt.setBpRoleType("PLEDGOR");
                dt.set__status(DTOStatus.ADD);
                dt = hlsCusPrjProjectBpService.insertSelective(iRequest, dt);
            } else {
                dt.set__status(DTOStatus.UPDATE);
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectBpService.updateByPrimaryKeySelective(iRequest, dt);
            }
        }
        //租赁项目评审报价信息
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<HlsCusPrjQuotation>();
        hlsCusPrjQuotationList = hlsCusPrjProjectInfo.getHlsCusPrjQuotationList();
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            if (dt.getQuotationId() == null || dt.getQuotationId() == 0) {
                dt.setSourceDocumentId(prjProject.getProjectId());
                dt.setSourceDocumentCategory("PRJ_PROJECT");
                dt.setDataClass("PRJ_PROJECT_INVEST");//项目尽调的报价
                dt.setConfirmFlag("NEW");
                dt = hlsCusPrjQuotationService.insertSelective(iRequest, dt);
            } else {
                dt = hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, dt);
            }
        }

        /*//租赁项目评审报价信息
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation = hlsCusPrjProjectInfo.getHlsCusPrjQuotation();
        if (hlsCusPrjQuotation.getQuotationId() == null || hlsCusPrjQuotation.getQuotationId() == 0) {
            hlsCusPrjQuotation.setSourceDocumentId(prjProject.getProjectId());
            hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotation.setDataClass("PRJ_PROJECT_INVEST");//项目尽调的报价
            hlsCusPrjQuotation.setConfirmFlag("NEW");
            hlsCusPrjQuotation = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotation);
        } else {
            HlsCusPrjQuotation oldPrjQuotation = new HlsCusPrjQuotation();
            oldPrjQuotation.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            oldPrjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, oldPrjQuotation);
            hlsCusPrjQuotation.setObjectVersionNumber(oldPrjQuotation.getObjectVersionNumber());
            hlsCusPrjQuotation = hlsCusPrjQuotationService.updateByPrimaryKey(iRequest, hlsCusPrjQuotation);
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(prjProject.getProjectId());
            hlsCusPrjProject = self().selectByPrimaryKey(iRequest, hlsCusPrjProject);
            hlsCusPrjProject.setContractAmount(hlsCusPrjQuotation.getLeaseItemAmount().toString());
            self().updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);

        }*/
        /*//组合报价
        if ("COMBINATION_QUOTATION".equals(hlsCusPrjQuotation.getPriceList())) {
            if (hlsCusPrjQuotation.getQuotationId() != null || hlsCusPrjQuotation.getQuotationId() != 0) {
                quotationSubsectionService.updateQuotationBySubsectionData(iRequest, hlsCusPrjQuotation);
            }
        }*/
        /*//其他应收付
        if ("PRJ_PROJECT".equals(hlsCusPrjProjectInfo.getHlsCusPrjProject().getDocumentType())) {
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjProjectInfo.getHlsCusPrjQuotationCashflowList();
            if (CollectionUtils.isNotEmpty(hlsCusPrjQuotationCashflowList)) {
                for (HlsCusPrjQuotationCashflow dt : hlsCusPrjQuotationCashflowList) {
                    if (dt.getQuotationCashflowId() == null || dt.getQuotationCashflowId() == 0) {
                        dt.setProjectId(prjProject.getProjectId());
                        dt.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                        dt.setCalcDate(dt.getDueDate());
                        dt.setFinIncomeDate(dt.getDueDate());
                        dt.set__status(DTOStatus.ADD);
                        hlsCusPrjQuotationCashflowService.insertSelective(iRequest, dt);
                    } else {
                        dt.set__status(DTOStatus.UPDATE);
                        dt.setProjectId(prjProject.getProjectId());
                        hlsCusPrjQuotationCashflowService.updateByPrimaryKeySelective(iRequest, dt);
                    }
                }
            }
        } else if ("CON_CONTRACT".equals(hlsCusPrjProjectInfo.getHlsCusPrjProject().getDocumentType())) {
            List<HlsCusConContractCashflow> hlsCusConContractCashflowList = hlsCusPrjProjectInfo.getHlsCusConContractCashflowList();
            if (hlsCusConContractCashflowList.size() > 0) {
                for (HlsCusConContractCashflow dt : hlsCusConContractCashflowList) {
                    if (dt.getCashflowId() == null || dt.getCashflowId() == 0) {
                        dt.setContractId(prjProject.getContractId());
                        dt.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                        dt.setCalcDate(dt.getDueDate());
                        dt.setFinIncomeDate(dt.getDueDate());
                        dt.set__status(DTOStatus.ADD);
                        hlsCusConContractCashflowService.insertSelective(iRequest, dt);
                    } else {
                        dt.set__status(DTOStatus.UPDATE);
                        dt.setContractId(prjProject.getContractId());
                        hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, dt);
                    }
                }
            }

        }*/

        /*//手动添加现金流(项目)
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationChargeCashflowList = new ArrayList<>();
        hlsCusPrjQuotationChargeCashflowList = hlsCusPrjProjectInfo.getHlsCusPrjQuotationChargeCashflowList();
        if (hlsCusPrjQuotationChargeCashflowList != null) {
            try {
                hlsCusPrjQuotationCashflowService.saveLeaseChargeCashflow(iRequest, hlsCusPrjQuotationChargeCashflowList);
            } catch (hls.core.utils.exception.HlsCusException e) {
                throw new IllegalArgumentException("添加现金流失败，请联系管理员！");
            }

        }*/
        /*//手动添加现金流(合同)
        List<HlsCusConContractCashflow> hlsCusConContractCashflowLists = new ArrayList<>();
        hlsCusConContractCashflowLists = hlsCusPrjProjectInfo.getHlsCusConContractCashflowList();
        if (hlsCusConContractCashflowLists != null) {
            try {
                hlsCusConContractCashflowService.saveLeaseChargeCashflow(iRequest, hlsCusConContractCashflowLists);
            } catch (hls.core.utils.exception.HlsCusException e) {
                throw new IllegalArgumentException("添加现金流失败，请联系管理员！");
            }

        }*/

        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList = new ArrayList<>();
        hlsCusPrjProjectAttachmentList = hlsCusPrjProjectInfo.getHlsCusPrjProjectAttachmentList();
        for (HlsCusPrjProjectAttachment dt : hlsCusPrjProjectAttachmentList) {
            if (dt.getProjectAttachmentId() == null || dt.getProjectAttachmentId() == 0) {
                /*dt(prjProject.getProjectId());
                dt.setSourceDocumentCategory("PRJ_PROJECT");
                dt.setDataClass("PRJ_PROJECT_INVEST");//项目尽调的报价
                dt.setConfirmFlag("NEW");*/
                dt.setProjectId(prjProject.getProjectId());
                dt.setPrjProjectType("PRJ_PROJECT");
                dt = attachmentService.insertSelective(iRequest, dt);
            } else {
                dt.setPrjProjectType("PRJ_PROJECT");
                dt = attachmentService.updateByPrimaryKeySelective(iRequest, dt);
            }
        }


        if (prjProject.getDataType().equals("CHANGE_REQ")) {
            //付款前提条件
            List<ProjectCreditCondition> projectCreditConditionList = new ArrayList<>();
            projectCreditConditionList = hlsCusPrjProjectInfo.getProjectCreditConditionList();
            if (projectCreditConditionList != null) {
                for (ProjectCreditCondition dto : projectCreditConditionList) {
                    if (dto.getCreditConditionId() == null || dto.getCreditConditionId() == 0) {
                        dto.setProjectId(prjProject.getProjectId());
                        projectCreditConditionService.insertSelective(iRequest, dto);
                    } else {
                        projectCreditConditionService.updateByPrimaryKeySelective(iRequest, dto);
                    }
                }
            }


            //管理要求
            List<HlsCusPrjSurviveRequire> hlsCusPrjSurviveRequireList = new ArrayList<>();
            hlsCusPrjSurviveRequireList = hlsCusPrjProjectInfo.getHlsCusPrjSurviveRequireList();
            if (hlsCusPrjSurviveRequireList != null) {
                for (HlsCusPrjSurviveRequire dto : hlsCusPrjSurviveRequireList) {
                    if (dto.getPrjSurviveId() == null || dto.getPrjSurviveId() == 0) {
                        dto.setProjectId(prjProject.getProjectId());
                        hlsCusPrjSurviveRequireService.insertSelective(iRequest, dto);
                    } else {
                        hlsCusPrjSurviveRequireService.updateByPrimaryKeySelective(iRequest, dto);
                    }
                }
            }

        }

        return prjProject;

    }


    @Override
    public HlsCusPrjProject prjProjectSaveSupple(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) {

        //项目评审
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject = hlsCusPrjProjectInfo.getHlsCusPrjProject();


        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList = new ArrayList<>();
        hlsCusPrjProjectAttachmentList = hlsCusPrjProjectInfo.getHlsCusPrjProjectAttachmentList();
        for (HlsCusPrjProjectAttachment dt : hlsCusPrjProjectAttachmentList) {
            if (dt.getProjectAttachmentId() == null || dt.getProjectAttachmentId() == 0) {

                dt.setProjectId(prjProject.getProjectId());
                dt = attachmentService.insertSelective(iRequest, dt);
            } else {
                dt = attachmentService.updateByPrimaryKeySelective(iRequest, dt);
            }
        }

        Map<String, String> params = new HashMap<String, String>();

        HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        HlsCusPrjProjectSupplement prjProjectSupplement = new HlsCusPrjProjectSupplement();

        hlsCusPrjProjectSupplement = hlsCusPrjProjectInfo.getHlsCusPrjProjectSupplement();
        if (hlsCusPrjProjectSupplement.getPrjSuppleId() != null && hlsCusPrjProjectSupplement.getPrjSuppleId() != 0) {
//           hlsCusPrjProjectSupplement.setProjectId(prjProject.getProjectId());

            prjProjectSupplement = hlsCusPrjProjectSupplementService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProjectSupplement);

        } else {
//           hlsCusPrjProjectSupplement.setProjectId(prjProject.getProjectId());
            String number = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PROJECT_SUPPLEMENT",
                    "SUPPLE", "SUPPLE", params);
            hlsCusPrjProjectSupplement.setPrjSuppleNumber(number);
            hlsCusPrjProjectSupplement.setSuppleStatus("NEW");
            prjProjectSupplement = hlsCusPrjProjectSupplementService.insertSelective(iRequest, hlsCusPrjProjectSupplement);
        }
        prjProject.setPrjSuppleId(prjProjectSupplement.getPrjSuppleId());
        return prjProject;
    }

    @Override
    public HlsCusPrjProject prjProjectSaveWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject project = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        project.setReviewComments(hlsCusPrjProject.getReviewComments());
        project = hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, project);
        return project;
    }

    @Override
    public HlsCusPrjProjectAttachment prjProjectSaveWflAttach(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment) {
        HlsCusPrjProjectAttachment attachment = hlsCusPrjProjectAttachmentService.selectByPrimaryKey(iRequest, hlsCusPrjProjectAttachment);
        attachment.setDocumentName(hlsCusPrjProjectAttachment.getDocumentName());
        attachment.setDescription(hlsCusPrjProjectAttachment.getDescription());
        attachment = hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(iRequest, attachment);
        return attachment;
    }

    //项目变更保存修改信息
    @Override
    public HlsCusPrjProject prjProjectChangeSave(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) {
        //项目评审信息
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject = hlsCusPrjProjectInfo.getHlsCusPrjProject();
        if (prjProject.getProjectId() != 0) {
            //获取正确的ObjectVersionNumber
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(prjProject.getProjectId());
            hlsCusPrjProject = self().selectByPrimaryKey(iRequest, hlsCusPrjProject);
            prjProject.setObjectVersionNumber(hlsCusPrjProject.getObjectVersionNumber());
            prjProject = self().updateByPrimaryKey(iRequest, prjProject);
        } /*else {
            prjProject.set__status("add");
            prjProject.setDocumentCategory("PRJ_PROJECT");
            prjProject.setProjectStatus("NEW");
            prjProject.setDataClass("NORMAL");
            prjProject.setCompanyId(iRequest.getCompanyId());
            prjProject.setDocumentType("PRJ_PROJECT");
            Map<String, String> params = new HashMap<String, String>();
            prjProject.setProjectNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, prjProject.getDocumentCategory(), prjProject.getDocumentType(), prjProject.getBusinessType(), params));
            prjProject = self().insertSelective(iRequest, prjProject);
            prjProject.setProjectIdNew(prjProject.getProjectId());
        }*/
        //项目评审客户信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = new ArrayList<>();
        hlsCusPrjProjectBpList = hlsCusPrjProjectInfo.getHlsCusPrjProjectBpList();
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                dt.set__status("add");
                dt = hlsCusPrjProjectBpService.insertSelective(iRequest, dt);
            } else {
                dt.set__status("update");
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectBpService.updateByPrimaryKey(iRequest, dt);
            }
        }
        //项目评审租赁物信息
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = new ArrayList<>();
        hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectInfo.getHlsCusPrjProjectLeaseItemList();
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItemList) {
            if (dt.getProjectLeaseItemId() == null || dt.getProjectLeaseItemId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                dt.set__status("add");
                dt = hlsCusPrjProjectLeaseItemService.insertSelective(iRequest, dt);
            } else {
                dt.set__status("update");
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectLeaseItemService.updateByPrimaryKey(iRequest, dt);
            }
        }
        //项目评审抵押物信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectMortgageList = new ArrayList<>();
        hlsCusPrjProjectMortgageList = hlsCusPrjProjectInfo.getHlsCusPrjProjectMortgageList();
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectMortgageList) {
            if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                dt.set__status("add");
                dt = hlsCusPrjProjectBpService.insertSelective(iRequest, dt);
            } else {
                dt.set__status("update");
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectBpService.updateByPrimaryKey(iRequest, dt);
            }
        }
        //项目评审质押物信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectPledgesList = new ArrayList<>();
        hlsCusPrjProjectPledgesList = hlsCusPrjProjectInfo.getHlsCusPrjProjectPledgeList();
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectPledgesList) {
            if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                dt.setProjectId(prjProject.getProjectId());
                dt.set__status("add");
                dt = hlsCusPrjProjectBpService.insertSelective(iRequest, dt);
            } else {
                dt.set__status("update");
                dt.setProjectId(prjProject.getProjectId());
                dt = hlsCusPrjProjectBpService.updateByPrimaryKey(iRequest, dt);
            }
        }
        //租赁项目评审报价信息
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation = hlsCusPrjProjectInfo.getHlsCusPrjQuotation();
        if (hlsCusPrjQuotation.getQuotationId() == null || hlsCusPrjQuotation.getQuotationId() == 0) {
            hlsCusPrjQuotation.setSourceDocumentId(prjProject.getProjectId());
            hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotation.setDataClass("CHANGE_REQ");
            hlsCusPrjQuotation.setConfirmFlag("NEW");
            hlsCusPrjQuotation = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotation);
        } else {
            HlsCusPrjQuotation oldPrjQuotation = new HlsCusPrjQuotation();
            oldPrjQuotation.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            oldPrjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, oldPrjQuotation);
            hlsCusPrjQuotation.setObjectVersionNumber(oldPrjQuotation.getObjectVersionNumber());
            hlsCusPrjQuotation = hlsCusPrjQuotationService.updateByPrimaryKey(iRequest, hlsCusPrjQuotation);
        }

//        //租赁项目尽调他行融资信息
//
//        //删除旧的融资情况
//        HlsCusPrjBpFinancingSituation oldPrjFin = new HlsCusPrjBpFinancingSituation();
//        oldPrjFin.setProjectId(prjProject.getProjectId());
//        List<HlsCusPrjBpFinancingSituation> oldList = hlsCusPrjBpFinancingSituationService.select(iRequest, oldPrjFin, 1, 10000);
//        hlsCusPrjBpFinancingSituationService.batchDelete(oldList);
//
//        List<HlsCusPrjBpFinancingSituation> hlsCusPrjBpFinancingSituationList = new ArrayList<>();
//        hlsCusPrjBpFinancingSituationList = hlsCusPrjProjectInfo.getHlsCusPrjBpFinancingSituationList();
//        for (HlsCusPrjBpFinancingSituation dt : hlsCusPrjBpFinancingSituationList) {
//            dt.setFinancingSituationId(null);
//            dt.set__status("add");
//            dt.setProjectId(prjProject.getProjectId());
//            dt = hlsCusPrjBpFinancingSituationService.insertSelective(iRequest, dt);
//        }
//        //租赁项目尽调他或有负债
//
//        //删除旧的或有负债
//        HlsCusPrjBpLiabilities oldBp = new HlsCusPrjBpLiabilities();
//        oldBp.setProjectId(prjProject.getProjectId());
//        List<HlsCusPrjBpLiabilities> oldBpList = hlsCusPrjBpLiabilitiesService.select(iRequest, oldBp, 1, 10000);
//        hlsCusPrjBpLiabilitiesService.batchDelete(oldBpList);
//
//        List<HlsCusPrjBpLiabilities> hlsCusPrjBpLiabilitiesList = new ArrayList<>();
//        hlsCusPrjBpLiabilitiesList = hlsCusPrjProjectInfo.getHlsCusPrjBpLiabilitiesList();
//        for (HlsCusPrjBpLiabilities dt : hlsCusPrjBpLiabilitiesList) {
//            dt.setLiabilitiesId(null);
//            dt.set__status("add");
//            dt.setProjectId(prjProject.getProjectId());
//            dt = hlsCusPrjBpLiabilitiesService.insertSelective(iRequest, dt);
//        }

        //资料清单
//        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList = new ArrayList<>();
//        hlsCusPrjProjectAttachmentList = hlsCusPrjProjectInfo.getHlsCusPrjProjectAttachmentList();
//        for (HlsCusPrjProjectAttachment dt : hlsCusPrjProjectAttachmentList) {
//            if (dt.getProjectAttachmentId() == null || dt.getProjectAttachmentId() == 0) {
//                dt.set__status("add");
//                dt.setProjectId(prjProject.getProjectId());
//                dt = hlsCusPrjProjectAttachmentService.insertSelective(iRequest, dt);
//            } else {
//                dt.set__status("update");
//                dt.setProjectId(prjProject.getProjectId());
//                dt = hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(iRequest, dt);
//            }
//        }
        return prjProject;
    }

    private static final String LEASE_BUSINESS_TYPE = "LEASE";

    private static final String LEASEBACK_BUSINESS_TYPE = "LEASEBACK";

    @Override
    public HlsCusPrjProject prjCreateVirtualCon(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {

        //如果是授信项目,先将项目名称,项目编号,业务类型,业务板块,行业分类更新到原项目
        HlsCusPrjProject record = new HlsCusPrjProject();
        record.setProjectId(hlsCusPrjProject.getProjectId());
        record = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, record);

        if ("Y".equals(record.getCreditFlag())) {
            record.setProjectName(hlsCusPrjProject.getProjectName());
            record.setProjectNumber(hlsCusPrjProject.getProjectNumber());
            record.setBusinessType(hlsCusPrjProject.getBusinessType());
            record.setIndustry(hlsCusPrjProject.getIndustry());
            record.setIndustryType(hlsCusPrjProject.getIndustryType());
            hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, record);
        }

        //前端传过来的租赁物id,需要传到复制方法里
        String itemIds = hlsCusPrjProject.getItemIds();

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(hlsCusPrjProject.getRefProjectId());
        prjProject = self().selectByPrimaryKey(iRequest, prjProject);
        HlsCusPrjProject resultPrjProject = new HlsCusPrjProject();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(prjProject);
        hlsBeanRefUtilService.setFieldValue(resultPrjProject, map);
        resultPrjProject.setContractStatus("NEW");
        resultPrjProject.setPurchaseFrame("N");
        resultPrjProject.setQuotationId(hlsCusPrjProject.getQuotationId());
        resultPrjProject.setPenaltyRate(DEFAULT_PENALTY_RATE);
        resultPrjProject.setFloatingRangeMethod(FLOATING_RANGE_METHOD);
        resultPrjProject.setLeaseItemProperty(LEASE_ITEM_TYPE);
        resultPrjProject.setContractAmount(hlsCusPrjProject.getContractAmount());
        resultPrjProject.setContractName(hlsCusPrjProject.getContractName());
        resultPrjProject.setFalsifyInterestRate(0.0005D);
        if (hlsCusPrjProject.getBusinessType() != null) {
            resultPrjProject.setBusinessType(hlsCusPrjProject.getBusinessType());
        }
        if (hlsCusPrjProject.getApplyBusinessType() != null) {
            resultPrjProject.setApplyBusinessType(hlsCusPrjProject.getApplyBusinessType());
        }

      /*  Map<String, String> params = new HashMap<String, String>();
        StringBuffer sub = new StringBuffer();
        sub.append(hlsCusPrjProject.getBusinessType());
        sub.append("_CON");*/
        //   resultPrjProject.setContractNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, "CON_CONTRACT", sub.toString(), hlsCusPrjProject.getBusinessType(), params));
        //合同的编码规则=项目编号+4位流水账号

        //合同编码赋值
    /*    if (hlsCusPrjProject.getHeadId()!= null) {
            ContentNumberLine contentNumberLine = new ContentNumberLine();
            contentNumberLine.setHeadId(hlsCusPrjProject.getHeadId());
            List<ContentNumberLine> contentNumberLines = contentNumberLineMapper.select(contentNumberLine);
            for (int i = 0; i < contentNumberLines.size(); i++) {
                if(contentNumberLines.get(i).getContentType().equals("LON_CONTENT")){
                    hlsCusPrjProject.setContractNumber(contentNumberLines.get(i).getDocumentNumber());
                }
            }
        }
        String contractNum = prjProject.getProjectNumber();*/

        if (hlsCusPrjProject.getContractNumber() == null) {
            /*HlsCusPrjProject pDto = new HlsCusPrjProject();
            pDto.setProjectNumber(contractNum);
            String num2 = this.getLeasingContractSerialNumber(iRequest, pDto);
            contractNum = contractNum + "-" + num2;
            resultPrjProject.setContractNumber(contractNum);*/
          /*  StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
            stringBuilder.append(COMPANY_NAME);
            stringBuilder.append(COMMON_STR);
            stringBuilder.append(simpleDateFormat.format(new Date()));
            stringBuilder.append(COMMON_STR);
            String lonNum = fndCodingRuleValuesService.getCodeRuleValue(iRequest, CONTENT_NUMBER_REQ,
                    LON_CONTENT, LON_CONTENT, params);
            stringBuilder.append(lonNum);

            stringBuilder.append(COMMON_STR);
            String businessType = hlsCusPrjProject.getBusinessType();
            if(LEASE_BUSINESS_TYPE.equalsIgnoreCase(businessType)){
                businessType = "ZZ";
            }else if(LEASEBACK_BUSINESS_TYPE.equalsIgnoreCase(businessType)){
                businessType = "HZ";
            }else{
                businessType = "";
            }*/
/*
            stringBuilder.append(businessType);
*/
/*
            resultPrjProject.setContractNumber(stringBuilder.toString());*/
            StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
            String value = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "CON_CONTRACT", "CONL", "LEASE", params);


            String number3 = "" + stringBuilder.append("H-").append(simpleDateFormat.format(new Date())).append("Y-15-").append(value);
            resultPrjProject.setContractNumber(number3);
/*
            resultPrjProject.setContractNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, "CON_CONTRACT", "CONL", "LEASE", params));
*/

            resultPrjProject.setUsedNumberFlag("N");
        } else {

            resultPrjProject.setContractNumber(hlsCusPrjProject.getContractNumber());
            resultPrjProject.setUsedNumberFlag("Y");
            ContentNumberHead contentNumberHead = new ContentNumberHead();
            contentNumberHead.setHeadId(hlsCusPrjProject.getHeadId());
            contentNumberHead = contentNumberHeadService.selectByPrimaryKey(iRequest, contentNumberHead);
            contentNumberHead.setUsedFlag("Y");
            resultPrjProject.setContentBusinessType(contentNumberHead.getBusinessType());

            contentNumberHead = contentNumberHeadService.updateByPrimaryKeySelective(iRequest, contentNumberHead);

        }
        resultPrjProject.setProjectId(null);
        resultPrjProject.setDataClass("VIRTUAL_CON");
        resultPrjProject.setDataType("NORMAL");
        resultPrjProject.setRefProjectId(hlsCusPrjProject.getRefProjectId());
        //插入编号
        resultPrjProject.setLeasingNumber(this.selectLeasingNumber(resultPrjProject.getCompanyId()));
        //传入批复号,与主合同编号一致
       /* StringBuilder sb = new StringBuilder();
        sb.append("EBFIL");
        sb.append("-");
        sb.append(resultPrjProject.getLeasingNumber());
        sb.append("-");
        if ("LEASE".equals(hlsCusPrjProject.getBusinessType())) {
            sb.append("ZZ-01");
        } else if ("LEASEBACK".equals(hlsCusPrjProject.getBusinessType())) {
            sb.append("HZ-01");
        } else {
            sb.append("JY-01");
        }
*/
        String approvalNumber = hlsCusPrjProjectMapper.queryApprovalNumber(hlsCusPrjProject.getRefProjectId());
        resultPrjProject.setApprovalNumber(approvalNumber);
        //resultPrjProject.setContractName(resultPrjProject.getApprovalNumber() + hlsCusPrjProjectMapper.getBpNameByProjectTenantId(hlsCusPrjProject));
        resultPrjProject = self().insertSelective(iRequest, resultPrjProject);
        //合同文本
    /*    if (hlsCusPrjProject.getHeadId()!= null) {
            ContentNumberLine contentNumberLine = new ContentNumberLine();
            contentNumberLine.setHeadId(hlsCusPrjProject.getHeadId());
            List<ContentNumberLine> contentNumberLines = contentNumberLineMapper.select(contentNumberLine);
            for (int i = 0; i < contentNumberLines.size(); i++) {
                HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
                hlsCusFctProjectAttachment.setProjectId(resultPrjProject.getProjectId());
                hlsCusFctProjectAttachment.setSourceType(contentNumberLines.get(i).getContentType());
                hlsCusFctProjectAttachment.setSystemNumber(contentNumberLines.get(i).getDocumentNumber());
                hlsCusFctProjectAttachment.setStatus("FIRST_DRAFT");
                hlsCusFctProjectAttachment.setDocumentNumber(contentNumberLines.get(i).getDocumentNumber());
                hlsCusFctProjectAttachmentService.insertSelective(iRequest, hlsCusFctProjectAttachment);
            }
        }else{
            HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
            hlsCusFctProjectAttachment.setProjectId(resultPrjProject.getProjectId());
            hlsCusFctProjectAttachment.setSourceType("LON_CONTENT");
            hlsCusFctProjectAttachment.setStatus("FIRST_DRAFT");
            hlsCusFctProjectAttachment.setDocumentNumber(resultPrjProject.getContractNumber());
            hlsCusFctProjectAttachmentService.insertSelective(iRequest, hlsCusFctProjectAttachment);
        }*/

        //self().updateByPrimaryKeyOptions();
        //自动插入权限
//        hlsCusSysUserAuthorityTrxService.insertUserAuthor(iRequest,"CON_CONTRACT_VIRTUAL",resultPrjProject.getProjectId(),"RISK");
//        hlsCusSysUserAuthorityTrxService.insertChiefUserAuthor(iRequest,"CON_CONTRACT_VIRTUAL",resultPrjProject.getProjectId());
        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setProjectId(resultPrjProject.getRefProjectId().toString());
        List<PrjProjectApproval> prjProjectApprovals = prjProjectApprovalMapper.conQueryAll(prjProjectApproval);
        if (prjProjectApprovals.size() > 0) {

            BeanRefUtils.beanToBean(prjProjectApprovals.get(0), prjProjectApproval, hlsBeanRefUtilService);
            prjProjectApproval.setProjectId(resultPrjProject.getProjectId().toString());
/*
            prjProjectApproval.setDataClass("VIRTUAL_CON");
*/
            projectApprovalService.insertSelective(iRequest, prjProjectApproval);
        }

//        ProjectApprovalCondition projectCreditCondition = new ProjectApprovalCondition();
//        projectCreditCondition.setProjectId(resultPrjProject.getRefProjectId());
//        List<ProjectApprovalCondition> projectCreditConditions = projectApprovalConditionMapper.queryAll(projectCreditCondition);
//        for (ProjectApprovalCondition projectCreditConditions1 : projectCreditConditions) {
//
//
//            BeanRefUtils.beanToBean(projectCreditConditions1, projectCreditCondition, hlsBeanRefUtilService);
//            projectCreditConditions1.setProjectId(resultPrjProject.getProjectId());
///*
//            prjProjectApproval.setDataClass("VIRTUAL_CON");
//*/
//            projectApprovalConditionService.insertSelective(iRequest, projectCreditConditions1);
//        }

        resultPrjProject.setItemIds(itemIds);
        projectInfoCopy(iRequest, resultPrjProject);


        return resultPrjProject;
    }

    @Override
    public List<Map> prjHomePageGetAllStatusProjectCount(IRequest iRequest, HlsCusPrjProject prjProject) {
        return hlsCusPrjProjectMapper.prjHomePageGetAllStatusProjectCount(prjProject);
    }

    @Override
    public List<Map> prjHomePageGetAllStatusConCount(IRequest iRequest, HlsCusPrjProject prjProject) {
        return hlsCusPrjProjectMapper.prjHomePageGetAllStatusConCount(prjProject);
    }

    @Override
    public List<HlsCusPrjProject> queryBpNotice(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pagesize) {
        prjProject.setCompanyId(iRequest.getCompanyId());
        PageHelper.startPage(page, pagesize);
        return hlsCusPrjProjectMapper.queryBpNotice(prjProject);
    }

    @Override
    public List<HlsCusPrjProject> prjHomePageProjectInfoGrid(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusPrjProjectMapper.prjHomePageProjectInfoGrid(prjProject);
    }

    @Override
    public List<HlsCusPrjProject> queryPrjHomePageProjectInfoGridNew(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusPrjProjectMapper.queryPrjHomePageProjectInfoGridNew(prjProject);
    }

    @Override
    public List<HlsCusPrjProject> prjHomePageProjectInfoGridSecond(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusPrjProjectMapper.prjHomePageProjectInfoGridSecond(prjProject);
    }

    @Override
    public List<HlsCusPrjProject> conHomePageContractInfoGrid(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusPrjProjectMapper.conHomePageContractInfoGrid(prjProject);
    }

    @Autowired
    private HlsCreditLineService hlsCreditLineService;

    @Override
    public HlsCusPrjProject prjSubmitWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();

        prjProject.setProjectId(hlsCusPrjProject.getProjectId());

        prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProject);

        if ("APPROVING".equalsIgnoreCase(prjProject.getProjectStatus()) || "APPROVED".equalsIgnoreCase(prjProject.getProjectStatus())
                || "REJECTED".equalsIgnoreCase(prjProject.getProjectStatus()) || "CLOSED".equalsIgnoreCase(prjProject.getProjectStatus())) {
            throw new HlsCusException("该单据不可提交,请检查单据状态！");
        }

        if (hlsCusPrjProject.getAssistUnitId() != null) {
            prjProject.setAssistUnitId(hlsCusPrjProject.getAssistUnitId());
        }
        prjProject.setAssistUnitId(hlsCusPrjProjectMapper.queryAssistUnitId(prjProject.getAssistProjectManager()));

        Double financeAmount = 0.0;
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationService.queryPrjQuotationByProjectId(hlsCusPrjProject);
        for (HlsCusPrjQuotation hlsCusPrjQuotation : hlsCusPrjQuotationList) {
            financeAmount = hlsCusPrjQuotation.getFinanceAmount();
        }
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        prjProject.setFinanceAmount(financeAmount);
        hlsCusPrjProjectList.add(prjProject);
        //databaseLockProvider.lock(prjProject);
        //获取申请人
        /*HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);*/
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        //额度占用创建的尽调走 额度占用审批
        if ("CREDIT_NORMAL".equals(prjProject.getDataType())) {
            params.put("workFlowType", QUOTA_OCCUPATION_WFL);
        } else {
            params.put("workFlowType", PROJECT_REVIEW_WFL);
        }
        //表单的主键
        params.put("projectId", prjProject.getProjectId());
        params.put("businessKey", prjProject.getProjectId());
        Map<String, Object> evenParams = new HashMap<>();
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

        //修改项目状态为审批中
        prjProject.setProjectStatus("APPROVING");
        prjProject.setReviewStatus(String.valueOf(100));
        HlsCusPrjProject getPrj = new HlsCusPrjProject();
        getPrj.setProjectId(prjProject.getProjectId());
        prjProject.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getPrj).getObjectVersionNumber());
        prjProject = self().updateByPrimaryKeySelective(iRequest, prjProject);

        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjProject.getProjectName() + prjProject.getProjectNumber();
        evenParams.put("message", msg);
        evenParams.put("noticeTitle", "租赁项目审批");
        evenParams.put("noticeType", "NOTICE");
        evenParams.put("url", "");
        evenParams.put("level", 1L);
        //sysEventService.eventSave(iRequest, prjProject.getProjectId(), prjProject.getDocumentCategory(), prjProject.getDocumentType(), "PRJ", "PRJ_PROJECT", "P2D", evenParams);

        return prjProject;
    }


    @Override
    public HlsCusPrjProject prjSubmitSuppleWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject, Long prjSuppleId) throws HlsCusException {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(hlsCusPrjProject.getProjectId());
        prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProject);
        prjProject.setPrjSuppleId(prjSuppleId);

        String employeeEnableFlag = hlsCusPrjProjectMapper.queryEnableByAllocation(prjProject);
        prjProject.setEmployeeEnableFlag(employeeEnableFlag);

        if (hlsCusPrjProject.getAssistUnitId() != null) {
            prjProject.setAssistUnitId(hlsCusPrjProject.getAssistUnitId());
        }

        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(prjProject);
        databaseLockProvider.lock(prjProject);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "PROJECT_REPORT_SUPPLEMENT");
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

        HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        HlsCusPrjProjectSupplement cusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        //修改项目状态为审批中  --- 修改补充尽调 - 表
        if (prjSuppleId != null) {

            cusPrjProjectSupplement.setPrjSuppleId(prjSuppleId);
            cusPrjProjectSupplement.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectSupplement = hlsCusPrjProjectSupplementMapper.selectByPrimaryKey(cusPrjProjectSupplement);
            hlsCusPrjProjectSupplement.setSuppleStatus("APPROVING");
            hlsCusPrjProjectSupplement.setSuppleSubmitDate(new Date());
            hlsCusPrjProjectSupplementMapper.updateByPrimaryKeySelective(hlsCusPrjProjectSupplement);
        }

//        prjProject.setProjectStatus("APPROVING");
        HlsCusPrjProject getPrj = new HlsCusPrjProject();
        getPrj.setProjectId(prjProject.getProjectId());
        prjProject.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getPrj).getObjectVersionNumber());
        prjProject = self().updateByPrimaryKeySelective(iRequest, prjProject);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjProject.getProjectName() + prjProject.getProjectNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "补充尽调报告审批流程");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, prjProject.getProjectId(), prjProject.getDocumentCategory(), prjProject.getDocumentType(), "PRJ", "PRJ_PROJECT", "P2D", paramsEvent);

        return prjProject;
    }

    @Override
    public HlsCusPrjProject prjChangeSubmitWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();

        prjProject = self().selectByPrimaryKey(iRequest, hlsCusPrjProject);

        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(prjProject);
        databaseLockProvider.lock(prjProject);

        /*修改审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        //hlsCusChangeReqInfo.setDocumentCategory("PRJ_PROJECT");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus("APPROVING");
        hlsCusChangeReqInfo.setWflNodeStatus(null);
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.updateByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        HlsCusPrjProject prjProjectOld = new HlsCusPrjProject();
        prjProjectOld.setProjectId(hlsCusChangeReqInfo.getDocumentId());
        prjProjectOld = self().selectByPrimaryKey(iRequest, prjProjectOld);
        prjProjectOld.setProjectStatus("PENDING");
        self().updateByPrimaryKeySelective(iRequest, prjProjectOld);

        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "PRJ_PROJECT_AUDIT_CHANGE_WFL");

        //根据变更类型设置变更工作流
        if ("PROJECT_RISK_CHANGE".equals(hlsCusChangeReqInfo.getChangeType())) {

            //项目风险变更
            params.put("wflKey", "PRJ_PROJECT_RISK_CHANGE_WFL");
        } else if ("PROJECT_PRICE_CHANGE".equals(hlsCusChangeReqInfo.getChangeType())) {

            //项目价格变更
            params.put("wflKey", "PRJ_PROJECT_AUDIT_CHANGE_WFL");
        } else if ("PROJECT_PRICE_RISK_CHANGE".equals(hlsCusChangeReqInfo.getChangeType())) {

            //项目价格+风险变更
            params.put("wflKey", "PRJ_PROJECT_RISK_PRICE_CHANGE_WFL");
        }
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

        prjProject.setProjectStatus("APPROVING");
        HlsCusPrjProject getPrj = new HlsCusPrjProject();
        getPrj.setProjectId(prjProject.getProjectId());
        prjProject.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getPrj).getObjectVersionNumber());
        prjProject = self().updateByPrimaryKeySelective(iRequest, prjProject);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjProject.getProjectName() + "项目的项目变更" + prjProject.getProjectNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "项目变更");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, prjProject.getProjectId(), prjProject.getDocumentCategory(), prjProject.getDocumentType(), "BAC", "PRJ_PROJECT_WFL", "P2D", paramsEvent);

        return prjProject;
    }

    @Override
    public List<Map> queryPrjDetail(IRequest iRequest, HlsCusPrjProject prjProject) {
        return hlsCusPrjProjectMapper.queryPrjDetail(prjProject);
    }

    //项目专用，与合同分开
    @Override
    public List<Map> queryPrjDetailSecond(IRequest iRequest, HlsCusPrjProject prjProject) {
        return hlsCusPrjProjectMapper.queryPrjDetailSecond(prjProject);
    }

    @Override
    public void projectInfoCopy(IRequest iRequest, HlsCusPrjProject prjProject) {
        //客户信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = new ArrayList<>();
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectBpList = hlsCusPrjProjectBpService.select(iRequest, hlsCusPrjProjectBp, 1, 100000);
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            if (dt.getBpType() == "MORTGAGOR" || dt.getBpType() == "PLEDGOR") {
                //抵质押物不执行插入
            } else {
                HlsCusPrjProjectBp hlsCusPrjProjectBp1 = new HlsCusPrjProjectBp();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectBp1, map);
                hlsCusPrjProjectBp1.setProjectId(prjProject.getProjectId());
                hlsCusPrjProjectBp1.setBpCategroy("CON_CONTRACT");
                hlsCusPrjProjectBpService.insertSelective(iRequest, hlsCusPrjProjectBp1);
            }

        }

        //租前管理要求
//        List<ProjectCreditCondition> projectCreditConditions = new ArrayList<>();
//        ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
//        projectCreditCondition.setProjectId(prjProject.getRefProjectId());
//        projectCreditConditions = projectCreditConditionService.select(iRequest, projectCreditCondition, 1, 1000);
//        for (ProjectCreditCondition dt : projectCreditConditions) {
//            ProjectCreditCondition projectCreditCondition1 = new ProjectCreditCondition();
//            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
//            hlsBeanRefUtilService.setFieldValue(projectCreditCondition1, map);
//            projectCreditCondition1.setProjectId(prjProject.getProjectId());
//            projectCreditCondition1.setApprovalFlag("NEW");
//            projectCreditConditionService.insertSelective(iRequest, projectCreditCondition1);
//        }

        //租后管理要求
        List<HlsCusPrjSurviveRequire> hlsCusPrjSurviveRequires = new ArrayList<>();
        HlsCusPrjSurviveRequire hlsCusPrjSurviveRequire = new HlsCusPrjSurviveRequire();
        hlsCusPrjSurviveRequire.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjSurviveRequires = hlsCusPrjSurviveRequireService.select(iRequest, hlsCusPrjSurviveRequire, 1, 1000);
        for (HlsCusPrjSurviveRequire dt : hlsCusPrjSurviveRequires) {
            HlsCusPrjSurviveRequire hlsCusPrjSurviveRequire1 = new HlsCusPrjSurviveRequire();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjSurviveRequire1, map);
            hlsCusPrjSurviveRequire1.setProjectId(prjProject.getProjectId());
            hlsCusPrjSurviveRequireService.insertSelective(iRequest, hlsCusPrjSurviveRequire1);
        }

        //租赁物清单
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItems = new ArrayList<>();
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectLeaseItems = hlsCusPrjProjectLeaseItemService.select(iRequest, hlsCusPrjProjectLeaseItem, 1, 100000);
        String[] leaseItemIdList = prjProject.getItemIds().split(",");
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItems) {
            //判断是否是前端传入的租赁物id,是则复制,不是则跳过
            Long leaseItemId = dt.getLeaseItemId();
            if (Arrays.asList(leaseItemIdList).contains(leaseItemId.toString())) {
                HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem1 = new HlsCusPrjProjectLeaseItem();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectLeaseItem1, map);
                hlsCusPrjProjectLeaseItem1.setProjectId(prjProject.getProjectId());
                hlsCusPrjProjectLeaseItemService.insertSelective(iRequest, hlsCusPrjProjectLeaseItem1);
            }
        }

        List<HlsCusPrjProjectChanceMp> hlsCusPrjProjectChanceMps = new ArrayList<>();
        HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp = new HlsCusPrjProjectChanceMp();
        hlsCusPrjProjectChanceMp.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectChanceMps = hlsCusPrjProjectChanceMpService.select(iRequest, hlsCusPrjProjectChanceMp, 1, 100000);
        for (HlsCusPrjProjectChanceMp dt : hlsCusPrjProjectChanceMps) {
            HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp1 = new HlsCusPrjProjectChanceMp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectChanceMp1, map);
            hlsCusPrjProjectChanceMp1.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectChanceMpService.insertSelective(iRequest, hlsCusPrjProjectChanceMp1);
        }

        //报价从父项目复制过来 todo 用户不在尽调做报价,可能报价数据不全,需要额外设置值
        List<HlsCusPrjQuotation> hlsCusPrjQuotations = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(prjProject.getRefProjectId());
        hlsCusPrjQuotation.setDataClass("PRJ_PROJECT_INVEST");
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotations = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 100000);
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotations) {
            HlsCusPrjQuotation hlsCusPrjQuotationNew = new HlsCusPrjQuotation();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationNew, map);
            hlsCusPrjQuotationNew.setProjectId(prjProject.getProjectId());

            hlsCusPrjQuotationNew.setSourceDocumentId(prjProject.getProjectId());
            hlsCusPrjQuotationNew.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotationNew.setPaymentNumber(prjProject.getContractNumber());
            hlsCusPrjQuotationNew.setPaymentStatus("NEW");
            hlsCusPrjQuotationNew.setDataClass("VIRTUAL_CON");
            hlsCusPrjQuotationNew.setConfirmFlag("NEW");
            hlsCusPrjQuotationNew.setSelectedFlag("N");
            hlsCusPrjQuotationNew.setGeneratedStatus("NOT_GENERATED");
            hlsCusPrjQuotationNew.setAssetsSurplusValue(DEFAULT_ASSETS_SURPLUS_VALUE);
            hlsCusPrjQuotationNew.setDownPayment(0D);
            hlsCusPrjQuotationNew.setDeposit(0D);
            hlsCusPrjQuotationNew.setLeaseCharge(0D);
            hlsCusPrjQuotationNew.setAdvServiceFee(0D);
            hlsCusPrjQuotationNew.setDownPaymentRatio(0D);
            hlsCusPrjQuotationNew.setDepositRatio(0D);
            hlsCusPrjQuotationNew.setLeaseChargeRatio(0D);
            hlsCusPrjQuotationNew.setAdvServiceFeeRatio(0D);
            hlsCusPrjQuotationNew.setLeaseItemAmount(prjProject.getContractAmount());
            hlsCusPrjQuotationNew.setFinanceAmount(prjProject.getContractAmount());
            hlsCusPrjQuotationNew.setBusinessType(prjProject.getBusinessType());

            hlsCusPrjQuotationNew.setBaseRate(hlsCusPrjQuotationNew.getIntRate());
            hlsCusPrjQuotationNew.setIntRateType("FIXED");

            hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotationNew);
        }

        //租前息信息
        HlsCusConContractBeforeRentH hlsCusConContractBeforeRentH = new HlsCusConContractBeforeRentH();
        hlsCusConContractBeforeRentH.setProjectId(prjProject.getRefProjectId());
        List<HlsCusConContractBeforeRentH> hlsCusConContractBeforeRentHList = hlsCusConContractBeforeRentHService.select(iRequest, hlsCusConContractBeforeRentH, 1, 99999);
        for (HlsCusConContractBeforeRentH dto : hlsCusConContractBeforeRentHList) {
            HlsCusConContractBeforeRentH var = new HlsCusConContractBeforeRentH();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(var, map);
            var.setProjectId(prjProject.getProjectId());
            hlsCusConContractBeforeRentHService.insertSelective(iRequest, var);

        }

    }

    //项目变更复制从表信息
    @Override
    public void projectBackUp(IRequest iRequest, HlsCusPrjProject prjProject, Boolean noticeFlag) throws HlsCusException {

        //prj_project_bp(客户信息，承租人，联合承租人，抵质押物)
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = new ArrayList<>();
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectBpList = hlsCusPrjProjectBpService.select(iRequest, hlsCusPrjProjectBp, 1, 100000);
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            HlsCusPrjProjectBp hlsCusPrjProjectBp1 = new HlsCusPrjProjectBp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectBp1, map);
            hlsCusPrjProjectBp1.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectBpService.insertSelective(iRequest, hlsCusPrjProjectBp1);
        }

        //抵押物
        List<HlsCusPrjProjectChanceMp> hlsCusPrjProjectChanceMps = new ArrayList<>();
        HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp = new HlsCusPrjProjectChanceMp();
        hlsCusPrjProjectChanceMp.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectChanceMps = hlsCusPrjProjectChanceMpService.select(iRequest, hlsCusPrjProjectChanceMp, 1, 100000);
        for (HlsCusPrjProjectChanceMp dt : hlsCusPrjProjectChanceMps) {
            HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp1 = new HlsCusPrjProjectChanceMp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectChanceMp1, map);
            hlsCusPrjProjectChanceMp1.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectChanceMpService.insertSelective(iRequest, hlsCusPrjProjectChanceMp1);
        }

        //租赁物清单
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItems = new ArrayList<>();
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectLeaseItems = hlsCusPrjProjectLeaseItemService.select(iRequest, hlsCusPrjProjectLeaseItem, 1, 100000);
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItems) {
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem1 = new HlsCusPrjProjectLeaseItem();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectLeaseItem1, map);
            hlsCusPrjProjectLeaseItem1.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectLeaseItemService.insertSelective(iRequest, hlsCusPrjProjectLeaseItem1);
        }


        //报价信息
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(prjProject.getRefProjectId());
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 100000);
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            HlsCusPrjQuotation hlsCusPrjQuotation1 = new HlsCusPrjQuotation();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotation1, map);
            hlsCusPrjQuotation1.setSourceDocumentId(prjProject.getProjectId());
            hlsCusPrjQuotation1.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotation1.setPaymentNumber(prjProject.getContractNumber());
            hlsCusPrjQuotation1.setPaymentStatus("NEW");
            hlsCusPrjQuotation1.setChangeRefQuotationId(dt.getQuotationId());
            hlsCusPrjQuotation1 = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotation1);


            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            hlsCusPrjQuotationDetails.setQuotationId(dt.getQuotationId());
            List<HlsCusPrjQuotationDetails> detailList = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000);
            if (CollectionUtils.isNotEmpty(detailList)) {
                hlsCusPrjQuotationDetails = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000).get(0);
                HlsCusPrjQuotationDetails copyDetails = new HlsCusPrjQuotationDetails();
                Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotationDetails);
                hlsBeanRefUtilService.setFieldValue(copyDetails, map1);
                copyDetails.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                hlsCusPrjQuotationDetailsService.insertSelective(iRequest, copyDetails);
            }

            //插入对应现金流表(调整为用contract_cashflow数据覆盖以确保数据最新)
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setProjectId(prjProject.getRefProjectId());
            hlsCusConContract = hlsCusConContractMapper.select(hlsCusConContract).get(0);

            HlsCusConContractCashflow hlsCusConContractCashflowQuery = new HlsCusConContractCashflow();
            hlsCusConContractCashflowQuery.setContractId(hlsCusConContract.getContractId());
            List<HlsCusConContractCashflow> hlsCusConContractCashflowList = hlsCusConContractCashflowService.select(iRequest, hlsCusConContractCashflowQuery, 1, 100000);
            for (HlsCusConContractCashflow cashflow : hlsCusConContractCashflowList) {
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
                Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(cashflow);
                hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
                hlsCusPrjQuotationCashflow1.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow1);
            }

//            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
//            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
//            hlsCusPrjQuotationCashflow.setQuotationId(dt.getQuotationId());
//            hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowService.select(iRequest, hlsCusPrjQuotationCashflow, 1, 100000);
//            for (HlsCusPrjQuotationCashflow dto : hlsCusPrjQuotationCashflows) {
//                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
//                Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
//                hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
//                hlsCusPrjQuotationCashflow1.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
//                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow1);
//            }
        }

        //复制资料清单
        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentsOld = new ArrayList<>();
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachmentOld = new HlsCusPrjProjectAttachment();
        hlsCusPrjProjectAttachmentOld.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectAttachmentsOld = hlsCusPrjProjectAttachmentService.select(iRequest, hlsCusPrjProjectAttachmentOld, 1, 100000);
        for (HlsCusPrjProjectAttachment dt : hlsCusPrjProjectAttachmentsOld) {

            //项目附件表
            HlsCusPrjProjectAttachment hpa = new HlsCusPrjProjectAttachment();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hpa, map);
            hpa.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectAttachmentService.insertSelective(iRequest, hpa);
            //复制系统附件表
            if ("PRJ_PROJECT_JD".equals(dt.getProjectAttachmentCategory())) {
                copyFndAtmFile(iRequest, String.valueOf(dt.getProjectAttachmentId()), String.valueOf(hpa.getProjectAttachmentId()), "PRJ_PROJECT_JD");
            } else {
                copyFndAtmFile(iRequest, String.valueOf(dt.getProjectAttachmentId()), String.valueOf(hpa.getProjectAttachmentId()), "PRJ_PROJECT_ATTACHMENT");
            }
        }

        //付款前提条件
        List<ProjectCreditCondition> projectCreditConditionsOld = new ArrayList<>();
        ProjectCreditCondition projectCreditConditionOld = new ProjectCreditCondition();
        projectCreditConditionOld.setProjectId(prjProject.getRefProjectId());
        projectCreditConditionsOld = projectCreditConditionService.select(iRequest, projectCreditConditionOld, 1, 100000);
        for (ProjectCreditCondition dto : projectCreditConditionsOld) {
            ProjectCreditCondition pcc = new ProjectCreditCondition();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(pcc, map);
            pcc.setProjectId(prjProject.getProjectId());
            pcc.setRefCreditConditionId(dto.getCreditConditionId());
            projectCreditConditionService.insertSelective(iRequest, pcc);
        }

        //存续期要求
        List<HlsCusPrjSurviveRequire> hlsCusPrjSurviveRequiresOld = new ArrayList<>();
        HlsCusPrjSurviveRequire hlsCusPrjSurviveRequireOld = new HlsCusPrjSurviveRequire();
        hlsCusPrjSurviveRequireOld.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjSurviveRequiresOld = hlsCusPrjSurviveRequireService.select(iRequest, hlsCusPrjSurviveRequireOld, 1, 100000);
        for (HlsCusPrjSurviveRequire dto : hlsCusPrjSurviveRequiresOld) {
            HlsCusPrjSurviveRequire psr = new HlsCusPrjSurviveRequire();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(psr, map);
            psr.setProjectId(prjProject.getProjectId());
            psr.setRefPrjSurviveId(dto.getPrjSurviveId());
            hlsCusPrjSurviveRequireService.insertSelective(iRequest, psr);
        }

        //授信方案
        List<HlsCreditPlan> hlsCreditPlanList = new ArrayList<>();
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(prjProject.getRefProjectId());
        hlsCreditPlan.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCreditPlanList = hlsCreditPlanService.select(iRequest, hlsCreditPlan, 1, 100000);
        for (HlsCreditPlan dt : hlsCreditPlanList) {
            HlsCreditPlan hlsCreditPlan1 = new HlsCreditPlan();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCreditPlan1, map);
            hlsCreditPlan1.setSourceDocumentId(prjProject.getProjectId());
            hlsCreditPlan1.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCreditPlan1 = hlsCreditPlanService.insertSelective(iRequest, hlsCreditPlan1);
        }

        //批复参数
        List<PrjCreditReplyPara> creditReplyParaList = new ArrayList<>();
        PrjCreditReplyPara replyPara = new PrjCreditReplyPara();
        replyPara.setReplyId(prjProject.getRefProjectId());
        creditReplyParaList = replyParaService.select(iRequest, replyPara, 1, 100000);
        for (PrjCreditReplyPara rPara : creditReplyParaList) {
            PrjCreditReplyPara item = rPara;
            item.setReplyParaId(null);
            item.setReplyId(prjProject.getProjectId());
            item = replyParaService.insertSelective(iRequest, item);
        }
        //批复产品
        List<ReplyProduct> replyProductList = new ArrayList<>();
        ReplyProduct replyProduct = new ReplyProduct();
        replyProduct.setReplyId(prjProject.getRefProjectId());
        replyProductList = replyProductService.select(iRequest, replyProduct, 1, 100000);
        for (ReplyProduct dt : replyProductList) {
            ReplyProduct item = new ReplyProduct();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(item, map);
            item.setReplyId(prjProject.getProjectId());
            item = replyProductService.insertSelective(iRequest, item);

            //产品参数
            ReplyProductPara productPara = new ReplyProductPara();
            productPara.setReplyProductId(dt.getReplyProductId());
            List<ReplyProductPara> productParaList = replyProductParaMapper.select(productPara);
            for (ReplyProductPara ppara : productParaList) {
                ReplyProductPara pitem = ppara;
                pitem.setReplyProductParaId(null);
                pitem.setReplyProductId(item.getReplyProductId());
                replyProductParaService.insertSelective(iRequest, pitem);
            }
        }


        //以下的数据复制前先判断,变更创建时不复制,变更通过时复制到主数据
        if (noticeFlag) {
            //prj_project_approval 审议信息 t.project_id = to_char(#{projectId}) PrjProjectApproval
            List<PrjProjectApproval> prjProjectApprovalList = new ArrayList<>();
            PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
            prjProjectApproval.setProjectId(prjProject.getRefProjectId().toString());
            prjProjectApprovalList = projectApprovalService.select(iRequest, prjProjectApproval, 1, 100000);
            PrjProjectApproval approvalOld = null;
            PrjProjectApproval approvalNew = null;
            if (prjProjectApprovalList.size() > 0) {
                approvalOld = prjProjectApprovalList.get(0);
                PrjProjectApproval prjProjectApproval1 = new PrjProjectApproval();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(approvalOld);
                hlsBeanRefUtilService.setFieldValue(prjProjectApproval1, map);
                prjProjectApproval1.setProjectId(prjProject.getProjectId().toString());
                approvalNew = projectApprovalService.insertSelective(iRequest, prjProjectApproval1);
            }

            if (approvalNew == null) {
                return;
            }

            //prj_project_meeting_approver 评审及表决结果汇总 ProjectMeetingApprover
            List<ProjectMeetingApprover> projectMeetingApproverlList = new ArrayList<>();
            ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
            //projectMeetingApprover.setProjectId(prjProject.getRefProjectId());
            projectMeetingApprover.setApprovalId(approvalOld.getApprovalId());
            projectMeetingApproverlList = projectMeetingApproverService.select(iRequest, projectMeetingApprover, 1, 100000);
            for (ProjectMeetingApprover dt : projectMeetingApproverlList) {
                ProjectMeetingApprover projectMeetingApprover1 = new ProjectMeetingApprover();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(projectMeetingApprover1, map);
                //projectMeetingApprover1.setProjectId(prjProject.getProjectId());
                projectMeetingApprover1.setApprovalId(approvalNew.getApprovalId());
                projectMeetingApprover1 = projectMeetingApproverService.insertSelective(iRequest, projectMeetingApprover1);
            }

            //prj_approval_condition 投前/后管理要求 ProjectApprovalCondition
            List<ProjectApprovalCondition> projectApprovalConditionList = new ArrayList<>();
            ProjectApprovalCondition projectApprovalCondition = new ProjectApprovalCondition();
            //projectMeetingApprover.setProjectId(prjProject.getRefProjectId());
            projectApprovalCondition.setApprovalId(approvalOld.getApprovalId());
            projectApprovalConditionList = projectApprovalConditionService.select(iRequest, projectApprovalCondition, 1, 100000);
            for (ProjectApprovalCondition dt : projectApprovalConditionList) {
                ProjectApprovalCondition projectMeetingApprover1 = new ProjectApprovalCondition();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(projectMeetingApprover1, map);
                //projectMeetingApprover1.setProjectId(prjProject.getProjectId());
                projectMeetingApprover1.setApprovalId(approvalNew.getApprovalId());
                projectMeetingApprover1 = projectApprovalConditionService.insertSelective(iRequest, projectMeetingApprover1);
            }
        }

    }

    //项目变更复制从表信息（不复制附件）
    public void projectBackUpNoAttachment(IRequest iRequest, HlsCusPrjProject prjProject, Boolean noticeFlag) throws HlsCusException {

        //prj_project_bp(客户信息，承租人，联合承租人，抵质押物)
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = new ArrayList<>();
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectBpList = hlsCusPrjProjectBpService.select(iRequest, hlsCusPrjProjectBp, 1, 100000);
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            HlsCusPrjProjectBp hlsCusPrjProjectBp1 = new HlsCusPrjProjectBp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectBp1, map);
            hlsCusPrjProjectBp1.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectBpService.insertSelective(iRequest, hlsCusPrjProjectBp1);
        }

        //租赁物清单
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItems = new ArrayList<>();
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjProjectLeaseItems = hlsCusPrjProjectLeaseItemService.select(iRequest, hlsCusPrjProjectLeaseItem, 1, 100000);
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItems) {
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem1 = new HlsCusPrjProjectLeaseItem();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectLeaseItem1, map);
            hlsCusPrjProjectLeaseItem1.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectLeaseItemService.insertSelective(iRequest, hlsCusPrjProjectLeaseItem1);
        }

        //报价信息
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(prjProject.getRefProjectId());
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 100000);
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            HlsCusPrjQuotation hlsCusPrjQuotation1 = new HlsCusPrjQuotation();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotation1, map);
            hlsCusPrjQuotation1.setSourceDocumentId(prjProject.getProjectId());
            hlsCusPrjQuotation1.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotation1.setPaymentNumber(prjProject.getContractNumber());
            hlsCusPrjQuotation1.setPaymentStatus("NEW");
            hlsCusPrjQuotation1.setChangeRefQuotationId(dt.getQuotationId());
            hlsCusPrjQuotation1 = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotation1);


            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            hlsCusPrjQuotationDetails.setQuotationId(dt.getQuotationId());
            List<HlsCusPrjQuotationDetails> detailList = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000);
            if (CollectionUtils.isNotEmpty(detailList)) {
                hlsCusPrjQuotationDetails = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000).get(0);
                HlsCusPrjQuotationDetails copyDetails = new HlsCusPrjQuotationDetails();
                Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotationDetails);
                hlsBeanRefUtilService.setFieldValue(copyDetails, map1);
                copyDetails.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                hlsCusPrjQuotationDetailsService.insertSelective(iRequest, copyDetails);
            }

            //插入对应现金流表
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflow.setQuotationId(dt.getQuotationId());
            hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowService.select(iRequest, hlsCusPrjQuotationCashflow, 1, 100000);
            for (HlsCusPrjQuotationCashflow dto : hlsCusPrjQuotationCashflows) {
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
                Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
                hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
                hlsCusPrjQuotationCashflow1.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                if (dto.getCfItem() != 90 && dto.getCfItem() != 91) {
                    hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow1);
                } else {
                    //对承兑汇票现金流单独处理
                    if (dto.getCfItem() == 91) {
                        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow2 = new HlsCusPrjQuotationCashflow();
                        hlsCusPrjQuotationCashflow2.setQuotationCashflowId(dto.getSourceCashflowId());
                        hlsCusPrjQuotationCashflow2 = hlsCusPrjQuotationCashflowService.selectByPrimaryKey(iRequest, hlsCusPrjQuotationCashflow2);
                        hlsCusPrjQuotationCashflow2.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                        hlsCusPrjQuotationCashflow2.setQuotationCashflowId(null);
                        hlsCusPrjQuotationCashflow2 = hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow2);
                        hlsCusPrjQuotationCashflow1.setSourceCashflowId(hlsCusPrjQuotationCashflow2.getQuotationCashflowId());
                        hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow1);
                    }

                }


            }
        }

        //付款前提条件
        List<ProjectCreditCondition> projectCreditConditionsOld = new ArrayList<>();
        ProjectCreditCondition projectCreditConditionOld = new ProjectCreditCondition();
        projectCreditConditionOld.setProjectId(prjProject.getRefProjectId());
        projectCreditConditionsOld = projectCreditConditionService.select(iRequest, projectCreditConditionOld, 1, 100000);
        for (ProjectCreditCondition dto : projectCreditConditionsOld) {
            ProjectCreditCondition pcc = new ProjectCreditCondition();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(pcc, map);
            pcc.setProjectId(prjProject.getProjectId());
            pcc.setRefCreditConditionId(dto.getCreditConditionId());
            projectCreditConditionService.insertSelective(iRequest, pcc);
        }

        //存续期要求
        List<HlsCusPrjSurviveRequire> hlsCusPrjSurviveRequiresOld = new ArrayList<>();
        HlsCusPrjSurviveRequire hlsCusPrjSurviveRequireOld = new HlsCusPrjSurviveRequire();
        hlsCusPrjSurviveRequireOld.setProjectId(prjProject.getRefProjectId());
        hlsCusPrjSurviveRequiresOld = hlsCusPrjSurviveRequireService.select(iRequest, hlsCusPrjSurviveRequireOld, 1, 100000);
        for (HlsCusPrjSurviveRequire dto : hlsCusPrjSurviveRequiresOld) {
            HlsCusPrjSurviveRequire psr = new HlsCusPrjSurviveRequire();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(psr, map);
            psr.setProjectId(prjProject.getProjectId());
            psr.setRefPrjSurviveId(dto.getPrjSurviveId());
            hlsCusPrjSurviveRequireService.insertSelective(iRequest, psr);
        }

        if (noticeFlag) {
            //复制授信审批通知书
            List<HlsCusProjectCreditNotice> hlsCusProjectCreditNotices = new ArrayList<>();
            HlsCusProjectCreditNotice hlsCusProjectCreditNotice = new HlsCusProjectCreditNotice();
            hlsCusProjectCreditNotice.setProjectId(prjProject.getRefProjectId());
            hlsCusProjectCreditNotices = iHlsCusProjectCreditNoticeService.select(iRequest, hlsCusProjectCreditNotice, 1, 100000);
            for (HlsCusProjectCreditNotice dto : hlsCusProjectCreditNotices) {
                HlsCusProjectCreditNotice notice = new HlsCusProjectCreditNotice();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dto);
                hlsBeanRefUtilService.setFieldValue(notice, map);
                notice.setProjectId(prjProject.getProjectId());
                iHlsCusProjectCreditNoticeService.insertSelective(iRequest, notice);
            }
        }

    }

    //复制系统附件表
    void copyFndAtmFile(IRequest iRequest, String oldPkValue, String newPkValue, String tableName) throws HlsCusException {

        if (StringUtils.isEmpty(oldPkValue) || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(newPkValue)) {
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

                fndAttachment.setAttachmentId(attachmentMulti.getAttachmentId());
                iFndAttachmentService.insert(iRequest, fndAttachment);

                attachmentMulti.setAttachmentId(fndAttachment.getAttachmentId());
                iFndAttachmentMultiService.updateByPrimaryKeySelective(iRequest, attachmentMulti);
            }
        }

    }

    //保存chang_req
    @Override
    public HlsCusPrjProject changeCreate(IRequest request, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {

        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(hlsCusPrjProject.getProjectId());

        //锁表
        //databaseLockProvider.lock(project);

        //校验项目状态
        project = hlsCusPrjProjectService.selectByPrimaryKey(request, project);
        if ("NEW".equals(project.getProjectStatus()) || "REJECTED".equals(project.getProjectStatus())) {
            throw new HlsCusException("当前项目无需进行变更,可直接维护信息!");
        }
        if ("APPROVING".equals(project.getProjectStatus())) {
            throw new HlsCusException("当前项目正在审批中，无法进行变更!");
        }

        /*单据状态置为挂起*/
        project.setProjectStatus("PENDING");
        project.setMeetingStatus("PENDING");
        project.set__status("update");

        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, project);

        /*插入审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setDocumentId(project.getProjectId());
        hlsCusChangeReqInfo.setDocumentCategory("PRJ_PROJECT");
        List<HlsCusChangeReqInfo> hlsCusChangeReqInfoList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);

        hlsCusChangeReqInfo.setStatus("NEW");
        List<HlsCusChangeReqInfo> newChangeReqList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);
        if (newChangeReqList != null && newChangeReqList.size() > 0) {
            throw new HlsCusException("该项目正在变更中!");
        }

        hlsCusChangeReqInfo.setDocumentVersionId(hlsCusChangeReqInfoList.size() + ONE);
        hlsCusChangeReqInfo.setChangeReqUserId(request.getUserId());
        hlsCusChangeReqInfo.setChangeDescription(hlsCusPrjProject.getChangeDescription());
        hlsCusChangeReqInfo.setChangeReqDate(new Date());
        hlsCusChangeReqInfo.setChangeType(hlsCusPrjProject.getChangeType());
        hlsCusChangeReqInfo.setApproveNumber(hlsCusPrjProject.getApproveNumber());
        hlsCusChangeReqInfo.setChangeInfoDesc(hlsCusPrjProject.getChangeInfoDesc());
        hlsCusChangeReqInfo.setChangeEffctDesc(hlsCusPrjProject.getChangeEffctDesc());
        //创建时设置为N，流程结束后再修改为Y
        hlsCusChangeReqInfo.setInstanceEndFlag("N");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.insertSelective(request, hlsCusChangeReqInfo);

        /*复制当前项目，创建变更数据*/
        hlsCusPrjProject = selectByPrimaryKey(request, hlsCusPrjProject);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjProject);
        hlsBeanRefUtilService.setFieldValue(prjProject, map1);
        prjProject.setRefProjectId(prjProject.getProjectId());
        prjProject.setProjectId(null);
        prjProject.setDataType("CHANGE_REQ");
        //prjProject.setProjectStatus("NEW");
        prjProject.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());

        prjProject.setReviewComments(null);

        //排除上会信息
        prjProject.setApprovalStatus("");
        prjProject.setApprovalId(null);
        prjProject.setVoteComment("");
        prjProject.setVoteResult("");
        prjProject.setVoteStatus("");
        prjProject.setMeetingProcessInstanceId("");
        prjProject.setProcessInstanceId(null);
        //修改会议类型，便于上会时区分
        prjProject.setMeetingType(PROJECT_CREDIT_CHANGE);
        prjProject = hlsCusPrjProjectService.insertSelective(request, prjProject);

        //复制一份用作历史的数据, dataType:CHANGE_REQ_HISTORY, 在变更结束后改为HISTORY
        HlsCusPrjProject prjChanceH = new HlsCusPrjProject();
        hlsBeanRefUtilService.setFieldValue(prjChanceH, map1);
        prjChanceH.setRefProjectId(prjChanceH.getProjectId());
        prjChanceH.setProjectId(null);
        prjChanceH.setDataType("CHANGE_REQ_HISTORY");
        //prjChanceH.setProjectStatus("NEW");
        prjChanceH.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        prjChanceH = self().insertSelective(request, prjChanceH);

        //插入从表（不复制附件）
        projectBackUp(request, prjProject, true);//变更单据会重新维护上会,不复制    方案更改,项目批复变更复用逻辑,需要复制上会信息
        projectBackUp(request, prjChanceH, true);//历史数据需要复制上会信息

        //查询打开链接需要的参数
        HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();
        cusPrjProject.setProjectId(prjProject.getProjectId());
        List<HlsCusPrjProject> list = hlsCusPrjProjectMapper.selectProjectChangeReqInfo(cusPrjProject);

        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
            cusPrjProject = list.get(0);
        }

        //直接发起审批
        phChangeSubmit(request, prjProject);

        return cusPrjProject;
    }

    private void phChangeSubmit(IRequest iRequest, HlsCusPrjProject prjProject) {
        //直接发起变更
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(prjProject);
        /*修改审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        //hlsCusChangeReqInfo.setDocumentCategory("PRJ_PROJECT");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus("APPROVING");
        hlsCusChangeReqInfo.setWflNodeStatus(null);
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.updateByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        HlsCusPrjProject prjProjectOld = new HlsCusPrjProject();
        prjProjectOld.setProjectId(hlsCusChangeReqInfo.getDocumentId());
        prjProjectOld = self().selectByPrimaryKey(iRequest, prjProjectOld);
        prjProjectOld.setProjectStatus("PENDING");
        self().updateByPrimaryKeySelective(iRequest, prjProjectOld);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "PRJ_PROJECT_AUDIT_CHANGE_WFL");
        params.put("wflKey", "PRJ_PROJECT_AUDIT_CHANGE_WFL");
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

        //prjProject.setProjectStatus("APPROVING");
        HlsCusPrjProject getPrj = new HlsCusPrjProject();
        getPrj.setProjectId(prjProject.getProjectId());
        prjProject.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getPrj).getObjectVersionNumber());
        prjProject = self().updateByPrimaryKeySelective(iRequest, prjProject);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjProject.getProjectName() + "项目的项目变更" + prjProject.getProjectNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "项目变更");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, prjProject.getProjectId(), prjProject.getDocumentCategory(), prjProject.getDocumentType(), "BAC", "PRJ_PROJECT_WFL", "P2D", paramsEvent);
    }

    @Override
    public HlsCusPrjProject cancelSubmit(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(hlsCusPrjProject.getProjectId());
        prjProject = self().selectByPrimaryKey(iRequest, prjProject);
        prjProject.setCancelDate(hlsCusPrjProject.getCancelDate());
        prjProject.setCancelPerson(hlsCusPrjProject.getCancelPerson());
        prjProject.setCancelReasion(hlsCusPrjProject.getCancelReasion());
        //将虚拟合同的状态改为PENDING，原状态为SIGN,起租合同无法撤销
        prjProject.setContractStatus("PENDING");
        prjProject = self().updateByPrimaryKey(iRequest, prjProject);
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(prjProject);
        databaseLockProvider.lock(prjProject);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CON_CONTRACT_CANCEL_WFL");
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjProject.getContractName() + "合同的合同撤销审核" + prjProject.getContractNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "租赁合同撤销审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, prjProject.getProjectId(), prjProject.getDocumentCategory(), prjProject.getDocumentType(), "PRJ", "PRJ_PROJECT", "P2D", paramsEvent);

        return prjProject;
    }

    @Override
    public List<HlsCusPrjProject> historyPrjQuery(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject) {
        return hlsCusPrjProjectMapper.historyPrjQuery(hlsCusPrjProject);
    }


    @Override
    public void deleteOld(Long projectIdOld, IRequest request) {

        //删除客户信息
        HlsCusPrjProject oldProject = new HlsCusPrjProject();
        oldProject.setProjectId(projectIdOld);
        hlsCusPrjProjectBpMapper.deleteBpByProjectId(oldProject);

        /*
        删除报价信息
        *
        *1 查询项目下所有的报价，先删除details表，跟现金流表
        */
        List<HlsCusPrjQuotation> quotationList = hlsCusPrjQuotationMapper.selectQuotationByProjectId(oldProject);
        for (HlsCusPrjQuotation quotation : quotationList) {

            hlsCusPrjQuotationDetailsService.deleteDetailsById(quotation);

            hlsCusPrjQuotationCashflowMapper.deleteByQuotationId(quotation);

            hlsCusPrjQuotationMapper.deleteByPrimaryKey(quotation);
        }

        //删除租赁物信息
        hlsCusPrjProjectLeaseItemMapper.deleteLeaseItemByProjectId(oldProject.getProjectId());

        //删除附件表
        List<HlsCusPrjProjectAttachment> attachmentList = attachmentMapper.queryByProjectId(oldProject.getProjectId());

        for (HlsCusPrjProjectAttachment attachment : attachmentList) {

            //删除系统附件表
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
            fndAttachmentMulti.setTablePkValue(String.valueOf(attachment.getProjectAttachmentId()));

            List<FndAttachmentMulti> multiList = fndAttachmentMultiMapper.select(fndAttachmentMulti);

            for (FndAttachmentMulti multi : multiList) {
                attachment.setAttachmentId(multi.getAttachmentId().toString());
                attachmentMapper.deleteFndAtmAttachment(attachment);
                fndAttachmentMultiMapper.deleteByPrimaryKey(multi);
            }

            attachmentMapper.deleteByPrimaryKey(attachment);
        }

        //付款前提条件
        ProjectCreditCondition condition = new ProjectCreditCondition();
        condition.setProjectId(oldProject.getProjectId());
        projectCreditConditionMapper.deleteCreditConditionByProjectId(condition);

        //存续期要求
        HlsCusPrjSurviveRequire require = new HlsCusPrjSurviveRequire();
        require.setProjectId(oldProject.getProjectId());
        hlsCusPrjSurviveRequireMapper.deleteSurviveRequireByProjectId(require);

        //审议
        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setProjectId(oldProject.getProjectId().toString());
        prjProjectApprovalMapper.delete(prjProjectApproval);

        //授信审批通知书
        String processDefinitionId = request.getAttribute("processDefinitionId");
        if (!"PRJ_PROJECT_AUDIT_CHANGE_WFL".equalsIgnoreCase(processDefinitionId)) {
            HlsCusProjectCreditNotice hlsCusProjectCreditNotice = new HlsCusProjectCreditNotice();
            hlsCusProjectCreditNotice.setProjectId(oldProject.getProjectId());
            cusProjectCreditNoticeMapper.deleteNoticeByProjectId(hlsCusProjectCreditNotice);
        }

        //授信方案
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(projectIdOld);
        hlsCreditPlan.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCreditPlanMapper.delete(hlsCreditPlan);


        //prj_project_meeting_approver 评审及表决结果汇总 ProjectMeetingApprover
        ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
        projectMeetingApprover.setProjectId(oldProject.getProjectId());
        projectMeetingApproverMapper.delete(projectMeetingApprover);

        //prj_approval_condition 投前/后管理要求 ProjectApprovalCondition
        ProjectApprovalCondition projectApprovalCondition = new ProjectApprovalCondition();
        projectApprovalCondition.setProjectId(oldProject.getProjectId());
        projectApprovalConditionMapper.delete(projectApprovalCondition);

        //抵押物删除
        HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp = new HlsCusPrjProjectChanceMp();
        hlsCusPrjProjectChanceMp.setProjectId(oldProject.getProjectId());
        hlsCusPrjProjectChanceMpMapper.delete(hlsCusPrjProjectChanceMp);

        //批复参数
        PrjCreditReplyPara replyPara = new PrjCreditReplyPara();
        replyPara.setReplyId(projectIdOld);
        replyParaMapper.delete(replyPara);
        //批复产品
        ReplyProduct replyProduct = new ReplyProduct();
        replyProduct.setReplyId(projectIdOld);
        List<ReplyProduct> ReplyProductList = replyProductMapper.select(replyProduct);
        for (ReplyProduct product : ReplyProductList) {
            //删除产品参数
            ReplyProductPara productPara = new ReplyProductPara();
            productPara.setReplyProductId(product.getReplyProductId());
            List<ReplyProductPara> productParaList = replyProductParaMapper.select(productPara);

            for (ReplyProductPara ppara : productParaList) {
                ppara.setReplyProductParaId(ppara.getReplyProductParaId());
                replyProductParaMapper.deleteByPrimaryKey(ppara);
            }
            replyProductMapper.delete(product);
        }

    }

    @Override
    public void updateOld(IRequest iRequest, Long projectIdOld, Long projectIdNew, IRequest request) throws HlsCusException {

        //prj_project_bp(客户信息，承租人，联合承租人，抵质押物)
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = new ArrayList<>();
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(projectIdNew);
        hlsCusPrjProjectBpList = hlsCusPrjProjectBpService.select(iRequest, hlsCusPrjProjectBp, 1, 100000);
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            HlsCusPrjProjectBp hlsCusPrjProjectBp1 = new HlsCusPrjProjectBp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectBp1, map);
            hlsCusPrjProjectBp1.setProjectId(projectIdOld);
            hlsCusPrjProjectBpService.insertSelective(iRequest, hlsCusPrjProjectBp1);
        }

        //租赁物清单
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItems = new ArrayList<>();
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(projectIdNew);
        hlsCusPrjProjectLeaseItems = hlsCusPrjProjectLeaseItemService.select(iRequest, hlsCusPrjProjectLeaseItem, 1, 100000);
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItems) {
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem1 = new HlsCusPrjProjectLeaseItem();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectLeaseItem1, map);
            hlsCusPrjProjectLeaseItem1.setProjectId(projectIdOld);
            hlsCusPrjProjectLeaseItemService.insertSelective(iRequest, hlsCusPrjProjectLeaseItem1);
        }

        //抵押物
        List<HlsCusPrjProjectChanceMp> hlsCusPrjProjectChanceMps = new ArrayList<>();
        HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp = new HlsCusPrjProjectChanceMp();
        hlsCusPrjProjectChanceMp.setProjectId(projectIdNew);
        hlsCusPrjProjectChanceMps = hlsCusPrjProjectChanceMpService.select(iRequest, hlsCusPrjProjectChanceMp, 1, 100000);
        for (HlsCusPrjProjectChanceMp dt : hlsCusPrjProjectChanceMps) {
            HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp1 = new HlsCusPrjProjectChanceMp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectChanceMp1, map);
            hlsCusPrjProjectChanceMp1.setProjectId(projectIdOld);
            hlsCusPrjProjectChanceMpService.insertSelective(iRequest, hlsCusPrjProjectChanceMp1);
        }

        //付款前提条件
        List<ProjectCreditCondition> conditionList = new ArrayList<>();
        ProjectCreditCondition condition = new ProjectCreditCondition();
        condition.setProjectId(projectIdNew);
        conditionList = projectCreditConditionService.select(iRequest, condition, 1, 100000);
        for (ProjectCreditCondition creditCondition : conditionList) {
            ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(creditCondition);
            hlsBeanRefUtilService.setFieldValue(projectCreditCondition, map);
            projectCreditCondition.setProjectId(projectIdOld);
            projectCreditConditionService.insertSelective(iRequest, projectCreditCondition);
        }

        //存续期要求
        List<HlsCusPrjSurviveRequire> requireList = new ArrayList<>();
        HlsCusPrjSurviveRequire require = new HlsCusPrjSurviveRequire();
        require.setProjectId(projectIdNew);
        requireList = hlsCusPrjSurviveRequireService.select(iRequest, require, 1, 100000);
        for (HlsCusPrjSurviveRequire surviveRequire : requireList) {
            HlsCusPrjSurviveRequire projectCreditCondition = new HlsCusPrjSurviveRequire();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(surviveRequire);
            hlsBeanRefUtilService.setFieldValue(projectCreditCondition, map);
            projectCreditCondition.setProjectId(projectIdOld);
            hlsCusPrjSurviveRequireService.insertSelective(iRequest, projectCreditCondition);
        }

        //报价信息
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(projectIdNew);
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 100000);
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            HlsCusPrjQuotation hlsCusPrjQuotation1 = new HlsCusPrjQuotation();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotation1, map);
            hlsCusPrjQuotation1.setSourceDocumentId(projectIdOld);
            hlsCusPrjQuotation1.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotation1 = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotation1);


            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            hlsCusPrjQuotationDetails.setQuotationId(dt.getQuotationId());
            List<HlsCusPrjQuotationDetails> detailList = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000);
            if (CollectionUtils.isNotEmpty(detailList)) {
                hlsCusPrjQuotationDetails = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000).get(0);
                HlsCusPrjQuotationDetails copyDetails = new HlsCusPrjQuotationDetails();
                Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotationDetails);
                hlsBeanRefUtilService.setFieldValue(copyDetails, map1);
                copyDetails.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                hlsCusPrjQuotationDetailsService.insertSelective(iRequest, copyDetails);
            }

            //插入对应现金流表
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflow.setQuotationId(dt.getQuotationId());
            hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowService.select(iRequest, hlsCusPrjQuotationCashflow, 1, 100000);
            for (HlsCusPrjQuotationCashflow dto : hlsCusPrjQuotationCashflows) {
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
                Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
                hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
                hlsCusPrjQuotationCashflow1.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflow1);
            }
        }

        //复制资料清单
        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentsOld = new ArrayList<>();
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachmentOld = new HlsCusPrjProjectAttachment();
        hlsCusPrjProjectAttachmentOld.setProjectId(projectIdNew);
        hlsCusPrjProjectAttachmentsOld = hlsCusPrjProjectAttachmentService.select(iRequest, hlsCusPrjProjectAttachmentOld, 1, 100000);
        for (HlsCusPrjProjectAttachment dt : hlsCusPrjProjectAttachmentsOld) {

            //项目附件表
            HlsCusPrjProjectAttachment hpa = new HlsCusPrjProjectAttachment();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hpa, map);
            hpa.setProjectId(projectIdOld);
            hlsCusPrjProjectAttachmentService.insertSelective(iRequest, hpa);

            //复制系统附件表
            copyFndAtmFile(iRequest, String.valueOf(dt.getProjectAttachmentId()), String.valueOf(hpa.getProjectAttachmentId()), "PRJ_PROJECT_ATTACHMENT");
        }

        //授信审批通知书
       /* String processDefinitionId = request.getAttribute("processDefinitionId");
        if (!"PRJ_PROJECT_AUDIT_CHANGE_WFL".equalsIgnoreCase(processDefinitionId)) {
            List<HlsCusProjectCreditNotice> hlsCusProjectCreditNotices = new ArrayList<>();
            HlsCusProjectCreditNotice hlsCusProjectCreditNotice = new HlsCusProjectCreditNotice();
            hlsCusProjectCreditNotice.setProjectId(projectIdNew);
            hlsCusProjectCreditNotices = iHlsCusProjectCreditNoticeService.select(iRequest, hlsCusProjectCreditNotice, 1, 100000);
            for (HlsCusProjectCreditNotice dto : hlsCusProjectCreditNotices) {
                HlsCusProjectCreditNotice notice = new HlsCusProjectCreditNotice();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dto);
                hlsBeanRefUtilService.setFieldValue(notice, map);
                notice.setProjectId(projectIdOld);
                iHlsCusProjectCreditNoticeService.insertSelective(iRequest, notice);
            }
        }*/

        //授信方案
        List<HlsCreditPlan> hlsCreditPlanList = new ArrayList<>();
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(projectIdNew);
        hlsCreditPlan.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCreditPlanList = hlsCreditPlanService.select(iRequest, hlsCreditPlan, 1, 100000);
        for (HlsCreditPlan dt : hlsCreditPlanList) {
            HlsCreditPlan hlsCreditPlan1 = new HlsCreditPlan();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCreditPlan1, map);
            hlsCreditPlan1.setSourceDocumentId(projectIdOld);
            hlsCreditPlan1.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCreditPlan1 = hlsCreditPlanService.insertSelective(iRequest, hlsCreditPlan1);
        }

        //审议
        List<PrjProjectApproval> prjProjectApprovalList = new ArrayList<>();
        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setProjectId(projectIdNew.toString());
        prjProjectApprovalList = projectApprovalService.select(iRequest, prjProjectApproval, 1, 100000);
        PrjProjectApproval approvalOld = null;
        PrjProjectApproval approvalNew = null;
        if (prjProjectApprovalList.size() > 0) {
            approvalOld = prjProjectApprovalList.get(0);
            PrjProjectApproval prjProjectApproval1 = new PrjProjectApproval();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(approvalOld);
            hlsBeanRefUtilService.setFieldValue(prjProjectApproval1, map);
            prjProjectApproval1.setProjectId(projectIdOld.toString());
            approvalNew = projectApprovalService.insertSelective(iRequest, prjProjectApproval1);
        }

        if (approvalNew == null) {
            return;
        }

        //prj_project_meeting_approver 评审及表决结果汇总 ProjectMeetingApprover
        List<ProjectMeetingApprover> projectMeetingApproverlList = new ArrayList<>();
        ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
        projectMeetingApprover.setApprovalId(approvalOld.getApprovalId());
        projectMeetingApproverlList = projectMeetingApproverService.select(iRequest, projectMeetingApprover, 1, 100000);
        for (ProjectMeetingApprover dt : projectMeetingApproverlList) {
            ProjectMeetingApprover projectMeetingApprover1 = new ProjectMeetingApprover();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(projectMeetingApprover1, map);
            projectMeetingApprover1.setApprovalId(approvalNew.getApprovalId());
            projectMeetingApprover1 = projectMeetingApproverService.insertSelective(iRequest, projectMeetingApprover1);
        }

        //prj_approval_condition 投前/后管理要求 ProjectApprovalCondition
        List<ProjectApprovalCondition> projectApprovalConditionList = new ArrayList<>();
        ProjectApprovalCondition projectApprovalCondition = new ProjectApprovalCondition();
        projectApprovalCondition.setApprovalId(approvalOld.getApprovalId());
        projectApprovalConditionList = projectApprovalConditionService.select(iRequest, projectApprovalCondition, 1, 100000);
        for (ProjectApprovalCondition dt : projectApprovalConditionList) {
            ProjectApprovalCondition projectMeetingApprover1 = new ProjectApprovalCondition();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(projectMeetingApprover1, map);
            projectMeetingApprover1.setApprovalId(approvalNew.getApprovalId());
            projectMeetingApprover1 = projectApprovalConditionService.insertSelective(iRequest, projectMeetingApprover1);
        }

        //批复参数
        List<PrjCreditReplyPara> creditReplyParaList = new ArrayList<>();
        PrjCreditReplyPara replyPara = new PrjCreditReplyPara();
        replyPara.setReplyId(projectIdNew);
        creditReplyParaList = replyParaService.select(iRequest, replyPara, 1, 100000);
        for (PrjCreditReplyPara dt : creditReplyParaList) {
            PrjCreditReplyPara item = dt;
            item.setReplyId(projectIdOld);
            item.setReplyParaId(null);
            item = replyParaService.insertSelective(iRequest, item);
        }
        //批复产品
        List<ReplyProduct> replyProductList = new ArrayList<>();
        ReplyProduct replyProduct = new ReplyProduct();
        replyProduct.setReplyId(projectIdNew);
        replyProductList = replyProductService.select(iRequest, replyProduct, 1, 100000);
        for (ReplyProduct dt : replyProductList) {
            ReplyProduct item = new ReplyProduct();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(item, map);
            item.setReplyId(projectIdOld);
            item = replyProductService.insertSelective(iRequest, item);

            //产品参数
            ReplyProductPara productPara = new ReplyProductPara();
            productPara.setReplyProductId(dt.getReplyProductId());
            List<ReplyProductPara> productParaList = replyProductParaMapper.select(productPara);

            for (ReplyProductPara ppara : productParaList) {
                ReplyProductPara ppitem = ppara;
                ppitem.setReplyProductParaId(null);
                ppitem.setReplyProductId(item.getReplyProductId());
                replyProductParaService.insertSelective(iRequest, ppitem);
            }
        }

    }

    @Override
    public void updateAtt(List<HlsCusPrjProjectAttachment> attachmentList, Long projectIdOld, IRequest requestCtx) {
        for (HlsCusPrjProjectAttachment dt : attachmentList) {
            dt.set__status("add");
            dt.setProjectId(projectIdOld);
            hlsCusPrjProjectAttachmentService.insertSelective(requestCtx, dt);
        }
    }

    @Override
    public void deleteAtt(List<HlsCusPrjProjectAttachment> attachmentList, IRequest requestCtx) {
        for (HlsCusPrjProjectAttachment dt : attachmentList) {
            dt.set__status("delete");
        }
        hlsCusPrjProjectAttachmentService.batchDelete(attachmentList);
    }

    @Override
    public boolean isCreateContract(HlsCusPrjProject hlsCusPrjProject) {
        int createContractFlag = hlsCusPrjProjectMapper.queryIsCreateContract(hlsCusPrjProject);
        if (createContractFlag == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean changeCancel(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject resultHlsCusPrjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(requestCt, hlsCusPrjProject);
        resultHlsCusPrjProjectOld.setProjectStatus("APPROVED");
        resultHlsCusPrjProjectOld.setMeetingStatus("APPROVED");
        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCt, resultHlsCusPrjProjectOld);

        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(hlsCusPrjProject.getChangeReqId());
        HlsCusChangeReqInfo changeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(requestCt, hlsCusChangeReqInfo);

        changeReqInfo.setStatus("CANCEL");
        changeReqInfo.set__status("update");
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(requestCt, changeReqInfo);
        return true;
    }


    @Override
    public List<HlsCusPrjProject> conSituationQuery(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Long companyId = requestCt.getCompanyId();
        String[] documentTypeArray = null;
        String[] amountArray = null;
        String[] absArray = null;
        Double zeroAmount = 0D;
        Double oneAmount = 100000000D;
        Double fiveAmount = 500000000D;
        Double tenAmount = 1000000000D;


        if (hlsCusPrjProject.getDocumentType() != null && hlsCusPrjProject.getDocumentType() != "") {
            documentTypeArray = hlsCusPrjProject.getDocumentType().split("、");
        }
        if (hlsCusPrjProject.getAmountBetween() != null && hlsCusPrjProject.getAmountBetween() != "") {
            amountArray = hlsCusPrjProject.getAmountBetween().split("、");
        }
        if (hlsCusPrjProject.getAbs() != null && hlsCusPrjProject.getAbs() != "") {
            absArray = hlsCusPrjProject.getAbs().split("、");
        }

        Map<String, Object> params = new HashMap<String, Object>(8);
        params.put("documentTypeArray", documentTypeArray);
        params.put("companyId", hlsCusPrjProject.getCompanyId());
        params.put("bpName", hlsCusPrjProject.getBpName());
        params.put("managerName", hlsCusPrjProject.getProjectManager());
        params.put("yearFrom", hlsCusPrjProject.getYearFrom());
        params.put("yearTo", hlsCusPrjProject.getYearTo());
        params.put("absArray", absArray);
        if (hlsCusPrjProject.getDateFrom() != "") {
            params.put("dateFrom", hlsCusPrjProject.getDateFrom());
        }
        if (hlsCusPrjProject.getDateTo() != "") {
            params.put("dateTo", hlsCusPrjProject.getDateTo());
        }
        if (hlsCusPrjProject.getDateValidFrom() != "") {
            params.put("dateValidFrom", hlsCusPrjProject.getDateValidFrom());
        }
        if (hlsCusPrjProject.getDateValidTo() != "") {
            params.put("dateValidTo", hlsCusPrjProject.getDateValidTo());
        }
        if (amountArray != null) {
            for (int i = 0; i < amountArray.length; i++) {
                if ("ONE".equalsIgnoreCase(amountArray[i])) {
                    params.put("amountFrom", zeroAmount);
                    break;
                }
                if ("ONE_TO_FIVE".equalsIgnoreCase(amountArray[i])) {
                    params.put("amountFrom", oneAmount);
                    break;
                }
                if ("FIVE_TO_TEN".equalsIgnoreCase(amountArray[i])) {
                    params.put("amountFrom", fiveAmount);
                    break;
                }
            }
            for (int i = amountArray.length - 1; i >= 0; i--) {
                if ("FIVE_TO_TEN".equalsIgnoreCase(amountArray[i])) {
                    params.put("amountTo", tenAmount);
                    break;
                }
                if ("ONE_TO_FIVE".equalsIgnoreCase(amountArray[i])) {
                    params.put("amountTo", fiveAmount);
                    break;
                }
                if ("ONE".equalsIgnoreCase(amountArray[i])) {
                    params.put("amountTo", oneAmount);
                    break;
                }
            }
        }
        params.put("trxAmountFrom", hlsCusPrjProject.getTrxAmountFrom());
        params.put("trxAmountTo", hlsCusPrjProject.getTrxAmountTo());
        List<HlsCusPrjProject> hlsCusPrjProjects = hlsCusPrjProjectMapper.conSituationQuery(params);
        Double amountTotal = 0D;
        Double depositTotal = 0D;
        Double serviceTotal = 0D;
        Double irrAmount = 0D;
        Double parAmount = 0D;
        Double serviceAmount = 0D;
        Double depositAmount = 0D;
        for (int i = 0; i < hlsCusPrjProjects.size(); i++) {
            amountTotal = amountTotal + hlsCusPrjProjects.get(i).getAmountPaid();
            depositTotal = depositTotal + hlsCusPrjProjects.get(i).getDeposit();
            serviceTotal = serviceTotal + hlsCusPrjProjects.get(i).getLeaseCharge();
            irrAmount = irrAmount + hlsCusPrjProjects.get(i).getAmountPaid() * hlsCusPrjProjects.get(i).getIrr();
            parAmount = parAmount + hlsCusPrjProjects.get(i).getAmountPaid() * hlsCusPrjProjects.get(i).getParInterestRate();
            serviceAmount = serviceAmount + hlsCusPrjProjects.get(i).getAmountPaid() * hlsCusPrjProjects.get(i).getLeaseChargeRatio();
            depositAmount = depositAmount + hlsCusPrjProjects.get(i).getAmountPaid() * hlsCusPrjProjects.get(i).getDepositRatio();
        }
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setAmountPaid(amountTotal);
        prjProject.setDeposit(depositTotal);
        prjProject.setLeaseCharge(serviceTotal);
        prjProject.setContractId(-1L);
        prjProject.setIrr(irrAmount / amountTotal);
        prjProject.setParInterestRate(parAmount / amountTotal);
        prjProject.setLeaseChargeRatio(serviceAmount / amountTotal);
        prjProject.setDepositRatio(depositAmount / amountTotal);
        if (CollectionUtils.isNotEmpty(hlsCusPrjProjects)) {
            hlsCusPrjProjects.add(prjProject);
        }
        return hlsCusPrjProjects;
    }

    /**
     * 合同签约右侧栏状态控制
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String contractSignStatus(IRequest iRequest, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (HlsCusConstant.CONTRACT_STATUS.NEW.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            if (HlsCusConstant.WORKFLOW_STATUS.NEW.equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus())
                    || HlsCusConstant.WORKFLOW_STATUS.APPROVING.equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus())) {
                return "undo";
            } else if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus())) {
                return "todo";
            } else {
                return "undo";
            }
        } else if (HlsCusConstant.CONTRACT_STATUS.CANCEL.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())
                || HlsCusConstant.CONTRACT_STATUS.PENDING.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())
                || HlsCusConstant.CONTRACT_STATUS.INCEPT.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())
                || HlsCusConstant.CONTRACT_STATUS.TERMINATION.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else if (HlsCusConstant.CONTRACT_STATUS.SIGNING.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        } else {
            return "read";
        }
    }

    /**
     * 租金支付表右侧按钮状态控制
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String rentpaymentStatus(IRequest iRequest, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (hlsCusPrjProject != null) {
            if (HlsCusConstant.CONTRACT_STATUS.APPROVED.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
                return "todo";
            } else {
                return "undo";
            }
        } else {
            return "undo";
        }


      /*  if (HlsCusConstant.WORKFLOW_STATUS.NEW.equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus())
                || HlsCusConstant.CONTRACT_STATUS.SIGNING.equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus())
                || "CANCEL".equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus())
                || "PENDING".equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus())) {
            return "undo";
        } else if (HlsCusConstant.CONTRACT_STATUS.APPROVED.equalsIgnoreCase(hlsCusPrjProject.getCreateContractStatus()) ) {
            return "todo";
        } else {
            return "read";
        }*/
    }

    /**
     * 付款前提条件豁免 按钮控制
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String conditionStatus(IRequest iRequest, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (hlsCusPrjProject != null) {
            if (HlsCusConstant.CONTRACT_STATUS.APPROVED.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
                return "todo";
            } else {
                return "undo";
            }
        } else {
            return "undo";
        }


    }

    /**
     * 付款申请右侧栏状态控制
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String paymentStatus(IRequest iRequest, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (hlsCusPrjProject != null) {
            if (HlsCusConstant.CONTRACT_STATUS.APPROVED.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
                //判断是否租金支付表拆分过
                HlsCusConContract hlsCusConContract = hlsCusConContractService.selectByProjectId(projectId);
                if (hlsCusConContract != null) {
                    return "todo";
                } else {
                    return "undo";
                }
            } else {
                return "undo";
            }
        } else {
            return "undo";
        }


    }

    /**
     * 头寸报备右侧栏状态控制
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String reportFinanceStatus(IRequest iRequest, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (hlsCusPrjProject != null) {
            if (HlsCusConstant.CONTRACT_STATUS.APPROVED.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
                //判断是否租金支付表拆分过
                HlsCusConContract hlsCusConContract = hlsCusConContractService.selectByProjectId(projectId);
                if (hlsCusConContract != null) {
                    return "todo";
                } else {
                    return "undo";
                }
            } else {
                return "undo";
            }
        } else {
            return "undo";
        }

    }

    /**
     * 合同关闭右侧栏状态控制
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String cancelStatus(IRequest iRequest, Long projectId) {
        HlsCusFundingPlan hlsCusFundingPlan = new HlsCusFundingPlan();
        hlsCusFundingPlan.setProjectId(projectId);
        List<HlsCusFundingPlan> hlsCusFundingPlanList = hlsCusFundingPlanMapper.queryForFundingPlan(hlsCusFundingPlan);
        if (hlsCusFundingPlanList == null || hlsCusFundingPlanList.size() == 0) {
            return "todo";
        } else {
            return "undo";
        }
    }

    /**
     * 合同起租日调整右侧栏状态控制
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String leaseStatus(IRequest iRequest, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (hlsCusPrjProject != null) {
            if (HlsCusConstant.CONTRACT_STATUS.APPROVED.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
                //判断是否租金支付表拆分过
                HlsCusConContract hlsCusConContract = hlsCusConContractService.selectByProjectId(projectId);
                if (hlsCusConContract != null) {
                    return "todo";
                } else {
                    return "undo";
                }
            } else {
                return "undo";
            }
        } else {
            return "undo";
        }


    }

    /**
     * 合同起贷的控制状态
     *
     * @param iRequest
     * @param projectId
     * @return
     */
    @Override
    public String paymentReqConfirmStatus(IRequest iRequest, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
//        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        //   hlsCusConContract.setSourceDocCategory("CON_CONTRACT");
        //hlsCusConContract.setDocumentCategory("CSH_PAYMENT_REQ");
//        hlsCusConContract.setProjectId(projectId);
//        hlsCusConContract.setDataClass("NORMAL");
        HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();
        cusPrjProject.setProjectId(projectId);
        cusPrjProject.setDataClass("VIRTUAL_CON");
        List<HlsCusPrjProject> hlsCusConContractLists = hlsCusPrjProjectService.select(iRequest, cusPrjProject, 1, 99999999);
        //  String paymentStatus = self().selectPaymentReqStatus(iRequest, hlsCusConContract);
//        List<HlsCusConContract> hlsCusConContractLists = hlsCusConContractService.select(iRequest, hlsCusConContract, 1, 99999999);
//        if (StringUtils.isEmpty(hlsCusConContractLists) || HlsCusConstant.WORKFLOW_STATUS.NEW.equalsIgnoreCase(paymentStatus) || HlsCusConstant.WORKFLOW_STATUS.APPROVED_RETURN.equalsIgnoreCase(paymentStatus)) {
//            return "undo";
//        } else {
//            if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equalsIgnoreCase(paymentStatus)) {
//                return "todo";
//            } else {
//                return "read";
//            }
//
//        }
        if (hlsCusConContractLists.size() == 0) {
            return "undo";
        } else {
            if (StringUtils.isEmpty(hlsCusConContractLists.get(0).getInceptWflStatus()) && ("SIGN".equals(hlsCusConContractLists.get(0).getContractStatus()) || "LOAN".equals(hlsCusConContractLists.get(0).getContractStatus()))) {
                return "todo";
            } else {
                if (HlsCusConstant.WORKFLOW_STATUS.NEW.equalsIgnoreCase(hlsCusConContractLists.get(0).getInceptWflStatus()) || HlsCusConstant.WORKFLOW_STATUS.APPROVED_RETURN.equalsIgnoreCase(hlsCusConContractLists.get(0).getInceptWflStatus())) {
                    return "todo";
                } else if (!HlsCusConstant.WORKFLOW_STATUS.NEW.equalsIgnoreCase(hlsCusConContractLists.get(0).getInceptWflStatus())) {
                    return "read";
                } else {
                    return "undo";
                }
            }
        }
    }

    @Override
    public String interestDerateStatus(IRequest iRequest, Long projectId) {
        /*有罚息就可以做罚息减免*/
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(projectId);
        List<HlsCusConContract> hlsCusConContracts = new ArrayList<>();
        hlsCusConContracts = hlsCusConContractService.select(iRequest, hlsCusConContract, 1, 99999);
        for (HlsCusConContract dt : hlsCusConContracts) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId(dt.getContractId());
            hlsCusConContractCashflow.setCfStatus("RELEASE");
            List<HlsCusConContractCashflow> hlsCusConContractCashflowList = new ArrayList<>();
            hlsCusConContractCashflowList = hlsCusConContractCashflowService.select(iRequest, hlsCusConContractCashflow, 1, 99999);
            for (HlsCusConContractCashflow cf : hlsCusConContractCashflowList) {
                if (cf.getCfItem().equals(9L)) {
                    return "todo";
                }
            }
        }
        return "read";
    }

    @Override
    public String fctContractEtStatus(IRequest iRequest, Long projectId) {
        /*签约状态，且未进行过付款*/
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if ("SIGN".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setProjectId(projectId);
            List<HlsCusConContract> hlsCusConContracts = new ArrayList<>();
            hlsCusConContracts = hlsCusConContractService.select(iRequest, hlsCusConContract, 1, 99999);
            if (hlsCusConContracts == null || hlsCusConContracts.size() == 0) {
                return "todo";
            }
//            for (HlsCusFctContract dt : hlsCusFctContracts) {
//                /*未进行过收款核销*/
//                HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow = new HlsCusFctQuotationCashflow();
//                hlsCusFctQuotationCashflow.setContractId(dt.getContractId());
//                List<HlsCusFctQuotationCashflow> hlsCusFctQuotationCashflowList = new ArrayList<>();
//                hlsCusFctQuotationCashflowList = cashflowService.select(iRequest, hlsCusFctQuotationCashflow, 1, 99999);
//                Double sum = 0D;
//                for (HlsCusFctQuotationCashflow cf : hlsCusFctQuotationCashflowList) {
//                    sum = sum + cf.getWriteOffAmount();
//                    sum = 0D;//modify: 收款核销后也可以撤销，只要未付款
//                }
//                if (sum.equals(0D)) {
//                    /*未进行过付款*/
//                    HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
//                    hlsCusCshPaymentReqLn.setSource_doc_id(dt.getContractId());
//                    List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = new ArrayList<>();
//                    hlsCusCshPaymentReqLnList = cshPaymentReqLnService.select(iRequest, hlsCusCshPaymentReqLn, 1, 99999);
//                    Double amount = 0D;
//                    for (HlsCusCshPaymentReqLn ln : hlsCusCshPaymentReqLnList) {
//                        amount = amount + ln.getAmount_paid();
//                    }
//                    if (amount.equals(0D)) {
//                        return "todo";
//                    }
//                }
//            }
        }
        if ("NEW".equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) && !"APPROVING".equals(hlsCusPrjProject.getCreateContractStatus())) {
            return "todo";
        }
        if ("CANCEL".equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            return "read";
        }
        return "undo";

    }

    /**
     * 保理合同结束右侧菜单栏权限控制
     *
     * @param requestCtx
     * @param projectId
     * @return
     */
    @Override
    public String fctContractEndStatus(IRequest requestCtx, Long projectId) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, hlsCusPrjProject);
        if (hlsCusPrjProject == null || (!HlsCusConstant.CONTRACT_STATUS.INCEPT.equals(hlsCusPrjProject.getContractStatus()) && !HlsCusConstant.CONTRACT_STATUS.TERMINATION.equals(hlsCusPrjProject.getContractStatus()))) {
            return HlsCusConstant.MENU_ITEM.UNDO;
        }
        HlsCusConContractTermination hlsCusConContractTermination = new HlsCusConContractTermination();
        if (hlsCusConContractTermination == null || hlsCusConContractTermination.getStatus() == null || HlsCusConstant.WORKFLOW_STATUS.NEW.equals(hlsCusConContractTermination.getStatus())
                || HlsCusConstant.WORKFLOW_STATUS.APPROVED_RETURN.equals(hlsCusConContractTermination.getStatus())) {
            return HlsCusConstant.MENU_ITEM.TODO;
        } else if (HlsCusConstant.WORKFLOW_STATUS.APPROVING.equals(hlsCusConContractTermination.getStatus())) {
            return HlsCusConstant.MENU_ITEM.PENDING;
        } else {
            //当工作流状态为审批完成则返回只读
            return HlsCusConstant.MENU_ITEM.READ;
        }

    }

    /**
     * 根据projectId查询放款申请状态
     *
     * @param iRequest
     * @param hlsCusConContract
     * @return
     */
    @Override
    public String selectPaymentReqStatus(IRequest iRequest, HlsCusConContract hlsCusConContract) {
        return hlsCusConContractMapper.selectPaymentReqStatus(hlsCusConContract);
    }

    @Autowired
    private ICshPaymentReqHdService paymentReqHdService;

    @Autowired
    private IHlsCusCshPaymentReqLnService hlsCusCshPaymentReqLnService;

    @Override
    public HlsCusPrjProject cancelProjectInfo(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(hlsCusPrjProject.getProjectId());
        prjProject = self().selectByPrimaryKey(requestCtx, prjProject);
        prjProject.setCancelDate(hlsCusPrjProject.getCancelDate());
        prjProject.setCancelPerson(hlsCusPrjProject.getCancelPerson());
        prjProject.setCancelReason(hlsCusPrjProject.getCancelReason());
        prjProject.setContractStatus("CANCEL");
        prjProject.setCancelStatus("APPROVED");
        prjProject = self().updateByPrimaryKey(requestCtx, prjProject);
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(hlsCusPrjProject.getProjectId());
        /*将虚拟合同的现金流冻结*/
        HlsCusPrjQuotation projectQuotation = new HlsCusPrjQuotation();
        projectQuotation.setDocumentId(hlsCusPrjProject.getProjectId());
        projectQuotation.setDataClass("VIRTUAL_CON");
        projectQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        List<HlsCusPrjQuotation> projectQuotationList = hlsCusPrjQuotationService.select(requestCtx, projectQuotation, 1, 99999);
        Long quotationId = 0L;
        for (HlsCusPrjQuotation dt : projectQuotationList) {
            quotationId = dt.getQuotationId();
        }
        HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();
        cashflow.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> cashflowList = new ArrayList<>();
        cashflowList = hlsCusPrjQuotationCashflowService.select(requestCtx, cashflow, 1, 99999);
        for (HlsCusPrjQuotationCashflow cf : cashflowList) {
            cf.setCfStatus("BLOCK");
            hlsCusPrjQuotationCashflowService.updateByPrimaryKey(requestCtx, cf);
        }
        /*将所有合同状态置为CANCEL*/
        List<HlsCusConContract> hlsCusFctContractList = hlsCusConContractService.select(requestCtx, hlsCusConContract, 1, 99999);
        for (HlsCusConContract dt : hlsCusFctContractList) {
            dt.setContractStatus("CANCEL");
            hlsCusConContractService.updateByPrimaryKey(requestCtx, dt);
            /*将付款申请状态置为取消*/
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setSourceDocId(dt.getContractId());
            hlsCusCshPaymentReqLn.setSourceDocCategory("CON_CONTRACT");
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = hlsCusCshPaymentReqLnService.select(requestCtx, hlsCusCshPaymentReqLn, 1, 99999);
            for (HlsCusCshPaymentReqLn ln : hlsCusCshPaymentReqLnList) {
                HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
                hlsCusCshPaymentReqHd.setPaymentReqId(ln.getPaymentReqId());
                hlsCusCshPaymentReqHd = paymentReqHdService.selectByPrimaryKey(requestCtx, hlsCusCshPaymentReqHd);
                hlsCusCshPaymentReqHd.setPaymentReqStatus("CANCEL");
                hlsCusCshPaymentReqHd = paymentReqHdService.updateByPrimaryKey(requestCtx, hlsCusCshPaymentReqHd);
            }
            /*冻结所有现金流*/
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId(dt.getContractId());
            List<HlsCusConContractCashflow> hlsCusConContractCashflowList = new ArrayList<>();
            hlsCusConContractCashflowList = hlsCusConContractCashflowService.select(requestCtx, hlsCusConContractCashflow, 1, 99999);
            for (HlsCusConContractCashflow cf : hlsCusConContractCashflowList) {
                cf.setCfStatus("BLOCK");
                hlsCusConContractCashflowService.updateByPrimaryKey(requestCtx, cf);
            }
        }

        return prjProject;
    }

    @Override
    public void prjContractSignSubmitWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);

   /*     HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(hlsCusPrjProject.getProjectId());
        List<HlsCusConContract> hlsCusConContractLists = hlsCusConContractMapper.select(hlsCusConContract);
        if (hlsCusConContractLists.size() == 1) {
            hlsCusConContract = hlsCusConContractLists.get(0);
            hlsCusConContract.setSignDate(hlsCusPrjProject.getSignDate());
            hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
        }*/
        hlsCusPrjProjectList.add(hlsCusPrjProject);
        databaseLockProvider.lock(hlsCusPrjProject);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        //FCT_CONTRACT_SIGN_WORK_FLOW
        params.put("workFlowType", "CON_CONTRACT_SIGN_WFL");
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);
        //修改合同单据
        HlsDurationHd hlsDurationHd = new HlsDurationHd();
        hlsDurationHd.setProjectId(hlsCusPrjProject.getProjectId());
        List<HlsDurationHd> hlsDurationHds = hlsDurationHdMapper.hlsDurationHdEtDetailQueryNew(hlsDurationHd);
        if (hlsDurationHds.size() > 0) {
            hlsCusPrjProject.setConApplicationChangeStatus("APPROVING");
        } else {
            hlsCusPrjProject.setConApplicationStatus("APPROVING");
        }
/*
        hlsCusPrjProject.setContractStatus("SIGNING");
*/
        HlsCusPrjProject getPrj = new HlsCusPrjProject();
        getPrj.setProjectId(hlsCusPrjProject.getProjectId());
        getPrj.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getPrj).getObjectVersionNumber());
        self().updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
    }

    public String getLeasingContractSerialNumber(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) {
        String num = hlsCusPrjProjectMapper.getLeasingContractMaxNumber(hlsCusPrjProject);
        if (StringUtils.isBlank(num)) {
            return "001";
        } else {
            int var = Integer.parseInt(num) + 1;
            num = ("" + (var + 1000)).substring(("" + (var + 1000)).length() - 3);
            return num;
        }
    }

    //在新建合同时查询，返回为此合同的年-流水号
    public String selectLeasingNumber(Long companyId) {
        return hlsCusPrjProjectMapper.selectLeasingNumber(companyId);
    }

   /* @Override
    public Double getRate(IRequest request, String invoiceProfile, String businessType) {
        if ("LEASE".equals(businessType)) {
            String invoiceProfileTemp = "";
            if ("IMMOVABLE".equals(invoiceProfile)) {
                invoiceProfileTemp = "IMMOVABLES_LEASE";
            } else {
                invoiceProfileTemp = "MOVABLES_LEASE";
            }
            HlsInvoiceProfileDtl hlsInvoiceProfileDtl = new HlsInvoiceProfileDtl();
            hlsInvoiceProfileDtl.setCfItem(101L);
            hlsInvoiceProfileDtl.setInvoiceProfile(invoiceProfileTemp);
            List<HlsInvoiceProfileDtl> hlsInvoiceProfileDtlList = hlsInvoiceProfileDtlService.select(request, hlsInvoiceProfileDtl, 1, 99999);
            if (CollectionUtils.isEmpty(hlsInvoiceProfileDtlList)) {
                throw new IllegalArgumentException("找不到利息对应的税率!");
            }
            // 获取税率
            FndSysCodes fndSysCodes = new FndSysCodes();
            fndSysCodes.setTax_type_code(hlsInvoiceProfileDtlList.get(0).getTaxTypeCode());
            fndSysCodes.setCompany_id(request.getCompanyId());
            List<FndSysCodes> fndSysCodesList = fndSysCodesService.select(request, fndSysCodes, 1, 99999);
            if (CollectionUtils.isEmpty(fndSysCodesList)) {
                throw new IllegalArgumentException("找不到利息对应的税率!");
            }
            fndSysCodes = fndSysCodesList.get(0);
            return fndSysCodes.getTax_type_rate();
        } else if ("OPERATING_LEASE".equals(businessType)) {
            return OPERATING_LEASE_RATE;
        } else {
            return LEASE_RATE;
        }
    }*/

    @Override
    public Long selectRefProjectIdByProjectId(IRequest requestContext, Long projectId) {
        if (null != projectId) {
            return hlsCusPrjProjectMapper.selectRefProjectIdByProjectId(projectId);
        }
        return null;
    }

    @Override
    public List<Map> queryProjectRiskReportAttachment(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusPrjProjectMapper.queryProjectRiskReportAttachment(hlsCusPrjProject);
    }


    @Autowired
    private HlsCusHlsCreditLineChanceMapper CreditLineChanceMapper;

    @Override
    public List<HlsCusPrjProject> queryCreditProject(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        // 查询creditChance
        List<HlsCusHlsCreditLineChance> creditChances = CreditLineChanceMapper.selectCreditLineChanceByStatusAndPass(new HlsCusHlsCreditLineChance());

        // 查询project
        List<HlsCusPrjProject> projects = hlsCusPrjProjectMapper.queryProjectAll(new HlsCusPrjProject());

        if (creditChances.size() > projects.size()) {
            try {
                // 创建并填充额外的project对象
                for (int i = projects.size(); i < creditChances.size(); i++) {
                    HlsCusPrjProject project = new HlsCusPrjProject();
                    //保存新创建的对象到数据库
                    copyAndSaveProject(requestCt,creditChances.get(i),project);
                }
                projects = hlsCusPrjProjectMapper.queryProjectAll(new HlsCusPrjProject());
            } catch (Exception e) {
                logger.error("Error occurred while copying properties from creditChance to project", e);
            }
        }

             // 返回最新的projects列表
        return projects;
    }
    private void copyAndSaveProject(IRequest requestCt,HlsCusHlsCreditLineChance creditChance, HlsCusPrjProject project) throws Exception {
        BeanUtils.copyProperties(creditChance, project);
        self().insertSelective(requestCt, project);
    }

    @Override
    public List<CompositeMap> selectProjectTenantRecLoop(CompositeMap map, String whereStr) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        //HlsBpMaster hlsBpMaster = new HlsBpMaster();
        String tenant_sec_id = null;
        if (map.get("parameter") != null) {
            Map parameter = (Map) map.get("parameter");
            if (parameter.get("tenant_sec_id") != null) {
                //hlsBpMaster.setBpId(Long.parseLong(parameter.get("bp_id").toString()));
                tenant_sec_id = parameter.get("tenant_sec_id").toString();
            }
        }
        return hlsCusPrjProjectMapper.selectProjectTenantRecLoop(tenant_sec_id);
    }

    @Override
    public List<CompositeMap> selectProjectTenantSecLoop(CompositeMap map, String whereStr) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        int tenant_sec_count = 0;
        String tenant_sec_id = "";
        String[] tenant_sec_ids = new String[100];
        if (map.get("parameter") != null) {
            Map parameter = (Map) map.get("parameter");
            if (parameter.get("tenant_sec_count") != null && parameter.get("tenant_sec_count") != "") {
                //hlsBpMaster.setBpId(Long.parseLong(parameter.get("bp_id").toString()));
                tenant_sec_count = Integer.parseInt(parameter.get("tenant_sec_count").toString());
            }
            if (parameter.get("tenant_sec_id") != null && parameter.get("tenant_sec_id") != "") {
                tenant_sec_id = parameter.get("tenant_sec_id").toString();
                tenant_sec_ids = tenant_sec_id.split(",");
            }

        }
        List<CompositeMap> bpInfo = new ArrayList<CompositeMap>();
        for (int i = 1; i <= tenant_sec_count; i++) {
            CompositeMap mapTemp = new CompositeMap();
            String NumberCn = NumberToCN.number2CNMontrayUnit(BigDecimal.valueOf(i));
            mapTemp.put("bp_seq", i);
            mapTemp.put("bp_seq_n", NumberCn);
            if (i <= tenant_sec_ids.length) {
                mapTemp.put("tenant_sec_id", tenant_sec_ids[i - 1]);
            } else {
                mapTemp.put("tenant_sec_id", "");
            }

            bpInfo.add(mapTemp);
        }
        return bpInfo;
    }

    @Override
    public List<CompositeMap> selectProjectBp(CompositeMap map, String whereStr) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        int tenant_sec_count = 0;
        String tenant_sec_id = "";
        String[] tenant_sec_ids = new String[100];
        if (map.get("parameter") != null) {
            Map parameter = (Map) map.get("parameter");
            if (parameter.get("tenant_sec_count") != null && parameter.get("tenant_sec_count") != "") {
                //hlsBpMaster.setBpId(Long.parseLong(parameter.get("bp_id").toString()));
                tenant_sec_count = Integer.parseInt(parameter.get("tenant_sec_count").toString());
            }
            if (parameter.get("tenant_sec_id") != null && parameter.get("tenant_sec_id") != "") {
                tenant_sec_id = parameter.get("tenant_sec_id").toString();
                tenant_sec_ids = tenant_sec_id.split(",");
            }

        }
        List<CompositeMap> bpInfo = new ArrayList<CompositeMap>();
        for (int i = 1; i <= tenant_sec_count; i++) {
            CompositeMap mapTemp = new CompositeMap();
            String NumberCn = NumberToCN.number2CNMontrayUnit(BigDecimal.valueOf(i));
            mapTemp.put("bp_seq", i);
            mapTemp.put("bp_seq_n", NumberCn);
            if (i <= tenant_sec_ids.length) {
                mapTemp.put("tenant_sec_id", tenant_sec_ids[i - 1]);
            } else {
                mapTemp.put("tenant_sec_id", "");
            }

            bpInfo.add(mapTemp);
        }
        return bpInfo;
    }

    private static final String BP_ID = "bpId";
    private static final String SOURCE_TYPE = "sourceType";
    private static final String PROJECT_ID = "projectId";
    private static final String CASHFLOW_ID = "cashflowId";
    private static final String TEMPLET_ID = "templetId";
    private static final String PROJECT_ATTACHMENT_ID = "projectAttachmentId";
    private static final String CHECK_ID = "checkId";
    private static final String INSTANCE_ID = "instanceId";
    //合同文本生成 start
    @Autowired
    private IDocFileTempletRuleService docFileTempletRuleService;
    @Autowired
    private IRuleEngineTypeService ruleEngineTypeService;

    @Autowired
    private Datasource2Json datasource2Json;
    @Autowired
    private IHLSRuleEngineInitService hlsRuleEngineInitService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    @Autowired
    private HlsCusPrjProjectDocxService reportDocxService;

    //项目方案合同文本生成入口
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<FndAttachment> reportCreateDocx(IRequest iRequest, HlsCusPrjProject prjProject, Long cashflowId) throws Exception {
        return self().reportCreateDocx(iRequest, prjProject.getProjectId(), prjProject.getTemplateCode(), cashflowId);
    }

    //合同文本生成过程
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachment> reportCreateDocx(IRequest iRequest, Long projectId, String templateType, Long cashflowId) throws Exception {
        if (projectId == null) {
            throw new ResMessageException("未找到项目方案，请保存后在生成!");
        }

        List<FndAttachment> list = new ArrayList<>();
        String reportDocx = REPORT_DOCX4;
        String docxDescription = null;
        String notTemplate = null;
        /*String[] templetIds = new String[1];
        if ("PRJ_REPORT_CONTENT".equalsIgnoreCase(templateType)) {
            reportDocx = REPORT_DOCX1;
            docxDescription = "项目尽调报告";
            notTemplate = "项目尽调报告模板";
        } else if ("PRJ_REPORT_RISK".equalsIgnoreCase(templateType)) {
            reportDocx = REPORT_DOCX2;
            docxDescription = "项目风险意见审查书";
            notTemplate = "项目风险意见审查书模板";
        } else if ("COLLECTION_LETTERL".equalsIgnoreCase(templateType)) {
            templetIds[0] = "21";
            reportDocx = REPORT_DOCX4;
            docxDescription = "逾期支付催收函";
            notTemplate = "逾期支付催收函模板";
        } else if ("COLLECTION_LETTERG".equalsIgnoreCase(templateType)) {
            reportDocx = REPORT_DOCX4;
            docxDescription = "担保人告知函";
            notTemplate = "担保人告知函模板";
        } else if ("COLLECTION_BOOK".equalsIgnoreCase(templateType)) {
            reportDocx = REPORT_DOCX4;
            docxDescription = " 租金催收通知书（无飞机业务）";
            notTemplate = "项目风险意见审查书模板";
        } else if ("COLLECTION_BOOKS".equalsIgnoreCase(templateType)) {
            templetIds[0] = "41";
            reportDocx = REPORT_DOCX4;
            docxDescription = "付款通知书";
            notTemplate = "付款通知书模板";
        } else if ("PRJ_REPORT_APPROVAL_CHANGE".equalsIgnoreCase(templateType)) {
            reportDocx = REPORT_DOCX3_CHANGE;
            docxDescription = "项目批复书（变更）";
            notTemplate = "项目批复书模板（变更）";
        } else if ("RENT_CHECK".equalsIgnoreCase(templateType)) {
            templetIds[0] = "64";
            reportDocx = "PRJ_RENT_CHECK";
            docxDescription = "投后检查报告";
            notTemplate = "投后检查报告";
        } else {
            reportDocx = REPORT_DOCX3;
            docxDescription = "项目批复书";
            notTemplate = "项目批复书模板";
        }

        if (templetIds.length == 0) {
            throw new ResMessageException("未找到项目尽调模板，请核查后再生成!");
        }*/



        Long companyId = iRequest.getCompanyId();
        if (companyId == null || companyId == -1L) {
            companyId = 3L;
        }

        //2023-02-22 动态获取合同文本模板信息。从HLS_DOC_FILE_TEMPLET_RULE开始，一路经过HLS_RULE_ENGINE,HLS_RULE_ENGINE_ROUTE,HLS_RULE_ENGINE_CONDITION
        //                到达HLS_RULE_ENGINE_RESULT表，获取到ROUTE_RESULT_VALUE,ROUTE_RESULT_DESCRIPTION的值，
        //                然后拿ROUTE_RESULT_VALUE的值去查hls_doc_file_templet表,最终通过templet_id去fnd_atm_attachment_multi，fnd_atm_attachment找到模板文件
        logger.info("获取合同文本模板");
        DocFileTempletRule docFileTempletRule = new DocFileTempletRule();
        //update2023-02-27 业务要求不需要区分companyId
        //docFileTempletRule.setCompanyId(companyId);
        docFileTempletRule.setTempletType(templateType);
        List<DocFileTempletRule> rules = docFileTempletRuleService.selectSelective(iRequest, docFileTempletRule);
        if (!rules.isEmpty()) {
            docFileTempletRule = rules.get(0);
        } else {
            throw new HlsCusException("未找到合同文本模板,请核查后再生成!");
        }
        logger.info("调用规则引擎，获取模板集合");

        String json = null;
        try {
            Map<String, Object> pMap = new HashMap<String, Object>();
            pMap.put("projectId", projectId);
            json = datasource2Json.executeSQL4Json(docFileTempletRule.getDataSourceId(), pMap);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResMessageException("查找规则树条件时遇到错误!",e.getMessage());
        }

        JSONObject jsonObject0 = JSON.parseObject(json);
        JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(jsonObject0.get(docFileTempletRule.getDataSourceId().toString())));
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(jsonObject1.get("default")));
        jsonObject.put("ruleEngineId", docFileTempletRule.getRuleEngineId());
        String[] templetIds = hlsRuleEngineInitService.ruleEngineInit(iRequest, jsonObject);
        logger.info("模板集合id：{}", Arrays.toString(templetIds));

        if (templetIds.length == 0) {
            throw new ResMessageException("未匹配到模板，请核查后再生成!");
        }

        HlsDocFileTemplet hlsDocFileTemplet = null;

        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();

        hlsCusPrjProjectAttachment.setProjectId(projectId);
        hlsCusPrjProjectAttachment.setProjectAttachmentCategory(reportDocx);
        List<HlsCusPrjProjectAttachment> atmLists = hlsCusPrjProjectAttachmentService.selectSelective(iRequest, hlsCusPrjProjectAttachment);
        atmLists.forEach(item -> {
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName(reportDocx);
            fndAttachmentMulti.setTablePkValue(item.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourceTypeCode = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue(reportDocx, item.getProjectAttachmentId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue(FND_ATM_ATTACHMENT_MULTI, sourceTypeCode);
            }
            hlsCusPrjProjectAttachmentService.deleteByPrimaryKey(item);
        });

        HlsCusPrjProjectAttachment ppa = null;
        Map<String, Object> params = null;

        for (int i = 0; i < templetIds.length; i++) {
            hlsDocFileTemplet = new HlsDocFileTemplet();
            hlsDocFileTemplet.setTempletId(Long.parseLong(templetIds[i]));
            hlsDocFileTemplet = hlsDocFileTempletService.selectByPrimaryKey(iRequest, hlsDocFileTemplet);
            if (hlsDocFileTemplet == null) {
                throw new HlsCusException("找不到模板信息！");
            }
            //重新插入合同文本记录
            HlsCusPrjProject hmr = new HlsCusPrjProject();
            hmr.setProjectId(projectId);
            hmr = this.selectByPrimaryKey(iRequest, hmr);
            StringBuilder attachmentName = new StringBuilder();
            docxDescription = hlsDocFileTemplet.getTempletName();
            attachmentName.append(hmr.getProjectNumber()).append("-").append(hlsDocFileTemplet.getTempletName());
            ppa = new HlsCusPrjProjectAttachment();
            ppa.setProjectId(projectId);
            ppa.setProjectAttachmentCategory(reportDocx);
            ppa.setDocumentName(attachmentName.toString());
            ppa.setDescription(docxDescription);
            ppa.setSourceId(hlsDocFileTemplet.getTempletId());
            ppa.setUploadPerson(iRequest.getUserId().toString());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ppa.setUploadDate(new Date());
            ppa = hlsCusPrjProjectAttachmentService.insertSelective(iRequest, ppa);

            //取对应参数生成合同文本文件
            params = new HashMap<String, Object>();
            params.put(PROJECT_ID, projectId);

            if (cashflowId != null) {
                params.put(CASHFLOW_ID, cashflowId);
            }
            params.put(TEMPLET_ID, hlsDocFileTemplet.getTempletId());
            params.put(PROJECT_ATTACHMENT_ID, ppa.getProjectAttachmentId());
            params.put(TABLE_NAME, reportDocx);
            params.put(SOURCE_TYPE, reportDocx);
            list.addAll(reportDocxService.process(iRequest, params, null));
        }
        return list;

    }

    //项目方案合同文本生成入口
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<FndAttachment> reportVirCreateDocx(IRequest iRequest, HlsCusPrjProject prjProject) throws Exception {
        return self().reportVirCreateDocx(iRequest, prjProject.getProjectId(), prjProject.getTemplateCode());
    }

    //合同文本生成过程
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachment> reportVirCreateDocx(IRequest iRequest, Long projectId, String templateType) throws Exception {
        List<FndAttachment> list = new ArrayList<>();
        String reportDocx;
        if ("CONTRACT_ATTACHMENT".equalsIgnoreCase(templateType)) {
            reportDocx = "FCT_PROJECT_ATTACHMENT";
        } else if ("PRJ_REPORT_RISK".equalsIgnoreCase(templateType)) {
            reportDocx = REPORT_DOCX2;
        } else {
            reportDocx = REPORT_DOCX3;
        }

        if (projectId == null) {
            throw new ResMessageException("未找到项目，请保存后在生成!");
        }

        Long companyId = iRequest.getCompanyId();
        if (companyId == null || companyId == -1L) {
            companyId = 3L;
        }
        DocFileTempletRule docFileTempletRule = new DocFileTempletRule();
        docFileTempletRule.setCompanyId(companyId);
        docFileTempletRule.setTempletType(templateType);
        List<DocFileTempletRule> rules = docFileTempletRuleService.selectSelective(iRequest, docFileTempletRule);
        if (!rules.isEmpty()) {
            docFileTempletRule = rules.get(0);
        } else {
            throw new ResMessageException("未找到模板，请核查后再生成!");
        }
        //根据规则引擎得到模板集合
        String json = null;
        RuleEngineType ruleEngineType = new RuleEngineType();
        try {
            Map<String, Object> pMap = new HashMap<String, Object>();
            pMap.put("projectId", projectId);

            ruleEngineType.setRuleEngineType(docFileTempletRule.getRuleEngineType());
            List<RuleEngineType> ruleEngineTypes = ruleEngineTypeService.selectSelective(iRequest, ruleEngineType);
            if (ruleEngineTypes.size() > 0) {
                ruleEngineType = ruleEngineTypes.get(0);
            }
            json = datasource2Json.executeSQL4Json(ruleEngineType.getDataSourceId(), pMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject0 = JSON.parseObject(json);
        JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(jsonObject0.get(ruleEngineType.getDataSourceId().toString())));
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(jsonObject1.get("default")));
        jsonObject.put("ruleEngineId", docFileTempletRule.getRuleEngineId());

        //拿到匹配的所有模板Id
        String[] templetIds = hlsRuleEngineInitService.ruleEngineInit(iRequest, jsonObject);
        if (templetIds.length == 0) {
//            throw new ResMessageException("未找到项目尽调模板，请核查后再生成!");
            throw new ResMessageException("未找到模板，请核查后再生成!");
        }
        HlsDocFileTemplet hlsDocFileTemplet = null;

        //每次生成合同文本区删除原来的合同文本记录(prj_project_attachment)
        HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();

        hlsCusFctProjectAttachment.setProjectId(projectId);
        hlsCusFctProjectAttachment.setSourceType(reportDocx);
        hlsCusFctProjectAttachment.setContentFlag("N");
        List<HlsCusFctProjectAttachment> atmLists = hlsCusFctProjectAttachmentService.selectSelective(iRequest, hlsCusFctProjectAttachment);

        atmLists.forEach(item -> {
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName(reportDocx);
            fndAttachmentMulti.setTablePkValue(item.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourceTypeCode = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue(reportDocx, item.getProjectAttachmentId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue(FND_ATM_ATTACHMENT_MULTI, sourceTypeCode);
            }
            hlsCusFctProjectAttachmentService.deleteByPrimaryKey(item);
        });

        Map<String, String> params = null;


        for (int i = 0; i < templetIds.length; i++) {

            hlsDocFileTemplet = new HlsDocFileTemplet();
            hlsDocFileTemplet.setTempletId(Long.parseLong(templetIds[i]));
            hlsDocFileTemplet = hlsDocFileTempletService.selectByPrimaryKey(iRequest, hlsDocFileTemplet);
            if (hlsDocFileTemplet == null) {
                throw new HlsCusException(NOT_FOUND_CONTRACT_TEMPLATE);
            }

            HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
            hlsCusPrjProjectBp.setProjectId(projectId);
            hlsCusPrjProjectBp.setBpType(hlsDocFileTemplet.getUsageCategory());
            hlsCusPrjProjectBp.setBpClass(hlsDocFileTemplet.getUsageClass());
            List<HlsCusPrjProjectBp> reportBps
                    = hlsCusPrjProjectBpService.selectBpByProjectIdOrderByBpCategory(hlsCusPrjProjectBp);
            for (HlsCusPrjProjectBp reportBp : reportBps) {
                HlsCusFctProjectAttachment ppa = new HlsCusFctProjectAttachment();
                hlsCusFctProjectAttachment.setTemplateId(hlsDocFileTemplet.getTempletId());
                hlsCusFctProjectAttachment.setProjectId(projectId);
                hlsCusFctProjectAttachment.setBpId(reportBp.getBpId());
                List<HlsCusFctProjectAttachment> templateIds_exists
                        = hlsCusFctProjectAttachmentService.selecttAttachmentByTemplateId(hlsCusFctProjectAttachment);
                if (templateIds_exists.size() == 0) {
                    if (hlsDocFileTemplet.getUsageCategory().equalsIgnoreCase(reportBp.getBpType()) && hlsDocFileTemplet.getUsageClass().equalsIgnoreCase(reportBp.getBpClass())) {

                        if (reportBp.getBpType().equalsIgnoreCase("TENANT")) {
                            ppa.setMajorConFlag("Y");
                        }
                        ppa.setSourceType(reportDocx);
                        ppa.setDocumentName(hlsDocFileTemplet.getTempletName());
                        ppa.setBpCategory(reportBp.getBpCategroy());

                        ppa.setProjectId(projectId);
                        ppa.setBpId(reportBp.getBpId());
                        ppa.setSourceId(hlsDocFileTemplet.getTempletId());
                        ppa.setBpClass(hlsDocFileTemplet.getUsageClass());
                        ppa.setContentFlag("N");
                        ppa.setBpRoleType(reportBp.getBpType());
                        ppa.setStatus("NEW");
                        ppa.setTemplateId(hlsDocFileTemplet.getTempletId());
                        ppa = hlsCusFctProjectAttachmentService.insertSelective(iRequest, ppa);

                        ppa = null;
                    }
                }


            }

        }
        return list;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<FndAttachment> reportVirCreateDocxContent(IRequest iRequest, HlsCusPrjProject prjProject) throws Exception {
        List<HlsCusPrjProject> counts
                = hlsCusPrjProjectService.queryVirtualContract(prjProject);
        HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();

        HlsCusPrjProject dto = new HlsCusPrjProject();
        dto.setProjectId(prjProject.getProjectId());
        dto = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, dto);
        hlsCusFctProjectAttachment.setProjectId(prjProject.getProjectId());
        hlsCusFctProjectAttachment.setContentFlag("N");
        List<FndAttachment> list = new ArrayList<>();

        List<HlsCusFctProjectAttachment> hlscusfctprojectattachments
                = hlsCusFctProjectAttachmentService.selectAttachmentByProjectId(hlsCusFctProjectAttachment);
        int number = 1;
        for (HlsCusFctProjectAttachment hlscusfctprojectattachment1 : hlscusfctprojectattachments) {
            if (hlscusfctprojectattachment1.getDocumentNumber() == null) {
                StringBuilder stringBuilder = new StringBuilder();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
                Map<String, String> params1 = new HashMap<String, String>();
                String number1 = String.format("%03d", counts.get(0).getContractCount());
                String str1 = String.format("%02d", number);
                String number3 = "" + stringBuilder.append(dto.getContractNumber()).append("-").append(str1);

/*
                String number3= ""+ stringBuilder.append("H-").append(simpleDateFormat.format(new Date())).append("-Y15-").append(number1).append("-").append(str1);
*/
                hlscusfctprojectattachment1.setDocumentNumber(number3);
                hlsCusFctProjectAttachmentService.updateByPrimaryKeySelective(iRequest, hlscusfctprojectattachment1);
                number = number + 1;
            }


        }

        List<HlsCusFctProjectAttachment> atmLists = hlsCusFctProjectAttachmentService.selectSelective(iRequest, hlsCusFctProjectAttachment);

        for (HlsCusFctProjectAttachment atmList : atmLists) {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(PROJECT_ID, prjProject.getProjectId());
            params.put(TEMPLET_ID, atmList.getTemplateId());
            params.put(BP_ID, atmList.getBpId());
            params.put(SOURCE_TYPE, atmList.getSourceType());
            params.put(PROJECT_ATTACHMENT_ID, atmList.getProjectAttachmentId());
            params.put(TABLE_NAME, atmList.getSourceType());
            list.addAll(reportDocxService.process(iRequest, params, null));
        }
        return list;
    }

    @Override
    public List<CompositeMap> selectPrjQuotationLoop(CompositeMap map, String whereStr) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        List<CompositeMap> quotationInfo = new ArrayList<CompositeMap>();
        if (map.get("parameter") != null) {
            Map parameter = (Map) map.get("parameter");
            if (parameter.get("project_id") != null) {
                Long projectId = Long.valueOf(parameter.get("project_id").toString());
                HlsCusPrjProject project = new HlsCusPrjProject();
                project.setProjectId(projectId);

                List<HlsCusPrjQuotation> quotationList = hlsCusPrjQuotationMapper.selectQuotationByProjectId(project);
                int i = 1;
                for (HlsCusPrjQuotation quotation : quotationList) {

                    CompositeMap compositeMap = new CompositeMap();
                    compositeMap.put("quotation_id", quotation.getQuotationId());
                    compositeMap.put("seq", i);
                    i++;
                    quotationInfo.add(compositeMap);
                }
            }
        }
        return quotationInfo;
    }

    @Override
    public List<CompositeMap> selectProjectQuotationNoticeLoop(CompositeMap map, String whereStr) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        Long quatationNums = 0L;
        List<HlsCusPrjQuotation> hlsCusPrjQuotations = new ArrayList<>();
        if (map.get("parameter") != null) {
            Map parameter = (Map) map.get("parameter");

            if (parameter.get("project_id") != null) {
                Long projectId = Long.valueOf(parameter.get("project_id").toString());
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(projectId);
                List<HlsCusPrjProject> hlsCusPrjProjects = hlsCusPrjProjectMapper.selectProjectQuotationNoticeLoop(hlsCusPrjProject);
                hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryConQuotationPrjNotice(hlsCusPrjProject);
                quatationNums = hlsCusPrjProjects.get(0).getQuatationNums();
            } else {
                //暂时假设一个值，测试
              /*  parameter.put("project_id", 804);
                Long projectId = Long.valueOf(parameter.get("project_id").toString());
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(projectId);
                List<HlsCusPrjProject> hlsCusPrjProjects = hlsCusPrjProjectMapper.selectProjectQuotationNoticeLoop(hlsCusPrjProject);
                hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryConQuotationPrjNotice(hlsCusPrjProject);
                quatationNums = hlsCusPrjProjects.get(0).getQuatationNums();*/

            }

        }
        List<CompositeMap> quotationInfo = new ArrayList<CompositeMap>();
        for (int i = 1; i <= quatationNums; i++) {
            CompositeMap mapTemp = new CompositeMap();
//            String NumberCn = NumberToCN.number2CNMontrayUnit(BigDecimal.valueOf(i));
            mapTemp.put("order_seq", i);
//            mapTemp.put("order_seq_n",NumberCn);
            mapTemp.put("quotation_id", hlsCusPrjQuotations.get(i - 1).getQuotationId());
            mapTemp.put("project_id", hlsCusPrjQuotations.get(i - 1).getProjectId());
            quotationInfo.add(mapTemp);
        }
        return quotationInfo;

    }


    @Override
    public List<CompositeMap> selectProjectGuarantorLoop(CompositeMap map, String whereStr) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        int warrantorCount = 0;
        String guarantorId = "";
        String[] guarantorIds = new String[100];
        if (map.get("parameter") != null) {
            Map parameter = (Map) map.get("parameter");
            if (parameter.get("warrantor_count") != null) {
                warrantorCount = Integer.parseInt(parameter.get("warrantor_count").toString());
            }
            if (parameter.get("guarantor_id") != null) {
                guarantorId = parameter.get("guarantor_id").toString();
                guarantorIds = guarantorId.split(",");
            }

        }
        List<CompositeMap> bpInfo = new ArrayList<CompositeMap>();
        for (int i = 1; i <= warrantorCount; i++) {
            CompositeMap mapTemp = new CompositeMap();
            String NumberCn = NumberToCN.number2CNMontrayUnit(BigDecimal.valueOf(i));
            mapTemp.put("bp_seq", i);
            mapTemp.put("bp_seq_n", NumberCn);
            if (i <= guarantorIds.length) {
                mapTemp.put("guarantor_id", guarantorIds[i - 1]);
            } else {
                mapTemp.put("guarantor_id", "");
            }

            bpInfo.add(mapTemp);
        }
        return bpInfo;
        /*IRequest iRequest = RequestHelper.getCurrentRequest(true);
        int bp_count = 0;
        if(map.get("parameter") != null){
            Map parameter  = (Map) map.get("parameter");
            if(parameter.get("bp_count") != null){
                //hlsBpMaster.setBpId(Long.parseLong(parameter.get("bp_id").toString()));
               bp_count =  Integer.parseInt(parameter.get("bp_count").toString()) ;
            }
        }
        List<CompositeMap> bpInfo = new ArrayList<CompositeMap>();
        for(int i =1;i<=bp_count;i++){
            CompositeMap mapTemp = new CompositeMap();
            mapTemp.put("bp_seq",i);
            bpInfo.add(mapTemp);
        }

        return bpInfo;*/
    }

    @Override
    public List<CompositeMap> selectProjectGuarantorNpLoop(CompositeMap map, String whereStr) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        int warrantorCountNp = 0;
        String guarantorId = "";
        String[] guarantorIds = new String[100];
        int j = 0;
        if (map.get("parameter") != null) {
            Map parameter = (Map) map.get("parameter");
            if (parameter.get("warrantor_count_np") != null) {
                warrantorCountNp = Integer.parseInt(parameter.get("warrantor_count_np").toString());
            }
            if (parameter.get("guarantor_np_id") != null) {
                guarantorId = parameter.get("guarantor_np_id").toString();
                guarantorIds = guarantorId.split(",");
                j++;
            }

        }
        List<CompositeMap> bpInfo = new ArrayList<CompositeMap>();
        for (int i = 1; i <= warrantorCountNp; i++) {
            CompositeMap mapTemp = new CompositeMap();
            String NumberCn = NumberToCN.number2CNMontrayUnit(BigDecimal.valueOf(i));
            mapTemp.put("bp_seq", i);
            mapTemp.put("bp_seq_n", NumberCn);
            if (i <= guarantorIds.length) {
                mapTemp.put("guarantor_np_id", guarantorIds[i - 1]);
            } else {
                mapTemp.put("guarantor_np_id", "");
            }

            bpInfo.add(mapTemp);
        }
        return bpInfo;

    }

    public void cloneQuotationAndQuotationCashflow(IRequest request, Long normalContractId, Long ChangeContractId, Date changeDate) {
        //获取变更前的报价
        HlsCusPrjQuotation projectQuotation = new HlsCusPrjQuotation();
        projectQuotation.setSourceDocumentId(normalContractId);
        projectQuotation.setSourceDocumentCategory("CON_CONTRACT");
        List<HlsCusPrjQuotation> projectQuotationList = hlsCusPrjQuotationService.select(request, projectQuotation, 1, 9999999);
//复制新报价
        if (projectQuotationList.size() > 0) {
            HlsCusPrjQuotation hlsCusPrjQuotationNew = new HlsCusPrjQuotation();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(projectQuotationList.get(0));
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationNew, map);
            hlsCusPrjQuotationNew.setSourceDocumentId(ChangeContractId);
            hlsCusPrjQuotationNew.setChangeDate(changeDate);
            hlsCusPrjQuotationNew.set__status(DTOStatus.ADD);
            hlsCusPrjQuotationNew = hlsCusPrjQuotationService.insertSelective(request, hlsCusPrjQuotationNew);
            //复制分段测算数据
            QuotationSubsection quotationSubsection = new QuotationSubsection();
            quotationSubsection.setQuotationId(projectQuotationList.get(0).getQuotationId());
            List<QuotationSubsection> quotationSubsectionList = quotationSubsectionService.select(request, quotationSubsection, 1, 99999999);
            for (QuotationSubsection dto : quotationSubsectionList) {
                QuotationSubsection quotationSubsectionTemp = new QuotationSubsection();
                Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
                hlsBeanRefUtilService.setFieldValue(quotationSubsectionTemp, mapCsh);
                quotationSubsectionTemp.setQuotationId(hlsCusPrjQuotationNew.getQuotationId());
                quotationSubsectionService.insertSelective(request, quotationSubsectionTemp);
            }
        }

    }

    @Override
    public List<HlsCusPrjProject> contractInceptFinance(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {


        List<HlsCusConContract> contractList = new ArrayList<>();

        if (list != null && list.size() > 0) {
            for (HlsCusPrjProject cusPrjProject : list) {
                if (cusPrjProject.getQuotationId() != null) {
                    Date leaseAccountDate = cusPrjProject.getLeaseAccountDate();
                    HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                    hlsCusPrjQuotation.setQuotationId(cusPrjProject.getQuotationId());
                    hlsCusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
                    hlsCusPrjQuotation.setLeaseAccountDate(leaseAccountDate);
                    //财务起租标记
                    hlsCusPrjQuotation.setFinancialLeaseFlag("Y");
                    hlsCusPrjQuotationMapper.updateByPrimaryKeySelective(hlsCusPrjQuotation);

                    //起租凭证
                    Map contractInceptMap = new HashMap<>();
                    HlsCusConContract resultHlsCusConContract = new HlsCusConContract();
                    resultHlsCusConContract.setContractId(cusPrjProject.getContractId());
                    resultHlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest, resultHlsCusConContract);

                    contractInceptMap.put("jeTrxId", resultHlsCusConContract.getContractId());
                    contractInceptMap.put("companyId", resultHlsCusConContract.getCompanyId());
                    contractInceptMap.put("contractId", resultHlsCusConContract.getContractId());
                    contractInceptMap.put("sourceDoc", "CON_CONTRACT");
                    AbstractJeTrxService contractInceptJeTrxService = JeTrxCommonService.map.get("CONTRACT_INCEPT");
                    contractInceptJeTrxService.process(iRequest, contractInceptMap);

                    //摊销list
                    contractList.add(resultHlsCusConContract);


                    /*//更改收益分摊日表中的 租前息(财务) 标志
                    List<GldFinanceIncomeDay> gldFinanceIncomeDayList = gldFinanceIncomeDayService.queryPreLeaseInterestByContractId(cusPrjProject);
                    for(GldFinanceIncomeDay gldFinanceIncomeDay : gldFinanceIncomeDayList){
                        GldFinanceIncomeDay financeIncomeDay = new GldFinanceIncomeDay();
                        financeIncomeDay.setFinanceIncomeDayId(gldFinanceIncomeDay.getFinanceIncomeDayId());
                        financeIncomeDay = gldFinanceIncomeDayService.selectByPrimaryKey(iRequest,financeIncomeDay);
                        financeIncomeDay.setPreLeaseInterestFlag("Y");
                        gldFinanceIncomeDayService.updateByPrimaryKeySelective(iRequest,financeIncomeDay);
                    }*/

                } else {

                }
            }
        }

        //摊销
        iContractFinanceIncomeService.financeIncomeSharing(iRequest, contractList);

        return list;
    }

    /**
     * 财务起租核算日标记 - 保存
     *
     * @param iRequest
     * @param list
     * @return
     * @throws Exception
     */
    @Override
    public List<HlsCusPrjProject> contractSaveIncept(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {

        if (list != null && list.size() > 0) {
            for (HlsCusPrjProject cusPrjProject : list) {
                if (cusPrjProject.getQuotationId() != null) {
                    Date leaseAccountDate = cusPrjProject.getLeaseAccountDate();
                    HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                    HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
                    hlsCusPrjQuotation.setQuotationId(cusPrjProject.getQuotationId());
                    cusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
                    cusPrjQuotation.setLeaseAccountDate(leaseAccountDate);
                    hlsCusPrjQuotationMapper.updateByPrimaryKeySelective(cusPrjQuotation);

                    /*//更改收益分摊日表中的 租前息(财务) 标志
                    List<GldFinanceIncomeDay> gldFinanceIncomeDayList = gldFinanceIncomeDayService.queryPreLeaseInterestByContractId(cusPrjProject);
                    for(GldFinanceIncomeDay gldFinanceIncomeDay : gldFinanceIncomeDayList){
                        GldFinanceIncomeDay financeIncomeDay = new GldFinanceIncomeDay();
                        financeIncomeDay.setFinanceIncomeDayId(gldFinanceIncomeDay.getFinanceIncomeDayId());
                        financeIncomeDay = gldFinanceIncomeDayService.selectByPrimaryKey(iRequest,financeIncomeDay);
                        financeIncomeDay.setPreLeaseInterestFlag("Y");
                        gldFinanceIncomeDayService.updateByPrimaryKeySelective(iRequest,financeIncomeDay);
                    }*/
                }
            }
        }
        return list;
    }

    @Autowired
    private HlsCusQuotationDelayHistoryMapper hlsCusQuotationDelayHistoryMapper;

    @Override
    public List<HlsCusPrjProject> contractInceptDelayFinance(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        HlsCusQuotationDelayHistory hlsCusQuotationDelayHistory = new HlsCusQuotationDelayHistory();

        List<HlsCusConContract> contractList = new ArrayList<>();

        if (list != null && list.size() > 0) {
            for (HlsCusPrjProject cusPrjProject : list) {
                if (cusPrjProject.getQuotationId() != null) {
                    Date leaseAccountDate = cusPrjProject.getLeaseAccountDate();
                    Date leaseAccountDateDelay = cusPrjProject.getLeaseAccountDateDelay();
                    HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                    hlsCusPrjQuotation.setQuotationId(cusPrjProject.getQuotationId());
                    hlsCusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
//                    hlsCusPrjQuotation.setLeaseAccountDate(leaseAccountDate);

                    //存上一次的财务起租核算日
                    hlsCusPrjQuotation.setLeaseAccountDateDelay(leaseAccountDate);
                    //存延后调整的财务起租核算日
                    hlsCusPrjQuotation.setLeaseAccountDate(leaseAccountDateDelay);
                    hlsCusPrjQuotation.setFinancialLeaseFlag("N");
                    hlsCusPrjQuotationMapper.updateByPrimaryKeySelective(hlsCusPrjQuotation);

                    hlsCusQuotationDelayHistory.setQuotationId(cusPrjProject.getQuotationId());
                    hlsCusQuotationDelayHistory.setProjectId(cusPrjProject.getProjectId());
                    hlsCusQuotationDelayHistory.setContractId(cusPrjProject.getContractId());
                    hlsCusQuotationDelayHistory.setLeaseAccountDate(leaseAccountDate);
                    hlsCusQuotationDelayHistory.setUserId(iRequest.getUserId());
                    hlsCusQuotationDelayHistory.setOperationDate(new Date());
                    hlsCusQuotationDelayHistoryMapper.insertSelective(hlsCusQuotationDelayHistory);


                    //凭证
                    Map contractInceptMap = new HashMap<>();
                    HlsCusConContract resultHlsCusConContract = new HlsCusConContract();
                    resultHlsCusConContract.setContractId(cusPrjProject.getContractId());
                    resultHlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest, resultHlsCusConContract);

                    contractInceptMap.put("jeTrxId", resultHlsCusConContract.getContractId());
                    contractInceptMap.put("companyId", resultHlsCusConContract.getCompanyId());
                    contractInceptMap.put("contractId", resultHlsCusConContract.getContractId());
                    contractInceptMap.put("sourceDoc", "CON_CONTRACT");
                    contractInceptMap.put("reverseJeDate", leaseAccountDate);
                    contractInceptMap.put("reverseJeTrxId", resultHlsCusConContract.getContractId());
                    AbstractJeTrxService contractInceptJeTrxService = JeTrxCommonService.map.get("CONTRACT_INCEPT");
                    contractInceptJeTrxService.process(iRequest, contractInceptMap);

                    //摊销list
                    contractList.add(resultHlsCusConContract);

                   /* Map paramMap = new HashMap<>();
                    paramMap.put("jeTrxId", resultHlsCusConContract.getContractId());
                    paramMap.put("companyId", resultHlsCusConContract.getCompanyId());
                    paramMap.put("contractId", resultHlsCusConContract.getContractId());
                    paramMap.put("sourceDoc", "CON_CONTRACT");
                    AbstractJeTrxService inceptJeTrxService = JeTrxCommonService.map.get("CONTRACT_INCEPT");
                    inceptJeTrxService.process(iRequest, paramMap);*/
                } else {

                }
            }
        }

        //摊销
        iContractFinanceIncomeService.financeIncomeSharing(iRequest, contractList);


        return list;
    }

    @Autowired
    private HlsCusCshWriteOffMapper hlsCusCshWriteOffMapper;

    @Autowired
    private ContractFinanceIncomeMapper contractFinanceIncomeMapper;

    @Autowired
    private JeTrxDtlMapper jeTrxDtlMapper;

    @Autowired
    private HlsCusJeHeadMapper hlsCusJeHeadMapper;

    private final static String GLD_HEAD_CONFIRM = "CONFIRM";

    /**
     * 财务起租日标记 - 校验
     *
     * @param iRequest
     * @param list
     * @return
     * @throws Exception
     */
    @Override
    public String contractInceptCheck(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        String flag = "true";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        if (list != null && list.size() > 0) {
            for (HlsCusPrjProject cusPrjProject : list) {
                if (cusPrjProject.getQuotationId() != null) {
//                    Date leaseAccountDate = cusPrjProject.getLeaseAccountDate();
                    Long contractId = cusPrjProject.getContractId();

                    //不允许有待生成的凭证存在
                    JeTrxDtl jeTrxDtl = new JeTrxDtl();
                    jeTrxDtl.setJeTrxId(contractId);
                    List<JeTrxDtl> jeTrxDtlList = jeTrxDtlMapper.queryNewByContract(jeTrxDtl);
                    if (jeTrxDtlList.size() > 0) {
                        flag = "gldfalse";
                    } else {
                        //不允许有原凭证未确认
                        HlsCusJeHead hlsCusJeHead = new HlsCusJeHead();
                        hlsCusJeHead.setJeTrxId(contractId);
                        List<HlsCusJeHead> hlsCusJeHeadList = hlsCusJeHeadMapper.queryNotConfirmBy(hlsCusJeHead);
                        if (hlsCusJeHeadList.size() == 1) {
                            for (HlsCusJeHead cusJeHead : hlsCusJeHeadList) {
                                if (!GLD_HEAD_CONFIRM.equalsIgnoreCase(cusJeHead.getJeStatus())) {
                                    flag = "gldconfirmfalse";
                                } else {
                                    flag = "true";
                                }
                            }
                        } else if (hlsCusJeHeadList.size() == 0) {
                            flag = "true";
                        } else {
                            flag = "gldconfirmfalse";
                        }
                    }

                }
            }
        }
        return flag;

    }


    /**
     * 财务起租日延后调整 -  校验
     *
     * @param iRequest
     * @param list
     * @return
     * @throws Exception
     */
    @Override
    public String contractInceptCheckFinance(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        String flag = "true";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        if (list != null && list.size() > 0) {
            for (HlsCusPrjProject cusPrjProject : list) {
                if (cusPrjProject.getQuotationId() != null) {
                    Date leaseAccountDate = cusPrjProject.getLeaseAccountDate();
                    Date leaseAccountDateDelay = cusPrjProject.getLeaseAccountDateDelay();
                    Long contractId = cusPrjProject.getContractId();
                    //核销表不能存在 从原起租日到调整后起租日期间核销的租金/租前息
                    HlsCusCshWriteOff cusCshWriteOff = new HlsCusCshWriteOff();
                    cusCshWriteOff.setContractId(contractId);
                    cusCshWriteOff.setReversedFlag("N");
                    cusCshWriteOff.setWriteOffDateFrom(leaseAccountDate);
                    cusCshWriteOff.setWriteOffDateTo(leaseAccountDateDelay);

                    List<HlsCusCshWriteOff> cshWriteOffList = hlsCusCshWriteOffMapper.queryWriteOffByFinance(cusCshWriteOff);
                    if (cshWriteOffList.size() == 0) {

                        //该月的月结 不能结束
                        String period = simpleDateFormat.format(leaseAccountDate);
                        HlsCusContractFinanceIncome hlsCusContractFinanceIncome = new HlsCusContractFinanceIncome();
                        hlsCusContractFinanceIncome.setContractId(contractId);
                        hlsCusContractFinanceIncome.setPostFlag("Y");
                        hlsCusContractFinanceIncome.setPeriodName(period);
                        List<HlsCusContractFinanceIncome> cusContractFinanceIncomeList = contractFinanceIncomeMapper.queryByRentDelay(hlsCusContractFinanceIncome);

                        if (cusContractFinanceIncomeList.size() == 0) {
                            //不允许有待生成的凭证存在
                            JeTrxDtl jeTrxDtl = new JeTrxDtl();
                            jeTrxDtl.setJeTrxId(contractId);
                            List<JeTrxDtl> jeTrxDtlList = jeTrxDtlMapper.queryNewByContract(jeTrxDtl);
                            if (jeTrxDtlList.size() > 0) {
                                flag = "gldfalse";
                            } else {
                                //不允许有原凭证未确认
                                HlsCusJeHead hlsCusJeHead = new HlsCusJeHead();
                                hlsCusJeHead.setJeTrxId(contractId);
                                List<HlsCusJeHead> hlsCusJeHeadList = hlsCusJeHeadMapper.queryNotConfirmBy(hlsCusJeHead);
                                if (hlsCusJeHeadList.size() == 1) {
                                    for (HlsCusJeHead cusJeHead : hlsCusJeHeadList) {
                                        if (!GLD_HEAD_CONFIRM.equalsIgnoreCase(cusJeHead.getJeStatus())) {
                                            flag = "gldconfirmfalse";
                                        } else {
                                            flag = "true";
                                        }
                                    }
                                } else {
                                    flag = "gldconfirmfalse";
                                }
                            }


                        } else {
                            flag = "incomefalse";
                        }

                    } else {
                        flag = "writefalse";
                    }


                }
            }
        }
        return flag;

    }

    @Override
    public List<HlsCusPrjProject> updateApprovalStatus(IRequest requestCtx, List<HlsCusPrjProject> hlsCusPrjProject) throws ResMessageException {
        for (HlsCusPrjProject prjProject : hlsCusPrjProject) {
            Long processInstanceId = prjProject.getProcessInstanceId();
            if (processInstanceId != null) {
                String approvalStatus = prjProject.getApprovalStatus();
                if ("VOTING".equals(approvalStatus)) {
                    //改变项目状态
                    prjProject.setApprovalStatus("VOTED");
                    //拼接业审委意见
                    requestCtx.setAttribute("wflRuleControlFlag", "Y");
                    List<Map> commentList = projectMeetingApproverService.selectPrjSumComment(requestCtx, prjProject);
                    Iterator var5 = commentList.iterator();

                    StringBuilder sumComment = new StringBuilder();
//                    if(PROJECT_CREDIT.equals(prjProject.getMeetingType())){
//                        sumComment.append("经业审委员投票，汇总意见为");
//                    }else if(PROJECT_CREDIT_CHANGE.equals(prjProject.getMeetingType())){
                    sumComment.append("业审委成员");
                    sumComment.append(commentList.size() + 1);
                    sumComment.append("人参加，以上会的形式进行审议，");
//                    }

                    while (var5.hasNext()) {
                        Map map = (Map) var5.next();
                        String resultCount = map.get("resultCount").toString();
                        String voteResult = map.get("voteResultN").toString();
                        if (voteResult != null) {
                            sumComment.append(resultCount).append("票").append(voteResult);
                            if (var5.hasNext()) {
                                sumComment.append("，");
                            } else {
                                sumComment.append("。");
                            }
                        }
                    }
                    prjProject.setVoteComment(sumComment.toString());
                    prjProject.setFiveCategories("NORMAL");

                    hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
                    ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
                    projectMeetingApprover.setApprovalId(prjProject.getApprovalId());
                    projectMeetingApprover.setProjectId(prjProject.getProjectId());
                    List<ProjectMeetingApprover> projectMeetingApproverList = projectMeetingApproverService.queryAllByProjectId(requestCtx, projectMeetingApprover);
                    if (projectMeetingApproverList.size() > 0) {
                        projectMeetingApproverList.forEach(approver -> {
                            approver.setVoteStatus("VOTE_FINISH");
                            projectMeetingApproverService.updateByPrimaryKey(requestCtx, approver);
                        });
                    }
                }
            }
        }
        return hlsCusPrjProject;
    }

    @Autowired
    private HlsCusPrjProjectMeetingMapper hlsCusPrjProjectMeetingMapper;

    private final static String REVIEW_APPROVE = "APPROVE";
    private final static String REVIEW_CONDITION_APPROVE = "CONDITION_APPROVE";
    private final static String REVIEW_VETO = "VETO";
    private final static String REVIEW_HOLD = "HOLD";
    private final static String REVIEW_VETO_CONTINUE = "CONTINUE";
    private final static String REVIEW_UNDER = "_";
    private final static String REVIEW_CHANGE = "CHANGE";
    private final static String REVIEW_CHANGE_APPROVE = "CHANGE_APPROVE";
    private final static String REVIEW_CHANGE_VETO = "CHANGE_VETO";
    private final static String REVIEW_CHANGE_HOLD = "CHANGE_HOLD";

    Map<String, String> params = new HashMap<String, String>();

    @Override
    public List<HlsCusPrjProject> createApproveNotice(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        HlsCusProjectCreditNotice prjNotice = new HlsCusProjectCreditNotice();

        if (list != null && list.size() > 0) {
            for (HlsCusPrjProject cusPrjProject : list) {
                if (cusPrjProject.getProjectId() != null) {
                    String codeNumber = "";
                    if (cusPrjProject.getUnitId() != null) {
                        codeNumber = hlsCusPrjProjectMapper.queryNoticeNumber(cusPrjProject.getUnitId());
                        //params.put("PARAMETER_01", codeNumber);
                    }
                    HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(cusPrjProject);

                    if (cusPrjProject.getFiveCategories() != null) {
                        prjProject.setFiveCategories(cusPrjProject.getFiveCategories());
                        hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
                    }

                    HlsCusProjectCreditNotice hlsCusProjectCreditNotice = new HlsCusProjectCreditNotice();
                    hlsCusProjectCreditNotice.setProjectId(cusPrjProject.getProjectId());


                    if (cusPrjProject.getPrjNoticeId() == null) {
                        if (REVIEW_APPROVE.equalsIgnoreCase(cusPrjProject.getApproveResult())) {

                            String businessCode = REVIEW_APPROVE + REVIEW_UNDER + codeNumber;

                            String number = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_MEETING_NOTICE",
                                    businessCode, "APPROVE", params);

                            hlsCusProjectCreditNotice.setProjectNoticeNumber(number);
                            hlsCusProjectCreditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            hlsCusProjectCreditNotice.setCreditPeriod(cusPrjProject.getCreditPeriod());
                            hlsCusProjectCreditNotice.setCreditPeriodFrom(cusPrjProject.getCreditPeriodFrom());
                            hlsCusProjectCreditNotice.setCreditPeriodTo(cusPrjProject.getCreditPeriodTo());
                            hlsCusProjectCreditNotice.setCreditType("APPROVE");
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                hlsCusProjectCreditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }


                        } else if (REVIEW_VETO.equalsIgnoreCase(cusPrjProject.getApproveResult())) {

                            String businessCode = REVIEW_VETO + REVIEW_UNDER + codeNumber;

                            String number = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_MEETING_NOTICE",
                                    businessCode, "VETO", params);

                            hlsCusProjectCreditNotice.setProjectNoticeNumber(number);
                            hlsCusProjectCreditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            hlsCusProjectCreditNotice.setCreditType("VETO");
                            if (cusPrjProject.getMeetingNoticeResult() != null) {
                                hlsCusProjectCreditNotice.setMeetingNoticeResult(cusPrjProject.getMeetingNoticeResult());
                            }
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                hlsCusProjectCreditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }


                        } else if (REVIEW_HOLD.equalsIgnoreCase(cusPrjProject.getApproveResult())) {
                            //暂缓通知书和否决通知书共用一个编码规则
                            String businessCode = REVIEW_VETO + REVIEW_UNDER + codeNumber;

                            String number = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_MEETING_NOTICE",
                                    businessCode, "VETO", params);

                            hlsCusProjectCreditNotice.setProjectNoticeNumber(number);
                            hlsCusProjectCreditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            hlsCusProjectCreditNotice.setCreditType("HOLD");
                            if (cusPrjProject.getMeetingNoticeResult() != null) {
                                hlsCusProjectCreditNotice.setMeetingNoticeResult(cusPrjProject.getMeetingNoticeResult());
                            }
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                hlsCusProjectCreditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }
                        } else if (REVIEW_CHANGE_APPROVE.equalsIgnoreCase(cusPrjProject.getApproveResult())) {
                            String businessCode = REVIEW_CHANGE + REVIEW_UNDER + codeNumber;

                            String number = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_MEETING_NOTICE",
                                    businessCode, REVIEW_CHANGE, params);

                            hlsCusProjectCreditNotice.setProjectNoticeNumber(number);
                            hlsCusProjectCreditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            hlsCusProjectCreditNotice.setCreditPeriod(cusPrjProject.getCreditPeriod());
                            hlsCusProjectCreditNotice.setCreditPeriodFrom(cusPrjProject.getCreditPeriodFrom());
                            hlsCusProjectCreditNotice.setCreditPeriodTo(cusPrjProject.getCreditPeriodTo());
                            hlsCusProjectCreditNotice.setCreditType("APPROVE");
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                hlsCusProjectCreditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }

                        } else if (REVIEW_CHANGE_VETO.equalsIgnoreCase(cusPrjProject.getApproveResult())) {
                            String businessCode = REVIEW_CHANGE + REVIEW_UNDER + codeNumber;

                            String number = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_MEETING_NOTICE",
                                    businessCode, REVIEW_CHANGE, params);

                            hlsCusProjectCreditNotice.setProjectNoticeNumber(number);
                            hlsCusProjectCreditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            hlsCusProjectCreditNotice.setCreditType("VETO");
                            if (cusPrjProject.getMeetingNoticeResult() != null) {
                                hlsCusProjectCreditNotice.setMeetingNoticeResult(cusPrjProject.getMeetingNoticeResult());
                            }
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                hlsCusProjectCreditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }
                        } else if (REVIEW_CHANGE_HOLD.equalsIgnoreCase(cusPrjProject.getApproveResult())) {
                            String businessCode = REVIEW_CHANGE + REVIEW_UNDER + codeNumber;

                            String number = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_MEETING_NOTICE",
                                    businessCode, REVIEW_CHANGE, params);

                            hlsCusProjectCreditNotice.setProjectNoticeNumber(number);
                            hlsCusProjectCreditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            hlsCusProjectCreditNotice.setCreditType("HOLD");
                            if (cusPrjProject.getMeetingNoticeResult() != null) {
                                hlsCusProjectCreditNotice.setMeetingNoticeResult(cusPrjProject.getMeetingNoticeResult());
                            }
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                hlsCusProjectCreditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }

                        }

                        prjNotice = iHlsCusProjectCreditNoticeService.insertSelective(iRequest, hlsCusProjectCreditNotice);
                    } else {
                        hlsCusProjectCreditNotice.setPrjNoticeId(cusPrjProject.getPrjNoticeId());
                        HlsCusProjectCreditNotice creditNotice = new HlsCusProjectCreditNotice();
                        creditNotice = cusProjectCreditNoticeMapper.selectByPrimaryKey(hlsCusProjectCreditNotice);
                        if (REVIEW_APPROVE.equalsIgnoreCase(cusPrjProject.getApproveResult()) || REVIEW_CHANGE_APPROVE.equalsIgnoreCase(cusPrjProject.getApproveResult())) {

                            creditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            creditNotice.setCreditPeriod(cusPrjProject.getCreditPeriod());
                            creditNotice.setCreditPeriodFrom(cusPrjProject.getCreditPeriodFrom());
                            creditNotice.setCreditPeriodTo(cusPrjProject.getCreditPeriodTo());
                            creditNotice.setCreditType("APPROVE");
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                creditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }
                        } else if (REVIEW_VETO.equalsIgnoreCase(cusPrjProject.getApproveResult()) || REVIEW_CHANGE_VETO.equalsIgnoreCase(cusPrjProject.getApproveResult())) {
                            creditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            creditNotice.setCreditType("VETO");
                            if (cusPrjProject.getMeetingNoticeResult() != null) {
                                creditNotice.setMeetingNoticeResult(cusPrjProject.getMeetingNoticeResult());
                            }
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                creditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }
                        } else if (REVIEW_HOLD.equalsIgnoreCase(cusPrjProject.getApproveResult()) || REVIEW_CHANGE_HOLD.equalsIgnoreCase(cusPrjProject.getApproveResult())) {
                            creditNotice.setMeetingNotice(cusPrjProject.getMeetingNotice());
                            creditNotice.setCreditType("HOLD");
                            if (cusPrjProject.getMeetingNoticeResult() != null) {
                                creditNotice.setMeetingNoticeResult(cusPrjProject.getMeetingNoticeResult());
                            }
                            if (cusPrjProject.getProcessInstanceId() != null) {
                                creditNotice.setInstanceId(cusPrjProject.getProcessInstanceId());
                            }
                        }
                        prjNotice = iHlsCusProjectCreditNoticeService.updateByPrimaryKeySelective(iRequest, creditNotice);
                    }

                    if (prjNotice.getPrjNoticeId() != null) {
                        cusPrjProject.setPrjNoticeId(prjNotice.getPrjNoticeId());
                    }

                }
            }
        }

        return list;
    }

    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    @Override
    public void checkItems(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws Exception {
        if (hlsCusPrjProject.getHlsCusPrjProjectLeaseItemList() != null) {
            for (HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem : hlsCusPrjProject.getHlsCusPrjProjectLeaseItemList()) {
                int k = 0;
                for (int a = 0; a < hlsCusPrjProject.getHlsCusPrjProjectLeaseItemList().size(); a++) {
                    if (hlsCusPrjProjectLeaseItem.getLeaseItemId().equals(hlsCusPrjProject.getHlsCusPrjProjectLeaseItemList().get(a).getLeaseItemId())) {
                        k++;
                    }
                }
                if (k > 1) {
                    throw new ResMessageException("无法选择相同租赁物！");
                }
//                if (hlsCusPrjProjectLeaseItem.getLeaseItemId() != null) {
//                    HlsCusPrjProjectLeaseItem prjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
//                    prjProjectLeaseItem.setLeaseItemId(hlsCusPrjProjectLeaseItem.getLeaseItemId());
//                    List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItems =hlsCusPrjProjectLeaseItemMapper.select(prjProjectLeaseItem);
//                    for (int i = 0; i < prjProjectLeaseItems.size(); i++) {
//                        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProjectLeaseItems.get(i).getProjectId());
//                        if(!CANCEL.equals(prjProject.getContractStatus())){
//                            throw new ResMessageException("同一租赁物不能被多个合同引用！");
//                        }
//                    }
//                }
            }
        }
        if (hlsCusPrjProject.getHlsCusPrjProjectBpList() != null) {
            for (HlsCusPrjProjectBp hlsCusPrjProjectBp : hlsCusPrjProject.getHlsCusPrjProjectBpList()) {
                if (hlsCusPrjProjectBp.getLeaseItemId() != null) {
                    HlsCusPrjProjectBp prjProjectBp = new HlsCusPrjProjectBp();
                    prjProjectBp.setLeaseItemId(hlsCusPrjProjectBp.getLeaseItemId());
                    List<HlsCusPrjProjectBp> prjProjectBps = hlsCusPrjProjectBpMapper.select(prjProjectBp);
                    Double pledgeProportion = 0D;
                    for (int i = 0; i < prjProjectBps.size(); i++) {
                        HlsCusPrjProject prjProject = new HlsCusPrjProject();
                        prjProject.setProjectId(prjProjectBps.get(i).getProjectId());
                        iRequest.setAttribute("wflRuleControlFlag", "Y");
                        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, prjProject);
                        if ("VIRTUAL_CON".equals(prjProject.getDataClass())) {
                            if (hlsCusPrjProjectBp.getPrjBpId() == null
                                    || !hlsCusPrjProjectBp.getPrjBpId().equals(prjProjectBps.get(i).getPrjBpId())) {
                                if (prjProjectBps.get(i).getPledgeProportion() != null) {
                                    pledgeProportion += prjProjectBps.get(i).getPledgeProportion();
                                }
                            }

                        }
                    }
                    if (hlsCusPrjProjectBp.getPledgeProportion() != null) {
                        pledgeProportion = pledgeProportion + hlsCusPrjProjectBp.getPledgeProportion();
                    }
                    if (pledgeProportion > 1) {
                        throw new ResMessageException("同一抵押物抵押占比不能超过100%！");
                    }
                }
            }
        }
    }

    private static final String WITHDRAW = "WITHDRAW";

    @Override
    public void projectMeetingWithdraw(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject dto = new HlsCusPrjProject();
        dto.setProjectId(hlsCusPrjProject.getProjectId());
        dto = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, dto);
        databaseLockProvider.lock(dto);

        String taskDefinitionKey = "sid-0HFyefQO-qElg-4aEs-8wfY-ixMUqYk3RA4A";
        List<Task> taskList = taskService.createTaskQuery().taskDefinitionKey(taskDefinitionKey).list();
        if (taskList.size() > 0) {
            for (Task item : taskList) {
                Long processInstanceId = dto.getProcessInstanceId();
                if (processInstanceId != null) {
                    if (String.valueOf(processInstanceId).equals(item.getProcessInstanceId())) {
                        //结束节点
                        Map<String, Object> vMap = new HashMap<String, Object>(2);
                        vMap.put("approveResult", WITHDRAW);
                        vMap.put("approveResultResult", "撤回");
                        vMap.put("withdrawFlag", "Y");
                        vMap.put("comment", "编号为" + dto.getProjectNumber() + "的项目投票已完成。");
                        // 添加审批备注
                        taskService.addComment(item.getId(), item.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "自动审批");
                        taskService.addComment(item.getId(), item.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, "项目撤回");
                        // 自动审批
                        taskService.complete(item.getId(), vMap);
                    }
                }
            }
        }
        //更新状态
        dto.setApprovalStatus("");
        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, dto);
    }

    @Override
    public List<Map> selectPrjBaseChangeInfo(IRequest iRequest, HlsCusPrjProject project, int pagenum, int pagesize) {

        PageHelper.startPage(pagenum, pagesize);
        return hlsCusPrjProjectMapper.selectPrjBaseChangeInfo(project);
        //return null;
    }

    @Override
    public HlsCusPrjProject prjCreateVirtualConFrame(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(hlsCusPrjProject.getRefProjectId());

        prjProject = self().selectByPrimaryKey(iRequest, prjProject);
        HlsCusPrjProject prjProjectF = new HlsCusPrjProject();
        prjProjectF.setProjectId(hlsCusPrjProject.getRefProjectId());
        prjProjectF = self().selectByPrimaryKey(iRequest, prjProject);
        HlsCusPrjProject resultPrjProjectF = new HlsCusPrjProject();
        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(prjProjectF);
        hlsBeanRefUtilService.setFieldValue(resultPrjProjectF, map1);
        resultPrjProjectF.setContractStatus("NEW");
        resultPrjProjectF.setQuotationId(hlsCusPrjProject.getQuotationId());
        resultPrjProjectF.setPenaltyRate(DEFAULT_PENALTY_RATE);
        resultPrjProjectF.setFloatingRangeMethod(FLOATING_RANGE_METHOD);
        resultPrjProjectF.setLeaseItemProperty(LEASE_ITEM_TYPE);
        resultPrjProjectF.setContractAmount(hlsCusPrjProject.getPurchaseFrameAmount());
        resultPrjProjectF.setContractName(hlsCusPrjProject.getPurchaseFrameName());
        resultPrjProjectF.setPurchaseFrame(hlsCusPrjProject.getPurchaseFrame());
        resultPrjProjectF.setPurchaseFrameAmount(hlsCusPrjProject.getPurchaseFrameAmount());
        resultPrjProjectF.setPurchaseFrameName(hlsCusPrjProject.getPurchaseFrameName());
        if (hlsCusPrjProject.getBusinessType() != null) {
            resultPrjProjectF.setBusinessType(hlsCusPrjProject.getBusinessType());
        }
        if (resultPrjProjectF.getContractNumber() == null) {

            StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
            String value = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "CON_CONTRACT", "CONL", "LEASE", params);


            String number3 = "" + stringBuilder.append("H-").append(simpleDateFormat.format(new Date())).append("Y-15-").append(value);
            resultPrjProjectF.setContractNumber(number3);
/*
                resultPrjProjectF.setContractNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, "CON_CONTRACT", "CONL", "LEASE", params));
*/
            resultPrjProjectF.setUsedNumberFlag("N");

        }

        resultPrjProjectF.setProjectId(null);
        resultPrjProjectF.setDataClass("VIRTUAL_CON");
        resultPrjProjectF.setDataType("NORMAL");
        resultPrjProjectF.setRefProjectId(hlsCusPrjProject.getRefProjectId());
        //插入编号
        resultPrjProjectF.setLeasingNumber(this.selectLeasingNumber(resultPrjProjectF.getCompanyId()));
        //传入批复号,与主合同编号一致
        StringBuilder sb1 = new StringBuilder();
        sb1.append("EBFIL");
        sb1.append("-");
        sb1.append(resultPrjProjectF.getLeasingNumber());
        sb1.append("-");
        if ("LEASE".equals(hlsCusPrjProject.getBusinessType())) {
            sb1.append("ZZ-01");
        } else if ("LEASEBACK".equals(hlsCusPrjProject.getBusinessType())) {
            sb1.append("HZ-01");
        } else {
            sb1.append("JY-01");
        }

        resultPrjProjectF.setApprovalNumber(sb1.toString());
        //resultPrjProject.setContractName(resultPrjProject.getApprovalNumber() + hlsCusPrjProjectMapper.getBpNameByProjectTenantId(hlsCusPrjProject));
        resultPrjProjectF = self().insertSelective(iRequest, resultPrjProjectF);
        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setProjectId(resultPrjProjectF.getRefProjectId().toString());
        List<PrjProjectApproval> prjProjectApprovals = prjProjectApprovalMapper.conQueryAll(prjProjectApproval);
        if (prjProjectApprovals.size() > 0) {

            BeanRefUtils.beanToBean(prjProjectApprovals.get(0), prjProjectApproval, hlsBeanRefUtilService);
            prjProjectApproval.setProjectId(resultPrjProjectF.getProjectId().toString());
/*
            prjProjectApproval.setDataClass("VIRTUAL_CON");
*/
            projectApprovalService.insertSelective(iRequest, prjProjectApproval);
        }

        ProjectApprovalCondition projectCreditCondition = new ProjectApprovalCondition();
        projectCreditCondition.setProjectId(resultPrjProjectF.getRefProjectId());
        List<ProjectApprovalCondition> projectCreditConditions = projectApprovalConditionMapper.queryAll(projectCreditCondition);
        for (ProjectApprovalCondition projectCreditConditions1 : projectCreditConditions) {


            BeanRefUtils.beanToBean(projectCreditConditions1, projectCreditCondition, hlsBeanRefUtilService);
            projectCreditConditions1.setProjectId(resultPrjProjectF.getProjectId());
/*
            prjProjectApproval.setDataClass("VIRTUAL_CON");
*/
            projectApprovalConditionService.insertSelective(iRequest, projectCreditConditions1);
        }
        projectInfoCopy(iRequest, resultPrjProjectF);

        return resultPrjProjectF;
    }

    @Override
    public List<HlsCusPrjProject> queryVirtualContract(HlsCusPrjProject hlsCusPrjProject) {
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        if (hlsCusPrjProject.getProjectId() != null) {
            hlsCusPrjProjectList = hlsCusPrjProjectMapper.queryVirtualContract(hlsCusPrjProject);
        }
        return hlsCusPrjProjectList;
    }

    @Override
    public List<Double> queryLeftAmount(HlsCusPrjProject hlscusprjproject) {
        List<Double> left_amount = new ArrayList<>();
        if (hlscusprjproject.getProjectId() != null) {
            left_amount.add(hlsCusPrjProjectMapper.queryLeftAmount(hlscusprjproject.getProjectId()));
        }
        return left_amount;
    }

    /**
     * 二期功能：投放审查管理-投放审查申请按钮：批量提交审批
     *
     * @param iRequest 请求
     * @param list     待提交审批记录
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String signBatchWflSubmit(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, hlsCusPrjProjectMapper.selectByPrimaryKey(list.get(i)));
        }
        //提交前的校验：承租人的投放金额是否在合作方有效可用额度范围内，且该承租人累计已用额度+本次投放金额是否超过其用信上限
        checkSign(iRequest, list);

        StringBuilder submitMessage = new StringBuilder();
        int errorNum = 0;
        StringBuilder errorMessage = new StringBuilder();
        for (HlsCusPrjProject project : list) {
            //TODO 校验通过后提交工作流
            signWorkFlowSubmit(iRequest, project, PROJECT_SIGN_WORK_FLOW);
            project.setSignStatus("APPROVING");
            this.updateByPrimaryKeySelective(iRequest, project);
        }
        String errorMessageStr = errorMessage.toString();
        if (StringUtils.isNotEmpty(errorMessageStr) && (errorMessageStr.lastIndexOf(BR) + BR.length() == errorMessageStr.length())) {
            errorMessageStr = errorMessageStr.substring(0, errorMessageStr.lastIndexOf(BR));
        }
        if (errorNum > 0){
            submitMessage.append("本次提交").append(list.size() - errorNum).append("条单据成功，")
                    .append(errorNum).append("条单据失败。失败信息为：").append(BR).append(errorMessageStr);
        } else {
            submitMessage.append("本次提交").append(list.size() - errorNum).append("条单据成功.");
        }

        return submitMessage.toString();
    }

    /**
     * 二期功能：进件投放审查退回
     *
     * @param iRequest
     * @param list
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void prjSignReturn(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            throw new HlsCusException("待退回的数据为空!");
        }
        for (HlsCusPrjProject project : list) {
            //退回类型
            String returnType = project.getReturnType();

            if (project.getRejectedDate() == null) {
                throw new HlsCusException("退回日期不能为空!");
            }
            if (StringUtils.isBlank(returnType)) {
                throw new HlsCusException("退回类型不能为空!");
            }
            if (StringUtils.isBlank(project.getCancelReason())) {
                throw new HlsCusException("退回原因不能为空!");
            }
            if (StringUtils.isBlank(project.getRejectedDescription())) {
                throw new HlsCusException("退回说明不能为空!");
            }
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(project.getProjectId());
            hlsCusPrjProject = self().selectByPrimaryKey(iRequest, hlsCusPrjProject);


            hlsCusPrjProject.setReturnType(project.getReturnType());
            hlsCusPrjProject.setProjectStatus(project.getReturnType());
            hlsCusPrjProject.setRejectedDate(project.getRejectedDate());
            hlsCusPrjProject.setCancelReason(project.getCancelReason());
            hlsCusPrjProject.setRejectedDescription(project.getRejectedDescription());

            self().updateByPrimaryKey(iRequest, hlsCusPrjProject);
        }
    }

    /**
     * 二期功能：进件投放审查申请工作流提交前校验：承租人的投放金额是否在合作方有效可用额度范围内，
     *                                      且该承租人累计已用额度+本次投放金额是否超过其用信上限。
     *                                      只有满足 '在合作方有效可用额度范围内，且该承租人累计已用额度+本次投放金额不超过超过其用信上限'
     *                                      才能通过校验，提交申请。
     * @param iRequest
     * @param hlsCusPrjProjectList
     */
    private void checkSign(IRequest iRequest, List<HlsCusPrjProject> hlsCusPrjProjectList) throws Exception {
        for (HlsCusPrjProject project : hlsCusPrjProjectList) {

            Long thisTenantId = project.getTenantId();

            //合作方授信总额
            Double creditAmount = 0D;
            //合作方关联承租人已用金额
            Double tenantAmount = 0D;
            //该承租人已用额度
            Double thisTenantAmount = 0D;
            //该承租人本次投放金额
            Double releaseAmount = 0D;
            //该承租人用信上限金额
            Double topLimitCreditAmount = 200000000D;
            //该承租人当前项目的批复用信上限（区分承租人类型：法人or自然人）
            Double creditReplyAmount = 0D;
            //该承租人增量授信总和
            Double incrementalCreditAmountTotal = 0D;

            //可用额度flag，超额度为 true
            boolean creditFlag = false;
            //用信上限flag，超额度为 true
            boolean topLimitFlag = false;
            //上述两个flag都为false才能通过校验

            //该承租人本次投放金额
            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            hlsCusPrjQuotation.setSourceDocumentId(project.getProjectId());
            hlsCusPrjQuotation.setSourceDocumentCategory(PRJ_PROJECT);
            List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationService.selectSelective(iRequest, hlsCusPrjQuotation);
            if (hlsCusPrjQuotationList.size()>0){
                releaseAmount = hlsCusPrjQuotationList.get(0).getFinanceAmount();
            }


            //通过bpid在查询到合作方
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setTenantId(project.getManufacturerId());
            List<HlsCusPrjProject> hlsCusPrjProjects = hlsCusPrjProjectMapper.prjHomePageProjectInfoGridHome(hlsCusPrjProject);
            if (hlsCusPrjProjects.size() > 0){
                //通过查到合作方的chanceId来找到合作方授信金额
                //合作方授信总额
                HlsCreditPlan hlsCreditPlan=new HlsCreditPlan();
                hlsCreditPlan.setSourceDocumentId(hlsCusPrjProjects.get(0).getChanceId());
                List<HlsCreditPlan> hlsCreditPlans = hlsCreditPlanMapper.queryCreditPlanInfo(hlsCreditPlan);
                if (hlsCreditPlans.size() > 0){
                    if (hlsCreditPlans.get(0).getCreditAmt() != null){
                        creditAmount = hlsCreditPlans.get(0).getCreditAmt();
                    }
                }

                //合作方关联承租人已用金额
                hlsCusPrjProject.setTenantAllName(hlsCusPrjProjects.get(0).getTenantAllName());
                List<HlsCusPrjProject> cusPrjProjects = hlsCusPrjProjectMapper.queryPrjHomePageProjectInfoGridNew(hlsCusPrjProject);
                for (HlsCusPrjProject cusPrjProject : cusPrjProjects) {
                    if (cusPrjProject.getFinanceAmount() != null){
                        tenantAmount += cusPrjProject.getFinanceAmount();
                    }
                    //该承租人已用额度
                    if (thisTenantId.equals(cusPrjProject.getTenantId())){
                        if (cusPrjProject.getFinanceAmount() != null){
                            thisTenantAmount += cusPrjProject.getFinanceAmount();
                        }
                    }
                }
            }

            //该承租人当前项目的批复用信上限（区分承租人类型：法人or自然人）
            PrjCreditReplyPara replyPara = new PrjCreditReplyPara();
            replyPara.setReplyId(project.getProjectId());

            HlsCusBpMaster cusBpMaster = new HlsCusBpMaster();
            cusBpMaster.setBpId(project.getTenantId());
            HlsCusBpMaster bpMasterInfo = hlsBpMasterService.selectByPrimaryKey(iRequest, cusBpMaster);
            replyPara.setReplyPara(bpMasterInfo.getBpClass());
            creditReplyAmount = replyParaMapper.selectDefaultValueByReplyIdAndReplyPara(replyPara);
            creditReplyAmount = creditReplyAmount == null ? 0L : creditReplyAmount;

            incrementalCreditAmountTotal = hlsCusPrjProjectBpMapper.selectProjectBpCreditAmountTotalByBpId(project.getTenantId());
            incrementalCreditAmountTotal = incrementalCreditAmountTotal == null ? 0L : incrementalCreditAmountTotal;


            // 开始校验
            creditFlag = add(tenantAmount, releaseAmount, 2) > creditAmount;


            if (creditReplyAmount == 0){
                //不存在批复用信上限

                if (incrementalCreditAmountTotal == 0){
                    //不存在增量授信
                    topLimitFlag = add(thisTenantAmount, releaseAmount, 2) > topLimitCreditAmount;
                } else {
                    //存在增量授信
                    topLimitFlag = add(thisTenantAmount, releaseAmount, 2) > topLimitCreditAmount && add(thisTenantAmount, releaseAmount, 2) > incrementalCreditAmountTotal;
                }

            } else {
                //存在批复用信上限

                if (incrementalCreditAmountTotal == 0){
                    //不存在增量授信
                    topLimitFlag = add(thisTenantAmount, releaseAmount, 2) > creditReplyAmount;
                } else {
                    //存在增量授信
                    topLimitFlag = add(thisTenantAmount, releaseAmount, 2) > creditReplyAmount && add(thisTenantAmount, releaseAmount, 2) > incrementalCreditAmountTotal;
                }
            }

            //超过合作方有效可用额度 或者 超过该承租人累计已用额度+本次投放金额超过其用信上限
            if (creditFlag || topLimitFlag){
                throw new ResMessageException("本次投放金额大于合作方可用额度/该承租人累计已用额度即将超过用信上限，请检查！");
            }
        }

    }

    /**
     * 二期功能：进件投放审查申请工作流提交
     *
     * @param iRequest     请求
     * @param project      进件投放审查申请数据
     * @param workFlowType 用于代码获取工作流提交的实现类
     */
    private void signWorkFlowSubmit(IRequest iRequest, HlsCusPrjProject project, String workFlowType) {
        List<HlsCusPrjProject> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        list.add(project);

        String bpName = prjProjectMapper.selectTenantNameByProject(project);

        //设置工作流参数
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(project));
        map.put(PROJECT, jsonObject.toString());
        map.put(PROJECT_NAME, bpName);
        map.put(DOCUMENT_CATEGORY, project.getDocumentCategory());
        map.put(DOCUMENT_TYPE, project.getDocumentType());
        map.put(DOCUMENT_ID, project.getProjectId());
        map.put(WORKFLOW_TYPE, workFlowType);
        map.put(DOCUMENT_NAME, bpName);
        map.put(DOCUMENT_NUMBER, project.getProjectNumber());
        map.put(LEASE_CHANNEL, project.getLeaseChannel());
        map.put("manufacturerId", project.getManufacturerId());
        activitiStartService.start(iRequest, list, map);
    }

    @Override
    public List<HlsCusPrjProject> submitContractTerminateWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {
        List<HlsCusPrjProject> list = new ArrayList<>();
        if (hlsCusPrjProject.getProjectId() != null) {
            hlsCusPrjProject = this.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            list.add(hlsCusPrjProject);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            if (ObjectUtils.isEmpty(employee)) {
                throw new HlsCusException("获取提交人失败");
            }
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);

            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", "CON_CONTRACT_END_WFL");

            activitiStartService.start(iRequest, list, params);

            //修改单据状态
            hlsCusPrjProject.setTerminateApprovalStatus("APPROVING");
            this.updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
        }
        return list;
    }

    /**
     * 复制文件模板,将读取源文件的输入流复制文件存入一个备用的模板文件
     *
     * @param filePath
     * @param is
     * @throws IOException
     */
    private static synchronized void copyModel(String filePath, InputStream is) throws IOException {

        FileOutputStream fos = new FileOutputStream(filePath); //复制出一个模板
        int readData;
        byte[] b = new byte[1024];

        while ((readData = is.read(b)) != -1) {
            fos.write(b, 0, readData);
        }

        fos.flush();
        is.close();
        fos.close();
    }

    @Override
    public List<HlsCusPrjProjectAttachment> generatePrjBaseAttachment(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws Exception {
        List<HlsCusPrjProjectAttachment> list = new ArrayList<>();

        HlsCusPrjProject project = this.hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (project == null) {
            throw new IllegalArgumentException("项目不存在!");
        } else {
            SysDocumentList condition = new SysDocumentList();
            condition.setDocumentCategory("PRJ_PROJECT");
            //condition.setDocumentType("NORMAL");
            condition.setEnabledFlag("Y");
            List<SysDocumentList> documentList = this.sysDocumentListMapper.select(condition);
            List<SysDocumentList> prjDocumentList = (List) documentList.stream().filter(item -> "PRJ_SALES".equals(item.getDocumentType()) ||
                    "PRJ_BP".equals(item.getDocumentType()) || "PRJ_LEASE".equals(item.getDocumentType())
                    || "PRJ_BP_CZ".equals(item.getDocumentType())
                    || "PRJ_BP_DB".equals(item.getDocumentType())
            ).collect(Collectors.toList());
            HlsCusPrjProjectAttachment attachment = new HlsCusPrjProjectAttachment();
            attachment.setProjectId(project.getProjectId());
            //attachment.setProjectAttachmentCategory("CHANCE_ATT");
            List<HlsCusPrjProjectAttachment> prjAttachs = this.hlsCusPrjProjectAttachmentService.selectSelective(iRequest, attachment);
            List<Long> recentDocumentIds = (List) prjAttachs.stream().map(HlsCusPrjProjectAttachment::getListDocumentId).collect(Collectors.toList());
            List<SysDocumentList> filterDocumentList = (List) prjDocumentList.stream().filter((o) -> {
                return recentDocumentIds.indexOf(o.getDocumentId()) < 0;
            }).collect(Collectors.toList());
            Iterator var13 = filterDocumentList.iterator();

            HlsCusPrjProjectAttachment item;
            while (var13.hasNext()) {
                SysDocumentList item1 = (SysDocumentList) var13.next();
                item = new HlsCusPrjProjectAttachment();
                item.setProjectId(project.getProjectId());
                item.setProjectAttachmentCategory(item1.getDocumentType());
                item.setSourceId(item1.getDocumentId());
                item.setListDocumentId(item1.getDocumentId());
                item.setDocumentName(item1.getDocumentListName());
                item.setUploadDate(new Date());
                item.setUploadPerson(Long.toString(iRequest.getUserId()));
                this.hlsCusPrjProjectAttachmentService.insertSelective(iRequest, item);
                list.add(item);

                //复制附件 立项申请审批表->租赁项目立项审批表
                if ("租赁项目立项审批表".equals(item1.getDocumentListName())) {
                    HlsCusPrjProjectAttachment prjProjectAttachment = new HlsCusPrjProjectAttachment();
                    prjProjectAttachment.setProjectId(project.getProjectId());
                    prjProjectAttachment.setProjectAttachmentCategory(item1.getDocumentType());
                    prjProjectAttachment.setSourceId(item1.getDocumentId());
                    prjProjectAttachment.setListDocumentId(item1.getDocumentId());
                    List<HlsCusPrjProjectAttachment> prjProjectAttachments = hlsCusPrjProjectAttachmentService.select(iRequest, prjProjectAttachment, 1, 999);
                    if (CollectionUtils.isNotEmpty(prjProjectAttachments)) {
                        //获取项目立项-立项申请表 filterAttach
                        HlsCusHlsCreditLineChanceAttach chanceAttach = new HlsCusHlsCreditLineChanceAttach();
                        chanceAttach.setChanceId(project.getChanceId());
                        List<HlsCusHlsCreditLineChanceAttach> chanceAttachs = hlsCreditLineChanceService.select(iRequest, chanceAttach, 1, 999);
                        HlsCusHlsCreditLineChanceAttach filterAttach = chanceAttachs.stream().filter(att -> att.getDocumentName().equals("立项申请表")).collect(Collectors.toList()).get(0);

                        //立项申请表 附件信息 attachmentMultis
                        FndAttachmentMulti filterattachmentMulti = new FndAttachmentMulti();
                        filterattachmentMulti.setTableName("HLS_CREDIT_LINE_CHANCE");
                        filterattachmentMulti.setTablePkValue(filterAttach.getChanceAttachmentId().toString());
                        List<FndAttachmentMulti> attachmentMultis = iFndAttachmentMultiService.select(iRequest, filterattachmentMulti, 1, 999);
                        for (FndAttachmentMulti multi : attachmentMultis) {
                            FndAttachment attchChance = new FndAttachment();
                            attchChance.setAttachmentId(multi.getAttachmentId());
                            attchChance = iFndAttachmentService.selectByPrimaryKey(iRequest, attchChance);
                            FndAttachment file = new FndAttachment();//项目附件
                            BeanRefUtils.beanToBean(attchChance, file, hlsBeanRefUtilService);
                            FndAttachmentMulti fileMulti = new FndAttachmentMulti();
                            BeanRefUtils.beanToBean(multi, fileMulti, hlsBeanRefUtilService);

                            fileMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
                            fileMulti.setTablePkValue(prjProjectAttachments.get(0).getProjectAttachmentId().toString());
                            fileMulti.setAttachmentId(file.getAttachmentId());
                            fileMulti.setCreatedBy(iRequest.getUserId());
                            fileMulti.setCreationDate(new Date());
                            fileMulti.setLastUpdateDate(new Date());
                            fileMulti.setLastUpdatedBy(iRequest.getUserId());
                            fndAttachmentMultiService.insertSelective(iRequest, fileMulti);

                            //服务器文件复制
                            FndAttachment sysFile = attchChance;
                            if (sysFile == null || org.apache.commons.lang.StringUtils.isBlank(sysFile.getFilePath())) {
                                throw new HlsCusException("文件不存在");
                            }
                            File copyfile = new File(sysFile.getFilePath());
                            if (!copyfile.exists()) {
                                throw new HlsCusException("文件不存在");
                            }
                            //定义项目文件的大小
                            int filePrjLength = 0;
                            //用inputStram输入流读取本地的docx文件
                            InputStream inStream = new FileInputStream(copyfile);
                            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
                            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
                            //复制模板
                            copyModel(copyPath, inStream);
                            //用输入流读取复制后的模板
                            InputStream modelIs = new FileInputStream(copyPath);
                            filePrjLength = (int) new File(copyPath).length();

                            file.setSourceTypeCode("fnd_atm_attachment_multi");
                            file.setSourcePkValue(fileMulti.getRecordId().toString());
                            file.setFilePath(copyPath);
                            file.setFileTypeCode(".docx");
                            file.setFileSize(((Integer) filePrjLength).longValue());
                            file.setCreationDate(new Date());
                            file.setCreatedBy(iRequest.getUserId());
                            file.setLastUpdateDate(new Date());
                            file.setLastUpdatedBy(iRequest.getUserId());
                            fndAttachmentService.insertSelective(iRequest, file);
                            fileMulti.setAttachmentId(file.getAttachmentId());
                            fndAttachmentMultiService.updateByPrimaryKey(iRequest, fileMulti);
                        }
                    }
                }
            }
        }

        return list;
    }

    @Override
    public List<FndAttachmentMulti> downloadAuditContent(List<PrjProjectApproval> hlsCusLonContractRepayment, IRequest requestContext, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseData responseData = new ResponseData();

        //解析前台传入的数据
        if (!CollectionUtils.isNotEmpty(hlsCusLonContractRepayment)) {
            throw new RuntimeException("参数不能为空!");
        }

        //获取文件暂存目录
        String temporarilyPath = "/attachment";

        List<FndAttachmentMulti> pathList = new ArrayList<>();

        if (hlsCusLonContractRepayment.size() > 0) {
            List<FndAttachmentMulti> responseDataPay = createFile(hlsCusLonContractRepayment, requestContext, temporarilyPath, request, response);
            if (responseDataPay.size() > 0) {
                pathList.addAll(responseDataPay);
            }
        }

        return pathList;
    }

    @SneakyThrows
    @Override
    public List<FndAttachmentMulti> downloadEtBooksContent(List<HlsCusPrjProject> project, IRequest requestContext, HttpServletRequest request, HttpServletResponse response) {
        ResponseData responseData = new ResponseData();

        //解析前台传入的数据
        if (!CollectionUtils.isNotEmpty(project)) {
            throw new RuntimeException("参数不能为空!");
        }

        //获取文件暂存目录
        String temporarilyPath = "/attachment";

        List<FndAttachmentMulti> pathList = new ArrayList<>();

        if (project.size() > 0) {
            List<FndAttachmentMulti> responseDataPay = createEtBooksFile(project, requestContext, temporarilyPath, request, response);
            if (responseDataPay.size() > 0) {
                pathList.addAll(responseDataPay);
            }
        }

        return pathList;
    }

    @Override
    public void projectConditionImport(IRequest iRequest, Long headerId, Long approvId, Long projectId, String approvalType) {
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(headerId, 4L);
        if (approvId == null || projectId == null || approvalType == null) {
            throw new RuntimeException("关联数据缺失");
        }
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            ProjectApprovalCondition record = new ProjectApprovalCondition();
            record.setProjectId(projectId);
            record.setApprovalId(approvId);
            record.setApprovalSeq(fndInterfaceLine.getAttributes_1());
            record.setConditionContent(fndInterfaceLine.getAttributes_2());
            record.setApprovalType(approvalType);
            projectApprovalConditionService.insert(iRequest, record);
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

    //生成并下载
    private List<FndAttachmentMulti> createEtBooksFile(List<HlsCusPrjProject> project, IRequest requestContext, String temporarilyPath, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<FndAttachmentMulti> conDocFileMultiList = new ArrayList<>();

        for (int i = 0; i < project.size(); i++) {
            HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
            hlsDocFileTemplet.setTempletId(1L);
            //hlsDocFileTemplet = hlsDocFileTempletMapper.selectByPrimaryKey(hlsDocFileTemplet);

            Map<String, Object> map = new HashMap<>();
            map.put("templetId", 81L);

            map.put("fileName", "提前结清说明书");

            map.put("projectId", project.get(i).getProjectId());


            //生成文本前删除原来的文本
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("CON_CHANGE_ET_INFO");
            fndAttachmentMulti.setTablePkValue(project.get(i).getProjectId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(requestContext, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourcePkValue = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue("CON_CHANGE_ET_INFO", project.get(i).getProjectId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue("fnd_atm_attachment_multi", sourcePkValue);
            }

            //开始生成文件
            docxCreateEtBooksContentMethod(requestContext, map);

            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
            conDocFileMulti.setTableName("CON_CHANGE_ET_INFO");
            conDocFileMulti.setTablePkValue(project.get(i).getProjectId().toString());
            List<FndAttachmentMulti> fileMultiList = fndAttachmentMultiService.selectSelective(requestContext, conDocFileMulti);
            conDocFileMultiList.add(fileMultiList.get(0));
        }
        return conDocFileMultiList;
    }

    //生成并下载
    private List<FndAttachmentMulti> createFile(List<PrjProjectApproval> hlsCusLonContractRepayment, IRequest requestContext, String temporarilyPath, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<FndAttachmentMulti> conDocFileMultiList = new ArrayList<>();

        for (int i = 0; i < hlsCusLonContractRepayment.size(); i++) {
            HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
            hlsDocFileTemplet.setTempletId(1L);
            //hlsDocFileTemplet = hlsDocFileTempletMapper.selectByPrimaryKey(hlsDocFileTemplet);

            Map<String, Object> map = new HashMap<>();
            map.put("templetId", 1L);

            map.put("fileName", "审议结论表");

            map.put("approvalId", hlsCusLonContractRepayment.get(i).getApprovalId());


            //生成文本前删除原来的文本
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("PRJ_PROJECT_APPROVAL");
            fndAttachmentMulti.setTablePkValue(hlsCusLonContractRepayment.get(i).getApprovalId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(requestContext, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourcePkValue = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue("PRJ_PROJECT_APPROVAL", hlsCusLonContractRepayment.get(i).getApprovalId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue("fnd_atm_attachment_multi", sourcePkValue);
            }

            //开始生成文件
            docxCreateAuditContentMethod(requestContext, map);

            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
            conDocFileMulti.setTableName("PRJ_PROJECT_APPROVAL");
            conDocFileMulti.setTablePkValue(hlsCusLonContractRepayment.get(i).getApprovalId().toString());
            List<FndAttachmentMulti> fileMultiList = fndAttachmentMultiService.selectSelective(requestContext, conDocFileMulti);
            conDocFileMultiList.add(fileMultiList.get(0));
        }
        return conDocFileMultiList;
    }

    private void docxCreateEtBooksContentMethod(IRequest requestContext, Map<String, Object> params) throws Exception {
        //templetId 模板文件的id
        Long templetId = Long.parseLong(params.get("templetId").toString());
        String fileName = params.get("fileName").toString();
        Long sourceDocLineId = Long.parseLong(params.get("projectId").toString());


        //fnd_atm_attachment的sourceTypeCode，这里是表fnd_atm_attachment_multi的名字
        String sourceTypeCode = "fnd_atm_attachment_multi";

        //查模板的参数
        String tableName = "hls_doc_file_templet";
        String tablPkValue = templetId.toString();

        FndAttachmentMulti sysFileMulti = new FndAttachmentMulti();
        sysFileMulti.setTableName(tableName);
        sysFileMulti.setTablePkValue(tablPkValue);
        List<FndAttachmentMulti> resultMultis = fndAttachmentMultiService.select(requestContext, sysFileMulti, 1, 1);
        if (resultMultis.isEmpty()) {
            throw new HlsCusException("文件模板不存在");
        }
        FndAttachmentMulti fndAttachmentMulti = resultMultis.get(0);


        FndAttachment templateFileParam = new FndAttachment();
        templateFileParam.setSourceTypeCode(sourceTypeCode);
        templateFileParam.setSourcePkValue(fndAttachmentMulti.getRecordId().toString());

        List<FndAttachment> fndAttachments = new ArrayList<FndAttachment>();
        fndAttachments = fndAttachmentService.selectSelective(requestContext, templateFileParam);

        FndAttachment sysFile = fndAttachments.get(0);
        if (sysFile == null || org.apache.commons.lang.StringUtils.isBlank(sysFile.getFilePath())) {
            throw new HlsCusException("文件模版不存在");
        }
        /*if (OSSHelper.isOSSEnabled()) {
            File file = fndAttachmentService.downloadAttachmentToFileSystem(sysFile.getAttachmentId());
            sysFile.setFilePath(file.getPath());
        }*/
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            throw new HlsCusException("文件模版不存在");
        }
        //定义备份文件的大小
        int fileBackLength = 0;
        //先获取模板文件的大小
        int fileLength = (int) file.length();
        if (fileLength > 0) {
            //用inputStram输入流读取本地的docx文件
            InputStream inStream = new FileInputStream(file);
            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
            //复制模板
            copyModel(copyPath, inStream);
            //用输入流读取复制后的模板
            InputStream modelIs = new FileInputStream(copyPath);
            //生成合同文本
            createDocx(requestContext, modelIs, new File(copyPath), params);
            fileBackLength = (int) new File(copyPath).length();
            FndAttachmentMulti condition = new FndAttachmentMulti();
            condition.setTablePkValue(sourceDocLineId.toString());
            condition.setTableName("CON_CHANGE_ET_INFO");
            List<FndAttachmentMulti> list = fndAttachmentMultiService.selectSelective(requestContext, condition);
            //保存生成文件
            FndAttachment conDocFile = null;
            FndAttachmentMulti conDocFileMul = null;
            //已经生成过直接修改路径后保存
            if (!list.isEmpty()) {
                conDocFileMul = list.get(0);
                FndAttachment fndCondition = new FndAttachment();
                fndCondition.setSourceTypeCode(sourceTypeCode);
                fndCondition.setSourcePkValue(conDocFileMul.getRecordId().toString());
                conDocFile = fndAttachmentService.selectSelective(requestContext, fndCondition).get(0);
                //上传
                conDocFile.setFilePath(copyPath);
                conDocFile.setFileName(fileName);
                fndAttachmentService.updateByPrimaryKeySelective(requestContext, conDocFile);
            } else {
                conDocFile = new FndAttachment();
//                String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
                conDocFile.setFileName(fileName + ".docx");
                FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
                conDocFileMulti.setTableName("CON_CHANGE_ET_INFO");
                conDocFileMulti.setTablePkValue(sourceDocLineId.toString());
                conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                conDocFileMulti.setCreatedBy(requestContext.getUserId());
                conDocFileMulti.setCreationDate(new Date());
                conDocFileMulti.setLastUpdateDate(new Date());
                conDocFileMulti.setLastUpdatedBy(requestContext.getUserId());
                fndAttachmentMultiService.insertSelective(requestContext, conDocFileMulti);
                conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
                conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
                //上传
                conDocFile.setFilePath(copyPath);
                conDocFile.setFileTypeCode(".docx");
                conDocFile.setFileSize(((Integer) fileBackLength).longValue());
                conDocFile.setCreationDate(new Date());
                conDocFile.setCreatedBy(requestContext.getUserId());
                conDocFile.setLastUpdateDate(new Date());
                conDocFile.setLastUpdatedBy(requestContext.getUserId());
                fndAttachmentService.insertSelective(requestContext, conDocFile);
                conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                fndAttachmentMultiService.updateByPrimaryKey(requestContext, conDocFileMulti);
            }
        }
    }


    private void docxCreateAuditContentMethod(IRequest requestContext, Map<String, Object> params) throws Exception {
        //templetId 模板文件的id
        Long templetId = Long.parseLong(params.get("templetId").toString());
        String fileName = params.get("fileName").toString();
        Long sourceDocLineId = Long.parseLong(params.get("approvalId").toString());


        //fnd_atm_attachment的sourceTypeCode，这里是表fnd_atm_attachment_multi的名字
        String sourceTypeCode = "fnd_atm_attachment_multi";

        //查模板的参数
        String tableName = "hls_doc_file_templet";
        String tablPkValue = templetId.toString();

        FndAttachmentMulti sysFileMulti = new FndAttachmentMulti();
        sysFileMulti.setTableName(tableName);
        sysFileMulti.setTablePkValue(tablPkValue);
        List<FndAttachmentMulti> resultMultis = fndAttachmentMultiService.select(requestContext, sysFileMulti, 1, 1);
        if (resultMultis.isEmpty()) {
            throw new HlsCusException("文件模板不存在");
        }
        FndAttachmentMulti fndAttachmentMulti = resultMultis.get(0);


        FndAttachment templateFileParam = new FndAttachment();
        templateFileParam.setSourceTypeCode(sourceTypeCode);
        templateFileParam.setSourcePkValue(fndAttachmentMulti.getRecordId().toString());

        List<FndAttachment> fndAttachments = new ArrayList<FndAttachment>();
        fndAttachments = fndAttachmentService.selectSelective(requestContext, templateFileParam);

        FndAttachment sysFile = fndAttachments.get(0);
        if (sysFile == null || org.apache.commons.lang.StringUtils.isBlank(sysFile.getFilePath())) {
            throw new HlsCusException("文件模版不存在");
        }
        /*if (OSSHelper.isOSSEnabled()) {
            File file = fndAttachmentService.downloadAttachmentToFileSystem(sysFile.getAttachmentId());
            sysFile.setFilePath(file.getPath());
        }*/
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            throw new HlsCusException("文件模版不存在");
        }
        //定义备份文件的大小
        int fileBackLength = 0;
        //先获取模板文件的大小
        int fileLength = (int) file.length();
        if (fileLength > 0) {
            //用inputStram输入流读取本地的docx文件
            InputStream inStream = new FileInputStream(file);
            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
            //复制模板
            copyModel(copyPath, inStream);
            //用输入流读取复制后的模板
            InputStream modelIs = new FileInputStream(copyPath);
            //生成合同文本
            createDocx(requestContext, modelIs, new File(copyPath), params);
            fileBackLength = (int) new File(copyPath).length();
            FndAttachmentMulti condition = new FndAttachmentMulti();
            condition.setTablePkValue(sourceDocLineId.toString());
            condition.setTableName("PRJ_PROJECT_APPROVAL");
            List<FndAttachmentMulti> list = fndAttachmentMultiService.selectSelective(requestContext, condition);
            //保存生成文件
            FndAttachment conDocFile = null;
            FndAttachmentMulti conDocFileMul = null;
            //已经生成过直接修改路径后保存
            if (!list.isEmpty()) {
                conDocFileMul = list.get(0);
                FndAttachment fndCondition = new FndAttachment();
                fndCondition.setSourceTypeCode(sourceTypeCode);
                fndCondition.setSourcePkValue(conDocFileMul.getRecordId().toString());
                conDocFile = fndAttachmentService.selectSelective(requestContext, fndCondition).get(0);
                //上传
                conDocFile.setFilePath(copyPath);
                conDocFile.setFileName(fileName);
                fndAttachmentService.updateByPrimaryKeySelective(requestContext, conDocFile);
            } else {
                conDocFile = new FndAttachment();
//                String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
                conDocFile.setFileName(fileName + ".docx");
                FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
                conDocFileMulti.setTableName("PRJ_PROJECT_APPROVAL");
                conDocFileMulti.setTablePkValue(sourceDocLineId.toString());
                conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                conDocFileMulti.setCreatedBy(requestContext.getUserId());
                conDocFileMulti.setCreationDate(new Date());
                conDocFileMulti.setLastUpdateDate(new Date());
                conDocFileMulti.setLastUpdatedBy(requestContext.getUserId());
                fndAttachmentMultiService.insertSelective(requestContext, conDocFileMulti);
                conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
                conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
                //上传
                conDocFile.setFilePath(copyPath);
                conDocFile.setFileTypeCode(".docx");
                conDocFile.setFileSize(((Integer) fileBackLength).longValue());
                conDocFile.setCreationDate(new Date());
                conDocFile.setCreatedBy(requestContext.getUserId());
                conDocFile.setLastUpdateDate(new Date());
                conDocFile.setLastUpdatedBy(requestContext.getUserId());
                fndAttachmentService.insertSelective(requestContext, conDocFile);
                conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                fndAttachmentMultiService.updateByPrimaryKey(requestContext, conDocFileMulti);
            }
        }
    }


    public void createDocx(IRequest requestContext, InputStream is, File file, Map<String, Object> params) throws Exception {
        //通过输入流构建WordprocessingMLPackage对象
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);
        //将构建的wordMLPackage对象传入方法中
        wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, requestContext, params);
        //将替换后的合同文本保存到服务器上作为备份
        wordMLPackage.save(file);
    }


    //投后检查报告生成
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachment> reportVirCreateCheckDocx(IRequest iRequest, PrjCheck prjcheck) throws Exception {
        List<FndAttachment> list = new ArrayList<>();
        String reportDocx = "PRJ_RENT_CHECK";
        String docxDescription = "投后检查报告";
        String notTemplate = "投后检查报告模板不存在，请检查！";
        String[] templetIds = new String[1];

        Long project_id = prjcheck.getContractId();
        Long check_id = prjcheck.getCheckId();
        String template_code = prjcheck.getTemplateCode();

        HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
        hlsDocFileTemplet.setTempletCode(template_code);
        hlsDocFileTemplet = hlsDocFileTempletService.select(iRequest, hlsDocFileTemplet, 1, 9999).get(0);

        templetIds[0] = hlsDocFileTemplet.getTempletId().toString();

        if (prjcheck.getContractId() == null) {
            throw new ResMessageException("未找到项目方案，请保存后在生成!");
        }

        if (templetIds.length == 0) {
            throw new ResMessageException("未找到投后检查报告模板，请核查后再生成!");
        }

        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();

        hlsCusPrjProjectAttachment.setProjectId(project_id);
        hlsCusPrjProjectAttachment.setProjectAttachmentCategory(reportDocx);
        List<HlsCusPrjProjectAttachment> atmLists = hlsCusPrjProjectAttachmentService.selectSelective(iRequest, hlsCusPrjProjectAttachment);
        atmLists.forEach(item -> {
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName(reportDocx);
            fndAttachmentMulti.setTablePkValue(item.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourceTypeCode = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue(reportDocx, item.getProjectAttachmentId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue(FND_ATM_ATTACHMENT_MULTI, sourceTypeCode);
            }
            hlsCusPrjProjectAttachmentService.deleteByPrimaryKey(item);
        });

        HlsCusPrjProjectAttachment ppa = null;
        Map<String, Object> params = null;

        for (int i = 0; i < templetIds.length; i++) {
            hlsDocFileTemplet = new HlsDocFileTemplet();
            hlsDocFileTemplet.setTempletId(Long.parseLong(templetIds[i]));
            hlsDocFileTemplet = hlsDocFileTempletService.selectByPrimaryKey(iRequest, hlsDocFileTemplet);
            if (hlsDocFileTemplet == null) {
                throw new HlsCusException(notTemplate);
            }
            //重新插入合同文本记录
            HlsCusPrjProject hmr = new HlsCusPrjProject();
            hmr.setProjectId(project_id);
            hmr = this.selectByPrimaryKey(iRequest, hmr);
            StringBuilder attachmentName = new StringBuilder();
            attachmentName.append(hmr.getProjectNumber()).append("-").append(hlsDocFileTemplet.getTempletName());
            ppa = new HlsCusPrjProjectAttachment();
            ppa.setProjectId(project_id);
            ppa.setProjectAttachmentCategory(reportDocx);
            ppa.setDocumentName(attachmentName.toString());
            ppa.setDescription(docxDescription);
            ppa.setSourceId(hlsDocFileTemplet.getTempletId());
            ppa.setUploadPerson(iRequest.getUserId().toString());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ppa.setUploadDate(new Date());
            ppa = hlsCusPrjProjectAttachmentService.insertSelective(iRequest, ppa);

            //取对应参数生成合同文本文件
            params = new HashMap<String, Object>();
            params.put(PROJECT_ID, project_id);
            params.put(CHECK_ID, check_id);
            params.put(INSTANCE_ID, prjcheck.getProcessInstanceId());
            params.put(TEMPLET_ID, hlsDocFileTemplet.getTempletId());
            params.put(PROJECT_ATTACHMENT_ID, ppa.getProjectAttachmentId());
            params.put(TABLE_NAME, reportDocx);
            params.put(SOURCE_TYPE, reportDocx);
            list.addAll(reportDocxService.process(iRequest, params, null));
        }
        return list;
    }


    private void checkContractWriteOff(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {
        HlsCusConContract contract = new HlsCusConContract();
        contract.setProjectId(hlsCusPrjProject.getProjectId());
        contract = hlsCusConContractService.select(requestCtx, contract, 1, 99999).get(0);
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(contract.getContractId());
        //检查是否满足提交条件
        List<HlsCusConContractCashflow> list = hlsCusConContractCashflowService.select(requestCtx, hlsCusConContractCashflow, 1, 9999);
        for (HlsCusConContractCashflow cashflow : list) {
            if (MathUtil.sub(cashflow.getDueAmount(), cashflow.getReceivedAmount()) != 0) {
                throw new HlsCusException("请完全核销现金流后进行合同结束处理！");
            }
        }
    }

    @Override
    public HlsCusPrjProject terminateProject(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(hlsCusPrjProject.getProjectId());
        prjProject = self().selectByPrimaryKey(requestCtx, prjProject);

        checkContractWriteOff(requestCtx, prjProject);

        prjProject.setContractStatus(TERMINATE);
        prjProject.setTerminateApprovalStatus("APPROVED");
        prjProject = self().updateByPrimaryKey(requestCtx, prjProject);

        HlsCusConContract contract = new HlsCusConContract();
        contract.setProjectId(hlsCusPrjProject.getProjectId());
        contract.setContractStatus("TERMINATE");
        hlsCusConContractMapper.updateByProjectId(contract);

        return prjProject;
    }

    /**
     * 项目预设附件生成  根据选择的type
     */
    @Override
    public List<HlsCusPrjProjectAttachment> generatePrjAttachment(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject, List<String> type) throws Exception {
        List<HlsCusPrjProjectAttachment> list = new ArrayList<>();

        HlsCusPrjProject project = this.hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (project == null) {
            throw new IllegalArgumentException("项目不存在!");
        } else {
            for (String documentType : type) {
                //资料清单
                SysDocumentList condition = new SysDocumentList();
                condition.setDocumentCategory("PRJ_PROJECT");
                condition.setEnabledFlag("Y");
                condition.setDocumentType(documentType);
                List<SysDocumentList> prjDocumentList = this.sysDocumentListMapper.select(condition);
                //项目附件
                HlsCusPrjProjectAttachment attachment = new HlsCusPrjProjectAttachment();
                attachment.setProjectId(project.getProjectId());
                List<HlsCusPrjProjectAttachment> prjAttachs = this.hlsCusPrjProjectAttachmentService.selectSelective(iRequest, attachment);
                List<Long> recentDocumentIds = (List) prjAttachs.stream().map(HlsCusPrjProjectAttachment::getListDocumentId).collect(Collectors.toList());
                //获取项目未生成的附件资料
                List<SysDocumentList> filterDocumentList = (List) prjDocumentList.stream().filter((o) -> {
                    return recentDocumentIds.indexOf(o.getDocumentId()) < 0;
                }).collect(Collectors.toList());
                Iterator var13 = filterDocumentList.iterator();

                HlsCusPrjProjectAttachment item;
                while (var13.hasNext()) {
                    SysDocumentList item1 = (SysDocumentList) var13.next();
                    item = new HlsCusPrjProjectAttachment();
                    item.setProjectId(project.getProjectId());
                    item.setProjectAttachmentCategory(item1.getDocumentType());
                    item.setSourceId(item1.getDocumentId());
                    item.setListDocumentId(item1.getDocumentId());
                    item.setDocumentName(item1.getDocumentListName());
                    item.setUploadDate(new Date());
                    item.setUploadPerson(Long.toString(iRequest.getUserId()));
                    this.hlsCusPrjProjectAttachmentService.insertSelective(iRequest, item);
                    list.add(item);

                    //复制附件 立项申请审批表->租赁项目立项审批表
                    if ("租赁项目立项审批表".equals(item1.getDocumentListName())) {
                        HlsCusPrjProjectAttachment prjProjectAttachment = new HlsCusPrjProjectAttachment();
                        prjProjectAttachment.setProjectId(project.getProjectId());
                        prjProjectAttachment.setProjectAttachmentCategory(item1.getDocumentType());
                        prjProjectAttachment.setSourceId(item1.getDocumentId());
                        prjProjectAttachment.setListDocumentId(item1.getDocumentId());
                        List<HlsCusPrjProjectAttachment> prjProjectAttachments = hlsCusPrjProjectAttachmentService.select(iRequest, prjProjectAttachment, 1, 999);
                        if (CollectionUtils.isNotEmpty(prjProjectAttachments)) {
                            //获取项目立项-立项申请表 filterAttach
                            HlsCusHlsCreditLineChanceAttach chanceAttach = new HlsCusHlsCreditLineChanceAttach();
                            chanceAttach.setChanceId(project.getChanceId());
                            List<HlsCusHlsCreditLineChanceAttach> chanceAttachs = hlsCreditLineChanceService.select(iRequest, chanceAttach, 1, 999);
                            HlsCusHlsCreditLineChanceAttach filterAttach = chanceAttachs.stream().filter(att -> att.getDocumentName().equals("立项申请表")).collect(Collectors.toList()).get(0);

                            //立项申请表 附件信息 attachmentMultis
                            FndAttachmentMulti filterattachmentMulti = new FndAttachmentMulti();
                            filterattachmentMulti.setTableName("HLS_CREDIT_LINE_CHANCE");
                            filterattachmentMulti.setTablePkValue(filterAttach.getChanceAttachmentId().toString());
                            List<FndAttachmentMulti> attachmentMultis = iFndAttachmentMultiService.select(iRequest, filterattachmentMulti, 1, 999);
                            for (FndAttachmentMulti multi : attachmentMultis) {
                                FndAttachment attchChance = new FndAttachment();
                                attchChance.setAttachmentId(multi.getAttachmentId());
                                attchChance = iFndAttachmentService.selectByPrimaryKey(iRequest, attchChance);
                                FndAttachment file = new FndAttachment();//项目附件
                                BeanRefUtils.beanToBean(attchChance, file, hlsBeanRefUtilService);
                                FndAttachmentMulti fileMulti = new FndAttachmentMulti();
                                BeanRefUtils.beanToBean(multi, fileMulti, hlsBeanRefUtilService);

                                fileMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
                                fileMulti.setTablePkValue(prjProjectAttachments.get(0).getProjectAttachmentId().toString());
                                fileMulti.setAttachmentId(file.getAttachmentId());
                                fileMulti.setCreatedBy(iRequest.getUserId());
                                fileMulti.setCreationDate(new Date());
                                fileMulti.setLastUpdateDate(new Date());
                                fileMulti.setLastUpdatedBy(iRequest.getUserId());
                                fndAttachmentMultiService.insertSelective(iRequest, fileMulti);

                                //服务器文件复制
                                FndAttachment sysFile = attchChance;
                                if (sysFile == null || org.apache.commons.lang.StringUtils.isBlank(sysFile.getFilePath())) {
                                    throw new HlsCusException("文件不存在");
                                }
                                File copyfile = new File(sysFile.getFilePath());
                                if (!copyfile.exists()) {
                                    throw new HlsCusException("文件不存在");
                                }
                                //定义项目文件的大小
                                int filePrjLength = 0;
                                //用inputStram输入流读取本地的docx文件
                                InputStream inStream = new FileInputStream(copyfile);
                                //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
                                String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
                                //复制模板
                                copyModel(copyPath, inStream);
                                //用输入流读取复制后的模板
                                InputStream modelIs = new FileInputStream(copyPath);
                                filePrjLength = (int) new File(copyPath).length();

                                file.setSourceTypeCode("fnd_atm_attachment_multi");
                                file.setSourcePkValue(fileMulti.getRecordId().toString());
                                file.setFilePath(copyPath);
                                file.setFileTypeCode(".docx");
                                file.setFileSize(((Integer) filePrjLength).longValue());
                                file.setCreationDate(new Date());
                                file.setCreatedBy(iRequest.getUserId());
                                file.setLastUpdateDate(new Date());
                                file.setLastUpdatedBy(iRequest.getUserId());
                                fndAttachmentService.insertSelective(iRequest, file);
                                fileMulti.setAttachmentId(file.getAttachmentId());
                                fndAttachmentMultiService.updateByPrimaryKey(iRequest, fileMulti);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    @SneakyThrows
    @Override
    public HlsCusPrjProject creditProjectGenerate(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) {

        //获取授信项目
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(hlsCusPrjProject.getChanceId());
        chance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(requestCtx, chance);
        //获取授信企业  授信管理中一个商业伙伴可能存在多个角色，每个角色分配不同的授信金额
        HlsCusHlsCreditLineChanceBp chanceBpRecord = new HlsCusHlsCreditLineChanceBp();
        chanceBpRecord.setChanceBpId(hlsCusPrjProject.getCreditChanceBpId());
        HlsCusHlsCreditLineChanceBp chanceBp = hlsCusHlsCreditLineChanceBpService.select(requestCtx, chanceBpRecord, 1, 999).get(0);

        //获取关联的授信尽调prj表数据
        HlsCusPrjProject prj = new HlsCusPrjProject();
        prj.setProjectId(chance.getJdProjectId());
        prj = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prj);

        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(prj);

        //新建项目,除了项目编号,项目金额,授信方案金额外其他数据全部从尽调项目复制
        HlsCusPrjProject creditProject = new HlsCusPrjProject();
        hlsBeanRefUtilService.setFieldValue(creditProject, map1);
        creditProject.setRefProjectId(prj.getProjectId());
        creditProject.setIndustry(hlsCusPrjProject.getIndustry());
        creditProject.setIndustryType(hlsCusPrjProject.getIndustryType());
        creditProject.setTenantId(hlsCusPrjProject.getTenantId());
        creditProject.setHostProjectManager(hlsCusPrjProject.getHostProjectManager());
        creditProject.setAssistProjectManager(hlsCusPrjProject.getAssistProjectManager());
        creditProject.setBusinessType(hlsCusPrjProject.getBusinessType());
        creditProject.setChanceId(hlsCusPrjProject.getChanceId());
        String projectNumber = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, "PRJ_PROJECT", "PRJ_PROJECT", hlsCusPrjProject.getBusinessType(), params);
        creditProject.setProjectNumber(projectNumber);
        creditProject.setLeaseItemAmount(chanceBp.getCreditAmount());
        creditProject.setFinanceAmount(chanceBp.getCreditAmount());
        creditProject.setCreditFlag("N");
        creditProject.setProjectId(null);
        creditProject.setProjectName(null);
        //授信额度占用创建的项目
        creditProject.setDataType("CREDIT_NORMAL");
        creditProject.setProjectStatus("NEW");
        creditProject.setCreditTenantId(hlsCusPrjProject.getTenantId());
        creditProject.setCreditChanceBpId(hlsCusPrjProject.getCreditChanceBpId());
        creditProject = self().insertSelective(requestCtx, creditProject);

        //更新授权企业表的CREDIT_PROJECT_ID 这里没多大实际意义，一个授信客户可以创建多个项目
        Long creditProjectId = creditProject.getProjectId();
        chanceBp.setCreditProjectId(creditProjectId);
        hlsCusHlsCreditLineChanceBpService.updateByPrimaryKeySelective(requestCtx, chanceBp);

        //插入从表 prj_project_bp(客户信息) 非授信主体项目 自动带出担保人
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(chanceBp);
        hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectBp, map);
        hlsCusPrjProjectBp.setProjectId(creditProjectId);
        hlsCusPrjProjectBp.setBpCategroy("PRJ_PROJECT");
        hlsCusPrjProjectBp.setBpType("TENANT");
        hlsCusPrjProjectBpService.insertSelective(requestCtx, hlsCusPrjProjectBp);
        if (!"MANUFACTURER".equals(chanceBp.getBpType())) {
            HlsCusPrjProjectBp jdPrjBpBase = new HlsCusPrjProjectBp();
            jdPrjBpBase.setProjectId(creditProject.getRefProjectId());
            jdPrjBpBase.setBpType("WARRANTOR");
            List<HlsCusPrjProjectBp> jdPrjBpList = hlsCusPrjProjectBpService.select(requestCtx, jdPrjBpBase, 1, 999);
            for (HlsCusPrjProjectBp dt : jdPrjBpList) {
                HlsCusPrjProjectBp hlsCusPrjProjectW = new HlsCusPrjProjectBp();
                Map<String, String> mapBp = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(hlsCusPrjProjectW, mapBp);
                hlsCusPrjProjectW.setProjectId(creditProjectId);
                hlsCusPrjProjectW.setBpCategroy("PRJ_PROJECT");
                hlsCusPrjProjectBpService.insertSelective(requestCtx, hlsCusPrjProjectW);
            }
        }
        //报价  授信尽调没有报价数据
        HlsCusPrjQuotation hlsCusPrjQuotation1 = new HlsCusPrjQuotation();
        hlsCusPrjQuotation1.setSourceDocumentId(creditProject.getProjectId());
        hlsCusPrjQuotation1.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotation1.setDataClass("PRJ_PROJECT_INVEST");
        hlsCusPrjQuotation1.setPaymentStatus("NEW");
        hlsCusPrjQuotation1.setLeaseItemAmount(null);
        hlsCusPrjQuotation1.setFinanceAmount(null);
        hlsCusPrjQuotation1 = hlsCusPrjQuotationService.insertSelective(requestCtx, hlsCusPrjQuotation1);

        //资料清单生成
        List<String> type = new ArrayList<>();
        type.add("PRJ_BP");
        type.add("PRJ_LEASE");
        type.add("PRJ_BP_CZ");
        type.add("PRJ_BP_DB");
        hlsCusPrjProjectService.generatePrjAttachment(requestCtx, creditProject, type);
        //插入从表 end

        return creditProject;
    }

    @Override
    public HlsCusPrjProject prjCreateCreditCon(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {
        HlsCusPrjProject prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, hlsCusPrjProject);

        if (prjProject.getContractNumber() != null) {
            throw new HlsCusException("该授信项目已创建合同");
        }

        HlsCusPrjProject resultPrjProject = new HlsCusPrjProject();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(prjProject);
        hlsBeanRefUtilService.setFieldValue(resultPrjProject, map);
        resultPrjProject.setProjectNumber(prjProject.getProjectNumber());
        resultPrjProject.setProjectName(prjProject.getProjectName());

        resultPrjProject.setContractStatus("NEW");
        resultPrjProject.setPurchaseFrame("N");
        resultPrjProject.setContractAmount(prjProject.getLeaseItemAmount());

        if (prjProject.getContractNumber() == null) {

            StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
            String value = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, "CON_CONTRACT", "CONL", "LEASE", params);


            String number3 = "" + stringBuilder.append("H-").append(simpleDateFormat.format(new Date())).append("Y-15-").append(value);
            resultPrjProject.setContractNumber(number3);


            resultPrjProject.setUsedNumberFlag("N");
        }
        resultPrjProject.setProjectId(null);
        resultPrjProject.setDataClass("VIRTUAL_CON");
        resultPrjProject.setDataType("NORMAL");
        resultPrjProject.setRefProjectId(hlsCusPrjProject.getRefProjectId());
        resultPrjProject.setCompanyId(prjProject.getCompanyId());
        //插入编号
        resultPrjProject.setLeasingNumber(this.selectLeasingNumber(resultPrjProject.getCompanyId()));

        String approvalNumber = hlsCusPrjProjectMapper.queryApprovalNumber(prjProject.getRefProjectId());
        resultPrjProject.setApprovalNumber(approvalNumber);
        resultPrjProject = self().insertSelective(requestCtx, resultPrjProject);

        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setProjectId(resultPrjProject.getRefProjectId().toString());
        List<PrjProjectApproval> prjProjectApprovals = prjProjectApprovalMapper.conQueryAll(prjProjectApproval);
        if (prjProjectApprovals.size() > 0) {

            BeanRefUtils.beanToBean(prjProjectApprovals.get(0), prjProjectApproval, hlsBeanRefUtilService);
            prjProjectApproval.setProjectId(resultPrjProject.getProjectId().toString());
            projectApprovalService.insertSelective(requestCtx, prjProjectApproval);
        }

        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(prjProject.getProjectId());
        List<HlsCusPrjProjectBp> projectBps = hlsCusPrjProjectBpService.selectSelective(requestCtx, hlsCusPrjProjectBp);
        for (HlsCusPrjProjectBp projectBp : projectBps) {
            hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
            hlsCusPrjProjectBp.setProjectId(resultPrjProject.getProjectId());
            hlsCusPrjProjectBp.setBpCategroy("CON_CONTRACT");
            hlsCusPrjProjectBp.setBpType(projectBp.getBpType());
            hlsCusPrjProjectBp.setBpId(projectBp.getBpId());
            hlsCusPrjProjectBpService.insertSelective(requestCtx, hlsCusPrjProjectBp);
        }
//        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
//        hlsCusPrjProjectBp.setProjectId(resultPrjProject.getProjectId());
//        hlsCusPrjProjectBp.setBpCategroy("CON_CONTRACT");
//        hlsCusPrjProjectBp.setBpType("TENANT");
//        hlsCusPrjProjectBp.setBpId(resultPrjProject.getTenantId());
//        hlsCusPrjProjectBpService.insertSelective(requestCtx, hlsCusPrjProjectBp);

        return resultPrjProject;
    }

    /**
     * 厂商起租规则定义
     *
     * @param hlsBpMaster
     */
    @Override
    public List<HlsBpMasterInceptRule> queryManufacturerInceptType(HlsCusBpMaster hlsBpMaster) {
        return hlsBpMasterInceptRuleMapper.query(hlsBpMaster.getBpId());
    }

    /**
     * 进件资料补充
     */
    @Override
    public List<Map> prjSupplementQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsCusPrjProjectMapper.prjSupplementQuery(project);
    }

    /**
     * 额度增量申请
     */
    @Override
    public HlsCusPrjProject projectAmountAllocateCreate(IRequest request, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {

        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(hlsCusPrjProject.getProjectId());

        //校验项目状态
        project = hlsCusPrjProjectService.selectByPrimaryKey(request, project);
        if ("NEW".equals(project.getProjectStatus()) || "REJECTED".equals(project.getProjectStatus())) {
            throw new HlsCusException("当前项目无需进行变更,可直接维护信息!");
        }
        if ("APPROVING".equals(project.getProjectStatus())) {
            throw new HlsCusException("当前项目正在审批中，无法进行变更!");
        }

        /*单据状态置为挂起*/
        project.setProjectStatus("PENDING");
        project.setMeetingStatus("PENDING");
        project.set__status("update");

        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, project);

        /*插入审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setDocumentId(project.getProjectId());
        hlsCusChangeReqInfo.setDocumentCategory("PRJ_PROJECT");
        List<HlsCusChangeReqInfo> hlsCusChangeReqInfoList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);

        hlsCusChangeReqInfo.setStatus("NEW");
        List<HlsCusChangeReqInfo> newChangeReqList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);
        if (newChangeReqList != null && newChangeReqList.size() > 0) {
            throw new HlsCusException("该项目正在变更中!");
        }

        if (hlsCusChangeReqInfoList.size() > 0) {
            hlsCusChangeReqInfoList.stream().filter(item -> null != (item.getChangeReqNumber()) && PRJ_AMOUNT_ALLOCATE.equals(item.getChangeType()))
                    .sorted(Comparator.comparing(HlsCusChangeReqInfo::getChangeReqNumber).reversed());
        }
        Long number = hlsCusChangeReqInfoList.size() + ONE;
        String changeReqNumber = project.getProjectNumber() + "-" + number;
        hlsCusChangeReqInfo.setChangeReqNumber(changeReqNumber);
        hlsCusChangeReqInfo.setDocumentVersionId(hlsCusChangeReqInfoList.size() + ONE);
        hlsCusChangeReqInfo.setChangeReqUserId(request.getUserId());
        hlsCusChangeReqInfo.setChangeDescription(hlsCusPrjProject.getChangeDescription());
        hlsCusChangeReqInfo.setChangeReqDate(new Date());
        hlsCusChangeReqInfo.setChangeType(PRJ_AMOUNT_ALLOCATE);
        hlsCusChangeReqInfo.setApproveNumber(project.getProjectNumber());
        hlsCusChangeReqInfo.setChangeInfoDesc("");
        hlsCusChangeReqInfo.setChangeEffctDesc("");
        //创建时设置为N，流程结束后再修改为Y
        hlsCusChangeReqInfo.setInstanceEndFlag("N");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.insertSelective(request, hlsCusChangeReqInfo);

        /*复制当前项目，创建变更数据*/
        hlsCusPrjProject = selectByPrimaryKey(request, hlsCusPrjProject);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjProject);
        hlsBeanRefUtilService.setFieldValue(prjProject, map1);
        prjProject.setRefProjectId(prjProject.getProjectId());
        prjProject.setProjectId(null);
        prjProject.setDataType("CHANGE_REQ");
        prjProject.setProjectStatus("APPROVED");
        prjProject.setMeetingStatus("APPROVED");
        prjProject.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());

        prjProject.setReviewComments(null);

        //排除上会信息
        prjProject.setApprovalStatus("");
        prjProject.setApprovalId(null);
        prjProject.setVoteComment("");
        prjProject.setVoteResult("");
        prjProject.setVoteStatus("");
        prjProject.setMeetingProcessInstanceId("");
        prjProject.setProcessInstanceId(null);
        //修改会议类型，便于上会时区分
        prjProject.setMeetingType(PRJ_AMOUNT_ALLOCATE);
        prjProject = hlsCusPrjProjectService.insertSelective(request, prjProject);

        //复制一份用作历史的数据, dataType:CHANGE_REQ_HISTORY, 在变更结束后改为HISTORY
        HlsCusPrjProject prjChanceH = new HlsCusPrjProject();
        hlsBeanRefUtilService.setFieldValue(prjChanceH, map1);
        prjChanceH.setRefProjectId(prjChanceH.getProjectId());
        prjChanceH.setProjectId(null);
        prjChanceH.setDataType("CHANGE_REQ_HISTORY");
        prjChanceH.setProjectStatus("APPROVED");
        prjChanceH.setMeetingStatus("APPROVED");
        prjChanceH.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        prjChanceH = self().insertSelective(request, prjChanceH);

        //插入从表（不复制附件）
        projectBackUp(request, prjProject, true);//变更单据会重新维护上会,不复制    方案更改,项目批复变更复用逻辑,需要复制上会信息
        projectBackUp(request, prjChanceH, true);//历史数据需要复制上会信息

        //查询打开链接需要的参数
        HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();
        cusPrjProject.setProjectId(prjProject.getProjectId());
        List<HlsCusPrjProject> list = hlsCusPrjProjectMapper.selectProjectChangeReqInfo(cusPrjProject);

        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
            cusPrjProject = list.get(0);
        }

        return cusPrjProject;
    }

    /**
     * 额度增量申请 提交审批
     */
    @Override
    public HlsCusPrjProject projectAmountAllocateSubmit(IRequest iRequest, HlsCusPrjProject prjProject) {
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        prjProject = self().selectByPrimaryKey(iRequest, prjProject);
        hlsCusPrjProjectList.add(prjProject);
        /*修改审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        //hlsCusChangeReqInfo.setDocumentCategory("PRJ_PROJECT");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(iRequest, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus("APPROVING");
        hlsCusChangeReqInfo.setWflNodeStatus(null);
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.updateByPrimaryKey(iRequest, hlsCusChangeReqInfo);

        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CREDIT_TENANT_AMOUNT_WFL");
        params.put("wflKey", "CREDIT_TENANT_AMOUNT_WFL");
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

       /* prjProject.setProjectStatus("APPROVING");
        prjProject = self().updateByPrimaryKeySelective(iRequest, prjProject);*/
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjProject.getProjectName() + "的额度增量申请" + prjProject.getProjectNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "额度增量申请");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, prjProject.getProjectId(), prjProject.getDocumentCategory(), prjProject.getDocumentType(), "BAC", "PRJ_PROJECT_WFL", "P2D", paramsEvent);
        return prjProject;
    }
    public boolean regBlank(String value) {
        String pattern = "(.*)[\\s](.*)";
        boolean isMatch = Pattern.matches(pattern, value);
        return isMatch;
    }

    public boolean regEn(String value) {
        String pattern = "(.*)[`~!@#$%^&*()\\-_+=<>?:\"{}\\,.\\/;'\\[\\]](.*)";
        boolean isMatch = Pattern.matches(pattern, value);
        return isMatch;
    }
    public boolean regCn(String value) {
        String pattern = "(.*)[·！#￥（——）……：；“”‘、，|《。》？、【】\\[\\]](.*)";
        boolean isMatch = Pattern.matches(pattern, value);
        return isMatch;
    }

    @Override
    public void excelBatchImport(IRequest iRequest, Long headerId, String division) throws HlsCusException {
        if (headerId == null) {
            throw new HlsCusException(PARAM_NOT_FOUND);
        }

        //承租人&进件基本信息
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(headerId);
        fndInterfaceLines.setReadLine(READ_LINE);
        fndInterfaceLines.setSheetName(PROJECT_TENANT_SHEET);
        List<FndInterfaceLines> projectTenants = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);

        //承租人&进件基本信息不能为空
        if (CollectionUtils.isEmpty(projectTenants)) {
            throw new HlsCusException(new StringBuffer(PROJECT_TENANT_SHEET).append("不能为空！").toString());
        }

        //校验承租人&进件信息进件序号不能重复
        long count = projectTenants.stream().map(FndInterfaceLines::getAttributes_1).distinct().count();

        if (CollectionUtils.size(projectTenants) != count) {
            throw new HlsCusException(new StringBuffer(PROJECT_TENANT_SHEET).append("进件序号不允许重复！").toString());
        }


        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        for (int i = 0; i < projectTenants.size(); i++) {
            try {
                int row = Math.toIntExact(projectTenants.get(i).getLineNumber());
                PrjExcelImportDto prjSheetImportDto = setValueToExcelDto(PROJECT_TENANT, projectTenants.get(i),division);

                //商业伙伴数据处理
                hlsBpMaster = importHlsBpMasterInfo(iRequest, prjSheetImportDto, PROJECT_TENANT_SHEET, row, TENANT);
                if (BaseConstants.NO.equals(hlsBpMaster.getExistFlag())) {
                    //插入商业伙伴地址信息
                    importBpMasterAddress(iRequest, prjSheetImportDto, PROJECT_TENANT_SHEET, row, hlsBpMaster);
                    //法人
                    if (IPrjProjectService.ORG_DESC.equals(prjSheetImportDto.getBpClass())) {
                        //插入商业伙伴主要组成人员
                        importBpMasterMainMembers(prjSheetImportDto, hlsBpMaster);
                    }
                }

                //处理进件信息
                hlsCusPrjProject = importPrjProject(iRequest, prjSheetImportDto, PROJECT_TENANT_SHEET, row, hlsBpMaster.getBpId(),division);

                //处理报价信息
                prjQuotation = importPrjQuotation(iRequest, prjSheetImportDto, PROJECT_TENANT_SHEET, row);
                prjQuotation.setQuotationNumber(hlsCusPrjProject.getProjectNumber() + "-" + i);
                prjQuotation.setEnabledFlag(Y);
                prjQuotation.setSourceDocumentCategory(PROJECT_DOCUMENT_CATEGORY);
                prjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
                prjQuotation.setFloatingRangeMethod(hlsCusPrjProject.getFloatingRangeMethod());
                prjQuotation.setLeaseStartDate(hlsCusPrjProject.getLeaseStartDate());
                prjQuotationMapper.insert(prjQuotation);


                //生成承租人信息
                HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                hlsCusPrjProjectBp.setBpCategroy(TENANT);
                hlsCusPrjProjectBp.setBpClass(hlsBpMaster.getBpClass());
                hlsCusPrjProjectBp.setBpId(hlsBpMaster.getBpId());
                hlsCusPrjProjectBpMapper.insert(hlsCusPrjProjectBp);

                //租赁物&保险信息
                fndInterfaceLines.setSheetName(LEASE_INSURANCE_SHEET);
                fndInterfaceLines.setAttributes_1(projectTenants.get(i).getAttributes_1());
                List<FndInterfaceLines> leaseItemLines = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);

                BpMasterReply bpMasterReply = new BpMasterReply();
                bpMasterReply.setFactoryId(hlsCusPrjProject.getFactoryId());
                List<BpMasterReply> replyList = bpMasterReplyMapper.selectReplyParaInfo(bpMasterReply);
                Boolean insuranceFlag = false;
                Double insuranceFreeAmount = 0D;
                if (CollectionUtils.isNotEmpty(replyList) && replyList.size() == 1) {
                    insuranceFreeAmount = replyList.get(0).getInsuranceFreeAmount();
                }

                //进件保险购买情况，调息规则
                if (CollectionUtils.isEmpty(replyList)) {
                    //保险购买情况改造
                    //hlsCusPrjProject.setInsuranceFlag(BaseConstants.NO);
                } else {
                    hlsCusPrjProject.setFloatingRangeMethod(bpMasterReply.getFloatingRangeMethod());

                    prjQuotation.setFloatingRangeMethod(bpMasterReply.getFloatingRangeMethod());
                    prjQuotation.setObjectVersionNumber(null);
                    prjQuotationMapper.updateByPrimaryKeySelective(prjQuotation);
                }

                hlsCusPrjProject.setObjectVersionNumber(null);
                //是否提前起租
                Long manufacturerId = hlsCusPrjProject.getManufacturerId();
                HlsBpMasterInceptRule hlsBpMasterInceptRuleQuery = new HlsBpMasterInceptRule();
                hlsBpMasterInceptRuleQuery.setBpId(manufacturerId);
                hlsBpMasterInceptRuleQuery.setEnabledFlag(BaseConstants.YES);
                hlsBpMasterInceptRuleQuery.setSortname("fixedDay");
                hlsBpMasterInceptRuleQuery.setSortorder("ASC");
                List<HlsBpMasterInceptRule> hlsBpMasterInceptRuleList = iHlsBpMasterInceptRuleService.select(iRequest, hlsBpMasterInceptRuleQuery, 0, 0);

                String updateType = "";

                if (hlsBpMasterInceptRuleList.size() == 0) {
                    //未定义起租规则 固定为投放日当月15号
                    updateType = HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY;
                } else {
                    HlsBpMasterInceptRule hlsBpMasterInceptRule0 = hlsBpMasterInceptRuleList.get(0);
                    String inceptRuleType0 = hlsBpMasterInceptRule0.getInceptRuleType();
                    if (HlsConstantUtil.HlsBpMasterInceptRule.LOAN_DATE.equals(inceptRuleType0)) {
                        updateType = HlsConstantUtil.HlsBpMasterInceptRule.LOAN_DATE;
                    } else if (HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY.equals(inceptRuleType0)) {
                        updateType = HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY;
                    }
                }
                if (StringUtils.equals(updateType, HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY) && StringUtils.equals(hlsCusPrjProject.getLeaseChannel(), "10")) {
                    if (StringUtils.isEmpty(prjSheetImportDto.getAppointInceptFlag())) {
                        throw new HlsCusException(new StringBuffer("第").append(row).append("行是否提前起租字段必输").toString());
                    } else {
//                        if (StringUtils.equals(prjSheetImportDto.getAppointInceptFlag(), "是")) {
//                            hlsCusPrjProject.setAppointInceptFlag(Y);
//                        } else {
//                            hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
//                        }
                    }
                } else if (StringUtils.equals(updateType, HlsConstantUtil.HlsBpMasterInceptRule.LOAN_DATE) && StringUtils.equals(hlsCusPrjProject.getLeaseChannel(), "10")) {
                    if (StringUtils.isEmpty(prjSheetImportDto.getAppointInceptFlag())) {
//                        hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
                    } else {
                        if (StringUtils.equals(prjSheetImportDto.getAppointInceptFlag(), "是")) {
                            throw new HlsCusException(new StringBuffer("第").append(row).append("行起租规则为“投放即起租时”，不允许提前起租").toString());
                        } else {
//                            hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
                        }
                    }
                } else {
//                    hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
                }
                prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);

                //担保人
                fndInterfaceLines.setSheetName(GUARANTOR_SHEET);
                List<FndInterfaceLines> guarantorList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
                for (int k = 0; k < guarantorList.size(); k++) {
                    int guarantorRow = Math.toIntExact(guarantorList.get(k).getLineNumber());
                    hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                    PrjExcelImportDto guarantorImportDto = setValueToExcelDto(GUARANTOR_SHEET, guarantorList.get(k),division);
                    HlsCusBpMaster guarantor = importGuarantorInfo(iRequest, guarantorImportDto, GUARANTOR_SHEET, guarantorRow, GUARANTOR);
                    //生成担保人信息
                    hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                    hlsCusPrjProjectBp.setBpCategroy(GUARANTOR);
                    hlsCusPrjProjectBp.setBpId(guarantor.getBpId());
                    hlsCusPrjProjectBp.setBpClass(guarantor.getBpClass());
//                    hlsCusPrjProjectBp.setRefV03(guarantor.getRefV03());
//                    hlsCusPrjProjectBp.setRefV02(guarantor.getRefV02());
//                    hlsCusPrjProjectBp.setRefV04(guarantor.getRefV04());
//                    hlsCusPrjProjectBp.setRefV05(guarantor.getRefV05());
                    hlsCusPrjProjectBpMapper.insert(hlsCusPrjProjectBp);
                }


                //进件导入后直接生成签约附件清单
                hlsCusPrjProjectService.generateProjectSignAttach(iRequest, hlsCusPrjProject.getProjectId());

            } catch (HlsCusException e) {

                interfaceErrorMsgService.insertInterfaceErrorMessage(iRequest, headerId, PRJ_PROJECT, e.getMessage());
                continue;
            }
        }
    }

    //处理担保人信息
    HlsCusBpMaster importGuarantorInfo(IRequest iRequest, PrjExcelImportDto excelInfo, String sheetName, int i, String type) throws HlsCusException {
        HashMap param = new HashMap();
        //非空校验
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setExistFlag(BaseConstants.NO);
        //进件序号(必填)
        if (StringUtils.isEmpty(excelInfo.getProjectNumber())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 1, "进件序号不能为空!"));
        }

        //担保人名称
        if (StringUtil.isEmpty(excelInfo.getBpName())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 2, IPrjProjectService.NOT_NULL));
        } else {
            if (regBlank(excelInfo.getBpName())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 2, "含有非法字符（包括空格），请检查!"));
            }
            hlsBpMaster.setBpName(excelInfo.getBpName());
        }

        //担保人简称（法人）(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getShortName())) {
            if (regBlank(excelInfo.getShortName())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 3, "含有非法字符（包括空格），请检查!"));
            }
        }
        //担保人类别不能为空
        if (StringUtil.isEmpty(excelInfo.getBpClass())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 4, TENANT_TYPE_NOT_NULL));
        } else {
            if (!StringUtils.equals(excelInfo.getBpClass(), IPrjProjectService.NP_DESC) && !StringUtils.equals(excelInfo.getBpClass(), IPrjProjectService.ORG_DESC)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 4, "担保人类别描述有误!"));
            }
        }
        //证件类型不能为空
        if (StringUtil.isEmpty(excelInfo.getIdType())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 5, IPrjProjectService.CARD_TYPE_NOT_NULL));
        }
        //证件号码(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getIdCardNo())) {
            if (regBlank(excelInfo.getIdCardNo())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, "含有非法字符（包括空格），请检查!"));
            }
        }

        //配偶证件号码(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getIdCardNoSp())) {
            if (regBlank(excelInfo.getIdCardNoSp())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 12, "含有非法字符（包括空格），请检查!"));
            }
        }

        //查询当前数据库中是否存在对应商业伙伴信息，若存在，则直接使用系统中存在的商业伙伴，如果查不到，则新建一个商业伙伴
        //如果是自然人，则根据身份证号进行查询
        if (IPrjProjectService.NP_DESC.equals(excelInfo.getBpClass())) {
            hlsBpMaster.setBpClass(IPrjProjectService.NP);
            String value =  codeValueMapper.selectCodeValuesByCodeNameAndValue("HLS211_ID_TYPE", excelInfo.getIdType());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "证件类型描述有误!"));
            }
            hlsBpMaster.setIdType(value);
            //校验身份证合法性
            if (!IDUtils.isValidatedAllIdcard(excelInfo.getIdCardNo())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, NP_ID_CARD_EXCEPTION));
            }
            hlsBpMaster.setIdCardNo(excelInfo.getIdCardNo());
        }
        //法人
        if (IPrjProjectService.ORG_DESC.equals(excelInfo.getBpClass())) {
            hlsBpMaster.setBpClass(IPrjProjectService.ORG);
            //设置证件类型
            String cardType = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.ZX_REGNOTYPE, excelInfo.getIdType());
            if (StringUtils.isEmpty(cardType)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "证件类型描述有误!"));
            }
            hlsBpMaster.setRegnotype(cardType);

            if (StringUtils.length(excelInfo.getIdCardNo()) != 15 && StringUtils.length(excelInfo.getIdCardNo()) != 18) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, "证件号码有误！"));
            }
            //证件类型为营业执照号
            if (IPrjProjectService.BUSINESS_LICENSE_DESC.equals(excelInfo.getIdType())) {
                hlsBpMaster.setBusinessLicenseNum(excelInfo.getIdCardNo());
            }
            //证件类型为统一社会信用代码
            if (IPrjProjectService.UNIFIED_SOCIAL_CREDIT.equals(excelInfo.getIdType())) {
                hlsBpMaster.setRegno(excelInfo.getIdCardNo());
            }
        }
        List<HlsCusBpMaster> masterList = hlsBpMasterMapper.select(hlsBpMaster);

        if (IPrjProjectService.NP_DESC.equals(excelInfo.getBpClass())) {
            try {
                IDUtils.Person person = IDUtils.getBirAgeSex(excelInfo.getIdCardNo());
                hlsBpMaster.setDateOfBirth(person.getBirthDate());
                hlsBpMaster.setAge(person.getAge());
                hlsBpMaster.setGender(person.getSex());
            } catch (ParseException e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, NP_ID_CARD_EXCEPTION));
            }
        }

        //查找到对应的商业伙伴数据
        if (CollectionUtils.isNotEmpty(masterList)) {
            if (masterList.size() > 1) {
                throw new HlsCusException(sheetName + "第" + i + "行查询到多个商业伙伴,请检查数据!");
            }
            hlsBpMaster = masterList.get(0);
            hlsBpMaster.setExistFlag(Y);
            if (IPrjProjectService.NP_DESC.equals(excelInfo.getBpClass())) {
                //担保人联系方式
                if (StringUtil.isEmpty(excelInfo.getDealerTel())) {
                    throw new HlsCusException(new StringBuffer(sheetName).append("第").append("20").append("列").append(DEALER_TEL_EXCEPTION).toString());
                } else {
                    String regExp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                    String regExp1 = "^(1)\\d{10}$";
                    Pattern p = Pattern.compile(regExp);
                    Pattern p1 = Pattern.compile(regExp1);
                    Matcher m = p.matcher(excelInfo.getDealerTel());
                    Matcher m1 = p1.matcher(excelInfo.getDealerTel());
                    if(m.matches() || m1.matches()) {
                        hlsBpMaster.setCellPhone(excelInfo.getDealerTel());
                    }else{
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 20, "担保人联系方式不符合规范，请检查！"));
                    }
                }
            }else if (IPrjProjectService.ORG_DESC.equals(excelInfo.getBpClass())) {
                if(StringUtil.isNotEmpty(excelInfo.getDealerTel())){
                    String regExp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                    String regExp1 = "^(1)\\d{10}$";
                    Pattern p = Pattern.compile(regExp);
                    Pattern p1 = Pattern.compile(regExp1);
                    Matcher m = p.matcher(excelInfo.getDealerTel());
                    Matcher m1 = p1.matcher(excelInfo.getDealerTel());
                    if(m.matches() || m1.matches()) {
                        hlsBpMaster.setCellPhone(excelInfo.getDealerTel());
                    }else{
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 20, "担保人联系方式不符合规范，请检查！"));
                    }
                }
            }
            hlsBpMasterService.updateByPrimaryKeySelective(iRequest, hlsBpMaster);
        } else {
            //共有数据
            hlsBpMaster.setOwnerUserId(iRequest.getUserId());
            hlsBpMaster.setBpCategory(type);
            hlsBpMaster.setBpType(type);
            hlsBpMaster.setEnabledFlag(Y);
            //国别/国家默认中国
            hlsBpMaster.setNationality("46");
            //自然人
            if (IPrjProjectService.NP.equals(hlsBpMaster.getBpClass())) {

                //设置证件类型
                String cardType = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdType());
                hlsBpMaster.setIdType(cardType);

                //担保人联系方式
                if (StringUtil.isEmpty(excelInfo.getDealerTel())) {
                    throw new HlsCusException(new StringBuffer(sheetName).append("第").append("20").append("列").append(DEALER_TEL_EXCEPTION).toString());
                } else {
                    String regExp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                    String regExp1 = "^(1)\\d{10}$";
                    Pattern p = Pattern.compile(regExp);
                    Pattern p1 = Pattern.compile(regExp1);
                    Matcher m = p.matcher(excelInfo.getDealerTel());
                    Matcher m1 = p1.matcher(excelInfo.getDealerTel());
                    if(m.matches() || m1.matches()) {
                        hlsBpMaster.setCellPhone(excelInfo.getDealerTel());
                    }else{
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 20, "担保人联系方式不符合规范，请检查！"));
                    }
                }

                //性别
                if (StringUtil.isEmpty(excelInfo.getGenger())) {
                    throw new HlsCusException(IPrjProjectService.GENDER_NOT_NULL);
                } else {
                    String gender = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.GENDER_SYS_CODE, excelInfo.getGenger());
                    if (StringUtils.isEmpty(gender)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 7, "性别描述有误！"));
                    }
                    hlsBpMaster.setGender(gender);
                }
                //婚姻状况
                if (StringUtil.isEmpty(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 8, IPrjProjectService.NOT_NULL));
                } else {
                    String maritalStatus = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.MARITAL_STATUS_SYS_CODE, excelInfo.getMaritalStatus());
                    if (StringUtils.isEmpty(maritalStatus)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 8, "描述有误！"));
                    }
                    hlsBpMaster.setMaritalStatus(maritalStatus);
                }
                //学历
                if (StringUtil.isEmpty(excelInfo.getAcademicBackground())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "学历不能为空!"));
                } else {
                    String academicBackground = codeValueMapper.selectCodeValuesByCodeNameAndValue("HLS211_ACADEMIC_BACKGROUND", excelInfo.getAcademicBackground());
                    if (StringUtils.isEmpty(academicBackground)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
                    }
                    hlsBpMaster.setAcademicBackground(academicBackground);
                }
                //配偶姓名
                if (StringUtil.isNotEmpty(excelInfo.getBpNameSp())) {
                    hlsBpMaster.setBpNameSp(excelInfo.getBpNameSp());
                    if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 10, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                    }
                } else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 10, IPrjProjectService.NOT_NULL));
                }

                //配偶证件类型
                if (StringUtil.isNotEmpty(excelInfo.getIdTypeSp())) {
                    String idTypeSp = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdTypeSp());
                    if (StringUtils.isEmpty(idTypeSp)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 11, "描述有误！"));
                    }
                    if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 11, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                    }
                    hlsBpMaster.setIdTypeSp(idTypeSp);
                } else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 11, IPrjProjectService.NOT_NULL));
                }

                //配偶证件号码
                if (StringUtil.isNotEmpty(excelInfo.getIdCardNoSp())) {
                    if (ID_CARD.equals(hlsBpMaster.getIdTypeSp())) {
                        if (IDUtils.isValidatedAllIdcard(excelInfo.getIdCardNoSp())) {
                            hlsBpMaster.setIdCardNoSp(excelInfo.getIdCardNoSp());
                            try {
                                IDUtils.Person person = IDUtils.getBirAgeSex(excelInfo.getIdCardNoSp());
                                hlsBpMaster.setDateOfBirthSp(person.getBirthDate());
                                hlsBpMaster.setGenderSp(person.getSex());
                                hlsBpMaster.setAgeSp(person.getAge());
                            } catch (ParseException e) {
                                throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(NP_ID_CARD_EXCEPTION).toString());
                            }
                        } else {
                            throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(ID_CARD_EXCEPTION).toString());
                        }
                    }
                    if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 12, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                    }
                } else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 12, IPrjProjectService.NOT_NULL));
                }
            } else {
                //法人
                //登记注册号类型 默认统一社会信用代码
                hlsBpMaster.setRegnotype("07");

                //担保人联系方式
                if(StringUtil.isNotEmpty(excelInfo.getDealerTel())){
                    String regExp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                    String regExp1 = "^(1)\\d{10}$";
                    Pattern p = Pattern.compile(regExp);
                    Pattern p1 = Pattern.compile(regExp1);
                    Matcher m = p.matcher(excelInfo.getDealerTel());
                    Matcher m1 = p1.matcher(excelInfo.getDealerTel());
                    if(m.matches() || m1.matches()) {
                        hlsBpMaster.setCellPhone(excelInfo.getDealerTel());
                    }else{
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 20, "担保人联系方式不符合规范，请检查！"));
                    }
                }

                //承租人简称
                if (StringUtil.isNotEmpty(excelInfo.getShortName())) {
                    hlsBpMaster.setShortName(excelInfo.getShortName());
                }
                //实际控制人
                if (StringUtil.isNotEmpty(excelInfo.getActualController())) {
                    hlsBpMaster.setActualController(excelInfo.getActualController());
                }
                //法定代表人
                if (StringUtil.isNotEmpty(excelInfo.getLegalPerson())) {
                    hlsBpMaster.setLegalPerson(excelInfo.getLegalPerson());
                }
                //注册时间
                if (StringUtil.isNotEmpty(excelInfo.getFoundedDate())) {
                    try {
                        hlsBpMaster.setFoundedDate(simpleDateFormat.parse(excelInfo.getFoundedDate()));
                    } catch (ParseException e) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 16, "格式错误！"));
                    }
                }
            }
            //担保类型
            if (StringUtil.isNotEmpty(excelInfo.getDealerType())) {
                String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.GUARANTOR_TYPE, excelInfo.getDealerType());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 17, "描述有误！"));
                }
                hlsBpMaster.setRefV03(value);
            } else {
                throw new HlsCusException(getExceptionInfo(GUARANTOR_SHEET, i, 17, IPrjProjectService.NOT_NULL));
            }
            //与承租人关系
            if (StringUtil.isNotEmpty(excelInfo.getDealerRelation())) {
                String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.ZX_MEMBERTYPE, excelInfo.getDealerRelation());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 18, "描述有误！"));
                }
                hlsBpMaster.setRefV02(value);
            } else {
                throw new HlsCusException(getExceptionInfo(GUARANTOR_SHEET, i, 18, IPrjProjectService.NOT_NULL));
            }

            //生成商业伙伴编码
            String bpClass = hlsBpMaster.getBpClass();
            String bp_code = fndCodingRuleValuesService.getCodeRuleValue(iRequest, HLS_BP_DOCUMENT_CATEGORY, hlsBpMaster.getBpClass(), hlsBpMaster.getBpClass(), param);
            hlsBpMaster.setBpCode(bp_code);
            String authorityRuleString = hlsBpMasterService.getAuthorityString(iRequest);
            hlsBpMaster.setAuthorityRuleString(authorityRuleString);
            hlsBpMasterService.insert(iRequest, hlsBpMaster);

            //进件导入时新建承租人或担保人时，若类别为法人，登记注册号类型和登记注册号码需往zx_bp_orgbase表中插值，对应字段同样为REGNOTYPE、REGNO（影响变更）
            if (ORG.equals(hlsBpMaster.getBpClass())) {
                ZxBpOrgbase zxBpOrgbase = new ZxBpOrgbase();
                zxBpOrgbase.setBpId(hlsBpMaster.getBpId());
                zxBpOrgbase.setRegnotype(hlsBpMaster.getRegnotype());
                zxBpOrgbase.setRegno(hlsBpMaster.getRegno());
                zxBpOrgbaseMapper.insertSelective(zxBpOrgbase);
            }
            //插入商业伙伴角色信息
            HlsBpMasterRole hlsBpMasterRole = new HlsBpMasterRole();
            hlsBpMasterRole.setBpId(hlsBpMaster.getBpId());
            hlsBpMasterRole.setBpCategory(type);
            hlsBpMasterRole.setBpType(type);
            hlsBpMasterRole.setPrimaryFlag(Y);
            hlsBpMasterRole.setEnabledFlag(Y);
            hlsBpMasterRoleMapper.insertSelective(hlsBpMasterRole);
        }

        //担保人签约方式
        if (StringUtil.isNotEmpty(excelInfo.getGuarantorSignType())) {
            String signType = codeValueMapper.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", excelInfo.getGuarantorSignType());
            if (StringUtils.isEmpty(signType)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 19, "描述有误！"));
            }
            hlsBpMaster.setRefV03(signType);
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 19, IPrjProjectService.SIGN_TYPE_NOT_NULL));
        }

        return hlsBpMaster;
    }

    //处理商业伙伴信息
    HlsCusBpMaster importHlsBpMasterInfo(IRequest iRequest, PrjExcelImportDto excelInfo, String sheetName, int i, String type) throws HlsCusException {
        HashMap param = new HashMap();
        //非空校验
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setExistFlag(BaseConstants.NO);
        //进件序号(必填)
        if (StringUtils.isEmpty(excelInfo.getProjectNumber())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 1, "进件序号不能为空!"));
        }

        //承租人名称(不允许包含空格)
        if (StringUtil.isEmpty(excelInfo.getBpName())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 2, IPrjProjectService.NOT_NULL));
        } else {
            if (regBlank(excelInfo.getBpName())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 2, "含有非法字符（包括空格），请检查!"));
            }
        }

        //承租人简称（法人）(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getShortName())) {
            if (regBlank(excelInfo.getShortName())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 3, "含有非法字符（包括空格），请检查!"));
            }
        }
        //承租人类别不能为空
        if (StringUtil.isEmpty(excelInfo.getBpClass())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 4, TENANT_TYPE_NOT_NULL));
        } else {
            if (!StringUtils.equals(excelInfo.getBpClass(), IPrjProjectService.NP_DESC) && !StringUtils.equals(excelInfo.getBpClass(), IPrjProjectService.ORG_DESC)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 4, "承租人类别描述有误!"));
            }
        }
        //证件类型不能为空
        if (StringUtil.isEmpty(excelInfo.getIdType())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 5, IPrjProjectService.CARD_TYPE_NOT_NULL));
        }
        //证件号码(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getIdCardNo())) {
            if (regBlank(excelInfo.getIdCardNo())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, "含有非法字符（包括空格），请检查!"));
            }
        }

        //配偶证件号码(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getIdCardNoSp())) {
            if (regBlank(excelInfo.getIdCardNoSp())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 13, "含有非法字符（包括空格），请检查!"));
            }
        }

        //详细地址(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getAddress())) {
            if (regBlank(excelInfo.getAddress())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 24, "含有非法字符（包括空格），请检查!"));
            }
        }
        //电话(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getPhone())) {
            if (regBlank(excelInfo.getPhone()) || regEn(excelInfo.getPhone()) || regCn(excelInfo.getPhone())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 25, "含有非法字符（包括空格），请检查!"));
            }
        }

        //手机(不允许包含空格)
        if (StringUtils.isNotEmpty(excelInfo.getMobilePhone())) {
//            if (regBlank(excelInfo.getMobilePhone()) || regEn(excelInfo.getMobilePhone()) || regCn(excelInfo.getMobilePhone())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 26, "含有非法字符（包括空格），请检查!"));
//            }
            String regExp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
            String regExp1 = "^(1)\\d{10}$";
            Pattern p = Pattern.compile(regExp);
            Pattern p1 = Pattern.compile(regExp1);
            Matcher m = p.matcher(excelInfo.getMobilePhone());
            Matcher m1 = p1.matcher(excelInfo.getMobilePhone());
            if(!(m1.matches() || m.matches())){
                throw new HlsCusException(getExceptionInfo(sheetName, i, 26, "联系方式不符合规范，请检查!"));
            }
        }

        //查询当前数据库中是否存在对应商业伙伴信息，若存在，则直接使用系统中存在的商业伙伴，如果查不到，则新建一个商业伙伴
        //如果是自然人，则根据身份证号进行查询
        HlsCusBpMaster hlsBpMasterExample = new HlsCusBpMaster();
        if (IPrjProjectService.NP_DESC.equals(excelInfo.getBpClass())) {
            hlsBpMaster.setBpClass(IPrjProjectService.NP);
            hlsBpMasterExample.setBpClass(IPrjProjectService.NP);
            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue("HLS211_ID_TYPE", excelInfo.getIdType());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "证件类型描述有误!"));
            }
            hlsBpMaster.setIdType(value);
            hlsBpMasterExample.setIdType(value);
            //校验身份证合法性
            if (!IDUtils.isValidatedAllIdcard(excelInfo.getIdCardNo())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, NP_ID_CARD_EXCEPTION));
            }
            hlsBpMaster.setIdCardNo(excelInfo.getIdCardNo());
            hlsBpMasterExample.setIdCardNo(excelInfo.getIdCardNo());
        }
        //法人
        if (IPrjProjectService.ORG_DESC.equals(excelInfo.getBpClass())) {
            hlsBpMaster.setBpClass(IPrjProjectService.ORG);
            //设置证件类型
            String cardType = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.ZX_REGNOTYPE, excelInfo.getIdType());
            if (StringUtils.isEmpty(cardType)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "证件类型描述有误!"));
            }
            hlsBpMaster.setRegnotype(cardType);
            hlsBpMasterExample.setRegnotype(cardType);
            if (StringUtils.length(excelInfo.getIdCardNo()) != 15 && StringUtils.length(excelInfo.getIdCardNo()) != 18) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, "证件号码有误！"));
            }
            //证件类型为营业执照号
            if (IPrjProjectService.BUSINESS_LICENSE_DESC.equals(excelInfo.getIdType())) {
                hlsBpMaster.setBusinessLicenseNum(excelInfo.getIdCardNo());
                hlsBpMasterExample.setBusinessLicenseNum(excelInfo.getIdCardNo());
            }
            //证件类型为统一社会信用代码
            if (IPrjProjectService.UNIFIED_SOCIAL_CREDIT.equals(excelInfo.getIdType())) {
                hlsBpMaster.setRegno(excelInfo.getIdCardNo());
                hlsBpMasterExample.setRegno(excelInfo.getIdCardNo());
            }
        }
        List<HlsCusBpMaster> masterList = hlsBpMasterMapper.select(hlsBpMasterExample);

        if (IPrjProjectService.NP_DESC.equals(excelInfo.getBpClass())) {
            try {
                IDUtils.Person person = IDUtils.getBirAgeSex(excelInfo.getIdCardNo());
                hlsBpMaster.setDateOfBirth(person.getBirthDate());
                hlsBpMaster.setAge(person.getAge());
                hlsBpMaster.setGender(person.getSex());
            } catch (ParseException e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, NP_ID_CARD_EXCEPTION));
            }
        }
        //查找到对应的商业伙伴数据
        if (CollectionUtils.isNotEmpty(masterList)) {
            if (masterList.size() > 1) {
                throw new HlsCusException(sheetName + "第" + i + "行查询到多个商业伙伴,请检查数据!");
            }
            hlsBpMaster = masterList.get(0);

            hlsBpMaster.setExistFlag(Y);
        }

        //共有数据
        hlsBpMaster.setBpName(excelInfo.getBpName());
        hlsBpMaster.setOwnerUserId(iRequest.getUserId());
        hlsBpMaster.setBpCategory(type);
        hlsBpMaster.setBpType(type);
        hlsBpMaster.setEnabledFlag(Y);
        //国别/国家默认中国
        hlsBpMaster.setNationality("46");
        //自然人
        if (IPrjProjectService.NP.equals(hlsBpMaster.getBpClass())) {
            //设置证件类型
            String cardType = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdType());
            hlsBpMaster.setIdType(cardType);

            //性别
            if (StringUtil.isEmpty(excelInfo.getGenger())) {
                throw new HlsCusException(IPrjProjectService.GENDER_NOT_NULL);
            } else {
                String gender = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.GENDER_SYS_CODE, excelInfo.getGenger());
                if (StringUtils.isEmpty(gender)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 7, "性别描述有误！"));
                }
                hlsBpMaster.setGender(gender);
            }
            //婚姻状况
            if (StringUtil.isEmpty(excelInfo.getMaritalStatus())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 8, IPrjProjectService.NOT_NULL));
            } else {
                String maritalStatus = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.MARITAL_STATUS_SYS_CODE, excelInfo.getMaritalStatus());
                if (StringUtils.isEmpty(maritalStatus)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 8, "描述有误！"));
                }
                hlsBpMaster.setMaritalStatus(maritalStatus);
            }

            //学历
            if (StringUtil.isEmpty(excelInfo.getAcademicBackground())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "学历不能为空!"));
            } else {
                String academicBackground = codeValueMapper.selectCodeValuesByCodeNameAndValue("HLS211_ACADEMIC_BACKGROUND", excelInfo.getAcademicBackground());
                if (StringUtils.isEmpty(academicBackground)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
                }
                hlsBpMaster.setAcademicBackground(academicBackground);
            }
            //学位(必填)
            if (StringUtils.isEmpty(excelInfo.getAcademicDegree())) {
                throw new HlsCusException(getExceptionInfo(PROJECT_TENANT_SHEET, i, 10, "学位不能为空!"));
            } else {
                String academicBackground = codeValueMapper.selectCodeValuesByCodeNameAndValue("ACADEMIC_DEGREE", excelInfo.getAcademicDegree());
                if (StringUtils.isEmpty(academicBackground)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 10, "描述有误！"));
                }
                hlsBpMaster.setAcademicDegree(academicBackground);
            }
            //配偶姓名
            if (StringUtil.isNotEmpty(excelInfo.getBpNameSp())) {
                hlsBpMaster.setBpNameSp(excelInfo.getBpNameSp());
                if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 11, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                }
            } else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 11, IPrjProjectService.NOT_NULL));
            }

            //配偶证件类型
            if (StringUtil.isNotEmpty(excelInfo.getIdTypeSp())) {
                String idTypeSp = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdTypeSp());
                if (StringUtils.isEmpty(idTypeSp)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 12, "描述有误！"));
                }
                if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 12, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                }
                hlsBpMaster.setIdTypeSp(idTypeSp);
            } else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 12, IPrjProjectService.NOT_NULL));
            }

            //配偶证件号码
            if (StringUtil.isNotEmpty(excelInfo.getIdCardNoSp())) {
                if (ID_CARD.equals(hlsBpMaster.getIdTypeSp())) {
                    if (IDUtils.isValidatedAllIdcard(excelInfo.getIdCardNoSp())) {
                        hlsBpMaster.setIdCardNoSp(excelInfo.getIdCardNoSp());
                        try {
                            IDUtils.Person person = IDUtils.getBirAgeSex(excelInfo.getIdCardNoSp());
                            hlsBpMaster.setDateOfBirthSp(person.getBirthDate());
                            hlsBpMaster.setGenderSp(person.getSex());
                            hlsBpMaster.setAgeSp(person.getAge());
                        } catch (ParseException e) {
                            throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(NP_ID_CARD_EXCEPTION).toString());
                        }
                    } else {
                        throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(ID_CARD_EXCEPTION).toString());
                    }
                }
                if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 13, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                }
            } else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 13, IPrjProjectService.NOT_NULL));
            }

            //手机号码
            if (StringUtils.isNotEmpty(excelInfo.getMobilePhone())) {
                hlsBpMaster.setCellPhone(excelInfo.getMobilePhone());
            }

            //居住状况
            if (StringUtil.isNotEmpty(excelInfo.getHouseProperty())) {
                String houseProperty = codeValueMapper.selectCodeValuesByCodeNameAndValue("BP.HOUSE_PROPERTY", excelInfo.getHouseProperty());
                if (StringUtils.isEmpty(houseProperty)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 19, "描述有误！"));
                }
                hlsBpMaster.setHouseProperty(houseProperty);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 19, IPrjProjectService.NOT_NULL));
            }
            //就业状况
            if (StringUtil.isNotEmpty(excelInfo.getEmploymentStatus())) {
                String employmentStatus = codeValueMapper.selectCodeValuesByCodeNameAndValue("EMPLOYMENT_STATUS", excelInfo.getEmploymentStatus());
                if (StringUtils.isEmpty(employmentStatus)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 27, "描述有误！"));
                }
                hlsBpMaster.setEmploymentStatus(employmentStatus);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 27, IPrjProjectService.NOT_NULL));
            }
            //单位名称
            if (StringUtil.isNotEmpty(excelInfo.getWorkingPlace())) {
                hlsBpMaster.setWorkingPlace(excelInfo.getWorkingPlace());
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 28, IPrjProjectService.NOT_NULL));
            }

            //职业性质
            if (StringUtil.isNotEmpty(excelInfo.getJobNature())) {
                String jobNature = codeValueMapper.selectCodeValuesByCodeNameAndValue("BP.JOB_NATURE", excelInfo.getJobNature());
                if (StringUtils.isEmpty(jobNature)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 29, "描述有误！"));
                }
                hlsBpMaster.setJobNature(jobNature);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 29, IPrjProjectService.NOT_NULL));
            }

            //行业
            if (StringUtil.isNotEmpty(excelInfo.getIndustry())) {
                String industry = codeValueMapper.selectCodeValuesByCodeNameAndValue("INDUSTRY_ORG", excelInfo.getIndustry());
                if (StringUtils.isEmpty(industry)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 30, "描述有误！"));
                }
                hlsBpMaster.setIndustry(industry);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 30, IPrjProjectService.NOT_NULL));
            }

            //职业
            if (StringUtil.isNotEmpty(excelInfo.getProfession())) {
                String profession = codeValueMapper.selectCodeValuesByCodeNameAndValue("PROFESSION", excelInfo.getProfession());
                if (StringUtils.isEmpty(profession)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 31, "描述有误！"));
                }
                hlsBpMaster.setProfession(profession);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 31, IPrjProjectService.NOT_NULL));
            }

            //职务
            if (StringUtil.isNotEmpty(excelInfo.getPosition())) {
                String position = codeValueMapper.selectCodeValuesByCodeNameAndValue("POSITION", excelInfo.getPosition());
                if (StringUtils.isEmpty(position)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 32, "描述有误！"));
                }
                hlsBpMaster.setPosition(position);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 32, IPrjProjectService.NOT_NULL));
            }

            //职称
            if (StringUtil.isNotEmpty(excelInfo.getJobTitle())) {
                String jobTitle = codeValueMapper.selectCodeValuesByCodeNameAndValue("JOB_TITLE", excelInfo.getJobTitle());
                if (StringUtils.isEmpty(jobTitle)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 33, "描述有误！"));
                }
                hlsBpMaster.setJobTitle(jobTitle);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 33, IPrjProjectService.NOT_NULL));
            }

        } else {
            //法人

            //登记注册号类型 默认统一社会信用代码
            hlsBpMaster.setRegnotype("07");
            //承租人简称
            if (StringUtil.isNotEmpty(excelInfo.getShortName())) {
                hlsBpMaster.setShortName(excelInfo.getShortName());
            }
            //实际控制人
            if (StringUtil.isNotEmpty(excelInfo.getActualController())) {
                hlsBpMaster.setActualController(excelInfo.getActualController());
            }
            //法定代表人
            if (StringUtil.isNotEmpty(excelInfo.getLegalPerson())) {
                hlsBpMaster.setLegalPerson(excelInfo.getLegalPerson());
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 16, IPrjProjectService.NOT_NULL));
            }
            //法定代表人证件号码
            if (StringUtil.isEmpty(excelInfo.getMainMembersCertCode())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 17, IPrjProjectService.NOT_NULL));
            }
            //注册时间
            if (StringUtil.isNotEmpty(excelInfo.getFoundedDate())) {
                try {
                    hlsBpMaster.setFoundedDate(simpleDateFormat.parse(excelInfo.getFoundedDate()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 18, IPrjProjectService.NOT_NULL));
                }
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 18, IPrjProjectService.NOT_NULL));
            }

            //行业
            if (StringUtil.isNotEmpty(excelInfo.getIndustry())) {
                String industry = codeValueMapper.selectCodeValuesByCodeNameAndValue("INDUSTRY_ORG", excelInfo.getIndustry());
                if (StringUtils.isEmpty(industry)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 30, "描述有误！"));
                }
                hlsBpMaster.setIndustry(industry);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 30, IPrjProjectService.NOT_NULL));
            }

            //存续状态
            if (StringUtil.isNotEmpty(excelInfo.getSubsistingStatus())) {
                String subsistingStatus = codeValueMapper.selectCodeValuesByCodeNameAndValue("SUBSISTING_STATUS", excelInfo.getSubsistingStatus());
                if (StringUtils.isEmpty(subsistingStatus)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 34, "描述有误！"));
                }
                hlsBpMaster.setSubsistingStatus(subsistingStatus);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 34, IPrjProjectService.NOT_NULL));
            }

            //组织机构类型
            if (StringUtil.isNotEmpty(excelInfo.getOrganizationType())) {
                String organizationType = codeValueMapper.selectCodeValuesByCodeNameAndValue("ORGANIZATION_TYPE", excelInfo.getOrganizationType());
                if (StringUtils.isEmpty(organizationType)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 35, "描述有误！"));
                }
                hlsBpMaster.setOrganizationType(organizationType);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 35, IPrjProjectService.NOT_NULL));
            }

            //营业许可证到期日
            if (StringUtil.isNotEmpty(excelInfo.getLicenseTerms())) {
                try {
                    hlsBpMaster.setLicenseTerms(simpleDateFormat.parse(excelInfo.getLicenseTerms()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 36, IPrjProjectService.NOT_NULL));
                }
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 36, IPrjProjectService.NOT_NULL));
            }

            //经济类型(公司性质)
            if (StringUtil.isNotEmpty(excelInfo.getCompanyNature())) {
                String companyNature = codeValueMapper.selectCodeValuesByCodeNameAndValue("PRJ_NATURE_OF_BUSINESS", excelInfo.getCompanyNature());
                if (StringUtils.isEmpty(companyNature)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 37, "描述有误！"));
                }
                hlsBpMaster.setCompanyNature(companyNature);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 37, IPrjProjectService.NOT_NULL));
            }

            //企业规模
            if (StringUtil.isNotEmpty(excelInfo.getEnterpriseScale())) {
                String enterpriseScale = codeValueMapper.selectCodeValuesByCodeNameAndValue("ENTERPRISE_SCALE_TYPE", excelInfo.getEnterpriseScale());
                if (StringUtils.isEmpty(enterpriseScale)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 38, "描述有误！"));
                }
                hlsBpMaster.setEnterpriseScale(enterpriseScale);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 38, IPrjProjectService.NOT_NULL));
            }

            //注册资本币种
            if (StringUtil.isNotEmpty(excelInfo.getRegisteredCapitalCurrency())) {
                String registeredCapitalCurrency = codeValueMapper.selectCodeValuesByCodeNameAndValue("CHS.CURRENCY_TYPE", excelInfo.getRegisteredCapitalCurrency());
                if (StringUtils.isEmpty(registeredCapitalCurrency)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 39, "描述有误！"));
                }
                hlsBpMaster.setRegisteredCapitalCurrency(registeredCapitalCurrency);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 39, IPrjProjectService.NOT_NULL));
            }

            //注册资本（万元）
            if (StringUtil.isNotEmpty(excelInfo.getRegisteredCapital())) {
                hlsBpMaster.setRegisteredCapital(String.valueOf(Double.valueOf(excelInfo.getRegisteredCapital())));
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 40, IPrjProjectService.NOT_NULL));
            }
        }
        //如果已存在，检查是否更新了承租人信息 更新承租人(有进件审批或审批完成)需要插入版本记录
        if (null != hlsBpMaster.getBpId()) {

            if (checkBpMasterBaseIsUpdate(hlsBpMaster)) {
                try {
                    financePreCreateBpMasterHistory(iRequest, hlsBpMaster.getBpId());
                } catch (Exception e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 40, "记录历史版本错误"));
                }
            }
            hlsBpMasterService.updateByPrimaryKeySelective(iRequest,hlsBpMaster);

        } else {

            //生成商业伙伴编码
            String bpClass = hlsBpMaster.getBpClass();
            String bp_code = fndCodingRuleValuesService.getCodeRuleValue(iRequest, HLS_BP_DOCUMENT_CATEGORY, hlsBpMaster.getBpClass(), hlsBpMaster.getBpClass(), param);
            hlsBpMaster.setBpCode(bp_code);
            String authorityRuleString = hlsBpMasterService.getAuthorityString(iRequest);
            hlsBpMaster.setAuthorityRuleString(authorityRuleString);
            hlsBpMasterService.insert(iRequest, hlsBpMaster);

            //进件导入时新建承租人或担保人时，若类别为法人，登记注册号类型和登记注册号码需往zx_bp_orgbase表中插值，对应字段同样为REGNOTYPE、REGNO（影响变更）
            if (ORG.equals(hlsBpMaster.getBpClass())) {
                ZxBpOrgbase zxBpOrgbase = new ZxBpOrgbase();
                zxBpOrgbase.setBpId(hlsBpMaster.getBpId());
                zxBpOrgbase.setRegnotype(hlsBpMaster.getRegnotype());
                zxBpOrgbase.setRegno(hlsBpMaster.getRegno());
                zxBpOrgbaseMapper.insertSelective(zxBpOrgbase);
            }

            //插入商业伙伴角色信息
            HlsBpMasterRole hlsBpMasterRole = new HlsBpMasterRole();
            hlsBpMasterRole.setBpId(hlsBpMaster.getBpId());
            hlsBpMasterRole.setBpCategory(type);
            hlsBpMasterRole.setBpType(type);
            hlsBpMasterRole.setPrimaryFlag(Y);
            hlsBpMasterRole.setEnabledFlag(Y);
            hlsBpMasterRoleMapper.insertSelective(hlsBpMasterRole);
        }
        //hlsBpMaster.setAuthorityRuleString(generateAuthorityString(iRequest,hlsBpMaster.getD));

        return hlsBpMaster;
    }


    public void financePreCreateBpMasterHistory(IRequest iRequest, Long bpId) throws Exception {
        //创建变更
        BpMasterChangeReq bpChangeReq = new BpMasterChangeReq();
        bpChangeReq.setBpId(bpId);
        bpChangeReq.setChangeReqUserId(iRequest.getUserId());
        bpChangeReq.setChangeReqDate(new Date());
        try {
            bpMasterChangeReqService.initBpChangeInfo(iRequest, bpChangeReq);
            BpMasterChangeReq bpMasterChangeReq = new BpMasterChangeReq();
            bpMasterChangeReq.setChangeReqId(bpChangeReq.getChangeReqId());
            bpMasterChangeReq.setChangeReqDate(new Date());
            bpMasterChangeReq.setStatus("APPROVED");
            bpMasterChangeReqService.updateByPrimaryKeySelective(iRequest, bpMasterChangeReq);
        } catch (ParameterNullException e) {
            HlsCusException hlsCusException = new HlsCusException("初始化商业伙伴变更失败，请联系管理员!");
            hlsCusException.initCause(e);
            throw hlsCusException;
        }
    }

    /**
     * 检查商业伙伴基础信息是不是有更新
     *
     * @auther ycx 2022.01.10
     * @param hlsBpMaster            商业伙伴信息
     * @return
     */
    private boolean checkBpMasterBaseIsUpdate(HlsCusBpMaster hlsBpMaster) {
        HlsCusBpMaster queryMaster = new HlsCusBpMaster();
        //查询改承租人是否有审批中或者审批完成的进件
        if(hlsBpMasterMapper.queryProjectStatusbyBPId(hlsBpMaster.getBpId() ) == null || hlsBpMasterMapper.queryProjectStatusbyBPId(hlsBpMaster.getBpId()).size() == 0){
            return false;
        }
        if (StringUtils.equals(hlsBpMaster.getBpClass(), NP) ) {
            //证件号码
            queryMaster.setIdCardNo(hlsBpMaster.getIdCardNo());
            queryMaster = hlsBpMasterMapper.selectOne(queryMaster);
            List<String> bpIgnoreList = new ArrayList<>(6);
            bpIgnoreList.add("lastUpdateDate");
            bpIgnoreList.add("createdBy");
            bpIgnoreList.add("creationDate");
            bpIgnoreList.add("lastUpdatedBy");
            bpIgnoreList.add("lastUpdatetimeDate");
            bpIgnoreList.add("bpId");
            if (checkDiffer(hlsBpMaster, queryMaster, bpIgnoreList)) {
                return true;
            }

        } else if (StringUtils.equals(hlsBpMaster.getBpClass(), ORG)) {
            //社会统一信用代码
            queryMaster.setRegno(hlsBpMaster.getRegno());
            queryMaster = hlsBpMasterMapper.selectOne(queryMaster);
            List<String> bpIgnoreList = new ArrayList<>(6);
            bpIgnoreList.add("lastUpdateDate");
            bpIgnoreList.add("createdBy");
            bpIgnoreList.add("creationDate");
            bpIgnoreList.add("lastUpdatedBy");
            bpIgnoreList.add("lastUpdatetimeDate");
            bpIgnoreList.add("bpId");
            if (checkDiffer(hlsBpMaster, queryMaster, bpIgnoreList)) {
                return true;
            }
        }


        return false;
    }

    //处理进件信息
    HlsCusPrjProject importPrjProject(IRequest iRequest, PrjExcelImportDto prjExcelImportDto, String sheetName, int i, Long bpId, String division) throws HlsCusException {
//        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
//        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
//        String emplyeeCode = "";
//
//        hlsCusPrjProject.setProjectStatus(PROJECT_STATUS_NEW);
//        hlsCusPrjProject.setContractTextStatus("UNCREATED");
////        hlsCusPrjProject.setSpecialAttachmentFlag(BaseConstants.NO);
//        hlsCusPrjProject.setCompanyId(iRequest.getCompanyId());
//
//        //厂商签约方式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getManufacturerSignType())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", prjExcelImportDto.getManufacturerSignType());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 84, "描述有误！"));
//            }
//            hlsCusPrjProject.setManufacturerSignType(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 84, IPrjProjectService.NOT_NULL));
//        }
//
//        //主机厂签约方式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getVenderSignType())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", prjExcelImportDto.getVenderSignType());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 85, "描述有误！"));
//            }
//            hlsCusPrjProject.setVenderSignType(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 85, IPrjProjectService.NOT_NULL));
//        }
//
//        //经销商签约方式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getDealerSignType())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", prjExcelImportDto.getDealerSignType());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 86, "描述有误！"));
//            }
//            hlsCusPrjProject.setDealerSignType(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 86, IPrjProjectService.NOT_NULL));
//        }
//
//        //商用车业务线
//        if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
//            //是否抵押越秀
//            if (StringUtil.isNotEmpty(prjExcelImportDto.getPledgeFlag())) {
//                String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.YES_AND_NO, prjExcelImportDto.getPledgeFlag());
//                if (StringUtils.isEmpty(value)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 87, "描述有误！"));
//                }
//                hlsCusPrjProject.setPledgeFlag(value);
//            } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 87, IPrjProjectService.NOT_NULL));
//            }
//            //保险购买情况
//            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceFlag())) {
//                String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(INSURANCE_PURCHASE_STATUS, prjExcelImportDto.getInsuranceFlag());
//                if (StringUtils.isEmpty(value)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 88, "描述有误！"));
//                }
//                hlsCusPrjProject.setInsuranceFlag(value);
//            } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 88, IPrjProjectService.NOT_NULL));
//            }
//        } else {
//            //保险购买情况
//            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceFlag())) {
//                String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(INSURANCE_PURCHASE_STATUS, prjExcelImportDto.getInsuranceFlag());
//                if (StringUtils.isEmpty(value)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 87, "描述有误！"));
//                }
//                hlsCusPrjProject.setInsuranceFlag(value);
//            } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 87, IPrjProjectService.NOT_NULL));
//            }
//        }
//        /*hlsCusPrjProject.setLeaseOrganization(session.getAttribute("unitCode").toString());*/
//        hlsCusPrjProject.setBpIdTenant(bpId);
//        HlsBpMaster hlsBpMaster = new HlsBpMaster();
//        hlsBpMaster.setEnabledFlag("Y");
//        //单据类别
//        hlsCusPrjProject.setDocumentCategory(PROJECT_DOCUMENT_CATEGORY);
//        //单据类型
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getDocumentType())) {
//            DocumentType documentType = new DocumentType();
//            documentType.setDocumentCategory(hlsCusPrjProject.getDocumentCategory());
//            documentType.setDescription(prjExcelImportDto.getDocumentType());
//            List<DocumentType> documentTypes = documentTypeMapper.select(documentType);
//            if (CollectionUtils.isEmpty(documentTypes) || documentTypes.size() != 1) {
////                throw new HlsCusException(DATA_ECXEPTION);
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 41, "单据类型有误！"));
//            }
//            hlsCusPrjProject.setDocumentType(documentTypes.get(0).getDocumentType());
//
//            //业务类型
//            BusinessType businessType = new BusinessType();
//            businessType.setDocumentCategory(hlsCusPrjProject.getDocumentCategory());
//            businessType.setDescription(prjExcelImportDto.getDocumentType());
//            List<BusinessType> businessTypeList = businessTypeMapper.select(businessType);
//            if (CollectionUtils.isEmpty(businessTypeList) || businessTypeList.size() != 1) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 41, "单据类型有误！"));
//            }
//            hlsCusPrjProject.setBusinessType(businessTypeList.get(0).getBusinessType());
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 41, IPrjProjectService.NOT_NULL));
//        }
//
//        //商业模式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseChannel())) {
//            FndLeaseChannel fndLeaseChannel = new FndLeaseChannel();
//            fndLeaseChannel.setDescription(prjExcelImportDto.getLeaseChannel());
//            try {
//                fndLeaseChannel = fndLeaseChannelMapper.selectOne(fndLeaseChannel);
//                hlsCusPrjProject.setLeaseChannel(fndLeaseChannel.getLeaseChannel());
//            } catch (Exception e) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 42, "商业模式有误！"));
//            }
//            if (StringUtils.isEmpty(fndLeaseChannel.getLeaseChannel())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 42, "商业模式有误！"));
//            }
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 42, IPrjProjectService.NOT_NULL));
//        }
//        //项目经理
//        /*if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_24())) {
//            FndEmployee fndEmployee = new FndEmployee();
//            fndEmployee.setName(fndInterfaceLines.getAttributes_24());
//            List<FndEmployee> fndEmployeeList = fndEmployeeMapper.select(fndEmployee);
//            if (CollectionUtils.isEmpty(fndEmployeeList) || fndEmployeeList.size() != 1) {
//                throw new HlsCusException(DATA_ECXEPTION);
//            }
//            hlsCusPrjProject.setEmployeeId(fndEmployeeList.get(0).getEmployeeId());
//            emplyeeCode = fndEmployeeList.get(0).getEmployeeCode();
//
//            //根据项目经理查询业务部门，若多个，则取第一个
//            FndEmployeeAssigns fndEmployeeAssigns = new FndEmployeeAssigns();
//            fndEmployeeAssigns.setEmployeeId(hlsCusPrjProject.getEmployeeId());
//            List<FndEmployeeAssigns> assignsList = fndEmployeeAssignsMapper.select(fndEmployeeAssigns);
//            if (CollectionUtils.isEmpty(assignsList)) {
//                throw new HlsCusException(DATA_ECXEPTION);
//            }
//            FndOrgUnit fndOrgUnit = new FndOrgUnit();
//            fndOrgUnit.setUnitId(assignsList.get(0).getUnitId());
//            fndOrgUnit = fndOrgUnitMapper.selectByPrimaryKey(fndOrgUnit);
//            hlsCusPrjProject.setLeaseOrganization(fndOrgUnit.getUnitCode());
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 24, IPrjProjectService.NOT_NULL));
//        }*/
//
//        //经销商名称
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getDealerName())) {
//            hlsBpMaster.setBpName(prjExcelImportDto.getDealerName());
//            try {
//                hlsBpMaster = hlsBpMasterMapper.selectOne(hlsBpMaster);
//            } catch (Exception e) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 43, "经销商系统匹配到多个商业伙伴！"));
//            }
//
//            if (hlsBpMaster == null) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 43, IPrjProjectService.NOT_NULL));
//            }
//            hlsCusPrjProject.setBpIdVender(hlsBpMaster.getBpId());
//        }/*else{
//            throw new HlsCusException(getExceptionInfo(sheetName,i,25,IPrjProjectService.NOT_NULL));
//        }*/
//        //归属主机厂
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getManufacturerName())) {
//            hlsBpMaster = new HlsBpMaster();
//            hlsBpMaster.setEnabledFlag("Y");
//            hlsBpMaster.setBpName(prjExcelImportDto.getManufacturerName());
//            try {
//                hlsBpMaster = hlsBpMasterMapper.selectOne(hlsBpMaster);
//            } catch (Exception e) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 44, "归属主机厂系统匹配到多个商业伙伴！"));
//            }
//            if (hlsBpMaster == null) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 44, "归属主机厂名称有误！"));
//            }
//            hlsCusPrjProject.setFactoryId(hlsBpMaster.getBpId());
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 44, IPrjProjectService.NOT_NULL));
//        }
//
//        //查询价目表是否存在
//        BpMasterReply bpMasterReply = new BpMasterReply();
//        bpMasterReply.setFactoryId(hlsCusPrjProject.getFactoryId());
//        List<BpMasterReply> replyList = iBpMasterReplyService.selectReplyInfo(iRequest, bpMasterReply);
//        Boolean existFlag = false;
//        if (prjExcelImportDto.getPlanName() != null) {
//            for (BpMasterReply reply : replyList) {
//                if (reply.getHlsProductDefinitionList() != null) {
//                    for (Map map : reply.getHlsProductDefinitionList()) {
//                        if (map.get(DEFINITION_NAME).toString().equals(prjExcelImportDto.getPlanName())) {
//                            existFlag = true;
//                        }
//                    }
//                }
//            }
//        }
//        if (!existFlag) {
//            throw new HlsCusException(prjExcelImportDto.getManufacturerName() + PRICE_EXCEPTION + prjExcelImportDto.getPlanName());
//        }
//
//        //查询用户对应的主机厂或者供应商,判断与用户维护的主机厂或者供应商是否一致
//        User user = new User();
//        user.setUserId(iRequest.getUserId());
//        user = userMapper.selectByPrimaryKey(user);
//        if ("VENDER".equalsIgnoreCase(user.getBpCategory())) {
//            if (!hlsCusPrjProject.getFactoryId().equals(user.getBpId())) {
//                throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(FACTORY_EXCEPTION).toString());
//            }
//        } else if ("DEALER".equalsIgnoreCase(user.getBpCategory())) {
//            if (hlsCusPrjProject.getBpIdVender() != null && !hlsCusPrjProject.getBpIdVender().equals(user.getBpId())) {
//                throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(DEALER_EXCEPTION).toString());
//            }
//        }
//        //保存厂商信息
//        HlsBpMasterRelation hlsBpMasterRelation = new HlsBpMasterRelation();
//        hlsBpMasterRelation.setBpId(hlsCusPrjProject.getFactoryId());
//        hlsBpMasterRelation.setRelationType("MANUFACTURER");
//        hlsBpMasterRelation.setEnabledFlag(BaseConstants.YES);
//        try {
//            hlsBpMasterRelation = hlsBpMasterRelationMapper.selectOne(hlsBpMasterRelation);
//        } catch (Exception e) {
//            throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行主机厂查找不到对应的厂商信息！").toString());
//        }
//
//        hlsCusPrjProject.setManufacturerId(hlsBpMasterRelation.getRelatedBpId());
//
//        //如果是中联重科厂商 才能保存合作方进件序号和合作方支付表编号
//
//        if(ZLZK_BP_CODE.equals(hlsBpMasterMapper.selectByPrimaryKey(hlsBpMasterRelation.getRelatedBpId()).getBpCode())){
//            //去除空格及换行符等
//            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//            String contractNumber = prjExcelImportDto.getPartnersContractNumber();
//            String paymentNumber = prjExcelImportDto.getPartnersPaymentNumber();
//            if(contractNumber != null){
//                Matcher m = p.matcher(contractNumber);
//                hlsCusPrjProject.setPartnersContractNumber(m.replaceAll(""));
//            }
//            if(paymentNumber != null){
//                Matcher n = p.matcher(paymentNumber);
//                hlsCusPrjProject.setPartnersPaymentNumber(n.replaceAll(""));
//            }
//        }
//
//        hlsCusPrjProject.setProjectNumber(getProjectNumber(iRequest, hlsCusPrjProject));
//        //预计起租日
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseStartDate())) {
//            try {
//                hlsCusPrjProject.setLeaseStartDate(simpleDateFormat.parse(prjExcelImportDto.getLeaseStartDate()));
//            } catch (ParseException e) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 46, IPrjProjectService.NOT_NULL));
//            }
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 46, IPrjProjectService.NOT_NULL));
//        }
//        /*//保险购买情况
//        if(StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_26())){
//            String value  = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.YES_AND_NO, fndInterfaceLines.getAttributes_26());
//            hlsCusPrjProject.setInsuranceFlag(value);
//        }else{
//            throw new HlsCusException(getExceptionInfo(sheetName,i,26,IPrjProjectService.NOT_NULL));
//        }*/
//
//        /*//调息规则
//        if(StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_27())){
//            String value  = codeValueMapper.selectCodeValuesByCodeNameAndValue(FLOATING_RANGE_METHOD, fndInterfaceLines.getAttributes_27());
//            hlsCusPrjProject.setFloatingRangeMethod(value);
//        }else{
//            throw new HlsCusException(getExceptionInfo(sheetName,i,27,IPrjProjectService.NOT_NULL));
//        }*/
//
//        //是否二手机
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getSecondHandFlag())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.PRJ_EQUIPMENT_TYPE, prjExcelImportDto.getSecondHandFlag());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 47, "描述有误！"));
//            }
//            hlsCusPrjProject.setSecondHandFlag(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 47, IPrjProjectService.NOT_NULL));
//        }
//
//        //是否经销商模式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getVenderModeFlag())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.YES_AND_NO, prjExcelImportDto.getVenderModeFlag());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 48, "描述有误！"));
//            }
//            hlsCusPrjProject.setVenderModeFlag(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 48, IPrjProjectService.NOT_NULL));
//        }
//
//        //基准利率类型
//        hlsCusPrjProject.setBaseRateType(PBOC);
//
//        //币种
//        hlsCusPrjProject.setCurrency(CNY);
//
//        //出卖人
//        if (StringUtils.isEmpty(prjExcelImportDto.getSellName())) {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 60, IPrjProjectService.NOT_NULL));
//        }
//        hlsBpMaster = new HlsBpMaster();
//        hlsBpMaster.setEnabledFlag("Y");
//        hlsBpMaster.setBpName(prjExcelImportDto.getSellName());
//        try {
//            hlsBpMaster = hlsBpMasterMapper.selectOne(hlsBpMaster);
//        } catch (Exception e) {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 60, "查找到多个出卖人！"));
//        }
//
//        if (hlsBpMaster == null) {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 60, IPrjProjectService.SELLER_NOT_EXISTS));
//        }
//        hlsCusPrjProject.setSellerId(hlsBpMaster.getBpId());
//
//        hlsCusPrjProject.setDivision(division);
//        hlsCusPrjProject.setAuthorityRuleString(generateAuthorityString(iRequest, hlsCusPrjProject.getDivision() == null ? "" : hlsCusPrjProject.getDivision(), hlsCusPrjProject.getDocumentType(), hlsCusPrjProject.getBusinessType(), emplyeeCode));
//
//        //产品方案
//        HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getPlanName())) {
//            hlsProductDefinition.setDefinitionName(prjExcelImportDto.getPlanName());
//            hlsProductDefinition.setEnabledFlag(Y);
//            try {
//                hlsProductDefinition = hlsProductDefinitionMapper.selectOne(hlsProductDefinition);
//            } catch (Exception e) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "查询到多个产品！"));
//            }
//
//            if (Objects.isNull(hlsProductDefinition)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "未找到相应产品！"));
//            }
//
//            hlsCusPrjProject.setPaymentBpType(hlsProductDefinition.getPaymentBpType());
//            hlsCusPrjProject.setRepayBpType(hlsProductDefinition.getRepayBpType());
//            //根据付款对象类型给付款对象赋值
//            switch (hlsCusPrjProject.getPaymentBpType()) {
//                case SynPrjInfoConstants.MANUFACTURER:
//                case SynPrjInfoConstants.PAYMENT_BP_TYPE_PARTNERS:
//                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getManufacturerId());
//                    break;
//                case SynPrjInfoConstants.TENANT:
//                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getBpIdTenant());
//                    break;
//                case SynPrjInfoConstants.VENDER:
//                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getFactoryId());
//                    break;
//                case SynPrjInfoConstants.DEALER:
//                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getBpIdVender());
//                    break;
//            }
//            //判断付款对象是否为空
//            if (null == hlsCusPrjProject.getPaymentBpId()) {
//                String paymentBpType = codeValueMapper.getMeaningByCodeAndCodeValue(SynPrjInfoConstants.PAYMENT_BP_TYPE, hlsCusPrjProject.getPaymentBpType());
//                throw new HlsCusException(new StringBuffer("产品付款对象类型为").append(paymentBpType).append(",").append(paymentBpType)
//                        .append("字段不能为空！").toString());
//            }
//
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 49, IPrjProjectService.NOT_NULL));
//        }
//
//        //签约方式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getSignType())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.SIGN_METHOD, prjExcelImportDto.getSignType());
//            if (StringUtils.isNotEmpty(value)) {
//                hlsCusPrjProject.setSignType(value);
//            } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 45, "值描述有误!"));
//            }
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 45, IPrjProjectService.NOT_NULL));
//        }
//
//        //租金计划是否通知客户不能为空
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getRentPlanNoticeFlag())) {
//            String rentPlanNoticeFlag = codeValueMapper.selectCodeValuesByCodeNameAndValue("SYS.YES_NO", prjExcelImportDto.getRentPlanNoticeFlag());
//            if (StringUtils.isEmpty(rentPlanNoticeFlag)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 80, "描述有误！"));
//            }
//            hlsCusPrjProject.setRentPlanNoticeFlag(rentPlanNoticeFlag);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 80, IPrjProjectService.NOT_NULL));
//        }
//        hlsCusPrjProject.setSubstituteReceiptFlag("N");
//        self().insertSelective(iRequest, hlsCusPrjProject);
//
//        return hlsCusPrjProject;
        return new HlsCusPrjProject();
    }

    //处理商业伙伴主要组成人员信息
    void importBpMasterMainMembers(PrjExcelImportDto excelInfo, HlsCusBpMaster hlsBpMaster) {
        HlsBpMasterMainMembers hlsBpMasterMainMembers = new HlsBpMasterMainMembers();
        hlsBpMasterMainMembers.setBpId(hlsBpMaster.getBpId());
        //主要组成人员姓名
        hlsBpMasterMainMembers.setMainMembersName(excelInfo.getLegalPerson());
        //主要组成人员证件类型
        hlsBpMasterMainMembers.setMainMembersCertType("ID_CARD");
        //主要组成人员证件号码
        hlsBpMasterMainMembers.setMainMembersCertCode(excelInfo.getMainMembersCertCode());
        //主要组成人员职位
        hlsBpMasterMainMembers.setMainMembersPosition("1");
        hlsBpMasterMainMembersMapper.insert(hlsBpMasterMainMembers);
    }

    //处理商业伙伴地址信息
    void importBpMasterAddress(IRequest iRequest, PrjExcelImportDto excelInfo, String sheetName, int i, HlsCusBpMaster hlsBpMaster) throws HlsCusException {
        HlsBpMasterAddress hlsBpMasterAddress = new HlsBpMasterAddress();
        hlsBpMasterAddress.setBpId(hlsBpMaster.getBpId());
        //地址类型
        if (StringUtil.isEmpty(excelInfo.getAddressType())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 20, IPrjProjectService.NOT_NULL));
        } else {
            String address = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.HLS211_ADDRESS_TYPE, excelInfo.getAddressType());
            if (StringUtils.isEmpty(address)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 20, "地址类型有误！"));
            } else {
                hlsBpMasterAddress.setAddressType(address);
            }
        }
        //国家
        if (StringUtil.isEmpty(excelInfo.getCountryId())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 21, IPrjProjectService.NOT_NULL));
        } else {
            FndCountry fc = new FndCountry();
            fc.setCountryName(excelInfo.getCountryId());
            try {
                fc = fndCountryMapper.selectOne(fc);
                if (Objects.isNull(fc)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 21, "请填写正确国家名称"));
                }
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 21, IPrjProjectService.NOT_NULL));
            }
            hlsBpMasterAddress.setCountryId(Long.valueOf(fc.getCountry()));
        }
        //省
        if (StringUtil.isEmpty(excelInfo.getProvinceId())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 22, IPrjProjectService.NOT_NULL));
        } else {
            FndProvince fp = new FndProvince();
            fp.setDescription(excelInfo.getProvinceId());
            try {
//                fp = fndProvinceMapper.selectOne(fp);
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 22, IPrjProjectService.NOT_NULL));
            }
            if (Objects.isNull(fp)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 22, "请填写正确省市名称"));
            }
            hlsBpMasterAddress.setProvinceId(Long.valueOf(fp.getProvince()));
        }
        //市
        if (StringUtil.isEmpty(excelInfo.getCityId())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 23, IPrjProjectService.NOT_NULL));
        } else {
            FndCity fc = new FndCity();
            fc.setDescription(excelInfo.getCityId());
            fc.setProvince(hlsBpMasterAddress.getProvinceId().toString());
            try {
//                fc = fndCityMapper.selectOne(fc);
                if (fc == null) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 23, "请填写正确省市名称"));
                }
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 23, IPrjProjectService.NOT_NULL));
            }
            hlsBpMasterAddress.setCityId(Long.valueOf(fc.getCity()));
        }
        //详细信息
        if (StringUtil.isEmpty(excelInfo.getAddress())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 24, IPrjProjectService.NOT_NULL));
        } else {
            hlsBpMasterAddress.setAddress(excelInfo.getAddress());
        }

        //邮编
        hlsBpMasterAddress.setZipcode(null);

        //电话
        if (IPrjProjectService.NP.equals(hlsBpMaster.getBpClass())) {
            //手机
            if (StringUtil.isEmpty(excelInfo.getMobilePhone())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 26, IPrjProjectService.NOT_NULL));
            } else {
                String regExp = "^(1)\\d{10}$";
                String regExp1 = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                Pattern p = Pattern.compile(regExp);
                Pattern p1 = Pattern.compile(regExp1);
                Matcher m = p.matcher(excelInfo.getMobilePhone());
                Matcher m1 = p1.matcher(excelInfo.getMobilePhone());
                if (m.matches() || m1.matches()) {
                    hlsBpMasterAddress.setCellPhone(excelInfo.getMobilePhone());
                } else {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 26, "联系方式不符合规范，请检查！"));
                }
            }
            if (!StringUtil.isEmpty(excelInfo.getPhone())) {
                hlsBpMasterAddress.setPhone(excelInfo.getPhone());
            }
        } else if (IPrjProjectService.ORG.equals(hlsBpMaster.getBpClass())) {
            if (StringUtil.isEmpty(excelInfo.getPhone())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 25, IPrjProjectService.NOT_NULL));
            } else {
                hlsBpMasterAddress.setPhone(excelInfo.getPhone());
            }
            if (!StringUtil.isEmpty(excelInfo.getMobilePhone())) {
                String regExp = "^(1)\\d{10}$";
                String regExp1 = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                Pattern p = Pattern.compile(regExp);
                Pattern p1 = Pattern.compile(regExp1);
                Matcher m = p.matcher(excelInfo.getMobilePhone());
                Matcher m1 = p1.matcher(excelInfo.getMobilePhone());
                if (m.matches() || m1.matches()) {
                    hlsBpMasterAddress.setCellPhone(excelInfo.getMobilePhone());
                } else {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 25, "手机号码格式错误"));
                }
            }
        }
        hlsBpMasterAddressMapper.insert(hlsBpMasterAddress);
    }

    //处理报价信息
    HlsCusPrjQuotation importPrjQuotation(IRequest iRequest, PrjExcelImportDto prjExcelImportDto, String sheetName, int i) throws HlsCusException {
//        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
//        prjQuotation.setQuotationDate(new Date());
//        //产品方案
//        HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getPlanName())) {
//            hlsProductDefinition.setDefinitionName(prjExcelImportDto.getPlanName());
//            hlsProductDefinition.setEnabledFlag(Y);
//            try {
//                hlsProductDefinition = hlsProductDefinitionMapper.selectOne(hlsProductDefinition);
//            } catch (Exception e) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "系统匹配到多条产品！"));
//            }
//
//            if (Objects.isNull(hlsProductDefinition)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "产品方案不存在！"));
//            }
//
//
//            prjQuotation.setPlanId(hlsProductDefinition.getDefinitionId());
//            //价目表
//            HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
//            hlsCalcConfig.setPriceList(hlsProductDefinition.getPriceList());
//            hlsCalcConfig = hlsCalcConfigMapper.selectByPrimaryKey(hlsCalcConfig);
//            prjQuotation.setPriceList(hlsCalcConfig.getPriceList());
//            prjQuotation.setSheets(hlsCalcConfig.getSheets());
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 49, IPrjProjectService.NOT_NULL));
//        }
//        //首付款推算方式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getDownPaymentMethod())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(PAY_METHOD, prjExcelImportDto.getDownPaymentMethod());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 50, "描述有误！"));
//            }
//            prjQuotation.setDownPaymentMethod(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 50, IPrjProjectService.NOT_NULL));
//        }
//        NumberFormat numberFormat = NumberFormat.getPercentInstance();
//        //首付款比例/金额
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getDownPayment())) {
//            if ("20".equalsIgnoreCase(prjQuotation.getDownPaymentMethod())) {
//                Double downPaymentRatio = Double.valueOf(prjExcelImportDto.getDownPayment());
//                prjQuotation.setDownPaymentRatio(downPaymentRatio);
//            } else {
//                prjQuotation.setDownPayment(Double.valueOf(prjExcelImportDto.getDownPayment()));
//            }
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 51, IPrjProjectService.NOT_NULL));
//        }
//        //保证金推算方式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getDepositMethod())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(PAY_METHOD, prjExcelImportDto.getDepositMethod());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 52, "描述有误！"));
//            }
//            prjQuotation.setDepositMethod(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 52, IPrjProjectService.NOT_NULL));
//        }
//        //保证金比例/金额
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getDeposit())) {
//            if ("20".equalsIgnoreCase(prjQuotation.getDepositMethod())) {
//                Double depositRatio = Double.valueOf(prjExcelImportDto.getDeposit());
//                prjQuotation.setDepositRatio(depositRatio);
//            } else {
//                prjQuotation.setDeposit(Double.valueOf(prjExcelImportDto.getDeposit()));
//            }
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 53, IPrjProjectService.NOT_NULL));
//        }
//        //租赁利率%
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getIntRate())) {
//            Double intRate = Double.valueOf(prjExcelImportDto.getIntRate());
//            prjQuotation.setIntRate(intRate);
//
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 54, IPrjProjectService.NOT_NULL));
//        }
//        //手续费率%
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseChargeRatio())) {
//            Double chargeRatio = Double.valueOf(prjExcelImportDto.getLeaseChargeRatio());
//            prjQuotation.setLeaseChargeRatio(chargeRatio);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 55, IPrjProjectService.NOT_NULL));
//        }
//        //是否返利
//        prjQuotation.setRepayFlag(hlsProductDefinition.getRepayFlag());
//
//        //宽限期类型
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getGraceType())) {
//            String value = codeValueMapper.selectCodeValuesByCodeNameAndValue(GRACE_TYPE, prjExcelImportDto.getGraceType());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 56, IPrjProjectService.GRACE_TYPE_MATCH_ERROR));
//            }
//            prjQuotation.setGraceType(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 56, IPrjProjectService.NOT_NULL));
//        }
//        //自定义宽限期月份,宽限类型50的时候做必填校验
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getGraceMonth())) {
//            prjQuotation.setGraceMonth(prjExcelImportDto.getGraceMonth());
//            String[] arr = prjExcelImportDto.getGraceMonth().split("-");
//            for (int g = 0; g < arr.length; g++) {
//                switch (g) {
//                    case 0:
//                        prjQuotation.setGraceMonthOne(Long.valueOf(arr[0]));
//                        break;
//                    case 1:
//                        prjQuotation.setGraceMonthTwo(Long.valueOf(arr[1]));
//                        break;
//                    case 2:
//                        prjQuotation.setGraceMonthThree(Long.valueOf(arr[2]));
//                        break;
//                    case 3:
//                        prjQuotation.setGraceMonthFour(Long.valueOf(arr[3]));
//                        break;
//                }
//            }
//        } else if ("50".equalsIgnoreCase(prjExcelImportDto.getGraceType())) {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 56, IPrjProjectService.NOT_NULL));
//        }
//        //租赁期限(月数)
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseTerm())) {
//            prjQuotation.setLeaseTerm(Double.valueOf(prjExcelImportDto.getLeaseTerm()));
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 58, IPrjProjectService.NOT_NULL));
//        }
//
//        //支付频率
//        prjQuotation.setAnnualPayTimes(hlsProductDefinition.getAnnualPayTimes());
//        prjQuotation.setLeaseTimes(prjQuotation.getLeaseTerm().longValue() / 12 * Long.valueOf(prjQuotation.getAnnualPayTimes()));
//
//        //逾期宽限类型
//        prjQuotation.setGraceFlag(hlsProductDefinition.getExemptPenaltyInt());
//
//        HlsProductDefinitionPara hlsProductDefinitionPara = new HlsProductDefinitionPara();
//        hlsProductDefinitionPara.setDefinitionId(hlsProductDefinition.getDefinitionId());
//        List<HlsProductDefinitionPara> paraList = hlsProductDefinitionParaMapper.select(hlsProductDefinitionPara);
//        for (HlsProductDefinitionPara para : paraList) {
//            if (para.getProductPara() != null) {
//                switch (para.getProductPara()) {
//                    case INT_RATE:
//                        prjQuotation.setIntRateReply(para.getDefaultValue().doubleValue());
//                        break;
//                    case LEASE_CHARGE_RATIO:
//                        prjQuotation.setLeaseChargeRatioReply(para.getDefaultValue().doubleValue());
//                        break;
//                    case GRACE_PERIOD:
//                        prjQuotation.setGracePeriod(para.getDefaultValue().longValue());
//                        break;
//                    case PENALTY_RATE:
//                        prjQuotation.setPenaltyRate(para.getDefaultValue().doubleValue());
//                        break;
//                    case MANUFACTURER_GRACE_DAY:
//                        prjQuotation.setManufacturerGraceDay(para.getDefaultValue().longValue());
//                        break;
//                    case MANUFACTURER_PENALTY_RATE:
//                        prjQuotation.setManufacturerPenaltyRate(para.getDefaultValue().doubleValue());
//                        break;
//                }
//            }
//        }
//
//        //保证金处理方式
//        prjQuotation.setDepositProcess(DEPOSIT_PROCESS_10);
//
//
//        //名义货价
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getResidualValue())) {
//            prjQuotation.setResidualValue(Double.valueOf(prjExcelImportDto.getResidualValue()));
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 59, IPrjProjectService.NOT_NULL));
//        }
//
//        //付款方式
//        if (StringUtils.isEmpty(prjExcelImportDto.getPaymentType())) {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 61, IPrjProjectService.NOT_NULL));
//        } else {
//            if (!codeValueMapper.selectCodeNamesByCode(SynPrjInfoConstants.PRJ_PAYMENT_METHOD).contains(prjExcelImportDto.getPaymentType())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 61, "值描述有误！"));
//            } else {
//                prjQuotation.setPaymentType(codeValueMapper.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.PRJ_PAYMENT_METHOD, prjExcelImportDto.getPaymentType()));
//            }
//            //如果付款方式为银承，校验承兑期限、银行保证金、银承手续费比例
//            if (StringUtils.equals(prjQuotation.getPaymentType(), "BANK_ACCEPTANCE")) {
//                //承兑期限
//                if (StringUtils.isEmpty(prjExcelImportDto.getAcceptanceTerm())) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 64, IPrjProjectService.NOT_NULL));
//                } else {
//                    if (!codeValueMapper.selectCodeNamesByCode(SynPrjInfoConstants.PRJ_ACCEPTANCE_TERM).contains(prjExcelImportDto.getAcceptanceTerm())) {
//                        throw new HlsCusException(getExceptionInfo(sheetName, i, 64, "值描述有误！"));
//                    } else {
//                        prjQuotation.setAcceptanceTerm(codeValueMapper.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.PRJ_ACCEPTANCE_TERM, prjExcelImportDto.getAcceptanceTerm()));
//                    }
//                }
//                //银承保证金比例
//                if (StringUtils.isEmpty(prjExcelImportDto.getBankDepositRatio())) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 62, IPrjProjectService.NOT_NULL));
//                } else {
//                    prjQuotation.setBankDepositRatio(Double.valueOf(prjExcelImportDto.getBankDepositRatio()));
//                }
//
//                //银承手续费比例
//                if (StringUtils.isEmpty(prjExcelImportDto.getBankLeaseChargeRatio())) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 63, IPrjProjectService.NOT_NULL));
//                } else {
//                    prjQuotation.setBankLeaseChargeRatio(Double.valueOf(prjExcelImportDto.getBankLeaseChargeRatio()));
//                }
//            }
//        }
//
//        //租金比例报价字段
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getPayMethodDesc()) && !ArrayUtils.contains(PAY_METHOD_DESCS, prjExcelImportDto.getPayMethodDesc())) {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 65, "值描述有误！"));
//        } else {
//            prjQuotation.setPayMethodDesc(StringUtils.substring(prjExcelImportDto.getPayMethodDesc(), 0, 4));
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getLeaseChargeTimes())) {
//            prjQuotation.setLeaseChargeTimes(prjExcelImportDto.getLeaseChargeTimes());
//        }
//
//        //当存在租/本比例时，判断租本比例相加是否为1
//        Double rentalPrincipalRatio = 0D;
//        Boolean rentalPrincipalRatioFlag = false;
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio1())) {
//            prjQuotation.setRentalPrincipalRatio1(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio1()));
//            rentalPrincipalRatio = MathUtil.add(rentalPrincipalRatio, Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio1()));
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 67, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio2())) {
//            prjQuotation.setRentalPrincipalRatio2(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio2()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio2()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 68, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio3())) {
//            prjQuotation.setRentalPrincipalRatio3(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio3()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio3()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 69, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio4())) {
//            prjQuotation.setRentalPrincipalRatio4(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio4()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio4()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 70, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio5())) {
//            prjQuotation.setRentalPrincipalRatio5(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio5()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio5()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 71, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio6())) {
//            prjQuotation.setRentalPrincipalRatio6(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio6()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio6()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 72, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio7())) {
//            prjQuotation.setRentalPrincipalRatio7(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio7()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio7()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 73, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio8())) {
//            prjQuotation.setRentalPrincipalRatio8(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio8()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio8()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 74, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio9())) {
//            prjQuotation.setRentalPrincipalRatio9(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio9()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio9()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 75, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio10())) {
//            prjQuotation.setRentalPrincipalRatio10(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio10()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio10()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 76, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio11())) {
//            prjQuotation.setRentalPrincipalRatio11(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio11()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio11()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 77, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio12())) {
//            prjQuotation.setRentalPrincipalRatio12(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio12()));
//            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio12()), rentalPrincipalRatio);
//            rentalPrincipalRatioFlag = true;
//        } else {
//            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 78, IPrjProjectService.NOT_NULL));
//            }
//        }
//
//        if (rentalPrincipalRatio != 1D && rentalPrincipalRatioFlag == true) {
//            throw new HlsCusException("租/本比例之和不为1！");
//        }
//
//        //宽限类型-特殊方案
//        if (StringUtils.isNotEmpty(prjExcelImportDto.getGraceTypeSpecial())) {
//            if (!codeValueMapper.selectCodeNamesByCode(SynPrjInfoConstants.GRACE_TYPE_SPECIAL).contains(prjExcelImportDto.getGraceTypeSpecial())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 79, "值有误！"));
//            } else {
//                prjQuotation.setGraceTypeSpecial(codeValueMapper.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.GRACE_TYPE_SPECIAL, prjExcelImportDto.getGraceTypeSpecial()));
//            }
//        }
//
//        return prjQuotation;
        return new HlsCusPrjQuotation();
    }

    private PrjExcelImportDto setValueToExcelDto(String excelType, FndInterfaceLines fndInterfaceLines, String division) {
        PrjExcelImportDto prjExcelImportDto = new PrjExcelImportDto();

        if (StringUtils.equals(excelType, PROJECT_TENANT)) {
            prjExcelImportDto.setProjectNumber(fndInterfaceLines.getAttributes_1());
            prjExcelImportDto.setBpName(fndInterfaceLines.getAttributes_2());
            prjExcelImportDto.setShortName(fndInterfaceLines.getAttributes_3());
            prjExcelImportDto.setBpClass(fndInterfaceLines.getAttributes_4());
            prjExcelImportDto.setIdType(fndInterfaceLines.getAttributes_5());
            prjExcelImportDto.setIdCardNo(fndInterfaceLines.getAttributes_6());
            prjExcelImportDto.setGenger(fndInterfaceLines.getAttributes_7());
            prjExcelImportDto.setMaritalStatus(fndInterfaceLines.getAttributes_8());
            prjExcelImportDto.setAcademicBackground(fndInterfaceLines.getAttributes_9());
            //10学位
            prjExcelImportDto.setAcademicDegree(fndInterfaceLines.getAttributes_10());
            prjExcelImportDto.setBpNameSp(fndInterfaceLines.getAttributes_11());
            prjExcelImportDto.setIdTypeSp(fndInterfaceLines.getAttributes_12());
            prjExcelImportDto.setIdCardNoSp(fndInterfaceLines.getAttributes_13());
            prjExcelImportDto.setGenderSp(fndInterfaceLines.getAttributes_14());
            prjExcelImportDto.setActualController(fndInterfaceLines.getAttributes_15());
            prjExcelImportDto.setLegalPerson(fndInterfaceLines.getAttributes_16());
            //17法定代表人证件号码
            prjExcelImportDto.setMainMembersCertCode(fndInterfaceLines.getAttributes_17());
            prjExcelImportDto.setFoundedDate(fndInterfaceLines.getAttributes_18());
            //19居住状况
            prjExcelImportDto.setHouseProperty(fndInterfaceLines.getAttributes_19());
            prjExcelImportDto.setAddressType(fndInterfaceLines.getAttributes_20());
            prjExcelImportDto.setCountryId(fndInterfaceLines.getAttributes_21());
            prjExcelImportDto.setProvinceId(fndInterfaceLines.getAttributes_22());
            prjExcelImportDto.setCityId(fndInterfaceLines.getAttributes_23());
            prjExcelImportDto.setAddress(fndInterfaceLines.getAttributes_24());
            prjExcelImportDto.setPhone(fndInterfaceLines.getAttributes_25());
            prjExcelImportDto.setMobilePhone(fndInterfaceLines.getAttributes_26());
            //28就业状况
            prjExcelImportDto.setEmploymentStatus(fndInterfaceLines.getAttributes_27());
            //29单位名称
            prjExcelImportDto.setWorkingPlace(fndInterfaceLines.getAttributes_28());
            //30职业性质
            prjExcelImportDto.setJobNature(fndInterfaceLines.getAttributes_29());
            //31行业
            prjExcelImportDto.setIndustry(fndInterfaceLines.getAttributes_30());
            //32职业
            prjExcelImportDto.setProfession(fndInterfaceLines.getAttributes_31());
            //33职务
            prjExcelImportDto.setPosition(fndInterfaceLines.getAttributes_32());
            //34职称
            prjExcelImportDto.setJobTitle(fndInterfaceLines.getAttributes_33());
            //35存续状态
            prjExcelImportDto.setSubsistingStatus(fndInterfaceLines.getAttributes_34());
            //36组织机构类型
            prjExcelImportDto.setOrganizationType(fndInterfaceLines.getAttributes_35());
            //37营业许可证到期日
            prjExcelImportDto.setLicenseTerms(fndInterfaceLines.getAttributes_36());
            //38经济类型
            prjExcelImportDto.setCompanyNature(fndInterfaceLines.getAttributes_37());
            //39企业规模
            prjExcelImportDto.setEnterpriseScale(fndInterfaceLines.getAttributes_38());
            //40注册资本币种
            prjExcelImportDto.setRegisteredCapitalCurrency(fndInterfaceLines.getAttributes_39());
            //41注册资本（万元）
            prjExcelImportDto.setRegisteredCapital(fndInterfaceLines.getAttributes_40());
            prjExcelImportDto.setDocumentType(fndInterfaceLines.getAttributes_41());
            prjExcelImportDto.setLeaseChannel(fndInterfaceLines.getAttributes_42());
            prjExcelImportDto.setDealerName(fndInterfaceLines.getAttributes_43());
            prjExcelImportDto.setManufacturerName(fndInterfaceLines.getAttributes_44());
            prjExcelImportDto.setSignType(fndInterfaceLines.getAttributes_45());
            prjExcelImportDto.setLeaseStartDate(fndInterfaceLines.getAttributes_46());
            prjExcelImportDto.setSecondHandFlag(fndInterfaceLines.getAttributes_47());
            prjExcelImportDto.setVenderModeFlag(fndInterfaceLines.getAttributes_48());
            prjExcelImportDto.setPlanName(fndInterfaceLines.getAttributes_49());
            prjExcelImportDto.setDownPaymentMethod(fndInterfaceLines.getAttributes_50());
            prjExcelImportDto.setDownPayment(fndInterfaceLines.getAttributes_51());
            prjExcelImportDto.setDepositMethod(fndInterfaceLines.getAttributes_52());
            prjExcelImportDto.setDeposit(fndInterfaceLines.getAttributes_53());
            prjExcelImportDto.setIntRate(fndInterfaceLines.getAttributes_54());
            prjExcelImportDto.setLeaseChargeRatio(fndInterfaceLines.getAttributes_55());
            prjExcelImportDto.setGraceType(fndInterfaceLines.getAttributes_56());
            prjExcelImportDto.setGraceMonth(fndInterfaceLines.getAttributes_57());
            prjExcelImportDto.setLeaseTerm(fndInterfaceLines.getAttributes_58());
            prjExcelImportDto.setResidualValue(fndInterfaceLines.getAttributes_59());
            prjExcelImportDto.setSellName(fndInterfaceLines.getAttributes_60());
            prjExcelImportDto.setPaymentType(fndInterfaceLines.getAttributes_61());
            prjExcelImportDto.setBankDepositRatio(fndInterfaceLines.getAttributes_62());
            prjExcelImportDto.setBankLeaseChargeRatio(fndInterfaceLines.getAttributes_63());
            prjExcelImportDto.setAcceptanceTerm(fndInterfaceLines.getAttributes_64());
            prjExcelImportDto.setPayMethodDesc(fndInterfaceLines.getAttributes_65());
            prjExcelImportDto.setLeaseChargeTimes(fndInterfaceLines.getAttributes_66());
            prjExcelImportDto.setRentalPrincipalRatio1(fndInterfaceLines.getAttributes_67());
            prjExcelImportDto.setRentalPrincipalRatio2(fndInterfaceLines.getAttributes_68());
            prjExcelImportDto.setRentalPrincipalRatio3(fndInterfaceLines.getAttributes_69());
            prjExcelImportDto.setRentalPrincipalRatio4(fndInterfaceLines.getAttributes_70());
            prjExcelImportDto.setRentalPrincipalRatio5(fndInterfaceLines.getAttributes_71());
            prjExcelImportDto.setRentalPrincipalRatio6(fndInterfaceLines.getAttributes_72());
            prjExcelImportDto.setRentalPrincipalRatio7(fndInterfaceLines.getAttributes_73());
            prjExcelImportDto.setRentalPrincipalRatio8(fndInterfaceLines.getAttributes_74());
            prjExcelImportDto.setRentalPrincipalRatio9(fndInterfaceLines.getAttributes_75());
            prjExcelImportDto.setRentalPrincipalRatio10(fndInterfaceLines.getAttributes_76());
            prjExcelImportDto.setRentalPrincipalRatio11(fndInterfaceLines.getAttributes_77());
            prjExcelImportDto.setRentalPrincipalRatio12(fndInterfaceLines.getAttributes_78());
            prjExcelImportDto.setGraceTypeSpecial(fndInterfaceLines.getAttributes_79());
            prjExcelImportDto.setRentPlanNoticeFlag(fndInterfaceLines.getAttributes_80());
            prjExcelImportDto.setAppointInceptFlag(fndInterfaceLines.getAttributes_81());
            prjExcelImportDto.setPartnersContractNumber(fndInterfaceLines.getAttributes_82());
            prjExcelImportDto.setPartnersPaymentNumber(fndInterfaceLines.getAttributes_83());
            prjExcelImportDto.setManufacturerSignType(fndInterfaceLines.getAttributes_84());
            prjExcelImportDto.setVenderSignType(fndInterfaceLines.getAttributes_85());
            prjExcelImportDto.setDealerSignType(fndInterfaceLines.getAttributes_86());
            //商用车
            if (StringUtils.equals(division, COMMERCIAL_VEHICLE)) {
                //是否抵押越秀
                prjExcelImportDto.setPledgeFlag(fndInterfaceLines.getAttributes_87());
                //保险购买情况
                prjExcelImportDto.setInsuranceFlag(fndInterfaceLines.getAttributes_88());
            } else { //工程机械
                //保险购买情况
                prjExcelImportDto.setInsuranceFlag(fndInterfaceLines.getAttributes_87());
            }
        } else if (StringUtils.equals(excelType, GUARANTOR_SHEET)) {
            prjExcelImportDto.setProjectNumber(fndInterfaceLines.getAttributes_1());
            prjExcelImportDto.setBpName(fndInterfaceLines.getAttributes_2());
            prjExcelImportDto.setShortName(fndInterfaceLines.getAttributes_3());
            prjExcelImportDto.setBpClass(fndInterfaceLines.getAttributes_4());
            prjExcelImportDto.setIdType(fndInterfaceLines.getAttributes_5());
            prjExcelImportDto.setIdCardNo(fndInterfaceLines.getAttributes_6());
            prjExcelImportDto.setGenger(fndInterfaceLines.getAttributes_7());
            prjExcelImportDto.setMaritalStatus(fndInterfaceLines.getAttributes_8());
            prjExcelImportDto.setAcademicBackground(fndInterfaceLines.getAttributes_9());
            prjExcelImportDto.setBpNameSp(fndInterfaceLines.getAttributes_10());
            prjExcelImportDto.setIdTypeSp(fndInterfaceLines.getAttributes_11());
            prjExcelImportDto.setIdCardNoSp(fndInterfaceLines.getAttributes_12());
            prjExcelImportDto.setGenderSp(fndInterfaceLines.getAttributes_13());
            prjExcelImportDto.setActualController(fndInterfaceLines.getAttributes_14());
            prjExcelImportDto.setLegalPerson(fndInterfaceLines.getAttributes_15());
            prjExcelImportDto.setFoundedDate(fndInterfaceLines.getAttributes_16());
            prjExcelImportDto.setDealerType(fndInterfaceLines.getAttributes_17());
            prjExcelImportDto.setDealerRelation(fndInterfaceLines.getAttributes_18());
            prjExcelImportDto.setGuarantorSignType(fndInterfaceLines.getAttributes_19());
            prjExcelImportDto.setDealerTel(fndInterfaceLines.getAttributes_20());
        } else if (StringUtils.equals(excelType, LEASE_INSURANCE)) {
            prjExcelImportDto.setBelongProjectNum(fndInterfaceLines.getAttributes_1());
            prjExcelImportDto.setLeaseNum(fndInterfaceLines.getAttributes_2());
            prjExcelImportDto.setLeaseFullName(fndInterfaceLines.getAttributes_3());
            prjExcelImportDto.setLeaseShortName(fndInterfaceLines.getAttributes_4());
            if (StringUtils.equals(division, COMMERCIAL_VEHICLE)) {
                //5-车辆类型
                prjExcelImportDto.setTrailerFlag(fndInterfaceLines.getAttributes_5());
                //6-租赁物净价
                prjExcelImportDto.setNetPrice(fndInterfaceLines.getAttributes_6());
                //7-购置税
                prjExcelImportDto.setPurchaseTax(fndInterfaceLines.getAttributes_7());
                //8-保险费
                prjExcelImportDto.setInsurancePremium(fndInterfaceLines.getAttributes_8());
                //9-配件费
                prjExcelImportDto.setAccessoryFee(fndInterfaceLines.getAttributes_9());
                //10-租赁物总价
                prjExcelImportDto.setPrice(fndInterfaceLines.getAttributes_10());
                //11-品牌
                prjExcelImportDto.setBrandC(fndInterfaceLines.getAttributes_11());
                //12-车系
                prjExcelImportDto.setSeriesC(fndInterfaceLines.getAttributes_12());
                //13-车型
                prjExcelImportDto.setModelC(fndInterfaceLines.getAttributes_13());
                //14-发动机号
                prjExcelImportDto.setEngineNumber(fndInterfaceLines.getAttributes_14());
                //15-车架号
                prjExcelImportDto.setFrameNumber(fndInterfaceLines.getAttributes_15());
                //16-车牌号
                prjExcelImportDto.setLicensePlateNumber(fndInterfaceLines.getAttributes_16());
                //17-拟上牌地（省）
                prjExcelImportDto.setProvinceCode(fndInterfaceLines.getAttributes_17());
                //18-拟上牌地（市）
                prjExcelImportDto.setCityCode(fndInterfaceLines.getAttributes_18());
                //19-备注
                prjExcelImportDto.setLeaseDescription(fndInterfaceLines.getAttributes_19());
                //20-保险类型
                prjExcelImportDto.setInsuranceType(fndInterfaceLines.getAttributes_20());
                //21-保险起始日
                prjExcelImportDto.setInsuranceDateFrom(fndInterfaceLines.getAttributes_21());
                //22-保险到期日
                prjExcelImportDto.setInsuranceDateTo(fndInterfaceLines.getAttributes_22());
                //23-保险公司
                prjExcelImportDto.setInsuranceCompany(fndInterfaceLines.getAttributes_23());
                //24-保单编号
                prjExcelImportDto.setInsuranceNumber(fndInterfaceLines.getAttributes_24());
                //25-投保金额
                prjExcelImportDto.setInsuranceAmount(fndInterfaceLines.getAttributes_25());
                //26-第一受益人
                prjExcelImportDto.setFirstBeneficiary(fndInterfaceLines.getAttributes_26());

            } else {
                prjExcelImportDto.setSpecification(fndInterfaceLines.getAttributes_5());
                prjExcelImportDto.setSerialNumber(fndInterfaceLines.getAttributes_6());
                prjExcelImportDto.setLeasePrice(fndInterfaceLines.getAttributes_7());
                prjExcelImportDto.setLeaseDescription(fndInterfaceLines.getAttributes_8());
                prjExcelImportDto.setInsuranceType(fndInterfaceLines.getAttributes_9());
                prjExcelImportDto.setInsuranceDateFrom(fndInterfaceLines.getAttributes_10());
                prjExcelImportDto.setInsuranceDateTo(fndInterfaceLines.getAttributes_11());
                prjExcelImportDto.setInsuranceCompany(fndInterfaceLines.getAttributes_12());
                prjExcelImportDto.setInsuranceNumber(fndInterfaceLines.getAttributes_13());
                prjExcelImportDto.setInsuranceAmount(fndInterfaceLines.getAttributes_14());
                prjExcelImportDto.setFirstBeneficiary(fndInterfaceLines.getAttributes_15());
            }
        }

        return prjExcelImportDto;
    }

    String getExceptionInfo(String sheetName, int rownum, int colnum, String message) {
        StringBuffer sb = new StringBuffer();
        sb.append(sheetName);
        sb.append(IPrjProjectService.ORDER_DESC);
        sb.append(rownum);
        sb.append(IPrjProjectService.ROW_DESC);
        sb.append("，");
        sb.append(IPrjProjectService.ORDER_DESC);
        sb.append(colnum);
        sb.append(IPrjProjectService.COL_DESC);
        sb.append(message);
        return sb.toString();
    }

    /**
     * 进件生成签约附件清单
     *
     * @param iRequest
     * @param projectId
     */
    @Override
    public void generateProjectSignAttach(IRequest iRequest, Long projectId) throws HlsCusException {
//        //查询相应的进件单据
//        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(projectId);
//        if(Objects.isNull(hlsCusPrjProject)){
//            throw new HlsCusException("未查询到相应的进件单据，请联系管理员！");
//        }
//
//        //厂商租赁生成签约附件清单
//        if(StringUtils.equals(hlsCusPrjProject.getLeaseChannel(), MANUFACTURER_CODE)){
//            Boolean generateFlag = true;
//            //判断是否已经生成签约附件清单
//            HlsCusPrjProjectAttachment prjProjectAttachment = new HlsCusPrjProjectAttachment();
//            prjProjectAttachment.setProjectId(projectId);
//            prjProjectAttachment.setProjectAttachmentCategory(PRJ_PROJECT_ATTACHMENT);
//            List<HlsCusPrjProjectAttachment> attachmentList = prjProjectAttachmentMapper.select(prjProjectAttachment);
//
//            //判断已有的附件中是否存在签约附件清单相关
//            if(CollectionUtils.isNotEmpty(attachmentList)){
//                Optional<HlsCusPrjProjectAttachment> first = attachmentList.stream().filter(attachment -> attachment.getSourceId() != null).findFirst();
//                if(first.isPresent()){
//                    HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = first.get();
//                    //获取模板相关信息
//                    PrjCddItemTempletHd prjCddItemTempletHd = prjCddItemTempletHdMapper.selectByPrimaryKey(hlsCusPrjProjectAttachment.getSourceId());
//                    if(!Objects.isNull(prjCddItemTempletHd)){
//                        generateFlag = false;
//                    }
////                    HlsDocFileTemplet hlsDocFileTemplet = hlsDocFileTempletMapper.selectByPrimaryKey(hlsCusPrjProjectAttachment.getSourceId());
////                    if(!Objects.isNull(hlsDocFileTemplet) && StringUtils.equals(hlsDocFileTemplet.getTempletType(),TEMPLATE_TYPE)){
////                        generateFlag = false;
////                    }
//                }
//            }
//
//            //生成签约附件清单
//            if(generateFlag) {
////                self().queryProjectAttachListByProjectId(iRequest, projectId, PRJ_PROJECT_ATTACHMENT);
//            }
//        }
//        if(StringUtils.equals(hlsCusPrjProject.getLeaseChannel(), CAR_CODE)) {
//            //汽车租赁插入“审查文件”附件目录
//            HlsCusPrjProjectAttachment projectAttachment = new HlsCusPrjProjectAttachment();
//            projectAttachment.setProjectId(projectId);
//            projectAttachment.setProjectAttachmentCategory("prj_project_attachment");
//            projectAttachment.setDocumentName("审查文件");
//            if (prjProjectAttachmentMapper.select(projectAttachment).size()==0){
//                iPrjProjectAttachmentService.insertSelective(iRequest, projectAttachment);}
//        }
    }

    /**
     * 根据厂商获取生成的资料清单
     *
     * @param projectId
     * @param TableName
     */
//    public void queryProjectAttachListByProjectId(IRequest iRequest,Long projectId, String TableName) {
//        //获取模板集合
//        List<PrjCddItemTempletHd> list = prjCddItemTempletHdMapper.queryProjectAttachListByProjectId(projectId);
//
//        //判断模板集合是否存在
//        if(CollectionUtils.isNotEmpty(list)){
//            //生成附件列表
//            list.stream().forEach(prjCddItemTempletHd -> {
//                HlsCusPrjProjectAttachment projectAttachment = new HlsCusPrjProjectAttachment();
//                projectAttachment.setProjectId(projectId);
//                projectAttachment.setProjectAttachmentCategory(TableName);
//                projectAttachment.setDocumentName(prjCddItemTempletHd.getTempletDesc());
//                projectAttachment.setSourceId(prjCddItemTempletHd.getTempletId());
//                projectAttachment.setSourceRuleId(prjCddItemTempletHd.getRuleId());
//                iPrjProjectAttachmentService.insertSelective(iRequest, projectAttachment);
//            });
//        }
//    }

    /**
     * 校验数据是否真的有更新
     *
     * @param obj1       用于更新的新数据，空的字段不更新所以不校验
     * @param obj2       现在表中的数据
     * @param ignoreList 不需要校验的字段
     * @return 是否重复
     */
    private boolean checkDiffer(Object obj1, Object obj2, List<String> ignoreList) {
        try {
            // 只有两个对象都是同一类型的才有可比性
            if (obj1.getClass() == obj2.getClass()) {
                // 获取object的属性描述
                BeanInfo beanInfo = Introspector.getBeanInfo(obj1.getClass());
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor pd : pds) {
                    String name = pd.getName();
                    //不存表的字段不校验（Transient注解）
                    try {
                        if (obj1.getClass().getDeclaredField(pd.getName()).getAnnotation(Transient.class) != null) {
                            continue;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                    // 忽略的字段不校验
                    if (ignoreList != null && ignoreList.contains(name)) {
                        continue;
                    }
                    Method readMethod = pd.getReadMethod();
                    // 在obj1上调用get方法等同于获得obj1的属性值
                    Object o1 = readMethod.invoke(obj1);
                    // 在obj2上调用get方法等同于获得obj2的属性值
                    Object o2 = readMethod.invoke(obj2);
                    //新字段空的不校验
                    if (o1 == null) {
                        continue;
                    }
                    //o1是空字符串不校验
                    if (o1 instanceof String && StringUtils.isEmpty((String) o1)) {
                        continue;
                    }
                    if (o2 == null) {
                        return true;
                    }
                    if (!o1.equals(o2)) {
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public List<FndAttachmentMulti> contextCreateMultiple(IRequest request, List<Long> list, HttpServletResponse response) throws Exception {

        List<FndAttachmentMulti> fndAttachmentMultiList1 = new ArrayList<>();
        for (Long projectId : list) {
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(projectId);
            hlsCusPrjProject = self().selectByPrimaryKey(request, hlsCusPrjProject);
            HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
            hlsBpMaster.setBpId(hlsCusPrjProject.getTenantId());
            hlsBpMaster = hlsBpMasterMapper.selectByPrimaryKey(hlsBpMaster);
            HlsDocFileTemplet templet = new HlsDocFileTemplet();
            if (!"ORG".equals(hlsBpMaster.getBpClass())) {
                templet.setTempletCode("INTO_BUSINESS_CONFIRMATION");
            } else {
                templet.setTempletCode("INTO_BUSINESS_CONFIRMATION2");
            }
//            HlsDocFileTemplet templet = new HlsDocFileTemplet();
//            templet.setTempletCode("INTO_BUSINESS_CONFIRMATION");
            List<HlsDocFileTemplet> docFileTemplets = hlsDocFileTempletMapper.select(templet);
            if (docFileTemplets.size() != 1) {
                throw new hls.core.utils.exception.HlsCusException("不存在模板,请配置");
            }
            Long templetId = docFileTemplets.get(0).getTempletId();
            FndAttachmentMulti fileParam = new FndAttachmentMulti();
            fileParam.setTableName("hls_doc_file_templet");
            fileParam.setTablePkValue(templetId.toString());
            FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(request, fileParam).get(0);
            FndAttachment templateFileParam = new FndAttachment();
            templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
            templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
            List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(request, templateFileParam);
            Validate.notEmpty(fndAttachments, "文件模版不存在");

            FndAttachment sysFile = fndAttachments.get(0);
            if (sysFile == null || org.apache.commons.lang.StringUtils.isBlank(sysFile.getFilePath())) {
                throw new hls.core.utils.exception.HlsCusException("文件模版不存在");
            }
            File file = new File(sysFile.getFilePath());
            if (!file.exists()) {
                throw new hls.core.utils.exception.HlsCusException("文件模版不存在");
            }
            InputStream inStream = new FileInputStream(file);
            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
            //复制模板
            HlsCusDownloadDocxUtil.copyModel(copyPath, inStream);
            //用输入流读取复制后的模板
            InputStream modelIs = new FileInputStream(copyPath);
            Map<String, Object> params = new HashMap<>();
            params.put("templetId", templetId);
            params.put("projectId", projectId);
            //生成合同文本
            String userName = hlsBpMaster.getBpName();
            HlsCusDownloadDocxUtil.createDocx(request, modelIs, new File(copyPath), params);
            FndAttachmentMulti fndAttachmentMulti = insertAtm(request, copyPath, userName + "业务确认函", projectId);
            fndAttachmentMultiList1.add(fndAttachmentMulti);
//            List<BusinessConfirm> businessConfirmList=businessConfirmMapper.query(confirmBatch.getBatchId());
//            for(BusinessConfirm businessConfirm: businessConfirmList){
//                HlsCusConContract hlsCusConContract= hlsCusConContractMapper.selectByPrimaryKey(businessConfirm.getContractId());
//                hlsCusConContract.setBusinessConfirmStatus("CREATED");
//                hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
//                BusinessConfirm businessConfirm1=businessConfirmMapper.selectByPrimaryKey(businessConfirm.getConfirmId());
//                businessConfirm1.setStatus("CREATED");
//                businessConfirmMapper.updateByPrimaryKeySelective(businessConfirm1);
//            }
        }

//        Long[] attachmentIds = new Long[fndAttachmentMultiList.size()];
//        for (int i = 0; i < fndAttachmentMultiList.size(); i++) {
//            attachmentIds[i] = fndAttachmentMultiList.get(i);
//        }
//        fndAttachmentService1.batchDownloadAttachment(request,attachmentIds,response);
        return fndAttachmentMultiList1;
    }

    private FndAttachmentMulti insertAtm(IRequest currentRequest, String filePath, String fileName, Long lnId) {
        File file = new File(filePath);
        //插入附件表
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTableName("con_confirm_batch");
        fndAttachmentMulti.setTablePkValue(lnId.toString());
        fndAttachmentMulti = fndAttachmentMultiService.insertSelective(currentRequest, fndAttachmentMulti);
        FndAttachment fndAttachment = new FndAttachment();
        fndAttachment.setSourceTypeCode("fnd_atm_attachment_multi");
        fndAttachment.setSourcePkValue(fndAttachmentMulti.getRecordId().toString());
        fndAttachment.setFileTypeCode("docx");
        fndAttachment.setMimeType("application/msword");
        fndAttachment.setFileName(fileName + ".docx");
        fndAttachment.setFileSize(file.length());
        fndAttachment.setFilePath(filePath);
        fndAttachmentService.insertSelective(currentRequest, fndAttachment);
        fndAttachmentMulti.setAttachmentId(fndAttachment.getAttachmentId());
        fndAttachmentMultiService.updateByPrimaryKeySelective(currentRequest, fndAttachmentMulti);
        return fndAttachmentMulti;
    }

    @Override
    public List<HlsCusPrjProject> manufacturerQueryProductInfo2(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return hlsCusPrjProjectMapper.manufacturerQueryProductInfo2(hlsCusPrjProject);
    }

    @Override
    public List<HlsCusPrjProject> manufacturerQueryProductInfo3(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return hlsCusPrjProjectMapper.manufacturerQueryProductInfo3(hlsCusPrjProject);
    }

    @Override
    public List<HlsEmployee> selectSalesByEmployeeName(IRequest iRequest, String name, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<HlsEmployee> hlsEmployees = userMapper.selectSalesByEmployeeName(name);
        return hlsEmployees;
    }

    /**
     * 当前登录用户信息获取
     *
     * @return
     */
    @Override
    public List<Map> queryUserInfo(IRequest iRequest, Map params) {
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        String userId = paramJson.getString("userId");
        return hlsCusPrjProjectMapper.queryUserInfo(userId);
    }

    @Override
    public String allLeaseItemCheck(String itemNumber, String division, String contractNumber, String classifyId) {
        String columnName;
        if (COMMERCIAL_VEHICLE.equals(division)) {
            //如果是商用车，校验FRAME_NUMBER车架号
            columnName = "frame_number";
        } else {
            columnName = "serial_number";
        }
        return prjProjectMapper.checkLeaseItemCheck(itemNumber, columnName, contractNumber, classifyId);
    }

    @Override
    public List<Map> queryListForRpt(IRequest requestContext, Map<String, Object> project, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsCusPrjProjectMapper.queryListForRPT(project);
    }

    @Override
    public List<Map> prjProcessInfoQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsCusPrjProjectMapper.prjProcessExportQuery(project);
    }

    @Override
    public HlsCusPrjProjectService self() {
        return HlsCusPrjProjectService.super.self();
    }
}
