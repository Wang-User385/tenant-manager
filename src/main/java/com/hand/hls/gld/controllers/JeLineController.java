package com.hand.hls.gld.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.components.EasCredentialsSycnExecutor;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeHead;
import com.hand.hls.gld.dto.JeLine;
import com.hand.hls.gld.service.IJeHeadService;
import com.hand.hls.gld.service.IJeLineService;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JeLineController extends BaseController {
    @Autowired
    private IJeLineService service;
    @Autowired
    private IJeHeadService jeHeadService;
    @Autowired
    private IJeLineService jeLineService;

    @Autowired
    private EasCredentialsSycnExecutor easCredentialsSycnExecutor;



    public JeLineController() {
    }

    @RequestMapping({"/gld/je/line/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(value = "pagenum",defaultValue = "1") int page, @RequestParam(value = "pagesize",defaultValue = "10") int pageSize, @RequestParam Long jeHeadId, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Long companyId = (Long)session.getAttribute("companyId");
        JSONObject param = (JSONObject)requestData.get("parameter");
        JeLine dto = (JeLine)param.toJavaObject(JeLine.class);
        if (jeHeadId != null) {
            dto.setJeHeadId(jeHeadId);
        }

        return new ResponseData(this.service.query(dto, page, pageSize));
    }

    @RequestMapping({"/gld/je/line/queryCompany"})
    @ResponseBody
    public ResponseData queryCompany(JeLine dto, HttpSession session) {
        Long companyId = (Long)session.getAttribute("companyId");
        dto.setCompanyId(companyId);
        return new ResponseData(this.service.queryCompany(dto));
    }

    @RequestMapping({"/gld/je/line/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<JeLine> dto = param.toJavaList(JeLine.class);
        return new ResponseData(this.service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping({"/gld/je/line/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<JeLine> dto = param.toJavaList(JeLine.class);
        this.service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 凭证复制创建
     *
     * @param request
     * @param jeHeadId
     * @return
     */
    @RequestMapping(value = "/gld/je/head/copy/create")
    public ResponseData gldCopyCreate(HttpServletRequest request, @RequestParam String jeHeadId) {
        IRequest iRequest = createRequestContext(request);
        List<JeLine> jeLineList = new ArrayList<JeLine>();
        HlsCusJeHead jeHead = new HlsCusJeHead();
        JeLine jeLine = new JeLine();

        if (jeHeadId != null) {
            jeHead.setJeHeadId(Long.valueOf(jeHeadId));
            jeHead = jeHeadService.selectByPrimaryKey(iRequest, jeHead);
            jeHead.setJeHeadId(null);
            JeHead jeHeadResult = jeHeadService.insertSelective(iRequest, jeHead);

            jeLine.setJeHeadId(Long.valueOf(jeHeadId));
            jeLineList = jeLineService.selectSelective(iRequest, jeLine);
            for (JeLine item : jeLineList) {
                item.setJeLineId(null);
                item.setJeHeadId(jeHeadResult.getJeHeadId());
                jeLineService.insertSelective(iRequest, item);
            }
        }
        return new ResponseData();
    }


    /**
     * 更新头状态
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping({"/gld/je/head/submit"})
    @ResponseBody
    public ResponseData updateHead(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData,@RequestParam String jeStatus) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray)requestData.get("parameter");

        List<HlsCusJeHead> dto = param.toJavaList(HlsCusJeHead.class);

        List<JeHead> postJeHeadList = new ArrayList();
        String postFlag="N";

        for(HlsCusJeHead jeHead : dto){
            jeHead.setJeStatus(jeStatus);
            if("TRIAL".equals(jeStatus)){
                jeHead.setTrialedBy(requestCtx.getUserId());
            }
            if("CONFIRM".equals(jeStatus)){
                jeHead.setRetrialedBy(requestCtx.getUserId());

                JeHead postJeHead=new JeHead();
                postJeHead.setJeHeadId(jeHead.getJeHeadId());
                postJeHeadList.add(postJeHead);
                postFlag="Y";
            }
            jeHead.set__status("update");
        }

        //由于接口中需要复审人字段所以需要在调用接口前更新
        dto=this.jeHeadService.batchUpdate(requestCtx, dto);

        //凭证复审通过自动调用凭证传输
        if("Y".equals(postFlag)){
            Long sessionId=requestCtx.getAttribute("session_id");
            HlsCusPsotEasTmp dtoTmp =new HlsCusPsotEasTmp();
            dtoTmp.setSessionId(sessionId);
            dtoTmp.setJeHeadList(postJeHeadList);
            try {
                easCredentialsSycnExecutor.fun(requestCtx,dtoTmp);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return new ResponseData(dto);
    }

    @RequestMapping({"/gld/je/head/query"})
    @ResponseBody
    public ResponseData headQuery(@ModelAttribute("_request_data") LeafRequestData requestData,HlsCusJeHead jeHead, @RequestParam(value = "pagenum",defaultValue = "1") int page, @RequestParam(value = "pagesize",defaultValue = "10") int pageSize, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Long companyId = (Long)session.getAttribute("companyId");
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusJeHead dto = (HlsCusJeHead)param.toJavaObject(HlsCusJeHead.class);
        if(jeHead.getJeStatus() != null){
            dto.setJeStatus(jeHead.getJeStatus());
        }
        if(jeHead.getTrialFlag() != null){
            dto.setTrialFlag(jeHead.getTrialFlag());
        }

        return new ResponseData(jeHeadService.selectHeadInfo(requestContext,dto, page, pageSize));
    }

    @RequestMapping({"/gld/je/line/group/query"})
    @ResponseBody
    public ResponseData lineGroupLine(@ModelAttribute("_request_data") LeafRequestData requestData,
                                      @RequestParam(value = "pagenum",defaultValue = "1") int page,
                                      @RequestParam(value = "pagesize",defaultValue = "10") int pagesize,
                                      HttpServletRequest request, HttpSession session) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject)requestData.get("parameter");
        JeLine dto = (JeLine)param.toJavaObject(JeLine.class);

        return new ResponseData(service.selectJeLineInfoGroupAccount(requestContext,dto, page, pagesize));
    }

    @RequestMapping({"/gld/je/line/seq/query"})
    @ResponseBody
    public ResponseData selectJeLineInfo(@ModelAttribute("_request_data") LeafRequestData requestData,
                                      @RequestParam(value = "pagenum",defaultValue = "1") int page,
                                      @RequestParam(value = "pagesize",defaultValue = "10") int pageSize,
                                      HttpServletRequest request, HttpSession session) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject)requestData.get("parameter");
        JeLine dto = (JeLine)param.toJavaObject(JeLine.class);

        return new ResponseData(service.selectJeLineInfo(requestContext,dto, page, pageSize));
    }




    @RequestMapping({"/gld/je/head/post/query"})
    @ResponseBody
    public ResponseData postHeadQuery(@ModelAttribute("_request_data") LeafRequestData requestData,HlsCusJeHead jeHead, @RequestParam(value = "pagenum",defaultValue = "1") int page, @RequestParam(value = "pagesize",defaultValue = "10") int pageSize, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Long companyId = (Long) session.getAttribute("companyId");
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusJeHead dto = (HlsCusJeHead) param.toJavaObject(HlsCusJeHead.class);
        if (jeHead.getJeStatus() != null) {
            dto.setJeStatus(jeHead.getJeStatus());
        }
        if (jeHead.getTrialFlag() != null) {
            dto.setTrialFlag(jeHead.getTrialFlag());
        }

        return new ResponseData(jeHeadService.gldJeDataQuery(requestContext, dto, page, pageSize));
    }

    @RequestMapping({"/gld/je/line/export"})
    @ResponseBody
    public void jeLineExport(@ModelAttribute("_request_data") LeafRequestData requestData,HlsCusJeHead hlsCusJeHead, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        try{
            jeHeadService.jeLineExport(request, response, hlsCusJeHead);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
