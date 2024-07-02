package com.hand.hls.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sys.dto.HlsCusSysAppointApprover;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusSysAppointApproverMapper extends Mapper<HlsCusSysAppointApprover>{
    List<String> selectSysAppointApprover(@Param("processInstanceId") Long processInstanceId, @Param("sidCode") String sidCode);

    List<HlsCusSysAppointApprover> selectSysAppointApproverDetail(HlsCusSysAppointApprover hlsCusSysAppointApprover);


    List<String> getPrjAppointApprover(@Param("sourceId") Long sourceId, @Param("sourceTable") String sourceTable);

    List<String> getLegalAffairsApprover(@Param("sourceId") Long sourceId, @Param("sourceTable") String sourceTable, @Param("sidCode") String sidCode);

    List<String> getOtherLegalAffairsApprover(@Param("sourceId") Long sourceId, @Param("sourceTable") String sourceTable, @Param("sidCode") String sidCode);

    List<String> getDefaultPrjAppointApprover();

    List<String> getDefalutLegalAffairsApprover();

    List<String> getRemoveDefalutLegalAffairsApprover();

    List<String> getContractOtherLegalAffairsApprover(@Param("processInstanceId") Long processInstanceId, @Param("sidCode") String sidCode);

    String getAssignByUserName(@Param("names") String names);
}