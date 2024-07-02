package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusLonContractRepaymentMapper<T extends HlsCusLonContractRepayment> extends Mapper<HlsCusLonContractRepayment> {
    List<Map<String, Object>> queryReport(Map<String, Object> map);

    List<HlsCusLonContractRepayment> selectLonContractRep(HlsCusLonContractRepayment lonContractRepayment);

    List<HlsCusLonContractRepayment> selectLonContractFin(HlsCusLonContractRepayment lonContractRepayment);

    List<HlsCusLonContractRepayment> selectRepaymentPlanOrderByRepaymentDate(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<Map> queryRep(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> queryChartRep(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> queryList(HlsCusLonContractRepayment hlsCusLonContractRepayment);
    List<HlsCusLonContractRepayment> selectRepaymentDetail(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> ctRateChangeDetailCompare(HlsCusConFloatingRateReqLn dto);

    List<HlsCusLonContractRepayment> unitQueryRep(Map<String, Object> params);

    List<HlsCusLonContractRepayment> queryDebtMaturityStructureChart(Map<String, Object> map);

    Double selectPlanAmountSum(@Param("withdrawId") Long withdrawId, @Param("interestFlag") String interestFlag);

    List<HlsCusLonContractRepayment> selectRepaymentPlanByDateOrderByRepaymentDate(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> selectRepaymentPlanByLastDateOrderByRepaymentDate(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> selectRepaymentPlanIntByTimesOrderByRepaymentDate(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> selectRepaymentPlanIntByDayOrderByRepaymentDate(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    void updateLonContractRepayment(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    Double cfItemAmountSum(HlsCusLonContractRepayment hlsCusLonContractRepayment);
    Double chargeAmountSum(HlsCusLonContractRepayment hlsCusLonContractRepayment);
    Double selectRemainingPrincipalAmountSum(@Param("withdrawId") Long withdrawId, @Param("incomeDate") Date incomeDate);

    Double selectAmountSum(@Param("withdrawId") Long withdrawId, @Param("incomeDate") Date incomeDate, @Param("incomeEndDate") Date incomeEndDate);
    //Double selectAmountSum(@Param("withdrawId") Long withdrawId, @Param("incomeDate") Date incomeDate);
    /**
     * 提款 利息最大期数
     * @param withdrawId
     * @return
     */
    Long selectInterestMaxTimes(@Param("withdrawId") Long withdrawId);

    Double selectInterestplannedDueAmount(@Param("withdrawId") Long withdrawId);

    Double intereSum(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> selectLonContractChangeAfterRep(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractRepayment> selectLonContractRepAndFin(HlsCusLonContractRepayment lonContractRepayment);
    Double queryinterestAccrualBalance(@Param("withdrawId") Long withdrawId,@Param("cfItem") String cfItem,@Param("times") Long times);

    List<HlsCusLonContractRepayment> queryPlannedDueDateStart(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    /**
     * 根据现金流类型查询分摊方式
     * @param withdrawId
     * @param cfItem
     * @return
     */
    Long queryAmortizationMethodCount(@Param("withdrawId") Long withdrawId,@Param("cfItem") Long cfItem);

}