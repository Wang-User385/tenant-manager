package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.HlsCusJeHead;
import java.util.List;
import java.util.Map;

public interface HlsCusJeHeadMapper extends Mapper<HlsCusJeHead> {
    List<HlsCusJeHead> jeListQuery(Map<String, Object> var1);
    List<HlsCusJeHead> jeListQueryDetail(Map<String, Object> var1);
    List<HlsCusJeHead> jeListQueryTrial(Map<String, Object> var1);
    List<HlsCusJeHead> jeListQueryRetrial(Map<String, Object> var1);
    List<HlsCusJeHead> gldJeDataQuery(HlsCusJeHead dto);

    List<HlsCusJeHead> queryNotConfirmBy(HlsCusJeHead cusJeHead);

    List<Map> selectHeadInfo(HlsCusJeHead hlsCusJeHead);


}
