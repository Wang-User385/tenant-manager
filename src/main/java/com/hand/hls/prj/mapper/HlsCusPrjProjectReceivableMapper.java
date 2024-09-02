package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;

import com.hand.hls.fct.dto.HlsCreditLineReceivable;
import com.hand.hls.prj.dto.HlsCusPrjProjectReceivable;

import java.util.List;

public interface HlsCusPrjProjectReceivableMapper extends Mapper<HlsCusPrjProjectReceivable>{
    /***
     * 保理项目审批应收账款明细查询
     * @param hlsCusPrjProjectReceivable
     * @return
     */
    List<HlsCusPrjProjectReceivable> findFactoringApprovalInfo(HlsCusPrjProjectReceivable hlsCusPrjProjectReceivable);
}