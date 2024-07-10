package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBusinessConditionBank;

import java.util.List;


public interface HlsBusinessConditionBankMapper extends Mapper<HlsBusinessConditionBank>{
    List<HlsBusinessConditionBank> queryAllByConditionId(HlsBusinessConditionBank hlsBusinessConditionBank);
}