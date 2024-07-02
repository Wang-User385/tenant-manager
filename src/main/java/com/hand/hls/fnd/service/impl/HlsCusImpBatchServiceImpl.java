package com.hand.hls.fnd.service.impl;

/**
 * @version: 1.0
 * @name: ImpBatchServiceImpl
 * @description: 导入批次表service实现类
 * @date: 2017-08-07 10:44
 */


import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsCusImpBatch;
import com.hand.hls.fnd.dto.HlsCusImpData;
import com.hand.hls.fnd.mapper.HlsCusImpBatchMapper;
import com.hand.hls.fnd.mapper.HlsCusImpDataMapper;
import com.hand.hls.fnd.service.HlsCusImpBatchService;
import com.hand.hls.utils.HlsCusPropertiesUtil;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusImpBatchServiceImpl extends BaseServiceImpl<HlsCusImpBatch> implements HlsCusImpBatchService {




    @Autowired
    private HlsCusImpBatchMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HlsCusImpDataMapper hlsCusImpDataMapper;

    @Override
    public HlsCusImpBatch getBatch(IRequest iRequest, HlsCusImpBatch batch){
        return mapper.selectByPrimaryKey(batch);
    }

    @Override
    public HlsCusImpBatch insertBatch(IRequest request, HlsCusImpBatch batch) {
        // 设置批次的初始值
        String uuid = UUID.randomUUID().toString();
        batch.setFilePath(HlsCusPropertiesUtil.getValue("upload.filePath")+uuid);//后面会将文件写入到由UUID产生的文件名中
        batch.setImpStatus("PARSE SUCCESS");
        batch.setImpErrCount((float) 0);
        batch.setParseErrCount((float) 0);
        batch.setImpDate(new Date());
        batch.setCreatedBy(request.getUserId());
        batch.setLastUpdatedBy(request.getUserId());
        return self().insertSelective(request,batch);
    }

    @Override
    public void uploadFile(HttpServletRequest request,HlsCusImpBatch batch) {
        // 文件上传处理工厂
        FileItemFactory factory = new DiskFileItemFactory();
        // 创建文件上传处理器
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(request);
            Iterator<FileItem> iter = items.iterator();
            List<FileItem> list = IteratorUtils.toList(iter);
            if(list.size()!=1){
                throw new RuntimeException("只能上传一个文件");
            }
           //文件表单字段
            list.get(0).write(new File(batch.getFilePath()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件路径不正确");
        }
        //fileItem = fac.createItem(context.getFieldName(), item.getContentType(), item.isFormField(), fileName);
        // 获得上传的文件
//        MultipartFile multipartFile = ((MultipartHttpServletRequest)request).getFile("file");
//        CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile)multipartFile;
//        FileItem item = commonsMultipartFile.getFileItem();
//        // 写入上传路径
//        try {
//            item.write(new File(batch.getFilePath()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void downloadFile(HttpServletResponse response,HlsCusImpBatch batch) {
        FileInputStream inputStream = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ServletOutputStream outputStream = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        InputStream is = null;
        try {
            // 获得模板文件的输入流
            inputStream = new FileInputStream(new File(batch.getFilePath()));
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
            response.setHeader("Content-Disposition", "attachment;filename="+ new String((batch.getFileName()).getBytes(), "iso-8859-1"));
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

    @Override
    public void deleteFile(IRequest request, HlsCusImpBatch batch) {
        self().deleteByPrimaryKey(batch);
        HlsCusImpData hlsCusImpData=new HlsCusImpData();
        hlsCusImpData.setBatchId(batch.getBatchId());
        hlsCusImpDataMapper.delete(hlsCusImpData);
        File file = new File(batch.getFilePath());
        file.delete();
    }

    @Override
    public List<User> selectUsers() {
        return userMapper.selectUsers(new User());
    }

    @Override
    public List<HlsCusImpBatch> selectBatch(HlsCusImpBatch dto , int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return mapper.selectBatch(dto);
    }
}