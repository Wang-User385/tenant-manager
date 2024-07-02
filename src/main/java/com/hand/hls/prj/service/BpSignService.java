package com.hand.hls.prj.service;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.prj.dto.CompanyAuthMessageDTO;
import com.hand.hls.prj.dto.SignKeywordLocationDto;
import com.hand.hls.prj.dto.SignPersonDto;
import com.hand.hls.prj.dto.SignRequestDto;
import com.hand.hls.sign.dto.SignVerify;
import org.apache.http.util.Asserts;

/**
 * 商业伙伴签章相关方法，定义了签章请求用到的常量配置，以及实现了场景签章和长期签章的逻辑
 *
 * @author shigure 2022/11/23 10:05
 */
public interface BpSignService {

    List<SignVerify> verify(IRequest requestCtx, SignVerify dto);

    /**
     * 认证结束处理方法
     */
    JSONObject verifyFinish(IRequest requestCtx, CompanyAuthMessageDTO messageDTO);

    /**
     * 场景电子签章
     */
    void sign(HlsCusBpMaster bpMaster, Long sourceDocId, String sourceDocCategory, IRequest request,String signObject);

    /**
     * 企业长期签章
     */
    void longTermSign(HlsCusBpMaster bpMaster, Long sourceDocId, String sourceDocCategory, IRequest request,String keyword);

    /**
     * 拼接签约请求体
     */
    default SignRequestDto buildSignRequest(HlsCusBpMaster bpMaster){
        String uniqueIdentification = SignService.generateSignUniqueId(bpMaster.getBpId());
        String businessScene = SignService.generateSignBusinessScene(bpMaster.getBpName());
        String orderNum = SignService.generateSignOrderNum(bpMaster.getBpCode());
        SignRequestDto request = new SignRequestDto();
        request.setUniqueIdentification(uniqueIdentification);
        request.setBusinessType(SignService.BUSINESS_TYPE);
        request.setBusinessScene(businessScene);
        request.setSignatureMethod(SIGNATURE_METHOD);
        request.setVerifyCount(VERIFY_COUNT);
        request.setSignerVerifyFlag(SIGNER_VERIFY_FLAG);
        request.setOrderNum(orderNum);
        return request;
    }

    default SignPersonDto buildSignPerson(HlsCusBpMaster bpMaster) {
        SignPersonDto signPersonDto = new SignPersonDto();
        signPersonDto.setElementType(SignService.ELEMENT_TYPE);
        signPersonDto.setDocumentType(DOCUMENT_TYPE);
        Asserts.notBlank(bpMaster.getIdCardNo(),"商业伙伴银行卡号");
        signPersonDto.setIdNumber(bpMaster.getIdCardNo());
        Asserts.notBlank(bpMaster.getPhone(),"联系人手机号");
        signPersonDto.setPhoneNumber(bpMaster.getPhone());
        signPersonDto.setReceivePhoneNumber(bpMaster.getPhone());
        //系统银行账户不唯一，且接口非必填，暂时不取
//        signPerson.setBankCardNumber(bpMaster.getBankAccountNum());
        signPersonDto.setCodeFlag(SignService.CODE_FLAG);
        signPersonDto.setFaceFlag(SignService.FACE_FLAG);
        signPersonDto.setFaceCount(SignService.FACE_COUNT);
        signPersonDto.setElementVerifyCount(SignService.ELEMENT_VERIFY_COUNT);
        return signPersonDto;
    }

    default SignKeywordLocationDto buildKeywordLocation(String keyword, String wordType){
        SignKeywordLocationDto location = new SignKeywordLocationDto();
        location.setPageNo("");
        location.setKeyword(keyword);
        location.setKeywordPositionIndex(KEYWORD_POSITION_INDEX);
        location.setOffsetX(OFFSET_X);
        location.setOffsetY(OFFSET_Y);
        location.setWordType(wordType);
        return location;
    }

    default String buildSignKeyword(String signObject){
        return signObject+"（盖章）";
    }

    /**
     * constant field
     */

    //TODO 正式使用需要删除
    String DEFAULT_FILE = "signsign@test.pdf";

    String DEFAULT_REASON = "合同电子签约";

    String DEFAULT_STATUS = "NEW";

    String SIGNATURE_METHOD = "SMS_CAPTCHA";

    /**
     * 校验失败重试次数
     */
    Integer VERIFY_COUNT = 3;

    /**
     * 签署人是否进行实名认证
     */
    String SIGNER_VERIFY_FLAG = "Y";

    /**
     * 证件类型（暂只支持身份证，传01）
     */
    String DOCUMENT_TYPE = "01";


    /**
     * 关键字索引 0：默认全部位置盖章
     */
    String KEYWORD_POSITION_INDEX = "0";
    /**
     * 签章X轴偏移量
     */
    String OFFSET_X = "70";
    /**
     * 签章Y轴偏移量
     */
    String OFFSET_Y = "";

    String VERIFYING_STATUS = "VERIFYING";

    String SUCCESS_STATUS = "SUCCESS";
}
