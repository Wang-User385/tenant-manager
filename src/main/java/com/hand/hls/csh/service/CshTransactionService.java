package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.utils.ResMessageException;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Auther: Eugene Song
 * @Date: 2019年2月19日
 * @Description:
 */
public interface CshTransactionService extends IBaseService<HlsCusCshTransaction>, ProxySelf<CshTransactionService> {
    int SCALE = 2;

    /**
     * 收款明细查询
     */
    List<Map> collectionDetails(IRequest var1, HlsCusCshTransaction var2, int var3, int var4);

    List<Map> CashThingTransactionQueryDetail(Map var1);

    void updateCshTrByPrimaryKey(HlsCusCshTransaction var1);

    List<HlsCusCshTransaction> queryDetailByIdList(Long var1);


    List<HlsCusCshTransaction> queryCshTransaction(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize,String sortName,String sortOrder);
    HlsCusCshTransaction createCshTransaction (IRequest requestCtx,HlsCusCshTransaction hlsCusCshTransaction);
    List<HlsCusCshTransaction> postCshTransaction (IRequest requestCtx,List<HlsCusCshTransaction> hlsCusCshTransactionList) throws ResMessageException;
    List<HlsCusCshTransaction> reverseCshTransaction (IRequest requestCtx,List<HlsCusCshTransaction> hlsCusCshTransactionList) throws ResMessageException;

    void refundCshTransactionSubmit (IRequest requestCtx, HttpSession session, List<HlsCusCshTransaction> hlsCusCshTransactionList) throws BeyondAmountLimitException, ResMessageException;

    List<HlsCusCshTransactionRefund> refundCshTransaction (IRequest requestCtx,List<HlsCusCshTransactionRefund> cshTransactionRefundList) throws BeyondAmountLimitException, ResMessageException;

    void updateReturn(IRequest var1, Long var2, HlsCusCshTransaction var3) throws BeyondAmountLimitException;



    List<HlsCusCshTransaction> detailQuery(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize,String sortName,String sortOrder);
    List<HlsCusCshTransaction> detailQueryNew(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize,String sortName,String sortOrder);
    List<HlsCusCshTransaction> detailQueryNewBusiness(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize,String sortName,String sortOrder);
    List<HlsCusCshTransaction> detailQueryNewFinance(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize,String sortName,String sortOrder);

    List<HlsCusCshTransaction> detailQueryAdvance(IRequest var1, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize,String sortName,String sortOrder);

    void receiptImport(IRequest iRequest, Long hdId,String templateCode,Long readLine) throws ExcelException, SQLException, ParseException;

    void cashflowImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException;


    List<HlsCusCshTransaction> searchCshTransation(IRequest iRequest,HlsCusCshTransaction transaction,int page, int pagesize);
    List<HlsCusCshTransaction> queryCshTransactionNew(IRequest iRequest,HlsCusCshTransaction transaction,int page, int pagesize);
    /**
     * 金额冻结
     */
    void blockAmount(IRequest iRequest, long transactionId, double amount);

    /**
     * 二期功能：
     * 现金事务lov查询业务接口
     * 可根据事务编号模糊查询
     */
    List<HlsCusCshTransaction> queryLov(IRequest iRequest, HlsCusCshTransaction hlsCusCshTransaction, int page, int pageSize) throws ParseException;

    List<HlsCusCshTransaction> queryDepositDeductMethod(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction, int pagenum, int pagesize);

    void updateDepositDeductMethod(IRequest requestCtx, List<HlsCusCshTransaction> transactionList) throws ResMessageException;

    /**
     * 金额释放
     */
    void releaseAmount(IRequest iRequest, long transactionId, double amount);

    /**
     * 金额释放
     */
    void releaseAmountNewTransaction(IRequest iRequest, long transactionId, double amount);

    void transactionImport(IRequest iRequest, Long headerId);
}
