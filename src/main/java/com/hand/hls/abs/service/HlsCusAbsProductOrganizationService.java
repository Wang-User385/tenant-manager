package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductOrganization;

import java.util.List;

public interface HlsCusAbsProductOrganizationService extends IBaseService<HlsCusAbsProductOrganization>, ProxySelf<HlsCusAbsProductOrganizationService> {
    /**
     * 产品-机构查询
     * 产品明细-机构表格
     */
    List<HlsCusAbsProductOrganization> queryDetail(IRequest request, HlsCusAbsProductOrganization organization, int page, int pageSize);


}
