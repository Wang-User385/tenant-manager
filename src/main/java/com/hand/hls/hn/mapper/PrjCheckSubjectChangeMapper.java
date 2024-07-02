package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckSubjectChange;
import com.hand.hls.hn.dto.PrjLeaseInspect;

import java.util.List;

public interface PrjCheckSubjectChangeMapper extends Mapper<PrjCheckSubjectChange>{
    List<PrjCheckSubjectChange> queryList(PrjCheckSubjectChange prjCheckSubjectChange);

    //
    List<PrjCheckSubjectChange> queryListPrjCheckSubjectChange(PrjCheckSubjectChange prjCheckSubjectChange);
}