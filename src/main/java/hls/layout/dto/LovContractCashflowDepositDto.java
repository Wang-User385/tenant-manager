package hls.layout.dto;

import leaf.annotation.LovField;

import java.util.Date;

/**
 * @Description：合同现金流-保证金
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/2/9 15:38
 * @Version：1.0
 */
public class LovContractCashflowDepositDto {
    @LovField(prompt = "合同编号", field = "contract_number", forDisplay = true, forQuery = true, displayWidth = 120, displayAlign = "left")
    private String contractNumber;
    @LovField(prompt = "商业伙伴", field = "bp_name",forQuery = true,forDisplay = true,displayWidth = 200)
    private String bpName;
    @LovField(prompt = "期数", field = "times", forDisplay = true, displayWidth = 60)
    private Long times;
    @LovField(prompt = "预定收款日期", field = "due_date")
    private Date dueDate;
    @LovField(prompt = "预定收款日期", field = "due_date_format", forDisplay = true, displayWidth = 110)
    private Date dueDateFormat;
    @LovField(prompt = "预定收款日期从", field = "due_date_from",forQuery = true)
    private Date dueDateFrom;
    @LovField(prompt = "预定收款日期到", field = "due_date_to" ,forQuery = true)
    private Date dueDateTo;
    @LovField(prompt = "性质", field = "cf_item_n", forDisplay = true, displayWidth = 80)
    private String cfItemN;
    @LovField(prompt = "保证金总额", field = "due_amount", forDisplay = true, displayWidth = 130)
    private Double dueAmount;
    @LovField(prompt = "剩余保证金金额", field = "surplus_deposit_amount", forDisplay = true, displayWidth = 130)
    private Double surplusDepositAmount;
}
