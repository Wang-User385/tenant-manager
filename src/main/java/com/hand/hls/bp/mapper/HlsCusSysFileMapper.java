//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hls.bp.dto.HlsCusSysFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusSysFileMapper extends HlsSysFileMapper<HlsCusSysFile> {
    List<HlsCusSysFile> selectFileList(@Param("sourceType") String sourceType, @Param("cashflowIdArr") String[] cashflowIdArr);
}
