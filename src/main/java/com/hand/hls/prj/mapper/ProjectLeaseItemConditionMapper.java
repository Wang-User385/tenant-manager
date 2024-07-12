package com.hand.hls.prj.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.PrjProjectLeaseItemCondition;
import com.hand.hls.prj.dto.PrjProjectLeaseItemMortgage;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * Created by lipan on 2024/7/10.
 */
public interface ProjectLeaseItemConditionMapper extends Mapper<PrjProjectLeaseItemCondition> {

    List<PrjProjectLeaseItemCondition> prjProjectLeaseItemConditionQuery(@Param("projectLeaseItemId") Long projectLeaseItemId);


}
