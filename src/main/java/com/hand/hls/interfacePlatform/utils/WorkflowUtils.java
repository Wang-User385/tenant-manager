package com.hand.hls.interfacePlatform.utils;

import java.net.URLEncoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shigure 2022/12/19 15:52
 */
@Component
public class WorkflowUtils {
    @Value("${workflow.encryptStr:}")
    private String encryptStr;

    @Value("${workflow.systemHost:}")
    private String systemHost;

    @Value("${workflow.authPattern:#}")
    private String authPattern;

    /**
     * 解密代码
     *
     * @param content
     * @return
     */
    public String decrypt(String content) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptStr.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] bytes = parseHexStr2Byte(content);
            return new String(cipher.doFinal(bytes));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public String generateDealUrl(String taskId, String processInstanceId){
        String urlSuffix = "/modules/MYWFL/MYWFL001/portal_task_detail.lview?taskId="+taskId+
                "&processInstanceId="+processInstanceId+"&isAdmin=false&winId=task_detail_win";
        return systemHost+authPattern+"?forwardUrl="+ URLEncoder.encode(urlSuffix);
    }

}
