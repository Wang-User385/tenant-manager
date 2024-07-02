package com.hand.hls.activiti.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;
import com.hand.hls.activiti.dto.HlsCusActOnlineMeetingMember;
import com.hand.hls.activiti.mapper.HlsCusActMeetingJudgeMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingJudgeService;
import com.hand.hls.activiti.service.HlsCusActMeetingService;
import com.hand.hls.activiti.service.HlsCusActOnlineMeetingMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 上会评审信息Controller
 * @Date: Created in 19:15 2017/12/17
 * @Modified By:
 */
@Controller
public class HlsCusActMeetingJudgeController extends BaseController {

    @Autowired
    private HlsCusActMeetingService hlsCusActMeetingService;
    @Autowired
    private HlsCusActMeetingJudgeService hlsCusActMeetingJudgeService;
    @Autowired
    private HlsCusActMeetingJudgeMapper hlsCusActMeetingJudgeMapper;
    @Autowired
    private HlsCusActOnlineMeetingMemberService hlsCusActOnlineMeetingMemberService;

    /**
     * 会议评审信息保存
     */
    @RequestMapping(value = "/wfl/act/meeting/judge/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData meetingJudgeSave(HttpServletRequest request, @RequestBody final HlsCusActMeetingJudge hlsCusActMeetingJudge) {
        try {
            IRequest iRequest = createRequestContext(request);
            Long processInstanceId = hlsCusActMeetingJudge.getProcessInstanceId();// 取得当前的工作流实例Id
            Long meetingId = hlsCusActMeetingService.queryMeetingIdByProcessInstanceId(iRequest, processInstanceId);
            if (meetingId == null) {
                meetingId = hlsCusActMeetingJudge.getMeetingId();
            }
            hlsCusActMeetingJudge.setMeetingId(meetingId);// 设置会议Id
            /*查出judge_member_code*/
            List<HlsCusActMeetingJudge> judgeMemberCodeList = hlsCusActMeetingJudgeMapper.queryEmployeeCodeByUserName(hlsCusActMeetingJudge);
            for (HlsCusActMeetingJudge dt : judgeMemberCodeList) {
                hlsCusActMeetingJudge.setJudgeMemberCode(dt.getJudgeMemberCode());
                HlsCusActMeetingJudge judge = new HlsCusActMeetingJudge();
                judge.setMeetingId(meetingId);
                judge.setJudgeMemberCode(hlsCusActMeetingJudge.getJudgeMemberCode());
                Long judgeId = hlsCusActMeetingJudgeService.queryJudgeIdByDetails(iRequest, judge);// 查询对应的上会评审信息是否存在
                if (judgeId != null) {
                    hlsCusActMeetingJudge.setJudgeId(judgeId);
                    hlsCusActMeetingJudgeService.updateByPrimaryKey(iRequest, hlsCusActMeetingJudge);
                    return new ResponseData(true);
                }
            }
            hlsCusActMeetingJudgeService.insertSelective(iRequest, hlsCusActMeetingJudge);
            return new ResponseData(true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("线上评审委员会审批信息提交失败,请重新尝试");
            return error;

        }
    }

    /**
     * 根据工作流实例Id查询上会评审信息
     *
     * @param request
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/wfl/act/meeting/judge/query")
    @ResponseBody
    public ResponseData meetingJudgeQueryByProcessInstanceId(HttpServletRequest request, Long processInstanceId, Long documentId, String documentCategory) {
        try {
            IRequest iRequest = createRequestContext(request);
            List<HlsCusActMeetingJudge> hlsCusActMeetingJudges = new ArrayList<>();
            hlsCusActMeetingJudges = hlsCusActMeetingJudgeService.queryAllJudgeDetailByProcessInstacneId(iRequest, processInstanceId);
            Long meetingId = -1L;
            if (hlsCusActMeetingJudges.size() == 0) {
                /*会签时用documentId和documentCategory查询上会评审信息*/
                HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMember = new HlsCusActOnlineMeetingMember();
                hlsCusActOnlineMeetingMember.setProjectId(documentId);
                hlsCusActOnlineMeetingMember.setProjectDocumentCategory(documentCategory);
                List<HlsCusActOnlineMeetingMember> list = new ArrayList<>();
                list = hlsCusActOnlineMeetingMemberService.select(iRequest, hlsCusActOnlineMeetingMember, 1, 99999);
                if (list.size() > 0) {
                    meetingId = list.get(0).getMeetingId();
                }
                return new ResponseData(hlsCusActMeetingJudgeService.queryAllJudgeDetailByMeetingId(iRequest, meetingId));
            } else {
                return new ResponseData(hlsCusActMeetingJudges);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("上会评审意见查询失败,请重新尝试");
            return error;

        }
    }


    /**
     * 根据工作流实例Id查询上会评审信息(code转换中文)
     */
    @RequestMapping(value = "/wfl/act/meeting/judge/detail/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData judgeDetailQueryByProcessInstanceId(@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HlsCusActMeetingJudge hlsCusActMeetingJudge) {
        PageHelper.startPage(page, pageSize);
        try {
            IRequest iRequest = RequestHelper.getCurrentRequest();
            return new ResponseData(hlsCusActMeetingJudgeService.queryJudgeDetailByProcessInstanceId(iRequest, hlsCusActMeetingJudge.getProcessInstanceId()));
        } catch (Exception e) {
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会信息查询失败，请重新尝试");
            return error;
        }
    }

    /**
     * 根据工作流实例Id查询待满足条件(code转换中文)
     */
    @RequestMapping(value = "/wfl/act/meeting/judge/detail/part/agree/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData partAgreeDetailQueryByProcessInstanceId(@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HlsCusActMeetingJudge hlsCusActMeetingJudge) {
        PageHelper.startPage(page, pageSize);
        try {
            IRequest iRequest = RequestHelper.getCurrentRequest();
            return new ResponseData(hlsCusActMeetingJudgeService.queryPartAgreeDetailDetailByProcessInstanceId(iRequest, hlsCusActMeetingJudge.getProcessInstanceId()));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("项目上会信息查询失败，请重新尝试");
            return error;
        }
    }

    /**
     * 待满足条件的满足
     * @param hlsCusActMeetingJudgeList
     * @return
     */
    @RequestMapping(value = "/wfl/act/meeting/judge/detail/part/agree/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData toBeSatisfiedConditionSave(@RequestBody List<HlsCusActMeetingJudge> hlsCusActMeetingJudgeList) {
        try {
            IRequest iRequest = RequestHelper.getCurrentRequest();
            Long meetingId = hlsCusActMeetingService.queryMeetingIdByProcessInstanceId(iRequest, hlsCusActMeetingJudgeList.get(0).getProcessInstanceId());
            for (HlsCusActMeetingJudge hlsCusActMeetingJudge : hlsCusActMeetingJudgeList) {
                hlsCusActMeetingJudge.setJudgeSuggestion("PART_AGREE");// 待满足条件的审批意见一定是有条件同意
                hlsCusActMeetingJudge.setMeetingId(meetingId);// 设置meetingId
            }
            hlsCusActMeetingJudgeService.batchUpdate(iRequest, hlsCusActMeetingJudgeList);
            return new ResponseData(true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("待满足条件保存失败，请重新尝试");
            return error;
        }
    }

    /**
     * 线下会议评审信息保存
     */
    @RequestMapping(value = "/wfl/act/meeting/offline/judge/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData offlineMeetingJudgeSave(HttpServletRequest request, @RequestBody final List<HlsCusActMeetingJudge> hlsCusActMeetingJudgeList) {
        try {
            IRequest iRequest = createRequestContext(request);
            Iterator<HlsCusActMeetingJudge> hlsCusActMeetingJudgeIterator = hlsCusActMeetingJudgeList.iterator();
            while (hlsCusActMeetingJudgeIterator.hasNext()) {
                HlsCusActMeetingJudge hlsCusActMeetingJudge = (HlsCusActMeetingJudge) hlsCusActMeetingJudgeIterator.next();
                Long processInstanceId = hlsCusActMeetingJudge.getProcessInstanceId();// 取得当前的工作流实例Id
                Long meetingId = hlsCusActMeetingService.queryMeetingIdByProcessInstanceId(iRequest, processInstanceId);
                hlsCusActMeetingJudge.setMeetingId(meetingId);// 设置会议Id
                Long judgeId = hlsCusActMeetingJudgeService.queryJudgeIdByDetails(iRequest, hlsCusActMeetingJudge);// 查询对应的上会评审信息是否存在
                if (judgeId == null) {
                    hlsCusActMeetingJudgeService.insertSelective(iRequest, hlsCusActMeetingJudge);
                } else {
                    hlsCusActMeetingJudge.setJudgeId(judgeId);
                    hlsCusActMeetingJudgeService.updateByPrimaryKey(iRequest, hlsCusActMeetingJudge);
                }
            }
            return new ResponseData(true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("线下评审委员会审批信息提交失败,请重新尝试");
            return error;

        }
    }


}

