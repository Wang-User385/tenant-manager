package com.hand.hls.prj.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;
import java.util.Date;

@Table(
        name = "hls_bp_master_role"
)
public class HlsBpMasterRole {
    @Id
    @Column(
            name = "BP_ROLE_ID"
    )
    @GeneratedValue
    private Long bpRoleId;
//    @Id
    @Column(
            name = "BP_ID"
    )
    private Long bpId;
    @Column(
            name = "BP_CATEGORY"
    )
    private String bpCategory;
    @Transient
    private String bpCategoryN;
//    @Id
    @Column(
            name = "BP_TYPE"
    )
    private String bpType;
    @Transient
    private String bpTypeN;
    @Column(
            name = "PRIMARY_FLAG"
    )
    private String primaryFlag;
    @Column(
            name = "ENABLED_FLAG"
    )
    private String enabledFlag;
    @Column(
            name = "CREATED_BY"
    )
    private Long createdBy;
    @Column(
            name = "CREATION_DATE"
    )
    private Date creationDate;
    @Column(
            name = "LAST_UPDATED_BY"
    )
    private Long lastUpdatedBy;
    @Column(
            name = "LAST_UPDATE_DATE"
    )
    private Date lastUpdateDate;
    @Column(
            name = "REF_V01"
    )
    private String refV01;
    @Column(
            name = "REF_V02"
    )
    private String refV02;
    @Column(
            name = "REF_V03"
    )
    private String refV03;
    @Column(
            name = "REF_V04"
    )
    private String refV04;
    @Column(
            name = "REF_V05"
    )
    private String refV05;
    @Column(
            name = "REF_N01"
    )
    private Long refN01;
    @Column(
            name = "REF_N02"
    )
    private Long refN02;
    @Column(
            name = "REF_N03"
    )
    private Long refN03;
    @Column(
            name = "REF_N04"
    )
    private Long refN04;
    @Column(
            name = "REF_N05"
    )
    private Long refN05;
    @Column(
            name = "REF_D01"
    )
    private Date refD01;
    @Column(
            name = "REF_D02"
    )
    private Date refD02;
    @Column(
            name = "REF_D03"
    )
    private Date refD03;
    @Column(
            name = "REF_D04"
    )
    private Date refD04;
    @Column(
            name = "REF_D05"
    )
    private Date refD05;

    public HlsBpMasterRole() {
    }

//    public Long getBpRoleId() {
//        return this.bpRoleId;
//    }
//
//    public void setBpRoleId(Long bpRoleId) {
//        this.bpRoleId = bpRoleId;
//    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getBpCategory() {
        return this.bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public String getBpCategoryN() {
        return this.bpCategoryN;
    }

    public void setBpCategoryN(String bpCategoryN) {
        this.bpCategoryN = bpCategoryN;
    }

    public String getBpType() {
        return this.bpType;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public String getPrimaryFlag() {
        return this.primaryFlag;
    }

    public void setPrimaryFlag(String primaryFlag) {
        this.primaryFlag = primaryFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getRefV01() {
        return this.refV01;
    }

    public void setRefV01(String refV01) {
        this.refV01 = refV01;
    }

    public String getRefV02() {
        return this.refV02;
    }

    public void setRefV02(String refV02) {
        this.refV02 = refV02;
    }

    public String getRefV03() {
        return this.refV03;
    }

    public void setRefV03(String refV03) {
        this.refV03 = refV03;
    }

    public String getRefV04() {
        return this.refV04;
    }

    public void setRefV04(String refV04) {
        this.refV04 = refV04;
    }

    public String getRefV05() {
        return this.refV05;
    }

    public void setRefV05(String refV05) {
        this.refV05 = refV05;
    }

    public Long getRefN01() {
        return this.refN01;
    }

    public void setRefN01(Long refN01) {
        this.refN01 = refN01;
    }

    public Long getRefN02() {
        return this.refN02;
    }

    public void setRefN02(Long refN02) {
        this.refN02 = refN02;
    }

    public Long getRefN03() {
        return this.refN03;
    }

    public void setRefN03(Long refN03) {
        this.refN03 = refN03;
    }

    public Long getRefN04() {
        return this.refN04;
    }

    public void setRefN04(Long refN04) {
        this.refN04 = refN04;
    }

    public Long getRefN05() {
        return this.refN05;
    }

    public void setRefN05(Long refN05) {
        this.refN05 = refN05;
    }

    public Date getRefD01() {
        return this.refD01;
    }

    public void setRefD01(Date refD01) {
        this.refD01 = refD01;
    }

    public Date getRefD02() {
        return this.refD02;
    }

    public void setRefD02(Date refD02) {
        this.refD02 = refD02;
    }

    public Date getRefD03() {
        return this.refD03;
    }

    public void setRefD03(Date refD03) {
        this.refD03 = refD03;
    }

    public Date getRefD04() {
        return this.refD04;
    }

    public void setRefD04(Date refD04) {
        this.refD04 = refD04;
    }

    public Date getRefD05() {
        return this.refD05;
    }

    public void setRefD05(Date refD05) {
        this.refD05 = refD05;
    }

    public String getBpTypeN() {
        return this.bpTypeN;
    }

    public void setBpTypeN(String bpTypeN) {
        this.bpTypeN = bpTypeN;
    }

    public Long getBpRoleId() {
        return bpRoleId;
    }

    public void setBpRoleId(Long bpRoleId) {
        this.bpRoleId = bpRoleId;
    }
}
