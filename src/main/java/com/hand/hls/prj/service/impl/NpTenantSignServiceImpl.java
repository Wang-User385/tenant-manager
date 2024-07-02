package com.hand.hls.prj.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.prj.dto.CompanyAuthMessageDTO;
import com.hand.hls.prj.dto.SignContractDto;
import com.hand.hls.prj.dto.SignPersonDto;
import com.hand.hls.prj.dto.SignRequestDto;
import com.hand.hls.prj.service.BpSignService;
import com.hand.hls.prj.service.ISignContractService;
import com.hand.hls.prj.service.ISignRecordService;
import com.hand.hls.prj.service.SignService;
import com.hand.hls.sign.dto.SignContract;
import com.hand.hls.sign.dto.SignRecord;
import com.hand.hls.sign.dto.SignVerify;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自然人签约相关实现
 *
 * @author shigure 2022/11/23 10:04
 */
@EnableAsync
@Slf4j
@Service(value = "npTenantSignServiceImpl")
public class NpTenantSignServiceImpl implements BpSignService {
    public static final String CONTRACT_SIGN_TYPE = "personal_scene";

    public static final String WORD_TYPE = "legal";

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

    @Override
    public List<SignVerify> verify(IRequest requestCtx, SignVerify dto) {
        // np暂不支持个人验证
        log.error("NpTenantSignServiceImpl 暂不支持个人认证!");
        return Collections.emptyList();
    }

    @Override
    public JSONObject verifyFinish(IRequest requestCtx, CompanyAuthMessageDTO messageDTO) {
        // np暂不支持个人验证
        log.error("NpTenantSignServiceImpl 暂不支持个人认证!");
        return SignService.generateMessage("np not support");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW ,rollbackFor = Exception.class)
    @Override
    public void sign(HlsCusBpMaster bpMaster,Long sourceDocId,String sourceDocCategory,IRequest iRequest,String signObject) {
        RequestHelper.setCurrentRequest(iRequest);
        SignRequestDto request = buildSignRequest(bpMaster);
        SignPersonDto verifyPerson = buildSignPerson(bpMaster);
        verifyPerson.setPersonName(bpMaster.getBpName());
        request.setVerifyPerson(verifyPerson);
        String keyword = buildSignKeyword(signObject);
        List<SignContractDto> contracts = buildSignContracts(bpMaster,keyword);
        request.setSignatureContracts(contracts);
        String url = signService.sign(request);

        // 生成签约记录
        SignRecord signRecord = new SignRecord();
        signRecord.setSignUniqueId(request.getUniqueIdentification());
        signRecord.setSignStatus("SIGNING");
        signRecord.setSignUrl(url);
        signRecord.setVerifyBpId(bpMaster.getBpId());
        signRecord.setSourceDocCategory(sourceDocCategory);
        signRecord.setSourceDocId(sourceDocId);
        signRecord.setSignObject(signObject);
        signRecordService.insertSelective(iRequest, signRecord);

        // 生成合同记录
        for (SignContractDto contract : contracts) {
            SignContract signContract = new SignContract();
            BeanUtils.copyProperties(contract, signContract);
            signContract.setSignId(signRecord.getSignId());
            signContract.setContractStatus(DEFAULT_STATUS);
            signContractService.insertSelective(iRequest, signContract);
        }
    }

    @Override
    public void longTermSign(HlsCusBpMaster bpMaster, Long sourceDocId, String sourceDocCategory, IRequest request, String keyword) {
        // np不支持长期证书
        log.error("NpTenantSignServiceImpl 个人签约不支持长期证书!");
    }

    private List<SignContractDto> buildSignContracts(HlsCusBpMaster bpMaster,String keyword) {
        List<SignContractDto> contracts = new ArrayList<>();
        SignContractDto signContractDto = new SignContractDto();
        String creationDate = sdf.get().format(new Date());

        //todo 测试文件，现在都没有具体附件信息
        signContractDto.setSerialNumber(1);
        signContractDto.setContractCode("TEST-HLS-" + creationDate + System.currentTimeMillis());
        signContractDto.setContractName("测试合同电签");
        signContractDto.setSignatureSubject(bpMaster.getBpName());
        signContractDto.setSealPerson(bpMaster.getBpName());
        signContractDto.setSealReason(DEFAULT_REASON);
        signContractDto.setContractSignType(CONTRACT_SIGN_TYPE);
        signContractDto.setContractCreationDate(creationDate);
        //等合同文本修改完毕后需要还原
//        signContractDto.setContractPDF(signService.encodeBase64FromFileSystem("测试合同.pdf"));

        String filePath = savePath+ DEFAULT_FILE;
        signContractDto.setContractPDF(signService.encodeBase64FromFileSystem(filePath));
        signContractDto.setKeywordLocationList(Collections.singletonList(buildKeywordLocation(keyword, WORD_TYPE)));
        contracts.add(signContractDto);
        return contracts;
    }


}
