package com.hand.hls.rw.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.mapper.AttachmentMapper;
import com.hand.hap.attachment.mapper.SysFileMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReq;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;
import com.hand.hls.csh.service.IConDebtExemptionReqCfService;
import com.hand.hls.csh.service.IConDebtExemptionReqService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.plm.dto.HlsCusPlmAttachment;
import com.hand.hls.plm.dto.PlmRiskFeedback;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationContractService;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.plm.mapper.HlsCusPlmAttachmentMapper;
import com.hand.hls.plm.mapper.PlmRiskFeedbackMapper;
import com.hand.hls.plm.service.HlsCusPlmIAttachmentService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;
import com.hand.hls.rw.mapper.HlsCusRiskWarningInfoMapper;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import com.hand.hls.rw.service.HlsCusIRiskWarningInfoService;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.ty.dto.JcTianyanchaInterfaceInfo;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusRiskWarningServiceImpl extends BaseServiceImpl<HlsCusRiskWarning> implements HlsCusIRiskWarningService {

    @Autowired
    private HlsCusRiskWarningMapper mapper;

    @Autowired
    private HlsCusIFiveClassificationService fiveClassificationService;

    @Autowired
    private HlsCusFiveClassificationMapper fiveClassificationMapper;

    @Autowired
    private HlsCusIRiskWarningInfoService riskWarningInfoService;

    @Autowired
    private HlsCusRiskWarningInfoMapper riskWarningInfoMapper;

    @Autowired
    private HlsCusIFiveClassificationContractService fiveClassificationContractService;

    @Autowired
    private HlsCusFiveClassificationContractMapper fiveClassificationContractMapper;

    @Autowired
    private HlsCusPlmAttachmentMapper attachmentMapper;

    @Autowired
    private HlsCusPlmIAttachmentService attachmentService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private HlsCusEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private PlmRiskFeedbackMapper plmRiskFeedbackMapper;
    @Autowired
    private AttachmentMapper sysAttachmentMapper;
    @Autowired
    private SysFileMapper sysFileMapper;
    @Autowired
    HlsCusRiskWarningMapper hlsCusRiskWarningMapper;
    @Autowired
    private HlsCusIRiskWarningService hlsCusIRiskWarningService;

    @Autowired
    private IConDebtExemptionReqService iConDebtExemptionReqService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private IConDebtExemptionReqCfService iConDebtExemptionReqCfService;

    /**
     * @Description:风险预警保存
     * @Author: Wty
     * @Date: Created om 20:28 2018/5/31
     */
    @Override
    public HlsCusRiskWarning submit(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        HlsCusRiskWarning returnRiskWarning;
        if (riskWarning.getRiskWarningId() == null || riskWarning.getRiskWarningId() == 0) {
            //保存风险预警
            riskWarning.setDocumentCategory("RISK_WARNING");
            riskWarning.setDocumentType("RISK_WARNING");
            riskWarning.setBusinessType("RISK_WARNING");
            riskWarning.setCompanyId(iRequest.getCompanyId());
            riskWarning.setStatus("NEW");
            riskWarning.setCompanyId(iRequest.getCompanyId());
            riskWarning.setCreationUserId(iRequest.getUserId());
            riskWarning.setIsReleaseWarning("N");
            riskWarning.setIsPresenceWarning("Y");
            riskWarning.setApplyDate(new Date());
            riskWarning.setDataClass("NORMAL");
            //生成单据编号
            Map<String, String> params = new HashMap<String, String>();
            riskWarning.setRiskWarningNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, riskWarning.getDocumentCategory(), riskWarning.getDocumentType(), riskWarning.getBusinessType(), params));
            HlsCusFiveClassification fiveClassification = riskWarning.getFiveClassification();
            returnRiskWarning = self().insertSelective(iRequest, riskWarning);

            //保存风险预警info
            HlsCusRiskWarningInfo riskWarningInfo = riskWarning.getRiskWarningInfo();
            if (riskWarningInfo != null) {
                riskWarningInfo.setRiskWarningId(returnRiskWarning.getRiskWarningId());
                returnRiskWarning.setRiskWarningInfo(riskWarningInfoService.submit(iRequest, riskWarningInfo));
            }
        } else {
            //更新风险预警
            returnRiskWarning = self().updateByPrimaryKeySelective(iRequest, riskWarning);

            if (returnRiskWarning.getRiskWarningInfo() != null) {
                returnRiskWarning.setRiskWarningInfo(riskWarningInfoService.submit(iRequest, riskWarning.getRiskWarningInfo()));
            }
        }
        return returnRiskWarning;
    }

    /**
     * @Description:提交工作流
     * @Author: Wty
     * @Date: Created om 11:00 2018/6/1
     */
    @Override
    public HlsCusRiskWarning submitWfl(IRequest iRequest, HlsCusRiskWarning riskWarning) throws HlsCusException {
        HlsCusRiskWarning returnRiskWarning = submit(iRequest, riskWarning);
//        databaseLockProvider.lock(returnRiskWarning);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (null!=employee)
        {
            String employeeCode = employee.getEmployeeCode();
            String name = employee.getName();
            iRequest.setEmployeeCode(employeeCode);
            iRequest.setEmployeeName(name);
        }
        else {
            throw new HlsCusException("没找到合同的项目经理，请确认！");
        }
        Long companyId = iRequest.getCompanyId();
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        List<HlsCusRiskWarning> list = new ArrayList<>();
        list.add(returnRiskWarning);
        String message;
        params.put("workFlowType", "RISK_WARNING_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "RISK_WARNING_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "RISK_WARNING_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, riskWarning.getRiskWarningId());
        params.put("riskType", riskWarning.getRiskType());
        params.put("riskWarningId", riskWarning.getRiskWarningId());
        params.put("businessKey", riskWarning.getRiskWarningId());
        params.put("documentCategory", "RISK_WARNING_WFL"); //
        params.put("documentName","风险预警审批流程"+riskWarning.getRiskWarningNumber());
        params.put("documentNumber", riskWarning.getRiskWarningNumber());
        params.put("riskWarning", JSON.toJSONString(riskWarning));
        params.put("startUserName", iRequest.getUserName());
        params.put("companyId", companyId);
        params.put("startEmpName",iRequest.getUserName());
        params.put("businessKey", riskWarning.getRiskWarningId().toString());
        message="风险预警流程，请及时关注。";

        activitiStartService.start(iRequest, list, params);
        returnRiskWarning.setStatus("APPROVING");
        returnRiskWarning.setSubmitStatus("F");

        //self().updateByPrimaryKeySelective(iRequest, returnRiskWarning);
        //mapper.updateByPrimaryKey(returnRiskWarning); //记录一个问题，版本号查询对不上，导致查不到数据 从而更新失败

        mapper.updateRiskWarningStatus(returnRiskWarning);
        return returnRiskWarning;
    }


    @Override
    public HlsCusRiskWarning submitWithoutWfl(IRequest iRequest, HlsCusRiskWarning riskWarning) throws HlsCusException {
        HlsCusRiskWarning returnRiskWarning = submit(iRequest, riskWarning);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (null!=employee)
        {
            String employeeCode = employee.getEmployeeCode();
            String name = employee.getName();
            iRequest.setEmployeeCode(employeeCode);
            iRequest.setEmployeeName(name);
        }
        else {
            throw new HlsCusException("没找到合同的项目经理，请确认！");
        }

        returnRiskWarning.setStatus("APPROVED");
        returnRiskWarning.setSubmitStatus("Y");
        mapper.updateRiskWarningStatus(returnRiskWarning);

        HlsCusRiskWarning record = new HlsCusRiskWarning();
        record.setRiskWarningId(returnRiskWarning.getRiskWarningId());
        List<HlsCusRiskWarning> hlsCusRiskWarnings = mapper.queryAllNew(record);
        record = hlsCusRiskWarnings.get(0);
        String bpIdN = record.getBpIdN();
        String createdByN = record.getCreatedByN();
        //发送通知
        //岗位：风险管理部和法务部
        Long [] posIds = {7L, 8L, 9L, 10L};
        SysUser sysUser = new SysUser();
        sysUser.setPositionIdList(posIds);
        List<SysUser> roleList = sysUserMapper.selectEemployeeUtil(sysUser);
        //更新事件方法
        Map<String,Object> evenParams = new HashMap<>(16);
        String url = "/RISK/RISK100/risk_warning_info.lview?layout_code=ASS005F2&risk_warning_id="+returnRiskWarning.getRiskWarningId()+"&maintain_type=READONLY&function_usage=QUERY&function_code=RISK100F1";
        evenParams.put("message",  createdByN + "发起了" + bpIdN  + "客户的风险预警");
        evenParams.put("noticeTitle", "风险预警通知");
        evenParams.put("url", url);
        evenParams.put("level", 1L);
        evenParams.put("noticeType", "NOTICE");
        for(int i = 0; i < roleList.size(); i++) {
            if(roleList.get(i).getUserId() == null){
                continue;
            }
            evenParams.put("eventUserId", roleList.get(i).getUserId());
            evenParams.put("allocation_id", roleList.get(i).getAllocationId());
            sysEventService.eventSave(iRequest, riskWarning.getRiskWarningId(), riskWarning.getDocumentCategory(), riskWarning.getDocumentType(), "PLM_RW", "RW_RELEASE", "P2D", evenParams);
            //this.sysEventService.eventSave(iRequest, riskWarning.getRiskWarningId(),"ABS", "ABS", "ABS", "SUB_SUBSCRIPTION", "ABS", evenParams);
        }

        return returnRiskWarning;
    }
    /**
     * @Description:风险预警解除预警
     * @Author: Wty
     * @Date: Created om 16:00 2018/6/1
     */
    @Override
    public HlsCusRiskWarning releaseWarning(IRequest iRequest, HlsCusRiskWarning riskWarning) {
//        databaseLockProvider.lock(riskWarning);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        List<HlsCusRiskWarning> list = new ArrayList<>();
        list.add(riskWarning);
        params.put("workFlowType", "PLM_RW_RELEASE_WORK_FLOW");
        activitiStartService.start(iRequest, list, params);
        riskWarning.setStatus("APPROVING");
        riskWarning = self().updateByPrimaryKeySelective(iRequest, riskWarning);

        //消息参数
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }


        String msg = userName + "解除了风险预警";
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "风险预警解除");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, riskWarning.getRiskWarningId(), riskWarning.getDocumentCategory(), riskWarning.getDocumentType(), "PLM_RW", "RW_RELEASE", "P2D", paramsEvent);

        //更新对应的五级分类
        HlsCusFiveClassification fiveClassification = new HlsCusFiveClassification();
        fiveClassification.setBelongsToId(riskWarning.getRiskWarningId());
        fiveClassification.setCompanyId(iRequest.getCompanyId());
        fiveClassification.setFiveClassificationType("RW");
        List<HlsCusFiveClassification> fcList = fiveClassificationMapper.selectFiveClassficationByBelongsToId(fiveClassification);
        if (CollectionUtils.isNotEmpty(fcList)) {
            HlsCusFiveClassification cusFiveClassification = new HlsCusFiveClassification();
            cusFiveClassification.setFiveClassificationId(fcList.get(0).getFiveClassificationId());
            cusFiveClassification.setStatus("APPROVING");
            fiveClassificationService.updateByPrimaryKeySelective(iRequest, cusFiveClassification);
        }

        return riskWarning;
    }

    /**
     * @Description:变更工作流提交
     * @Author: Wty
     * @Date: Created om 16:39 2018/6/1
     */
    @Override
    public HlsCusRiskWarning submitChangeWfl(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        HlsCusRiskWarning returnRiskWarning = submit(iRequest, riskWarning);
//        databaseLockProvider.lock(returnRiskWarning);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        List<HlsCusRiskWarning> list = new ArrayList<>();
        list.add(returnRiskWarning);
        params.put("workFlowType", "PLM_RW_CHANGE_WORK_FLOW");
        activitiStartService.start(iRequest, list, params);
        returnRiskWarning.setStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, returnRiskWarning);

        //消息参数
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "调整了风险预警";
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "风险预警调整");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, returnRiskWarning.getRiskWarningId(), returnRiskWarning.getDocumentCategory(), returnRiskWarning.getDocumentType(), "PLM_RW", "RW_CHANGE", "P2D", paramsEvent);

        //更新对应的五级分类
        HlsCusFiveClassification fiveClassification = new HlsCusFiveClassification();
        fiveClassification.setBelongsToId(returnRiskWarning.getRiskWarningId());
        fiveClassification.setCompanyId(iRequest.getCompanyId());
        fiveClassification.setFiveClassificationType("RW");
        List<HlsCusFiveClassification> fcList = fiveClassificationMapper.selectFiveClassficationByBelongsToId(fiveClassification);
        if (CollectionUtils.isNotEmpty(fcList)) {
            HlsCusFiveClassification cusFiveClassification = new HlsCusFiveClassification();
            cusFiveClassification.setFiveClassificationId(fcList.get(0).getFiveClassificationId());
            cusFiveClassification.setStatus("APPROVING");
            fiveClassificationService.updateByPrimaryKeySelective(iRequest, cusFiveClassification);
        }

        return returnRiskWarning;
    }

    /**
     * @Description:copy原来的数据并更新changeIq和changeTime
     * @Author: Wty
     * @Date: Created om 13:44 2018/6/4
     */
    @Override
    public void copyChangeData(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
        riskWarningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
        riskWarningInfo.setChangeIq("CHANGE");
        List<HlsCusRiskWarningInfo> changeRiskWarningInfoList = riskWarningInfoMapper.selectRiskWarningInfo(riskWarningInfo);

        //判断是否有CHANGE的数据，如果有就不保存，如果没有CHANGE数据则进行copy
        if (changeRiskWarningInfoList.size() < 1) {
            Long maxChangeTime = 1L;
            //查询NORMAL的风险预警信息
            riskWarningInfo.setChangeIq("NORMAL");
            //查询最大的changeTime
            List<HlsCusRiskWarningInfo> changeTimeList = riskWarningInfoMapper.selectMaxChangeTime(riskWarningInfo);
            if (CollectionUtils.isNotEmpty(changeTimeList)) {
                maxChangeTime = changeTimeList.get(0).getChangeTime() + 1;
            }
            //保存copy的riskWarningInfo
            List<HlsCusRiskWarningInfo> riskWarningInfoList = riskWarningInfoMapper.selectRiskWarningInfo(riskWarningInfo);
            if (CollectionUtils.isNotEmpty(riskWarningInfoList)) {
                riskWarningInfoList.get(0).setChangeIq("CHANGE");
                riskWarningInfoList.get(0).setRiskWarningInfoId(null);
                riskWarningInfoList.get(0).setChangeTime(maxChangeTime);
                riskWarningInfoService.insertSelective(iRequest, riskWarningInfoList.get(0));
            }
            //查询五级分类
            HlsCusFiveClassification fiveClassification = new HlsCusFiveClassification();
            fiveClassification.setFiveClassificationType("RW");
            fiveClassification.setBelongsToId(riskWarning.getRiskWarningId());
            List<HlsCusFiveClassification> fiveClassificationList = fiveClassificationMapper.selectFiveClassficationByBelongsToId(fiveClassification);
            //查询五级分类合同信息
            if (CollectionUtils.isNotEmpty(fiveClassificationList)) {
                HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
                fiveClassificationContract.setFiveClassificationId(fiveClassificationList.get(0).getFiveClassificationId());
                fiveClassificationContract.setChangeIq("NORMAL");
                fiveClassificationContract.setApprovalNode("END_NODE");
                List<HlsCusFiveClassificationContract> fiveClassificationContractList = fiveClassificationContractMapper.selectFiveClassificationContracts(fiveClassificationContract);
                //copy保存五级分类的合同
                if (CollectionUtils.isNotEmpty(fiveClassificationContractList)) {
                    for (int i = 0; i < fiveClassificationContractList.size(); i++) {
                        fiveClassificationContractList.get(i).setChangeIq("CHANGE");
                        fiveClassificationContractList.get(i).setChangeTime(maxChangeTime);
                        fiveClassificationContractList.get(i).setFiveClassifyConId(null);
                        fiveClassificationContractList.get(i).setApprovalNode("FIRST_NODE");
                        fiveClassificationContractList.get(i).set__status("add");
                    }
                    fiveClassificationContractService.batchUpdate(iRequest, fiveClassificationContractList);
                }
            }
            //备份附件信息
            HlsCusPlmAttachment attachment = new HlsCusPlmAttachment();
            attachment.setChangeIq("NORMAL");
            attachment.setPlmType("RW");
            attachment.setPlmId(riskWarning.getRiskWarningId());
            List<HlsCusPlmAttachment> attachmentList = attachmentMapper.selectAttachment(attachment);
            if (CollectionUtils.isNotEmpty(attachmentList)) {
                for (int i = 0; i < attachmentList.size(); i++) {
                    attachmentList.get(i).setAttachmentId(null);
                    attachmentList.get(i).setChangeIq("CHANGE");
                    attachmentList.get(i).setChangeTime(maxChangeTime);
                    attachmentList.get(i).set__status("add");
                }
                attachmentService.batchUpdate(iRequest, attachmentList);
            }
        }
    }

    /**
     * @Description:查询数据信息
     * @Author: Wty
     * @Date: Created om 15:03 2018/6/4
     */
    @Override
    public List<HlsCusRiskWarning>  queryAll(IRequest iRequest, HlsCusRiskWarning riskWarning) {
       return mapper.queryAll(riskWarning);
    }
    @Override
    public List<HlsCusRiskWarning>  queryAllNew(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        return mapper.queryAllNew(riskWarning);
    }


    /**
     * @Description:首页rollTable查询
     * @Author: Wty
     * @Date: Created om 13:28 2018/6/5
     */
    @Override
    public List<HlsCusRiskWarning> homeThirdQuery(IRequest iRequest, HlsCusRiskWarning riskWarning, int page, int pageSize) {
        if (riskWarning.getRiskLevelStatus() != null && !"".equals(riskWarning.getRiskLevelStatus())) {
            riskWarning.setRiskLevelArray(riskWarning.getRiskLevelStatus().split(","));
        }
        if (StringUtils.isNotEmpty(riskWarning.getStatuse2()))
        {
            riskWarning.setStatuses(Arrays.asList(riskWarning.getStatuse2().split(",")));
        }
        if (StringUtils.isNotEmpty(riskWarning.getIsReleaseWarning2()))
        {
            riskWarning.setIsReleaseWarnings(Arrays.asList(riskWarning.getIsReleaseWarning2().split(",")));
        }
        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("rw.creation_date desc");
        riskWarning.setCompanyId(iRequest.getCompanyId());
        List<HlsCusRiskWarning> list = mapper.homeThirdQuery(riskWarning);
        for (HlsCusRiskWarning dt : list) {
            HlsCusRiskWarningInfo info = new HlsCusRiskWarningInfo();
            info.setRiskWarningId(dt.getRiskWarningId());
            List<HlsCusRiskWarningInfo> listInfo = riskWarningInfoMapper.select(info);
            if (listInfo.size() > 0) {
                String riskInfoSource = "";
                if (StringUtils.isNotBlank(listInfo.get(0).getBreachContractRiskInfo())) {
                    riskInfoSource += "违约风险信息,";
                }
                if (StringUtils.isNotBlank(listInfo.get(0).getFinancialRiskInfo())) {
                    riskInfoSource += "财务风险信息,";
                }
                if (StringUtils.isNotBlank(listInfo.get(0).getGuaranteeRiskInfo())) {
                    riskInfoSource += "担保风险信息,";
                }
                if (StringUtils.isNotBlank(listInfo.get(0).getManagementRiskInfo())) {
                    riskInfoSource += "经营管理风险信息,";
                }
                if (StringUtils.isNotBlank(listInfo.get(0).getAssociatedRiskInfo())) {
                    riskInfoSource += "关联风险信息,";
                }
                if (StringUtils.isNotBlank(listInfo.get(0).getLitigationRiskInfo())) {
                    riskInfoSource += "涉诉风险信息,";
                }
                if (StringUtils.isNotBlank(listInfo.get(0).getOtherRiskInfo())) {
                    riskInfoSource += "其他预警信息,";
                }

                if (StringUtils.isNotBlank(riskInfoSource)) {
                    riskInfoSource = riskInfoSource.substring(0, riskInfoSource.length() - 1);
                    dt.setRiskInfoSource(riskInfoSource);
                }
            }

        }
        return list;
    }

    /**
     * @Description:判断是否解除预警
     * @Author: Wty
     * @Date: Created om 15:21 2018/6/5s
     */
    @Override
    public List<HlsCusRiskWarning> selectIsReleasing(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        riskWarning.setCompanyId(iRequest.getCompanyId());
        return mapper.selectIsReleasing(riskWarning);
    }

    /**
     * @Description:首页chart图
     * @Author: Wty
     * @Date: Created om 14:00 2018/6/6
     */
    @Override
    public List<Map> selectHomeChart(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        riskWarning.setCompanyId(iRequest.getCompanyId());
        return mapper.selectHomeChart(riskWarning);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public HlsCusRiskWarning createChange(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        HlsCusRiskWarning normalRiskWarning = mapper.selectByPrimaryKey(riskWarning.getRefRiskWarningId());

        riskWarning.setBpId(normalRiskWarning.getBpId());
        riskWarning.setBpType(normalRiskWarning.getBpType());
        riskWarning.setRiskWarningInfo(normalRiskWarning.getRiskWarningInfo());
        riskWarning.setStatus(normalRiskWarning.getStatus());
        riskWarning.setRiskWarningNumber(normalRiskWarning.getRiskWarningNumber());
        riskWarning.setRiskInfoSource(normalRiskWarning.getRiskInfoSource());
        riskWarning.setIsPresenceWarning(normalRiskWarning.getIsPresenceWarning());
        riskWarning.setIsReleaseWarning(normalRiskWarning.getIsReleaseWarning());
        riskWarning.setDocumentCategory(normalRiskWarning.getDocumentCategory());
        riskWarning.setDocumentType(normalRiskWarning.getDocumentType());
        riskWarning.setBusinessType(normalRiskWarning.getBusinessType());
        riskWarning.setCreationUserId(iRequest.getUserId());
        riskWarning.setCompanyId(iRequest.getCompanyId());
        riskWarning.setCreatedBy(iRequest.getUserId());
        riskWarning.setLastUpdatedBy(iRequest.getUserId());
        mapper.insertSelective(riskWarning);

        //复制行信息
        cloneLinedData(iRequest,normalRiskWarning.getRiskWarningId(),riskWarning.getRiskWarningId());

        normalRiskWarning.setStatus("PENDING");
        normalRiskWarning.setRefRiskWarningId(riskWarning.getRiskWarningId());
        mapper.updateByPrimaryKeySelective(normalRiskWarning);

        return riskWarning;
    }
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void  cloneLinedData(IRequest iRequest, Long sourceWaringId, Long targetWaringId){
        HlsCusRiskWarningInfo riskWarningInfo=new HlsCusRiskWarningInfo();
        riskWarningInfo.setRiskWarningId(sourceWaringId);
        List<HlsCusRiskWarningInfo> riskWarningInfos = riskWarningInfoService.select(iRequest, riskWarningInfo, 1, 999);
        for(HlsCusRiskWarningInfo warningInfo:riskWarningInfos){
            warningInfo.setRiskWarningInfoId(null);
            warningInfo.setRiskWarningId(targetWaringId);
            warningInfo.setCreatedBy(iRequest.getUserId());
            warningInfo.setLastUpdatedBy(iRequest.getUserId());
            warningInfo.setCreationDate(null);
            warningInfo.setLastUpdateDate(null);
            warningInfo.set__status(DTOStatus.ADD);
        }
        riskWarningInfoService.batchUpdate(iRequest,riskWarningInfos);
    }
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void clonePlmRiskFeedback(IRequest iRequest, Long sourceWaringId, Long targetWaringId, String changIq){
        PlmRiskFeedback plmRiskFeedback=new PlmRiskFeedback();
        plmRiskFeedback.setRiskWarningId(sourceWaringId);
        List<PlmRiskFeedback> list=plmRiskFeedbackMapper.select(plmRiskFeedback);
        //先克隆PLM_ATTACHMENT
        List<HlsCusPlmAttachment> clonedList= cloneHlsCusPlmAttachment(iRequest,sourceWaringId,targetWaringId,"PLM_RW_ATTACHMENT_BACK",changIq);



        Map<Long,List<HlsCusPlmAttachment>> map =clonedList
                .stream().collect(Collectors.groupingBy(HlsCusPlmAttachment::getSourceId));
        for (PlmRiskFeedback riskFeedback : list) {
            //根据原来的 attachmentId 获取新的 attachmentId
            List<HlsCusPlmAttachment> attachments=map.get(riskFeedback.getPlmAttachmentId());
            if (CollectionUtils.isNotEmpty(attachments)){
                riskFeedback.setPlmAttachmentId(attachments.get(0).getPlmAttachmentId());
                riskFeedback.setFeedbackId(null);
                riskFeedback.setRiskWarningId(targetWaringId);
                riskFeedback.setCreatedBy(iRequest.getUserId());
                riskFeedback.setLastUpdatedBy(iRequest.getUserId());
                riskFeedback.setCreationDate(null);
                riskFeedback.setLastUpdateDate(new Date());
                riskFeedback.setCreationDate(new Date());
                plmRiskFeedbackMapper.insertSelective(riskFeedback);
            }
        }
    }

    /**
     * 附件信息
     * @param iRequest -
     * @param sourceId 源id
     * @param targetId 目标id
     * @param cateGory 分类
     * @param thisChangeIq changIq
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public List<HlsCusPlmAttachment> cloneHlsCusPlmAttachment(IRequest iRequest, Long sourceId, Long targetId, String cateGory, String thisChangeIq ){
        if (StringUtils.isEmpty(cateGory)){
            cateGory="PLM_RW_ATTACHMENT";
        }
        HlsCusPlmAttachment plmAttachment=new HlsCusPlmAttachment();
        plmAttachment.setPlmId(sourceId);
        plmAttachment.setPlmAttachmentCategory(cateGory);
        //原来的
        List<HlsCusPlmAttachment> list=attachmentMapper.select(plmAttachment);
        for (HlsCusPlmAttachment attachment : list) {
            //原来的Id
            attachment.setSourceId(attachment.getPlmAttachmentId());
            attachment.setPlmAttachmentId(null);
            attachment.setPlmId(targetId);
            attachment.setChangeIqOld(attachment.getChangeIq());
            attachment.setChangeIq(thisChangeIq);
            attachment.setPlmAttachmentCategory(cateGory);
            attachment.setCreatedBy(iRequest.getUserId());
            attachment.setLastUpdatedBy(iRequest.getUserId());
            attachment.setCreationDate(new Date());
            attachment.setLastUpdateDate(new Date());
            attachmentMapper.insert(attachment);
        }
        plmAttachment.setPlmId(targetId);
        //查询新复制出来的行信息
        //新的
        list=attachmentMapper.select(plmAttachment).stream().filter(pojo->Objects.nonNull(pojo.getSourceId())).collect(Collectors.toList());
        //遍历ATTACHMENT 表的数据,分别复制sys_attachment 和 sys_file
        list.forEach(this::cloneSysAttachmentAndSysFile);
        return list;
    }

    /**
     * 分别复制sys_attachment 和 sys_file
     * @param plmAttachment
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void cloneSysAttachmentAndSysFile(HlsCusPlmAttachment plmAttachment) {
        Attachment attachment=new Attachment();
        attachment.setSourceKey(plmAttachment.getSourceId().toString());
        attachment.setSourceType(plmAttachment.getPlmAttachmentCategory());
        List<Attachment> attachments = sysAttachmentMapper.select(attachment);
        if (!attachments.isEmpty())
        {
            for (Attachment attachment1 : attachments) {
                //old source Id
                attachment1.setSourceKey(plmAttachment.getPlmAttachmentId().toString());
                attachment1.setCreatedBy(attachment1.getAttachmentId());
                attachment1.setAttachmentId(null);
                attachment1.setCreationDate(new Date());
                attachment.setLastUpdateDate(new Date());
                sysAttachmentMapper.insertSelective(attachment1);
            }
            attachment.setSourceKey(plmAttachment.getPlmAttachmentId().toString());
            attachments= sysAttachmentMapper.select(attachment);
            for (Attachment attachmentNew : attachments) {
                cloneSysFiles(attachmentNew);
            }
        }
    }

    /**
     * 复制sys_file表数据
     * @param attachmentNew
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void cloneSysFiles(Attachment attachmentNew) {
        SysFile sysFile=new SysFile();
        //这里使用createdBy 字段 装 old Attachment 的  attachmentId
        sysFile.setAttachmentId(attachmentNew.getCreatedBy());
        List<SysFile> sysFiles= sysFileMapper.select(sysFile);
        if (!sysFiles.isEmpty())
        {
            for (SysFile sfile : sysFiles) {
                sfile.setAttachmentId(attachmentNew.getAttachmentId());
                sfile.setFileId(null);
                sfile.setCreationDate(new Date());
                sfile.setLastUpdateDate(new Date());
                sysFileMapper.insertSelective(sfile);
            }
        }
    }

    /**
     * 删除行信息
     * @param iRequest
     * @param riskWaringId
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void  deleteLinedData(IRequest iRequest, Long  riskWaringId){
        HlsCusRiskWarningInfo riskWarningInfo=new HlsCusRiskWarningInfo();
        riskWarningInfo.setRiskWarningId(riskWaringId);
        riskWarningInfoMapper.delete(riskWarningInfo);
    }

    /**
     * 删除附件信息
     * @param iRequest
     * @param foreignId
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void  deleteHlsCusPlmAttachment(IRequest iRequest, Long foreignId, String category, Long plmAttachmentId){
        if (StringUtils.isEmpty(category))
        {
            category="PLM_RW_ATTACHMENT";
        }
        HlsCusPlmAttachment attachment=new HlsCusPlmAttachment();
        if (null!=plmAttachmentId) {
            attachment.setPlmAttachmentId(plmAttachmentId);
            attachmentMapper.deleteByPrimaryKey(attachment);
        }else {
            attachment.setPlmId(foreignId);
            attachment.setPlmAttachmentCategory(category);
            attachmentMapper.deleteSysFile(attachment);
            attachmentMapper.deleteSysAttachment(attachment);
            attachmentMapper.delete(attachment);
        }

    }

    /**
     * 删除反馈信息
     * @param iRequest
     * @param riskWaringId
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void  deletePlmRiskFeedback(IRequest iRequest, Long  riskWaringId){
        PlmRiskFeedback plmRiskFeedback=new PlmRiskFeedback();
        plmRiskFeedback.setRiskWarningId(riskWaringId);
        plmRiskFeedbackMapper.select(plmRiskFeedback).forEach(
                feedBack->deleteHlsCusPlmAttachment(iRequest,feedBack.getRiskWarningId(),"PLM_RW_ATTACHMENT_BACK",feedBack.getPlmAttachmentId())
        );
        plmRiskFeedbackMapper.delete(plmRiskFeedback);
    }
    @Override
    public HlsCusRiskWarning cancelChange(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        riskWarning=mapper.selectByPrimaryKey(riskWarning);

        HlsCusRiskWarning normalRiskWarning=new HlsCusRiskWarning();
        normalRiskWarning.setRiskWarningId(riskWarning.getRefRiskWarningId());
        normalRiskWarning.setStatus("APPROVED");
        mapper.updateByPrimaryKeySelective(normalRiskWarning);
        return riskWarning;
    }

    @Override
    public HlsCusRiskWarning submitChange(IRequest iRequest, HlsCusRiskWarning changeRiskWarning) {
        changeRiskWarning=mapper.selectByPrimaryKey(changeRiskWarning);

        HlsCusRiskWarning normalRiskWarning=mapper.selectByPrimaryKey(changeRiskWarning.getRefRiskWarningId());

        HlsCusRiskWarning historyWarning = new HlsCusRiskWarning();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(normalRiskWarning);
        hlsBeanRefUtilService.setFieldValue(historyWarning, map);
        historyWarning.setDataClass("HISTORY");
        historyWarning.setStatus(changeRiskWarning.getStatus());
        historyWarning.setRefRiskWarningId(normalRiskWarning.getRiskWarningId());
        historyWarning.setRiskWarningId(null);
        historyWarning.setApplyDate(changeRiskWarning.getApplyDate());
        historyWarning.setChangeApplyDesc(changeRiskWarning.getChangeApplyDesc());
        historyWarning.setCreationUserId(changeRiskWarning.getCreationUserId());
        historyWarning.setCreatedBy(changeRiskWarning.getCreatedBy());
        historyWarning.setLastUpdatedBy(changeRiskWarning.getCreatedBy());
        historyWarning.setLastUpdateLogin(changeRiskWarning.getCreatedBy());
        mapper.insertSelective(historyWarning);
        //复制normal的行数据 至history=============================
        cloneLinedData(iRequest,normalRiskWarning.getRiskWarningId(),historyWarning.getRiskWarningId());
        clonePlmRiskFeedback(iRequest,normalRiskWarning.getRiskWarningId(),historyWarning.getRiskWarningId(),"HISTORY");
        cloneHlsCusPlmAttachment(iRequest,normalRiskWarning.getRiskWarningId(),historyWarning.getRiskWarningId(),"","HISTORY");
        //======================================================================
        //更新normal
        normalRiskWarning.setStatus(changeRiskWarning.getStatus());
        normalRiskWarning.setIsReleaseWarning(changeRiskWarning.getIsReleaseWarning());
        normalRiskWarning.setIsPresenceWarning(changeRiskWarning.getIsPresenceWarning());
        normalRiskWarning.setBpType(changeRiskWarning.getBpType());
        normalRiskWarning.setRiskInfoSource(changeRiskWarning.getRiskInfoSource());
        normalRiskWarning.setBpId(changeRiskWarning.getBpId());
        normalRiskWarning.setRefRiskWarningId(-1L);
        self().updateByPrimaryKeySelective(iRequest, normalRiskWarning);
        deleteLinedData(iRequest,normalRiskWarning.getRiskWarningId());
        deletePlmRiskFeedback(iRequest,normalRiskWarning.getRiskWarningId());
        deleteHlsCusPlmAttachment(iRequest,normalRiskWarning.getRiskWarningId(),"",null);
        //复制changeReq的行数据 至normal
        cloneLinedData(iRequest,changeRiskWarning.getRiskWarningId(),normalRiskWarning.getRiskWarningId());
        clonePlmRiskFeedback(iRequest,changeRiskWarning.getRiskWarningId(),normalRiskWarning.getRiskWarningId(),"NORMAL");
        cloneHlsCusPlmAttachment(iRequest,changeRiskWarning.getRiskWarningId(),normalRiskWarning.getRiskWarningId(),"","NORMAL");
        //======================================================================
//        throw new RuntimeException();
        return changeRiskWarning;
    }
    //HlsCusRiskWarningInfo
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
////    //工商变更
////    public StringBuffer updataCommercialChange(IRequest iRequest,JSONArray jsonArray){
////        Date newTime = new Date();
////        StringBuffer stringBufferCommercialChange = new StringBuffer();
////        stringBufferCommercialChange.append("工商变更："+'\n');
////        for(int i=0;i<jsonArray.size();i++){
////            JSONObject newObject =(JSONObject)jsonArray.get(i);
////            Long id = Long.valueOf(newObject.get("id").toString());
////            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
////            String companyName = newObject.get("companyName").toString();
////            Long companyId = Long.valueOf(newObject.get("companyId").toString());
////            String changeItem = newObject.get("changeItem").toString();
////            String contentBefore = newObject.get("contentBefore").toString();
////            String contentAfter = newObject.get("contentAfter").toString();
////            String changeTime = newObject.get("changeTime").toString();
////            String newChangeTime = formatter.format(new Date(Long.parseLong(changeTime)));
////            try {
////                newTime = formatter.parse(newChangeTime);
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
////            //格式化
////            stringBufferCommercialChange.append("&nbsp;&nbsp;&nbsp;&nbsp;["+"公司id（公司唯一标识）："+companyGid+"，");
////            stringBufferCommercialChange.append("&nbsp;&nbsp;&nbsp;&nbsp;"+"公司名称："+companyName+"，");
////            stringBufferCommercialChange.append("&nbsp;&nbsp;&nbsp;&nbsp;"+"公司id（Deprecated）："+companyId+"，");
////            stringBufferCommercialChange.append("&nbsp;&nbsp;&nbsp;&nbsp;"+"变更事项："+changeItem+"，");
////            stringBufferCommercialChange.append("&nbsp;&nbsp;&nbsp;&nbsp;"+" 变更前："+contentBefore+"，");
////            stringBufferCommercialChange.append("&nbsp;&nbsp;&nbsp;&nbsp;"+"变更后 ："+contentAfter+"，");
////            stringBufferCommercialChange.append("&nbsp;&nbsp;&nbsp;&nbsp;"+"变更时间 ："+newTime+"]");
////
////            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo2 = new JcTianyanchaInterfaceInfo();
////            jcTianyanchaInterfaceInfo2.setDocumentId(companyId);
////            //有且只有一条
////            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
////            hlsCusRiskWarning.setBpId(companyId);
////            hlsCusRiskWarning.setApplyDate(new Date());
////            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
////            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
////            if(cusRiskWarning!=null){
////                cusRiskWarning.setRiskInfo(stringBufferCommercialChange.substring(0,stringBufferCommercialChange.length()));
////                hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);
////
////                //不存在就插入
////            }else{
////                //保存风险预警
////                HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
////                riskWarning.setDocumentCategory("RISK_WARNING");
////                riskWarning.setDocumentType("RISK_WARNING");
////                riskWarning.setBusinessType("RISK_WARNING");
////                riskWarning.setCompanyId(iRequest.getCompanyId());
////                riskWarning.setStatus("NEW");
////                riskWarning.setCompanyId(iRequest.getCompanyId());
////                riskWarning.setCreationUserId(iRequest.getUserId());
////                riskWarning.setIsReleaseWarning("N");
////                riskWarning.setIsPresenceWarning("Y");
////                riskWarning.setApplyDate(new Date());
////                riskWarning.setDataClass("NORMAL");
////                //生成单据编号
////                Map<String, String> params = new HashMap<String, String>();
////                riskWarning.setRiskWarningNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, riskWarning.getDocumentCategory(), riskWarning.getDocumentType(), riskWarning.getBusinessType(), params));
////                riskWarning.setRiskInfo(stringBufferCommercialChange.substring(0,stringBufferCommercialChange.length()));
////                hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);
////            }
////        }
////      return stringBufferCommercialChange;
////    }
//
//
//    //法院公告
//    public StringBuffer updataCourtNotice(IRequest iRequest,JSONArray jsonArray){
//        Date newTime = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("法院公告："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(newObject.get("companyId").toString());
//            String announceId = newObject.get("announceId").toString();
//            String bltnno = String.valueOf(newObject.get("bltnno"));
//            String bltnstate = String.valueOf(newObject.get("bltnstate"));
//            String bltntype = String.valueOf(newObject.get("bltntype"));
//            String bltntypename = String.valueOf(newObject.get("bltntypename"));
//            String caseno = String.valueOf(newObject.get("caseno"));
//            String content = String.valueOf(newObject.get("content"));
//            String courtcode = String.valueOf(newObject.get("courtcode"));
//            String dealgrade = String.valueOf(newObject.get("dealgrade"));
//            String dealgradename = String.valueOf(newObject.get("dealgradename"));
//            String judge = String.valueOf(newObject.get("judge"));
//            String party1 = String.valueOf(newObject.get("party1"));
//            String party2 = String.valueOf(newObject.get("party2"));
//            String province = String.valueOf(newObject.get("province"));
//            String publishdate = String.valueOf(newObject.get("publishdate"));
//            String newChangeTime = formatter.format(new Date(Long.parseLong(publishdate)));
//            try {
//                newTime = formatter.parse(newChangeTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String publishpage = String.valueOf(newObject.get("publishpage"));
//            String reason = String.valueOf(newObject.get("reason"));
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"公告id："+announceId+"，");
//            stringBuffer.append("       "+" 公告号："+bltnno+"，");
//            stringBuffer.append("       "+"公告状态号 ："+bltnstate+"，");
//            stringBuffer.append("       "+"公告类型 ："+bltntype+"，");
//            stringBuffer.append("       "+"公告类型名称 ："+bltntypename+"，");
//            stringBuffer.append("       "+"案件号 ："+caseno+"，");
//            stringBuffer.append("       "+"案件内容 ："+content+"，");
//            stringBuffer.append("       "+"法院名 ："+courtcode+"，");
//            stringBuffer.append("       "+"处理等级 ："+dealgrade+"，");
//            stringBuffer.append("       "+"处理等级名称 ："+dealgradename+"，");
//            stringBuffer.append("       "+"法官 ："+judge+"，");
//            stringBuffer.append("       "+"原告 ："+party1+"，");
//            stringBuffer.append("       "+"当事人 ："+party2+"，");
//            stringBuffer.append("       "+"省份 ："+province+"，");
//            stringBuffer.append("       "+"刊登日期 ："+newTime+"，");
//            stringBuffer.append("       "+"刊登版面 ："+publishpage+"，");
//            stringBuffer.append("       "+"原因 ："+reason+"]");
//
//        }
//        return stringBuffer;
//    }

//    //被执行人
//    public StringBuffer updataExecutee(IRequest iRequest,JSONArray jsonArray){
//        Date newTime = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("被执行人："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(newObject.get("companyId").toString());
//            String caseCode = newObject.get("caseCode").toString();
//            String execCourtName = String.valueOf(newObject.get("execCourtName"));
//            String pname = String.valueOf(newObject.get("pname"));
//            String partyCardNum = String.valueOf(newObject.get("partyCardNum"));
//            String execMoney = String.valueOf(newObject.get("execMoney"));
//            String caseCreateTime = String.valueOf(newObject.get("caseCreateTime"));
//            String newChangeTime = formatter.format(new Date(Long.parseLong(caseCreateTime)));
//            try {
//                newTime = formatter.parse(newChangeTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"案号："+caseCode+"，");
//            stringBuffer.append("       "+"执行法院："+execCourtName+"，");
//            stringBuffer.append("       "+"被执行人名称 ："+pname+"，");
//            stringBuffer.append("       "+"身份证号／组织机构代码 ："+partyCardNum+"，");
//            stringBuffer.append("       "+"创建时间 ："+newTime+"，");
//            stringBuffer.append("       "+"执行标的 ："+execMoney+"]");
//
//        }
//        return stringBuffer;
//    }

//    //行政处罚【工商局】
//    public StringBuffer updataAdministrative (IRequest iRequest,JSONArray jsonArray){
//        Date newTime1 = new Date();
//        Date newTime2 = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("行政处罚【工商局】："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(newObject.get("companyId").toString());
//            String content = newObject.get("content").toString();
//            String punishNumber = String.valueOf(newObject.get("punishNumber"));
//            String description = String.valueOf(newObject.get("description"));
//            String decisionDate = String.valueOf(newObject.get("decisionDate"));
//            String newDecisionDate = formatter.format(new Date(Long.parseLong(decisionDate)));
//            try {
//                newTime1 = formatter.parse(newDecisionDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String type = String.valueOf(newObject.get("type"));
//            String departmentName = String.valueOf(newObject.get("departmentName"));
//            String publishDate = String.valueOf(newObject.get("publishDate"));
//            String newChangeTime = formatter.format(new Date(Long.parseLong(publishDate)));
//            try {
//                newTime2 = formatter.parse(newChangeTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"行政处罚内容："+content+"，");
//            stringBuffer.append("       "+"定书文号："+punishNumber+"，");
//            stringBuffer.append("       "+"描述  ："+description+"，");
//            stringBuffer.append("       "+"违法行为类型："+type+"，");
//            stringBuffer.append("       "+"决定日期 ："+newTime1+"，");
//            stringBuffer.append("       "+"决定机关名称 ："+departmentName+"，");
//            stringBuffer.append("       "+"公示日期 ："+newTime2+"]");
//
//        }
//        return stringBuffer;
//    }

//    //严重违法
//    public StringBuffer updataSerious (IRequest iRequest,JSONArray jsonArray){
//        Date newTime1 = new Date();
//        Date newTime2 = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("严重违法："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(newObject.get("companyId").toString());
//            String removeReason = newObject.get("removeReason").toString();
//            String removeDepartment = String.valueOf(newObject.get("removeDepartment"));
//            String putReason = String.valueOf(newObject.get("putReason"));
//            String putDepartment = String.valueOf(newObject.get("putDepartment"));
//            String execMoney = String.valueOf(newObject.get("execMoney"));
//            String putDate = String.valueOf(newObject.get("putDate"));
//            String newChangeTime = formatter.format(new Date(Long.parseLong(putDate)));
//            try {
//                newTime1 = formatter.parse(newChangeTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String removeDate = String.valueOf(newObject.get("removeDate"));
//            String newRemoveDate = formatter.format(new Date(Long.parseLong(removeDate)));
//            try {
//                newTime2 = formatter.parse(newRemoveDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"移除原因 ："+removeReason+"，");
//            stringBuffer.append("       "+"移除部门 ："+removeDepartment+"，");
//            stringBuffer.append("       "+"列入原因 ："+putReason+"，");
//            stringBuffer.append("       "+"列入部门 ："+putDepartment+"，");
//            stringBuffer.append("       "+"列入日期 ："+newTime1+"，");
//            stringBuffer.append("       "+"移除日期 ："+newTime2+"]");
//
//        }
//        return stringBuffer;
//    }

//    //股权出质
//    public StringBuffer updataEquity (IRequest iRequest,JSONArray jsonArray){
//        Date newTime1 = new Date();
//        Date newTime2 = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("股权出质："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(newObject.get("companyId").toString());
//            String equityAmount = newObject.get("equityAmount").toString();
//            String regNumber = String.valueOf(newObject.get("regNumber"));
//            String pledgee = String.valueOf(newObject.get("pledgee"));
//            String putDate = String.valueOf(newObject.get("putDate"));
//            String newChangeTime = formatter.format(new Date(Long.parseLong(putDate)));
//            try {
//                newTime1 = formatter.parse(newChangeTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String regDate = String.valueOf(newObject.get("regDate"));
//            String newRemoveDate = formatter.format(new Date(Long.parseLong(regDate)));
//            try {
//                newTime2 = formatter.parse(newRemoveDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String state = String.valueOf(newObject.get("state"));
//            String pledgor = String.valueOf(newObject.get("pledgor"));
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"出质股权数额 ："+equityAmount+"，");
//            stringBuffer.append("       "+"注册号 ："+regNumber+"，");
//            stringBuffer.append("       "+"质权人 ："+pledgee+"，");
//            stringBuffer.append("       "+"列入原因 ："+state+"，");
//            stringBuffer.append("       "+"出质人："+pledgor+"，");
//            stringBuffer.append("       "+"股权出质设立发布日期 ："+newTime1+"，");
//            stringBuffer.append("       "+"股权出质设立登记日期 ："+newTime2+"]");
//
//        }
//        return stringBuffer;
//    }

//    //动产抵押
//    public StringBuffer updataMortgage (IRequest iRequest,JSONArray jsonArray){
//        Date newTime1 = new Date();
//        Date newTime2 = new Date();
//        Date newTime3 = new Date();
//        Date newTime4 = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            //
//            JSONObject baseInfoObject = (JSONObject)newObject.get("baseInfo");
//            stringBuffer.append("       "+"基本信息："+"\n");
//            String overviewAmount = String.valueOf(baseInfoObject.get("overviewAmount"));
//            String scope = String.valueOf(baseInfoObject.get("scope"));
//            String status = String.valueOf(baseInfoObject.get("status"));
//            String remark = String.valueOf(baseInfoObject.get("remark"));
//            String regDate = String.valueOf(baseInfoObject.get("regDate"));
//            String newRegDate = formatter.format(new Date(Long.parseLong(regDate)));
//            try {
//                newTime2 = formatter.parse(newRegDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String overviewType = String.valueOf(baseInfoObject.get("overviewType"));
//            String type = String.valueOf(baseInfoObject.get("type"));
//            String cancelReason = String.valueOf(baseInfoObject.get("cancelReason"));
//            String overviewScope = String.valueOf(baseInfoObject.get("overviewScope"));
//
//            String amount = String.valueOf(baseInfoObject.get("amount"));
//            String overviewRemark = String.valueOf(baseInfoObject.get("overviewRemark"));
//            String overviewTerm = String.valueOf(baseInfoObject.get("overviewTerm"));
//            String regDepartment = String.valueOf(baseInfoObject.get("regDepartment"));
//            String regNum = String.valueOf(baseInfoObject.get("regNum"));
//            String term = String.valueOf(baseInfoObject.get("term"));
//            String base = String.valueOf(baseInfoObject.get("base"));
//            String cancelDate = String.valueOf(baseInfoObject.get("cancelDate"));
//            String newCancelDate = formatter.format(new Date(Long.parseLong(cancelDate)));
//            try {
//                newTime3 = formatter.parse(newCancelDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String publishDate = String.valueOf(baseInfoObject.get("publishDate"));
//            String newPublishDate = formatter.format(new Date(Long.parseLong(publishDate)));
//            try {
//                newTime4 = formatter.parse(newPublishDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            stringBuffer.append("               ["+"概况数额："+overviewAmount+"，");
//            stringBuffer.append("                "+"担保范围："+scope+"，");
//            stringBuffer.append("                "+"状态 ："+status+"，");
//            stringBuffer.append("                "+"备注 ："+remark+"，");
//            stringBuffer.append("                "+"登记日期："+newTime2+"，");
//            stringBuffer.append("                "+"概况种类："+overviewType+"，");
//            stringBuffer.append("                "+"被担保债权种类："+type+"，");
//            stringBuffer.append("                "+"注销原因："+cancelReason+"，");
//            stringBuffer.append("                "+"概况担保的范围："+overviewScope+"，");
//            stringBuffer.append("                "+"被担保债权数额："+amount+"，");
//            stringBuffer.append("                "+"概况备注："+overviewRemark+"，");
//            stringBuffer.append("                "+"概况债务人履行债务的期限："+overviewTerm+"，");
//            stringBuffer.append("                "+"登记机关："+regDepartment+"，");
//            stringBuffer.append("                "+"登记编号："+regNum+"，");
//            stringBuffer.append("                "+"债务人履行债务的期限："+term+"，");
//            stringBuffer.append("                "+"省份："+base+"，");
//            stringBuffer.append("                "+"注销日期："+newTime3+"，");
//            stringBuffer.append("                "+"公示日期 ："+newTime4+"]");
//            //
//            JSONArray jsonArrayChangeInfoList = newObject.getJSONArray("changeInfoList");
//            stringBuffer.append("       "+"变更信息："+"\n");
//            for(int j=0;j<jsonArrayChangeInfoList.size();j++){
//                JSONObject changeInfoListObject =(JSONObject)jsonArray.get(i);
//                String changeContent = String.valueOf(changeInfoListObject.get("changeContent"));
//                String changeDate = String.valueOf(newObject.get("changeDate"));
//                String newChangeDate = formatter.format(new Date(Long.parseLong(changeDate)));
//                try {
//                    newTime1 = formatter.parse(newChangeDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                stringBuffer.append("               ["+"变更日期："+newTime1+"，");
//                stringBuffer.append("                "+"变更内容 ："+changeContent+"]");
//            }
//
//            //
//            JSONArray jsonArrayPawnInfoList = newObject.getJSONArray("pawnInfoList");
//            stringBuffer.append("       "+"抵押物："+"\n");
//            for(int n=0;n<jsonArrayPawnInfoList.size();n++){
//                JSONObject pawnInfoListObject =(JSONObject)jsonArray.get(i);
//                String detail = String.valueOf(pawnInfoListObject.get("detail"));
//                String ownership = String.valueOf(pawnInfoListObject.get("ownership"));
//                String pawnName = String.valueOf(pawnInfoListObject.get("pawnName"));
//                String remark1 = String.valueOf(pawnInfoListObject.get("remark"));
//                stringBuffer.append("               ["+"数量、质量、状况、所在地等情况："+detail+"，");
//                stringBuffer.append("                "+"所有权归属："+ownership+"，");
//                stringBuffer.append("                "+"名称 ："+pawnName+"，");
//                stringBuffer.append("                "+"备注 ："+remark1+"]");
//            }
//            //
//            JSONArray jsonArrayPeopleInfo = newObject.getJSONArray("peopleInfo");
//            stringBuffer.append("       "+"抵押人："+"\n");
//            for(int m=0;m<jsonArrayPeopleInfo.size();m++){
//                JSONObject peopleInfoObject =(JSONObject)jsonArray.get(i);
//                String licenseNum = String.valueOf(peopleInfoObject.get("licenseNum"));
//                String peopleName = String.valueOf(peopleInfoObject.get("peopleName"));
//                String liceseType = String.valueOf(peopleInfoObject.get("liceseType"));
//                stringBuffer.append("               ["+"证照/证件号码："+licenseNum+"，");
//                stringBuffer.append("                "+"抵押权人名称："+peopleName+"，");
//                stringBuffer.append("                "+"抵押权人证照/证件类型："+liceseType+"]");
//            }
//        }
//        return stringBuffer;
//    }

//    //立案信息
//    public StringBuffer updataFiling (IRequest iRequest,JSONArray jsonArray){
//        Date newTime1 = new Date();
//        Date newTime2 = new Date();
//        Date newTime3 = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("立案信息："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
//
//            String plaintiff = String.valueOf(newObject.get("plaintiff"));
//            String caseStatus = String.valueOf(newObject.get("caseStatus"));
//            String assistant = String.valueOf(newObject.get("assistant"));
//            String court = String.valueOf(newObject.get("court"));
//            String caseNo = String.valueOf(newObject.get("caseNo"));
//            String caseType = String.valueOf(newObject.get("caseType"));
//            String third = String.valueOf(newObject.get("third"));
//            String defendant = String.valueOf(newObject.get("defendant"));
//            String judge = String.valueOf(newObject.get("judge"));
//            String department = String.valueOf(newObject.get("department"));
//
//            String filingDate = String.valueOf(newObject.get("filingDate"));
//            String newFilingDate = formatter.format(new Date(Long.parseLong(filingDate)));
//            try {
//                newTime1 = formatter.parse(newFilingDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String closeDate = String.valueOf(newObject.get("closeDate"));
//            String newCloseDate = formatter.format(new Date(Long.parseLong(closeDate)));
//            try {
//                newTime2 = formatter.parse(newCloseDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String startTime = String.valueOf(newObject.get("startTime"));
//            String newStartTime = formatter.format(new Date(Long.parseLong(startTime)));
//            try {
//                newTime3 = formatter.parse(newStartTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"公诉人/原告/上诉人/申请人 ："+plaintiff+"，");
//            stringBuffer.append("       "+"案件状态 ："+caseStatus+"，");
//            stringBuffer.append("       "+"法官助理 ："+assistant+"，");
//            stringBuffer.append("       "+"法院 ："+court+"，");
//            stringBuffer.append("       "+"案号："+caseNo+"，");
//            stringBuffer.append("       "+"案件类型 ："+caseType+"，");
//            stringBuffer.append("       "+"第三人 ："+third+"，");
//            stringBuffer.append("       "+"被告人/被告/被上诉人/被申请人 ："+defendant+"，");
//            stringBuffer.append("       "+"承办法官："+judge+"，");
//            stringBuffer.append("       "+"承办部门："+department+"，");
//            stringBuffer.append("       "+"立案日期 ："+newTime1+"，");
//            stringBuffer.append("       "+"开庭日期 ："+newTime3+"，");
//            stringBuffer.append("       "+"结束日期 ："+newTime2+"]");
//
//        }
//        return stringBuffer;
//    }

//    //土地抵押
//    public StringBuffer updataLand (IRequest iRequest,JSONArray jsonArray){
//        Date newTime1 = new Date();
//        Date newTime2 = new Date();
//        Date newTime3 = new Date();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("土地抵押："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
//
//            String mortgageApplicationName = String.valueOf(newObject.get("mortgageApplicationName"));
//            String mortgageArea = String.valueOf(newObject.get("mortgageArea"));
//            String nature = String.valueOf(newObject.get("nature"));
//            String landArea = String.valueOf(newObject.get("landArea"));
//            String otherItemApplicationNameNum = String.valueOf(newObject.get("otherItemApplicationNameNum"));
//            String landAministrativeArea = String.valueOf(newObject.get("landAministrativeArea"));
//            String landMark = String.valueOf(newObject.get("landMark"));
//            String useRightNum = String.valueOf(newObject.get("useRightNum"));
//            String landLoc = String.valueOf(newObject.get("landLoc"));
//            String mortgageAmount = String.valueOf(newObject.get("mortgageAmount"));
//            String landNum = String.valueOf(newObject.get("landNum"));
//            String userType = String.valueOf(newObject.get("userType"));
//            String evaluateAmount = String.valueOf(newObject.get("evaluateAmount"));
//            String mortgagePerson = String.valueOf(newObject.get("mortgagePerson"));
//            String mortgageToUser = String.valueOf(newObject.get("mortgageToUser"));
//
//            String startDateClean = String.valueOf(newObject.get("startDateClean"));
//            String newFilingDate = formatter.format(new Date(Long.parseLong(startDateClean)));
//            try {
//                newTime1 = formatter.parse(newFilingDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String endDateClean = String.valueOf(newObject.get("endDateClean"));
//            String newCloseDate = formatter.format(new Date(Long.parseLong(endDateClean)));
//            try {
//                newTime2 = formatter.parse(newCloseDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"土地抵押权人 ："+mortgageApplicationName+"，");
//            stringBuffer.append("       "+"抵押面积(公顷) ："+mortgageArea+"，");
//            stringBuffer.append("       "+"土地抵押人性质 ："+nature+"，");
//            stringBuffer.append("       "+"土地面积(公顷) ："+landArea+"，");
//            stringBuffer.append("       "+"土地他项权利人证号："+otherItemApplicationNameNum+"，");
//            stringBuffer.append("       "+"所在行政区 ："+landAministrativeArea+"，");
//            stringBuffer.append("       "+" 宗地标识 ："+landMark+"，");
//            stringBuffer.append("       "+"土地使用权证号 ："+useRightNum+"，");
//            stringBuffer.append("       "+"宗地坐落："+landLoc+"，");
//            stringBuffer.append("       "+"抵押金额(万元)："+mortgageAmount+"，");
//            stringBuffer.append("       "+"宗地编号："+landNum+"，");
//            stringBuffer.append("       "+"抵押土地权属性质与使用权类型："+userType+"，");
//            stringBuffer.append("       "+"评估金额(万元)："+evaluateAmount+"，");
//            stringBuffer.append("       "+"土地抵押人名称："+mortgagePerson+"，");
//            stringBuffer.append("       "+"抵押土地用途："+mortgageToUser+"，");
//            stringBuffer.append("       "+"土地抵押登记起始时间 ："+newTime1+"，");
//            stringBuffer.append("       "+"土地抵押结束时间 ："+newTime2+"]");
//
//        }
//        return stringBuffer;
//    }

//    //大股东变更
//    public StringBuffer updataShareholders (IRequest iRequest,JSONArray jsonArray){
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("大股东变更："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
//
//            String bigShareholder = String.valueOf(newObject.get("bigShareholder"));
//            String bigShareholderBefore = String.valueOf(newObject.get("bigShareholderBefore"));
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"原大股东名称 ："+bigShareholderBefore+"，");
//            stringBuffer.append("       "+"现大股东名称 ："+bigShareholder+"]");
//        }
//        return stringBuffer;
//    }


//    //股权变更
//    public StringBuffer updataChange (IRequest iRequest,JSONArray jsonArray){
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("股权变更："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            Long id = Long.valueOf(newObject.get("id").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
//
//            String capital = String.valueOf(newObject.get("capital"));
//            String capitalBefore = String.valueOf(newObject.get("capitalBefore"));
//            String percent = String.valueOf(newObject.get("percent"));
//            String shareholderName = String.valueOf(newObject.get("shareholderName"));
//            String percentBefore = String.valueOf(newObject.get("percentBefore"));
//
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，");
//            stringBuffer.append("       "+"公司名称："+companyName+"，");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
//            stringBuffer.append("       "+"股东名称："+shareholderName+"，");
//            stringBuffer.append("       "+"原认缴出资额："+capitalBefore+"，");
//            stringBuffer.append("       "+"现认缴出资额："+capital+"，");
//            stringBuffer.append("       "+"原出资比例："+percentBefore+"，");
//            stringBuffer.append("       "+"现出资比例："+percent+"]");
//        }
//        return stringBuffer;
//    }

//    //新闻舆情
//    public StringBuffer updataOpinion (IRequest iRequest,JSONArray jsonArray){
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("新闻舆情："+'\n');
//        for(int i=0;i<jsonArray.size();i++){
//            JSONObject newObject =(JSONObject)jsonArray.get(i);
//            String docid = String.valueOf(newObject.get("docid").toString());
//            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
//            String companyName = newObject.get("companyName").toString();
//            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
//
//            String source = String.valueOf(newObject.get("source"));
//            String title = String.valueOf(newObject.get("title"));
//            String url = String.valueOf(newObject.get("url"));
//
//            //格式化
//            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
//            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
//            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
//            stringBuffer.append("       "+"新闻标题："+title+"，\n");
//            stringBuffer.append("       "+"来源："+source+"，\n");
//            stringBuffer.append("       "+"原文链接："+url+"]");
//        }
//        return stringBuffer;
//    }






//    public HlsCusRiskWarning updateRiskWarningBySky(IRequest iRequest,HlsCusRiskWarning riskWarning,String postData){
//
////        JSONObject jsonObject = JSONObject.parseObject(postData);
////        Long reason = Long.valueOf(String.valueOf(jsonObject.get("total")));
////        String type = String.valueOf(jsonObject.get("type"));
////        JSONArray jsonArray = jsonObject.getJSONArray("items");
////        StringBuffer stringBuffer = new StringBuffer();
////        //
////        //保存风险预警
////        riskWarning.setDocumentCategory("RISK_WARNING");
////        riskWarning.setDocumentType("RISK_WARNING");
////        riskWarning.setBusinessType("RISK_WARNING");
////        riskWarning.setCompanyId(iRequest.getCompanyId());
////        riskWarning.setStatus("NEW");
////        riskWarning.setCompanyId(iRequest.getCompanyId());
////        riskWarning.setCreationUserId(iRequest.getUserId());
////        riskWarning.setIsReleaseWarning("N");
////        riskWarning.setIsPresenceWarning("Y");
////        riskWarning.setApplyDate(new Date());
////        riskWarning.setDataClass("NORMAL");
////        //生成单据编号
////        Map<String, String> params = new HashMap<String, String>();
////
////
////        //工商变更
////        if("11".equals(type)){
////            stringBuffer.append(updataCommercialChange(iRequest,jsonArray));
////            //法院公告
////        }else if("32".equals(type)){
////            stringBuffer.append(updataCourtNotice(iRequest,jsonArray));
////            //被执行人
////        }else if("34".equals(type)){
////            stringBuffer.append(updataExecutee(iRequest,jsonArray));
////            //行政处罚【工商局】
////        }else if("42".equals(type)){
////            stringBuffer.append(updataAdministrative(iRequest,jsonArray));
////            //严重违法
////        }else if("43".equals(type)){
////            stringBuffer.append(updataSerious(iRequest,jsonArray));
////
////            //股权出质
////        }else if("44".equals(type)){
////            stringBuffer.append(updataEquity(iRequest,jsonArray));
////
////            //动产抵押
////        }else if("45".equals(type)){
////            stringBuffer.append(updataMortgage(iRequest,jsonArray));
////
////            //立案信息
////        }else if("80".equals(type)){
////            stringBuffer.append(updataFiling(iRequest,jsonArray));
////
////            //土地抵押
////        }else if("85".equals(type)){
////            stringBuffer.append(updataLand(iRequest,jsonArray));
////
////            //大股东变更
////        }else if("116".equals(type)){
////            stringBuffer.append(updataShareholders(iRequest,jsonArray));
////            //股权变更
////        }else if("122".equals(type)){
////            stringBuffer.append(updataChange(iRequest,jsonArray));
////
////            //新闻舆情
////        }else if("3001".equals(type)){
////            stringBuffer.append(updataOpinion(iRequest,jsonArray));
////
////        }else{
////
////        }
////
////        //赋值
////        riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
////        //保存风险预警info
//////        HlsCusRiskWarningInfo riskWarningInfo = riskWarning.getRiskWarningInfo();
//////        riskWarningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
////        //riskWarningInfo.setR
////
////        riskWarning.setRiskWarningNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, riskWarning.getDocumentCategory(), riskWarning.getDocumentType(), riskWarning.getBusinessType(), params));
////        riskWarning = self().insertSelective(iRequest, riskWarning);
//        return riskWarning;
//    }

}
