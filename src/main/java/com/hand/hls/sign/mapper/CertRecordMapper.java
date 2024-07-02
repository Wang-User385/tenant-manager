package com.hand.hls.sign.mapper;

import java.util.List;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sign.dto.CertRecord;

/**
 * description
 *
 * @author shigure 2022/11/15 18:46
 */
public interface CertRecordMapper extends Mapper<CertRecord> {
    List<CertRecord> signRecordSelectByBpId(Long bpId);
    List<CertRecord> signRecordSelectByUserId(Long userId);
    String queryLoginIpByUserId(Long userId);
}
