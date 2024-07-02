package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckGuarantMortgage;
import com.hand.hls.hn.dto.PrjCheckProjectApproveSchedule;

import java.util.List;

public interface PrjCheckGuarantMortgageMapper extends Mapper<PrjCheckGuarantMortgage>{
    List<PrjCheckGuarantMortgage> queryList(PrjCheckGuarantMortgage prjCheckGuarantMortgage);
}