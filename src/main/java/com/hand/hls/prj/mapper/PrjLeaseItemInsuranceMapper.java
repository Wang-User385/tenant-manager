package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.PrjLeaseItemInsurance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrjLeaseItemInsuranceMapper extends Mapper<PrjLeaseItemInsurance>{
    /**
     * 查看指定的保险编号系统中存在数量
     */
    Integer queryInsuranceNumberExistsCount(@Param("insuranceNumber") String insuranceNumber);

    List<PrjLeaseItemInsurance> queryByProjectId(@Param("projectId") Long projectId);

    PrjLeaseItemInsurance selectInsByLeaseItemId(@Param("projectLeaseItemId") Long projectLeaseItemId);

    List<PrjLeaseItemInsurance> selectNew(@Param("projectLeaseItemId") Long projectLeaseItemId);
}