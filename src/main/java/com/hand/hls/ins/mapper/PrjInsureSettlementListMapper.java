package com.hand.hls.ins.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ins.dto.PrjInsureSettlementList;

import java.util.List;

public interface PrjInsureSettlementListMapper extends Mapper<PrjInsureSettlementList>{
     List<PrjInsureSettlementList> queryAll(PrjInsureSettlementList prjInsureSettlementList);
}