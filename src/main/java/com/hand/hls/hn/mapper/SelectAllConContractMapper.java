package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConContract;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.hn.dto.CheckPlanConContract;
import com.hand.hls.hn.dto.PrjCheck;

import java.util.List;

public interface SelectAllConContractMapper extends Mapper<CheckPlanConContract>{
    List<CheckPlanConContract> queryAllApproveContract(CheckPlanConContract checkPlanConContract);
}