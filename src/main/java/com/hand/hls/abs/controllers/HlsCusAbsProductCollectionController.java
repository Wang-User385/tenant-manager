package com.hand.hls.abs.controllers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import com.hand.hls.abs.service.HlsCusAbsProductCollectionService;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusAbsProductCollectionController extends BaseController {

    @Autowired
    private HlsCusAbsProductCollectionService service;


    @Autowired
    private HlsCusAbsProductService productService;

    @Autowired
    IExportService excelService;

    @Autowired
    ObjectMapper objectMapper;
    @RequestMapping(value = "/ct/abs/product/collection/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsProductCollection dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectProductCollectionData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/abs/product/collection/detail/query")
    @ResponseBody
    public HlsCusAbsProductCollection queryCollection(HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.queryCollection(requestContext, dto);
    }

    @RequestMapping(value = "/ct/abs/product/cashInfo/detail/query")
    @ResponseBody
    public HlsCusAbsProductCollection queryCashDetail(HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.queryCashDeatil(requestContext, dto);
    }


    @RequestMapping(value = "/ct/abs/product/remittance/detail/query")
    @ResponseBody
    public HlsCusAbsProductCollection queryRemittance(HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.queryRemittance(requestContext, dto);
    }

    @RequestMapping(value = "/ct/abs/product/collection/save")
    @ResponseBody
    public ResponseData update(@RequestBody HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProductCollection> list = new ArrayList<>(1);
        list.add(service.save(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/abs/product/remittance/save")
    @ResponseBody
    public ResponseData remittanceSave(@RequestBody HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProductCollection> list = new ArrayList<>(1);
        list.add(service.remittanceSave(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/abs/product/remittance/submit")
    @ResponseBody
    public ResponseData remittanceSubmit(@RequestBody HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProductCollection> list = new ArrayList<>(1);
        list.add(service.remittanceSubmit(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/abs/product/collection/confirm")
    @ResponseBody
    public ResponseData colConfirm(@RequestBody HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProductCollection> list = new ArrayList<>(1);
        list.add(service.colConfirm(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/abs/product/collection/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProductCollection> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    /**
     * 兑付详情界面保存
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/product/collection/cashSave")
    @ResponseBody
    public ResponseData saveCashDetail(@RequestBody HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProductCollection> list = new ArrayList<>(1);
        list.add(service.saveCashDeatil(requestCtx, dto));
        return new ResponseData(list);
    }

    /**
     * 兑付确认
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/product/collection/cashConfirm")
    @ResponseBody
    public ResponseData confirmCashDetail(@RequestBody HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProductCollection> list = new ArrayList<>(1);
        list.add(service.confirmCashDeatil(requestCtx, dto));
        return new ResponseData(list);
    }

    /**
     * 兑付计算
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/product/collection/cashCalculate")
    @ResponseBody
    public ResponseData calculateCashDetail(@RequestBody HlsCusAbsProductCollection dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProductCollection> list = new ArrayList<>(1);
        list.add(service.calculateCashDetail(requestCtx, dto));
        return new ResponseData(list);
    }


    @RequestMapping(value = "/ct/abs/product/collection/excel/download")
    public void exportAccountPredict(HttpServletRequest request, @RequestParam String config,
                                     HttpServletResponse httpServletResponse) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusAbsProductCollection.class, ColumnInfo.class);
            ExportConfig<HlsCusAbsProductCollection, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            this.service.exportAndDownloadExcel(exportConfig, request, httpServletResponse, requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

