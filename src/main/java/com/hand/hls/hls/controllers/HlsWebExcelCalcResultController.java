package com.hand.hls.hls.controllers;

import com.hand.hap.core.exception.TokenException;
import com.hand.hls.common.utils.GzipUtil;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import com.hand.hls.hls.service.IHlsWebExcelCalcResultService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsWebExcelCalcResultController extends BaseController{

    @Autowired
    private IHlsWebExcelCalcResultService service;


    @RequestMapping(value = "/hls/web/excel/calc/result/sheets/query", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String querySheet(HttpServletRequest request, HlsWebExcelCalcResult calcResult) {
        IRequest requestCtx = createRequestContext(request);

        return service.queryWebExcelCalcResult(requestCtx,calcResult);
    }

    @RequestMapping(value = "/hls/web/excel/calc/sheets/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData calcSheets(HttpServletRequest request, Long excelId,Long sourceDocumentId,String sourceDocumentCategory,Long resultId, @RequestBody String sheets,String finStatementName )
            throws TokenException {
        IRequest iRequest = createRequestContext(request);
        ResponseData rd = null;
        try {
            List list = new ArrayList();
            HlsWebExcelCalcResult calcResult = new HlsWebExcelCalcResult();
            String stringSheets = GzipUtil.atob(sheets);
            String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
            String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");

            calcResult.setExcelId(excelId);
            calcResult.setSourceDocumentId(sourceDocumentId);
            calcResult.setSourceDocumentCategory(sourceDocumentCategory);
            calcResult.setSheets(jsonSheets);
            calcResult.setCompressSheets(sheets);
            calcResult.setResultId(resultId);
            calcResult.setFinStatementName(finStatementName);

            calcResult = service.calcExcel(iRequest,calcResult);
            calcResult.setSheets(null);
            calcResult.setCompressSheets(null);
            list.add(calcResult);
            rd = new ResponseData(list);
        } catch (Exception e) {
            rd = new ResponseData(false);
            rd.setMessage(e.getMessage());
        }

        return rd;
    }

    @RequestMapping(value = "/hls/web/excel/calc/result/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsWebExcelCalcResult> dto = parameter.toJavaList(HlsWebExcelCalcResult.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}