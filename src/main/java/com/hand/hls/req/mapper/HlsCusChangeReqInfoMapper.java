package com.hand.hls.req.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;

import java.util.List;
import java.util.Map;

public interface HlsCusChangeReqInfoMapper extends Mapper<HlsCusChangeReqInfo> {
    List<HlsCusChangeReqInfo> queryChangeInfo(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    List<HlsCusChangeReqInfo> queryContractChangeInfo(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    List<HlsCusChangeReqInfo> queryHistory(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    List<HlsCusChangeReqInfo> queryStatus(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    Map queryConChangeType(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    Map queryPrcContractChangeType();

    List<HlsCusChangeReqInfo> selectConChangeType(Map<String, Object> map);

    List<HlsCusChangeReqInfo> selectDocuemntSubmitChangeInfo(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    List<HlsCusChangeReqInfo> queryChangeInfoByChangeReqId();
    List<HlsCusChangeReqInfo> queryChangeInfoByChangeReqId1(Long changeReqId);
}