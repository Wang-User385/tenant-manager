package com.hand.hls.fin.dto;

/**
 * created by zhangyu 2018/5/21
 **/

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@ExtensionAttribute(disable = true)
@Table(name = "ct_lon_con_sign_sales_info")
public class HlsCusCtLonContractConsignmentSalesInfo extends BaseDTO {

    public static final String FIELD_CONSIGNMENT_SALES_ID = "consignmentSalesId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_CONSIGNMENT_SALES_TYPE = "consignmentSalesType";
    public static final String FIELD_CONSIGNMENT_SALES_BP_ID = "consignmentSalesBpId";
    public static final String FIELD_CELL_PHONE = "cellPhone";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_ZIPCODE = "zipcode";


    @Id
    @GeneratedValue
    private Long consignmentSalesId; //融资合同承销商ID

    @NotNull
    private Long contractId; //融资合同ID

    @Length(max = 30)
    private String consignmentSalesType; //承销商类型

    private Long consignmentSalesBpId; //承销商机构ID

    @Length(max = 200)
    private String contactPerson;

    @Length(max = 30)
    private String cellPhone;

    @Length(max = 30)
    private String phone;

    @Length(max = 30)
    private String email;

    @Length(max = 2000)
    private String address;

    @Length(max = 30)
    private String zipcode;

    @Transient
    private String consignmentSalesBpName;

    @Transient
    private String BpName;

    @Transient
    private String consignmentSalesTypeDesc;

    public Long getConsignmentSalesBpId() {
        return consignmentSalesBpId;
    }

    public Long getConsignmentSalesId() {
        return consignmentSalesId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public String getConsignmentSalesType() {
        return consignmentSalesType;
    }

    public void setConsignmentSalesType(String consignmentSalesType) {
        this.consignmentSalesType = consignmentSalesType;
    }

    public void setConsignmentSalesBpId(Long consignmentSalesBpId) {
        this.consignmentSalesBpId = consignmentSalesBpId;
    }

    public void setConsignmentSalesId(Long consignmentSalesId) {
        this.consignmentSalesId = consignmentSalesId;
    }

    public String getConsignmentSalesBpName() {
        return consignmentSalesBpName;
    }

    public void setConsignmentSalesBpName(String consignmentSalesBpName) {
        this.consignmentSalesBpName = consignmentSalesBpName;
    }

    public String getBpName() {
        return BpName;
    }

    public void setBpName(String bpName) {
        BpName = bpName;
    }

    public String getConsignmentSalesTypeDesc() {
        return consignmentSalesTypeDesc;
    }

    public void setConsignmentSalesTypeDesc(String consignmentSalesTypeDesc) {
        this.consignmentSalesTypeDesc = consignmentSalesTypeDesc;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
