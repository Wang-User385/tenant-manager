package di.controller;


//对接接口

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import di.service.DockingInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DockingInterfaceController extends BaseController {

    @Autowired
    private DockingInterfaceService dockingInterfaceService;


    @RequestMapping(
            value = {"/di/placeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData placeOrder(@RequestParam String name,
                                   @RequestParam String idCardNo,
                                   @RequestParam String mobile,
                                   @RequestParam String productCode,
                                   @RequestParam String idissue,
                                   @RequestParam String idexp,
                                   @RequestParam String outBizNo, HttpServletRequest request) {
        return dockingInterfaceService.placeOrder(name,idCardNo,mobile,productCode,outBizNo,idissue,idexp,request);
    }

    @RequestMapping(
            value = {"/di/closeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData closeOrder(@RequestParam String orderNo,
                                   @RequestParam String reason, HttpServletRequest request) {
        return dockingInterfaceService.closeOrder(orderNo,reason);
    }
}
