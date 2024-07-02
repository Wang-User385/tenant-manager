package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fp.dto.FundingExecute;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.dto.JcFundFillingDetail;

import java.util.List;

public interface JcFundFillingService extends IBaseService<JcFundFilling>, ProxySelf<JcFundFillingService> {
    List<JcFundFillingDetail> initYear(IRequest iRequest, JcFundFilling filling);

    List<JcFundFilling> queryAllByUnit(IRequest iRequest, JcFundFilling filling, int page, int pageSize);



    List<JcFundFilling> querySummaryAllByUnit(IRequest iRequest, JcFundFilling filling, int page, int pageSize);

    List<JcFundFillingDetail> initQuarter(IRequest iRequest, JcFundFilling filling);

    List<JcFundFillingDetail> initMon(IRequest iRequest, JcFundFilling filling);

    List<JcFundFillingDetail> initWeek(IRequest iRequest, JcFundFilling filling);

    JcFundFilling fillingSubmitWfl(IRequest iRequest, JcFundFilling filling) throws HlsCusException;

    JcFundFilling fillingSummarySubmitWfl(IRequest iRequest, JcFundFilling filling) throws HlsCusException;

    List<JcFundFillingDetail> initSummaryYear(IRequest request, JcFundFilling filling);

    List<JcFundFillingDetail> initSummaryQuarter(IRequest request, JcFundFilling filling);

    List<JcFundFillingDetail> initSummaryMon(IRequest request, JcFundFilling filling);

    List<JcFundFillingDetail> initSummaryWeek(IRequest request, JcFundFilling filling);

    void initExecutInit(IRequest request, JcFundFilling filling, String summaryFlag);

    void initExecut(IRequest request, FundingExecute execute, String summaryFlag);

    void fundIncome(IRequest request, FundingExecute execute, String summaryFlag);

    void fundExpenditure(IRequest request, FundingExecute execute, String summaryFlag);

    List<JcFundFilling> queryAllByUnitReq(IRequest iRequest, JcFundFilling filling, int page, int pageSize);

    JcFundFilling createReq(IRequest iRequest , JcFundFilling fillingReq)throws Exception;

    JcFundFilling fillingReqSubmitWfl(IRequest iRequest, JcFundFilling filling) throws HlsCusException;
}