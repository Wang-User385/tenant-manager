package com.hand.hls.csh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsBpMasterBankAccountService;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.csh.service.*;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HLSCurrency;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HLSCurrencyMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import com.hand.hls.partner.mapper.YLCshTransferPaymentDtoMapper;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.mapper.SysCodeValueMapper;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Auther: Eugene Song
 * @Date: 2019年2月19日
 * @Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CshTransactionServiceImpl extends BaseServiceImpl<HlsCusCshTransaction> implements CshTransactionService {
    public static final String PAYMENT = "PAYMENT";
    public static final String YES = "Y";


    @Autowired(required = false)
    ICreateCshTransactionService iCreateCshTransactionService;
    @Autowired(required = false)
    IReversedCshTransactionService iReversedCshTransactionService;
    @Autowired(required = false)
    IRefundCshTransactionService iRefundCshTransactionService;
    @Autowired(required = false)
    HlsBpMasterBankAccountService hlsBpMasterBankAccountService;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    DatabaseLockProvider databaseLockProvider;
    @Autowired
    HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    CshWriteOffService cshWriteOffService;
    @Autowired
    HlsCusCshWriteOffMapper hlsCusCshWriteOffMapper;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;

    @Autowired
    private HLSCurrencyMapper hlsCurrencyMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    HlsCusConContractCashflowService hlsCusConContractCashflowService;

    @Autowired
    private HlsCusFctQuotationCashflowService hlsCusFctQuotationCashflowService;

    @Autowired
    HlsCusCshTransactionMapper mapper;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private CshBankAccountMapper cshBankAccountMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;


    @Autowired
    private SysCodeValueMapper sysCodeValueMapper;

    @Autowired
    private HlsCusBpMasterBankAccountMapper bpMasterBankAccountMapper;

    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;

    @Autowired
    private HlsCusCshTransactionRefundMapper hlsCusCshTransactionRefundMapper;

    @Autowired
    private CshTransactionRefundLnMapper cshTransactionRefundLnMapper;

    @Autowired
    private HlsCusConContractMapper contractMapper;

    Map<String, String> params = new HashMap<String, String>();

    @Override
    public List<Map> collectionDetails(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize) {

        PageHelper.startPage(pagenum, pagesize);
        return mapper.collectionDetails(hlsCusCshTransaction);
    }

    @Override
    public List<Map> CashThingTransactionQueryDetail(Map map) {
        List<Map> list = mapper.queryByTransactionId(map);
        for (Map dt : list) {
            String writeOffType = dt.get("writeOffType").toString();
            if ("REFUND".equalsIgnoreCase(writeOffType)) {
                Long transactionId = Long.parseLong(dt.get("transactionId").toString());
                HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
                hlsCusCshTransaction.setTransactionId(transactionId);
                hlsCusCshTransaction = mapper.selectByPrimaryKey(hlsCusCshTransaction);
                if (hlsCusCshTransaction != null) {
                    dt.put("refundFlag", "true");
                    dt.put("refundFlagDesc", "退款中");
                } else {
                    dt.put("refundFlag", "false");
                    dt.put("refundFlagDesc", "退款审批退回");
                }
            }
        }
        return list;
    }

    @Override
    public void updateCshTrByPrimaryKey(HlsCusCshTransaction cshTr) {
        mapper.updateCshTrByPrimaryKey(cshTr);
    }


    @Override
    public List<HlsCusCshTransaction> queryDetailByIdList(Long transactionId) {
        return mapper.queryDetailByIdList(transactionId);
    }


    /**
     * @Discription:生成单据编号
     */
    private void getTransactionNum(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionType("RECEIPT");
        hlsCusCshTransaction.setBusinessType("RECEIPT");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                hlsCusCshTransaction.getTransactionType(), hlsCusCshTransaction.getBusinessType(), params));
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
        hlsCusCshTransaction.setCompanyId(hlsCusCshTransaction.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");

    }

    @Override
    /**
     * @Discription:现金事务查询
     * @param: [requestCtx, hlsCusCshTransaction, pagenum, pagesize]
     * @return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransaction>
     */
    public List<HlsCusCshTransaction> queryCshTransaction(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum, pagesize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }

        return hlsCusCshTransactionMapper.queryCshTransaction(hlsCusCshTransaction);
    }

    @Override
    /**
     * @Discription:收款新增
     * @param: [requestCtx, hlsCusCshTransaction]
     * @return: com.hand.hls.csh.dto.HlsCusCshTransaction
     */
    public HlsCusCshTransaction createCshTransaction(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        if (iCreateCshTransactionService != null) {
            iCreateCshTransactionService.beforCreate(requestCtx, hlsCusCshTransaction);
        }
        /*//当对面银行账户不存在时，新增一条商业伙伴银行账户
        if (hlsCusCshTransaction.getBpBankAccountId() == null) {
            HlsCusBpMasterBankAccount ba = new HlsCusBpMasterBankAccount();
            ba.setBpId(hlsCusCshTransaction.getBpId());
            ba.setBankAccountName(hlsCusCshTransaction.getBpBankAccountName());
            ba.setBankAccountNum(hlsCusCshTransaction.getBpBankAccountNum());
            ba.setBankBranchName(hlsCusCshTransaction.getBpBankBranchName());
            ba.setBankFullName(hlsCusCshTransaction.getBpBankName());
            hlsBpMasterBankAccountService.insertSelective(requestCtx, ba);

            hlsCusCshTransaction.setBpBankAccountId(ba.getBankAccountId());
        }*/

        //通过编码规则生成编号
        getTransactionNum(requestCtx, hlsCusCshTransaction);

        if (hlsCusCshTransaction.getWriteOffAmount() == null) {
            hlsCusCshTransaction.setWriteOffAmount(0D);
        }
        hlsCusCshTransaction.setCompanyId(requestCtx.getCompanyId());
        //创建&&更新
        if (hlsCusCshTransaction.getTransactionId() == null) {
            self().insertSelective(requestCtx, hlsCusCshTransaction);
        } else {
            self().updateByPrimaryKeySelective(requestCtx, hlsCusCshTransaction);
        }

        if (iCreateCshTransactionService != null) {
            iCreateCshTransactionService.afterCreate(requestCtx, hlsCusCshTransaction);
        }
        return hlsCusCshTransaction;

    }

    @Override
    /**
     * @Discription:收款过账
     * @param: [requestCtx, hlsCusCshTransactionList]
     * @return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransaction>
     */
    public List<HlsCusCshTransaction> postCshTransaction(IRequest requestCtx, List<HlsCusCshTransaction> hlsCusCshTransactionList) throws ResMessageException {

        for (HlsCusCshTransaction hlsCusCshTransaction : hlsCusCshTransactionList) {
            //step1 校验状态
            //锁表 oracle库这个方法会导致更新数据一直无返回,注释掉
            //databaseLockProvider.lock(hlsCusCshTransaction);
            hlsCusCshTransaction = self().selectByPrimaryKey(requestCtx, hlsCusCshTransaction);
            if ("Y".equals(hlsCusCshTransaction.getPostedFlag())) {
                throw new ResMessageException("已过账无需重复过账！");
            }
            //step2 更新标志
            hlsCusCshTransaction.setPostedFlag("Y");
            self().updateByPrimaryKeySelective(requestCtx, hlsCusCshTransaction);

            //step3 生成凭证
            //modify 在核销时生成 收款新增凭证
            /*AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");
            Map transactionParams = new HashMap<>();
            transactionParams.put("jeTrxId", hlsCusCshTransaction.getTransactionId());
            transactionParams.put("companyId", requestCtx.getCompanyId());
            if (hlsCusCshTransaction.getContractId() != null) {
                transactionParams.put("jeSourceId", hlsCusCshTransaction.getContractId());
                transactionParams.put("jeSourceDoc", "CON_CONTRACT");
            } else if (hlsCusCshTransaction.getLonContractId() != null) {
                transactionParams.put("jeSourceId", hlsCusCshTransaction.getLonContractId());
                transactionParams.put("jeSourceDoc", "LON_CONTRACT");
            } else if (hlsCusCshTransaction.getWithdrawId() != null) {
                transactionParams.put("jeSourceId", hlsCusCshTransaction.getWithdrawId());
                transactionParams.put("jeSourceDoc", "FCT_CONTRACT_WITHDRAW");
            }
            transactionJeTrxService.process(requestCtx, transactionParams);*/
        }

        return hlsCusCshTransactionList;
    }

    @Autowired
    private YLCshTransferPaymentDtoMapper ylCshTransferPaymentDtoMapper;

    @Override
    /**
     * @Discription:收款反冲
     * @param: [requestCtx, hlsCusCshTransactionList]
     * @return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransaction>
     */
    public List<HlsCusCshTransaction> reverseCshTransaction(IRequest requestCtx, List<HlsCusCshTransaction> hlsCusCshTransactionList) throws ResMessageException {
        for (HlsCusCshTransaction hlsCusCshTransaction : hlsCusCshTransactionList) {
            //step1 校验状态
            //锁表
            //databaseLockProvider.lock(hlsCusCshTransaction);
            HlsCusCshTransaction cshTransaction = self().selectByPrimaryKey(requestCtx, hlsCusCshTransaction);

            if (iReversedCshTransactionService != null) {
                iReversedCshTransactionService.beforReverse(requestCtx, hlsCusCshTransaction);
            }


            if ("Y".equals(hlsCusCshTransaction.getReversedFlag())) {
                throw new ResMessageException("已反冲无需重复反冲！");
            }
            if (hlsCusCshTransaction.getReversedDate().compareTo(cshTransaction.getTransactionDate()) == -1) {
                throw new ResMessageException("反冲日期不能早于现金事务的日期！");
            }

            //插入一条反冲记录
            HlsCusCshTransaction cshTr = new HlsCusCshTransaction(cshTransaction);

            if (cshTr.getTransactionAmount() == null) {
                cshTr.setTransactionAmount(0d);
            }

            if (cshTr.getWriteOffAmount() == null) {
                cshTr.setWriteOffAmount(0d);
            }

            if (cshTr != null) {
                Double num = 0 - cshTr.getTransactionAmount() + cshTr.getWriteOffAmount();

                cshTr.setTransactionId(null);
                cshTr.setTransactionAmount(num);
                cshTr.setTransactionDate(hlsCusCshTransaction.getReversedDate());
                cshTr.setPenaltyCalcDate(hlsCusCshTransaction.getReversedDate());
                cshTr.setReversedFlag("R");
                cshTr.setDescription(hlsCusCshTransaction.getDescription());
                cshTr.setReversedDescription(hlsCusCshTransaction.getReversedDescription());
                cshTr.setCompanyId(hlsCusCshTransaction.getCompanyId());
                cshTr.setReversedTrxId(hlsCusCshTransaction.getTransactionId());
                cshTr.setReversedDate(hlsCusCshTransaction.getReversedDate());
                cshTr.setPostedFlag("N");
                cshTr.setBankSlipNum(hlsCusCshTransaction.getBankSlipNum());
                cshTr.setContractId(null);
                cshTr.setHandlingCharge(null);
                cshTr.setWriteOffFlag("NOT");
                cshTr.setWriteOffAmount(0D);
                cshTr.setFullWriteOffDate(null);
                cshTr.setSourceDocCategory(null);
                cshTr.setSourceDocId(null);
                cshTr.setSourceDocLineId(null);
                cshTr.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, cshTr.getTransactionCategory(), cshTr.getTransactionType(), cshTr.getBusinessType(), params));

            } else {
                return null;
            }

            cshTr = self().insertSelective(requestCtx, cshTr);
            Example example = new Example(YLCshTransferPaymentDto.class);
            example.createCriteria().andEqualTo("transactionId",hlsCusCshTransaction.getTransactionId());
            List<YLCshTransferPaymentDto> ylCshTransferPaymentDtos = ylCshTransferPaymentDtoMapper.selectByExample(example);
            if ("Y".equals(hlsCusCshTransaction.getRefV15()) && CollectionUtils.isEmpty(ylCshTransferPaymentDtos)) {
                hlsCusCshTransaction.setReversedDate(cshTransaction.getReversedDate());
                hlsCusCshTransaction.setReversedTrxId(cshTr.getTransactionId());
                hlsCusCshTransactionMapper.updateByPrimaryKey(hlsCusCshTransaction);
            } else {
                hlsCusCshTransaction.setReversedFlag("W");
                hlsCusCshTransaction.setReversedTrxId(cshTr.getTransactionId());
                hlsCusCshTransaction.setReversedDate(cshTr.getReversedDate());
                hlsCusCshTransaction.setReversedDescription(cshTr.getReversedDescription());
                hlsCusCshTransaction.setBankSlipNum(cshTr.getBankSlipNum());
                hlsCusCshTransactionMapper.updateByPrimaryKey(hlsCusCshTransaction);
                if (!CollectionUtils.isEmpty(ylCshTransferPaymentDtos)){
                    YLCshTransferPaymentDto dto = ylCshTransferPaymentDtos.get(0);
                    dto.setTransferPaymentStatus("UNCONFIRMED");
                    dto.setTransactionId(null);
                    ylCshTransferPaymentDtoMapper.updateByPrimaryKey(dto);
                }
            }

            //生成凭证
            AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");
            Map transactionParams = new HashMap<>();
            transactionParams.put("jeTrxId", cshTr.getTransactionId());
            transactionParams.put("companyId", requestCtx.getCompanyId());
            transactionParams.put("contractId", cshTr.getContractId());
            transactionParams.put("sourceDoc", "CON_CONTRACT");
            transactionParams.put("reverseJeDate", hlsCusCshTransaction.getReversedDate());
            transactionParams.put("reverseJeTrxId", cshTransaction.getTransactionId());
            //transactionJeTrxService.process(requestCtx, transactionParams);

            if (iReversedCshTransactionService != null) {
                iReversedCshTransactionService.afterReverse(requestCtx, hlsCusCshTransaction);
            }
        }
        return hlsCusCshTransactionList;
    }
    @Override
    /**
     * @Title: refundCshTransaction
     * @Discription: 收款退款
     * @Param: [requestCtx, cshTransactionRefundList]
     * @Return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransactionRefund>
     */
    public List<HlsCusCshTransactionRefund> refundCshTransaction(IRequest requestCtx, List<HlsCusCshTransactionRefund> cshTransactionRefundList) throws BeyondAmountLimitException, ResMessageException {

        for (HlsCusCshTransactionRefund cshTransactionRefund : cshTransactionRefundList) {

            HlsCusCshTransactionRefund refund = hlsCusCshTransactionRefundMapper.selectByPrimaryKey(cshTransactionRefund.getRefundId());
            //退款标志
            refund.setRefundFlag("Y");
            cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, refund);

            CshTransactionRefundLn transactionRefundLn = new CshTransactionRefundLn();
            transactionRefundLn.setRefundId(refund.getRefundId());
            List<CshTransactionRefundLn> refundLnList = cshTransactionRefundLnMapper.select(transactionRefundLn);

            for (CshTransactionRefundLn refundLn : refundLnList) {

                //step 1 在核销表内新增一条数据
                HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                cshWriteOff.setWriteOffType("REFUND");
                cshWriteOff.setWriteOffDate(refund.getRefundDate());
                cshWriteOff.setCshTransactionId(refund.getTransactionId());
                cshWriteOff.setCshWriteOffAmount(refundLn.getRefundAmount());
                cshWriteOff.setReversedFlag("N");
                cshWriteOff.setWriteOffDueAmount(refundLn.getRefundAmount());
                cshWriteOffService.insertSelective(requestCtx, cshWriteOff);


                //step 2 在现金事物表中增加一条退款记录
                HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
                hlsCusCshTransaction.setTransactionId(refund.getTransactionId());
                hlsCusCshTransaction = self().selectByPrimaryKey(requestCtx, hlsCusCshTransaction);

                HlsCusCshTransaction returnCsh = new HlsCusCshTransaction(hlsCusCshTransaction);
                returnCsh.setTransactionId(null);
                returnCsh.setTransactionCategory("CSH_TRANSACTION");
                returnCsh.setTransactionType("REFUND");
                if (!"DEPOSIT".equals(hlsCusCshTransaction.getBusinessType()) && !"DEPOSIT_POOL".equals(hlsCusCshTransaction.getBusinessType())) {
                    returnCsh.setBusinessType("PAYMENT");
                } else {
                    //如果是保证金退款，则类型为DEPOSIT
                    returnCsh.setBusinessType(hlsCusCshTransaction.getBusinessType());
                }

                returnCsh.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, returnCsh.getTransactionCategory(), returnCsh.getTransactionType(), PAYMENT, params));
                returnCsh.setTransactionDate(refund.getRefundDate());
                returnCsh.setPenaltyCalcDate(refund.getRefundDate());
                returnCsh.setCompanyId(requestCtx.getCompanyId());
                returnCsh.setCurrencyCode(refund.getCurrencyCode());
                returnCsh.setTransactionAmount(refundLn.getRefundAmount());
                returnCsh.setReversedFlag("N");
                returnCsh.setReversedTrxId(null);
                returnCsh.setReversedDate(null);
                returnCsh.setPostedFlag("N");
                returnCsh.setContractId(null);
                returnCsh.setHandlingCharge(null);
                returnCsh.setWriteOffFlag("NOT");
                returnCsh.setWriteOffAmount(0d);
                returnCsh.setFullWriteOffDate(null);
                returnCsh.setSourceDocCategory("CSH_WRITE_OFF");
                returnCsh.setSourceDocId(hlsCusCshTransaction.getTransactionId());
                returnCsh.setSourceDocLineId(cshWriteOff.getWriteOffId());
                self().insertSelective(requestCtx, returnCsh);

                //step 3 更新现金是服务中的核销金额和核销标志（判断是否核销完全，完全核销时更新完全核销日期）
                hlsCusCshTransactionMapper.updateCshTrByPrimaryKey(hlsCusCshTransaction);

                //step 4 更新核销表中的后续现金事物Id
                hlsCusCshWriteOffMapper.updateSubCshTrxId(cshWriteOff.getWriteOffId(), returnCsh.getTransactionId(), returnCsh.getTransactionAmount());

                //step 5 凭证
                Map<String, AbstractJeTrxService> interfaceMaps = jeTrxCommonService.map;
                AbstractJeTrxService writeOffJeTrxService = interfaceMaps.get("CSH_WRITE_OFF");
                AbstractJeTrxService transactionJeTrxService = interfaceMaps.get("CSH_TRANSACTION");

                /*更新凭证事物流水*/
                Map writeOffParams = new HashMap<>();
                writeOffParams.put("jeTrxId", cshWriteOff.getWriteOffId());
                writeOffParams.put("companyId", requestCtx.getCompanyId());
                writeOffParams.put("contractId", cshWriteOff.getContractId());
                writeOffParams.put("sourceDoc", "CON_CONTRACT");
                writeOffJeTrxService.process(requestCtx, writeOffParams);

                Map transactionParams = new HashMap<>();
                transactionParams.put("jeTrxId", returnCsh.getTransactionId());
                transactionParams.put("companyId", requestCtx.getCompanyId());
                transactionParams.put("contractId", hlsCusCshTransaction.getContractId());
                transactionParams.put("sourceDoc", "CON_CONTRACT");
                transactionJeTrxService.process(requestCtx, transactionParams);
            }
        }
        return cshTransactionRefundList;
    }


    @Override
    /**
     * @Discription:收款退款申请
     * @param: [requestCtx, hlsCusCshTransactionList]
     * @return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransaction>
     */
    public void refundCshTransactionSubmit(IRequest requestCtx, HttpSession session, List<HlsCusCshTransaction> hlsCusCshTransactionList) throws BeyondAmountLimitException, ResMessageException {

        //插入申请表
        List<HlsCusCshTransactionRefund> cshTransactionRefundList = new ArrayList<>();
        for (HlsCusCshTransaction hlsCusCshTransaction : hlsCusCshTransactionList) {
            //锁表
            databaseLockProvider.lock(hlsCusCshTransaction);

            HlsCusCshTransaction cshTr = self().selectByPrimaryKey(requestCtx, hlsCusCshTransaction);

            if (iRefundCshTransactionService != null) {
                iRefundCshTransactionService.beforRefund(requestCtx, hlsCusCshTransaction);
            }
            //校验状态
            if (!"Y".equals(hlsCusCshTransaction.getPostedFlag())) {
                throw new ResMessageException("请先过账在退款！");
            }
            if (!"N".equals(hlsCusCshTransaction.getReversedFlag())) {
                throw new ResMessageException("已反冲不能退款！");
            }
            if ("FULL".equals(hlsCusCshTransaction.getWriteOffFlag())) {
                throw new ResMessageException("已全部核销不能退款");
            }
            //leaf2 中退款日期存储在TransactionDate字段上，节省了一个字段
            if (hlsCusCshTransaction.getTransactionDate().compareTo(cshTr.getTransactionDate()) == -1) {
                throw new ResMessageException("退款日期不能小于收款日期！");
            }


            if (hlsCusCshTransaction.getWriteOffAmount() == null) {
                hlsCusCshTransaction.setWriteOffAmount(0d);
            }
            if (hlsCusCshTransaction.getReturnDueAmount() == null) {
                hlsCusCshTransaction.setReturnDueAmount(0d);
            }

            Double sumAmount = hlsCusCshTransaction.getWriteOffAmount() + hlsCusCshTransaction.getReturnDueAmount();
            if (new BigDecimal(sumAmount).compareTo(new BigDecimal(0)) == 0) {
                hlsCusCshTransaction.setWriteOffFlag("NOT");
                hlsCusCshTransaction.setWriteOffAmount(sumAmount);
            } else if (new BigDecimal(sumAmount).compareTo(new BigDecimal(hlsCusCshTransaction.getTransactionAmount())) < 0) {
                hlsCusCshTransaction.setWriteOffFlag("PARTIAL");
                hlsCusCshTransaction.setWriteOffAmount(sumAmount);
            } else if (new BigDecimal(sumAmount).compareTo(new BigDecimal(hlsCusCshTransaction.getTransactionAmount())) == 0) {
                hlsCusCshTransaction.setWriteOffFlag("FULL");
                hlsCusCshTransaction.setWriteOffAmount(sumAmount);
                hlsCusCshTransaction.setFullWriteOffDate(hlsCusCshTransaction.getTransactionDate());
            } else {
                throw new BeyondAmountLimitException();
            }


            HlsCusCshTransactionRefund cshTransactionRefund = new HlsCusCshTransactionRefund();
            cshTransactionRefund.setTransactionId(hlsCusCshTransaction.getTransactionId());
            cshTransactionRefund.setRefundNumber(codingRuleValuesService.getCodeRuleValue(requestCtx, "CSH_TRX",
                    "REFUND", "REFUND", params));
            cshTransactionRefund.setRefundDate(hlsCusCshTransaction.getTransactionDate());
            cshTransactionRefund.setRefundAmount(hlsCusCshTransaction.getReturnDueAmount());
            //没有保存按钮 直接提交
            cshTransactionRefund.setRefundStatus("APPROVING");
            cshTransactionRefund.setBankAccountId(hlsCusCshTransaction.getBankAccountId());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
            cshTransactionRefund.setPeriodNumber(df.format(hlsCusCshTransaction.getTransactionDate()));
            cshTransactionRefund.setCurrencyCode(hlsCusCshTransaction.getCurrencyCode());
            cshTransactionRefund.setBankSlipNum(hlsCusCshTransaction.getBankSlipNum());
            cshTransactionRefund.setBpBankAccountId(cshTr.getBpBankAccountId());
            cshTransactionRefund.setDescription(hlsCusCshTransaction.getDescription());
            cshTransactionRefund.setRefundFlag("N");
            cshTransactionRefund.setCreatedBy(hlsCusCshTransaction.getCreatedBy());
            cshTransactionRefundService.insertSelective(requestCtx, cshTransactionRefund);

            cshTransactionRefundList.add(cshTransactionRefund);

            //提交工作流
            Map<String, Object> params = new HashMap<String, Object>();
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(cshTransactionRefund));

            params.put("cshTransactionRefund", jsonObject.toString());
            params.put("documentId", cshTransactionRefund.getRefundId());
            params.put("refundId", cshTransactionRefund.getRefundId());
            params.put("documentCategory", "CSH_TRANSACTION_REFUND_WFL");
            params.put("documentType", "CSH_TRANSACTION_REFUND_WFL");
            params.put("workFlowType", "CSH_TRANSACTION_REFUND_WFL");
            params.put("documentNumber", cshTransactionRefund.getRefundNumber());
            params.put("documentName", cshTr.getTransactionNum() + "的收款退款申请");
            params.put("companyId", requestCtx.getCompanyId());
            params.put("unitId", session.getAttribute("unitId"));

            activitiStartService.start(requestCtx, cshTransactionRefundList, params);


        }


    }


    /**
     * 收款退款
     * 1、在核销表内新增一条数据
     * 2、在现金事物表中增加一条退款记录
     * 3、更新现金是服务中的核销金额和核销标志（判断是否核销完全，完全核销时更新完全核销日期）
     * 4、更新核销表中的后续现金事物Id
     * modify_by xuju 2018/06/18
     *
     * @throws BeyondAmountLimitException
     */
    @Override
    public void updateReturn(IRequest requestContext, Long companyId, HlsCusCshTransaction cshTransaction) throws BeyondAmountLimitException {
        //判斷
        Long writeOffId = cshTransaction.getWriteOffId();

        if (cshTransaction.getWriteOffAmount() == null) {
            cshTransaction.setWriteOffAmount(0d);
        }
        if (cshTransaction.getReturnDueAmount() == null) {
            cshTransaction.setReturnDueAmount(0d);
        }
        Double sumAmount = CalculateUtil.add(cshTransaction.getWriteOffAmount(), cshTransaction.getReturnDueAmount());

        if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            cshTransaction.setWriteOffFlag("NOT");
            cshTransaction.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cshTransaction.getTransactionAmount().toString())) < 0 && sumAmount > 0) {
            cshTransaction.setWriteOffFlag("PARTIAL");
            cshTransaction.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cshTransaction.getTransactionAmount().toString())) == 0) {
            cshTransaction.setWriteOffFlag("FULL");
            cshTransaction.setWriteOffAmount(sumAmount);
            cshTransaction.setTransactionAmount(sumAmount);
            cshTransaction.setFullWriteOffDate(cshTransaction.getTransactionDate());
        } else {
            throw new BeyondAmountLimitException();
        }

        //3、更新现金是服务中的核销金额和核销标志（判断是否核销完全，完全核销时更新完全核销日期）
        mapper.updateCshTrByPrimaryKey(cshTransaction);

        HLSCurrency hlsCurrency = new HLSCurrency();
        HlsCusCshTransaction transaction = hlsCusCshTransactionMapper.selectByPrimaryKey(cshTransaction.getTransactionId());
        hlsCurrency.setCurrencyCode(transaction.getCurrencyCode());
        hlsCurrency = hlsCurrencyMapper.selectOne(hlsCurrency);


        //1、在核销表内新增一条数据
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        if (writeOffId != null && writeOffId != 0) {
            cshWriteOff.setWriteOffId(writeOffId);
            cshWriteOff = cshWriteOffService.selectByPrimaryKey(requestContext, cshWriteOff);
            cshWriteOff.setWriteOffDate(cshTransaction.getTransactionDate());
            cshWriteOff.setCshWriteOffAmount(cshTransaction.getReturnDueAmount());
            cshWriteOff.setWriteOffDueAmount(cshTransaction.getReturnDueAmount());
            cshWriteOff = cshWriteOffService.updateByPrimaryKeySelective(requestContext, cshWriteOff);
        } else {
            cshWriteOff.setWriteOffType("REFUND");
            cshWriteOff.setWriteOffDocCategory(cshTransaction.getSourceDocCategory());
            cshWriteOff.setWriteOffDate(cshTransaction.getTransactionDate());
            cshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
            cshWriteOff.setCshWriteOffAmount(cshTransaction.getReturnDueAmount());
            cshWriteOff.setReversedFlag("N");

            //现金流信息-现金流待定
            cshWriteOff.setCfItem(transaction.getCfItem());
            //cshWriteOff.setCfType(transaction.getCfType());

            cshWriteOff.setContractId(transaction.getContractId());
            cshWriteOff.setCashflowId(transaction.getSourceDocLineId());

            //期数
            if ("CON_CONTRACT".equalsIgnoreCase(transaction.getSourceDocCategory())) {
                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                conContractCashflow.setCashflowId(transaction.getSourceDocLineId());
                conContractCashflow = hlsCusConContractCashflowService.selectByPrimaryKey(requestContext, conContractCashflow);
                cshWriteOff.setTimes(conContractCashflow.getTimes());
            } else if ("FCT_CONTRACT".equalsIgnoreCase(transaction.getSourceDocCategory())) {
                HlsCusFctQuotationCashflow fctQuotationCashflow = new HlsCusFctQuotationCashflow();
                fctQuotationCashflow.setQuotationCashflowId(transaction.getSourceDocLineId());
                fctQuotationCashflow = hlsCusFctQuotationCashflowService.selectByPrimaryKey(requestContext, fctQuotationCashflow);
                cshWriteOff.setTimes(fctQuotationCashflow.getTimes());
            }

            cshWriteOff.setWriteOffDueAmount(cshTransaction.getReturnDueAmount());
            cshWriteOff.setWriteOffDocCategory(cshTransaction.getSourceDocCategory());
            cshWriteOffService.insertSelective(requestContext, cshWriteOff);
        }


        //2、在现金事物表中增加一条退款记录
        HlsCusCshTransaction returnCsh = new HlsCusCshTransaction(cshTransaction);
        returnCsh.setTransactionId(null);
        returnCsh.setTransactionCategory("CSH_TRANSACTION");
        returnCsh.setTransactionType("REFUND");
        if (!"DEPOSIT".equals(cshTransaction.getBusinessType()) && !"DEPOSIT_POOL".equals(cshTransaction.getBusinessType())) {
            //如果是保证金退款，则类型为DEPOSIT
            returnCsh.setBusinessType("PAYMENT");
        } else {
            returnCsh.setBusinessType(cshTransaction.getBusinessType());
        }
        returnCsh.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestContext, returnCsh.getTransactionCategory(), returnCsh.getTransactionType(), PAYMENT, params));
        returnCsh.setTransactionDate(cshTransaction.getTransactionDate());
        returnCsh.setPenaltyCalcDate(cshTransaction.getTransactionDate());
        returnCsh.setCompanyId(companyId);
        returnCsh.setCurrencyCode("CNY");
        returnCsh.setTransactionAmount(cshTransaction.getReturnDueAmount());
        returnCsh.setReversedFlag("N");
        returnCsh.setReversedTrxId(null);
        returnCsh.setReversedDate(null);
        returnCsh.setPostedFlag("N");
        returnCsh.setContractId(null);
        returnCsh.setHandlingCharge(null);
        returnCsh.setWriteOffFlag("NA");
        returnCsh.setFullWriteOffDate(cshTransaction.getTransactionDate());
        returnCsh.setWriteOffAmount(null);
        returnCsh.setFullWriteOffDate(null);
        returnCsh.setSourceDocCategory("CSH_TRANSACTION");
        returnCsh.setSourceDocId(cshTransaction.getTransactionId());
        returnCsh.setSourceDocLineId(null);
        //查找退款现金事务
        returnCsh.setTransactionDate(cshTransaction.getTransactionDate());
        returnCsh.setBankSlipNum(cshTransaction.getBankSlipNum());
        returnCsh.setDescription(cshTransaction.getDescription());
        returnCsh.setPaymentMethod(cshTransaction.getPaymentMethod());
        returnCsh.setBankAccountId(cshTransaction.getBankAccountId());
        returnCsh.setBankAccountName(cshTransaction.getBankAccountName());
        //对方账户信息
        returnCsh.setBpBankAccountId(cshTransaction.getBpBankAccountId());
        returnCsh.setBpBankAccountName(cshTransaction.getBpBankAccountName());
        returnCsh.setBpBankAccountNum(cshTransaction.getBpBankAccountNum());
        returnCsh.setBpBankBranchName(cshTransaction.getBpBankBranchName());
        returnCsh.setBpBankName(cshTransaction.getBpBankName());
        returnCsh = self().insertSelective(requestContext, returnCsh);

        HlsCusCshWriteOff returnCshWriteOff = new HlsCusCshWriteOff();
        Map<String, String> cshWriteOffNormal = hlsBeanRefUtilService.getFieldValueMap(cshWriteOff);
        hlsBeanRefUtilService.setFieldValue(returnCshWriteOff, cshWriteOffNormal);
        returnCshWriteOff.setWriteOffId(null);
        returnCshWriteOff.setCshTransactionId(returnCsh.getTransactionId());
        returnCshWriteOff.setTransactionId(returnCsh.getTransactionId());
        cshWriteOffService.insertSelective(requestContext, returnCshWriteOff);

        // 更新现金合同流表
        if ("CON_CONTRACT".equalsIgnoreCase(transaction.getSourceDocCategory())) {
            HlsCusConContractCashflow conCashflow = updateConContractFlow(requestContext, cshWriteOff);
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(requestContext, conCashflow);

        } else if ("FCT_CONTRACT".equalsIgnoreCase(transaction.getSourceDocCategory())) {
            HlsCusFctQuotationCashflow fctCashflow = updateFctContractFlow(requestContext, cshWriteOff);
            hlsCusFctQuotationCashflowService.updateByPrimaryKeySelective(requestContext, fctCashflow);
        }


        //3、更新现金是服务中的核销金额和核销标志（判断是否核销完全，完全核销时更新完全核销日期）
        mapper.updateCshTrByPrimaryKey(cshTransaction);


        //4、更新核销表中的后续现金事物Id
        hlsCusCshWriteOffMapper.updateSubCshTrxId(cshWriteOff.getWriteOffId(), returnCsh.getTransactionId(), returnCsh.getTransactionAmount());

        //更新账户余额表

        //获取对应的凭证接口实例 by fjm 17.9.1
        Map<String, AbstractJeTrxService> interfaceMaps = jeTrxCommonService.map;
        AbstractJeTrxService writeOffJeTrxService = interfaceMaps.get("CSH_WRITE_OFF");
        AbstractJeTrxService transactionJeTrxService = interfaceMaps.get("CSH_TRANSACTION");

        /*更新凭证事物流水*/
        Map writeOffParams = new HashMap<>();
        writeOffParams.put("jeTrxId", returnCshWriteOff.getWriteOffId());
        writeOffParams.put("companyId", companyId);
        writeOffParams.put("contractId", cshWriteOff.getContractId());
        writeOffParams.put("sourceDoc", transaction.getSourceDocCategory());
        writeOffJeTrxService.process(requestContext, writeOffParams);

        Map transactionParams = new HashMap<>();
        transactionParams.put("jeTrxId", returnCsh.getTransactionId());
        transactionParams.put("companyId", companyId);
        transactionParams.put("contractId", cshTransaction.getContractId());
        transactionParams.put("sourceDoc", transaction.getSourceDocCategory());
        transactionJeTrxService.process(requestContext, transactionParams);


        if (!"DEPOSIT".equalsIgnoreCase(cshTransaction.getBusinessType())) {
            //发起退款流程，导入参数为新插入的核销记录，以方便审批退回的时候，删除记录returnCshWriteOff
            List<HlsCusCshWriteOff> list = new ArrayList<>();
            returnCshWriteOff.setCompanyId(cshTransaction.getCompanyId());
            //returnCshWriteOff.setUnitId(cshTransaction.getUnitId());
            returnCshWriteOff.setBpName(cshTransaction.getBpName());

            list.add(returnCshWriteOff);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(requestContext.getUserId());
            String employeeCode = employee.getEmployeeCode();
            requestContext.setEmployeeCode(employeeCode);
            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", "RECEIPT_REFUND_WFL");
            activitiStartService.start(requestContext, list, params);

            Map<String, Object> paramsEvent = new HashMap<String, Object>();
            String userName = "";
            if (loginUserInfoService.queryUserInfo(requestContext.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(requestContext.getEmployeeCode())).get(0).getUserName();
            }
            String msg = userName + "创建了金额为" + returnCshWriteOff.getWriteOffDueAmount() + "元的收款退款";
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeTitle", "收款退款");
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            sysEventService.eventSave(requestContext, returnCshWriteOff.getCshTransactionId(), returnCshWriteOff.getDocumentType(), returnCshWriteOff.getDocumentType(), "CSH", "WRITE_OFF_REFUND", "P2D", paramsEvent);
        }
    }


    private HlsCusConContractCashflow updateConContractFlow(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {

        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
        conContractCashflow.setCashflowId(cshWriteOff.getCashflowId());
        conContractCashflow = hlsCusConContractCashflowService.selectByPrimaryKey(iRequest, conContractCashflow);
        if (conContractCashflow.getReceivedAmount() == null) {
            conContractCashflow.setReceivedAmount(0d);
        }
        Double receivedAmount = CalculateUtil.add(conContractCashflow.getReceivedAmount(), cshWriteOff.getWriteOffDueAmount());
        conContractCashflow.setReceivedAmount(receivedAmount);

        if (new BigDecimal(conContractCashflow.getDueAmount().toString()).compareTo(new BigDecimal(conContractCashflow.getReceivedAmount().toString())) == 0) {
            conContractCashflow.setWriteOffFlag("FULL");
        } else if (new BigDecimal(conContractCashflow.getDueAmount().toString()).compareTo(new BigDecimal(conContractCashflow.getReceivedAmount())) > 0 && conContractCashflow.getReceivedAmount() > 0) {
            conContractCashflow.setWriteOffFlag("PARTIAL");
        } else if (new BigDecimal(conContractCashflow.getReceivedAmount().toString()).compareTo(new BigDecimal(0)) == 0) {
            conContractCashflow.setWriteOffFlag("NOT");
        } else {
            throw new BeyondAmountLimitException();
        }
        conContractCashflow.setLastReceivedDate(cshWriteOff.getWriteOffDate());
        return conContractCashflow;
    }

    private HlsCusFctQuotationCashflow updateFctContractFlow(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {

        HlsCusFctQuotationCashflow fctCashflow = new HlsCusFctQuotationCashflow();
        fctCashflow.setQuotationCashflowId(cshWriteOff.getCashflowId());
        fctCashflow = hlsCusFctQuotationCashflowService.selectByPrimaryKey(iRequest, fctCashflow);
        if (fctCashflow.getReceivedAmount() == null) {
            fctCashflow.setReceivedAmount(0d);
        }

        Double receivedAmount = CalculateUtil.add(fctCashflow.getReceivedAmount(), cshWriteOff.getWriteOffDueAmount());
        fctCashflow.setReceivedAmount(receivedAmount);
        if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            fctCashflow.setWriteOffFlag("NOT");
        } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(fctCashflow.getDueAmount().toString())) < 0 && receivedAmount > 0) {
            fctCashflow.setWriteOffFlag("PARTIAL");
        } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(fctCashflow.getDueAmount().toString())) == 0) {
            fctCashflow.setWriteOffFlag("FULL");
            fctCashflow.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        } else {
            throw new BeyondAmountLimitException();
        }
        fctCashflow.setLastReceivedDate(cshWriteOff.getWriteOffDate());
        return fctCashflow;
    }

    /**
     * @Title: setTransactionRefund
     * @Discription: 退款申请状态赋值
     * @Param: [cshTransaction, status]
     * @Return: com.hand.hls.csh.dto.HlsCusCshTransaction
     */
    private HlsCusCshTransaction setTransactionRefund(HlsCusCshTransaction cshTransaction, String status) {
        HlsCusCshTransactionRefund cshTransactionRefund = new HlsCusCshTransactionRefund();
        if (cshTransaction.getAdvanceCshTrxId() != null) {
            cshTransactionRefund.setTransactionId(cshTransaction.getAdvanceCshTrxId());
            List<HlsCusCshTransactionRefund> transactionRefundList = hlsCusCshTransactionRefundMapper.select(cshTransactionRefund);
            //根据该笔收款的退款申请记录获取，如有多条退款申请记录，且其中只要出现了审批中，则收款退款主界面显示审批中，优先顺序依次为：审批中>审批拒绝>审批通过>新建
            List<Map> wflStatus = sysCodeValueMapper.queryCodeDetails("HLS_WFL_STATUS");

            Long count = transactionRefundList.stream().filter(item -> status.equalsIgnoreCase(item.getRefundStatus())).count();

            if (count > 0) {
                cshTransaction.setRefundStatus(status);
                wflStatus.forEach(item -> {
                    if (item.get("code_value").equals(status)) {
                        cshTransaction.setRefundStatusDesc(item.get("meaning").toString());
                    }
                });
            }
        }
        return cshTransaction;
    }

    @Override
    public List<HlsCusCshTransaction> detailQuery(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum, pagesize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }


        List<HlsCusCshTransaction> cshTransactionList = mapper.detailQuery(hlsCusCshTransaction);

        //modify 只有待处理款项才能退款
        for (HlsCusCshTransaction cshTransaction : cshTransactionList) {
            //退款申请审批状态 根据该笔收款的退款申请记录获取，如有多条退款申请记录，且其中只要出现了审批中，则收款退款主界面显示审批中，优先顺序依次为：审批中>审批拒绝>审批通过>新建
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.NEW);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.REJECTED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        }
        return cshTransactionList;
    }
    @Override
    public List<HlsCusCshTransaction> detailQueryAdvance(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum, pagesize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }


        List<HlsCusCshTransaction> cshTransactionList = mapper.detailQueryAdvance(hlsCusCshTransaction);

        //modify 只有待处理款项才能退款
        for (HlsCusCshTransaction cshTransaction : cshTransactionList) {
            //退款申请审批状态 根据该笔收款的退款申请记录获取，如有多条退款申请记录，且其中只要出现了审批中，则收款退款主界面显示审批中，优先顺序依次为：审批中>审批拒绝>审批通过>新建
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.NEW);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.REJECTED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        }
        return cshTransactionList;
    }


    @Override
    public List<HlsCusCshTransaction> detailQueryNew(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum, pagesize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }


        List<HlsCusCshTransaction> cshTransactionList = mapper.detailQueryNew(hlsCusCshTransaction);

        //modify 只有待处理款项才能退款
        for (HlsCusCshTransaction cshTransaction : cshTransactionList) {
            //退款申请审批状态 根据该笔收款的退款申请记录获取，如有多条退款申请记录，且其中只要出现了审批中，则收款退款主界面显示审批中，优先顺序依次为：审批中>审批拒绝>审批通过>新建
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.NEW);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.REJECTED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        }
        return cshTransactionList;
    }

    @Override
    public List<HlsCusCshTransaction> detailQueryNewBusiness(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum, pagesize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }


        List<HlsCusCshTransaction> cshTransactionList = mapper.detailQueryNewBusiness(hlsCusCshTransaction);

        //modify 只有待处理款项才能退款
        for (HlsCusCshTransaction cshTransaction : cshTransactionList) {
            //退款申请审批状态 根据该笔收款的退款申请记录获取，如有多条退款申请记录，且其中只要出现了审批中，则收款退款主界面显示审批中，优先顺序依次为：审批中>审批拒绝>审批通过>新建
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.NEW);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.REJECTED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        }
        return cshTransactionList;
    }

    @Override
    public List<HlsCusCshTransaction> detailQueryNewFinance(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum, pagesize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
        if("ALL".equals(hlsCusCshTransaction.getReversedFlag())){
            hlsCusCshTransaction.setReversedFlag(null);
        }
        if("全部".equals(hlsCusCshTransaction.getReversedFlagN())){
            hlsCusCshTransaction.setReversedFlagN(null);
        }

        List<HlsCusCshTransaction> cshTransactionList = mapper.detailQueryNewFinance(hlsCusCshTransaction);

        //modify 只有待处理款项才能退款
        for (HlsCusCshTransaction cshTransaction : cshTransactionList) {
            //退款申请审批状态 根据该笔收款的退款申请记录获取，如有多条退款申请记录，且其中只要出现了审批中，则收款退款主界面显示审批中，优先顺序依次为：审批中>审批拒绝>审批通过>新建
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.NEW);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.REJECTED);
            cshTransaction = setTransactionRefund(cshTransaction, HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        }
        return cshTransactionList;
    }


    // 校验字段是否有值
    public static void validate(String message, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null || "".equals(objects[i]) || "null".equals(objects[i])) {
                throw new RuntimeException(message);
            }
        }
    }

    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId,Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;
    }

    //获取招商账号
    public List<FndInterfaceLines> getInterfaceBankData(Long hdId,Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesListc = fndInterfaceLinesMapper.fndInterfaceLinesDetailQueryC(fndInterfaceLines);
        return fndInterfaceLinesListc;
    }

    @Override
    public void receiptImport(IRequest iRequest, Long hdId,String templateCode,Long readLine) throws ExcelException, SQLException, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId,readLine);



        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusCshTransaction record = new HlsCusCshTransaction();
           Date transactionDate = null;
            Double transactionAmount=null;
            String bankAccountName=null;
            String bankAccountNum=null;
            String bankSlipNum=null;
            String currencyName=null;
            String description=null;
            String comments=null;
            String bpName=null;
            String paymentMethodName=null;
            // 对方账户
            String bpBankAccountName=null;
            String bpBankAccountNum=null;
            String bpBankName=null;
            String bpBankBranchName=null;

            if(templateCode.equals("NORMAL")){
                //校验流水号是否重复(导入数据)
               int    bankSlipNumCount = fndInterfaceLinesList.stream().collect(Collectors.groupingBy(FndInterfaceLines::getAttributes_5)).size();
                if(bankSlipNumCount < fndInterfaceLinesList.size()) {
                    throw new RuntimeException("导入模版中银行流水号重复！");
                }


                if(fndInterfaceLine.getAttributes_1() !=null){
                    // 收款日期
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_1());
                }
                if(fndInterfaceLine.getAttributes_2() !=null){
                    // 收款金额
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_2());
                }
                if(fndInterfaceLine.getAttributes_3() !=null){
                    // 收款账户
                    bankAccountName = fndInterfaceLine.getAttributes_3();
                }

                if(fndInterfaceLine.getAttributes_4() !=null){
                    // 收款账号
                    bankAccountNum = fndInterfaceLine.getAttributes_4();
                }
                if(fndInterfaceLine.getAttributes_5() !=null){
                    // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_5();
                }

                if(fndInterfaceLine.getAttributes_6() !=null){
                    // 币种
                    currencyName = fndInterfaceLine.getAttributes_6();
                }
                if(fndInterfaceLine.getAttributes_7() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_7();
                }
                if(fndInterfaceLine.getAttributes_8() !=null){
                    // 附言
                    comments = fndInterfaceLine.getAttributes_8();
                }
                if(fndInterfaceLine.getAttributes_9() !=null){
                    // 商业伙伴名称
                    bpName = fndInterfaceLine.getAttributes_9();
                }
                if(fndInterfaceLine.getAttributes_10() !=null){
                    // 付款方式
                    paymentMethodName = fndInterfaceLine.getAttributes_10();
                }
                if(fndInterfaceLine.getAttributes_11() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_11();
                }

                if(fndInterfaceLine.getAttributes_12() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_12();
                }
                if(fndInterfaceLine.getAttributes_13() !=null){

                    // 银行名称
                    bpBankName = fndInterfaceLine.getAttributes_13();
                } if(fndInterfaceLine.getAttributes_14() !=null){
                    // 支行名称
                    bpBankBranchName = fndInterfaceLine.getAttributes_14();
                }


            }else if(templateCode.equals("BEIJING")){
                //校验流水号是否重复(导入数据)
            int    bankSlipNumCount = fndInterfaceLinesList.stream().collect(Collectors.groupingBy(FndInterfaceLines::getAttributes_3)).size();
                if(bankSlipNumCount < fndInterfaceLinesList.size()) {
                    throw new RuntimeException("导入模版中银行流水号重复！");
                }

                if(fndInterfaceLine.getAttributes_4() !=null){
                    // 收款日期
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_4());
                }
                if(fndInterfaceLine.getAttributes_8() !=null){
                    // 收款金额
/*
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_8());
*/
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_8().replaceAll(",", "").toString());

                }
                if(fndInterfaceLine.getAttributes_3() !=null){
                    // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_3();
                }


                // 币种
                currencyName = "人民币";
                if(fndInterfaceLine.getAttributes_11() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_11();
                }
                if(fndInterfaceLine.getAttributes_12() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_12();
                }
                if(fndInterfaceLine.getAttributes_13() !=null){
                    // 对方账号
                    bpBankAccountNum =  fndInterfaceLine.getAttributes_13();
                }

                if(fndInterfaceLine.getAttributes_12() !=null){
                    // 商业伙伴
                    bpName = fndInterfaceLine.getAttributes_12();
                }
                List<FndInterfaceLines> fndInterfaceLinesListc = getInterfaceBankData(hdId,(long) 1);
                for (FndInterfaceLines fndInterfaceLinec : fndInterfaceLinesListc) {
                    // 收款账号
                    bankAccountNum = fndInterfaceLinec.getAttributes_2();
                }

            }else if(templateCode.equals("XINGYE")){
                //校验流水号是否重复(导入数据)
            int  bankSlipNumCount = fndInterfaceLinesList.stream().collect(Collectors.groupingBy(FndInterfaceLines::getAttributes_1)).size();
                if(bankSlipNumCount < fndInterfaceLinesList.size()) {
                    throw new RuntimeException("导入模版中银行流水号重复！");
                }
                if(fndInterfaceLine.getAttributes_16() !=null){
                    // 收款日期
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_16());
                }
                if(fndInterfaceLine.getAttributes_8() !=null){
                    // 收款金额
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_8());
                }
                if(fndInterfaceLine.getAttributes_1() !=null){
                    // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_1();
                }


                // 币种
                currencyName = "人民币";
                if(fndInterfaceLine.getAttributes_10() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_10();
                }
                if(fndInterfaceLine.getAttributes_12() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_12();
                }
                if(fndInterfaceLine.getAttributes_12() !=null){
                    // 对方账户
                    bpName = fndInterfaceLine.getAttributes_12();
                }
                if(fndInterfaceLine.getAttributes_11() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_11();
                }
                if(fndInterfaceLine.getAttributes_13() !=null){
                    // 银行名称
                    bpBankName = fndInterfaceLine.getAttributes_13();
                }
                if(fndInterfaceLine.getAttributes_2() !=null){
                    // 收款账号
                    bankAccountNum = fndInterfaceLine.getAttributes_2();
                }



            }else if(templateCode.equals("PUFA")){
                //校验流水号是否重复(导入数据)
           int     bankSlipNumCount = fndInterfaceLinesList.stream().collect(Collectors.groupingBy(FndInterfaceLines::getAttributes_11)).size();
                if(bankSlipNumCount < fndInterfaceLinesList.size()) {
                    throw new RuntimeException("导入模版中银行流水号重复！");
                }

                // 收款日期
                transactionDate = df.parse(fndInterfaceLine.getAttributes_1());


                if(fndInterfaceLine.getAttributes_6() !=null){
                    // 收款金额
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_6());
                }

                if(fndInterfaceLine.getAttributes_11() !=null){
                  // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_11();
                }

                // 币种
                currencyName = "人民币";
                if(fndInterfaceLine.getAttributes_12() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_12();
                }
                if(fndInterfaceLine.getAttributes_9() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_9();
                }

                if(fndInterfaceLine.getAttributes_8() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_8();
                }
                if(fndInterfaceLine.getAttributes_10() !=null){
                    // 银行名称
                    bpBankName = fndInterfaceLine.getAttributes_10();
                }

                // 商业伙伴名称
                if(fndInterfaceLine.getAttributes_9() !=null){
                    bpName = fndInterfaceLine.getAttributes_9();
                }
                List<FndInterfaceLines> fndInterfaceLinesListc = getInterfaceBankData(hdId,(long) 0);
                for (FndInterfaceLines fndInterfaceLinec : fndInterfaceLinesListc) {
                    // 收款账号
                    bankAccountNum = fndInterfaceLinec.getAttributes_2();
                }

            }else if(templateCode.equals("JIANHANG")){
                //校验流水号是否重复(导入数据)
           int     bankSlipNumCount = fndInterfaceLinesList.stream().collect(Collectors.groupingBy(FndInterfaceLines::getAttributes_12)).size();
                if(bankSlipNumCount < fndInterfaceLinesList.size()) {
                    throw new RuntimeException("导入模版中银行流水号重复！");
                }


                // 收款日期
                if(fndInterfaceLine.getAttributes_1() !=null){
                    // 收款日期
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_1());
                }
                if(fndInterfaceLine.getAttributes_3() !=null){
                    // 收款金额
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_3());
                }

                if(fndInterfaceLine.getAttributes_12() !=null){
                    // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_12();
                }

                // 币种
                currencyName = "人民币";
                if(fndInterfaceLine.getAttributes_10() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_10();
                }
                if(fndInterfaceLine.getAttributes_6() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_6();
                }
                if(fndInterfaceLine.getAttributes_6() !=null){
                    // 商业伙伴
                    bpName = fndInterfaceLine.getAttributes_6();
                }
                if(fndInterfaceLine.getAttributes_7() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_7();
                }
                if(fndInterfaceLine.getAttributes_8() !=null){
                    // 银行名称
                    bpBankName = fndInterfaceLine.getAttributes_8();
                }



                List<FndInterfaceLines> fndInterfaceLinesListc = getInterfaceBankData(hdId,(long) 3);
                for (FndInterfaceLines fndInterfaceLinec : fndInterfaceLinesListc) {
                    // 收款账号
                    bankAccountNum = fndInterfaceLinec.getAttributes_2();
                }
            }else if(templateCode.equals("JIAOHANG")){

                if(fndInterfaceLine.getAttributes_1() !=null){
                    // 收款日期
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_1());
                }
                if(fndInterfaceLine.getAttributes_6() !=null){

                    // 收款金额
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_6().replaceAll(",", "").toString());
                }
                if(fndInterfaceLine.getAttributes_10() !=null){
                    // 商业伙伴名称
                    bpName = fndInterfaceLine.getAttributes_10();
                }

                // 币种
                currencyName = "人民币";
                if(fndInterfaceLine.getAttributes_2() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_2();
                }

                if(fndInterfaceLine.getAttributes_9() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_9();
                }
                if(fndInterfaceLine.getAttributes_10() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_10();
                }

                List<FndInterfaceLines> fndInterfaceLinesListc = getInterfaceBankData(hdId,(long) 0);
                for (FndInterfaceLines fndInterfaceLinec : fndInterfaceLinesListc) {
                    // 收款账号
                    bankAccountNum = fndInterfaceLinec.getAttributes_2();
                }

            }else if(templateCode.equals("PINGAN")){
                // 收款日期
/*
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
*/
                if(fndInterfaceLine.getAttributes_1() !=null){
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_1());

                }
                if(fndInterfaceLine.getAttributes_8() !=null){
                    // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_8();
                }


                // 收款金额
                if(fndInterfaceLine.getAttributes_4() != null){
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_4());
                }

                // 币种
                currencyName = "人民币";
                if(fndInterfaceLine.getAttributes_10() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_10();
                }

                if(fndInterfaceLine.getAttributes_7() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_7();
                }
                if(fndInterfaceLine.getAttributes_6() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_6();
                }
                if(fndInterfaceLine.getAttributes_2() !=null){
                    // 对方账号
                    bankAccountNum = fndInterfaceLine.getAttributes_2();
                }

                if(fndInterfaceLine.getAttributes_7() !=null){

                    bpName = fndInterfaceLine.getAttributes_7();
                }

            }else if(templateCode.equals("CAIWU")){
                //校验流水号是否重复(导入数据)
                int    bankSlipNumCount = fndInterfaceLinesList.stream().collect(Collectors.groupingBy(FndInterfaceLines::getAttributes_1)).size();
                if(bankSlipNumCount < fndInterfaceLinesList.size()) {
                    throw new RuntimeException("导入模版中银行流水号重复！");
                }
                if(fndInterfaceLine.getAttributes_2() !=null){
                    // 收款日期
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_2());
                }
                if(fndInterfaceLine.getAttributes_5() !=null){
                    // 收款金额
                    //transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_5());
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_5().replaceAll(",", "").toString());

                }
                if(fndInterfaceLine.getAttributes_1() !=null){
                    // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_1();

                }

                // 币种
                currencyName ="人民币";
                if(fndInterfaceLine.getAttributes_9() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_9();

                }

                if(fndInterfaceLine.getAttributes_6() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_6();
                }
                if(fndInterfaceLine.getAttributes_7() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_7();
                }
                if(fndInterfaceLine.getAttributes_8() !=null){
                    //  // 银行名称
                    bpBankAccountNum = fndInterfaceLine.getAttributes_8();
                }
                if(fndInterfaceLine.getAttributes_11() !=null){
                    // 收款账号
                    bankAccountNum = fndInterfaceLine.getAttributes_11();

/*
                    bpBankAccountNum = fndInterfaceLine.getAttributes_11();
*/
                }

                if(fndInterfaceLine.getAttributes_6() !=null){
                    //
                    bpName = fndInterfaceLine.getAttributes_6();
                }


            }else if(templateCode.equals("ZHAOSHANG")){
                //校验流水号是否重复(导入数据)
                int    bankSlipNumCount = fndInterfaceLinesList.stream().collect(Collectors.groupingBy(FndInterfaceLines::getAttributes_9)).size();
                if(bankSlipNumCount < fndInterfaceLinesList.size()) {
                    throw new RuntimeException("导入模版中银行流水号重复！");
                }
                List<FndInterfaceLines> fndInterfaceLinesListc = getInterfaceBankData(hdId,(long) 1);
                for (FndInterfaceLines fndInterfaceLinec : fndInterfaceLinesListc) {
                    // 收款账号
                    bankAccountNum = fndInterfaceLinec.getAttributes_8();
                }
                if(fndInterfaceLine.getAttributes_1() !=null){
                    // 收款日期
                    transactionDate = df.parse(fndInterfaceLine.getAttributes_1());
                }
                if(fndInterfaceLine.getAttributes_6() !=null){
                    // 收款金额
                    transactionAmount = Double.parseDouble(fndInterfaceLine.getAttributes_6());
                }

                if(fndInterfaceLine.getAttributes_9() !=null){
                    // 银行流水号
                    bankSlipNum = fndInterfaceLine.getAttributes_9();
                }


                // 币种
                currencyName ="人民币";
                if(fndInterfaceLine.getAttributes_8() !=null){
                    // 摘要
                    description = fndInterfaceLine.getAttributes_8();
                }

                if(fndInterfaceLine.getAttributes_19() !=null){
                    // 对方账户
                    bpBankAccountName = fndInterfaceLine.getAttributes_19();
                }
                if(fndInterfaceLine.getAttributes_17() !=null){
                    // 对方账号
                    bpBankAccountNum = fndInterfaceLine.getAttributes_17();
                }
                if(fndInterfaceLine.getAttributes_16() !=null){
                    // 商业伙伴名称
                    bpName = fndInterfaceLine.getAttributes_16();
                }


            }

            //必填校验
            validate( "收款日期不能为空", transactionDate);
            validate("收款金额不能为空", transactionAmount);
            validate("商业伙伴不能为空", bpName);
/*
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:收款日期不能为空", transactionDate);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:收款金额不能为空", transactionAmount);*/
           // validate("excel第" + fndInterfaceLine.getLineNumber() + "行:收款账户不能为空", bankAccountName);
/*
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:商业伙伴不能为空", bpName);
*/
            //validate("excel第" + fndInterfaceLine.getLineNumber() + "行:对方账户不能为空", bpBankAccountName);

            //数据规范性校验
            HlsCusCshBankAccount cshBankAccount = new HlsCusCshBankAccount();
          //  cshBankAccount.setBankAccountName(bankAccountName);
            cshBankAccount.setBankAccountNum(bankAccountNum);
            List<HlsCusCshBankAccount> cshBankAccountList = cshBankAccountMapper.select(cshBankAccount);
            if (cshBankAccountList.isEmpty()) {
/*
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:收款账户或收款账号没有在系统中维护！");
*/
                throw new RuntimeException("收款账户或收款账号没有在系统中维护！");

            }

            HLSCurrency currency = new HLSCurrency();
            currency.setCurrencyName(currencyName);
            List<HLSCurrency> currencyList = hlsCurrencyMapper.select(currency);
            if (currencyList.isEmpty()) {
/*
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:币种没有在系统中维护！");
*/
                throw new RuntimeException("币种没有在系统中维护！");

            }

            //校验流水号是否重复(存量数据)
            if (bankSlipNum != null) {
                HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
                cshTransaction.setBankSlipNum(bankSlipNum);
                List<HlsCusCshTransaction> checkList = hlsCusCshTransactionMapper.checkImportBankSlipNum(cshTransaction);
                if (checkList.size() != 0) {
/*
                    throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:银行流水号重复！");
*/
                    throw new RuntimeException("银行流水号重复！");

                }
            }


           /*  HlsCusBpMaster bpMasters = new HlsCusBpMaster();
            bpMasters.setBpName(bpName);
                List<HlsCusBpMaster> bpMasterLists = hlsCusBpMasterMapper.select(bpMasters);
           if (bpMasterLists.isEmpty()) {
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:商业伙伴没有在系统中维护！");
            }
            if (bpMasterList.size() > 1) {
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:商业伙伴存在重名不能使用模版批量导入，请手工新增！");
            }*/


            List<Map> paymentMethodCodeValue = sysCodeValueMapper.queryCodeDetails("CSH.PAYMENT_METHOD");


            //如果对方银行账户 在系统中新建一个 如果商业伙伴 不存在  就不新建
            HlsCusBpMaster bpMaster = new HlsCusBpMaster();
            bpMaster.setBpName(bpName);
            List<HlsCusBpMaster> bpMasterList = hlsCusBpMasterMapper.select(bpMaster);
            Long bpId = null;
            if (bpMasterList.size() == 1) {
                bpId = bpMasterList.get(0).getBpId();
            }
            Long bpBankAccountId = null;
            HlsCusBpMasterBankAccount bpMasterBankAccount = new HlsCusBpMasterBankAccount();
            //bpMasterBankAccount.setBpId(bpMasterList.get(0).getBpId());
            bpMasterBankAccount.setBankAccountNum(bpBankAccountNum);
            //bpMasterBankAccount.setBankAccountName(bpBankAccountName);
            //bpMasterBankAccount.setBankFullName(bpBankName);
            //bpMasterBankAccount.setBankBranchName(bpBankBranchName);


            List<HlsCusBpMasterBankAccount> bpMasterBankAccountList = bpMasterBankAccountMapper.select(bpMasterBankAccount);

            if (bpMasterBankAccountList.isEmpty()) {
                // throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:对方账号、银行名称、支行名称、对方账户与系统中不匹配！");
                HlsCusBpMasterBankAccount newBankAccount = new HlsCusBpMasterBankAccount();
                if (bpId != null) {
                    newBankAccount.setBpId(bpId);
                    newBankAccount.setBankAccountNum(bpBankAccountNum);
                    newBankAccount.setBankAccountName(bpBankAccountName);
                    newBankAccount.setBankFullName(bpBankName);
                    newBankAccount.setBankBranchName(bpBankBranchName);
                    newBankAccount.setBankAccountType("GENERAL_ACCOUNT");
                    newBankAccount = hlsBpMasterBankAccountService.insertSelective(iRequest, newBankAccount);

                    bpBankAccountId = newBankAccount.getBankAccountId();
                }
            } else {
                //throw new RuntimeException("对方账号、银行名称、支行名称、对方账户与系统中不匹配！");

                bpBankAccountId = bpMasterBankAccountList.get(0).getBankAccountId();
            }

          //  HlsCusBpMaster bpMasters = hlsCusBpMasterMapper.selectByPrimaryKey(bpMasterBankAccountList.get(0).getBpId());

            if (bpId == null) {
                throw new RuntimeException( "商业伙伴没有在系统中维护！");

/*
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:商业伙伴没有在系统中维护！");
*/
            }

            //赋值 插入
            record.setTransactionDate(transactionDate);
            record.setCompanyId(iRequest.getCompanyId());
            record.setTransactionAmount(transactionAmount);
            record.setBankAccountId(cshBankAccountList.get(0).getBankAccountId());
            record.setBankSlipNum(bankSlipNum);
            record.setCurrencyCode(currencyList.get(0).getCurrencyCode());
            record.setDescription(description);
            record.setComments(comments);
            //record.setBpId(bpMasterList.get(0).getBpId());
            record.setBpId(bpId);

            String finalPaymentMethodName = paymentMethodName;
            paymentMethodCodeValue.forEach(item -> {
                if (item.get("meaning").equals(finalPaymentMethodName)) {
                    record.setPaymentMethod(item.get("code_value").toString());
                }
            });
            record.setBpBankAccountId(bpBankAccountId);
            record.setBpBankAccountName(bpBankAccountName);
            record.setBpBankAccountNum(bpBankAccountNum);
            record.setBpBankName(bpBankName);
            record.setBpBankBranchName(bpBankBranchName);
            record.setWriteOffAmount(0.0);
            record.setImportFlag(YES);
            record.setImportDate(new Date());
            getTransactionNum(iRequest, record);
            //自动过账
            record.setPostedFlag("Y");
            //插入
            self().insertSelective(iRequest, record);
        }


    }

    @Override
    public void cashflowImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 0L);


        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusCshTransaction record = new HlsCusCshTransaction();
            // 银行流水号
            String bankSlipNum = fndInterfaceLine.getAttributes_1();
            // 账号
            String bankAccountNum = fndInterfaceLine.getAttributes_2();
            // 户名
            String bankAccountName = fndInterfaceLine.getAttributes_3();


            //凭证代码 attribute_4
            //系统未使用

            // 币种
            String currencyName = fndInterfaceLine.getAttributes_5();
            // 付款方式
            //如果是‘现’，系统中‘付款方式’赋值为现金，如果是‘转’，系统中‘付款方式’为电汇

            String paymentMethod = fndInterfaceLine.getAttributes_6();
            if ("现".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "现金";
            }
            if ("转".equalsIgnoreCase(paymentMethod)) {
                paymentMethod = "电汇";
            }
            String paymentMethodName = paymentMethod;

            ;
            // 借方金额
            Double DRAmount = Double.parseDouble(nvl(fndInterfaceLine.getAttributes_7(), "0"));
            // 贷方金额
            Double CRAmount = Double.parseDouble(nvl(fndInterfaceLine.getAttributes_8(), "0"));
            // 收款金额
            Double transactionAmount = CRAmount;
            // 如果贷方金额金额为0 则不插入这笔金额
            if (CRAmount.compareTo(0.0) == 0 || CRAmount == null) {
                continue;
            }

            // 账户余额 attribute_9
            // 系统未使用


            // 摘要
            String description = fndInterfaceLine.getAttributes_10();
            // 对方账号
            String bpBankAccountNum = fndInterfaceLine.getAttributes_11();
            // 对方户名
            String bpBankAccountName = fndInterfaceLine.getAttributes_12();
            // 商业伙伴
            String bpName = bpBankAccountName;

            // 对方银行
            String bpBankName = fndInterfaceLine.getAttributes_13();
            // 对方行号 attribute_14
            // 系统未使用

            // 交易日期
            Date transactionDate = df.parse(fndInterfaceLine.getAttributes_15().substring(0, 10));

            // 用途
            String purpose = fndInterfaceLine.getAttributes_16();
            // 备注
            String remarks = fndInterfaceLine.getAttributes_17();
            // 附言
            String comments = purpose + ',' + remarks;

            if (purpose == "" || purpose == null) {
                comments = remarks;
            }


            //必填校验
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:交易日期不能为空", transactionDate);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:借方金额或贷方金额不能同时为空", transactionAmount);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:账号不能为空", bankAccountNum);
            //validate("excel第" + fndInterfaceLine.getLineNumber() + "行:对方账号不能为空", bpBankAccountNum);

            //数据规范性校验
            HlsCusCshBankAccount cshBankAccount = new HlsCusCshBankAccount();
            cshBankAccount.setBankAccountNum(bankAccountNum);
            List<HlsCusCshBankAccount> cshBankAccountList = cshBankAccountMapper.select(cshBankAccount);
            if (cshBankAccountList.isEmpty()) {
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:收款账号没有在系统中维护！");
            }

            HLSCurrency currency = new HLSCurrency();
            currency.setCurrencyName(currencyName);
            List<HLSCurrency> currencyList = hlsCurrencyMapper.select(currency);
            if (currencyList.isEmpty()) {
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:币种没有在系统中维护！");
            }

            HlsCusBpMasterBankAccount bpMasterBankAccount = new HlsCusBpMasterBankAccount();
            bpMasterBankAccount.setBankAccountNum(bpBankAccountNum);
            List<HlsCusBpMasterBankAccount> bpMasterBankAccountList = bpMasterBankAccountMapper.select(bpMasterBankAccount);


            //如果对方银行账户 在系统中新建一个 如果商业伙伴 不存在  就不新建
            HlsCusBpMaster bpMaster = new HlsCusBpMaster();
            bpMaster.setBpName(bpName);
            List<HlsCusBpMaster> bpMasterList = hlsCusBpMasterMapper.select(bpMaster);
            Long bpId = null;
            Long bpBankAccountId = null;

            if (bpMasterList.size() == 1) {
                bpId = bpMasterList.get(0).getBpId();
            }

            if (bpMasterBankAccountList.isEmpty()) {
                // throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:对方账号、银行名称、支行名称、对方账户与系统中不匹配！");
                HlsCusBpMasterBankAccount newBankAccount = new HlsCusBpMasterBankAccount();
                if (bpId != null) {
                    newBankAccount.setBpId(bpId);
                    newBankAccount.setBankAccountNum(bpBankAccountNum);
                    newBankAccount.setBankAccountName(bpBankAccountName);
                    newBankAccount.setBankFullName(bpBankName);
                    newBankAccount.setBankAccountType("GENERAL_ACCOUNT");
                    newBankAccount = hlsBpMasterBankAccountService.insertSelective(iRequest, newBankAccount);
                    bpBankAccountId = newBankAccount.getBankAccountId();
                }
            } else {
                bpBankAccountId = bpMasterBankAccountList.get(0).getBankAccountId();
            }


            /*if (bpMasterBankAccountList.size() > 1) {
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:对方账号在系统中维护给了多个商业伙伴！");
            }*/



            /*if (bpMaster == null) {
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:商业伙伴没有在系统中维护！");
            }*/

            List<Map> paymentMethodCodeValue = sysCodeValueMapper.queryCodeDetails("CSH.PAYMENT_METHOD");


            //赋值 插入
            record.setTransactionDate(transactionDate);
            record.setCompanyId(iRequest.getCompanyId());
            record.setTransactionAmount(transactionAmount);
            record.setBankAccountId(cshBankAccountList.get(0).getBankAccountId());
            record.setBankSlipNum(bankSlipNum);
            record.setCurrencyCode(currencyList.get(0).getCurrencyCode());
            record.setDescription(description);
            record.setComments(comments);
            record.setBpId(bpId);

            paymentMethodCodeValue.forEach(item -> {
                if (item.get("meaning").equals(paymentMethodName)) {
                    record.setPaymentMethod(item.get("code_value").toString());
                }
            });
            record.setBpBankAccountId(bpBankAccountId);
            record.setBpBankAccountName(bpBankAccountName);
            record.setBpBankAccountNum(bpBankAccountNum);
            record.setBpBankName(bpBankName);
            record.setWriteOffAmount(0.0);
            record.setImportFlag(YES);
            record.setImportDate(new Date());
            getTransactionNum(iRequest, record);
            //自动过账
            record.setPostedFlag("Y");
            //插入
            self().insertSelective(iRequest, record);
        }
    }

    @Override
    public List<HlsCusCshTransaction> searchCshTransation(IRequest iRequest, HlsCusCshTransaction transaction, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsCusCshTransactionMapper.searchCshTransation(transaction);
    }
    @Override
    public List<HlsCusCshTransaction> queryCshTransactionNew(IRequest iRequest, HlsCusCshTransaction transaction, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsCusCshTransactionMapper.queryCshTransactionNew(transaction);
    }
    @Override
    public void blockAmount(IRequest iRequest, long transactionId, double amount) {
        hlsCusCshTransactionMapper.updateBlockAmount(amount, transactionId);
    }

    /**
     * 二期功能：
     * 现金事务lov查询业务接口
     * 可根据事务编号模糊查询
     *
     * @param iRequest
     * @param hlsCusCshTransaction
     * @param page
     * @param pageSize
     */
    @Override
    public List<HlsCusCshTransaction> queryLov(IRequest iRequest, HlsCusCshTransaction hlsCusCshTransaction, int page, int pageSize) throws ParseException {
        PageHelper.startPage(page, pageSize);
        return hlsCusCshTransactionMapper.queryLov(hlsCusCshTransaction);
    }

    @Override
    public List<HlsCusCshTransactionRefund> queryCashflowRefundAdLov(IRequest iRequest, HlsCusCshTransactionRefund hlsCusCshTransactionRefund, int page, int pageSize) throws ParseException {
        PageHelper.startPage(page, pageSize);
        return hlsCusCshTransactionRefundMapper.createRefundQueryNew(hlsCusCshTransactionRefund);
    }

    @Override
    public List<HlsCusCshTransaction> queryDepositDeductMethod(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsCusCshTransactionMapper.queryDepositDeductMethod(hlsCusCshTransaction);
    }

    @Override
    public void updateDepositDeductMethod(IRequest requestCtx, List<HlsCusCshTransaction> transactionList) throws ResMessageException{
        for (HlsCusCshTransaction t : transactionList){
            //获取现金事务对应的合同状态
            HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
            //hlsCusCshTransaction.setManageUserId(hlsCusCshTransactionMapper.queryEmployeeId(requestCtx.getUserId()));
            hlsCusCshTransaction.setDepositContractId(t.getDepositContractId());
            /*if(Boolean.TRUE.equals(isAdmin)){
                hlsCusCshTransaction.setRoleCode("ADMIN");
            }*/
            List<HlsCusCshTransaction> hlsCusCshTransactionQuery = hlsCusCshTransactionMapper.queryDepositDeductMethod(hlsCusCshTransaction);
            if (hlsCusCshTransactionQuery == null || hlsCusCshTransactionQuery.size() != 1 || "N".equals(hlsCusCshTransactionQuery.get(0).getModifyFlag())){
                throw new ResMessageException("单据状态变化，保存失败，请刷新重试");
            }else {
                HlsCusConContract cusConContract = new HlsCusConContract();
                cusConContract.setContractId(t.getContractId());
                cusConContract.setDepositDeductMethod(t.getDepositDeductMethod());
                contractMapper.updateByPrimaryKeySelective(cusConContract);
            }
        }
    }

    /**
     * 更型冻结金额
     */
    @Override
    public void releaseAmount(IRequest iRequest, long transactionId, double amount) {
        HlsCusCshTransaction transaction = new HlsCusCshTransaction();
        transaction.setTransactionId(transactionId);
        transaction=self().selectByPrimaryKey(iRequest,transaction);
        Double blockAmount = MathUtil.sub(nvl(transaction.getBlockAmount(),0d),amount);
        HlsCusCshTransaction transactionUpdate = new HlsCusCshTransaction();
        transactionUpdate.setTransactionId(transactionId);
        transactionUpdate.setBlockAmount(blockAmount);
        self().updateByPrimaryKeySelective(iRequest, transactionUpdate);
    }

    @Override
    public void releaseAmountNewTransaction(IRequest iRequest, long transactionId, double amount) {
        releaseAmount(iRequest,transactionId,amount);
    }

    @Autowired
    private HlsCusCshBankAccountMapper hlsCusCshBankAccountMapper;

    @Override
    public void transactionImport(IRequest iRequest, Long headerId) {
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(headerId, 0L);
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
            String transactionDate = fndInterfaceLine.getAttributes_1();
            String transactionAmount = fndInterfaceLine.getAttributes_2();
            String bankAccountNum = fndInterfaceLine.getAttributes_3();
            String bankSlipNum = fndInterfaceLine.getAttributes_4();
            String comments = fndInterfaceLine.getAttributes_5();
            String paymentMethod = "OTHER";
            //对方账户
            String bpBankAccountName = fndInterfaceLine.getAttributes_7();
            //对方账号
            String bpBankAccountNum = fndInterfaceLine.getAttributes_8();
            //银行名称
            String bpBankName = fndInterfaceLine.getAttributes_9();
            //支行名称
            String bpBankBranchName = fndInterfaceLine.getAttributes_10();
            //支行名称
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:收款时间不能为空", transactionDate);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:收款金额不能为空", transactionAmount);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:收款账户不能为空", bankAccountNum);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:对方账户不能为空", bpBankAccountName);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:对方账号不能为空", bpBankAccountNum);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:银行名称·不能为空", bpBankName);
            Example example = new Example(HlsCusCshBankAccount.class);
            example.createCriteria().andEqualTo("bankAccountNum",bankAccountNum);
            List<HlsCusCshBankAccount> list = hlsCusCshBankAccountMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(list)){
                throw new RuntimeException("账户不是合法账号");
            }
            if (list.size() > 1){
                throw new RuntimeException("一个账户对应对应多条数据");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                transactionDate = transactionDate.replaceAll("/","-");
                cshTransaction.setTransactionDate(simpleDateFormat.parse(transactionDate));
            } catch (ParseException e) {
                throw new RuntimeException("时间格式报错");
            }
            cshTransaction.setTransactionAmount(Double.parseDouble(transactionAmount));
            cshTransaction.setBankAccountId(list.get(0).getBankAccountId());
            cshTransaction.setBankSlipNum(bankSlipNum);
            cshTransaction.setComments(comments);
            cshTransaction.setPaymentMethod(paymentMethod);
            cshTransaction.setBpBankAccountName(bpBankAccountName);
            cshTransaction.setBpBankAccountNum(bpBankAccountNum);
            cshTransaction.setBpBankName(bpBankName);
            cshTransaction.setBpBankBranchName(bpBankBranchName);
            setExtraInfo(cshTransaction,iRequest);
            self().insertSelective(iRequest,cshTransaction);
        }
    }
    public static final String DOCUMENT_CATEGORY = "CSH_TRANSACTION";

    public static final String DOCUMENT_TYPE = "RECEIPT";

    public static final String BUSINESS_TYPE = "RECEIPT";
    private void setExtraInfo(HlsCusCshTransaction cshTransaction,IRequest requestCtx){
        //后台生成编码规则
        Map<String, String> params = new HashMap<>();
        String transactionNum = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
        cshTransaction.setTransactionNum(transactionNum);
        cshTransaction.setTransactionCategory(DOCUMENT_CATEGORY);
        cshTransaction.setTransactionType(DOCUMENT_TYPE);
        cshTransaction.setBusinessType(BUSINESS_TYPE);
        cshTransaction.setPenaltyCalcDate(cshTransaction.getTransactionDate());
        cshTransaction.setCompanyId(requestCtx.getCompanyId());
        cshTransaction.setWriteOffFlag("NOT");
        cshTransaction.setReversedFlag("N");
        cshTransaction.setPostedFlag("N");
        cshTransaction.setCurrencyCode("CNY");
    }

}
