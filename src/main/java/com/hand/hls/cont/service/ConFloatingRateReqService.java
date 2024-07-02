package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/16 - 14:14
 */
public interface ConFloatingRateReqService extends IBaseService<HlsCusConFloatingRateReq>, ProxySelf<ConFloatingRateReqService> {
    List<HlsCusConFloatingRateReq> queryConFloatingRateReq(IRequest var1, HlsCusConFloatingRateReq var2, int var3, int var4);

    ResponseData submitFloatingRateWfl(IRequest var1, HlsCusConFloatingRateReq var2);

    //查询根据新的prjQuotationCashflow 是否能找到旧的con_contract_cashflow
    String confirmOldCashflow(IRequest requestContext, HlsCusConFloatingRateReq hlsCusConFloatingRateReq);

}
