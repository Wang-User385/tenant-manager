package com.hand.hap.activiti.approval.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(disable = true)
@Table(name = "wfl_approve_candidate_rule")
@Getter
@Setter
public class ApproveCandidateRule extends BaseDTO {
    @Id
    @GeneratedValue
    private Long candidateRuleId;

    @NotEmpty
    @Length(max = 50)
    private String code;

    @NotEmpty
    @Length(max = 255)
    @Condition(operator = LIKE)
    private String description;

    @NotEmpty
    @Length(max = 100)
    private String expression;

    private String enableFlag;

}
