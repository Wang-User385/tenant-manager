package com.hand.hls.plm.pli.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.PliLeaseItem;
import com.hand.hls.plm.pli.dto.PlmPliCheckItem;
import com.hand.hls.plm.pli.dto.PlmPliContract;
import com.hand.hls.plm.pli.service.IPliLeaseItemService;
import com.hand.hls.plm.pli.service.IPlmPliContractService;
import com.hand.hls.plm.pli.service.PlmPliCheckItemService;
import com.hand.hls.plm.service.HlsCusShipsService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class PlmPliContractController extends BaseController {

    @Autowired
    private IPlmPliContractService service;
    @Autowired
    private HlsCusShipsService shipsService;

    @RequestMapping(value = "/plm/pli/contract/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param  =(JSONObject) requestData.get("parameter");
        PlmPliContract dto = param.toJavaObject(PlmPliContract.class);

        return new ResponseData(service.selectPlmPliContractData(requestContext, dto, page, pageSize));
    }

    @Autowired
    PlmPliCheckItemService plmPliCheckItemService;

    @RequestMapping(value = "/plm/pli/contract/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<PlmPliContract> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.contractSelect(requestCtx,dto));
    }

    @Autowired
    IPliLeaseItemService pliLeaseItemService;

    @RequestMapping(value = "/plm/pli/contract/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PlmPliContract> dto) {
        IRequest iRequest = createRequestContext(request);
        for (PlmPliContract con : dto) {
            PliLeaseItem pliLeaseItem = new PliLeaseItem();
            pliLeaseItem.setContractId(con.getContractId());
            List<PliLeaseItem> pliLeaseItemList = pliLeaseItemService.select(iRequest, pliLeaseItem, 1, 99999);
            //删除租赁物清单
            for (PliLeaseItem p : pliLeaseItemList) {
                PlmPliCheckItem plmPliCheckItem = new PlmPliCheckItem();
                plmPliCheckItem.set__status("delete");
                plmPliCheckItem.setPlmPliItemId(p.getPlmPliItemId());
                plmPliCheckItemService.delete(plmPliCheckItem);
            }
            //删除租赁物
            pliLeaseItemService.batchDelete(pliLeaseItemList);
            //删除船舶信息
            shipsService.deleteByPliContractId(con.getPliContractId());
        }
        //删除合同
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/plm/pli/contract/other/all/query")
    @ResponseBody
    public ResponseData queryOtherAll(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PlmPliContract dto = param.toJavaObject(PlmPliContract.class);

        return new ResponseData(service.selectOtherAll(dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/pli/contract/other/all/project/query")
    @ResponseBody
    public ResponseData queryOtherAllProject(PlmPliContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectOtherAllProject(dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/pli/contract/meeting/risk/description/query")
    @ResponseBody
    public ResponseData selectMeetingRiskDescription(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        PlmPliContract dto = param.toJavaObject(PlmPliContract.class);
        return new ResponseData(service.selectMeetingRiskDescription(dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/pli/contract/other/all/add")
    @ResponseBody
    public ResponseData addOtherAll(@RequestBody(required = false) List<PlmPliContract> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.addOtherAll(dto));
    }

    /**
     * 五级分类导出
     * @param dto
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/plm/pli/contract/other/export")
    @ResponseBody
    public ResponseData exportBankPredict(PlmPliContract dto, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestContext = createRequestContext(request);
        service.exportFivePlmPliContract(requestContext,request,response, dto);
        return new ResponseData();
    }
}
