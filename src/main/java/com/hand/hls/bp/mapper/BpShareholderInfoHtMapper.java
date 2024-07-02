package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpShareholderInfoHt;

import java.util.List;

public interface BpShareholderInfoHtMapper extends Mapper<BpShareholderInfoHt>{

    List<BpShareholderInfoHt> queryBpShareholderInfoHt(BpShareholderInfoHt bpShareholderInfoHt);
}