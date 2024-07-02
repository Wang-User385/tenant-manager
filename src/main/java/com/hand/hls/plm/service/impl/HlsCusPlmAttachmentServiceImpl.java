package com.hand.hls.plm.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.IAttachmentService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.plm.dto.HlsCusPlmAttachment;
import com.hand.hls.plm.mapper.HlsCusPlmAttachmentMapper;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import com.hand.hls.plm.service.HlsCusPlmIAttachmentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmAttachmentServiceImpl extends BaseServiceImpl<HlsCusPlmAttachment> implements HlsCusPlmIAttachmentService {

    @Autowired
    private HlsCusPlmAttachmentMapper mapper;

    @Autowired
    private HlsDocFileTempletMapper docFileTempletMapper;

    @Autowired
    private IAttachmentService attachmentService;

    //@Autowired
    //private HlsSysFileService fileService;

    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;

    @Autowired
    private IAttachCategoryService attachCategoryService;

    //@Autowired
    //private HlsSysAttachmentService hlsSysAttachmentService;
    @Autowired
    private HlsCusIPostloanInspectionService hlsCusIPostloanInspectionService;

    /**
     * @Description:附件查询
     * @Author: Wty
     * @Date: Created om 13:14 2018/5/23
     */
    @Override
    public List<HlsCusPlmAttachment> plmAttachmentDetailQuery(IRequest iRequest, HlsCusPlmAttachment hlsCusFinanceAttachment, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.plmAttachmentDetailQuery(hlsCusFinanceAttachment);
    }

    @Override
    public List<HlsCusPlmAttachment> plmAttachmentDetailQuery2(IRequest iRequest, HlsCusPlmAttachment hlsCusFinanceAttachment, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.plmAttachmentDetailQuery2(hlsCusFinanceAttachment);
    }

    @Override
    public List<HlsCusPlmAttachment> queryAllFile(IRequest requestContext, HlsCusPlmAttachment hlsCusPlmAttachment, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAllFile(hlsCusPlmAttachment);
    }

    /**
     * @Description:五级分类变更时删除原本的附件更改changeDelete
     * @Author: Wty
     * @Date: Created om 13:15 2018/5/23
     */
    @Override
    public void plmAttachmentChangeOldRemove(IRequest iRequest, HlsCusPlmAttachment hlsCusFinanceAttachment) {
        mapper.plmAttachmentChangeOldRemove(hlsCusFinanceAttachment);
    }

    /**
     * @Description:创建并下载打印文本
     * @Author: Wty
     * @Date: Created om 下午3:31 2018/7/2
     */
    //@Override
    /*public List<HlsCusSysFile> createPrintText(IRequest iRequest, HlsCusPlmAttachment hlsCusPlmAttachment) {
        List<HlsCusSysFile> files = new ArrayList<>();
        HlsDocFileTemplet docFileTemplet = new HlsDocFileTemplet();
        docFileTemplet.setTempletCode(hlsCusPlmAttachment.getTemplateCode());
        List<HlsDocFileTemplet> docFileTemplets = docFileTempletMapper.selectList(docFileTemplet);
        String plmType = hlsCusPlmAttachment.getPlmType();
        if (CollectionUtils.isNotEmpty(docFileTemplets)) {
            Map<String, Object> params = new HashMap<>();
            if ("FC".equals(plmType)) {
                params.put("fiveClassificationId", hlsCusPlmAttachment.getPlmId());
                params.put("contractNumber", hlsCusPlmAttachment.getContractNumber());
                params.put("attachmentCategory", "PLM_FC_ATTACHMENT");
                params.put("attachmentSourceType","PLM_FC_DOC_FILE_ATTACHMENT");
            }else if ("PLI".equals(plmType)){
                params.put("postLoanInspectionId", hlsCusPlmAttachment.getPlmId());
                params.put("attachmentCategory", "PLM_PLI_ATTACHMENT");
                params.put("attachmentSourceType","PLM_PLI_DOC_FILE_ATTACHMENT");
            } else if ("RW".equals(plmType)) {
                params.put("riskWarningId", hlsCusPlmAttachment.getPlmId());
                params.put("attachmentCategory", "PLM_RW_ATTACHMENT");
                params.put("attachmentSourceType","PLM_RW_DOC_FILE_ATTACHMENT");
            }
            //所有文本模版的参数都是固定的
            params.put("sourceType", "HLS_DOC_FILE_TEMPLET");
            params.put("sourceKey", docFileTemplets.get(0).getTempletId());
            params.put("templetId", docFileTemplets.get(0).getTempletId());
            params.put("plmId", hlsCusPlmAttachment.getPlmId());
            params.put("plmType", hlsCusPlmAttachment.getPlmType());
            files.add(process(iRequest, params));
        } else {
            throw new IllegalArgumentException("找不到对应的模版！");
        }
        return files;
    }*/

    /**
     * @Description:生成打印文本
     * @Author: Wty
     * @Date: Created om 下午4:06 2018/7/2
     * @param: [requestContext, params] params参数
     * @return: hls.core.hls.bp.dto.HlsCusSysFile
     */
    /*private HlsCusSysFile docxCreateMethod(IRequest requestContext, Map<String, Object> params) throws Exception {
        HlsCusSysFile returnSysFile = null;
        String attachmentSourceType = params.get("attachmentSourceType").toString();
        String attachmentCategory = params.get("attachmentCategory").toString();
            //根据sys_attachment表上的记录获取附件的信息
            Attachment attachment = attachmentService.selectAttachByCodeAndKey(requestContext, params.get("sourceType").toString(), params.get("sourceKey").toString());
            if (null!=attachment)
            {
                HlsCusSysFile sysFile = fileService.queryByAttachmentId(attachment.getAttachmentId());
                if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
                    File file = new File(sysFile.getFilePath());
                    if (file.exists()) {
                        int fileLength = (int) file.length();//先获取模板文件的大小
                        int fileBackLength = 0;//定义备份文件的大小
                        if (fileLength > 0) {
                            InputStream inStream = new FileInputStream(file);//用inputStram输入流读取本地的docx文件

                            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());//定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖

                            HlsCusPlmAttachmentServiceImpl.copyModel(copyPath, inStream);//复制模板

                            InputStream modelIs = new FileInputStream(copyPath);//用输入流读取复制后的模板

                            createDocx(requestContext, modelIs, new File(copyPath), params);//生成合同文本

                            fileBackLength = (int) new File(copyPath).length();

                            //查找attactCategory  id
                            AttachCategory attachCategory = new AttachCategory();
                            attachCategory.setSourceType(attachmentSourceType);
                            attachCategory.setStatus("1");
                            attachCategory = attachCategoryService.selectAttachByCode(requestContext,attachmentSourceType);

                            //更新附件列表
                            HlsCusSysAttachment hsa = new HlsCusSysAttachment();
                            hsa.setSourceKey(attachmentCategory+"-"+ params.get("plmId").toString());
                            hsa.setCategoryId(attachCategory.getCategoryId());
                            hsa.setSourceType(attachmentCategory);
                            hsa.setName(attachmentCategory);
                            hsa.setStatus("1");

                            //如果有就更新
                            if (hlsSysAttachmentService.sysAttachmentQuery(hsa).size() < 1) {
                                hsa = hlsSysAttachmentService.insertSelective(requestContext, hsa);
                            } else {
                                hsa = hlsSysAttachmentService.sysAttachmentQuery(hsa).get(0);
                            }

                            List<HlsCusSysFile> hlsCusSysFileList = fileService.selectByAttachmentId(hsa.getAttachmentId());
                            for (int i = 0; i < hlsCusSysFileList.size(); i++) {
                                fileService.deleteByPrimaryKey(hlsCusSysFileList.get(i));
                            }

                            HlsCusSysFile backSysFile = new HlsCusSysFile();
                            String fileBak = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5);
                            backSysFile.setFileName(fileBak.concat(".docx"));
                            backSysFile.setAttachmentId(hsa.getAttachmentId());
                            backSysFile.setFilePath(copyPath);
                            backSysFile.setFileSize(new BigDecimal(fileBackLength));
                            backSysFile.setFileType(sysFile.getFileType());
                            backSysFile.setUploadDate(new Date());
                            returnSysFile = fileService.insertSelective(requestContext, backSysFile);
                        }
                    }
                    else {
                        throw new FileNotFoundException("未找到对应文件");
                    }
                }
            }
        return returnSysFile;
    }*/

    /**
     * @Description:异常处理
     * @Author: Wty
     * @Date: Created om 下午4:09 2018/7/2
     */
   /* public HlsCusSysFile process(IRequest requestContext, Map<String, Object> params) {
        try {
            return docxCreateMethod(requestContext, params);
        } catch (TokenException e) {
            e.printStackTrace();
        } catch (FileReadIOException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }*/

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

    public void createDocx(IRequest requestContext, InputStream is, File file, Map<String, Object> params) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);//通过输入流构建WordprocessingMLPackage对象

        wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, requestContext, params);//将构建的wordMLPackage对象传入方法中

        wordMLPackage.save(file);//将替换后的合同文本保存到服务器上作为备份
    }

    @Override
    public void downPLmZip(String postloanInspectionIds, String printType, IRequest iRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {


        String[] postloanInspectionIdArr = postloanInspectionIds.split(",");
        //List<HlsCusSysFile> hlsCusSysFiles = new ArrayList<>();
        for (int i = 0; i < postloanInspectionIdArr.length; i++) {
            HlsCusPostloanInspection hlsCusPostloanInspection = new HlsCusPostloanInspection();
            hlsCusPostloanInspection.setPostloanInspectionId(Long.parseLong(postloanInspectionIdArr[i].split("-")[0]));
            hlsCusPostloanInspection = hlsCusIPostloanInspectionService.selectByPrimaryKey(iRequest, hlsCusPostloanInspection);
            String templateCode = "";
            if ("check_items".equals(printType)) {
                templateCode = "POST_LOAN_ON_SITE_ONSPECTION_ITEM_BP";
            } else {
                if ("ON_SITE_INSPECT".equals(hlsCusPostloanInspection.getInspectionType())) {
                    templateCode = "POST_LOAN_ON_SITE_ONSPECTION_REPORT_BP";
                } else if ("OFF_SITE_INSPECT".equals(hlsCusPostloanInspection.getInspectionType())) {
                    templateCode = "POST_LOAN_OFF_SITE_ONSPECTION_REPORT_BP";
                } else if ("OFF_SITE_INSPECT_PLAN".equals(hlsCusPostloanInspection.getInspectionType())) {
                    templateCode = "POST_LOAN_OFF_SITE_ONSPECTION_REPORT_PLANE";
                } else if ("ON_SITE_INSPECT_PLANE".equals(hlsCusPostloanInspection.getInspectionType())) {
                    templateCode = "POST_LOAN_ON_SITE_ONSPECTION_REPORT_PLANE";
                }
            }

           /* HlsDocFileTemplet docFileTemplet = new HlsDocFileTemplet();
            docFileTemplet.setTempletCode(templateCode);
            List<HlsDocFileTemplet> docFileTemplets = docFileTempletMapper.selectList(docFileTemplet);
            Map<String, Object> params = new HashMap<>();
            HlsCusSysFile file = new HlsCusSysFile();
            params.put("sourceType", "HLS_DOC_FILE_TEMPLET");
            params.put("bpId",postloanInspectionIdArr[i].split("-")[1]);
            params.put("sourceKey", docFileTemplets.get(0).getTempletId());
            params.put("templetId", docFileTemplets.get(0).getTempletId());
            params.put("plmId", postloanInspectionIdArr[i].split("-")[0]);
            params.put("postLoanInspectionId", postloanInspectionIdArr[i].split("-")[0]);
            params.put("attachmentCategory", "PLM_PLI_ATTACHMENT");
            params.put("attachmentSourceType", "PLM_PLI_DOC_FILE_ATTACHMENT");
            hlsCusSysFiles.add(process(iRequest, params));*/
        }
        /*if (hlsCusSysFiles.size() > 0) {
            String userName = iRequest.getUserName();
            File zipFilePath = new File("/u01/uploads");
            if (!zipFilePath.exists()) {
                zipFilePath.mkdirs();
            }
            //拼接文件名,用户名+系统时间,避免出现重复
            String zipFile = zipFilePath + File.separator + userName + "-" + System.currentTimeMillis() + ".zip";
            FileOutputStream outStream = new FileOutputStream(zipFile);
            ZipOutputStream toClient = new ZipOutputStream(outStream);
            toClient.setEncoding("GBK");//设置编码,避免出现乱码
            //压缩列表中的文件
            hlsCusSysFiles.forEach(item -> {
                try {
                    File fileAttachment = new File(item.getFilePath());
                    String originFileName = item.getFileName();
                    HlsCusZipUtil.zipFile(originFileName, fileAttachment, toClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            toClient.close();//关闭流
            outStream.close();//关闭流
            HlsCusZipUtil.downloadZip(new File(zipFile), response);//下载zip文件
        }*/
    }
}
