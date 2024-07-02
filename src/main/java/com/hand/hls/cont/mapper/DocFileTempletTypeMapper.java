//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.DocFileTempletType;

import java.util.List;

public interface DocFileTempletTypeMapper extends Mapper<DocFileTempletType> {
    //List<RuleEngineType> selectRuleEngineTypeWithDataSourceNameIf(RuleEngineType var1);
    List<DocFileTempletType> selectFileTempletTypeLov(DocFileTempletType var1);
}
