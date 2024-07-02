package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsBpPersons;


import java.util.List;

public interface HlsBpPersonsMapper extends Mapper<HlsBpPersons> {
    List<HlsBpPersons> selectForLovIf(HlsBpPersons var1);

    List<HlsBpMaster> selectProjectLovIf(HlsBpMaster var1);
}
