package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import com.hand.hls.csh.service.HlsCusCshBankAccountService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Description:重写收付管理首页查询
 * @Author: wty
 * @Date: Created in 13:16 2018/5/14
 */

@Controller
public class HlsCusCshBankAccountController extends BaseController {

    @Autowired
    private HlsCusCshBankAccountService service;


    @RequestMapping(value = "/hls/cus/csh/bank/account/query")
    @ResponseBody
    public ResponseData contractHomeThirdQuery(@ModelAttribute("_request_data") LeafRequestData requestData, final HttpSession session, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshBankAccount hlsCusCshBankAccount = param.toJavaObject(HlsCusCshBankAccount.class);
        Long companyId = Long.valueOf(String.valueOf(session.getAttribute("companyId")));
        return new ResponseData(service.selectCshBankAccountLov(hlsCusCshBankAccount, pagenum, pagesize));
    }
}
