package com.hand.hls.layout.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.LovFunctionBmAccessDto;
import com.hand.hls.sys.dto.SysFunctionBmAccessDto;
import com.hand.hls.sys.mapper.SysFunctionBmAccessMapper;
import com.hand.hls.sys.service.ISysFunctionBmAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author: Eric Chen
 * @Email : qiang.chen04@hand-china.com
 * @Date: 2019/1/21 14:01
 */
@Service
@Transactional
public class SysFunctionBmAccessServiceImpl extends BaseServiceImpl<SysFunctionBmAccessDto> implements ISysFunctionBmAccessService {

    @Autowired
    private SysFunctionBmAccessMapper sysFunctionBmAccessMapper;

    @Override
    public List<LovFunctionBmAccessDto> selectForLov(Map param, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return sysFunctionBmAccessMapper.selectForLov(param);
    }
}
