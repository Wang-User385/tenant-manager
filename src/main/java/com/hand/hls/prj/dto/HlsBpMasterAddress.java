//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.dto;

import java.util.Date;
import javax.persistence.*;

@Table(
    name = "hls_bp_master_address"
)
public class HlsBpMasterAddress {
    @Id
    @GeneratedValue
    @Column(
        name = "ADDRESS_ID"
    )
    private Long addressId;
    @Column(
        name = "ADDRESS_TYPE"
    )
    private String addressType;
    @Transient
    private String addressTypeN;
    @Column(
        name = "BP_ID"
    )
    private Long bpId;
    @Column(
        name = "COUNTRY_ID"
    )
    private Long countryId;
    @Transient
    private String countryIdN;
    @Column(
        name = "PROVINCE_ID"
    )
    private Long provinceId;
    @Transient
    private String provinceIdN;
    @Column(
        name = "CITY_ID"
    )
    private Long cityId;
    @Transient
    private String cityIdN;
    @Column(
        name = "DISTRICT_ID"
    )
    private Long districtId;
    @Transient
    private String districtIdN;
    @Column(
        name = "STREET"
    )
    private String street;
    @Column(
        name = "ADDRESS"
    )
    private String address;
    @Column(
        name = "ZIPCODE"
    )
    private String zipcode;
    @Column(
        name = "CONTACT_PERSON"
    )
    private String contactPerson;
    @Column(
        name = "POSITION"
    )
    private String position;
    @Column(
        name = "PHONE"
    )
    private String phone;
    @Column(
        name = "PHONE_EXTRA"
    )
    private String phoneExtra;
    @Column(
        name = "FAX"
    )
    private String fax;
    @Column(
        name = "CELL_PHONE"
    )
    private String cellPhone;
    @Column(
        name = "EMAIL"
    )
    private String email;
    @Column(
        name = "CONTACT_PERSON_TEXT"
    )
    private String contactPersonText;
    @Column(
        name = "CONTACT_PERSON_2"
    )
    private String contactPerson2;
    @Column(
        name = "POSITION_2"
    )
    private String position2;
    @Column(
        name = "PHONE_2"
    )
    private String phone2;
    @Column(
        name = "PHONE_EXTRA_2"
    )
    private String phoneExtra2;
    @Column(
        name = "FAX_2"
    )
    private String fax2;
    @Column(
        name = "CELL_PHONE_2"
    )
    private String cellPhone2;
    @Column(
        name = "EMAIL_2"
    )
    private String email2;
    @Column(
        name = "CONTACT_PERSON_TEXT_2"
    )
    private String contactPersonText2;
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

    @Transient
    private String addressDetail;

    public HlsBpMasterAddress() {
        //nothing
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getAddressType() {
        return this.addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getDistrictId() {
        return this.districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getContactPerson() {
        return this.contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneExtra() {
        return this.phoneExtra;
    }

    public void setPhoneExtra(String phoneExtra) {
        this.phoneExtra = phoneExtra;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCellPhone() {
        return this.cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactPersonText() {
        return this.contactPersonText;
    }

    public void setContactPersonText(String contactPersonText) {
        this.contactPersonText = contactPersonText;
    }

    public String getContactPerson2() {
        return this.contactPerson2;
    }

    public void setContactPerson2(String contactPerson2) {
        this.contactPerson2 = contactPerson2;
    }

    public String getPosition2() {
        return this.position2;
    }

    public void setPosition2(String position2) {
        this.position2 = position2;
    }

    public String getPhone2() {
        return this.phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhoneExtra2() {
        return this.phoneExtra2;
    }

    public void setPhoneExtra2(String phoneExtra2) {
        this.phoneExtra2 = phoneExtra2;
    }

    public String getFax2() {
        return this.fax2;
    }

    public void setFax2(String fax2) {
        this.fax2 = fax2;
    }

    public String getCellPhone2() {
        return this.cellPhone2;
    }

    public void setCellPhone2(String cellPhone2) {
        this.cellPhone2 = cellPhone2;
    }

    public String getEmail2() {
        return this.email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getContactPersonText2() {
        return this.contactPersonText2;
    }

    public void setContactPersonText2(String contactPersonText2) {
        this.contactPersonText2 = contactPersonText2;
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

    public String getAddressTypeN() {
        return this.addressTypeN;
    }

    public void setAddressTypeN(String addressTypeN) {
        this.addressTypeN = addressTypeN;
    }

    public String getCountryIdN() {
        return this.countryIdN;
    }

    public void setCountryIdN(String countryIdN) {
        this.countryIdN = countryIdN;
    }

    public String getProvinceIdN() {
        return this.provinceIdN;
    }

    public void setProvinceIdN(String provinceIdN) {
        this.provinceIdN = provinceIdN;
    }

    public String getCityIdN() {
        return this.cityIdN;
    }

    public void setCityIdN(String cityIdN) {
        this.cityIdN = cityIdN;
    }

    public String getDistrictIdN() {
        return this.districtIdN;
    }

    public void setDistrictIdN(String districtIdN) {
        this.districtIdN = districtIdN;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district == null ? null : district.trim();
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getCountryN() {
        return countryN;
    }

    public void setCountryN(String countryN) {
        this.countryN = countryN;
    }

    public String getProvinceN() {
        return provinceN;
    }

    public void setProvinceN(String provinceN) {
        this.provinceN = provinceN;
    }

    public String getCityN() {
        return cityN;
    }

    public void setCityN(String cityN) {
        this.cityN = cityN;
    }

    public String getDistrictN() {
        return districtN;
    }

    public void setDistrictN(String districtN) {
        this.districtN = districtN;
    }
}
