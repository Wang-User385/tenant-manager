package com.hand.hls.ecif.controllers;

import com.hand.hls.utils.DocumentValidate;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;
import com.hand.hls.ecif.service.HlsCusEcifBpMasterChangeService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsCusEcifBpMasterChangeController extends BaseController{

    @Autowired
    private HlsCusEcifBpMasterChangeService service;



    @RequestMapping(value = "/hls/ws/ecif/bp/master/change/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusEcifBpMasterChange dto = param.toJavaObject(HlsCusEcifBpMasterChange.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/ws/ecif/bp/master/change/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusEcifBpMasterChange> list = param.toJavaList(HlsCusEcifBpMasterChange.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/ws/ecif/bp/master/change/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusEcifBpMasterChange> dto = parameter.toJavaList(HlsCusEcifBpMasterChange.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


        /**
         * @Description:客户变更明细数据创建
         * @Author: Wangchao
         * @Date: Created om 10:12 2020/4/27
         */
        @RequestMapping(value = "/hls/ws/ecif/bp/master/change/create")
        @ResponseBody
        public ResponseData ecifBpMasterChange(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request)throws Exception {
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusEcifBpMasterChange dto = param.toJavaObject(HlsCusEcifBpMasterChange.class);


            HlsCusEcifBpMasterChange head = new HlsCusEcifBpMasterChange();
            head.setBpId(dto.getBpId());
            head.setWflStatus("APPROVING");

            //校验单据状态
            List<HlsCusEcifBpMasterChange> headList = service.select(requestCtx, head,1,100);
            List<String> statusList = new ArrayList<>();
            //存在审批中状态的不能进行创建
            statusList.add(DocumentValidate.NEW);
            statusList.add(DocumentValidate.APPROVED_RETURN);
            statusList.add(DocumentValidate.CANCEL);
            statusList.add(DocumentValidate.REJECTED);
            if(headList.size()>0){
                DocumentValidate.statusValidate("APPROVING", statusList);
            }



            List<HlsCusEcifBpMasterChange> list = new ArrayList<>();
            list.add(service.ecifBpMasterChange(requestCtx, dto));
            return new ResponseData(list);
        }



        /**
         * @Description:客户变更工作流提交
         * @Author: Wangchao
         * @Date: Created om 10:12 2020/4/27
         */
        @RequestMapping(value = "/hls/ecif/bp/master/change/submit/wfl")
        @ResponseBody
        public ResponseData ecifBpMasterSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws Exception {
            IRequest requestCtx = createRequestContext(request);
//            JSONObject param = (JSONObject) requestData.get("parameter");
//            HlsCusEcifBpMasterChange dto = param.toJavaObject(HlsCusEcifBpMasterChange.class);

            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCusEcifBpMasterChange>  dtoList = param.toJavaList(HlsCusEcifBpMasterChange.class);

            HlsCusEcifBpMasterChange head = new HlsCusEcifBpMasterChange();
            head.setEcifChangeId(dtoList.get(0).getEcifChangeId());

            //校验单据状态
            head = service.selectByPrimaryKey(requestCtx, head);
            List<String> statusList = new ArrayList<>();
            //以下状态才可以提交审批
            statusList.add(DocumentValidate.NEW);
            statusList.add(DocumentValidate.APPROVED_RETURN);
            statusList.add(DocumentValidate.CANCEL);
            statusList.add(DocumentValidate.REJECTED);
            DocumentValidate.statusValidate(head.getWflStatus(), statusList);

            HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange = service.ecifBpMasterSubmitWfl(requestCtx, dtoList.get(0));
            List<HlsCusEcifBpMasterChange> list = new ArrayList<>();
            if (hlsCusEcifBpMasterChange != null) {
                list.add(hlsCusEcifBpMasterChange);
            }
            return new ResponseData(list);
        }


        /**
         * @Description:客户变更取消
         * @Author: Wangchao
         * @Date: Created om 10:12 2020/4/28
         */
        @RequestMapping(value = "/hls/ecif/bp/master/change/cancel")
        @ResponseBody
        public ResponseData ecifBpMasterChangeCancel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusEcifBpMasterChange dto = param.toJavaObject(HlsCusEcifBpMasterChange.class);
            HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange = service.ecifBpMasterChangeCancel(requestCtx, dto);
            List<HlsCusEcifBpMasterChange> list = new ArrayList<>();
            if (hlsCusEcifBpMasterChange != null) {
                list.add(hlsCusEcifBpMasterChange);
            }
            return new ResponseData(list);
        }



        /**
         * @Description:客户上报总行版本信息查询
         * @Author: Wangchao
         * @Date: Created om 10:12 2020/4/28
         */
        @RequestMapping(value="/hls/ecif/bp/master/change/history")
        @ResponseBody
        public ResponseData ecifHistoryQuery(HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
            List<HlsCusEcifBpMasterChange> list=service.ecifHistoryQuery(hlsCusEcifBpMasterChange,page,pagesize);
            return new ResponseData(list);
        }

    }