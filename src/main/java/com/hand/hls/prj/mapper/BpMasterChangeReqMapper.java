package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.BpMasterChangeReq;

import java.util.List;

public interface BpMasterChangeReqMapper extends Mapper<BpMasterChangeReq>{

    List<BpMasterChangeReq> queryVersionNum(BpMasterChangeReq req);

    List<BpMasterChangeReq> queryChangeNewStatusNum(BpMasterChangeReq req);

}