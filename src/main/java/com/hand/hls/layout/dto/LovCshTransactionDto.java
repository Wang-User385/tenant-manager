package com.hand.hls.layout.dto;


import leaf.annotation.LovField;

import java.util.Date;

/**
 * 现金事务lov对应的dto
 */
public class LovCshTransactionDto {
    @LovField(prompt = "现金事务编号", field = "transaction_num", forQuery = true, forDisplay = true, displayAlign = "left")
    private String transactionNum;
    @LovField(prompt = "现金事务类型", field = "transaction_type_desc", forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String transactionTypeDesc;
    @LovField(prompt = "日期", field = "transaction_date", forDisplay = true, displayWidth = 100)
    private Date transactionDate;
    @LovField(prompt = "现金事务金额", field = "transaction_amount", forDisplay = true, displayWidth = 120, displayAlign = "right")
    private Double transactionAmount;
    @LovField(prompt = "核销金额", field = "write_off_amount", forDisplay = true, displayWidth = 120, displayAlign = "right")
    private Double writeOffAmount;
    @LovField(prompt = "合同编号", field = "contract_number", forQuery = true, forDisplay = true, displayAlign = "left")
    private String contractNumber;

}
