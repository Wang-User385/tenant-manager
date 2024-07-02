package com.hand.hls.wfl.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.wfl.dto.HlsCusWflAppointApprover;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusWflAppointApproverMapper extends Mapper<HlsCusWflAppointApprover> {

    List<String> selectAppointApprover(@Param("processInstanceId")Long processInstanceId , @Param("sidCode")String sidCode);

    List<HlsCusWflAppointApprover> queryAppointApprover(HlsCusWflAppointApprover hlsCusCshBillAppointApprover);

    List<String> selectAppointApproverBySourceId(HlsCusWflAppointApprover hlsCusCshBillAppointApprover);
}