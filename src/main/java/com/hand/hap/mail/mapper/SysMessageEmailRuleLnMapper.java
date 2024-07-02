package com.hand.hap.mail.mapper;

import com.hand.hap.mail.dto.SysMessageEmailRuleLn;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysMessageEmailRuleLnMapper extends Mapper<SysMessageEmailRuleLn>{
    List<Map> query(Map map);
    /**
     * 查询收件人数据
     */
    List<SysMessageEmailRuleLn> queryData(@Param("ruleId") Long ruleId);
}
