package com.hand.hls.app.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.*;
import com.hand.hap.activiti.mapper.TaskNewMapper;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.app.entity.HlsCusAppResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.autoconfigure.properties.OSSProperties;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.history.mapper.HlsStandardHistoryMapper;
import com.hand.hls.interfacePlatform.utils.InterfacePlatformUtils;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.oss.service.CloudStorageService;
import com.hand.hls.oss.utils.OSSUtils;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMeetingJudgeMapper;
import com.hand.hls.prj.mapper.HlsCusProjectChangeMeetingJudgeMapper;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.HlsCusSysAppointApproverMapper;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.utils.HttpExecuteResponse;
import com.hand.hls.wfl.service.WflAppointApproverService;
import hls.layout.service.impl.ServerLayoutSaveServiceImpl;
import leaf.bm.components.RecordHelper;
import leaf.utils.BrowserUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author 赵凯
 * @Date 2021-03-09 13:10
 * @description
 */
@Controller
public class HlsCusAppActivitiController extends BaseController {


    //待办
    private static final String WFL_TYPE_TODO = "TODO";
    //已办
    private static final String WFL_TYPE_DONE = "DONE";


    //已办详情
    private static final String WFL_TYPE_END = "END";
    private static final String WFL_TYPE_PROCESSING = "PROCESSING";

    private static final String NULL = "null";
    public static final String PROJECT = "PROJECT";
    public static final String SYSTEM_CODE = "SYSTEM_CODE";
    public static final String SYSTEM_TABLE = "SYSTEM_TABLE";

    private Logger logger = LoggerFactory.getLogger(getClass());

    //我的
    private static final String WFL_TYPE_MYDO = "MYDO";

    private static final String PRJ_PROJECT_CHANCE_WFL = "PRJ_PROJECT_CHANCE";
    private static final String PRJ_PROJECT_INVESTMENT_WFL = "PRJ_PROJECT_INVESTMENT_WFL";

    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;


    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private TaskNewMapper taskNewMapper;


    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IFndAttachmentService iFndAttachmentService;

    @Autowired
    private HlsCusPrjProjectMeetingJudgeMapper hlsCusPrjProjectMeetingJudgeMapper;
    @Autowired
    private HlsCusProjectChangeMeetingJudgeMapper hlsCusProjectChangeMeetingJudgeMapper;
    @Autowired
    private HlsStandardHistoryMapper hlsStandardHistoryMapper;
    @Autowired
    private ServerLayoutSaveServiceImpl serverLayoutSaveService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceService service;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;
    @Autowired
    private FndAtmAttachmentMapper fndAtmAttachmentMapper;
    @Autowired
    private HlsCusSysAppointApproverMapper hlsCusSysAppointApproverMapper;
    @Value("${file.upload.dir:.}")
    private String savePath = ".";
    private boolean needParse = false;
    @Autowired(
            required = false
    )
    private OSSProperties ossProperties;
    @Autowired(
            required = false
    )
    private CloudStorageService cloudStorageService;

    @Value("${file.upload.dir}")
    private String uploadFileAddr;

    @Value("${intfPlatform.getAttachmentByUrl}")
    private String getAttachmentByUrl;

    private  Long businessKey;
    private static final String ADMIN_TRUE = "Y";
    private static final String ADMIN_FALSE = "N";

    private final String ADD_RISK_CREDIT = "ADD_RISK_CREDIT";


    @Autowired
    WflAppointApproverService wflAppointApproverService;
    @Autowired
    private InterfacePlatformUtils interfacePlatformUtils;

    /**
     * 工作流按钮接口
     *
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = {"/r/api/wfl/detail/operate"}, method = {RequestMethod.POST})
    @ResponseBody
    public HlsCusAppResponseData wxOperate(HttpServletRequest request, @RequestBody JSONObject params) {
        try{
            String interfaceUrl = "/wx/api/wfl/detail/operate";
            String interfaceName = "工作流按钮操作接口";
            IRequest iRequest = this.createRequestContext(request);
            HlsCusAppResponseData hlsCusAppResponseData = new HlsCusAppResponseData();

            if (params.size() == 0) {
                hlsCusAppResponseData.setSuccess(false);
                hlsCusAppResponseData.setReturnMsg("请求参数缺失！");
                hlsCusAppResponseData.setReturnStatus("E");
                hlsCusAppResponseData.setReturnCode("E00001");
                hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
                hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
                logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());
                return hlsCusAppResponseData;
            }

            //hotfix-2023.02.14:获取回调参数调整
            /*JSONArray globalParams = params.getJSONArray("globalParam");
            JSONObject globalParam = globalParams.getJSONObject(0);
            String valueStr = globalParam.getString("value");
            JSONObject valueParams = JSON.parseObject(valueStr);*/


            String substring = String.valueOf(params.get("sourceId"));
            String sourceId = substring.substring(substring.indexOf("-")+1);
            //按钮id
            String approveStatus = String.valueOf(params.get("approveStatus"));
            //按钮描述
            String approveResultDesc =null;
            switch (approveStatus){
                case "APPROVED":
                    approveResultDesc="同意"; break;
                case "REJECTED":
                    approveResultDesc="拒绝"; break;
                case "CANCELLED":
                    approveResultDesc="取消"; break;
                case "SENDBACK":
                    approveStatus="APPROVED_RETURN";
                    approveResultDesc="退回"; break;
                case "SUSPENDED":
                    approveResultDesc="挂起"; break;
            }
            String dealUser = String.valueOf(params.get("dealUser"));
            String remark = String.valueOf(params.get("remark"));
            String workflowName=String.valueOf(params.get("workflowName"));
            String value = String.valueOf(params.get("instanceId"));
            String instanceId = value.substring(value.indexOf("-")+1);

            String nodeId=String.valueOf(params.get("nodeId"));
            //审批附件
            String approveAttachmentUuid = String.valueOf(params.get("approveAttachmentUuid"));



            logger.info("====================微信详情 工作流按钮操作================================");
            logger.info("params:" + params);
            logger.info("taskId:" + sourceId);
            logger.info("approveResult:" + approveStatus);
            logger.info("approveResultDesc:" + approveResultDesc);
            logger.info("====================微信详情 工作流按钮操作================================");


            String action = "complete";
            //String comment = null;
            String jumpTarget = null;
            String jumpTargetName = null;
            String carbonCopyUsers = null;
            String assignee = null;
            if (sourceId == null || NULL.equalsIgnoreCase(sourceId)) {
                hlsCusAppResponseData.setSuccess(false);
                hlsCusAppResponseData.setReturnMsg("请求参数workflowId缺失");
                hlsCusAppResponseData.setReturnStatus("E");
                hlsCusAppResponseData.setReturnCode("E00002");
                hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
                hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
                logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());
                return hlsCusAppResponseData;
            }
            if (approveStatus == null || NULL.equalsIgnoreCase(approveStatus)) {
                hlsCusAppResponseData.setSuccess(false);
                hlsCusAppResponseData.setReturnMsg("请求参数approveResult缺失");
                hlsCusAppResponseData.setReturnStatus("E");
                hlsCusAppResponseData.setReturnCode("E00003");
                hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
                hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
                logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());
                return hlsCusAppResponseData;
            }


            TaskActionRequestExt taskActionRequest = new TaskActionRequestExt();
            //处理意见
            taskActionRequest.setComment(remark);
            taskActionRequest.setAction(action);
            taskActionRequest.setJumpTarget(jumpTarget);
            taskActionRequest.setJumpTargetName(jumpTargetName);
            taskActionRequest.setCarbonCopyUsers(carbonCopyUsers);
            taskActionRequest.setCurrentTaskId(sourceId);
            taskActionRequest.setAssignee(dealUser);

            List<RestVariable> variables = new ArrayList<>();
            List<RestVariable> transientVariables = new ArrayList<>();
            RestVariable restVariable1 = new RestVariable();
            restVariable1.setName("approveResult");
            restVariable1.setValue(approveStatus);
            variables.add(restVariable1);

            RestVariable restVariable2 = new RestVariable();
            restVariable2.setName("approveResultDesc");
            restVariable2.setValue(approveResultDesc);
            variables.add(restVariable2);

            RestVariable restVariable3 = new RestVariable();
            restVariable3.setName("comment");
            restVariable3.setValue(remark);
            variables.add(restVariable3);
            taskActionRequest.setVariables(variables);

            taskActionRequest.setTransientVariables(null);

            List<Map> maps = new ArrayList<>();
            Map map = new HashMap();
            String data = "success";
            Boolean success = true;
            String message = "操作成功";

            SysUser sysUser = new SysUser();
            sysUser.setAttribute15(dealUser);
            SysUser user = sysUserMapper.queryUserByUserName(dealUser);
            if(user==null){
                hlsCusAppResponseData.setSuccess(false);
                hlsCusAppResponseData.setReturnMsg("系统中不存在这个处理人账号！");
                hlsCusAppResponseData.setReturnStatus("E");
                hlsCusAppResponseData.setReturnCode("E00005");
                hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
                hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
                logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());

                return hlsCusAppResponseData;
            }
            iRequest.setUserId(user.getUserId());
            iRequest.setUserName(user.getUserName());
            iRequest.setEmployeeName(user.getDescription());
            iRequest.setAttribute("isPhone","true");
            String allocationId = hlsCusSysAppointApproverMapper.getAssignByUserName(user.getUserName());
            iRequest.setAttribute("allocationId",Long.valueOf(allocationId));
            //insertActHiComment(sourceId,instanceId,approveStatus,user,approveResultDesc,remark);
            if(approveAttachmentUuid!=null&&!"null".equalsIgnoreCase(approveAttachmentUuid)){
                Boolean mobileFileUpload = mobileFileUpload(sourceId, approveAttachmentUuid,instanceId,dealUser,iRequest);
                if(mobileFileUpload==false){
                    hlsCusAppResponseData.setSuccess(false);
                    hlsCusAppResponseData.setReturnMsg("审批附件传输异常");
                    hlsCusAppResponseData.setReturnStatus("E");
                    hlsCusAppResponseData.setReturnCode("E00004");
                    hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
                    hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
                    logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());

                    return hlsCusAppResponseData;
                }
            }
            try {
                activitiService.executeTaskAction(iRequest, sourceId, taskActionRequest, false);
            } catch (Exception e){
                logger.info("====================微信详情 按钮操作  错误信息打印================================");
                logger.error("first param ",e);
                logger.info(e.getStackTrace().toString());
                logger.info("====================微信详情 按钮操作  错误信息打印================================");
                data = e.getMessage();
                success = false;
                message = e.getMessage();
                map.put("message", message);
                hlsCusAppResponseData.setSuccess(success);
                hlsCusAppResponseData.setReturnMsg("不存在"+sourceId+"这一条数据！");
                hlsCusAppResponseData.setReturnStatus("w");
                hlsCusAppResponseData.setReturnCode("W00002");
                hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
                hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
                logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());

                return hlsCusAppResponseData;
            }

            map.put("message", message);
            hlsCusAppResponseData.setSuccess(success);
            hlsCusAppResponseData.setReturnMsg("成功!");
            hlsCusAppResponseData.setReturnStatus("S");
            hlsCusAppResponseData.setReturnCode("S00001");
            hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
            hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
            logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());


            return hlsCusAppResponseData;
        }catch (Exception e){
            logger.error("回调不成功！");
            e.printStackTrace();

            //hotfix-2023.02.14:获取回调参数调整
            /*JSONArray globalParams = params.getJSONArray("globalParam");
            JSONObject globalParam = globalParams.getJSONObject(0);
            String valueStr = globalParam.getString("value");
            JSONObject valueParams = JSON.parseObject(valueStr);*/

            HlsCusAppResponseData hlsCusAppResponseData = new HlsCusAppResponseData();
            hlsCusAppResponseData.setSuccess(false);
            hlsCusAppResponseData.setReturnMsg("回调接口调用失败");
            hlsCusAppResponseData.setReturnStatus("E");
            hlsCusAppResponseData.setReturnCode("E00002");
            hlsCusAppResponseData.setSourceId(String.valueOf(params.get("sourceId")));
            hlsCusAppResponseData.setDocmentNum(String.valueOf(params.get("docmentNum")));
            logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());

            return hlsCusAppResponseData;
        }
    }

    @RequestMapping({"/wx/api/fnd/attachment/download"})
    @ResponseBody
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

    @RequestMapping({"/r/api/wfl/detail/operate/upload"})
    @ResponseBody
    public HlsCusAppResponseData mobileUploadAttachment(String uuid, MultipartFile file,String fileName) throws Exception {
        HlsCusAppResponseData hlsCusAppResponseData = new HlsCusAppResponseData();
        try{
            File toFile = this.multipartFileToFile(file);
            //this.uploadAttachment(URLDecoder.decode(fileName, "UTF-8"), toFile.getPath(), tableName, String.valueOf(pkValue), file.getSize(),request);
            Date now = new Date(System.currentTimeMillis());
            FndAttachment fndAttachment = new FndAttachment();
            fndAttachment.setSourceTypeCode("fnd_atm_attachment_multi");
            fndAttachment.setFileName(fileName);
            fndAttachment.setFilePath(toFile.getPath());
            fndAttachment.setMimeType("");
            fndAttachment.setFileSize(toFile.length());
            fndAttachment.setFileTypeCode("");
            fndAttachment.setCreationDate(now);
            fndAttachment.setLastUpdateDate(now);
            fndAttachment.setLastUpdatedBy(0L);
            fndAttachment.setCreatedBy(0L);
            this.fndAttachmentMapper.insertSelective(fndAttachment);
            fndAtmAttachmentMapper.updateUUIDToFndAtmAttachment(String.valueOf(fndAttachment.getAttachmentId()), uuid);
            hlsCusAppResponseData.setSuccess(true);
            hlsCusAppResponseData.setReturnMsg("文件上传成功!");
            hlsCusAppResponseData.setReturnStatus("S");
            hlsCusAppResponseData.setReturnCode("S00002");
            hlsCusAppResponseData.setSourceId("");
            hlsCusAppResponseData.setDocmentNum("");
            logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());

            return hlsCusAppResponseData;
        }catch (Exception e){
            logger.error("文件上传失败",e.getMessage());
            hlsCusAppResponseData.setSuccess(false);
            hlsCusAppResponseData.setReturnMsg("文件上传失败,失败原因："+e.getMessage());
            hlsCusAppResponseData.setReturnStatus("E");
            hlsCusAppResponseData.setReturnCode("E00006");
            hlsCusAppResponseData.setSourceId("");
            hlsCusAppResponseData.setDocmentNum("");
            logger.info("=========hlsCusAppResponseData: " + hlsCusAppResponseData.toString());

            return hlsCusAppResponseData;
        }

    }

    //用来插入act_hi_comment表数据
    private void insertActHiComment(String sourceId,String instanceId,String approveStatus,SysUser user,String approveResultDesc,String remark){
        Map map1=new HashMap();
        map1.put("id_",sourceId+1);
        map1.put("proc_inst_id_",instanceId);
        map1.put("action_","AddComment");
        map1.put("type_","action");
        map1.put("message_",approveStatus);
        map1.put("user_id_",user.getUserId());
        map1.put("time_",System.currentTimeMillis());
        RecordHelper.insert("act_hi_comment", map1);
        map1.put("id_",sourceId+2);
        map1.put("type_","actionDesc");
        map1.put("message_",approveResultDesc);
        RecordHelper.insert("act_hi_comment", map1);
        map1.put("id_",sourceId+3);
        map1.put("type_","comment");
        map1.put("message_",remark);
        RecordHelper.insert("act_hi_comment", map1);
    }

    //将移动端审批文件上传
    private Boolean mobileFileUpload(String sourceId, String approveAttachmentUuid,String processInstanceId,String dealUser,IRequest request){
        try{
            Task task =  taskService.createTaskQuery().taskId(sourceId).singleResult();
            List<TaskNew> taskNews = taskNewMapper.queryTasks(processInstanceId);
            if(task==null){
                return false;
            }
            for (TaskNew taskNew : taskNews) {
                if(taskNew.getBusinessKey()!=null){
                    businessKey= Long.valueOf(taskNew.getBusinessKey());
                    break;
                }
            }
            HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment=new HlsCusPrjProjectAttachment();
            hlsCusPrjProjectAttachment.setProjectId(businessKey);
            hlsCusPrjProjectAttachment.setProjectAttachmentCategory("PRJ_PROJECT_JD");
            List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.prjProjectAttachmentDetailQuery(hlsCusPrjProjectAttachment);
            Long pkValue =0L;
            for (HlsCusPrjProjectAttachment cusPrjProjectAttachment : hlsCusPrjProjectAttachments) {
                if("风险岗项目风险评估".equals(task.getName())){
                    if("风险评估报告".equals(cusPrjProjectAttachment.getDocumentName())){
                        pkValue=cusPrjProjectAttachment.getProjectAttachmentId();
                    }
                }else{
                    if("法律审查意见".equals(cusPrjProjectAttachment.getDocumentName())){
                        pkValue=cusPrjProjectAttachment.getProjectAttachmentId();
                    }
                }
            }
            String tableName="PRJ_PROJECT_JD";
            //通过uuid下载获取文件
            String accessToken = interfacePlatformUtils.getAccessTokenFromRedis();
            String url=getAttachmentByUrl+approveAttachmentUuid+"/file?access_token="+accessToken;
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doGet(url);
            JSONArray jsonArray=new JSONArray();
            List<File> fileList=new ArrayList<>();
            if(httpExecuteResponse.isSuccess()&&httpExecuteResponse.getResponseAsString()!=null){
                logger.info("接口访问成功!");
                jsonArray= JSONArray.parseArray(httpExecuteResponse.getResponseAsString());
//            uploadFileAddr
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    File file = this.downLoadFromUrl(jsonObject.get("fileUrl").toString(), jsonObject.get("fileName").toString(), uploadFileAddr);
                    if(file!=null){
                        this.uploadAttachment(URLDecoder.decode(file.getName(), "UTF-8"), file.getPath(), tableName, String.valueOf(pkValue), file.length(),request);
                    }
                }
            }else{
                logger.error("访问接口失败，访问响应码："+httpExecuteResponse.getResponseCode());
                logger.error("返回信息："+httpExecuteResponse.getResponseAsString());
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 通过url下载文件到指定位置
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public  File  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间
        conn.setConnectTimeout(10*1000);
        //设置一个请求头
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //获得输入流
        InputStream inputStream = conn.getInputStream();
        //获取数组
        byte[] getData = readInputStream(inputStream);
        //文件保存路径
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(null != fos){
            fos.close();
        }
        if(null != inputStream){
            inputStream.close();
        }
        System.out.println("info:"+url+" download success");
        return file;
    }

    public   byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * MultipartFile 转 File
     * @param file
     * @throws Exception
     */
    public  File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(uploadFileAddr+"/"+System.currentTimeMillis()+file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    /**
     * 获取流文件
     * @param ins
     * @param file
     */
    private  void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long uploadAttachment(String fileName, String filePath, String sourceType, String pkValue, Long fileSize,IRequest request) {
        Date now = new Date(System.currentTimeMillis());
        int index = fileName.lastIndexOf(".");
        String typeCode = null;
        String mineType = null;
        if (index > 0) {
            String ext = fileName.substring(index + 1);
            Map map = this.fndAttachmentMapper.queryExt(ext);
            if (map != null) {
                typeCode = map.getOrDefault("file_type_code", "").toString();
                mineType = map.getOrDefault("mine_type", "").toString();
            }
        }

        this.fndAttachmentMapper.deleteAttachmentMulti(sourceType, pkValue);
        FndAttachment fndAttachment = new FndAttachment();
        fndAttachment.setSourceTypeCode("fnd_atm_attachment_multi");
        fndAttachment.setFileName(fileName);
        fndAttachment.setFilePath(filePath);
        fndAttachment.setMimeType(mineType);
        fndAttachment.setFileSize(fileSize);
        fndAttachment.setFileTypeCode(typeCode);
        fndAttachment.setCreationDate(now);
        fndAttachment.setLastUpdateDate(now);
        fndAttachment.setLastUpdatedBy(request.getUserId());
        fndAttachment.setCreatedBy(request.getUserId());
        this.fndAttachmentMapper.insertSelective(fndAttachment);
        FndAttachmentMulti multi = new FndAttachmentMulti();
        multi.setTableName(sourceType);
        multi.setTablePkValue(pkValue);
        multi.setAttachmentId(fndAttachment.getAttachmentId());
        multi.setCreationDate(now);
        multi.setLastUpdateDate(now);
        multi.setLastUpdatedBy(request.getUserId());
        multi.setCreatedBy(request.getUserId());
        this.fndAttachmentMultiMapper.insertSelective(multi);
        FndAttachment condition = new FndAttachment();
        condition.setAttachmentId(fndAttachment.getAttachmentId());
        condition.setSourcePkValue(multi.getRecordId().toString());
        this.fndAttachmentMapper.updateByPrimaryKeySelective(condition);
        return fndAttachment.getAttachmentId();
    }
}
