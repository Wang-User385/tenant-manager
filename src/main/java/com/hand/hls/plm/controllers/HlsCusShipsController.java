package com.hand.hls.plm.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.dto.HlsCusShips;
import com.hand.hls.plm.service.HlsCusShipsService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusShipsController extends BaseController {

    @Autowired
    private HlsCusShipsService service;


    @RequestMapping(value = "/plm/ships/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusShips dto = param.toJavaObject(HlsCusShips.class);

        return new ResponseData(service.selectShip(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/plm/ships/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusShips> dto, BindingResult result, HttpServletRequest request){
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/ships/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusShips> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}
