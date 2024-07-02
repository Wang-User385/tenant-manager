package com.hand.hls.ecif.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;

import java.util.List;

public interface HlsCusEcifBpMasterChangeMapper extends Mapper<HlsCusEcifBpMasterChange>{

  HlsCusEcifBpMasterChange selectChangeInfoByBpId(HlsCusEcifBpMasterChange dto);


  HlsCusEcifBpMasterChange selectChangeApprovedReturn(HlsCusEcifBpMasterChange dto);

  List<HlsCusEcifBpMasterChange> selectByEcifHistory(HlsCusEcifBpMasterChange dto);

}