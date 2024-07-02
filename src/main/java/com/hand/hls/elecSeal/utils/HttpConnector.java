//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;




import cfca.paperless.client.connector.HttpClient;
import cfca.paperless.client.util.CommonUtil;

import com.hand.hls.interfacePlatform.utils.InterfacePlatformUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLException;
@Component
public class HttpConnector {
    public static final Logger logger = LoggerFactory.getLogger(HttpConnector.class);
    public String JKS_PATH = "";
    public String JKS_PWD = "";
    public String url ="/hitf/v2p/rest/invoke/R0RIQ0c6Q0ZDQS5TSUdOQVRVUkU6c2VhbFBkZg==?access_token=";
    public String requestMethod = "POST";
    public int connectTimeout = 3000;
    public int readTimeout = 10000;
    public boolean isSSL = false;
    public String sslProtocol = "TLSv1.2";
    public String keyStorePath;
    public String keyStorePassword;
    public String trustStorePath;
    public String trustStorePassword;
    private HttpClient httpClient;

    @Autowired
    private InterfacePlatformUtils interfacePlatformUtils;
    @Autowired
    private FormDataConnectorUtils formDataConnectorUtils;
    @Value("${file.upload.dir}")
    private String uploadFileAddr;

    public HttpConnector(){

    }

    public HttpConnector(String uri, String httpMethod, int connectTimeout, int readTimeout) {
        this.keyStorePath = this.JKS_PATH;
        this.keyStorePassword = this.JKS_PWD;
        this.trustStorePath = this.JKS_PATH;
        this.trustStorePassword = this.JKS_PWD;
        this.url = uri;
        this.requestMethod = httpMethod;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public void setSSLConfig(boolean isSSL, String sslProtocol, String keyStorePath, String keyStorePassword, String trustStorePath, String trustStorePassword) {
        this.isSSL = isSSL;
        this.sslProtocol = sslProtocol;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePassword = trustStorePassword;
        this.trustStorePath = trustStorePath;
    }

    public void init() throws Exception {
        this.httpClient = new HttpClient();
        this.httpClient.config.connectTimeout = this.connectTimeout;
        this.httpClient.config.readTimeout = this.readTimeout;
        this.httpClient.httpConfig.userAgent = "Paperless Client";
        this.httpClient.httpConfig.contentType = "application/json";
        this.httpClient.httpConfig.accept = "application/json";
        this.httpClient.sslConfig.sslProtocol = this.sslProtocol;

        try {
            if (this.isSSL) {
                if (CommonUtil.isEmpty(this.trustStorePath)) {
                    throw new Exception("trustStorePath is empty");
                }

                if (CommonUtil.isEmpty(this.trustStorePassword)) {
                    throw new Exception("trustStorePassword is empty");
                }

                if (CommonUtil.isNotEmpty(this.keyStorePath) && CommonUtil.isEmpty(this.keyStorePassword)) {
                    throw new Exception("keyStorePath is not empty, but keyStorePassword is empty");
                }

                if (CommonUtil.isEmpty(this.keyStorePath)) {
                    this.httpClient.initSSL((String)null, (String)null, this.trustStorePath, this.trustStorePassword);
                } else {
                    this.httpClient.initSSL(this.keyStorePath, this.keyStorePassword, this.trustStorePath, this.trustStorePassword);
                }
            }

        } catch (Exception var2) {
            throw new Exception("HttpClient init error", var2);
        }
    }

    public byte[] process(Map<String, String> requestHttpHeadersMap, byte[] requestHttpBodyData, Map<String, String> responseHttpHeadersMap,String url) throws Exception {
        //获取token
        String accessToken = interfacePlatformUtils.getAccessTokenFromRedis();
        logger.info("获取token成功！");
        // filePathMap 保存文件类型的参数名和所需要的数据
        String paramName = "file";
        File file = bytesToFile(requestHttpBodyData, uploadFileAddr +"/", paramName);
        logger.info("字节数组写入文件成功！");
        //headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data");
        headers.put("Authorization", "Bearer "+accessToken);
        logger.info("headers:"+headers);
        byte[] respContent= formDataConnectorUtils.doPostFile(url, headers, file, requestHttpHeadersMap,responseHttpHeadersMap);
        logger.info("respContent:"+respContent);
        return respContent;
    }
    /**
     * 将Byte数组转换成文件
     * @param bytes byte数组
     * @param filePath 文件路径  如 D:\\Users\\Downloads\\
     * @param fileName  文件名
     */
    public  File bytesToFile(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {

            file = new File(filePath + fileName);
            if (!file.getParentFile().exists()){
                //文件夹不存在 生成
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("字节数组写入文件失败");
            return  null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
