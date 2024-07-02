package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpHeadOfficeList;

import java.util.List;

public interface BpHeadOfficeListMapper extends Mapper<BpHeadOfficeList>{

    List<BpHeadOfficeList> queryAll(BpHeadOfficeList bpHeadOfficeList);

}