package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;

import java.util.List;

public interface HlsCusHlsCreditLineChanceAttachMapper extends Mapper<HlsCusHlsCreditLineChanceAttach> {

//    List<HlsCusHlsCreditLineChanceBp> selectByForeignKey(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

//    List<HlsCusHlsCreditLineChanceBp> selectBpByMarket(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    List<HlsCusHlsCreditLineChanceAttach> queryCreditChanceAttachInfo(HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach);

    /***
     * 保理附件查询
     * @param hlsCusHlsCreditLineChanceAttach
     * @return
     */
    List<HlsCusHlsCreditLineChanceAttach> findFactoringInfo(HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach);

    List<HlsCusHlsCreditLineChanceAttach> findListHlsCusHlsCreditLineChanceAttach(HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach);
}