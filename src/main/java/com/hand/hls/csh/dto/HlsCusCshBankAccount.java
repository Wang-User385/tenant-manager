//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "csh_bank_account"
)
@Getter
@Setter
public class HlsCusCshBankAccount extends CshBankAccount {

    //是否开通电子商业汇票功能
    private String invoiceFlag;

    //联行号
    private String unitBankCode;

    private Long managerId;

    private String address;

    private Date creationDate;

    private Date lastUpdateDate;

    private Long createdBy;

    private Long lastUpdatedBy;

    /**
     * 开户日期
     */
    private Date openDate;

    /**
     * 销户日期
     */
    private Date cancellDate;


    private String managerName;


    private String contact;


    @Transient
    private String createName;

    @Transient
    private String lastUpdateName;

    @Transient
    private Date openDateFrom;

    @Transient
    private Date openDateTo;

    private String validFlag;

    private Date validStart;

    private Date validEnd;

    @Transient
    private Double balance;

    @Transient
    private Long bpId;

    @Transient
    private String bankCode;

    private String code;

    private String message;

    private String time;

    @Transient
    private String refV02N;

    @Transient
    private String cityN;

    @Transient
    private String companyFullName;
    @Transient
    private String bankTypeNew;
    @Transient
    private String bankTypeNewN;
    private String purposeType;
    @Transient
    private String purposeTypeN;

    public HlsCusCshBankAccount() {
    }
}
