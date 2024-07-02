package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConfirmBatchTemp;

import java.util.List;
import java.util.Map;

public interface ConfirmBatchTempMapper extends Mapper<ConfirmBatchTemp>{
    List<Map> queryForConfirmDetailLview(Map map);
}