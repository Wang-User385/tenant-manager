package com.hand.hls.hls.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationLnService;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsDurationLnController extends BaseController {

    @Autowired
    private HlsDurationLnService service;
    @Autowired
    private HlsDurationLnMapper mapper;
    @Autowired
    private HlsCusConContractService contractService;


    @RequestMapping(value = "/hls/duration/ln/change/date")
    @ResponseBody
    public ResponseData changeDateUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationLn hlsDurationLn = param.toJavaObject(HlsDurationLn.class);
        return new ResponseData(service.hlsLnChangeDateUpdate(requestCtx,hlsDurationLn));
    }

    @RequestMapping(value = "/hls/duration/ln/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationLn dto = param.toJavaObject(HlsDurationLn.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = " /hls/duration/ln/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsDurationLn> list = param.toJavaList(HlsDurationLn.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/duration/ln/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsDurationLn> dto = parameter.toJavaList(HlsDurationLn.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/hls/duration/ln/delete")
    @ResponseBody
    public ResponseData deleteLn(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        HlsDurationLn dto = parameter.toJavaObject(HlsDurationLn.class);
        mapper.deleteHlsDurationLn(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/hls/duration/ln/delete/new")
    @ResponseBody
    public ResponseData deleteLnCon(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        HlsDurationLn dto = parameter.toJavaObject(HlsDurationLn.class);
        dto = mapper.selectByPrimaryKey(dto);
        if(dto.getContractId() != null && dto.getContractId().toString() != ""){
            Long contractId = dto.getContractId();
            //删除后更新支付表状态
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractStatus("INCEPT");
            contract.setContractId(contractId);
            contractService.updateByPrimaryKeySelective(iRequest,contract);
        }
        service.deleteByPrimaryKey(dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/hls/duration/ln/item/detail/query")
    @ResponseBody
    public ResponseData itemDetailQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationLn dto = param.toJavaObject(HlsDurationLn.class);
        //质押 回购  明细查询 不分页
        return new ResponseData(service.hlsDurationLnItemDetailQuery(dto));
    }

    @RequestMapping(value = "/hls/duration/ln/warrant/detail/query")
    @ResponseBody
    public ResponseData warrantDetailQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationLn dto = param.toJavaObject(HlsDurationLn.class);
        //质押 回购  权证信息 不分页
        return new ResponseData(service.hlsDurationLnWarrantDetailQuery(dto));
    }


    /**
     * @Title: projectDelete
     * @Discription: 项目变更审批工作联系单 删除按钮
     * @Param: [request, requestData]
     * @Return: com.hand.hap.system.dto.ResponseData
     */
    @RequestMapping(value = "/hls/duration/ln/project/delete")
    @ResponseBody
    public ResponseData projectDelete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        //选择的虚拟合同
        List<HlsDurationLn> lnList = parameter.toJavaList(HlsDurationLn.class);
        //虚拟合同关联的支付表
        for (HlsDurationLn ln : lnList) {
            HlsDurationLn durationLn = new HlsDurationLn();
            durationLn.setHdId(ln.getHdId());
            durationLn.setSourceType("CONTRACT");
            durationLn.setSourceId(ln.getSourceId());
            List<HlsDurationLn> durationLns = mapper.select(durationLn);
            service.batchDelete(durationLns);
        }
        service.batchDelete(lnList);
        return new ResponseData();
    }
}
