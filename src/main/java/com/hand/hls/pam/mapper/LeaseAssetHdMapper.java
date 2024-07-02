package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.LeaseAssetHd;

import java.util.List;
import java.util.Map;

public interface LeaseAssetHdMapper extends Mapper<LeaseAssetHd>{
    List<LeaseAssetHd> selectLeaseAssetHd();

    LeaseAssetHd selectLeaseAssetHdDet(Map map);
}