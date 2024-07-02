package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.FundFillingReqLn;
import com.hand.hls.fp.dto.JcFundFillingLn;

import java.util.List;

public interface FundFillingReqLnMapper extends Mapper<FundFillingReqLn> {
    List<FundFillingReqLn> queryAllLn(FundFillingReqLn reqLn);

    List<FundFillingReqLn> queryAllForUpdateAmount(FundFillingReqLn jcFundFillingLn);
    List<FundFillingReqLn> queryAllDetailNew(FundFillingReqLn jcFundFillingLn);
}