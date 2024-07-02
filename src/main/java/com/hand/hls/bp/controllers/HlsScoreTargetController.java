//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsScoreTarget;
import com.hand.hls.bp.service.IHlsScoreTargetService;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HlsScoreTargetController extends BaseController {
    @Autowired
    private IHlsScoreTargetService service;

    public HlsScoreTargetController() {
    }

    @RequestMapping({"/hls/score/target/query"})
    @ResponseBody
    public ResponseData query(HttpSession session, HlsScoreTarget dto, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        Map para = requestData.getParameter();
        if (para.get("scoreTargetCode") != null) {
            dto.setScoreTargetCode((String)para.get("scoreTargetCode"));
        }

        if (para.get("scoreTargetName") != null) {
            dto.setScoreTargetName((String)para.get("scoreTargetName"));
        }

        if (para.get("targetValueType") != null) {
            dto.setTargetValueType((String)para.get("targetValueType"));
        }

        IRequest requestContext = this.createRequestContext(request);
        Long companyId = (Long)session.getAttribute("companyId");
        dto.setCompanyId(companyId);
        return new ResponseData(this.service.query(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping({"/hls/score/target/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, HttpSession session, @ModelAttribute("_request_data") LeafRequestData requestData) throws TokenException {
        ResponseData rd = null;
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List dto = parameter.toJavaList(HlsScoreTarget.class);

        try {
            IRequest requestCtx = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(requestCtx);
            Long companyId = (Long)session.getAttribute("companyId");
            Iterator var9 = dto.iterator();

            while(var9.hasNext()) {
                HlsScoreTarget tar = (HlsScoreTarget)var9.next();
                if (companyId != null) {
                    tar.setCompanyId(companyId);
                }
            }

            return new ResponseData(this.service.batchUpdate(requestCtx, dto));
        } catch (Exception var11) {
            rd = new ResponseData(false);
            rd.setMessage("指标代码不可重复！");
            return rd;
        }
    }

    @RequestMapping({"/hls/score/target/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsScoreTarget> dto = parameter.toJavaList(HlsScoreTarget.class);
        this.service.deleteHl(dto);
        return new ResponseData(dto);
    }
}
