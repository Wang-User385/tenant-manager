package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.elecSeal.utils.ElectronicSignatureUtils;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.fct.mapper.HlsCusFctProjectAttachmentMapper;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import com.hand.hls.hls.service.HlsMarketingReportDocxService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectDocxService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.apache.commons.lang.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectDocxServiceImpl implements HlsCusPrjProjectDocxService {

    private static final String CREATE_DOCX_FAILURE = "合同文本生成失败!";

    private static final String BAK = "_back_";

    private static final String DOCX_FILE_NAME = "合同文本";

    private static final String DOCX = ".docx";

    private static final String PDF = ".pdf";

    private static final String _PDF = "_pdf";

    /**
     * 文件不存在提示.
     */
    private static final String FILE_NOT_EXSIT = "未找到文件模板";
    /**
     * 文件下载默认编码.
     */
    /**
     * 合同文本上传模板sourceType
     */
    private static final String FILE_DOC_SOURCE_TYPE = "hls_doc_file_templet";
    private static final String ENC = "UTF-8";

    private static final String TEMPLET_ID = "templetId";
    private static final String PROJECT_ID = "projectId";
    private static final String CASHFLOW_ID = "cashflowId";
    private static final String PROJECT_ATTACHMENT_ID = "projectAttachmentId";
    private static final String SOURCE_TYPE = "sourceType";
    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsCusFctProjectAttachmentMapper hlsCusFctProjectAttachmentMapper;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    //合同文本
    public static final String REPORT_DOCX = "HLS_MARKETING_REPORT_DOCX";
    public static final String DATA_ECXEPTION = "数据异常";
    //附件表名
    public static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";
    public static final String NOT_FOUND_CONTRACT_TEMPLATE  = "找不到对应的合同文本模板";
    public static final String TABLE_NAME = "table_name";
    public static final String CONTRACT_DOCX_DESCRIPTION = "合同文本";

    @Autowired
    private HlsCusFctProjectAttachmentService hlsCusFctProjectAttachmentService;
    @Autowired
    private ElectronicSignatureUtils electronicSignatureUtils;


    public void createDocx(IRequest requestContext, InputStream is, File file, Map<String, Object> params) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);//通过输入流构建WordprocessingMLPackage对象

        wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, requestContext, params);//将构建的wordMLPackage对象传入方法中

        wordMLPackage.save(file);//将替换后的合同文本保存到服务器上作为备份

        //wordMLPackage.save(os);//将替换后的合同文本写入到输出流中以用于下载

    }

    @Override
    public List<FndAttachment> process(IRequest requestContext, Map<String, Object> params, JSONObject jsonObject) throws Exception{
        return docxCreateMethod(requestContext, params);
    }

    public List<FndAttachment> docxCreateMethod(IRequest requestContext, Map<String, Object> params) throws FileReadIOException,Docx4JException,Exception  {
        //待合同文本表完成后，此处的contractId参数应该换位contentId,根据contentId获取书签对应的参数的key和value，然后传入到动态sql中执行

        List<FndAttachment> attachmentList = new ArrayList<>();

        //String fileType = PDF; 接口注释
        String fileType = DOCX;

        Long templetId = Long.parseLong(params.get(TEMPLET_ID).toString());
        Long projectId = Long.parseLong(params.get(PROJECT_ID).toString());
        Long cashflowId;
        if (params.containsKey(CASHFLOW_ID)){
            cashflowId = Long.parseLong(params.get(CASHFLOW_ID).toString());
        }


        Long projectAttachmentId = Long.parseLong(params.get(PROJECT_ATTACHMENT_ID).toString());
        //项目信息
        HlsCusPrjProject record = new HlsCusPrjProject();
        record.setProjectId(projectId);
        HlsCusPrjProject report = hlsCusPrjProjectService.selectByPrimaryKey(requestContext, record);

        String SourceType = FILE_DOC_SOURCE_TYPE;//合同文本的SourceType
        Long SourceKey = templetId;//模板的ID为对应的SourceKey

        try {
            FndAttachmentMulti fileParam = new FndAttachmentMulti();
            fileParam.setTableName(SourceType);
            fileParam.setTablePkValue(SourceKey.toString());

            FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(requestContext, fileParam).get(0);

            FndAttachment templateFileParam = new FndAttachment();
            templateFileParam.setSourceTypeCode(FND_ATM_ATTACHMENT_MULTI);
            templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());

            //模板文件
            FndAttachment sysFile = fndAttachmentService.selectSelective(requestContext, templateFileParam).get(0);
            if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
                File file = new File(sysFile.getFilePath());
                if (file.exists()) {

                    int fileLength = (int) file.length();//先获取模板文件的大小

                    int fileBackLength = 0;//定义备份文件的大小

                    if (fileLength > 0) {

                        InputStream inStream = new FileInputStream(file);//用inputStram输入流读取本地的docx文件

                        String copyPath = sysFile.getFilePath().concat(BAK).concat(UUID.randomUUID().toString());//定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖

                        this.copyModel(copyPath, inStream);//复制模板

                        InputStream modelIs = new FileInputStream(copyPath);//用输入流读取复制后的模板

                        //ServletOutputStream outputStream = response.getOutputStream();//获取response对象中的outputStream输出流

                        createDocx(requestContext, modelIs, new File(copyPath), params);//生成合同文本
//                        System.out.println("生成文本的路径"+copyPath); 接口注释
//                        System.out.println("生成文本"+new File(copyPath)); 接口注释
//                        File pdfFile = electronicSignatureUtils.TransformWordToPdfTest(new File(copyPath)); 接口注释
//                        fileBackLength = (int) pdfFile.length(); 接口注释

                        fileBackLength = (int) new File(copyPath).length();

                        FndAttachmentMulti condition = new FndAttachmentMulti();
                        condition.setTablePkValue(projectAttachmentId.toString());
                        condition.setTableName(params.get(TABLE_NAME).toString());
                        List<FndAttachmentMulti> list = fndAttachmentMultiService.selectSelective(requestContext, condition);
                        //保存生成文件
                        FndAttachment conDocFile = null;
                        FndAttachmentMulti conDocFileMul = null;
                        //已经生成过直接修改路径后保存
                        if (!list.isEmpty() && list.size() > 0) {
                            conDocFileMul = list.get(0);
                            FndAttachment fndCondition = new FndAttachment();
                            fndCondition.setSourceTypeCode(FND_ATM_ATTACHMENT_MULTI);
                            fndCondition.setSourcePkValue(conDocFileMul.getRecordId().toString());
                            conDocFile = fndAttachmentService.selectSelective(requestContext, fndCondition).get(0);
                            conDocFile.setFilePath(copyPath);
                            fndAttachmentService.updateByPrimaryKeySelective(requestContext, conDocFile);
                        } else {
                            conDocFile = new FndAttachment();
                            //pdfFile.getName() != null 接口注释
                            if (sysFile.getFileName() != null) {
                                //conDocFile.setFileName(pdfFile.getName()); 接口注释
                                conDocFile.setFileName(sysFile.getFileName());
                            } else{
                                conDocFile.setFileName(DOCX_FILE_NAME + fileType);
                            }
                            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
                            conDocFileMulti.setTableName(params.get(TABLE_NAME).toString());
                            conDocFileMulti.setTablePkValue(projectAttachmentId.toString());
                            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                            conDocFileMulti.setCreatedBy(requestContext.getUserId());
                            conDocFileMulti.setCreationDate(new Date());
                            conDocFileMulti.setLastUpdateDate(new Date());
                            conDocFileMulti.setLastUpdatedBy(requestContext.getUserId());
                            fndAttachmentMultiService.insertSelective(requestContext, conDocFileMulti);

                            conDocFile.setSourceTypeCode(FND_ATM_ATTACHMENT_MULTI);
                            conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());

                            //conDocFile.setFilePath(pdfFile.getPath());  接口注释
                            conDocFile.setFilePath(copyPath);
                            //conDocFile.setFileTypeCode(PDF); 接口注释
                            conDocFile.setFileTypeCode(DOCX);


//                            conDocFile.setSourcePkValue(contractAttachmentId.toString());

                            conDocFile.setFileSize(((Integer) fileBackLength).longValue());
                            conDocFile.setCreationDate(new Date());
                            conDocFile.setCreatedBy(requestContext.getUserId());
                            conDocFile.setLastUpdateDate(new Date());
                            conDocFile.setLastUpdatedBy(requestContext.getUserId());
                            conDocFile = fndAttachmentService.insertSelective(requestContext, conDocFile);
                            attachmentList.add(conDocFile);

                            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
                            fndAttachmentMultiService.updateByPrimaryKey(requestContext, conDocFileMulti);
                        }
                    }

                    //重新插入合同文本记录--插入生成文件名
                    if(params.get(SOURCE_TYPE).toString().equalsIgnoreCase("FCT_PROJECT_ATTACHMENT")){
                        HlsCusFctProjectAttachment ppa = new HlsCusFctProjectAttachment();
                        ppa.setProjectAttachmentId(projectAttachmentId);
                        ppa = hlsCusFctProjectAttachmentService.selectByPrimaryKey(requestContext, ppa);
                            if (report != null) {
                                ppa.setContentFlag("Y");
                                ppa.setObjectVersionNumber(ppa.getObjectVersionNumber());

                            } else {
                                ppa.setContentFlag("N");
                                ppa.setObjectVersionNumber(ppa.getObjectVersionNumber());

                            }
//                            hlsCusFctProjectAttachmentService.updateByPrimaryKey(requestContext, ppa);
                        hlsCusFctProjectAttachmentMapper.updateOne(ppa);

                    }else{
                        HlsCusPrjProjectAttachment ppa = new HlsCusPrjProjectAttachment();
                        ppa.setProjectAttachmentId(projectAttachmentId);
                        ppa = hlsCusPrjProjectAttachmentService.selectByPrimaryKey(requestContext, ppa);
                        if (ppa.getDocumentName() == null || StringUtils.isBlank(ppa.getDocumentName())) {
                            if (report != null) {
                                ppa.setDocumentName(DOCX_FILE_NAME + report.getProjectNumber() + fileType);
                            } else {
                                ppa.setDocumentName(DOCX_FILE_NAME + fileType);
                            }
                            hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(requestContext, ppa);
                        }
                        //response.setContentLength(fileBackLength);//获取替换完成之后的文件大小
                    }



                } else {
                    throw new IOException(CREATE_DOCX_FAILURE);
                    //response.getWriter().write(FILE_NOT_EXSIT);
                }
            } else {
                throw new IOException(CREATE_DOCX_FAILURE);
                //response.getWriter().write(FILE_NOT_EXSIT);
            }
        } catch (Docx4JException e){
            throw new Docx4JException("模板文件有问题！");
        } catch (IOException e) {
            throw new FileReadIOException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return attachmentList;
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

}
