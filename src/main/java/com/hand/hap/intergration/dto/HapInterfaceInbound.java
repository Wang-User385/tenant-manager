//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.intergration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

@Table(
        name = "sys_if_invoke_inbound"
)
@ExtensionAttribute(
        disable = true
)
public class HapInterfaceInbound extends BaseDTO {
    @Id
    @GeneratedValue
    private Long inboundId;
    @NotEmpty
    private String interfaceName;
    @NotEmpty
    private String interfaceUrl;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date requestTime;
    private String requestHeaderParameter;
    private String requestBodyParameter;
    private String responseContent;
    @Column(
            name = "STACKTRACE"
    )
    private String stackTrace;
    private String ip;
    private String referer;
    private String userAgent;
    private Long responseTime;
    private String requestMethod;
    private String requestStatus;
    @Transient
    private Integer page = Integer.valueOf(1);
    @Transient
    private Integer pagesize = Integer.valueOf(10);
    @Transient
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date startDate;
    @Transient
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date endDate;

    public HapInterfaceInbound() {
    }

    public void setInboundId(Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getInboundId() {
        return this.inboundId;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    public String getInterfaceUrl() {
        return this.interfaceUrl;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getRequestTime() {
        return this.requestTime;
    }

    public void setRequestHeaderParameter(String requestHeaderParameter) {
        this.requestHeaderParameter = requestHeaderParameter;
    }

    public String getRequestHeaderParameter() {
        return this.requestHeaderParameter;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public String getResponseContent() {
        return this.responseContent;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getReferer() {
        return this.referer;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public Long getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestBodyParameter() {
        return this.requestBodyParameter;
    }

    public void setRequestBodyParameter(String requestBodyParameter) {
        this.requestBodyParameter = requestBodyParameter;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPagesize() {
        return this.pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public String getRequestStatus() {
        return this.requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
