package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.BpMasterEmployee;

import java.util.List;
import java.util.Map;

public interface BpMasterEmployeeMapper extends Mapper<BpMasterEmployee>{

    List<BpMasterEmployee> queryManufacturerEmployeeBySlipId(Long slipId);
    List<BpMasterEmployee> queryBpMasterEmployeeByContract(Map map);
    List<BpMasterEmployee> queryBpMasterEmployeeBySQ();

    /**
     * 查找厂商最早的关联业务经理
     */
    Long queryEmployeeIdByManufacturerId(Long manufacturerId);

    String queryUnitCodeByEmployeeId(Long employeeId);
    List<BpMasterEmployee> queryBpMasterEmployeeDetail(Long bpId);

}
