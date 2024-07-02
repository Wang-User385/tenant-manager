package com.hand.hls.hls.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.common.components.HlsWordToPdfComponent;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationCalcService;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectMeeting;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMeetingMapper;
import com.hand.hls.prj.mapper.PrjProjectApprovalMapper;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsDurationHdController extends BaseController {

    @Autowired
    private HlsDurationHdService service;

    @Autowired
    private HlsDurationCalcService hlsDurationCalcService;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private HlsWordToPdfComponent hlsWordToPdfComponent;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;
    @Autowired
    private HlsCusPrjProjectMeetingMapper meetingMapper;
    @Autowired
    private ProjectMeetingApproverMapper approverMapper;
    @Autowired
    private PrjProjectApprovalMapper approvalMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "/hls/duration/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd dto = param.toJavaObject(HlsDurationHd.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/duration/hd/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsDurationHd> list = param.toJavaList(HlsDurationHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.submitWfl(requestCtx, list));
    }

    @RequestMapping(value = "/hls/duration/reply/submit")
    @ResponseBody
    public ResponseData fillingSummarySubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hd = new HlsDurationHd();
        hd = service.submitReplyWfl(requestCtx, param.toJavaObject(HlsDurationHd.class));
        getValidator().validate(hd, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(true);
    }

//    @RequestMapping(value = "/hls/duration/reply/submit")
//    @ResponseBody
//    public ResponseData submitReplyWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException {
//        IRequest requestCtx = createRequestContext(request);
//        RequestHelper.setCurrentRequest(requestCtx);
//        JSONArray param = (JSONArray) requestData.get("parameter");
//        List<HlsDurationHd> list = param.toJavaList(HlsDurationHd.class);
//        getValidator().validate(list, result);
//        if (result.hasErrors()) {
//            ResponseData responseData = new ResponseData(false);
//            responseData.setMessage(getErrorMessage(result, request));
//            return responseData;
//        }
//        return new ResponseData(service.submitReplyWfl(requestCtx, list));
//    }

    @RequestMapping(value = "/hls/duration/reply/cancel")
    @ResponseBody
    public ResponseData cancelReply(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsDurationHd> list = param.toJavaList(HlsDurationHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        //作废的虚拟合同需要恢 虚拟合同的状态为签约
        for (HlsDurationHd hd : list) {
            HlsCusPrjProject project = new HlsCusPrjProject();
            project.setProjectId(hd.getProjectId());
            project.setProjectStatus("APPROVED");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, project);
            //作废后 更改复制出来的数据的状态
            meetingMapper.updateMeetingStatus(hd.getProjectId(),"CANCEL");
            approverMapper.updateMeetingApproverStatus(hd.getProjectId(),"CANCEL");
            approvalMapper.updateApprovalStatus(hd.getProjectId(),"CANCEL");
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/duration/hd/cancel")
    @ResponseBody
    public ResponseData cancel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsDurationHd> list = param.toJavaList(HlsDurationHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        //作废的虚拟合同需要恢 虚拟合同的状态为签约
        for (HlsDurationHd hd : list) {
            HlsCusPrjProject project = new HlsCusPrjProject();
            project.setProjectId(hd.getProjectId());
            project.setContractStatus("SIGN");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, project);
            HlsDurationLn hlsDurationLn = new HlsDurationLn();
            hlsDurationLn.setHdId(hd.getHdId());
            List<HlsDurationLn> lnList = hlsDurationLnMapper.hlsDurationLnEtDetailQueryNew(hlsDurationLn);
            for (HlsDurationLn ln1 : lnList) {
                if (ln1.getContractId() != null) {
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setContractId(ln1.getContractId());
                    conContract.setContractStatus("INCEPT");
                    hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, conContract);
                }
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/duration/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsDurationHd> dto = parameter.toJavaList(HlsDurationHd.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


    @RequestMapping(value = "/hls/duration/hd/save")
    @ResponseBody
    public ResponseData save(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        return new ResponseData(service.hlsDurationSave(requestCtx, hlsDurationHd));
    }

    @RequestMapping(value = "/hls/duration/hd/reply/save")
    @ResponseBody
    public ResponseData saveReply(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws ParseException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        return new ResponseData(service.hlsDurationReplySave(requestCtx,session, hlsDurationHd));
    }

    @RequestMapping(value = "/hls/duration/et/calculate")
    @ResponseBody
    public ResponseData etCalculate(HttpServletRequest request,
                                    @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    @RequestParam Long lnId) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        try {
            return new ResponseData(hlsDurationCalcService.calculate(iRequest, lnId, param));
        } catch (Exception e) {
            logger.error("et calculate error:", e);
            return new ResponseData(false, e.getMessage());
        }
    }

    @RequestMapping(value = "/hls/duration/et/save")
    @ResponseBody
    public ResponseData etSave(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws ResMessageException {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        try {
            return new ResponseData(service.etSave(iRequest, hlsDurationHd));
        } catch (Exception e) {
            logger.error("et calculate error:", e);
            return new ResponseData(false, e.getMessage());
        }
    }

    @RequestMapping(value = "/hls/duration/project/save")
    @ResponseBody
    public ResponseData projectSave(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws ParseException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        return new ResponseData(service.projectSave(requestCtx, session, hlsDurationHd));
    }


    @RequestMapping(value = "/hls/duration/execute/create")
    @ResponseBody
    public ResponseData executeCreate(HttpServletRequest request, HttpSession session,
                                      @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        try {
            HlsDurationHd hd = service.executeCreate(iRequest, session, hlsDurationHd);
            List<HlsDurationHd> list = new ArrayList<>();
            list.add(hd);
            return new ResponseData(list);
        } catch (Exception e) {
            logger.error("et calculate error:", e);
            return new ResponseData(false, e.getMessage());
        }
    }

    @RequestMapping(value = "/hls/duration/execute/cancel")
    @ResponseBody
    public ResponseData executeCancel(HttpServletRequest request,
                                      @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        try {
            service.executeCancel(hlsDurationHd);
            return new ResponseData();
        } catch (Exception e) {
            logger.error("et calculate error:", e);
            return new ResponseData(false, e.getMessage());
        }
    }

    @RequestMapping(value = "/hls/duration/execute/update")
    @ResponseBody
    public ResponseData executeUpadate(HttpServletRequest request,
                                       @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        try {
            service.executeUpdate(iRequest, hlsDurationHd);
            return new ResponseData();
        } catch (Exception e) {
            logger.error("et calculate error:", e);
            return new ResponseData(false, e.getMessage());
        }
    }

    @RequestMapping(value = "/hls/duration/execute/submit")
    @ResponseBody
    public ResponseData executeSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsDurationHd> list = param.toJavaList(HlsDurationHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.executeSubmit(requestCtx, list));
    }

    @RequestMapping(value = "/hls/duration/execute/check")
    @ResponseBody
    public void executeCheck(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, Long hdId, HttpServletRequest request) throws ResMessageException {
        service.executeCheck(hdId);
    }

    @RequestMapping(value = "/hls/durationHd/query/for/prj/lov", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryHlsDurationHdForPrjLov(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request,
                                                    @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest iRequest = this.createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd metadataRelation = param.toJavaObject(HlsDurationHd.class);

        String durationNumber = request.getParameter("duration_number");
        String projectNumber = request.getParameter("project_number");
        if (durationNumber != null) {
            metadataRelation.setDurationNumber(durationNumber);
        }
        if (projectNumber != null) {
            metadataRelation.setProjectNumber(projectNumber);
        }
        //IRequest requestCtx = createRequestContext(request);
        //requestCtx.setAttribute("wflRuleControlFlag", "Y");
        List<HlsDurationHd> list = service.queryHlsDurationHdForPrjLov(iRequest, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/durationHd/query/for/prj/lov/check", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryHlsDurationHdForPrjLovCheck(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request,
                                                         @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest iRequest = this.createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject hlsCusPrjProject = param.toJavaObject(HlsCusPrjProject.class);
        String writeOffFlag = "N";
        if (hlsCusPrjProject.getProjectId() != null) {
            HlsCusPrjProject prjContract = new HlsCusPrjProject();
            prjContract.setRefProjectId(hlsCusPrjProject.getProjectId());
            IRequest requestCtx = createRequestContext(request);
            requestCtx.setAttribute("wflRuleControlFlag", "Y");
            List<HlsCusPrjProject> prjContractList = hlsCusPrjProjectService.select(requestCtx, prjContract, 1, 100000);
            if (prjContractList.size() > 0) {
                for (HlsCusPrjProject dt : prjContractList) {
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setProjectId(dt.getProjectId());
                    List<HlsCusConContract> hlsCusConContractList = hlsCusConContractService.select(requestCtx, hlsCusConContract, 1, 100000);
                    if (hlsCusConContractList.size() > 0) {
                        for (HlsCusConContract con : hlsCusConContractList) {
                            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                            hlsCusConContractCashflow.setContractId(con.getContractId());
                            List<HlsCusConContractCashflow> hlsCusConContractCashflowList =
                                    hlsCusConContractCashflowMapper.selectConCashflowNotWriteOff(hlsCusConContractCashflow);
                            if (hlsCusConContractCashflowList.size() > 0) {
                                writeOffFlag = "Y";
                                break;
                            }
                        }
                    }
                    if (writeOffFlag.equals("Y")) {
                        break;
                    }
                }
            }

        }
        List<String> list = new ArrayList<>();
        list.add(writeOffFlag);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/duration/prjContract/check")
    @ResponseBody
    public ResponseData prjContractCheck(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws ParseException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        String checkFlag = service.prjContractLeaseItemCheck(requestCtx, session, hlsDurationHd);
        List<String> list = new ArrayList<>();
        list.add(checkFlag);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/duration/approval/doc")
    @ResponseBody
    public ResponseData contentCreate(String code, String hdId, final HttpServletRequest request, HttpServletResponse response) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        if (StringUtils.isEmpty(code)) {
            throw new HlsCusException("模板文件代码为空！");
        }
        if (StringUtils.isEmpty(hdId)) {
            throw new HlsCusException("存续期信息为空！");
        }
        try {
            List<FndAttachmentMulti> multiList = service.contextCreateMultiple(requestContext, code, hdId, response);
            hlsWordToPdfComponent.wordToPdfAttachmentMuti(requestContext, multiList);
            return new ResponseData(multiList);
        } catch (Exception e) {
            logger.error("文件模版不存在!", e);
            return new ResponseData(false, "文件模版不存在!");
        }
    }

    @RequestMapping(value = "/hls/duration/hd/durationcreate")
    @ResponseBody
    public ResponseData durationCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationHd hlsDurationHd = param.toJavaObject(HlsDurationHd.class);
        HlsCusPrjProject dto = service.durationCreate(requestCtx, hlsDurationHd);
        List<HlsCusPrjProject> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(dto);
        return new ResponseData(hlsCusPrjProjectList);
    }

}
