package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.controllers.FndAttachmentController;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.common.components.HlsWordToPdfComponent;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.fct.service.HlsCusPrjContractDocxService;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
public class HlsCusFctProjectAttachmentController extends BaseController {


    @Autowired
    private HlsCusFctProjectAttachmentService service;
    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    @Autowired
    private HlsCusPrjContractDocxService hlsCusPrjContractDocxService;
    @Autowired
    private HlsCusPrjProjectAttachmentService prjProjectAttachmentService;
    @Autowired
    private HlsWordToPdfComponent hlsWordToPdfComponent;

    private static final String PRJ_REPORT_CONTENT = "PRJ_REPORT_CONTENT";
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @RequestMapping(value = "/ct/fct/project/attachment/content/list/query")
    @ResponseBody
    public ResponseData contentQueryList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFctProjectAttachment dto = param.toJavaObject(HlsCusFctProjectAttachment.class);
        List<HlsCusFctProjectAttachment> list=service.queryContentFileInfo(requestCtx,dto, page, pageSize ,1);
        //集合排序-正序
        Collections.sort(list, (o1, o2) -> {
            int i = o1.getProjectAttachmentId().intValue() - o2.getProjectAttachmentId().intValue();
            return i;
        });
        return new ResponseData(list);
    }


    /**
     * 租赁-生成合同文本
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/con/project/attachment/content/create")
    @ResponseBody
    public ResponseData prjConContentSave(HttpServletRequest request, Long projectId) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        service.prjConContentSave(requestCtx, projectId);
        return new ResponseData();
    }


    @RequestMapping(value = "/ct/fct/project/attachment/content/info/query")
    @ResponseBody
    public ResponseData contentQueryDetailInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFctProjectAttachment dto = param.toJavaObject(HlsCusFctProjectAttachment.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusFctProjectAttachment> list=service.queryContentFileInfo(requestCtx,dto, page, pageSize,2);
        if(CollectionUtils.isNotEmpty(list)){
            //集合排序-正序
            Collections.sort(list, (o1, o2) -> {
                if("PRJ_CON_BACKLEASE".equalsIgnoreCase(o2.getBpCategory())||"FCT_A_CON_MAIN_NO".equalsIgnoreCase(o2.getBpCategory())||"FCT_F_CONTRACT".equalsIgnoreCase(o2.getBpCategory())){
                    return 1;
                }
                if("FCT_CONTRACT_CONTENT_ATT".equalsIgnoreCase(o2.getBpCategory())||"PRJ_CONTRACT_CONTENT_ATT".equalsIgnoreCase(o2.getBpCategory())){
                    return -1;
                }
                return -1;
            });
        }
        return new ResponseData(list);
    }


    @RequestMapping(value = "/prj/attachment/upload")
    public Object uploadAttachment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        String pkValue = multiRequest.getParameter("pkvalue");
        IRequest requestCtx = createRequestContext(request);
        HlsCusFctProjectAttachment t = new HlsCusFctProjectAttachment();
        t.setProjectAttachmentId(Long.parseLong(pkValue));
        t.setStandardContract("N");
        service.updateByPrimaryKeySelective(requestCtx, t);
        return fndAttachmentController.uploadAttachment(request, response);
    }

    /**
     * 尽调报告生成
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/project/attachment/content/create")
    @ResponseBody
    public ResponseData prjContentSave(HttpServletRequest request, Long projectId,String templateCode) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        List<FndAttachmentMulti> fndAttachmentMultiList = service.prjContentSave(requestCtx, projectId,templateCode);
        if(!PRJ_REPORT_CONTENT.equals(templateCode)) {
            hlsWordToPdfComponent.wordToPdfAttachmentMuti(requestCtx, fndAttachmentMultiList);
        }
        return new ResponseData(fndAttachmentMultiList);
    }

    /**
     * 项目审批通知书生成
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/project/approval/notice/create")
    @ResponseBody
    public ResponseData prjApprovalNotice(HttpServletRequest request, Long projectId,String templateCode,Long prjNoticeId,Long quotationId,Long oldProjectId) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        List<FndAttachmentMulti> fndAttachmentMultiList = service.prjApprovalNoticeSave(requestCtx, projectId,templateCode,prjNoticeId,quotationId,oldProjectId);
        hlsWordToPdfComponent.wordToPdfAttachmentMuti(requestCtx,fndAttachmentMultiList);
        return new ResponseData(fndAttachmentMultiList);
    }


    @Autowired
    private FndAttachmentController fndAttachmentController;

    @RequestMapping(value = "/ct/fct/project/attachment/contentFinalized")
    @ResponseBody
    public ResponseData contentFinalized(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFctProjectAttachment dto = param.toJavaObject(HlsCusFctProjectAttachment.class);
        IRequest requestCtx = createRequestContext(request);
        service.contentFinalized(requestCtx,dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/prj/con/check/contract/number")
    @ResponseBody
    public ResponseData prjConCheckContractNumber(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFctProjectAttachment> projectList = param.toJavaList(HlsCusFctProjectAttachment.class);

        if (projectList.size() != 0) {
            for(HlsCusFctProjectAttachment dt : projectList){
                if("LON_CONTENT".equals(dt.getSourceType())){
                    HlsCusFctProjectAttachment fctProjectAttachment = new HlsCusFctProjectAttachment();
                    fctProjectAttachment.setDocumentNumber(dt.getDocumentNumber());
                    List<HlsCusFctProjectAttachment> resultList = service.selectSelective(requestContext, fctProjectAttachment);
                    if (resultList.size() > 0) {
                        throw new HlsCusException("融资租赁合同编号重复！");
                    }
                }
            }
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/vir/prj/attachment/delete")
    @ResponseBody
    public ResponseData deleteFctAttachment(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {

        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFctProjectAttachment  hlsCusFctProjectAttachment= param.toJavaObject(HlsCusFctProjectAttachment.class);
        List<HlsCusFctProjectAttachment> atmLists = service.selectSelective(requestCtx, hlsCusFctProjectAttachment);
        atmLists.forEach(item -> {
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("");
            fndAttachmentMulti.setTablePkValue(item.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(requestCtx, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourceTypeCode = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue("FCT_PROJECT_ATTACHMENT", item.getProjectAttachmentId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue("fnd_atm_attachment_multi", sourceTypeCode);
            }
            service.deleteByPrimaryKey(item);
        });

        return new ResponseData();
    }



    @RequestMapping(value = "/vir/prj/attachment/update")
    @ResponseBody
    public void updateVIrtualAttachment(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFctProjectAttachment hlscusfctprojectattachment = param.toJavaObject(HlsCusFctProjectAttachment.class);
        service.updateByPrimaryKeySelective(iRequest,hlscusfctprojectattachment);

    }
}