package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProjectOrganization;

import java.util.List;

public interface HlsCusAbsProjectOrganizationMapper extends Mapper<HlsCusAbsProjectOrganization> {

    /**
     * ABS立项明细机构信息查询
     */
    List<HlsCusAbsProjectOrganization> queryDetail(HlsCusAbsProjectOrganization organization);

}
