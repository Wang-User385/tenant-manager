package com.hand.hls.wsdl.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 返回信息
 * @author wade
 */
@Data
public class SyInterfaceResponseData {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 请求时间(HH:mm:ss)/返回时间
     */
    private String reqTime;

    /**
     * 请求日期(yyyy-MM-dd)/返回日期
     */
    private String reqDate;

    /**
     * 返回编码，000000：处理成功，999999：处理失败
     */
    private String recode;

    /**
     * 返回信息
     */
    private String reg;

    /**
     * 返回具体数据
     */
    private JSONObject data;
}
