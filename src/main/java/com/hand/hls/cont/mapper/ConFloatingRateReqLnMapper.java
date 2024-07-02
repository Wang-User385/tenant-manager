package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/16 - 14:21
 */
public interface ConFloatingRateReqLnMapper<T extends HlsCusConFloatingRateReqLn> extends Mapper<HlsCusConFloatingRateReqLn> {
    List<HlsCusConFloatingRateReqLn> queryConFloatingRateReqLn(HlsCusConFloatingRateReqLn var1);

    void cancleRateChange(HlsCusConFloatingRateReqLn var1);
}
