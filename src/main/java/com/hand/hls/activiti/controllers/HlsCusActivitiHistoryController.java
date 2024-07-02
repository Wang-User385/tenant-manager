package com.hand.hls.activiti.controllers;


import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.activiti.service.HlsCusActVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工作流历史记录查询Controller
 */
@Controller
public class HlsCusActivitiHistoryController extends BaseController {

    @Autowired
    private HlsCusActVarService hlsCusActVarService;

    /**
     * 查询流程ID
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wfl/history/process/queryId")
    @ResponseBody
    public ResponseData queryProcessInstanceId(HttpServletRequest request, @RequestParam("workFlowKey") String workFlowKey, @RequestParam("businessKey") String businessKey) {
        List<String> list = new ArrayList<>(1);
        list.add(hlsCusActVarService.selectWorkFlowProcessInstanceId(workFlowKey, businessKey));
        return new ResponseData(list);
    }

    /**
     * 查询processInstanceI和taskID
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wfl/history/process/task/queryId")
    @ResponseBody
    public ResponseData queryTaskId(HttpServletRequest request, @RequestParam("workFlowKey") String workFlowKey, @RequestParam("businessKey") String businessKey) {
        Map<String, Object> map = hlsCusActVarService.queryTaskId(workFlowKey, businessKey);
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        return new ResponseData(list);
    }
}
