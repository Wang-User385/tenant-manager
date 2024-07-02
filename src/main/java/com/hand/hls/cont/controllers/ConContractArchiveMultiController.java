package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.ServiceExecutionAdvice;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.cont.dto.ContractArchiveMulti;
import com.hand.hls.cont.dto.HlsCusConContractArchive;
import com.hand.hls.cont.service.IConContractArchiveService;
import com.hand.hls.cont.service.IContractArchiveMultiService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import leaf.bean.LeafRequestData;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class ConContractArchiveMultiController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(ServiceExecutionAdvice.class);

    @Autowired
    private IContractArchiveMultiService archiveMultiService;

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private IConContractArchiveService archiveService;

    @RequestMapping(value = "/contract/archive/attachment/select", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData selectFile(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) throws Exception {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long contractId = param.getLong("contract_id");
        String archiveType = param.get("archive_type").toString();
        return new ResponseData(archiveMultiService.selectArchiveAttachment(contractId, archiveType, iRequest, pagenum, pagesize));
    }

    @RequestMapping(value = "/contract/archive/attachment/save", method = RequestMethod.POST)
    @ResponseBody
    public void saveFile(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        JSONArray jsonArray = JSON.parseArray(requestData.get("parameter").toString());
        List<ContractArchiveMulti> list = jsonArray.toJavaList(ContractArchiveMulti.class);
        archiveMultiService.saveArchiveAttachment(list, iRequest);
    }

    @RequestMapping(value = "/contract/archive/attachment/selectAll", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData selectAllFile(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) throws Exception {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long contractId = param.getLong("contract_id");
        String status = param.getString("status");
        return new ResponseData(archiveMultiService.selectAllArchiveAttachment(contractId, status, iRequest, pagenum, pagesize));
    }

    @RequestMapping(value = "/contract/archive/attachment/selectAttachIds", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData selectAttachIds(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ContractArchiveMulti> list = param.toJavaList(ContractArchiveMulti.class);
        return new ResponseData(archiveMultiService.selectAttachmentId(list));
    }

    @RequestMapping({"/contract/archive/attachment/downloadFile"})
    @ResponseBody
    public void downloadAttachmentZip(HttpServletRequest request, @RequestParam("attachment_id") String attachmentIds, HttpServletResponse response) throws Exception {
        IRequest requestContext = this.createRequestContext(request);
        String[] attachmentIdList = attachmentIds.split(",");
        if (attachmentIdList != null) {
            try {
                String zipFilePath = "";
                String fileName = "";
                List<HlsCusSysFile> hlsCusSysFiles = new ArrayList<>(attachmentIdList.length);
                if (attachmentIdList != null) {
                    for (int i = 0; i < attachmentIdList.length; i++) {
                        FndAttachment fndAttachment = new FndAttachment();
                        fndAttachment.setAttachmentId(Long.valueOf(attachmentIdList[i]));
                        FndAttachment attachment = (FndAttachment) fndAttachmentService.selectByPrimaryKey(requestContext, fndAttachment);
                        fileName = attachment.getFileName();
                        String filePath = attachment.getFilePath();
                        HlsCusSysFile hlsCusSysFile = new HlsCusSysFile();
                        hlsCusSysFile.setFilePath(filePath);
                        hlsCusSysFile.setFileName(fileName);
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
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @RequestMapping({"/attachment/zip/downloadFile"})
    @ResponseBody
    public void downloadJinJianAttachmentZip(HttpServletRequest request, @RequestParam("attachment_id") String attachmentIds, HttpServletResponse response) {
        try {
            IRequest requestContext = this.createRequestContext(request);
            String[] attachmentIdList = attachmentIds.split(",");
            int length = 0;
            String fileName = "";
            String zipFileName = "函件套打.zip";
            String zipFilePath = zipFileName;
            FileOutputStream outputStream = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream));
            if (attachmentIdList != null) {
                for (int i = 0; i < attachmentIdList.length; i++) {
                    FndAttachment fndAttachment = new FndAttachment();
                    fndAttachment.setAttachmentId(Long.valueOf(attachmentIdList[i]));
                    FndAttachment attachment = (FndAttachment) fndAttachmentService.selectByPrimaryKey(requestContext, fndAttachment);
                    length += attachment.getFileSize();
                    fileName = System.currentTimeMillis() + attachment.getFileName() + System.currentTimeMillis();
                    String filePath = attachment.getFilePath();
                    InputStream in = this.getAttachmentInputStream(filePath);

                    zipOut.putNextEntry(new ZipEntry(fileName));
                    int j = 0;
                    byte[] buffer = new byte[1024 * 1024 * 2];
                    while ((j = in.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, j);
                    }
                    // 关闭输入流
                    in.close();

                }
                zipOut.closeEntry();
                zipOut.close();
                fileName = "文件集合" + System.currentTimeMillis();
                // 文件压缩成功
                FileInputStream inputStream = new FileInputStream(zipFilePath);
                String userName = requestContext.getUserName();
                String zipFile = "attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso-8859-1") + ".zip";

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", zipFile);
                //response.setHeader("Content-Length", String.valueOf(length));
                response.setCharacterEncoding("UTF-8");

                OutputStream os = new BufferedOutputStream(response.getOutputStream());

                byte[] bytes = new byte[1024 * 1024];
                int i = 0;
                while ((i = inputStream.read(bytes)) > 0) {
                    os.write(bytes, 0, i);
                }
                os.flush();
                os.close();

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public InputStream getAttachmentInputStream(String filePath) throws AttachmentException {
        if (StringUtils.isBlank(filePath)) {
            logger.error("can not find attachment with filePath [{}]", filePath);
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        } else {
            Object answer;

            File file = new File(filePath);
            if (!file.exists()) {
                throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
            }

            try {
                answer = FileUtils.openInputStream(file);
            } catch (IOException var5) {
                logger.error(var5.getMessage(), var5);
                throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
            }


            return (InputStream) answer;
        }
    }

    //归档确认
    @RequestMapping(value = "/contract/archive/confirm")
    public ResponseData archiveConfirm(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusConContractArchive> dto = parameter.toJavaList(HlsCusConContractArchive.class);
        archiveService.archiveConfirm(iRequest, dto);
        return new ResponseData(dto);
    }
}
