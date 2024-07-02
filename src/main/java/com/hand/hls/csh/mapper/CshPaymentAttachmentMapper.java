package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshPaymentAttachment;

import java.util.List;

public interface CshPaymentAttachmentMapper extends Mapper<CshPaymentAttachment>{
    List<CshPaymentAttachment> queryCshPaymentHdFirst(CshPaymentAttachment cshPaymentAttachment);
    List<CshPaymentAttachment> queryPayFileFirstNp(CshPaymentAttachment cshPaymentAttachment);
    List<CshPaymentAttachment> queryGuarantorBusinessLicense(CshPaymentAttachment cshPaymentAttachment);
    List<CshPaymentAttachment> queryOther(CshPaymentAttachment cshPaymentAttachment);
    List<CshPaymentAttachment> selectPaymentAttachmentInfo(CshPaymentAttachment cshPaymentAttachment);
    List<CshPaymentAttachment> queryPaymentAttachmentMulti(CshPaymentAttachment cshPaymentAttachment);
    List<CshPaymentAttachment> selectPaymentAttachmentInfo1(CshPaymentAttachment cshPaymentAttachment);

    List<CshPaymentAttachment> queryPaymentAttaMutliInfo(CshPaymentAttachment cshPaymentAttachment);

    /**
     * 二期功能：零售业务付款申请附件查询
     * @param cshPaymentAttachment
     * @return
     */
    List<CshPaymentAttachment> selectRetailPaymentAttachmentInfo(CshPaymentAttachment cshPaymentAttachment);

}