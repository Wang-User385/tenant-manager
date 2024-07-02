package com.hand.hls.prj.dto;


import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.fct.dto.*;

import java.util.List;

/**
 * title:授信立项
 * description:授信立项模块
 * author: LiuTengfei
 * date:2018/10/12-13:13
 */
public class HlsCusCreditInfo extends BaseDTO {

    private HlsCusHlsCreditLine hlsCusHlsCreditLine;//授信评审

    private List<HlsCusHlsCreditLineGuarantor> hlsCreditLineGuarantors;//授信保证信息

    private List<HlsCusHlsCreditLineMortgage> hlsCusHlsCreditLineMortgages;//授信抵押信息

    private List<HlsCusHlsCreditLinePledge> hlsCusHlsCreditLinePledges;//授信质押信息

    private HlsCusFctProjectFinStatement fctProjectFinStatements;//保理财报信息表

    private List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList;//项目附件

    public HlsCusHlsCreditLine getHlsCusHlsCreditLine() {
        return hlsCusHlsCreditLine;
    }

    public void setHlsCusHlsCreditLine(HlsCusHlsCreditLine hlsCusHlsCreditLine) {
        this.hlsCusHlsCreditLine = hlsCusHlsCreditLine;
    }

    public List<HlsCusHlsCreditLineGuarantor> getHlsCreditLineGuarantors() {
        return hlsCreditLineGuarantors;
    }

    public void setHlsCreditLineGuarantors(List<HlsCusHlsCreditLineGuarantor> hlsCreditLineGuarantors) {
        this.hlsCreditLineGuarantors = hlsCreditLineGuarantors;
    }

    public List<HlsCusHlsCreditLineMortgage> getHlsCusHlsCreditLineMortgages() {
        return hlsCusHlsCreditLineMortgages;
    }

    public void setHlsCusHlsCreditLineMortgages(List<HlsCusHlsCreditLineMortgage> hlsCusHlsCreditLineMortgages) {
        this.hlsCusHlsCreditLineMortgages = hlsCusHlsCreditLineMortgages;
    }

    public List<HlsCusHlsCreditLinePledge> getHlsCusHlsCreditLinePledges() {
        return hlsCusHlsCreditLinePledges;
    }

    public void setHlsCusHlsCreditLinePledges(List<HlsCusHlsCreditLinePledge> hlsCusHlsCreditLinePledges) {
        this.hlsCusHlsCreditLinePledges = hlsCusHlsCreditLinePledges;
    }

    public HlsCusFctProjectFinStatement getFctProjectFinStatements() {
        return fctProjectFinStatements;
    }

    public void setFctProjectFinStatements(HlsCusFctProjectFinStatement fctProjectFinStatements) {
        this.fctProjectFinStatements = fctProjectFinStatements;
    }

    public List<HlsCusPrjProjectAttachment> getHlsCusPrjProjectAttachmentList() {
        return hlsCusPrjProjectAttachmentList;
    }

    public void setHlsCusPrjProjectAttachmentList(List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList) {
        this.hlsCusPrjProjectAttachmentList = hlsCusPrjProjectAttachmentList;
    }
}
