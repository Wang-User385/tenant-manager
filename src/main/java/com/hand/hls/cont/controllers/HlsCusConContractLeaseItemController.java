package com.hand.hls.cont.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;
import com.hand.hls.cont.service.HlsCusConContractLeaseItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusConContractLeaseItemController extends BaseController {

    @Autowired
    private HlsCusConContractLeaseItemService service;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;


    @RequestMapping(value = "/ct/con/contract/lease/item/query")
    @ResponseBody
    public ResponseData query(HlsCusConContractLeaseItem dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusConContractLeaseItem> list = service.select(requestContext, dto, page, pageSize);
        for (HlsCusConContractLeaseItem dt : list) {
            HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
            hlsCusBpMaster.setBpId(dt.getBpIdManufacturer());
            dt.setEvaluationBpName(hlsCusBpMasterService.selectByPrimaryKey(requestContext, hlsCusBpMaster).getBpName());
        }
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/con/contract/lease/item/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusConContractLeaseItem> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/con/contract/lease/item/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusConContractLeaseItem> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}