package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.ConDebtAttachment;
import com.hand.hls.csh.dto.CshPaymentAttachment;

import java.util.List;

public interface ConDebtAttachmentMapper extends Mapper<ConDebtAttachment> {
    List<ConDebtAttachment> selectDebtAttachmentInfo(ConDebtAttachment conDebtAttachment);
}
