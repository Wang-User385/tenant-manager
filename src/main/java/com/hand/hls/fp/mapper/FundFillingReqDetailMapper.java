package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.FundFillingReqDetail;
import com.hand.hls.fp.dto.JcFundFillingDetail;

import java.util.List;

public interface FundFillingReqDetailMapper extends Mapper<FundFillingReqDetail> {
    void deleteFillingDetail(FundFillingReqDetail detail);

    List<FundFillingReqDetail> queryAllNew(FundFillingReqDetail jcFundFillingDetail);

    List<FundFillingReqDetail> queryAllByLnId(FundFillingReqDetail jcFundFillingDetail);

    List<FundFillingReqDetail> queryAllReaDetail(FundFillingReqDetail jcFundFillingDetail);

    List<FundFillingReqDetail> queryAll(FundFillingReqDetail jcFundFillingDetail);

    List<FundFillingReqDetail> queryDetail(FundFillingReqDetail jcFundFillingDetail);

    List<FundFillingReqDetail> queryDetailWeek(FundFillingReqDetail jcFundFillingDetail);

    FundFillingReqDetail queryDetailTotalSec(FundFillingReqDetail jcFundFillingDetail);

    FundFillingReqDetail queryDetailSxKyh(FundFillingReqDetail jcFundFillingDetail);

    FundFillingReqDetail queryDetailSxBlh(FundFillingReqDetail jcFundFillingDetail);

    FundFillingReqDetail queryDetailTjKyh(FundFillingReqDetail jcFundFillingDetail);

    FundFillingReqDetail queryDetailTjBlh(FundFillingReqDetail jcFundFillingDetail);

    FundFillingReqDetail queryDetailTotalInflow(FundFillingReqDetail jcFundFillingDetail);

    FundFillingReqDetail queryDetailTotalOutflow(FundFillingReqDetail jcFundFillingDetail);
}