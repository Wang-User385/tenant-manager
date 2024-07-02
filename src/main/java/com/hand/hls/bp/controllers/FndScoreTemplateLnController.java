//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.FndScoreTemplateLn;
import com.hand.hls.bp.service.IFndScoreTemplateLnService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FndScoreTemplateLnController extends BaseController {
    @Autowired
    private IFndScoreTemplateLnService service;

    public FndScoreTemplateLnController() {
    }

    @RequestMapping({"/fnd/score/template/ln/query"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndScoreTemplateLn dto = (FndScoreTemplateLn)param.toJavaObject(FndScoreTemplateLn.class);
        return new ResponseData(this.service.select(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping({"/fnd/score/template/ln/querylevelone"})
    @ResponseBody
    public ResponseData querylevleone(HttpServletRequest request, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndScoreTemplateLn dto = (FndScoreTemplateLn)param.toJavaObject(FndScoreTemplateLn.class);
        return new ResponseData(this.service.selectLevelOne(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping({"/fnd/score/template/ln/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTemplateLn> dtos = parameter.toJavaList(FndScoreTemplateLn.class);
        this.getValidator().validate(dtos, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            return new ResponseData(this.service.batchUpdate(iRequest, dtos));
        }
    }

    @RequestMapping({"/fnd/score/template/ln/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTemplateLn> dtos = parameter.toJavaList(FndScoreTemplateLn.class);
        this.service.batchDelete(dtos);
        return new ResponseData(dtos);
    }

    @RequestMapping({"/fnd/score/result/dtl/byHdId"})
    @ResponseBody
    public ResponseData selectscoreTemplateLnId(HttpServletRequest request, Long scoreTemplateHdId) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        List<FndScoreTemplateLn> list = this.service.selectLnRoot(scoreTemplateHdId);
        return new ResponseData(list);
    }

    @RequestMapping({"/fnd/score/result/dtl/queryByLnId"})
    @ResponseBody
    public ResponseData query(FndScoreTemplateLn dto, HttpServletRequest request, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        if (dto.getScoreTemplateLnId() != null && dto.getScoreResultId() != null) {
            List<FndScoreTemplateLn> listGather = this.service.queryScoreTemplateLn(dto);
            return new ResponseData(listGather);
        } else {
            return new ResponseData(false, "请求参数有误");
        }
    }



    @RequestMapping({"/fnd/score/result/dtl/queryByLnIds"})
    @ResponseBody
    public ResponseData queryS(@RequestParam("scoreTemplateLnId")Long [] scoreTemplateLnId,
                               HttpServletRequest request, @RequestParam("scoreResultId")Long scoreResultId,
                               @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        if (scoreTemplateLnId.length>0 && scoreResultId != null) {
            List<FndScoreTemplateLn> listGather = this.service.queryScoreTemplateLn2(scoreTemplateLnId,scoreResultId);
            return new ResponseData(listGather);
        } else {
            return new ResponseData(false, "请求参数有误");
        }
    }



    @RequestMapping({"/fnd/score/result/dtl/countScoreByLnId"})
    @ResponseBody
    public Map countScore(FndScoreTemplateLn dto, HttpServletRequest request, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        IRequest iRequest = this.createRequestContext(request);
        Map map = new HashMap();
        RequestHelper.setCurrentRequest(iRequest);
        if (dto.getScoreTemplateLnId() != null && dto.getScoreResultId() != null) {
            List<FndScoreTemplateLn> listGather = this.service.queryScoreTemplateLn(dto);
            Double score = 0.0;
            for(FndScoreTemplateLn fndScoreTemplateLn:listGather){
                score+=fndScoreTemplateLn.getTargetScore();
            }

            map.put("score",score);
            return map;
        } else {
            map.put("error", "请求参数有误");
            return map;
        }
    }
}
