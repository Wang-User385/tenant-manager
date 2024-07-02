package com.hand.hls.bp.service.impl;

import java.util.List;

import com.hand.hls.bp.mapper.HlsCusBpMasterRoleMapper;
import com.hand.hls.bp.service.HlsBpMasterRoleService;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

@Service
@Transactional
public class HlsBpMasterRoleServiceImpl extends BaseServiceImpl<HlsBpMasterRole> implements HlsBpMasterRoleService {

    @Autowired
    private HlsCusBpMasterRoleMapper mapper;

    @Override
    public List<HlsBpMasterRole> queryAll(IRequest requestContext, HlsBpMasterRole bpMasterRole, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(bpMasterRole);
    }

    @Override
    public List<String> queryType(Long bpId) {
        // TODO Auto-generated method stub
        return mapper.queryType(bpId);
    }

    @Override
    public List<HlsBpMasterRole> selectType() {
        // TODO Auto-generated method stub
        return mapper.selectType();
    }

    @Override
    public List<HlsBpMasterRole> selectAllType() {
        // TODO Auto-generated method stub
        return mapper.selectAllType();
    }

    @Override
    public String selectBbpCategory(String bpType) {
        // TODO Auto-generated method stub
        return mapper.selectBbpCategory(bpType);
    }

    @Override
    public List<String> selectRoleById(Long bpId) {
        // TODO Auto-generated method stub
        return mapper.selectRoleById(bpId);
    }

    @Override
    public List<HlsBpMasterRole> queryRoleType(IRequest requestContext, HlsBpMasterRole bpMasterRole, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryRoleType(bpMasterRole);
    }

    @Override
    public int updateRoleBpType(HlsBpMasterRole bpMasterRole) {
        return mapper.updateRoleBpType(bpMasterRole);
    }
}
