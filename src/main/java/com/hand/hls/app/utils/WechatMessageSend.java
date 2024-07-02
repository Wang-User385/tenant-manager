package com.hand.hls.app.utils;




import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WechatMessageSend {
    private static final Logger logger = LoggerFactory.getLogger(WechatMessageSend.class);

    public static JSONObject send(String corpid, String corpsecret, String message) {
        //微信token
        JSONObject tokenObject = getToken(corpid, corpsecret);
        String access_token = (String) tokenObject.get("access_token");

        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + access_token;
        String params = message;
        String result = post(url, params);
        logger.info(result);
        return JSONObject.parseObject(result);
    }


    public static JSONObject updateCard(String corpid, String corpsecret, String message) {

        //微信token
        JSONObject tokenObject = getToken(corpid, corpsecret);
        String access_token = (String) tokenObject.get("access_token");

        //推送微信消息
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/update_taskcard?access_token=" + access_token;
        String params = message;
        String result = post(url, params);
        logger.info(result);
        return JSONObject.parseObject(result);

    }



    public static String post(String url, String params) {
        String resultData = "";
        String charset = "UTF-8";
        try {
            URL myURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            if (!params.isEmpty()) {
                connection.setDoOutput(true);
            }
            connection.setRequestProperty("Content-Type", "application/json");
            charset = "UTF-8";
            connection.connect();
            if (!params.isEmpty()) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params.getBytes(charset));
                outputStream.flush();
                outputStream.close();
            }
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : " + connection.getResponseCode());
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            reader.close();
            connection.disconnect();
            resultData = results.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return resultData;
    }

    public static String get(String url) {
        String resultData = "";
        try {
            URL restServiceURL = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException(
                        "HTTP GET Request Failed with Error code : " + httpURLConnection.getResponseCode());
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            reader.close();
            resultData = results.toString();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return resultData;
    }

    public static JSONObject getToken(String corpid, String corpsecret) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;
        return JSONObject.parseObject(get(url));
    }

}

