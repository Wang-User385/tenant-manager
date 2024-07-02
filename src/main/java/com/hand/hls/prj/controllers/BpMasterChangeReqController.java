package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.BpMasterChangeReq;
import com.hand.hls.prj.service.IBpMasterChangeReqService;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BpMasterChangeReqController extends BaseController {

    @Autowired
    private IBpMasterChangeReqService service;

    public static final String NEW = "NEW";
    public static final String REJECTED = "REJECTED";
    public static final String CANCEL = "CANCEL";
    public static final String VENDER_WFL_TYPE = "VENDER_WFL_TYPE";
    public static final String DISTRIBUTOR_WFL_TYPE = "DISTRIBUTOR_WFL_TYPE";
    public static final String CHANGE_WFL_TYPE = "CHANGE_WFL_TYPE";

    private Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "/hls/bp/master/change/req/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        BpMasterChangeReq dto = param.toJavaObject(BpMasterChangeReq.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/bp/master/change/req/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<BpMasterChangeReq> list = param.toJavaList(BpMasterChangeReq.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/bp/master/change/req/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<BpMasterChangeReq> dto = parameter.toJavaList(BpMasterChangeReq.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    /**
     * 商业伙伴变更创建
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/hls/bp/master/change/request/submit")
    @ResponseBody
    public ResponseData submitRequest(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        BpMasterChangeReq changeReq = JSONObject.parseObject(requestData.get("parameter").toString(), BpMasterChangeReq.class);
        try {
            return new ResponseData(service.submit(createRequestContext(request), changeReq));
        } catch (ParameterNullException e) {
            return new ResponseData(false, e.getMessage());
        }
    }

    /**
     * 商业伙伴变更提交审批流程
     *
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/hls/bp/master/change/approve")
    @ResponseBody
    public ResponseData approve(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map para = requestData.getParameter();
        BpMasterChangeReq changeReq = new BpMasterChangeReq();

        String workflowType = para.get("workflowType").toString();
        Long allocationId = null;
        if (para.get("allocationId") == null && !StringUtils.equals(workflowType, VENDER_WFL_TYPE)) {
//            return new ResponseData(false, "未找到复核人信息，请确认后再提交！");
        } else if (StringUtils.equals(workflowType, DISTRIBUTOR_WFL_TYPE) || StringUtils.equals(workflowType, CHANGE_WFL_TYPE)) {
            allocationId = Long.valueOf(para.get("allocationId").toString());
        }

        if (para.get("changeReqId") != null) {
            changeReq.setChangeReqId(Long.valueOf(para.get("changeReqId").toString()));
            changeReq = this.service.selectByPrimaryKey(iRequest, changeReq);

            String approveStatus = StringUtils.isNotBlank(changeReq.getStatus()) ? changeReq.getStatus().toUpperCase() : NEW;

            if (!StringUtils.equals(approveStatus, NEW) && !StringUtils.equals(approveStatus, REJECTED) && !StringUtils.equals(approveStatus, CANCEL)) {

                return new ResponseData(false, "此单据已审批,不能重复审批");
            } else {
                this.service.approveWfl(iRequest, changeReq, allocationId, workflowType);
                return new ResponseData();
            }
        } else {
            return new ResponseData(false, "未找到提交的变更单据,请联系管理员!");
        }
    }


    /**
     * 商业伙伴变更新增校验
     */
    @RequestMapping(value = "/hls/bp/master/change/validate")
    @ResponseBody
    public ResponseData bpChangeValidate(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        BpMasterChangeReq changeReq = JSONObject.parseObject(requestData.get("parameter").toString(), BpMasterChangeReq.class);
        try {
            return new ResponseData(service.bpChangeValidate(createRequestContext(request), changeReq));
        } catch (Exception e) {
            return new ResponseData(false, e.getMessage());
        }
    }

    /**
     * 商业伙伴变更获取工作流类型
     */
    @RequestMapping(value = "/hls/bp/master/get/wfl/type")
    @ResponseBody
    public ResponseData getBpMasterChangeWflType(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        BpMasterChangeReq changeReq = JSONObject.parseObject(requestData.get("parameter").toString(), BpMasterChangeReq.class);
        ResponseData responseData = new ResponseData();
        try {
            responseData.setSuccess(true);
            List<String> wflType = new ArrayList<>(1);
            //wflType.add(service.getBpMasterChangeWflType(createRequestContext(request), changeReq));
            //禅道1507准入改造，变更这里统一做伙伴信息变更工作流，不再走准入工作流审批。
            wflType.add(CHANGE_WFL_TYPE);
            responseData.setRows(wflType);
        } catch (Exception e) {
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }

        return responseData;
    }
}