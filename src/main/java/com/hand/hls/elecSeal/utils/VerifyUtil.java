//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;


import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.com.itextpdf.kernel.geom.Rectangle;
import cfca.sadk.com.itextpdf.kernel.pdf.PdfArray;
import cfca.sadk.com.itextpdf.kernel.pdf.PdfReader;
import cfca.sadk.com.itextpdf.signatures.PdfPKCS7;
import cfca.sadk.org.bouncycastle.jce.provider.BouncyCastleProvider;
import cfca.sadk.seal.base.bean.sign.SealVerifyResult;
import cfca.sadk.seal.base.bean.sign.VerifyInfo;
import cfca.sadk.seal.base.exception.SealException;
import cfca.sadk.seal.base.util.VerifyProxy;
import cfca.sadk.seal.cert.PdfX509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VerifyUtil {
    private static Logger businessLog = LoggerFactory.getLogger(VerifyUtil.class);
    public static final BouncyCastleProvider provider = new BouncyCastleProvider();

    public VerifyUtil() {
    }

    public static SealVerifyResult verify(byte[] sourceData) throws SealException {
        return verify((byte[])sourceData, (byte[])null);
    }

    public static SealVerifyResult verify(InputStream inputStream) throws SealException {
        return verify((InputStream)inputStream, (byte[])null);
    }

    /** @deprecated */
    public static SealVerifyResult verify(byte[] sourceData, boolean checkSignature, boolean checkModified) throws SealException {
        if (null == sourceData) {
            throw new SealException("sourceData is null");
        } else {
            try {
                VerifyProxy verifyInfo = new VerifyProxy(sourceData);
                return verifyInfo.verifySignature(checkSignature, checkModified);
            } catch (GeneralSecurityException var4) {
                businessLog.error("GeneralSecurityException:", var4);
                throw new SealException(var4.getMessage());
            } catch (IOException var5) {
                businessLog.error("IOException:", var5);
                throw new SealException(var5.getMessage());
            }
        }
    }

    public static byte[] getUncoveredBytes(byte[] sourceData) throws SealException {
        return getUncoveredBytes(sourceData, (byte[])null);
    }

    public static boolean checkUncoveredBytes(byte[] sourceData) throws SealException {
        return checkUncoveredBytes(sourceData, (byte[])null);
    }

    private static SealVerifyResult verify(InputStream inputStream, byte[] openPassword) throws SealException {
        businessLog.info("verify begin...");
        if (null == inputStream) {
            throw new SealException("inputStream is null");
        } else {
            SealVerifyResult var3;
            try {
                VerifyProxy verifyInfo = new VerifyProxy(inputStream, openPassword);
                var3 = verifyInfo.verifySignature();
            } catch (GeneralSecurityException var8) {
                businessLog.error("GeneralSecurityException:", var8);
                throw new SealException(var8.getMessage());
            } catch (IOException var9) {
                businessLog.error("IOException:", var9);
                throw new SealException(var9.getMessage());
            } finally {
                businessLog.info("verify end...");
            }

            return var3;
        }
    }

    public static SealVerifyResult verify(byte[] sourceData, byte[] openPassword) throws SealException {
        businessLog.info("verify begin...");
        if (null == sourceData) {
            throw new SealException("sourceData is null");
        } else {
            SealVerifyResult var3;
            try {
                VerifyProxy verifyInfo = new VerifyProxy(sourceData, openPassword);
                var3 = verifyInfo.verifySignature();
            } catch (GeneralSecurityException var8) {
                businessLog.error("GeneralSecurityException:", var8);
                throw new SealException(var8.getMessage());
            } catch (IOException var9) {
                businessLog.error("IOException:", var9);
                throw new SealException(var9.getMessage());
            } finally {
                businessLog.info("verify end...");
            }

            return var3;
        }
    }

    private static byte[] getUncoveredBytes(byte[] sourceData, byte[] openPassword) throws SealException {
        businessLog.info("getUncoveredBytes begin...");
        if (null == sourceData) {
            throw new SealException("sourceData is null");
        } else {
            byte[] var3;
            try {
                VerifyProxy verifyInfo = new VerifyProxy(sourceData, openPassword);
                var3 = verifyInfo.getUncoveredBytes(sourceData);
            } catch (GeneralSecurityException var8) {
                businessLog.error("GeneralSecurityException:", var8);
                throw new SealException(var8.getMessage());
            } catch (IOException var9) {
                businessLog.error("IOException:", var9);
                throw new SealException(var9.getMessage());
            } finally {
                businessLog.info("getUncoveredBytes end...");
            }

            return var3;
        }
    }

    private static boolean checkUncoveredBytes(byte[] sourceData, byte[] openPassword) throws SealException {
        businessLog.info("checkUncoveredBytes begin...");
        if (null == sourceData) {
            throw new SealException("sourceData is null");
        } else {
            boolean var4;
            try {
                VerifyProxy verifyInfo = new VerifyProxy(sourceData, openPassword);
                SealVerifyResult result = verifyInfo.verifySignature(false, true);
                var4 = Boolean.FALSE.equals(result.getVerifyResult());
            } catch (GeneralSecurityException var9) {
                businessLog.error("GeneralSecurityException:", var9);
                throw new SealException(var9.getMessage());
            } catch (IOException var10) {
                businessLog.error("IOException:", var10);
                throw new SealException(var10.getMessage());
            } finally {
                businessLog.info("checkUncoveredBytes end...");
            }

            return var4;
        }
    }

    /** @deprecated */
    public boolean verifyPKCS7(PdfReader pdfReader, VerifyUtil.AbstractPreProceedingWithVerifyingPKCS7 preVerifyingPKCS7) throws GeneralSecurityException, IOException, PKIException {
        businessLog.info("verifyPKCS7  begin...");
        boolean isPKCS7Valid = false;

        try {
            VerifyProxy verifyInfo = new VerifyProxy(pdfReader, (byte[])null);
            SealVerifyResult verifyResult = verifyInfo.verifySignature();
            isPKCS7Valid = verifyResult.getVerifyResult();
            HashMap<String, VerifyInfo> verifyMap = verifyResult.getVerifyInfos();
            Iterator iterator = verifyMap.keySet().iterator();

            while(iterator.hasNext()) {
                String key = (String)iterator.next();
                VerifyInfo singleInfo = (VerifyInfo)verifyMap.get(key);
                preVerifyingPKCS7.proceedingWithVerifyingPKCS7(0, singleInfo.getSignatureName(), singleInfo.getPdfX509Certificate(), singleInfo.getPdfPKCS7(), (List)null, (PdfArray)null, verifyResult.getFailReason(), singleInfo.getSignOrder(), true, true);
            }
        } catch (GeneralSecurityException var14) {
            businessLog.error("GeneralSecurityException:", var14);
            throw new PKIException(var14.getMessage());
        } catch (IOException var15) {
            businessLog.error("IOException:", var15);
            throw new PKIException(var15.getMessage());
        } finally {
            businessLog.info("verify end...");
        }

        return isPKCS7Valid;
    }

    public class ErrorInfo {
        public static final String CERT_NOT_VALID = "x509Cert is not valid";
        public static final String CERT_IS_EXPIRED = "x509Cert is expired";
        public static final String CERT_IS_REVOKED = "x509Cert is revoked";
        public static final String CERT_IS_NOT_TRUSTED = "x509Cert is not trusted";
        public static final String CERT_KEYUSAGE_WRONG = "keyUsage of x509Cert is wrong";
        public static final String CERT_ISSUER_NOT_FOUND = "can not get the user issuer's cert";

        public ErrorInfo() {
        }
    }

    public static class FieldPositon {
        public int page;
        public Rectangle position;

        public FieldPositon() {
        }
    }

    public abstract static class AbstractPreProceedingWithVerifyingPKCS7 {
        public AbstractPreProceedingWithVerifyingPKCS7() {
        }

        public abstract boolean proceedingWithVerifyingPKCS7(int var1, String var2, PdfX509Certificate var3, PdfPKCS7 var4, List<VerifyUtil.FieldPositon> var5, PdfArray var6, String var7, int var8, boolean var9, boolean var10);
    }
}
