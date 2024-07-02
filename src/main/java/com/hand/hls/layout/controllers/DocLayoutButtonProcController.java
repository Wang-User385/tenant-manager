package com.hand.hls.layout.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutButtonProcDto;
import com.hand.hls.layout.service.IDocLayoutButtonProcService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * WEB-INF/classes/FND/FND_MODEL/FND114/hls_doc_layout_button_proc.lwm
 */
@Controller
public class DocLayoutButtonProcController extends BaseController {
    @Autowired
    private IDocLayoutButtonProcService iDocLayoutButtonProcService;

    @RequestMapping(value = "/hls/doc/layout/button/proc/query", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE) final int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize,
                              HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request) ;
        RequestHelper.setCurrentRequest(iRequest);
        PageHelper.startPage(pagenum, pagesize);
        DocLayoutButtonProcDto docLayoutButtonProcDto = new DocLayoutButtonProcDto();
        Map para = requestData.getParameter();
        if (para == null || para.get("button_code") ==null || para.get("function_code" )== null){
            return new ResponseData(false,"请求参数有误！");
        }else {
            docLayoutButtonProcDto.setFunctionCode(para.get("function_code").toString());
            docLayoutButtonProcDto.setButtonCode(para.get("button_code").toString());
        }

        JSONObject param = (JSONObject) para;
        docLayoutButtonProcDto = param.toJavaObject(DocLayoutButtonProcDto.class);
        List<DocLayoutButtonProcDto> list = iDocLayoutButtonProcService.select(iRequest,docLayoutButtonProcDto,pagenum,pagesize);
        return new ResponseData(list);

    }

    @RequestMapping(value = "/hls/doc/layout/button/proc/submit", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                               final BindingResult result,
                               final HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        /*JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<DocLayoutButtonProcDto> docLayoutButtonProcDtoList = parameter.toJavaList(DocLayoutButtonProcDto.class);
        getValidator().validate(docLayoutButtonProcDtoList, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(iDocLayoutButtonProcService.batchUpdate(requestCtx, docLayoutButtonProcDtoList));*/
        if (requestData.get("parameter") != null && ((List) requestData.get("parameter")).size() > 0) {
            List<Map> list = ((List) requestData.get("parameter"));
            for (int i = 0; i < list.size(); i++) {
                if ("insert".equals(list.get(i).get("_status"))) {
                    CompositeMap compositeMap = new CompositeMap("", list.get(i));
                    RecordHelper.insert("HLS_DOC_LAYOUT_BUTTON_PROC", compositeMap);
                } else if ("update".equals(list.get(i).get("_status"))) {
                    CompositeMap compositeMap = new CompositeMap("", list.get(i));
                    RecordHelper.update("HLS_DOC_LAYOUT_BUTTON_PROC", compositeMap);
                } else if ("delete".equals(list.get(i).get("_status"))) {
                    CompositeMap compositeMap = new CompositeMap("", list.get(i));
                    RecordHelper.delete("HLS_DOC_LAYOUT_BUTTON_PROC", compositeMap);
                }
            }
        }
        return new ResponseData(((List) requestData.get("parameter")));
    }

    @RequestMapping(value = "/hls/doc/layout/button/proc/remove", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData deleteCon(HttpServletRequest request,
                                  @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<DocLayoutButtonProcDto> docLayoutButtonProcDtoList =parameter.toJavaList(DocLayoutButtonProcDto.class);
        IRequest requestContext = createRequestContext(request);
        iDocLayoutButtonProcService.batchDelete(docLayoutButtonProcDtoList);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(docLayoutButtonProcDtoList);
    }

}
