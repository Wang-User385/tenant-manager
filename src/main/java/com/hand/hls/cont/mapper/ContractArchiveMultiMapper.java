package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ContractArchiveMulti;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractArchiveMultiMapper extends Mapper<ContractArchiveMulti>{

    List<ContractArchiveMulti> chanceAttachmentQuery(@Param("id") Long id);

    List<ContractArchiveMulti> projectAttachmentQuery(@Param("id") Long id, @Param("archiveType") String archiveType);

    List<ContractArchiveMulti> approvalAttachmentQuery(@Param("id") Long id);

    List<ContractArchiveMulti> contractAttachmentQuery(@Param("id") Long id);

    List<ContractArchiveMulti> paymentAttachmentQuery(@Param("id") Long id);

    List<ContractArchiveMulti> otherAttachmentQuery(@Param("id") Long id);

    List<ContractArchiveMulti> riskAttachmentQuery(@Param("id") Long id);

    List<ContractArchiveMulti> checkAttachmentQuery(@Param("id") Long id);

    List<ContractArchiveMulti> retailPaymentAttachmentQuery(@Param("id") Long id);
    List<ContractArchiveMulti> retailContractAttachmentQuery(@Param("id") Long id);
}