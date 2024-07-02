package com.hand.hls.common.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class HlsWordToPdfComponent {

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Value("${jacob.jacobUrl}")
    private String jacobUrl;

    @Value("${jacob.jacobServiceUrl}")
    private String jacobServiceUrl;

    @Value("${jacob.targetPath}")
    private String targetPath;

    @Value("${jacob.start}")
    private String jacobStart;

    private static final String TRUE = "true";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void wordToPdfAttachment(IRequest iRequest, List<FndAttachment> list) {
        if(TRUE.equals(jacobStart)) {
            for (FndAttachment attachment : list) {
                sendWordToPdfRequest(attachment.getAttachmentId());
                attachment = fndAttachmentService.selectByPrimaryKey(iRequest, attachment);
                attachment.setFilePath(attachment.getFilePath() + "_pdf");
                File file = new File(attachment.getFilePath());
                attachment.setFileName(attachment.getFileName().replace("docx", "pdf"));
                attachment.setFileTypeCode(attachment.getFileTypeCode().replace("docx", "pdf"));
                attachment.setFileSize(file.length());
                fndAttachmentService.updateByPrimaryKey(iRequest, attachment);
            }
        }
    }

    public void wordToPdfAttachmentMuti(IRequest iRequest, List<FndAttachmentMulti> list) {
        if(TRUE.equals(jacobStart)) {
            for (FndAttachmentMulti attachmentMulti : list) {
                FndAttachment fndAttachment = new FndAttachment();
                fndAttachment.setAttachmentId(attachmentMulti.getAttachmentId());
                fndAttachment = fndAttachmentService.selectByPrimaryKey(iRequest, fndAttachment);

                sendWordToPdfRequest(attachmentMulti.getAttachmentId());


                fndAttachment.setFilePath(fndAttachment.getFilePath() + "_pdf");
                File file = new File(fndAttachment.getFilePath());
                fndAttachment.setFileName(fndAttachment.getFileName().replace("docx", "pdf"));
                fndAttachment.setFileTypeCode(fndAttachment.getFileTypeCode().replace("docx", "pdf"));
                fndAttachment.setFileSize(file.length());
                fndAttachmentService.updateByPrimaryKey(iRequest, fndAttachment);
            }
        }
    }

    public void sendWordToPdfRequest(Long attachmentId){
        StringBuilder sb = new StringBuilder();
        sb.append(jacobServiceUrl);
        sb.append("?target_url=");
        sb.append(jacobUrl);
        sb.append("/");
        sb.append(attachmentId);
        sb.append("&target_path=");
        sb.append(targetPath);
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                HttpGet httpGet = new HttpGet(sb.toString());

                client = HttpClients.createDefault();
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                logger.info("sendWordToPdfRequest"+result);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
