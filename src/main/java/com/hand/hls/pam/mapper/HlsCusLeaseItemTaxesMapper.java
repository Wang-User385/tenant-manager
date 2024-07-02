package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.pam.dto.HlsCusLeaseItemTaxes;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/16 - 10:50
 */
public interface HlsCusLeaseItemTaxesMapper extends Mapper<HlsCusLeaseItemTaxes> {


    List<HlsCusLeaseItemTaxes> selectByLeaseForeignKey(HlsCusLeaseItemTaxes hlsCusLeaseItemTaxes);

}
