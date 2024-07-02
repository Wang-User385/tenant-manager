package com.hand.hls.custom.demo.controllers;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.custom.demo.dto.SysPage;
import com.hand.hls.custom.demo.service.ISysPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SysPageController extends BaseController {
    @Autowired
    private ISysPageService sysPageService;

    @RequestMapping("/leaf-yhzl")
    public SysPage queryByName(HttpServletRequest request, SysPage page) {
        return sysPageService.selectByPrimaryKey(createRequestContext(request), page);
    }

}
