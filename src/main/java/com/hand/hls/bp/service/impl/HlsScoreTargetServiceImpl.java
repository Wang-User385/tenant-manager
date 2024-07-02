//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.FndScoreTargetValues;
import com.hand.hls.bp.dto.HlsScoreTarget;
import com.hand.hls.bp.mapper.FndScoreTargetValuesMapper;
import com.hand.hls.bp.mapper.HlsScoreTargetMapper;
import com.hand.hls.bp.service.IHlsScoreTargetService;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HlsScoreTargetServiceImpl extends BaseServiceImpl<HlsScoreTarget> implements IHlsScoreTargetService {
    @Autowired
    private HlsScoreTargetMapper hlsScoreTargetMapper;
    @Autowired
    private FndScoreTargetValuesMapper fndScoreTargetValuesMapper;

    public HlsScoreTargetServiceImpl() {
    }

    public List<HlsScoreTarget> query(IRequest request, HlsScoreTarget dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return this.hlsScoreTargetMapper.query(dto);
    }

    public void deleteHl(List<HlsScoreTarget> list) {
        FndScoreTargetValues childMap = new FndScoreTargetValues();
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            HlsScoreTarget child = (HlsScoreTarget)var3.next();
            childMap.setScoreTargetId(child.getScoreTargetId());
            this.fndScoreTargetValuesMapper.delete(childMap);
            this.hlsScoreTargetMapper.delete(child);
        }

    }
}
