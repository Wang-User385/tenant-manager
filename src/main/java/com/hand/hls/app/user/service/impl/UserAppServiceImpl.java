package com.hand.hls.app.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.activiti.custom.process.CustomHistoricProcessInstanceQueryRequest;
import com.hand.hap.activiti.dto.HistoricProcessInstanceResponseExt;
import com.hand.hap.activiti.dto.TaskActionRequestExt;
import com.hand.hap.activiti.dto.TaskNew;
import com.hand.hap.activiti.dto.TaskResponseExt;
import com.hand.hap.activiti.exception.WflSecurityException;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.security.components.PasswordManager;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.GENER.mapper.HlsGeneralIssueMapper;
import com.hand.hls.app.user.dto.HlsCusVersion;
import com.hand.hls.app.user.mapper.UserAppMapper;
import com.hand.hls.app.user.service.IHlsCusVersionService;
import com.hand.hls.app.user.service.IUserAppService;
import com.hand.hls.app.utils.generalUtils.AppCheckRequiredUtils;
import com.hand.hls.app.utils.generalUtils.AppConstantUtils;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.mapper.AstFcEstimateMapper;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.mapper.LitigationManagementMapper;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCreditLineAttachMapper;
import com.hand.hls.fct.mapper.HlsCusFctProjectAttachmentMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fin.dto.HlsCusLonContractAttachment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.mapper.HlsCusLonContractAttachmentMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fnd.dto.CockpitImport;
import com.hand.hls.fnd.mapper.CockpitImportMapper;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.dto.JcFundFillingLn;
import com.hand.hls.fp.mapper.JcFundFillingLnMapper;
import com.hand.hls.fp.mapper.JcFundFillingMapper;
import com.hand.hls.hls.dto.*;
import com.hand.hls.hls.mapper.*;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.mapper.PrjLeaseInspectMapper;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.dto.AssetsDisposalAttch;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.pam.mapper.AssetsDisposalAttchMapper;
import com.hand.hls.pam.mapper.AssetsDisposalMapper;
import com.hand.hls.pam.mapper.LeaseAssetHdMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.risk.dto.RiskAttachment;
import com.hand.hls.risk.mapper.RiskAttachmentMapper;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import com.hand.hls.sys.dto.*;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.mapper.HlsCusSysAppointApproverMapper;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.sys.service.IHlsCusSysAppointApproverService;
import com.hand.hls.sys.service.IHlsSystemNoticeService;
import com.hand.hls.taa.dto.JcTransferApplication;
import com.hand.hls.taa.dto.JcTransferApplicationDetal;
import com.hand.hls.taa.mapper.JcTransferApplicationDetalMapper;
import com.hand.hls.taa.mapper.JcTransferApplicationMapper;
import hls.core.utils.exception.HlsCusException;
import org.activiti.rest.common.api.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author liao
 */
@Service

public class UserAppServiceImpl extends BaseServiceImpl<TaskNew> implements IUserAppService {

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationService;
    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;
    @Autowired
    private IUserAppService userAppService;
    @Autowired
    private IHlsCusVersionService hlsCusVersionService;
    @Autowired
    private HlsSystemNoticeMapper hlsSystemNoticeMapper;
    @Autowired
    private HlsCusSysAppointApproverMapper hlsCusSysAppointApproverMapper;
    @Autowired
    private IHlsCusSysAppointApproverService hlsCusSysAppointApproverService;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private IHlsSystemNoticeService hsnService;
    @Autowired
    private UserAppMapper userAppMapper;
    @Autowired
    private HlsCusHlsMarketingReportMapper hlsCusHlsMarketingReportMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusHlsReportAttachmentMapper hlsCusHlsReportAttachmentMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;
    @Autowired
    private HlsCreditLineAttachMapper hlsCreditLineAttachMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;
    @Autowired
    private HlsScoreCalculationMapper hlsScoreCalculationMapper;
    @Autowired
    private HlsCusFctProjectAttachmentMapper hlsCusFctProjectAttachmentMapper;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;
    @Autowired
    private ProjectCreditConditionMapper projectCreditConditionMapper;
    @Autowired
    private CshPaymentAttachmentMapper cshPaymentAttachmentMapper;
    @Autowired
    private HlsCusCshTransactionRefundMapper cshTransactionRefundMapper;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsDurationHdMapper hlsDurationHdMapper;
    @Autowired
    private HlsDurationAttachmentMapper hlsDurationAttachmentMapper;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;
    @Autowired
    private HlsCusLonContractWithdrawMapper hlsCusLonContractWithdrawMapper;
    @Autowired
    private HlsCusLonContractAttachmentMapper hlsCusLonContractAttachmentMapper;
    @Autowired
    private hlsBillRequestMapper hlsBillRequestMapper1;
    @Autowired
    private PrjCheckMapper prjCheckMapper;
    @Autowired
    private AssetsDisposalMapper assetsDisposalMapper;
    @Autowired
    private AssetsDisposalAttchMapper assetsDisposalAttchMapper;
    @Autowired
    private LeaseAssetHdMapper leaseAssetHdMapper;
    @Autowired
    private LitigationManagementMapper litigationManagementMapper;
    @Autowired
    private HlsCusRiskWarningMapper hlsCusRiskWarningMapper;
    @Autowired
    private RiskAttachmentMapper riskAttachmentMapper;
    @Autowired
    private PrjLeaseInspectMapper prjLeaseInspectMapper;
    @Autowired
    private AstFcEstimateMapper astFcEstimateMapper;
    @Autowired
    private HlsGeneralIssueMapper hlsGeneralIssueMapper;
    @Autowired
    private JcTransferApplicationMapper jcTransferApplicationMapper;
    @Autowired
    private JcTransferApplicationDetalMapper jcTransferApplicationDetalMapper;

    @Autowired
    private HlsCusPrjQuotationService quotationService;
    @Autowired
    private CockpitImportMapper cockpitImportMapper;
    @Autowired
    private JcFundFillingMapper jcFundFillingMapper;
    @Autowired
    private JcFundFillingLnMapper jcFundFillingLnMapper;
    @Autowired
    private HlsDurationDepositMapper hlsDurationDepositMapper;


    /**
     * queryUserById 必输字段
     */
    private final static List<String> QUERY_USER_BY_ID = Arrays.asList("userId");

    /**
     * queryUserByName 必输字段
     */
    private final static List<String> QUERY_USER_BY_NAME = Arrays.asList("userName");
    /**
     * queryUserByName 必输字段
     */
    private final static List<String> QUERY_USER_BY_ATTRIBUTE = Arrays.asList("attribute3");
    /**
     * checkLogin 必输字段
     */
    private final static List<String> CHECK_LOGIN = Arrays.asList("password", "userName");
    /**
     * queryProcessInstances 必输字段
     */
    private final static List<String> QUERY_PROCESS_INSTANCES = Arrays.asList("userId", "allocationId", "userName");
    /**
     * taskDetails 必输字段
     */
    private final static List<String> TASK_DETAILS = Arrays.asList("taskId", "adminFlag");
    /**
     * executeTaskAction 必输字段
     */
    private final static List<String> EXECUTE_TASK_ACTION = Arrays.asList("taskId","companyId", "action", "adminFlag");
    /**
     * executeTaskForward 必输字段
     */
    private final static List<String> EXECUTE_TASK_FORWARD = Arrays.asList("allocationId", "taskId", "action", "processInstanceId");
    /**
     * prcJump 必输字段
     */
    private final static List<String> PRC_JUMP = Arrays.asList("processInstanceId", "jumpTarget", "jumpTargetName");
    /**
     * queryUserAllNotice 必输校验
     */
    private final static List<String> QUERY_USER_ALL_NOTICE = Arrays.asList("allocationId");
    private final static List<String> QUERY_USER_ALL_NOTICE_ID = Arrays.asList("noticeId");
    /**
     * SAVE_DESIGNATED 必输校验
     */
    private final static List<String> SAVE_DESIGNATED = Arrays.asList("processInstanceId", "userId", "userAllocationId", "sourceId", "sourceTable", "sidCode", "positionId");
    /**
     * QUERY_USER_ROLE 必输校验
     */
    private final static List<String> QUERY_USER_ROLE = Arrays.asList("allocationId", "userId");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseData queryUserById(IRequest iRequest, JSONObject jsonObject) throws Exception {
        ResponseData responseData = new ResponseData();

        try {

            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, QUERY_USER_BY_ID);
            if (!responseData.isSuccess()) {
                return responseData;
            }
            User user = new User();
            user.setUserId(Long.valueOf(jsonObject.getString("userId")));
            responseData.setSuccess(true);
            responseData.setRows(userService.selectUsers(iRequest, user, AppConstantUtils.PublicConstans.DEFAULT_PAGE, AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE));
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseData queryUserByName(IRequest iRequest, JSONObject jsonObject) throws Exception {
        ResponseData responseData = new ResponseData();

        try {

            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, QUERY_USER_BY_ATTRIBUTE);
            if (!responseData.isSuccess()) {
                return responseData;
            }
            responseData.setSuccess(true);
            User user1 = userService.selectByAttribute3(jsonObject.getString("attribute3"));
            List<User> userList = new ArrayList<>();
            userList.add(user1);
            responseData.setRows(userList);
//            responseData.setRows(userService.selectUsers(iRequest, user, AppConstantUtils.PublicConstans.DEFAULT_PAGE, AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE));
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData checkLogin(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();

        try {
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, CHECK_LOGIN);
            if (!responseData.isSuccess()) {
                return responseData;
            }
            User user = userMapper.selectByUserName(jsonObject.getString("userName"));
            if (user != null) {
                if (passwordManager.matches(jsonObject.getString("password"), user.getPasswordEncrypted())) {
                    List<User> userList = new ArrayList<User>();
                    userList.add(user);
                    responseData.setSuccess(true);
                    responseData.setRows(userList);
                    responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
                } else {
                    responseData.setSuccess(false);
                    responseData.setMessage(AppConstantUtils.PublicMsg.LOGIN_PASSWORD_ERROR);
                }
            } else {
                responseData.setSuccess(false);
                responseData.setMessage(AppConstantUtils.PublicMsg.LOGIN_USER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData allocationQueryByUserId(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();

        try {
            if (jsonObject.getString("userId").isEmpty() || jsonObject.getString("userId") == null) {
                return new ResponseData(false, "缺少必输参数 userId!");
            }
            Long userId = Long.parseLong(jsonObject.getString("userId"));
            List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.editQuery(userId, jsonObject.getLong("allocationId"));


            responseData.setSuccess(true);
            responseData.setRows(sysUserAllocations);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData queryTask(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();
        try {

            int page;
            int pageSize;
            if (!jsonObject.getString("page").isEmpty()) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (!jsonObject.getString("pageSize").isEmpty()) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }
            RequestHelper.setCurrentRequest(iRequest);
            TaskNew dto = new TaskNew();
            dto.setAssignee(jsonObject.getString("allocationId"));
            dto.setUserId(jsonObject.getLong("userId"));
            List<TaskNew> userTaskList = activitiService.queryTaskListNew(dto, page, pageSize);
            responseData.setSuccess(true);
            responseData.setRows(userTaskList);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData quotationCalc(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            List<HlsCusPrjQuotation> responseInfo = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date leaseStartDate = sdf.parse(jsonObject.getString("lease_start_date"));
            String leaseStartDateString = sdf.format(leaseStartDate);
            jsonObject.put("lease_start_date",leaseStartDateString);
            HlsCusPrjQuotation  hlsCusPrjQuotation= jsonObject.toJavaObject(HlsCusPrjQuotation.class);
            hlsCusPrjQuotation = quotationService.savePrjQuotationForApp(iRequest,hlsCusPrjQuotation);
            Long quotationId = hlsCusPrjQuotation.getQuotationId();
            quotationService.quotationReCalc(iRequest, quotationId,true);
            hlsCusPrjQuotation = quotationService.selectByPrimaryKey(iRequest,hlsCusPrjQuotation);
            responseInfo.add(hlsCusPrjQuotation);

            responseData.setSuccess(true);
            responseData.setRows(responseInfo);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData queryCashflow(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            if(jsonObject.getString("quotationId") ==null){
                throw new HlsCusException("缺少参数 quotationId");
            }
            if (!jsonObject.getString("quotationId").isEmpty()) {
                Long quotationId = Long.valueOf(jsonObject.getString("quotationId"));
                HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
                cashflow.setQuotationId(quotationId);
                List<Map> cashflows = hlsCusConContractCashflowMapper.selectCashflowInfoForApp(cashflow);

                responseData.setSuccess(true);
                responseData.setRows(cashflows);
                responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData selectComboBox(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            if(jsonObject.getString("code") ==null){
                throw new HlsCusException("缺少参数 code");
            }
            if (!jsonObject.getString("code").isEmpty()) {
                String code = jsonObject.getString("code");
                List<Map> results = new ArrayList<>();
                if("PRICE_LIST".equals(code)){
                    results = userAppMapper.selectPriceList();
                }else if("BUSINESS_TYPE".equals(code)){
                    results = userAppMapper.selectBusinessType();
                }else if("TAX_TYPE_CODE".equals(code)){
                    results = userAppMapper.selectTaxType();
                }else{
                    results = userAppMapper.selectComboBox(code);
                }
                responseData.setSuccess(true);
                responseData.setRows(results);
                responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData selectBusinessReport(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            int page;
            int pageSize;
            if (jsonObject.getString("page") != null) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (jsonObject.getString("pageSize") != null) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }

            /*Map map = new HashMap();
            if (jsonObject.getString("contractName") != null) {
                map.put("contractName",jsonObject.getString("contractName"));
            }
            if (jsonObject.getString("hostProjectManagerN") != null) {
                map.put("hostProjectManagerN",jsonObject.getString("hostProjectManagerN"));
            }
            if (jsonObject.getString("unitName") != null) {
                map.put("unitName",jsonObject.getString("unitName"));
            }*/

            Map map = jsonObject.toJavaObject(Map.class);

            //先查询汇总结果
            Map sum = hlsCusConContractMapper.cshPaymentReqConSum(map);
            //再分页
            PageHelper.startPage(page, pageSize);
            List<HlsCusConContract> data = hlsCusConContractMapper.cshPaymentReqCon(map);

            JSONObject json = new JSONObject();
            json.put("data", data);
            json.put("sum", sum);
            List<JSONObject> result = new ArrayList<>();
            result.add(json);
            responseData.setSuccess(true);
            responseData.setRows(result);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData selectCollectionReport(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            int page;
            int pageSize;
            if (jsonObject.getString("page") != null) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (jsonObject.getString("pageSize") != null) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }

            /*HlsCusConContractCashflow param = new HlsCusConContractCashflow();
            if (jsonObject.getString("contractName") != null) {
                param.setContractName(jsonObject.getString("contractName"));
            }
            if (jsonObject.getString("hostProjectManagerN") != null) {
                param.setHostProjectManagerN(jsonObject.getString("hostProjectManagerN"));
            }
            if (jsonObject.getDate("dueDateFrom") != null) {
                param.setDueDateFrom(jsonObject.getDate("dueDateFrom"));
            }
            if (jsonObject.getDate("dueDateTo") != null) {
                param.setDueDateTo(jsonObject.getDate("dueDateTo"));
            }
            if (jsonObject.getString("hostUnitIdN") != null) {
                param.setHostUnitIdN(jsonObject.getString("hostUnitIdN"));
            }*/

            HlsCusConContractCashflow param = jsonObject.toJavaObject(HlsCusConContractCashflow.class);
            //先查询汇总结果
            Map sum = hlsCusConContractCashflowMapper.selectPayiedRentalConCashflowReportNewSum(param);
            //分页
            PageHelper.startPage(page, pageSize);
            List<HlsCusConContractCashflow> data = hlsCusConContractCashflowMapper.selectPayiedRentalConCashflowReportNew(param);

            JSONObject json = new JSONObject();
            json.put("data", data);
            json.put("sum", sum);
            List<JSONObject> result = new ArrayList<>();
            result.add(json);

            responseData.setSuccess(true);
            responseData.setRows(result);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData selectCockpitReport(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            int page;
            int pageSize;
            if (jsonObject.getString("page") != null) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (jsonObject.getString("pageSize") != null) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }

            CockpitImport param = jsonObject.toJavaObject(CockpitImport.class);

            PageHelper.startPage(page, pageSize);
            List<CockpitImport> data = cockpitImportMapper.queryAllReport(param);

            responseData.setSuccess(true);
            responseData.setRows(data);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData bpCockpitReport(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            int page;
            int pageSize;
            if (jsonObject.getString("page") != null) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (jsonObject.getString("pageSize") != null) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }

            CockpitImport param = jsonObject.toJavaObject(CockpitImport.class);

            PageHelper.startPage(page, pageSize);
            List<CockpitImport> data = cockpitImportMapper.queryBpAllReport(param);

            responseData.setSuccess(true);
            responseData.setRows(data);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData projectCockpitReport(IRequest iRequest, JSONObject jsonObject){
        ResponseData responseData = new ResponseData();
        try {

            RequestHelper.setCurrentRequest(iRequest);

            int page;
            int pageSize;
            if (jsonObject.getString("page") != null) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (jsonObject.getString("pageSize") != null) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }

            CockpitImport param = jsonObject.toJavaObject(CockpitImport.class);
            if (param.getHostProjectManager() != null) {
                //添加权限控制 项目经理只能看到自己的 部门负责人看到整个部门的  其他人看到所有的
                param.setHostProjectManager(param.getHostProjectManager());
                CockpitImport cockpitImport = new CockpitImport();
                cockpitImport.setHostProjectManager(param.getHostProjectManager());
                String authorizationFlag = cockpitImportMapper.queryPrjAuthorization(cockpitImport).get(0).getAuthorizationFlag();
                param.setAuthorizationFlag(authorizationFlag);
            }


            PageHelper.startPage(page, pageSize);
            List<CockpitImport> data = cockpitImportMapper.queryPrjAllReport(param);

            responseData.setSuccess(true);
            responseData.setRows(data);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }
    @Override
    public ResponseData queryWorkflowDetail(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();
        try {

            int page;
            int pageSize;
            /*if (!jsonObject.getString("page").isEmpty()) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (!jsonObject.getString("pageSize").isEmpty()) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }*/

            RequestHelper.setCurrentRequest(iRequest);

            List<JSONObject> responseInfo = new ArrayList<>();
            if(jsonObject.getString("processInstanceId") ==null || jsonObject.getString("workflowType") ==null){
                throw new HlsCusException("缺少参数 processInstanceId或者workflowType");
            }
            if (!jsonObject.getString("processInstanceId").isEmpty()&&!jsonObject.getString("workflowType").isEmpty()) {
                Long processInstanceId = Long.valueOf(jsonObject.getString("processInstanceId"));
                String workflowType = jsonObject.getString("workflowType");
                JSONObject json = new JSONObject();
                //项目方案审批
                if("PRJ_MARKETING_REPORT_WFL".equals(workflowType)){
                    json = prjMarketingReportWfl(processInstanceId);
                }
                //项目立项审批
                else if("FCT_PROJECTCREATE_WFL".equals(workflowType)||"CREDIT_CHANCE_CREATE_WFL".equals(workflowType)){
                    json = fctProjectcreateWfl(processInstanceId);
                }
                //项目审查和评审流程
                else if("PROJECT_REVIEW_WFL".equals(workflowType) || "CREDIT_PROJECT_REVIEW_WFL".equals(workflowType)){
                    json = fctProjectreviewWfl(processInstanceId);
                }
                //客户信用评级审批流程
                else if("CUSTOMER_RATE_WFL".equals(workflowType)){
                    json = customerRateWfl(processInstanceId);
                }
                //租赁合同审查流程
                else if("CON_CONTRACT_CREATE_WFL".equals(workflowType)){
                    json = conContractCreateWfl(processInstanceId);
                }
                //合同核保申请审批流程
                else if("CON_CONTRACT_SIGN_WFL".equals(workflowType)){
                    json = conContractSignWfl(processInstanceId);
                }
                //放款申请审批流程
                else if("CON_PAYMENT_WFL".equals(workflowType)){
                    json = conPaymentWfl(processInstanceId);
                }
                //款项支付流程
                else if("CSH_PAYMENT_WFL".equals(workflowType)){
                    json = cshPaymentWfl(processInstanceId);
                }
                //退款支付审批流程
                else if("CSH_TRANSACTION_REFUND_PAY_WFL".equals(workflowType)){
                    json = cshTransationRefundPayWfl(processInstanceId);
                }
                //关税付款申请审批流程
                else if("TARIFF_PAYMENT_WFL".equals(workflowType)){
                    json = tariffPaymentWfl(processInstanceId);
                }
                //退款申请审批流程
                else if("CSH_TRANSACTION_REFUND_WFL".equals(workflowType)){
                    json = cshTransationRefundWfl(processInstanceId);
                }
                //合同支付表确认审批流程
                else if("CON_CONTRACT_CASHCONFIRM".equals(workflowType)){
                    json = conContractCashconform(processInstanceId);
                }
                //存续期审批工作流-业务变更申请
                else if("DURATION_WFL_SPECIAL".equals(workflowType)){
                    json = durationWflSpecial(processInstanceId);
                }
                //业务变更执行审批工作流
                else if("CONTRACT_CHANGE_WFL".equals(workflowType)){
                    json = contractChangeWfl(processInstanceId);
                }
                //存续期审批工作流-合同终止
                else if("DURATION_WFL_TERMINATE".equals(workflowType)){
                    json = durationWflTerminate(processInstanceId);
                }
                //存续期审批工作流-提前结清
                else if("DURATION_WFL_ET".equals(workflowType)){
                    json = durationWflEt(processInstanceId);
                }
                //租赁合同结束审批流程
                else if("CON_CONTRACT_END_WFL".equals(workflowType)){
                    json = conContractEndWfl(processInstanceId);
                }
                //合同调息审批工作流
                else if("CON_CONTRACT_INTEREST_ADJUSTMENT".equals(workflowType)){
                    json = conContractInterestAdjustment(processInstanceId);
                }
                //融资提款申请审批
                else if("LON_CONTRACT_WITHDRAW_WFL".equals(workflowType)){
                    json = lonContractWithdrawWfl(processInstanceId);
                }
                //融资提款变更审批
                else if("CT_LON_CON_WITHDRAW_CHANGE_WFL".equals(workflowType)){
                    json = lonContractWithdrawWfl(processInstanceId);
                }
                //票据开具审批流程
                else if("BILL_APPLICATION_WFL".equals(workflowType)){
                    json = billApplicationWfl(processInstanceId);
                }
                //资产价值重估审批流程
                else if("PROPERTY_EVALUATE_WFL".equals(workflowType)){
                    json = propertyEvaluateWfl(processInstanceId);
                }
                //资产处置审批流程
                else if("ASSETS_DISPOSAL_WFL".equals(workflowType)){
                    json = assetsDisposalWfl(processInstanceId);
                }
                //诉讼管理审批流程
                else if("LAWSUITS_MANAGEMENT_WFL".equals(workflowType)){
                    json = lawsuitsManagementWfl(processInstanceId);
                }
                //租后检查流程
                else if("RENT_CHECK_WFL".equals(workflowType)){
                    json = rentCheckWfl(processInstanceId);
                }
                //风险预警审批流程
                else if("RISK_WARNING_WFL".equals(workflowType)){
                    json = riskWarningWfl(processInstanceId);
                }

                //租赁物巡查流程
                else if("LEASE_INSPECT_WFL".equals(workflowType)){
                    json = leaseInspectWfl(processInstanceId);
                }
                //资产分类审批流程
                else if("ASSETS_CLASSIFICATION_WFL".equals(workflowType)){
                    json = assetsClassificationWfl(processInstanceId);
                }
                //资产分类应计提拨备审批流程
                else if("PROVISION_SHALL_BE_MADE_WFL".equals(workflowType)){
                    json = provisionShallBeMadeWfl(processInstanceId);
                }
                //通用事项审批
                else if("GENERAL_MATTERS_WFL".equals(workflowType)){
                    json = generalMattersWfl(processInstanceId);
                }

                //转账申请审批流程
                else if("TRANSFER_APPLICATION_WFL".equals(workflowType)){
                    json = transferApplicationWfl(processInstanceId);
                }
                //项目批复变更审批流程
                else if("FCT_PROJECT_CHANGE_WFL".equals(workflowType)){
                    json = fctProjectChangeWfl(processInstanceId);
                }
                //资金计划填报审批流程（业务部）
                else if("FUNDING_PLAN_WFL_NEW".equals(workflowType)){
                    json = fundingPlanWflNew(processInstanceId);
                }
                //资金计划汇总审批工作流
                else if("FUNDING_PLAN_WFL_NEW_ZJ".equals(workflowType)){
                    json = fundingPlanWflNewZj(processInstanceId);
                }
                //保证金抵扣/退还申请审批流程
                else if("DURATION_WFL_B0ND".equals(workflowType)){
                    json = durationWflBond(processInstanceId);
                }
                responseInfo.add(json);
            } else {
                throw new HlsCusException("参数错误");
            }

            responseData.setSuccess(true);
            responseData.setRows(responseInfo);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public JSONObject prjMarketingReportWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long marketing_report_id = Long.valueOf(businessKey);
        //基本信息
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsCusHlsMarketingReport.setMarketingReportId(marketing_report_id);
        List<HlsCusHlsMarketingReport> basics = hlsCusHlsMarketingReportMapper.queryMarketingReportDetail(hlsCusHlsMarketingReport);
        //报价方案
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setSourceDocumentId(marketing_report_id);
        prjQuotation.setSourceDocumentCategory("HLS_MARKETING_REPORT");
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo1(prjQuotation);
        //附件信息
        HlsCusHlsReportAttachment hlsCusHlsReportAttachment = new HlsCusHlsReportAttachment();
        hlsCusHlsReportAttachment.setMarketingReportId(marketing_report_id);
        List<HlsCusHlsReportAttachment> attachments = hlsCusHlsReportAttachmentMapper.queryReportAttachment(hlsCusHlsReportAttachment);
        JSONObject json = new JSONObject();
        json.put("basicInfo", basics);
        json.put("quotation", quotations);
        json.put("attachments", attachments);
        return json;
    }


    @Override
    public JSONObject fctProjectcreateWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long chance_id = Long.valueOf(businessKey);
        //基本信息
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = new HlsCusHlsCreditLineChance();
        hlsCusHlsCreditLineChance.setChanceId(chance_id);
        HlsCusHlsCreditLineChance basic = hlsCusHlsCreditLineChanceMapper.selectCreditLineChanceById(hlsCusHlsCreditLineChance);
        //报价信息
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        prjQuotation.setSourceDocumentId(chance_id);
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(prjQuotation);
        HlsCusPrjQuotation quotation = null;
        if(quotations.size()>0){
            quotation = quotations.get(0);
        }
        //附件信息
        HlsCreditLineAttach hlsCreditLineAttach = new HlsCreditLineAttach();
        hlsCreditLineAttach.setChanceId(chance_id);
        List<HlsCreditLineAttach> attachments = hlsCreditLineAttachMapper.queryCredAttachment(hlsCreditLineAttach);
        JSONObject json = new JSONObject();
        json.put("basicInfo", basic);
        json.put("quotation", quotation);
        json.put("attachments", attachments);
        return json;
    }

    @Override
    public JSONObject fctProjectreviewWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long project_id = Long.valueOf(businessKey);
        //基本信息
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(project_id);
        List<Map> basics = hlsCusPrjProjectMapper.queryPrjDetailSecond(prjProject);
        Map basic = basics.size()>0?basics.get(0):null;

        //报价信息
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        prjQuotation.setSourceDocumentId(project_id);
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(prjQuotation);
        HlsCusPrjQuotation quotation = quotations.size()>0?quotations.get(0):null;
        //附件信息
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
        hlsCusPrjProjectAttachment.setProjectId(project_id);
        List<Map> attachments = hlsCusPrjProjectAttachmentMapper.selectPrjProjectAttachmentInfo(hlsCusPrjProjectAttachment);
        JSONObject json = new JSONObject();
        json.put("basicInfo", basic);
        json.put("quotation", quotation);
        json.put("attachments", attachments);
        return json;
    }

    @Override
    public JSONObject customerRateWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long score_id = Long.valueOf(businessKey);
        //基本信息
        HlsScoreCalculation hlsScoreCalculation = new HlsScoreCalculation();
        hlsScoreCalculation.setScoreId(score_id);
        List<HlsScoreCalculation> basics = hlsScoreCalculationMapper.selectScoreCalculation(hlsScoreCalculation);
        HlsScoreCalculation basic = basics.size()>0?basics.get(0):null;

        JSONObject json = new JSONObject();
        json.put("basicInfo", basic);
        return json;
    }

    @Override
    public JSONObject conContractCreateWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long projectId = Long.valueOf(businessKey);
        //基本信息
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<Map> basics = hlsCusPrjProjectMapper.queryPrjDetailNew(hlsCusPrjProject);
        Map basic = basics.size()>0?basics.get(0):null;
        //报价
        Long quotationId = Long.valueOf(basic != null?basic.get("quotation_id").toString():"0");
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation.setDataClass("VIRTUAL_CON");
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(prjQuotation);
        HlsCusPrjQuotation quotation = quotations.size()>0?quotations.get(0):null;
        //合同文本
        HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
        hlsCusFctProjectAttachment.setProjectId(projectId);
        List<HlsCusFctProjectAttachment> contents = hlsCusFctProjectAttachmentMapper.queryContentFileInfo2(hlsCusFctProjectAttachment);
        //资料清单
        HlsCusPrjProjectAttachment t = new HlsCusPrjProjectAttachment();
        t.setProjectId(projectId);
        List<HlsCusPrjProjectAttachment> attachments = hlsCusPrjProjectAttachmentMapper.selectContractAttachmentInfo(t);
        JSONObject json = new JSONObject();
        json.put("basicInfo", basic);
        json.put("quotation", quotation);
        json.put("contents", contents);
        json.put("attachments", attachments);
        return json;
    }

    @Override
    public JSONObject conContractSignWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long projectId = Long.valueOf(businessKey);
        //基本信息
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<Map> basics = hlsCusPrjProjectMapper.queryPrjDetailNew(hlsCusPrjProject);
        Map basic = basics.size()>0?basics.get(0):null;
        //报价
        Long quotationId = Long.valueOf(basic != null?basic.get("quotation_id").toString():"0");
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation.setDataClass("VIRTUAL_CON");
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(prjQuotation);
        HlsCusPrjQuotation quotation = quotations.size()>0?quotations.get(0):null;
        //合同文本
        HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
        hlsCusFctProjectAttachment.setProjectId(projectId);
        List<HlsCusFctProjectAttachment> contents = hlsCusFctProjectAttachmentMapper.queryContentFileInfo2(hlsCusFctProjectAttachment);


        JSONObject json = new JSONObject();
        json.put("basicInfo", basic);
        json.put("quotation", quotation);
        json.put("contents", contents);
        return json;
    }

    @Override
    public JSONObject conPaymentWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long paymentReqId = Long.valueOf(businessKey);
        //基本信息
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(paymentReqId);
        List<HlsCusCshPaymentReqHd> basics = hlsCusCshPaymentReqHdMapper.conContractCshReqDetail(hlsCusCshPaymentReqHd);
        HlsCusCshPaymentReqHd basic = basics.size()>0?basics.get(0):null;
        //放款信息
        HlsCusCshPaymentReqLn lnPara= new HlsCusCshPaymentReqLn();
        lnPara.setPaymentReqId(paymentReqId);
        List<HlsCusCshPaymentReqLn> paymentReqLns = hlsCusCshPaymentReqLnMapper.selectPaymentLnNew(lnPara);
        //付款前题条件
        ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
        projectCreditCondition.setProjectId(paymentReqId);
        List<ProjectCreditCondition> conditions = projectCreditConditionMapper.queryByPrj(projectCreditCondition);
        //附件
        CshPaymentAttachment cshPaymentAttachment = new CshPaymentAttachment();
        cshPaymentAttachment.setPaymentReqId(paymentReqId);
        List<CshPaymentAttachment> attachments = cshPaymentAttachmentMapper.selectPaymentAttachmentInfo(cshPaymentAttachment);
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("paymentReqLns", paymentReqLns);
        json.put("conditions", conditions);
        json.put("attachments", attachments);
        return json;
    }

    @Override
    public JSONObject cshPaymentWfl(Long workProcessId){
        String businessKey = userAppMapper.selectByProcessId(workProcessId);
        Long paymentReqId = Long.valueOf(businessKey);
        //基本信息
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(paymentReqId);
        List<HlsCusCshPaymentReqHd> basics = hlsCusCshPaymentReqHdMapper.conContractCshReqDetail(hlsCusCshPaymentReqHd);
        HlsCusCshPaymentReqHd basic = basics.size()>0?basics.get(0):null;

        //放款信息
        HlsCusCshPaymentReqLn lnPara= new HlsCusCshPaymentReqLn();
        lnPara.setPaymentReqId(paymentReqId);
        List<HlsCusCshPaymentReqLn> paymentReqLns = hlsCusCshPaymentReqLnMapper.selectPaymentLnNew(lnPara);

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("paymentReqLns", paymentReqLns);
        return json;
    }

    @Override
    public JSONObject cshTransationRefundPayWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long refundId = Long.valueOf(businessKey);
        //基本信息
        HlsCusCshTransactionRefund cshTransactionRefund = new HlsCusCshTransactionRefund();
        cshTransactionRefund.setRefundId(refundId);
        List<HlsCusCshTransactionRefund> basics = cshTransactionRefundMapper.cshTransactionRefundQuery(cshTransactionRefund);
        HlsCusCshTransactionRefund basic = basics.size()>0?basics.get(0):null;

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        return json;
    }

    @Override
    public JSONObject cshTransationRefundWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long refundId = Long.valueOf(businessKey);
        //基本信息
        HlsCusCshTransactionRefund cshTransactionRefund = new HlsCusCshTransactionRefund();
        cshTransactionRefund.setRefundId(refundId);
        List<HlsCusCshTransactionRefund> basics = cshTransactionRefundMapper.cshTransactionRefundQuery(cshTransactionRefund);
        HlsCusCshTransactionRefund basic = basics.size()>0?basics.get(0):null;

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        return json;
    }

    @Override
    public JSONObject tariffPaymentWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long paymentReqId = Long.valueOf(businessKey);
        //基本信息
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(paymentReqId);
        List<Map> basics = hlsCusCshPaymentReqHdMapper.queryTariffCshPaymentReqHd(hlsCusCshPaymentReqHd);
        Map basic = basics.size()>0?basics.get(0):null;
        //应付关税明细
        Map map = new HashMap();
        map.put("paymentReqId",paymentReqId);
        List<HlsCusConContractCashflow> cashflows = hlsCusConContractCashflowMapper.queryTariffCshPaymentCashflow(map);
        //投放行表
        /*HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
        hlsCusCshPaymentReqLn.setPaymentReqId(paymentReqId);
        List<HlsCusCshPaymentReqLn> paymentReqLns = hlsCusCshPaymentReqLnMapper.selectPaymentLnNew(hlsCusCshPaymentReqLn);*/
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("cashflows", cashflows);
        return json;
    }

    @Override
    public JSONObject conContractCashconform(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long paymentConfirmId = Long.valueOf(businessKey);
        //基本信息
        Map map = new HashMap();
        map.put("paymentConfirmId",paymentConfirmId);
        List<Map> basics = hlsCusConContractMapper.conContractRentPaymentConfirmHome(map);
        Map basic = basics.size()>0?basics.get(0):null;
        //报价
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setPaymentConfirmId(paymentConfirmId);
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(prjQuotation);
        HlsCusPrjQuotation quotation = quotations.size()>0?quotations.get(0):null;
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("quotation", quotation);
        return json;
    }

    @Override
    public JSONObject durationWflSpecial(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long hdId = Long.valueOf(businessKey);
        //基本信息
        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        List<HlsDurationHd> basics = hlsDurationHdMapper.hlsDurationHdDetailQueryNew(hd);
        HlsDurationHd basic = basics.size()>0?basics.get(0):null;
        //附件
        HlsDurationAttachment hlsDurationAttachment = new HlsDurationAttachment();
        hlsDurationAttachment.setHdId(hdId);
        hlsDurationAttachment.setAttachmentCategory("BUSINESS_CHANGE");
        List<HlsDurationAttachment> attachments = hlsDurationAttachmentMapper.selectDurationAttachmentInfo(hlsDurationAttachment);
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("attachments", attachments);
        return json;
    }
    @Override
    public JSONObject contractChangeWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long hdId = Long.valueOf(businessKey);
        //基本信息
        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        List<HlsDurationHd> basics = hlsDurationHdMapper.hlsDurationHdDetailQueryReqNew(hd);
        HlsDurationHd basic = basics.size()>0?basics.get(0):null;
        //附件
        HlsDurationAttachment hlsDurationAttachment = new HlsDurationAttachment();
        hlsDurationAttachment.setHdId(hdId);
        hlsDurationAttachment.setAttachmentCategory("FINANCIAL_TERMS");
        List<HlsDurationAttachment> attachments = hlsDurationAttachmentMapper.selectDurationAttachmentInfo(hlsDurationAttachment);

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("attachments", attachments);
        return json;
    }

    @Override
    public JSONObject durationWflEt(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long hdId = Long.valueOf(businessKey);
        //基本信息
        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        List<HlsDurationHd> basics = hlsDurationHdMapper.hlsDurationHdEtDetailQueryNew(hd);
        HlsDurationHd basic = basics.size()>0?basics.get(0):null;
        //现金流
        HlsDurationLn ln = new HlsDurationLn();
        ln.setHdId(hdId);
        List<HlsDurationLn> hlsDurationLns = hlsDurationLnMapper.hlsDurationLnEtDetailQueryNew(ln);
        //附件
        HlsDurationAttachment hlsDurationAttachment = new HlsDurationAttachment();
        hlsDurationAttachment.setHdId(hdId);
        hlsDurationAttachment.setAttachmentCategory("ET");
        List<HlsDurationAttachment> attachments = hlsDurationAttachmentMapper.selectDurationAttachmentInfo(hlsDurationAttachment);
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("hlsDurationLns", hlsDurationLns);
        json.put("attachments", attachments);
        return json;
    }
    @Override
    public JSONObject durationWflTerminate(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long hdId = Long.valueOf(businessKey);
        //基本信息
        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        List<HlsDurationHd> basics = hlsDurationHdMapper.hlsDurationHdDetailQueryNew(hd);
        HlsDurationHd basic = basics.size()>0?basics.get(0):null;
        //附件
        HlsDurationAttachment hlsDurationAttachment = new HlsDurationAttachment();
        hlsDurationAttachment.setHdId(hdId);
        hlsDurationAttachment.setAttachmentCategory("CON_TERMINATION");
        List<HlsDurationAttachment> attachments = hlsDurationAttachmentMapper.selectDurationAttachmentInfo(hlsDurationAttachment);
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("attachments", attachments);
        return json;
    }
    @Override
    public JSONObject conContractEndWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long projectId = Long.valueOf(businessKey);
        //基本信息
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        List<Map> basics = hlsCusPrjProjectMapper.queryPrjDetailNew(hlsCusPrjProject);
        Map basic = basics.size()>0?basics.get(0):null;
        //报价
        Long quotationId = Long.valueOf(basic != null?basic.get("quotation_id").toString():"0");
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation.setDataClass("VIRTUAL_CON");
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(prjQuotation);
        HlsCusPrjQuotation quotation = quotations.size()>0?quotations.get(0):null;

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("quotation", quotation);
        return json;
    }

    @Override
    public JSONObject conContractInterestAdjustment(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long fltReqId = Long.valueOf(businessKey);

        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setFltReqId(fltReqId);
        List<HlsCusPrjQuotation> quotations = hlsCusPrjQuotationMapper.queryQuotationCalcReqWfl(quotation);
        JSONObject json = new JSONObject();
        json.put("quotations", quotations);
        return json;
    }

    public JSONObject lonContractWithdrawWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long widthdrawId = Long.valueOf(businessKey);
        //提款信息
        HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
        lonContractWithdraw.setWithdrawId(widthdrawId);
        List<HlsCusLonContractWithdraw> basics = hlsCusLonContractWithdrawMapper.lonContractWithdrawFormData(lonContractWithdraw);
        HlsCusLonContractWithdraw basic = basics.size()>0?basics.get(0):null;
        //附件
        HlsCusLonContractAttachment lonContractAttachment = new HlsCusLonContractAttachment();
        lonContractAttachment.setWithdrawId(widthdrawId);
        List<HlsCusLonContractAttachment> attachments = hlsCusLonContractAttachmentMapper.queryLonConWithdrowAttachment(lonContractAttachment);
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("attachments", attachments);
        return json;
    }

    @Override
    public JSONObject propertyEvaluateWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long assetHdId = Long.valueOf(businessKey);
        Map map = new HashMap();
        map.put("assetHdId",assetHdId);
        LeaseAssetHd basic = leaseAssetHdMapper.selectLeaseAssetHdDet(map);

        JSONObject json = new JSONObject();
        json.put("basic",basic);
        return json;
    }

    @Override
    public JSONObject assetsDisposalWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long assetsDisposalId = Long.valueOf(businessKey);
        AssetsDisposal assetsDisposal = new AssetsDisposal();
        assetsDisposal.setAssetsDisposalId(assetsDisposalId);
        List<AssetsDisposal> basics = assetsDisposalMapper.queryAssetsDisposal(assetsDisposal);
        AssetsDisposal basic = basics.size()>0?basics.get(0):null;
        //附件
        AssetsDisposalAttch assetsDisposalAttch = new AssetsDisposalAttch();
        assetsDisposalAttch.setAssetsDisposalId(assetsDisposalId);
        List<AssetsDisposalAttch> attachments = assetsDisposalAttchMapper.queryAssetsDisposalAttch(assetsDisposalAttch);
        JSONObject json = new JSONObject();
        json.put("basic",basic);
        json.put("attachments",attachments);
        return json;
    }

    @Override
    public JSONObject lawsuitsManagementWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long litigationManagementId = Long.valueOf(businessKey);
        LitigationManagement litigationManagement = new LitigationManagement();
        litigationManagement.setLitigationManagementId(litigationManagementId);
        List<LitigationManagement> basics = litigationManagementMapper.queryLitigationManagement(litigationManagement);
        LitigationManagement basic = basics.size()>0?basics.get(0):null;

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        return json;
    }
    @Override
    public JSONObject billApplicationWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long billId = Long.valueOf(businessKey);
        Map map = new HashMap();
        map.put("billId",billId);
        List<hlsBillRequest> basics = hlsBillRequestMapper1.queryHlsBillRequest(map);
        hlsBillRequest basic = basics.size()>0?basics.get(0):null;
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        return json;
    }

    @Override
    public JSONObject rentCheckWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long checkId = Long.valueOf(businessKey);
        PrjCheck prjCheck = new PrjCheck();
        prjCheck.setCheckId(checkId);
        List<PrjCheck> basics = prjCheckMapper.queryList(prjCheck);
        PrjCheck basic = basics.size()>0?basics.get(0):null;
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        return json;
    }
    @Override
    public JSONObject riskWarningWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long riskWarningId = Long.valueOf(businessKey);

        //基本信息
        HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
        riskWarning.setRiskWarningId(riskWarningId);
        List<HlsCusRiskWarning> basics = hlsCusRiskWarningMapper.queryAllNew(riskWarning);
        HlsCusRiskWarning basic = basics.size()>0?basics.get(0):null;
        //附件
        RiskAttachment riskAttachment = new RiskAttachment();
        riskAttachment.setRiskWarningId(riskWarningId);
        List<RiskAttachment> attachments = riskAttachmentMapper.queryCredAttachment(riskAttachment);
        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("attachments", attachments);
        return json;
    }
    @Override
    public JSONObject leaseInspectWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long prj_lease_id = Long.valueOf(businessKey);
        PrjLeaseInspect prjLeaseInspect = new PrjLeaseInspect();
        prjLeaseInspect.setPrjLeaseId(prj_lease_id);
        List<PrjLeaseInspect> basics = prjLeaseInspectMapper.queryList(prjLeaseInspect);
        PrjLeaseInspect basic = basics.size()>0?basics.get(0):null;
        JSONObject json = new JSONObject();
        json.put("basic",basic);
        return json;
    }
    @Override
    public JSONObject assetsClassificationWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long fc_estimate_id = Long.valueOf(businessKey);
        AstFcEstimate astFcEstimate = new AstFcEstimate();
        astFcEstimate.setFcEstimateId(fc_estimate_id);
        List<AstFcEstimate> basics = astFcEstimateMapper.queryAstFcEstimate(astFcEstimate);
        AstFcEstimate basic = basics.size()>0?basics.get(0):null;
        JSONObject json = new JSONObject();
        json.put("basic",basic);
        return json;
    }
    @Override
    public JSONObject provisionShallBeMadeWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long fc_estimate_id = Long.valueOf(businessKey);
        AstFcEstimate astFcEstimate = new AstFcEstimate();
        astFcEstimate.setFcEstimateId(fc_estimate_id);
        List<AstFcEstimate> basics = astFcEstimateMapper.queryAstFcEstimate(astFcEstimate);
        AstFcEstimate basic = basics.size()>0?basics.get(0):null;

        JSONObject json = new JSONObject();
        json.put("basic",basic);
        return json;
    }

    @Override
    public JSONObject generalMattersWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long general_id = Long.valueOf(businessKey);
        HlsGeneralIssue hlsgeneralissue = new HlsGeneralIssue();
        hlsgeneralissue.setGeneralId(general_id);
        List<HlsGeneralIssue> basics = hlsGeneralIssueMapper.queryHlsGeneralIssueNewWfl(hlsgeneralissue);
        HlsGeneralIssue basic = basics.size()>0?basics.get(0):null;
        JSONObject json = new JSONObject();
        json.put("basic",basic);
        return json;
    }
    @Override
    public JSONObject transferApplicationWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long transfer_application_id = Long.valueOf(businessKey);
        JcTransferApplication jcTransferApplication = new JcTransferApplication();
        jcTransferApplication.setTransferApplicationId(transfer_application_id);
        List<JcTransferApplication> basics = jcTransferApplicationMapper.queryJcTransferApplication(jcTransferApplication);
        JcTransferApplication basic = basics.size()>0?basics.get(0):null;
        //转账详情
        JcTransferApplicationDetal jcTransferApplicationDetal = new JcTransferApplicationDetal();
        jcTransferApplicationDetal.setTransferApplicationId(transfer_application_id);
        List<JcTransferApplicationDetal> transferDetails = jcTransferApplicationDetalMapper.queryJcTransferApplicationDetal(jcTransferApplicationDetal);
        JSONObject json = new JSONObject();
        json.put("basic",basic);
        json.put("transferDetails",transferDetails);
        return json;
    }

    @Override
    public JSONObject fctProjectChangeWfl(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long hdId = Long.valueOf(businessKey);
        //基本信息
        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        List<HlsDurationHd> basics = hlsDurationHdMapper.prjProjectApprovalConQuery(hd);
        HlsDurationHd basic = basics.size()>0?basics.get(0):null;
        //附件
        HlsDurationAttachment hlsDurationAttachment = new HlsDurationAttachment();
        hlsDurationAttachment.setHdId(hdId);
        hlsDurationAttachment.setAttachmentCategory("REPLY");
        List<HlsDurationAttachment> attachments = hlsDurationAttachmentMapper.selectDurationAttachmentInfo(hlsDurationAttachment);

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("attachments", attachments);
        return json;
    }

    @Override
    public JSONObject fundingPlanWflNew(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long fillingId = Long.valueOf(businessKey);
        //基本信息
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling.setFillingId(fillingId);
        List<JcFundFilling> basics = jcFundFillingMapper.queryAllByUnit(fundFilling);
        JcFundFilling basic = basics.size()>0?basics.get(0):null;
        //资金计划填报
        JcFundFillingLn fundFillingLn = new JcFundFillingLn();
        fundFillingLn.setFillingId(fillingId);
        List<JcFundFillingLn> contents = jcFundFillingLnMapper.queryAllNew(fundFillingLn);

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("contents", contents);
        return json;
    }

    @Override
    public JSONObject fundingPlanWflNewZj(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long fillingId = Long.valueOf(businessKey);
        //基本信息
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling.setFillingId(fillingId);
        List<JcFundFilling> basics = jcFundFillingMapper.queryAllNew(fundFilling);
        JcFundFilling basic = basics.size()>0?basics.get(0):null;
        //资金计划汇总
        JcFundFillingLn fundFillingLn = new JcFundFillingLn();
        fundFillingLn.setFillingId(fillingId);
        List<JcFundFillingLn> contents = jcFundFillingLnMapper.queryAllNew(fundFillingLn);

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("contents", contents);
        return json;
    }

    @Override
    public JSONObject durationWflBond(Long processInstanceId){
        String businessKey = userAppMapper.selectByProcessId(processInstanceId);
        Long hdId = Long.valueOf(businessKey);
        //基本信息
        HlsDurationHd durationHd = new HlsDurationHd();
        durationHd.setHdId(hdId);
        List<HlsDurationHd> basics = hlsDurationHdMapper.hlsDurationHdDetailQueryNew1(durationHd);
        HlsDurationHd basic = basics.size()>0?basics.get(0):null;
        //保证金冲抵、退还方案
        HlsDurationDeposit durationDeposit = new HlsDurationDeposit();
        durationDeposit.setHdId(hdId);
        List<HlsDurationDeposit> deposits = hlsDurationDepositMapper.hlsDurationDepositDetailQuery(durationDeposit);
        HlsDurationDeposit deposit = deposits.size()>0?deposits.get(0):null;
        //保证金冲抵、退还方案、保证金抵扣债权
        HlsDurationLn hlsDurationLn = new HlsDurationLn();
        hlsDurationLn.setHdId(hdId);
        List<HlsDurationLn> contents = hlsDurationLnMapper.hlsDurationLnDepositQuery(hlsDurationLn);
        List<HlsDurationLn> details = hlsDurationLnMapper.hlsDurationLnDetailQuery(hlsDurationLn);

        JSONObject json = new JSONObject();
        json.put("basic", basic);
        json.put("deposit", deposit);
        json.put("contents", contents);
        json.put("details", details);
        return json;
    }

    @Override
    public ResponseData queryProcessInstances(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();

        try {
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, QUERY_PROCESS_INSTANCES);
            if (!responseData.isSuccess()) {
                return responseData;
            }

            CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest = jsonObject.toJavaObject(CustomHistoricProcessInstanceQueryRequest.class);
            if (jsonObject.get("startDate") != null && !"".equals(jsonObject.get("startDate"))) {
                historicProcessInstanceQueryRequest.setStartedAfter(jsonObject.getDate("startDate"));
            }
            if (jsonObject.get("endDate") != null && !"".equals(jsonObject.get("endDate"))) {
                historicProcessInstanceQueryRequest.setStartedBefore(jsonObject.getDate("endDate"));
            }
            Long allocationId = jsonObject.getLong("allocationId");

            if (iRequest.getUserId() < 0) {
                throw new WflSecurityException(WflSecurityException.USER_NOT_RELATE_EMP);
            }

            //管理员可以查看全部，否则只能查看自己的
            User user = userService.selectByUserName(jsonObject.getString("userName"));
            List<String> codeList = user.getRoleCode();
            Boolean ADMINFlag = false;
            for (String item : codeList) {
                if ("ADMIN".equalsIgnoreCase(item)) {
                    ADMINFlag = true;
                }
            }
            if (!ADMINFlag) {
                historicProcessInstanceQueryRequest.setInvolvedUser(allocationId.toString());
            }

            //状态查询
            String status = jsonObject.getString("processInstanceStatus");
            if (status != null && !"".equals(status)) {
                if ("已结束".equals(status)) {
                    historicProcessInstanceQueryRequest.setFinished(true);
                } else {
                    historicProcessInstanceQueryRequest.setFinished(false);
                }
            }

            if (jsonObject.getString("involved") != null) {
                if (jsonObject.getBooleanValue("involved")) {
                    historicProcessInstanceQueryRequest.setStartedBy(allocationId.toString());
                } else {
                    historicProcessInstanceQueryRequest.setInvolvedUser(allocationId.toString());
                }
            }

            if (jsonObject.getString("startedBy")!=null && !jsonObject.getString("startedBy").isEmpty()) {
                FndEmployee employee = new FndEmployee();
                employee.setName(jsonObject.getString("startedBy"));
                List<FndEmployee> fndEmployees = fndEmployeeMapper.queryEmployeesForLov(employee);
                if(fndEmployees.size()>0){
                    historicProcessInstanceQueryRequest.setStartedBy(fndEmployees.get(0).getAllocationId().toString());
                }
            }

            if (jsonObject.get("documentName") != null && !"".equals(jsonObject.get("documentName"))) {
                String documentName = jsonObject.get("documentName").toString();
                historicProcessInstanceQueryRequest.setDocumentName(documentName);

            }
            if (jsonObject.get("wflName") != null && jsonObject.get("wflName") != null && !"".equals(jsonObject.get("wflName"))) {
                String wflName = jsonObject.get("wflName").toString();
                historicProcessInstanceQueryRequest.setProcessDefinitionNameLike(wflName);
            }
            if (jsonObject.get("wflStart") != null && !"".equals(jsonObject.get("wflStart"))) {
                String wflStart = jsonObject.get("wflStart").toString();
                if ("已结束".equals(wflStart)) {
                    historicProcessInstanceQueryRequest.setFinished(true);
                } else if ("挂起中".equals(wflStart)) {
                    historicProcessInstanceQueryRequest.setSuspended(true);
                } else {
                    historicProcessInstanceQueryRequest.setSuspended(false);
                    historicProcessInstanceQueryRequest.setFinished(false);
                }
            }

            int page;
            int pageSize;
            if (!jsonObject.getString("page").isEmpty() || jsonObject.getString("page") != null) {

                page = Integer.parseInt(jsonObject.getString("page"));
            } else {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            }
            if (!jsonObject.getString("pageSize").isEmpty() || jsonObject.getString("pageSize") != null) {

                pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
            } else {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            }


            historicProcessInstanceQueryRequest.setStart((page - 1) * pageSize);
            historicProcessInstanceQueryRequest.setSize(pageSize);


            historicProcessInstanceQueryRequest.setSort("startTime");
            historicProcessInstanceQueryRequest.setOrder("desc");
            Map<String, String> requestParams = new HashMap<String, String>(16);
            DataResponse dataResponse = activitiService.queryProcessInstances(iRequest, historicProcessInstanceQueryRequest, requestParams, true);

            /*if (condition_field(jsonObject) && dataResponse.getData() != null) {
                // 页面 查询条件
                responseData.setRows(filterReturnList(dataResponse, jsonObject));
            } else {
                responseData.setRows((List<?>) dataResponse.getData());
            }*/
            responseData.setRows((List<?>) dataResponse.getData());

            responseData.setTotal(dataResponse.getTotal());
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);


        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    /**
     * 根据查询条件 进行筛选
     */
    private List<?> filterReturnList(DataResponse dataResponse, JSONObject jsonObject) {
        Object data = dataResponse.getData();
        List<HistoricProcessInstanceResponseExt> historyList = JSON.parseArray(JSON.toJSONString(data), HistoricProcessInstanceResponseExt.class);
        List<HistoricProcessInstanceResponseExt> returnList = new ArrayList<>();
        for (HistoricProcessInstanceResponseExt ext : historyList) {

            if (jsonObject.get("documentName") != null && ext.getDocumentName() != null && !"".equals(jsonObject.get("documentName"))) {
                String documentName = jsonObject.get("documentName").toString();
                if (!ext.getDocumentName().contains(documentName)) {
                    continue;
                }
            }
            if (jsonObject.get("wflName") != null && jsonObject.get("wflName") != null && !"".equals(jsonObject.get("wflName"))) {
                String wflName = jsonObject.get("wflName").toString();
                if (!ext.getProcessName().contains(wflName)) {
                    continue;
                }
            }
            if (jsonObject.get("wflStart") != null && !"".equals(jsonObject.get("wflStart"))) {
                if ("已结束".equals(jsonObject.getString("wflStart"))) {
                    if (ext.getEndTime() == null) {
                        continue;
                    }
                } else if ("挂起".equals(jsonObject.getString("wflStart"))) {
                    if (ext.getEndTime() != null) {
                        continue;
                    }
                    if (!ext.isSuspended()) {
                        continue;
                    }
                } else if ("运行中".equals(jsonObject.getString("wflStart"))) {
                    if (ext.getEndTime() != null) {
                        continue;
                    }
                    if (ext.isSuspended()) {
                        continue;
                    }
                }
            }
            returnList.add(ext);
        }
        return returnList;

    }

    /**
     * 如果有查询条件 ，返回true
     */
    private boolean condition_field(JSONObject jsonObject) {

        if (jsonObject.get("documentName") != null || jsonObject.get("wflName") != null || jsonObject.get("wflStart") != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ResponseData taskDetails(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();

        try {
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, TASK_DETAILS);
            if (!responseData.isSuccess()) {
                return responseData;
            }
            String taskId = jsonObject.getString("taskId");
            String adminFlag = jsonObject.getString("adminFlag");

            adminFlag = adminFlag.toUpperCase();
            List<TaskResponseExt> resultList = new ArrayList<TaskResponseExt>(16);
            if (adminFlag.equals(AppConstantUtils.PublicConstans.DEFAULT_TRUE)) {
                resultList.add(activitiService.getTaskDetails(iRequest, taskId));
            } else {
                resultList.add(activitiService.getTaskDetails(iRequest, taskId, true));
            }
            responseData.setSuccess(true);
            responseData.setRows(resultList);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData executeTaskAction(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();
        TaskActionRequestExt taskActionRequestExt = jsonObject.toJavaObject(TaskActionRequestExt.class);

        try {
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, EXECUTE_TASK_ACTION);
            if (!responseData.isSuccess()) {
                return responseData;
            }


            String taskId = jsonObject.getString("taskId");
            Long allocationId = jsonObject.getLong("allocationId");
            iRequest.setAttribute("allocationId", allocationId);
            Long companyId = jsonObject.getLong("companyId");
            iRequest.setCompanyId(companyId);
            String adminFlag = jsonObject.getString("adminFlag").toUpperCase();
            JSONArray variables = jsonObject.getJSONArray("variables");
            if (variables.size() < 3) {
                return new ResponseData(false, "缺少必输参数 variables!");
            }
            if (AppConstantUtils.PublicConstans.DEFAULT_FALSE.equals(adminFlag)) {
                activitiService.executeTaskAction(iRequest, taskId, taskActionRequestExt, false);
            } else {
                activitiService.executeTaskAction(iRequest, taskId, taskActionRequestExt, true);
            }
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData executeTaskForward(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();


        try {
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, EXECUTE_TASK_FORWARD);
            if (!responseData.isSuccess()) {
                return responseData;
            }
//            JSONArray param = (JSONArray) jsonObject.get("parameter");
//            JSONObject beans = (JSONObject) param.get(0);

            // Long allocationId = beans.getLong("assign");

            Long allocationId = jsonObject.getLong("allocationId");
            String processInstanceId = jsonObject.getString("processInstanceId");
            TaskActionRequestExt actionRequest = new TaskActionRequestExt();
            actionRequest.setCurrentTaskId(jsonObject.getString("taskId"));
            actionRequest.setAssignee(String.valueOf(allocationId));
            actionRequest.setAction(jsonObject.getString("action"));
            actionRequest.setComment("");
            activitiService.executeTaskByAdmin(iRequest, processInstanceId, actionRequest);

            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData userTaskQusery(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();
        TaskActionRequestExt taskActionRequestExt = jsonObject.toJavaObject(TaskActionRequestExt.class);

        try {
            if (jsonObject.getString("processInstanceId").isEmpty() || jsonObject.getString("processInstanceId") == null) {
                return new ResponseData(false, "缺少必输参数 processInstanceId!");
            }

            responseData.setRows(activitiService.getProcessNodes(iRequest, jsonObject.getString("processInstanceId")));

            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData prcJump(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();
        try {
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, PRC_JUMP);
            if (!responseData.isSuccess()) {
                return responseData;
            }

            iRequest.setAttribute("allocationId", jsonObject.getLong("allocationId"));
            activitiService.jumpActivitiTo(iRequest, jsonObject.getString("processInstanceId"), jsonObject.getString("jumpTarget"), jsonObject.getString("jumpTargetName"));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData queryEmployees(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();
        try {
            FndEmployee employee = jsonObject.toJavaObject(FndEmployee.class);

            responseData.setRows(fndEmployeeMapper.queryEmployeesForLov(employee));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData queryAppVersion(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            HlsCusVersion hlsCusVersion = jsonObject.toJavaObject(HlsCusVersion.class);

            responseData.setRows(hlsCusVersionService.selectVersion(hlsCusVersion));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData updateAppVersion(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            HlsCusVersion hlsCusVersion = jsonObject.toJavaObject(HlsCusVersion.class);
            hlsCusVersionService.updateVersion(hlsCusVersion);

            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData queryUserAllNotice(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            // 跟随pc --  查询当前用户所有notice
            //  jsonObject.put("noticeType","NOTICE");
//            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, QUERY_USER_ALL_NOTICE);
//            if (!responseData.isSuccess()) {
//                return responseData;
//            }
            Long allocation_id = jsonObject.getLongValue("allocationId");
            HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
            // hlsSystemNotice.setNoticeType(jsonObject.getString("noticeType"));

            int page;
            int pageSize;
            if (jsonObject.getString("page") == null || jsonObject.getIntValue("page") == 0) {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            } else {
                page = jsonObject.getIntValue("page");
            }
            if (jsonObject.getString("pageSize") == null) {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            } else {
                pageSize = jsonObject.getIntValue("pageSize");
            }

            PageHelper.startPage(page, pageSize);

            responseData.setRows(hlsSystemNoticeMapper.selectUserAllNotice(allocation_id, hlsSystemNotice));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData queryUnReadNotice(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            // 跟随pc --  查询当前用户所有notice
            //  jsonObject.put("noticeType","NOTICE");
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, QUERY_USER_ALL_NOTICE);
            if (!responseData.isSuccess()) {
                return responseData;
            }
            Long allocation_id = jsonObject.getLongValue("allocationId");
            HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
            // hlsSystemNotice.setNoticeType(jsonObject.getString("noticeType"));
            int page;
            int pageSize;
            if (jsonObject.getString("page") == null || jsonObject.getIntValue("page") == 0) {
                page = AppConstantUtils.PublicConstans.DEFAULT_PAGE;
            } else {
                page = jsonObject.getIntValue("page");
            }
            if (jsonObject.getString("pageSize") == null) {
                pageSize = AppConstantUtils.PublicConstans.DEFAULT_PAGE_SIZE;
            } else {
                pageSize = jsonObject.getIntValue("pageSize");
            }

            PageHelper.startPage(page, pageSize);
            responseData.setRows(hlsSystemNoticeMapper.selectUserUnNotice(allocation_id, hlsSystemNotice));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData deleteNotice(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();
        try {
            // 跟随pc --  查询当前用户所有notice
            //  jsonObject.put("noticeType","NOTICE");
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, QUERY_USER_ALL_NOTICE_ID);
            if (!responseData.isSuccess()) {
                return responseData;
            }
            Long noticeId = 0L;
            String[] noticeIdStr = jsonObject.getString("noticeId").split(",");
            if (noticeId != null) {
                for (int i = 0; i < noticeIdStr.length; i++) {
                    noticeId = Long.valueOf(noticeIdStr[i]);
                    HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
                    hlsSystemNotice.setNoticeId(noticeId);
                    hlsSystemNoticeMapper.delete(hlsSystemNotice);
                }
                responseData.setSuccess(true);
                responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
            } else {
                responseData.setSuccess(false);
                responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData setRead(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {

            HlsSystemNotice dto = jsonObject.toJavaObject(HlsSystemNotice.class);
            hsnService.setRead(iRequest, dto);
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData insertDesignated(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            HlsCusSysAppointApprover hlsCusSysAppointApprover = jsonObject.toJavaObject(HlsCusSysAppointApprover.class);

            hlsCusSysAppointApproverService.insert(iRequest, hlsCusSysAppointApprover);
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData queryDesignated(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            HlsCusSysAppointApprover hlsCusSysAppointApprover = jsonObject.toJavaObject(HlsCusSysAppointApprover.class);

            responseData.setRows(hlsCusSysAppointApproverMapper.selectSysAppointApproverDetail(hlsCusSysAppointApprover));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData deleteDesignated(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            HlsCusSysAppointApprover hlsCusSysAppointApprover = jsonObject.toJavaObject(HlsCusSysAppointApprover.class);
            hlsCusSysAppointApproverMapper.delete(hlsCusSysAppointApprover);

            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData saveDesignated(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {
            responseData = AppCheckRequiredUtils.checkRequired(jsonObject, SAVE_DESIGNATED);
            if (!responseData.isSuccess()) {
                return responseData;
            }

            HlsCusSysAppointApprover hlsCusSysAppointApprover = jsonObject.toJavaObject(HlsCusSysAppointApprover.class);
            List<HlsCusSysAppointApprover> appointList = hlsCusSysAppointApproverMapper.selectSysAppointApproverDetail(hlsCusSysAppointApprover);
            // 有了指派人就删除旧的 然后新增
            if (appointList.size() > 0) {
                for (HlsCusSysAppointApprover appoint : appointList) {
                    hlsCusSysAppointApproverMapper.delete(appoint);
                }
            }
            hlsCusSysAppointApproverService.insert(iRequest, hlsCusSysAppointApprover);

            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }

    @Override
    public ResponseData queryUserRole(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {

            if (jsonObject.getLong("userId") == null) {
                return new ResponseData(false, "缺少必输参数 userId!");
            }

            List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.editQuery(jsonObject.getLong("userId"), jsonObject.getLong("allocationId"));
            responseData.setRows(sysUserAllocations);
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }


    @Override
    public ResponseData queryApprovedInfo(IRequest iRequest, JSONObject jsonObject) {

        ResponseData responseData = new ResponseData();
        try {

            if (jsonObject.getLong("processInstanceId") == null) {
                return new ResponseData(false, "缺少必输参数 processInstanceId!");
            }


            responseData.setRows(Arrays.asList(activitiService.getInstanceDetail(iRequest, jsonObject.getString("processInstanceId"))));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;

    }


}
