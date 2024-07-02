package com.hand.hls.prj.service.impl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusBpAttachment;
import com.hand.hls.bp.mapper.HlsCusBpAttachmentMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.interfacePlatform.utils.InterfacePlatformUtils;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.prj.utils.CommonException;
import com.hand.hls.sign.dto.SignContract;
import com.hand.hls.sign.dto.SignParty;
import com.hand.hls.sign.dto.SignRecord;
import com.hand.hls.sign.dto.SignVerify;
import com.hand.hls.sign.mapper.SignContractMapper;
import com.hand.hls.sign.mapper.SignPartyMapper;
import com.hand.hls.sign.mapper.SignRecordMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author shigure 2022/11/22 10:42
 */

@EnableAsync
@Service
public class SignServiceImpl implements SignService {
    private final Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

    @Autowired
    private InterfacePlatformUtils platformUtils;

    @Autowired
    private SignRecordMapper signRecordMapper;

    @Autowired
    private SignContractMapper signContractMapper;

    @Autowired
    private ISignVerifyService signVerifyService;

    @Autowired
    private HlsCusBpAttachmentMapper bpAttachmentMapper;

    @Value("${file.upload.dir:.}")
    private String savePath = ".";

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private FndAtmAttachmentMapper attachmentMapper;

    @Autowired
    private HlsCusPrjProjectAttachmentService projectAttachmentService;

    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;

    @Autowired
    private HlsCusPrjProjectService prjProjectService;

    @Autowired
    private HlsCusPrjProjectAttachmentMapper prjProjectAttachmentMapper;

    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    private IFndAttachmentService iFndAttachmentService;

    @Autowired
    private IFndAttachmentMultiService iFndAttachmentMultiService;

    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HlsCusBpMasterMapper bpMasterMapper;

    @Autowired
    private SignPartyMapper signPartyMapper;


    @Override
    public List<Map> orgSignProjectQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        Long userId = iRequest.getUserId();
        User user = userMapper.selectByPrimaryKey(userId);
        String userName = user.getUserName();
        // admin可以查看到所有的签章记录
        if (!"admin".equals(userName) && (user.getBpId() == null || StringUtils.isEmpty(user.getBpCategory()))) {
            return Collections.emptyList();
        }
        project.put("bp_id", user.getBpId());
        project.put("bp_category", user.getBpCategory());
        project.put("user_name", userName);
        return signRecordMapper.orgSignProjectQuery(project);
    }

    @Override
    public String sign(SignRequestDto signRequestDto) {
        JSONObject result = null;
        String requestJsonString = JSON.toJSONString(signRequestDto);
        JSONObject requestJson = JSON.parseObject(requestJsonString);
        result = platformUtils.getInterfaceRequest(requestJson,
                SIGN_INTERFACE + "?access_token=",
                "签章信息下发");
        String returnStatus = result.getString("returnStatus");
        String returnMsg = result.getString("returnMsg");
        if (!SUCCESS_RETURN_STATUS.equals(returnStatus)) {
            throw new CommonException(returnMsg);
        }
        String signUrl = result.getString("returnData");
        return signUrl;
    }

    @Override
    public SignRequestDto signCompanyLongTerm(SignRequestDto signRequestDto) {
        String requestJsonString = JSON.toJSONString(signRequestDto);
        JSONObject requestJson = JSON.parseObject(requestJsonString);
        JSONObject result = platformUtils.getInterfaceRequest(requestJson,
                COMPANY_LONG_TERM_INTERFACE + "?access_token=",
                "企业长期签章");
        signRequestDto = result.toJavaObject(SignRequestDto.class);
        return signRequestDto;
    }

    @Override
    public void generateProjectAttachment(Long projectId) {
        IRequest currentRequest = RequestHelper.getCurrentRequest(true);
        SignRecord signRecord = new SignRecord();
        signRecord.setSourceDocId(projectId);
        signRecord.setSourceDocCategory(PROJECT_SOURCE_DOC_CATEGORY);
        signRecord = signRecordMapper.selectOne(signRecord);
        if (signRecord == null) {
            return;
        }

        // 查找项目附件
        HlsCusPrjProjectAttachment attachment = new HlsCusPrjProjectAttachment();
        attachment.setProjectId(projectId);
        attachment.setProjectAttachmentCategory(PROJECT_ATTACHMENT_CATEGORY);
        attachment = prjProjectAttachmentMapper.selectOne(attachment);
        if (attachment == null) {
            attachment = new HlsCusPrjProjectAttachment();
            attachment.setProjectId(projectId);
            attachment.setProjectAttachmentCategory(PROJECT_ATTACHMENT_CATEGORY);
            attachment.setDocumentName("电子签章文件");
            attachment.setTenantSignFlag("Y");
            attachment = projectAttachmentService.insertSelective(currentRequest, attachment);
        }

        // 录入签章附件
        SignContract contract = new SignContract();
        contract.setSignId(signRecord.getSignId());
        List<SignContract> signContracts = signContractMapper.select(contract);
        if (CollectionUtils.isEmpty(signContracts)) {
            return;
        }
        for (SignContract signContract : signContracts) {
            FndAttachmentMulti multiDto = new FndAttachmentMulti();
            multiDto.setAttachmentId(signContract.getSignedAttachmentId3());
            multiDto.setTableName(FILE_SOURCE_TYPE);
            multiDto.setTablePkValue(String.valueOf(signContract.getContractId()));
            FndAttachmentMulti multi = fndAttachmentMultiMapper.selectOne(multiDto);

            String recordId = String.valueOf(multi.getRecordId());
            multi.setTablePkValue(String.valueOf(attachment.getProjectAttachmentId()));
            multi.setTableName(PROJECT_ATTACHMENT_CATEGORY);
            iFndAttachmentMultiService.insertSelective(currentRequest, multi);

            FndAttachment fndAttachment = new FndAttachment();
            fndAttachment.setSourceTypeCode(FND_ATM_ATTACHMENT_MULTI);
            fndAttachment.setSourcePkValue(recordId);
            try {
                fndAttachment = fndAttachmentMapper.selectOne(fndAttachment);
                Long sourceAttachmentId = fndAttachment.getAttachmentId();

                fndAttachment.setSourcePkValue(String.valueOf(multi.getRecordId()));
                iFndAttachmentService.insert(currentRequest, fndAttachment);

                multi.setAttachmentId(fndAttachment.getAttachmentId());
                fndAttachmentMultiMapper.updateByPrimaryKey(multi);
                Long newAttachmentId = fndAttachment.getAttachmentId();


            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        hlsCusPrjProject.setOrgSignState("SIGNED");
        prjProjectService.updateByPrimaryKeySelective(currentRequest, hlsCusPrjProject);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject processResult(SignatureSendMessageDTO messageDTO) {
        String uniqueIdentification = messageDTO.getUniqueIdentification();
        String resultStatus = messageDTO.getResultStatus();
        String resultDesc = messageDTO.getResultDesc();
        SignRecord signRecord = new SignRecord();
        signRecord.setSignUniqueId(uniqueIdentification);
        signRecord = signRecordMapper.selectOne(signRecord);
        if (signRecord == null) {
            return SignService.generateMessage("record not found");
        }
        // 修改signRecord状态
        signRecord.setSignStatus(resultStatus);
        signRecord.setSignResult(resultDesc);
        signRecord.setSignDate(new Date());
        signRecordMapper.updateByPrimaryKeySelective(signRecord);

        List<ContractSendMessageDTO> contractList = messageDTO.getContractList();
        if (CollectionUtils.isNotEmpty(contractList)) {
            for (ContractSendMessageDTO contract : contractList) {
                SignContract signContract = new SignContract();
                signContract.setContractCode(contract.getContractCode());
                signContract.setContractName(contract.getContractName());
                signContract.setSignId(signRecord.getSignId());
                signContract = signContractMapper.selectOne(signContract);
                if (signContract == null) {
                    continue;
                }
                signContract.setContractStatus(resultStatus);

                byte[] decode = Base64.getDecoder().decode(contract.getDocumentSignedFile());
                String fileName = uniqueIdentification + ".pdf";
                File file = new File(getSavePath(fileName));
                try(FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(decode);
                    fos.flush();
                    Long attachmentId = fndAttachmentService.uploadAttachment(URLDecoder.decode(file.getName(), "UTF-8"), file.getPath(), FILE_SOURCE_TYPE, String.valueOf(signContract.getContractId()), file.length());
                    signContract.setSignedAttachmentId(attachmentId);
                    logger.info("file {} has been written.", fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                signContractMapper.updateByPrimaryKeySelective(signContract);
            }
        }
        // 项目承租人签约 主机厂签约结束的逻辑目前缺失
        if (TENANT_SIGN_OBJECT.equals(signRecord.getSignObject()) && SIGNED_STATUS.equals(resultStatus) && PROJECT_SOURCE_DOC_CATEGORY.equals(signRecord.getSourceDocCategory())) {
            HlsCusPrjProject cusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(signRecord.getSourceDocId());
            // 更改项目承租人已签约SIGNED
            cusPrjProject.setOrgSignState(SIGNED_STATUS);
            cusPrjProject.setSignedFlag("Y");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(cusPrjProject);
        }
        return SignService.generateMessage("success");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject processResultNew(SignatureSendMessageDTO messageDTO) {
        String uniqueIdentification = messageDTO.getUniqueIdentification();
        String resultStatus = messageDTO.getResultStatus();
        String resultDesc = messageDTO.getResultDesc();
        SignRecord signRecord = new SignRecord();
        signRecord.setSignUniqueId(uniqueIdentification);
        signRecord = signRecordMapper.selectOne(signRecord);
        if (signRecord == null) {
            return SignService.generateMessage("record not found");
        }
        // 修改signRecord状态
        signRecord.setSignStatus(resultStatus);
        signRecord.setSignResult(resultDesc);
        signRecord.setSignDate(new Date());
        signRecordMapper.updateByPrimaryKeySelective(signRecord);
        List<SignContract> signContracts = new ArrayList<>();
        List<ContractSendMessageDTO> contractList = messageDTO.getContractList();
        if (CollectionUtils.isNotEmpty(contractList)) {
            for (ContractSendMessageDTO contract : contractList) {
                SignContract signContract = new SignContract();
                signContract.setContractCode(contract.getContractCode());
                signContract.setContractName(contract.getContractName());
                signContract.setSignId(signRecord.getSignId());
                signContract = signContractMapper.selectOne(signContract);
                if (signContract == null) {
                    continue;
                }
                signContract.setContractStatus(resultStatus);

                byte[] decode = Base64.getDecoder().decode(contract.getDocumentSignedFile());
                String fileName = uniqueIdentification + ".pdf";
                File file = new File(getSavePath(fileName));
                try(FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(decode);
                    fos.flush();
                    Long attachmentId = fndAttachmentService.uploadAttachment(URLDecoder.decode(file.getName(), "UTF-8"), file.getPath(), FILE_SOURCE_TYPE, String.valueOf(signContract.getContractId()), file.length());
                    signContract.setSignedAttachmentId(attachmentId);
                    signContracts.add(signContract);
                    logger.info("file {} has been written.", fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                signContractMapper.updateByPrimaryKeySelective(signContract);
            }
        }
        // 签约结束方法
        ISignFinish finishService = SignFinishCommonService.getFinishService(signRecord.getSourceDocCategory());
        if (finishService != null) {
            finishService.signFinish(signRecord, signContracts);
        }
        return SignService.generateMessage("success");
    }

    @Override
    public String encodeBase64FromUrl(String u) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = null;
        try {
            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int b;
            while ((b = inputStream.read(data)) != -1) {
                swapStream.write(data, 0, b);
            }
            encode = encoder.encodeToString(swapStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encode;
    }

    @Override
    public String encodeBase64FromFileSystem(String path) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组进行Base64编码，得到Base64编码的字符串
        return Base64.getEncoder().encodeToString(data);
    }

    @Override
    public String wordToPdf(String path) {
        String base64Word = encodeBase64FromFileSystem(path);
        JSONObject requestJson = new JSONObject();
        requestJson.put("wordContent", base64Word);
        JSONObject result = platformUtils.getInterfaceRequest(requestJson,
                WORD_TO_PDF_INTERFACE + "?access_token=",
                "word转pdf");
        if (result == null || !"success".equals(result.get("status"))) {
            throw new CommonException("word转换pdf失败!");
        }
        return result.getString("pdfContent");
    }

    @Override
    public List<SignVerify> createVerify(IRequest requestCtx, SignVerify dto) {
        SignVerify signVerify = null;
        // 默认启用
        if(dto.getSignEnabledFlag()==null){
            dto.setSignEnabledFlag("Y");
        }
        if (dto.getVerifyId() == null) {
            dto.setOrderNum(SignService.generateVerifyOrderNum(dto.getBpCode()));
            dto.setBusinessType(BUSINESS_TYPE);
            dto.setBusinessScene(SignService.generateVerifyBusinessScene(dto.getBpName()));
            dto.setCompanyName(dto.getBpName());
            dto.setCompanyDocumentType(VERIFY_DOCUMENT_TYPE);
            dto.setDocumentNumber(dto.getRegisterCertNum());
            dto.setDocumentType(VERIFY_DOCUMENT_TYPE);
            dto.setSignCheckStatus("N");
            signVerify = signVerifyService.insertSelective(requestCtx, dto);
        } else {
            signVerify = signVerifyService.updateByPrimaryKeySelective(requestCtx, dto);
        }
        return Collections.singletonList(signVerify);
    }

    @Override
    public SignResponseDto querySign(IRequest requestCtx, String sourceDocCategory, Long sourceDocId) {
        SignRecord signRecord = signRecordMapper.queryLatestSign(sourceDocCategory, sourceDocId);
        if (signRecord == null) {
            return new SignResponseDto();
        }
        String signUniqueId = signRecord.getSignUniqueId();
        Asserts.notBlank(signUniqueId, "signUniqueId");
        SignResponseDto signResponse = querySignResult(signUniqueId);
        return signResponse;
    }

    @Override
    public List<HlsCusBpAttachment> createVerifyAttachment(IRequest request, HlsCusBpAttachment dto) {
        dto.setBpAttachmentCategory("BP_MASTER_ATT");
        dto.setDocumentName("企业签章开户材料");
        dto.setCreatedBy(request.getUserId());
        dto.setCreationDate(new Date());
        dto.setLastUpdatedBy(request.getUserId());
        dto.setLastUpdateDate(new Date());
        bpAttachmentMapper.insertSelective(dto);
        return Collections.singletonList(dto);
    }


    private SignResponseDto querySignResult(String uniqueIdentification) {
        Asserts.notBlank(uniqueIdentification, "uniqueIdentification");
        JSONObject requestJson = new JSONObject();
        requestJson.put("uniqueIdentification", uniqueIdentification);
        JSONObject result = platformUtils.getInterfaceRequest(requestJson,
                QUERY_INTERFACE + "?access_token=",
                "签章结果查询");

        if (result.getBoolean("failed") != null && result.getBoolean("failed")) {
            String message = result.getString("message");
            throw new CommonException(StringUtils.isEmpty(message) ? "签章查询出错" : message);
        }
        SignResponseDto response = result.toJavaObject(SignResponseDto.class);
        return response;
    }

    private SignRequestDto buildCompanyLongTerm(Long signId, String keyword, String wordType) {
        SignRecord signRecord = signRecordMapper.selectByPrimaryKey(signId);
        SignContract signContract = new SignContract();
        signContract.setSignId(signId);
        List<SignContract> signContracts = signContractMapper.select(signContract);

        SignRequestDto request = new SignRequestDto();
        request.setUniqueIdentification(signRecord.getSignUniqueId());

        List<SignContractDto> signContractDtos = new ArrayList<>();
        for (SignContract contract : signContracts) {
            SignContractDto contractDto = new SignContractDto();
            BeanUtils.copyProperties(contract, contractDto);
            contractDto.setSealReason(BpSignService.DEFAULT_REASON);
            //todo 测试写死的长期章
            contractDto.setDigitalCertificateCode("SEAL202208300001");
            FndAtmAttachmentDto fndAtmAttachmentDto = attachmentMapper.selectByPrimaryKey(contract.getSignedAttachmentId());
            String filePath = fndAtmAttachmentDto.getFilePath();
            String contractEncoding = this.encodeBase64FromFileSystem(filePath);
            contractDto.setContractPDF(contractEncoding);

            SignKeywordLocationDto location = new SignKeywordLocationDto();
            location.setPageNo("");
            location.setKeyword(keyword);
            location.setKeywordPositionIndex(BpSignService.KEYWORD_POSITION_INDEX);
            location.setOffsetX(BpSignService.OFFSET_X);
            location.setOffsetY(BpSignService.OFFSET_Y);
            location.setWordType(wordType);
            contractDto.setKeywordLocationList(Collections.singletonList(location));
            signContractDtos.add(contractDto);
        }
        request.setSignatureContracts(signContractDtos);
        return request;
    }

    private String getSavePath(String fileName) {
        String filePath = this.savePath;
        return filePath + File.separator + UUID.randomUUID() + "@" + fileName;
    }
}
