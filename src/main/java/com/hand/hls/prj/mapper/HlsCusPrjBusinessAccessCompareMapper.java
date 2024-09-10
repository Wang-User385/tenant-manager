package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.prj.dto.HlsCusPrjBusinessAccessCompare;

import java.util.List;


public interface HlsCusPrjBusinessAccessCompareMapper extends Mapper<HlsCusPrjBusinessAccessCompare>{
    /***
     * 保理业务审核信息
     * @param hlsCusPrjBusinessAccessCompare
     * @return
     */
    List<HlsCusPrjBusinessAccessCompare> findFactoringApprovalInfo(HlsCusPrjBusinessAccessCompare hlsCusPrjBusinessAccessCompare);
    List<HlsCusPrjBusinessAccessCompare> findProjectAccessInfo(HlsCusPrjBusinessAccessCompare hlsCusPrjBusinessAccessCompare);

    List<HlsCusPrjBusinessAccessCompare> findPrjFactoringApprovalCompare();
}