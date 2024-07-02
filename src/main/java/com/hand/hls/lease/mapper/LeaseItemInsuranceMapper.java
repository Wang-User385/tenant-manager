package com.hand.hls.lease.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.lease.dto.LeaseItemInsurance;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LeaseItemInsuranceMapper extends Mapper<LeaseItemInsurance>{
    List<LeaseItemInsurance> selectNew(@Param("conLeaseItemId")Long conLeaseItemId);
    List<LeaseItemInsurance> queryAll(Map map);
    List<LeaseItemInsurance> queryContractLeaseItemInsurance(@Param("contractId")Long contractId);

    /**
     * 查看指定的保险编号系统中存在数量
     */
    Integer queryInsuranceNumberExistsCount(@Param("insuranceNumber") String insuranceNumber);

    /**
     * 查询已经逾期的保险
     * @param contractLeaseItemId
     * @return
     */
    List<LeaseItemInsurance> selectOverdueInsurance(@Param("contractLeaseItemId") Long contractLeaseItemId);
}
