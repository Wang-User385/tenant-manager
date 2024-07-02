package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

public class CshPaymentReqLn extends BaseDTO {
    @Id
    @GeneratedValue
    private Long payment_req_ln_id;
    @Condition(
            operator = "LIKE"
    )
    private Long payment_req_id;
    private String source_doc_category;
    private Long source_doc_id;
    private Long source_doc_line_id;
    private Double amount;
    private Double amount_paid;
    private String description;
    private String payment_flag;
    private Date payment_completed_date;
    private String payment_method;
    private Long bp_id;
    private Long bp_bank_account_id;
    private String bp_bank_name;
    private String bp_bank_branch_name;
    private String bp_bank_account_num;
    private String bp_bank_account_name;
    @Transient
    private String ln_id_str;
    @Transient
    private String cf_item;
    @Transient
    private String cf_type;
    @Transient
    private Double non_payment;
    @Transient
    private String currency;
    @Transient
    private String payment_Obj;
    @Transient
    private String contract_id;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String contract_name;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String contract_number;
    @Transient
    private String proposer;
    @Transient
    private String tenant_name;
    @Transient
    private String times;
    @Transient
    private String yf_description;
    @Transient
    private Double due_amount;
    @Transient
    private Double unpaid_amount;
    @Transient
    private String due_date;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String bp_name;
    @Transient
    private Double starp_amount;
    @Transient
    private Double end_amount;
    @Transient
    private Long cashflow_id;
    @Transient
    private String paymentReqStatus;
    @Transient
    private String paymentReqStatusDesc;
    @Transient
    private String company_full_name;
    @Transient
    private String type_description;
    @Transient
    private String payment_req_number;
    @Transient
    private String user_name;
    @Transient
    private String payment_status;
    @Transient
    private Date payment_req_date;
    @Transient
    private String create_name;
    @Transient
    private Date LAST_UPDATE_DATE;
    @Transient
    private String document_type;
    @Transient
    private Long companyId;
    @Transient
    private Long paymentReqId;
    @Transient
    private Long processInstanceId;
    @Transient
    private Long queryTimeSolt;
    @Transient
    private String documentType;
    @Transient
    private Double receivedAmount;
    @Transient
    private String repaymentIds;
    @Transient
    private Long withdrawCfId;
    @Transient
    private List<HlsCusCshPaymentReqDt> cshPaymentReqDts;

    public CshPaymentReqLn() {
    }

    public List<HlsCusCshPaymentReqDt> getCshPaymentReqDts() {
        return this.cshPaymentReqDts;
    }

    public void setCshPaymentReqDts(List<HlsCusCshPaymentReqDt> cshPaymentReqDts) {
        this.cshPaymentReqDts = cshPaymentReqDts;
    }

    public Long getWithdrawCfId() {
        return this.withdrawCfId;
    }

    public void setWithdrawCfId(Long withdrawCfId) {
        this.withdrawCfId = withdrawCfId;
    }

    public String getRepaymentIds() {
        return this.repaymentIds;
    }

    public void setRepaymentIds(String repaymentIds) {
        this.repaymentIds = repaymentIds;
    }

    public Double getReceivedAmount() {
        return this.receivedAmount;
    }

    public void setReceivedAmount(Double receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getPaymentReqStatusDesc() {
        return this.paymentReqStatusDesc;
    }

    public void setPaymentReqStatusDesc(String paymentReqStatusDesc) {
        this.paymentReqStatusDesc = paymentReqStatusDesc;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Long getQueryTimeSolt() {
        return this.queryTimeSolt;
    }

    public void setQueryTimeSolt(Long queryTimeSolt) {
        this.queryTimeSolt = queryTimeSolt;
    }

    public Long getProcessInstanceId() {
        return this.processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getPaymentReqId() {
        return this.paymentReqId;
    }

    public void setPaymentReqId(Long paymentReqId) {
        this.paymentReqId = paymentReqId;
    }

    public String getDocument_type() {
        return this.document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Date getLAST_UPDATE_DATE() {
        return this.LAST_UPDATE_DATE;
    }

    public void setLAST_UPDATE_DATE(Date lAST_UPDATE_DATE) {
        this.LAST_UPDATE_DATE = lAST_UPDATE_DATE;
    }

    public String getCreate_name() {
        return this.create_name;
    }

    public void setCreate_name(String create_name) {
        this.create_name = create_name;
    }

    public String getPayment_status() {
        return this.payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public Date getPayment_req_date() {
        return this.payment_req_date;
    }

    public void setPayment_req_date(Date payment_req_date) {
        this.payment_req_date = payment_req_date;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPayment_req_number() {
        return this.payment_req_number;
    }

    public void setPayment_req_number(String payment_req_number) {
        this.payment_req_number = payment_req_number;
    }

    public String getCompany_full_name() {
        return this.company_full_name;
    }

    public void setCompany_full_name(String company_full_name) {
        this.company_full_name = company_full_name;
    }

    public String getType_description() {
        return this.type_description;
    }

    public void setType_description(String type_description) {
        this.type_description = type_description;
    }

    public String getPaymentReqStatus() {
        return this.paymentReqStatus;
    }

    public void setPaymentReqStatus(String paymentReqStatus) {
        this.paymentReqStatus = paymentReqStatus;
    }

    public Long getCashflow_id() {
        return this.cashflow_id;
    }

    public void setCashflow_id(Long cashflow_id) {
        this.cashflow_id = cashflow_id;
    }

    public Long getPayment_req_ln_id() {
        return this.payment_req_ln_id;
    }

    public void setPayment_req_ln_id(Long payment_req_ln_id) {
        this.payment_req_ln_id = payment_req_ln_id;
    }

    public Long getPayment_req_id() {
        return this.payment_req_id;
    }

    public void setPayment_req_id(Long payment_req_id) {
        this.payment_req_id = payment_req_id;
    }

    public String getSource_doc_category() {
        return this.source_doc_category;
    }

    public void setSource_doc_category(String source_doc_category) {
        this.source_doc_category = source_doc_category;
    }

    public Long getSource_doc_id() {
        return this.source_doc_id;
    }

    public void setSource_doc_id(Long source_doc_id) {
        this.source_doc_id = source_doc_id;
    }

    public Long getSource_doc_line_id() {
        return this.source_doc_line_id;
    }

    public void setSource_doc_line_id(Long source_doc_line_id) {
        this.source_doc_line_id = source_doc_line_id;
    }

    public String getCf_item() {
        return this.cf_item;
    }

    public void setCf_item(String cf_item) {
        this.cf_item = cf_item;
    }

    public String getCf_type() {
        return this.cf_type;
    }

    public void setCf_type(String cf_type) {
        this.cf_type = cf_type;
    }

    public Double getNon_payment() {
        return this.non_payment;
    }

    public void setNon_payment(Double non_payment) {
        this.non_payment = non_payment;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContract_number() {
        return this.contract_number;
    }

    public void setContract_number(String contract_number) {
        this.contract_number = contract_number;
    }

    public String getPayment_flag() {
        return this.payment_flag;
    }

    public void setPayment_flag(String payment_flag) {
        this.payment_flag = payment_flag;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayment_Obj() {
        return this.payment_Obj;
    }

    public void setPayment_Obj(String payment_Obj) {
        this.payment_Obj = payment_Obj;
    }

    public Date getPayment_completed_date() {
        return this.payment_completed_date;
    }

    public String getLn_id_str() {
        return this.ln_id_str;
    }

    public void setLn_id_str(String ln_id_str) {
        this.ln_id_str = ln_id_str;
    }

    public void setPayment_completed_date(Date payment_completed_date) {
        this.payment_completed_date = payment_completed_date;
    }

    public String getPayment_method() {
        return this.payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Long getBp_id() {
        return this.bp_id;
    }

    public void setBp_id(Long bp_id) {
        this.bp_id = bp_id;
    }

    public Long getBp_bank_account_id() {
        return this.bp_bank_account_id;
    }

    public void setBp_bank_account_id(Long bp_bank_account_id) {
        this.bp_bank_account_id = bp_bank_account_id;
    }

    public String getBp_bank_name() {
        return this.bp_bank_name;
    }

    public void setBp_bank_name(String bp_bank_name) {
        this.bp_bank_name = bp_bank_name;
    }

    public String getTenant_name() {
        return this.tenant_name;
    }

    public void setTenant_name(String tenant_name) {
        this.tenant_name = tenant_name;
    }

    public String getTimes() {
        return this.times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getYf_description() {
        return this.yf_description;
    }

    public void setYf_description(String yf_description) {
        this.yf_description = yf_description;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmount_paid() {
        return this.amount_paid;
    }

    public void setAmount_paid(Double amount_paid) {
        this.amount_paid = amount_paid;
    }

    public Double getDue_amount() {
        return this.due_amount;
    }

    public void setDue_amount(Double due_amount) {
        this.due_amount = due_amount;
    }

    public Double getStarp_amount() {
        return this.starp_amount;
    }

    public void setStarp_amount(Double starp_amount) {
        this.starp_amount = starp_amount;
    }

    public Double getEnd_amount() {
        return this.end_amount;
    }

    public void setEnd_amount(Double end_amount) {
        this.end_amount = end_amount;
    }

    public Double getUnpaid_amount() {
        return this.unpaid_amount;
    }

    public void setUnpaid_amount(Double unpaid_amount) {
        this.unpaid_amount = unpaid_amount;
    }

    public String getDue_date() {
        return this.due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getBp_bank_branch_name() {
        return this.bp_bank_branch_name;
    }

    public void setBp_bank_branch_name(String bp_bank_branch_name) {
        this.bp_bank_branch_name = bp_bank_branch_name;
    }

    public String getBp_bank_account_num() {
        return this.bp_bank_account_num;
    }

    public void setBp_bank_account_num(String bp_bank_account_num) {
        this.bp_bank_account_num = bp_bank_account_num;
    }

    public String getBp_bank_account_name() {
        return this.bp_bank_account_name;
    }

    public void setBp_bank_account_name(String bp_bank_account_name) {
        this.bp_bank_account_name = bp_bank_account_name;
    }

    public String getContract_id() {
        return this.contract_id;
    }

    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }

    public String getContract_name() {
        return this.contract_name;
    }

    public void setContract_name(String contract_name) {
        this.contract_name = contract_name;
    }

    public String getProposer() {
        return this.proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public String getBp_name() {
        return this.bp_name;
    }

    public void setBp_name(String bp_name) {
        this.bp_name = bp_name;
    }

    public String toString() {
        return "HlsCusCshPaymentReqLn [payment_req_ln_id=" + this.payment_req_ln_id + ", payment_req_id=" + this.payment_req_id + ", source_doc_category=" + this.source_doc_category + ", source_doc_id=" + this.source_doc_id + ", source_doc_line_id=" + this.source_doc_line_id + ", amount=" + this.amount + ", amount_paid=" + this.amount_paid + ", description=" + this.description + ", payment_flag=" + this.payment_flag + ", payment_completed_date=" + this.payment_completed_date + ", payment_method=" + this.payment_method + ", bp_id=" + this.bp_id + ", bp_bank_account_id=" + this.bp_bank_account_id + ", bp_bank_name=" + this.bp_bank_name + ", bp_bank_branch_name=" + this.bp_bank_branch_name + ", bp_bank_account_num=" + this.bp_bank_account_num + ", bp_bank_account_name=" + this.bp_bank_account_name + ", ln_id_str=" + this.ln_id_str + ", cf_item=" + this.cf_item + ", cf_type=" + this.cf_type + ", non_payment=" + this.non_payment + ", currency=" + this.currency + ", payment_Obj=" + this.payment_Obj + ", contract_id=" + this.contract_id + ", contract_name=" + this.contract_name + ", contract_number=" + this.contract_number + ", proposer=" + this.proposer + ", tenant_name=" + this.tenant_name + ", times=" + this.times + ", yf_description=" + this.yf_description + ", due_amount=" + this.due_amount + ", unpaid_amount=" + this.unpaid_amount + ", due_date=" + this.due_date + ", bp_name=" + this.bp_name + ", starp_amount=" + this.starp_amount + ", end_amount=" + this.end_amount + ", cashflow_id=" + this.cashflow_id + "]";
    }
}
