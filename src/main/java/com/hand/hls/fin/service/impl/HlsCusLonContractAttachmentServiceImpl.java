package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.IAttachmentService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractAttachment;
import com.hand.hls.fin.service.HlsCusILonContractAttachmentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractAttachmentServiceImpl extends BaseServiceImpl<HlsCusLonContractAttachment> implements HlsCusILonContractAttachmentService {

    @Override
    public List<HlsCusLonContractAttachment> selectLonConAttachment(IRequest request, HlsCusLonContractAttachment lonContractAttachment, int page, int pageSize) {
        return null;
    }

    @Override
    public List<HlsCusLonContractAttachment> lonContractAttachmentDetailQuery(IRequest request, HlsCusLonContractAttachment lonContractAttachment, int page, int pageSize) {
        return null;
    }

    @Override
    public void updateLonContractAttachmentStatus(IRequest iRequest, HlsCusLonContract hlsCusLonContract) {

    }

    @Override
    public HlsCusLonContractAttachment saveLonContractAttachment(IRequest request, HlsCusLonContractAttachment lonContractAttachment) {
        return null;
    }

    @Override
    public void lonContractAttachmentDownload(IRequest request, HlsCusLonContractAttachment dto) {

    }

    @Override
    public void selectOrderNumberMax(IRequest request, List<HlsCusLonContractAttachment> dto) {

    }

    @Override
    public void downloadContractFile(Long contractId, IRequest iRequest, HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public int selectAttachmentCodeNullCount(Long contractId) {
        return 0;
    }
}