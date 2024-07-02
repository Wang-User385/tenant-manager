package com.hand.hls.activiti.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.activiti.dto.HlsCusActMeetingRiskList;
import com.hand.hls.activiti.service.HlsCusActMeetingRiskListService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 上会风险防范措施Controller
 * @Date: Created in 16:55 2018/1/9
 * @Modified By:
 */
@Controller
public class HlsCusActMeetingRiskListController extends BaseController {

    @Autowired
    private HlsCusActMeetingRiskListService hlsCusActMeetingRiskListService;

    /**
     * 上会阶段风险防范措施查询
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/wfl/act/meeting/risk/list/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData meetingRiskListQuery(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
        PageHelper.startPage(page, pagesize);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusActMeetingRiskList riskList = param.toJavaObject(HlsCusActMeetingRiskList.class);

        try {
            IRequest iRequest = createRequestContext(request);
            return new ResponseData(hlsCusActMeetingRiskListService.queryRiskListByProcessInstanceId(iRequest, riskList.getProcessInstanceId()));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("风险防范措施加载失败,请重新尝试");
            return error;

        }
    }


    /**
     * 上会阶段风险防范措施保存
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/wfl/act/meeting/risk/list/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData meetingRiskListSave(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        try {
            IRequest iRequest = createRequestContext(request);
            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCusActMeetingRiskList> arrayList = param.toJavaList(HlsCusActMeetingRiskList.class);

            hlsCusActMeetingRiskListService.batchUpdate(iRequest, arrayList);

            return new ResponseData(true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("风险防范措施保存失败,请重新尝试");
            return error;

        }
    }

    /**
     * 已指定风险防范措施查询
     *
     * @param request
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/wfl/act/meeting/risk/list/select/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData selectMeetingRiskListQuery(HttpServletRequest request, Long processInstanceId) {
        try {
            IRequest iRequest = createRequestContext(request);
            return new ResponseData(hlsCusActMeetingRiskListService.queryRiskListSelectedByProcessInstanceId(iRequest, processInstanceId));
        } catch (Exception e) {
            e.printStackTrace();
            ResponseData error = new ResponseData(false);
            error.setMessage("风险防范措施加载失败,请重新尝试");
            return error;

        }
    }

    @RequestMapping(value = "/act/meeting/risk/list/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, Long riskId) {
        IRequest iRequest = createRequestContext(request);
        HlsCusActMeetingRiskList riskList = new HlsCusActMeetingRiskList();
        riskList.setMeetingRiskListId(riskId);

        riskList.setRiskStatus("DELETED");

        hlsCusActMeetingRiskListService.updateByPrimaryKeySelective(iRequest,riskList);
        return new ResponseData();
    }
}
