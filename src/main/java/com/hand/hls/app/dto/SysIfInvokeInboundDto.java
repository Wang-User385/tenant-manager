package com.hand.hls.app.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author PC
 */
@ExtensionAttribute(disable = true)
@Table(name = "sys_if_invoke_inbound")
@Getter
@Setter
public class SysIfInvokeInboundDto extends BaseDTO {
    @Id
    @GeneratedValue
    private Long inboundId;
    /**
     * 接口名称
     * */
    private String interfaceName;
    /**
     * url
     * */
    private String interfaceUrl;
    /**
     * 请求时间
     * */
    private Date requestTime;
    /**
     * 请求header参数
     * */
    private String requestHeaderParameter;
    /**
     * 请求body参数
     * */
    private String requestBodyParameter;
    /**
     * 请求方式
     */
    private String requestMethod;
    /**
     * 请求状态
     */
    private String requestStatus;
    /**
     * 响应内容
     */
    private String responseContent;
    /**
     * 响应时间
     */
    private Long responseTime;
    /**
     * 错误堆栈
     */
    private String stacktrace;
    /**
     * ip地址
     */
    private String ip;
}
