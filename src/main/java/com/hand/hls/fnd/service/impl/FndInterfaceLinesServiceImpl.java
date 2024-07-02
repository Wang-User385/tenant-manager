package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.service.FndInterfaceLinesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/22
 * @description:
 */
@Service
public class FndInterfaceLinesServiceImpl extends BaseServiceImpl<FndInterfaceLines> implements FndInterfaceLinesService {
    @Autowired
    FndInterfaceLinesMapper fndInterfaceLinesMapper;



    @Override
    public List<FndInterfaceLines> fndInterfaceLinesDetailQuery(IRequest iRequest, FndInterfaceLines fndInterfaceLines, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
    }
}
