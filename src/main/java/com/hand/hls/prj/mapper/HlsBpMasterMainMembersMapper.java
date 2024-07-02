package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsBpMasterMainMembers;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsBpMasterMainMembersMapper extends Mapper<HlsBpMasterMainMembers>{

    List<HlsBpMasterMainMembers> queryByBpId (@Param("bpId")Long bpId);
}