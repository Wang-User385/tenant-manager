//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.FndScoreTemplateLn;
import java.util.List;
import java.util.Map;

public interface FndScoreTemplateLnMapper extends Mapper<FndScoreTemplateLn> {
    List<FndScoreTemplateLn> select(FndScoreTemplateLn var1);

    List<FndScoreTemplateLn> selectLevelOne(FndScoreTemplateLn var1);

    List<FndScoreTemplateLn> queryScoreTemplateLn1(FndScoreTemplateLn var1);

    List<FndScoreTemplateLn> queryScoreTemplateLn2(Map map);

    List<FndScoreTemplateLn> selectLnRoot1(Long var1);
}
