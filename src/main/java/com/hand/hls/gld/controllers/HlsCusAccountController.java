//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.gld.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.gld.dto.Account;
import com.hand.hls.gld.dto.HlsCusAccount;
import com.hand.hls.gld.exception.AccountCodeException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hand.hls.gld.service.IHlsCusAccountService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HlsCusAccountController extends BaseController {
    @Autowired
    private IHlsCusAccountService service;

    public HlsCusAccountController() {
    }

    @RequestMapping({"/hls/gld/account/query"})
    @ResponseBody
    public ResponseData queryTest(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAccount metadataRelation = (HlsCusAccount)param.toJavaObject(HlsCusAccount.class);
        IRequest requestCtx = this.createRequestContext(request);
        List<HlsCusAccount> list = this.service.queryModify(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping({"/hls/gld/account/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, HttpSession session) throws AccountCodeException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusAccount> dtos = parameter.toJavaList(HlsCusAccount.class);
        ResponseData rd = null;
        IRequest requestCtx = null;
        List<HlsCusAccount> list = this.service.queryAccountCode();
        Iterator var9 = list.iterator();

        while(var9.hasNext()) {
            HlsCusAccount queryAccount = (HlsCusAccount)var9.next();
            Iterator var11 = dtos.iterator();

            while(var11.hasNext()) {
                HlsCusAccount hlsCusAccount = (HlsCusAccount)var11.next();
                if(queryAccount.getAccountCode().equals(hlsCusAccount.getAccountCode()) && queryAccount.getAccountName().equals(hlsCusAccount.getAccountName()) && !(queryAccount.getAccountId().equals(hlsCusAccount.getAccountId()))) {
                    throw new AccountCodeException("科目名称的科目代码不能重复!");
                }
            }
        }

        requestCtx = this.createRequestContext(request);
        return new ResponseData(this.service.batchUpdate(requestCtx, dtos));
    }

    @RequestMapping({"/hls/gld/account/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<HlsCusAccount> dto = param.toJavaList(HlsCusAccount.class);
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        this.service.batchDelete(dto);
        return new ResponseData(dto);
    }


}
