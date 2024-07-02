package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusLonContractRepaymentController extends BaseController {

    @Autowired
    private HlsCusLonContractRepaymentService service;
    @Autowired
    IExportService excelService;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 本息还款计划
     */
    @RequestMapping(value = "/hlsLon/contract/repayment/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractRepayment dto = param.toJavaObject(HlsCusLonContractRepayment.class);
        return new ResponseData(service.selectLonContractRep(requestContext, dto, pagenum, pageSize, "Rep"));
    }

    /**
     * 融资服务费还款计划
     */
    @RequestMapping(value = "/hlsLon/contract/repayment/query/finance")
    @ResponseBody
    public ResponseData queryFinance(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractRepayment dto = param.toJavaObject(HlsCusLonContractRepayment.class);
        return new ResponseData(service.selectLonContractRep(requestContext, dto, pagenum, pageSize, "Fin"));
    }
//
//    @RequestMapping(value = "/hlsLon/contract/repayment/submit")
//    @ResponseBody
//    public ResponseData update(HttpServletRequest request, @RequestBody List<HlsCusLonContractRepayment> dto) {
//        IRequest requestCtx = createRequestContext(request);
//        return new ResponseData(service.batchUpdate(requestCtx, dto));
//    }
//
//    @RequestMapping(value = "/hlsLon/contract/repayment/remove")
//    @ResponseBody
//    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusLonContractRepayment> dto) throws HlsCusException {
//        IRequest requestCtx = createRequestContext(request);
//        service.batchDeleteRepayment(requestCtx, dto);
//        return new ResponseData();
//    }
//
//    @RequestMapping(value = "/hlsLon/contract/repayment/default/query")
//    @ResponseBody
//    public ResponseData queryByDefault(HlsCusLonContractRepayment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.select(requestContext, dto, page, pageSize));
//    }


    /**
     * 还款确认
     *
     * @param request
     * @param hlsCusLonContractWithdraws
     * @return
     */
    @RequestMapping(value = "/hlsLon/contract/repayment/confirm")
    @ResponseBody
    public ResponseData confirmRepayment(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException, com.hand.hls.exception.HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusLonContractWithdraw> hlsCusLonContractWithdraws = parameter.toJavaList(HlsCusLonContractWithdraw.class);
        service.confirmRepayment(requestCtx, hlsCusLonContractWithdraws);
        return new ResponseData();
    }
//    @RequestMapping(value = "/hlsLon/contract/repayment/queryChartRep")
//    @ResponseBody
//    public ResponseData queryChartRep(@RequestBody HlsCusLonContractRepayment hlsCusLonContractRepayment, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
//        IRequest requestContext = createRequestContext(request);
//        Long companyId = (Long) session.getAttribute("companyId");
//        hlsCusLonContractRepayment.setCompanyId(companyId);
//        return new ResponseData(service.queryChartRep(requestContext, hlsCusLonContractRepayment, page, pageSize));
//    }
//
//    @RequestMapping(value = "/hlsLon/contract/repayment/queryRep")
//    @ResponseBody
//    public ResponseData queryRep(@RequestBody HlsCusLonContractRepayment hlsCusLonContractRepayment, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
//        IRequest requestContext = createRequestContext(request);
//        Long companyId = (Long) session.getAttribute("companyId");
//        hlsCusLonContractRepayment.setCompanyId(companyId);
//        return new ResponseData(service.queryRep(requestContext, hlsCusLonContractRepayment, page, pageSize));
//    }
//
//    @RequestMapping(value = "/hlsLon/contract/repayment/queryRepaymentList")
//    @ResponseBody
//    public ResponseData queryRepaymentList(@RequestBody HlsCusLonContractRepayment hlsCusLonContractRepayment, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
//        IRequest requestContext = createRequestContext(request);
//        Long companyId = (Long) session.getAttribute("companyId");
//        hlsCusLonContractRepayment.setCompanyId(companyId);
//        hlsCusLonContractRepayment.setConfirmFlag("N");
//        return new ResponseData(service.queryList(requestContext, hlsCusLonContractRepayment, page, pageSize));
//    }
//
    @RequestMapping(value = "/hlsLon/contract/repayment/queryPaidList")
    @ResponseBody
    public ResponseData queryPaidList(@RequestBody HlsCusLonContractRepayment hlsCusLonContractRepayment, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        Long companyId = (Long) session.getAttribute("companyId");
        hlsCusLonContractRepayment.setCompanyId(companyId);
        //hlsCusLonContractRepayment.setConfirmFlag("Y");
        return new ResponseData(service.queryList(requestContext, hlsCusLonContractRepayment, page, pageSize));
    }
//
//    /*还款综合查询*/
//    @RequestMapping(value = "/hlsLon/contract/repayment/unitQueryRep")
//    @ResponseBody
//    public ResponseData unitQueryRep(HttpServletRequest request, HlsCusLonContractRepayment hlsCusLonContractRepayment, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpSession session) {
//        hlsCusLonContractRepayment.setCompanyId((Long) session.getAttribute("companyId"));
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.unitQueryRep(requestContext, hlsCusLonContractRepayment, page, pageSize));
//    }
//
//
//    /**
//     * 综合查询
//     *
//     * @param request
//     * @param config
//     * @param httpServletResponse
//     * @param session
//     */
//    @RequestMapping(value = "/hlsLon/contract/repayment/unitQueryRepExport")
//    public void unitQueryRepExport(HttpServletRequest request, @RequestParam String config,
//                                   HttpServletResponse httpServletResponse, HttpSession session) throws InvocationTargetException, IllegalAccessException {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusLonContractRepayment.class, ColumnInfo.class);
//            ExportConfig<HlsCusLonContractRepayment, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
//            service.unitQueryRepDownloadExcel(requestContext, request, httpServletResponse, exportConfig.getParam());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RequestMapping(value = "/ct/csh/debt/maturity/structure/chart/query")
//    @ResponseBody
//    public ResponseData queryDebtMaturityStructureChart(HlsCusLonContractRepayment lonContractRepayment, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.queryDebtMaturityStructureChart(requestContext, lonContractRepayment, page, pageSize));
//    }
//
//    @RequestMapping(value = "/ct/csh/debt/maturity/structure/chart/export")
//    public void createXLSC(HttpServletRequest request, @RequestParam String config,
//                           HttpServletResponse httpServletResponse, HttpSession session) {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusLonContractRepayment.class, ColumnInfo.class);
//            ExportConfig<HlsCusLonContractRepayment, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
//            excelService.exportAndDownloadExcel("hls.core.lon.mapper.HlsCusLonContractRepaymentMapper.queryDebtMaturityStructureChart",
//                    exportConfig, request, httpServletResponse, requestContext);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 还款计划导出
//     *
//     * @param request
//     * @param config
//     * @param httpServletResponse
//     * @param session
//     */
//    @RequestMapping(value = "/hlsLon/contract/repayment/repayPlanExport")
//    public void repayPlanExport(HttpServletRequest request, @RequestParam String config,
//                                HttpServletResponse httpServletResponse, HttpSession session) {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusLonContractRepayment.class, ColumnInfo.class);
//            ExportConfig<HlsCusLonContractRepayment, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
//            exportConfig.getParam().setInterestFlag("Y");
//            service.exportRepaymentReport(request, httpServletResponse, exportConfig.getParam());
////            excelService.exportAndDownloadExcel("hls.core.lon.mapper.HlsCusLonContractRepaymentMapper.selectLonContractRep",
////                    exportConfig, request, httpServletResponse, requestContext);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 融资付款费用导出
//     *
//     * @param request
//     * @param config
//     * @param httpServletResponse
//     * @param session
//     */
//    @RequestMapping(value = "/hlsLon/contract/repayment/payPlanExport")
//    public void payPlanExport(HttpServletRequest request, @RequestParam String config,
//                              HttpServletResponse httpServletResponse, HttpSession session) {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
//                    ExportConfig.class, HlsCusLonContractRepayment.class, ColumnInfo.class);
//            ExportConfig<HlsCusLonContractRepayment, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
//            exportConfig.getParam().setInterestFlag("N");
//            service.exportRepaymentReport(request, httpServletResponse, exportConfig.getParam());
////            excelService.exportAndDownloadExcel("hls.core.lon.mapper.HlsCusLonContractRepaymentMapper.selectLonContractRep",
////                    exportConfig, request, httpServletResponse, requestContext);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 变更后本息还款计划
//     */
//    @RequestMapping(value = "/hlsLon/contract/change/after/repayment/query")
//    @ResponseBody
//    public ResponseData hlsLonContractChangeAfterQuery(HlsCusLonContractRepayment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(service.selectLonContractChangeAfterRep(requestContext, dto, page, pageSize, "Rep"));
//    }

}