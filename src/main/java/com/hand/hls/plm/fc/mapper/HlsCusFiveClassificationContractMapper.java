package com.hand.hls.plm.fc.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.pli.dto.PlmPliContract;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*import hls.core.plm.pli.dto.PlmPliContract;*/

public interface HlsCusFiveClassificationContractMapper extends Mapper<HlsCusFiveClassificationContract> {

    List<HlsCusFiveClassificationContract> selectAllContracts(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectConContracts(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectFctContracts(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectFiveClassificationContracts(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectFctContractAmount(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectConContractAmount(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> homeChartQuery(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectMaxChangeTime(HlsCusFiveClassificationContract fiveClassificationContract);

    void updateContractsChangeIq(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectContractFrequency(HlsCusFiveClassificationContract fiveClassificationContract);

    //拨备计提查询审批通过的五级分类下属合同
    List<HlsCusFiveClassificationContract> selectFiveClassificationContractsForRp(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectBpInceptionLease(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectRwContractsByBpId(HlsCusFiveClassificationContract fiveClassificationContract);
    List<PlmPliContract> selectRwContractsByBpId2(HlsCusFiveClassificationContract fiveClassificationContract);


    List<HlsCusFiveClassificationContract> selectLastAndFirstResult(HlsCusFiveClassificationContract fiveClassificationContract);

    List<Map> selectOverdueConContractInfo(HlsCusFiveClassificationContract fiveClassificationContract);

    List<Map> selectOverdueFctContractInfo(HlsCusFiveClassificationContract fiveClassificationContract);

    List<Map> rcHomeRollTableQuery(HlsCusFiveClassificationContract fiveClassificationContract);

    List<Map> rcConContractBaseInfoQuery(HlsCusFiveClassificationContract fiveClassificationContract);

    List<Map> rcFctContractBaseInfoQuery(HlsCusFiveClassificationContract fiveClassificationContract);

    //租金催收首页chart1 应收金额
    List<Map> rcHomeChartAllDueAmountQuery(HlsCusFiveClassificationContract fiveClassificationContract);

    //租金催收首页chart1 逾期金额
    List<Map> rcHomeChartAllOverDueAmountQuery(HlsCusFiveClassificationContract fiveClassificationContract);

    //租金催收首页chart2 查询
    List<Map> rcHomeSecondChartQuery(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectContractsByPlmType(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectFcContractsByPlmType(HlsCusFiveClassificationContract fiveClassificationContract);

    List<String> selectUserNameByConContractIds(HlsCusFiveClassificationContract fiveClassificationContract);

    List<String> selectUserNameByFctContractIds(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectUserNameLatestByConContractIds(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectUserNameLatestByFctContractIds(HlsCusFiveClassificationContract fiveClassificationContract);

    //五级分类工作流查找对应人的合同
    List<HlsCusFiveClassificationContract> selectFiveClassificationActivitiConContracts(HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> selectFiveClassificationActivitiFctContracts(HlsCusFiveClassificationContract fiveClassificationContract);

    void deleteContractsOpinions(HlsCusFiveClassificationContract fiveClassificationContract);

    List<Long> selectUserIdByUserName(String userName);

    List<String> selectLatestApprovalNode(HlsCusFiveClassificationContract fiveClassificationContract);

    void updateConFiveClassificationResult(Map map);

    void updateFctFiveClassificationResult(Map map);

    List<Long> selectRcUserIds(Map map);

    List<HlsCusFiveClassificationContract> selectNewContractDefaultValue(HlsCusFiveClassificationContract fiveClassificationContract);

    /**
     * 保理核销情况
     * @param fiveClassificationContract
     * @return
     */
    List<Map> selectOverdueFctContractData(HlsCusFiveClassificationContract fiveClassificationContract);


    /**
     * 租赁核销情况
     * @param fiveClassificationContract
     * @return
     */
    List<Map> selectOverdueConContractData(HlsCusFiveClassificationContract fiveClassificationContract);


    /**
     * 查询主办分配ID
     * @param bpId
     * @param companyId
     * @return
     */
    List<Long> selectProjectEmployeeAssignId(@Param("bpId") Long bpId, @Param("companyId") Long companyId);

    /**
     * 查询员工部门=unitCode的数量
     * @param employeeCode
     * @return
     */
    int selectCountUnitNo(@Param("employeeCode") String employeeCode, @Param("unitCode") String unitCode);
}
