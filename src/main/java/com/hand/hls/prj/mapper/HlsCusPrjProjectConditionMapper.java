package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineChanceCondition;
import com.hand.hls.prj.dto.HlsCusPrjProjectCondition;

import java.util.List;

public interface HlsCusPrjProjectConditionMapper extends Mapper<HlsCusPrjProjectCondition>{
    /***
     * 保理审批投放条件
     * @param hlsCusPrjProjectCondition
     * @return
     */
    List<HlsCusPrjProjectCondition> findFactoringApprovalCondition(HlsCusPrjProjectCondition hlsCusPrjProjectCondition);
    List<HlsCusPrjProjectCondition> queryProjectConditionByProjectId(HlsCusPrjProjectCondition hlsCusPrjProjectCondition);
}