package com.hand.hls.vat.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.interfacePlatform.utils.InvoiceBaseUtils;
import com.hand.hls.utils.JsonUtils;
import com.hand.hls.vat.dto.AcrInvoiceBill;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.exception.AcrInvoiceException;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import com.hand.hls.vat.service.IAcrInvoiceHdService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class AcrInvoiceHdController extends BaseController {

    @Autowired
    private IAcrInvoiceHdService service;

    @Autowired
    private IConContractCashflowService conContractCashflowService;

    @Autowired
    private HlsCusAcrInvoiceHdMapper acrInvoiceHdMapper;
    @Autowired
    private InvoiceBaseUtils invoiceBaseUtils;

    /**
     * 销项发票--查询待开票清单
     */
    @RequestMapping(value = "/acr/invoice/queryWaitingInvoiceList")
    @ResponseBody
    public ResponseData queryWaitingInvoiceList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                HttpServletRequest request) {
        Map parameter = JSON.parseObject(JsonUtils.toCamelJsonString(requestData.get("parameter")), Map.class);
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(conContractCashflowService.queryWaitingInvoiceList(requestContext, parameter, pagenum, pagesize));
    }

    /**
     * 销项发票详情
     *
     * @return
     */
    @RequestMapping(value = "/acr/invoice/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                              @RequestParam(required = false) String forReverse,
                              HttpServletRequest request) {
        HlsCusAcrInvoiceHd dto = JSON.parseObject(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        if (StringUtils.isNotBlank(forReverse))
            dto.setForReverse(forReverse);
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);

        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        List<HlsCusAcrInvoiceHd> list=acrInvoiceHdMapper.queryAcrInvoiceHdDetail(dto);
        return new ResponseData(list);
    }
    @RequestMapping(value = "/acr/invoice/hd/queryNew")
    @ResponseBody
    public ResponseData queryNew(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                              @RequestParam(required = false) String forReverse,
                              HttpServletRequest request) {
        HlsCusAcrInvoiceHd dto = JSON.parseObject(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        if (StringUtils.isNotBlank(forReverse))
            dto.setForReverse(forReverse);
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);

        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        List<HlsCusAcrInvoiceHd> list=acrInvoiceHdMapper.queryAcrInvoiceHdDetailNew(dto);
        return new ResponseData(list);
    }
    //搜索引擎首页查询
    @RequestMapping(value = "/search/invoice/hd/home/query")
    @ResponseBody
    public ResponseData searchInvoicHdHomeQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                @RequestParam(required = false) String forReverse,
                                                HttpServletRequest request) {
        String paramStr = requestData.get("parameter").toString();
        HlsCusAcrInvoiceHd dto = JSON.parseObject(paramStr, new TypeReference<HlsCusAcrInvoiceHd>() {
        });
        if (StringUtils.isNotBlank(forReverse))
            dto.setForReverse(forReverse);
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(service.searchInvoicHdHomeQuery(dto, pagenum, pagesize));
    }

    /**
     * 发票头明细--根据hdId
     *
     * @param invoiceHdId
     * @return
     */
    @RequestMapping(value = "/acr/invoice/hd/queryById")
    @ResponseBody
    public ResponseData queryDetailsById(@RequestParam Long invoiceHdId,
                                         HttpServletRequest request) {
        if (invoiceHdId == null) {
            return new ResponseData(false, "发票头信息不存在!");
        }
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(Arrays.asList(service.queryAcrInvoiceHdDetailById(invoiceHdId)));
    }

    /**
     * 发票头维护
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/acr/invoice/hd/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                               HttpServletRequest request) {
        List<HlsCusAcrInvoiceHd> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        if (dto.size() > 0 && "CONFIRM".equals(dto.get(0).getInvoiceStatus())) {
            return new ResponseData(false, "已确认的发票不能修改!");
        }
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/acr/invoice/hd/selectForCreate")
    @ResponseBody
    public ResponseData selectForCreate(HttpServletRequest request,
                                        @RequestParam String combineRule,
                                        @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws AcrInvoiceException {
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        String cfIdStr = (String) parameter.get("cf_id_str");
        String tempId = (String) parameter.get("temp_id");
        if (cfIdStr == null || StringUtils.isBlank(tempId)) {
            return new ResponseData(false, "现金流信息缺失!");
        }
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        List<Long> cashflows = JSON.parseArray(cfIdStr, Long.class);
        return new ResponseData(service.selectForCreate(tempId, cashflows, combineRule));
    }

    /**
     * 创建销项发票
     *
     * @return
     */
    @RequestMapping(value = "/acr/invoice/hd/create")
    @ResponseBody
    public ResponseData createInvoice(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                      HttpServletRequest request) throws AcrInvoiceException {
        List<HlsCusAcrInvoiceHd> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        service.create(iRequest, dto);
        return new ResponseData();
    }


    /**
     * 发票确认
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/acr/invoice/hd/confirm")
    @ResponseBody
    public ResponseData confirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                HttpServletRequest request) throws AcrInvoiceException {
//        List<HlsCusAcrInvoiceHd> dto=new ArrayList<>();
//        for(int i=0;i<requestData.size();i++){
//            HlsCusAcrInvoiceHd parameter =  JSON.parseObject(JSON.toJSONString(requestData.get("parameter")),HlsCusAcrInvoiceHd.class);
//            dto.add(parameter);
//        }
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusAcrInvoiceHd> list = param.toJavaList(HlsCusAcrInvoiceHd.class);
        RequestHelper.setCurrentRequest(requestCtx);
        service.confirm(requestCtx, list);
        return new ResponseData();
    }

    /**
     * 发票删除
     * 1.back cashFlow
     * 2.only invoice status "new"
     */
    @RequestMapping(value = "/acr/invoice/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws AcrInvoiceException {
        List<HlsCusAcrInvoiceHd> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        service.delete(iRequest, dto);
        return new ResponseData(dto);
    }

    /**
     * 发票反冲
     */
    @RequestMapping(value = "/acr/invoice/hd/reverse")
    @ResponseBody
    public ResponseData reverse(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                @RequestParam String reverseType,
                                HttpServletRequest request) throws AcrInvoiceException {
        List<HlsCusAcrInvoiceHd> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        service.reverse(requestCtx, dto, reverseType);
        return new ResponseData();
    }

    @RequestMapping(value = "/acr/invoice/hd/postInterface")
    @ResponseBody
    public ResponseData postInterface(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long invoiceHdId = Long.valueOf(param.get("invoice_hd_id").toString());
        HlsCusAcrInvoiceHd acrInvoiceHd = acrInvoiceHdMapper.selectByPrimaryKey(invoiceHdId);
        invoiceBaseUtils.createInvoiceItfc(requestContext,acrInvoiceHd);
        return new ResponseData();
    }

    @RequestMapping(value = "/acr/invoice/hd/queryDZFPUrl")
    @ResponseBody
    public ResponseData queryInvoiceUrl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long invoiceHdId = Long.valueOf(param.get("invoice_hd_id").toString());
        HlsCusAcrInvoiceHd acrInvoiceHd = acrInvoiceHdMapper.selectByPrimaryKey(invoiceHdId);
        invoiceBaseUtils.queryInvoiceUrlItfc(requestContext,acrInvoiceHd);
        return new ResponseData();
    }
}