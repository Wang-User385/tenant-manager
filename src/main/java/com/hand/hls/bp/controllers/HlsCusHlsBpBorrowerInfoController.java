package com.hand.hls.bp.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusHlsBpBorrowerInfo;
import com.hand.hls.bp.service.HlsCusHlsBpBorrowerInfoService;
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
public class HlsCusHlsBpBorrowerInfoController extends BaseController {

@Autowired
private HlsCusHlsBpBorrowerInfoService service;


@RequestMapping(value = "/hls/bp/borrower/info/query")
@ResponseBody
public ResponseData query(HlsCusHlsBpBorrowerInfo dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    return new ResponseData(service.queryAll(dto,requestContext,page,pageSize));
}

@RequestMapping(value = "/hls/bp/borrower/info/submit")
@ResponseBody
public ResponseData update(@RequestBody List<HlsCusHlsBpBorrowerInfo> dto, BindingResult result, HttpServletRequest request){
getValidator().validate(dto, result);
if (result.hasErrors()) {
ResponseData responseData = new ResponseData(false);
responseData.setMessage(getErrorMessage(result, request));
return responseData;
}
    IRequest requestCtx = createRequestContext(request);
    return new ResponseData(service.batchUpdate(requestCtx, dto));
}

@RequestMapping(value = "/hls/bp/borrower/info/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusHlsBpBorrowerInfo> dto){
    service.batchDelete(dto);
    return new ResponseData();
}
}