package com.hand.hls.csh.service;

import java.util.List;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.DepositAttachment;
import com.hand.hls.csh.dto.DepositManageHd;

public interface IDepositAttachmentService extends IBaseService<DepositAttachment>, ProxySelf<IDepositAttachmentService>{


    List<DepositAttachment> pageQuery(IRequest requestCtx, DepositManageHd dto, Integer page, Integer pageSize, String sortName, String sortOrder);
}