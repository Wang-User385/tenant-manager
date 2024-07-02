package com.hand.hls.hls.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.hls.dto.HlsWebExcel;
import com.hand.hls.hls.mapper.HlsWebExcelConfigLnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;
import com.hand.hls.hls.service.IHlsWebExcelConfigLnService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsWebExcelConfigLnServiceImpl extends BaseServiceImpl<HlsWebExcelConfigLn> implements IHlsWebExcelConfigLnService{

    @Autowired
    private HlsWebExcelConfigLnMapper lnMapper;

    @Override
    public List<HlsWebExcelConfigLn> selectHlsWebExcelConfiglineByHdId(IRequest iRequest, HlsWebExcelConfigLn ln, int pagenum, int pagesize) {

        PageHelper.startPage(pagenum,pagesize);
        return lnMapper.selectHlsWebExcelConfiglineByHdId(ln);
    }
}