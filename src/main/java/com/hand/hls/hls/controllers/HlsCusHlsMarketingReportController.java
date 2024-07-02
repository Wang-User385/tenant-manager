package com.hand.hls.hls.controllers;

import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.dto.SysTemplate;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.FndOrgUnitMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class HlsCusHlsMarketingReportController extends BaseController{

    @Autowired
    private HlsCusHlsMarketingReportService service;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private FndOrgUnitMapper fndOrgUnitMapper;
    private static final String BUSINESS_KEY = "BUSINESS_KEY";
    private static final String Y = "Y";

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HlsCusHlsMarketingReportMapper mapper;
    //项目方案合同文本生成
    @RequestMapping(value = "/hls/marketing/create/content")
    @ResponseBody
    public ResponseData hlsMarketingCreateContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) throws Exception {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsMarketingReport dto = param.toJavaObject(HlsCusHlsMarketingReport.class);
        HlsCusHlsMarketingReport hlsMarketingReport = new HlsCusHlsMarketingReport();
        //获取项目方案id
        Long marketingReprotId = dto.getMarketingReportId() ;
        hlsMarketingReport.setMarketingReportId(marketingReprotId);
        //设置合同文本可打印节点 更新表中文本打印标记
        hlsMarketingReport.setDocxFlag(Y);
        hlsMarketingReport.setContractTextStatus("CREATED");
        mapper.updateByPrimaryKeySelective(hlsMarketingReport);

        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportCreateDocx(iRequest, hlsMarketingReport);
        }catch (Exception e){
            logger.error(e.getMessage());
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }
        dto.setSourcePkValue(list.get(0).getSourcePkValue());
        List<HlsCusHlsMarketingReport> hlsDto = new ArrayList<HlsCusHlsMarketingReport>();
        hlsDto.add(dto);
        return new ResponseData(hlsDto);
    }

    @RequestMapping(value = "/hls/marketing/atm/query")
    @ResponseBody
    public ResponseData queryAtm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsMarketingReport dto = param.toJavaObject(HlsCusHlsMarketingReport.class);
        return new ResponseData(service.selectDetail(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/marketing/report/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsMarketingReport dto = param.toJavaObject(HlsCusHlsMarketingReport.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }
    //获取表主键
//    @RequestMapping(value = "/hls/marketing/report/getMarketingReportId")
//    @ResponseBody
//    public ResponseData query() {
//        return  service.queryMarketingReport();
//    }

    @RequestMapping(value = "/hls/marketing/report/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusHlsMarketingReport> list = param.toJavaList(HlsCusHlsMarketingReport.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/marketing/report/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusHlsMarketingReport> dto = parameter.toJavaList(HlsCusHlsMarketingReport.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
    @RequestMapping(value = "/hls/marketing/report/delete")
    @ResponseBody
    public ResponseData deleteReport(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception{
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusHlsMarketingReport> dto = parameter.toJavaList(HlsCusHlsMarketingReport.class);
        for (int i = 0; i < dto.size(); i++) {
            HlsCusHlsMarketingReport hlsCusHlsMarketingReport = service.selectByPrimaryKey(iRequest,dto.get(i));

            if(!"NEW".equals(hlsCusHlsMarketingReport.getStatus()) && !"REJECTED".equals(hlsCusHlsMarketingReport.getStatus())){
                throw new ResMessageException("请选择新建或者审批拒绝的单据!");
            }
        }
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
    @RequestMapping(value = "/hls/marketing/report/approve/submit")
    @ResponseBody
    public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException, ParameterNullException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = param.toJavaObject(HlsCusHlsMarketingReport.class);
        return new ResponseData(service.marketingReportSubmit(iRequest, hlsCusHlsMarketingReport));
    }



    @RequestMapping("/hls/marketing/report/generateAuthorityString")
    public ResponseData generateAuthorityString(HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        String authorityRuleString = service.generateAuthorityString(iRequest);
        return new ResponseData(Arrays.asList(authorityRuleString));
    }
    @RequestMapping("/hls/marketing/report/querySession")
    public ResponseData querySession(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException{
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = param.toJavaObject(HlsCusHlsMarketingReport.class);
        SysUser sysUser = sysUserMapper.selectUserById(hlsCusHlsMarketingReport.getHostProjectManager());
        hlsCusHlsMarketingReport.setHostProjectManagerN(sysUser.getDescription());
        List<HlsCusHlsMarketingReport> hlsCusHlsMarketingReports = new ArrayList<>();
        hlsCusHlsMarketingReports.add(hlsCusHlsMarketingReport);
        return new ResponseData(hlsCusHlsMarketingReports);
    }

    @RequestMapping("/hls/marketing/report/id")
    public ResponseData queryMarketingReportId(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException{
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = param.toJavaObject(HlsCusHlsMarketingReport.class);
        hlsCusHlsMarketingReport= mapper.queryMarketingReportIdNew();
        List<HlsCusHlsMarketingReport> hlsCusHlsMarketingReports = new ArrayList<>();
        hlsCusHlsMarketingReports.add(hlsCusHlsMarketingReport);
        return new ResponseData(hlsCusHlsMarketingReports);
    }

}