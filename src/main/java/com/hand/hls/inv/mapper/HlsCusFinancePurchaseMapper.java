package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;

import java.util.List;
import java.util.Map;

/**
 * @Description:申购mapper
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */
public interface HlsCusFinancePurchaseMapper extends Mapper<HlsCusFinancePurchase> {
    List<HlsCusFinancePurchase> selectBaseInfo(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> selectBankAccountLov(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> queryAll(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> querySameProductName(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> detailPurchaseQuery(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> purchaseCheckNewOrReturn(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> checkIsRedeemed(HlsCusFinancePurchase financePurchase);

    void updateDetailDateId(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> searchHistory(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> selectHomeFirstOneTab(HlsCusFinancePurchase financePurchase);

    List<HlsCusFinancePurchase> selectHomeFirstTwoTab(HlsCusFinancePurchase financePurchase);

    List<Map> homeChartThirdQueryAmount(Map map);

    List<Map> homeChartThirdQueryAllAmount(Map map);

    List<HlsCusFinancePurchase> selectExpiringPurchase(HlsCusFinancePurchase financePurchase);

    List<Long> selectOrgUnitUserIds(Map map);

    List<HlsCusFinancePurchase> selectHomeFirstThreeTab(HlsCusFinancePurchase financePurchase);

    //查询申购明细
    List<HlsCusFinancePurchase> queryInvPurchaseDetail(HlsCusFinancePurchase financePurchase);

    //首页产品分组查询信息
    List<HlsCusFinancePurchase> queryInvPurchaseList(HlsCusFinancePurchase financePurchase);

    //财务投资理财待办查询
    List<HlsCusFinancePurchase> queryInvDoFinance(HlsCusFinancePurchase financePurchase);

    //申购产品信息明细
    List<HlsCusFinancePurchase> queryProductsFinanceDetail(HlsCusFinancePurchase financePurchase);


    List<HlsCusFinancePurchase> queryById(HlsCusFinancePurchase financePurchase);

}