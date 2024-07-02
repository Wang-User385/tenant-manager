package com.hand.hls.fct.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/9 - 15:59
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ExtensionAttribute(disable = true)
@Table(name = "HLS_CREDIT_LINE_CHANCE_BP")
public class HlsCusHlsCreditLineChanceBp extends BaseDTO {

    @Id
    @GeneratedValue
    private Long chanceBpId;

    private Long chanceId;

    private Long bpId;

    private String bpName;
    private String bpEngName;
    private String bpType;

    private String note;
    @Transient
    private String bpNameN;
    @Transient
    private String bpIdN;
    @Transient
    private String bpCode;
    @Transient
    private String bpTypeN;

    private String bpClass;

    @Transient
    private String bpClassN;

    private String marketingReportId;

    private String bpCatagory;

    @Transient
    private String tenantSecId;
    @Transient
    private String guarantorId;
    @Transient
    private String guarantorNpId;

    @Transient
    private String tenantSecIdN;

    private String status;

    private String groupCompanies;
    private String economicInduClassify;
    @Transient
    private String economicInduClassifyN;

    private Double creditAmount;
    private Double creditAmountUsed;
    private Long creditProjectId;
    @Transient
    private String contractNumber;
    @Transient
    private String allocatFlag;

}
