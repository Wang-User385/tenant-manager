//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sys.dto.SysUserAuthorityRule;

import java.util.List;

public interface SysUserAuthorityRuleMapper extends Mapper<SysUserAuthorityRule> {
    List<SysUserAuthorityRule> selectList(SysUserAuthorityRule var1);

    /**
     * 查询 用户权限信息
     * @param var1  请求对象
     * @return 返回结果集
     */
    List<SysUserAuthorityRule> queryUserRule(SysUserAuthorityRule var1);
}
