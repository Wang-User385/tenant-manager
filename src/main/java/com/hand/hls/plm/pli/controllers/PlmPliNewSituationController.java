package com.hand.hls.plm.pli.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.PlmPliNewSituation;
import com.hand.hls.plm.pli.service.PlmPliNewSituationService;
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
public class PlmPliNewSituationController extends BaseController {

@Autowired
private PlmPliNewSituationService service;


@RequestMapping(value = "/plm/pli/new/situation/query")
@ResponseBody
public ResponseData query(PlmPliNewSituation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    return new ResponseData(service.select(requestContext,dto,page,pageSize));
}

@RequestMapping(value = "/plm/pli/new/situation/submit")
@ResponseBody
public ResponseData update(@RequestBody List<PlmPliNewSituation> dto, BindingResult result, HttpServletRequest request){
getValidator().validate(dto, result);
if (result.hasErrors()) {
ResponseData responseData = new ResponseData(false);
responseData.setMessage(getErrorMessage(result, request));
return responseData;
}
    IRequest requestCtx = createRequestContext(request);
    return new ResponseData(service.batchUpdate(requestCtx, dto));
}

@RequestMapping(value = "/plm/pli/new/situation/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request, @RequestBody List<PlmPliNewSituation> dto){
    service.batchDelete(dto);
    return new ResponseData();
}
}