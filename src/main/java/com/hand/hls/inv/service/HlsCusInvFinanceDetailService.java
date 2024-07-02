package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;
import com.hand.hls.inv.dto.HlsCusInvFinanceDetail;

import java.util.List;


public interface HlsCusInvFinanceDetailService extends IBaseService<HlsCusInvFinanceDetail>, ProxySelf<HlsCusInvFinanceDetailService> {

    //财务投资理财已办查询
    List<HlsCusInvFinanceDetail> queryInvDoneFinanceList(IRequest iRequest, HlsCusInvFinanceDetail hlsCusInvFinanceDetail, int page, int pageSize);

    //实际申购工作流审批
    HlsCusFinancePurchase invFinanceDetailSubmit(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    //实际赎回工作流审批
    HlsCusFinanceRedeem invFinanceDetailSubmit(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem);

    List<HlsCusInvFinanceDetail> queryInvFinanceDetal(IRequest iRequest, HlsCusInvFinanceDetail hlsCusInvFinanceDetail, int page, int pageSize);
}