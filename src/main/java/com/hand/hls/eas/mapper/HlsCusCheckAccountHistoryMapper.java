package com.hand.hls.eas.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eas.dto.HlsCusCheckAccountHistory;

public interface HlsCusCheckAccountHistoryMapper extends Mapper<HlsCusCheckAccountHistory>{


    HlsCusCheckAccountHistory isExistsNo(HlsCusCheckAccountHistory dto);

}