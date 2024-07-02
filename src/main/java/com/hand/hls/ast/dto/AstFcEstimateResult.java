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

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ast_fc_estimate_result"
)
public class AstFcEstimateResult extends BaseDTO {
    public static final String FIELD_RESULT_ID = "resultId";
    public static final String FIELD_FC_ESTIMATE_ID = "fcEstimateId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_LAMP_COLOR_BY_SYSTEM = "lampColorBySystem";
    public static final String FIELD_LAMP_COLOR = "lampColor";
    public static final String FIELD_FIVE_CLASS_CODE_BY_SYSTEM = "fiveClassCodeBySystem";
    public static final String FIELD_ORIGINAL_FIVE_CLASS_CODE = "originalFiveClassCode";
    public static final String FIELD_FIVE_CLASS_CODE = "fiveClassCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ESTIMATE_DATE = "estimateDate";
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
    private Long resultId;
    @NotNull
    private Long fcEstimateId;
    @NotNull
    private Long contractId;
    @Length(
            max = 30
    )
    private String lampColorBySystem;
    @Length(
            max = 30
    )
    private String lampColor;
    @Length(
            max = 30
    )
    private String originalFiveClassCode;
    @Length(
            max = 30
    )
    private String fiveClassCodeBySystem;
    @Length(
            max = 30
    )
    private String fiveClassCode;
    @Length(
            max = 2000
    )
    private String description;
    private Date estimateDate;
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

    public AstFcEstimateResult() {
    }


    @Transient
    private String fiveClassTarget;
    @Transient
    private String targetValue;
    @Transient
    private String fiveClassTargetName;
    @Transient
    private String finalAssetsClassResultN;

    public String getFiveClassTarget() {
        return fiveClassTarget;
    }

    public void setFiveClassTarget(String fiveClassTarget) {
        this.fiveClassTarget = fiveClassTarget;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public String getFiveClassTargetName() {
        return fiveClassTargetName;
    }

    public void setFiveClassTargetName(String fiveClassTargetName) {
        this.fiveClassTargetName = fiveClassTargetName;
    }

    public String getFinalAssetsClassResultN() {
        return finalAssetsClassResultN;
    }

    public void setFinalAssetsClassResultN(String finalAssetsClassResultN) {
        this.finalAssetsClassResultN = finalAssetsClassResultN;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getResultId() {
        return this.resultId;
    }

    public void setFcEstimateId(Long fcEstimateId) {
        this.fcEstimateId = fcEstimateId;
    }

    public Long getFcEstimateId() {
        return this.fcEstimateId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return this.contractId;
    }

    public void setLampColorBySystem(String lampColorBySystem) {
        this.lampColorBySystem = lampColorBySystem;
    }

    public String getLampColorBySystem() {
        return this.lampColorBySystem;
    }

    public void setLampColor(String lampColor) {
        this.lampColor = lampColor;
    }

    public String getLampColor() {
        return this.lampColor;
    }

    public void setFiveClassCodeBySystem(String fiveClassCodeBySystem) {
        this.fiveClassCodeBySystem = fiveClassCodeBySystem;
    }

    public String getFiveClassCodeBySystem() {
        return this.fiveClassCodeBySystem;
    }

    public void setFiveClassCode(String fiveClassCode) {
        this.fiveClassCode = fiveClassCode;
    }

    public String getFiveClassCode() {
        return this.fiveClassCode;
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

    public String getOriginalFiveClassCode() {
        return this.originalFiveClassCode;
    }

    public void setOriginalFiveClassCode(String originalFiveClassCode) {
        this.originalFiveClassCode = originalFiveClassCode;
    }
}
