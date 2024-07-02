package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.service.HlsFinStatementLnService;
import com.hand.hls.fnd.dto.HlsFinStatementLn;
import com.hand.hls.fnd.mapper.HlsFinStatementLnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wty on 2018/7/4.
 */
@Service
@Transactional
public class HlsFinStatementLnServiceImpl extends BaseServiceImpl<HlsFinStatementLn> implements HlsFinStatementLnService {
    @Autowired
    private HlsFinStatementLnMapper hlsFinStatementLnMapper;

    @Override
    public List<HlsFinStatementLn> lnQuery(HlsFinStatementLn hlsFinStatementLn, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsFinStatementLn> list = hlsFinStatementLnMapper.lnQuery(hlsFinStatementLn);
        return list;
    }
}
