package com.hand.hls.pam.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusCreditProject;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.HlsCusLeaseItemManage;
import com.hand.hls.pam.service.HlsCusLeaseItemService;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/14 - 11:58
 */
@Controller
public class HlsCusLeaseItemController  extends BaseController {

    @Autowired
    private HlsCusLeaseItemService hlsCusLeaseItemService;

    /**
     * 租赁物创建保存
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/hls/pam/lease/savesubmit")
    @ResponseBody
    public ResponseData save(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = createRequestContext(request);
        HlsCusLeaseItemManage dto = param.toJavaObject(HlsCusLeaseItemManage.class);
        List<HlsCusLeaseItemManage> list = new ArrayList<>(1);
        list.add(hlsCusLeaseItemService.leaseSaveSubmit(requestCtx, dto));
        return new ResponseData(list);
    }


    /**
     * 租赁物主页面查询
     * @param requestData
     * @param request
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/hls/pam/lease/itemQuery")
    @ResponseBody
    public ResponseData selectCreditLineChanceByStatus(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request,
                                                       @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLeaseItem hlsCusLeaseItem = param.toJavaObject(HlsCusLeaseItem.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(hlsCusLeaseItemService.selectPamLeaseItem(requestCtx, hlsCusLeaseItem, pagenum, pagesize));
    }


    /**
     * 租赁物跳转详细页面查询
     * @param requestData
     * @param request
     * @param response
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/hls/pam/lease/itemDetail/selectModelByCondition")
    @ResponseBody
    public ResponseData selectModelByCondition(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLeaseItem hlsCusLeaseItem = param.toJavaObject(HlsCusLeaseItem.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(hlsCusLeaseItemService.selectModelByCondition(requestContext, hlsCusLeaseItem, pagenum, pagesize));
    }


    @RequestMapping(value = "/hls/lease/item/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLeaseItem dto = param.toJavaObject(HlsCusLeaseItem.class);
        return new ResponseData(hlsCusLeaseItemService.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/lease/item/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusLeaseItem> list = param.toJavaList(HlsCusLeaseItem.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(hlsCusLeaseItemService.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/lease/item/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusLeaseItem> dto = parameter.toJavaList(HlsCusLeaseItem.class);
        hlsCusLeaseItemService.batchDelete(dto);
        return new ResponseData(dto);
    }
    /**
     * 抵押物作废
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/hls/pam/lease/invalid")
    @ResponseBody
    public ResponseData invalidLeaseItem(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusLeaseItem> list = param.toJavaList(HlsCusLeaseItem.class);
        list = hlsCusLeaseItemService.invalidLeaseItem(requestCtx, list);
        return new ResponseData(list);
    }

    /**
     * 租赁物作废
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/hls/pam/lease/rental/invalid")
    @ResponseBody
    public ResponseData invalidLeaseItemRental(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusLeaseItem> list = param.toJavaList(HlsCusLeaseItem.class);
        list = hlsCusLeaseItemService.invalidLeaseItemRental(requestCtx, list);
        return new ResponseData(list);
    }


    /*// 租赁物新增导入
    @RequestMapping(value = "/hls/pledge/dongcan/list/import", method = RequestMethod.POST)
    public Map<String, Object> receiptImportPledgeDc(HttpServletRequest request, Long headerId, Long leaseItemId, String patternDet) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            hlsCusLeaseItemService.receiptImportPledgeDc(iRequest, headerId,leaseItemId,patternDet);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }*/



    @RequestMapping("/hls/lease/item/generateAuthorityString")
    public ResponseData generateAuthorityString(HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        String authorityRuleString = hlsCusLeaseItemService.generateAuthorityString(iRequest);
        return new ResponseData(Arrays.asList(authorityRuleString));
    }

}
