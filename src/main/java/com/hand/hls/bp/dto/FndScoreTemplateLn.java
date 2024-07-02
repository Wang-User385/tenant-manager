//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fnd_score_template_ln"
)
public class FndScoreTemplateLn extends BaseDTO {
    @Id
    @GeneratedValue
    private Long scoreTemplateLnId;
    private Long scoreTemplateHdId;
    private String summaryFlag;
    private Long scoreTargetId;
    private Long dataSourceColumnId;
    private Double weightValue;
    private Long parentLnId;
    private String enabledFlag;
    private String description;
    private Long lineSeq;
    @Transient
    private String scoreTargetName;
    @Transient
    private String scoreTargetCode;
    @Transient
    private Long scoreResultDtlId;
    @Transient
    private Long scoreResultId;
    @Transient
    private Long scoreTargetValueId;
    @Transient
    private String targetValue;
    @Transient
    private Double targetScore;
    @Transient
    private Double targetScoreOriginal;
    @Transient
    private String targetScoreGrade;
    @Transient
    private String targetName;
    @Transient
    private String firstTargetName;
    @Transient
    private String descriptions;
    @Transient
    private Long scoreValues;
    @Transient
    private String dsDescription;
    @Transient
    private List<FndScoreTemplateLn> scoreTemplateLn;

    public FndScoreTemplateLn() {
    }

    public String getDsDescription() {
        return this.dsDescription;
    }

    public void setDsDescription(String dsDescription) {
        this.dsDescription = dsDescription;
    }

    public Long getLineSeq() {
        return this.lineSeq;
    }

    public void setLineSeq(Long lineSeq) {
        this.lineSeq = lineSeq;
    }

    public String getScoreTargetCode() {
        return this.scoreTargetCode;
    }

    public void setScoreTargetCode(String scoreTargetCode) {
        this.scoreTargetCode = scoreTargetCode;
    }

    public String getScoreTargetName() {
        return this.scoreTargetName;
    }

    public void setScoreTargetName(String scoreTargetName) {
        this.scoreTargetName = scoreTargetName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScoreTemplateLnId(Long scoreTemplateLnId) {
        this.scoreTemplateLnId = scoreTemplateLnId;
    }

    public Long getScoreTemplateLnId() {
        return this.scoreTemplateLnId;
    }

    public void setScoreTemplateHdId(Long scoreTemplateHdId) {
        this.scoreTemplateHdId = scoreTemplateHdId;
    }

    public Long getScoreTemplateHdId() {
        return this.scoreTemplateHdId;
    }

    public void setSummaryFlag(String summaryFlag) {
        this.summaryFlag = summaryFlag;
    }

    public String getSummaryFlag() {
        return this.summaryFlag;
    }

    public void setScoreTargetId(Long scoreTargetId) {
        this.scoreTargetId = scoreTargetId;
    }

    public Long getScoreTargetId() {
        return this.scoreTargetId;
    }

    public void setDataSourceColumnId(Long dataSourceColumnId) {
        this.dataSourceColumnId = dataSourceColumnId;
    }

    public Long getDataSourceColumnId() {
        return this.dataSourceColumnId;
    }

    public void setWeightValue(Double weightValue) {
        this.weightValue = weightValue;
    }

    public Double getWeightValue() {
        return this.weightValue;
    }

    public void setParentLnId(Long parentLnId) {
        this.parentLnId = parentLnId;
    }

    public Long getParentLnId() {
        return this.parentLnId;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public List<FndScoreTemplateLn> getScoreTemplateLn() {
        return this.scoreTemplateLn;
    }

    public void setScoreTemplateLn(List<FndScoreTemplateLn> scoreTemplateLn) {
        this.scoreTemplateLn = scoreTemplateLn;
    }

    public Long getScoreResultDtlId() {
        return this.scoreResultDtlId;
    }

    public void setScoreResultDtlId(Long scoreResultDtlId) {
        this.scoreResultDtlId = scoreResultDtlId;
    }

    public Long getScoreResultId() {
        return this.scoreResultId;
    }

    public void setScoreResultId(Long scoreResultId) {
        this.scoreResultId = scoreResultId;
    }

    public Long getScoreTargetValueId() {
        return this.scoreTargetValueId;
    }

    public void setScoreTargetValueId(Long scoreTargetValueId) {
        this.scoreTargetValueId = scoreTargetValueId;
    }

    public String getTargetValue() {
        return this.targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public Double getTargetScore() {
        return this.targetScore;
    }

    public void setTargetScore(Double targetScore) {
        this.targetScore = targetScore;
    }

    public Double getTargetScoreOriginal() {
        return this.targetScoreOriginal;
    }

    public void setTargetScoreOriginal(Double targetScoreOriginal) {
        this.targetScoreOriginal = targetScoreOriginal;
    }

    public String getTargetScoreGrade() {
        return this.targetScoreGrade;
    }

    public void setTargetScoreGrade(String targetScoreGrade) {
        this.targetScoreGrade = targetScoreGrade;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getFirstTargetName() {
        return this.firstTargetName;
    }

    public void setFirstTargetName(String firstTargetName) {
        this.firstTargetName = firstTargetName;
    }

    public String getDescriptions() {
        return this.descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public Long getScoreValues() {
        return this.scoreValues;
    }

    public void setScoreValues(Long scoreValues) {
        this.scoreValues = scoreValues;
    }
}
