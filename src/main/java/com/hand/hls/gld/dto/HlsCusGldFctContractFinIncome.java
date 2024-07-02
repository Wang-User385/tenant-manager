package com.hand.hls.gld.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

@ExtensionAttribute(disable = true)
@Table(name = "gld_fct_contract_fin_income")
public class HlsCusGldFctContractFinIncome extends GldFctContractFinIncome {

    private Long cfItem;

    private Double financeIncomeInclud;

    private Double financeIncomeVat;

    public Long getCfItem() {
        return cfItem;
    }

    public void setCfItem(Long cfItem) {
        this.cfItem = cfItem;
    }

    public Double getFinanceIncomeInclud() {
        return financeIncomeInclud;
    }

    public void setFinanceIncomeInclud(Double financeIncomeInclud) {
        this.financeIncomeInclud = financeIncomeInclud;
    }

    public Double getFinanceIncomeVat() {
        return financeIncomeVat;
    }

    public void setFinanceIncomeVat(Double financeIncomeVat) {
        this.financeIncomeVat = financeIncomeVat;
    }
}
