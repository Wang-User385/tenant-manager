package com.hand.hls.risk.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.risk.dto.RiskAttachment;

import java.util.List;

public interface RiskAttachmentMapper extends Mapper<RiskAttachment>{
    List<RiskAttachment> queryCredAttachment(RiskAttachment riskAttachment);

    List<RiskAttachment> queryPrjCheckAttachment(PrjCheck prjCheck);
}