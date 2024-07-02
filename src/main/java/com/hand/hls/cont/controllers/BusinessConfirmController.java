package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.cont.dto.BusinessConfirm;
import com.hand.hls.cont.dto.ConfirmBatch;
import com.hand.hls.cont.service.IBusinessConfirmService;
import com.hand.hls.cont.service.IConContractService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BusinessConfirmController extends BaseController {

    @Autowired
    private IBusinessConfirmService service;
    @Autowired
    private IConContractService conContractService;


    @RequestMapping(value = "/con/business/confirm/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        BusinessConfirm dto = param.toJavaObject(BusinessConfirm.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/con/business/confirm/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<BusinessConfirm> list = param.toJavaList(BusinessConfirm.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/con/business/confirm/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<BusinessConfirm> dto = parameter.toJavaList(BusinessConfirm.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/con/business/confirm/create")
    @ResponseBody
    public ResponseData create(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray contractIdsArray = JSONObject.parseObject(requestData.get("parameter").toString()).getJSONArray("contractIds");
        List<Long> contractIds = JSONObject.parseArray(contractIdsArray.toJSONString(),Long.class);
        List<Long> batchIds = conContractService.createBusinessConfirm(iRequest, contractIds);
        return new ResponseData(batchIds);
    }

    @RequestMapping(value = "/con/business/confirm/cancel")
    @ResponseBody
    public void cancelBusinessConfirm(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Long batchId = Long.valueOf(JSONObject.parseObject(requestData.get("parameter").toString()).get("batch_id").toString());
        service.cancelBusinessConfirm(iRequest, batchId);
    }

    @RequestMapping(value = "/con/business/confirm/confirm")
    @ResponseBody
    public void confirmBusinessConfirm(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Long batchId = JSONObject.parseObject(requestData.get("parameter").toString()).getLong("batch_id");
        JSONArray contractIdsArray = JSONObject.parseObject(requestData.get("parameter").toString()).getJSONArray("contractIds");
        List<Long> contractIds = JSONObject.parseArray(contractIdsArray.toJSONString(),Long.class);
        service.confirmBusinessConfirm(iRequest, batchId, contractIds);
    }

//    @RequestMapping(value = "/con/business/confirm/downloadDocx")
//    @ResponseBody
//    public void downloadDocxBusinessConfirm(HttpServletRequest request, HttpServletResponse response, String batchIdsString) throws Exception {
//        IRequest iRequest = createRequestContext(request);
//        String[] batchIds = batchIdsString.split(",");
//        List<ConfirmBatch> list = new ArrayList<>(batchIds.length);
//        for (int i = 0; i < batchIds.length; i++) {
//            ConfirmBatch confirmBatch = new ConfirmBatch();
//            confirmBatch.setBatchId(Long.valueOf(batchIds[i]));
//            list.add(confirmBatch);
//        }
//        service.downloadBusinessConfirm(iRequest, response, request, list);
//    }
    @RequestMapping(value = "/con/business/confirm/text")
    @ResponseBody
    public ResponseData contentCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ConfirmBatch> list = param.toJavaList(ConfirmBatch.class);
        List<FndAttachmentMulti> list1= service.contextCreateMultiple(requestCtx, list, response);
        return new ResponseData(list1);
    }

    @RequestMapping(value = "/con/business/confirm/del")
    @ResponseBody
    public ResponseData delBusinessConfirm(HttpServletRequest request, String batchIds) {
        IRequest iRequest = createRequestContext(request);
        try {
            service.delBusinessConfirm(iRequest, batchIds);
            return new ResponseData(true);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseData(false);
        }
    }

    /**
     * 经销商业务确认函创建
     * @param request HttpServletRequest
     * @param requestData LeafRequestData
     * @return ResponseData
     * @throws Exception e
     */
    @RequestMapping(value = "/con/dealer/business/confirm/create")
    @ResponseBody
    public ResponseData createDealerBusinessConfirm(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray contractIdsArray = JSONObject.parseObject(requestData.get("parameter").toString()).getJSONArray("contractIds");
        List<Long> contractIds = JSONObject.parseArray(contractIdsArray.toJSONString(),Long.class);
        List<Long> batchIds = conContractService.createDealerBusinessConfirm(iRequest, contractIds);
        return new ResponseData(batchIds);
    }

    /**
     * 经销商业务确认函打印
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param batchIdsString String
     * @throws Exception e
     */
//    @RequestMapping(value = "/con/dealer/business/confirm/downloadDocx")
//    @ResponseBody
//    public void downloadDocxDealerBusinessConfirm(HttpServletRequest request, HttpServletResponse response, String batchIdsString) throws Exception {
//        IRequest iRequest = createRequestContext(request);
//        String[] batchIds = batchIdsString.split(",");
//        List<ConfirmBatch> list = new ArrayList<>(batchIds.length);
//        for (int i = 0; i < batchIds.length; i++) {
//            ConfirmBatch confirmBatch = new ConfirmBatch();
//            confirmBatch.setBatchId(Long.valueOf(batchIds[i]));
//            list.add(confirmBatch);
//        }
//        service.downloadDealerBusinessConfirm(iRequest, response, request, list);
//    }

    @RequestMapping(value = "/con/dealer/business/confirm/text")
    @ResponseBody
    public ResponseData contentCreate1(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ConfirmBatch> list = param.toJavaList(ConfirmBatch.class);
        List<FndAttachmentMulti> list1= service.contextCreateMultiple1(requestCtx, list, response);
        return new ResponseData(list1);
    }
}