package com.hand.hls.fnd.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.HlsCusDocumentList;
import com.hand.hls.fnd.service.HlsCusDocumentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:附件清单controller
 * @Author: wty
 * @Date: Created in 0:15 2018/4/16
 */
@Controller
public class HlsCusDocumentListController extends BaseController {

    @Autowired
    private HlsCusDocumentListService hlsCusDocumentListService;

    @RequestMapping("document/list/query")
    @ResponseBody
    public ResponseData selectList(HttpServletRequest request, HlsCusDocumentList hlsCusDocumentList,
                                   @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusDocumentList> datas = hlsCusDocumentListService.selectList(iRequest,hlsCusDocumentList,page,pagesize);
        return new ResponseData(datas);
    }

    @RequestMapping(value = "document/list/submit")
    @ResponseBody
    public ResponseData submit(@RequestBody final List<HlsCusDocumentList> hlsCusDocumentListList, final BindingResult result,
                               final HttpServletRequest request){
        getValidator().validate(hlsCusDocumentListList, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(hlsCusDocumentListService.batchUpdate(requestContext, hlsCusDocumentListList));
    }

    @RequestMapping("document/list/init")
    @ResponseBody
    public ResponseData BpMasterListInit(HttpServletRequest request, HlsCusDocumentList hlsCusDocumentList,
                                         @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusDocumentList> datas = hlsCusDocumentListService.BpMasterListInit(iRequest,hlsCusDocumentList,page,pagesize);
        return new ResponseData(datas);
    }

}
