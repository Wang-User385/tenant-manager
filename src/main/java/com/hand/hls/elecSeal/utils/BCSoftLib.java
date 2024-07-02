//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.algorithm.sm2.SM2Crypto;
import cfca.sadk.algorithm.sm2.SM2PrivateKey;
import cfca.sadk.algorithm.sm2.SM2PublicKey;
import cfca.sadk.algorithm.util.SymmetricHelper;
import cfca.sadk.extend.session.ECCCurveId;
import cfca.sadk.jcajce.provider.SADKProvider;
import cfca.sadk.lib.crypto.BaseLib;
import cfca.sadk.lib.crypto.DeviceInfo;
import cfca.sadk.lib.crypto.Session;
import cfca.sadk.lib.crypto.card.c200.ECCHelper;
import cfca.sadk.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import cfca.sadk.org.bouncycastle.crypto.AsymmetricBlockCipher;
import cfca.sadk.org.bouncycastle.crypto.digests.SM3Digest;
import cfca.sadk.org.bouncycastle.crypto.encodings.PKCS1Encoding;
import cfca.sadk.org.bouncycastle.crypto.engines.RSAEngine;
import cfca.sadk.org.bouncycastle.crypto.generators.SM2KeyPairGenerator;
import cfca.sadk.org.bouncycastle.crypto.params.RSAKeyParameters;
import cfca.sadk.org.bouncycastle.jcajce.provider.asymmetric.ec.ECCSuportedCurves;
import cfca.sadk.org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher.ECIES;
import cfca.sadk.org.bouncycastle.jcajce.provider.asymmetric.sm.GMTPrivateKey;
import cfca.sadk.org.bouncycastle.jcajce.provider.asymmetric.sm.GMTPublicKey;
import cfca.sadk.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import cfca.sadk.org.bouncycastle.jce.interfaces.ECPublicKey;
import cfca.sadk.org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import cfca.sadk.signature.rsa.RSAKeyParamsDecoder;
import cfca.sadk.signature.rsa.RSAPackageUtil;
import cfca.sadk.signature.sm2.SM2PackageUtil;
import cfca.sadk.system.SADKDebugger;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;

public final class BCSoftLib extends BaseLib {
    private static final Provider SADK_PROVIDER = SADKProvider.INSTANCE();
    private static volatile Session singleton;
    private HashUtil hashUtil=new HashUtil();
    public BCSoftLib() {
    }

    public static Session INSTANCE() {
        if (singleton == null) {
            Class var0 = BCSoftLib.class;
            synchronized(BCSoftLib.class) {
                if (singleton == null) {
                    singleton = new BCSoftLib();
                }
            }
        }

        return singleton;
    }

    protected KeyPair SM2GenerateKeyPair() throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("SM2GenerateKeyPair::>>>>>>Running");
        }

        try {
            KeyPair keypair = SM2KeyPairGenerator.SM2GenerateKeyPair();
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2GenerateKeyPair::<<<<<<Finished: keypair=" + SADKDebugger.dump(keypair));
            }

            GMTPrivateKey tmp_privateKey = (GMTPrivateKey)keypair.getPrivate();
            GMTPublicKey tmp_publicKey = (GMTPublicKey)keypair.getPublic();
            SM2PrivateKey privKey = new SM2PrivateKey(tmp_privateKey.getD(), tmp_publicKey.getPubX_Int(), tmp_publicKey.getPubY_Int());
            return new KeyPair(privKey.getSM2PublicKey(), privKey);
        } catch (Throwable var5) {
            LoggerManager.exceptionLogger.error("SM2GenerateKeyPair::<<<<<<Failure", var5);
            throw new PKIException("GenerateKeyPair failure with throwable: " + var5.getMessage(), var5);
        }
    }

    protected KeyPair ECCGenerateKeyPair(Mechanism mechanism) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("ECGenerateKeyPair::>>>>>>Running: mechanism=" + SADKDebugger.dump(mechanism));
        }

        try {
            AlgorithmParameterSpec params = null;
            String curveName = mechanism.getCurveName();
            boolean isSupport = true;
            if (curveName == null) {
                curveName = "prime256v1";
            } else {
                isSupport = ECCSuportedCurves.isSupportedCurve(curveName) || ECCCurveId.isCardSupport(ECCCurveId.findECCCurveId(curveName));
            }

            if (isSupport) {
                ASN1ObjectIdentifier oid = ECUtil.getNamedCurveOid(curveName);
                if (oid == null) {
                    throw new PKIException("generateKeyPair " + curveName + " is not supported!");
                } else {
                    params = new ECNamedCurveGenParameterSpec(curveName);
                    KeyPairGenerator keyPairGen = null;

                    try {
                        keyPairGen = KeyPairGenerator.getInstance("EC", SADK_PROVIDER);
                    } catch (Exception var9) {
                        throw new PKIException("ECGenerateKeyPair failure with exception: " + var9.getMessage(), var9);
                    }

                    try {
                        keyPairGen.initialize(params, new SecureRandom());
                        KeyPair keyPair = keyPairGen.generateKeyPair();
                        if (LoggerManager.debugLogger.isDebugEnabled()) {
                            LoggerManager.debugLogger.debug("ECGenerateKeyPair::<<<<<<Finished: keypair=" + SADKDebugger.dump(keyPair));
                        }

                        return keyPair;
                    } catch (Exception var8) {
                        throw new PKIException("ECGenerateKeyPair failure with exception: " + var8.getMessage(), var8);
                    }
                }
            } else {
                throw new PKIException("ECGenerateKeyPair failure with exception: " + curveName + " is not support!");
            }
        } catch (PKIException var10) {
            LoggerManager.exceptionLogger.error("ECGenerateKeyPair::<<<<<<Failure", var10);
            throw var10;
        } catch (Throwable var11) {
            LoggerManager.exceptionLogger.error("ECGenerateKeyPair::<<<<<<Failure", var11);
            throw new PKIException("ECGenerateKeyPair failure with throwable: " + var11.getMessage(), var11);
        }
    }

    protected KeyPair RSAGenerateKeyPair(int bitLength) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            LoggerManager.debugLogger.debug("RSAGenerateKeyPair::>>>>>>Running: bitLength=" + bitLength);
        }

        try {
            if (bitLength >= 0 && bitLength <= 4096 && bitLength % 512 == 0) {
                KeyPairGenerator keyPairGen = null;

                try {
                    keyPairGen = KeyPairGenerator.getInstance("RSA", SADK_PROVIDER);
                } catch (Exception var5) {
                    throw new PKIException("RSAGenerateKeyPair failure with exception: " + var5.getMessage(), var5);
                }

                try {
                    keyPairGen.initialize(bitLength);
                    KeyPair keypair = keyPairGen.generateKeyPair();
                    if (LoggerManager.debugLogger.isDebugEnabled()) {
                        LoggerManager.debugLogger.debug("RSAGenerateKeyPair::<<<<<<Finished: keypair=" + SADKDebugger.dump(keypair));
                    }

                    return keypair;
                } catch (Exception var4) {
                    throw new PKIException("RSAGenerateKeyPair failure with exception: " + var4.getMessage(), var4);
                }
            } else {
                throw new PKIException("RSAGenerateKeyPair failure with invalid bitLength=" + bitLength);
            }
        } catch (PKIException var6) {
            LoggerManager.exceptionLogger.error("RSAGenerateKeyPair::<<<<<<Failure", var6);
            throw var6;
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("RSAGenerateKeyPair::<<<<<<Failure", var7);
            throw new PKIException("RSAGenerateKeyPair failure with throwable: " + var7.getMessage(), var7);
        }
    }

    protected byte[] SM2EncryptMessage(SM2PublicKey sm2PublicKey, byte[] message) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM2EncryptMessage::>>>>>>Running");
            buffer.append("\n sm2PublicKey: ");
            buffer.append(SADKDebugger.dump(sm2PublicKey));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            SM2Crypto crypto = new SM2Crypto();
            crypto.initEncrypt(sm2PublicKey.getQ());
            byte[] encryptData = crypto.encrypt(message);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2EncryptMessage::<<<<<<Finished: encryptData=" + SADKDebugger.dump(encryptData));
            }

            return encryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("SM2EncryptMessage::<<<<<<Failure", var5);
            throw new PKIException("SM2EncryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("SM2EncryptMessage::<<<<<<Failure", var6);
            throw new PKIException("SM2EncryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] SM2DecryptMessage(SM2PrivateKey sm2PrivateKey, byte[] encryptData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM2DecryptMessage::>>>>>>Running");
            buffer.append("\n sm2PrivateKey: ");
            buffer.append(SADKDebugger.dump(sm2PrivateKey));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            SM2Crypto crypto = new SM2Crypto();
            crypto.initDecrypt(sm2PrivateKey.getDByInt());
            byte[] decryptData = crypto.decrypt(encryptData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2DecryptMessage::<<<<<<Finished: decryptData=" + SADKDebugger.dump(decryptData));
            }

            return decryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("SM2DecryptMessage::<<<<<<Failure", var5);
            throw new PKIException("SM2DecryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("SM2DecryptMessage::<<<<<<Failure", var6);
            throw new PKIException("SM2DecryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] SM2SignHash(SM2PrivateKey sm2PrivateKey, byte[] hashValue) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM2SignHash::>>>>>>Running");
            buffer.append("\n sm2PrivateKey: ");
            buffer.append(SADKDebugger.dump(sm2PrivateKey));
            buffer.append("\n hashValue: ");
            buffer.append(SADKDebugger.dump(hashValue));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] signValue = SM2PackageUtil.encryptByBC(hashValue, sm2PrivateKey);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2SignHash::<<<<<<Finished: signValue=" + SADKDebugger.dump(signValue));
            }

            return signValue;
        } catch (Exception var4) {
            LoggerManager.exceptionLogger.error("SM2SignHash::<<<<<<Failure", var4);
            throw new PKIException("SM2SignHash failure with exception: " + var4.getMessage(), var4);
        } catch (Throwable var5) {
            LoggerManager.exceptionLogger.error("SM2SignHash::<<<<<<Failure", var5);
            throw new PKIException("SM2SignHash failure with throwable: " + var5.getMessage(), var5);
        }
    }

    protected boolean SM2VerifyHash(SM2PublicKey sm2PublicKey, byte[] hashValue, byte[] signValue) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM2VerifyHash::>>>>>>Running");
            buffer.append("\n sm2PublicKey: ");
            buffer.append(SADKDebugger.dump(sm2PublicKey));
            buffer.append("\n hashValue: ");
            buffer.append(SADKDebugger.dump(hashValue));
            buffer.append("\n signValue: ");
            buffer.append(SADKDebugger.dump(signValue));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            boolean verifyResult = SM2PackageUtil.verifyByBC(hashValue, signValue, sm2PublicKey);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2VerifyHash::<<<<<<Finished: verifyResult=" + verifyResult);
            }

            return verifyResult;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("SM2VerifyHash::<<<<<<Failure", var5);
            throw new PKIException("SM2VerifyHash failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("SM2VerifyHash::<<<<<<Failure", var6);
            throw new PKIException("SM2VerifyHash failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] SM2HashMessage(SM2PublicKey sm2PubKey, byte[] message, boolean withZ) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM2HashMessage::>>>>>>Running");
            buffer.append("\n sm2PublicKey: ");
            buffer.append(SADKDebugger.dump(sm2PubKey));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            buffer.append("\n withZ: " + withZ);
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] hashValue = new byte[32];
            SM3Digest sm3Hash = new SM3Digest();
            if (withZ) {
                sm3Hash.update(sm2PubKey.getDefaultZ(), 0, 32);
            }

            sm3Hash.update(message, 0, message.length);
            sm3Hash.doFinal(hashValue, 0);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2HashMessage::<<<<<<Finished: hashValue=" + SADKDebugger.dump(hashValue));
            }

            return hashValue;
        } catch (Exception var6) {
            LoggerManager.exceptionLogger.error("SM2HashMessage::<<<<<<Failure", var6);
            throw new PKIException("SM2HashMessage failure with exception: " + var6.getMessage(), var6);
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("SM2HashMessage::<<<<<<Failure", var7);
            throw new PKIException("SM2HashMessage failure with throwable: " + var7.getMessage(), var7);
        }
    }

    protected byte[] SM2HashFile(SM2PublicKey sm2PubKey, InputStream stream, boolean withZ) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM2HashFile::>>>>>>Running");
            buffer.append("\n sm2PublicKey: ");
            buffer.append(SADKDebugger.dump(sm2PubKey));
            buffer.append("\n stream: ");
            buffer.append(SADKDebugger.dump(stream));
            buffer.append("\n withZ: " + withZ);
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] hashValue = new byte[32];
            byte[] buffer = new byte[65536];
            SM3Digest engine = new SM3Digest();
            if (withZ) {
                engine.update(sm2PubKey.getDefaultZ(), 0, 32);
            }

            boolean var7 = false;

            int rLength;
            while((rLength = stream.read(buffer, 0, buffer.length)) != -1) {
                engine.update(buffer, 0, rLength);
            }

            engine.doFinal(hashValue, 0);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2HashFile::<<<<<<Finished: hashValue=" + SADKDebugger.dump(hashValue));
            }

            return hashValue;
        } catch (Exception var8) {
            LoggerManager.exceptionLogger.error("SM2HashFile::<<<<<<Failure", var8);
            throw new PKIException("SM2HashFile failure with exception: " + var8.getMessage(), var8);
        } catch (Throwable var9) {
            LoggerManager.exceptionLogger.error("SM2HashFile::<<<<<<Failure", var9);
            throw new PKIException("SM2HashFile failure with throwable: " + var9.getMessage(), var9);
        }
    }

    protected byte[] RSAEncryptMessage(PublicKey publicKey, byte[] message) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RSAEncryptMessage::>>>>>>Running");
            buffer.append("\n publicKey: ");
            buffer.append(SADKDebugger.dump(publicKey));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            RSAKeyParameters param = RSAKeyParamsDecoder.generatePublicKeyParameter(publicKey);
            AsymmetricBlockCipher eng1 = new RSAEngine();
            AsymmetricBlockCipher eng = new PKCS1Encoding(eng1);
            eng.init(true, param);
            byte[] encryptData = eng.processBlock(message, 0, message.length);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RSAEncryptMessage::<<<<<<Finished: encryptData=" + SADKDebugger.dump(encryptData));
            }

            return encryptData;
        } catch (Exception var6) {
            LoggerManager.exceptionLogger.error("RSAEncryptMessage::<<<<<<Failure", var6);
            throw new PKIException("RSAEncryptMessage failure with exception: " + var6.getMessage(), var6);
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("RSAEncryptMessage::<<<<<<Failure", var7);
            throw new PKIException("RSAEncryptMessage failure with throwable: " + var7.getMessage(), var7);
        }
    }

    protected byte[] RSADecryptMessage(PrivateKey privateKey, byte[] encryptData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RSADecryptMessage::>>>>>>Running");
            buffer.append("\n privateKey: ");
            buffer.append(SADKDebugger.dump(privateKey));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            RSAKeyParameters param = RSAKeyParamsDecoder.generatePrivateKeyParameter(privateKey);
            AsymmetricBlockCipher eng1 = new RSAEngine();
            AsymmetricBlockCipher eng = new PKCS1Encoding(eng1);
            eng.init(false, param);
            byte[] decryptData = eng.processBlock(encryptData, 0, encryptData.length);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RSADecryptMessage::<<<<<<Finished: decryptData=" + SADKDebugger.dump(decryptData));
            }

            return decryptData;
        } catch (Exception var6) {
            LoggerManager.exceptionLogger.error("RSADecryptMessage::<<<<<<Failure", var6);
            throw new PKIException("RSADecryptMessage failure with exception: " + var6.getMessage(), var6);
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("RSADecryptMessage::<<<<<<Failure", var7);
            throw new PKIException("RSADecryptMessage failure with throwable: " + var7.getMessage(), var7);
        }
    }

    protected byte[] RSASignHash(PrivateKey privateKey, byte[] hashWithAlgorithm) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RSASignHash::>>>>>>Running");
            buffer.append("\n privateKey: ");
            buffer.append(SADKDebugger.dump(privateKey));
            buffer.append("\n hashWithAlgorithm: ");
            buffer.append(SADKDebugger.dump(hashWithAlgorithm));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] signValue = RSAPackageUtil.encrypt(hashWithAlgorithm, privateKey);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RSASignHash::<<<<<<Finished: signValue=" + SADKDebugger.dump(signValue));
            }

            return signValue;
        } catch (Exception var4) {
            LoggerManager.exceptionLogger.error("RSASignHash::<<<<<<Failure", var4);
            throw new PKIException("RSASignHash failure with exception: " + var4.getMessage(), var4);
        } catch (Throwable var5) {
            LoggerManager.exceptionLogger.error("RSASignHash::<<<<<<Failure", var5);
            throw new PKIException("RSASignHash failure with throwable: " + var5.getMessage(), var5);
        }
    }

    protected boolean RSAVerifyHash(PublicKey publicKey, byte[] hashWithAlgorithm, byte[] signValue) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RSAVerifyHash::>>>>>>Running");
            buffer.append("\n publicKey: ");
            buffer.append(SADKDebugger.dump(publicKey));
            buffer.append("\n hashWithAlgorithm: ");
            buffer.append(SADKDebugger.dump(hashWithAlgorithm));
            buffer.append("\n signValue: ");
            buffer.append(SADKDebugger.dump(signValue));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] decryptedBytes = RSAPackageUtil.decrypt(signValue, publicKey);
            boolean verifyResult = RSAPackageUtil.isRSAHashEqual(decryptedBytes, hashWithAlgorithm);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RSAVerifyHash::<<<<<<Finished: verifyResult=" + verifyResult);
            }

            return verifyResult;
        } catch (Exception var6) {
            LoggerManager.exceptionLogger.error("RSAVerifyHash::<<<<<<Failure", var6);
            throw new PKIException("RSAVerifyHash failure with exception: " + var6.getMessage(), var6);
        } catch (Throwable var7) {
            LoggerManager.exceptionLogger.error("RSAVerifyHash::<<<<<<Failure", var7);
            throw new PKIException("RSAVerifyHash failure with throwable: " + var7.getMessage(), var7);
        }
    }

    protected byte[] RSAHashMessage(Mechanism mechanism, byte[] message) throws PKIException {
        return this.RSAHashMessage(mechanism, message, true);
    }

    protected byte[] RSAHashMessage(Mechanism mechanism, byte[] message, boolean isDerEncoding) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RSAHashMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] hashWithAlgorithm = hashUtil.RSAHashMessageByBC(message, mechanism, isDerEncoding);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RSAHashMessage::<<<<<<Finished: hashWithAlgorithm=" + SADKDebugger.dump(hashWithAlgorithm));
            }

            return hashWithAlgorithm;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("RSAHashMessage::<<<<<<Failure", var5);
            throw new PKIException("RSAHashMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("RSAHashMessage::<<<<<<Failure", var6);
            throw new PKIException("RSAHashMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] RSAHashFile(Mechanism mechanism, InputStream stream) throws PKIException {
        return this.RSAHashFile(mechanism, stream, true);
    }

    protected byte[] RSAHashFile(Mechanism mechanism, InputStream stream, boolean isDerEncoding) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RSAHashFile::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n stream: ");
            buffer.append(SADKDebugger.dump(stream));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] hashWithAlgorithm = hashUtil.RSAHashFileByBC(stream, mechanism, isDerEncoding);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RSAHashFile::<<<<<<Finished: hashWithAlgorithm=" + SADKDebugger.dump(hashWithAlgorithm));
            }

            return hashWithAlgorithm;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("RSAHashFile::<<<<<<Failure", var5);
            throw new PKIException("RSAHashFile failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("RSAHashFile::<<<<<<Failure", var6);
            throw new PKIException("RSAHashFile failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] SM4EncryptMessage(Mechanism mechanism, Key key, byte[] message) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM4EncryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] encryptData = SymmetricHelper.dataEncrypt(false, mechanism, key.getEncoded(), message);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM4EncryptMessage::<<<<<<Finished: encryptData=" + SADKDebugger.dump(encryptData));
            }

            return encryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("SM4EncryptMessage::<<<<<<Failure", var5);
            throw new PKIException("SM4EncryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("SM4EncryptMessage::<<<<<<Failure", var6);
            throw new PKIException("SM4EncryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] SM4DecryptMessage(Mechanism mechanism, Key key, byte[] encryptData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM4DecryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] decryptData = SymmetricHelper.dataDecrypt(false, mechanism, key.getEncoded(), encryptData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM4DecryptMessage::<<<<<<Finished: decryptData=" + SADKDebugger.dump(decryptData));
            }

            return decryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("SM4DecryptMessage::<<<<<<Failure", var5);
            throw new PKIException("SM4DecryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("SM4DecryptMessage::<<<<<<Failure", var6);
            throw new PKIException("SM4DecryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] RC4EncryptMessage(Mechanism mechanism, Key key, byte[] message) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RC4EncryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] encryptData = SymmetricHelper.dataEncrypt(false, mechanism, key.getEncoded(), message);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RC4EncryptMessage::<<<<<<Finished: encryptData=" + SADKDebugger.dump(encryptData));
            }

            return encryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("RC4EncryptMessage::<<<<<<Failure", var5);
            throw new PKIException("RC4EncryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("RC4EncryptMessage::<<<<<<Failure", var6);
            throw new PKIException("RC4EncryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] RC4DecryptMessage(Mechanism mechanism, Key key, byte[] encryptData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("RC4DecryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] decryptData = SymmetricHelper.dataDecrypt(false, mechanism, key.getEncoded(), encryptData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("RC4DecryptMessage::<<<<<<Finished: decryptData=" + SADKDebugger.dump(decryptData));
            }

            return decryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("RC4DecryptMessage::<<<<<<Failure", var5);
            throw new PKIException("RC4DecryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("RC4DecryptMessage::<<<<<<Failure", var6);
            throw new PKIException("RC4DecryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] DESedeEncryptMessage(Mechanism mechanism, Key key, byte[] message) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("DESedeEncryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] encryptData = SymmetricHelper.dataEncrypt(false, mechanism, key.getEncoded(), message);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("DESedeEncryptMessage::<<<<<<Finished: encryptData=" + SADKDebugger.dump(encryptData));
            }

            return encryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("DESedeEncryptMessage::<<<<<<Failure", var5);
            throw new PKIException("DESedeEncryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("DESedeEncryptMessage::<<<<<<Failure", var6);
            throw new PKIException("DESedeEncryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] DESedeDecryptMessage(Mechanism mechanism, Key key, byte[] encryptData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("DESedeDecryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] decryptData = SymmetricHelper.dataDecrypt(false, mechanism, key.getEncoded(), encryptData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("DESedeDecryptMessage::<<<<<<Finished: decryptData=" + SADKDebugger.dump(decryptData));
            }

            return decryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("DESedeDecryptMessage::<<<<<<Failure", var5);
            throw new PKIException("DESedeDecryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("DESedeDecryptMessage::<<<<<<Failure", var6);
            throw new PKIException("DESedeDecryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] AESEncryptMessage(Mechanism mechanism, Key key, byte[] message) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("AESEncryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] encryptData = SymmetricHelper.dataEncrypt(false, mechanism, key.getEncoded(), message);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("AESEncryptMessage::<<<<<<Finished: encryptData=" + SADKDebugger.dump(encryptData));
            }

            return encryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("AESEncryptMessage::<<<<<<Failure", var5);
            throw new PKIException("AESEncryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("AESEncryptMessage::<<<<<<Failure", var6);
            throw new PKIException("AESEncryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected byte[] AESDecryptMessage(Mechanism mechanism, Key key, byte[] encryptData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("AESDecryptMessage::>>>>>>Running");
            buffer.append("\n mechanism: ");
            buffer.append(SADKDebugger.dump(mechanism));
            buffer.append("\n key: ");
            buffer.append(SADKDebugger.dump(key));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] decryptData = SymmetricHelper.dataDecrypt(false, mechanism, key.getEncoded(), encryptData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("AESDecryptMessage::<<<<<<Finished: decryptData=" + SADKDebugger.dump(decryptData));
            }

            return decryptData;
        } catch (Exception var5) {
            LoggerManager.exceptionLogger.error("AESDecryptMessage::<<<<<<Failure", var5);
            throw new PKIException("AESDecryptMessage failure with exception: " + var5.getMessage(), var5);
        } catch (Throwable var6) {
            LoggerManager.exceptionLogger.error("AESDecryptMessage::<<<<<<Failure", var6);
            throw new PKIException("AESDecryptMessage failure with throwable: " + var6.getMessage(), var6);
        }
    }

    protected void SM2HashFile(SM2PublicKey sm2PubKey, boolean supportedWithoutZ, InputStream stream, byte[] hashWithZ, byte[] hashWithoutZ) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SM2HashFile::>>>>>>Running");
            buffer.append("\n sm2PubKey: ");
            buffer.append(SADKDebugger.dump(sm2PubKey));
            buffer.append("\n supportedWithoutZ: " + supportedWithoutZ);
            buffer.append("\n stream: ");
            buffer.append(SADKDebugger.dump(stream));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        try {
            byte[] buffer = new byte[65536];
            SM3Digest engineWithZ = new SM3Digest();
            engineWithZ.update(sm2PubKey.getDefaultZ(), 0, 32);
            SM3Digest engineWithoutZ = null;
            if (supportedWithoutZ) {
                engineWithoutZ = new SM3Digest();
            }

            boolean var9 = false;

            int rLength;
            while((rLength = stream.read(buffer, 0, buffer.length)) != -1) {
                engineWithZ.update(buffer, 0, rLength);
                if (supportedWithoutZ) {
                    engineWithoutZ.update(buffer, 0, rLength);
                }
            }

            engineWithZ.doFinal(hashWithZ, 0);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("SM2HashFile::<<<<<<Finished: hashWithZ=" + SADKDebugger.dump(hashWithZ));
            }

            if (supportedWithoutZ) {
                engineWithoutZ.doFinal(hashWithoutZ, 0);
                if (LoggerManager.debugLogger.isDebugEnabled()) {
                    LoggerManager.debugLogger.debug("SM2HashFile::<<<<<<Finished: hashWithoutZ=" + SADKDebugger.dump(hashWithoutZ));
                }
            }

        } catch (Exception var10) {
            LoggerManager.exceptionLogger.error("SM2HashFile::<<<<<<Failure", var10);
            throw new PKIException("SM2HashFile failure with exception: " + var10.getMessage(), var10);
        } catch (Throwable var11) {
            LoggerManager.exceptionLogger.error("SM2HashFile::<<<<<<Failure", var11);
            throw new PKIException("SM2HashFile failure with throwable: " + var11.getMessage(), var11);
        }
    }

    protected byte[] ECDSASignHash(PrivateKey privateKey, byte[] hashWithoutAlgorithm, Mechanism mechanism) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("ECDSASignHash::>>>>>>Running");
            buffer.append("\n privateKey: ");
            buffer.append(SADKDebugger.dump(privateKey));
            buffer.append("\n hashWithoutAlgorithm: ");
            buffer.append(SADKDebugger.dump(hashWithoutAlgorithm));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        boolean isFailed = false;

        byte[] var7;
        try {
            Signature ecdsaSigner = Signature.getInstance("NONEwithECDSA", SADKProvider.INSTANCE());
            ecdsaSigner.initSign(privateKey);
            ecdsaSigner.update(hashWithoutAlgorithm);
            byte[] signValue = ecdsaSigner.sign();
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("ECDSASignHash::<<<<<<Finished: signValue=" + SADKDebugger.dump(signValue));
            }

            var7 = signValue;
        } catch (Exception var14) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSASignHash::<<<<<<Failure", var14);
            throw new PKIException("ECDSASignHash failure with exception: " + var14.getMessage(), var14);
        } catch (Throwable var15) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSASignHash::<<<<<<Failure", var15);
            throw new PKIException("ECDSASignHash failure with throwable: " + var15.getMessage(), var15);
        } finally {
            if (isFailed) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("ECDSASignHash::>>>>>>Running");
                buffer.append("\n privateKey: ");
                buffer.append(SADKDebugger.dump(privateKey));
                buffer.append("\n hashWithoutAlgorithm: ");
                buffer.append(SADKDebugger.dump(hashWithoutAlgorithm));
                LoggerManager.exceptionLogger.error(buffer.toString());
            }

        }

        return var7;
    }

    protected boolean ECDSAVerifyHash(PublicKey publicKey, byte[] hashWithoutAlgorithm, byte[] signValue, Mechanism mechanism) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("ECDSAVerifyHash::>>>>>>Running");
            buffer.append("\n publicKey: ");
            buffer.append(SADKDebugger.dump(publicKey));
            buffer.append("\n hashWithAlgorithm: ");
            buffer.append(SADKDebugger.dump(hashWithoutAlgorithm));
            buffer.append("\n signValue: ");
            buffer.append(SADKDebugger.dump(signValue));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        boolean isFailed = false;

        boolean var12;
        try {
            if (signValue == null) {
                throw new Exception("the signature data is null or not 64 bytes!");
            }

            ECPublicKey ecPubKey = (ECPublicKey)publicKey;
            int size = ecPubKey.getParameters().getCurve().getFieldSize();
            int rsLength = (7 + size) / 8;
            byte[] signData = ECCHelper.encodeToStdAsn1(rsLength, signValue);
            Signature ecdsaSigner = Signature.getInstance("NONEwithECDSA", SADKProvider.INSTANCE());
            ecdsaSigner.initVerify(publicKey);
            ecdsaSigner.update(hashWithoutAlgorithm);
            boolean verifyResult = ecdsaSigner.verify(signData);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("ECDSAVerifyHash::<<<<<<Finished: verifyResult=" + verifyResult);
            }

            var12 = verifyResult;
        } catch (Exception var19) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSAVerifyHash::<<<<<<Failure", var19);
            throw new PKIException("ECDSAVerifyHash failure with exception: " + var19.getMessage(), var19);
        } catch (Throwable var20) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSAVerifyHash::<<<<<<Failure", var20);
            throw new PKIException("ECDSAVerifyHash failure with throwable: " + var20.getMessage(), var20);
        } finally {
            if (isFailed) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("ECDSAVerifyHash::>>>>>>Running");
                buffer.append("\n publicKey: ");
                buffer.append(SADKDebugger.dump(publicKey));
                buffer.append("\n hashWithAlgorithm: ");
                buffer.append(SADKDebugger.dump(hashWithoutAlgorithm));
                buffer.append("\n signValue: ");
                buffer.append(SADKDebugger.dump(signValue));
                LoggerManager.exceptionLogger.error(buffer.toString());
            }

        }

        return var12;
    }

    protected byte[] ECCEncryptMessage(PublicKey publicKey, byte[] message) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("ECDSAEncryptMessage::>>>>>>Running");
            buffer.append("\n publicKey: ");
            buffer.append(SADKDebugger.dump(publicKey));
            buffer.append("\n message: ");
            buffer.append(SADKDebugger.dump(message));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        boolean isFailed = false;

        byte[] var6;
        try {
            ECIES engine = new ECIES();
            engine.engineInit(1, publicKey, new SecureRandom());
            byte[] encryptData = engine.engineDoFinal(message, 0, message.length);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("ECDSAEncryptMessage::<<<<<<Finished: encryptData=" + SADKDebugger.dump(encryptData));
            }

            var6 = encryptData;
        } catch (Exception var13) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSAEncryptMessage::<<<<<<Failure", var13);
            throw new PKIException("ECDSAEncryptMessage failure with exception: " + var13.getMessage(), var13);
        } catch (Throwable var14) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSAEncryptMessage::<<<<<<Failure", var14);
            throw new PKIException("ECDSAEncryptMessage failure with throwable: " + var14.getMessage(), var14);
        } finally {
            if (isFailed) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("ECDSAEncryptMessage::>>>>>>Running");
                buffer.append("\n publicKey: ");
                buffer.append(SADKDebugger.dump(publicKey));
                buffer.append("\n message: ");
                buffer.append(SADKDebugger.dump(message));
                LoggerManager.exceptionLogger.error(buffer.toString());
            }

        }

        return var6;
    }

    protected byte[] ECCDecryptMessage(PrivateKey privateKey, byte[] encryptData) throws PKIException {
        if (LoggerManager.debugLogger.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("ECDSADecryptMessage::>>>>>>Running");
            buffer.append("\n privateKey: ");
            buffer.append(SADKDebugger.dump(privateKey));
            buffer.append("\n encryptData: ");
            buffer.append(SADKDebugger.dump(encryptData));
            LoggerManager.debugLogger.debug(buffer.toString());
        }

        boolean isFailed = false;

        byte[] var6;
        try {
            ECIES engine = new ECIES();
            engine.engineInit(2, privateKey, new SecureRandom());
            byte[] decryptData = engine.engineDoFinal(encryptData, 0, encryptData.length);
            if (LoggerManager.debugLogger.isDebugEnabled()) {
                LoggerManager.debugLogger.debug("ECDSADecryptMessage::<<<<<<Finished: decryptData=" + SADKDebugger.dump(decryptData));
            }

            var6 = decryptData;
        } catch (Exception var13) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSADecryptMessage::<<<<<<Failure", var13);
            throw new PKIException("ECDSADecryptMessage failure with exception: " + var13.getMessage(), var13);
        } catch (Throwable var14) {
            isFailed = true;
            LoggerManager.exceptionLogger.error("ECDSADecryptMessage::<<<<<<Failure", var14);
            throw new PKIException("ECDSADecryptMessage failure with throwable: " + var14.getMessage(), var14);
        } finally {
            if (isFailed) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("ECDSADecryptMessage::>>>>>>Running");
                buffer.append("\n privateKey: ");
                buffer.append(SADKDebugger.dump(privateKey));
                buffer.append("\n encryptData: ");
                buffer.append(SADKDebugger.dump(encryptData));
                LoggerManager.exceptionLogger.error(buffer.toString());
            }

        }

        return var6;
    }

    public PublicKey exportEncPublicKey() throws PKIException {
        throw new PKIException("exportEncPublicKey failure: BCSoftLib not supported!");
    }

    public boolean importSM2KeyPair(byte[] encryptKeyData, int CKID) throws PKIException {
        throw new PKIException("importSM2KeyPair failure: BCSoftLib not supported!");
    }

    public String getDeviceName() {
        return "JSOFT_LIB";
    }

    public int getDeviceType() {
        return 0;
    }

    public boolean useJniNativeOperation() throws PKIException {
        return false;
    }

    public DeviceInfo[] getDeviceInfos() throws PKIException {
        return null;
    }
}
