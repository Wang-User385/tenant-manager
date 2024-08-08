package com.hand.hls.prj.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.dto.PrjProjectLeaseItemCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * Created by lipan on 2024/7/10.
 */
public interface ProjectLeaseItemConditionMapper extends Mapper<PrjProjectLeaseItemCondition> {

    List<PrjProjectLeaseItemCondition> prjProjectLeaseItemConditionQuery(@Param("projectLeaseItemId") Long projectLeaseItemId);
    Map prjProjectLeaseItemConditionInfo(HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem);


    List<PrjProjectLeaseItemCondition> prjProjectLeaseItemConditionByLeaseItemId(@Param("projectLeaseItemId") Long projectLeaseItemId);

}
