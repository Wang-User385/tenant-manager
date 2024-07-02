package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

import java.util.List;

public interface HlsCreditLineMapper<T extends HlsCusHlsCreditLine> extends Mapper<HlsCusHlsCreditLine> {
  List<HlsCusHlsCreditLine> hlsCreditLineQueryBySource(HlsCusHlsCreditLine hlsCreditLine);

    void updateCreditLine(@Param("sourceDocumentCategory") String sourceDocumentCategory,
                          @Param("sourceDocumentId") Long sourceDocumentId,
                          @Param("userId") Long userId,
                          @Param("date") Date date);
   List<HlsCusHlsCreditLine> selectCreditLineStatusInfo(HlsCusHlsCreditLine hlsCusHlsCreditLine);
   List<HlsCusHlsCreditLine> selectCreditLineInfo(HlsCusHlsCreditLine hlsCusHlsCreditLine);
   List<HlsCusHlsCreditLine> creditLineDetailQuery(HlsCusHlsCreditLine hlsCusHlsCreditLine);
   List<HlsCusHlsCreditLine> hlsCreditQuery(HlsCusHlsCreditLine hlsCusHlsCreditLine);
   List<HlsCusHlsCreditLine> hlsCreditContractQuery(HlsCusHlsCreditLine hlsCusHlsCreditLine);

   List<HlsCusHlsCreditLine> hlsCreditBpQuery(HlsCusHlsCreditLine hlsCusHlsCreditLine);

   List<HlsCusHlsCreditLine> updateCreditlineInfo(HlsCusHlsCreditLine hlsCusHlsCreditLine);

   List<HlsCusHlsCreditLine> creditLineIntegratedQuery(HlsCusHlsCreditLine hlsCusHlsCreditLine);
}
