package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.FundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hn.dto.PrjCheck;

import java.util.List;

public interface PrjCheckMapper extends Mapper<PrjCheck>{
    List<PrjCheck> queryList(PrjCheck prjCheck);
    List<PrjCheck> queryListNew(PrjCheck prjCheck);
    List<PrjCheck> queryLastCheck(PrjCheck prjCheck);
    List<PrjCheck> queryManufacturer(PrjCheck prjCheck);
    List<PrjCheck> queryCheckAll();
    List<PrjCheck> queryRetailCheck(PrjCheck prjCheck);
}