package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.JeHead;
import com.hand.hls.gld.dto.JeTrxDtl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JeTrxDtlMapper extends Mapper<JeTrxDtl>{
    List<JeTrxDtl> queryJeTrxDtlStatus();
    List<JeTrxDtl> waitGenerateList(JeTrxDtl dto);
    List<JeTrxDtl> gld320aJeTrxDtlQuery(JeTrxDtl jeTrxDtl);
    List<JeTrxDtl> selectTrxDtlByJeHeader(@Param("jeHeadList") List<JeHead> jeHeadList);

    List<JeTrxDtl> queryNewByContract(JeTrxDtl  jeTrxDtl);
}