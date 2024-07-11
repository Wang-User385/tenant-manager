package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjProjectLeaseItemMapper extends Mapper<HlsCusPrjProjectLeaseItem> {

    void deleteLeaseItemByProjectId(Long projectId);

    void deleteLeaseItemByProjectId1(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);

    List<HlsCusPrjProjectLeaseItem> queryPrjProjectLeaseItem(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);
    List<HlsCusPrjProjectLeaseItem> queryPrjProjectLeaseItem1(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);

    List<HlsCusPrjProjectLeaseItem> queryPrjProjectLeaseItemChangeBefore(/*HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem*/);
    List<HlsCusPrjProjectLeaseItem> queryPrjProjectLeaseItemChangeBefore2(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);
    List<HlsCusPrjProjectLeaseItem> queryPrjProjectLeaseItemChangeBefore1(/*HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem*/);

    //通过供应商查询合同ID
    List<HlsCusPrjProjectLeaseItem> queryPrjProjectLeaseItemByBpName(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);

    //查找系统代码的值
    String getValueSysCode(@Param("code") String code,@Param("meaning") String meaning);

    List<HlsCusPrjProjectLeaseItem> inspectionPlaneInfoQuery(Map<String, Object> params);

    List<HlsCusPrjProjectLeaseItem> queryLeaseItemByOccupiedVirCon(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);

    List<HlsCusPrjProjectLeaseItem> wxinQueryConProjectLease(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);

    List<HlsCusPrjProjectLeaseItem> queryLeaseItemForExcelUpdate(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);

    List<HlsCusPrjProjectLeaseItem> queryMortgagePledgeForExcelUpdate(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);



    List<HlsCusPrjProjectLeaseItem> wxinQueryLeaseChange(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);

    List<HlsCusPrjProjectLeaseItem> selectLeaseItemByProjectId(Long projectId);
}
