package com.hand.hls.cont.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.app.utils.generalUtils.AppConstantUtils;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.service.IConContractArchiveService;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.cont.service.IContractArchiveMultiService;
import com.hand.hls.csh.dto.CshPaymentAttachment;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.csh.service.ICshPaymentAttachmentService;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.service.IHlsCreditLineAttachService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.IPrjProjectAttachmentService;
import com.hand.hls.prj.service.IPrjProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ConContractArchiveServiceImpl extends BaseServiceImpl<HlsCusConContractArchive> implements IConContractArchiveService {

    private static Logger logger = LoggerFactory.getLogger(ConContractArchiveServiceImpl.class);
    private static final String CONTRACT_ARCHIVE_PREFIX = "FLMG";

    @Value("${file.upload.dir:.}")
    private String savePath = ".";
    private boolean needParse = false;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private IPrjProjectAttachmentService projectAttachmentService;

    @Autowired
    private ICshPaymentAttachmentService cshPaymentAttachmentService;

    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService;

    private HlsCusCshPaymentReqHdMapper cshPaymentReqHdMapper;

    @Autowired
    private IConContractService contractService;

    @Autowired
    private IPrjProjectService projectService;

    @Autowired
    private IContractArchiveMultiService contractArchiveMultiService;

    @Autowired
    private IHlsCreditLineAttachService creditLineAttachmentService;

    /**
     * 生成合同文档记录
     *
     * @return
     */
    @Override
    public void saveContractArchive(IRequest iRequest, HlsCusConContract contract) {
        HlsCusConContractArchive conContractArchive = new HlsCusConContractArchive();
        Map<String, String> params = new HashMap<String, String>();
        String contractArchiveNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest,
                "CONTRACT_ARCHIVE",
                "CONTRACT_ARCHIVE",
                "CONTRACT_ARCHIVE",
                params);
        conContractArchive.setContractId(contract.getContractId());
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        conContractArchive.setContractArchiveNumber(CONTRACT_ARCHIVE_PREFIX + dateStr + contractArchiveNumber);
        conContractArchive.setStatus("NEW");
        self().insertSelective(iRequest, conContractArchive);
    }

    /**
     * 将文档上传到对应流程的附件中
     *
     * @param iRequest
     * @param conractId 合同id
     */
    public Long uploadToNodes(IRequest iRequest, Long conractId, String fileCategory) {

        Long pkValue = null;
        switch (fileCategory) {
            case "PROJECT":
                //立项资料
                break;
            case "due_diligence_lessee":
                //尽调资料(承租人)
                pkValue = saveProjectAttachmentBpCz(iRequest, conractId);
                break;

            case "due_diligence_guarantor":
                //尽调资料(担保人)
                break;

            case "due_diligence_lease_item":
                //尽调资料(租赁物)
                break;

            case "approval_comments":
                //审批意见资料
                break;
            case "contract":
                //合同资料
                pkValue = saveContractAttachment(iRequest, conractId);
                break;

            case "loan":
                //放款资料
                pkValue = saveCshPaymentAttachment(iRequest, conractId);
                break;

            case "other":
                //其它资料
                break;
        }
        return pkValue;
    }

    private Long saveCreditLineAttachment(IRequest iRequest, Long contractId) {
        HlsCreditLineAttach creditLineAttach = new HlsCreditLineAttach();

        HlsCreditLineAttach hlsCreditLineAttachResult = creditLineAttachmentService.insertSelective(iRequest, creditLineAttach);
        return hlsCreditLineAttachResult.getChanceAttachmentId();
    }

    private Long saveContractAttachment(IRequest iRequest, Long contractId) {
        HlsCusPrjProjectAttachment prjProjectAttachment = new HlsCusPrjProjectAttachment();

        //对应节点单据的id
//        prjProjectAttachment.setAttachmentId(pkValue);
        prjProjectAttachment.setProjectAttachmentCategory("CON_CONTRACT_ATT");
        HlsCusPrjProjectAttachment prjProjectAttachmentResult = projectAttachmentService.insertSelective(iRequest, prjProjectAttachment);
        return prjProjectAttachmentResult.getProjectAttachmentId();
    }

    private Long saveCshPaymentAttachment(IRequest iRequest, Long contractId) {
        HlsCusCshPaymentReqHd cshPaymentReqHdQuery = new HlsCusCshPaymentReqHd();
        cshPaymentReqHdQuery.setSourceContractId(contractId);
        HlsCusCshPaymentReqHd cshPaymentReqHd = cshPaymentReqHdMapper.selectOne(cshPaymentReqHdQuery);

        CshPaymentAttachment cshPaymentAttachment = new CshPaymentAttachment();
        cshPaymentAttachment.setPaymentReqId(cshPaymentReqHd.getPaymentReqId());
        cshPaymentAttachment.setProjectAttachmentCategory("CON_PAYMENT");

        CshPaymentAttachment cshPaymentAttachmentResult = cshPaymentAttachmentService.insert(iRequest, cshPaymentAttachment);
        return cshPaymentAttachmentResult.getCshAttcahmentId();
    }


    /**
     * 尽调(承租人)
     *
     * @param iRequest
     * @param contractId
     * @return
     */
    private Long saveProjectAttachmentBpCz(IRequest iRequest, Long contractId) {
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        HlsCusConContract contractResult = contractService.selectByPrimaryKey(iRequest, contract);

        HlsCusPrjProjectAttachment projectAttachment = new HlsCusPrjProjectAttachment();
        projectAttachment.setProjectAttachmentCategory("PRJ_BP_CZ");
        projectAttachment.setProjectId(contractResult.getProjectId());
        HlsCusPrjProjectAttachment prjProjectAttachmentResult = projectAttachmentService.insertSelective(iRequest, projectAttachment);
        return prjProjectAttachmentResult.getProjectAttachmentId();
    }

    /**
     * 尽调(担保人)
     *
     * @param iRequest
     * @param contractId
     * @return
     */
    private Long saveProjectAttachmentBpDb(IRequest iRequest, Long contractId) {
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        HlsCusConContract contractResult = contractService.selectByPrimaryKey(iRequest, contract);


        HlsCusPrjProjectAttachment projectAttachment = new HlsCusPrjProjectAttachment();
        projectAttachment.setProjectAttachmentCategory("PRJ_BP_DB");
        projectAttachment.setProjectId(contractResult.getProjectId());
        HlsCusPrjProjectAttachment prjProjectAttachmentResult = projectAttachmentService.insertSelective(iRequest, projectAttachment);
        return prjProjectAttachmentResult.getProjectAttachmentId();
    }

    /**
     * 尽调(租赁物)
     *
     * @param iRequest
     * @param contractId
     * @return
     */
    private Long saveProjectAttachmentLease(IRequest iRequest, Long contractId) {
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        HlsCusConContract contractResult = contractService.selectByPrimaryKey(iRequest, contract);

        HlsCusPrjProjectAttachment projectAttachment = new HlsCusPrjProjectAttachment();
        projectAttachment.setProjectAttachmentCategory("PRJ_LEASE");
        projectAttachment.setProjectId(contractResult.getProjectId());
        HlsCusPrjProjectAttachment prjProjectAttachmentResult = projectAttachmentService.insertSelective(iRequest, projectAttachment);
        return prjProjectAttachmentResult.getProjectAttachmentId();
    }


    @Override
    public ResponseData uploadFile(HttpServletRequest request,
                                   IRequest iRequest,
                                   Long contractId,
                                   String fileCategory) {
        ResponseData responseData = new ResponseData();

        try {
            RequestHelper.setCurrentRequest(iRequest);
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            if (!multipartResolver.isMultipart(request)) {
                return new ResponseData(false);
            }
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

            String sourceType = multiRequest.getParameter("sourcetype");
//            String pkValue = multiRequest.getParameter("pkvalue");
            String filename = multiRequest.getParameter("filename");

            Iterator iter = multiRequest.getFileNames();
            Long attachmentId = null;
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    String path = getSavePath();
                    File target = new File(path);
                    file.transferTo(target);
                    HlsCusConContractArchive contractArchiveQuery = new HlsCusConContractArchive();
                    contractArchiveQuery.setContractId(contractId);
                    List<HlsCusConContractArchive> contractArchiveList = self().selectSelective(iRequest, contractArchiveQuery);
                    String pkValue = "";
                    if (CollectionUtil.isNotEmpty(contractArchiveList)) {
                        pkValue = contractArchiveList.get(0).getContractArchiveId().toString();
                    }
                    attachmentId = fndAttachmentService.uploadAttachment(URLDecoder.decode(filename, "UTF-8"), path, sourceType, pkValue, file.getSize());
                }
            }
            createContractArchiveMulti(iRequest, contractId, fileCategory, Arrays.asList(attachmentId));
            responseData.setRows(new ArrayList<>(Arrays.asList(attachmentId)));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    public void createContractArchiveMulti(IRequest iRequest, Long contractId, String fileCategory, List<Long> attachmentIds) {
        HlsCusConContractArchive contractArchive = new HlsCusConContractArchive();
        contractArchive.setContractId(contractId);
        List<HlsCusConContractArchive> conContractArchiveList = self().selectSelective(iRequest, contractArchive);
        ContractArchiveMulti contractArchiveMulti = new ContractArchiveMulti();
        contractArchiveMulti.setContractArchiveId(conContractArchiveList.get(0).getContractArchiveId());
        contractArchiveMulti.setCopies(String.valueOf(attachmentIds.size()));

        StringBuilder attchfilenames = new StringBuilder();
        attachmentIds.forEach(attachmentId -> {
            FndAttachment fndAttachment = new FndAttachment();
            fndAttachment.setAttachmentId(attachmentId);
            FndAttachment fndAttachmentResult = fndAttachmentService.selectByPrimaryKey(iRequest, fndAttachment);
            attchfilenames.append(fndAttachmentResult.getFileName());
            attchfilenames.append(";");
        });

        if (attchfilenames != null) {
            contractArchiveMulti.setAttachmentName(attchfilenames.toString().substring(0, attchfilenames.lastIndexOf(";")));
        }

        contractArchiveMulti.setFileCategory(fileCategory);
        contractArchiveMulti.setFileName("文件名称");

        contractArchiveMultiService.insertSelective(iRequest, contractArchiveMulti);
    }

    private String getSavePath() {
        String filePath = "d:\\hls_attachment";
        if (needParse) {
            final LocalDateTime now = LocalDateTime.now();
            filePath = savePath.replace("{yyyy}", String.valueOf(now.getYear()))
                    .replace("{MM}", String.valueOf(now.getMonth()))
                    .replace("{dd}", String.valueOf(now.getDayOfMonth()));
        }
        return filePath + File.separator + UUID.randomUUID();
    }

    @Override
    public void archiveConfirm(IRequest iRequest, List<HlsCusConContractArchive> hlsCusArchives) {
        for (HlsCusConContractArchive conContractArchive : hlsCusArchives) {
            //更新档案管理员和归档状态
            conContractArchive.setConfirmPerson(iRequest.getUserId());
            conContractArchive.setStatus("CONFIRM");
            conContractArchive.setConfirmDate(new Date());
            self().updateByPrimaryKeySelective(iRequest,conContractArchive);
        }
    }
}
