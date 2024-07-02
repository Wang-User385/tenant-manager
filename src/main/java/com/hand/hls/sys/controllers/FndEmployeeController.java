package com.hand.hls.sys.controllers;

/**
 * @author 胡兴恒
 * @Time 2020-05-21 10:30
 */


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.sys.dto.FndEmployee;
import com.hand.hls.sys.service.IFndEmployeeService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class FndEmployeeController extends BaseController {
    @Autowired
    private IFndEmployeeService service;

    public FndEmployeeController() {
    }

    @RequestMapping({"/fnd/employee/query"})
    @ResponseBody
    public ResponseData query(@RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        PageHelper.startPage(pagenum, pagesize);
        FndEmployee employee = new FndEmployee();
        if (requestData != null) {
            Map para = requestData.getParameter();
            if (para.get("name") != null) {
                employee.setName(para.get("name").toString());
            }

            if (para.get("employee_code") != null) {
                employee.setEmployeeCode(para.get("employee_code").toString());
            }

            if (para.get("employeeCode") != null) {
                employee.setEmployeeCode(para.get("employeeCode").toString());
            }
            if (para.get("unitName") != null){
                employee.setUnitName(para.get("unitName").toString());
            }
        }

        IRequest requestCtx = this.createRequestContext(request);
        return new ResponseData(this.service.selectEmployee(requestCtx, employee));
    }

    @RequestMapping({"/fnd/employee/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndEmployee> employees = parameter.toJavaList(FndEmployee.class);
        this.getValidator().validate(employees, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest requestCtx = this.createRequestContext(request);
            return new ResponseData(this.service.batchUpdate(requestCtx, employees));
        }
    }

    @RequestMapping({"/fnd/employee/remove"})
    @ResponseBody
    public ResponseData delete(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndEmployee> employees = parameter.toJavaList(FndEmployee.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        this.service.batchDelete(employees);
        return new ResponseData(employees);
    }

    @RequestMapping({"/fnd/employee/prj_metting_judge/queryForLov"})
    @ResponseBody
    public ResponseData queryForLov(@RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = this.createRequestContext(request);
        PageHelper.startPage(pagenum, pagesize);
        FndEmployee employee = new FndEmployee();
        new FndEmployee();
        if (requestData != null) {
            Map para = requestData.getParameter();
            if (para.get("name") != null) {
                employee.setName(para.get("name").toString());
            }

            if (para.get("employee_code") != null) {
                employee.setEmployeeCode(para.get("employee_code").toString());
            }

            if (para.get("employeeCode") != null) {
                employee.setEmployeeCode(para.get("employeeCode").toString());
            }
        }

        return new ResponseData(this.service.queryForLov(requestCtx, employee));
    }

    @RequestMapping({"/fnd/employee/position/manager"})
    @ResponseBody
    public ResponseData positionManagerQuery(@RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request, HttpSession session, @ModelAttribute("_request_data") LeafRequestData requestData) {
        PageHelper.startPage(pagenum, pagesize);
        FndEmployee employee = new FndEmployee();
        if (requestData != null) {
            Map para = requestData.getParameter();
            if (para.get("employee_id") != null) {
                employee.setEmployeeId(Long.valueOf(para.get("employee_id").toString()));
            }
        }

        IRequest requestCtx = this.createRequestContext(request);
        Long companyId = (Long)session.getAttribute("companyId");
        Long employeeId = (Long)session.getAttribute("employeeId");
        employee.setCompanyId(companyId);
        employee.setEmployeeId(employeeId);
        return new ResponseData(this.service.selectPositionManager(requestCtx, employee));
    }

    @PostMapping({"/fnd/employee/queryEmployeesForLov"})
    public ResponseData queryEmployeesForLov(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, FndEmployee emp, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest iRequest = this.createRequestContext(request);
        if (requestData != null) {
            Map map = (Map)requestData.get("parameter");
            if (map != null) {
                emp.setName((String)map.getOrDefault("name", (Object)null));
                emp.setUnitName((String)map.getOrDefault("unit_name", (Object)null));
                page = Integer.parseInt(request.getParameter("pagenum"));
            }
        }

        return new ResponseData(this.service.queryEmployeesForLov(iRequest, emp, page, pagesize));
    }

    @PostMapping({"/fnd/employee/queryEmployeesForLovForAppoint"})
    public ResponseData queryEmployeesForLovForAppoint(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, FndEmployee emp, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest iRequest = this.createRequestContext(request);
        String unitName="";
        if(iRequest.getAttributeMap().get("unit_name") != null){
             unitName = iRequest.getAttributeMap().get("unit_name").toString();
            if(unitName != null){
                emp.setUnitName(unitName);
            }
        }

        if(iRequest.getAttributeMap().get("position_name") != null){
            String positionName = iRequest.getAttributeMap().get("position_name").toString();
            if(positionName != null){
                emp.setPositionName(positionName);
            }
        }

        if (requestData != null) {
            Map map = (Map)requestData.get("parameter");
            if (map != null) {
                emp.setName((String)map.getOrDefault("name", (Object)null));
                emp.setUnitName((String)map.getOrDefault("unitName", (Object)unitName));
                if(map.get("unit_name") != null){
                    emp.setUnitName(map.get("unit_name").toString());
                }
                page = Integer.parseInt(request.getParameter("pagenum"));
            }
        }

        return new ResponseData(this.service.queryEmployeesForLov(iRequest, emp, page, pagesize));
    }

    @RequestMapping({"/fnd/employee/selectAllEmployeeForFct"})
    @ResponseBody
    public ResponseData selectAllEmployeeForFct(@RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request, HlsEmployee hlsEmployee) {
        this.createRequestContext(request);
        Map leafRequestData = (Map)JSON.parseObject(request.getParameter("_request_data"), Map.class);
        Map<String, String> parameter = (Map)leafRequestData.get("parameter");
        if (StringUtils.isNotEmpty((String)parameter.get("employeeCode"))) {
            hlsEmployee.setEmployeeCode((String)parameter.get("employeeCode"));
        }

        if (StringUtils.isNotEmpty((String)parameter.get("name"))) {
            hlsEmployee.setName((String)parameter.get("name"));
        }

        return new ResponseData(this.service.selectAllEmployeeForFct(hlsEmployee, pagenum, pagesize));
    }
}

