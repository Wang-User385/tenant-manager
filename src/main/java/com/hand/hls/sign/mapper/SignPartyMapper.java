package com.hand.hls.sign.mapper;

import java.util.List;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sign.dto.SignParty;

/**
 * description
 *
 * @author shigure 2022/11/15 18:46
 */
public interface SignPartyMapper extends Mapper<SignParty> {

    /**
     * 查询需要签约的信息
     * @param projectAttachmentId
     * @return
     */
    List<SignParty> queryToSign(Long projectAttachmentId);

    List<SignParty> queryUnsigned(Long projectAttachmentId);

}
