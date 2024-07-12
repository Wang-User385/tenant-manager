package com.hand.hls.prj.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.credit.dto.QueryProjectLeaseItemSalesDTO;
import com.hand.hls.prj.dto.PrjProjectLeaseItemSales;
import com.hand.hls.prj.dto.QuotationSubsection;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * Created by lipan on 2024/7/10.
 */
public interface ProjectLeaseItemSalesMapper extends Mapper<PrjProjectLeaseItemSales> {

    List<PrjProjectLeaseItemSales> prjProjectLeaseItemSalesQuery(@Param("projectLeaseItemId") Long projectLeaseItemId);


    QueryProjectLeaseItemSalesDTO getQueryProjectLeaseItemSalesDTOProjectLeaseItemId(@Param("projectLeaseItemId")Long projectLeaseItemId);
}
