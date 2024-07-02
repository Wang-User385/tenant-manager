//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKCSObjectIdentifiers;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.algorithm.sm2.SM2HashZValue;
import cfca.sadk.algorithm.sm2.SM2PublicKey;
import cfca.sadk.algorithm.sm2.SM3Digest;
import cfca.sadk.lib.crypto.Session;
import cfca.sadk.lib.crypto.jni.JNIDigest;
import cfca.sadk.lib.crypto.jni.JNISM2;
import cfca.sadk.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import cfca.sadk.org.bouncycastle.asn1.DERNull;
import cfca.sadk.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import cfca.sadk.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import cfca.sadk.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import cfca.sadk.org.bouncycastle.asn1.x509.DigestInfo;
import cfca.sadk.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import cfca.sadk.org.bouncycastle.crypto.Digest;
import cfca.sadk.org.bouncycastle.jcajce.provider.asymmetric.sm.SM2Params;
import cfca.sadk.org.bouncycastle.util.encoders.Hex;
import cfca.sadk.system.Environments;
import cfca.sadk.system.Mechanisms;
import cfca.sadk.util.Assert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Hashtable;

public class HashUtil {
    private  final int BUFFERSIZE = 65536;
    public  final Hashtable<String, ASN1ObjectIdentifier> ALGOIDMAP;

    public HashUtil() {
    }

    public  byte[] SM3HashData(byte[] sourceData, Session session) throws PKIException {
        return SM3HashData(sourceData, (SM2PublicKey)null, session, false);
    }

    public  byte[] SM3HashData(byte[] sourceData, SM2PublicKey sm2PublicKey, Session session, boolean withZ) throws PKIException {
        byte[] hashValue;
        if (withZ) {
            if (sm2PublicKey == null) {
                throw new PKIException("SM3Hash Failure: the sm2PublicKey is null!");
            }

            if (isJniLib(session)) {
                hashValue = SM2HashMessageByJNIWithZValue(SM2Params.getDefaultuserid(), sourceData, sm2PublicKey.getPubX(), sm2PublicKey.getPubY());
            } else {
                hashValue = SM2HashMessageByBCWithZValue(SM2Params.getDefaultuserid(), sourceData, sm2PublicKey.getPubXByInt(), sm2PublicKey.getPubYByInt());
            }
        } else if (isJniLib(session)) {
            hashValue = SM2HashMessageByJNIWithoutZValue(sourceData);
        } else {
            hashValue = SM2HashMessageByBCWithoutZValue(sourceData);
        }

        return hashValue;
    }

    /** @deprecated */
    public  byte[] SM2HashMessageByJNIWithoutZValue(byte[] sourceData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@SM2HashMessageByJNIWithoutZValue>>>>>>Running: " + length(sourceData));
        }

        JNIDigest hash = null;

        byte[] var3;
        try {
            hash = jniInstanceEngine(922);
            byte[] hashValue = hashData(sourceData, hash);
            hash = null;
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashMessageByJNIWithoutZValue>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            var3 = hashValue;
        } catch (PKIException var10) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByJNIWithoutZValue failed", var10);
            throw var10;
        } catch (Exception var11) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByJNIWithoutZValue failed", var11);
            throw new PKIException("HashUtil@SM2HashMessageByJNIWithoutZValue failed", var11);
        } catch (Throwable var12) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByJNIWithoutZValue failed", var12);
            throw new PKIException("HashUtil@SM2HashMessageByJNIWithoutZValue failed", var12);
        } finally {
            JNIDigest.destroy(hash);
        }

        return var3;
    }

    public  byte[] SM3HashDataWithZValue(byte[] sourceData, SM2PublicKey sm2PublicKey, Session session) throws Exception {
        if (sm2PublicKey == null) {
            throw new PKIException("SM3HashDataWithZValue Failure: the sm2PublicKey is null!");
        } else {
            byte[] hashValue;
            if (isJniLib(session)) {
                hashValue = SM2HashMessageByJNIWithZValue(SM2Params.getDefaultuserid(), sourceData, sm2PublicKey.getPubX(), sm2PublicKey.getPubY());
            } else {
                hashValue = SM2HashMessageByBCWithZValue(SM2Params.getDefaultuserid(), sourceData, sm2PublicKey.getPubXByInt(), sm2PublicKey.getPubYByInt());
            }

            return hashValue;
        }
    }

    public  byte[] SM3HashDataWithoutZValue(byte[] sourceData, Session session) throws Exception {
        byte[] hashValue;
        if (isJniLib(session)) {
            hashValue = SM2HashMessageByJNIWithoutZValue(sourceData);
        } else {
            hashValue = SM2HashMessageByBCWithoutZValue(sourceData);
        }

        return hashValue;
    }

    public  byte[] SM3HashFileWithZValue(InputStream sourceStream, SM2PublicKey sm2PublicKey, Session session) throws Exception {
        if (sm2PublicKey == null) {
            throw new PKIException("SM3HashFileWithZValue Failure: the sm2PublicKey is null!");
        } else {
            byte[] hashValue;
            if (isJniLib(session)) {
                hashValue = SM2HashFileByJNIWithZValue(SM2Params.getDefaultuserid(), sourceStream, sm2PublicKey.getPubX(), sm2PublicKey.getPubY());
            } else {
                hashValue = SM2HashFileByBCWithZValue(SM2Params.getDefaultuserid(), sourceStream, sm2PublicKey.getPubXByInt(), sm2PublicKey.getPubYByInt());
            }

            return hashValue;
        }
    }

    public  byte[] SM3HashFileWithoutZValue(InputStream sourceStream, Session session) throws Exception {
        byte[] hashValue;
        if (isJniLib(session)) {
            hashValue = SM2HashFileByJNIWithoutZValue(sourceStream);
        } else {
            hashValue = SM2HashFileByBCWithoutZValue(sourceStream);
        }

        return hashValue;
    }

    public  byte[] RSAHashData(byte[] sourceData, Mechanism mechanism, Session session, boolean ifDEREncoding) throws Exception {
        byte[] hashValue;
        if (isJniLib(session)) {
            hashValue = RSAHashMessageByJNI(sourceData, mechanism, ifDEREncoding);
        } else {
            hashValue = RSAHashMessageByBC(sourceData, mechanism, ifDEREncoding);
        }

        return hashValue;
    }

    public  byte[] RSAHashFile(InputStream sourceStream, Mechanism mechanism, Session session, boolean ifDEREncoding) throws Exception {
        byte[] hashValue;
        if (isJniLib(session)) {
            hashValue = RSAHashFileByJNI(sourceStream, mechanism, ifDEREncoding);
        } else {
            hashValue = RSAHashFileByBC(sourceStream, mechanism, ifDEREncoding);
        }

        return hashValue;
    }

    /** @deprecated */
    public  byte[] RSAHashMessageByBC(byte[] sourceData, Mechanism mechanism, boolean ifDEREncoding) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("RSAHashMessageByBC>>>>>>Running: mechanism=" + mechanism + "," + length(sourceData));
        }

        byte[] var5;
        try {
            if (sourceData == null) {
                throw new PKIException("RSAHashMessage Failure: the source data is null or empty!");
            }

            Digest hash = getDigestByBC(mechanism);
            byte[] hashValue = hashData(sourceData, hash);
            if (ifDEREncoding) {
                hashValue = EncodedHashValue(mechanism, hashValue);
            }

            var5 = hashValue;
        } catch (PKIException var11) {
            LoggerManager.exceptionLogger.error("RSAHashMessageByBC<<<<<<Failure", var11);
            throw var11;
        } catch (Throwable var12) {
            LoggerManager.exceptionLogger.error("RSAHashMessageByBC<<<<<<Failure", var12);
            throw new PKIException("RSAHashMessage Failure: " + var12.getMessage(), var12);
        } finally {
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RSAHashMessageByBC<<<<<<Finished");
            }

        }

        return var5;
    }

    /** @deprecated */
    public  byte[] RSAHashMessageByJNI(byte[] sourceData, Mechanism mechanism, boolean ifDEREncoding) throws Exception {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@RSAHashMessageByJNI>>>>>>Running: " + length(sourceData));
        }

        try {
            Digest hash = jniInstanceEngine(mechanism);
            byte[] hashValue = hashData(sourceData, hash);
            if (ifDEREncoding) {
                hashValue = EncodedHashValue(mechanism, hashValue);
            }

            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@RSAHashMessageByJNI>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            return hashValue;
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashMessageByJNI failed", var5);
            throw var5;
        } catch (Exception var6) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashMessageByJNI failed", var6);
            throw new PKIException("HashUtil@RSAHashMessageByJNI failed", var6);
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashMessageByJNI failed", var7);
            throw new PKIException("HashUtil@RSAHashMessageByJNI failed", var7);
        }
    }

    /** @deprecated */
    public  byte[] RSAHashFileByBC(InputStream sourceStream, Mechanism mechanism, boolean ifDEREncoding) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@RSAHashFileByBC>>>>>>Running: " + length(sourceStream));
        }

        try {
            Digest hash = javaInstanceEngine(mechanism);
            byte[] hashValue = hashFile(sourceStream, hash);
            if (ifDEREncoding) {
                hashValue = EncodedHashValue(mechanism, hashValue);
            }

            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@RSAHashFileByBC>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            return hashValue;
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashFileByBC failed", var5);
            throw var5;
        } catch (Exception var6) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashFileByBC failed", var6);
            throw new PKIException("HashUtil@RSAHashFileByBC failed", var6);
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashFileByBC failed", var7);
            throw new PKIException("HashUtil@RSAHashFileByBC failed", var7);
        }
    }

    /** @deprecated */
    public  byte[] RSAHashFileByJNI(InputStream sourceStream, Mechanism mechanism, boolean ifDEREncoding) throws Exception {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@RSAHashFileByJNI>>>>>>Running: " + length(sourceStream));
        }

        try {
            Digest hash = jniInstanceEngine(mechanism);
            byte[] hashValue = hashFile(sourceStream, hash);
            if (ifDEREncoding) {
                hashValue = EncodedHashValue(mechanism, hashValue);
            }

            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@RSAHashFileByJNI>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            return hashValue;
        } catch (PKIException var5) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashFileByJNI failed", var5);
            throw var5;
        } catch (Exception var6) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashFileByJNI failed", var6);
            throw new PKIException("HashUtil@RSAHashFileByJNI failed", var6);
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("HashUtil@RSAHashFileByJNI failed", var7);
            throw new PKIException("HashUtil@RSAHashFileByJNI failed", var7);
        }
    }

    /** @deprecated */
    public  byte[] SM2HashMessageByBCWithoutZValue(byte[] sourceData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@SM2HashMessageByBCWithoutZValue>>>>>>Running: " + length(sourceData));
        }

        try {
            Digest hash = new SM3Digest();
            byte[] hashValue = hashData(sourceData, hash);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashMessageByBCWithoutZValue>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            return hashValue;
        } catch (PKIException var3) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByBCWithoutZValue failed", var3);
            throw var3;
        } catch (Exception var4) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByBCWithoutZValue failed", var4);
            throw new PKIException("HashUtil@SM2HashMessageByBCWithoutZValue failed", var4);
        } catch (Throwable var5) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByBCWithoutZValue failed", var5);
            throw new PKIException("HashUtil@SM2HashMessageByBCWithoutZValue failed", var5);
        }
    }

    /** @deprecated */
    public  byte[] SM2HashFileByBCWithoutZValue(InputStream sourceStream) throws Exception {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByBCWithoutZValue>>>>>>Running: " + length(sourceStream));
        }

        try {
            Digest hash = new SM3Digest();
            byte[] hashValue = hashFile(sourceStream, hash);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByBCWithoutZValue>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            return hashValue;
        } catch (PKIException var3) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByBCWithoutZValue failed", var3);
            throw var3;
        } catch (Exception var4) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByBCWithoutZValue failed", var4);
            throw new PKIException("HashUtil@SM2HashFileByBCWithoutZValue failed", var4);
        } catch (Throwable var5) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByBCWithoutZValue failed", var5);
            throw new PKIException("HashUtil@SM2HashFileByBCWithoutZValue failed", var5);
        }
    }

    /** @deprecated */
    public  byte[] SM2HashFileByJNIWithoutZValue(InputStream sourceStream) throws Exception {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByJNIWithoutZValue>>>>>>Running: " + length(sourceStream));
        }

        JNIDigest hash = null;

        byte[] var3;
        try {
            hash = jniInstanceEngine(922);
            byte[] hashValue = hashFile(sourceStream, hash);
            hash = null;
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByJNIWithoutZValue>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            var3 = hashValue;
        } catch (PKIException var10) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByJNIWithoutZValue failed", var10);
            throw var10;
        } catch (Exception var11) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByJNIWithoutZValue failed", var11);
            throw new PKIException("HashUtil@SM2HashFileByJNIWithoutZValue failed", var11);
        } catch (Throwable var12) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByJNIWithoutZValue failed", var12);
            throw new PKIException("HashUtil@SM2HashFileByJNIWithoutZValue failed", var12);
        } finally {
            JNIDigest.destroy(hash);
        }

        return var3;
    }

    /** @deprecated */
    public  byte[] SM2HashMessageByBCWithZValue(byte[] sourceData, BigInteger pubX, BigInteger pubY) throws PKIException {
        return SM2HashMessageByBCWithZValue(SM2Params.getDefaultuserid(), sourceData, pubX, pubY);
    }

    /** @deprecated */
    public  byte[] SM2HashMessageByJNIWithZValue(byte[] sourceData, byte[] pubX, byte[] pubY) throws PKIException {
        return SM2HashMessageByJNIWithZValue(SM2Params.getDefaultuserid(), sourceData, pubX, pubY);
    }

    /** @deprecated */
    public  byte[] SM2HashFileByBCWithZValue(InputStream sourceStream, BigInteger pubX, BigInteger pubY) throws PKIException {
        return SM2HashFileByBCWithZValue(SM2Params.getDefaultuserid(), sourceStream, pubX, pubY);
    }

    /** @deprecated */
    public  byte[] SM2HashFileByJNIWithZValue(InputStream sourceStream, byte[] pubX, byte[] pubY) throws PKIException {
        return SM2HashFileByJNIWithZValue(SM2Params.getDefaultuserid(), sourceStream, pubX, pubY);
    }

    /** @deprecated */
    public  byte[] SM2HashMessageByBCWithZValue(byte[] userId, byte[] sourceData, BigInteger pubX, BigInteger pubY) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("SM2HashMessageByBCWithZValue>>>>>>Running: " + length(sourceData));
        }

        byte[] var7;
        try {
            if (sourceData == null || sourceData.length == 0) {
                throw new PKIException("SM2HashMessageByBCWithZValue Failure: the source data is null or empty!");
            }

            byte[] hashValue = new byte[32];
            byte[] z = SM2HashZValue.getZa(pubX, pubY, userId);
            SM3Digest digest = new SM3Digest();
            digest.update(z, 0, z.length);
            digest.update(sourceData, 0, sourceData.length);
            digest.doFinal(hashValue, 0);
            var7 = hashValue;
        } catch (Throwable var12) {
            LoggerManager.exceptionLogger.error("SM2HashMessageByBCWithZValue<<<<<<Failure", var12);
            throw new PKIException("SM2HashMessageByBCWithZValue Failure: " + var12.getMessage(), var12);
        } finally {
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2HashMessageByBCWithZValue<<<<<<Finished");
            }

        }

        return var7;
    }

    /** @deprecated */
    public  byte[] SM2HashMessageByJNIWithZValue(byte[] userId, byte[] sourceData, byte[] pubX, byte[] pubY) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("SM2HashMessageByJNIWithZValue>>>>>>Running: " + length(sourceData));
        }

        JNIDigest hash = null;

        byte[] var7;
        try {
            byte[] zvalue = jniBuildZ(pubX, pubY, userId);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashMessageByJNIWithZValue>>>>>>zvalue: " + Hex.toHexString(zvalue));
            }

            hash = jniInstanceEngine(922);
            hash.update(zvalue, 0, zvalue.length);
            byte[] hashValue = hashData(sourceData, hash);
            hash = null;
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashMessageByJNIWithZValue>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            var7 = hashValue;
        } catch (PKIException var14) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByJNIWithZValue failed", var14);
            throw var14;
        } catch (Exception var15) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByJNIWithZValue failed", var15);
            throw new PKIException("HashUtil@SM2HashMessageByJNIWithZValue failed", var15);
        } catch (Throwable var16) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashMessageByJNIWithZValue failed", var16);
            throw new PKIException("HashUtil@SM2HashMessageByJNIWithZValue failed", var16);
        } finally {
            JNIDigest.destroy(hash);
        }

        return var7;
    }

    /** @deprecated */
    public  byte[] SM2HashFileByBCWithZValue(byte[] userId, InputStream sourceStream, BigInteger pubX, BigInteger pubY) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByBCWithZValue>>>>>>Running: " + length(sourceStream));
        }

        try {
            byte[] zvalue = javaBuildZ(pubX, pubY, userId);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByBCWithZValue>>>>>>zvalue: " + Hex.toHexString(zvalue));
            }

            Digest hash = new SM3Digest();
            hash.update(zvalue, 0, zvalue.length);
            byte[] hashValue = hashFile(sourceStream, hash);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByBCWithZValue>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            return hashValue;
        } catch (PKIException var7) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByBCWithZValue failed", var7);
            throw var7;
        } catch (Exception var8) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByBCWithZValue failed", var8);
            throw new PKIException("HashUtil@SM2HashFileByBCWithZValue failed", var8);
        } catch (Throwable var9) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByBCWithZValue failed", var9);
            throw new PKIException("HashUtil@SM2HashFileByBCWithZValue failed", var9);
        }
    }

    private  byte[] EncodedHashValue(Mechanism mechanism, byte[] hash) throws Exception {
        try {
            AlgorithmIdentifier algId = Mechanisms.getDigestAlgIdentifier(mechanism);
            if (algId == null) {
                algId = getRIPEMDIdentifier(mechanism);
            }

            if (algId == null) {
                throw new Exception("EncodedHashValue with invalid digest mechanism: " + mechanism);
            } else {
                DigestInfo dInfo = new DigestInfo(algId, hash);
                return dInfo.getEncoded("DER");
            }
        } catch (IOException var4) {
            throw new Exception("EncodedHashValue Failure", var4);
        }
    }

    private  AlgorithmIdentifier getRIPEMDIdentifier(Mechanism mechanism) {
        ASN1ObjectIdentifier oid = null;
        if (mechanism != null && mechanism.getMechanismType() != null) {
            String type = mechanism.getMechanismType().toUpperCase();
            if (type.equals("RIPEMD128")) {
                oid = TeleTrusTObjectIdentifiers.ripemd128;
            } else if (type.equals("RIPEMD160")) {
                oid = TeleTrusTObjectIdentifiers.ripemd160;
            } else if (type.equals("RIPEMD256")) {
                oid = TeleTrusTObjectIdentifiers.ripemd256;
            }
        } else {
            oid = null;
        }

        AlgorithmIdentifier digestAlgIdentifier = null;
        if (oid != null) {
            digestAlgIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.sha1, DERNull.INSTANCE);
        }

        return digestAlgIdentifier;
    }

    private  Digest getDigestByBC(Mechanism mechanism) throws Exception {
        Digest engine = Mechanisms.getDigest(mechanism);
        if (engine == null) {
            throw new Exception("DigestByBC: can not support this algorithm:" + mechanism);
        } else {
            return engine;
        }
    }

    /** @deprecated */
    public  byte[] SM2HashFileByJNIWithZValue(byte[] userId, InputStream sourceStream, byte[] pubX, byte[] pubY) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByJNIWithZValue>>>>>>Running: " + length(sourceStream));
        }

        JNIDigest hash = null;

        byte[] var7;
        try {
            byte[] zvalue = jniBuildZ(pubX, pubY, userId);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByJNIWithZValue>>>>>>zvalue: " + Hex.toHexString(zvalue));
            }

            hash = jniInstanceEngine(922);
            hash.update(zvalue, 0, zvalue.length);
            byte[] hashValue = hashFile(sourceStream, hash);
            hash = null;
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("HashUtil@SM2HashFileByJNIWithZValue>>>>>>Finished!hashValue=" + Hex.toHexString(hashValue));
            }

            var7 = hashValue;
        } catch (PKIException var14) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByJNIWithZValue failed", var14);
            throw var14;
        } catch (Exception var15) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByJNIWithZValue failed", var15);
            throw new PKIException("HashUtil@SM2HashFileByJNIWithZValue failed", var15);
        } catch (Throwable var16) {
            LoggerManager.exceptionLogger.error("HashUtil@SM2HashFileByJNIWithZValue failed", var16);
            throw new PKIException("HashUtil@SM2HashFileByJNIWithZValue failed", var16);
        } finally {
            JNIDigest.destroy(hash);
        }

        return var7;
    }

    private  byte[] hashFile(InputStream sourceFileStream, Digest hash) throws PKIException {
        if (hash == null) {
            throw new PKIException("HashUtil@hashFile hash is null!");
        } else {
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug(String.format("HashUtil@hashFile>>>>>>Running: length=%s,hash=%s,algorithm=%s", length(sourceFileStream), hash, hash.getAlgorithmName()));
            }

            BufferedInputStream bufferedReader = null;
            byte[] hashValue = new byte[hash.getDigestSize()];

            try {
                bufferedReader = new BufferedInputStream(sourceFileStream, 65536);
                int streamLength = bufferedReader.available();
                if (streamLength <= 0) {
                    throw new PKIException("HashUtil@hashFile source stream is null!");
                }

                if (streamLength > 0) {
                    byte[] buffer = new byte[65536];
                    int rLength = bufferedReader.read(buffer);
                    if (rLength == -1) {
                        throw new PKIException("HashUtil@hashFile source stream read=-1!");
                    }

                    long datLength = 0L;

                    do {
                        if (rLength < 65536) {
                            hash.update(buffer, 0, rLength);
                            datLength += (long)rLength;
                            break;
                        }

                        hash.update(buffer, 0, buffer.length);
                        datLength += (long)buffer.length;
                        rLength = bufferedReader.read(buffer);
                    } while(rLength != -1);

                    if (datLength < (long)streamLength && datLength != (long)streamLength) {
                        throw new PKIException("HashUtil@hashFile source datLength->" + datLength + "!=" + streamLength);
                    }
                }

                hash.doFinal(hashValue, 0);
            } catch (PKIException var19) {
                throw new PKIException("HashUtil@hashFile failed for " + hash.getAlgorithmName(), var19);
            } catch (Exception var20) {
                throw new PKIException("HashUtil@hashFile failed for " + hash.getAlgorithmName(), var20);
            } catch (Throwable var21) {
                throw new PKIException("HashUtil@hashFile failed for " + hash.getAlgorithmName(), var21);
            } finally {
                if (LoggerManager.debugLogger.isDebugEnabled()) {
                    LoggerManager.debugLogger.debug("HashUtil@hashFile<<<<<<Finished for " + hash.getAlgorithmName());
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception var18) {
                        throw new PKIException("HashUtil@hashFile failed when closed for " + hash.getAlgorithmName(), var18);
                    }
                }

            }

            return hashValue;
        }
    }

    private  byte[] hashData(byte[] data, Digest hash) throws PKIException {
        Assert.notEmpty(data, "data must not be null or empty");
        if (hash == null) {
            throw new PKIException("HashUtil@hashData hash is null!");
        } else {
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug(String.format("HashUtil@hashData>>>>>>Running: length=%s,hash=%s,algorithm=%s", data.length, hash, hash.getAlgorithmName()));
            }

            byte[] hashValue = new byte[hash.getDigestSize()];

            try {
                hash.update(data, 0, data.length);
                hash.doFinal(hashValue, 0);
                return hashValue;
            } catch (Exception var4) {
                throw new PKIException("HashUtil@hashData failed for " + hash.getAlgorithmName(), var4);
            } catch (Throwable var5) {
                throw new PKIException("HashUtil@hashData failed for " + hash.getAlgorithmName(), var5);
            }
        }
    }

    private  Digest javaInstanceEngine(Mechanism mechanism) throws PKIException {
        Digest engine = Mechanisms.getDigest(mechanism);
        if (engine == null) {
            throw new PKIException("HashUtil soft can not support this algorithm:" + mechanism);
        } else {
            return engine;
        }
    }

    private  JNIDigest jniInstanceEngine(Mechanism mechanism) throws PKIException {
        int hashID = Mechanisms.getHashID(mechanism);
        if (hashID == 0) {
            throw new PKIException("HashUtil jni can not support this algorithm:" + mechanism);
        } else {
            return jniInstanceEngine(hashID);
        }
    }

    private  JNIDigest jniInstanceEngine(int hashID) throws PKIException {
        return new JNIDigest(hashID);
    }

    private  byte[] jniBuildZ(byte[] pubX, byte[] pubY, byte[] userId) throws PKIException {
        if (pubX != null && pubX.length == 32) {
            if (pubY != null && pubY.length == 32) {
                byte[] hashValue = new byte[32];

                try {
                    byte[] id = userId == null ? SM2Params.getDefaultuserid() : userId;
                    JNISM2.calculateZValue(pubX, pubY, id, hashValue);
                    return hashValue;
                } catch (Exception var5) {
                    throw new PKIException("HashUtil jni calculateZValue failed", var5);
                } catch (Throwable var6) {
                    throw new PKIException("HashUtil jni calculateZValue failed", var6);
                }
            } else {
                throw new PKIException("HashUtil jni calculateZValue failed: pubYLength required 32");
            }
        } else {
            throw new PKIException("HashUtil jni calculateZValue failed: pubXLength required 32");
        }
    }

    private  byte[] javaBuildZ(BigInteger pubX, BigInteger pubY, byte[] userId) throws PKIException {
        if (pubX == null) {
            throw new PKIException("HashUtil java calculateZValue failed: pubX not null");
        } else if (pubY == null) {
            throw new PKIException("HashUtil java calculateZValue failed: pubY not null");
        } else {
            try {
                byte[] id = userId == null ? SM2Params.getDefaultuserid() : userId;
                byte[] hashValue = SM2HashZValue.getZa(pubX, pubY, id);
                return hashValue;
            } catch (Exception var5) {
                throw new PKIException("HashUtil java calculateZValue failed", var5);
            } catch (Throwable var6) {
                throw new PKIException("HashUtil java calculateZValue failed", var6);
            }
        }
    }

    private  String length(InputStream stream) {
        String length;
        if (stream == null) {
            length = "length=none";
        } else {
            try {
                length = "length=" + stream.available();
            } catch (Exception var3) {
                length = "length=unknown";
            }
        }

        return length;
    }

    private  String length(byte[] message) {
        String length;
        if (message == null) {
            length = "length=none";
        } else {
            length = "length=" + message.length;
        }

        return length;
    }

    private  boolean isJniLib(Session session) {
        return session != null && session.getDeviceType() == 1;
    }

    public  boolean checkHashLength(Mechanism mechanism, byte[] hashValue) throws Exception {
        boolean match = true;
        if (mechanism != null && hashValue != null) {
            int hashLength = Mechanisms.hashLength(mechanism);
            if (hashLength < 0) {
                throw new IllegalArgumentException("invalid hash mechanism: " + mechanism);
            }

            match = hashValue.length == hashLength;
            if (!match) {
                throw new IllegalArgumentException(String.format("hashLength=%d mechanism not match %s", hashValue.length, mechanism.getMechanismType()));
            }
        }

        return match;
    }

     {
        ALGOIDMAP = new Hashtable();
        ALGOIDMAP.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
        ALGOIDMAP.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
        ALGOIDMAP.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
        ALGOIDMAP.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
        ALGOIDMAP.put("sha1WithRSAEncryption", X509ObjectIdentifiers.id_SHA1);
        ALGOIDMAP.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        ALGOIDMAP.put("sha256WithRSAEncryption", NISTObjectIdentifiers.id_sha256);
        ALGOIDMAP.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        ALGOIDMAP.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        ALGOIDMAP.put("sha512WithRSAEncryption", NISTObjectIdentifiers.id_sha512);
        ALGOIDMAP.put("MD2", PKCSObjectIdentifiers.md2);
        ALGOIDMAP.put("MD4", PKCSObjectIdentifiers.md4);
        ALGOIDMAP.put("MD5", PKCSObjectIdentifiers.md5);
        ALGOIDMAP.put("md5WithRSAEncryption", PKCSObjectIdentifiers.md5);
        ALGOIDMAP.put("SM3", PKCSObjectIdentifiers.sm3);
    }
}
