package com.hand.hls.ecif.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;

import java.util.List;

public interface HlsCusBpMasterRequestRecordsMapper extends Mapper<HlsCusBpMasterRequestRecords>{

    List<HlsCusBpMasterRequestRecords> selectByOutboundId(HlsCusBpMasterRequestRecords dto);

    List<HlsCusBpMasterRequestRecords> selectByOutboundIdAndBpId(HlsCusBpMasterRequestRecords dto);


    List<HlsCusBpMasterRequestRecords> selectByBpId(HlsCusBpMasterRequestRecords dto);


    HlsCusBpMasterRequestRecords getDataByReturnToBusinessData(HlsCusBpMasterRequestRecords dto);


    List<HlsCusBpMasterRequestRecords> selectByProjectId(HlsCusBpMasterRequestRecords dto);

    HlsCusBpMasterRequestRecords selectChangeDate();

    List<HlsCusBpMasterRequestRecords> selectByCstNo(HlsCusBpMasterRequestRecords dto);

}