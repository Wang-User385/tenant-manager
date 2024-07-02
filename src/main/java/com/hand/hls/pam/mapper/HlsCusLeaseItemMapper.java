package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.pam.dto.HlsCusLeaseItem;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/14 - 17:19
 */
public interface HlsCusLeaseItemMapper extends Mapper<HlsCusLeaseItem>  {


    List<HlsCusLeaseItem> selectPamLeaseItem(HlsCusLeaseItem hlsCusLeaseItem);


    /**
     * 租赁物跳转详细页面查询
     * @param hlsCusLeaseItem
     * @return
     */
    List<HlsCusLeaseItem> selectModelByCondition(HlsCusLeaseItem hlsCusLeaseItem);

    List<HlsCusLeaseItem> selectLeaseCollateral(HlsCusLeaseItem hlsCusLeaseItem);

    List<HlsCusLeaseItem> selectLeaseMes();
    List<HlsCusLeaseItem> selectPlgMes();
}
