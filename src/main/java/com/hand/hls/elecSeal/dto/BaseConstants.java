//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;

import java.nio.charset.Charset;
import java.util.Locale;

public class BaseConstants {
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String ISO_CHARSET = "iso-8859-1";
    public static final Charset CHARSET_DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final Locale DEFAULT_LOCALE;
    public static final String GUID_TIME_FORMAT = "yyyyMMddHHmmssSSS";
    public static final String CONSTANT_VALUE_SUFFIX_PNG = "png";
    public static final String COLOR_PREFIX = "#";
    public static final String CODE_SUCCESS = "000000";
    public static final String MESSAGE_SUCCESS = "successfully";
    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";
    public static final String ENVIRONMENT_LOG = "environmentLogger";
    public static final String LOGBACK_CONFIG_NAME = "logback.xml";
    public static final String CFCA_LOGBACK_CONFIG_NAME = "logback-cfca.xml";
    public static final String TRACE_ID = "TraceId";
    public static final String CLIENT_IP = "ClientIp";
    public static final String SERVER_IP = "ServerIp";
    public static final String COLON = ":";
    public static final String SPOT = ".";
    public static final String SYNTHESIZE_TYPE_COORDINATE = "2";
    public static final String SYNTHESIZE_TYPE_KEYWORD = "3";
    public static final String SEAL_TYPE_PDF = "1";
    public static final String SEAL_TYPE_OFD = "2";
    public static final String SEAL_SIGN_TYPE_BLANK = "1";
    public static final String SEAL_SIGN_TYPE_COORDINATE = "2";
    public static final String SEAL_SIGN_TYPE_KEYWORD = "3";
    public static final String SEAL_TYPE_SEAL = "1";
    public static final String SEAL_TYPE_IMAGE = "2";
    public static final String SEAL_TYPE_TEXT = "3";
    public static final String HASHALG_SHA1 = "SHA-1";
    public static final String HASHALG_SHA256 = "SHA-256";
    public static final String HASHALG_SHA384 = "SHA-384";
    public static final String HASHALG_SHA512 = "SHA-512";
    public static final String HASHALG_SM3 = "SM3";
    public static final String HASHALG_MD5 = "MD5";
    public static final String KEY_ALG_RSA = "RSA";
    public static final String KEY_ALG_SM2 = "SM2";
    public static final String KEY_ALG_LENGTH_256 = "256";
    public static final String KEY_ALG_LENGTH_1024 = "1024";
    public static final String KEY_ALG_LENGTH_2048 = "2048";
    public static final String KEY_ALG_LENGTH_4096 = "4096";
    public static final String INPUT_TYPE_FILEPATH = "1";
    public static final String INPUT_TYPE_FILEDATA = "2";
    public static final String INPUT_TYPE_TEMPLATE = "3";
    public static final String TIME_STAMP_CHANNEL_CFCA = "0";
    public static final String TIME_STAMP_CHANNEL_LOCAL = "1";
    public static final String SCEND_CERT_CHANNEL_REAL = "0";
    public static final String SCEND_CERT_CHANNEL_PRE = "1";
    public static final String SEAL_VERIFY_TYPE_1 = "1";
    public static final String SEAL_VERIFY_TYPE_2 = "2";
    public static final String LEFT_STRIPE = "5";
    public static final String RIGHT_STRIPE = "6";
    public static final String UP_STRIPE = "7";
    public static final String TEMPLATE_FORMAT_PDF = "pdf";
    public static final String TEMPLATE_FORMAT_DOCX = "docx";
    public static final String FIELDTYPE_TEXT = "text";
    public static final String FIELDTYPE_CHECKBOX = "checkbox";
    public static final String TEMPLAET_OPERATION_ADD = "1";
    public static final String TEMPLATE_OPERATION_UPDATE = "3";
    public static final String CONSTANT_VALUE_0PLUS = "0+";
    public static final String CONSTANT_VALUE_MINUS0 = "-0";
    public static final String CONSTANT_VALUE_MINUS = "-";
    public static final String POSTIVEINTEGER = "+";
    public static final String FIELD_TYPE_TEXT = "text";
    public static final String FIELD_TYPE_CHECKBOX = "checkbox";
    public static final String CERT_LEVEL_1 = "1";
    public static final String CERT_LEVEL_2 = "2";
    public static final String CONSTANT_VALUE_0 = "0";
    public static final String CONSTANT_VALUE_1 = "1";
    public static final String CONSTANT_VALUE_2 = "2";
    public static final String CONSTANT_VALUE_3 = "3";
    public static final String CONSTANT_VALUE_4 = "4";
    public static final String CONSTANT_VALUE_5 = "5";
    public static final String CONSTANT_VALUE_6 = "6";
    public static final String CONSTANT_VALUE_7 = "7";
    public static final String CONSTANT_VALUE_8 = "8";
    public static final String CONSTANT_VALUE_21 = "21";
    public static final String CONSTANT_VALUE_11 = "11";
    public static final String CONSTANT_VALUE_00 = "00";
    public static final String CONSTANT_VALUE_10 = "10";
    public static final String CONSTANT_VALUE_1024 = "1024";
    public static final String DATABASE_SEAL_CERT_ALIAS = "印章证书";
    public static final String DATABASE_SEAL_ALIAS = "印章";
    public static final String DATABASE_OPERATOR_ALIAS = "柜员";
    public static final String DATABASE_SEAL_IMAGE_ALIAS = "印模";
    public static final String SIGN_BY_HASH = "1";
    public static final String SIGN_BY_MESSAGE = "2";
    public static final String ASPOSE_LIC_PASSWORD = "cfcaAsposePwd-1234";
    public static String logbackPath;
    public static int sealImageDpi;

    public BaseConstants() {
    }

    static {
        DEFAULT_LOCALE = Locale.CHINA;
        logbackPath = "";
        sealImageDpi = 288;
    }

    public interface VerifyLevel {
        int verifySignature = 1;
        int verifyCertChain = 2;
        int verifyCRL = 4;
        int verifyKeyUsage = 8;
    }
}
