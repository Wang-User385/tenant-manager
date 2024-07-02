package com.hand.hls.prj.service.impl;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import javax.persistence.Transient;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.prj.service.ISignFinish;
import com.hand.hls.prj.service.ISignPartyService;
import com.hand.hls.sign.dto.SignContract;
import com.hand.hls.sign.dto.SignParty;
import com.hand.hls.sign.dto.SignRecord;
import com.hand.hls.sign.mapper.SignContractMapper;
import com.hand.hls.sign.mapper.SignPartyMapper;
import com.hand.hls.sign.mapper.SignRecordMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author Lenovo 2023/08/09 15:17
 */
@Component
public class PartySignFinish implements ISignFinish {

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private FndAtmAttachmentMapper attachmentMapper;

    @Autowired
    private SignPartyMapper signPartyMapper;

    @Autowired
    private ISignPartyService signPartyService;


    @Override
    public String getSourceDocCategory() {
        return "PRJ_SIGN_PARTY";
    }

    @Override
    public boolean signFinish(SignRecord signRecord, List<SignContract> signContractList) {
        SignParty signParty = signPartyMapper.selectByPrimaryKey(signRecord.getSourceDocId());
        for(SignContract contract:signContractList){
            FndAtmAttachmentDto fndAtmAttachmentDto = attachmentMapper.selectByPrimaryKey(contract.getSignedAttachmentId());
            fndAttachmentService.uploadAttachment(fndAtmAttachmentDto.getFileName(), fndAtmAttachmentDto.getFilePath(), "PRJ_SIGN_PARTY", String.valueOf(signParty.getObjectId()),fndAtmAttachmentDto.getFileSize());
        }
        signParty.setSignStatus(signRecord.getSignStatus());
        signParty.setSignTime(new Date());
        signPartyMapper.updateByPrimaryKeySelective(signParty);

        // 全部签约完毕，发起长期签章
        List<SignParty> unsigned = signPartyMapper.queryUnsigned(signParty.getProjectAttachmentId());
        if(CollectionUtils.isEmpty(unsigned) && !"long_term".equals(signParty.getSignType())){
            signPartyService.submitSignLongTerm(RequestHelper.getCurrentRequest(),signParty);
        }
        return true;
    }
}
