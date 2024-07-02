package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.mapper.JcFundFillingLnMapper;
import leaf.utils.ConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundFillingLn;
import com.hand.hls.fp.service.JcFundFillingLnService;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundFillingLnServiceImpl extends BaseServiceImpl<JcFundFillingLn> implements JcFundFillingLnService{

    @Autowired
    private JcFundFillingLnMapper mapper;

    @Override
    public List<JcFundFillingLn> selectDetailAll(IRequest request,JcFundFillingLn jcFundFillingLn,int page,int pageSize) {
        PageHelper.startPage(page,pageSize);
        return mapper.queryDetail(jcFundFillingLn);
    }
    @Override
    public ResponseData queryFiledPrompt(CompositeMap map, String var2){
        List answer;
        CompositeMap parameter = (CompositeMap) map.get("parameter");
        String fillYear = parameter.get("fill_year").toString();
        String fillMon = parameter.get("fill_mon").toString();
        String fillWeek = parameter.get("fill_week").toString();
        JcFundFillingLn ln = new JcFundFillingLn();
        if(fillYear != null && fillYear != ""){
            ln.setFillYear(fillYear);
        }
        if(fillMon != null && fillMon != ""){
            ln.setFillMon(fillMon);
        }
        if(fillWeek != null && fillWeek != ""){
            ln.setFillWeek(fillWeek);
        }
        if (ConfigUtils.isMySQL()) {
            answer = this.mapper.queryFiledPrompt(ln);
        } else {
            answer = this.mapper.queryFiledPrompt(ln);
        }
        return new ResponseData(answer);
    }
}