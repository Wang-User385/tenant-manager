package com.hand.hls.prj.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.PrjProjectLeaseItemMortgage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * Created by lipan on 2024/7/10.
 */
public interface ProjectLeaseItemMortgageMapper extends Mapper<PrjProjectLeaseItemMortgage> {

    List<PrjProjectLeaseItemMortgage> prjProjectLeaseItemMortgageQuery(@Param("projectLeaseItemId") Long projectLeaseItemId);
    Map prjProjectLeaseItemMortgageInfo(PrjProjectLeaseItemMortgage prjProjectLeaseItemMortgage);


    List<PrjProjectLeaseItemMortgage> prjProjectLeaseItemMortgageByLeaseItemId(@Param("projectLeaseItemId") Long projectLeaseItemId);
}
