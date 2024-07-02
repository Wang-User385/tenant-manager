package com.hand.hls.csh.mapper;

import java.util.List;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.DepositAttachment;
import com.hand.hls.csh.dto.DepositManageHd;

public interface DepositAttachmentMapper extends Mapper<DepositManageHd>{

    List<DepositAttachment> query(DepositManageHd dto);
}