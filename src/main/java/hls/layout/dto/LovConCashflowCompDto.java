package hls.layout.dto;

import leaf.annotation.LovField;

import java.util.Date;

/**
 * @description
 * @author dql
 * @date 2024/9/2 09:29:06
 */
public class LovConCashflowCompDto
{
    @LovField(prompt = "合同编号", field = "contract_number", forDisplay = true, forQuery = true, displayWidth = 120, displayAlign = "left")
    private String contractNumber;
    @LovField(prompt = "商业伙伴", field = "bp_name",forQuery = true,forDisplay = true,displayWidth = 200)
    private String bpName;
    @LovField(prompt = "期数", field = "times", forDisplay = true, displayWidth = 60)
    private Long times;
    @LovField(prompt = "应收日期", field = "due_date" , forDisplay = true, displayWidth = 110)
    private Date dueDate;
    @LovField(prompt = "应收日期从", field = "due_date_from",forQuery = true)
    private Date dueDateFrom;
    @LovField(prompt = "应收日期到", field = "due_date_to" ,forQuery = true)
    private Date dueDateTo;
    @LovField(prompt = "应收金额", field = "due_amount", forDisplay = true, displayWidth = 130)
    private Double dueAmount;
    @LovField(prompt = "已收金额", field = "received_amount", forDisplay = true, displayWidth = 130)
    private Double receivedAmount;
    @LovField(prompt = "代偿金额", field = "received_comp_amount", forDisplay = true, displayWidth = 130)
    private Double receivedCompAmount;
    @LovField(prompt = "退款金额", field = "refund_amount" , displayWidth = 130)
    private Double refundAmount;
    @LovField(prompt = "收款账户名", field = "bp_bank_account_name" , displayWidth = 130)
    private Double bpBankAccountName;
    @LovField(prompt = "收款银行账号", field = "bp_bank_account_num" , displayWidth = 130)
    private Double bpBankAccountNum;
    @LovField(prompt = "现金事务ID", field = "transaction_id" , displayWidth = 130)
    private Double transactionId;
    @LovField(prompt = "现金事务类型", field = "transaction_type" , displayWidth = 130)
    private Double transactionType;
    @LovField(prompt = "核销状态", field = "write_off_flag" , displayWidth = 130)
    private Double writeOffFlag;
    @LovField(prompt = "供应商ID", field = "manufacturer_id" , displayWidth = 130)
    private Double manufacturerId;
    @LovField(prompt = "现金流ID", field = "cashflow_id" , displayWidth = 130)
    private Double cashflowId;
}
