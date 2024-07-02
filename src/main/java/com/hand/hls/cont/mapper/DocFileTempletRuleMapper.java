package com.hand.hls.cont.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.DocFileTempletRule;

public interface DocFileTempletRuleMapper extends Mapper<DocFileTempletRule> {
    List<DocFileTempletRule> selectByFontCondition(DocFileTempletRule docFileTempletRule);

    /**
     * 根据 templetType 与 companyId 查询模板
     *
     * @param templetType 模板类型
     * @param companyId   公司 ID
     * @return DocFileTempletRule
     */
    DocFileTempletRule selectByTempletTypeAndCompanyId(@Param("templetType") String templetType, @Param("companyId") Long companyId);

    void updateRuleEngineTypleByRuleEngineId(DocFileTempletRule docFileTempletRule);
}