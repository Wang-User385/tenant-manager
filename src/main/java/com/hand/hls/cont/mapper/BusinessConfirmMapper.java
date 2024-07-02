package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.BusinessConfirm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusinessConfirmMapper extends Mapper<BusinessConfirm>{

   Long queryNextVersion();

   int batchDel(@Param("batchIds")String batchIds);

   List<BusinessConfirm> query(@Param("batchId") Long batchId);
}