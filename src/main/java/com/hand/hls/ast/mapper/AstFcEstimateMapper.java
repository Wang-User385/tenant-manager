//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ast.dto.AstFcEstimate;
import java.util.List;

import com.hand.hls.ast.dto.AstFiveClassRuleDetail;
import org.apache.ibatis.annotations.Param;
import uncertain.composite.CompositeMap;

public interface AstFcEstimateMapper extends Mapper<AstFcEstimate> {
    List<CompositeMap> modelQuery1(@Param("param") CompositeMap var1, @Param("whereSql") String var2);

    List<AstFcEstimate> queryAstFcEstimate(AstFcEstimate astFcEstimate);

    //
    AstFiveClassRuleDetail queryFiveClassTarget(AstFcEstimate astFcEstimate);

    AstFcEstimate calculationNowCount(@Param("id") Long id);
}
