package com.hand.hls.plm.pli.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.pli.dto.PlmPliContract;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PlmPliContractMapper extends Mapper<PlmPliContract> {

    /**
     * PLM_PLI_CONTRACT表数据查询
     * @param dto
     * @return
     */
    List<PlmPliContract> selectPlmPliContractData(PlmPliContract dto);

    List<PlmPliContract> selectOtherAll(PlmPliContract dto);

    List<PlmPliContract> selectOtherAllProject(PlmPliContract dto);

    List<PlmPliContract> selectMeetingRiskDescription(PlmPliContract dto);


    /**
     * 合同主办分配ID
     * @param postloanInspectionId
     * @return
     */
    List<Long>  selectContractEmployAssignId(@Param("postloanInspectionId") Long postloanInspectionId);

    /**
     * 部门ID
     *
     * @param postloanInspectionId
     * @return
     */
    List<Long> selectContractUnitId(@Param("postloanInspectionId") Long postloanInspectionId);

}