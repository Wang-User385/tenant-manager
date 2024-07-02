package com.hand.hls.office.controllers;

import java.io.File;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.office.adaptor.IOfficeAdaptor;
import com.hand.hls.office.adaptor.impl.DefaultOfficeAdaptor;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import com.hand.hls.office.dto.OfficeFile;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.office.service.IFndAtmAttachmentService;
import com.hand.hls.office.utils.OfficeFileKeyGenerator;
import com.hand.hls.office.utils.OfficeUtilis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * office在线编辑插件
 *
 * @author EricChen 2018-5-16
 * @email qiang.chen04@hand-china.com
 */
@Controller
public class OfficeController extends BaseController implements InitializingBean {

    @Autowired(required = false)
    private IOfficeAdaptor officeAdapter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private IFndAtmAttachmentService fndAtmAttachmentService;
    @Autowired
    private FndAtmAttachmentMapper fndAttachmentMapper;

    /**
     * 在线编辑
     * 示例 :window.open(/sys/office/edit?sourceTypeCode=fnd_atm_attachment_multi&sourcePkValue=15961', '_blank');
     *
     * @param request
     * @param sourceTypeCode FND_ATM_ATTACHMENT.SOURCE_TYPE_CODE
     * @param sourcePkValue  FND_ATM_ATTACHMENT.SOURCE_PK_VALUE
     */
    @RequestMapping("/sys/office/edit")
    public ModelAndView edit(HttpServletRequest request, @RequestParam String sourceTypeCode, @RequestParam String sourcePkValue) throws Exception {
        FndAtmAttachmentDto dto = fndAtmAttachmentService.selectByCodeAndPkValue(sourceTypeCode, sourcePkValue);
        if (dto == null || StringUtils.isEmpty(dto.getFilePath())) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        String filePath = dto.getFilePath();
        if (StringUtils.isBlank(dto.getFileTypeCode()))
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        String ext = dto.getFileTypeCode();
        if (!dto.getFileTypeCode().contains(".")) ext = "." + ext;
        if (!OfficeUtilis.getFileExts().contains(ext)) {
            throw new Exception("不支持的类型");
        }
        File file = new File(filePath);
        if (file.exists()) {
            int fileLength = (int) file.length();
            if (fileLength > 0) {
                String fileName = dto.getFileName();
                String downloadUrl = OfficeUtilis.getDownloadPath(sourceTypeCode, sourcePkValue);
                String callbackUrl = OfficeUtilis.getCallbackUrl(sourceTypeCode, sourcePkValue);
                String fileKey = OfficeFileKeyGenerator.getDocKey(dto.getAttachmentId().toString());
                OfficeFile officeFile = new OfficeFile(fileName, downloadUrl, fileKey, callbackUrl, filePath);
                return officeAdapter.doEdit(request, officeFile);
            }
        } else {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        return null;
    }

    /**
     * 在线查看
     *
     * @param request
     * @param sourceTypeCode FND_ATM_ATTACHMENT.SOURCE_TYPE_CODE
     * @param sourcePkValue  FND_ATM_ATTACHMENT.SOURCE_PK_VALUE
     */
    @RequestMapping("/sys/office/view")
    public ModelAndView view(HttpServletRequest request, @RequestParam String sourceTypeCode, @RequestParam String sourcePkValue) throws Exception {
        FndAtmAttachmentDto dto = fndAtmAttachmentService.selectByCodeAndPkValue(sourceTypeCode, sourcePkValue);
        if (dto == null || StringUtils.isEmpty(dto.getFilePath())) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        String filePath = dto.getFilePath();
        if (StringUtils.isBlank(dto.getFileTypeCode()))
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        String ext = dto.getFileTypeCode();
        if (!dto.getFileTypeCode().contains("."))
            ext = "." + ext;
        if (!OfficeUtilis.getFileExts().contains(ext)) {
            throw new Exception("不支持的类型");
        }
        File file = new File(filePath);
        if (file.exists()) {
            int fileLength = (int) file.length();
            if (fileLength > 0) {
                String fileName = dto.getFileName();
                String downloadUrl = OfficeUtilis.getDownloadPath(sourceTypeCode, sourcePkValue);
                String callbackUrl = OfficeUtilis.getCallbackUrl(sourceTypeCode, sourcePkValue);
                String fileKey = OfficeFileKeyGenerator.getDocKey(dto.getAttachmentId().toString());
                OfficeFile officeFile = new OfficeFile(fileName, downloadUrl, fileKey, callbackUrl, filePath);
                return officeAdapter.doView(request, officeFile);
            }
        } else {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        return null;
    }

    /**
     * 在线查看
     *
     * @param request
     * @param sourceTypeCode FND_ATM_ATTACHMENT.SOURCE_TYPE_CODE
     * @param sourcePkValue  FND_ATM_ATTACHMENT.SOURCE_PK_VALUE
     */
    @RequestMapping("/sys/office/review")
    public ModelAndView review(HttpServletRequest request, @RequestParam String sourceTypeCode, @RequestParam String sourcePkValue) throws Exception {
        FndAtmAttachmentDto dto = fndAtmAttachmentService.selectByCodeAndPkValue(sourceTypeCode, sourcePkValue);
        if (dto == null || StringUtils.isEmpty(dto.getFilePath())) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        String filePath = dto.getFilePath();
        if (StringUtils.isBlank(dto.getFileTypeCode()))
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        String ext = dto.getFileTypeCode();
        if (!dto.getFileTypeCode().contains("."))
            ext = "." + ext;
        if (!OfficeUtilis.getFileExts().contains(ext)) {
            throw new Exception("不支持的类型");
        }
        File file = new File(filePath);
        if (file.exists()) {
            int fileLength = (int) file.length();
            if (fileLength > 0) {
                String fileName = dto.getFileName();
                String downloadUrl = OfficeUtilis.getDownloadPath(sourceTypeCode, sourcePkValue);
                String callbackUrl = OfficeUtilis.getCallbackUrl(sourceTypeCode, sourcePkValue);
                String fileKey = OfficeFileKeyGenerator.getDocKey(dto.getAttachmentId().toString());
                OfficeFile officeFile = new OfficeFile(fileName, downloadUrl, fileKey, callbackUrl, filePath);
                return officeAdapter.review(request, officeFile);
            }
        } else {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        return null;
    }

    /**
     * 回写数据
     *
     * @param sourceTypeCode FND_ATM_ATTACHMENT.SOURCE_TYPE_CODE
     * @param sourcePkValue  FND_ATM_ATTACHMENT.SOURCE_PK_VALUE
     */
    @RequestMapping("/sys/office/open/track")
    public void track(HttpServletRequest request, HttpServletResponse response, @RequestParam String sourceTypeCode, @RequestParam String sourcePkValue) throws Exception {
        FndAtmAttachmentDto dto = fndAtmAttachmentService.selectByCodeAndPkValue(sourceTypeCode, sourcePkValue);
        if (dto == null || StringUtils.isEmpty(dto.getFilePath())) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        String filePath = dto.getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            String fileName = dto.getFileName();
            String downloadUrl = OfficeUtilis.getDownloadPath(sourceTypeCode, sourcePkValue);
            String callbackUrl = OfficeUtilis.getCallbackUrl(sourceTypeCode, sourcePkValue);
            String fileKey = OfficeFileKeyGenerator.getDocKey(dto.getAttachmentId().toString());
            OfficeFile officeFile = new OfficeFile(fileName, downloadUrl, fileKey, callbackUrl, filePath);
            officeAdapter.trackAndSave(request, response, officeFile, dto.getAttachmentId().toString());
            dto.setFileSize(file.length());
            fndAttachmentMapper.updateByPrimaryKey(dto);
        } else {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
    }

    /**
     * 下载文档(只为DocumentServer开放)
     */
    @RequestMapping("/sys/office/open/download")
    public void download4DocumentServer(HttpServletRequest request, HttpServletResponse response, @RequestParam String sourceTypeCode, @RequestParam String sourcePkValue) throws Exception {
        //手动验证是否为documentServer
        if (!checkDocumentServerIp(request))
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        FndAtmAttachmentDto dto = fndAtmAttachmentService.selectByCodeAndPkValue(sourceTypeCode, sourcePkValue);
        if (dto == null || StringUtils.isEmpty(dto.getFilePath())) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        String filePath = dto.getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            String fileTypeCode = StringUtils.isNoneBlank(dto.getFileTypeCode()) ? "." + dto.getFileTypeCode() : ".txt";
            String fileName = StringUtils.isNoneBlank(dto.getFileName()) ? dto.getFileName() : "新建文件" + fileTypeCode;
            response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document" + ";charset=" + "UTF-8");
            response.setHeader("Accept-Ranges", "bytes");
            int fileLength = (int) file.length();
            response.setContentLength(fileLength);
            if (fileLength > 0) {
                String downloadUrl = OfficeUtilis.getDownloadPath(sourceTypeCode, sourcePkValue);
                String callbackUrl = OfficeUtilis.getCallbackUrl(sourceTypeCode, sourcePkValue);
                String fileKey = OfficeFileKeyGenerator.getDocKey(dto.getAttachmentId().toString());

                OfficeFile officeFile = new OfficeFile(fileName, downloadUrl, fileKey, callbackUrl, filePath);
                officeAdapter.download(request, response, officeFile);
            }
        } else {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
    }

    /**
     * 转换文档,
     */
    @RequestMapping("/sys/office/convert")
    public void conversion(HttpServletRequest request, HttpServletResponse response, @RequestParam String sourceTypeCode, @RequestParam String sourcePkValue) throws Exception {
        FndAtmAttachmentDto dto = fndAtmAttachmentService.selectByCodeAndPkValue(sourceTypeCode, sourcePkValue);
        if (dto == null || StringUtils.isEmpty(dto.getFilePath())) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        String filePath = dto.getFilePath();
        if (StringUtils.isBlank(dto.getFileTypeCode()))
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        String ext = dto.getFileTypeCode();
        if (!dto.getFileTypeCode().contains("."))
            ext = "." + ext;
        if (!OfficeUtilis.getFileExts().contains(ext)) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
        }
        String fileName = dto.getFileName();
        String downloadUrl = OfficeUtilis.getDownloadPath(sourceTypeCode, sourcePkValue);
        String callbackUrl = OfficeUtilis.getCallbackUrl(sourceTypeCode, sourcePkValue);
        String fileKey = OfficeFileKeyGenerator.getDocKey(dto.getAttachmentId().toString());
        OfficeFile officeFile = new OfficeFile(fileName, downloadUrl, fileKey, callbackUrl, filePath);
        officeAdapter.doConvertAndDownload(request, response, officeFile);
    }

    /**
     * 检测当前请求是否属于documentServer
     */
    private static boolean checkDocumentServerIp(HttpServletRequest request) {
        String documentServerURL = OfficeUtilis.getDSUrl();
        if (!OfficeUtilis.isDSCheckEnabled()) return true;
        if (StringUtils.isBlank(documentServerURL)) return false;
        String useragent = request.getHeader("useragent");
        if (useragent == null) return false;
        return useragent.contains("Node.js");//防止伪造请求
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (officeAdapter == null) {
            officeAdapter = new DefaultOfficeAdaptor();
            officeAdapter.init();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(officeAdapter);
        }
    }
}
