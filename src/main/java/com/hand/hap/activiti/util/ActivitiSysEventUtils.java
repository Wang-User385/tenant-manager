//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.activiti.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.activiti.controllers.WorkflowAttachmentController;
import com.hand.hap.activiti.mapper.TaskNewMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hls.app.controllers.HlsCusAppActivitiController;
import com.hand.hls.app.event.service.AppWflTodoNoticeService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.*;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.elecSeal.utils.FormDataConnectorUtils;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCreditLineAttachMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.interfacePlatform.utils.InterfacePlatformUtils;
import com.hand.hls.interfacePlatform.utils.WorkflowUtils;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.oss.service.CloudStorageService;
import com.hand.hls.oss.utils.OSSUtils;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.risk.dto.RiskAttachment;
import com.hand.hls.risk.mapper.RiskAttachmentMapper;
import com.hand.hls.sys.dto.SysDocumentHistory;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.sys.service.impl.SysDocumentHistoryServiceImpl;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.HttpExecuteResponse;
import hls.core.sys.event.service.SysEventService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import leaf.utils.BrowserUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.data.UserDataManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;

@Component
public class ActivitiSysEventUtils {
    @Autowired
    private UserDataManager userDataManager;
    @Autowired
    private IUserService userService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private AppWflTodoNoticeService appWflTodoNoticeService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;
    @Autowired
    private HlsCreditLineAttachMapper hlsCreditLineAttachMapper;
    @Autowired
    private InterfacePlatformUtils interfacePlatformUtils;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusConContractBpMapper hlsCusConContractBpMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private HlsCusConContractChangeReqMapper hlsCusConContractChangeReqMapper;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;
    @Autowired
    private HlsCusContractAttachmentMapper hlsCusContractAttachmentMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private HlsCusConContractLeaseItemMapper hlsCusConContractLeaseItemMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    private PrjCheckMapper prjCheckMapper;
    @Autowired
    private RiskAttachmentMapper riskAttachmentMapper;
    @Autowired
    private PrjProjectApprovalMapper prjProjectApprovalMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;
    @Autowired
    private CshPaymentAttachmentMapper cshPaymentAttachmentMapper;
    @Autowired
    private FndAtmAttachmentMapper fndAtmAttachmentMapper;
    @Autowired
    private HlsCusCshContractCashflowMapper hlsCusCshContractCashflowMapper;
    @Autowired
    private HlsCusConDebtExemptionReqMapper hlsCusConDebtExemptionReqMapper;
    @Autowired
    private HlsCusConDebtExemptionReqCfMapper hlsCusConDebtExemptionReqCfMapper;
    @Autowired
    private DepositManageHdMapper depositManageHdMapper;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private FormDataConnectorUtils formDataConnectorUtils;
    @Autowired
    private HlsCreditPlanMapper hlsCreditPlanMapper;
    @Autowired
    private HlsCusPrjProjectChanceMpMapper hlsCusPrjProjectChanceMpMapper;
    @Autowired
    private TaskNewMapper taskNewMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private HlsCusChangeReqInfoMapper hlsCusChangeReqInfoMapper;
    @Autowired
    private ConChangeEtInfoMapper conChangeEtInfoMapper;
    @Autowired
    private ConChangeRepaymentInfoMapper conChangeRepaymentInfoMapper;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired(
            required = false
    )
    private CloudStorageService cloudStorageService;
    @Autowired
    private WorkflowUtils workflowUtils;
    @Value("${workflow.encryptStr:}")
    private String encryptKey;

    @Value("${workflow.systemHost:}")
    private String systemHost;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Long businessKey;
    //访问接口
    @Value("${intfPlatform.base}")
    private  String base;
    @Value("${intfPlatform.accessTokenUrl}")
    private  String accessTokenUrl;
    @Value("${intfPlatform.todoSendUrl}")
    private  String todoSendUrl;
    @Value("${intfPlatform.toRenewUrl}")
    private String toRenewUrl;
    @Value("${intfPlatform.todoSendUrlBefor}")
    private String todoSendUrlBefor;
    @Value("${intfPlatform.initializeAttachment}")
    private String initializeAttachment;
    @Value("${intfPlatform.attachmentUpload}")
    private String attachmentUpload;

    public static final String SYSTEM_NAME = "融资租赁";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(",###.##");

    public ActivitiSysEventUtils() {
    }

    public void addEvent(DelegateTask delegateTask, String eventCode) {
        TaskEntity task = (TaskEntity)delegateTask;
        ExecutionEntity taskExecution = task.getExecution();
        String pName = (String)taskExecution.getVariable("pName");
        IRequest iRequest = (IRequest)taskExecution.getVariable("iRequest");
        Long documentId = (Long)taskExecution.getVariable("documentId");
        String documentCategory = (String)taskExecution.getVariable("documentCategory");
        String documentType = (String)taskExecution.getVariable("documentType");
        String approveResultDesc = (String)taskExecution.getVariable("approveResultDesc");
        String approveResult = (String)taskExecution.getVariable("approveResult");
        String assignee = task.getAssignee();
        String startUser = task.getProcessInstance().getStartUserId();
        User sysUser = null;
        Map<String, Object> params = new HashMap();

        String desc = task.getDescription();
        if (taskExecution.getVariable("pName") != null && taskExecution.getVariable("startEmpName") != null && taskExecution.getVariable("documentName") != null) {
            desc = taskExecution.getVariable("startEmpName") + "提交了名为" + taskExecution.getVariable("documentName") + "的" + taskExecution.getVariable("pName");
        }
        User startEmp = this.userService.queryUserByAllocationId(startUser);
        User assigneeEmp = this.userService.queryUserByAllocationId(assignee);
        String startEmpName="";
        String assigneeName="";

        if(startEmp==null){
            SysUser user = sysUserMapper.selectUserNameById(Long.valueOf(startUser));
            startEmpName= user.getDescription();
        }else{
            startEmp = userMapper.selectByPrimaryKey(startEmp.getUserId());
            startEmpName=startEmp.getDescription();
        }
        if(assigneeEmp==null){
            SysUser user = sysUserMapper.selectUserNameById(Long.valueOf(assignee));
            assigneeName= user.getDescription();
        }else{
            assigneeEmp = userMapper.selectByPrimaryKey(assigneeEmp.getUserId());
            assigneeName=assigneeEmp.getDescription();
        }
        if(approveResult==null){
            if(desc!=null){
                desc = startEmpName + "提交了名为" + taskExecution.getVariable("documentName") + "的" + taskExecution.getVariable("pName");
            }
        }else if("APPROVED".equals(approveResult) ||"APPOINT".equals(approveResult)){
            desc = assigneeName + "通过了名为" + taskExecution.getVariable("documentName") + "的" + taskExecution.getVariable("pName");
        }else{
            desc = assigneeName + "退回了名为" + taskExecution.getVariable("documentName") + "的" + taskExecution.getVariable("pName");
        }

        if (approveResultDesc != null) {
            params.put("message", "流程：" + desc + "－ 节点：" + task.getName() + " - 审批意见：" + approveResultDesc);
        } else {
            params.put("message", "流程：" + desc + "－ 节点：" + task.getName());
        }

        params.put("taskId", task.getId());
        params.put("eventCode", eventCode);
        Long userId;
        String url;
        if (StringUtils.isNotEmpty(assignee) && eventCode.equals("WFL.TO_DO")) {
            sysUser = this.userService.queryUserByAllocationId(assignee);
            if (sysUser != null) {
                userId = sysUser.getUserId();
                iRequest.setUserId(userId);
                params.put("noticeTitle", "工作流待办-" + pName + "-" + task.getName());
                params.put("noticeType", "TODO");
                params.put("level", 1);
                url = "/MYWFL/MYWFL001/task_detail.lview?taskId=" + task.getId() + "&processInstanceId=" + task.getProcessInstanceId();
                params.put("url", url);
                params.put("allocation_id", assignee);
                this.sysEventService.createEvent(iRequest, documentId, documentCategory, documentType, params);
                //企业微信发送待办
                weChartTodo(startUser,userId,pName,task,taskExecution, eventCode);
            }
        }

        //20230217:hotfix-修改审批人传递错误的问题
        if (StringUtils.isNotEmpty(assignee) && eventCode.equals("WFL.APPROVE")) {
            sysUser = this.userService.queryUserByAllocationId(assignee);
            if (sysUser != null) {
                userId = sysUser.getUserId();
                iRequest.setUserId(userId);
                params.put("noticeTitle", "工作流通知-" + pName + "-" + task.getName());
                params.put("noticeType", "NOTICE");
                params.put("level", 1);
                url = "/WFL/WFL003/process_instance_detail.lview?taskId=" + task.getId() + "&id=" + task.getProcessInstanceId();
                params.put("url", url);
                params.put("allocation_id", startUser);
                this.sysEventService.createEvent(iRequest, documentId, documentCategory, documentType, params);
                weChartTodo(startUser,userId,pName,task,taskExecution, eventCode);
            }
        }

    }

    //企业微信发送待办
    private void weChartTodo(String startUserStr,Long userId,String pName,TaskEntity task,ExecutionEntity taskExecution,String eventCode){
        try {
            if(task.getProcessInstance().getBusinessKey()!=null&&task.getProcessInstance().getBusinessKey()!=""){
                businessKey= Long.valueOf((task.getProcessInstance().getBusinessKey()));
            }
            User startUser = this.userService.queryUserByAllocationId(startUserStr);
            startUser = userMapper.selectByPrimaryKey(startUser.getUserId());
            String title = String.valueOf(task.getProcessInstance().getVariable("creditLineName"))+"-"+pName;
            if("null".equals(title)||null==title){
                title = String.valueOf(task.getProcessInstance().getVariable("projectName"))+"-"+pName;
            }
            if("null".equals(title)||null==title){
                title = String.valueOf(task.getProcessInstance().getVariable("documentName"))+"-"+pName;
            }
            if(pName.contains("保证金代付期中租金审批流程")){
                title=String.valueOf(task.getProcessInstance().getVariable("contractName"))+"-保证金代付期中租金";
            }else if(pName.contains("保证金处理方式变更审批流程")){
                title=String.valueOf(task.getProcessInstance().getVariable("contractName"))+"-保证金处理方式变更";
            }
            User approveUser = userMapper.selectByPrimaryKey(userId);
            String dealUrl="";
            String remark = (String)taskExecution.getVariable("comment");
            String approveResult = (String)taskExecution.getVariable("approveResult");

            String url = workflowUtils.generateDealUrl(task.getId(),task.getProcessInstanceId());
            String flag="RZZL/YHZL-";
            JSONObject mobiles = new JSONObject(new LinkedHashMap());
            mobiles.put("dataType","TODO");
            mobiles.put("systemCode","ZB-LBS");

            String titleName = SYSTEM_NAME+"-"+pName;
            mobiles.put("title",titleName);
            mobiles.put("description",taskExecution.getVariable("documentName"));
            mobiles.put("sourceId", flag+task.getId());
            mobiles.put("docmentNum",task.getProcessInstance().getVariable("documentNumber"));
            mobiles.put("instanceId",flag+taskExecution.getProcessInstanceId());
            mobiles.put("relationInstanceId","");
            mobiles.put("workflowId",task.getProcessDefinitionId());
            mobiles.put("workflowName",StringUtils.isEmpty(title)?"":title.replace("null-","").replace("null",""));
            mobiles.put("nodeId",taskExecution.getActivityId());
            mobiles.put("nodeName",task.getName());

            String userName = approveUser.getUserName();
            mobiles.put("receiver",userName);
            if(eventCode.equals("WFL.TO_DO")){
                mobiles.put("status","UNAPPROVED");
                dealUrl=todoSendUrl;
            }else{
                dealUrl=toRenewUrl;
                logger.info("单据审批同步：{}",taskExecution.getVariables().toString());
                mobiles.put("remark",remark);
                mobiles.put("approveUser",userName);
                if("APPROVED".equals(approveResult)){
                    mobiles.put("status","APPROVED");
                }else{
                    mobiles.put("status","REJECTED");
                }
            }
            mobiles.put("initiator",startUser.getUserName());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(System.currentTimeMillis());
            mobiles.put("initiationDate",format);
            if(eventCode.equals("WFL.TO_DO")&&(("项目评审审议流程".equals(pName)&&task.getName().contains("指派"))||("放款申请审批流程".equals(pName)&&task.getName().contains("财务岗审核")))){
                mobiles.put("mobileFlag",false);
            }else {
                mobiles.put("mobileFlag",true);
            }
            mobiles.put("dealUrl",url);
            JSONArray content=new JSONArray();
            boolean isToPhone=true;
            switch (pName){
                case "项目立项审批流程":
                    content=hlsCusHlsCreditLineChanceProcess(); break;
                case "项目评审审议流程":
                    if(task.getName().contains("风控秘书岗")){
                        isToPhone=false;
                        break;
                    }
                    content=hlsCusPriProjectProcess(task.getName());break;
                case "合同支付表确认审批流程":
                    content=hlsCusConContractProcess();break;
                case "放款申请审批流程":
                    content=hlsCusCshPaymentReqHdProcess();break;
                case "罚息减免流程":
                    content=hlsCusCshContractCashflowProcess();break;
                case "保证金处理方式变更审批流程":
                    content=depositManageHdProcess();break;
                case "保证金代付期中租金审批流程":
                    content=depositManageHdProcess2();break;
                case "授信变更流程":
                    content=creditChangeProcess();break;
                case "合同变更流程":
                    content=contractChangeProcess();break;
                case "付款申请流程":
                    content=paymentApplication();break;
                case "零售业务合同变更流程":
                    content=contractChangeProcess2();break;
                case "租后检查流程":
                    content=hlsPrjCheckProcess();break;
                case "零售业务租后检查流程":
                    content=hlsRetailPrjCheckProcess();break;
                case "投放审查审批流程":
                    content=hlsSigProjectProcess();break;
                case "进件审批流程":
                    content=retailProjectProcess();break;
                default:
                    isToPhone=false;
            }
            if(isToPhone){
                mobiles.put("content",content);
                logger.info(mobiles.toString());
                //appWflTodoNoticeService.sendTodoNotice(Long.valueOf(task.getId()), String.valueOf(userId),mobiles);
                JSONObject responseJsonObject = interfacePlatformUtils.getInterfaceRequest(mobiles, dealUrl,"移动端审批");
                System.out.println(responseJsonObject);
                logger.info("移动端待办待阅接口--------->"+responseJsonObject);
            }
        } catch (Exception e) {
            logger.error("----------企业微信待办发送失败-----------");
            e.printStackTrace();
        }
    }

    //合同变更中四个页面共有的附件和退回按钮
    private JSONArray connContractChange(List<Map> prjProjects,int order){
        JSONArray jsonArray=new JSONArray();
        //附件资料
        JSONObject content4=new JSONObject(new LinkedHashMap());
        JSONObject header4=new JSONObject(new LinkedHashMap());
        header4.put("name","附件资料");
        header4.put("icon","profile");
        header4.put("orderSeq",order+1);
        header4.put("hasChildren",true);
        header4.put("isShow",true);
        content4.put("header",header4);
        JSONArray lines3=new JSONArray();
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment=new HlsCusPrjProjectAttachment();
        hlsCusPrjProjectAttachment.setProjectId(Long.valueOf(prjProjects.get(0).get("project_id").toString()));
        List<Map> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.selectContractChangeAttachmentInfo1(hlsCusPrjProjectAttachment);

        for (int j = 0; j < hlsCusPrjProjectAttachments.size(); j++){
            JSONArray lines31=new JSONArray();

            String[] attachmentIds = hlsCusPrjProjectAttachments.get(j).get("attachmentId").toString().split(",");
            String[] fileNames = hlsCusPrjProjectAttachments.get(j).get("fileName").toString().split(",");
            for (int i = 0; i < attachmentIds.length; i++) {
                FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[0]);
                JSONObject line41=new JSONObject(new LinkedHashMap());
                line41.put("fieldDescription",hlsCusPrjProjectAttachments.get(j).get("documentName"));
                line41.put("fieldName","documentName");
                line41.put("fieldType","Text");
                line41.put("fieldValue",fndAttachment.getFileName());
                line41.put("orderSeq",1);
                line41.put("isEdit",false);
                line41.put("isShow",true);
                line41.put("nullableFlag",true);

                JSONObject line42=new JSONObject(new LinkedHashMap());
                line42.put("fieldDescription",fileNames[i]);
                line42.put("fieldName","fileName");
                line42.put("fieldType","File");
                line42.put( "fileTransferType", "UUID");
                line42.put("fieldValue",fndAttachment.getUUID());
                line42.put("orderSeq",2);
                line42.put("isEdit",false);
                line42.put("isShow",true);
                line42.put("nullableFlag",true);

                lines31.add(line41);
                lines31.add(line42);
            }
            lines3.add(lines31);
        }
        content4.put("lines",lines3);

        //退回按钮
        JSONObject content5=new JSONObject(new LinkedHashMap());
        JSONObject header5=new JSONObject(new LinkedHashMap());
        header5.put("name","approve");
        header5.put("orderSeq",order+2);
        header5.put("hasChildren",false);
        content5.put("header",header5);
        JSONArray lines4=new JSONArray();

        JSONObject line51=new JSONObject(new LinkedHashMap());
        line51.put("fieldDescription","退回");
        line51.put("fieldName","sendback");
        line51.put("fieldType","Text");
        line51.put("fieldValue","SENDBACK");
        line51.put("orderSeq",1);
        line51.put("isEdit",false);
        line51.put("isShow",true);
        line51.put("nullableFlag",false);

        lines4.add(line51);
        content5.put("lines",lines4);

        jsonArray.add(content4);
        jsonArray.add(content5);

        return jsonArray;
    }

    //零售业务合同变更中几个页面共有的附件和退回按钮
    private JSONArray connContractChange2(Long contractId,int order){
        JSONArray jsonArray=new JSONArray();
        //附件资料
        JSONObject content4=new JSONObject(new LinkedHashMap());
        JSONObject header4=new JSONObject(new LinkedHashMap());
        header4.put("name","附件资料");
        header4.put("icon","profile");
        header4.put("orderSeq",order+1);
        header4.put("hasChildren",true);
        header4.put("isShow",true);
        content4.put("header",header4);
        JSONArray lines3=new JSONArray();
        HlsCusContractAttachment hlsCusContractAttachment=new HlsCusContractAttachment();
        hlsCusContractAttachment.setContractId(contractId);
        List<HlsCusContractAttachment> hlsCusContractAttachments = hlsCusContractAttachmentMapper.queryChangeAttachmentByCategory(hlsCusContractAttachment);

//        for (int j = 0; j < hlsCusContractAttachments.size(); j++){
//            JSONArray lines31=new JSONArray();
//
//            String[] attachmentIds = hlsCusContractAttachments.get(j).getAttachmentId().toString().split(",");
//            String[] fileNames = hlsCusContractAttachments.get(j).getFileNames().split(",");
//            for (int i = 0; i < attachmentIds.length; i++) {
//                FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[0]);
//                JSONObject line41=new JSONObject(new LinkedHashMap());
//                line41.put("fieldDescription",hlsCusContractAttachments.get(j).getDocumentName());
//                line41.put("fieldName","documentName");
//                line41.put("fieldType","Text");
//                line41.put("fieldValue",fileNames[i]);
//                line41.put("orderSeq",1);
//                line41.put("isEdit",false);
//                line41.put("isShow",true);
//                line41.put("nullableFlag",true);
//
//                JSONObject line42=new JSONObject(new LinkedHashMap());
//                line42.put("fieldDescription",fileNames[i]);
//                line42.put("fieldName","fileName");
//                line42.put("fieldType","File");
//                line42.put( "fileTransferType", "UUID");
//                line42.put("fieldValue",fndAttachment.getUUID());
//                line42.put("orderSeq",2);
//                line42.put("isEdit",false);
//                line42.put("isShow",true);
//                line42.put("nullableFlag",true);
//
//                JSONObject line43=new JSONObject(new LinkedHashMap());
//                line43.put("fieldDescription",hlsCusContractAttachments.get(j).getDescription());
//                line43.put("fieldName","documentName");
//                line43.put("fieldType","Text");
//                line43.put("fieldValue",hlsCusContractAttachments.get(j).getDescription());
//                line43.put("orderSeq",3);
//                line43.put("isEdit",false);
//                line43.put("isShow",true);
//                line43.put("nullableFlag",true);
//
//                lines31.add(line41);
//                lines31.add(line42);
//                lines31.add(line43);
//            }
//            lines3.add(lines31);
//        }
//        content4.put("lines",lines3);

        //退回按钮
        JSONObject content5=new JSONObject(new LinkedHashMap());
        JSONObject header5=new JSONObject(new LinkedHashMap());
        header5.put("name","approve");
        header5.put("orderSeq",order+2);
        header5.put("hasChildren",false);
        content5.put("header",header5);
        JSONArray lines4=new JSONArray();

        JSONObject line51=new JSONObject(new LinkedHashMap());
        line51.put("fieldDescription","退回");
        line51.put("fieldName","sendback");
        line51.put("fieldType","Text");
        line51.put("fieldValue","SENDBACK");
        line51.put("orderSeq",1);
        line51.put("isEdit",false);
        line51.put("isShow",true);
        line51.put("nullableFlag",false);

        lines4.add(line51);
        content5.put("lines",lines4);

        jsonArray.add(content4);
        jsonArray.add(content5);

        return jsonArray;
    }

    //合同变更
    private JSONArray contractChangeProcess(){
        try{
            JSONArray content=new JSONArray();
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusPrjProject prjProject =new HlsCusPrjProject();
            prjProject.setProjectId(businessKey);
            List<Map> prjProjects = hlsCusPrjProjectMapper.queryPrjDetailNew1(prjProject);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","合同名称");
            line1.put("fieldName","contractName");
            line1.put("fieldType","Text");
            line1.put("fieldValue",prjProjects.get(0).get("contractName"));
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","业务类型");
            line2.put("fieldName","businessTypeN");
            line2.put("fieldType","Text");
            line2.put("fieldValue",prjProjects.get(0).get("businessTypeN"));
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","行业分类");
            line3.put("fieldName","industryTypeN");
            line3.put("fieldType","Text");
            line3.put("fieldValue",prjProjects.get(0).get("industryTypeN"));
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","签署日期");
            line4.put("fieldName","signDate");
            line4.put("fieldType","Text");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String dateForm="";
            if(prjProjects.get(0).get("sign_date")!=null){
                dateForm = String.valueOf(prjProjects.get(0).get("sign_date"));
            }
            line4.put("fieldValue",dateForm);
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","业务板块");
            line5.put("fieldName","industryN");
            line5.put("fieldType","Text");
            line5.put("fieldValue",prjProjects.get(0).get("industryN"));
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","项目所在地");
            line6.put("fieldName","singAddr");
            line6.put("fieldType","Text");
            line6.put("fieldValue",prjProjects.get(0).get("singAddr"));
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","项目主办");
            line7.put("fieldName","hostProjectManagerN");
            line7.put("fieldType","Text");
            line7.put("fieldValue",prjProjects.get(0).get("hostProjectManagerN"));
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);

            content1.put("lines",lines);
            content.add(content1);

            //变更说明
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","变更说明");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();
            List<HlsCusChangeReqInfo> HlsCusChangeReqInfos = hlsCusChangeReqInfoMapper.queryChangeInfoByChangeReqId1(Long.valueOf(prjProjects.get(0).get("change_req_id").toString()));

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","变更说明");
            line21.put("fieldName","changeInfoDesc");
            line21.put("fieldType","Text");
            line21.put("fieldValue",HlsCusChangeReqInfos.get(0).getChangeInfoDesc());
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","是否上会");
            line22.put("fieldName","meetFlagN");
            line22.put("fieldType","Text");
            line22.put("fieldValue",HlsCusChangeReqInfos.get(0).getMeetFlagN());
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","变更编号");
            line23.put("fieldName","approveNumber");
            line23.put("fieldType","Text");
            line23.put("fieldValue",HlsCusChangeReqInfos.get(0).getApproveNumber());
            line23.put("orderSeq",2);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            lines1.add(line21);
            lines1.add(line22);
            lines1.add(line23);

            content2.put("lines",lines1);
            content.add(content2);

            if("ET".equals(prjProjects.get(0).get("change_type").toString())){
                //提前结清信息
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","提前结清信息");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",false);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                ConChangeEtInfo conChangeEtInfo=new ConChangeEtInfo();
                conChangeEtInfo.setProjectId(Long.valueOf(prjProjects.get(0).get("project_id").toString()));
                List<ConChangeEtInfo> conChangeEtInfos = conChangeEtInfoMapper.queryChangeEtInfo(conChangeEtInfo);

                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","提前结清日");
                line31.put("fieldName","etDate");
                line31.put("fieldType","Text");
                line31.put("fieldValue",conChangeEtInfos.get(0).getEtDate()==null?"":sdf.format(conChangeEtInfos.get(0).getEtDate()));
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","未还本金总额");
                line32.put("fieldName","unreceivedPrincipal");
                line32.put("fieldType","Text");
                line32.put("fieldValue",conChangeEtInfos.get(0).getUnreceivedPrincipal()==null?0:conChangeEtInfos.get(0).getUnreceivedPrincipal());
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","至提前结清日利息");
                line33.put("fieldName","etInterest");
                line33.put("fieldType","Text");
                line33.put("fieldValue",conChangeEtInfos.get(0).getEtInterest()==null?0:conChangeEtInfos.get(0).getEtInterest());
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                JSONObject line34=new JSONObject(new LinkedHashMap());
                line34.put("fieldDescription","留购价款");
                line34.put("fieldName","nominalCost");
                line34.put("fieldType","Text");
                line34.put("fieldValue",conChangeEtInfos.get(0).getNominalCost()==null?0:conChangeEtInfos.get(0).getNominalCost());
                line34.put("orderSeq",4);
                line34.put("isEdit",false);
                line34.put("isShow",true);
                line34.put("nullableFlag",true);

                JSONObject line35=new JSONObject(new LinkedHashMap());
                line35.put("fieldDescription","提前结清手续费");
                line35.put("fieldName","etFee");
                line35.put("fieldType","Text");
                line35.put("fieldValue",conChangeEtInfos.get(0).getEtFee()==null?0:conChangeEtInfos.get(0).getEtFee());
                line35.put("orderSeq",5);
                line35.put("isEdit",false);
                line35.put("isShow",true);
                line35.put("nullableFlag",true);

                JSONObject line36=new JSONObject(new LinkedHashMap());
                line36.put("fieldDescription","违约金");
                line36.put("fieldName","liquidatedDamages");
                line36.put("fieldType","Text");
                line36.put("fieldValue",conChangeEtInfos.get(0).getLiquidatedDamages()==null?0:conChangeEtInfos.get(0).getLiquidatedDamages());
                line36.put("orderSeq",6);
                line36.put("isEdit",false);
                line36.put("isShow",true);
                line36.put("nullableFlag",true);

                JSONObject line37=new JSONObject(new LinkedHashMap());
                line37.put("fieldDescription","减免金额");
                line37.put("fieldName","reduceAmount");
                line37.put("fieldType","Text");
                line37.put("fieldValue",conChangeEtInfos.get(0).getReduceAmount()==null?0:conChangeEtInfos.get(0).getReduceAmount());
                line37.put("orderSeq",7);
                line37.put("isEdit",false);
                line37.put("isShow",true);
                line37.put("nullableFlag",true);

                JSONObject line38=new JSONObject(new LinkedHashMap());
                line38.put("fieldDescription","实际应结清金额");
                line38.put("fieldName","totalAmount");
                line38.put("fieldType","Text");
                line38.put("fieldValue",conChangeEtInfos.get(0).getTotalAmount()==null?0:conChangeEtInfos.get(0).getTotalAmount());
                line38.put("orderSeq",8);
                line38.put("isEdit",false);
                line38.put("isShow",true);
                line38.put("nullableFlag",true);

                JSONObject line39=new JSONObject(new LinkedHashMap());
                line39.put("fieldDescription","起租日");
                line39.put("fieldName","leaseStartDate");
                line39.put("fieldType","Text");
                line39.put("fieldValue",conChangeEtInfos.get(0).getLeaseStartDate()==null?"":sdf.format(conChangeEtInfos.get(0).getLeaseStartDate()));
                line39.put("orderSeq",8);
                line39.put("isEdit",false);
                line39.put("isShow",true);
                line39.put("nullableFlag",true);

                lines2.add(line31);
                lines2.add(line32);
                lines2.add(line33);
                lines2.add(line34);
                lines2.add(line35);
                lines2.add(line36);
                lines2.add(line37);
                lines2.add(line38);
                lines2.add(line39);

                content3.put("lines",lines2);
                content.add(content3);

                //现金流信息
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","现金流信息");
                header4.put("icon","profile");
                header4.put("orderSeq",3);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                HlsCusConContractCashflow cashflow=new HlsCusConContractCashflow();
                cashflow.setQuotationId(Long.valueOf(prjProjects.get(0).get("quotation_id").toString()));
                List<Map> cashflowInfoByPrj = hlsCusConContractCashflowMapper.selectCashflowInfoByPrj(cashflow);

                for (int i = 0; i < cashflowInfoByPrj.size(); i++) {
                    JSONArray lines31=new JSONArray();

                    JSONObject line41=new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription","期数");
                    line41.put("fieldName","times");
                    line41.put("fieldType","Text");
                    line41.put("fieldValue",cashflowInfoByPrj.get(i).get("times"));
                    line41.put("orderSeq",1);
                    line41.put("isEdit",false);
                    line41.put("isShow",true);
                    line41.put("nullableFlag",true);

                    JSONObject line42=new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription","现金流项目");
                    line42.put("fieldName","cfItem");
                    line42.put("fieldType","Text");
                    line42.put("fieldValue",cashflowInfoByPrj.get(i).get("cfItem"));
                    line42.put("orderSeq",2);
                    line42.put("isEdit",false);
                    line42.put("isShow",true);
                    line42.put("nullableFlag",true);

                    JSONObject line43=new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription","支付日期");
                    line43.put("fieldName","dueDate");
                    line43.put("fieldType","Text");
                    String dueDate = sdf.format(cashflowInfoByPrj.get(i).get("dueDate"));
                    line43.put("fieldValue",dueDate);
                    line43.put("orderSeq",3);
                    line43.put("isEdit",false);
                    line43.put("isShow",true);
                    line43.put("nullableFlag",true);

                    JSONObject line44=new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription","租金（含税）");
                    line44.put("fieldName","dueAmount");
                    line44.put("fieldType","Text");
                    line44.put("fieldValue",cashflowInfoByPrj.get(i).get("dueAmount"));
                    line44.put("orderSeq",4);
                    line44.put("isEdit",false);
                    line44.put("isShow",true);
                    line44.put("nullableFlag",true);

                    JSONObject line45=new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription","租金（不含税）");
                    line45.put("fieldName","netDueAmount");
                    line45.put("fieldType","Text");
                    line45.put("fieldValue",cashflowInfoByPrj.get(i).get("netDueAmount"));
                    line45.put("orderSeq",5);
                    line45.put("isEdit",false);
                    line45.put("isShow",true);
                    line45.put("nullableFlag",true);

                    JSONObject line46=new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription","租金（元）");
                    line46.put("fieldName","vatDueAmount");
                    line46.put("fieldType","Text");
                    line46.put("fieldValue",cashflowInfoByPrj.get(i).get("vatDueAmount"));
                    line46.put("orderSeq",6);
                    line46.put("isEdit",false);
                    line46.put("isShow",true);
                    line46.put("nullableFlag",true);

                    JSONObject line47=new JSONObject(new LinkedHashMap());
                    line47.put("fieldDescription","本金（元）");
                    line47.put("fieldName","thePrincipal");
                    line47.put("fieldType","Text");
                    line47.put("fieldValue",cashflowInfoByPrj.get(i).get("thePrincipal"));
                    line47.put("orderSeq",7);
                    line47.put("isEdit",false);
                    line47.put("isShow",true);
                    line47.put("nullableFlag",true);

                    JSONObject line48=new JSONObject(new LinkedHashMap());
                    line48.put("fieldDescription","利息（元）");
                    line48.put("fieldName","theInterest");
                    line48.put("fieldType","Text");
                    line48.put("fieldValue",cashflowInfoByPrj.get(i).get("theInterest"));
                    line48.put("orderSeq",8);
                    line48.put("isEdit",false);
                    line48.put("isShow",true);
                    line48.put("nullableFlag",true);

                    JSONObject line49=new JSONObject(new LinkedHashMap());
                    line49.put("fieldDescription","手续费（元）");
                    line49.put("fieldName","theLeaseCharge");
                    line49.put("fieldType","Text");
                    line49.put("fieldValue",cashflowInfoByPrj.get(i).get("theLeaseCharge")==null?"":cashflowInfoByPrj.get(i).get("theLeaseCharge"));
                    line49.put("orderSeq",9);
                    line49.put("isEdit",false);
                    line49.put("isShow",true);
                    line49.put("nullableFlag",true);

                    JSONObject line410=new JSONObject(new LinkedHashMap());
                    line410.put("fieldDescription","租前息（元）");
                    line410.put("fieldName","rhePreInterest");
                    line410.put("fieldType","Text");
                    line410.put("fieldValue",cashflowInfoByPrj.get(i).get("rhePreInterest")==null?"":cashflowInfoByPrj.get(i).get("rhePreInterest"));
                    line410.put("orderSeq",10);
                    line410.put("isEdit",false);
                    line410.put("isShow",true);
                    line410.put("nullableFlag",true);

                    JSONObject line411=new JSONObject(new LinkedHashMap());
                    line411.put("fieldDescription","首付款（元）");
                    line411.put("fieldName","theFirstPayment");
                    line411.put("fieldType","Text");
                    line411.put("fieldValue",cashflowInfoByPrj.get(i).get("theFirstPayment")==null?"":cashflowInfoByPrj.get(i).get("theFirstPayment"));
                    line411.put("orderSeq",11);
                    line411.put("isEdit",false);
                    line411.put("isShow",true);
                    line411.put("nullableFlag",true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);
                    lines31.add(line47);
                    lines31.add(line48);
                    lines31.add(line49);
                    lines31.add(line410);
                    lines31.add(line411);

                    lines3.add(lines31);
                }
                content4.put("lines",lines3);
                content.add(content4);
                JSONArray jsonArray = connContractChange(prjProjects,4);
                content.add(jsonArray.get(0));
                content.add(jsonArray.get(1));
            }else if("REPAYMENT_SCHEDULE".equals(prjProjects.get(0).get("change_type").toString())){
                //报价方案
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","报价方案");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",false);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                HlsCusPrjQuotation hlsCusPrjQuotation=new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setSourceDocumentId(businessKey);
                List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(hlsCusPrjQuotation);

                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","报价方案");
                line31.put("fieldName","priceListN");
                line31.put("fieldType","Text");
                line31.put("fieldValue",hlsCusPrjQuotations.get(0).getPriceListN());
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","项目金额（元）");
                line32.put("fieldName","leaseItemAmount");
                line32.put("fieldType","Text");
                line32.put("fieldValue",hlsCusPrjQuotations.get(0).getLeaseItemAmount());
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","投放日");
                line33.put("fieldName","leaseStartDate");
                line33.put("fieldType","Text");
                String leaseStartDate = sdf.format(hlsCusPrjQuotations.get(0).getLeaseStartDate());
                line33.put("fieldValue",leaseStartDate);
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                JSONObject line34=new JSONObject(new LinkedHashMap());
                line34.put("fieldDescription","租期（年）");
                line34.put("fieldName","leaseTerm");
                line34.put("fieldType","Text");
                line34.put("fieldValue",hlsCusPrjQuotations.get(0).getLeaseTerm());
                line34.put("orderSeq",4);
                line34.put("isEdit",false);
                line34.put("isShow",true);
                line34.put("nullableFlag",true);

                JSONObject line35=new JSONObject(new LinkedHashMap());
                line35.put("fieldDescription","利率类型");
                line35.put("fieldName","intRateTypeN");
                line35.put("fieldType","Text");
                line35.put("fieldValue",hlsCusPrjQuotations.get(0).getIntRateTypeN());
                line35.put("orderSeq",5);
                line35.put("isEdit",false);
                line35.put("isShow",true);
                line35.put("nullableFlag",true);

                JSONObject line36=new JSONObject(new LinkedHashMap());
                line36.put("fieldDescription","年利率（%）");
                line36.put("fieldName","intRate");
                line36.put("fieldType","Text");
                line36.put("fieldValue",hlsCusPrjQuotations.get(0).getIntRate()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotations.get(0).getIntRate()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line36.put("orderSeq",6);
                line36.put("isEdit",false);
                line36.put("isShow",true);
                line36.put("nullableFlag",true);

                JSONObject line37=new JSONObject(new LinkedHashMap());
                line37.put("fieldDescription","首付款（元）");
                line37.put("fieldName","downPayment");
                line37.put("fieldType","Text");
                line37.put("fieldValue",hlsCusPrjQuotations.get(0).getDownPayment()==null?0:hlsCusPrjQuotations.get(0).getDownPayment());
                line37.put("orderSeq",7);
                line37.put("isEdit",false);
                line37.put("isShow",true);
                line37.put("nullableFlag",true);

                JSONObject line38=new JSONObject(new LinkedHashMap());
                line38.put("fieldDescription","保证金（元）");
                line38.put("fieldName","deposit");
                line38.put("fieldType","Text");
                line38.put("fieldValue",hlsCusPrjQuotations.get(0).getDeposit()==null?0:hlsCusPrjQuotations.get(0).getDeposit());
                line38.put("orderSeq",8);
                line38.put("isEdit",false);
                line38.put("isShow",true);
                line38.put("nullableFlag",true);

                JSONObject line39=new JSONObject(new LinkedHashMap());
                line39.put("fieldDescription","收租间隔月份");
                line39.put("fieldName","rentingFrequency");
                line39.put("fieldType","Text");
                line39.put("fieldValue",hlsCusPrjQuotations.get(0).getRentingFrequency());
                line39.put("orderSeq",9);
                line39.put("isEdit",false);
                line39.put("isShow",true);
                line39.put("nullableFlag",true);

                JSONObject line310=new JSONObject(new LinkedHashMap());
                line310.put("fieldDescription","留购价款（元）");
                line310.put("fieldName","residualValue");
                line310.put("fieldType","Text");
                line310.put("fieldValue",hlsCusPrjQuotations.get(0).getResidualValue());
                line310.put("orderSeq",10);
                line310.put("isEdit",false);
                line310.put("isShow",true);
                line310.put("nullableFlag",true);

                JSONObject line311=new JSONObject(new LinkedHashMap());
                line311.put("fieldDescription","XIRR");
                line311.put("fieldName","xirr");
                line311.put("fieldType","Text");
                line311.put("fieldValue",hlsCusPrjQuotations.get(0).getXirr()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotations.get(0).getXirr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line311.put("orderSeq",11);
                line311.put("isEdit",false);
                line311.put("isShow",true);
                line311.put("nullableFlag",true);

                JSONObject line312=new JSONObject(new LinkedHashMap());
                line312.put("fieldDescription","IRR");
                line312.put("fieldName","irr");
                line312.put("fieldType","Text");
                line312.put("fieldValue",hlsCusPrjQuotations.get(0).getIrr()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotations.get(0).getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line312.put("orderSeq",12);
                line312.put("isEdit",false);
                line312.put("isShow",true);
                line312.put("nullableFlag",true);

                JSONObject line313=new JSONObject(new LinkedHashMap());
                line313.put("fieldDescription","每期租(本)金（元）");
                line313.put("fieldName","pmt");
                line313.put("fieldType","Text");
                line313.put("fieldValue",hlsCusPrjQuotations.get(0).getPmt());
                line313.put("orderSeq",13);
                line313.put("isEdit",false);
                line313.put("isShow",true);
                line313.put("nullableFlag",true);

                lines2.add(line31);
                lines2.add(line32);
                lines2.add(line33);
                lines2.add(line34);
                lines2.add(line35);
                lines2.add(line36);
                lines2.add(line37);
                lines2.add(line38);
                lines2.add(line39);
                lines2.add(line310);
                lines2.add(line311);
                lines2.add(line312);
                lines2.add(line313);

                content3.put("lines",lines2);
                content.add(content3);

                //变更信息
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","变更信息");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",false);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                ConChangeRepaymentInfo conChangeRepaymentInfo=new ConChangeRepaymentInfo();
                conChangeRepaymentInfo.setProjectId(Long.valueOf(prjProjects.get(0).get("project_id").toString()));
                List<ConChangeRepaymentInfo> conChangeRepaymentInfos = conChangeRepaymentInfoMapper.queryChangeRepaymentInfo(conChangeRepaymentInfo);

                JSONObject line41=new JSONObject(new LinkedHashMap());
                line41.put("fieldDescription","变更起始日");
                line41.put("fieldName","changeStartDate");
                line41.put("fieldType","Text");

                String changeStartDate = sdf.format(conChangeRepaymentInfos.get(0).getChangeStartDate());
                line41.put("fieldValue",changeStartDate);
                line41.put("orderSeq",1);
                line41.put("isEdit",false);
                line41.put("isShow",true);
                line41.put("nullableFlag",true);

                JSONObject line42=new JSONObject(new LinkedHashMap());
                line42.put("fieldDescription","变更起始期数");
                line42.put("fieldName","changeStartTimes");
                line42.put("fieldType","Text");
                line42.put("fieldValue",conChangeRepaymentInfos.get(0).getChangeStartTimes());
                line42.put("orderSeq",2);
                line42.put("isEdit",false);
                line42.put("isShow",true);
                line42.put("nullableFlag",true);

                JSONObject line43=new JSONObject(new LinkedHashMap());
                line43.put("fieldDescription","变更后总期数");
                line43.put("fieldName","afterTotalTimes");
                line43.put("fieldType","Text");
                line43.put("fieldValue",conChangeRepaymentInfos.get(0).getAfterTotalTimes());
                line43.put("orderSeq",3);
                line43.put("isEdit",false);
                line43.put("isShow",true);
                line43.put("nullableFlag",true);

                JSONObject line44=new JSONObject(new LinkedHashMap());
                line44.put("fieldDescription","变更前总期数");
                line44.put("fieldName","beforeTotalTimes");
                line44.put("fieldType","Text");
                line44.put("fieldValue",conChangeRepaymentInfos.get(0).getBeforeTotalTimes());
                line44.put("orderSeq",4);
                line44.put("isEdit",false);
                line44.put("isShow",true);
                line44.put("nullableFlag",true);

                JSONObject line45=new JSONObject(new LinkedHashMap());
                line45.put("fieldDescription","变更后剩余期数");
                line45.put("fieldName","afterRemainTimes");
                line45.put("fieldType","Text");
                line45.put("fieldValue",conChangeRepaymentInfos.get(0).getAfterRemainTimes());
                line45.put("orderSeq",5);
                line45.put("isEdit",false);
                line45.put("isShow",true);
                line45.put("nullableFlag",true);

                JSONObject line46=new JSONObject(new LinkedHashMap());
                line46.put("fieldDescription","还款变更方案");
                line46.put("fieldName","repaymentChangeTypeN");
                line46.put("fieldType","Text");
                line46.put("fieldValue",conChangeRepaymentInfos.get(0).getRepaymentChangeTypeN());
                line46.put("orderSeq",6);
                line46.put("isEdit",false);
                line46.put("isShow",true);
                line46.put("nullableFlag",true);

                JSONObject line47=new JSONObject(new LinkedHashMap());
                line47.put("fieldDescription","变更手续费");
                line47.put("fieldName","ccrFee");
                line47.put("fieldType","Text");
                line47.put("fieldValue",conChangeRepaymentInfos.get(0).getCcrFee());
                line47.put("orderSeq",7);
                line47.put("isEdit",false);
                line47.put("isShow",true);
                line47.put("nullableFlag",true);

                lines3.add(line41);
                lines3.add(line42);
                lines3.add(line43);
                lines3.add(line44);
                lines3.add(line45);
                lines3.add(line46);
                lines3.add(line47);

                content4.put("lines",lines3);
                content.add(content4);

                //变更对比
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","变更对比");
                header5.put("icon","profile");
                header5.put("orderSeq",5);
                header5.put("hasChildren",false);
                header5.put("isShow",true);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();

                JSONObject line51=new JSONObject(new LinkedHashMap());
                line51.put("fieldDescription","变更前");
                line51.put("fieldName","changeBefore");
                line51.put("fieldType","Text");
                line51.put("fieldValue","");
                line51.put("orderSeq",1);
                line51.put("isEdit",false);
                line51.put("isShow",true);
                line51.put("nullableFlag",true);

                JSONObject line52=new JSONObject(new LinkedHashMap());
                line52.put("fieldDescription","总期数");
                line52.put("fieldName","beforeTotalTimes");
                line52.put("fieldType","Text");
                line52.put("fieldValue",conChangeRepaymentInfos.get(0).getBeforeTotalTimes());
                line52.put("orderSeq",2);
                line52.put("isEdit",false);
                line52.put("isShow",true);
                line52.put("nullableFlag",true);

                JSONObject line53=new JSONObject(new LinkedHashMap());
                line53.put("fieldDescription","租赁结束日");
                line53.put("fieldName","beforeLeaseEndDate");
                line53.put("fieldType","Text");
                String beforeLeaseEndDate = sdf.format(conChangeRepaymentInfos.get(0).getBeforeLeaseEndDate());
                line53.put("fieldValue",beforeLeaseEndDate);
                line53.put("orderSeq",3);
                line53.put("isEdit",false);
                line53.put("isShow",true);
                line53.put("nullableFlag",true);

                JSONObject line54=new JSONObject(new LinkedHashMap());
                line54.put("fieldDescription","租金总额");
                line54.put("fieldName","beforeTotalAmount");
                line54.put("fieldType","Text");
                line54.put("fieldValue",conChangeRepaymentInfos.get(0).getBeforeTotalAmount());
                line54.put("orderSeq",4);
                line54.put("isEdit",false);
                line54.put("isShow",true);
                line54.put("nullableFlag",true);

                JSONObject line55=new JSONObject(new LinkedHashMap());
                line55.put("fieldDescription","利息总额");
                line55.put("fieldName","beforeTotalInterest");
                line55.put("fieldType","Text");
                line55.put("fieldValue",conChangeRepaymentInfos.get(0).getBeforeTotalInterest());
                line55.put("orderSeq",5);
                line55.put("isEdit",false);
                line55.put("isShow",true);
                line55.put("nullableFlag",true);

                JSONObject line56=new JSONObject(new LinkedHashMap());
                line56.put("fieldDescription","IRR");
                line56.put("fieldName","beforeIrr");
                line56.put("fieldType","Text");
                line56.put("fieldValue",conChangeRepaymentInfos.get(0).getBeforeIrr()==null?0:new BigDecimal(String.valueOf(conChangeRepaymentInfos.get(0).getBeforeIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line56.put("orderSeq",6);
                line56.put("isEdit",false);
                line56.put("isShow",true);
                line56.put("nullableFlag",true);

                JSONObject line57=new JSONObject(new LinkedHashMap());
                line57.put("fieldDescription","变更后");
                line57.put("fieldName","changeAfter");
                line57.put("fieldType","Text");
                line57.put("fieldValue","");
                line57.put("orderSeq",7);
                line57.put("isEdit",false);
                line57.put("isShow",true);
                line57.put("nullableFlag",true);

                JSONObject line58=new JSONObject(new LinkedHashMap());
                line58.put("fieldDescription","总期数");
                line58.put("fieldName","afterTotalTimes");
                line58.put("fieldType","Text");
                line58.put("fieldValue",conChangeRepaymentInfos.get(0).getAfterTotalTimes());
                line58.put("orderSeq",8);
                line58.put("isEdit",false);
                line58.put("isShow",true);
                line58.put("nullableFlag",true);

                JSONObject line59=new JSONObject(new LinkedHashMap());
                line59.put("fieldDescription","租赁结束日");
                line59.put("fieldName","afterLeaseEndDate");
                line59.put("fieldType","Text");
                String afterLeaseEndDate = sdf.format(conChangeRepaymentInfos.get(0).getAfterLeaseEndDate());
                line59.put("fieldValue",afterLeaseEndDate);
                line59.put("orderSeq",9);
                line59.put("isEdit",false);
                line59.put("isShow",true);
                line59.put("nullableFlag",true);

                JSONObject line510=new JSONObject(new LinkedHashMap());
                line510.put("fieldDescription","租金总额");
                line510.put("fieldName","afterTotalAmount");
                line510.put("fieldType","Text");
                line510.put("fieldValue",conChangeRepaymentInfos.get(0).getAfterTotalAmount());
                line510.put("orderSeq",10);
                line510.put("isEdit",false);
                line510.put("isShow",true);
                line510.put("nullableFlag",true);

                JSONObject line511=new JSONObject(new LinkedHashMap());
                line511.put("fieldDescription","利息总额");
                line511.put("fieldName","afterTotalInterest");
                line511.put("fieldType","Text");
                line511.put("fieldValue",conChangeRepaymentInfos.get(0).getAfterTotalInterest());
                line511.put("orderSeq",11);
                line511.put("isEdit",false);
                line511.put("isShow",true);
                line511.put("nullableFlag",true);

                JSONObject line512=new JSONObject(new LinkedHashMap());
                line512.put("fieldDescription","IRR");
                line512.put("fieldName","afterIrr");
                line512.put("fieldType","Text");
                line512.put("fieldValue",conChangeRepaymentInfos.get(0).getAfterIrr()==null?0:new BigDecimal(String.valueOf(conChangeRepaymentInfos.get(0).getAfterIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line512.put("orderSeq",12);
                line512.put("isEdit",false);
                line512.put("isShow",true);
                line512.put("nullableFlag",true);

                lines4.add(line51);
                lines4.add(line52);
                lines4.add(line53);
                lines4.add(line54);
                lines4.add(line55);
                lines4.add(line56);
                lines4.add(line57);
                lines4.add(line58);
                lines4.add(line59);
                lines4.add(line510);
                lines4.add(line511);
                lines4.add(line512);

                content5.put("lines",lines4);
                content.add(content5);
                JSONArray jsonArray = connContractChange(prjProjects,5);
                content.add(jsonArray.get(0));
                content.add(jsonArray.get(1));

            }else if("BUSINESS_CHANGE_BEFORE".equals(prjProjects.get(0).get("change_type").toString())){
                //报价方案
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","报价方案");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",false);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                HlsCusPrjQuotation hlsCusPrjQuotation=new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setSourceDocumentId(businessKey);
                List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(hlsCusPrjQuotation);

                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","报价方案");
                line31.put("fieldName","priceListN");
                line31.put("fieldType","Text");
                line31.put("fieldValue",hlsCusPrjQuotations.get(0).getPriceListN());
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","项目金额（元）");
                line32.put("fieldName","leaseItemAmount");
                line32.put("fieldType","Text");
                line32.put("fieldValue",hlsCusPrjQuotations.get(0).getLeaseItemAmount());
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","投放日");
                line33.put("fieldName","leaseStartDate");
                line33.put("fieldType","Text");
                String leaseStartDate = sdf.format(hlsCusPrjQuotations.get(0).getLeaseStartDate());
                line33.put("fieldValue",leaseStartDate);
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                JSONObject line34=new JSONObject(new LinkedHashMap());
                line34.put("fieldDescription","租期（年）");
                line34.put("fieldName","leaseTerm");
                line34.put("fieldType","Text");
                line34.put("fieldValue",hlsCusPrjQuotations.get(0).getLeaseTerm());
                line34.put("orderSeq",4);
                line34.put("isEdit",false);
                line34.put("isShow",true);
                line34.put("nullableFlag",true);

                JSONObject line35=new JSONObject(new LinkedHashMap());
                line35.put("fieldDescription","利率类型");
                line35.put("fieldName","intRateTypeN");
                line35.put("fieldType","Text");
                line35.put("fieldValue",hlsCusPrjQuotations.get(0).getIntRateTypeN());
                line35.put("orderSeq",5);
                line35.put("isEdit",false);
                line35.put("isShow",true);
                line35.put("nullableFlag",true);

                JSONObject line36=new JSONObject(new LinkedHashMap());
                line36.put("fieldDescription","年利率（%）");
                line36.put("fieldName","intRate");
                line36.put("fieldType","Text");
                line36.put("fieldValue",hlsCusPrjQuotations.get(0).getIntRate()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotations.get(0).getIntRate()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line36.put("orderSeq",6);
                line36.put("isEdit",false);
                line36.put("isShow",true);
                line36.put("nullableFlag",true);

                JSONObject line37=new JSONObject(new LinkedHashMap());
                line37.put("fieldDescription","每期租(本)金（元）");
                line37.put("fieldName","pmt");
                line37.put("fieldType","Text");
                line37.put("fieldValue",hlsCusPrjQuotations.get(0).getPmt());
                line37.put("orderSeq",7);
                line37.put("isEdit",false);
                line37.put("isShow",true);
                line37.put("nullableFlag",true);

                JSONObject line38=new JSONObject(new LinkedHashMap());
                line38.put("fieldDescription","IRR");
                line38.put("fieldName","irr");
                line38.put("fieldType","Text");
                line38.put("fieldValue",hlsCusPrjQuotations.get(0).getIrr()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotations.get(0).getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line38.put("orderSeq",8);
                line38.put("isEdit",false);
                line38.put("isShow",true);
                line38.put("nullableFlag",true);

                lines2.add(line31);
                lines2.add(line32);
                lines2.add(line33);
                lines2.add(line34);
                lines2.add(line35);
                lines2.add(line36);
                lines2.add(line37);
                lines2.add(line38);

                content3.put("lines",lines2);
                content.add(content3);

                //现金流信息
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","现金流信息");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                HlsCusConContractCashflow cashflow=new HlsCusConContractCashflow();
                cashflow.setQuotationId(Long.valueOf(prjProjects.get(0).get("quotation_id").toString()));
                List<Map> cashflowInfoByPrj = hlsCusConContractCashflowMapper.selectCashflowInfoByPrj(cashflow);

                for (int i = 0; i < cashflowInfoByPrj.size(); i++) {
                    JSONArray lines31=new JSONArray();

                    JSONObject line41=new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription","期数");
                    line41.put("fieldName","times");
                    line41.put("fieldType","Text");
                    line41.put("fieldValue",cashflowInfoByPrj.get(i).get("times"));
                    line41.put("orderSeq",1);
                    line41.put("isEdit",false);
                    line41.put("isShow",true);
                    line41.put("nullableFlag",true);

                    JSONObject line42=new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription","现金流项目");
                    line42.put("fieldName","cfItem");
                    line42.put("fieldType","Text");
                    line42.put("fieldValue",cashflowInfoByPrj.get(i).get("cfItem"));
                    line42.put("orderSeq",2);
                    line42.put("isEdit",false);
                    line42.put("isShow",true);
                    line42.put("nullableFlag",true);

                    JSONObject line43=new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription","支付日期");
                    line43.put("fieldName","dueDate");
                    line43.put("fieldType","Text");
                    String dueDate = sdf.format(cashflowInfoByPrj.get(i).get("dueDate"));
                    line43.put("fieldValue",dueDate);
                    line43.put("orderSeq",3);
                    line43.put("isEdit",false);
                    line43.put("isShow",true);
                    line43.put("nullableFlag",true);

                    JSONObject line44=new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription","租金（含税）");
                    line44.put("fieldName","dueAmount");
                    line44.put("fieldType","Text");
                    line44.put("fieldValue",cashflowInfoByPrj.get(i).get("dueAmount"));
                    line44.put("orderSeq",4);
                    line44.put("isEdit",false);
                    line44.put("isShow",true);
                    line44.put("nullableFlag",true);

                    JSONObject line45=new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription","租金（不含税）");
                    line45.put("fieldName","netDueAmount");
                    line45.put("fieldType","Text");
                    line45.put("fieldValue",cashflowInfoByPrj.get(i).get("netDueAmount"));
                    line45.put("orderSeq",5);
                    line45.put("isEdit",false);
                    line45.put("isShow",true);
                    line45.put("nullableFlag",true);

                    JSONObject line46=new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription","租金（元）");
                    line46.put("fieldName","vatDueAmount");
                    line46.put("fieldType","Text");
                    line46.put("fieldValue",cashflowInfoByPrj.get(i).get("vatDueAmount"));
                    line46.put("orderSeq",6);
                    line46.put("isEdit",false);
                    line46.put("isShow",true);
                    line46.put("nullableFlag",true);

                    JSONObject line47=new JSONObject(new LinkedHashMap());
                    line47.put("fieldDescription","本金（元）");
                    line47.put("fieldName","thePrincipal");
                    line47.put("fieldType","Text");
                    line47.put("fieldValue",cashflowInfoByPrj.get(i).get("thePrincipal"));
                    line47.put("orderSeq",7);
                    line47.put("isEdit",false);
                    line47.put("isShow",true);
                    line47.put("nullableFlag",true);

                    JSONObject line48=new JSONObject(new LinkedHashMap());
                    line48.put("fieldDescription","利息（元）");
                    line48.put("fieldName","theInterest");
                    line48.put("fieldType","Text");
                    line48.put("fieldValue",cashflowInfoByPrj.get(i).get("theInterest"));
                    line48.put("orderSeq",8);
                    line48.put("isEdit",false);
                    line48.put("isShow",true);
                    line48.put("nullableFlag",true);

                    JSONObject line49=new JSONObject(new LinkedHashMap());
                    line49.put("fieldDescription","手续费（元）");
                    line49.put("fieldName","theLeaseCharge");
                    line49.put("fieldType","Text");
                    line49.put("fieldValue",cashflowInfoByPrj.get(i).get("theLeaseCharge")==null?"":cashflowInfoByPrj.get(i).get("theLeaseCharge"));
                    line49.put("orderSeq",9);
                    line49.put("isEdit",false);
                    line49.put("isShow",true);
                    line49.put("nullableFlag",true);

                    JSONObject line410=new JSONObject(new LinkedHashMap());
                    line410.put("fieldDescription","租前息（元）");
                    line410.put("fieldName","rhePreInterest");
                    line410.put("fieldType","Text");
                    line410.put("fieldValue",cashflowInfoByPrj.get(i).get("rhePreInterest")==null?"":cashflowInfoByPrj.get(i).get("rhePreInterest"));
                    line410.put("orderSeq",10);
                    line410.put("isEdit",false);
                    line410.put("isShow",true);
                    line410.put("nullableFlag",true);

                    JSONObject line411=new JSONObject(new LinkedHashMap());
                    line411.put("fieldDescription","首付款（元）");
                    line411.put("fieldName","theFirstPayment");
                    line411.put("fieldType","Text");
                    line411.put("fieldValue",cashflowInfoByPrj.get(i).get("theFirstPayment")==null?"":cashflowInfoByPrj.get(i).get("theFirstPayment"));
                    line411.put("orderSeq",11);
                    line411.put("isEdit",false);
                    line411.put("isShow",true);
                    line411.put("nullableFlag",true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);
                    lines31.add(line47);
                    lines31.add(line48);
                    lines31.add(line49);
                    lines31.add(line410);
                    lines31.add(line411);

                    lines3.add(lines31);
                }
                content4.put("lines",lines3);
                content.add(content4);

                //现金流变更前信息
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","现金流变更前");
                header5.put("icon","profile");
                header5.put("orderSeq",5);
                header5.put("hasChildren",true);
                header5.put("isShow",true);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();
                HlsCusConContractCashflow cashflowBefore=new HlsCusConContractCashflow();
                cashflowBefore.setChangeReqId(Long.valueOf(prjProjects.get(0).get("change_req_id").toString()));
                List<Map> cashflowInfoByPrjChangeBefore = hlsCusConContractCashflowMapper.selectCashflowInfoByPrjChangeBefore(cashflowBefore);

                for (int i = 0; i < cashflowInfoByPrjChangeBefore.size(); i++) {
                    JSONArray lines41=new JSONArray();

                    JSONObject line51=new JSONObject(new LinkedHashMap());
                    line51.put("fieldDescription","期数");
                    line51.put("fieldName","times");
                    line51.put("fieldType","Text");
                    line51.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("times"));
                    line51.put("orderSeq",1);
                    line51.put("isEdit",false);
                    line51.put("isShow",true);
                    line51.put("nullableFlag",true);

                    JSONObject line52=new JSONObject(new LinkedHashMap());
                    line52.put("fieldDescription","现金流项目");
                    line52.put("fieldName","cfItem");
                    line52.put("fieldType","Text");
                    line52.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("cfItem"));
                    line52.put("orderSeq",2);
                    line52.put("isEdit",false);
                    line52.put("isShow",true);
                    line52.put("nullableFlag",true);

                    JSONObject line53=new JSONObject(new LinkedHashMap());
                    line53.put("fieldDescription","支付日期");
                    line53.put("fieldName","dueDate");
                    line53.put("fieldType","Text");
                    String dueDate = sdf.format(cashflowInfoByPrjChangeBefore.get(i).get("dueDate"));
                    line53.put("fieldValue",dueDate);
                    line53.put("orderSeq",3);
                    line53.put("isEdit",false);
                    line53.put("isShow",true);
                    line53.put("nullableFlag",true);

                    JSONObject line54=new JSONObject(new LinkedHashMap());
                    line54.put("fieldDescription","租金（含税）");
                    line54.put("fieldName","dueAmount");
                    line54.put("fieldType","Text");
                    line54.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("dueAmount"));
                    line54.put("orderSeq",4);
                    line54.put("isEdit",false);
                    line54.put("isShow",true);
                    line54.put("nullableFlag",true);

                    JSONObject line55=new JSONObject(new LinkedHashMap());
                    line55.put("fieldDescription","租金（不含税）");
                    line55.put("fieldName","netDueAmount");
                    line55.put("fieldType","Text");
                    line55.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("netDueAmount"));
                    line55.put("orderSeq",5);
                    line55.put("isEdit",false);
                    line55.put("isShow",true);
                    line55.put("nullableFlag",true);

                    JSONObject line56=new JSONObject(new LinkedHashMap());
                    line56.put("fieldDescription","租金（元）");
                    line56.put("fieldName","vatDueAmount");
                    line56.put("fieldType","Text");
                    line56.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("vatDueAmount"));
                    line56.put("orderSeq",6);
                    line56.put("isEdit",false);
                    line56.put("isShow",true);
                    line56.put("nullableFlag",true);

                    JSONObject line57=new JSONObject(new LinkedHashMap());
                    line57.put("fieldDescription","本金（元）");
                    line57.put("fieldName","thePrincipal");
                    line57.put("fieldType","Text");
                    line57.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("thePrincipal"));
                    line57.put("orderSeq",7);
                    line57.put("isEdit",false);
                    line57.put("isShow",true);
                    line57.put("nullableFlag",true);

                    JSONObject line58=new JSONObject(new LinkedHashMap());
                    line58.put("fieldDescription","利息（元）");
                    line58.put("fieldName","theInterest");
                    line58.put("fieldType","Text");
                    line58.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("theInterest"));
                    line58.put("orderSeq",8);
                    line58.put("isEdit",false);
                    line58.put("isShow",true);
                    line58.put("nullableFlag",true);

                    JSONObject line59=new JSONObject(new LinkedHashMap());
                    line59.put("fieldDescription","手续费（元）");
                    line59.put("fieldName","theLeaseCharge");
                    line59.put("fieldType","Text");
                    line59.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("theLeaseCharge")==null?"":cashflowInfoByPrj.get(i).get("theLeaseCharge"));
                    line59.put("orderSeq",9);
                    line59.put("isEdit",false);
                    line59.put("isShow",true);
                    line59.put("nullableFlag",true);

                    JSONObject line510=new JSONObject(new LinkedHashMap());
                    line510.put("fieldDescription","租前息（元）");
                    line510.put("fieldName","rhePreInterest");
                    line510.put("fieldType","Text");
                    line510.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("rhePreInterest")==null?"":cashflowInfoByPrj.get(i).get("rhePreInterest"));
                    line510.put("orderSeq",10);
                    line510.put("isEdit",false);
                    line510.put("isShow",true);
                    line510.put("nullableFlag",true);

                    JSONObject line511=new JSONObject(new LinkedHashMap());
                    line511.put("fieldDescription","首付款（元）");
                    line511.put("fieldName","theFirstPayment");
                    line511.put("fieldType","Text");
                    line511.put("fieldValue",cashflowInfoByPrjChangeBefore.get(i).get("theFirstPayment")==null?"":cashflowInfoByPrj.get(i).get("theFirstPayment"));
                    line511.put("orderSeq",11);
                    line511.put("isEdit",false);
                    line511.put("isShow",true);
                    line511.put("nullableFlag",true);

                    lines41.add(line51);
                    lines41.add(line52);
                    lines41.add(line53);
                    lines41.add(line54);
                    lines41.add(line55);
                    lines41.add(line56);
                    lines41.add(line57);
                    lines41.add(line58);
                    lines41.add(line59);
                    lines41.add(line510);
                    lines41.add(line511);

                    lines4.add(lines41);
                }
                content5.put("lines",lines4);
                content.add(content5);
                JSONArray jsonArray = connContractChange(prjProjects,5);
                content.add(jsonArray.get(0));
                content.add(jsonArray.get(1));

            }else if("BUSINESS_CHANGE_AFTER".equals(prjProjects.get(0).get("change_type").toString())){
                //租赁物变更后信息
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","租赁物变更后信息");
                header4.put("icon","profile");
                header4.put("orderSeq",3);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem=new HlsCusPrjProjectLeaseItem();
                hlsCusPrjProjectLeaseItem.setProjectId(businessKey);
                hlsCusPrjProjectLeaseItem.setProjectType("contract");
                List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItems = hlsCusPrjProjectLeaseItemMapper.queryPrjProjectLeaseItem1(hlsCusPrjProjectLeaseItem);
                HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItemBefore1=new HlsCusPrjProjectLeaseItem();
                hlsCusPrjProjectLeaseItemBefore1.setChangeReqId(Long.valueOf(prjProjects.get(0).get("change_req_id").toString()));
                hlsCusPrjProjectLeaseItemBefore1.setProjectType("contract");
                List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemBefores = hlsCusPrjProjectLeaseItemMapper.queryPrjProjectLeaseItemChangeBefore2(hlsCusPrjProjectLeaseItemBefore1);

//                JSONObject line41=new JSONObject(new LinkedHashMap());
//                line41.put("fieldDescription","变更后");
//                line41.put("fieldName","");
//                line41.put("fieldType","Text");
//                line41.put("fieldValue","");
//                line41.put("orderSeq",1);
//                line41.put("isEdit",false);
//                line41.put("isShow",true);
//                line41.put("nullableFlag",true);
//
//                lines3.add(line41);

                for (HlsCusPrjProjectLeaseItem cusPrjProjectLeaseItem : hlsCusPrjProjectLeaseItems) {
                    JSONArray lines31=new JSONArray();

                    JSONObject line42=new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription","项目名称");
                    line42.put("fieldName","leaseItemIdN");
                    line42.put("fieldType","Text");
                    line42.put("fieldValue",cusPrjProjectLeaseItem.getLeaseItemIdN());
                    line42.put("orderSeq",2);
                    line42.put("isEdit",false);
                    line42.put("isShow",true);
                    line42.put("nullableFlag",true);

                    JSONObject line43=new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription","租赁物名称");
                    line43.put("fieldName","assetName");
                    line43.put("fieldType","Text");
                    line43.put("fieldValue",cusPrjProjectLeaseItem.getAssetName());
                    line43.put("orderSeq",3);
                    line43.put("isEdit",false);
                    line43.put("isShow",true);
                    line43.put("nullableFlag",true);

                    JSONObject line44=new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription","数量");
                    line44.put("fieldName","quantity");
                    line44.put("fieldType","Text");
                    line44.put("fieldValue",cusPrjProjectLeaseItem.getQuantity());
                    line44.put("orderSeq",4);
                    line44.put("isEdit",false);
                    line44.put("isShow",true);
                    line44.put("nullableFlag",true);

                    JSONObject line45=new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription","租赁物价值（元）");
                    line45.put("fieldName","price");
                    line45.put("fieldType","Text");
                    line45.put("fieldValue",cusPrjProjectLeaseItem.getPrice());
                    line45.put("orderSeq",5);
                    line45.put("isEdit",false);
                    line45.put("isShow",true);
                    line45.put("nullableFlag",true);

                    JSONObject line46=new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription","租赁物价值（总价）");
                    line46.put("fieldName","totalPrice");
                    line46.put("fieldType","Text");
                    line46.put("fieldValue",cusPrjProjectLeaseItem.getTotalPrice());
                    line46.put("orderSeq",6);
                    line46.put("isEdit",false);
                    line46.put("isShow",true);
                    line46.put("nullableFlag",true);

                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);

                    lines3.add(lines31);
                }
                content4.put("lines",lines3);
                content.add(content4);

//                JSONObject line47=new JSONObject(new LinkedHashMap());
//                line47.put("fieldDescription","变更前");
//                line47.put("fieldName","");
//                line47.put("fieldType","Text");
//                line47.put("fieldValue","");
//                line47.put("orderSeq",7);
//                line47.put("isEdit",false);
//                line47.put("isShow",true);
//                line47.put("nullableFlag",true);
//
//                lines3.add(line47);
                //租赁物变更前信息
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","租赁物变更前信息");
                header5.put("icon","profile");
                header5.put("orderSeq",4);
                header5.put("hasChildren",true);
                header5.put("isShow",true);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();

                for (HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItemBefore : hlsCusPrjProjectLeaseItemBefores) {
                    JSONArray lines41=new JSONArray();

                    JSONObject line51=new JSONObject(new LinkedHashMap());
                    line51.put("fieldDescription","项目名称");
                    line51.put("fieldName","leaseItemIdN");
                    line51.put("fieldType","Text");
                    line51.put("fieldValue",hlsCusPrjProjectLeaseItemBefore.getLeaseItemIdN());
                    line51.put("orderSeq",1);
                    line51.put("isEdit",false);
                    line51.put("isShow",true);
                    line51.put("nullableFlag",true);

                    JSONObject line52=new JSONObject(new LinkedHashMap());
                    line52.put("fieldDescription","租赁物名称");
                    line52.put("fieldName","assetName");
                    line52.put("fieldType","Text");
                    line52.put("fieldValue",hlsCusPrjProjectLeaseItemBefore.getAssetName());
                    line52.put("orderSeq",2);
                    line52.put("isEdit",false);
                    line52.put("isShow",true);
                    line52.put("nullableFlag",true);

                    JSONObject line53=new JSONObject(new LinkedHashMap());
                    line53.put("fieldDescription","数量");
                    line53.put("fieldName","quantity");
                    line53.put("fieldType","Text");
                    line53.put("fieldValue",hlsCusPrjProjectLeaseItemBefore.getQuantity());
                    line53.put("orderSeq",3);
                    line53.put("isEdit",false);
                    line53.put("isShow",true);
                    line53.put("nullableFlag",true);

                    JSONObject line54=new JSONObject(new LinkedHashMap());
                    line54.put("fieldDescription","租赁物价值（元）");
                    line54.put("fieldName","price");
                    line54.put("fieldType","Text");
                    line54.put("fieldValue",hlsCusPrjProjectLeaseItemBefore.getPrice());
                    line54.put("orderSeq",4);
                    line54.put("isEdit",false);
                    line54.put("isShow",true);
                    line54.put("nullableFlag",true);

                    JSONObject line55=new JSONObject(new LinkedHashMap());
                    line55.put("fieldDescription","租赁物价值（总价）");
                    line55.put("fieldName","totalPrice");
                    line55.put("fieldType","Text");
                    line55.put("fieldValue",hlsCusPrjProjectLeaseItemBefore.getTotalPrice());
                    line55.put("orderSeq",5);
                    line55.put("isEdit",false);
                    line55.put("isShow",true);
                    line55.put("nullableFlag",true);

                    lines41.add(line51);
                    lines41.add(line52);
                    lines41.add(line53);
                    lines41.add(line54);
                    lines41.add(line55);

                    lines4.add(lines41);
                }
                content5.put("lines",lines4);
                content.add(content5);

                //抵押物变更后信息
                JSONObject content6=new JSONObject(new LinkedHashMap());
                JSONObject header6=new JSONObject(new LinkedHashMap());
                header6.put("name","抵押物变更后信息");
                header6.put("icon","profile");
                header6.put("orderSeq",5);
                header6.put("hasChildren",true);
                header6.put("isShow",true);
                content6.put("header",header6);
                JSONArray lines5=new JSONArray();
                HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp=new HlsCusPrjProjectChanceMp();
                hlsCusPrjProjectChanceMp.setProjectId(businessKey);
                List<HlsCusPrjProjectChanceMp> hlsCusPrjProjectChanceMps = hlsCusPrjProjectChanceMpMapper.queryPrjProjectChance(hlsCusPrjProjectChanceMp);
                HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMpBefore1=new HlsCusPrjProjectChanceMp();
                hlsCusPrjProjectChanceMpBefore1.setChangeReqId(Long.valueOf(prjProjects.get(0).get("change_req_id").toString()));
                List<HlsCusPrjProjectChanceMp> hlsCusPrjProjectChanceMpBefores = hlsCusPrjProjectChanceMpMapper.queryPrjProjectChanceChangeBefore1(hlsCusPrjProjectChanceMpBefore1);

//                JSONObject line61=new JSONObject(new LinkedHashMap());
//                line61.put("fieldDescription","变更后");
//                line61.put("fieldName","");
//                line61.put("fieldType","Text");
//                line61.put("fieldValue","");
//                line51.put("orderSeq",1);
//                line51.put("isEdit",false);
//                line51.put("isShow",true);
//                line51.put("nullableFlag",true);
//
//                lines4.add(line51);

                for (HlsCusPrjProjectChanceMp cusPrjProjectChanceMp : hlsCusPrjProjectChanceMps) {
                    JSONArray lines51=new JSONArray();

                    JSONObject line61=new JSONObject(new LinkedHashMap());
                    line61.put("fieldDescription","项目名称");
                    line61.put("fieldName","leaseItemIdN");
                    line61.put("fieldType","Text");
                    line61.put("fieldValue",cusPrjProjectChanceMp.getLeaseItemIdN());
                    line61.put("orderSeq",1);
                    line61.put("isEdit",false);
                    line61.put("isShow",true);
                    line61.put("nullableFlag",true);

                    JSONObject line62=new JSONObject(new LinkedHashMap());
                    line62.put("fieldDescription","抵(质)押物编号");
                    line62.put("fieldName","leaseItemCode");
                    line62.put("fieldType","Text");
                    line62.put("fieldValue",cusPrjProjectChanceMp.getLeaseItemCode());
                    line62.put("orderSeq",2);
                    line62.put("isEdit",false);
                    line62.put("isShow",true);
                    line62.put("nullableFlag",true);

                    JSONObject line63=new JSONObject(new LinkedHashMap());
                    line63.put("fieldDescription","抵押物购置价（元）");
                    line63.put("fieldName","originalAssetValueTotal");
                    line63.put("fieldType","Text");
                    line63.put("fieldValue",cusPrjProjectChanceMp.getOriginalAssetValueTotal()==null?"":cusPrjProjectChanceMp.getOriginalAssetValueTotal());
                    line63.put("orderSeq",3);
                    line63.put("isEdit",false);
                    line63.put("isShow",true);
                    line63.put("nullableFlag",true);

                    JSONObject line64=new JSONObject(new LinkedHashMap());
                    line64.put("fieldDescription","抵押物评估价值（元）");
                    line64.put("fieldName","assetValueTotal");
                    line64.put("fieldType","Text");
                    line64.put("fieldValue",cusPrjProjectChanceMp.getAssetValueTotal()==null?"":cusPrjProjectChanceMp.getAssetValueTotal());
                    line64.put("orderSeq",4);
                    line64.put("isEdit",false);
                    line64.put("isShow",true);
                    line64.put("nullableFlag",true);

                    lines51.add(line61);
                    lines51.add(line62);
                    lines51.add(line63);
                    lines51.add(line64);

                    lines5.add(lines51);
                }
                content6.put("lines",lines5);
                content.add(content6);
//                JSONObject line56=new JSONObject(new LinkedHashMap());
//                line56.put("fieldDescription","变更前");
//                line56.put("fieldName","");
//                line56.put("fieldType","Text");
//                line56.put("fieldValue","");
//                line56.put("orderSeq",6);
//                line56.put("isEdit",false);
//                line56.put("isShow",true);
//                line56.put("nullableFlag",true);
//
//                lines4.add(line56);
                //抵押物变更前信息
                JSONObject content7=new JSONObject(new LinkedHashMap());
                JSONObject header7=new JSONObject(new LinkedHashMap());
                header7.put("name","抵押物变更前信息");
                header7.put("icon","profile");
                header7.put("orderSeq",6);
                header7.put("hasChildren",true);
                header7.put("isShow",true);
                content7.put("header",header7);
                JSONArray lines6=new JSONArray();

                for (HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMpBefore : hlsCusPrjProjectChanceMpBefores) {
                    JSONArray lines61=new JSONArray();

                    JSONObject line71=new JSONObject(new LinkedHashMap());
                    line71.put("fieldDescription","项目名称");
                    line71.put("fieldName","leaseItemIdN");
                    line71.put("fieldType","Text");
                    line71.put("fieldValue",hlsCusPrjProjectChanceMpBefore.getLeaseItemIdN());
                    line71.put("orderSeq",1);
                    line71.put("isEdit",false);
                    line71.put("isShow",true);
                    line71.put("nullableFlag",true);

                    JSONObject line72=new JSONObject(new LinkedHashMap());
                    line72.put("fieldDescription","抵(质)押物编号");
                    line72.put("fieldName","leaseItemCode");
                    line72.put("fieldType","Text");
                    line72.put("fieldValue",hlsCusPrjProjectChanceMpBefore.getLeaseItemCode());
                    line72.put("orderSeq",2);
                    line72.put("isEdit",false);
                    line72.put("isShow",true);
                    line72.put("nullableFlag",true);

                    JSONObject line73=new JSONObject(new LinkedHashMap());
                    line73.put("fieldDescription","抵押物购置价（元）");
                    line73.put("fieldName","originalAssetValueTotal");
                    line73.put("fieldType","Text");
                    line73.put("fieldValue",hlsCusPrjProjectChanceMpBefore.getOriginalAssetValueTotal()==null?"":hlsCusPrjProjectChanceMpBefore.getOriginalAssetValueTotal());
                    line73.put("orderSeq",3);
                    line73.put("isEdit",false);
                    line73.put("isShow",true);
                    line73.put("nullableFlag",true);

                    JSONObject line74=new JSONObject(new LinkedHashMap());
                    line74.put("fieldDescription","抵押物评估价值（元）");
                    line74.put("fieldName","assetValueTotal");
                    line74.put("fieldType","Text");
                    line74.put("fieldValue",hlsCusPrjProjectChanceMpBefore.getAssetValueTotal()==null?"":hlsCusPrjProjectChanceMpBefore.getAssetValueTotal());
                    line74.put("orderSeq",4);
                    line74.put("isEdit",false);
                    line74.put("isShow",true);
                    line74.put("nullableFlag",true);

                    lines61.add(line71);
                    lines61.add(line72);
                    lines61.add(line73);
                    lines61.add(line74);

                    lines6.add(lines61);
                }
                content7.put("lines",lines6);
                content.add(content7);

                JSONArray jsonArray = connContractChange(prjProjects,6);
                content.add(jsonArray.get(0));
                content.add(jsonArray.get(1));
            }

            return content;
        }catch (Exception e){
            logger.error("----------合同变更流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //零售业务合同变更
    private JSONArray contractChangeProcess2()  {
        try{
            JSONArray content=new JSONArray();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//            HlsCusConContractChangeReq hlsCusConContractChangeReq=new HlsCusConContractChangeReq();
//            hlsCusConContractChangeReq.setChangeReqId(businessKey);
//            List<HlsCusConContractChangeReq> hlsCusConContractChangeReqs = hlsCusConContractChangeReqMapper.queryContractChangeReqDetail((Map) hlsCusConContractChangeReq);
            HlsCusConContractChangeReq hlsCusConContractChangeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);
            //租赁物变更
            if("LEASE_ITEM".equals(hlsCusConContractChangeReq.getChangeType())){
                IRequest iRequest= RequestHelper.newEmptyRequest();
                List<JSONObject> returnJSONList = new ArrayList<>();
                HlsCusConContractChangeReq changeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);
                //基本信息
                JSONObject content1=new JSONObject(new LinkedHashMap());
                JSONObject header1=new JSONObject(new LinkedHashMap());
                header1.put("name","基本信息");
                header1.put("icon","profile");
                header1.put("orderSeq",1);
                header1.put("hasChildren",false);
                header1.put("isShow",true);
                content1.put("header",header1);
                JSONArray lines=new JSONArray();
                Map<String, Object> map = new HashMap<>();
                map.put("contractId", hlsCusConContractChangeReq.getContractId());
//                HlsCusConContract hlsCusConContract =new HlsCusConContract();
//                hlsCusConContract.setContractId(hlsCusConContractChangeReq.getContractId());
                List<HlsCusConContract> hlsCusConContracts=hlsCusConContractMapper.queryConContractDetails(map);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList.add(data);
                        });
                    }
                }

                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","合同编号");
                line1.put("fieldName","contractNumber");
                line1.put("fieldType","Text");
                line1.put("fieldValue",returnJSONList.get(0).get("contract_number"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","商业模式");
                line2.put("fieldName","leaseChannelN");
                line2.put("fieldType","Text");
                line2.put("fieldValue",returnJSONList.get(0).get("lease_channel_n"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","业务模式");
                line3.put("fieldName","businessTypeN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",returnJSONList.get(0).get("business_type_n"));
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","承租人名称");
                line4.put("fieldName","bpIdTenantN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",returnJSONList.get(0).get("bp_id_tenant_n"));
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","合作方");
                line5.put("fieldName","manufacturerIdN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",returnJSONList.get(0).get("manufacturer_id_n"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","归属主机厂");
                line6.put("fieldName","factoryIdN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",returnJSONList.get(0).get("factory_id_n"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","经销商");
                line7.put("fieldName","bpIdVenderN");
                line7.put("fieldType","Text");
                line7.put("fieldValue",returnJSONList.get(0).get("bp_id_vender_n"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","起租日期");
                line8.put("fieldName","leaseStartDate");
                line8.put("fieldType","Text");
//                String leaseStartDate = sdf.format(returnJSONList.get(0).get("lease_start_date"));
                line8.put("fieldValue",returnJSONList.get(0).get("lease_start_date"));
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","原设备款");
                line9.put("fieldName","leaseItemAmount");
                line9.put("fieldType","Text");
                line9.put("fieldValue",returnJSONList.get(0).get("lease_item_amount"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                JSONObject line10=new JSONObject(new LinkedHashMap());
                line10.put("fieldDescription","现设备额");
                line10.put("fieldName","sumLeaseItemAmount");
                line10.put("fieldType","Text");
                line10.put("fieldValue",returnJSONList.get(0).get("sum_lease_item_amount"));
                line10.put("orderSeq",10);
                line10.put("isEdit",false);
                line10.put("isShow",true);
                line10.put("nullableFlag",true);

                JSONObject line11=new JSONObject(new LinkedHashMap());
                line11.put("fieldDescription","融资额");
                line11.put("fieldName","financeAmount");
                line11.put("fieldType","Text");
                line11.put("fieldValue",returnJSONList.get(0).get("finance_amount"));
                line11.put("orderSeq",11);
                line11.put("isEdit",false);
                line11.put("isShow",true);
                line11.put("nullableFlag",true);

                JSONObject line12=new JSONObject(new LinkedHashMap());
                line12.put("fieldDescription","保证金");
                line12.put("fieldName","deposit");
                line12.put("fieldType","Text");
                line12.put("fieldValue",returnJSONList.get(0).get("deposit"));
                line12.put("orderSeq",12);
                line12.put("isEdit",false);
                line12.put("isShow",true);
                line12.put("nullableFlag",true);

                JSONObject line13=new JSONObject(new LinkedHashMap());
                line13.put("fieldDescription","租赁期限(月)");
                line13.put("fieldName","leaseTerm");
                line13.put("fieldType","Text");
                line13.put("fieldValue",returnJSONList.get(0).get("lease_term"));
                line13.put("orderSeq",13);
                line13.put("isEdit",false);
                line13.put("isShow",true);
                line13.put("nullableFlag",true);

                JSONObject line14=new JSONObject(new LinkedHashMap());
                line14.put("fieldDescription","支付频率");
                line14.put("fieldName","annualPayTimesN");
                line14.put("fieldType","Text");
                line14.put("fieldValue",returnJSONList.get(0).get("annual_pay_times_n"));
                line14.put("orderSeq",14);
                line14.put("isEdit",false);
                line14.put("isShow",true);
                line14.put("nullableFlag",true);

                JSONObject line15=new JSONObject(new LinkedHashMap());
                line15.put("fieldDescription","租赁期数");
                line15.put("fieldName","leaseTimes");
                line15.put("fieldType","Text");
                line15.put("fieldValue",returnJSONList.get(0).get("lease_times"));
                line15.put("orderSeq",15);
                line15.put("isEdit",false);
                line15.put("isShow",true);
                line15.put("nullableFlag",true);

                JSONObject line16=new JSONObject(new LinkedHashMap());
                line16.put("fieldDescription","保险购买情况");
                line16.put("fieldName","insuranceFlagN");
                line16.put("fieldType","Text");
                line16.put("fieldValue",returnJSONList.get(0).get("insurance_flag_n"));
                line16.put("orderSeq",16);
                line16.put("isEdit",false);
                line16.put("isShow",true);
                line16.put("nullableFlag",true);

                JSONObject line17=new JSONObject(new LinkedHashMap());
                line17.put("fieldDescription","设备类型(变更前)");
                line17.put("fieldName","secondHandFlagPreN");
                line17.put("fieldType","Text");
                line17.put("fieldValue",returnJSONList.get(0).get("second_hand_flag_pre_n"));
                line17.put("orderSeq",17);
                line17.put("isEdit",false);
                line17.put("isShow",true);
                line17.put("nullableFlag",true);

                JSONObject line18=new JSONObject(new LinkedHashMap());
                line18.put("fieldDescription","设备类型(变更后)");
                line18.put("fieldName","secondHandFlagN");
                line18.put("fieldType","Text");
                line18.put("fieldValue",returnJSONList.get(0).get("second_hand_flag_n"));
                line18.put("orderSeq",18);
                line18.put("isEdit",false);
                line18.put("isShow",true);
                line18.put("nullableFlag",true);

                JSONObject line19=new JSONObject(new LinkedHashMap());
                line19.put("fieldDescription","当前是否逾期");
                line19.put("fieldName","currentOverdueStatus");
                line19.put("fieldType","Text");
                line19.put("fieldValue",returnJSONList.get(0).get("current_overdue_status"));
                line19.put("orderSeq",19);
                line19.put("isEdit",false);
                line19.put("isShow",true);
                line19.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);
                lines.add(line10);
                lines.add(line11);
                lines.add(line12);
                lines.add(line13);
                lines.add(line14);
                lines.add(line15);
                lines.add(line16);
                lines.add(line17);
                lines.add(line18);
                lines.add(line19);

                content1.put("lines",lines);
                content.add(content1);

                //变更信息
                JSONObject content2=new JSONObject(new LinkedHashMap());
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","变更信息");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();
                List<JSONObject> returnJSONList1 = new ArrayList<>();

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList1.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList1.add(data);
                        });
                    }
                }
                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","变更编号");
                line21.put("fieldName","changeReqNumber");
                line21.put("fieldType","Text");
                line21.put("fieldValue",returnJSONList1.get(0).get("change_req_number"));
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","变更日期");
                line22.put("fieldName","changeReqDate");
                line22.put("fieldType","Text");
                line22.put("fieldValue",returnJSONList1.get(0).get("change_req_date"));
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","变更说明");
                line23.put("fieldName","description");
                line23.put("fieldType","Text");
                line23.put("fieldValue",returnJSONList1.get(0).get("description"));
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);
//
                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);

                content2.put("lines",lines1);
                content.add(content2);

                //变更前租赁物信息
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","变更前租赁物信息");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",true);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                Map<String, Object> map2 = new HashMap<>();
                map2.put("contractId", hlsCusConContractChangeReq.getContractId());
//                HlsCusConContractLeaseItem hlsCusConContractLeaseItem=new HlsCusConContractLeaseItem();
//                hlsCusConContractLeaseItem.setContractId(hlsCusConContractChangeReq.getContractId());
                List<HlsCusConContractLeaseItem> hlsCusConContractLeaseItems=hlsCusConContractLeaseItemMapper.queryContractLeaseItemDetail(map2);
                for (HlsCusConContractLeaseItem hlsCusConContractLeaseItem1 : hlsCusConContractLeaseItems) {
                    JSONArray lines21 = new JSONArray();

                    JSONObject line31 = new JSONObject(new LinkedHashMap());
                    line31.put("fieldDescription", "租赁物名称");
                    line31.put("fieldName", "fullName");
                    line31.put("fieldType", "Text");
                    line31.put("fieldValue", hlsCusConContractLeaseItem1.getFullName());
                    line31.put("orderSeq", 1);
                    line31.put("isEdit", false);
                    line31.put("isShow", true);
                    line31.put("nullableFlag", true);

                    JSONObject line32 = new JSONObject(new LinkedHashMap());
                    line32.put("fieldDescription", "规格型号");
                    line32.put("fieldName", "classifyIdN");
                    line32.put("fieldType", "Text");
                    line32.put("fieldValue", hlsCusConContractLeaseItem1.getClassifyIdN());
                    line32.put("orderSeq", 2);
                    line32.put("isEdit", false);
                    line32.put("isShow", true);
                    line32.put("nullableFlag", true);

                    JSONObject line33 = new JSONObject(new LinkedHashMap());
                    line33.put("fieldDescription", "整机编号");
                    line33.put("fieldName", "serialNumber");
                    line33.put("fieldType", "Text");
                    line33.put("fieldValue", hlsCusConContractLeaseItem1.getSerialNumber());
                    line33.put("orderSeq", 3);
                    line33.put("isEdit", false);
                    line33.put("isShow", true);
                    line33.put("nullableFlag", true);

                    JSONObject line34 = new JSONObject(new LinkedHashMap());
                    line34.put("fieldDescription", "租赁物总价");
                    line34.put("fieldName", "price");
                    line34.put("fieldType", "Text");
                    line34.put("fieldValue", hlsCusConContractLeaseItem1.getPrice());
                    line34.put("orderSeq", 4);
                    line34.put("isEdit", false);
                    line34.put("isShow", true);
                    line34.put("nullableFlag", true);

                    JSONObject line35 = new JSONObject(new LinkedHashMap());
                    line35.put("fieldDescription", "备注");
                    line35.put("fieldName", "description");
                    line35.put("fieldType", "Text");
                    line35.put("fieldValue", hlsCusConContractLeaseItem1.getDescription());
                    line35.put("orderSeq", 5);
                    line35.put("isEdit", false);
                    line35.put("isShow", true);
                    line35.put("nullableFlag", true);

                    lines21.add(line31);
                    lines21.add(line32);
                    lines21.add(line33);
                    lines21.add(line34);
                    lines21.add(line35);
                    lines2.add(lines21);

                }
                content3.put("lines",lines2);
                content.add(content3);

                //变更后租赁物信息
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","变更后租赁物信息");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                List<JSONObject> returnJSONList2 = new ArrayList<>();
                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_lease_item", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects)){
                        jsonObjects.forEach(item -> returnJSONList2.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_lease_item");
                    if(CollectionUtils.isNotEmpty(jsonObjects)){
                        jsonObjects.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            if(!StringUtils.equals("delete",status)){
                                returnJSONList2.add(data);
                            }
                        });
                    }
                }
                for(JSONObject jsonObject:returnJSONList2){

                    JSONArray lines31 = new JSONArray();

                    JSONObject line41 = new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription", "租赁物名称");
                    line41.put("fieldName", "fullName");
                    line41.put("fieldType", "Text");
                    line41.put("fieldValue", jsonObject.get("full_name"));
                    line41.put("orderSeq", 1);
                    line41.put("isEdit", false);
                    line41.put("isShow", true);
                    line41.put("nullableFlag", true);

                    JSONObject line42 = new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription", "规格型号");
                    line42.put("fieldName", "classifyIdN");
                    line42.put("fieldType", "Text");
                    line42.put("fieldValue", jsonObject.get("classify_id_n"));
                    line42.put("orderSeq", 2);
                    line42.put("isEdit", false);
                    line42.put("isShow", true);
                    line42.put("nullableFlag", true);

                    JSONObject line43 = new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription", "整机编号");
                    line43.put("fieldName", "serialNumber");
                    line43.put("fieldType", "Text");
                    line43.put("fieldValue", jsonObject.get("serial_number"));
                    line43.put("orderSeq", 3);
                    line43.put("isEdit", false);
                    line43.put("isShow", true);
                    line43.put("nullableFlag", true);

                    JSONObject line44 = new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription", "租赁物总价");
                    line44.put("fieldName", "price");
                    line44.put("fieldType", "Text");
                    line44.put("fieldValue", jsonObject.get("price"));
                    line44.put("orderSeq", 4);
                    line44.put("isEdit", false);
                    line44.put("isShow", true);
                    line44.put("nullableFlag", true);

                    JSONObject line45 = new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription", "备注");
                    line45.put("fieldName", "description");
                    line45.put("fieldType", "Text");
                    line45.put("fieldValue", jsonObject.get("description"));
                    line45.put("orderSeq", 5);
                    line45.put("isEdit", false);
                    line45.put("isShow", true);
                    line45.put("nullableFlag", true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines3.add(lines31);

                }
                content4.put("lines",lines3);
                content.add(content4);
                //退回按钮
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","approve");
                header5.put("orderSeq",5);
                header5.put("hasChildren",false);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();

                JSONObject line51=new JSONObject(new LinkedHashMap());
                line51.put("fieldDescription","退回");
                line51.put("fieldName","sendback");
                line51.put("fieldType","Text");
                line51.put("fieldValue","SENDBACK");
                line51.put("orderSeq",1);
                line51.put("isEdit",false);
                line51.put("isShow",true);
                line51.put("nullableFlag",false);

                lines4.add(line51);
                content5.put("lines",lines4);
                content.add(content5);
//                JSONArray jsonArray = connContractChange2(hlsCusConContractChangeReq.getContractId(),businessKey,"CON_CONTRACT_LEASE_ITEM_CHANGE",4);
//                content.add(jsonArray.get(0));
//                content.add(jsonArray.get(1));
            }
            //承租人变更
            else if("TENANT".equals(hlsCusConContractChangeReq.getChangeType())){
                IRequest iRequest= RequestHelper.newEmptyRequest();
                List<JSONObject> returnJSONList = new ArrayList<>();
                HlsCusConContractChangeReq changeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);
                //变更信息
                JSONObject content1=new JSONObject(new LinkedHashMap());
                JSONObject header1=new JSONObject(new LinkedHashMap());
                header1.put("name","变更信息");
                header1.put("icon","profile");
                header1.put("orderSeq",1);
                header1.put("hasChildren",false);
                header1.put("isShow",true);
                content1.put("header",header1);
                JSONArray lines=new JSONArray();

//                HlsCusConContractChangeReq hlsCusConContractChangeReq1 =new HlsCusConContractChangeReq();
//                hlsCusConContractChangeReq1.setChangeReqId(businessKey);
//                List<HlsCusConContractChangeReq> hlsCusConContractChangeReqs1=hlsCusConContractChangeReqMapper.queryContractChangeReqDetail((Map) hlsCusConContractChangeReq1);

//                HlsCusConContractChangeReq hlsCusConContractChangeReq1=hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList.add(data);
                        });
                    }
                }


                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","合同编号");
                line1.put("fieldName","contractNumber");
                line1.put("fieldType","Text");
                line1.put("fieldValue",returnJSONList.get(0).get("contract_number"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","商业模式");
                line2.put("fieldName","leaseChannelN");
                line2.put("fieldType","Text");
                line2.put("fieldValue",returnJSONList.get(0).get("lease_channel_n"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","业务模式");
                line3.put("fieldName","businessTypeN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",returnJSONList.get(0).get("business_type_n"));
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","合作方");
                line4.put("fieldName","manufacturerIdN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",returnJSONList.get(0).get("manufacturer_id_n"));
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","归属主机厂");
                line5.put("fieldName","factoryIdN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",returnJSONList.get(0).get("factory_id_n"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","经销商");
                line6.put("fieldName","bpIdVenderN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",returnJSONList.get(0).get("bp_id_vender_n"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","起租日期");
                line7.put("fieldName","inceptionOfLease");
                line7.put("fieldType","Text");
//                String inceptionOfLease = sdf.format(returnJSONList.get(0).get("inception_of_lease"));
                line7.put("fieldValue",returnJSONList.get(0).get("inception_of_lease"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","变更编号");
                line8.put("fieldName","changeReqNumber");
                line8.put("fieldType","Text");
                line8.put("fieldValue",returnJSONList.get(0).get("change_req_number"));
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","变更类型");
                line9.put("fieldName","changeTypeN");
                line9.put("fieldType","Text");
                line9.put("fieldValue",returnJSONList.get(0).get("change_type_n"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                JSONObject line10=new JSONObject(new LinkedHashMap());
                line10.put("fieldDescription","变更日期");
                line10.put("fieldName","changeReqDate");
                line10.put("fieldType","Text");
//                String changeReqDate =sdf.format(returnJSONList.get(0).get("change_req_date"));
                line10.put("fieldValue",returnJSONList.get(0).get("change_req_date"));
                line10.put("orderSeq",10);
                line10.put("isEdit",false);
                line10.put("isShow",true);
                line10.put("nullableFlag",true);

                JSONObject line11=new JSONObject(new LinkedHashMap());
                line11.put("fieldDescription","变更申请人");
                line11.put("fieldName","changeReqUserIdN");
                line11.put("fieldType","Text");
                line11.put("fieldValue",returnJSONList.get(0).get("change_req_user_id_n"));
                line11.put("orderSeq",11);
                line11.put("isEdit",false);
                line11.put("isShow",true);
                line11.put("nullableFlag",true);

                JSONObject line12=new JSONObject(new LinkedHashMap());
                line12.put("fieldDescription","当前是否逾期");
                line12.put("fieldName","currentOverdueStatus");
                line12.put("fieldType","Text");
                line12.put("fieldValue",returnJSONList.get(0).get("current_overdue_status"));
                line12.put("orderSeq",12);
                line12.put("isEdit",false);
                line12.put("isShow",true);
                line12.put("nullableFlag",true);

                JSONObject line13=new JSONObject(new LinkedHashMap());
                line13.put("fieldDescription","变更理由");
                line13.put("fieldName","description");
                line13.put("fieldType","Text");
                line13.put("fieldValue",returnJSONList.get(0).get("description"));
                line13.put("orderSeq",13);
                line13.put("isEdit",false);
                line13.put("isShow",true);
                line13.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);
                lines.add(line10);
                lines.add(line11);
                lines.add(line12);
                lines.add(line13);

                content1.put("lines",lines);
                content.add(content1);

                //变更前承租人信息
                SysDocumentHistory sysDocumentHistory = new SysDocumentHistory();
                sysDocumentHistory.setDocumentId(businessKey);
                sysDocumentHistory.setDocumentCategory("CONTRACT_CHANGE");
                IRequest iRequest1= RequestHelper.newEmptyRequest();
                JSONObject contract = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest1, businessKey, "CONTRACT_CHANGE", "con_contract").get(0).getJSONObject("data");
                HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
                hlsBpMaster.setBpId(contract.getLong("tenant_id"));
                hlsBpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectByPrimaryKey(hlsBpMaster);
                String bpClassN = codeService.getCodeValue(iRequest, "FND.BP_CLASS", hlsBpMaster.getBpClass()).getMeaning();
                hlsBpMaster.setBpClassN(bpClassN);
                List<HlsCusBpMaster> list = new ArrayList<>(1);
                list.add(hlsBpMaster);

                JSONObject content2=new JSONObject(new LinkedHashMap());
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","变更前承租人信息");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","承租人名称");
                line21.put("fieldName","bpName");
                line21.put("fieldType","Text");
                line21.put("fieldValue",list.get(0).getBpName());
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","商业伙伴分类");
                line22.put("fieldName","bpClassN");
                line22.put("fieldType","Text");
                line22.put("fieldValue",list.get(0).getBpClassN());
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","商业伙伴编码");
                line23.put("fieldName","bpCode");
                line23.put("fieldType","Text");
                line23.put("fieldValue",list.get(0).getBpCode());
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);

                content2.put("lines",lines1);
                content.add(content2);

                //变更后承租人信息

                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","变更后承租人信息");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",false);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
//                Map<String, Object> map = new HashMap<>();
//                map.put("contractId", hlsCusConContractChangeReq.getContractId());
//                HlsCusConContractBp hlsCusConContractBp=new HlsCusConContractBp();
//                hlsCusConContractBp.setContractId(hlsCusConContractChangeReq.getContractId());
//                List<HlsCusConContractBp> hlsCusConContractBps =hlsCusConContractBpMapper.queryContractBp(map);
                List<JSONObject> returnJSONList1 = new ArrayList<>();
                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_bp", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList1.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_bp");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList1.add(data);
                        });
                    }
                }


                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","承租人名称");
                line31.put("fieldName","bpName");
                line31.put("fieldType","Text");
                line31.put("fieldValue",returnJSONList1.get(0).get("bp_name"));
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","商业伙伴分类");
                line32.put("fieldName","bpClassN");
                line32.put("fieldType","Text");
                line32.put("fieldValue",returnJSONList1.get(0).get("bp_class_n"));
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","商业伙伴编码");
                line33.put("fieldName","bpCode");
                line33.put("fieldType","Text");
                line33.put("fieldValue",returnJSONList1.get(0).get("bp_code"));
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                lines2.add(line31);
                lines2.add(line32);
                lines2.add(line33);

                content3.put("lines",lines2);
                content.add(content3);

                //退回按钮
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","approve");
                header4.put("orderSeq",4);
                header4.put("hasChildren",false);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();

                JSONObject line41=new JSONObject(new LinkedHashMap());
                line41.put("fieldDescription","退回");
                line41.put("fieldName","sendback");
                line41.put("fieldType","Text");
                line41.put("fieldValue","SENDBACK");
                line41.put("orderSeq",1);
                line41.put("isEdit",false);
                line41.put("isShow",true);
                line41.put("nullableFlag",false);

                lines3.add(line41);
                content4.put("lines",lines3);
                content.add(content4);
//                JSONArray jsonArray = connContractChange2(hlsCusConContractChangeReq.getContractId(),3);
//                content.add(jsonArray.get(0));
//                content.add(jsonArray.get(1));
            }
            //缩期展期
            else if("EXTENSION".equals(hlsCusConContractChangeReq.getChangeType())) {

                IRequest iRequest= RequestHelper.newEmptyRequest();
                List<JSONObject> returnJSONList = new ArrayList<>();
                HlsCusConContractChangeReq changeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);
                //变更信息
                JSONObject content1=new JSONObject(new LinkedHashMap());
                JSONObject header1=new JSONObject(new LinkedHashMap());
                header1.put("name","变更信息");
                header1.put("icon","profile");
                header1.put("orderSeq",1);
                header1.put("hasChildren",false);
                header1.put("isShow",true);
                content1.put("header",header1);
                JSONArray lines=new JSONArray();

//                HlsCusConContractChangeReq hlsCusConContractChangeReq2 =new HlsCusConContractChangeReq();
//                hlsCusConContractChangeReq2.setChangeReqId(businessKey);
//                List<HlsCusConContractChangeReq> hlsCusConContractChangeReqs2=hlsCusConContractChangeReqMapper.queryContractChangeReqDetail((Map) hlsCusConContractChangeReq2);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList.add(data);
                        });
                    }
                }


                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","合同编号");
                line1.put("fieldName","contractNumber");
                line1.put("fieldType","Text");
                line1.put("fieldValue",returnJSONList.get(0).get("contract_number"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","商业模式");
                line6.put("fieldName","leaseChannelN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",returnJSONList.get(0).get("lease_channel_n"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","业务模式");
                line7.put("fieldName","businessTypeN");
                line7.put("fieldType","Text");
                line7.put("fieldValue",returnJSONList.get(0).get("business_type_n"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","承租人名称");
                line2.put("fieldName","bpIdTenantN");
                line2.put("fieldType","Text");
                line2.put("fieldValue",returnJSONList.get(0).get("bp_id_tenant_n"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","合作方");
                line3.put("fieldName","manufacturerIdN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",returnJSONList.get(0).get("manufacturer_id_n"));
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","主机厂");
                line4.put("fieldName","factoryIdN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",returnJSONList.get(0).get("factory_id_n"));
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","经销商");
                line5.put("fieldName","bpIdVenderN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",returnJSONList.get(0).get("bp_id_vender_n"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","项目经理");
                line8.put("fieldName","employeeIdN");
                line8.put("fieldType","Text");
                line8.put("fieldValue",returnJSONList.get(0).get("employee_id_n"));
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","变更类型");
                line9.put("fieldName","changeTypeN");
                line9.put("fieldType","Text");
                line9.put("fieldValue",returnJSONList.get(0).get("change_type_n"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                JSONObject line10=new JSONObject(new LinkedHashMap());
                line10.put("fieldDescription","变更日期");
                line10.put("fieldName","changeReqDate");
                line10.put("fieldType","Text");
//                String changeReqDate=sdf.format(returnJSONList.get(0).get("change_req_date"));
                line10.put("fieldValue",returnJSONList.get(0).get("change_req_date"));
                line10.put("orderSeq",10);
                line10.put("isEdit",false);
                line10.put("isShow",true);
                line10.put("nullableFlag",true);

                JSONObject line11=new JSONObject(new LinkedHashMap());
                line11.put("fieldDescription","合同价目表");
                line11.put("fieldName","priceListN");
                line11.put("fieldType","Text");
                line11.put("fieldValue",returnJSONList.get(0).get("change_req_date"));
                line11.put("orderSeq",5);
                line11.put("isEdit",false);
                line11.put("isShow",true);
                line11.put("nullableFlag",true);

                JSONObject line12=new JSONObject(new LinkedHashMap());
                line12.put("fieldDescription","变更价目表");
                line12.put("fieldName","ccrPriceList");
                line12.put("fieldType","Text");
                line12.put("fieldValue",returnJSONList.get(0).get("ccr_price_list_n"));
                line12.put("orderSeq",12);
                line12.put("isEdit",false);
                line12.put("isShow",true);
                line12.put("nullableFlag",true);

                JSONObject line13=new JSONObject(new LinkedHashMap());
                line13.put("fieldDescription","变更前IRR");
                line13.put("fieldName","irr");
                line13.put("fieldType","Text");
                line13.put("fieldValue",returnJSONList.get(0).get("irr"));
                line13.put("orderSeq",13);
                line13.put("isEdit",false);
                line13.put("isShow",true);
                line13.put("nullableFlag",true);

                JSONObject line14=new JSONObject(new LinkedHashMap());
                line14.put("fieldDescription","变更后IRR");
                line14.put("fieldName","changeIrr");
                line14.put("fieldType","Text");
                line14.put("fieldValue",returnJSONList.get(0).get("change_irr"));
                line14.put("orderSeq",14);
                line14.put("isEdit",false);
                line14.put("isShow",true);
                line14.put("nullableFlag",true);

                JSONObject line15=new JSONObject(new LinkedHashMap());
                line15.put("fieldDescription","变更前期数");
                line15.put("fieldName","times");
                line15.put("fieldType","Text");
                line15.put("fieldValue",returnJSONList.get(0).get("times"));
                line15.put("orderSeq",15);
                line15.put("isEdit",false);
                line15.put("isShow",true);
                line15.put("nullableFlag",true);

                JSONObject line16=new JSONObject(new LinkedHashMap());
                line16.put("fieldDescription","变更后期数");
                line16.put("fieldName","changeTimes");
                line16.put("fieldType","Text");
                line16.put("fieldValue",returnJSONList.get(0).get("change_times"));
                line16.put("orderSeq",16);
                line16.put("isEdit",false);
                line16.put("isShow",true);
                line16.put("nullableFlag",true);

                JSONObject line17=new JSONObject(new LinkedHashMap());
                line17.put("fieldDescription","变更理由");
                line17.put("fieldName","description");
                line17.put("fieldType","Text");
                line17.put("fieldValue",returnJSONList.get(0).get("description"));
                line17.put("orderSeq",17);
                line17.put("isEdit",false);
                line17.put("isShow",true);
                line17.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);
                lines.add(line10);
                lines.add(line11);
                lines.add(line12);
                lines.add(line13);
                lines.add(line14);
                lines.add(line15);
                lines.add(line16);
                lines.add(line17);

                content1.put("lines",lines);
                content.add(content1);

                //基本信息
                JSONObject content2=new JSONObject(new LinkedHashMap());
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","基本信息");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();
                List<JSONObject> returnJSONList1 = new ArrayList<>();

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList1.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList1.add(data);
                        });
                    }
                }

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","变更起始期");
                line21.put("fieldName","ccrStartTimes");
                line21.put("fieldType","Text");
                line21.put("fieldValue",returnJSONList1.get(0).get("ccr_start_times"));
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","变更后剩余期数");
                line22.put("fieldName","ccrOutstandingTimes");
                line22.put("fieldType","Text");
                line22.put("fieldValue",returnJSONList1.get(0).get("ccr_outstanding_times"));
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","变更手续费");
                line23.put("fieldName","ccrFee");
                line23.put("fieldType","Text");
                line23.put("fieldValue",returnJSONList1.get(0).get("ccr_fee"));
                line23.put("orderSeq",2);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);

                content2.put("lines",lines1);
                content.add(content2);

                //变更后现金流
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","变更后现金流");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",true);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                Map<String, Object> map = new HashMap<>();
                map.put("contractId", hlsCusConContractChangeReq.getContractId());
//                HlsCusConContractCashflow hlsCusConContractCashflow=new HlsCusConContractCashflow();
//                hlsCusConContractCashflow.setContractId(hlsCusConContractChangeReq.getContractId());
                List<HlsCusConContractCashflow> hlsCusConContractCashflows=hlsCusConContractCashflowMapper.queryConContractCashflowDetail(map);
                for (HlsCusConContractCashflow hlsCusConContractCashflow1 : hlsCusConContractCashflows) {
                    JSONArray lines21 = new JSONArray();

                    JSONObject line31 = new JSONObject(new LinkedHashMap());
                    line31.put("fieldDescription", "期数");
                    line31.put("fieldName", "times");
                    line31.put("fieldType", "Text");
                    line31.put("fieldValue", hlsCusConContractCashflow1.getTimes());
                    line31.put("orderSeq", 1);
                    line31.put("isEdit", false);
                    line31.put("isShow", true);
                    line31.put("nullableFlag", true);

                    JSONObject line32 = new JSONObject(new LinkedHashMap());
                    line32.put("fieldDescription", "现金流项目");
                    line32.put("fieldName", "cfItemN");
                    line32.put("fieldType", "Text");
                    line32.put("fieldValue", hlsCusConContractCashflow1.getCfItemN());
                    line32.put("orderSeq", 2);
                    line32.put("isEdit", false);
                    line32.put("isShow", true);
                    line32.put("nullableFlag", true);

                    JSONObject line33 = new JSONObject(new LinkedHashMap());
                    line33.put("fieldDescription", "计算日");
                    line33.put("fieldName", "calcDate");
                    line33.put("fieldType", "Text");
                    String calcDate=sdf.format(hlsCusConContractCashflow1.getCalcDate());
                    line33.put("fieldValue", calcDate);
                    line33.put("orderSeq", 3);
                    line33.put("isEdit", false);
                    line33.put("isShow", true);
                    line33.put("nullableFlag", true);

                    JSONObject line34 = new JSONObject(new LinkedHashMap());
                    line34.put("fieldDescription", "应收金额");
                    line34.put("fieldName", "dueAmount");
                    line34.put("fieldType", "Text");
                    line34.put("fieldValue", hlsCusConContractCashflow1.getDueAmount());
                    line34.put("orderSeq", 4);
                    line34.put("isEdit", false);
                    line34.put("isShow", true);
                    line34.put("nullableFlag", true);

                    JSONObject line35 = new JSONObject(new LinkedHashMap());
                    line35.put("fieldDescription", "应收本金");
                    line35.put("fieldName", "principal");
                    line35.put("fieldType", "Text");
                    line35.put("fieldValue", hlsCusConContractCashflow1.getPrincipal());
                    line35.put("orderSeq", 5);
                    line35.put("isEdit", false);
                    line35.put("isShow", true);
                    line35.put("nullableFlag", true);

                    JSONObject line36 = new JSONObject(new LinkedHashMap());
                    line36.put("fieldDescription", "应收利息");
                    line36.put("fieldName", "interest");
                    line36.put("fieldType", "Text");
                    line36.put("fieldValue", hlsCusConContractCashflow1.getInterest());
                    line36.put("orderSeq", 6);
                    line36.put("isEdit", false);
                    line36.put("isShow", true);
                    line36.put("nullableFlag", true);

                    JSONObject line37 = new JSONObject(new LinkedHashMap());
                    line37.put("fieldDescription", "当前剩余本金");
                    line37.put("fieldName", "outstandingPrincipal");
                    line37.put("fieldType", "Text");
                    line37.put("fieldValue", hlsCusConContractCashflow1.getOutstandingPrincipal());
                    line37.put("orderSeq", 7);
                    line37.put("isEdit", false);
                    line37.put("isShow", true);
                    line37.put("nullableFlag", true);

                    JSONObject line38 = new JSONObject(new LinkedHashMap());
                    line38.put("fieldDescription", "已收金额");
                    line38.put("fieldName", "receivedAmount");
                    line38.put("fieldType", "Text");
                    line38.put("fieldValue", hlsCusConContractCashflow1.getReceivedAmount());
                    line38.put("orderSeq", 8);
                    line38.put("isEdit", false);
                    line38.put("isShow", true);
                    line38.put("nullableFlag", true);

                    JSONObject line39 = new JSONObject(new LinkedHashMap());
                    line39.put("fieldDescription", "核销状态");
                    line39.put("fieldName", "writeOffFlagN");
                    line39.put("fieldType", "Text");
                    line39.put("fieldValue", hlsCusConContractCashflow1.getWriteOffFlagN());
                    line39.put("orderSeq", 9);
                    line39.put("isEdit", false);
                    line39.put("isShow", true);
                    line39.put("nullableFlag", true);

                    lines21.add(line31);
                    lines21.add(line32);
                    lines21.add(line33);
                    lines21.add(line34);
                    lines21.add(line35);
                    lines21.add(line36);
                    lines21.add(line37);
                    lines21.add(line38);
                    lines21.add(line39);
                    lines2.add(lines21);

                }
                content3.put("lines",lines2);
                content.add(content3);

                //变更前现金流
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","变更前现金流");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                List<JSONObject> returnJSONList2 = new ArrayList<>();

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList2.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList2.add(data);
                        });
                    }
                }

                for (JSONObject jsonObject : returnJSONList2) {
                    JSONArray lines31 = new JSONArray();

                    JSONObject line41 = new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription", "期数");
                    line41.put("fieldName", "times");
                    line41.put("fieldType", "Text");
                    line41.put("fieldValue", jsonObject.get("times"));
                    line41.put("orderSeq", 1);
                    line41.put("isEdit", false);
                    line41.put("isShow", true);
                    line41.put("nullableFlag", true);

                    JSONObject line42 = new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription", "现金流项目");
                    line42.put("fieldName", "cfItemN");
                    line42.put("fieldType", "Text");
                    line42.put("fieldValue", jsonObject.get("cf_item_n"));
                    line42.put("orderSeq", 2);
                    line42.put("isEdit", false);
                    line42.put("isShow", true);
                    line42.put("nullableFlag", true);

                    JSONObject line43 = new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription", "计算日");
                    line43.put("fieldName", "calcDate");
                    line43.put("fieldType", "Text");
//                    String calcDate=sdf.format(jsonObject.get("calc_date"));
                    line43.put("fieldValue", jsonObject.get("calc_date"));
                    line43.put("orderSeq", 3);
                    line43.put("isEdit", false);
                    line43.put("isShow", true);
                    line43.put("nullableFlag", true);

                    JSONObject line44 = new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription", "应收金额");
                    line44.put("fieldName", "dueAmount");
                    line44.put("fieldType", "Text");
                    line44.put("fieldValue", jsonObject.get("due_amount"));
                    line44.put("orderSeq", 4);
                    line44.put("isEdit", false);
                    line44.put("isShow", true);
                    line44.put("nullableFlag", true);

                    JSONObject line45 = new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription", "应收本金");
                    line45.put("fieldName", "principal");
                    line45.put("fieldType", "Text");
                    line45.put("fieldValue", jsonObject.get("principal"));
                    line45.put("orderSeq", 5);
                    line45.put("isEdit", false);
                    line45.put("isShow", true);
                    line45.put("nullableFlag", true);

                    JSONObject line46 = new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription", "应收利息");
                    line46.put("fieldName", "interest");
                    line46.put("fieldType", "Text");
                    line46.put("fieldValue", jsonObject.get("interest"));
                    line46.put("orderSeq", 6);
                    line46.put("isEdit", false);
                    line46.put("isShow", true);
                    line46.put("nullableFlag", true);

                    JSONObject line47 = new JSONObject(new LinkedHashMap());
                    line47.put("fieldDescription", "当前剩余本金");
                    line47.put("fieldName", "outstandingPrincipal");
                    line47.put("fieldType", "Text");
                    line47.put("fieldValue", jsonObject.get("outstanding_principal"));
                    line47.put("orderSeq", 7);
                    line47.put("isEdit", false);
                    line47.put("isShow", true);
                    line47.put("nullableFlag", true);

                    JSONObject line48 = new JSONObject(new LinkedHashMap());
                    line48.put("fieldDescription", "已收金额");
                    line48.put("fieldName", "receivedAmount");
                    line48.put("fieldType", "Text");
                    line48.put("fieldValue", jsonObject.get("received_amount"));
                    line48.put("orderSeq", 8);
                    line48.put("isEdit", false);
                    line48.put("isShow", true);
                    line48.put("nullableFlag", true);

                    JSONObject line49 = new JSONObject(new LinkedHashMap());
                    line49.put("fieldDescription", "核销状态");
                    line49.put("fieldName", "writeOffFlagN");
                    line49.put("fieldType", "Text");
                    line49.put("fieldValue", jsonObject.get("write_off_flag_n"));
                    line49.put("orderSeq", 9);
                    line49.put("isEdit", false);
                    line49.put("isShow", true);
                    line49.put("nullableFlag", true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);
                    lines31.add(line47);
                    lines31.add(line48);
                    lines31.add(line49);
                    lines3.add(lines31);

                }
                content4.put("lines",lines3);
                content.add(content4);

                //退回按钮
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","approve");
                header5.put("orderSeq",5);
                header5.put("hasChildren",false);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();

                JSONObject line51=new JSONObject(new LinkedHashMap());
                line51.put("fieldDescription","退回");
                line51.put("fieldName","sendback");
                line51.put("fieldType","Text");
                line51.put("fieldValue","SENDBACK");
                line51.put("orderSeq",1);
                line51.put("isEdit",false);
                line51.put("isShow",true);
                line51.put("nullableFlag",false);

                lines4.add(line51);
                content5.put("lines",lines4);
                content.add(content5);

            }
            //提前结清
            else if("PREPAYMENT".equals(hlsCusConContractChangeReq.getChangeType())){
                IRequest iRequest= RequestHelper.newEmptyRequest();
                List<JSONObject> returnJSONList = new ArrayList<>();
                HlsCusConContractChangeReq changeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);

                //基本信息
                JSONObject content1=new JSONObject(new LinkedHashMap());
                JSONObject header1=new JSONObject(new LinkedHashMap());
                header1.put("name","基本信息");
                header1.put("icon","profile");
                header1.put("orderSeq",1);
                header1.put("hasChildren",false);
                header1.put("isShow",true);
                content1.put("header",header1);
                JSONArray lines=new JSONArray();

//                HlsCusConContract hlsCusConContract =new HlsCusConContract();
//                hlsCusConContract.setContractId(hlsCusConContractChangeReq.getContractId());
//                List<HlsCusConContract> hlsCusConContracts=hlsCusConContractMapper.queryConContractDetails((Map) hlsCusConContract);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList.add(data);
                        });
                    }
                }

                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","合同编号");
                line1.put("fieldName","contractNumber");
                line1.put("fieldType","Text");
                line1.put("fieldValue",returnJSONList.get(0).get("contract_number"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","商业模式");
                line5.put("fieldName","leaseChannelN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",returnJSONList.get(0).get("lease_channel_n"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","业务模式");
                line6.put("fieldName","businessTypeN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",returnJSONList.get(0).get("business_type_n"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","承租人名称");
                line2.put("fieldName","bpIdTenantN");
                line2.put("fieldType","Text");
                line2.put("fieldValue",returnJSONList.get(0).get("bp_id_tenant_n"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","合作方");
                line3.put("fieldName","manufacturerIdN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",returnJSONList.get(0).get("manufacturer_id_n"));
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);


                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","经销商");
                line4.put("fieldName","bpIdVenderN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",returnJSONList.get(0).get("bp_id_vender_n"));
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","起租日期");
                line8.put("fieldName","leaseStartDate");
                line8.put("fieldType","Text");
                String leaseStartDate = sdf.format(returnJSONList.get(0).getDate("lease_start_date"));
                line8.put("fieldValue",leaseStartDate);
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","融资额");
                line7.put("fieldName","financeAmount");
                line7.put("fieldType","Text");
                line7.put("fieldValue",returnJSONList.get(0).get("finance_amount"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","项目经理");
                line9.put("fieldName","employeeIdN");
                line9.put("fieldType","Text");
                line9.put("fieldValue",returnJSONList.get(0).get("employee_id_n"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);



                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);


                content1.put("lines",lines);
                content.add(content1);

                //变更信息
                JSONObject content2=new JSONObject(new LinkedHashMap());
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","变更信息");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();
                List<JSONObject> returnJSONList1 = new ArrayList<>();

//                HlsCusConContract hlsCusConContract1=new HlsCusConContract();
//                hlsCusConContract1.setChangeReqId(businessKey);
//                Map hlsCusConContract1s=hlsCusConContractMapper.selectPrePaymentChangeReqInfo(hlsCusConContract1);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList1.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList1.add(data);
                        });
                    }
                }

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","提前结清期数");
                line21.put("fieldName","times");
                line21.put("fieldType","Text");
                line21.put("fieldValue",returnJSONList1.get(0).get("times"));
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","提前结清日");
                line22.put("fieldName","dueDate");
                line22.put("fieldType","Text");
                line22.put("fieldValue",returnJSONList1.get(0).get("due_date"));
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","逾期租金");
                line23.put("fieldName","overdueAmount");
                line23.put("fieldType","Text");
                line23.put("fieldValue",returnJSONList1.get(0).get("overdue_amount"));
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","剩余本金");
                line24.put("fieldName","outstandingPrincipal");
                line24.put("fieldType","Text");
                line24.put("fieldValue",returnJSONList1.get(0).get("outstanding_principal"));
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                line25.put("fieldDescription","期间利息");
                line25.put("fieldName","periodInterest");
                line25.put("fieldType","Text");
                line25.put("fieldValue",returnJSONList1.get(0).get("period_interest"));
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                JSONObject line26=new JSONObject(new LinkedHashMap());
                line26.put("fieldDescription","期间利息变更");
                line26.put("fieldName","periodInterestChange");
                line26.put("fieldType","Text");
                line26.put("fieldValue",returnJSONList1.get(0).get("period_interest_change"));
                line26.put("orderSeq",6);
                line26.put("isEdit",false);
                line26.put("isShow",true);
                line26.put("nullableFlag",true);

                JSONObject line27=new JSONObject(new LinkedHashMap());
                line27.put("fieldDescription","保证金是否抵扣");
                line27.put("fieldName","depositDeductFlag");
                line27.put("fieldType","Text");
                line27.put("fieldValue",returnJSONList1.get(0).get("deposit_deduct_flag_n"));
                line27.put("orderSeq",7);
                line27.put("isEdit",false);
                line27.put("isShow",true);
                line27.put("nullableFlag",true);

                JSONObject line28=new JSONObject(new LinkedHashMap());
                line28.put("fieldDescription","保证金");
                line28.put("fieldName","deposit");
                line28.put("fieldType","Text");
                line28.put("fieldValue",returnJSONList1.get(0).get("deposit"));
                line28.put("orderSeq",8);
                line28.put("isEdit",false);
                line28.put("isShow",true);
                line28.put("nullableFlag",true);

                JSONObject line29=new JSONObject(new LinkedHashMap());
                line29.put("fieldDescription","罚息金额");
                line29.put("fieldName","penaltyInterest");
                line29.put("fieldType","Text");
                line29.put("fieldValue",returnJSONList1.get(0).get("penalty_interest"));
                line29.put("orderSeq",9);
                line29.put("isEdit",false);
                line29.put("isShow",true);
                line29.put("nullableFlag",true);

                JSONObject line210=new JSONObject(new LinkedHashMap());
                line210.put("fieldDescription","名义货价");
                line210.put("fieldName","nominalPrice");
                line210.put("fieldType","Text");
                line210.put("fieldValue",returnJSONList1.get(0).get("nominal_price"));
                line210.put("orderSeq",10);
                line210.put("isEdit",false);
                line210.put("isShow",true);
                line210.put("nullableFlag",true);

                JSONObject line211=new JSONObject(new LinkedHashMap());
                line211.put("fieldDescription","提前结清款");
                line211.put("fieldName","preAmount");
                line211.put("fieldType","Text");
                line211.put("fieldValue",returnJSONList1.get(0).get("pre_amount"));
                line211.put("orderSeq",11);
                line211.put("isEdit",false);
                line211.put("isShow",true);
                line211.put("nullableFlag",true);

                JSONObject line212=new JSONObject(new LinkedHashMap());
                line212.put("fieldDescription","厂商返利");
                line212.put("fieldName","replyAmount");
                line212.put("fieldType","Text");
                line212.put("fieldValue",returnJSONList1.get(0).get("reply_amount"));
                line212.put("orderSeq",12);
                line212.put("isEdit",false);
                line212.put("isShow",true);
                line212.put("nullableFlag",true);

                JSONObject line213=new JSONObject(new LinkedHashMap());
                line213.put("fieldDescription","厂商贴息");
                line213.put("fieldName","discountAmount");
                line213.put("fieldType","Text");
                line213.put("fieldValue",returnJSONList1.get(0).get("discount_amount"));
                line213.put("orderSeq",13);
                line213.put("isEdit",false);
                line213.put("isShow",true);
                line213.put("nullableFlag",true);

                JSONObject line214=new JSONObject(new LinkedHashMap());
                line214.put("fieldDescription","IRR");
                line214.put("fieldName","irr");
                line214.put("fieldType","Text");
                line214.put("fieldValue",returnJSONList1.get(0).get("irr"));
                line214.put("orderSeq",14);
                line214.put("isEdit",false);
                line214.put("isShow",true);
                line214.put("nullableFlag",true);

                JSONObject line215=new JSONObject(new LinkedHashMap());
                line215.put("fieldDescription","变更手续费");
                line215.put("fieldName","etFee");
                line215.put("fieldType","Text");
                line215.put("fieldValue",returnJSONList1.get(0).get("et_fee"));
                line215.put("orderSeq",15);
                line215.put("isEdit",false);
                line215.put("isShow",true);
                line215.put("nullableFlag",true);

                JSONObject line216=new JSONObject(new LinkedHashMap());
                line216.put("fieldDescription","变更后的IRR");
                line216.put("fieldName","changeIrr");
                line216.put("fieldType","Text");
                line216.put("fieldValue",returnJSONList1.get(0).get("change_irr"));
                line216.put("orderSeq",2);
                line216.put("isEdit",false);
                line216.put("isShow",true);
                line216.put("nullableFlag",true);

                JSONObject line217=new JSONObject(new LinkedHashMap());
                line217.put("fieldDescription","变更原因");
                line217.put("fieldName","description");
                line217.put("fieldType","Text");
                line217.put("fieldValue",returnJSONList1.get(0).get("description"));
                line217.put("orderSeq",2);
                line217.put("isEdit",false);
                line217.put("isShow",true);
                line217.put("nullableFlag",true);



                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);
                lines1.add(line24);
                lines1.add(line25);
                lines1.add(line26);
                lines1.add(line27);
                lines1.add(line28);
                lines1.add(line29);
                lines1.add(line210);
                lines1.add(line211);
                lines1.add(line212);
                lines1.add(line213);
                lines1.add(line214);
                lines1.add(line215);
                lines1.add(line216);
                lines1.add(line217);

                content2.put("lines",lines1);
                content.add(content2);

                //变更后现金流
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","变更后现金流");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",true);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                Map<String, Object> map = new HashMap<>();
                map.put("contractId", hlsCusConContractChangeReq.getContractId());
//                HlsCusConContractCashflow hlsCusConContractCashflow=new HlsCusConContractCashflow();
//                hlsCusConContractCashflow.setContractId(hlsCusConContractChangeReq.getContractId());
                List<HlsCusConContractCashflow> hlsCusConContractCashflows=hlsCusConContractCashflowMapper.queryConContractCashflowDetail(map);
                for (HlsCusConContractCashflow hlsCusConContractCashflow1 : hlsCusConContractCashflows) {
                    JSONArray lines21 = new JSONArray();

                    JSONObject line31 = new JSONObject(new LinkedHashMap());
                    line31.put("fieldDescription", "期数");
                    line31.put("fieldName", "times");
                    line31.put("fieldType", "Text");
                    line31.put("fieldValue", hlsCusConContractCashflow1.getTimes());
                    line31.put("orderSeq", 1);
                    line31.put("isEdit", false);
                    line31.put("isShow", true);
                    line31.put("nullableFlag", true);

                    JSONObject line32 = new JSONObject(new LinkedHashMap());
                    line32.put("fieldDescription", "现金流项目");
                    line32.put("fieldName", "cfItemN");
                    line32.put("fieldType", "Text");
                    line32.put("fieldValue", hlsCusConContractCashflow1.getCfItemN());
                    line32.put("orderSeq", 2);
                    line32.put("isEdit", false);
                    line32.put("isShow", true);
                    line32.put("nullableFlag", true);

                    JSONObject line33 = new JSONObject(new LinkedHashMap());
                    line33.put("fieldDescription", "计算日");
                    line33.put("fieldName", "calcDate");
                    line33.put("fieldType", "Text");
                    String calcDate=sdf.format(hlsCusConContractCashflow1.getCalcDate());
                    line33.put("fieldValue", calcDate);
                    line33.put("orderSeq", 3);
                    line33.put("isEdit", false);
                    line33.put("isShow", true);
                    line33.put("nullableFlag", true);

                    JSONObject line34 = new JSONObject(new LinkedHashMap());
                    line34.put("fieldDescription", "应收金额");
                    line34.put("fieldName", "dueAmount");
                    line34.put("fieldType", "Text");
                    line34.put("fieldValue", hlsCusConContractCashflow1.getDueAmount());
                    line34.put("orderSeq", 4);
                    line34.put("isEdit", false);
                    line34.put("isShow", true);
                    line34.put("nullableFlag", true);

                    JSONObject line35 = new JSONObject(new LinkedHashMap());
                    line35.put("fieldDescription", "应收本金");
                    line35.put("fieldName", "principal");
                    line35.put("fieldType", "Text");
                    line35.put("fieldValue", hlsCusConContractCashflow1.getPrincipal());
                    line35.put("orderSeq", 5);
                    line35.put("isEdit", false);
                    line35.put("isShow", true);
                    line35.put("nullableFlag", true);

                    JSONObject line36 = new JSONObject(new LinkedHashMap());
                    line36.put("fieldDescription", "应收利息");
                    line36.put("fieldName", "interest");
                    line36.put("fieldType", "Text");
                    line36.put("fieldValue", hlsCusConContractCashflow1.getInterest());
                    line36.put("orderSeq", 6);
                    line36.put("isEdit", false);
                    line36.put("isShow", true);
                    line36.put("nullableFlag", true);

                    JSONObject line37 = new JSONObject(new LinkedHashMap());
                    line37.put("fieldDescription", "当前剩余本金");
                    line37.put("fieldName", "outstandingPrincipal");
                    line37.put("fieldType", "Text");
                    line37.put("fieldValue", hlsCusConContractCashflow1.getOutstandingPrincipal());
                    line37.put("orderSeq", 7);
                    line37.put("isEdit", false);
                    line37.put("isShow", true);
                    line37.put("nullableFlag", true);

                    JSONObject line38 = new JSONObject(new LinkedHashMap());
                    line38.put("fieldDescription", "已收金额");
                    line38.put("fieldName", "receivedAmount");
                    line38.put("fieldType", "Text");
                    line38.put("fieldValue", hlsCusConContractCashflow1.getReceivedAmount());
                    line38.put("orderSeq", 8);
                    line38.put("isEdit", false);
                    line38.put("isShow", true);
                    line38.put("nullableFlag", true);

                    JSONObject line39 = new JSONObject(new LinkedHashMap());
                    line39.put("fieldDescription", "核销状态");
                    line39.put("fieldName", "writeOffFlagN");
                    line39.put("fieldType", "Text");
                    line39.put("fieldValue", hlsCusConContractCashflow1.getWriteOffFlagN());
                    line39.put("orderSeq", 9);
                    line39.put("isEdit", false);
                    line39.put("isShow", true);
                    line39.put("nullableFlag", true);

                    lines21.add(line31);
                    lines21.add(line32);
                    lines21.add(line33);
                    lines21.add(line34);
                    lines21.add(line35);
                    lines21.add(line36);
                    lines21.add(line37);
                    lines21.add(line38);
                    lines21.add(line39);
                    lines2.add(lines21);

                }
                content3.put("lines",lines2);
                content.add(content3);

                //变更前现金流
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","变更前现金流");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                List<JSONObject> returnJSONList2 = new ArrayList<>();

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList2.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList2.add(data);
                        });
                    }
                }

                for (JSONObject jsonObject : returnJSONList2) {
                    JSONArray lines31 = new JSONArray();

                    JSONObject line41 = new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription", "期数");
                    line41.put("fieldName", "times");
                    line41.put("fieldType", "Text");
                    line41.put("fieldValue", jsonObject.get("times"));
                    line41.put("orderSeq", 1);
                    line41.put("isEdit", false);
                    line41.put("isShow", true);
                    line41.put("nullableFlag", true);

                    JSONObject line42 = new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription", "现金流项目");
                    line42.put("fieldName", "cfItemN");
                    line42.put("fieldType", "Text");
                    line42.put("fieldValue", jsonObject.get("cf_item_n"));
                    line42.put("orderSeq", 2);
                    line42.put("isEdit", false);
                    line42.put("isShow", true);
                    line42.put("nullableFlag", true);

                    JSONObject line43 = new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription", "计算日");
                    line43.put("fieldName", "calcDate");
                    line43.put("fieldType", "Text");
                    String calcDate=sdf.format(jsonObject.getDate("calc_date"));
                    line43.put("fieldValue", calcDate);
                    line43.put("orderSeq", 3);
                    line43.put("isEdit", false);
                    line43.put("isShow", true);
                    line43.put("nullableFlag", true);

                    JSONObject line44 = new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription", "应收金额");
                    line44.put("fieldName", "dueAmount");
                    line44.put("fieldType", "Text");
                    line44.put("fieldValue", jsonObject.get("due_amount"));
                    line44.put("orderSeq", 4);
                    line44.put("isEdit", false);
                    line44.put("isShow", true);
                    line44.put("nullableFlag", true);

                    JSONObject line45 = new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription", "应收本金");
                    line45.put("fieldName", "principal");
                    line45.put("fieldType", "Text");
                    line45.put("fieldValue", jsonObject.get("principal"));
                    line45.put("orderSeq", 5);
                    line45.put("isEdit", false);
                    line45.put("isShow", true);
                    line45.put("nullableFlag", true);

                    JSONObject line46 = new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription", "应收利息");
                    line46.put("fieldName", "interest");
                    line46.put("fieldType", "Text");
                    line46.put("fieldValue", jsonObject.get("interest"));
                    line46.put("orderSeq", 6);
                    line46.put("isEdit", false);
                    line46.put("isShow", true);
                    line46.put("nullableFlag", true);

                    JSONObject line47 = new JSONObject(new LinkedHashMap());
                    line47.put("fieldDescription", "当前剩余本金");
                    line47.put("fieldName", "outstandingPrincipal");
                    line47.put("fieldType", "Text");
                    line47.put("fieldValue", jsonObject.get("outstanding_principal"));
                    line47.put("orderSeq", 7);
                    line47.put("isEdit", false);
                    line47.put("isShow", true);
                    line47.put("nullableFlag", true);

                    JSONObject line48 = new JSONObject(new LinkedHashMap());
                    line48.put("fieldDescription", "已收金额");
                    line48.put("fieldName", "receivedAmount");
                    line48.put("fieldType", "Text");
                    line48.put("fieldValue", jsonObject.get("received_amount"));
                    line48.put("orderSeq", 8);
                    line48.put("isEdit", false);
                    line48.put("isShow", true);
                    line48.put("nullableFlag", true);

                    JSONObject line49 = new JSONObject(new LinkedHashMap());
                    line49.put("fieldDescription", "核销状态");
                    line49.put("fieldName", "writeOffFlagN");
                    line49.put("fieldType", "Text");
                    line49.put("fieldValue", jsonObject.get("write_off_flag_n"));
                    line49.put("orderSeq", 9);
                    line49.put("isEdit", false);
                    line49.put("isShow", true);
                    line49.put("nullableFlag", true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);
                    lines31.add(line47);
                    lines31.add(line48);
                    lines31.add(line49);
                    lines3.add(lines31);

                }
                content4.put("lines",lines3);
                content.add(content4);

                //退回按钮
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","approve");
                header5.put("orderSeq",5);
                header5.put("hasChildren",false);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();

                JSONObject line51=new JSONObject(new LinkedHashMap());
                line51.put("fieldDescription","退回");
                line51.put("fieldName","sendback");
                line51.put("fieldType","Text");
                line51.put("fieldValue","SENDBACK");
                line51.put("orderSeq",1);
                line51.put("isEdit",false);
                line51.put("isShow",true);
                line51.put("nullableFlag",false);

                lines4.add(line51);
                content5.put("lines",lines4);
                content.add(content5);

//                JSONArray jsonArray = connContractChange2(hlsCusConContractChangeReq.getContractId(),businessKey,"CON_CONTRACT_PREPAYMENT_CHANGE",4);
//                content.add(jsonArray.get(0));
//                content.add(jsonArray.get(1));

            }
            //付息延期
            else if("DELAY".equals(hlsCusConContractChangeReq.getChangeType())){
                IRequest iRequest= RequestHelper.newEmptyRequest();
                List<JSONObject> returnJSONList = new ArrayList<>();
                HlsCusConContractChangeReq changeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);

                //变更信息
                JSONObject content1=new JSONObject(new LinkedHashMap());
                JSONObject header1=new JSONObject(new LinkedHashMap());
                header1.put("name","变更信息");
                header1.put("icon","profile");
                header1.put("orderSeq",1);
                header1.put("hasChildren",false);
                header1.put("isShow",true);
                content1.put("header",header1);
                JSONArray lines=new JSONArray();
//
//                HlsCusConContractChangeReq hlsCusConContractChangeReq2 =new HlsCusConContractChangeReq();
//                hlsCusConContractChangeReq2.setChangeReqId(businessKey);
//                List<HlsCusConContractChangeReq> hlsCusConContractChangeReqs2=hlsCusConContractChangeReqMapper.queryContractChangeReqDetail((Map) hlsCusConContractChangeReq2);

//                HlsCusConContractChangeReq hlsCusConContractChangeReq1=hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);
//
//
//                JSONObject line1=new JSONObject(new LinkedHashMap());
//                line1.put("fieldDescription","合同编号");
//                line1.put("fieldName","contractNumber");
//                line1.put("fieldType","Text");
//                line1.put("fieldValue",hlsCusConContractChangeReq1.getContractNumber());
//                line1.put("orderSeq",1);
//                line1.put("isEdit",false);
//                line1.put("isShow",true);
//                line1.put("nullableFlag",true);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList.add(data);
                        });
                    }
                }

                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","合同编号");
                line1.put("fieldName","contractNumber");
                line1.put("fieldType","Text");
                line1.put("fieldValue",returnJSONList.get(0).get("contract_number"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);
//
                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","商业模式");
                line6.put("fieldName","leaseChannelN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",returnJSONList.get(0).get("lease_channel_n"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","业务模式");
                line7.put("fieldName","businessTypeN");
                line7.put("fieldType","Text");
                line7.put("fieldValue",returnJSONList.get(0).get("business_type_n"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","承租人名称");
                line2.put("fieldName","bpIdTenantN");
                line2.put("fieldType","Text");
                line2.put("fieldValue",returnJSONList.get(0).get("bp_id_tenant_n"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","合作方");
                line3.put("fieldName","manufacturerIdN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",returnJSONList.get(0).get("manufacturer_id_n"));
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","主机厂");
                line4.put("fieldName","factoryIdN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",returnJSONList.get(0).get("factory_id_n"));
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","经销商");
                line5.put("fieldName","bpIdVenderN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",returnJSONList.get(0).get("bp_id_vender_n"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","项目经理");
                line8.put("fieldName","employeeIdN");
                line8.put("fieldType","Text");
                line8.put("fieldValue",returnJSONList.get(0).get("employee_id_n"));
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line10=new JSONObject(new LinkedHashMap());
                line10.put("fieldDescription","变更类型");
                line10.put("fieldName","changeTypeN");
                line10.put("fieldType","Text");
                line10.put("fieldValue",returnJSONList.get(0).get("change_type_n"));
                line10.put("orderSeq",10);
                line10.put("isEdit",false);
                line10.put("isShow",true);
                line10.put("nullableFlag",true);

                JSONObject line11=new JSONObject(new LinkedHashMap());
                line11.put("fieldDescription","变更日期");
                line11.put("fieldName","changeReqDate");
                line11.put("fieldType","Text");
//                String changeReqDate=sdf.format(returnJSONList.get(0).get("change_req_date"));
//                line11.put("fieldValue",changeReqDate);
                line11.put("fieldValue",returnJSONList.get(0).get("change_req_date"));
                line11.put("orderSeq",11);
                line11.put("isEdit",false);
                line11.put("isShow",true);
                line11.put("nullableFlag",true);
//
                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","变更编号");
                line9.put("fieldName","changeReqNumber");
                line9.put("fieldType","Text");
                line9.put("fieldValue",returnJSONList.get(0).get("change_req_number"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                JSONObject line12=new JSONObject(new LinkedHashMap());
                line12.put("fieldDescription","变更价目表");
                line12.put("fieldName","ccrPriceList");
                line12.put("fieldType","Text");
                line12.put("fieldValue",returnJSONList.get(0).get("ccr_price_list_n"));
                line12.put("orderSeq",12);
                line12.put("isEdit",false);
                line12.put("isShow",true);
                line12.put("nullableFlag",true);

                JSONObject line13=new JSONObject(new LinkedHashMap());
                line13.put("fieldDescription","变更前IRR");
                line13.put("fieldName","irr");
                line13.put("fieldType","Text");
                line13.put("fieldValue",returnJSONList.get(0).get("irr"));
                line13.put("orderSeq",13);
                line13.put("isEdit",false);
                line13.put("isShow",true);
                line13.put("nullableFlag",true);

                JSONObject line14=new JSONObject(new LinkedHashMap());
                line14.put("fieldDescription","变更后IRR");
                line14.put("fieldName","changeIrr");
                line14.put("fieldType","Text");
                line14.put("fieldValue",returnJSONList.get(0).get("change_irr"));
                line14.put("orderSeq",14);
                line14.put("isEdit",false);
                line14.put("isShow",true);
                line14.put("nullableFlag",true);

                JSONObject line15=new JSONObject(new LinkedHashMap());
                line15.put("fieldDescription","变更前期数");
                line15.put("fieldName","times");
                line15.put("fieldType","Text");
                line15.put("fieldValue",returnJSONList.get(0).get("times"));
                line15.put("orderSeq",15);
                line15.put("isEdit",false);
                line15.put("isShow",true);
                line15.put("nullableFlag",true);

                JSONObject line16=new JSONObject(new LinkedHashMap());
                line16.put("fieldDescription","变更后期数");
                line16.put("fieldName","changeTimes");
                line16.put("fieldType","Text");
                line16.put("fieldValue",returnJSONList.get(0).get("change_times"));
                line16.put("orderSeq",16);
                line16.put("isEdit",false);
                line16.put("isShow",true);
                line16.put("nullableFlag",true);

                JSONObject line17=new JSONObject(new LinkedHashMap());
                line17.put("fieldDescription","变更理由");
                line17.put("fieldName","description");
                line17.put("fieldType","Text");
                line17.put("fieldValue",returnJSONList.get(0).get("description"));
                line17.put("orderSeq",17);
                line17.put("isEdit",false);
                line17.put("isShow",true);
                line17.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);
                lines.add(line10);
                lines.add(line11);
                lines.add(line12);
                lines.add(line13);
                lines.add(line14);
                lines.add(line15);
                lines.add(line16);
                lines.add(line17);

                content1.put("lines",lines);
                content.add(content1);

//                基本信息
                JSONObject content2=new JSONObject(new LinkedHashMap());
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","基本信息");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();
                List<JSONObject> returnJSONList1 = new ArrayList<>();

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList1.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList1.add(data);
                        });
                    }
                }

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","变更起始期");
                line21.put("fieldName","ccrStartTimes");
                line21.put("fieldType","Text");
                line21.put("fieldValue",returnJSONList1.get(0).get("ccr_start_times"));
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","变更期数");
                line22.put("fieldName","ccrOutstandingTimes");
                line22.put("fieldType","Text");
                line22.put("fieldValue",returnJSONList1.get(0).get("ccr_outstanding_times"));
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","变更手续费");
                line23.put("fieldName","ccrFee");
                line23.put("fieldType","Text");
                line23.put("fieldValue",returnJSONList1.get(0).get("ccr_fee"));
                line23.put("orderSeq",2);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);

                content2.put("lines",lines1);
                content.add(content2);

//                变更后现金流
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","变更后现金流");
                header3.put("icon","profile");
                header3.put("orderSeq",3);
                header3.put("hasChildren",true);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                Map<String, Object> map = new HashMap<>();
                map.put("contractId", hlsCusConContractChangeReq.getContractId());
                List<HlsCusConContractCashflow> hlsCusConContractCashflows=hlsCusConContractCashflowMapper.queryConContractCashflowDetail(map);
                for (HlsCusConContractCashflow hlsCusConContractCashflow1 : hlsCusConContractCashflows) {
                    JSONArray lines21 = new JSONArray();

                    JSONObject line31 = new JSONObject(new LinkedHashMap());
                    line31.put("fieldDescription", "期数");
                    line31.put("fieldName", "times");
                    line31.put("fieldType", "Text");
                    line31.put("fieldValue", hlsCusConContractCashflow1.getTimes());
                    line31.put("orderSeq", 1);
                    line31.put("isEdit", false);
                    line31.put("isShow", true);
                    line31.put("nullableFlag", true);
//
                    JSONObject line32 = new JSONObject(new LinkedHashMap());
                    line32.put("fieldDescription", "现金流项目");
                    line32.put("fieldName", "cfItemN");
                    line32.put("fieldType", "Text");
                    line32.put("fieldValue", hlsCusConContractCashflow1.getCfItemN());
                    line32.put("orderSeq", 2);
                    line32.put("isEdit", false);
                    line32.put("isShow", true);
                    line32.put("nullableFlag", true);

                    JSONObject line33 = new JSONObject(new LinkedHashMap());
                    line33.put("fieldDescription", "计算日");
                    line33.put("fieldName", "calcDate");
                    line33.put("fieldType", "Text");
                    String calcDate=sdf.format(hlsCusConContractCashflow1.getCalcDate());
                    line33.put("fieldValue", calcDate);
                    line33.put("orderSeq", 3);
                    line33.put("isEdit", false);
                    line33.put("isShow", true);
                    line33.put("nullableFlag", true);

                    JSONObject line34 = new JSONObject(new LinkedHashMap());
                    line34.put("fieldDescription", "应收金额");
                    line34.put("fieldName", "dueAmount");
                    line34.put("fieldType", "Text");
                    line34.put("fieldValue", hlsCusConContractCashflow1.getDueAmount());
                    line34.put("orderSeq", 4);
                    line34.put("isEdit", false);
                    line34.put("isShow", true);
                    line34.put("nullableFlag", true);

                    JSONObject line35 = new JSONObject(new LinkedHashMap());
                    line35.put("fieldDescription", "应收本金");
                    line35.put("fieldName", "principal");
                    line35.put("fieldType", "Text");
                    line35.put("fieldValue", hlsCusConContractCashflow1.getPrincipal());
                    line35.put("orderSeq", 5);
                    line35.put("isEdit", false);
                    line35.put("isShow", true);
                    line35.put("nullableFlag", true);
//
                    JSONObject line36 = new JSONObject(new LinkedHashMap());
                    line36.put("fieldDescription", "应收利息");
                    line36.put("fieldName", "interest");
                    line36.put("fieldType", "Text");
                    line36.put("fieldValue", hlsCusConContractCashflow1.getInterest());
                    line36.put("orderSeq", 6);
                    line36.put("isEdit", false);
                    line36.put("isShow", true);
                    line36.put("nullableFlag", true);

                    JSONObject line37 = new JSONObject(new LinkedHashMap());
                    line37.put("fieldDescription", "当前剩余本金");
                    line37.put("fieldName", "outstandingPrincipal");
                    line37.put("fieldType", "Text");
                    line37.put("fieldValue", hlsCusConContractCashflow1.getOutstandingPrincipal());
                    line37.put("orderSeq", 7);
                    line37.put("isEdit", false);
                    line37.put("isShow", true);
                    line37.put("nullableFlag", true);
//
                    JSONObject line38 = new JSONObject(new LinkedHashMap());
                    line38.put("fieldDescription", "已收金额");
                    line38.put("fieldName", "receivedAmount");
                    line38.put("fieldType", "Text");
                    line38.put("fieldValue", hlsCusConContractCashflow1.getReceivedAmount());
                    line38.put("orderSeq", 8);
                    line38.put("isEdit", false);
                    line38.put("isShow", true);
                    line38.put("nullableFlag", true);

                    JSONObject line39 = new JSONObject(new LinkedHashMap());
                    line39.put("fieldDescription", "核销状态");
                    line39.put("fieldName", "writeOffFlagN");
                    line39.put("fieldType", "Text");
                    line39.put("fieldValue", hlsCusConContractCashflow1.getWriteOffFlagN());
                    line39.put("orderSeq", 9);
                    line39.put("isEdit", false);
                    line39.put("isShow", true);
                    line39.put("nullableFlag", true);
//
                    lines21.add(line31);
                    lines21.add(line32);
                    lines21.add(line33);
                    lines21.add(line34);
                    lines21.add(line35);
                    lines21.add(line36);
                    lines21.add(line37);
                    lines21.add(line38);
                    lines21.add(line39);
                    lines2.add(lines21);
//
                }
                content3.put("lines",lines2);
                content.add(content3);

                //变更前现金流
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","变更前现金流");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                List<JSONObject> returnJSONList2 = new ArrayList<>();

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList2.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList2.add(data);
                        });
                    }
                }

                for (JSONObject jsonObject : returnJSONList2) {
                    JSONArray lines31 = new JSONArray();

                    JSONObject line41 = new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription", "期数");
                    line41.put("fieldName", "times");
                    line41.put("fieldType", "Text");
                    line41.put("fieldValue", jsonObject.get("times"));
                    line41.put("orderSeq", 1);
                    line41.put("isEdit", false);
                    line41.put("isShow", true);
                    line41.put("nullableFlag", true);

                    JSONObject line42 = new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription", "现金流项目");
                    line42.put("fieldName", "cfItemN");
                    line42.put("fieldType", "Text");
                    line42.put("fieldValue", jsonObject.get("cf_item_n"));
                    line42.put("orderSeq", 2);
                    line42.put("isEdit", false);
                    line42.put("isShow", true);
                    line42.put("nullableFlag", true);
//
                    JSONObject line43 = new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription", "计算日");
                    line43.put("fieldName", "calcDate");
                    line43.put("fieldType", "Text");
//                    String calcDate=sdf.format(jsonObject.get("calc_date"));
                    line43.put("fieldValue",jsonObject.get("calc_date"));
                    line43.put("orderSeq", 3);
                    line43.put("isEdit", false);
                    line43.put("isShow", true);
                    line43.put("nullableFlag", true);
//
                    JSONObject line44 = new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription", "应收金额");
                    line44.put("fieldName", "dueAmount");
                    line44.put("fieldType", "Text");
                    line44.put("fieldValue", jsonObject.get("due_amount"));
                    line44.put("orderSeq", 4);
                    line44.put("isEdit", false);
                    line44.put("isShow", true);
                    line44.put("nullableFlag", true);
//
                    JSONObject line45 = new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription", "应收本金");
                    line45.put("fieldName", "principal");
                    line45.put("fieldType", "Text");
                    line45.put("fieldValue", jsonObject.get("principal"));
                    line45.put("orderSeq", 5);
                    line45.put("isEdit", false);
                    line45.put("isShow", true);
                    line45.put("nullableFlag", true);
//
                    JSONObject line46 = new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription", "应收利息");
                    line46.put("fieldName", "interest");
                    line46.put("fieldType", "Text");
                    line46.put("fieldValue", jsonObject.get("interest"));
                    line46.put("orderSeq", 6);
                    line46.put("isEdit", false);
                    line46.put("isShow", true);
                    line46.put("nullableFlag", true);
//
                    JSONObject line47 = new JSONObject(new LinkedHashMap());
                    line47.put("fieldDescription", "当前剩余本金");
                    line47.put("fieldName", "outstandingPrincipal");
                    line47.put("fieldType", "Text");
                    line47.put("fieldValue", jsonObject.get("outstanding_principal"));
                    line47.put("orderSeq", 7);
                    line47.put("isEdit", false);
                    line47.put("isShow", true);
                    line47.put("nullableFlag", true);
//
                    JSONObject line48 = new JSONObject(new LinkedHashMap());
                    line48.put("fieldDescription", "已收金额");
                    line48.put("fieldName", "receivedAmount");
                    line48.put("fieldType", "Text");
                    line48.put("fieldValue", jsonObject.get("received_amount"));
                    line48.put("orderSeq", 8);
                    line48.put("isEdit", false);
                    line48.put("isShow", true);
                    line48.put("nullableFlag", true);
//
                    JSONObject line49 = new JSONObject(new LinkedHashMap());
                    line49.put("fieldDescription", "核销状态");
                    line49.put("fieldName", "writeOffFlagN");
                    line49.put("fieldType", "Text");
                    line49.put("fieldValue", jsonObject.get("write_off_flag_n"));
                    line49.put("orderSeq", 9);
                    line49.put("isEdit", false);
                    line49.put("isShow", true);
                    line49.put("nullableFlag", true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);
                    lines31.add(line47);
                    lines31.add(line48);
                    lines31.add(line49);
                    lines3.add(lines31);

                }
                content4.put("lines",lines3);
                content.add(content4);

                //退回按钮
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","approve");
                header5.put("orderSeq",5);
                header5.put("hasChildren",false);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();

                JSONObject line51=new JSONObject(new LinkedHashMap());
                line51.put("fieldDescription","退回");
                line51.put("fieldName","sendback");
                line51.put("fieldType","Text");
                line51.put("fieldValue","SENDBACK");
                line51.put("orderSeq",1);
                line51.put("isEdit",false);
                line51.put("isShow",true);
                line51.put("nullableFlag",false);

                lines4.add(line51);
                content5.put("lines",lines4);
                content.add(content5);

            }
            //回购
            else if("REPO".equals(hlsCusConContractChangeReq.getChangeType())){

                IRequest iRequest= RequestHelper.newEmptyRequest();
                List<JSONObject> returnJSONList = new ArrayList<>();
                HlsCusConContractChangeReq changeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(businessKey);

                //基本信息
                JSONObject content1=new JSONObject(new LinkedHashMap());
                JSONObject header1=new JSONObject(new LinkedHashMap());
                header1.put("name","基本信息");
                header1.put("icon","profile");
                header1.put("orderSeq",1);
                header1.put("hasChildren",false);
                header1.put("isShow",true);
                content1.put("header",header1);
                JSONArray lines=new JSONArray();

//                HlsCusConContract hlsCusConContract =new HlsCusConContract();
//                hlsCusConContract.setContractId(hlsCusConContractChangeReq.getContractId());
//                List<HlsCusConContract> hlsCusConContracts=hlsCusConContractMapper.queryConContractDetails((Map) hlsCusConContract);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList.add(data);
                        });
                    }
                }

                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","合同编号");
                line1.put("fieldName","contractNumber");
                line1.put("fieldType","Text");
                line1.put("fieldValue",returnJSONList.get(0).get("contract_number"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","商业模式");
                line6.put("fieldName","leaseChannelN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",returnJSONList.get(0).get("lease_channel_n"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","业务模式");
                line7.put("fieldName","businessTypeN");
                line7.put("fieldType","Text");
                line7.put("fieldValue",returnJSONList.get(0).get("business_type_n"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","承租人名称");
                line2.put("fieldName","bpIdTenantN");
                line2.put("fieldType","Text");
                line2.put("fieldValue",returnJSONList.get(0).get("bp_id_tenant_n"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","合作方");
                line3.put("fieldName","manufacturerIdN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",returnJSONList.get(0).get("manufacturer_id_n"));
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","归属主机厂");
                line4.put("fieldName","factoryIdN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",returnJSONList.get(0).get("factory_id_n"));
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);


                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","经销商");
                line5.put("fieldName","bpIdVenderN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",returnJSONList.get(0).get("bp_id_vender_n"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","起租日期");
                line9.put("fieldName","leaseStartDate");
                line9.put("fieldType","Text");
//                String leaseStartDate = sdf.format(returnJSONList.get(0).get("lease_start_date"));
                line9.put("fieldValue",returnJSONList.get(0).get("lease_start_date"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","融资额");
                line8.put("fieldName","financeAmount");
                line8.put("fieldType","Text");
                line8.put("fieldValue",returnJSONList.get(0).get("finance_amount"));
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line10=new JSONObject(new LinkedHashMap());
                line10.put("fieldDescription","项目经理");
                line10.put("fieldName","employeeIdN");
                line10.put("fieldType","Text");
                line10.put("fieldValue",returnJSONList.get(0).get("employee_id_n"));
                line10.put("orderSeq",10);
                line10.put("isEdit",false);
                line10.put("isShow",true);
                line10.put("nullableFlag",true);



                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);
                lines.add(line10);

                content1.put("lines",lines);
                content.add(content1);

                //变更信息
                JSONObject content2=new JSONObject(new LinkedHashMap());
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","变更信息");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();
                List<JSONObject> returnJSONList1 = new ArrayList<>();

//                HlsCusConContract hlsCusConContract1=new HlsCusConContract();
//                hlsCusConContract1.setChangeReqId(businessKey);
//                Map hlsCusConContract1s=hlsCusConContractMapper.selectPrePaymentChangeReqInfo(hlsCusConContract1);

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList1.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_change_req");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList1.add(data);
                        });
                    }
                }

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","回购期数");
                line21.put("fieldName","times");
                line21.put("fieldType","Text");
                line21.put("fieldValue",returnJSONList1.get(0).get("times"));
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","回购日");
                line22.put("fieldName","dueDate");
                line22.put("fieldType","Text");
                line22.put("fieldValue",returnJSONList1.get(0).get("due_date"));
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","逾期租金");
                line23.put("fieldName","overdueAmount");
                line23.put("fieldType","Text");
                line23.put("fieldValue",returnJSONList1.get(0).get("overdue_amount"));
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","剩余本金");
                line24.put("fieldName","outstandingPrincipal");
                line24.put("fieldType","Text");
                line24.put("fieldValue",returnJSONList1.get(0).get("outstanding_principal"));
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                line25.put("fieldDescription","期间利息");
                line25.put("fieldName","periodInterest");
                line25.put("fieldType","Text");
                line25.put("fieldValue",returnJSONList1.get(0).get("period_interest"));
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                JSONObject line26=new JSONObject(new LinkedHashMap());
                line26.put("fieldDescription","期间利息变更");
                line26.put("fieldName","periodInterestChange");
                line26.put("fieldType","Text");
                line26.put("fieldValue",returnJSONList1.get(0).get("period_interest_change"));
                line26.put("orderSeq",6);
                line26.put("isEdit",false);
                line26.put("isShow",true);
                line26.put("nullableFlag",true);

                JSONObject line27=new JSONObject(new LinkedHashMap());
                line27.put("fieldDescription","保证金是否抵扣");
                line27.put("fieldName","depositDeductFlag");
                line27.put("fieldType","Text");
                line27.put("fieldValue",returnJSONList1.get(0).get("deposit_deduct_flag_n"));
                line27.put("orderSeq",7);
                line27.put("isEdit",false);
                line27.put("isShow",true);
                line27.put("nullableFlag",true);

                JSONObject line28=new JSONObject(new LinkedHashMap());
                line28.put("fieldDescription","保证金");
                line28.put("fieldName","deposit");
                line28.put("fieldType","Text");
                line28.put("fieldValue",returnJSONList1.get(0).get("deposit"));
                line28.put("orderSeq",8);
                line28.put("isEdit",false);
                line28.put("isShow",true);
                line28.put("nullableFlag",true);

                JSONObject line29=new JSONObject(new LinkedHashMap());
                line29.put("fieldDescription","罚息金额");
                line29.put("fieldName","penaltyInterest");
                line29.put("fieldType","Text");
                line29.put("fieldValue",returnJSONList1.get(0).get("penalty_interest"));
                line29.put("orderSeq",9);
                line29.put("isEdit",false);
                line29.put("isShow",true);
                line29.put("nullableFlag",true);

                JSONObject line210=new JSONObject(new LinkedHashMap());
                line210.put("fieldDescription","名义货价");
                line210.put("fieldName","nominalPrice");
                line210.put("fieldType","Text");
                line210.put("fieldValue",returnJSONList1.get(0).get("nominal_price"));
                line210.put("orderSeq",10);
                line210.put("isEdit",false);
                line210.put("isShow",true);
                line210.put("nullableFlag",true);

                JSONObject line211=new JSONObject(new LinkedHashMap());
                line211.put("fieldDescription","提前结清款");
                line211.put("fieldName","preAmount");
                line211.put("fieldType","Text");
                line211.put("fieldValue",returnJSONList1.get(0).get("pre_amount"));
                line211.put("orderSeq",11);
                line211.put("isEdit",false);
                line211.put("isShow",true);
                line211.put("nullableFlag",true);

                JSONObject line212=new JSONObject(new LinkedHashMap());
                line212.put("fieldDescription","厂商返利");
                line212.put("fieldName","replyAmount");
                line212.put("fieldType","Text");
                line212.put("fieldValue",returnJSONList1.get(0).get("reply_amount"));
                line212.put("orderSeq",12);
                line212.put("isEdit",false);
                line212.put("isShow",true);
                line212.put("nullableFlag",true);

                JSONObject line213=new JSONObject(new LinkedHashMap());
                line213.put("fieldDescription","厂商贴息");
                line213.put("fieldName","discountAmount");
                line213.put("fieldType","Text");
                line213.put("fieldValue",returnJSONList1.get(0).get("discount_amount"));
                line213.put("orderSeq",13);
                line213.put("isEdit",false);
                line213.put("isShow",true);
                line213.put("nullableFlag",true);

                JSONObject line214=new JSONObject(new LinkedHashMap());
                line214.put("fieldDescription","IRR");
                line214.put("fieldName","irr");
                line214.put("fieldType","Text");
                line214.put("fieldValue",returnJSONList1.get(0).get("irr"));
                line214.put("orderSeq",14);
                line214.put("isEdit",false);
                line214.put("isShow",true);
                line214.put("nullableFlag",true);

                JSONObject line215=new JSONObject(new LinkedHashMap());
                line215.put("fieldDescription","变更手续费");
                line215.put("fieldName","etFee");
                line215.put("fieldType","Text");
                line215.put("fieldValue",returnJSONList1.get(0).get("et_fee"));
                line215.put("orderSeq",15);
                line215.put("isEdit",false);
                line215.put("isShow",true);
                line215.put("nullableFlag",true);

                JSONObject line216=new JSONObject(new LinkedHashMap());
                line216.put("fieldDescription","变更后的IRR");
                line216.put("fieldName","changeIrr");
                line216.put("fieldType","Text");
                line216.put("fieldValue",returnJSONList1.get(0).get("change_irr"));
                line216.put("orderSeq",2);
                line216.put("isEdit",false);
                line216.put("isShow",true);
                line216.put("nullableFlag",true);

                JSONObject line217=new JSONObject(new LinkedHashMap());
                line217.put("fieldDescription","变更原因");
                line217.put("fieldName","description");
                line217.put("fieldType","Text");
                line217.put("fieldValue",returnJSONList1.get(0).get("description"));
                line217.put("orderSeq",2);
                line217.put("isEdit",false);
                line217.put("isShow",true);
                line217.put("nullableFlag",true);



                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);
                lines1.add(line24);
                lines1.add(line25);
                lines1.add(line26);
                lines1.add(line27);
                lines1.add(line28);
                lines1.add(line29);
                lines1.add(line210);
                lines1.add(line211);
                lines1.add(line212);
                lines1.add(line213);
                lines1.add(line214);
                lines1.add(line215);
                lines1.add(line216);
                lines1.add(line217);

                content2.put("lines",lines1);
                content.add(content2);

                //变更后现金流
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","变更后现金流");
                header4.put("icon","profile");
                header4.put("orderSeq",3);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                List<JSONObject> returnJSONList2 = new ArrayList<>();

                if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow", 1L);
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> returnJSONList2.add(item.getJSONObject("data")));
                    }
                }else{
                    List<JSONObject> jsonObjects1 = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                            businessKey, "CONTRACT_CHANGE", "con_contract_cashflow");
                    if(CollectionUtils.isNotEmpty(jsonObjects1)){
                        jsonObjects1.forEach(item -> {
                            JSONObject data = item.getJSONObject("data");
                            String status = data.getString("_status");
                            returnJSONList2.add(data);
                        });
                    }
                }

                for (JSONObject jsonObject : returnJSONList2) {
                    JSONArray lines31 = new JSONArray();

                    JSONObject line41 = new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription", "期数");
                    line41.put("fieldName", "times");
                    line41.put("fieldType", "Text");
                    line41.put("fieldValue", jsonObject.get("times"));
                    line41.put("orderSeq", 1);
                    line41.put("isEdit", false);
                    line41.put("isShow", true);
                    line41.put("nullableFlag", true);

                    JSONObject line42 = new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription", "现金流项目");
                    line42.put("fieldName", "cfItemN");
                    line42.put("fieldType", "Text");
                    line42.put("fieldValue", jsonObject.get("cf_item_n"));
                    line42.put("orderSeq", 2);
                    line42.put("isEdit", false);
                    line42.put("isShow", true);
                    line42.put("nullableFlag", true);

                    JSONObject line43 = new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription", "计算日");
                    line43.put("fieldName", "calcDate");
                    line43.put("fieldType", "Text");
//                    String calcDate=sdf.format(jsonObject.get("calc_date"));
                    line43.put("fieldValue", jsonObject.get("calc_date"));
                    line43.put("orderSeq", 3);
                    line43.put("isEdit", false);
                    line43.put("isShow", true);
                    line43.put("nullableFlag", true);

                    JSONObject line44 = new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription", "应收金额");
                    line44.put("fieldName", "dueAmount");
                    line44.put("fieldType", "Text");
                    line44.put("fieldValue", jsonObject.get("due_amount"));
                    line44.put("orderSeq", 4);
                    line44.put("isEdit", false);
                    line44.put("isShow", true);
                    line44.put("nullableFlag", true);

                    JSONObject line45 = new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription", "应收本金");
                    line45.put("fieldName", "principal");
                    line45.put("fieldType", "Text");
                    line45.put("fieldValue", jsonObject.get("principal"));
                    line45.put("orderSeq", 5);
                    line45.put("isEdit", false);
                    line45.put("isShow", true);
                    line45.put("nullableFlag", true);

                    JSONObject line46 = new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription", "应收利息");
                    line46.put("fieldName", "interest");
                    line46.put("fieldType", "Text");
                    line46.put("fieldValue", jsonObject.get("interest"));
                    line46.put("orderSeq", 6);
                    line46.put("isEdit", false);
                    line46.put("isShow", true);
                    line46.put("nullableFlag", true);

                    JSONObject line47 = new JSONObject(new LinkedHashMap());
                    line47.put("fieldDescription", "当前剩余本金");
                    line47.put("fieldName", "outstandingPrincipal");
                    line47.put("fieldType", "Text");
                    line47.put("fieldValue", jsonObject.get("outstanding_principal"));
                    line47.put("orderSeq", 7);
                    line47.put("isEdit", false);
                    line47.put("isShow", true);
                    line47.put("nullableFlag", true);

                    JSONObject line48 = new JSONObject(new LinkedHashMap());
                    line48.put("fieldDescription", "已收金额");
                    line48.put("fieldName", "receivedAmount");
                    line48.put("fieldType", "Text");
                    line48.put("fieldValue", jsonObject.get("received_amount"));
                    line48.put("orderSeq", 8);
                    line48.put("isEdit", false);
                    line48.put("isShow", true);
                    line48.put("nullableFlag", true);

                    JSONObject line49 = new JSONObject(new LinkedHashMap());
                    line49.put("fieldDescription", "核销状态");
                    line49.put("fieldName", "writeOffFlagN");
                    line49.put("fieldType", "Text");
                    line49.put("fieldValue", jsonObject.get("write_off_flag_n"));
                    line49.put("orderSeq", 9);
                    line49.put("isEdit", false);
                    line49.put("isShow", true);
                    line49.put("nullableFlag", true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);
                    lines31.add(line47);
                    lines31.add(line48);
                    lines31.add(line49);
                    lines3.add(lines31);

                }
                content4.put("lines",lines3);
                content.add(content4);

                //变更前现金流
                JSONObject content3=new JSONObject(new LinkedHashMap());
                JSONObject header3=new JSONObject(new LinkedHashMap());
                header3.put("name","变更前现金流");
                header3.put("icon","profile");
                header3.put("orderSeq",4);
                header3.put("hasChildren",true);
                header3.put("isShow",true);
                content3.put("header",header3);
                JSONArray lines2=new JSONArray();
                Map<String, Object> map = new HashMap<>();
                map.put("contractId", hlsCusConContractChangeReq.getContractId());
//                HlsCusConContractCashflow hlsCusConContractCashflow=new HlsCusConContractCashflow();
//                hlsCusConContractCashflow.setContractId(hlsCusConContractChangeReq.getContractId());
                List<HlsCusConContractCashflow> hlsCusConContractCashflows=hlsCusConContractCashflowMapper.queryConContractCashflowDetail(map);
                for (HlsCusConContractCashflow hlsCusConContractCashflow1 : hlsCusConContractCashflows) {
                    JSONArray lines21 = new JSONArray();

                    JSONObject line31 = new JSONObject(new LinkedHashMap());
                    line31.put("fieldDescription", "期数");
                    line31.put("fieldName", "times");
                    line31.put("fieldType", "Text");
                    line31.put("fieldValue", hlsCusConContractCashflow1.getTimes());
                    line31.put("orderSeq", 1);
                    line31.put("isEdit", false);
                    line31.put("isShow", true);
                    line31.put("nullableFlag", true);

                    JSONObject line32 = new JSONObject(new LinkedHashMap());
                    line32.put("fieldDescription", "现金流项目");
                    line32.put("fieldName", "cfItemN");
                    line32.put("fieldType", "Text");
                    line32.put("fieldValue", hlsCusConContractCashflow1.getCfItemN());
                    line32.put("orderSeq", 2);
                    line32.put("isEdit", false);
                    line32.put("isShow", true);
                    line32.put("nullableFlag", true);

                    JSONObject line33 = new JSONObject(new LinkedHashMap());
                    line33.put("fieldDescription", "计算日");
                    line33.put("fieldName", "calcDate");
                    line33.put("fieldType", "Text");
                    String calcDate=sdf.format(hlsCusConContractCashflow1.getCalcDate());
                    line33.put("fieldValue", calcDate);
                    line33.put("orderSeq", 3);
                    line33.put("isEdit", false);
                    line33.put("isShow", true);
                    line33.put("nullableFlag", true);

                    JSONObject line34 = new JSONObject(new LinkedHashMap());
                    line34.put("fieldDescription", "应收金额");
                    line34.put("fieldName", "dueAmount");
                    line34.put("fieldType", "Text");
                    line34.put("fieldValue", hlsCusConContractCashflow1.getDueAmount());
                    line34.put("orderSeq", 4);
                    line34.put("isEdit", false);
                    line34.put("isShow", true);
                    line34.put("nullableFlag", true);

                    JSONObject line35 = new JSONObject(new LinkedHashMap());
                    line35.put("fieldDescription", "应收本金");
                    line35.put("fieldName", "principal");
                    line35.put("fieldType", "Text");
                    line35.put("fieldValue", hlsCusConContractCashflow1.getPrincipal());
                    line35.put("orderSeq", 5);
                    line35.put("isEdit", false);
                    line35.put("isShow", true);
                    line35.put("nullableFlag", true);

                    JSONObject line36 = new JSONObject(new LinkedHashMap());
                    line36.put("fieldDescription", "应收利息");
                    line36.put("fieldName", "interest");
                    line36.put("fieldType", "Text");
                    line36.put("fieldValue", hlsCusConContractCashflow1.getInterest());
                    line36.put("orderSeq", 6);
                    line36.put("isEdit", false);
                    line36.put("isShow", true);
                    line36.put("nullableFlag", true);

                    JSONObject line37 = new JSONObject(new LinkedHashMap());
                    line37.put("fieldDescription", "当前剩余本金");
                    line37.put("fieldName", "outstandingPrincipal");
                    line37.put("fieldType", "Text");
                    line37.put("fieldValue", hlsCusConContractCashflow1.getOutstandingPrincipal());
                    line37.put("orderSeq", 7);
                    line37.put("isEdit", false);
                    line37.put("isShow", true);
                    line37.put("nullableFlag", true);

                    JSONObject line38 = new JSONObject(new LinkedHashMap());
                    line38.put("fieldDescription", "已收金额");
                    line38.put("fieldName", "receivedAmount");
                    line38.put("fieldType", "Text");
                    line38.put("fieldValue", hlsCusConContractCashflow1.getReceivedAmount());
                    line38.put("orderSeq", 8);
                    line38.put("isEdit", false);
                    line38.put("isShow", true);
                    line38.put("nullableFlag", true);

                    JSONObject line39 = new JSONObject(new LinkedHashMap());
                    line39.put("fieldDescription", "核销状态");
                    line39.put("fieldName", "writeOffFlagN");
                    line39.put("fieldType", "Text");
                    line39.put("fieldValue", hlsCusConContractCashflow1.getWriteOffFlagN());
                    line39.put("orderSeq", 9);
                    line39.put("isEdit", false);
                    line39.put("isShow", true);
                    line39.put("nullableFlag", true);

                    lines21.add(line31);
                    lines21.add(line32);
                    lines21.add(line33);
                    lines21.add(line34);
                    lines21.add(line35);
                    lines21.add(line36);
                    lines21.add(line37);
                    lines21.add(line38);
                    lines21.add(line39);
                    lines2.add(lines21);

                }
                content3.put("lines",lines2);
                content.add(content3);




                //退回按钮
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","approve");
                header5.put("orderSeq",5);
                header5.put("hasChildren",false);
                content5.put("header",header5);
                JSONArray lines4=new JSONArray();

                JSONObject line51=new JSONObject(new LinkedHashMap());
                line51.put("fieldDescription","退回");
                line51.put("fieldName","sendback");
                line51.put("fieldType","Text");
                line51.put("fieldValue","SENDBACK");
                line51.put("orderSeq",1);
                line51.put("isEdit",false);
                line51.put("isShow",true);
                line51.put("nullableFlag",false);

                lines4.add(line51);
                content5.put("lines",lines4);
                content.add(content5);

//                JSONArray jsonArray = connContractChange2(hlsCusConContractChangeReq.getContractId(),businessKey,"CON_CONTRACT_PREPAYMENT_CHANGE",4);
//                content.add(jsonArray.get(0));
//                content.add(jsonArray.get(1));
            }

            return content;
        }catch (Exception e){
            logger.error("----------零售业务合同变更流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }
    //付款申请流程
    private JSONArray paymentApplication(){
        try {
            JSONArray content=new JSONArray();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);

            JSONArray lines=new JSONArray();
            HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd =new HlsCusCshPaymentReqHd();
            hlsCusCshPaymentReqHd.setPaymentReqId(businessKey);
            List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds=hlsCusCshPaymentReqHdMapper.queryCshPaymentReqHd(hlsCusCshPaymentReqHd);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","申请编号");
            line1.put("fieldName","paymentReqNumber");
            line1.put("fieldType","Text");
            line1.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getPaymentReqNumber());
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","申请日期");
            line2.put("fieldName","paymentReqDate");
            line2.put("fieldType","Text");
            String paymentReqDate = sdf.format(hlsCusCshPaymentReqHds.get(0).getPaymentReqDate());
            line2.put("fieldValue",paymentReqDate);
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","抵扣前申请总金额");
            line3.put("fieldName","amount");
            line3.put("fieldType","Text");
            line3.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getAmount());
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","抵扣后申请总金额");
            line4.put("fieldName","afterDeductAmount");
            line4.put("fieldType","Text");
            line4.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getAfterDeductAmount());
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","付款对象");
            line5.put("fieldName","paymentBpName");
            line5.put("fieldType","Text");
            line5.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getPaymentBpName());
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","付款对象账户");
            line6.put("fieldName","bpBankAccountName");
            line6.put("fieldType","Text");
            line6.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getBpBankAccountName());
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","付款对象账号");
            line7.put("fieldName","bpBankAccountNum");
            line7.put("fieldType","Text");
            line7.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getBpBankAccountNum());
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            JSONObject line8=new JSONObject(new LinkedHashMap());
            line8.put("fieldDescription","付款账户银行");
            line8.put("fieldName","bpBankName");
            line8.put("fieldType","Text");
            line8.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getBpBankName());
            line8.put("orderSeq",8);
            line8.put("isEdit",false);
            line8.put("isShow",true);
            line8.put("nullableFlag",true);

            JSONObject line9=new JSONObject(new LinkedHashMap());
            line9.put("fieldDescription","付款方式");
            line9.put("fieldName","paymentMethodN");
            line9.put("fieldType","Text");
            line9.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getPaymentMethodN());
            line9.put("orderSeq",9);
            line9.put("isEdit",false);
            line9.put("isShow",true);
            line9.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);
            lines.add(line8);
            lines.add(line9);

            content1.put("lines",lines);
            content.add(content1);

            //备注
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","备注");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","备注");
            line21.put("fieldName","description");
            line21.put("fieldType","Text");
            line21.put("fieldValue",hlsCusCshPaymentReqHds.get(0).getDescription());
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            lines1.add(line21);

            content2.put("lines",lines1);
            content.add(content2);

            //付款信息
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","付款信息");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",true);
            header3.put("isShow",true);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn=new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setPaymentReqId(businessKey);
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns=hlsCusCshPaymentReqLnMapper.queryCshPaymentReqLn2(hlsCusCshPaymentReqLn);
            for (HlsCusCshPaymentReqLn cshPaymentReqLn : hlsCusCshPaymentReqLns) {
                JSONArray lines21=new JSONArray();

                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","合同编号");
                line31.put("fieldName","contractNumber");
                line31.put("fieldType","Text");
                line31.put("fieldValue",cshPaymentReqLn.getContractNumber());
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","承租人");
                line32.put("fieldName","bpName");
                line32.put("fieldType","Text");
                line32.put("fieldValue",cshPaymentReqLn.getBpName());
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","应付项目");
                line33.put("fieldName","cfItemN");
                line33.put("fieldType","Text");
                line33.put("fieldValue",cshPaymentReqLn.getCfItemN());
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                JSONObject line34=new JSONObject(new LinkedHashMap());
                line34.put("fieldDescription","期数");
                line34.put("fieldName","times");
                line34.put("fieldType","Text");
                line34.put("fieldValue",cshPaymentReqLn.getTimes());
                line34.put("orderSeq",4);
                line34.put("isEdit",false);
                line34.put("isShow",true);
                line34.put("nullableFlag",true);

                JSONObject line35=new JSONObject(new LinkedHashMap());
                line35.put("fieldDescription","应付金额");
                line35.put("fieldName","dueAmount");
                line35.put("fieldType","Text");
                line35.put("fieldValue",cshPaymentReqLn.getDueAmount());
                line35.put("orderSeq",5);
                line35.put("isEdit",false);
                line35.put("isShow",true);
                line35.put("nullableFlag",true);

                JSONObject line36=new JSONObject(new LinkedHashMap());
                line36.put("fieldDescription","本次申请金额");
                line36.put("fieldName","amount");
                line36.put("fieldType","Text");
                line36.put("fieldValue",cshPaymentReqLn.getAmount());
                line36.put("orderSeq",6);
                line36.put("isEdit",false);
                line36.put("isShow",true);
                line36.put("nullableFlag",true);

                JSONObject line37=new JSONObject(new LinkedHashMap());
                line37.put("fieldDescription","收付抵扣");
                line37.put("fieldName","dueAmount");
                line37.put("fieldType","Text");
                line37.put("fieldValue","收付抵扣");
                line37.put("orderSeq",7);
                line37.put("isEdit",false);
                line37.put("isShow",true);
                line37.put("nullableFlag",true);

                JSONObject line38=new JSONObject(new LinkedHashMap());
                line38.put("fieldDescription","抵扣总额");
                line38.put("fieldName","deductionAmount");
                line38.put("fieldType","Text");
                line38.put("fieldValue",cshPaymentReqLn.getDeductionAmount());
                line38.put("orderSeq",8);
                line38.put("isEdit",false);
                line38.put("isShow",true);
                line38.put("nullableFlag",true);

                JSONObject line39=new JSONObject(new LinkedHashMap());
                line39.put("fieldDescription","抵扣后申请金额");
                line39.put("fieldName","appliedPayAmount");
                line39.put("fieldType","Text");
                line39.put("fieldValue",cshPaymentReqLn.getAppliedPayAmount());
                line39.put("orderSeq",9);
                line39.put("isEdit",false);
                line39.put("isShow",true);
                line39.put("nullableFlag",true);

                JSONObject line310=new JSONObject(new LinkedHashMap());
                line310.put("fieldDescription","保证金金额");
                line310.put("fieldName","depositDueAmount");
                line310.put("fieldType","Text");
                line310.put("fieldValue",cshPaymentReqLn.getDepositDueAmount());
                line310.put("orderSeq",10);
                line310.put("isEdit",false);
                line310.put("isShow",true);
                line310.put("nullableFlag",true);

                JSONObject line311=new JSONObject(new LinkedHashMap());
                line311.put("fieldDescription","已收保证金金额");
                line311.put("fieldName","depositReceivedAmount");
                line311.put("fieldType","Text");
                line311.put("fieldValue",cshPaymentReqLn.getDepositReceivedAmount());
                line311.put("orderSeq",11);
                line311.put("isEdit",false);
                line311.put("isShow",true);
                line311.put("nullableFlag",true);

                JSONObject line312=new JSONObject(new LinkedHashMap());
                line312.put("fieldDescription","服务费金额");
                line312.put("fieldName","serviceChargeDueAmount");
                line312.put("fieldType","Text");
                line312.put("fieldValue",cshPaymentReqLn.getServiceChargeDueAmount());
                line312.put("orderSeq",12);
                line312.put("isEdit",false);
                line312.put("isShow",true);
                line312.put("nullableFlag",true);

                JSONObject line313=new JSONObject(new LinkedHashMap());
                line313.put("fieldDescription","已收服务费金额");
                line313.put("fieldName","serviceChargeReceivedAmount");
                line313.put("fieldType","Text");
                line313.put("fieldValue",cshPaymentReqLn.getServiceChargeReceivedAmount());
                line313.put("orderSeq",13);
                line313.put("isEdit",false);
                line313.put("isShow",true);
                line313.put("nullableFlag",true);

                JSONObject line314=new JSONObject(new LinkedHashMap());
                line314.put("fieldDescription","服务费(贴息)");
                line314.put("fieldName","serviceChargeFeeDueAmount");
                line314.put("fieldType","Text");
                line314.put("fieldValue",cshPaymentReqLn.getServiceChargeFeeDueAmount());
                line314.put("orderSeq",14);
                line314.put("isEdit",false);
                line314.put("isShow",true);
                line314.put("nullableFlag",true);

                JSONObject line315=new JSONObject(new LinkedHashMap());
                line315.put("fieldDescription","已收服务费(贴息)");
                line315.put("fieldName","serviceChargeFeeReceivedAmount");
                line315.put("fieldType","Text");
                line315.put("fieldValue",cshPaymentReqLn.getServiceChargeFeeReceivedAmount());
                line315.put("orderSeq",15);
                line315.put("isEdit",false);
                line315.put("isShow",true);
                line315.put("nullableFlag",true);

                JSONObject line316=new JSONObject(new LinkedHashMap());
                line316.put("fieldDescription","付款方式");
                line316.put("fieldName","paymentMethodN");
                line316.put("fieldType","Text");
                line316.put("fieldValue",cshPaymentReqLn.getPaymentMethodN());
                line316.put("orderSeq",16);
                line316.put("isEdit",false);
                line316.put("isShow",true);
                line316.put("nullableFlag",true);

                lines21.add(line31);
                lines21.add(line32);
                lines21.add(line33);
                lines21.add(line34);
                lines21.add(line35);
                lines21.add(line36);
                lines21.add(line37);
                lines21.add(line38);
                lines21.add(line39);
                lines21.add(line310);
                lines21.add(line311);
                lines21.add(line312);
                lines21.add(line313);
                lines21.add(line314);
                lines21.add(line315);
                lines21.add(line316);

                lines2.add(lines21);
            }
            content3.put("lines",lines2);
            content.add(content3);


            CshPaymentAttachment condition = new CshPaymentAttachment();
            condition.setPaymentReqId(businessKey);
            condition.setProjectAttachmentCategory("RETAIL_PAYMENT");
            List<CshPaymentAttachment> cshPaymentAttachments = cshPaymentAttachmentMapper.queryPaymentAttachmentMulti(condition);
            if(CollectionUtils.isNotEmpty(cshPaymentAttachments)){
                //附件信息
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","附件信息");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                JSONArray lines3=new JSONArray();
                // 附件信息
                for(CshPaymentAttachment cshPaymentAttachment:cshPaymentAttachments){
                    JSONArray lines33=new JSONArray();
                    JSONObject line317=new JSONObject(new LinkedHashMap());
                    line317.put("fieldDescription","文件名称");
                    line317.put("fieldName","documentName");
                    line317.put("fieldType","Text");
                    line317.put("fieldValue",cshPaymentAttachment.getDocumentName());
                    line317.put("orderSeq",1);
                    line317.put("isEdit",false);
                    line317.put("isShow",true);
                    line317.put("nullableFlag",true);
                    lines33.add(line317);

                    JSONObject line318=new JSONObject(new LinkedHashMap());
                    line318.put("fieldDescription","附件名称");
                    line318.put("fieldName","fileName");
                    line318.put("fieldType","Text");
                    line318.put("fieldValue",cshPaymentAttachment.getFileName());
                    line318.put("orderSeq",2);
                    line318.put("isEdit",false);
                    line318.put("isShow",true);
                    line318.put("nullableFlag",true);
                    lines33.add(line318);

                    JSONObject line319=new JSONObject(new LinkedHashMap());
                    line319.put("fieldDescription","附件");
                    line319.put("fieldName","fileDownload");
                    line319.put("fieldType","Link");
                    line319.put("fieldValue",systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,cshPaymentAttachment.getAttachmentId()));
                    line319.put("orderSeq",3);
                    line319.put("isEdit",false);
                    line319.put("isShow",true);
                    line319.put("nullableFlag",true);
                    lines33.add(line319);
                    lines3.add(lines33);
                }
                if(CollectionUtils.isNotEmpty(lines3)){
                    content4.put("lines",lines3);
                    content.add(content4);
                }
            }

            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","approve");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines4=new JSONArray();

            JSONObject line41=new JSONObject(new LinkedHashMap());
            line41.put("fieldDescription","退回");
            line41.put("fieldName","sendback");
            line41.put("fieldType","Text");
            line41.put("fieldValue","SENDBACK");
            line41.put("orderSeq",1);
            line41.put("isEdit",false);
            line41.put("isShow",true);
            line41.put("nullableFlag",false);

            lines4.add(line41);
            content5.put("lines",lines4);
            content.add(content5);
            return content;
        }
        catch (Exception e){
            logger.error("----------付款申请流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //授信变更流程
    private JSONArray creditChangeProcess(){
        try {
            JSONArray content=new JSONArray();

            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusHlsCreditLineChance hlsCusHlsCreditLine=new HlsCusHlsCreditLineChance();
            hlsCusHlsCreditLine.setChanceId(businessKey);
            HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = hlsCusHlsCreditLineChanceMapper.selectCreditLineChanceById(hlsCusHlsCreditLine);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","项目名称");
            line1.put("fieldName","creditLineName");
            line1.put("fieldType","Text");
            line1.put("fieldValue",hlsCusHlsCreditLineChance.getCreditLineName());
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","项目编号");
            line2.put("fieldName","creditLineNumber");
            line2.put("fieldType","Text");
            line2.put("fieldValue",hlsCusHlsCreditLineChance.getCreditLineNumber());
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","资金用途");
            line3.put("fieldName","financeNote");
            line3.put("fieldType","Text");
            line3.put("fieldValue",hlsCusHlsCreditLineChance.getFinanceNote());
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","增信措施");
            line4.put("fieldName","creditMeasures");
            line4.put("fieldType","Text");
            line4.put("fieldValue",hlsCusHlsCreditLineChance.getCreditMeasures());
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","申请日期");
            line5.put("fieldName","touchCreatedDate");
            line5.put("fieldType","Text");
            line5.put("fieldValue",hlsCusHlsCreditLineChance.getTouchCreatedDate());
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);

            content1.put("lines",lines);

            //授信方案
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","授信方案");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();
            HlsCreditPlan hlsCreditPlan=new HlsCreditPlan();
            hlsCreditPlan.setChanceId(businessKey);
            List<HlsCreditPlan> hlsCreditPlans = hlsCreditPlanMapper.queryCreditPlanInfo(hlsCreditPlan);

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","授信金额");
            line21.put("fieldName","creditAmt");
            line21.put("fieldType","Text");
            line21.put("fieldValue",hlsCreditPlans.get(0).getCreditAmt());
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","额度类型");
            line22.put("fieldName","quotaTypeN");
            line22.put("fieldType","Text");
            line22.put("fieldValue",hlsCreditPlans.get(0).getQuotaTypeN());
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","是否集团授信");
            line23.put("fieldName","conglomerateFlagN");
            line23.put("fieldType","Text");
            line23.put("fieldValue",hlsCreditPlans.get(0).getConglomerateFlagN());
            line23.put("orderSeq",3);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            JSONObject line24=new JSONObject(new LinkedHashMap());
            line24.put("fieldDescription","所属集团");
            line24.put("fieldName","belongConglomerate");
            line24.put("fieldType","Text");
            line24.put("fieldValue",hlsCreditPlans.get(0).getBelongConglomerate());
            line24.put("orderSeq",4);
            line24.put("isEdit",false);
            line24.put("isShow",true);
            line24.put("nullableFlag",true);

            JSONObject line25=new JSONObject(new LinkedHashMap());
            line25.put("fieldDescription","授信开始日期");
            line25.put("fieldName","dateFrom");
            line25.put("fieldType","Text");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String dateForm = sdf.format(hlsCreditPlans.get(0).getDateFrom());
            line25.put("fieldValue",dateForm);
            line25.put("orderSeq",5);
            line25.put("isEdit",false);
            line25.put("isShow",true);
            line25.put("nullableFlag",true);

            JSONObject line26=new JSONObject(new LinkedHashMap());
            line26.put("fieldDescription","授信结束日期");
            line26.put("fieldName","dateTo");
            line26.put("fieldType","Text");
            String dateTo = sdf.format(hlsCreditPlans.get(0).getDateTo());
            line26.put("fieldValue",dateTo);
            line26.put("orderSeq",6);
            line26.put("isEdit",false);
            line26.put("isShow",true);
            line26.put("nullableFlag",true);

            JSONObject line27=new JSONObject(new LinkedHashMap());
            line27.put("fieldDescription","剩余授信额度");
            line27.put("fieldName","leftCreditAmt");
            line27.put("fieldType","Text");
            line27.put("fieldValue",hlsCreditPlans.get(0).getLeftCreditAmt());
            line27.put("orderSeq",7);
            line27.put("isEdit",false);
            line27.put("isShow",true);
            line27.put("nullableFlag",true);

            JSONObject line28=new JSONObject(new LinkedHashMap());
            line28.put("fieldDescription","IRR不低于(%)");
            line28.put("fieldName","irr");
            line28.put("fieldType","Text");
            line28.put("fieldValue",hlsCreditPlans.get(0).getIrr()==null?0:new BigDecimal(String.valueOf(hlsCreditPlans.get(0).getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
            line28.put("orderSeq",8);
            line28.put("isEdit",false);
            line28.put("isShow",true);
            line28.put("nullableFlag",true);

            JSONObject line29=new JSONObject(new LinkedHashMap());
            line29.put("fieldDescription","融资期限不超过(年)");
            line29.put("fieldName","creditDesc");
            line29.put("fieldType","Text");
            line29.put("fieldValue",hlsCreditPlans.get(0).getFinancingDate()==null?0:hlsCreditPlans.get(0).getFinancingDate());
            line29.put("orderSeq",9);
            line29.put("isEdit",false);
            line29.put("isShow",true);
            line29.put("nullableFlag",true);

            JSONObject line210=new JSONObject(new LinkedHashMap());
            line210.put("fieldDescription","授信说明");
            line210.put("fieldName","creditDesc");
            line210.put("fieldType","Text");
            line210.put("fieldValue",hlsCreditPlans.get(0).getCreditDesc());
            line210.put("orderSeq",10);
            line210.put("isEdit",false);
            line210.put("isShow",true);
            line210.put("nullableFlag",true);

            JSONObject line211=new JSONObject(new LinkedHashMap());
            line211.put("fieldDescription","变更说明");
            line211.put("fieldName","changeDesc");
            line211.put("fieldType","Text");
            line211.put("fieldValue",hlsCreditPlans.get(0).getChangeDesc());
            line211.put("orderSeq",11);
            line211.put("isEdit",false);
            line211.put("isShow",true);
            line211.put("nullableFlag",true);

            lines1.add(line21);
            lines1.add(line22);
            lines1.add(line23);
            lines1.add(line24);
            lines1.add(line25);
            lines1.add(line26);
            lines1.add(line27);
            lines1.add(line28);
            lines1.add(line29);
            lines1.add(line210);
            lines1.add(line211);

            content2.put("lines",lines1);

            //授信额度占用
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","授信额度占用");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",true);
            header3.put("isShow",true);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();
            HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp=new HlsCusHlsCreditLineChanceBp();
            hlsCusHlsCreditLineChanceBp.setChanceId(businessKey);
            List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps = hlsCusHlsCreditLineChanceBpMapper.selectByForeignKey(hlsCusHlsCreditLineChanceBp);

            for (HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp : hlsCusHlsCreditLineChanceBps) {
                JSONArray lines21=new JSONArray();

                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","客户名称");
                line31.put("fieldName","bpIdN");
                line31.put("fieldType","Text");
                line31.put("fieldValue",cusHlsCreditLineChanceBp.getBpIdN());
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","客户编号");
                line32.put("fieldName","bpCode");
                line32.put("fieldType","Text");
                line32.put("fieldValue",cusHlsCreditLineChanceBp.getBpCode());
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","客户类型");
                line33.put("fieldName","bpTypeN");
                line33.put("fieldType","Text");
                line33.put("fieldValue",cusHlsCreditLineChanceBp.getBpTypeN());
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                JSONObject line34=new JSONObject(new LinkedHashMap());
                line34.put("fieldDescription","所属集团");
                line34.put("fieldName","groupCompanies");
                line34.put("fieldType","Text");
                line34.put("fieldValue",cusHlsCreditLineChanceBp.getGroupCompanies());
                line34.put("orderSeq",4);
                line34.put("isEdit",false);
                line34.put("isShow",true);
                line34.put("nullableFlag",true);

                JSONObject line35=new JSONObject(new LinkedHashMap());
                line35.put("fieldDescription","授信额度");
                line35.put("fieldName","creditAmount");
                line35.put("fieldType","Text");
                line35.put("fieldValue",cusHlsCreditLineChanceBp.getCreditAmount());
                line35.put("orderSeq",5);
                line35.put("isEdit",false);
                line35.put("isShow",true);
                line35.put("nullableFlag",true);

                lines21.add(line31);
                lines21.add(line32);
                lines21.add(line33);
                lines21.add(line34);
                lines21.add(line35);

                lines2.add(lines21);
            }
            if(hlsCusHlsCreditLineChanceBps.size()<=0){
                lines2.add(new JSONArray());
            }
            content3.put("lines",lines2);

            //变更前信息
            JSONObject content4=new JSONObject(new LinkedHashMap());
            JSONObject header4=new JSONObject(new LinkedHashMap());
            header4.put("name","变更前信息");
            header4.put("icon","profile");
            header4.put("orderSeq",4);
            header4.put("hasChildren",false);
            header4.put("isShow",true);
            content4.put("header",header4);
            JSONArray lines3=new JSONArray();

            JSONObject line41=new JSONObject(new LinkedHashMap());
            line41.put("fieldDescription","变更前的授信金额");
            line41.put("fieldName","changeBeforeCreditAmt");
            line41.put("fieldType","Text");
            line41.put("fieldValue",hlsCreditPlans.get(0).getChangeBeforeCreditAmt());
            line41.put("orderSeq",1);
            line41.put("isEdit",false);
            line41.put("isShow",true);
            line41.put("nullableFlag",true);

            JSONObject line42=new JSONObject(new LinkedHashMap());
            line42.put("fieldDescription","授信结束日");
            line42.put("fieldName","dateToBefore");
            line42.put("fieldType","Text");
            String dateToBefore = sdf.format(hlsCreditPlans.get(0).getDateToBefore());
            line42.put("fieldValue",dateToBefore);
            line42.put("orderSeq",2);
            line42.put("isEdit",false);
            line42.put("isShow",true);
            line42.put("nullableFlag",true);

            lines3.add(line41);
            lines3.add(line42);

            content4.put("lines",lines3);

            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","name");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines4=new JSONArray();

            JSONObject line51=new JSONObject(new LinkedHashMap());
            line51.put("fieldDescription","退回");
            line51.put("fieldName","sendback");
            line51.put("fieldType","Text");
            line51.put("fieldValue","SENDBACK");
            line51.put("orderSeq",1);
            line51.put("isEdit",false);
            line51.put("isShow",true);
            line51.put("nullableFlag",true);

            lines4.add(line51);
            content5.put("lines",lines4);

            content.add(content1);
            content.add(content2);
            content.add(content3);
            content.add(content4);
            content.add(content5);

            return content;
        }catch (Exception e){
            logger.error("----------罚息减免流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //保证金处理方式变更审批流程
    private JSONArray depositManageHdProcess(){
        try {
            JSONArray content=new JSONArray();

            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            DepositManageHd depositManageHd=new DepositManageHd();
            depositManageHd.setManageHdId(businessKey);
            List<DepositManageHd> depositManageHds = depositManageHdMapper.selectDepositManageByField(depositManageHd);
            DepositManageHd depositManageHd1=depositManageHds.get(0);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","合同名称");
            line1.put("fieldName","contractName");
            line1.put("fieldType","Text");
            line1.put("fieldValue",depositManageHd1.getContractName());
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","支付表编号");
            line2.put("fieldName","contractNumber");
            line2.put("fieldType","Text");
            line2.put("fieldValue",depositManageHd1.getContractNumber());
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","客户名称");
            line3.put("fieldName","bpName");
            line3.put("fieldType","Text");
            line3.put("fieldValue",depositManageHd1.getBpName());
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","申请时间");
            line4.put("fieldName","createDate");
            line4.put("fieldType","Text");
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
            String dueDate = sdf.format(depositManageHd1.getCreateDate());
            line4.put("fieldValue",dueDate);
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","申请人");
            line5.put("fieldName","applicant");
            line5.put("fieldType","Text");
            line5.put("fieldValue",depositManageHd1.getApplicant());
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","现金流核销状态");
            line6.put("fieldName","verificationState");
            line6.put("fieldType","Text");
            line6.put("fieldValue",depositManageHd1.getVerificationState());
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","方案编号");
            line7.put("fieldName","manageNumber");
            line7.put("fieldType","Text");
            line7.put("fieldValue",depositManageHd1.getManageNumber());
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);

            content1.put("lines",lines);

            List<DepositManageHd> depositManageHds1 = depositManageHdMapper.conRentCashQueryNormal(depositManageHds.get(0));
            List<DepositManageHd> depositManageHds2 = depositManageHdMapper.conRentCashQueryChange(depositManageHds.get(0));

            //处理方式变更
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","处理方式变更");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","变更前处理方式");
            line21.put("fieldName","depositDeductionN");
            line21.put("fieldType","Text");
            line21.put("fieldValue",depositManageHd1.getDepositDeductionN());
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","变更后处理方式");
            line22.put("fieldName","afterChangeWayN");
            line22.put("fieldType","Text");
            line22.put("fieldValue",depositManageHd1.getAfterChangeWayN());
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            lines1.add(line21);
            lines1.add(line22);

            content2.put("lines",lines1);

            //变更前合同现金流信息
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","变更前合同现金流信息");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",true);
            header3.put("isShow",true);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();

            for (DepositManageHd manageHd : depositManageHds1) {
                if(manageHd!=null){
                    JSONArray lines21=new JSONArray();

                    JSONObject line31=new JSONObject(new LinkedHashMap());
                    line31.put("fieldDescription","期数");
                    line31.put("fieldName","times");
                    line31.put("fieldType","Text");
                    line31.put("fieldValue",manageHd.getTimes());
                    line31.put("orderSeq",1);
                    line31.put("isEdit",false);
                    line31.put("isShow",true);
                    line31.put("nullableFlag",true);

                    JSONObject line32=new JSONObject(new LinkedHashMap());
                    line32.put("fieldDescription","租金");
                    line32.put("fieldName","dueAmount");
                    line32.put("fieldType","Text");
                    line32.put("fieldValue",manageHd.getDueAmount());
                    line32.put("orderSeq",2);
                    line32.put("isEdit",false);
                    line32.put("isShow",true);
                    line32.put("nullableFlag",true);

                    JSONObject line33=new JSONObject(new LinkedHashMap());
                    line33.put("fieldDescription","本金");
                    line33.put("fieldName","principal");
                    line33.put("fieldType","Text");
                    line33.put("fieldValue",manageHd.getPrincipal());
                    line33.put("orderSeq",3);
                    line33.put("isEdit",false);
                    line33.put("isShow",true);
                    line33.put("nullableFlag",true);

                    JSONObject line34=new JSONObject(new LinkedHashMap());
                    line34.put("fieldDescription","利息");
                    line34.put("fieldName","interest");
                    line34.put("fieldType","Text");
                    line34.put("fieldValue",manageHd.getInterest());
                    line34.put("orderSeq",4);
                    line34.put("isEdit",false);
                    line34.put("isShow",true);
                    line34.put("nullableFlag",true);

                    JSONObject line35=new JSONObject(new LinkedHashMap());
                    line35.put("fieldDescription","现金流项目类型");
                    line35.put("fieldName","cfItemN");
                    line35.put("fieldType","Text");
                    line35.put("fieldValue",manageHd.getCfItemN());
                    line35.put("orderSeq",5);
                    line35.put("isEdit",false);
                    line35.put("isShow",true);
                    line35.put("nullableFlag",true);

                    JSONObject line36=new JSONObject(new LinkedHashMap());
                    line36.put("fieldDescription","退还抵扣方式");
                    line36.put("fieldName","depositDeductionN");
                    line36.put("fieldType","Text");
                    line36.put("fieldValue",manageHd.getDepositDeductionN()==null?"":manageHd.getDepositDeductionN());
                    line36.put("orderSeq",6);
                    line36.put("isEdit",false);
                    line36.put("isShow",true);
                    line36.put("nullableFlag",true);

                    JSONObject line37=new JSONObject(new LinkedHashMap());
                    line37.put("fieldDescription","支付日期");
                    line37.put("fieldName","dueDate");
                    line37.put("fieldType","Text");
                    String dueDate1 = sdf.format(manageHd.getDueDate());
                    line37.put("fieldValue",dueDate1);
                    line37.put("orderSeq",7);
                    line37.put("isEdit",false);
                    line37.put("isShow",true);
                    line37.put("nullableFlag",true);

                    lines21.add(line31);
                    lines21.add(line32);
                    lines21.add(line33);
                    lines21.add(line34);
                    lines21.add(line35);
                    lines21.add(line36);
                    lines21.add(line37);

                    lines2.add(lines21);
                }
            }
            if(depositManageHds1.size()<=0){
                lines2.add(new JSONArray());
            }
            content3.put("lines",lines2);

            //变更后合同现金流信息
            JSONObject content4=new JSONObject(new LinkedHashMap());
            JSONObject header4=new JSONObject(new LinkedHashMap());
            header4.put("name","变更后合同现金流信息");
            header4.put("icon","profile");
            header4.put("orderSeq",4);
            header4.put("hasChildren",true);
            header4.put("isShow",true);
            content4.put("header",header4);
            JSONArray lines3=new JSONArray();

            for (DepositManageHd manageHd : depositManageHds2) {
                if(manageHd!=null){
                    JSONArray lines31=new JSONArray();

                    JSONObject line41=new JSONObject(new LinkedHashMap());
                    line41.put("fieldDescription","期数");
                    line41.put("fieldName","times");
                    line41.put("fieldType","Text");
                    line41.put("fieldValue",manageHd.getTimes());
                    line41.put("orderSeq",1);
                    line41.put("isEdit",false);
                    line41.put("isShow",true);
                    line41.put("nullableFlag",true);

                    JSONObject line42=new JSONObject(new LinkedHashMap());
                    line42.put("fieldDescription","租金");
                    line42.put("fieldName","dueAmount");
                    line42.put("fieldType","Text");
                    line42.put("fieldValue",manageHd.getDueAmount());
                    line42.put("orderSeq",2);
                    line42.put("isEdit",false);
                    line42.put("isShow",true);
                    line42.put("nullableFlag",true);

                    JSONObject line43=new JSONObject(new LinkedHashMap());
                    line43.put("fieldDescription","本金");
                    line43.put("fieldName","netPrincipal");
                    line43.put("fieldType","Text");
                    line43.put("fieldValue",manageHd.getNetPrincipal());
                    line43.put("orderSeq",3);
                    line43.put("isEdit",false);
                    line43.put("isShow",true);
                    line43.put("nullableFlag",true);

                    JSONObject line44=new JSONObject(new LinkedHashMap());
                    line44.put("fieldDescription","利息");
                    line44.put("fieldName","interest");
                    line44.put("fieldType","Text");
                    line44.put("fieldValue",manageHd.getInterest());
                    line44.put("orderSeq",4);
                    line44.put("isEdit",false);
                    line44.put("isShow",true);
                    line44.put("nullableFlag",true);

                    JSONObject line45=new JSONObject(new LinkedHashMap());
                    line45.put("fieldDescription","现金流项目类型");
                    line45.put("fieldName","cfItemN");
                    line45.put("fieldType","Text");
                    line45.put("fieldValue",manageHd.getCfItemN());
                    line45.put("orderSeq",5);
                    line45.put("isEdit",false);
                    line45.put("isShow",true);
                    line45.put("nullableFlag",true);

                    JSONObject line46=new JSONObject(new LinkedHashMap());
                    line46.put("fieldDescription","退还抵扣方式");
                    line46.put("fieldName","depositDeductionN");
                    line46.put("fieldType","Text");
                    line46.put("fieldValue",manageHd.getDepositDeductionN()==null?"":manageHd.getDepositDeductionN());
                    line46.put("orderSeq",6);
                    line46.put("isEdit",false);
                    line46.put("isShow",true);
                    line46.put("nullableFlag",true);

                    JSONObject line47=new JSONObject(new LinkedHashMap());
                    line47.put("fieldDescription","支付日期");
                    line47.put("fieldName","dueDate");
                    line47.put("fieldType","Text");
                    String dueDate2 = sdf.format(manageHd.getDueDate());
                    line47.put("fieldValue",dueDate2);
                    line47.put("orderSeq",7);
                    line47.put("isEdit",false);
                    line47.put("isShow",true);
                    line47.put("nullableFlag",true);

                    lines31.add(line41);
                    lines31.add(line42);
                    lines31.add(line43);
                    lines31.add(line44);
                    lines31.add(line45);
                    lines31.add(line46);
                    lines31.add(line47);

                    lines3.add(lines31);
                }
            }
            if(depositManageHds2.size()<=0){
                lines3.add(new JSONArray());
            }
            content4.put("lines",lines3);

            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","approve");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines4=new JSONArray();

            JSONObject line51=new JSONObject(new LinkedHashMap());
            line51.put("fieldDescription","退回");
            line51.put("fieldName","sendback");
            line51.put("fieldType","Text");
            line51.put("fieldValue","SENDBACK");
            line51.put("orderSeq",1);
            line51.put("isEdit",false);
            line51.put("isShow",true);
            line51.put("nullableFlag",false);

            lines4.add(line51);
            content5.put("lines",lines4);

            content.add(content1);
            content.add(content2);
            content.add(content3);
            content.add(content4);
            content.add(content5);

            return content;
        }catch (Exception e){
            logger.error("----------罚息减免流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //保证金代付期中租金审批流程
    private JSONArray depositManageHdProcess2(){
        try {
            JSONArray content=new JSONArray();

            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            DepositManageHd depositManageHd=new DepositManageHd();
            depositManageHd.setManageHdId(businessKey);
            List<DepositManageHd> depositManageHds = depositManageHdMapper.selectDepositManageByField(depositManageHd);
            DepositManageHd depositManageHd1=depositManageHds.get(0);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","合同名称");
            line1.put("fieldName","contractName");
            line1.put("fieldType","Text");
            line1.put("fieldValue",depositManageHd1.getContractName());
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","支付表编号");
            line2.put("fieldName","contractNumber");
            line2.put("fieldType","Text");
            line2.put("fieldValue",depositManageHd1.getContractNumber());
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","客户名称");
            line3.put("fieldName","bpName");
            line3.put("fieldType","Text");
            line3.put("fieldValue",depositManageHd1.getBpName());
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","申请时间");
            line4.put("fieldName","createDate");
            line4.put("fieldType","Text");
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
            String dueDate = sdf.format(depositManageHd1.getCreateDate());
            line4.put("fieldValue",dueDate);
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","申请人");
            line5.put("fieldName","applicant");
            line5.put("fieldType","Text");
            line5.put("fieldValue",depositManageHd1.getApplicant());
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","现金流核销状态");
            line6.put("fieldName","verificationState");
            line6.put("fieldType","Text");
            line6.put("fieldValue",depositManageHd1.getVerificationState());
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","方案编号");
            line7.put("fieldName","manageNumber");
            line7.put("fieldType","Text");
            line7.put("fieldValue",depositManageHd1.getManageNumber());
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);

            content1.put("lines",lines);

            //保证金代付租金明细
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","保证金代付租金明细");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","保证金金额(元)");
            line21.put("fieldName","depositAmount");
            line21.put("fieldType","Text");
            line21.put("fieldValue",depositManageHd1.getDepositAmount());
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","执行金额(元)");
            line22.put("fieldName","afterChangeWayN");
            line22.put("fieldType","Text");
            line22.put("fieldValue",depositManageHd1.getPerformAmount());
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","保证金余额");
            line23.put("fieldName","afterperformBalance");
            line23.put("fieldType","Text");
            line23.put("fieldValue",depositManageHd1.getAfterperformBalance());
            line23.put("orderSeq",3);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            JSONObject line24=new JSONObject(new LinkedHashMap());
            line24.put("fieldDescription","支付日期");
            line24.put("fieldName","dueDate");
            line24.put("fieldType","Text");
            String dueDate3="";
            if(depositManageHd1.getDueDate()!=null){
                dueDate3= sdf.format(depositManageHd1.getDueDate());
            }
            line24.put("fieldValue",dueDate3);
            line24.put("orderSeq",4);
            line24.put("isEdit",false);
            line24.put("isShow",true);
            line24.put("nullableFlag",true);

            lines1.add(line21);
            lines1.add(line22);
            lines1.add(line23);
            lines1.add(line24);

            content2.put("lines",lines1);

            //保证金代付现金流信息
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","保证金代付现金流信息");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",true);
            header3.put("isShow",true);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();
            List<DepositManageHd> depositManageHds1 = depositManageHdMapper.conRentCashQueryAll(depositManageHds.get(0));

            for (DepositManageHd manageHd : depositManageHds1) {
                if(manageHd!=null){
                    JSONArray lines21=new JSONArray();

                    JSONObject line31=new JSONObject(new LinkedHashMap());
                    line31.put("fieldDescription","期数");
                    line31.put("fieldName","times");
                    line31.put("fieldType","Text");
                    line31.put("fieldValue",manageHd.getTimes());
                    line31.put("orderSeq",1);
                    line31.put("isEdit",false);
                    line31.put("isShow",true);
                    line31.put("nullableFlag",true);

                    JSONObject line32=new JSONObject(new LinkedHashMap());
                    line32.put("fieldDescription","支付日期");
                    line32.put("fieldName","dueDate");
                    line32.put("fieldType","Text");
                    String dueDate4 = sdf.format(manageHd.getDueDate());
                    line32.put("fieldValue",dueDate4);
                    line32.put("orderSeq",2);
                    line32.put("isEdit",false);
                    line32.put("isShow",true);
                    line32.put("nullableFlag",true);

                    JSONObject line33=new JSONObject(new LinkedHashMap());
                    line33.put("fieldDescription","租金");
                    line33.put("fieldName","dueAmount");
                    line33.put("fieldType","Text");
                    line33.put("fieldValue",manageHd.getDueAmount());
                    line33.put("orderSeq",3);
                    line33.put("isEdit",false);
                    line33.put("isShow",true);
                    line33.put("nullableFlag",true);

                    JSONObject line34=new JSONObject(new LinkedHashMap());
                    line34.put("fieldDescription","本金");
                    line34.put("fieldName","principal");
                    line34.put("fieldType","Text");
                    line34.put("fieldValue",manageHd.getPrincipal());
                    line34.put("orderSeq",4);
                    line34.put("isEdit",false);
                    line34.put("isShow",true);
                    line34.put("nullableFlag",true);

                    JSONObject line35=new JSONObject(new LinkedHashMap());
                    line35.put("fieldDescription","不含税本金");
                    line35.put("fieldName","netPrincipal");
                    line35.put("fieldType","Text");
                    line35.put("fieldValue",manageHd.getNetPrincipal());
                    line35.put("orderSeq",5);
                    line35.put("isEdit",false);
                    line35.put("isShow",true);
                    line35.put("nullableFlag",true);

                    JSONObject line36=new JSONObject(new LinkedHashMap());
                    line36.put("fieldDescription","利息");
                    line36.put("fieldName","interest");
                    line36.put("fieldType","Text");
                    line36.put("fieldValue",manageHd.getInterest());
                    line36.put("orderSeq",6);
                    line36.put("isEdit",false);
                    line36.put("isShow",true);
                    line36.put("nullableFlag",true);

                    JSONObject line37=new JSONObject(new LinkedHashMap());
                    line37.put("fieldDescription","手续费");
                    line37.put("fieldName","leaseCharge");
                    line37.put("fieldType","Text");
                    line37.put("fieldValue",manageHd.getLeaseCharge());
                    line37.put("orderSeq",7);
                    line37.put("isEdit",false);
                    line37.put("isShow",true);
                    line37.put("nullableFlag",true);

                    JSONObject line38=new JSONObject(new LinkedHashMap());
                    line38.put("fieldDescription","保证金");
                    line38.put("fieldName","deposit");
                    line38.put("fieldType","Text");
                    line38.put("fieldValue",manageHd.getDeposit());
                    line38.put("orderSeq",8);
                    line38.put("isEdit",false);
                    line38.put("isShow",true);
                    line38.put("nullableFlag",true);

                    JSONObject line39=new JSONObject(new LinkedHashMap());
                    line39.put("fieldDescription","租前息");
                    line39.put("fieldName","beforeRentTotal");
                    line39.put("fieldType","Text");
                    line39.put("fieldValue",manageHd.getBeforeRentTotal());
                    line39.put("orderSeq",9);
                    line39.put("isEdit",false);
                    line39.put("isShow",true);
                    line39.put("nullableFlag",true);

                    JSONObject line310=new JSONObject(new LinkedHashMap());
                    line310.put("fieldDescription","首付款");
                    line310.put("fieldName","downPayment");
                    line310.put("fieldType","Text");
                    line310.put("fieldValue",manageHd.getDownPayment());
                    line310.put("orderSeq",10);
                    line310.put("isEdit",false);
                    line310.put("isShow",true);
                    line310.put("nullableFlag",true);

                    lines21.add(line31);
                    lines21.add(line32);
                    lines21.add(line33);
                    lines21.add(line34);
                    lines21.add(line35);
                    lines21.add(line36);
                    lines21.add(line37);
                    lines21.add(line38);
                    lines21.add(line39);
                    lines21.add(line310);

                    lines2.add(lines21);
                }
            }
            if(depositManageHds1.size()<=0){
                lines2.add(new JSONArray());
            }
            content3.put("lines",lines2);

            //退回按钮
            JSONObject content4=new JSONObject(new LinkedHashMap());
            JSONObject header4=new JSONObject(new LinkedHashMap());
            header4.put("name","approve");
            header4.put("orderSeq",4);
            header4.put("hasChildren",false);
            content4.put("header",header4);
            JSONArray lines3=new JSONArray();

            JSONObject line41=new JSONObject(new LinkedHashMap());
            line41.put("fieldDescription","退回");
            line41.put("fieldName","sendback");
            line41.put("fieldType","Text");
            line41.put("fieldValue","SENDBACK");
            line41.put("orderSeq",1);
            line41.put("isEdit",false);
            line41.put("isShow",true);
            line41.put("nullableFlag",false);

            lines3.add(line41);
            content4.put("lines",lines3);

            content.add(content1);
            content.add(content2);
            content.add(content3);
            content.add(content4);

            return content;
        }catch (Exception e){
            logger.error("----------罚息减免流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //罚息减免移动端审批
    private JSONArray hlsCusCshContractCashflowProcess(){
        try{
            JSONArray content=new JSONArray();

            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusConDebtExemptionReq conDebtExemptionReq = new HlsCusConDebtExemptionReq();
            conDebtExemptionReq.setChangeReqId(businessKey);
            HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq = hlsCusConDebtExemptionReqMapper.selectByPrimaryKey(conDebtExemptionReq);
            HlsCusConContract hlsCusConContract=new HlsCusConContract();
            hlsCusConContract.setContractId(hlsCusConDebtExemptionReq.getDocumentId());
            hlsCusConContract.setOverdueTimes(-1);
            List<Map> contractList = hlsCusCshContractCashflowMapper.queryOverdueContractList(hlsCusConContract);
            HlsCusConDebtExemptionReqCf hlsCusConDebtExemptionReqCf=new HlsCusConDebtExemptionReqCf();
            hlsCusConDebtExemptionReqCf.setChangeReqId(businessKey);
            List<Map> hlsCusConDebtExemptionReqCfs = hlsCusConDebtExemptionReqCfMapper.selectConDebtExemptionReqCf1(hlsCusConDebtExemptionReqCf);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","合同名称");
            line1.put("fieldName","contractName");
            line1.put("fieldType","Text");
            line1.put("fieldValue",contractList.get(0).get("contract_name"));
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","承租人");
            line2.put("fieldName","tenantName");
            line2.put("fieldType","Text");
            String tenant_name = (String) contractList.get(0).get("tenant_name");
            String tenantName = "";
            tenantName=tenant_name.substring(1, (int)tenant_name.length());
            line2.put("fieldValue",tenantName);
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","计算截止日");
            line3.put("fieldName","dueDate");
            line3.put("fieldType","Text");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String reqDate = sdf.format(hlsCusConDebtExemptionReq.getReqDate());
            line3.put("fieldValue",reqDate);
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","变更原因");
            line4.put("fieldName","description");
            line4.put("fieldType","Text");
            line4.put("fieldValue",hlsCusConDebtExemptionReq.getDescription());
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);

            content1.put("lines",lines);

            //减免现金流
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","减免现金流");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",true);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();

            for (Map cusConDebtExemptionReqCf : hlsCusConDebtExemptionReqCfs) {
                JSONArray lines11=new JSONArray();

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","期数");
                line21.put("fieldName","times");
                line21.put("fieldType","Text");
                line21.put("fieldValue",cusConDebtExemptionReqCf.get("times"));
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","到期日");
                line22.put("fieldName","dueDate");
                line22.put("fieldType","Text");
                Timestamp due_date = Timestamp.valueOf(String.valueOf(cusConDebtExemptionReqCf.get("due_date")));
                String dueDate = sdf.format(due_date);
                line22.put("fieldValue",dueDate);
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","罚息总额");
                line23.put("fieldName","dueAmount");
                line23.put("fieldType","Text");
                line23.put("fieldValue",cusConDebtExemptionReqCf.get("due_amount"));
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","未收罚金合计");
                line24.put("fieldName","uncollectedAmount");
                line24.put("fieldType","Text");
                line24.put("fieldValue",cusConDebtExemptionReqCf.get("uncollected_amount"));
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                line25.put("fieldDescription","本次减免金额");
                line25.put("fieldName","exemptionAmount");
                line25.put("fieldType","Text");
                line25.put("fieldValue",cusConDebtExemptionReqCf.get("exemption_amount"));
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                lines11.add(line21);
                lines11.add(line22);
                lines11.add(line23);
                lines11.add(line24);
                lines11.add(line25);

                lines1.add(lines11);

            }
            if(hlsCusConDebtExemptionReqCfs.size()<=0){
                lines1.add(new JSONArray());
            }
            content2.put("lines",lines1);

            //退回按钮
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","approve");
            header3.put("orderSeq",3);
            header3.put("hasChildren",false);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();

            JSONObject line31=new JSONObject(new LinkedHashMap());
            line31.put("fieldDescription","退回");
            line31.put("fieldName","sendback");
            line31.put("fieldType","Text");
            line31.put("fieldValue","SENDBACK");
            line31.put("orderSeq",1);
            line31.put("isEdit",false);
            line31.put("isShow",true);
            line31.put("nullableFlag",false);

            lines2.add(line31);
            content3.put("lines",lines2);

            content.add(content1);
            content.add(content2);
            content.add(content3);

            return content;
        }catch (Exception e){
            logger.error("----------罚息减免流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //放款申请移动端审批数据获取
    private JSONArray hlsCusCshPaymentReqHdProcess(){
        try{
            JSONArray content=new JSONArray();

            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd=new HlsCusCshPaymentReqHd();
            hlsCusCshPaymentReqHd.setPaymentReqId(businessKey);
            List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = hlsCusCshPaymentReqHdMapper.conContractCshReqDetail(hlsCusCshPaymentReqHd);
            HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd1=hlsCusCshPaymentReqHds.get(0);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","合同名称");
            line1.put("fieldName","contractName");
            line1.put("fieldType","Text");
            line1.put("fieldValue",hlsCusCshPaymentReqHd1.getContractName());
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","承租人");
            line2.put("fieldName","bpName");
            line2.put("fieldType","Text");
            line2.put("fieldValue",hlsCusCshPaymentReqHd1.getBpName());
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","利率");
            line3.put("fieldName","intRate");
            line3.put("fieldType","Text");
            line3.put("fieldValue",hlsCusCshPaymentReqHd1.getIntRate()==null?0:new BigDecimal(String.valueOf(hlsCusCshPaymentReqHd1.getIntRate()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","内部收益率");
            line4.put("fieldName","irr");
            line4.put("fieldType","Text");
            line4.put("fieldValue",hlsCusCshPaymentReqHd1.getIrr()==null?0:new BigDecimal(String.valueOf(hlsCusCshPaymentReqHd1.getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","租期(年)");
            line5.put("fieldName","leaseTerm");
            line5.put("fieldType","Text");
            line5.put("fieldValue",hlsCusCshPaymentReqHd1.getLeaseTerm());
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","拟投放时期");
            line6.put("fieldName","proposedLaunchDate");
            line6.put("fieldType","Text");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String proposedLaunchDate = sdf.format(hlsCusCshPaymentReqHd1.getProposedLaunchDate());
            line6.put("fieldValue",proposedLaunchDate);
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","资金用途");
            line7.put("fieldName","financeNote");
            line7.put("fieldType","Text");
            line7.put("fieldValue",hlsCusCshPaymentReqHd1.getFinanceNote());
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            JSONObject line8=new JSONObject(new LinkedHashMap());
            line8.put("fieldDescription","本次放款总额(元)");
            line8.put("fieldName","loanTotalAmount");
            line8.put("fieldType","Text");
            line8.put("fieldValue",hlsCusCshPaymentReqHd1.getLoanTotalAmount());
            line8.put("orderSeq",8);
            line8.put("isEdit",false);
            line8.put("isShow",true);
            line8.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);
            lines.add(line8);

            content1.put("lines",lines);
            content.add(content1);

            //放款信息
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","放款信息");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",true);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();
            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn=new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setPaymentReqId(businessKey);
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns = hlsCusCshPaymentReqLnMapper.selectPaymentLnNew(hlsCusCshPaymentReqLn);

            for (HlsCusCshPaymentReqLn cusCshPaymentReqLn : hlsCusCshPaymentReqLns) {
                JSONArray lines11=new JSONArray();

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","放款方式");
                line21.put("fieldName","paymentMethodN");
                line21.put("fieldType","Text");
                line21.put("fieldValue",cusCshPaymentReqLn.getPaymentMethodN());
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","金额");
                line22.put("fieldName","sumDueAmount");
                line22.put("fieldType","Text");
                line22.put("fieldValue",cusCshPaymentReqLn.getSumDueAmount());
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","收款方名称");
                line23.put("fieldName","bpIdN");
                line23.put("fieldType","Text");
                line23.put("fieldValue",cusCshPaymentReqLn.getBpIdN());
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","收款方户名");
                line24.put("fieldName","bpBankAccountNameN");
                line24.put("fieldType","Text");
                line24.put("fieldValue",cusCshPaymentReqLn.getBpBankAccountNameN());
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                line25.put("fieldDescription","银行账号");
                line25.put("fieldName","bpBankAccountNum");
                line25.put("fieldType","Text");
                line25.put("fieldValue",cusCshPaymentReqLn.getBpBankAccountNum());
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                JSONObject line26=new JSONObject(new LinkedHashMap());
                line26.put("fieldDescription","开户银行名称");
                line26.put("fieldName","bpBankBranchName");
                line26.put("fieldType","Text");
                line26.put("fieldValue",cusCshPaymentReqLn.getBpBankBranchName());
                line26.put("orderSeq",6);
                line26.put("isEdit",false);
                line26.put("isShow",true);
                line26.put("nullableFlag",true);

                lines11.add(line21);
                lines11.add(line22);
                lines11.add(line23);
                lines11.add(line24);
                lines11.add(line25);
                lines11.add(line26);

                lines1.add(lines11);
            }
            if(hlsCusCshPaymentReqLns.size()<=0){
                lines1.add(new JSONArray());
            }
            content2.put("lines",lines1);
            content.add(content2);

            // 附件资料
            CshPaymentAttachment cshPaymentAttachment=new CshPaymentAttachment();
            cshPaymentAttachment.setPaymentReqId(businessKey);
            List<CshPaymentAttachment> cshPaymentAttachments = cshPaymentAttachmentMapper.selectPaymentAttachmentInfo1(cshPaymentAttachment);
            if(CollectionUtils.isNotEmpty(cshPaymentAttachments)) {
                //附件资料
                JSONObject content3 = new JSONObject(new LinkedHashMap());
                JSONObject header3 = new JSONObject(new LinkedHashMap());
                header3.put("name", "附件资料");
                header3.put("icon", "profile");
                header3.put("orderSeq", 3);
                header3.put("hasChildren", true);
                header3.put("isShow", true);
                content3.put("header", header3);
                JSONArray lines2 = new JSONArray();
                for (CshPaymentAttachment paymentAttachment : cshPaymentAttachments) {
                    if (paymentAttachment.getFileName() != null && paymentAttachment.getFileName() != "") {
                        String[] attachmentIds = paymentAttachment.getAttachmentId().split(",");
                        String[] fileNames = paymentAttachment.getFileName().split(",");
                        // 取小值，防止异常数据
                        int len = Math.min(attachmentIds.length,fileNames.length);
                        for (int i = 0; i < len; i++) {
                            FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[i]);

                            JSONArray lines33=new JSONArray();
                            JSONObject line317=new JSONObject(new LinkedHashMap());
                            line317.put("fieldDescription","文件名称");
                            line317.put("fieldName","documentName");
                            line317.put("fieldType","Text");
                            line317.put("fieldValue",paymentAttachment.getDocumentName());
                            line317.put("orderSeq",1);
                            line317.put("isEdit",false);
                            line317.put("isShow",true);
                            line317.put("nullableFlag",true);
                            lines33.add(line317);

                            JSONObject line318=new JSONObject(new LinkedHashMap());
                            line318.put("fieldDescription","附件名称");
                            line318.put("fieldName","fileName");
                            line318.put("fieldType","Text");
                            line318.put("fieldValue",fndAttachment.getFileName());
                            line318.put("orderSeq",2);
                            line318.put("isEdit",false);
                            line318.put("isShow",true);
                            line318.put("nullableFlag",true);
                            lines33.add(line318);

                            JSONObject line319=new JSONObject(new LinkedHashMap());
                            line319.put("fieldDescription","附件");
                            line319.put("fieldName","relatedFile");
                            line319.put("fieldType","Link");
                            line319.put("fieldValue", systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,fndAttachment.getAttachmentId().toString()));
                            line319.put("orderSeq",3);
                            line319.put("isEdit",false);
                            line319.put("isShow",true);
                            line319.put("nullableFlag",true);
                            lines33.add(line319);

                            lines2.add(lines33);
                        }
                    }
                }

                if(CollectionUtils.isNotEmpty(lines2)){
                    content3.put("lines",lines2);
                    content.add(content3);
                }
            }


            //退回按钮
            JSONObject content4=new JSONObject(new LinkedHashMap());
            JSONObject header4=new JSONObject(new LinkedHashMap());
            header4.put("name","approve");
            header4.put("orderSeq",4);
            header4.put("hasChildren",false);
            content4.put("header",header4);
            JSONArray lines3=new JSONArray();

            JSONObject line41=new JSONObject(new LinkedHashMap());
            line41.put("fieldDescription","退回");
            line41.put("fieldName","sendback");
            line41.put("fieldType","Text");
            line41.put("fieldValue","SENDBACK");
            line41.put("orderSeq",1);
            line41.put("isEdit",false);
            line41.put("isShow",true);
            line41.put("nullableFlag",false);

            lines3.add(line41);
            content4.put("lines",lines3);
            content.add(content4);

            return  content;
        }catch (Exception e){
            logger.error("----------放款申请流程填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //合同支付表确认移动端审批数据获取
    private JSONArray hlsCusConContractProcess(){
        try{
            JSONArray content=new JSONArray();
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            Map<String,Object> map=new HashMap<>();
            map.put("paymentConfirmId",businessKey);
            List<Map> hlsCusConContracts = hlsCusConContractMapper.conContractRentPaymentConfirmHome(map);

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","合同名称");
            line1.put("fieldName","contractName");
            line1.put("fieldType","Text");
            line1.put("fieldValue",hlsCusConContracts.get(0).get("contractName"));
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","合同编号");
            line2.put("fieldName","contractNumber");
            line2.put("fieldType","Text");
            line2.put("fieldValue",hlsCusConContracts.get(0).get("contractNumber"));
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","业务类型");
            line3.put("fieldName","businessTypeN");
            line3.put("fieldType","Text");
            line3.put("fieldValue",hlsCusConContracts.get(0).get("businessTypeN"));
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","项目主办");
            line4.put("fieldName","hostProjectManagerN");
            line4.put("fieldType","Text");
            line4.put("fieldValue",hlsCusConContracts.get(0).get("hostProjectManagerN"));
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","合同金额");
            line5.put("fieldName","contractAmount");
            line5.put("fieldType","Text");
            line5.put("fieldValue",hlsCusConContracts.get(0).get("contractAmount"));
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","罚息利率");
            line6.put("fieldName","falsifyInterestRate");
            line6.put("fieldType","Text");
            line6.put("fieldValue",(BigDecimal)hlsCusConContracts.get(0).get("falsifyInterestRate")==null? "0":((BigDecimal)hlsCusConContracts.get(0).get("falsifyInterestRate")).multiply(new BigDecimal("100"))+"%");
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","违约金率");
            line7.put("fieldName","penaltyRatio");
            line7.put("fieldType","Text");
            line7.put("fieldValue",(BigDecimal)hlsCusConContracts.get(0).get("penaltyRatio")==null? "0":((BigDecimal)hlsCusConContracts.get(0).get("penaltyRatio")).multiply(new BigDecimal("100"))+"%");
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            JSONObject line8=new JSONObject(new LinkedHashMap());
            line8.put("fieldDescription","起租类型");
            line8.put("fieldName","loanInitialLeaseN");
            line8.put("fieldType","Text");
            line8.put("fieldValue",hlsCusConContracts.get(0).get("loanInitialLeaseN"));
            line8.put("orderSeq",8);
            line8.put("isEdit",false);
            line8.put("isShow",true);
            line8.put("nullableFlag",true);

            JSONObject line9=new JSONObject(new LinkedHashMap());
            line9.put("fieldDescription","签约时间");
            line9.put("fieldName","signDate");
            line9.put("fieldType","Text");
            line9.put("fieldValue",hlsCusConContracts.get(0).get("signDate"));
            line9.put("orderSeq",9);
            line9.put("isEdit",false);
            line9.put("isShow",true);
            line9.put("nullableFlag",true);

            JSONObject line10=new JSONObject(new LinkedHashMap());
            line10.put("fieldDescription","签约地点");
            line10.put("fieldName","singAddr");
            line10.put("fieldType","Text");
            line10.put("fieldValue",hlsCusConContracts.get(0).get("singAddr"));
            line10.put("orderSeq",10);
            line10.put("isEdit",false);
            line10.put("isShow",true);
            line10.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);
            lines.add(line8);
            lines.add(line9);
            lines.add(line10);

            content1.put("lines",lines);

            //报价方案
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","报价方案");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines1=new JSONArray();
            HlsCusPrjQuotation hlsCusPrjQuotation=new HlsCusPrjQuotation();
            hlsCusPrjQuotation.setSourceDocumentCategory("CONTRACT_CONFIRM");
            hlsCusPrjQuotation.setSourceDocumentId(businessKey);
            List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(hlsCusPrjQuotation);
            HlsCusPrjQuotation hlsCusPrjQuotation1=hlsCusPrjQuotations.get(0);

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","项目金额(元)");
            line21.put("fieldName","financeAmount");
            line21.put("fieldType","Text");
            line21.put("fieldValue",hlsCusPrjQuotation1.getFinanceAmount());
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","投放日");
            line22.put("fieldName","leaseStartDate");
            line22.put("fieldType","Text");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String leaseStartDate = sdf.format(hlsCusPrjQuotation1.getLeaseStartDate());
            line22.put("fieldValue",leaseStartDate);
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","XIRR");
            line23.put("fieldName","xirr");
            line23.put("fieldType","Text");
            line23.put("fieldValue",hlsCusPrjQuotation1.getXirr()==null? 0 :new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getXirr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
            line23.put("orderSeq",3);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            JSONObject line24=new JSONObject(new LinkedHashMap());
            line24.put("fieldDescription","IRR");
            line24.put("fieldName","irr");
            line24.put("fieldType","Text");
            line24.put("fieldValue",hlsCusPrjQuotation1.getIrr()==null? 0 :new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
            line24.put("orderSeq",4);
            line24.put("isEdit",false);
            line24.put("isShow",true);
            line24.put("nullableFlag",true);

            JSONObject line25=new JSONObject(new LinkedHashMap());
            line25.put("fieldDescription","税后IRR");
            line25.put("fieldName","irrAfterTax");
            line25.put("fieldType","Text");
            line25.put("fieldValue",hlsCusPrjQuotation1.getIrrAfterTax()==null? 0 :new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getIrrAfterTax()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
            line25.put("orderSeq",5);
            line25.put("isEdit",false);
            line25.put("isShow",true);
            line25.put("nullableFlag",true);

            JSONObject line26=new JSONObject(new LinkedHashMap());
            line26.put("fieldDescription","每期租金");
            line26.put("fieldName","pmt");
            line26.put("fieldType","Text");
            line26.put("fieldValue",hlsCusPrjQuotation1.getPmt());
            line26.put("orderSeq",6);
            line26.put("isEdit",false);
            line26.put("isShow",true);
            line26.put("nullableFlag",true);

            JSONObject line27=new JSONObject(new LinkedHashMap());
            line27.put("fieldDescription","手续费收入总额");
            line27.put("fieldName","leaseCharge");
            line27.put("fieldType","Text");
            line27.put("fieldValue",hlsCusPrjQuotation1.getLeaseCharge());
            line27.put("orderSeq",7);
            line27.put("isEdit",false);
            line27.put("isShow",true);
            line27.put("nullableFlag",true);

            JSONObject line28=new JSONObject(new LinkedHashMap());
            line28.put("fieldDescription","租前息收入总额");
            line28.put("fieldName","beforeRentTotal");
            line28.put("fieldType","Text");
            line28.put("fieldValue",hlsCusPrjQuotation1.getBeforeRentTotal());
            line28.put("orderSeq",8);
            line28.put("isEdit",false);
            line28.put("isShow",true);
            line28.put("nullableFlag",true);

            lines1.add(line21);
            lines1.add(line22);
            lines1.add(line23);
            lines1.add(line24);
            lines1.add(line25);
            lines1.add(line26);
            lines1.add(line27);
            lines1.add(line28);

            content2.put("lines",lines1);

            //现金流信息
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","现金流信息");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",true);
            header3.put("isShow",true);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();
            HlsCusConContractCashflow cashflow=new HlsCusConContractCashflow();
            cashflow.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
            List<HlsCusConContractCashflow> hlsCusConContractCashflows = hlsCusConContractCashflowMapper.selectCashflowInfoByPrjNew1(cashflow);

            for (HlsCusConContractCashflow hlsCusConContractCashflow : hlsCusConContractCashflows) {
                JSONArray lines21=new JSONArray();

                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","期数");
                line31.put("fieldName","times");
                line31.put("fieldType","Text");
                line31.put("fieldValue",hlsCusConContractCashflow.getTimes());
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","支付日期");
                line32.put("fieldName","dueDate");
                line32.put("fieldType","Text");
                String date = sdf.format(hlsCusConContractCashflow.getDueDate());
                line32.put("fieldValue",date);
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","租金");
                line33.put("fieldName","dueAmount");
                line33.put("fieldType","Text");
                line33.put("fieldValue",hlsCusConContractCashflow.getDueAmount());
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                JSONObject line34=new JSONObject(new LinkedHashMap());
                line34.put("fieldDescription","本金");
                line34.put("fieldName","netPrincipal");
                line34.put("fieldType","Text");
                line34.put("fieldValue",hlsCusConContractCashflow.getNetPrincipal());
                line34.put("orderSeq",4);
                line34.put("isEdit",false);
                line34.put("isShow",true);
                line34.put("nullableFlag",true);

                JSONObject line35=new JSONObject(new LinkedHashMap());
                line35.put("fieldDescription","利息");
                line35.put("fieldName","theInterest");
                line35.put("fieldType","Text");
                line35.put("fieldValue",hlsCusConContractCashflow.getTheInterest());
                line35.put("orderSeq",5);
                line35.put("isEdit",false);
                line35.put("isShow",true);
                line35.put("nullableFlag",true);

                lines21.add(line31);
                lines21.add(line32);
                lines21.add(line33);
                lines21.add(line34);
                lines21.add(line35);
                lines2.add(lines21);
            }
            if(hlsCusConContractCashflows.size()<=0){
                lines2.add(new JSONArray());
            }
            content3.put("lines",lines2);

            //退回按钮
            JSONObject content4=new JSONObject(new LinkedHashMap());
            JSONObject header4=new JSONObject(new LinkedHashMap());
            header4.put("name","approve");
            header4.put("orderSeq",4);
            header4.put("hasChildren",false);
            content4.put("header",header4);
            JSONArray lines3=new JSONArray();

            JSONObject line41=new JSONObject(new LinkedHashMap());
            line41.put("fieldDescription","退回");
            line41.put("fieldName","sendback");
            line41.put("fieldType","Text");
            line41.put("fieldValue","SENDBACK");
            line41.put("orderSeq",1);
            line41.put("isEdit",false);
            line41.put("isShow",true);
            line41.put("nullableFlag",false);

            lines3.add(line41);
            content4.put("lines",lines3);

            content.add(content1);
            content.add(content2);
            content.add(content3);
            content.add(content4);

            return content;
        }catch (Exception e){
            logger.error("----------合同支付表确认移动端审批填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //项目评审审议流程数据获取
    private JSONArray hlsCusPriProjectProcess(String taskName){
        try{
            JSONArray content=new JSONArray();
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusPrjProject prjProject =new HlsCusPrjProject();
            prjProject.setProjectId(businessKey);
            List<Map> prjProjects = hlsCusPrjProjectMapper.queryPrjDetailSecond(prjProject);
            HlsCusPrjProjectBp hlsCusPrjProjectBp=new HlsCusPrjProjectBp();
            hlsCusPrjProjectBp.setProjectId(businessKey);
            List<HlsCusPrjProjectBp> hlsCusPrjProjectBps = hlsCusPrjProjectBpMapper.selectByForeignKey2(hlsCusPrjProjectBp);
            HlsCreditPlan hlsCreditPlan=new HlsCreditPlan();
            hlsCreditPlan.setChanceId(businessKey);
            List<HlsCreditPlan> hlsCreditPlans = hlsCreditPlanMapper.queryCreditPlanInfo(hlsCreditPlan);
            List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps = hlsCusPrjProjectBpMapper.selectByForeignKey(hlsCusPrjProjectBp);

            JSONObject content2=new JSONObject(new LinkedHashMap());
            if(prjProjects.get(0).get("businessTypeN")==null&&prjProjects.get(0).get("industryN")==null){
                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","项目名称");
                line1.put("fieldName","projectName");
                line1.put("fieldType","Text");
                line1.put("fieldValue",prjProjects.get(0).get("projectName"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","项目编号");
                line2.put("fieldName","projectNumber");
                line2.put("fieldType","Text");
                line2.put("fieldValue",prjProjects.get(0).get("projectNumber"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","系内/外");
                line3.put("fieldName","isLowRiskN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",prjProjects.get(0).get("is_low_risk_n"));
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","项目主办");
                line4.put("fieldName","hostProjectManagerN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",prjProjects.get(0).get("hostProjectManagerN"));
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","项目协办");
                line5.put("fieldName","assistProjectManagerN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",prjProjects.get(0).get("assistProjectManagerN"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","申请日期");
                line6.put("fieldName","touchCreatedDate");
                line6.put("fieldType","Text");
                line6.put("fieldValue",prjProjects.get(0).get("touchCreatedDate"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","项目所在地");
                line7.put("fieldName","cityN");
                line7.put("fieldType","Text");
                line7.put("fieldValue",prjProjects.get(0).get("cityN"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","资金用途");
                line8.put("fieldName","financeNote");
                line8.put("fieldType","Text");
                line8.put("fieldValue",prjProjects.get(0).get("financeNote"));
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","增信措施");
                line9.put("fieldName","guaranteeMethodNote");
                line9.put("fieldType","Text");
                line9.put("fieldValue",prjProjects.get(0).get("guaranteeMethodNote"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);

                content1.put("lines",lines);

                //授信方案
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","授信方案");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","授信金额");
                line21.put("fieldName","creditAmt");
                line21.put("fieldType","Text");
                line21.put("fieldValue",hlsCreditPlans.get(0).getCreditAmt());
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","额度类型");
                line22.put("fieldName","quotaTypeN");
                line22.put("fieldType","Text");
                line22.put("fieldValue",hlsCreditPlans.get(0).getQuotaTypeN());
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","是否集团授信");
                line23.put("fieldName","conglomerateFlagN");
                line23.put("fieldType","Text");
                line23.put("fieldValue",hlsCreditPlans.get(0).getConglomerateFlagN());
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","所属集团");
                line24.put("fieldName","belongConglomerate");
                line24.put("fieldType","Text");
                line24.put("fieldValue",hlsCreditPlans.get(0).getBelongConglomerate());
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                line25.put("fieldDescription","授信开始日期");
                line25.put("fieldName","dateFrom");
                line25.put("fieldType","Text");
                String dateForm = sdf.format(hlsCreditPlans.get(0).getDateFrom());
                line25.put("fieldValue",dateForm);
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                JSONObject line26=new JSONObject(new LinkedHashMap());
                line26.put("fieldDescription","授信结束日期");
                line26.put("fieldName","dateTo");
                line26.put("fieldType","Text");
                String dateTo = sdf.format(hlsCreditPlans.get(0).getDateTo());
                line26.put("fieldValue",dateTo);
                line26.put("orderSeq",6);
                line26.put("isEdit",false);
                line26.put("isShow",true);
                line26.put("nullableFlag",true);

                JSONObject line27=new JSONObject(new LinkedHashMap());
                line27.put("fieldDescription","IRR不低于(%)");
                line27.put("fieldName","irr");
                line27.put("fieldType","Text");
                line27.put("fieldValue",hlsCreditPlans.get(0).getIrr()==null?0:new BigDecimal(String.valueOf(hlsCreditPlans.get(0).getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line27.put("orderSeq",7);
                line27.put("isEdit",false);
                line27.put("isShow",true);
                line27.put("nullableFlag",true);

                JSONObject line28=new JSONObject(new LinkedHashMap());
                line28.put("fieldDescription","融资期限不超过(年)");
                line28.put("fieldName","financingDate");
                line28.put("fieldType","Text");
                line28.put("fieldValue",hlsCreditPlans.get(0).getFinancingDate()==null?0:hlsCreditPlans.get(0).getFinancingDate());
                line28.put("orderSeq",8);
                line28.put("isEdit",false);
                line28.put("isShow",true);
                line28.put("nullableFlag",true);

                JSONObject line29=new JSONObject(new LinkedHashMap());
                line29.put("fieldDescription","授信说明");
                line29.put("fieldName","creditDesc");
                line29.put("fieldType","Text");
                line29.put("fieldValue",hlsCreditPlans.get(0).getCreditDesc());
                line29.put("orderSeq",7);
                line29.put("isEdit",false);
                line29.put("isShow",true);
                line29.put("nullableFlag",true);

                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);
                lines1.add(line24);
                lines1.add(line25);
                lines1.add(line26);
                lines1.add(line27);
                lines1.add(line28);
                lines1.add(line29);

                content2.put("lines",lines1);
            }else{
                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","项目名称");
                line1.put("fieldName","projectName");
                line1.put("fieldType","Text");
                line1.put("fieldValue",prjProjects.get(0).get("projectName"));
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","项目编号");
                line2.put("fieldName","projectNumber");
                line2.put("fieldType","Text");
                line2.put("fieldValue",prjProjects.get(0).get("projectNumber"));
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","承租人");
                line3.put("fieldName","bpIdN");
                line3.put("fieldType","Text");
                StringBuilder personA=new StringBuilder();
                StringBuilder personB=new StringBuilder();

                for (HlsCusPrjProjectBp cusPrjProjectBp : hlsCusPrjProjectBps) {
                    if(cusPrjProjectBp.getBpType().equals("TENANT")){
                        personA.append(cusPrjProjectBp.getBpIdN());
                        personA.append(",");
                    }else if(cusPrjProjectBp.getBpType().equals("WARRANTOR")){
                        personB.append(cusPrjProjectBp.getBpIdN());
                        personB.append(",");
                    }
                }
                String lessee;
                if(personA!=null&&!"".equals(personA.toString())){
                    lessee = personA.substring(0, personA.length() - 1);
                }else{
                    lessee="";
                }
                line3.put("fieldValue",lessee);
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","担保人");
                line4.put("fieldName","bpIdN");
                line4.put("fieldType","Text");
                String guarantor;
                if(personB!=null&&!"".equals(personB.toString())){
                    guarantor= personB.substring(0, personB.length() - 1);
                }else{
                    guarantor="";
                }
                line4.put("fieldValue",guarantor);
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","业务类型");
                line5.put("fieldName","businessTypeN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",prjProjects.get(0).get("businessTypeN"));
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","业务版块");
                line6.put("fieldName","industryN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",prjProjects.get(0).get("industryN"));
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","系内/外");
                line7.put("fieldName","isLowRiskN");
                line7.put("fieldType","Text");
                line7.put("fieldValue",prjProjects.get(0).get("is_low_risk_n"));
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","项目主办");
                line8.put("fieldName","hostProjectManagerN");
                line8.put("fieldType","Text");
                line8.put("fieldValue",prjProjects.get(0).get("hostProjectManagerN"));
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","申请日期");
                line9.put("fieldName","touchCreatedDate");
                line9.put("fieldType","Text");
                line9.put("fieldValue",prjProjects.get(0).get("touchCreatedDate"));
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                JSONObject line10=new JSONObject(new LinkedHashMap());
                line10.put("fieldDescription","项目所在地");
                line10.put("fieldName","cityN");
                line10.put("fieldType","Text");
                line10.put("fieldValue",prjProjects.get(0).get("cityN"));
                line10.put("orderSeq",10);
                line10.put("isEdit",false);
                line10.put("isShow",true);
                line10.put("nullableFlag",true);

                JSONObject line11=new JSONObject(new LinkedHashMap());
                line11.put("fieldDescription","行业分类");
                line11.put("fieldName","industryTypeN");
                line11.put("fieldType","Text");
                line11.put("fieldValue",prjProjects.get(0).get("industryTypeN"));
                line11.put("orderSeq",11);
                line11.put("isEdit",false);
                line11.put("isShow",true);
                line11.put("nullableFlag",true);


                JSONObject line12=new JSONObject(new LinkedHashMap());
                line12.put("fieldDescription","资金用途");
                line12.put("fieldName","financeNote");
                line12.put("fieldType","Text");
                line12.put("fieldValue",prjProjects.get(0).get("financeNote"));
                line12.put("orderSeq",12);
                line12.put("isEdit",false);
                line12.put("isShow",true);
                line12.put("nullableFlag",true);

                JSONObject line13=new JSONObject(new LinkedHashMap());
                line13.put("fieldDescription","增信措施");
                line13.put("fieldName","guaranteeMethodNote");
                line13.put("fieldType","Text");
                line13.put("fieldValue",prjProjects.get(0).get("guaranteeMethodNote"));
                line13.put("orderSeq",13);
                line13.put("isEdit",false);
                line13.put("isShow",true);
                line13.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);
                lines.add(line10);
                lines.add(line11);
                lines.add(line12);
                lines.add(line13);

                content1.put("lines",lines);

                //报价方案
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","报价方案");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();
                HlsCusPrjQuotation hlsCusPrjQuotation=new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
                hlsCusPrjQuotation.setSourceDocumentId(businessKey);
                List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(hlsCusPrjQuotation);
                HlsCusPrjQuotation hlsCusPrjQuotation1 = hlsCusPrjQuotations.get(0);

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","项目金额(元)");
                line21.put("fieldName","leaseItemAmount");
                line21.put("fieldType","Text");
                line21.put("fieldValue",hlsCusPrjQuotation1.getLeaseItemAmount());
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","租赁期限(年)");
                line22.put("fieldName","leaseTerm");
                line22.put("fieldType","Text");
                line22.put("fieldValue",hlsCusPrjQuotation1.getLeaseTerm());
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","租赁期数");
                line23.put("fieldName","leaseTimes");
                line23.put("fieldType","Text");
                line23.put("fieldValue",hlsCusPrjQuotation1.getLeaseTimes()==null?0:hlsCusPrjQuotation1.getLeaseTimes());
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","融资利率(%)");
                line24.put("fieldName","intRate");
                line24.put("fieldType","Text");
                line24.put("fieldValue",hlsCusPrjQuotation1.getIntRate()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getIntRate()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                line25.put("fieldDescription","内部收益率");
                line25.put("fieldName","irr");
                line25.put("fieldType","Text");
                line25.put("fieldValue",hlsCusPrjQuotation1.getIrr()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                JSONObject line26=new JSONObject(new LinkedHashMap());
                line26.put("fieldDescription","手续费(元)");
                line26.put("fieldName","leaseCharge");
                line26.put("fieldType","Text");
                line26.put("fieldValue",hlsCusPrjQuotation1.getLeaseCharge());
                line26.put("orderSeq",6);
                line26.put("isEdit",false);
                line26.put("isShow",true);
                line26.put("nullableFlag",true);

                JSONObject line27=new JSONObject(new LinkedHashMap());
                line27.put("fieldDescription","保证金(元)");
                line27.put("fieldName","deposit");
                line27.put("fieldType","Text");
                line27.put("fieldValue",hlsCusPrjQuotation1.getDeposit());
                line27.put("orderSeq",7);
                line27.put("isEdit",false);
                line27.put("isShow",true);
                line27.put("nullableFlag",true);

                JSONObject line28=new JSONObject(new LinkedHashMap());
                line28.put("fieldDescription","收租间隔月份");
                line28.put("fieldName","rentingFrequencyN");
                line28.put("fieldType","Text");
                line28.put("fieldValue",hlsCusPrjQuotation1.getRentingFrequencyN());
                line28.put("orderSeq",8);
                line28.put("isEdit",false);
                line28.put("isShow",true);
                line28.put("nullableFlag",true);

                JSONObject line29=new JSONObject(new LinkedHashMap());
                line29.put("fieldDescription","留购价款(元)");
                line29.put("fieldName","residualValue");
                line29.put("fieldType","Text");
                line29.put("fieldValue",hlsCusPrjQuotation1.getResidualValue());
                line29.put("orderSeq",9);
                line29.put("isEdit",false);
                line29.put("isShow",false);
                line29.put("nullableFlag",true);


                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);
                lines1.add(line24);
                lines1.add(line25);
                lines1.add(line26);
                lines1.add(line27);
                lines1.add(line28);
                lines1.add(line29);

                content2.put("lines",lines1);

            }

            //客户信息
            JSONObject content6=new JSONObject(new LinkedHashMap());
            JSONObject header6=new JSONObject(new LinkedHashMap());
            header6.put("name","客户信息");
            header6.put("icon","profile");
            header6.put("orderSeq",3);
            header6.put("hasChildren",true);
            header6.put("isShow",true);
            content6.put("header",header6);
            JSONArray lines5=new JSONArray();

            for (HlsCusPrjProjectBp cusPrjProjectBp : hlsCusPrjProjectBps) {
                JSONArray lines51=new JSONArray();

                JSONObject line61=new JSONObject(new LinkedHashMap());
                line61.put("fieldDescription","客户名称");
                line61.put("fieldName","bpIdN");
                line61.put("fieldType","Text");
                line61.put("fieldValue",cusPrjProjectBp.getBpIdN());
                line61.put("orderSeq",1);
                line61.put("isEdit",false);
                line61.put("isShow",true);
                line61.put("nullableFlag",true);

                JSONObject line62=new JSONObject(new LinkedHashMap());
                line62.put("fieldDescription","客户类型");
                line62.put("fieldName","bpTypeN");
                line62.put("fieldType","Text");
                line62.put("fieldValue",cusPrjProjectBp.getBpTypeN());
                line62.put("orderSeq",2);
                line62.put("isEdit",false);
                line62.put("isShow",true);
                line62.put("nullableFlag",true);

                JSONObject line63=new JSONObject(new LinkedHashMap());
                line63.put("fieldDescription","行业分类");
                line63.put("fieldName","economicInduClassifyN");
                line63.put("fieldType","Text");
                logger.info("========================>"+cusPrjProjectBp.getEconomicInduClassifyN());
                line63.put("fieldValue",StringUtils.trimToEmpty(cusPrjProjectBp.getEconomicInduClassifyN()));
                line63.put("orderSeq",3);
                line63.put("isEdit",false);
                line63.put("isShow",true);
                line63.put("nullableFlag",true);

                lines51.add(line61);
                lines51.add(line62);
                lines51.add(line63);

                lines5.add(lines51);
            }
            if(hlsCusPrjProjectBps.size()<=0){
                lines5.add(new JSONArray());
            }
            content6.put("lines",lines5);

            //租赁物信息
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","租赁物信息");
            header3.put("icon","profile");
            header3.put("orderSeq",4);
            header3.put("hasChildren",false);
            header3.put("isShow",true);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();

            JSONObject line31=new JSONObject(new LinkedHashMap());
            line31.put("fieldDescription","租赁物信息");
            line31.put("fieldName","leaseMatterNote");
            line31.put("fieldType","Text");
            line31.put("fieldValue",prjProjects.get(0).get("leaseMatterNote"));
            line31.put("orderSeq",1);
            line31.put("isEdit",false);
            line31.put("isShow",true);
            line31.put("nullableFlag",true);

            lines2.add(line31);
            content3.put("lines",lines2);

            //租赁物信息列表
            JSONObject content4=new JSONObject(new LinkedHashMap());
            JSONObject header4=new JSONObject(new LinkedHashMap());
            header4.put("name","租赁物信息列表");
            header4.put("icon","profile");
            header4.put("orderSeq",5);
            header4.put("hasChildren",true);
            header4.put("isShow",true);
            content4.put("header",header4);
            JSONArray lines3=new JSONArray();
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem=new HlsCusPrjProjectLeaseItem();
            hlsCusPrjProjectLeaseItem.setProjectId(businessKey);
            hlsCusPrjProjectLeaseItem.setProjectType("contract");
            List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItems = hlsCusPrjProjectLeaseItemMapper.queryPrjProjectLeaseItem1(hlsCusPrjProjectLeaseItem);

            for (HlsCusPrjProjectLeaseItem cusPrjProjectLeaseItem : hlsCusPrjProjectLeaseItems) {
                JSONArray lines31=new JSONArray();

                JSONObject line41=new JSONObject(new LinkedHashMap());
                line41.put("fieldDescription","项目名称");
                line41.put("fieldName","leaseItemIdN");
                line41.put("fieldType","Text");
                line41.put("fieldValue",cusPrjProjectLeaseItem.getLeaseItemIdN());
                line41.put("orderSeq",1);
                line41.put("isEdit",false);
                line41.put("isShow",true);
                line41.put("nullableFlag",true);

                JSONObject line42=new JSONObject(new LinkedHashMap());
                line42.put("fieldDescription","租赁物名称");
                line42.put("fieldName","assetName");
                line42.put("fieldType","Text");
                line42.put("fieldValue",cusPrjProjectLeaseItem.getAssetName());
                line42.put("orderSeq",2);
                line42.put("isEdit",false);
                line42.put("isShow",true);
                line42.put("nullableFlag",true);

                JSONObject line43=new JSONObject(new LinkedHashMap());
                line43.put("fieldDescription","数量");
                line43.put("fieldName","quantity");
                line43.put("fieldType","Text");
                line43.put("fieldValue",cusPrjProjectLeaseItem.getQuantity());
                line43.put("orderSeq",3);
                line43.put("isEdit",false);
                line43.put("isShow",true);
                line43.put("nullableFlag",true);

                JSONObject line44=new JSONObject(new LinkedHashMap());
                line44.put("fieldDescription","租赁物价值（元）");
                line44.put("fieldName","price");
                line44.put("fieldType","Text");
                line44.put("fieldValue",cusPrjProjectLeaseItem.getPrice());
                line44.put("orderSeq",4);
                line44.put("isEdit",false);
                line44.put("isShow",true);
                line44.put("nullableFlag",true);

                JSONObject line45=new JSONObject(new LinkedHashMap());
                line45.put("fieldDescription","租赁物价值（总价）");
                line45.put("fieldName","totalPrice");
                line45.put("fieldType","Text");
                line45.put("fieldValue",cusPrjProjectLeaseItem.getTotalPrice());
                line45.put("orderSeq",5);
                line45.put("isEdit",false);
                line45.put("isShow",true);
                line45.put("nullableFlag",true);

                lines31.add(line41);
                lines31.add(line42);
                lines31.add(line43);
                lines31.add(line44);
                lines31.add(line45);
                lines3.add(lines31);
            }
            if(hlsCusPrjProjectLeaseItems.size()<=0){
                lines3.add(new JSONArray());
            }
            content4.put("lines",lines3);

            //附件资料
            JSONArray lines4=new JSONArray();
            HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment=new HlsCusPrjProjectAttachment();
            hlsCusPrjProjectAttachment.setProjectId(businessKey);
            List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.selectPrjProjectAttachmentInfo1(hlsCusPrjProjectAttachment);
            List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList = hlsCusPrjProjectAttachmentMapper.selectPrjProjectJdAttachmentInfo(hlsCusPrjProjectAttachment);
            List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentAll=new ArrayList<>();
            hlsCusPrjProjectAttachmentAll.addAll(hlsCusPrjProjectAttachments);
            hlsCusPrjProjectAttachmentAll.addAll(hlsCusPrjProjectAttachmentList);
            for (HlsCusPrjProjectAttachment cusPrjProjectAttachment : hlsCusPrjProjectAttachmentAll) {
                if(StringUtils.isNotBlank(cusPrjProjectAttachment.getFileName())){
                    String[] attachmentIds = cusPrjProjectAttachment.getAttachmentId().split(",");
                    String[] fileNames = cusPrjProjectAttachment.getFileName().split(",");
                    // 取小值，防止异常数据
                    int len = Math.min(attachmentIds.length,fileNames.length);
                    for (int i = 0; i < len; i++) {
                        FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[i]);

                        JSONArray lines33=new JSONArray();
                        JSONObject line317=new JSONObject(new LinkedHashMap());
                        line317.put("fieldDescription","文件名称");
                        line317.put("fieldName","documentName");
                        line317.put("fieldType","Text");
                        line317.put("fieldValue",cusPrjProjectAttachment.getDocumentName());
                        line317.put("orderSeq",1);
                        line317.put("isEdit",false);
                        line317.put("isShow",true);
                        line317.put("nullableFlag",true);
                        lines33.add(line317);

                        JSONObject line318=new JSONObject(new LinkedHashMap());
                        line318.put("fieldDescription","附件名称");
                        line318.put("fieldName","fileName");
                        line318.put("fieldType","Text");
                        line318.put("fieldValue",fndAttachment.getFileName());
                        line318.put("orderSeq",2);
                        line318.put("isEdit",false);
                        line318.put("isShow",true);
                        line318.put("nullableFlag",true);
                        lines33.add(line318);

                        JSONObject line319=new JSONObject(new LinkedHashMap());
                        line319.put("fieldDescription","附件");
                        line319.put("fieldName","relatedFile");
                        line319.put("fieldType","Link");
                        line319.put("fieldValue", systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,fndAttachment.getAttachmentId().toString()));
                        line319.put("orderSeq",3);
                        line319.put("isEdit",false);
                        line319.put("isShow",true);
                        line319.put("nullableFlag",true);
                        lines33.add(line319);

                        lines4.add(lines33);
                    }
//                    if(StringUtils.isNotEmpty(cusPrjProjectAttachment.getFileName())){
//                        JSONArray lines41=new JSONArray();
//                        JSONObject line51=new JSONObject(new LinkedHashMap());
//                        line51.put("fieldDescription","附件资料");
//                        line51.put("fieldName","documentName");
//                        line51.put("fieldType","Text");
//                        line51.put("fieldValue",cusPrjProjectAttachment.getDocumentName());
//                        line51.put("orderSeq",1);
//                        line51.put("isEdit",false);
//                        line51.put("isShow",true);
//                        line51.put("nullableFlag",true);
//
//
//                        JSONObject line52=new JSONObject(new LinkedHashMap());
//                        line52.put("fieldDescription","附件");
//                        line52.put("fieldName","fileName");
//                        line52.put("fieldType","File");
//                        line52.put("fileTransferType", "UUID");
//                        line52.put("fieldValue",uuid);
//                        line52.put("orderSeq",2);
//                        line52.put("isEdit",false);
//                        line52.put("isShow",true);
//                        line52.put("nullableFlag",true);
//
//                        lines41.add(line51);
//                        lines41.add(line52);
//                        if("风险岗项目风险评估".equals(taskName)||"法务岗法律审查".equals(taskName)) {
//                            JSONObject line54 = new JSONObject(new LinkedHashMap());
//                            line54.put("fieldDescription", "文件上传");
//                            line54.put("fieldName", "relatedFile");
//                            line54.put("fieldType", "File");
//                            line54.put("fileTransferType", "UUID");
//                            line54.put("fieldValue", uuid);
//                            line54.put("orderSeq", 3);
//                            line54.put("isEdit", true);
//                            line54.put("isShow", true);
//                            line54.put("nullableFlag", false);
//                            lines41.add(line54);
//                        }
//                        lines4.add(lines41);
//                    }
                }
            }

            if(CollectionUtils.isNotEmpty(lines4)){
                JSONObject content5=new JSONObject(new LinkedHashMap());
                JSONObject header5=new JSONObject(new LinkedHashMap());
                header5.put("name","附件资料");
                header5.put("icon","profile");
                header5.put("orderSeq",6);
                header5.put("hasChildren",true);
                header5.put("isShow",true);
                content5.put("header",header5);
                content5.put("lines",lines4);
                content.add(content5);
            }

            //退回按钮
            JSONObject content7=new JSONObject(new LinkedHashMap());
            JSONObject header7=new JSONObject(new LinkedHashMap());
            header7.put("name","approve");
            header7.put("orderSeq",7);
            header7.put("hasChildren",false);
            content7.put("header",header7);
            JSONArray lines6=new JSONArray();

            JSONObject line71=new JSONObject(new LinkedHashMap());
            line71.put("fieldDescription","退回");
            line71.put("fieldName","sendback");
            line71.put("fieldType","Text");
            line71.put("fieldValue","SENDBACK");
            line71.put("orderSeq",1);
            line71.put("isEdit",false);
            line71.put("isShow",true);
            line71.put("nullableFlag",false);

            lines6.add(line71);
            content7.put("lines",lines6);

            content.add(content1);
            content.add(content2);
            content.add(content6);
            content.add(content3);
            content.add(content4);
            content.add(content7);

            return content;
        }catch (Exception e){
            logger.error("----------项目尽调移动端审批填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //项目立项移动端审批数据获取
    private JSONArray hlsCusHlsCreditLineChanceProcess(){
        try{
            JSONArray content=new JSONArray();
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusHlsCreditLineChance hlsCusHlsCreditLine=new HlsCusHlsCreditLineChance();
            hlsCusHlsCreditLine.setChanceId(businessKey);
            HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = hlsCusHlsCreditLineChanceMapper.selectCreditLineChanceById(hlsCusHlsCreditLine);
            HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp=new HlsCusHlsCreditLineChanceBp();
            hlsCusHlsCreditLineChanceBp.setChanceId(businessKey);
            List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps = hlsCusHlsCreditLineChanceBpMapper.selectByForeignKey(hlsCusHlsCreditLineChanceBp);
            HlsCreditPlan hlsCreditPlan=new HlsCreditPlan();
            hlsCreditPlan.setChanceId(businessKey);
            List<HlsCreditPlan> hlsCreditPlans = hlsCreditPlanMapper.queryCreditPlanInfo(hlsCreditPlan);

            JSONObject content2=new JSONObject(new LinkedHashMap());
            if(hlsCusHlsCreditLineChance.getBusinessTypeN()==null&&hlsCusHlsCreditLineChance.getIndustryN()==null){
                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","项目名称");
                line1.put("fieldName","creditLineName");
                line1.put("fieldType","Text");
                line1.put("fieldValue",hlsCusHlsCreditLineChance.getCreditLineName());
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","项目编号");
                line2.put("fieldName","creditLineNumber");
                line2.put("fieldType","Text");
                line2.put("fieldValue",hlsCusHlsCreditLineChance.getCreditLineNumber());
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","项目主办");
                line3.put("fieldName","proposerEmployeeIdN");
                line3.put("fieldType","Text");
                line3.put("fieldValue",hlsCusHlsCreditLineChance.getProposerEmployeeIdN());
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","项目协办");
                line4.put("fieldName","projectAssistantN");
                line4.put("fieldType","Text");
                line4.put("fieldValue",hlsCusHlsCreditLineChance.getProjectAssistantN());
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","申请日期");
                line5.put("fieldName","touchCreatedDate");
                line5.put("fieldType","Text");
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                //String touchCreateDate = sdf.format(hlsCusHlsCreditLineChance.getTouchCreatedDate());
                line5.put("fieldValue",hlsCusHlsCreditLineChance.getTouchCreatedDate());
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","项目所在地");
                line6.put("fieldName","cityN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",hlsCusHlsCreditLineChance.getCityN());
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","资金用途");
                line7.put("fieldName","financeNote");
                line7.put("fieldType","Text");
                line7.put("fieldValue",hlsCusHlsCreditLineChance.getFinanceNote());
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","增信措施");
                line8.put("fieldName","creditMeasures");
                line8.put("fieldType","Text");
                line8.put("fieldValue",hlsCusHlsCreditLineChance.getCreditMeasures());
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);

                content1.put("lines",lines);
                content.add(content1);

                //授信方案
                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","授信方案");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();

                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","授信金额");
                line21.put("fieldName","creditAmt");
                line21.put("fieldType","Text");
                line21.put("fieldValue",hlsCreditPlans.get(0).getCreditAmt());
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","额度类型");
                line22.put("fieldName","quotaTypeN");
                line22.put("fieldType","Text");
                line22.put("fieldValue",hlsCreditPlans.get(0).getQuotaTypeN());
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","是否集团授信");
                line23.put("fieldName","conglomerateFlagN");
                line23.put("fieldType","Text");
                line23.put("fieldValue",hlsCreditPlans.get(0).getConglomerateFlagN());
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","所属集团");
                line24.put("fieldName","belongConglomerate");
                line24.put("fieldType","Text");
                line24.put("fieldValue",hlsCreditPlans.get(0).getBelongConglomerate());
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                line25.put("fieldDescription","授信开始日期");
                line25.put("fieldName","dateFrom");
                line25.put("fieldType","Text");
                String dateForm = sdf.format(hlsCreditPlans.get(0).getDateFrom());
                line25.put("fieldValue",dateForm);
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                JSONObject line26=new JSONObject(new LinkedHashMap());
                line26.put("fieldDescription","授信结束日期");
                line26.put("fieldName","dateTo");
                line26.put("fieldType","Text");
                String dateTo = sdf.format(hlsCreditPlans.get(0).getDateTo());
                line26.put("fieldValue",dateTo);
                line26.put("orderSeq",6);
                line26.put("isEdit",false);
                line26.put("isShow",true);
                line26.put("nullableFlag",true);

                JSONObject line27=new JSONObject(new LinkedHashMap());
                line27.put("fieldDescription","IRR不低于(%)");
                line27.put("fieldName","irr");
                line27.put("fieldType","Text");
                line27.put("fieldValue",hlsCreditPlans.get(0).getIrr()==null?0:new BigDecimal(String.valueOf(hlsCreditPlans.get(0).getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line27.put("orderSeq",7);
                line27.put("isEdit",false);
                line27.put("isShow",true);
                line27.put("nullableFlag",true);

                JSONObject line28=new JSONObject(new LinkedHashMap());
                line28.put("fieldDescription","融资期限不超过(年)");
                line28.put("fieldName","financingDate");
                line28.put("fieldType","Text");
                line28.put("fieldValue",hlsCreditPlans.get(0).getFinancingDate()==null?0:hlsCreditPlans.get(0).getFinancingDate());
                line28.put("orderSeq",8);
                line28.put("isEdit",false);
                line28.put("isShow",true);
                line28.put("nullableFlag",true);

                JSONObject line29=new JSONObject(new LinkedHashMap());
                line29.put("fieldDescription","授信说明");
                line29.put("fieldName","creditDesc");
                line29.put("fieldType","Text");
                line29.put("fieldValue",hlsCreditPlans.get(0).getCreditDesc());
                line29.put("orderSeq",9);
                line29.put("isEdit",false);
                line29.put("isShow",true);
                line29.put("nullableFlag",true);

                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);
                lines1.add(line24);
                lines1.add(line25);
                lines1.add(line26);
                lines1.add(line27);
                lines1.add(line28);
                lines1.add(line29);

                content2.put("lines",lines1);
                content.add(content2);
            }else{
                JSONObject line1=new JSONObject(new LinkedHashMap());
                line1.put("fieldDescription","项目名称");
                line1.put("fieldName","creditLineName");
                line1.put("fieldType","Text");
                line1.put("fieldValue",hlsCusHlsCreditLineChance.getCreditLineName());
                line1.put("orderSeq",1);
                line1.put("isEdit",false);
                line1.put("isShow",true);
                line1.put("nullableFlag",true);

                JSONObject line2=new JSONObject(new LinkedHashMap());
                line2.put("fieldDescription","项目编号");
                line2.put("fieldName","creditLineNumber");
                line2.put("fieldType","Text");
                line2.put("fieldValue",hlsCusHlsCreditLineChance.getCreditLineNumber());
                line2.put("orderSeq",2);
                line2.put("isEdit",false);
                line2.put("isShow",true);
                line2.put("nullableFlag",true);

                JSONObject line3=new JSONObject(new LinkedHashMap());
                line3.put("fieldDescription","承租人");
                line3.put("fieldName","bpIdN");
                line3.put("fieldType","Text");
                StringBuilder personA=new StringBuilder();
                StringBuilder personB=new StringBuilder();
                for (HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp : hlsCusHlsCreditLineChanceBps) {
                    if(cusHlsCreditLineChanceBp.getBpType().equals("TENANT")){
                        personA.append(cusHlsCreditLineChanceBp.getBpIdN());
                        personA.append(",");
                    }else if(cusHlsCreditLineChanceBp.getBpType().equals("WARRANTOR")){
                        personB.append(cusHlsCreditLineChanceBp.getBpIdN());
                        personB.append(",");
                    }
                }
                String lessee;
                if(personA!=null&&!"".equals(personA.toString())){
                    lessee = personA.substring(0, personA.length() - 1);
                }else{
                    lessee="";
                }
                line3.put("fieldValue",lessee);
                line3.put("orderSeq",3);
                line3.put("isEdit",false);
                line3.put("isShow",true);
                line3.put("nullableFlag",true);

                JSONObject line4=new JSONObject(new LinkedHashMap());
                line4.put("fieldDescription","担保人");
                line4.put("fieldName","bpIdN");
                line4.put("fieldType","Text");
                String guarantor;

                if(personB!=null&&!"".equals(personB.toString())){
                    guarantor= personB.substring(0, personB.length() - 1);
                }else{
                    guarantor="";
                }
                line4.put("fieldValue",guarantor);
                line4.put("orderSeq",4);
                line4.put("isEdit",false);
                line4.put("isShow",true);
                line4.put("nullableFlag",true);

                JSONObject line5=new JSONObject(new LinkedHashMap());
                line5.put("fieldDescription","业务类型");
                line5.put("fieldName","businessTypeN");
                line5.put("fieldType","Text");
                line5.put("fieldValue",hlsCusHlsCreditLineChance.getBusinessTypeN());
                line5.put("orderSeq",5);
                line5.put("isEdit",false);
                line5.put("isShow",true);
                line5.put("nullableFlag",true);

                JSONObject line6=new JSONObject(new LinkedHashMap());
                line6.put("fieldDescription","业务版块");
                line6.put("fieldName","industryN");
                line6.put("fieldType","Text");
                line6.put("fieldValue",hlsCusHlsCreditLineChance.getIndustryN());
                line6.put("orderSeq",6);
                line6.put("isEdit",false);
                line6.put("isShow",true);
                line6.put("nullableFlag",true);

                JSONObject line7=new JSONObject(new LinkedHashMap());
                line7.put("fieldDescription","申请日期");
                line7.put("fieldName","touchCreatedDate");
                line7.put("fieldType","Text");
                //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                //String touchCreateDate = sdf.format(hlsCusHlsCreditLineChance.getTouchCreatedDate());
                line7.put("fieldValue",hlsCusHlsCreditLineChance.getTouchCreatedDate());
                line7.put("orderSeq",7);
                line7.put("isEdit",false);
                line7.put("isShow",true);
                line7.put("nullableFlag",true);

                JSONObject line8=new JSONObject(new LinkedHashMap());
                line8.put("fieldDescription","行业分类");
                line8.put("fieldName","industryTypeN");
                line8.put("fieldType","Text");
                line8.put("fieldValue",hlsCusHlsCreditLineChance.getIndustryTypeN());
                line8.put("orderSeq",8);
                line8.put("isEdit",false);
                line8.put("isShow",true);
                line8.put("nullableFlag",true);


                JSONObject line9=new JSONObject(new LinkedHashMap());
                line9.put("fieldDescription","资金用途");
                line9.put("fieldName","financeNote");
                line9.put("fieldType","Text");
                line9.put("fieldValue",hlsCusHlsCreditLineChance.getFinanceNote());
                line9.put("orderSeq",9);
                line9.put("isEdit",false);
                line9.put("isShow",true);
                line9.put("nullableFlag",true);

                JSONObject line10=new JSONObject(new LinkedHashMap());
                line10.put("fieldDescription","增信措施");
                line10.put("fieldName","creditMeasures");
                line10.put("fieldType","Text");
                line10.put("fieldValue",hlsCusHlsCreditLineChance.getCreditMeasures());
                line10.put("orderSeq",10);
                line10.put("isEdit",false);
                line10.put("isShow",true);
                line10.put("nullableFlag",true);

                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                lines.add(line5);
                lines.add(line6);
                lines.add(line7);
                lines.add(line8);
                lines.add(line9);
                lines.add(line10);
                content1.put("lines",lines);
                content.add(content1);

                //对外报送方案

                JSONObject header2=new JSONObject(new LinkedHashMap());
                header2.put("name","对外报送方案");
                header2.put("icon","profile");
                header2.put("orderSeq",2);
                header2.put("hasChildren",false);
                header2.put("isShow",true);
                content2.put("header",header2);
                JSONArray lines1=new JSONArray();
                HlsCusPrjQuotation hlsCusPrjQuotation=new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
                hlsCusPrjQuotation.setSourceDocumentId(businessKey);
                List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(hlsCusPrjQuotation);
                HlsCusPrjQuotation hlsCusPrjQuotation1 = hlsCusPrjQuotations.get(0);
                JSONObject line21=new JSONObject(new LinkedHashMap());
                line21.put("fieldDescription","项目金额(元)");
                line21.put("fieldName","financeAmount");
                line21.put("fieldType","Text");
                line21.put("fieldValue",hlsCusPrjQuotation1.getFinanceAmount());
                line21.put("orderSeq",1);
                line21.put("isEdit",false);
                line21.put("isShow",true);
                line21.put("nullableFlag",true);

                JSONObject line22=new JSONObject(new LinkedHashMap());
                line22.put("fieldDescription","租金支付方式");
                line22.put("fieldName","rentingMethodN");
                line22.put("fieldType","Text");
                line22.put("fieldValue",hlsCusPrjQuotation1.getRentingMethodN());
                line22.put("orderSeq",2);
                line22.put("isEdit",false);
                line22.put("isShow",true);
                line22.put("nullableFlag",true);

                JSONObject line23=new JSONObject(new LinkedHashMap());
                line23.put("fieldDescription","预期收益IRR(%)(不低于)");
                line23.put("fieldName","irr");
                line23.put("fieldType","Text");

                line23.put("fieldValue",hlsCusPrjQuotation1.getIrr()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getIrr()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line23.put("orderSeq",3);
                line23.put("isEdit",false);
                line23.put("isShow",true);
                line23.put("nullableFlag",true);

                JSONObject line24=new JSONObject(new LinkedHashMap());
                line24.put("fieldDescription","手续费率(%)(以首期租金方式收取)");
                line24.put("fieldName","leaseChargeRatio");
                line24.put("fieldType","Text");
                line24.put("fieldValue",hlsCusPrjQuotation1.getLeaseChargeRatio()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getLeaseChargeRatio()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line24.put("orderSeq",4);
                line24.put("isEdit",false);
                line24.put("isShow",true);
                line24.put("nullableFlag",true);

                JSONObject line25=new JSONObject(new LinkedHashMap());
                line25.put("fieldDescription","保证金(%)");
                line25.put("fieldName","depositRatio");
                line25.put("fieldType","Text");
                line25.put("fieldValue",hlsCusPrjQuotation1.getDepositRatio()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getDepositRatio()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line25.put("orderSeq",5);
                line25.put("isEdit",false);
                line25.put("isShow",true);
                line25.put("nullableFlag",true);

                JSONObject line26=new JSONObject(new LinkedHashMap());
                line26.put("fieldDescription","首付款(%)(直租时填写)");
                line26.put("fieldName","downPaymentRatio");
                line26.put("fieldType","Text");
                line26.put("fieldValue",hlsCusPrjQuotation1.getDownPaymentRatio()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getDownPaymentRatio()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line26.put("orderSeq",6);
                line26.put("isEdit",false);
                line26.put("isShow",true);
                line26.put("nullableFlag",true);

                JSONObject line27=new JSONObject(new LinkedHashMap());
                line27.put("fieldDescription","融资利率(%)");
                line27.put("fieldName","intRate");
                line27.put("fieldType","Text");
                line27.put("fieldValue",hlsCusPrjQuotation1.getIntRate()==null?0:new BigDecimal(String.valueOf(hlsCusPrjQuotation1.getIntRate()*100)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue()+"%");
                line27.put("orderSeq",7);
                line27.put("isEdit",false);
                line27.put("isShow",true);
                line27.put("nullableFlag",true);

                JSONObject line28=new JSONObject(new LinkedHashMap());
                line28.put("fieldDescription","融资期限(月)");
                line28.put("fieldName","leaseTermM");
                line28.put("fieldType","Text");
                line28.put("fieldValue",hlsCusPrjQuotation1.getLeaseTermM());
                line28.put("orderSeq",8);
                line28.put("isEdit",false);
                line28.put("isShow",true);
                line28.put("nullableFlag",true);

                JSONObject line29=new JSONObject(new LinkedHashMap());
                line29.put("fieldDescription","留购价款(元)");
                line29.put("fieldName","residualValue");
                line29.put("fieldType","Text");
                line29.put("fieldValue",hlsCusPrjQuotation1.getResidualValue());
                line29.put("orderSeq",9);
                line29.put("isEdit",false);
                line29.put("isShow",false);
                line29.put("nullableFlag",true);

                JSONObject line210=new JSONObject(new LinkedHashMap());
                line210.put("fieldDescription","备注");
                line210.put("fieldName","description");
                line210.put("fieldType","Text");
                line210.put("fieldValue",StringUtils.trimToEmpty(hlsCusPrjQuotation1.getDescription()));
                line210.put("orderSeq",10);
                line210.put("isEdit",false);
                line210.put("isShow",true);
                line210.put("nullableFlag",true);

                lines1.add(line21);
                lines1.add(line22);
                lines1.add(line23);
                lines1.add(line24);
                lines1.add(line25);
                lines1.add(line26);
                lines1.add(line27);
                lines1.add(line28);
                lines1.add(line29);
                lines1.add(line210);

                content2.put("lines",lines1);
                content.add(content2);
            }
            //客户信息
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            header3.put("name","客户信息");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",true);
            header3.put("isShow",true);
            content3.put("header",header3);
            JSONArray lines2=new JSONArray();

            for (HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp : hlsCusHlsCreditLineChanceBps) {
                JSONArray lines21=new JSONArray();

                JSONObject line31=new JSONObject(new LinkedHashMap());
                line31.put("fieldDescription","客户名称");
                line31.put("fieldName","bpIdN");
                line31.put("fieldType","Text");
                line31.put("fieldValue",cusHlsCreditLineChanceBp.getBpIdN());
                line31.put("orderSeq",1);
                line31.put("isEdit",false);
                line31.put("isShow",true);
                line31.put("nullableFlag",true);

                JSONObject line32=new JSONObject(new LinkedHashMap());
                line32.put("fieldDescription","客户类型");
                line32.put("fieldName","bpTypeN");
                line32.put("fieldType","Text");
                line32.put("fieldValue",cusHlsCreditLineChanceBp.getBpTypeN());
                line32.put("orderSeq",2);
                line32.put("isEdit",false);
                line32.put("isShow",true);
                line32.put("nullableFlag",true);

                JSONObject line33=new JSONObject(new LinkedHashMap());
                line33.put("fieldDescription","行业分类");
                line33.put("fieldName","economicInduClassifyN");
                line33.put("fieldType","Text");
                line33.put("fieldValue",StringUtils.trimToEmpty(cusHlsCreditLineChanceBp.getEconomicInduClassifyN()));
                line33.put("orderSeq",3);
                line33.put("isEdit",false);
                line33.put("isShow",true);
                line33.put("nullableFlag",true);

                lines21.add(line31);
                lines21.add(line32);
                lines21.add(line33);

                lines2.add(lines21);
            }
            if (hlsCusHlsCreditLineChanceBps.size()<=0){
                lines2.add(new JSONArray());
            }
            content3.put("lines",lines2);
            content.add(content3);

            //附件资料
            JSONArray lines3=new JSONArray();
            HlsCreditLineAttach hlsCreditLineAttach=new HlsCreditLineAttach();
            hlsCreditLineAttach.setChanceId(businessKey);
            List<HlsCreditLineAttach> hlsCreditLineAttaches = hlsCreditLineAttachMapper.queryCredAttachment(hlsCreditLineAttach);
            for (HlsCreditLineAttach creditLineAttach : hlsCreditLineAttaches) {
                if(StringUtils.isNotBlank(creditLineAttach.getFileName())){
                    String[] attachmentIds = creditLineAttach.getAttachmentId().split(",");
                    String[] fileNames = creditLineAttach.getFileName().split(",");
                    // 取小值，防止异常数据
                    int len = Math.min(attachmentIds.length,fileNames.length);
                    for (int i = 0; i < len; i++) {
                        FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[i]);

                        JSONArray lines33=new JSONArray();
                        JSONObject line317=new JSONObject(new LinkedHashMap());
                        line317.put("fieldDescription","文件名称");
                        line317.put("fieldName","documentName");
                        line317.put("fieldType","Text");
                        line317.put("fieldValue",creditLineAttach.getDocumentName());
                        line317.put("orderSeq",1);
                        line317.put("isEdit",false);
                        line317.put("isShow",true);
                        line317.put("nullableFlag",true);
                        lines33.add(line317);

                        JSONObject line318=new JSONObject(new LinkedHashMap());
                        line318.put("fieldDescription","附件名称");
                        line318.put("fieldName","fileName");
                        line318.put("fieldType","Text");
                        line318.put("fieldValue",fndAttachment.getFileName());
                        line318.put("orderSeq",2);
                        line318.put("isEdit",false);
                        line318.put("isShow",true);
                        line318.put("nullableFlag",true);
                        lines33.add(line318);

                        JSONObject line319=new JSONObject(new LinkedHashMap());
                        line319.put("fieldDescription","附件");
                        line319.put("fieldName","relatedFile");
                        line319.put("fieldType","Link");
                        line319.put("fieldValue", systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,fndAttachment.getAttachmentId().toString()));
                        line319.put("orderSeq",3);
                        line319.put("isEdit",false);
                        line319.put("isShow",true);
                        line319.put("nullableFlag",true);
                        lines33.add(line319);

                        lines3.add(lines33);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(lines3)){
                JSONObject content4=new JSONObject(new LinkedHashMap());
                JSONObject header4=new JSONObject(new LinkedHashMap());
                header4.put("name","附件资料");
                header4.put("icon","profile");
                header4.put("orderSeq",4);
                header4.put("hasChildren",true);
                header4.put("isShow",true);
                content4.put("header",header4);
                content4.put("lines",lines3);
                content.add(content4);
            }


            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","approve");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines4=new JSONArray();

            JSONObject line51=new JSONObject(new LinkedHashMap());
            line51.put("fieldDescription","退回");
            line51.put("fieldName","sendback");
            line51.put("fieldType","Text");
            line51.put("fieldValue","SENDBACK");
            line51.put("orderSeq",1);
            line51.put("isEdit",false);
            line51.put("isShow",true);
            line51.put("nullableFlag",false);

            lines4.add(line51);
            content5.put("lines",lines4);
            content.add(content5);

            return content;
        }catch (Exception e){
            logger.error("----------项目立项移动端审批填充content数据失败-----------");
            e.printStackTrace();
            return null;
        }
    }

    //租后检查移动端审批数据获取
    private JSONArray hlsPrjCheckProcess(){
        try{
            JSONArray content=new JSONArray();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            PrjCheck prjCheck=new PrjCheck();
            prjCheck.setCheckId(businessKey);
            List<PrjCheck> prjChecks = prjCheckMapper.queryList(prjCheck);
            if(CollectionUtils.isEmpty(prjChecks)){
                logger.error("租后检查流程发起失败！检查id:{}",businessKey);
                return content;
            }
            prjCheck = prjChecks.get(0);


            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","参与部门及调查人员");
            line1.put("fieldName","checkUser");
            line1.put("fieldType","Text");
            line1.put("fieldValue",prjCheck.getCheckUser());
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","项目开始时间");
            line2.put("fieldName","leaseStartDate");
            line2.put("fieldType","Text");
            line2.put("fieldValue",prjCheck.getLeaseStartDate()==null?"":sdf.format(prjCheck.getLeaseStartDate()));
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","项目结束时间");
            line3.put("fieldName","leaseEndDate");
            line3.put("fieldType","Text");
            line3.put("fieldValue",prjCheck.getLeaseEndDate()==null?"":sdf.format(prjCheck.getLeaseEndDate()));
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line8=new JSONObject(new LinkedHashMap());
            line8.put("fieldDescription","承租人名称");
            line8.put("fieldName","bpName");
            line8.put("fieldType","Text");
            line8.put("fieldValue",prjCheck.getBpName());
            line8.put("orderSeq",4);
            line8.put("isEdit",false);
            line8.put("isShow",true);
            line8.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","承租人投放总额");
            line4.put("fieldName","financeAmountMerge");
            line4.put("fieldType","Text");
            line4.put("fieldValue",formatNumber(prjCheck.getFinanceAmountMerge()));
            line4.put("orderSeq",5);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","承租人保证金总额");
            line5.put("fieldName","depositMerge");
            line5.put("fieldType","Text");
            line5.put("fieldValue",formatNumber(prjCheck.getDepositMerge()));
            line5.put("orderSeq",6);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","承租人剩余敞口（元）");
            line6.put("fieldName","riskExposure");
            line6.put("fieldType","Text");
            line6.put("fieldValue",formatNumber(prjCheck.getRiskExposure()));
            line6.put("orderSeq",7);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","承租人首付款总额");
            line7.put("fieldName","firstRental");
            line7.put("fieldType","Text");
            line7.put("fieldValue",formatNumber(prjCheck.getFirstRental()));
            line7.put("orderSeq",8);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);
            lines.add(line8);

            content1.put("lines",lines);
            content.add(content1);

            //上次检查情况
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","上次检查情况");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines2=new JSONArray();

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","上次检查时间");
            line21.put("fieldName","lastCheckDate");
            line21.put("fieldType","Text");
            line21.put("fieldValue",prjCheck.getLastCheckDate()==null?"":sdf.format(prjCheck.getLastCheckDate()));
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","上次检查方式");
            line22.put("fieldName","lastCheckType");
            line22.put("fieldType","Text");
            line22.put("fieldValue",prjCheck.getLastCheckType());
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","上次检查结果");
            line23.put("fieldName","lastFiveResult");
            line23.put("fieldType","Text");
            line23.put("fieldValue",prjCheck.getLastFiveResultN());
            line23.put("orderSeq",3);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            lines2.add(line21);
            lines2.add(line22);
            lines2.add(line23);

            content2.put("lines",lines2);
            content.add(content2);

            //本次检查情况
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            JSONArray lines3=new JSONArray();
            header3.put("name","本次检查情况");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",false);
            header3.put("isShow",true);
            content3.put("header",header3);

            JSONObject line31=new JSONObject(new LinkedHashMap());
            line31.put("fieldDescription","本次检查时间");
            line31.put("fieldName","checkDate");
            line31.put("fieldType","Text");
            line31.put("fieldValue",sdf.format(prjCheck.getCheckDate()));
            line31.put("orderSeq",1);
            line31.put("isEdit",false);
            line31.put("isShow",true);
            line31.put("nullableFlag",true);

            JSONObject line32=new JSONObject(new LinkedHashMap());
            line32.put("fieldDescription","检查类型");
            line32.put("fieldName","checkType");
            line32.put("fieldType","Text");
            line32.put("fieldValue",prjCheck.getCheckTypeN());
            line32.put("orderSeq",2);
            line32.put("isEdit",false);
            line32.put("isShow",true);
            line32.put("nullableFlag",true);

            JSONObject line33=new JSONObject(new LinkedHashMap());
            line33.put("fieldDescription","五级分类结果");
            line33.put("fieldName","fiveResult");
            line33.put("fieldType","Text");
            line33.put("fieldValue",prjCheck.getFiveClassificationResultN());
            line33.put("orderSeq",3);
            line33.put("isEdit",false);
            line33.put("isShow",true);
            line33.put("nullableFlag",true);

            lines3.add(line31);
            lines3.add(line32);
            lines3.add(line33);

            content3.put("lines",lines3);
            content.add(content3);

            // 附件资料
            List<RiskAttachment> riskAttachments = riskAttachmentMapper.queryPrjCheckAttachment(prjCheck);
            if(CollectionUtils.isNotEmpty(riskAttachments)) {
                //附件资料
                JSONObject content4 = new JSONObject(new LinkedHashMap());
                JSONObject header4 = new JSONObject(new LinkedHashMap());
                header4.put("name", "附件信息");
                header4.put("icon", "profile");
                header4.put("orderSeq", 4);
                header4.put("hasChildren", true);
                header4.put("isShow", true);
                content4.put("header", header4);
                JSONArray lines4 = new JSONArray();
                for (RiskAttachment riskAttachment : riskAttachments) {
                    if (StringUtils.isNotEmpty(riskAttachment.getFileName())) {
                        String[] attachmentIds = riskAttachment.getAttachmentId().split(",");
                        String[] fileNames = riskAttachment.getFileName().split(",");
                        // 取小值，防止异常数据
                        int len = Math.min(attachmentIds.length,fileNames.length);
                        for (int i = 0; i < len; i++) {
                            FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[i]);

                            JSONArray lines33=new JSONArray();
                            JSONObject line317=new JSONObject(new LinkedHashMap());
                            line317.put("fieldDescription","附件名称");
                            line317.put("fieldName","documentName");
                            line317.put("fieldType","Text");
                            line317.put("fieldValue",riskAttachment.getDocumentName());
                            line317.put("orderSeq",1);
                            line317.put("isEdit",false);
                            line317.put("isShow",true);
                            line317.put("nullableFlag",true);
                            lines33.add(line317);

                            JSONObject line318=new JSONObject(new LinkedHashMap());
                            line318.put("fieldDescription","文件名称");
                            line318.put("fieldName","fileName");
                            line318.put("fieldType","Text");
                            line318.put("fieldValue",fndAttachment.getFileName());
                            line318.put("orderSeq",2);
                            line318.put("isEdit",false);
                            line318.put("isShow",true);
                            line318.put("nullableFlag",true);
                            lines33.add(line318);

                            JSONObject line319=new JSONObject(new LinkedHashMap());
                            line319.put("fieldDescription","附件");
                            line319.put("fieldName","relatedFile");
                            line319.put("fieldType","Link");
                            line319.put("fieldValue", systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,fndAttachment.getAttachmentId().toString()));
                            line319.put("orderSeq",3);
                            line319.put("isEdit",false);
                            line319.put("isShow",true);
                            line319.put("nullableFlag",true);
                            lines33.add(line319);

                            lines4.add(lines33);
                        }
                    }
                }

                    if(CollectionUtils.isNotEmpty(lines4)){
                    content4.put("lines",lines4);
                    content.add(content4);
                }
            }


            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","approve");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines5=new JSONArray();

            JSONObject line51=new JSONObject(new LinkedHashMap());
            line51.put("fieldDescription","退回");
            line51.put("fieldName","sendback");
            line51.put("fieldType","Text");
            line51.put("fieldValue","SENDBACK");
            line51.put("orderSeq",1);
            line51.put("isEdit",false);
            line51.put("isShow",true);
            line51.put("nullableFlag",false);

            lines5.add(line51);
            content5.put("lines",lines5);
            content.add(content5);
            return content;
        }catch (Exception e){
            logger.error("----------租后检查流程填充content数据失败-----------");
            e.printStackTrace();
            return new JSONArray();
        }
    }

    //零售租后检查移动端审批数据获取
    public JSONArray hlsRetailPrjCheckProcess(){
        try{
            JSONArray content=new JSONArray();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            PrjCheck prjCheck=new PrjCheck();
            prjCheck.setCheckId(businessKey);
            List<PrjCheck> prjChecks = prjCheckMapper.queryRetailCheck(prjCheck);
            if(CollectionUtils.isEmpty(prjChecks)){
                logger.error("零售业务租后检查流程发起失败！检查id:{}",businessKey);
                return content;
            }
            prjCheck = prjChecks.get(0);


            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","参与部门及调查人员");
            line1.put("fieldName","checkUser");
            line1.put("fieldType","Text");
            line1.put("fieldValue",prjCheck.getCheckUser());
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);


            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","合作方名称");
            line2.put("fieldName","manufacturerIdN");
            line2.put("fieldType","Text");
            line2.put("fieldValue",prjCheck.getManufacturerIdN());
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","年度");
            line3.put("fieldName","yearN");
            line3.put("fieldType","Text");
            line3.put("fieldValue",prjCheck.getYearN());
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","合同总数");
            line4.put("fieldName","contractQuantity");
            line4.put("fieldType","Text");
            line4.put("fieldValue",prjCheck.getContractQuantity());
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","正常数量");
            line5.put("fieldName","normalQuantity");
            line5.put("fieldType","Text");
            line5.put("fieldValue",prjCheck.getNormalQuantity());
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","正常剩余租金");
            line6.put("fieldName","normalSurplusRental");
            line6.put("fieldType","Text");
            line6.put("fieldValue",prjCheck.getNormalSurplusRental());
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","关注数量");
            line7.put("fieldName","specialMentionQuantity");
            line7.put("fieldType","Text");
            line7.put("fieldValue",prjCheck.getSpecialMentionQuantity());
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            JSONObject line8=new JSONObject(new LinkedHashMap());
            line8.put("fieldDescription","关注剩余租金");
            line8.put("fieldName","specialMentionQuantity");
            line8.put("fieldType","Text");
            line8.put("fieldValue",prjCheck.getSpecialMentionQuantity());
            line8.put("orderSeq",8);
            line8.put("isEdit",false);
            line8.put("isShow",true);
            line8.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);
            lines.add(line8);

            content1.put("lines",lines);
            content.add(content1);

            //上次检查情况
            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","上次检查情况");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines2=new JSONArray();

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","上次检查时间");
            line21.put("fieldName","lastCheckDate");
            line21.put("fieldType","Text");
            line21.put("fieldValue",prjCheck.getLastCheckDate()==null?"":sdf.format(prjCheck.getLastCheckDate()));
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","上次检查方式");
            line22.put("fieldName","lastCheckType");
            line22.put("fieldType","Text");
            line22.put("fieldValue",prjCheck.getLastCheckType());
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","上次检查结果");
            line23.put("fieldName","lastFiveResult");
            line23.put("fieldType","Text");
            line23.put("fieldValue",prjCheck.getLastFiveResultN());
            line23.put("orderSeq",3);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            lines2.add(line21);
            lines2.add(line22);
            lines2.add(line23);

            content2.put("lines",lines2);
            content.add(content2);

            //本次检查情况
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            JSONArray lines3=new JSONArray();
            header3.put("name","本次检查情况");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",false);
            header3.put("isShow",true);
            content3.put("header",header3);

            JSONObject line31=new JSONObject(new LinkedHashMap());
            line31.put("fieldDescription","本次检查时间");
            line31.put("fieldName","checkDate");
            line31.put("fieldType","Text");
            line31.put("fieldValue",sdf.format(prjCheck.getCheckDate()));
            line31.put("orderSeq",1);
            line31.put("isEdit",false);
            line31.put("isShow",true);
            line31.put("nullableFlag",true);

            JSONObject line32=new JSONObject(new LinkedHashMap());
            line32.put("fieldDescription","检查类型");
            line32.put("fieldName","checkType");
            line32.put("fieldType","Text");
            line32.put("fieldValue",prjCheck.getCheckTypeN());
            line32.put("orderSeq",2);
            line32.put("isEdit",false);
            line32.put("isShow",true);
            line32.put("nullableFlag",true);

            JSONObject line33=new JSONObject(new LinkedHashMap());
            line33.put("fieldDescription","五级分类结果");
            line33.put("fieldName","fiveResult");
            line33.put("fieldType","Text");
            line33.put("fieldValue",prjCheck.getFiveClassificationResultN());
            line33.put("orderSeq",3);
            line33.put("isEdit",false);
            line33.put("isShow",true);
            line33.put("nullableFlag",true);

            lines3.add(line31);
            lines3.add(line32);
            lines3.add(line33);

            content3.put("lines",lines3);
            content.add(content3);

            // 附件资料
            List<RiskAttachment> riskAttachments = riskAttachmentMapper.queryPrjCheckAttachment(prjCheck);
            if(CollectionUtils.isNotEmpty(riskAttachments)) {
                //附件资料
                JSONObject content4 = new JSONObject(new LinkedHashMap());
                JSONObject header4 = new JSONObject(new LinkedHashMap());
                header4.put("name", "附件信息");
                header4.put("icon", "profile");
                header4.put("orderSeq", 4);
                header4.put("hasChildren", true);
                header4.put("isShow", true);
                content4.put("header", header4);
                JSONArray lines4 = new JSONArray();
                for (RiskAttachment riskAttachment : riskAttachments) {
                    if (StringUtils.isNotEmpty(riskAttachment.getFileName())) {
                        String[] attachmentIds = riskAttachment.getAttachmentId().split(",");
                        String[] fileNames = riskAttachment.getFileName().split(",");
                        // 取小值，防止异常数据
                        int len = Math.min(attachmentIds.length,fileNames.length);
                        for (int i = 0; i < len; i++) {
                            FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[i]);

                            JSONArray lines33=new JSONArray();
                            JSONObject line317=new JSONObject(new LinkedHashMap());
                            line317.put("fieldDescription","附件名称");
                            line317.put("fieldName","documentName");
                            line317.put("fieldType","Text");
                            line317.put("fieldValue",riskAttachment.getDocumentName());
                            line317.put("orderSeq",1);
                            line317.put("isEdit",false);
                            line317.put("isShow",true);
                            line317.put("nullableFlag",true);
                            lines33.add(line317);

                            JSONObject line318=new JSONObject(new LinkedHashMap());
                            line318.put("fieldDescription","文件名称");
                            line318.put("fieldName","fileName");
                            line318.put("fieldType","Text");
                            line318.put("fieldValue",fndAttachment.getFileName());
                            line318.put("orderSeq",2);
                            line318.put("isEdit",false);
                            line318.put("isShow",true);
                            line318.put("nullableFlag",true);
                            lines33.add(line318);

                            JSONObject line319=new JSONObject(new LinkedHashMap());
                            line319.put("fieldDescription","附件");
                            line319.put("fieldName","relatedFile");
                            line319.put("fieldType","Link");
                            line319.put("fieldValue", systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,fndAttachment.getAttachmentId().toString()));
                            line319.put("orderSeq",3);
                            line319.put("isEdit",false);
                            line319.put("isShow",true);
                            line319.put("nullableFlag",true);
                            lines33.add(line319);

                            lines4.add(lines33);
                        }
                    }
                }

                if(CollectionUtils.isNotEmpty(lines4)){
                    content4.put("lines",lines4);
                    content.add(content4);
                }
            }


            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","approve");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines5=new JSONArray();

            JSONObject line51=new JSONObject(new LinkedHashMap());
            line51.put("fieldDescription","退回");
            line51.put("fieldName","sendback");
            line51.put("fieldType","Text");
            line51.put("fieldValue","SENDBACK");
            line51.put("orderSeq",1);
            line51.put("isEdit",false);
            line51.put("isShow",true);
            line51.put("nullableFlag",false);

            lines5.add(line51);
            content5.put("lines",lines5);
            content.add(content5);
            return content;
        }catch (Exception e){
            logger.error("----------零售业务租后检查流程填充content数据失败-----------");
            e.printStackTrace();
            return new JSONArray();
        }
    }

    //投放审查移动端审批数据获取
    public JSONArray hlsSigProjectProcess(){
        try{
            JSONArray content=new JSONArray();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(businessKey);
            Map prjProjectMap = hlsCusPrjProjectMapper.prjRpLModifyQuery(hlsCusPrjProject);
            hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(businessKey);
            if(MapUtils.isEmpty(prjProjectMap)){
                logger.error("投放审查流程发起失败！检查id:{}",businessKey);
                return content;
            }


            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","业务模式");
            line1.put("fieldName","businessType");
            line1.put("fieldType","Text");
            line1.put("fieldValue",prjProjectMap.get("business_type_n"));
            line1.put("orderSeq",1);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","承租人");
            line2.put("fieldName","tenantId");
            line2.put("fieldType","Text");
            line2.put("fieldValue",prjProjectMap.get("tenant_id_n"));
            line2.put("orderSeq",2);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","项目经理");
            line3.put("fieldName","employeeId");
            line3.put("fieldType","Text");
            line3.put("fieldValue",prjProjectMap.get("employee_id_n"));
            line3.put("orderSeq",3);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","经销商");
            line4.put("fieldName","bpIdVender");
            line4.put("fieldType","Text");
            line4.put("fieldValue",prjProjectMap.get("bp_id_vender"));
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","主机厂");
            line5.put("fieldName","factoryId");
            line5.put("fieldType","Text");
            line5.put("fieldValue",prjProjectMap.get("factory_id_n"));
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","合作方");
            line6.put("fieldName","manufacturer");
            line6.put("fieldType","Text");
            line6.put("fieldValue",prjProjectMap.get("manufacturer_id_n"));
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","业务线");
            line7.put("fieldName","division");
            line7.put("fieldType","Text");
            line7.put("fieldValue",prjProjectMap.get("division_n"));
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);

            JSONObject line8=new JSONObject(new LinkedHashMap());
            line8.put("fieldDescription","预计投放日");
            line8.put("fieldName","leaseStartDate");
            line8.put("fieldType","Text");
            line8.put("fieldValue",prjProjectMap.get("lease_start_date").toString().substring(0,10));
            line8.put("orderSeq",8);
            line8.put("isEdit",false);
            line8.put("isShow",true);
            line8.put("nullableFlag",true);

            JSONObject line9=new JSONObject(new LinkedHashMap());
            line9.put("fieldDescription","保险购买情况");
            line9.put("fieldName","insuranceFlag");
            line9.put("fieldType","Text");
            line9.put("fieldValue",prjProjectMap.get("insurance_flag_n"));
            line9.put("orderSeq",9);
            line9.put("isEdit",false);
            line9.put("isShow",true);
            line9.put("nullableFlag",true);

            JSONObject line10=new JSONObject(new LinkedHashMap());
            line10.put("fieldDescription","是否抵押");
            line10.put("fieldName","pledgeFlag");
            line10.put("fieldType","Text");
            line10.put("fieldValue",prjProjectMap.get("pledge_flag_n"));
            line10.put("orderSeq",10);
            line10.put("isEdit",false);
            line10.put("isShow",true);
            line10.put("nullableFlag",true);

            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);
            lines.add(line8);
            lines.add(line9);
            lines.add(line10);

            content1.put("lines",lines);
            content.add(content1);

            //承租人额度信息
            Double creditAmt = 0D;
            Double tenantAmount = 0D;
            List<HlsCusPrjProject> creditAmtProjects = hlsCusPrjProjectMapper.queryCreditAmt(hlsCusPrjProject.getManufacturerId(), hlsCusPrjProject.getLeaseStartDate());
            if(CollectionUtils.isNotEmpty(creditAmtProjects)){
                for(HlsCusPrjProject creditAmtProject:creditAmtProjects){
                    Double usedFinanceAmount = 0D;
                    if("REVOLVING".equals(creditAmtProject.getQuotaType())){
                        usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountRevolving(creditAmtProject.getDefinitionId());
                    }else{
                        usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountNonRevolving(creditAmtProject.getDefinitionId());
                    }
                    creditAmt+=creditAmtProject.getCreditAmt();
                    tenantAmount +=usedFinanceAmount;
                }
            }


            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","承租人额度信息");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines2=new JSONArray();

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","合作方授信总额");
            line21.put("fieldName","creditAmt");
            line21.put("fieldType","Text");
            line21.put("fieldValue",formatNumber(creditAmt));
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","承租人累计已用额度");
            line22.put("fieldName","tenantAmount");
            line22.put("fieldType","Text");
            line22.put("fieldValue",formatNumber(tenantAmount));
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","本次待投放金额");
            line23.put("fieldName","lastFiveResult");
            line23.put("fieldType","Text");
            line23.put("fieldValue",formatNumber(hlsCusPrjProject.getFinanceAmount()));
            line23.put("orderSeq",3);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            lines2.add(line21);
            lines2.add(line22);
            lines2.add(line23);

            content2.put("lines",lines2);
            content.add(content2);

            //报价方案
            Map projectQuotationMap = hlsCusPrjProjectMapper.prjProjectQuotationQuery(hlsCusPrjProject);
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            JSONArray lines3=new JSONArray();
            header3.put("name","报价方案");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",false);
            header3.put("isShow",true);
            content3.put("header",header3);

            JSONObject line31=new JSONObject(new LinkedHashMap());
            line31.put("fieldDescription","产品名称");
            line31.put("fieldName","planId");
            line31.put("fieldType","Text");
            line31.put("fieldValue",projectQuotationMap.get("plan_id"));
            line31.put("orderSeq",1);
            line31.put("isEdit",false);
            line31.put("isShow",true);
            line31.put("nullableFlag",true);

            JSONObject line32=new JSONObject(new LinkedHashMap());
            line32.put("fieldDescription","租赁期限（月）");
            line32.put("fieldName","leaseTerm");
            line32.put("fieldType","Text");
            line32.put("fieldValue",projectQuotationMap.get("lease_term_m"));
            line32.put("orderSeq",2);
            line32.put("isEdit",false);
            line32.put("isShow",true);
            line32.put("nullableFlag",true);

            JSONObject line33=new JSONObject(new LinkedHashMap());
            line33.put("fieldDescription","还租频率");
            line33.put("fieldName","annualPayTimes");
            line33.put("fieldType","Text");
            line33.put("fieldValue",projectQuotationMap.get("annual_pay_times"));
            line33.put("orderSeq",3);
            line33.put("isEdit",false);
            line33.put("isShow",true);
            line33.put("nullableFlag",true);

            JSONObject line34=new JSONObject(new LinkedHashMap());
            line34.put("fieldDescription","首付款比例");
            line34.put("fieldName","downPaymentRatio");
            line34.put("fieldType","Text");
            line34.put("fieldValue",projectQuotationMap.get("down_payment_ratio"));
            line34.put("orderSeq",4);
            line34.put("isEdit",false);
            line34.put("isShow",true);
            line34.put("nullableFlag",true);

            JSONObject line35=new JSONObject(new LinkedHashMap());
            line35.put("fieldDescription","首付款金额");
            line35.put("fieldName","downPayment");
            line35.put("fieldType","Text");
            line35.put("fieldValue",projectQuotationMap.get("down_payment"));
            line35.put("orderSeq",5);
            line35.put("isEdit",false);
            line35.put("isShow",true);
            line35.put("nullableFlag",true);

            JSONObject line36=new JSONObject(new LinkedHashMap());
            line36.put("fieldDescription","保证金比例");
            line36.put("fieldName","depositRatio");
            line36.put("fieldType","Text");
            line36.put("fieldValue",projectQuotationMap.get("deposit_ratio"));
            line36.put("orderSeq",6);
            line36.put("isEdit",false);
            line36.put("isShow",true);
            line36.put("nullableFlag",true);

            JSONObject line37=new JSONObject(new LinkedHashMap());
            line37.put("fieldDescription","保证金");
            line37.put("fieldName","deposit");
            line37.put("fieldType","Text");
            line37.put("fieldValue",projectQuotationMap.get("deposit"));
            line37.put("orderSeq",7);
            line37.put("isEdit",false);
            line37.put("isShow",true);
            line37.put("nullableFlag",true);

            JSONObject line38=new JSONObject(new LinkedHashMap());
            line38.put("fieldDescription","融资额");
            line38.put("fieldName","financeAmount");
            line38.put("fieldType","Text");
            line38.put("fieldValue",projectQuotationMap.get("finance_amount"));
            line38.put("orderSeq",8);
            line38.put("isEdit",false);
            line38.put("isShow",true);
            line38.put("nullableFlag",true);

            JSONObject line39=new JSONObject(new LinkedHashMap());
            line39.put("fieldDescription","年利率");
            line39.put("fieldName","intRate");
            line39.put("fieldType","Text");
            line39.put("fieldValue",projectQuotationMap.get("int_rate"));
            line39.put("orderSeq",9);
            line39.put("isEdit",false);
            line39.put("isShow",true);
            line39.put("nullableFlag",true);

            JSONObject line310=new JSONObject(new LinkedHashMap());
            line310.put("fieldDescription","名义价格");
            line310.put("fieldName","residualValue");
            line310.put("fieldType","Text");
            line310.put("fieldValue",projectQuotationMap.get("residual_value"));
            line310.put("orderSeq",10);
            line310.put("isEdit",false);
            line310.put("isShow",true);
            line310.put("nullableFlag",true);

            JSONObject line311=new JSONObject(new LinkedHashMap());
            line311.put("fieldDescription","宽限期类型");
            line311.put("fieldName","graceType");
            line311.put("fieldType","Text");
            line311.put("fieldValue",projectQuotationMap.get("grace_type_n"));
            line311.put("orderSeq",11);
            line311.put("isEdit",false);
            line311.put("isShow",true);
            line311.put("nullableFlag",true);

            JSONObject line312=new JSONObject(new LinkedHashMap());
            line312.put("fieldDescription","IRR");
            line312.put("fieldName","irr");
            line312.put("fieldType","Text");
            line312.put("fieldValue",projectQuotationMap.get("irr"));
            line312.put("orderSeq",12);
            line312.put("isEdit",false);
            line312.put("isShow",true);
            line312.put("nullableFlag",true);

            lines3.add(line31);
            lines3.add(line32);
            lines3.add(line33);
            lines3.add(line34);
            lines3.add(line35);
            lines3.add(line36);
            lines3.add(line37);
            lines3.add(line38);
            lines3.add(line39);
            lines3.add(line310);
            lines3.add(line311);
            lines3.add(line312);

            content3.put("lines",lines3);
            content.add(content3);

            // 附件资料
            List<HlsCusPrjProjectAttachment> projectAttachments = hlsCusPrjProjectAttachmentMapper.prjProjectAttachQuery(hlsCusPrjProject);
            if(CollectionUtils.isNotEmpty(projectAttachments)) {
                //附件资料
                JSONObject content4 = new JSONObject(new LinkedHashMap());
                JSONObject header4 = new JSONObject(new LinkedHashMap());
                header4.put("name", "附件信息");
                header4.put("icon", "profile");
                header4.put("orderSeq", 4);
                header4.put("hasChildren", true);
                header4.put("isShow", true);
                content4.put("header", header4);
                JSONArray lines4 = new JSONArray();
                for (HlsCusPrjProjectAttachment projectAttachment : projectAttachments) {
                    if (StringUtils.isNotEmpty(projectAttachment.getFileNames())) {
                        String[] attachmentIds = projectAttachment.getFileId().split(",");
                        String[] fileNames = projectAttachment.getFileNames().split(",");
                        // 取小值，防止异常数据
                        int len = Math.min(attachmentIds.length,fileNames.length);
                        for (int i = 0; i < len; i++) {
                            FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[i]);

                            JSONArray lines33=new JSONArray();
                            JSONObject line317=new JSONObject(new LinkedHashMap());
                            line317.put("fieldDescription","附件名称");
                            line317.put("fieldName","documentName");
                            line317.put("fieldType","Text");
                            line317.put("fieldValue",projectAttachment.getDocumentName());
                            line317.put("orderSeq",1);
                            line317.put("isEdit",false);
                            line317.put("isShow",true);
                            line317.put("nullableFlag",true);
                            lines33.add(line317);

                            JSONObject line318=new JSONObject(new LinkedHashMap());
                            line318.put("fieldDescription","文件名称");
                            line318.put("fieldName","fileName");
                            line318.put("fieldType","Text");
                            line318.put("fieldValue",fndAttachment.getFileName());
                            line318.put("orderSeq",2);
                            line318.put("isEdit",false);
                            line318.put("isShow",true);
                            line318.put("nullableFlag",true);
                            lines33.add(line318);

                            JSONObject line319=new JSONObject(new LinkedHashMap());
                            line319.put("fieldDescription","附件");
                            line319.put("fieldName","relatedFile");
                            line319.put("fieldType","Link");
                            line319.put("fieldValue", systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,fndAttachment.getAttachmentId().toString()));
                            line319.put("orderSeq",3);
                            line319.put("isEdit",false);
                            line319.put("isShow",true);
                            line319.put("nullableFlag",true);
                            lines33.add(line319);

                            lines4.add(lines33);
                        }
                    }
                }

                if(CollectionUtils.isNotEmpty(lines4)){
                    content4.put("lines",lines4);
                    content.add(content4);
                }
            }


            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","approve");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines5=new JSONArray();

            JSONObject line51=new JSONObject(new LinkedHashMap());
            line51.put("fieldDescription","退回");
            line51.put("fieldName","sendback");
            line51.put("fieldType","Text");
            line51.put("fieldValue","SENDBACK");
            line51.put("orderSeq",1);
            line51.put("isEdit",false);
            line51.put("isShow",true);
            line51.put("nullableFlag",false);

            lines5.add(line51);
            content5.put("lines",lines5);
            content.add(content5);
            return content;
        }catch (Exception e){
            logger.error("----------投放审查流程填充content数据失败-----------");
            e.printStackTrace();
            return new JSONArray();
        }
    }

    //进件移动端审批数据获取
    public JSONArray retailProjectProcess(){
        try{
            JSONArray content=new JSONArray();
            //基本信息
            JSONObject content1=new JSONObject(new LinkedHashMap());
            JSONObject header1=new JSONObject(new LinkedHashMap());
            header1.put("name","基本信息");
            header1.put("icon","profile");
            header1.put("orderSeq",1);
            header1.put("hasChildren",false);
            header1.put("isShow",true);
            content1.put("header",header1);
            JSONArray lines=new JSONArray();
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(businessKey);
            Map prjProjectMap = hlsCusPrjProjectMapper.prjRpLModifyQuery(hlsCusPrjProject);
            hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(businessKey);
            if(MapUtils.isEmpty(prjProjectMap)){
                logger.error("进件流程发起失败！检查id:{}",businessKey);
                return content;
            }

            JSONObject line1=new JSONObject(new LinkedHashMap());
            line1.put("fieldDescription","业务模式");
            line1.put("fieldName","businessType");
            line1.put("fieldType","Text");
            line1.put("fieldValue",prjProjectMap.get("business_type_n"));
            line1.put("orderSeq",2);
            line1.put("isEdit",false);
            line1.put("isShow",true);
            line1.put("nullableFlag",true);

            JSONObject line2=new JSONObject(new LinkedHashMap());
            line2.put("fieldDescription","承租人名称");
            line2.put("fieldName","tenantId");
            line2.put("fieldType","Text");
            line2.put("fieldValue",prjProjectMap.get("tenant_id_n"));
            line2.put("orderSeq",3);
            line2.put("isEdit",false);
            line2.put("isShow",true);
            line2.put("nullableFlag",true);

            JSONObject line3=new JSONObject(new LinkedHashMap());
            line3.put("fieldDescription","进件编号");
            line3.put("fieldName","projectNumber");
            line3.put("fieldType","Text");
            line3.put("fieldValue",prjProjectMap.get("project_number"));
            line3.put("orderSeq",1);
            line3.put("isEdit",false);
            line3.put("isShow",true);
            line3.put("nullableFlag",true);

            JSONObject line4=new JSONObject(new LinkedHashMap());
            line4.put("fieldDescription","经销商");
            line4.put("fieldName","bpIdVender");
            line4.put("fieldType","Text");
            line4.put("fieldValue",prjProjectMap.get("bp_id_vender"));
            line4.put("orderSeq",4);
            line4.put("isEdit",false);
            line4.put("isShow",true);
            line4.put("nullableFlag",true);

            JSONObject line5=new JSONObject(new LinkedHashMap());
            line5.put("fieldDescription","主机厂");
            line5.put("fieldName","factoryId");
            line5.put("fieldType","Text");
            line5.put("fieldValue",prjProjectMap.get("factory_id_n"));
            line5.put("orderSeq",5);
            line5.put("isEdit",false);
            line5.put("isShow",true);
            line5.put("nullableFlag",true);

            JSONObject line6=new JSONObject(new LinkedHashMap());
            line6.put("fieldDescription","合作方");
            line6.put("fieldName","manufacturer");
            line6.put("fieldType","Text");
            line6.put("fieldValue",prjProjectMap.get("manufacturer_id_n"));
            line6.put("orderSeq",6);
            line6.put("isEdit",false);
            line6.put("isShow",true);
            line6.put("nullableFlag",true);

            JSONObject line7=new JSONObject(new LinkedHashMap());
            line7.put("fieldDescription","设备类型");
            line7.put("fieldName","secondHandFlagN");
            line7.put("fieldType","Text");
            line7.put("fieldValue",prjProjectMap.get("second_hand_flag_n"));
            line7.put("orderSeq",7);
            line7.put("isEdit",false);
            line7.put("isShow",true);
            line7.put("nullableFlag",true);



            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);

            content1.put("lines",lines);
            content.add(content1);

            //承租人额度信息
            Double creditAmt = 0D;
            Double tenantAmount = 0D;
            List<HlsCusPrjProject> creditAmtProjects = hlsCusPrjProjectMapper.queryCreditAmt(hlsCusPrjProject.getManufacturerId(), hlsCusPrjProject.getLeaseStartDate());
            if(CollectionUtils.isNotEmpty(creditAmtProjects)){
                for(HlsCusPrjProject creditAmtProject:creditAmtProjects){
                    Double usedFinanceAmount = 0D;
                    if("REVOLVING".equals(creditAmtProject.getQuotaType())){
                        usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountRevolving(creditAmtProject.getDefinitionId());
                    }else{
                        usedFinanceAmount = hlsCusPrjProjectMapper.queryUsedFinanceAmountNonRevolving(creditAmtProject.getDefinitionId());
                    }
                    creditAmt+=creditAmtProject.getCreditAmt();
                    tenantAmount +=usedFinanceAmount;
                }
            }


            JSONObject content2=new JSONObject(new LinkedHashMap());
            JSONObject header2=new JSONObject(new LinkedHashMap());
            header2.put("name","承租人额度信息");
            header2.put("icon","profile");
            header2.put("orderSeq",2);
            header2.put("hasChildren",false);
            header2.put("isShow",true);
            content2.put("header",header2);
            JSONArray lines2=new JSONArray();

            JSONObject line21=new JSONObject(new LinkedHashMap());
            line21.put("fieldDescription","合作方授信总额");
            line21.put("fieldName","creditAmt");
            line21.put("fieldType","Text");
            line21.put("fieldValue",formatNumber(creditAmt));
            line21.put("orderSeq",1);
            line21.put("isEdit",false);
            line21.put("isShow",true);
            line21.put("nullableFlag",true);

            JSONObject line22=new JSONObject(new LinkedHashMap());
            line22.put("fieldDescription","承租人累计已用额度");
            line22.put("fieldName","tenantAmount");
            line22.put("fieldType","Text");
            line22.put("fieldValue",formatNumber(tenantAmount));
            line22.put("orderSeq",2);
            line22.put("isEdit",false);
            line22.put("isShow",true);
            line22.put("nullableFlag",true);

            JSONObject line23=new JSONObject(new LinkedHashMap());
            line23.put("fieldDescription","本次待投放金额");
            line23.put("fieldName","lastFiveResult");
            line23.put("fieldType","Text");
            line23.put("fieldValue",formatNumber(hlsCusPrjProject.getFinanceAmount()));
            line23.put("orderSeq",3);
            line23.put("isEdit",false);
            line23.put("isShow",true);
            line23.put("nullableFlag",true);

            lines2.add(line21);
            lines2.add(line22);
            lines2.add(line23);

            content2.put("lines",lines2);
            content.add(content2);

            //报价方案
            Map projectQuotationMap = hlsCusPrjProjectMapper.prjProjectQuotationQuery(hlsCusPrjProject);
            JSONObject content3=new JSONObject(new LinkedHashMap());
            JSONObject header3=new JSONObject(new LinkedHashMap());
            JSONArray lines3=new JSONArray();
            header3.put("name","报价方案");
            header3.put("icon","profile");
            header3.put("orderSeq",3);
            header3.put("hasChildren",false);
            header3.put("isShow",true);
            content3.put("header",header3);

            JSONObject line31=new JSONObject(new LinkedHashMap());
            line31.put("fieldDescription","产品名称");
            line31.put("fieldName","planId");
            line31.put("fieldType","Text");
            line31.put("fieldValue",projectQuotationMap.get("plan_id"));
            line31.put("orderSeq",1);
            line31.put("isEdit",false);
            line31.put("isShow",true);
            line31.put("nullableFlag",true);

            JSONObject line32=new JSONObject(new LinkedHashMap());
            line32.put("fieldDescription","租赁期限（月）");
            line32.put("fieldName","leaseTerm");
            line32.put("fieldType","Text");
            line32.put("fieldValue",projectQuotationMap.get("lease_term_m"));
            line32.put("orderSeq",2);
            line32.put("isEdit",false);
            line32.put("isShow",true);
            line32.put("nullableFlag",true);

            JSONObject line33=new JSONObject(new LinkedHashMap());
            line33.put("fieldDescription","还款频率");
            line33.put("fieldName","annualPayTimes");
            line33.put("fieldType","Text");
            line33.put("fieldValue",projectQuotationMap.get("annual_pay_times"));
            line33.put("orderSeq",3);
            line33.put("isEdit",false);
            line33.put("isShow",true);
            line33.put("nullableFlag",true);

            JSONObject line34=new JSONObject(new LinkedHashMap());
            line34.put("fieldDescription","首付款比例");
            line34.put("fieldName","downPaymentRatio");
            line34.put("fieldType","Text");
            line34.put("fieldValue",projectQuotationMap.get("down_payment_ratio"));
            line34.put("orderSeq",4);
            line34.put("isEdit",false);
            line34.put("isShow",true);
            line34.put("nullableFlag",true);

            JSONObject line35=new JSONObject(new LinkedHashMap());
            line35.put("fieldDescription","首付款金额");
            line35.put("fieldName","downPayment");
            line35.put("fieldType","Text");
            line35.put("fieldValue",projectQuotationMap.get("down_payment"));
            line35.put("orderSeq",5);
            line35.put("isEdit",false);
            line35.put("isShow",true);
            line35.put("nullableFlag",true);

            JSONObject line36=new JSONObject(new LinkedHashMap());
            line36.put("fieldDescription","保证金比例");
            line36.put("fieldName","depositRatio");
            line36.put("fieldType","Text");
            line36.put("fieldValue",projectQuotationMap.get("deposit_ratio"));
            line36.put("orderSeq",6);
            line36.put("isEdit",false);
            line36.put("isShow",true);
            line36.put("nullableFlag",true);

            JSONObject line37=new JSONObject(new LinkedHashMap());
            line37.put("fieldDescription","保证金");
            line37.put("fieldName","deposit");
            line37.put("fieldType","Text");
            line37.put("fieldValue",projectQuotationMap.get("deposit"));
            line37.put("orderSeq",7);
            line37.put("isEdit",false);
            line37.put("isShow",true);
            line37.put("nullableFlag",true);

            JSONObject line38=new JSONObject(new LinkedHashMap());
            line38.put("fieldDescription","融资额");
            line38.put("fieldName","financeAmount");
            line38.put("fieldType","Text");
            line38.put("fieldValue",projectQuotationMap.get("finance_amount"));
            line38.put("orderSeq",8);
            line38.put("isEdit",false);
            line38.put("isShow",true);
            line38.put("nullableFlag",true);

            JSONObject line39=new JSONObject(new LinkedHashMap());
            line39.put("fieldDescription","年利率");
            line39.put("fieldName","intRate");
            line39.put("fieldType","Text");
            line39.put("fieldValue",projectQuotationMap.get("int_rate"));
            line39.put("orderSeq",9);
            line39.put("isEdit",false);
            line39.put("isShow",true);
            line39.put("nullableFlag",true);

            JSONObject line310=new JSONObject(new LinkedHashMap());
            line310.put("fieldDescription","名义价格");
            line310.put("fieldName","residualValue");
            line310.put("fieldType","Text");
            line310.put("fieldValue",projectQuotationMap.get("residual_value"));
            line310.put("orderSeq",10);
            line310.put("isEdit",false);
            line310.put("isShow",true);
            line310.put("nullableFlag",true);



            JSONObject line312=new JSONObject(new LinkedHashMap());
            line312.put("fieldDescription","IRR");
            line312.put("fieldName","irr");
            line312.put("fieldType","Text");
            line312.put("fieldValue",projectQuotationMap.get("irr"));
            line312.put("orderSeq",12);
            line312.put("isEdit",false);
            line312.put("isShow",true);
            line312.put("nullableFlag",true);

            lines3.add(line31);
            lines3.add(line32);
            lines3.add(line33);
            lines3.add(line34);
            lines3.add(line35);
            lines3.add(line36);
            lines3.add(line37);
            lines3.add(line38);
            lines3.add(line39);
            lines3.add(line310);
            lines3.add(line312);

            content3.put("lines",lines3);
            content.add(content3);

            // 附件资料
            List<HlsCusPrjProjectAttachment> projectAttachments = hlsCusPrjProjectAttachmentMapper.prjProjectAttachQuery(hlsCusPrjProject);
            if(CollectionUtils.isNotEmpty(projectAttachments)) {
                //附件资料
                JSONObject content4 = new JSONObject(new LinkedHashMap());
                JSONObject header4 = new JSONObject(new LinkedHashMap());
                header4.put("name", "附件信息");
                header4.put("icon", "profile");
                header4.put("orderSeq", 4);
                header4.put("hasChildren", true);
                header4.put("isShow", true);
                content4.put("header", header4);
                JSONArray lines4 = new JSONArray();
                for (HlsCusPrjProjectAttachment projectAttachment : projectAttachments) {
                    if (StringUtils.isNotEmpty(projectAttachment.getFileNames())) {
                        String[] attachmentIds = projectAttachment.getFileId().split(",");
                        String[] fileNames = projectAttachment.getFileNames().split(",");
                        // 取小值，防止异常数据
                        int len = Math.min(attachmentIds.length,fileNames.length);
                        for (int i = 0; i < len; i++) {
                            FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentIds[i]);

                            JSONArray lines33=new JSONArray();
                            JSONObject line317=new JSONObject(new LinkedHashMap());
                            line317.put("fieldDescription","附件名称");
                            line317.put("fieldName","documentName");
                            line317.put("fieldType","Text");
                            line317.put("fieldValue",projectAttachment.getDocumentName());
                            line317.put("orderSeq",1);
                            line317.put("isEdit",false);
                            line317.put("isShow",true);
                            line317.put("nullableFlag",true);
                            lines33.add(line317);

                            JSONObject line318=new JSONObject(new LinkedHashMap());
                            line318.put("fieldDescription","文件名称");
                            line318.put("fieldName","fileName");
                            line318.put("fieldType","Text");
                            line318.put("fieldValue",fndAttachment.getFileName());
                            line318.put("orderSeq",2);
                            line318.put("isEdit",false);
                            line318.put("isShow",true);
                            line318.put("nullableFlag",true);
                            lines33.add(line318);

                            JSONObject line319=new JSONObject(new LinkedHashMap());
                            line319.put("fieldDescription","附件");
                            line319.put("fieldName","relatedFile");
                            line319.put("fieldType","Link");
                            line319.put("fieldValue", systemHost+ WorkflowAttachmentController.PUBLIC_DOWNLOAD+"?attachment_id="+EncryptUtils.encrypt(encryptKey,fndAttachment.getAttachmentId().toString()));
                            line319.put("orderSeq",3);
                            line319.put("isEdit",false);
                            line319.put("isShow",true);
                            line319.put("nullableFlag",true);
                            lines33.add(line319);

                            lines4.add(lines33);
                        }
                    }
                }

                if(CollectionUtils.isNotEmpty(lines4)){
                    content4.put("lines",lines4);
                    content.add(content4);
                }
            }


            //退回按钮
            JSONObject content5=new JSONObject(new LinkedHashMap());
            JSONObject header5=new JSONObject(new LinkedHashMap());
            header5.put("name","approve");
            header5.put("orderSeq",5);
            header5.put("hasChildren",false);
            content5.put("header",header5);
            JSONArray lines5=new JSONArray();

            JSONObject line51=new JSONObject(new LinkedHashMap());
            line51.put("fieldDescription","退回");
            line51.put("fieldName","sendback");
            line51.put("fieldType","Text");
            line51.put("fieldValue","SENDBACK");
            line51.put("orderSeq",1);
            line51.put("isEdit",false);
            line51.put("isShow",true);
            line51.put("nullableFlag",false);

            lines5.add(line51);
            content5.put("lines",lines5);
            content.add(content5);
            return content;
        }catch (Exception e){
            logger.error("----------进件流程填充content数据失败-----------");
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public void addEvent(DelegateExecution delegateExecution, String eventCode) {
        IRequest iRequest = (IRequest)delegateExecution.getVariable("iRequest");
        String pName = (String)delegateExecution.getVariable("pName");
        String initiator = (String)delegateExecution.getVariable("initiator");
        if (initiator != null) {
            User sysUser = this.userService.queryUserByAllocationId(initiator);
            if (sysUser != null) {
                Long documentId = (Long)delegateExecution.getVariable("documentId");
                String documentCategory = (String)delegateExecution.getVariable("documentCategory");
                String documentType = (String)delegateExecution.getVariable("documentType");
                Long userId = sysUser.getUserId();
                iRequest.setUserId(userId);
                Map<String, Object> params = new HashMap();
                if (eventCode.equals("WFL.START")) {
                    params.put("noticeTitle", "工作流通知-" + pName + "-流程开始");
                    params.put("message", pName + "-流程开始");
                } else if (eventCode.equals("WFL.FINISH")) {
                    params.put("noticeTitle", "工作流通知-" + pName + "-流程结束");
                    params.put("message", pName + "-流程结束");
                }

                params.put("noticeType", "NOTICE");
                params.put("level", 1);
                params.put("eventCode", eventCode);
                params.put("allocation_id", initiator);
                this.sysEventService.createEvent(iRequest, documentId, documentCategory, documentType, params);
            }
        }

    }

    public ResponseEntity<byte[]> downloadAttachment(HttpServletRequest request, @RequestParam("attachment_id") Long attachmentId) throws Exception {
        IRequest requestContext = this.createRequestContext(request);
        FndAttachment fndAttachment = new FndAttachment();
        fndAttachment.setAttachmentId(attachmentId);
        FndAttachment attachment = (FndAttachment)this.fndAttachmentService.selectByPrimaryKey(requestContext, fndAttachment);
        if (attachment != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", BrowserUtils.getFileName(request, attachment.getFileName()));
            headers.setContentLength(attachment.getFileSize());
            String filePath = attachment.getFilePath();
            byte[] bytes;
            if (OSSUtils.isOOSFile(filePath) && this.cloudStorageService != null) {
                InputStream download = this.cloudStorageService.download(filePath);
                bytes = IOUtils.toByteArray(download);
                IOUtils.closeQuietly(download);
                return new ResponseEntity(bytes, headers, HttpStatus.OK);
            }

            File file = new File(filePath);
            if (file.exists()) {
                bytes = FileUtils.readFileToByteArray(file);
                return new ResponseEntity(bytes, headers, HttpStatus.OK);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private IRequest createRequestContext(HttpServletRequest request) {
        return this.createRequestContext(request, false);
    }

    private IRequest createRequestContext(HttpServletRequest request, boolean refresh) {
        if (refresh) {
            return RequestHelper.createServiceRequest(request);
        } else {
            IRequest currentRequest = RequestHelper.getCurrentRequest();
            return currentRequest != null ? currentRequest : RequestHelper.createServiceRequest(request);
        }
    }

    //获取uuid
    public String getUUID(){
        JSONObject interfaceRequest = interfacePlatformUtils.getInterfaceRequest(new JSONObject(), initializeAttachment);
        return interfaceRequest.get("content").toString();
    }

    //将文件上传到接口平台
    public void upLoadFileToPlat(String uuid,String fileName, String filePath,String attachmentId){
        //获取token
        String accessToken = interfacePlatformUtils.getAccessTokenFromRedis();
        HttpPost httpPost = new HttpPost(base+attachmentUpload+accessToken);
        //headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data");
        formDataConnectorUtils.packageHeader(headers, httpPost);
        CloseableHttpResponse response = null;
        try{
            File file = new File(filePath);
            // 设置请求头 boundary边界不可重复，重复会导致提交失败
            String boundary = "-------------------------" + UUID.randomUUID().toString();
            httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

            // 创建MultipartEntityBuilder
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // 设置字符编码
            builder.setCharset(StandardCharsets.UTF_8);
            // 模拟浏览器
            builder.setMode(HttpMultipartMode.RFC6532);
            // 设置边界
            builder.setBoundary(boundary);
            // 设置multipart/form-data流文件
            builder.addPart("file", new FileBody(file));
            // application/octet-stream代表不知道是什么格式的文件
            builder.addBinaryBody("media", file, ContentType.create("application/octet-stream"), fileName);

            // 编码类型
            ContentType strContent=ContentType.create("text/plain", StandardCharsets.UTF_8);
            builder.addTextBody("bucketName", "hptl",strContent);
            builder.addTextBody("directory", "todoFile",strContent);
            builder.addTextBody("attachmentUUID", uuid,strContent);
            builder.addTextBody("fileName", fileName,strContent);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            CloseableHttpClient httpClient=formDataConnectorUtils.getClient();
            response = httpClient.execute(httpPost);
            HttpExecuteResponse httpExecuteResponse = new HttpExecuteResponse();
            int statusCode = response.getStatusLine().getStatusCode();
            httpExecuteResponse.setResponseCode(statusCode);
            httpExecuteResponse.setResponseBody(EntityUtils.toByteArray(response.getEntity()));
            httpExecuteResponse.setHeaders(response.getAllHeaders());
            String responseAsString = httpExecuteResponse.getResponseAsString();
            if (response.getStatusLine() != null && response.getStatusLine().getStatusCode() < 400) {
                logger.info("文件上传成功!------>返回信息："+responseAsString);
                int result = fndAtmAttachmentMapper.updateUUIDToFndAtmAttachment(attachmentId, uuid);
                if(result>=0){
                    logger.info("uuid插入成功");
                }else{
                    logger.warn("uuid插入失败");
                }
            } else {
                logger.error("对方响应的状态码不在符合的范围内!");
                logger.error("返回信息："+responseAsString);
//                throw new RuntimeException();
            }
        } catch (Exception e) {
            logger.error("网络访问异常,请求url地址={},响应体={},error={}", base+attachmentUpload+accessToken, response, e);
//            throw new RuntimeException();
        } finally {
            logger.info("统一外网请求参数打印,post请求url地址={}", base+attachmentUpload+accessToken);
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("请求链接释放异常", e);
            }
        }
    }

    //将文件上传到接口平台
    public void upLoadFileToPlat1(String fileName, String filePath,String attachmentId){
        //获取token
        String accessToken = interfacePlatformUtils.getAccessTokenFromRedis();
        HttpPost httpPost = new HttpPost(base+attachmentUpload+accessToken);
        // 固定参数,以接口平台提供为准
        String bucketName = "hptl";
        // 固定参数,以接口平台提供为准
        String directory = "todoFile";
        //附件URL, 由https://apitest-gateway.gdhcapital.com.cn/hfle/v1/1/files/uuid接口获取
        String attachmentUUID = getUUID();
        String returnUrl = null;

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
//            HttpPost httppost = new HttpPost(base+attachmentUpload);

//            httpPost.setHeader("Authorization", accessToken);

            ContentType contentType = ContentType.create("application/octet-stream", Consts.UTF_8);

            File file = new File(filePath);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.RFC6532)
                    .setCharset(Consts.UTF_8)
                    .addPart("bucketName", new StringBody(bucketName, ContentType.TEXT_PLAIN))
                    .addPart("directory", new StringBody(directory, ContentType.TEXT_PLAIN))
                    .addPart("attachmentUUID", new StringBody(attachmentUUID, ContentType.TEXT_PLAIN))
//                    .addPart("fileName", new StringBody(file.getName(), ContentType.TEXT_PLAIN))
                    .addBinaryBody("file", file, contentType, fileName)
                    .build();

            httpPost.setEntity(reqEntity);

            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    returnUrl = EntityUtils.toString(resEntity, "GBK");
                    System.out.println(returnUrl);
                    int result = fndAtmAttachmentMapper.updateUUIDToFndAtmAttachment(attachmentId, attachmentUUID);
                    if(result>=0){
                        logger.info("uuid插入成功");
                    }else{
                        logger.warn("uuid插入失败");
                    }
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        return returnUrl;
    }

    private String formatNumber(Object d){
        if (d == null) {
            return "";
        }
        return DECIMAL_FORMAT.format(d);
    }
}
