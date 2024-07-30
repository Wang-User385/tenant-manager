package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.bp.dto.BpCategoryInfoLov;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;
import com.hand.hls.bp.mapper.HlsCusBpMasterContactInfoMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.service.HlsBpMasterService;
import com.hand.hls.sys.service.IFndCompanyService;
import com.hand.hls.vat.dto.HlsCusAcrReceiptLn;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class HlsBpMasterController extends BaseController {

    @Autowired
    HlsBpMasterService hlsBpMasterService;
    @Autowired
    HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private IFndCompanyService fndCompanyService;

    @Autowired
    private HlsCusBpMasterContactInfoMapper hlsCusBpMasterContactInfoMapper;

    private static final String BP_CATEGORY = "bp_category";
    private static final String CATEGORY_DESC = "category_desc";
    private static final String BP_TYPE = "bp_type";
    private static final String TYPE_DESC = "type_desc";
    private static final String PARAMETER = "parameter";
    private static final String NEW = "NEW";
    private static final String REJECTED = "REJECTED";
    private static final String CANCEL = "CANCEL";
    private static final String DISTRIBUTOR = "DISTRIBUTOR";


    @RequestMapping(value = "/hls/bp/home/query")
    @ResponseBody
    public ResponseData queryMaster(
            @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsBpMaster metadataRelation = param.toJavaObject(HlsBpMaster.class);
        List<HlsBpMaster> list = hlsBpMasterService.bpMasterHomeQuery(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/bp/queryById")
    @ResponseBody
    public ResponseData queryById(
            @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
            HttpServletRequest request, HttpServletResponse response) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsBpMaster result = param.toJavaObject(HlsBpMaster.class);
        result = hlsBpMasterService.selectByPrimaryKey(requestCtx, result);
        List<HlsBpMaster> list = new ArrayList();
        if(result != null){
            list.add(result);
        }
        return new ResponseData(list);
    }

    /**
     * 二期功能：保证金的付款对象查询
     * @param requestData
     * @param transactionIds
     * @param refundId
     * @param request
     * @param response
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping({"/hls/bp/queryPaymentBpMasterLov"})
    @ResponseBody
    public ResponseData queryPaymentBpMasterLov(@ModelAttribute("_request_data") LeafRequestData requestData, String transactionIds, String refundId, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get(PARAMETER);
        IRequest requestCtx = this.createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag","N");
        List<HlsBpMaster> list = this.hlsBpMasterService.queryPaymentBpMasterLov(requestCtx, transactionIds, refundId, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/bp/queryBpMasterLov")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsBpMaster metadataRelation = param.toJavaObject(HlsBpMaster.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsBpMaster> list = hlsBpMasterService.queryBpMasterLov(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/bp/queryBpMasterLov2")
    @ResponseBody
    public ResponseData query2(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsBpMaster metadataRelation = param.toJavaObject(HlsBpMaster.class);
        String bpType = request.getParameter("bp_type");
        String bpClass = request.getParameter("bp_class");
        String marketingReportId = request.getParameter("marketing_report_id");
        if(bpType != null){
            metadataRelation.setBpType(bpType);
        }
        if(bpClass != null){
            metadataRelation.setBpClass(bpClass);
        }
        if(metadataRelation != null){
            metadataRelation.setMarketingReportId(marketingReportId);
        }
        IRequest requestCtx = createRequestContext(request);
        List<HlsBpMaster> list = hlsBpMasterService.queryBpMasterLov2(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/bp/asset/inspection/selectForLovIf")
    @ResponseBody
    public ResponseData AssetInspectionSelectForLovIf(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsBpMaster dto = param.toJavaObject(HlsBpMaster.class);
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return new ResponseData(hlsBpMasterService.selectForLovIf(dto, pagenum, pagesize));
    }

    /**
     * 生成商业伙伴编码权限字符串
     *
     * @param request
     * @return
     */
    @RequestMapping("/hls/bp/master/generateAuthorityString")
    public ResponseData generateAuthorityString(HttpServletRequest request) {
        IRequest requestContext = RequestHelper.getCurrentRequest();
        if (requestContext == null) {
            requestContext = createRequestContext(request);
        }
        FndCompany fndCompany = new FndCompany();
        fndCompany.setCompanyId(requestContext.getCompanyId());
        fndCompany = fndCompanyService.selectByPrimaryKey(requestContext, fndCompany);
        String employeeCode = requestContext.getEmployeeCode();
        String positionCode = requestContext.getAttribute("positionCode") == null ? "" : requestContext.getAttribute("positionCode");
        String unitCode = requestContext.getAttribute("unitCode") == null ? "" : requestContext.getAttribute("unitCode");
//        String authorityRuleString = '"' + fndCompany.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + employeeCode + '"';
        String authorityRuleString = '"' + fndCompany.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' +  positionCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"'  + positionCode + '"' + "." + '"'+  employeeCode + '"';
        return new ResponseData(Arrays.asList(authorityRuleString));
    }

    /**
     * 融资授信查询lov
     *
     * @param request dto PageNum PageSize
     * @return dto
     */
    @RequestMapping("/hls/lon/credit/bp/lov/query")
    @ResponseBody
    public ResponseData lonCreditBpLovQuery(HlsBpMaster dto,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                            HttpServletRequest request) {
        Map<String, String> parameter = (Map) JSON.parseObject(request.getParameter("_request_data"), Map.class).get("parameter");
        if (StringUtils.isNotBlank(parameter.get("bpCode"))) {
            dto.setBpCode(parameter.get("bpCode"));
        }
        if (StringUtils.isNotBlank(parameter.get("bpName"))) {
            dto.setBpCode(parameter.get("bpName"));
        }
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(hlsBpMasterService.lonCreditBpLovQuery(requestCtx, dto, pagenum, pagesize));
    }

    /**
     * 校验登记注册号码
     *
     * @param request
     * @param bpId
     * @param regNumber
     * @return
     */
    @RequestMapping("/hls/bp/master/validRegNumber")
    public ResponseData validRegNumber(HttpServletRequest request,
                                       @RequestParam Long bpId,
                                       @RequestParam String regNumber) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        Boolean flag = hlsBpMasterService.validRegNumber(requestCtx, bpId, regNumber);
        if (flag) {
            return new ResponseData(true);
        } else {
            return new ResponseData(false);
        }
    }

    @RequestMapping("/hls/bp/master/validRegCertNumber")
    public ResponseData validRegCertNumber(HttpServletRequest request,
                                       @RequestParam Long bpId,
                                       @RequestParam String regNumber) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        Boolean flag = hlsBpMasterService.validRegCertNumber(requestCtx, bpId, regNumber);
        if (flag) {
            return new ResponseData(true);
        } else {
            return new ResponseData(false);
        }
    }


    @RequestMapping({"/hls/bp/master/validRegNumber1"})
    public ResponseData validRegNumber1(HttpServletRequest request, @RequestParam Long bpId, @RequestParam String regNumber) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return this.hlsBpMasterService.validRegNumber1(requestCtx, bpId, regNumber) ? new ResponseData(true) : new ResponseData(false);
    }

    /**
     * 校验身份证号重复性
     */
    @RequestMapping("/hls/bp/master/validIdCardNo")
    public ResponseData validIdCardNo(HttpServletRequest request,
                                       @RequestParam Long bpId,
                                       @RequestParam String idCardNo) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        Boolean flag = hlsBpMasterService.validIdCardNo(requestCtx, bpId, idCardNo);
        if (flag) {
            return new ResponseData(true);
        } else {
            return new ResponseData(false);
        }
    }

    /**
     * 校验法人商业伙伴重复性
     */
    @RequestMapping("/hls/bp/master/org/bpName/valid")
    public ResponseData validBpName(HttpServletRequest request,
                                      @RequestParam Long bpId,
                                      @RequestParam String bpName,
                                      @RequestParam String selectFlag) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        Boolean flag = hlsBpMasterService.validBpName(requestCtx, bpId, bpName);
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(true);
        String resMessage = String.valueOf(flag);
        responseData.setMessage(resMessage);
        if("Y".equals(selectFlag)){
            responseData.setSuccess(Boolean.parseBoolean(resMessage));
        }
        return responseData;
    }

    @RequestMapping(value = "/hls/bp/queryBpPhone")
    @ResponseBody
    public ResponseData queryBpPhone(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusBpMasterContactInfo> hlsBpMasters = param.toJavaList(HlsCusBpMasterContactInfo.class);
        List<HlsCusBpMasterContactInfo> list = new ArrayList();
        for (HlsCusBpMasterContactInfo hlsBpMaster : hlsBpMasters) {
            HlsCusBpMasterContactInfo record = new HlsCusBpMasterContactInfo();
            record.setCellPhone(hlsBpMaster.getCellPhone());
            //判断当前数据库中得手机号与更改更改行的手机号是否一致,一致则不算作重复
            if(hlsBpMaster.getContactInfoId() != null){
                HlsCusBpMasterContactInfo select = hlsCusBpMasterContactInfoMapper.selectByPrimaryKey(hlsBpMaster);
                if(select.getCellPhone() != null && select.getCellPhone().equals(hlsBpMaster.getCellPhone())){
                    continue;
                }
            }
            list.addAll(hlsCusBpMasterContactInfoMapper.select(record));
        }
        return new ResponseData(list);
    }

    //
    /**
     * 商业伙伴创建入口商业伙伴类别LOV查询
     *
     * @param dto
     * @param pagenum
     * @param pagesize
     * @param request
     * @return
     */
    @RequestMapping(value ="/hls/bp/master/type/query")
    @ResponseBody
    public ResponseData bpTypeInfoQuery(BpCategoryInfoLov dto, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        Map<String, String> parameter = (Map) (JSON.parseObject(request.getParameter("_request_data"), Map.class)).get(PARAMETER);
        if (StringUtils.isNotBlank(parameter.get(BP_CATEGORY))) {
            dto.setBp_category(parameter.get(BP_CATEGORY));
        }

        if (StringUtils.isNotBlank(parameter.get(CATEGORY_DESC))) {
            dto.setCategory_desc(parameter.get(CATEGORY_DESC));
        }
        if (StringUtils.isNotBlank(parameter.get(BP_TYPE))) {
            dto.setBp_type(parameter.get(BP_TYPE));
        }

        if (StringUtils.isNotBlank(parameter.get(TYPE_DESC))) {
            dto.setType_desc(parameter.get(TYPE_DESC));
        }
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.hlsBpMasterService.bpTypeInfoQuery(requestCtx, dto, pagenum, pagesize));
    }
    /**
     * 商业伙伴保存校验是否有同名商业伙伴
     *
     * @param request
     * @param bpId
     * @param bpName
     * @return
     */
    @RequestMapping({"/hls/bp/master/validBpName"})
    public ResponseData validBpName2(HttpServletRequest request, @RequestParam Long bpId, @RequestParam String bpName) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return this.hlsBpMasterService.validBpName2(requestCtx, bpId, bpName) ? new ResponseData(true) : new ResponseData(false);
    }

    @RequestMapping({"/hls/bp/master/manufacturer/query"})
    @ResponseBody
    public ResponseData manufacturerInfoQuery(BpManufacturerInfoLov dto, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        Map<String, String> parameter = (Map) (JSON.parseObject(request.getParameter("_request_data"), Map.class)).get(PARAMETER);
        if (StringUtils.isNotBlank(parameter.get("bpCategory"))) {
            dto.setBpCategory(parameter.get("bpCategory"));
        }

        if (StringUtils.isNotBlank(parameter.get("bpName"))) {
            dto.setBpName(parameter.get("bpName"));
        }
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.hlsBpMasterService.bpManufacturerInfoQuery(requestCtx, dto, pagenum, pagesize));
    }

    @RequestMapping({"/hls/bp/master/vender/query"})
    @ResponseBody
    public ResponseData venderInfoQuery(BpVenderInfoLov dto, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        Map<String, String> parameter = (Map) (JSON.parseObject(request.getParameter("_request_data"), Map.class)).get(PARAMETER);
        if (StringUtils.isNotBlank(parameter.get("bpCategory"))) {
            dto.setBpCategory(parameter.get("bpCategory"));
        }

        if (StringUtils.isNotBlank(parameter.get("bpName"))) {
            dto.setBpName(parameter.get("bpName"));
        }

        if (StringUtils.isNotBlank(parameter.get("relationBpName"))) {
            dto.setRelationBpName(parameter.get("relationBpName"));
        }

        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.hlsBpMasterService.bpVenderInfoQuery(requestCtx, dto, pagenum, pagesize));
    }

    @RequestMapping({"/hls/bp/master/dealer/query"})
    @ResponseBody
    public ResponseData dealerInfoQuery(BpDealerInfoLov dto, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        Map<String, String> parameter = (Map) (JSON.parseObject(request.getParameter("_request_data"), Map.class)).get(PARAMETER);
        if (StringUtils.isNotBlank(parameter.get("bpCategory"))) {
            dto.setBpCategory(parameter.get("bpCategory"));
        }

        if (StringUtils.isNotBlank(parameter.get("bpName"))) {
            dto.setBpName(parameter.get("bpName"));
        }
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.hlsBpMasterService.bpDealerInfoQuery(requestCtx, dto, pagenum, pagesize));
    }

    @RequestMapping({"/hls/bp/queryBpMasterVenderLov"})
    @ResponseBody
    public ResponseData queryVender(@ModelAttribute("_request_data") LeafRequestData requestData, String bpCategory, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get(PARAMETER);
        HlsBpMaster metadataRelation = param.toJavaObject(HlsBpMaster.class);
        if (StringUtils.isNotEmpty(bpCategory)) {
            String[] bpCategorys = bpCategory.split(",");
            metadataRelation.setBpCategorys(bpCategorys);
        }
        IRequest requestCtx = this.createRequestContext(request);
        List<HlsBpMaster> list = this.hlsBpMasterService.queryBpMasterVenderLovNew(requestCtx,metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }
    /**
     * 获取厂商/合作方
     * @param request request
     * @return HlsBpMaster
     */
    @RequestMapping(value = "/bp/manufacturer/partner/lov/query")
    @ResponseBody
    public ResponseData bpManufacturerPartnerLovQuery (HttpServletRequest request) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.hlsBpMasterService.bpManufacturerPartnerLovQuery());
    }


}
