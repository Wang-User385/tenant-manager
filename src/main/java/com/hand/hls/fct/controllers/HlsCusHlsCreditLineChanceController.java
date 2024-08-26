package com.hand.hls.fct.controllers;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.fct.dto.HlsCusCreditProject;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.sys.mapper.SysUserMapper;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class HlsCusHlsCreditLineChanceController extends BaseController {

    @Autowired
    private HlsCusHlsCreditLineChanceService service;
    @Autowired
    private HlsCusHlsCreditLineChanceMapper sysUserMapper;
    private static final String Y = "Y";
    @Autowired
    private HlsCusHlsCreditLineChanceMapper mapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /*项目立项业务意向书生成*/
    @RequestMapping(value = "/hls/chance/create/content")
    @ResponseBody
    public ResponseData hlsMarketingCreateContent(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) throws Exception {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineChance dto = param.toJavaObject(HlsCusHlsCreditLineChance.class);
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = new HlsCusHlsCreditLineChance();
        //获取项目方案id
        Long chancId = dto.getChanceId();
        hlsCusHlsCreditLineChance.setChanceId(chancId);
        //设置合同文本可打印节点 更新表中文本打印标记
        hlsCusHlsCreditLineChance.setDocxFlag(Y);
        hlsCusHlsCreditLineChance.setContractTextStatus("CREATED");
        mapper.updateByPrimaryKeySelective(hlsCusHlsCreditLineChance);

        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportCreateDocx(iRequest, hlsCusHlsCreditLineChance);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }
        dto.setSourcePkValue(list.get(0).getSourcePkValue());
        List<HlsCusHlsCreditLineChance> hlsDto = new ArrayList<HlsCusHlsCreditLineChance>();
        hlsDto.add(dto);
        return new ResponseData(hlsDto);
    }

    /*项目方案生成项目立项信息*/
    @RequestMapping(value = "/hls/credit/line/chance/create")
    @ResponseBody
    public ResponseData hlsCreditLineChanceCrate(HlsCusHlsCreditLineChance dto, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param1 = (JSONObject) requestData.get("parameter");
        JSONObject param = (JSONObject) param1.get("param");
        Long marketingReportId = Long.valueOf(String.valueOf(param.get("marketing_report_id")));
        Long proposerPmployeeId = Long.valueOf(String.valueOf(param.get("proposer_employee_id")));
        //project_assistant
        Long projectAssistant = Long.valueOf(String.valueOf(param.get("project_assistant")));
        dto.setCreditFlag(String.valueOf(param.get("credit_flag")));
        dto.setMarketingReportNumber(String.valueOf(param.get("marketing_report_number")));
        //unit_id
        Long unitId = Long.valueOf(String.valueOf(param.get("unit_id")));
        //company_id
        Long companyId = Long.valueOf(String.valueOf(param.get("company_id")));
        dto.setCompanyId(companyId);
        dto.setCreditLineName(String.valueOf(param.get("credit_line_name")));
        dto.setProjectAssistant(projectAssistant);
        dto.setUnitId(unitId);
        dto.setBusinessType(String.valueOf(param.get("business_type")));
        dto.setProposerEmployeeId(proposerPmployeeId);
        dto.setMarketingReportId(marketingReportId);
        HlsCusHlsCreditLineChance hlsCreditChance = service.hlsCreditCreate(requestContext, dto);
        List<HlsCusHlsCreditLineChance> hlsCreditChanceList = new ArrayList<>();
        hlsCreditChanceList.add(hlsCreditChance);
        return new ResponseData(hlsCreditChanceList);
    }

    @RequestMapping(value = "/hls/credit/line/chance/query")
    @ResponseBody
    public ResponseData query(HlsCusHlsCreditLineChance dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/credit/line/chance/selectModelByCondition")
    @ResponseBody
    public ResponseData selectModelByCondition(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = param.toJavaObject(HlsCusHlsCreditLineChance.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectModelByCondition(requestContext, hlsCusHlsCreditLineChance, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/credit/line/chance/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusHlsCreditLineChance> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/credit/line/chance/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusHlsCreditLineChance> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /*综合授信项目查询*/
    @RequestMapping(value = "/hls/credit/line/chance/queryByStatus")
    @ResponseBody
    public ResponseData selectCreditLineChanceByStatus(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) throws ParseException {
        JSONObject param = (JSONObject) requestData.get("parameter");

        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }

        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = param.toJavaObject(HlsCusHlsCreditLineChance.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.selectCreditLineChanceByStatus(requestCtx, hlsCusHlsCreditLineChance, pagenum, pagesize, sortName, sortOrder));
    }

    /*授信立项扇形图查询*/
    @RequestMapping(value = "/hls/credit/line/chance/chart/query")
    @ResponseBody
    public ResponseData selectCreditLineChanceStatusInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, HttpServletRequest request) throws ParseException {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.selectCreditLineChanceStatusInfo(hlsCusHlsCreditLineChance, requestCtx));
    }

    //授信立项首页条件查询
    @RequestMapping(value = "/hls/credit/line/chance/condition/query")
    @ResponseBody
    public ResponseData selectCreditLineChanceByCondition(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, HttpServletRequest request) throws ParseException {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.selectCreditLineChanceByCondition(hlsCusHlsCreditLineChance, requestCtx));
    }

    /**
     * 授信立项提交和工作流开始事件
     */
    @RequestMapping(value = "/hls/credit/line/chance/condition/submitApproval")
    @ResponseBody
    public ResponseData submitApproval(HttpServletRequest request, @RequestBody HlsCusCreditProject dto) throws ParseException {
        try {
            IRequest requestCtx = createRequestContext(request);
            HlsCusHlsCreditLineChance cusHlsCreditLineChance = service.submitApproval(requestCtx, dto);
            List<HlsCusHlsCreditLineChance> cusHlsCreditLineChanceList = service.selectCreditLineChanceById1(cusHlsCreditLineChance);
            return new ResponseData(cusHlsCreditLineChanceList);
        } catch (IllegalArgumentException e) {
            ResponseData error = new ResponseData(false);
            error.setMessage(e.getMessage());
            return error;
        }
    }

    /**
     * 授信立项级联提交
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/hls/credit/line/chance/condition/cascadeSubmit")
    @ResponseBody
    public ResponseData save(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = createRequestContext(request);
        HlsCusCreditProject dto = param.toJavaObject(HlsCusCreditProject.class);
        List<HlsCusCreditProject> list = new ArrayList<>(1);
        list.add(service.cascadeSubmit(requestCtx, dto));
        return new ResponseData(list);
    }

    /**
     * 授信立项关闭
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/hls/credit/line/chance/closeCreditChance")
    @ResponseBody
    public ResponseData closeCreditChance(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
/*        JSONObject param = (JSONObject) requestData.get("parameter");

        IRequest requestCtx = createRequestContext(request);
//        HlsCusCreditProject dto = param.toJavaObject(HlsCusCreditProject.class);
        List<HlsCusCreditProject> hlsCusCreditProjects = param.toJavaList(HlsCusCreditProject.class);
        List<HlsCusCreditProject> list = new ArrayList<>(1);
//        list.add(service.cascadeSubmit(requestCtx, dto));

        return new ResponseData(list);*/


        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusHlsCreditLineChance> list = param.toJavaList(HlsCusHlsCreditLineChance.class);

        service.closeCreditChance(requestCtx, list);

        return new ResponseData(list);


    }


    /**
     * @return com.hand.hap.system.dto.ResponseData
     * @author kalvin
     * @Description 生成编码规则
     * @Date 9:14 2021/4/9
     * @Param [request]
     **/
    @RequestMapping("/hls/credit/line/chance/generateAuthorityString")
    public ResponseData generateAuthorityString(HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        String authorityRuleString = service.generateAuthorityString(iRequest);
        return new ResponseData(Arrays.asList(authorityRuleString));
    }

    @RequestMapping("/hls/credit/line/chance/id")
    public ResponseData queryChanceIdNew(HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusHlsCreditLineChance> cusHlsCreditLineChances = mapper.queryChanceIdNew();
        return new ResponseData(cusHlsCreditLineChances);
    }

    @RequestMapping("/hls/credit/chance/cooperativeOrganizationCheck")
    public ResponseData creditChanceCooperativeOrganizationCheck(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData)  throws HlsCusException{
        IRequest iRequest = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineChance dto = param.toJavaObject(HlsCusHlsCreditLineChance.class);
        dto.setBpId(Long.parseLong(param.get("cooperative_organization_id").toString()));
        String dateFromS=param.get("date_from").toString();
        String dateToS=param.get("date_to").toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dto.setValidFrom(formatter.parse(dateFromS));
            dto.setValidTo(formatter.parse(dateToS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        service.checkCooperativeOrganization(iRequest, dto);

        List<HlsCusHlsCreditLineChance> cusHlsCreditLineChances = new ArrayList<>();
        return new ResponseData(cusHlsCreditLineChances);
    }

    @RequestMapping("/chance/access/compare/generate")
    @ResponseBody
    public void generateChanceBusinessAccessCompare(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long document_id = Long.parseLong(param.get("documentId").toString());
        HlsCusHlsCreditLineChance dto = new HlsCusHlsCreditLineChance();
        dto.setChanceId(document_id);
        service.generateChanceCompare(iRequest, dto);
    }

    @RequestMapping("/chance/access/compare/import")
    @ResponseBody
    public Map<String, Object> chanceBusinessAccessCompareImport(HttpServletRequest request, Long headerId, Long chanceId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.chanceAccessCompareImport(iRequest, headerId, chanceId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }


    @RequestMapping("/chance/change/req/submit")
    @ResponseBody
    public ResponseData changeCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws com.hand.hls.exception.HlsCusException {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");

        HlsCusHlsCreditLineChance dto = param.toJavaObject(HlsCusHlsCreditLineChance.class);

        dto = service.changeCreate(requestCtx, dto);

        List<HlsCusHlsCreditLineChance> hlsCusChanceList = new ArrayList<>();
        hlsCusChanceList.add(dto);
        return new ResponseData(hlsCusChanceList);
    }



    /*立项变更提交*/
    @RequestMapping(value = "/hls/credit/line/chance/change/submit/wfl")
    @ResponseBody
    public ResponseData prjChangeSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineChance hlsCusPrjProject = param.toJavaObject(HlsCusHlsCreditLineChance.class);

        List<HlsCusHlsCreditLineChance> list = new ArrayList<>();
        list.add(service.prjChangeSubmitWfl(requestCtx, hlsCusPrjProject));
        return new ResponseData(list);
    }

    /*立项变更取消*/
    @RequestMapping(value = "/hls/credit/line/chance/change/cancel")
    @ResponseBody
    public ResponseData changeCancel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineChance dto = param.toJavaObject(HlsCusHlsCreditLineChance.class);

        return new ResponseData(service.changeCancel(requestCtx, dto));
    }

    /*授信关闭*/
    @RequestMapping(value = "/hls/credit/line/chance/credit/cancel")
    @ResponseBody
    public ResponseData creditCancel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineChance dto = param.toJavaObject(HlsCusHlsCreditLineChance.class);

        return new ResponseData(service.creditCancel(requestCtx, dto));
    }

    /*授信关闭*/
    @RequestMapping(value = "/chance/recreate")
    @ResponseBody
    public ResponseData reCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");

        HlsCusHlsCreditLineChance dto = param.toJavaObject(HlsCusHlsCreditLineChance.class);

        dto = service.reCreate(requestCtx, dto);

        List<HlsCusHlsCreditLineChance> hlsCusChanceList = new ArrayList<>();
        hlsCusChanceList.add(dto);
        return new ResponseData(hlsCusChanceList);
    }

    /*授信企业删除关联项目*/
    @RequestMapping(value = "/chance/deleteRelaProject")
    @ResponseBody
    public ResponseData deleteRelaProject(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONArray listObjectThir = JSONArray.parseArray(requestData.get("parameter").toString());
        List<HlsCusHlsCreditLineChanceBp> bpList = JSONArray.parseArray(listObjectThir.toJSONString(), HlsCusHlsCreditLineChanceBp.class);
        service.deleteRelaProject(requestCtx, bpList);
        return new ResponseData(bpList);
    }


    /*授信新建获取当前登陆人*/
    @RequestMapping(value = "/chance/selectByUserId")
    @ResponseBody
    public ResponseData selectByUserId(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        Long userId = requestCtx.getUserId();
        JSONArray listObjectThir = JSONArray.parseArray(requestData.get("parameter").toString());
        List<HlsCusHlsCreditLineChanceBp> bpList = JSONArray.parseArray(listObjectThir.toJSONString(), HlsCusHlsCreditLineChanceBp.class);
        return new ResponseData(sysUserMapper.selectByUserId(userId));
    }


    /***
     * 获取创建保理所需信息
     * @param requestData
     * @param request
     * @return
     */
    @RequestMapping(value = "/get/factoring/info")
    @ResponseBody
    public ResponseData getFactoringInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.createInfo(requestCtx.getUserId()));
    }


    @RequestMapping(value = "/batch/delete/info")
    @ResponseBody
    public ResponseData batchDeleteInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusHlsCreditLineChance> hlsCusHlsCreditLineChances = param.toJavaList(HlsCusHlsCreditLineChance.class);
        service.batchDelete(hlsCusHlsCreditLineChances);
        return new ResponseData();
    }


    @RequestMapping(value = "/credit/chance/submit")
    @ResponseBody
    public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = param.toJavaObject(HlsCusHlsCreditLineChance.class);
        return new ResponseData(service.submit(hlsCusHlsCreditLineChance,requestCtx));
    }


}
