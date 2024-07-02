package com.hand.hls.prj.service.impl;

import cfca.sadk.algorithm.common.PKIException;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.activiti.exception.TaskActionException;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DistributedLockFactoryBean;
import com.hand.hap.system.mapper.CodeValueMapper;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.*;
import com.hand.hls.bp.dto.FndScoreTemplateHd;
//import com.hand.hls.bp.dto.HlsBpMaster;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.*;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.IFndScoreTemplateHdService;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;

import com.hand.hls.cont.mapper.ConContractAttachmentMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.*;

import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;

import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fin.mapper.BpMasterAttachmentMapper;
import com.hand.hls.fnd.components.Datasource2Json;
import com.hand.hls.fnd.dto.*;
import com.hand.hls.fnd.mapper.*;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.fnd.service.IInterfaceErrorMsgService;

import com.hand.hls.lease.dto.YxLeaseItemClassify;
import com.hand.hls.lease.mapper.YxLeaseItemClassifyMapper;
import com.hand.hls.prj.components.ExcelUtils;
import com.hand.hls.prj.components.SynPrjInfoConstants;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.*;

import com.hand.hls.prj.utils.IDUtils;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import com.hand.hls.ruleengine.service.IRuleEngineTypeService;

import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.mapper.FndOrgUnitMapper;
import com.hand.hls.sys.service.IFndEmployeeAssignsService;
import com.hand.hls.sys.service.ISysDocumentListService;

import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.wfl.service.IActivitiStartService;

import com.hand.hls.wsdl.dto.*;
import leaf.service.validation.ParameterNullException;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hand.hls.utils.HlsConstantUtil.BaseController.Y;

/**
 * description: 进件签约
 * author: lrd
 * create: 2019-08-09
 **/

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjProjectServiceImpl extends BaseServiceImpl<HlsCusPrjProject> implements IPrjProjectService {

    private static final String PROJECT_STATUS = "APPROVING";
    private static final String CANCEL = "CANCEL";
    private static final String PACKAGE_REJECTED = "PACKAGE_REJECTED";
    private static final String PROJECT_WORK_FLOW_TYPE = "PRJ_PROJECT";
    private static final String PRJ_PROJECT = "PRJ_PROJECT";
    private static final String SIGN_WORK_FLOW_TYPE = "SIGN_WFL";
    private static final String SIGN_FINANCE_WORK_FLOW_TYPE = "PROJECT_REVIEW_WORK_FLOW";
    private static final String PROJECT = "project";
    private static final String PROJECT_NAME = "projectName";
    private static final String DOCUMENT_CATEGORY = "documentCategory";
    private static final String DOCUMENT_TYPE = "documentType";
    private static final String DOCUMENT_ID = "documentId";
    private static final String WORKFLOW_TYPE = "workFlowType";
    private static final String DOCUMENT_NAME = "documentName";
    private static final String DOCUMENT_NUMBER = "documentNumber";
    private static final String TEMPLET_ID = "templetId";
    private static final String PROJECT_ID = "projectId";
    private static final String PROJECT_ATTACHMENT_ID = "projectAttachmentId";
    private static final String BP_ID = "projectAttachmentId";
    private static final Long READ_LINE = 0L;
    private static final String PARAM_NOT_FOUND = "参数未找到";
    private static final String[] PAY_METHOD_DESCS = {"租金比例-指定期数", "本金比例-指定期数", "租金比例-指定月份", "本金比例-指定月份", "等额本息（宽限期延息_均摊）", "等额本金（宽限期延息_均摊）"};
    private static final String[] PAY_METHOD_DESCS_MONTHS = {"租金比例-指定月份", "本金比例-指定月份"};
    private static final String PROJECT_TENANT_SHEET = "承租人&进件基本信息";
    private static final String LEASE_INSURANCE_SHEET = "租赁物&保险信息";
    private static final String GUARANTOR_SHEET = "担保人信息";
    private static final String PROJECT_TENANT = "PROJECT_TENANT";
    private static final String LEASE_INSURANCE = "LEASE_INSURANCE";
    private static final String GUARANTOR = "GUARANTOR";
    private static final String LEASE_CHANNEL_MANUFACTORY = "10";
    private static final String RISK_REVIEW_WORK_FLOW = "RISK_REVIEW_WORK_FLOW";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String LEASE_CHANNEL = "leaseChannel";
    //中联重科 商业伙伴code
    private static final String ZLZK_BP_CODE = "C000030601";
    private static final String PROJRCT_SIGN_FLAG_N = "N";
    private static final String CERTIFICATION_CALL = "CERTIFICATION_CALL";
    //保险单模板代码
    private static final String SIGN_INSURANCE_FILE = "SIGN_INSURANCE_FILE";
    //收款确认书
    private static final String SIGN_PAYMENT_CONFIRM = "SIGN_PAYMENT_CONFIRM";
    //租赁物权属文件
    private static final String SIGN_PRODUCT_QUALIFIED = "SIGN_PRODUCT_QUALIFIED";
    //承租人其他资料
    private static final String TENENT_FILE_OTHERE = "TENENT_FILE_OTHERE";
    //签约方式-线上
    private static final String SIGN_ONLINE = "SIGN_ONLINE";
    //工程机械
    private static final String ENGINEERING_MACHINERY = "ENGINEERING_MACHINERY";
    //商用车
    private static final String COMMERCIAL_VEHICLE = "COMMERCIAL_VEHICLE";
    //商用车
    private static final String NOT_COMMERCIAL_VEHICLE = "NOT_COMMERCIAL_VEHICLE";
    //承租人身份证正/反面复印件
    private static final String SIGN_ID_CARD = "SIGN_ID_CARD";
    //结婚证/户口本（二选一）
    private static final String MARRYIAGE_REGISTER = "MARRYIAGE_REGISTER";
    //婚姻证明
    private static final String MARRIAGE_CERTIFICATE = "MARRIAGE_CERTIFICATE";
    //原始购买合同
    private static final String SIGN_PURCHASE_CONTRACT = "SIGN_PURCHASE_CONTRACT";
    //机动车销售发票
    private static final String VEHICLES_INVOICE = "VEHICLES_INVOICE";
    //车辆合格证/登记证书（二选一）
    private static final String VEHICLE_CERTIFICATE = "VEHICLE_CERTIFICATE";
    //车辆挂靠协议
    private static final String AFFILIATED_AGREEMENT = "AFFILIATED_AGREEMENT";
    //登记证书
    //private static final String
    //经销商业务确认函
    private static final String DEALER_BUSINESS_CONFIRM = "DEALER_BUSINESS_CONFIRM";
    //厂商业务确认函
    private static final String MANUFACTURER_BUSINESS_CONFIRM = "MANUFACTURER_BUSINESS_CONFIRM";
    private static final String MARRIED = "MARRIED";
    //未婚
    private static final String UNMARRIED = "UNMARRIED";
    //证件类型（身份证）
    private static final String ID_CARD = "ID_CARD";
    //证件类型（身份证）
    private static final String DIVISION = "DIVISION";
    //换行符
    private static final String BR = "<br>";
    //前台展示的最大报错的行数
    private static final int ERROR_LINES = 10;
    private static final String FLAG = "FLAG";
    /**
     * 不能包含特殊字符
     */
    private static final Pattern PATTERN_CHARACTER_FIND = Pattern.compile("[-@#￥%&~^]");
    /**
     * 不能包含看不见的特殊字符
     */
    private static final Pattern PATTERN_INVISIBLE_CHARACTER_FIND = Pattern.compile("\\s");
    //光伏设备业务线：PHOTOVOLTAIC_DEVICES
    private static final String PHOTOVOLTAIC_DEVICES = "PHOTOVOLTAIC_DEVICES";

    private static final String INSURANCE_PURCHASE_STATUS = "INSURANCE_PURCHASE_STATUS";

    private static final String NEW_INSURANCE_TYPE = "NEW_INSURANCE_TYPE";


    private static final String APPROVING = "APPROVING";
    private static final String APPROVED = "APPROVED";
    //合同文本状态待审核
    private static final String UNAUDITED  = "UNAUDITED";
    public static final String FND_BP_CLASS = "FND.BP_CLASS";
    public static final String SUBSISTING_STATUS = "SUBSISTING_STATUS";
    public static final String ENTERPRISE_SCALE_TYPE = "ENTERPRISE_SCALE_TYPE";
    public static final String CHS_CURRENCY_TYPE = "CHS.CURRENCY_TYPE";
    public static final String PAYMENT_BP_TYPE = "PAYMENT_BP_TYPE";
    public static final String NEW_LOCOMOTIVE = "NEW_LOCOMOTIVE";
    public static final String OLD_LOCOMOTIVE = "OLD_LOCOMOTIVE";
    @Value("${file.upload.dir:.}")
    private String savePath = ".";

    private static final Pattern patternNumber = Pattern.compile("[^0-9]");

    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private IPrjProjectService prjProjectService;
    @Autowired
    private ICodeService iCodeService;

    @Autowired
    HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsBpMasterMainMembersMapper hlsBpMasterMainMembersMapper;
    @Autowired
    HlsCusPrjQuotationMapper prjQuotationMapper;

    @Autowired
    HlsCusPrjProjectLeaseItemMapper prjProjectLeaseItemMapper;

    @Autowired
    PrjProjectBpMapper prjProjectBpMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private PrjLeaseItemInsuranceMapper prjLeaseItemInsuranceMapper;

    @Autowired
    FndCompanyMapper fndCompanyMapper;

    @Autowired
    FndEmployeeMapper fndEmployeeMapper;

    @Autowired
    FndOrgUnitMapper fndOrgUnitMapper;
    @Autowired
    HlsBpMasterMapper hlsBpMasterMapper;
    @Autowired
    HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    IPrjProjectBpService prjProjectBpService;

    @Autowired
    private ExcelUtils excelUtils;

    @Autowired
    HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    IPrjChanceService prjChanceService;
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private HlsCodeValueMapper codeValueMapper2;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private DocumentTypeMapper documentTypeMapper;

    @Autowired
    private BusinessTypeMapper businessTypeMapper;

    @Autowired
    private HlsCalcConfigMapper hlsCalcConfigMapper;


    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    @Autowired
    private IPrjProjectLeaseItemService prjProjectLeaseItemService;


    @Autowired
    private HlsProductDefinitionParaMapper hlsProductDefinitionParaMapper;

    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;

    @Autowired
    private IDocFileTempletRuleService docFileTempletRuleService;

    @Autowired
    private IRuleEngineTypeService ruleEngineTypeService;

    @Autowired
    private Datasource2Json datasource2Json;

    @Autowired
    private IHLSRuleEngineInitService hlsRuleEngineInitService;

    @Autowired
    private IPrjProjectAttachmentService prjProjectAttachmentService;

    @Autowired
    private IConContractAttachmentService conContractAttachmentService;

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    @Autowired
    private YxLeaseItemClassifyMapper yxLeaseItemClassifyMapper;



    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;

    @Autowired
    private ConContractAttachmentMapper conContractAttachmentMapper;

    @Autowired
    private HlsCusPrjProjectAttachmentMapper prjProjectAttachmentMapper;



    @Autowired
    private ISysDocumentListService sysDocumentListService;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private HlsCreditLineService hlsCreditLineService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HlsCusBpMasterRelationMapper hlsBpMasterRelationMapper;

    @Autowired
    private BpMasterAgreementMapper bpMasterAgreementMapper;

    @Autowired
    private FndLeaseChannelMapper fndLeaseChannelMapper;

    @Autowired
    private HlsBpMasterService hlsBpMasterService;

    @Autowired
    private HlsBpMasterRoleMapper hlsBpMasterRoleMapper;

    @Autowired
    private HlsBpMasterAddressMapper hlsBpMasterAddressMapper;

    @Autowired
    private FndCountryMapper fndCountryMapper;

    @Autowired
    private FndProvinceMapper fndProvinceMapper;

    @Autowired
    private FndCityMapper fndCityMapper;
    @Autowired
    private BpMasterReplyMapper bpMasterReplyMapper;

    @Autowired
    private IBpMasterReplyService iBpMasterReplyService;

    @Autowired
    private ZxBpOrgbaseMapper zxBpOrgbaseMapper;

    @Autowired
    private IFndEmployeeAssignsService fndEmployeeAssignsService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private HlsBpMasterBankAccountMapper hlsBpMasterBankAccountMapper;



    @Autowired
    private BpCategoryMapper bpCategoryMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;



    @Autowired
    private IPrjQuotationService prjQuotationService;

    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private IVirtualAccountService virtualAccountService;
    @Autowired
    private DivisionMapper divisionMapper;
    @Autowired
    private IBpMasterChangeReqService bpMasterChangeReqService;

    @Autowired
    private HlsCusCshPaymentReqHdMapper cshPaymentReqHdMapper;

    @Autowired
    private HlsPriceListConfigLnMapper hlsPriceListConfigLnMapper;
    @Autowired
    private FndCodingRuleMapper codingRuleMapper;
    @Autowired
    private FndCodingRuleDetailsMapper codingRuleDetailsMapper;

    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;


    @Autowired
    private IConContractLeaseItemService conContractLeaseItemService;



    @Autowired
    private IFndScoreTemplateHdService iFndScoreTemplateHdService;

    @Autowired
    private IHlsScoreCalculationService hlsScoreCalculationService;

    @Autowired
    private ICodeService codeService;


    @Autowired
    private DistributedLockFactoryBean distributedLockFactoryBean;
    @Autowired
    private IInterfaceErrorMsgService interfaceErrorMsgService;

    @Autowired
    private IHlsBpMasterInceptRuleService iHlsBpMasterInceptRuleService;

    @Autowired
    private SqlSessionFactory sessionFactory;



    @Autowired
    private BpMasterAttachmentMapper bpMasterAttachmentMapper;



    private static final String BP_CODE = "越租第";
    private static final String FND_ATTACHMENT_ID = "fnd_attachment_id";



    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public String projectBatchSubmit(IRequest iRequest, String[] projectIdList) {
        StringBuilder errorMessage = new StringBuilder();
        StringBuilder submitMessage = new StringBuilder();
        int errorNum = 0;
        Map<String, String> itemMap = new HashMap<>(16);
        //预先存储所有的租赁物用于校验
        getAllItems(projectIdList, itemMap);
        for (String s : projectIdList) {
            HlsCusPrjProject project = prjProjectMapper.selectByPrimaryKey(Long.valueOf(s));
            String errorStr = prjProjectService.submitTask(iRequest, project, itemMap);
            if (StringUtils.isNotEmpty(errorStr)) {
                errorNum++;
                //报错的行数超过十行省略
                substrErrorMsg(errorMessage, project, errorStr);
            }
        }
        String errorMessageStr = errorMessage.toString();
        if (StringUtils.isNotEmpty(errorMessageStr) && (errorMessageStr.lastIndexOf(BR) + BR.length() == errorMessageStr.length())) {
            errorMessageStr = errorMessageStr.substring(0, errorMessageStr.lastIndexOf(BR));
        }
        submitMessage.append("本次提交").append(projectIdList.length - errorNum).append("条单据成功，")
          .append(errorNum).append("条单据失败。失败信息为：").append(BR).append(errorMessageStr);
        return submitMessage.toString();
    }

    private String leaseItemCheck(HlsCusPrjProject project, Map<String, String> itemMap) {
        Long projectId = project.getProjectId();
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(projectId);
        String division = project.getDivision();
        //汽车租赁的都用商用车的校验逻辑
        if ("20".equals(project.getLeaseChannel())) {
            division = COMMERCIAL_VEHICLE;
        }

        String result = null;
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = prjProjectLeaseItemMapper.select(hlsCusPrjProjectLeaseItem);
        for (HlsCusPrjProjectLeaseItem prjProjectLeaseItem : hlsCusPrjProjectLeaseItemList) {
            String itemNumber;
            if (COMMERCIAL_VEHICLE.equals(division)) {
                itemNumber = prjProjectLeaseItem.getFrameNumber();
            } else {
                itemNumber = prjProjectLeaseItem.getSerialNumber();
            }

//            String classifyId = String.valueOf(prjProjectLeaseItem.getClassifyId());
            String contractNumber = null;
            if (StringUtils.isNotEmpty(itemNumber)) {
                if (itemMap.containsKey(project.getProjectNumber() + itemNumber)) {
                    contractNumber = itemMap.get(project.getProjectNumber() + itemNumber);
                }
                if (StringUtils.isEmpty(contractNumber)) {
//                    contractNumber = allLeaseItemCheck(itemNumber, division, null, classifyId);
                }
                if (StringUtils.isNotEmpty(contractNumber)) {
                    if (StringUtils.isEmpty(result)) {
                        result = "合同与" + contractNumber + "合同车架号/(规格型号+整机编号)重复，重复号码：" + itemNumber + "。";
                    } else {
                        result = result + BR + project.getProjectNumber() + "合同与" + contractNumber + "合同车架号/(规格型号+整机编号)重复，重复号码：" + itemNumber + "。";
                    }
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String submitTask(IRequest iRequest, HlsCusPrjProject project, Map<String, String> itemMap) {
        String result;
        //检查机械编号重复
//        result = leaseItemCheck(project, itemMap);
//        if (StringUtils.isNotEmpty(result)) {
//            return result;
//        }
        //提交前校验进件基本信息
        result = beforeSubmitCheckProjectInfo(project);
        if (StringUtils.isNotEmpty(result)) {
            return result;
        }
        //校验是否报价成功
        result = calculateQuotation(project);
        if (StringUtils.isNotEmpty(result)) {
            return result;
        }
        //调用提交审批方法
        try {
            self().projectWflSubmit(iRequest, project);
        } catch (UnexpectedRollbackException e) {
            logger.error("submitTask", e);
        } catch (Exception e) {
            logger.error("进件审批失败", e);
            return e.getMessage();
        }
        //自动评分
        self().autoScore(iRequest, project.getProjectId(), "PRJ_PROJECT", "N");
        return null;
    }

    /**
     * 厂商租赁校验字段是否有值
     */
    private String calculateQuotation(HlsCusPrjProject project) {
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowMapper.queryPrjQuotationCashflowDetailByProjectId1(project.getProjectId());
        if(hlsCusPrjQuotationCashflows.size()<=0){
            return new StringBuffer("报价计算不成功").toString();
        }
        return null;
    }

    /**
     * 厂商租赁校验字段是否有值
     */
    private String checkManufactoryBaseInfo(HlsCusPrjProject project) {
        //是否二手机
        if (StringUtils.isEmpty(project.getSecondHandFlag())) {
            return new StringBuffer("是否二手机为空！").toString();
        }
        //是否经销商模式
//        if (StringUtils.isEmpty(project.getBpIdVender())) {
//            return new StringBuffer("是否经销商模式为空！").toString();
//        }
        //投放日
        if (null == project.getLeaseStartDate()) {
            return new StringBuffer("投放日为空！").toString();
        }
        //出卖人
        if (null == project.getSellerId()) {
            return new StringBuffer("出卖人为空！").toString();
        }
        // 签约方式（线上签约，线下签约）
        if (null == project.getSignType()) {
            return new StringBuffer("合同未维护签约方式！").toString();
        }
        return null;
    }

    /**
     * 提交审批前校验进件信息
     */
    private String beforeSubmitCheckProjectInfo(HlsCusPrjProject project) {
        //查询单据是否有承租人
        HlsCusPrjProjectBp prjProjectBp = new HlsCusPrjProjectBp();
        prjProjectBp.setProjectId(project.getProjectId());
        prjProjectBp.setBpCategroy(TENANT);
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBps = hlsCusPrjProjectBpMapper.select(prjProjectBp);
//        if (CollectionUtils.isEmpty(hlsCusPrjProjectBps)) {
//            return new StringBuffer("未维护承租人信息！").toString();
//        }

        //查询单据是否存在报价
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setSourceDocumentId(project.getProjectId());
        quotation.setSourceDocumentCategory(PRJ_PROJECT);
        List<HlsCusPrjQuotation> hlsCusPrjQuotations = prjQuotationMapper.select(quotation);
        if (CollectionUtils.isEmpty(hlsCusPrjQuotations)) {
            return new StringBuffer("未维护报价信息！").toString();
        }

        //查询付款对象字段是否有值
        if (null == project.getPaymentBpId()) {
            return new StringBuffer("未维护报价信息！").toString();
        }

        //若为厂商租赁则还需要校验一些必填字段是否有值
        if (StringUtils.equals(project.getLeaseChannel(), LEASE_CHANNEL_MANUFACTORY)) {
            return checkManufactoryBaseInfo(project);
        }
        return null;
    }

    public void getAllItems(String[] projectIdList, Map<String, String> itemMap) {
        for (String s : projectIdList) {
            HlsCusPrjProject project = prjProjectMapper.selectByPrimaryKey(Long.valueOf(s));
            Long projectId = project.getProjectId();
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
            hlsCusPrjProjectLeaseItem.setProjectId(projectId);
            String division = project.getDivision();
            if ("20".equals(project.getLeaseChannel())) {
                division = COMMERCIAL_VEHICLE;
            }
            List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = prjProjectLeaseItemMapper.select(hlsCusPrjProjectLeaseItem);
            for (HlsCusPrjProjectLeaseItem leaseItem : hlsCusPrjProjectLeaseItemList) {
                String itemNumber;
                String itemNumberType;
                if (COMMERCIAL_VEHICLE.equals(division)) {
                    itemNumber = leaseItem.getFrameNumber();
                    itemNumberType = itemNumber + COMMERCIAL_VEHICLE;
                } else {
                    itemNumber = leaseItem.getSerialNumber();
                    itemNumberType = itemNumber + NOT_COMMERCIAL_VEHICLE;
                }
                if (StringUtils.isNotEmpty(itemNumber)) {
                    if (itemMap.containsKey(itemNumberType)) {
                        itemMap.put(project.getProjectNumber() + itemNumber, itemMap.get(itemNumberType));
                        itemMap.put(itemMap.get(itemNumberType) + itemNumber, project.getProjectNumber());
                    } else {
                        itemMap.put(itemNumberType, project.getProjectNumber());
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void projectWflSubmit(IRequest iRequest, HlsCusPrjProject project) throws HlsCusException  {
        iRequest.setAttribute("authorityRuleFlag", "N");
        //获取业务模式
        String leaseChannel = project.getLeaseChannel();
        String workFlowType = PROJECT_WORK_FLOW_TYPE;

//        if (StringUtils.equals(leaseChannel, LEASE_CHANNEL_MANUFACTORY)) {
//            self().projectSubmitValidate(project);

//            if (StringUtils.equals(SynPrjInfoConstants.SY,project.getManufacturerUser())){
//                //校验进件提交附件是否上传
//                //校验附件是否都上传了
//                HashMap<String, String> paramMap = new HashMap<>(6);
//                paramMap.put("documentType",project.getDocumentType());
//                paramMap.put("manufacturerUser",project.getManufacturerUser());
//                paramMap.put("partnersContractNumber",project.getPartnersContractNumber());
//                paramMap.put("leaseChannel",leaseChannel);
//                paramMap.put("uploadTiming","PROJECT");
//                paramMap.put("division", project.getDivision());
//                paramMap.put("projectId", project.getProjectId().toString());
//                attachListService.checkAttachUploadByParamMap(iRequest, paramMap);
//            }
//        } else {
//            //查询子额度信息
//            HlsCusHlsCreditLine hlsCreditLine = new HlsCusHlsCreditLine();
//            hlsCreditLine.setCreditLineId(project.getCreditLineId());
//            hlsCreditLine = hlsCreditLineService.selectByPrimaryKey(iRequest, hlsCreditLine);
//            //查询厂商
//            HlsBpMaster manufacture = (HlsBpMaster) hlsBpMasterMapper.selectByPrimaryKey(project.getManufacturerId());
//            //查询承租人
//            HlsBpMaster tenant = (HlsBpMaster) hlsBpMasterMapper.selectByPrimaryKey(project.getTenantId());
//
//            //校验汽车租赁可用额度
//            checkDocumentComponent.checkBatchCredit(iRequest, project, manufacture.getBpName(), hlsCreditLine);
//            //校验单一客户额度以及上限
//            checkDocumentComponent.checkBatchTenant(project, tenant.getBpId());
//            //校验风险审查附件是否上传
//            //校验附件是否都上传了
//            attachListService.checkAttachUpload(iRequest, project.getPartnersContractNumber(), "RISK_REVIEW",
//              project.getLeaseChannel(), project.getDivision());
//            //校验已婚承租人必须推送结婚证/户口本
//            attachListService.checkTenantMarriedAttach(iRequest, project.getBpIdTenant(), project.getPartnersContractNumber());
//            //设置风险审查的流程编码
//            workFlowType = RISK_REVIEW_WORK_FLOW;
//        }

//        List<YxReplyVerifyCheck> yxReplyVerifyChecks = yxReplyVerifyCheckMapper.replyCheckQueryByManufacturerId(project.getManufacturerId());
//        for (YxReplyVerifyCheck yxReplyVerifyCheck : yxReplyVerifyChecks) {
//            if (StringUtils.equals(yxReplyVerifyCheck.getCheckPoint(), "EXAMINATION")) {
//                replyVerifyService.yxReplyVerifyDcRiskCompany(iRequest, project, "EXAMINATION");
//            }
//        }

//        Map map = new HashMap();
//        map.put(WsdlService.PROJECT_ID, project.getProjectId());
//        //项目基本信息
//        List<Map> list = hlsCusPrjProjectMapper.prjRpLModifyQuery(map);
//        Map projectMap = list.get(0);
//        //租赁物信息
//        List<Map> leaseItems = hlsCusPrjProjectMapper.prjProjectLIQuery(map);
//        for (int i = 0; i < leaseItems.size(); i++) {
//            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = hlsCusPrjProjectLeaseItemMapper.selectByPrimaryKey(Long.valueOf(leaseItems.get(i).get("project_lease_item_id").toString()));
//            if (leaseItems.get(i).get("classify_id") != null) {
//                hlsCusPrjProjectLeaseItem.setClassifyId(Long.valueOf(leaseItems.get(i).get("classify_id").toString()));
//                hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKey(hlsCusPrjProjectLeaseItem);
//            }
//        }
//        if(SynPrjInfoConstants.TH.equals(project.getManufacturerUser()) || SynPrjInfoConstants.TY.equals(project.getManufacturerUser())){
//            //天合的项目经理改成当前用户
//            HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
//            project.setEmployeeId(Long.parseLong((String) session.getAttribute("employeeId")));
//        }
//        if(project.getLeaseChannel().equals("10")&&!project.getDivision().equals("PHOTOVOLTAIC_DEVICES")&&project.getSignType().equals("SIGN_ONLINE")){
//            HlsBpMaster hlsBpMaster = new HlsBpMaster();
//            hlsBpMaster.setBpId(project.getBpIdTenant());
//            hlsBpMaster = hlsBpMasterService.selectByPrimaryKey(iRequest,hlsBpMaster);
//            if(hlsBpMaster.getBpClass().equals("ORG")){
//                YxAnxinsignRecord yxAnxinsignRecord = new YxAnxinsignRecord();
//                yxAnxinsignRecord.setBpId(hlsBpMaster.getBpId());
//                yxAnxinsignRecord = yxAnxinsignRecordMapper.selectOne(yxAnxinsignRecord);
//                if(yxAnxinsignRecord == null){
//                    throw new HlsCusException("未完成安心签开户不可提交审批！");
//                }else{
//                    if((!"Y".equals(yxAnxinsignRecord.getAnxinsignEnabledFlag()))||yxAnxinsignRecord.getAnxinsignId().isEmpty()||yxAnxinsignRecord.getLegalAnxinsignId().isEmpty()){
//                        throw new HlsCusException("未完成安心签开户不可提交审批！");
//                    }
//                }
//            }
//        }
//        if(project.getLeaseChannel().equals("10")&&!project.getDivision().equals("PHOTOVOLTAIC_DEVICES")){
//            HlsCusPrjProjectBp prjProjectBp = new HlsCusPrjProjectBp();
//            prjProjectBp.setProjectId(project.getProjectId());
//            prjProjectBp.setBpCategory(GUARANTOR);
//            prjProjectBp.setRefV04(SIGN_ONLINE);
//            List<PrjProjectBp> prjProjectBpList = prjProjectBpMapper.select(prjProjectBp);
//            for (PrjProjectBp projectBp : prjProjectBpList) {
//                if(projectBp.getBpClass().equals("ORG")){
//                    YxAnxinsignRecord yxAnxinsignRecord = new YxAnxinsignRecord();
//                    yxAnxinsignRecord.setBpId(projectBp.getBpId());
//                    yxAnxinsignRecord = yxAnxinsignRecordMapper.selectOne(yxAnxinsignRecord);
//                    if(yxAnxinsignRecord == null){
//                        throw new HlsCusException("请先完成担保人安心签开户！");
//                    }else{
//                        if((!"Y".equals(yxAnxinsignRecord.getAnxinsignEnabledFlag()))||yxAnxinsignRecord.getAnxinsignId().isEmpty()||yxAnxinsignRecord.getLegalAnxinsignId().isEmpty()){
//                            throw new HlsCusException("请先完成担保人安心签开户！");
//                        }
//                    }
//                }
//            }
//        }

        //进件风控预审前保存进件信息
        self().updateByPrimaryKey(iRequest, project);
//        if (StringUtils.equals(leaseChannel, "20")) {
//            //进件风控预审
//            Map<String,Object> trialMap = preTrial(iRequest,project,"20");
//            boolean preTrialFlag = (boolean) trialMap.get("preTrialFlag");
//            if (!preTrialFlag) {
//                String preTrialMessage = trialMap.get("errorMessage").toString();
//                if (StringUtils.isNotEmpty(preTrialMessage)) {
//                    throw new HlsCusException(preTrialMessage);
//                }
//            }
//        }

        project = prjProjectService.selectByPrimaryKey(iRequest, project);
        // 校验额度
        String documentType = project.getDocumentType();
        /**
        if(PRJL.equals(documentType) || PRJLB.equals(documentType)){
            Double creditAmt = 0D;
            Double tenantAmount = 0D;
            List<HlsCusPrjProject> creditAmtProjects = hlsCusPrjProjectMapper.queryCreditAmt(project.getManufacturerId(), project.getLeaseStartDate());
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(creditAmtProjects)){
                for(HlsCusPrjProject creditAmtProject:creditAmtProjects){
                    Double usedFinanceAmount = 0D;
                    if("REVOLVING".equals(creditAmtProject.getQuotaType())){
                        usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountRevolving(creditAmtProject.getDefinitionId());
                    }else{
                        usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountNonRevolving(creditAmtProject.getDefinitionId());
                    }
                    creditAmt+=creditAmtProject.getCreditAmt();
                    tenantAmount +=usedFinanceAmount;
                }
            }
            // 本次待投放
            Double financeAmount = project.getFinanceAmount();
            financeAmount = financeAmount == null ? 0D : financeAmount;
            if((creditAmt - tenantAmount) < financeAmount ){
                throw new HlsCusException("本次投放金额超过剩余可用额度！");
            }
        }
         **/
        //更改项目状态
        project.setProjectStatus(PROJECT_STATUS);

        //流程开始前清除决策报告信息，确保每次在流程中的决策报告是最新的
//        project.setReportLink(null);
//        project.setReportQueryTimes(null);
//        project.setReportStatus(null);
//        project.setReportToken(null);

        self().updateByPrimaryKey(iRequest, project);
        wflSubmit(iRequest, project, workFlowType);
    }
    void wflSubmit(IRequest iRequest, HlsCusPrjProject project, String workFlowType) {
        List<HlsCusPrjProject> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        list.add(project);

        Map TenantMap = prjProjectMapper.prjProjectBpTenantQuery1(project);
        //设置工作流参数
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(project));
        map.put(PROJECT, jsonObject.toString());
        map.put(PROJECT_NAME, project.getProjectNumber()+"-"+TenantMap.get("tenant_name"));
        map.put(DOCUMENT_CATEGORY, project.getDocumentCategory());
        map.put(DOCUMENT_TYPE, project.getDocumentType());
        map.put(DOCUMENT_ID, project.getProjectId());
        map.put(WORKFLOW_TYPE, workFlowType);
        map.put(DOCUMENT_NAME, project.getProjectNumber()+"-"+TenantMap.get("tenant_name"));
        map.put(DOCUMENT_NUMBER, project.getProjectNumber());
        map.put(LEASE_CHANNEL, project.getLeaseChannel());
        map.put(DIVISION, project.getDivision());
        map.put(FLAG, null);
        map.put("manufacturerId",project.getManufacturerId());
        map.put("projectId",project.getProjectId());
        activitiStartService.start(iRequest, list, map);
    }

    @Override
    public void signWflSubmit(IRequest iRequest, HlsCusPrjProject prjProject) throws Exception {

    }

    @Override
    public String signBatchWflSubmit(IRequest iRequest, List<HlsCusPrjProject> list) {
        return null;
    }

    @Override
    public String signSingleWflSubmit(IRequest iRequest, List<HlsCusPrjProject> list) {
        return null;
    }

    @Override
    public HlsCusPrjProject selectForApprove(Long projectId) {
        return null;
    }

    @Override
    public List<Map> queryProjectDetailById(Long projectId) {
        return null;
    }

    @Override
    public String generateAuthorityString(IRequest iRequest, String division, String documentType, String businessType, String employeeCode) {
        Long companyId = iRequest.getCompanyId();
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        FndCompany company = fndCompanyMapper.selectByPrimaryKey(companyId);
        String unitCode = (String) session.getAttribute("unitCode");
        if (StringUtils.isEmpty(employeeCode) || StringUtils.equals(employeeCode, "undefined")) {
            employeeCode = iRequest.getEmployeeCode();
        }
        businessType = "undefined".equals(businessType) ? "" : businessType;
        division = "undefined".equals(division) ? "" : division;
        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + '"' + "." + '"' + documentType + '"' + "." + '"' + businessType + '"' + "." + '"' + division + '"' + "." + '"' + employeeCode + '"';
        return authorityString;
    }

    @Override
    public Boolean validateRequiredInfo(HlsCusPrjProject project) {
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setSourceDocumentId(project.getProjectId());
        quotation.setSourceDocumentCategory("PRJ_PROJECT");
        quotation.setEnabledFlag(Y);
        if (prjQuotationMapper.selectCount(quotation) != 1) {
            return false;
        }
//        HlsCusPrjProjectBp prjProjectBp = new HlsCusPrjProjectBp();
//        prjProjectBp.setProjectId(project.getProjectId());
//        prjProjectBp.setBpCategory("TENANT");
//        List<HlsCusPrjProjectBp> bpList = prjProjectBpMapper.select(prjProjectBp);
//        if (bpList.size() == 0) {
//            return false;
//        }
        return true;
    }

    @Override
    public Boolean validateQuotation(HlsCusPrjProject project) {
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setSourceDocumentId(project.getProjectId());
        quotation.setSourceDocumentCategory("PRJ_PROJECT");
        quotation.setEnabledFlag(Y);
        quotation = prjQuotationMapper.selectQuotationInfo(quotation).get(0);
//        if (StringUtils.equals(quotation.getCalcCheckFlag(), "N")) {
//            return false;
//        }

        return true;
    }

    @Override
    public void projectBpCreate(IRequest iRequest, Long projectId) {

    }

    @Override
    public List<Map> searchProjectQuery(IRequest iRequest, HlsCusPrjProject prjProject, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjProjectMapper.searchProjectQuery(prjProject);
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

        logger.info("excelBatchImport--projectTenants.size:{}", projectTenants.size());
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        for (int i = 0; i < projectTenants.size(); i++) {
            try {
                int row = Math.toIntExact(projectTenants.get(i).getLineNumber());
                PrjExcelImportDto prjSheetImportDto = setValueToExcelDto(PROJECT_TENANT, projectTenants.get(i),division);
                logger.info("第{}个,excelBatchImport--prjSheetImportDto{},", i, prjSheetImportDto);
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
                logger.info("商业伙伴数据处理");
                //处理进件信息
                hlsCusPrjProject = importPrjProject(iRequest, prjSheetImportDto, PROJECT_TENANT_SHEET, row, hlsBpMaster.getBpId(),division);
                logger.info("处理进件信息");
                //处理报价信息
                prjQuotation = importPrjQuotation(iRequest, prjSheetImportDto, PROJECT_TENANT_SHEET, row);
                prjQuotation.setQuotationNumber(hlsCusPrjProject.getProjectNumber() + "-" + i);
                prjQuotation.setEnabledFlag(Y);
                prjQuotation.setSourceDocumentCategory(PROJECT_DOCUMENT_CATEGORY);
                prjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
//                prjQuotation.setFloatingRangeWay(hlsCusPrjProject.getFloatingRangeMethod());
                prjQuotation.setLeaseStartDate(hlsCusPrjProject.getLeaseStartDate());
                prjQuotationMapper.insert(prjQuotation);
                logger.info("处理报价信息");

                //生成承租人信息
                HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                hlsCusPrjProjectBp.setBpCategroy(TENANT);
                hlsCusPrjProjectBp.setBpClass(hlsBpMaster.getBpClass());
                hlsCusPrjProjectBp.setBpId(hlsBpMaster.getBpId());
                hlsCusPrjProjectBpMapper.insert(hlsCusPrjProjectBp);
                logger.info("生成承租人信息");
                //租赁物&保险信息
                fndInterfaceLines.setSheetName(LEASE_INSURANCE_SHEET);
                fndInterfaceLines.setAttributes_1(projectTenants.get(i).getAttributes_1());
                List<FndInterfaceLines> leaseItemLines = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);

//                BpMasterReply bpMasterReply = new BpMasterReply();
//                bpMasterReply.setFactoryId(hlsCusPrjProject.getFactoryId());
//                List<BpMasterReply> replyList = bpMasterReplyMapper.selectReplyParaInfo(bpMasterReply);
                Boolean insuranceFlag = false;
                Double insuranceFreeAmount = 0D;
//                if (CollectionUtils.isNotEmpty(replyList) && replyList.size() == 1) {
//                    insuranceFreeAmount = replyList.get(0).getInsuranceFreeAmount();
//                }

//                PrjCddItemRuleAssign prjCddItemRuleAssignQuery = new PrjCddItemRuleAssign();
//                prjCddItemRuleAssignQuery.setBpId(hlsCusPrjProject.getManufacturerId());
//                prjCddItemRuleAssignQuery.setDivision(hlsCusPrjProject.getDivision());
//                prjCddItemRuleAssignQuery.setBusinessType(hlsCusPrjProject.getBusinessType());
//                prjCddItemRuleAssignQuery.setBpClass(hlsBpMaster.getBpClass());
//                prjCddItemRuleAssignQuery.setEnabledFlag("Y");
//                List<PrjCddItemRuleAssign> prjCddItemRuleAssignList = prjCddItemRuleAssignMapper.select(prjCddItemRuleAssignQuery);

//                if(prjCddItemRuleAssignList.size() == 0){
//                    throw new HlsCusException(new StringBuffer("第").append(row).append("行未匹配到附件清单规则，请先在厂商资料规则定义中进行维护！").toString());
//                }else if(prjCddItemRuleAssignList.size() > 1){
//                    throw new HlsCusException(new StringBuffer("第").append(row).append("行匹配到多条附件清单规则，请先在厂商资料规则定义中进行维护！").toString());
//                }

                Double leaseItemAmount = 0D;
                List<HlsCusPrjProjectLeaseItem> leaseItemList = new ArrayList();
                for (int j = 0; j < leaseItemLines.size(); j++) {
                    int leaseItemRow = Math.toIntExact(leaseItemLines.get(j).getLineNumber());
                    HlsCusPrjProjectLeaseItem leaseItem = new HlsCusPrjProjectLeaseItem();
                    PrjExcelImportDto leaseItemSheetImportDto = setValueToExcelDto(LEASE_INSURANCE, leaseItemLines.get(j),division);
                    //租赁物序号
                    if (StringUtil.isEmpty(leaseItemSheetImportDto.getLeaseNum())) {
                        throw new HlsCusException(getExceptionInfo(LEASE_INSURANCE_SHEET, leaseItemRow, 2, IPrjProjectService.NOT_NULL));
                    }
                    Boolean existFlag = false;
//                    for (HlsCusPrjProjectLeaseItem item : leaseItemList) {
//                         existFlag = true;
//                         leaseItem = item;
//                    }
                    if (!existFlag) {
                        leaseItem = importLeaseItem(iRequest, leaseItemSheetImportDto, LEASE_INSURANCE_SHEET, leaseItemRow,division);
                        leaseItem.setProjectId(hlsCusPrjProject.getProjectId());
                        //工程机械校验产品型号
//                        if (StringUtils.equals(division,ENGINEERING_MACHINERY)) {
//                            List<YxLeaseItemClassify> yxLeaseItemClassifyList = yxLeaseItemClassifyMapper.yxLeaseItemClassifyQueryByProductModel(leaseItemSheetImportDto.getSpecification(), hlsCusPrjProject.getManufacturerId());
//                            if (CollectionUtils.isEmpty(yxLeaseItemClassifyList)) {
//                                throw new HlsCusException(new StringBuffer("第").append(leaseItemRow).append("行未匹配到产品型号，请先在租赁物产品库中进行维护！").toString());
//                            } else {
//                                leaseItem.setClassifyId(yxLeaseItemClassifyList.get(0).getClassifyId());
//                            }
//                        }
                        hlsCusPrjProjectLeaseItemMapper.insert(leaseItem);
                    }
                    leaseItemList.add(leaseItem);

//                    if (insuranceFreeAmount != null && leaseItem.getPrice().compareTo(insuranceFreeAmount) == 1) {
//                        insuranceFlag = true;
//                    }

//                    leaseItemAmount = CalculateUtil.add(leaseItemAmount, leaseItem.getPrice());
                    //保险信息
                    if (StringUtil.isNotEmpty(leaseItemSheetImportDto.getInsuranceNumber())) {
                        PrjLeaseItemInsurance itemInsurance = importLeaseItemInsurance(iRequest, leaseItemSheetImportDto, LEASE_INSURANCE_SHEET, leaseItemRow,division);
                        itemInsurance.setProjectLeaseItemId(leaseItem.getProjectLeaseItemId());
                        prjLeaseItemInsuranceMapper.insert(itemInsurance);
                    }
                }
                logger.info("租赁物&保险信息");

                //进件保险购买情况，调息规则
//                if (CollectionUtils.isEmpty(replyList)) {
//                    //保险购买情况改造
//                    //hlsCusPrjProject.setInsuranceFlag(BaseConstants.NO);
//                } else {
                    /*if (replyList.get(0).getInsuranceFreeAmount() == null || replyList.get(0).getInsuranceFreePeriod() == null) {
                        hlsCusPrjProject.setInsuranceFlag(BaseConstants.NO);
                    } else {
                        bpMasterReply = replyList.get(0);
                        Long insuranceFreePeriod = bpMasterReply.getInsuranceFreePeriod();
                        if (insuranceFreePeriod == null) {
                            insuranceFreePeriod = 0L;
                        }
                        if (insuranceFlag && prjQuotation.getLeaseTerm().compareTo(insuranceFreePeriod.doubleValue()) == 1) {
                            hlsCusPrjProject.setInsuranceFlag(Y);
                        } else {
                            hlsCusPrjProject.setInsuranceFlag(BaseConstants.NO);
                        }
                    }*/
//                    hlsCusPrjProject.setFloatingRangeMethod(bpMasterReply.getFloatingRangeMethod());

//                    prjQuotation.setFloatingRangeWay(bpMasterReply.getFloatingRangeMethod());
//                    prjQuotation.setObjectVersionNumber(null);
//                    prjQuotationMapper.updateByPrimaryKeySelective(prjQuotation);
//                }

                hlsCusPrjProject.setObjectVersionNumber(null);
                //是否提前起租
//                Long manufacturerId = hlsCusPrjProject.getManufacturerId();
//                HlsBpMasterInceptRule hlsBpMasterInceptRuleQuery = new HlsBpMasterInceptRule();
//                hlsBpMasterInceptRuleQuery.setBpId(manufacturerId);
//                hlsBpMasterInceptRuleQuery.setEnabledFlag(BaseConstants.YES);
//                hlsBpMasterInceptRuleQuery.setSortname("fixedDay");
//                hlsBpMasterInceptRuleQuery.setSortorder("ASC");
//                List<HlsBpMasterInceptRule> hlsBpMasterInceptRuleList = iHlsBpMasterInceptRuleService.select(iRequest, hlsBpMasterInceptRuleQuery, 0, 0);
//
//                String updateType = "";
//
//                if (hlsBpMasterInceptRuleList.size() == 0) {
//                    //未定义起租规则 固定为投放日当月15号
//                    updateType = HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY;
//                } else {
//                    HlsBpMasterInceptRule hlsBpMasterInceptRule0 = hlsBpMasterInceptRuleList.get(0);
//                    String inceptRuleType0 = hlsBpMasterInceptRule0.getInceptRuleType();
//                    if (HlsConstantUtil.HlsBpMasterInceptRule.LOAN_DATE.equals(inceptRuleType0)) {
//                        updateType = HlsConstantUtil.HlsBpMasterInceptRule.LOAN_DATE;
//                    } else if (HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY.equals(inceptRuleType0)) {
//                        updateType = HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY;
//                    }
//                }
//                if (StringUtils.equals(updateType, HlsConstantUtil.HlsBpMasterInceptRule.FIXED_DAY) && StringUtils.equals(hlsCusPrjProject.getLeaseChannel(), "10")) {
//                    if (StringUtils.isEmpty(prjSheetImportDto.getAppointInceptFlag())) {
//                        throw new HlsCusException(new StringBuffer("第").append(row).append("行是否提前起租字段必输").toString());
//                    } else {
//                        if (StringUtils.equals(prjSheetImportDto.getAppointInceptFlag(), "是")) {
//                            hlsCusPrjProject.setAppointInceptFlag(Y);
//                        } else {
//                            hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
//                        }
//                    }
//                } else if (StringUtils.equals(updateType, HlsConstantUtil.HlsBpMasterInceptRule.LOAN_DATE) && StringUtils.equals(hlsCusPrjProject.getLeaseChannel(), "10")) {
//                    if (StringUtils.isEmpty(prjSheetImportDto.getAppointInceptFlag())) {
//                        hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
//                    } else {
//                        if (StringUtils.equals(prjSheetImportDto.getAppointInceptFlag(), "是")) {
//                            throw new HlsCusException(new StringBuffer("第").append(row).append("行起租规则为“投放即起租时”，不允许提前起租").toString());
//                        } else {
//                            hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
//                        }
//                    }
//                } else {
//                    hlsCusPrjProject.setAppointInceptFlag(BaseConstants.NO);
//                }
//                prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
//                logger.info("进件保险购买情况，调息规则");

                //自动计算报价
//                try {
//                    excelUtils.updateQutationByProject(prjQuotation.getQuotationId(), hlsCusPrjProject.getProjectId(), null);
//                } catch (Exception e) {
//                    logger.error("excute excel error:{}", e);
//                    throw new HlsCusException(new StringBuffer("第").append(row).append("行").append(QUOTATION_CALC_EXCEPTION).append("(").append(e.getMessage()).append(")").toString());
//                }
//                logger.info("自动计算报价");

                //担保人
//                fndInterfaceLines.setSheetName(GUARANTOR_SHEET);
//                List<FndInterfaceLines> guarantorList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
//                for (int k = 0; k < guarantorList.size(); k++) {
//                    int guarantorRow = Math.toIntExact(guarantorList.get(k).getLineNumber());
//                    hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
//                    PrjExcelImportDto guarantorImportDto = setValueToExcelDto(GUARANTOR_SHEET, guarantorList.get(k),division);
//                    HlsCusBpMaster guarantor = importGuarantorInfo(iRequest, guarantorImportDto, GUARANTOR_SHEET, guarantorRow, GUARANTOR);
//                    //生成担保人信息
//                    hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
//                    hlsCusPrjProjectBp.setBpCategroy(GUARANTOR);
//                    hlsCusPrjProjectBp.setBpType(GUARANTOR);
//                    hlsCusPrjProjectBp.setBpId(guarantor.getBpId());
//                    hlsCusPrjProjectBp.setBpClass(guarantor.getBpClass());
//                    hlsCusPrjProjectBp.setRefV03(guarantor.getRefV03());
//                    hlsCusPrjProjectBp.setRefV02(guarantor.getRefV02());
////                    hlsCusPrjProjectBp.setRefV04(guarantor.getRefV04());
////                    hlsCusPrjProjectBp.setRefV05(guarantor.getRefV05());
//                    hlsCusPrjProjectBpMapper.insert(hlsCusPrjProjectBp);
//                }
//                logger.info("担保人");

                //进件导入后直接生成签约附件清单
                hlsCusPrjProjectService.generateProjectSignAttach(iRequest, hlsCusPrjProject.getProjectId());

            } catch (HlsCusException e) {
                logger.error("project import error:{}", e);
                interfaceErrorMsgService.insertInterfaceErrorMessage(iRequest, headerId, PRJ_PROJECT, e.getMessage());
                continue;
            }

        }


    }

    @Override
    public void excelBatchImport2(IRequest iRequest, Long headerId, String division,Long projectId) throws HlsCusException {
        if (headerId == null) {
            throw new HlsCusException(PARAM_NOT_FOUND);
        }
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(headerId);
        fndInterfaceLines.setReadLine(READ_LINE);
        if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
            fndInterfaceLines.setSheetName("商用车导入清单");
        }else{
            fndInterfaceLines.setSheetName("工程机械导入清单");
        }
        List<FndInterfaceLines> projectTenants = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        logger.info("excelBatchImport--projectTenants.size:{}", projectTenants.size());
        for (int i = 0; i < projectTenants.size(); i++) {
            try {
                //租赁物&保险信息
                int leaseItemRow = Math.toIntExact(projectTenants.get(i).getLineNumber());
                HlsCusPrjProjectLeaseItem leaseItem = new HlsCusPrjProjectLeaseItem();
                leaseItem = importLeaseItem2(iRequest, projectTenants.get(i), LEASE_INSURANCE_SHEET, leaseItemRow,division);
                leaseItem.setProjectId(projectId);
                hlsCusPrjProjectLeaseItemMapper.insert(leaseItem);
                 //保险信息
                PrjLeaseItemInsurance itemInsurance = importLeaseItemInsurance2(iRequest, projectTenants.get(i), LEASE_INSURANCE_SHEET, leaseItemRow,division);
                itemInsurance.setProjectLeaseItemId(leaseItem.getProjectLeaseItemId());
                String insuranceType = itemInsurance.getInsuranceType();
                // 认定为有保险类型才插入保险信息
                if (StrUtil.isNotEmpty(insuranceType)) {
                    prjLeaseItemInsuranceMapper.insert(itemInsurance);
                }
            } catch (HlsCusException e) {
                logger.error("project import error:{}", e);
                interfaceErrorMsgService.insertInterfaceErrorMessage(iRequest, headerId, PRJ_PROJECT, e.getMessage());
                continue;
            }

        }


    }

    //处理保险信息
    PrjLeaseItemInsurance importLeaseItemInsurance(IRequest iRequest, PrjExcelImportDto prjExcelImportDto, String sheetName, int i, String division) throws HlsCusException {
        PrjLeaseItemInsurance itemInsurance = new PrjLeaseItemInsurance();
        //商用车
        if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
            //保险类型
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceType())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(NEW_INSURANCE_TYPE, prjExcelImportDto.getInsuranceType());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 20, "描述有误！"));
                }
                itemInsurance.setInsuranceType(value);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 20, IPrjProjectService.NOT_NULL));
            }
            //保险起始日
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceDateFrom())) {
                try {
                    itemInsurance.setInsuranceDateFrom(simpleDateFormat.parse(prjExcelImportDto.getInsuranceDateFrom()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 21, IPrjProjectService.NOT_NULL));
                }
            }
            //保险到期日
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceDateTo())) {
                try {
                    itemInsurance.setInsuranceDateTo(simpleDateFormat.parse(prjExcelImportDto.getInsuranceDateTo()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 22, IPrjProjectService.NOT_NULL));
                }
            }
            //保险公司
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceCompany())) {
                itemInsurance.setInsuranceCompany(prjExcelImportDto.getInsuranceCompany());
            }
            //保单编号
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceNumber())) {
                itemInsurance.setInsuranceNumber(prjExcelImportDto.getInsuranceNumber());
            }
            //投保金额
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceAmount())) {
                itemInsurance.setInsuranceAmount(Double.valueOf(prjExcelImportDto.getInsuranceAmount()));
            }
            //第一受益人
            if (StringUtil.isNotEmpty(prjExcelImportDto.getFirstBeneficiary())) {
                itemInsurance.setFirstBeneficiary(prjExcelImportDto.getFirstBeneficiary());
            }

        } else {
            //工程机械

            //保险类型
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceType())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(NEW_INSURANCE_TYPE, prjExcelImportDto.getInsuranceType());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
                }
                itemInsurance.setInsuranceType(value);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 9, IPrjProjectService.NOT_NULL));
            }
            //保险起始日
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceDateFrom())) {
                try {
                    itemInsurance.setInsuranceDateFrom(simpleDateFormat.parse(prjExcelImportDto.getInsuranceDateFrom()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 10, IPrjProjectService.NOT_NULL));
                }
            }
            //保险到期日
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceDateTo())) {
                try {
                    itemInsurance.setInsuranceDateTo(simpleDateFormat.parse(prjExcelImportDto.getInsuranceDateTo()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 11, IPrjProjectService.NOT_NULL));
                }
            }
            //保险公司
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceCompany())) {
                itemInsurance.setInsuranceCompany(prjExcelImportDto.getInsuranceCompany());
            }
            //保单编号
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceNumber())) {
                itemInsurance.setInsuranceNumber(prjExcelImportDto.getInsuranceNumber());
            }
            //投保金额
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceAmount())) {
                itemInsurance.setInsuranceAmount(Double.valueOf(prjExcelImportDto.getInsuranceAmount()));
            }
            //第一受益人
            if (StringUtil.isNotEmpty(prjExcelImportDto.getFirstBeneficiary())) {
                itemInsurance.setFirstBeneficiary(prjExcelImportDto.getFirstBeneficiary());
            }
        }
        return itemInsurance;
    }

    //处理保险信息
    PrjLeaseItemInsurance importLeaseItemInsurance2(IRequest iRequest, FndInterfaceLines fndInterfaceLines, String sheetName, int i, String division) throws HlsCusException {
        PrjLeaseItemInsurance itemInsurance = new PrjLeaseItemInsurance();
        //商用车
        if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
            //保险类型
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_19())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(NEW_INSURANCE_TYPE, fndInterfaceLines.getAttributes_19());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 19, "描述有误！"));
                }
                itemInsurance.setInsuranceType(value);
            }else {
                return itemInsurance;
            }
            //保险起始日
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_20())) {
                try {
                    itemInsurance.setInsuranceDateFrom(simpleDateFormat.parse(fndInterfaceLines.getAttributes_20()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 20, IPrjProjectService.NOT_NULL));
                }
            }
            //保险到期日
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_21())) {
                        try {
                            itemInsurance.setInsuranceDateTo(simpleDateFormat.parse(fndInterfaceLines.getAttributes_21()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 21, IPrjProjectService.NOT_NULL));
                }
            }
            //保险公司
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_22())) {
                itemInsurance.setInsuranceCompany(fndInterfaceLines.getAttributes_22());
            }
            //保单编号
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_23())) {
                itemInsurance.setInsuranceNumber(fndInterfaceLines.getAttributes_23());
            }
            //投保金额
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_24())) {
                itemInsurance.setInsuranceAmount(Double.valueOf(fndInterfaceLines.getAttributes_24()));
            }
            //第一受益人
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_25())) {
                itemInsurance.setFirstBeneficiary(fndInterfaceLines.getAttributes_25());
            }

        } else {
            //工程机械

            //保险类型
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_9())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(NEW_INSURANCE_TYPE, fndInterfaceLines.getAttributes_9());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
                }
                itemInsurance.setInsuranceType(value);
            }else {
                return itemInsurance;
            }
            //保险起始日
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_10())) {
                try {
                    itemInsurance.setInsuranceDateFrom(simpleDateFormat.parse(fndInterfaceLines.getAttributes_10()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 10, IPrjProjectService.NOT_NULL));
                }
            }
            //保险到期日
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_11())) {
                try {
                    itemInsurance.setInsuranceDateTo(simpleDateFormat.parse(fndInterfaceLines.getAttributes_11()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 11, IPrjProjectService.NOT_NULL));
                }
            }
            //保险公司
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_12())) {
                itemInsurance.setInsuranceCompany(fndInterfaceLines.getAttributes_12());
            }
            //保单编号
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_13())) {
                itemInsurance.setInsuranceNumber(fndInterfaceLines.getAttributes_13());
            }
            //投保金额
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_14())) {
                itemInsurance.setInsuranceAmount(Double.valueOf(fndInterfaceLines.getAttributes_14()));
            }
            //第一受益人
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_15())) {
                itemInsurance.setFirstBeneficiary(fndInterfaceLines.getAttributes_15());
            }
        }
        return itemInsurance;
    }

    public boolean regBlank(String value) {
        String pattern = "(.*)[\\s](.*)";
        boolean isMatch = Pattern.matches(pattern, value);
        return isMatch;
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
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_ID_TYPE", excelInfo.getIdType());
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
            String cardType = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.ZX_REGNOTYPE, excelInfo.getIdType());
            if (StringUtils.isEmpty(cardType)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "证件类型描述有误!"));
            }
            hlsBpMaster.setRegnotype(cardType);
            hlsBpMaster.setRegistrationNumType(cardType);

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
                hlsBpMaster.setRegisterCertNum(excelInfo.getIdCardNo());
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
//            hlsBpMasterService.updateByPrimaryKeySelective(iRequest, hlsBpMaster);
            hlsBpMasterMapper.updateByPrimaryKeySelective(hlsBpMaster);
        } else {
            //共有数据
            hlsBpMaster.setOwnerUserId(iRequest.getUserId());
            hlsBpMaster.setCreatedBy(iRequest.getUserId());
            hlsBpMaster.setCreationDate(new Date());
            hlsBpMaster.setBpCategory(type);
            hlsBpMaster.setBpType(type);
            hlsBpMaster.setEnabledFlag(Y);
            //国别/国家默认中国
            hlsBpMaster.setNationality("46");
            //自然人
            if (IPrjProjectService.NP.equals(hlsBpMaster.getBpClass())) {

                //设置证件类型
                String cardType = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdType());
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
                    String gender = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HR.EMPLOYEE_GENDER", excelInfo.getGenger());
                    if (StringUtils.isEmpty(gender)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 7, "性别描述有误！"));
                    }
                    hlsBpMaster.setGender(gender);
                }
                //婚姻状况
                if (StringUtil.isEmpty(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 8, IPrjProjectService.NOT_NULL));
                } else {
                    String maritalStatus = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.MARITAL_STATUS_SYS_CODE, excelInfo.getMaritalStatus());
                    if (StringUtils.isEmpty(maritalStatus)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 8, "描述有误！"));
                    }
                    hlsBpMaster.setMaritalStatus(maritalStatus);
                }
                //学历
//                if (StringUtil.isEmpty(excelInfo.getAcademicBackground())) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "学历不能为空!"));
//                } else {
//                    String academicBackground = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_ACADEMIC_BACKGROUND", excelInfo.getAcademicBackground());
//                    if (StringUtils.isEmpty(academicBackground)) {
//                        throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
//                    }
//                    hlsBpMaster.setAcademicBackground(academicBackground);
//                    hlsBpMaster.setHighestDegree(academicBackground);
//                }
                if (StringUtil.isNotEmpty(excelInfo.getAcademicBackground())) {
                    String academicBackground = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_ACADEMIC_BACKGROUND", excelInfo.getAcademicBackground());
                    if (StringUtils.isEmpty(academicBackground)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
                    }
                    hlsBpMaster.setAcademicBackground(academicBackground);
                    hlsBpMaster.setHighestDegree(academicBackground);
                }
                //配偶姓名
                if (StringUtil.isNotEmpty(excelInfo.getBpNameSp())) {
                    hlsBpMaster.setBpNameSp(excelInfo.getBpNameSp());
                    if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 10, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                    }
                }
//                else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 10, IPrjProjectService.NOT_NULL));
//                }

                //配偶证件类型
                if (StringUtil.isNotEmpty(excelInfo.getIdTypeSp())) {
                    String idTypeSp = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdTypeSp());
                    if (StringUtils.isEmpty(idTypeSp)) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 11, "描述有误！"));
                    }
                    if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 11, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                    }
                    hlsBpMaster.setIdTypeSp(idTypeSp);
                }
//                else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 11, IPrjProjectService.NOT_NULL));
//                }

                //配偶证件号码
                if (StringUtil.isNotEmpty(excelInfo.getIdCardNoSp())) {
                    hlsBpMaster.setIdCardNoSp(excelInfo.getIdCardNoSp());
//                    String genderSp = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_GENDER", excelInfo.getGenderSp());
//                    hlsBpMaster.setGenderSp(genderSp);
//                    if (ID_CARD.equals(hlsBpMaster.getIdTypeSp())) {
//                        if (IDUtils.isValidatedAllIdcard(excelInfo.getIdCardNoSp())) {
//                            hlsBpMaster.setIdCardNoSp(excelInfo.getIdCardNoSp());
//                            try {
//                                IDUtils.Person person = IDUtils.getBirAgeSex(excelInfo.getIdCardNoSp());
//                                hlsBpMaster.setDateOfBirthSp(person.getBirthDate());
//                                hlsBpMaster.setGenderSp(person.getSex());
////                                hlsBpMaster.setAgeSp(person.getAge());
//                            } catch (ParseException e) {
//                                throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(NP_ID_CARD_EXCEPTION).toString());
//                            }
//                        } else {
//                            throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(ID_CARD_EXCEPTION).toString());
//                        }
//                    }

                    if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 12, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                    }
                }
                if (StringUtils.isNotEmpty(excelInfo.getGenderSp())) {
                    String genderSp = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_GENDER", excelInfo.getGenderSp());
                    hlsBpMaster.setGenderSp(genderSp);
                }
//                else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 12, IPrjProjectService.NOT_NULL));
//                }
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
                    hlsBpMaster.setExtraNam(excelInfo.getShortName());
                }
                //实际控制人
                if (StringUtil.isNotEmpty(excelInfo.getActualController())) {
                    hlsBpMaster.setActualController(excelInfo.getActualController());
                    hlsBpMaster.setActualPerson(excelInfo.getActualController());
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
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.GUARANTOR_TYPE, excelInfo.getDealerType());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 17, "描述有误！"));
                }
                hlsBpMaster.setRefV03(value);
            } else {
                throw new HlsCusException(getExceptionInfo(GUARANTOR_SHEET, i, 17, IPrjProjectService.NOT_NULL));
            }
            //与承租人关系
            if (StringUtil.isNotEmpty(excelInfo.getDealerRelation())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.ZX_MEMBERTYPE, excelInfo.getDealerRelation());
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
//            String authorityRuleString = hlsBpMasterService.getAuthorityString(iRequest);
//            hlsBpMaster.setAuthorityRuleString(authorityRuleString);
//            hlsBpMasterService.insert(iRequest, hlsBpMaster);
            hlsBpMasterMapper.insert(hlsBpMaster);

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
//        if (StringUtil.isNotEmpty(excelInfo.getGuarantorSignType())) {
//            String signType = codeValueMapper2.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", excelInfo.getGuarantorSignType());
//            if (StringUtils.isEmpty(signType)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 19, "描述有误！"));
//            }
//            hlsBpMaster.setRefV04(signType);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 19, IPrjProjectService.SIGN_TYPE_NOT_NULL));
//        }

        return hlsBpMaster;
    }

    //处理租赁物信息
    HlsCusPrjProjectLeaseItem importLeaseItem(IRequest iRequest, PrjExcelImportDto prjExcelImportDto, String sheetName, int i,String division) throws HlsCusException {
        HlsCusPrjProjectLeaseItem leaseItem = new HlsCusPrjProjectLeaseItem();
        //租赁物序号
        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseNum())) {
            leaseItem.setSeqNumber(Long.valueOf(prjExcelImportDto.getLeaseNum()));
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 2, IPrjProjectService.NOT_NULL));
        }
        //租赁物名称
        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseFullName())) {
            leaseItem.setFullName(prjExcelImportDto.getLeaseFullName());
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 3, IPrjProjectService.NOT_NULL));
        }
//        //租赁物简称
        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseShortName())) {
            leaseItem.setShortName(prjExcelImportDto.getLeaseShortName());
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 4, IPrjProjectService.NOT_NULL));
        }
        if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
            //商用车业务线
            //车辆类型必输
            if (StringUtil.isNotEmpty(prjExcelImportDto.getTrailerFlag())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue("TRAILER_FLAG", prjExcelImportDto.getTrailerFlag());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "描述有误！"));
                }
                leaseItem.setTrailerFlag(value);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, IPrjProjectService.NOT_NULL));
            }
            //租赁物净价必输
            if (StringUtil.isEmpty(prjExcelImportDto.getNetPrice())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, IPrjProjectService.NOT_NULL));
            } else {
                leaseItem.setNetPrice(Double.valueOf(prjExcelImportDto.getNetPrice()));
            }

            //7-购置税
            if (Objects.nonNull(prjExcelImportDto.getPurchaseTax())) {
                leaseItem.setPurchaseTax(Double.valueOf(prjExcelImportDto.getPurchaseTax()));
            }

            //8-保险费
            if (Objects.nonNull(prjExcelImportDto.getInsurancePremium())) {
                leaseItem.setInsurancePremium(Double.valueOf(prjExcelImportDto.getInsurancePremium()));
            }

            //9-配件费
            if (Objects.nonNull(prjExcelImportDto.getAccessoryFee())) {
                leaseItem.setAccessoryFee(Double.valueOf(prjExcelImportDto.getAccessoryFee()));
            }


            // 租赁物总价必输
            if (StringUtil.isEmpty(prjExcelImportDto.getPrice())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 10, IPrjProjectService.NOT_NULL));
            } else {
                leaseItem.setPrice(Double.valueOf(prjExcelImportDto.getPrice()));
            }

            //11-品牌
            if (Objects.nonNull(prjExcelImportDto.getBrandC())) {
                leaseItem.setBrandC(prjExcelImportDto.getBrandC());
            }

            //12-车系
            if (Objects.nonNull(prjExcelImportDto.getSeriesC())) {
                leaseItem.setSeriesC(prjExcelImportDto.getSeriesC());
            }
//
            //13-车型
            if (Objects.nonNull(prjExcelImportDto.getModelC())) {
                leaseItem.setModelC(prjExcelImportDto.getModelC());
            }


//             当车辆类型为 新能源车 或 燃油燃气车 时 发动机号必输
            if (!"TRAILER".equals(leaseItem.getTrailerFlag())) {
                if (StringUtil.isEmpty(prjExcelImportDto.getEngineNumber())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 14, IPrjProjectService.NOT_NULL));
                } else {
                    leaseItem.setEngineNumber(prjExcelImportDto.getEngineNumber());
                }
            }
            // 车架号必输
            if (StringUtil.isEmpty(prjExcelImportDto.getFrameNumber())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 15, IPrjProjectService.NOT_NULL));
            } else {
                leaseItem.setFrameNumber(prjExcelImportDto.getFrameNumber());
            }

//            16-车牌号
            if (Objects.nonNull(prjExcelImportDto.getLicensePlateNumber())) {
                leaseItem.setLicensePlateNumber(prjExcelImportDto.getLicensePlateNumber());
            }


            // 拟上牌地（省）必输
            if (StringUtil.isEmpty(prjExcelImportDto.getProvinceCode())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 17, IPrjProjectService.NOT_NULL));
            } else {
                FndProvince fp = new FndProvince();
                fp.setDescription(prjExcelImportDto.getProvinceCode());
                try {
                    fp = fndProvinceMapper.selectOne(fp);
                } catch (Exception e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 17, IPrjProjectService.NOT_NULL));
                }
                if (Objects.isNull(fp)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 17, "请填写正确省市名称"));
                }
                leaseItem.setProvinceCode(fp.getProvince());
            }

            // 拟上牌地（市）必输
            if (StringUtil.isEmpty(prjExcelImportDto.getCityCode())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 18, IPrjProjectService.NOT_NULL));
            } else {
                FndCity fc = new FndCity();
                fc.setDescription(prjExcelImportDto.getCityCode());
                fc.setProvince(leaseItem.getProvinceCode());
                try {
                    fc = fndCityMapper.selectOne(fc);
                    if (fc == null) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 18, "请填写正确省市名称"));
                    }
                } catch (Exception e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 18, IPrjProjectService.NOT_NULL));
                }
                leaseItem.setCityCode(fc.getCity());
            }
        } else {
            //规则型号
            if (StringUtil.isNotEmpty(prjExcelImportDto.getSpecification())) {
                YxLeaseItemClassify yxLeaseItemClassify=new YxLeaseItemClassify();
                yxLeaseItemClassify.setAttributeValue(prjExcelImportDto.getSpecification());
                yxLeaseItemClassify=yxLeaseItemClassifyMapper.selectOne(yxLeaseItemClassify);
                leaseItem.setClassifyId(yxLeaseItemClassify.getClassifyId());

                leaseItem.setSpecification(prjExcelImportDto.getSpecification());
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, IPrjProjectService.NOT_NULL));
            }

            //整机编号
            if (StringUtil.isNotEmpty(prjExcelImportDto.getSerialNumber())) {
                leaseItem.setSerialNumber(prjExcelImportDto.getSerialNumber());
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, IPrjProjectService.NOT_NULL));
            }
            //设备单价
            if (StringUtil.isNotEmpty(prjExcelImportDto.getLeasePrice())) {
                leaseItem.setPrice(Double.valueOf(prjExcelImportDto.getLeasePrice()));
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 7, IPrjProjectService.NOT_NULL));
            }
        }
        //备注
        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseDescription())) {
            leaseItem.setDescription(prjExcelImportDto.getLeaseDescription());
        }

        return leaseItem;
    }

    //处理租赁物信息
    HlsCusPrjProjectLeaseItem importLeaseItem2(IRequest iRequest,FndInterfaceLines fndInterfaceLines, String sheetName, int i,String division) throws HlsCusException {
        HlsCusPrjProjectLeaseItem leaseItem = new HlsCusPrjProjectLeaseItem();
        //租赁物序号
        if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_1())) {
            leaseItem.setSeqNumber(Long.valueOf(fndInterfaceLines.getAttributes_1()));
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 1, IPrjProjectService.NOT_NULL));
        }
        //租赁物名称
        if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_2())) {
            leaseItem.setFullName(fndInterfaceLines.getAttributes_2());
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 2, IPrjProjectService.NOT_NULL));
        }
        //租赁物简称
        if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_3())) {
            leaseItem.setShortName(fndInterfaceLines.getAttributes_3());
        }
        if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
            //商用车业务线
            //车辆类型必输
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_4())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue("TRAILER_FLAG", fndInterfaceLines.getAttributes_4());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "描述有误！"));
                }
                leaseItem.setTrailerFlag(value);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 4, IPrjProjectService.NOT_NULL));
            }
            //租赁物净价必输
            if (StringUtil.isEmpty(fndInterfaceLines.getAttributes_5())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, IPrjProjectService.NOT_NULL));
            } else {
                leaseItem.setNetPrice(Double.valueOf(fndInterfaceLines.getAttributes_5()));
            }

            //7-购置税
            if (Objects.nonNull(fndInterfaceLines.getAttributes_6())) {
                leaseItem.setPurchaseTax(Double.valueOf(fndInterfaceLines.getAttributes_6()));
            }

            //8-保险费
            if (Objects.nonNull(fndInterfaceLines.getAttributes_7())) {
                leaseItem.setInsurancePremium(Double.valueOf(fndInterfaceLines.getAttributes_7()));
            }

            //9-配件费
            if (Objects.nonNull(fndInterfaceLines.getAttributes_8())) {
                leaseItem.setAccessoryFee(Double.valueOf(fndInterfaceLines.getAttributes_8()));
            }


            // 租赁物总价必输
            if (StringUtil.isEmpty(fndInterfaceLines.getAttributes_9())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 9, IPrjProjectService.NOT_NULL));
            } else {
                leaseItem.setPrice(Double.valueOf(fndInterfaceLines.getAttributes_9()));
            }

            //11-品牌
            if (Objects.nonNull(fndInterfaceLines.getAttributes_10())) {
                leaseItem.setBrandC(fndInterfaceLines.getAttributes_10());
            }

            //12-车系
            if (Objects.nonNull(fndInterfaceLines.getAttributes_11())) {
                leaseItem.setSeriesC(fndInterfaceLines.getAttributes_11());
            }
//
            //13-车型
            if (Objects.nonNull(fndInterfaceLines.getAttributes_12())) {
                leaseItem.setModelC(fndInterfaceLines.getAttributes_12());
            }


//             当车辆类型为 新能源车 或 燃油燃气车 时 发动机号必输
            if (!"TRAILER".equals(leaseItem.getTrailerFlag())) {
                if (StringUtil.isEmpty(fndInterfaceLines.getAttributes_13())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 13, IPrjProjectService.NOT_NULL));
                } else {
                    leaseItem.setEngineNumber(fndInterfaceLines.getAttributes_13());
                }
            }
            // 车架号必输
            if (StringUtil.isEmpty(fndInterfaceLines.getAttributes_14())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 14, IPrjProjectService.NOT_NULL));
            } else {
                leaseItem.setFrameNumber(fndInterfaceLines.getAttributes_14());
            }

//            16-车牌号
            if (Objects.nonNull(fndInterfaceLines.getAttributes_15())) {
                leaseItem.setLicensePlateNumber(fndInterfaceLines.getAttributes_15());
            }


            // 拟上牌地（省）必输
            if (StringUtil.isEmpty(fndInterfaceLines.getAttributes_16())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 16, IPrjProjectService.NOT_NULL));
            } else {
                FndProvince fp = new FndProvince();
                fp.setDescription(fndInterfaceLines.getAttributes_16());
                List<FndProvince> fndProvinces = fndProvinceMapper.selectAllFndProvince(fp);
                if (fndProvinces.isEmpty()) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 16, "请填写正确省市名称"));
                }else {
                    fp = fndProvinces.get(0);
                    leaseItem.setProvinceCode(fp.getProvince());
                }
            }

            // 拟上牌地（市）必输
            if (StringUtil.isEmpty(fndInterfaceLines.getAttributes_17())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 17, IPrjProjectService.NOT_NULL));
            } else {
                FndCity fc = new FndCity();
                fc.setDescription(fndInterfaceLines.getAttributes_17());
                fc.setProvince(leaseItem.getProvinceCode());
                List<FndCity> fndCities = fndCityMapper.queryAll(fc);
                if (fndCities.isEmpty()) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 17, "请填写正确省市名称"));
                } else {
                    fc = fndCities.get(0);
                    leaseItem.setCityCode(fc.getCity());
                }
            }
            //备注
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_18())) {
                leaseItem.setDescription(fndInterfaceLines.getAttributes_18());
            }
        } else {
            //规格型号
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_4())) {
                leaseItem.setProductModel(fndInterfaceLines.getAttributes_4());
            }
            //规则型号
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_5())) {
                YxLeaseItemClassify yxLeaseItemClassify=new YxLeaseItemClassify();
                yxLeaseItemClassify.setAttributeValue(fndInterfaceLines.getAttributes_5());
                yxLeaseItemClassify=yxLeaseItemClassifyMapper.selectOne(yxLeaseItemClassify);
                if (null == yxLeaseItemClassify){
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 5, IPrjProjectService.UNDEFINED));
                }
                leaseItem.setClassifyId(yxLeaseItemClassify.getClassifyId());
                leaseItem.setSpecification(fndInterfaceLines.getAttributes_5());
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, IPrjProjectService.NOT_NULL));
            }

            //整机编号
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_6())) {
                leaseItem.setSerialNumber(fndInterfaceLines.getAttributes_6());
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, IPrjProjectService.NOT_NULL));
            }
            //设备单价
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_7())) {
                leaseItem.setPrice(Double.valueOf(fndInterfaceLines.getAttributes_7()));
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 7, IPrjProjectService.NOT_NULL));
            }
            //备注
            if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_8())) {
                leaseItem.setDescription(fndInterfaceLines.getAttributes_8());
            }

        }

        return leaseItem;
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
    //处理报价信息
    HlsCusPrjQuotation importPrjQuotation(IRequest iRequest, PrjExcelImportDto prjExcelImportDto, String sheetName, int i) throws HlsCusException {
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
//        prjQuotation.setQuotationDate(new Date());
        //产品方案
        HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
        if (StringUtil.isNotEmpty(prjExcelImportDto.getPlanName())) {
            hlsProductDefinition.setDefinitionName(prjExcelImportDto.getPlanName());
            hlsProductDefinition.setEnabledFlag(Y);
            try {
                hlsProductDefinition = hlsProductDefinitionMapper.selectOne(hlsProductDefinition);
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "系统匹配到多条产品！"));
            }

            if (Objects.isNull(hlsProductDefinition)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "产品方案不存在！"));
            }


            prjQuotation.setPlanId(hlsProductDefinition.getDefinitionId());
            //价目表
            HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
            hlsCalcConfig.setPriceList(hlsProductDefinition.getPriceList());
            hlsCalcConfig = hlsCalcConfigMapper.selectByPrimaryKey(hlsCalcConfig);
            prjQuotation.setPriceList(hlsCalcConfig.getPriceList());
            prjQuotation.setSheets(hlsCalcConfig.getSheets());
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 49, IPrjProjectService.NOT_NULL));
        }
        //首付款推算方式
        if (StringUtil.isNotEmpty(prjExcelImportDto.getDownPaymentMethod())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(PAY_METHOD, prjExcelImportDto.getDownPaymentMethod());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 50, "描述有误！"));
            }
            prjQuotation.setDownPaymentMethod(value);
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 50, IPrjProjectService.NOT_NULL));
        }
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        //首付款比例/金额
        if (StringUtil.isNotEmpty(prjExcelImportDto.getDownPayment())) {
            if ("20".equalsIgnoreCase(prjQuotation.getDownPaymentMethod())) {
                Double downPaymentRatio = Double.valueOf(prjExcelImportDto.getDownPayment());
                prjQuotation.setDownPaymentRatio(downPaymentRatio);
            } else {
                prjQuotation.setDownPayment(Double.valueOf(prjExcelImportDto.getDownPayment()));
            }
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 51, IPrjProjectService.NOT_NULL));
        }
        //保证金推算方式
        if (StringUtil.isNotEmpty(prjExcelImportDto.getDepositMethod())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(PAY_METHOD, prjExcelImportDto.getDepositMethod());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 52, "描述有误！"));
            }
            prjQuotation.setDepositMethod(value);
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 52, IPrjProjectService.NOT_NULL));
        }
        //保证金比例/金额
        if (StringUtil.isNotEmpty(prjExcelImportDto.getDeposit())) {
            if ("20".equalsIgnoreCase(prjQuotation.getDepositMethod())) {
                Double depositRatio = Double.valueOf(prjExcelImportDto.getDeposit());
                prjQuotation.setDepositRatio(depositRatio);
            } else {
                prjQuotation.setDeposit(Double.valueOf(prjExcelImportDto.getDeposit()));
            }
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 53, IPrjProjectService.NOT_NULL));
        }
        //租赁利率%
        if (StringUtil.isNotEmpty(prjExcelImportDto.getIntRate())) {
            Double intRate = Double.valueOf(prjExcelImportDto.getIntRate());
            prjQuotation.setIntRate(intRate);

        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 54, IPrjProjectService.NOT_NULL));
        }
        //手续费率%
        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseChargeRatio())) {
            Double chargeRatio = Double.valueOf(prjExcelImportDto.getLeaseChargeRatio());
            prjQuotation.setLeaseChargeRatio(chargeRatio);
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 55, IPrjProjectService.NOT_NULL));
        }
        //是否返利
        prjQuotation.setRepayFlag(hlsProductDefinition.getRepayFlag());

        //宽限期类型
        if (StringUtil.isNotEmpty(prjExcelImportDto.getGraceType())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(GRACE_TYPE, prjExcelImportDto.getGraceType());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 56, IPrjProjectService.GRACE_TYPE_MATCH_ERROR));
            }
            prjQuotation.setGraceType(value);
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 56, IPrjProjectService.NOT_NULL));
        }
        //自定义宽限期月份,宽限类型50的时候做必填校验
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
        //租赁期限(月数)
        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseTerm())) {
            prjQuotation.setLeaseTerm(Double.valueOf(prjExcelImportDto.getLeaseTerm()));
            prjQuotation.setLeaseTermM(String.valueOf(prjExcelImportDto.getLeaseTerm()));
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 58, IPrjProjectService.NOT_NULL));
        }

        //支付频率
        prjQuotation.setAnnualPayTimes(hlsProductDefinition.getAnnualPayTimes());
        prjQuotation.setLeaseTimes(prjQuotation.getLeaseTerm().longValue() / 12 * Long.valueOf(prjQuotation.getAnnualPayTimes()));

        //逾期宽限类型
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

        //保证金处理方式
        prjQuotation.setDepositProcess(DEPOSIT_PROCESS_10);


        //名义货价
        if (StringUtil.isNotEmpty(prjExcelImportDto.getResidualValue())) {
            prjQuotation.setResidualValue(Double.valueOf(prjExcelImportDto.getResidualValue()));
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 59, IPrjProjectService.NOT_NULL));
        }

        //付款方式
        if (StringUtils.isEmpty(prjExcelImportDto.getPaymentType())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 61, IPrjProjectService.NOT_NULL));
        } else {
            if (!codeValueMapper2.selectCodeNamesByCode(SynPrjInfoConstants.PRJ_PAYMENT_METHOD1).contains(prjExcelImportDto.getPaymentType())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 61, "值描述有误！"));
            } else {
                prjQuotation.setPaymentType(codeValueMapper2.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.PRJ_PAYMENT_METHOD1, prjExcelImportDto.getPaymentType()));
            }
            //如果付款方式为银承，校验承兑期限、银行保证金、银承手续费比例
            if (StringUtils.equals(prjQuotation.getPaymentType(), "BANK_ACCEPTANCE")) {
                //承兑期限
                if (StringUtils.isEmpty(prjExcelImportDto.getAcceptanceTerm())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 64, IPrjProjectService.NOT_NULL));
                } else {
                    if (!codeValueMapper2.selectCodeNamesByCode(SynPrjInfoConstants.PRJ_ACCEPTANCE_TERM).contains(prjExcelImportDto.getAcceptanceTerm())) {
                        throw new HlsCusException(getExceptionInfo(sheetName, i, 64, "值描述有误！"));
                    } else {
                        prjQuotation.setAcceptanceTerm(codeValueMapper2.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.PRJ_ACCEPTANCE_TERM, prjExcelImportDto.getAcceptanceTerm()));
                    }
                }
                //银承保证金比例
                if (StringUtils.isEmpty(prjExcelImportDto.getBankDepositRatio())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 62, IPrjProjectService.NOT_NULL));
                } else {
                    prjQuotation.setBankDepositRatio(Double.valueOf(prjExcelImportDto.getBankDepositRatio()));
                }

                //银承手续费比例
                if (StringUtils.isEmpty(prjExcelImportDto.getBankLeaseChargeRatio())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 63, IPrjProjectService.NOT_NULL));
                } else {
                    prjQuotation.setBankLeaseChargeRatio(Double.valueOf(prjExcelImportDto.getBankLeaseChargeRatio()));
                }
            }
        }

        //租金比例报价字段
        if (StringUtils.isNotEmpty(prjExcelImportDto.getPayMethodDesc()) && !ArrayUtils.contains(PAY_METHOD_DESCS, prjExcelImportDto.getPayMethodDesc())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 65, "值描述有误！"));
        } else {
            prjQuotation.setPayMethodDesc(StringUtils.substring(prjExcelImportDto.getPayMethodDesc(), 0, 4));
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getLeaseChargeTimes())) {
            prjQuotation.setLeaseChargeTimes(prjExcelImportDto.getLeaseChargeTimes());
        }

        //当存在租/本比例时，判断租本比例相加是否为1
        Double rentalPrincipalRatio = 0D;
        Boolean rentalPrincipalRatioFlag = false;

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio1())) {
            prjQuotation.setRentalPrincipalRatio1(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio1()));
            rentalPrincipalRatio = MathUtil.add(rentalPrincipalRatio, Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio1()));
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 67, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio2())) {
            prjQuotation.setRentalPrincipalRatio2(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio2()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio2()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 68, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio3())) {
            prjQuotation.setRentalPrincipalRatio3(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio3()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio3()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 69, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio4())) {
            prjQuotation.setRentalPrincipalRatio4(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio4()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio4()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 70, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio5())) {
            prjQuotation.setRentalPrincipalRatio5(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio5()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio5()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 71, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio6())) {
            prjQuotation.setRentalPrincipalRatio6(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio6()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio6()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 72, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio7())) {
            prjQuotation.setRentalPrincipalRatio7(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio7()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio7()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 73, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio8())) {
            prjQuotation.setRentalPrincipalRatio8(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio8()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio8()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 74, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio9())) {
            prjQuotation.setRentalPrincipalRatio9(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio9()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio9()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 75, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio10())) {
            prjQuotation.setRentalPrincipalRatio10(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio10()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio10()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 76, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio11())) {
            prjQuotation.setRentalPrincipalRatio11(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio11()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio11()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 77, IPrjProjectService.NOT_NULL));
            }
        }

        if (StringUtils.isNotEmpty(prjExcelImportDto.getRentalPrincipalRatio12())) {
            prjQuotation.setRentalPrincipalRatio12(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio12()));
            rentalPrincipalRatio = MathUtil.add(Double.valueOf(prjExcelImportDto.getRentalPrincipalRatio12()), rentalPrincipalRatio);
            rentalPrincipalRatioFlag = true;
        } else {
            if (ArrayUtils.contains(PAY_METHOD_DESCS_MONTHS, prjExcelImportDto.getPayMethodDesc())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 78, IPrjProjectService.NOT_NULL));
            }
        }

        if (rentalPrincipalRatio != 1D && rentalPrincipalRatioFlag == true) {
            throw new HlsCusException("租/本比例之和不为1！");
        }

        //宽限类型-特殊方案
        if (StringUtils.isNotEmpty(prjExcelImportDto.getGraceTypeSpecial())) {
            if (!codeValueMapper2.selectCodeNamesByCode(SynPrjInfoConstants.GRACE_TYPE_SPECIAL).contains(prjExcelImportDto.getGraceTypeSpecial())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 79, "值有误！"));
            } else {
                prjQuotation.setGraceTypeSpecial(codeValueMapper2.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.GRACE_TYPE_SPECIAL, prjExcelImportDto.getGraceTypeSpecial()));
            }
        }

        return prjQuotation;
    }

    //处理进件信息
    HlsCusPrjProject importPrjProject(IRequest iRequest, PrjExcelImportDto prjExcelImportDto, String sheetName, int i, Long bpId, String division) throws HlsCusException {
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        String emplyeeCode = "";

        hlsCusPrjProject.setProjectStatus(PROJECT_STATUS_NEW);
        hlsCusPrjProject.setContractTextStatus("UNCREATED");
//        hlsCusPrjProject.setSpecialAttachmentFlag(BaseConstants.NO);
        hlsCusPrjProject.setCompanyId(iRequest.getCompanyId());

        //厂商签约方式
        if (StringUtil.isNotEmpty(prjExcelImportDto.getManufacturerSignType())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", prjExcelImportDto.getManufacturerSignType());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 84, "描述有误！"));
            }
            hlsCusPrjProject.setManufacturerSignType(value);
        }
//        else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 84, IPrjProjectService.NOT_NULL));
//        }

        //主机厂签约方式
        if (StringUtil.isNotEmpty(prjExcelImportDto.getVenderSignType())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", prjExcelImportDto.getVenderSignType());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 85, "描述有误！"));
            }
            hlsCusPrjProject.setVenderSignType(value);
        }
//        else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 85, IPrjProjectService.NOT_NULL));
//        }

        //经销商签约方式
        if (StringUtil.isNotEmpty(prjExcelImportDto.getDealerSignType())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue("SIGN_METHOD", prjExcelImportDto.getDealerSignType());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 86, "描述有误！"));
            }
            hlsCusPrjProject.setDealerSignType(value);
        }
//        else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 86, IPrjProjectService.NOT_NULL));
//        }

        //商用车业务线
        if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
            //是否抵押越秀
//            if (StringUtil.isNotEmpty(prjExcelImportDto.getPledgeFlag())) {
//                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.YES_AND_NO, prjExcelImportDto.getPledgeFlag());
//                if (StringUtils.isEmpty(value)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 87, "描述有误！"));
//                }
//                hlsCusPrjProject.setPledgeFlag(value);
//            } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 87, IPrjProjectService.NOT_NULL));
//            }
            //保险购买情况
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceFlag())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(INSURANCE_PURCHASE_STATUS, prjExcelImportDto.getInsuranceFlag());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 88, "描述有误！"));
                }
                hlsCusPrjProject.setInsuranceFlag(value);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 88, IPrjProjectService.NOT_NULL));
            }
        } else {
//            //保险购买情况
            if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceFlag())) {
                String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(INSURANCE_PURCHASE_STATUS, prjExcelImportDto.getInsuranceFlag());
                if (StringUtils.isEmpty(value)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 87, "描述有误！"));
                }
                hlsCusPrjProject.setInsuranceFlag(value);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 87, IPrjProjectService.NOT_NULL));
            }
        }
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getInsuranceFlag())) {
//        String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(INSURANCE_PURCHASE_STATUS, prjExcelImportDto.getInsuranceFlag());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 87, "描述有误！"));
//            }
//        hlsCusPrjProject.setInsuranceFlag(value);
//        } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 87, IPrjProjectService.NOT_NULL));
//        }
        /*hlsCusPrjProject.setLeaseOrganization(session.getAttribute("unitCode").toString());*/
        hlsCusPrjProject.setTenantId(bpId);
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setEnabledFlag("Y");
        //单据类别
//        hlsCusPrjProject.setDocumentCategory(PROJECT_DOCUMENT_CATEGORY);
          hlsCusPrjProject.setDocumentCategory("PRJ_PROJECT_IMPORT");
        //单据类型
        if (StringUtil.isNotEmpty(prjExcelImportDto.getDocumentType())) {
            DocumentType documentType = new DocumentType();
            documentType.setDocumentCategory(hlsCusPrjProject.getDocumentCategory());
            documentType.setDescription(prjExcelImportDto.getDocumentType());
            List<DocumentType> documentTypes = documentTypeMapper.select(documentType);
            if (CollectionUtils.isEmpty(documentTypes) || documentTypes.size() != 1) {
//                throw new HlsCusException(DATA_ECXEPTION);
                throw new HlsCusException(getExceptionInfo(sheetName, i, 41, "单据类型有误！"));
            }
            hlsCusPrjProject.setDocumentType(documentTypes.get(0).getDocumentType());

            //业务类型
            BusinessType businessType = new BusinessType();
            businessType.setDocumentCategory(hlsCusPrjProject.getDocumentCategory());
            businessType.setDescription(prjExcelImportDto.getDocumentType());
            List<BusinessType> businessTypeList = businessTypeMapper.select(businessType);
            if (CollectionUtils.isEmpty(businessTypeList) || businessTypeList.size() != 1) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 41, "单据类型有误！"));
            }
            hlsCusPrjProject.setBusinessType(businessTypeList.get(0).getBusinessType());
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 41, IPrjProjectService.NOT_NULL));
        }

        //商业模式
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
        //项目经理
        /*if (StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_24())) {
            FndEmployee fndEmployee = new FndEmployee();
            fndEmployee.setName(fndInterfaceLines.getAttributes_24());
            List<FndEmployee> fndEmployeeList = fndEmployeeMapper.select(fndEmployee);
            if (CollectionUtils.isEmpty(fndEmployeeList) || fndEmployeeList.size() != 1) {
                throw new HlsCusException(DATA_ECXEPTION);
            }
            hlsCusPrjProject.setEmployeeId(fndEmployeeList.get(0).getEmployeeId());
            emplyeeCode = fndEmployeeList.get(0).getEmployeeCode();

            //根据项目经理查询业务部门，若多个，则取第一个
            FndEmployeeAssigns fndEmployeeAssigns = new FndEmployeeAssigns();
            fndEmployeeAssigns.setEmployeeId(hlsCusPrjProject.getEmployeeId());
            List<FndEmployeeAssigns> assignsList = fndEmployeeAssignsMapper.select(fndEmployeeAssigns);
            if (CollectionUtils.isEmpty(assignsList)) {
                throw new HlsCusException(DATA_ECXEPTION);
            }
            FndOrgUnit fndOrgUnit = new FndOrgUnit();
            fndOrgUnit.setUnitId(assignsList.get(0).getUnitId());
            fndOrgUnit = fndOrgUnitMapper.selectByPrimaryKey(fndOrgUnit);
            hlsCusPrjProject.setLeaseOrganization(fndOrgUnit.getUnitCode());
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 24, IPrjProjectService.NOT_NULL));
        }*/

        //经销商名称
        if (StringUtil.isNotEmpty(prjExcelImportDto.getDealerName())) {
            hlsBpMaster.setBpName(prjExcelImportDto.getDealerName());
            try {
                hlsBpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectOne(hlsBpMaster);
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 43, "经销商系统匹配到多个商业伙伴！"));
            }

            if (hlsBpMaster == null) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 43, IPrjProjectService.NOT_NULL));
            }
            hlsCusPrjProject.setBpIdVender(hlsBpMaster.getBpId());
        }/*else{
            throw new HlsCusException(getExceptionInfo(sheetName,i,25,IPrjProjectService.NOT_NULL));
        }*/
        //归属主机厂
        if (StringUtil.isNotEmpty(prjExcelImportDto.getManufacturerName())) {
            hlsBpMaster = new HlsCusBpMaster();
            hlsBpMaster.setEnabledFlag("Y");
            hlsBpMaster.setBpName(prjExcelImportDto.getManufacturerName());
            try {
                hlsBpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectOne(hlsBpMaster);
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 44, "归属主机厂系统匹配到多个商业伙伴！"));
            }
            if (hlsBpMaster == null) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 44, "归属主机厂名称有误！"));
            }
            hlsCusPrjProject.setFactoryId(hlsBpMaster.getBpId());
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 44, IPrjProjectService.NOT_NULL));
        }

        //查询价目表是否存在
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

        //查询用户对应的主机厂或者供应商,判断与用户维护的主机厂或者供应商是否一致
        User user = new User();
        user.setUserId(iRequest.getUserId());
        user = userMapper.selectByPrimaryKey(user);
        if ("VENDER".equalsIgnoreCase(user.getBpCategory())) {
            if (!hlsCusPrjProject.getFactoryId().equals(user.getBpId())) {
                throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(FACTORY_EXCEPTION).toString());
            }
        } else if ("DEALER".equalsIgnoreCase(user.getBpCategory())) {
            if (hlsCusPrjProject.getBpIdVender() != null && !hlsCusPrjProject.getBpIdVender().equals(user.getBpId())) {
                throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(DEALER_EXCEPTION).toString());
            }
        }
        //保存厂商信息
        HlsCusBpMasterRelation hlsBpMasterRelation = new HlsCusBpMasterRelation();
        hlsBpMasterRelation.setBpId(hlsCusPrjProject.getFactoryId());
        hlsBpMasterRelation.setRelationType("MANUFACTURER");
        hlsBpMasterRelation.setEnabledFlag(BaseConstants.YES);
        try {
            hlsBpMasterRelation = (HlsCusBpMasterRelation) hlsBpMasterRelationMapper.selectOne(hlsBpMasterRelation);
        } catch (Exception e) {
            throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行主机厂查找不到对应的厂商信息！").toString());
        }

        hlsCusPrjProject.setManufacturerId(hlsBpMasterRelation.getRelatedBpId());

        //如果是中联重科厂商 才能保存合作方进件序号和合作方支付表编号

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

        hlsCusPrjProject.setProjectNumber(getProjectNumber(iRequest, hlsCusPrjProject));
        //预计起租日
        if (StringUtil.isNotEmpty(prjExcelImportDto.getLeaseStartDate())) {
            try {
                hlsCusPrjProject.setLeaseStartDate(simpleDateFormat.parse(prjExcelImportDto.getLeaseStartDate()));
            } catch (ParseException e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 46, IPrjProjectService.NOT_NULL));
            }
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 46, IPrjProjectService.NOT_NULL));
        }
        /*//保险购买情况
        if(StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_26())){
            String value  = codeValueMapper.selectCodeValuesByCodeNameAndValue(IPrjProjectService.YES_AND_NO, fndInterfaceLines.getAttributes_26());
            hlsCusPrjProject.setInsuranceFlag(value);
        }else{
            throw new HlsCusException(getExceptionInfo(sheetName,i,26,IPrjProjectService.NOT_NULL));
        }*/

        /*//调息规则
        if(StringUtil.isNotEmpty(fndInterfaceLines.getAttributes_27())){
            String value  = codeValueMapper.selectCodeValuesByCodeNameAndValue(FLOATING_RANGE_METHOD, fndInterfaceLines.getAttributes_27());
            hlsCusPrjProject.setFloatingRangeMethod(value);
        }else{
            throw new HlsCusException(getExceptionInfo(sheetName,i,27,IPrjProjectService.NOT_NULL));
        }*/

        //是否二手机
        if (StringUtil.isNotEmpty(prjExcelImportDto.getSecondHandFlag())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.PRJ_EQUIPMENT_TYPE, prjExcelImportDto.getSecondHandFlag());
            if (StringUtils.isEmpty(value)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 47, "描述有误！"));
            }
            hlsCusPrjProject.setSecondHandFlag(value);
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 47, IPrjProjectService.NOT_NULL));
        }

        //是否经销商模式
//        if (StringUtil.isNotEmpty(prjExcelImportDto.getVenderModeFlag())) {
//            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.YES_AND_NO, prjExcelImportDto.getVenderModeFlag());
//            if (StringUtils.isEmpty(value)) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 48, "描述有误！"));
//            }
//            hlsCusPrjProject.setVenderModeFlag(value);
//        } else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 48, IPrjProjectService.NOT_NULL));
//        }

        //基准利率类型
        hlsCusPrjProject.setBaseRateType(PBOC);

        //币种
        hlsCusPrjProject.setCurrency(CNY);

        //出卖人
        if (StringUtils.isEmpty(prjExcelImportDto.getSellName())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 60, IPrjProjectService.NOT_NULL));
        }
        hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setEnabledFlag("Y");
        hlsBpMaster.setBpName(prjExcelImportDto.getSellName());
        try {
            hlsBpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectOne(hlsBpMaster);
        } catch (Exception e) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 60, "查找到多个出卖人！"));
        }

        if (hlsBpMaster == null) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 60, IPrjProjectService.SELLER_NOT_EXISTS));
        }
        hlsCusPrjProject.setSellerId(hlsBpMaster.getBpId());

        hlsCusPrjProject.setDivision(division);
        hlsCusPrjProject.setAuthorityRuleString(generateAuthorityString(iRequest, hlsCusPrjProject.getDivision() == null ? "" : hlsCusPrjProject.getDivision(), hlsCusPrjProject.getDocumentType(), hlsCusPrjProject.getBusinessType(), emplyeeCode));

        //产品方案
        HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
        if (StringUtil.isNotEmpty(prjExcelImportDto.getPlanName())) {
            hlsProductDefinition.setDefinitionName(prjExcelImportDto.getPlanName());
            hlsProductDefinition.setEnabledFlag(Y);
            try {
                hlsProductDefinition = hlsProductDefinitionMapper.selectOne(hlsProductDefinition);
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "查询到多个产品！"));
            }

            if (Objects.isNull(hlsProductDefinition)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 49, "未找到相应产品！"));
            }

            hlsCusPrjProject.setPaymentBpType(hlsProductDefinition.getPaymentBpType());
//            hlsCusPrjProject.setRepayBpType(hlsProductDefinition.getRepayBpType());
            //根据付款对象类型给付款对象赋值
            switch (hlsCusPrjProject.getPaymentBpType()) {
                case SynPrjInfoConstants.MANUFACTURER:
                case SynPrjInfoConstants.PAYMENT_BP_TYPE_PARTNERS:
                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getManufacturerId());
                    break;
                case SynPrjInfoConstants.TENANT:
                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getTenantId());
                    break;
                case SynPrjInfoConstants.VENDER:
                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getFactoryId());
                    break;
                case SynPrjInfoConstants.DEALER:
                    hlsCusPrjProject.setPaymentBpId(hlsCusPrjProject.getBpIdVender());
                    break;
            }
            //判断付款对象是否为空
            if (null == hlsCusPrjProject.getPaymentBpId()) {
                String paymentBpType = codeValueMapper2.getMeaningByCodeAndCodeValue(SynPrjInfoConstants.PAYMENT_BP_TYPE, hlsCusPrjProject.getPaymentBpType());
                throw new HlsCusException(new StringBuffer("产品付款对象类型为").append(paymentBpType).append(",").append(paymentBpType)
                        .append("字段不能为空！").toString());
            }

        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 49, IPrjProjectService.NOT_NULL));
        }

        //签约方式
        if (StringUtil.isNotEmpty(prjExcelImportDto.getSignType())) {
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue(SynPrjInfoConstants.SIGN_METHOD, prjExcelImportDto.getSignType());
            if (StringUtils.isNotEmpty(value)) {
                hlsCusPrjProject.setSignType(value);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 45, "值描述有误!"));
            }
        }
//        else {
//            throw new HlsCusException(getExceptionInfo(sheetName, i, 45, IPrjProjectService.NOT_NULL));
//        }

        //租金计划是否通知客户不能为空
        if (StringUtil.isNotEmpty(prjExcelImportDto.getRentPlanNoticeFlag())) {
            String rentPlanNoticeFlag = codeValueMapper2.selectCodeValuesByCodeNameAndValue("SYS.YES_NO", prjExcelImportDto.getRentPlanNoticeFlag());
            if (StringUtils.isEmpty(rentPlanNoticeFlag)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 80, "描述有误！"));
            }
            hlsCusPrjProject.setRentPlanNoticeFlag(rentPlanNoticeFlag);
        } else {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 80, IPrjProjectService.NOT_NULL));
        }
        self().insertSelective(iRequest, hlsCusPrjProject);

        return hlsCusPrjProject;
    }

    //处理商业伙伴地址信息
    void importBpMasterAddress(IRequest iRequest, PrjExcelImportDto excelInfo, String sheetName, int i, HlsCusBpMaster hlsBpMaster) throws HlsCusException {
        HlsCusBpMasterAddress hlsBpMasterAddress = new HlsCusBpMasterAddress();
        hlsBpMasterAddress.setBpId(hlsBpMaster.getBpId());
        //地址类型
        if (StringUtil.isEmpty(excelInfo.getAddressType())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 20, IPrjProjectService.NOT_NULL));
        } else {
            String address = codeValueMapper2.selectCodeValuesByCodeNameAndValue("FND.BP_ADDRESS_TYPE", excelInfo.getAddressType());
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
            hlsBpMasterAddress.setCountry(String.valueOf(fc.getCountry()));
        }
        //省
        if (StringUtil.isEmpty(excelInfo.getProvinceId())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 22, IPrjProjectService.NOT_NULL));
        } else {
            FndProvince fp = new FndProvince();
            fp.setDescription(excelInfo.getProvinceId());
            try {
                fp = fndProvinceMapper.selectOne(fp);
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 22, IPrjProjectService.NOT_NULL));
            }
            if (Objects.isNull(fp)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 22, "请填写正确省市名称"));
            }
            hlsBpMasterAddress.setProvinceId(Long.valueOf(fp.getProvince()));
            hlsBpMasterAddress.setProvince(String.valueOf(fp.getProvince()));
        }
        //市
        if (StringUtil.isEmpty(excelInfo.getCityId())) {
            throw new HlsCusException(getExceptionInfo(sheetName, i, 23, IPrjProjectService.NOT_NULL));
        } else {
            FndCity fc = new FndCity();
            fc.setDescription(excelInfo.getCityId());
            fc.setProvince(hlsBpMasterAddress.getProvinceId().toString());
            try {
                fc = fndCityMapper.selectOne(fc);
                if (fc == null) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 23, "请填写正确省市名称"));
                }
            } catch (Exception e) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 23, IPrjProjectService.NOT_NULL));
            }
            hlsBpMasterAddress.setCityId(Long.valueOf(fc.getCity()));
            hlsBpMasterAddress.setCity(String.valueOf(fc.getCity()));
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
//        if (IPrjProjectService.NP.equals(hlsBpMaster.getBpClass())) {
//            //手机
//            if (StringUtil.isEmpty(excelInfo.getMobilePhone())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 26, IPrjProjectService.NOT_NULL));
//            } else {
//                String regExp = "^(1)\\d{10}$";
//                String regExp1 = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
//                Pattern p = Pattern.compile(regExp);
//                Pattern p1 = Pattern.compile(regExp1);
//                Matcher m = p.matcher(excelInfo.getMobilePhone());
//                Matcher m1 = p1.matcher(excelInfo.getMobilePhone());
//                if (m.matches() || m1.matches()) {
//                    hlsBpMasterAddress.setCellPhone(excelInfo.getMobilePhone());
//                }else {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 26, "联系方式不符合规范，请检查！"));
//                }
//            }
//            if (!StringUtil.isEmpty(excelInfo.getPhone())) {
//                hlsBpMasterAddress.setPhone(excelInfo.getPhone());
//            }
//        } else if (IPrjProjectService.ORG.equals(hlsBpMaster.getBpClass())) {
//            if (StringUtil.isEmpty(excelInfo.getPhone())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 25, IPrjProjectService.NOT_NULL));
//            } else {
//                hlsBpMasterAddress.setPhone(excelInfo.getPhone());
//            }
//            if (!StringUtil.isEmpty(excelInfo.getMobilePhone())) {
//                String regExp = "^(1)\\d{10}$";
//                String regExp1 = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
//                Pattern p = Pattern.compile(regExp);
//                Pattern p1 = Pattern.compile(regExp1);
//                Matcher m = p.matcher(excelInfo.getMobilePhone());
//                Matcher m1 = p1.matcher(excelInfo.getMobilePhone());
//                if (m.matches() || m1.matches()) {
//                    hlsBpMasterAddress.setCellPhone(excelInfo.getMobilePhone());
//                } else {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 25, "手机号码格式错误"));
//                }
//            }
//        }
        hlsBpMasterAddressMapper.insert(hlsBpMasterAddress);
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
            if (regBlank(excelInfo.getMobilePhone()) || regEn(excelInfo.getMobilePhone()) || regCn(excelInfo.getMobilePhone())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 26, "含有非法字符（包括空格），请检查!"));
            }
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
            String value = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_ID_TYPE", excelInfo.getIdType());
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
            String cardType = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.ZX_REGNOTYPE, excelInfo.getIdType());
            if (StringUtils.isEmpty(cardType)) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 5, "证件类型描述有误!"));
            }
            hlsBpMaster.setRegnotype(cardType);
            hlsBpMaster.setRegistrationNumType(cardType);
            hlsBpMasterExample.setRegnotype(cardType);
            hlsBpMasterExample.setRegistrationNumType(cardType);
            if (StringUtils.length(excelInfo.getIdCardNo()) != 15 && StringUtils.length(excelInfo.getIdCardNo()) != 18) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 6, "证件号码有误！"));
            }
            //证件类型为营业执照号
            if (IPrjProjectService.BUSINESS_LICENSE_DESC.equals(excelInfo.getIdType())) {
                hlsBpMaster.setBusinessLicenseNum(excelInfo.getIdCardNo());
                hlsBpMaster.setRegisterCertNum(excelInfo.getIdCardNo());
                hlsBpMasterExample.setBusinessLicenseNum(excelInfo.getIdCardNo());
                hlsBpMasterExample.setRegisterCertNum(excelInfo.getIdCardNo());
            }
            //证件类型为统一社会信用代码
            if (IPrjProjectService.UNIFIED_SOCIAL_CREDIT.equals(excelInfo.getIdType())) {
                hlsBpMaster.setRegno(excelInfo.getIdCardNo());
                hlsBpMaster.setRegisterCertNum(excelInfo.getIdCardNo());
                hlsBpMasterExample.setRegno(excelInfo.getIdCardNo());
                hlsBpMasterExample.setRegisterCertNum(excelInfo.getIdCardNo());
            }
        }
        List<HlsCusBpMaster> masterList1 = hlsBpMasterMapper.select(hlsBpMasterExample);

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
        if (CollectionUtils.isNotEmpty(masterList1)) {
            if (masterList1.size() > 1) {
                throw new HlsCusException(sheetName + "第" + i + "行查询到多个商业伙伴,请检查数据!");
            }
            hlsBpMaster = masterList1.get(0);

            hlsBpMaster.setExistFlag(Y);
        }

        //共有数据
        hlsBpMaster.setBpName(excelInfo.getBpName());
        hlsBpMaster.setOwnerUserId(iRequest.getUserId());
        hlsBpMaster.setCreatedBy(iRequest.getUserId());
        hlsBpMaster.setCreationDate(new Date());
        hlsBpMaster.setBpCategory(type);
        hlsBpMaster.setBpType(type);
        hlsBpMaster.setEnabledFlag(Y);
        //国别/国家默认中国
        hlsBpMaster.setNationality("46");
        //自然人
        if (IPrjProjectService.NP.equals(hlsBpMaster.getBpClass())) {
            //设置证件类型
            String cardType = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdType());
            hlsBpMaster.setIdType(cardType);

            //性别
            if (StringUtil.isEmpty(excelInfo.getGenger())) {
                throw new HlsCusException(IPrjProjectService.GENDER_NOT_NULL);
            } else {
                String gender = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HR.EMPLOYEE_GENDER", excelInfo.getGenger());
                if (StringUtils.isEmpty(gender)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 7, "性别描述有误！"));
                }
                hlsBpMaster.setGender(gender);
            }
            //婚姻状况
            if (StringUtil.isEmpty(excelInfo.getMaritalStatus())) {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 8, IPrjProjectService.NOT_NULL));
            } else {
                String maritalStatus = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.MARITAL_STATUS_SYS_CODE, excelInfo.getMaritalStatus());
                if (StringUtils.isEmpty(maritalStatus)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 8, "描述有误！"));
                }
                hlsBpMaster.setMaritalStatus(maritalStatus);
            }

            //学历
//            if (StringUtil.isEmpty(excelInfo.getAcademicBackground())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "学历不能为空!"));
//            } else {
//                String academicBackground = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_ACADEMIC_BACKGROUND", excelInfo.getAcademicBackground());
//                if (StringUtils.isEmpty(academicBackground)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
//                }
//                hlsBpMaster.setAcademicBackground(academicBackground);
//                hlsBpMaster.setHighestDegree(academicBackground);
//
//            }
            if (StringUtil.isNotEmpty(excelInfo.getAcademicBackground())) {
                String academicBackground = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_ACADEMIC_BACKGROUND", excelInfo.getAcademicBackground());
                if (StringUtils.isEmpty(academicBackground)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 9, "描述有误！"));
                }
                hlsBpMaster.setAcademicBackground(academicBackground);
                hlsBpMaster.setHighestDegree(academicBackground);

            }
            //学位(必填)
//            if (StringUtils.isEmpty(excelInfo.getAcademicDegree())) {
//                throw new HlsCusException(getExceptionInfo(PROJECT_TENANT_SHEET, i, 10, "学位不能为空!"));
//            } else {
//                String academicBackground = codeValueMapper2.selectCodeValuesByCodeNameAndValue("ACADEMIC_DEGREE", excelInfo.getAcademicDegree());
//                if (StringUtils.isEmpty(academicBackground)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 10, "描述有误！"));
//                }
//                hlsBpMaster.setAcademicDegree(academicBackground);
//            }
            if (StringUtils.isNotEmpty(excelInfo.getAcademicDegree())) {
                String academicBackground = codeValueMapper2.selectCodeValuesByCodeNameAndValue("ACADEMIC_DEGREE", excelInfo.getAcademicDegree());
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
            }
//            else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 11, IPrjProjectService.NOT_NULL));
//            }

            //配偶证件类型
            if (StringUtil.isNotEmpty(excelInfo.getIdTypeSp())) {
                String idTypeSp = codeValueMapper2.selectCodeValuesByCodeNameAndValue(IPrjProjectService.NP_CARD_SYS_CODE, excelInfo.getIdTypeSp());
                if (StringUtils.isEmpty(idTypeSp)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 12, "描述有误！"));
                }
                if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 12, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                }
                hlsBpMaster.setIdTypeSp(idTypeSp);
            }
//            else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 12, IPrjProjectService.NOT_NULL));
//            }

            //配偶证件号码
            if (StringUtil.isNotEmpty(excelInfo.getIdCardNoSp())) {
                hlsBpMaster.setIdCardNoSp(excelInfo.getIdCardNoSp());
//                if (ID_CARD.equals(hlsBpMaster.getIdTypeSp())) {
//                    if (IDUtils.isValidatedAllIdcard(excelInfo.getIdCardNoSp())) {
//                        hlsBpMaster.setIdCardNoSp(excelInfo.getIdCardNoSp());
//                        try {
//                            IDUtils.Person person = IDUtils.getBirAgeSex(excelInfo.getIdCardNoSp());
//                            hlsBpMaster.setDateOfBirthSp(person.getBirthDate());
//                            hlsBpMaster.setGenderSp(person.getSex());
//                            hlsBpMaster.setAgeSp(person.getAge());
//                        } catch (ParseException e) {
//                            throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(NP_ID_CARD_EXCEPTION).toString());
//                        }
//                    } else {
//                        throw new HlsCusException(new StringBuffer(sheetName).append("第").append(i).append("行").append(ID_CARD_EXCEPTION).toString());
//                    }
//                }
                if (!IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 13, "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确"));
                }
            }
            if (StringUtils.isNotEmpty(excelInfo.getGenderSp())) {
                String genderSp = codeValueMapper2.selectCodeValuesByCodeNameAndValue("HLS211_GENDER", excelInfo.getGenderSp());
                hlsBpMaster.setGenderSp(genderSp);
            }
//            else if (IPrjProjectService.MARRIED.equals(excelInfo.getMaritalStatus())) {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 13, IPrjProjectService.NOT_NULL));
//            }

            //手机号码
            if (StringUtils.isNotEmpty(excelInfo.getMobilePhone())) {
                hlsBpMaster.setCellPhone(excelInfo.getMobilePhone());
            }

            //居住状况
            if (StringUtil.isNotEmpty(excelInfo.getHouseProperty())) {
                String houseProperty = codeValueMapper2.selectCodeValuesByCodeNameAndValue("BP.HOUSE_PROPERTY", excelInfo.getHouseProperty());
                if (StringUtils.isEmpty(houseProperty)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 19, "描述有误！"));
                }
                hlsBpMaster.setHouseProperty(houseProperty);
            }
//            else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 19, IPrjProjectService.NOT_NULL));
//            }
            //就业状况
            if (StringUtil.isNotEmpty(excelInfo.getEmploymentStatus())) {
                String employmentStatus = codeValueMapper2.selectCodeValuesByCodeNameAndValue("EMPLOYMENT_STATUS", excelInfo.getEmploymentStatus());
                if (StringUtils.isEmpty(employmentStatus)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 27, "描述有误！"));
                }
                hlsBpMaster.setEmploymentStatus(employmentStatus);
            }
//            else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 27, IPrjProjectService.NOT_NULL));
//            }
            //单位名称
            if (StringUtil.isNotEmpty(excelInfo.getWorkingPlace())) {
                hlsBpMaster.setWorkingPlace(excelInfo.getWorkingPlace());
                hlsBpMaster.setWorkingCompany(excelInfo.getWorkingPlace());
            }
//            else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 28, IPrjProjectService.NOT_NULL));
//            }

            //职业性质
            if (StringUtil.isNotEmpty(excelInfo.getJobNature())) {
                String jobNature = codeValueMapper2.selectCodeValuesByCodeNameAndValue("BP.JOB_NATURE", excelInfo.getJobNature());
                if (StringUtils.isEmpty(jobNature)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 29, "描述有误！"));
                }
                hlsBpMaster.setJobNature(jobNature);
            }
//            else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 29, IPrjProjectService.NOT_NULL));
//            }

            //行业
//            if (StringUtil.isNotEmpty(excelInfo.getIndustry())) {
//                String industry = codeValueMapper2.selectCodeValuesByCodeNameAndValue("INDUSTRY_ORG", excelInfo.getIndustry());
//                if (StringUtils.isEmpty(industry)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 30, "描述有误！"));
//                }
//                hlsBpMaster.setIndustry(industry);
//            } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 30, IPrjProjectService.NOT_NULL));
//            }

            //职业
            if (StringUtil.isNotEmpty(excelInfo.getProfession())) {
                String profession = codeValueMapper2.selectCodeValuesByCodeNameAndValue("PROFESSION", excelInfo.getProfession());
                if (StringUtils.isEmpty(profession)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 31, "描述有误！"));
                }
                hlsBpMaster.setProfession(profession);
            }
//            else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 31, IPrjProjectService.NOT_NULL));
//            }

            //职务
            if (StringUtil.isNotEmpty(excelInfo.getPosition())) {
                String position = codeValueMapper2.selectCodeValuesByCodeNameAndValue("POSITION", excelInfo.getPosition());
                if (StringUtils.isEmpty(position)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 32, "描述有误！"));
                }
                hlsBpMaster.setPosition(position);
            }
//            else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 32, IPrjProjectService.NOT_NULL));
//            }

            //职称
            if (StringUtil.isNotEmpty(excelInfo.getJobTitle())) {
                String jobTitle = codeValueMapper2.selectCodeValuesByCodeNameAndValue("JOB_TITLE", excelInfo.getJobTitle());
                if (StringUtils.isEmpty(jobTitle)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 33, "描述有误！"));
                }
                hlsBpMaster.setJobTitle(jobTitle);
            }
//            else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 33, IPrjProjectService.NOT_NULL));
//            }

        } else {
            //法人

            //登记注册号类型 默认统一社会信用代码
            hlsBpMaster.setRegnotype("07");
            //承租人简称
            if (StringUtil.isNotEmpty(excelInfo.getShortName())) {
                hlsBpMaster.setShortName(excelInfo.getShortName());
                hlsBpMaster.setExtraNam(excelInfo.getShortName());
            }
            //实际控制人
            if (StringUtil.isNotEmpty(excelInfo.getActualController())) {
                hlsBpMaster.setActualController(excelInfo.getActualController());
                hlsBpMaster.setActualPerson(excelInfo.getActualController());
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
            }else{
                hlsBpMaster.setIdCardNo(excelInfo.getMainMembersCertCode());
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
//            if (StringUtil.isNotEmpty(excelInfo.getIndustry())) {
//                String industry = codeValueMapper2.selectCodeValuesByCodeNameAndValue("INDUSTRY_ORG", excelInfo.getIndustry());
//                if (StringUtils.isEmpty(industry)) {
//                    throw new HlsCusException(getExceptionInfo(sheetName, i, 30, "描述有误！"));
//                }
//                hlsBpMaster.setIndustry(industry);
//            } else {
//                throw new HlsCusException(getExceptionInfo(sheetName, i, 30, IPrjProjectService.NOT_NULL));
//            }

            //存续状态
            if (StringUtil.isNotEmpty(excelInfo.getSubsistingStatus())) {
                String subsistingStatus = codeValueMapper2.selectCodeValuesByCodeNameAndValue("SUBSISTING_STATUS", excelInfo.getSubsistingStatus());
                if (StringUtils.isEmpty(subsistingStatus)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 34, "描述有误！"));
                }
                hlsBpMaster.setSubsistingStatus(subsistingStatus);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 34, IPrjProjectService.NOT_NULL));
            }

            //组织机构类型
            if (StringUtil.isNotEmpty(excelInfo.getOrganizationType())) {
                String organizationType = codeValueMapper2.getCodeValueByCodeAndMeaning1("FND.ORGTYPESUB", excelInfo.getOrganizationType());
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
                    hlsBpMaster.setManagementEndDate(simpleDateFormat.parse(excelInfo.getLicenseTerms()));
                } catch (ParseException e) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 36, IPrjProjectService.NOT_NULL));
                }
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 36, IPrjProjectService.NOT_NULL));
            }

            //经济类型(公司性质)
            if (StringUtil.isNotEmpty(excelInfo.getCompanyNature())) {
//                String companyNature = codeValueMapper2.selectCodeValuesByCodeNameAndValue("PRJ_NATURE_OF_BUSINESS", excelInfo.getCompanyNature());
                String companyNature = codeValueMapper2.selectCodeValuesByCodeNameAndValue("PRJ_NATURE_OF_BUSINESS", excelInfo.getCompanyNature());
                if (StringUtils.isEmpty(companyNature)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 37, "描述有误！"));
                }
                hlsBpMaster.setCompanyNature(companyNature);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 37, IPrjProjectService.NOT_NULL));
            }

            //企业规模
            if (StringUtil.isNotEmpty(excelInfo.getEnterpriseScale())) {
                String enterpriseScale = codeValueMapper2.selectCodeValuesByCodeNameAndValue("FND.ENTERPRISE_SCALE_TYPE", excelInfo.getEnterpriseScale());
                if (StringUtils.isEmpty(enterpriseScale)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 38, "描述有误！"));
                }
                hlsBpMaster.setEnterpriseScale(enterpriseScale);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 38, IPrjProjectService.NOT_NULL));
            }

            //注册资本币种
            if (StringUtil.isNotEmpty(excelInfo.getRegisteredCapitalCurrency())) {
                String registeredCapitalCurrency = codeValueMapper2.selectCodeValuesByCodeNameAndValue("CHS.CURRENCY_TYPE", excelInfo.getRegisteredCapitalCurrency());
                if (StringUtils.isEmpty(registeredCapitalCurrency)) {
                    throw new HlsCusException(getExceptionInfo(sheetName, i, 39, "描述有误！"));
                }
                hlsBpMaster.setRegisteredCapitalCurrency(registeredCapitalCurrency);
                hlsBpMaster.setRegisterCapitalCur(registeredCapitalCurrency);
            } else {
                throw new HlsCusException(getExceptionInfo(sheetName, i, 39, IPrjProjectService.NOT_NULL));
            }

            //注册资本（万元）
            if (StringUtil.isNotEmpty(excelInfo.getRegisteredCapital())) {
                hlsBpMaster.setRegisteredCapital(String.valueOf(excelInfo.getRegisteredCapital()));
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
//            hlsBpMasterService.updateByPrimaryKeySelective(iRequest,hlsBpMaster);
            hlsBpMasterMapper.updateByPrimaryKeySelective(hlsBpMaster);

        } else {

            //生成商业伙伴编码
            String bpClass = hlsBpMaster.getBpClass();
            String bp_code = fndCodingRuleValuesService.getCodeRuleValue(iRequest, HLS_BP_DOCUMENT_CATEGORY, hlsBpMaster.getBpClass(), hlsBpMaster.getBpClass(), param);
            hlsBpMaster.setBpCode(bp_code);
            String authorityRuleString = hlsBpMasterService.getAuthorityString(iRequest);
            hlsBpMaster.setAuthorityRuleString(authorityRuleString);
//            hlsBpMasterService.insert(iRequest, hlsBpMaster);
            hlsBpMasterMapper.insertSelective(hlsBpMaster);

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
//        hlsBpMaster.setAuthorityRuleString(generateAuthorityString(iRequest,hlsBpMaster.getD));

        return hlsBpMaster;
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
            queryMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectOne(queryMaster);
            List<String> bpIgnoreList = new ArrayList<>(6);
            bpIgnoreList.add("lastUpdateDate");
            bpIgnoreList.add("createdBy");
            bpIgnoreList.add("creationDate");
            bpIgnoreList.add("lastUpdatedBy");
            bpIgnoreList.add("lastUpdatetimeDate");
            bpIgnoreList.add("bpId");
            if (checkDiffer(hlsBpMaster, queryMaster, bpIgnoreList)) {
                logger.info("承租人{},因基础信息不一致需要插入历史表", queryMaster.getBpName());
                return true;
            }

        }else if(StringUtils.equals(hlsBpMaster.getBpClass(), ORG)){
            //社会统一信用代码
            queryMaster.setRegno(hlsBpMaster.getRegno());
            queryMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectOne(queryMaster);
            List<String> bpIgnoreList = new ArrayList<>(6);
            bpIgnoreList.add("lastUpdateDate");
            bpIgnoreList.add("createdBy");
            bpIgnoreList.add("creationDate");
            bpIgnoreList.add("lastUpdatedBy");
            bpIgnoreList.add("lastUpdatetimeDate");
            bpIgnoreList.add("bpId");
            if (checkDiffer(hlsBpMaster, queryMaster, bpIgnoreList)) {
                logger.info("承租人{},因基础信息不一致需要插入历史表", queryMaster.getBpName());
                return true;
            }
        }


        return false;
    }
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
                        logger.info("字段{}为空，将被更新", name);
                        return true;
                    }
                    if (!o1.equals(o2)) {
                        logger.info("字段{}新旧值不相等，将被更新", name);
                        return true;
                    }
                }
                return false;
            }
            logger.info("传入实体类类型不一致");
        } catch (Exception e) {
            logger.error("未知错误导致无法判断是否更新", e);
        }
        return true;
    }
    private PrjExcelImportDto setValueToExcelDto(String excelType, FndInterfaceLines fndInterfaceLines,String division) {
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
            if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
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
            if (StringUtils.equals(division,COMMERCIAL_VEHICLE)) {
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

            }else {
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
    @Override
    public String createQuotationNumber(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        StringBuilder sb = new StringBuilder();
        hlsCusPrjProject = self().selectByPrimaryKey(iRequest, hlsCusPrjProject);
        String projectNumber = hlsCusPrjProject.getProjectNumber();
        /*取后六位流水号*/
        projectNumber = projectNumber.substring(projectNumber.length() - 6, projectNumber.length());
        sb.append(projectNumber);
        /*拼上报价编号数量*/
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
        hlsCusPrjQuotation.setSourceDocumentCategory(PROJECT_DOCUMENT_CATEGORY);
        List<HlsCusPrjQuotation> quotations = prjQuotationMapper.select(hlsCusPrjQuotation);
        sb.append(LINE);
        sb.append(quotations.size() + 1);
        return sb.toString();
    }

    @Override
    public List<Map> getQuotationDefaultValue(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation) {
        return null;
    }

    @Override
    public List<FndAttachment> projectCreateDocx(IRequest iRequest, Long projectId, String templateType) throws Exception {
        return null;
    }

    @Override
    public List<FndAttachment> projectCreateDocxWithLastFlag(IRequest iRequest, Long projectId, String templateType, Boolean lastFlag) throws Exception {
        return null;
    }

    @Override
    public List<FndAttachment> projectCreateDocx(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws Exception {
        return null;
    }

    @Override
    public void projectDocxZipDownload(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusPrjProject hlsCusPrjProject, String documentCategory) throws IOException, HlsCusException {

    }

    @Override
    public void prjSignReturn(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {

    }

    @Override
    public void signCreateDocx(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws Exception {

    }

    @Override
    public void batchCreateSignDocxAndDown(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, List<HlsCusPrjProject> list) throws IOException {

    }

    @Override
    public void updateApplyDate(IRequest iRequest, Long projectId, Date date) {

    }

    @Override
    public void wflBatchApprove(IRequest iRequest, List<Map> list, String comment) throws TaskActionException {

    }

    @Override
    public List<Map> signBatchCalc(IRequest iRequest, HlsCusPrjProject prjProject) throws HlsCusException {
        return null;
    }

    @Override
    public String getProjectNumber(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException {
        //生成编码规则
        String projectNumber;
        Map param = new HashMap();
        projectNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, hlsCusPrjProject.getDocumentCategory(), hlsCusPrjProject.getDocumentType(), hlsCusPrjProject.getBusinessType(), param);
//        //查询框架协议编号
//        BpMasterAgreement bpMasterAgreement = new BpMasterAgreement();
//        bpMasterAgreement.setBpId(hlsCusPrjProject.getManufacturerId());
//        bpMasterAgreement.setEnabledFlag(BaseConstants.YES);
//        bpMasterAgreement = bpMasterAgreementMapper.selectOne(bpMasterAgreement);
//
//        HlsCusBpMaster bpMaster = new HlsCusBpMaster();
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(BP_CODE);
//        String agreementNumgetString = null;
//        if (bpMasterAgreement.getAgreementNum().contains("越合第") && bpMasterAgreement.getAgreementNum().contains("号")) {
//            agreementNumgetString = bpMasterAgreement.getAgreementNum().substring(bpMasterAgreement.getAgreementNum().indexOf("越合第") + 3, bpMasterAgreement.getAgreementNum().indexOf("号"));
//        } else {
//            throw new HlsCusException("厂商的框架协议编号有误!");
//        }
//        sb.append(agreementNumgetString);
//        sb.append("-");
//        if (hlsCusPrjProject.getFactoryId() != null) {
//            //查询主机厂简称
//            bpMaster.setBpId(hlsCusPrjProject.getFactoryId());
//            bpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectByPrimaryKey(bpMaster);
//
//            if (bpMasterAgreement == null || bpMasterAgreement.getAgreementNum() == null) {
//                throw new HlsCusException(NUMBER_EXCEPION);
//            }
//
//            sb.append(bpMaster.getExtraNam());
//            sb.append("-");
//        }

//        sb.append(projectNumber);
//        sb.append(NUMBER_DESC);
        return projectNumber;
    }

    @Override
    public void calcQuotationFront(IRequest iRequest, Long projectId, Long quotationId) throws Exception {
        List<Map> list = new ArrayList<>();

        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setQuotationId(quotationId);
        quotation = prjQuotationMapper.selectByPrimaryKey(quotation);

        //前台计算默认首付款设定金额
        Map downMap = new HashMap();
        downMap.put(ExcelUtils.KEY_FIELD, DOWN_PAYMENT_TMP);
        downMap.put(ExcelUtils.KEY_VALUE, quotation.getDownPayment());
        list.add(downMap);

        //设置默认值保证金
        Map depositMap = new HashMap();
        depositMap.put(ExcelUtils.KEY_FIELD, DEPOSIT_TMP);
        depositMap.put(ExcelUtils.KEY_VALUE, quotation.getDeposit());
        list.add(depositMap);

        //投放日
        if(iRequest.getAttribute("leaseStartDate")!=null){
            Map leaseStartDateMap = new HashMap();
            leaseStartDateMap.put("field", "lease_start_date");
            leaseStartDateMap.put("value", iRequest.getAttribute("leaseStartDate"));
            list.add(leaseStartDateMap);
        }

        //名义价格
        Map residualValueMap = new HashMap();
        residualValueMap.put("field", "residual_value");
        residualValueMap.put("value", quotation.getResidualValue());
        list.add(residualValueMap);
//        //租赁业务类型
//        Map businessTypeMap = new HashMap();
//        businessTypeMap.put("field", "business_type");
//        businessTypeMap.put("value", quotation.getBusinessType());
//        list.add(businessTypeMap);
        //还款频率
        Map annualPayTimesMap = new HashMap();
        annualPayTimesMap.put("field", "annual_pay_times");
        annualPayTimesMap.put("value",  quotation.getAnnualPayTimes());
        list.add(annualPayTimesMap);
//        //租赁类型首付款比例
//        Map downPaymentRatioMap = new HashMap();
//        downPaymentRatioMap.put("field", "down_payment_ratio");
//        downPaymentRatioMap.put("value", quotation.getDownPaymentRatio());
//        list.add(downPaymentRatioMap);
//        //首付款比例
//        Map map69 = new HashMap();
//        map69.put("field", "down_payment_ratio");
//        map69.put("value", doubleDataTran(quotation.getDownPaymentRatio()));
//        list.add(map69);
//        //保证金比例
//        Map map70 = new HashMap();
//        map70.put("field", "deposit_ratio");
//        map70.put("value", doubleDataTran(quotation.getDepositRatio()));
//        list.add(map70);
        //手续费比例
        Map map71 = new HashMap();
        map71.put("field", "lease_charge_ratio");
        map71.put("value", doubleDataTran(quotation.getLeaseChargeRatio()));
        list.add(map71);
//        //保证金
//        Map map68 = new HashMap();
//        map68.put("field", "deposit");
//        map68.put("value", quotation.getDeposit());
//        list.add(map68);

        excelUtils.updateQutationByProject(quotationId, projectId, list);
    }
    public Double doubleDataTran(Object var) {
        double result;
        if (var == null) {
            result = 0D;
        } else {
            result = Double.valueOf(var.toString());
        }
        return result;
    }

    public Double doubleDataTran(Double var) {
        double result;
        if (var == null) {
            result = 0D;
        } else {
            result = var;
        }
        return result;
    }

    public String stringDataTran(Object var) {
        String result;
        if (var == null) {
            result = "";
        } else {
            result = var.toString();
        }
        return result;
    }

    @Override
    public void wordToPdf(IRequest iRequest, List<FndAttachment> list) {

    }

    @Override
    public void sendWordToPdfRequest(Long attachmentId) {

    }

    @Override
    public List<Map> prjRpQueryAuto(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize) {
        return null;
    }

    @Override
    public void updateStatusForProjectReview(IRequest iRequest, Long projectId, String signStatus, Date signDate) {

    }

    @Override
    public boolean projectReviewSynchronization(IRequest iRequest, Long[] projectIds) throws HlsCusException {
        return false;
    }

    @Override
    public boolean projectReviewSyncForProjectFailure(IRequest iRequest, String partnersContractNumber, Long partnersId, String approvalStatus, String returnType, String returnReason) {
        return false;
    }

    @Override
    public void checkFinanceProjectInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception {

    }

    @Override
    public void checkCorpFinanceProjectInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception {

    }

    @Override
    public void checkSyProjectInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception {

    }

    @Override
    public void checkFinanceProjectLeaseItem(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception {

    }

    @Override
    public void checkEquipmentCodingNotifyInfo(IRequest iRequest, SyFinanceInterface syFinanceInterface, SyEquipmentCodingNotifyInfo equipmentCodingNotifyInfo) throws Exception {

    }

    @Override
    public void saveEquipmentCodingNotifyInfo(IRequest iRequest, SyFinanceInterface syFinanceInterface, SyEquipmentCodingNotifyInfo equipmentCodingNotifyInfo) throws Exception {

    }

    @Override
    public void checkPreFinanceBpmasterInfo(IRequest iRequest, List<BpMasterBaseDto> bpMasterBaseDtoList) throws Exception {

    }

    @Override
    public void synPrjProjectAttach(IRequest iRequest, MultipartFile file, FndAttachment fndAttachment) throws Exception {

    }

    @Override
    public List<Map> prjProcessInfoQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize) {
        return null;
    }

    @Override
    public void deleteAttachByProjectNumAndAttachCode(IRequest iRequest, String projectNumber, String attachCode) {

    }

    @Override
    public Long queryPrimaryKeyByPartnersContractNumber(String partnersContractNumber, String tableName, String primaryKey) {
        return null;
    }

    @Override
    public void deleteProjectInfo(Long projectId) {

    }

    @Override
    public FinanceDocumentStatusBody financeQueryDocumentStatus(IRequest iRequest, PrjDocumentStatusDto prjDocumentStatusDto) throws Exception {
        return null;
    }

    @Override
    public void financeSynProjectInfo(IRequest iRequest, HlsCusPrjProject prjProject) throws Exception {

    }

    @Override
    public void financeCorpSynProjectInfo(IRequest iRequest, HlsCusPrjProject prjProject) throws Exception {

    }

    @Override
    public void financeSynQuotationInfo(IRequest iRequest, HlsCusPrjProject prjProject, HlsCusPrjQuotation prjQuotation, int leaseItemNum) throws Exception {

    }

    @Override
    public void financeCorpSynQuotationInfo(IRequest iRequest, HlsCusPrjProject prjProject, HlsCusPrjQuotation prjQuotation, int leaseItemNum) throws Exception {

    }

    @Override
    public void financeSynLeaseItemInfo(IRequest iRequest, HlsCusPrjProject prjProject, HlsCusPrjQuotation prjQuotation, List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList) throws Exception {

    }

    @Override
    public void financeSynLeaseItemTy(IRequest iRequest, HlsCusPrjProject prjProject, List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList) throws Exception {

    }

    @Override
    public void financeCorpSynLeaseItemInfo(IRequest iRequest, HlsCusPrjProject prjProject, HlsCusPrjQuotation prjQuotation, List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList) throws Exception {

    }

    @Override
    public void financeSynBpMasterInfo(IRequest iRequest, HlsCusPrjProject prjProject, List<BpMasterBaseDto> bpMasterBaseDtoList, List<HlsCusPrjProjectBp> projectBpList) throws Exception {

    }

    @Override
    public void financePreSynBpMasterInfo(IRequest iRequest, List<BpMasterBaseDto> bpMasterBaseDtoList) throws Exception {

    }

    @Override
    public FinanceTenantResultBody financeTenantResult(IRequest iRequest, List<FinanceTenantResultBody> financeInterface) throws Exception {
        return null;
    }

    @Override
    public void financeSynBpMasterAddressInfo(HlsBpMaster hlsBpMaster, List<HlsBpMasterAddress> hlsBpMasterAddressList) throws Exception {

    }

    @Override
    public void financeSynBpMasterBankAccountInfo(IRequest iRequest, HlsBpMaster hlsBpMaster, List<HlsBpMasterBankAccount> hlsBpMasterBankAccountList) throws Exception {

    }

    @Override
    public void financeSynBpMainMembersInfo(IRequest iRequest, HlsBpMaster hlsBpMaster, List<HlsBpMasterMainMembers> hlsBpMasterMainMembersList) throws Exception {

    }

    @Override
    public void financeSynPrjBankInfo(IRequest iRequest, HlsCusPrjProject prjProject, Long bpId) throws Exception {

    }

    @Override
    public void excecuteAndSavePrjInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception {

    }

    @Override
    public void savePrjLeaseItem(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception {

    }

    @Override
    public void retryGenerateDocx(IRequest iRequest, Long projectId) {

    }

    @Override
    public List<Map> repaymentBatchCalc(IRequest iRequest, Long cshPaymentReqId) throws HlsCusException {
        return null;
    }

    @Override
    public void projectSubmitValidate(HlsCusPrjProject project) throws HlsCusException {

    }

    @Override
    public void syncRentInfo(IRequest iRequest, RentInfo rentInfo) throws HlsCusException {

    }



    @Override
    public List prjRiskTrailQuery(IRequest iRequest, String[] projectIdList) {
        return null;
    }

    @Override
    public List bpInfoByProjectId(IRequest iRequest, String[] projectIdList) {
        return null;
    }

    @Override
    public List queryOlAutoReject(IRequest iRequest, String requestHeadId) {
        return null;
    }

    @Override
    public boolean returnPayment(IRequest iRequest, Long projectId, String returnReason, String returnDescription) throws HlsCusException {
        return false;
    }

    @Override
    public void syncPaymentReq(IRequest iRequest, PaymentReq paymentReq) throws HlsCusException {

    }

    @Override
    public void updateLeaseItem(IRequest iRequest, Long projectId, PaymentReq paymentReq) throws HlsCusException {

    }

    @Override
    public List<Map> queryReportHistoryList(Map<String, Object> taskInfo, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public List<Map> queryProcessName() {
        return null;
    }

    @Override
    public List<ReportStartUserLov> queryReportStartUserLov(IRequest iRequest, ReportStartUserLov user, int page, int pagesize) {
        return null;
    }

    @Override
    public List<ReportStartUserDepartmentLov> ReportStartUserDepartmentLov(IRequest iRequest, ReportStartUserDepartmentLov dept, int page, int pagesize) {
        return null;
    }

    @Override
    public void sigRetryGenerateDocx(IRequest iRequest, Long projectId) throws HlsCusException {

    }

    @Override
    public void sigBpRetryGenerateDocx(IRequest iRequest, Long projectId, Map params) throws HlsCusException, PKIException {

    }

    @Override
    public void autoScore(IRequest iRequest, Long projectId, String objectType, String reCalFlag) {
        HlsScoreCalculation hlsScoreCalculation = new HlsScoreCalculation();
        hlsScoreCalculation.setObjectType(objectType);
        hlsScoreCalculation.setObjectId(projectId);

        if (hlsScoreCalculation.getScoreTemplateHdId() == null) {
            if (HlsConstantUtil.ScoreObjectType.PRJ_PROJECT.equals(objectType)) {
                // 进件模板暂时写死
                FndScoreTemplateHd fndScoreTemplateHd = new FndScoreTemplateHd();
                fndScoreTemplateHd.setScoreTemplateHdCode("PRJ_TEMPLET");
                List<FndScoreTemplateHd> fndScoreTemplateHdList = iFndScoreTemplateHdService.selectList(fndScoreTemplateHd);
                if (fndScoreTemplateHdList.size() > 0) {
                    fndScoreTemplateHd = (FndScoreTemplateHd) fndScoreTemplateHdList.get(0);
                    hlsScoreCalculation.setScoreTemplateHdId(fndScoreTemplateHd.getScoreTemplateHdId());
                }
            }
        }
        if (iRequest.getCompanyId() != null) {
            hlsScoreCalculation.setCompanyId(iRequest.getCompanyId());
        }
        if (iRequest.getUserId() != null) {
            hlsScoreCalculation.setScoreUserId(iRequest.getUserId());
        }
        hlsScoreCalculation.setScoreDate(new Date());
        hlsScoreCalculationService.hlsScoreCalculation(iRequest, hlsScoreCalculation, reCalFlag);

    }

    @Override
    public HlsCusPrjProject queryProjectByProjectId(Long projectId) {
        return prjProjectMapper.selectPrjById(projectId);
    }

    @Override
    public void downloadBusinessConfirmPdf(IRequest iRequest, HttpServletResponse response, List<HlsCusPrjProject> list) throws Exception {

    }

    @Override
    public List<Map> generateBusinessConfirm(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        return null;
    }

    @Override
    public List<Map> queryUserInfo(IRequest iRequest, Map params) {
        return null;
    }

    @Override
    public String allLeaseItemCheck(String itemNumber, String division, String contractNumber, String classifyId) {
        return null;
    }

    @Override
    public String submitSignTask(IRequest iRequest, HlsCusPrjProject project) {
        return null;
    }

    @Override
    public String getDivision(String contractNumber, Long contractId, Long projectId) {
        String division = null;
        if (StringUtils.isNotEmpty(contractNumber)) {
            division = prjProjectMapper.getDivision(contractNumber);
        }
        if (StringUtils.isEmpty(division) && projectId != null) {
            division = prjProjectMapper.selectByPrimaryKey(projectId).getDivision();
        }
        if (StringUtils.isEmpty(division) && contractId != null) {
            division = conContractMapper.selectByPrimaryKey(contractId).getDivision();
        }
        return division;
    }

    @Override
    public void downloadConfirmPdf(IRequest iRequest, HttpServletResponse response, List<FndAttachment> list) throws Exception {

    }

    @Override
    public List<Long> generateProjectBusinessConfirm(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        return null;
    }

    @Override
    public List<Long> checkDownloadConfirmPdf(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception {
        return null;
    }

    @Override
    public List<Map> reportAuthQuery(IRequest iRequest) {
        return null;
    }

    @Override
    public String getManufacturerCode(String contractNumber, Long contractId, Long projectId) {
        String manufacturerCode = null;
        if (StringUtils.isNotEmpty(contractNumber)) {
            manufacturerCode = prjProjectMapper.getManufacturerCode(contractNumber);
        }
        if (StringUtils.isEmpty(manufacturerCode) && projectId != null) {
            manufacturerCode = prjProjectMapper.selectManufacturerCodeByPrimaryKey(projectId);
        }
        if (StringUtils.isEmpty(manufacturerCode) && contractId != null) {
            manufacturerCode = conContractMapper.selectManufacturerCodeByPrimaryKey(contractId);
        }
        return manufacturerCode;
    }

    @Override
    public String getLeaseChannel(String contractNumber, Long contractId, Long projectId) {
        return null;
    }

    @Override
    public void createOrgTenantSignRequest(IRequest iRequest, HlsCusPrjProject project) {

    }

    @Override
    public void substrErrorMsg(StringBuilder errorMessage, HlsCusPrjProject project, String errorStr) {
        for (int i = 0; i < 100; i++) {
            String errorMessageStr = errorMessage.toString();
            if (getLines(errorMessageStr) < ERROR_LINES) {
                if (getLines(errorMessageStr + errorStr) >= ERROR_LINES) {
                    errorStr = errorStr.substring(0, errorStr.lastIndexOf(BR));
                } else {
                    errorMessage.append(project.getProjectNumber()).append(errorStr).append(BR);
                    return;
                }
            } else if (getLines(errorMessageStr) == ERROR_LINES) {
                errorMessage.append("...").append(BR);
            } else {
                return;
            }
        }
    }
    private int getLines(String errorMessage) {
        if (StringUtils.isNotEmpty(errorMessage)) {
            String afterStr = errorMessage.replaceAll(BR, "");
            return (errorMessage.length() - afterStr.length()) / BR.length();
        } else {
            return 0;
        }
    }


    @Override
    public void createOrgGuarantorSignRequest(IRequest iRequest, HlsCusPrjProject project, HlsCusPrjProjectBp prjProjectBp) {

    }
}
