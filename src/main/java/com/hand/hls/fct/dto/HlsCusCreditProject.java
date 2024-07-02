package com.hand.hls.fct.dto;

import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;

import java.util.List;

/**
 * title:授信立项
 * description:授信立项模块
 * author: LiuTengfei
 * date:2018/10/12-13:13
 */

public class HlsCusCreditProject extends BaseDTO{
    /**
     * 授信立项
     */
    private HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance;
    /**
     * 授信保证信息
     */
    private List<HlsCusHlsCreditChanceGuarantor> hlsCusHlsCreditChanceGuarantors;
    /**
     * 授信抵押信息
     */
    private List<HlsCusHlsCreditChanceMortgage> hlsCusHlsCreditChanceMortgages;
    /**
     * 授信质押信息
     */
    private List<HlsCusHlsCreditChancePledge> hlsCusHlsCreditChancePledges;

    public HlsCusHlsCreditLineChance getHlsCusHlsCreditLineChance() {
        return hlsCusHlsCreditLineChance;
    }

    public void setHlsCusHlsCreditLineChance(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance) {
        this.hlsCusHlsCreditLineChance = hlsCusHlsCreditLineChance;
    }

    public List<HlsCusHlsCreditChanceGuarantor> getHlsCusHlsCreditChanceGuarantors() {
        return hlsCusHlsCreditChanceGuarantors;
    }

    public void setHlsCusHlsCreditChanceGuarantors(List<HlsCusHlsCreditChanceGuarantor> hlsCusHlsCreditChanceGuarantors) {
        this.hlsCusHlsCreditChanceGuarantors = hlsCusHlsCreditChanceGuarantors;
    }

    public List<HlsCusHlsCreditChanceMortgage> getHlsCusHlsCreditChanceMortgages() {
        return hlsCusHlsCreditChanceMortgages;
    }

    public void setHlsCusHlsCreditChanceMortgages(List<HlsCusHlsCreditChanceMortgage> hlsCusHlsCreditChanceMortgages) {
        this.hlsCusHlsCreditChanceMortgages = hlsCusHlsCreditChanceMortgages;
    }

    public List<HlsCusHlsCreditChancePledge> getHlsCusHlsCreditChancePledges() {
        return hlsCusHlsCreditChancePledges;
    }

    public void setHlsCusHlsCreditChancePledges(List<HlsCusHlsCreditChancePledge> hlsCusHlsCreditChancePledges) {
        this.hlsCusHlsCreditChancePledges = hlsCusHlsCreditChancePledges;
    }

    public HlsCusCreditChanceFinStatement getHlsCusCreditChanceFinStatement() {
        return hlsCusCreditChanceFinStatement;
    }

    public void setHlsCusCreditChanceFinStatement(HlsCusCreditChanceFinStatement hlsCusCreditChanceFinStatement) {
        this.hlsCusCreditChanceFinStatement = hlsCusCreditChanceFinStatement;
    }

    public List<HlsCusPrjProjectAttachment> getHlsCusPrjProjectAttachmentList() {
        return hlsCusPrjProjectAttachmentList;
    }

    public void setHlsCusPrjProjectAttachmentList(List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList) {
        this.hlsCusPrjProjectAttachmentList = hlsCusPrjProjectAttachmentList;
    }

    /**
     * 保理财报信息表
     */
    private HlsCusCreditChanceFinStatement hlsCusCreditChanceFinStatement;

    /**
     * 附件信息
     */
    private List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList;

    /**
     * 客户信息
     */
    private List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps;


    public List<HlsCusHlsCreditLineChanceBp> getHlsCusHlsCreditLineChanceBps() {
        return hlsCusHlsCreditLineChanceBps;
    }

    public void setHlsCusHlsCreditLineChanceBps(List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps) {
        this.hlsCusHlsCreditLineChanceBps = hlsCusHlsCreditLineChanceBps;
    }

    private List<HlsCusHlsCreditLineChanceAttach> hlsCusHlsCreditLineChanceAttaches;

    public List<HlsCusHlsCreditLineChanceAttach> getHlsCusHlsCreditLineChanceAttaches() {
        return hlsCusHlsCreditLineChanceAttaches;
    }

    public void setHlsCusHlsCreditLineChanceAttaches(List<HlsCusHlsCreditLineChanceAttach> hlsCusHlsCreditLineChanceAttaches) {
        this.hlsCusHlsCreditLineChanceAttaches = hlsCusHlsCreditLineChanceAttaches;
    }


}
