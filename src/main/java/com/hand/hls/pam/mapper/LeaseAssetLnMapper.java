package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.LeaseAssetLn;

import java.util.List;

public interface LeaseAssetLnMapper extends Mapper<LeaseAssetLn>{
    List<LeaseAssetLn> selectLeaseAssetLn();

    List<LeaseAssetLn> selectLeaseAssetLnList();
}