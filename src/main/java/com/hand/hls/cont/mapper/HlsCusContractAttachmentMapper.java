package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusContractAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusContractAttachmentMapper extends Mapper<HlsCusContractAttachment> {
    List<HlsCusContractAttachment> queryContractAttachment(HlsCusContractAttachment hlsCusContractAttachment);

    List<HlsCusContractAttachment> selectCshDocByContractIdAndCategory(HlsCusContractAttachment hlsCusContractAttachment);

    List<HlsCusContractAttachment> contractAttachmentLendDocumentListQuery(HlsCusContractAttachment hlsCusContractAttachment);
    /**
     * 罚息附件
     * @param hlsCusContractAttachment
     * @return
     */
    List<HlsCusContractAttachment>  selectContractFineAttachment(HlsCusContractAttachment hlsCusContractAttachment);

    List<HlsCusContractAttachment> selectContractEndFileByContractId(HlsCusContractAttachment hlsCusContractAttachment);

    List<HlsCusContractAttachment> queryContractAttachmentDetail(Map map);

    List<HlsCusContractAttachment> queryChangeAttachmentByCategory(HlsCusContractAttachment hlsCusContractAttachment);

    List<Map<String, Object>> queryAttachmentForContractChange(@Param("tableName") String tableName, @Param("tablePkValue")String tablePkValue);
    Map<String, Object> queryAttachmentFile(@Param("attachment_id")Long attachment_id);

}