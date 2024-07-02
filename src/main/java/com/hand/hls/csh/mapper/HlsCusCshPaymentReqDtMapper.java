package com.hand.hls.csh.mapper;

import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/1 - 16:55
 */
public interface HlsCusCshPaymentReqDtMapper extends CshPaymentReqDtMapper<HlsCusCshPaymentReqDt>  {

    List<HlsCusCshPaymentReqDt> queryPaymentReqDt(HlsCusCshPaymentReqDt var1);
}
