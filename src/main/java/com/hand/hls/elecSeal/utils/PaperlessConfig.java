package com.hand.hls.elecSeal.utils;

import java.io.File;

/**
 * @Description 测试用例的属性配置信息
 * @Author zf
 * @Date 2016-5-13
 * @CodeReviewer ljc
 */
public class PaperlessConfig {

    /* 无纸化可信签名系统的host  */
//    public static String host = "10.1.24.38";
    
    /* 无纸化可信签名系统的port  */
//    public static String port = "8183";

    public static String sslProtocol = "SSL";
    public static String keyStorePath = "./TestData/cert/client.jks";
    public static String keyStorePassword = "11111111";
    public static String trustStorePath = "./TestData/cert/client.jks";
    public static String trustStorePassword = "11111111";
 
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    public static final String configPath = "./config";
    
    public static final String baseFontPath = configPath +File.separator + "simsun-all.ttf";
    
    public static final String templateFilePath =  configPath +File.separator + "tempFiles";
    
    public static final String trusterCertPath =  configPath +File.separator + "trustCert";
     
    public static final String crlFilePath = configPath +File.separator + "crl";
    
    
    public final static String organizationCode = "003";
    public final static String operationCode = "";
    public final static String channelCode = "";
    public final static String sealCode = "RSA01";
    public final static String sealPassword="cfca1234";
    
    
    
    
}
