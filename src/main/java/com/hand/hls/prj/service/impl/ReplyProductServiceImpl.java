package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.ReplyProduct;
import com.hand.hls.prj.mapper.ReplyProductMapper;
import com.hand.hls.prj.service.IReplyProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReplyProductServiceImpl extends BaseServiceImpl<ReplyProduct> implements IReplyProductService{

    @Autowired
    private ReplyProductMapper mapper;

    @Override
    public List<ReplyProduct> manufacturerQueryProductInfo(IRequest iRequest, ReplyProduct replyProduct, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return mapper.manufacturerQueryProductInfo(replyProduct);
    }
    @Override
    public List<ReplyProduct> manufacturerQueryProductInfo1(IRequest iRequest, ReplyProduct replyProduct, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return mapper.manufacturerQueryProductInfo1(replyProduct);
    }
}