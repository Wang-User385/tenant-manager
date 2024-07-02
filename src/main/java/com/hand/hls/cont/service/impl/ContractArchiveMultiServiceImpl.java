package com.hand.hls.cont.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsCusSysAttachmentMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractArchive;
import com.hand.hls.cont.mapper.ContractArchiveMultiMapper;
import com.hand.hls.cont.mapper.HlsCusConContractArchiveMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.CshPaymentAttachment;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.csh.service.ICshPaymentAttachmentService;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.service.IHlsCreditLineAttachService;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.history.dto.HlsStandardHistory;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.office.dto.FndAtmAttachmentMultiDto;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.office.mapper.FndAtmAttachmentMultiMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.risk.dto.RiskAttachment;
import com.hand.hls.risk.service.impl.RiskAttachmentServiceImpl;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;
import com.hand.hls.rw.mapper.HlsCusRiskWarningInfoMapper;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.ContractArchiveMulti;
import com.hand.hls.cont.service.IContractArchiveMultiService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContractArchiveMultiServiceImpl extends BaseServiceImpl<ContractArchiveMulti> implements IContractArchiveMultiService {

    @Autowired
    private ContractArchiveMultiMapper archiveMultiMapper;

    @Autowired
    private IContractArchiveMultiService archiveMultiService;

    @Autowired
    private HlsCusConContractMapper conContractMapper;

    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;

    @Autowired
    private IHlsCreditLineAttachService creditLineAttachService;

    @Autowired
    private FndAtmAttachmentMultiMapper attachmentMultiMapper;

    @Autowired
    private HlsCusCshPaymentReqHdMapper paymentReqHdMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper paymentReqLnMapper;

    @Autowired
    private ICshPaymentAttachmentService paymentAttachmentService;

    @Autowired
    private HlsCusPrjProjectAttachmentService projectAttachmentService;

    @Autowired
    private HlsCusConContractArchiveMapper conContractArchiveMapper;

    @Autowired
    private PrjCheckMapper prjCheckMapper;

    @Autowired
    private HlsCusRiskWarningInfoMapper riskWarningInfoMapper;

    @Autowired
    private RiskAttachmentServiceImpl riskAttachmentService;
    @Autowired
    private HlsCusPrjQuotationMapper quotationMapper;
    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;

    @Override
    public List<ContractArchiveMulti> selectArchiveAttachment(Long contractId, String archiveType, IRequest iRequest, int pagenum, int pagesize) {
        if (contractId == null) {
            throw new IllegalArgumentException("contractId:null");
        }
        if (archiveType == null) {
            archiveType = "HLS_CREDIT_LINE_CHANCE";
        }
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(contractId);
        conContract = conContractMapper.selectOne(conContract);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(conContract.getProjectId());
        prjProject = prjProjectMapper.selectOne(prjProject);

        Long chanceId = prjProject.getChanceId();
        List<ContractArchiveMulti> list = new ArrayList<>();
        Long id;
        switch (archiveType) {
            case "HLS_CREDIT_LINE_CHANCE":
                id = chanceId;
                PageHelper.startPage(pagenum, pagesize);
                list = archiveMultiMapper.chanceAttachmentQuery(id);
                break;
            case "PRJ_BP_CZ":
            case "PRJ_BP_DB":
            case "PRJ_LEASE":
                id = prjProject.getRefProjectId();
                PageHelper.startPage(pagenum, pagesize);
                list = archiveMultiMapper.projectAttachmentQuery(id, archiveType);
                break;
            case "PRJ_PROJECT_JD":
                id = prjProject.getRefProjectId();
                PageHelper.startPage(pagenum, pagesize);
                list = archiveMultiMapper.approvalAttachmentQuery(id);
                break;
            case "CON_CONTRACT_ATT":
                id = prjProject.getProjectId();
                PageHelper.startPage(pagenum, pagesize);
                list = archiveMultiMapper.contractAttachmentQuery(id);
                break;
            case "CON_PAYMENT":
                HlsCusCshPaymentReqHd paymentReqHd = new HlsCusCshPaymentReqHd();
                paymentReqHd.setSourceContractId(contractId);
                paymentReqHd.setPaymentType("PAYMENT");
                paymentReqHd = paymentReqHdMapper.selectOne(paymentReqHd);
                if (paymentReqHd != null) {
                    id = paymentReqHd.getPaymentReqId();
                    PageHelper.startPage(pagenum, pagesize);
                    list = archiveMultiMapper.paymentAttachmentQuery(id);
                }
                ;
                break;
            case "OTHER":
                HlsCusConContractArchive conContractArchive = new HlsCusConContractArchive();
                conContractArchive.setContractId(contractId);
                conContractArchive = conContractArchiveMapper.selectOne(conContractArchive);
                PageHelper.startPage(pagenum, pagesize);
                list = archiveMultiMapper.otherAttachmentQuery(conContractArchive.getContractArchiveId());
                break;
        }

        return list;
    }

    @Override
    public void saveArchiveAttachment(List<ContractArchiveMulti> list, IRequest iRequest) {
        if (list.size() == 0) {
            throw new IllegalArgumentException("没有需要保存的文件");
        }
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(list.get(0).getContractId());
        conContract = conContractMapper.selectOne(conContract);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(conContract.getProjectId());
        prjProject = prjProjectMapper.selectOne(prjProject);

        PrjCheck prjCheck = new PrjCheck();
        HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
        List<PrjCheck> prjCheckList = new ArrayList<>();
        Long chanceId, jdProjectId, projectId, paymentReqId;
        if ("PRJ_PROJECT".equals(prjProject.getDocumentType())) {
            //大单业务
            chanceId = prjProject.getChanceId();
            jdProjectId = prjProject.getRefProjectId();
            projectId = prjProject.getProjectId();
            HlsCusCshPaymentReqHd paymentReqHd = new HlsCusCshPaymentReqHd();
            paymentReqHd.setSourceContractId(conContract.getContractId());
            paymentReqHd.setPaymentType("PAYMENT");
            paymentReqHd = paymentReqHdMapper.selectOne(paymentReqHd);
            paymentReqId = paymentReqHd.getPaymentReqId();
            prjCheck.setContractId(prjProject.getProjectId());
            prjCheckList = prjCheckMapper.select(prjCheck);
            if (prjCheckList.size() != 0) {
                prjCheck = prjCheckList.stream().max(Comparator.comparing(PrjCheck::getCheckId)).get();
            }
            riskWarningInfo.setProjectId(prjProject.getProjectId());
            List<HlsCusRiskWarningInfo> riskWarningInfoList = riskWarningInfoMapper.select(riskWarningInfo);
            if (riskWarningInfoList.size() != 0) {
                riskWarningInfo = riskWarningInfoList.stream().max(Comparator.comparing(HlsCusRiskWarningInfo::getRiskWarningId)).get();
            }
        } else {
            //零售业务租后检查以合作方为维度进行
            prjCheck.setManufacturerId(prjProject.getManufacturerId());
            prjCheckList = prjCheckMapper.select(prjCheck);
            if (prjCheckList.size() != 0) {
                prjCheck = prjCheckList.stream().max(Comparator.comparing(PrjCheck::getCheckId)).get();
            }

            //零售合同对应的尽调项目
            HlsCusPrjQuotation prjQuotation=new HlsCusPrjQuotation();
            prjQuotation.setQuotationId(conContract.getQuotationId());
            prjQuotation=quotationMapper.selectByPrimaryKey(prjQuotation.getQuotationId());
            HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
            hlsProductDefinition.setDefinitionId(prjQuotation.getPlanId());
            hlsProductDefinition = hlsProductDefinitionMapper.selectByPrimaryKey(hlsProductDefinition);

            HlsCusPrjProject prjProjectJd = new HlsCusPrjProject();
            prjProjectJd.setProjectId(hlsProductDefinition.getReplyProductId());
            prjProjectJd = prjProjectMapper.selectByPrimaryKey(prjProjectJd);
            chanceId = prjProjectJd.getChanceId();
            jdProjectId = prjProjectJd.getProjectId();
            projectId = prjProject.getProjectId();

            //零售付款
            HlsCusCshPaymentReqLn paymentReqLn = new HlsCusCshPaymentReqLn();
            paymentReqLn.setSourceDocId(conContract.getContractId());
            paymentReqLn.setSourceDocCategory("CON_CONTRACT");
            paymentReqLn = paymentReqLnMapper.selectOne(paymentReqLn);
            paymentReqId = paymentReqLn.getPaymentReqId();
        }

        ContractArchiveMulti archiveMulti;
        for (ContractArchiveMulti dto : list) {
            archiveMulti = new ContractArchiveMulti();
            archiveMulti.setRecordId(dto.getRecordId());
            archiveMulti = archiveMultiService.selectByPrimaryKey(iRequest, archiveMulti);
            if (archiveMulti != null) {
                archiveMultiService.updateByPrimaryKey(iRequest, dto);
            } else {
                archiveMultiService.insertSelective(iRequest, dto);
            }

            if (dto.getFileNameId() == null) {
                switch (dto.getFileCategory()) {
                    case "HLS_CREDIT_LINE_CHANCE":
                        HlsCreditLineAttach creditLineAttach = new HlsCreditLineAttach();
                        creditLineAttach.setChanceId(chanceId);
                        creditLineAttach.setAttachmentCategory("CHANCE_ATT");
                        creditLineAttach.setSourceType("SYS_DOCUMENT_LIST");
                        //creditLineAttach.setSourceId();
                        creditLineAttach.setDocumentName(dto.getFileName());
                        creditLineAttach.setUploadDate(new Date());
                        creditLineAttach.setUploadPerson(Long.toString(iRequest.getUserId()));
                        creditLineAttach = creditLineAttachService.insertSelective(iRequest, creditLineAttach);
                        dto.setFileNameId(creditLineAttach.getChanceAttachmentId());
                        archiveMultiService.updateByPrimaryKeySelective(iRequest, dto);
                        break;
                    case "PRJ_BP_CZ":
                    case "PRJ_BP_DB":
                    case "PRJ_LEASE":
                    case "PRJ_PROJECT_JD":
                        //零售进件和投放  prj_project_attachment  ONLINE_SIGN_CONTRACT
                    case "prj_project_attachment":
                    case "ONLINE_SIGN_CONTRACT":
                    case "CON_CONTRACT_ATT":
                        HlsCusPrjProjectAttachment prjProjectAttachment = new HlsCusPrjProjectAttachment();
                        if ("CON_CONTRACT_ATT".equals(dto.getFileCategory()) || "prj_project_attachment".equals(dto.getFileCategory()) || "ONLINE_SIGN_CONTRACT".equals(dto.getFileCategory())) {
                            prjProjectAttachment.setProjectId(projectId);
                        } else {
                            prjProjectAttachment.setProjectId(jdProjectId);
                        }
                        prjProjectAttachment.setProjectAttachmentCategory(dto.getFileCategory());
                        prjProjectAttachment.setDocumentName(dto.getFileName());
                        prjProjectAttachment.setUploadDate(new Date());
                        prjProjectAttachment.setUploadPerson(Long.toString(iRequest.getUserId()));
                        prjProjectAttachment = projectAttachmentService.insertSelective(iRequest, prjProjectAttachment);
                        dto.setFileNameId(prjProjectAttachment.getProjectAttachmentId());
                        archiveMultiService.updateByPrimaryKeySelective(iRequest, dto);
                        break;
                    //RETAIL_PAYMENT 零售付款
                    case "RETAIL_PAYMENT":
                    case "CON_PAYMENT":
                        CshPaymentAttachment paymentAttachment = new CshPaymentAttachment();
                        paymentAttachment.setPaymentReqId(paymentReqId);
                        paymentAttachment.setProjectAttachmentCategory(dto.getFileCategory());
                        paymentAttachment.setDocumentName(dto.getFileName());
                        paymentAttachment.setUploadDate(new Date());
                        paymentAttachment.setUploadPerson(Long.toString(iRequest.getUserId()));
                        paymentAttachment = paymentAttachmentService.insertSelective(iRequest, paymentAttachment);
                        dto.setFileNameId(paymentAttachment.getCshAttcahmentId());
                        archiveMultiService.updateByPrimaryKeySelective(iRequest, dto);
                        break;
                    case "OTHER":
                        dto.setFileNameId(dto.getRecordId());
                        archiveMultiService.updateByPrimaryKeySelective(iRequest, dto);
                        break;
                    case "RISK_ATT":
                    case "PRJ_CHECK_ATT":
                        RiskAttachment riskAttachment = new RiskAttachment();
                        if ("RISK_ATT".equals(dto.getFileCategory())) {
                            riskAttachment.setRiskWarningId(riskWarningInfo.getRiskWarningId());
                        } else {
                            riskAttachment.setRiskWarningId(prjCheck.getCheckId());
                        }
                        riskAttachment.setAttachmentCategory(dto.getFileCategory());
                        riskAttachment.setDocumentName(dto.getFileName());
                        riskAttachment.setUploadDate(new Date());
                        riskAttachment.setUploadPerson(Long.toString(iRequest.getUserId()));
                        riskAttachment = riskAttachmentService.insertSelective(iRequest, riskAttachment);
                        dto.setFileNameId(riskAttachment.getRiskAttachmentId());
                        archiveMultiService.updateByPrimaryKeySelective(iRequest, dto);
                        break;
                }
                ;
            }

        }
    }

    @Override
    public List<ContractArchiveMulti> selectAllArchiveAttachment(Long contractId, String status, IRequest iRequest, int pagenum, int pagesize) {
        if (contractId == null) {
            throw new IllegalArgumentException("contractId:null");
        }

        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(contractId);
        conContract = conContractMapper.selectOne(conContract);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(conContract.getProjectId());
        prjProject = prjProjectMapper.selectOne(prjProject);

        List<ContractArchiveMulti> list = new ArrayList<>();
        if ("PRJ_PROJECT".equals(prjProject.getDocumentType())) {
            //大单业务
            HlsCusCshPaymentReqHd paymentReqHd = new HlsCusCshPaymentReqHd();
            paymentReqHd.setSourceContractId(contractId);
            paymentReqHd.setPaymentType("PAYMENT");
            paymentReqHd = paymentReqHdMapper.selectOne(paymentReqHd);
            HlsCusConContractArchive conContractArchive = new HlsCusConContractArchive();
            conContractArchive.setContractId(contractId);
            conContractArchive = conContractArchiveMapper.selectOne(conContractArchive);
            PrjCheck prjCheck = new PrjCheck();
            prjCheck.setContractId(prjProject.getProjectId());
            List<PrjCheck> prjCheckList = prjCheckMapper.select(prjCheck);
            HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
            riskWarningInfo.setProjectId(prjProject.getProjectId());
            List<HlsCusRiskWarningInfo> riskWarningInfoList = riskWarningInfoMapper.select(riskWarningInfo);

            if (prjProject != null) {
                list.addAll(archiveMultiMapper.chanceAttachmentQuery(prjProject.getChanceId()));
            }
            if (prjProject.getRefProjectId() != null) {
                list.addAll(archiveMultiMapper.projectAttachmentQuery(prjProject.getRefProjectId(), "PRJ_BP_CZ"));
                list.addAll(archiveMultiMapper.projectAttachmentQuery(prjProject.getRefProjectId(), "PRJ_BP_DB"));
                list.addAll(archiveMultiMapper.projectAttachmentQuery(prjProject.getRefProjectId(), "PRJ_LEASE"));
                list.addAll(archiveMultiMapper.approvalAttachmentQuery(prjProject.getRefProjectId()));
            }
            list.addAll(archiveMultiMapper.contractAttachmentQuery(prjProject.getProjectId()));
            if (paymentReqHd != null) {
                list.addAll(archiveMultiMapper.paymentAttachmentQuery(paymentReqHd.getPaymentReqId()));
            }
            list.addAll(archiveMultiMapper.otherAttachmentQuery(conContractArchive.getContractArchiveId()));
            if (prjCheckList.size() != 0) {
                for (PrjCheck check : prjCheckList) {
                    list.addAll(archiveMultiMapper.checkAttachmentQuery(check.getCheckId()));
                }
            }
            if (riskWarningInfoList.size() != 0) {
                for (HlsCusRiskWarningInfo hlsCusRiskWarningInfo : riskWarningInfoList) {
                    list.addAll(archiveMultiMapper.riskAttachmentQuery(hlsCusRiskWarningInfo.getRiskWarningId()));
                }
            }
        } else {
            //零售业务
            HlsCusConContractArchive conContractArchive = new HlsCusConContractArchive();
            conContractArchive.setContractId(contractId);
            conContractArchive = conContractArchiveMapper.selectOne(conContractArchive);
            //零售业务租后检查以合作方为维度进行
            PrjCheck prjCheck = new PrjCheck();
            prjCheck.setManufacturerId(prjProject.getManufacturerId());
            List<PrjCheck> prjCheckList = prjCheckMapper.select(prjCheck);

            //零售合同对应的尽调项目
            HlsCusPrjQuotation prjQuotation=new HlsCusPrjQuotation();
            prjQuotation.setQuotationId(conContract.getQuotationId());
            prjQuotation=quotationMapper.selectByPrimaryKey(prjQuotation.getQuotationId());
            HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
            hlsProductDefinition.setDefinitionId(prjQuotation.getPlanId());
            hlsProductDefinition = hlsProductDefinitionMapper.selectByPrimaryKey(hlsProductDefinition);

            HlsCusPrjProject prjProjectJd = new HlsCusPrjProject();
            prjProjectJd.setProjectId(hlsProductDefinition.getReplyProductId());
            prjProjectJd = prjProjectMapper.selectByPrimaryKey(prjProjectJd);

            if (prjProjectJd != null) {
                list.addAll(archiveMultiMapper.chanceAttachmentQuery(prjProjectJd.getChanceId()));
            }
            if (prjProjectJd != null) {
                list.addAll(archiveMultiMapper.projectAttachmentQuery(prjProjectJd.getProjectId(), "PRJ_BP_CZ"));
                list.addAll(archiveMultiMapper.projectAttachmentQuery(prjProjectJd.getProjectId(), "PRJ_BP_DB"));
                list.addAll(archiveMultiMapper.projectAttachmentQuery(prjProjectJd.getProjectId(), "PRJ_LEASE"));
                list.addAll(archiveMultiMapper.approvalAttachmentQuery(prjProjectJd.getProjectId()));
            }
            //零售合同附件 进件、投放审查附件
            list.addAll(archiveMultiMapper.retailContractAttachmentQuery(prjProject.getProjectId()));

            //零售付款
            HlsCusCshPaymentReqLn paymentReqLn = new HlsCusCshPaymentReqLn();
            paymentReqLn.setSourceDocId(contractId);
            paymentReqLn.setSourceDocCategory("CON_CONTRACT");
            paymentReqLn = paymentReqLnMapper.selectOne(paymentReqLn);
            list.addAll(archiveMultiMapper.retailPaymentAttachmentQuery(paymentReqLn.getPaymentReqId()));

            list.addAll(archiveMultiMapper.otherAttachmentQuery(conContractArchive.getContractArchiveId()));
            if (prjCheckList.size() != 0) {
                for (PrjCheck check : prjCheckList) {
                    list.addAll(archiveMultiMapper.checkAttachmentQuery(check.getCheckId()));
                }
            }
        }

        if ("download".equals(status) || "detail".equals(status)) {
            list = list.stream().filter(contractArchiveMulti -> Integer.parseInt(contractArchiveMulti.getCopies()) > 0).collect(Collectors.toList());
        }
        Page page = new Page(pagenum, pagesize);
        page.setTotal(list.size());
        if (list.size() >= pagenum * pagesize) {
            page.addAll(list.subList((pagenum - 1) * pagesize, pagenum * pagesize));
        } else {
            page.addAll(list.subList((pagenum - 1) * pagesize, list.size()));
        }
        PageInfo pageInfo = new PageInfo<>(page);
        return pageInfo.getList();
    }

    @Override
    public List<Long> selectAttachmentId(List<ContractArchiveMulti> list) {
        List<Long> attachmentIdList = new ArrayList<>();
        FndAtmAttachmentMultiDto attachmentMultiDto;
        List<FndAtmAttachmentMultiDto> attachmentMultiDtoList = new ArrayList<>();
        for (ContractArchiveMulti archiveMulti : list) {
            attachmentMultiDto = new FndAtmAttachmentMultiDto();
            attachmentMultiDto.setTablePkValue(archiveMulti.getFileNameId());
            switch (archiveMulti.getFileCategory()) {
                case "HLS_CREDIT_LINE_CHANCE":
                    attachmentMultiDto.setTableName("HLS_CREDIT_LINE_CHANCE");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                    break;
                case "PRJ_BP_CZ":
                case "PRJ_BP_DB":
                case "PRJ_LEASE":
                case "CON_CONTRACT_ATT":
                    attachmentMultiDto.setTableName("PRJ_PROJECT_ATTACHMENT");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                case "prj_project_attachment":
                    attachmentMultiDto.setTableName("prj_project_attachment");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                case "ONLINE_SIGN_CONTRACT":
                    attachmentMultiDto.setTableName("ONLINE_SIGN_CONTRACT");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                case "PRJ_PROJECT_JD":
                    attachmentMultiDto.setTableName("PRJ_PROJECT_JD");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                case "RETAIL_PAYMENT":
                case "CON_PAYMENT":
                    attachmentMultiDto.setTableName("csh_payment_attachment");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                    break;
                case "OTHER":
                    attachmentMultiDto.setTableName("con_contract_archive_multi");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                    break;
                case "RISK_ATT":
                    attachmentMultiDto.setTableName("plm_risk_warning");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                    break;
                case "PRJ_CHECK_ATT":
                    attachmentMultiDto.setTableName("prj_check");
                    attachmentMultiDtoList.addAll(attachmentMultiMapper.select(attachmentMultiDto));
                    break;
            }
        }

        for (FndAtmAttachmentMultiDto fndAtmAttachmentMultiDto : attachmentMultiDtoList) {
            attachmentIdList.add(fndAtmAttachmentMultiDto.getAttachmentId());
        }

        return attachmentIdList;
    }

}