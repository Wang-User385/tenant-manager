package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ast.dto.ConContractLov;
import com.hand.hls.ast.dto.VirtualConContractLov;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractAttachment;
import com.hand.hls.mort.dto.HlsMortgage;
import org.apache.ibatis.annotations.Param;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface HlsCusConContractMapper extends Mapper<HlsCusConContract> {
    List<Map<String, Object>> queryPaymentChangeInfoLov(HlsCusConContract hlsCusConContract);

    List<Map> queryReturnBank(Long contractId);

    //合同管理首页环状图
    List<Map> conHomePageGetAllStatusContractCount(HlsCusConContract conContract);
    //合同管理首页grid
    List<HlsCusConContract> conHomePageContractInfoGrid(HlsCusConContract conContract);
    List<HlsCusConContract> queryConCostRate(HlsCusConContract conContract);

    List<HlsCusConContract> queryPaymentInfoList(HlsCusConContract conContract);
    List<HlsCusConContract> queryConIncome(HlsCusConContract conContract);

    List<HlsCusConContract> selectConContractFlt();

    Long queryProjectIdByContractId(@Param("contractId")Long contractId);

    List<HlsCusConContract> selectNeedCalcPenaltyContract(Long companyId);

    List<HlsCusConContract> selectOverContract(HlsCusConContract conContract);
    List<HlsCusConContract> selectOverContract2(HlsCusConContract conContract);

    List<Map<String, Object>> selectContractForABS(Map<String, Object> map);

    String selectPaymentReqStatus(HlsCusConContract hlsCusConContract);

    List<HlsCusConContract> selectConCshPaymentReqForSummary(HlsCusConContract dto);

    List<HlsCusConContract> conContractCshReqDetail(HlsCusConContract dto);

    HlsCusConContract selectByProjectId(Long projectId);
    Integer updateByProjectId(HlsCusConContract hlsCusConContract);

    List<HlsCusConContract> selectConContractQuery(HlsCusConContract dto);

    List<HlsCusConContract> selectConContractDetailQuery(Long contractId);

    List<HlsCusConContract> selectConContractDetail(HlsCusConContract dto);

    List<HlsCusConContract> selectBillingProfileDetail(HlsCusConContract dto);

    List<HlsCusConContract> queryInsureContractList(HlsCusConContract dto);

    HlsCusConContract queryInsureContractDetail(HlsCusConContract dto);


    List<Map> selectContractInfoByProject(HlsCusConContract dto);
    List<Map> selectContractInfoByProjectCon(HlsCusConContract dto);

    List<ConContractLov> queryContractLov(@Param("param") ConContractLov param);
    List<VirtualConContractLov> queryVirtualContractLov(@Param("param") VirtualConContractLov param);

    List<HlsCusConContract> searchContractHome(HlsCusConContract cusConContract);


    Long selectMaxOverDueDays(@Param("contractId") Long contractId);

    HlsCusConContract judgeLoanInitial(HlsCusConContract cusConContract);

    List<HlsCusConContract> queryToInceptList(HlsCusConContract cusConContract);

    List<HlsCusConContract> queryToFinanceInceptList(HlsCusConContract cusConContract);

    List<HlsCusConContract> queryConFinancialInfo(HlsCusConContract cusConContract);

    List<HlsCusConContract> conAverageDailyBalanceQuery(HlsCusConContract cusConContract);

    List<HlsCusConContract> prjLoanAdvanceQuery(HlsCusConContract cusConContract);

    List<HlsCusConContract> ConContractAmortizationQuery(HlsCusConContract cusConContract);
    List<HlsCusConContract> conCashflowAmortizationQuery(HlsCusConContract cusConContract);

    int queryCountPaymentById(HlsCusConContract cusConContract);

    List<HlsCusConContract> selectFloatingRateContract();

    void updateCashflowAmortizationMethod(HlsCusConContract cusConContract);

    List<HlsCusConContract> conQuotationHistoryHomeGrid(HlsCusConContract conContract);

    List<HlsCusConContract> queryContractByProjectId(HlsCusConContract hlsCusConContract);

    int judgePaymentByContract(HlsCusConContract cusConContract);

    //业务变更执行通过 分摊数据 查询
    List<HlsCusConContract> conGetContract(HlsCusConContract conContract);


    List<HlsCusConContract> selectContractInceptList(HlsCusConContract conContract);
    List<HlsCusConContract> cshPaymentReqPrjNew(HlsCusConContract conContract);
    List<Map> conContractRentPaymentConfirmHome(Map map);

    List<HlsCusConContract> cshPaymentReqCon(Map map);

    Map cshPaymentReqConSum(Map map);

    List<HlsCusConContract> selectStampDutyLease(HlsCusConContract dto);

    void stampDutyAccrualLease(HlsCusConContract hlsCusConContract);

    List<HlsCusConContract> selectRiskFundAccrual(HlsCusConContract dto);

    List<HlsCusConContract> queryContractInfoInceptMain(HlsCusPrjProject prjProject);

    List<HlsCusConContract> queryContractInfoTerminateMain(HlsCusPrjProject prjProject);
    HlsCusConContract querySapStatusForReceived(@Param("contractId")Long contractId);
    List<HlsCusConContract> queryByContractIds(List<Long> list);
    List<Map> queryConContractDetailsForHome(Map map);
    String selectManufacturerCodeByPrimaryKey(@Param("contractId")Long contractId);
    /**
     * 厂商下所有合同状态为起租INCEPT/暂挂PENDING，
     * 且投放日lease_start_date在上季度及之前的合同，
     * 及合同状态为结束但结束日期termination_date为本季度的合同
     */
    List<HlsCusConContract> queryByCondition(HlsCusConContract conContract);
    List<HlsCusConContract> queryConContractDetails(Map map);

    /**
     * 大单提前部分还本合同信息查询
     * @param map
     * @return
     */
    List<HlsCusConContract> queryConContractPartialPrepaymentDetails(Map map);
    /**
     * 查询合同变更需要更新的字段
     * @param contractId
     * @return
     */
    Map queryContractChangeUpdateInfo(Long contractId);

    Map selectPrePaymentChangeReqInfo(HlsCusConContract hlsCusConContract);
    /**
     * 根据合同编号查找合同
     */
    List<HlsCusConContract> queryContractIdByContractNumber(String contractNumber);

    /**
     * 查询租赁物下一个主键值
     */
    @Select("select CON_CONTRACT_LEASE_ITEM_S.nextval from dual")
    Long queryNextId();

    /**
     * 查询批复参数
     */
    Double queryReplyParamDefaultValue(@Param("contractId")Long contractId, @Param("replyPara")String replyPara);

    HlsCusConContract selectByRefProjectId(Long projectId);
    //进件合同起租列表页面查询
    List<HlsCusConContract> queryContractInceptInfoMain(HlsCusPrjProject prjProject);


    List<HlsCusConContract> findAllByHlsCusConContract(HlsCusConContract hlsCusConContract);

    List<HlsCusConContract> findDetail(HlsCusConContract hlsCusConContract);

    List<HlsCusConContract> findAttachReview(HlsCusConContract hlsCusConContract);
    List<HlsCusConContract> findAttachContact(HlsCusConContract hlsCusConContract);
    List<HlsCusConContract> findAttachMortgage(HlsCusConContract hlsCusConContract);

    /**
     * 车辆还款计划首页数据查询
     */
    List<HlsCusConContract> findContractByPayPlan(HlsCusConContract hlsCusConContract);

}