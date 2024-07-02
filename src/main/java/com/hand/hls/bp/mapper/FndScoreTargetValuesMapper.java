//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.FndScoreTargetValues;
import java.util.List;
import java.util.Map;

public interface FndScoreTargetValuesMapper extends Mapper<FndScoreTargetValues> {
    List<FndScoreTargetValues> getTargetValueObj1(Map var1);
    List<FndScoreTargetValues> getTargetValueObj2(Map var1);
    List<FndScoreTargetValues> findScoreTargetValueList(FndScoreTargetValues fndScoreTargetValues);
}
