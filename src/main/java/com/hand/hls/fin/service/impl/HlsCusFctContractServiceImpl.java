package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.IAttachmentService;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mail.MessageTypeEnum;
import com.hand.hap.mail.ReceiverTypeEnum;
import com.hand.hap.mail.SendTypeEnum;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctContract;
import com.hand.hls.fct.dto.HlsCusFctPkg;
import com.hand.hls.fct.dto.HlsCusFctProject;
import com.hand.hls.fin.service.HlsCusFctContractService;
import org.apache.commons.collections.map.HashedMap;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctContractServiceImpl extends BaseServiceImpl<HlsCusFctContract> implements HlsCusFctContractService {

    @Override
    public List<HlsCusFctProject> selectContract(IRequest requestContext, HlsCusFctProject hlsCusFctProject, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public String paymentStatus(IRequest iRequest, Long projectId) {
        return null;
    }

    @Override
    public String paymentReqConfirmStatus(IRequest iRequest, Long projectId) {
        return null;
    }

    @Override
    public String interestDerateStatus(IRequest iRequest, Long projectId) {
        return null;
    }

    @Override
    public String fctContractEtStatus(IRequest iRequest, Long projectId) {
        return null;
    }

    @Override
    public String fctContractEndStatus(IRequest requestCtx, Long projectId) {
        return null;
    }

    @Override
    public List<HlsCusFctContract> selectCshPaymentReq(IRequest iRequest, HlsCusFctContract dto, int page, int pageSize) {
        return null;
    }

    @Override
    public List<HlsCusFctContract> selectCshPaymentReqForSummary(IRequest iRequest, HlsCusFctContract dto, int page, int pageSize) {
        return null;
    }

    @Override
    public HlsCusFctContract conPaymentChange(IRequest iRequest, HlsCusFctPkg hlsCusFctPkg) throws HlsCusException {
        return null;
    }

    @Override
    public void fctConChangeSubmitWfl(IRequest iRequest, HlsCusFctProject hlsCusFctProject) {

    }

    @Override
    public void fctConChangeSubmitWfl(IRequest iRequest, HlsCusFctContract hlsCusFctContract) throws HlsCusException {

    }

    @Override
    public List<HlsCusFctContract> selectFctContractDetail(HlsCusFctContract hlsCusFctContract) {
        return null;
    }

    @Override
    public HlsCusFctContract fctContractSave(IRequest request, HlsCusFctPkg hlsCusFctPkg) throws HlsCusException {
        return null;
    }

    @Override
    public HlsCusFctContract fctPaymentSave(IRequest request, HlsCusFctPkg hlsCusFctPkg) {
        return null;
    }

    @Override
    public void ctPrintSave(IRequest request, HlsCusFctPkg hlsCusFctPkg) {

    }

    @Override
    public void calcFctConFinIncome(IRequest request, HlsCusFctContract hlsCusFctContract) {

    }

    @Override
    public void fctMSendMail(String subject, StringBuffer content, String receivers, IRequest requestCtx, HlsCusFctProject hlsCusFctProject) {

    }

    @Override
    public HlsCusFctContract selectByProjectId(Long projectId) {
        return null;
    }

    @Override
    public String contractSignStatus(IRequest iRequest, Long projectId) {
        return null;
    }

    @Override
    public List<HlsCusFctContract> queryFctCompanyInceptInfo(IRequest iRequest, HlsCusFctContract dto, int page, int pageSize) {
        return null;
    }

    @Override
    public List<HlsCusFctContract> queryContractReciptInfo(IRequest iRequest, HlsCusFctContract dto, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public String selectPaymentReqStatus(IRequest iRequest, HlsCusFctContract hlsCusFctContract) {
        return null;
    }

    @Override
    public Long selectPaymentReqLnIdByContractId(IRequest iRequest, Long contractId, String sourceDocCategory, String documentCategory) {
        return null;
    }

    @Override
    public HlsCusFctContract loanConfirmSubmitWfl(IRequest iRequest, HlsCusFctPkg hlsCusFctPkg) {
        return null;
    }

    @Override
    public Double preCleanCalcResidualInterest(IRequest iRequest, HlsCusFctPkg hlsCusFctPkg) {
        return null;
    }

    @Override
    public int insertByHand(HlsCusFctContract hlsCusFctContract) {
        return 0;
    }

    @Override
    public void updateContractStatusByProjectId(HlsCusFctContract hlsCusFctContract) {

    }

    @Override
    public void createContractDocumentFile(IRequest iRequest, HlsCusFctContract hlsCusFctContract) throws HlsCusException, FileReadIOException {

    }
}