//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.FndScoreTemplateDbSource;
import java.util.HashMap;
import java.util.List;

public interface FndScoreTemplateDbSourceMapper extends Mapper<FndScoreTemplateDbSource> {
    List<HashMap> combDs1();

    List<FndScoreTemplateDbSource> selectDbSourceName1(FndScoreTemplateDbSource var1);

    List<Long> sourceId1(Long var1);
}
