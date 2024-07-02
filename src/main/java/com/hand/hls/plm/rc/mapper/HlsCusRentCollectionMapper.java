package com.hand.hls.plm.rc.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.rc.dto.HlsCusRentCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description:租金催收mapper
 * @Author: Wty
 * @Date: Created om 21:38 2018/6/6
 */

public interface HlsCusRentCollectionMapper extends Mapper<HlsCusRentCollection> {
    List<HlsCusRentCollection> queryAll(HlsCusRentCollection rentCollection);

    List<Map> getAdvancedRemindList();

    List<Map> getAdvancedRemindMaliList(Map map);

    /**
     * 更新催收状态
     * @param procInstId
     * @return
     */
    int updateCollectionStatus(@Param("procInstId") String procInstId, @Param("collectionStatus") String collectionStatus);
}