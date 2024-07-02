package com.hand.hls.plm.pli.dto;


import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description:贷后检查历史结论dto
 * @Author: Wty
 * @Date: Created om 16:27 2018/5/24
 */
@ExtensionAttribute(disable = true)
@Table(name = "plm_postloan_inspect_res_h")
public class HlsCusPostloanInspectionConclusionH extends BaseDTO {

    public static final String FIELD_CONCLUSION_H_ID = "conclusionHId";
    public static final String FIELD_POSTLOAN_INSPECTION_ID = "postloanInspectionId";
    public static final String FIELD_INSPECTION_DATE = "inspectionDate";
    public static final String FIELD_POSTLOAN_INSPECTION_CONCLUSION = "postloanInspectionConclusion";


    @Id
    @GeneratedValue
    private Long conclusionHId; //贷后检查历史结论id

    @NotNull
    private Long postloanInspectionId; //贷后检查id

    private Date inspectionDate; //贷后检查日期

    @Length(max = 2000)
    private String postloanInspectionConclusion; //贷后检查结论

    @Transient
    private Long bpId;//客户信息id

    public Long getBpId() {
        return bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public void setConclusionHId(Long conclusionHId) {
        this.conclusionHId = conclusionHId;
    }

    public Long getConclusionHId() {
        return conclusionHId;
    }

    public void setPostloanInspectionId(Long postloanInspectionId) {
        this.postloanInspectionId = postloanInspectionId;
    }

    public Long getPostloanInspectionId() {
        return postloanInspectionId;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setPostloanInspectionConclusion(String postloanInspectionConclusion) {
        this.postloanInspectionConclusion = postloanInspectionConclusion;
    }

    public String getPostloanInspectionConclusion() {
        return postloanInspectionConclusion;
    }

}
