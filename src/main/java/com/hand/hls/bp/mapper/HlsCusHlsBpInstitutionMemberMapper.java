package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusHlsBpInstitutionMember;

import java.util.List;

public interface HlsCusHlsBpInstitutionMemberMapper extends Mapper<HlsCusHlsBpInstitutionMember> {
    List<HlsCusHlsBpInstitutionMember> queryAll(HlsCusHlsBpInstitutionMember dto);

}