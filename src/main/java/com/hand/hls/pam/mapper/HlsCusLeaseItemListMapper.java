package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.HlsCusLeaseItemList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusLeaseItemListMapper extends Mapper<HlsCusLeaseItemList> {
    List<HlsCusLeaseItemList> selectItemList(Long leaseItemId);
    List<HlsCusLeaseItemList> selectItemList1(@Param("leaseItemId") Long leaseItemId);
    Double selectItemTotal(@Param("leaseItemId") Long leaseItemId);
}
