package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProjectOrganization;

import java.util.List;

public interface HlsCusAbsProjectOrganizationService extends IBaseService<HlsCusAbsProjectOrganization>, ProxySelf<HlsCusAbsProjectOrganizationService> {
    List<HlsCusAbsProjectOrganization> queryDetail(IRequest request, HlsCusAbsProjectOrganization organization, int page, int pageSize);

    List<HlsCusAbsProjectOrganization> removeFlag(IRequest request, List<HlsCusAbsProjectOrganization> projectOrganizations);


    void organizationBatchDelete(IRequest request, List<HlsCusAbsProjectOrganization> projectOrganizations);
}
