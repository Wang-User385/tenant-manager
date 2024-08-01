package hls.layout.dto;

import leaf.annotation.LovField;

import java.util.Date;

/**
 * @Description：合同现金流-代偿租金
 * @Author：
 * @Date：
 * @Version：1.0
 */
public class LovContractCashflowCompDto {
    @LovField(prompt = "合同编号", field = "contract_number", forDisplay = true, forQuery = true, displayWidth = 120, displayAlign = "left")
    private String contractNumber;
    @LovField(prompt = "商业伙伴", field = "bp_name",forQuery = true,forDisplay = true,displayWidth = 200)
    private String bpName;
    @LovField(prompt = "期数", field = "times", forDisplay = true, displayWidth = 60)
    private Long times;
    @LovField(prompt = "应收日期", field = "due_date" )
    private Date dueDate;
    @LovField(prompt = "应收日期", field = "due_date_format", forDisplay = true, displayWidth = 110)
    private Date dueDateFormat;
    @LovField(prompt = "应收日期从", field = "due_date_from",forQuery = true)
    private Date dueDateFrom;
    @LovField(prompt = "应收日期到", field = "due_date_to" ,forQuery = true)
    private Date dueDateTo;
    @LovField(prompt = "性质", field = "cf_item_n", forDisplay = true, displayWidth = 80)
    private String cfItemN;
    @LovField(prompt = "租金总额", field = "due_amount", forDisplay = true, displayWidth = 130)
    private Double dueAmount;
    @LovField(prompt = "剩余金额", field = "surplus_amount", forDisplay = true, displayWidth = 130)
    private Double surplusDepositAmount;
}
