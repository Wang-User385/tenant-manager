package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.ReplyProductPara;
import com.hand.hls.prj.mapper.ReplyProductParaMapper;
import com.hand.hls.prj.service.IReplyProductParaService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReplyProductParaServiceImpl extends BaseServiceImpl<ReplyProductPara> implements IReplyProductParaService {
    @Autowired
    private ReplyProductParaMapper replyProductParaMapper;

    @Override
    public List<ReplyProductPara> selectReplyProductParaList(IRequest request, @Param("replyProductId") Long replyProductId, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<ReplyProductPara> replyProductParaList = replyProductParaMapper.selectReplyProductParaList(replyProductId);
        return replyProductParaList;
    }

    @Override
    public List<ReplyProductPara> manufacturerQueryProductParaInfo(IRequest iRequest, ReplyProductPara replyProductPara, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return replyProductParaMapper.manufacturerQueryProductParaInfo(replyProductPara);
    }

    @Override
    public List<ReplyProductPara> query(IRequest iRequest, ReplyProductPara replyProductPara, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return replyProductParaMapper.query(replyProductPara);
    }
}
