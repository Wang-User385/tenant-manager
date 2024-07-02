package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface HlsCusConContractCashflowService extends IBaseService<HlsCusConContractCashflow>, ProxySelf<HlsCusConContractCashflowService> {
    /*List<Map<String, Object>> queryAcrWriteOffDetail(IRequest request, Map<String, Object> map);

    List<HlsCusConContractCashflow> queryOutstandingPrincipal(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);
    *//**
     * 删除现金流
     *//*
    int deleteCashflow(HlsCusConContract sourceContract);

    *//**
     * 复制现金流
     *//*
    List<HlsCusConContractCashflow> cloneCashflow(HlsCusConContract sourceContract, HlsCusConContract targetContract);

    */
    /**
     * 租赁待开票查询
     */

    List<HlsCusConContractCashflow> queryFactoringInvoice(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

//    List<HlsCusConContractCashflow> queryInvoicePercent(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

    /**
     * 计算提前还款的现金流
     */
    /*
    boolean calculatePreRepayment(IRequest request, Map<String, Object> map);

    // 根据给定的日期和合同id查出所在的期数
    Long queryTimesByDate(IRequest request, Long contractId, Date date);

    List<HlsCusConContractCashflow> queryPreRepaymentChangeInfo(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryPreRepaymentChangeBefore(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryPreRepaymentChangeAfter(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);

    boolean saveRentalPlanChange(IRequest request, List<HlsCusConContractCashflow> hlsCusConContractCashflowList);

    *//**
     * PMT计算公式
     * @param outstandingPrincipal 剩余本金
     * @param intRate 年利率
     * @param rentingFrequency 付款频率
     * @param leaseTimes 剩余期数
     * @param payType 先付后附,先付为1，后付为0
     * @return 每期租金
     *//*
    Double PMT(Double outstandingPrincipal, Double intRate, Long rentingFrequency, Long leaseTimes, int payType);

    void calculate(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryRentalPlanChangeAfter(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryRentalPlanChangeBefore(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);



    List<HlsCusConContractCashflow> queryCshFineDetailInfo(HlsCusConContractCashflow hlsCusConContractCashflow);

    *//**
     * 创建附件 默认
     * @param iRequest
     * @param hlsCusConContractCashflow
     *//*


    List<HlsCusConContractCashflow> queryCshFineDetailList(IRequest iRequest, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);



    Double getDueAmount(IRequest iRequest, Date calcDateFrom, Date calcDateTo, Double dueAmount, Double intRate, Long interestYearDays);

    Long getRentingFrequency(String rentingFrequency);*/

    //查询该合同下的杂项费用是否完全核销
    String confirmFullWriteOff(IRequest requestContext, Long contractId, String writeOffType);

    /**
     * 核销头信息
     *//*
    List<HlsCusConContractCashflow> queryCshWriteOffHd(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow);

    //10、项目回款情况统计表
    List<HlsCusConContractCashflow> queryProjectReceiptChart(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

    //9、资产、负债剩余期限结构统计表
    List<HlsCusConContractCashflow> queryAssetsLiabilitiesStructureChart(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

    List<HlsCusConContractCashflow> selectAllConContractCashflowLov(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);


    *//**
     * 收付管理查询未完全核销的现金流
     * @param request
     * @param hlsCusConContractCashflow
     * @param page
     * @param pageSize
     * @return
     *//*
    List<HlsCusConContractCashflow> selectNotFullContractCashflow(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

    *//**
     * 收付管理查询未完全核销的现金流主页面查询
     * @param request
     * @param hlsCusConContractCashflow
     * @param page
     * @param pageSize
     * @return
     *//*
    List<HlsCusConContractCashflow> selectNotFullContractCashflowHome(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);


    *//**
     * 导出
     * @param request
     * @param response
     * @param cashflow
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     *//*
    void exportNotFullContractCashflow(HttpServletRequest request, HttpServletResponse response, HlsCusConContractCashflow cashflow)throws IOException, InvocationTargetException, IllegalAccessException;



    String getCfItemDescription(String cfItem);


    List<HlsCusConContractCashflow> queryCashflowByCfItemOrderByDueDate(HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);



    *//**
     *查询租赁/保理 现金流
     * @param hlsCusConContractCashflow
     * @return
     *//*
    List<HlsCusConContractCashflow> selectContarctCashFlowData(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);



    List<HlsCusConContractCashflow> selectConContractCashflowDetail(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);


    void prePaymentCalculate(IRequest iRequest, HlsCusConContract hlsCusConContract);

    //查询合同其他应收现金流
    List<HlsCusConContractCashflow> queryContractOutCashflowByContractId(HlsCusConContract hlsCusConContract, int page, int pagesize);
*/
    //查询合同还款计划
    List<HlsCusConContractCashflow> queryContractRepCashflowByContractId(HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pagesize);

  //租赁合同现金流<due_date合并>
    List<HlsCusConContractCashflow> conQueryCashFlow(HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pagesize);
/*
    void exportConQuotationCashflow(HttpServletRequest request, HttpServletResponse response, HlsCusConContractCashflow hlsCusConContractCashflow) throws IOException, InvocationTargetException, IllegalAccessException;

    void exportConCashFlow(HttpServletRequest request, HttpServletResponse response, HlsCusConContractCashflow hlsCusConContractCashflow) throws IOException, InvocationTargetException, IllegalAccessException;
*/

    public List<HlsCusCshPaymentReqHd> contractGenerate(IRequest request, Long projectId);

    List<HlsCusConContractCashflow> selectConLoanRequest(IRequest requestContext, HlsCusConContractCashflow dto, int page, int pageSize);

    List<HlsCusConContractCashflow> queryContractCashflowLov(IRequest requestCtx, HlsCusConContractCashflow hlsCusConContractCashflow, int pagenum, int pagesize);

    List<HlsCusConContractCashflow> queryCshFineInfoList(IRequest iRequest, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

    void createCshFineAttachment(IRequest iRequest, Long contractId);

    List<HlsCusConContractCashflow> fineCshSubmit(IRequest requestContext, List<HlsCusConContractCashflow> hlsCusConContractCashflowList);

    List<HlsCusConContractCashflow> queryForRealIncomeReport(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

    /**
     * 二期功能：进件投放审查通过后，复制现金流
     * @param request
     * @param contractId
     * @param quotationId
     * @param newQuotationId
     * @return
     */
    List<HlsCusConContractCashflow> saveCashflowFromPrjCashflow(IRequest request, Long contractId, Long quotationId, Long newQuotationId);


    /**
     * 二期功能：付款申请页面查询
     * @param request 请求
     * @param hlsCusConContractCashflow 入参
     * @param page 分页参数
     * @param pageSize 分页参数
     * @return 列表
     */
    List<HlsCusConContractCashflow> queryCshPaymentCreateInfo(IRequest request, HlsCusConContractCashflow hlsCusConContractCashflow, int page, int pageSize);

    /**
     * 二期功能：查询现金流
     * @param contractId
     * @return
     */
    List<HlsCusConContractCashflow> queryCashflowList(Long contractId);

    /**
     * 关税现金流导入
     * @param request
     * @param headerId
     */
    void contTariffCashflowImport(IRequest request,Long headerId) throws Exception;

    List<Map<String, Object>> queryEtAmount(IRequest var1, Map var2);
}
