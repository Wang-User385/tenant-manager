package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjLeaseFactoryBuilding;
import com.hand.hls.hn.dto.PrjLeaseInfo;
import com.hand.hls.hn.dto.PrjLeaseInspect;

import java.util.List;

public interface PrjLeaseInfoMapper extends Mapper<PrjLeaseInfo>{

    List<PrjLeaseInfo> selectPrjLeaseInfo(PrjLeaseInfo prjLeaseInfo);

    List<PrjLeaseInfo> selectLeaseItemByProjectId(Long projectId);

    List<PrjLeaseInfo> selectLeaseInfoCount(PrjLeaseInspect prjLeaseInfo);
}