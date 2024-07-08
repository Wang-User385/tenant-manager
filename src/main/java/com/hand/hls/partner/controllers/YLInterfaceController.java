package com.hand.hls.partner.controllers;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.PlaceOrderDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = {"/r/api"})
public class YLInterfaceController extends BaseController {
    @RequestMapping(
            value = {"/di/placeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData placeOrder(@RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
        return null;
    }
}
