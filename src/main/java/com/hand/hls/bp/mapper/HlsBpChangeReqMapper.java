package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpChangeReq;

import java.util.List;

public interface HlsBpChangeReqMapper extends Mapper<HlsBpChangeReq>{

    List<HlsBpChangeReq> queryAll(HlsBpChangeReq hlsBpChangeReq);

}