package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.HistoricProcessInstanceResponseExt;
import com.hand.hap.activiti.exception.WflSecurityException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import com.hand.hls.sys.service.SysUserService;
import leaf.bean.LeafRequestData;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.security.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Controller
public class HlsCusPrjProjectAttachmentController extends BaseController {

    @Autowired
    private HlsCusPrjProjectAttachmentService service;

    @Autowired
    private HlsCusPrjProjectService prjProjectService;

    @Autowired
    private HlsCusFctProjectAttachmentService fctProjectAttachmentService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(HlsCusPrjProjectAttachmentController.class);

    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;
    @RequestMapping(value = "/ct/prj/project/attachment/query/review/info")
    @ResponseBody
    public ResponseData prjProjectAttachmentDetailQuery2(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum,
                                                         @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectAttachment dto = param.toJavaObject(HlsCusPrjProjectAttachment.class);
        IRequest requestContext = createRequestContext(request);
        String[] fileTypeArr = dto.getProjectAttachmentCategory().split("-");
        dto.setFileTypeArr(fileTypeArr);
        List<HlsCusPrjProjectAttachment> list = service.prjProjectAttachmentDetailQuery(requestContext, dto, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/prj/project/attachment/query/info")
    @ResponseBody
    public ResponseData prjProjectAttachmentDetailQuery(HlsCusPrjProjectAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if (dto.getProjectId() == null || dto.getProjectId() == 0) {
            dto.setProjectId(0L);
        }
        String[] fileTypeArr = dto.getProjectAttachmentCategory().split("-");
        dto.setFileTypeArr(fileTypeArr);
        List<HlsCusPrjProjectAttachment> list = service.prjProjectAttachmentDetailQuery(requestContext, dto, page, pageSize);
    //租赁项目审批通知书
    //        HlsCusFctProjectAttachment fctProjectAttachment = new HlsCusFctProjectAttachment();
    //        fctProjectAttachment.setProjectId(dto.getProjectId());
    //        fctProjectAttachment.setBpCategory("PRJ_APPROVAL_NOTICE_REPORT");
    //        List<HlsCusFctProjectAttachment> listFct = fctProjectAttachmentService.select(requestContext, fctProjectAttachment, page, pageSize);
    //
    //        List<HlsCusPrjProjectAttachment> list2 = new ArrayList<>();
    //        SysFile file3 = new SysFile();
    //        String securityKey3 = TokenUtils.getSecurityKey(request.getSession());
    //        for (HlsCusFctProjectAttachment dt : listFct) {
    //            HlsCusPrjProjectAttachment dtt = new HlsCusPrjProjectAttachment();
    //            dtt.setProjectId(dt.getProjectId());
    //            dtt.setProjectAttachmentCategory(dt.getBpCategory());
    //            dtt.setDocumentName(dt.getDocumentName());
    //            dtt.setProjectAttachmentId(dt.getProjectAttachmentId());
    //            dtt.setDescription(dt.getDescription());
    //            dtt.setFileName(dt.getFileName());
    //            dtt.setFileId(dt.getFileId());
    //            dtt.setFilePath(dt.getFilePath());
    //            dtt.setFileSuffix(dt.getFileSuffix());
    //            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //            dtt.setUploadDate(sDateFormat.format(dt.getUploadDate()));
    //
    //            dtt.setUploadPerson(dt.getUploadPersonName());
    //            if (dtt.getFileId() == null || "".equals(dtt.getFileId())) {
    //            } else {
    //                file3.setFileId(Long.valueOf(dtt.getFileId()));
    //                dtt.set_token(TokenUtils.generateToken(securityKey3, file3));
    //            }
    //            list2.add(dtt);
    //        }

        SysFile file = new SysFile();
        String securityKey = TokenUtils.getSecurityKey(request.getSession());
        for (HlsCusPrjProjectAttachment f : list) {
            if (f.getFileId() == null || "".equals(f.getFileId())) {

            } else {
                file.setFileId(Long.valueOf(f.getFileId()));
                f.set_token(TokenUtils.generateToken(securityKey, file));
            }
        }
//        List<HlsCusPrjProjectAttachment> listAll = new ArrayList<>();
//        for (HlsCusPrjProjectAttachment f : list) {
//            listAll.add(f);
//        }
//        for (HlsCusPrjProjectAttachment f : list2) {
//            listAll.add(f);
//        }
        return new ResponseData(list);
    }



    /**
     * 项目评审附件查询
     *
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/bp/attachment/project/query")
    @ResponseBody
    public ResponseData projectListQuery(final HlsCusPrjProjectAttachment dto, final HttpServletRequest request) {
        try {
            if (dto.getProjectId() != null && dto.getProjectId() != 0) {
                IRequest requestContext = createRequestContext(request);
                List<HlsCusPrjProjectAttachment> dtoList = service.selectInProjectAttachmentService(requestContext, dto, 1, 1000000000);
                return new ResponseData(dtoList);
            } else {
                return new ResponseData();
            }
        } catch (Exception e) {
            logger.error("------------------------项目评审附件清单查询失败----------------------", e);
            ResponseData error = new ResponseData();
            error.setSuccess(false);
            return error;
        }
    }


    @RequestMapping(value = "/ct/con/project/attachment/query/info")
    @ResponseBody
    public ResponseData conProjectAttachmentDetailQuery(HlsCusPrjProjectAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if (dto.getProjectId() == null || dto.getProjectId() == 0) {
            dto.setProjectId(0L);
        }
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(dto.getProjectId());
        String[] fileTypeArr = dto.getProjectAttachmentCategory().split("-");
        dto.setFileTypeArr(fileTypeArr);
        List<HlsCusPrjProjectAttachment> list = service.prjProjectAttachmentDetailQuery(requestContext, dto, page, pageSize);
        return new ResponseData(list);
    }

    //@RequestBody List<HlsCusPrjProjectAttachment> list
    @RequestMapping(value = "/ct/prj/project/attachment/submit")
    @ResponseBody
    public ResponseData update( @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);

        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProjectAttachment> list= param.toJavaList(HlsCusPrjProjectAttachment.class);

        if (list.size() > 0) {
            service.batchUpdateAttachemnt(iRequest, list);
        }
        return new ResponseData(list);
    }

    @RequestMapping("/ct/prj/project/sign/content/{projectId}")
    @ResponseBody
    public ResponseData selectContractAttachmentInfo(HttpServletRequest request, @PathVariable String projectId)
            throws WflSecurityException {
        IRequest iRequest = createRequestContext(request);
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
        hlsCusPrjProjectAttachment.setProjectId(Long.parseLong(projectId));
        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.selectContractAttachmentInfo(hlsCusPrjProjectAttachment);
        return new ResponseData(hlsCusPrjProjectAttachments);
    }
    @RequestMapping("/ct/prj/project/sign/contentFileList")
    @ResponseBody
    public void selectContractAttachmentList(HttpServletRequest request, @RequestParam("projectId") String projectId, HttpServletResponse response)
            throws Exception {
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
        hlsCusPrjProjectAttachment.setProjectId(Long.parseLong(projectId));
        List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.selectContractAttachmentList(hlsCusPrjProjectAttachment);
        if (hlsCusPrjProjectAttachments.size() > 0) {
            try {
                String zipFilePath = "";
                String fileName = "";
                List<HlsCusSysFile> hlsCusSysFiles = new ArrayList<>(hlsCusPrjProjectAttachments.size());
                for (int i = 0; i < hlsCusPrjProjectAttachments.size(); i++) {
                    HlsCusSysFile hlsCusSysFile = new HlsCusSysFile();
                    String filePath = hlsCusPrjProjectAttachments.get(i).getFilePath();
                    String fileName1 = hlsCusPrjProjectAttachments.get(i).getFileName();
                    hlsCusSysFile.setFilePath(filePath);
                    hlsCusSysFile.setFileName(fileName1);
                    hlsCusSysFiles.add(hlsCusSysFile);
                }
                if (hlsCusSysFiles.size() > 0) {
                    File zipFilePath1 = new File(zipFilePath);
                    //拼接文件名,用户名+系统时间,避免出现重复
                    fileName = "downloadZip_" + System.currentTimeMillis();
                    //String zipFile = "attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso-8859-1") + ".zip";
                    String zipFile = zipFilePath1 + fileName + ".zip";

                    FileOutputStream outStream = new FileOutputStream(zipFile);
                    ZipOutputStream toClient = new ZipOutputStream(outStream);
                    //打包转换为zip文件
                    HlsCusZipUtil.zipFile(hlsCusSysFiles, toClient);
                    toClient.close();
                    outStream.close();
                    //下载zip文件
                    HlsCusZipUtil.downloadZip(new File(zipFile), response);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

}
