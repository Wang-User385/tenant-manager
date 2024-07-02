package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.FundFillingReqDetail;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.dto.JcFundFillingDetail;
import com.hand.hls.fp.dto.JcFundFillingLn;

import java.util.List;

public interface JcFundFillingDetailService extends IBaseService<JcFundFillingDetail>, ProxySelf<JcFundFillingDetailService> {
    List<JcFundFillingDetail> selectDetailAll(IRequest iRequest, JcFundFillingDetail jcFundFillingDetail, int page, int pageSize);

    List<JcFundFillingDetail> updateFundLnAmount(IRequest iRequest, JcFundFillingDetail detail);

    List<FundFillingReqDetail> updateReqFundLnAmount(IRequest iRequest, FundFillingReqDetail detail);

    List<JcFundFillingDetail> updateSummaryAmount(IRequest iRequest, JcFundFillingDetail detail);


}