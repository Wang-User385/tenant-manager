package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsSysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import org.apache.ibatis.annotations.Param;

public interface HlsSysDocumentHistoryDetailMapper<T extends SysDocumentHistoryDetailMapper> extends Mapper<HlsSysDocumentHistoryDetail> {
    void bpDataTransfer(@Param("changeReqId")Long changeReqId);

    void bpDeleteTransferData(@Param("changeReqId")Long changeReqId);

    void dataTransfer(@Param("changeReqId")Long changeReqId);

    void deleteTransferData(@Param("changeReqId")Long changeReqId);

}
