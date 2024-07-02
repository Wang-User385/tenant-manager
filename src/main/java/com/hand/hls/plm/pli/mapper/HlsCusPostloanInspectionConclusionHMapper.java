package com.hand.hls.plm.pli.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspectionConclusionH;

import java.util.List;

/**
 * @Description:贷后检查历史结论mapper
 * @Author: Wty
 * @Date: Created om 16:28 2018/5/24
 */
public interface HlsCusPostloanInspectionConclusionHMapper extends Mapper<HlsCusPostloanInspectionConclusionH> {
    List<HlsCusPostloanInspectionConclusionH> selectPostLoanConclusionH(HlsCusPostloanInspectionConclusionH postloanInspectionConclusionH);

    List<HlsCusPostloanInspectionConclusionH> selectPostLoanConclusionHByBpId(HlsCusPostloanInspectionConclusionH postloanInspectionConclusionH);
}