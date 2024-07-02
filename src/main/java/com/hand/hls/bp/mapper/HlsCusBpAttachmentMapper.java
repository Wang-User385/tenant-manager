package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpAttachment;

import java.util.List;
import java.util.Map;

public interface HlsCusBpAttachmentMapper extends Mapper<HlsCusBpAttachment> {
    List<HlsCusBpAttachment> bptAttachmentDetailQuery(HlsCusBpAttachment hlsCusBpAttachment);

    List<HlsCusBpAttachment> queryAllBpFileDocumentListName(HlsCusBpAttachment hlsCusBpMasterAttachment);// 查询商业伙伴所有附件的附件青清单

    List<HlsCusBpAttachment> queryAllFile(HlsCusBpAttachment hlsCusBpMasterAttachment);// 查询对应附件清单的所有附件

    void deleteFileByFileId(Long fileId);

    List<Map<String,Object>> queryFileInfoByTablePkValue(String tablePkValue);
}