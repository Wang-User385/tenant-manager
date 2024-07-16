package com.hand.hls.credit.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 预审商业伙伴表字段返回接收类
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/11 16:44
 */
@Data
public class QueryHlsBpMasterDTO {

    /**
     * 客户编号
     */
    private String bpCode;

    /**
     * 客户名称
     */
    private String bpName;

    /**
     * 客户证件类型
     */
    private String idType;

    /**
     * 客户证件号码
     */
    private String idCardNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 证件签发日期
     */
    private Date idIssueDate;

    /**
     * 证件到期日期
     */
    private Date idExpirationDate;



}
