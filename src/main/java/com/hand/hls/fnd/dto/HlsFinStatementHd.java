package com.hand.hls.fnd.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;



@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_fin_statement_hd"
)
public class HlsFinStatementHd extends BaseDTO {
    @Id
    @GeneratedValue
    Long finStatementHdId;
    Long finStatementTempletHdId;
    Long companyId;
    Long bpId;
    Long fiscalYear;
    Long fiscalMonth;
    String currencyCode;
    @Transient
    String finStatementTempletName;
    @Transient
    private String year;
    Date creationDate;
    @Transient
    Long projectId;
    @Transient
    String bpType;

    private Long headerId;

    private String reportYear;

    private String reportType;
    private String ifFinancialCompany;
    private String ifAudit;
    private String auditInstitution;
    private String auditResults;
    private String ifApprovedBranch;

    private String checkStatus;

    public HlsFinStatementHd() {
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getFinStatementTempletName() {
        return this.finStatementTempletName;
    }

    public void setFinStatementTempletName(String finStatementTempletName) {
        this.finStatementTempletName = finStatementTempletName;
    }

    public Long getFinStatementHdId() {
        return this.finStatementHdId;
    }

    public void setFinStatementHdId(Long finStatementHdId) {
        this.finStatementHdId = finStatementHdId;
    }

    public Long getFinStatementTempletHdId() {
        return this.finStatementTempletHdId;
    }

    public void setFinStatementTempletHdId(Long finStatementTempletHdId) {
        this.finStatementTempletHdId = finStatementTempletHdId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getFiscalYear() {
        return this.fiscalYear;
    }

    public void setFiscalYear(Long fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public Long getFiscalMonth() {
        return this.fiscalMonth;
    }

    public void setFiscalMonth(Long fiscalMonth) {
        this.fiscalMonth = fiscalMonth;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getBpType() {
        return this.bpType;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public String getReportYear() {
        return reportYear;
    }

    public void setReportYear(String reportYear) {
        this.reportYear = reportYear;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getIfFinancialCompany() {
        return ifFinancialCompany;
    }

    public void setIfFinancialCompany(String ifFinancialCompany) {
        this.ifFinancialCompany = ifFinancialCompany;
    }

    public String getIfAudit() {
        return ifAudit;
    }

    public void setIfAudit(String ifAudit) {
        this.ifAudit = ifAudit;
    }

    public String getAuditInstitution() {
        return auditInstitution;
    }

    public void setAuditInstitution(String auditInstitution) {
        this.auditInstitution = auditInstitution;
    }

    public String getAuditResults() {
        return auditResults;
    }

    public void setAuditResults(String auditResults) {
        this.auditResults = auditResults;
    }

    public String getIfApprovedBranch() {
        return ifApprovedBranch;
    }

    public void setIfApprovedBranch(String ifApprovedBranch) {
        this.ifApprovedBranch = ifApprovedBranch;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }
}
