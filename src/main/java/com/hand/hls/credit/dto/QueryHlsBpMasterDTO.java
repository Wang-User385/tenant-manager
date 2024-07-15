package com.hand.hls.credit.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 预审商业伙伴表字段返回接收类
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/11 16:44
 */
@Data
public class QueryHlsBpMasterDTO extends BaseDTO {

    /**
     * 客户编号
     */
    private String bpCode;

    /**
     * 客户名称
     */
    private String bpName;

    /**
     * 客户证件类型
     */
    private String idType;

    /**
     * 客户证件号码
     */
    private String idCardNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 证件签发日期
     */
    private Date idIssueDate;

    /**
     * 证件到期日期
     */
    private Date idExpirationDate;

    /**
     * 性别
     */
    private String gender;

    /**
     * 民族
     */
    private String ethnicity;

    /**
     * 出生日期
     */
    private String dateOfBirth;

    /**
     * 性别
     */
    private Long age;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 户籍所属省份
     */
    private String domicileProvince;

    /**
     * 户籍所属市
     */
    private String domicileCity;

    /**
     * 居住地址省
     */
    private String houseProvince;

    /**
     * 居住地址市
     */
    private String houseCity;

    /**
     * 居住地址
     */
    private String houseAddress;

    /**
     * 有无驾照
     */
    private String driverLicenseFlag;

    /**
     * 驾照类型
     */
    private String driverLicenseType;

    /**
     * 驾照截止日期
     */
    private Date driverLicenseDeadline;

    /**
     * 公司所属省份
     */
    private String companyProvince;

    /**
     * 公司所属城市
     */
    private String companyCity;

    /**
     * 户籍所在地（区）
     */
    private String domicileDistrict;
    /**
     * 居住所在地（区）
     */
    private String houseDistrict;
    /**
     * 公司所在地（区）
     */
    private String companyDistrict;
    /**
     *户籍详细地址
     */
    private String domicileAddress;
    /**
     * 是否本地户籍
     */
    private String domicileLocalFlag;


}
