package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.VirtualAccount;

import java.util.List;
import java.util.Map;

public interface VirtualAccountMapper extends Mapper<VirtualAccount>{
    /**
     * 查询未被分配的虚拟账号列表
     * @param virtualAccount
     * @return
     */
    List<VirtualAccount> queryUnusedVirtualAccount(VirtualAccount virtualAccount);

    Long queryRepeatAccount(Map map);
}