package com.hand.hls.eas.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.eas.mapper.HlsCusPsotEasTmpMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.gld.dto.JeHead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;
import com.hand.hls.eas.service.IHlsCusPsotEasTmpService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPsotEasTmpServiceImpl extends BaseServiceImpl<HlsCusPsotEasTmp> implements IHlsCusPsotEasTmpService{


    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;


    @Autowired
    private HlsCusPsotEasTmpMapper hlsCusPsotEasTmpMapper;




    //金蝶基础资料同步接口
    @Override
    public HlsCusPsotEasTmp gldPost(IRequest iRequest, HlsCusPsotEasTmp dto) {

        //删除当前sessionId临时表数据
        hlsCusPsotEasTmpMapper.deleteCurrentSessionIdTmpData(dto);

      for(JeHead item: dto.getJeHeadList()){
          HlsCusPsotEasTmp hlsCusPsotEasTmp= new HlsCusPsotEasTmp();
          hlsCusPsotEasTmp.setSessionId(dto.getSessionId());
          hlsCusPsotEasTmp.setSourceId(item.getJeHeadId());
          hlsCusPsotEasTmp.setSourceTable("GLD_JE_HEAD");
          this.insert(iRequest,hlsCusPsotEasTmp);
      }

        Long outboundId=null;
        hlsCusEasLoginService.easCredentialsSynchronization(iRequest,outboundId);


        //执行完后删除当前sessionId临时表数据
        hlsCusPsotEasTmpMapper.deleteCurrentSessionIdTmpData(dto);

      return dto;
    }


    //金蝶基础资料同步接口
    @Override
    public HlsCusPsotEasTmp gldPostDelete(IRequest iRequest, HlsCusPsotEasTmp dto) {

        hlsCusEasLoginService.easCredentialsDelete(iRequest,dto.getVoucherId(),dto.getComOrgNum(),dto.getSourceId());
        return dto;
    }
}