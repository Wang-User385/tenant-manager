package com.hand.hls.cont.mapper;

import com.hand.hls.cont.dto.HlsCusConContractChangeReq;

import java.util.List;
import java.util.Map;

public interface HlsCusConContractChangeReqMapper extends ConContractChangeReqMapper<HlsCusConContractChangeReq> {

    /**
     * 大单提前部分还本变更单查询
     * @param map
     * @return
     */
    List<HlsCusConContractChangeReq> queryContractChangePartialPrepaymentReqDetail(Map map);
}