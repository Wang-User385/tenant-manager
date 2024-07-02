package com.hand.hls.GENER.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsGeneralIssueMapper extends Mapper<HlsGeneralIssue>{
    List<HlsGeneralIssue> queryHlsGeneralIssue(HlsGeneralIssue hlsgeneralissue);
    Long queryGeneralUnitId(@Param("nextApprovePerson") Long nextApprovePerson);
    Long queryGeneralPositionId(@Param("unitId") Long unitId);
    String queryGeneralEmployeeCode(@Param("positionId") Long positionId);
    List<HlsGeneralIssue> queryHlsGeneralIssueNewWfl(HlsGeneralIssue hlsgeneralissue);

}