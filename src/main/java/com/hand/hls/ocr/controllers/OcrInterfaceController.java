package com.hand.hls.ocr.controllers;

import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.ocr.service.OcrInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

@Controller
public class OcrInterfaceController extends BaseController {
    @Autowired
    private OcrInterfaceService service;
    // ocr导入发票
    @RequestMapping(value = "/vat/acp/invoice/ocr/import")
    @ResponseBody
    public ResponseData queryVatAcpInvoiceOcrImport(HttpServletRequest request, HttpServletResponse response) throws HlsCusException {

        IRequest iRequest = this.createRequestContext(request);
        service.ocrImportInvoice(request,iRequest);

        return new ResponseData(new ArrayList<>());
    }

    // ocr导入表格
    @RequestMapping(value = "/table/ocr/import")
    @ResponseBody
    public String tableOcrImport(HttpServletRequest request, HttpServletResponse response) throws HlsCusException {

        IRequest iRequest = this.createRequestContext(request);

        return service.ocrImportTable(request,iRequest);
    }

    // ocr识别pdf
    @RequestMapping(value = "/pdf/ocr/import")
    @ResponseBody
    public String pdfOcrImport(HttpServletRequest request, HttpServletResponse response) throws HlsCusException {

        IRequest iRequest = this.createRequestContext(request);

        return service.ocrImportPdf(request,iRequest);
    }

    //
    @SuppressWarnings("all")
    @RequestMapping(value = "/ocr/attachment/package/download")
    public void downloanFile(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileName) throws FileReadIOException {
        try {
            String path = service.getSavePathDir();
            String addHeader = "attachment;filename=\"";
            path += fileName;
            addHeader += URLEncoder.encode(fileName, "UTF-8");
            addHeader += "\"";
            File file = new File(path);
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
