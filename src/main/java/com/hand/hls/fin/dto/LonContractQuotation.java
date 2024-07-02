package com.hand.hls.fin.dto;



import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "lon_contract_quotation"
)
public class LonContractQuotation extends BaseDTO {
    public static final String FIELD_QUOTATION_ID = "quotationId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_PRICE_LIST = "priceList";
    public static final String FIELD_START_ACTIVE_DATE = "startActiveDate";
    public static final String FIELD_END_ACTIVE_DATE = "endActiveDate";
    public static final String FIELD_INT_RATE_TYPE = "intRateType";
    public static final String FIELD_BASE_RATE_TYPE = "baseRateType";
    public static final String FIELD_BASE_RATE = "baseRate";
    public static final String FIELD_LOAN_RATE = "loanRate";
    public static final String FIELD_FLOATING_WAY = "floatingWay";
    public static final String FIELD_FLOATING_WAY_RANGE = "floatingWayRange";
    public static final String FIELD_INTEREST_CYCLE = "interestCycle";
    public static final String FIELD_FLOATING_RANGE_METHOD = "floatingRangeMethod";
    public static final String FIELD_INTEREST_CALC_METHOD = "interestCalcMethod";
    public static final String FIELD_INTEREST_PAYMENT_DATE = "interestPaymentDate";
    @Id
    @GeneratedValue
    private Long quotationId;
   
    @NotNull
    private Long contractId;
    @Length(
            max = 2000
    )
    private String description;
   
    @Length(
            max = 100
    )
    private String status;
   
    @Length(
            max = 100
    )
    private String priceList;
   
    private Date startActiveDate;
   
    private Date endActiveDate;
   
    @Length(
            max = 100
    )
    private String intRateType;
   
    @Length(
            max = 100
    )
    private String baseRateType;
   
    private Double baseRate;
   
    private Double loanRate;
   
    @Length(
            max = 100
    )
    private String floatingWay;
   
    private Double floatingWayRange;
   
    @Length(
            max = 100
    )
    private String interestCycle;
   
    @Length(
            max = 100
    )
    private String floatingRangeMethod;
   
    @Length(
            max = 100
    )
    private String interestCalcMethod;
   
    private Long interestPaymentDate;
   
    private Long loanTimes;
   
    private Double loanTerm;
   
    private Long interestCalcDate;
   
    private String principalCycle;
   
    private Long principalPaymentDate;

    public LonContractQuotation() {
    }

    public Long getLoanTimes() {
        return this.loanTimes;
    }

    public void setLoanTimes(Long loanTimes) {
        this.loanTimes = loanTimes;
    }

    public Double getLoanTerm() {
        return this.loanTerm;
    }

    public void setLoanTerm(Double loanTerm) {
        this.loanTerm = loanTerm;
    }

    public String getPrincipalCycle() {
        return this.principalCycle;
    }

    public void setPrincipalCycle(String principalCycle) {
        this.principalCycle = principalCycle;
    }

    public Long getInterestCalcDate() {
        return this.interestCalcDate;
    }

    public void setInterestCalcDate(Long interestCalcDate) {
        this.interestCalcDate = interestCalcDate;
    }

    public Long getPrincipalPaymentDate() {
        return this.principalPaymentDate;
    }

    public void setPrincipalPaymentDate(Long principalPaymentDate) {
        this.principalPaymentDate = principalPaymentDate;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public Long getQuotationId() {
        return this.quotationId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return this.contractId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public String getPriceList() {
        return this.priceList;
    }

    public void setStartActiveDate(Date startActiveDate) {
        this.startActiveDate = startActiveDate;
    }

    public Date getStartActiveDate() {
        return this.startActiveDate;
    }

    public void setEndActiveDate(Date endActiveDate) {
        this.endActiveDate = endActiveDate;
    }

    public Date getEndActiveDate() {
        return this.endActiveDate;
    }

    public void setIntRateType(String intRateType) {
        this.intRateType = intRateType;
    }

    public String getIntRateType() {
        return this.intRateType;
    }

    public void setBaseRateType(String baseRateType) {
        this.baseRateType = baseRateType;
    }

    public String getBaseRateType() {
        return this.baseRateType;
    }

    public void setBaseRate(Double baseRate) {
        this.baseRate = baseRate;
    }

    public Double getBaseRate() {
        return this.baseRate;
    }

    public void setLoanRate(Double loanRate) {
        this.loanRate = loanRate;
    }

    public Double getLoanRate() {
        return this.loanRate;
    }

    public void setFloatingWay(String floatingWay) {
        this.floatingWay = floatingWay;
    }

    public String getFloatingWay() {
        return this.floatingWay;
    }

    public void setFloatingWayRange(Double floatingWayRange) {
        this.floatingWayRange = floatingWayRange;
    }

    public Double getFloatingWayRange() {
        return this.floatingWayRange;
    }

    public void setInterestCycle(String interestCycle) {
        this.interestCycle = interestCycle;
    }

    public String getInterestCycle() {
        return this.interestCycle;
    }

    public void setFloatingRangeMethod(String floatingRangeMethod) {
        this.floatingRangeMethod = floatingRangeMethod;
    }

    public String getFloatingRangeMethod() {
        return this.floatingRangeMethod;
    }

    public void setInterestCalcMethod(String interestCalcMethod) {
        this.interestCalcMethod = interestCalcMethod;
    }

    public String getInterestCalcMethod() {
        return this.interestCalcMethod;
    }

    public void setInterestPaymentDate(Long interestPaymentDate) {
        this.interestPaymentDate = interestPaymentDate;
    }

    public Long getInterestPaymentDate() {
        return this.interestPaymentDate;
    }
}
