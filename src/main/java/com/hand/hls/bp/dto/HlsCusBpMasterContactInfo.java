package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hls.prj.dto.HlsBpMasterContactInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "hls_bp_master_contact_info")
@Getter
@Setter
public class HlsCusBpMasterContactInfo extends HlsBpMasterContactInfo {

    @Length(max = 255)
    private String isSignPerson;

    @Length(max = 255)
    private String isMailInvoiceContact;

    private Long addressId;

    private String gender;

    private String title;

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    @Transient
    private String  contactInfo;
    @Transient
    private String addressInfo;
    @Transient
    private String emergencyContact;
    @Transient
    private String contactAddress;
    @Transient
    private String positionN;
    @Transient
    private String genderN;
    @Transient
    private String refV01N;

    private String country;
    private String province;
    private String city;
    private String district;
    @Transient
    private String countryN;
    @Transient
    private String provinceN;
    @Transient
    private String cityN;
    @Transient
    private String districtN;
    private String address;

    private String personType;

    private Date idExpirationDate;
    @Transient
    private String titleN;

    public String getTitleN() {
        return titleN;
    }

    public void setTitleN(String titleN) {
        this.titleN = titleN;
    }

    public String getIsSignPerson() {
        return isSignPerson;
    }

    public void setIsSignPerson(String isSignPerson) {
        this.isSignPerson = isSignPerson;
    }

    public String getIsMailInvoiceContact() {
        return isMailInvoiceContact;
    }

    public void setIsMailInvoiceContact(String isMailInvoiceContact) {
        this.isMailInvoiceContact = isMailInvoiceContact;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    @Override
    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getEmergencyContact() {
        return this.emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact == null ? null : emergencyContact.trim();
    }

    public String getContactAddress() {
        return this.contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress == null ? null : contactAddress.trim();
    }

    @Override
    public String getPositionN() {
        return positionN;
    }

    @Override
    public void setPositionN(String positionN) {
        this.positionN = positionN;
    }

    public String getGender() {
        return gender;
    }

    public String getTitle() {
        return title;
    }

    public String getGenderN() {
        return genderN;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setGenderN(String genderN) {
        this.genderN = genderN;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public Date getIdExpirationDate() {
        return idExpirationDate;
    }

    public void setIdExpirationDate(Date idExpirationDate) {
        this.idExpirationDate = idExpirationDate;
    }
}