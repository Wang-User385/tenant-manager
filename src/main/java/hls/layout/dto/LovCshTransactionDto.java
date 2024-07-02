package hls.layout.dto;

import leaf.annotation.LovField;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene Song
 * Date: 2019年2月21日
 */
public class LovCshTransactionDto {

    @LovField(prompt = "收款编号", field = "transaction_num", forQuery = true, forDisplay = true, displayWidth = 100)
    private String transactionNum;
    @LovField(prompt = "收款对象", field = "bp_name", forDisplay = true, forQuery = true, displayWidth = 120, displayAlign = "left")
    private String bpName;
    @LovField(prompt = "收款金额", field = "transaction_amount", forDisplay = true, displayWidth = 120, displayAlign = "right")
    private Double transactionAmount;
    @LovField(prompt = "已核销金额", field = "write_off_amount", forDisplay = true, displayWidth = 120, displayAlign = "right")
    private Double writeOffAmount;
    @LovField(prompt = "剩余可核销金额", field = "un_write_off_amount", forDisplay = true, displayWidth = 120, displayAlign = "right")
    private Double unWriteOffAmount;


}
