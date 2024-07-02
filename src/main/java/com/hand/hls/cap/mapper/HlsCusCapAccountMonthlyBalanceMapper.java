package com.hand.hls.cap.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cap.dto.HlsCusCapAccountMonthlyBalance;

import java.util.List;
import java.util.Map;

public interface HlsCusCapAccountMonthlyBalanceMapper extends Mapper<HlsCusCapAccountMonthlyBalance> {

    /**
     * 资金账户综合管理首页GRID
     * 资金账户收付期间明细基础信息
     * 资金账户综合管理首页CHART
     */
    List<HlsCusCapAccountMonthlyBalance> capAccountQuery(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 资金账户收付期间明细 --系统生成首付明细
     */
    List<HlsCusCapAccountMonthlyBalance> accountFlowQuery(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 资金账户收付期间明细 --根据期间和账户查询月结
     */
    HlsCusCapAccountMonthlyBalance queryByPeriodAndAccount(HlsCusCapAccountMonthlyBalance dto);
    HlsCusCapAccountMonthlyBalance queryByBalanceDateAndAccount(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 资金账户收付期间明细 --根据期间和账户查询该期间之前的月结数据
     */
    List<HlsCusCapAccountMonthlyBalance> queryBeforePeriodAndAccount(HlsCusCapAccountMonthlyBalance dto);
    List<HlsCusCapAccountMonthlyBalance> queryBeforeBalanceDateAndAccount(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 资金账户收付期间明细 --根据期间和账户查询该期间之后的月结数据
     */
    List<HlsCusCapAccountMonthlyBalance> queryAfterPeriodAndAccount(HlsCusCapAccountMonthlyBalance dto);
    List<HlsCusCapAccountMonthlyBalance> queryAfterBalanceDateAndAccount(HlsCusCapAccountMonthlyBalance dto);
    HlsCusCapAccountMonthlyBalance queryBeforeMinBalanceDateAndAccount(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 资金账户收支预测grid
     */
    List<HlsCusCapAccountMonthlyBalance> queryPredictAccountStatus(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 未确认账户收支状况grid
     */
    HlsCusCapAccountMonthlyBalance queryCtPredictUnAccountStatus(HlsCusCapAccountMonthlyBalance dto);

    HlsCusCapAccountMonthlyBalance queryCxPredictUnAccountStatus(HlsCusCapAccountMonthlyBalance dto);

    HlsCusCapAccountMonthlyBalance queryWithdrawPredictUnAccountStatus(HlsCusCapAccountMonthlyBalance dto);

    HlsCusCapAccountMonthlyBalance queryRepaymentPredictUnAccountStatus(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 账户收支预测明细--租赁
     */
    List<HlsCusCapAccountMonthlyBalance> queryCtCashDetail(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 账户收支预测明细--保理
     */
    List<HlsCusCapAccountMonthlyBalance> queryCxCashDetail(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 账户收支预测明细--融资
     */
    List<HlsCusCapAccountMonthlyBalance> queryLonCashDetail(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 剩余本金分析--租赁
     */
    HlsCusCapAccountMonthlyBalance queryCtPrinciple(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 剩余本金分析--保理
     */
    HlsCusCapAccountMonthlyBalance queryCxPrinciple(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 含税应收分析
     */
    HlsCusCapAccountMonthlyBalance queryTaxPrinciple(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 融资端应付
     */
    HlsCusCapAccountMonthlyBalance queryLonPrinciple(HlsCusCapAccountMonthlyBalance dto);

    /**
     * bar-流入统计
     * 未来一个月/六个月/十二个月
     */
    List<HlsCusCapAccountMonthlyBalance> queryPressureInflowOne(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> queryPressureInflowSix(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> queryPressureInflowTwelve(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 当天的提款
     */
    HlsCusCapAccountMonthlyBalance queryTodayWithdraw(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> accountBalanceInfoQuery(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> accountBalanceGroupByBank(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> queryAmountByDay(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> queryAmountByMonth(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> queryPredictAccountStatusGroupByBank(HlsCusCapAccountMonthlyBalance dto);

    List<HlsCusCapAccountMonthlyBalance> accountReceivedAndPaid(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 租赁保理未来收支分析
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<HlsCusCapAccountMonthlyBalance> selectfeatureAmountData(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 融资端未来应付分析
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectFinanceFeatureData(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 租赁保理未来收支分析 客户系数为0 客户系数不为0
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<HlsCusCapAccountMonthlyBalance> selectFeatureBpFatorData(HlsCusCapAccountMonthlyBalance dto);



    /**
     * 资金盈缺图表数据
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<HlsCusCapAccountMonthlyBalance>  selectFinanceIncomeAndPayData(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 压力测试图表数据
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<HlsCusCapAccountMonthlyBalance> selectFinancePressureData(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 日期频率
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<String> selectDateFrequency(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 压力测试收入
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<HlsCusCapAccountMonthlyBalance>  selectPressureIncomeData(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 压力测试支出
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<HlsCusCapAccountMonthlyBalance>  selectPressurePayData(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 资金账户收支预测
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectBankAccountPredictedAmount(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 暂未确认账户收支情况
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectNotConfirmAccountPredictedAmount(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 资金账户收支预测 按账户分组
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectAccountPredictedAmount(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 租赁保理收支预测明细
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>>  selectFctConContractPredictedAmount(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 融资收支预测明细
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectLonFinancePredictedAmount(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 资金调拨收支预测明细
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>>  selectTransferPredictedAmount(HlsCusCapAccountMonthlyBalance dto);

    /**
     * 融资收支预测明细 未确认
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>>  selectNotConfirmLonPredictedAmount(HlsCusCapAccountMonthlyBalance dto);


    /**
     * 资金账户余额首页图查询
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    HlsCusCapAccountMonthlyBalance selectAccountBalanceChart(HlsCusCapAccountMonthlyBalance dto);
}