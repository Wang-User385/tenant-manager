//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsDocFileParaTable;
import com.hand.hls.cont.mapper.HlsDocFileParaTableMapper;
import com.hand.hls.cont.service.HlsDocFileParaTableService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HlsDocFileParaTableServiceImpl extends BaseServiceImpl<HlsDocFileParaTable> implements HlsDocFileParaTableService {
    @Autowired
    HlsDocFileParaTableMapper HlsDocFileParaTableMapper;

    public HlsDocFileParaTableServiceImpl() {
    }
    @Override
    protected boolean useSelectiveUpdate(){
        return false;
    }
    @Override
    public List<HlsDocFileParaTable> selectList(HlsDocFileParaTable HlsDocFileParaTable) {
        return this.HlsDocFileParaTableMapper.selectList(HlsDocFileParaTable);
    }
}
