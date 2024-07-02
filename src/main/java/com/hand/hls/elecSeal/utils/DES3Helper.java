//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.paperless.base.util.Base64;
import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.algorithm.util.SymmetricHelper;
import cfca.sadk.org.bouncycastle.util.encoders.Hex;
import cfca.sadk.system.Environments;
import java.io.InputStream;
import java.io.OutputStream;


final class DES3Helper {
    private DES3Helper() {
    }

    static boolean encrypt(byte[] k24Bytes, byte[] iv8Bytes, InputStream inputStream, OutputStream outputStream) throws PKIException {
        return DES3CBCEncrypt(true, k24Bytes, iv8Bytes, inputStream, outputStream);
    }

    static boolean decrypt(byte[] k24Bytes, byte[] iv8Bytes, InputStream inputStream, OutputStream outputStream) throws PKIException {
        return DES3CBCEncrypt(false, k24Bytes, iv8Bytes, inputStream, outputStream);
    }

    static byte[] encrypt(byte[] k24Bytes, byte[] iv8Bytes, byte[] data) throws PKIException {
        byte[] encryptData = DES3CBCEncrypt(true, k24Bytes, iv8Bytes, data);

        try {
            return Base64.encode(encryptData);
        } catch (Exception var5) {
            throw new PKIException("DES3CBCEncrypt/Decrypt encryptData encodedBase54 failed!", var5);
        }
    }

    static byte[] decrypt(byte[] k24Bytes, byte[] iv8Bytes, byte[] base64EncryptData) throws PKIException {
        byte[] encryptData;
        try {
            encryptData = Base64.decode(base64EncryptData);
        } catch (Exception var5) {
            throw new PKIException("DES3CBCEncrypt/Decrypt encryptData decodedBase54 failed!", var5);
        }

        return DES3CBCEncrypt(false, k24Bytes, iv8Bytes, encryptData);
    }

    private static boolean DES3CBCEncrypt(boolean forEncryption, byte[] k24Bytes, byte[] iv8Bytes, InputStream inputStream, OutputStream outputStream) throws PKIException {
        check(k24Bytes, iv8Bytes);
        if (inputStream == null) {
            throw new PKIException("DES3CBCEncrypt/Decrypt inputStream required not null!");
        } else if (outputStream == null) {
            throw new PKIException("DES3CBCEncrypt/Decrypt outputStream required not null!");
        } else {
            Mechanism mechanism = new Mechanism("DESede/CBC/PKCS7Padding", iv8Bytes);
            boolean passed;
            if (forEncryption) {
                passed = SymmetricHelper.fileEncrypt(false, mechanism, k24Bytes, inputStream, outputStream);
            } else {
                passed = SymmetricHelper.fileDecrypt(false, mechanism, k24Bytes, inputStream, outputStream);
            }

            return passed;
        }
    }

    private static byte[] DES3CBCEncrypt(boolean forEncryption, byte[] k24Bytes, byte[] iv8Bytes, byte[] data) throws PKIException {
        check(k24Bytes, iv8Bytes);
        if (data == null) {
            throw new PKIException("DES3CBCEncrypt/Decrypt data required not null!");
        } else {
            Mechanism mechanism = new Mechanism("DESede/CBC/PKCS7Padding", iv8Bytes);
            byte[] outBytes;
            if (forEncryption) {
                outBytes = SymmetricHelper.dataEncrypt(false, mechanism, k24Bytes, data);
            } else {
                outBytes = SymmetricHelper.dataDecrypt(false, mechanism, k24Bytes, data);
            }

            return outBytes;
        }
    }

    private static void check(byte[] k24Bytes, byte[] iv8Bytes) throws PKIException {
        if (k24Bytes != null && k24Bytes.length == 24) {
            if (iv8Bytes == null || iv8Bytes.length != 8) {
                throw new PKIException("DES3CBCEncrypt/Decrypt iv8Bytes required 8 bytes!");
            }
        } else {
            throw new PKIException("DES3CBCEncrypt/Decrypt k24Bytes required 24 bytes!");
        }
    }

    static {
        Environments.environments();
    }

    static final class IvDES3Key {
        final byte[] iv8Bytes;
        final byte[] k24Bytes;

        IvDES3Key(String password) throws PKIException {
            if (password == null) {
                throw new PKIException("IvDES3Key required password hexstringLength=64");
            } else {
                byte[] data;
                try {
                    data = Hex.decode(password);
                } catch (Exception var4) {
                    throw new PKIException("IvDES3Key decoded password failed: " + password, var4);
                }

                if (data != null && data.length == 32) {
                    this.iv8Bytes = new byte[8];
                    this.k24Bytes = new byte[24];
                    System.arraycopy(data, 0, this.iv8Bytes, 0, 8);
                    System.arraycopy(data, 8, this.k24Bytes, 0, 24);
                } else {
                    throw new PKIException("IvDES3Key required password length=64");
                }
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("IvDES3Key [iv8Bytes=");
            builder.append(Hex.toHexString(this.iv8Bytes));
            builder.append(", k24Bytes=");
            builder.append(Hex.toHexString(this.k24Bytes));
            builder.append("]");
            return builder.toString();
        }
    }
}
