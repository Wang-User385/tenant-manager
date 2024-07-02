package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;

import java.util.List;


public interface HlsCusConFloatingRateReqLnService extends IBaseService<HlsCusConFloatingRateReqLn>, ProxySelf<HlsCusConFloatingRateReqLnService> {
    HlsCusConFloatingRateReq createFloatingRateChange(IRequest request, HlsCusConFloatingRateReq floatingRateReq) throws HlsCusException;

    /*调息明细页面-调息列表查询*/
    List<HlsCusConFloatingRateReqLn> rateChangeListQuery(IRequest requestContext, HlsCusConFloatingRateReq dto, int page, int pageSize);

    HlsCusConFloatingRateReqLn rateChangeDetailQuery(IRequest irequest, HlsCusConFloatingRateReqLn dto);

    HlsCusConFloatingRateReqLn ctRateChangeTotalQuery(IRequest irequest, HlsCusConFloatingRateReq dto);

//    void floatingRate(IRequest request, HlsCusConFloatingRateReq cusConFloatingRateReq);

    void ctCancelFloatingRateReq(IRequest iRequest, @StdWho HlsCusConFloatingRateReq floatingRateReq);

    void deleteFltReqLn(IRequest requestContext, List<HlsCusConFloatingRateReqLn> conFloatingRateReqLnList);

    void confirmFltReq(IRequest request, HlsCusConFloatingRateReq conFloatingRateReq) throws HlsCusException;


    Long copyWithdrawRepayment(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, String usageCode, String historyReason, Long changeReqId) throws HlsCusException;

    List<HlsCusConFloatingRateReq> queryBaseRateSet(IRequest iRequest,HlsCusConFloatingRateReq sysUserAuthorityRule, int pageNum, int pageSize);

}