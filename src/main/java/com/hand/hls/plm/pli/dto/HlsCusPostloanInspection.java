package com.hand.hls.plm.pli.dto;

/**
 * @Description:贷后检查dto
 * @Author: Wty
 * @Date: Created om 16:27 2018/5/24
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "plm_postloan_inspection")
public class HlsCusPostloanInspection extends BaseDTO {


    public static final String FIELD_BP_ID = "bpId";


    @Id
    @GeneratedValue
    private Long postloanInspectionId; //贷后检查id

    @Length(max = 100)
    private String postloanInspectionNumber; //贷后检查编号

    private Long companyId; //公司id

    @Length(max = 100)
    private String documentType; //单据类型

    @Length(max = 100)
    private String documentCategory; //单据类别

    @Length(max = 100)
    private String businessType; //业务类型

    @Length(max = 100)
    private String status; //审批结果

    @NotNull
    private Long bpId; //客户信息id

    @Length(max = 100)
    private String inspectFrenquency; //检查频率

    @Length(max = 100)
    private String inspectionMethod; //检查方式 定期/不定期

    private Date inspectionDate; //检查日期

    @Length(max = 1000)
    private String peerStaff; //同行人员

    @Length(max = 1000)
    private String checkAddress; //检查地址

    @Length(max = 2000)
    private String customerOperation; //客户经营情况

    @Length(max = 2000)
    private String useFundsCheck; //资金用途检查

    @Length(max = 2000)
    private String financialStatus; //财务状况分析

    @Length(max = 2000)
    private String customerRepayment; //客户还款情况

    @Length(max = 2000)
    private String otherSituation; //其他情况

    @Length(max = 2000)
    private String postloanInspectionConclusion; //贷后检查结论

    private Long creationUserId;//创建用户id

    private String inspectionType; //检查性质(现场/非现场)

    private Long inspectionYear; //检查年份

    private Long inspectionMonth; //检查月份

    private Date expectedCompletionDate; //预计完成日期

    private Date startDate; //检查开始日

    private Date endDate; //检查截止日

    private String projectManager; //贷后现场检查项目经理

    private String riskExaminer; //风险管理部现场检查人

    private Date postloanInspectionCreatetime;//创建时间

    private String inspectors; //检查人员

    private String offsiteInspection; //非现场检查方式

    private String otherInspection; //其他检查方式

    private Long finStatementHdId;

    private String rptDate; //财报日期
    @Length(max = 50)
    private String intervieweeName1; //被采访人姓名1
    @Length(max = 50)
    private String intervieweeName2; //被采访人姓名2
    @Length(max = 100)
    private String intervieweeJobTitle1; //被采访人职位1
    @Length(max = 100)
    private String intervieweeJobTitle2; //被采访人职位2

    @Transient
    private HlsCusFiveClassification fiveClassification;//五级分类

    @Transient
    private List<HlsCusPostloanInspectionConclusionH> postloanInspectionConclusionHList;//贷后检查历史记录

    @Transient
    private String bpName;//客户名称

    @Transient
    private String bpCategory;//客户类别

    @Transient
    private String bpCategoryDescription;//客户类别描述

    @Transient
    private String bpType;//客户类型

    @Transient
    private String bpTypeDescription;//客户类型描述

    @Transient
    private String bpCode;//客户编码

    @Transient
    private String createPerson;//创建人

    @Transient
    private Long fiveClassificationId;//五级分类id

    @Transient
    private String inspectionDateFrom;//检查时间从

    @Transient
    private String inspectionDateTo;//检查时间到

    @Transient
    private Long contractBpId;//合同商业伙伴id

    @Transient
    private String contractFrequency;//合同贷后检查频率

    @Transient
    private String creationName;//创建人名

    @Transient
    private Date remindDate;//提醒日

    @Transient
    private Date agreementInspectionDate;//约定检查日

    private Date appointedDate;//约定检查日

    @Transient
    private String checkContain;//检查清单中内容

    @Transient
    private String inspectionFrequencyList;//首页待检查频率查询List

    @Transient
    private String[] inspectionFrequencyArray;

    @Transient
    private String bpClass;//客户分类

    @Transient
    private String associatedDivision;//关联区分

    private String isPlaneCheck;
    @Transient
    private String printType;

    @Transient
    private String inspectionMethodN;

    @Transient
    private String inspectionTypeN;

    @Transient
    private String statusN;

    public Long getFinStatementHdId() {
        return finStatementHdId;
    }

    public void setFinStatementHdId(Long finStatementHdId) {
        this.finStatementHdId = finStatementHdId;
    }

    public Long getPostloanInspectionId() {
        return postloanInspectionId;
    }

    public void setPostloanInspectionId(Long postloanInspectionId) {
        this.postloanInspectionId = postloanInspectionId;
    }

    public String getPostloanInspectionNumber() {
        return postloanInspectionNumber;
    }

    public void setPostloanInspectionNumber(String postloanInspectionNumber) {
        this.postloanInspectionNumber = postloanInspectionNumber;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBpId() {
        return bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getInspectFrenquency() {
        return inspectFrenquency;
    }

    public void setInspectFrenquency(String inspectFrenquency) {
        this.inspectFrenquency = inspectFrenquency;
    }

    public String getInspectionMethod() {
        return inspectionMethod;
    }

    public void setInspectionMethod(String inspectionMethod) {
        this.inspectionMethod = inspectionMethod;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getPeerStaff() {
        return peerStaff;
    }

    public void setPeerStaff(String peerStaff) {
        this.peerStaff = peerStaff;
    }

    public String getCheckAddress() {
        return checkAddress;
    }

    public void setCheckAddress(String checkAddress) {
        this.checkAddress = checkAddress;
    }

    public String getCustomerOperation() {
        return customerOperation;
    }

    public void setCustomerOperation(String customerOperation) {
        this.customerOperation = customerOperation;
    }

    public String getUseFundsCheck() {
        return useFundsCheck;
    }

    public void setUseFundsCheck(String useFundsCheck) {
        this.useFundsCheck = useFundsCheck;
    }

    public String getFinancialStatus() {
        return financialStatus;
    }

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public String getCustomerRepayment() {
        return customerRepayment;
    }

    public void setCustomerRepayment(String customerRepayment) {
        this.customerRepayment = customerRepayment;
    }

    public String getOtherSituation() {
        return otherSituation;
    }

    public void setOtherSituation(String otherSituation) {
        this.otherSituation = otherSituation;
    }

    public String getPostloanInspectionConclusion() {
        return postloanInspectionConclusion;
    }

    public void setPostloanInspectionConclusion(String postloanInspectionConclusion) {
        this.postloanInspectionConclusion = postloanInspectionConclusion;
    }

    public Long getCreationUserId() {
        return creationUserId;
    }

    public void setCreationUserId(Long creationUserId) {
        this.creationUserId = creationUserId;
    }

    public HlsCusFiveClassification getFiveClassification() {
        return fiveClassification;
    }

    public void setFiveClassification(HlsCusFiveClassification fiveClassification) {
        this.fiveClassification = fiveClassification;
    }

    public List<HlsCusPostloanInspectionConclusionH> getPostloanInspectionConclusionHList() {
        return postloanInspectionConclusionHList;
    }

    public void setPostloanInspectionConclusionHList(List<HlsCusPostloanInspectionConclusionH> postloanInspectionConclusionHList) {
        this.postloanInspectionConclusionHList = postloanInspectionConclusionHList;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpCategory() {
        return bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public String getBpCategoryDescription() {
        return bpCategoryDescription;
    }

    public void setBpCategoryDescription(String bpCategoryDescription) {
        this.bpCategoryDescription = bpCategoryDescription;
    }

    public String getBpType() {
        return bpType;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public String getBpTypeDescription() {
        return bpTypeDescription;
    }

    public void setBpTypeDescription(String bpTypeDescription) {
        this.bpTypeDescription = bpTypeDescription;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Long getFiveClassificationId() {
        return fiveClassificationId;
    }

    public void setFiveClassificationId(Long fiveClassificationId) {
        this.fiveClassificationId = fiveClassificationId;
    }

    public String getInspectionDateFrom() {
        return inspectionDateFrom;
    }

    public void setInspectionDateFrom(String inspectionDateFrom) {
        this.inspectionDateFrom = inspectionDateFrom;
    }

    public String getInspectionDateTo() {
        return inspectionDateTo;
    }

    public void setInspectionDateTo(String inspectionDateTo) {
        this.inspectionDateTo = inspectionDateTo;
    }

    public Long getContractBpId() {
        return contractBpId;
    }

    public void setContractBpId(Long contractBpId) {
        this.contractBpId = contractBpId;
    }

    public String getContractFrequency() {
        return contractFrequency;
    }

    public void setContractFrequency(String contractFrequency) {
        this.contractFrequency = contractFrequency;
    }

    public String getCreationName() {
        return creationName;
    }

    public void setCreationName(String creationName) {
        this.creationName = creationName;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    public Date getAgreementInspectionDate() {
        return agreementInspectionDate;
    }

    public void setAgreementInspectionDate(Date agreementInspectionDate) {
        this.agreementInspectionDate = agreementInspectionDate;
    }

    public String getCheckContain() {
        return checkContain;
    }

    public void setCheckContain(String checkContain) {
        this.checkContain = checkContain;
    }

    public String getInspectionFrequencyList() {
        return inspectionFrequencyList;
    }

    public void setInspectionFrequencyList(String inspectionFrequencyList) {
        this.inspectionFrequencyList = inspectionFrequencyList;
    }

    public String[] getInspectionFrequencyArray() {
        return inspectionFrequencyArray;
    }

    public void setInspectionFrequencyArray(String[] inspectionFrequencyArray) {
        this.inspectionFrequencyArray = inspectionFrequencyArray;
    }

    public String getBpClass() {
        return bpClass;
    }

    public void setBpClass(String bpClass) {
        this.bpClass = bpClass;
    }

    public String getAssociatedDivision() {
        return associatedDivision;
    }

    public void setAssociatedDivision(String associatedDivision) {
        this.associatedDivision = associatedDivision;
    }

    public Date getAppointedDate() {
        return appointedDate;
    }

    public void setAppointedDate(Date appointedDate) {
        this.appointedDate = appointedDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public Long getInspectionYear() {
        return inspectionYear;
    }

    public void setInspectionYear(Long inspectionYear) {
        this.inspectionYear = inspectionYear;
    }

    public Long getInspectionMonth() {
        return inspectionMonth;
    }

    public void setInspectionMonth(Long inspectionMonth) {
        this.inspectionMonth = inspectionMonth;
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public String getRiskExaminer() {
        return riskExaminer;
    }

    public void setRiskExaminer(String riskExaminer) {
        this.riskExaminer = riskExaminer;
    }

    public Date getPostloanInspectionCreatetime() {
        return postloanInspectionCreatetime;
    }

    public void setPostloanInspectionCreatetime(Date postloanInspectionCreatetime) {
        this.postloanInspectionCreatetime = postloanInspectionCreatetime;
    }

    public String getInspectors() {
        return inspectors;
    }

    public void setInspectors(String inspectors) {
        this.inspectors = inspectors;
    }

    public String getOffsiteInspection() {
        return offsiteInspection;
    }

    public void setOffsiteInspection(String offsiteInspection) {
        this.offsiteInspection = offsiteInspection;
    }

    public String getOtherInspection() {
        return otherInspection;
    }

    public void setOtherInspection(String otherInspection) {
        this.otherInspection = otherInspection;
    }

    public String getRptDate() {
        return rptDate;
    }

    public void setRptDate(String rptDate) {
        this.rptDate = rptDate;
    }

    public String getIsPlaneCheck() {
        return isPlaneCheck;
    }

    public void setIsPlaneCheck(String isPlaneCheck) {
        this.isPlaneCheck = isPlaneCheck;
    }

    public String getIntervieweeName1() {
        return intervieweeName1;
    }

    public void setIntervieweeName1(String intervieweeName1) {
        this.intervieweeName1 = intervieweeName1;
    }

    public String getIntervieweeName2() {
        return intervieweeName2;
    }

    public void setIntervieweeName2(String intervieweeName2) {
        this.intervieweeName2 = intervieweeName2;
    }

    public String getIntervieweeJobTitle1() {
        return intervieweeJobTitle1;
    }

    public void setIntervieweeJobTitle1(String intervieweeJobTitle1) {
        this.intervieweeJobTitle1 = intervieweeJobTitle1;
    }

    public String getIntervieweeJobTitle2() {
        return intervieweeJobTitle2;
    }

    public void setIntervieweeJobTitle2(String intervieweeJobTitle2) {
        this.intervieweeJobTitle2 = intervieweeJobTitle2;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public String getInspectionMethodN() {
        return inspectionMethodN;
    }

    public void setInspectionMethodN(String inspectionMethodN) {
        this.inspectionMethodN = inspectionMethodN;
    }

    public String getInspectionTypeN() {
        return inspectionTypeN;
    }

    public void setInspectionTypeN(String inspectionTypeN) {
        this.inspectionTypeN = inspectionTypeN;
    }

    public String getStatusN() {
        return statusN;
    }

    public void setStatusN(String statusN) {
        this.statusN = statusN;
    }
}
