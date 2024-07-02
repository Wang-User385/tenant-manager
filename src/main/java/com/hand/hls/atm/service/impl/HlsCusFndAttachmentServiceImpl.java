package com.hand.hls.atm.service.impl;

import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.service.HlsCusFndAttachmentService;
import com.hand.hls.atm.service.IFndAttachmentService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class HlsCusFndAttachmentServiceImpl extends BaseServiceImpl<FndAttachment> implements HlsCusFndAttachmentService {
    private static Logger logger = LoggerFactory.getLogger(HlsCusFndAttachmentServiceImpl.class);
    private Logger logger1 = LoggerFactory.getLogger(getClass());
    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Override
    @SuppressWarnings("all")
    public void attachmentPackageDownload(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, IRequest request, Long[] attachmentIds, String name) {
        /*if(attachmentIds == null || attachmentIds.length == 0){
            return ;
        }*/
        String[] paths = new String[attachmentIds.length];
        String[] fileNames = new String[attachmentIds.length];
        for(int i = 0; i < attachmentIds.length; i++){
            FndAttachment sysFile = new FndAttachment();
            sysFile.setAttachmentId(attachmentIds[i]);
            sysFile = fndAttachmentService.selectByPrimaryKey(request, sysFile);
            paths[i] = sysFile.getFilePath();
            fileNames[i] = sysFile.getFileName();
        }
        fileNames = validataFileName(fileNames);
        try {
            File directory = copyToDirectory(paths, fileNames, httpServletRequest.getSession().getServletContext().getRealPath(File.separator)+File.separator+"resources"+File.separator+name+File.separator);
            if(compress(directory.getAbsolutePath(), httpServletRequest.getSession().getServletContext().getRealPath(File.separator)+File.separator+"resources"+File.separator+name+".zip")){
                deleteDirectory(directory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchDownloadAttachment(IRequest iRequest, String attachmentIds,HttpServletResponse response) throws IOException, AttachmentException {
        String[] attachmentIdList = attachmentIds.split(",");
        int length = 0;
        String fileName = "";
        String zipFileName = "测试.zip";
        String zipFilePath = zipFileName;
        FileOutputStream outputStream = new FileOutputStream(zipFilePath);
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream));
        if (attachmentIds != null) {
            for (int i = 0; i < attachmentIdList.length; i++) {
                FndAttachment fndAttachment = new FndAttachment();
                fndAttachment.setAttachmentId(Long.valueOf(attachmentIdList[i]));

                FndAttachment attachment = self().selectByPrimaryKey(iRequest, fndAttachment);
                length += attachment.getFileSize();
                fileName = attachment.getFileName();
                String filePath = attachment.getFilePath();
                InputStream in = getAttachmentInputStream(filePath);
                zipOut.putNextEntry(new ZipEntry(fileName));
                int j = 0;
                byte[] buffer = new byte[1024 * 1024];
                while ((j = in.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, j);
                }
                // 关闭输入流
                in.close();

            }
            zipOut.closeEntry();
            zipOut.close();
            // 文件压缩成功
            FileInputStream inputStream = new FileInputStream(zipFilePath);
            String userName = iRequest.getUserName();
            String zipFile = "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1")  + ".zip";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", zipFile);
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            byte[] bytes = new byte[1024 * 1024];
            int i = 0;
            while ((i = inputStream.read(bytes)) > 0) {
                os.write(bytes, 0, i);
            }
            os.flush();
            os.close();

        }
    }

    public InputStream getAttachmentInputStream(String filePath) throws AttachmentException {
        if (net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isBlank(filePath)) {
            this.logger1.error("can not find attachment with filePath [{}]", filePath);
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
                this.logger1.error(var5.getMessage(), var5);
                throw new AttachmentException("msg.warning.download.file.error", "msg.warning.download.file.error", new Object[0]);
            }


            return (InputStream) answer;
        }
    }


    /**
     * 文件名称校验
     */
    private String[] validataFileName(String[] fileNames){
        for(int i = 0; i < fileNames.length-1; i++){
            if(StringUtils.isEmpty(fileNames[i])){
                fileNames[i] = "未命名";
            }
            int number = 1;
            for(int j = i+1; j < fileNames.length; j++){
                if(StringUtils.equals(fileNames[i], fileNames[j])){
                    if(fileNames[j].contains(".")){
                        fileNames[j] = fileNames[j].split("\\.")[0] + "("+ number +")." + fileNames[j].split("\\.")[1];
                    }else{
                        fileNames[j] += "("+ number +")";
                    }
                    number++;
                }
            }
        }
        return fileNames;
    }

    /**
     * 将文件复制到文件夹中
     * @author congweijing
     * @param filePaths 待复制的文件路径数组
     * @param directoryPath 目标目录路径
     * @return 返回复制后的目录对象
     * @throws IOException io流错误
     */
    private File copyToDirectory(String[] filePaths, String[] fileNames, String directoryPath) throws IOException {
        File directory = createNewDirectory(directoryPath);
        for(int i = 0; i < filePaths.length; i++){
            File file = new File(filePaths[i]);
            if(file.exists()){
                FileUtils.copyFile(file, new File(directory.getAbsolutePath()+File.separator+fileNames[i]));
            }
        }
        return directory;
    }

    /**
     * 创建新的目录，当指定目录存在时在目录名称后面添加随机数以创建目录
     * @author congweijing
     * @param directoryPath 指定创建的目录路径
     * @return 返回创建的目录
     */
    private File createNewDirectory(String directoryPath) throws IOException {
        File file = new File(directoryPath);
        if(file.exists()){
            String path = directoryPath.substring(0,directoryPath.length()-1)+System.currentTimeMillis()+File.separator;
            return createNewDirectory(path);
        }
        if(file.mkdirs()){
            return file;
        }else{
            logger.error("Directory create error!");
            throw new IOException("目录创建错误");
        }
    }

    /**
     * 文件夹的压缩
     * @author congweijing
     * @param sourcePath 源目录路径
     * @param targetPath 目标路径
     * @return 压缩结果
     */
    private boolean compress(String sourcePath, String targetPath) throws IOException {
        File source = new File(sourcePath);
        File target = new File(targetPath);
        if(!source.exists()){
            logger.error("源文件夹不存在");
            return false;
        }
        if(target.exists()){
            target.delete();
        }
        FileOutputStream fos = new FileOutputStream(target);
        CheckedOutputStream cos = new CheckedOutputStream(fos,new CRC32());
        ZipOutputStream zos = new ZipOutputStream(cos);
        String baseDir = "";
        compress(source, zos, baseDir);
        if(zos != null){
            zos.close();
        }
        if(fos != null){
            fos.close();
        }
        return true;
    }

    /**
     * 目录压缩的重载递归方法，判断待压缩的是文件还是目录
     * @param file 待压缩的文件/目录对象
     * @param zos 压缩输出流对象
     * @param path 路径
     * @throws IOException io流错误
     */
    private void compress(File file, ZipOutputStream zos, String path) throws IOException {
        if (file.isDirectory()) {
            compressDirectory(file, zos, path);
        } else {
            compressFile(file, zos, path);
        }
    }

    /**
     * 压缩目录
     */
    private void compressDirectory(File file, ZipOutputStream zos, String path) throws IOException {
        File[] files = file.listFiles();
        for(File f : files){
            compress(f, zos, path+file.getName()+File.separator);
        }
    }

    /**
     * 压缩文件
     */
    private void compressFile(File file, ZipOutputStream zos, String path) throws IOException {
        if (!file.exists()){
            return;
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ZipEntry entry = new ZipEntry(path + file.getName());
        zos.putNextEntry(entry);
        int count;
        byte[] bs = new byte[1024];
        while (-1 != (count = bis.read(bs, 0, 1024))) {
            zos.write(bs, 0, count);
            zos.flush();
        }
        if(bis != null){
            bis.close();
        }
    }

    /**
     * 递归删除目录
     */
    private boolean deleteDirectory(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f : files){
                deleteDirectory(f);
            }
        }
        return file.delete();
    }

}
