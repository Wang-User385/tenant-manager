package com.hand.hls.common.utils;


import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;


public class DoPostSoap {

    private static final Integer SOCKETTIMEOUT = 30000;// 请求超时时间
    private static final Integer CONNECTTIMEOUT = 30000;// 传输超时时间

    public HlsCusHapInterfaceOutbound doPostSoap(String soapXml, String postUrl)throws IOException {
        String retStr = "";
        String str = "";
        HlsCusHapInterfaceOutbound hapInterfaceOutbound=new HlsCusHapInterfaceOutbound();
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKETTIMEOUT).setConnectTimeout(CONNECTTIMEOUT).build();
        CloseableHttpResponse response=null;
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type","text/xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", "");
            //httpPost.setHeader("Username", "admin");
            //httpPost.setHeader("Password", "admin");
            StringEntity data = new StringEntity(soapXml, Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            response = closeableHttpClient.execute(httpPost);
            response.getStatusLine();//获取响应状态码

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                retStr = EntityUtils.toString(httpEntity, "UTF-8");

            }
            hapInterfaceOutbound.setResponseCode(response.getStatusLine().toString());
            hapInterfaceOutbound.setResponseContent(retStr);
            // 释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
          //  e.printStackTrace();
            throw new IOException(e);
        }

        return hapInterfaceOutbound;
    }
}
