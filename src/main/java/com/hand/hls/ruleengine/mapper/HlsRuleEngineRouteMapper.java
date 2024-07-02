package com.hand.hls.ruleengine.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ruleengine.dto.HlsRuleEngineRoute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsRuleEngineRouteMapper extends Mapper<HlsRuleEngineRoute>{
    List<HlsRuleEngineRoute> selectRuleEngineRoute(Long ruleEngineId);
    List<HlsRuleEngineRoute> selectRuleEngineChildRoute(Long routeId);
    List<HlsRuleEngineRoute> selectRuleEnginerRouteTree(Long ruleEngineId);
    void removeRuleEngineByRouteId1(Long routeId);
    void removeRuleEngineByRouteId2(Long routeId);
    void removeRuleEngineByRouteId3(Long routeId);

    List<HlsRuleEngineRoute> selectRuleEngineRouteByFunctionCode(@Param("ruleEngineId") Long ruleEngineId, @Param("functionCode") String functionCode);

}