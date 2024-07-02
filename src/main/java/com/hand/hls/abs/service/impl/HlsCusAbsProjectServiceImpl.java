package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.*;
import com.hand.hls.abs.mapper.HlsCusAbsProjectMapper;
import com.hand.hls.abs.service.*;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.service.HlsCusFctContractService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProjectServiceImpl extends BaseServiceImpl<HlsCusAbsProject> implements HlsCusAbsProjectService {

    /**
     * 单据状态
     * 立项新建
     * 立项中/资产转让审批中
     * 立项退回
     * 立项成功 ABS_PROJECT_WFL
     */
    private static final String STATUS_NEW = "NEW";
    private static final String STATUS_APPROVING = "APPROVING";
    private static final String STATUS_APPROVED_RETURN = "APPROVED_RETURN";
    private static final String STATUS_APPROVED = "APPROVED";
    /**
     * 立项审批
     * EVEN CODE ABS_PROJECT_WFL
     */
    private static final String EVENT_CODE_ABS_PROJECT_WFL = "ABS_PROJECT_WFL";
    /**
     * 资产包状态
     * ABS_PROPERTY_STATUS_FREE
     * ABS_PROPERTY_STATUS_OCCUPY
     */
    private static final String ABS_PROPERTY_STATUS_FREE = "FREE";
    private static final String ABS_PROPERTY_STATUS_OCCUPY = "OCCUPY";
    @Autowired
    private HlsCusAbsProjectMapper hlsCusAbsProjectMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusAbsProjectOrganizationService organizationService;
    @Autowired
    private HlsCusAbsPropertyContractService propertyContractService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusFctContractService hlsCusFctContractService;

    @Autowired
    private HlsCusAbsBankAccountService absBankAccountService;

    @Autowired
    private HlsCusAbsProFeeInfoService absProjectFeeInfoService;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private HlsSysFileService hlsSysFileService;

    @Autowired
    private HlsCusAbsAssetsPackageService absAssetsPackageService;

    @Override
    public HlsCusAbsPkg absProjectSave(IRequest request, HlsCusAbsPkg hlsCusAbsPkg) {
        HlsCusAbsProject absProject = hlsCusAbsPkg.getHlsCusAbsProject();
        HlsCusAbsPkg pkg = new HlsCusAbsPkg();
        // 更新立项表
        if (absProject.getProjectId() == null || absProject.getProjectId() == 0) {
            /*编码规则*/
            Map<String, String> params = new HashMap<String, String>();
            absProject.setProjectNumber(fndCodingRuleValuesService.getCodeRuleValue(request, HlsCusConstant.ABS_PRO_TYPE.PROJECT, HlsCusConstant.ABS_PRO_TYPE.PROJECT, HlsCusConstant.ABS_PRO_TYPE.PROJECT, params));
            absProject.setCreatedBy(request.getUserId());
            absProject.setLastUpdatedBy(request.getUserId());
            absProject = self().insertSelective(request, absProject);
        } else {
            absProject = self().updateByPrimaryKey(request, absProject);
        }
        pkg.setHlsCusAbsProject(absProject);
        // 更新机构表
        List<HlsCusAbsProjectOrganization> organizations = hlsCusAbsPkg.getHlsCusAbsProjectOrganizations();
        if(CollectionUtils.isNotEmpty(organizations)){
            for (HlsCusAbsProjectOrganization or : organizations) {
                if (or.getOrganizationId() == null || or.getOrganizationId() == 0) {
                    or.setProjectId(absProject.getProjectId());
                    organizationService.insertSelective(request, or);
                } else {
                    or.setProjectId(absProject.getProjectId());
                    organizationService.updateByPrimaryKeySelective(request, or);
                }
            }
            pkg.setHlsCusAbsProjectOrganizations(organizations);
        }

        // 更新账户信息
        List<HlsCusAbsBankAccount> absBankAccounts = hlsCusAbsPkg.getHlsCusAbsProBankAccounts();
        if(CollectionUtils.isNotEmpty(absBankAccounts)){
            for (HlsCusAbsBankAccount bankAccount : absBankAccounts) {
                if (bankAccount.getAbsBankAccountId() == null || bankAccount.getAbsBankAccountId() == 0) {
                    bankAccount.setSourceKey(absProject.getProjectId());
                    absBankAccountService.insertSelective(request, bankAccount);
                } else {
                    bankAccount.setSourceKey(absProject.getProjectId());
                    absBankAccountService.updateByPrimaryKeySelective(request, bankAccount);
                }
            }
            pkg.setHlsCusAbsProBankAccounts(absBankAccounts);
        }


        // 更新费用
        List<HlsCusAbsProFeeInfo> projectFeeInfos = hlsCusAbsPkg.getHlsCusAbsProFeeInfos();
        if(CollectionUtils.isNotEmpty(projectFeeInfos)){
            for (HlsCusAbsProFeeInfo feeInfo : projectFeeInfos) {
                if (feeInfo.getFeeInfoId() == null || feeInfo.getFeeInfoId() == 0) {
                    feeInfo.setSourceKey(absProject.getProjectId());
                    absProjectFeeInfoService.insertSelective(request, feeInfo);
                } else {
                    feeInfo.setSourceKey(absProject.getProjectId());
                    absProjectFeeInfoService.updateByPrimaryKeySelective(request, feeInfo);
                }
            }
            pkg.setHlsCusAbsProFeeInfos(projectFeeInfos);
        }

        //附件
        if (hlsCusAbsPkg.getHlsCusAbsProAttachments() != null) {
            for (HlsCusPrjProjectAttachment attachment: hlsCusAbsPkg.getHlsCusAbsProAttachments()){
                if(attachment.getProjectAttachmentId()!=null){
                    if(attachment.getDescription()==null){
                        attachment.setDescription("");
                    }
                    if(attachment.getAttachmentCode()==null){
                        attachment.setAttachmentCode("");
                    }
                    hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(request,attachment);

                    HlsCusSysFile sysFile=new HlsCusSysFile();
                    sysFile.setFileId(Long.parseLong(attachment.getFileId()));
                    sysFile.setFileName(attachment.getFileName());
                    hlsSysFileService.updateByPrimaryKeySelective(request,sysFile);
                }
            }

        }

        return pkg;
    }

    @Override
    public List<HlsCusAbsProject> queryProjectDetail(IRequest request, HlsCusAbsProject hlsCusAbsProject) {
        return hlsCusAbsProjectMapper.queryProjectDetail(hlsCusAbsProject);
    }

    @Override
    public List<HlsCusAbsProject> absPieChartQuery(IRequest request, HlsCusAbsProject hlsCusAbsProject) {
        return hlsCusAbsProjectMapper.absPieChartQuery(hlsCusAbsProject);
    }

    @Override

        public List<HlsCusAbsProject>  absProjectSubmit(IRequest request, HlsCusAbsProject hlsCusAbsProject) throws HlsCusException {
        /*先保存数据在提交审批*/
/*
        HlsCusAbsProject hlsCusAbsProject = new HlsCusAbsProject();
*/
        hlsCusAbsProject.setProjectId(hlsCusAbsProject.getProjectId());
        hlsCusAbsProject = self().selectByPrimaryKey(request, hlsCusAbsProject);
        if(!hlsCusAbsProject.getProjectStatus().equalsIgnoreCase("APPROVING") || !hlsCusAbsProject.getProjectStatus().equalsIgnoreCase("APPROVED")){
            List<HlsCusAbsProject> hlsCusAbsProjectList = new ArrayList<>();
            hlsCusAbsProjectList.add(hlsCusAbsProject);
            databaseLockProvider.lock(hlsCusAbsProject);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
            String employeeCode = employee.getEmployeeCode();
            request.setEmployeeCode(employeeCode);
            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", HlsCusConstant.ABS_WFL.PROJECT_WFL);
            activitiStartService.start(request, hlsCusAbsProjectList, params);
            /*流程启动后更新单据状态*/
            hlsCusAbsProject.setProjectStatus(STATUS_APPROVING);
            self().updateByPrimaryKeySelective(request, hlsCusAbsProject);


        }
        List<HlsCusAbsProject> contractInsure = new ArrayList<>();
        contractInsure.add(hlsCusAbsProject);
        return contractInsure;
/*        if(hlsCusAbsProject.getPackId()!=null){
            List<HlsCusAbsAssetsPackage> assetsPackageList = absAssetsPackageService.selectPackageOccupyData(request, hlsCusAbsProject.getPackId());
            for(HlsCusAbsAssetsPackage assetsPackage:assetsPackageList){
                StringBuilder builder=new StringBuilder();
                builder.append("资产包中合同号为:");
                builder.append(assetsPackage.getContractNumber());
                builder.append("被占用，无法提交审批。占用单据为：");
                builder.append(assetsPackage.getOccupyDocument());
                throw new HlsCusException(builder.toString());
            }
        }*/

//        /*发消息*/
//        Map<String, Object> paramsEvent = new HashMap<String, Object>();
//        String userName = "";
//        if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
//            userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
//        }
//        String msg = userName + "创建了" + hlsCusAbsProject.getProjectName() + "ABS立项审核" + hlsCusAbsProject.getProjectNumber();
//        paramsEvent.put("message", msg);
//        paramsEvent.put("noticeTitle", "ANS立项审批");
//        paramsEvent.put("noticeType", "NOTICE");
//        paramsEvent.put("url", "");
//        paramsEvent.put("level", 1L);
//        sysEventService.eventSave(request, hlsCusAbsProject.getProjectId(), hlsCusAbsProject.getDocumentCategory(), hlsCusAbsProject.getDocumentType(), "ABS", EVENT_CODE_ABS_PROJECT_WFL, "P2D", paramsEvent);
    }

    @Override
    public HlsCusAbsPkg assetTransferSave(IRequest request, HlsCusAbsPkg hlsCusAbsPkg) {
        HlsCusAbsPkg pkg = new HlsCusAbsPkg();
        HlsCusAbsProject absProject = hlsCusAbsPkg.getHlsCusAbsProject();
        absProject = self().updateByPrimaryKeySelective(request, absProject);
        pkg.setHlsCusAbsProject(absProject);
        List<HlsCusAbsProjectOrganization> projectOrganizations = hlsCusAbsPkg.getHlsCusAbsProjectOrganizations();
        for (HlsCusAbsProjectOrganization org : projectOrganizations) {
            org.setReceiverFlag("Y");
            organizationService.updateByPrimaryKeySelective(request, org);
        }
        pkg.setHlsCusAbsProjectOrganizations(projectOrganizations);
        return pkg;
    }

    @Override
    public void submitWflApproval(IRequest request, HlsCusAbsProject hlsCusAbsProject) {
        self().updateByPrimaryKeySelective(request, hlsCusAbsProject);
    }

    /* @Override
    public HlsCusAbsPkg absTransferSubmit(IRequest request, HlsCusAbsPkg hlsCusAbsPkg) {
        *//*先保存数据在提交审批*//*
        hlsCusAbsPkg = self().assetTransferSave(request, hlsCusAbsPkg);
        HlsCusAbsProject hlsCusAbsProject = new HlsCusAbsProject();
        hlsCusAbsProject.setProjectId(hlsCusAbsPkg.getHlsCusAbsProject().getProjectId());
        hlsCusAbsProject = self().selectByPrimaryKey(request, hlsCusAbsProject);
        List<HlsCusAbsProject> hlsCusAbsProjectList = new ArrayList<>();
        hlsCusAbsProjectList.add(hlsCusAbsProject);
        databaseLockProvider.lock(hlsCusAbsProject);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "ABS_TRANSFER_WFL");
        activitiStartService.start(request, hlsCusAbsProjectList, params);
        *//*流程启动后更新单据状态*//*
//        hlsCusAbsProject.setTransferStatus(STATUS_APPROVING);
        self().updateByPrimaryKeySelective(request, hlsCusAbsProject);

        *//*发消息*//*
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + hlsCusAbsProject.getProjectName() + "资产转让款项收取审核" + hlsCusAbsProject.getProjectNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "资产转让款项收取审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(request, hlsCusAbsProject.getProjectId(), hlsCusAbsProject.getDocumentCategory(), hlsCusAbsProject.getDocumentType(), "ABS", EVENT_CODE_ABS_PROJECT_WFL, "P2D", paramsEvent);
        return hlsCusAbsPkg;
    }*/


    @Override
    public List<HlsCusAbsProject> selectProjectPackageData(IRequest request, HlsCusAbsProject hlsCusAbsProject, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return hlsCusAbsProjectMapper.selectProjectPackageData(hlsCusAbsProject);
    }


    @Override
    public void cancelAbsProject(IRequest request, HlsCusAbsProject hlsCusAbsProject) throws HlsCusException {

        int count = hlsCusAbsProjectMapper.selectProductCountByProjectId(hlsCusAbsProject.getProjectId());

        if(count>0){
            throw new  HlsCusException("该立项被发债产品引用，不可作废");
        }

        hlsCusAbsProject.setProjectStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
        hlsCusAbsProjectMapper.updateByPrimaryKeySelective(hlsCusAbsProject);
    }
}

