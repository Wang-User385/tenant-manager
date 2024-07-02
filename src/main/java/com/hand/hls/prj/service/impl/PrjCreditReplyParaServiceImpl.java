package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.PrjCreditReplyPara;
import com.hand.hls.prj.mapper.PrjCreditReplyParaMapper;
import com.hand.hls.prj.service.IPrjCreditReplyParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjCreditReplyParaServiceImpl extends BaseServiceImpl<PrjCreditReplyPara> implements IPrjCreditReplyParaService {

    @Autowired
    private PrjCreditReplyParaMapper bpReplyParaMapper;

    @Override
    public List<PrjCreditReplyPara> manufacturerBpParaQuery(IRequest iRequest, PrjCreditReplyPara bpReplyPara, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return bpReplyParaMapper.manufacturerBpParaQuery(bpReplyPara);
    }
}