package com.hand.hls.csh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.service.HlsBpMasterBankAccountService;
import com.hand.hls.csh.dto.CshBaseDto;
import com.hand.hls.csh.dto.CshTransactionRefundLn;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.service.*;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.service.HlsBpMasterService;
import com.hand.hls.utils.DateUtils;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Action;
import java.beans.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/24
 * @description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class CshTransactionRefundServiceImpl extends BaseServiceImpl<HlsCusCshTransactionRefund> implements CshTransactionRefundService {
    @Autowired
    HlsCusCshTransactionRefundMapper cshTransactionRefundMapper;
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";

    private static final String WORK_FLOW_TYPE_OR_NAME_REFUND = "CSH_REFUND_APPLY_WORK_FLOW";
    private static final String WORK_FLOW_DEMO_REFUND = "REFUND";




    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;

    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService;

    @Autowired
    private CshTransactionRefundLnService cshTransactionRefundLnService;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;

    @Autowired
    private CshTransactionService cshTransactionService;

    @Autowired
    private HlsCusBpMasterService cusBpMasterService;

    @Autowired
    private HlsBpMasterBankAccountService bpMasterBankAccountService;

    /**
     * 二期功能：退款申请创建tab页查询
     *
     * @param request
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusCshTransactionRefund> createRefundQuery(IRequest request, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return cshTransactionRefundMapper.createRefundQuery(transactionRefund);
    }

    /**
     * 二期功能：退款申请维护tab页查询
     *
     * @param iRequest
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusCshTransactionRefund> modifyHomeQuery(IRequest iRequest, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return cshTransactionRefundMapper.modifyHomeQuery(transactionRefund);
    }

    /**
     * 二期功能：退款明细页面行信息查询
     *
     * @param iRequest
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusCshTransactionRefund> refundInfoLnQuery(IRequest iRequest, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return cshTransactionRefundMapper.refundInfoLnQuery(transactionRefund);
    }

    /**
     * 二期功能：退款申请创建页面保存按钮逻辑
     *
     * @param requestCtx
     * @param cshBaseDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ResponseData createAndUpdate(IRequest requestCtx, CshBaseDto cshBaseDto) throws Exception {
        ResponseData responseData = new ResponseData(false);
        if(null == cshBaseDto){
            responseData.setMessage(HlsConstantUtil.TransactionRefund.PARAMETER_IS_NULL);
            return responseData;
        }
        HlsCusCshTransactionRefund transactionRefund = cshBaseDto.getTransactionRefund();
        List<CshTransactionRefundLn> transactionRefundLineList = cshBaseDto.getTransactionRefundLnList();
        if(null == transactionRefund || CollectionUtils.isEmpty(transactionRefundLineList)){
            responseData.setMessage(HlsConstantUtil.TransactionRefund.PARAMETER_IS_NULL);
            return responseData;
        }

        if(null == transactionRefund.getRefundId()){
            String refundNumber = cshPaymentReqHdService.getCodeValue(requestCtx);
            transactionRefund.setRefundNumber(refundNumber);
            transactionRefund.setRefundStatus(HlsConstantUtil.WorkFlowStatus.NEW);
            transactionRefund.setPaymentRefundStatus(HlsConstantUtil.SlipStatus.NEW);
            transactionRefund.setAuthorityRuleString(cshPaymentReqLnService.getAuthorityRuleString(requestCtx));
            transactionRefund = self().insertSelective(requestCtx, transactionRefund);
        }else{
            transactionRefund = self().updateByPrimaryKeySelective(requestCtx, transactionRefund);
        }
        for (CshTransactionRefundLn transactionRefundLine : transactionRefundLineList) {
            transactionRefundLine.setRefundId(transactionRefund.getRefundId());
            transactionRefundLine.setPaymentMethod(transactionRefund.getPaymentMethod());
            transactionRefundLine.setSourceTransactionId(transactionRefundLine.getTransactionId());
            if(null == transactionRefundLine.getLnId()){
                transactionRefundLine.set__status(DTOStatus.ADD);
            }else{
                transactionRefundLine.set__status(DTOStatus.UPDATE);
            }
        }
        cshTransactionRefundLnService.batchUpdate(requestCtx, transactionRefundLineList);

        List<HlsCusCshTransactionRefund> list = new ArrayList<>(1);
        list.add(transactionRefund);
        responseData.setRows(list);
        responseData.setSuccess(true);
        return responseData;
    }

    /**
     * 二期功能：退款申请创建/维护页面  提交按钮  逻辑
     *
     * @param iRequest
     * @param refundId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ResponseData refundSubmit(IRequest iRequest, Long refundId) throws Exception {
        ResponseData responseData = new ResponseData(false);
        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund.setRefundId(refundId);
        transactionRefund = self().selectByPrimaryKey(iRequest, transactionRefund);
        List<HlsCusCshTransactionRefund> transactionRefundList = self().refundInfoLnQuery(iRequest, transactionRefund, 1, 0);

        //收款管理的预收款设置冻结金额
        for (HlsCusCshTransactionRefund refund : transactionRefundList) {
            if(HlsConstantUtil.TransactionType.ADVANCE_RECEIPT.equals(refund.getTransactionType()) ||
                    HlsConstantUtil.TransactionType.DEPOSIT.equals(refund.getTransactionType())) {
                cshTransactionService.blockAmount(iRequest, refund.getTransactionId(), refund.getRefundAmount());
            }
        }

        List<HlsCusCshTransactionRefund> wflList = new ArrayList<>(1);
        wflList.add(transactionRefund);
        //设置工作流参数
        Map<String, Object> map = new HashMap<>();
        map.put(HlsConstantUtil.WorkFlowParameterKey.WORK_FLOW_TYPE, WORK_FLOW_TYPE_OR_NAME_REFUND);
        map.put(IActivitiCommonService.WORK_FLOW_NAME, WORK_FLOW_TYPE_OR_NAME_REFUND);
        map.put(IActivitiCommonService.DEMO_NAME, WORK_FLOW_DEMO_REFUND);
        map.put(IActivitiCommonService.BUSINESS_KEY, transactionRefund.getRefundId());
        map.put("documentCategory", "CSH_TRANSACTION_REFUND");
        map.put("documentType", "CSH_TRANSACTION_REFUND");
        map.put("documentId", transactionRefund.getRefundId());
        map.put("refundAmount", transactionRefund.getRefundAmount());

        //付款对象信息
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setBpId(transactionRefund.getBpId());
        hlsBpMaster = cusBpMasterService.selectByPrimaryKey(iRequest, hlsBpMaster);

        //付款对象银行信息
        HlsCusBpMasterBankAccount bpMasterBankAccount = new HlsCusBpMasterBankAccount();
        bpMasterBankAccount.setBankAccountId(transactionRefund.getBpBankAccountId());
        bpMasterBankAccount = bpMasterBankAccountService.selectByPrimaryKey(iRequest, bpMasterBankAccount);

        map.put("documentName", "退款对象："+hlsBpMaster.getBpName()+"[银行:"+bpMasterBankAccount.getBankFullName()+"][账号:"+bpMasterBankAccount.getBankAccountNum()+"]");
        map.put("documentNumber", transactionRefund.getRefundNumber());
        activitiStartService.start(iRequest, wflList, map);

        updateRefundStatus(iRequest, transactionRefund.getRefundId(), HlsConstantUtil.WorkFlowStatus.APPROVING);

        responseData.setSuccess(true);
        responseData.setRows(wflList);
        return responseData;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRefundStatus(IRequest iRequest, long refundId, String refundStatus) {
        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund.setRefundId(refundId);
        transactionRefund.setRefundStatus(refundStatus);
        self().updateByPrimaryKeySelective(iRequest, transactionRefund);
    }

    /**
     * 二期功能：待支付清单-退款申请 首页查询
     *
     * @param iRequest
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusCshTransactionRefund> refundPayHome(IRequest iRequest, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return cshTransactionRefundMapper.refundPayHome(transactionRefund);
    }

    @Override
    public List<HlsCusCshTransactionRefund> cshTransactionRefundQuery(HlsCusCshTransactionRefund hlsCusCshTransactionRefund, int page, int pagesize,String sortName,String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(page,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        return cshTransactionRefundMapper.cshTransactionRefundQuery(hlsCusCshTransactionRefund);
    }

    @Override
    public List<HlsCusCshTransactionRefund> cshPaymentTransactionRefundQuery(HlsCusCshTransactionRefund hlsCusCshTransactionRefund, int page, int pagesize,String sortName,String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(page,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        return cshTransactionRefundMapper.cshPaymentTransactionRefundQuery(hlsCusCshTransactionRefund);
    }
    @Override
    /**
     * @Discription:收款退款申请
     * @param: [requestCtx, hlsCusCshTransactionList]
     * @return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransaction>
     */
    public void refundCshTransactionSubmit(IRequest requestCtx, HttpSession session, List<HlsCusCshTransactionRefund> hlsCusCshTransactionRefund) throws BeyondAmountLimitException, ResMessageException {
        HlsCusCshTransactionRefund cusCshTransactionRefund = cshTransactionRefundMapper.selectByPrimaryKey(hlsCusCshTransactionRefund.get(0).getRefundId());
        if (APPROVED.equals(cusCshTransactionRefund.getPaymentRefundStatus()) || APPROVING.equals(cusCshTransactionRefund.getPaymentRefundStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        cusCshTransactionRefund.setHandlingDepartment(hlsCusCshTransactionRefund.get(0).getHandlingDepartment());
        cusCshTransactionRefund.setHandlingDepartmentId(hlsCusCshTransactionRefund.get(0).getHandlingDepartmentId());
        cusCshTransactionRefund.setFinancialAgent(hlsCusCshTransactionRefund.get(0).getFinancialAgent());
        cusCshTransactionRefund.setFinancialAgentId(hlsCusCshTransactionRefund.get(0).getFinancialAgentId());
        cusCshTransactionRefund.setRefundId(cusCshTransactionRefund.getRefundId());

        cusCshTransactionRefund.setPaymentDescription(hlsCusCshTransactionRefund.get(0).getPaymentDescription());
        cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx,cusCshTransactionRefund);
        databaseLockProvider.lock(cusCshTransactionRefund);
        //提交工作流
        Map<String, Object> params = new HashMap<String, Object>();
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(cusCshTransactionRefund));
        List<HlsCusCshTransactionRefund> cs = new ArrayList<>();
        cs.add(cusCshTransactionRefund);
        params.put("cshTransactionRefund", jsonObject.toString());
        params.put("refundId", cusCshTransactionRefund.getRefundId());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "CSH_TRANSACTION_REFUND_PAY_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "CSH_PAYMENT_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, cusCshTransactionRefund.getRefundId());
        params.put("documentCategory", "CSH_PAYMENT_WFL");
        params.put("documentType", "CSH_TRANSACTION_REFUND_PAY_WFL");
        params.put("workFlowType", "CSH_TRANSACTION_REFUND_PAY_WFL");
        params.put("documentNumber", cusCshTransactionRefund.getRefundNumber());
        params.put("documentName", cusCshTransactionRefund.getRefundNumber() + "的收款退款支付申请");
        params.put("companyId", requestCtx.getCompanyId());
        params.put("unitId", session.getAttribute("unitId"));
        params.put("documentNumber", cusCshTransactionRefund.getRefundNumber());
        params.put("companySpv", hlsCusCshTransactionRefund.get(0).getCompanySpv());
        params.put("companySpvN", hlsCusCshTransactionRefund.get(0).getCompanySpvN());
        params.put("LOAN_TOTAL_AMOUNT", hlsCusCshTransactionRefund.get(0).getRefundAmount());

        activitiStartService.start(requestCtx, cs, params);
        }
    @Override
    public List<HlsCusCshTransactionRefund> saveLn(IRequest iRequest, List<HlsCusCshTransactionRefund> list) throws ResMessageException {

        List<HlsCusCshTransactionRefund> refundList = self().batchUpdate(iRequest, list);
        return refundList;
    }
}
