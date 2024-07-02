package com.hand.hls.abs.dto;

/**
 *
 *
 * @param 资产打包
 * @author yuanyuan 2019-04-01 8:41 PM
 * @return
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable=true)
@Table(name = "CT_ABS_ASSETS_PACK")
@Setter
@Getter
public class HlsCusAbsAssetsPack extends BaseDTO {

    public static final String FIELD_PACK_ID = "packId";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_BASE_DATE = "baseDate";
    @Id
    @GeneratedValue
    private Long packId;

    /**
     * 资产包号
     */
    private String packNumber;

    /**
     * 资产包说明
     */
    @Length(max = 2000)
    private String description;

    /**
     * 封包日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date baseDate;

    @Transient
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String baseDateStr;


    private Date creationDate;


    private String dataClass;


    private Long changeReqId;


    private Long refPackId;


    /**
     * 资产包状态
     */
    private String packStatus;

    /**
     *  业务类型
     */
    @Length(max = 50)
    private String businessType;

    /**
     * 单据类别
     */
    @Length(max = 50)
    private String documentCategory;

    /**
     * 单据类型
     */
    @Length(max = 50)
    private String documentType;

    /**
     * 创建人
     */
    @Transient
    private String creationName;

    /**
     * 资产包状态
     */
    @Transient
    private String occupyStatus;


    /**
     * 占用单据
     */
    @Transient
    private String projectNumber;


    /**
     * 占用主体
     */
    @Transient
    private String projectShortName;



    /**
     * 行表 资产包数据
     */
    @Transient
    private List<HlsCusAbsAssetsPackage> absAssetsPackageList;


    /**
     * 未收租金
     */
    @Transient
    private Double uncollectedDueAmount;

    /**
     * 未收本金
     */
    @Transient
    private Double uncollectedPrincipal;

    /**
     * 未收利息
     */
    @Transient
    private Double uncollectedInterest;

    /**
     * 应收租金
     */
    @Transient
    private Double collectedDueAmount;

    /**
     * 应收本金
     */
    @Transient
    private Double collectedPrincipal;

    /**
     * 应收利息
     */
    @Transient
    private Double collectedInterest;


    /**
     * 入池资产合同个数
     */
    @Transient
    private Long assetContractCount;


    /**
     * 入池资产承租人个数
     */
    @Transient
    private Long assetRentCount;

    private Date baseDateFrom;
    private Date baseDateTo;
    @Transient
    private String occupyStatusN;
    @Transient
    private Double uncollectedDeposit;
    @Transient
    private Double uncollectedCharge;
}
