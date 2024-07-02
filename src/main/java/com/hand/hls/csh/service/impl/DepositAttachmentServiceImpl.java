package com.hand.hls.csh.service.impl;

import java.util.List;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.DepositAttachment;
import com.hand.hls.csh.dto.DepositManageHd;
import com.hand.hls.csh.mapper.DepositAttachmentMapper;
import com.hand.hls.csh.service.IDepositAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author Lenovo 2023/07/18 14:43
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositAttachmentServiceImpl extends BaseServiceImpl<DepositAttachment> implements IDepositAttachmentService {

    @Autowired
    private DepositAttachmentMapper depositAttachmentMapper;

    @Override
    public List<DepositAttachment> pageQuery(IRequest requestCtx, DepositManageHd dto, Integer page, Integer pageSize, String sortName, String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }

        if (page != null && pageSize != null) {
            PageHelper.startPage(page, pageSize);
            if(net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isNotEmpty(orderBy)){
                PageHelper.orderBy(orderBy);
            }
        }
        return depositAttachmentMapper.query(dto);
    }
}
