package com.hand.hls.fct.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HlsCusFctPkg implements Serializable {
    //是否备份报价
    private boolean hasDumpQuotation;
    /**
     * 是否更新已核销金额
     */
    private Boolean writeOffUpdate;
    /**
     * 授信立项表对象
     */
    private HlsCusFctChance hlsCusFctChance;
    /**
     * 项目表对象
     */
    private HlsCusFctProject hlsCusFctProject;
    /**
     * 合同表对象
     */
    private HlsCusFctContract hlsCusFctContract;
    /**
     * 合同结束对象
     */
    private HlsCusFctContractTermination hlsCusFctContractTermination;
    /**
     * 报价方案表对象
     */
    private HlsCusFctProjectQuotation hlsCusFctProjectQuotation;
    /**
     * 商业伙伴信息
     */
    private List<HlsCusFctProjectBp> hlsCusFctProjectBpList;
    /**
     * 保证信息-商业伙伴表
     */
    private List<HlsCusFctProjectBp> guaranteeHlsCusFctProjectBpList;
    /**
     * 抵押信息-商业伙伴表
     */
    private List<HlsCusFctProjectBp> mortgageHlsCusFctProjectBpList;
    /**
     * 质押信息-商业伙伴表
     */
    private List<HlsCusFctProjectBp> pledgeHlsCusFctProjectBpList;
    /**
     * 报价方案行表对象  --还款计划
     */

    public void setHasDumpQuotation(boolean hasDumpQuotation) {
        this.hasDumpQuotation = hasDumpQuotation;
    }
}
