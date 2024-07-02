package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusCtDocumentFinIncome;
import com.hand.hls.gld.dto.Period;
import com.hand.hls.vat.exception.AcpInvoiceException;
import uncertain.composite.CompositeMap;

import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author: WuJun
 * @Date: 2020/02/07
 * @Time: 18:00
 */
public interface IContractFinanceIncomeService extends IBaseService<HlsCusContractFinanceIncome>, ProxySelf<IContractFinanceIncomeService>{
    /**
     * fetch data by rule id
     *
     * @param iRequest IRequest
     * @param hlsCusContractFinanceIncome HlsCusContractFinanceIncome
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @return Result<queryResult>
     */
    List<HlsCusContractFinanceIncome> queryContractFinanceIncome(IRequest iRequest, HlsCusContractFinanceIncome hlsCusContractFinanceIncome,int pageNum, int pageSize);

    /**
     * fetch data by rule id
     *
     * @param var1 request
     * @param var2 Period
     * @param var3 var3
     * @param var4 var4
     * @return Result<period_name query>
     */
    List<Period> periodNameQueryForComb(IRequest var1, Period var2, int var3, int var4);

    /**
     * selectDefaultPeriodName
     *
     * @param compositeMap compositeMap
     * @param whereStr whereStr
     * @return Result<default value>
     */
    List<CompositeMap> selectDefaultPeriodName(CompositeMap compositeMap,String whereStr);

    /**
     * queryConFinanceIncomeByKey
     *
     * @param iRequest IRequest
     * @param hlsCusContractFinanceIncome HlsCusContractFinanceIncome
     * @return Result<select value by key>
     */
    HlsCusContractFinanceIncome queryConFinanceIncomeByKey(IRequest iRequest,HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

    /**
     * fetch data by rule id
     *
     * @param iRequest IRequest
     * @param hlsCusCtDocumentFinIncome HlsCusCtDocumentFinIncome
     * @return Result<queryResult>
     */
    List<HlsCusCtDocumentFinIncome> queryCtDocumentFinIncomeByCondition(IRequest iRequest,HlsCusCtDocumentFinIncome hlsCusCtDocumentFinIncome,int pageNum, int pageSize);

    /**
     * @Title: financeIncomeSharing
     * @Discription: 收益分摊入口
     * @Param: [iRequest, list]
     * @Return: void
     */
    void financeIncomeSharing(IRequest iRequest , List<HlsCusConContract> list) throws Exception;

    List<HlsCusContractFinanceIncome> reportQuery(IRequest iRequest , HlsCusContractFinanceIncome contractFinanceIncome,int pagenum,int pagesize);

    List<HlsCusContractFinanceIncome> selectFeeByCfItem(IRequest iRequest , HlsCusContractFinanceIncome contractFinanceIncome,int pagenum,int pagesize);

    List<HlsCusContractFinanceIncome> selectFeeByCfItemForPreLeaseInterest(IRequest iRequest , HlsCusContractFinanceIncome contractFinanceIncome,int pagenum,int pagesize);

    List<HlsCusContractFinanceIncome> selectFeeByCfItemForInterest(IRequest iRequest , HlsCusContractFinanceIncome contractFinanceIncome,int pagenum,int pagesize);

    List<HlsCusConContract> selectStampDuty(HlsCusConContract hlsCusConContract);

    List<HlsCusContractFinanceIncome> selectImportTempList(Long var1,Long var2) throws ParseException;

    void importConfirm(IRequest var1, List<HlsCusContractFinanceIncome> var2, Long contractId);

    List<HlsCusContractFinanceIncome> selectFinanceIncome(IRequest iRequest , HlsCusContractFinanceIncome contractFinanceIncome,int pagenum,int pagesize);

}