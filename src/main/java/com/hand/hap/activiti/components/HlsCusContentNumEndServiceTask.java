package com.hand.hap.activiti.components;


import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.dto.ContentNumberLine;
import com.hand.hls.cont.mapper.ContentNumberLineMapper;
import com.hand.hls.cont.service.IContentNumberHeadService;
import com.hand.hls.cont.service.IContentNumberLineService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.service.HlsCreditLineService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusContentNumEndServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private IContentNumberHeadService iContentNumberHeadService;
    @Autowired
    private ContentNumberLineMapper contentNumberLineMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private IContentNumberLineService service;

    private Logger logger = LoggerFactory.getLogger(HlsCusContentNumEndServiceTask.class);

    public HlsCusContentNumEndServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();

        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");

        String headString = (String) delegateExecution.getVariable("contentNumberHead");
        Long headId = (Long) delegateExecution.getVariable("headId");
        ContentNumberHead contentNumberHead = JSON.parseObject(headString, ContentNumberHead.class);

        contentNumberHead = iContentNumberHeadService.selectByPrimaryKey(requestCtx,contentNumberHead);

        //根据审批结果修改状态
        if ("APPROVED".equalsIgnoreCase(result) || result == null) {

            contentNumberHead.setStatus("APPROVED");
            ContentNumberLine contentNumberLine = new ContentNumberLine();
            contentNumberLine.setHeadId(headId);
            List<ContentNumberLine> list = contentNumberLineMapper.selectContentLineList(contentNumberLine);

            service.createDocumentNumber(requestCtx,list);

        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {

            contentNumberHead.setStatus("APPROVED_RETURN");

        } else if ("REJECTED".equalsIgnoreCase(result)) {

            contentNumberHead.setStatus("REJECTED");
        }
        iContentNumberHeadService.updateByPrimaryKeySelective(requestCtx,contentNumberHead);

    }
}
