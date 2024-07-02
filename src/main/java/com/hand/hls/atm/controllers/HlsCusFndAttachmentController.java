package com.hand.hls.atm.controllers;

import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.atm.service.HlsCusFndAttachmentService;
import com.hand.hls.atm.service.IFndAttachmentService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

@Controller
public class HlsCusFndAttachmentController extends BaseController implements InitializingBean {
    @Autowired
    private HlsCusFndAttachmentService fndAttachmentService;
    @Value("${file.upload.dir:.}")
    private String savePath = ".";


    @Override
    public void afterPropertiesSet() throws Exception {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    @RequestMapping({"/fnd/attachment/download/zip"})
    @ResponseBody
    public void downloadAttachmentZip(HttpServletRequest request, @RequestParam("attachment_id") String attachmentIds, HttpServletResponse response) throws Exception {
        IRequest iRequest = this.createRequestContext(request);
        fndAttachmentService.batchDownloadAttachment(iRequest , attachmentIds , response);
    }

    @SuppressWarnings("all")
    @RequestMapping(value = "/attachment/package/download")
    public void downloanFile(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileName) throws FileReadIOException {
        try {
            String name = request.getSession().getServletContext().getRealPath(File.separator) + File.separator+"resources"+File.separator;
            String addHeader = "attachment;filename=\"";
            name += fileName;
            addHeader += URLEncoder.encode(fileName, "UTF-8");
            addHeader += "\"";
            File file = new File(name);
            if (file.exists()) {
                response.addHeader("Content-Disposition", addHeader);
                response.setContentType("EXCEL;charset=UTF-8");
                response.setHeader("Accept-Ranges", "bytes");
                int fileLength = (int) file.length();
                response.setContentLength(fileLength);
                if (fileLength > 0) {
                    writeFileToResp(response, file);
                }
            } else {
                response.getWriter().write("文件不存在！");
            }
            if(!file.delete()){
                throw new FileReadIOException();
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        }
    }

    @SuppressWarnings("all")
    private void writeFileToResp(HttpServletResponse response, File file) throws IOException{
        byte[] buf = new byte[BUFFER_SIZE];
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(file);
            os = response.getOutputStream();
            int readLength;
            while (((readLength = is.read(buf)) != -1)) {
                os.write(buf, 0, readLength);
            }
            os.flush();
        } catch (IOException e){
            throw new IOException("IO流出错");
        } finally {
            os.close();
            is.close();
        }
    }


}
