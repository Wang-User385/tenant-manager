package com.hand.hls.eas.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eas.dto.HlsCusCoreAccountHistory;

import java.util.List;

public interface HlsCusCoreAccountHistoryMapper extends Mapper<HlsCusCoreAccountHistory>{

  void  insertCheckAccountHistroryData(HlsCusCoreAccountHistory dto);

  void  insertHlsCheckAccountHistroryData(HlsCusCoreAccountHistory dto);

  void  deleteCurrentHistroryData(HlsCusCoreAccountHistory dto);

  void  deleteCurrentEasHistroryData(HlsCusCoreAccountHistory dto);

  void  deleteCurrentHlsEasHistroryData(HlsCusCoreAccountHistory dto);


  List<HlsCusCoreAccountHistory> selectDataByCheckDate(HlsCusCoreAccountHistory dto);
}