package com.hand.hls.fin.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.*;
import com.hand.hls.fin.mapper.HlsCusLonConRepaymentBatchMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchLnService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.gld.service.impl.HlsCusLonContractRepaymentMergeServiceImpl;
import com.hand.hls.utils.HlsCusDownloadDocxUtil;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.apache.commons.lang.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonConRepaymentBatchServiceImpl extends BaseServiceImpl<HlsCusLonConRepaymentBatch> implements IHlsCusLonConRepaymentBatchService {

    @Autowired
    private HlsCusLonConRepaymentBatchMapper hlsCusLonConRepaymentBatchMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    HlsDocFileTempletMapper hlsDocFileTempletMapper;

    @Autowired
    FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private HlsCusLonContractMapper hlsCusLonContractMapper;
    @Autowired
    private IHlsCusLonConRepaymentBatchLnService hlsCusLonConRepaymentBatchLnService;
    @Autowired
    private HlsCusLonContractRepaymentService hlsCusLonContractRepaymentService;
    @Autowired
    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;
    @Autowired
    private HlsCusLonContractRepaymentMergeServiceImpl lonContractRepaymentMergeServiceImpl;


    /**
     * 模板表
     */
    private static final String FILE_TEMPLET_TABLE = "hls_doc_file_templet";

    /**
     * fnd_atm_attachment的sourceType属性
     */
    private static final String SOURCE_TYPE_CODE_ATTACHMENT = "fnd_atm_attachment_multi";

    private static final String FUND_DOCUMENT_TYPE_RONGZI = "RONGZI";
    private static final String FUND_DOCUMENT_TYPE_RONGCHU= "RONGCHU";
    private static final String FUND_DOCUMENT_TYPE_TONGHUMING = "TONGHUMING";

    @Override
    public HlsCusLonConRepaymentBatch lonConRepaymentBatchSubmitWfl(IRequest iRequest, HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch) {
        hlsCusLonConRepaymentBatch = hlsCusLonConRepaymentBatchMapper.selectByPrimaryKey(hlsCusLonConRepaymentBatch);
        List<HlsCusLonConRepaymentBatch> hlsCusLonConRepaymentBatchList = new ArrayList<>();
        hlsCusLonConRepaymentBatchList.add(hlsCusLonConRepaymentBatch);
        if(hlsCusLonConRepaymentBatch != null && hlsCusLonConRepaymentBatch.getBatchId() != null){
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", "LON_CON_PAYMENT_BATCH_WFL");
            activitiStartService.start(iRequest, hlsCusLonConRepaymentBatchList, params);

            //修改项目状态为审批中
            hlsCusLonConRepaymentBatch = self().selectByPrimaryKey(iRequest, hlsCusLonConRepaymentBatch);
            hlsCusLonConRepaymentBatch.setBatchStatus("APPROVING");
            hlsCusLonConRepaymentBatch = self().updateByPrimaryKeySelective(iRequest, hlsCusLonConRepaymentBatch);

        }
        return hlsCusLonConRepaymentBatch;
    }

    @Override
    public List<FndAttachmentMulti> contextCreateMultiple(IRequest request, String code, String batchId, HttpServletResponse response) throws Exception {
        HlsDocFileTemplet queryTemplet = new HlsDocFileTemplet();
        queryTemplet.setTempletCode(code);
        HlsDocFileTemplet hlsDocFileTemplet = hlsDocFileTempletMapper.selectOne(queryTemplet);
        Assert.notNull(hlsDocFileTemplet, "未找到模板代码为" + code + "的模板文件");
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTableName(FILE_TEMPLET_TABLE);
        fndAttachmentMulti.setTablePkValue(hlsDocFileTemplet.getTempletId().toString());
        fndAttachmentMulti = fndAttachmentMultiMapper.selectOne(fndAttachmentMulti);
        FndAttachment sysFile = new FndAttachment();
        sysFile.setSourceTypeCode(SOURCE_TYPE_CODE_ATTACHMENT);
        sysFile.setSourcePkValue(fndAttachmentMulti.getRecordId().toString());
        sysFile = fndAttachmentMapper.selectOne(sysFile);
        int fileBackLength = 0;
        if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
            File file = new File(sysFile.getFilePath());
            if (file.exists()) {
                //先获取模板文件的大小
                int fileLength = (int) file.length();
                if (fileLength > 0) {
                    InputStream inStream = new FileInputStream(file);
                    //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
                    String copyPath = file.getPath().concat("_back_").concat(UUID.randomUUID().toString());
                    //复制模板
                    HlsCusDownloadDocxUtil.copyModel(copyPath, inStream);
                    //用输入流读取复制后的模板
                    InputStream modelIs = new FileInputStream(copyPath);
                    Map<String, Object> map = new HashMap<>();
                    map.put("templetId", hlsDocFileTemplet.getTempletId());
                    map.put("batchId", batchId);
                    //通过输入流构建WordprocessingMLPackage对象
                    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(modelIs);
                    //将构建的wordMLPackage对象传入方法中
                    wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, request, map);
                    //将替换后的合同文本保存到服务器上作为备份
                    wordMLPackage.save(new File(copyPath));

                    fileBackLength = (int) new File(copyPath).length();

                    //1.保存文件
                    contextCreateMultipleSave(request,code,batchId,sysFile,copyPath,fileBackLength,response);

                    //2.返回结果
                    FndAttachmentMulti fndAttachmentMultiNew = new FndAttachmentMulti();
                    fndAttachmentMultiNew.setTablePkValue(batchId);
                    fndAttachmentMultiNew.setTableName(code);
                    List<FndAttachmentMulti> fndAttachmentMultiList = new ArrayList<>();
                    fndAttachmentMultiList = fndAttachmentMultiService.selectSelective(request, fndAttachmentMultiNew);

                    return fndAttachmentMultiList;

                }
            }else{
                throw new HlsCusException("文件模版不存在");
            }
        }
        return null;
    }

    @Override
    public void contextCreateMultipleSave(IRequest request, String code, String paymentId,FndAttachment sysFile,String copyPath,int fileLength, HttpServletResponse response) throws Exception {
        //查询是否存在过
        FndAttachmentMulti condition = new FndAttachmentMulti();
        condition.setTablePkValue(paymentId);
        condition.setTableName(code);
        List<FndAttachmentMulti> list = fndAttachmentMultiService.selectSelective(request, condition);

        //保存生成文件
        FndAttachment conDocFile = null;
        FndAttachmentMulti conDocFileMul = null;
        //已经生成过直接修改路径后保存
        if (!list.isEmpty()) {
            conDocFileMul = list.get(0);
            FndAttachment fndCondition = new FndAttachment();
            fndCondition.setSourceTypeCode("fnd_atm_attachment_multi");
            fndCondition.setSourcePkValue(conDocFileMul.getRecordId().toString());
            conDocFile = fndAttachmentService.selectSelective(request, fndCondition).get(0);
            //上传
            conDocFile.setFilePath(copyPath);
            conDocFile.setFileSize(((Integer) fileLength).longValue());
            fndAttachmentService.updateByPrimaryKeySelective(request, conDocFile);
        }else{
            conDocFile = new FndAttachment();
            String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
            conDocFile.setFileName(fileName);
            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
            conDocFileMulti.setTableName(code);
            conDocFileMulti.setTablePkValue(paymentId);
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            conDocFileMulti.setCreatedBy(request.getUserId());
            conDocFileMulti.setCreationDate(new Date());
            conDocFileMulti.setLastUpdateDate(new Date());
            conDocFileMulti.setLastUpdatedBy(request.getUserId());
            fndAttachmentMultiService.insertSelective(request, conDocFileMulti);
            conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
            conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
            //上传
            conDocFile.setFilePath(copyPath);
            conDocFile.setFileTypeCode(".docx");
            conDocFile.setFileSize(((Integer) fileLength).longValue());
            conDocFile.setCreationDate(new Date());
            conDocFile.setCreatedBy(request.getUserId());
            conDocFile.setLastUpdateDate(new Date());
            conDocFile.setLastUpdatedBy(request.getUserId());
            fndAttachmentService.insertSelective(request, conDocFile);
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            fndAttachmentMultiService.updateByPrimaryKey(request, conDocFileMulti);
        }
    }


    @Override
    public void confirmBatchStatus(IRequest request , List<HlsCusLonConRepaymentBatch> hlsCusLonConRepaymentBatchList , final HttpSession session){
        for(HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch : hlsCusLonConRepaymentBatchList){

            //校验
            /*List<Map> list = hlsCusLonConRepaymentBatchMapper.selectLonConRepaymentBatchDetail(hlsCusLonConRepaymentBatch);
            if(list.size()>0){
                BigDecimal repayment_amount_total = (BigDecimal)list.get(0).get("repayment_amount_total");
                double repaymentAmountTotal = (repayment_amount_total==null?0:repayment_amount_total.doubleValue());
                BigDecimal repayment_amount_back_total = (BigDecimal)list.get(0).get("repayment_amount_back_total");
                double repaymentAmountBackTotal = (repayment_amount_back_total==null?0:repayment_amount_back_total.doubleValue());
                if(CalculateUtil.sub(repaymentAmountTotal,repaymentAmountBackTotal) !=0){
                    throw new IllegalArgumentException("付款金额之和需等于支付总金额");
                }
            }*/
            hlsCusLonConRepaymentBatch.setBatchStatus("APPROVED");
            hlsCusLonConRepaymentBatch.setConfirmStatus("Y");
            hlsCusLonConRepaymentBatch.setConfirmPerson(String.valueOf(request.getUserId()));
            hlsCusLonConRepaymentBatch.setConfirmUnit(String.valueOf(session.getAttribute("unitId")));
            hlsCusLonConRepaymentBatch.setConfirmDate(new Date());
            hlsCusLonConRepaymentBatchMapper.updateByPrimaryKeySelective(hlsCusLonConRepaymentBatch);

            HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn = new HlsCusLonConRepaymentBatchLn();
            hlsCusLonConRepaymentBatchLn.setBatchId(hlsCusLonConRepaymentBatch.getBatchId());
            List<HlsCusLonConRepaymentBatchLn> hlsCusLonConRepaymentBatchLnList = hlsCusLonConRepaymentBatchLnService.select(request,hlsCusLonConRepaymentBatchLn,1,1000000);
            List<HlsCusLonContractRepayment> hlsCusLonContractRepaymentList = new ArrayList<>();
            List<HlsCusLonContractWithdraw> hlsCusLonContractWithdrawList = new ArrayList<>();
            for(HlsCusLonConRepaymentBatchLn dt:hlsCusLonConRepaymentBatchLnList){
                if(dt.getRepaymentId() != null){
                    HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
                    lonContractRepayment.setRepaymentId(dt.getRepaymentId());
                    lonContractRepayment = hlsCusLonContractRepaymentService.selectByPrimaryKey(request,lonContractRepayment);
                    //更改核销状态，核销金额
                    lonContractRepayment.setWriteOffFlag("FULL");
                    lonContractRepayment.setConfirmFlag("Y");
                    if(lonContractRepayment.getCfItem() == 302){
                        //如果是利息的话需要修改原现金流金额
                        lonContractRepayment.setPlannedDueAmount(dt.getPlannedDueAmount());
                    }
                    lonContractRepayment.setWriteOffAmount(lonContractRepayment.getPlannedDueAmount());
                    if (lonContractRepayment.getWriteOffAmount() != null && lonContractRepayment.getWriteOffAmount() != 0) {
                        BigDecimal amountNew = new BigDecimal(lonContractRepayment.getWriteOffAmount());
                        lonContractRepayment.setWriteOffAmount(amountNew.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    }
                    lonContractRepayment.setWriteOffDate(hlsCusLonConRepaymentBatch.getRepaymentDate());
                    hlsCusLonContractRepaymentService.updateByPrimaryKeySelective(request,lonContractRepayment);
                    if(lonContractRepayment != null){
                        hlsCusLonContractRepaymentList.add(lonContractRepayment);
                        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
                        hlsCusLonContractWithdraw.setWithdrawId(lonContractRepayment.getWithdrawId());
                        hlsCusLonContractWithdraw = hlsCusLonContractWithdrawService.selectByPrimaryKey(request,hlsCusLonContractWithdraw);
                        String existFlag = "N";
                        for(HlsCusLonContractWithdraw dt2:hlsCusLonContractWithdrawList){
                            if(dt2.getWithdrawId().equals(lonContractRepayment.getWithdrawId())){
                                existFlag = "Y";
                                break;
                            }
                        }
                        if(existFlag.equals("N")){
                            hlsCusLonContractWithdrawList.add(hlsCusLonContractWithdraw);
                        }
                    }

                }
            }
            for(HlsCusLonContractWithdraw dt:hlsCusLonContractWithdrawList){
                //更改融资提款头表上的核销金额， 只根据本金与应还金额比较
                Double totalAmount = dt.getWriteOffAmount();
                if(totalAmount == null){
                    totalAmount = 0.00D;
                }
                String writeOffFlag = "";
                for(HlsCusLonContractRepayment dt2:hlsCusLonContractRepaymentList){
                    if(dt.getWithdrawId().equals(dt2.getWithdrawId())){
                        if (dt2.getCfItem().longValue() == 301L) {
                            totalAmount = CalculateUtil.add(totalAmount, dt2.getWriteOffAmount());
                        }
                    }
                }

                dt.setWriteOffAmount(totalAmount);
                if(totalAmount == 0){
                    writeOffFlag = "NOT";
                }else if (dt.getDueAmount() > totalAmount) {
                    writeOffFlag = "PARTIAL";
                } else {
                    writeOffFlag = "FULL";
                }
                dt.setWriteOffFlag(writeOffFlag);
                hlsCusLonContractWithdrawService.updateByPrimaryKeySelective(request, dt);

                //重新分摊
                lonContractRepaymentMergeServiceImpl.calcLonConWithdrawFinCostNew(request,dt,"REPAYMENT");

            }


        }
    }



    @Override
    public void saveConfirmBatchStatus(IRequest request , List<HlsCusLonConRepaymentBatch> hlsCusLonConRepaymentBatchList , final HttpSession session){
        for(HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch : hlsCusLonConRepaymentBatchList){
            if(FUND_DOCUMENT_TYPE_RONGZI.equalsIgnoreCase(hlsCusLonConRepaymentBatch.getFundDocumentType())){
                String documentId = hlsCusLonConRepaymentBatch.getDocumentId();
                HlsCusLonConRepaymentBatch lonConRepaymentBatch = new HlsCusLonConRepaymentBatch();
                lonConRepaymentBatch.setBatchId(Long.valueOf(documentId));
                HlsCusLonConRepaymentBatch repaymentBatch = hlsCusLonConRepaymentBatchMapper.selectByPrimaryKey(lonConRepaymentBatch);

                repaymentBatch.setConfirmSeq(hlsCusLonConRepaymentBatch.getConfirmSeq());
                hlsCusLonConRepaymentBatchMapper.updateByPrimaryKeySelective(repaymentBatch);

            }else if (FUND_DOCUMENT_TYPE_RONGCHU.equalsIgnoreCase(hlsCusLonConRepaymentBatch.getFundDocumentType())){
                String documentId = hlsCusLonConRepaymentBatch.getDocumentId();
                HlsCusLonContract lonContract = new HlsCusLonContract();
                lonContract.setContractId(Long.valueOf(documentId));
                HlsCusLonContract hlsCusLonContract = hlsCusLonContractMapper.selectByPrimaryKey(lonContract);

//                hlsCusLonContract.setConfirmSeq(hlsCusLonConRepaymentBatch.getConfirmSeq());
                hlsCusLonContractMapper.updateByPrimaryKeySelective(hlsCusLonContract);

            }


        }
    }

}