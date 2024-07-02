package com.hand.hls.interfacePlatform.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.utils.HttpExecuteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Component("interfacePlatformUtils")
//@Transactional
//加事务注解如果接口日志insert失败，会导致接口调用无法正常进行
public class InterfacePlatformUtils {
    @Autowired
    private HlsCusHapInterfaceOutboundService hapInterfaceOutboundService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String accessTokenKey = "accessToken";
    private static final int MAX_RESPONSE_LEN = 3900;

    //访问接口
    @Value("${intfPlatform.base}")
    private  String base;
    @Value("${intfPlatform.accessTokenUrl}")
    private  String accessTokenUrl;


    /**
     * 从redis中获取token，不存在则重新访问获取
     * @return
     */
    public String getAccessTokenFromRedis(){
        String token = "";
        Long expire = redisTemplate.getExpire(accessTokenKey);

        if (expire > 0) {
            token = redisTemplate.opsForValue().get(accessTokenKey);
        }else{
            getAccessTokenFromHttp(base+accessTokenUrl);
            token = redisTemplate.opsForValue().get(accessTokenKey);
        }
        return token;
    }

    /**
     * 访问权限url获取token
     */
    private void getAccessTokenFromHttp(String tokenUrl){
        JSONObject requestObj = new JSONObject();
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
        HttpExecuteResponse response = HttpClientUtils.doPost(tokenUrl,bodys, headers);

        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("获取token接口");
        outbound.setInterfaceUrl(tokenUrl);
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
     * 请求
     * @param requestJson
     * @param intfUrl
     * @return
     */
    public JSONObject getInterfaceRequest(JSONObject requestJson,String intfUrl){
        String token = getAccessTokenFromRedis();
        String url = base+intfUrl+token;
        String bodys = JSON.toJSONString(requestJson);
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
        String responseString = response.getResponseAsString();
        JSONObject responseJsonObject = JSON.parseObject(responseString);
        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("移动端附件信息传递");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        outbound.setResponseContent(responseString);
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
            return responseJsonObject;
        } catch (Exception e) {
            logger.error("-----------插入日志信息出错---------");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 请求
     * @param requestJson
     * @param intfUrl
     * @return
     */
    public JSONObject getInterfaceRequest(JSONObject requestJson,String intfUrl,String itfName){
        String token = getAccessTokenFromRedis();
        String url = base+intfUrl+token;
        String bodys = JSON.toJSONString(requestJson);
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
        if("移动端审批".equals(itfName)){
            response= HttpClientUtils.doPost(url,bodys, headers);
            bodys="";
        }else{
            response= HttpClientUtils.doPost(url,bodys, headers);
        }

        String responseString = response.getResponseAsString();
        JSONObject responseJsonObject = JSON.parseObject(responseString);
        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(itfName);
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        if(responseString.length()<=MAX_RESPONSE_LEN){
            outbound.setResponseContent(responseString);
        }
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
        return responseJsonObject;
    }
}
