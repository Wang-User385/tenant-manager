package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMasterShareholder;
import com.hand.hls.prj.dto.HlsBpMasterShareholder;
import com.hand.hls.prj.mapper.HlsBpMasterShareholderMapper;

import java.util.List;
import java.util.Map;

public interface HlsCusBpMasterShareholderMapper<T extends HlsBpMasterShareholderMapper >  extends Mapper<HlsCusBpMasterShareholder> {
    List<HlsBpMasterShareholder> queryShareholderDetails(Map map);
}
