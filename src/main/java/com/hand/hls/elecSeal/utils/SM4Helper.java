//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.paperless.base.util.Base64;
import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.algorithm.sm2.SM3Digest;
import cfca.sadk.algorithm.util.SymmetricHelper;
import cfca.sadk.org.bouncycastle.util.encoders.Hex;
import cfca.sadk.system.Environments;
import java.io.InputStream;
import java.io.OutputStream;


final class SM4Helper {
    private SM4Helper() {
    }

    static boolean encrypt(byte[] k16Bytes, byte[] iv16Bytes, InputStream inputStream, OutputStream outputStream) throws PKIException {
        return SM4CBCEncrypt(true, k16Bytes, iv16Bytes, inputStream, outputStream);
    }

    static boolean decrypt(byte[] k16Bytes, byte[] iv16Bytes, InputStream inputStream, OutputStream outputStream) throws PKIException {
        return SM4CBCEncrypt(false, k16Bytes, iv16Bytes, inputStream, outputStream);
    }

    static byte[] encrypt(byte[] k16Bytes, byte[] iv16Bytes, byte[] data) throws PKIException {
        byte[] encryptData = SM4CBCEncrypt(true, k16Bytes, iv16Bytes, data);

        try {
            return Base64.encode(encryptData);
        } catch (Exception var5) {
            throw new PKIException("SM4CBCEncrypt/Decrypt encryptData encodedBase54 failed!", var5);
        }
    }

    static byte[] decrypt(byte[] k16Bytes, byte[] iv16Bytes, byte[] base64EncryptData) throws PKIException {
        byte[] encryptData;
        try {
            encryptData = Base64.decode(base64EncryptData);
        } catch (Exception var5) {
            throw new PKIException("SM4CBCEncrypt/Decrypt encryptData decodedBase54 failed!", var5);
        }

        return SM4CBCEncrypt(false, k16Bytes, iv16Bytes, encryptData);
    }

    private static boolean SM4CBCEncrypt(boolean forEncryption, byte[] k16Bytes, byte[] iv16Bytes, InputStream inputStream, OutputStream outputStream) throws PKIException {
        check(k16Bytes, iv16Bytes);
        if (inputStream == null) {
            throw new PKIException("SM4CBCEncrypt/Decrypt inputStream required not null!");
        } else if (outputStream == null) {
            throw new PKIException("SM4CBCEncrypt/Decrypt outputStream required not null!");
        } else {
            Mechanism mechanism = new Mechanism(Mechanism.SM4_CBC, iv16Bytes);
            boolean passed;
            if (forEncryption) {
                passed = SymmetricHelper.fileEncrypt(false, mechanism, k16Bytes, inputStream, outputStream);
            } else {
                passed = SymmetricHelper.fileDecrypt(false, mechanism, k16Bytes, inputStream, outputStream);
            }

            return passed;
        }
    }

    private static byte[] SM4CBCEncrypt(boolean forEncryption, byte[] k16Bytes, byte[] iv16Bytes, byte[] data) throws PKIException {
        check(k16Bytes, iv16Bytes);
        if (data == null) {
            throw new PKIException("SM4CBCEncrypt/Decrypt data required not null!");
        } else {
            Mechanism mechanism = new Mechanism(Mechanism.SM4_CBC, iv16Bytes);
            byte[] outBytes;
            if (forEncryption) {
                outBytes = SymmetricHelper.dataEncrypt(false, mechanism, k16Bytes, data);
            } else {
                outBytes = SymmetricHelper.dataDecrypt(false, mechanism, k16Bytes, data);
            }

            return outBytes;
        }
    }

    private static void check(byte[] k16Bytes, byte[] iv16Bytes) throws PKIException {
        if (k16Bytes != null && k16Bytes.length == 16) {
            if (iv16Bytes == null || iv16Bytes.length != 16) {
                throw new PKIException("SM4CBCEncrypt/Decrypt iv16Bytes required 16 bytes!");
            }
        } else {
            throw new PKIException("SM4CBCEncrypt/Decrypt k16Bytes required 16 bytes!");
        }
    }



    static final class IvSM4Key {
        final byte[] iv16Bytes;
        final byte[] k16Bytes;

        IvSM4Key(String password) throws PKIException {
            if (password == null) {
                throw new PKIException("SM4PBEKDF password required not null!");
            } else {
                byte[] iv16Bytes;
                byte[] k16Bytes;
                try {
                    byte[] src = password.getBytes("UTF8");
                    byte[] hash = SM3KDF(src);
                    iv16Bytes = new byte[16];
                    k16Bytes = new byte[16];
                    System.arraycopy(hash, 0, iv16Bytes, 0, 16);
                    System.arraycopy(hash, 16, k16Bytes, 0, 16);
                } catch (Exception var6) {
                    throw new PKIException("SM4PBEKDF failed", var6);
                }

                this.iv16Bytes = iv16Bytes;
                this.k16Bytes = k16Bytes;
            }
        }

        IvSM4Key(byte[] iv16Bytes, byte[] k16Bytes) throws PKIException {
            if (iv16Bytes != null && iv16Bytes.length == 16) {
                if (k16Bytes != null && k16Bytes.length == 16) {
                    this.iv16Bytes = iv16Bytes;
                    this.k16Bytes = k16Bytes;
                } else {
                    throw new PKIException("IvSM4Key required k16Bytes length=16");
                }
            } else {
                throw new PKIException("IvSM4Key required iv16Bytes length=16");
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("IvSM4Key [iv16Bytes=");
            builder.append(Hex.toHexString(this.iv16Bytes));
            builder.append(", k16Bytes=");
            builder.append(Hex.toHexString(this.k16Bytes));
            builder.append("]");
            return builder.toString();
        }

        static byte[] SM3KDF(byte[] data) throws PKIException {
            if (data == null) {
                throw new PKIException("SM3KDF data required not null!");
            } else {
                byte[] ct = new byte[]{0, 0, 0, 1};
                SM3Digest sm3 = new SM3Digest();
                sm3.update(data, 0, data.length);
                sm3.update(ct, 0, ct.length);
                byte[] hash = new byte[32];
                sm3.doFinal(hash, 0);
                return hash;
            }
        }
    }
}
