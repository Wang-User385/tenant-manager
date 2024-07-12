package com.hand.hls.prj.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.PrjProjectLeaseItemMortgage;
import com.hand.hls.prj.dto.PrjProjectLeaseItemSales;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * Created by lipan on 2024/7/10.
 */
public interface ProjectLeaseItemMortgageMapper extends Mapper<PrjProjectLeaseItemMortgage> {

    List<PrjProjectLeaseItemMortgage> prjProjectLeaseItemMortgageQuery(@Param("projectLeaseItemId") Long projectLeaseItemId);


}
