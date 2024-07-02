package com.hand.hls.fct.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @Author: syj
 * @Description:
 * @Date: Created in 2017/11/8 19:13
 * @Modified By:
 */
@Getter
@Setter
@ExtensionAttribute(disable=true)
@Table(name = "hls_credit_line_guarantor")
public class HlsCusHlsCreditLineGuarantor extends HlsCreditLineGuarantor {

    private Long projectId;//保理项目ID

    @Length(max = 100)
    private String guaranteeCurrency;

    @Transient
    private String guaranteeCurrencyN;

    private Double guaranteeAmount;

    @Transient
    private String bpName;

    @Transient
    private String currencyName;

    @Transient
    private String guarantorBpIdN;
}
