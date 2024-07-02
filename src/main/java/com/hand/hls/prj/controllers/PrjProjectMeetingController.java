package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsCusPrjProjectMeeting;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMeetingMapper;
import com.hand.hls.prj.service.PrjProjectMeetingService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class PrjProjectMeetingController extends BaseController {

    @Autowired
    private PrjProjectMeetingService service;
    @Autowired
    private HlsCusPrjProjectMeetingMapper prjProjectMeetingMapper;
    @Autowired
    private PrjProjectMeetingService prjProjectMeetingService;


    @RequestMapping(value = "/prj/project/meeting/query")
    @ResponseBody
    public ResponseData query(HlsCusPrjProjectMeeting dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPrjProjectMeeting> listMeet = service.select(requestContext, dto, page, pageSize);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/prj/project/meeting/info/query")
    @ResponseBody
    public ResponseData queryInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HlsCusPrjProjectMeeting hlsCusPrjProjectMeeting, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectMeeting dto = param.toJavaObject(HlsCusPrjProjectMeeting.class);
        dto.setProjectId(hlsCusPrjProjectMeeting.getProjectId());
        List<HlsCusPrjProjectMeeting> list = service.queryInfo(requestContext, dto, page, pageSize);
        return new ResponseData(list);
    }
    @RequestMapping(value = "/prj/project/meeting/info/reply/query")
    @ResponseBody
    public ResponseData queryInfoReply(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HlsCusPrjProjectMeeting hlsCusPrjProjectMeeting, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectMeeting dto = param.toJavaObject(HlsCusPrjProjectMeeting.class);
        dto.setProjectId(hlsCusPrjProjectMeeting.getProjectId());
        List<HlsCusPrjProjectMeeting> list = service.queryInfoReply(requestContext, dto, page, pageSize);
        return new ResponseData(list);
    }


    @RequestMapping(value = "/prj/project/meeting/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest requestCtx = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProjectMeeting> dto = param.toJavaList(HlsCusPrjProjectMeeting.class);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/prj/project/meeting/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusPrjProjectMeeting> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/meeting/detail/save")
    @ResponseBody
    public ResponseData save(HttpServletRequest request, @RequestBody Map<String, Object> maps) {
        IRequest requestCtx = createRequestContext(request);
        service.save(requestCtx, maps);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/meeting/query/byprjid")
    @ResponseBody
    public List<HlsCusPrjProjectMeeting> queyMeeting(HlsCusPrjProjectMeeting dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        List<HlsCusPrjProjectMeeting> prjProjectMeetingList = prjProjectMeetingMapper.queryPrjProjectMeetingByProjectId(dto);
        return prjProjectMeetingList;
    }

    @RequestMapping(value = "/prj/project/update/comment")
    @ResponseBody
    public ResponseData updateComment(HlsCusPrjProjectMeeting dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProjectMeeting prjProjectMeeting = prjProjectMeetingService.queryMeetingByMeetingId(dto);
        prjProjectMeeting.set__status("update");
        prjProjectMeeting.setMeetingComments(dto.getMeetingComments());
        List<HlsCusPrjProjectMeeting> list = new ArrayList<>();
        list.add(prjProjectMeeting);
        prjProjectMeetingService.batchUpdate(requestCtx, list);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/query/comment")
    @ResponseBody
    public List<HlsCusPrjProjectMeeting> queryMeetingByMeetingId(HlsCusPrjProjectMeeting dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProjectMeeting prjProjectMeeting = prjProjectMeetingService.queryMeetingByMeetingId(dto);
        List<HlsCusPrjProjectMeeting> list = new ArrayList<>();
        list.add(prjProjectMeeting);
        return list;
    }

    @RequestMapping(value = "/prj/project/meeting/confirm/query")
    @ResponseBody
    public ResponseData queryConfirmMeetingInfo(HlsCusPrjProjectMeeting dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPrjProjectMeeting> list = service.queryConfirmMeetingInfo(requestContext, dto, page, pageSize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/project/meeting/submit/query")
    @ResponseBody
    public ResponseData queryConfirmMeetingSubmitInfo(HlsCusPrjProjectMeeting dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPrjProjectMeeting> list = service.queryConfirmMeetingSubmitInfo(requestContext, dto, page, pageSize);
        return new ResponseData(list);
    }


    /**
     * 保存综合意见
     * 生成 业审委综合意见
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/contract/summary/noticeFile")
    @ResponseBody
    public ResponseData createDocumentFile(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException, FileReadIOException {
        IRequest requestCtx = createRequestContext(request);

        ResponseData responseData = new ResponseData();
        JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusPrjProjectMeeting hlsCusPrjProjectMeeting = param.toJavaObject(HlsCusPrjProjectMeeting.class);

        Long projectId = hlsCusPrjProjectMeeting.getProjectId();
        try {
            prjProjectMeetingService.updateByPrimaryKeySelective(requestCtx , hlsCusPrjProjectMeeting);
            prjProjectMeetingService.createContractNoticeFile(requestCtx,hlsCusPrjProjectMeeting);
        } catch (Exception e) {
//            logger.error("生成起租通知书失败", e);
            responseData.setSuccess(false);
        }

        return responseData;
    }
}