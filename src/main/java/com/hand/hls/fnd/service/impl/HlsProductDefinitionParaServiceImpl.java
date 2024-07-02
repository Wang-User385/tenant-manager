package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import com.hand.hls.fnd.mapper.HlsProductDefinitionParaMapper;
import com.hand.hls.fnd.service.IHlsProductDefinitionParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsProductDefinitionParaServiceImpl extends BaseServiceImpl<HlsProductDefinitionPara> implements IHlsProductDefinitionParaService {

    @Autowired
    private HlsProductDefinitionParaMapper hlsProductDefinitionParaMapper;

    @Override
    public List<HlsProductDefinitionPara> selectHlsProductDefinitionParaList(IRequest request, HlsProductDefinition hlsProductDefinition, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsProductDefinitionPara> hlsProductDefinitionParaList = hlsProductDefinitionParaMapper.selectHlsProductDefinitionParaList(hlsProductDefinition);
        return hlsProductDefinitionParaList;
    }
}