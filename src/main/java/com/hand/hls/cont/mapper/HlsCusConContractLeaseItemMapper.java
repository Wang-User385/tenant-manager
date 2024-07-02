package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;
import com.hand.hls.lease.dto.YxLeaseItemClassify;

import java.util.List;
import java.util.Map;

public interface HlsCusConContractLeaseItemMapper extends Mapper<HlsCusConContractLeaseItem> {

    /**
     * 租赁物查询（带权限）
     * @param conContract 查询条件
     * @return 租赁物信息
     */
    List<Map<String, Object>> queryContractLeaseItem(Map<String, Object> conContract);
    List<HlsCusConContractLeaseItem> queryContractLeaseItemDetail(Map map);
}