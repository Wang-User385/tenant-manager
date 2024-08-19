package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
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

import java.text.SimpleDateFormat;
import java.util.Date;


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
            String code = response.getString("code");
            if("10000".equals(code)){
                resultObj = response.getJSONObject("result_obj");
                //status = resultObj.getString("status");
            }else{
                JSONObject returnJson = new JSONObject();
                returnJson.put("code",code);
                returnJson.put("sub_msg",response.getString("sub_msg"));
                //将错误提示更新到中间表中
                order.setMessage(response.getString("sub_msg"));
                alipayOrderMapper.updateByPrimaryKeySelective(order);
                throw new HlsCusException(returnJson.toJSONString());
            }
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
            status = resultObj.getString("status");
            finishTime = resultObj.getDate("finishTime");
        }else{
            status = "SUCCESS";
            finishTime = new Date();
        }
        order.setStatus(status);
        order.setLastReceivedDate(finishTime);
        alipayOrderMapper.updateByPrimaryKeySelective(order);
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
