package com.hand.hls.sys.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.sys.dto.HlsCusSysUserAuthorityTrx;
import com.hand.hls.sys.service.HlsCusSysUserAuthorityTrxService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusSysUserAuthorityTrxServiceImpl extends BaseServiceImpl<HlsCusSysUserAuthorityTrx> implements HlsCusSysUserAuthorityTrxService {


    @Override
    public List<HlsCusSysUserAuthorityTrx> queryAuthorUser(IRequest iRequest, HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx) {
        return null;
    }

    @Override
    public void insertUserAuthor(IRequest iRequest, String documentCategory, Long documentId, String unitType) {

    }

    @Override
    public void insertChiefUserAuthor(IRequest iRequest, String documentCategory, Long documentId) {

    }

    @Override
    public List<HlsCusSysUserAuthorityTrx> queryAuthorityList(IRequest iRequest, HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx, int page, int pageSize) {
        return null;
    }

    @Override
    public List<HlsCusSysUserAuthorityTrx> queryAuthorityDocument(IRequest iRequest, HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx, int page, int pageSize) {
        return null;
    }

    @Override
    public void updatePrjProjectCreatedBy(HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx) {

    }

    @Override
    public void updateFctProjectCreatedBy(HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx) {

    }

    @Override
    public void updateLonContractCreatedBy(HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx) {

    }
}