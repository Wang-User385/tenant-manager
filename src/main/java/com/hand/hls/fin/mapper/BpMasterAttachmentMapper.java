package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.BpMasterAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BpMasterAttachmentMapper extends Mapper<BpMasterAttachment>{
    List<BpMasterAttachment> queryAttachmentDetails(Map map);

    /**
     * 附件信息
     *      - app
     */
    List<BpMasterAttachment> queryForApp(@Param("bpId")Long bpId);

    List<BpMasterAttachment> queryByBpId(@Param("bpId")Long bpId,@Param("bpAttachmentCategory")String bpAttachmentCategory);

//    List<String> queryAttachCodeByBpId(@Param("bpId")Long bpId);
}
