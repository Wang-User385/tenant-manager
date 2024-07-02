package com.hand.hls.ws.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hls.ws.dto.HlsSysUserData;
import com.hand.hls.ws.service.SoapSysUserQueryService;
import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface ="com.hand.hls.ws.service.SoapSysUserQueryService",
        serviceName = "SoapSysUserQueryService")

public class SoapSysUserQueryImpl implements SoapSysUserQueryService {


    public List<HlsSysUserData> querySysUser(String ar){
        String strURL="";
        String params="";
        String res="";
//        res= post(strURL,params);
//        System.out.println(res);
//        JSONObject jsonObject =  JSON.parseObject(res);
//
//        String access_token= (String) jsonObject.get("access_token");
//
//        System.out.println(access_token);
      HlsSysUserData hlsSysUserData=new HlsSysUserData();
        List<HlsSysUserData> hlsSysUserDataList=new ArrayList<>();
        hlsSysUserData.setDescription("张三");
        hlsSysUserData.setUserName("zhangsan");
        hlsSysUserDataList.add(hlsSysUserData);
        HlsSysUserData hlsSysUserData2=new HlsSysUserData();
        hlsSysUserData2.setDescription("赵四");
        hlsSysUserData2.setUserName("zhaosi");
        hlsSysUserDataList.add(hlsSysUserData2);
        return hlsSysUserDataList;
    }


    public static String post(String strURL, String params) {
        System.out.println(strURL);
        System.out.println(params);
        BufferedReader reader = null;
        try {
            strURL=  "http://localhost:8666/leaf_jczl/oauth/token?client_id=12&client_secret=ea36970a-c2e6-4d4c-bbfd-a9858316370e&grant_type=password&username=admin&password=admin";
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            //一定要用BufferedReader 来接收响应， 使用字节来接收响应的方法是接收不到内容的
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(params);
            //System.out.println(params);
            out.flush();
            out.close();
            // 读取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String res = "";
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            reader.close();

            System.out.println(res);
            return res;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "error"; // 自定义错误信息
    }


}