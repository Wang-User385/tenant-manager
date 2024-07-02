//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CustomerLevelCaculator;
import com.hand.hls.bp.dto.FndScoreTemplateDbSource;
import com.hand.hls.bp.mapper.FndScoreTemplateDbSourceMapper;
import com.hand.hls.bp.service.IFndScoreTemplateDbSourceService;
import com.hand.hls.fnd.components.Datasource2Json;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FndScoreTemplateDbSourceServiceImpl extends BaseServiceImpl<FndScoreTemplateDbSource> implements IFndScoreTemplateDbSourceService {
    @Resource
    private FndScoreTemplateDbSourceMapper fndScoreTemplateDbSourceMapper;
    @Autowired
    Datasource2Json datasource2Json;
    @Autowired
    CustomerLevelCaculator customerLevelCaculator;

    public FndScoreTemplateDbSourceServiceImpl() {
    }

    public List<HashMap> combDs() {
        return this.fndScoreTemplateDbSourceMapper.combDs1();
    }

    public Long fndScoreTemplateDbSource(IRequest iRequest, Long scoreTemplateHdId, Map<String, Object> map) {
        List<Long> listHId= new ArrayList<>();
        if("Y".equalsIgnoreCase(map.get("is_fiscal").toString())){
            listHId = this.fndScoreTemplateDbSourceMapper.sourceId1(scoreTemplateHdId);
        }
        Long ScoreResultId = null;
        Map<String, Object> maps = new HashMap();

        for(int i = 0; i < listHId.size(); ++i) {
            maps.put(((Long)listHId.get(i)).toString(), map);
        }

        try {
            String data = this.datasource2Json.executeSQLs4Json(listHId, maps);
            JSONObject jsonObject = JSONObject.parseObject(data);
            ScoreResultId = this.customerLevelCaculator.calEntry(iRequest, scoreTemplateHdId, jsonObject);
            return ScoreResultId;
        } catch (IOException var9) {
            var9.printStackTrace();
            return ScoreResultId;
        }
    }

    public List<FndScoreTemplateDbSource> selectDbSourceName(IRequest iRequest, FndScoreTemplateDbSource dto, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.fndScoreTemplateDbSourceMapper.selectDbSourceName1(dto);
    }

    public int batchDelete(List<FndScoreTemplateDbSource> list) {
        list.forEach((item) -> {
            this.fndScoreTemplateDbSourceMapper.delete(item);
        });
        return 0;
    }
}
