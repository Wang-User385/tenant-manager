package com.hand.hls.cap.controllers;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hls.cap.dto.HlsCusCapAccountMonthlyBalance;
import com.hand.hls.cap.dto.HlsCusCapPkg;
import com.hand.hls.cap.mapper.HlsCusCapAccountMonthlyBalanceMapper;
import com.hand.hls.cap.service.HlsCusCapAccountMonthlyBalanceService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

@Controller
public class HlsCusCapAccountMonthlyBalanceController extends BaseController {

    @Autowired
    private HlsCusCapAccountMonthlyBalanceService service;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private HlsCusCapAccountMonthlyBalanceMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 资金账户历史月结记录
     */
    @SuppressWarnings("ALL")
//    @RequestMapping(value = "/cap/account/monthly/balance/history/query")
//    @ResponseBody
//    public ResponseData queryHistory(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        List<HlsCusCapAccountMonthlyBalance> list = new ArrayList<>();
//        list = service.select(requestContext, dto, page, pageSize);
//        list.sort(Comparator.comparing(HlsCusCapAccountMonthlyBalance::getBalanceDate));
//        return new ResponseData(list);
//    }
//
//    /**
//     * 资金账户月结
//     */
//    @RequestMapping(value = "/cap/account/monthly/balance/query")
//    @ResponseBody
//    public ResponseData query(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) throws ParseException {
//        IRequest requestContext = createRequestContext(request);
//        List<HlsCusCapAccountMonthlyBalance> list = new ArrayList<>();
//        list.add(service.queryBalanceInfo(requestContext, dto));
//        return new ResponseData(list);
//    }
//
//    /**
//     * 资金账户历史月结记录
//     */
//    @RequestMapping(value = "/cap/account/status/query")
//    @ResponseBody
//    public ResponseData queryAccount(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws ParseException {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.capAccountQuery(requestContext, dto, page, pageSize));
//    }
//
//    @RequestMapping(value = "/cap/account/flow/detail/query")
//    @ResponseBody
//    public ResponseData flowQuery(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        List<HlsCusCapAccountMonthlyBalance> list = service.accountFlowQuery(requestContext, dto, page, pageSize);
//        list.sort(Comparator.comparing(HlsCusCapAccountMonthlyBalance::getTransactionDate));
//        return new ResponseData(list);
//    }

    /**
     * 月结保存
     */
    @RequestMapping(value = "/cap/account/monthly/balance/save")
    @ResponseBody
    public HlsCusCapPkg update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusCapPkg pkg = param.toJavaObject(HlsCusCapPkg.class);
        if (pkg.getHlsCusCapAccountMonthlyBalance().getBalanceDate() != null) {
            try {
                service.dayBalanceSave(requestCtx, pkg);
                pkg.setSuccess(true);
            } catch (Exception e) {
                pkg.setSuccess(false);
                pkg.setMessage(e.getMessage());
            }
        }
        return pkg;
    }

//    @RequestMapping(value = "/cap/account/monthly/balance/accountBalanceInfoQuery")
//    @ResponseBody
//    public ResponseData accountInfoQuery(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws ParseException {
//        PageHelper.startPage(page, pageSize);
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(this.mapper.accountBalanceInfoQuery(dto));
//    }

    @RequestMapping(value = "/cap/account/monthly/balance/accountBalanceGroupByBank")
    @ResponseBody
    public ResponseData accountBalanceGroupByBank(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws ParseException {
        PageHelper.startPage(page, pageSize);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(this.mapper.accountBalanceGroupByBank(dto));
    }

    /**
     * 月结
     */
    @RequestMapping(value = "/cap/account/monthly/balance/confirm")
    @ResponseBody
    public HlsCusCapPkg confirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusCapPkg pkg = param.toJavaObject(HlsCusCapPkg.class);
        List<HlsCusCapAccountMonthlyBalance> list = pkg.getHlsCusCapAccountMonthlyBalanceList();
        for (HlsCusCapAccountMonthlyBalance balance:list) {
            balance = service.queryByBalanceDateAndAccount(requestCtx, balance);
            if (balance == null) {
                throw new IllegalArgumentException("请先保存，再进行月结！");
            }
            service.monthConfirm(requestCtx, balance);
        }
        return pkg;
    }

//    /**
//     * @Description:资金账户综合管理首页Chart查询
//     * @Author: Cyy
//     * @Date: Created on 11:11 2018/12/29
//     */
//    @RequestMapping(value = "/cap/account/amount/chart/query")
//    @ResponseBody
//    public ResponseData chartQuery(HttpServletRequest request,
//                                   HlsCusCapAccountMonthlyBalance dto) {
//        IRequest iRequest = createRequestContext(request);
//        return new ResponseData(service.selectAccountBalanceChart(iRequest, dto));
//    }
//
//
//    @RequestMapping(value = "/cap/account/monthly/balance/remove")
//    @ResponseBody
//    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusCapAccountMonthlyBalance> dto) {
//        service.batchDelete(dto);
//        return new ResponseData();
//    }

    /**
     * 首页资金帐户收支预测
     */
    @RequestMapping(value = "/cap/bank/predict/query")
    @ResponseBody
    public ResponseData queryBankPredict(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws ParseException {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectBankAccountPredictedAmount(requestContext, dto, page, pageSize));
    }

    /**
     * 首页未确认帐户收支预测
     */
    @RequestMapping(value = "/cap/predict/query")
    @ResponseBody
    public ResponseData queryAllPredictAccount(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectNotConfirmAccountPredictedAmount(requestContext, dto));
    }


//    /**
//     * 租赁预测明细
//     */
//    @RequestMapping(value = "/cap/ct/cx/predict/query")
//    @ResponseBody
//    public ResponseData queryCtCash(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.queryCtCxCashDetail(requestContext, dto, page, pageSize));
//    }
//
//    /**
//     * 融资预测明细
//     */
////    @RequestMapping(value = "/cap/lon/predict/query")
////    @ResponseBody
////    public ResponseData queryLonCash(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
////                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
////        IRequest requestContext = createRequestContext(request);
////        return new ResponseData(service.queryLonCashDetail(requestContext, dto, page, pageSize));
////    }
//
//
//    /**
//     * 首页剩余本金--租赁
//     */
//    @RequestMapping(value = "/cap/ct/principle/query")
//    @ResponseBody
//    public List<HlsCusCapAccountMonthlyBalance> queryCtPrinciple(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return service.pressureQueryCt(requestContext, dto);
//    }
//
//    /**
//     * 首页剩余本金--保理
//     */
//    @RequestMapping(value = "/cap/cx/principle/query")
//    @ResponseBody
//    public List<HlsCusCapAccountMonthlyBalance> queryCxPrinciple(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return service.pressureQueryCx(requestContext, dto);
//    }
//
//    /**
//     * 含税应收分析
//     */
//    @RequestMapping(value = "/cap/tax/principle/query")
//    @ResponseBody
//    public List<HlsCusCapAccountMonthlyBalance> queryTaxPrinciple(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return service.pressureQueryTax(requestContext, dto);
//    }
//
//    /**
//     * 融资端未来应付
//     */
//    @RequestMapping(value = "/cap/lon/principle/query")
//    @ResponseBody
//    public List<HlsCusCapAccountMonthlyBalance> queryLonPrinciple(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return service.pressureQueryLon(requestContext, dto);
//    }
//
//    /**
//     * 资金盈缺图表数据
//     */
//    @RequestMapping(value = "/cap/in/out/bar/query")
//    @ResponseBody
//    public ResponseData queryFinanceFlow(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.selectFinanceIncomeAndPayData(requestContext, dto));
//    }
//
//    /**
//     * 压力测试图表数据
//     */
//    @RequestMapping(value = "/cap/pressure/bar/query")
//    @ResponseBody
//    public ResponseData queryPressureFlow(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.selectFinancePressureData(requestContext, dto));
//    }
//
//
//    /**
//     * 租赁保理未来收支分析
//     * @param dto
//     * @param page
//     * @param pageSize
//     * @param request
//     * @return
//     * @throws ParseException
//     */
//    @RequestMapping(value = "/cap/query/amount/byFrequency")
//    @ResponseBody
//    public ResponseData queryAmountByAnyFrequency(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.selectfeatureAmountData(requestContext, dto,page,pageSize));
//    }
//
//
//    @RequestMapping(value = "/cap/account/monthly/balance/accountReceivedAndPaid")
//    @ResponseBody
//    public ResponseData accountReceivedAndPaid(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request) throws ParseException {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.accountReceivedAndPaid(requestContext, dto));
//    }
//
//
//    /**
//     * 租赁保理未来收支分析导出
//     * @param dto
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/cap/feature/amount/export")
//    @ResponseBody
//    public ResponseData exportFeatureAmount(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request, HttpServletResponse response) {
//        service.exportfeatureAmountData(request,response, dto);
//        return new ResponseData();
//    }
//    @RequestMapping(value = "/cap/other/amount/export")
//    @ResponseBody
//    public ResponseData exportOtherPlan(HlsCusCapitalOtherPlan dto, HttpServletRequest request, HttpServletResponse response) {
//        service.exportOtherPlan(request,response, dto);
//        return new ResponseData();
//    }
//
//
//    /**
//     * 融资端未来应付分析
//     * @param dto
//     * @param page
//     * @param pageSize
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/cap/query/finance/byFrequency")
//    @ResponseBody
//    public ResponseData queryFinanceByAnyFrequency(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.selectFinanceFeatureData(requestContext, dto,page,pageSize));
//    }
//
//
//    /**
//     * 融资端未来应付分析导出
//     * @param dto
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/cap/feature/finance/export")
//    @ResponseBody
//    public ResponseData exportFeatureFinance(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request, HttpServletResponse response) {
//        service.exportfeatureFinanceData(request,response, dto);
//        return new ResponseData();
//    }
//
//
//
//    /**
//     * 租赁保理未来收支分析 客户系数为0 或 客户系数不为0
//     * @param dto
//     * @param page
//     * @param pageSize
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/cap/query/fator/byFrequency")
//    @ResponseBody
//    public ResponseData queryMasterFactorByAnyFrequency(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.selectFeatureBpFatorData(requestContext, dto,page,pageSize));
//    }
//
//
//    /**
//     * 租赁保理未来收支分析 客户系数为0 或 客户系数不为0 导出
//     * @param dto
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/cap/feature/fator/export")
//    @ResponseBody
//    public ResponseData exportFeatureFator(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request, HttpServletResponse response) {
//        service.exportfeatureFactorData(request,response, dto);
//        return new ResponseData();
//    }
//
//
//    /**
//     * 租赁保理未来收支分析导出
//     * @param dto
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/cap/feature/pressure/export")
//    @ResponseBody
//    public ResponseData exportPressureReport(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request, HttpServletResponse response) {
//        IRequest requestContext = createRequestContext(request);
//        service.exportFinancePressureData(requestContext,request,response, dto);
//        return new ResponseData();
//    }
//
//    @RequestMapping(value = "/cap/monthly/otherAmount/description/update")
//    @ResponseBody
//    public ResponseData updateOtherAmountDesc(@RequestBody List<HlsCusCapAccountMonthlyBalance> list, HttpServletRequest request, HttpServletResponse response){
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.batchUpdate(requestContext,list));
//    }
//
//
//    /**
//     * 首页资金帐户收支预测 导出
//     * @param dto
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/cap/bank/predict/export")
//    @ResponseBody
//    public ResponseData exportBankPredict(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request, HttpServletResponse response) {
//        IRequest requestContext = createRequestContext(request);
//        service.exportBankPredictData(requestContext,request,response, dto);
//        return new ResponseData();
//    }


    /**
     * 资金帐户收支预测 按账户
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/cap/account/predict/query")
    @ResponseBody
    public ResponseData queryAccountPredict(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws ParseException {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAccountPredictedAmount(requestContext, dto, page, pageSize));
    }


//    /**
//     * 资金帐户收支预测 按账户 导出
//     * @param request
//     * @param config
//     * @param httpServletResponse
//     */
//    @RequestMapping(value = "/cap/account/predict/export")
//    public void exportAccountPredict(HttpServletRequest request, @RequestParam String config,
//                                   HttpServletResponse httpServletResponse) {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusCapAccountMonthlyBalance.class, ColumnInfo.class);
//            ExportConfig<HlsCusCapAccountMonthlyBalance, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            service.exportAccountPredictData(requestContext,request, httpServletResponse, exportConfig.getParam());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 租赁保理收支预测明细
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/cap/fctcon/predict/query")
    @ResponseBody
    public ResponseData queryFctConPredit(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFctConContractPredictedAmount(requestContext, dto, page, pageSize));
    }

//
//    /**
//     * 租赁保理收支预测明细 导出
//     * @param request
//     * @param config
//     * @param httpServletResponse
//     */
//    @RequestMapping(value = "/cap/fctcon/predict/export")
//    public void exportFctconPredict(HttpServletRequest request, @RequestParam String config,
//                                     HttpServletResponse httpServletResponse) {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusCapAccountMonthlyBalance.class, ColumnInfo.class);
//            ExportConfig<HlsCusCapAccountMonthlyBalance, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            service.exportFctConPredictData(requestContext,request, httpServletResponse, exportConfig.getParam());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 融资收支预测明细
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/cap/lon/predict/query")
    @ResponseBody
    public ResponseData queryLonPredit(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectLonFinancePredictedAmount(requestContext, dto, page, pageSize));
    }


//    /**
//     * 融资收支预测明细 导出
//     * @param request
//     * @param config
//     * @param httpServletResponse
//     */
//    @RequestMapping(value = "/cap/lon/predict/export")
//    public void exportLonPredict(HttpServletRequest request, @RequestParam String config,
//                                    HttpServletResponse httpServletResponse) {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusCapAccountMonthlyBalance.class, ColumnInfo.class);
//            ExportConfig<HlsCusCapAccountMonthlyBalance, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            service.exportLonPredictData(requestContext,request, httpServletResponse, exportConfig.getParam());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 资金调拨收支预测明细
//     * @param dto
//     * @param page
//     * @param pageSize
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/cap/transfer/predict/query")
//    @ResponseBody
//    public ResponseData queryTransferPredit(HlsCusCapAccountMonthlyBalance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.selectTransferPredictedAmount(requestContext, dto, page, pageSize));
//    }
//
//
//    /**
//     * 资金调拨收支预测明细 导出
//     * @param request
//     * @param config
//     * @param httpServletResponse
//     */
//    @RequestMapping(value = "/cap/transfer/predict/export")
//    public void exportTransferPredict(HttpServletRequest request, @RequestParam String config,
//                                 HttpServletResponse httpServletResponse) {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusCapAccountMonthlyBalance.class, ColumnInfo.class);
//            ExportConfig<HlsCusCapAccountMonthlyBalance, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            service.exportTransferPredictData(requestContext,request, httpServletResponse, exportConfig.getParam());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 首页资金帐户收支预测 导出
//     * @param dto
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/cap/account/balance/export")
//    @ResponseBody
//    public ResponseData exportAccountBalance(HlsCusCapAccountMonthlyBalance dto, HttpServletRequest request, HttpServletResponse response) {
//        IRequest requestContext = createRequestContext(request);
//        service.exportAccountBalanceData(requestContext,request,response, dto);
//        return new ResponseData();
//    }
}
