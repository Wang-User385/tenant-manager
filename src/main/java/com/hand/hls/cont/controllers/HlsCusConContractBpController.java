package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContractBp;
import com.hand.hls.cont.service.HlsCusConContractBpService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusConContractBpController extends BaseController {

    @Autowired
    private HlsCusConContractBpService service;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;


    @RequestMapping(value = "/ct/con/contract/bp/query")
    @ResponseBody
    public ResponseData query(HlsCusConContractBp dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusConContractBp> list = service.select(requestContext, dto, page, pageSize);
        for (HlsCusConContractBp dt : list) {
            HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
            hlsCusBpMaster.setBpId(dt.getBpId());
            hlsCusBpMaster = hlsCusBpMasterService.selectByPrimaryKey(requestContext, hlsCusBpMaster);
            dt.setBpCode(hlsCusBpMaster.getBpCode());
            dt.setBpName(hlsCusBpMaster.getBpName());
        }
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/con/contract/bp/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusConContractBp> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        if (dto != null) {
            for (HlsCusConContractBp dt : dto) {
                if (dt.getRecordId() == null || dt.getRecordId() == 0) {
                    dt.set__status("add");
                } else {
                    dt.set__status("update");
                }
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/con/contract/bp/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusConContractBp> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/con/contract/bp/queryByCashflowId")
    @ResponseBody
    public ResponseData queryByCashflowId(HlsCusConContractBp dto,
                                          @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                          HttpServletRequest request) {
        if (dto.getCashflowId() == null) {
            return new ResponseData(false, "请先选择合同!");
        }
        Map<String, String> parameter = (Map) JSON.parseObject(request.getParameter("_request_data"), Map.class).get("parameter");
        if (StringUtils.isNotBlank(parameter.get("bpCode"))) {
            dto.setBpCode(parameter.get("bpCode"));
        }
        if (StringUtils.isNotBlank(parameter.get("bpName"))) {
            dto.setBpName(parameter.get("bpName"));
        }
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(service.queryByContractId(dto, pagenum, pagesize));
    }

    /**
     * 二期功能：付款申请创建-付款对象银行信息Lov
     * @param requestData
     * @param dto
     * @param pagenum
     * @param pagesize
     * @param request
     * @return
     */
    @RequestMapping({"/con/contract/bp/bank/lov"})
    @ResponseBody
    public ResponseData queryContractBpLovNew(@ModelAttribute("_request_data") LeafRequestData requestData,
                                              HlsCusConContractBp dto,
                                              @RequestParam(defaultValue = "1") int pagenum,
                                              @RequestParam(defaultValue = "10") int pagesize,
                                              HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusConContractBp hlsCusConContractBp = (HlsCusConContractBp)param.toJavaObject(HlsCusConContractBp.class);
        return new ResponseData(this.service.queryPaymentBpBankInfoLov(requestContext, dto, pagenum, pagesize));
    }
}