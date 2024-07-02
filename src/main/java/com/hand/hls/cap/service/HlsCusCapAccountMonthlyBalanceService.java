package com.hand.hls.cap.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cap.dto.HlsCusCapAccountMonthlyBalance;
import com.hand.hls.cap.dto.HlsCusCapPkg;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusCapAccountMonthlyBalanceService extends IBaseService<HlsCusCapAccountMonthlyBalance>, ProxySelf<HlsCusCapAccountMonthlyBalanceService> {
//
//    List<HlsCusCapAccountMonthlyBalance> capAccountQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) throws ParseException;
//
    List<HlsCusCapAccountMonthlyBalance> accountFlowQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);
//
    HlsCusCapAccountMonthlyBalance queryByPeriodAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto);
    HlsCusCapAccountMonthlyBalance queryByBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> queryBeforePeriodAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto);
    List<HlsCusCapAccountMonthlyBalance> queryBeforeBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> queryAfterPeriodAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto);
    List<HlsCusCapAccountMonthlyBalance> queryAfterBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//    HlsCusCapAccountMonthlyBalance queryBeforeMinBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    HlsCusCapAccountMonthlyBalance queryBalanceInfo(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
    HlsCusCapAccountMonthlyBalance monthConfirm(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
    String monthCal(String period, int amount) throws ParseException;
//
//    HlsCusCapPkg balanceSave(IRequest request, HlsCusCapPkg hlsCusCapPkg);
    HlsCusCapPkg dayBalanceSave(IRequest request, HlsCusCapPkg hlsCusCapPkg);

    HlsCusCapAccountMonthlyBalance queryBeforeMinBalanceDateAndAccount(IRequest request, HlsCusCapAccountMonthlyBalance monthlyBalance);
//
//    List<Map> accountChartQuery(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> queryPredictAccountStatus(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize) throws ParseException;
//
//    List<HlsCusCapAccountMonthlyBalance> queryPredictUnAccountStatus(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> queryCtCxCashDetail(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);
//
//    List<HlsCusCapAccountMonthlyBalance> queryLonCashDetail(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);
//
//    List<HlsCusCapAccountMonthlyBalance> pressureQueryCt(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> pressureQueryCx(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> pressureQueryTax(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> pressureQueryLon(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    List<HlsCusCapAccountMonthlyBalance> pressureQuery(IRequest request, HlsCusCapAccountMonthlyBalance dto, String type, String businessType[]);
//
//    List<HlsCusCapAccountMonthlyBalance> queryPressureFlow(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//    void insertInFlowFromOther(IRequest request, Long bankAccountId, Double amount, Date date);
//
//    void insertOutFlowFromOther(IRequest request, Long bankAccountId, Double amount, Date date);
//
//    List<HlsCusCapAccountMonthlyBalance> queryAmountByFrequency(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//    List<HlsCusCapAccountMonthlyBalance> accountReceivedAndPaid(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//
//    /**
//     * 租赁保理未来收支分析
//     * add by yuan.yuan01@hand.china.com
//     * @param request
//     * @param dto
//     * @param page
//     * @param pageSize
//     * @return
//     */
//    List<HlsCusCapAccountMonthlyBalance> selectfeatureAmountData(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);
//
//
//    /**
//     * 租赁保理未来收支分析导出
//     * add by yuan.yuan01@hand.china.com
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportfeatureAmountData(HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);
//
//
//    /**
//     * 融资端未来应付分析
//     * @param request
//     * @param dto
//     * @param page
//     * @param pageSize
//     * @return
//     */
//    List<Map<String,Object>> selectFinanceFeatureData(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);
//
//
//    /**
//     * 融资端未来应付分析导出
//     * add by yuan.yuan01@hand.china.com
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportfeatureFinanceData(HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);
//
//
//
//    /**
//     * 租赁保理未来收支分析 客户系数为0 客户系数不为0
//     * add by yuan.yuan01@hand.china.com
//     * @param dto
//     * @return
//     */
//    List<HlsCusCapAccountMonthlyBalance> selectFeatureBpFatorData(IRequest request, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);
//
//
//
//
//    /**
//     * 租赁保理未来收支分析导出 客户系数为0 或 客户系数不为0
//     * add by yuan.yuan01@hand.china.com
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportfeatureFactorData(HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);
//
//
//
//    /**
//     * 资金盈缺图表数据
//     * add by yuan.yuan01@hand.china.com
//     * @param dto
//     * @return
//     */
//    List<HlsCusCapAccountMonthlyBalance>  selectFinanceIncomeAndPayData(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//
//
//    /**
//     * 压力测试图表数据
//     * add by yuan.yuan@hand.china.com
//     * @param dto
//     * @return
//     */
//    List<HlsCusCapAccountMonthlyBalance> selectFinancePressureData(IRequest request, HlsCusCapAccountMonthlyBalance dto);
//
//
//    /**
//     * 压力测试报表导出
//     * add by yuan.yuan01@hand.china.com
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportFinancePressureData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);


    /**
     * 资金账户收支预测
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectBankAccountPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);



    /**
     * 暂未确认账户收支情况
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectNotConfirmAccountPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto);


//    /**
//     * 资金账户收支预测 导出
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportBankPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);


    /**
     * 资金账户收支预测 按账户分组
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectAccountPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);


//    /**
//     * 资金账户收支预测 按账户分组 导出
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportAccountPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);


    /**
     * 租赁保理收支预测明细
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>>  selectFctConContractPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);


//
//    /**
//     * 租赁保理收支预测明细 导出
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportFctConPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);



    /**
     * 融资收支预测明细
     * add by yuan.yuan01@hand.china.com
     * @param dto
     * @return
     */
    List<Map<String,Object>> selectLonFinancePredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);



//    /**
//     * 融资收支预测明细 导出
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportLonPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);
//
//
//    /**
//     * 资金调拨收支预测明细
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param dto
//     * @param page
//     * @param pageSize
//     * @return
//     */
//    List<Map<String,Object>>  selectTransferPredictedAmount(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto, int page, int pageSize);
//
//
//    /**
//     * 资金调拨收支预测明细 导出
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportTransferPredictData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);
//
//
//    /**
//     * 资金账户余额首页图查询
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param dto
//     * @return
//     */
//    List<Map<String,Object>> selectAccountBalanceChart(IRequest iRequest, HlsCusCapAccountMonthlyBalance dto);
//
//
//    /**
//     * 资金账户余额 导出
//     * add by yuan.yuan01@hand.china.com
//     * @param iRequest
//     * @param request
//     * @param response
//     * @param dto
//     */
//    void exportAccountBalanceData(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusCapAccountMonthlyBalance dto);


//    void exportOtherPlan(HttpServletRequest request, HttpServletResponse response, HlsCusCapitalOtherPlan dto);

}
