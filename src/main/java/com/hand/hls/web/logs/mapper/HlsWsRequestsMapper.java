package com.hand.hls.web.logs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;

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
}