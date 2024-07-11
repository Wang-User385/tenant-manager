package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.service.ProjectLeaseItemDetailService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lipan
 * @date 2024/7/09 - 14:17
 */

@Controller
public class ProjectLeaseItemDetailController extends BaseController {
    @Autowired
    private ProjectLeaseItemDetailService projectLeaseItemDetailService;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    public ProjectLeaseItemDetailController() {
    }


    @RequestMapping({"/prj/leaseItem/detail/info/query"})
    @ResponseBody
    public ResponseData queryPrjLeaseItemDetail(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectLeaseItemId = param.getLong("project_lease_item_id");
        HlsCusPrjProjectLeaseItem dto1 = param.toJavaObject(HlsCusPrjProjectLeaseItem.class);
        List<HlsCusPrjProjectLeaseItem> list = hlsCusPrjProjectLeaseItemMapper.queryPrjProjectLeaseItem(dto1);
        return new ResponseData(list);
    }
    @RequestMapping({"/prj/leaseItem/detail/sales/query"})
    @ResponseBody
    public ResponseData queryPrjLeaseItemSales(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectLeaseItemId = param.getLong("project_lease_item_id");
        List<PrjProjectLeaseItemSales> prjProjectLeaseItemSales = projectLeaseItemDetailService.queryPrjLeaseItemSales(projectLeaseItemId);
        return new ResponseData(prjProjectLeaseItemSales);
    }
    @RequestMapping({"/prj/leaseItem/detail/insurance/query"})
    @ResponseBody
    public ResponseData queryPrjLeaseItemInsurance(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectLeaseItemId = param.getLong("project_lease_item_id");
        List<PrjLeaseItemInsurance> prjLeaseItemInsurances = projectLeaseItemDetailService.queryPrjLeaseItemInsurance(projectLeaseItemId);
        return new ResponseData(prjLeaseItemInsurances);
    }
    @RequestMapping({"/prj/leaseItem/detail/condition/query"})
    @ResponseBody
    public ResponseData queryPrjLeaseItemCondition(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectLeaseItemId = param.getLong("project_lease_item_id");
        List<PrjProjectLeaseItemCondition> prjProjectLeaseItemConditions = projectLeaseItemDetailService.queryPrjLeaseItemCondition(projectLeaseItemId);
        return new ResponseData(prjProjectLeaseItemConditions);
    }
    @RequestMapping({"/prj/leaseItem/detail/mortgage/query"})
    @ResponseBody
    public ResponseData queryPrjLeaseItemMortgage(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectLeaseItemId = param.getLong("project_lease_item_id");
        List<PrjProjectLeaseItemMortgage> prjProjectLeaseItemMortgages = projectLeaseItemDetailService.queryPrjLeaseItemMortgages(projectLeaseItemId);
        return new ResponseData(prjProjectLeaseItemMortgages);
    }

}
