package com.hand.hls.abs.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsPkg;
import com.hand.hls.abs.dto.HlsCusAbsProject;
import com.hand.hls.abs.service.HlsCusAbsProjectService;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ferry
 * @date 2018-12-03
 * @description ABS 立项控制层
 */

@Controller
public class HlsCusAbsProjectController extends BaseController {

    @Autowired
    private HlsCusAbsProjectService service;


    @RequestMapping(value = "/ct/abs/project/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsProject dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/abs/project/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusAbsProject> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/abs/project/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProject> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    // @RequestBody HlsCusAbsPkg hlsCusAbsPkg,
    /*ABS立项保存*/
    @RequestMapping(value = "/ct/abs/project/detail/save")
    @ResponseBody
    public HlsCusAbsPkg update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {

        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAbsPkg hlsCusAbsPkg = param.toJavaObject(HlsCusAbsPkg.class);

        HlsCusAbsProject absProject = hlsCusAbsPkg.getHlsCusAbsProject();
        if (absProject != null) {
            try {
                service.absProjectSave(requestCtx, hlsCusAbsPkg);
                hlsCusAbsPkg.setSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
                hlsCusAbsPkg.setSuccess(false);
                hlsCusAbsPkg.setMessage(e.getMessage());
            }
        }
        return hlsCusAbsPkg;
    }

    /*ABS资产转让保存*/
    @RequestMapping(value = "/ct/abs/asset/transfer/save")
    @ResponseBody
    public HlsCusAbsPkg assetTransferSave(@RequestBody HlsCusAbsPkg hlsCusAbsPkg, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        HlsCusAbsProject absProject = hlsCusAbsPkg.getHlsCusAbsProject();
        if (absProject == null) {
            hlsCusAbsPkg.setSuccess(false);
            hlsCusAbsPkg.setMessage("ABS立项信息为空,保存失败");
        }else{
            try {
                service.assetTransferSave(requestCtx, hlsCusAbsPkg);
                hlsCusAbsPkg.setSuccess(true);
            } catch (Exception e) {
                hlsCusAbsPkg.setSuccess(false);
                hlsCusAbsPkg.setMessage(e.getMessage());
            }
        }
        return hlsCusAbsPkg;
    }

    //,HlsCusAbsProject dto
    /*ABS立项基本信息查询*/
    @RequestMapping(value = "/ct/abs/project/detail/query")
    @ResponseBody
    public ResponseData detailQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusAbsProject dto = param.toJavaObject(HlsCusAbsProject.class);

        return new ResponseData(service.queryProjectDetail(requestContext, dto));
    }

    /*ABS立项首页PIE图查询*/
    @RequestMapping({"/ct/abs/project/pie/chart/query"})
    @ResponseBody
    public List<HlsCusAbsProject> absPieChartQuery(HlsCusAbsProject dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusAbsProject> list = service.absPieChartQuery(requestContext, dto);
        return list;
    }

    // @RequestBody HlsCusAbsPkg hlsCusAbsPkg,
    /*ABS立项提交审批*/
    @RequestMapping(value = "/ct/abs/project/submit/wfl")
    @ResponseBody
    public ResponseData submitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAbsProject hlsCusAbsProject = param.toJavaObject(HlsCusAbsProject.class);
        return new ResponseData(service.absProjectSubmit(requestCtx,hlsCusAbsProject));


    }

    /*ABS立项提交审批*/
    @RequestMapping(value = "/ct/abs/project/approval/submit")
    @ResponseBody
    public void submitWflApproval(HlsCusAbsProject hlsCusAbsProject, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        service.submitWflApproval(requestCtx, hlsCusAbsProject);
    }



    /*ABS资产转让提交审批*/
   /* @RequestMapping(value = "/ct/abs/asset/transfer/submit/wfl")
    @ResponseBody
    public HlsCusAbsPkg transferSubmitWfl(@RequestBody HlsCusAbsPkg hlsCusAbsPkg, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        HlsCusAbsProject absProject = hlsCusAbsPkg.getHlsCusAbsProject();
        if (absProject != null) {
            try {
                service.absTransferSubmit(requestCtx, hlsCusAbsPkg);
                hlsCusAbsPkg.setSuccess(true);
            } catch (Exception e) {
                hlsCusAbsPkg.setMessage(e.getMessage());
                hlsCusAbsPkg.setSuccess(false);
            }
        }
        return hlsCusAbsPkg;
    }*/



    @RequestMapping(value = "/ct/abs/project/packQuery")
    @ResponseBody
    public ResponseData packQuery(HlsCusAbsProject dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectProjectPackageData(requestContext, dto, page, pageSize));
    }


    /**
     * 作废
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/project/cancel")
    @ResponseBody
    public ResponseData cancelProject(HlsCusAbsProject dto, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        service.cancelAbsProject(requestContext,dto);
        return new ResponseData();
    }
}

