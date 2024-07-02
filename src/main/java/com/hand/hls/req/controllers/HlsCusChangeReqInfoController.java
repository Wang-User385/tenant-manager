package com.hand.hls.req.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import com.hand.hls.fct.service.HlsCusFctProjectService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.user.service.LoginUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusChangeReqInfoController extends BaseController {

    @Autowired
    private HlsCusChangeReqInfoService service;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusFctProjectService hlsCusFctProjectService;


    /*
      项目变更，
      存入大字段，先创建变更记录，然后将当前项目的信息备份到大字段表中

    * */
    @RequestMapping(value = "/req/change/info/change/req")
    @ResponseBody
    public ResponseData projectChangeReq(@RequestBody HlsCusChangeReqInfo dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryChangeInfo(requestContext, dto));
    }



    /**
     * 查询合同变更的信息
     */
    @RequestMapping(value = "/req/change/info/select")
    @ResponseBody
    public ResponseData queryChangeInfo(@RequestBody HlsCusChangeReqInfo dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryChangeInfo(requestContext, dto));
    }

    //租赁合同变更信息查询
    @RequestMapping(value = "/prj/contract/change/info/query")
    @ResponseBody
    public ResponseData queryContractChangeInfo(@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                HttpServletRequest request, @RequestBody HlsCusChangeReqInfo dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryContractChangeInfo(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/change/req/status/query")
    @ResponseBody
    public ResponseData queryChangeStatus(HttpServletRequest request, @RequestBody HlsCusChangeReqInfo dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryStatus(dto));
    }

    @RequestMapping(value = "/change/req/wfl/info/query")
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestParam Long changeReqId) {
        IRequest requestContext = createRequestContext(request);
        HlsCusChangeReqInfo dto = new HlsCusChangeReqInfo();
        dto.setChangeReqId(changeReqId);
        dto = service.selectByPrimaryKey(requestContext, dto);
        dto.setUserName(sysUserService.selectUserById(dto.getChangeReqUserId()).getDescription());
        List<HlsCusChangeReqInfo> list = new ArrayList<>();
        list.add(dto);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/change/req/info/query")
    @ResponseBody
    public ResponseData query(HlsCusChangeReqInfo dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryHistory(dto));
    }

    @RequestMapping(value = "/change/req/info/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusChangeReqInfo> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/change/req/info/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusChangeReqInfo> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/fct/con/change/type/query")
    @ResponseBody
    public Map selectInformationAllStatistics(HlsCusChangeReqInfo hlsCusChangeReqInfo) {
        Map map = service.queryConChangeType(hlsCusChangeReqInfo);
        return map;
    }

    @RequestMapping(value = "/prj/con/change/type/query")
    @ResponseBody
    public Map selectPrjContractChangeType() {
        return service.queryPrcContractChangeType();
    }

    @RequestMapping(value = "/hls/cus/con/change/type/detail/query")
    @ResponseBody
    public ResponseData queryFctProject(HlsCusChangeReqInfo hlsCusChangeReqInfo, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) throws ParseException {
        IRequest requestContext = RequestHelper.getCurrentRequest();
        List list = service.selectConChangeType(requestContext, hlsCusChangeReqInfo, page, pageSize);
        return new ResponseData(list);
    }
}