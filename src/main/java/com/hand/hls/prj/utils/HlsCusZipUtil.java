package com.hand.hls.prj.utils;

import com.hand.hap.attachment.UpConstants;
import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hls.bp.dto.HlsCusSysFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by fjm on 2018/2/7.
 */
public class HlsCusZipUtil {

    /**
     * 压缩文件列表中的文件
     *
     * @param files
     * @param outputStream
     * @throws IOException
     */
    public static void zipFile(List<HlsCusSysFile> files, ZipOutputStream outputStream) throws IOException, AttachmentException {
        try {
            //压缩列表中的文件
            files.forEach(item -> {
                try {
                    File file = new File(item.getFilePath());
                    String originFileName = item.getFileName();
                    zipFile(originFileName, file, outputStream);
                    //删除文件
//                    file.delete();
                } catch (Exception e) {
                    return;
                }
            });
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 将文件写入到zip文件中
     *
     * @param inputFile
     * @param outputStream
     * @throws Exception
     */
    public static void zipFile(String originFileName, File inputFile, ZipOutputStream outputStream) throws IOException, AttachmentException {
        try {
            if (inputFile.exists()) {
                if (inputFile.isFile()) {
                    FileInputStream inStream = new FileInputStream(inputFile);
                    BufferedInputStream bInStream = new BufferedInputStream(inStream);
                    ZipEntry entry = new ZipEntry(originFileName);
                    outputStream.putNextEntry(entry);

                    final int MAX_BYTE = 10 * 1024 * 1024;    //最大的流为10M
                    long streamTotal = 0;                      //接受流的容量
                    int streamNum = 0;                      //流需要分开的数量
                    int leaveByte = 0;                      //文件剩下的字符数
                    byte[] inOutbyte;                          //byte数组接受文件的数据

                    streamTotal = bInStream.available();                        //通过available方法取得流的最大字符数
                    streamNum = (int) Math.floor(streamTotal / MAX_BYTE);    //取得流文件需要分开的数量
                    leaveByte = (int) streamTotal % MAX_BYTE;                //分开文件之后,剩余的数量

                    if (streamNum > 0) {
                        for (int j = 0; j < streamNum; ++j) {
                            inOutbyte = new byte[MAX_BYTE];
                            //读入流,保存在byte数组
                            bInStream.read(inOutbyte, 0, MAX_BYTE);
                            outputStream.write(inOutbyte, 0, MAX_BYTE);  //写出流
                        }
                    }
                    //写出剩下的流数据
                    inOutbyte = new byte[leaveByte];
                    bInStream.read(inOutbyte, 0, leaveByte);
                    outputStream.write(inOutbyte);
                    outputStream.closeEntry();     //Closes the current ZIP entry and positions the stream for writing the next entry
                    bInStream.close();    //关闭
                    inStream.close();
                }
            } else {
                throw new AttachmentException(UpConstants.ERROR_DOWNLOAD_FILE_ERROR, UpConstants.ERROR_DOWNLOAD_FILE_ERROR,new Object[0]);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 下载打包的文件
     *
     * @param file
     * @param response
     */
    public static void downloadZip(File file, HttpServletResponse response) {
        try {
            // 以流的形式下载文件。
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            //response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            response.setContentType("application/zip");
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentLength((int) file.length());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
//            file.delete();        //将生成的服务器端文件删除
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 下载打包的文件
     * 删除服务器端附件
     * @param file
     * @param response
     */
    public static void downloadZip2(File file, HttpServletResponse response) {
        try {
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            response.setContentType("application/zip");
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentLength((int) file.length());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            file.delete();        //将生成的服务器端文件删除
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
