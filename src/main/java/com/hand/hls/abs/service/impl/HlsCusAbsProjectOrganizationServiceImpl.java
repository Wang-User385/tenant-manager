package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProFeeInfo;
import com.hand.hls.abs.dto.HlsCusAbsProjectOrganization;
import com.hand.hls.abs.mapper.HlsCusAbsProFeeInfoMapper;
import com.hand.hls.abs.mapper.HlsCusAbsProjectOrganizationMapper;
import com.hand.hls.abs.service.HlsCusAbsProjectOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProjectOrganizationServiceImpl extends BaseServiceImpl<HlsCusAbsProjectOrganization> implements HlsCusAbsProjectOrganizationService {
    @Autowired
    private HlsCusAbsProjectOrganizationMapper hlsCusAbsProjectOrganizationMapper;

    @Autowired
    private HlsCusAbsProFeeInfoMapper projectFeeInfoMapper;

    @Override
    public List<HlsCusAbsProjectOrganization> queryDetail(IRequest request, HlsCusAbsProjectOrganization organization, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsProjectOrganizationMapper.queryDetail(organization);
    }

    @Override
    public List<HlsCusAbsProjectOrganization> removeFlag(IRequest request, List<HlsCusAbsProjectOrganization> projectOrganizations) {
        for (HlsCusAbsProjectOrganization org : projectOrganizations) {
            org.setReceiverFlag("N");
            self().updateByPrimaryKeySelective(request, org);
        }
        return projectOrganizations;
    }

    @Override
    public void organizationBatchDelete(IRequest request, List<HlsCusAbsProjectOrganization> projectOrganizations) {

        HlsCusAbsProFeeInfo projectFeeInfo=new HlsCusAbsProFeeInfo();

        for(HlsCusAbsProjectOrganization projectOrganization:projectOrganizations){
            projectFeeInfo.setOrganizationId(projectOrganization.getOrganizationId());
            projectFeeInfoMapper.delete(projectFeeInfo);
        }
         self().batchDelete(projectOrganizations);
    }
}
