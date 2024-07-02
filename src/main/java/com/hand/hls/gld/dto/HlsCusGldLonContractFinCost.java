package com.hand.hls.gld.dto;

/**
 * Created by IntelliJ IDEA.
 * @author: WuJun
 * @Date: 2020/02/07
 * @Time: 11:48
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "gld_lon_contract_fin_cost"
)
@Getter
@Setter
public class HlsCusGldLonContractFinCost extends LonContractFinCost {
    public HlsCusGldLonContractFinCost() {
    }

    private Long cfItem;
    //含税金额
    private Double financeIncomeInclud;
    //计提税额
    private Double financeIncomeVat;

    @Transient
    private String __status;

    private Double financeCost;
    private String periodName;
    private Date startDate;
    private Date endDate;
    private String postFlag;

    @Transient
    private String contractNumber;
    @Transient
    private String withdrawNumber;
    @Transient
    private String withdrawCurrencyCode;

    @Transient
    private Double rateFromLon;
    @Transient
    private Double rateToLon;

    @Transient
    private Double netInterest;
    @Transient
    private String currency;
    @Transient
    private Double taxTypeRate;
    @Transient
    private String calcInterestYearDays;
    @Transient
    private Date dueDate;
    @Transient
    private Long timesCalcDays;

    @Transient
    private String cfItemDesc;
    @Transient
    private String postFlagDesc;

    @Transient
    private String contractContentNumber;

    @Transient
    private String bpName;

    @Transient
    private String taxRateDesc;

    private Long productId;

    private Long absFeeId;

    @Transient
    private Long financeIncomeId;

    @Transient
    private String reportName;

    private Long gldPaymentId;
    
    @Transient
    private String cfItemN;

    @Transient
    private String postFlagN;

    @Transient
    private String currencyN;

    @Transient
    private String creditBpName;

    @Transient
    private String companyFullName;

    @Transient
    private Double dueAmount;


}
