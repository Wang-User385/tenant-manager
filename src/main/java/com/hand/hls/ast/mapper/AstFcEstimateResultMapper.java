//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ast.dto.AstFcEstimateResult;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import uncertain.composite.CompositeMap;

public interface AstFcEstimateResultMapper extends Mapper<AstFcEstimateResult> {
    List<CompositeMap> queryByRequestData1(@Param("map") CompositeMap var1);

    List<AstFcEstimateResult> queryAstFcEstimateResult(AstFcEstimateResult astFcEstimateResult);
}
