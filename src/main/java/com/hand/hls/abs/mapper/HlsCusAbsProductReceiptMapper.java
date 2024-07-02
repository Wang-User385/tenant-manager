package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductReceipt;

import java.util.List;

public interface HlsCusAbsProductReceiptMapper extends Mapper<HlsCusAbsProductReceipt> {


   List<HlsCusAbsProductReceipt> selectProductReceiptData(HlsCusAbsProductReceipt productReceipt);

}