//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.FndScoreTargetValues;
import com.hand.hls.bp.service.IFndScoreTargetValuesService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FndScoreTargetValuesController extends BaseController {
    @Autowired
    private IFndScoreTargetValuesService service;

    public FndScoreTargetValuesController() {
    }

    @RequestMapping({"/fnd/score/target/values/query"})
    @ResponseBody
    public ResponseData query(FndScoreTargetValues dto, @RequestParam(defaultValue = "1") int pagenum, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        Map para = requestData.getParameter();
        if (para.get("score_target_id") != null) {
            dto.setScoreTargetId(Long.parseLong((String)para.get("score_target_id")));
        }

        JSONObject param = (JSONObject)requestData.get("parameter");
        dto = (FndScoreTargetValues)param.toJavaObject(FndScoreTargetValues.class);
        dto.setSortorder("asc");
        dto.setSortname("line_number");
        return new ResponseData(this.service.select(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping({"/fnd/score/target/values/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTargetValues> dto = parameter.toJavaList(FndScoreTargetValues.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping({"/fnd/score/target/values/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTargetValues> dto = parameter.toJavaList(FndScoreTargetValues.class);
        this.service.batchDelete(dto);
        return new ResponseData(dto);
    }



    @RequestMapping({"/fnd/score/target/values/queryAll"})
    @ResponseBody
    public ResponseData findScoreTargetValueList(FndScoreTargetValues dto, @RequestParam(defaultValue = "1") int pagenum, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        Map para = requestData.getParameter();
        if (para.get("score_target_id") != null) {
            dto.setScoreTargetId(Long.parseLong((String)para.get("score_target_id")));
        }

        JSONObject param = (JSONObject)requestData.get("parameter");
        dto = (FndScoreTargetValues)param.toJavaObject(FndScoreTargetValues.class);
        dto.setSortorder("asc");
        dto.setSortname("line_number");
        return new ResponseData(this.service.findScoreTargetValueList(requestContext, dto, pagenum, pageSize));
    }

}
