package com.hand.hls.mort.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "hls_lease_mortgage")
@Getter
@Setter
public class HlsMortgage {

    @Id
    @GeneratedValue
    private Long mortgageId;
    /*项目ID*/
    private Long projectId;
    /*项目编码*/
    private String projectNumber;
    /*合同ID*/
    private Long contractId;
    /*合同编码*/
    private String contractNumber;
    /*抵押编号*/
    private  String mortgageNumber;
    /*合作方*/
    @Transient
    private String manufacturerIdN;
    /*主机厂*/
    @Transient
    private String factoryIdN;
    /*承租人*/
    @Transient
    private String tenantName;
    /*CONTRACT_LEASE_ITEM_ID租赁物ID*/
    private Long contractLeaseItemId;
    /*PROJECT_LEASE_ITEM_ID租赁物ID*/
    private Long projectLeaseItemId;
    /*租赁物名称*/
    private String fullName;
    /*业务线*/
    private  String division;
    /*项目主办ID*/
    @Transient
    private Long hostProjectManager;
    /*项目主办*/
    @Transient
    private String hostProjectManagerN;
    /*实际上牌地（省）*/
    private String actualProvinceCode;
    /*实际上牌地（市）*/
    private String actualCityCode;
    /*抵押超期倒计时（天）*/
    @Transient
    private String overtimeCountdown;
    /*机动车登记编号*/
    private String vehicleRegistNumber;
    /*机动车所有权人*/
    private String vehicleOwner;
    /*抵押登记编号*/
    private String mortgageRegistNumber;
    /*抵押登记日期*/
    private Date mortgageRegistDate;
    /*抵押权人名称*/
    private String mortgageeName;
    /*规格型号ID*/
    private Long classifyId;
    /*规格型号*/
    private String classifyIdN;
    /*所在地*/
    private String location;
    /*登记机关*/
    private String registrationAuthority;
    /*抵押说明*/
    private String description;

    /*实际上牌地（省）*/
    @Transient
    private String actualProvinceCodeN;
    /*实际上牌地（市）*/
    @Transient
    private String actualCityCodeN;
    /*抵押状态*/
    @Transient
    private String pledgeState;
    /*抵押状态*/
    @Transient
    private String pledgeStateN;

    @Transient
    private String productModel;

    private String mortgageStatus;

    @Transient
    private String mortgageStatusN;

    @Transient
    private String bpIdTenantN;

    @Transient
    private String businessType;

    @Transient
    private String businessTypeN;

    @Transient
    private String divisionN;

    @Transient
    private String employeeIdN;

    @Transient
    private String unitIdN;

    @Transient
    private Date actualPayDate;


    private Date mortgageApprovedDate;

    @Transient
    private Long processInstanceId;

    @Transient
    private String documentName;

    @Transient
    private Date uploadDate;

}
