package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpMasterBusinessCondition;
import java.util.List;

public interface HlsBpMasterBusinessConditionMapper extends Mapper<HlsBpMasterBusinessCondition>{
    List<HlsBpMasterBusinessCondition> queryAllByBpId(HlsBpMasterBusinessCondition hlsBpMasterBusinessCondition);
}