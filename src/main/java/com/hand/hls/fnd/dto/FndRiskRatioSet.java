package com.hand.hls.fnd.dto;

import javax.persistence.Id;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Select;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "FND_RISK_RATIO_SET")
@Setter
@Getter
public class FndRiskRatioSet extends BaseDTO {
    @Id
    private String riskRatioSetCode;

    @Length(max = 4000)
    private String description;

    @Length(max = 3)
    private String enabledFlag;

    private Date validFrom;

    private Date validTo;

    @Children
    @Transient
    private List<FndRiskRatio> fndRiskRatio;
}
