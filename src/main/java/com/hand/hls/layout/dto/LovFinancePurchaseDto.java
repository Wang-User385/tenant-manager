package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

/**
 * Created with IntelliJ IDEA.
 * User: jianfeng.fang
 * Date: 2020年03月07日
 */
public class LovFinancePurchaseDto {

    @LovField(prompt = "申请编号", field = "purchase_number", forQuery = true, forDisplay = true, displayWidth = 150, displayAlign = "left")
    private String purchaseNumber;
    @LovField(prompt = "申购时间", field = "purchase_date", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String purchaseDate;
    @LovField(prompt = "起息日", field = "value_date")
    private String valueDate;
    @LovField(prompt = "到期日", field = "expected_due_date", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String expectedDueDate;
    @LovField(prompt = "是否受限", field = "is_limited", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String isLimited;
    @LovField(prompt = "已实际申购金额", field = "purchased_amount", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String purchasedAmount;
    @LovField(prompt = "已实际赎回金额", field = "redeemed_amount", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String redeemedAmount;


}
