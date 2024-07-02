package com.hand.hls.vat.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.vat.dto.HlsCusAcrReceiptHd;
import com.hand.hls.vat.dto.HlsCusAcrReceiptLn;
import com.hand.hls.vat.dto.ReceiptAttachment;
import com.hand.hls.vat.mapper.HlsCusAcrReceiptHdMapper;
import com.hand.hls.vat.service.IAcrReceiptHdService;
import com.hand.hls.vat.service.IAcrReceiptLnService;
import com.hand.hls.vat.service.IReceiptAttachmentService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

import static com.hand.hls.utils.HlsCusDownloadDocxUtil.createDocx;
import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional(rollbackFor = Exception.class)
public class AcrReceiptHdServiceImpl extends BaseServiceImpl<HlsCusAcrReceiptHd> implements IAcrReceiptHdService {

    private static final String FILED_CF_ITEM = "cfItem";
    private static final String LN_SOURCE_TYPE = "CASHFLOW_PRINCIPAL";
    private static final String ACR_RECEIPT_ATTACHMENT = "ACR_RECEIPT_ATTACHMENT";


    @Autowired
    private HlsCusAcrReceiptHdMapper acrReceiptHdMapper;

    @Autowired
    private IAcrReceiptLnService acrReceiptLnService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;

    @Autowired
    private IReceiptAttachmentService receiptAttachmentService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;

    @Override
    public List<Map> queryReceiptList(int pageNum, int pageSize, Map condition) {
        PageHelper.startPage(pageNum, pageSize);
        return acrReceiptHdMapper.queryReceiptList(condition);
    }

    @Override
    public List<Map> queryWaitingReceiptList(int pageNum, int pageSize, Map condition) {
        PageHelper.startPage(pageNum, pageSize);
        if ("1".equals(condition.get(FILED_CF_ITEM))) {
            return acrReceiptHdMapper.queryLeaseBackPrincipalWaitingReceiptList(condition);
        } else {
            return acrReceiptHdMapper.queryDepositWaitingReceiptList(condition);
        }
    }

    @Override
    public void create(IRequest iRequest, List<HlsCusAcrReceiptHd> dto) throws IllegalArgumentException {
        if (dto != null && dto.size() > 0) {
            int size = dto.stream().collect(groupingBy(o -> o.getBpId())).keySet().size();
            if (size > 1) {
                throw new IllegalArgumentException("请选择相同的商业伙伴进行合并开收据!");
            }
            //add by eugene song 保证金、风险金 只能同一个支付表 合并开票
            if ("DEPOSIT".equals(dto.get(0).getReceiptType())) {
                List<Long> contractIdList = new ArrayList<>();
                dto.stream().forEach(item -> {
                    HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(item.getSourceId());
                    contractIdList.add(cashflow.getContractId());
                });
                long count = contractIdList.stream().distinct().count();
                if (count > 1) {
                    throw new IllegalArgumentException("请选择相同的支付表进行合并开收据!");
                }
            }


            HlsCusAcrReceiptHd acrReceiptHd = new HlsCusAcrReceiptHd();
            acrReceiptHd.setCompanyId(iRequest.getCompanyId());
            acrReceiptHd.setReceiptDate(new Date());
            acrReceiptHd.setBpId(dto.get(0).getBpId());
            acrReceiptHd.setCurrency(dto.get(0).getCurrency());

            acrReceiptHd.setBusinessType("RECEIPT");
            acrReceiptHd.setDocumentType("RECEIPT");
            acrReceiptHd.setDocumentCategory("AR_RECEIPT");
            acrReceiptHd.setReceiptType(dto.get(0).getReceiptType());
            Map<String, String> params = new HashMap<String, String>();
            acrReceiptHd.setDocumentNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, acrReceiptHd.getDocumentCategory(), acrReceiptHd.getDocumentType(), acrReceiptHd.getBusinessType(), params));
            acrReceiptHd.setReceiptStatus("NEW");

            double totalAmount = dto.stream().mapToDouble(HlsCusAcrReceiptHd::getAmount).sum();
            acrReceiptHd.setTotalAmount(totalAmount);

            self().insert(iRequest, acrReceiptHd);

            List<HlsCusAcrReceiptLn> acrReceiptLnList = new ArrayList<>();
            dto.forEach(item -> {
                HlsCusAcrReceiptLn acrReceiptLn = new HlsCusAcrReceiptLn();
                acrReceiptLn.set__status(DTOStatus.ADD);
                acrReceiptLn.setReceiptHdId(acrReceiptHd.getReceiptHdId());
                acrReceiptLn.setSourceType(LN_SOURCE_TYPE);
                acrReceiptLn.setSourceId(item.getSourceId());
                acrReceiptLn.setAmount(item.getAmount());
                acrReceiptLnList.add(acrReceiptLn);
            });
            acrReceiptLnService.batchUpdate(iRequest, acrReceiptLnList);
        }

    }

    @Override
    public FndAttachment receiptSaveDocAndDownload(List<HlsCusAcrReceiptHd> hlsCusAcrInvoiceHdList, IRequest requestCtx) {
        String templateCode = new String("");
        String receiptType = hlsCusAcrInvoiceHdList.get(0).getReceiptType();
        if ("LEASEBACK_PRINCIPAL".equals(receiptType)) {
            templateCode = "RECEIPT_LEASEBACK_PRINCIPAL";
        } else if ("DEPOSIT".equals(receiptType)) {
            templateCode = "RECEIPT_DEPOSIT";
        }
        HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
        hlsDocFileTemplet.setTempletCode(templateCode);
        List<HlsDocFileTemplet> hlsDocFileTempletList = hlsDocFileTempletService.selectSelective(requestCtx, hlsDocFileTemplet);
        FndAttachment fndAttachment = new FndAttachment();


        if (!hlsCusAcrInvoiceHdList.isEmpty()) {
            ReceiptAttachment receiptAttachment = new ReceiptAttachment();
            receiptAttachment.setReceiptHdId(hlsCusAcrInvoiceHdList.get(0).getReceiptHdId());
            receiptAttachment.setReceiptAttachmentCategory(receiptType);
            receiptAttachment.setTemplateId(hlsDocFileTempletList.get(0).getTempletId());
            receiptAttachment = receiptAttachmentService.insertSelective(requestCtx, receiptAttachment);

            Long templateId = receiptAttachment.getTemplateId();
            Long receiptAttachmentId = receiptAttachment.getReceiptAttachmentId();

            Map<String, Object> params = new HashMap<>();
            params.put("templetId", templateId);
            params.put("receiptHdId", hlsCusAcrInvoiceHdList.get(0).getReceiptHdId());
            params.put("receiptAttachmentId", receiptAttachmentId);
            try {
                fndAttachment = self().docxCreateMethod(requestCtx, params);
                HlsCusAcrReceiptHd hlsCusAcrReceiptHd = new HlsCusAcrReceiptHd();
                hlsCusAcrReceiptHd.setReceiptHdId(hlsCusAcrInvoiceHdList.get(0).getReceiptHdId());
                hlsCusAcrReceiptHd.setReceiptStatus("PRINT");
                this.updateByPrimaryKeySelective(requestCtx, hlsCusAcrReceiptHd);
            } catch (Exception e) {
                throw new RuntimeException("生成合同文本失败", e);
            }
        }

        return fndAttachment;
    }

    @Override
    public FndAttachment docxCreateMethod(IRequest requestContext, Map<String, Object> params) throws Exception {
        Long templetId = Long.parseLong(params.get("templetId").toString());
        Long receiptAttachmentId = Long.parseLong(params.get("receiptAttachmentId").toString());

        String SourceType = "hls_doc_file_templet";
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
            condition.setTablePkValue(receiptAttachmentId.toString());
            condition.setTableName(ACR_RECEIPT_ATTACHMENT);
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
                conDocFileMulti.setTableName(ACR_RECEIPT_ATTACHMENT);
                conDocFileMulti.setTablePkValue(receiptAttachmentId.toString());
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
            return conDocFile;
        }
        return new FndAttachment();
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