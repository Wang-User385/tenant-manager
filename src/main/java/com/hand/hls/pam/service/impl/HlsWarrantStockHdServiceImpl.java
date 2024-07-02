package com.hand.hls.pam.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.fct.mapper.HlsCusFctProjectAttachmentMapper;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.fct.service.HlsCusPrjContractDocxService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.pam.dto.HlsWarrantStockLn;
import com.hand.hls.pam.dto.HlsWarrantStockTran;
import com.hand.hls.pam.service.IHlsWarrantStockLnService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusDownloadDocxUtil;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.pam.service.IHlsWarrantStockHdService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsWarrantStockHdServiceImpl extends BaseServiceImpl<HlsWarrantStockHd> implements IHlsWarrantStockHdService{
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private IHlsWarrantStockLnService hlsWarrantStockLnService;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    HlsDocFileTempletMapper hlsDocFileTempletMapper;

    @Autowired
    FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    private static final String FILE_TEMPLET_TABLE = "hls_doc_file_templet";
    /**
     * fnd_atm_attachment的sourceType属性
     */
    private static final String SOURCE_TYPE_CODE_ATTACHMENT = "fnd_atm_attachment_multi";

    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;

    @Autowired
    private HlsCusFctProjectAttachmentMapper hlsCusFctProjectAttachmentMapper;

    @Autowired
    private HlsCusPrjContractDocxService hlsCusPrjContractDocxService;

    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;

    @Autowired
    private HlsCusFctProjectAttachmentService hlsCusFctProjectAttachmentService;

    private static final  String WARRANT = "WARRANT";
    private static final  String WARRANT_STOCK_NUMBER = "WARRANT_STOCK_NUMBER";

    private static final  String NEW = "NEW";
    private static final  String APPROVING = "APPROVING";
    private static final  String APPROVED = "APPROVED";
    private static final  String WARRANT_IN_STOCK_WFL = "WARRANT_IN_STOCK_WFL";
    private static final  String WARRANT_OUT_STOCK_WFL = "WARRANT_OUT_STOCK_WFL";
    private static final  String IN_STOCK = "IN_STOCK";
    private static final  String OUT_STOCK = "OUT_STOCK";
    private static final  String TEMP_OUT_STOCK = "TEMP_OUT_STOCK";
    private static final  String LAST_OUT_STOCK = "LAST_OUT_STOCK";

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Override
    public HlsWarrantStockTran warrantStockSubmitWfl(IRequest requestCtx, HlsWarrantStockTran hlsWarrantStockTran) {
        HlsWarrantStockHd hlsWarrantStockHd =  hlsWarrantStockTran.getHlsWarrantStockHd();
        List<HlsWarrantStockLn> hlsWarrantStockLnList = hlsWarrantStockTran.getHlsWarrantStockLnList();

        Long projectId = null;
        String paymentFlag = "N";

        Map<String, String> param = new HashMap<>();
        param.put("PARAMETER_01", "-");
        String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, WARRANT_STOCK_NUMBER,  WARRANT , WARRANT , param );
        hlsWarrantStockHd.setApplicantNumber(ruleCode);
        hlsWarrantStockHd.setApproveStatus(APPROVING);
        if(hlsWarrantStockHd.getProjectId() != null){
            HlsCusPrjProject hlsCusPrjProject =  hlsCusPrjProjectMapper.selectByPrimaryKey(hlsWarrantStockHd.getProjectId() );
            if(hlsCusPrjProject.getContractName() != null) {
                hlsWarrantStockHd.setContractName(hlsCusPrjProject.getContractName());
            }
            if(hlsCusPrjProject.getContractNumber() != null){
                hlsWarrantStockHd.setContractNumber(hlsCusPrjProject.getContractNumber());
            }
            projectId = hlsCusPrjProject.getProjectId();

        }


        HlsWarrantStockHd  warrantStockHd = this.insertSelective(requestCtx , hlsWarrantStockHd);


        for(int i = 0 ; i < hlsWarrantStockLnList.size() ; i ++ ){
            HlsWarrantStockLn hlsWarrantStockLn = new HlsWarrantStockLn();
            hlsWarrantStockLn.setWarrantLnId(null);
            hlsWarrantStockLn.setWarrantStockId(warrantStockHd.getWarrantStockId());
            hlsWarrantStockLn.setWarrantId(hlsWarrantStockLnList.get(i).getWarrantId());
            hlsWarrantStockLnService.insertSelective(requestCtx , hlsWarrantStockLn);
        }

        //判断合同是否放款 ，一个合同下 , 任意一个支付表付任意一笔款，就符合条件
        if(projectId != null){
            HlsCusConContract conContract = new HlsCusConContract();
            conContract.setProjectId(projectId);
            List<HlsCusConContract> hlsCusConContractList = hlsCusConContractMapper.queryContractByProjectId(conContract);

            for(HlsCusConContract contract : hlsCusConContractList){
                int payCount =  hlsCusConContractMapper.judgePaymentByContract(contract);
                if(payCount > 0){
                    paymentFlag = "Y";
                }
            }

        }
        warrantStockHd.setPaymentFlag(paymentFlag);

        List<HlsWarrantStockHd> hlsWarrantStockHdList = new ArrayList<>();
        hlsWarrantStockHdList.add(warrantStockHd);


        String wflName = WARRANT_IN_STOCK_WFL;
        if( IN_STOCK.equalsIgnoreCase(warrantStockHd.getStockType()) || OUT_STOCK.equalsIgnoreCase(warrantStockHd.getStockType())){
            wflName = WARRANT_IN_STOCK_WFL;
        }else if(TEMP_OUT_STOCK.equalsIgnoreCase(warrantStockHd.getStockType()) || LAST_OUT_STOCK.equalsIgnoreCase(warrantStockHd.getStockType()) ){
            wflName = WARRANT_OUT_STOCK_WFL;
        }


        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        //此次启动的工作流的唯一标识
        params.put("workFlowType", wflName);
        //工作流状态
        params.put("approveStatus", warrantStockHd.getApproveStatus());
        //表单的主键
        params.put("warrantStockId", warrantStockHd.getWarrantStockId());
        params.put("paymentFlag" , paymentFlag);
        Map<String, Object> evenParams = new HashMap<>();
        activitiStartService.start(requestCtx, hlsWarrantStockHdList, params);

        //插入事件
        String userName = "";
        if (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode())).get(0).getUserName();
        }
        //消息用于动态
        String msg = userName + "提交了一条权证出入库申请" ;
        evenParams.put("message", msg);
        evenParams.put("noticeTitle", "权证出入库申请");
        evenParams.put("url", "");
        evenParams.put("level", 2L);
        evenParams.put("noticeType", "NOTICE");
        evenParams.put("sourceUserId", requestCtx.getUserId());
        sysEventService.eventSave(requestCtx, warrantStockHd.getWarrantStockId(), WARRANT, WARRANT, wflName, wflName, "P2D", evenParams);


        return hlsWarrantStockTran;

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachmentMulti> warrantNoticeSave(IRequest iRequest, Long projectId, String templateCode, Long warrantStockId) throws HlsCusException {

        List<FndAttachmentMulti> fndAttachmentMultiList = new ArrayList<FndAttachmentMulti>();

        if (templateCode == null || templateCode.trim().length() == 0) {
            return fndAttachmentMultiList;
        }

        HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
        hlsDocFileTemplet.setTempletCode(templateCode);
        List<HlsDocFileTemplet> list = hlsDocFileTempletService.selectSelective(iRequest, hlsDocFileTemplet);
        HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
        if (list.size() == 1) {
            Long templeteId = list.get(0).getTempletId();
            hlsCusFctProjectAttachment.setTemplateId(templeteId);
            hlsCusFctProjectAttachment.setSourceType(templateCode);
            hlsCusFctProjectAttachment.setProjectId(projectId);
            hlsCusFctProjectAttachment.setSourceId(warrantStockId);
            hlsCusFctProjectAttachment = hlsCusFctProjectAttachmentService.insertSelective(iRequest, hlsCusFctProjectAttachment);
        }
        this.warrantTextSave(iRequest, projectId, warrantStockId , templateCode);
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTablePkValue(String.valueOf(hlsCusFctProjectAttachment.getProjectAttachmentId()));
        fndAttachmentMulti.setTableName("FCT_PROJECT_ATTACHMENT");
        fndAttachmentMultiList = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);

        return fndAttachmentMultiList;
    }


    @Override
    public List<HlsCusFctProjectAttachment> warrantTextSave(IRequest iRequest, Long projectId, Long warrantStockId , String templateCode) throws HlsCusException {
        HlsCusFctProjectAttachment t = new HlsCusFctProjectAttachment();
        t.setProjectId(projectId);
        t.setSourceId(warrantStockId);
        t.setSourceType(templateCode);
        List<HlsCusFctProjectAttachment> hlsCusFctProjectAttachmentList = hlsCusFctProjectAttachmentMapper.queryAttachmentFileInfo(t);
        if (CollectionUtils.isEmpty(hlsCusFctProjectAttachmentList)) {
            throw new HlsCusException("没有合同模板");
        }

        String companyName = "";
        String categoryType = "";
        String contrateMainNum = "";

        //生成合同附件
        for (int i = 0; i < hlsCusFctProjectAttachmentList.size(); i++) {
            HlsCusFctProjectAttachment attachment = hlsCusFctProjectAttachmentList.get(i);
            FndAttachmentMulti query = new FndAttachmentMulti();
            query.setTablePkValue(attachment.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> files = fndAttachmentMultiService.selectSelective(iRequest, query);
            Map<String, Object> params = new HashMap<String, Object>();
            //前台传入的参数需要这里接收
            params.put("projectId", attachment.getProjectId());
            params.put("templetId", attachment.getTemplateId());
            params.put("sourceType", attachment.getSourceType());
            params.put("projectAttachmentId", attachment.getProjectAttachmentId());
            params.put("companyId", iRequest.getCompanyId());
            params.put("warrantStockId", warrantStockId);
            hlsCusPrjContractDocxService.process(iRequest, params);
        }
        return hlsCusFctProjectAttachmentList;
    }


}