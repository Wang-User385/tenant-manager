package com.hand.hls.prj.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import com.hand.hls.office.dto.FndAtmAttachmentMultiDto;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.office.mapper.FndAtmAttachmentMultiMapper;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.prj.utils.CommonException;
import com.hand.hls.sign.dto.SignContract;
import com.hand.hls.sign.dto.SignParty;
import com.hand.hls.sign.dto.SignRecord;
import com.hand.hls.sign.mapper.SignPartyMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.util.Asserts;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author shigure 2022/11/29 17:29
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SignPartyServiceImpl extends BaseServiceImpl<SignParty> implements ISignPartyService {

    @Autowired
    private HlsCusBpMasterMapper bpMasterMapper;

    @Autowired
    private SignPartyMapper signPartyMapper;

    @Autowired
    private SignService signService;

    @Autowired
    private ISignRecordService signRecordService;

    @Autowired
    private ISignContractService signContractService;

    @Autowired
    private HlsCusPrjProjectAttachmentMapper projectAttachmentMapper;

    @Autowired
    private FndAtmAttachmentMapper attachmentMapper;

    @Autowired
    private FndAtmAttachmentMultiMapper atmAttachmentMultiMapper;

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Value("${file.upload.dir:.}")
    private String savePath = ".";

    private final ThreadLocal<SimpleDateFormat> sdf  =
            ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd"));

    @Override
    public void submitSignScene(IRequest request, Long projectId, String projectAttachmentCategory, String manufacturerCode) {
        // 签约合同文本附件
        List<HlsCusPrjProjectAttachment> attachments = projectAttachmentMapper.selectProjectDocxNew(projectAttachmentCategory, projectId, manufacturerCode);
        if(CollectionUtils.isEmpty(attachments)){
            log.warn("未找到签约合同文本附件!");
            return;
        }
        HlsCusPrjProjectAttachment attachment = attachments.get(0);
//        for(HlsCusPrjProjectAttachment attachment : attachments){
            Long projectAttachmentId = attachment.getProjectAttachmentId();
            List<SignParty> signParties = signPartyMapper.queryToSign(projectAttachmentId);
            for(SignParty signParty:signParties){
                if(!"NEW".equals(signParty.getSignStatus())){
                    throw new CommonException("只有新建状态可以发起签约!");
                }
            }
            // 校验
            if(CollectionUtils.isEmpty(signParties) || signParties.size()>1){
                throw new CommonException("签约方信息维护异常!");
            }
            SignParty party = signParties.get(0);
            // 发起签约
            this.signScene(request,party);
//        }
    }

    @Override
    public void signScene(IRequest iRequest, SignParty signParty) {
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusPrjProjectAttachment attachment = projectAttachmentMapper.selectProjectDocxById(signParty.getProjectAttachmentId());
        HlsCusBpMaster bpMaster = bpMasterMapper.selectByPrimaryKey(signParty.getBpId());
        // 企业场景和个人场景 request主体一样
        SignRequestDto request = buildSignRequest(bpMaster);
        // versionPerson需要注意，法人的传值是法定代表人的信息
        SignPersonDto verifyPerson = buildSignPerson(signParty.getSealPerson(),signParty.getIdCardNo(),signParty.getPhone());
        request.setVerifyPerson(verifyPerson);
        // signContract有区别，location有区别
        List<SignContractDto> contracts = buildSignContracts(bpMaster,signParty,attachment);
        request.setSignatureContracts(contracts);
        String url = signService.sign(request);
        // 生成签约记录
        SignRecord signRecord = new SignRecord();
        signRecord.setSignUniqueId(request.getUniqueIdentification());
        signRecord.setSignStatus("SIGNING");
        signRecord.setSignUrl(url);
        signRecord.setVerifyBpId(bpMaster.getBpId());
        signRecord.setSourceDocCategory("PRJ_SIGN_PARTY");
        signRecord.setSourceDocId(signParty.getObjectId());
        signRecord.setSignType(signParty.getSignType());
        signRecordService.insertSelective(iRequest, signRecord);

        // 生成合同记录
        for (SignContractDto contract : contracts) {
            SignContract signContract = new SignContract();
            BeanUtils.copyProperties(contract, signContract);
            signContract.setSignId(signRecord.getSignId());
            signContract.setContractStatus(DEFAULT_STATUS);
            signContractService.insertSelective(iRequest, signContract);
        }

        // 更改状态
        signParty = signPartyMapper.selectByPrimaryKey(signParty);
        signParty.setSignStatus("SIGNING");
        signPartyMapper.updateByPrimaryKeySelective(signParty);
    }

    @Override
    public void submitSignLongTerm(IRequest request, SignParty signParty) {
        RequestHelper.setCurrentRequest(request);
        // copy出一个新的签约方，用作长期签约，keyword不确定，估计还是得手输
        SignParty longTermParty = new SignParty();
        BeanUtils.copyProperties(signParty,longTermParty);
        longTermParty.setSignType("long_term");
        longTermParty.setSignStatus("NEW");
        longTermParty.setParentId(signParty.getObjectId());
        longTermParty.setKeyword("广东粤海融资租赁有限公司");
        signPartyMapper.insertSelective(longTermParty);
        this.signLongTerm(request,longTermParty);
    }

    @Override
    public void signLongTerm(IRequest iRequest, SignParty signParty) {
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusPrjProjectAttachment attachment = projectAttachmentMapper.selectByPrimaryKey(signParty.getProjectAttachmentId());
        SignRequestDto request = new SignRequestDto();
        HlsCusBpMaster bpMaster = bpMasterMapper.selectByPrimaryKey(signParty.getBpId());
        Long partyParentId = signParty.getParentId();

        // 寻找已完成的签约文件
        FndAtmAttachmentMultiDto multiDto = new FndAtmAttachmentMultiDto();
        multiDto.setTableName("PRJ_SIGN_PARTY");
        multiDto.setTablePkValue(partyParentId);
        List<FndAtmAttachmentMultiDto> multiDtos = atmAttachmentMultiMapper.select(multiDto);
        if(CollectionUtils.isEmpty(multiDtos)){
            log.error("未找到已签约文件!");
            return;
        }
        FndAtmAttachmentDto fndAtmAttachmentDto = attachmentMapper.selectByCodeAndPkValue("fnd_atm_attachment_multi", String.valueOf(multiDtos.get(0).getRecordId()));
        attachment.setFilePath(fndAtmAttachmentDto.getFilePath());
        attachment.setFileTypeCode(fndAtmAttachmentDto.getFileTypeCode());
        List<SignContractDto> contracts = buildSignContracts(bpMaster,signParty,attachment);
        buildLongTermContracts(contracts);
        request.setSignatureContracts(contracts);
        SignRequestDto signRequestDto = signService.signCompanyLongTerm(request);
        String uniqueIdentification = signRequestDto.getUniqueIdentification();
        List<SignContractDto> signatureContracts = signRequestDto.getSignatureContracts();
        for (SignContractDto contractDto : signatureContracts) {
            byte[] decode = Base64.getDecoder().decode(contractDto.getSignedPDF());
            String fileName = contractDto.getContractCode() + ".pdf";
            String filePath = this.savePath + File.separator + UUID.randomUUID() +"@"+fileName;
            File file = new File(filePath);
            try(FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decode);
                fos.flush();
                fndAttachmentService.uploadAttachment(URLDecoder.decode(file.getName(),
                        "UTF-8"), file.getPath(), "PRJ_SIGN_PARTY", String.valueOf(signParty.getObjectId()),
                        file.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拼接签约请求体
     */
    private SignRequestDto buildSignRequest(HlsCusBpMaster bpMaster){
        String uniqueIdentification = SignService.generateSignUniqueId(bpMaster.getBpId());
        String businessScene = SignService.generateSignBusinessScene(bpMaster.getBpName());
        String orderNum = SignService.generateSignOrderNum(bpMaster.getBpCode());
        SignRequestDto request = new SignRequestDto();
        request.setUniqueIdentification(uniqueIdentification);
        request.setBusinessType(SignService.BUSINESS_TYPE);
        request.setBusinessScene(businessScene);
        request.setSignatureMethod(SIGNATURE_METHOD);
        request.setVerifyCount(VERIFY_COUNT);
        request.setSignerVerifyFlag(SIGNER_VERIFY_FLAG);
        request.setOrderNum(orderNum);
        return request;
    }

    private SignPersonDto buildSignPerson(String personName,String idCardNo,String phone) {
        Asserts.notBlank(idCardNo,"签约人身份证号码");
        Asserts.notBlank(phone,"签约人手机号");
        SignPersonDto signPersonDto = new SignPersonDto();
        signPersonDto.setPersonName(personName);
        signPersonDto.setElementType(SignService.ELEMENT_TYPE);
        signPersonDto.setDocumentType(DOCUMENT_TYPE);
        signPersonDto.setIdNumber(idCardNo);
        signPersonDto.setPhoneNumber(phone);
        signPersonDto.setReceivePhoneNumber(phone);
        signPersonDto.setCodeFlag(SignService.CODE_FLAG);
        signPersonDto.setFaceFlag(SignService.FACE_FLAG);
        signPersonDto.setFaceCount(SignService.FACE_COUNT);
        signPersonDto.setElementVerifyCount(SignService.ELEMENT_VERIFY_COUNT);
        return signPersonDto;
    }

    private SignKeywordLocationDto buildKeywordLocation(String keyword, String wordType){
        SignKeywordLocationDto location = new SignKeywordLocationDto();
        location.setPageNo("");
        location.setKeyword(keyword);
        location.setKeywordPositionIndex(KEYWORD_POSITION_INDEX);
        location.setOffsetX(OFFSET_X);
        location.setOffsetY(OFFSET_Y);
        location.setWordType(wordType);
        return location;
    }

    private List<SignContractDto> buildSignContracts(HlsCusBpMaster bpMaster, SignParty signParty, HlsCusPrjProjectAttachment attachment) {
        List<SignContractDto> contracts = new ArrayList<>();
        SignContractDto signContractDto = new SignContractDto();
        String creationDate = sdf.get().format(new Date());
        try {
            signContractDto.setSerialNumber(1);
            signContractDto.setContractCode(attachment.getDocumentName());
            signContractDto.setContractName(attachment.getDocumentName());
            signContractDto.setSignatureSubject(bpMaster.getBpName());
            signContractDto.setSealPerson(signParty.getSealPerson());
            signContractDto.setSealReason(DEFAULT_REASON);
            signContractDto.setContractSignType(signParty.getSignType());
            signContractDto.setContractCreationDate(creationDate);
            if("pdf".equals(attachment.getFileTypeCode())){
                signContractDto.setContractPDF(signService.encodeBase64FromFileSystem(attachment.getFilePath()));
            }else if (attachment.getFileTypeCode().contains("doc")){
                signContractDto.setContractPDF(signService.wordToPdf(attachment.getFilePath()));
            } else{
                log.error("未识别的签约文件类型");
                return contracts;
            }
            String wordType = "legal";
            if("ORG".equals(bpMaster.getBpClass())){
                // 企业场景签约信息
                signContractDto.setCompanyName(bpMaster.getBpName());
                signContractDto.setCompanyCode(bpMaster.getRegisterCertNum());
                wordType = "company";
            }
            signContractDto.setKeywordLocationList(Collections.singletonList(buildKeywordLocation(signParty.getKeyword(), wordType)));
            contracts.add(signContractDto);
            return contracts;
        }finally {
            sdf.remove();
        }
    }

    /**
     * 填充长期签章信息
     */
    private List<SignContractDto> buildLongTermContracts(List<SignContractDto> contracts) {
        // todo 长期签章证书需要替换
        contracts.forEach(signContractDto -> signContractDto.setDigitalCertificateCode("SEAL202208300001"));
        return contracts;
    }

}
