package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsCusImpSegment;
import com.hand.hls.fnd.mapper.HlsCusImpSegmentMapper;
import com.hand.hls.fnd.service.HlsCusImpSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusImpSegmentServiceImpl extends BaseServiceImpl<HlsCusImpSegment> implements HlsCusImpSegmentService {
    @Autowired
    private HlsCusImpSegmentMapper mapper;

    @Override
    public List<HlsCusImpSegment> selectSegmentByTempId(IRequest iRequest, HlsCusImpSegment segment){
        return mapper.selectSegmentByTempId(segment);
    }

    @Override
    public Map<String,HlsCusImpSegment> listToMap(List<CodeValue> numberCodeValues, List<HlsCusImpSegment> segments){
        Map<String, HlsCusImpSegment> map=new HashMap<>();
        if(numberCodeValues==null){//如果快码为空的话，初始化一个list，防止空指针
            numberCodeValues=new ArrayList<>();
        }
        if(segments!=null){
            segments=updateSegmentFormat(numberCodeValues,segments);
            for(HlsCusImpSegment s:segments){
                map.put(s.getSegmentDesc().trim(),s);
            }
            return map;
        }
        return null;
    }

    @Override
    public List<HlsCusImpSegment> updateSegmentFormat(List<CodeValue> numberCodeValues, List<HlsCusImpSegment> segments){
        if(numberCodeValues==null){//如果快码为空的话，初始化一个list，防止空指针
            numberCodeValues=new ArrayList<>();
        }
        if(segments!=null){
            for(HlsCusImpSegment s:segments){
                if(HlsCusImpSegment.TYPE_CODE_NUMBER.equals(s.getTypeCode())){//如果类型是数字的话
                    boolean unUpdated=true;//修改标识符
                    //修改format
                    for(CodeValue v:numberCodeValues){
                        if(v.getValue().equals(s.getFormat())){
                            s.setFormat(v.getTag());
                            unUpdated=false;
                        }
                    }
                    //未修改过format
                    if(unUpdated){
                        s.setFormat("");
                    }
                }
            }
        }
        return segments;
    }

    @Override
    public Map<String,HlsCusImpSegment> listToMap(List<HlsCusImpSegment> segments){
        Map<String, HlsCusImpSegment> map=new HashMap<>();
        if(segments!=null){
            for(HlsCusImpSegment s:segments){
                map.put(s.getSegmentDesc(),s);
            }
            return map;
        }
        return null;
    }

    @Override
    public Map<String,String> getDescriptionMap(List<HlsCusImpSegment> segments){
        Map<String,String> descriptionMap=new HashMap<>();
        if(segments!=null){
            for(HlsCusImpSegment s:segments){
                descriptionMap.put(s.getSegmentCode(),s.getSegmentDesc());
            }
        }
        return descriptionMap;
    }
}