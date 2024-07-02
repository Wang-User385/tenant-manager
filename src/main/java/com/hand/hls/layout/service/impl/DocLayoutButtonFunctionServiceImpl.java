package com.hand.hls.layout.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.function.dto.Function;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.mapper.DocLayoutButtonFunctionMapper;
import com.hand.hls.layout.service.IDocLayoutButtonFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutButtonFunctionServiceImpl extends BaseServiceImpl<Function> implements IDocLayoutButtonFunctionService {
   @Autowired
   DocLayoutButtonFunctionMapper docLayoutButtonFunctionMapper;
    public List<Function> selectForFunctionInfo(IRequest requestContext, Function param, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return docLayoutButtonFunctionMapper.selectForFunctionInfo(param);
    }
}
