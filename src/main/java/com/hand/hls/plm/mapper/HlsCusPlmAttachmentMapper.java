package com.hand.hls.plm.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.dto.HlsCusPlmAttachment;

import java.util.List;
import java.util.Map;

public interface HlsCusPlmAttachmentMapper extends Mapper<HlsCusPlmAttachment> {
    List<HlsCusPlmAttachment> plmAttachmentDetailQuery(HlsCusPlmAttachment cusPlmAttachment);


    List<HlsCusPlmAttachment> plmAttachmentQuery(HlsCusPlmAttachment cusPlmAttachment);

    List<HlsCusPlmAttachment> plmAttachmentDetailQuery2(HlsCusPlmAttachment cusPlmAttachment);

    List<HlsCusPlmAttachment> selectAttachment(HlsCusPlmAttachment cusPlmAttachment);

    List<HlsCusPlmAttachment> queryAllFile(HlsCusPlmAttachment cusPlmAttachment);// 查询对应附件清单的所有附件


    void plmAttachmentChangeOldRemove(HlsCusPlmAttachment cusPlmAttachment);

    List<HlsCusPlmAttachment> selectMaxChangeTime(HlsCusPlmAttachment cusPlmAttachment);

    List<HlsCusPlmAttachment> selectNoDeleteAttachment(HlsCusPlmAttachment cusPlmAttachment);

    void updateAttachmentChangeIq(HlsCusPlmAttachment cusPlmAttachment);

    void deleteSysFile(HlsCusPlmAttachment attachment);

    void deleteSysAttachment(HlsCusPlmAttachment attachment);

    List<Map> plmAttachmentLeafQuery();
}
