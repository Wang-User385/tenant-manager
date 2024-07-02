package com.hand.hls.eas.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.dto.HapInterfaceLine;
import com.hand.hap.intergration.util.HapInvokeLogUtils;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.common.dto.HlsCusDocumentRecordList;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.service.HlsCusDocumentRecordListService;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshBankAccountMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.eas.dto.*;
import com.hand.hls.eas.mapper.*;
import com.hand.hls.eas.service.IHlsCusEasLoginService;

import com.hand.hls.eas.service.IHlsCusEasSourceRecordService;
import com.hand.hls.eas.service.IHlsCusPsotEasTmpService;
import com.hand.hls.eas.utils.*;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeHead;
import com.hand.hls.gld.mapper.HlsCusJeHeadMapper;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.service.IFndCompanyService;
import com.hand.hls.sys.service.IFndOrgUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.eas.client.WSContext;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusEasLoginServiceImpl extends BaseServiceImpl<HlsCusEasLogin> implements IHlsCusEasLoginService {


    @Autowired
    private HlsCusBpMasterRequestRecordsService hlsCusBpMasterRequestRecordsService;


    @Autowired
    private IHlsCusEasSourceRecordService hlsCusEasSourceRecordService;

    @Autowired
    private HlsCusDocumentRecordListService hlsCusDocumentRecordListService;

    @Autowired
    private HlsCusEasLoginMapper hlsCusEasLoginMapper;

    @Autowired
    private IHlsCusPsotEasTmpService hlsCusPsotEasTmpService;

    @Autowired
    private HlsCusBankAccountHistoryMapper hlsCusBankAccountHistoryMapper;


    @Autowired
    private HlsCusCoreAccountHistoryMapper hlsCusCoreAccountHistoryMapper;

    @Autowired
    private HlsCusAccountHistoryMapper hlsCusAccountHistoryMapper;

    @Autowired
    private HlsCusPsotEasTmpMapper hlsCusPsotEasTmpMapper;


    @Autowired
    private HlsCusJeHeadMapper jeHeadMapper;

    @Autowired
    private HlsCusCshBankAccountMapper hlsCusCshBankAccountMapper;


    @Autowired
    private HlsCusEasSourceRecordMapper hlsCusEasSourceRecordMapper;

    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    @Autowired
    private HlsCusHapInterfaceOutboundService hlsHapInterfaceOutboundService;


    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private IFndCompanyService fndCompanyService;


    @Autowired
    private IFndOrgUnitService iFndOrgUnitService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;


    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;



    //金蝶登录接口
    @Override
    public HlsCusEasLogin easDoLogin(IRequest iRequest, HlsCusEasLogin dto) {
        String stackTrace=null;
        String requestStatus=null;
        String responseCode=null;

        //获取接口配置信息地址
        HapInterfaceLine hapInterfaceLine= hlsCusBpMasterRequestRecordsService.getInterfaceUrl(iRequest,"EAS_ITFC","EAS_LOGIN");

        //获取eas登录基础信息
        List<HlsCusEasLogin> hlsCusEasLoginList=this.selectAll(iRequest);

        WSContext cxt = null;

        String userName = hlsCusEasLoginList.get(0).getUserName();
        String password = hlsCusEasLoginList.get(0).getPassword();
        String slnName = hlsCusEasLoginList.get(0).getSlnName();
        String dcName = hlsCusEasLoginList.get(0).getDcName();
        String language = hlsCusEasLoginList.get(0).getLanguage();
        int dbType = hlsCusEasLoginList.get(0).getDbType();

        EASLogin xyEASLogin=new EASLogin();

        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();

        try {
            cxt= xyEASLogin.easDoLogin(hapInterfaceLine.getIftUrl(), userName, password, slnName, dcName, language, dbType);

            dto.setSessionId(cxt.getSessionId());
        }
        catch(Throwable throwable){
            cxt = null;
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();

        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(hapInterfaceLine.getIftUrl());

        String json = JSON.toJSONString(hlsCusEasLoginList.get(0));
        outbound.setRequestParameter(json);
        if(stackTrace!=null){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
            responseCode="200";
        }
        outbound.setRequestStatus(requestStatus);
        String json2 = JSON.toJSONString(cxt);
        outbound.setResponseContent(json2);
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息success
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());
        HlsCusHapInterfaceOutbound outboundData= hlsCusBpMasterRequestRecordsService.outboundInvokeInsert(iRequest,outbound);

        return dto;
    }



    public void easCodeUpdate(IRequest iRequest,String sourceTable,Long sourceId,String sourceType,String easnumber) {

        if("01".equals(sourceType)){//客户名称基础资料
            HlsCusConContract hlsCusConContract= new HlsCusConContract();
            hlsCusConContract.setEasBpCode(easnumber);
            hlsCusConContract.setContractId(sourceId);
            hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);

        }
        else if("04".equals(sourceType)){//供应商基础资料

            if("CSH_PAYMENT_REQ_LN".equals(sourceTable)){
                HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
                hlsCusCshPaymentReqLn.setPaymentReqLnId(sourceId);
                hlsCusCshPaymentReqLn.setEasCode(easnumber);
                hlsCusCshPaymentReqLnMapper.updateByPrimaryKeySelective(hlsCusCshPaymentReqLn);
            }
            else {
                HlsCusBpMaster hlsCusBpMaster=new HlsCusBpMaster();
                hlsCusBpMaster.setEasBpCode(easnumber);
                hlsCusBpMaster.setBpId(sourceId);
                hlsCusBpMasterService.updateByPrimaryKeySelective(iRequest,hlsCusBpMaster);
            }


        }
        else if("bm".equals(sourceType)){//部门基础资料
                FndOrgUnit fndOrgUnit=new FndOrgUnit();
                fndOrgUnit.setUnitId(sourceId);
                fndOrgUnit.setEasCode(easnumber);
                iFndOrgUnitService.updateByPrimaryKeySelective(iRequest,fndOrgUnit);

            }
        else if("05".equals(sourceType)){//合同基础资料
            HlsCusConContract hlsCusConContract= new HlsCusConContract();
            hlsCusConContract.setEasCode(easnumber);
            hlsCusConContract.setContractId(sourceId);
            hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
        }


    }

    //金蝶基础资料同步接口
    @Override
    public void easBasicSyn(IRequest iRequest, HlsCusEasBasicData dto,String sourceTable,Long sourceId) {
        String stackTrace=null;
        String requestStatus=null;
        String responseCode=null;
        String returnString="";

        String param="";

        //获取接口配置信息地址
        HapInterfaceLine hapInterfaceLine= hlsCusBpMasterRequestRecordsService.getInterfaceUrl(iRequest,"EAS_ITFC","EAS_BASIC_SYN");

        //传入数据封装 "\"name\"";

        param = JSON.toJSONString(dto);

        WSaccountingItemCoreFacade easWSaccountingItemCoreFacade=new WSaccountingItemCoreFacade();

        HlsCusEasSourceRecord hlsCusEasSourceRecord= new HlsCusEasSourceRecord();
        hlsCusEasSourceRecord.setSourceId(sourceId);
        hlsCusEasSourceRecord.setSourceTable(sourceTable);
        hlsCusEasSourceRecord.setSourceType(dto.getTypeNumber());
        hlsCusEasSourceRecord.setRecordDate(new Date());
        hlsCusEasSourceRecord= hlsCusEasSourceRecordService.insertSelective(iRequest,hlsCusEasSourceRecord);

        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();

        try {
            returnString= easWSaccountingItemCoreFacade.basicDataSynchronization(hapInterfaceLine.getIftUrl(),param);
        }
        catch(Throwable throwable){
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();



        if(!("".equals(returnString)&&returnString!=null)){
            JSONObject jsonObject = JSONObject.parseObject(returnString);
            String code = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            String time = jsonObject.getString("time");
            String easnumber = jsonObject.getString("easnumber");

            hlsCusEasSourceRecord.setCode(code);
            hlsCusEasSourceRecord.setMessage(message);
            hlsCusEasSourceRecord.setTime(time);
            hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

            //反写easnumber
            easCodeUpdate(iRequest, sourceTable, sourceId,dto.getTypeNumber(), easnumber);


            if("200".equals(code)){
                requestStatus="success";
                responseCode="200";

            }
            else{
                requestStatus="failure";
            }
        }
        else{
            requestStatus="failure";
            hlsCusEasSourceRecord.setCode("");
            hlsCusEasSourceRecord.setMessage("接口调用出错");
            hlsCusEasSourceRecord.setTime("");
            hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);
        }


        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(hapInterfaceLine.getIftUrl());

        outbound.setRequestParameter(param);
//        if(stackTrace!=null||"".equals(returnString)){
//            requestStatus="failure";
//        }
//        else{
//            requestStatus="success";
//            responseCode="200";
//        }
        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnString);
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息success
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());
        HlsCusHapInterfaceOutbound outboundData= hlsCusBpMasterRequestRecordsService.outboundInvokeInsert(iRequest,outbound);


        //日志信息与单据信息关联
        HlsCusDocumentRecordList hlsCusDocumentRecordList = new HlsCusDocumentRecordList();
        hlsCusDocumentRecordList.setOutboundId(outboundData.getOutboundId());
        hlsCusDocumentRecordList.setSourceId(hlsCusEasSourceRecord.getSourceId());
        hlsCusDocumentRecordList.setSourceTable(hlsCusEasSourceRecord.getSourceTable());
        hlsCusDocumentRecordList.setMark("金蝶接口单据记录表");
        hlsCusDocumentRecordList.setSourceType(dto.getTypeNumber());

        hlsCusDocumentRecordListService.insertSelective(iRequest,hlsCusDocumentRecordList);

    }



    //金蝶凭证传输同步接口
    @Override
    public void easCredentialsSynchronization(IRequest iRequest,Long outboundId) {

        HlsCusEasLogin hlsCusEasLogin=new HlsCusEasLogin();
        easDoLogin(iRequest, hlsCusEasLogin);

        String stackTrace=null;
        String requestStatus=null;
        String responseCode=null;
        String returnString="";

        String param="";


        //获取接口配置信息地址
        HapInterfaceLine hapInterfaceLine= hlsCusBpMasterRequestRecordsService.getInterfaceUrl(iRequest,"EAS_ITFC","EAS_CREDENTIALS_SYN");

        List<HlsCusPsotEasTmp> hlsCusPsotEasTmpList=null;
        //传入数据封装
        if(outboundId!=null){//从日志监控重发机制触发
            HlsCusPsotEasTmp hlsCusPsotEasTmp= new HlsCusPsotEasTmp();
            hlsCusPsotEasTmp.setOutboundId(outboundId);
            hlsCusPsotEasTmpList= hlsCusPsotEasTmpMapper.selectCredentialsLogData(hlsCusPsotEasTmp);
        }
        else{
            Long sessionId=iRequest.getAttribute("session_id");
            HlsCusPsotEasTmp hlsCusPsotEasTmp= new HlsCusPsotEasTmp();
            hlsCusPsotEasTmp.setSessionId(sessionId);
            hlsCusPsotEasTmpList= hlsCusPsotEasTmpMapper.selectCredentialsTmpData(hlsCusPsotEasTmp);
        }

        for(HlsCusPsotEasTmp item:hlsCusPsotEasTmpList){
            HlsCusPsotEasTmp hlsCusPsotEasTmpP= new HlsCusPsotEasTmp();
            hlsCusPsotEasTmpP.setSourceId(item.getSourceId());
            List<HlsCusCredentialsData> hlsCusCredentialsDataList= hlsCusEasLoginMapper.selectCredentialsData(hlsCusPsotEasTmpP);

            param = JSON.toJSONString(hlsCusCredentialsDataList);

        WSaddNewVoucherFacade easwSaddNewVoucherFacade=new WSaddNewVoucherFacade();

        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();

        try {
            returnString= easwSaddNewVoucherFacade.credentialsSynchronization(hapInterfaceLine.getIftUrl(),param);
        }
        catch(Throwable throwable){
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();

        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(hapInterfaceLine.getIftUrl());

        outbound.setRequestParameter(param);
        if(stackTrace!=null||"".equals(returnString)){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
            responseCode="200";
        }
        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnString);
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息success
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());
        HlsCusHapInterfaceOutbound outboundData= hlsCusBpMasterRequestRecordsService.outboundInvokeInsert(iRequest,outbound);

        //日志信息与单据信息关联
            HlsCusEasSourceRecord hlsCusEasSourceRecord= new HlsCusEasSourceRecord();
            hlsCusEasSourceRecord.setSourceId(item.getSourceId());
            hlsCusEasSourceRecord.setSourceTable(item.getSourceTable());
            hlsCusEasSourceRecord.setSourceType("GLD_ADD");
            hlsCusEasSourceRecord.setRecordDate(new Date());
            hlsCusEasSourceRecord= hlsCusEasSourceRecordService.insertSelective(iRequest,hlsCusEasSourceRecord);

            HlsCusDocumentRecordList hlsCusDocumentRecordList = new HlsCusDocumentRecordList();
            hlsCusDocumentRecordList.setOutboundId(outboundData.getOutboundId());
            hlsCusDocumentRecordList.setSourceId(hlsCusEasSourceRecord.getSourceId());
            hlsCusDocumentRecordList.setSourceTable(hlsCusEasSourceRecord.getSourceTable());
            hlsCusDocumentRecordList.setMark("金蝶接口单据记录表");
            hlsCusDocumentRecordList.setSourceType("GLD_ADD");

            hlsCusDocumentRecordListService.insertSelective(iRequest,hlsCusDocumentRecordList);


            if(!("".equals(returnString)&&returnString!=null)){
                JSONObject jsonObject = JSONObject.parseObject(returnString);
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                String time = jsonObject.getString("time");
                hlsCusEasSourceRecord.setCode(code);
                hlsCusEasSourceRecord.setMessage(message);
                hlsCusEasSourceRecord.setTime(time);
                hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

                String voucherNumber = jsonObject.getString("voucherNumber");
                String voucherId = jsonObject.getString("voucherId");
                HlsCusJeHead jeHead= new HlsCusJeHead();
                jeHead.setJeHeadId(item.getSourceId());
                jeHead.setVoucherNumber(voucherNumber);
                jeHead.setVoucherId(voucherId);
                jeHead.setGlMsg(message);
                //200为成功  201为传输重复 也视为成功
                if("200".equals(code)||"201".equals(code)){
                    jeHead.setGlStatus("SYN");
                    requestStatus="success";
                    responseCode="200";
                }
                else{
                    jeHead.setGlStatus("SYN_FAILURE");
                    requestStatus="failure";
                    responseCode="";
                }

                jeHeadMapper.updateByPrimaryKeySelective(jeHead);
            }
            else{
                requestStatus="failure";
                responseCode="";
                hlsCusEasSourceRecord.setCode("");
                hlsCusEasSourceRecord.setMessage("接口调用出错");
                hlsCusEasSourceRecord.setTime("");
                hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

                HlsCusJeHead jeHead= new HlsCusJeHead();
                jeHead.setJeHeadId(item.getSourceId());
                jeHead.setGlStatus("SYN_FAILURE");
                jeHeadMapper.updateByPrimaryKeySelective(jeHead);
            }

            HlsCusHapInterfaceOutbound outboundUpdate= new HlsCusHapInterfaceOutbound();
            outboundUpdate.setOutboundId(outboundData.getOutboundId());
            outboundUpdate.setRequestStatus(requestStatus);
            outboundUpdate.setResponseCode(responseCode);
            hlsHapInterfaceOutboundService.updateByPrimaryKeySelective(iRequest,outboundUpdate);

        }





    }


    //金蝶凭证删除接口
    @Override
    public void easCredentialsDelete(IRequest iRequest,String voucherId, String comOrgNum,Long sourceId) {

        HlsCusEasLogin hlsCusEasLogin=new HlsCusEasLogin();
        easDoLogin(iRequest, hlsCusEasLogin);

        String stackTrace=null;
        String requestStatus=null;
        String responseCode=null;
        String returnString="";

        String param="";
        param="voucherId"+":"+voucherId+";"+"comOrgNum"+":"+comOrgNum+";";
        //获取接口配置信息地址
        HapInterfaceLine hapInterfaceLine= hlsCusBpMasterRequestRecordsService.getInterfaceUrl(iRequest,"EAS_ITFC","EAS_CREDENTIALS_DELETE");

        //传入数据封装

        WSdelVoucherByIdFacade easWSdelVoucherByIdFacade=new WSdelVoucherByIdFacade();

        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();

        try {
            returnString= easWSdelVoucherByIdFacade.credentialsDelete(hapInterfaceLine.getIftUrl(),voucherId,comOrgNum);
        }
        catch(Throwable throwable){
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();

        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(hapInterfaceLine.getIftUrl());

        outbound.setRequestParameter(param);
        if(stackTrace!=null||"".equals(returnString)){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
            responseCode="200";
        }
        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnString);
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息success
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());
        HlsCusHapInterfaceOutbound outboundData= hlsCusBpMasterRequestRecordsService.outboundInvokeInsert(iRequest,outbound);


        //日志信息与单据信息关联


            HlsCusEasSourceRecord hlsCusEasSourceRecord= new HlsCusEasSourceRecord();
            hlsCusEasSourceRecord.setSourceId(sourceId);
            hlsCusEasSourceRecord.setSourceTable("GLD_JE_HEAD");
            hlsCusEasSourceRecord.setSourceType("GLD_DELETE");
            hlsCusEasSourceRecord.setRecordDate(new Date());
            hlsCusEasSourceRecord= hlsCusEasSourceRecordService.insertSelective(iRequest,hlsCusEasSourceRecord);

            HlsCusDocumentRecordList hlsCusDocumentRecordList = new HlsCusDocumentRecordList();
            hlsCusDocumentRecordList.setOutboundId(outboundData.getOutboundId());
            hlsCusDocumentRecordList.setSourceId(hlsCusEasSourceRecord.getSourceId());
            hlsCusDocumentRecordList.setSourceTable(hlsCusEasSourceRecord.getSourceTable());
            hlsCusDocumentRecordList.setMark("金蝶接口单据记录表");
            hlsCusDocumentRecordList.setSourceType("GLD_DELETE");
            hlsCusDocumentRecordListService.insertSelective(iRequest,hlsCusDocumentRecordList);


            if(!("".equals(returnString)&&returnString!=null)){
                JSONObject jsonObject = JSONObject.parseObject(returnString);
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                String time = jsonObject.getString("time");
                hlsCusEasSourceRecord.setCode(code);
                hlsCusEasSourceRecord.setMessage(message);
                hlsCusEasSourceRecord.setTime(time);
                hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

                String glStatus="";

                if("200".equals(code)){
                    requestStatus="success";
                    responseCode="200";
                    glStatus="DELETE";
                }
                else{
                    requestStatus="failure";
                    responseCode="";
                    glStatus="DELETE_FAILURE";
                }



                HlsCusJeHead jeHead= new HlsCusJeHead();
                jeHead.setJeHeadId(sourceId);
                jeHead.setGlMsg(message);
                jeHead.setGlStatus(glStatus);
                jeHead.setVoucherNumber("");
                jeHeadMapper.updateByPrimaryKeySelective(jeHead);
            }
            else{
                requestStatus="failure";
                responseCode="";
                hlsCusEasSourceRecord.setCode("");
                hlsCusEasSourceRecord.setMessage("接口调用出错");
                hlsCusEasSourceRecord.setTime("");
                hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

                HlsCusJeHead jeHead= new HlsCusJeHead();
                jeHead.setJeHeadId(sourceId);
                jeHead.setGlStatus("DELETE_FAILURE");
                jeHead.setGlMsg("接口调用出错");
                jeHeadMapper.updateByPrimaryKeySelective(jeHead);

            }

        HlsCusHapInterfaceOutbound outboundUpdate= new HlsCusHapInterfaceOutbound();
        outboundUpdate.setOutboundId(outboundData.getOutboundId());
        outboundUpdate.setRequestStatus(requestStatus);
        outboundUpdate.setResponseCode(responseCode);
        hlsHapInterfaceOutboundService.updateByPrimaryKeySelective(iRequest,outboundUpdate);

    }



    //金蝶银行账号同步接口
    @Override
    public void easBankAccountSyn(IRequest iRequest,Long outboundId) {
        HlsCusEasLogin hlsCusEasLogin=new HlsCusEasLogin();
        easDoLogin(iRequest, hlsCusEasLogin);

        String stackTrace=null;
        String requestStatus=null;
        String responseCode=null;
        String returnString="";

        String param="";
        //获取接口配置信息地址
        HapInterfaceLine hapInterfaceLine= hlsCusBpMasterRequestRecordsService.getInterfaceUrl(iRequest,"EAS_ITFC","EAS_BANK_ACCOUNT_SYN");

        //传入数据封装
        HlsCusBankAccountHistory hlsCusBankAccountHistory= new HlsCusBankAccountHistory();
        hlsCusBankAccountHistory=hlsCusBankAccountHistoryMapper.selectMaxHistoryVersion(hlsCusBankAccountHistory);
        hlsCusBankAccountHistoryMapper.insertBnakAccountHistroryData(hlsCusBankAccountHistory);


       List<HlsCusBankAccountHistory> hlsCusBankAccountHistoryList= hlsCusBankAccountHistoryMapper.selectBankAccountData(hlsCusBankAccountHistory);

        for(HlsCusBankAccountHistory item:hlsCusBankAccountHistoryList){
           HlsCusBankAccountPostData hlsCusBankAccountPostData= new HlsCusBankAccountPostData();
            hlsCusBankAccountPostData.setNumber(item.getNumberCode());
            hlsCusBankAccountPostData.setName(item.getName());
            hlsCusBankAccountPostData.setBankAccountNumber(item.getBankAccountNumber());
            hlsCusBankAccountPostData.setCompany(item.getCompany());
            hlsCusBankAccountPostData.setBank(item.getBank());
            hlsCusBankAccountPostData.setIsByCurrency(item.getIsByCurrency());
            hlsCusBankAccountPostData.setCurrency(item.getCurrency());
            hlsCusBankAccountPostData.setAccount(item.getAccount());
            hlsCusBankAccountPostData.setProperty(item.getProperty());
            hlsCusBankAccountPostData.setOpenDate(item.getOpenDate());
            hlsCusBankAccountPostData.setAccountType(item.getAccountType());

            hlsCusBankAccountPostData.setInnerAcct("");
            hlsCusBankAccountPostData.setClassificatio("");
            hlsCusBankAccountPostData.setCtrlStrategy("");
            hlsCusBankAccountPostData.setReference("");
            hlsCusBankAccountPostData.setDescription("");
            hlsCusBankAccountPostData.setSimpleCode("");
            hlsCusBankAccountPostData.setIsDefaultReck("false");
            hlsCusBankAccountPostData.setIsReckoning("true");
            hlsCusBankAccountPostData.setIsDCPay("true");
            hlsCusBankAccountPostData.setIsForEDrafOnly("true");
            hlsCusBankAccountPostData.setIsVirtualAcct("true");
            hlsCusBankAccountPostData.setIsSetBankInterface("");
            hlsCusBankAccountPostData.setIsOnlyRead("false");
            hlsCusBankAccountPostData.setBankInterfaceType("");
            hlsCusBankAccountPostData.setAcctName("");
            hlsCusBankAccountPostData.setCountry("");
            hlsCusBankAccountPostData.setOpenArea("");
            hlsCusBankAccountPostData.setMaxPayAmount("0");
            hlsCusBankAccountPostData.setIsMotherAccount("false");
            hlsCusBankAccountPostData.setNotOutPay("false");
            hlsCusBankAccountPostData.setSubaccount("");


       param = JSON.toJSONString(hlsCusBankAccountPostData);


        WSaddBankAccountFacade easWSaddBankAccountFacade=new WSaddBankAccountFacade();

        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();

        try {
            returnString= easWSaddBankAccountFacade.bankAccountSynchronization(hapInterfaceLine.getIftUrl(),param);
        }
        catch(Throwable throwable){
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();

        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(hapInterfaceLine.getIftUrl());

        outbound.setRequestParameter(param);
        if(stackTrace!=null||"".equals(returnString)){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
            responseCode="200";
        }
        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnString);
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息success
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());
        HlsCusHapInterfaceOutbound outboundData= hlsCusBpMasterRequestRecordsService.outboundInvokeInsert(iRequest,outbound);


        //日志信息与单据信息关联
        HlsCusEasSourceRecord hlsCusEasSourceRecord= new HlsCusEasSourceRecord();

            StringBuffer sb = new StringBuffer(item.getNumberCode());
            while (true) {
                int index = sb.indexOf("CORE");
                if (index == -1) {
                    break;
                }
                sb.delete(index, index + "CORE".length());
            }

            String numberCode=sb.toString();

        hlsCusEasSourceRecord.setSourceId(Long.valueOf(numberCode));

        hlsCusEasSourceRecord.setSourceTable("CSH_BANK_ACCOUNT");
        hlsCusEasSourceRecord.setSourceType("BNAK_ACCOUNT_SYN");
        hlsCusEasSourceRecord.setRecordDate(new Date());
        hlsCusEasSourceRecord= hlsCusEasSourceRecordService.insertSelective(iRequest,hlsCusEasSourceRecord);

        HlsCusDocumentRecordList hlsCusDocumentRecordList = new HlsCusDocumentRecordList();
        hlsCusDocumentRecordList.setOutboundId(outboundData.getOutboundId());
        hlsCusDocumentRecordList.setSourceId(hlsCusEasSourceRecord.getSourceId());
        hlsCusDocumentRecordList.setSourceTable(hlsCusEasSourceRecord.getSourceTable());
        hlsCusDocumentRecordList.setMark("银行账户信息表");
        hlsCusDocumentRecordList.setSourceType("BNAK_ACCOUNT_SYN");
        hlsCusDocumentRecordListService.insertSelective(iRequest,hlsCusDocumentRecordList);


        if(!("".equals(returnString)&&returnString!=null)){
            JSONObject jsonObject = JSONObject.parseObject(returnString);
            String code = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            String time = jsonObject.getString("time");
            hlsCusEasSourceRecord.setCode(code);
            hlsCusEasSourceRecord.setMessage(message);
            hlsCusEasSourceRecord.setTime(time);
            hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);


            HlsCusCshBankAccount hlsCusCshBankAccount= new HlsCusCshBankAccount();
            hlsCusCshBankAccount.setBankAccountId(Long.valueOf(numberCode));
            hlsCusCshBankAccount.setMessage(message);
            hlsCusCshBankAccount.setTime(time);
            hlsCusCshBankAccount.setCode(code);

            hlsCusCshBankAccountMapper.updateByPrimaryKeySelective(hlsCusCshBankAccount);

            if("200".equals(code)){
                requestStatus="success";
                responseCode="200";
            }
            else{
                requestStatus="failure";
                responseCode="";
            }

        }
        else{
            requestStatus="failure";
            responseCode="";
            hlsCusEasSourceRecord.setCode("");
            hlsCusEasSourceRecord.setMessage("接口调用出错");
            hlsCusEasSourceRecord.setTime("");
            hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

            HlsCusCshBankAccount hlsCusCshBankAccount= new HlsCusCshBankAccount();
            hlsCusCshBankAccount.setBankAccountId(Long.valueOf(numberCode));
            hlsCusCshBankAccount.setMessage("接口调用出错");
            hlsCusCshBankAccount.setCode("FAILURE");
            hlsCusCshBankAccountMapper.updateByPrimaryKeySelective(hlsCusCshBankAccount);
        }

            HlsCusHapInterfaceOutbound outboundUpdate= new HlsCusHapInterfaceOutbound();
            outboundUpdate.setOutboundId(outboundData.getOutboundId());
            outboundUpdate.setRequestStatus(requestStatus);
            outboundUpdate.setResponseCode(responseCode);
            hlsHapInterfaceOutboundService.updateByPrimaryKeySelective(iRequest,outboundUpdate);
        }


    }


    //金蝶财务对账同步接口
    @Override
    public void easCheckAccountSyn(IRequest iRequest,Long outboundId) {
        HlsCusEasLogin hlsCusEasLogin=new HlsCusEasLogin();
        easDoLogin(iRequest, hlsCusEasLogin);

        String stackTrace=null;
        String requestStatus=null;
        String responseCode=null;
        String returnString="";

        String param="";
        //获取接口配置信息地址
        HapInterfaceLine hapInterfaceLine= hlsCusBpMasterRequestRecordsService.getInterfaceUrl(iRequest,"EAS_ITFC","EAS_CHECK_ACCOUNT_SYN");


        SimpleDateFormat dfCheckDate = new SimpleDateFormat("yyyy-MM-dd");
        String checkDate = dfCheckDate.format(new Date());

        HlsCusCoreAccountHistory hlsCusCoreAccountHistoryP=new HlsCusCoreAccountHistory();
        hlsCusCoreAccountHistoryP.setCheckDate(checkDate);
        //如果重复跑先删除当天数据
        hlsCusCoreAccountHistoryMapper.deleteCurrentHistroryData(hlsCusCoreAccountHistoryP);
        hlsCusCoreAccountHistoryMapper.deleteCurrentEasHistroryData(hlsCusCoreAccountHistoryP);
        hlsCusCoreAccountHistoryMapper.deleteCurrentHlsEasHistroryData(hlsCusCoreAccountHistoryP);

        //传入数据封装
        hlsCusCoreAccountHistoryMapper.insertCheckAccountHistroryData(hlsCusCoreAccountHistoryP);
        hlsCusCoreAccountHistoryMapper.insertHlsCheckAccountHistroryData(hlsCusCoreAccountHistoryP);

        HlsCusAccountHistory hlsCusAccountHistoryP= new HlsCusAccountHistory();
        hlsCusAccountHistoryP.setCheckDate(checkDate);

        //  List<HlsCusAccountHistory> hlsCusAccountHistoryList=  hlsCusAccountHistoryMapper.selectCheckAccountData(hlsCusAccountHistoryP);

        //报文结构按照四块分别封装

        //1合同封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryContractList=  hlsCusAccountHistoryMapper.selectCheckContractData(hlsCusAccountHistoryP);
        //2科目封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryGldAccountList=  hlsCusAccountHistoryMapper.selectCheckGldAccountData(hlsCusAccountHistoryP);
        //3币种封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryCurrencyList=  hlsCusAccountHistoryMapper.selectCheckCurrencyData(hlsCusAccountHistoryP);
        //4组织封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryCompanyList=  hlsCusAccountHistoryMapper.selectCheckCompanyData(hlsCusAccountHistoryP);

        if(hlsCusAccountHistoryContractList.size()==0){
            return;
        }

        StringBuffer strBuffer=new StringBuffer();
        strBuffer.append("{");
        //合同部分
        strBuffer.append("\"contractNos\":"+"[");
        String contractString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryContractList){
            contractString=contractString+"{\"contractNo\":"+"\""+item1.getContractNo()+"\""+"},";
        }
        contractString= contractString.substring(0,contractString.length()-1);
        strBuffer.append(contractString);
        strBuffer.append("],");

        //科目部分
        strBuffer.append("\"acctNums\":"+"[");
        String gldAccountString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryGldAccountList){
            gldAccountString=gldAccountString+"{\"acctNum\":"+"\""+item1.getAcctNum()+"\""+"},";
        }
        gldAccountString= gldAccountString.substring(0,gldAccountString.length()-1);
        strBuffer.append(gldAccountString);
        strBuffer.append("],");
        //币种部分
        strBuffer.append("\"currencys\":"+"[");
        String currencyString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryCurrencyList){
            currencyString=currencyString+"{\"currency\":"+"\""+item1.getCompanynumber()+"\""+"},";
        }
        currencyString= currencyString.substring(0,currencyString.length()-1);
        strBuffer.append(currencyString);
        strBuffer.append("],");

        //组织部分
        strBuffer.append("\"companyNumbers\":"+"[");
        String companyString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryCompanyList){
            companyString=companyString+"{\"companyNumber\":"+"\""+item1.getAccountNumber()+"\""+"},";
        }
        companyString= companyString.substring(0,companyString.length()-1);
        strBuffer.append(companyString);
        strBuffer.append("],");

        strBuffer.append("\"date\":"+"\""+checkDate+"\"");
        strBuffer.append("}");
        param=strBuffer.toString();


        // for(HlsCusAccountHistory item:hlsCusAccountHistoryList){
//            HlsCusAccountAndContractData hlsCusAccountAndContractData= new HlsCusAccountAndContractData();
//            hlsCusAccountAndContractData.setAccountNumber(item.getAccountNumber());
//            hlsCusAccountAndContractData.setAcctNum(item.getAcctNum());
//            hlsCusAccountAndContractData.setContractNo(item.getContractNo());
//            hlsCusAccountAndContractData.setDate(item.getBizDate());



        //   param = JSON.toJSONString(hlsCusAccountAndContractData);


        WSReconciliationCoreFacade easWSReconciliationCoreFacade=new WSReconciliationCoreFacade();

        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();

        try {
            returnString= easWSReconciliationCoreFacade.checkAccountSyn(hapInterfaceLine.getIftUrl(),param);
        }
        catch(Throwable throwable){
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();

        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(hapInterfaceLine.getIftUrl());

        outbound.setRequestParameter(param);
        if(stackTrace!=null||"".equals(returnString)){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
            responseCode="200";
        }
        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnString);
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息success
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());
        HlsCusHapInterfaceOutbound outboundData= hlsCusBpMasterRequestRecordsService.outboundInvokeInsert(iRequest,outbound);


        //日志信息与单据信息关联
        HlsCusEasSourceRecord hlsCusEasSourceRecord= new HlsCusEasSourceRecord();
        hlsCusEasSourceRecord.setSourceId(-1L);
        hlsCusEasSourceRecord.setSourceTable("EAS_CORE_ACCOUNT_HISTORY");
        hlsCusEasSourceRecord.setSourceType("CHECK_ACCOUNT");

        //创建SimpleDateFormat对象实例并定义好转换格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date recordDate = null;
        try {
            // 注意格式需要与上面一致，不然会出现异常
            recordDate = sdf.parse(checkDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        hlsCusEasSourceRecord.setRecordDate(recordDate);
        hlsCusEasSourceRecord= hlsCusEasSourceRecordService.insertSelective(iRequest,hlsCusEasSourceRecord);

        HlsCusDocumentRecordList hlsCusDocumentRecordList = new HlsCusDocumentRecordList();
        hlsCusDocumentRecordList.setOutboundId(outboundData.getOutboundId());
        hlsCusDocumentRecordList.setSourceId(hlsCusEasSourceRecord.getSourceId());
        hlsCusDocumentRecordList.setSourceTable(hlsCusEasSourceRecord.getSourceTable());
        hlsCusDocumentRecordList.setMark("对账历史记录信息表");
        hlsCusDocumentRecordList.setSourceType("CHECK_ACCOUNT");
        hlsCusDocumentRecordListService.insertSelective(iRequest,hlsCusDocumentRecordList);


        if(!("".equals(returnString)&&returnString!=null)){
            // JSONObject jsonObject = JSONObject.parseObject(returnString);
            JSONObject jsonObject = JSON.parseObject(returnString);
            String code = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            String time = jsonObject.getString("time");
            hlsCusEasSourceRecord.setCode(code);
            //hlsCusEasSourceRecord.setMessage(message);
            hlsCusEasSourceRecord.setTime(time);
            hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

            String data=jsonObject.getString("data");

            if(data!=null){
                JSONArray array = JSON.parseArray(data);

                for (int i = 0; i < array.size(); i++) {
                    //JSONArray中的数据转换为String类型需要在外边加"";不然会报出类型强转异常！
                    String str = array.get(i)+"";
                    JSONObject object = JSON.parseObject(str);

                    String acctName = object.getString("acctName");
                    String acctNum = object.getString("acctNum");
                    String accountNumber = object.getString("companyNumber");
                    String contractNo = object.getString("contractNo");
                    String date = object.getString("date");
                    String currency = object.getString("currency");

                    HlsCusAccountHistory hlsCusAccountHistory=new HlsCusAccountHistory();

                    hlsCusAccountHistory.setAcctNum(acctNum);

                    String afterBalance = object.getString("afterBalance");
                    hlsCusAccountHistory.setAfterBalance(Double.valueOf(afterBalance));

                    String dayBalance = object.getString("dayBalance");
                    hlsCusAccountHistory.setDayBalance(Double.valueOf(dayBalance));

                    String beforeBalance = object.getString("beforeBalance");
                    hlsCusAccountHistory.setBeforeBalance(Double.valueOf(beforeBalance));

                    hlsCusAccountHistory.setCompanynumber(currency);//币种
                    hlsCusAccountHistory.setAccountNumber(accountNumber);//组织
                    hlsCusAccountHistory.setAcctName(acctName);


                    hlsCusAccountHistory.setContractNo(contractNo);
                    hlsCusAccountHistory.setBizDate(date);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String currentdate = df.format(new Date());
                    hlsCusAccountHistory.setCheckDate(checkDate);
                    hlsCusAccountHistory.setCode(code);
                    hlsCusAccountHistory.setTime(time);
                    hlsCusAccountHistoryMapper.insertSelective(hlsCusAccountHistory);
                }
            }

        }

        HlsCusHapInterfaceOutbound outboundUpdate= new HlsCusHapInterfaceOutbound();
        outboundUpdate.setOutboundId(outboundData.getOutboundId());
        outboundUpdate.setRequestStatus(requestStatus);
        outboundUpdate.setResponseCode(responseCode);
        hlsHapInterfaceOutboundService.updateByPrimaryKeySelective(iRequest,outboundUpdate);

        //}



    }


    public void easDataSyn(IRequest iRequest, Long contractId) {

        HlsCusEasSourceRecord hlsCusEasSourceRecord = new HlsCusEasSourceRecord();
        hlsCusEasSourceRecord.setContractId(contractId);
        //客户资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList01 = hlsCusEasSourceRecordMapper.selectEasType01(hlsCusEasSourceRecord);
        //供应商资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList04 = hlsCusEasSourceRecordMapper.selectEasType04(hlsCusEasSourceRecord);
        //租赁合同资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList05 = hlsCusEasSourceRecordMapper.selectEasType05(hlsCusEasSourceRecord);
        //部门资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordListbm = hlsCusEasSourceRecordMapper.selectEasTypebm(hlsCusEasSourceRecord);

        //往来单位
        //List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList11 = hlsCusEasSourceRecordMapper.selectEasType11(hlsCusEasSourceRecord);


        //首先调用金蝶登录接口
        HlsCusEasLogin hlsCusEasLogin = new HlsCusEasLogin();
        hlsCusEasLogin = hlsCusEasLoginService.easDoLogin(iRequest, hlsCusEasLogin);

        if (hlsCusEasLogin != null) {
            if (hlsCusEasLogin.getSessionId() != null) {//说明登录成功
                //客户资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList01) {
                    //首先查询出拼接的客户名称

                    //然后根据这个名称去合同表中查询是否存在如果存在直接用已存在的金蝶编号 如果不存在则自己编一个编号传过去

                    String easBpCode="";
                    if(item.getEasCode()!=null){
                        easBpCode=  item.getEasCode();
                    }
                    else{
                        easBpCode=  item.getSourceNumber();
                    }
                    HlsCusConContract hlsCusConContract= new HlsCusConContract();
                    hlsCusConContract.setEasBpCode(easBpCode);
                    hlsCusConContract.setEasBpName(item.getSourceName());
                    hlsCusConContract.setContractId(item.getSourceId());
                    hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);

                  //if(item.getEasCode()==null){
                        HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                         hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                         hlsCusEasBasicData.setNumber(easBpCode);
                         hlsCusEasBasicData.setName(item.getSourceName());
                         hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                  //}
                }
                //供应商资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList04) {

                    String sourceNumber=item.getSourceNumber();
                    //首先需要
                    if(sourceNumber==null){
                        HlsCusBpMaster hlsCusBpMaster=new HlsCusBpMaster();
                        hlsCusBpMaster.setBpId(item.getSourceId());
                        hlsCusBpMaster.setEasBpCode("HX"+item.getSourceId());
                        hlsCusBpMasterMapper.updateByPrimaryKeySelective(hlsCusBpMaster);
                        sourceNumber="HX"+item.getSourceId();
                    }

                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    hlsCusEasBasicData.setNumber(sourceNumber);
                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }

                //租赁合同资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList05) {
                    //首先对eas编号赋值
                    if(item.getEasCode()==null){
                        HlsCusConContract hlsCusConContract= new HlsCusConContract();
                        hlsCusConContract.setEasCode("HX"+item.getSourceId());
                        hlsCusConContract.setContractId(item.getSourceId());
                        hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);

                    }

                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    if(item.getEasCode()==null){
                        hlsCusEasBasicData.setNumber("HX"+item.getSourceId());
                    }
                    else{
                        hlsCusEasBasicData.setNumber(item.getSourceNumber());
                    }

                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }

                //部门资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordListbm) {
                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    hlsCusEasBasicData.setNumber(item.getSourceNumber());
                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }

                //往来单位资料
//                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList11) {
//                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
//                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
//                    hlsCusEasBasicData.setNumber(item.getSourceNumber());
//                    hlsCusEasBasicData.setName(item.getSourceName());
//                    hlsCusEasBasicData.setDescription(item.getDescription());
//                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
//                }

            }
        }
    }




    public void easVenderDataSyn(IRequest iRequest, Long paymentReqId) {

        HlsCusEasSourceRecord hlsCusEasSourceRecord = new HlsCusEasSourceRecord();
        hlsCusEasSourceRecord.setPaymentReqId(paymentReqId);
        //客户资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordListPay04 = hlsCusEasSourceRecordMapper.selectEasTypePay04(hlsCusEasSourceRecord);



        //首先调用金蝶登录接口
        HlsCusEasLogin hlsCusEasLogin = new HlsCusEasLogin();
        hlsCusEasLogin = hlsCusEasLoginService.easDoLogin(iRequest, hlsCusEasLogin);

        if (hlsCusEasLogin != null) {
            if (hlsCusEasLogin.getSessionId() != null) {//说明登录成功
             for(HlsCusEasSourceRecord item:hlsCusEasSourceRecordListPay04){
                 if("N".equals(item.getIfFromContract())){//付款对象不是取自合同而是根据手输的账户名
                  if(item.getEasPayCode()==null &&item.getEasPayBpCode()==null){//通过账户名在付款行表上和商业伙伴表上未匹配到
                      HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                      hlsCusEasBasicData.setTypeNumber("04");
                      hlsCusEasBasicData.setNumber("HX"+item.getSourceId()+"P");
                      hlsCusEasBasicData.setName(item.getBpBankAccountName());
                      hlsCusEasBasicData.setDescription(item.getBpBankAccountName());

                      HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
                      hlsCusCshPaymentReqLn.setPaymentReqLnId(item.getSourceId());
                      hlsCusCshPaymentReqLn.setEasCode("HX"+item.getSourceId()+"P");
                      hlsCusCshPaymentReqLnMapper.updateByPrimaryKeySelective(hlsCusCshPaymentReqLn);

                      hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, "CSH_PAYMENT_REQ_LN", item.getSourceId());

                  }

                     if(item.getEasPayCode()==null &&item.getEasPayBpCode()!=null){//通过账户名在付款行表上未匹配到，商业伙伴表上匹配到了
                         HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
                         hlsCusCshPaymentReqLn.setPaymentReqLnId(item.getSourceId());
                         hlsCusCshPaymentReqLn.setEasCode(item.getEasPayBpCode());
                         hlsCusCshPaymentReqLnMapper.updateByPrimaryKeySelective(hlsCusCshPaymentReqLn);
                     }

                     if(item.getEasPayCode()!=null){//通过账户名在付款行表上匹配到
                         HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
                         hlsCusCshPaymentReqLn.setPaymentReqLnId(item.getSourceId());
                         hlsCusCshPaymentReqLn.setEasCode(item.getEasPayCode());
                         hlsCusCshPaymentReqLnMapper.updateByPrimaryKeySelective(hlsCusCshPaymentReqLn);

                     }
                 }
                 else{//付款对象是取自合同

                     if(item.getEasBpCode()==null){//付款对象对应的金蝶编码没有
                         //首先需要
                         HlsCusBpMaster hlsCusBpMaster=new HlsCusBpMaster();
                         hlsCusBpMaster.setBpId(item.getBpId());
                         hlsCusBpMaster.setEasBpCode("HX"+item.getBpId());
                         hlsCusBpMasterMapper.updateByPrimaryKeySelective(hlsCusBpMaster);

                         HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                         hlsCusEasBasicData.setTypeNumber("04");
                         hlsCusEasBasicData.setNumber("HX"+item.getBpId());
                         hlsCusEasBasicData.setName(item.getBpName());
                         hlsCusEasBasicData.setDescription(item.getBpName());

                         hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, "HLS_BP_MASTER", item.getBpId());

                     }
                     else{
                         if(item.getEasBpCount()==0){
                             HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                             hlsCusEasBasicData.setTypeNumber("04");
                             hlsCusEasBasicData.setNumber(item.getEasBpCode());
                             hlsCusEasBasicData.setName(item.getBpName());
                             hlsCusEasBasicData.setDescription(item.getBpName());

                             hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, "HLS_BP_MASTER", item.getBpId());
                         }
                     }


                 }
             }
            }
        }
    }


    //金蝶财务对账同步接口
    @Override
    public void easCheckAccountSynNew(IRequest iRequest,String checkDate) {
        HlsCusEasLogin hlsCusEasLogin=new HlsCusEasLogin();
        easDoLogin(iRequest, hlsCusEasLogin);

        String stackTrace=null;
        String requestStatus=null;
        String responseCode=null;
        String returnString="";

        String param="";
        //获取接口配置信息地址
        HapInterfaceLine hapInterfaceLine= hlsCusBpMasterRequestRecordsService.getInterfaceUrl(iRequest,"EAS_ITFC","EAS_CHECK_ACCOUNT_SYN");

        HlsCusCoreAccountHistory hlsCusCoreAccountHistoryP=new HlsCusCoreAccountHistory();
        hlsCusCoreAccountHistoryP.setCheckDate(checkDate);
        //如果重复跑先删除当天数据
        hlsCusCoreAccountHistoryMapper.deleteCurrentHistroryData(hlsCusCoreAccountHistoryP);
        hlsCusCoreAccountHistoryMapper.deleteCurrentEasHistroryData(hlsCusCoreAccountHistoryP);
        hlsCusCoreAccountHistoryMapper.deleteCurrentHlsEasHistroryData(hlsCusCoreAccountHistoryP);

        //传入数据封装
        hlsCusCoreAccountHistoryMapper.insertCheckAccountHistroryData(hlsCusCoreAccountHistoryP);
        hlsCusCoreAccountHistoryMapper.insertHlsCheckAccountHistroryData(hlsCusCoreAccountHistoryP);

        HlsCusAccountHistory hlsCusAccountHistoryP= new HlsCusAccountHistory();
        hlsCusAccountHistoryP.setCheckDate(checkDate);

      //  List<HlsCusAccountHistory> hlsCusAccountHistoryList=  hlsCusAccountHistoryMapper.selectCheckAccountData(hlsCusAccountHistoryP);

        //报文结构按照四块分别封装

        //1合同封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryContractList=  hlsCusAccountHistoryMapper.selectCheckContractData(hlsCusAccountHistoryP);
        //2科目封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryGldAccountList=  hlsCusAccountHistoryMapper.selectCheckGldAccountData(hlsCusAccountHistoryP);
        //3币种封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryCurrencyList=  hlsCusAccountHistoryMapper.selectCheckCurrencyData(hlsCusAccountHistoryP);
        //4组织封装
        List<HlsCusAccountHistory> hlsCusAccountHistoryCompanyList=  hlsCusAccountHistoryMapper.selectCheckCompanyData(hlsCusAccountHistoryP);

        if(hlsCusAccountHistoryContractList.size()==0){
          return;
        }

        StringBuffer strBuffer=new StringBuffer();
        strBuffer.append("{");
        //合同部分
        strBuffer.append("\"contractNos\":"+"[");
        String contractString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryContractList){
            contractString=contractString+"{\"contractNo\":"+"\""+item1.getContractNo()+"\""+"},";
        }
        contractString= contractString.substring(0,contractString.length()-1);
        strBuffer.append(contractString);
        strBuffer.append("],");

        //科目部分
        strBuffer.append("\"acctNums\":"+"[");
        String gldAccountString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryGldAccountList){
            gldAccountString=gldAccountString+"{\"acctNum\":"+"\""+item1.getAcctNum()+"\""+"},";
        }
        gldAccountString= gldAccountString.substring(0,gldAccountString.length()-1);
        strBuffer.append(gldAccountString);
        strBuffer.append("],");
        //币种部分
        strBuffer.append("\"currencys\":"+"[");
        String currencyString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryCurrencyList){
            currencyString=currencyString+"{\"currency\":"+"\""+item1.getCompanynumber()+"\""+"},";
        }
        currencyString= currencyString.substring(0,currencyString.length()-1);
        strBuffer.append(currencyString);
        strBuffer.append("],");

        //组织部分
        strBuffer.append("\"companyNumbers\":"+"[");
        String companyString="";
        for(HlsCusAccountHistory item1:hlsCusAccountHistoryCompanyList){
            companyString=companyString+"{\"companyNumber\":"+"\""+item1.getAccountNumber()+"\""+"},";
        }
        companyString= companyString.substring(0,companyString.length()-1);
        strBuffer.append(companyString);
        strBuffer.append("],");

        strBuffer.append("\"date\":"+"\""+checkDate+"\"");
        strBuffer.append("}");
        param=strBuffer.toString();


            WSReconciliationCoreFacade easWSReconciliationCoreFacade=new WSReconciliationCoreFacade();

            Date endDate=null;
            Date startDate=null;
            long start=0l;
            long end=0l;
            start = System.currentTimeMillis();
            startDate=new Date();

            try {
                returnString= easWSReconciliationCoreFacade.checkAccountSyn(hapInterfaceLine.getIftUrl(),param);
            }
            catch(Throwable throwable){
                stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
                throwable.printStackTrace();
            }
            end = System.currentTimeMillis();
            endDate=new Date();

            //日志信息插入
            HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
            outbound.setInterfaceName(hapInterfaceLine.getLineName());
            outbound.setInterfaceUrl(hapInterfaceLine.getIftUrl());

            outbound.setRequestParameter(param);
            if(stackTrace!=null||"".equals(returnString)){
                requestStatus="failure";
            }
            else{
                requestStatus="success";
                responseCode="200";
            }
            outbound.setRequestStatus(requestStatus);
            outbound.setResponseContent(returnString);
            outbound.setRequestTime(new Date());//请求时间
            outbound.setResponseTime(end-start);//响应时间
            outbound.setStartDate(startDate);//开始时间
            outbound.setEndDate(endDate);//结束时间
            outbound.setStackTrace(stackTrace);//错误信息success
            outbound.setResponseCode(responseCode);//请求code
            outbound.setLineId(hapInterfaceLine.getLineId());
            HlsCusHapInterfaceOutbound outboundData= hlsCusBpMasterRequestRecordsService.outboundInvokeInsert(iRequest,outbound);


            //日志信息与单据信息关联
            HlsCusEasSourceRecord hlsCusEasSourceRecord= new HlsCusEasSourceRecord();
            hlsCusEasSourceRecord.setSourceId(-1L);
            hlsCusEasSourceRecord.setSourceTable("EAS_CORE_ACCOUNT_HISTORY");
            hlsCusEasSourceRecord.setSourceType("CHECK_ACCOUNT");

        //创建SimpleDateFormat对象实例并定义好转换格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date recordDate = null;
        try {
            // 注意格式需要与上面一致，不然会出现异常
            recordDate = sdf.parse(checkDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

            hlsCusEasSourceRecord.setRecordDate(recordDate);
            hlsCusEasSourceRecord= hlsCusEasSourceRecordService.insertSelective(iRequest,hlsCusEasSourceRecord);

            HlsCusDocumentRecordList hlsCusDocumentRecordList = new HlsCusDocumentRecordList();
            hlsCusDocumentRecordList.setOutboundId(outboundData.getOutboundId());
            hlsCusDocumentRecordList.setSourceId(hlsCusEasSourceRecord.getSourceId());
            hlsCusDocumentRecordList.setSourceTable(hlsCusEasSourceRecord.getSourceTable());
            hlsCusDocumentRecordList.setMark("对账历史记录信息表");
            hlsCusDocumentRecordList.setSourceType("CHECK_ACCOUNT");
            hlsCusDocumentRecordListService.insertSelective(iRequest,hlsCusDocumentRecordList);


            if(!("".equals(returnString)&&returnString!=null)){
                // JSONObject jsonObject = JSONObject.parseObject(returnString);
                JSONObject jsonObject = JSON.parseObject(returnString);
                String code = jsonObject.getString("code");
                 String message = jsonObject.getString("message");
                String time = jsonObject.getString("time");
                hlsCusEasSourceRecord.setCode(code);
                //hlsCusEasSourceRecord.setMessage(message);
                hlsCusEasSourceRecord.setTime(time);
                hlsCusEasSourceRecordService.updateByPrimaryKeySelective(iRequest,hlsCusEasSourceRecord);

                String data=jsonObject.getString("data");

                if(data!=null){
                    JSONArray array = JSON.parseArray(data);

                    for (int i = 0; i < array.size(); i++) {
                        //JSONArray中的数据转换为String类型需要在外边加"";不然会报出类型强转异常！
                        String str = array.get(i)+"";
                        JSONObject object = JSON.parseObject(str);

                        String acctName = object.getString("acctName");
                        String acctNum = object.getString("acctNum");
                        String accountNumber = object.getString("companyNumber");
                        String contractNo = object.getString("contractNo");
                        String date = object.getString("date");
                        String currency = object.getString("currency");

                        HlsCusAccountHistory hlsCusAccountHistory=new HlsCusAccountHistory();

                        hlsCusAccountHistory.setAcctNum(acctNum);

                        String afterBalance = object.getString("afterBalance");
                        hlsCusAccountHistory.setAfterBalance(Double.valueOf(afterBalance));

                        String dayBalance = object.getString("dayBalance");
                        hlsCusAccountHistory.setDayBalance(Double.valueOf(dayBalance));

                        String beforeBalance = object.getString("beforeBalance");
                        hlsCusAccountHistory.setBeforeBalance(Double.valueOf(beforeBalance));

                        hlsCusAccountHistory.setCompanynumber(currency);//币种
                        hlsCusAccountHistory.setAccountNumber(accountNumber);//组织
                        hlsCusAccountHistory.setAcctName(acctName);


                        hlsCusAccountHistory.setContractNo(contractNo);
                        hlsCusAccountHistory.setBizDate(date);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String currentdate = df.format(new Date());
                        hlsCusAccountHistory.setCheckDate(checkDate);
                        hlsCusAccountHistory.setCode(code);
                        hlsCusAccountHistory.setTime(time);
                        hlsCusAccountHistoryMapper.insertSelective(hlsCusAccountHistory);
                    }
                }

            }

            HlsCusHapInterfaceOutbound outboundUpdate= new HlsCusHapInterfaceOutbound();
            outboundUpdate.setOutboundId(outboundData.getOutboundId());
            outboundUpdate.setRequestStatus(requestStatus);
            outboundUpdate.setResponseCode(responseCode);
            hlsHapInterfaceOutboundService.updateByPrimaryKeySelective(iRequest,outboundUpdate);


    }
}