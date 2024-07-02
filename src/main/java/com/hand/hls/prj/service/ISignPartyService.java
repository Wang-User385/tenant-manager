package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.sign.dto.SignParty;

/**
 * description
 *
 * @author Lenovo 2023/07/31 10:48
 */
public interface ISignPartyService extends IBaseService<SignParty>, ProxySelf<ISignPartyService> {
    void submitSignScene(IRequest request, Long projectId, String projectAttachmentCategory, String manufacturerCode);

    void signScene(IRequest request, SignParty signParty);

    void submitSignLongTerm(IRequest request, SignParty signParty);

    void signLongTerm(IRequest request, SignParty signParty);

    /**
     * constant field
     */

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

    String CONTRACT_SIGN_TYPE = "personal_scene";

    String WORD_TYPE = "legal";

}
