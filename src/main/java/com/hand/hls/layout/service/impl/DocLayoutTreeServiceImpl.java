package com.hand.hls.layout.service.impl;

import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayoutTree;
import com.hand.hls.layout.mapper.DocLayoutTreeMapper;
import com.hand.hls.layout.service.IDocLayoutTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutTreeServiceImpl extends BaseServiceImpl<DocLayoutTree> implements IDocLayoutTreeService {

    @Autowired
    private DocLayoutTreeMapper docLayoutTreeMapper;

    @Override
    public Map queryFlag(DocLayoutTree docLayoutTree) {
        return docLayoutTreeMapper.queryFlag(docLayoutTree);
    }

    @Override
    public ResponseData selectDocLayoutTree(CompositeMap map, String whereStr) {
        List<CompositeMap> list = docLayoutTreeMapper.selectDocLayoutTree(map);
        return new ResponseData(list);
    }


}