package com.hand.hls.fin.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonContract;

import java.util.List;
import java.util.Map;

public interface LonContractMapper<T extends HlsCusLonContract> extends Mapper<HlsCusLonContract> {
    List<Map> lonConBoard(Long companyId);

    List<HlsCusLonContract> selectLonContract(HlsCusLonContract lonContract);

    List<HlsCusLonContract> selectLonContractFormData(HlsCusLonContract lonContract);

    /*综合查询*/
    List<HlsCusLonContract> lonContractQuery(Map<String, Object> params);


}