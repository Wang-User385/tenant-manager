//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.sm2.SM2HashZValue;
import cfca.sadk.algorithm.sm2.SM2PublicKey;
import cfca.sadk.algorithm.sm2.SM3Digest;
import cfca.sadk.com.itextpdf.io.source.IRandomAccessSource;
import cfca.sadk.com.itextpdf.io.source.RASInputStream;
import cfca.sadk.com.itextpdf.io.source.RandomAccessSourceFactory;
import cfca.sadk.com.itextpdf.kernel.pdf.PdfArray;
import cfca.sadk.com.itextpdf.kernel.pdf.PdfDocument;
import cfca.sadk.com.itextpdf.kernel.pdf.PdfReader;
import cfca.sadk.com.itextpdf.signatures.PdfSignature;
import cfca.sadk.com.itextpdf.signatures.SignatureUtil;
import cfca.sadk.lib.crypto.jni.JNIDigest;
import cfca.sadk.org.bouncycastle.crypto.Digest;
import cfca.sadk.org.bouncycastle.jcajce.provider.asymmetric.sm.SM2Params;
import cfca.sadk.seal.base.config.SysEnv;
import cfca.sadk.seal.base.exception.SealException;
import cfca.sadk.seal.base.util.MethodCostTimeUtil;
import cfca.sadk.seal.base.util.PDFUtil;
import cfca.sadk.system.Mechanisms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

public class PdfHashUtil {
    private static Logger businessLog = LoggerFactory.getLogger(PdfHashUtil.class);

    public PdfHashUtil() {
    }

    public static byte[] calculatePdfHash(InputStream inputStream, Mechanism mechanism, SM2PublicKey publicKey) throws SealException {
        PdfDocument document = null;
        if (null == inputStream) {
            throw new SealException("getHashedInfo inputStream is null");
        } else {
            byte[] var7;
            try {
                PdfReader reader = new PdfReader(inputStream);
                document = new PdfDocument(reader);
                RASInputStream pdfStream = getHashedInfo(document);
                byte[] hashData = calculateHashOfSourceData(pdfStream, mechanism, publicKey);
                var7 = hashData;
            } catch (SealException var12) {
                businessLog.error("calculatePdfHash failed", var12);
                throw var12;
            } catch (Exception var13) {
                businessLog.error("calculatePdfHash failed", var13);
                throw new SealException("calculatePdfHash failed", var13);
            } finally {
                if (document != null) {
                    document.close();
                }

            }

            return var7;
        }
    }

    public static byte[] calculatePdfHash(byte[] pdfData, Mechanism mechanism, SM2PublicKey publicKey) throws SealException {
        PdfDocument document = null;
        if (null == pdfData) {
            throw new SealException("getHashedInfo pdfData is null");
        } else {
            byte[] var7;
            try {
                PdfReader reader = new PdfReader(pdfData);
                document = new PdfDocument(reader);
                RASInputStream pdfStream = getHashedInfo(document);
                byte[] hashData = calculateHashOfSourceData(pdfStream, mechanism, publicKey);
                var7 = hashData;
            } catch (SealException var12) {
                businessLog.error("calculatePdfHash failed", var12);
                throw var12;
            } catch (Exception var13) {
                businessLog.error("calculatePdfHash failed", var13);
                throw new SealException("calculatePdfHash failed", var13);
            } finally {
                if (document != null) {
                    document.close();
                }

            }

            return var7;
        }
    }

    public static byte[] calculateHashOfSourceData(RASInputStream rg, Mechanism mechanism, SM2PublicKey publicKey) throws Exception {
        if (null == mechanism) {
            throw new SealException("calculateHashOfSourceData mechanism is null");
        } else {
            String mechodid = MethodCostTimeUtil.recordStartTime("calculateHashOfSourceData()");
            byte[] buffer = new byte[8192];
            Digest digest = null;
            byte[] hashValue;
            if (Mechanisms.isSM2WithSM3(mechanism)) {
                digest = new SM3Digest();
                if (null != publicKey) {
                    hashValue = SM2HashZValue.getZa(publicKey.getPubX_Int(), publicKey.getPubY_Int(), SM2Params.getDefaultuserid());
                    ((Digest)digest).update(hashValue, 0, hashValue.length);
                }
            } else {
                boolean useJNI = SysEnv.isUseJNIForHash();

                try {
                    if (useJNI) {
                        digest = new JNIDigest(Mechanisms.getHashID(mechanism));
                    } else {
                        digest = Mechanisms.getDigest(mechanism);
                    }
                } catch (Exception var8) {
                    if (useJNI) {
                        businessLog.warn("JNI is not initialized,use softlib ", var8.getMessage());
                        digest = Mechanisms.getDigest(mechanism);
                    } else {
                        businessLog.error("get Digest failed", var8);
                    }
                }
            }

            hashValue = new byte[((Digest)digest).getDigestSize()];
            int rLength = rg.read(buffer);
            PDFUtil.checkLength((long)rg.available());

            do {
                if (rLength < 8192) {
                    ((Digest)digest).update(buffer, 0, rLength);
                    break;
                }

                ((Digest)digest).update(buffer, 0, buffer.length);
                rLength = rg.read(buffer);
            } while(rLength != -1);

            ((Digest)digest).doFinal(hashValue, 0);
            MethodCostTimeUtil.recordEndTime(mechodid);
            return hashValue;
        }
    }

    private static RASInputStream getHashedInfo(PdfDocument document) throws Exception {
        try {
            SignatureUtil signatureUtil = new SignatureUtil(document);
            List<String> sigNames = signatureUtil.getSignatureNames();
            String lastSigName = (String)sigNames.get(sigNames.size() - 1);
            PdfSignature signature = signatureUtil.getSignature(lastSigName);
            PdfArray b = signature.getByteRange();
            long[] gaps = SignatureUtil.asLongArray(b);
            if (b.size() == 4 && gaps[0] == 0L) {
                IRandomAccessSource readerSource = document.getReader().getSafeFile().createSourceView();
                RASInputStream rg = new RASInputStream((new RandomAccessSourceFactory()).createRanged(readerSource, gaps));
                return rg;
            } else {
                throw new IllegalArgumentException("Single exclusion space supported");
            }
        } catch (Exception var9) {
            businessLog.error("getHashedInfo failed", var9);
            throw var9;
        }
    }
}
