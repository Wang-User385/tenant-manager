package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsCusImpTemplate;

import java.util.List;

public interface HlsCusImpTemplateMapper extends Mapper<HlsCusImpTemplate> {

    /**
     * 通过编码查询模板
     * @param template
     * @return
     */
    HlsCusImpTemplate selectTemplateByCode(HlsCusImpTemplate template);

    /**
     * 查询模板编码
     * @return
     */
    List<HlsCusImpTemplate> selectTempCode();

}