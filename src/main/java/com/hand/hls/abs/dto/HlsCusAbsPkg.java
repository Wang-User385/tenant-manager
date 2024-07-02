package com.hand.hls.abs.dto;

import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HlsCusAbsPkg implements Serializable {
    private HlsCusAbsProject hlsCusAbsProject;
    private HlsCusAbsProduct hlsCusAbsProduct;
    private List<HlsCusAbsProjectOrganization> hlsCusAbsProjectOrganizations;
    private List<HlsCusAbsPropertyContract> hlsCusAbsPropertyContracts;
    private List<HlsCusAbsAttachment> hlsCusAbsAttachments;
    private List<HlsCusAbsContractFinish> hlsCusAbsContractFinishes;
    private Boolean success;
    private String message;
    private String noticeType;

    /**
     * 账户信息
     */
    private List<HlsCusAbsBankAccount> hlsCusAbsProBankAccounts;

    /**
     * 费用信息
     */
    private List<HlsCusAbsProFeeInfo> hlsCusAbsProFeeInfos;


    /**
     * 附件信息
     */
    private List<HlsCusPrjProjectAttachment> hlsCusAbsProAttachments;


    /**
     * 产品中介机构信息
     */
    private List<HlsCusAbsProductOrganization>  hlsCusAbsProductOrganizations;


    /**
     * 资金用途
     */
    private List<HlsCusAbsProductPurpose>  hlsCusAbsProductPurposes;


    /**
     * 分层结构
     */
    private List<HlsCusAbsProductStructure>  hlsCusAbsProductStructures;


    /**
     * 本息还款计划
     */
    private List<HlsCusAbsProductRepayment>  hlsCusAbsProductRepayments;


    /**
     * 资产包
     */
    private List<HlsCusAbsAssetsPackage>  hlsCusAbsAssetsPackages;

    /**
     * 收款确认
     */
    private List<HlsCusAbsProductReceipt>  hlsCusAbsProductReceipts;


    /**
     * 归集
     */
    private List<HlsCusAbsProductCollection>  hlsCusAbsProductCollections;


    /**
     * 回购
     */
    private List<HlsCusAbsProductBuyback>  hlsCusAbsProductBuybacks;
}

