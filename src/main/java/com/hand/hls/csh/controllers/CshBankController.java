//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusCshBank;
import com.hand.hls.csh.service.CshBankService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CshBankController extends BaseController {
    @Autowired
    private CshBankService service;

    public CshBankController() {
    }

    @RequestMapping({"/csh/bank/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusCshBank metadataRelation = (HlsCusCshBank)param.toJavaObject(HlsCusCshBank.class);
        PageHelper.startPage(pagenum, pagesize);
        IRequest requestCtx = this.createRequestContext(request);
        List<HlsCusCshBank> list = this.service.queryCshBank(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping({"/csh/bank/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, HttpSession session) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCshBank> metadataRelations = parameter.toJavaList(HlsCusCshBank.class);
        ResponseData rd = null;
        IRequest requestCtx = null;
        requestCtx = this.createRequestContext(request);
        return new ResponseData(this.service.batchUpdate(requestCtx, metadataRelations));
    }

    @RequestMapping({"/csh/bank/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<HlsCusCshBank> metadataRelations = param.toJavaList(HlsCusCshBank.class);
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        this.service.batchDelete(metadataRelations);
        return new ResponseData(metadataRelations);
    }
}
