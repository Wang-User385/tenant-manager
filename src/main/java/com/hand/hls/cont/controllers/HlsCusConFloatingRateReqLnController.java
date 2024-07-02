//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.FloatingRateReqDetail;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.cont.service.ConFloatingRateReqLnService;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqLnService;
import com.hand.hls.cont.service.IFloatingRateReqDetailService;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/con/floating/rate"})
public class HlsCusConFloatingRateReqLnController extends BaseController {
    @Autowired
    private HlsCusConFloatingRateReqLnService service;
    @Autowired
    private IFloatingRateReqDetailService floatingRateReqDetailService;
    public static final String DOCUMENT_CATEGORY = "CON_FLOATING_RATE_REQ";

    public HlsCusConFloatingRateReqLnController() {
    }

    @RequestMapping({"/create/rateChange"})
    @ResponseBody
    public ResponseData createRateChange(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = this.createRequestContext(request);
        Long companyId = requestContext.getCompanyId();
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConFloatingRateReq floatingRateReq = param.toJavaObject(HlsCusConFloatingRateReq.class);
        floatingRateReq.setCompanyId(companyId);
        HlsCusConFloatingRateReq floatingRateChange = this.service.createFloatingRateChange(requestContext,floatingRateReq);
        List<HlsCusConFloatingRateReq> list = new ArrayList<>();
        list.add(floatingRateChange);
        return new ResponseData(list);

    }
    @RequestMapping(value = "baseRateSet/query")
    @ResponseBody
    public ResponseData queryAllocationUser(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                            HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusConFloatingRateReq dto = param.toJavaObject(HlsCusConFloatingRateReq.class);
        return new ResponseData(service.queryBaseRateSet(iRequest,dto, pagenum, pagesize));
    }
    @RequestMapping(value = "baseRateSet/delete")
    @ResponseBody
    public ResponseData deleteBaseRateSet(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                            HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusConFloatingRateReqLn dto = param.toJavaObject(HlsCusConFloatingRateReqLn.class);
        List<HlsCusConFloatingRateReqLn> list = new ArrayList<>();
        list.add(dto);
        if(list.size()>0){
            service.deleteFltReqLn(iRequest,list);
        }else{
            return new ResponseData(false, "删除失败!");
        }
        return new ResponseData();
    }
}
