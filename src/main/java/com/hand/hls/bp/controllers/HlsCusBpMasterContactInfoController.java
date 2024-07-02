package com.hand.hls.bp.controllers;


import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;
import com.hand.hls.bp.service.HlsCusBpMasterContactInfoService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HlsCusBpMasterContactInfoController extends BaseController {

    @Autowired
    private HlsCusBpMasterContactInfoService service;

    @RequestMapping(value = "/hls/bp/contact/info/all/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData creditLineInfoAllQuery(@ModelAttribute("_request_data") LeafRequestData requestData,HlsCusBpMasterContactInfo bpMasterContactInfo,
                                               @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMasterContactInfo dto = param.toJavaObject(HlsCusBpMasterContactInfo.class);

        if(bpMasterContactInfo.getBpId()!=null){
            dto.setBpId(bpMasterContactInfo.getBpId());
        }

        List<HlsCusBpMasterContactInfo> list = service.queryAll(dto, pagenum, pagesize);
        return new ResponseData(list);
    }


}
