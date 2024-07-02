package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsSysDocumentHistoryBlob;
import com.hand.hls.sys.mapper.SysDocumentHistoryBlobMapper;
import org.apache.ibatis.annotations.Param;

public interface HlsSysDocumentHistoryBlobMapper<T extends SysDocumentHistoryBlobMapper> extends Mapper<HlsSysDocumentHistoryBlob> {

    void bpDataTransfer(@Param("changeReqId")Long changeReqId);

    void bpDeleteTransferData(@Param("changeReqId")Long changeReqId);

    void dataTransfer(@Param("changeReqId")Long changeReqId);

    void deleteTransferData(@Param("changeReqId")Long changeReqId);


}
