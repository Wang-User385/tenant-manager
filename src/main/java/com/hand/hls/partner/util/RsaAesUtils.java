package com.hand.hls.partner.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaAesUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(RsaAesUtils.class);

    public static void main(String[] args) throws Exception {
        //generatorKeys();
        String data = "{\n" +
                "\t\"head\": {\n" +
                "\t\t\"systemId\": \"HT-SF\",\n" +
                "\t\t\"serialNumber\": \"305967f5-4278-40eb-b257-40e486b550a2\",\n" +
                "\t\t\"transactionCode\": \"HT_SYNC_FARMER_INFO_SIT\",\n" +
                "\t\t\"transactionTime\": \"20240527 15:02:11\",\n" +
                "\t\t\"version\": 1\n" +
                "\t},\n" +
                "\t\"body\": [{\n" +
                "\t\t\"idType\": \"ID_CARD\",\n" +
                "\t\t\"gender\": \"MALE\",\n" +
                "\t\t\"liveAddress\": \"广西钦州市钦南区久隆镇石安村委石安村1-1号\",\n" +
                "\t\t\"idCardFrom\": \"20180227\",\n" +
                "\t\t\"nation\": \"汉\",\n" +
                "\t\t\"idCardNo\": \"450702198210226632\",\n" +
                "\t\t\"dateOfBirth\": \"19821022\",\n" +
                "\t\t\"enabledFlag\": \"Y\",\n" +
                "\t\t\"preApprovalStatus\": \"APPROVED\",\n" +
                "\t\t\"liveProvince\": \"450000\",\n" +
                "\t\t\"bpClass\": \"NP\",\n" +
                "\t\t\"liveCity\": \"450700\",\n" +
                "\t\t\"bpName\": \"罗仕锦\",\n" +
                "\t\t\"nationality\": \"46\",\n" +
                "\t\t\"liveDistrict\": \"450702\",\n" +
                "\t\t\"idCardUntil\": \"20380227\",\n" +
                "\t\t\"cellPhone\": \"19907775553\",\n" +
                "\t\t\"contactInfoList\": []\n" +
                "\t}]\n" +
                "}";
        JSONObject encryptedJson = encryptedData(data);
        System.out.println("加密后的数据： " + encryptedJson.toString());
        String decryptedString = decryptedData(encryptedJson);
        System.out.println("解密后的数据： " + decryptedString);

    }

    public static void generatorKeys(){
        try {
            // 创建一个KeyPairGenerator对象
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            // 初始化KeyPairGenerator对象，设置密钥长度为2048位
            keyPairGenerator.initialize(2048);

            // 生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 获取公钥和私钥
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // 打印Base64编码后的公钥和私钥字符串
            System.out.println("公钥：" + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            System.out.println("私钥：" + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject encryptedData(String data) throws Exception{
        //读取RSA公钥
        String publicKeyStr = getKeyString("security/public.key");

        //生成AES密钥
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Key aesKey = keyGenerator.generateKey();
        //使用AES秘钥加密数据
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedData = aesCipher.doFinal(data.getBytes());
        String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);
        //使用RSA公钥加密AES秘钥
        Cipher rsaCipher = Cipher.getInstance("RSA");
        PublicKey publicKey = getPublicKey(publicKeyStr);
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
        String encryptedAesKeyBase64 = Base64.getEncoder().encodeToString(encryptedAesKey);

        JSONObject encryptedJson = new JSONObject();
        encryptedJson.put("encryptedDataBase64",encryptedDataBase64);
        encryptedJson.put("encryptedAesKeyBase64",encryptedAesKeyBase64);
        return encryptedJson;
    }

    public static String decryptedData(JSONObject encryptedJson) throws Exception{
        //读取RSA私钥
        String privateKeyStr = getKeyString("security/private.key");

        String encryptedDataBase64 = encryptedJson.getString("encryptedDataBase64");
        String encryptedAesKeyBase64 = encryptedJson.getString("encryptedAesKeyBase64");

        //使用RSA私钥解密AES秘钥
        Cipher rsaCipher = Cipher.getInstance("RSA");
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedAesKey = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedAesKeyBase64));

        //使用解密后的AES秘钥解密数据
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, new javax.crypto.spec.SecretKeySpec(decryptedAesKey, "AES"));
        byte[] decryptedData = aesCipher.doFinal(Base64.getDecoder().decode(encryptedDataBase64));
        String decryptedDataStr = new String(decryptedData);

        return decryptedDataStr;
    }

    public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
        publicKeyStr = publicKeyStr
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\n", "");
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        privateKeyStr = privateKeyStr
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\n", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private static String getKeyString(String fileName) {
        if (RsaAesUtils.class.getClassLoader().getResource(fileName) == null) {
            LOGGER.warn("getKeyString()# " + fileName + " is not exist.Will run #generateKeyPair()# firstly.");
            throw new RuntimeException("Can not read Key from " + fileName);
        } else {
            try {
                InputStream in = RsaAesUtils.class.getClassLoader().getResource(fileName).openStream();
                Throwable var2 = null;

                String var3;
                try {
                    var3 = IOUtils.toString(in);
                } catch (Throwable var13) {
                    var2 = var13;
                    throw var13;
                } finally {
                    if (in != null) {
                        if (var2 != null) {
                            try {
                                in.close();
                            } catch (Throwable var12) {
                                var2.addSuppressed(var12);
                            }
                        } else {
                            in.close();
                        }
                    }

                }

                return var3;
            } catch (IOException var15) {
                LOGGER.error("getKeyString()#" + var15.getMessage(), var15);
                throw new RuntimeException("Can not read Key from " + fileName);
            }
        }
    }
}
