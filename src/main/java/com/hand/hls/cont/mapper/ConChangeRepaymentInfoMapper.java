package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConChangeRepaymentInfo;

import java.util.List;

public interface ConChangeRepaymentInfoMapper extends Mapper<ConChangeRepaymentInfo> {


    List<ConChangeRepaymentInfo> queryChangeRepaymentInfo(ConChangeRepaymentInfo ccri);
}