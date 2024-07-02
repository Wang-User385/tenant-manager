package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusWriteOffMatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusWriteOffMatchMapper extends Mapper<HlsCusWriteOffMatch> {

    List<HlsCusWriteOffMatch> selectWriteOffMatch(HlsCusWriteOffMatch hlsCusWriteOffMatch);


    Double selectMatchAmountSum(@Param("cshTransactionId") Long cshTransactionId);

    Double selectMatchAmount(@Param("cshTransactionId") Long cshTransactionId);

    int updateMatchFlag(@Param("cshTransactionId") Long cshTransactionId);

    int selectMatchCount(@Param("cshTransactionId") Long cshTransactionId);

    List<HlsCusWriteOffMatch> selectSumAmount(HlsCusWriteOffMatch hlsCusWriteOffMatch);
}