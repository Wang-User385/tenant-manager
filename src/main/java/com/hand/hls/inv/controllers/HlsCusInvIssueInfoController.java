package com.hand.hls.inv.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import com.hand.hls.inv.dto.HlsCusInvIssueInfo;
import com.hand.hls.inv.service.HlsCusInvIssueInfoService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusInvIssueInfoController extends BaseController {

    @Autowired
    private HlsCusInvIssueInfoService service;
    @Autowired
    private HlsCusAbsProductService hlsCusAbsProductService;


    //HlsCusInvIssueInfo dto,
    @RequestMapping(value = "/inv/issue/info/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusInvIssueInfo dto = param.toJavaObject(HlsCusInvIssueInfo.class);
        List<HlsCusInvIssueInfo> list = service.select(requestContext, dto, page, pageSize);
        for (HlsCusInvIssueInfo dt : list) {
            HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
            hlsCusAbsProduct.setPublishNumber(dt.getIssueInfoId().toString());
            List<HlsCusAbsProduct> absList = hlsCusAbsProductService.select(requestContext, hlsCusAbsProduct, 1, 1000);
            if (absList.size() > 0) {
                dt.setSelectedFlag("Y");
            } else {
                dt.setSelectedFlag("N");
            }
        }
        return new ResponseData(list);
    }

    @RequestMapping(value = "/inv/issue/info/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusInvIssueInfo> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.issueInfoSave(requestCtx, dto));
    }

    @RequestMapping(value = "/inv/issue/info/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusInvIssueInfo> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}