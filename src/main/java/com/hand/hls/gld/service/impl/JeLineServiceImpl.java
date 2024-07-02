package com.hand.hls.gld.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.JeLine;
import com.hand.hls.gld.mapper.JeLineMapper;
import com.hand.hls.gld.service.IJeLineService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class JeLineServiceImpl extends BaseServiceImpl<JeLine> implements IJeLineService {
    @Autowired
    private JeLineMapper mapper;

    public JeLineServiceImpl() {
    }

    public List<JeLine> query(JeLine dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return this.mapper.query(dto);
    }

    public List<JeLine> queryNoPage(Map<String, Object> map) {
        return this.mapper.query(map);
    }

    public List<JeLine> queryCompany(JeLine dto) {
        return this.mapper.queryCompany(dto);
    }

    @Override
    public List<Map> selectJeLineInfoGroupAccount(IRequest iRequest, JeLine line, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum,pagesize);
        return mapper.selectJeLineInfoGroupAccount(line);
    }

    @Override
    public List<Map> selectJeLineInfo(IRequest iRequest, JeLine line, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum,pagesize);
        if(line.getJeLineIdStr() != null){
            List idList = new ArrayList();
            String[] lineArr = line.getJeLineIdStr().split(",");
            for(int i = 0; i < lineArr.length; i++){
                idList.add(Long.valueOf(lineArr[i]));
            }
            line.setJeLineIdList(idList);
        }
        return mapper.selectJeLineInfo(line);
    }
}
