package com.hand.hls.prj.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.interfacePlatform.utils.InterfacePlatformUtils;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.service.*;
import com.hand.hls.prj.utils.CommonException;
import com.hand.hls.sign.dto.SignContract;
import com.hand.hls.sign.dto.SignRecord;
import com.hand.hls.sign.dto.SignVerify;
import com.hand.hls.sign.mapper.SignContractMapper;
import com.hand.hls.sign.mapper.SignRecordMapper;
import com.hand.hls.sign.mapper.SignVerifyMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 法人签约相关实现
 *
 * @author shigure 2022/11/24 9:55
 */
@EnableAsync
@Slf4j
@Service("orgTenantSignServiceImpl")
public class OrgTenantSignServiceImpl implements BpSignService {
    public static final String CONTRACT_SIGN_TYPE = "company_scene";

    public static final String WORD_TYPE = "company";

    /**
     * 企业长期证书
     */
    public static final String DIGITAL_CERTIFICATE_CODE = "SEAL202208300001";


    private final ThreadLocal<SimpleDateFormat> sdf  =
            ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd"));

    @Value("${file.upload.dir:.}")
    private String savePath = ".";

    @Autowired
    private SignService signService;

    @Autowired
    private ISignRecordService signRecordService;

    @Autowired
    private ISignContractService signContractService;

    @Autowired
    private SignContractMapper signContractMapper;

    @Autowired
    private SignRecordMapper signRecordMapper;

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private ISignVerifyService signVerifyService;

    @Autowired
    private SignVerifyMapper signVerifyMapper;

    @Autowired
    private InterfacePlatformUtils platformUtils;


    @Override
    public List<SignVerify> verify(IRequest requestCtx, SignVerify dto) {
        dto = signVerifyService.selectByPrimaryKey(requestCtx, dto);
        String uniqueId = SignService.generateVerifyUniqueId(dto.getBpId());
        String verifyResult = dto.getVerifyResult();
        if(VERIFYING_STATUS.equals(verifyResult)){
            throw new CommonException("正在认证中!");
        }
        dto.setUniqueId(uniqueId);
        SignatureVerifyDTO signatureVerifyDTO = new SignatureVerifyDTO();
        BeanUtils.copyProperties(dto, signatureVerifyDTO);
        signatureVerifyDTO.setCodeFlag(SignService.CODE_FLAG);
        signatureVerifyDTO.setFaceFlag(SignService.FACE_FLAG);
        signatureVerifyDTO.setDigitalCertificateFlag(SignService.DIGITAL_CERTIFICATE_FLAG);
        signatureVerifyDTO.setElementType(SignService.ELEMENT_TYPE);
        signatureVerifyDTO.setElementVerifyCount(String.valueOf(SignService.ELEMENT_VERIFY_COUNT));
        signatureVerifyDTO.setFaceCount(String.valueOf(SignService.FACE_COUNT));
        signatureVerifyDTO.setCompanyVerifyCount(String.valueOf(SignService.COMPANY_VERIFY_COUNT));
        signatureVerifyDTO.setCompanyElementVerifyCount(String.valueOf(SignService.ELEMENT_VERIFY_COUNT));
        signatureVerifyDTO.setLegalCertificateFlag(SignService.LEGAL_CERTIFICATE_FLAG);
        String jsonString = JSON.toJSONString(signatureVerifyDTO);
        JSONObject requestJson = JSON.parseObject(jsonString);
        JSONObject result = platformUtils.getInterfaceRequest(requestJson,
                SignService.COMPANY_VERIFY_INTERFACE + "?access_token=",
                "企业认证信息下发");
        log.info("企业认证信息下发结果：{}", result);
        String returnStatus = result.getString("returnStatus");
        if("S".equals(returnStatus)){
            dto.setVerifyUrl(result.getString("returnData"));
            dto.setVerifyResult(VERIFYING_STATUS);
            signVerifyService.updateByPrimaryKeySelective(requestCtx, dto);
        }else{
            throw new CommonException("发起认证失败!");
        }
        return Collections.singletonList(dto);
    }

    @Override
    public JSONObject verifyFinish(IRequest requestCtx, CompanyAuthMessageDTO messageDTO) {
        String uniqueIdentification = messageDTO.getUniqueIdentification();
        SignVerify verify = new SignVerify();
        verify.setUniqueId(uniqueIdentification);
        SignVerify signVerify = signVerifyMapper.selectOne(verify);
        if(signVerify==null){
            return SignService.generateMessage("verify not found");
        }
        signVerify.setVerifyResult(messageDTO.getResultStatus());
        if(SUCCESS_STATUS.equals(messageDTO.getResultStatus())){
            signVerify.setOpenTime(new Date());
        }
        signVerify.setLegalSealCode(messageDTO.getLegalSealCode());
        signVerify.setSealCode(messageDTO.getSealCode());
        signVerifyMapper.updateByPrimaryKeySelective(signVerify);
        return SignService.generateMessage("success");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void sign(HlsCusBpMaster bpMaster, Long sourceDocId, String sourceDocCategory, IRequest iRequest, String signObject) {
        RequestHelper.setCurrentRequest(iRequest);
        // 签约人实名认证校验
        SignVerify signVerify = signVerifyMapper.queryVerifiedBp(bpMaster.getBpId());
        if(signVerify==null){
            throw new CommonException("签约人未完成或未启用实名认证!");
        }

        // 承租人签约校验
        SignRecord signRecord = new SignRecord();
        signRecord.setSourceDocCategory(sourceDocCategory);
        signRecord.setSourceDocId(sourceDocId);
        signRecord.setSignObject(SignService.TENANT_SIGN_OBJECT);
        signRecord.setSignStatus(SignService.SIGNED_STATUS);
        List<SignRecord> signRecords = signRecordService.selectSelective(iRequest, signRecord);
        if(CollectionUtils.isEmpty(signRecords)){
            throw new CommonException("承租人未完成签约!");
        }

        // 承租人签约附件获取
        signRecords = signRecords.stream().sorted(Comparator.comparing(SignRecord::getSignDate).reversed()).collect(Collectors.toList());
        signRecord = signRecords.get(0);
        SignContract signContract = new SignContract();
        signContract.setSignId(signRecord.getSignId());
        signContract = signContractMapper.selectOne(signContract);
        Long signedAttachmentId = signContract.getSignedAttachmentId();
        FndAttachment fndAttachment = new FndAttachment();
        fndAttachment.setAttachmentId(signedAttachmentId);
        fndAttachment = fndAttachmentService.selectByPrimaryKey(iRequest, fndAttachment);
        String filePath = fndAttachment.getFilePath();
        String contractPdf = null;
        if(StringUtils.isNotBlank(filePath)){
            contractPdf  = signService.encodeBase64FromFileSystem(filePath);
        }

        SignRequestDto request = buildSignRequest(bpMaster);
        SignPersonDto verifyPerson = buildSignPerson(bpMaster);
        // 法人要取legalPerson
        verifyPerson.setPersonName(bpMaster.getLegalPerson());
        request.setVerifyPerson(verifyPerson);
        String keyword = buildSignKeyword(signObject);
        List<SignContractDto> contracts = buildSignContracts(bpMaster, keyword,contractPdf);
        request.setSignatureContracts(contracts);
        // 法人已完成实名认证，不需要再认证了!
        request.setSignerVerifyFlag("N");
        String url = signService.sign(request);
        // 生成签约记录
        signRecord = new SignRecord();
        signRecord.setSignUniqueId(request.getUniqueIdentification());
        signRecord.setSignStatus(SignService.SIGNING_STATUS);
        signRecord.setSignUrl(url);
        signRecord.setVerifyBpId(bpMaster.getBpId());
        signRecord.setSourceDocCategory(sourceDocCategory);
        signRecord.setSourceDocId(sourceDocId);
        signRecord.setSignObject(signObject);
        signRecordService.insertSelective(iRequest, signRecord);

        // 生成合同记录
        for (SignContractDto contract : contracts) {
            signContract = new SignContract();
            BeanUtils.copyProperties(contract, signContract);
            signContract.setSignId(signRecord.getSignId());
            signContract.setContractStatus(DEFAULT_STATUS);
            signContractService.insertSelective(iRequest, signContract);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void longTermSign(HlsCusBpMaster bpMaster, Long sourceDocId, String sourceDocCategory, IRequest iRequest, String keyword) {
        RequestHelper.setCurrentRequest(iRequest);
        SignRequestDto request = new SignRequestDto();
        List<SignContractDto> contracts = buildLongTermContracts(bpMaster, keyword);
        request.setSignatureContracts(contracts);
        SignRequestDto signRequestDto = signService.signCompanyLongTerm(request);
        String uniqueIdentification = signRequestDto.getUniqueIdentification();
//        SignRecord signRecord = new SignRecord();
//        signRecord.setSignUniqueId(uniqueIdentification);
//        signRecord = signRecordMapper.selectOne(signRecord);
//        if (signRecord == null) {
//            return;
//        }
        List<SignContractDto> signatureContracts = signRequestDto.getSignatureContracts();
        for (SignContractDto contractDto : signatureContracts) {
//            Integer serialNumber = contractDto.getSerialNumber();
//            SignContract signContract = new SignContract();
//            signContract.setSerialNumber(serialNumber);
//            signContract.setSignId(signRecord.getSignId());
//            signContract = signContractMapper.selectOne(signContract);
//            if (signContract == null) {
//                continue;
//            }
            byte[] decode = Base64.getDecoder().decode(contractDto.getSignedPDF());
            try {
                String fileName = contractDto.getContractCode() + ".pdf";
                String filePath = this.savePath + File.separator + UUID.randomUUID() +"@"+fileName;
                File file = new File(filePath);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(decode);
                fos.flush();
                fos.close();
//                Long attachmentId = fndAttachmentService.uploadAttachment(URLDecoder.decode(file.getName(), "UTF-8"), file.getPath(), FILE_SOURCE_TYPE, String.valueOf(signContract.getContractId()), file.length());
//                logger.info("file {} has been written.", fileName);
//                signContract.setSignedAttachmentId3(attachmentId);
//                signContractMapper.updateByPrimaryKeySelective(signContract);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        if(projectId!=null){
//            generateProjectAttachment(projectId);
//        }
    }

    private List<SignContractDto> buildSignContracts(HlsCusBpMaster bpMaster, String keyword,String contractPdf) {
        List<SignContractDto> contracts = new ArrayList<>();
        SignContractDto signContractDto = new SignContractDto();
        String creationDate = sdf.get().format(new Date());

        //todo 测试文件，现在都没有具体附件信息
        signContractDto.setSerialNumber(1);
        signContractDto.setContractCode("TEST-HLS-" + creationDate + System.currentTimeMillis());
        signContractDto.setContractName("测试合同电签");
        signContractDto.setSignatureSubject(bpMaster.getBpName());
        Asserts.notBlank(bpMaster.getLegalPerson(),"法人名称");
        signContractDto.setSealPerson(bpMaster.getLegalPerson());
        signContractDto.setSealReason(DEFAULT_REASON);
        signContractDto.setContractSignType(CONTRACT_SIGN_TYPE);
        signContractDto.setContractCreationDate(creationDate);
        signContractDto.setCompanyName(bpMaster.getBpName());
        Asserts.notBlank(bpMaster.getRegisterCertNum(),"登记注册证号码");
        signContractDto.setCompanyCode(bpMaster.getRegisterCertNum());
        //等合同文本修改完毕后需要还原
//        signContractDto.setContractPDF(signService.encodeBase64FromFileSystem("测试合同.pdf"));

        String filePath = savePath + DEFAULT_FILE;
        signContractDto.setContractPDF(StringUtils.isBlank(contractPdf)?signService.encodeBase64FromFileSystem(filePath):contractPdf);
        signContractDto.setKeywordLocationList(Collections.singletonList(buildKeywordLocation(keyword, WORD_TYPE)));
        contracts.add(signContractDto);
        return contracts;
    }

    private List<SignContractDto> buildLongTermContracts(HlsCusBpMaster bpMaster, String keyword) {
        List<SignContractDto> contracts = buildSignContracts(bpMaster, keyword,null);
        contracts.forEach(signContractDto -> signContractDto.setDigitalCertificateCode(DIGITAL_CERTIFICATE_CODE));
        return contracts;
    }

}
