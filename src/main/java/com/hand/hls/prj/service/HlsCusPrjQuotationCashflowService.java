package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.HlsCusImportInterface;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

public interface HlsCusPrjQuotationCashflowService extends IBaseService<HlsCusPrjQuotationCashflow>, ProxySelf<HlsCusPrjQuotationCashflowService>, HlsCusImportInterface {

    List<HlsCusPrjQuotationCashflow> saveCalc2PrjQuotationCashflow(IRequest requestContext, HlsCusPrjQuotation prjQuotation) throws  Exception;

    /**
     * 计算手续费现金流
     * @param iRequest
     * @param prjQuotation
     * @return
     * @throws Exception
     */
    List<HlsCusPrjQuotationCashflow> calculateLeaseChargeCashflow(IRequest iRequest, HlsCusPrjQuotation prjQuotation) throws  Exception;

    List<HlsCusPrjQuotationCashflow> queryPrjQuotationCashflowByProjectId(HlsCusPrjQuotationCashflow prjQuotationCashflow, int page, int pagesize);

    List<HlsCusPrjQuotationCashflow> queryCshFineInfo2(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryCshFineInfo(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryFineCshFlowInfo(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> fineCshSubmit(IRequest requestContext, List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList);

    List<HlsCusPrjQuotationCashflow> queryPrjQuotationOutCashflowByProjectId(HlsCusPrjQuotationCashflow prjQuotationCashflow, int page, int pagesize);

    List<HlsCusPrjQuotationCashflow> prjQueryCashFlow(HlsCusPrjQuotationCashflow prjQuotationCashflow, int page, int pagesize);

    double Newtons_method(double guess, double[] payments, Date[] days);

    void saveLeaseChargeCashflow(IRequest iRequest, List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowLists) throws HlsCusException;

    void exportPrjQuotationCashflow(HttpServletRequest request, HttpServletResponse response, HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow) throws IOException, InvocationTargetException, IllegalAccessException;

    void exportPrjCashFlow(HttpServletRequest request, HttpServletResponse response, HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow) throws IOException, InvocationTargetException, IllegalAccessException;

    public void calcIrrAndXirr(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws IllegalArgumentException, HlsCusException;


    void calcNewCashflow(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws IllegalArgumentException, HlsCusException;


    void cashSkipWorkday(IRequest requestContext, List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList);

    void taxCashflowDemolition(IRequest requestContext, HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotationCashflow> queryQuotationCashFlowById(HlsCusPrjQuotation hlsCusPrjQuotation);

    int deleteQuotationCashFlowById(HlsCusPrjQuotation hlsCusPrjQuotation);

    void saveFeeCashflow(IRequest iRequest, List<HlsCusPrjQuotationCashflow> cusPrjQuotationCashflowList) throws hls.core.utils.exception.HlsCusException, HlsCusException;

    void savePaynote(IRequest iRequest,List<HlsCusPrjQuotationCashflow> cashflowList) throws HlsCusException;

    void saveCashflowFromQuotationCashflow(IRequest iRequest, Long contractId, Long quotationId,String interestAmortizationMethod);
}