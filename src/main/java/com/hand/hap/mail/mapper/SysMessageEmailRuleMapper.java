package com.hand.hap.mail.mapper;

import com.hand.hap.mail.dto.SysMessageEmailRule;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysMessageEmailRuleMapper extends Mapper<SysMessageEmailRule>{
    List<Map> query(Map map);

    /**
     * 查询规则记录
     */
    SysMessageEmailRule queryForSendEmailByRuleCode(@Param("ruleCode")String ruleCode);
}
