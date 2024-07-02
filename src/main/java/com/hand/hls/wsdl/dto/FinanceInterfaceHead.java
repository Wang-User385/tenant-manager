package com.hand.hls.wsdl.dto;

import lombok.Data;

/**
 * <p>汽车租赁接口数据封装消息头
 *
 * @author ferry ferry_sy@163.com
 * created by 2020/02/25 13:48
 */

@Data
public class FinanceInterfaceHead {
    /**
     * 系统标识
     */
    private String systemId;

    /**
     * 流水号 UUID
     */
    private String serialNumber;

    /**
     * 交易码 业务类型+环境（YX_PROJECT_UAT）
     */
    private String transactionCode;

    /**
     * 交易时间 格式为：yyyyMMdd HH:mm:ss
     */
    private String transactionTime;

    /**
     * 版本号 标识每个接口的版本
     */
    private Long version = 1L;

    /**
     * 错误代码 01成功/02失败，发送可为空，返回不可为空
     */
    private String errorCode;

    /**
     * 错误信息 简要错误信息，发送可为空，返回不可为空
     */
    private String errorMessage;


}
