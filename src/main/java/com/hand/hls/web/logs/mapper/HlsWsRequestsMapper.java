package com.hand.hls.web.logs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;

import java.util.List;

/**
 *
 * @author liao
 */
public interface HlsWsRequestsMapper extends Mapper<HlsWsRequests>{


    /**
     * 创建数据
     * @param hlsWsRequests 对象
     * @return 返回主键
     */
    Long create(HlsWsRequests hlsWsRequests);

    /**
     * 查询数据
     * @param hlsWsRequests 对象
     * @return 返回符合条件的记录
     */
    List<HlsWsRequests> findByHlsWsRequests(HlsWsRequests hlsWsRequests);
}