package com.hand.hls.fct.service.impl;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.attachment.mapper.AttachCategoryMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.mapper.HlsCusFctQuotationCashflowMapper;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.fct.service.HlsCusPrjContractDocxService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.plm.nm.dto.PlmNoticeSent;
import com.hand.hls.plm.nm.service.PlmNoticeSentService;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Service
public class HlsCusFctQuotationCashflowServiceImpl extends BaseServiceImpl<HlsCusFctQuotationCashflow> implements HlsCusFctQuotationCashflowService {


    @Autowired
    private HlsCusFctQuotationCashflowService service;
    @Autowired
    private HlsCusFctQuotationCashflowMapper mapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    private PlmNoticeSentService plmNoticeSentService;
    @Autowired
    private HlsDocFileTempletMapper hlsDocFileTempletMapper;
    @Autowired
    private HlsCusPrjContractDocxService docxService;


    private static final String PLM_NOTICE_PRINT_PAY = "PLM_NOTICE_PRINT_PAY";
    private static final String PLM_NOTICE_PRINT_DEFAULT = "PLM_NOTICE_PRINT_DEFAULT";
    private static final String PLM_NOTICE_PRINT_ACCOUNT_RECEIVABLE = "PLM_NOTICE_PRINT_ACCOUNT_RECEIVABLE";
    private static final String PLM_NOTICE_PRINT_FEE = "PLM_NOTICE_PRINT_FEE";

    private static final String DEFAULT = "DEFAULT";
    private static final String PAY = "PAY";
    private static final String FEE = "FEE";
    private static final String ACCOUNTA_RECEIVABLE = "ACCOUNTA_RECEIVABLE";



    Calendar calendar = Calendar.getInstance();
    /*for金额计算*/
    DecimalFormat df = new DecimalFormat("0.00");
    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    @Override
    public List<HlsCusFctQuotationCashflow> selectNoticePrint(IRequest request, HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow, Integer page, Integer pageSize) {
        //获取部门信息
        Long companyId = request.getCompanyId();
        String employeeCode = request.getEmployeeCode();
        //运营岗
        HlsCusEmployee employeePAY = new HlsCusEmployee();
        List<String> payList = new ArrayList<>();
        payList.add("00320");
        payList.add("05530");

        employeePAY.setPositionCodeList(payList);
        employeePAY.setCompanyId(companyId);
        employeePAY.setEmployeeCode(employeeCode);
        List<HlsCusEmployee> employeePayList = hlsCusEmployeeMapper.selectEmployeeCodeByPositionCodeList(employeePAY);

        //资产管理岗
        HlsCusEmployee employeeDEFAULT = new HlsCusEmployee();
        List<String> defaultList = new ArrayList<>();
        defaultList.add("00340");
        defaultList.add("05540");

        employeeDEFAULT.setPositionCodeList(defaultList);
        employeeDEFAULT.setCompanyId(companyId);
        employeeDEFAULT.setEmployeeCode(employeeCode);
        List<HlsCusEmployee> employeeDefaultList = hlsCusEmployeeMapper.selectEmployeeCodeByPositionCodeList(employeeDEFAULT);

        if (employeePayList.size()>0&&employeeDefaultList.size()<=0){
            hlsCusFctQuotationCashflow.setNoticeFlag("Y");
        }

        if (employeePayList.size()<=0&&employeeDefaultList.size()>0){
            hlsCusFctQuotationCashflow.setNoticeFlag("N");
        }

        PageHelper.startPage(page, pageSize);
        return mapper.selectNoticePrint(hlsCusFctQuotationCashflow);

    }


    @Override
    public ResponseData downloadNoticePrintFile(List<HlsCusFctQuotationCashflow> fctQuotationCashflows, IRequest requestContext, HttpServletRequest request, HttpServletResponse response) {

        ResponseData responseData = new ResponseData();

        //List<HlsCusFctQuotationCashflow> fctQuotationCashflows = JSON.parseArray(jsonStr, HlsCusFctQuotationCashflow.class);

        List<String> cashflowIdList=new ArrayList<>();

        //解析前台传入的数据
        if (CollectionUtils.isNotEmpty(fctQuotationCashflows))
        {
            for (HlsCusFctQuotationCashflow cashflow:fctQuotationCashflows) {
                if (null!=cashflow)
                {
                    cashflowIdList.add(cashflow.getDocumentCategory()+cashflow.getQuotationCashflowId());
                }
            }
        }
        else {
            throw new RuntimeException("参数不能为空!");
        }
        List<HlsCusFctQuotationCashflow> cashflowListPay= new ArrayList<>();
        List<HlsCusFctQuotationCashflow> cashflowListDefault = new ArrayList<>();
        List<HlsCusFctQuotationCashflow> cashflowListFee = new ArrayList<>();
        List<HlsCusFctQuotationCashflow> cashflowListAccount = new ArrayList<>();
        //生成通知书 同期数 客户 合并
        List<HlsCusFctQuotationCashflow> quotationCashflows = mapper.selectFctContractTimesBpInfo(cashflowIdList);
        //商业伙伴赋值
        for (HlsCusFctQuotationCashflow fctQuotationCashflow : quotationCashflows) {
            if (StringUtils.isEmpty(fctQuotationCashflow.getBpName()))
            {
                for (HlsCusFctQuotationCashflow quotationCashflow : fctQuotationCashflows) {
                    if (quotationCashflow.getQuotationCashflowId().equals(fctQuotationCashflow.getQuotationCashflowId())){
                        fctQuotationCashflow.setBpName(quotationCashflow.getBpName());
                        break;
                    }
                }
            }
        }
        for(HlsCusFctQuotationCashflow quotationCashflow:quotationCashflows){
            if("N".equals(quotationCashflow.getPrintStatus())) {
                PlmNoticeSent plmNoticeSent=new PlmNoticeSent();
                plmNoticeSent.setContractId(quotationCashflow.getContractId());
                plmNoticeSent.setDocumentStyle(quotationCashflow.getDocumentCategory());
                plmNoticeSent.setContractNumber(quotationCashflow.getContractNumber());
                plmNoticeSent.setBpId(quotationCashflow.getBpId());
                plmNoticeSent.setDocumentCategory("PLM_NM_NOTICE");
                plmNoticeSent.setDocumentType("NM");
                plmNoticeSent.setNoticeType(quotationCashflow.getNoticeType());
                plmNoticeSent.setTimes(quotationCashflow.getTimes());
                //plmNoticeSent.setCourierCompany("顺丰");
                plmNoticeSent.setRecipient(quotationCashflow.getPersonName());
                plmNoticeSent.setRecipientAddress(quotationCashflow.getAddress());
                plmNoticeSent.setRecipientCall(quotationCashflow.getCellPhone());
                plmNoticeSentService.insertSelective(requestContext, plmNoticeSent);
            }

            if (PAY.equalsIgnoreCase(quotationCashflow.getNoticeType())){
                cashflowListPay.add(quotationCashflow);
            }
            if(DEFAULT.equalsIgnoreCase(quotationCashflow.getNoticeType())){
                cashflowListDefault.add(quotationCashflow);
            }
            if(FEE.equalsIgnoreCase(quotationCashflow.getNoticeType())){
                cashflowListFee.add(quotationCashflow);
            }
            if (ACCOUNTA_RECEIVABLE.equalsIgnoreCase(quotationCashflow.getNoticeType())) {
                cashflowListAccount.add(quotationCashflow);
            }
        }

        //获取文件暂存目录
        /*String temporarilyType = "PLM_NOTICE_PRINT";
        String temporarilyPath = "/u01/notice";
        AttachCategory attachCategoryTemp = new AttachCategory();
        attachCategoryTemp.setSourceType(temporarilyType);
        List<AttachCategory> attachCategoryList = attachCategoryMapper.select(attachCategoryTemp);
        if (attachCategoryList.size() > 0) {
            attachCategoryTemp = attachCategoryList.get(0);
            temporarilyPath = attachCategoryTemp.getCategoryPath();
        }


        List<String> pathList = new ArrayList<>();
        //租金支付通知书
        if(cashflowListPay.size()>0){
            List<String>  responseDataPay= createFile(cashflowListPay,requestContext,PLM_NOTICE_PRINT_PAY,temporarilyPath, request, response);
            if (responseDataPay.size()>0){
                pathList.addAll(responseDataPay);
            }
        }

        //保理
        if (cashflowListAccount.size() > 0) {
            List<String> responseDataAccount = createFile(cashflowListAccount, requestContext, PLM_NOTICE_PRINT_ACCOUNT_RECEIVABLE, temporarilyPath, request, response);
            if (responseDataAccount.size() > 0) {
                pathList.addAll(responseDataAccount);
            }
        }

        //迟延违约金通知书
        if(cashflowListDefault.size()>0){
            List<String>  responseDataDefault = createFile(cashflowListDefault,requestContext,PLM_NOTICE_PRINT_DEFAULT,temporarilyPath, request, response);
            if (responseDataDefault.size()>0){
                pathList.addAll(responseDataDefault);
            }
        }

        //咨询服务费通知书
        if(cashflowListFee.size()>0){
            List<String>  responseDataFee = createFile(cashflowListFee,requestContext,PLM_NOTICE_PRINT_FEE,temporarilyPath, request, response);
            if (responseDataFee.size()>0){
                pathList.addAll(responseDataFee);
            }
        }*/

       // if (pathList.size() > 0) {
            //downloadFile(requestContext, request, response, pathList, temporarilyPath);
            responseData.setSuccess(true);
            responseData.setMessage("下载成功");

            //更新打印状态
            for( HlsCusFctQuotationCashflow dt: fctQuotationCashflows){
                if("FCT_CONTRACT".equals(dt.getDocumentCategory())) {
                    HlsCusFctQuotationCashflow fctCashflowUpdate = new HlsCusFctQuotationCashflow();
                    fctCashflowUpdate.setQuotationCashflowId(dt.getQuotationCashflowId());
                    fctCashflowUpdate.setPrintStatus("Y");
                    mapper.updateByPrimaryKeySelective(fctCashflowUpdate);
                }
                if("CON_CONTRACT".equals(dt.getDocumentCategory())) {
                    HlsCusConContractCashflow conCashflowUpdate = new HlsCusConContractCashflow();
                    conCashflowUpdate.setCashflowId(dt.getQuotationCashflowId());
                    conCashflowUpdate.setPrintStatus("Y");
                    conContractCashflowMapper.updateByPrimaryKeySelective(conCashflowUpdate);
                }
            }

       // } else {
       //     responseData.setSuccess(false);
       //     responseData.setMessage("下载失败");
       // }
        return responseData;
    }

    //生成并下载
    private List<String> createFile(List<HlsCusFctQuotationCashflow> cashflowList, IRequest requestContext,
                                    String templetCode, String temporarilyPath, HttpServletRequest request, HttpServletResponse response) {
        List<String> pathList = new ArrayList<>();

        //获取模板
        Long templetId = 408L;
        String templetType = "PLM_NOTICE_PRINT";
        HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
        hlsDocFileTemplet.setTempletType(templetType);
        hlsDocFileTemplet.setTempletCode(templetCode);
        List<HlsDocFileTemplet> hlsDocFileTempletList = hlsDocFileTempletMapper.select(hlsDocFileTemplet);
        //获取成功则继续生成，失败这返回消息
        if (hlsDocFileTempletList.size() > 0) {
            hlsDocFileTemplet = hlsDocFileTempletList.get(0);
            templetId = hlsDocFileTemplet.getTempletId();
        } else {
            return pathList;
        }


        for(int i=0;i<cashflowList.size();i++){
            StringBuilder stringBuilder=new StringBuilder();
            String fileName = stringBuilder.append(i+1).append("、").append(cashflowList.get(i).getBpName()).append("第")
                    .append(Math.abs(cashflowList.get(i).getTimes())).append("期")
                    .append(hlsDocFileTemplet.getTempletName()).append(".docx").toString();
            Map<String,Object> map = new HashMap<>();

            map.put("templetId",templetId);
            map.put("fileName",fileName);
            map.put("temporarilyPath",temporarilyPath);
            map.put("contractType",cashflowList.get(i).getDocumentCategory());
            map.put("cashflowId",cashflowList.get(i).getQuotationCashflowId());
            map.put("contractId",cashflowList.get(i).getContractId());
            map.put("times",cashflowList.get(i).getTimes());

            String path = docxService.processNotice(requestContext,map);
            if (StringUtils.isNotBlank(path)){
                pathList.add(fileName);
            }
        }
        return pathList;
    }

    //下载通知书
    private void downloadFile(IRequest iRequest, HttpServletRequest request, HttpServletResponse response,
                              List<String> pathList, String categoryPath) {
        List<HlsCusSysFile> hlsCusSysFiles = new ArrayList<>();
        for (String path : pathList) {
            HlsCusSysFile file = new HlsCusSysFile();
            file.setFilePath(categoryPath + "/" + path);
            file.setFileName(path);
            hlsCusSysFiles.add(file);
        }

        if (hlsCusSysFiles.size() > 0) {

            String userName = iRequest.getUserName();//获取用户名
            File zipFilePath = new File(categoryPath);
            if (!zipFilePath.exists()) {
                zipFilePath.mkdirs();
            }
            //拼接文件名,用户名+系统时间,避免出现重复
            String zipFile = zipFilePath + File.separator + userName + "-" + System.currentTimeMillis() + ".zip";
            try {
                FileOutputStream outStream = new FileOutputStream(zipFile);
                ZipOutputStream toClient = new ZipOutputStream(outStream);
                HlsCusZipUtil.zipFile(hlsCusSysFiles, toClient);//打包转换为zip文件
                toClient.close();//关闭流
                outStream.close();//关闭流
                HlsCusZipUtil.downloadZip(new File(zipFile), response);//下载zip文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AttachmentException e) {
                e.printStackTrace();
            }

        }
    }
}
