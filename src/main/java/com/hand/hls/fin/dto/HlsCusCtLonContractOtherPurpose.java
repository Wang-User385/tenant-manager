package com.hand.hls.fin.dto;

/**
 * add by zhangyu 20180523
 **/

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@ExtensionAttribute(disable = true)
@Table(name = "ct_lon_contract_other_purpose")
public class HlsCusCtLonContractOtherPurpose extends BaseDTO {

    public static final String FIELD_OTHER_PURPOSE_ID = "otherPurposeId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_WITHDRAW_ID = "withdrawId";
    public static final String FIELD_PURPOSE_BP_NAME = "purposeBpName";
    public static final String FIELD_PROJECT_NAME = "projectName";
    public static final String FIELD_PURPOSE_AMOUNT = "purposeAmount";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_DATA_TYPE = "dataType";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name="sequence", sequenceName="CT_LON_CON_OTHER_PURPOSE_s", initialValue=1, allocationSize=1)
    private Long otherPurposeId; //ID

    private Long contractId; //融资合同ID

    private Long withdrawId; //融资提款ID

    @Length(max = 2000)
    private String purposeBpName; //用款主体

    @Length(max = 400)
    private String projectName; //项目名称

    private Double purposeAmount; //用款金额

    @Length(max = 2000)
    private String description; //说明

    private String dataType;

    public void setOtherPurposeId(Long otherPurposeId) {
        this.otherPurposeId = otherPurposeId;
    }

    public Long getOtherPurposeId() {
        return otherPurposeId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public Long getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(Long withdrawId) {
        this.withdrawId = withdrawId;
    }

    public String getPurposeBpName() {
        return purposeBpName;
    }

    public void setPurposeBpName(String purposeBpName) {
        this.purposeBpName = purposeBpName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getPurposeAmount() {
        return purposeAmount;
    }

    public void setPurposeAmount(Double purposeAmount) {
        this.purposeAmount = purposeAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
