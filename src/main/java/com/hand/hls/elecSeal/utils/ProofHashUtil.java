//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.paperless.base.bean.ProofBean;
import cfca.paperless.base.exception.CodeException;
import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.jcajce.provider.SADKProvider;
import cfca.sadk.org.bouncycastle.util.encoders.Hex;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProofHashUtil {
    private  Logger businessLog = LoggerFactory.getLogger(ProofHashUtil.class);
    private HashUtil hashUtil=new HashUtil();

    public ProofHashUtil() {
    }

    public  String bytes2hex(byte[] bytes) {
        String result = Hex.toHexString(bytes);
        return result.toUpperCase();
    }

    public  String digestAndHex(byte[] source) throws Exception {
        businessLog.info("digestAndHex start");
        long start = System.currentTimeMillis();
        if (source != null && source.length != 0) {
            ByteArrayInputStream input = new ByteArrayInputStream(source);
            byte[] digestBytes = hashUtil.RSAHashFile(input, new Mechanism("SHA-1"), BCSoftLib.INSTANCE(), false);
            input = null;
            String digest = bytes2hex(digestBytes);
            long end = System.currentTimeMillis();
            businessLog.info("digestAndHex end, cost =" + (end - start) + " ms");
            return digest;
        } else {
            return "";
        }
    }

    public  String digestAndHex(byte[] source, String hashAlg) throws Exception {
        businessLog.info("digestAndHex start");
        long start = System.currentTimeMillis();
        if (source != null && source.length != 0) {
            if (!"SHA-1".equals(hashAlg) && !"SHA-256".equals(hashAlg) && !"SHA-384".equals(hashAlg) && !"SHA-512".equals(hashAlg) && !"SM3".equals(hashAlg) && !"MD5".equals(hashAlg)) {
                throw new CodeException("600402", "hashAlg取值错误");
            } else {
                Mechanism mechanism = new Mechanism("SHA-1");
                if ("SHA-1".equals(hashAlg)) {
                    mechanism = new Mechanism("SHA-1");
                } else if ("SHA-256".equals(hashAlg)) {
                    mechanism = new Mechanism("SHA-256");
                } else if ("SHA-384".equals(hashAlg)) {
                    mechanism = new Mechanism("SHA-384");
                } else if ("SHA-512".equals(hashAlg)) {
                    mechanism = new Mechanism("SHA-512");
                } else if ("SM3".equals(hashAlg)) {
                    mechanism = new Mechanism("SM3");
                } else if ("MD5".equals(hashAlg)) {
                    mechanism = new Mechanism("MD5");
                }

                ByteArrayInputStream input = new ByteArrayInputStream(source);
                byte[] digestBytes = null;
                if (!"SHA-1".equals(hashAlg) && !"SHA-256".equals(hashAlg) && !"SHA-384".equals(hashAlg) && !"SHA-512".equals(hashAlg)) {
                    if ("SM3".equals(hashAlg)) {
                        digestBytes = hashUtil.SM3HashFileWithoutZValue(input, BCSoftLib.INSTANCE());
                    } else if ("MD5".equals(hashAlg)) {
                        MessageDigest messageDigest = MessageDigest.getInstance("MD5", SADKProvider.INSTANCE());
                        digestBytes = messageDigest.digest(source);
                    }
                } else {
                    digestBytes = hashUtil.RSAHashFile(input, mechanism, BCSoftLib.INSTANCE(), false);
                }

                input = null;
                String digest = bytes2hex(digestBytes);
                long end = System.currentTimeMillis();
                businessLog.info("digestAndHex end, cost =" + (end - start) + " ms");
                return digest;
            }
        } else {
            return "";
        }
    }

    public  boolean compareHashHex(byte[] source, String result) throws Exception {
        //businessLog.info("compareHashHex start");
        long start = System.currentTimeMillis();
        ByteArrayInputStream input = new ByteArrayInputStream(source);
        byte[] digestBytes = hashUtil.RSAHashFile(input, new Mechanism("SHA-1"), BCSoftLib.INSTANCE(), false);
        String real = bytes2hex(digestBytes);
        input = null;
        long end = System.currentTimeMillis();
        //businessLog.info("compareHashHex end, cost =" + (end - start) + " ms");
        return result != null && real != null && result.equals(real);
    }

    public  List<ProofBean> getProofHashFromXml(File xmlFile) throws CodeException {
        try {
            byte[] sourceData = FileUtils.readFileToByteArray(xmlFile);
            List<ProofBean> resultList = new ArrayList();
            SAXReader sax = new SAXReader();
            Document document = sax.read(new ByteArrayInputStream(sourceData));
            List list = document.selectNodes("/ProofHashXml/Proof");
            Iterator iter = list.iterator();

            while(iter.hasNext()) {
                ProofBean proofBean = new ProofBean();
                Element element = (Element)iter.next();
                String fileName = element.attribute("fileName").getText();
                if (!fileName.contains(".pdf")) {
                    String hash = element.attribute("hash").getText();
                    proofBean.setFileName(fileName);
                    proofBean.setHash(hash);
                    resultList.add(proofBean);
                }
            }

            return resultList;
        } catch (Exception var11) {
            throw new CodeException("600646", var11);
        }
    }

    public  Map<String, String> getProofNameAndHashFromXml(File xmlFile) throws CodeException {
        try {
            byte[] sourceData = FileUtils.readFileToByteArray(xmlFile);
            Map<String, String> proofMap = new HashMap();
            SAXReader sax = new SAXReader();
            Document document = sax.read(new ByteArrayInputStream(sourceData));
            List list = document.selectNodes("/ProofHashXml/Proof");
            Iterator iter = list.iterator();

            while(iter.hasNext()) {
                ProofBean proofBean = new ProofBean();
                Element element = (Element)iter.next();
                String fileName = element.attribute("fileName").getText();
                if (!fileName.contains(".pdf")) {
                    String hash = element.attribute("hash").getText();
                    proofBean.setFileName(fileName);
                    proofBean.setHash(hash);
                    proofMap.put(fileName, hash);
                }
            }

            return proofMap;
        } catch (Exception var11) {
            throw new CodeException("600646", var11);
        }
    }
}
