package com.hand.hls.bp.controllers;


import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import org.apache.commons.lang3.StringUtils;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusBpMasterController extends BaseController {

    @Autowired
    private HlsCusBpMasterService service;

    private static final String BP_CATEGORY = "bp_category";
    private static final String BP_NAME = "bp_name";
    private static final String CATEGORY_DESC = "category_desc";
    private static final String BP_TYPE = "bp_type";
    private static final String TYPE_DESC = "type_desc";
    private static final String PARAMETER = "parameter";
    private static final String BP_ID = "bp_id";
    private static final String NEW = "NEW";
    private static final String REJECTED = "REJECTED";
    private static final String CANCEL = "CANCEL";
    private static final String NEW_STATUS = "NEW";
    private static final String DISTRIBUTOR = "DISTRIBUTOR";
    private static final String FAILURE = "FAILURE";


    @RequestMapping(value = "/hls/bp/credit/line/info/all/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData creditLineInfoAllQuery(@ModelAttribute("_request_data") LeafRequestData requestData,
                                               @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMaster dto = param.toJavaObject(HlsCusBpMaster.class);
        List<HlsCusBpMaster> list = service.queryHlsBpMasterCreditInfoAll(dto, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/bp/credit/line/info/all/queryBpLov", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryBpLov(@ModelAttribute("_request_data") LeafRequestData requestData,
                                   @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMaster dto = param.toJavaObject(HlsCusBpMaster.class);
        List<HlsCusBpMaster> list = service.queryBpLov(dto, pagenum, pagesize);
        return new ResponseData(list);
    }

    /**
     * 判断平台风险性
     */
    @RequestMapping("/hls/bp/master/platform/risk/query")
    public ResponseData queryPlatformRisk(HttpServletRequest request,
                                          @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMaster dto = param.toJavaObject(HlsCusBpMaster.class);
        return service.queryPlatformRisk(requestCtx, dto);
    }

    @RequestMapping(value = "/hls/bp/query/master/detail/prj", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryCusBpMasterDetailsForPrj(@ModelAttribute("_request_data") LeafRequestData requestData,HttpServletRequest request,
                                                      @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMaster metadataRelation = param.toJavaObject(HlsCusBpMaster.class);

        String bpType = request.getParameter("bp_type");
        String bpClass = request.getParameter("bp_class");
        String projectId = request.getParameter("project_id");
        String userId = request.getParameter("user_id");
        if(bpType != null){
            metadataRelation.setBpType(bpType);
        }
        if(bpClass != null){
            metadataRelation.setBpClass(bpClass);
        }
//        if(projectId != null){
//            metadataRelation.setProjectId(Long.valueOf(projectId));
//        }
        if(userId != null && bpType.equals("TENANT") ){
            metadataRelation.setUserId(Long.valueOf(userId));
        }
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("wflRuleControlFlag", "Y");
        List<HlsCusBpMaster> list = service.queryCusBpMasterDetailsForPrj(requestCtx,metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/bp/master/queryBySky")
    @ResponseBody
    public ResponseData updatePrjProjectTenantId(HlsCusBpMaster dto, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param1 = (JSONObject)requestData.get("parameter");
        String bp_name = String.valueOf(param1.get("bp_name"));
        String sky_type = String.valueOf(param1.get("sky_type"));
        String bpCode = String.valueOf(param1.get("bp_code"));
        String registerCertNum = String.valueOf(param1.get("register_cert_num"));
        String sId = String.valueOf(param1.get("bp_id"));
        if(!"".equals(sId)&&!"null".equals(sId)){
            Long bp_id = Long.valueOf(sId);
            dto.setBpId(bp_id);
        }
        dto.setBpName(bp_name);
        dto.setSkyType(sky_type);
        dto.setBpCode(bpCode);
        dto.setRegisterCertNum(registerCertNum);
        Map map = service.queryBySky(requestContext,dto,page,pageSize);
        HlsCusBpMaster hlsCusBpMaster2 = (HlsCusBpMaster) map.get("hlsCusBpMaster");
        List<HlsCusBpMaster> hlsCusBpMasters = new ArrayList();
        hlsCusBpMasters.add(hlsCusBpMaster2);
        // return new ResponseData(("0".equals(map.get("error_code"))||"300000".equals(map.get("error_code"))),String.valueOf(hlsCusBpMaster2.getBpId()));
        return new ResponseData(hlsCusBpMasters);
    }

    /**
     * 主机厂准入提交审批流程
     *
     * @param request
     * @param requestData
     * @param
     * @return
     */
    @RequestMapping({"/hls/bp/master/wfl/submit"})
    @ResponseBody
    public ResponseData wflSubmit(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map para = requestData.getParameter();
        HlsCusBpMaster bpMaster = new HlsCusBpMaster();
        Long allocationId = null;
        Long bpRelationId = null;
        if (para.get("bpId") != null) {
            bpMaster.setBpId(Long.valueOf(para.get("bpId").toString()));
            bpMaster = this.service.selectByPrimaryKey(iRequest, bpMaster);

            String bpType = service.queryBpTypeByRole(bpMaster.getBpId());
//            if (para.get("bpRelationId") != null) {
//                bpRelationId = Long.valueOf(para.get("bpRelationId").toString());
//            }
//            if (StringUtils.equals(bpType, DISTRIBUTOR)) {
//                if (para.get("allocationId") == null) {
//                    return new ResponseData(false, "未找到复核人信息，请确认后再提交！");
//                } else {
//                    allocationId = Long.valueOf(para.get("allocationId").toString());
//                }
/*注释
                String checkString = riskBpAdmitService.checkRiskDistributor(bpMaster,bpRelationId);
                if (StringUtils.isNotEmpty(checkString)) {
                    return new ResponseData(false, checkString);
                }
*/
            }
//            String approveStatus = StringUtils.isNotBlank(bpMaster.getBpApproveStatus()) ? bpMaster.getBpApproveStatus().toUpperCase() : NEW_STATUS;
//
//            if (!StringUtils.equals(approveStatus, NEW) && !StringUtils.equals(approveStatus, REJECTED) && !StringUtils.equals(approveStatus, CANCEL)) {
//
//                return new ResponseData(false, "此商业伙伴已审批,不能重复审批");
//            } else {
//                this.hlsBpMasterService.bpWflSubmit(iRequest, bpMaster, allocationId, bpMasterRelation);
//                return new ResponseData();
//            }

//            String admitStatus = para.get("admitStatus").toString();
//            if (!StringUtils.equals(admitStatus, NEW) && !StringUtils.equals(admitStatus, REJECTED) && !StringUtils.equals(admitStatus, CANCEL) && StringUtils.isNotEmpty(admitStatus)) {
//                return new ResponseData(false, "此商业伙伴的伙伴关系准入状态不符合提交要求,不能提交审批！");
//            } else {
//                this.service.bpWflSubmit(iRequest, bpMaster, allocationId, bpRelationId);
//                return new ResponseData();
//            }
//
//        } else {
//            return new ResponseData(false, "未找到提交的商业伙伴,请联系管理员!");
//        }
//        this.service.bpWflSubmit(iRequest, bpMaster, allocationId, bpRelationId);
        this.service.bpWflSubmit(iRequest, bpMaster);
        return new ResponseData();
    }


    @RequestMapping({"/hls/bp/master/getCityIdAndProvinceId"})
    @ResponseBody
    public ResponseData getCityIdAndProvinceIdByDistrictId(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long districtId = (Long) param.get("district_id");

        return new ResponseData(service.getCityIdAndProvinceIdByDistrictId(districtId));
    }


    }
