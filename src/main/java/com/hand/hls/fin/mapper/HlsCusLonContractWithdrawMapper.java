package com.hand.hls.fin.mapper;

import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusLonContractWithdrawMapper extends LonContractWithdrawMapper<HlsCusLonContractWithdraw> {
    List<Map<String, Object>> queryDebtReport(Map<String, Object> map);
    List<Map<String, Object>> queryFoundUseOfWithdraw(Map<String, Object> map);
    List<Map<String, Object>> queryFoundUseOfProject(Map<String, Object> map);

    List<Map<String, Object>> queryReport(Map<String, Object> map);

    List<HlsCusLonContractWithdraw> lonContractWithdrawFormData(HlsCusLonContractWithdraw lonContractWithdraw);
    List<HlsCusLonContractWithdraw> lonContractWithdrawFormById(HlsCusLonContractWithdraw lonContractWithdraw);

    void deleteLonConAllRepayWithCf(HlsCusLonContractRepayment hlsCusLonContractRepayment);

    List<HlsCusLonContractWithdraw> selectLonConWithdrawFlt();

    //7、债务期限结构统计表
    List<HlsCusLonContractWithdraw> queryDebtStructureChart(Map<String, Object> map);

    //公司融资情况总览
    List<HlsCusLonContractWithdraw> queryLonWithdrawInfo(HlsCusLonContractWithdraw lonContractWithdraw);

    //公司融资情况明细
    List<HlsCusLonContractWithdraw> queryLonWithdrawDetailInfo(HlsCusLonContractWithdraw lonContractWithdraw);

    /**
     * 取出下一次流水号
     * @param contractId
     * @return
     */
    String selectWithdrawNumberMax(@Param("contractId") Long contractId);


    /**
     *
     * @param creditLineId
     * @return
     */
    Double selectCreditDueAmount(@Param("creditLineId") Long creditLineId);

    //变更历史查询
    List<HlsCusLonContractWithdraw> withdrawChangeHistoryQuery(HlsCusLonContractWithdraw lonContractWithdraw);

    List<HlsCusLonContractWithdraw> selectContractWithdraw(HlsCusLonContractWithdraw hlsCusLonContractWithdraw);

    List<HlsCusLonContractWithdraw> withdrawComprehensiveQuery(Map<String, Object> hlsCusLonContractWithdraw);

    List<HlsCusLonContractWithdraw> selectWd(HlsCusLonContractWithdraw var1);

    List<Map> queryLonContractWithdrawAll();

    List<HlsCusLonContractWithdraw> lonContractQuotationForm(HlsCusLonContractWithdraw lonContractWithdraw);

    Double queryForFinanceQuotation(@Param("baseRateType") String baseRateType,@Param("dueDate") Date dueDate);

    Double querySurplusCredit(@Param("withdrawId") Long withdrawId);
    Long queryForFinanceQuotationTime(@Param("rateChangeDate") Date rateChangeDate,@Param("withdrawId") Long withdrawId);

    void updateAutoWriteByWithdrawId(HlsCusLonContractWithdraw lonContractWithdraw);

    List<HlsCusLonContractWithdraw> selectStampDutyFinancing(HlsCusLonContractWithdraw dto);

    void stampDutyAccrualFinancing(HlsCusLonContractWithdraw hlsCusLonContractWithdraw);
}