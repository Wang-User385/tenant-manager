//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsDocFileTempletPara;
import com.hand.hls.cont.mapper.HlsDocFileTempletParaMapper;
import com.hand.hls.cont.service.HlsDocFileTempletParaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HlsDocFileTempletParaServiceImpl extends BaseServiceImpl<HlsDocFileTempletPara> implements HlsDocFileTempletParaService {
    @Autowired
    HlsDocFileTempletParaMapper mapper;

    public HlsDocFileTempletParaServiceImpl() {
    }

    @Override
    protected boolean useSelectiveUpdate(){
        return false;
    }

    @Override
    public List<HlsDocFileTempletPara> selectList(HlsDocFileTempletPara hlsDocFileTempletPara, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.mapper.selectList(hlsDocFileTempletPara);
    }
}
