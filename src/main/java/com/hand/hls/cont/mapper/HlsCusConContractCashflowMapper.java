package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.partner.dto.ClaimsSubrogationDTO;
import com.hand.hls.partner.dto.OverdueRepurchaseTrialCalculationDTO;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusConContractCashflowMapper extends Mapper<HlsCusConContractCashflow> {
    List<Map<String, Object>> queryAcrWriteOffDetail(Map<String, Object> map);

    List<HlsCusConContractCashflow> queryOutstandingPrincipal(HlsCusConContractCashflow hlsCusConContractCashflow);
    List<HlsCusConContractCashflow> selectCashflowInfoReport(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryPreRepaymentChangeInfo(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryInvoicePercent(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryFactoringInvoice(HlsCusConContractCashflow hlsCusConContractCashflow);

    Long queryTimesByDate(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryPreRepaymentChange(Long contractId);

    void deleteByContractId(HlsCusConContractCashflow hlsCusConContractCashflow);

    String getCfItemDescription(String cfItem);

    List<HlsCusConContractCashflow> queryRentalPlanChange(HlsCusConContractCashflow hlsCusConContractCashflow);

    public void updateOne(HlsCusConContractCashflow cashflow);

    public HlsCusConContractCashflow selectConContractCashflow(Long cashflowId);

    public void updateOneConContractCashflow(HlsCusConContractCashflow cashflow);

    //查询该虚拟合同下所有的罚息情况
    List<HlsCusConContractCashflow> queryCshFineInfoList(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryCshFineDetailInfo(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryCshFineDetailList(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryCashflowByCfItemOrderByDueDate(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> selectCalcPenaltyCashFlowByConId(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> selectCalcPenaltyCashFlowBySourceId(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> selectConNoticeMessageInfoByContractId(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<Long> selectCollectPostUserId(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryCshWriteOffHd(HlsCusConContractCashflow hlsCusConContractCashflow);

    //10、项目回款情况统计表
    List<HlsCusConContractCashflow> queryProjectReceiptChart(Map<String, Object> map);

    //9、资产、负债剩余期限结构统计表
    List<HlsCusConContractCashflow> queryAssetsLiabilitiesStructureChart(Map<String, Object> map);

    List<HlsCusConContractCashflow> queryAssetsLiabilitiesStructureCExport(Map<String, Object> map);

    List<HlsCusConContractCashflow> selectAllConContractCashflowLov(HlsCusConContractCashflow hlsCusConContractCashflow);

    /**
     * 收付管理查询未完全核销的现金流
     * @param hlsCusConContractCashflow
     * @return
     */
    List<HlsCusConContractCashflow> selectNotFullContractCashflow(HlsCusConContractCashflow hlsCusConContractCashflow);

    /**
     * 收付管理查询未完全核销的现金流主页面查询
     * @param
     * @return
     */
    List<HlsCusConContractCashflow> selectNotFullContractCashflowHome(HlsCusConContractCashflow hlsCusConContractCashflow);


    /**
     *查询租赁/保理 现金流
     * @param hlsCusConContractCashflow
     * @return
     */
    List<HlsCusConContractCashflow> selectContarctCashFlowData(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> selectConContractCashflowDetail(HlsCusConContractCashflow hlsCusConContractCashflow);

    Map queryDateDiff(@Param("start") Date start, @Param("end") Date end);

    void updateDueDate(HlsCusConContractCashflow hlsCusConContractCashflow);

    /**
     * 保理合同放款申请页面查询前期款收款信息
     *
     * @param dto
     * @return
     */
    List<HlsCusConContractCashflow> selectConLoanRequest(HlsCusConContractCashflow dto);

    Double queryCashflowTotalAmount(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryCashflowLeaseChargeinfo(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryCashflowNotWriteOffCount(HlsCusConContractCashflow hlsCusConContractCashflow);

    Long selectConContractCashflowBeforeQuery(Long contractId);

    void deleteCalcByContractId(@Param("contractId") Long contractId);

    List<HlsCusConContractCashflow> conChangeCashflowselect(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryContractOutCashflowByContractId(@Param("contractId") Long contractId);

    List<HlsCusConContractCashflow> queryContractRepCashflowByContractId(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> conQueryCashFlow(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> querySubsectionCashflow(HlsCusConContractCashflow hlsCusConContractCashflow);

    void deleteNotWriteOffRent(@Param("contractId") Long contractId);

    Double querySubsectionCashflowDueAmount(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryContractCashflowLov(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> queryContractCashflowLovNew(HlsCusConContractCashflow cashflow);

    /**
     * 二期功能：收款管理-业务功能 核销为保证金 LOV 查询
     * @param cashflow
     * @return
     */
    List<HlsCusConContractCashflow> queryContractCashflowForDepositLov(HlsCusConContractCashflow cashflow);


    List<Map> selectCashflowInfoByContract(HlsCusConContractCashflow cashflow);

    List<Map> selectCashflowInfoByPrjCon(HlsCusPrjProject prjCon);


    List<HlsCusConContractCashflow> queryAllocationCashflow(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> queryAllocationCashflow1(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> queryAllocationCashflow3(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> queryAllocationCashflow4(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> queryAllocationBpId(HlsCusConContractCashflow cashflow);

    List<HlsCusConContractCashflow> selectByCashflowIds(List<Long> cashflowIds);

    List<Map> selectContractCashflowInfo(Map map);

    List<Map> selectCashflowInfoByPrj(HlsCusConContractCashflow cashflow);

    List<Map> selectCashflowInfoByPrjChangeBefore(HlsCusConContractCashflow cashflow);


    List<Map> selectCashflowInfoForApp(HlsCusConContractCashflow cashflow);

    List<Map> selectCashflowInfoByPrjNew(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> selectCashflowInfoByPrjNew1(HlsCusConContractCashflow cashflow);

    /**
     * 为罚息计算,查询符合条件的合同现金流
     */
    List<HlsCusConContractCashflow> selectForOverDueDayEnd(HlsCusConContractCashflow cashflow);


    List<HlsCusConContractCashflow> queryConContractCashflowDetail(Map map);
    List<HlsCusConContractCashflow> queryForRealIncomeReport(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryForHandleReport(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryContractOverdueDetailInfo(HlsCusConContractCashflow hlsCusConContractCashflow);

    void deleteContractCashflowByQuotationId(HlsCusPrjQuotation quotation);
    String queryCashflowByFlag(HlsCusConContractCashflow hlsCusConContractCashflow);


    void deleteByCashflowId(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryOldCashflow(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> selectConContractCashflowInfoQuery(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryCashflowCalcPMT(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> queryOldCashflowByQuotation(HlsCusConContractCashflow hlsCusConContractCashflow);

    List<HlsCusConContractCashflow> selectPayList();

    List<HlsCusConContractCashflow> selectActualPayList();

    List<Map> selectCashflowItem();

    List<HlsCusConContractCashflow> selectCashflowRental(HlsCusConContractCashflow cashflow);


    /**
     * 五级分类评估指标 计算逻辑 所需参数查询
     * @param hlsCusConContractCashflow 现金流对象
     * @return 返回结果集
     */
    List<HlsCusConContractCashflow> queryFiveClassInfo(HlsCusConContractCashflow hlsCusConContractCashflow);
    List<HlsCusConContractCashflow> selectConCashflowNotWriteOff(HlsCusConContractCashflow cashflow);


    /**
     * 查询关税现金流
     * @param hlsCusConContractCashflow
     * @return
     */
    List<HlsCusConContractCashflow> queryTariffCashflowAll(HlsCusConContractCashflow hlsCusConContractCashflow);
    public void updateCashfolwBlock(HlsCusConContractCashflow cashflow);
    public void updateCashfolwRetention(HlsCusConContractCashflow cashflow);
    public void insertCashfolwEt(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> selectCashFlow(HlsCusConContractCashflow hlsCusConContractCashflow);
    void updateContractCashFlow(HlsCusConContractCashflow var1);
    List<HlsCusConContractCashflow> queryAllReq(HlsCusConContractCashflow hlsCusConContractCashflow);
    List<HlsCusConContractCashflow> queryContractCashflowJob(HlsCusConContractCashflow hlsCusCshContractCashflow);
    List<HlsCusConContractCashflow> queryTariffCshPaymentCashflow(Map map);

    List<HlsCusConContractCashflow> selectPayiedRentalConCashflowReportNew(HlsCusConContractCashflow hlsCusConContractCashflow);
    Map selectPayiedRentalConCashflowReportNewSum(HlsCusConContractCashflow hlsCusConContractCashflow);


    List<HlsCusConContractCashflow> selectCashflowTimes(HlsCusConContractCashflow hlsCusConContractCashflow);

    int deleteChengeTimeAfterCashflow(@Param("contractId") Long contractId, @Param("times") Long times);

    List<HlsCusConContractCashflow> queryCshPaymentCreateInfo(HlsCusConContractCashflow hlsCusConContractCashflow);
    /**
     * 二期功能：付款申请页面查询
     * @param hlsCusConContractCashflow 入参
     * @return 列表
     */
    List<HlsCusConContractCashflow> queryCshPaymentCreateInfo2(HlsCusConContractCashflow hlsCusConContractCashflow);

    /**
     * 二期功能：查询现金流
     *      - 收付抵扣
     */
    List<HlsCusConContractCashflow> queryContractCashflowLovForDeduct(HlsCusConContractCashflow hlsCusConContractCashflow);

    /**
     * 二期功能：查询现金流
     * @param contractId
     * @return
     */
    List<HlsCusConContractCashflow> queryCashflowList(@Param("contractId") Long contractId);

    List<HlsCusConContractCashflow> queryContractCashflowWriteoffLov(HlsCusConContractCashflow hlsCusConContractCashflow);
    int updateBlockAmount(@Param("cashflowId")Long cashflowId, @Param("blockAmount")Double blockAmount, @Param("blockPrincipal")Double blockPrincipal, @Param("blockInterest")Double blockInterest);

    List<HlsCusConContractCashflow> querySmsCashflowList(@Param("days") Integer days,@Param("contractStatus") String contractStatus,
                                                         @Param("bpClass") String bpClass);

    List<HlsCusConContractCashflow> querySmsOverdueCashflowList(@Param("contractStatus") String contractStatus,@Param("bpClass") String bpClass);

    /**
     * 获取合同现金流信息，支付日期正序排列
     * @param contractId
     * @param quotationId
     * @return
     */
    List<HlsCusConContractCashflow> selectConContractCashFlowByContractIdAndQuotationId(@Param("contractId")Long contractId,@Param("quotationId")Long quotationId);

    Double queryOverDueAmount(Map map);
    Long selectMaxContractCashflowIdBeforeDate(Map map);
    /**
     * 查询due_date日期前期数最大的现金流
     */
    HlsCusConContractCashflow selectMaxContractCashflowInfoBeforeDate(Map map);

    List<HlsCusConContractCashflow> queryEtAmount(Map map);
    Double queryRestAmount(Map map);
    List<HlsCusConContractCashflow> queryEtRestCapital(Map map);
    List<HlsCusConContractCashflow> selectForOverDueDayEndNew(HlsCusConContractCashflow cashflow);
    List<HlsCusConContractCashflow> selectForManuFactoryOverDueDayEnd(Map var1);

    /*
     * 合同变更结束查询保证金自动抵扣的现金流顺序，保证金抵扣顺序为罚息-利息--本金-名义货价
     * */
    List<HlsCusConContractCashflow> queryAutoDepositCashflowOrder(HlsCusConContractCashflow cashflow);

    /**
     * 保证金抵扣查询时剔除已冻结金额
     * @param cashflow 合同ID
     * @return 现金流
     */
    List<HlsCusConContractCashflow> queryAutoDepositCashflowOrderWithoutBlockAmt(HlsCusConContractCashflow cashflow);

    /**
     * 车辆业务还款计划明细：现金流数据查询
     * @param cashflow
     * @return
     */
    List<HlsCusConContractCashflow> queryCashflowByContractId(HlsCusConContractCashflow cashflow);

    /**
     * 根据接口传递的订单编号和其次号获取代偿现金流数据
     * @param claimsSubrogationDTO
     * @return
     */
    HlsCusConContractCashflow queryClaimsSubrogation(ClaimsSubrogationDTO claimsSubrogationDTO);

    /**
     * 根据订单编号和试算日期获取到最大的已到期应收日期
     * @param trialTime
     * @return
     */
    Date queryDueDate(@Param("orderNo")String orderNo ,@Param("trialTime")String trialTime);

    /**
     * 查询出需要回购的现金流数据
     * @param orderNo
     * @return
     */
    List<HlsCusConContractCashflow> queryUnReceivedByOrderNo(@Param("orderNo")String orderNo,@Param("dueDate")Date dueDate);


    /**
     * 冻结现金流
     */
    void updateCashflowBlock(@Param("contractId")Long contractId, @Param("dueDate")Date dueDate);

    /**
     * 核销为预收款选择代扣现金流数据
     * @param conContractCashflow
     * @return
     */
    List<HlsCusConContractCashflow> queryContractCashflowForCompLov(HlsCusConContractCashflow conContractCashflow);

    void updateCashflowByinfo(HlsCusConContractCashflow hlsCusConContractCashflow);

    HlsCusConContractCashflow getHlsCusConContractCashflowByContractId(@Param("contractId") Long contractId,@Param("termNo") Long termNo);


    List<HlsCusConContractCashflow> queryConContractCashflowList(@Param("contractId") Long contractId, @Param("times") Long times);

    /**
     * 查询回购未完全核销期次
     * @param orderNo
     * @param dueDate
     * @return
     */
    List<Integer> queryUnReceivedTimesByOrderNo(@Param("orderNo")String orderNo,@Param("dueDate")Date dueDate);

    //查询剩应收金额、剩余本金
    HlsCusConContractCashflow queryMinDueDateInfo(@Param("orderNo")String orderNo,@Param("dueDate")Date dueDate);

    //更新报价表上租赁期数
    void updatePrjQuotationLeaseTimesByOrderNo(@Param("times")Long times, @Param("orderNo")String orderNo);

    //查询罚息现金流
    HlsCusConContractCashflow queryConContractCashflowListPenalty(@Param("contractId") Long contractId, @Param("times") Long times);

}
