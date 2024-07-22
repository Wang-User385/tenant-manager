package com.hand.hls.partner.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.LeasingNotice;
import com.hand.hls.partner.service.ILeasingNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(value = {"/r/api"})
public class LeasingNoticeController extends BaseController {

    @Autowired
    private ILeasingNoticeService leasingNoticeService;


    /**
     * 易靓审核结果通知
     *
     * @param leasingNotice
     * @param request
     * @return
     */
    @RequestMapping(value = "/notice/re/push", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData noticeRePush(LeasingNotice leasingNotice, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        leasingNoticeService.noticeRePush(leasingNotice, requestContext);
        return new ResponseData();
    }
}
