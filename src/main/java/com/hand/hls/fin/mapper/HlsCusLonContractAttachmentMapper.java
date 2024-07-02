package com.hand.hls.fin.mapper;

import com.hand.hls.fin.dto.HlsCusLonContractAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusLonContractAttachmentMapper extends LonContractAttachmentMapper<HlsCusLonContractAttachment> {
    List<HlsCusLonContractAttachment> selectLonConAttachment(HlsCusLonContractAttachment lonContractAttachment);

    List<HlsCusLonContractAttachment> queryLonConWithdrowAttachment(HlsCusLonContractAttachment lonContractAttachment);

    List<HlsCusLonContractAttachment> queryLonContractAttachmentFam(HlsCusLonContractAttachment lonContractAttachment);

    List<HlsCusLonContractAttachment> lonContractAttachmentDetailQuery(HlsCusLonContractAttachment lonContractAttachment);

    int selectAttachmentCodeNullCount(@Param("contractId") Long contractId);

    Long selectOrderNumberMax(Long contractId);
    void deleteAttachmentBySourceIdAndType(HlsCusLonContractAttachment attachment);

    List<HlsCusLonContractAttachment> selectContractAndWithdrawFile(@Param("contractId") Long contractId);
}