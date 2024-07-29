//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.HlsCusConContractPaymentPtService;
import com.hand.hls.cont.service.IDocFileTempletRuleService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import com.hand.hls.csh.service.ICshPaymentAttachmentService;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.eft.mapper.HlsCusFundTransferListMapper;
import com.hand.hls.eft.service.HlsCusFundTransferListService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.hls.mapper.HlsCusFundingPlanMapper;
import com.hand.hls.hls.service.IFundingPlanService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectInsureService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.mapper.FndOrgUnitMapper;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.utils.HlsCusCheckNull;
import com.hand.hls.utils.HlsCusDownloadDocxUtil;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.lang.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class CshPaymentReqHdServiceImpl extends BaseServiceImpl<HlsCusCshPaymentReqHd> implements CshPaymentReqHdService {
    public static final String ABANDON = "ABANDON";
    public static final String ABANDONED = "ABANDONED";
    @Autowired
    HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    HlsCusConContractPaymentPtService hlsCusConContractPaymentPtService;
    @Autowired
    IProjectCreditConditionService iProjectCreditConditionService;
    @Autowired
    HlsCusPrjProjectInsureService hlsCusPrjProjectInsureService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusFundingPlanLnMapper hlsCusFundingPlanLnMapper;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;
    @Autowired
    private HlsCusFundTransferListMapper fundTransferListMapper;
    @Autowired
    private HlsCusFundTransferListService hlsCusFundTransferListService;
    @Autowired
    private IFundingPlanService fundingPlanService;
    @Autowired
    private HlsCusFundingPlanMapper hlsCusFundingPlanMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusCshPaymentReqDtMapper hlsCusCshPaymentReqDtMapper;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private HlsCusPrjProjectSupplementMapper hlsCusPrjProjectSupplementMapper;

    @Autowired
    private IDocFileTempletRuleService docFileTempletRuleService;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private HlsCusBpMasterBankAccountMapper hlsCusBpMasterBankAccountMapper;
    @Autowired
    private FndOrgUnitMapper fndOrgUnitMapper;
    @Autowired
    private ProjectCreditConditionMapper projectCreditConditionMapper;
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private CshPaymentAttachmentMapper cshPaymentAttachmentMapper;
    @Autowired
    private ICshPaymentAttachmentService cshPaymentAttachmentService;

    @Autowired
    private PrjProjectApprovalMapper approvalMapper;
    @Autowired
    private ProjectApprovalConditionMapper conditionMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    /**
     * 工作流状态 新建
     */
    private static final String NEW = "NEW";
    /**
     * 工作流状态 驳回
     */
    private static final String REJECTED = "REJECTED";

    /**
     * 工作流状态 审批中
     */
    private static final String APPROVING = "APPROVING";

    /**
     * 模板表
     */
    private static final String FILE_TEMPLET_TABLE = "hls_doc_file_templet";
    /**
     * fnd_atm_attachment的sourceType属性
     */
    private static final String SOURCE_TYPE_CODE_ATTACHMENT = "fnd_atm_attachment_multi";
    /**
     * 编码UTF-8
     */
    private static final String ENC_UTF_8 = "UTF-8";


    /**
     * 工作流状态 审批通过
     */
    private static final String APPROVED = "APPROVED";
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;

    @Autowired
    HlsDocFileTempletMapper hlsDocFileTempletMapper;

    @Autowired
    FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    FndAttachmentMapper fndAttachmentMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public CshPaymentReqHdServiceImpl() {
    }

    @Override
    public List<HlsCusCshPaymentReqHd> queryCshPaymentReqHd(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.hlsCusCshPaymentReqHdMapper.queryCshPaymentReqHd(hlsCusCshPaymentReqHd);
    }

    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;

    @Override
    public String getCodeValue(IRequest requestContext) {
        Map<String, String> params = new HashMap<String, String>();
        return codingRuleValuesService.getCodeRuleValue(requestContext, "CSH_PAYMENT_REQ", "PAYMENT_REQ", "PAYMENT_REQ", params);
    }

    @Override
    public HlsCusCshPaymentReqHd create(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {
        HlsCusFundingPlan hlsCusFundingPlan = hlsCusFundingPlanMapper.selectByPrimaryKey(hlsCusCshPaymentReqHd.getFundingPlanId());
        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusFundingPlan.getContractId());
        hlsCusCshPaymentReqHd.setPaymentReqStatus("NEW");
        hlsCusCshPaymentReqHd.setTransferStatus("NEW");
        hlsCusCshPaymentReqHd.setDocumentCategory("CSH_PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setDocumentType("PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setCompanyId(hlsCusConContract.getCompanyId());
        hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
        String value = getCodeValue(iRequest);
        hlsCusCshPaymentReqHd.setPaymentReqNumber(value);
        hlsCusCshPaymentReqHd.setPaymentReqStatus("NEW");
        hlsCusCshPaymentReqHd.setCurrency(hlsCusConContract.getCurrency());
        hlsCusCshPaymentReqHd.setSourceDocId(hlsCusCshPaymentReqHd.getProjectId());
        hlsCusCshPaymentReqHd.setSourceDocType("CON_CONTRACT");
        hlsCusCshPaymentReqHd.setCreatedBy(iRequest.getUserId());
        hlsCusCshPaymentReqHd.setCreationDate(new Date());
        hlsCusCshPaymentReqHd = this.insertSelective(iRequest, hlsCusCshPaymentReqHd);

        //插入付款行
        HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
        hlsCusFundingPlanLn.setFundingPlanId(hlsCusFundingPlan.getFundingPlanId());
        List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.selectPaymentPlanInfo(hlsCusFundingPlanLn);


        for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(i).getPlanId());
            conContractCashflow.setProcessStatus("PAYMENT_APPROVING");
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);

            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();

            hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            hlsCusCshPaymentReqLn.setSourceDocCategory("CON_CONTRACT");
            hlsCusCshPaymentReqLn.setSourceDocId(hlsCusCshPaymentReqHd.getSourceDocId());
            hlsCusCshPaymentReqLn.setSourceDocLineId(hlsCusFundingPlanLns.get(i).getPlanId());
            hlsCusCshPaymentReqLn.setFundingPlanLnId(hlsCusFundingPlanLns.get(i).getFundingPlanLnId());
            //hlsCusCshPaymentReqLn.setAmount(hlsCusFundingPlanLns.get(i).getDueAmount());
            hlsCusCshPaymentReqLn.setAmount(hlsCusFundingPlanLns.get(i).getRemainingPayableAmount());

            hlsCusCshPaymentReqLn.setPaymentMethod(hlsCusFundingPlanLns.get(i).getPaymentMethod());
//            hlsCusCshPaymentReqLn.setBpId(hlsCusFundingPlanLns.get(i).getBpId());
//            hlsCusCshPaymentReqLn.setBpBankAccountId(hlsCusFundingPlanLns.get(i).getBankAccountId());
//            hlsCusCshPaymentReqLn.setBpBankAccountName(hlsCusFundingPlanLns.get(i).getBankAccountName());
//            hlsCusCshPaymentReqLn.setBpBankAccountNum(hlsCusFundingPlanLns.get(i).getBankAccountNum());
//            hlsCusCshPaymentReqLn.setBpBankBranchName(hlsCusFundingPlanLns.get(i).getBankBranchName());
//            hlsCusCshPaymentReqLn.setBpBankName(hlsCusFundingPlanLns.get(i).getBankName());
            hlsCusCshPaymentReqLn.setDeductAmount(hlsCusFundingPlanLns.get(i).getDeductAmount());
            hlsCusCshPaymentReqLn.setCurrency(hlsCusCshPaymentReqHd.getCurrency());
            hlsCusCshPaymentReqLn.setExchange(hlsCusFundingPlanLns.get(i).getExchange());
            hlsCusCshPaymentReqLn.setIfFromContract("Y");
            cshPaymentReqLnService.insertSelective(iRequest, hlsCusCshPaymentReqLn);

        }

        return hlsCusCshPaymentReqHd;
    }

    @Override
    public HlsCusCshPaymentReqHd save(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusCshPaymentReqHd.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        if (hlsCusCshPaymentReqHd.getPaymentReqId() == null) {
            hlsCusCshPaymentReqHd.setDocumentCategory("CSH_PAYMENT_REQ");
            hlsCusCshPaymentReqHd.setDocumentType("PAYMENT_REQ");
            hlsCusCshPaymentReqHd.setCompanyId(hlsCusPrjProject.getCompanyId());
            hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
            String value = getCodeValue(iRequest);
            hlsCusCshPaymentReqHd.setPaymentReqNumber(value);
            hlsCusCshPaymentReqHd.setPaymentReqStatus("NEW");
            hlsCusCshPaymentReqHd.setCurrency(hlsCusPrjProject.getCurrency());
            hlsCusCshPaymentReqHd.setSourceDocId(hlsCusCshPaymentReqHd.getProjectId());
            hlsCusCshPaymentReqHd.setSourceDocType("CON_CONTRACT");
            hlsCusCshPaymentReqHd.setCreatedBy(iRequest.getUserId());
            hlsCusCshPaymentReqHd.setCreationDate(new Date());
            hlsCusCshPaymentReqHd = this.insertSelective(iRequest, hlsCusCshPaymentReqHd);
        } else {
            hlsCusCshPaymentReqHd = this.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqHd);
        }
        if (hlsCusCshPaymentReqHd.getHlsCusConContractPaymentPtList() != null) {
            for (ProjectCreditCondition projectCreditCondition : hlsCusCshPaymentReqHd.getHlsCusConContractPaymentPtList()) {
                if (projectCreditCondition.getCreditConditionId() != null) {
                    iProjectCreditConditionService.updateByPrimaryKeySelective(iRequest, projectCreditCondition);
                }
            }
        }
        if (hlsCusCshPaymentReqHd.getHlsCusPrjProjectInsureList() != null) {
            for (HlsCusPrjProjectInsure hlsCusPrjProjectInsure : hlsCusCshPaymentReqHd.getHlsCusPrjProjectInsureList()) {
                if (hlsCusPrjProjectInsure.getInsureId() != null) {
                    hlsCusPrjProjectInsureService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProjectInsure);
                } else {
                    hlsCusPrjProjectInsureService.insertSelective(iRequest, hlsCusPrjProjectInsure);
                }
            }
        }
        if (hlsCusCshPaymentReqHd.getHlsCusPrjProjectAttachmentList() != null) {
            for (HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment : hlsCusCshPaymentReqHd.getHlsCusPrjProjectAttachmentList()) {
                if (hlsCusPrjProjectAttachment.getProjectAttachmentId() != null) {
                    hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProjectAttachment);
                } else {
                    hlsCusPrjProjectAttachmentService.insertSelective(iRequest, hlsCusPrjProjectAttachment);
                }
            }
        }
        if (hlsCusCshPaymentReqHd.getHlsCusCshPaymentReqLnList() != null) {
            for (HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn : hlsCusCshPaymentReqHd.getHlsCusCshPaymentReqLnList()) {
                if (hlsCusCshPaymentReqLn.getPaymentReqLnId() != null) {
                    if ("Y".equals(hlsCusCshPaymentReqLn.getIfFromContract())) {
                        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                        hlsCusPrjProjectBp.setProjectId(hlsCusCshPaymentReqLn.getProjectId());
                        hlsCusPrjProjectBp.setBpId(hlsCusCshPaymentReqLn.getBpId());
                        hlsCusPrjProjectBp.setBpType(hlsCusCshPaymentReqLn.getBpType());
                        List<HlsCusPrjProjectBp> hlsCusPrjProjectBps = hlsCusPrjProjectBpMapper.select(hlsCusPrjProjectBp);
                        if (hlsCusPrjProjectBps.size() == 1 && !HlsCusCheckNull.isNull(hlsCusPrjProjectBps.get(0).getBankAccountId())) {
                            HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                            hlsCusBpMasterBankAccount.setBankAccountId(hlsCusPrjProjectBps.get(0).getBankAccountId());
                            hlsCusBpMasterBankAccount = hlsCusBpMasterBankAccountMapper.selectByPrimaryKey(hlsCusBpMasterBankAccount);
                            hlsCusCshPaymentReqLn.setBpBankAccountId(hlsCusBpMasterBankAccount.getBankAccountId());
                            hlsCusCshPaymentReqLn.setBpBankAccountName(hlsCusBpMasterBankAccount.getBankAccountName());
                            hlsCusCshPaymentReqLn.setBpBankAccountNum(hlsCusBpMasterBankAccount.getBankAccountNum());
                            hlsCusCshPaymentReqLn.setBpBankBranchName(hlsCusBpMasterBankAccount.getBankFullName());
                        } else {
                            hlsCusCshPaymentReqLn.setBpBankAccountId(-1L);
                            hlsCusCshPaymentReqLn.setBpBankAccountName("");
                            hlsCusCshPaymentReqLn.setBpBankAccountNum("");
                            hlsCusCshPaymentReqLn.setBpBankBranchName("");
                        }
                    }
                    cshPaymentReqLnService.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqLn);
                }
            }
        }

//        HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
//        hlsCusFundingPlanLn.setFundingPlanId(hlsCusCshPaymentReqHd.getFundingPlanId());
//        List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.selectPaymentPlanInfo(hlsCusFundingPlanLn);
//
//        HlsCusCshPaymentReqLn cusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
//        cusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
//        hlsCusCshPaymentReqLnMapper.deleteByReqId(cusCshPaymentReqLn);
//
//        for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
//            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
//            conContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(i).getPlanId());
//            conContractCashflow.setProcessStatus("PAYMENT_APPROVING");
//            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);
//
//            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
//
//            hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
//            hlsCusCshPaymentReqLn.setSourceDocCategory("CON_CONTRACT");
//            hlsCusCshPaymentReqLn.setSourceDocId(hlsCusCshPaymentReqHd.getContractId());
//            hlsCusCshPaymentReqLn.setSourceDocLineId(hlsCusFundingPlanLns.get(i).getPlanId());
//            //hlsCusCshPaymentReqLn.setAmount(hlsCusFundingPlanLns.get(i).getDueAmount());
//            hlsCusCshPaymentReqLn.setAmount(hlsCusFundingPlanLns.get(i).getRemainingPayableAmount());
//
//            hlsCusCshPaymentReqLn.setPaymentMethod(hlsCusFundingPlanLns.get(i).getPaymentMethod());
//            hlsCusCshPaymentReqLn.setBpId(hlsCusFundingPlanLns.get(i).getBpId());
//            hlsCusCshPaymentReqLn.setBpBankAccountId(hlsCusFundingPlanLns.get(i).getBankAccountId());
//            hlsCusCshPaymentReqLn.setBpBankAccountName(hlsCusFundingPlanLns.get(i).getBankAccountName());
//            hlsCusCshPaymentReqLn.setBpBankAccountNum(hlsCusFundingPlanLns.get(i).getBankAccountNum());
//            hlsCusCshPaymentReqLn.setBpBankName(hlsCusFundingPlanLns.get(i).getBankName());
//            cshPaymentReqLnService.insertSelective(iRequest, hlsCusCshPaymentReqLn);
//
//        }

        return hlsCusCshPaymentReqHd;
    }

    public void dateCheck(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws ResMessageException {
        /**
         * 新建时校验
         */
		/*if(HlsCusCheckNull.isNull(hlsCusCshPaymentReqHd.getPaymentReqId())){
			HlsCusCshPaymentReqHd cusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
			cusCshPaymentReqHd.setPaymentReqId(hlsCusCshPaymentReqHd.getFundingPlanId());
			List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds =  hlsCusCshPaymentReqHdMapper.select(cusCshPaymentReqHd);
			if(!hlsCusCshPaymentReqHds.isEmpty()) {
				Long count = hlsCusCshPaymentReqHds.stream().filter(item -> NEW.equals(item.getStatus()) || APPROVING.equals(item.getStatus())).count();
				if (count > 0) {
					throw new ResMessageException("已经创建了申请,无需重复创建!");
				}
			}
		}*/
        /**
         * 提交时校验
         */

        HlsCusCshPaymentReqHd cusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cusCshPaymentReqHd.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        hlsCusCshPaymentReqHd = hlsCusCshPaymentReqHdMapper.selectByPrimaryKey(cusCshPaymentReqHd);
        if (hlsCusCshPaymentReqHd.getTransferStatus() != null) {
            if (APPROVED.equals(hlsCusCshPaymentReqHd.getTransferStatus()) || APPROVING.equals(hlsCusCshPaymentReqHd.getTransferStatus())) {
                throw new ResMessageException("已经提交了申请,无需重复提交!");
            }
        }
        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        List<HlsCusCshPaymentReqHd> cshPaymentReqHds = hlsCusCshPaymentReqHdMapper.queryForTransfer(cshPaymentReqHd);
        cshPaymentReqHd = cshPaymentReqHds.get(0);
        HlsCusFundTransferList hlsCusFundTransferList = new HlsCusFundTransferList();
        hlsCusFundTransferList.setSourceDocId(hlsCusCshPaymentReqHd.getPaymentReqId());
        List<HlsCusFundTransferList> fundTransferLists = fundTransferListMapper.queryTransferListDetail(hlsCusFundTransferList);
        Double amount = 0D;
        Double noteAmount = 0D;
        for (int i = 0; i < fundTransferLists.size(); i++) {
            amount = HlsCusMathUtil.add(amount, fundTransferLists.get(i).getPaymentAmount());
            if ("NOTE".equals(fundTransferLists.get(i).getPaymentMethod())) {
                noteAmount = HlsCusMathUtil.add(noteAmount, fundTransferLists.get(i).getPaymentAmount());
            }
        }

        if (!amount.equals(HlsCusMathUtil.sub(cshPaymentReqHd.getLoanTotalAmount(), cshPaymentReqHd.getDeductTotalAmount()))) {
            throw new ResMessageException("放款总额减去抵扣总额应等于列上所有金额的和！");

        }
        if (!noteAmount.equals(cshPaymentReqHd.getPayNoteAmount())) {
            throw new ResMessageException("承兑汇票总额应等于列上所有支付方式为“票据”的总额！");
        }


    }

    private void approveWfl(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws ResMessageException {

        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = hlsCusCshPaymentReqHdMapper.queryForTransfer(hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = hlsCusCshPaymentReqHds.get(0);
        if (APPROVED.equals(hlsCusCshPaymentReqHd.getTransferStatus()) || APPROVING.equals(hlsCusCshPaymentReqHd.getTransferStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }

        FndOrgUnit fndOrgUnit = fndOrgUnitMapper.selectByPrimaryKey(hlsCusCshPaymentReqHd.getHostUnitId());
        String ifAviation = "N";
        if ("BUSINESS_DEPT_AVIATION".equals(fndOrgUnit.getUnitCode())) {
            ifAviation = "Y";
        }
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new ResMessageException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        List<HlsCusCshPaymentReqHd> cs = new ArrayList<>();
        cs.add(hlsCusCshPaymentReqHd);
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "MONEY_TRANSFERS_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "MONEY_TRANSFERS_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "MONEY_TRANSFERS");
        params.put(IActivitiCommonService.BUSINESS_KEY, hlsCusCshPaymentReqHd.getPaymentReqId());
        params.put("documentCategory", "FUND_TRANSFER");
        params.put("hlsCusCshPaymentReqHd", JSON.toJSONString(hlsCusCshPaymentReqHd));
        params.put("documentName", hlsCusCshPaymentReqHd.getContractName());
        params.put("documentNumber", hlsCusCshPaymentReqHd.getPaymentNumber());
        params.put("projectId", hlsCusCshPaymentReqHd.getProjectId());
        params.put("contractId", hlsCusCshPaymentReqHd.getContractId());
        params.put("fundingPlanId", hlsCusCshPaymentReqHd.getFundingPlanId());
        params.put("startUserName", iRequest.getUserName());
        params.put("ifAviation", ifAviation);

        HlsCusEmployee managerAssign = new HlsCusEmployee();
        managerAssign.setEmployeeId(hlsCusCshPaymentReqHd.getEmployeeId());
        managerAssign.setCompanyId(hlsCusCshPaymentReqHd.getCompanyId());


        params.put("employeeManagerAssignsId", hlsCusEmployeeMapper.selectEmployeeAssignIdByEmployeeId(managerAssign).get(0).getEmployeeAssignId());
        params.put("unitId", hlsCusCshPaymentReqHd.getHostUnitId());
        params.put("companyId", iRequest.getCompanyId());

        Date now = new Date();
        HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        hlsCusPrjProjectSupplement.setProjectId(hlsCusCshPaymentReqHd.getRefProjectId());
        hlsCusPrjProjectSupplement.setSuppleStatus(APPROVED);
        List<HlsCusPrjProjectSupplement> hlsCusPrjProjectSupplements = hlsCusPrjProjectSupplementMapper.select(hlsCusPrjProjectSupplement);
        Date fromDate;

        if (hlsCusPrjProjectSupplements.size() > 0) {
            //根据提交时间排序
            hlsCusPrjProjectSupplements.stream().sorted(Comparator.comparing(HlsCusPrjProjectSupplement::getSuppleSubmitDate).reversed());
            LocalDate localDateFrom = DateToLocaleDate(hlsCusPrjProjectSupplements.get(0).getApproveDate());
            fromDate = LocalDateToDate(localDateFrom.plusDays(180));
        } else {
            LocalDate localDateFrom = DateToLocaleDate(hlsCusCshPaymentReqHd.getCreditPeriodFrom());
            fromDate = LocalDateToDate(localDateFrom.plusDays(180));
        }
        String dueDiligenceFlag = "N";
        if (now.compareTo(fromDate) > 0) {
            dueDiligenceFlag = "Y";
        } else {
            dueDiligenceFlag = "N";
        }
        params.put("dueDiligenceFlag", dueDiligenceFlag);

        activitiStartService.start(iRequest, cs, params);

        HlsCusCshPaymentReqHd cusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cusCshPaymentReqHd.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        cusCshPaymentReqHd.setTransferStatus(APPROVING);
        this.updateByPrimaryKeySelective(iRequest, cusCshPaymentReqHd);

        HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
        hlsCusFundingPlanLn.setFundingPlanId(hlsCusCshPaymentReqHd.getFundingPlanId());
        List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.selectPaymentPlanInfo(hlsCusFundingPlanLn);

        for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(i).getPlanId());
            conContractCashflow.setProcessStatus("FUND_APPROVING");
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);
        }
        List<HlsCusCshPaymentReqHd> cusCshPaymentReqHdList = hlsCusCshPaymentReqHdMapper.queryContractIdByFundPlan(cusCshPaymentReqHd);
        if (cusCshPaymentReqHdList.size() == 1) {
            if (cusCshPaymentReqHdList.get(0).getContractId() != null) {
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(cusCshPaymentReqHdList.get(0).getContractId());
                HlsCusConContract hlsCusConContractNew = hlsCusConContractService.selectByPrimaryKey(iRequest, hlsCusConContract);
                if (!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                    hlsCusConContractNew.setContractStatus("FUNDING");
                }
                hlsCusConContractService.updateByPrimaryKeySelective(iRequest, hlsCusConContractNew);
            }
        }

    }

    @Override
    public void checkSupplement(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws ResMessageException {
        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = hlsCusCshPaymentReqHdMapper.queryForTransfer(hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = hlsCusCshPaymentReqHds.get(0);
        Date now = new Date();
        HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        hlsCusPrjProjectSupplement.setProjectId(hlsCusCshPaymentReqHd.getRefProjectId());
        hlsCusPrjProjectSupplement.setSuppleStatus(APPROVED);
        List<HlsCusPrjProjectSupplement> hlsCusPrjProjectSupplements = hlsCusPrjProjectSupplementMapper.select(hlsCusPrjProjectSupplement);
        Date fromDate;

        if (hlsCusPrjProjectSupplements.size() > 0) {
            //根据提交时间排序
            hlsCusPrjProjectSupplements.stream().sorted(Comparator.comparing(HlsCusPrjProjectSupplement::getSuppleSubmitDate).reversed());
            LocalDate localDateFrom = DateToLocaleDate(hlsCusPrjProjectSupplements.get(0).getApproveDate());
            fromDate = LocalDateToDate(localDateFrom.plusDays(180));
        } else {
            LocalDate localDateFrom = DateToLocaleDate(hlsCusCshPaymentReqHd.getCreditPeriodFrom());
            fromDate = LocalDateToDate(localDateFrom.plusDays(180));
        }
        if (now.compareTo(fromDate) > 0) {
            throw new ResMessageException("由于授信至今已超180天，请前往【项目尽调撰写】功能进行补充尽调！");
        }
    }

    public static LocalDate DateToLocaleDate(Date date) {

        Instant instant = date.toInstant();

        ZoneId zoneId = ZoneId.systemDefault();

        return instant.atZone(zoneId).toLocalDate();

    }

    public static Date LocalDateToDate(LocalDate localDate) {

        ZoneId zoneId = ZoneId.systemDefault();

        ChronoZonedDateTime<LocalDate> zonedDateTime = localDate.atStartOfDay(zoneId);

        return Date.from(zonedDateTime.toInstant());

    }

    @Override
    public List<HlsCusCshPaymentReqHd> transferSubmit(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws ResMessageException, ParameterNullException {
        //状态检查
        dateCheck(hlsCusCshPaymentReqHd);
        //启动工作流
        approveWfl(iRequest, hlsCusCshPaymentReqHd);

        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<>();
        hlsCusCshPaymentReqHds.add(hlsCusCshPaymentReqHd);
        return hlsCusCshPaymentReqHds;

    }

    @Override
    public List<HlsCusCshPaymentReqHd> abandonTransfer(IRequest iRequest, List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds) throws ResMessageException, ParameterNullException {
        for (int i = 0; i < hlsCusCshPaymentReqHds.size(); i++) {
            if (!"NEW".equals(hlsCusCshPaymentReqHds.get(i).getTransferStatus())
                    && !"REJECTED".equals(hlsCusCshPaymentReqHds.get(i).getTransferStatus())
                    && null != hlsCusCshPaymentReqHds.get(i).getTransferStatus()) {
                throw new ResMessageException("该单据已被审批，不能作废！");

            }
            HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
            hlsCusCshPaymentReqHd.setPaymentReqId(hlsCusCshPaymentReqHds.get(i).getPaymentReqId());
            hlsCusCshPaymentReqHd.setPaymentReqStatus(ABANDON);
            hlsCusCshPaymentReqHd.setTransferStatus(ABANDON);
            this.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqHd);

            //头寸报备状态
            HlsCusFundingPlan hlsCusFundingPlan = new HlsCusFundingPlan();
            hlsCusFundingPlan.setFundingPlanId(hlsCusCshPaymentReqHds.get(i).getFundingPlanId());
            hlsCusFundingPlan.setStatus(ABANDON);
            fundingPlanService.updateByPrimaryKeySelective(iRequest, hlsCusFundingPlan);

            HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
            hlsCusFundingPlanLn.setFundingPlanId(hlsCusCshPaymentReqHds.get(i).getFundingPlanId());
            List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.select(hlsCusFundingPlanLn);
            for (int j = 0; j < hlsCusFundingPlanLns.size(); j++) {
                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                hlsCusConContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(j).getPlanId());
                hlsCusConContractCashflow.setFundingPlanStatus(ABANDONED);
                hlsCusConContractCashflow.setProcessStatus(ABANDONED);
                hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, hlsCusConContractCashflow);

                //抵扣
                HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt = new HlsCusCshPaymentReqDt();
                hlsCusCshPaymentReqDt.setFundingPlanLnId(hlsCusFundingPlanLns.get(i).getFundingPlanLnId());
                List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDts = hlsCusCshPaymentReqDtMapper.select(hlsCusCshPaymentReqDt);
                for (int k = 0; k < hlsCusCshPaymentReqDts.size(); k++) {
                    HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                    conContractCashflow.setCashflowId(hlsCusCshPaymentReqDts.get(k).getSourceDocLineId());
                    conContractCashflow.setFundingPlanStatus(ABANDONED);
                    conContractCashflow.setWhetherDeduct("N");
                    hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);
                }
            }
        }
        return hlsCusCshPaymentReqHds;
    }

    @Override
    public void saveEftTransfer(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws ResMessageException {
        if (hlsCusCshPaymentReqHd.getLeaseAccountDate() != null) {
            if (hlsCusCshPaymentReqHd.getLeaseAccountDate().compareTo(hlsCusCshPaymentReqHd.getProposedLaunchDate()) < 0) {
                throw new ResMessageException("起租日（财务）必须大于等于拟投放日期！");

            }
        }
        if (hlsCusCshPaymentReqHd.getFinLoanInitialLease() != null) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getContractId());
            hlsCusConContract.setFinLoanInitialLease(hlsCusCshPaymentReqHd.getFinLoanInitialLease());
            hlsCusConContractService.updateByPrimaryKeySelective(iRequest, hlsCusConContract);
        }
        if (hlsCusCshPaymentReqHd.getLeaseAccountDate() != null) {
            HlsCusFundingPlan hlsCusFundingPlan = hlsCusFundingPlanMapper.selectByPrimaryKey(hlsCusCshPaymentReqHd.getFundingPlanId());
            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            hlsCusPrjQuotation.setSourceDocumentId(hlsCusFundingPlan.getContractId());
            hlsCusPrjQuotation.setSourceDocumentCategory("CON_CONTRACT");
            List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.select(hlsCusPrjQuotation);

            if (hlsCusPrjQuotations.size() == 1) {
                HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
                prjQuotation.setQuotationId(hlsCusPrjQuotations.get(0).getQuotationId());
                prjQuotation.setSourceDocumentCategory("CON_CONTRACT");
                prjQuotation.setSourceDocumentId(hlsCusCshPaymentReqHd.getContractId());
                prjQuotation.setLeaseAccountDate(hlsCusCshPaymentReqHd.getLeaseAccountDate());
                hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotation);
            }
        }
    }

    @Override
    public void createEftTransferList(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {
        HlsCusFundTransferList fundTransferList = new HlsCusFundTransferList();
        fundTransferList.setSourceDocId(hlsCusCshPaymentReqHd.getPaymentReqId());
        List<HlsCusFundTransferList> fundTransferLists = fundTransferListMapper.select(fundTransferList);

        if (fundTransferLists.size() == 0) {
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns = hlsCusCshPaymentReqLnMapper.select(hlsCusCshPaymentReqLn);
            for (int i = 0; i < hlsCusCshPaymentReqLns.size(); i++) {
                HlsCusFundTransferList hlsCusFundTransferList = new HlsCusFundTransferList();
                hlsCusFundTransferList.setSourceDocId(hlsCusCshPaymentReqHd.getPaymentReqId());
                hlsCusFundTransferList.setSourceDocLineId(hlsCusCshPaymentReqLns.get(i).getPaymentReqLnId());
                hlsCusFundTransferList.setBpId(hlsCusCshPaymentReqLns.get(i).getBpId());
                hlsCusFundTransferList.setInBankAccountId(hlsCusCshPaymentReqLns.get(i).getBpBankAccountId());
                hlsCusFundTransferList.setInBankAccountName(hlsCusCshPaymentReqLns.get(i).getBpBankAccountName());
                hlsCusFundTransferList.setInBankAccountNum(hlsCusCshPaymentReqLns.get(i).getBpBankAccountNum());
                hlsCusFundTransferList.setInBankBranchName(hlsCusCshPaymentReqLns.get(i).getBpBankBranchName());
                hlsCusFundTransferList.setPaymentAmount(hlsCusCshPaymentReqLns.get(i).getAmount());
                hlsCusFundTransferListService.insertSelective(iRequest, hlsCusFundTransferList);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachmentMulti> contextCreateMultiple(IRequest request, String code, String paymentId, HttpServletResponse response) throws Exception {
        HlsDocFileTemplet queryTemplet = new HlsDocFileTemplet();
        queryTemplet.setTempletCode(code);
        HlsDocFileTemplet hlsDocFileTemplet = hlsDocFileTempletMapper.selectOne(queryTemplet);
        Assert.notNull(hlsDocFileTemplet, "未找到模板代码为" + code + "的模板文件");
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTableName(FILE_TEMPLET_TABLE);
        fndAttachmentMulti.setTablePkValue(hlsDocFileTemplet.getTempletId().toString());
        fndAttachmentMulti = fndAttachmentMultiMapper.selectOne(fndAttachmentMulti);
        FndAttachment sysFile = new FndAttachment();
        sysFile.setSourceTypeCode(SOURCE_TYPE_CODE_ATTACHMENT);
        sysFile.setSourcePkValue(fndAttachmentMulti.getRecordId().toString());
        sysFile = fndAttachmentMapper.selectOne(sysFile);
        int fileBackLength = 0;
        if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
            File file = new File(sysFile.getFilePath());
            if (file.exists()) {
                //先获取模板文件的大小
                int fileLength = (int) file.length();
                if (fileLength > 0) {
                    InputStream inStream = new FileInputStream(file);
                    //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
                    String copyPath = file.getPath().concat("_back_").concat(UUID.randomUUID().toString());
                    //复制模板
                    HlsCusDownloadDocxUtil.copyModel(copyPath, inStream);
                    //用输入流读取复制后的模板
                    InputStream modelIs = new FileInputStream(copyPath);
                    Map<String, Object> map = new HashMap<>();
                    map.put("templetId", hlsDocFileTemplet.getTempletId());
                    map.put("paymentId", paymentId);
                    //通过输入流构建WordprocessingMLPackage对象
                    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(modelIs);
                    //将构建的wordMLPackage对象传入方法中
                    wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, request, map);
                    //将替换后的合同文本保存到服务器上作为备份
                    wordMLPackage.save(new File(copyPath));

                    fileBackLength = (int) new File(copyPath).length();

                    //1.保存文件
                    contextCreateMultipleSave(request, code, paymentId, sysFile, copyPath, fileBackLength, response);

                    //2.返回结果
                    FndAttachmentMulti fndAttachmentMultiNew = new FndAttachmentMulti();
                    fndAttachmentMultiNew.setTablePkValue(paymentId);
                    fndAttachmentMultiNew.setTableName(code);
                    List<FndAttachmentMulti> fndAttachmentMultiList = new ArrayList<>();
                    fndAttachmentMultiList = fndAttachmentMultiService.selectSelective(request, fndAttachmentMultiNew);

                    return fndAttachmentMultiList;

                }
            } else {
                throw new HlsCusException("文件模版不存在");
            }
        }
        return null;
    }

    @Override
    public void contextCreateMultipleSave(IRequest request, String code, String paymentId, FndAttachment sysFile, String copyPath, int fileLength, HttpServletResponse response) throws Exception {
        //查询是否存在过
        FndAttachmentMulti condition = new FndAttachmentMulti();
        condition.setTablePkValue(paymentId);
        condition.setTableName(code);
        List<FndAttachmentMulti> list = fndAttachmentMultiService.selectSelective(request, condition);

        //保存生成文件
        FndAttachment conDocFile = null;
        FndAttachmentMulti conDocFileMul = null;
        //已经生成过直接修改路径后保存
        if (!list.isEmpty()) {
            conDocFileMul = list.get(0);
            FndAttachment fndCondition = new FndAttachment();
            fndCondition.setSourceTypeCode("fnd_atm_attachment_multi");
            fndCondition.setSourcePkValue(conDocFileMul.getRecordId().toString());
            conDocFile = fndAttachmentService.selectSelective(request, fndCondition).get(0);
            //上传
            conDocFile.setFilePath(copyPath);
            conDocFile.setFileSize(((Integer) fileLength).longValue());
            fndAttachmentService.updateByPrimaryKeySelective(request, conDocFile);
        } else {
            conDocFile = new FndAttachment();
            String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
            conDocFile.setFileName(fileName);
            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
            conDocFileMulti.setTableName(code);
            conDocFileMulti.setTablePkValue(paymentId);
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            conDocFileMulti.setCreatedBy(request.getUserId());
            conDocFileMulti.setCreationDate(new Date());
            conDocFileMulti.setLastUpdateDate(new Date());
            conDocFileMulti.setLastUpdatedBy(request.getUserId());
            fndAttachmentMultiService.insertSelective(request, conDocFileMulti);
            conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
            conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
            //上传
            conDocFile.setFilePath(copyPath);
            conDocFile.setFileTypeCode(".docx");
            conDocFile.setFileSize(((Integer) fileLength).longValue());
            conDocFile.setCreationDate(new Date());
            conDocFile.setCreatedBy(request.getUserId());
            conDocFile.setLastUpdateDate(new Date());
            conDocFile.setLastUpdatedBy(request.getUserId());
            fndAttachmentService.insertSelective(request, conDocFile);
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            fndAttachmentMultiService.updateByPrimaryKey(request, conDocFileMulti);
        }
    }

    @Override
    public HlsCusCshPaymentReqHd cshHdCreate(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {

/*
        HlsCusFundingPlan hlsCusFundingPlan =hlsCusFundingPlanMapper.selectByPrimaryKey(hlsCusCshPaymentReqHd.getFundingPlanId());
*/
/*
        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusFundingPlan.getContractId());
*/


        hlsCusCshPaymentReqHd.setPaymentReqStatus("NEW");
        hlsCusCshPaymentReqHd.setTransferStatus("NEW");
        hlsCusCshPaymentReqHd.setDocumentCategory("CSH_PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setDocumentType("PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setCompanyId(hlsCusCshPaymentReqHd.getCompanyId());
        hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setPaymentType("PAYMENT");
        String value = getCodeValue(iRequest);
        hlsCusCshPaymentReqHd.setPaymentReqNumber(value);
        hlsCusCshPaymentReqHd.setPaymentReqStatus("NEW");
        hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
        hlsCusCshPaymentReqHd.setUnitId(Long.valueOf(iRequest.getAttribute("unitId")));
        hlsCusCshPaymentReqHd.setCurrency(hlsCusCshPaymentReqHd.getCurrency());
        hlsCusCshPaymentReqHd.setSourceDocId(hlsCusCshPaymentReqHd.getProjectId());
        hlsCusCshPaymentReqHd.setSourceContractId(hlsCusCshPaymentReqHd.getContractId());
        hlsCusCshPaymentReqHd.setSourceDocType("CON_CONTRACT");
        hlsCusCshPaymentReqHd.setCreatedBy(iRequest.getUserId());
        hlsCusCshPaymentReqHd.setCreationDate(new Date());
        hlsCusCshPaymentReqHd.setPaymentReqDate(new Date());
        hlsCusCshPaymentReqHd.setSendFlag("N");

        hlsCusCshPaymentReqHd.setFinanceAmount(hlsCusCshPaymentReqHd.getFinanceAmount());
        hlsCusCshPaymentReqHd.setProjectName(hlsCusCshPaymentReqHd.getProjectName());
        hlsCusCshPaymentReqHd.setProposedLaunchDate(hlsCusCshPaymentReqHd.getProposedLaunchDate());
        hlsCusCshPaymentReqHd.setSumToufangAmount(hlsCusCshPaymentReqHd.getSumToufangAmount());
        hlsCusCshPaymentReqHd.setContractBalance(hlsCusCshPaymentReqHd.getContractBalance());
        hlsCusCshPaymentReqHd.setContractCurrency(hlsCusCshPaymentReqHd.getContractCurrency());
        hlsCusCshPaymentReqHd.setLoanTotalAmount(hlsCusCshPaymentReqHd.getLoanTotalAmount());

        hlsCusCshPaymentReqHd.setEmployeeId(hlsCusCshPaymentReqHd.getEmployeeId());
        hlsCusCshPaymentReqHd.setAuthorityRuleString(hlsCusCshPaymentReqHd.getAuthorityRuleString());
        hlsCusCshPaymentReqHd = this.insertSelective(iRequest, hlsCusCshPaymentReqHd);

        Long paymentReqId = hlsCusCshPaymentReqHd.getPaymentReqId();

        CshPaymentAttachment cshPaymentAttachment = new CshPaymentAttachment();
        cshPaymentAttachment.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        List<CshPaymentAttachment> cshPaymentAttachments = cshPaymentAttachmentMapper.queryCshPaymentHdFirst(cshPaymentAttachment);
        /*if(cshPaymentAttachments.get(0).getBpType().equalsIgnoreCase("NP") && cshPaymentAttachments.get(0).getYesNo().equalsIgnoreCase("Y")) {
            List<CshPaymentAttachment> cshPaymentAttachmentsList=cshPaymentAttachmentMapper.queryPayFileFirstNp(cshPaymentAttachment);

            for (int i=0;i<cshPaymentAttachmentsList.size();i++){
                CshPaymentAttachment cshPaymentAttachmentLists  =new CshPaymentAttachment();
                cshPaymentAttachmentLists.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
                cshPaymentAttachmentLists.setDocumentName(cshPaymentAttachmentsList.get(i).getValueName());
                cshPaymentAttachmentLists.setProjectAttachmentCategory("CON_PAYMENT");
                cshPaymentAttachmentLists.setCreatedBy(iRequest.getUserId());
                cshPaymentAttachmentLists.setCreationDate(new Date());
                cshPaymentAttachmentService.insertSelective(iRequest, cshPaymentAttachmentLists);

            }

        }else if(cshPaymentAttachments.get(0).getBpType().equalsIgnoreCase("ORG")  && cshPaymentAttachments.get(0).getYesNo().equalsIgnoreCase("Y")){
            List<CshPaymentAttachment> cshPaymentAttachmentsList=cshPaymentAttachmentMapper.queryGuarantorBusinessLicense(cshPaymentAttachment);
            for (int i=0;i<cshPaymentAttachmentsList.size();i++){
                CshPaymentAttachment cshPaymentAttachmentLists  =new CshPaymentAttachment();

                cshPaymentAttachmentLists.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
                cshPaymentAttachmentLists.setDocumentName(cshPaymentAttachmentsList.get(i).getValueName());
                cshPaymentAttachmentLists.setProjectAttachmentCategory("CON_PAYMENT");
                cshPaymentAttachmentLists.setCreatedBy(iRequest.getUserId());
                cshPaymentAttachmentLists.setCreationDate(new Date());
                cshPaymentAttachmentService.insertSelective(iRequest, cshPaymentAttachmentLists);

            }
        }else{
            List<CshPaymentAttachment> cshPaymentAttachmentsList=cshPaymentAttachmentMapper.queryOther(cshPaymentAttachment);
            for (int i=0;i<cshPaymentAttachmentsList.size();i++){
                CshPaymentAttachment cshPaymentAttachmentLists  =new CshPaymentAttachment();
                cshPaymentAttachmentLists.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
                cshPaymentAttachmentLists.setDocumentName(cshPaymentAttachmentsList.get(i).getValueName());
                cshPaymentAttachmentLists.setProjectAttachmentCategory("CON_PAYMENT");
                cshPaymentAttachmentLists.setCreatedBy(iRequest.getUserId());
                cshPaymentAttachmentLists.setCreationDate(new Date());
                cshPaymentAttachmentService.insertSelective(iRequest, cshPaymentAttachmentLists);

            }
        }*/

        List<CshPaymentAttachment> cshPaymentAttachmentsList = cshPaymentAttachmentMapper.queryOther(cshPaymentAttachment);
        for (int i = 0; i < cshPaymentAttachmentsList.size(); i++) {
            CshPaymentAttachment cshPaymentAttachmentLists = new CshPaymentAttachment();
            cshPaymentAttachmentLists.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            cshPaymentAttachmentLists.setDocumentName(cshPaymentAttachmentsList.get(i).getValueName());
            cshPaymentAttachmentLists.setProjectAttachmentCategory("CON_PAYMENT");
            cshPaymentAttachmentLists.setCreatedBy(iRequest.getUserId());
            cshPaymentAttachmentLists.setCreationDate(new Date());
            cshPaymentAttachmentService.insertSelective(iRequest, cshPaymentAttachmentLists);
        }

        //获取项目信息
        HlsCusPrjProject hlsCusProject = new HlsCusPrjProject();
        hlsCusProject.setProjectId(hlsCusCshPaymentReqHd.getProjectId());
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusProject);

        //从审议获取投放前要求
        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setProjectId(hlsCusPrjProject.getRefProjectId().toString());
        prjProjectApproval.setDataClass("NORMAL");
        prjProjectApproval.setApprovalType("NORMAL");
        List<PrjProjectApproval> prjProjectApprovals = approvalMapper.select(prjProjectApproval);
        prjProjectApproval = prjProjectApprovals.get(0);

        ProjectApprovalCondition projectApprovalCondition = new ProjectApprovalCondition();
        projectApprovalCondition.setApprovalId(prjProjectApproval.getApprovalId());
        projectApprovalCondition.setApprovalType("BEFORE");
        List<ProjectApprovalCondition> projectApprovalConditions = conditionMapper.select(projectApprovalCondition);

        for (ProjectApprovalCondition approvalCondition : projectApprovalConditions) {
            ProjectCreditCondition projectCreditConditionList = new ProjectCreditCondition();
            projectCreditConditionList.setProjectId(hlsCusCshPaymentReqHd.getPaymentReqId());
            projectCreditConditionList.setConditionType(approvalCondition.getConditionType());
            projectCreditConditionList.setConditions(approvalCondition.getConditionContent());
            projectCreditConditionList.setCreatedBy(iRequest.getUserId());
            projectCreditConditionList.setCreationDate(new Date());
            iProjectCreditConditionService.insertSelective(iRequest, projectCreditConditionList);
        }

        //合同附件带出到放款
        HlsCusPrjProjectAttachment hlsCusFctProjectAttachment = new HlsCusPrjProjectAttachment();
        hlsCusFctProjectAttachment.setProjectId(hlsCusCshPaymentReqHd.getProjectId());
        hlsCusFctProjectAttachment.setProjectAttachmentCategory("CON_CONTRACT_ATT");
        List<HlsCusPrjProjectAttachment> hlsCusFctProjectAttachmentList = hlsCusPrjProjectAttachmentService.select(iRequest, hlsCusFctProjectAttachment, 1, 99999999);
        for (HlsCusPrjProjectAttachment dto : hlsCusFctProjectAttachmentList) {
            CshPaymentAttachment att = new CshPaymentAttachment();
            Map<String, String> sysFilemap = hlsBeanRefUtilService.getFieldValueMap(dto);
            hlsBeanRefUtilService.setFieldValue(att, sysFilemap);
            att.setPaymentReqId(paymentReqId);
            att.setDocumentName(dto.getDocumentName());
            att.setProjectAttachmentCategory("CON_PAYMENT");
            att.setCreatedBy(iRequest.getUserId());
            att.setCreationDate(new Date());
            cshPaymentAttachmentService.insertSelective(iRequest, att);

            //复制fnd_atm_attachment_multi
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
            fndAttachmentMulti.setTablePkValue(dto.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> multiList = fndAttachmentMultiMapper.select(fndAttachmentMulti);
            //只会有一条数据
            for (FndAttachmentMulti multi : multiList) {
                FndAttachmentMulti attachmentMulti = new FndAttachmentMulti();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(multi);
                hlsBeanRefUtilService.setFieldValue(attachmentMulti, map);
                attachmentMulti.setTablePkValue(att.getCshAttcahmentId().toString());
                attachmentMulti.setTableName("csh_payment_attachment");
                fndAttachmentMultiService.insertSelective(iRequest, attachmentMulti);
                //复制fnd_atm_attachment
                FndAttachment fndAttachment = new FndAttachment();
                fndAttachment.setAttachmentId(multi.getAttachmentId());
                fndAttachment = fndAttachmentMapper.selectByPrimaryKey(fndAttachment);
                fndAttachment.setAttachmentId(attachmentMulti.getAttachmentId());
                fndAttachment.setAttachmentId(null);
                fndAttachmentService.insert(iRequest, fndAttachment);
                attachmentMulti.setAttachmentId(fndAttachment.getAttachmentId());
                fndAttachmentMultiService.updateByPrimaryKeySelective(iRequest, attachmentMulti);
            }
        }

        return hlsCusCshPaymentReqHd;
    }

    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId, Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;
    }

    @Override
    public void cshInImport(IRequest iRequest, Long hdId, Long paymentReqId) throws ExcelException, Exception, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, (long) 2);
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
            String paymentMethod = fndInterfaceLine.getAttributes_1();
            String dueAmountLn = fndInterfaceLine.getAttributes_2();
            String seqNum = fndInterfaceLine.getAttributes_3();
            String sumDueAmount = fndInterfaceLine.getAttributes_4();
            String billType = fndInterfaceLine.getAttributes_5();
            String acceptancePeriod = fndInterfaceLine.getAttributes_6();
            String validityLc = fndInterfaceLine.getAttributes_7();
            if ("银行承兑汇票".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "BANK_ACCEPTANCE";
            }
            if ("商业承兑汇票".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "BUSINESS_ACCEPTANCE";
            }
            if ("国际信用证".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "INTERNATIONAL_CREDIT";
            }
            if ("国内信用证".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "DOMESTIC_CREDIT";
            }
            if ("其他".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "OTHER";
            }
            if ("电汇".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "LETTER_GUARANTEE";
            }
            if ("电子".equalsIgnoreCase(billType)) {
                billType = "ELECTRONIC";
            }
            if ("纸质".equalsIgnoreCase(billType)) {
                billType = "PAPER";
            }
            hlsCusCshPaymentReqLn.setPaymentReqId(paymentReqId);
            hlsCusCshPaymentReqLn.setSourceDocCategory("CON_CONTRACT");
            hlsCusCshPaymentReqLn.setIfFromContract("Y");

            hlsCusCshPaymentReqLn.setBillType(billType);
            hlsCusCshPaymentReqLn.setPaymentMethod(paymentMethod);
            hlsCusCshPaymentReqLn.setDueAmountLn(Double.parseDouble(OracleUtils.nvl(dueAmountLn, "2")));
            hlsCusCshPaymentReqLn.setSeqNum(Long.valueOf(seqNum));
            hlsCusCshPaymentReqLn.setSumDueAmount(Double.parseDouble(OracleUtils.nvl(sumDueAmount, "2")));
            // Date transactionDate = df.parse(validityLc);

            if (validityLc != null) {
                hlsCusCshPaymentReqLn.setValidityLc(validityLc);

            }
            if (acceptancePeriod != null) {
                hlsCusCshPaymentReqLn.setAcceptancePeriod(Long.valueOf(acceptancePeriod));

            }

            cshPaymentReqLnService.insertSelective(iRequest, hlsCusCshPaymentReqLn);

        }
    }

    @Override
    public List<HlsCusCshPaymentReqHd> dailyrate(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusCshPaymentReqHd> list = hlsCusCshPaymentReqHdMapper.dailyrate(hlsCusCshPaymentReqHd);
        return list;
    }

    @Override
    public HlsCusCshPaymentReqHd tariffPaymentReqCreate(IRequest iRequest, List<HlsCusConContractCashflow> tariffCashflows) throws Exception {
        if (tariffCashflows == null || tariffCashflows.size() == 0) {
            throw new HlsCusException("现金流获取失败");
        }
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqStatus("NEW");
        hlsCusCshPaymentReqHd.setTransferStatus("NEW");
        hlsCusCshPaymentReqHd.setDocumentCategory("CSH_PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setDocumentType("TARIFF_PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setCompanyId(iRequest.getCompanyId());
        hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setPaymentType("PAYMENT_TARIFF");
        hlsCusCshPaymentReqHd.setLoanType("TARIFF");
        Map<String, String> params = new HashMap<String, String>();
        String paymentReqNumber = codingRuleValuesService.getCodeRuleValue(iRequest, "CSH_PAYMENT_REQ", "TARIFF_PAYMENT_REQ", "PAYMENT_REQ", params);
        hlsCusCshPaymentReqHd.setPaymentReqNumber(paymentReqNumber);
        hlsCusCshPaymentReqHd.setPaymentReqStatus("NEW");
        hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
        hlsCusCshPaymentReqHd.setEmployeeId(Long.valueOf(iRequest.getAttribute("employeeId")));
        hlsCusCshPaymentReqHd.setUnitId(Long.valueOf(iRequest.getAttribute("unitId")));
        hlsCusCshPaymentReqHd.setCurrency("CNY");
        hlsCusCshPaymentReqHd.setSourceDocId(tariffCashflows.get(0).getProjectId());
        hlsCusCshPaymentReqHd.setSourceContractId(tariffCashflows.get(0).getContractId());
        hlsCusCshPaymentReqHd.setSourceDocType("CON_CONTRACT");
        hlsCusCshPaymentReqHd.setCreatedBy(iRequest.getUserId());
        hlsCusCshPaymentReqHd.setCreationDate(new Date());
        hlsCusCshPaymentReqHd.setPaymentReqDate(new Date());
        hlsCusCshPaymentReqHd.setSendFlag("N");
        HlsCusCshPaymentReqHd contractPaymentInfo = hlsCusCshPaymentReqHdMapper.queryContractPaymentInfo(tariffCashflows.get(0).getContractId());
        hlsCusCshPaymentReqHd.setFinanceAmount(contractPaymentInfo.getFinanceAmount());
        hlsCusCshPaymentReqHd.setProjectName(contractPaymentInfo.getProjectName());
        hlsCusCshPaymentReqHd.setSumToufangAmount(contractPaymentInfo.getSumToufangAmount());
        hlsCusCshPaymentReqHd.setContractBalance(contractPaymentInfo.getContractBalance());
        hlsCusCshPaymentReqHd.setContractCurrencyId(contractPaymentInfo.getContractCurrencyId());
        hlsCusCshPaymentReqHd.setContractCurrency(contractPaymentInfo.getContractCurrency());
        hlsCusCshPaymentReqHd = this.insertSelective(iRequest, hlsCusCshPaymentReqHd);

        //创建付款申请行表
        for (HlsCusConContractCashflow item : tariffCashflows) {
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            hlsCusCshPaymentReqLn.setSourceDocCategory("CON_CONTRACT");
            hlsCusCshPaymentReqLn.setSourceDocId(hlsCusCshPaymentReqHd.getSourceDocId());
            hlsCusCshPaymentReqLn.setSourceDocLineId(item.getCashflowId());
            hlsCusCshPaymentReqLn.setAmount(item.getDueAmount());
            hlsCusCshPaymentReqLn.setDueAmountLn(item.getDueAmount());
            hlsCusCshPaymentReqLn.setCurrency(hlsCusCshPaymentReqHd.getCurrency());
            hlsCusCshPaymentReqLn.setIfFromContract("Y");
            cshPaymentReqLnService.insertSelective(iRequest, hlsCusCshPaymentReqLn);
        }
        return hlsCusCshPaymentReqHd;
    }

    @Override
    public List<HlsCusCshPaymentReqHd> submitTariffPaymentReqWfl(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws HlsCusException {
        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<>();
        if (hlsCusCshPaymentReqHd.getPaymentReqId() != null) {
            hlsCusCshPaymentReqHd = this.selectByPrimaryKey(iRequest, hlsCusCshPaymentReqHd);
            hlsCusCshPaymentReqHds.add(hlsCusCshPaymentReqHd);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            if (ObjectUtils.isEmpty(employee)) {
                throw new HlsCusException("获取提交人失败");
            }
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);

            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", "TARIFF_PAYMENT_WFL");

            activitiStartService.start(iRequest, hlsCusCshPaymentReqHds, params);

            //修改单据状态
            hlsCusCshPaymentReqHd.setPaymentReqStatus("APPROVING");
            this.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqHd);
        }
        return hlsCusCshPaymentReqHds;
    }

    /**
     * 二期功能：零售业务付款支付首页查询
     *
     * @param iRequest
     * @param hlsCusCshPaymentReqHd
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<HlsCusCshPaymentReqHd> retailPaymentHomeQuery(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusCshPaymentReqHd> list = hlsCusCshPaymentReqHdMapper.retailPaymentHomeQuery(hlsCusCshPaymentReqHd);
        return list;
    }

    @Override
    public void tariffPaymentReqApproved(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {
        if (hlsCusCshPaymentReqHd.getPaymentReqId() != null) {
            hlsCusCshPaymentReqHd = this.selectByPrimaryKey(iRequest, hlsCusCshPaymentReqHd);
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = hlsCusCshPaymentReqLnMapper.select(hlsCusCshPaymentReqLn);
            for (HlsCusCshPaymentReqLn item : hlsCusCshPaymentReqLnList) {
                item.setBpId(hlsCusCshPaymentReqHd.getBpId());
                item.setPaymentMethod("TT");
                item.setBpBankAccountNum(hlsCusCshPaymentReqHd.getBpBankAccountNum());
                item.setBpBankAccountName(hlsCusCshPaymentReqHd.getBpBankAccountName());
                item.setBpBankBranchName(hlsCusCshPaymentReqHd.getBpBankBranchName());
                item.setBankAccountId(hlsCusCshPaymentReqHd.getBankAccountId());
                cshPaymentReqLnService.updateByPrimaryKeySelective(iRequest, item);
            }
        }
    }

    @Override
    public List<Double> queryActualPaymentAmount(HlsCusCshPaymentReqHd hlscuscshpaymentreqhd) {
        List<Double> actual_amount = new ArrayList<>();
        if (hlscuscshpaymentreqhd.getPaymentReqId() != null) {
            actual_amount.add(hlsCusCshPaymentReqHdMapper.queryActualPaymentAmount(hlscuscshpaymentreqhd.getPaymentReqId()));
        }
        return actual_amount;
    }

    @Override
    public List<Double> queryPaymentAmount(HlsCusCshPaymentReqHd hlscuscshpaymentreqhd) {
        List<Double> amount = new ArrayList<>();
        if (hlscuscshpaymentreqhd.getPaymentReqId() != null) {
            amount.add(hlsCusCshPaymentReqHdMapper.queryPaymentAmount(hlscuscshpaymentreqhd.getPaymentReqId()));
        }
        return amount;
    }



}
