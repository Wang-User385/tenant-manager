package com.hand.hls.prj.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpAttachment;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.service.BpSignService;
import com.hand.hls.prj.service.SignService;
import com.hand.hls.sign.dto.SignVerify;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author shigure 2022/11/29 16:52
 */
@Controller
public class SignController extends BaseController {

    @Autowired
    private SignService signService;

    @Autowired
    private HlsCusBpMasterMapper bpMasterMapper;

    @Qualifier("npTenantSignServiceImpl")
    @Autowired
    private BpSignService npTenantSignServiceImpl;

    @Qualifier("orgTenantSignServiceImpl")
    @Autowired
    private BpSignService orgTenantSignServiceImpl;

    @RequestMapping(value = {"/elec-verify/create/record"})
    @ResponseBody
    public ResponseData createVerify(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        SignVerify dto = param.toJavaObject(SignVerify.class);
        List<SignVerify> verifyList = signService.createVerify(requestCtx, dto);
        return new ResponseData(verifyList);
    }

    @RequestMapping(value = {"/elec-verify/create/attachment"})
    @ResponseBody
    public ResponseData createVerifyAttachment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusBpAttachment dto = param.toJavaObject(HlsCusBpAttachment.class);
        List<HlsCusBpAttachment> attachmentList = signService.createVerifyAttachment(requestCtx, dto);
        return new ResponseData(attachmentList);
    }

    @RequestMapping(value = {"/elec-verify/verify"})
    @ResponseBody
    public ResponseData verify(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        SignVerify dto = param.toJavaObject(SignVerify.class);
        List<SignVerify> verifyList = orgTenantSignServiceImpl.verify(requestCtx, dto);
        return new ResponseData(verifyList);
    }

    @PostMapping(value = {"/r/api/elec-sign/company-auth"})
    @ResponseBody
    public JSONObject companyAuthFinish(HttpServletRequest request, @RequestBody CompanyAuthMessageDTO messageDTO) {
        return orgTenantSignServiceImpl.verifyFinish(RequestHelper.getCurrentRequest(true),messageDTO);
    }

    @RequestMapping(value = {"/elec-verify/query-sign"})
    @ResponseBody
    public ResponseData querySign(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        PrjProject dto = param.toJavaObject(PrjProject.class);
        SignResponseDto signResponseDto = signService.querySign(requestCtx, SignService.PROJECT_SOURCE_DOC_CATEGORY, dto.getProjectId());
        return new ResponseData(Collections.singletonList(signResponseDto));
    }

    @RequestMapping(value = {"/elec-sign/org-records"})
    @ResponseBody
    public ResponseData queryOrgSignProject(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,  HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Map<String, Object> project = param.toJavaObject(Map.class);
        List<Map> result = signService.orgSignProjectQuery(requestContext, project,pagenum, pagesize);
        return new ResponseData(result);
    }

    @RequestMapping(value = {"/elec-verify/sign"})
    @ResponseBody
    public ResponseData orgSign(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusPrjProject dto = param.toJavaObject(HlsCusPrjProject.class);
        // 现在默认是主机厂，可能需要改
        HlsCusBpMaster hlsCusBpMaster = bpMasterMapper.selectByPrimaryKey(dto.getFactoryId());
        orgTenantSignServiceImpl.sign(hlsCusBpMaster,dto.getProjectId(),SignService.PROJECT_SOURCE_DOC_CATEGORY,requestCtx,"丙方");
        return new ResponseData(Collections.singletonList(dto));
    }

    @PostMapping(value = {"/r/api/elec-sign/finish"})
    @ResponseBody
    public JSONObject signFinish(HttpServletRequest request, @RequestBody SignatureSendMessageDTO messageDTO) {
        RequestHelper.setCurrentRequest(RequestHelper.getCurrentRequest(true));
        return signService.processResultNew(messageDTO);
    }

    /**
     * 电签测试用接口
     */
    @RequestMapping(value = "/test/interface/sign")
    @ResponseBody
    public ResponseData testSign(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        HlsCusBpMaster master = new HlsCusBpMaster();
        master.setBpId(981L);
        HlsCusBpMaster hlsCusBpMaster = bpMasterMapper.selectByPrimaryKey(master);
        npTenantSignServiceImpl.sign(hlsCusBpMaster,12L,SignService.PROJECT_SOURCE_DOC_CATEGORY, RequestHelper.getCurrentRequest(true),"乙方");
        return new ResponseData();
    }
}
