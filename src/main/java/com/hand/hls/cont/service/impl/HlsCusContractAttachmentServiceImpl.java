package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.IAttachmentService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusContractAttachment;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsCusContractAttachmentMapper;
import com.hand.hls.cont.service.HlsCusContractAttachmentService;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.gld.service.HlsCusConContractService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractAttachmentServiceImpl extends BaseServiceImpl<HlsCusContractAttachment> implements HlsCusContractAttachmentService {
    @Autowired
    HlsCusContractAttachmentMapper hlsCusContractAttachmentMapper;
//    @Autowired
//    private HlsSysAttachmentService hlsSysAttachmentService;
    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;

//    @Autowired
//    private HlsCusSysFileMapper sysFileMapper;
    @Autowired
    private IAttachmentService iAttachmentService;

//    @Autowired
//    private HlsSysFileService fileService;
    @Autowired
    private IAttachCategoryService attachCategoryService;

    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

//    @Autowired
//    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Override
    public List<HlsCusContractAttachment> queryContractAttachment(IRequest request, HlsCusContractAttachment hlsCusContractAttachment) {
        return hlsCusContractAttachmentMapper.queryContractAttachment(hlsCusContractAttachment);

    }

    @Override
    public List<HlsCusContractAttachment> selectCshDocByContractIdAndCategory(IRequest request, HlsCusContractAttachment hlsCusContractAttachment) {
        return hlsCusContractAttachmentMapper.selectCshDocByContractIdAndCategory(hlsCusContractAttachment);

    }

    @Override
    public void createContractDocumentFile(IRequest iRequest, HlsCusConContract hlsCusConContract) throws Exception {
        HlsDocFileTemplet templet = new HlsDocFileTemplet();
        templet.setTempletCode(hlsCusConContract.getTempletCode());
        List<HlsDocFileTemplet> docFileTemplets = hlsDocFileTempletService.select(iRequest, templet, 1, 1);

        HlsCusContractAttachment param = new HlsCusContractAttachment();
        param.setContractId(hlsCusConContract.getContractId());
        List<HlsCusContractAttachment> list = hlsCusContractAttachmentMapper.contractAttachmentLendDocumentListQuery(param);
        if(CollectionUtils.isNotEmpty(list)){
            return;
        }

        if (docFileTemplets.size() == 1) {
            docxCreateMethod(iRequest, docFileTemplets.get(0).getTempletId(), hlsCusConContract);
        } else {
            throw new HlsCusException("不存在模板,请配置");
        }

    }

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    public void docxCreateMethod(IRequest iRequest, Long templetId, HlsCusConContract hlsCusConContract) throws Exception {
        Long contractId = hlsCusConContract.getContractId();
        String sourceType = "hls_doc_file_templet";
        FndAttachmentMulti fileParam = new FndAttachmentMulti();
        fileParam.setTableName(sourceType);
        fileParam.setTablePkValue(templetId.toString());
        FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(iRequest, fileParam).get(0);
        FndAttachment templateFileParam = new FndAttachment();
        templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
        templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
        List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(iRequest, templateFileParam);
        Validate.notEmpty(fndAttachments, "文件模版不存在");
        FndAttachment sysFile = fndAttachments.get(0);
        if (sysFile == null || StringUtils.isBlank(sysFile.getFilePath())) {
            throw new HlsCusException("文件模版不存在");
        }
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            throw new HlsCusException("文件模版不存在");
        }
        int fileLength = (int) file.length();//先获取模板文件的大小
        int fileBackLength = 0;//定义备份文件的大小
        FndAttachment conDocFile = null;
        if (fileLength > 0) {
            InputStream inStream = new FileInputStream(file);//用inputStram输入流读取本地的docx文件
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());//定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            this.copyModel(copyPath, inStream);//复制模板

            InputStream modelIs = new FileInputStream(copyPath);//用输入流读取复制后的模板
            Map<String, Object> params = new HashMap<>();
            params.put("templetId", templetId);
            params.put("contractId", contractId);
            params.put("projectId", hlsCusConContract.getProjectId());
            createDocx(iRequest, modelIs, new File(copyPath), params);
            fileBackLength = (int) new File(copyPath).length();
            HlsCusContractAttachment hlsCusContractAttachment = new HlsCusContractAttachment();
            hlsCusContractAttachment.setContractAttachmentCategory(hlsCusConContract.getDocumentType());
            hlsCusContractAttachment.setContractId(contractId);
            hlsCusContractAttachment.setDocumentName(hlsCusConContract.getDocDocumentName() + ".docx");
            hlsCusContractAttachment.setSourceType(hlsCusConContract.getDocumentType());
            hlsCusContractAttachment = self().insertSelective(iRequest, hlsCusContractAttachment);

            conDocFile = new FndAttachment();
            String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
            conDocFile.setFileName(fileName);
            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
            conDocFileMulti.setTableName("CON_CONTRACT_ATTACHMENT");
            conDocFileMulti.setTablePkValue(hlsCusContractAttachment.getContractAttachmentId().toString());
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            conDocFileMulti.setCreatedBy(iRequest.getUserId());
            conDocFileMulti.setCreationDate(new Date());
            conDocFileMulti.setLastUpdateDate(new Date());
            conDocFileMulti.setLastUpdatedBy(iRequest.getUserId());
            fndAttachmentMultiService.insertSelective(iRequest, conDocFileMulti);
            conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
            conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
            //上传
            conDocFile.setFilePath(copyPath);
            conDocFile.setFileTypeCode(".docx");
            conDocFile.setFileSize(((Integer) fileBackLength).longValue());
            conDocFile.setCreationDate(new Date());
            conDocFile.setCreatedBy(iRequest.getUserId());
            conDocFile.setLastUpdateDate(new Date());
            conDocFile.setLastUpdatedBy(iRequest.getUserId());
            fndAttachmentService.insertSelective(iRequest, conDocFile);
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            fndAttachmentMultiService.updateByPrimaryKey(iRequest, conDocFileMulti);
        }
    }

    public void createDocx(IRequest requestContext, InputStream is, File file, Map<String, Object> params) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);//通过输入流构建WordprocessingMLPackage对象

        wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, requestContext, params);//将构建的wordMLPackage对象传入方法中

        wordMLPackage.save(file);//将替换后的合同文本保存到服务器上作为备份
    }

    /**
     * 复制文件模板,将读取源文件的输入流复制文件存入一个备用的模板文件
     *
     * @param filePath
     * @param is
     * @throws IOException
     */
    private synchronized void copyModel(String filePath, InputStream is) throws IOException {

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

    @Override
    public List<HlsCusContractAttachment> selectContractFineAttachment(IRequest request, HlsCusContractAttachment hlsCusContractAttachment, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return hlsCusContractAttachmentMapper.selectContractFineAttachment(hlsCusContractAttachment);
    }

    @Override
    public List<HlsCusContractAttachment> contractAttachmentLendDocumentListQuery(IRequest request, HlsCusContractAttachment hlsCusContractAttachment) {
        return hlsCusContractAttachmentMapper.contractAttachmentLendDocumentListQuery(hlsCusContractAttachment);

    }

    @Override
    public List<HlsCusContractAttachment> selectContractEndFileByContractId(IRequest request, HlsCusContractAttachment hlsCusContractAttachment) {
        return hlsCusContractAttachmentMapper.selectContractEndFileByContractId(hlsCusContractAttachment);

    }

}