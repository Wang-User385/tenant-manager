package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonCreditContractAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusLonCreditContractAttachmentMapper extends Mapper<HlsCusLonCreditContractAttachment> {
    List<HlsCusLonCreditContractAttachment> lonCreditContractAttachmentDetailQuery(HlsCusLonCreditContractAttachment HlsCusLonCreditContractAttachment);

    int selectAttachmentCodeNullCount(@Param("creditContractId") Long creditContractId);
}
