package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.*;
import com.hand.hls.bp.service.*;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectBpService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class HlsCusPrjProjectBpController extends BaseController {

    @Autowired
    private HlsCusPrjProjectBpService service;

    @Autowired
    private HlsCusIBpShareholderInformationService shareholderService;

    @Autowired
    private HlsCusBpSeniorPersionService hlsCusBpSeniorPersionService;

    @Autowired
    private HlsCusBpCreditRateService hlsCusBpCreditRateService;

    @Autowired
    private HlsCusBpCreditHistoricalService hlsCusBpCreditHistoricalService;

    @Autowired
    private HlsCusBpCreditMotherlineService hlsCusBpCreditMotherlineService;

    @Autowired
    private HlsCusBpAntiLaunderingService hlsCusBpAntiLaunderingService;

    @Autowired
    private HlsCusIBpHistoricalEvolutionService hlsCusIBpHistoricalEvolutionService;

    @Autowired
    private HlsCusBpMasterAddressService hlsCusBpMasterAddressService;

    @Autowired
    private HlsCusBpMasterContactInfoService hlsCusBpMasterContactInfoService;

    @Autowired
    private HlsCusPrjProjectService prjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    HlsCusHlsCreditLineChanceBpService hlsCusHlsCreditLineChanceBpService;

    @RequestMapping(value = "/ct/prj/project/bp/before/query")
    @ResponseBody
    public ResponseData queryBefore(HlsCusPrjProjectBp dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.prjProjectBpBeforeInfoQuery(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/prj/project/bp/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectBp dto = param.toJavaObject(HlsCusPrjProjectBp.class);
        dto.setBpName(null);
        dto.setBpRoleType(null);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPrjProjectBp> list=service.prjProjectBpInfoQuery(requestContext, dto, page, pageSize);
        if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusPrjProjectBp>() {
                @Override
                public int compare(HlsCusPrjProjectBp o1, HlsCusPrjProjectBp o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        return new ResponseData(list);
    }

    //地址信息
    @RequestMapping(value = "/ct/prj/project/bp/address/query")
    @ResponseBody
    public ResponseData bpAddressQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMasterAddress dto = param.toJavaObject(HlsCusBpMasterAddress.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusBpMasterAddress> list=hlsCusBpMasterAddressService.selectbpAddress(dto, requestContext, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //联系人信息
    @RequestMapping(value = "/ct/prj/project/bp/contactInfo/query")
    @ResponseBody
    public ResponseData bpContactInfoQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMasterContactInfo dto = param.toJavaObject(HlsCusBpMasterContactInfo.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusBpMasterContactInfo> list=hlsCusBpMasterContactInfoService.queryAll(dto, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //授权办理业务人信息
    @RequestMapping(value = "/ct/prj/project/bp/contactInfo/queryAllAuthorize")
    @ResponseBody
    public ResponseData bpContactInfoAuthorizeQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMasterContactInfo dto = param.toJavaObject(HlsCusBpMasterContactInfo.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusBpMasterContactInfo> list=hlsCusBpMasterContactInfoService.queryAllAuthorize(dto, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //受益人信息
    @RequestMapping(value = "/ct/prj/project/bp/contactInfo/queryAllBenifit")
    @ResponseBody
    public ResponseData bpContactInfoBenifitQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMasterContactInfo dto = param.toJavaObject(HlsCusBpMasterContactInfo.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusBpMasterContactInfo> list=hlsCusBpMasterContactInfoService.queryAllBenifit(dto, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //历史往来
    @RequestMapping(value = "/ct/prj/project/bp/creditHistorical/query")
    @ResponseBody
    public ResponseData bpCreditHistoricalQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        BpCreditHistorical dto = param.toJavaObject(BpCreditHistorical.class);
        IRequest requestContext = createRequestContext(request);
        List<BpCreditHistorical> list=hlsCusBpCreditHistoricalService.selectCreditHistoricalByBpId(dto, requestContext, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //与母行过去三年历史往来
    @RequestMapping(value = "/ct/prj/project/bp/creditMotherline/query")
    @ResponseBody
    public ResponseData bpCreditMotherlineQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        BpCreditMotherline dto = param.toJavaObject(BpCreditMotherline.class);
        IRequest requestContext = createRequestContext(request);
        List<BpCreditMotherline> list=hlsCusBpCreditMotherlineService.selectCreditMotherlineByBpId(dto, requestContext, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //股东信息
    @RequestMapping(value = "/ct/prj/project/bp/shareholder/query")
    @ResponseBody
    public ResponseData shareholderQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpShareholderInformation dto = param.toJavaObject(HlsCusBpShareholderInformation.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusBpShareholderInformation> list=shareholderService.selectAll(requestContext, dto, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }


    //高管信息
    @RequestMapping(value = "/ct/prj/project/bp/seniorPersion/query")
    @ResponseBody
    public ResponseData seniorPersionQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpSeniorPersion dto = param.toJavaObject(HlsCusBpSeniorPersion.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusBpSeniorPersion> list=hlsCusBpSeniorPersionService.querySeniorByBpId(dto, requestContext, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //信用评级
    @RequestMapping(value = "/ct/prj/project/bp/credit/query")
    @ResponseBody
    public ResponseData bpCreditQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsScoreCalculation dto = param.toJavaObject(HlsScoreCalculation.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsScoreCalculation> list=hlsCusBpCreditRateService.selectCreditRateByBpIdPrj(dto, requestContext, page, pageSize);
        return new ResponseData(list);
    }

    //反洗钱信息
    @RequestMapping(value = "/ct/prj/project/bp/AntiLaundering/query")
    @ResponseBody
    public ResponseData bpAntiLaunderingQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        BpAntiLaundering dto = param.toJavaObject(BpAntiLaundering.class);
        IRequest requestContext = createRequestContext(request);
        List<BpAntiLaundering> list=hlsCusBpAntiLaunderingService.selectAntiLaunderingByBpId(dto, requestContext, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }

    //大事件
    @RequestMapping(value = "/ct/prj/project/bp/historicalEvolution/query")
    @ResponseBody
    public ResponseData bpHistoricalEvolutionQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpHistoricalEvolution dto = param.toJavaObject(HlsCusBpHistoricalEvolution.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusBpHistoricalEvolution> list=hlsCusIBpHistoricalEvolutionService.selectAll(requestContext,dto, page, pageSize);
        /*if(!CollectionUtils.isEmpty(list)){
            //集合排序-正序
            Collections.sort(list, new Comparator<HlsCusBpShareholderInformation>() {
                @Override
                public int compare(HlsCusBpShareholderInformation o1, HlsCusBpShareholderInformation o2) {
                    if("TENANT".equalsIgnoreCase(o2.getBpType())){
                        return 1;
                    }
                    return -1;
                }
            });
        }*/
        return new ResponseData(list);
    }


    @RequestMapping(value = "/ct/prj/project/bp/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProjectBp> dto = param.toJavaList(HlsCusPrjProjectBp.class);
        if (dto != null) {
            for (HlsCusPrjProjectBp dt : dto) {
                if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                    dt.set__status("add");
                } else {
                    dt.set__status("update");
                }
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }


    @RequestMapping(value = "/hls/cus/prj/project/bp/query")
    @ResponseBody
    public ResponseData prjProjectBpQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectBp dto = param.toJavaObject(HlsCusPrjProjectBp.class);
        Long projectId = dto.getProjectId();
        if (projectId == null) {
            projectId = 0L;
        }
        dto.setProjectId(projectId);
        dto.setBpName(null);
        return new ResponseData(service.prjProjectBpQuery(dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/cus/pledge/mortgagor/query")
    @ResponseBody
    public ResponseData selectPledgeAndMortgagor(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectBp dto = param.toJavaObject(HlsCusPrjProjectBp.class);
        return new ResponseData(service.selectPledgeAndMortgagor(requestCtx,dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/cus/prj/project/bp/updateTenantId")
    @ResponseBody
    public ResponseData updatePrjProjectTenantId(HlsCusPrjProjectBp dto, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param1 = (JSONObject)requestData.get("parameter");
        Long project_id = Long.valueOf(String.valueOf(param1.get("project_id")));
        //Long tenant_id = Long.valueOf(String.valueOf(param1.get("tenant_id")));
       // String id = String.valueOf(param1.get("tenant_id"));
        JSONArray id2 = (JSONArray) param1.get("tenant_id");
//        for(int i=0;i<id2.size();i++){
//
//        }
        Long tenant_id = Long.valueOf(String.valueOf(id2.get(0)));
        //System.out.println(tenant_id);
//        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
//        hlsCusPrjProject.setProjectId(project_id);
//        hlsCusPrjProject.setTenantId(tenant_id);
        hlsCusPrjProjectMapper.updatePrjTenant(tenant_id,project_id);
        return new ResponseData();
    }

}