package com.hand.hls.eas.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;

import java.util.List;

public interface HlsCusEasSourceRecordMapper extends Mapper<HlsCusEasSourceRecord>{


    List<HlsCusEasSourceRecord>  selectEasType01(HlsCusEasSourceRecord dto);

    List<HlsCusEasSourceRecord>  selectEasType04(HlsCusEasSourceRecord dto);

    List<HlsCusEasSourceRecord>  selectEasType05(HlsCusEasSourceRecord dto);

    List<HlsCusEasSourceRecord>  selectEasTypebm(HlsCusEasSourceRecord dto);

    List<HlsCusEasSourceRecord>  selectEasType11(HlsCusEasSourceRecord dto);

    HlsCusEasSourceRecord  selectEasTypeCommon(HlsCusEasSourceRecord dto);

    List<HlsCusEasSourceRecord>  selectEasTypePay04(HlsCusEasSourceRecord dto);

}