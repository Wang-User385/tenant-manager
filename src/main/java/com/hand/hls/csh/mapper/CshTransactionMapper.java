package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusCshTransaction;

import java.util.List;

public interface CshTransactionMapper<T> extends Mapper<HlsCusCshTransaction> {
    void updateCshTrByPrimaryKey(HlsCusCshTransaction cshTr);
    List<HlsCusCshTransaction> queryCshTransaction (HlsCusCshTransaction hlsCusCshTransaction);
    List<HlsCusCshTransaction> queryDetailByIdList(Long transactionId);
    HlsCusCshTransaction queryDetailById(Long transactionId);

    List<HlsCusCshTransaction> selectCashflowWriteOffDetail(HlsCusCshTransaction hlsCusCshTransaction);
    List<HlsCusCshTransaction> queryCshTransactionNew (HlsCusCshTransaction hlsCusCshTransaction);

}