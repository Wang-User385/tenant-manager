package com.hand.hls.fct.service.impl;

import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.mapper.AttachCategoryMapper;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.IAttachmentService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.service.FctProjectAttachmentService;
import com.hand.hls.fct.service.HlsCusFctProjectService;
import com.hand.hls.fct.service.HlsCusPrjContractDocxService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: xuju
 * @Description:
 * @Date: Created in 2018/05/16 14:51
 * @Modified By:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjContractDocxServiceImpl implements HlsCusPrjContractDocxService {

    /**
     * 文件不存在提示.
     */
    private static final String FILE_NOT_EXSIT = "未找到文件模板";
    /*
    租赁项目审批通知书
     */
    private static final String PRJ_APPROVAL_NOTICE_REPORT = "PRJ_APPROVAL_NOTICE_REPORT";

    private static final String FCT_DOC_SOURCE_TYPE = "FCT_PROJECT_ATTACHMENT";
    private static final String AST_NOTICE_MANAGE = "AST_NOTICE_MANAGE";
    /**
     * 文件下载默认编码.
     */
    private static final String ENC = "UTF-8";


    @Autowired
    private IAttachmentService attachmentService;
    @Autowired
    private HlsSysFileService fileService;
    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;
    @Autowired
    private FctProjectAttachmentService fctProjectAttachmentService;
    @Autowired
    private IAttachCategoryService attachCategoryService;

    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;


    @Autowired
    private AttachCategoryMapper attachCategoryMapper;
    @Autowired
    private HlsCusFctProjectService hlsCusFctProjectService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    public void createDocx(IRequest requestContext, InputStream is, File file, Map<String, Object> params) throws Exception {
        //通过输入流构建WordprocessingMLPackage对象
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);
        //将构建的wordMLPackage对象传入方法中
        wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, requestContext, params);
        //将替换后的合同文本保存到服务器上作为备份
        wordMLPackage.save(file);
    }

    @Override
    public void process(IRequest requestContext, Map<String, Object> params) {
        try {
            docxCreateMethod(requestContext, params);
        } catch (Exception e) {
            throw new RuntimeException("生成合同文本失败", e);
        }
    }

    public void docxCreateMethod(IRequest requestContext, Map<String, Object> params) throws Exception {
        Long templetId = Long.parseLong(params.get("templetId").toString());
        Long projectId = Long.parseLong(params.get("projectId").toString());
        String sourceType = params.get("sourceType").toString();
        Long projectAttachmentId = Long.parseLong(params.get("projectAttachmentId").toString());

        String SourceType = "hls_doc_file_templet";
        String backSourceType = "";
        Long SourceKey = templetId;

        FndAttachmentMulti fileParam = new FndAttachmentMulti();
        fileParam.setTableName(SourceType);
        fileParam.setTablePkValue(SourceKey.toString());
        FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(requestContext, fileParam).get(0);
        FndAttachment templateFileParam = new FndAttachment();
        templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
        templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
        List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(requestContext, templateFileParam);
        Validate.notEmpty(fndAttachments, "文件模版不存在");
        FndAttachment sysFile = fndAttachments.get(0);
        if (sysFile == null || StringUtils.isBlank(sysFile.getFilePath())) {
            throw new HlsCusException("文件模版不存在");
        }
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            throw new HlsCusException("文件模版不存在");
        }
        //定义备份文件的大小
        int fileBackLength = 0;
        //先获取模板文件的大小
        int fileLength = (int) file.length();
        if (fileLength > 0) {
            //用inputStram输入流读取本地的docx文件
            InputStream inStream = new FileInputStream(file);
            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
            //复制模板
            copyModel(copyPath, inStream);
            //用输入流读取复制后的模板
            InputStream modelIs = new FileInputStream(copyPath);
            //生成合同文本
            createDocx(requestContext, modelIs, new File(copyPath), params);
            fileBackLength = (int) new File(copyPath).length();
            FndAttachmentMulti condition = new FndAttachmentMulti();
            condition.setTablePkValue(projectAttachmentId.toString());
            condition.setTableName(FCT_DOC_SOURCE_TYPE);
            List<FndAttachmentMulti> list = fndAttachmentMultiService.selectSelective(requestContext, condition);
            //保存生成文件
            FndAttachment conDocFile = null;
            FndAttachmentMulti conDocFileMul = null;
            //已经生成过直接修改路径后保存
            if (!list.isEmpty()) {
                conDocFileMul = list.get(0);
                FndAttachment fndCondition = new FndAttachment();
                fndCondition.setSourceTypeCode("fnd_atm_attachment_multi");
                fndCondition.setSourcePkValue(conDocFileMul.getRecordId().toString());
                conDocFile = fndAttachmentService.selectSelective(requestContext, fndCondition).get(0);
                //上传
                conDocFile.setFilePath(copyPath);
                fndAttachmentService.updateByPrimaryKeySelective(requestContext, conDocFile);
            } else {
                conDocFile = new FndAttachment();
                String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
                conDocFile.setFileName(fileName);
                FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
                conDocFileMulti.setTableName(FCT_DOC_SOURCE_TYPE);
                conDocFileMulti.setTablePkValue(projectAttachmentId.toString());
                conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                conDocFileMulti.setCreatedBy(requestContext.getUserId());
                conDocFileMulti.setCreationDate(new Date());
                conDocFileMulti.setLastUpdateDate(new Date());
                conDocFileMulti.setLastUpdatedBy(requestContext.getUserId());
                fndAttachmentMultiService.insertSelective(requestContext, conDocFileMulti);
                conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
                conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
                //上传
                conDocFile.setFilePath(copyPath);
                conDocFile.setFileTypeCode(".docx");
                conDocFile.setFileSize(((Integer) fileBackLength).longValue());
                conDocFile.setCreationDate(new Date());
                conDocFile.setCreatedBy(requestContext.getUserId());
                conDocFile.setLastUpdateDate(new Date());
                conDocFile.setLastUpdatedBy(requestContext.getUserId());
                fndAttachmentService.insertSelective(requestContext, conDocFile);
                conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                fndAttachmentMultiService.updateByPrimaryKey(requestContext, conDocFileMulti);
            }
        }
    }

    /**
     * 复制文件模板,将读取源文件的输入流复制文件存入一个备用的模板文件
     *
     * @param filePath
     * @param is
     * @throws IOException
     */
    private static synchronized void copyModel(String filePath, InputStream is) throws IOException {

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

    /****************************************通知书打印************************************/
    /**
     * PLM_NOTICE_PRINT
     * <p>
     * 通过合同文本的方式将通知书创建到指定的目录，
     * 再去将我们目录里的文件压缩下载，
     * 再通过将我们服务器文件删除
     * 数据库不会存在相关记录
     */
    @Override
    public String processNotice(IRequest requestContext, Map<String, Object> params) {
        String path = null;
        try {
            path = docxCreateNoticeMethod(requestContext, params);
        } catch (TokenException e) {
            e.printStackTrace();
        } catch (FileReadIOException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        return path;
    }

    private String docxCreateNoticeMethod(IRequest requestContext, Map<String, Object> params) throws TokenException, FileReadIOException, Docx4JException {
        Long templetId = Long.parseLong(params.get("templetId").toString());
        String fileName = params.get("fileName").toString();
        String temporarilyPath = params.get("temporarilyPath").toString();
        String noticeManageId = params.get("noticeManageId").toString();

        String SourceType = "hls_doc_file_templet";
        Long SourceKey = templetId;

        //获取暂存目录

        temporarilyPath += ("\\" + UUID.randomUUID().toString());

        try {
            //根据sys_attachment表上的记录获取附件的信息
            FndAttachmentMulti fileParam = new FndAttachmentMulti();
            fileParam.setTableName(SourceType);
            fileParam.setTablePkValue(SourceKey.toString());
            FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(requestContext, fileParam).get(0);
            FndAttachment templateFileParam = new FndAttachment();
            templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
            templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
            List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(requestContext, templateFileParam);
            Validate.notEmpty(fndAttachments, "文件模版不存在");
            FndAttachment sysFile = fndAttachments.get(0);

            if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
                File file = new File(sysFile.getFilePath());
                if (file.exists()) {
                    //先获取模板文件的大小
                    int fileLength = (int) file.length();
                    //定义备份文件的大小
                    int fileBackLength = 0;
                    if (fileLength > 0) {
                        //用inputStram输入流读取本地的docx文件
                        InputStream inStream = new FileInputStream(file);
                        //复制模板
                        copyModel(temporarilyPath, inStream);
                        //用输入流读取复制后的模板
                        InputStream modelIs = new FileInputStream(temporarilyPath);
                        //生成合同文本
                        createDocx(requestContext, modelIs, new File(temporarilyPath), params);
                        fileBackLength = (int) new File(temporarilyPath).length();
                        FndAttachmentMulti condition = new FndAttachmentMulti();
                        condition.setTablePkValue(noticeManageId);
                        condition.setTableName(AST_NOTICE_MANAGE);
                        List<FndAttachmentMulti> list = fndAttachmentMultiService.selectSelective(requestContext, condition);
                        //保存生成文件
                        FndAttachment conDocFile = null;
                        FndAttachmentMulti conDocFileMul = null;
                        //已经生成过直接修改路径后保存
                        if (!list.isEmpty()) {
                            conDocFileMul = list.get(0);
                            FndAttachment fndCondition = new FndAttachment();
                            fndCondition.setSourceTypeCode("fnd_atm_attachment_multi");
                            fndCondition.setSourcePkValue(conDocFileMul.getRecordId().toString());
                            conDocFile = fndAttachmentService.selectSelective(requestContext, fndCondition).get(0);
                            //上传
                            conDocFile.setFilePath(temporarilyPath);
                            fndAttachmentService.updateByPrimaryKeySelective(requestContext, conDocFile);
                        } else {
                            conDocFile = new FndAttachment();
//                            String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
                            conDocFile.setFileName(fileName);
                            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
                            conDocFileMulti.setTableName(AST_NOTICE_MANAGE);
                            conDocFileMulti.setTablePkValue(noticeManageId);
                            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                            conDocFileMulti.setCreatedBy(requestContext.getUserId());
                            conDocFileMulti.setCreationDate(new Date());
                            conDocFileMulti.setLastUpdateDate(new Date());
                            conDocFileMulti.setLastUpdatedBy(requestContext.getUserId());
                            fndAttachmentMultiService.insertSelective(requestContext, conDocFileMulti);
                            conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
                            conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
                            //上传
                            conDocFile.setFilePath(temporarilyPath);
                            conDocFile.setFileTypeCode(".docx");
                            conDocFile.setFileSize(((Integer) fileBackLength).longValue());
                            conDocFile.setCreationDate(new Date());
                            conDocFile.setCreatedBy(requestContext.getUserId());
                            conDocFile.setLastUpdateDate(new Date());
                            conDocFile.setLastUpdatedBy(requestContext.getUserId());
                            fndAttachmentService.insertSelective(requestContext, conDocFile);
                            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                            fndAttachmentMultiService.updateByPrimaryKey(requestContext, conDocFileMulti);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temporarilyPath;
    }
}
