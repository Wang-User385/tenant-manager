package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

public class LovForCompanySetOfBooksDto {
    @LovField(prompt = "帐套代码", field = "set_of_books_code", forQuery = true, forDisplay = true,displayWidth = 250)
    private String setOfBooksCode; //帐套代码
    @LovField(prompt = "帐套名称",field = "set_of_books_name", forQuery = true, forDisplay = true,displayWidth = 250)
    private String setOfBooksName; //帐套名称
    @LovField(prompt = "帐套ID",field="set_of_books_id")
    private Long setOfBooksId; //帐套ID

    @LovField(prompt = "科目表",field = "account_set_id_n", forDisplay = true,displayWidth = 250)
    private String accountSetIdN; //科目表

    @LovField(prompt = "币种",field = "functional_currency_n", forDisplay = true,displayWidth = 250)
    private String functionalCurrencyN; //币种

    public String getSetOfBooksCode() {
        return setOfBooksCode;
    }

    public void setSetOfBooksCode(String setOfBooksCode) {
        this.setOfBooksCode = setOfBooksCode;
    }

    public String getSetOfBooksName() {
        return setOfBooksName;
    }

    public void setSetOfBooksName(String setOfBooksName) {
        this.setOfBooksName = setOfBooksName;
    }

    public Long getSetOfBooksId() {
        return setOfBooksId;
    }

    public void setSetOfBooksId(Long setOfBooksId) {
        this.setOfBooksId = setOfBooksId;
    }

    public String getAccountSetIdN() {
        return accountSetIdN;
    }

    public void setAccountSetIdN(String accountSetIdN) {
        this.accountSetIdN = accountSetIdN;
    }

    public String getFunctionalCurrencyN() {
        return functionalCurrencyN;
    }

    public void setFunctionalCurrencyN(String functionalCurrencyN) {
        this.functionalCurrencyN = functionalCurrencyN;
    }
}
