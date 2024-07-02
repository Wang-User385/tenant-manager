//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
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
        name = "ast_five_class_rule_detail"
)
public class AstFiveClassRuleDetail extends BaseDTO {
    public static final String FIELD_RULE_DETAIL_ID = "ruleDetailId";
    public static final String FIELD_FIVE_CLASS_RULE_ID = "fiveClassRuleId";
    public static final String FIELD_SEQUENCE_NO = "sequenceNo";
    public static final String FIELD_LEFT_BRACKET = "leftBracket";
    public static final String FIELD_FIVE_CLASS_TARGET = "fiveClassTarget";
    public static final String FIELD_CALCULATE_SYMBOL = "calculateSymbol";
    public static final String FIELD_CALCULATE_VALUE = "calculateValue";
    public static final String FIELD_RIGHT_BRACKET = "rightBracket";
    public static final String FIELD_LOGIC_CALC_SYMBOL = "logicCalcSymbol";
    @Id
    @GeneratedValue
    private Long ruleDetailId;
    @NotNull
    private Long fiveClassRuleId;
    private Long sequenceNo;
    @Length(
            max = 30
    )
    private String leftBracket;
    @Length(
            max = 30
    )
    private String fiveClassTarget;
    @Transient
    private String includeTargetValue;
    @Length(
            max = 30
    )
    private String calculateSymbol;
    @Length(
            max = 30
    )
    private String calculateValue;
    @Length(
            max = 30
    )
    private String rightBracket;
    @Length(
            max = 30
    )
    private String logicCalcSymbol;

    public AstFiveClassRuleDetail() {
    }

    public String getIncludeTargetValue() {
        return this.includeTargetValue;
    }

    public void setIncludeTargetValue(String includeTargetValue) {
        this.includeTargetValue = includeTargetValue;
    }

    public void setRuleDetailId(Long ruleDetailId) {
        this.ruleDetailId = ruleDetailId;
    }

    public Long getRuleDetailId() {
        return this.ruleDetailId;
    }

    public void setFiveClassRuleId(Long fiveClassRuleId) {
        this.fiveClassRuleId = fiveClassRuleId;
    }

    public Long getFiveClassRuleId() {
        return this.fiveClassRuleId;
    }

    public void setSequenceNo(Long sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Long getSequenceNo() {
        return this.sequenceNo;
    }

    public void setLeftBracket(String leftBracket) {
        this.leftBracket = leftBracket;
    }

    public String getLeftBracket() {
        return this.leftBracket;
    }

    public void setFiveClassTarget(String fiveClassTarget) {
        this.fiveClassTarget = fiveClassTarget;
    }

    public String getFiveClassTarget() {
        return this.fiveClassTarget;
    }

    public void setCalculateSymbol(String calculateSymbol) {
        this.calculateSymbol = calculateSymbol;
    }

    public String getCalculateSymbol() {
        return this.calculateSymbol;
    }

    public void setCalculateValue(String calculateValue) {
        this.calculateValue = calculateValue;
    }

    public String getCalculateValue() {
        return this.calculateValue;
    }

    public void setRightBracket(String rightBracket) {
        this.rightBracket = rightBracket;
    }

    public String getRightBracket() {
        return this.rightBracket;
    }

    public void setLogicCalcSymbol(String logicCalcSymbol) {
        this.logicCalcSymbol = logicCalcSymbol;
    }

    public String getLogicCalcSymbol() {
        return this.logicCalcSymbol;
    }
}
