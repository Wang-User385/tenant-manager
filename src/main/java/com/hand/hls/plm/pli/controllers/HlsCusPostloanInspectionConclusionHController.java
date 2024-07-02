package com.hand.hls.plm.pli.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspectionConclusionH;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionConclusionHService;
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
 * @Description:贷后检查历史结论controller
 * @Author: Wty
 * @Date: Created om 16:25 2018/5/24
 */
@Controller
public class HlsCusPostloanInspectionConclusionHController extends BaseController {

    @Autowired
    private HlsCusIPostloanInspectionConclusionHService service;

    /**
     * @Description:贷后检查历史结论查询
     * @Author: Wty
     * @Date: Created om 15:22 2018/5/28
     */
    @RequestMapping(value = "/plm/postloan/inspection/conclusion/h/query")
    @ResponseBody
    public ResponseData query(HlsCusPostloanInspectionConclusionH dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectConclusionH(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/postloan/inspection/conclusion/h/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusPostloanInspectionConclusionH> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/postloan/inspection/conclusion/h/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusPostloanInspectionConclusionH> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


}