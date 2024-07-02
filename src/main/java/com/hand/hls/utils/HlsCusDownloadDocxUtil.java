package com.hand.hls.utils;

import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * description
 *
 * @author yuanyuan 2019/07/29 2:44 PM
 */
public class HlsCusDownloadDocxUtil {


    /**
     * 复制文件模板,将读取源文件的输入流复制文件存入一个备用的模板文件
     *
     * @param filePath
     * @param is
     * @throws IOException
     */
    public static synchronized void copyModel(String filePath, InputStream is) throws IOException {

        FileOutputStream fos = new FileOutputStream(filePath); //复制出一个模板
        int readData;
        byte[] b = new byte[1024];

        while ((readData = is.read(b)) != -1) {
            fos.write(b, 0, readData);
        }
        fos.flush();
        is.close();
        fos.close();
    }


    public static void createDocx(IRequest requestContext, InputStream is, File file, Map<String, Object> params) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);//通过输入流构建WordprocessingMLPackage对象
        BookMarkReplaceComponent bookMarkReplaceComponent = SpringContextHolder.getBean(BookMarkReplaceComponent.class);

        wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, requestContext, params);//将构建的wordMLPackage对象传入方法中

        wordMLPackage.save(file);//将替换后的合同文本保存到服务器上作为备份
    }



}
