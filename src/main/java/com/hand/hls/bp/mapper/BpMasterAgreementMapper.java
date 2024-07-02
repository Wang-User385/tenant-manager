package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpMasterAgreement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BpMasterAgreementMapper extends Mapper<BpMasterAgreement>{
List<BpMasterAgreement> queryBpMasterAgreementDetail(@Param("bpId")Long bpId);
    List<BpMasterAgreement> queryBpMasterAgreementDetail1(@Param("bpId")Long bpId);
}