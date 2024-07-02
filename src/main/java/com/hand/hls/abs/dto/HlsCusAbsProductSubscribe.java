package com.hand.hls.abs.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ExtensionAttribute(disable = true)
@Table(name = "ct_abs_product_subscribe")
public class HlsCusAbsProductSubscribe extends BaseDTO {
    @Id
    @GeneratedValue
    private Long subscribeId; //pk

    @NotNull
    private Long productId; //产品id

    @Length(max = 20)
    private String subscribeLevel; //认购级别

    private Long subscribeMain; //认购主体

    private Date subscribeDate; //认购日期

    private Long subscribeAccountId; //认购账户

    private BigDecimal subscribeAmount; //认购金额

    private Double interestRate; //利率

    private Long paymentTarget; //付款对象


    private Long structureId;


    private String releCreditFlag;


    private String subscribeType;


    private Long creditLineId;


    private String subscribeAccountName;  // 认购账户名称


    private String subscribeAccountNum;  // 认购账号


    private String subscribeBranchName; // 认购开户行


    private String releComFlag;


    private String subscribeStatus;


    private String subscribeMainName;


    private Long creditContractId;


    @Transient
    private String companyName;  // 公司名称

    private String paymentTargetName;  // 付款对象名称


    private String targetAccountName;  // 对方账户名称


    private String targetAccountNum;  // 对方账号


    private String targetBranchName;

    @Transient
    private  Date validFrom;

    @Transient
    private  Date validTo;

    @Transient
    private  String documentType;

    @Transient
    private  String creditCategory;

    private String creditType;
    @Transient
    private String creditTypeN;
    private BigDecimal creditLineAmt;
    private BigDecimal enabledCreditAmt;
    private BigDecimal UsedCreditAmt;


    @Transient
    private BigDecimal actualCreditAmt;


    @Transient
    private  String projectGrade;


    @Transient
    private Double predictRate;


    @Transient
    private  String creditConNumber;

    @Transient
    private BigDecimal alReleaseAmount;


    @Transient
    private String  subPercent;


    @Transient
    private BigDecimal issueAmount;

    @Transient
    private String subscribeTypeN;

    @Transient
    private String subscribeLevelN;

    @Transient
    private String  subscribeAccountNameN;

    @Transient
    private String targetAccountNameN;

    @Transient
    private String releComFlagN;

    @Transient
    private String releCreditFlagN;

    @Transient
    private String creditConNumberN;

}
