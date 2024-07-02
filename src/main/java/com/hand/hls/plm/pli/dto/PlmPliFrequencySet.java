package com.hand.hls.plm.pli.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@ExtensionAttribute(disable = true)
@Table(name = "pli_frequency_set")
public class PlmPliFrequencySet extends BaseDTO {

    public static final String FREQUENCY_ID = "frequencyId";
    public static final String FREQUENCY_CODE = "frequencyCode";
    public static final String FREQUENCY_NAME = "frequencyName";
    public static final String ON_SITE_INSPECT = "onSiteInspect";
    public static final String OFF_SITE_INSPECT = "offSiteInspect";
    public static final String PRIORITY_INSPECT = "priorityInspect";
    public static final String NOTE = "note";
    public static final String ENABLE_FLAG = "enableFlag";

    @Id
    @GeneratedValue
    private Long frequencyId;
    @NotNull
    private String frequencyCode;

    @Condition(operator = "like")
    private String frequencyName;

    private Long onSiteInspect;

    private Long offSiteInspect;

    private String priorityInspect;

    private String note;

    private String enableFlag;

    private Long onSiteReminderDay;

    private Long offSiteReminderDay;

    private String frequencyType;

    @Transient
    private String frequencyTypeN;

    @Transient
    private String priorityInspectN;



    public Long getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(Long frequencyId) {
        this.frequencyId = frequencyId;
    }

    public String getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(String frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public String getFrequencyName() {
        return frequencyName;
    }

    public void setFrequencyName(String frequencyName) {
        this.frequencyName = frequencyName;
    }

    public Long getOnSiteInspect() {
        return onSiteInspect;
    }

    public void setOnSiteInspect(Long onSiteInspect) {
        this.onSiteInspect = onSiteInspect;
    }

    public Long getOffSiteInspect() {
        return offSiteInspect;
    }

    public void setOffSiteInspect(Long offSiteInspect) {
        this.offSiteInspect = offSiteInspect;
    }

    public String getPriorityInspect() {
        return priorityInspect;
    }

    public void setPriorityInspect(String priorityInspect) {
        this.priorityInspect = priorityInspect;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public Long getOnSiteReminderDay() {
        return onSiteReminderDay;
    }

    public void setOnSiteReminderDay(Long onSiteReminderDay) {
        this.onSiteReminderDay = onSiteReminderDay;
    }

    public Long getOffSiteReminderDay() {
        return offSiteReminderDay;
    }

    public void setOffSiteReminderDay(Long offSiteInspectDay) {
        this.offSiteReminderDay = offSiteInspectDay;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    public String getFrequencyTypeN() {
        return frequencyTypeN;
    }

    public void setFrequencyTypeN(String frequencyTypeN) {
        this.frequencyTypeN = frequencyTypeN;
    }

    public String getPriorityInspectN() {
        return priorityInspectN;
    }

    public void setPriorityInspectN(String priorityInspectN) {
        this.priorityInspectN = priorityInspectN;
    }
}
