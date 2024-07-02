package com.hand.hls.activiti.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;
import com.hand.hls.activiti.dto.HlsCusActOnlineMeetingMember;
import com.hand.hls.activiti.mapper.HlsCusActOnlineMeetingMemberMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingJudgeService;
import com.hand.hls.activiti.service.HlsCusActMeetingService;
import com.hand.hls.activiti.service.HlsCusActOnlineMeetingMemberService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目上会评审成员Controller
 * Created by qixiang.shao on 2017/12/11
 */
@Controller
public class HlsCusActOnlineMeetingMemberController extends BaseController {

    @Autowired
    private HlsCusActOnlineMeetingMemberService hlsCusActOnlineMeetingMemberService;
    @Autowired
    private HlsCusActOnlineMeetingMemberMapper hlsCusActOnlineMeetingMemberMapper;
    @Autowired
    private HlsCusActMeetingService hlsCusActMeetingService;
    @Autowired
    private HlsCusActMeetingJudgeService hlsCusActMeetingJudgeService;


    /**
     * 线上评审委员会成员插入
     *
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value = "/wfl/act/online/meeting/member/insert")
    @ResponseBody
    public ResponseData insertOnlineMeetingMember(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMember = param.toJavaObject(HlsCusActOnlineMeetingMember.class);
        try {
            IRequest iRequest = createRequestContext(request);
            List<HlsCusActOnlineMeetingMember> hlsCusActOnlineMeetingMemberList = new ArrayList<HlsCusActOnlineMeetingMember>();
            //先将已插入的删除
            List<HlsCusActOnlineMeetingMember> list = new ArrayList<HlsCusActOnlineMeetingMember>();
            List<HlsCusActMeetingJudge> hlsCusActMeetingJudgeList = new ArrayList<>();
            /*list = hlsCusActOnlineMeetingMemberService.queryMeetingMemberByProcessInstanceId(hlsCusActOnlineMeetingMember.getProcessInstanceId());*/
            list = hlsCusActOnlineMeetingMemberMapper.queryMeetingMemberByDocumentInfo(hlsCusActOnlineMeetingMember.getProjectId(), hlsCusActOnlineMeetingMember.getProjectDocumentCategory());
            Long meetingIdOld = 0L;
            if (list.size() > 0) {
                meetingIdOld = list.get(0).getMeetingId();
                hlsCusActOnlineMeetingMemberService.batchDelete(list);
            }
            /*同时删除act_meeting_judge*/
            HlsCusActMeetingJudge hlsCusActMeetingJudge1 = new HlsCusActMeetingJudge();
            if (meetingIdOld != 0) {
                hlsCusActMeetingJudge1.setMeetingId(meetingIdOld);
                List<HlsCusActMeetingJudge> hlsCusActMeetingJudgeList1 = hlsCusActMeetingJudgeService.select(iRequest, hlsCusActMeetingJudge1, 1, 99999);
                if (hlsCusActMeetingJudgeList1.size() > 0) {
                    hlsCusActMeetingJudgeService.batchDelete(hlsCusActMeetingJudgeList1);
                }
            }
            String employeeCodeString = hlsCusActOnlineMeetingMember.getEmployeeCodeString();
            String[] employeeCodeArr = employeeCodeString.split("\\|");
            for (int index = 0; index < employeeCodeArr.length; index++) {
                HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMemberAdd = new HlsCusActOnlineMeetingMember();
                hlsCusActOnlineMeetingMemberAdd.setOnlineMeetingMemberCode(employeeCodeArr[index]);
                hlsCusActOnlineMeetingMemberAdd.setProjectId(hlsCusActOnlineMeetingMember.getProjectId());
                hlsCusActOnlineMeetingMemberAdd.setProjectDocumentCategory(hlsCusActOnlineMeetingMember.getProjectDocumentCategory());
                hlsCusActOnlineMeetingMemberAdd.setProcessInstanceId(hlsCusActOnlineMeetingMember.getProcessInstanceId());
                Long meetingId = hlsCusActMeetingService.queryMeetingIdByProcessInstanceId(iRequest, hlsCusActOnlineMeetingMember.getProcessInstanceId());
                hlsCusActOnlineMeetingMemberAdd.setMeetingId(meetingId);
                hlsCusActOnlineMeetingMemberAdd.set__status("add");
                hlsCusActOnlineMeetingMemberList.add(hlsCusActOnlineMeetingMemberAdd);
                /*插入act_meeting_judge*/
                HlsCusActMeetingJudge hlsCusActMeetingJudge = new HlsCusActMeetingJudge();
                hlsCusActMeetingJudge.setJudgeMemberCode(employeeCodeArr[index]);
                hlsCusActMeetingJudge.setProcessInstanceId(hlsCusActOnlineMeetingMember.getProcessInstanceId());
                hlsCusActMeetingJudge.setMeetingId(meetingId);
                hlsCusActMeetingJudge.set__status("add");
                hlsCusActMeetingJudgeList.add(hlsCusActMeetingJudge);
            }
            hlsCusActMeetingJudgeService.batchUpdate(iRequest, hlsCusActMeetingJudgeList);
            return new ResponseData(hlsCusActOnlineMeetingMemberService.batchUpdate(iRequest, hlsCusActOnlineMeetingMemberList));
        } catch (Exception e) {
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会评审委员会信息保存失败,请重新尝试");
            return error;
        }

    }


    /**
     * 线下评审委员会确认
     *
     * @param request
     * @param hlsCusActOnlineMeetingMember
     * @return
     */
    @RequestMapping(value = "wfl/act/offline/meeting/member/insert")
    @ResponseBody
    public ResponseData insertOfflineMeetingMember(HttpServletRequest request, @RequestBody HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMember) {
        try {
            IRequest iRequest = createRequestContext(request);
            List<HlsCusActOnlineMeetingMember> hlsCusActOnlineMeetingMemberList = new ArrayList<HlsCusActOnlineMeetingMember>();
            String employeeCodeString = hlsCusActOnlineMeetingMember.getEmployeeCodeString();
            String[] employeeCodeArr = employeeCodeString.split("\\|");
            for (int index = 0; index < employeeCodeArr.length; index++) {
                HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMemberAdd = new HlsCusActOnlineMeetingMember();
                hlsCusActOnlineMeetingMemberAdd.setOnlineMeetingMemberCode(employeeCodeArr[index]);
                hlsCusActOnlineMeetingMemberAdd.setProcessInstanceId(hlsCusActOnlineMeetingMember.getProcessInstanceId());
                Long meetingId = hlsCusActMeetingService.queryMeetingIdByProcessInstanceId(iRequest, hlsCusActOnlineMeetingMember.getProcessInstanceId());
                hlsCusActOnlineMeetingMemberAdd.setMeetingId(meetingId);
                hlsCusActOnlineMeetingMemberAdd.set__status("add");
                hlsCusActOnlineMeetingMemberList.add(hlsCusActOnlineMeetingMemberAdd);
            }
            return new ResponseData(hlsCusActOnlineMeetingMemberService.batchUpdate(iRequest, hlsCusActOnlineMeetingMemberList));
        } catch (Exception e) {
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会评审委员会信息保存失败,请重新尝试");
            return error;
        }

    }


}
