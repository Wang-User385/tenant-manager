package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;

import java.util.List;

public interface HlsCreditLineChanceApproverMapper extends Mapper<HlsCreditLineChanceApprover> {
    List<HlsCreditLineChanceApprover> queryAll();

    /***
     * 保理评委信息
     * @return
     */
    List<HlsCreditLineChanceApprover> findVoteInfo(HlsCreditLineChanceApprover hlsCreditLineChanceApprover);


}