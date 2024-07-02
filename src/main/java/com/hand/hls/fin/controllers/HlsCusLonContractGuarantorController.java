package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractGuarantor;
import com.hand.hls.fin.service.HlsCusLonContractService;
import com.hand.hls.fin.service.IHlsCusLonContractGuarantorService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class HlsCusLonContractGuarantorController extends BaseController {

    @Autowired
    private IHlsCusLonContractGuarantorService service;

    /**
     * 查询担保人在其他合同是否有担保合同，有返回提示
     * @param requestData
     * @param request
     * @return
     */
    @RequestMapping(value = "/hlsLon/contract/company/find")
    @ResponseBody
    public ResponseData queryCompany(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                     HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        //System.out.println("合同编号：==================================》"+contractId);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLonContractGuarantor lonContractGuarantor = param.toJavaObject(HlsCusLonContractGuarantor.class);
        List<HlsCusLonContract> list = service.selectCompanyByBpId(requestContext, lonContractGuarantor);
        if(list == null){
            return new ResponseData();
        }
        return new ResponseData(list);
    }
}
