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
import com.hand.hls.bp.dto.FndScoreTemplateHd;
import com.hand.hls.bp.service.IFndScoreTemplateHdService;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FndScoreTemplateHdController extends BaseController {
    @Autowired
    private IFndScoreTemplateHdService service;

    public FndScoreTemplateHdController() {
    }

    @RequestMapping({"/prj/score/template/hd/query"})
    @ResponseBody
    public ResponseData query(FndScoreTemplateHd dto, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return StringUtils.isBlank(dto.getEnabledFlag()) ? new ResponseData(false, "请求参数有误。") : new ResponseData(this.service.selectList(dto));
    }

    @RequestMapping({"/fnd/score/template/hd/query"})
    @ResponseBody
    public ResponseData query(FndScoreTemplateHd dto, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request, HttpSession session, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestContext = this.createRequestContext(request);
        Map para = requestData.getParameter();
        if (para.get("score_template_hd_code") != null) {
            dto.setScoreTemplateHdCode(para.get("score_template_hd_code").toString());
        }

        if (para.get("score_template_hd_name") != null) {
            dto.setScoreTemplateHdName(para.get("score_template_hd_name").toString());
        }

        if (para.get("score_template_type_id") != null) {
            dto.setScoreTemplateTypeId(Long.parseLong(para.get("score_template_type_id").toString()));
        }

        if (para.get("calc_method") != null) {
            dto.setCalcMethod(para.get("calc_method").toString());
        }

        return new ResponseData(this.service.query(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping({"/fnd/score/template/hd/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, HttpSession session, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTemplateHd> dto = parameter.toJavaList(FndScoreTemplateHd.class);
        this.getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);

            try {
                Long companyId = (Long)session.getAttribute("companyId");
                Iterator var13 = dto.iterator();

                while(var13.hasNext()) {
                    FndScoreTemplateHd tar = (FndScoreTemplateHd)var13.next();
                    if (companyId != null) {
                        tar.setCompanyId(companyId);
                    }
                }

                return new ResponseData(this.service.batchUpdate(iRequest, dto));
            } catch (Exception var11) {
                ResponseData rd = new ResponseData(false);
                rd.setMessage("类型代码不可重复！");
                return rd;
            }
        }
    }

    @RequestMapping({"/fnd/score/template/hd/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreTemplateHd> dtos = parameter.toJavaList(FndScoreTemplateHd.class);
        this.service.deleteChild(dtos);
        return new ResponseData(dtos);
    }

    @RequestMapping({"/fnd/hd/column/ds"})
    @ResponseBody
    public ResponseData dbCloumnData(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndScoreTemplateHd dto = (FndScoreTemplateHd)param.toJavaObject(FndScoreTemplateHd.class);
        return new ResponseData(this.service.dbCloumnData(dto));
    }


    @RequestMapping(value = "/fnd/hd/source/findList")
    @ResponseBody
    public ResponseData selectQujianDataSourceList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FndScoreTemplateHd dto = param.toJavaObject(FndScoreTemplateHd.class);
        return new ResponseData(service.selectScoreTemplateHd());
    }
}
