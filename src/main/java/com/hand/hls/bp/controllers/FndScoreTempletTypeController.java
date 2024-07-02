//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.FndScoreTempletType;
import com.hand.hls.bp.service.IFndScoreTempletTypeService;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/fnd/score/template/type"})
public class FndScoreTempletTypeController extends BaseController {
    @Autowired
    private IFndScoreTempletTypeService service;

    public FndScoreTempletTypeController() {
    }

    @RequestMapping(
            value = {"/query"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData fuzzQuery(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndScoreTempletType company = (FndScoreTempletType)param.toJavaObject(FndScoreTempletType.class);
        IRequest requestCtx = this.createRequestContext(request);
        List<FndScoreTempletType> list = this.service.query(requestCtx, company, pagenum, pagesize);
        return new ResponseData(list);
    }

    @PostMapping({"/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, HttpSession session, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws TokenException, BaseException {
        JSONArray para = (JSONArray)requestData.get("parameter");
        List<FndScoreTempletType> listFndScoreTempletType = para.toJavaList(FndScoreTempletType.class);
        this.getValidator().validate(listFndScoreTempletType, result);
        ResponseData rd;
        if (result.hasErrors()) {
            rd = new ResponseData(false);
            rd.setMessage(this.getErrorMessage(result, request));
            return rd;
        } else {
            rd = null;
            IRequest requestCtx = null;

            try {
                requestCtx = this.createRequestContext(request);
                Long companyId = (Long)session.getAttribute("companyId");
                Iterator var10 = listFndScoreTempletType.iterator();

                while(var10.hasNext()) {
                    FndScoreTempletType tar = (FndScoreTempletType)var10.next();
                    if (companyId != null) {
                        tar.setCompanyId(companyId);
                    }
                }

                return new ResponseData(this.service.batchUpdate(requestCtx, listFndScoreTempletType));
            } catch (Exception var12) {
                rd = new ResponseData(false);
                rd.setMessage("评分指标类型模型代码 不可重复！");
                return rd;
            }
        }
    }

    @RequestMapping({"/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws BaseException {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<FndScoreTempletType> dto = param.toJavaList(FndScoreTempletType.class);
        IRequest requestContext = this.createRequestContext(request);
        this.service.batchDelete(requestContext, dto);
        return new ResponseData(dto);
    }
}
