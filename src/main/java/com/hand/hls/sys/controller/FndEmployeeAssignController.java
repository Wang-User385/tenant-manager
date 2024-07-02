package com.hand.hls.sys.controller;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.sys.dto.FndEmployee;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/14 - 9:04
 */
public class FndEmployeeAssignController  extends BaseController  {

    @Autowired
    private FndEmployeeMapper mapper;

    @PostMapping({"/fnd/employee/queryEmployeesAssignForLov"})
    public ResponseData queryEmployeesForLov(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, FndEmployee emp, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest iRequest = this.createRequestContext(request);
        if (requestData != null) {
            Map map = (Map)requestData.get("parameter");
            if (map != null) {
                emp.setName((String)map.getOrDefault("name", (Object)null));
                emp.setUnitName((String)map.getOrDefault("unit_name", (Object)null));
                emp.setPositionName((String)map.getOrDefault("position_name", (Object)null));
                page = Integer.parseInt(request.getParameter("pagenum"));
            }
        }

//        return this.mapper.queryEmployeesForLov(emp);
//        return new ResponseData(this.service.queryEmployeesForLov(iRequest, emp, page, pagesize));
        return new ResponseData(this.mapper.queryEmployeesForLov(emp));
    }
}
