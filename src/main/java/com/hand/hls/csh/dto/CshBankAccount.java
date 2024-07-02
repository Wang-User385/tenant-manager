//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "csh_bank_account"
)
public class CshBankAccount extends BaseDTO {
    public static final String FIELD_BANK_ACCOUNT_ID = "bankAccountId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_BANK_ID = "bankId";
    public static final String FIELD_BANK_BRANCH_NAME = "bankBranchName";
    public static final String FIELD_BANK_ACCOUNT_NUM = "bankAccountNum";
    public static final String FIELD_BANK_ACCOUNT_NAME = "bankAccountName";
    public static final String FIELD_BANK_ACCOUNT_TYPE = "bankAccountType";
    public static final String FIELD_GLD_ACCOUNT_CODE = "gldAccountCode";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_BANK_COLOR = "bankColor";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_FINANCING_LOAN_FLAG = "financingLoanFlag";
    public static final String FIELD_SWIFT_CODE = "swiftCode";
    public static final String FIELD_OPENING_DATE = "openingDate";
    public static final String FIELD_CLOSING_DATE = "closingDate";
    public static final String FIELD_CNAPS_CODE = "cnapsCode";
    public static final String FIELD_ACCOUNT_NATURE_ONE = "accountNatureOne";
    public static final String FIELD_ACCOUNT_NATURE_TWO = "accountNatureTwo";
    public static final String FIELD_ACCOUNT_NATURE_THREE = "accountNatureThree";
    public static final String FIELD_REF_V01 = "refV01";
    public static final String FIELD_REF_V02 = "refV02";
    public static final String FIELD_REF_V03 = "refV03";
    public static final String FIELD_REF_V04 = "refV04";
    public static final String FIELD_REF_V05 = "refV05";
    public static final String FIELD_REF_V06 = "refV06";
    public static final String FIELD_REF_V07 = "refV07";
    public static final String FIELD_REF_V08 = "refV08";
    public static final String FIELD_REF_V09 = "refV09";
    public static final String FIELD_REF_V10 = "refV10";
    public static final String FIELD_REF_V11 = "refV11";
    public static final String FIELD_REF_V12 = "refV12";
    public static final String FIELD_REF_V13 = "refV13";
    public static final String FIELD_REF_V14 = "refV14";
    public static final String FIELD_REF_V15 = "refV15";
    public static final String FIELD_REF_N01 = "refN01";
    public static final String FIELD_REF_N02 = "refN02";
    public static final String FIELD_REF_N03 = "refN03";
    public static final String FIELD_REF_N04 = "refN04";
    public static final String FIELD_REF_N05 = "refN05";
    public static final String FIELD_REF_N06 = "refN06";
    public static final String FIELD_REF_N07 = "refN07";
    public static final String FIELD_REF_N08 = "refN08";
    public static final String FIELD_REF_N09 = "refN09";
    public static final String FIELD_REF_N10 = "refN10";
    public static final String FIELD_REF_D01 = "refD01";
    public static final String FIELD_REF_D02 = "refD02";
    public static final String FIELD_REF_D03 = "refD03";
    public static final String FIELD_REF_D04 = "refD04";
    public static final String FIELD_REF_D05 = "refD05";
    public static final String FIELD_REF_D06 = "refD06";
    public static final String FIELD_REF_D07 = "refD07";
    public static final String FIELD_REF_D08 = "refD08";
    public static final String FIELD_REF_D09 = "refD09";
    public static final String FIELD_REF_D10 = "refD10";
    /**
     * 是否开通电子商业汇票功能
     */
    public static final String FIELD_INVOICE_FLAG = "invoiceFlag";
    /**
     * 联行号
     */
    public static final String FIELD_UNIT_BANK_CODE = "unitBankCode";
    /**
     * 地址信息
     */
    public static final String FIELD_ADDRESS = "address";
    /**
     * MANAGER_ID
     * 客户经理Id
     */
    public static final String FIELD_MANAGER_ID = "managerId";
    /**
     * MANAGER_NAME
     * 客户经理
     */
    public static final String FIELD_MANAGER_NAME = "managerName";
    /**
     * CONTACT
     * 联系方式
     */
    public static final String FIELD_CONTACT = "contact";
    /**
     * VALID_FLAG
     * 是否受限
     */
    public static final String FIELD_VALID_FLAG = "validFlag";
    /**
     * VALID_START
     * 受限开始日
     */
    public static final String FIELD_VALID_START = "validStart";
    /**
     * VALID_END
     * 受限结束日
     */
    public static final String FIELD_VALID_END = "validEnd";
    @Id
    @GeneratedValue
    private Long bankAccountId;
    @NotNull
    private Long companyId;
    private Long bankId;
    @Length(
            max = 1000
    )
    private String bankBranchName;
    @NotEmpty
    @Length(
            max = 100
    )
    private String bankAccountNum;
    @NotEmpty
    @Length(
            max = 1000
    )
    private String bankAccountName;
    @Length(
            max = 100
    )
    private String bankAccountType;
    @Length(
            max = 100
    )
    private String gldAccountCode;
    @Length(
            max = 100
    )
    private String currency;
    @Length(
            max = 100
    )
    private String bankColor;
    @Length(
            max = 1
    )
    private String enabledFlag;
    @Length(
            max = 1
    )
    private String financingLoanFlag;
    @Length(
            max = 100
    )
    private String swiftCode;
    private Date openingDate;
    private Date closingDate;
    @Length(
            max = 100
    )
    private String cnapsCode;
    @Length(
            max = 100
    )
    private String accountNatureOne;
    @Length(
            max = 100
    )
    private String accountNatureTwo;
    @Length(
            max = 100
    )
    private String accountNatureThree;
    @Length(
            max = 2147483647
    )
    private String refV01;
    @Length(
            max = 2147483647
    )
    private String refV02;
    @Length(
            max = 2147483647
    )
    private String refV03;
    @Length(
            max = 2147483647
    )
    private String refV04;
    @Length(
            max = 2147483647
    )
    private String refV05;
    @Length(
            max = 2147483647
    )
    private String refV06;
    @Length(
            max = 2147483647
    )
    private String refV07;
    @Length(
            max = 2147483647
    )
    private String refV08;
    @Length(
            max = 2147483647
    )
    private String refV09;
    @Length(
            max = 2147483647
    )
    private String refV10;
    @Length(
            max = 2147483647
    )
    private String refV11;
    @Length(
            max = 2147483647
    )
    private String refV12;
    @Length(
            max = 2147483647
    )
    private String refV13;
    @Length(
            max = 2147483647
    )
    private String refV14;
    @Length(
            max = 2147483647
    )
    private String refV15;
    private Double refN01;
    private Double refN02;
    private Double refN03;
    private Double refN04;
    private Double refN05;
    private Double refN06;
    private Double refN07;
    private Double refN08;
    private Double refN09;
    private Double refN10;
    private Date refD01;
    private Date refD02;
    private Date refD03;
    private Date refD04;
    private Date refD05;
    private Date refD06;
    private Date refD07;
    private Date refD08;
    private Date refD09;
    private Date refD10;
    @Length(
            max = 10
    )
    private String invoiceFlag;
    @Length(
            max = 100
    )
    private String unitBankCode;
    @Length(
            max = 200
    )
    private String address;
    @Length(
            max = 38
    )
    private Long managerId;
    @Length(
            max = 100
    )
    private String managerName;
    @Length(
            max = 100
    )
    private String contact;
    @Length(
            max = 1
    )
    private String validFlag;
    private Date validStart;
    private Date validEnd;

    @Transient
    private String bankCode;
    @Transient
    private String bankAccountTypeN;
    @Transient
    private String currencyN;
    @Transient
    private String bankName;
    @Transient
    private String bankShortName;
    @Transient
    private Date openingDateFrom;
    @Transient
    private Date openingDateTo;
    private String bankTypeNew;
    @Transient
    private String bankTypeNewN;

    public String getBankTypeNew() {
        return bankTypeNew;
    }

    public void setBankTypeNew(String bankTypeNew) {
        this.bankTypeNew = bankTypeNew;
    }

    public String getBankTypeNewN() {
        return bankTypeNewN;
    }

    public void setBankTypeNewN(String bankTypeNewN) {
        this.bankTypeNewN = bankTypeNewN;
    }

    public CshBankAccount() {
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Long getBankAccountId() {
        return this.bankAccountId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getBankBranchName() {
        return this.bankBranchName;
    }

    public void setBankAccountNum(String bankAccountNum) {
        this.bankAccountNum = bankAccountNum;
    }

    public String getBankAccountNum() {
        return this.bankAccountNum;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountName() {
        return this.bankAccountName;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankAccountType() {
        return this.bankAccountType;
    }

    public void setGldAccountCode(String gldAccountCode) {
        this.gldAccountCode = gldAccountCode;
    }

    public String getGldAccountCode() {
        return this.gldAccountCode;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setBankColor(String bankColor) {
        this.bankColor = bankColor;
    }

    public String getBankColor() {
        return this.bankColor;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setFinancingLoanFlag(String financingLoanFlag) {
        this.financingLoanFlag = financingLoanFlag;
    }

    public String getFinancingLoanFlag() {
        return this.financingLoanFlag;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getSwiftCode() {
        return this.swiftCode;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    public Date getOpeningDate() {
        return this.openingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public Date getClosingDate() {
        return this.closingDate;
    }

    public void setCnapsCode(String cnapsCode) {
        this.cnapsCode = cnapsCode;
    }

    public String getCnapsCode() {
        return this.cnapsCode;
    }

    public void setAccountNatureOne(String accountNatureOne) {
        this.accountNatureOne = accountNatureOne;
    }

    public String getAccountNatureOne() {
        return this.accountNatureOne;
    }

    public void setAccountNatureTwo(String accountNatureTwo) {
        this.accountNatureTwo = accountNatureTwo;
    }

    public String getAccountNatureTwo() {
        return this.accountNatureTwo;
    }

    public void setAccountNatureThree(String accountNatureThree) {
        this.accountNatureThree = accountNatureThree;
    }

    public String getAccountNatureThree() {
        return this.accountNatureThree;
    }

    public void setRefV01(String refV01) {
        this.refV01 = refV01;
    }

    public String getRefV01() {
        return this.refV01;
    }

    public void setRefV02(String refV02) {
        this.refV02 = refV02;
    }

    public String getRefV02() {
        return this.refV02;
    }

    public void setRefV03(String refV03) {
        this.refV03 = refV03;
    }

    public String getRefV03() {
        return this.refV03;
    }

    public void setRefV04(String refV04) {
        this.refV04 = refV04;
    }

    public String getRefV04() {
        return this.refV04;
    }

    public void setRefV05(String refV05) {
        this.refV05 = refV05;
    }

    public String getRefV05() {
        return this.refV05;
    }

    public void setRefV06(String refV06) {
        this.refV06 = refV06;
    }

    public String getRefV06() {
        return this.refV06;
    }

    public void setRefV07(String refV07) {
        this.refV07 = refV07;
    }

    public String getRefV07() {
        return this.refV07;
    }

    public void setRefV08(String refV08) {
        this.refV08 = refV08;
    }

    public String getRefV08() {
        return this.refV08;
    }

    public void setRefV09(String refV09) {
        this.refV09 = refV09;
    }

    public String getRefV09() {
        return this.refV09;
    }

    public void setRefV10(String refV10) {
        this.refV10 = refV10;
    }

    public String getRefV10() {
        return this.refV10;
    }

    public void setRefV11(String refV11) {
        this.refV11 = refV11;
    }

    public String getRefV11() {
        return this.refV11;
    }

    public void setRefV12(String refV12) {
        this.refV12 = refV12;
    }

    public String getRefV12() {
        return this.refV12;
    }

    public void setRefV13(String refV13) {
        this.refV13 = refV13;
    }

    public String getRefV13() {
        return this.refV13;
    }

    public void setRefV14(String refV14) {
        this.refV14 = refV14;
    }

    public String getRefV14() {
        return this.refV14;
    }

    public void setRefV15(String refV15) {
        this.refV15 = refV15;
    }

    public String getRefV15() {
        return this.refV15;
    }

    public void setRefN01(Double refN01) {
        this.refN01 = refN01;
    }

    public Double getRefN01() {
        return this.refN01;
    }

    public void setRefN02(Double refN02) {
        this.refN02 = refN02;
    }

    public Double getRefN02() {
        return this.refN02;
    }

    public void setRefN03(Double refN03) {
        this.refN03 = refN03;
    }

    public Double getRefN03() {
        return this.refN03;
    }

    public void setRefN04(Double refN04) {
        this.refN04 = refN04;
    }

    public Double getRefN04() {
        return this.refN04;
    }

    public void setRefN05(Double refN05) {
        this.refN05 = refN05;
    }

    public Double getRefN05() {
        return this.refN05;
    }

    public void setRefN06(Double refN06) {
        this.refN06 = refN06;
    }

    public Double getRefN06() {
        return this.refN06;
    }

    public void setRefN07(Double refN07) {
        this.refN07 = refN07;
    }

    public Double getRefN07() {
        return this.refN07;
    }

    public void setRefN08(Double refN08) {
        this.refN08 = refN08;
    }

    public Double getRefN08() {
        return this.refN08;
    }

    public void setRefN09(Double refN09) {
        this.refN09 = refN09;
    }

    public Double getRefN09() {
        return this.refN09;
    }

    public void setRefN10(Double refN10) {
        this.refN10 = refN10;
    }

    public Double getRefN10() {
        return this.refN10;
    }

    public void setRefD01(Date refD01) {
        this.refD01 = refD01;
    }

    public Date getRefD01() {
        return this.refD01;
    }

    public void setRefD02(Date refD02) {
        this.refD02 = refD02;
    }

    public Date getRefD02() {
        return this.refD02;
    }

    public void setRefD03(Date refD03) {
        this.refD03 = refD03;
    }

    public Date getRefD03() {
        return this.refD03;
    }

    public void setRefD04(Date refD04) {
        this.refD04 = refD04;
    }

    public Date getRefD04() {
        return this.refD04;
    }

    public void setRefD05(Date refD05) {
        this.refD05 = refD05;
    }

    public Date getRefD05() {
        return this.refD05;
    }

    public void setRefD06(Date refD06) {
        this.refD06 = refD06;
    }

    public Date getRefD06() {
        return this.refD06;
    }

    public void setRefD07(Date refD07) {
        this.refD07 = refD07;
    }

    public Date getRefD07() {
        return this.refD07;
    }

    public void setRefD08(Date refD08) {
        this.refD08 = refD08;
    }

    public Date getRefD08() {
        return this.refD08;
    }

    public void setRefD09(Date refD09) {
        this.refD09 = refD09;
    }

    public Date getRefD09() {
        return this.refD09;
    }

    public void setRefD10(Date refD10) {
        this.refD10 = refD10;
    }

    public Date getRefD10() {
        return this.refD10;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccountTypeN() {
        return this.bankAccountTypeN;
    }

    public void setBankAccountTypeN(String bankAccountTypeN) {
        this.bankAccountTypeN = bankAccountTypeN;
    }

    public String getCurrencyN() {
        return this.currencyN;
    }

    public void setCurrencyN(String currencyN) {
        this.currencyN = currencyN;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankShortName() {
        return this.bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getInvoiceFlag() {
        return invoiceFlag;
    }

    public void setInvoiceFlag(String invoiceFlag) {
        this.invoiceFlag = invoiceFlag;
    }

    public String getUnitBankCode() {
        return unitBankCode;
    }

    public void setUnitBankCode(String unitBankCode) {
        this.unitBankCode = unitBankCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public Date getValidStart() {
        return validStart;
    }

    public void setValidStart(Date validStart) {
        this.validStart = validStart;
    }

    public Date getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(Date validEnd) {
        this.validEnd = validEnd;
    }

    public Date getOpeningDateFrom() {
        return openingDateFrom;
    }

    public void setOpeningDateFrom(Date openingDateFrom) {
        this.openingDateFrom = openingDateFrom;
    }

    public Date getOpeningDateTo() {
        return openingDateTo;
    }

    public void setOpeningDateTo(Date openingDateTo) {
        this.openingDateTo = openingDateTo;
    }
}
