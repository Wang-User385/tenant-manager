package com.hand.hls.cont.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.HlsCusFndAttachmentService;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.*;
import com.hand.hls.cont.service.IBusinessConfirmService;
//import com.hand.hls.docx4J.components.DocxGenerateCommon;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.service.IPrjProjectService;
//import com.hand.hls.ufs.service.IUfsFileService;
import com.hand.hls.utils.HlsCusDownloadDocxUtil;
import hls.core.utils.exception.HlsCusException;
import net.logstash.logback.encoder.org.apache.commons.lang.Validate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class BusinessConfirmServiceImpl extends BaseServiceImpl<BusinessConfirm> implements IBusinessConfirmService{

    private static BusinessConfirmServiceImpl businessConfirmService = new BusinessConfirmServiceImpl();


    @Autowired
    private BusinessConfirmMapper businessConfirmMapper;
    @Autowired
    private BusinessConfirmMapper businessConfirmMapper1;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private ConfirmBatchTempMapper confirmBatchTempMapper;
    @Autowired
    private ConfirmBatchMapper confirmBatchMapper;
    @Autowired
    private HlsDocFileTempletMapper hlsDocFileTempletMapper;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusFndAttachmentService fndAttachmentService1;


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public Long createBusinessConfirm(IRequest iRequest,List<HlsCusConContract> list){
        Long version;
        synchronized (businessConfirmService){
            //查询当前版本号
            version = businessConfirmMapper.queryNextVersion();
            list.stream().forEach(item->{
                BusinessConfirm businessConfirm = new BusinessConfirm();
                businessConfirm.setContractId(item.getContractId());
                businessConfirm.setVersion(version);
                self().insertSelective(iRequest,businessConfirm);
            });
        }
        return version;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void cancelBusinessConfirm(IRequest iRequest,Long batchId){
        ConfirmBatchTemp confirmBatchTemp=confirmBatchTempMapper.selectByPrimaryKey(batchId);
        confirmBatchTempMapper.delete(confirmBatchTemp);
        BusinessConfirm businessConfirm=new BusinessConfirm();
        businessConfirm.setBatchId(batchId);
        List<BusinessConfirm> businessConfirmList=businessConfirmMapper.select(businessConfirm);
        businessConfirmList.stream().forEach(deleteEach->{
            businessConfirmMapper.delete(deleteEach);
        });
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void confirmBusinessConfirm(IRequest iRequest,Long batchId,List<Long> list){
        ConfirmBatchTemp confirmBatchTemp=confirmBatchTempMapper.selectByPrimaryKey(batchId);
        BusinessConfirm queryBusinessConfirmDto=new BusinessConfirm();
        queryBusinessConfirmDto.setBatchId(batchId);
        List<BusinessConfirm> businessConfirmList= businessConfirmMapper.select(queryBusinessConfirmDto);

        ConfirmBatch confirmBatch=new ConfirmBatch();
        confirmBatch.setBatchCode(confirmBatchTemp.getBatchCode());
        confirmBatch.setFactoryId(confirmBatchTemp.getFactoryId());
        confirmBatch.setManufacturerId(confirmBatchTemp.getManufacturerId());
        confirmBatch.setRefreshStatus(confirmBatchTemp.getRefreshStatus());
        confirmBatch.setCreatedBy(iRequest.getUserId());
        confirmBatch.setCreationDate(new Date());
        confirmBatch.setLastUpdatedBy(iRequest.getUserId());
        confirmBatch.setLastUpdateDate(new Date());
        confirmBatchMapper.insertSelective(confirmBatch);

        for(BusinessConfirm businessConfirm:businessConfirmList){
            businessConfirm.setBatchId(confirmBatch.getBatchId());
            businessConfirmMapper.updateByPrimaryKeySelective(businessConfirm);
        }
        for(Long contractId : list){
            BusinessConfirm   confirmBycontract=new BusinessConfirm();
            confirmBycontract.setContractId(contractId);
            confirmBycontract.setBatchId(confirmBatch.getBatchId());
            List<BusinessConfirm> confirmBycontractList= businessConfirmMapper.select(confirmBycontract);
            if(confirmBycontractList.size()==0){
                businessConfirmMapper.insertSelective(confirmBycontract);
            }
        }
        confirmBatchTempMapper.delete(confirmBatchTemp);
    }


//    @Autowired
//    private DocxGenerateCommon docxGenerateCommon;
    @Autowired
    private IPrjProjectService prjProjectService;

//    @Autowired
//    private IUfsFileService ufsFileService;

    @Value("${jacob.start:false}")
    private Boolean jacob;

//    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
//    @Override
//    public void downloadBusinessConfirm(IRequest iRequest, HttpServletResponse response,HttpServletRequest request, List<ConfirmBatch> list) throws Exception {
//        List<Map> downloadList = new ArrayList<>(list.size());
//        final String documentCategory = "BUSINESS_CONFIRMATION";
//        for (ConfirmBatch confirmBatch : list) {
//            ConfirmBatch confirmBatchForDocx= confirmBatchMapper.selectByPrimaryKey(confirmBatch.getBatchId());
//            Map<String, Object> param = new HashMap<>();
//            param.put("batchId", confirmBatchForDocx.getBatchId());
//            param.put("fileName", confirmBatchForDocx.getBatchCode() + "-业务确认函");
//            docxGenerateCommon.comfirmDocx(iRequest, documentCategory, confirmBatchForDocx.getBatchId(),
//                    documentCategory,confirmBatchForDocx.getBatchCode(), param);
//            downloadList.add(param);
//
//            BusinessConfirm businessConfirm=new BusinessConfirm();
//            businessConfirm.setBatchId(confirmBatch.getBatchId());
//            List<BusinessConfirm> businessConfirmList = businessConfirmMapper.select(businessConfirm);
//            for (BusinessConfirm confirm:businessConfirmList){
//                confirm.setStatus("CREATED");
//                businessConfirmMapper.updateByPrimaryKeySelective(confirm);
//            }
//
//        }
//        for (Map map : downloadList) {
//            FndAttachment attachment = new FndAttachment();
//            attachment.setAttachmentId(Long.valueOf(map.get("attachmentId").toString()));
//            //prjProjectService.sendWordToPdfRequest(attachment.getAttachmentId());
//            attachment = fndAttachmentService.selectByPrimaryKey(iRequest, attachment);
//            ufsFileService.sendWordPdfRequestByUfs(attachment);
//            attachment.setFilePath(attachment.getFilePath() + "_pdf");
//            File file = new File(attachment.getFilePath());
//            attachment.setFileName(attachment.getFileName().replace("docx", "pdf"));
//            attachment.setFileTypeCode(attachment.getFileTypeCode().replace("docx", "pdf"));
//            attachment.setFileSize(file.length());
//            map.put(DocxGenerateCommon.FILE_PATH, attachment.getFilePath());
//            map.put(DocxGenerateCommon.FILE_TYPE_CODE, attachment.getFileTypeCode());
//            fndAttachmentService.updateByPrimaryKey(iRequest, attachment);
//        }
//        /*if (jacob) {
//            for (Map map : downloadList) {
//                FndAttachment attachment = new FndAttachment();
//                attachment.setAttachmentId(Long.valueOf(map.get("attachmentId").toString()));
//                prjProjectService.sendWordToPdfRequest(attachment.getAttachmentId());
//                attachment = fndAttachmentService.selectByPrimaryKey(iRequest, attachment);
//                attachment.setFilePath(attachment.getFilePath() + "_pdf");
//                File file = new File(attachment.getFilePath());
//                attachment.setFileName(attachment.getFileName().replace("docx", "pdf"));
//                attachment.setFileTypeCode(attachment.getFileTypeCode().replace("docx", "pdf"));
//                attachment.setFileSize(file.length());
//                map.put(DocxGenerateCommon.FILE_PATH, attachment.getFilePath());
//                map.put(DocxGenerateCommon.FILE_TYPE_CODE, attachment.getFileTypeCode());
//                fndAttachmentService.updateByPrimaryKey(iRequest, attachment);
//            }
//        }*/
//        docxGenerateCommon.docxDown(response, "业务确认函", downloadList);
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
//    public void downloadDealerBusinessConfirm(IRequest iRequest, HttpServletResponse response, HttpServletRequest request, List<ConfirmBatch> list) throws Exception {
//        List<Map> downloadList = new ArrayList<>(list.size());
//        final String documentCategory = "DEALER_BUSINESS_CONFIRMATION";
//        for (ConfirmBatch confirmBatch : list) {
//            ConfirmBatch confirmBatchForDocx= confirmBatchMapper.selectByPrimaryKey(confirmBatch.getBatchId());
//            Map<String, Object> param = new HashMap<>();
//            param.put("batchId", confirmBatchForDocx.getBatchId());
//            param.put("fileName", confirmBatchForDocx.getBatchCode() + "-经销商业务确认函");
//            docxGenerateCommon.comfirmDocx(iRequest, documentCategory, confirmBatchForDocx.getBatchId(),
//                    documentCategory,confirmBatchForDocx.getBatchCode(), param);
//            downloadList.add(param);
//
//            BusinessConfirm businessConfirm=new BusinessConfirm();
//            businessConfirm.setBatchId(confirmBatch.getBatchId());
//            List<BusinessConfirm> businessConfirmList = businessConfirmMapper.select(businessConfirm);
//            for (BusinessConfirm confirm:businessConfirmList){
//                confirm.setDealerStatus("CREATED");
//                businessConfirmMapper.updateByPrimaryKeySelective(confirm);
//            }
//
//        }
//        for (Map map : downloadList) {
//            FndAttachment attachment = new FndAttachment();
//            attachment.setAttachmentId(Long.valueOf(map.get("attachmentId").toString()));
//            //prjProjectService.sendWordToPdfRequest(attachment.getAttachmentId());
//            attachment = fndAttachmentService.selectByPrimaryKey(iRequest, attachment);
//            ufsFileService.sendWordPdfRequestByUfs(attachment);
//            attachment.setFilePath(attachment.getFilePath() + "_pdf");
//            File file = new File(attachment.getFilePath());
//            attachment.setFileName(attachment.getFileName().replace("docx", "pdf"));
//            attachment.setFileTypeCode(attachment.getFileTypeCode().replace("docx", "pdf"));
//            attachment.setFileSize(file.length());
//            map.put(DocxGenerateCommon.FILE_PATH, attachment.getFilePath());
//            map.put(DocxGenerateCommon.FILE_TYPE_CODE, attachment.getFileTypeCode());
//            fndAttachmentService.updateByPrimaryKey(iRequest, attachment);
//        }
//        /*if (jacob) {
//            for (Map map : downloadList) {
//                FndAttachment attachment = new FndAttachment();
//                attachment.setAttachmentId(Long.valueOf(map.get("attachmentId").toString()));
//                prjProjectService.sendWordToPdfRequest(attachment.getAttachmentId());
//                attachment = fndAttachmentService.selectByPrimaryKey(iRequest, attachment);
//                attachment.setFilePath(attachment.getFilePath() + "_pdf");
//                File file = new File(attachment.getFilePath());
//                attachment.setFileName(attachment.getFileName().replace("docx", "pdf"));
//                attachment.setFileTypeCode(attachment.getFileTypeCode().replace("docx", "pdf"));
//                attachment.setFileSize(file.length());
//                map.put(DocxGenerateCommon.FILE_PATH, attachment.getFilePath());
//                map.put(DocxGenerateCommon.FILE_TYPE_CODE, attachment.getFileTypeCode());
//                fndAttachmentService.updateByPrimaryKey(iRequest, attachment);
//            }
//        }*/
//        docxGenerateCommon.docxDown(response, "经销商业务确认函", downloadList);
//    }

    @Override
    public void delBusinessConfirm(IRequest iRequest, String batchIds) {
        //删除确认函中间表
        businessConfirmMapper.batchDel(batchIds);
        //删除确认函批次表
        confirmBatchMapper.batchDel(batchIds);
    }

    @Override
    public List<FndAttachmentMulti> contextCreateMultiple(IRequest request, List<ConfirmBatch> list, HttpServletResponse response) throws Exception {

        List<FndAttachmentMulti> fndAttachmentMultiList1 = new ArrayList<>();
        for (ConfirmBatch confirmBatch: list) {
            HlsDocFileTemplet templet = new HlsDocFileTemplet();
            templet.setTempletCode("LS_BUSINESS_CONFIRMATION");
            List<HlsDocFileTemplet> docFileTemplets = hlsDocFileTempletMapper.select(templet);
            if (docFileTemplets.size() != 1) {
                throw new HlsCusException("不存在模板,请配置");
            }
            Long templetId = docFileTemplets.get(0).getTempletId();
            FndAttachmentMulti fileParam = new FndAttachmentMulti();
            fileParam.setTableName("hls_doc_file_templet");
            fileParam.setTablePkValue(templetId.toString());
            FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(request, fileParam).get(0);
            FndAttachment templateFileParam = new FndAttachment();
            templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
            templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
            List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(request, templateFileParam);
            Validate.notEmpty(fndAttachments, "文件模版不存在");

            FndAttachment sysFile = fndAttachments.get(0);
            if (sysFile == null || StringUtils.isBlank(sysFile.getFilePath())) {
                throw new HlsCusException("文件模版不存在");
            }
            File file = new File(sysFile.getFilePath());
            if (!file.exists()) {
                throw new HlsCusException("文件模版不存在");
            }
            InputStream inStream = new FileInputStream(file);
            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
            //复制模板
            HlsCusDownloadDocxUtil.copyModel(copyPath, inStream);
            //用输入流读取复制后的模板
            InputStream modelIs = new FileInputStream(copyPath);
            Map<String, Object> params = new HashMap<>();
            params.put("templetId", templetId);
            params.put("batchId", confirmBatch.getBatchId());
            //生成合同文本
            String userName=confirmBatch.getBatchCode();
            HlsCusDownloadDocxUtil.createDocx(request, modelIs, new File(copyPath), params);
            FndAttachmentMulti fndAttachmentMulti = insertAtm(request, copyPath, userName+"业务确认函" ,confirmBatch.getBatchId());
            fndAttachmentMultiList1.add(fndAttachmentMulti);
            List<BusinessConfirm> businessConfirmList=businessConfirmMapper.query(confirmBatch.getBatchId());
            for(BusinessConfirm businessConfirm: businessConfirmList){
                HlsCusConContract hlsCusConContract= hlsCusConContractMapper.selectByPrimaryKey(businessConfirm.getContractId());
                hlsCusConContract.setBusinessConfirmStatus("CREATED");
                hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
                BusinessConfirm businessConfirm1=businessConfirmMapper.selectByPrimaryKey(businessConfirm.getConfirmId());
                businessConfirm1.setStatus("CREATED");
                businessConfirmMapper.updateByPrimaryKeySelective(businessConfirm1);
            }
        }

//        Long[] attachmentIds = new Long[fndAttachmentMultiList.size()];
//        for (int i = 0; i < fndAttachmentMultiList.size(); i++) {
//            attachmentIds[i] = fndAttachmentMultiList.get(i);
//        }
//        fndAttachmentService1.batchDownloadAttachment(request,attachmentIds,response);
        return fndAttachmentMultiList1;
    }

    @Override
    public List<FndAttachmentMulti> contextCreateMultiple1(IRequest request, List<ConfirmBatch> list, HttpServletResponse response) throws Exception {
        List<FndAttachmentMulti> fndAttachmentMultiList1 = new ArrayList<>();
        for (ConfirmBatch confirmBatch: list) {
            HlsDocFileTemplet templet = new HlsDocFileTemplet();
            templet.setTempletCode("LS_BUSINESS_CONFIRMATION");
            List<HlsDocFileTemplet> docFileTemplets = hlsDocFileTempletMapper.select(templet);
            if (docFileTemplets.size() != 1) {
                throw new HlsCusException("不存在模板,请配置");
            }
            Long templetId = docFileTemplets.get(0).getTempletId();
            FndAttachmentMulti fileParam = new FndAttachmentMulti();
            fileParam.setTableName("hls_doc_file_templet");
            fileParam.setTablePkValue(templetId.toString());
            FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(request, fileParam).get(0);
            FndAttachment templateFileParam = new FndAttachment();
            templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
            templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
            List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(request, templateFileParam);
            Validate.notEmpty(fndAttachments, "文件模版不存在");

            FndAttachment sysFile = fndAttachments.get(0);
            if (sysFile == null || StringUtils.isBlank(sysFile.getFilePath())) {
                throw new HlsCusException("文件模版不存在");
            }
            File file = new File(sysFile.getFilePath());
            if (!file.exists()) {
                throw new HlsCusException("文件模版不存在");
            }
            InputStream inStream = new FileInputStream(file);
            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());
            //复制模板
            HlsCusDownloadDocxUtil.copyModel(copyPath, inStream);
            //用输入流读取复制后的模板
            InputStream modelIs = new FileInputStream(copyPath);
            Map<String, Object> params = new HashMap<>();
            params.put("templetId", templetId);
            params.put("batchId", confirmBatch.getBatchId());
            //生成合同文本
            String userName=confirmBatch.getBatchCode();
            HlsCusDownloadDocxUtil.createDocx(request, modelIs, new File(copyPath), params);
            FndAttachmentMulti fndAttachmentMulti = insertAtm(request, copyPath, userName+"经销商业务确认函" ,confirmBatch.getBatchId());
            fndAttachmentMultiList1.add(fndAttachmentMulti);
            List<BusinessConfirm> businessConfirmList=businessConfirmMapper.query(confirmBatch.getBatchId());
            for(BusinessConfirm businessConfirm: businessConfirmList){
                HlsCusConContract hlsCusConContract= hlsCusConContractMapper.selectByPrimaryKey(businessConfirm.getContractId());
                hlsCusConContract.setDealerBizCfmStatus("CREATED");
                hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
                BusinessConfirm businessConfirm1=businessConfirmMapper.selectByPrimaryKey(businessConfirm.getConfirmId());
                businessConfirm1.setDealerStatus("CREATED");
                businessConfirmMapper.updateByPrimaryKeySelective(businessConfirm1);
            }
        }

//        Long[] attachmentIds = new Long[fndAttachmentMultiList.size()];
//        for (int i = 0; i < fndAttachmentMultiList.size(); i++) {
//            attachmentIds[i] = fndAttachmentMultiList.get(i);
//        }
//        fndAttachmentService1.batchDownloadAttachment(request,attachmentIds,response);
        return fndAttachmentMultiList1;
    }

    private FndAttachmentMulti insertAtm(IRequest currentRequest, String filePath, String fileName,Long lnId) {
        File file = new File(filePath);
        //插入附件表
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTableName("con_confirm_batch");
        fndAttachmentMulti.setTablePkValue(lnId.toString());
        fndAttachmentMulti = fndAttachmentMultiService.insertSelective(currentRequest, fndAttachmentMulti);
        FndAttachment fndAttachment = new FndAttachment();
        fndAttachment.setSourceTypeCode("fnd_atm_attachment_multi");
        fndAttachment.setSourcePkValue(fndAttachmentMulti.getRecordId().toString());
        fndAttachment.setFileTypeCode("docx");
        fndAttachment.setMimeType("application/msword");
        fndAttachment.setFileName(fileName + ".docx");
        fndAttachment.setFileSize(file.length());
        fndAttachment.setFilePath(filePath);
        fndAttachmentService.insertSelective(currentRequest, fndAttachment);
        fndAttachmentMulti.setAttachmentId(fndAttachment.getAttachmentId());
        fndAttachmentMultiService.updateByPrimaryKeySelective(currentRequest, fndAttachmentMulti);
        return fndAttachmentMulti;
    }

}
