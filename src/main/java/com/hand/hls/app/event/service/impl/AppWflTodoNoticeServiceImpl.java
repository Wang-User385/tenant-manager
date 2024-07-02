package com.hand.hls.app.event.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.TaskActionRequestExt;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.app.event.service.AppWflTodoNoticeService;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.interfacePlatform.utils.InterfacePlatformUtils;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.utils.HttpExecuteResponse;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class AppWflTodoNoticeServiceImpl implements AppWflTodoNoticeService {
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private HlsCusHapInterfaceOutboundService hapInterfaceOutboundService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String appId;
    private static String appSecret;
    private static String todoJumpUrl;
    //获取token
    private static String getAccessTokenUrl;
    private static String accessTokenKey = "accessToken";
    //企业微信发送待办
    private static String sendTodoUrl;
    //云之家待办检查
    private static String checkcreatetodoUrl;
    //企业微信待办置为已办
    private static String dealTodoUrl;
    @Value("${intfPlatform.base}")
    private  String base;
    @Value("${intfPlatform.accessTokenUrl}")
    private  String accessTokenUrl;
    @Value("${intfPlatform.todoSendUrl}")
    private  String todoSendUrl;
    @Autowired
    private InterfacePlatformUtils interfacePlatformUtils;

    private  void init(){
        appId = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("APP_INTERFACE","appId");
        appSecret = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("APP_INTERFACE","appSecret");
        todoJumpUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("APP_INTERFACE","todoJumpUrl");
        getAccessTokenUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("APP_INTERFACE","getAccessTokenUrl");
        sendTodoUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("APP_INTERFACE","sendTodoUrl");
        checkcreatetodoUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("APP_INTERFACE","checkcreatetodoUrl");
        dealTodoUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("APP_INTERFACE","dealTodoUrl");
    }


    @Override
    public void sendTodoNotice(Long sourceId, String openId, JSONObject mobiles){
        init();
        //获取token
        String token = getAccessTokenFromRedis();
        //推送更新工作流
        sendNotice(sourceId,openId,token,mobiles);
    }

    /**
     * 从缓存中获取token
     * @return token
     */
    private String getAccessTokenFromRedis(){
        String token = "";
        Long expire = redisTemplate.getExpire(accessTokenKey);

        if (expire > 0) {
            token = redisTemplate.opsForValue().get(accessTokenKey);
        }else{
            getAccessTokenFromHttp();
            token = redisTemplate.opsForValue().get(accessTokenKey);
        }
        return token;
    }

    /**
     * 从 http 获取token并存入缓存
     */
    private void getAccessTokenFromHttp(){
        String url = base+accessTokenUrl;
        JSONObject requestObj = new JSONObject();
//        requestObj.put("appId", appId);
//        requestObj.put("secret", appSecret);
//        requestObj.put("timestamp", new Date());
//        requestObj.put("scope", "app");
        String bodys = requestObj.toString();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Date startDate=null;
        long start=0L;
        Date endDate=null;
        long end=0L;
        start = System.currentTimeMillis();
        startDate=new Date();
        //发送请求
        HttpExecuteResponse response = HttpClientUtils.doPost(url,bodys, headers);

        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("企业微信获取token接口");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        outbound.setResponseContent(response.getResponseAsString());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setResponseCode(String.valueOf(response.getResponseCode()));//请求code
        outbound.setLineId(null);
        if(response.isSuccess()){
            outbound.setRequestStatus("success");
        }else{
            outbound.setRequestStatus("failure");
        }
        try{
            IRequest iRequest = RequestHelper.getCurrentRequest(true);
            HlsCusHapInterfaceOutbound outboundData= hapInterfaceOutboundService.insert(iRequest,outbound);
        } catch (Exception e) {
            logger.error("-----------插入日志信息出错---------");
            e.printStackTrace();
        }
        //存入缓存
        setAccessTokenRedis(response.getResponseAsString());
    }

    /**
     * 分析接口返回值，提示错误信息
     * @param response
     * @throws HlsCusException
     */
    private void responseAnalysis(HttpExecuteResponse response) throws HlsCusException{
        if(!response.isSuccess()){
            String invoiceJsonStr = response.getResponseAsString();

            JSONObject invoiceJson = null;
            try {
                invoiceJson = JSONObject.parseObject(invoiceJsonStr);
            } catch (Exception e) {
                throw new HlsCusException(invoiceJsonStr);
            }
            if(invoiceJson != null && invoiceJson.getString("success") != "true"){
                throw new HlsCusException(invoiceJson.getString("error"));
            }
        }
    }

    /**
     * 将从http请求获取的token存到redis中
     * @param tokenJsonStr
     */
    private void setAccessTokenRedis(String tokenJsonStr){
        JSONObject tokenJson = JSONObject.parseObject(tokenJsonStr);
        String token = tokenJson.getString("access_token");
        Long expireIn = tokenJson.getLong("expires_in");
        redisTemplate.opsForValue().set(accessTokenKey,token,expireIn, TimeUnit.SECONDS);
        logger.error("-----------存入缓存成功---------");

    }

    /**
     * 向云之家发送待办
     * @param sourceId
     * @param openId
     * @param token
     */
    private void sendNotice(Long sourceId,String openId,String token,JSONObject mobiles){
        String url = base+todoSendUrl+token;
        String bodys = JSON.toJSONString(mobiles);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Date startDate=null;
        long start=0L;
        Date endDate=null;
        long end=0L;
        start = System.currentTimeMillis();
        startDate=new Date();
        //发送请求
        HttpExecuteResponse response;
        response= HttpClientUtils.doPost(url,bodys, headers);

        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("企业微信发送待办接口");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter("");
        outbound.setResponseContent(response.getResponseAsString());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setResponseCode(String.valueOf(response.getResponseCode()));//请求code
        outbound.setLineId(null);
        if(response.isSuccess()){
            outbound.setRequestStatus("success");
        }else{
            outbound.setRequestStatus("failure");
        }
        try{
            IRequest iRequest = RequestHelper.getCurrentRequest(true);
            HlsCusHapInterfaceOutbound outboundData= hapInterfaceOutboundService.insert(iRequest,outbound);
        } catch (Exception e) {
            logger.error("-----------插入日志信息出错---------");
            e.printStackTrace();
        }
    }

    @Override
    public void dealTodoNotice(Long sourceId, String openId){
        init();
        String token = getAccessTokenFromRedis();
        yunzhijiaDealTodoNotice(sourceId,token,openId);
    }

    /**
     * 待办消息是否生成的确认
     * @param sourceId
     * @param token
     * @param openId
     * @return
     */
    private Boolean checkcreatetodo(Long sourceId,String token,String openId){
        String url = checkcreatetodoUrl+token;
        JSONObject requestObj = new JSONObject();
        requestObj.put("sourcetype", appId);
        requestObj.put("sourceitemid", sourceId);
        requestObj.put("openId", openId);
        String bodys = requestObj.toString();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Date startDate=null;
        long start=0L;
        Date endDate=null;
        long end=0L;
        start = System.currentTimeMillis();
        startDate=new Date();
        //发送请求
        HttpExecuteResponse response = HttpClientUtils.doPost(url,bodys, headers);

        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("云之家待办消息是否生成的确认");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        outbound.setResponseContent(response.getResponseAsString());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setResponseCode(String.valueOf(response.getResponseCode()));//请求code
        outbound.setLineId(null);
        if(response.isSuccess()){
            outbound.setRequestStatus("success");
        }else{
            outbound.setRequestStatus("failure");
        }
        try{
            IRequest iRequest = RequestHelper.getCurrentRequest(true);
            HlsCusHapInterfaceOutbound outboundData= hapInterfaceOutboundService.insert(iRequest,outbound);
        } catch (Exception e) {
            logger.error("-----------插入日志信息出错---------");
            e.printStackTrace();
        }
        //返回结果
        return checkcreatetodoResult(response);

    }

    /**
     * 分析接口返回值，提示错误信息
     * @param response
     * @throws HlsCusException
     */
    private Boolean checkcreatetodoResult(HttpExecuteResponse response){
        if(!response.isSuccess()){
            String responseJsonStr = response.getResponseAsString();

            JSONObject responseJson = null;
            try {
                responseJson = JSONObject.parseObject(responseJsonStr);
            } catch (Exception e) {
                logger.error("---------待办消息确认出错-----------");
                logger.error(responseJsonStr);
//                throw new HlsCusException(responseJsonStr);
            }
            if(responseJson != null && responseJson.getString("success") != "true"){
                logger.error("---------待办消息返回false-----------");
                logger.error(responseJsonStr);
//                throw new HlsCusException(responseJson.getString("error"));
            }else{
                JSONObject dataJson = responseJson.getJSONObject("data");
                Long undelCount = dataJson.getLong("undelCount");
                Boolean checkFlag = dataJson.getBoolean("check");
                if(checkFlag && undelCount == 1){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 待办置为已办
     * @param sourceId
     * @param token
     * @param openId
     */
    private void yunzhijiaDealTodoNotice(Long sourceId,String token,String openId){
        String url = dealTodoUrl+token;
        JSONObject requestObj = new JSONObject();
        requestObj.put("sourcetype", appId);
        requestObj.put("sourceitemid", sourceId);

        List<String> openids = new ArrayList<>();
//        openids.add(openId);
        requestObj.put("openids", openids);

        JSONObject actiontype = new JSONObject();
        actiontype.put("deal",1);
        actiontype.put("read",1);
        requestObj.put("actiontype", actiontype);

        String bodys = requestObj.toString();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Date startDate=null;
        long start=0L;
        Date endDate=null;
        long end=0L;
        start = System.currentTimeMillis();
        startDate=new Date();
        //发送请求
        HttpExecuteResponse response = HttpClientUtils.doPost(url,bodys, headers);

        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("云之家待办置为已办");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        outbound.setResponseContent(response.getResponseAsString());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setResponseCode(String.valueOf(response.getResponseCode()));//请求code
        outbound.setLineId(null);
        if(response.isSuccess()){
            outbound.setRequestStatus("success");
        }else{
            outbound.setRequestStatus("failure");
        }
        try{
            IRequest iRequest = RequestHelper.getCurrentRequest(true);
            HlsCusHapInterfaceOutbound outboundData= hapInterfaceOutboundService.insert(iRequest,outbound);
        } catch (Exception e) {
            logger.error("-----------插入日志信息出错---------");
            e.printStackTrace();
        }
    }
}
