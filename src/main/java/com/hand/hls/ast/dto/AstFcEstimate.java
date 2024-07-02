//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ast_fc_estimate"
)
public class AstFcEstimate extends BaseDTO {
    public static final String FIELD_FC_ESTIMATE_ID = "fcEstimateId";
    public static final String FIELD_FIVE_CLASS_PLAN = "fiveClassPlan";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_ESTIMATE_NUM = "estimateNum";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ESTIMATE_DATE = "estimateDate";
    public static final String FIELD_INTERNAL_PERIOD_NUM = "internalPeriodNum";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_REF_V01 = "refV01";
    public static final String FIELD_REF_V02 = "refV02";
    public static final String FIELD_REF_V03 = "refV03";
    public static final String FIELD_REF_V04 = "refV04";
    public static final String FIELD_REF_V05 = "refV05";
    public static final String FIELD_REF_N01 = "refN01";
    public static final String FIELD_REF_N02 = "refN02";
    public static final String FIELD_REF_N03 = "refN03";
    public static final String FIELD_REF_N04 = "refN04";
    public static final String FIELD_REF_N05 = "refN05";
    public static final String FIELD_REF_D01 = "refD01";
    public static final String FIELD_REF_D02 = "refD02";
    public static final String FIELD_REF_D03 = "refD03";
    public static final String FIELD_REF_D04 = "refD04";
    public static final String FIELD_REF_D05 = "refD05";
    @Id
    @GeneratedValue
    private Long fcEstimateId;
    @NotEmpty
    @Length(
            max = 30
    )
    private String fiveClassPlan;
    @NotNull
    private Long companyId;
    @Length(
            max = 30
    )
    private String estimateNum;
    @Length(
            max = 2000
    )
    private String description;
    private Date estimateDate;
    private Long internalPeriodNum;
    @Length(
            max = 30
    )
    private String status;
    @Length(
            max = 2000
    )
    private String refV01;
    @Length(
            max = 2000
    )
    private String refV02;
    @Length(
            max = 2000
    )
    private String refV03;
    @Length(
            max = 2000
    )
    private String refV04;
    @Length(
            max = 2000
    )
    private String refV05;
    private Long refN01;
    private Long refN02;
    private Long refN03;
    private Long refN04;
    private Long refN05;
    private Date refD01;
    private Date refD02;
    private Date refD03;
    private Date refD04;
    private Date refD05;

    public AstFcEstimate() {
    }


    //新增字段

    private Long projectId;
    private Long employeeId;
    private Long unitId;
    private String sysTenClassResult;
    private String startTenClassResult;
    private String startScoreDesc;
    private String finalAssetsClassResult;
    private String raiseStatus;
    private Double breakRate;
    private String nowCountStatus;
    private String contractNumber;
    private Long processInstanceId;
    private Double nowCount;

    private Long planId;//组后检查计划
    @Transient
    private String planIdN;
    @Transient
    private String projectNumber;

    public String getPlanIdN() {
        return planIdN;
    }

    public void setPlanIdN(String planIdN) {
        this.planIdN = planIdN;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Double getNowCount() {
        return nowCount;
    }

    public void setNowCount(Double nowCount) {
        this.nowCount = nowCount;
    }
    @Transient
    private String startScoreDescN;

    public String getStartScoreDescN() {
        return startScoreDescN;
    }

    public void setStartScoreDescN(String startScoreDescN) {
        this.startScoreDescN = startScoreDescN;
    }

    @Transient
    private String tenantIdN;

    @Transient
    private String fiveClassPlanN;

    public String getFiveClassPlanN() {
        return fiveClassPlanN;
    }

    public void setFiveClassPlanN(String fiveClassPlanN) {
        this.fiveClassPlanN = fiveClassPlanN;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTenantIdN() {
        return tenantIdN;
    }

    public void setTenantIdN(String tenantIdN) {
        this.tenantIdN = tenantIdN;
    }

    @Transient
    private String contractName;

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    @Transient
    private String employeeIdN;
    @Transient
    private String unitIdN;
    @Transient
    private String businessTypeN;

    @Transient
    private Date estimateDateFrom;
    @Transient
    private Date estimateDateTo;
    @Transient
    private String sysTenClassResultN;
    @Transient
    private String startTenClassResultN;
    @Transient
    private String finalAssetsClassResultN;
    @Transient
    private String raiseStatusN;
    @Transient
    private String nowCountStatusN;

    @Transient
    private String statusN;

    public String getStatusN() {
        return statusN;
    }

    public void setStatusN(String statusN) {
        this.statusN = statusN;
    }

    public String getSysTenClassResultN() {
        return sysTenClassResultN;
    }

    public void setSysTenClassResultN(String sysTenClassResultN) {
        this.sysTenClassResultN = sysTenClassResultN;
    }

    public String getStartTenClassResultN() {
        return startTenClassResultN;
    }

    public void setStartTenClassResultN(String startTenClassResultN) {
        this.startTenClassResultN = startTenClassResultN;
    }

    public String getFinalAssetsClassResultN() {
        return finalAssetsClassResultN;
    }

    public void setFinalAssetsClassResultN(String finalAssetsClassResultN) {
        this.finalAssetsClassResultN = finalAssetsClassResultN;
    }

    public String getRaiseStatusN() {
        return raiseStatusN;
    }

    public void setRaiseStatusN(String raiseStatusN) {
        this.raiseStatusN = raiseStatusN;
    }

    public String getNowCountStatusN() {
        return nowCountStatusN;
    }

    public void setNowCountStatusN(String nowCountStatusN) {
        this.nowCountStatusN = nowCountStatusN;
    }

    public Date getEstimateDateFrom() {
        return estimateDateFrom;
    }

    public void setEstimateDateFrom(Date estimateDateFrom) {
        this.estimateDateFrom = estimateDateFrom;
    }

    public Date getEstimateDateTo() {
        return estimateDateTo;
    }

    public void setEstimateDateTo(Date estimateDateTo) {
        this.estimateDateTo = estimateDateTo;
    }

    public String getEmployeeIdN() {
        return employeeIdN;
    }

    public void setEmployeeIdN(String employeeIdN) {
        this.employeeIdN = employeeIdN;
    }

    public String getUnitIdN() {
        return unitIdN;
    }

    public void setUnitIdN(String unitIdN) {
        this.unitIdN = unitIdN;
    }

    public String getBusinessTypeN() {
        return businessTypeN;
    }

    public void setBusinessTypeN(String businessTypeN) {
        this.businessTypeN = businessTypeN;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getSysTenClassResult() {
        return sysTenClassResult;
    }

    public void setSysTenClassResult(String sysTenClassResult) {
        this.sysTenClassResult = sysTenClassResult;
    }

    public String getStartTenClassResult() {
        return startTenClassResult;
    }

    public void setStartTenClassResult(String startTenClassResult) {
        this.startTenClassResult = startTenClassResult;
    }

    public String getStartScoreDesc() {
        return startScoreDesc;
    }

    public void setStartScoreDesc(String startScoreDesc) {
        this.startScoreDesc = startScoreDesc;
    }

    public String getFinalAssetsClassResult() {
        return finalAssetsClassResult;
    }

    public void setFinalAssetsClassResult(String finalAssetsClassResult) {
        this.finalAssetsClassResult = finalAssetsClassResult;
    }

    public String getRaiseStatus() {
        return raiseStatus;
    }

    public void setRaiseStatus(String raiseStatus) {
        this.raiseStatus = raiseStatus;
    }

    public Double getBreakRate() {
        return breakRate;
    }

    public void setBreakRate(Double breakRate) {
        this.breakRate = breakRate;
    }

    public String getNowCountStatus() {
        return nowCountStatus;
    }

    public void setNowCountStatus(String nowCountStatus) {
        this.nowCountStatus = nowCountStatus;
    }

    public void setFcEstimateId(Long fcEstimateId) {
        this.fcEstimateId = fcEstimateId;
    }

    public Long getFcEstimateId() {
        return this.fcEstimateId;
    }

    public void setFiveClassPlan(String fiveClassPlan) {
        this.fiveClassPlan = fiveClassPlan;
    }

    public String getFiveClassPlan() {
        return this.fiveClassPlan;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setEstimateNum(String estimateNum) {
        this.estimateNum = estimateNum;
    }

    public String getEstimateNum() {
        return this.estimateNum;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setEstimateDate(Date estimateDate) {
        this.estimateDate = estimateDate;
    }

    public Date getEstimateDate() {
        return this.estimateDate;
    }

    public void setInternalPeriodNum(Long internalPeriodNum) {
        this.internalPeriodNum = internalPeriodNum;
    }

    public Long getInternalPeriodNum() {
        return this.internalPeriodNum;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setRefV01(String refV01) {
        this.refV01 = refV01;
    }

    public String getRefV01() {
        return this.refV01;
    }

    public void setRefV02(String refV02) {
        this.refV02 = refV02;
    }

    public String getRefV02() {
        return this.refV02;
    }

    public void setRefV03(String refV03) {
        this.refV03 = refV03;
    }

    public String getRefV03() {
        return this.refV03;
    }

    public void setRefV04(String refV04) {
        this.refV04 = refV04;
    }

    public String getRefV04() {
        return this.refV04;
    }

    public void setRefV05(String refV05) {
        this.refV05 = refV05;
    }

    public String getRefV05() {
        return this.refV05;
    }

    public void setRefN01(Long refN01) {
        this.refN01 = refN01;
    }

    public Long getRefN01() {
        return this.refN01;
    }

    public void setRefN02(Long refN02) {
        this.refN02 = refN02;
    }

    public Long getRefN02() {
        return this.refN02;
    }

    public void setRefN03(Long refN03) {
        this.refN03 = refN03;
    }

    public Long getRefN03() {
        return this.refN03;
    }

    public void setRefN04(Long refN04) {
        this.refN04 = refN04;
    }

    public Long getRefN04() {
        return this.refN04;
    }

    public void setRefN05(Long refN05) {
        this.refN05 = refN05;
    }

    public Long getRefN05() {
        return this.refN05;
    }

    public void setRefD01(Date refD01) {
        this.refD01 = refD01;
    }

    public Date getRefD01() {
        return this.refD01;
    }

    public void setRefD02(Date refD02) {
        this.refD02 = refD02;
    }

    public Date getRefD02() {
        return this.refD02;
    }

    public void setRefD03(Date refD03) {
        this.refD03 = refD03;
    }

    public Date getRefD03() {
        return this.refD03;
    }

    public void setRefD04(Date refD04) {
        this.refD04 = refD04;
    }

    public Date getRefD04() {
        return this.refD04;
    }

    public void setRefD05(Date refD05) {
        this.refD05 = refD05;
    }

    public Date getRefD05() {
        return this.refD05;
    }
}
