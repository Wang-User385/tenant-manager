//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.office.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import org.apache.ibatis.annotations.Param;

public interface FndAtmAttachmentMapper extends Mapper<FndAtmAttachmentDto> {
    FndAtmAttachmentDto selectAttachmentByTbNameAndTemCode(@Param("tableName") String var1, @Param("templetCode") String var2);

    FndAtmAttachmentDto selectByCodeAndPkValue(@Param("sourceTypeCode") String var1, @Param("sourcePkValue") String var2);

    //更新文件uuid
    int updateUUIDToFndAtmAttachment(@Param("attachmentId") String param1,@Param("UUID") String param2);
    //通过uuid查找到附件
    FndAtmAttachmentDto queryAttachmentByUUID(@Param("UUID") String param);
}
