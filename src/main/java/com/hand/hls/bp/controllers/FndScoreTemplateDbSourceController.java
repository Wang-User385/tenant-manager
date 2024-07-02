//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.FndScoreTemplateDbSource;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.service.IFndScoreTemplateDbSourceService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FndScoreTemplateDbSourceController extends BaseController {
    @Autowired
    private IFndScoreTemplateDbSourceService service;

    public FndScoreTemplateDbSourceController() {
    }

    @RequestMapping({"/fnd/score/template/db/source/query"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        FndScoreTemplateDbSource dto = new FndScoreTemplateDbSource();
        Map para = requestData.getParameter();
        if (para.get("score_template_hd_id") != null) {
            dto.setScoreTemplateHdId(Long.parseLong(para.get("score_template_hd_id").toString()));
        }

        return new ResponseData(this.service.selectDbSourceName(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping({"/fnd/score/template/db/source/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTemplateDbSource> dtos = parameter.toJavaList(FndScoreTemplateDbSource.class);
        this.getValidator().validate(dtos, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            return new ResponseData(this.service.batchUpdate(iRequest, dtos));
        }
    }

    @RequestMapping({"/fnd/score/template/db/source/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTemplateDbSource> dtos = parameter.toJavaList(FndScoreTemplateDbSource.class);
        this.service.batchDelete(dtos);
        return new ResponseData(dtos);
    }

    @RequestMapping({"/fnd/score/template/db/source/combobox"})
    @ResponseBody
    public ResponseData combDs(HttpServletRequest request) {
        return new ResponseData(this.service.combDs());
    }

    @RequestMapping({"/fnd/score/template/db/source/queryByHId1"})
    @ResponseBody
    public ResponseData queryByHId(@RequestBody Map parameter, HttpServletRequest request, HttpSession session, String isFiscal) {
        IRequest requestContext = this.createRequestContext(request);
        if (parameter.get("SCORE_TEMPLATE_HD_ID") == null) {
            return new ResponseData(false, "请求参数有误");
        } else {
            if (session.getAttribute("companyId") != null) {
                parameter.put("COMPANY_ID", session.getAttribute("companyId"));
            }

            if (session.getAttribute("userId") != null) {
                parameter.put("SCORE_USER_ID", session.getAttribute("userId"));
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            parameter.put("SCORE_DATE", df.format(new Date()));
            List<Long> list = new ArrayList();
            parameter.put("is_fiscal",isFiscal );
            list.add(this.service.fndScoreTemplateDbSource(requestContext, Long.parseLong(parameter.get("SCORE_TEMPLATE_HD_ID").toString()), parameter));
            return new ResponseData(list);
        }
    }
}
