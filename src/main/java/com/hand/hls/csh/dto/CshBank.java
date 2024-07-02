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
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "csh_bank"
)
public class CshBank extends BaseDTO {
    public static final String FIELD_BANK_ID = "bankId";
    public static final String FIELD_BANK_CODE = "bankCode";
    public static final String FIELD_BANK_NAME = "bankName";
    public static final String FIELD_BANK_SHORT_NAME = "bankShortName";
    public static final String FIELD_BANK_TYPE = "bankType";
    public static final String FIELD_ORG_TYPE = "orgType";
    public static final String FIELD_BP_ID = "bpId";
    public static final String FIELD_START_ACTIVE_DATE = "startActiveDate";
    public static final String FIELD_END_ACTIVE_DATE = "endActiveDate";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
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
    public static final String FIELD_BANK_DOSSIER = "bank_dossier";
    @Id
    @GeneratedValue
    private Long bankId;
    @NotEmpty
    @Length(
            max = 100
    )
    private String bankCode;
    @Length(
            max = 2000
    )
    private String bankName;
    @Length(
            max = 1000
    )
    private String bankShortName;
    @Length(
            max = 100
    )
    private String bankType;
    @Length(
            max = 100
    )
    private String orgType;
    @Length(
            max = 200
    )
    private String bankDossier;
    private Long bpId;
    private Date startActiveDate;
    private Date endActiveDate;
    @Length(
            max = 1
    )
    private String enabledFlag;
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
    @Transient
    private String bankTypeN;

    public CshBank() {
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getBankShortName() {
        return this.bankShortName;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankType() {
        return this.bankType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgType() {
        return this.orgType;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getBpId() {
        return this.bpId;
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

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
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

    public String getBankTypeN() {
        return this.bankTypeN;
    }

    public void setBankTypeN(String bankTypeN) {
        this.bankTypeN = bankTypeN;
    }

    public String getBankDossier() {
        return bankDossier;
    }

    public void setBankDossier(String bankDossier) {
        this.bankDossier = bankDossier;
    }
}
