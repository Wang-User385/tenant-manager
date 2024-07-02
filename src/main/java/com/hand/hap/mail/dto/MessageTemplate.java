package com.hand.hap.mail.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 消息模板.
 *
 * @author qiang.zeng@hand-china.com
 */
@Table(name = "SYS_MESSAGE_TEMPLATE")
@Data
public class MessageTemplate extends BaseDTO {

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getTemplateTypeCn() {
        return templateTypeCn;
    }

    public void setTemplateTypeCn(String templateTypeCn) {
        this.templateTypeCn = templateTypeCn;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getSendTypeCn() {
        return sendTypeCn;
    }

    public void setSendTypeCn(String sendTypeCn) {
        this.sendTypeCn = sendTypeCn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTemplateTypeN() {
        return templateTypeN;
    }

    public void setTemplateTypeN(String templateTypeN) {
        this.templateTypeN = templateTypeN;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getEnableN() {
        return enableN;
    }

    public void setEnableN(String enableN) {
        this.enableN = enableN;
    }

    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long templateId;

    private Long accountId;

    @Length(max = 50)
    @NotEmpty
    private String templateCode;

    @Length(max = 240)
    private String description;

    @Length(max = 50)
    private String templateType;

    @Transient
    private String templateTypeCn;

    @Length(max = 50)
    private String priorityLevel;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @Transient
    private String meaning;

    @Length(max = 50)
    private String sendType;

    @Transient
    private String sendTypeCn;

    @Transient
    private String userName;

    @Transient
    private  String templateTypeN;

    private String enable;

    @Transient
    private String enableN;
}
