package com.hand.hls.plm.controllers;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.mail.mapper.MessageTemplateMapper;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.dto.HlsCusPlmAttachment;
import com.hand.hls.plm.service.HlsCusPlmIAttachmentService;
import hls.core.sys.event.service.SysEventService;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

@Controller
public class HlsCusPlmAttachmentController extends BaseController {

    @Autowired
    private HlsCusPlmIAttachmentService service;

    @Autowired
    private SysEventService sysEventService;

    @RequestMapping(value = "/plm/attachment/query")
    @ResponseBody
    public ResponseData query(HlsCusPlmAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/attachment/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPlmAttachment> dto = param.toJavaList(HlsCusPlmAttachment.class);
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/attachment/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPlmAttachment> dto = param.toJavaList(HlsCusPlmAttachment.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    /**
     * @Description:附件查询
     * @Author: Wty
     * @Date: Created om 21:30 2018/5/21
     */
    @RequestMapping(value = "/plm/attachment/query/info")
    @ResponseBody
    public ResponseData plmAttachmentDetailQuery(HlsCusPlmAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPlmAttachment> list = service.plmAttachmentDetailQuery(requestContext, dto, page, pageSize);
        SysFile file = new SysFile();
        String securityKey = TokenUtils.getSecurityKey(request.getSession());
        for (HlsCusPlmAttachment f : list) {
            if (f.getFileId() == null || "".equals(f.getFileId())) {

            } else {
                file.setFileId(Long.valueOf(f.getFileId()));
                f.set_token(TokenUtils.generateToken(securityKey, file));
            }
        }
        return new ResponseData(list);
    }

    /**
     * @Description:附件查询2
     * @Author: Wty
     * @Date: Created om 21:30 2018/5/21
     */
    @RequestMapping(value = "/plm/attachment/document/name/query/info")
    @ResponseBody
    public ResponseData plmAttachmentDetailQuery2(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPlmAttachment dto = param.toJavaObject(HlsCusPlmAttachment.class);
        List<HlsCusPlmAttachment> list = service.plmAttachmentDetailQuery2(requestContext, dto, page, pageSize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/plm/attachment/document/file/query")
    @ResponseBody
    public ResponseData bpAttachmentFileQuery(final HlsCusPlmAttachment plmMasterAttachment, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPlmAttachment> plmMasterAttachmentList = service.queryAllFile(requestContext, plmMasterAttachment, page, pagesize);
        String securityKey = TokenUtils.getSecurityKey(request.getSession());
        SysFile file = new SysFile();
        for (HlsCusPlmAttachment f : plmMasterAttachmentList) {
            file.setFileId(f.getFileId());
            f.set_token(TokenUtils.generateToken(securityKey, file));
        }
        return new ResponseData(plmMasterAttachmentList);
    }

    /**
     * @Description:五级分类附件变更删除原来的数据变更changeDelete
     * @Author: Wty
     * @Date: Created om 21:30 2018/5/21
     */
    @RequestMapping(value = "/plm/change/attachment/remove")
    @ResponseBody
    public ResponseData plmAttachmentChangeOldRemove(@RequestBody List<HlsCusPlmAttachment> dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        if (CollectionUtils.isNotEmpty(dto)) {
            for (int i = 0; i < dto.size(); i++) {
                service.plmAttachmentChangeOldRemove(iRequest, dto.get(i));
            }
        }
        return new ResponseData();
    }

    /**
     * @Description:just a test to test send notice
     * @Author: Wty
     * @Date: Created om 14:19 2018/6/15
     * @param: [request]
     * @return: com.hand.hap.system.dto.ResponseData
     */

    //@Autowired
    //private HlsSystemNoticeService hlsSystemNoticeService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IMessageService messageService;

    //@Autowired
    //private IMailingListService iMailingListService;

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;

    //@Autowired
   // private IMailingListService mailingListService;

    @RequestMapping(value = "/plm/change/attachment/my/test")
    @ResponseBody
    public ResponseData testNotce(HttpServletRequest request) throws Exception {

        //1。发送指定邮件根据模版和值匹配
        IRequest iRequest = createRequestContext(request);
  /*
        MailingList mailingList = new MailingList();
        mailingList.setReceivers("1315514009@qq.com");
        //自定义邮件发送内容,mode为custom
        mailingList.setMode("custom");
        mailingList.setTemplateCode("CT_INV_CLOSE_MAIL");
        //邮件状态U为待发送
        mailingList.setSentFlag("U");
        mailingList.setCreationDate(new Date());
        mailingList.set__status("add");

        HlsCusFinancePurchase financePurchase = new HlsCusFinancePurchase();
        financePurchase.setFinancialProductName("理财产品名称");
        financePurchase.setIssuingAgency("吴天宇机构");
        financePurchase.setExpectedDueDate(new Date());
        financePurchase.setInvestmentAmount(1234567.24);
        mailingListService.sendCustomMailByTemplate(iRequest,mailingList,financePurchase);
*/

        // 2。查找对应的待发送清单，发送邮件
       /* List<MailingList> mailingListArrayList = iMailingListService.selectSendMailList();
        if (CollectionUtils.isNotEmpty(mailingListArrayList)) {
            for (MailingList m : mailingListArrayList) {
                m.set__status("update");
                mailingListService.sendCustomMailByTemplate(iRequest, m, null);
            }
        }*/
        /*
        3。直接发送list
         List<MailingList> mailingListArrayList = new ArrayList();
         mailingListArrayList.add(mailingList);
        if (CollectionUtils.isNotEmpty(mailingListArrayList)) {
            for (MailingList m : mailingListArrayList) {
                mailingListService.sendCustomMailByTemplate(iRequest, m, null);
            }
        }
         */

        return new ResponseData();

    }

    @RequestMapping(value = "/plm/attachment/createPrintText")
    @ResponseBody
    public ResponseData createPrintText(@RequestBody HlsCusPlmAttachment dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        String securityKey = TokenUtils.getSecurityKey(request.getSession());
        /*HlsCusSysFile file = new HlsCusSysFile();
        List<HlsCusSysFile> list = service.createPrintText(iRequest, dto);
        for (HlsCusSysFile f : list) {
            file.setFileId(f.getFileId());
            file.set_token(TokenUtils.generateToken(securityKey, file));
            file.setFilePath(f.getFilePath());
            file.setFileName(f.getFileName());
        }
        List<HlsCusSysFile> files = new ArrayList<>();
        files.add(file);
        return new ResponseData(files);*/
        return null;
    }

    @RequestMapping(value = "/plm/attachment/createPrintTexts")
    @ResponseBody
    public ResponseData createPrintTexts(@RequestBody List<HlsCusPlmAttachment> dto, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData(false);
        LinkedList<Object> rows = new LinkedList<>();
        /*List<HlsCusSysFile> hlsCusSysFiles = new ArrayList<>();
        for (HlsCusPlmAttachment hlsCusPlmAttachment : dto) {
            rows.addAll(createPrintText(hlsCusPlmAttachment, request).getRows());
        }
        //打包下载
        for (Object object : rows) {
            HlsCusSysFile sysfile = (HlsCusSysFile) object;
            //sysfile.setFileId(((HlsCusSysFile) object));
            hlsCusSysFiles.add(sysfile);
        }
        if (hlsCusSysFiles.size() > 0) {
            try {
                // service.downPLmZip(hlsCusSysFiles, requestCtx, request, response);
                responseData.setSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
                responseData.setSuccess(false);
                responseData.setMessage(e.getMessage());
            }
        }*/
        return responseData;
    }


    @RequestMapping(value = "/plm/attachment/attachment/downloadZipFile")
    public ResponseData downloadConContentZipFile(@RequestParam("postloanInspectionIds") String postloanInspectionIds, @RequestParam("printType") String printType, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData(false);
        try {
            service.downPLmZip(postloanInspectionIds, printType, requestCtx, request, response);
            responseData.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }
}
