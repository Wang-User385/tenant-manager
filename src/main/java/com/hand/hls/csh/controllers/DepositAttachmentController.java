package com.hand.hls.csh.controllers;

import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.DepositManageHd;
import com.hand.hls.csh.service.IDepositAttachmentService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class DepositAttachmentController extends BaseController{

    @Autowired
    private IDepositAttachmentService depositAttachmentService;


    /*保证金附件查询*/
    @RequestMapping(value = "/csh/depositDeposit/pageQuery")
    @ResponseBody
    public ResponseData pageQuery(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) throws ParseException {
        JSONObject param = (JSONObject) requestData.get("parameter");

        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }

        DepositManageHd dto = param.toJavaObject(DepositManageHd.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(depositAttachmentService.pageQuery(requestCtx, dto, pagenum, pagesize, sortName, sortOrder));
    }
}