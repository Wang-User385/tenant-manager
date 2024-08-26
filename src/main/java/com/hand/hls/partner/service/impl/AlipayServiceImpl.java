package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.dto.AlipayOrderDTO;
import com.hand.hls.partner.mapper.AlipayOrderMapper;
import com.hand.hls.partner.service.IAlipayService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import com.hand.hls.partner.util.AlipayUtils;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class AlipayServiceImpl implements IAlipayService {

    @Autowired
    private IHlsWsRequestsService logService;
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private IYLMessageNoticeService messageNoticeService;
    @Autowired
    private AlipayOrderMapper alipayOrderMapper;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private CshWriteOffService cshWriteOffService;

    @Autowired
    private HlsCusBpMasterBankAccountMapper hlsCusBpMasterBankAccountMapper;

    public String orderApply(String outOrderNo) throws HlsCusException {
        String penetrateId = "";

        JSONObject reqJson = new JSONObject();
        reqJson.put("outOrderNo",outOrderNo);
        //step1: 穿透单创建
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-000-穿透单创建ORDER.APPLY",reqJson);
        String resStr = null;
        String returnStatus = "S";
        String error = "";
        try{
            resStr = AlipayUtils.orderApply(outOrderNo);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
            error = "error:" + e.getMessage();
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析
        if(StringUtils.isEmpty(error)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_submit_response");
            String code = response.getString("code");
            if("10000".equals(code)){
                JSONObject resultObj = response.getJSONObject("result_obj");
                penetrateId = resultObj.getString("penetrateId");
            }else{
                JSONObject returnJson = new JSONObject();
                returnJson.put("code",code);
                returnJson.put("sub_msg",response.getString("sub_msg"));
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else{
            throw new HlsCusException(error);
        }

        return penetrateId;
    }

    public String loanApply(String customerName,String userCertNo,String penetrateId,String channel) throws HlsCusException {
        String extInfo = "";

        JSONObject reqJson = new JSONObject();
        reqJson.put("customerName",customerName);
        reqJson.put("userCertNo",userCertNo);
        reqJson.put("penetrateId",penetrateId);
        reqJson.put("channel",channel);
        //step1: 代扣授权签约
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-001-代扣授权签约申请LOAN.APPLY",reqJson);
        String resStr = null;
        String returnStatus = "S";
        String error = "";
        try{
            resStr = AlipayUtils.loanApply(customerName,userCertNo,penetrateId,channel);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
            error = "error:" + e.getMessage();
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析
        if(StringUtils.isEmpty(error)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_submit_response");
            String code = response.getString("code");
            if("10000".equals(code)){
                JSONObject resultObj = response.getJSONObject("result_obj");
                extInfo = resultObj.getString("extInfo");
            }else{
                JSONObject returnJson = new JSONObject();
                returnJson.put("code",code);
                returnJson.put("sub_msg",response.getString("sub_msg"));
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else{
            throw new HlsCusException(error);
        }
        return extInfo;
    }

    public String loanQuery(String penetrateId) throws HlsCusException {
        String status = "";

        JSONObject reqJson = new JSONObject();
        reqJson.put("penetrateId",penetrateId);
        //step1: 代扣授权签约申请查询
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-002-代扣授权签约申请查询LOAN.QUERY",reqJson);
        String resStr = null;
        String returnStatus = "S";
        String error = "";
        try{
            resStr = AlipayUtils.loanQuery(penetrateId);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
            error = "error:" + e.getMessage();
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析
        if(StringUtils.isEmpty(error)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_query_response");
            String code = response.getString("code");
            if("10000".equals(code)){
                JSONObject resultObj = response.getJSONObject("result_obj");
                status = resultObj.getString("status");
            }else{
                JSONObject returnJson = new JSONObject();
                returnJson.put("code",code);
                returnJson.put("sub_msg",response.getString("sub_msg"));
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else{
            throw new HlsCusException(error);
        }
        return status;
    }

    public void orderCancel(String penetrateId) throws HlsCusException {
        JSONObject reqJson = new JSONObject();
        reqJson.put("penetrateId",penetrateId);
        //step1: 代扣授权签约申请查询
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-006-订单取消ORDER.CANCEL",reqJson);
        String resStr = null;
        String returnStatus = "S";
        String error = "";
        try{
            resStr = AlipayUtils.orderCancel(penetrateId);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
            error = "error:" + e.getMessage();
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析
        if(StringUtils.isEmpty(error)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_submit_response");
            String code = response.getString("code");
            if(!"10000".equals(code)){
                JSONObject returnJson = new JSONObject();
                returnJson.put("code",code);
                returnJson.put("sub_msg",response.getString("sub_msg"));
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else{
            throw new HlsCusException(error);
        }
    }

    public void paymentApply(String penetrateId,String outSeqNo,String amount,String subject) throws HlsCusException {
        JSONObject reqJson = new JSONObject();
        reqJson.put("penetrateId",penetrateId);
        reqJson.put("outSeqNo",outSeqNo);
        reqJson.put("amount",amount);
        reqJson.put("subject",subject);
        //step1: 扣款请求
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-003-扣款请求PAYMENT.APPLY",reqJson);
        String resStr = null;
        String returnStatus = "S";
        String error = "";
        try{
            resStr = AlipayUtils.paymentApply(penetrateId,outSeqNo,amount,subject);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
            error = "error:" + e.getMessage();
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析
        if(StringUtils.isEmpty(error)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_submit_response");
            String code = response.getString("code");
            if(!"10000".equals(code)){
                JSONObject returnJson = new JSONObject();
                returnJson.put("code",code);
                returnJson.put("sub_msg",response.getString("sub_msg"));
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else{
            throw new HlsCusException(error);
        }
    }

    public JSONObject paymentQuery(String outSeqNo, AlipayOrderDTO order) throws HlsCusException {
        String status = "";

        JSONObject reqJson = new JSONObject();
        reqJson.put("outSeqNo",outSeqNo);
        //step1: 扣款查询
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-004-扣款查询PAYMENT.QUERY",reqJson);
        String resStr = null;
        String returnStatus = "S";
        String error = "";
        try{
            resStr = AlipayUtils.paymentQuery(outSeqNo);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
            error = "error:" + e.getMessage();
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);
        JSONObject resultObj = new JSONObject();
        //step2：解析
        if(StringUtils.isEmpty(error)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_query_response");
            resultObj = response;
//            String code = response.getString("code");
//            if("10000".equals(code)){
//                resultObj = response.getJSONObject("result_obj");
//                //status = resultObj.getString("status");
//            }else{
//                JSONObject returnJson = new JSONObject();
//                returnJson.put("code",code);
//                returnJson.put("sub_msg",response.getString("sub_msg"));
//                return returnJson;
//                //throw new HlsCusException(returnJson.toJSONString());
//            }
        }else{
            throw new HlsCusException(error);
        }
        return resultObj;
    }

    public void paymentCancel(String outSeqNo) throws HlsCusException {
        JSONObject reqJson = new JSONObject();
        reqJson.put("outSeqNo",outSeqNo);
        //step1: 支付申请撤销
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-005-支付申请撤销PAYMENT.CANCEL",reqJson);
        String resStr = null;
        String returnStatus = "S";
        String error = "";
        try{
            resStr = AlipayUtils.paymentCancel(outSeqNo);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
            error = "error:" + e.getMessage();
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析
        if(StringUtils.isEmpty(error)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_submit_response");
            String code = response.getString("code");
            if(!"10000".equals(code)){
                JSONObject returnJson = new JSONObject();
                returnJson.put("code",code);
                returnJson.put("sub_msg",response.getString("sub_msg"));
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else{
            throw new HlsCusException(error);
        }
    }

    public void autoWriteOff(AlipayOrderDTO order) throws HlsCusException {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        //step1：插入现金事务表
        HlsCusCshTransaction transaction = new HlsCusCshTransaction();
        String transactionNum = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "CSH_TRANSACTION", "RECEIPT", "RECEIPT", new HashMap<>());
        transaction.setTransactionNum(transactionNum);
        transaction.setTransactionCategory("CSH_TRANSACTION");
        transaction.setTransactionType("RECEIPT");
        transaction.setBusinessType("RECEIPT");
        transaction.setTransactionDate(order.getLastReceivedDate());
        transaction.setPenaltyCalcDate(order.getLastReceivedDate());
        transaction.setCompanyId(iRequest.getCompanyId());
        transaction.setTransactionAmount(order.getAmount()/100.0);
        transaction.setCurrencyCode("CNY");
        transaction.setPaymentMethod("Alipay");
        transaction.setReversedFlag("N");
        transaction.setPostedFlag("Y");
        HlsCusConContractCashflow cashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(order.getCashflowId());
        transaction.setContractId(cashflow.getContractId());
        transaction.setDescription(order.getSubject());
        transaction.setSourceDocCategory("GT_ALIPAY_ORDER");
        transaction.setSourceDocId(order.getOrderId());
        transaction.setWriteOffFlag("NOT");
        transaction.setWriteOffAmount(0D);
        hlsCusCshTransactionMapper.insertSelective(transaction);
        //step2：构造核销记录（租金、罚息）
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowMapper.queryConContractCashflowList(cashflow.getContractId(), cashflow.getTimes());
        List<HlsCusCshWriteOff> writeOffList = new ArrayList<>();
        for (HlsCusConContractCashflow todoCashflow : cashflowList) {
            HlsCusCshWriteOff writeOff = new HlsCusCshWriteOff();
            writeOff.setContractId(todoCashflow.getContractId());
            writeOff.setCshTransactionId(transaction.getTransactionId());
            writeOff.setCashflowId(todoCashflow.getCashflowId());
            writeOff.setWriteOffType("RECEIPT_CREDIT");
            writeOff.setWriteOffDate(new Date());
            writeOff.setReversedFlag("N");
            writeOff.setCfItem(todoCashflow.getCfItem());
            writeOff.setCfType(todoCashflow.getCfType());

            double writeOffAmount = 0D;
            if(todoCashflow.getReceivedAmount() == null){
                writeOffAmount = todoCashflow.getDueAmount();
            }else{
                writeOffAmount = todoCashflow.getDueAmount() - todoCashflow.getReceivedAmount();
            }
            double writeOffInterest = 0D;
            if(todoCashflow.getInterest() != null){
                if(todoCashflow.getReceivedInterest() == null){
                    writeOffInterest = todoCashflow.getInterest();
                }else{
                    writeOffInterest = todoCashflow.getInterest() - todoCashflow.getReceivedInterest();
                }
            }
            double writeOffPrincipal = 0D;
            if(todoCashflow.getPrincipal() != null){
                if(todoCashflow.getReceivedPrincipal() == null){
                    writeOffPrincipal = todoCashflow.getPrincipal();
                }else{
                    writeOffPrincipal = todoCashflow.getPrincipal() - todoCashflow.getReceivedPrincipal();
                }
            }
            writeOff.setCshWriteOffAmount(writeOffAmount);
            writeOff.setWriteOffDueAmount(writeOffAmount);
            writeOff.setWriteOffInterest(writeOffInterest);
            writeOff.setWriteOffPrincipal(writeOffPrincipal);
            writeOff.setTimes(todoCashflow.getTimes());
            writeOff.setWriteOffDocCategory("CON_CONTRACT");

            writeOffList.add(writeOff);
        }
        //step3：核销
        try{
            HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
            cshWriteOffService.writeOff(iRequest, writeOffList, session);
        }catch(Exception e){
            e.printStackTrace();
            throw new HlsCusException(e.getMessage());
        }
    }

    @Override
    public String getPenetrateId(Long projectId) throws HlsCusException {
        HlsCusPrjProject prjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        String penetrateId = prjProject.getPenetrateId();
        if(StringUtils.isEmpty(penetrateId)){
            //系统开关控制是否启用蚂蚁链接口
            String flag = alipayOrderMapper.getMeaningSysCode("SYS_INTERFACE_FLAG","ALIPAY_FLAG");
            if("Y".equals(flag)){
                penetrateId = orderApply(prjProject.getProjectNumber());
            }
            HlsCusPrjProject updateProject = new HlsCusPrjProject();
            updateProject.setProjectId(projectId);
            updateProject.setPenetrateId(penetrateId);
            prjProjectMapper.updateByPrimaryKeySelective(updateProject);
        }
        return penetrateId;
    }


    @Override
    public String sign(Long projectId) throws HlsCusException {
        String extInfo = "";

        HlsCusPrjProject prjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(prjProject.getTenantId());
        String customerName = bpMaster.getBpName();
        String userCertNo = bpMaster.getIdCardNo();
        String penetrateId = prjProject.getPenetrateId();

        //extInfo = loanApply(customerName,userCertNo,penetrateId,"ALIPAYAPP");
        //extInfo = "https://openapi.alipay.com/gateway.do?" + extInfo;//拼接上前缀

        //系统开关控制是否启用蚂蚁链接口
        String alipayStatus = "";
        String flag = alipayOrderMapper.getMeaningSysCode("SYS_INTERFACE_FLAG","ALIPAY_FLAG");
        if("Y".equals(flag)){
            extInfo = loanApply(customerName,userCertNo,penetrateId,"QRCODE");
            alipayStatus = "TO_BE_APPLIED";
        }else{
            extInfo = "*****未启用蚂蚁链接口************";
            alipayStatus = "ACTIVATED";
            //判断用户当前是否已有银行账户信息
            HlsCusBpMasterBankAccount queryBpMasterBankAccount = new HlsCusBpMasterBankAccount();
            queryBpMasterBankAccount.setBpId(prjProject.getTenantId());
            //倒叙获取银行信息，拿最新的一条进行判断即可
            List<HlsCusBpMasterBankAccount> hlsCusBpMasterBankAccounts = hlsCusBpMasterBankAccountMapper.queryCusBpMasterBankByBpId(queryBpMasterBankAccount);
            if (!ObjectUtils.isEmpty(hlsCusBpMasterBankAccounts.get(0))){
                //更新
                HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                hlsCusBpMasterBankAccount.setBankAccountId(hlsCusBpMasterBankAccounts.get(0).getBankAccountId());
                hlsCusBpMasterBankAccount.setBankFullName("支付宝（中国）网络技术有限公司");
                hlsCusBpMasterBankAccount.setBankBranchName("支付宝（中国）网络技术有限公司");
                hlsCusBpMasterBankAccount.setBankSignType("Alipay");
                hlsCusBpMasterBankAccount.setBankAccountNum(userCertNo);
                hlsCusBpMasterBankAccount.setBankAccountName(bpMaster.getBpName());
                hlsCusBpMasterBankAccount.setLastUpdateDate(new Date());
                hlsCusBpMasterBankAccountMapper.updateByPrimaryKeySelective(hlsCusBpMasterBankAccount);
            }else {
                //在用户银行账户中插入一条对应数据
                HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                //银行账号对应承租人身份证号
                hlsCusBpMasterBankAccount.setBankAccountNum(userCertNo);
                hlsCusBpMasterBankAccount.setBpId(bpMaster.getBpId());
                hlsCusBpMasterBankAccount.setBankAccountName(bpMaster.getBpName());
                hlsCusBpMasterBankAccount.setCurrency("CNY");
                hlsCusBpMasterBankAccount.setEnabledFlag("Y");
                hlsCusBpMasterBankAccount.setBankFullName("支付宝（中国）网络技术有限公司");
                hlsCusBpMasterBankAccount.setBankBranchName("支付宝（中国）网络技术有限公司");
                hlsCusBpMasterBankAccount.setCountry("中华人民共和国");
                hlsCusBpMasterBankAccount.setBankSignType("Alipay");
                hlsCusBpMasterBankAccount.setCreationDate(new Date());
                hlsCusBpMasterBankAccount.setLastUpdateDate(new Date());
                hlsCusBpMasterBankAccountMapper.insertSelective(hlsCusBpMasterBankAccount);
            }
        }

        HlsCusPrjProject updateProject = new HlsCusPrjProject();
        updateProject.setProjectId(projectId);
        updateProject.setAlipayStatus(alipayStatus);
        prjProjectMapper.updateByPrimaryKeySelective(updateProject);

        return extInfo;
    }

    @Override
    public void signQuery(Long projectId) throws HlsCusException {
        //TO_BE_APPLIED：待客户端完成申请
        //ACTIVATED：⽣效
        //NOT_SUPPORTED：不⽀持
        //CANCELED 取消
        //FAILED 其他失败情况
        HlsCusPrjProject prjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        String alipayStatus = prjProject.getAlipayStatus();
        if(StringUtils.isEmpty(alipayStatus) || "TO_BE_APPLIED".equals(alipayStatus)){
            //系统开关控制是否启用蚂蚁链接口
            String flag = alipayOrderMapper.getMeaningSysCode("SYS_INTERFACE_FLAG","ALIPAY_FLAG");
            if("Y".equals(flag)){
                String status = loanQuery(prjProject.getPenetrateId());
                HlsCusPrjProject updateProject = new HlsCusPrjProject();
                updateProject.setProjectId(projectId);
                updateProject.setAlipayStatus(status);
                prjProjectMapper.updateByPrimaryKeySelective(updateProject);

                //如果已经代扣签约，则推送消息
                if("ACTIVATED".equals(status)){
                    //判断用户当前是否已有银行账户信息
                    HlsCusBpMasterBankAccount queryBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                    queryBpMasterBankAccount.setBpId(prjProject.getTenantId());
                    //倒叙获取银行信息，拿最新的一条进行判断即可
                    List<HlsCusBpMasterBankAccount> hlsCusBpMasterBankAccounts = hlsCusBpMasterBankAccountMapper.queryCusBpMasterBankByBpId(queryBpMasterBankAccount);
                    if (!ObjectUtils.isEmpty(hlsCusBpMasterBankAccounts.get(0))){
                        //更新
                        HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                        hlsCusBpMasterBankAccount.setBankAccountId(hlsCusBpMasterBankAccounts.get(0).getBankAccountId());
                        hlsCusBpMasterBankAccount.setBankFullName("支付宝（中国）网络技术有限公司");
                        hlsCusBpMasterBankAccount.setBankBranchName("支付宝（中国）网络技术有限公司");
                        hlsCusBpMasterBankAccount.setBankSignType("Alipay");
                        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(prjProject.getTenantId());
                        String userCertNo = bpMaster.getIdCardNo();
                        hlsCusBpMasterBankAccount.setBankAccountNum(userCertNo);
                        hlsCusBpMasterBankAccount.setBankAccountName(bpMaster.getBpName());
                        hlsCusBpMasterBankAccount.setLastUpdateDate(new Date());
                        hlsCusBpMasterBankAccountMapper.updateByPrimaryKeySelective(hlsCusBpMasterBankAccount);
                    }else {
                        //在用户银行账户中插入一条对应数据
                        HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                        //银行账号对应承租人身份证号
                        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(prjProject.getTenantId());
                        String userCertNo = bpMaster.getIdCardNo();
                        hlsCusBpMasterBankAccount.setBankAccountNum(userCertNo);
                        hlsCusBpMasterBankAccount.setBankAccountName(bpMaster.getBpName());
                        hlsCusBpMasterBankAccount.setBpId(bpMaster.getBpId());
                        hlsCusBpMasterBankAccount.setCurrency("CNY");
                        hlsCusBpMasterBankAccount.setEnabledFlag("Y");
                        hlsCusBpMasterBankAccount.setBankFullName("支付宝（中国）网络技术有限公司");
                        hlsCusBpMasterBankAccount.setBankBranchName("支付宝（中国）网络技术有限公司");
                        hlsCusBpMasterBankAccount.setCountry("中华人民共和国");
                        hlsCusBpMasterBankAccount.setBankSignType("Alipay");
                        hlsCusBpMasterBankAccount.setCreationDate(new Date());
                        hlsCusBpMasterBankAccount.setLastUpdateDate(new Date());
                        hlsCusBpMasterBankAccountMapper.insertSelective(hlsCusBpMasterBankAccount);
                    }
                    messageNoticeService.withholdContractResult(projectId,RequestHelper.getCurrentRequest());
                }
            }
        }
    }

    @Override
    public void signCancel(Long projectId) throws HlsCusException {
        //TO_BE_APPLIED：待客户端完成申请
        //ACTIVATED：⽣效
        //NOT_SUPPORTED：不⽀持
        //CANCELED 取消
        //FAILED 其他失败情况
        HlsCusPrjProject prjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        String alipayStatus = prjProject.getAlipayStatus();
        if("ACTIVATED".equals(alipayStatus)){
            //系统开关控制是否启用蚂蚁链接口
            String flag = alipayOrderMapper.getMeaningSysCode("SYS_INTERFACE_FLAG","ALIPAY_FLAG");
            if("Y".equals(flag)){
                orderCancel(prjProject.getPenetrateId());
                HlsCusPrjProject updateProject = new HlsCusPrjProject();
                updateProject.setProjectId(projectId);
                updateProject.setAlipayStatus("CANCELED");
                prjProjectMapper.updateByPrimaryKeySelective(updateProject);
                //代扣解约成功，推送消息
                messageNoticeService.withholdContractResult(projectId,RequestHelper.getCurrentRequest());
            }
        }
    }

    @Override
    public void withhold(Long orderId) throws HlsCusException {
        AlipayOrderDTO order = alipayOrderMapper.selectOrderForWithhold(orderId);
        String penetrateId = order.getPenetrateId();
        String outSeqNo = order.getOutSeqNo();
        String amount = order.getAmount().toString();
        String subject = order.getSubject();
        //系统开关控制是否启用蚂蚁链接口
        String flag = alipayOrderMapper.getMeaningSysCode("SYS_INTERFACE_FLAG","ALIPAY_FLAG");
        if("Y".equals(flag)){
            paymentApply(penetrateId,outSeqNo,amount,subject);
        }
        order.setStatus("SENDING");
        alipayOrderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public void withholdQuery(Long orderId) throws HlsCusException {
        //APPLIED 【中间态】待⽀付。交易创建，等待买家付款
        //CLOSED  【终态，失败】交易关闭。未付款交易超时关闭，或⽀付完成后全额退款
        //SUCCESS 【中间态】交易成功。交易⽀付成功，可退款
        //FINISHED 【终态，成功】交易已结算。交易结束，不可退款
        //FAILED  【终态，失败】交易失败。
        AlipayOrderDTO order = alipayOrderMapper.selectByPrimaryKey(orderId);
        String status = "";
        Date finishTime = null;
        //系统开关控制是否启用蚂蚁链接口
        String flag = alipayOrderMapper.getMeaningSysCode("SYS_INTERFACE_FLAG","ALIPAY_FLAG");
        if("Y".equals(flag)){
            JSONObject resultObj = paymentQuery(order.getOutSeqNo(), order);
            //判断是否成功
            if ("10000".equals(resultObj.get("code"))){
                JSONObject bodyInfo = resultObj.getJSONObject("result_obj");
                status = bodyInfo.getString("status");
                finishTime = bodyInfo.getDate("finishTime");

                order.setStatus(status);
                order.setLastReceivedDate(finishTime);
            }else {
                order.setMessage(resultObj.getString("sub_msg"));
            }
        }else{
            status = "SUCCESS";
            finishTime = new Date();

            order.setStatus(status);
            order.setLastReceivedDate(finishTime);
        }

        if("SUCCESS".equals(status)){
            autoWriteOff(order);
        }
        alipayOrderMapper.updateOrderStatusAndTimeByOrder(order);
    }

    @Override
    public void withholdCancel(Long orderId) throws HlsCusException {
        AlipayOrderDTO order = alipayOrderMapper.selectByPrimaryKey(orderId);
        paymentCancel(order.getOutSeqNo());
    }

    private HlsWsRequests insertLogs(String functionName, JSONObject reqJson) {
        //存储请求报文日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        hlsWsRequests.setFunctionName(functionName);
        hlsWsRequests.setRequestJson(JSONObject.toJSONString(reqJson));
        hlsWsRequests = logService.interfaceSave(hlsWsRequests, RequestHelper.getCurrentRequest());
        return hlsWsRequests;
    }

    private void updateLogs(HlsWsRequests hlsWsRequests,String resStr,String returnStatus){
        //存储返回报文日志
        hlsWsRequests.setResponseJson(resStr);
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,RequestHelper.getCurrentRequest());
    }

}
