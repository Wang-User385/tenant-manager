package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineAttach;

import java.util.List;

public interface HlsCreditLineAttachMapper extends Mapper<HlsCreditLineAttach>{
    List<HlsCreditLineAttach> queryCredAttachment(HlsCreditLineAttach hlsCreditLineAttach);
    List<HlsCreditLineAttach> selectCreditChangeAttachmentInfo(HlsCreditLineAttach hlsCreditLineAttach);
}