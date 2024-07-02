//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import javax.servlet.http.HttpServletRequest;

import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import com.hand.hls.bp.service.HlsBpMasterAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HlsBpMasterAddressController extends BaseController {
    @Autowired
    private HlsBpMasterAddressService service;

    public HlsBpMasterAddressController() {
    }

    @RequestMapping({"/hls/bp/address/query"})
    @ResponseBody
    public ResponseData query(HlsCusBpMasterAddress bpMasterAddress, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.selectAll(requestContext, bpMasterAddress, page, pagesize));
    }

}
