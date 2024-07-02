package com.hand.hls.app.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.app.dto.HlsCashflowAyncDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCashflowAyncMapper extends Mapper<HlsCashflowAyncDto> {

    List<HlsCashflowAyncDto> getRepayment(@Param("fromDate") String fromDate,@Param("toDate") String toDate);

    List<HlsCashflowAyncDto> getAllRepayment();

    HlsCashflowAyncDto getOneRepayment(@Param("contractId") Long contractId);

}
