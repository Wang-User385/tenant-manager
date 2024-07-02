package com.hand.hls.common.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.dto.HapInterfaceLine;
import com.hand.hap.intergration.service.IHapInterfaceLineService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import com.hand.hls.eas.dto.HlsCusEasBasicData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;
import com.hand.hls.eas.mapper.HlsCusEasSourceRecordMapper;
import com.hand.hls.eas.mapper.HlsCusPsotEasTmpMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.common.dto.HlsCusDocumentRecordList;
import com.hand.hls.common.service.HlsCusDocumentRecordListService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusDocumentRecordListServiceImpl extends BaseServiceImpl<HlsCusDocumentRecordList> implements HlsCusDocumentRecordListService{

    @Autowired
    private HlsCusHapInterfaceOutboundService hapInterfaceOutboundService;

    @Autowired
    private IHapInterfaceLineService hapInterfaceLineService;


    @Autowired
    private HlsCusBpMasterRequestRecordsService hlsCusBpMasterRequestRecordsService;

    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    @Autowired
    private HlsCusEasSourceRecordMapper hlsCusEasSourceRecordMapper;

    @Autowired
    private HlsCusPsotEasTmpMapper hlsCusPsotEasTmpMapper;



    //接口发送失败后统一重发
    @Override
    public HlsCusHapInterfaceOutbound wsInterfaceUnifiedResend(IRequest iRequest, HlsCusHapInterfaceOutbound dto) {

        dto=hapInterfaceOutboundService.selectByPrimaryKey(iRequest,dto);

        HapInterfaceLine hapInterfaceLine= new HapInterfaceLine();
        hapInterfaceLine.setLineId(dto.getLineId());
        hapInterfaceLine=hapInterfaceLineService.selectByPrimaryKey(iRequest,hapInterfaceLine);

        //ECIF接口  单笔查询接口
        if("ECIF_SINGLE_QUERY".equals(hapInterfaceLine.getLineCode())){
            HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords= new HlsCusBpMasterRequestRecords();
            hlsCusBpMasterRequestRecords.setOutboundId(dto.getOutboundId());
            hlsCusBpMasterRequestRecordsService.wsEcifSignalQuery(iRequest,hlsCusBpMasterRequestRecords);
        }
        //批量创建接口
        else if("ECIF_CREATE_UPDATE_LIST".equals(hapInterfaceLine.getLineCode())){
            HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords= new HlsCusBpMasterRequestRecords();
            hlsCusBpMasterRequestRecords.setOutboundId(dto.getOutboundId());
            hlsCusBpMasterRequestRecordsService.wsEcifBatchCreateUpdate(iRequest,hlsCusBpMasterRequestRecords);
        }
        //金蝶基础资料同步接口
        else if("EAS_BASIC_SYN".equals(hapInterfaceLine.getLineCode())){
            HlsCusEasSourceRecord hlsCusEasSourceRecord= new HlsCusEasSourceRecord();
            hlsCusEasSourceRecord.setOutboundId(dto.getOutboundId());
            hlsCusEasSourceRecord=  hlsCusEasSourceRecordMapper.selectEasTypeCommon(hlsCusEasSourceRecord);

            if(hlsCusEasSourceRecord!=null){
                HlsCusEasBasicData hlsCusEasBasicData= new HlsCusEasBasicData();

                hlsCusEasBasicData.setTypeNumber(hlsCusEasSourceRecord.getTypeNumber());
                hlsCusEasBasicData.setNumber(hlsCusEasSourceRecord.getSourceNumber());
                hlsCusEasBasicData.setName(hlsCusEasSourceRecord.getSourceName());
                hlsCusEasBasicData.setDescription(hlsCusEasSourceRecord.getDescription());

                Long sourceId=hlsCusEasSourceRecord.getSourceId();
                String sourceTable=hlsCusEasSourceRecord.getSourceTable();

                //登录接口
                HlsCusEasLogin hlsCusEasLogin = new HlsCusEasLogin();
                hlsCusEasLogin = hlsCusEasLoginService.easDoLogin(iRequest, hlsCusEasLogin);

                hlsCusEasLoginService.easBasicSyn(iRequest,hlsCusEasBasicData,sourceTable,sourceId);
            }
        }
        //银行账户同步接口
        else if("EAS_BANK_ACCOUNT_SYN".equals(hapInterfaceLine.getLineCode())){
            hlsCusEasLoginService.easBankAccountSyn(iRequest,dto.getOutboundId());
        }
        //科目对账同步接口
        else if("EAS_CHECK_ACCOUNT_SYN".equals(hapInterfaceLine.getLineCode())){
            hlsCusEasLoginService.easCheckAccountSyn(iRequest,dto.getOutboundId());
        }
        //凭证同步接口
        else if("EAS_CREDENTIALS_SYN".equals(hapInterfaceLine.getLineCode())){
            hlsCusEasLoginService.easCredentialsSynchronization(iRequest,dto.getOutboundId());
        }
        //凭证删除接口
        else if("EAS_CREDENTIALS_DELETE".equals(hapInterfaceLine.getLineCode())){
            HlsCusPsotEasTmp hlsCusPsotEasTmp= new HlsCusPsotEasTmp();
            hlsCusPsotEasTmp.setOutboundId(dto.getOutboundId());
            List<HlsCusPsotEasTmp> hlsCusPsotEasTmpList= hlsCusPsotEasTmpMapper.selectCredentialsLogData(hlsCusPsotEasTmp);

           if(hlsCusPsotEasTmpList.size()>0){
               String voucherId=hlsCusPsotEasTmpList.get(0).getVoucherId();
               String comOrgNum=hlsCusPsotEasTmpList.get(0).getComOrgNum();
               Long sourceId=hlsCusPsotEasTmpList.get(0).getSourceId();
               hlsCusEasLoginService.easCredentialsDelete(iRequest,voucherId,comOrgNum,sourceId);
           }
        }
        return dto;
    }

}