package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductOrganization;

import java.util.List;

public interface HlsCusAbsProductOrganizationMapper extends Mapper<HlsCusAbsProductOrganization> {
    /**
     * 产品明细-机构信息查询
     */
    List<HlsCusAbsProductOrganization> queryDetail(HlsCusAbsProductOrganization organization);
}
