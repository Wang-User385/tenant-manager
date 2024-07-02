package com.hand.hls.wsdl.dto;

import lombok.Data;

/**
 * <p>三一接口数据封装消息头
 * @author luobiao
 * created by 2022/04/06 14:48
 */
@Data
public class SyInterfaceHead {
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
     * 渠道编号(传定值，如: minshengjinzu)
     */
    private String channelCode;

    /**
     * 渠道名称(传定值，如: 民生金租)
     */
    private String channelName;

    /**
     * 返回编码，000000：处理成功，999999：处理失败
     */
    private String record;

    /**
     * 返回信息
     */
    private String reg;

    /**
     * 版本号
     */
    private Long version;
}
