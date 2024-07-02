package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConChangeEtInfo;

import java.util.List;

public interface ConChangeEtInfoMapper extends Mapper<ConChangeEtInfo> {

    List<ConChangeEtInfo> queryChangeEtInfo(ConChangeEtInfo ccei);
}