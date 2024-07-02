//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.springframework.data.annotation.Transient;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_score_target_values"
)
public class FndScoreTargetValues extends BaseDTO {
    @Id
    @GeneratedValue
    private Long scoreTargetValueId;
    private Long scoreTargetId;
    private Long lineNumber;
    private String leftBracket;
    private String fromValue;
    private String toValue;
    private String rightBracket;
    private String fixedTargetValue;
    private String description;
//    @Transient
//    private String fromValueN;
//    @Transient
//    private String toValueN;
//
//    public String getFromValueN() {
//        return fromValueN;
//    }
//
//    public void setFromValueN(String fromValueN) {
//        this.fromValueN = fromValueN;
//    }
//
//    public String getToValueN() {
//        return toValueN;
//    }
//
//    public void setToValueN(String toValueN) {
//        this.toValueN = toValueN;
//    }

    public FndScoreTargetValues() {
    }

    public void setScoreTargetValueId(Long scoreTargetValueId) {
        this.scoreTargetValueId = scoreTargetValueId;
    }

    public Long getScoreTargetValueId() {
        return this.scoreTargetValueId;
    }

    public void setScoreTargetId(Long scoreTargetId) {
        this.scoreTargetId = scoreTargetId;
    }

    public Long getScoreTargetId() {
        return this.scoreTargetId;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Long getLineNumber() {
        return this.lineNumber;
    }

    public void setLeftBracket(String leftBracket) {
        this.leftBracket = leftBracket;
    }

    public String getLeftBracket() {
        return this.leftBracket;
    }

    public void setFromValue(String fromValue) {
        this.fromValue = fromValue;
    }

    public String getFromValue() {
        return this.fromValue;
    }

    public void setToValue(String toValue) {
        this.toValue = toValue;
    }

    public String getToValue() {
        return this.toValue;
    }

    public void setRightBracket(String rightBracket) {
        this.rightBracket = rightBracket;
    }

    public String getRightBracket() {
        return this.rightBracket;
    }

    public void setFixedTargetValue(String fixedTargetValue) {
        this.fixedTargetValue = fixedTargetValue;
    }

    public String getFixedTargetValue() {
        return this.fixedTargetValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
