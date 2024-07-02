package com.hand.hls.fnd.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;

import com.hand.hap.system.dto.BaseDTO;

@ExtensionAttribute(disable = true)
@Table(name = "FND_RISK_RATIO")
@Getter
@Setter
public class FndRiskRatio extends BaseDTO {

    public static final String FIELD_RISK_RATIO_ID = "riskRatioId";
    public static final String FIELD_RISK_RATIO_SET_CODE = "riskRatioSetCode";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_RISK_RATIO = "riskRatio";
    public static final String FIELD_ASSET_CLASS = "assetClass";


    @Id
    @GeneratedValue
    private Long riskRatioId;

    @Length(max = 300)
    private String riskRatioSetCode;

    @Length(max = 30)
    private String currency;

    private Double riskRatio;

    @Length(max = 300)
    private String assetClass;

}
