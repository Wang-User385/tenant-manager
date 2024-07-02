package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductOrganization;
import com.hand.hls.abs.mapper.HlsCusAbsProductOrganizationMapper;
import com.hand.hls.abs.service.HlsCusAbsProductOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductOrganizationServiceImpl extends BaseServiceImpl<HlsCusAbsProductOrganization> implements HlsCusAbsProductOrganizationService {
    @Autowired
    private HlsCusAbsProductOrganizationMapper hlsCusAbsProductOrganizationMapper;

    @Override
    public List<HlsCusAbsProductOrganization> queryDetail(IRequest request, HlsCusAbsProductOrganization organization, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsProductOrganizationMapper.queryDetail(organization);
    }
}
