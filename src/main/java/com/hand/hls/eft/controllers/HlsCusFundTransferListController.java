package com.hand.hls.eft.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.eft.service.HlsCusFundTransferListService;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class HlsCusFundTransferListController extends BaseController {

    @Autowired
    private HlsCusFundTransferListService service;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/query")
    @ResponseBody
    public ResponseData query(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFundTransferList(requestContext, dto, page, pageSize));
    }


    /**
     * 资金主页查询详情
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/queryDetail")
    @ResponseBody
    public ResponseData queryDetail(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFundTransferListDeatil(requestContext, dto, page, pageSize));
    }


    /**
     * 财务查询调拨单
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/finance/transfer/list/query")
    @ResponseBody
    public ResponseData queryFinanceTransfer(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFundTransferFinanceList(requestContext, dto, page, pageSize));
    }


    /**
     * 财务查询调拨单
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/copy")
    @ResponseBody
    public ResponseData queryCopyTransferList(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectCopyFundTransferList(requestContext, dto, page, pageSize));
    }

    /**
     * 调拨审批单查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/approve")
    @ResponseBody
    public ResponseData queryApproveTransferList(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectApproveTransferList(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/eft/fund/transfer/list/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundTransferList> dto = param.toJavaList(HlsCusFundTransferList.class);
        return new ResponseData(service.batchUpdateTransferList(requestCtx, dto));
    }


    @RequestMapping(value = "/eft/fund/transfer/list/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFundTransferList> dto) {
        IRequest requestCtx = createRequestContext(request);
        service.batchDeleteTransferList(requestCtx,dto);
        return new ResponseData();
    }


    /**
     * 应还代办
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/queryTaskList")
    @ResponseBody
    public ResponseData queryTaskList(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectTransferTaskList(requestContext, dto, page, pageSize));
    }


    /**
     * 选择应还代办进行调拨（资金）
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/create")
    @ResponseBody
    public ResponseData createTransferListFund(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundTransferList> dto = param.toJavaList(HlsCusFundTransferList.class);
        return new ResponseData(service.createTransferListFund(requestCtx, dto));
    }


    /**
     * 资金调拨单导出
     * @param request
     * @param config
     * @param httpServletResponse
     */
    @RequestMapping(value = "/eft/fund/transfer/list/export")
    public void exportTransferList(HttpServletRequest request, @RequestParam String config,
                                HttpServletResponse httpServletResponse) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusFundTransferList.class, ColumnInfo.class);
            ExportConfig<HlsCusFundTransferList, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            service.exportTransferList(request, httpServletResponse, exportConfig.getParam());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择应还代办进行创建（财务）
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/finance/transfer/list/create")
    @ResponseBody
    public ResponseData createTransferListFinance(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundTransferList> dto = param.toJavaList(HlsCusFundTransferList.class);
        return new ResponseData(Arrays.asList(service.createTransferListFinance(requestCtx, dto)));
    }

    /**
     * 选择应还代办进行作废（财务）
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/finance/transfer/list/cancel")
    @ResponseBody
    public ResponseData cancelTransferListFinance(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundTransferList> dto = param.toJavaList(HlsCusFundTransferList.class);
        IRequest requestCtx = createRequestContext(request);
        service.cancelTransferListFinance(requestCtx, dto);
        return new ResponseData();
    }


    /**
     * 未来30天资金缺口
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/queryBalance")
    @ResponseBody
    public ResponseData selectAccountBalance(HlsCusFundTransferList dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAccountBalanceData(dto));
    }


    /**
     * 资金缺口选择创建
     * @param request
     * @return
     * @throws HlsCusException
     */
    @RequestMapping(value = "/eft/gap/transfer/list/create")
    @ResponseBody
    public ResponseData createTransferListGap(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundTransferList> dto = param.toJavaList(HlsCusFundTransferList.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.createTransferListGap(requestCtx, dto));
    }


    /**
     * 查询未来收支明细
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/queryBalanceDetail")
    @ResponseBody
    public ResponseData queryBalanceDetail(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAccountBalanceDetail(requestContext, dto, page, pageSize));
    }




    /**
     * 查询账户的详情
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/list/queryAccount")
    @ResponseBody
    public ResponseData queryAccountDetail(HlsCusFundTransferList dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectBankAccountData(requestContext,dto));
    }



    /**
     * 账户收支明细导出
     * @param request
     * @param config
     * @param httpServletResponse
     */
    @RequestMapping(value = "/eft/fund/transfer/list/exportBalanceDetail")
    public void exportBalanceDetail(HttpServletRequest request, @RequestParam String config,
                                   HttpServletResponse httpServletResponse) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusFundTransferList.class, ColumnInfo.class);
            ExportConfig<HlsCusFundTransferList, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            service.exportBalanceDetail(request, httpServletResponse, exportConfig.getParam());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载调拨审批单
     * @param jsonStr
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/eft/fund/transfer/list/download")
    @ResponseBody
    public void downloadDepositDeductionDeduction(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request, HttpServletResponse response) throws Exception{
        String[] params = request.getParameterValues("param");
        String strName = new String(params[0].getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        List<HlsCusFundTransferList> fundTransferLists = JSONObject.parseArray(strName, HlsCusFundTransferList.class);
        service.downloadTransferList(fundTransferLists,request,response);
    }



    @RequestMapping(value = "/eft/fund/transfer/list/queryWriteOff")
    @ResponseBody
    public ResponseData queryWriteOff(HlsCusFundTransferList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectWriteOffDeatil(requestContext, dto, page, pageSize));
    }


    /**
     * 选择已办事项办进行作废（资金）
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/finance/task/list/cancel")
    @ResponseBody
    public ResponseData cancelTaskListCancel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundTransfer> dtoList = param.toJavaList(HlsCusFundTransfer.class);
        IRequest requestCtx = createRequestContext(request);
        service.cancelTaskListCancel(requestCtx, dtoList);
        return new ResponseData();
    }

    @RequestMapping(value = "/eft/fund/transfer/list/delete")
    @ResponseBody
    public ResponseData deleteTaskList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundTransferList> dtoList = param.toJavaList(HlsCusFundTransferList.class);
        service.batchDelete(dtoList);
        return new ResponseData();
    }
}