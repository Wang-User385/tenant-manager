//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.paperless.base.util.Base64;
import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.algorithm.sm2.SM2PrivateKey;
import cfca.sadk.lib.crypto.Session;
import cfca.sadk.org.bouncycastle.util.Strings;
import cfca.sadk.system.FileHelper;
import cfca.sadk.system.SADKDebugger;
import cfca.sadk.system.SM2OutputFormat;
import cfca.sadk.util.Assert;
import cfca.sadk.util.KeyUtil;
import cfca.sadk.x509.certificate.X509Cert;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EncryptUtil {
     final String CHARSET = "UTF-8";
     final int MAX_SM2_ENCRYPT_LENGTH = 8388608;
     final int MAX_SM2_DECRYPT_LENGTH = 16777216;
     final int MAX_SM4_MEMORY_DECRYPT_LENGTH = 16777216;

    public EncryptUtil() {
    }

    public  byte[] encryptMessageBySM2(byte[] sourceData, String certFilePath, Session session) throws PKIException {
        X509Cert x509Cert = certFrom(certFilePath);
        if (!x509Cert.isSM2Cert()) {
            LoggerManager.exceptionLogger.error("encryptMessageBySM2<<<<<<Failure: required certFilePath for SM2Cert");
            throw new PKIException("required certFilePath for SM2Cert");
        } else {
            return encrypt(new Mechanism("SM2"), x509Cert.getPublicKey(), sourceData, session);
        }
    }


    public  byte[] encryptMessageBySM2(byte[] sourceData, X509Cert x509Cert, Session session) throws PKIException {
        if (x509Cert == null) {
            throw new PKIException("Param@x509Cert required not null");
        } else if (!x509Cert.isSM2Cert()) {
            LoggerManager.exceptionLogger.error("encryptMessageBySM2<<<<<<Failure: required certFilePath for SM2Cert");
            throw new PKIException("required certFilePath for SM2Cert");
        } else {
            return encryptMessage(new Mechanism("SM2"), x509Cert.getPublicKey(), sourceData, session);
        }
    }


    public  byte[] encryptMessageBySM2(byte[] sourceData, Key key, Session session) throws PKIException {
        if (!(key instanceof PublicKey)) {
            throw new PKIException("encryptMessageBySM2@key required PublicKey");
        } else {
            return encryptMessage(new Mechanism("SM2"), (PublicKey)key, sourceData, session);
        }
    }


    public  byte[] decryptMessageBySM2(byte[] encryptedData, String sm2FilePath, String sm2FilePwd, Session session) throws PKIException {
        StringBuilder buffer;
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            buffer = new StringBuilder();
            buffer.append("decryptMessageBySM2>>>>>>Running");
            buffer.append("\n encryptedData: ");
            buffer.append(SADKDebugger.dump(encryptedData));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (encryptedData == null) {
            throw new PKIException("Param@encryptedData required not null");
        } else if (sm2FilePath == null) {
            throw new PKIException("Param@sm2FilePath key not null");
        } else if (sm2FilePwd == null) {
            throw new PKIException("Param@sm2FilePwd key not null");
        } else if (session == null) {
            throw new PKIException("Param@session required not null");
        } else {
            buffer = null;

            SM2PrivateKey sm2PrivKey;
            try {
                sm2PrivKey = KeyUtil.getPrivateKeyFromSM2(sm2FilePath, sm2FilePwd);
            } catch (PKIException var6) {
                LoggerManager.exceptionLogger.error("decryptMessageBySM2<<<<<<GetPrivateKeyFromSM2 Failure", var6);
                throw var6;
            } catch (Throwable var7) {
                LoggerManager.exceptionLogger.error("decryptMessageBySM2<<<<<<GetPrivateKeyFromSM2 Failure", var7);
                throw new PKIException("decryptMessageBySM2 GetPrivateKeyFromSM2 Failure", var7);
            }

            return decrypt(new Mechanism("SM2"), sm2PrivKey, encryptedData, session);
        }
    }


    public  byte[] decryptMessageBySM2(byte[] encryptedData, Key key, Session session) throws PKIException {
        return decrypt(new Mechanism("SM2"), key, encryptedData, session);
    }


    public  byte[] encryptMessageByRSA(byte[] sourceData, String certFilePath, Session session) throws PKIException {
        X509Cert x509Cert = certFrom(certFilePath);
        if (!x509Cert.isRSACert()) {
            LoggerManager.exceptionLogger.error("encryptMessageByRSA<<<<<<Failure: required certFilePath for RSACert,certFilePath=" + certFilePath);
            throw new PKIException("required certFilePath for RSACert");
        } else {
            return encrypt(new Mechanism("RSA/ECB/PKCS1PADDING"), x509Cert.getPublicKey(), sourceData, session);
        }
    }


    public  byte[] encryptMessageByRSA(byte[] sourceData, X509Cert x509Cert, Session session) throws PKIException {
        if (x509Cert == null) {
            throw new PKIException("Param@x509Cert required not null");
        } else if (!x509Cert.isRSACert()) {
            LoggerManager.exceptionLogger.error("encryptMessageByRSA<<<<<<Failure: required certFilePath for RSACert");
            throw new PKIException("required certFilePath for RSACert");
        } else {
            return encrypt(new Mechanism("RSA/ECB/PKCS1PADDING"), x509Cert.getPublicKey(), sourceData, session);
        }
    }


    public  byte[] encryptMessageByRSA(byte[] sourceData, Key key, Session session) throws PKIException {
        PublicKey pubKey = null;
        if (key instanceof PublicKey) {
            pubKey = (PublicKey)key;
            return encrypt(new Mechanism("RSA/ECB/PKCS1PADDING"), pubKey, sourceData, session);
        } else {
            throw new PKIException("key is not Publickey, PublicKey expected!");
        }
    }


    public  byte[] decryptMessageByRSA(byte[] encryptData, String pfxFilePath, String pfxFilePwd, Session session) throws PKIException {
        if (pfxFilePath == null) {
            throw new PKIException("Param@pfxFilePath required not null");
        } else if (pfxFilePwd == null) {
            throw new PKIException("Param@pfxFilePwd required not null");
        } else {
            PrivateKey prvKey = null;

            try {
                prvKey = KeyUtil.getPrivateKeyFromPFX(pfxFilePath, pfxFilePwd);
            } catch (PKIException var6) {
                LoggerManager.exceptionLogger.error("decryptMessageByRSA<<<<<<GetPrivateKeyFromPFX Failure", var6);
                throw var6;
            } catch (Exception var7) {
                LoggerManager.exceptionLogger.error("decryptMessageByRSA<<<<<<GetPrivateKeyFromPFX Failure", var7);
                throw new PKIException("decryptMessageByRSA GetPrivateKeyFromPFX Failure", var7);
            }

            return decrypt(new Mechanism("RSA/ECB/PKCS1PADDING"), prvKey, encryptData, session);
        }
    }


    public  byte[] decryptMessageByRSA(byte[] encryptData, Key key, Session session) throws PKIException {
        PrivateKey prvKey = null;
        if (key instanceof PrivateKey) {
            prvKey = (PrivateKey)key;
            return decrypt(new Mechanism("RSA/ECB/PKCS1PADDING"), prvKey, encryptData, session);
        } else {
            throw new PKIException("decryptMessageByRSA Failure: key is not PrivateKey, PrivateKey expected!");
        }
    }


    public  void encryptFileBySM2(String sourceFilePath, String encryptFilePath, X509Cert x509Cert, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("encryptFileBySM2>>>>>>Running");
            buffer.append("\n sourceFilePath: ");
            buffer.append(SADKDebugger.dump(sourceFilePath));
            buffer.append("\n x509Cert: ");
            buffer.append(SADKDebugger.dump(x509Cert));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (x509Cert == null) {
            throw new PKIException("Param@x509Cert required not null");
        } else if (sourceFilePath == null) {
            throw new PKIException("missing sourceFilePath");
        } else if (encryptFilePath == null) {
            throw new PKIException("missing encryptFilePath");
        } else if (session == null) {
            throw new PKIException("Param@session required not null");
        } else {
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.exists()) {
                throw new PKIException("sourceFilePath not exists");
            } else if (sourceFile.length() > 8388608L) {
                throw new PKIException("sourceFilePath length limited");
            } else if (!x509Cert.isSM2Cert()) {
                LoggerManager.exceptionLogger.error("encryptMessageBySM2<<<<<<Failure: required certFilePath for SM2Cert");
                throw new PKIException("required certFilePath for SM2Cert");
            } else {
                try {
                    byte[] data = FileHelper.read(sourceFilePath);
                    data = encryptMessageBySM2(data, x509Cert, session);
                    FileHelper.write(encryptFilePath, data);
                    if (LoggerManager.debugLogger.isDebugEnabled()) {
                        LoggerManager.debugLogger.debug("encryptFileBySM2<<<<<<Finished: encryptFilePath=" + encryptFilePath);
                    }

                } catch (PKIException var6) {
                    LoggerManager.exceptionLogger.error("encryptFileBySM2<<<<<<Failure", var6);
                    throw var6;
                } catch (Throwable var7) {
                    LoggerManager.exceptionLogger.error("encryptFileBySM2<<<<<<Failure", var7);
                    throw new PKIException("encryptFileBySM2 Failure: " + var7.getMessage(), var7);
                }
            }
        }
    }


    public  void decryptFileBySM2(String encryptFilePath, String decryptFilePath, SM2PrivateKey sm2PrvKey, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("decryptFileBySM2>>>>>>Running");
            buffer.append("\n encryptFilePath: ");
            buffer.append(SADKDebugger.dump(encryptFilePath));
            buffer.append("\n decryptFilePath: ");
            buffer.append(SADKDebugger.dump(decryptFilePath));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (encryptFilePath == null) {
            throw new PKIException("missing encryptFilePath");
        } else if (decryptFilePath == null) {
            throw new PKIException("missing decryptFilePath");
        } else {
            File encryptFile = new File(encryptFilePath);
            if (!encryptFile.exists()) {
                throw new PKIException("encryptFilePath not exists");
            } else if (encryptFile.length() > 16777216L) {
                throw new PKIException("encryptFilePath length limited");
            } else {
                try {
                    byte[] data = FileHelper.read(encryptFilePath);
                    data = decryptMessageBySM2(data, sm2PrvKey, session);
                    FileHelper.write(decryptFilePath, data);
                    if (LoggerManager.debugLogger.isDebugEnabled()) {
                        LoggerManager.debugLogger.debug("decryptFileBySM2<<<<<<Finished: decryptFilePath=" + decryptFilePath);
                    }

                } catch (PKIException var6) {
                    LoggerManager.exceptionLogger.error("decryptFileBySM2<<<<<<Failure", var6);
                    throw var6;
                } catch (Throwable var7) {
                    LoggerManager.exceptionLogger.error("decryptFileBySM2<<<<<<Failure", var7);
                    throw new PKIException("decryptFileBySM2 Failure: " + var7.getMessage(), var7);
                }
            }
        }
    }

    public  String encryptMessageByDES3(String sourceText, String password) throws PKIException {
        if (sourceText == null) {
            throw new PKIException("encryptMessageByDES3@sourceText required not null!");
        } else {
            byte[] sourceData;
            try {
                sourceData = sourceText.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var4) {
                LoggerManager.exceptionLogger.error("encryptMessageByDES3@sourceText getBytes failed!", var4);
                throw new PKIException("encryptMessageByDES3@sourceText getBytes failed!", var4);
            }

            byte[] base64EncryptData = encryptMessageByDES3(sourceData, password);
            return Strings.fromByteArray(base64EncryptData);
        }
    }

    public  String decryptMessageByDES3(String base64EncryptData, String password) throws PKIException {
        if (base64EncryptData == null) {
            throw new PKIException("decryptMessageByDES3@base64EncryptData required not null!");
        } else {
            byte[] encryptData;
            try {
                encryptData = base64EncryptData.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var6) {
                LoggerManager.exceptionLogger.error("decryptMessageByDES3@base64EncryptData getBytes failed!", var6);
                throw new PKIException("decryptMessageByDES3@base64EncryptData getBytes failed!", var6);
            }

            byte[] decryptData = decryptMessageByDES3(encryptData, password);

            try {
                return new String(decryptData, "UTF-8");
            } catch (UnsupportedEncodingException var5) {
                LoggerManager.exceptionLogger.error("decryptMessageByDES3@decryptData toString failed!", var5);
                throw new PKIException("decryptMessageByDES3@decryptData toString failed!", var5);
            }
        }
    }

    public  void encryptFileByDES3(String sourceFilePath, String encryptFilePath, String password) throws PKIException {
        PKIException failure = null;
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(sourceFilePath);
            outputStream = new FileOutputStream(encryptFilePath);
            encryptFileByDES3((InputStream)inputStream, (OutputStream)outputStream, password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileByDES3<<<<<<Finished!");
                buffer.append("\n sourceFilePath: ");
                buffer.append(SADKDebugger.dump(sourceFilePath));
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.debugLogger.debug(buffer.toString(), failure);
            }
        } catch (PKIException var13) {
            failure = var13;
            throw var13;
        } catch (Exception var14) {
            failure = new PKIException("encryptFileByDES3 Failure: " + var14.getMessage(), var14);
            throw failure;
        } finally {
            FileHelper.closedStream(inputStream, "encryptFileByDES3@sourceFilePath closed failed");
            FileHelper.closedStream(outputStream, "encryptFileByDES3@encryptFilePath closed failed");
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileByDES3<<<<<<Failure!");
                buffer.append("\n sourceFilePath: ");
                buffer.append(SADKDebugger.dump(sourceFilePath));
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }

    public  void decryptFileByDES3(String encryptFilePath, String decryptOutputStream, String password) throws PKIException {
        PKIException failure = null;
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(encryptFilePath);
            outputStream = new FileOutputStream(decryptOutputStream);
            decryptFileByDES3((InputStream)inputStream, (OutputStream)outputStream, password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileByDES3<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.debugLogger.debug(buffer.toString(), failure);
            }
        } catch (PKIException var13) {
            failure = var13;
            throw var13;
        } catch (Exception var14) {
            failure = new PKIException("decryptFileByDES3 Failure: " + var14.getMessage(), var14);
            throw failure;
        } finally {
            FileHelper.closedStream(inputStream, "decryptFileByDES3@encryptFilePath closed failed");
            FileHelper.closedStream(outputStream, "decryptFileByDES3@decryptOutputStream closed failed");
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileByDES3<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }

    public  void decryptFileByDES3(String encryptFilePath, ByteArrayOutputStream outByteStream, String password) throws PKIException {
        PKIException failure = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(encryptFilePath);
            decryptFileByDES3((InputStream)inputStream, (OutputStream)outByteStream, password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileByDES3<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n outByteStream: ");
                buffer.append(SADKDebugger.dump(outByteStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.debugLogger.debug(buffer.toString(), failure);
            }
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("decryptFileByDES3 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            FileHelper.closedStream(inputStream, "decryptFileByDES3@encryptFilePath closed failed");
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileByDES3<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n outByteStream: ");
                buffer.append(SADKDebugger.dump(outByteStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }

    public  byte[] encryptMessageBySM4(byte[] sourceData, String password) throws PKIException {
        SM4Helper.IvSM4Key ivKey;
        try {
            ivKey = new SM4Helper.IvSM4Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("KDFIvSM4KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv16Bytes));
                buffer.append("\n k16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k16Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var4) {
            LoggerManager.exceptionLogger.error("KDFIvSM4KeyFromPassword<<<<<<Failure: password=" + password, var4);
            throw var4;
        }

        return encryptMessageBySM4(sourceData, ivKey.iv16Bytes, ivKey.k16Bytes);
    }

    public  byte[] decryptMessageBySM4(byte[] encryptData, String password) throws PKIException {
        SM4Helper.IvSM4Key ivKey;
        try {
            ivKey = new SM4Helper.IvSM4Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("KDFIvSM4KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv16Bytes));
                buffer.append("\n k16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k16Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var4) {
            LoggerManager.exceptionLogger.error("KDFIvSM4KeyFromPassword<<<<<<Failure: password=" + password, var4);
            throw var4;
        }

        return decryptMessageBySM4(encryptData, ivKey.iv16Bytes, ivKey.k16Bytes);
    }

    public  byte[] encryptMessageBySM4(byte[] sourceData, byte[] sm4iv, byte[] sm4key) throws PKIException {
        PKIException failure = null;

        byte[] var15;
        try {
            byte[] base64EncryptData = SM4Helper.encrypt(sm4key, sm4iv, sourceData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptMessageBySM4<<<<<<Finished!");
                buffer.append("\n sourceData: ");
                buffer.append(SADKDebugger.dump(sourceData));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                buffer.append("\n base64EncryptText: ");
                buffer.append(SADKDebugger.dumpBase64(base64EncryptData));
                LoggerManager.debugLogger.debug(buffer.toString());
            }

            var15 = base64EncryptData;
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("encryptMessageBySM4 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptMessageBySM4<<<<<<Failure!");
                buffer.append("\n sourceData: ");
                buffer.append(SADKDebugger.dump(sourceData));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

        return var15;
    }

    public  byte[] decryptMessageBySM4(byte[] base64EncryptedBytes, byte[] sm4iv, byte[] sm4key) throws PKIException {
        PKIException failure = null;

        byte[] var15;
        try {
            byte[] sourceData = SM4Helper.decrypt(sm4key, sm4iv, base64EncryptedBytes);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptMessageBySM4<<<<<<Finished!");
                buffer.append("\n base64EncryptedBytes: ");
                buffer.append(SADKDebugger.dumpBase64(base64EncryptedBytes));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                buffer.append("\n sourceData: ");
                buffer.append(SADKDebugger.dump(sourceData));
                LoggerManager.debugLogger.debug(buffer.toString());
            }

            var15 = sourceData;
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("decryptMessageBySM4 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptMessageBySM4<<<<<<Failure!");
                buffer.append("\n base64EncryptedBytes: ");
                buffer.append(SADKDebugger.dumpBase64(base64EncryptedBytes));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

        return var15;
    }

    public  void encryptFileBySM4(String sourceFilePath, String encryptFilePath, String password) throws PKIException {
        PKIException failure = null;
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(sourceFilePath);
            outputStream = new FileOutputStream(encryptFilePath);
            encryptFileBySM4((InputStream)inputStream, (OutputStream)outputStream, password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileBySM4<<<<<<Finished!");
                buffer.append("\n sourceFilePath: ");
                buffer.append(SADKDebugger.dump(sourceFilePath));
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.debugLogger.debug(buffer.toString(), failure);
            }
        } catch (PKIException var13) {
            failure = var13;
            throw var13;
        } catch (Exception var14) {
            failure = new PKIException("encryptFileBySM4 Failure: " + var14.getMessage(), var14);
            throw failure;
        } finally {
            FileHelper.closedStream(inputStream, "encryptFileBySM4@sourceFilePath closed failed");
            FileHelper.closedStream(outputStream, "encryptFileBySM4@encryptFilePath closed failed");
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileBySM4<<<<<<Failure!");
                buffer.append("\n sourceFilePath: ");
                buffer.append(SADKDebugger.dump(sourceFilePath));
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }

    public  void decryptFileBySM4(String encryptFilePath, String decryptOutputStream, String password) throws PKIException {
        PKIException failure = null;
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(encryptFilePath);
            outputStream = new FileOutputStream(decryptOutputStream);
            decryptFileBySM4((InputStream)inputStream, (OutputStream)outputStream, password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileBySM4<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.debugLogger.debug(buffer.toString(), failure);
            }
        } catch (PKIException var13) {
            failure = var13;
            throw var13;
        } catch (Exception var14) {
            failure = new PKIException("decryptFileBySM4 Failure: " + var14.getMessage(), var14);
            throw failure;
        } finally {
            FileHelper.closedStream(inputStream, "decryptFileBySM4@encryptFilePath closed failed");
            FileHelper.closedStream(outputStream, "decryptFileBySM4@decryptOutputStream closed failed");
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileBySM4<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }

    public  void decryptFileBySM4(String encryptFilePath, ByteArrayOutputStream decryptOutputStream, String password) throws PKIException {
        PKIException failure = null;
        FileInputStream encryptInputStream = null;

        try {
            encryptInputStream = new FileInputStream(encryptFilePath);
            decryptFileBySM4((InputStream)encryptInputStream, (OutputStream)decryptOutputStream, password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileBySM4<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.debugLogger.debug(buffer.toString(), failure);
            }
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("decryptFileBySM4 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            FileHelper.closedStream(encryptInputStream, "decryptFileBySM4@encryptInputStream closed failed");
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileBySM4<<<<<<Failure!");
                buffer.append("\n encryptFilePath: ");
                buffer.append(SADKDebugger.dump(encryptFilePath));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }


    public  int decryptFileBySM4(String encryptedFilePath, byte[] outPlainBytes, String password) throws PKIException {
        if (encryptedFilePath == null) {
            throw new PKIException("decryptFileBySM4@encryptedFilePath required not nul!");
        } else if (outPlainBytes == null) {
            throw new PKIException("decryptFileBySM4@outPlainBytes required not nul!");
        } else if (password == null) {
            throw new PKIException("decryptFileBySM4@password required not nul!");
        } else {
            File encryptedFile = new File(encryptedFilePath);
            if (!encryptedFile.exists()) {
                throw new PKIException("decryptFileBySM4@encryptedFilePath not exists!encryptedFile->" + encryptedFile);
            } else {
                long encryptedFileLength = encryptedFile.length();
                if (encryptedFileLength > (long)outPlainBytes.length) {
                    throw new PKIException(String.format("decryptFileBySM4@encryptedFilePath(%s) more than outPlainBytes(%s)", encryptedFileLength, outPlainBytes.length));
                } else if (encryptedFileLength > 16777216L) {
                    throw new PKIException(String.format("decryptFileBySM4@encryptedFilePath(%s) limited MAX_SM4_MEMORY_DECRYPT_LENGTH(%s)", encryptedFileLength, 16777216));
                } else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream((int)encryptedFileLength);
                    decryptFileBySM4(encryptedFilePath, baos, password);
                    byte[] decryptBytes = baos.toByteArray();
                    baos.reset();
                    baos = null;
                    System.arraycopy(decryptBytes, 0, outPlainBytes, 0, decryptBytes.length);
                    return decryptBytes.length;
                }
            }
        }
    }

    public  byte[] encrypt(Mechanism mechanism, Key key, byte[] sourceData, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("encrypt[data]>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n sourceData: ");
            buffer.append(SADKDebugger.dump(sourceData));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (mechanism == null) {
            throw new PKIException("encrypt[data]Param@mechanism required not null");
        } else if (key == null) {
            throw new PKIException("encrypt[data]Param@key required not null");
        } else if (session == null) {
            throw new PKIException("encrypt[data]Param@session required not null");
        } else {
            Assert.notEmpty(sourceData, "encrypt[data]Param@sourceData must not be null or empty");
            PKIException failure = null;

            byte[] var7;
            try {
                byte[] encryptedBytes = session.encrypt(mechanism, key, sourceData);
                byte[] base64EncryptBytes = Base64.encode(encryptedBytes);
                if (LoggerManager.debugLogger.isDebugEnabled()) {
                    LoggerManager.debugLogger.debug("encrypt[data]<<<<<<Finished: base64EncryptBytes=" + SADKDebugger.dumpBase64(base64EncryptBytes));
                }

                var7 = base64EncryptBytes;
            } catch (PKIException var15) {
                failure = var15;
                throw var15;
            } catch (Exception var16) {
                failure = new PKIException("encrypt[data] Failure: " + var16.getMessage(), var16);
                throw failure;
            } catch (Throwable var17) {
                failure = new PKIException("encrypt[data] Failure: " + var17.getMessage(), var17);
                throw failure;
            } finally {
                if (failure != null) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("encrypt[data]>>>>>>Failure");
                    buffer.append("\n mechanism: ");
                    buffer.append(SADKDebugger.dump(mechanism));
                    buffer.append("\n key: ");
                    buffer.append(SADKDebugger.dump(key));
                    buffer.append("\n sourceData: ");
                    buffer.append(SADKDebugger.dump(sourceData));
                    buffer.append("\n session: ");
                    buffer.append(SADKDebugger.dump(session));
                    LoggerManager.exceptionLogger.error(buffer.toString(), failure);
                }

            }

            return var7;
        }
    }

    public  byte[] decrypt(Mechanism mechanism, Key key, byte[] encryptData, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("decrypt[data]>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (mechanism == null) {
            throw new PKIException("decrypt[data]Param@mechanism required not null");
        } else if (key == null) {
            throw new PKIException("decrypt[data]Param@key required not null");
        } else if (session == null) {
            throw new PKIException("decrypt[data]Param@session required not null");
        } else {
            Assert.notEmpty(encryptData, "decrypt[data]Param@encryptData must not be null or empty");

            byte[] encryptBytes;
            try {
                encryptBytes = Base64.decode(encryptData);
            } catch (Exception var16) {
                LoggerManager.exceptionLogger.error("decrypt[data]<<<<<<Failure: Param@encryptData required base64=" + SADKDebugger.dump(encryptData));
                throw new PKIException("decrypt[data]Param@encryptData required base64", var16);
            }

            PKIException failure = null;
            Object var6 = null;

            byte[] var7;
            try {
                byte[] decryptBytes = session.decrypt(mechanism, key, encryptBytes);
                if (LoggerManager.debugLogger.isDebugEnabled()) {
                    LoggerManager.debugLogger.debug("decrypt[data]<<<<<<Finished: decryptBytes=" + SADKDebugger.dump(decryptBytes));
                }

                var7 = decryptBytes;
            } catch (PKIException var17) {
                failure = var17;
                throw var17;
            } catch (Exception var18) {
                failure = new PKIException("decrypt[data] Failure: " + var18.getMessage(), var18);
                throw failure;
            } catch (Throwable var19) {
                failure = new PKIException("decrypt[data] Failure: " + var19.getMessage(), var19);
                throw failure;
            } finally {
                if (failure != null) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("decrypt[data]>>>>>>Failure");
                    buffer.append("\n mechanism: ");
                    buffer.append(SADKDebugger.dump(mechanism));
                    buffer.append("\n key: ");
                    buffer.append(SADKDebugger.dump(key));
                    buffer.append("\n encryptData: ");
                    buffer.append(SADKDebugger.dump(encryptData));
                    buffer.append("\n session: ");
                    buffer.append(SADKDebugger.dump(session));
                    LoggerManager.exceptionLogger.error(buffer.toString(), failure);
                }

            }

            return var7;
        }
    }

    public  void encrypt(Mechanism mechanism, Key key, String sourceFilePath, String encryptFilePath, Session session) throws PKIException {
        if (sourceFilePath == null) {
            throw new PKIException("encrypt[file]Param@sourceFilePath required not null");
        } else if (encryptFilePath == null) {
            throw new PKIException("encrypt[file]Param@encryptFilePath required not null");
        } else {
            Assert.hasFileLength(sourceFilePath);
            FileInputStream inputStream = null;
            FileOutputStream outputStream = null;
            PKIException failure = null;

            try {
                inputStream = new FileInputStream(sourceFilePath);
                File file = new File(encryptFilePath);
                if (!file.exists()) {
                    boolean createResult = file.createNewFile();
                    if (!createResult) {
                        LoggerManager.exceptionLogger.error("createNewFile failed: " + file.getAbsolutePath());
                    }
                }

                outputStream = new FileOutputStream(file);
                encryptFile(mechanism, key, inputStream, outputStream, session);
            } catch (PKIException var16) {
                throw var16;
            } catch (Exception var17) {
                failure = new PKIException("encrypt[file] Failure: " + var17.getMessage(), var17);
                throw failure;
            } catch (Throwable var18) {
                failure = new PKIException("encrypt[file] Failure: " + var18.getMessage(), var18);
                throw failure;
            } finally {
                FileHelper.closedStream(inputStream, "encrypt[file]@inputStream closed failure");
                FileHelper.closedStream(outputStream, "encrypt[file]@outputStream closed failure");
            }

        }
    }

    public  void decrypt(Mechanism mechanism, Key key, String encryptFilePath, String decryptOutputStream, Session session) throws PKIException {
        if (encryptFilePath == null) {
            throw new PKIException("decrypt[file]Param@encryptFilePath required not null");
        } else if (decryptOutputStream == null) {
            throw new PKIException("decrypt[file]Param@decryptOutputStream required not null");
        } else {
            Assert.hasFileLength(encryptFilePath);
            FileInputStream inputStream = null;
            FileOutputStream outputStream = null;
            PKIException failure = null;

            try {
                inputStream = new FileInputStream(encryptFilePath);
                File file = new File(decryptOutputStream);
                if (!file.exists()) {
                    boolean createResult = file.createNewFile();
                    if (!createResult) {
                        LoggerManager.exceptionLogger.error("createNewFile failed: " + file.getAbsolutePath());
                    }
                }

                outputStream = new FileOutputStream(file);
                decryptFile(mechanism, key, inputStream, outputStream, session);
            } catch (PKIException var16) {
                throw var16;
            } catch (Exception var17) {
                failure = new PKIException("decrypt[file] Failure: " + var17.getMessage(), var17);
                throw failure;
            } catch (Throwable var18) {
                failure = new PKIException("decrypt[file] Failure: " + var18.getMessage(), var18);
                throw failure;
            } finally {
                FileHelper.closedStream(inputStream, "decrypt[file]@inputStream closed failure");
                FileHelper.closedStream(outputStream, "decrypt[file]@outputStream closed failure");
            }

        }
    }

    private  void encryptFile(Mechanism mechanism, Key key, InputStream inputStream, OutputStream outputStream, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("encrypt[file]>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n inputStream: ");
            buffer.append(SADKDebugger.dump(inputStream));
            buffer.append("\n outputStream: ");
            buffer.append(SADKDebugger.dump(outputStream));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (mechanism == null) {
            throw new PKIException("encrypt[file]Param@mechanism required not null");
        } else if (key == null) {
            throw new PKIException("encrypt[file]Param@key required not null");
        } else if (inputStream == null) {
            throw new PKIException("encrypt[file]Param@inputStream required not null");
        } else if (outputStream == null) {
            throw new PKIException("encrypt[file]Param@outputStream required not null");
        } else if (session == null) {
            throw new PKIException("encrypt[file]Param@session required not null");
        } else {
            PKIException failure = null;

            try {
                session.encrypt(mechanism, key, inputStream, outputStream);
                if (LoggerManager.debugLogger.isDebugEnabled()) {
                    LoggerManager.debugLogger.debug("encrypt[file]<<<<<<Finished!");
                }
            } catch (PKIException var14) {
                failure = var14;
                throw var14;
            } catch (Exception var15) {
                failure = new PKIException("encrypt[file] Failure: " + var15.getMessage(), var15);
                throw failure;
            } catch (Throwable var16) {
                failure = new PKIException("encrypt[file] Failure: " + var16.getMessage(), var16);
                throw failure;
            } finally {
                if (failure != null) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("encrypt[file]>>>>>>Running");
                    buffer.append("\n mechanism: ");
                    buffer.append(SADKDebugger.dump(mechanism));
                    buffer.append("\n key: ");
                    buffer.append(SADKDebugger.dump(key));
                    buffer.append("\n inputStream: ");
                    buffer.append(SADKDebugger.dump(inputStream));
                    buffer.append("\n outputStream: ");
                    buffer.append(SADKDebugger.dump(outputStream));
                    buffer.append("\n session: ");
                    buffer.append(SADKDebugger.dump(session));
                    LoggerManager.exceptionLogger.error(buffer.toString(), failure);
                }

            }

        }
    }

    private  void decryptFile(Mechanism mechanism, Key key, InputStream inputStream, OutputStream outputStream, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("decrypt[file]>>>>>>Running");
            buffer.append("\n Mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n inputStream: ");
            buffer.append(SADKDebugger.dump(inputStream));
            buffer.append("\n outputStream: ");
            buffer.append(SADKDebugger.dump(outputStream));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (mechanism == null) {
            throw new PKIException("decrypt[file]Param@mechanism required not null");
        } else if (key == null) {
            throw new PKIException("decrypt[file]Param@key required not null");
        } else if (inputStream == null) {
            throw new PKIException("decrypt[file]Param@inputStream required not null");
        } else if (outputStream == null) {
            throw new PKIException("decrypt[file]Param@outputStream required not null");
        } else if (session == null) {
            throw new PKIException("decrypt[file]Param@session required not null");
        } else {
            PKIException failure = null;

            try {
                session.decrypt(mechanism, key, inputStream, outputStream);
                if (LoggerManager.debugLogger.isDebugEnabled()) {
                    LoggerManager.debugLogger.debug("decrypt[file]<<<<<<Finished!");
                }
            } catch (PKIException var14) {
                failure = var14;
                throw var14;
            } catch (Exception var15) {
                failure = new PKIException("decrypt[file] Failure: " + var15.getMessage(), var15);
                throw failure;
            } catch (Throwable var16) {
                failure = new PKIException("decrypt[file] Failure: " + var16.getMessage(), var16);
                throw failure;
            } finally {
                if (failure != null) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("decrypt[file]>>>>>>Running");
                    buffer.append("\n Mechanism: ");
                    buffer.append(SADKDebugger.dump(mechanism));
                    buffer.append("\n key: ");
                    buffer.append(SADKDebugger.dump(key));
                    buffer.append("\n inputStream: ");
                    buffer.append(SADKDebugger.dump(inputStream));
                    buffer.append("\n outputStream: ");
                    buffer.append(SADKDebugger.dump(outputStream));
                    buffer.append("\n session: ");
                    buffer.append(SADKDebugger.dump(session));
                    LoggerManager.exceptionLogger.error(buffer.toString(), failure);
                }

            }

        }
    }


    public  byte[] encryptMessageByDES3(byte[] sourceData, String password) throws PKIException {
        DES3Helper.IvDES3Key ivKey;
        try {
            ivKey = new DES3Helper.IvDES3Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("buildIvDES3KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv8Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv8Bytes));
                buffer.append("\n k24Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k24Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var4) {
            LoggerManager.exceptionLogger.error("buildIvDES3KeyFromPassword<<<<<<Failure: password=" + password, var4);
            throw var4;
        }

        return encryptMessageByDES3(sourceData, ivKey.iv8Bytes, ivKey.k24Bytes);
    }


    public  byte[] decryptMessageByDES3(byte[] base64EncryptedBytes, String password) throws PKIException {
        DES3Helper.IvDES3Key ivKey;
        try {
            ivKey = new DES3Helper.IvDES3Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("buildIvDES3KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv8Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv8Bytes));
                buffer.append("\n k24Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k24Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var4) {
            LoggerManager.exceptionLogger.error("buildIvDES3KeyFromPassword<<<<<<Failure: password=" + password, var4);
            throw var4;
        }

        return decryptMessageByDES3(base64EncryptedBytes, ivKey.iv8Bytes, ivKey.k24Bytes);
    }


    public  byte[] encryptMessageByDES3(byte[] sourceData, byte[] des3iv, byte[] des3key) throws PKIException {
        PKIException failure = null;

        byte[] var15;
        try {
            byte[] base64EncryptData = DES3Helper.encrypt(des3key, des3iv, sourceData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptMessageByDES3<<<<<<Finished!");
                buffer.append("\n sourceData: ");
                buffer.append(SADKDebugger.dump(sourceData));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                buffer.append("\n base64EncryptText: ");
                buffer.append(SADKDebugger.dumpBase64(base64EncryptData));
                LoggerManager.debugLogger.debug(buffer.toString());
            }

            var15 = base64EncryptData;
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("encryptMessageByDES3 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptMessageByDES3<<<<<<Failure!");
                buffer.append("\n sourceData: ");
                buffer.append(SADKDebugger.dump(sourceData));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

        return var15;
    }


    public  byte[] decryptMessageByDES3(byte[] base64EncryptedBytes, byte[] des3iv, byte[] des3key) throws PKIException {
        PKIException failure = null;

        byte[] var15;
        try {
            byte[] sourceData = DES3Helper.decrypt(des3key, des3iv, base64EncryptedBytes);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptMessageByDES3<<<<<<Finished!");
                buffer.append("\n base64EncryptedBytes: ");
                buffer.append(SADKDebugger.dumpBase64(base64EncryptedBytes));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                buffer.append("\n sourceData: ");
                buffer.append(SADKDebugger.dump(sourceData));
                LoggerManager.debugLogger.debug(buffer.toString());
            }

            var15 = sourceData;
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("decryptMessageByDES3 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptMessageByDES3<<<<<<Failure!");
                buffer.append("\n base64EncryptedBytes: ");
                buffer.append(SADKDebugger.dumpBase64(base64EncryptedBytes));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

        return var15;
    }


    public  void encryptFileByDES3(InputStream sourceInputStream, OutputStream encryptOutputStream, String password) throws PKIException {
        DES3Helper.IvDES3Key ivKey;
        try {
            ivKey = new DES3Helper.IvDES3Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("buildIvDES3KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv8Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv8Bytes));
                buffer.append("\n k24Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k24Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("buildIvDES3KeyFromPassword<<<<<<Failure: password=" + password, var5);
            throw var5;
        }

        encryptFileByDES3(sourceInputStream, encryptOutputStream, ivKey.iv8Bytes, ivKey.k24Bytes);
    }


    public  void decryptFileByDES3(InputStream encryptInputStream, OutputStream decryptOutputStream, String password) throws PKIException {
        DES3Helper.IvDES3Key ivKey;
        try {
            ivKey = new DES3Helper.IvDES3Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("buildIvDES3KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv8Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv8Bytes));
                buffer.append("\n k24Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k24Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("buildIvDES3KeyFromPassword<<<<<<Failure: password=" + password, var5);
            throw var5;
        }

        decryptFileByDES3(encryptInputStream, decryptOutputStream, ivKey.iv8Bytes, ivKey.k24Bytes);
    }


    public  void encryptFileByDES3(InputStream sourceInputStream, OutputStream encryptOutputStream, byte[] des3iv, byte[] des3key) throws PKIException {
        PKIException failure = null;

        try {
            DES3Helper.encrypt(des3key, des3iv, sourceInputStream, encryptOutputStream);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileByDES3<<<<<<Finished!");
                buffer.append("\n sourceInputStream: ");
                buffer.append(SADKDebugger.dump(sourceInputStream));
                buffer.append("\n encryptOutputStream: ");
                buffer.append(SADKDebugger.dump(encryptOutputStream));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("encryptFileByDES3 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileByDES3<<<<<<Failure!");
                buffer.append("\n sourceInputStream: ");
                buffer.append(SADKDebugger.dump(sourceInputStream));
                buffer.append("\n encryptOutputStream: ");
                buffer.append(SADKDebugger.dump(encryptOutputStream));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }


    public  void decryptFileByDES3(InputStream encryptInputStream, OutputStream decryptOutputStream, byte[] des3iv, byte[] des3key) throws PKIException {
        PKIException failure = null;

        try {
            DES3Helper.decrypt(des3key, des3iv, encryptInputStream, decryptOutputStream);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileByDES3<<<<<<Finished!");
                buffer.append("\n encryptInputStream: ");
                buffer.append(SADKDebugger.dump(encryptInputStream));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("decryptFileByDES3 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileByDES3<<<<<<Failure!");
                buffer.append("\n encryptInputStream: ");
                buffer.append(SADKDebugger.dump(encryptInputStream));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n des3key: ");
                buffer.append(SADKDebugger.dump(des3key));
                buffer.append("\n des3iv: ");
                buffer.append(SADKDebugger.dump(des3iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }


    public  void encryptFileBySM4(InputStream sourceInputStream, OutputStream encryptOutputStream, String password) throws PKIException {
        SM4Helper.IvSM4Key ivKey;
        try {
            ivKey = new SM4Helper.IvSM4Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("KDFIvSM4KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv16Bytes));
                buffer.append("\n k16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k16Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("KDFIvSM4KeyFromPassword<<<<<<Failure: password=" + password, var5);
            throw var5;
        }

        encryptFileBySM4(sourceInputStream, encryptOutputStream, ivKey.iv16Bytes, ivKey.k16Bytes);
    }


    public  void decryptFileBySM4(InputStream encryptInputStream, OutputStream decryptOutputStream, String password) throws PKIException {
        SM4Helper.IvSM4Key ivKey;
        try {
            ivKey = new SM4Helper.IvSM4Key(password);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("KDFIvSM4KeyFromPassword<<<<<<Finished!");
                buffer.append("\n password: ");
                buffer.append(SADKDebugger.dump(password));
                buffer.append("\n iv16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.iv16Bytes));
                buffer.append("\n k16Bytes: ");
                buffer.append(SADKDebugger.dump(ivKey.k16Bytes));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("KDFIvSM4KeyFromPassword<<<<<<Failure: password=" + password, var5);
            throw var5;
        }

        decryptFileBySM4(encryptInputStream, decryptOutputStream, ivKey.iv16Bytes, ivKey.k16Bytes);
    }


    public  void encryptFileBySM4(InputStream sourceInputStream, OutputStream encryptOutputStream, byte[] sm4iv, byte[] sm4key) throws PKIException {
        PKIException failure = null;

        try {
            SM4Helper.encrypt(sm4key, sm4iv, sourceInputStream, encryptOutputStream);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileBySM4<<<<<<Finished!");
                buffer.append("\n sourceInputStream: ");
                buffer.append(SADKDebugger.dump(sourceInputStream));
                buffer.append("\n encryptOutputStream: ");
                buffer.append(SADKDebugger.dump(encryptOutputStream));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("encryptFileBySM4 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("encryptFileBySM4<<<<<<Failure!");
                buffer.append("\n sourceInputStream: ");
                buffer.append(SADKDebugger.dump(sourceInputStream));
                buffer.append("\n encryptOutputStream: ");
                buffer.append(SADKDebugger.dump(encryptOutputStream));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }


    public void decryptFileBySM4(InputStream encryptInputStream, OutputStream decryptOutputStream, byte[] sm4iv, byte[] sm4key) throws PKIException {
        PKIException failure = null;

        try {
            SM4Helper.decrypt(sm4key, sm4iv, encryptInputStream, decryptOutputStream);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileBySM4<<<<<<Finished!");
                buffer.append("\n encryptInputStream: ");
                buffer.append(SADKDebugger.dump(encryptInputStream));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                LoggerManager.debugLogger.debug(buffer.toString());
            }
        } catch (PKIException var12) {
            failure = var12;
            throw var12;
        } catch (Exception var13) {
            failure = new PKIException("decryptFileBySM4 Failure: " + var13.getMessage(), var13);
            throw failure;
        } finally {
            if (failure != null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("decryptFileBySM4<<<<<<Failure!");
                buffer.append("\n encryptInputStream: ");
                buffer.append(SADKDebugger.dump(encryptInputStream));
                buffer.append("\n decryptOutputStream: ");
                buffer.append(SADKDebugger.dump(decryptOutputStream));
                buffer.append("\n sm4key: ");
                buffer.append(SADKDebugger.dump(sm4key));
                buffer.append("\n sm4iv: ");
                buffer.append(SADKDebugger.dump(sm4iv));
                LoggerManager.exceptionLogger.error(buffer.toString(), failure);
            }

        }

    }


    public  byte[] encryptMessage(Mechanism encryptAlg, PublicKey publicKey, byte[] sourceData, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("encryptMessage>>>>>>Running");
            buffer.append("\n Mechanism: ");
            buffer.append(SADKDebugger.dump(encryptAlg));
            buffer.append("\n publicKey: ");
            buffer.append(SADKDebugger.dump(publicKey));
            buffer.append("\n sourceData: ");
            buffer.append(SADKDebugger.dump(sourceData));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] encryptedBytes = session.encrypt(encryptAlg, publicKey, sourceData);
            encryptedBytes = sm2FormatEncrypted64Bytes(encryptAlg, encryptedBytes);
            byte[] base64EncryptBytes = Base64.encode(encryptedBytes);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("encryptMessage<<<<<<Finished: base64EncryptBytes=" + SADKDebugger.dumpBase64(base64EncryptBytes));
            }

            return base64EncryptBytes;
        } catch (PKIException var6) {
            LoggerManager.exceptionLogger.error("encryptMessage<<<<<<Failure", var6);
            throw var6;
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("encryptMessage<<<<<<Failure", var7);
            throw new PKIException("encryptMessage Failure: " + var7.getMessage(), var7);
        }
    }


    public  byte[] decryptMessage(Mechanism encryptAlg, Key key, byte[] base64EncryptedBytes, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("decryptMessage>>>>>>Running");
            buffer.append("\n Mechanism: ");
            buffer.append(SADKDebugger.dump(encryptAlg));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n base64EncryptedBytes: ");
            buffer.append(SADKDebugger.dump(base64EncryptedBytes));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] decryptBytes = session.decrypt(encryptAlg, key, Base64.decode(base64EncryptedBytes));
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("decryptMessage<<<<<<Finished: decryptBytes=" + SADKDebugger.dump(decryptBytes));
            }

            return decryptBytes;
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("decryptMessage<<<<<<Failure", var5);
            throw var5;
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("decryptMessage<<<<<<Failure", var6);
            throw new PKIException("decryptMessage Failure: " + var6.getMessage(), var6);
        }
    }


    public  void encrypt(Mechanism mechanism, Key key, FileInputStream plainFileInputStream, FileOutputStream out, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("encrypt>>>>>>Running");
            buffer.append("\n Mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            session.encrypt(mechanism, key, plainFileInputStream, out);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("encrypt<<<<<<Finished");
            }

        } catch (PKIException var6) {
            LoggerManager.exceptionLogger.error("encrypt<<<<<<Failure", var6);
            throw var6;
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("encrypt<<<<<<Failure", var7);
            throw new PKIException("encrypt Failure: " + var7.getMessage(), var7);
        }
    }


    public  void decrypt(Mechanism encryptAlg, Key key, FileInputStream encryptFileInputStream, FileOutputStream out, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("decrypt>>>>>>Running");
            buffer.append("\n Mechanism: ");
            buffer.append(SADKDebugger.dump(encryptAlg));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            session.decrypt(encryptAlg, key, encryptFileInputStream, out);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("decrypt<<<<<<Finished");
            }

        } catch (PKIException var6) {
            LoggerManager.exceptionLogger.error("decrypt<<<<<<Failure", var6);
            throw var6;
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("decrypt<<<<<<Failure", var7);
            throw new PKIException("decrypt Failure: " + var7.getMessage(), var7);
        }
    }


    public  void encryptFileBySM2(FileInputStream plainFileInputStream, FileOutputStream out, X509Cert sm2cert, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("encryptFileBySM2>>>>>>Running");
            buffer.append("\n X509Cert: ");
            buffer.append(SADKDebugger.dump(sm2cert));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            encryptFileBySM2(plainFileInputStream, out, sm2cert.getPublicKey(), session);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("encryptFileBySM2<<<<<<Finished");
            }

        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("encryptFileBySM2<<<<<<Failure", var5);
            throw var5;
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("encryptFileBySM2<<<<<<Failure", var6);
            throw new PKIException("encryptFileBySM2 Failure: " + var6.getMessage(), var6);
        }
    }


    public  void encryptFileBySM2(FileInputStream plainFileInputStream, FileOutputStream out, PublicKey sm2pubKey, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("encryptFileBySM2>>>>>>Running");
            buffer.append("\n PublicKey: ");
            buffer.append(SADKDebugger.dump(sm2pubKey));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] plainData = new byte[plainFileInputStream.available()];
            plainFileInputStream.read(plainData);
            byte[] encryptedData = encryptMessageBySM2(plainData, (Key)sm2pubKey, session);
            out.write(encryptedData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("encryptFileBySM2<<<<<<Finished");
            }

        } catch (PKIException var6) {
            LoggerManager.exceptionLogger.error("encryptFileBySM2<<<<<<Failure", var6);
            throw var6;
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("encryptFileBySM2<<<<<<Failure", var7);
            throw new PKIException("encryptFileBySM2 Failure: " + var7.getMessage(), var7);
        }
    }


    public  void decryptFileBySM2(FileInputStream encryptedFileInputStream, FileOutputStream out, PrivateKey sm2priKey, Session session) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("decryptFileBySM2>>>>>>Running");
            buffer.append("\n PrivateKey: ");
            buffer.append(SADKDebugger.dump(sm2priKey));
            buffer.append("\n session: ");
            buffer.append(SADKDebugger.dump(session));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] encryptedData = new byte[encryptedFileInputStream.available()];
            encryptedFileInputStream.read(encryptedData);
            byte[] decryptedData = decryptMessageBySM2(encryptedData, sm2priKey, session);
            out.write(decryptedData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("decryptFileBySM2<<<<<<Finished");
            }

        } catch (PKIException var6) {
            LoggerManager.exceptionLogger.error("decryptFileBySM2<<<<<<Failure", var6);
            throw var6;
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("decryptFileBySM2<<<<<<Failure", var7);
            throw new PKIException("decryptFileBySM2 Failure: " + var7.getMessage(), var7);
        }
    }

    private  byte[] sm2FormatEncrypted64Bytes(Mechanism mechanism, byte[] encryptedBytes) {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("sm2FormatEncrypted64Bytes::>>>>>>Running");
            buffer.append("\n Mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n encryptedBytes: ");
            buffer.append(SADKDebugger.dump(encryptedBytes));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        if (mechanism != null && "SM2".equals(mechanism.getMechanismType())) {
            encryptedBytes = SM2OutputFormat.sm2FormatEncryptedRAWBytes(encryptedBytes);
        }

        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("sm2FormatEncrypted64Bytes::<<<<<<Finished: encryptedBytes=" + SADKDebugger.dump(encryptedBytes));
        }

        return encryptedBytes;
    }

    private  X509Cert certFrom(String certFilePath) throws PKIException {
        if (certFilePath == null) {
            throw new PKIException("certFrom Param@certFilePath required not null");
        } else {
            try {
                X509Cert x509Cert = new X509Cert(certFilePath);
                return x509Cert;
            } catch (PKIException var2) {
                LoggerManager.exceptionLogger.error("certFrom<<<<<<Failure: +certFilePath", var2);
                throw var2;
            } catch (Exception var3) {
                LoggerManager.exceptionLogger.error("certFrom<<<<<<Failure: +certFilePath", var3);
                throw new PKIException("certFrom Failure: " + certFilePath, var3);
            }
        }
    }

}
