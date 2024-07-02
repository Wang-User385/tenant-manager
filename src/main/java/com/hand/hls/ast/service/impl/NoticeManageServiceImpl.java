package com.hand.hls.ast.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.attachment.mapper.AttachCategoryMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ast.mapper.NoticeManageMapper;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsDocFileTempletMapper;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusPrjContractDocxService;
import com.hand.hls.plm.nm.dto.PlmNoticeSent;
import com.hand.hls.plm.nm.service.PlmNoticeSentService;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.ast.service.INoticeManageService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class NoticeManageServiceImpl extends BaseServiceImpl<NoticeManage> implements INoticeManageService {

    @Autowired
    private NoticeManageMapper mapper;
    @Autowired
    private INoticeManageService service;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    private PlmNoticeSentService plmNoticeSentService;
    @Autowired
    private HlsDocFileTempletMapper hlsDocFileTempletMapper;
    @Autowired
    private HlsCusPrjContractDocxService docxService;
    @Autowired
    private AttachCategoryMapper attachCategoryMapper;

    private static final String ACTUAL_PAY = "ACTUAL_PAY";
    private static final String PAY = "PAY";
    private static final String ADJUST = "ADJUST";

    @Override
    public List<NoticeManage> cashflowInfo(NoticeManage noticeManage, IRequest requestContext, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.queryAll(noticeManage);
    }

    public List<NoticeManage> queryContractCashflowLov(IRequest requestCtx, NoticeManage noticeManage, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.mapper.queryContractCashflowLov(noticeManage);
    }

    public List<NoticeManage> queryNoticeTempLov(IRequest requestCtx, NoticeManage noticeManage, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.mapper.queryNoticeTempLov(noticeManage);
    }

    public List<NoticeManage> queryContractCashflowItemLov(IRequest requestCtx, NoticeManage noticeManage, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.mapper.queryContractCashflowItemLov(noticeManage);
    }

    @Override
    public ResponseData downloadNoticePrintFile(List<NoticeManage> noticeManages, IRequest requestContext, HttpServletRequest request, HttpServletResponse response) {
        ResponseData responseData = new ResponseData();

        //解析前台传入的数据
        if (!CollectionUtils.isNotEmpty(noticeManages)) {
            throw new RuntimeException("参数不能为空!");
        }

        List<NoticeManage> noticeListPay = new ArrayList<>();
        List<NoticeManage> noticeListActualPay = new ArrayList<>();
        List<NoticeManage> noticeListAdjust = new ArrayList<>();

        for (NoticeManage noticeManage : noticeManages) {

            if (PAY.equalsIgnoreCase(noticeManage.getNoticeType())) {
                noticeListPay.add(noticeManage);
            }
            if (ACTUAL_PAY.equalsIgnoreCase(noticeManage.getNoticeType())) {
                noticeListActualPay.add(noticeManage);
            }
            if (ADJUST.equalsIgnoreCase(noticeManage.getNoticeType())) {
                noticeListAdjust.add(noticeManage);
            }
        }
        if (noticeListPay.isEmpty() && noticeListActualPay.isEmpty() && noticeListAdjust.isEmpty()) {
            responseData.setSuccess(false);
            responseData.setMessage("下载失败！通知书类型不正确！");
        }


        //获取文件暂存目录
        String temporarilyType = "PLM_NOTICE_PRINT";
        String temporarilyPath = "E:\\upload";
        AttachCategory attachCategoryTemp = new AttachCategory();
        attachCategoryTemp.setSourceType(temporarilyType);
        List<AttachCategory> attachCategoryList = attachCategoryMapper.select(attachCategoryTemp);
        if (attachCategoryList.size() > 0) {
            attachCategoryTemp = attachCategoryList.get(0);
            temporarilyPath = attachCategoryTemp.getCategoryPath();
        }

        List<HlsCusSysFile> pathList = new ArrayList<>();
        //租金支付通知书
        if (noticeListPay.size() > 0) {
            List<HlsCusSysFile> responseDataPay = createFile(noticeListPay, requestContext, temporarilyPath, request, response);
            if (responseDataPay.size() > 0) {
                pathList.addAll(responseDataPay);
            }
        }

        //实际支付表
        if (noticeListActualPay.size() > 0) {
            List<HlsCusSysFile> responseDataAccount = createFile(noticeListActualPay, requestContext, temporarilyPath, request, response);
            if (responseDataAccount.size() > 0) {
                pathList.addAll(responseDataAccount);
            }
        }

        //调整通知书
        if (noticeListAdjust.size() > 0) {
            List<HlsCusSysFile> responseDataDefault = createFile(noticeListAdjust, requestContext, temporarilyPath, request, response);
            if (responseDataDefault.size() > 0) {
                pathList.addAll(responseDataDefault);
            }
        }

        if (pathList.size() > 0) {
            downloadFile(requestContext, request, response, pathList, temporarilyPath);
            responseData.setSuccess(true);
            responseData.setMessage("下载成功");
        } else {
            responseData.setSuccess(false);
            responseData.setMessage("下载失败！文件生成失败");
        }
        return responseData;
    }

    //生成并下载
    private List<HlsCusSysFile> createFile(List<NoticeManage> noticeManageList, IRequest requestContext, String temporarilyPath, HttpServletRequest request, HttpServletResponse response) {
        List<HlsCusSysFile> pathList = new ArrayList<>();
//        Map<String,Object> pathMap = new HashMap<>(2);

        for (int i = 0; i < noticeManageList.size(); i++) {
            HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
            hlsDocFileTemplet.setTempletId(noticeManageList.get(i).getTempletId());
            hlsDocFileTemplet = hlsDocFileTempletMapper.selectByPrimaryKey(hlsDocFileTemplet);

            StringBuilder stringBuilder = new StringBuilder();
            String fileName;
//            fileName = stringBuilder.append(noticeManageList.get(i).getCashflowId()).append(hlsDocFileTemplet.getTempletName()).append(".docx").toString();

            int b = hlsDocFileTemplet.getTempletCode().lastIndexOf("_");
            String a = (hlsDocFileTemplet.getTempletCode().substring(0, b));
            if ("PLM_NOTICE_PRINT_ACTUAL_PAY".equals(a)) {
                fileName = stringBuilder.append(i + 1).append("、").append(noticeManageList.get(i).getContractNumber())
                        .append(hlsDocFileTemplet.getTempletName()).append(".docx").toString();

            } else {
                fileName = stringBuilder.append(i + 1).append("、").append(noticeManageList.get(i).getContractNumber()).append("第")
                        .append(Math.abs(noticeManageList.get(i).getTimes())).append("期")
                        .append(hlsDocFileTemplet.getTempletName()).append(".docx").toString();
            }


            Map<String, Object> map = new HashMap<>();
            map.put("templetId", noticeManageList.get(i).getTempletId());
            map.put("fileName", fileName);
            map.put("temporarilyPath", temporarilyPath);
            map.put("cashflowId", noticeManageList.get(i).getCashflowId());
            map.put("contractId", noticeManageList.get(i).getContractId());
            map.put("noticeManageId", noticeManageList.get(i).getNoticeManageId());
            map.put("times", noticeManageList.get(i).getTimes());

            String path = docxService.processNotice(requestContext, map);
            if (StringUtils.isNotBlank(path)) {
                HlsCusSysFile file = new HlsCusSysFile();
                file.setFileName(fileName);
                file.setFilePath(path);
                pathList.add(file);
            }
        }
        return pathList;
    }

    //下载通知书
    private void downloadFile(IRequest iRequest, HttpServletRequest request, HttpServletResponse response,
                              List<HlsCusSysFile> pathList, String categoryPath) {
        List<HlsCusSysFile> hlsCusSysFiles = new ArrayList<>(pathList);

        HttpHeaders headers = new HttpHeaders();
        byte[] buffer = new byte[3000];
        ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
        if (hlsCusSysFiles.size() > 0) {
            //获取用户名
            String userName = iRequest.getUserName();
            File zipFilePath = new File(categoryPath);
            if (!zipFilePath.exists()) {
                zipFilePath.mkdirs();
            }
            //拼接文件名,用户名+系统时间,避免出现重复
            String zipFile = userName + "-" + System.currentTimeMillis() + ".zip";
            try {
                FileOutputStream outStream = new FileOutputStream(zipFile);
                ZipOutputStream toClient = new ZipOutputStream(outStream);
                //打包转换为zip文件
                HlsCusZipUtil.zipFile(hlsCusSysFiles, toClient);
                toClient.close();//关闭流
                outStream.close();//关闭流
                File file = new File(zipFile);
//                BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
//                buffer = new byte[fis.available()];
//
//                byteArrayResource = new ByteArrayResource(buffer);
//                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//                headers.setContentDispositionFormData("attachment", new String(zipFile.getBytes(), StandardCharsets.ISO_8859_1));
//                headers.setContentLength(buffer.length);
                HlsCusZipUtil.downloadZip(file, response);//下载zip文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AttachmentException e) {
                e.printStackTrace();
            }
        }
//        return  ResponseEntity.ok().headers(headers).body(byteArrayResource);
    }

}