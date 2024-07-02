package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusContractAttachment;
import com.hand.hls.cont.service.HlsCusContractAttachmentService;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusContractAttachmentController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(HlsCusContractAttachmentController.class);
    @Autowired
    private HlsCusContractAttachmentService service;

    @RequestMapping(value = "/con/contract/attachment/query")
    @ResponseBody
    public ResponseData query(HlsCusContractAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/con/contract/attachment/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusContractAttachment> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/con/contract/attachment/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusContractAttachment> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 付款附件
     *
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/con/attachment/file/list/query")
    @ResponseBody
    public ResponseData contractAttachmentDocumentListQuery(final HlsCusContractAttachment dto, final HttpServletRequest request) {
        try {
            IRequest requestContext = createRequestContext(request);
            List<HlsCusContractAttachment> dtoList = service.selectCshDocByContractIdAndCategory(requestContext, dto);
            String securityKey = TokenUtils.getSecurityKey(request.getSession());
            SysFile file = new SysFile();
            for (HlsCusContractAttachment f : dtoList) {
                file.setFileId(Long.parseLong(f.getFileId()));
                f.set_token(TokenUtils.generateToken(securityKey, file));
            }
            return new ResponseData(dtoList);
        } catch (Exception e) {
            ResponseData error = new ResponseData();
            error.setSuccess(false);
            return error;
        }
    }

    /**
     * 付款附件
     *
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/con/attachment/file/detail/query")
    @ResponseBody
    public ResponseData selectCshDocByContractIdAndCategory(final HlsCusContractAttachment dto, final HttpServletRequest request) {
        try {
            IRequest requestContext = createRequestContext(request);
            List<HlsCusContractAttachment> dtoList = service.queryContractAttachment(requestContext, dto);
            String securityKey = TokenUtils.getSecurityKey(request.getSession());
            SysFile file = new SysFile();
            for (HlsCusContractAttachment f : dtoList) {
                file.setFileId(Long.parseLong(f.getFileId()));
                f.set_token(TokenUtils.generateToken(securityKey, file));
            }
            return new ResponseData(dtoList);
        } catch (Exception e) {
            ResponseData error = new ResponseData();
            error.setSuccess(false);
            return error;
        }
    }

    /**
     * 生成应收账款回款通知书
     * 起租通知书
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/cus/con/contract/createDocumentFile")
    @ResponseBody
    public ResponseData createDocumentFile(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException, FileReadIOException {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        ResponseData responseData = new ResponseData();
        JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
        try {
            service.createContractDocumentFile(iRequest, hlsCusConContract);
        } catch (Exception e) {
            logger.error("生成起租通知书失败", e);
            responseData.setSuccess(false);
        }

        return responseData;
    }


    @RequestMapping(value = "/con/contract/attachment/queryFine")
    @ResponseBody
    public ResponseData queryFineAttachemnt(HlsCusContractAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectContractFineAttachment(requestContext, dto, page, pageSize));
    }

    /**
     * 付款附件
     *
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/con/lend/attachment/file/list/query")
    @ResponseBody
    public ResponseData contractAttachmentLendDocumentListQuery(final HlsCusContractAttachment dto, final HttpServletRequest request) {
        try {
            IRequest requestContext = createRequestContext(request);
            List<HlsCusContractAttachment> dtoList = service.contractAttachmentLendDocumentListQuery(requestContext, dto);
            String securityKey = TokenUtils.getSecurityKey(request.getSession());
            SysFile file = new SysFile();
            for (HlsCusContractAttachment f : dtoList) {
                file.setFileId(Long.parseLong(f.getFileId()));
                f.set_token(TokenUtils.generateToken(securityKey, file));
            }
            return new ResponseData(dtoList);
        } catch (Exception e) {
            ResponseData error = new ResponseData();
            error.setSuccess(false);
            return error;
        }
    }

    /**
     * 合同结束附件
     *
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/con/end/attachment/file/list/query")
    @ResponseBody
    public ResponseData selectContractEndFileByContractId(final HlsCusContractAttachment dto, final HttpServletRequest request) {
        try {
            IRequest requestContext = createRequestContext(request);
            List<HlsCusContractAttachment> dtoList = service.selectContractEndFileByContractId(requestContext, dto);
            String securityKey = TokenUtils.getSecurityKey(request.getSession());
            SysFile file = new SysFile();
            for (HlsCusContractAttachment f : dtoList) {
                file.setFileId(Long.parseLong(f.getFileId()));
                f.set_token(TokenUtils.generateToken(securityKey, file));
            }
            return new ResponseData(dtoList);
        } catch (Exception e) {
            ResponseData error = new ResponseData();
            error.setSuccess(false);
            return error;
        }
    }

//    /**
//     * 生成应收账款回款通知书/所有权转移证书
//     * 起租通知书
//     *
//     * @param hlsCusConContract
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/hls/cus/con/contract/end/createDocumentFile")
//    @ResponseBody
//    public ResponseData createContractDocumentFile(HlsCusConContract hlsCusConContract, HttpServletRequest request) throws HlsCusException, FileReadIOException {
//        IRequest iRequest = RequestHelper.getCurrentRequest();
//        ResponseData responseData = new ResponseData();
//        try {
//            String templetCode[] = {"EBFIL_TRANSFER_PROOF_GD", "EBFIL_PRJ_TRANSFER_GD"};
//            for (int i = 0; i < templetCode.length; i++) {
//                hlsCusConContract.setDocumentType("PRJ_TRANSFER_GD");
//                hlsCusConContract.setTempletCode(templetCode[i]);
//                if ("EBFIL_TRANSFER_PROOF_GD".equals(templetCode[i])) {
//                    hlsCusConContract.setDocDocumentName("所有权转移回执");
//                    hlsCusConContract.setDocumentType("PRJ_TRANSFER_PROOF_GD");
//                } else {
//                    hlsCusConContract.setDocDocumentName("所有权转移证书");
//                    hlsCusConContract.setDocumentType("PRJ_TRANSFER_GD");
//                }
//                service.createContractDocumentFile(iRequest, hlsCusConContract);
//            }
//            responseData.setSuccess(true);
//        } catch (Exception e) {
//            responseData.setSuccess(false);
//            responseData.setMessage(e.getMessage());
//        }
//
//        return responseData;
//    }


}