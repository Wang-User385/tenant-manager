package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.HlsCusAccount;

import java.util.List;

public interface HlsCusAccountMapper extends Mapper<HlsCusAccount>{

    List<HlsCusAccount> queryAccountCode();

    List<HlsCusAccount> queryModify(HlsCusAccount var1);
}