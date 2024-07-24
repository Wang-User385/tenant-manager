package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.exception.UserException;
import com.hand.hap.account.mapper.RoleMapper;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.account.dto.Role;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.*;
import com.hand.hls.bp.mapper.*;
import com.hand.hls.bp.service.HlsBpMasterAddressService;
import com.hand.hls.bp.service.HlsCusBpMasterContactInfoService;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.fin.exception.HlsCusAmountOverException;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import com.hand.hls.fnd.mapper.HlsFinStatementHdMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionParaMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.fnd.service.IInterfaceErrorMsgService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.lease.dto.YxLeaseItemClassify;
import com.hand.hls.lease.mapper.YxLeaseItemClassifyMapper;
import com.hand.hls.pam.dto.HlsCusLeaseItemList;
import com.hand.hls.pam.mapper.HlsCusLeaseItemListMapper;
import com.hand.hls.pam.mapper.HlsCusLeaseItemMapper;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import com.hand.hls.prj.service.*;
import com.hand.hls.sign.mapper.SignPartyMapper;
import com.hand.hls.sys.service.ISysDocumentListService;
import com.hand.hls.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import leaf.bean.LeafRequestData;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.hand.hls.prj.service.IPrjProjectService.APPROVED;
import static org.springframework.util.StreamUtils.BUFFER_SIZE;

@Controller
public class HlsCusPrjProjectController extends BaseController {

    @Autowired
    private HlsCusPrjProjectService service;
    @Autowired
    private HlsCusHlsCreditLineMapper creditLineMapper;

    @Autowired
    private HlsFinStatementHdMapper hlsFinStatementHdMapper;

    @Autowired
    HlsCusHlsCreditLineMapper hlsCusHlsCreditLineMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;

    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;
    @Autowired
    private HlsBpMasterService hlsBpMasterService;

    @Autowired
    private HlsBpMasterAddressService hlsBpMasterAddressService;

    @Autowired
    private HlsCusBpMasterContactInfoService hlsCusBpMasterContactInfoService;

    @Autowired
    private HlsCusBpShareholderInformationMapper hlsCusBpShareholderInformationMapper;
    @Autowired
    private HlsCusBpMasterRelationMapper hlsBpMasterRelationMapper;
    @Autowired
    private ISysDocumentListService sysDocumentListService;

    @Autowired
    private HlsCusBpSeniorPersionMapper hlsCusBpSeniorPersionMapper;

    @Autowired
    private HlsBpMasterRoleMapper hlsBpMasterRoleMapper;

    @Autowired
    private HlsCusBpMasterRelationMapper hlsCusBpMasterRelationMapper;

    @Autowired
    private HlsCusBpMasterContactInfoMapper hlsCusBpMasterContactInfoMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;

    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;

    @Autowired
    private HlsCusConContractService contractlowService;

    @Autowired
    private PrjProjectApprovalMapper projectApprovalMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceBpService creditLineChanceBpService;
    @Autowired
    private HlsCusLeaseItemMapper hlsCusLeaseItemMapper;
    @Autowired
    private HlsCusLeaseItemListMapper hlsCusLeaseItemListMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;
    @Autowired
    HlsCreditPlanMapper hlsCreditPlanMapper;
    @Autowired
    private IPrjProjectService prjProjectService;
    @Autowired
    private IConContractService conContractService;
    @Autowired
    private IInterfaceErrorMsgService interfaceErrorMsgService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private IProcessCancelDetailService cancelDetailService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    private HlsBpMasterInceptRuleMapper hlsBpMasterInceptRuleMapper;
    @Autowired
    private YxLeaseItemClassifyMapper yxLeaseItemClassifyMapper;
    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;
    @Autowired
    private HlsProductDefinitionParaMapper hlsProductDefinitionParaMapper;
    @Qualifier("npTenantSignServiceImpl")
    @Autowired
    private BpSignService npTenantSignServiceImpl;

    @Qualifier("orgTenantSignServiceImpl")
    @Autowired
    private BpSignService orgTenantSignServiceImpl;

    @Autowired
    private SignPartyMapper signPartyMapper;

    @Autowired
    private ISignPartyService signPartyService;

    private static final String PARAMETER = "parameter";
    private static final String AUTHORITY_RULE_FLAG = "authorityRuleFlag";
    private static final String ADMIN = "ADMIN";
    private static final String PROJECT_ID = "project_id";
    private static final String CONTRACT_ID = "contract_id";
    private static final String MANUFACTURER_ID = "manufacturer_id";
    private static final String N = "N";
    private static final String PROJECT_ID_LIST = "projectIdList";

    /*@RequestMapping(value = "/prj/contract/change/save")
    @ResponseBody
    public ResponseData savePrjContractChange(HttpServletRequest request, @RequestBody HlsCusPrjProject dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.savePrjContractChange(requestContext, dto));
    }*/

    @RequestMapping(value = "/prj/project/bp/count")
    @ResponseBody
    public ResponseData queryProjectBpCount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        //查询指项目商业伙伴信
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProjectBp prjProjectBp = param.toJavaObject(HlsCusPrjProjectBp.class);
        return new ResponseData(service.queryPrijectBpCount(requestCtx, prjProjectBp));
    }

    @RequestMapping(value = "/prj/project/bp/select")
    @ResponseBody
    public ResponseData queryProjectAndBpNameByProjectId(HttpServletRequest request, @RequestBody HlsCusPrjProject dto) {
        //查询指定的项目信息
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryPrijectAndBpNameByProjectId(requestContext, dto));
    }

    @RequestMapping(value = "/prj/contract/change/back")
    @ResponseBody
    public ResponseData backPrjContractChange(HttpServletRequest request, @RequestBody HlsCusPrjProject dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.backPrjContractChange(requestContext, dto));
    }

    @RequestMapping(value = "/prj/contract/change/submit")
    @ResponseBody
    public ResponseData submitPrjContractChange(HttpServletRequest request, @RequestBody HlsCusPrjProject dto) {
        IRequest requestContext = createRequestContext(request);
        service.submitPrjContractChange(requestContext, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/con/contract/change/query")
    @ResponseBody
    public ResponseData queryPrjContractChange(@RequestParam(defaultValue = DEFAULT_PAGE) int page, HttpServletRequest request,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryPrjContractChange(page, pageSize));
    }

    @RequestMapping(value = "/ct/prj/project/contractCreate/query")
    @ResponseBody
    public ResponseData isCreateContract(@RequestBody HlsCusPrjProject dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.isCreateContract(dto));
    }


    @RequestMapping(value = "/ct/prj/project/query")
    @ResponseBody
    public ResponseData query(HlsCusPrjProject dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    //项目审批风险报告附件查询
    @RequestMapping(value = "/prj/project/risk/report/query")
    @ResponseBody
    public ResponseData queryRiskReportAttachment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        return new ResponseData(service.queryProjectRiskReportAttachment(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/ct/prj/project/detail/query")
    @ResponseBody
    public ResponseData queryPrjDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //查询项目明细
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryPrjDetail(requestContext, dto));
    }

    @RequestMapping(value = "/ct/prj/project/detail/query/second")
    @ResponseBody
    public ResponseData queryPrjDetailSecond(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //查询项目明细（项目用，与合同分开）
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryPrjDetailSecond(requestContext, dto));
    }

    @RequestMapping(value = "/ct/prj/project/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //保存项目信息
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInfo dto = param.toJavaObject(HlsCusPrjProjectInfo.class);
        //dto.setHlsCusPrjQuotation(param.toJavaObject(HlsCusPrjQuotation.class));
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = service.prjProjectSave(requestCtx, dto);
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(hlsCusPrjProject);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/submit/supple")
    @ResponseBody
    public ResponseData updateSupple(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //保存项目信息
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInfo dto = param.toJavaObject(HlsCusPrjProjectInfo.class);
        //dto.setHlsCusPrjQuotation(param.toJavaObject(HlsCusPrjQuotation.class));
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = service.prjProjectSaveSupple(requestCtx, dto);
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(hlsCusPrjProject);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/wfl/update")
    @ResponseBody
    public ResponseData wflUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //保存项目信息
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        //dto.setHlsCusPrjQuotation(param.toJavaObject(HlsCusPrjQuotation.class));
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = service.prjProjectSaveWfl(requestCtx, dto);
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(hlsCusPrjProject);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/wfl/update/attach")
    @ResponseBody
    public ResponseData wflUpdateAttach(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //保存项目信息
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectAttachment dto = param.toJavaObject(HlsCusPrjProjectAttachment.class);
        //dto.setHlsCusPrjQuotation(param.toJavaObject(HlsCusPrjQuotation.class));
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = service.prjProjectSaveWflAttach(requestCtx, dto);
        List<HlsCusPrjProjectAttachment> list = new ArrayList<>();
        list.add(hlsCusPrjProjectAttachment);
        return new ResponseData(list);
    }



    /*@RequestMapping(value = "/ct/prj/project/change/submit")
    @ResponseBody
    public HlsCusPrjProject changeUpdate(@RequestBody HlsCusPrjProjectInfo dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject = service.prjProjectChangeSave(requestCtx, dto);
        return hlsCusPrjProject;
    }*/

    @RequestMapping(value = "/ct/prj/project/submit/create/con")
    @ResponseBody
    public ResponseData prjCreateVirtualCon(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //创建虚拟合同
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(service.prjCreateVirtualCon(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/submit/create/con_frame")
    @ResponseBody
    public ResponseData prjCreateVirtualConFrame(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //创建虚拟合同
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(service.prjCreateVirtualConFrame(requestCtx, dto));
        return new ResponseData(list);
    }
    @RequestMapping(value = "/ct/prj/project/submit/wfl")
    @ResponseBody
    public ResponseData prjSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        //启动工作流
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        Long assist_unit_id = 0L;
        if(hlsCusPrjProject.getAssistUnitId() != null){
            assist_unit_id = hlsCusPrjProject.getAssistUnitId();
        }
        hlsCusPrjProject = service.selectByPrimaryKey(requestCtx,hlsCusPrjProject);

        if(assist_unit_id != 0L){
            hlsCusPrjProject.setAssistUnitId(assist_unit_id);
        }


        ResponseData rd = new ResponseData(true);
        //提交时针对项目表tenantid 进行更新
        Long projectId= hlsCusPrjProject.getProjectId();

        List<HlsCusPrjProjectBp> bp = new ArrayList<>();
        bp = hlsCusPrjProjectBpMapper.selectByProjectId(projectId);
        //更新
        if(bp.size() == 0){
            throw new IllegalArgumentException("请维护一条承租人");
        }else{
            hlsCusPrjProjectMapper.updatePrjTenant(bp.get(0).getBpId(),projectId);
        }
        if(bp.size() >1){
            if ("MANUFACTURER".equals(bp.get(0).getBpType())){
                throw new IllegalArgumentException("有且只能维护一个合作机构");
            } else {
                throw new IllegalArgumentException("有且只能维护一条承租人");
            }
        }
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setProjectId(projectId);
        List<HlsCusPrjProjectBp> reportBps
                = hlsCusPrjProjectBpMapper.select(hlsCusPrjProjectBp);

        //复议时间校验
        Integer returnTimes = hlsCusPrjProject.getReturnTimes();
        Date returnDate = hlsCusPrjProject.getReturnDate();
//        if(true){
////            throw new RuntimeException("aaaaa");
//            return new ResponseData(false,"xzxx");
//        }
        if(returnTimes != null){
            if(returnTimes == 1){
                if(compareDays(returnDate, new Date()) > 30){
//                    throw new IllegalArgumentException("初次复议只可在30天内发起审批");
                    return new ResponseData(false,"初次复议只可在30天内发起审批");
                }
            }else{
                if(compareDays(returnDate, new Date()) < 180){
//                    throw new IllegalArgumentException("非初次复议180天内不可发起审批");
                    return new ResponseData(false,"非初次复议180天内不可发起审批");
                }
            }
        }

        //基本信息
        String title = "基本信息";
        for (HlsCusPrjProjectBp reportBp : reportBps) {
            HlsCusBpMaster   hlsCusBpMaster=new HlsCusBpMaster();
            hlsCusBpMaster.setBpId(reportBp.getBpId());
            List<HlsCusBpMaster> hlsCusBpMasters
                    = hlsCusBpMasterMapper.queryCusBpMasterDetails1(hlsCusBpMaster);
            /*if(hlsCusBpMasters.size() > 0){
                if (hlsCusBpMasters.get(0).getBpClass().equalsIgnoreCase("NP") && !hlsCusBpMasters.get(0).getBpType().equalsIgnoreCase("VENDER")) {
                    String infoStr = hlsCusBpMasters.get(0).getBpName();
                    validatePropertyNew(hlsCusBpMasters.get(0).getIdType(), "证件类型", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getBpType(), "客户类型", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getIdCardNo(), "证件号码", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getMaritalStatus(), "婚姻状况", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getGender(), "性别", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getAge(), "年龄", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getNationality(), "国籍", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getPhone(), "手机号", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getWorkingCompany(), "工作单位", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getBusinessJob(), "职务", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getHighestDegree(), "最高学历", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getPersonalCredit(), "个人征信", title, infoStr);

                    validatePropertyNew(hlsCusBpMasters.get(0).getPersonalResume(), "个人简历", title, infoStr);

                }else if (hlsCusBpMasters.get(0).getBpClass().equalsIgnoreCase("ORG") && !hlsCusBpMasters.get(0).getBpType().equalsIgnoreCase("VENDER")) {
                    String infoStr = hlsCusBpMasters.get(0).getBpName();
                    validatePropertyNew(hlsCusBpMasters.get(0).getBpName(), "客户名称", title, infoStr);


                    validatePropertyNew(hlsCusBpMasters.get(0).getBpType(), "客户类型", title, infoStr);

                    validatePropertyNew(hlsCusBpMasters.get(0).getRegisterCapitalCur(), "注册资本币种", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getRegisteredCapital(), "注册资本(万元)", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getRegisteredAddress(), "注册地址", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getFoundedDate(), "注册日期", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getActualCurrency(), "实收资本币种", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getPaidUpCapital(), "实收资本(万元)", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getManagementEndDate(), "经营结束时间", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getRegistrationNumType(), "登记注册号类型", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getRegisterCertNum(), "登记注册号码", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getOperatingPeriodEnd(), "统一信用代码到期日", title, infoStr);

                    validatePropertyNew(hlsCusBpMasters.get(0).getOrganizationType(), "组织机构类别", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getEconomicInduClassify(), "行业分类", title, infoStr);

                    validatePropertyNew(hlsCusBpMasters.get(0).getRefV01(), "是否有中征码", title, infoStr);
                    if (hlsCusBpMasters.get(0).getRefV01().equalsIgnoreCase("Y")){
                        validatePropertyNew(hlsCusBpMasters.get(0).getLoanCardNum(), "中征码", title, infoStr);

                    }

                    validatePropertyNew(hlsCusBpMasters.get(0).getGroupCustomers(), "是否集团内客户", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getEnterpriseScale(), "*企业规模划分", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getBusinessArea(), "主要经营活动所在国家/地区", title, infoStr);

                    validatePropertyNew(hlsCusBpMasters.get(0).getListedCompanyFlag(), "是否上市公司", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getEnterpriseAffiliation(), "企业隶属关系", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getContactInformationEnterprise(), "企业联系方式", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getTelephoneFinance(), "财务部电话", title, infoStr);
                    validatePropertyNew(hlsCusBpMasters.get(0).getBusinessScope(), "经营范围", title, infoStr);


                }
            }*/

            if("TENANT".equalsIgnoreCase(reportBp.getBpType())){
                HlsCusHlsCreditLineChanceBp chanceBp=new HlsCusHlsCreditLineChanceBp();
                chanceBp.setChanceId(reportBp.getChanceId());
                List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps = hlsCusHlsCreditLineChanceBpMapper.selectByForeignKey(chanceBp);
                for (HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp : hlsCusHlsCreditLineChanceBps) {
                    if(hlsCusHlsCreditLineChanceBp.getBpId().equals(reportBp.getBpId())){
                        chanceBp=hlsCusHlsCreditLineChanceBp;
                    }
                }
                double creditAmountUsed=chanceBp.getCreditAmountUsed()==null?0:chanceBp.getCreditAmountUsed();
                chanceBp.setCreditAmountUsed(creditAmountUsed+hlsCusPrjProject.getLeaseItemAmount());
                creditLineChanceBpService.updateUsedAmountByBpId(chanceBp);
            }
        }


        hlsCusPrjProject = service.prjSubmitWfl(requestCtx, hlsCusPrjProject);
        hlsCusPrjProject.setSuccess(true);
        return rd;
    }


    //字段必输校验
    public Boolean validate(HlsCusBpMaster hlsCusBpMaster, ResponseData data,IRequest iRequest) {
        Boolean result = true;
        try {
            String infoStr = "承租人";
            //基本信息
            String title = "基本信息";
            validateProperty(hlsCusBpMaster.getBpName(), "客户名称", title, infoStr);
            validateProperty(hlsCusBpMaster.getRegisteredCapital(), "注册资本（万元）", title, infoStr);
            validateProperty(hlsCusBpMaster.getPaidUpCapital(), "实收资本(万元)", title, infoStr);
            validateProperty(hlsCusBpMaster.getRegisterCapitalCur(), "注册资本币种", title, infoStr);
            validateProperty(hlsCusBpMaster.getFoundedDate(), "注册日期", title, infoStr);
            validateProperty(hlsCusBpMaster.getRegistrationNumType(), "登记注册号类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getRegisterCertNum(), "登记注册证号码", title, infoStr);
            validateProperty(hlsCusBpMaster.getOrganizationType(), "组织机构类别", title, infoStr);
            //validateProperty(hlsCusBpMaster.getLegalPerson(), "法定代表人/经营者名称", title, infoStr);
            validateProperty(hlsCusBpMaster.getRefV01(), "是否有中征码", title, infoStr);
            if("Y".equals(hlsCusBpMaster.getRefV01())){
                validateProperty(hlsCusBpMaster.getLoanCardNum(), "中征码", title, infoStr);
            }
            validateProperty(hlsCusBpMaster.getEconomicInduClassify(), "行业分类", title, infoStr);
            validateProperty(hlsCusBpMaster.getExclusiveTrade(), "是否为专属行业", title, infoStr);
            validateProperty(hlsCusBpMaster.getGroupCustomers(), "是否集团客户", title, infoStr);
            if("Y".equals(hlsCusBpMaster.getGroupCustomers())){
                validateProperty(hlsCusBpMaster.getGroupMembership(), "所属集团", title, infoStr);
            }
            validateProperty(hlsCusBpMaster.getEnterpriseScale(), "企业规模划分", title, infoStr);
            validateProperty(hlsCusBpMaster.getHeadOfficeBpType(), "总行客户类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getBusinessArea(), "主要经营活动所在国家/地区", title, infoStr);
            validateProperty(hlsCusBpMaster.getEnterpriseAffiliation(), "企业隶属关系", title, infoStr);
            validateProperty(hlsCusBpMaster.getBpFinancialType(), "经济类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getBusinessScope(), "经营范围", title, infoStr);

            //基本信息-法定代表人
            title = "基本信息-法定代表人";
            validateProperty(hlsCusBpMaster.getLegalPerson(), "法定代表人", title, infoStr);
            validateProperty(hlsCusBpMaster.getIdType(), "证件类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getIdCardNo(), "证件号码", title, infoStr);
            validateProperty(hlsCusBpMaster.getIdExpirationDate(), "证件有效期到", title, infoStr);

            //基本信息-实际控制人
            title = "基本信息-实际控制人";
            validateProperty(hlsCusBpMaster.getActualPerson(), "实际控制人", title, infoStr);
            validateProperty(hlsCusBpMaster.getActualClass(), "实际控制人类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getActualType(), "证件类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getActualNumber(), "证件号码", title, infoStr);
            validateProperty(hlsCusBpMaster.getActualExpirationDate(), "证件有效期到", title, infoStr);


            //兴业银行
            title = "兴业银行";
            validateProperty(hlsCusBpMaster.getInsiderTradingB(), "是否内部交易", title, infoStr);
            if("Y".equals(hlsCusBpMaster.getInsiderTradingB())){
                validateProperty(hlsCusBpMaster.getRelatedTransactionB(), "关联方类型", title, infoStr);
            }
            validateProperty(hlsCusBpMaster.getRelatedTransactionIsB(), "是否关联交易", title, infoStr);
            if("Y".equals(hlsCusBpMaster.getRelatedTransactionIsB())){
                validateProperty(hlsCusBpMaster.getRelatedTransactionBd(), "关联方类型", title, infoStr);
            }

            //管理信息
            title = "管理信息";
            validateProperty(hlsCusBpMaster.getStrategicBpType(), "总行战略客户类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getListedCompany(), "是否上市公司", title, infoStr);
            if("Y".equals(hlsCusBpMaster.getListedCompany())){
                validateProperty(hlsCusBpMaster.getListingCode(), "上市代码", title, infoStr);
            }


            //地址信息
            title = "地址信息";
            HlsCusBpMasterAddress hlsCusBpMasterAddress=new HlsCusBpMasterAddress();
            hlsCusBpMasterAddress.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusBpMasterAddress> hlsCusBpMasterAddressList=  hlsBpMasterAddressService.select(iRequest,hlsCusBpMasterAddress,1,1000);

            hlsCusBpMasterAddress.setBpId(hlsCusBpMaster.getBpId());
            hlsCusBpMasterAddress.setAddressType("REGISTERED_ADDRESS");
            List<HlsCusBpMasterAddress> hlsCusBpMasterAddressListNew=  hlsBpMasterAddressService.select(iRequest,hlsCusBpMasterAddress,1,1000);
            if(hlsCusBpMasterAddressListNew.size()==0){
                data.setMessage(infoStr+title+"地址类型为住所地（注册地址）必须维护一条！");
                data.setSuccess(false);
                result = false;
            }

            for(HlsCusBpMasterAddress item:hlsCusBpMasterAddressList){
                validateProperty(item.getCountry(), "国家",title,infoStr);
            }


            //地址信息
            title = "联系人信息";
            HlsCusBpMasterContactInfo hlsCusBpMasterContactInfo= new HlsCusBpMasterContactInfo();
            hlsCusBpMasterContactInfo.setBpId(hlsCusBpMaster.getBpId());
            hlsCusBpMasterContactInfo.setPersonType("CONTACT");
            List<HlsCusBpMasterContactInfo> hlsCusBpMasterContactInfoList=   hlsCusBpMasterContactInfoService.select(iRequest,hlsCusBpMasterContactInfo,1,1000);
            for(HlsCusBpMasterContactInfo item:hlsCusBpMasterContactInfoList){
                validateProperty(item.getContactPerson(), "联系人姓名",title,infoStr);
                validateProperty(item.getCellPhone(), "手机号码",title,infoStr);
            }

            //授权办理业务人信息
            title = "授权办理业务人信息";
            HlsCusBpMasterContactInfo hlsCusBpMasterContactInfoAuthorize= new HlsCusBpMasterContactInfo();
            hlsCusBpMasterContactInfoAuthorize.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusBpMasterContactInfo> hlsCusBpMasterContactInfoListAuthorize =   hlsCusBpMasterContactInfoMapper.queryAllAuthorize(hlsCusBpMasterContactInfoAuthorize);
            if(hlsCusBpMasterContactInfoListAuthorize.size() == 0){
                throw new RuntimeException("授权办理业务人信息！");
            }
            for(HlsCusBpMasterContactInfo item:hlsCusBpMasterContactInfoListAuthorize){
                validateProperty(item.getContactPerson(), "授权办理人员姓名",title,infoStr);
                validateProperty(item.getIdType(), "证件类型",title,infoStr);
                validateProperty(item.getIdCardNo(), "证件号码",title,infoStr);
                validateProperty(item.getIdExpirationDate(), "证件有效期到",title,infoStr);
            }

            //受益人信息
            title = "受益人信息";
            HlsCusBpMasterContactInfo hlsCusBpMasterContactInfoBenifit= new HlsCusBpMasterContactInfo();
            hlsCusBpMasterContactInfoBenifit.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusBpMasterContactInfo> hlsCusBpMasterContactInfoListBenifit=   hlsCusBpMasterContactInfoMapper.queryAllBenifit(hlsCusBpMasterContactInfoBenifit);
            if(hlsCusBpMasterContactInfoListBenifit.size() == 0){
                throw new RuntimeException("受益人信息至少维护一条！");
            }
            for(HlsCusBpMasterContactInfo item:hlsCusBpMasterContactInfoListBenifit){
                validateProperty(item.getContactPerson(), "受益人姓名",title,infoStr);
                validateProperty(item.getIdType(), "证件类型",title,infoStr);
                validateProperty(item.getIdCardNo(), "证件号码",title,infoStr);
                validateProperty(item.getIdExpirationDate(), "证件有效期到",title,infoStr);
                validateProperty(item.getContactAddress(), "地址",title,infoStr);
            }

            //股东信息
            title = "股东信息";
            HlsCusBpShareholderInformation hlsCusBpShareholderInformation= new HlsCusBpShareholderInformation();
            hlsCusBpShareholderInformation.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusBpShareholderInformation> hlsCusBpShareholderInformationList=  hlsCusBpShareholderInformationMapper.select(hlsCusBpShareholderInformation);
            if(hlsCusBpShareholderInformationList.size() == 0){
                throw new RuntimeException("股东信息至少维护一条！");
            }
            for(HlsCusBpShareholderInformation item:hlsCusBpShareholderInformationList){
                validateProperty(item.getShareholderType(), "股东类型",title,infoStr);
                validateProperty(item.getShareholderName(), "股东名称",title,infoStr);
                validateProperty(item.getShareholderIdType(), "证件类型/登记注册号类型",title,infoStr);
                validateProperty(item.getShareholderIdCardNo(), "证件号码/登记注册号",title,infoStr);
                validateProperty(item.getHoldingRatio(), "出资比例%",title,infoStr);
            }

            //高管及主要人员
            title = "高管及主要人员";
            HlsCusBpSeniorPersion hlsCusBpSeniorPersion= new HlsCusBpSeniorPersion();
            hlsCusBpSeniorPersion.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusBpSeniorPersion> hlsCusBpSeniorPersionList=  hlsCusBpSeniorPersionMapper.select(hlsCusBpSeniorPersion);
            for(HlsCusBpSeniorPersion item:hlsCusBpSeniorPersionList){
                validateProperty(item.getDocumentPersonType(), "关系人类型",title,infoStr);
                validateProperty(item.getPersonName(), "姓名",title,infoStr);
            }


            //开票信息
             title = "开票信息";
            validateProperty(hlsCusBpMaster.getTaxpayerType(), "纳税人类型", title, infoStr);
            validateProperty(hlsCusBpMaster.getInvoiceBpAddressPhoneNum(), "地址与电话", title, infoStr);
            validateProperty(hlsCusBpMaster.getInvoiceTitle(), "发票抬头", title, infoStr);
            validateProperty(hlsCusBpMaster.getInvoiceBpBankAccount(), "开户行与账号", title, infoStr);
            validateProperty(hlsCusBpMaster.getSocialCreditCode(), "统一社会信用代码", title, infoStr);


            //角色信息
            title = "角色信息";
            HlsBpMasterRole hlsBpMasterRole= new HlsBpMasterRole();
            hlsBpMasterRole.setBpId(hlsCusBpMaster.getBpId());
            List<HlsBpMasterRole> hlsBpMasterRoleList=  hlsBpMasterRoleMapper.select(hlsBpMasterRole);
            for(HlsBpMasterRole item:hlsBpMasterRoleList){
                validateProperty(item.getBpType(), "客户类型",title,infoStr);
            }


            //集团信息
            /*title = "集团信息";
            HlsCusBpMasterRelation hlsCusBpMasterRelation= new HlsCusBpMasterRelation();
            hlsCusBpMasterRelation.setBpId(hlsCusBpMaster.getBpId());
            List<HlsCusBpMasterRelation> hlsCusBpMasterRelationList=  hlsCusBpMasterRelationMapper.select(hlsCusBpMasterRelation);
            for(HlsCusBpMasterRelation item:hlsCusBpMasterRelationList){
                validateProperty(item.getBpRelationType(), "客户关系",title,infoStr);
                validateProperty(item.getRelationBpId(), "相关客户编号",title,infoStr);
            }*/



        }
        catch (Exception e) {
            data.setMessage(e.getMessage());
            data.setSuccess(false);
            result = false;

        }
        return result;
    }

    public void validateProperty(Object o, String msg,String  title,String infoStr) throws RuntimeException {
        if (o == null || "".equals(String.valueOf(o))) {
            msg=infoStr+"-"+title+"-"+msg+"必填！";
            throw new RuntimeException(msg);
        }
    }

    public void validatePropertyNew(Object o, String msg,String  title,String infoStr) throws HlsCusException {
        if (o == null || "".equals(String.valueOf(o))) {
            msg=infoStr+"-"+title+"-"+msg+"必填！";
            throw new HlsCusException(msg);
        }
    }

    @RequestMapping(value = "/ct/prj/project/submit/supple/wfl")
    @ResponseBody
    public ResponseData prjSubmitSuppleWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        //启动工作流
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        Long assist_unit_id = 0L;
        if(hlsCusPrjProject.getAssistUnitId() != null){
            assist_unit_id = hlsCusPrjProject.getAssistUnitId();
        }
        Long prjSuppleId = 0L;
        if(hlsCusPrjProject.getPrjSuppleId() != null){
            prjSuppleId = hlsCusPrjProject.getPrjSuppleId();
        }
        hlsCusPrjProject = service.selectByPrimaryKey(requestCtx,hlsCusPrjProject );

        if(assist_unit_id != 0L){
            hlsCusPrjProject.setAssistUnitId(assist_unit_id);
        }

        hlsCusPrjProject = service.prjSubmitSuppleWfl(requestCtx, hlsCusPrjProject , prjSuppleId);
        hlsCusPrjProject.setSuccess(true);
        return new ResponseData(true);
    }



    @RequestMapping(value = "/ct/prj/project/cancel/submit/wfl")
    @ResponseBody
    public HlsCusPrjProject cancelSubmit(@RequestBody HlsCusPrjProject dto, HttpServletRequest request) {
        //合同撤销工作流
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject = service.cancelSubmit(requestCtx, dto);
        return hlsCusPrjProject;
    }

    @RequestMapping(value = "/ct/prj/project/change/submit/wfl")
    @ResponseBody
    public ResponseData prjChangeSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);

        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(service.prjChangeSubmitWfl(requestCtx, hlsCusPrjProject));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusPrjProject> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/ct/prj/project/prjHomePageGetAllStatusProjectCount")
    @ResponseBody
    public ResponseData prjHomePageGetAllStatusProjectCount(HlsCusPrjProject dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        //项目立项首页环状图
        IRequest requestContext = createRequestContext(request);
        Long companyId = requestContext.getCompanyId();
        dto.setCompanyId(companyId);
        return new ResponseData(service.prjHomePageGetAllStatusProjectCount(requestContext, dto));
    }

    @RequestMapping(value = "/ct/prj/project/prjHomePageGetAllStatusConCount")
    @ResponseBody
    public ResponseData prjHomePageGetAllStatusConCount(HlsCusPrjProject dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        //合同首页环状图
        IRequest requestContext = createRequestContext(request);
        Long companyId = requestContext.getCompanyId();
        dto.setCompanyId(companyId);
        return new ResponseData(service.prjHomePageGetAllStatusConCount(requestContext, dto));
    }

    @RequestMapping(value = "/ct/prj/project/prjHomePageProjectInfoGrid")
    @ResponseBody
    public ResponseData prjHomePageProjectInfoGrid(HlsCusPrjProject dto, HttpSession session, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if(requestContext.getAttributeMap().get("_request_data") != null){
            JSONObject requestData =  JSONObject.parseObject(requestContext.getAttributeMap().get("_request_data").toString());
            JSONObject param = (JSONObject)requestData.get("parameter");
            dto = JSONObject.toJavaObject(param, HlsCusPrjProject.class);
        }
        Long companyId = requestContext.getCompanyId();
      //  dto.setCompanyId(companyId);
        //dto.setUserId((Long) session.getAttribute("userId"));

        //dto.setProjectStatus("APPROVED");
        return new ResponseData(service.prjHomePageProjectInfoGrid(requestContext, dto, pagenum, pageSize));
    }
    @RequestMapping(value = "/ct/prj/project/queryPrjHomePageProjectInfoGridNew")
    @ResponseBody
    public ResponseData queryPrjHomePageProjectInfoGridNew(HlsCusPrjProject dto, HttpSession session, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if(requestContext.getAttributeMap().get("_request_data") != null){
            JSONObject requestData =  JSONObject.parseObject(requestContext.getAttributeMap().get("_request_data").toString());
            JSONObject param = (JSONObject)requestData.get("parameter");
            dto = JSONObject.toJavaObject(param, HlsCusPrjProject.class);
        }
        Long companyId = requestContext.getCompanyId();
        // dto.setCompanyId(companyId);
        // dto.setUserId((Long) session.getAttribute("userId"));

        //dto.setProjectStatus("APPROVED");
        return new ResponseData(service.queryPrjHomePageProjectInfoGridNew(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping(value = "/ct/prj/project/prjHomePageProjectInfoGridSecond")
    @ResponseBody
    public ResponseData prjHomePageProjectInfoGridSecond(HlsCusPrjProject dto, HttpSession session, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if(requestContext.getAttributeMap().get("_request_data") != null){
            JSONObject requestData =  JSONObject.parseObject(requestContext.getAttributeMap().get("_request_data").toString());
            JSONObject param = (JSONObject)requestData.get("parameter");
            dto = JSONObject.toJavaObject(param, HlsCusPrjProject.class);
        }
        Long companyId = requestContext.getCompanyId();
        dto.setCompanyId(companyId);
        dto.setUserId((Long) session.getAttribute("userId"));

        //dto.setProjectStatus("APPROVED");
        return new ResponseData(service.prjHomePageProjectInfoGridSecond(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping(value = "/ct/bp/project/notice")
    @ResponseBody
    public ResponseData queryBpNotice(HlsCusPrjProject dto, HttpSession session, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryBpNotice(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/con/contract/prjHomePageProjectInfoGrid")
    @ResponseBody
    public ResponseData conHomePageContractInfoGrid(HlsCusPrjProject dto, HttpSession session, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        Long companyId = requestContext.getCompanyId();
        dto.setCompanyId(companyId);
        dto.setUserId((Long) session.getAttribute("userId"));
        return new ResponseData(service.conHomePageContractInfoGrid(requestContext, dto, page, pageSize));
    }

    /*变更申请创建*/
    @RequestMapping(value = "/hls/cus/prj/project/change/req/submit")
    @ResponseBody
    public ResponseData changeCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");

        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);

        dto = service.changeCreate(requestCtx, dto);

        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(dto);
        return new ResponseData(hlsCusPrjProjectList);
    }

    /*额度增量申请*/
    @RequestMapping(value = "/credit/project/amount/allocate/create")
    @ResponseBody
    public ResponseData projectAmountAllocateCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        Long project_id= Long.parseLong(param.get("project_id").toString());
        HlsCusPrjProject dto = new HlsCusPrjProject();
        dto.setProjectId(project_id);
        dto = hlsCusPrjProjectMapper.selectByPrimaryKey(dto);
        dto = service.projectAmountAllocateCreate(requestCtx, dto);

        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(dto);
        return new ResponseData(hlsCusPrjProjectList);
    }

    //根据changereqid查询历史
    @RequestMapping(value = "/prj/project/change/history/query")
    @ResponseBody
    public ResponseData historyQuery(@RequestBody HlsCusPrjProject dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.historyPrjQuery(requestCtx, dto));
    }

    /*尽调变更取消*/
    @RequestMapping(value = "/ct/prj/change/cancel")
    @ResponseBody
    public ResponseData changeCancel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);

        return new ResponseData(service.changeCancel(requestCtx, dto));
    }

    /**
     * 租赁excel模版下载
     */
    @RequestMapping(value = "/ct/prj/excel/file/download")
    public void uploadBpData(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileType) throws FileReadIOException {
        try {
            String fileName = request.getSession().getServletContext().getRealPath("/") + "resources/excel/PRJ/";
            String addHeader = "attachment;filename=\"";
            if ("ANOMALY".equals(fileType)) {
                fileName += "不规则报价导入_财通.xlsx";
                String name = "不规则报价导入_财通.xlsx";
                boolean isMSIE = AttachmentHttpUtils.isMSBrowser(request);
                if (isMSIE) {
                    name = URLEncoder.encode(name, "UTF-8");
                } else {
                    name = new String(name.getBytes("UTF-8"), "ISO-8859-1");
                }
                addHeader += name;
            } else {
                throw new IllegalArgumentException("未知错误");
            }
            addHeader += "\"";
            File file = new File(fileName);
            if (file.exists()) {
                response.addHeader("Content-Disposition", addHeader);
                response.setContentType("EXCEL;charset=UTF-8");
                response.setHeader("Accept-Ranges", "bytes");
                int fileLength = (int) file.length();
                response.setContentLength(fileLength);
                if (fileLength > 0) {
                    writeFileToResp(response, file);
                }
            } else {
                response.getWriter().write("文件不存在！");
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        }
    }

    private void writeFileToResp(HttpServletResponse response, File file) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        try (InputStream inStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int readLength;
            while (((readLength = inStream.read(buf)) != -1)) {
                outputStream.write(buf, 0, readLength);
            }
            outputStream.flush();
        }
    }


    /*财通投放情况报表*/
    @RequestMapping(value = "/ct/rpt/con/project/situation/query")
    @ResponseBody
    public ResponseData conSituationQuery(HlsCusPrjProject dto, HttpServletRequest request, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.conSituationQuery(requestContext, dto, page, pageSize));
    }

    /**
     * 合同签约右侧栏状态
     *
     * @param request
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/hls/cus/prj/contract/sign/status")
    @ResponseBody
    public ResponseData contractSignStatus(HttpServletRequest request, Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        List<String> list = new ArrayList<>();
        //签约
//        list.add(service.contractSignStatus(requestCtx, projectId));

        //租金支付表制作 - 审批通过
        list.add(service.rentpaymentStatus(requestCtx , projectId));
        //付款前提条件 - 审批通过
        list.add(service.conditionStatus(requestCtx , projectId));
        //头寸报备
        list.add(service.reportFinanceStatus(requestCtx , projectId));
        //付款申请
        list.add(service.paymentStatus(requestCtx , projectId));
        //起租
        list.add(service.leaseStatus(requestCtx , projectId));

        //合同关闭 -常亮
        list.add(service.cancelStatus(requestCtx , projectId));
        //起租
//        list.add(service.paymentReqConfirmStatus(requestCtx, projectId));
        //罚息减免
//        list.add(service.interestDerateStatus(requestCtx, projectId));
        //合同撤销
//        list.add(service.fctContractEtStatus(requestCtx, projectId));
        //合同结束
//        list.add(service.fctContractEndStatus(requestCtx, projectId));

        return new ResponseData(list);
    }




    @RequestMapping(value = "/hls/cus/prj/contract/payment/status")
    @ResponseBody
    public String paymentTableMakeStatus(HttpServletRequest request, @RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.paymentStatus(requestCtx, projectId);
    }


    /**
     * 合同起贷右侧栏状态控制
     *
     * @param request
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/hls/cus/prj/contract/payment/confirm/status")
    @ResponseBody
    public String paymentReqStatus(HttpServletRequest request, @RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.paymentReqConfirmStatus(requestCtx, projectId);
    }

    @RequestMapping(value = "/hls/cus/prj/contract/interest/derate/status")
    @ResponseBody
    public String paymentTableConfirmStatus(HttpServletRequest request, @RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.interestDerateStatus(requestCtx, projectId);
    }

    @RequestMapping(value = "/hls/cus/prj/contract/et/status")
    @ResponseBody
    public String fctContractEtStatus(HttpServletRequest request, @RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.fctContractEtStatus(requestCtx, projectId);
    }

    @RequestMapping(value = "/hls/cus/prj/contract/end/status")
    @ResponseBody
    public String fctContractEndStatus(HttpServletRequest request, @RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.fctContractEndStatus(requestCtx, projectId);
    }

    @RequestMapping(value = "/prj/project/cancel/info/cancel")
    @ResponseBody
    public ResponseData cancelProjectInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        service.cancelProjectInfo(requestCtx, dto);
        return new ResponseData();
    }


    /**
     * 租赁合同签约合同【工作流开始】
     */
    @RequestMapping(value = "/hls/cus/prj/contract/sign/wfl")
    @ResponseBody
    public ResponseData fctContractSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestCtx = createRequestContext(request);
        service.prjContractSignSubmitWfl(requestCtx, dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/hls/cus/prj/project/cancel")
    @ResponseBody
    public ResponseData cancelFctProject(HlsCusPrjProject hlsCusPrjProject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        hlsCusPrjProject.setProjectStatus("CANCEL");
        service.updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
        return responseData;
    }


    @RequestMapping(value = "/hls/prj/credit/line/term/ecc", method = RequestMethod.GET)
    @ResponseBody
    public String queryHlsCreditLinetermByBusinessKey(final HttpServletRequest request, HlsCusPrjProject var1) {
        String flag = "false";
        IRequest iRequest = createRequestContext(request);
        HlsCusPrjProject dto = new HlsCusPrjProject();
        dto.setProjectId(var1.getProjectId());
        dto = service.selectByPrimaryKey(iRequest, dto);

        HlsCusHlsCreditLine hlsCusHlsCreditLine = new HlsCusHlsCreditLine();
        hlsCusHlsCreditLine.setCreditLineId(dto.getCreditLineId());
        hlsCusHlsCreditLine = hlsCusHlsCreditLineMapper.selectByPrimaryKey(hlsCusHlsCreditLine);
        if (hlsCusHlsCreditLine.getTerm() != null) {
            flag = "true";
        }
        // hlsCusHlsCreditLine= hlsCusFctProjectService.queryHlsCreditLineAmountByCreditId(iRequest,creditLineId);
        return flag;
    }

    /*@RequestMapping(value = "/hls/cus/prj/project/get/rate")
    @ResponseBody
    public Double projectGetRate(HttpServletRequest request, @RequestParam String invoiceProfile, @RequestParam String businessType) {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        double varRate = service.getRate(iRequest, invoiceProfile, businessType);
        return varRate;
    }*/

    @RequestMapping(value = "/contract/incept/lease/check")
    @ResponseBody
    public ResponseData contractCheckInceptFinance(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);

        List<String> responseList = new ArrayList<>();
        responseList.add(service.contractInceptCheck(requestContext,list));
        return new ResponseData(responseList);
    }

    /**
     * 财务起租核算日标记  - 财务起租
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/contract/incept/lease/finance")
    @ResponseBody
    public ResponseData contractInceptFinance(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);

        list = service.contractInceptFinance(requestContext,list);

        return new ResponseData(list);
    }

    /**
     * 财务起租核算日标记  - 保存
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/contract/incept/save/finance")
    @ResponseBody
    public ResponseData contractSaveIncept(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);

        list = service.contractSaveIncept(requestContext,list);

        return new ResponseData(list);
    }


    @RequestMapping(value = "/contract/incept/delaylease/finance")
    @ResponseBody
    public ResponseData contractInceptDelayFinance(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);

        list = service.contractInceptDelayFinance(requestContext,list);

        return new ResponseData(list);
    }

    @RequestMapping(value = "/contract/incept/delaycheck/finance")
    @ResponseBody
    public ResponseData contractInceptcheckFinance(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);

        List<String> responseList = new ArrayList<>();
        responseList.add(service.contractInceptCheckFinance(requestContext,list));
        return new ResponseData(responseList);

    }




    //投票完成
    @RequestMapping(value = "/prj/project/meeting/update/status")
    @ResponseBody
    public ResponseData updateApprovalStatus(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);

        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> hlsCusPrjProject = param.toJavaList(HlsCusPrjProject.class);
        service.updateApprovalStatus(requestCtx, hlsCusPrjProject);
        return new ResponseData();
    }

    //审批意见
    @RequestMapping(value = "/hls/prj/approve/notice")
    @ResponseBody
    public ResponseData queryApproveNotice(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryApproveNotice(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    //否决审批意见
    @RequestMapping(value = "/hls/prj/veto/notice")
    @ResponseBody
    public ResponseData queryVetoNotice(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        String approveResult = request.getParameter("approveResult");
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject.setApproveResult(approveResult);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryVetoNoticeDefault(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    //审批意见
    @RequestMapping(value = "/hls/prj/veto/noticeresult")
    @ResponseBody
    public ResponseData queryVetoNoticeResult(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryVetoNoticeResult(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    //申请人
    @RequestMapping(value = "/hls/prj/approve/noticebp")
    @ResponseBody
    public ResponseData queryApproveNoticeBp(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryApproveNoticeBp(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private HlsCusQuatationElementsMapper hlsCusQuatationElementsMapper;

    @Autowired
    private HistoryService historyService;

    private static final String RELEASE_DAY = "RELEASE_DAY";
    private static final String LEASE_DAY = "LEASE_DAY";
    private static final String NAME_DAY = "NAME_DAY";

    private static final String FLOATING = "FLOATING";
    private static final String FIXED = "FIXED";



    //报价方案
    @RequestMapping(value = "/hls/prj/approve/noticequatation")
    @ResponseBody
    public ResponseData queryApproveNoticeQuatation(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotation hlsCusPrjQuotation = param.toJavaObject(HlsCusPrjQuotation.class);
        List<HlsCusPrjQuotation>  hlsCusPrjQuotationList =  hlsCusPrjQuotationMapper.queryConQuotationNoticeDetail(hlsCusPrjQuotation);
        List<HlsCusPrjQuotationElements> prjQuatationElementsList  =  new ArrayList<>();

        HlsCusPrjQuotationElements quatationElements = new HlsCusPrjQuotationElements();
        quatationElements.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationElements> cusPrjQuotationElements =  hlsCusQuatationElementsMapper.queryByQuatationId(quatationElements);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        DecimalFormat df = new DecimalFormat("0.00%");
        DecimalFormat df2 = new DecimalFormat("###,###,###,###,###,##0.00");
        DecimalFormat df3 = new DecimalFormat("###,###,###,###,###,##0");
        HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();

        if(cusPrjQuotationElements.size() == 0){
            if(!hlsCusPrjQuotationList.isEmpty()){
                for(HlsCusPrjQuotation dt:hlsCusPrjQuotationList){
                    String usedCashFlow = null;
                    String riskMitigationMeasures = null;
                    if(dt.getSourceDocumentId() != null){
                        cusPrjProject.setProjectId(dt.getSourceDocumentId());
                        cusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(cusPrjProject);
                        usedCashFlow =  cusPrjProject.getUsedCashFlow();
                        riskMitigationMeasures = cusPrjProject.getRiskMitigationMeasures();
                    }

                    if(dt.getBusinessTypeN() != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("业务种类");
                        prjQuatationElements.setElementsDescribe( dt.getBusinessTypeN());
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getSubjectMatterIntrodution() != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("租赁标的物");
                        prjQuatationElements.setElementsDescribe(dt.getSubjectMatterIntrodution());
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getCurrencyN() != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("币种");
                        prjQuatationElements.setElementsDescribe(dt.getCurrencyN()+"(元)");
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getFinanceAmount() != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("融资金额");
                        prjQuatationElements.setElementsDescribe(  df2.format(new BigDecimal(dt.getFinanceAmount())) + "(元)" );
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getLeaseTerm() != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("融资期限");
                        prjQuatationElements.setElementsDescribe(dt.getLeaseTerm().intValue() +"个月");
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getLeaseChargeRatio() != null){
                        //String leaseChargeRatio = new DecimalFormat("0.00").format(dt.getLeaseChargeRatio());
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("手续费");
                        prjQuatationElements.setElementsDescribe("融资金额的" + df.format(dt.getLeaseChargeRatio()) );
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getDepositRatio() != null){
                        //String depostRatio = new DecimalFormat("0.00").format(dt.getDepositRatio());
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("保证金/风险金");
                        prjQuatationElements.setElementsDescribe("融资金额的" + df.format(dt.getDepositRatio()) );
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getRentalPlan()  != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("还款方式");
                        prjQuatationElements.setElementsDescribe(dt.getRentalPlan());
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getIntRate() != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("租赁利率");

                        Date lprLinkDate = null;
                        if(RELEASE_DAY.equalsIgnoreCase(dt.getLprRefDay() )){
                            lprLinkDate = dt.getFirstReleaseDate();
                        }else if(LEASE_DAY.equalsIgnoreCase(dt.getLprRefDay())){
                            lprLinkDate = dt.getFirstReleaseDate();
                        }else if(NAME_DAY.equalsIgnoreCase(dt.getLprRefDay())){
                            lprLinkDate = dt.getLprNameDate();
                        }
                        String linkDate = null;
                        if(lprLinkDate != null){
                            Calendar calender = Calendar.getInstance();
                            Date date = lprLinkDate;
                            calender.setTime(date);

                            String month = String.valueOf(calender.get(Calendar.MONTH)+1);
                            linkDate = calender.get(Calendar.YEAR) + "年" + month + "月" + calender.get(Calendar.DATE)  + "日";
                        }

                        String adjustTerm = null;
                        if(FLOATING.equalsIgnoreCase(dt.getIntRateType())){
                            adjustTerm = "调息周期为" + dt.getLprAdjustmentTerm() + "月";
                        }else if(FIXED.equalsIgnoreCase(dt.getIntRateType())){
                            adjustTerm = dt.getIntRateTypeN();
                        }

                        String floatingRate = null;
                        if(dt.getFloatingWayRate() >= 0){
                            floatingRate = "+" + df3.format(dt.getFloatingWayRate() );
                        }else{
                            floatingRate = df3.format(dt.getFloatingWayRate()) ;
                        }

                        prjQuatationElements.setElementsDescribe( "挂钩" + linkDate + "公布的" + dt.getBaseRateTypeN() + floatingRate + "BP," + adjustTerm );
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(usedCashFlow != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("资金用途");
                        prjQuatationElements.setElementsDescribe(usedCashFlow);
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(dt.getResidualValue() != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("留购价");
                        prjQuatationElements.setElementsDescribe(df2.format(new BigDecimal(dt.getResidualValue())) + "(元)");
                        prjQuatationElementsList.add(prjQuatationElements);
                    }
                    if(riskMitigationMeasures != null){
                        HlsCusPrjQuotationElements prjQuatationElements = new HlsCusPrjQuotationElements();
                        prjQuatationElements.setQuotationId(dt.getQuotationId());
                        prjQuatationElements.setProjectId(dt.getSourceDocumentId());
                        prjQuatationElements.setProjectElements("风险缓释措施");
                        prjQuatationElements.setElementsDescribe(riskMitigationMeasures);
                        prjQuatationElementsList.add(prjQuatationElements);
                    }

                    for(HlsCusPrjQuotationElements prjQuatationElement : prjQuatationElementsList){
                        hlsCusQuatationElementsMapper.insertSelective(prjQuatationElement);
                    }
                }
            }

        }

        HlsCusPrjQuotationElements prjQuotationElements = new HlsCusPrjQuotationElements();
        prjQuotationElements.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationElements> hlsCusPrjQuotationElements= hlsCusQuatationElementsMapper.queryElementInfo(prjQuotationElements);

        return  new ResponseData(hlsCusPrjQuotationElements);

    }

    //授信期限 待确定
    @RequestMapping(value = "/hls/prj/approve/notice/leaseterm")
    @ResponseBody
    public ResponseData queryApproveNoticeLeaseterm(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        if(hlsCusPrjProject.getPrjNoticeId() != null){
            hlsCusPrjProjectList = hlsCusPrjProjectMapper.queryApproveCreditPeriodHistory(hlsCusPrjProject);
        }else {
            hlsCusPrjProjectList = hlsCusPrjProjectMapper.queryApproveCreditPeriod(hlsCusPrjProject);
        }

        return  new ResponseData(hlsCusPrjProjectList);
    }

    //授信期限 待确定
    @RequestMapping(value = "/hls/prj/approve/notice/leasetermQuery")
    @ResponseBody
    public ResponseData queryApproveNoticeLeasetermQuery(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
//        String taskdef = "sid-MIsoxvsm-m65O-4CJK-8fn0-iJ4czlsn8Rc4";
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
//        hlsCusPrjProject.setProcessInstanceId(742505L);
//        hlsCusPrjProject.setTaskDefKey(taskdef);
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList = hlsCusPrjProjectMapper.queryApproveCreditPeriodHistory(hlsCusPrjProject);


        return  new ResponseData(hlsCusPrjProjectList);
    }

    //付款前提条件
    @RequestMapping(value = "/hls/prj/approve/paymentcondition")
    @ResponseBody
    public ResponseData queryApprovePaymentCondition(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryApprovePaymentCondition(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    //租后管理要求
    @RequestMapping(value = "/hls/prj/approve/managerequire")
    @ResponseBody
    public ResponseData queryApproveManageRequire(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryApproveManageRequire(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    //审批通知书制作
    @RequestMapping(value = "/hls/prj/create/notice")
    @ResponseBody
    public ResponseData createApproveNotice(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
//        JSONObject param = (JSONObject) requestData.get("parameter");
//        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);

        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);
//        list = service.contractInceptDelayFinance(requestContext,list);
        list= service.createApproveNotice(iRequest , list);
//        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryApproveManageRequire(hlsCusPrjProject);

        return  new ResponseData(list);
    }


    //五级分类
    @RequestMapping(value = "/hls/prj/approve/riskclass")
    @ResponseBody
    public ResponseData queryApproveRiskClass(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryApproveRiskClass(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/prj/project/update/vote/comment")
    @ResponseBody
    public ResponseData updateVoteComment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> hlsCusPrjProject = param.toJavaList(HlsCusPrjProject.class);

        hlsCusPrjProject.forEach(item->{
            service.updateByPrimaryKeySelective(requestCtx, item);

        });
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/update/powerful/person")
    @ResponseBody
    public ResponseData updatePowerfulPerson(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        String projectId = (String)parameter.get("projectId");
        String powerfulPerson = (String)parameter.get("powerfulPerson");
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(Long.valueOf(projectId));
        hlsCusPrjProject = service.selectByPrimaryKey(requestCtx,hlsCusPrjProject);
        hlsCusPrjProject.setPowerfulPerson(powerfulPerson);
        service.updateByPrimaryKeySelective(requestCtx,hlsCusPrjProject);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/update/sum/comment")
    @ResponseBody
    public ResponseData updateSumComment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        String projectId = (String) parameter.get("projectId");
        String type = (String) parameter.get("type");
        if ("project_id".equals(type)) {
            return new ResponseData();
        }
        String value = (String) parameter.get("value");
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(Long.valueOf(projectId));
        hlsCusPrjProject = service.selectByPrimaryKey(requestCtx, hlsCusPrjProject);
        if ("vote_status".equals(type)) {
            hlsCusPrjProject.setVoteStatus(value);
            if("VETO".equals(value) || "FURTHER".equals(value)){
                hlsCusPrjProject.setFiveCategories("");
            }
            hlsCusPrjProject = service.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
        } else if ("vote_comment".equals(type)) {
            hlsCusPrjProject.setVoteComment(value);
            hlsCusPrjProject = service.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
        } else if ("vote_result".equals(type)) {
            hlsCusPrjProject.setVoteResult(value);
            hlsCusPrjProject = service.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
        } else if ("five_categories".equals(type)) {
            hlsCusPrjProject.setFiveCategories(value);
            hlsCusPrjProject = service.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
        } else if ("con_end_task".equals(type)) {
            hlsCusPrjProject.setConEndTask(value);
            hlsCusPrjProject = service.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
        }
        return new ResponseData();
    }


    //否决意见
    @RequestMapping(value = "/hls/prj/approve/noticeveto")
    @ResponseBody
    public ResponseData queryNoticeVeto(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        Long projectId = Long.valueOf(request.getParameter("projectId"));
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<HlsCusPrjProject> hlsCusPrjProjectList  = hlsCusPrjProjectMapper.queryVetoNotice(hlsCusPrjProject);
        return  new ResponseData(hlsCusPrjProjectList);
    }

    @RequestMapping(value = "/hls/cus/prj/project/close")
    @ResponseBody
    public ResponseData projectClose(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> hlsCusPrjProjectList = parameter.toJavaList(HlsCusPrjProject.class);
        List<HlsCusPrjProject> list  = new ArrayList<>();
        for(HlsCusPrjProject dt:hlsCusPrjProjectList){
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(dt.getProjectId());
            hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
            hlsCusPrjProject.setProjectStatus("CLOSED");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
            list.add(hlsCusPrjProject);
        }

        return  new ResponseData(list);
    }
    @RequestMapping(value = "/ct/con/contract/checkItems")
    @ResponseBody
    public ResponseData checkItems(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        service.checkItems(requestContext,hlsCusPrjProject);
        return new ResponseData();
    }


    @RequestMapping(value = "/prj/project/project/meeting/withdraw")
    @ResponseBody
    public ResponseData projectMeetingWithdraw(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        service.projectMeetingWithdraw(requestCtx, hlsCusPrjProject);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/saveEndTask")
    @ResponseBody
    public ResponseData saveEndTask(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject =param.toJavaObject(HlsCusPrjProject.class);

        service.updateByPrimaryKeySelective(requestContext, hlsCusPrjProject);
        List<HlsCusPrjProject> hlsCusPrjProjects = new ArrayList<HlsCusPrjProject>();
        hlsCusPrjProjects.add(hlsCusPrjProject);
        return new ResponseData(hlsCusPrjProjects);
    }
    @RequestMapping(value = "/prj/project/queryEndTask")
    @ResponseBody
    public ResponseData queryEndTask(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        //查询指定的项目信息
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject =param.toJavaObject(HlsCusPrjProject.class);
        return new ResponseData(hlsCusPrjProjectMapper.queryPrjDetail(hlsCusPrjProject));
    }

    @RequestMapping(value = "/prj/base/change/info")
    @ResponseBody
    public ResponseData changeQuotationInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject project = param.toJavaObject(HlsCusPrjProject.class);

        return new ResponseData(service.selectPrjBaseChangeInfo(requestContext, project, page, pageSize));
    }

    private static final String Y = "Y";


    //尽调合同文本生成
    @RequestMapping(value = "/prj/project/create/content")
    @ResponseBody
    public ResponseData hlsMarketingCreateContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request)throws Exception {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long cashflowId = null;
        if (param.size() == 4) {
            cashflowId = param.getLong("cashflow_id");
            param.remove("cashflow_id");
        }
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        //获取项目方案id
        Long projectId = dto.getProjectId() ;
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject.setTemplateCode(dto.getTemplateCode());
        //设置合同文本可打印节点 更新表中文本打印标记
        hlsCusPrjProject.setDocxFlag(Y);
        hlsCusPrjProject.setCreateContractStatus("CREATED");//CREATE_CONTRACT_STATUS
        hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);

        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportCreateDocx(iRequest, hlsCusPrjProject, cashflowId);
        }catch (Exception e){
            logger.error(e.getMessage());
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }
        dto.setSourcePkValue1(list.get(0).getSourcePkValue());
        dto.setAttachmentId(list.get(0).getAttachmentId());
        List<HlsCusPrjProject> hlsDto = new ArrayList<HlsCusPrjProject>();
        hlsDto.add(dto);
        return new ResponseData(hlsDto);
    }


    /*
     * 进件生成合同文本
     * */
    @RequestMapping("/prj/project/create/docx")
    public ResponseData createProjectDocx(HttpServletRequest request,Long projectId, String templateType) throws Exception {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        logger.info("手动生成合同文本,进件Id：{},模板规则引擎{}",projectId,templateType);
        Map map = hlsCusPrjProjectMapper.prjProjectQuotationQuery(prjProject);
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = hlsCusPrjQuotationCashflowMapper.queryPrjQuotationCashflowDetailByProjectId1(projectId);
        prjProject.setDocxFlag(Y);
        prjProject.setCreateContractStatus("CREATED");
        //update 2023-02-22 改为通过模板规则引擎获取规则树配置的合同模板
        //prjProject.setTemplateCode("COLLECTION_BOOKS");
        prjProject.setTemplateCode(templateType);
        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportCreateDocx(iRequest, prjProject, hlsCusPrjQuotationCashflows.get(0).getQuotationCashflowId());
        }catch (Exception e){
            logger.error(e.getMessage());
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }
        //20230308 二期功能：生成合同文本后，就直接修改合同文本状态
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(projectId);
        if (hlsCusPrjProject != null){
            if (APPROVED.equals(hlsCusPrjProject.getProjectStatus())
                    && ("UNCREATED".equals(hlsCusPrjProject.getContractTextStatus())
                    || hlsCusPrjProject.getContractTextStatus() == null)
                    ){

                hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(projectId);
                hlsCusPrjProject.setContractTextStatus("CREATED");
                service.updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
            }
        }
        responseData.setSuccess(true);
        return responseData;
    }

    /*
     * 进件签约合同文本
     * */
    @RequestMapping("/prj/project/sign/docx")
    public ResponseData signProjectDocx(HttpServletRequest request,Long projectId) throws Exception {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        Map prjProject = new HashMap();
        prjProject.put("projectId",projectId);
        List<Map> prjProjectList = hlsCusPrjProjectMapper.prjRpModifyEntranceQuery1(prjProject);
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectPrjById(projectId);
//        hlsCusPrjProject.setManufacturerId(Long.valueOf(String.valueOf(prjProjectList.get(0).get("manufacturer_id"))));
//        hlsCusPrjProject.setFactoryId(Long.valueOf(String.valueOf(prjProjectList.get(0).get("factory_id"))));
//        hlsCusPrjProject.setBpIdVender(Long.valueOf(String.valueOf(prjProjectList.get(0).get("bp_id_vender"))));
        hlsCusPrjProject.setSignType("SIGN_OFFLINE");
        hlsCusPrjProject.setOrgSignState("SIGNED");
        hlsCusPrjProject.setSignedFlag("Y");
        hlsCusPrjProjectMapper.updateStatus(hlsCusPrjProject);
        responseData.setSuccess(true);
        return responseData;
    }

    /*
     * 进件签约合同文本(在线)
     * */
    @RequestMapping("/prj/project/sign/online")
    public ResponseData signProjectOnline(HttpServletRequest request,Long projectId,String projectAttachmentCategory,String manufacturerCode) throws Exception {
        IRequest iRequest = createRequestContext(request);
        signPartyService.submitSignScene(iRequest,projectId,projectAttachmentCategory,manufacturerCode);
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(true);
        return responseData;
    }


    @RequestMapping(value = "/virprj/project/create/content")
    @ResponseBody
    public ResponseData virProjectCreateContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request)throws Exception {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        //获取项目方案id
        Long projectId = dto.getProjectId() ;
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject.setTemplateCode(dto.getTemplateCode());
        //设置合同文本可打印节点 更新表中文本打印标记
        hlsCusPrjProject.setDocxFlag(Y);
        hlsCusPrjProject.setCreateContractStatus("CREATED");//CREATE_CONTRACT_STATUS
        hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);

        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportVirCreateDocx(iRequest, hlsCusPrjProject);
        }catch (Exception e){
            logger.error(e.getMessage());
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }

        return new ResponseData(list);
    }
    @RequestMapping(value = "/virprj/project/create/content1")
    @ResponseBody
    public ResponseData reportVirCreateDocxContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request)throws Exception {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        //获取项目方案id
        Long projectId = dto.getProjectId() ;

        hlsCusPrjProject.setProjectId(projectId);
        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportVirCreateDocxContent(iRequest, hlsCusPrjProject);
        }catch (Exception e){
            logger.error(e.getMessage());
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }

        return new ResponseData(list);
    }

    /**
     * 更改客户信息  承租人
     */
    @RequestMapping(value = "/prj/project/update/tenant")
    @ResponseBody
    public ResponseData submitApproval(HttpServletRequest request, @RequestBody HlsCusPrjProject dto) throws ParseException {
        try {
            IRequest requestCtx = createRequestContext(request);
            HlsCusPrjProject hlsCusPrjProject = service.updateByPrimaryKey(requestCtx, dto);

            return new ResponseData((List<?>) hlsCusPrjProject);
        } catch (IllegalArgumentException e) {
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }
    }
    @RequestMapping({"/prj/virtualPrj/queryVirtualContract"})
    @ResponseBody
    public ResponseData queryForFinanceContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        return new ResponseData(service.queryVirtualContract(hlsCusPrjProject));
    }

    @RequestMapping({"/prj/virtualPrj/queryLeftAmount"})
    @ResponseBody
    public ResponseData queryLeftAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        return new ResponseData(service.queryLeftAmount(hlsCusPrjProject));
    }


    /**
     * 租赁合同结束审批流程
     * @param requestData
     * @param request
     * @return
     */
    @RequestMapping(value = "/submit/contract/terminate/wfl")
    @ResponseBody
    public ResponseData submitContractTerminateWfl(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);

        List<HlsCusPrjProject> listR = service.submitContractTerminateWfl(requestContext,hlsCusPrjProject);

        return new ResponseData(listR);
    }

    @RequestMapping(value = "/prj/base/attachment/generate")
    @ResponseBody
    public ResponseData generatePrjBaseAttachment(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        List<HlsCusPrjProjectAttachment> listR = service.generatePrjBaseAttachment(requestContext,hlsCusPrjProject);

        return new ResponseData(listR);
    }

    @RequestMapping("/prj/jdAttachment/delete")
    @ResponseBody
    public ResponseData deleteJdAttachment(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        RequestHelper.setCurrentRequest(createRequestContext(request));
        Map parameter = requestData.getParameter();
        Long attachmentId = 0L;
        Object attachmentIdObj = parameter.get("project_attachment_id");
        if (attachmentIdObj != null) {
            attachmentId = Long.valueOf(attachmentIdObj.toString());
        }

        return new ResponseData(hlsCusPrjProjectAttachmentService.deleteAttachment(attachmentId));
    }


    @RequestMapping(value = "/prj/wfl/saveWflProject")
    @ResponseBody
    public ResponseData saveWflProject(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result,
                                       HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        //JSONArray param = (JSONArray) requestData.get("parameter");
        //List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);
        //HlsCusPrjProject hlsCusPrjProject = list.get(0);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);

        hlsCusPrjProjectMapper.updateWflProject(hlsCusPrjProject);

        PrjProjectApproval projectApproval=new PrjProjectApproval();
        if((param.get("approval_id") != null && param.get("approval_result")!= null)) {
            projectApproval.setApprovalId(Long.parseLong(param.get("approval_id").toString()));
            projectApproval.setApprovalResult(param.get("approval_result").toString());
            projectApprovalMapper.updateApprovalResult(projectApproval);
        }

        return new ResponseData();
    }

    //尽调审议生成
    @RequestMapping(value = "/prj/project/create/auditContent")
    @ResponseBody
    public ResponseData auditContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<PrjProjectApproval> prjProjectApproval = param.toJavaList(PrjProjectApproval.class);
        List<FndAttachmentMulti> list = service.downloadAuditContent(prjProjectApproval, requestContext, request, response);
        return new ResponseData(list);
    }

    //提前结清说明书生成
    @RequestMapping(value = "/prj/project/create/etBooks")
    @ResponseBody
    public ResponseData etBooksContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> project = param.toJavaList(HlsCusPrjProject.class);
        List<FndAttachmentMulti> list = service.downloadEtBooksContent(project, requestContext, request, response);
        return new ResponseData(list);
    }

    //虚拟合同签约 只有大单调用
    @RequestMapping(value = "/prj/project/sign")
    @ResponseBody
    public ResponseData projectSign(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");

        //irr校验
        HlsCusPrjProject prjProjectBase = param.toJavaObject(HlsCusPrjProject.class);
        prjProjectBase = service.selectByPrimaryKey(requestContext, prjProjectBase);
        HlsCusPrjQuotation quotationCheck = new HlsCusPrjQuotation();
        quotationCheck.setSourceDocumentId(prjProjectBase.getProjectId());
        quotationCheck.setSourceDocumentCategory("PRJ_PROJECT");
        quotationCheck.setDataClass("VIRTUAL_CON");
        List<HlsCusPrjQuotation> quotationCheckList = hlsCusPrjQuotationMapper.select(quotationCheck);

        HlsCusPrjProject prjProjectJD = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProjectBase.getRefProjectId());
        //判断授信与非授信项目,从不同的地方取出 IRR不低于 字段
        /*if("Y".equals(prjProjectJD.getCreditFlag())){
            //授信项目向上找2级才是尽调项目
            prjProjectJD.setProjectId(prjProjectJD.getRefProjectId());
            prjProjectJD= hlsCusPrjProjectMapper.selectByPrimaryKey(prjProjectJD);
            HlsCreditPlan creditPlanJd =  new HlsCreditPlan();
            creditPlanJd.setSourceDocumentId(prjProjectJD.getProjectId());
            creditPlanJd.setSourceDocumentCategory("PRJ_PROJECT");
            creditPlanJd = hlsCreditPlanMapper.select(creditPlanJd).get(0);
            for(HlsCusPrjQuotation item : quotationCheckList){
                if(item.getIrr() < creditPlanJd.getIrr()){
                    throw new UserException("合同报价后的IRR不能低于上会的IRR("+creditPlanJd.getIrr()+"),请检查！", null);
                }
            }
        }else{*/
        HlsCusPrjQuotation quotationJd = new HlsCusPrjQuotation();
        quotationJd.setSourceDocumentId(prjProjectJD.getProjectId());
        quotationJd.setSourceDocumentCategory("PRJ_PROJECT");
        quotationCheck.setDataClass("PRJ_PROJECT_INVEST");
        quotationJd = hlsCusPrjQuotationMapper.select(quotationJd).get(0);

        //只有大单的会进行虚拟合同签约
        for (HlsCusPrjQuotation item : quotationCheckList) {
            if (item.getXirr() < quotationJd.getXirr()) {
                throw new UserException("合同报价后的XIRR不能低于上会的XIRR(" + MathUtil.mul(quotationJd.getXirr(), 100d) + "%),请检查！", null);
            }
        }
        //}
        //end

        //当前合同最后一期支付日要小于第零期投放日+租赁月数
        HlsCusPrjQuotation quotationVirtualCon = quotationCheckList.get(0);
        HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
        quotationCashflow.setQuotationId(quotationVirtualCon.getQuotationId());
        List<HlsCusPrjQuotationCashflow> quotationCashflowList = hlsCusPrjQuotationCashflowMapper.select(quotationCashflow);
        if (quotationCashflowList != null || quotationCashflowList.size() != 0) {
            quotationCashflow = quotationCashflowList.stream().filter(item -> 1 == item.getCfItem())
                    .sorted(Comparator.comparing(HlsCusPrjQuotationCashflow::getTimes).reversed()).collect(Collectors.toList()).get(0);
            int leaseTermMonth = new Double(quotationVirtualCon.getLeaseTerm() * 12).intValue();
            Date leaseStartDate = quotationVirtualCon.getLeaseStartDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(leaseStartDate);
            calendar.add(Calendar.MONTH, leaseTermMonth);
            Date endDate = calendar.getTime();
            if (quotationCashflow.getDueDate().compareTo(endDate) >= 0) {
                throw new UserException("当前合同报价最后一期支付日需小于合同投放日+租赁月数，请修改最后一期支付日后进行合同签约", null);
            }
        }

        String legalContractNumber="";
        HlsCusPrjProjectAttachment projectAttachment = new HlsCusPrjProjectAttachment();
        projectAttachment.setProjectAttachmentCategory("CON_CONTRACT_ATT");
        projectAttachment.setProjectId(param.toJavaObject(HlsCusPrjProject.class).getProjectId());
        List<HlsCusPrjProjectAttachment> attList = hlsCusPrjProjectAttachmentMapper.select(projectAttachment);
        if(attList != null) {
            projectAttachment = attList.stream().filter(item -> "融资租赁合同".equals(item.getDocumentName())).findFirst().get();
            legalContractNumber = projectAttachment == null? "" : projectAttachment.getContractNumber();
        }

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(param.toJavaObject(HlsCusPrjProject.class).getProjectId());
        prjProject.setContractStatus("SIGN");
        prjProject.setContractNum(legalContractNumber);
        service.updateByPrimaryKeySelective(requestContext, prjProject);

        //签约生成报价(投放计划)
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        //传参同君成前端调用
        hlsCusPrjQuotation.setSourceDocumentId(prjProject.getProjectId());
        List<HlsCusPrjQuotation> list = new ArrayList<>();
        list.add(hlsCusPrjQuotation);
        list = contractlowService.createContractPlanByContract(requestContext,list);
        HlsCusPrjQuotation quotationReturn = list.get(0);
        //调用投放计划确认逻辑生成合同 _request_data: {"parameter":[{"quotation_id":2233,"source_document_id":16,"project_id":"16"}]}
        hlsCusPrjQuotation = new HlsCusPrjQuotation();
        //控制操作顺序,放款->支付->支付表确认,报价id在这一步CONTRACT_PLAN类型的报价, 在支付表确认审批通过后合同关联的报价是CONTRACT_CONFIR类型,此时去做付款支付会报错
        hlsCusPrjQuotation.setQuotationId(quotationReturn.getQuotationId());
        hlsCusPrjQuotation.setSourceDocumentId(quotationReturn.getSourceDocumentId());
        hlsCusPrjQuotation.setProjectId(quotationReturn.getSourceDocumentId());
        list.set(0, hlsCusPrjQuotation);
        contractlowService.confirmContractPlan(requestContext,list);

        return new ResponseData( true);
    }

    @RequestMapping(value = "/prj/project/queryProjectByProjectId")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(param.toJavaObject(HlsCusPrjProject.class).getProjectId());
        List<HlsCusPrjProject> hlsCusPrjProjects = service.selectSelective(requestContext, prjProject);

        HlsCusPrjProject refProject=new HlsCusPrjProject();
        refProject.setProjectId(hlsCusPrjProjects.get(0).getRefProjectId());
        refProject=service.selectSelective(requestContext, refProject).get(0);
        //额度占用创建的项目 不上会的话 审议信息和上报方案显示空
        if("CREDIT_NORMAL".equals(refProject.getDataType()) && "N".equals(refProject.getMeetingFlag())){
            hlsCusPrjProjects=new ArrayList<>();
            hlsCusPrjProjects.add(refProject);
        }

        return new ResponseData(hlsCusPrjProjects);
    }

    //附件上传校验
    @RequestMapping(value = "/prj/project/checkWflAtt")
    @ResponseBody
    public ResponseData checkWflAtt(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProjectAttachment> prjProjectAttachment = param.toJavaList(HlsCusPrjProjectAttachment.class);
        List<HlsCusPrjProjectAttachment> list = hlsCusPrjProjectAttachmentMapper.checkWflAtt(prjProjectAttachment.get(0));
        return new ResponseData(list);
    }

    @RequestMapping({"/prj/attachment/download/zip"})
    @ResponseBody
    public void downloadAttachmentZip(HttpServletRequest request, @RequestParam("attachment_id") String attachmentIds, HttpServletResponse response) throws Exception {
        IRequest requestContext = this.createRequestContext(request);
        String[] attachmentIdList = attachmentIds.split(",");
        int length = 0;
        String fileName = "";
        String zipFileName = "函件套打.zip";
        String zipFilePath = zipFileName;
        FileOutputStream outputStream = new FileOutputStream(zipFilePath);
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream));
        if (attachmentIdList != null) {
            for (int i = 0; i < attachmentIdList.length; i++) {
                FndAttachment fndAttachment = new FndAttachment();
                fndAttachment.setAttachmentId(Long.valueOf(attachmentIdList[i]));
                FndAttachment attachment = (FndAttachment) this.fndAttachmentService.selectByPrimaryKey(requestContext, fndAttachment);
                length += attachment.getFileSize();
                fileName = attachment.getFileName();
                String filePath = attachment.getFilePath();
                InputStream in = this.getAttachmentInputStream(filePath);

                zipOut.putNextEntry(new ZipEntry(fileName));
                int j = 0;
                byte[] buffer = new byte[1024 * 1024*2];
                while ((j = in.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, j);
                }
                // 关闭输入流
                in.close();

            }
            zipOut.closeEntry();
            zipOut.close();
            // 文件压缩成功
            FileInputStream inputStream = new FileInputStream(zipFilePath);
            String userName = requestContext.getUserName();
            String zipFile = "attachment;filename=" + new String(fileName.getBytes("utf-8"),"iso-8859-1") + ".zip";

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", zipFile);
            //response.setHeader("Content-Length", String.valueOf(length));
            response.setCharacterEncoding("UTF-8");

            OutputStream os = new BufferedOutputStream(response.getOutputStream());

            byte[] bytes = new byte[1024 * 1024];
            int i = 0;
            while ((i = inputStream.read(bytes)) > 0) {
                os.write(bytes, 0, i);
            }
            os.flush();
            os.close();

        }
    }

    public InputStream getAttachmentInputStream(String filePath) throws AttachmentException {
        if (StringUtils.isBlank(filePath)) {
            this.logger.error("can not find attachment with filePath [{}]", filePath);
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        } else {
            Object answer;

            File file = new File(filePath);
            if (!file.exists()) {
                throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
            }

            try {
                answer = FileUtils.openInputStream(file);
            } catch (IOException var5) {
                this.logger.error(var5.getMessage(), var5);
                throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
            }


            return (InputStream)answer;
        }
    }

    private static int compareDays(Date start, Date from) {
        if (null == start || null == from) {
            return -1;
        }
        long intervalMilli = Math.abs(start.getTime() - from.getTime());//取绝对值
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }

    @RequestMapping(value = "/prj/virprj/create/content")
    @ResponseBody
    public ResponseData virProjectCreateCheckContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request)throws Exception {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long checkId = ((Integer)param.get("check_id")).longValue();
        Long projectId = ((Integer)param.get("project_id")).longValue();
        String templateCode = param.get("template_code").toString();
        PrjCheck dto = new PrjCheck();
        dto.setContractId(projectId);
        dto.setCheckId(checkId);
        if(param.get("instance_id")!= null){
            Long instanceId = Long.valueOf(param.get("instance_id").toString());
            dto.setProcessInstanceId(instanceId);
        }
        dto.setTemplateCode(templateCode);

        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportVirCreateCheckDocx(iRequest, dto);
        }catch (Exception e){
            logger.error(e.getMessage());
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }
        dto.setSourcePkValue1(list.get(0).getSourcePkValue());
        dto.setAttachmentId(list.get(0).getAttachmentId());
        List<PrjCheck> hlsDto = new ArrayList<PrjCheck>();
        hlsDto.add(dto);
        return new ResponseData(hlsDto);
    }


    @RequestMapping(value = "/prj/project/terminate")
    @ResponseBody
    public ResponseData terminateProject(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        service.terminateProject(requestCtx, dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/prj/project/creditProjectGenerate")
    @ResponseBody
    public ResponseData creditProjectGenerate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        HlsCusPrjProject returnPrj=service.creditProjectGenerate(requestCtx, dto);
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(returnPrj);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/submit/create/credit/con")
    @ResponseBody
    public ResponseData prjCreateCreditCon(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        //创建虚拟合同
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(service.prjCreateCreditCon(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/sign/credit/con")
    @ResponseBody
    public void prjSignCreditCon(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {

        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectId = param.getLongValue("project_id");
        HlsCusPrjProject dto = new HlsCusPrjProject();
        dto.setProjectId(projectId);
        dto.setOrgSignState("CONFIRM");
        service.updateByPrimaryKeySelective(requestCtx, dto);
    }

    @RequestMapping(value = "occupy/prj/project/delete")
    @ResponseBody
    public ResponseData deletePrj(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectId = param.getLongValue("project_id");
        HlsCusPrjProject dto = new HlsCusPrjProject();
        dto.setProjectId(projectId);
        dto.setProjectStatus("NEW");
        List<HlsCusPrjProject> list=new ArrayList<>();
        list.add(dto);
        service.batchDelete(list);
        return new ResponseData();
    }

    @RequestMapping(value = "prj/project/loadInformation")
    @ResponseBody
    public void loadInformation(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = param.toJavaObject(HlsCusPrjProjectAttachment.class);
        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.queryByProjectId(hlsCusPrjProjectAttachment.getProjectId());
        if(hlsCusPrjProjectAttachments.size()>0){
            return;
        }

        HlsCusPrjProject project =new HlsCusPrjProject();
        project.setProjectId(hlsCusPrjProjectAttachment.getProjectId());
        project=service.selectByPrimaryKey(requestCtx,project);
        hlsCusPrjProjectAttachment.setProjectAttachmentCategory("CON_CONTRACT_ATT");

        hlsCusPrjProjectAttachment.setDocumentName("融资租赁合同");
        hlsCusPrjProjectAttachmentMapper.insert(hlsCusPrjProjectAttachment);
        if("LEASEBACK".equals(project.getBusinessType())) {
            hlsCusPrjProjectAttachment.setDocumentName("租赁物转让协议");
            hlsCusPrjProjectAttachmentMapper.insert(hlsCusPrjProjectAttachment);
        }
        if("LEASE".equals(project.getBusinessType())){
            hlsCusPrjProjectAttachment.setDocumentName("买卖合同");
            hlsCusPrjProjectAttachmentMapper.insert(hlsCusPrjProjectAttachment);
        }

    }

    @RequestMapping(value = "prj/project/getLeaseItemList")
    @ResponseBody
    public void getLeaseItemList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = param.toJavaObject(HlsCusPrjProjectLeaseItem.class);
        Integer leaseItemId = (Integer) param.get("lease_item_id");
        String projectId = param.get("project_id").toString();
        String projectType= (String) param.get("project_type");
        List<HlsCusLeaseItemList> hlsCusLeaseItemLists = hlsCusLeaseItemListMapper.selectItemList1(Long.valueOf(leaseItemId));
        Double total = hlsCusLeaseItemListMapper.selectItemTotal(Long.valueOf(leaseItemId));
        for (HlsCusLeaseItemList hlsCusLeaseItemList : hlsCusLeaseItemLists) {
            //String itemType="GENERIC_DEVICE";
            hlsCusPrjProjectLeaseItem.setProjectId(Long.valueOf(projectId));
            hlsCusPrjProjectLeaseItem.setLeaseItemId(Long.valueOf(leaseItemId));
            hlsCusPrjProjectLeaseItem.setQuantity(hlsCusLeaseItemList.getQuantity());
            hlsCusPrjProjectLeaseItem.setPrice(hlsCusLeaseItemList.getPrice());
            hlsCusPrjProjectLeaseItem.setTotalPrice(total);
            hlsCusPrjProjectLeaseItem.setAssetName(hlsCusLeaseItemList.getAssetName());
            if(projectType!=null){
                hlsCusPrjProjectLeaseItem.setProjectType(projectType);
            }
//            hlsCusPrjProjectLeaseItem.setLeaseItemName(name);
            hlsCusPrjProjectLeaseItemMapper.insert(hlsCusPrjProjectLeaseItem);
        }
    }

    @RequestMapping(value = "prj/project/delLeaseItemList")
    @ResponseBody
    public void delLeaseItemList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = param.toJavaObject(HlsCusPrjProjectLeaseItem.class);

        Integer projectId = Integer.valueOf(String.valueOf(param.get("project_id")));
        String projectType= (String) param.get("project_type");

        hlsCusPrjProjectLeaseItemMapper.deleteLeaseItemByProjectId1(hlsCusPrjProjectLeaseItem);

    }

    @RequestMapping("/project/condition/import")
    @ResponseBody
    public Map<String, Object> projectConditionImport(HttpServletRequest request, Long headerId, Long approvalId, Long projectId, String approvalType) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.projectConditionImport(iRequest, headerId, approvalId, projectId, approvalType);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }
    /*
     * 生成报价编号
     * */
    @RequestMapping("/prj/create/quotation/number")
    public ResponseData createPrjQuotationNumber(HttpServletRequest request,Long projectId) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusPrjQuotation> quotations = new ArrayList<>();
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        String quotationNumber = prjProjectService.createQuotationNumber(iRequest,prjProject);
        quotation.setQuotationNumber(quotationNumber);
        quotations.add(quotation);
        return new ResponseData(quotations);
    }

    /**
     * 进件批量提交审批
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/prj/batch/submit")
    @ResponseBody
    public ResponseData contractFileTurnOver(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
        ResponseData responseData = new ResponseData(true);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute(AUTHORITY_RULE_FLAG, N);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get(PARAMETER);
        String[] projectIdList = param.getString(PROJECT_ID_LIST).split(",");
        for (String projectId : projectIdList) {
            service.generateProjectSignAttach(requestCtx,Long.valueOf(projectId));
        }
        responseData.setMessage(prjProjectService.projectBatchSubmit(requestCtx,projectIdList));
        return responseData;
    }
    //项目提交审批
    @RequestMapping(value = "/prj/project/new/wfl/submit")
    @ResponseBody
    public ResponseData wflSubmit(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HlsCusPrjProject prjProject) throws Exception {
        IRequest iRequest = createRequestContext(request);
        Map para = requestData.getParameter();
        if (para.get("projectId") != null) {
            prjProject.setProjectId(Long.valueOf(para.get("projectId").toString()));
        } else {
            throw new ResMessageException("未找到提交的项目,请联系管理员!");
        }

        if (!prjProjectService.validateRequiredInfo(prjProject)) {
            return new ResponseData(false, HlsConstantUtil.PrjProject.ONLY_QUOTATION);
        }

        if (!prjProjectService.validateQuotation(prjProject)) {
            return new ResponseData(false, "报价失败，请检查");
        }

        sysDocumentListService.validateNecessaryDocumentList(iRequest, prjProject.getProjectId(), "PRJ_PROJECT");

        //校验项目状态
        prjProject = service.selectByPrimaryKey(iRequest, prjProject);
        List<String> list = new ArrayList<>();
        //以下状态才可以提交审批
        list.add(DocumentValidate.NEW);
        list.add(DocumentValidate.APPROVED_RETURN);
        list.add(DocumentValidate.CANCEL);
        list.add(DocumentValidate.REJECTED);
        DocumentValidate.statusValidate(prjProject.getProjectStatus(), list);
//        service.projectSubmit(iRequest, prjProject);
        prjProjectService.projectWflSubmit(iRequest, prjProject);
        //承租人分类BP_CLASS若为ORG时，不进行评分
        HlsCusPrjProject hlsCusPrjProject = prjProjectService.queryProjectByProjectId(prjProject.getProjectId());
        if(null!=hlsCusPrjProject){
            //去掉权限控制
            iRequest.setAttribute("authorityRuleFlag", "N");
            HlsBpMaster hlsBpMaster = hlsBpMasterService.queryByBpId(hlsCusPrjProject.getTenantId());
            //自然人
            if("NP".equals(hlsBpMaster.getBpClass())){
                //自动评分
                prjProjectService.autoScore(iRequest,prjProject.getProjectId(),"PRJ_PROJECT","N");
            }
        }
        return new ResponseData();
    }

    //二期功能：投放审查管理-投放审查申请按钮：批量提交审批
    @RequestMapping(value = "/prj/sign/batch/wfl/submit")
    @ResponseBody
    public ResponseData wflBatchSignSubmit(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        ResponseData responseData = new ResponseData(true);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute(AUTHORITY_RULE_FLAG, N);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);
        responseData.setMessage(service.signBatchWflSubmit(requestCtx,list));
        return responseData;
    }

    /**
     * 二期功能：投放审查管理-签约退回按钮
     * */
    @RequestMapping("/prj/sign/return")
    public ResponseData pojectSignReturn(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData(true);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);
        // 插入退回记录
        for (HlsCusPrjProject hlsCusPrjProject : list) {
            ProcessCancelDetail cancelDetail = new ProcessCancelDetail();
            cancelDetail.setDocumentId(hlsCusPrjProject.getProjectId());
            cancelDetail.setDocumentType("PRJ");
            cancelDetail.setCancelExplanation(hlsCusPrjProject.getRejectedDescription());
            cancelDetail.setCancelReason(hlsCusPrjProject.getCancelReason());
            cancelDetail.setCancelType(hlsCusPrjProject.getReturnType());
            cancelDetail.setCancelDate(new Date());
            if (Objects.nonNull(hlsCusPrjProject.getChangeProjectNumberFlag())) {
                cancelDetail.setChangeProjectNumberFlag(hlsCusPrjProject.getChangeProjectNumberFlag());
            }
            cancelDetailService.insertSelective(iRequest, cancelDetail);
        }
        service.prjSignReturn(iRequest, list);
        return  responseData;
    }

    //生成签约附件清单
    @RequestMapping(value = "/prj/generate/project/sign/attach")
    @ResponseBody
    public ResponseData generateProjectSignAttach(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get(PARAMETER);
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);

        if (Objects.isNull(hlsCusPrjProject)) {
            throw new HlsCusException("未找到相应的单据，请联系管理员！");
        }
        service.generateProjectSignAttach(iRequest,hlsCusPrjProject.getProjectId());
        return new ResponseData(true);
    }
    /*
     *
     * 查询租赁物总价款信息
     * */
    @RequestMapping(value = "/prj/project/lease/item/amount/query")
    @ResponseBody
    public ResponseData queryLeaseItemAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        ArrayList list = new ArrayList();
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject project = param.toJavaObject(HlsCusPrjProject.class);
        Double sumAmount = hlsCusPrjProjectMapper.selectLeaseItemAmount(project);
        project.setLeaseItemAmount(sumAmount);

        //名义价格
        Map prjRpLModifyQuery = hlsCusPrjProjectMapper.prjRpLModifyQuery(project);
        HlsCusBpMaster hlsCusBpMaster=new HlsCusBpMaster();
        hlsCusBpMaster.setBpName((String) prjRpLModifyQuery.get("manufacturer_id_n"));
        List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.select(hlsCusBpMaster);
        HlsProductDefinition hlsProductDefinition=new HlsProductDefinition();
        hlsProductDefinition.setBpId(hlsCusBpMasters.get(0).getBpId());
        List<HlsProductDefinition> hlsProductDefinitions = hlsProductDefinitionMapper.selectHlsProductDefinitionList(hlsProductDefinition);
        if(hlsProductDefinitions.size()>0){
            List<HlsProductDefinitionPara> hlsProductDefinitionParas = hlsProductDefinitionParaMapper.selectHlsProductDefinitionParaList(hlsProductDefinitions.get(0));
            for (HlsProductDefinitionPara hlsProductDefinitionPara : hlsProductDefinitionParas) {
                if("RESIDUAL_VALUE".equals(hlsProductDefinitionPara.getProductPara())){
                    project.setResidualValue(Double.valueOf(String.valueOf(hlsProductDefinitionPara.getDefaultValue())));
                }
            }
        }else{
            project.setResidualValue(0D);
        }

        list.add(project);
        return new ResponseData(list);
    }
    /*
     * 后台计算报价
     * */
    @RequestMapping(value = "/quotaion/auto/calc")
    @ResponseBody
    public ResponseData quotationAutoCalc(HttpServletRequest request, Long quotationId,String leaseStartDate,Long projectId) throws Exception {
        IRequest iRequest = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        Map prjRpLModifyQuery = hlsCusPrjProjectMapper.prjRpLModifyQuery(hlsCusPrjProject);
        HlsCusPrjProject hlsCusPrjProject1 = hlsCusPrjProjectMapper.selectPrjById(projectId);
        Long bpId = Long.valueOf(prjRpLModifyQuery.get("manufacturer_id").toString());
        List<HlsBpMasterInceptRule> hlsBpMasterInceptRules = hlsBpMasterInceptRuleMapper.query(bpId);
        HlsProductDefinition hlsProductDefinition=new HlsProductDefinition();
        hlsProductDefinition.setBpId(bpId);
        List<HlsProductDefinition> hlsProductDefinitions = hlsProductDefinitionMapper.selectHlsProductDefinitionList(hlsProductDefinition);
        List<HlsProductDefinitionPara> hlsProductDefinitionParas = hlsProductDefinitionParaMapper.selectHlsProductDefinitionParaList(hlsProductDefinitions.get(0));
        for (HlsProductDefinitionPara hlsProductDefinitionPara : hlsProductDefinitionParas) {
            if("租息率".equals(hlsProductDefinitionPara.getProductParaDisplay())){
                iRequest.setAttribute("intRateReplyTmp",hlsProductDefinitionPara.getDefaultValue());
            }
            if("服务费比例".equals(hlsProductDefinitionPara.getProductParaDisplay())){
                iRequest.setAttribute("leaseChargeRatioReplyTmp",hlsProductDefinitionPara.getDefaultValue());
            }
        }
        iRequest.setAttribute("exemptPenaltyIntN",hlsProductDefinitions.get(0).getExemptPenaltyIntN());
        iRequest.setAttribute("marginDeductionMethodN",hlsProductDefinitions.get(0).getMarginDeductionMethodN());
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String format = df.format(hlsCusPrjProject1.getLeaseStartDate());
        iRequest.setAttribute("leaseStartDate",format);
        for (HlsBpMasterInceptRule hlsBpMasterInceptRule : hlsBpMasterInceptRules) {
            if("Y".equalsIgnoreCase(hlsBpMasterInceptRule.getEnabledFlag())){
                if("FIXED_DAY".equalsIgnoreCase(hlsBpMasterInceptRule.getInceptRuleType())){
                    iRequest.setAttribute("inceptFixDay",hlsBpMasterInceptRule.getFixedDay());
                }else{
                    String[] strings = format.split("-");
                    iRequest.setAttribute("inceptFixDay",strings[strings.length-1]);
                }
            }
        }
        iRequest.setAttribute("floatingRangeMethodN",prjRpLModifyQuery.get("floating_range_method_n"));
        hlsCusPrjQuotationService.quotationReCalc(iRequest,quotationId,false);
//        prjProjectService.calcQuotationFront(iRequest,projectId,quotationId);
        return new ResponseData(true);

    }
    /**
     * 获取项目编码
     *
     * @param request
     * @param documentCategory
     * @param documentType
     * @param businessType
     * @return
     * @author Marshal
     */
    @RequestMapping("/project/common/coding/getCodeValue")
    public ResponseData getProjectCommonNumber(HttpServletRequest request,
                                               @RequestParam String documentCategory,
                                               @RequestParam String documentType,
                                               @RequestParam String businessType,
                                               @RequestParam(required = false) Long factoryId,
                                               @RequestParam Long manufacturerId) throws Exception {
        if (StringUtils.isAnyBlank(documentCategory, documentType, businessType)) {
            return new ResponseData(false, "请求发生错误!");
        }
        IRequest iRequest = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setDocumentType("PRJ_PROJECT");
        hlsCusPrjProject.setBusinessType(businessType);
        hlsCusPrjProject.setDocumentCategory(documentCategory);
        hlsCusPrjProject.setManufacturerId(manufacturerId);
        hlsCusPrjProject.setFactoryId(factoryId);
        hlsCusPrjProject.setContractTextStatus("UNCREATED");
        String projectNumber = prjProjectService.getProjectNumber(iRequest,hlsCusPrjProject);
        return new ResponseData(Arrays.asList(projectNumber));
    }
    /**
     *
     *处理合作方进件序号和支付表编号中的非法字符
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/project/partners_number/check")
    @ResponseBody
    public ResponseData internalRecord(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject  parameter = (JSONObject) requestData.get("parameter");
        Map<Long, Object> param = parameter.toJavaObject(Map.class);
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        String contractNumber = String.valueOf(param.get("partners_contract_number")) ;
        String paymentNumber = String.valueOf(param.get("partners_payment_number")) ;
        Map map = new HashMap();
        if(contractNumber != null){
            Matcher m = p.matcher(contractNumber);
            map.put("partners_contract_number",m.replaceAll(""));
        }
        if(paymentNumber != null){
            Matcher n = p.matcher(paymentNumber);
            map.put("partners_payment_number",n.replaceAll(""));

        }
        List<Map> mapList = new ArrayList<>();
        mapList.add(map);
        return new ResponseData(mapList);
    }

    /**
     * 通过pb_id获取合作方名字
     */
    @RequestMapping(value = "/prj/manufacturer/query")
    @ResponseBody
    public ResponseData queryManufacturer(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request,String bpName){
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, BaseConstants.NO);
        JSONObject param = (JSONObject)requestData.get(PARAMETER);
        HlsCusBpMaster hlsCusBpMaster =new HlsCusBpMaster();
        hlsCusBpMaster.setBpName(bpName);
        List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.queryVenderInfo(hlsCusBpMaster);
        for (HlsCusBpMaster cusBpMaster : hlsCusBpMasters) {
            if(cusBpMaster.getBpName().equals(bpName)){
                return new ResponseData( hlsBpMasterRelationMapper.query(hlsCusBpMasters.get(0).getBpId()));
            }
        }
        return new ResponseData();
    }

    /**
     * 通过pb_id获取合作方授信金额、承租人已用金额、本次待投放金额
     */
    @RequestMapping(value = "/prj/manufacturer/query/amount")
    @ResponseBody
    public ResponseData queryManufacturerAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request,Long projectId){
        IRequest requestContext = createRequestContext(request);

        Map<String,Object> map=new HashMap<>();
        List<Map> list=new ArrayList<>();
        Double creditAmt = 0D;
        Double tenantAmount = 0D;
        Double NotDeployedAmount = 0D;

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = prjProjectService.selectByPrimaryKey(requestContext,hlsCusPrjProject);
        if(hlsCusPrjProject==null){
            map.put("creditAmt", creditAmt);
            map.put("tenantAmount", tenantAmount);
            list.add(map);
            return new ResponseData(list);
        }

        List<HlsCusPrjProject> creditAmtProjects = hlsCusPrjProjectMapper.queryCreditAmt(hlsCusPrjProject.getManufacturerId(), hlsCusPrjProject.getLeaseStartDate());
        // 存储已计算过的授信项目，已有的就跳过，因为可能找到多个definition
        HashSet<Long> calcedProject = new HashSet<>();
        if(CollectionUtils.isNotEmpty(creditAmtProjects)){
            for(HlsCusPrjProject creditAmtProject:creditAmtProjects){
                Double usedFinanceAmount = 0D;
                Double UnusedFinanceAmount = 0D;
                if("REVOLVING".equals(creditAmtProject.getQuotaType())){
                    usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountRevolving(creditAmtProject.getDefinitionId());
                }else{
                    usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountNonRevolving(creditAmtProject.getDefinitionId());
                }
                //未投放额度：取合作方所有关联进件，订单状态为开启且未投放的总额
                UnusedFinanceAmount = hlsCusPrjProjectMapper.queryNotDeployedFinanceAmount(creditAmtProject.getDefinitionId());
                if (!calcedProject.contains(creditAmtProject.getProjectId())) {
                    calcedProject.add(creditAmtProject.getProjectId());
                    creditAmt += creditAmtProject.getCreditAmt();
                }
                tenantAmount +=usedFinanceAmount;
                NotDeployedAmount +=UnusedFinanceAmount;
            }
        }
        map.put("creditAmt", creditAmt);
        map.put("tenantAmount", tenantAmount);
        map.put("releaseAmount", NotDeployedAmount);
        map.put("surplusAmount", creditAmt-tenantAmount-NotDeployedAmount);
        map.put("currentMargin", 0D);
        map.put("remainingInvested", 0D);
        map.put("depositAlreadyInvested", 0D);
        list.add(map);
        return new ResponseData(list);
    }

    /**
     * 通过pb_id获取合作方授信金额、承租人已用金额、本次待投放金额
     */
    @RequestMapping(value = "/contract/manufacturer/query/amount")
    @ResponseBody
    public ResponseData queryConManufacturerAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request,Long contractId){
        IRequest requestContext = createRequestContext(request);
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(contractId);
        hlsCusConContract = conContractService.selectByPrimaryKey(requestContext,hlsCusConContract);

        Map<String,Object> map=new HashMap<>();
        List<Map> list=new ArrayList<>();
        Double creditAmt = 0D;
        Double tenantAmount = 0D;
        if(hlsCusConContract==null){
            map.put("creditAmt", creditAmt);
            map.put("tenantAmount", tenantAmount);
            list.add(map);
            return new ResponseData(list);
        }
        HashSet<Long> calcedProject = new HashSet<>();
        List<HlsCusPrjProject> creditAmtProjects = hlsCusPrjProjectMapper.queryCreditAmt(hlsCusConContract.getManufacturerId(), hlsCusConContract.getLeaseStartDate());
        if(CollectionUtils.isNotEmpty(creditAmtProjects)){
            for(HlsCusPrjProject creditAmtProject:creditAmtProjects){
                Double usedFinanceAmount = 0D;
                if("REVOLVING".equals(creditAmtProject.getQuotaType())){
                    usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountRevolving(creditAmtProject.getDefinitionId());
                }else{
                    usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountNonRevolving(creditAmtProject.getDefinitionId());
                }
                if (!calcedProject.contains(creditAmtProject.getProjectId())) {
                    creditAmt += creditAmtProject.getCreditAmt();
                    calcedProject.add(creditAmtProject.getProjectId());
                }
                tenantAmount +=usedFinanceAmount;
            }
        }
        map.put("creditAmt", creditAmt);
        map.put("tenantAmount", tenantAmount);
        list.add(map);
        return new ResponseData(list);
    }


    /**
     * 判断是否是高端装备
     */
    @RequestMapping(value = "/prj/manufacturer/query/isAdvancedEquipment")
    @ResponseBody
    public ResponseData queryAdvancedEquipment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, BaseConstants.NO);
        JSONObject param = (JSONObject)requestData.get(PARAMETER);
        Map map=new HashMap();
        map.put("classifyId",param.get("classify_id"));
        List<YxLeaseItemClassify> yxLeaseItemClassifies = yxLeaseItemClassifyMapper.yxLeaseItemClassifyQuery(map);
        return new ResponseData(yxLeaseItemClassifies);
    }

    /**
     * 保存承租人备注
     */
    @RequestMapping(value = "/prj/save/description")
    @ResponseBody
    public ResponseData saveDescription(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, BaseConstants.NO);
        JSONObject param = (JSONObject)requestData.get(PARAMETER);
        HlsCusPrjProject prjProject=new HlsCusPrjProject();
        if(param.get("project_id") != null) {
            prjProject.setProjectId(Long.valueOf(String.valueOf(param.get("project_id"))));
            prjProject.setDescription((String) param.get("description"));
            hlsCusPrjProjectMapper.saveDescription(prjProject);
        }else{
            //进件承租人零售标志赋值为Y
            HlsCusBpMaster master=new HlsCusBpMaster();
            master.setBpId(Long.valueOf(String.valueOf(param.get("bp_id"))));
            master.setRetailFlag(String.valueOf(param.get("retail_flag")));
            hlsBpMasterMapper.updateByPrimaryKeySelective(master);
        }
        return new ResponseData();
    }


    /**
     * 厂商起租规则查询
     */
    @RequestMapping(value = "/prj/manufacturer/type/query")
    @ResponseBody
    public ResponseData queryManufacturerUpdateType(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, BaseConstants.NO);
        JSONObject param = (JSONObject)requestData.get(PARAMETER);
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(param.getString(MANUFACTURER_ID));
        return new ResponseData(service.queryManufacturerInceptType(hlsBpMaster));
    }

    /**
     * 获取项目和合同的厂商code
     * @param requestData 合同或项目编号
     * @param request 请求消息
     * @return 厂商code
     */
    @RequestMapping(value = "/prj/project/get/manufacturerCode")
    @ResponseBody
    public ResponseData getManufacturerCode(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, N);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String contractNumber = param.getString("contractNumber");
        if(StringUtils.isEmpty(contractNumber)){
            contractNumber = param.getString("projectNumber");
        }
        Long contractId = param.getLong("contractId");
        Long projectId = param.getLong("projectId");
        ArrayList<String> list = new ArrayList<>(4);
        list.add(prjProjectService.getManufacturerCode(contractNumber,contractId,projectId));
        return new ResponseData(list);
    }
    /**
     * 获取项目和合同的产品线
     * @param requestData 合同或项目编号
     * @param request 请求消息
     * @return 产品线
     */
    @RequestMapping(value = "/prj/project/get/division")
    @ResponseBody
    public ResponseData getDivision(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, N);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String contractNumber = param.getString("contractNumber");
        if(StringUtils.isEmpty(contractNumber)){
            contractNumber = param.getString("projectNumber");
        }
        Long contractId = param.getLong("contractId");
        Long projectId = param.getLong("projectId");
        ArrayList<String> list = new ArrayList<>(4);
        list.add(prjProjectService.getDivision(contractNumber,contractId,projectId));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/allocate/change/submit")
    @ResponseBody
    public ResponseData projectAmountAllocateSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);

        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(service.projectAmountAllocateSubmit(requestCtx, hlsCusPrjProject));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/supplement/query")
    @ResponseBody
    public ResponseData queryPrjSupplement(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,  HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, BaseConstants.NO);
        JSONObject param = (JSONObject) requestData.get(PARAMETER);
        Map<String, Object> project = param.toJavaObject(Map.class);

        if(null == requestContext.getRoleId()){
            throw new HlsCusException("未获取到当前用户的角色信息，请联系管理员！");
        }else {
            Role role = roleMapper.selectByPrimaryKey(requestContext.getRoleId());
            if(!StringUtils.equals(role.getRoleCode(),ADMIN)){
                //判断请求的用户角色
                if(null == requestContext.getUserId()){
                    throw new HlsCusException("未获取到当前用户的信息，请联系管理员！");
                }else {
                    User user = userMapper.selectByPrimaryKey(requestContext.getUserId());
                    if(Objects.isNull(user)){
                        throw new HlsCusException("未获取到当前用户的信息，请联系管理员！");
                    }else {
                        if(null == user.getBpId()){
//                            throw new HlsCusException("当前用户尚未关联商业伙伴，请联系管理员！");
                            project.put("user_id",requestContext.getUserId());
                        }else {
                            HlsCusBpMaster hlsBpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectByPrimaryKey(user.getBpId());
                            project.put("bp_category",hlsBpMaster.getBpCategory());
                            project.put("user_id",requestContext.getUserId());
                            if(!hlsBpMaster.getBpType().equals("MANAGEMENT_COMPANY")){
                                project.put("bp_id",hlsBpMaster.getBpId());
                            }
                        }
                    }
                }
            }
        }
        List<Map> result = service.prjSupplementQuery(requestContext, project,pagenum, pagesize);
        return new ResponseData(result);
    }



    /**
     * 创建前厂商资料定义校验
     * @param request
     * @return
     */
    @RequestMapping(value = "/project/check/before/create")
    @ResponseBody
    public ResponseData projectCheckBeforeCreate(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        String result = "PASS";
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        String division = paramJson.getString("division");
        String business_type = paramJson.getString("business_type");
        Long manufacturer_id = paramJson.getLong("manufacturer_id");
        Long bp_id = paramJson.getLong("bp_id");

//        HlsBpMaster hlsBpMaster = (HlsBpMaster) hlsBpMasterMapper.selectByPrimaryKey(bp_id);
//        PrjCddItemRuleAssign prjCddItemRuleAssignQuery = new PrjCddItemRuleAssign();
//        prjCddItemRuleAssignQuery.setBpId(manufacturer_id);
//        prjCddItemRuleAssignQuery.setDivision(division);
//        prjCddItemRuleAssignQuery.setBusinessType(business_type);
//        prjCddItemRuleAssignQuery.setBpClass(hlsBpMaster.getBpClass());
//        prjCddItemRuleAssignQuery.setEnabledFlag("Y");
//        List<PrjCddItemRuleAssign> prjCddItemRuleAssignList = prjCddItemRuleAssignMapper.select(prjCddItemRuleAssignQuery);
//
//        if(prjCddItemRuleAssignList.size() == 0){
//            result = "请先分配附件清单规则！";
//        }else if(prjCddItemRuleAssignList.size() > 1){
//            result = "找到多条附件清单规则，请删除多余规则！";
//        }
        return new ResponseData(Arrays.asList(result));
    }

    @RequestMapping("/prj/batch/excel/import")
    @Transactional(rollbackFor = Exception.class)
    public ResponseData prjBatchExcelImport(HttpServletRequest request,Long headerId,String division) throws HlsCusException {
        logger.info("进件批量导入进入controller：headerId：{}，division：{}",headerId,division);
        ResponseData responseData = new ResponseData(true);
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute(AUTHORITY_RULE_FLAG,N);
        prjProjectService.excelBatchImport(iRequest,headerId,division);

        //判断导入是否有异常信息
        String message = interfaceErrorMsgService.checkImportErrorMessageExist(headerId,"PRJ_PROJECT");
        responseData.setMessage(message);
        //存在异常信息手动回滚事务
        if(StringUtils.equals(message,BaseConstants.NO)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return responseData;
    }

    @RequestMapping("/prj/batch/excel/import2")
    @Transactional(rollbackFor = Exception.class)
    public ResponseData prjBatchExcelImport2(HttpServletRequest request,Long headerId,String division,Long projectId) throws HlsCusException {
        logger.info("进件批量导入进入controller：headerId：{}，division：{}",headerId,division);
        ResponseData responseData = new ResponseData(true);
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute(AUTHORITY_RULE_FLAG,N);
        prjProjectService.excelBatchImport2(iRequest,headerId,division,projectId);

        //判断导入是否有异常信息
        String message = interfaceErrorMsgService.checkImportErrorMessageExist(headerId,"PRJ_PROJECT");
        responseData.setMessage(message);
        //存在异常信息手动回滚事务
        if(StringUtils.equals(message,BaseConstants.NO)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return responseData;
    }

    @RequestMapping(value = "/prj/check/download/confirm/text")
    @ResponseBody
    public ResponseData contentCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<Long> list = param.toJavaList(Long.class);
        List<FndAttachmentMulti> list1= service.contextCreateMultiple(requestCtx, list, response);
        return new ResponseData(list1);
    }

    @RequestMapping(value = "/hls/manufacturer/reply/product/query2")
    @ResponseBody
    public ResponseData manufacturerBpCreditLineQuery1(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                       @RequestParam(defaultValue = "") String latestFlag,
                                                       @RequestParam(required = false) Long replyId,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                       HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
//        ReplyProduct dto = param.toJavaObject(ReplyProduct.class);
        HlsCusPrjProject dto =param.toJavaObject(HlsCusPrjProject.class);

//        if (!StringUtils.isEmpty(latestFlag)) {
//            dto.setLatestFlag(latestFlag);
//        }
//        if (!StringUtils.isEmpty(replyId.toString())) {
//            dto.setReplyId(replyId);
//        }
        return new ResponseData(service.manufacturerQueryProductInfo2(iRequest, dto, pagenum, pagesize));
    }
    @RequestMapping(value = "/hls/manufacturer/reply/product/query3")
    @ResponseBody
    public ResponseData manufacturerBpCreditLineQuery2(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                       @RequestParam(defaultValue = "") String latestFlag,
                                                       @RequestParam(required = false) Long replyId,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                       HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusPrjProject dto =param.toJavaObject(HlsCusPrjProject.class);

        return new ResponseData(service.manufacturerQueryProductInfo3(iRequest, dto, pagenum, pagesize));
    }
    @RequestMapping(value = "/hls/manufacturer/reply/product/querySales")
    @ResponseBody
    public ResponseData selectSalesByEmployeeName(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                  @RequestParam(defaultValue = "") String latestFlag,
                                                  @RequestParam(required = false) Long replyId,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                  HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusPrjProject dto =param.toJavaObject(HlsCusPrjProject.class);
        return new ResponseData(service.selectSalesByEmployeeName(iRequest, dto.getEmployeeName(), pagenum, pagesize));
    }
    /**
     * 当前登录用户信息获取
     * @param request
     * @return
     */
    @RequestMapping(value = "/prj/user/info/query")
    @ResponseBody
    public ResponseData queryUserInfo(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.queryUserInfo(iRequest,params));
    }

    /**
     * 检查租赁物编号是否重复
     * @param requestData 请求数据
     * @param request 请求消息
     * @return 校验结果
     */
    @RequestMapping(value = "/prj/project/lease/item/check")
    @ResponseBody
    public ResponseData allLeaseItemCheck(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, N);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String itemNumber = param.getString("itemNumber");
        String division = param.getString("division");
        String contractNumber = param.getString("contractNumber");
        String classifyId = param.getString("classifyId");
        ArrayList<String> list = new ArrayList<>(4);
        list.add(service.allLeaseItemCheck(itemNumber,division,contractNumber,classifyId));
        return new ResponseData(list);
    }
    @RequestMapping(value = "/prj/rpt033/query")
    @ResponseBody
    public ResponseData queryListForRPT(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,  HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute(AUTHORITY_RULE_FLAG, BaseConstants.NO);
        JSONObject param = (JSONObject) requestData.get(PARAMETER);
        Map<String, Object> project = param.toJavaObject(Map.class);

        if(null == requestContext.getRoleId()){
            throw new HlsCusException("未获取到当前用户的角色信息，请联系管理员！");
        }else {
            Role role = roleMapper.selectByPrimaryKey(requestContext.getRoleId());
            if(!StringUtils.equals(role.getRoleCode(),ADMIN)){
                //判断请求的用户角色
                if(null == requestContext.getUserId()){
                    throw new HlsCusException("未获取到当前用户的信息，请联系管理员！");
                }else {
                    User user = userMapper.selectByPrimaryKey(requestContext.getUserId());

                    if (Objects.isNull(user)) {
                        throw new HlsCusException("未获取到当前用户的信息，请联系管理员！");
                    }
                    else {
                        if(null == user.getBpId()){
                            project.put("flag",0);

                        }else {
                            project.put("flag",1);
                            project.put("bp_id",user.getBpId());
                        }



//
//                        else {
                        //
//                        SysUserAllocation sysUserAllocation1 = new SysUserAllocation();
//                        sysUserAllocation1.setUserId(user.getUserId());
//                        if (null == sysUserAllocationMapper.select(sysUserAllocation1)
//                        ) {
//                            throw new HlsCusException("当前用户尚未绑定员工，请联系管理员！");
//                        } else {
//                            List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.select(sysUserAllocation1);
//
//                            FndEmployeeAssigns fndEmployeeAssigns = fndEmployeeAssignsMapper.selectByPrimaryKey(sysUserAllocations.get(0).getEmployeeAssignId());
//                            project.put("employee_id", fndEmployeeAssigns.getEmployeeId());
//                            project.put("user_id", user.getUserId());
//                        }

                    }

                }

            }
            else{ project.put("flag",1);
            }
        }

        List<Map> result = service.queryListForRpt(requestContext, project,pagenum, pagesize);
        return new ResponseData(result);
    }

    @RequestMapping(value = "/prj/process/report/query")
    @ResponseBody
    public ResponseData queryPrjProcessInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,  HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        Map<String, Object> project = param.toJavaObject(Map.class);
        List<Map> result = service.prjProcessInfoQuery(requestContext, project,pagenum, pagesize);
        return new ResponseData(result);
    }
}