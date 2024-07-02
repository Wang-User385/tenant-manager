package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsCusImpBatch;
import com.hand.hls.fnd.dto.HlsCusImpData;
import com.hand.hls.fnd.dto.HlsCusImpSegment;

import java.util.List;
import java.util.Map;

public interface HlsCusImpDataService extends IBaseService<HlsCusImpData>, ProxySelf<HlsCusImpDataService> {
    /**
     * 解析文件
     * @param iRequest
     * @param batchId
     * @return
     */


    /**
     * 获得临时数据
     * @param iRequest
     * @param batchId
     * @return
     */
    List<Map<String,String>> getDataMap(IRequest iRequest, float batchId);

    List<HlsCusImpSegment> getSegment(IRequest iRequest, HlsCusImpBatch batch);

    /**
     * 插入批次
     * @param iRequest
     * @param datas
     * @return
     */
    int batchInsert(IRequest iRequest, List<HlsCusImpData> datas);

    /**
     * 更新错误信息
     * @param map
     * @param errMessage
     */
    void updateErrMessage(Map<String, String> map, String... errMessage);

    /**
     * 更新错误信息
     * @param dataMap
     * @param index
     * @param errMessage
     */
    void updateErrMessage(List<Map<String, String>> dataMap, int index, String... errMessage);

    void batchUpdateImpStatus(IRequest iRequest, float batchId, String beforeStatus, String afterStatus);

}