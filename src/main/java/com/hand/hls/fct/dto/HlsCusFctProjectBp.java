package com.hand.hls.fct.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @Author: cyy
 * @Description:
 * @Date: Created in 2018/4/18 10:28
 * @Modified By:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ExtensionAttribute(disable = true)
@Table(name = "fct_project_bp")
public class HlsCusFctProjectBp extends FctProjectBp {

    private Long chanceId;

    private String downRepaymentParty;

    private String useFeeRepaymentParty;

    private String compensationRepaymentParty;

    private String repayGuaranteeParty;

    private String guaranteeType;

    private Long guaranteeAmount;

    private Double mortgageGuaranteeAmount;

    private String mortgageGuaAmtCapital;

    private Double mortgageRate;

    private Double mortgageAvailableAmount;

    private String mortgageAvailAmtCapital;

    private Double pledgeAmount;

    private Double pledgeAmountCapital;

    private Double pledgeRate;

    private Double repayGuaranteeSubAmount;

    private String description;
    /**
     * 2018-11-29 添加字段
     * 保证币种/抵押币种/质押币种【2018-11-29添加】
     */
    private String communalCurrency;
    /**
     * 抵押的评估币种/质押的质押物币种【2018-11-29添加】
     */
    private String communalOtherCurrency;
    /**
     * 抵押物评估值/质押物价值【2018-11-29添加】
     */
    private String communalValue;
    /**
     * 抵押评估日期【2018-11-29添加】
     * MORTGAGE_EVALUATION_DATE
     */
    private Date mortgageEvaluationDate;
    /**
     * 抵押评估机构名称【2018-11-29添加】
     * MORTGAGE_EVALUATION_NAME
     */
    private String mortgageEvaluationName;
    /**
     *抵押评估机构组织机构代码【2018-11-29添加】
     * MORTGAGE_EVALUATION_CODE
     */
    private String mortgageEvaluationCode;
    /**
     * 抵押物种类/质押物类型【2018-11-29添加】
     * COLLATERAL_TYPE
     */
    private String collateralType;
    /**
     * 登记机关【2018-11-29添加】
     * REGISTRATION_AUTHORITY
     */
    private String registrationAuthority;
    /**
     * 登记日期【2018-11-29添加】
     * REGISTRATION_DATE
     */
    private Date registrationDate;
    /**
     * 抵押物说明【2018-11-29添加】
     * MORTGAGE_DESC
     */
    private String mortgageDesc;
    /**
     * 信贷系统担保物的编码/信贷系统质押物编号【2018-11-29添加】
     * COLLATERAL_CODE
     */
    private String collateralCode;

    /**
     * 地址【外键】
     */
    private Long addressId;
    /**
     * 联系人【外键】
     */
    private Long contactInfoId;
    /**
     * 银行账户Id
     */
    private Long bankAccountId;

    /**
     * 自然人担保形式
     */
    private String npGuaranteeType;

    /**
     * 合同编码
     */
    private String contractNumber;

    /**
     * 合同流水号
     */
    private String serialNumber;

    /**
     * 被担保方
     */
    private String securedParty;

    /**
     * 到期日
     */

    private Date expiryDate;

    /**
     * 商业伙伴类型(自然人或法人)
     */
    @Transient
    private String bpClass;

    @Transient
    private String bankFullName;
    /**
     * 贷款卡编码
     */
    @Transient
    private String loanCardNum;

    /**
     * 证件类型
     */
    @Transient
    private String idType;

    /**
     * 证件号
     */
    @Transient
    private String idCardNo;
    /**
     * 联系人信息，该字段是由【国家+省+市+区+详细地址】拼接而成
     */
    private String addressInfo;
    /**
     * 地址信息，该字段是由【联系人+手机号码】拼接而成
     */
    private String contactInfo;
    /**
     * 项目角色描述
     */
    @Transient
    private String bpRoleTypeDesc;

    /**
     * 商业伙伴的联系电话
     */
    @Transient
    private String phone;
    /**
     * 新增备注,与原备注不同[备注2]
     */
    private String remarks;
    /**
     * hls_bp_master
     * 开票纳税人类型
     */
    @Transient
    private String taxpayerType;

    @Transient
    private String taxpayerTypeDesc;

    /**
     * hls_bp_master
     * 开票发票抬头
     */
    @Transient
    private String invoiceTitle;

    /**
     * hls_bp_master
     * 开票地址和电话
     */
    @Transient
    private String invoiceBpAddressPhoneNum;

    /**
     * hls_bp_master
     * 开票行和账户号
     */
    @Transient
    private String invoiceBpBankAccount;

    @Transient
    private String sellerAndBuyer;

    @Transient
    private String bpCodes;

    /**
     * 支行名称
     */
    @Transient
    private String bankBranchName;

    @Transient
    private String approvalNumber;

    @Transient
    private String contractName;

    @Transient
    private String mainContractNumber;

    @Transient
    private String creditGrantorName;

    @Transient
    private String guaranteeAmountStr;

    @Transient
    private String recipient;

    @Transient
    private String  businessType;

    @Transient
    private  Date rateValidFrom;

    @Transient
    private  Date rateValidTo;

    @Transient
    private  Date signDate;

    @Transient
    private String contractStatus;

    @Transient
    private Long contractId;

    @Transient
    private String communalCurrencyDesc;

    @Transient
    private String communalOtherCurrencyDesc;

    @Transient
    private String guaranteeTypeDesc;

    @Transient
    private String npGuaranteeTypeDesc;

    @Transient
    private String securedPartyDesc;

    @Transient
    private String collateralTypeDesc;

    @Transient
    private String  businessTypeN;

    @Transient
    private String contractStatusN;
}
