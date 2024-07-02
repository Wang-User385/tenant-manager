package com.hand.hls.ast.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ast.dto.AssetClassHead;
import org.apache.ibatis.annotations.Param;

public interface AssetClassHeadMapper extends Mapper<AssetClassHead> {

    Long queryMaxBatchNumber();


    AssetClassHead queryClassHeadInfoByHeadId(Long classHeadId);

    AssetClassHead queryId(@Param("bpId") Long bpId, @Param("classYear") String classYear, @Param("classSeason") String classSeason) throws NullPointerException;

}