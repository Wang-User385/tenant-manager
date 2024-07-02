package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsCusImpSegment;

import java.util.List;
import java.util.Map;

public interface HlsCusImpSegmentService extends IBaseService<HlsCusImpSegment>, ProxySelf<HlsCusImpSegmentService> {

    /**
     * 查询模板行信息通过模板ID
     * @param iRequest
     * @param segment
     * @return
     */
    List<HlsCusImpSegment> selectSegmentByTempId(IRequest iRequest, HlsCusImpSegment segment);

    /**
     * list转map
     * @param numberCodeValues
     * @param segments
     * @return
     */
    Map<String, HlsCusImpSegment> listToMap(List<CodeValue> numberCodeValues, List<HlsCusImpSegment> segments);

    /**
     * list转map
     * @param segments
     * @return
     */
    Map<String,HlsCusImpSegment> listToMap(List<HlsCusImpSegment> segments);

    Map<String,String> getDescriptionMap(List<HlsCusImpSegment> segments);

    List<HlsCusImpSegment> updateSegmentFormat(List<CodeValue> numberCodeValues, List<HlsCusImpSegment> segments);
}