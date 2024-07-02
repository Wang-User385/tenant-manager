package com.hand.hls.cont.mapper;


import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;

import java.util.List;

/**
 * Created by wangyan on 2017/10/24.
 * Modify by zhangyu on 2018/5/30
 */
public interface HlsCusConFloatingRateReqLnMapper extends ConFloatingRateReqLnMapper<HlsCusConFloatingRateReqLn> {
    List<HlsCusConFloatingRateReqLn> ctRateChangeListQuery(HlsCusConFloatingRateReq dto);

    HlsCusConFloatingRateReqLn ctRateChangeDetailQuery(HlsCusConFloatingRateReqLn dto);

    List<HlsCusConFloatingRateReqLn> queryFloatlnCalcDetail(HlsCusConFloatingRateReqLn var1);

    List<HlsCusConFloatingRateReqLn> queryFloatByReqId(HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn);

}
