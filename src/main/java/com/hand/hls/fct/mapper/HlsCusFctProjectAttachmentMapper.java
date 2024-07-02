package com.hand.hls.fct.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;

import java.util.List;

public interface HlsCusFctProjectAttachmentMapper extends Mapper<HlsCusFctProjectAttachment> {

    List<HlsCusFctProjectAttachment> fctProjectNoticeAttachmentDetailQuery(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);

    List<HlsCusFctProjectAttachment> queryContentFileInfo(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);

    List<HlsCusFctProjectAttachment> queryContentFileInfo2(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);
    List<HlsCusFctProjectAttachment> queryContentFileInfo3(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);
    List<HlsCusFctProjectAttachment> queryContentFileInfoNew(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);
    List<HlsCusFctProjectAttachment> queryContentFileInfo2New(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);
    List<HlsCusFctProjectAttachment> selectByCategory(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);

    void deleteHistoryPrjReport(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);

    void deleteHistoryPrjReportNotice(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);

    List<HlsCusFctProjectAttachment> queryAttachmentFileInfo(HlsCusFctProjectAttachment hlsCusFctProjectAttachment);
    List<HlsCusFctProjectAttachment> selecttAttachmentByProjectId(HlsCusFctProjectAttachment var1);
    List<HlsCusFctProjectAttachment> selecttAttachmentByTemplateId(HlsCusFctProjectAttachment var1);
    public void updateOne(HlsCusFctProjectAttachment var1);

}
