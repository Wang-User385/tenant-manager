package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckAssetClass;
import com.hand.hls.hn.dto.PrjCheckBankSituation;

import java.util.List;

public interface PrjCheckAssetClassMapper extends Mapper<PrjCheckAssetClass>{
    List<PrjCheckAssetClass> queryList(PrjCheckAssetClass prjCheckAssetClass);
}