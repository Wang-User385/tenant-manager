package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.partner.service.IAlipayService;
import com.hand.hls.partner.util.AlipayUtils;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class AlipayServiceImpl implements IAlipayService {

    @Autowired
    private IHlsWsRequestsService logService;
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    public String orderApply(String outOrderNo){
        String penetrateId = "";

        JSONObject reqJson = new JSONObject();
        reqJson.put("outOrderNo",outOrderNo);
        //step1: 穿透单创建
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-穿透单创建ORDER.APPLY",reqJson);
        String resStr = null;
        String returnStatus = "S";
        try{
            resStr = AlipayUtils.orderApply(outOrderNo);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析penetrateId
        if(StringUtils.isNotEmpty(resStr)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_submit_response");
            String code = response.getString("code");
            if("10000".equals(code)){
                JSONObject resultObj = response.getJSONObject("result_obj");
                penetrateId = resultObj.getString("penetrateId");
            }
        }

        return penetrateId;
    }

    public String loanApply(String customerName,String userCertNo,String penetrateId,String channel){
        String extInfo = "";

        JSONObject reqJson = new JSONObject();
        reqJson.put("customerName",customerName);
        reqJson.put("userCertNo",userCertNo);
        reqJson.put("penetrateId",penetrateId);
        reqJson.put("channel",channel);
        //step1: 代扣授权签约
        HlsWsRequests hlsWsRequests = insertLogs("GT-MY-代扣授权签约LOAN.APPLY",reqJson);
        String resStr = null;
        String returnStatus = "S";
        try{
            resStr = AlipayUtils.loanApply(customerName,userCertNo,penetrateId,channel);
        }catch(Exception e){
            e.printStackTrace();
            returnStatus = "E";
        }
        updateLogs(hlsWsRequests,resStr,returnStatus);

        //step2：解析extInfo
        if(StringUtils.isNotEmpty(resStr)){
            JSONObject resJson = JSONObject.parseObject(resStr);
            JSONObject response = resJson.getJSONObject("anttech_blockchain_defin_assetmanage_penetrate_submit_response");
            String code = response.getString("code");
            if("10000".equals(code)){
                JSONObject resultObj = response.getJSONObject("result_obj");
                extInfo = resultObj.getString("extInfo");
            }
        }
        return extInfo;
    }

    @Override
    public String getPenetrateId(Long projectId){
        HlsCusPrjProject prjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        String penetrateId = prjProject.getPenetrateId();
        if(StringUtils.isEmpty(penetrateId)){
            penetrateId = orderApply(prjProject.getProjectNumber());
            if(StringUtils.isNotEmpty(penetrateId)){
                HlsCusPrjProject updateProject = new HlsCusPrjProject();
                updateProject.setProjectId(projectId);
                updateProject.setPenetrateId(penetrateId);
                prjProjectMapper.updateByPrimaryKeySelective(updateProject);
            }
        }
        return penetrateId;
    }


    @Override
    public String sign(Long projectId){
        String extInfo = "";

        HlsCusPrjProject prjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(prjProject.getTenantId());
        String customerName = bpMaster.getBpName();
        String userCertNo = bpMaster.getIdCardNo();
        String penetrateId = prjProject.getPenetrateId();

        extInfo = loanApply(customerName,userCertNo,penetrateId,"ALIPAYAPP");

        return extInfo;
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
