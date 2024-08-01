package hls.layout.dto;

import leaf.annotation.LovField;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene Song
 * Date: 2019年2月21日
 */
public class LovContractCashflowDto {


    @LovField(prompt = "合同编号", field = "contract_number", forDisplay = true, forQuery = true, displayWidth = 120, displayAlign = "left")
    private String contractNumber;
    @LovField(prompt = "合同编号", field = "prj_con_number", forDisplay = false, forQuery = false, displayWidth = 120, displayAlign = "left")
    private String prjConNumber;
    @LovField(prompt = "法务合同编号", field = "legal_contract_number", forDisplay = false, forQuery = false, displayWidth = 120, displayAlign = "left")
    private String legalContractNumber;
    @LovField(prompt = "期数", field = "times", forDisplay = true, displayWidth = 60)
    private Long times;
    @LovField(prompt = "应收日期", field = "dueDateFormat")
    private Date dueDate;
    @LovField(prompt = "应收日期", field = "due_date_format", forDisplay = true, displayWidth = 110)
    private Date dueDateFormat;
    @LovField(prompt = "预定收款日期从", field = "due_date_from",forQuery = true)
    private Date dueDateFrom;
    @LovField(prompt = "预定收款日期到", field = "due_date_to" ,forQuery = true)
    private Date dueDateTo;
    @LovField(prompt = "应收金额", field = "due_amount", forDisplay = true, displayWidth = 130)
    private Double dueAmount;
    @LovField(prompt = "客户名称", field = "bp_name",forQuery = true,forDisplay = true,displayWidth = 200)
    private String bpName;
    @LovField(prompt = "性质", field = "cf_item_n", forDisplay = true, forQuery = true, displayWidth = 80)
    private String cfItemN;
    @LovField(prompt = "剩余金额", field = "surplus_amount", forDisplay = true, displayWidth = 130)
    private Double surplusAmount;

}
