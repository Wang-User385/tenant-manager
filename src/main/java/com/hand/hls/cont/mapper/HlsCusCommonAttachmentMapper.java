package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusCommonAttachment;

import java.util.List;
import java.util.Map;

public interface HlsCusCommonAttachmentMapper extends Mapper<HlsCusCommonAttachment>{

    /**
     * 查询关税资料清单
     * @param map
     * @return
     */
    List<HlsCusCommonAttachment> queryContractTariffAttachmentInfo(Map map);
    List<HlsCusCommonAttachment> queryCshPaymentAttachmentInfo(Map map);

}