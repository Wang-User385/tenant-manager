package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusLonContractQuotation;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.exception.HlsCusAmountOverException;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusLonContractWithdrawService extends IBaseService<HlsCusLonContractWithdraw>, ProxySelf<HlsCusLonContractWithdrawService> {

    /**
     * 债务情况表
     *
     *
     * @param request 请求参数
     * @param map 查询参数集合
     * @return 返回报表数据
     */
    List<Map<String,Object>> queryDebtReport(IRequest request, Map<String, Object> map);
    /**
     * 资金使用情况表
     *
     *
     * @param request 请求参数
     * @param map 查询参数集合
     * @return 返回报表数据
     */
    List<Map<String,Object>> queryFoundUseOfWithdraw(IRequest request, Map<String, Object> map);
    /**
     * 资金使用情况表
     *
     *
     * @param request 请求参数
     * @param map 查询参数集合
     * @return 返回报表数据
     */
    List<Map<String,Object>> queryFoundUseOfProject(IRequest request, Map<String, Object> map);

    /**
     * 融资明细报表
     *
     * 以提款为维度
     *
     * @param request 请求参数
     * @param map 查询参数集合
     * @return 返回报表数据
     */
    List<Map<String,Object>> queryReport(IRequest request, Map<String, Object> map);

    List<HlsCusLonContractWithdraw> queryContractWithdraw(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize);

    HlsCusLonContractWithdraw lonContractWithdrawFormData(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, HttpSession session);

    HlsCusLonContractWithdraw lonContractWithdrawCreate(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, HttpSession session)  throws HlsCusException;

    HlsCusLonContractWithdraw save(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw) throws HlsCusAmountOverException, HlsCusException;

    void lonContractCalcRepayment(IRequest iRequest, HlsCusLonContractWithdraw lonContractWithdraw) throws Exception;

    List<HlsCusLonContractWithdraw> submitLonContractWithdrawToWfl(IRequest iRequest, HlsCusLonContractWithdraw lonContractWithdraw, HttpSession session) throws HlsCusAmountOverException, HlsCusException;

    /*------------融资提款综合查询---------*/
    List<HlsCusLonContractWithdraw> withdrawComprehensiveQuery(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize);

    /*------------融资提款计提---------*/
    void calcLonConWithdrawFinCost(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw);

    Double getRepaymentAmount(IRequest iRequest, Date repaymentCalcDateFrom, Date repaymentCalcDateTo, Long withdrawId, Double withdrawAmount, Double intRate, String calcInterestYearDays, Date[] repaymentPrincipalDate);

    Long getCalcDays(Date calcStartDate, Date calcEndDate);

    Date getMonthEndDate(Date calcDate);

    HlsCusLonContractWithdraw lonConWithdrawChangeReqCreate(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw) throws HlsCusException ;

    void lonConWithdrawChangeReqConfirm(IRequest request, HlsCusLonContractWithdraw lonContractWithdrawChangeReq) throws HlsCusException ;

    void lonContractWithdrawChangeReqCancel(IRequest request, HlsCusLonContractWithdraw lonContractWithdrawChangeReq);

    List<HlsCusLonContractWithdraw> queryDebtStructureChart(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize);

    List<HlsCusLonContractWithdraw> queryLonWithdrawInfo(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize);

    List<HlsCusLonContractWithdraw> queryLonWithdrawDetailInfo(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, Integer page, Integer pageSize);

    void batchDeleteWithdraw(IRequest request, List<HlsCusLonContractWithdraw> lonContractWithdraws);

    /**
     * 融资提款-还款计划行计算逻辑
     * @param iRequest
     * @param hlsCusLonContractRepaymentList
     * @return
     */
//    List<HlsCusLonContractRepayment> calcLoanRepaymentCashflow(IRequest iRequest, List<HlsCusLonContractRepayment> hlsCusLonContractRepaymentList) throws HlsCusException;
//throws ParseException
    void calXirr(IRequest iRequest, HlsCusLonContractWithdraw hlsCusLonContractWithdraw, HlsCusLonContractQuotation hlsCusLonContractQuotation);

    //变更历史查询
    List<HlsCusLonContractWithdraw> withdrawChangeHistoryQuery(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, int page, int pageSize);

    /**
     * 查询基准利率
     * @param
     * @return
     */
    List<Double> queryForFinanceQuotation(HlsCusLonContractWithdraw hlsCusLonContractWithdraw);

    /**
     * 还款计划导入
     * @param iRequest
     * @param hdId
     * @param withdrawId
     * @param contractId
     * @throws ExcelException
     * @throws Exception
     * @throws ParseException
     */

    void lonContractRepaymentImport(IRequest iRequest, Long hdId , Long withdrawId,Long contractId) throws ExcelException, Exception, ParseException;
    void lonContractChangeCalcRepayment(IRequest iRequest, HlsCusLonContractWithdraw lonContractWithdraw) throws Exception;
    List<Long> queryForFinanceQuotationTime(HlsCusLonContractWithdraw hlsCusLonContractWithdraw);

    void updateAutoWriteByWithdrawId(HlsCusLonContractWithdraw lonContractWithdraw);

    List<HlsCusLonContractWithdraw> selectStampDutyFinancing(HlsCusLonContractWithdraw dto);
}