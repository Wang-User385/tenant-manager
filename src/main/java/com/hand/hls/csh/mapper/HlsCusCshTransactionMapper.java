package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 现金事务mybayis接口层
 */
public interface HlsCusCshTransactionMapper extends CshTransactionMapper<HlsCusCshTransaction> {

    List<Map> collectionDetails(HlsCusCshTransaction hlsCusCshTransaction);

    List<Map> queryByTransactionId(Map map);

    List<HlsCusCshTransaction> selectCshTransactionById(HlsCusCshTransaction hlsCusCshTransaction);

    List<HlsCusCshTransaction> selectCashflowWriteOffDetail(HlsCusCshTransaction hlsCusCshTransaction);

    void updateReversedTrxId(HlsCusCshTransaction hlsCusCshTransaction);

    //List<HlsCusCshTransaction> queryDetailByIdList(Long var1);

    //void updateCshTrByPrimaryKey(HlsCusCshTransaction var1);

    List<HlsCusCshTransaction> detailQuery(HlsCusCshTransaction hlsCusCshTransaction);
    List<HlsCusCshTransaction> detailQueryNew(HlsCusCshTransaction hlsCusCshTransaction);
    List<HlsCusCshTransaction> detailQueryNewBusiness(HlsCusCshTransaction hlsCusCshTransaction);
    List<HlsCusCshTransaction> detailQueryNewFinance(HlsCusCshTransaction hlsCusCshTransaction);
    List<HlsCusCshTransaction> detailQueryAdvance(HlsCusCshTransaction hlsCusCshTransaction);

    List<HlsCusCshTransaction> selectPaymentCshQuery(HlsCusCshTransaction hlsCusCshTransaction);


    List<HlsCusCshTransaction> selectPaymentQueryDetail(HlsCusCshTransaction hlsCusCshTransaction);


    List<HlsCusCshTransaction> searchCshTransation(HlsCusCshTransaction hlsCusCshTransaction);

    List<HlsCusCshTransaction> checkImportBankSlipNum(HlsCusCshTransaction hlsCusCshTransaction);

    /**
     * 二期功能：现金事务lov查询接口 可根据事务编号模糊查询
     * @param hlsCusCshTransaction
     * @return
     */
    List<HlsCusCshTransaction> queryLov(HlsCusCshTransaction hlsCusCshTransaction);

    /**
     * 更型冻结金额
     */
    @Update("update csh_transaction ct set ct.block_amount = nvl(ct.block_amount, 0) + ${amount} where ct.transaction_id = ${transactionId}")
    void updateBlockAmount(@Param("amount") Double amount, @Param("transactionId") Long transactionId);

    /**
     * 查询保证金抵扣流水剔除厂商租赁
     * @param var1
     * @return
     */
    List<HlsCusCshTransaction> queryDeductCshTransaction(HlsCusCshTransaction var1);

    List<HlsCusCshTransaction> queryDepositDeductMethod(HlsCusCshTransaction hlsCusCshTransaction);

    /**
     * 根据代偿租金的合同ID和现金流ID查询出对应的核销事务数据
     * @param contractId
     * @param cashflowId
     * @return
     */
    List<HlsCusCshTransaction> queryTransactionByCashflowId(@Param("contractId") Long contractId, @Param("cashflowId") Long cashflowId);
}
