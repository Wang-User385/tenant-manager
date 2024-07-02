package com.hand.hls.prj.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hls.bp.dto.HlsCusBpAttachment;
import com.hand.hls.prj.dto.SignRequestDto;
import com.hand.hls.prj.dto.SignResponseDto;
import com.hand.hls.prj.dto.SignatureSendMessageDTO;
import com.hand.hls.sign.dto.SignVerify;
import org.apache.http.util.Asserts;

/**
 * 主要包含电子签章通用的逻辑
 *
 * @author shigure 2022/11/22 10:41
 */
public interface SignService extends ProxySelf<SignService> {
    ThreadLocal<SimpleDateFormat> sdf  =
            ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd"));

    /**
     * 签章项目查询
     */
    List<Map> orgSignProjectQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize);

    /**
     * 场景签章
     */
    String sign(SignRequestDto signRequestDto);

    SignRequestDto signCompanyLongTerm(SignRequestDto signRequestDto);

    void generateProjectAttachment(Long projectId);

    JSONObject processResult(SignatureSendMessageDTO messageDTO);

    /**
     * 适用于sign_party的签约结束
     */
    JSONObject processResultNew(SignatureSendMessageDTO messageDTO);

    String encodeBase64FromUrl(String url);

    String encodeBase64FromFileSystem(String path);

    String wordToPdf(String path);

    List<SignVerify> createVerify(IRequest requestCtx, SignVerify dto);

    SignResponseDto querySign(IRequest requestCtx,String sourceDocCategory,Long sourceDocId);

    List<HlsCusBpAttachment> createVerifyAttachment(IRequest requestCtx, HlsCusBpAttachment dto);

    static String generateSignUniqueId(Long bpId){
        Asserts.notNull(bpId, "bpId");
        long millis = System.currentTimeMillis();
        return BUSINESS_TYPE + SEPARATOR + bpId + SEPARATOR + millis;
    }

    static String generateVerifyUniqueId(Long bpId){
        return generateSignUniqueId(bpId);
    }

    static String generateSignBusinessScene(String bpName){
        Asserts.notBlank(bpName, "bpName");
        return "与" + bpName + "的电子签约";
    }

    static String generateVerifyBusinessScene(String bpName){
        Asserts.notBlank(bpName, "bpName");
        return bpName + "的电子签章开户";
    }

    static String generateSignOrderNum(String bpCode){
        Asserts.notBlank(bpCode, "bpCode");
        String format = sdf.get().format(new Date());
        return BUSINESS_TYPE + SEPARATOR + bpCode + SEPARATOR + format;
    }

    static String generateVerifyOrderNum(String bpCode) {
        Asserts.notBlank(bpCode, "bpCode");
        String format = sdf.get().format(new Date());
        return BUSINESS_TYPE + SEPARATOR + bpCode + SEPARATOR + format;
    }

    /**
     * 回调接口消息返回
     */
    static JSONObject generateMessage(String message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",message);
        return jsonObject;
    }

    /**
     * 常量
     * constant field
     */

    /**
     * 业务系统
     */
    String BUSINESS_TYPE = "ZB-LBS";

    /**
     * 编号分隔符
     */
    String SEPARATOR = "-";

    /**
     * 是否需要验证码
     */
    String CODE_FLAG = "Y";

    /**
     * 是否需要人脸识别
     */
    String FACE_FLAG = "N";

    /**
     * 人脸检测认证失败，重试次数
     */
    Integer FACE_COUNT = 3;

    /**
     * 三要素/四要素认证失败，重试次数
     */
    Integer ELEMENT_VERIFY_COUNT = 3;

    /**
     * 三要素/四要素认证失败，重试次数
     */
    Integer COMPANY_VERIFY_COUNT = 3;

    String ELEMENT_TYPE = "three";

    String DIGITAL_CERTIFICATE_FLAG = "Y";

    String LEGAL_CERTIFICATE_FLAG = "Y";

    String VERIFY_DOCUMENT_TYPE = "01";

    String SIGN_INTERFACE = "/hitf/v2p/rest/invoke/R0RIQ0c6RVNJR046SktQVDAwNA==";
    String QUERY_INTERFACE = "/hitf/v2p/rest/invoke/R0RIQ0c6RVNJR046SktQVDAwNC4wMQ==";
    String COMPANY_LONG_TERM_INTERFACE = "/hitf/v2p/rest/invoke/R0RIQ0c6RVNJR046SktQVDAwNC4wNA==";
    String WORD_TO_PDF_INTERFACE = "/hitf/v2p/rest/invoke/R0RIQ0c6RVNJR046SktQVDAwNC4wNQ==";
    String COMPANY_VERIFY_INTERFACE = "/hitf/v2p/rest/invoke/R0RIQ0c6RVNJR046SktQVDAwNQ==";

    String SUCCESS_RETURN_STATUS = "S";
    String FILE_SOURCE_TYPE = "PRJ_SIGN_RECORD";
    String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";
    String PROJECT_ATTACHMENT_CATEGORY = "ONLINE_SIGN_CONTRACT";
    String PROJECT_SOURCE_DOC_CATEGORY = "PRJ_PROJECT";
    String TENANT_SIGN_OBJECT = "乙方";
    String SIGNED_STATUS = "SIGNED";
    String SIGNING_STATUS = "SIGNING";
}
