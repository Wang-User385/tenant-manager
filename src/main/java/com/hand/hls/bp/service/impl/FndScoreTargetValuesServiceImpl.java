//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.FndScoreTargetValues;
import com.hand.hls.bp.mapper.FndScoreTargetValuesMapper;
import com.hand.hls.bp.service.IFndScoreTargetValuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FndScoreTargetValuesServiceImpl extends BaseServiceImpl<FndScoreTargetValues> implements IFndScoreTargetValuesService {
    public FndScoreTargetValuesServiceImpl() {
    }

    @Autowired
    private FndScoreTargetValuesMapper fndScoreTargetValuesMapper;
    @Override
    public List<FndScoreTargetValues> findScoreTargetValueList(IRequest requestContext, FndScoreTargetValues fndScoreTargetValues, int pagenum, int pageSize) {
       /* Map map = new HashMap();
        map.put("fndScoreTargetValues",fndScoreTargetValues);*/
        return fndScoreTargetValuesMapper.findScoreTargetValueList(fndScoreTargetValues);
    }
}
