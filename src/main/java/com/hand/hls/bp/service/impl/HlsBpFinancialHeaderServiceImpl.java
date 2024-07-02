package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpFinancialHeaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpFinancialHeader;
import com.hand.hls.bp.service.IHlsBpFinancialHeaderService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpFinancialHeaderServiceImpl extends BaseServiceImpl<HlsBpFinancialHeader> implements IHlsBpFinancialHeaderService{

    @Autowired
    private HlsBpFinancialHeaderMapper hlsBpFinancialHeaderMapper;

    @Override
    public List<HlsBpFinancialHeader> selectBpExportInfo(IRequest iRequest, HlsBpFinancialHeader hlsBpFinancialHeader, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum,pagesize);
        return hlsBpFinancialHeaderMapper.selectBpExportInfo(hlsBpFinancialHeader);
    }
}