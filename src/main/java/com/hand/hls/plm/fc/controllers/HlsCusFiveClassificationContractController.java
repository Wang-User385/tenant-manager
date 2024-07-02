package com.hand.hls.plm.fc.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationContractService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusFiveClassificationContractController extends BaseController {

    @Autowired
    private HlsCusIFiveClassificationContractService service;


    @RequestMapping(value = "/plm/five/classification/contract/query")
    @ResponseBody
    public ResponseData query(HlsCusFiveClassificationContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/five/classification/contract/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusFiveClassificationContract> dto, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/five/classification/contract/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFiveClassificationContract> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * @Description:五级分类box中查询所有的起租合同
     * @Author: Wty
     * @Date: Created om 15:09 2018/5/17
     */
    @RequestMapping(value = "/plm/five/classification/all/incept/contract/select")
    @ResponseBody
    public ResponseData selectAllContracts(HttpServletRequest request,
                                           HlsCusFiveClassificationContract dto,
                                           @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectAllContracts(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:五级分类查询创建页面的起租合同
     * @Author: Wty
     * @Date: Created om 15:09 2018/5/17
     */
    @RequestMapping(value = "/plm/five/classification/contracts/select")
    @ResponseBody
    public ResponseData selectFcContracts(HttpServletRequest request,
                                          @RequestBody HlsCusFiveClassificationContract dto,
                                          @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectFcContracts(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:五级分类工作流查询指定人的合同
     * @Author: Wty
     * @Date: Created om 15:09 2018/5/17
     */
    @RequestMapping(value = "/plm/five/classification/contracts/activiti/select")
    @ResponseBody
    public ResponseData selectFcActivitiContracts(HttpServletRequest request,
                                                  @RequestBody HlsCusFiveClassificationContract dto,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectFcActivitiContracts(iRequest, dto, page, pageSize));
    }


    /**
     * @Description:五级分类五级分类首页chart查询
     * @Author: Wty
     * @Date: Created om 20:11 2018/5/22
     */
    @RequestMapping(value = "/plm/five/classification/contracts/home/chart/query")
    @ResponseBody
    public ResponseData homeChartQuery(HttpServletRequest request,
                                       HlsCusFiveClassificationContract dto) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.homeChartQuery(iRequest, dto));
    }

    /**
     * @Description:风险预警通过bpId查找所有保理租赁合同
     * @Author: Wty
     * @Date: Created om 19:36 2018/5/31
     */
    @RequestMapping(value = "/plm/rw/risk/warning/contracts/query")
    @ResponseBody
    public ResponseData selectRwContractsByBpId(HttpServletRequest request,
                                                @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFiveClassificationContract dto = param.toJavaObject(HlsCusFiveClassificationContract.class);
        return new ResponseData(service.selectRwContractsByBpId(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:租金催收首页逾期合同rollTable查询
     * @Author: Wty
     * @Date: Created om 13:34 2018/6/11
     */
    @RequestMapping(value = "/plm/rc/home/rollTable/overdue/contracts/info/query")
    @ResponseBody
    public ResponseData rcHomeRollTableQuery(HttpServletRequest request,
                                             HlsCusFiveClassificationContract dto,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.rcHomeRollTableQuery(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:租金催收明细逾期合同明细查询
     * @Author: Wty
     * @Date: Created om 13:35 2018/6/11
     */
    @RequestMapping(value = "/plm/rc/detail/overdue/contracts/info/query")
    @ResponseBody
    public ResponseData rcSelectOverdueContractsInfo(HttpServletRequest request,
                                                     HlsCusFiveClassificationContract dto,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.rcSelectOverdueContractsInfo(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:租金催收基本信息查询
     * @Author: Wty
     * @Date: Created om 16:33 2018/6/11
     */
    @RequestMapping(value = "/plm/rc/detail/contract/base/info/query")
    @ResponseBody
    public ResponseData rcSelectBaseInfo(HttpServletRequest request,
                                         HlsCusFiveClassificationContract dto,
                                         @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.rcSelectBaseInfo(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:租金催收首页firstChart查询
     * @Author: Wty
     * @Date: Created om 13:27 2018/6/12
     */
    @RequestMapping(value = "/plm/rc/home/first/chart/query")
    @ResponseBody
    public ResponseData rcHomeFirstChartQuery(HttpServletRequest request,
                                              HlsCusFiveClassificationContract dto,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.rcHomeFirstChartQuery(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:租金催收首页secondChart查询
     * @Author: Wty
     * @Date: Created om 14:20 2018/6/12
     */
    @RequestMapping(value = "/plm/rc/home/second/chart/query")
    @ResponseBody
    public ResponseData rcHomeSecondChartQuery(HttpServletRequest request,
                                               HlsCusFiveClassificationContract dto,
                                               @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.rcHomeSecondChartQuery(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:获取copy对象
     * @Author: Wty
     * @Date: Created om 14:20 2018/6/12
     */
    @RequestMapping(value = "/plm/fc/get/copy/contracts")
    @ResponseBody
    public ResponseData getCopyContracts(HttpServletRequest request,
                                         @RequestBody HlsCusFiveClassificationContract dto) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.getCopyContracts(iRequest, dto));
    }


    /**
     * @Description:工作流审批保存意见
     * @Author: Wty
     * @Date: Created om 下午5:38 2018/7/11
     * @param: [request, list, approvalNode] list保存的集合,approvalNode审批节点
     * @return: com.hand.hap.system.dto.ResponseData
     */
    @RequestMapping(value = "/plm/fc/opinions/contracts/save")
    @ResponseBody
    public ResponseData saveOpinions(HttpServletRequest request,
                                     @RequestBody List<HlsCusFiveClassificationContract> list,
                                     @RequestParam String approvalNode) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.saveOpinions(iRequest, list, approvalNode));
    }
    @RequestMapping(value = "/plm/five/classification/show")
    @ResponseBody
    public Boolean shouldButtonShow(HttpServletRequest request) {
        return service.shouldButtonShow(createRequestContext(request));
    }


}
