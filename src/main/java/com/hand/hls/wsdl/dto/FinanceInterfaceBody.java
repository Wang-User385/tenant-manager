package com.hand.hls.wsdl.dto;

import lombok.Data;

/**
 * <p>汽车租赁接口数据封装消息头
 *
 * @author ferry ferry_sy@163.com
 * created by 2020/02/25 13:48
 */

@Data
public class FinanceInterfaceBody {
    /**
     * 状态 S：成功，E失败，R:重复传输
     */
    private String type;

    /**
     * 信息 消息
     */
    private String message;

    /**
     * 业务信息
     */
    private String businessKey;
}
