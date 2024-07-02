package com.hand.hap.activiti.util;

import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

/**
 * description
 *
 * @author Lenovo 2023/03/30 14:34
 */
@Slf4j
public class EncryptUtils {

    public static final String ALGORITHM = "AES";
    public static final String SECURE_MODE = "AES/ECB/PKCS5PADDING";

    public static String encrypt(String secret, String content) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(SECURE_MODE);
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            // 初始化为加密模式的密码器
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            //转成16进制返回
            return parseByte2HexStr(result);
        }catch (Exception e){
            log.error("Error while encrypting: {}" , e);
        }
        return null;
    }

    public static String decrypt(String secret, String content) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(SECURE_MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] bytes = parseHexStr2Byte(content);
            return new String(cipher.doFinal(bytes));
        } catch (Exception e) {
            log.error("Error while decrypting: {}" , e);
        }
        return null;
    }

    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return new byte[0];
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
