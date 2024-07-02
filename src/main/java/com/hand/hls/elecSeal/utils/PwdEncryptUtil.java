//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.paperless.base.exception.CodeException;
import cfca.paperless.base.util.StringUtil;


public class PwdEncryptUtil {
    private static final String PROTEST_KEY = "1l10OO0O01lll1l1";
    EncryptUtil encryptUtil=new EncryptUtil();
    public PwdEncryptUtil() {
    }

    public  String encrypto(String input) throws CodeException {
        if (!StringUtil.isNotEmptyAndNull(input)) {
            return "";
        } else {
            String ret = input;
            if (StringUtil.isNotEmpty(input)) {
                try {
                    byte[] inputSM4 = encryptUtil.encryptMessageBySM4(input.getBytes("UTF-8"), "1l10OO0O01lll1l1");
                    ret = new String(Base64.encode(inputSM4), "UTF-8");
                } catch (Exception var3) {
                    throw new CodeException("600710", "加密错误");
                }
            }

            return ret;
        }
    }

    public  byte[] encrypto(byte[] input) throws CodeException {
        try {
            byte[] ret = encryptUtil.encryptMessageBySM4(input, "1l10OO0O01lll1l1");
            return ret;
        } catch (Exception var3) {
            throw new CodeException("600710", "加密错误");
        }
    }

    public  String decrypto(String input) throws CodeException {
        if (!StringUtil.isNotEmptyAndNull(input)) {
            return "";
        } else {
            String ret = input;
            if (StringUtil.isNotEmpty(input)) {
                try {
                    byte[] data = Base64.decode(input.getBytes("UTF-8"));
                    byte[] endata = encryptUtil.decryptMessageBySM4(data, "1l10OO0O01lll1l1");
                    ret = new String(endata, "UTF-8");
                } catch (Exception var4) {
                    throw new CodeException("600711", "解密错误，请核对输入数据是否正确加密");
                }
            }

            return ret;
        }
    }

    public  byte[] decrypto(byte[] input) throws CodeException {
        byte[] ret = input;
        if (input != null) {
            try {
                byte[] data = Base64.decode(input);
                ret = encryptUtil.decryptMessageBySM4(data, "1l10OO0O01lll1l1");
            } catch (Exception var3) {
                throw new CodeException("600711", "解密错误，请核对输入数据是否正确加密");
            }
        }

        return ret;
    }
}
