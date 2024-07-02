package com.hand.hls.plm.rc.dto;

/**
 * @Description:租金催收dto
 * @Author: Wty
 * @Date: Created om 21:39 2018/6/6
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "plm_rent_collection")
public class HlsCusRentCollection extends BaseDTO {

    public static final String FIELD_RENT_COLLECTION_ID = "rentCollectionId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_CONTRACT_NUMBER = "contractNumber";
    public static final String FIELD_COLLECTION_TIME = "collectionTime";
    public static final String FIELD_COLLECTION_MEMBER = "collectionMember";
    public static final String FIELD_COLLECTION_METHOD = "collectionMethod";
    public static final String FIELD_CONTACT_PERSON = "contactPerson";
    public static final String FIELD_JOB = "job";
    public static final String FIELD_COLLECTION_RESULT = "collectionResult";
    public static final String FIELD_COLLECTION_NOTE = "collectionNote";


    @Id
    @GeneratedValue
    private Long rentCollectionId; //id

    @NotNull
    private Long contractId; //合同id

    @Length(max = 100)
    private String contractNumber; //合同编号

    private Date collectionTime; //催收时间

    @Length(max = 100)
    private String collectionMember; //催收人员

    @Length(max = 100)
    private String collectionMethod; //催收方式 现场:ONSITE/非现场OFFSITE

    @Length(max = 100)
    private String contactPerson; //对方联系人

    @Length(max = 100)
    private String job; //职务

    @Length(max = 100)
    private String collectionResult; //催收结果

    @Length(max = 100)
    private String collectionNote; //备注

    @Length(max = 200)
    private String telephoneNumber;//电话号码

    @NotNull
    private Long companyId;//公司id

    //工作流ID
    private String procInstId;

    /**
     * 承诺下次还款日
     */
    private Date nextTimePayDate;

    /**
     * 承诺下次还款金额
     */
    private Double nextTimePayAmount;

    /**
     * 后续措施
     */
    private String collectionTake;

    /**
     * 催收状态
     */
    private String collectionStatus;


    private Date creationDate;

    /**
     * 催收方式描述
     */
    @Transient
    private String collectionMethodDesc;

    @Transient
    private String attachmentName;

    @Transient
    private String collectionMethodN;

    public String getCollectionMethodN() {
        return collectionMethodN;
    }

    public void setCollectionMethodN(String collectionMethodN) {
        this.collectionMethodN = collectionMethodN;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public void setRentCollectionId(Long rentCollectionId) {
        this.rentCollectionId = rentCollectionId;
    }

    public Long getRentCollectionId() {
        return rentCollectionId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setCollectionTime(Date collectionTime) {
        this.collectionTime = collectionTime;
    }

    public Date getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionMember(String collectionMember) {
        this.collectionMember = collectionMember;
    }

    public String getCollectionMember() {
        return collectionMember;
    }

    public void setCollectionMethod(String collectionMethod) {
        this.collectionMethod = collectionMethod;
    }

    public String getCollectionMethod() {
        return collectionMethod;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setCollectionResult(String collectionResult) {
        this.collectionResult = collectionResult;
    }

    public String getCollectionResult() {
        return collectionResult;
    }

    public void setCollectionNote(String collectionNote) {
        this.collectionNote = collectionNote;
    }

    public String getCollectionNote() {
        return collectionNote;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public Date getNextTimePayDate() {
        return nextTimePayDate;
    }

    public void setNextTimePayDate(Date nextTimePayDate) {
        this.nextTimePayDate = nextTimePayDate;
    }

    public Double getNextTimePayAmount() {
        return nextTimePayAmount;
    }

    public void setNextTimePayAmount(Double nextTimePayAmount) {
        this.nextTimePayAmount = nextTimePayAmount;
    }

    public String getCollectionTake() {
        return collectionTake;
    }

    public void setCollectionTake(String collectionTake) {
        this.collectionTake = collectionTake;
    }

    public String getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCollectionMethodDesc() {
        return collectionMethodDesc;
    }

    public void setCollectionMethodDesc(String collectionMethodDesc) {
        this.collectionMethodDesc = collectionMethodDesc;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }
}
