package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpShareholderInfo;

import java.util.List;

public interface HlsBpShareholderInfoMapper extends Mapper<HlsBpShareholderInfo>{
    List<HlsBpShareholderInfo> queryHlsBpShareholderInfo(HlsBpShareholderInfo hlsBpShareholderInfo);

}