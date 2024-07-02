package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene Song
 * Date: 2019年2月20日
 */
public class LovCshBankAccountDto {
    @LovField(prompt = "银行名称", field = "bank_name", forDisplay = true, displayWidth = 150, displayAlign = "left")
    private String bankName;
//    @LovField(prompt = "支行名称", field = "bank_branch_name", forDisplay = true, displayWidth = 200, displayAlign = "left")
//    private String bankBranchName;
    @LovField(prompt = "账号", field = "bank_account_num", forQuery = true, forDisplay = true, displayWidth = 150, displayAlign = "left")
    private String bankAccountNum;
    @LovField(prompt = "账户", field = "bank_account_name", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String bankAccountName;

}
