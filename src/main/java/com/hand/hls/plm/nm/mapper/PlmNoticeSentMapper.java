package com.hand.hls.plm.nm.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.nm.dto.PlmNoticeSent;

import java.util.List;

public interface PlmNoticeSentMapper extends Mapper<PlmNoticeSent> {
    List<PlmNoticeSent> selectNoticeSentAll(PlmNoticeSent dto);
}