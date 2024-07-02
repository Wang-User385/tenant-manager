package com.hand.hls.prj.service;

import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import hls.core.utils.exception.HlsCusException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjQuotationService extends IBaseService<HlsCusPrjQuotation>, ProxySelf<HlsCusPrjQuotationService> {

    HlsCusPrjQuotation prjQuotationSave(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation);

    List<HlsCusPrjQuotation> queryPaymentTableInfo(IRequest iRequest, HlsCusPrjQuotation prjQuotation, int page, int pageSize);

    List<HlsCusPrjQuotation> queryPaymentTableInfoLov(IRequest iRequest, HlsCusPrjQuotation prjQuotation, int page, int pageSize);

    List<HlsCusPrjQuotation> paymentTableSubmit(IRequest iRequest, List<HlsCusPrjQuotation> hlsCusPrjQuotationList);

    List<HlsCusPrjQuotation> queryCshFineInfo(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryPaymentTableInfoConfirm(IRequest iRequest, HlsCusPrjQuotation prjQuotation, int page, int pageSize);

    List<HlsCusPrjQuotation> queryPrjQuotationInfo(HlsCusPrjQuotation prjQuotation);

    /**
     * 投放计划制作查询项目报价信息
     * @param prjQuotation
     * @return
     */
    List<HlsCusPrjQuotation> queryPrjQuotationInfoForContractPlan(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryQuotationInfoList(HlsCusPrjQuotation prjQuotation);

    void updateQuotaion(HlsCusPrjQuotation quotation, List<Map> maps, HlsCalcConfig hlsCalcConfig,Boolean calcFlag) throws Exception;

    void quotationReCalc(IRequest request,Long quotationId,Boolean calcFlag) throws Exception;
    void quotationReCalcBefore(IRequest request,Long quotationId,Boolean calcFlag) throws Exception;
    List<HlsCusPrjQuotation> queryQuotationInfo(HlsCusPrjQuotation prjQuotation);

    void quotationChangeReCalc(IRequest request, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws Exception;

    Double doubleDataTran(Double var);

    void updateXirr(IRequest request, HlsCusPrjQuotation prjQuotation) throws IllegalArgumentException;


    HlsCusPrjQuotation prjQuotationCreateNew(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation);

    HlsCusPrjQuotation quotationQuote(IRequest iRequest,HlsCusPrjQuotation hlsCusPrjQuotation,Long projectId,Long quotationId) throws HlsCusException;

    List<HlsCusPrjQuotation> queryPrjQuotationByProjectId(HlsCusPrjProject hlsCusPrjProject);

    void saveExcel(IRequest iRequest,HlsCusPrjQuotation quotation) throws HlsCusException;

    Double selectLprBaseRateByDate(IRequest iRequest,HlsCusPrjQuotation quotation) throws HlsCusException;

    void savePrjQuotationRecord(IRequest iRequest, HlsCusPrjQuotation quotation) throws Exception;

    /**
     * 保存报价头表 APP接口
     * @param iRequest
     * @param quotation
     * @return
     * @throws Exception
     */
    HlsCusPrjQuotation savePrjQuotationForApp(IRequest iRequest, HlsCusPrjQuotation quotation) throws Exception;

    List<Map> selectQuotationChangeInfo(IRequest iRequest, HlsCusPrjQuotation quotation,int pagenum, int pagesize);

    List<HlsCusPrjQuotation> queryQuotationRateReq(IRequest var1, HlsCusPrjQuotation var2, int pagenum, int pagesize);
    List<HlsCusPrjQuotation> queryQuotationRateReqNew(IRequest var1, HlsCusPrjQuotation var2, int pagenum, int pagesize);
    List<HlsCusPrjQuotation> queryQuotationFloatingLnReq(IRequest var1, HlsCusPrjQuotation var2, int pagenum, int pagesize);
    List<HlsCusPrjQuotation> queryQuotationFloatingLnReqNew(IRequest var1, HlsCusPrjQuotation var2, int pagenum, int pagesize);
    List<HlsCusPrjQuotation> queryQuotationFloatingLnReqWfl(IRequest var1, HlsCusPrjQuotation var2, int pagenum, int pagesize);
    List<HlsCusPrjQuotation> queryFloatingInterest(IRequest iRequest, HlsCusPrjQuotation var2, int pagenum, int pagesize);

    List<HlsCusPrjQuotation> queryTotalFloatingInterest(IRequest iRequest, HlsCusPrjQuotation var2, int pagenum, int pagesize);

    void writeBack(XSSFWorkbook wb, JSONArray array, String priceList);

    void saveCalcFront(IRequest iRequest,HlsCusPrjQuotation quotation, String sheets) throws Exception;

    String updateHistoryQuotationXirr(IRequest iRequest,HlsCusPrjQuotation quotation);

    ResponseData queryBaseRateNow(CompositeMap var1, String var2);

    void updateIrr(IRequest request, HlsCusPrjQuotation prjQuotation) throws IllegalArgumentException;

}