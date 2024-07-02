package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusPrjQuotationController extends BaseController {

    @Autowired
    private HlsCusPrjQuotationService service;

    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;

    @RequestMapping(value = "/ct/prj/quotation/query/info")
    @ResponseBody
    public ResponseData query(HlsCusPrjQuotation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPrjQuotation> list = new ArrayList<>();
        if (dto.getSourceDocumentId() != 0) {
            list = service.select(requestContext, dto, page, pageSize);

        }


        return new ResponseData(list);
    }


    //报价引用
    @RequestMapping(value = "/hls/cus/prj/quotation/quote")
    @ResponseBody
    public ResponseData quotationQuote(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotation hlsCusPrjQuotation = param.toJavaObject(HlsCusPrjQuotation.class);
        String calcName = param.get("calc_name").toString();
        Long projectId = Long.parseLong(request.getParameter("project_id")) ;
        Long quotationId = Long.parseLong(request.getParameter("quotation_id")) ;
        Long createId = Long.parseLong(request.getParameter("create_by")) ;
        hlsCusPrjQuotation.setCreatedBy(createId);
        hlsCusPrjQuotation.setLastUpdatedBy(createId);
        hlsCusPrjQuotation.setCalcName(calcName);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusPrjQuotation> list = new ArrayList<>();
        list.add(service.quotationQuote(requestCtx,hlsCusPrjQuotation,projectId,quotationId));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/quotation/re/calc")
    @ResponseBody
    public ResponseData quotationReCalc(HttpServletRequest request, Long quotationId)
            throws Exception {
        ResponseData rd = new ResponseData();
        IRequest requestContext = createRequestContext(request);

        //hlsCusPrjProjectService.prjProjectSave(requestContext, hlsCusPrjProjectInfo);
        //报价方案变更则需要删除原报价
//        service.quotationReCalcBefore(requestContext, quotationId,true);
        service.quotationReCalc(requestContext, quotationId,true);
        rd.setSuccess(true);

        return rd;
    }


    @RequestMapping(value = "/ct/prj/quotation/create/new")
    @ResponseBody
    public ResponseData createNewQuatation(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {

        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotation  hlsCusPrjQuotation= param.toJavaObject(HlsCusPrjQuotation.class);

        service.prjQuotationCreateNew(requestCtx,hlsCusPrjQuotation);

        return new ResponseData();
    }

    @RequestMapping(value = "/prj/quotation/query/base/rate")
    @ResponseBody
    public ResponseData queryBaseRate(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws HlsCusException {

        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotation  hlsCusPrjQuotation= param.toJavaObject(HlsCusPrjQuotation.class);

        Double baseRate = service.selectLprBaseRateByDate(requestCtx,hlsCusPrjQuotation);
        hlsCusPrjQuotation.setBaseRate(baseRate);

        List<HlsCusPrjQuotation> list = new ArrayList();
        list.add(hlsCusPrjQuotation);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/quotation/delete")
    @ResponseBody
    public ResponseData deletePrjQuatation(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {

        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotation  hlsCusPrjQuotation= param.toJavaObject(HlsCusPrjQuotation.class);
        if(hlsCusPrjQuotation.getQuotationId() != null){
            service.deleteByPrimaryKey(hlsCusPrjQuotation);
        }


        return new ResponseData();
    }

    @RequestMapping(value = "/prj/quotation/deposit/fee/save")
    @ResponseBody
    public ResponseData quotationDepositSave(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws HlsCusException, com.hand.hls.exception.HlsCusException {

        IRequest requestCtx = createRequestContext(request);
        JSONArray params = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjQuotationCashflow>  quotationCashflows = params.toJavaList(HlsCusPrjQuotationCashflow.class);


        hlsCusPrjQuotationCashflowService.saveFeeCashflow(requestCtx,quotationCashflows);

        return new ResponseData();
    }

    @RequestMapping(value = "/prj/quotation/paynote/fee/save")
    @ResponseBody
    public ResponseData quotationPaynoteFeeSave(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws HlsCusException, com.hand.hls.exception.HlsCusException {

        IRequest requestCtx = createRequestContext(request);
        JSONArray params = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjQuotationCashflow>  quotationCashflows = params.toJavaList(HlsCusPrjQuotationCashflow.class);


        hlsCusPrjQuotationCashflowService.savePaynote(requestCtx,quotationCashflows);

        return new ResponseData();
    }

    @RequestMapping(value = "/prj/quotation/record/save")
    @ResponseBody
    public ResponseData quotationRecordSave(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {

        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotation  hlsCusPrjQuotation= param.toJavaObject(HlsCusPrjQuotation.class);

        service.savePrjQuotationRecord(requestCtx,hlsCusPrjQuotation);

        return new ResponseData();
    }

    @RequestMapping(value = "/change/quotation/info")
    @ResponseBody
    public ResponseData changeQuotationInfo(HlsCusPrjQuotation quotation,  @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        return new ResponseData(service.selectQuotationChangeInfo(requestContext, quotation, page, pageSize));
    }


    @RequestMapping({"/quotation/floating/ln/query"})
    @ResponseBody
    public ResponseData queryFloatLn(HlsCusPrjQuotation dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjQuotation metadataRelation = (HlsCusPrjQuotation)param.toJavaObject(HlsCusPrjQuotation.class);

        return new ResponseData(this.service.queryQuotationRateReqNew(requestContext, metadataRelation, pagenum, pagesize));

    }


    @RequestMapping({"/quotation/floating/calc/query"})
    @ResponseBody
    public ResponseData queryFloatCalc(HlsCusPrjQuotation dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjQuotation metadataRelation = (HlsCusPrjQuotation)param.toJavaObject(HlsCusPrjQuotation.class);

        return new ResponseData(this.service.queryQuotationFloatingLnReqNew(requestContext, metadataRelation, pagenum, pagesize));

    }

    @RequestMapping({"/quotation/floating/calc/wfl/query"})
    @ResponseBody
    public ResponseData queryFloatCalcWfl(HlsCusPrjQuotation dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjQuotation metadataRelation = (HlsCusPrjQuotation)param.toJavaObject(HlsCusPrjQuotation.class);

        return new ResponseData(this.service.queryQuotationFloatingLnReqWfl(requestContext, metadataRelation, pagenum, pagesize));

    }


    /**
     * 调息查询台账
     * @param dto
     * @param requestData
     * @param pagenum
     * @param pagesize
     * @param request
     * @return
     */
    @RequestMapping({"/query/floating/interest"})
    @ResponseBody
    public ResponseData queryFloatInterest(HlsCusPrjQuotation dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjQuotation metadataRelation = (HlsCusPrjQuotation)param.toJavaObject(HlsCusPrjQuotation.class);

        return new ResponseData(this.service.queryFloatingInterest(requestContext, metadataRelation, pagenum, pagesize));

    }


    @RequestMapping({"/query/floating/total/interest"})
    @ResponseBody
    public ResponseData queryTotalFloatInterest(HlsCusPrjQuotation dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjQuotation metadataRelation = (HlsCusPrjQuotation)param.toJavaObject(HlsCusPrjQuotation.class);

        return new ResponseData(this.service.queryTotalFloatingInterest(requestContext, metadataRelation, pagenum, pagesize));

    }

    @RequestMapping({"/calc/history/quotation/xirr"})
    @ResponseBody
    public ResponseData calcHistoryQuotationXirr(HlsCusPrjQuotation dto, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        String message = this.service.updateHistoryQuotationXirr(requestContext,dto);
        List list = new ArrayList();
        list.add(message);
        return new ResponseData(list);
    }

}