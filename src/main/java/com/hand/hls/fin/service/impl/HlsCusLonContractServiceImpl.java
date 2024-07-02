package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.*;
import com.hand.hls.fin.mapper.*;
import com.hand.hls.fin.service.*;

import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.sys.service.HlsCusSysUserAuthorityTrxService;
import com.hand.hls.user.dto.LoginUserInfo;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import com.hand.hls.fin.service.HlsCusLonContractQuotationService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractServiceImpl extends BaseServiceImpl<HlsCusLonContract> implements HlsCusLonContractService {

    @Autowired
    private final static Logger logger = LoggerFactory.getLogger(HlsCusLonContractService.class);


    @Autowired
    HlsCusLonBankMapper hlsCusLonBankMapper;

    @Autowired
    HlsCusLonContractMapper lonContractMapper;

    @Autowired
    HlsCusLonContractQuotationMapper lonContractQuotationMapper;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    HlsCusCtLonContractChangeReqMapper hlsCusCtLonContractChangeReqMapper;

    @Autowired
    HlsCusLonContractQuotationService hlsCusLonContractQuotationService;

    @Autowired
    HlsCusCtLonContractRefBankInfoService hlsCusCtLonContractRefBankInfoService;

    @Autowired
    HlsCusCtLonContractBankAccountService hlsCusCtLonContractBankAccountService;

    @Autowired
    HlsCusCtLonContractGuarantorBpService hlsCusCtLonContractGuarantorBpService;
    @Autowired
    IHlsCusLonContractGuarantorService hlsCusLonContractGuarantorService;

    @Autowired
    HlsCusCtLonContractMortgageService hlsCusCtLonContractMortgageService;

    @Autowired
    HlsCusCtLonContractPledgeService hlsCusCtLonContractPledgeService;

    @Autowired
    HlsCusLonContractWithdrawPlanService hlsCusLonContractWithdrawPlanService;

    @Autowired
    HlsCusLonContractRepaymentPlanService hlsCusLonContractRepaymentPlanService;

    @Autowired
    HlsCusILonContractAttachmentService hlsCusILonContractAttachmentService;

    @Autowired
    HlsCusCtLonContractChangeReqService hlsCusCtLonContractChangeReqService;

    @Autowired
    HlsCusCtLonContractConsignmentSalesInfoService hlsCusCtLonContractConsignmentSalesInfoService;

    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsCusCtAbsContractService hlsCusCtAbsContractService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusSysUserAuthorityTrxService hlsCusSysUserAuthorityTrxService;
    @Autowired
    private HlsCusLonContractRepaymentPlanMapper hlsCusLonContractRepaymentPlanMapper;

    @Autowired
    private HlsCusLonContractBailService hlsCusLonContractBailService;

    @Autowired
    private HlsCusFctContractService hlsCusFctContractService;
    @Autowired
    private HlsCusCreditContractMapper hlscuscreditcontractmapper;


    @Autowired
    private HlsSysFileService hlsSysFileService;

    @Autowired
    private HlsCusLonContractPledgeInfoMapper lonContractPledgeInfoMapper;

    @Override
    public Integer validata(IRequest request, HlsCusLonContract lonContract) {
        return lonContractMapper.validata(lonContract);
    }

    @Override
    public List<HlsCusLonContract> queryLonContract(IRequest request, HlsCusLonContract lonContract, int page, int pageSize) {
        lonContract.setCompanyId(request.getCompanyId());
        PageHelper.startPage(page, pageSize);
        return lonContractMapper.selectLonContract(lonContract);
    }

    @Override
    public void save(IRequest iRequest, HlsCusLonContract lonContract, HttpSession session) throws HlsCusException {

        Long creditLineId = lonContract.getCreditLineId();
        Long creditContractId = lonContract.getCreditContractId();
        lonContract.setCompanyId(iRequest.getCompanyId());
//        HlsCusLonContractQuotation hlsCusLonContractQuotation = new HlsCusLonContractQuotation();
        if(lonContract.getContractId()==null || lonContract.getContractId()==0){
            lonContract.setContractId(null);
        }
        if(StringUtils.isNotBlank(lonContract.getMajorContractNumber())) {
            int count = lonContractMapper.selectMajorContractNumCount(lonContract);
            if (count > 0) {
                throw new HlsCusException("该融资合同已存在，无需重复录入");
            }
        }
        HlsCusCreditContract lonContractContactAmountCheck = hlscuscreditcontractmapper.lonContractCheckQueryAmt(lonContract.getCreditContractId());
        if(lonContractContactAmountCheck!= null) {
            if(lonContractContactAmountCheck.getCreditUnexposureAmt() != null && lonContractContactAmountCheck.getCreditUnexposureAmt() <lonContract.getFinanceAmount()){
                throw new HlsCusException("合同金额需小于等于对应授信的可用额度！");
            }
        }


        //如果该合同已经确认，校验“合同执行期从”需小于等于关联下的提款中的最小提款日，“合同执行期到”需要大于等于关联下的提款中的最大的到期日；
        //金额：合同金额需要大于等于关联提款的提款金额总和
        if("APPROVED".equals(lonContract.getContractStatus())){
            HlsCusLonContract lonContractCheck = lonContractMapper.lonContractCheckQuery(lonContract.getContractId());
            if(lonContractCheck!= null){
                if(lonContractCheck.getFinancingTermFrom() != null && lonContractCheck.getFinancingTermFrom().getTime()<lonContract.getFinancingTermFrom().getTime()){
                    throw new HlsCusException("“合同执行期从”需小于等于关联下的提款中的最小提款日");
                }
                if(lonContractCheck.getFinancingTermTo() != null && lonContractCheck.getFinancingTermTo().getTime()>lonContract.getFinancingTermTo().getTime()){
                    throw new HlsCusException("“合同执行期到”需要大于等于关联下的提款中的最大的到期日");
                }
                if(lonContractCheck.getFinanceAmount() != null && lonContractCheck.getFinanceAmount()>lonContract.getFinanceAmount()){
                    throw new HlsCusException("合同金额需要大于等于关联提款的提款金额总和");
                }
            }
        }
        if (lonContract.getContractNumber() == "" || lonContract.getContractNumber() == null) {
            Map<String, String> params = new HashMap<String, String>();
            String value = codingRuleValuesService.getCodeRuleValue(iRequest, "LOAN_CONTRACT", "LOAN_CONTRACT", "LOAN_CONTRACT", params);

            lonContract.setContractNumber(value);
        }

       String contractNewFlag;
        //融资合同信息
        if (lonContract.getContractId() != null && lonContract.getContractId() != 0) {
            lonContract = self().updateByPrimaryKey(iRequest, lonContract);
            contractNewFlag = "N";
        } else {
            Long companyId = Long.valueOf(String.valueOf(session.getAttribute("companyId")));
            lonContract.setCompanyId(companyId);
            lonContract = self().insertSelective(iRequest, lonContract);
            contractNewFlag = "Y";
        }
        Long contractId = lonContract.getContractId();

        //合同保存完则需要更新授信金额

        //融资合同关联银行信息
        if (lonContract.getCtLonContractRefBankInfos() != null) {
            for (HlsCusCtLonContractRefBankInfo ctLonContractRefBankInfo : lonContract.getCtLonContractRefBankInfos()) {
                if (ctLonContractRefBankInfo.getRefBankId() != null) {
                    hlsCusCtLonContractRefBankInfoService.updateByPrimaryKeySelective(iRequest, ctLonContractRefBankInfo);
                } else {
                    ctLonContractRefBankInfo.setContractId(contractId);
                    hlsCusCtLonContractRefBankInfoService.insertSelective(iRequest, ctLonContractRefBankInfo);
                }
            }
        }


        //账户信息
        if (lonContract.getLonContractBankAccounts() != null) {
            for (HlsCusCtLonContractBankAccount lonContractBankAccount : lonContract.getLonContractBankAccounts()) {
                if (lonContractBankAccount.getConBankAccountId() != null) {
                    hlsCusCtLonContractBankAccountService.updateByPrimaryKeySelective(iRequest, lonContractBankAccount);
                } else {
                    lonContractBankAccount.setContractId(contractId);
                    hlsCusCtLonContractBankAccountService.insertSelective(iRequest, lonContractBankAccount);
                }
            }
        }


        //融资合同担保信息
        if (lonContract.getHlsCusLonContractGuarantorList() != null) {
            for (HlsCusLonContractGuarantor hlsCusLonContractGuarantor : lonContract.getHlsCusLonContractGuarantorList()) {
                if (hlsCusLonContractGuarantor.getGuarantorId() != null) {
                    hlsCusLonContractGuarantorService.updateByPrimaryKeySelective(iRequest, hlsCusLonContractGuarantor);
                } else {
                    hlsCusLonContractGuarantor.setContractId(contractId);
                    hlsCusLonContractGuarantorService.insertSelective(iRequest, hlsCusLonContractGuarantor);
                }
            }
        }

        //融资合同抵押信息
        if (lonContract.getHlsCusCtLonContractMortgages() != null) {
            for (HlsCusCtLonContractMortgage ctLonContractMortgage : lonContract.getHlsCusCtLonContractMortgages()) {
                if (ctLonContractMortgage.getMortgageId() != null) {
                    hlsCusCtLonContractMortgageService.updateByPrimaryKeySelective(iRequest, ctLonContractMortgage);
                } else {
                    ctLonContractMortgage.setContractId(contractId);
                    hlsCusCtLonContractMortgageService.insertSelective(iRequest, ctLonContractMortgage);
                }
            }
        }

        //融资合同质押信息
        if (lonContract.getCtLonContractPledges() != null) {
            Set<String> pledgeSet=new HashSet<>();
            for (HlsCusCtLonContractPledge ctLonContractPledge : lonContract.getCtLonContractPledges()) {
                pledgeSet.add(ctLonContractPledge.getPledgeDocCategory()+ctLonContractPledge.getPledgeDocId());
                if (ctLonContractPledge.getPledgeId() != null) {
                    HlsCusCtLonContractPledge pledgeBefore=hlsCusCtLonContractPledgeService.selectByPrimaryKey(iRequest,ctLonContractPledge);
                    hlsCusCtLonContractPledgeService.updateByPrimaryKeySelective(iRequest, ctLonContractPledge);
                    Map<String, Object> maxAndMinTimes = lonContractPledgeInfoMapper.selectPledgeMaxAndMinTimes(ctLonContractPledge.getPledgeId());
                /*   if(maxAndMinTimes!=null){
                        Long pledgeTimesMax= Long.parseLong(maxAndMinTimes.get("pledgeTimesMax").toString());
                        if(pledgeTimesMax!=null){
                            if(ctLonContractPledge.getPledgeTimesTo().compareTo(pledgeTimesMax)==-1){
                                throw  new HlsCusException("请检查质押/转让期数从与到(与明细不符)");
                            }
                        }
                        Long pledgeTimesMin= Long.parseLong(maxAndMinTimes.get("pledgeTimesMin").toString());
                        if(pledgeTimesMin!=null){
                            if(ctLonContractPledge.getPledgeTimesFrom().compareTo(pledgeTimesMin)==1){
                                throw  new HlsCusException("请检查质押/转让期数从与到(与明细不符)");
                            }
                        }
                    }*/
                    if(!pledgeBefore.getContractId().equals(ctLonContractPledge.getContractId())
                            ||pledgeBefore.getPledgeDocCategory().equals(ctLonContractPledge.getPledgeDocCategory())){
                        if("Y".equals(pledgeBefore.getPledgeReleaseFlag())) {
                            hlsCusCtLonContractPledgeService.updatePledgeFlag(iRequest, pledgeBefore, "N");
                        }
                    }
                } else {
                    ctLonContractPledge.setContractId(contractId);
                    hlsCusCtLonContractPledgeService.insertSelective(iRequest, ctLonContractPledge);
                }
                //更新合同质押标志
                if("Y".equals(ctLonContractPledge.getPledgeReleaseFlag())) {
                    hlsCusCtLonContractPledgeService.updatePledgeFlag(iRequest, ctLonContractPledge, "Y");
                }
            }
            if(pledgeSet.size()!=lonContract.getCtLonContractPledges().size()){
                throw  new HlsCusException("请勿选择两个相同的合同");
            }
        }

        //保证金信息
        if (lonContract.getHlsCusLonContractBails() != null) {
            for (HlsCusLonContractBail lonContractBail :lonContract.getHlsCusLonContractBails()) {
                if (lonContractBail.getContractBailId() != null) {
                    hlsCusLonContractBailService.updateByPrimaryKeySelective(iRequest, lonContractBail);
                } else {
                    lonContractBail.setContractId(contractId);
                    hlsCusLonContractBailService.insertSelective(iRequest, lonContractBail);
                }
            }
        }

        //附件信息
        if (lonContract.getLonContractAttachments() != null) {
            for (HlsCusLonContractAttachment hlsCusLonContractAttachment : lonContract.getLonContractAttachments()
                    ) {
                if (hlsCusLonContractAttachment.getContractAttachmentId() != null) {
                    hlsCusILonContractAttachmentService.updateByPrimaryKeySelective(iRequest, hlsCusLonContractAttachment);

                    HlsCusSysFile sysFile=new HlsCusSysFile();
                    sysFile.setFileId(hlsCusLonContractAttachment.getFileId());
                    sysFile.setFileName(hlsCusLonContractAttachment.getFileName());
                    hlsSysFileService.updateByPrimaryKeySelective(iRequest,sysFile);
                }else{
                    hlsCusLonContractAttachment.setContractId(contractId);
                    hlsCusLonContractAttachment.setSourceId(contractId);
                    hlsCusILonContractAttachmentService.insertSelective(iRequest, hlsCusLonContractAttachment);

                }
            }
        }

        int attachCodeCount= hlsCusILonContractAttachmentService.selectAttachmentCodeNullCount(contractId);
        if(attachCodeCount>0) {
            throw new HlsCusException("附件信息中附件编码不可以为空");
        }

        if("Y".equals(contractNewFlag)) {
            //发送系统消息
            Map<String, Object> paramsEvent = new HashMap<String, Object>();
            String userName = "";
            List<LoginUserInfo> loginUserInfos=loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode());
            if (loginUserInfos.size() > 0) {
                userName = loginUserInfos.get(0).getUserName();
            }
            //发送系统消息
            String msg;
            msg = userName + "新建了融资合同，编号为" + lonContract.getContractNumber();
            paramsEvent.put("noticeTitle", "融资合同申请");
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            sysEventService.eventSave(iRequest, lonContract.getContractId(), lonContract.getDocumentCategory(), lonContract.getDocumentType(), "LON_CONTRACT", "CT_LON_CONTRACT_WFL", "P2D", paramsEvent);
        }
    }


    @Override
    public HlsCusLonContract selectLonContractFormData(IRequest request, HlsCusLonContract lonContract) {
        List<HlsCusLonContract> hlsCusLonContracts = lonContractMapper.selectLonContractFormData(lonContract);
        HlsCusLonContract result = new HlsCusLonContract();
        if (hlsCusLonContracts.size() == 1) {
            result = hlsCusLonContracts.get(0);
        }
        return result;
    }

    @Override
    public List<HlsCusLonContract> submitLonContractToWfl(IRequest iRequest, HlsCusLonContract lonContract, HttpSession session) throws HlsCusException{
        //先保存数据
        self().save(iRequest, lonContract, session);
        lonContract = self().selectByPrimaryKey(iRequest, lonContract);

        List<HlsCusLonContract> list = new ArrayList<>();
        list.add(lonContract);

        if (lonContract.getLonCompanyId().equals(265L) || lonContract.getLonCompanyId().equals(266L)) {
            //变更审批通过，修改原合同状态，修改变更记录状态
            if ("CHANGE_REQ".equalsIgnoreCase(lonContract.getDataClass())) {
                HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReqTmp = new HlsCusCtLonContractChangeReq();
                hlsCusCtLonContractChangeReqTmp.setChangeReqId(lonContract.getContractId());
                List<HlsCusCtLonContractChangeReq> hlsCusCtLonContractChangeReqs = hlsCusCtLonContractChangeReqService.select(iRequest, hlsCusCtLonContractChangeReqTmp, 1, 999999);
                HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReq = hlsCusCtLonContractChangeReqs.get(0);
                //回写变更数据到NORMAL合同
                this.lonContractChangeReqConfirm(iRequest, lonContract, hlsCusCtLonContractChangeReq);

                HlsCusLonContract originLonContract = new HlsCusLonContract();
                originLonContract.setContractId(hlsCusCtLonContractChangeReq.getContractId());
                originLonContract = self().selectByPrimaryKey(iRequest, originLonContract);
                originLonContract.setContractStatus("APPROVED");
                self().updateByPrimaryKeySelective(iRequest, originLonContract);
                hlsCusCtLonContractChangeReq.setReqStatus("APPROVED");
                hlsCusCtLonContractChangeReqMapper.updateChangeReq(hlsCusCtLonContractChangeReq);
            }
            lonContract.setContractStatus("APPROVED");
            self().updateByPrimaryKeySelective(iRequest, lonContract);
        } else {
            if (CollectionUtils.isNotEmpty(list)) {
                logger.debug("=============== start lonContractChangeReq activiti ==============");
                databaseLockProvider.lock(list.get(0));
                //获取申请人
                HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
                String employeeCode = employee.getEmployeeCode();
                iRequest.setEmployeeCode(employeeCode);

                //开始流程
                Map<String, Object> params = new HashMap<String, Object>();
                if ("NORMAL".equalsIgnoreCase(lonContract.getDataClass())) {
                    params.put("workFlowType", "CT_LON_CONTRACT_WFL");
                } else {
                    params.put("workFlowType", "CT_LON_CONTRACT_CHANGE_REQ_WFL");
                }
                activitiStartService.start(iRequest, list, params);

                //修改单据状态
                lonContract.setContractStatus("APPROVING");
                self().updateByPrimaryKeySelective(iRequest, lonContract);

                if ("CHANGE_REQ".equalsIgnoreCase(lonContract.getDataClass())) {
                    HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReqTmp = new HlsCusCtLonContractChangeReq();
                    hlsCusCtLonContractChangeReqTmp.setChangeReqId(lonContract.getContractId());
                    List<HlsCusCtLonContractChangeReq> hlsCusCtLonContractChangeReqs = hlsCusCtLonContractChangeReqService.select(iRequest, hlsCusCtLonContractChangeReqTmp, 1, 999999);
                    HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReq = hlsCusCtLonContractChangeReqs.get(0);
                    hlsCusCtLonContractChangeReq.setReqStatus("APPROVING");
                    hlsCusCtLonContractChangeReq.setSubmittedBy(iRequest.getUserId());
                    hlsCusCtLonContractChangeReq.setSubmitDate(new Date());
                    hlsCusCtLonContractChangeReqMapper.updateChangeReq(hlsCusCtLonContractChangeReq);
                }

                //发送系统消息
                Map<String, Object> paramsEvent = new HashMap<String, Object>();
                String userName = "";
                if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
                    userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
                }
                //发送系统消息
                String msg;
                if ("NORMAL".equalsIgnoreCase(lonContract.getDataClass())) {
                    msg = userName + "提交了" + lonContract.getContractName() + "的融资合同申请，编号为" + lonContract.getContractNumber();
                    paramsEvent.put("noticeTitle", "融资合同申请");
                } else {
                    msg = userName + "提交了" + lonContract.getContractName() + "的融资合同变更申请，编号为" + lonContract.getContractNumber();
                    paramsEvent.put("noticeTitle", "融资合同变更申请");
                }

                paramsEvent.put("message", msg);
                paramsEvent.put("noticeType", "NOTICE");
                paramsEvent.put("url", "");
                paramsEvent.put("level", 1L);
                sysEventService.eventSave(iRequest, lonContract.getContractId(), lonContract.getDocumentCategory(), lonContract.getDocumentType(), "LON_CONTRACT", "CT_LON_CONTRACT_WFL", "P2D", paramsEvent);
            }
        }

        return list;
    }

    @Override
    public HlsCusLonContract lonContractChangeReqCreate(IRequest request, HlsCusLonContract lonContract, HttpSession session) {
        //获取NORMAL的融资合同
        lonContract = self().selectByPrimaryKey(request, lonContract);
        Long contractId = lonContract.getContractId();

        //获取NORMAL合同的交易信息
        HlsCusLonContractQuotation hlsCusLonContractQuotationTmp = new HlsCusLonContractQuotation();
        hlsCusLonContractQuotationTmp.setContractId(contractId);
        List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationService.select(request, hlsCusLonContractQuotationTmp, 1, 99999);
        HlsCusLonContractQuotation hlsCusLonContractQuotation = hlsCusLonContractQuotations.get(0);

        //获取NORMAL合同的关联银行
        HlsCusCtLonContractRefBankInfo hlsCusCtLonContractRefBankInfoTmp = new HlsCusCtLonContractRefBankInfo();
        hlsCusCtLonContractRefBankInfoTmp.setContractId(contractId);
        List<HlsCusCtLonContractRefBankInfo> hlsCusCtLonContractRefBankInfos = hlsCusCtLonContractRefBankInfoService.select(request, hlsCusCtLonContractRefBankInfoTmp, 1, 99999);

        //获取NORMAL合同的ABS资产包
        HlsCusCtAbsContract hlsCusCtAbsContractTmp = new HlsCusCtAbsContract();
        hlsCusCtAbsContractTmp.setLonContractId(contractId);
        List<HlsCusCtAbsContract> hlsCusCtAbsContracts = hlsCusCtAbsContractService.select(request, hlsCusCtAbsContractTmp, 1, 999999);

        //获取NORMAL合同的承销商信息
        HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfoTmp = new HlsCusCtLonContractConsignmentSalesInfo();
        hlsCusCtLonContractConsignmentSalesInfoTmp.setContractId(contractId);
        List<HlsCusCtLonContractConsignmentSalesInfo> hlsCusCtLonContractConsignmentSalesInfos = hlsCusCtLonContractConsignmentSalesInfoService.select(request, hlsCusCtLonContractConsignmentSalesInfoTmp, 1, 999999);

        //获取NORMAL合同的提款信息
        HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlanTmp = new HlsCusLonContractWithdrawPlan();
        hlsCusLonContractWithdrawPlanTmp.setContractId(contractId);
        List<HlsCusLonContractWithdrawPlan> hlsCusLonContractWithdrawPlans = hlsCusLonContractWithdrawPlanService.select(request, hlsCusLonContractWithdrawPlanTmp, 1, 99999);

        //获取NORMAL合同的担保信息
        HlsCusCtLonContractGuarantorBp hlsCusCtLonContractGuarantorBpTmp = new HlsCusCtLonContractGuarantorBp();
        hlsCusCtLonContractGuarantorBpTmp.setContractId(contractId);
        List<HlsCusCtLonContractGuarantorBp> hlsCusCtLonContractGuarantorBps = hlsCusCtLonContractGuarantorBpService.select(request, hlsCusCtLonContractGuarantorBpTmp, 1, 99999);

        //获取NORMAL合同的抵押信息
        HlsCusCtLonContractMortgage hlsCusCtLonContractMortgageTmp = new HlsCusCtLonContractMortgage();
        hlsCusCtLonContractMortgageTmp.setContractId(contractId);
        List<HlsCusCtLonContractMortgage> hlsCusCtLonContractMortgages = hlsCusCtLonContractMortgageService.select(request, hlsCusCtLonContractMortgageTmp, 1, 99999);

        //获取NORMAL合同的质押信息
        HlsCusCtLonContractPledge hlsCusCtLonContractPledgeTmp = new HlsCusCtLonContractPledge();
        hlsCusCtLonContractPledgeTmp.setContractId(contractId);
        List<HlsCusCtLonContractPledge> hlsCusCtLonContractPledges = hlsCusCtLonContractPledgeService.select(request, hlsCusCtLonContractPledgeTmp, 1, 99999);

        //获取NORMAL合同的附件信息
        HlsCusLonContractAttachment hlsCusLonContractAttachmentTmp = new HlsCusLonContractAttachment();
        hlsCusLonContractAttachmentTmp.setContractId(contractId);
        List<HlsCusLonContractAttachment> hlsCusLonContractAttachments = hlsCusILonContractAttachmentService.select(request, hlsCusLonContractAttachmentTmp, 1, 999999);

        //复制NORMAL状态融资合同
        HlsCusLonContract lonContractChangeReq = new HlsCusLonContract();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(lonContract);
        hlsBeanRefUtilService.setFieldValue(lonContractChangeReq, map);
        //插入CHANGE_REQ记录
        lonContractChangeReq.set__status("add");
        lonContractChangeReq.setContractId(null);
        lonContractChangeReq.setDataClass("CHANGE_REQ");
        lonContractChangeReq.setContractStatus("NEW");
        lonContractChangeReq.setApprovalResult(null);
        lonContractChangeReq.setApprovalComment(null);
        lonContractChangeReq = self().insert(request, lonContractChangeReq);

        Long changeReqId = lonContractChangeReq.getContractId();

        //复制交易信息
        HlsCusLonContractQuotation hlsCusLonContractQuotationChangeReq = new HlsCusLonContractQuotation();
        Map<String, String> mapQuotation = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractQuotation);
        hlsBeanRefUtilService.setFieldValue(hlsCusLonContractQuotationChangeReq, mapQuotation);
        //插入到CHANGE_REQ合同项下
        hlsCusLonContractQuotationChangeReq.set__status("add");
        hlsCusLonContractQuotationChangeReq.setContractId(changeReqId);
        hlsCusLonContractQuotationChangeReq.setQuotationId(null);
        hlsCusLonContractQuotationService.insertSelective(request, hlsCusLonContractQuotationChangeReq);

        //复制关联银行
        if (hlsCusCtLonContractRefBankInfos.size() > 0) {
            for (HlsCusCtLonContractRefBankInfo hlsCusCtLonContractRefBankInfo : hlsCusCtLonContractRefBankInfos
                    ) {
                HlsCusCtLonContractRefBankInfo hlsCusCtLonContractRefBankInfoChangeReq = new HlsCusCtLonContractRefBankInfo();
                Map<String, String> mapBankInfo = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractRefBankInfo);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractRefBankInfoChangeReq, mapBankInfo);
                //插入到CHANGE_REQ合同项下
                hlsCusCtLonContractRefBankInfoChangeReq.set__status("add");
                hlsCusCtLonContractRefBankInfoChangeReq.setContractId(changeReqId);
                hlsCusCtLonContractRefBankInfoChangeReq.setRefBankId(null);
                hlsCusCtLonContractRefBankInfoService.insertSelective(request, hlsCusCtLonContractRefBankInfoChangeReq);
            }
        }

        //复制ABS资产包
        if (hlsCusCtAbsContracts.size() > 0) {
            for (HlsCusCtAbsContract hlsCusCtAbsContract : hlsCusCtAbsContracts
                    ) {
                HlsCusCtAbsContract hlsCusCtAbsContractChangeReq = new HlsCusCtAbsContract();
                Map<String, String> mapAbsContract = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtAbsContract);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtAbsContractChangeReq, mapAbsContract);
                //插入到CHANGE_REQ合同项下
                hlsCusCtAbsContractChangeReq.set__status(DTOStatus.ADD);
                hlsCusCtAbsContractChangeReq.setLonContractId(changeReqId);
                hlsCusCtAbsContractChangeReq.setAbsConId(null);
                hlsCusCtAbsContractService.insertSelective(request, hlsCusCtAbsContractChangeReq);
            }
        }

        //复制承销商信息
        if (hlsCusCtLonContractConsignmentSalesInfos.size() > 0) {
            for (HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfo : hlsCusCtLonContractConsignmentSalesInfos
                    ) {
                HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfoChangeReq = new HlsCusCtLonContractConsignmentSalesInfo();
                Map<String, String> mapConsignmentSalesInfo = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractConsignmentSalesInfo);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractConsignmentSalesInfoChangeReq, mapConsignmentSalesInfo);
                //插入到CHANGE_REQ合同项下
                hlsCusCtLonContractConsignmentSalesInfoChangeReq.set__status("add");
                hlsCusCtLonContractConsignmentSalesInfoChangeReq.setContractId(changeReqId);
                hlsCusCtLonContractConsignmentSalesInfoChangeReq.setConsignmentSalesId(null);
                hlsCusCtLonContractConsignmentSalesInfoService.insertSelective(request, hlsCusCtLonContractConsignmentSalesInfoChangeReq);
            }
        }

        //复制提款信息
        if (hlsCusLonContractWithdrawPlans.size() > 0) {
            for (HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlan : hlsCusLonContractWithdrawPlans
                    ) {
                Long withdrawPlanId = hlsCusLonContractWithdrawPlan.getWithdrawPlanId();

                HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlanChangeReq = new HlsCusLonContractWithdrawPlan();
                Map<String, String> mapWithdrawPlan = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractWithdrawPlan);
                hlsBeanRefUtilService.setFieldValue(hlsCusLonContractWithdrawPlanChangeReq, mapWithdrawPlan);
                //插入到CHANGE_REQ合同项下
                hlsCusLonContractWithdrawPlanChangeReq.set__status("add");
                hlsCusLonContractWithdrawPlanChangeReq.setContractId(changeReqId);
                hlsCusLonContractWithdrawPlanChangeReq.setWithdrawPlanId(null);
                hlsCusLonContractWithdrawPlanChangeReq = hlsCusLonContractWithdrawPlanService.insertSelective(request, hlsCusLonContractWithdrawPlanChangeReq);

                Long changeReqWithdrawPlanId = hlsCusLonContractWithdrawPlanChangeReq.getWithdrawPlanId();

                //获取NORMAL合同的提款项下还款计划
                HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanTmp = new HlsCusLonContractRepaymentPlan();
                hlsCusLonContractRepaymentPlanTmp.setWithdrawPlanId(withdrawPlanId);
                List<HlsCusLonContractRepaymentPlan> hlsCusLonContractRepaymentPlans = hlsCusLonContractRepaymentPlanService.select(request, hlsCusLonContractRepaymentPlanTmp, 1, 999999);

                //复制还款计划
                if (hlsCusLonContractRepaymentPlans.size() > 0) {
                    for (HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlan : hlsCusLonContractRepaymentPlans
                            ) {
                        HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanChangeReq = new HlsCusLonContractRepaymentPlan();
                        Map<String, String> mapRepaymentPlan = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractRepaymentPlan);
                        hlsBeanRefUtilService.setFieldValue(hlsCusLonContractRepaymentPlanChangeReq, mapRepaymentPlan);
                        //插入到CHANGE_REQ合同项下
                        hlsCusLonContractRepaymentPlanChangeReq.set__status("add");
                        hlsCusLonContractRepaymentPlanChangeReq.setWithdrawPlanId(changeReqWithdrawPlanId);
                        hlsCusLonContractRepaymentPlanChangeReq.setContractId(changeReqId);
                        hlsCusLonContractRepaymentPlanChangeReq.setRepaymentPlanId(null);
                        hlsCusLonContractRepaymentPlanService.insertSelective(request, hlsCusLonContractRepaymentPlanChangeReq);
                    }
                }
            }
        }

        //复制担保信息
        if (hlsCusCtLonContractGuarantorBps.size() > 0) {
            for (HlsCusCtLonContractGuarantorBp hlsCusCtLonContractGuarantorBp : hlsCusCtLonContractGuarantorBps
                    ) {
                HlsCusCtLonContractGuarantorBp hlsCusCtLonContractGuarantorBpChangeReq = new HlsCusCtLonContractGuarantorBp();
                Map<String, String> mapGuarantorBp = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractGuarantorBp);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractGuarantorBpChangeReq, mapGuarantorBp);
                //插入到CHANGE_REQ合同项下
                hlsCusCtLonContractGuarantorBpChangeReq.set__status("add");
                hlsCusCtLonContractGuarantorBpChangeReq.setContractId(changeReqId);
                hlsCusCtLonContractGuarantorBpChangeReq.setGuaranteeBpId(null);
                hlsCusCtLonContractGuarantorBpService.insertSelective(request, hlsCusCtLonContractGuarantorBpChangeReq);
            }
        }

        //复制抵押信息
        if (hlsCusCtLonContractMortgages.size() > 0) {
            for (HlsCusCtLonContractMortgage hlsCusCtLonContractMortgage : hlsCusCtLonContractMortgages
                    ) {
                HlsCusCtLonContractMortgage hlsCusCtLonContractMortgageChangeReq = new HlsCusCtLonContractMortgage();
                Map<String, String> mapMortgage = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractMortgage);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractMortgageChangeReq, mapMortgage);
                //插入到CHANGE_REQ合同项下
                hlsCusCtLonContractMortgageChangeReq.set__status("add");
                hlsCusCtLonContractMortgageChangeReq.setContractId(changeReqId);
                hlsCusCtLonContractMortgageChangeReq.setMortgageId(null);
                hlsCusCtLonContractMortgageService.insertSelective(request, hlsCusCtLonContractMortgageChangeReq);
            }
        }

        //复制质押信息
        if (hlsCusCtLonContractPledges.size() > 0) {
            for (HlsCusCtLonContractPledge hlsCusCtLonContractPledge : hlsCusCtLonContractPledges
                    ) {
                HlsCusCtLonContractPledge hlsCusCtLonContractPledgeChangeReq = new HlsCusCtLonContractPledge();
                Map<String, String> mapPledge = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractPledge);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractPledgeChangeReq, mapPledge);
                //插入到CHANGE_REQ合同项下
                hlsCusCtLonContractPledgeChangeReq.set__status("add");
                hlsCusCtLonContractPledgeChangeReq.setContractId(changeReqId);
                hlsCusCtLonContractPledgeChangeReq.setPledgeId(null);
                hlsCusCtLonContractPledgeService.insertSelective(request, hlsCusCtLonContractPledgeChangeReq);
            }
        }

        //复制附件信息
        if (hlsCusLonContractAttachments.size() > 0) {
            for (HlsCusLonContractAttachment hlsCusLonContractAttachment : hlsCusLonContractAttachments
                    ) {
                HlsCusLonContractAttachment hlsCusLonContractAttachmentChangeReq = new HlsCusLonContractAttachment();
                Map<String, String> mapAttachment = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractAttachment);
                hlsBeanRefUtilService.setFieldValue(hlsCusLonContractAttachmentChangeReq, mapAttachment);
                //插入到CHANGE_REQ合同项下
                hlsCusLonContractAttachmentChangeReq.set__status("add");
                hlsCusLonContractAttachmentChangeReq.setContractId(changeReqId);
                hlsCusLonContractAttachmentChangeReq.setContractAttachmentId(null);
                hlsCusILonContractAttachmentService.insertSelective(request, hlsCusLonContractAttachmentChangeReq);
            }
        }

        //插入变更记录表
        HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReq = new HlsCusCtLonContractChangeReq();

        hlsCusCtLonContractChangeReq.set__status("add");
        hlsCusCtLonContractChangeReq.setChangeReqId(changeReqId);
        hlsCusCtLonContractChangeReq.setContractId(contractId);
        hlsCusCtLonContractChangeReq.setReqDate(new Date());
        hlsCusCtLonContractChangeReq.setReqStatus("NEW");
        hlsCusCtLonContractChangeReqService.insertSelective(request, hlsCusCtLonContractChangeReq);

        lonContract.setContractStatus("PENDING");
        self().updateByPrimaryKeySelective(request, lonContract);

        return lonContractChangeReq;
    }

    @Override
    public void lonContractChangeReqConfirm(IRequest request, HlsCusLonContract lonContractChangeReq, HlsCusCtLonContractChangeReq ctLonContractChangeReq) {
        Long contractId = ctLonContractChangeReq.getContractId();
        Long changeReqId = ctLonContractChangeReq.getChangeReqId();

        //获取NORMAL状态合同
        HlsCusLonContract lonContractNormal = new HlsCusLonContract();
        lonContractNormal.setContractId(contractId);
        lonContractNormal = self().selectByPrimaryKey(request, lonContractNormal);

        //获取NORMAL合同的交易信息
        HlsCusLonContractQuotation hlsCusLonContractQuotationTmp = new HlsCusLonContractQuotation();
        hlsCusLonContractQuotationTmp.setContractId(contractId);
        List<HlsCusLonContractQuotation> hlsCusLonContractQuotationNormals = hlsCusLonContractQuotationService.select(request, hlsCusLonContractQuotationTmp, 1, 99999);
        HlsCusLonContractQuotation hlsCusLonContractQuotationNormal = hlsCusLonContractQuotationNormals.get(0);

        //获取NORMAL合同的关联银行信息
        HlsCusCtLonContractRefBankInfo hlsCusCtLonContractRefBankInfoTmp = new HlsCusCtLonContractRefBankInfo();
        hlsCusCtLonContractRefBankInfoTmp.setContractId(contractId);
        List<HlsCusCtLonContractRefBankInfo> hlsCusCtLonContractRefBankInfoNormals = hlsCusCtLonContractRefBankInfoService.select(request, hlsCusCtLonContractRefBankInfoTmp, 1, 99999);

        //获取NORMAL合同的ABS资产包信息
        HlsCusCtAbsContract hlsCusCtAbsContractTmp = new HlsCusCtAbsContract();
        hlsCusCtAbsContractTmp.setLonContractId(contractId);
        List<HlsCusCtAbsContract> hlsCusCtAbsContractListNormals = hlsCusCtAbsContractService.select(request, hlsCusCtAbsContractTmp, 1, 999999);

        //获取NORMAL合同的承销商信息
        HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfoTmp = new HlsCusCtLonContractConsignmentSalesInfo();
        hlsCusCtLonContractConsignmentSalesInfoTmp.setContractId(contractId);
        List<HlsCusCtLonContractConsignmentSalesInfo> hlsCusCtLonContractConsignmentSalesInfos = hlsCusCtLonContractConsignmentSalesInfoService.select(request, hlsCusCtLonContractConsignmentSalesInfoTmp, 1, 999999);

        //获取NORMAL合同的提款计划
        HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlanTmp = new HlsCusLonContractWithdrawPlan();
        hlsCusLonContractWithdrawPlanTmp.setContractId(contractId);
        List<HlsCusLonContractWithdrawPlan> hlsCusLonContractWithdrawPlanNormals = hlsCusLonContractWithdrawPlanService.select(request, hlsCusLonContractWithdrawPlanTmp, 1, 99999);

        //获取NORMAL合同的担保信息
        HlsCusCtLonContractGuarantorBp hlsCusCtLonContractGuarantorBpTmp = new HlsCusCtLonContractGuarantorBp();
        hlsCusCtLonContractGuarantorBpTmp.setContractId(contractId);
        List<HlsCusCtLonContractGuarantorBp> hlsCusCtLonContractGuarantorBpNormals = hlsCusCtLonContractGuarantorBpService.select(request, hlsCusCtLonContractGuarantorBpTmp, 1, 99999);

        //获取NORMAL合同的抵押信息
        HlsCusCtLonContractMortgage hlsCusCtLonContractMortgageTmp = new HlsCusCtLonContractMortgage();
        hlsCusCtLonContractMortgageTmp.setContractId(contractId);
        List<HlsCusCtLonContractMortgage> hlsCusCtLonContractMortgageNormals = hlsCusCtLonContractMortgageService.select(request, hlsCusCtLonContractMortgageTmp, 1, 99999);

        //获取NORMAL合同的质押信息
        HlsCusCtLonContractPledge hlsCusCtLonContractPledgeTmp = new HlsCusCtLonContractPledge();
        hlsCusCtLonContractPledgeTmp.setContractId(contractId);
        List<HlsCusCtLonContractPledge> hlsCusCtLonContractPledgeNormals = hlsCusCtLonContractPledgeService.select(request, hlsCusCtLonContractPledgeTmp, 1, 99999);

        //获取NORMAL合同的附件信息
        HlsCusLonContractAttachment hlsCusLonContractAttachmentTmp = new HlsCusLonContractAttachment();
        hlsCusLonContractAttachmentTmp.setContractId(contractId);
        List<HlsCusLonContractAttachment> hlsCusLonContractAttachmentNormals = hlsCusILonContractAttachmentService.select(request, hlsCusLonContractAttachmentTmp, 1, 999999);


        //复制NORMAL合同存储为HISTORY
        HlsCusLonContract lonContractHistory = new HlsCusLonContract();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(lonContractNormal);
        hlsBeanRefUtilService.setFieldValue(lonContractHistory, map);
        //插入HISTORY记录
        lonContractHistory.set__status("add");
        lonContractHistory.setContractId(null);
        lonContractHistory.setDataClass("HISTORY");
        self().insertSelective(request, lonContractHistory);
        Long historyContractId = lonContractHistory.getContractId();

        //复制NORMAL合同的交易信息备份
        HlsCusLonContractQuotation hlsCusLonContractQuotationHistory = new HlsCusLonContractQuotation();
        Map<String, String> mapQuotationHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractQuotationNormal);
        hlsBeanRefUtilService.setFieldValue(hlsCusLonContractQuotationHistory, mapQuotationHistory);
        hlsCusLonContractQuotationHistory.set__status("add");
        hlsCusLonContractQuotationHistory.setQuotationId(null);
        hlsCusLonContractQuotationHistory.setContractId(historyContractId);
        hlsCusLonContractQuotationService.insertSelective(request, hlsCusLonContractQuotationHistory);

        //复制NORMAL合同的关联银行信息备份
        if (hlsCusCtLonContractRefBankInfoNormals.size() > 0) {
            for (HlsCusCtLonContractRefBankInfo hlsCusCtLonContractRefBankInfoNormal : hlsCusCtLonContractRefBankInfoNormals
                    ) {
                HlsCusCtLonContractRefBankInfo hlsCusCtLonContractRefBankInfoHistory = new HlsCusCtLonContractRefBankInfo();
                Map<String, String> mapBankInfoHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractRefBankInfoNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractRefBankInfoHistory, mapBankInfoHistory);
                hlsCusCtLonContractRefBankInfoHistory.set__status("add");
                hlsCusCtLonContractRefBankInfoHistory.setContractId(historyContractId);
                hlsCusCtLonContractRefBankInfoHistory.setRefBankId(null);
                hlsCusCtLonContractRefBankInfoService.insertSelective(request, hlsCusCtLonContractRefBankInfoHistory);
            }
        }

        //复制NORMAL合同的ABS资产包信息备份
        if (hlsCusCtAbsContractListNormals.size() > 0) {
            for (HlsCusCtAbsContract hlsCusCtAbsContractNormal : hlsCusCtAbsContractListNormals
                    ) {
                HlsCusCtAbsContract hlsCusCtAbsContractHistory = new HlsCusCtAbsContract();
                Map<String, String> mapAbsContractHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtAbsContractNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtAbsContractHistory, mapAbsContractHistory);
                hlsCusCtAbsContractHistory.set__status(DTOStatus.ADD);
                hlsCusCtAbsContractHistory.setLonContractId(historyContractId);
                hlsCusCtAbsContractHistory.setAbsConId(null);
                hlsCusCtAbsContractService.insertSelective(request, hlsCusCtAbsContractHistory);
            }
        }

        //复制NORMAL合同的承销商信息备份
        if (hlsCusCtLonContractConsignmentSalesInfos.size() > 0) {
            for (HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfoNormal : hlsCusCtLonContractConsignmentSalesInfos
                    ) {
                HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfoHistory = new HlsCusCtLonContractConsignmentSalesInfo();
                Map<String, String> mapConsignmentSalesInfoHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractConsignmentSalesInfoNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractConsignmentSalesInfoHistory, mapConsignmentSalesInfoHistory);
                hlsCusCtLonContractConsignmentSalesInfoHistory.set__status("add");
                hlsCusCtLonContractConsignmentSalesInfoHistory.setContractId(historyContractId);
                hlsCusCtLonContractConsignmentSalesInfoHistory.setConsignmentSalesId(null);
                hlsCusCtLonContractConsignmentSalesInfoService.insertSelective(request, hlsCusCtLonContractConsignmentSalesInfoHistory);
            }
        }

        //复制NORMAL合同的提款计划以及还款计划备份
        if (hlsCusLonContractWithdrawPlanNormals.size() > 0) {
            for (HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlanNormal : hlsCusLonContractWithdrawPlanNormals
                    ) {
                Long withdrawPlanId = hlsCusLonContractWithdrawPlanNormal.getWithdrawPlanId();

                HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlanHistory = new HlsCusLonContractWithdrawPlan();
                Map<String, String> mapWithdrawPlanHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractWithdrawPlanNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusLonContractWithdrawPlanHistory, mapWithdrawPlanHistory);
                hlsCusLonContractWithdrawPlanHistory.set__status("add");
                hlsCusLonContractWithdrawPlanHistory.setContractId(historyContractId);
                hlsCusLonContractWithdrawPlanHistory.setWithdrawPlanId(null);
                hlsCusLonContractWithdrawPlanHistory = hlsCusLonContractWithdrawPlanService.insertSelective(request, hlsCusLonContractWithdrawPlanHistory);

                Long historyWithdrawPlanId = hlsCusLonContractWithdrawPlanHistory.getWithdrawPlanId();

                //获取NORMAL合同的提款项下还款计划
                HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanTmp = new HlsCusLonContractRepaymentPlan();
                hlsCusLonContractRepaymentPlanTmp.setWithdrawPlanId(withdrawPlanId);
                List<HlsCusLonContractRepaymentPlan> hlsCusLonContractRepaymentPlanNormals = hlsCusLonContractRepaymentPlanService.select(request, hlsCusLonContractRepaymentPlanTmp, 1, 999999);

                //复制还款计划
                if (hlsCusLonContractRepaymentPlanNormals.size() > 0) {
                    for (HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanNormal : hlsCusLonContractRepaymentPlanNormals
                            ) {
                        HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanHistory = new HlsCusLonContractRepaymentPlan();
                        Map<String, String> mapRepaymentPlanHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractRepaymentPlanNormal);
                        hlsBeanRefUtilService.setFieldValue(hlsCusLonContractRepaymentPlanHistory, mapRepaymentPlanHistory);
                        hlsCusLonContractRepaymentPlanHistory.set__status("add");
                        hlsCusLonContractRepaymentPlanHistory.setWithdrawPlanId(historyWithdrawPlanId);
                        hlsCusLonContractRepaymentPlanHistory.setContractId(historyContractId);
                        hlsCusLonContractRepaymentPlanHistory.setRepaymentPlanId(null);
                        hlsCusLonContractRepaymentPlanService.insertSelective(request, hlsCusLonContractRepaymentPlanHistory);
                    }
                }
            }
        }

        //复制NORMAL合同的担保信息备份
        if (hlsCusCtLonContractGuarantorBpNormals.size() > 0) {
            for (HlsCusCtLonContractGuarantorBp hlsCusCtLonContractGuarantorBpNormal : hlsCusCtLonContractGuarantorBpNormals
                    ) {
                HlsCusCtLonContractGuarantorBp hlsCusCtLonContractGuarantorBpHistory = new HlsCusCtLonContractGuarantorBp();
                Map<String, String> mapGuarantorBpHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractGuarantorBpNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractGuarantorBpHistory, mapGuarantorBpHistory);
                hlsCusCtLonContractGuarantorBpHistory.set__status("add");
                hlsCusCtLonContractGuarantorBpHistory.setContractId(historyContractId);
                hlsCusCtLonContractGuarantorBpHistory.setGuaranteeBpId(null);
                hlsCusCtLonContractGuarantorBpService.insertSelective(request, hlsCusCtLonContractGuarantorBpHistory);
            }
        }

        //复制NORMAL合同的抵押信息备份
        if (hlsCusCtLonContractMortgageNormals.size() > 0) {
            for (HlsCusCtLonContractMortgage hlsCusCtLonContractMortgageNormal : hlsCusCtLonContractMortgageNormals
                    ) {
                HlsCusCtLonContractMortgage hlsCusCtLonContractMortgageHistory = new HlsCusCtLonContractMortgage();
                Map<String, String> mapMortgageHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractMortgageNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractMortgageHistory, mapMortgageHistory);
                hlsCusCtLonContractMortgageHistory.set__status("add");
                hlsCusCtLonContractMortgageHistory.setContractId(historyContractId);
                hlsCusCtLonContractMortgageHistory.setMortgageId(null);
                hlsCusCtLonContractMortgageService.insertSelective(request, hlsCusCtLonContractMortgageHistory);
            }
        }

        //复制NORMAL合同的质押信息备份
        if (hlsCusCtLonContractPledgeNormals.size() > 0) {
            for (HlsCusCtLonContractPledge hlsCusCtLonContractPledgeNormal : hlsCusCtLonContractPledgeNormals
                    ) {
                HlsCusCtLonContractPledge hlsCusCtLonContractPledgeHistory = new HlsCusCtLonContractPledge();
                Map<String, String> mapPledgeHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtLonContractPledgeNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractPledgeHistory, mapPledgeHistory);
                hlsCusCtLonContractPledgeHistory.set__status("add");
                hlsCusCtLonContractPledgeHistory.setContractId(historyContractId);
                hlsCusCtLonContractPledgeHistory.setPledgeId(null);
                hlsCusCtLonContractPledgeService.insertSelective(request, hlsCusCtLonContractPledgeHistory);
            }
        }

        //复制NORMAL合同的附件信息备份
        if (hlsCusLonContractAttachmentNormals.size() > 0) {
            for (HlsCusLonContractAttachment hlsCusLonContractAttachmentNormal : hlsCusLonContractAttachmentNormals
                    ) {
                HlsCusLonContractAttachment hlsCusLonContractAttachmentHistory = new HlsCusLonContractAttachment();
                Map<String, String> mapAttachmentHistory = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractAttachmentNormal);
                hlsBeanRefUtilService.setFieldValue(hlsCusLonContractAttachmentHistory, mapAttachmentHistory);
                hlsCusLonContractAttachmentHistory.set__status("add");
                hlsCusLonContractAttachmentHistory.setContractId(historyContractId);
                hlsCusLonContractAttachmentHistory.setContractAttachmentId(null);
                hlsCusILonContractAttachmentService.insertSelective(request, hlsCusLonContractAttachmentHistory);
            }
        }

        //将NORMAL状态的ABS资产包租赁合同置为不出表
        updateAbsContractShowFlag(request, lonContractNormal, "C");


        //覆盖NORMAL合同信息
        Map<String, String> mapLonContract = hlsBeanRefUtilService.getFieldValueMap(lonContractChangeReq);
        hlsBeanRefUtilService.setFieldValue(lonContractNormal, mapLonContract);
        lonContractNormal.set__status("update");
        lonContractNormal.setDataClass("NORMAL");
        lonContractNormal.setContractId(contractId);
        lonContractNormal.setContractStatus("APPROVED");
        self().updateByPrimaryKey(request, lonContractNormal);


        //获取CHANGE_REQ合同的交易信息
        Long quotationId = hlsCusLonContractQuotationNormal.getQuotationId();
        hlsCusLonContractQuotationTmp.setContractId(changeReqId);
        List<HlsCusLonContractQuotation> hlsCusLonContractQuotationChangeReqs = hlsCusLonContractQuotationService.select(request, hlsCusLonContractQuotationTmp, 1, 99999);
        HlsCusLonContractQuotation hlsCusLonContractQuotationChangeReq = hlsCusLonContractQuotationChangeReqs.get(0);
        //覆盖NORMAL合同交易信息
        Map<String, String> mapLonContractQuotation = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractQuotationChangeReq);
        hlsBeanRefUtilService.setFieldValue(hlsCusLonContractQuotationNormal, mapLonContractQuotation);
        hlsCusLonContractQuotationNormal.set__status("update");
        hlsCusLonContractQuotationNormal.setQuotationId(quotationId);
        hlsCusLonContractQuotationNormal.setContractId(contractId);
        hlsCusLonContractQuotationService.updateByPrimaryKey(request, hlsCusLonContractQuotationNormal);

        //删除NORMAL合同的关联银行信息
        HlsCusCtLonContractRefBankInfo ctLonContractRefBankInfo = new HlsCusCtLonContractRefBankInfo();
        ctLonContractRefBankInfo.setContractId(contractId);
        List<HlsCusCtLonContractRefBankInfo> ctLonContractRefBankInfos = hlsCusCtLonContractRefBankInfoService.select(request, ctLonContractRefBankInfo, 1, 99999);
        hlsCusCtLonContractRefBankInfoService.batchDelete(ctLonContractRefBankInfos);
        //复制CHANGE_REQ项下关联银行信息插入到NORMAL合同项下
        ctLonContractRefBankInfo.setContractId(changeReqId);
        List<HlsCusCtLonContractRefBankInfo> ctLonContractRefBankInfoChangeReqs = hlsCusCtLonContractRefBankInfoService.select(request, ctLonContractRefBankInfo, 1, 99999);
        if (ctLonContractRefBankInfoChangeReqs.size() > 0) {
            for (HlsCusCtLonContractRefBankInfo ctLonContractRefBankInfoChangeReq : ctLonContractRefBankInfoChangeReqs
                    ) {
                HlsCusCtLonContractRefBankInfo hlsCusCtLonContractRefBankInfoNormal = new HlsCusCtLonContractRefBankInfo();
                Map<String, String> mapBankInfoChangeReq = hlsBeanRefUtilService.getFieldValueMap(ctLonContractRefBankInfoChangeReq);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractRefBankInfoNormal, mapBankInfoChangeReq);
                hlsCusCtLonContractRefBankInfoNormal.set__status("add");
                hlsCusCtLonContractRefBankInfoNormal.setContractId(contractId);
                hlsCusCtLonContractRefBankInfoNormal.setRefBankId(null);
                hlsCusCtLonContractRefBankInfoService.insertSelective(request, hlsCusCtLonContractRefBankInfoNormal);
            }
        }

        //删除NORMAL合同的ABS资产包信息
        HlsCusCtAbsContract hlsCusCtAbsContract = new HlsCusCtAbsContract();
        hlsCusCtAbsContract.setLonContractId(contractId);
        List<HlsCusCtAbsContract> hlsCusCtAbsContracts = hlsCusCtAbsContractService.select(request, hlsCusCtAbsContract, 1, 999999);
        hlsCusCtAbsContractService.batchDelete(hlsCusCtAbsContracts);
        //复制CHANGE_REQ项下关联银行信息插入到NORMAL合同项下
        hlsCusCtAbsContract.setLonContractId(changeReqId);
        List<HlsCusCtAbsContract> hlsCusCtAbsContractsChangeReqs = hlsCusCtAbsContractService.select(request, hlsCusCtAbsContract, 1, 999999);
        if (hlsCusCtAbsContractsChangeReqs.size() > 0) {
            for (HlsCusCtAbsContract hlsCusCtAbsContractChangeReq : hlsCusCtAbsContractsChangeReqs
                    ) {
                HlsCusCtAbsContract hlsCusCtAbsContractNormal = new HlsCusCtAbsContract();
                Map<String, String> mapAbsContractChangeReq = hlsBeanRefUtilService.getFieldValueMap(hlsCusCtAbsContractChangeReq);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtAbsContractNormal, mapAbsContractChangeReq);
                hlsCusCtAbsContractNormal.set__status(DTOStatus.ADD);
                hlsCusCtAbsContractNormal.setLonContractId(contractId);
                hlsCusCtAbsContractNormal.setAbsConId(null);
                hlsCusCtAbsContractService.insertSelective(request, hlsCusCtAbsContractNormal);
            }
        }

        //将变更后的NORMAL合同项下资产包租赁合同置为出表
        updateAbsContractShowFlag(request, lonContractNormal, "Y");

        //删除NORMAL合同的承销商信息
        HlsCusCtLonContractConsignmentSalesInfo ctLonContractConsignmentSalesInfo = new HlsCusCtLonContractConsignmentSalesInfo();
        ctLonContractConsignmentSalesInfo.setContractId(contractId);
        List<HlsCusCtLonContractConsignmentSalesInfo> ctLonContractConsignmentSalesInfos = hlsCusCtLonContractConsignmentSalesInfoService.select(request, ctLonContractConsignmentSalesInfo, 1, 999999);
        hlsCusCtLonContractConsignmentSalesInfoService.batchDelete(ctLonContractConsignmentSalesInfos);
        //复制CHANGE_REQ项下承销商信息插入到NORMAL合同项下
        ctLonContractConsignmentSalesInfo.setContractId(changeReqId);
        List<HlsCusCtLonContractConsignmentSalesInfo> ctLonContractConsignmentSalesInfoChangeReqs = hlsCusCtLonContractConsignmentSalesInfoService.select(request, ctLonContractConsignmentSalesInfo, 1, 999999);
        if (ctLonContractConsignmentSalesInfoChangeReqs.size() > 0) {
            for (HlsCusCtLonContractConsignmentSalesInfo ctLonContractConsignmentSalesInfoChangeReq : ctLonContractConsignmentSalesInfoChangeReqs
                    ) {
                HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfoNormal = new HlsCusCtLonContractConsignmentSalesInfo();
                Map<String, String> mapConsignmentSalesInfoChangeReq = hlsBeanRefUtilService.getFieldValueMap(ctLonContractConsignmentSalesInfoChangeReq);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractConsignmentSalesInfoNormal, mapConsignmentSalesInfoChangeReq);
                hlsCusCtLonContractConsignmentSalesInfoNormal.set__status("add");
                hlsCusCtLonContractConsignmentSalesInfoNormal.setContractId(contractId);
                hlsCusCtLonContractConsignmentSalesInfoNormal.setConsignmentSalesId(null);
                hlsCusCtLonContractConsignmentSalesInfoService.insertSelective(request, hlsCusCtLonContractConsignmentSalesInfoNormal);
            }
        }

        //删除NORMAL合同的提款和还款信息
        HlsCusLonContractWithdrawPlan lonContractWithdrawPlan = new HlsCusLonContractWithdrawPlan();
        lonContractWithdrawPlan.setContractId(contractId);
        List<HlsCusLonContractWithdrawPlan> lonContractWithdrawPlans = hlsCusLonContractWithdrawPlanService.select(request, lonContractWithdrawPlan, 1, 999999);
        if (lonContractWithdrawPlans.size() > 0) {
            for (HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlan : lonContractWithdrawPlans
                    ) {
                if (hlsCusLonContractWithdrawPlan.getWithdrawId() == null) {
                    HlsCusLonContractRepaymentPlan lonContractRepaymentPlan = new HlsCusLonContractRepaymentPlan();
                    lonContractRepaymentPlan.setContractId(contractId);
                    lonContractRepaymentPlan.setWithdrawPlanId(hlsCusLonContractWithdrawPlan.getWithdrawPlanId());
                    List<HlsCusLonContractRepaymentPlan> lonContractRepaymentPlans = hlsCusLonContractRepaymentPlanService.select(request, lonContractRepaymentPlan, 1, 999999);
                    hlsCusLonContractRepaymentPlanService.batchDelete(lonContractRepaymentPlans);
                    hlsCusLonContractWithdrawPlanService.deleteByPrimaryKey(hlsCusLonContractWithdrawPlan);
                }
            }
        }
        //复制CHANGE_REQ项下提款和还款信息插入到NORMAL合同项下
        lonContractWithdrawPlan.setContractId(changeReqId);
        List<HlsCusLonContractWithdrawPlan> lonContractWithdrawPlanChangeReqs = hlsCusLonContractWithdrawPlanService.select(request, lonContractWithdrawPlan, 1, 999999);
        if (lonContractWithdrawPlanChangeReqs.size() > 0) {
            for (HlsCusLonContractWithdrawPlan lonContractWithdrawPlanChangeReq : lonContractWithdrawPlanChangeReqs
                    ) {
                if (lonContractWithdrawPlanChangeReq.getWithdrawId() == null) {
                    Long changeReqWithdrawPlanId = lonContractWithdrawPlanChangeReq.getWithdrawPlanId();

                    HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlanNormal = new HlsCusLonContractWithdrawPlan();
                    Map<String, String> mapWithdrawPlanChangeReq = hlsBeanRefUtilService.getFieldValueMap(lonContractWithdrawPlanChangeReq);
                    hlsBeanRefUtilService.setFieldValue(hlsCusLonContractWithdrawPlanNormal, mapWithdrawPlanChangeReq);
                    hlsCusLonContractWithdrawPlanNormal.set__status("add");
                    hlsCusLonContractWithdrawPlanNormal.setContractId(contractId);
                    hlsCusLonContractWithdrawPlanNormal.setWithdrawPlanId(null);
                    hlsCusLonContractWithdrawPlanNormal = hlsCusLonContractWithdrawPlanService.insertSelective(request, hlsCusLonContractWithdrawPlanNormal);

                    Long normalWithdrawPlanId = hlsCusLonContractWithdrawPlanNormal.getWithdrawPlanId();

                    //获取CHANGE_REQ合同的提款项下还款计划
                    HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanTmp = new HlsCusLonContractRepaymentPlan();
                    hlsCusLonContractRepaymentPlanTmp.setWithdrawPlanId(changeReqWithdrawPlanId);
                    List<HlsCusLonContractRepaymentPlan> hlsCusLonContractRepaymentPlanChangeReqs = hlsCusLonContractRepaymentPlanService.select(request, hlsCusLonContractRepaymentPlanTmp, 1, 999999);

                    //复制还款计划
                    if (hlsCusLonContractRepaymentPlanChangeReqs.size() > 0) {
                        for (HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanChangeReq : hlsCusLonContractRepaymentPlanChangeReqs
                                ) {
                            HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlanNormal = new HlsCusLonContractRepaymentPlan();
                            Map<String, String> mapRepaymentPlanChangeReq = hlsBeanRefUtilService.getFieldValueMap(hlsCusLonContractRepaymentPlanChangeReq);
                            hlsBeanRefUtilService.setFieldValue(hlsCusLonContractRepaymentPlanNormal, mapRepaymentPlanChangeReq);
                            hlsCusLonContractRepaymentPlanNormal.set__status("add");
                            hlsCusLonContractRepaymentPlanNormal.setWithdrawPlanId(normalWithdrawPlanId);
                            hlsCusLonContractRepaymentPlanNormal.setContractId(contractId);
                            hlsCusLonContractRepaymentPlanNormal.setRepaymentPlanId(null);
                            hlsCusLonContractRepaymentPlanService.insertSelective(request, hlsCusLonContractRepaymentPlanNormal);
                        }
                    }
                }
            }
        }

        //删除NORMAL合同的担保信息
        HlsCusCtLonContractGuarantorBp ctLonContractGuarantorBp = new HlsCusCtLonContractGuarantorBp();
        ctLonContractGuarantorBp.setContractId(contractId);
        List<HlsCusCtLonContractGuarantorBp> ctLonContractGuarantorBps = hlsCusCtLonContractGuarantorBpService.select(request, ctLonContractGuarantorBp, 1, 999999);
        hlsCusCtLonContractGuarantorBpService.batchDelete(ctLonContractGuarantorBps);
        //复制CHANGE_REQ项下的担保信息插入到NORMAL合同项下
        ctLonContractGuarantorBp.setContractId(changeReqId);
        List<HlsCusCtLonContractGuarantorBp> ctLonContractGuarantorBpChangeReqs = hlsCusCtLonContractGuarantorBpService.select(request, ctLonContractGuarantorBp, 1, 999999);
        if (ctLonContractGuarantorBpChangeReqs.size() > 0) {
            for (HlsCusCtLonContractGuarantorBp ctLonContractGuarantorBpChangeReq : ctLonContractGuarantorBpChangeReqs
                    ) {
                HlsCusCtLonContractGuarantorBp hlsCusCtLonContractGuarantorBpNormal = new HlsCusCtLonContractGuarantorBp();
                Map<String, String> mapGuarantorBpChangeReq = hlsBeanRefUtilService.getFieldValueMap(ctLonContractGuarantorBpChangeReq);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractGuarantorBpNormal, mapGuarantorBpChangeReq);
                hlsCusCtLonContractGuarantorBpNormal.set__status("add");
                hlsCusCtLonContractGuarantorBpNormal.setContractId(contractId);
                hlsCusCtLonContractGuarantorBpNormal.setGuaranteeBpId(null);
                hlsCusCtLonContractGuarantorBpService.insertSelective(request, hlsCusCtLonContractGuarantorBpNormal);
            }
        }

        //删除NORMAL合同的抵押信息
        HlsCusCtLonContractMortgage ctLonContractMortgage = new HlsCusCtLonContractMortgage();
        ctLonContractMortgage.setContractId(contractId);
        List<HlsCusCtLonContractMortgage> ctLonContractMortgages = hlsCusCtLonContractMortgageService.select(request, ctLonContractMortgage, 1, 999999);
        hlsCusCtLonContractMortgageService.batchDelete(ctLonContractMortgages);
        //复制CHANGE_REQ项下的抵押信息插入到NORMAL合同项下
        ctLonContractMortgage.setContractId(changeReqId);
        List<HlsCusCtLonContractMortgage> ctLonContractMortgageChangeReqs = hlsCusCtLonContractMortgageService.select(request, ctLonContractMortgage, 1, 999999);
        if (ctLonContractMortgageChangeReqs.size() > 0) {
            for (HlsCusCtLonContractMortgage ctLonContractMortgageChangeReq : ctLonContractMortgageChangeReqs
                    ) {
                HlsCusCtLonContractMortgage hlsCusCtLonContractMortgageNormal = new HlsCusCtLonContractMortgage();
                Map<String, String> mapMortgageChangeReq = hlsBeanRefUtilService.getFieldValueMap(ctLonContractMortgageChangeReq);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractMortgageNormal, mapMortgageChangeReq);
                hlsCusCtLonContractMortgageNormal.set__status("add");
                hlsCusCtLonContractMortgageNormal.setContractId(contractId);
                hlsCusCtLonContractMortgageNormal.setMortgageId(null);
                hlsCusCtLonContractMortgageService.insertSelective(request, hlsCusCtLonContractMortgageNormal);
            }
        }

        //删除NORMAL合同的质押信息
        HlsCusCtLonContractPledge ctLonContractPledge = new HlsCusCtLonContractPledge();
        ctLonContractPledge.setContractId(contractId);
        List<HlsCusCtLonContractPledge> ctLonContractPledges = hlsCusCtLonContractPledgeService.select(request, ctLonContractPledge, 1, 999999);
        hlsCusCtLonContractPledgeService.batchDelete(ctLonContractPledges);
        //复制CHANGE_REQ项下的质押信息插入到NORMAL合同项下
        ctLonContractPledge.setContractId(changeReqId);
        List<HlsCusCtLonContractPledge> ctLonContractPledgeChangeReqs = hlsCusCtLonContractPledgeService.select(request, ctLonContractPledge, 1, 999999);
        if (ctLonContractPledgeChangeReqs.size() > 0) {
            for (HlsCusCtLonContractPledge ctLonContractPledgeChangeReq : ctLonContractPledgeChangeReqs
                    ) {
                HlsCusCtLonContractPledge hlsCusCtLonContractPledgeNormal = new HlsCusCtLonContractPledge();
                Map<String, String> mapPledgeChangeReq = hlsBeanRefUtilService.getFieldValueMap(ctLonContractPledgeChangeReq);
                hlsBeanRefUtilService.setFieldValue(hlsCusCtLonContractPledgeNormal, mapPledgeChangeReq);
                hlsCusCtLonContractPledgeNormal.set__status("add");
                hlsCusCtLonContractPledgeNormal.setContractId(contractId);
                hlsCusCtLonContractPledgeNormal.setPledgeId(null);
                hlsCusCtLonContractPledgeService.insertSelective(request, hlsCusCtLonContractPledgeNormal);
            }
        }

        //删除NORMAL合同的附件信息
        HlsCusLonContractAttachment lonContractAttachment = new HlsCusLonContractAttachment();
        lonContractAttachment.setContractId(contractId);
        List<HlsCusLonContractAttachment> lonContractAttachments = hlsCusILonContractAttachmentService.select(request, lonContractAttachment, 1, 999999);
        hlsCusILonContractAttachmentService.batchDelete(lonContractAttachments);
        //复制CHANGE_REQ项下的附件信息插入到NORMAL合同项下
        lonContractAttachment.setContractId(changeReqId);
        List<HlsCusLonContractAttachment> lonContractAttachmentChangeReqs = hlsCusILonContractAttachmentService.select(request, lonContractAttachment, 1, 999999);
        if (lonContractAttachmentChangeReqs.size() > 0) {
            for (HlsCusLonContractAttachment lonContractAttachmentChangeReq : lonContractAttachmentChangeReqs
                    ) {
                HlsCusLonContractAttachment hlsCusLonContractAttachmentNormal = new HlsCusLonContractAttachment();
                Map<String, String> mapAttachmentChangeReq = hlsBeanRefUtilService.getFieldValueMap(lonContractAttachmentChangeReq);
                hlsBeanRefUtilService.setFieldValue(hlsCusLonContractAttachmentNormal, mapAttachmentChangeReq);
                hlsCusLonContractAttachmentNormal.set__status("add");
                hlsCusLonContractAttachmentNormal.setContractId(contractId);
                hlsCusLonContractAttachmentNormal.setContractAttachmentId(null);
                hlsCusILonContractAttachmentService.insertSelective(request, hlsCusLonContractAttachmentNormal);
            }
        }

    }

    @Override
    //取消变更
    public void cancelLonContractChangeReq(IRequest iRequest, HlsCusLonContract lonContract, HttpSession session) {
        //获取变更记录
        HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReq = new HlsCusCtLonContractChangeReq();
        hlsCusCtLonContractChangeReq.setChangeReqId(lonContract.getContractId());
        List<HlsCusCtLonContractChangeReq> hlsCusCtLonContractChangeReqs = hlsCusCtLonContractChangeReqService.select(iRequest, hlsCusCtLonContractChangeReq, 1, 999999);
        hlsCusCtLonContractChangeReq = hlsCusCtLonContractChangeReqs.get(0);

        //获取NORMAL合同
        HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
        hlsCusLonContract.setContractId(hlsCusCtLonContractChangeReq.getContractId());
        hlsCusLonContract = self().selectByPrimaryKey(iRequest, hlsCusLonContract);

        //更新CHANGE_REQ合同为取消状态
        lonContract.set__status("update");
        lonContract.setContractStatus("CANCEL");
        self().updateByPrimaryKeySelective(iRequest, lonContract);

        //更新变更记录为取消状态
        hlsCusCtLonContractChangeReq.setReqStatus("CANCEL");
        hlsCusCtLonContractChangeReqMapper.updateChangeReq(hlsCusCtLonContractChangeReq);

        //更新NORMAL合同为审批通过状态
        hlsCusLonContract.set__status("update");
        hlsCusLonContract.setContractStatus("APPROVED");
        self().updateByPrimaryKeySelective(iRequest, hlsCusLonContract);
    }

    @Override
    public List<HlsCusLonContract> lonContractQuery(IRequest request, HlsCusLonContract lonContract, int page, int pageSize) {
       // PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<String, Object>();
        String[] listDocumentType = null;
        String[] listContractStatus = null;
        String[] listCurrency = null;
        String[] listFinancingChannel = null;
        String[] listDomesticOverseaFinance = null;
        String[] listWithdrawStatus=null;

        if (lonContract.getDocumentTypes() != null) {
            listDocumentType = lonContract.getDocumentTypes().split(",");
        }
        if (lonContract.getContractStatuses() != null) {
            listContractStatus = lonContract.getContractStatuses().split(",");
        }
        if (lonContract.getCurrencys() != null) {
            listCurrency = lonContract.getCurrencys().split(",");
        }
        if (lonContract.getFinancingChannels() != null) {
            listFinancingChannel = lonContract.getFinancingChannels().split(",");
        }
        if (lonContract.getDomesticOverseaFinances() != null) {
            listDomesticOverseaFinance = lonContract.getDomesticOverseaFinances().split(",");
        }
        if(lonContract.getWithdrawStatuses()!=null){
            listWithdrawStatus=lonContract.getWithdrawStatuses().split(",");
        }
        params.put("companyId", lonContract.getCompanyId());
        params.put("listDocumentType", listDocumentType);
        params.put("listContractStatus", listContractStatus);
        params.put("listCurrency", listCurrency);
        params.put("listFinancingChannel", listFinancingChannel);
        params.put("listDomesticOverseaFinance", listDomesticOverseaFinance);
        params.put("amtFirst", lonContract.getAmtFirst());
        params.put("amtSecond", lonContract.getAmtSecond());
        params.put("amtThird", lonContract.getAmtThird());
        params.put("amtFrom", lonContract.getAmtFrom());
        params.put("amtTo", lonContract.getAmtTo());
        params.put("contractNumber", lonContract.getContractNumber());
        params.put("contractName", lonContract.getContractName());
        params.put("creditBpName", lonContract.getCreditBpName());
        params.put("creditContractName", lonContract.getCreditContractName());
        params.put("creditConNumber", lonContract.getCreditConNumber());
        params.put("guarantorName", lonContract.getGuarantorName());
        params.put("orgTypeDesc", lonContract.getOrgTypeDesc());
        params.put("listWithdrawStatus",listWithdrawStatus);
        params.put("majorContractNumber", lonContract.getMajorContractNumber());
        return lonContractMapper.lonContractQuery(params);
    }

    @Override
    public List<HlsCusLonContract> lonContractChangeReqQuery(IRequest request, HlsCusLonContract lonContract, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<String, Object>();
        String[] listDocumentType = null;
        String[] listContractStatus = null;
        String[] listCurrency = null;
        String[] listFinancingChannel = null;
        String[] listDomesticOverseaFinance = null;

        if (lonContract.getDocumentTypes() != null) {
            listDocumentType = lonContract.getDocumentTypes().split(",");
        }
        if (lonContract.getContractStatuses() != null) {
            listContractStatus = lonContract.getContractStatuses().split(",");
        }
        if (lonContract.getCurrencys() != null) {
            listCurrency = lonContract.getCurrencys().split(",");
        }
        if (lonContract.getFinancingChannels() != null) {
            listFinancingChannel = lonContract.getFinancingChannels().split(",");
        }
        if (lonContract.getDomesticOverseaFinances() != null) {
            listDomesticOverseaFinance = lonContract.getDomesticOverseaFinances().split(",");
        }
        params.put("companyId", lonContract.getCompanyId());
        params.put("listDocumentType", listDocumentType);
        params.put("listContractStatus", listContractStatus);
        params.put("listCurrency", listCurrency);
        params.put("listFinancingChannel", listFinancingChannel);
        params.put("listDomesticOverseaFinance", listDomesticOverseaFinance);
        params.put("amtFrom", lonContract.getAmtFrom());
        params.put("amtTo", lonContract.getAmtTo());
        params.put("reqDateFrom", lonContract.getReqDateFrom());
        params.put("reqDateTo", lonContract.getReqDateTo());
        params.put("contractNumber", lonContract.getContractNumber());
        params.put("contractName", lonContract.getContractName());
        params.put("creditBpName", lonContract.getCreditBpName());
        params.put("creditContractName", lonContract.getCreditContractName());
        params.put("creditConNumber", lonContract.getCreditConNumber());
        return lonContractMapper.lonContractChangeReqQuery(params);
    }

    //ABS贷款合同关联租赁合同出表
    @Override
    public void updateAbsContractShowFlag(IRequest request, HlsCusLonContract lonContract, String absShowFlag) {
        HlsCusCtAbsContract hlsCusCtAbsContractTmp = new HlsCusCtAbsContract();
        hlsCusCtAbsContractTmp.setLonContractId(lonContract.getContractId());
        List<HlsCusCtAbsContract> hlsCusCtAbsContractList = hlsCusCtAbsContractService.select(request, hlsCusCtAbsContractTmp, 1, 999999);
        if (hlsCusCtAbsContractList.size() > 0) {
            for (HlsCusCtAbsContract hlsCusCtAbsContract : hlsCusCtAbsContractList
                    ) {
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(hlsCusCtAbsContract.getContractId());
                hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(request, hlsCusConContract);

                hlsCusConContract.set__status(DTOStatus.UPDATE);
                hlsCusConContract.setAbsShowFlag(absShowFlag);
                hlsCusConContractService.updateByPrimaryKeySelective(request, hlsCusConContract);
            }
        }
    }

    //更新董事会相关信息
    @Override
    public void updateLonContractApproval(IRequest request, HlsCusLonContract lonContract) {
        HlsCusLonContract lonContractResult = new HlsCusLonContract();
        lonContractResult.setContractId(lonContract.getContractId());
        lonContractResult = self().selectByPrimaryKey(request, lonContractResult);

        lonContractResult.setApprovalResult(lonContract.getApprovalResult());
        lonContractResult.setApprovalComment(lonContract.getApprovalComment());

        self().updateByPrimaryKeySelective(request, lonContractResult);

    }


    @Override
    public int updateContractSettleStatus() {
        return lonContractMapper.updateContractSettleStatus();
    }

    @Override
    public int selectWithdrawValidStatus(IRequest requestContext , HlsCusLonContract hlsCusLonContract) {
        return lonContractMapper.selectWithdrawValidStatus(hlsCusLonContract);
    }

    @Override
    public int updateLonContractSourceCreditLineId(Long creditContractId, Long creditLineId, Long sourceCreditLineId) {
        return lonContractMapper.updateLonContractSourceCreditLineId(creditContractId,creditLineId,sourceCreditLineId);
    }

    @Override
    public List<HlsCusLonContract> contractNumberCheck(String contractNumber) {
        return lonContractMapper.contractNumberCheck(contractNumber);
    }
}
