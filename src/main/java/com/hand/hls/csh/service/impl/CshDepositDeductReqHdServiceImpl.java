package com.hand.hls.csh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.CshDepositDeductReqHdMapper;
import com.hand.hls.csh.mapper.CshDepositDeductReqLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.service.*;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.service.IFndCompanyService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.wfl.service.IActivitiStartService;
import jodd.util.ArraysUtil;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshDepositDeductReqHdServiceImpl extends BaseServiceImpl<CshDepositDeductReqHd> implements ICshDepositDeductReqHdService {

    private static final Logger logger = LoggerFactory.getLogger(CshDepositDeductReqHdServiceImpl.class);
    private static final String CSH_DEPOSIT_DEDUCT_REQ_HD = "cshDepositDeductReqHd";
    private static final String REQ_HD_ID = "reqHdId";
    private static final String REQ_NUMBER = "reqNumber";
    private static final String WORKFLOW_TYPE = "workFlowType";
    private static final String DOCUMENT_CATEGORY = "documentCategory";
    private static final String DOCUMENT_TYPE = "documentType";
    private static final String BUSINESS_TYPE = "businessType";
    private static final String DOCUMENT_ID = "documentId";
    private static final String DOCUMENT_NUMBER = "documentNumber";
    private static final String DOCUMENT_NAME = "documentName";
    private static final String TRANSACTION_AMOUNT = "transactionAmount";
    private static final String BP_NAME = "bpName";
    private static final String CONTRACT_ID = "contractId";
    private static final String BACK = "BACK";
    private static final Long[] INSERT_CFITEMS = {1L, 11L, 13L, 9L, 65L};

    private static final String DEPOSIT_CREDIT = "DEPOSIT_CREDIT";
    private static final String APPROVED = "APPROVED";

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private ICshDepositDeductReqLnService iCshDepositDeductReqLnService;
    @Autowired
    private CshDepositDeductReqLnMapper cshDepositDeductReqLnMapper;
    @Autowired
    private CshDepositDeductReqHdMapper cshDepositDeductReqHdMapper;
    @Autowired
    private IActivitiStartService iActivitiStartService;
    @Autowired
    private IFndCompanyService iFndCompanyService;
    @Autowired
    private CshWriteOffService writeOffService;
    @Autowired
    private CshTransactionService cshTransactionService;
    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private IConContractCashflowService iConContractCashflowService;
    @Autowired
    private IConContractService contractService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;
    @Autowired
    private ICshDepositDeductJobLogsService cshDepositDeductJobLogsService;
    @Autowired
    private ICshDepositDeductReqHdService cshDepositDeductReqHdService;


    @Override
    public CshDepositDeductReqHd selectCshDepositDeductReqHdInit(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd) {
        cshDepositDeductReqHd = cshDepositDeductReqHdMapper.selectCshDepositDeductReqHdInit(cshDepositDeductReqHd);
        return cshDepositDeductReqHd;
    }

    @Override
    public CshDepositDeductReqHd selectCshDepositDeductReqHd(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd) {
        cshDepositDeductReqHd = cshDepositDeductReqHdMapper.selectCshDepositDeductReqHd(cshDepositDeductReqHd);
        return cshDepositDeductReqHd;
    }

    @Override
    public List<CshDepositDeductReqHd> selectCshDepositDeductReqHdList(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<CshDepositDeductReqHd> cshDepositDeductReqHdList = cshDepositDeductReqHdMapper.selectCshDepositDeductReqHdList(cshDepositDeductReqHd);
        return cshDepositDeductReqHdList;
    }

    @Override
    public CshDepositDeductReqHd saveDepositDeductReqWithoutNewTransaction(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd, String createDeductMethod) throws HlsCusException {
        return saveDepositDeductReq(request, cshDepositDeductReqHd, createDeductMethod);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public CshDepositDeductReqHd saveDepositDeductReq(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd, String createDeductMethod) throws HlsCusException {
        //保存申请头信息
        if (cshDepositDeductReqHd.getReqHdId() == null) {
            HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
            hlsCusCshTransaction.setTransactionId(cshDepositDeductReqHd.getTransactionId());
            hlsCusCshTransaction = cshTransactionService.selectByPrimaryKey(request, hlsCusCshTransaction);
            Assert.notNull(hlsCusCshTransaction, "未找到保证金现金事务");

            //单据类别信息
            cshDepositDeductReqHd.setDocumentCategory(HlsConstantUtil.CshDepositDeductReqHd.DOCUMENT_CATEGORY);
            cshDepositDeductReqHd.setDocumentType(HlsConstantUtil.CshDepositDeductReqHd.DOCUMENT_TYPE);
            cshDepositDeductReqHd.setBusinessType(HlsConstantUtil.CshDepositDeductReqHd.DOCUMENT_CATEGORY);

            //获取申请编号
            if (cshDepositDeductReqHd.getReqNumber() == "" || cshDepositDeductReqHd.getReqNumber() == null) {
                Map<String, String> params = new HashMap<String, String>();
                String reqNumber = fndCodingRuleValuesService.getCodeRuleValue(request, cshDepositDeductReqHd.getDocumentCategory(), cshDepositDeductReqHd.getDocumentType(), cshDepositDeductReqHd.getBusinessType(), params);
                cshDepositDeductReqHd.setReqNumber(reqNumber);
            }

            //单据所属公司及所有者
            cshDepositDeductReqHd.setCompanyId(request.getCompanyId());
            cshDepositDeductReqHd.setOwnerUserId(request.getUserId());

            //单据状态
            if (StringUtils.isEmpty(cshDepositDeductReqHd.getReqStatus())) {
                cshDepositDeductReqHd.setReqStatus(HlsConstantUtil.WorkFlowStatus.NEW);
            }

            //授权规则
            FndCompany fndCompany = new FndCompany();
            fndCompany.setCompanyId(request.getCompanyId());
            fndCompany = iFndCompanyService.selectByPrimaryKey(request, fndCompany);
            String companyCode = fndCompany.getCompanyCode();
            String unitCode = request.getAttribute("unitCode") == null ? "" : (String) request.getAttribute("unitCode");
            String positionCode = request.getAttribute("positionCode") == null ? "" : (String) request.getAttribute("positionCode");
            String employeeCode = request.getEmployeeCode();
            String authorityRuleString = '"' + companyCode + '"' + "." + '"' + unitCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + employeeCode + '"';

            cshDepositDeductReqHd.setAuthorityRuleString(authorityRuleString);

            // 精度处理
            cshDepositDeductReqHd.setCanReturnAmount(MathUtil.round(cshDepositDeductReqHd.getCanReturnAmount(), 2));
            cshDepositDeductReqHd.setCreateDeductMethod(createDeductMethod);
            self().insert(request, cshDepositDeductReqHd);
        } else {
            self().updateByPrimaryKey(request, cshDepositDeductReqHd);
        }
        if ("MANUAL".equals(createDeductMethod) && cshDepositDeductReqHd.getReqHdId() != null) {
            CshDepositDeductReqHd cshDepositDeductReqHdNew = new CshDepositDeductReqHd();
            cshDepositDeductReqHdNew.setReqHdId(cshDepositDeductReqHd.getReqHdId());
            cshDepositDeductReqHdNew = this.selectByPrimaryKey(request, cshDepositDeductReqHdNew);
            if (APPROVED.equals(cshDepositDeductReqHdNew.getReqStatus()) || "APPROVING".equals(cshDepositDeductReqHdNew.getReqStatus())) {
                throw new HlsCusException("单据状态改变，保存失败");
            }
        }

        Long reqHdId = cshDepositDeductReqHd.getReqHdId();
        List<Long> reqLnIdList = cshDepositDeductReqLnMapper.queryReqLnId(cshDepositDeductReqHd.getReqHdId());

        //保存申请行信息
        if (cshDepositDeductReqHd.getCshDepositDeductReqLnList() != null) {
            for (CshDepositDeductReqLn cshDepositDeductReqLn : cshDepositDeductReqHd.getCshDepositDeductReqLnList()) {
                cshDepositDeductReqLn.setSurplusAmount(MathUtil.round((cshDepositDeductReqLn.getSurplusAmount()), 2));
                cshDepositDeductReqLn.setSurplusPrincipal(MathUtil.round(cshDepositDeductReqLn.getSurplusPrincipal(), 2));
                cshDepositDeductReqLn.setSurplusInterest(MathUtil.round(cshDepositDeductReqLn.getSurplusInterest(), 2));
                cshDepositDeductReqLn.setWriteOffDueAmount(MathUtil.round(cshDepositDeductReqLn.getWriteOffDueAmount(), 2));
                cshDepositDeductReqLn.setWriteOffPrincipal(MathUtil.round(cshDepositDeductReqLn.getWriteOffPrincipal(), 2));
                cshDepositDeductReqLn.setWriteOffInterest(MathUtil.round(cshDepositDeductReqLn.getWriteOffInterest(), 2));

                if (cshDepositDeductReqLn.getReqLnId() != null) {
                    iCshDepositDeductReqLnService.updateByPrimaryKey(request, cshDepositDeductReqLn);
                    reqLnIdList.remove(cshDepositDeductReqLn.getReqLnId());
                } else {
                    cshDepositDeductReqLn.setReqHdId(reqHdId);
                    iCshDepositDeductReqLnService.insert(request, cshDepositDeductReqLn);
                }
            }
        }

        if ("MANUAL".equals(createDeductMethod)) {
            //如果是前台维护的，加入删除逻辑
            for (Long reqLnId : reqLnIdList) {
                CshDepositDeductReqLn cshDepositDeductReqln = new CshDepositDeductReqLn();
                cshDepositDeductReqln.setReqLnId(reqLnId);
                cshDepositDeductReqLnMapper.deleteByPrimaryKey(cshDepositDeductReqln);
            }
        }
        return cshDepositDeductReqHd;
    }

    //校验合同现金流抵扣是否合规，并判断是否走审批流程
    public String checkDeductContract(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException {
        String wflFlag = "Y";
        Long maxUnDeductTimes = iCshDepositDeductReqLnService.selectUnDeductCashflowMaxTimes(cshDepositDeductReqHd);

        CshDepositDeductReqLn cshDepositDeductReqLnQuery = new CshDepositDeductReqLn();
        cshDepositDeductReqLnQuery.setReqHdId(cshDepositDeductReqHd.getReqHdId());
        List<CshDepositDeductReqLn> cshDepositDeductReqLnList = iCshDepositDeductReqLnService.selectCshDepositDeductReqLnList(request, cshDepositDeductReqLnQuery, 0, 0);

        /*只能抵扣最后一期现金流 否则提交审批时报错：请核销所有期中现金流后发起保证金抵扣期末现金。*/
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(cshDepositDeductReqHd.getContractId());
        List<HlsCusConContractCashflow> cashflowList = iConContractCashflowService.select(request, cashflow, 1, 999);
        //获取现金流最大期次
        HlsCusConContractCashflow cashflowMaxtimes = cashflowList.stream().max(Comparator.comparing(HlsCusConContractCashflow::getTimes)).get();
        Long maxTimes = cashflowMaxtimes.getTimes();

        List<CshDepositDeductReqLn> filterLn = cshDepositDeductReqLnList.stream().filter(
                item -> item.getTimes().equals(maxTimes.doubleValue())).collect(Collectors.toList());

        cashflowList = cashflowList.stream().filter(
                    o -> 1L == o.getCfItem() || 8L == o.getCfItem() || 9L == o.getCfItem() || 11L == o.getCfItem() || 13L == o.getCfItem()
            ).collect(Collectors.toList());
        //未核销金额和
        Double sumUnReceivedAmount = 0D;
        //期中未核销现金流期数
        Long sumUnReceivedTimes = 0L;
        //所有未核销现金流期数
        Long sumUnReceivedTimesAll = 0L;
        for (HlsCusConContractCashflow item : cashflowList) {
            Double unReceivedAmount = MathUtil.sub(item.getDueAmount(), nvl(item.getReceivedAmount(), 0d));
            if (unReceivedAmount > 0) {
                sumUnReceivedAmount = sumUnReceivedAmount + unReceivedAmount;
                sumUnReceivedTimesAll = sumUnReceivedTimesAll + 1;
                if (item.getTimes() < maxTimes) {
                    sumUnReceivedTimes = sumUnReceivedTimes + 1;
                }
            }
        }
        if (sumUnReceivedTimes > 0) {
            throw new HlsCusException("请核销所有期中现金流后发起保证金抵扣期末现金流！");
        }
        return wflFlag;
    }

    @Override
    public void blockAmount(IRequest iRequest, Long deductReqId) throws HlsCusException {
        CshDepositDeductReqHd cshDepositDeductReqHd = cshDepositDeductReqHdMapper.selectByPrimaryKey(deductReqId);
        //更新保证金及合同现金流冻结金额
        double writeOffDueAmountTotal = 0.0;
        CshDepositDeductReqLn cshDepositDeductReqLnQuery = new CshDepositDeductReqLn();
        cshDepositDeductReqLnQuery.setReqHdId(deductReqId);
        List<CshDepositDeductReqLn> cshDepositDeductReqLnList = iCshDepositDeductReqLnService.selectCshDepositDeductReqLnList(iRequest, cshDepositDeductReqLnQuery, 0, 0);
        //更新现金流冻结金额
        for (CshDepositDeductReqLn cshDepositDeductReqLn : cshDepositDeductReqLnList) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(cshDepositDeductReqLn.getCashflowId());
            hlsCusConContractCashflow = iConContractCashflowService.selectByPrimaryKey(iRequest, hlsCusConContractCashflow);

            //抵扣金额
            double writeOffDueAmount = Optional.ofNullable(cshDepositDeductReqLn.getWriteOffDueAmount()).orElse(0.0);
            double writeOffPrincipal = Optional.ofNullable(cshDepositDeductReqLn.getWriteOffPrincipal()).orElse(0.0);
            double writeOffInterest = Optional.ofNullable(cshDepositDeductReqLn.getWriteOffInterest()).orElse(0.0);

            //已冻结金额
            double blockAmount = Optional.ofNullable(hlsCusConContractCashflow.getBlockAmount()).orElse(0.0);
            double blockPrincipal = Optional.ofNullable(hlsCusConContractCashflow.getBlockPrincipal()).orElse(0.0);
            double blockInterest = Optional.ofNullable(hlsCusConContractCashflow.getBlockInterest()).orElse(0.0);

            Double canWriteOffAmount = MathUtil.sub(MathUtil.sub(hlsCusConContractCashflow.getDueAmount(),
                    Optional.ofNullable(hlsCusConContractCashflow.getReceivedAmount()).orElse(0.0),
                    SCALE), nvl(hlsCusConContractCashflow.getBlockAmount(), 0d), SCALE);
            if (Double.compare(canWriteOffAmount, writeOffDueAmount) < 0) {
                throw new HlsCusException("合同可抵扣金额不足，请核对!");
            }

            hlsCusConContractCashflow.setBlockAmount(MathUtil.add(blockAmount, writeOffDueAmount, SCALE));
            hlsCusConContractCashflow.setBlockPrincipal(MathUtil.add(blockPrincipal, writeOffPrincipal, SCALE));
            hlsCusConContractCashflow.setBlockInterest(MathUtil.add(blockInterest, writeOffInterest, SCALE));

            iConContractCashflowService.updateByPrimaryKeySelective(iRequest, hlsCusConContractCashflow);

            writeOffDueAmountTotal = MathUtil.add(writeOffDueAmountTotal, writeOffDueAmount, SCALE);
        }
        //更新保证金冻结金额
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionId(cshDepositDeductReqHd.getTransactionId());
        hlsCusCshTransaction = cshTransactionService.selectByPrimaryKey(iRequest, hlsCusCshTransaction);
        Double blockAmount = Optional.ofNullable(hlsCusCshTransaction.getBlockAmount()).orElse(0D);

        double canWriteOffAmount = MathUtil.sub(
                MathUtil.sub(hlsCusCshTransaction.getTransactionAmount(), Optional.ofNullable(hlsCusCshTransaction.getWriteOffAmount()).orElse(0D)),
                Optional.ofNullable(hlsCusCshTransaction.getBlockAmount()).orElse(0D), SCALE);
        if (Double.compare(canWriteOffAmount, MathUtil.round(writeOffDueAmountTotal, SCALE)) < 0) {
            throw new HlsCusException("保证金可抵扣金额不足，请核对!");
        }

        hlsCusCshTransaction.setBlockAmount(MathUtil.add(blockAmount, writeOffDueAmountTotal, SCALE));
        cshTransactionService.updateByPrimaryKeySelective(iRequest, hlsCusCshTransaction);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void blockAmountWithNewTransaction(IRequest iRequest, Long deductReqId) throws HlsCusException {
        self().blockAmount(iRequest, deductReqId);
    }

    @Override
    public CshDepositDeductReqHd submitDepositDeductReq(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException {
        cshDepositDeductReqHd = self().selectCshDepositDeductReqHd(request, cshDepositDeductReqHd);
        String reqStatus = cshDepositDeductReqHd.getReqStatus();
        if (HlsConstantUtil.WorkFlowStatus.APPROVING.equals(reqStatus) || HlsConstantUtil.WorkFlowStatus.APPROVED.equals(reqStatus)) {
            throw new HlsCusException("抵扣申请单状态有误，请核对!");
        }
        //校验合同现金流抵扣是否合规，从后往前抵扣
        String wflFlag = checkDeductContract(request, cshDepositDeductReqHd);
        Map<String, Object> map = cshDepositDeductReqHdService.getWflObjectMap(request, cshDepositDeductReqHd);

        List<CshDepositDeductReqHd> list = new ArrayList<>();
        list.add(cshDepositDeductReqHd);

        if("Y".equals(wflFlag)) {
            iActivitiStartService.start(request, list, map);
        }else{
            self().updateReqStatus(request, cshDepositDeductReqHd.getReqHdId(), "APPROVED");
        }

        return cshDepositDeductReqHd;
    }

    @Override
    public boolean deleteDepositDeductReq(IRequest requestContext, JSONObject param) throws HlsCusException {
        Long reqHdId = Long.valueOf(param.getString("req_hd_id"));
        CshDepositDeductReqHd cshDepositDeductReqHd = new CshDepositDeductReqHd();
        cshDepositDeductReqHd.setReqHdId(reqHdId);
        try {
            cshDepositDeductReqHd = this.selectCshDepositDeductReqHd(requestContext, cshDepositDeductReqHd);
        } catch (Exception e) {
            throw new HlsCusException("该单据已失效，请刷新");
        }
        if (!"NEW".equals(cshDepositDeductReqHd.getReqStatus())) {
            throw new HlsCusException("仅新建单据可以取消");
        }
        CshDepositDeductReqHd cancelReqHd = new CshDepositDeductReqHd();
        cancelReqHd.setReqHdId(reqHdId);
        cancelReqHd.setReqStatus("CANCEL");
        this.updateByPrimaryKey(requestContext, cancelReqHd);
        return true;
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public Map<String, Object> getWflObjectMap(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException {
        //更改项目状态
        cshDepositDeductReqHd.setReqStatus(HlsConstantUtil.WorkFlowStatus.APPROVING);
        self().updateByPrimaryKeySelective(request, cshDepositDeductReqHd);

        self().blockAmount(request, cshDepositDeductReqHd.getReqHdId());

        checkDepositAmt(request, cshDepositDeductReqHd.getReqHdId());
        //设置工作流参数
        Map<String, Object> map = new HashMap<>();
        map.put("approveResult", APPROVED);
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(cshDepositDeductReqHd));
        map.put(CSH_DEPOSIT_DEDUCT_REQ_HD, jsonObject.toString());
        map.put(REQ_HD_ID, cshDepositDeductReqHd.getReqHdId());
        map.put(REQ_NUMBER, cshDepositDeductReqHd.getReqNumber());
        map.put(WORKFLOW_TYPE, HlsConstantUtil.CshDepositDeductReqHd.WORK_FLOW_TYPE);
        map.put(DOCUMENT_CATEGORY, cshDepositDeductReqHd.getDocumentCategory());
        map.put(DOCUMENT_TYPE, cshDepositDeductReqHd.getDocumentType());
        map.put(BUSINESS_TYPE, cshDepositDeductReqHd.getBusinessType());
        map.put(DOCUMENT_ID, cshDepositDeductReqHd.getReqHdId());
        map.put(DOCUMENT_NUMBER, cshDepositDeductReqHd.getReqNumber());
        map.put(DOCUMENT_NAME, cshDepositDeductReqHd.getDocumentName());
        map.put(TRANSACTION_AMOUNT, cshDepositDeductReqHd.getTransactionAmount());
        map.put(BP_NAME, cshDepositDeductReqHd.getBpName());
        map.put(CONTRACT_ID, cshDepositDeductReqHd.getContractId());
        return map;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void beforeDeduct(IRequest iRequest, CshDepositDeductReqHd cshDepositDeductReqHd, String status) {
        CshDepositDeductReqHd deductReqHd = new CshDepositDeductReqHd();
        deductReqHd.setReqHdId(cshDepositDeductReqHd.getReqHdId());
        deductReqHd = self().selectByPrimaryKey(iRequest, deductReqHd);
        deductReqHd.setReqStatus(status);
        cshDepositDeductReqHdMapper.updateByPrimaryKeySelective(deductReqHd);
    }

    @Override
    public void releaseAmount(IRequest iRequest, Long reqHdId) {
        CshDepositDeductReqHd cshDepositDeductReqHd = cshDepositDeductReqHdMapper.selectByPrimaryKey(reqHdId);
        //冻结金额释放
        //更新保证金及合同现金流冻结金额
        double writeOffDueAmountTotal = 0D;
        CshDepositDeductReqLn cshDepositDeductReqLnQuery = new CshDepositDeductReqLn();
        cshDepositDeductReqLnQuery.setReqHdId(reqHdId);
        List<CshDepositDeductReqLn> CshDepositDeductReqLnList = iCshDepositDeductReqLnService.selectCshDepositDeductReqLnList(iRequest, cshDepositDeductReqLnQuery, 0, 0);
        //更新现金流冻结金额
        for (CshDepositDeductReqLn cshDepositDeductReqLn : CshDepositDeductReqLnList) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(cshDepositDeductReqLn.getCashflowId());
            hlsCusConContractCashflow = iConContractCashflowService.selectByPrimaryKey(iRequest, hlsCusConContractCashflow);

            //已冻结金额
            double blockAmount = Optional.ofNullable(hlsCusConContractCashflow.getBlockAmount()).orElse(0D);
            double blockPrincipal = Optional.ofNullable(hlsCusConContractCashflow.getBlockPrincipal()).orElse(0D);
            double blockInterest = Optional.ofNullable(hlsCusConContractCashflow.getBlockInterest()).orElse(0D);

            //抵扣金额
            double writeOffDueAmount = Optional.ofNullable(cshDepositDeductReqLn.getWriteOffDueAmount()).orElse(0D);
            double writeOffPrincipal = Optional.ofNullable(cshDepositDeductReqLn.getWriteOffPrincipal()).orElse(0D);
            double writeOffInterest = Optional.ofNullable(cshDepositDeductReqLn.getWriteOffInterest()).orElse(0D);


            if (Double.compare(MathUtil.sub(blockAmount, writeOffDueAmount, SCALE), 0D) < 0) {
                throw new RuntimeException("冻结金额不能为负");
            }

            hlsCusConContractCashflow.setBlockAmount(MathUtil.sub(blockAmount, writeOffDueAmount, SCALE));
            hlsCusConContractCashflow.setBlockPrincipal(MathUtil.sub(blockPrincipal, writeOffPrincipal, SCALE));
            hlsCusConContractCashflow.setBlockInterest(MathUtil.sub(blockInterest, writeOffInterest, SCALE));

            iConContractCashflowService.updateByPrimaryKeySelective(iRequest, hlsCusConContractCashflow);

            writeOffDueAmountTotal = MathUtil.add(writeOffDueAmountTotal, writeOffDueAmount);
        }
        //更新保证金冻结金额
        cshTransactionService.releaseAmount(iRequest, cshDepositDeductReqHd.getTransactionId(), writeOffDueAmountTotal);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateReqStatus(IRequest iRequest, Long id, String status) {
        CshDepositDeductReqHd cshDepositDeductReqHd = new CshDepositDeductReqHd();
        cshDepositDeductReqHd.setReqHdId(id);
        cshDepositDeductReqHd = self().selectCshDepositDeductReqHd(iRequest, cshDepositDeductReqHd);
        if (APPROVED.equalsIgnoreCase(status)) {
            //保证金剩余金额校验,现金流金额提前校验
            checkDepositAmt(iRequest, id);
        }
        cshDepositDeductReqHd.setReqStatus(APPROVED);
        self().beforeDeduct(iRequest, cshDepositDeductReqHd, status);
        if (APPROVED.equalsIgnoreCase(status)) {
            //执行保证金抵扣逻辑
            self().execDepositDeductReqApproved(iRequest, cshDepositDeductReqHd, "MANUAL");
        } else {
            self().releaseAmount(iRequest, id);
        }
    }

    public void checkDepositAmt(IRequest iRequest, Long reqHdId) {
        //检查现金事务
        CshDepositDeductReqHd cshDepositDeductReqHd = cshDepositDeductReqHdMapper.selectByPrimaryKey(reqHdId);
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionId(cshDepositDeductReqHd.getTransactionId());
        hlsCusCshTransaction = cshTransactionService.selectByPrimaryKey(iRequest, hlsCusCshTransaction);
        Double blockAmount = Optional.ofNullable(hlsCusCshTransaction.getBlockAmount()).orElse(0D);
        Double writeOffAmount = Optional.ofNullable(hlsCusCshTransaction.getWriteOffAmount()).orElse(0D);
        Double transactionAmount = Optional.ofNullable(hlsCusCshTransaction.getTransactionAmount()).orElse(0D);
        if (Double.compare(MathUtil.round(transactionAmount, SCALE), MathUtil.round(MathUtil.add(blockAmount, writeOffAmount), SCALE)) < 0) {
            throw new RuntimeException("现金事务已冻结金额超出，请检查并审批拒绝");
        }
        //检查现金流
        CshDepositDeductReqLn cshDepositDeductReqLnQuery = new CshDepositDeductReqLn();
        cshDepositDeductReqLnQuery.setReqHdId(reqHdId);
        List<CshDepositDeductReqLn> cshDepositDeductReqLnList = iCshDepositDeductReqLnService.selectCshDepositDeductReqLnList(iRequest, cshDepositDeductReqLnQuery, 0, 0);
        for (CshDepositDeductReqLn cshDepositDeductReqLn : cshDepositDeductReqLnList) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(cshDepositDeductReqLn.getCashflowId());
            hlsCusConContractCashflow = iConContractCashflowService.selectByPrimaryKey(iRequest, hlsCusConContractCashflow);
            Double cshBlockAmount = Optional.ofNullable(hlsCusConContractCashflow.getBlockAmount()).orElse(0D);
            Double receivedAmount = Optional.ofNullable(hlsCusConContractCashflow.getReceivedAmount()).orElse(0D);
            Double dueAmount = Optional.ofNullable(hlsCusConContractCashflow.getDueAmount()).orElse(0D);
            if (Double.compare(dueAmount, MathUtil.add(cshBlockAmount, receivedAmount)) < 0) {
                throw new RuntimeException("现金流已冻结金额超出，请检查并审批拒绝：" + hlsCusConContractCashflow.getCashflowId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateDeductReqStatus(IRequest iRequest, Long id, String reqStatus, String deductStatus, String message) {
        CshDepositDeductReqHd cshDepositDeductReqHd = new CshDepositDeductReqHd();
        cshDepositDeductReqHd.setReqHdId(id);
        cshDepositDeductReqHd = self().selectByPrimaryKey(iRequest, cshDepositDeductReqHd);
        cshDepositDeductReqHd.setReqStatus(reqStatus);
        cshDepositDeductReqHd.setDeductStatus(deductStatus);
        cshDepositDeductReqHd.setErrorMessage(message);
        cshDepositDeductReqHdMapper.updateByPrimaryKeySelective(cshDepositDeductReqHd);
    }

    @Override
    public void updateDeductStatus(Long id, String status, String message) {
        CshDepositDeductReqHd cshDepositDeductReqHd = new CshDepositDeductReqHd();
        cshDepositDeductReqHd.setReqHdId(id);
        cshDepositDeductReqHd.setReqStatus(HlsConstantUtil.WorkFlowStatus.APPROVED);
        cshDepositDeductReqHd.setDeductStatus(status);
        cshDepositDeductReqHd.setErrorMessage(message);
        cshDepositDeductReqHdMapper.updateByPrimaryKeySelective(cshDepositDeductReqHd);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String execDepositDeductReqApproved(IRequest iRequest, CshDepositDeductReqHd cshDepositDeductReqHd, String changeType) {
        cshDepositDeductReqHd = self().selectByPrimaryKey(iRequest, cshDepositDeductReqHd);
        String reqStatus = APPROVED;
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        try {
            hlsCusCshTransaction = hlsCusCshTransactionMapper.selectByPrimaryKey(cshDepositDeductReqHd.getTransactionId());
            hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusCshTransaction.getContractId());
        } catch (Exception e) {
            logger.error("query error", e);
        }
        String status = HlsConstantUtil.DeductSapStatus.FAILURE;
        try {
            cshDepositDeductJobLogsService.checkOtherLock(cshDepositDeductReqHd);
            CshDepositDeductReqLn cshDepositDeductReqLn = new CshDepositDeductReqLn();
            cshDepositDeductReqLn.setReqHdId(cshDepositDeductReqHd.getReqHdId());
            List<CshDepositDeductReqLn> cshDepositDeductReqLnList = iCshDepositDeductReqLnService.select(iRequest, cshDepositDeductReqLn, 0, 0);
            cshDepositDeductJobLogsService.setDeductSource(changeType);
            cshDepositDeductJobLogsService.setIRequest(iRequest);

            String message;
            try {
                self().writeOffDeposit(iRequest, cshDepositDeductReqHd);
                status = HlsConstantUtil.DeductSapStatus.SUCCESS;
                message = "抵扣成功";
                cshDepositDeductJobLogsService.success("保证金抵扣成功  ", hlsCusConContract.getContractNumber(), cshDepositDeductReqHd.getReqNumber(), hlsCusCshTransaction.getTransactionNum());
                self().updateDeductReqStatus(iRequest, cshDepositDeductReqHd.getReqHdId(), reqStatus, status, message);
            } catch (Exception e) {
                cshDepositDeductJobLogsService.error(e, "保证金抵扣异常  ", hlsCusConContract.getContractNumber(), cshDepositDeductReqHd.getReqNumber(), hlsCusCshTransaction.getTransactionNum());
                status = HlsConstantUtil.DeductSapStatus.INNER_ERROR;
                message = e.getMessage();
                self().releaseAmount(iRequest, cshDepositDeductReqHd.getReqHdId());
                self().updateDeductReqStatus(iRequest, cshDepositDeductReqHd.getReqHdId(), reqStatus, status, message);
            }

        } catch (HlsCusException e) {
            cshDepositDeductJobLogsService.error(e, "并发校验");
            String message = e.getMessage();
            self().releaseAmount(iRequest, cshDepositDeductReqHd.getReqHdId());
            self().updateDeductReqStatus(iRequest, cshDepositDeductReqHd.getReqHdId(), reqStatus, status, message);
        }
        return status;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void writeOffDeposit(IRequest iRequest, CshDepositDeductReqHd cshDepositDeductReqHd) throws Exception {
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        //执行保证金抵扣逻辑
        CshDepositDeductReqLn cshDepositDeductReqLn = new CshDepositDeductReqLn();
        cshDepositDeductReqLn.setReqHdId(cshDepositDeductReqHd.getReqHdId());
        List<CshDepositDeductReqLn> cshDepositDeductReqLnList = iCshDepositDeductReqLnService.selectCshDepositDeductReqLnList(iRequest, cshDepositDeductReqLn, 0, 0);

        Date deductDate = cshDepositDeductReqHd.getDeductDate();
        Long transactionId = cshDepositDeductReqHd.getTransactionId();
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionId(transactionId);
        hlsCusCshTransaction = hlsCusCshTransactionMapper.selectByPrimaryKey(hlsCusCshTransaction);
        List<HlsCusCshWriteOff> hlsCusCshWriteOffs = new ArrayList<>();
        Double writeOffSum = 0D;
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(hlsCusCshTransaction.getContractId());
        hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContract);

        for (CshDepositDeductReqLn cshDepositDeductReqLnTmp : cshDepositDeductReqLnList) {
            HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
            BeanUtils.copyProperties(cshDepositDeductReqLnTmp, hlsCusCshWriteOff);
            //由于BigDecimal无法复制，手动设置
            hlsCusCshWriteOff.setWriteOffDueAmount(cshDepositDeductReqLnTmp.getWriteOffDueAmount());
            hlsCusCshWriteOff.setWriteOffPrincipal(cshDepositDeductReqLnTmp.getWriteOffPrincipal());
            hlsCusCshWriteOff.setWriteOffInterest(cshDepositDeductReqLnTmp.getWriteOffInterest());
            hlsCusCshWriteOff.setDeductReqLnId(cshDepositDeductReqLnTmp.getReqLnId());
            hlsCusCshWriteOffs.add(hlsCusCshWriteOff);
            //下面两行是为了插入保证金报表
            writeOffSum = MathUtil.add(writeOffSum, cshDepositDeductReqLnTmp.getWriteOffDueAmount());
            iCshDepositDeductReqLnService.insertDepositWriteOffHistoryByDeduct(cshDepositDeductReqLnTmp.getReqLnId(), writeOffSum);
        }
        for (HlsCusCshWriteOff hlsCusCshWriteOff : hlsCusCshWriteOffs) {
            hlsCusCshWriteOff.setWriteOffDate(deductDate);
            hlsCusCshWriteOff.setWriteOffType(DEPOSIT_CREDIT);
            hlsCusCshWriteOff.setCshTransactionId(transactionId);
        }
        self().releaseAmount(iRequest, cshDepositDeductReqHd.getReqHdId());
        writeOffService.releaseCredit(iRequest, hlsCusCshWriteOffs);
        writeOffService.writeOff(iRequest, hlsCusCshWriteOffs, session);

        //更新原保证金现金流已抵扣金额
        update51CashflowDepositAmount(iRequest, hlsCusCshTransaction, writeOffSum);
    }

    public void update51CashflowDepositAmount(IRequest iRequest, HlsCusCshTransaction hlsCusCshTransaction, Double writeOffSum) {
        Long writeOffId = hlsCusCshTransaction.getSourceDocLineId();
        HlsCusCshWriteOff writeOff= new HlsCusCshWriteOff();
        writeOff.setWriteOffId(writeOffId);
        writeOff=writeOffService.selectByPrimaryKey(iRequest,writeOff);
        Long cashflow51=writeOff.getCashflowId();
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setCashflowId(cashflow51);
        cashflow = iConContractCashflowService.selectByPrimaryKey(iRequest, cashflow);
        Double depositAmount = MathUtil.add(nvl(cashflow.getDepositDeductAmount(),0d), writeOffSum);

        HlsCusConContractCashflow cashflowUpdate = new HlsCusConContractCashflow();
        cashflowUpdate.setCashflowId(cashflow51);
        cashflowUpdate.setDepositDeductAmount(depositAmount);
        iConContractCashflowService.updateByPrimaryKeySelective(iRequest, cashflowUpdate);
    }

    @Override
    @Transactional(rollbackFor = HlsCusException.class)
    public ResponseData back(IRequest iRequest, CshDepositDeductReqHd cshDepositDeductReqHd) {
        ResponseData responseData = new ResponseData(false);

        cshDepositDeductReqHd = self().selectByPrimaryKey(iRequest, cshDepositDeductReqHd);
        if (!APPROVED.equals(cshDepositDeductReqHd.getReqStatus())) {
            responseData.setMessage("仅申请状态为审批通过的可以撤回");
            return responseData;
        }
        if (StringUtils.equals(HlsConstantUtil.DeductSapStatus.SUCCESS, cshDepositDeductReqHd.getDeductStatus())) {
            responseData.setMessage("该保证金已抵扣，不能撤回!");
            return responseData;
        }
        if (StringUtils.equals(BACK, cshDepositDeductReqHd.getDeductStatus())) {
            responseData.setMessage("该保证金已撤回");
            return responseData;
        }

        CshDepositDeductReqLn cshDepositDeductReqLnQuery = new CshDepositDeductReqLn();
        cshDepositDeductReqLnQuery.setReqHdId(cshDepositDeductReqHd.getReqHdId());
        List<CshDepositDeductReqLn> cshDepositDeductReqLnList = cshDepositDeductReqLnMapper.select(cshDepositDeductReqLnQuery);

        for (CshDepositDeductReqLn cshDepositDeductReqLn : cshDepositDeductReqLnList) {
            if (cshDepositDeductReqLn.getSourceInfoId() != null) {
                List<String> statusList = cshDepositDeductReqLnMapper.selectOtherSourceInfoIdSuccess(cshDepositDeductReqLn.getSourceInfoId()
                        , cshDepositDeductReqLn.getReqHdId(), cshDepositDeductReqLn.getInfoIdBatch());
                if (CollectionUtils.isNotEmpty(statusList)) {
                    responseData.setMessage("同一期承租人还款确认数据已经在抵扣申请编号" + statusList.get(0) + "中成功核销，无法撤回");
                    return responseData;
                }
            }
        }

        self().releaseAmount(iRequest, cshDepositDeductReqHd.getReqHdId());
        responseData.setSuccess(true);
        self().updateDeductReqStatus(iRequest, cshDepositDeductReqHd.getReqHdId(),
                cshDepositDeductReqHd.getReqStatus(), BACK, "撤销成功");
        return responseData;
    }

}
