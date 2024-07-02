package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusFctProject;
import com.hand.hls.fct.service.HlsCusFctProjectService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class HlsCusFctProjectController extends BaseController {

    @Autowired
    private HlsCusFctProjectService hlsCusFctProjectService;

    @RequestMapping(value = "/hls/cus/fct/manager/query")
    @ResponseBody
    public ResponseData managerQuery(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusFctProject hlsCusFctProject = param.toJavaObject(HlsCusFctProject.class);
        IRequest requestCtx = createRequestContext(request);
        Long companyId = requestCtx.getCompanyId();
        Long employeeId = Long.valueOf(requestCtx.getAttribute("employeeId").toString());
//        Long employeeId = 10001L;
        hlsCusFctProject.setCompanyId(companyId);
        hlsCusFctProject.setProjectAssistant(0L);
        hlsCusFctProject.setEmployeeId(employeeId);
        return new ResponseData(hlsCusFctProjectService.selectManagerByUnitId(hlsCusFctProject));
    }

}
