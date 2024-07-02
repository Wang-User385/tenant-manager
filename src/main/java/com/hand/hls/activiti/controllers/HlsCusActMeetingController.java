package com.hand.hls.activiti.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.activiti.dto.HlsCusActMeeting;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;
import com.hand.hls.activiti.dto.HlsCusActOnlineMeetingMember;
import com.hand.hls.activiti.mapper.HlsCusActOnlineMeetingMemberMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingJudgeService;
import com.hand.hls.activiti.service.HlsCusActMeetingService;
import com.hand.hls.activiti.service.HlsCusActOnlineMeetingMemberService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 工作流上会信息Controller
 * @Date: Created in 16:24 2017/12/17
 * @Modified By:
 */
@Controller
public class HlsCusActMeetingController extends BaseController {

    @Autowired
    private HlsCusActMeetingService hlsCusActMeetingService;
    @Autowired
    private HlsCusActOnlineMeetingMemberService hlsCusActOnlineMeetingMemberService;
    @Autowired
    private HlsCusActOnlineMeetingMemberMapper hlsCusActOnlineMeetingMemberMapper;
    @Autowired
    private HlsCusActMeetingJudgeService hlsCusActMeetingJudgeService;

    /**
     * 上会会议信息保存
     */
    @RequestMapping(value = "/wfl/act/meeting/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData meetingSave(HttpServletRequest request,  @RequestBody HlsCusActMeeting hlsCusActMeeting) {

        try {
            IRequest iRequest = createRequestContext(request);

            Long meetingId = hlsCusActMeetingService.queryMeetingIdByDetails(iRequest, hlsCusActMeeting);
            if(hlsCusActMeeting.getMeetingTime() != null){
                hlsCusActMeeting.setMeetingDate(new SimpleDateFormat("YYYY-MM-DD").parse(hlsCusActMeeting.getMeetingTime()));
            }
            if (meetingId == null) {
                /*会签时用documentId和documentCategory查询上会评审信息*/
                HlsCusActMeeting meeting = new HlsCusActMeeting();
                meeting.setDocumentCategory(hlsCusActMeeting.getDocumentCategory());
                meeting.setDocumentId(hlsCusActMeeting.getDocumentId());
                meetingId = hlsCusActMeetingService.queryMeetingIdByDetails(iRequest, meeting);
                if (meetingId != null) {
                    hlsCusActMeeting.setMeetingId(meetingId);
                    hlsCusActMeetingService.updateByPrimaryKeySelective(iRequest, hlsCusActMeeting);
                } else {
                    hlsCusActMeetingService.insertSelective(iRequest, hlsCusActMeeting);
                }
            }else{
                hlsCusActMeeting.setMeetingId(meetingId);
                hlsCusActMeetingService.updateByPrimaryKeySelective(iRequest, hlsCusActMeeting);
            }

            /*保存评审委员信息*/
            List<HlsCusActOnlineMeetingMember> members = hlsCusActMeeting.getHlsCusActOnlineMeetingMemberList();
            for(HlsCusActOnlineMeetingMember member: members){
                member.setMeetingId(hlsCusActMeeting.getMeetingId());
                member.setProjectId(hlsCusActMeeting.getDocumentId());
                member.setProjectDocumentCategory(hlsCusActMeeting.getDocumentCategory());
                hlsCusActOnlineMeetingMemberService.insertSelective(iRequest,member);
            }

            return new ResponseData(true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会会议信息保存失败,请重新尝试");
            return error;

        }
    }


    /**
     * 更新上会综合意见
     * @param request
     * @param hlsCusActMeeting
     * @return
     */
    @RequestMapping(value = "/wfl/act/meeting/comment/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData meetingCommentSave(HttpServletRequest request, @RequestBody final HlsCusActMeeting hlsCusActMeeting) {
        try {
            IRequest iRequest = createRequestContext(request);
            hlsCusActMeetingService.updateMeetingComprehensiveComment(iRequest, hlsCusActMeeting);
            return new ResponseData(true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会综合意见保存失败,请重新尝试");
            return error;

        }
    }


    /**
     * 根据工作流实例Id查询上会信息
     */
    @RequestMapping(value = "/wfl/act/meeting/query")
    @ResponseBody
    public ResponseData meetingQueryByProcessInstanceId(HttpServletRequest request, Long processInstanceId, Long documentId, String documentCategory) {
        try {
            IRequest iRequest = createRequestContext(request);
            List<HlsCusActMeeting> hlsCusActMeetings = new ArrayList<>();
            hlsCusActMeetings = hlsCusActMeetingService.queryMeetingDetailByProcessInstanceId(iRequest, processInstanceId);
            return new ResponseData(hlsCusActMeetings);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会信息查询失败，请重新尝试");
            return error;
        }
    }
    /**
     * 根据工作流实例Id更新上会类型
     */
    @RequestMapping(value = "/wfl/act/meetingType/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData updateMeetingType(HttpServletRequest request, @RequestBody final HlsCusActMeeting hlsCusActMeeting) {
        try {
            IRequest iRequest = createRequestContext(request);
            hlsCusActMeetingService.updateMeetingType(hlsCusActMeeting);
            return new ResponseData(true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会信息查询失败，请重新尝试");
            return error;
        }
    }


}
