//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.prj.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractChangeReq;
import com.hand.hls.cont.service.IConContractChangeReqService;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.IPrjProjectService;
import com.hand.hls.prj.service.PrjTaskService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PrjTaskController extends BaseController {
    @Autowired
    private PrjTaskService prjTaskService;
    @Autowired
    private IPrjProjectService prjProjectService;
    @Autowired
    private IConContractService conContractService;
    @Autowired
    private IConContractChangeReqService conContractChangeReqService;

    public PrjTaskController() {
    }

    @RequestMapping({"/query/prj/task"})
    @ResponseBody
    public ResponseData queryPrjTask(HlsCusConContract dto, HttpServletRequest request) {
        String conctractId = null;
        String projectId = null;
        if (dto.getContractId() != null) {
            conctractId = dto.getContractId().toString();
        }

        if (dto.getProjectId() != null) {
            projectId = dto.getProjectId().toString();
        }

        List<Map> listMap = this.prjTaskService.queryPrjTask(conctractId, projectId);
        return new ResponseData(listMap);
    }

    @RequestMapping({"/query/con/task"})
    @ResponseBody
    public ResponseData queryConTask(HlsCusConContract dto, HttpServletRequest request) {
        String conctractId = null;
        if (dto.getContractId() != null) {
            conctractId = dto.getContractId().toString();
        }

        List<Map> listMap = this.prjTaskService.queryConTask(conctractId);
        return new ResponseData(listMap);
    }

    @RequestMapping({"/query/all/prj/con"})
    @ResponseBody
    public ResponseData queryAllPrjCon(HlsCusConContract dto, HttpServletRequest request) {
        String conctractId = null;
        String projectId = null;
        if (dto.getContractId() != null) {
            conctractId = dto.getContractId().toString();
        }

        if (dto.getProjectId() != null) {
            projectId = dto.getProjectId().toString();
        }

        Map<String, List<Map>> listMap = this.prjTaskService.queryAllPrjConById(conctractId, projectId);
        List<Map> list = new ArrayList();
        list.add(listMap);
        return new ResponseData(list);
    }

    @RequestMapping({"/query/notice/by/inceptId"})
    @ResponseBody
    public ResponseData querInceptNotice(String contractInceptId, HttpServletRequest request) {
        return new ResponseData(this.prjTaskService.queryInceptById(contractInceptId));
    }

    @RequestMapping({"/query/signnotice/by/contractId"})
    @ResponseBody
    public ResponseData querSignNotice(String contractId, HttpServletRequest request) {
        return new ResponseData(this.prjTaskService.querySignNoticeById(contractId));
    }

    @RequestMapping({"/query/task/info/getprocInstIdByPrjId"})
    @ResponseBody
    public ResponseData getprocInstIdByPrjId(@RequestBody(required = false) HlsCusPrjProject dto, HttpServletRequest request) {
        List<Map> listMap = this.prjTaskService.getprocInstIdByPrjId(dto.getProjectId());
        return new ResponseData(listMap);
    }

    @RequestMapping({"/query/task/info/by/{processInstanceId}"})
    @ResponseBody
    public ResponseData queryTask(@PathVariable String processInstanceId, HttpServletRequest request) {
        List<Map> listMap = this.prjTaskService.queryTaskInfo(processInstanceId);
        return new ResponseData(listMap);
    }

    @RequestMapping({"/query/con/pay/info"})
    @ResponseBody
    public ResponseData queryConPayInfo(HlsCusConContract dto, HttpServletRequest request) {
        String conctractId = null;
        String projectId = null;
        if (dto.getContractId() != null) {
            conctractId = dto.getContractId().toString();
        }

        if (dto.getProjectId() != null) {
            projectId = dto.getProjectId().toString();
        }

        List<Map> listMap = this.prjTaskService.queryConPayInfo(conctractId, projectId);
        return new ResponseData(listMap);
    }

    @RequestMapping({"/query/prj/by/info/{cpId}/{type}"})
    @ResponseBody
    public ResponseData queryPrj(@PathVariable String cpId, @PathVariable String type, HttpServletRequest request) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        List<Object> list = new ArrayList();
        if (type.equals("CON_CONTRACT")) {
            HlsCusConContract conContract = new HlsCusConContract();
            conContract.setContractId(Long.parseLong(cpId));
            conContract = this.conContractService.selectByPrimaryKey(iRequest,conContract);
            list.add(conContract);
        }

        if (type.equals("PRJ_PROJECT")) {
            HlsCusPrjProject prjProject = new HlsCusPrjProject();
            prjProject.setProjectId(Long.parseLong(cpId));
            prjProject = (HlsCusPrjProject)this.prjProjectService.selectByPrimaryKey(iRequest, prjProject);
            list.add(prjProject);
        }

        if (type.equals("CONTRACT_CHANGE")) {
            HlsCusConContractChangeReq conChange = new HlsCusConContractChangeReq();
            conChange.setChangeReqId(Long.parseLong(cpId));
            conChange = (HlsCusConContractChangeReq)this.conContractChangeReqService.selectByPrimaryKey(iRequest, conChange);
            list.add(conChange);
        }

        return new ResponseData(list);
    }
}
