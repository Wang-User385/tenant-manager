package com.hand.hls.cont.service.impl;

import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.HlsCusFndAttachmentService;
import com.hand.hls.cont.dto.HlsCusContractTariffInfo;
import com.hand.hls.cont.mapper.HlsCusCommonAttachmentMapper;
import com.hand.hls.cont.service.IHlsCusContractTariffInfoService;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import hls.core.sys.mapper.SysCodeValueMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.HlsCusCommonAttachment;
import com.hand.hls.cont.service.IHlsCusCommonAttachmentService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCommonAttachmentServiceImpl extends BaseServiceImpl<HlsCusCommonAttachment> implements IHlsCusCommonAttachmentService{

    @Autowired
    private HlsCusCommonAttachmentMapper hlsCusCommonAttachmentMapper;
    @Autowired
    private HlsCusFndAttachmentService fndAttachmentService;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private FndAtmAttachmentMapper fndAtmAttachmentMapper;
    @Autowired
    private IHlsCusContractTariffInfoService hlsCusContractTariffInfoService;
    @Autowired
    private SysCodeValueMapper sysCodeValueMapper;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public String contractContextPackage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, IRequest iRequest, HlsCusCommonAttachment attachmentPara) {
        Map map = new HashMap();
        map.put("tariffId", attachmentPara.getSourceId());
        List<HlsCusCommonAttachment> list = hlsCusCommonAttachmentMapper.queryContractTariffAttachmentInfo(map);
        List<Long> attachments = new ArrayList<>();
        list.forEach(attachment->{
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("jczl_common_attachment");
            fndAttachmentMulti.setTablePkValue(attachment.getAttachmentId().toString());
            List<FndAttachmentMulti> selectdFndAttachmentMultis = this.fndAttachmentMultiMapper.select(fndAttachmentMulti);

            List<Long> collect = selectdFndAttachmentMultis.stream().filter(item -> item.getAttachmentId() != null).map(m -> m.getAttachmentId()).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(collect)){
                attachments.addAll(collect);
            }

        });
        Long[] attachmentIds = new Long[attachments.size()];
        for (int i = 0; i < attachments.size(); i++) {
            attachmentIds[i] = attachments.get(i);
        }
        //压缩文件
        fndAttachmentService.attachmentPackageDownload(httpServletRequest, httpServletResponse, iRequest, attachmentIds, "tariff_files");
        return "tariff_files.zip";
    }

    @Override
    public void tariffContractContextInit(IRequest iRequest,HlsCusCommonAttachment attachmentPara){
        //先删除原有的资料清单
        attachmentPara.setSourceCategory("con_contract_tariff_info");
        List<HlsCusCommonAttachment> list = this.selectSelective(iRequest,attachmentPara);
        for(HlsCusCommonAttachment attachment:list){
            //删除系统附件表
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("jczl_common_attachment");
            fndAttachmentMulti.setTablePkValue(String.valueOf(attachment.getAttachmentId()));

            List<FndAttachmentMulti> multiList = fndAttachmentMultiMapper.select(fndAttachmentMulti);

            for(FndAttachmentMulti multi:multiList){
                FndAtmAttachmentDto fndAtmAttachmentDto = new FndAtmAttachmentDto();
                fndAtmAttachmentDto.setAttachmentId(multi.getAttachmentId());
                fndAtmAttachmentMapper.delete(fndAtmAttachmentDto);
                fndAttachmentMultiMapper.deleteByPrimaryKey(multi);
            }

            this.deleteByPrimaryKey(attachment);
        }

        HlsCusContractTariffInfo hlsCusContractTariffInfo = new HlsCusContractTariffInfo();
        hlsCusContractTariffInfo.setTariffId(attachmentPara.getSourceId());
        hlsCusContractTariffInfo = hlsCusContractTariffInfoService.selectByPrimaryKey(iRequest,hlsCusContractTariffInfo);
        //首次关税资料清单
        List<Map> tariffList = new ArrayList<>();
        if(hlsCusContractTariffInfo.getTimes() == null || hlsCusContractTariffInfo.getTimes() == 0){
            tariffList = sysCodeValueMapper.queryCodeDetails("FIRST_TARIFF_LIST");
        }else{
            //非首次关税资料清单
            tariffList= sysCodeValueMapper.queryCodeDetails("NOT_FIRST_TARIFF_LIST");
        }
        tariffList.forEach(item -> {
            HlsCusCommonAttachment newCommonAttachment = new HlsCusCommonAttachment();
            newCommonAttachment.setSourceId(attachmentPara.getSourceId());
            newCommonAttachment.setSourceCategory("con_contract_tariff_info");
            newCommonAttachment.setDocumentName(item.get("meaning").toString());
            this.insertSelective(iRequest,newCommonAttachment);
        });
    }


    @Override
    public String cshContractContextPackage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, IRequest iRequest, HlsCusCommonAttachment attachmentPara) {
        Map map = new HashMap();
        map.put("paymentReqId", attachmentPara.getSourceId());
        List<HlsCusCommonAttachment> list = hlsCusCommonAttachmentMapper.queryCshPaymentAttachmentInfo(map);
        List<Long> attachments = new ArrayList<>();
        list.forEach(attachment->{
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("csh_payment_attachment");
            fndAttachmentMulti.setTablePkValue(attachment.getAttachmentId().toString());
            List<FndAttachmentMulti> selectdFndAttachmentMultis = this.fndAttachmentMultiMapper.select(fndAttachmentMulti);

            List<Long> collect = selectdFndAttachmentMultis.stream().filter(item -> item.getAttachmentId() != null).map(m -> m.getAttachmentId()).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(collect)){
                attachments.addAll(collect);
            }

        });
        Long[] attachmentIds = new Long[attachments.size()];
        for (int i = 0; i < attachments.size(); i++) {
            attachmentIds[i] = attachments.get(i);
        }
        //压缩文件
        fndAttachmentService.attachmentPackageDownload(httpServletRequest, httpServletResponse, iRequest, attachmentIds,list.get(0).getFileName());
        StringBuilder builder = new StringBuilder();
        return builder.append(list.get(0).getFileName()).append(".zip").toString();
    }


}