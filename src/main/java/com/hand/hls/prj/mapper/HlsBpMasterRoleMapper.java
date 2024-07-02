//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsBpMasterRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hand.hls.prj.mapper.HlsBpMasterBankAccountMapper;

public interface HlsBpMasterRoleMapper<T extends HlsBpMasterRole> extends Mapper<HlsBpMasterRole> {
    List<HlsBpMasterRole> queryAll(HlsBpMasterRole var1);

    List<HlsBpMasterRole> queryRoleType(HlsBpMasterRole var1);

    List<String> queryType(Long var1);

    List<HlsBpMasterRole> selectType();

    List<HlsBpMasterRole> selectAllType();

    String selectBbpCategory(String var1);

    List<String> selectRoleById(Long var1);

    int updateRoleBpType(HlsBpMasterRole bpMasterRole);

    List<HlsBpMasterRole> querys(HashMap var1);

    List<HlsBpMasterRole> query2(Map map);
}
