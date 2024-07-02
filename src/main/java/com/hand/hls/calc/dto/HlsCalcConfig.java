package com.hand.hls.calc.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@ExtensionAttribute(disable = true)
@Table(name = "hls_price_list")
@Getter
@Setter
public class HlsCalcConfig extends BaseDTO {

    @Id
    private String priceList;
    private String description;
    private String sheets;
    private String enabledFlag;

    private String paymentType;

    @Transient
    private String message;

    @Transient
    private String paymentTypeN;

    private String adjustRatePrice;

    private String adjustRateDescription;
    @Transient
    private String intranetFlag;
}
