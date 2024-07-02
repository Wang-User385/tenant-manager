package com.hand.hls.hls.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.cont.service.ConChangeEtInfoService;
import com.hand.hls.cont.service.ConChangeRepaymentInfoService;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.*;
import com.hand.hls.hls.mapper.*;
import com.hand.hls.hls.service.*;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;
import com.hand.hls.pam.mapper.HlsLeaseItemDetailMapper;
import com.hand.hls.pam.service.IHlsLeaseItemDetailService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.*;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.utils.HlsCusDownloadDocxUtil;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationHdServiceImpl extends BaseServiceImpl<HlsDurationHd> implements HlsDurationHdService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsDurationLnService hlsDurationLnService;

    @Autowired
    private HlsDurationDepositService hlsDurationDepositService;

    @Autowired
    private HlsDurationCompareService hlsDurationCompareService;

    @Autowired
    private HlsDurationAttachmentService hlsDurationAttachmentService;


    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private HlsDurationHdMapper hlsDurationHdMapper;

    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;

    @Autowired
    private HlsDurationDepositMapper hlsDurationDepositMapper;

    @Autowired
    private HlsDurationCompareMapper hlsDurationCompareMapper;


    @Autowired
    private HlsDurationCalcService hlsDurationCalcService;

    @Autowired
    private HlsDurationItemService hlsDurationItemService;

    @Autowired
    private HlsDurationItemMapper hlsDurationItemMapper;

    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;

    @Autowired
    private HlsCusConContractCashflowService contractCashflowService;

    @Autowired
    private HlsCusPrjProjectBpMapper prjProjectBpMapper;

    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;

    @Autowired
    private IPrjProjectBpService prjProjectBpService;

    @Autowired
    private HlsCusConContractMapper conContractMapper;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusPrjProjectLeaseItemService hlsCusPrjProjectLeaseItemService;
    @Autowired
    private IHlsCusPrjProjectChanceMpService mpService;

    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;

    @Autowired
    private HlsLeaseItemDetailMapper hlsLeaseItemDetailMapper;
    @Autowired
    private IHlsLeaseItemDetailService hlsLeaseItemDetailService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private IHlsWebExcelCalcResultService hlsWebExcelCalcResultService;
    @Autowired
    private HlsWebExcelCalcResultMapper hlsWebExcelCalcResultMapper;
    @Autowired
    private HlsWebExcelMapper webExcelMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;

    @Autowired
    private HlsCusOffBalanceAccountService hlsCusOffBalanceAccountService;
    @Autowired
    private HlsCusConContractService cusConContractService;
    @Autowired
    private PrjProjectApprovalMapper prjProjectApprovalMapper;
    @Autowired
    private ProjectApprovalConditionMapper conditionMapper;
    @Autowired
    private IProjectApprovalService projectApprovalService;

    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;

    @Autowired
    private ConChangeEtInfoService conChangeEtInfoSerivce;

    @Autowired
    private ConChangeRepaymentInfoService conChangeRepaymentInfoService;

    private static final String RELEASE = "RELEASE";
    private static final String REPURCHASE = "REPURCHASE";
    private static final String BP_INFO = "BP_INFO";
    private static final String BLOCK = "BLOCK";
    private static final String PRJ_PROJECT = "PRJ_PROJECT";

    private static final String LEASE_ITEMS = "LEASE_ITEMS";
    private static final String PLEDGOR_MORTGAGORS = "PLEDGOR_MORTGAGORS";


    private static String DOCUMENT_CATEGORY = "DURATION";
    private static String DOCUMENT_TYPE = "DURATION";
    private static String BUSINESS_TYPE = "DURATION";


    private static String EXECUTE_DOCUMENT_CATEGORY = "CONTRACT_CHANGE";
    private static String EXECUTE_DOCUMENT_TYPE = "STD";
    private static String EXECUTE_BUSINESS_TYPE = "CONTRACT_CHANGE";

    private static final String ET = "ET";
    private static final String CON_TERMINATION = "CON_TERMINATION";
    private static final String PREPAYMENT = "PREPAYMENT";
    private static final String TERMINATE = "TERMINATE";

    private static final String FINANCIAL_TERMS = "FINANCIAL_TERMS";
    @Autowired
    private HlsCusPrjProjectMeetingMapper meetingMapper;
    @Autowired
    private PrjProjectMeetingService meetingService;
    @Autowired
    private ProjectMeetingApproverMapper approverMapper;
    @Autowired
    private IProjectMeetingApproverService approverService;
    @Autowired
    private PrjProjectApprovalMapper approvalMapper;
    @Autowired
    private IProjectApprovalService approvalService;
    @Autowired
    private ProjectApprovalConditionService conditionService;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * PROJECT           项目变更审批事项工作联系单
     * SPECIAL           特殊事项
     * RELEASE           解押
     * REPURCHASE        回购
     * DEPOSIT           保证金释放、退还
     * TERMINATE         正常结清
     * ET                提前结清
     * PREPAYMENT        提前还款
     */
    enum durationType {
        PROJECT, SPECIAL, RELEASE, REPURCHASE, DEPOSIT, TERMINATE, ET, PREPAYMENT, BUSINESS_CHANGE, CON_TERMINATION
    }

    /**
     * DEPOSIT     保证金
     * INTEREST    保证金利息
     * CREDIT      债权
     */
    enum depositType {
        DEPOSIT, INTEREST, CREDIT
    }

    /**
     * FINANCIAL_TERMS, 金融条款变更
     * ET  提前结清
     * PREPAYMENT, 提前还款
     * BP_INFO, 客户信息
     * RELEASE, 解押
     * REPURCHASE, 回购
     * AGREEMENT, 补充协议签订
     * PLEDGOR_MORTGAGORS 抵质押物
     * LEASE_ITEMS 租赁物
     */
    enum sourceType {
        FINANCIAL_TERMS, ET, PREPAYMENT, RELEASE, REPURCHASE, BP_INFO, AGREEMENT, PLEDGOR_MORTGAGORS, LEASE_ITEMS
    }


    /**
     * FINANCIAL_TERMS, 金融条款变更
     * ET  提前结清
     * PREPAYMENT, 提前还款
     * RELEASE, 解押
     * REPURCHASE, 回购
     */
    enum executeType {
        FINANCIAL_TERMS, ET, PREPAYMENT, RELEASE, REPURCHASE
    }

    private void rsidualAmountCheck(HlsDurationLn checkData, String msg) throws ResMessageException {
        if (sub(add(nvl(checkData.getRefundAmount(), 0.0), nvl(checkData.getDeductionAmount(), 0.0)),

                nvl(checkData.getResidualAmount(), 0.0)) > 0) {
            throw new ResMessageException(msg + ",本次退款金额: " + checkData.getRefundAmount() + "与本次抵扣金额: " + checkData.getDeductionAmount()
                    + " 合计超过余额: " + checkData.getResidualAmount());
        }
    }

    private void deductionAmountCheck(HlsDurationLn checkData) throws ResMessageException {
        if (sub(nvl(checkData.getDeductionAmount(), 0.0), nvl(checkData.getResidualAmount(), 0.0)) > 0) {
            throw new ResMessageException("本次抵扣金额: " + checkData.getDeductionAmount() + " 大于剩余金额: " + checkData.getResidualAmount());
        }
    }

    private void itemAmountCheck(HlsDurationLn checkData, String sourceType) throws ResMessageException {
        if (sourceType.equals(durationType.RELEASE.name())) {
            if (sub(checkData.getReleaseAmount(), checkData.getMortgagePledgeAmount()) > 0) {
                throw new ResMessageException("解押后抵质押金额: " + checkData.getReleaseAmount() + " 大于抵质押金额: " + checkData.getMortgagePledgeAmount());
            }
        } else if (sourceType.equals(durationType.REPURCHASE.name())) {
            if (sub(checkData.getReleaseAmount(), checkData.getNetAssetValueNow()) > 0) {
                throw new ResMessageException("回购后账面净值: " + checkData.getReleaseAmount() + " 大于账面净值(当前): " + checkData.getMortgagePledgeAmount());
            }
        }

    }


    //项目批复变更
    //思路 创建复制一条change的变更信息去做信息维护 审批通过则用change覆盖normal  原normal留版变更history
    @Override
    public List<HlsDurationHd> hlsDurationReplySave(IRequest iRequest, HttpSession session, HlsDurationHd hd) throws ParseException {
        List<HlsDurationHd> hdList = new ArrayList<>();
        HlsDurationHd hlsDurationHd = new HlsDurationHd();
        if (hd.getProjectId() != null) {
            Map<String, String> params = new HashMap<>();
            hd.setDurationStatus("NEW");
            hd.setDurationType("REPLY");
            String durationNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
            hd.setDurationNumber(durationNumber);
            hd.setApplyDate(df.parse(df.format(new Date())));
            hd.setApplyPerson(iRequest.getUserId());
            hd.setApplyUnitId(Long.parseLong(String.valueOf(session.getAttribute("unitId"))));
            hlsDurationHd = self().insertSelective(iRequest, hd);
            //复制上会信息
            HlsCusPrjProjectMeeting meeting = new HlsCusPrjProjectMeeting();
            meeting.setProjectId(hlsDurationHd.getProjectId());
            List<HlsCusPrjProjectMeeting> meetingList = meetingMapper.queryInfo(meeting);
            if (meetingList.size() > 0) {
                //正常来说一个项目只有一条会议信 NORMAL 所以下面默认循环一次
                for (HlsCusPrjProjectMeeting projectMeeting : meetingList) {
                    BeanUtils.copyProperties(projectMeeting, meeting);
                    meeting.setProjectMeetingId(null);
                    meeting.setDataClass("CHANGE");
                    meetingService.insertSelective(iRequest, meeting);
                }
                //上会审批人员复制
                ProjectMeetingApprover approver = new ProjectMeetingApprover();
                approver.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approverList = approverMapper.queryAllByProjectId(approver);
                if (approverList.size() > 0) {
                    for (ProjectMeetingApprover meetingApprover : approverList) {
                        BeanUtils.copyProperties(meetingApprover, approver);
                        approver.setProjectMeetingId(meeting.getProjectMeetingId());
                        approver.setDataClass("CHANGE");
                        approver.setApproverRecordId(null);
                        approverService.insertSelective(iRequest, approver);
                    }
                }
                //复制批复信息
                PrjProjectApproval approval = new PrjProjectApproval();
                approval.setProjectId(hlsDurationHd.getProjectId().toString());
                List<PrjProjectApproval> approvalList = approvalMapper.queryAll(approval);
                if (approvalList.size() > 0) {
                    for (PrjProjectApproval projectApproval : approvalList) {
                        BeanUtils.copyProperties(projectApproval, approval);
                        //创建时将 原信息留版 存入变更记录（用于审批通过后留版给history）
                        approval.setIntRateHis(projectApproval.getIntRate());
                        approval.setFinanceAmountHis(projectApproval.getFinanceAmount());
                        approval.setLeaseTermHis(projectApproval.getLeaseTerm());
                        approval.setApprovalId(null);
                        approval.setDataClass("CHANGE");
                        approvalService.insertSelective(iRequest, approval);
                        //批复条件
                        ProjectApprovalCondition condition = new ProjectApprovalCondition();
                        condition.setApprovalId(projectApproval.getApprovalId());
                        List<ProjectApprovalCondition> conditionList = conditionMapper.queryAll(condition);
                        if (conditionList.size() > 0) {
                            for (ProjectApprovalCondition approvalCondition : conditionList) {
                                BeanUtils.copyProperties(approvalCondition, condition);
                                condition.setApprovalRecordId(null);
                                condition.setApprovalId(approval.getApprovalId());
                                conditionService.insertSelective(iRequest, condition);
                            }
                        }
                    }
                }

            }


        } else {
            throw new RuntimeException("请先选择项目编号！ ");
        }
        hdList.add(hlsDurationHd);
        return hdList;
    }

    @Override
    public List<HlsDurationHd> hlsDurationSave(IRequest iRequest, HlsDurationHd hd) throws ResMessageException {
        //保存头表
        Map<String, String> params = new HashMap<>();

        Long hdId = hd.getHdId();

        //数据校验

        //保证金释放、退还 && 正常结清 && 提前结清 && 提前还款
        //重复性校验
        String hdDurationType = hd.getDurationType();
        List<HlsDurationDeposit> hlsDurationDeposits = hd.getHlsDurationDepositList();
        List<Long> contractIds = new ArrayList<>();
        for (HlsDurationDeposit hlsDurationDeposit : hlsDurationDeposits) {
            if (hlsDurationDeposit.getContractId() != null) {
                contractIds.add(hlsDurationDeposit.getContractId());
            }
        }

        // 先注释-为了军军老师测试
        if (ET.equalsIgnoreCase(hdDurationType) || PREPAYMENT.equalsIgnoreCase(hdDurationType) || TERMINATE.equalsIgnoreCase(hdDurationType)) {
            contractCheckList(contractIds, hd, hdDurationType);
        } else {
            contractCheck(hd.getContractId(), hd);
        }

        //金额校验
        if (hd.getDurationType().equals(durationType.DEPOSIT.name()) ||
                hd.getDurationType().equals(durationType.TERMINATE.name()) ||
                hd.getDurationType().equals(durationType.ET.name()) ||
                hd.getDurationType().equals(durationType.PREPAYMENT.name())) {

            List<HlsDurationLn> checkDatas = hd.getHlsDurationLnList();

            Double deductionAmount = 0.0;
            Double deductionAmountCompare = 0.0;

            for (HlsDurationLn checkData : checkDatas) {
                String msg = "";
                if (checkData.getSourceType().equals(depositType.DEPOSIT.name())) {
                    msg = "保证金退还";
                    rsidualAmountCheck(checkData, msg);
                    deductionAmount = add(deductionAmount, nvl(checkData.getDeductionAmount(), 0.0));

                } else if (checkData.getSourceType().equals(depositType.INTEREST.name())) {
                    msg = "保证金利息";
                    rsidualAmountCheck(checkData, msg);
                    deductionAmount = add(deductionAmount, nvl(checkData.getDeductionAmount(), 0.0));

                } else if (checkData.getSourceType().equals(depositType.CREDIT.name())) {
                    deductionAmountCheck(checkData);
                    deductionAmountCompare = add(deductionAmountCompare, nvl(checkData.getDeductionAmount(), 0.0));

                } else if (checkData.getSourceType().equals(durationType.RELEASE.name()) || checkData.getSourceType().equals(durationType.REPURCHASE.name())) {
                    itemAmountCheck(checkData, checkData.getSourceType());
                }

            }
            //保证金退还抵扣金额 + 保证金利息抵扣金额 =  债权抵扣金额
            if (deductionAmount.compareTo(deductionAmountCompare) != 0) {
                throw new ResMessageException("保证金(含利息)抵扣金额合计: " + deductionAmount + " 与债权抵扣金额合计: " + new BigDecimal(deductionAmountCompare).toPlainString() + " 不相等!");
            }
        }


        if (durationType.DEPOSIT.name().equals(hd.getDurationType())) {
            hd.setDepositChangeFlag("Y");
        } else if (durationType.REPURCHASE.name().equals(hd.getDurationType())) {
            hd.setRepurchaseChangeFlag("Y");
        } else if (durationType.RELEASE.name().equals(hd.getDurationType())) {
            hd.setReleaseChangeFlag("Y");
        }

        //新增
        if (hdId == null) {
            //获取编码规则
            String durationNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
            hd.setDurationNumber(durationNumber);
            hd.setDurationStatus("NEW");
            hd.setExecuteStatus("NEW");
            hd.setExecuteFlag("N");
            HlsDurationHd hlsDurationHd = self().insertSelective(iRequest, hd);
            hdId = hlsDurationHd.getHdId();
        } else {
            //维护
            self().updateByPrimaryKeySelective(iRequest, hd);
        }
        Long id = hdId;

        //保证金冲抵、退还方案
        List<HlsDurationDeposit> hlsDurationDepositList = hd.getHlsDurationDepositList();

        //删除掉不属于 当前合同的 保证金数据
        HlsDurationLn durationLn = new HlsDurationLn();
        durationLn.setHdId(id);
        List<HlsDurationLn> lnList = hlsDurationLnMapper.select(durationLn);
        //规避掉正常结清单据保存不了的情况，但是仍可能存在问题
        if (hd.getContractId() != null) {
            List<HlsDurationLn> deleteDeposit = lnList.stream().filter(info -> depositType.DEPOSIT.name().equals(info.getSourceType()) || depositType.INTEREST.name().equals(info.getSourceType()))
                    .filter(item -> item.getContractId().compareTo(hd.getContractId()) != 0).collect(Collectors.toList());
            hlsDurationLnService.batchDelete(deleteDeposit);
        }


        hlsDurationDepositList.forEach(item -> {
            Double depositResidualAmount = 0.0;
            //除去正常结清 维度是 虚拟合同， 其他的变更都是 支付表维度
            if (durationType.TERMINATE.name().equals(hd.getDurationType())) {
                depositResidualAmount = hlsDurationDepositMapper.getDepositResidualAmountByProjectId(item.getProjectId());

            } else {
                depositResidualAmount = hlsDurationDepositMapper.getDepositResidualAmountByContractId(item.getContractId());
            }
            item.setDepositResidualAmount(depositResidualAmount);

            if (item.getSourceId() == null) {
                item.setHdId(id);
                item.setExecuteStatus("NEW");
                item = hlsDurationDepositService.insertSelective(iRequest, item);
                HlsDurationDeposit durationDeposit = item;
                //保证金退还 && 保证金利息 && 债权
                List<HlsDurationLn> hlsDurationLnList = hd.getHlsDurationLnList();


                hlsDurationLnList.stream().filter(info -> depositType.DEPOSIT.name().equals(info.getSourceType()) || depositType.INTEREST.name().equals(info.getSourceType()) || depositType.CREDIT.name().equals(info.getSourceType()))
                        .forEach(ln -> {
                            ln.setHdId(id);
                            ln.setSourceId(durationDeposit.getSourceId());
                            if (ln.getLnId() == null) {
                                hlsDurationLnService.insertSelective(iRequest, ln);
                            } else {
                                hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);
                            }
                        });
            } else {
                item.setExecuteStatus("NEW");
                Long sourceId = item.getSourceId();
                hlsDurationDepositService.updateByPrimaryKeySelective(iRequest, item);
                //保证金退还 && 保证金利息 && 债权
                List<HlsDurationLn> hlsDurationLnList = hd.getHlsDurationLnList();
                hlsDurationLnList.stream().filter(info -> depositType.DEPOSIT.name().equals(info.getSourceType()) || depositType.INTEREST.name().equals(info.getSourceType()) || depositType.CREDIT.name().equals(info.getSourceType()))
                        .forEach(ln -> {
                            ln.setHdId(id);
                            ln.setSourceId(sourceId);
                            if (ln.getLnId() == null) {
                                hlsDurationLnService.insertSelective(iRequest, ln);
                            } else {
                                hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);
                            }

                        });
            }
        });

        //提前结清 && 正常结清 && 解押 && 回购
        List<HlsDurationLn> hlsDurationLnList = hd.getHlsDurationLnList();

        hlsDurationLnList.stream().filter(info -> !(depositType.DEPOSIT.name().equals(info.getSourceType()) || depositType.INTEREST.name().equals(info.getSourceType()) || depositType.CREDIT.name().equals(info.getSourceType()))).forEach(item -> {
            if (item.getLnId() == null && !(item.getContractId() == null && item.getProjectId() == null)) {
                item.setHdId(id);
                hlsDurationLnService.insertSelective(iRequest, item);
            } else {
                hlsDurationLnService.updateByPrimaryKeySelective(iRequest, item);
            }
        });
        if (!hd.getDurationType().equalsIgnoreCase("DEPOSIT")) {
            //一个虚拟合同 同时只能存在一个存续期申请，因此需要在保存时暂挂虚拟合同
            HlsCusPrjProject project = new HlsCusPrjProject();
            project.setProjectId(hd.getProjectId());
            project = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, project);


            project.setContractStatus("PENDING");
            hlsCusPrjProjectService.updateByPrimaryKey(iRequest, project);
            //向下存在支付表则暂挂支付表
            if (ET.equalsIgnoreCase(hdDurationType) || FINANCIAL_TERMS.equalsIgnoreCase(hd.getExecuteType())) {
                //提前结清需要在申请时暂挂支付表（提前结清直接在申请时走完。不做执行）
                for (HlsDurationLn ln1 : lnList) {
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setContractId(ln1.getContractId());
                    conContract.setContractStatus("PENDING");
                    cusConContractService.updateByPrimaryKeySelective(iRequest, conContract);
                }

            }
        }
//        if(CON_TERMINATION.equalsIgnoreCase(hdDurationType) ){
//            HlsCusConContract conContract = new HlsCusConContract();
//            conContract.setProjectId(hd.getProjectId());
//            conContract.setContractStatus("PENDING");
//            cusConContractService.updateByProjectId(conContract);
//        }

        //附件
        List<HlsDurationAttachment> hlsDurationAttachmentList = hd.getHlsDurationAttachmentList();

        hlsDurationAttachmentList.forEach(item -> {
            if (item.getDurationAttachmentId() == null) {
                item.setHdId(id);
                hlsDurationAttachmentService.insertSelective(iRequest, item);
            } else {
                hlsDurationAttachmentService.updateByPrimaryKeySelective(iRequest, item);
            }
        });


        List<HlsDurationHd> hdList = new ArrayList<>();
        hdList.add(hd);
        return hdList;
    }

    /*核销校验*/
    @Override
    public void conCashCheckList(HlsDurationLn ln, String durationTypes) throws ResMessageException {
        if (durationType.ET.name().equals(durationTypes)) {
            List<HlsDurationLn> hlsDurationLns = hlsDurationLnMapper.selectWriteOff(ln);
            if (hlsDurationLns.size() > 0) {
                throw new ResMessageException("结清日后存在核销记录，请检查数据！");
            }
        }
    }

    /**
     * @Title: submitWfl
     * @Discription: 存续期提交工作流
     * @Param: [iRequest, hdList]
     * @Return: void
     */
    @Override
    public List<HlsDurationHd> submitWfl(IRequest iRequest, List<HlsDurationHd> hdList) throws ResMessageException {
        HlsDurationHd hd = hlsDurationHdMapper.selectByPrimaryKey(hdList.get(0).getHdId());
        HlsDurationLn ln = new HlsDurationLn();
        ln.setHdId(hd.getHdId());
        List<HlsDurationLn> hlsDurationLns = hlsDurationLnMapper.select(ln);

        //锁表
        databaseLockProvider.lock(hd);
        //获取申请人
       /* HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (Objects.isNull(employee)) {
            throw new ResMessageException("获取提交人失败");
        }

        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);*/
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();

        String hdDurationType = hd.getDurationType();

        //由于是提交暂挂，存在变更提交前发生核销情况 因此需要校验 contractCheckList
        if (durationType.ET.name().equals(hdDurationType)) {
            for (HlsDurationLn hlsDurationLn : hlsDurationLns) {
                conCashCheckList(hlsDurationLn, hdDurationType);
            }
        }
        /**
         PROJECT           项目变更审批事项工作联系单
         TERMINATE         正常结清
         ET                提前结清
         DEPOSIT           保证金释放、退还
         RELEASE           解押
         REPURCHASE        回购
         PREPAYMENT        提前还款
         SPECIAL           特殊事项
         */

        String workFlow = "";

        if (durationType.BUSINESS_CHANGE.name().equals(hdDurationType)) {
            workFlow = "DURATION_WFL_SPECIAL";
            //业务变更需要上会  存在历史单据 没有上会信息 这里需要处理 批复条件复制
            PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
            ProjectApprovalCondition condition = new ProjectApprovalCondition();
            prjProjectApproval.setProjectId(hd.getProjectId().toString());
            prjProjectApproval.setDataClass("VIRTUAL_CON");
            List<PrjProjectApproval> prjProjectApprovals = prjProjectApprovalMapper.conQueryAll(prjProjectApproval);
            if (prjProjectApprovals.size() == 0) {
                HlsCusPrjProject project = new HlsCusPrjProject();
                project.setProjectId(hd.getProjectId());
                HlsCusPrjProject projectList = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, project);
                if (projectList != null) {
                    prjProjectApproval.setProjectId(projectList.getRefProjectId().toString());
                    prjProjectApproval.setDataClass("PRJ_PROJECT_INVEST");
                    List<PrjProjectApproval> approvals = prjProjectApprovalMapper.queryAll(prjProjectApproval);
                    if (approvals.size() > 0) {
                        //复制项目批复到变更批复
                        BeanRefUtils.beanToBean(approvals.get(0), prjProjectApproval, hlsBeanRefUtilService);
                        prjProjectApproval.setProjectId(hd.getProjectId().toString());
                        prjProjectApproval.setDataClass(null);
                        projectApprovalService.insertSelective(iRequest, prjProjectApproval);
                        //批复条件复制
                        condition.setProjectId(hd.getProjectId());
                        condition.setApprovalId(approvals.get(0).getApprovalId());
                        List<ProjectApprovalCondition> conditionList = conditionMapper.queryAll(condition);
                        if (conditionList.size() > 0) {
                            for (ProjectApprovalCondition condition1 : conditionList) {
                                BeanRefUtils.beanToBean(condition1, condition, hlsBeanRefUtilService);
                                condition.setProjectId(hd.getProjectId());
                                conditionMapper.insertSelective(condition);
                            }
                        }
                    } else {
                        prjProjectApproval.setProjectId(hd.getProjectId().toString());
                        prjProjectApproval.setDataClass("VIRTUAL_CON");
                        prjProjectApproval = projectApprovalService.insertSelective(iRequest, prjProjectApproval);
                    }
                }
            } else {
                prjProjectApproval = prjProjectApprovals.get(0);
            }
            params.put("approvalId", prjProjectApproval.getApprovalId());
        } else if (durationType.ET.name().equals(hdDurationType)) {
            workFlow = "DURATION_WFL_ET";
        } else if (durationType.CON_TERMINATION.name().equals(hdDurationType)) {
            workFlow = "DURATION_WFL_TERMINATE";
        } else if (durationType.DEPOSIT.name().equals(hdDurationType)) {
            workFlow = "DURATION_WFL_B0ND";
        }

        params.put("workFlowType", "DURATION_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, workFlow);
        params.put(IActivitiCommonService.DEMO_NAME, "DURATION_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, hd.getHdId());

        String documentName = "";
        /*if(hd.getContractId() != null){
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(hd.getContractId());
            hlsCusConContract = conContractService.selectByPrimaryKey(iRequest,hlsCusConContract);
            if(hlsCusConContract.getContractName() != null){
                documentName = hlsCusConContract.getContractName();
            }
        }*/

        if (hd.getProjectId() != null) {
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(hd.getProjectId());
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            if (hlsCusPrjProject.getContractName() != null) {
                documentName = hlsCusPrjProject.getContractName();
            }
            if (documentName.equals("") && hlsCusPrjProject.getProjectName() != null) {
                documentName = hlsCusPrjProject.getProjectName();
            }
            //带入项目经理AB角
            params.put("employeeManagerAssignsId", hlsCusPrjProject.getHostProjectManager());
            params.put("projectAssistant", hlsCusPrjProject.getAssistProjectManager());
        }

        if (documentName.equals("")) {
            documentName = hd.getDurationNumber();
        }

        params.put("documentCategory", "DURATION");
        params.put("documentName", documentName);
        params.put("documentNumber", hd.getDurationNumber());
        params.put("hdId", hd.getHdId());
        params.put("unitId", hd.getApplyUnitId());
        params.put("contractId", hd.getContractId());
        params.put("projectId", hd.getProjectId());
        params.put("durationType", hdDurationType);
        params.put("startUserName", iRequest.getUserName());
        params.put("companyId", iRequest.getCompanyId());

        Double refundAmount = 0.0;
        Double deductionAmount = 0.0;
        for (HlsDurationLn durationLn : hlsDurationLns) {
            refundAmount = add(OracleUtils.nvl(durationLn.getRefundAmount(), 0.0), refundAmount, 2);
            deductionAmount = add(OracleUtils.nvl(durationLn.getDeductionAmount(), 0.0), deductionAmount, 2);
        }
        params.put("refundAmount", refundAmount);
        params.put("deductionAmount", deductionAmount);


        activitiStartService.start(iRequest, hdList, params);
        //修改变更状态
        HlsDurationHd durationHd = new HlsDurationHd();
        durationHd.setHdId(hd.getHdId());
        durationHd.setDurationStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, durationHd);


        return hdList;
    }

    @Override
    public HlsDurationHd submitReplyWfl(IRequest iRequest, HlsDurationHd hdList) throws ResMessageException {
        List<HlsDurationHd> durationHdList = hlsDurationHdMapper.prjProjectApprovalConQuery(hdList);
        HlsDurationHd hd = durationHdList.get(0);
        if (!("NEW".equalsIgnoreCase(hd.getDurationStatus()) || "REJECTED".equalsIgnoreCase(hd.getDurationStatus()))) {
            throw new ResMessageException("该单据不可提交，请检查单据状态！");
        }
        //暂挂项目
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectStatus("PENDING");
        prjProject.setProjectId(hd.getProjectId());
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
        //锁表
        databaseLockProvider.lock(hd);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        String hdDurationType = hd.getDurationType();
        //状态判断
        //项目暂挂

        params.put("workFlowType", "FCT_PROJECT_CHANGE_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "FCT_PROJECT_CHANGE_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "FCT_PROJECTREVIEW_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, hd.getHdId());

        params.put("documentCategory", "FCT_PROJECT_CHANGE_WFL");
        params.put("documentName", hd.getProjectName());
        params.put("projectName", hd.getProjectName());
        params.put("documentNumber", hd.getDurationNumber());
        params.put("durationHd", JSON.toJSONString(hd));
        params.put("hdId", hd.getHdId());
        params.put("financeAmount", hd.getFinanceAmount());
        params.put("unitId", hd.getApplyUnitId());
        params.put("projectId", hd.getProjectId());
        params.put("approvalId", hd.getApprovalId());
        params.put("durationType", hdDurationType);
        params.put("startUserName", iRequest.getUserName());
        params.put("companyId", iRequest.getCompanyId());
        params.put("projectAssistant", hd.getAssistProjectManager());


        activitiStartService.start(iRequest, durationHdList, params);
        //修改变更状态
        HlsDurationHd durationHd = new HlsDurationHd();
        durationHd.setHdId(hd.getHdId());
        durationHd.setDurationStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, durationHd);


        return hd;
    }


    /**
     * @Title: etSave
     * @Discription: 提前结清保存
     * @Param: [iRequest, hd]
     * @Return: java.util.List<com.hand.hls.hls.dto.HlsDurationLn>
     */
    @Override
    public List<HlsDurationLn> etSave(IRequest iRequest, HlsDurationHd hd) throws ResMessageException {
        List<HlsDurationLn> lnList = hd.getHlsDurationLnList();
        List<HlsDurationLn> returnln = new ArrayList<>();
        for (HlsDurationLn ln : lnList) {
            hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);
            ln = hlsDurationLnMapper.selectByPrimaryKey(ln.getLnId());
            ln = hlsDurationCalcService.createQuotation(iRequest, ln.getLnId());
            returnln.add(ln);

        }
        return returnln;
    }

    @Override
    public List<HlsDurationLn> projectSave(IRequest iRequest, HttpSession session, HlsDurationHd hd) throws ParseException {
        //点击下一步，创建出存续期数据，初始化
        Map<String, String> params = new HashMap<>();
        String durationNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
        hd.setDurationNumber(durationNumber);
        hd.setDurationType(hd.getDurationType());
        hd.setDurationStatus("NEW");
        hd.setExecuteStatus("NEW");
        hd.setExecuteFlag("N");
        hd.setApplyDate(df.parse(df.format(new Date())));
        hd.setApplyPerson(iRequest.getUserId());
        hd.setApplyUnitId(Long.parseLong(String.valueOf(session.getAttribute("unitId"))));
        hd.setProjectId(hd.getRefProjectId());
        HlsDurationHd hlsDurationHd = self().insertSelective(iRequest, hd);


        //根据选择的项目 将虚拟合同 支付表 插入到行表
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setRefProjectId(hd.getRefProjectId());
        prjProject.setDataClass("VIRTUAL_CON");
        prjProject.setDataType("NORMAL");
        List<HlsCusPrjProject> projectList = prjProjectMapper.select(prjProject);

        List<HlsDurationLn> lnList = new ArrayList<>();

        for (HlsCusPrjProject project : projectList) {

            HlsDurationLn ln = new HlsDurationLn();
            ln.setHdId(hlsDurationHd.getHdId());
            ln.setSourceType("PROJECT");
            ln.setSourceId(project.getProjectId());
            hlsDurationLnService.insertSelective(iRequest, ln);

            HlsCusConContract conContract = new HlsCusConContract();
            conContract.setProjectId(project.getProjectId());
            conContract.setDataClass("NORMAL");
            List<HlsCusConContract> contractList = conContractMapper.select(conContract);
            lnList.add(ln);

            if ("PROJECT".equals(hd.getDurationType())) {
                for (HlsCusConContract contract : contractList) {
                    ln = new HlsDurationLn();
                    ln.setHdId(hlsDurationHd.getHdId());
                    ln.setSourceType("CONTRACT");
                    ln.setSourceId(project.getProjectId());
                    ln.setContractId(contract.getContractId());
                    hlsDurationLnService.insertSelective(iRequest, ln);
                    lnList.add(ln);
                }
            }
        }
        return lnList;
    }


    @Override
    public void contractCheck(Long contractId, HlsDurationHd hd) throws ResMessageException {
        String executeReqFlag = hd.getExecuteReqFlag();
        if (durationType.BUSINESS_CHANGE.name().equals(hd.getDurationType()) || durationType.CON_TERMINATION.name().equals(hd.getDurationType())) {
            Example hdExample = new Example(HlsDurationHd.class);
            if ("Y".equalsIgnoreCase(executeReqFlag)) {
                hdExample.createCriteria()
                        .andEqualTo("projectId", hd.getProjectId())
                        .andIn("executeType", Arrays.asList("FINANCIAL_TERMS"))
                        .andIn("executeStatus", Arrays.asList("NEW", "REJECTED", "APPROVING"))
                        .andNotEqualTo("hdId", hd.getHdId());
                List<HlsDurationHd> durationHdList = hlsDurationHdMapper.selectByExample(hdExample);
                if (durationHdList.size() > 0) {
                    throw new ResMessageException("该合同已经做过相应的存续期创建，且流程未结束，不能再创建存续期！");
                }
            } else {
                hdExample.createCriteria()
                        .andEqualTo("projectId", hd.getProjectId())
                        .andIn("durationType", Arrays.asList("BUSINESS_CHANGE", "CON_TERMINATION"))
                        .andIn("durationStatus", Arrays.asList("NEW", "APPROVED", "REJECTED", "APPROVING"))
                        .andNotIn("executeStatus", Arrays.asList("CANCEL", "APPROVED"))
                        .andNotEqualTo("hdId", hd.getHdId());
                List<HlsDurationHd> durationHdList = hlsDurationHdMapper.selectByExample(hdExample);
                if (durationHdList.size() > 0) {
                    throw new ResMessageException("该合同已经做过相应的存续期创建，且流程未结束，不能再创建存续期！");
                }
            }
        } else {
            if (hd.getHdId() != null) {
                //保证金退还抵扣时，同一个支付表只能有一个是新建或提交中的状态
                Example hdExample = new Example(HlsDurationHd.class);
                hdExample.createCriteria()
                        .andEqualTo("contractId", contractId)
                        .andEqualTo("durationType", depositType.DEPOSIT.name())
                        .andIn("durationStatus", Arrays.asList("NEW", "APPROVING"))
                        .andNotEqualTo("hdId", hd.getHdId());
                List<HlsDurationHd> durationHdList = hlsDurationHdMapper.selectByExample(hdExample);
                if (durationHdList.size() > 0) {
                    throw new ResMessageException("该支付表已经做过保证金退还和抵扣，不能再创建存续期！");
                }
            } else {
                //保证金退还抵扣时，同一个支付表只能有一个是新建或提交中的状态
                Example hdExample = new Example(HlsDurationHd.class);
                hdExample.createCriteria()
                        .andEqualTo("contractId", contractId)
                        .andEqualTo("durationType", durationType.BUSINESS_CHANGE.name())
                        .andIn("durationStatus", Arrays.asList("NEW", "REJECTED", "APPROVED", "APPROVING"));
                List<HlsDurationHd> durationHdList = hlsDurationHdMapper.selectByExample(hdExample);
                if (durationHdList.size() > 0) {
                    throw new ResMessageException("该合同存在流程中的存续期单据，不能再创建存续期！");
                }
            }
        }
    }

    @Override
    public void contractCheckList(List<Long> contractIds, HlsDurationHd hd, String durationType) throws ResMessageException {
        //保证金退还抵扣时，同一个支付表只能有一个是新建或提交中的状态
        Example hdExample = new Example(HlsDurationHd.class);
        for (Long contractId : contractIds) {

            HlsDurationHd hlsDurationHd = new HlsDurationHd();
            hlsDurationHd.setContractId(contractId);
            hlsDurationHd.setDurationType(depositType.DEPOSIT.name());
            if (hd.getHdId() != null) {
                hlsDurationHd.setHdId(hd.getHdId());
            }

            List<HlsDurationHd> durationHdList = hlsDurationHdMapper.contractCheckList(hlsDurationHd);

            if (durationHdList.size() > 0) {
                throw new ResMessageException("该支付表已经做过保证金退还和抵扣，不能再创建存续期！");
            }
        }


        //正常结清 和 提前结清 不能同时创建
        if (hd.getHdId() != null) {
            HlsDurationHd hlsDurationHd = hlsDurationHdMapper.selectByPrimaryKey(hd.getHdId());
            HlsDurationHd durationHd = new HlsDurationHd();
            durationHd.setProjectId(hlsDurationHd.getProjectId());
            List<HlsDurationHd> durationHdList = hlsDurationHdMapper.select(durationHd);

            Long count = durationHdList.stream().
                    filter(item ->
                            (item.getDurationType().equals(TERMINATE)) //|| item.getDurationType().equals(ET)
                                    && !item.getHdId().equals(hd.getHdId())).
                    filter(item -> !item.getDurationStatus().equals("CANCEL"))

                    .count();
            if (count > 0) {
                throw new ResMessageException("该支付表已经做过结清，不能再创建存续期！");
            }

        } else {
            HlsDurationHd durationHd = new HlsDurationHd();
            durationHd.setProjectId(hd.getProjectId());
            List<HlsDurationHd> durationHdList = hlsDurationHdMapper.select(durationHd);
            Long count = durationHdList.stream().
                    filter(item ->
                            (item.getDurationType().equals(TERMINATE))).
//                            (item.getDurationType().equals(TERMINATE) || item.getDurationType().equals(ET))).
        filter(item -> !item.getDurationStatus().equals("CANCEL")).count();
            if (count > 0) {
                throw new ResMessageException("该支付表已经做过结清，不能再创建存续期！");
            }
            //提前结清保存校验，如果合同下所有支付表都已经被创建且没有取消，则不可以继续创建
            if (ET.equals(hd.getDurationType())) {
                HlsDurationLn ln = new HlsDurationLn();
                ln.setProjectId(hd.getProjectId());
                List<HlsDurationLn> lnList = hlsDurationLnMapper.CountEt(ln);
                if (lnList.size() > 0) {
                    for (HlsDurationLn durationLn : lnList) {
                        //申请单据未取消，且未执行则不可创建
                        if (durationLn.getExecuteStatus() == null) {
                            if (!"CANCEL".equalsIgnoreCase(durationLn.getDurationStatus())) {
                                throw new ResMessageException("该合同正在存续期申请中且未结束，不能再创建存续期！");
                            }
                        }
                        //申请单据未取消，且未执行则不可创建
                        if (durationLn.getExecuteStatus() != null) {
                            if (!"APPROVED".equalsIgnoreCase(durationLn.getExecuteStatus()) && "APPROVED".equalsIgnoreCase(durationLn.getDurationStatus())) {
                                throw new ResMessageException("该合同正在存续期申请中且未结束，不能再创建存续期！");
                            }
                        }
                    }
                }
            }
        }

    }


    /**
     * @Title: executeCreate
     * @Discription: 执行创建（合同变更创建）
     * @Param: [iRequest, hd]
     * @Return: com.hand.hls.hls.dto.HlsDurationHd
     */
    @Override
    public HlsDurationHd executeCreate(IRequest iRequest, HttpSession session, HlsDurationHd hd) throws ParseException {
        //将存续期方案复制一份
        Map<String, String> params = new HashMap<>();
        Long hdId = hd.getHdId();
        Long projectId = hd.getPrjConId();
        HlsDurationHd hds = new HlsDurationHd();
        hds.setHdId(hdId);
        List<HlsDurationHd> durationHdDto = hlsDurationHdMapper.createSelectHdAll(hds);
        HlsDurationHd hlsDurationHd = durationHdDto.get(0);
        hlsDurationHd.setChangeReason(hd.getChangeReason());
        hlsDurationHd.setChangeDescription(hd.getChangeDescription());
        String executeNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, EXECUTE_DOCUMENT_CATEGORY, EXECUTE_DOCUMENT_TYPE, EXECUTE_BUSINESS_TYPE, params);
        hlsDurationHd.setExecuteNumber(executeNumber);
        hlsDurationHd.setExecuteStatus("NEW");
        hlsDurationHd.setExecuteFlag("Y");
        hlsDurationHd.setProjectId(projectId);
        hlsDurationHd.setContractId(hd.getContractId());
        hlsDurationHd.setSourceHdId(hdId);
        hlsDurationHd.setApplyDate(df.parse(df.format(new Date())));
        hlsDurationHd.setApplyPerson(iRequest.getUserId());
        hlsDurationHd.setApplyUnitId(Long.parseLong(String.valueOf(session.getAttribute("unitId"))));


        if (durationType.ET.name().equals(durationHdDto.get(0).getDurationType())) {
            hlsDurationHd.setEtChangeFlag("Y");
            hlsDurationHd.setAgreementChangeFlag("N");
            hlsDurationHd.setExecuteType(executeType.ET.name());
        } else if (durationType.PREPAYMENT.name().equals(durationHdDto.get(0).getDurationType())) {
            hlsDurationHd.setPrepaymentChangeFlag("Y");
            hlsDurationHd.setAgreementChangeFlag("N");
            hlsDurationHd.setExecuteType(executeType.PREPAYMENT.name());
        } else if (durationType.RELEASE.name().equals(durationHdDto.get(0).getDurationType())) {
            hlsDurationHd.setExecuteType(executeType.RELEASE.name());
        } else if (durationType.REPURCHASE.name().equals(durationHdDto.get(0).getDurationType())) {
            hlsDurationHd.setExecuteType(executeType.REPURCHASE.name());
        } else {
            hlsDurationHd.setExecuteType(executeType.FINANCIAL_TERMS.name());
        }

        hlsDurationHd.setHdId(null);
        HlsDurationHd hlsDurationHdCheck = new HlsDurationHd();
        hlsDurationHdCheck.setHdId(hdId);
        List<HlsDurationHd> hlsDurationHdList = hlsDurationHdMapper.selectDurationHdCheck(hlsDurationHdCheck);
        if (hlsDurationHdList.size() > 0) {
            throw new RuntimeException("该存续期方案已存在未作废的变更申请单！");
        }
        hlsDurationHd = self().insertSelective(iRequest, hlsDurationHd);


        HlsDurationLn ln = new HlsDurationLn();
        ln.setHdId(hdId);
        List<HlsDurationLn> lnList = hlsDurationLnMapper.select(ln);

        for (HlsDurationLn durationLn : lnList) {
            //保证金 相关数据 不在合同变更执行 ，在保证金管理 执行 不需要复制 ，项目 和 特殊事项的行数据也不需要复制 走金融条款变更
            if (!durationType.PROJECT.name().equals(durationLn.getSourceType()) ||
                    !durationType.SPECIAL.name().equals(durationLn.getSourceType()) ||
                    !depositType.DEPOSIT.name().equals(durationLn.getSourceType()) ||
                    !depositType.INTEREST.name().equals(durationLn.getSourceType()) ||
                    !depositType.CREDIT.name().equals(durationLn.getSourceType())) {
                Long lnId = durationLn.getLnId();
                durationLn.setHdId(hlsDurationHd.getHdId());
                durationLn.setLnId(null);
                hlsDurationLnService.insertSelective(iRequest, durationLn);

                if (sourceType.ET.name().equals(durationLn.getSourceType()) || sourceType.PREPAYMENT.name().equals(durationLn.getSourceType())) {
                    HlsDurationCompare compare = new HlsDurationCompare();
                    compare.setHdId(hdId);
                    compare.setLnId(lnId);
                    List<HlsDurationCompare> compareList = hlsDurationCompareMapper.select(compare);
                    for (HlsDurationCompare durationCompare : compareList) {
                        durationCompare.setHdId(hlsDurationHd.getHdId());
                        durationCompare.setLnId(durationLn.getLnId());
                        durationCompare.setCompareId(null);
                        hlsDurationCompareService.insertSelective(iRequest, durationCompare);
                    }
                }
            }
        }

        if (durationType.ET.name().equals(durationHdDto.get(0).getDurationType()) || durationType.PREPAYMENT.name().equals(durationHdDto.get(0).getDurationType())
                || durationType.RELEASE.name().equals(durationHdDto.get(0).getDurationType()) || durationType.REPURCHASE.name().equals(durationHdDto.get(0).getDurationType())
                || durationType.PROJECT.name().equals(hd.getDurationType()) || durationType.SPECIAL.name().equals(hd.getDurationType()) || durationType.BUSINESS_CHANGE.name().equals(hd.getDurationType())) {
            //插入合同的租赁物 和 抵质押物
            HlsDurationLn hlsDurationLn = new HlsDurationLn();
            hlsDurationLn.setProjectId(projectId);

//            List<HlsDurationLn> pledgorMortgagors = hlsDurationLnMapper.hlsDurationLnPledgorMortgagorQuery(hlsDurationLn);
            List<HlsDurationLn> pledgorMortgagors = hlsDurationLnMapper.hlsDurationLnPledgorMortgagorQueryNew(hlsDurationLn);
//            List<HlsDurationLn> leaseItems = hlsDurationLnMapper.hlsDurationLnLeaseItemQuery(hlsDurationLn);
            List<HlsDurationLn> leaseItems = hlsDurationLnMapper.hlsDurationLnLeaseItemQueryNew(hlsDurationLn);

            for (HlsDurationLn data : pledgorMortgagors) {
                HlsDurationItem item = new HlsDurationItem();
                item.setProjectId(projectId);
                item.setSourceType(sourceType.PLEDGOR_MORTGAGORS.name());
//                item.setPledgeProportion(data.getPledgeProportion());
                item.setHdId(hlsDurationHd.getHdId());
                item.setLeaseItemId(data.getLeaseItemId());
                item.setExecuteStatus("NEW");
                hlsDurationItemService.insertSelective(iRequest, item);
            }
            for (HlsDurationLn data : leaseItems) {
                HlsDurationItem item = new HlsDurationItem();
                item.setProjectId(projectId);
                item.setSourceType(sourceType.LEASE_ITEMS.name());
//                item.setPledgeProportion(data.getPledgeProportion());
                item.setHdId(hlsDurationHd.getHdId());
                item.setLeaseItemId(data.getLeaseItemId());
                item.setExecuteStatus("NEW");
                hlsDurationItemService.insertSelective(iRequest, item);
            }

        }
        //项目变更审批事项工作联系单 && 特殊事项 需要复制 客户信息
        if (durationType.PROJECT.name().equals(hd.getDurationType()) || durationType.SPECIAL.name().equals(hd.getDurationType()) || durationType.BUSINESS_CHANGE.name().equals(hd.getDurationType())) {

            HlsCusPrjProjectBp projectBp = new HlsCusPrjProjectBp();
            if (projectId != null) {
                projectBp.setProjectId(projectId);
                List<HlsCusPrjProjectBp> prjProjectBpList = prjProjectBpMapper.select(projectBp);
                List<HlsCusPrjProjectBp> bpList = prjProjectBpList.stream().filter(item -> item.getBpId() != null).collect(Collectors.toList());
                for (HlsCusPrjProjectBp bp : bpList) {
                    HlsDurationLn durationLn = new HlsDurationLn();
                    durationLn.setSourceType(sourceType.BP_INFO.name());
                    durationLn.setSourceId(bp.getPrjBpId());
                    durationLn.setHdId(hlsDurationHd.getHdId());
                    durationLn.setBpId(bp.getBpId());
                    durationLn.setBpType(bp.getBpType());
                    durationLn.setBankAccountId(bp.getBankAccountId());
                    hlsDurationLnService.insertSelective(iRequest, durationLn);
                }
            }
        }

        return hlsDurationHd;
    }


    /**
     * @Title: executeCancel
     * @Discription: 取消执行方案
     * @Param: [hd]
     * @Return: void
     */
    @Override
    public void executeCancel(HlsDurationHd hd) {
        if (hd.getHdId() != null) {
            self().deleteByPrimaryKey(hd);

            HlsDurationLn ln = new HlsDurationLn();
            HlsDurationCompare compare = new HlsDurationCompare();
            HlsDurationDeposit deposit = new HlsDurationDeposit();
            HlsDurationItem item = new HlsDurationItem();
            ln.setHdId(hd.getHdId());
            compare.setHdId(hd.getHdId());
            deposit.setHdId(hd.getHdId());
            item.setHdId(hd.getHdId());
            hlsDurationLnService.batchDelete(hlsDurationLnMapper.select(ln));
            hlsDurationCompareService.batchDelete(hlsDurationCompareMapper.select(compare));
            hlsDurationDepositService.batchDelete(hlsDurationDepositMapper.select(deposit));
            hlsDurationItemService.batchDelete(hlsDurationItemMapper.select(item));
        }
    }

    /**
     * @Title: executeUpdate
     * @Discription: 更新变更信息
     * @Param: [iRequest, hd]
     * @Return: void
     */
    @Override
    @Deprecated
    public void executeUpdate(IRequest iRequest, HlsDurationHd hd) {

        if (hd.getHdId() != null) {
            //项目变更审批事项工作联系单 && 特殊事项 需要复制 客户信息
            if (durationType.PROJECT.name().equals(hd.getDurationType()) || durationType.SPECIAL.name().equals(hd.getDurationType())) {

                HlsCusPrjProjectBp projectBp = new HlsCusPrjProjectBp();
                if (hd.getProjectId() != null) {
                    projectBp.setProjectId(hd.getProjectId());
                    List<HlsCusPrjProjectBp> prjProjectBpList = prjProjectBpMapper.select(projectBp);
                    List<HlsCusPrjProjectBp> bpList = prjProjectBpList.stream().filter(item -> item.getBpId() != null).collect(Collectors.toList());

                    for (HlsCusPrjProjectBp bp : bpList) {
                        HlsDurationLn durationLn = new HlsDurationLn();
                        durationLn.setSourceType(sourceType.BP_INFO.name());
                        durationLn.setSourceId(bp.getPrjBpId());
                        durationLn.setHdId(hd.getHdId());
                        durationLn.setBpId(bp.getBpId());
                        durationLn.setBpType(bp.getBpType());
                        durationLn.setBankAccountId(bp.getBankAccountId());
                        hlsDurationLnService.insertSelective(iRequest, durationLn);
                    }
                }
            }
            self().updateByPrimaryKeySelective(iRequest, hd);
        }

    }


    /**
     * @Title: submitWfl
     * @Discription: 存续期提交工作流
     * @Param: [iRequest, hdList]
     * @Return: void
     */
    @Override
    public List<HlsDurationHd> executeSubmit(IRequest iRequest, List<HlsDurationHd> hdList) throws ResMessageException {
        HlsDurationHd hd = hlsDurationHdMapper.selectByPrimaryKey(hdList.get(0).getHdId());
        HlsDurationLn ln = new HlsDurationLn();
        ln.setHdId(hd.getHdId());
        List<HlsDurationLn> hlsDurationLns = hlsDurationLnMapper.select(ln);
        //锁表
        databaseLockProvider.lock(hd);
        //获取申请人
       /* HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (Objects.isNull(employee)) {
            throw new ResMessageException("获取提交人失败");
        }

        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);*/
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();

        String documentName = hd.getExecuteNumber();
        if (hd.getProjectId() != null) {
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(hd.getProjectId());
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            params.put("projectAssistant", hlsCusPrjProject.getAssistProjectManager());
            if (hlsCusPrjProject.getProjectName() != null) {
                documentName = documentName + hlsCusPrjProject.getProjectName();
            }
        }

        params.put("workFlowType", "CONTRACT_CHANGE_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "CONTRACT_CHANGE_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "CONTRACT_CHANGE_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, hd.getHdId());
        params.put("documentCategory", "CONTRACT_CHANGE");
        params.put("documentName", documentName);
        params.put("documentNumber", hd.getExecuteNumber());
        params.put("hdId", hd.getHdId());
        params.put("unitId", hd.getApplyUnitId());
        params.put("contractId", hd.getContractId());
        params.put("projectId", hd.getProjectId());
        params.put("durationType", hd.getDurationType());
        params.put("executeType", hd.getExecuteType());
        params.put("startUserName", iRequest.getUserName());
        params.put("companyId", iRequest.getCompanyId());
        params.put("endTaskFlag", "N");

        Double refundAmount = 0.0;
        Double deductionAmount = 0.0;
        for (HlsDurationLn durationLn : hlsDurationLns) {
            refundAmount = add(OracleUtils.nvl(durationLn.getRefundAmount(), 0.0), refundAmount, 2);
            deductionAmount = add(OracleUtils.nvl(durationLn.getDeductionAmount(), 0.0), deductionAmount, 2);
        }
        params.put("refundAmount", refundAmount);
        params.put("deductionAmount", deductionAmount);


        activitiStartService.start(iRequest, hdList, params);
        //修改变更状态
        hd.setExecuteStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, hd);
        return hdList;
    }

    /**
     * @Title: executeCheck
     * @Discription: 执行审批通过前 校验
     * @Param: [hdId]
     * @Return: java.lang.String
     */
    @Override
    public void executeCheck(Long hdId) throws ResMessageException {
        //计算日之后的现金流 不能发生过核销
        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        hd = hlsDurationHdMapper.selectByPrimaryKey(hd);

        String executeType = hd.getExecuteType();
        HlsDurationLn ln = new HlsDurationLn();
        ln.setHdId(hdId);
        ln.setSourceType(executeType);
        List<HlsDurationLn> hlsDurationLnList = hlsDurationLnMapper.select(ln);

        for (HlsDurationLn hlsDurationLn : hlsDurationLnList) {

            Date calcDate = null;
            //提前结清 || 提前还款  ||金融条款
            if (ET.equals(executeType) || PREPAYMENT.equals(executeType) || FINANCIAL_TERMS.equals(executeType)) {
                if (ET.equals(hlsDurationLn.getSourceType())) {
                    calcDate = hlsDurationLn.getEtDate();
                } else if (PREPAYMENT.equals(hlsDurationLn.getSourceType())) {
                    calcDate = hlsDurationLn.getPrepaymentDate();
                } else if (FINANCIAL_TERMS.equals(hlsDurationLn.getSourceType())) {
                    calcDate = hlsDurationLn.getChangeDate();
                }
                if (calcDate != null) {
                    Example conContractCashflowExample = new Example(HlsCusConContractCashflow.class);
                    //计算日之后的现金流 不能发生过核销
                    conContractCashflowExample.createCriteria().andEqualTo("contractId", hlsDurationLn.getContractId()).
                            andGreaterThan("dueDate", calcDate).
                            andNotEqualTo("writeOffFlag", "NOT").
                            andEqualTo("cfItem", 1L).
                            andEqualTo("cfStatus", "RELEASE");
                    List<HlsCusConContractCashflow> cashflowList = conContractCashflowMapper.selectByExample(conContractCashflowExample);
                    if (cashflowList.size() > 0) {
                        throw new ResMessageException("执行日之后的现金流发生过核销，请先核销反冲！");
                    }
                }
            }
        }
    }

    @Override
    public List<HlsDurationHd> queryHlsDurationHdForPrjLov(IRequest iRequest, HlsDurationHd hd, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        List<HlsDurationHd> list = hlsDurationHdMapper.queryHlsDurationHdForPrjLov(hd);
        return list;
    }

    @Override
    public String prjContractLeaseItemCheck(IRequest iRequest, HttpSession session, HlsDurationHd hd) {
        String checkFlag = "Y";
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(hd.getProjectId());
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = new ArrayList<>();
        hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectLeaseItemService.select(iRequest, hlsCusPrjProjectLeaseItem, 1, 100000);
        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItemList) {
            HlsDurationLn hlsDurationLn = new HlsDurationLn();
            hlsDurationLn.setLeaseItemId(dt.getLeaseItemId());
            List<HlsDurationHd> hlsDurationHdList = hlsDurationHdMapper.hlsDurationHdExistCheckForLeaseItem(hlsDurationLn);
            if (hlsDurationHdList.size() > 0) {
                checkFlag = "N_LEASE";
                break;
            }
        }

        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setPrjBpId(hd.getProjectId());
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList = hlsCusPrjProjectBpService.select(iRequest, hlsCusPrjProjectBp, 1, 100000);
        for (HlsCusPrjProjectBp dt : hlsCusPrjProjectBpList) {
            if (dt.getLeaseItemId() != null) {
                HlsDurationLn hlsDurationLn = new HlsDurationLn();
                hlsDurationLn.setLeaseItemId(Long.parseLong(dt.getLeaseItemId()));
                List<HlsDurationHd> hlsDurationHdList = hlsDurationHdMapper.hlsDurationHdExistCheckForLeaseItem(hlsDurationLn);
                if (hlsDurationHdList.size() > 0) {
                    checkFlag = "N_MORTGAGE";
                    break;
                }
            }
        }

        return checkFlag;
    }


    /**
     * @Title: executeCashflow
     * @Discription: 现金流 回写 到业务表
     * @Param: [iRequest, hd]
     * @Return: List<HlsCusConContract>
     */
    @Override
    public List<HlsCusConContract> executeCashflow(IRequest iRequest, HlsDurationHd hd) {
        String executeType = hd.getExecuteType();
        HlsDurationLn ln = new HlsDurationLn();
        ln.setHdId(hd.getHdId());
        ln.setSourceType(executeType);
        List<HlsDurationLn> hlsDurationLnList = hlsDurationLnMapper.select(ln);

        List<HlsCusConContract> contractList = new ArrayList<>();

        for (HlsDurationLn hlsDurationLn : hlsDurationLnList) {
            Long quotationId = null;
            Date calcDate = null;

            HlsCusConContract conContract = new HlsCusConContract();
            //提前结清
            if (ET.equals(executeType)) {
                if (ET.equals(hlsDurationLn.getSourceType())) {
                    quotationId = hlsDurationLn.getEtQuotationId();
                    calcDate = hlsDurationLn.getEtDate();
                    conContract.setContractId(hlsDurationLn.getContractId());
                    conContract.setContractStatus(ET);
                    conContract.setChangeDate(calcDate);
                    conContractMapper.updateByPrimaryKeySelective(conContract);
                    contractList.add(conContract);
                }
            }
            //提前还款
            else if (PREPAYMENT.equals(executeType)) {
                if (PREPAYMENT.equals(hlsDurationLn.getSourceType())) {
                    quotationId = hlsDurationLn.getPrepaymentQuotationId();
                    calcDate = hlsDurationLn.getPrepaymentDate();
                    conContract.setContractId(hlsDurationLn.getContractId());
                    conContract.setChangeDate(calcDate);
                    contractList.add(conContract);
                }
            }
            //金融条款
            else if (FINANCIAL_TERMS.equals(executeType)) {
                if (FINANCIAL_TERMS.equals(hlsDurationLn.getSourceType())) {
                    quotationId = hlsDurationLn.getChangeQuotationId();
                    calcDate = hlsDurationLn.getChangeDate();
                    conContract.setContractId(hlsDurationLn.getContractId());
                    conContract.setChangeDate(calcDate);
                    contractList.add(conContract);
                }
            }


            if (quotationId != null) {

                HlsCusPrjQuotation prjQuotation = prjQuotationMapper.selectByPrimaryKey(quotationId);

                //冻结掉 计算日之后的 现金流
                Example conContractCashflowExample = new Example(HlsCusConContractCashflow.class);
                conContractCashflowExample.createCriteria().
                        andEqualTo("contractId", hlsDurationLn.getContractId()).
                        andGreaterThanOrEqualTo("dueDate", calcDate).
                        andIn("cfItem", Arrays.asList(1L, 10L, 3L, 4L, 8L, 52L));

                List<HlsCusConContractCashflow> cashflowList = conContractCashflowMapper.selectByExample(conContractCashflowExample);
                for (HlsCusConContractCashflow cashflow : cashflowList) {
                    cashflow.setCfStatus(BLOCK);
                    contractCashflowService.updateByPrimaryKeySelective(iRequest, cashflow);
                }
                //重新插入新的现金流
                Example prjQuotationCashflowExample = new Example(HlsCusPrjQuotationCashflow.class);
                prjQuotationCashflowExample.createCriteria().
                        andEqualTo("quotationId", quotationId).andGreaterThanOrEqualTo("dueDate", calcDate).
                        andIn("cfItem", Arrays.asList(1L, 10L, 3L, 4L, 8L, 52L));

                List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.selectByExample(prjQuotationCashflowExample);

                HlsCusConContract c = new HlsCusConContract();
                c.setContractId(hlsDurationLn.getContractId());
                c = conContractMapper.selectByPrimaryKey(c);

                c.setFinanceAmount(prjQuotation.getFinanceAmount());
                c.setLeaseItemAmount(prjQuotation.getLeaseItemAmount());
                c.setLeaseTimes(prjQuotation.getLeaseTimes());
                c.setQuotationId(quotationId);
                if (prjQuotationCashflowList.size() > 0) {
                    c.setFirstPayDate(prjQuotationCashflowList.get(0).getDueDate());
                    c.setLeaseEndDate(prjQuotationCashflowList.get(0).getCalcDate());
                }
                for (int i = 0; i < prjQuotationCashflowList.size(); i++) {
                    HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                    if (prjQuotationCashflowList.get(i).getDueDate() != null) {
                        //新加判断 报价现金流的dueDate可能为空
                        if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getFirstPayDate().getTime()) < 0) {
                            c.setFirstPayDate(prjQuotationCashflowList.get(i).getDueDate());
                        }
                        if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getLeaseEndDate().getTime()) > 0) {
                            c.setLeaseEndDate(prjQuotationCashflowList.get(i).getCalcDate());
                        }
                    }

                    BeanRefUtils.beanToBean(prjQuotationCashflowList.get(i), conContractCashflow, hlsBeanRefUtilService);
                    conContractCashflow.setTimes(prjQuotationCashflowList.get(i).getTimes().longValue());
                    conContractCashflow.setContractId(hlsDurationLn.getContractId());
                    conContractCashflow.setCfStatus("RELEASE");
                    conContractCashflow.setWriteOffFlag("NOT");
                    conContractCashflow.setBillingStatus("NOT");
                    conContractCashflow.setOverdueStatus("N");
                    conContractCashflow.setPenaltyProcessStatus("N");
                    conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
                    conContractCashflow.setGeneratedSourceDocId(quotationId);
                    conContractCashflow.setGeneratedSourceDocLineId(prjQuotationCashflowList.get(i).getQuotationCashflowId());
                    contractCashflowService.insertSelective(iRequest, conContractCashflow);
                }
                //更新费用类 期数 和 日期 保证金返还/风险金返还/留购价  其他费用都写入了prjQuotationCashflow 不用在单独写入
//                Example feeContractCashflowExample = new Example(HlsCusConContractCashflow.class);
//                feeContractCashflowExample.createCriteria().
//                        andEqualTo("contractId", hlsDurationLn.getContractId()).
//                        andGreaterThanOrEqualTo("dueDate", calcDate).
//                        andIn("cfItem", Arrays.asList(52L, 54L, 8L));
//
//                List<HlsCusConContractCashflow> feeCashflowList = conContractCashflowMapper.selectByExample(feeContractCashflowExample);
//                for (HlsCusConContractCashflow feeCasfhlow : feeCashflowList) {
//                    feeCasfhlow.setTimes(prjQuotation.getLeaseTimes());
//                    feeCasfhlow.setDueDate(prjQuotation.getLeaseEndDate());
//                    feeCasfhlow.setCalcDate(prjQuotation.getLeaseEndDate());
//                    contractCashflowService.updateByPrimaryKeySelective(iRequest,feeCasfhlow);
//                }

                conContractMapper.updateByPrimaryKeySelective(c);
            }

        }

        return contractList;
    }


    /**
     * @Title: executeBpInfo
     * @Discription: 更新 交易方信息
     * @Param: [requestCtx, hd]
     * @Return: void
     */
    @Override
    public void executeBpInfo(IRequest requestCtx, HlsDurationHd hd) {
        HlsDurationLn ln = new HlsDurationLn();
        ln.setHdId(hd.getHdId());
        ln.setSourceType(BP_INFO);
        List<HlsDurationLn> hlsDurationLnList = hlsDurationLnMapper.select(ln);


        //删除重插
        HlsCusPrjProjectBp projectBp = new HlsCusPrjProjectBp();
        projectBp.setProjectId(hd.getProjectId());
        List<HlsCusPrjProjectBp> prjProjectBpList = prjProjectBpMapper.select(projectBp);
        List<HlsCusPrjProjectBp> bpList = prjProjectBpList.stream().filter(item -> item.getBpId() != null).collect(Collectors.toList());
        prjProjectBpService.batchDelete(bpList);

        for (HlsDurationLn hlsDurationLn : hlsDurationLnList) {
            HlsCusPrjProjectBp bp = new HlsCusPrjProjectBp();
            bp.setProjectId(hd.getProjectId());
            bp.setBpCategroy(PRJ_PROJECT);
            bp.setBpType(hlsDurationLn.getBpType());
            bp.setBpId(hlsDurationLn.getBpId());
            bp.setBpCategroy("CON_CONTRACT");
            bp.setBankAccountId(hlsDurationLn.getBankAccountId());
            prjProjectBpService.insertSelective(requestCtx, bp);
        }
    }


    /**
     * @Title: executeLeaseItem
     * @Discription: 更新 租赁物 抵质押物 君成租赁物抵质押物 都存lease_item_id 不用管明细，所以变更后则将ID更新过去 如果租赁物变更时 存在删除，那么变更通过时 不处理删除的租赁物
     * @Param: [requestCtx, hd]
     * @Return: void
     */
    @Override
    public void executeLeaseItem(IRequest requestCtx, HlsDurationHd hd) {
        HlsDurationItem ln = new HlsDurationItem();
        ln.setHdId(hd.getHdId());
        List<HlsDurationItem> hlsDurationLnList = hlsDurationItemMapper.select(ln);
        List<HlsCusLeaseItem> hlsCusLeaseItemList = new ArrayList<>();
        for (HlsDurationItem hlsDurationLn : hlsDurationLnList) {
            if (PLEDGOR_MORTGAGORS.equals(hlsDurationLn.getSourceType()) || LEASE_ITEMS.equals(hlsDurationLn.getSourceType())) {
                HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
                hlsCusPrjProjectLeaseItem.setProjectId(hd.getProjectId());
                //查询原租赁物
                List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectLeaseItemService.select(requestCtx, hlsCusPrjProjectLeaseItem, 1, 100000);
                String existFlag;
                if (LEASE_ITEMS.equals(hlsDurationLn.getSourceType())) {
                    existFlag = "N";
                    if (hlsCusPrjProjectLeaseItemList.size() > 0) {
                        for (HlsCusPrjProjectLeaseItem dt : hlsCusPrjProjectLeaseItemList) {
                            if (dt.getLeaseItemId().equals(hlsDurationLn.getLeaseItemId())) {
                                existFlag = "Y";
                                break;
                            }
                        }
                    }
                    if (existFlag.equals("N")) {
                        HlsCusPrjProjectLeaseItem item = new HlsCusPrjProjectLeaseItem();
                        item.setLeaseItemId(hlsDurationLn.getLeaseItemId());
                        item.setProjectId(hd.getProjectId());
                        hlsCusPrjProjectLeaseItemService.insert(requestCtx, item);
                    }
                }

                HlsCusPrjProjectChanceMp mp = new HlsCusPrjProjectChanceMp();
                mp.setProjectId(hd.getProjectId());
                //原抵质押 查询
                List<HlsCusPrjProjectChanceMp> mpList = mpService.select(requestCtx, mp, 1, 100000);
                if (PLEDGOR_MORTGAGORS.equals(hlsDurationLn.getSourceType())) {
                    existFlag = "N";
                    if (mpList.size() > 0) {
                        for (HlsCusPrjProjectChanceMp dt : mpList) {
                            if (dt.getLeaseItemId().equals(hlsDurationLn.getLeaseItemId())) {
                                existFlag = "Y";
                                break;
                            }
                        }
                    }
                    if (existFlag.equals("N")) {
                        HlsCusPrjProjectChanceMp item = new HlsCusPrjProjectChanceMp();
                        item.setLeaseItemId(hlsDurationLn.getLeaseItemId());
                        item.setProjectId(hd.getProjectId());
                        mpService.insert(requestCtx, item);
                    }
                }
            }
        }

    }

    @Autowired
    private JeTrxCommonService commonService;
    private static final String JE_CON_CONTRACT = "CON_CONTRACT";

    public void jeTrxPayPledges(IRequest iRequest, HlsCusOffBalanceAccount hlsCusOffBalanceAccount, Long companyId) {
        //核销数据 插凭证流水表
        Map params = new HashMap<>();
        params.put("jeTrxId", hlsCusOffBalanceAccount.getBalanceAccountId());
        params.put("projectId", hlsCusOffBalanceAccount.getProjectId());
        params.put("companyId", companyId);
        params.put("jeSourceDoc", JE_CON_CONTRACT);
        params.put("jeSourceId", hlsCusOffBalanceAccount.getProjectId());
        AbstractJeTrxService paymentEqimentJeTrx = commonService.map.get("OFF_BALANCE_SHEET_PLEDGES");
        paymentEqimentJeTrx.process(iRequest, params);


    }

    /**
     * 更新租赁物excel
     */
    private void updateLeaseExcel(Long excelId, Long sourceDocumentId, String sourceDocumentCategory, Long resultId, String sheets, String sheetName, String excelCode) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        ResponseData rd = null;
        try {
            //构造一个空的workbook

            HlsWebExcel hlsWebExcel = new HlsWebExcel();
            hlsWebExcel.setExcelCode(excelCode);
            List<HlsWebExcel> hlsWebExcelList = webExcelMapper.select(hlsWebExcel);
            HlsWebExcel config = hlsWebExcelList.get(0);


            List list = new ArrayList();
            HlsWebExcelCalcResult calcResult = new HlsWebExcelCalcResult();


            String jsonSheets = hlsWebExcelCalcResultService.unzipSheet(config.getSheets());

            calcResult.setExcelId(excelId);
            calcResult.setSourceDocumentId(sourceDocumentId);
            calcResult.setSourceDocumentCategory(sourceDocumentCategory);
            calcResult.setSheets(jsonSheets);
            calcResult.setCompressSheets(sheets);
            calcResult.setResultId(resultId);

            JSONArray array = JSONArray.parseArray(jsonSheets);
            XSSFWorkbook wb = new XSSFWorkbook();
            hlsWebExcelCalcResultService.readSheets(wb, array);

            HlsLeaseItemDetail hlsLeaseItemDetail = new HlsLeaseItemDetail();
            hlsLeaseItemDetail.setLeaseItemId(sourceDocumentId);
            List<HlsLeaseItemDetail> hlsLeaseItemDetailList = hlsLeaseItemDetailMapper.select(hlsLeaseItemDetail);


            JSONArray modifiedLines = hlsWebExcelCalcResultService.extractLineDataFromObject(hlsLeaseItemDetailList, sheetName, excelId);
            hlsWebExcelCalcResultService.updateSheetLines(modifiedLines, wb.getSheet(sheetName), excelId);
            hlsWebExcelCalcResultService.writeBack(wb, array, excelId);


            calcResult.setSheets(hlsWebExcelCalcResultService.getCompressSheets(JSON.toJSONString(array)));
            calcResult.setCompressSheets(calcResult.getSheets());
            calcResult = hlsWebExcelCalcResultService.updateByPrimaryKeySelective(iRequest, calcResult);

        } catch (Exception e) {
            rd = new ResponseData(false);
            rd.setMessage(e.getMessage());
        }
    }

    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;

    @Autowired
    HlsDocFileTempletMapper hlsDocFileTempletMapper;

    @Autowired
    FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private IFndAttachmentService fndAttachmentService;
    /**
     * 模板表
     */
    private static final String FILE_TEMPLET_TABLE = "hls_doc_file_templet";
    /**
     * fnd_atm_attachment的sourceType属性
     */
    private static final String SOURCE_TYPE_CODE_ATTACHMENT = "fnd_atm_attachment_multi";

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachmentMulti> contextCreateMultiple(IRequest request, String code, String hdId, HttpServletResponse response) throws Exception {
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
                    map.put("hdId", hdId);
                    //通过输入流构建WordprocessingMLPackage对象
                    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(modelIs);
                    //将构建的wordMLPackage对象传入方法中
                    wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, request, map);
                    //将替换后的合同文本保存到服务器上作为备份
                    wordMLPackage.save(new File(copyPath));

                    fileBackLength = (int) new File(copyPath).length();

                    //1.保存文件
                    contextCreateMultipleSave(request, code, hdId, sysFile, copyPath, fileBackLength, response);

                    //2.返回结果
                    FndAttachmentMulti fndAttachmentMultiNew = new FndAttachmentMulti();
                    fndAttachmentMultiNew.setTablePkValue(hdId);
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


    public void contextCreateMultipleSave(IRequest request, String code, String hdId, FndAttachment sysFile, String copyPath, int fileLength, HttpServletResponse response) throws Exception {
        //查询是否存在过
        FndAttachmentMulti condition = new FndAttachmentMulti();
        condition.setTablePkValue(hdId);
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
            conDocFileMulti.setTablePkValue(hdId);
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

    /*提前结清 审批通过 数据处理 */
    @Override
    public void executeEt(IRequest request, HlsDurationHd hd) {
        HlsDurationLn hlsDurationLn = new HlsDurationLn();
        hlsDurationLn.setHdId(hd.getHdId());
        List<HlsDurationLn> lnList = hlsDurationLnMapper.hlsDurationLnEtDetailQueryNew(hlsDurationLn);
        for (HlsDurationLn ln1 : lnList) {
            //偶发性多次执行通过过程 这里做个状态判断
            if ("APPROVING".equalsIgnoreCase(hd.getDurationStatus())) {
                HlsCusConContract contract = new HlsCusConContract();
                contract.setContractId(ln1.getContractId());
                contract = cusConContractService.selectByPrimaryKey(request, contract);
                databaseLockProvider.lock(contract);
                if ("PENDING".equalsIgnoreCase(contract.getContractStatus())) {
                    //更新合同状态为结清
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setContractId(ln1.getContractId());
                    conContract.setContractStatus("ET");
                    conContract.setChangeDate(ln1.getEtDate());
                    cusConContractService.updateByPrimaryKeySelective(request, conContract);
                    //处理现金流
                    HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
                    cashflow.setContractId(ln1.getContractId());
                    cashflow.setCfStatus("BLOCK");
                    cashflow.setDueDate(ln1.getEtDate());
                    //冻结现金流
                    conContractCashflowMapper.updateCashfolwBlock(cashflow);
                    //留购价现金流解冻并且更新日期为提前结清日
                    conContractCashflowMapper.updateCashfolwRetention(cashflow);
                    //插入现金流
                    DecimalFormat df = new DecimalFormat("###,##0.00");
                    HlsCusConContractCashflow concashflow = new HlsCusConContractCashflow();
                    concashflow.setDueDate(ln1.getEtDate());
                    concashflow.setFinIncomeDate(ln1.getEtDate());
                    concashflow.setCalcDate(ln1.getEtDate());
                    concashflow.setContractId(ln1.getContractId());
                    Double dueAmount = ln1.getEtPreAmount();
                    Double principal = ln1.getRentalResidualAmount();
                    Double interest = dueAmount - principal;
                    Double vatRate = ln1.getIntRate();
                    Double netPrincipal = HlsCusMathUtil.round((principal / (1 + vatRate)), 2);
                    Double netInterest = HlsCusMathUtil.round((interest / (1 + vatRate)), 2);
                    //拆税
                    concashflow.setDueAmount(dueAmount);
                    concashflow.setVatDueAmount(dueAmount - (netPrincipal + netInterest));
                    concashflow.setNetDueAmount(netPrincipal + netInterest);
                    concashflow.setPrincipal(principal);
                    concashflow.setVatPrincipal(principal - netPrincipal);
                    concashflow.setNetPrincipal(netPrincipal);
                    concashflow.setInterest(interest);
                    concashflow.setVatInterest(interest - netInterest);
                    concashflow.setNetInterest(netInterest);
                    concashflow.setCfItem(11L);
                    concashflow.setCfType(11L);
                    concashflow.setCfDirection("INFLOW");
                    concashflow.setCfStatus("RELEASE");
                    concashflow.setTimes(ln1.getLeaseTimes());
                    //插入结清现金流
                    contractCashflowService.insertSelective(request, concashflow);
                }
            }
        }
    }

    /*合同终止 审批通过 数据处理 */
    @Override
    public void executeEnd(IRequest request, HlsDurationHd hd) {
        if (hd != null) {
            //偶发性多次执行通过过程 这里做个状态判断
            if ("APPROVING".equalsIgnoreCase(hd.getDurationStatus())) {
                HlsCusConContract contract = new HlsCusConContract();
                databaseLockProvider.lock(contract);
                //更新合同状态为结清
                contract.setProjectId(hd.getProjectId());
                contract.setContractStatus("END");
                cusConContractService.updateByProjectId(contract);
            }
        }
    }

    @SneakyThrows
    @Override
    public HlsCusPrjProject durationCreate(IRequest request, HlsDurationHd hd) {
        //保存头表
        //Map<String, String> params = new HashMap<>();

        //获取编码规则
        //String durationNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
        //hd.setDurationNumber(durationNumber);
        //hd.setDurationStatus("NEW");
        //hd.setExecuteStatus("NEW");
        //hd.setExecuteFlag("N");
        //HlsDurationHd hlsDurationHd = self().insertSelective(iRequest, hd);
        //hdId = hlsDurationHd.getHdId();

        //复制一份报价和现金流信息,将报价id存入HlsDurationLn CHANGE_QUOTATION_ID


        //查询租赁物与抵押物生成HlsDurationItem数据生成


        //方案2 使用常规变更
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hd.getProjectId());

        //1,创建变更头 使用CON_CONTRACT_CHANGE_REQ表
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
        /*project.setMeetingStatus("PENDING");
        project.set__status("update");*/

        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, project);

        /*插入审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setDocumentId(project.getProjectId());
        hlsCusChangeReqInfo.setDocumentCategory("CON_CONTRACT");
        List<HlsCusChangeReqInfo> hlsCusChangeReqInfoList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);

        hlsCusChangeReqInfo.setStatus("NEW");
        List<HlsCusChangeReqInfo> newChangeReqList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);
        /*if (newChangeReqList != null && newChangeReqList.size() > 0) {
            throw new HlsCusException("该项目正在变更中!");
        }*/

        Map<String, String> params = new HashMap<String, String>();
        //String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(request, "PRJ_PROJECT", "PRJ_PROJECT", "LEASE", params);
        String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(request, "CONTRACT_CHANGE", "STD", "CONTRACT_CHANGE", params);

        hlsCusChangeReqInfo.setDocumentVersionId(hlsCusChangeReqInfoList.size() + 1L);
        hlsCusChangeReqInfo.setChangeReqUserId(request.getUserId());
        hlsCusChangeReqInfo.setChangeReqDate(new Date());
        hlsCusChangeReqInfo.setChangeType(hd.getDurationType());
        hlsCusChangeReqInfo.setApproveNumber(codeRuleValue/*hlsCusPrjProject.getApproveNumber()*/);
        //创建时设置为N，流程结束后再修改为Y
        hlsCusChangeReqInfo.setInstanceEndFlag("N");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.insertSelective(request, hlsCusChangeReqInfo);

        /*复制当前项目，创建变更数据*/
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjProject);
        hlsBeanRefUtilService.setFieldValue(prjProject, map1);
        prjProject.setRefProjectId(prjProject.getProjectId());
        prjProject.setProjectId(null);
        prjProject.setDataType("CHANGE_REQ");
        prjProject.setProjectStatus("NEW");
        prjProject.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        prjProject = hlsCusPrjProjectService.insertSelective(request, prjProject);

        //复制一份用作历史的数据, dataType:CHANGE_REQ_HISTORY, 在变更结束后改为HISTORY
        HlsCusPrjProject prjChanceH = new HlsCusPrjProject();
        hlsBeanRefUtilService.setFieldValue(prjChanceH, map1);
        prjChanceH.setRefProjectId(prjChanceH.getProjectId());
        prjChanceH.setProjectId(null);
        prjChanceH.setDataType("CHANGE_REQ_HISTORY");
        prjChanceH.setProjectStatus("NEW");
        prjChanceH.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        prjChanceH = hlsCusPrjProjectService.insertSelective(request, prjChanceH);

        //插入从表
        hlsCusPrjProjectService.projectBackUp(request, prjProject, true);
        hlsCusPrjProjectService.projectBackUp(request, prjChanceH, true);

        //获取合同
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(prjProject.getRefProjectId());
        hlsCusConContract = cusConContractService.select(request, hlsCusConContract, 1, 999).get(0);

        //还款计划变更需要初始化一份con_change_repayment_info表数据,关联prjProject
        if("REPAYMENT_SCHEDULE".equals(hd.getDurationType())){
            ConChangeRepaymentInfo conChangeRepaymentInFo = new ConChangeRepaymentInfo();
            conChangeRepaymentInFo.setProjectId(prjProject.getProjectId());
            conChangeRepaymentInFo.setOldProjectId(prjProject.getRefProjectId());
            conChangeRepaymentInFo.setOldXirr(hlsCusConContract.getXirr());

            // 变更前总期数 还款变更方案
            List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            hlsCusPrjQuotation.setSourceDocumentId(prjProject.getRefProjectId());
            hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotationList = prjQuotationMapper.select(hlsCusPrjQuotation);
            hlsCusPrjQuotation = hlsCusPrjQuotationList.get(0);
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflows = new ArrayList<>();
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            hlsCusPrjQuotationCashflow.setCfItem(1L);
            hlsCusPrjQuotationCashflows = prjQuotationCashflowMapper.select(hlsCusPrjQuotationCashflow);

            //查询现金流表获取 总期数
            conChangeRepaymentInFo.setBeforeTotalTimes(Long.valueOf(hlsCusPrjQuotationCashflows.size()));
            //跟新还款计划比对字段
            Map resMap = prjQuotationCashflowMapper.selectQuotationCompareInfo(hlsCusPrjQuotation.getQuotationId()).get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            conChangeRepaymentInFo.setBeforeIrr(Double.valueOf(resMap.get("irr").toString()));
            conChangeRepaymentInFo.setBeforeLeaseEndDate(sdf.parse(resMap.get("lease_end_date").toString()));
            conChangeRepaymentInFo.setBeforeTotalAmount(Double.valueOf(resMap.get("total_amount").toString()));
            conChangeRepaymentInFo.setBeforeTotalPrincipal(Double.valueOf(resMap.get("total_principal").toString()));
            conChangeRepaymentInFo.setBeforeTotalInterest(Double.valueOf(resMap.get("total_interest").toString()));
            conChangeRepaymentInFo.setBeforeTotalFee(Double.valueOf(resMap.get("total_fee").toString()));
            conChangeRepaymentInFo.setBeforeTotalDeposit(Double.valueOf(resMap.get("total_deposit").toString()));

            conChangeRepaymentInFo.setCalcFlag("N");
            conChangeRepaymentInfoService.insert(request, conChangeRepaymentInFo);
        }

        //提前结清初始化提前结清信息
        if("ET".equals(hd.getDurationType())){
            ConChangeEtInfo conChangeEtInFo = new ConChangeEtInfo();
            conChangeEtInFo.setProjectId(prjProject.getProjectId());
            conChangeEtInFo.setOldProjectId(prjProject.getRefProjectId());
            conChangeEtInFo.setOldXirr(hlsCusConContract.getXirr());
            //起租日
            conChangeEtInFo.setLeaseStartDate(hlsCusConContract.getLeaseStartDate());
            conChangeEtInFo.setCalcFlag("N");
            conChangeEtInfoSerivce.insert(request, conChangeEtInFo);
        }


        //查询打开链接需要的参数
        HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();
        cusPrjProject.setProjectId(prjProject.getProjectId());
        List<HlsCusPrjProject> list = prjProjectMapper.selectProjectChangeReqInfo(cusPrjProject);

        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
            cusPrjProject = list.get(0);
        }

        return cusPrjProject;
    }



}
