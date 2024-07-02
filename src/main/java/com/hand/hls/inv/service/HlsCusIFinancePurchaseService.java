package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @Description:申购service
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */
public interface HlsCusIFinancePurchaseService extends IBaseService<HlsCusFinancePurchase>, ProxySelf<HlsCusIFinancePurchaseService> {
    HlsCusFinancePurchase invPurchaseSave(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    List<HlsCusFinancePurchase> selectBaseInfo(IRequest request, HlsCusFinancePurchase dto, HttpSession session, int page, int pageSize);

    List<HlsCusFinancePurchase> queryAll(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize);

    List<HlsCusFinancePurchase> querySameProductName(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize);

    List<HlsCusFinancePurchase> homeThirdQuery(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize);

    List<HlsCusFinancePurchase> detailPurchaseQuery(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize);

    List<HlsCusFinancePurchase> purchaseCheckNewOrReturn(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize);

    List<HlsCusFinancePurchase> purchaseSubmitWfl(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    List<HlsCusFinancePurchase> checkIsRedeemed(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    List<HlsCusFinancePurchase> searchHistory(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase, int page, int pageSize);

    List<HlsCusFinancePurchase> selectHomeFirstOneTab(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    List<HlsCusFinancePurchase> selectHomeFirstTwoTab(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    List<HlsCusFinancePurchase> integratedQuery(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase, int page, int pageSize);

    HlsCusFinancePurchase purchaseInvalidSubmit(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    HlsCusFinancePurchase purchaseChangesSubmit(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    //申购金额变更审批
    List<HlsCusFinancePurchase> purchaseChangesSubmitWfl(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    List<Map> homeChartThirdQueryAmount(IRequest iRequest, Map map);

    List<HlsCusFinancePurchase> selectHomeFirstThreeTab(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase);

    //查询申购明细
    List<HlsCusFinancePurchase> queryInvPurchaseDetail(IRequest iRequest, HlsCusFinancePurchase financePurchase);

    //查询申购明细
    List<HlsCusFinancePurchase> queryInvPurchaseList(IRequest iRequest, HlsCusFinancePurchase financePurchase, int page, int pageSize);

    //财务投资理财待办查询
    List<HlsCusFinancePurchase> queryInvDoFinance(IRequest iRequest, HlsCusFinancePurchase financePurchase, int page, int pageSize);

    //申购产品信息明细
    List<HlsCusFinancePurchase> queryProductsFinanceDetail(IRequest iRequest, HlsCusFinancePurchase financePurchase);
    //根据ID查询申购产品信息
    List<HlsCusFinancePurchase> queryById(IRequest iRequest, HlsCusFinancePurchase financePurchase);

}