//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.hand.hls.bp.service.HlsBpMasterRoleService;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HlsBpMasterRoleController extends BaseController {
    @Autowired
    private HlsBpMasterRoleService service;

    public HlsBpMasterRoleController() {
    }

    @RequestMapping({"/hls/bp/role/query"})
    @ResponseBody
    public ResponseData query(HlsBpMasterRole bpMasterRole, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        List<HlsBpMasterRole> datas = new ArrayList();
        if (!bpMasterRole.getBpType().toString().equals("") && bpMasterRole.getBpId().toString().equals("0")) {
            datas = this.service.queryRoleType(requestContext, bpMasterRole, page, pagesize);
        } else if (!bpMasterRole.getBpType().toString().equals("") && !bpMasterRole.getBpId().toString().equals("0") && !bpMasterRole.getBpId().toString().equals("0")) {
            datas = this.service.queryAll(requestContext, bpMasterRole, page, pagesize);
        }

        return new ResponseData((List)datas);
    }

}
