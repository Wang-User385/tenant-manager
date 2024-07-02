package com.hand.hls.hn.dto;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.cont.dto.ConContract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "con_contract")
@Getter
@Setter
public class CheckPlanConContract extends BaseDTO {

    @Id
    @GeneratedValue
    private Long contractId;

    private Long projectId;

    private Long changeReqId;

    private Long quotationId;

    @NotEmpty
    @Length(max = 100)
    private String businessType;

    @Length(max = 100)
    private String contractNumber;

    @Length(max = 200)
    private String contractName;

    @Length(max = 100)
    private String contractStatus;

    private Long tenantId;

    private Long employeeId;

    private Long unitId;

    private Date leaseStartDate;

    private Date firstPayDate;

    @Transient
    private String bpName;//承租人名称

    @Transient
    private String businessTypeDesc;//业务类型描述

    @Transient
    private String documentTypeDesc;//单据类型描述

    private Long hostProjectManager;
    @Transient
    private String hostProjectManagerN;

    private Long projectAssistant2;

    private Double proposerEmployeeRatio;

    private Double projectAssistantRatio;

    private Double projectAssistant2Ratio;

    private Long riskHost;
    private Double riskHostRatio;
    private Long riskAssistantFirst;
    private Long riskAssistantSecond;
    private Double riskAssistantFirstRatio;
    private Double riskAssistantSecondRatio;
    @Transient
    private String projectAssistant2Name;
    @Transient
    private String projectAssistant2N;
    @Transient
    private String riskHostN;
    @Transient
    private String riskAssistantFirstN;
    @Transient
    private String riskAssistantSecondN;

    private Long legalHost;
    private Double legalHostRatio;
    private Long legalAssistantFirst;
    private Long legalAssistantSecond;
    private Double legalAssistantFirstRatio;
    private Double legalAssistantSecondRatio;
    @Transient
    private String legalHostN;
    @Transient
    private String legalAssistantFirstN;
    @Transient
    private String legalAssistantSecondN;

}
