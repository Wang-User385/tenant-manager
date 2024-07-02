package com.hand.hls.layout.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayoutConfigLov;
import com.hand.hls.layout.mapper.DocLayoutConfigLovMapper;
import com.hand.hls.layout.service.IDocLayoutConfigLovService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutConfigLovServiceImpl extends BaseServiceImpl<DocLayoutConfigLov> implements IDocLayoutConfigLovService {

    @Autowired
    private DocLayoutConfigLovMapper docLayoutConfigLovMapper;

    @Override
    public List<DocLayoutConfigLov> selectByConfigId(IRequest requestContext, DocLayoutConfigLov layoutConfigLov, int page, int pageSize) {
        Page<Object> objects = PageHelper.startPage(page, pageSize);
        Example example = new Example(DocLayoutConfigLov.class);
        example.createCriteria().andEqualTo(DocLayoutConfigLov.FIELD_CONFIG_ID, layoutConfigLov.getConfigId());
        return docLayoutConfigLovMapper.selectByExample(example);
    }
}