package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonConBankAccTransfer;

import java.util.List;

public interface HlsCusLonConBankAccTransferMapper extends Mapper<HlsCusLonConBankAccTransfer>{

    void deleteTransferByBatchId(HlsCusLonConBankAccTransfer hlsCusLonConBankAccTransfer);

    List<HlsCusLonConBankAccTransfer> selectConBankAccountByBatchId(HlsCusLonConBankAccTransfer hlsCusLonConBankAccTransfer);


}