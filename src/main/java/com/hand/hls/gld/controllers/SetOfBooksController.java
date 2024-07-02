package com.hand.hls.gld.controllers;

import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.gld.dto.SetOfBooks;
import com.hand.hls.gld.exception.CodeException;
import com.hand.hls.gld.service.ISetOfBooksService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SetOfBooksController extends BaseController {

@Autowired
private ISetOfBooksService service;


@RequestMapping(value = "/gld/set/of/books/query")
@ResponseBody
public ResponseData query(SetOfBooks dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    return new ResponseData(service.select(requestContext,dto,page,pageSize));
}

@RequestMapping(value = "/gld/set/of/books/submit")
@ResponseBody
public ResponseData update(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws CodeException {

    JSONArray paras = (JSONArray) requestData.get("parameter");
    List<SetOfBooks> dto = paras.toJavaList(SetOfBooks.class);
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    List<SetOfBooks> list = new ArrayList<SetOfBooks>();
    try {
       list  = service.batchUpdate(requestCtx, dto);
    }catch (Exception e){
        throw new CodeException();
    }
    return new ResponseData(list);
}

@RequestMapping(value = "/gld/set/of/books/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){

    JSONArray paras = (JSONArray) requestData.get("parameter");
    List<SetOfBooks> dto = paras.toJavaList(SetOfBooks.class);
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    service.batchDelete(dto);
    return new ResponseData(dto);
}

@RequestMapping(value = "/gld/set/of/books/baseQuery")
@ResponseBody
public ResponseData baseQuery(SetOfBooks dto, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    return new ResponseData(service.baseSelect(requestContext,dto,pagenum,pagesize));
}

@RequestMapping(value = "/gld/set/of/books/query/lovForCompany")
@ResponseBody
public ResponseData queryLovForCompany(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    SetOfBooks dto = new SetOfBooks();
    Map map = (Map)requestData.get("parameter");
    if (map != null) {
        dto.setSetOfBooksCode((String)map.getOrDefault("setOfBooksCode", (Object)null));
        dto.setSetOfBooksName((String)map.getOrDefault("setOfBooksName", (Object)null));
        page = Integer.parseInt(request.getParameter("pagenum"));
    }
    return new ResponseData(service.selectLovForCompany(requestContext,dto,page,pageSize));
}


}