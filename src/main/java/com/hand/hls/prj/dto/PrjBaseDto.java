package com.hand.hls.prj.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: ximufeng
 * @version: v1.0
 * @description: 进件模块头行实体类
 * @date:2019/11/22
 */
@Data
public class PrjBaseDto {
    /**
     * 进件基本信息
     */
    private HlsCusPrjProject prjProject;

    /**
     * 报价信息
     */

    private HlsCusPrjQuotation prjQuotation;

    /**
     * 现金流信息
     */

    private List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList;

    /**
     * 租赁物信息
     */

    private List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList;

    /**
     * 进件商业伙伴信息
     */

    private List<BpMasterBaseDto> bpMasterBaseDtoList;

}
