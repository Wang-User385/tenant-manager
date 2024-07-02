package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsCusImpTemplate;
import com.hand.hls.fnd.mapper.HlsCusImpTemplateMapper;
import com.hand.hls.fnd.service.HlsCusImpTemplateService;
import com.hand.hls.utils.HlsCusPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusImpTemplateServiceImpl extends BaseServiceImpl<HlsCusImpTemplate> implements HlsCusImpTemplateService {

    @Autowired
    private HlsCusImpTemplateMapper mapper;

    @Override
    public HlsCusImpTemplate selectTemplateByCode(IRequest iRequest, HlsCusImpTemplate template){
        return mapper.selectTemplateByCode(template);
    }

    @Override
    public List<HlsCusImpTemplate> selectTempCode() {
        return mapper.selectTempCode();
    }

    @Override
    public void downloadFile(HttpServletResponse response, HlsCusImpTemplate template) {
        FileInputStream inputStream = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ServletOutputStream outputStream = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        InputStream is = null;
        try {
            // 获得模板文件的输入流
            inputStream = new FileInputStream(new File(HlsCusPropertiesUtil.getValue("template.filePath")+template.getTempName()+".xlsx"));
            byte[] buffer = new byte[1024];
            int n;
            while((n = inputStream.read(buffer)) != -1){
                os.write(buffer,0,n);
            }
            byte[] content = os.toByteArray();
            is = new ByteArrayInputStream(content);
            // 触发弹出浏览器下载页
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ new String((template.getTempName()+".xlsx").getBytes(), "iso-8859-1"));
            response.setContentLength(content.length);
            outputStream = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                bos.close();
                bis.close();
                outputStream.flush();
                outputStream.close();
                is.close();
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}